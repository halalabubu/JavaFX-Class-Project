package src.c195classproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * class that holds the data used by the report controller
 */
public class ReportModel {

    /**appointments associated with a contact*/
    private static final ObservableList<Appointment> appointmentsByContact = FXCollections.observableArrayList();

    /**appointmentsByContact getter*/
    public static ObservableList<Appointment> getApps (){return appointmentsByContact;};

}
