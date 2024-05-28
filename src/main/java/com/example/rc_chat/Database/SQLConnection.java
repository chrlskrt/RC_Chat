package com.example.rc_chat.Database;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    /* FINAL PROJ CONNECTION */
    public static final String URL = "jdbc:mysql://147.185.221.19:57558/";
    public static String DBName = "";
    public static final String USERNAME = "rccola";
    public static final String PASSWORD = "rccolachat";

    /* TEMPORARY CONNECTION */
//    public static final String URL = "jdbc:mysql://localhost:3306/";
//    public static String DBName = "";
//    public static final String USERNAME = "root";
//    public static final String PASSWORD = "";

    public static Connection getConnection () throws RuntimeException {
        Connection c = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String finalURL = URL + DBName;
            c = DriverManager.getConnection(finalURL, USERNAME, PASSWORD);
            System.out.println("DB Connection success");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("DATABASE IS NOT ONLINE");
            throw new RuntimeException();
        }

        return c;
    }
}
