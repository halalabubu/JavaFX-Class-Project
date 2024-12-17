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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * controller class for the add appointment form
 */
public class AddAppController implements Initializable {

    //FXML UI items
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

        //find a new App ID to autofill the IDTextBox
        String query;
        query = "SELECT Max(Appointment_ID) FROM client_schedule.appointments";
        try {
            Statement stmt = JDBC.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            //rs starts at null
            rs.next();
            //get current highest APPID and increment by 1 for a new unique ID
            int ID = rs.getInt("MAX(Appointment_ID)") + 1;
            IDTextBox.setText(Integer.toString(ID));

            //Fill the contacts
            ContactsCustomersUsersComboBox.updateContacts();
            ContactsCustomersUsersComboBox.updateCustomers();
            ContactsCustomersUsersComboBox.updateUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //set items
        contactDropDown.setItems(ContactsCustomersUsersComboBox.getContacts());
        customerDropDown.setItems(ContactsCustomersUsersComboBox.getCustomers());
        userDropDown.setItems(ContactsCustomersUsersComboBox.getUsers());

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

    }

    /**
     * Save all input and insert into the database. Function also performs input validation
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

        if (!TimeZoneHelper.validAppointmentTime(startDateLocal)) return;
        if (!TimeZoneHelper.validAppointmentTime(endDateLocal)) return;


        startDateLocal =  TimeZoneHelper.localtoUTC(startDateLocal);
        endDateLocal =  TimeZoneHelper.localtoUTC(endDateLocal);
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime =  TimeZoneHelper.localtoUTC(localDateTime);

        String startDateStr = startDateLocal.format(dtf);
        String endDateStr = endDateLocal.format(dtf);
        String localDateStr = localDateTime.format(dtf);

        //form sql query
        String query = "INSERT INTO appointments Values (" +
                " " + IDTextBox.getText() +
                ", '" + titleTextBox.getText() +
                "', '" + descriptionTextBox.getText()+
                "', '" + locationTextBox.getText()+
                "', '" + typeTextBox.getText()+
                "', '" + startDateStr+
                "', '" + endDateStr+
                "', '" + localDateStr +
                "', " + LoginPage.getCurrentUserID() +
                ", '" + localDateStr+       //since its created and last updated are the same
                "', " + LoginPage.getCurrentUserID() +          //same case as above but with user
                ", " + customerID+
                ", " + userID+
                ", " + contactID +
                ")";

        Statement stmt = JDBC.connection.createStatement();
        stmt.executeUpdate(query);

        //if successful will now load the main page
        loadMainPage();
    }

    /**
     * Fires if the cancel click is clicked
     * @throws IOException
     */
    public void cancelClick() throws IOException {
        loadMainPage();
    }

    /**
     * Loads the main page
     * @throws IOException
     */
    public  void loadMainPage() throws IOException {
        Locale currentLocale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"),bundle);
        Stage window = (Stage) IDTextBox.getScene().getWindow();
        window.setScene(new Scene(root));
    }

}
