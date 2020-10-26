package edu.usc.csci201.connect4.listeners;

import com.google.firebase.auth.UserRecord;

public interface AuthEventListener { 

	 void onLogin(UserRecord user);
	 void onLoginFail(String err);
	 void onRegister(UserRecord user);
	 void onRegisterFail(String err);
} 