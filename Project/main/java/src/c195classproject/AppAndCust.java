package src.c195classproject;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * class used to manage the arrays used to set the viewTables for both customers and appointments
 */
public class AppAndCust {

    /**arrays that hold the appointments used to set the viewTables*/
    private static final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    /**arrays that hold the customers used to set the viewTables*/
    private static final ObservableList<Customers> customers = FXCollections.observableArrayList();

    /**used to save the currently selected objects item, so it can easily be accessed in the modify form*/
    private static int selectedIndex;//appointments index
    /**used to save the currently selected customers objects item, so it can easily be accessed in the modify form*/
    private static Customers selectedCustomer;

    //getters and setters
    public static Customers getSelectedCustomer() {return selectedCustomer;}
    public static void setSelectedCustomer(Customers selectedCustomer)
    {AppAndCust.selectedCustomer = selectedCustomer;}

    public static int getSelectedIndex() {
        return selectedIndex;
    }
    public static void setSelectedIndex(int selectedIndex) {
        AppAndCust.selectedIndex = selectedIndex;
    }

    public static ObservableList<Appointment> getAppointments() {return appointments;}
    public static void addAppointment(Appointment newAppointment){appointments.add(newAppointment);}

    public static ObservableList<Customers> getCustomers() {return customers;}
    public static void addCustomer(Customers newCustomer) {AppAndCust.customers.add(newCustomer);}

    /**
     * queries the database and adds the results to the appointments array
     * @throws Exception
     */
    public static void queryAppointments() throws Exception{

        final String query = "SELECT * FROM appointments";


            Statement stmt = JDBC.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next())
            {
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

                AppAndCust.addAppointment(appointment);
            }
    }

    /**
     * Queries the database with whatever query it is given
     * @param query String used as a query
     * @throws Exception
     */
    public static void queryAppointments(String query) throws Exception{


        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next())
        {
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

            AppAndCust.addAppointment(appointment);
        }
    }

    /**
     * queries the database and adds the results to the customers array
     * @throws Exception
     */
    public static void queryCustomers() throws Exception{
        final String query = "SELECT * FROM customers";

        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next())
        {
            Customers customer = new Customers();
            customer.setCustomerID(rs.getInt("Customer_ID"));
            customer.setCustomerName(rs.getString("Customer_Name"));
            customer.setAddress(rs.getString("Address"));
            customer.setPostalCode(rs.getString("Postal_Code"));

            customer.setPhone(rs.getString("Phone"));
            customer.setCreateDate(rs.getTimestamp("Create_Date"));
            customer.setCreatedBy(rs.getString("Created_By"));
            customer.setLastUpdate(rs.getTimestamp("Last_update"));

            customer.setLastUpdatedBy(rs.getString("Last_Updated_By"));
            customer.setDivisionID(rs.getInt("Division_ID"));

            AppAndCust.addCustomer(customer);
        }
    }
}
