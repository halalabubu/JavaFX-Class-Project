package src.c195classproject;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * class that manages the lists that control the combo boxes used by the customer forms
 */
public class CountryAndDivisions {

    /**array of countries*/
    private static final ObservableList<Country> countries = FXCollections.observableArrayList();
    /**array of divisions*/
    private static final ObservableList<Division> divisions = FXCollections.observableArrayList();


    public static ObservableList<Country> getCountries(){return countries;};
    public static ObservableList<Division> getDivisions(){return divisions;};

    /**
     * updates both country and division to up to date with the database
     * @throws SQLException
     */
    public static void update() throws SQLException {

        String query = "SELECT * FROM countries";
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        //clear country and  divisions
        countries.clear();
        divisions.clear();

        while (rs.next()){
            Country country = new Country();

            country.setName(rs.getString("Country"));
            country.setCountryID(rs.getInt("Country_ID"));
            countries.add(country);
        }
        query = "SELECT * FROM first_level_divisions";
        rs = stmt.executeQuery(query);
        while (rs.next()){
            Division division = new Division();

            division.setName(rs.getString("Division"));
            division.setCountryID(rs.getInt("Country_ID"));
            division.setDivisionID(rs.getInt("Division_ID"));
            divisions.add(division);
        }
    }

    /**
     * returns the div associated with the given id
     * @param id id used to form sql query
     * @return Division
     * @throws SQLException
     */
    public static Division getDivfromID(int id) throws SQLException {
        String query = "SELECT * FROM first_level_divisions WHERE Division_ID = " + id;
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        rs.next();

        Division division = new Division();

        division.setName(rs.getString("Division"));
        division.setCountryID(rs.getInt("Country_ID"));
        division.setDivisionID(rs.getInt("Division_ID"));

        return division;
    }

    /**
     * returns the country associated with the given id
     * @param id id used to form sql query
     * @return country
     * @throws SQLException
     */
    public static Country getCountryFromID (int id) throws SQLException {

        String query = "SELECT * FROM countries WHERE Country_ID = " + id;
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        rs.next();
        Country country = new Country();

        country.setName(rs.getString("Country"));
        country.setCountryID(rs.getInt("Country_ID"));

        return country;
    }

}

/**
 * class that holds all relevant country data
 */
class Country{
    private String name;
    private int countryID;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCountryID() {
        return countryID;
    }
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    //used since to display its options the comboBox relies on the toString method
    //the other option is to create a converter object, but this is simpler
    @Override
    public String toString() {
        return this.name;
    }
}

/**
 * class that holds all relevant division data
 */
class Division{
    private String name;
    private int countryID;
    private int divisionID;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCountryID() {
        return countryID;
    }
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
    public int getDivisionID() {
        return divisionID;
    }
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    //used since to display its options the comboBox relies on the toString method
    //the other option is to create a converter object, but this is simpler
    @Override
    public String toString() {
        return this.name;
    }
}