// Appointment.java
// W. Money
// 3/3/2023
// app representing an appointment

package edu.fscj.cop2805c.appointmentApp;

import java.time.ZonedDateTime;

public class Appointment {

    private String title;
    private String desc;
    private Contact contact;
    private ZonedDateTime apptTime;
    private ZonedDateTime apptRemind;
    private int reminder;


    // accessors
    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public Contact getContact() {
        return contact;
    }

    public ZonedDateTime getApptTime() {
        return apptTime;
    }

    public ZonedDateTime getApptRemind() {
        return apptRemind;
    }

    public int getReminder() {
        return reminder;
    }

    public StringBuilder getName() {
        return contact.getApptContactName();
    }

    public String getContactPref() {
        return contact.getContactPref();
    }

    public String getContactPhone() {
        return contact.getPhone();
    }

    // mutators
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setApptTime(ZonedDateTime apptTime) {
        this.apptTime = apptTime;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public Appointment(String title, String desc, Contact contact,
                       ZonedDateTime apptTime, int reminder) {
        this.title = title;
        this.desc = desc;
        this.contact = contact;
        this.apptTime = apptTime;
        this.reminder = reminder;
        this.apptRemind = apptTime.minusHours(reminder);
    }


    @Override
    public String toString() {
        String s = "Appt: \n";
        s += "\t Title: " + title + "\n";
        s += "\t Desc: " + desc + "\n";
        s += "\t Contact: " + contact + "\n";
        s += "\t Appt Date/Time: " + apptTime + "\n";
        s += "\t Reminder: " + apptRemind + "\n";
        return s;
    }




}
