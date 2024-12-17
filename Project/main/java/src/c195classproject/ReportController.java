package src.c195classproject;

import helper.JDBC;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller class for the report form
 */
public class ReportController implements Initializable {

    //FXML items
    @FXML
    private TableView<Appointment> appTable;
    @FXML
    private TableColumn<Appointment, Integer> appID,customerID;
    @FXML
    private TableColumn<Appointment,String> title,description,location,type;
    @FXML
    private TableColumn<Appointment, Timestamp> startDate,endDate;

    @FXML
    private RadioButton appByCTM,viewSchedule;
    @FXML
    private ComboBox <Contact> contactsComboBox;
    @FXML
    private TextArea textArea1,textArea2,textArea3;
    @FXML
    private Text countPerCountry,countPerMonth,countPerType;
    @FXML
    Button backButton;


    /**
     * Lambda on line 74
     * Lambda expression to greatly simplify the code. When the back button is clicked, it loads the main page
     */
    @Override
    public void initialize(URL loc, ResourceBundle resources) {
        //fill contacts combo box
        try {
            ContactsCustomersUsersComboBox.updateContacts();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        contactsComboBox.setItems(ContactsCustomersUsersComboBox.getContacts());

        appID.setCellValueFactory(new PropertyValueFactory<>("appIDCol"));
        customerID.setCellValueFactory(new PropertyValueFactory<>("customerIDCol"));
        title.setCellValueFactory(new PropertyValueFactory<>("titleCol"));
        description.setCellValueFactory(new PropertyValueFactory<>("descriptionCol"));
        location.setCellValueFactory(new PropertyValueFactory<>("locationCol"));
        type.setCellValueFactory(new PropertyValueFactory<>("typeCol"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startCol"));
        endDate.setCellValueFactory(new PropertyValueFactory<>("endCol"));

        //Lambda expression to greatly simplify the code. When the back button is clicked, it loads the main page
        backButton.setOnAction(event -> {
            try {
                backClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }});


        //loads the results to the three text fields
        try {
            //count appointments by type
            String query = "SELECT type, Count(*) as appCount " +
                    "FROM client_schedule.appointments " +
                    "GROUP BY type";
            String tmp = "";
            Statement stmt = JDBC.connection.createStatement();
            ResultSet rs;
            rs = stmt.executeQuery(query);

            while (rs.next())
            {
                tmp = tmp + rs.getString("type") + ": " + rs.getInt("appCount") + "\n";

            }
            textArea2.setText(tmp);

            //Count appointments by country
            query = "SELECT Country,COUNT(*) as count\n" +
                    "FROM client_schedule.appointments\n" +
                    "INNER JOIN client_schedule.customers as o ON appointments.Customer_ID = o.customer_ID\n" +
                    "INNER JOIN client_schedule.first_level_divisions as l ON l.Division_ID = o.division_ID\n" +
                    "INNER JOIN client_schedule.countries as c ON c.Country_ID = l.Country_ID\n" +
                    "GROUP BY Country";
            rs = stmt.executeQuery(query);

            tmp="";
            while (rs.next()){
                tmp = tmp + rs.getString("Country")+": "+ rs.getInt("count")+ "\n";

            }
            textArea1.setText(tmp);

            //count appointments by month
            final int MINYR = 12;
            tmp = "";
            for (int i = 0; i < MINYR; i++) {
                query = "SELECT Count(*) as count FROM client_schedule.appointments WHERE month(Start) = " + (i+1);//month doesn't start at 0 jan is 1
                rs = stmt.executeQuery(query);
                rs.next();//will never be null, just returns 0
                tmp = tmp + (i+1) +": "+ rs.getInt("count") + "\n";
            }
            textArea3.setText(tmp);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * When a contact is selected, loads that contacts assigned appointments
     * @throws SQLException
     */
    public void contactsComboBoxSelected() throws SQLException {

        //for some reason this runs when a new page is loaded but the combo box value becomes null
        //the error can be ignored but this avoids it
        if (contactsComboBox.getSelectionModel().getSelectedItem() == null)
            return;

    String query = "SELECT * FROM appointments WHERE Contact_ID = "+
            contactsComboBox.getSelectionModel().getSelectedItem().getContact_ID();
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs;
        rs = stmt.executeQuery(query);

        //clear the array
        ReportModel.getApps().clear();

        while (rs.next()) {
            Appointment appointment = new Appointment();
            appointment.setAppIDCol(rs.getInt("Appointment_ID"));
            appointment.setUserIDCol(rs.getInt("User_ID"));
            appointment.setCustomerIDCol(rs.getInt("Customer_ID"));
            appointment.setContactIDCol(rs.getInt("Contact_ID"));

            appointment.setTitleCol(rs.getString("Title"));
            appointment.setDescriptionCol(rs.getString("Description"));
            appointment.setLocationCol(rs.getString("Location"));
            appointment.setTypeCol(rs.getString("Type"));

            appointment.setStartCol(rs.getTimestamp("Start"));
            appointment.setEndCol(rs.getTimestamp("End"));

            ReportModel.getApps().add(appointment);
        }

        appTable.setItems(ReportModel.getApps());


    }

    /**
     * when radio box is selected makes associated ui visible
     */
    public void appByCTMActive() {
        appTable.setOpacity(0);
        appTable.setDisable(true);
        textArea1.setOpacity(100);     textArea2.setOpacity(100);    textArea3.setOpacity(100);
        textArea1.setDisable(false);   textArea2.setDisable(false);  textArea3.setDisable(false);
        countPerCountry.setOpacity(100);
        countPerMonth.setOpacity(100);
        countPerType.setOpacity(100);

    }

    /**
     * when radio box is selected makes associated ui visible
     */
    public void viewScheduleActive() {
        appTable.setOpacity(100);
        appTable.setDisable(false);
        textArea1.setOpacity(0);   textArea2.setOpacity(0);   textArea3.setOpacity(0);
        textArea1.setDisable(true);textArea2.setDisable(true);textArea3.setDisable(true);
        countPerCountry.setOpacity(0);
        countPerMonth.setOpacity(0);
        countPerType.setOpacity(0);

    }

    /**
     * When back is clicked it loads the main page
     * @throws IOException
     */
    public void backClick() throws IOException {

        Locale currentLocale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"),bundle);
        Stage window = (Stage) appTable.getScene().getWindow();
        window.setScene(new Scene(root));

    }

}
















