package src.c195classproject;

import helper.InputValidation;
import helper.JDBC;
import helper.TimeZoneHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * Controller class for the login page controller
 * Lambda expression on line 111. Used on line 65
 */
public class LoginPageController implements Initializable {

    //FXML items
    @FXML
    private Button loginButton;
    @FXML
    private Label locationText;
    @FXML
    private TextField usernameInput,passwordInput;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        ZoneId zone = ZoneId.systemDefault();
        locationText.setText(zone.toString());

    }

    /**
     * Fires when the login button is clicked
     * @throws Exception
     */
    public void loginButtonClick() throws Exception{

        String user = usernameInput.getText();
        String password = passwordInput.getText();


        final String query = "SELECT * FROM users WHERE User_Name = '" + user + "'";
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        //prints login attempt to a file with username used and a timestamp
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(logActivity.getFileName()),true));
        if (rs.next()) {
            if (rs.getString("Password").equals(password)) {
                //System.out.println("Success");

                String tmp = "Text in user field: " +usernameInput.getText()+"        Time: "
                        + LocalDateTime.now().atZone(ZoneId.of("UTC"))+" Successful\n";
                pw.append(tmp);
                pw.close();


                //retrieve user id based on the user being logged in
                LoginPage.setCurrentUserID(Integer.toString(rs.getInt("User_ID")));
                Locale currentLocale = Locale.getDefault();
                ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);
                Parent root = FXMLLoader.load(getClass().getResource("MainPage.fxml"),bundle);
                Stage window = (Stage) loginButton.getScene().getWindow();
                window.setScene(new Scene(root));



                //Check if an appointment start time is within 15 minutes of LocalDateTime.now()
                TimeZoneHelper.checkApp15Min();
                return;
            }
        }

        InputValidation.wrongPasswordLogin();

        String tmp = "Text in user field: " +usernameInput.getText()+"        Time: " +
                LocalDateTime.now().atZone(ZoneId.of("UTC"))+" Not Successful\n";
        pw.append(tmp);
        pw.close();

    }

    /**
     * Interface to retrieve the file name
     */
    interface LogActivity {
        String getFileName();
    }

    /**
     * Lambda Expression that returns the files name
     */
    LogActivity logActivity = () -> "login_activity.txt";

}