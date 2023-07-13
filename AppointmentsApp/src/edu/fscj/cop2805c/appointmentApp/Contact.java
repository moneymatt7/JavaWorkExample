// Appointment.java
// W. Money
// 3/3/2023
// class representing the contact of each appointment

package edu.fscj.cop2805c.appointmentApp;

import java.io.Serializable;
import java.util.Locale;

// class representing an individual contact
class Contact implements Serializable {

    private static Integer idStatic = 0;
    private Integer id; // primary key
    private StringBuilder name;
    private String fName;
    private String lName;
    private String email;
    private String phone;
    private PREFCONTACT contactPref;
    private Locale locale;
    private StringBuilder apptContactName;
    private int reminder;


    // accessors
    public Integer getId() { return id; }
    public StringBuilder getName() {
        return name;
    }

    public String getFName() {
        return fName;
    }

    public String getLName() {
        return lName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getContactPref() {
        return contactPref.toString();
    }

    public Locale getLocale() {
        return locale;
    }

    public int getReminder() { return reminder; }

    // accessor for name in appointment reminder
    public StringBuilder getApptContactName() {
        return apptContactName;
    }


    // mutators
    public void setName(StringBuilder name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setContactPref(PREFCONTACT contactPref) {
        this.contactPref = contactPref;
    }

    // overloaded constructor to create individual contact
    public Contact(Integer id, String fName, String lName, String email, String phone,
                   PREFCONTACT contactPref, Locale locale, int reminder) {

        if (id == 0) {
            idStatic++;
            this.id = idStatic;
        } else {
            this.id = id;
        }

        this.name = new StringBuilder();
        this.name.append(lName).append(", ").append(fName);
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phone = phone;
        this.contactPref = contactPref;
        this.locale = locale;
        this.apptContactName = new StringBuilder();
        this.apptContactName.append(fName).append(" ").append(lName);
        this.reminder = reminder;
    }

    @Override
    public String toString() {
        String s = email + ",(" + name + ")" + phone + "," +
                contactPref + "," + locale;
        return s;
    }
}
