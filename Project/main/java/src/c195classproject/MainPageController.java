package src.c195classproject;

import helper.InputValidation;
import helper.JDBC;
import helper.TimeZoneHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Controller class for the main page form
 */
public class MainPageController implements Initializable{

    //FXML ITEMS
    @FXML
    private Button reports, add, update, delete, logout;
    @FXML
    private RadioButton viewAll,viewApp, viewByMonth, viewByWeek;

    //Calendar
    @FXML
    public DatePicker calendar;

    //Table for appointments
    @FXML
    private TableView<Appointment> table;
    @FXML
    private TableColumn<Appointment, Integer> appID,customerID,userID,contact;
    @FXML
    private TableColumn<Appointment,String> title,description,location,type;
    @FXML
    private TableColumn<Appointment, Timestamp> startDate,endDate;

    //Table for Customers
    @FXML
    private TableView<Customers> tableCustomer;
    @FXML
    private TableColumn<Customers,Integer> col1,col10;
    @FXML
    private TableColumn<Customers,String> col2,col3,col4,col5,col7,col9;
    @FXML
    private TableColumn<Customers,Timestamp> col6,col8;

    /**
     *         Lambda on line 98
     *         lambda expression that makes the application quit on the click of this button.
     *         Instead of having to make a function I can write it all in one line
     */
    @Override
    public void initialize(URL locationOf, ResourceBundle resources){

        //Appointment TableView columns
        appID.setCellValueFactory(new PropertyValueFactory<>("appIDCol"));
        customerID.setCellValueFactory(new PropertyValueFactory<>("customerIDCol"));
        userID.setCellValueFactory(new PropertyValueFactory<>("userIDCol"));
        contact.setCellValueFactory(new PropertyValueFactory<>("contactIDCol"));
        title.setCellValueFactory(new PropertyValueFactory<>("titleCol"));
        description.setCellValueFactory(new PropertyValueFactory<>("descriptionCol"));
        location.setCellValueFactory(new PropertyValueFactory<>("locationCol"));
        type.setCellValueFactory(new PropertyValueFactory<>("typeCol"));
        startDate.setCellValueFactory(new PropertyValueFactory<>("startCol"));
        endDate.setCellValueFactory(new PropertyValueFactory<>("endCol"));

        //Customer TableView columns
        col1.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        col2.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        col3.setCellValueFactory(new PropertyValueFactory<>("address"));
        col4.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        col5.setCellValueFactory(new PropertyValueFactory<>("phone"));
        col6.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        col7.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        col8.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        col9.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        col10.setCellValueFactory(new PropertyValueFactory<>("divisionID"));

        //set calendar to today
        calendar.setValue(LocalDate.now());

        //Lambda Expression
        logout.setOnAction(event -> Platform.exit());



        //queries the server and using the results fills the fx arrays
        try {

            AppAndCust.getAppointments().clear();
            AppAndCust.getCustomers().clear();
            AppAndCust.queryAppointments();
            AppAndCust.queryCustomers();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        table.setItems(AppAndCust.getAppointments());
        tableCustomer.setItems(AppAndCust.getCustomers());

        //change table times to localtime
        convertUTCToLocal();


        //enable the Appointments table (sets opacity and enables)
        viewAppActive();

    }

    /**Fires when Appointments Radio Box is ticked*/
    public void viewAppActive() {
        tableCustomer.setDisable(true);
        tableCustomer.setOpacity(0);
        table.setDisable(false);
        table.setOpacity(100);
        viewByWeek.setDisable(false);
        viewByMonth.setDisable(false);
        calendar.setDisable(false);
        viewAll.setDisable(false);

    }
    /**Fires when Customer Radio Box is ticked*/
    public void viewCustomerActive() {
        tableCustomer.setDisable(false);
        tableCustomer.setOpacity(100);
        table.setDisable(true);
        table.setOpacity(0);
        viewByWeek.setDisable(true);
        viewByMonth.setDisable(true);
        calendar.setDisable(true);
        viewAll.setDisable(true);
    }

    /**
     * Fires when the add button is clicked, loads addApp or addCus depending on radio box selection
     * @throws IOException
     */
    public void addClick() throws IOException {

        if (viewApp.isSelected())
        {
            Locale currentLocale = Locale.getDefault();
            ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
            Parent root = FXMLLoader.load(getClass().getResource("AddAppointment.fxml"),bundle);
            Stage window = (Stage) update.getScene().getWindow();
            window.setScene(new Scene(root));
        }
        else
        {
            Locale currentLocale = Locale.getDefault();
            ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
            Parent root = FXMLLoader.load(getClass().getResource("AddCustomer.fxml"),bundle);
            Stage window = (Stage) update.getScene().getWindow();
            window.setScene(new Scene(root));
        }
    }

    /**
     * Fires when the update button is clicked, loads modifyApp or modifyCus depending on radio box selection
     * @throws IOException
     */
    public void updateClick() throws IOException {

        if (viewApp.isSelected())
        {
            if (table.getSelectionModel().isEmpty())
            {InputValidation.noTableSelectedError();
                return;}

            AppAndCust.setSelectedIndex(table.getSelectionModel().getSelectedIndex());

            Locale currentLocale = Locale.getDefault();
            ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
            Parent root = FXMLLoader.load(getClass().getResource("ModifyAppointment.fxml"),bundle);
            Stage window = (Stage) update.getScene().getWindow();
            window.setScene(new Scene(root));
        }
        else {

            if (tableCustomer.getSelectionModel().isEmpty()){
                InputValidation.noTableSelectedError();
                return;
            }

            AppAndCust.setSelectedCustomer(tableCustomer.getSelectionModel().getSelectedItem());

            Locale currentLocale = Locale.getDefault();
            ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
            Parent root = FXMLLoader.load(getClass().getResource("ModifyCustomer.fxml"),bundle);
            Stage window = (Stage) update.getScene().getWindow();
            window.setScene(new Scene(root));
        }
    }

    /**
     * Fires when the delete button is clicked. Deletes the currently selected item from the tableView based on radio button selection
     * @throws Exception
     */
    public void deleteClick() throws Exception {

        //check if appointment or customer
        if (viewApp.isSelected())
        {
            if (table.getSelectionModel().isEmpty())
                {InputValidation.noTableSelectedError();
                return;}
            if (!InputValidation.appDelete(table.getSelectionModel().getSelectedItem()))
                return;

            int index = table.getSelectionModel().getSelectedItem().getAppIDCol();
            String query ="DELETE FROM appointments WHERE Appointment_ID = " + index;
            Statement stmt = JDBC.connection.createStatement();
            stmt.executeUpdate(query);
            AppAndCust.getAppointments().remove(table.getSelectionModel().getSelectedItem());

        }
        else //customer is selected
        {
            if (tableCustomer.getSelectionModel().isEmpty())
            {InputValidation.noTableSelectedError();
                return;}
            if (!InputValidation.cusDelete(tableCustomer.getSelectionModel().getSelectedItem()))
                return;

            int customerID = tableCustomer.getSelectionModel().getSelectedItem().getCustomerID();
            Statement stmt = JDBC.connection.createStatement();

            //delete all customer appointments
            String query ="DELETE FROM appointments WHERE Customer_ID = " + customerID;
            stmt.executeUpdate(query);
            //Then delete the customer
            query ="DELETE FROM customers WHERE Customer_ID = " + customerID;
            stmt.executeUpdate(query);

            //refresh app and customer table, other option is to return deleted items and remove from table
            AppAndCust.getAppointments().clear();
            AppAndCust.getCustomers().clear();
            AppAndCust.queryAppointments();
            AppAndCust.queryCustomers();

            table.setItems(AppAndCust.getAppointments());
            tableCustomer.setItems(AppAndCust.getCustomers());
        }
    }

    /**
     * Fires when the report button is clicked, loads the report form
     * @throws IOException
     */
    public void reportClick() throws IOException {
        Locale currentLocale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
        Parent root = FXMLLoader.load(getClass().getResource("ReportPage.fxml"),bundle);
        Stage window = (Stage) update.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    /**
     * Loads all appointments
     * @throws Exception
     */
    public void viewAll() throws Exception {
        AppAndCust.getAppointments().clear();
        AppAndCust.queryAppointments();
        convertUTCToLocal();
    }

    /**
     * View appointments scheduled the month set by the calendar
     * @throws Exception
     */
    public void viewByMonth() throws Exception {

        LocalDate date = calendar.getValue();
        int month = date.getMonthValue();
        final String query =
                "SELECT * " +
                "FROM client_schedule.appointments " +
                "WHERE month(Start) = " + month;
        AppAndCust.getAppointments().clear();
        AppAndCust.queryAppointments(query);
        convertUTCToLocal();
    }

    /**
     * View appointments scheduled the week set by the calendar
     * @throws Exception
     */
    public void viewByWeek() throws Exception {
        LocalDate date = calendar.getValue();
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weekNumber = date.get(woy) - 1; //sql starts at 0?
        final String query =
                "SELECT * " +
                        "FROM client_schedule.appointments " +
                        "WHERE week(Start) = " + weekNumber;
        AppAndCust.getAppointments().clear();
        AppAndCust.queryAppointments(query);
        convertUTCToLocal();
    }

    /**
     * Converts stored appointments from UTC to local
     */
    private void convertUTCToLocal (){

        ZonedDateTime s;

        for (int i = 0; i < AppAndCust.getAppointments().size(); i++) {
            LocalDateTime start,end;

            start = AppAndCust.getAppointments().get(i).getStartCol().toLocalDateTime();
            end = AppAndCust.getAppointments().get(i).getEndCol().toLocalDateTime();

            s = start.atZone(ZoneId.systemDefault());
            start = s.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();


            AppAndCust.getAppointments().get(i).setStartCol(Timestamp.valueOf(start));
            AppAndCust.getAppointments().get(i).setEndCol(Timestamp.valueOf(end));

        }
    }




    /**
     * When a date is selected updates the list based on the currently selected radio box
     * @throws Exception
     */
    public void calendarAction() throws Exception {

        if (viewAll.isSelected())
            viewAll();
        else if (viewByMonth.isSelected())
            viewByMonth();
        else
            viewByWeek();

    }
}














