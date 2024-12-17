package helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import src.c195classproject.Appointment;
import src.c195classproject.Customers;

import java.time.LocalDateTime;
import java.util.Optional;



/**
 * Handles input validation and error messaging
 */
public class InputValidation {

    /**
     *     lets the methods know if french is currently selected
     */
    static private boolean french = false;

    /**
     * Setter for the french boolean value
     * @param setTo
     */
    static public void setFrench(boolean setTo){ french = setTo;}
    /**
     * Checks if a string exceeds 50 chars or is empty
     * @param stringToTest The string to be tested
     * @return true if over or empty
     */
    static public boolean StringOver50orEmpty(String stringToTest)
        {
            if (stringToTest.length() > 50 || stringToTest.isEmpty())
            {
                if (french){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Erreur de chaîne");
                    a.setHeaderText("Le texte que vous avez saisi est trop long ou vous avez laissé vide une entrée de texte obligatoire.");
                    a.show();
                }
                else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("String Error");
                    a.setHeaderText("The text you have input is too long or you have left a required text input empty.");
                    a.show();

                }
                return true;
            }
            return false;
        };

    /**
     * Checks if start date is less than
     * @param start start date
     * @param end   end date
     * @return  returns true or false
     */
    static public boolean validDateXLessThanY (LocalDateTime start, LocalDateTime end){

        if (start.isAfter(end))
        {

            Alert a = new Alert(Alert.AlertType.ERROR);
            if (french){
                a.setTitle("Erreur avec la date de début et la date de fin.");
                a.setHeaderText("La date de début est postérieure à la date de fin.");
                a.show();
            }
            else {
                a.setTitle("Error with start date and end date.");
                a.setHeaderText("The start date is after the end date.");
                a.show();
            }
            return false;

        }
        return true;
    };
    /**
     * Display error saying to please select an option from the combo box
     */
    static public void comboBoxIsEmptyError(){

        Alert a = new Alert(Alert.AlertType.ERROR);
        if (french){
            a.setTitle("Erreur ComboBox");
            a.setHeaderText("Veuillez sélectionner une option dans la liste.");
            a.show();
        }
        else {
            a.setTitle("ComboBox Error");
            a.setHeaderText("Please select an option from the list.");
            a.show();
        }
    };

    /**
     * Display an error asking to please select an item from the table
     */
    static public void noTableSelectedError(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        if (french){
            a.setTitle("Serreur d'élection");
            a.setHeaderText("Veuillez sélectionner une option dans le tableau.");
            a.show();
        }
        else {
            a.setTitle("Selection Error");
            a.setHeaderText("Please select an option from the table.");
            a.show();
        }
    }

    /**
     * Display and error stating that a start or end time is outside of work hours
     */
    static public void outOfWorkHoursError(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        if (french){
            a.setTitle("Erreur de temps");
            a.setHeaderText("Veuillez choisir une heure comprise dans les heures de travail.");
            a.show();
        }
        else {
            a.setTitle("Time Error");
            a.setHeaderText("Please select a time within work hours.");
            a.show();
        }
    }

    /**
     * Displays an error stating that an appointment already exists
     */
    static public void appointmentAlreadyExists(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        if (french){
            a.setTitle("Erreur de réservation");
            a.setHeaderText("Ce créneau horaire est déjà pris par un autre rendez-vous.");
            a.show();
        }
        else {
            a.setTitle("Booking Error");
            a.setHeaderText("This time slot is already taken by another appointment.");
            a.show();
        }
    }

    /**
     * Displays a message stating that an appointment is within 15 minutes along with details of said appointment
     * @param app Appointment details are loaded from
     */
    static public void appointmentWithin15MinError(Appointment app)
    {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        if (french){
            a.setTitle("Rendez-vous dans les 15 minutes");
            a.setHeaderText(
                    "ID de rendez-vous: " + app.getAppIDCol()+"\n"+
                            "Title: "+ app.getTitleCol()+"\n"+
                            "Description: "+ app.getDescriptionCol()+"\n"+
                            "Localisation: "+ app.getLocationCol()+"\n"+
                            "Type: "+ app.getTypeCol()+"\n"+
                            "Démarrage: "+ app.getStartCol()+"\n"+
                            "Fin: "+ app.getEndCol());
            a.show();
        }
        else {
            a.setTitle("Appointment Within 15 Minutes");
            a.setHeaderText(
                    "Appointment ID: " + app.getAppIDCol()+"\n"+
                            "Title: "+ app.getTitleCol()+"\n"+
                            "Description: "+ app.getDescriptionCol()+"\n"+
                            "Location: "+ app.getLocationCol()+"\n"+
                            "Type: "+ app.getTypeCol()+"\n"+
                            "Start: "+ app.getStartCol()+"\n"+
                            "End: "+ app.getEndCol());
            a.show();
        }
    }

    /**
     * Displays a message stating that no appointment is starting withing 15 minutes
     */
    static public void noAppWithin15Minutes(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        if (french){
            a.setTitle("Pas de rendez-vous");
            a.setHeaderText("Pas de rendez-vous dans les 15 minutes.");
            a.show();
        }
        else {
            a.setTitle("No Appointments");
            a.setHeaderText("No Appointments are within 15 minutes.");
            a.show();
        }
    }

    /**
     * Displays an error stating that the start date and end date do not fall on the same day
     */
    static public void startEndOnSameDayError(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        if (french){
            a.setTitle("Début Fin Erreur");
            a.setHeaderText("Le jour de début et le jour de fin ne sont pas le même jour.");
            a.show();
        }
        else {
            a.setTitle("Start End Error");
            a.setHeaderText("The start day and end day are not on the same day.");
            a.show();
        }
    }

    /**
     * Displays a confirmation box confirming the deletion of an appointment with a few details included
     * @param app Appointment details are pulled from
     * @return true or false value
     */
    static public boolean appDelete(Appointment app){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        if (french){
            alert.setTitle("Confirmation");
            alert.setHeaderText("Êtes-vous sûr de vouloir le supprimer?\n"+
                    "ID du rendez-vous: " + app.getAppIDCol() +
                    "\nType: " + app.getTypeCol());
            alert.setContentText("C'est irréversible.");
        }
        else {
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure you want to delete this?\n"+
                    "Appointment ID: " + app.getAppIDCol() +
                    "\nType: " + app.getTypeCol());
            alert.setContentText("This is irreversible.");
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    /**
     * Displays a confirmation box confirming the deletion of a Customer with a few details included
     * @param customer Customer details are pulled from
     * @return true or false
     */
    static public boolean cusDelete(Customers customer){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        if (french){
            alert.setTitle("Confirmation");
            alert.setHeaderText("Êtes-vous sûr de vouloir le supprimer?\n"+
                    "Identifiant du client: " + customer.getCustomerID() +
                    "\nNom du client: " + customer.getCustomerName());
            alert.setContentText("C'est irréversible.");
        }
        else {
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure you want to delete this?\n"+
                    "Customer ID: " + customer.getCustomerID() +
                    "\nCustomer Name: " + customer.getCustomerName());
            alert.setContentText("This is irreversible.");
        }

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    /**
     * Displays an error saying that an incorrect combination of username and password has been used
     */
    static public void wrongPasswordLogin(){
        Alert a = new Alert(Alert.AlertType.ERROR);

        if (french){
            a.setTitle("Non autorisé");
            a.setHeaderText("Mauvaise combinaison de mot de passe et de nom d'utilisateur.");
            a.show();
        }
        else {
            a.setTitle("Unauthorized");
            a.setHeaderText("Wrong Password and Username Combination");
            a.show();
        }

    }

}


