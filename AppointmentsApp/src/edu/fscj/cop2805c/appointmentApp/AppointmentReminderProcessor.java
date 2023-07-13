// AppointmentReminderProcessor.java
// W. Money
// 3/20/2023
// processes appointment reminders

// W. Money
// 3/22/2023
// added error logging

// W. Money
// 4/3/2023
// added aggregate log server from ReminderLogger.java




package edu.fscj.cop2805c.appointmentApp;

import edu.fscj.cop2805c.log.Logger;
import edu.fscj.cop2805c.reminder.ReminderProcessor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentLinkedQueue;


public class AppointmentReminderProcessor extends Thread
implements ReminderProcessor, Logger<Reminder> {

    private static final String LOGFILE = "reminderLog.txt";

    private ConcurrentLinkedQueue<Reminder> safeQueue;
    private boolean stopped = false;
    private ReminderLogger logger;
    private Socket logSocket;
    private ObjectOutputStream streamToServer;

    public AppointmentReminderProcessor(ConcurrentLinkedQueue<Reminder> safeQueue) {
        this.safeQueue = safeQueue;
        logger = new ReminderLogger();

        try {
            logSocket = new Socket("localhost", ReminderLogger.LOGPORT);
            System.out.println("Connected to log server.");

            streamToServer = new ObjectOutputStream(logSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // start polling, invoke run() line 55
        this.start();
    }

    // remove reminders from queue and process
    public void processReminders() {
        System.out.println("Queue size is " + safeQueue.size() +
                " before processing.");

        safeQueue.stream().forEach(e -> {
            e = safeQueue.remove();
            System.out.println(e);
            log(e);
        });
        System.out.println("Queue size is " + safeQueue.size() +
                " after processing.");
    }

    // allow external class to end processing
    public void endProcessing() {
        this.stopped = true;
        try {
            logSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        interrupt();
    }

    // poll queue for appointment reminders
    public void run() {
        final int SLEEP_TIME = 1000; //in ms
        while (true){
            try{
                processReminders();
                Thread.sleep(SLEEP_TIME);
                System.out.println("Polling...");
            }
            catch (InterruptedException ie) {
                // check to see if exit conditions met
                if (this.stopped == true) {
                    System.out.println("Poll thread has received the exit signal.");
                    break;
                }
            }
        }
    }

    // method to log when a reminder is built and save to file
   @Override
    public void log(Reminder r) {
        String msg = ":reminder:" + r.getContact().getName();
        try  {
            streamToServer.writeObject(msg);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
