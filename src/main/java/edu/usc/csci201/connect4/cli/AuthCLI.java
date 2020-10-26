package edu.usc.csci201.connect4.cli;

import com.google.firebase.auth.UserRecord;

import edu.usc.csci201.connect4.listeners.AuthEventListener;
import edu.usc.csci201.connect4.utils.Log;

public class AuthCLI implements AuthEventListener {
		
	public void onLogin(UserRecord user) {
		Log.printlnServer("Asynchronous callback for onLogin() triggered."); 
		Log.printlnServer("Logged user UID is " + user.getUid());
	} 
	
	public void onRegister(UserRecord user) { 
		Log.printlnServer("Asynchronous callback for onRegister() triggered.");
		Log.printlnServer("Registered user UID is " + user.getUid());
	}

	public void onLoginFail(String err) {
		Log.printlnServer("Failed to login ... " + err);
	}

	public void onRegisterFail(String err) {
		Log.printlnServer("Failed to register user ... " + err);
	} 
}
