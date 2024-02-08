package android.application.duties_for_all;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Date {
    //variables
    private final int day;
    private final int month;
    private final int year;

    //constructors
    public Date() {
        Calendar cal = Calendar.getInstance();
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.day = cal.get(Calendar.DAY_OF_MONTH);
    }
    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }
    public Date(String date) {
        char[] chars = date.toCharArray();
        String day;
        String month = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]);
        String year;
        if (chars.length == 11) {
            day = String.valueOf(chars[4]);
            year = String.valueOf(chars[7]) + String.valueOf(chars[8]) + String.valueOf(chars[9]) + String.valueOf(chars[10]);
        }
        else {
            day = String.valueOf(chars[4]) + String.valueOf(chars[5]);
            year = String.valueOf(chars[8]) + String.valueOf(chars[9]) + String.valueOf(chars[10]) + String.valueOf(chars[11]);
        }
        this.day = Integer.parseInt(day);
        this.month = findMonth(month);
        this.year = Integer.parseInt(year);
    }

    //getters
    public int getDay() {return day;}
    public int getMonth() {return month;}
    public int getYear() {return year;}

    //methods
    private int findMonth(String month) {
        if (month.equals("Jan")) {return 1;}
        if (month.equals("Feb")) {return 2;}
        if (month.equals("Mar")) {return 3;}
        if (month.equals("Apr")) {return 4;}
        if (month.equals("May")) {return 5;}
        if (month.equals("Jun")) {return 6;}
        if (month.equals("Jul")) {return 7;}
        if (month.equals("Aug")) {return 8;}
        if (month.equals("Sep")) {return 9;}
        if (month.equals("Oct")) {return 10;}
        if (month.equals("Nov")) {return 11;}
        if (month.equals("Dec")) {return 12;}
        //default, should never occur
        return 1;
    }
    @NonNull
    public String toString() {
        if (month == 1) {return "Jan" + " " + day + ", " + year;}
        if (month == 2) {return "Feb" + " " + day + ", " + year;}
        if (month == 3) {return "Mar" + " " + day + ", " + year;}
        if (month == 4) {return "Apr" + " " + day + ", " + year;}
        if (month == 5) {return "May" + " " + day + ", " + year;}
        if (month == 6) {return "Jun" + " " + day + ", " + year;}
        if (month == 7) {return "Jul" + " " + day + ", " + year;}
        if (month == 8) {return "Aug" + " " + day + ", " + year;}
        if (month == 9) {return "Sep" + " " + day + ", " + year;}
        if (month == 10) {return "Oct" + " " + day + ", " + year;}
        if (month == 11) {return "Nov" + " " + day + ", " + year;}
        if (month == 12) {return "Dec" + " " + day + ", " + year;}
        //default, should never occur
        return "Jan" + " " + day + ", " + year;
    }
    public boolean before(Date date) {
        if (year > date.getYear()) {return false;}
        else if (year == date.getYear()) {
            if (month > date.getMonth()) {return false;}
            else if (month == date.getMonth()) {
                if (day > date.getDay()) {return false;}
                else return day != date.getDay();
            }
            else {return true;}
        }
        else {return true;}
    }
    public Date getNext() {
        int month;
        int day;
        int year;
        if (((this.month == 1 || this.month == 3 || this.month == 5 || this.month == 7 || this.month == 8 || this.month == 10 || this.month == 12) &&
                this.day == 31) || ((this.month == 4 || this.month == 6 || this.month == 9 || this.month == 11) && this.day == 30) ||
                (this.month == 2 && ((this.day == 28 && this.year % 4 != 0) || this.day == 29))) {
            day = 1;
            month = this.month + 1;
        }
        else {
            day = this.day + 1;
            month = this.month;
        }
        if (this.month == 12 && this.day == 31) {
            month = 1;
            year = this.year + 1;
        }
        else {year = this.year;}

        return new Date(day, month, year);
    }
}

