package src.c195classproject;

import helper.InputValidation;
import helper.JDBC;
import helper.TimeZoneHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller class for the update app form
 */
public class UpdateAppController implements Initializable {
    //FXML items
    @FXML
    DatePicker startDatePicker, endDatePicker;
    @FXML
    private ComboBox<Contact> contactDropDown;
    @FXML
    private ComboBox<Customer> customerDropDown;
    @FXML
    private ComboBox<User> userDropDown;
    @FXML
    private TextField IDTextBox,descriptionTextBox,locationTextBox,titleTextBox,typeTextBox;
    @FXML
    private Spinner<Integer> startDateHour,startDateMinute,endDateMinute,endDateHour;

    @Override
    public void initialize(URL locationOf, ResourceBundle resources){

        //update and refresh the ComboBoxes
        try {
            ContactsCustomersUsersComboBox.updateContacts();
            ContactsCustomersUsersComboBox.updateCustomers();
            ContactsCustomersUsersComboBox.updateUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        //set spinner min max values
        int hourMax = 23;
        int minuteMax = 59;
        int min = 0;
        startDateHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min,hourMax));
        endDateHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min,hourMax));

        startDateMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min,minuteMax));
        endDateMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(min,minuteMax));

        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());

    //Autofill all fields
        String temp;
        int index = AppAndCust.getSelectedIndex();
        Appointment app= AppAndCust.getAppointments().get(index);

        LocalDateTime startDate = app.getStartCol().toLocalDateTime();
        LocalDateTime endDate = app.getEndCol().toLocalDateTime();
        startDate = (LocalDateTime) TimeZoneHelper.utcToLocal(startDate);
        endDate = (LocalDateTime) TimeZoneHelper.utcToLocal(endDate);


        temp = Integer.toString(app.getAppIDCol());
        IDTextBox.setText(temp);
        titleTextBox.setText(app.getTitleCol());
        descriptionTextBox.setText(app.getDescriptionCol());
        locationTextBox.setText(app.getLocationCol());
        typeTextBox.setText(app.getTypeCol());
        startDatePicker.setValue(LocalDate.from(startDate));
        endDatePicker.setValue(LocalDate.from(endDate));
        startDateHour.getValueFactory().setValue(app.getStartCol().getHours());
        startDateMinute.getValueFactory().setValue(app.getStartCol().getMinutes());
        endDateHour.getValueFactory().setValue(app.getEndCol().getHours());
        endDateMinute.getValueFactory().setValue(app.getEndCol().getMinutes());


        contactDropDown.setItems(ContactsCustomersUsersComboBox.getContacts());
        customerDropDown.setItems(ContactsCustomersUsersComboBox.getCustomers());
        userDropDown.setItems(ContactsCustomersUsersComboBox.getUsers());

        //not the best way but fine for this application (linear search). could use JDBC.
        //used to preset the values for their respective comboBoxes
        for (int i = 0; i < ContactsCustomersUsersComboBox.getContacts().size(); i++) {
            if (ContactsCustomersUsersComboBox.getContacts().get(i).getContact_ID() == app.getContactIDCol()){
                contactDropDown.getSelectionModel().select(ContactsCustomersUsersComboBox.getContacts().get(i));
                break;}
        }
        for (int i = 0; i < ContactsCustomersUsersComboBox.getCustomers().size(); i++) {
            if (ContactsCustomersUsersComboBox.getCustomers().get(i).getCustomerID() == app.getCustomerIDCol()){
                customerDropDown.getSelectionModel().select(ContactsCustomersUsersComboBox.getCustomers().get(i));
                break;}
        }
        for (int i = 0; i < ContactsCustomersUsersComboBox.getUsers().size(); i++) {
            if (ContactsCustomersUsersComboBox.getUsers().get(i).getUserID() == app.getUserIDCol()){
                userDropDown.getSelectionModel().select(ContactsCustomersUsersComboBox.getUsers().get(i));
                break;}
        }
    }

    /**
     * Save all input and update the database. Function also performs input validation
     * @throws SQLException
     * @throws IOException
     */
    public void saveClick() throws SQLException, IOException {


        //ERROR CHECKING AND HANDLING
        //check if strings are empty or over length 50. this sql database uses varchar(50)
        if (InputValidation.StringOver50orEmpty(titleTextBox.getText())) return;
        if (InputValidation.StringOver50orEmpty(descriptionTextBox.getText())) return;
        if (InputValidation.StringOver50orEmpty(locationTextBox.getText())) return;
        if (InputValidation.StringOver50orEmpty(typeTextBox.getText())) return;


        //check if combo box is selected
        if (contactDropDown.getSelectionModel().isEmpty()){
            InputValidation.comboBoxIsEmptyError();
            return;};
        if (customerDropDown.getSelectionModel().isEmpty()){
            InputValidation.comboBoxIsEmptyError();
            return;};
        if (userDropDown.getSelectionModel().isEmpty()){
            InputValidation.comboBoxIsEmptyError();
            return;};

        //retrieve selected Contact ID from name list
        int contactID = contactDropDown.getSelectionModel().getSelectedItem().getContact_ID();
        int userID = userDropDown.getSelectionModel().getSelectedItem().getUserID();
        int customerID = customerDropDown.getSelectionModel().getSelectedItem().getCustomerID();


        String startDate, endDate;
        LocalDateTime startDateLocal, endDateLocal;
        LocalDate localDate = startDatePicker.getValue();
        startDate = localDate + " " + startDateHour.getValue() +":"+ startDateMinute.getValue()+ ":00";
        localDate = endDatePicker.getValue();
        endDate = localDate + " " + endDateHour.getValue() +":"+ endDateMinute.getValue()+ ":00";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-M-dd H:m:s");
        startDateLocal = LocalDateTime.parse(startDate,dtf);
        endDateLocal = LocalDateTime.parse(endDate,dtf);


        //check if start date is before end date;
        if (!InputValidation.validDateXLessThanY(startDateLocal,endDateLocal))
            return;

        //check if start and end date fall on the same day
        if (!TimeZoneHelper.startEndOnSameDay(TimeZoneHelper.localtoET(startDateLocal),TimeZoneHelper.localtoET(endDateLocal)))
            return;

        //check if hours fall within work hours
        if (!TimeZoneHelper.isOnWorkHours(startDateLocal)) return;
        if (!TimeZoneHelper.isOnWorkHours(endDateLocal)) return;

        int index = AppAndCust.getSelectedIndex();
        Appointment app= AppAndCust.getAppointments().get(index);

        //check if appointment collides with another
        if (!TimeZoneHelper.validApppointmentExcludeID(startDateLocal, app.getAppIDCol())) return;
        if (!TimeZoneHelper.validApppointmentExcludeID(endDateLocal, app.getAppIDCol())) return;


        startDateLocal = (LocalDateTime) TimeZoneHelper.localtoUTC(startDateLocal);
        endDateLocal = (LocalDateTime) TimeZoneHelper.localtoUTC(endDateLocal);
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = (LocalDateTime) TimeZoneHelper.localtoUTC(localDateTime);

        String startDateStr = startDateLocal.format(dtf);
        String endDateStr = endDateLocal.format(dtf);
        String localDateStr = localDateTime.format(dtf);




        String query = "UPDATE appointments "+
                "SET Title='"+titleTextBox.getText()+"',"+
                "Description='"+descriptionTextBox.getText()+"',"+
                "Location='"+locationTextBox.getText()+"',"+
                "Type='"+typeTextBox.getText()+"',"+
                "Start='"+startDateStr+"',"+
                "End='"+endDateStr+"',"+
                "Last_Update='"+localDateStr+"',"+
                "Last_Updated_By='"+LoginPage.getCurrentUserID()+"',"+
                "Customer_ID='"+customerID+"',"+
                "User_ID='"+userID+"',"+
                "Contact_ID='"+contactID+"'"+
                "WHERE appointment_ID = " + app.getAppIDCol();

        Statement stmt = JDBC.connection.createStatement();
        stmt.executeUpdate(query);
        loadMainPage();
    }

    /**
     * Loads the main page when the cancel button is clicked
     * @throws IOException
     */
    public void cancelClick() throws IOException {
        loadMainPage();
    }

    /**
     * Loads the main page
     * @throws IOException
     */
    private void loadMainPage() throws IOException {
        Locale currentLocale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"),bundle);
        Stage window = (Stage) IDTextBox.getScene().getWindow();
        window.setScene(new Scene(root));
    }
}
