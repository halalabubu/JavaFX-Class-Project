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
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * controller class for the modify customer form
 */
public class ModifyCustomerController implements Initializable {

    //FXML items
    @FXML
    TextField customerIDTextBox,customerNameTextBox,phoneNumberTextBox,addressTextBox,postalTextBox;
    @FXML
    ComboBox<Country> countryComboBox;
    @FXML
    ComboBox <Division> divisionComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        customerIDTextBox.setText(Integer.toString(AppAndCust.getSelectedCustomer().getCustomerID()));
        customerNameTextBox.setText(AppAndCust.getSelectedCustomer().getCustomerName());
        phoneNumberTextBox.setText(AppAndCust.getSelectedCustomer().getPhone());
        addressTextBox.setText(AppAndCust.getSelectedCustomer().getAddress());
        postalTextBox.setText(AppAndCust.getSelectedCustomer().getPostalCode());


        //get country and division from division code
        try {
            int divID = AppAndCust.getSelectedCustomer().getDivisionID();
            int countryID;
            Division division = CountryAndDivisions.getDivfromID(divID);
            countryID = division.getCountryID();
            Country country = CountryAndDivisions.getCountryFromID(countryID);

            //fill country combo boxes
            CountryAndDivisions.update();
            countryComboBox.setItems(CountryAndDivisions.getCountries());
            countryComboBox.getSelectionModel().select(country);
            countrySelected();

            divisionComboBox.getSelectionModel().select(division);
            divisionComboBox.setVisibleRowCount(10);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attempts to update the database with the input when the save button is clicked. Gives an error if there is a problem
     * Input is validated before it is the database is updated
     * @throws SQLException
     * @throws IOException
     */
    public void saveClick() throws SQLException, IOException {

        //Input Validation
        if (InputValidation.StringOver50orEmpty(customerIDTextBox.getText())) return;
        if (InputValidation.StringOver50orEmpty(phoneNumberTextBox.getText())) return;
        if (InputValidation.StringOver50orEmpty(addressTextBox.getText())) return;
        if (InputValidation.StringOver50orEmpty(postalTextBox.getText())) return;

        LocalDateTime localdatetime = LocalDateTime.now();
        localdatetime = TimeZoneHelper.localtoUTC(localdatetime);

        String query = "UPDATE Customers SET " +
                "Customer_Name= '"+customerNameTextBox.getText()+"',"+
                "Address= '"+addressTextBox.getText()+"',"+
                "Postal_Code= '"+ postalTextBox.getText() + "',"+
                "Phone= '" + phoneNumberTextBox.getText() + "',"+
                "Last_Update='" + localdatetime.format(TimeZoneHelper.dtf)+"',"+
                "Last_Updated_By= '" + LoginPage.getCurrentUserID() +"',"+
                "Division_ID= '" + divisionComboBox.getSelectionModel().getSelectedItem().getDivisionID()+"'"+
                "WHERE  Customer_ID = "+ customerIDTextBox.getText();

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
