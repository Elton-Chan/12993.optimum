package com.optimum.connection;

import java.sql.*;

public class DatabaseConnection {

	public static Connection conn;

	public void getConnection() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn=DriverManager.getConnection(  
					"jdbc:mysql://localhost:3306/batch5","root","root");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e1) {
			e1.printStackTrace();
		}


	} // end of getConnection


	// to close connection
	public static void closeConnection(Connection conn) {

		try {
			conn.close();

		} catch (SQLException e) {
			//
		}

	}



}

