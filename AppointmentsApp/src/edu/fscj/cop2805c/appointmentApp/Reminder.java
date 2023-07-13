// Appointment.java
// W. Money
// 3/3/2023
// class represents a reminder for an appointment

// W. Money
// 3/21/2023
// moved buildReminder method from AppointmentApp.Java
// implements ReminderBuilder interface

package edu.fscj.cop2805c.appointmentApp;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Reminder implements ReminderBuilder {

    // String message is created by buildReminder in AppointmentApp.java
    private String message;
    private ZonedDateTime reminderTime;
    Contact contact;
    Appointment appt;

    public Reminder() {
    }

    public Reminder(Appointment appt, ZonedDateTime reminderTime, Contact contact) {
        this.appt = appt;
        this.reminderTime = reminderTime;
        this.contact = contact;

        buildReminder(appt);
    }

    public String getMessage() {
        return message;
    }

    public ZonedDateTime getDateTime() {
        return reminderTime;
    }

    public Contact getContact() {
        return contact;
    }

    // overrides toString to display String message
    @Override
    public String toString() {
        String s = message;
        return s;
    }

    // builds a reminder message,
    public void buildReminder(Appointment appt) {
        final String NEWLINE = "\n";

        Contact c = appt.getContact();

        // format and localize DateTime output
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
        formatter = formatter.localizedBy(c.getLocale());

        String apptTime = appt.getApptTime().format(formatter);

        ResourceBundle res = ResourceBundle.getBundle(
                "edu.fscj.cop2805c.appointmentApp.ReminderMsg", c.getLocale());

        String reminderGreeting = res.getString("ReminderMsg");

        // body of text for reminder
        String msg =
                apptTime + NEWLINE +
                        reminderGreeting + NEWLINE +
                        c.getApptContactName() + NEWLINE +
                        "Title: " + appt.getTitle() + NEWLINE +
                        "Desc: " + appt.getDesc() + NEWLINE;

        // format and decorate output of reminder message (msg)
        // get the widest line and the number of lines in the message
        int longest = getLongest(msg);
        // split and get the max length
        String[] msgSplit = msg.split(NEWLINE);
        int maxLen = 0;
        for (String s : msgSplit)
            if (s.length() > maxLen)
                maxLen = s.length();
        maxLen += 4; // Adjust for padding and new line

        // make headerFooter of plus signs
        char[] plusChars = new char[maxLen];
        Arrays.fill(plusChars, '+');
        String headerFooter = new String(plusChars);

        // add the header to our output
        String newMsg = headerFooter + "\n";

        // reuse the header template for our body lines (plus/spaces/plus)
        Arrays.fill(plusChars, ' ');
        plusChars[0] = plusChars[maxLen - 1] = '+';
        String bodyLine = new String(plusChars);

        // for each string in the output, insert into a body line
        for (String s : msgSplit) {
            StringBuilder sBld = new StringBuilder(bodyLine);
            // add 2 to end position in body line replace
            // operation so final space/plus don't get pushed out
            sBld.replace(2,s.length() + 2, s);
            // add to our output
            newMsg += new String(sBld) + "\n";
        }
        newMsg += headerFooter + "\n";

        // debug built message
        // System.out.println(newMsg);

        // assign reminder output built to message variable
        message = newMsg;

    }

    public int getLongest(String s) {
        final String NEWLINE = "\n";
        int maxLength = 0;
        String[] splitStr = s.split(NEWLINE);
        for (String line : splitStr){
            if (line.length() > maxLength)
                maxLength = line.length();
        }
        return maxLength;
    }
}