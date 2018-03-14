package com.optimum.dao;


import com.optimum.connection.DatabaseConnection;
import com.optimum.controller.Controller;

import java.net.*;
import java.sql.*;
import java.util.*;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class LoginDao extends DatabaseConnection{

	//variables to check if true/false
	private static boolean status;
	private static boolean firstlogin;
	private static boolean checkStatus;
	private static boolean checkDuplicates;
	private static boolean checkPassword;
	private static boolean checkAnswer;
	private static boolean checkLock;
	public static int counter =0;
	PreparedStatement preparedStatement = null;

	//Getter Setters for boolean variables
	public static boolean isCheckLock() {
		return checkLock;
	}

	public static void setCheckLock(boolean checkLock) {
		LoginDao.checkLock = checkLock;
	}

	public static boolean isCheckAnswer() {
		return checkAnswer;
	}

	public static void setCheckAnswer(boolean checkAnswer) {
		LoginDao.checkAnswer = checkAnswer;
	}


	public boolean isStatus() {
		return status; 
	}

	public static boolean isFirstlogin() {
		return firstlogin;
	}

	public static void setFirstlogin(boolean firstlogin) {
		LoginDao.firstlogin = firstlogin;
	}

	public void setStatus(boolean status) {
		LoginDao.status = status;
	}

	public static boolean isCheckStatus() {
		return checkStatus;
	}

	public static void setCheckStatus(boolean checkStatus) {
		LoginDao.checkStatus = checkStatus;
	}

	public static boolean isCheckPassword() {
		return checkPassword;
	}

	public static void setCheckPassword(boolean checkPassword) {
		LoginDao.checkPassword = checkPassword;
	}

	//end of Getter Setters


	public void loginAuthentication(String login,String pass) {//this method is to check for login whether it is true/ first login/ account locked 

		Controller refController = new Controller();

		try {

			Statement stmt= conn.createStatement();
			ResultSet rs=stmt.executeQuery("select * from loginapp WHERE email ='"+login+"'" );

			while (rs.next())
			{

				String email = rs.getString("email");
				String password = rs.getString("password");
				Integer attempts = rs.getInt("attempts");
				Integer first = rs.getInt("first_login");
				Integer status = rs.getInt("status");

				if(status==1) {//checks if account is locked
					setCheckLock(true);
					if(attempts < 3) {//checks if account is going to be locked

						if(login.equals(email) && pass.equals(password)) {//checks if login authentication is correct
							
							LoginDao.counter=0;	
							setStatus(true);
							
							if(first == 1) {
								setFirstlogin(true);
							}
							else {
								setFirstlogin(false);
								String query = "UPDATE loginapp set first_login =0 where email ='"+email+"'";
								preparedStatement = conn.prepareStatement(query);
								preparedStatement.executeUpdate();
							}
							String query = "UPDATE loginapp set attempts =0 where email ='"+email+"'";
							preparedStatement = conn.prepareStatement(query);
							preparedStatement.executeUpdate();

						}

						else if (login.equals(email) && pass!=password ) {

							setStatus(false);
							
							System.out.println("Wrong Password please try again");
							System.out.println("Enter Password: ");
							
							LoginDao.counter += 1;
														
							String query = "UPDATE loginapp set attempts ="+LoginDao.counter +" where email ='"+email+"'";
							preparedStatement = conn.prepareStatement(query);
							preparedStatement.executeUpdate();
							
							refController.password(login);

						}
						else {
							setStatus(false);
							System.out.println("Sorry Login ID not found please try again.");
							refController.loginMenu();
						}

					}
					else {
						String query = "UPDATE loginapp set status =0 where email ='"+login+"'";
						preparedStatement = conn.prepareStatement(query);
						preparedStatement.executeUpdate();
						
						LoginDao.counter=0;	
						System.out.println("Your account is locked");
					}

				}
				else {
					setCheckLock(false);

				}
			}

		}

		catch(Exception e) {
			System.out.println(e);
		}

	}//end of loginAuthentication

	public void insertRegistration(String name,String nric,String dob,String email,Integer mobile) {//this method is to register new users

		Controller refController = new Controller();

		try {
			Statement stmt= conn.createStatement();
			ResultSet rs=stmt.executeQuery("select * from loginapp where email <> 'admin' ");

			while(rs.next()) {//checks if any duplicates are found

				Integer mobileRs = rs.getInt("mobile");
				String nricRs = rs.getString("nric");
				String emailRs = rs.getString("email");

				if(mobileRs == mobile) {
					System.out.println("User is already registered!");
					refController.name();
				}
				else if (nricRs.equals(nric)) {
					System.out.println("User is already registered!");
					refController.name();
				}
				else if (emailRs.equals(email)) {
					System.out.println("User is already registered!");
					refController.name();
				}
				else {
					checkDuplicates = true;
				}



			}
			if(checkDuplicates == true) {//double checks if any duplicates were found if not executes insert to database
				String temppass = nric.substring(1, 5)+mobile.toString().substring(4,8);
				String query = "INSERT INTO loginapp(name,nric,email,dob,mobile,password,role,first_login,status,attempts)"+
						"values(?,?,?,?,?,?,?,?,?,?)";
				sendMail(name, email,temppass);
				preparedStatement = conn.prepareStatement(query);
				preparedStatement.setString(1, name);
				preparedStatement.setString(2, nric);
				preparedStatement.setString(3, email);
				preparedStatement.setString(4, dob);
				preparedStatement.setInt(5, mobile);
				preparedStatement.setString(6, temppass);
				preparedStatement.setString(7, "user");
				preparedStatement.setInt(8, 1);
				preparedStatement.setInt(9, 1);
				preparedStatement.setInt(10, 0);
				preparedStatement.executeUpdate();

			}
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}//end of insertRegistration

	public void insertSecurity(String pass,String question,String answer,String login) {//this method is to insert security question/answer for first login

		String query = "UPDATE loginapp set password =?,security_question =?,security_answer =?,first_login=? where email ='"+login+"'";
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, pass);
			preparedStatement.setString(2, question);
			preparedStatement.setString(3, answer);
			preparedStatement.setInt(4, 0);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end of insertSecurity

	public void checkPass(String password,String login) {//this method is to check if password is correct during first login

		try {
			Statement stmt= conn.createStatement();
			ResultSet rs=stmt.executeQuery("select * from loginapp WHERE email ='"+login+"'" );

			while(rs.next()) {

				String passRs = rs.getString("password");
				String emailRs = rs.getString("email");
				if(emailRs.equals(login)&&passRs.equals(password)) {
					setCheckPassword(true);
				}
				else {
					System.out.println("Your Temp Pass is Wrong");
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}//end of checkPass

	public void forgetSecurity(String login) {//this method is to show Security Question when user enters forget password

		try {
			Statement stmt= conn.createStatement();
			ResultSet rs=stmt.executeQuery("select * from loginapp WHERE email ='"+login+"'" );

			while(rs.next()) {

				String securityQ = rs.getString("security_question");
				System.out.println(securityQ);

			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}//end of forgetSecurity

	public void forgetAnswer(String login,String answer) {//this method is to set new pass if security answer is correct

		try {
			Statement stmt= conn.createStatement();
			ResultSet rs=stmt.executeQuery("select * from loginapp WHERE email ='"+login+"'" );

			while(rs.next()) {

				String securityA = rs.getString("security_answer");
				if(securityA.equals(answer))
				{
					setCheckAnswer(true);
				}
				else {
					setCheckAnswer(false);
				}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}//end of forgetAnswer

	public void viewUsers() {//this method is for admin to display all users

		try {
			Statement stmt= conn.createStatement();
			ResultSet rs=stmt.executeQuery("select * from loginapp where email <> 'admin' ");

			while(rs.next()) {

				String name = rs.getString("name");
				String email = rs.getString("email");
				String status = rs.getString("status");

				System.out.println(name+"\t"+email+"\t"+status);
			}

		}catch(SQLException e) {
			e.printStackTrace();
		}
	}//end of viewUsers

	public void lockUsers(String userLock,String status) {//this method is for admin to lock/unlock users and reset their login attempts 


		String query = "UPDATE loginapp set status="+status+",attempts =0 where email ='"+userLock+"'";
		

		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.executeUpdate();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end of lockUsers

	public static void sendMail(String regName, String regEmail, String regPassword) {//this method is for admin to send temp pass to user's email

		String to = regEmail; //change accordingly  
		String from = "optimum.batch5@gmail.com"; 
		String passwordEmail = "Optimum2018";

		//Get the session object  
		Properties props = System.getProperties();  
		props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
		props.put("mail.smtp.socketFactory.port", "465"); // SSL Port
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
		props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
		props.put("mail.smtp.port", "465"); // SMTP Port

		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, passwordEmail);
			}
		};

		Session session = Session.getDefaultInstance(props, auth); 

		//compose the message  
		try{  

			MimeMessage message = new MimeMessage(session);   
			message.setFrom(new InternetAddress(from));  
			message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
			message.setSubject("Temp message");  
			message.setText("Hello " + regName + "! This is the admin, the following is your temporary password: " + regPassword);  

			// Send message  
			Transport.send(message);  
			System.out.println("Temporary password has been send to " + regEmail + "!");


		}catch (Exception mex) {
			mex.printStackTrace();
		}  

	} // end of sendMail



}
