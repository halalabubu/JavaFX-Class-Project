package src.c195classproject;

import java.sql.Timestamp;

/**appointment class used to store the details for an appointment*/
public class Appointment {

    //appointment columns
    private int appIDCol;
    private int customerIDCol;
    private int userIDCol;
    private int contactIDCol;
    private String titleCol;
    private String descriptionCol;
    private String locationCol;
    private String typeCol;
    private Timestamp startCol,endCol;

    //getters and setters
    public int getAppIDCol() {
        return appIDCol;
    }

    public void setAppIDCol(int appIDCol) {
        this.appIDCol = appIDCol;
    }

    public int getCustomerIDCol() {
        return customerIDCol;
    }

    public void setCustomerIDCol(int customerIDCol) {
        this.customerIDCol = customerIDCol;
    }

    public int getUserIDCol() {
        return userIDCol;
    }

    public void setUserIDCol(int userIDCol) {
        this.userIDCol = userIDCol;
    }

    public int getContactIDCol() {
        return contactIDCol;
    }

    public void setContactIDCol(int contactIDCol) {
        this.contactIDCol = contactIDCol;
    }

    public String getTitleCol() {
        return titleCol;
    }

    public void setTitleCol(String titleCol) {
        this.titleCol = titleCol;
    }

    public String getDescriptionCol() {
        return descriptionCol;
    }

    public void setDescriptionCol(String descriptionCol) {
        this.descriptionCol = descriptionCol;
    }

    public String getLocationCol() {
        return locationCol;
    }

    public void setLocationCol(String locationCol) {
        this.locationCol = locationCol;
    }

    public String getTypeCol() {
        return typeCol;
    }

    public void setTypeCol(String typeCol) {
        this.typeCol = typeCol;
    }

    public Timestamp getStartCol() {
        return startCol;
    }

    public void setStartCol(Timestamp startCol) {
        this.startCol = startCol;
    }

    public Timestamp getEndCol() {return endCol;}

    public void setEndCol(Timestamp endCol) {
        this.endCol = endCol;
    }

}

