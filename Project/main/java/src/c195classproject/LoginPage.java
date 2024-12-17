package src.c195classproject;

import helper.InputValidation;
import helper.JDBC;
import helper.TimeZoneHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * main class that starts and loads the login page
 */
public class LoginPage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //get current locale
        String language = Locale.getDefault().getLanguage();
        if (language.equals("fr"))
            InputValidation.setFrench(true);

        Locale currentLocale = Locale.getDefault();

        System.out.println(currentLocale);
        //set resource bundle       currentLocale
        ResourceBundle bundle = ResourceBundle.getBundle("labelText",currentLocale);

        //load login page
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPage.class.getResource("LoginPage.fxml"),bundle);
        Scene LoginFormScene = new Scene(fxmlLoader.load());
        stage.setTitle("Software 2 C195");
        stage.setScene(LoginFormScene);
        stage.show();
    }

    /**
     * Creates the jdbc connection, launches fxml, and then closes the jdbc connection
     * @param args
     */
    public static void main(String[] args) {

        //creates the connection to the database
        JDBC.openConnection();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowUTC = LocalDateTime.now(ZoneId.of("UTC"));
        //LocalDateTime nowUTC = TimeZoneHelper.localtoUTC(now);

        System.out.println("CZ:         " + now);
        System.out.println("CZ to UTC:  " + TimeZoneHelper.localtoUTC(now));
        System.out.println("UTC:        " + nowUTC);
        System.out.println("UTC to CZ:  " + TimeZoneHelper.utcToLocal(nowUTC));


        //launch fxml
        launch();

        // after the program ends, the connection is closed to the database
        JDBC.closeConnection();
    }

    /**the user id of the user currently logged in*/
    static private String currentUserID;
    public static String getCurrentUserID() {return currentUserID;}
    public static void setCurrentUserID(String currentUserID)
    {LoginPage.currentUserID = currentUserID;}

}