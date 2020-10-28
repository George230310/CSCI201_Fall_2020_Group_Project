/**
* Auth.java is the middle-man class used for interfacing 
* with the FirebaseAuthentication system in an asynchronous manner
* Use it by calling .getAuth().method() on an instance 
* of a FirebaseServer (Usually shared by the Server instance)
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.server;

import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import edu.usc.csci201.connect4.events.AuthEventCallback.ListEventListener;
import edu.usc.csci201.connect4.events.AuthEventCallback.LoginEventListener;
import edu.usc.csci201.connect4.events.AuthEventCallback.RegisterEventListener;


public class Auth {
	
	private final FirebaseServer fb;
	
	public Auth(FirebaseServer fb) {
		this.fb = fb;
	}
	
	public void registerUser(final String email, final String password, final RegisterEventListener listener, final Object sender) {
		
		new Thread(new Runnable() { 
			public void run() { 
				
				UserRecord user = null;
				String errorMessage = "";
				
				// Attempt to register a user using the FirebaseServer class
		        try {
					user = fb.registerUserWith(email, password);
				} catch (FirebaseAuthException e) {
					errorMessage = e.getMessage();
				} catch (IllegalArgumentException e) {
					errorMessage = e.getMessage();
				} finally {
					// Invoke the register callback of listener.onRegister or listener.onRegisterFail
					if (errorMessage.isBlank() && listener != null) listener.onRegister(user, sender);
					else if (listener != null) listener.onRegisterFail(errorMessage, sender);
				}

			}
			
		}).start(); 
	}
	
	public void registerUser(final String email, final String password, final RegisterEventListener listener) {
		registerUser(email, password, null);
	}
	
	public void loginUser(final String email, final String password, final LoginEventListener listener) {
		loginUser(email, password, listener, null);
	}
	
	public void loginUser(final String email, final String password, final LoginEventListener listener, final Object sender) {
		
		new Thread(new Runnable() { 
			public void run() { 
				
				UserRecord user = null;
				String errorMessage = "";
				
				// Attempt to register a user using the FirebaseServer class
		        try {
					user = fb.login(email, password);
				} catch (FirebaseAuthException e) {
					errorMessage = e.getMessage();
				} catch (IllegalArgumentException e) {
					errorMessage = e.getMessage();
				} finally {
					// Invoke the register callback of listener.onLogin or listener.onLoginFail
					if (errorMessage.isBlank() && listener != null) listener.onLogin(user, sender);
					else if (listener != null) listener.onLoginFail(errorMessage, sender);
				}

			}
			
		}).start(); 
	}
	
	public void listUsers(final ListEventListener listener, final Object sender) {
		ExportedUserRecord[] users = null; 
		Iterable<ExportedUserRecord> rawUsers = null;
		String errorMessage = "";
		
		try {
			rawUsers = fb.listUsers();
			for (ExportedUserRecord rawUser : rawUsers) {
				
			}
		} catch (FirebaseAuthException e) {
			errorMessage = e.getMessage();
		} finally {
			// Invoke the list callback of listener.onList or listener.onListFail
			if (errorMessage.isBlank() && listener != null) listener.onList(users, sender);
			else if (listener != null) listener.onListFail(errorMessage, sender);
		}
	}
	
	public void listUsers(final ListEventListener listener) {
		listUsers(listener, null);
	}
	

} 

