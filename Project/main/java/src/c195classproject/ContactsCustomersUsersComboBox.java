package src.c195classproject;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class used to manage the lists that control the combo boxes. These boxes are used in both add and modify forms for both customers and appointments.
 */
public class ContactsCustomersUsersComboBox {

    /**arrays used to store the contacts displayed in the combo boxes*/
    private static final ObservableList<Contact> contacts = FXCollections.observableArrayList();
    /**arrays used to store the customers displayed in the combo boxes*/
    private static final ObservableList<Customer> customers = FXCollections.observableArrayList();
    /**arrays used to store the users displayed in the combo boxes*/
    private static final ObservableList<User> users = FXCollections.observableArrayList();

    //returns the Contacts list
    public static ObservableList<Contact> getContacts() {return contacts;}
    //returns the Customers list
    public static ObservableList<Customer> getCustomers() {return customers;}
    //returns the Users list
    public static ObservableList<User> getUsers() {return users;}

    /**
     * updates the Contacts list
     * @throws SQLException
     */
    public static void updateContacts() throws SQLException {

        String query = "SELECT * FROM client_schedule.contacts";
        //System.out.println(query);
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        ContactsCustomersUsersComboBox.getContacts().clear();
        while (rs.next())
        {
            Contact contact = new Contact();
            contact.setContact_ID(rs.getInt("Contact_ID"));
            contact.setContactName(rs.getString("Contact_Name"));
            contact.setEmail(rs.getString("Email"));

            contacts.add(contact);
        }
    }

    /**
     * updates the Customers list
     * @throws SQLException
     */
    public  static void updateCustomers() throws SQLException {

        String query = "SELECT * FROM client_schedule.customers";
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        ContactsCustomersUsersComboBox.getCustomers().clear();
        while (rs.next())
        {
            Customer customer = new Customer();
            customer.setCustomerID(rs.getInt("Customer_ID"));
            customer.setCustomerName(rs.getString("Customer_Name"));

            customers.add(customer);
        }
    }

    /**
     * updates the Users list
     * @throws SQLException
     */
    public  static void updateUsers() throws SQLException {

        String query = "SELECT * FROM client_schedule.users";
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        ContactsCustomersUsersComboBox.getUsers().clear();
        while (rs.next())
        {
            User user = new User();
            user.setUserID(rs.getInt("User_ID"));
            user.setUserName(rs.getString("User_Name"));

            users.add(user);
        }
    }
}

/**
 * Contact class that stores all relevant contact information
 */
class Contact{
    public int getContact_ID() {
        return contact_ID;
    }
    public void setContact_ID(int contact_ID) {
        this.contact_ID = contact_ID;
    }
    public String getContactName() {
        return contactName;
    }
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    private int contact_ID;
    private String contactName, email;

    /**
     *    used since to display its options the comboBox relies on the toString method
     *    the other option is to create a converter object, but this is simpler
     */
    @Override
    public String toString() {
        return this.contact_ID + " - " + this.contactName;
    }

}

/**
 * User class that stores all relevant User information
 */
class User{
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }


    private int userID;
    private String userName;

    /**
     *    used since to display its options the comboBox relies on the toString method
     *    the other option is to create a converter object, but this is simpler
     */
    @Override
    public String toString() {
        return this.userID+" - "+ this.userName;
    }
}

/**
 * Customer class that stores all relevant customer information
 */
class Customer{
    public int getCustomerID() {
        return customerID;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    private int customerID;
    private String customerName;

    /**
     *    used since to display its options the comboBox relies on the toString method
     *    the other option is to create a converter object, but this is simpler
     */
    @Override
    public String toString() {
        return this.customerID +" - "+ this.customerName;
    }
}