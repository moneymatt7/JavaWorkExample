// Appointment.java
// W. Money
// 3/3/2023
// app that tracks appointments and sends reminders

// W. Money
// 3/20/2023
// queue now ConcurrentLinkedQueue
// buildReminder method moved to Reminder.java

// W. Money
// 3/22/2023
// added methods to save contacts to data file

package edu.fscj.cop2805c.appointmentApp;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import edu.fscj.cop2805c.dispatch.Dispatcher;

import java.io.*;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.*;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;


public class AppointmentApp implements AppointmentReminder, Dispatcher<Reminder> {

    // file to be created and read storing Contact objects
// private static final String CONTACT_FILE = "contacts.dat";

    // list of appointments
    ArrayList<Appointment> appointmentsList = new ArrayList<>();

    // queue of reminders for appointments
    // changed to thread-safe Queue<LinkedList> 3/20/2023
   ConcurrentLinkedQueue safeQueue = new ConcurrentLinkedQueue(
           new LinkedList<Reminder>()
    );
    // stream to output reminders from queue
    private Stream<Reminder> stream = safeQueue.stream();

    public AppointmentApp(){

    }

    // add multiple appointments
    public void addAppointments(Appointment... appointments) {
        for (Appointment a : appointments) {
            appointmentsList.add(a);
        }

    }

    // method with loop to add different appointments with random times and
    public Appointment genAppts(Contact c){
        Random r = new Random();
        String title = "Test appointment";
        String desc = "This is a test appointment";
        int reminder = c.getReminder();
        // randomly generated variable for time and date of appointment
        ZonedDateTime apptTime =
                ZonedDateTime.now().plusHours(r.nextInt(24)).plusDays(r.nextInt(365));


        Appointment appt = new Appointment( title, desc, c,
                apptTime, reminder );

        return appt;
    }

    // dispatch a reminder to the queue
    public void dispatch(Reminder reminder) {
        this.safeQueue.add(reminder);
        System.out.println("current queue length is " + this.safeQueue.size());
    }

    // add a reminder to the queue, output info about reminder
    public void sendReminder(Reminder reminder) {
        Contact c = reminder.getContact();
        String sentContactPref =
                switch (c.getContactPref()) {
                    case "TEXT", "PHONE" -> sentContactPref = "SMS message";
                    case "EMAIL" -> sentContactPref = "email message";
                    case "NONE" -> sentContactPref = " empty";
                    default -> " no message sent ";
                };

        System.out.println("Sending the following " + sentContactPref +
                " to " + reminder.contact.getApptContactName() + " at " +
                reminder.contact.getPhone() + "\n");

        // dispatch a reminder
        // dispatch(reminder);

        // lambda to dispatch reminder
       Dispatcher<Reminder> d = (r)-> {
            this.safeQueue.add(r);
            //System.out.println("The current queue length is " + this.queue.size());
        };
        d.dispatch(reminder);
    }

    // checks if time requested by contact for reminder is now
    public boolean triggerReminder(Appointment a) {
        boolean result = false;
        ZonedDateTime timeNow = a.getApptRemind();

        // W.Money
        // 3/22/2023
        // changed if to provide test for no reminder
        // else if produces positive results
        if (a.getName().toString().equals("Steve Smith")){
            result = false;
            return result;
        }
        else if (a.getApptRemind().equals(timeNow)) {
            result = true;
        }
        return result;
    }

    // check if time to send reminder, if true build and send reminder to dispatch
    public void sendReminders(AppointmentApp a) {
        for (int i=0; i < a.appointmentsList.size(); i++){
            Reminder r = new Reminder(a.appointmentsList.get(i),
                    a.appointmentsList.get(i).getApptRemind(),
                    a.appointmentsList.get(i).getContact());

            if (a.triggerReminder(a.appointmentsList.get(i))) {
                a.sendReminder(r);
            }
            // show date of next appointment for each not in reminder window
            else
                System.out.println("Reminder will be sent to " +
                        a.appointmentsList.get(i).getName() + " on " +
                        a.appointmentsList.get(i).getApptRemind().getDayOfWeek() +
                        ", " + a.appointmentsList.get(i).getApptRemind().getMonth() +
                        " " + a.appointmentsList.get(i).getApptRemind().getDayOfMonth());
        }
        appointmentsList.clear();
    }

    // read saved appointments ArrayList
/*    public ArrayList<Contact> readContacts() {
        ArrayList<Contact> list = new ArrayList<>();

        try (ObjectInputStream contactData = new ObjectInputStream(
                new FileInputStream(CONTACT_FILE));) {
            list = (ArrayList<Contact>) (contactData.readObject());
            for (Contact c : list)
                System.out.println("readContacts: read " + c);
        } catch (FileNotFoundException e) {
            System.err.println("readContacts: no input file");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }
*/
    // write contacts from ArrayList to .dat file and save*  public void writeContacts(ArrayList<Contact> cl) {
  /*    try (ObjectOutputStream contactData = new ObjectOutputStream(
                new FileOutputStream(CONTACT_FILE));) {
                    contactData.writeObject(cl);
        } catch (IOException e) {
            e.printStackTrace();
      }
    }
*/




    public static void main(String[] args) {

        AppointmentApp appts = new AppointmentApp();


        ContactDB.createDB();
        // list of contacts
         ArrayList<Contact> contactsList = ContactDB.readContactDB();

        // starts processor thread
      AppointmentReminderProcessor processor = new AppointmentReminderProcessor(appts.safeQueue);



/*
        // if no contacts saved in file, create contacts
        if (contactsList.isEmpty()) {
            // construct test Contact objects
            Contact contact1 = new Contact("Ash", "Williams", "Ash@sMart.com",
                    "555-555-5555", PHONE, new Locale("en"), 1);
            contactsList.add(contact1);
            Contact contact2 = new Contact("Joëlle", "Coutaz", "Joëlle.Coutaz@email.com",
                    "666-666-6666", EMAIL, new Locale("fr"), 2);
            contactsList.add(contact2);
            Contact contact3 = new Contact("Andy", "Bechtolsheim", "Andy.Bechtolsheim@email.com",
                    "777-777-7777", EMAIL, new Locale("de"), 3);
            contactsList.add(contact3);
            Contact contact4 = new Contact("Dave", "Xia", "Xia.Peisu@email.com",
                    "888-888-8888", EMAIL, new Locale("zh"), 4);
            contactsList.add(contact4);
            // contact to test with no reminder sent
            Contact contact5 = new Contact("Steve", "Smith", "SteveSmith@email.com",
                    "999-999-9999", EMAIL, new Locale("en"), 5);
            contactsList.add(contact5);
        }
        else {
            System.out.println("Contacts read from data file!");
        }
*/

        // add appointments for each contact
        for (Contact contact : contactsList) {
            appts.addAppointments(appts.genAppts(contact));
        }

        // send reminders
        appts.sendReminders(appts);

        // wait
       try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ie) {
            System.out.println("Sleep interrupted! " + ie);
        }
/*
        // create more appointments for test data
        for (Contact contact : contactsList) {
            appts.addAppointments(appts.genAppts(contact));
        }

        // create and add reminders again
        appts.sendReminders(appts);

        // wait again
       try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ie) {
            System.out.println("Sleep interrupted! " + ie);
        }
*/

        // end polling
    processor.endProcessing();

        // generate (or regenerate) the user data file
        //appts.writeContacts(contactsList);

        ContactDB.deleteDB();
    }


}