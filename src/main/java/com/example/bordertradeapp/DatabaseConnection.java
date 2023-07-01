package com.example.bordertradeapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{

    //Logger logger = LogManager.getLogger(DbConnection.class);

    private Connection dbconnection;

    public Connection getDbconnection()
    {
        return dbconnection;
    }

    public DatabaseConnection()
    {
        //System.out.println("Connection is established");

        String url = "jdbc:postgresql://localhost:5432/cbt";
        try
        {
            dbconnection = DriverManager.getConnection(url,"postgres","Dedsec@7");
            //System.out.println("Connection is established");
            // logger.info("Connection Successfully Established");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}
