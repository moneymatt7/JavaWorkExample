// ReminderLogger.java
// W. Money
// 4/3/2023
// class to handle network log requests

package edu.fscj.cop2805c.appointmentApp;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class ReminderLogger extends Thread {
    // allow clients to access port
    final static int LOGPORT = 9000;

    private static final String LOGFILE = "reminderLog.txt";
    private ServerSocket server;
    private Socket socket;
    private ObjectInputStream inputStream;
    private BufferedWriter reminderLog;

    // constructor opens log file and starts server thread
    public ReminderLogger() {
        try {
            reminderLog = Files.newBufferedWriter(Path.of(LOGFILE),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            start();
        } catch (IOException e) {
            System.err.println("Could not open log file");
            e.printStackTrace();
        }
    }

    // server thread
    public void run() {

        try {
            server = new ServerSocket(LOGPORT);

            socket = server.accept();
            inputStream = new ObjectInputStream(socket.getInputStream());

            // read until connection closed by client
            while (true) {
                String logMsg = (String) inputStream.readObject();
                System.out.println("server received " + logMsg);
                LocalDateTime local = LocalDateTime.from(
                        Instant.now().atZone(ZoneId.systemDefault()));
                String msg = local.truncatedTo(ChronoUnit.MILLIS) + logMsg;
                reminderLog.write(msg);
                reminderLog.newLine();
            }
        } catch (EOFException e) {
            // connection closed by client, shutdown
            System.out.println("Logger connection closed.");
            // flush once with nested try/catch
            try {reminderLog.flush(); } catch (IOException e1) { }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
