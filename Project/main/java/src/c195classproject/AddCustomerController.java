package src.c195classproject;

import helper.InputValidation;
import helper.JDBC;
import helper.TimeZoneHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * controller class for the add customer form
 */
public class AddCustomerController implements Initializable {

    //FXML items
    @FXML
    TextField customerIDTextBox,customerNameTextBox,phoneNumberTextBox,addressTextBox,postalTextBox;
    @FXML
    ComboBox <Country> countryComboBox;
    @FXML
    ComboBox <Division> divisionComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            String query;
            query = "SELECT Max(Customer_ID) FROM client_schedule.customers";
            Statement stmt = JDBC.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            //rs starts at null
            rs.next();
            //get current highest Customer_ID and increment by 1 for a new unique ID
            int ID = rs.getInt("MAX(Customer_ID)") + 1;
            customerIDTextBox.setText(Integer.toString(ID));

            //update Country and Divisions
            CountryAndDivisions.update();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        //fill country comboBB boxes
        countryComboBox.setItems(CountryAndDivisions.getCountries());
        divisionComboBox.setVisibleRowCount(10);

    }

    /**
     * Attempts to insert the input to the database when the save button is clicked. Gives an error if there is a problem
     * Input is validated before it is inserted into the database
     * @throws SQLException
     * @throws IOException
     */
        public void saveClick() throws SQLException, IOException {

        //Input Validation
        if (InputValidation.StringOver50orEmpty(customerIDTextBox.getText())) return;
        if (InputValidation.StringOver50orEmpty(phoneNumberTextBox.getText())) return;
        if (InputValidation.StringOver50orEmpty(addressTextBox.getText())) return;
        if (InputValidation.StringOver50orEmpty(postalTextBox.getText())) return;

        //checks if an item is selected from the combo box
        if (countryComboBox.getSelectionModel().isEmpty()){
            InputValidation.comboBoxIsEmptyError();
            return;};
        if (divisionComboBox.getSelectionModel().isEmpty()){
            InputValidation.comboBoxIsEmptyError();
            return;};

        LocalDateTime localdatetime = LocalDateTime.now();
        localdatetime = TimeZoneHelper.localtoUTC(localdatetime);


            String query = "INSERT INTO Customers Values (" +
                    " " + customerIDTextBox.getText() +
                    ", '" + customerNameTextBox.getText() +
                    "', '" + addressTextBox.getText()+
                    "', '" + postalTextBox.getText()+
                    "', '" + phoneNumberTextBox.getText()+
                    "', '" + localdatetime.format(TimeZoneHelper.dtf)+  //creation date
                    "', " + LoginPage.getCurrentUserID()+              //Created By
                    ", '" + localdatetime.format(TimeZoneHelper.dtf) + //Last Update
                    "', " + LoginPage.getCurrentUserID() +             //Last update by
                    ", " + divisionComboBox.getSelectionModel().getSelectedItem().getDivisionID()+
                    ")";

            Statement stmt = JDBC.connection.createStatement();
            stmt.executeUpdate(query);

            //all done now load mainPage
            loadMainPage();

    }

    /**
     * Fires when the cancel button is clicked. Loads the main page
     * @throws IOException
     */
        public void cancelClick() throws IOException {
        loadMainPage();
    }

    /**
     * When the country comboBox is selected, update the division box to reflect this change
     */
    public void countrySelected() {

        if (countryComboBox.getSelectionModel().getSelectedItem() == null)
            return;
        divisionComboBox.getItems().clear();
        for (int i = 0; i < CountryAndDivisions.getDivisions().size(); i++) {

            if (CountryAndDivisions.getDivisions().get(i).getCountryID()
                    == countryComboBox.getSelectionModel().getSelectedItem().getCountryID()) {

                divisionComboBox.getItems().add(CountryAndDivisions.getDivisions().get(i));
            }
        }
    }

    /**
     * Fires when an item is selected from the division combo box
     */
    public void divisionSelected() {
    }

    /**
     * Loads the main form
     * @throws IOException
     */
    public  void loadMainPage() throws IOException {
        Locale currentLocale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
        Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"),bundle);
        Stage window = (Stage) customerNameTextBox.getScene().getWindow();
        window.setScene(new Scene(root));
    }
}
