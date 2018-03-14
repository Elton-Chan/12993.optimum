package com.optimum.application;

import java.sql.*;
import java.util.Scanner;

import com.optimum.connection.DatabaseConnection;
import com.optimum.controller.Controller;
import com.optimum.pojo.Pojo;

public class LoginPage extends DatabaseConnection{

	public static void main(String[] args)throws Exception {

		Controller refController = new Controller();
		DatabaseConnection refDatabase = new DatabaseConnection();

		refDatabase.getConnection();
		refController.menu();

	}
}

