package com.example.rc_chat.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    /* FINAL PROJ CONNECTION */
//    public static final String URL = "jdbc:mysql://insert_ip/";
//    public static String DBName = "";
//    public static final String USERNAME = "rccola";
//    public static final String PASSWORD = "rccolachat";

    /* TEMPORARY CONNECTION */
    public static final String URL = "jdbc:mysql://localhost:3306/";
    public static String DBName = "";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    public static Connection getConnection (){
        Connection c = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String finalURL = URL + DBName;
            c = DriverManager.getConnection(finalURL, USERNAME, PASSWORD);
            System.out.println("DB Connection success");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return c;
    }
}
