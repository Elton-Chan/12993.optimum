package com.optimum.controller;

import java.util.Scanner;

import com.optimum.dao.LoginDao;
import com.optimum.pojo.Pojo;

public class Controller extends LoginDao {

	Pojo refPojo = new Pojo();
	Scanner sc = new Scanner(System.in);
	LoginDao dao = new LoginDao();

	public static String name;
	public static String nric;
	public static String email;
	public static String dob;
	public static Integer mobile;
	public static String password;

	public void securityMenu() {//Menu for display first login

		String sq1 = "1.Favourite colour";
		String sq2 = "2.Favourite drink";
		String sq3 = "3.Favourite day of the week";
		
		System.out.println(sq1);
		System.out.println(sq2);
		System.out.println(sq3);
		
		System.out.println("Choice: ");
		String securityQ = sc.nextLine();
		
		System.out.println("Answer: ");
		String securityC = sc.nextLine();

		if(securityQ.equals("1")) {//sets question to 1
			dao.insertSecurity(Controller.password,sq1, securityC,refPojo.getLoginID());
		}
		else if(securityQ.equals("2")) {//sets question to 2
			dao.insertSecurity(Controller.password,sq2, securityC,refPojo.getLoginID());
		}
		else{//sets question to 3
			dao.insertSecurity(Controller.password,sq3, securityC,refPojo.getLoginID());
		}
		System.out.println("Please Login again\n");
		menuChoice("1");
	}//end of securityMenu

	public void pass() {//checks if new pass and retype pass is same

		System.out.println("New Pass:");
		String newpass = sc.nextLine();
		System.out.println("Retype:");
		String retypepass = sc.nextLine();
		if(newpass.equals(retypepass)) {
			Controller.password = newpass;
		}
		else {
			System.out.println("Please enter Same password");
			pass();
		}
	}//end of pass
	
	public void menu() {//print out main menu
		System.out.println("1.Login");
		System.out.println("2.Forget Password");
		System.out.println("Enter Choice: ");
		String choice = sc.nextLine();
		menuChoice(choice);
	}//end menu

	public void adminMenu() {//show admin's menu
		
		System.out.println("1.Register New User: ");
		System.out.println("2.View Users: ");
		System.out.println("3.Log Out");
		String adminC = sc.nextLine();
		
		if(adminC.equals("1")) {//call name() to register user
			name();						
		}
		else if(adminC.equals("2")) {//show lists of users to admin
			dao.viewUsers();
			System.out.println("1.Change user status");
			System.out.println("2.Back");
			String delete = sc.nextLine();
			
			if(delete.equals("1")) {//set user to lock/unlock
				System.out.println("Type in LoginID of user to lock: ");
				String userLock = sc.nextLine();
				System.out.println("Type 1 to unlock and 0 to lock");
				String status = sc.nextLine();
				dao.lockUsers(userLock,status);
				adminMenu();
			}
			else if(delete.equals("2")){//log out admin
				System.out.println("Going back to first Page");
				adminMenu();
			}
			else {
				System.out.println("Error");
				menu();
			}
		}
		else if(adminC.equals("3")) {//log out admin
			System.out.println("You've Logged Out");
			menu();
		}
		else {
			System.out.println("Error");
		}
	}//end of adminMenu

	public void loginMenu() {//print out login menu after main menu

		System.out.println("Enter LoginID: ");
		String loginid = sc.nextLine();
		refPojo.setLoginID(loginid);

		System.out.println("Enter Password: ");
		String password = sc.nextLine();
		refPojo.setPassword(password);

		dao.loginAuthentication(refPojo.getLoginID(),refPojo.getPassword());
	}//end of loginMenu

	public void password(String login) {//set password if LoginDao detects wrong		
		String password = sc.nextLine();
		refPojo.setPassword(password);
		refPojo.setLoginID(login);
		dao.loginAuthentication(refPojo.getLoginID(),refPojo.getPassword());
	}//end of password

	public void name() {//regex to check if name is correct format
		String regexN = "^[a-zA-Z ]+$";

		System.out.println("Enter Name: ");

		String name = sc.nextLine();
		if(name.matches(regexN)) {				
			Controller.name = name;
			nric();
		}
		else {
			System.out.println("Please Enter only Characters");
			name();
		}
	}//end of name

	public void nric() {//regex to check if nric is correct format
		String regexNr = "^[a-zA-Z]{1}[0-9]{7}[a-zA-Z]$";

		System.out.println("Enter NRIC: ");

		String nric = sc.nextLine();

		if(nric.matches(regexNr)) {
			Controller.nric = nric;
			dob();				
		}
		else {
			System.out.println("Please Enter S1234567F format");
			nric();
		}
	}//end nric

	public void dob() {//regex to check if dob is correct format
		String regexD = "^([0-2][0-9]||3[0-1])/(0[0-9]||1[0-2])/([0-9][0-9])?[0-9][0-9]$";

		System.out.println("Enter Date Of Birth: ");

		String dob = sc.nextLine();
		if(dob.matches(regexD)) {
			Controller.dob = dob;
			email();				
		}
		else {
			System.out.println("Please Enter DD/MM/YYYY Format");
			dob();
		}
	}//end of dob

	public void email() {//regex to check if email is correct format
		String regexE =  "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		System.out.println("Enter Email  : ");

		String email = sc.nextLine();
		if(email.matches(regexE)) {
			Controller.email = email;
			mobile();				 
		}
		else {
			System.out.println("Please Enter Email Format");
			email();
		}
	}//end of email

	public void mobile() {//regex to check if mobile is correct format
		String regexM = "^[0-9]{8}$";

		System.out.println("Enter Mobile: ");

		String mobile = sc.nextLine();

		if(mobile.matches(regexM)) {//inserts into database if all formats are correct

			Controller.mobile = Integer.parseInt(mobile);

			dao.insertRegistration(name, nric, dob,email,Controller.mobile);
			System.out.println("Registration Success!");
			adminMenu();

		}
		else {
			System.out.println("Please Enter 00000000 format");
			mobile();
		}
	}//end of mobile
	public void menuChoice(String choice) {//sets main menu choices to start the app
		if(choice.equals("1")) {				
			loginMenu();				
			if(isCheckLock()==true) {//if account is not locked then continue
				if(isStatus()==true && refPojo.getLoginID().equals("admin")) {//if the account is admin's then go to admin menu

					System.out.println("Welcome! " + refPojo.getLoginID() );
					adminMenu();

				}


				else if(isStatus()==true && refPojo.getLoginID() != "admin") {//else go to normal user menu
					if(isFirstlogin()==false) {//checks if login is first or not
						System.out.println("Welcome! " + refPojo.getLoginID() );
						System.out.println("Log Out Y/N: ");
						String logout = sc.nextLine();

						while(logout.equals("N")|logout.equals("n")) {
							System.out.println("Welcome!" + refPojo.getLoginID() );
							System.out.println("Log Out Y/N: ");
							logout = sc.nextLine();		
						}
						System.out.println("You've Logged Out");
						menu();

					}
					else {//if first login then ask for security question/answer
						System.out.println("Welcome! " + refPojo.getLoginID() );
						System.out.println("Temp pass:");
						String temppass = sc.nextLine();
						pass();
						dao.checkPass(temppass,refPojo.getLoginID());
						if(isCheckPassword()==true) {

							securityMenu();

						}
						else {
							menu();
						}
					}
				}
				else {
					menu();
				}
			}
			else {
				System.out.println("Account not found/locked");
				menu();
			}
		}//end of If choice.equals "1"
		else if(choice.equals("2")){
			System.out.println("Login ID: ");

			String loginid = sc.nextLine();

			refPojo.setLoginID(loginid);

			dao.forgetSecurity(refPojo.getLoginID());

			System.out.println("Answer: ");
			String answer = sc.nextLine();
			dao.forgetAnswer(refPojo.getLoginID(), answer);
			System.out.println(refPojo.getLoginID()+" "+answer);
			if(isCheckAnswer()==true) {
				System.out.println("Correct Answer!");
				pass();
				securityMenu();
			}
			else {
				menu();
			}
		}//end of else If choice.equals "2"

	}//end of menuChoice
}


