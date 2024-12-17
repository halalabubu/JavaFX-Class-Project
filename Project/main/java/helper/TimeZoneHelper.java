package helper;

import src.c195classproject.Appointment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;



/**
 * Helper class used to convert time to and from local,ET, and UTC.
 */
public class TimeZoneHelper {
    /**
     * string constant used to set the formatter so LOCALDATETIME knows how to parse the strings
     */
    public static final String DATE_FORMAT = "uuuu-M-dd H:m:s";// final constant so public should be good. other libraries do this (calander,localdatetime,etc)
    /**
     * Current time zone
     */
    private static final ZoneId CURRENT_ZONE = ZoneId.systemDefault();
    /**
     * UTC Time Zone
     */
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    /**
     * ET Time Zone
     */
    private static final ZoneId ET_ZONE = ZoneId.of("America/New_York");
    /**
     * used to parse string with the set DATE_FORMAT
     */
    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
    /**
     * used for testing purposes, prints current time zone time, ET time, and UTC time
     */
    public static void test(){

        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern();

        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime nowWithID = now.atZone(CURRENT_ZONE);
        ZonedDateTime utcZoned = nowWithID.withZoneSameInstant(UTC_ZONE);
        ZonedDateTime etZoned = nowWithID.withZoneSameInstant(ET_ZONE);

        //System.out.println("now: " + dtfh.format(now));
        //System.out.println("UTC: " + dtfh.format(utcZoned));
        //System.out.println("ET: " + dtfh.format(etZoned));
    }

    /**
     * convert from local to UTC
     * @param localDateTime Time to be converted
     * @return returns the LocalDateTime
     */
    public static LocalDateTime localtoUTC (LocalDateTime localDateTime)
    {
        ZonedDateTime ldtWithZone = localDateTime.atZone(CURRENT_ZONE);
        return ldtWithZone.withZoneSameInstant(UTC_ZONE).toLocalDateTime();
    }
    /**
     * convert from local to ET
     * @param localDateTime Time to be converted
     * @return returns the LocalDateTime
     */
    public static LocalDateTime localtoET (LocalDateTime localDateTime)
    {
        ZonedDateTime ldtWithZone = localDateTime.atZone(CURRENT_ZONE);
        return ldtWithZone.withZoneSameInstant(ET_ZONE).toLocalDateTime();
    }

    /**
     * Convert from UTC to Local
     * @param utcDateTime Time to be converted
     * @return returns the LocalDateTime
     */
    public static LocalDateTime utcToLocal (LocalDateTime utcDateTime)
    {
        ZonedDateTime utcWithZone = utcDateTime.atZone(UTC_ZONE);
        return utcWithZone.withZoneSameInstant(CURRENT_ZONE).toLocalDateTime();
    }

    /**
     * Cconverts from local to ET, then checks against work hours and if it falls on the weekend
     * @param localDateTime Time to be converted
     * @return returns the LocalDateTime
     */
    public static boolean isOnWorkHours (LocalDateTime localDateTime){
        LocalDateTime timeInET =  localtoET(localDateTime);

        DayOfWeek day = timeInET.getDayOfWeek();

        if (day.equals(DayOfWeek.SATURDAY)) {
            InputValidation.outOfWorkHoursError();
            return false;}

        if (day.equals(DayOfWeek.SUNDAY)) {
            InputValidation.outOfWorkHoursError();
            return false;}

        if (timeInET.getHour() >= 8 && timeInET.getHour() < 22) return true;

        InputValidation.outOfWorkHoursError();
        return false;
    }

    /**
     * Check if DateTime is during another appointment but excludes results that match the given ID
     * Generally used to exclude self
     * @param localDateTime Time to be checked. Converted to UTC
     * @param ID ID to be excluded
     * @return returns true or false
     * @throws SQLException
     */
    public static boolean validApppointmentExcludeID(LocalDateTime localDateTime, int ID)throws SQLException {

        LocalDateTime timeInUTC =  localtoUTC(localDateTime);
        String query = "SELECT * FROM appointments WHERE '"+ timeInUTC +"' BETWEEN Start AND End " +
                "AND Appointment_ID != " + ID;
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        if (!rs.next())
            return true;

        InputValidation.appointmentAlreadyExists();
        return false;
    }



    /**
     * Check if DateTime is during another appointment
     * @param localDateTime Time to be checked. Converted to UTC
     * @return  returns true or false
     * @throws SQLException
     */
    public static boolean validAppointmentTime(LocalDateTime localDateTime) throws SQLException {

        LocalDateTime timeInUTC =  localtoUTC(localDateTime);
        String query = "SELECT * FROM appointments WHERE '"+ timeInUTC +"' BETWEEN Start AND End";
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        if (!rs.next())
            return true;

        InputValidation.appointmentAlreadyExists();
        return false;
    }


    /**
     * checks if both start and end fall on the same day (DOES NOT CONVERT TO ET)
     * @param start start date to check
     * @param end end date to check
     * @return returns true or false
     */
    public static boolean startEndOnSameDay(LocalDateTime start, LocalDateTime end){
        start = start.truncatedTo(ChronoUnit.DAYS);
        end = end.truncatedTo(ChronoUnit.DAYS);

        if (start.equals(end))
            return true;

        InputValidation.startEndOnSameDayError();
        return false;
    }

    /**
     * Checks if an appointment is within 15 minutes
     * @throws SQLException
     */
    public static void checkApp15Min() throws SQLException {
        final int fifteenMinWindow = 15;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus15;
        now = TimeZoneHelper.localtoUTC(now);
        nowPlus15 = now.plusMinutes(fifteenMinWindow);

        String query = "SELECT * FROM appointments WHERE Start BETWEEN '"+now+"' and '"+ nowPlus15+"'";
        Statement stmt = JDBC.connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        Appointment app = new Appointment();

        if (rs.next())
        {
            //there is at least 1 appointment within 15 minutes

            app.setAppIDCol(rs.getInt("Appointment_ID"));
            app.setTitleCol(rs.getString("Title"));
            app.setDescriptionCol(rs.getString("Description"));
            app.setLocationCol(rs.getString("Location"));
            app.setTypeCol(rs.getString("Type"));
            app.setStartCol(rs.getTimestamp("Start"));
            app.setEndCol(rs.getTimestamp("End"));
            //we don't need the rest

            //Create alert for appointment within 15 minutes
            InputValidation.appointmentWithin15MinError(app);
            return;
        }
        //no appointments
        InputValidation.noAppWithin15Minutes();

    }
}
