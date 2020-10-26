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

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import edu.usc.csci201.connect4.events.AuthEventCallback.LoginEventListener;
import edu.usc.csci201.connect4.events.AuthEventCallback.RegisterEventListener;


public class Auth {
	
	private final FirebaseServer fb; 
	
	public Auth(FirebaseServer fb) {
		this.fb = fb;
	}
	
	public void registerUser(final String email, final String password, final RegisterEventListener listener) {
		
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
					// Invoke the register callback of this.authListener 
					if (errorMessage.isBlank() && listener != null) listener.onRegister(user);
					else if (listener != null) listener.onRegisterFail(errorMessage);
				}

			}
			
		}).start(); 
	}
	
	public void loginUser(final String email, final String password, final LoginEventListener listener) {
		
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
					// Invoke the register callback of this.authListener 
					if (errorMessage.isBlank() && listener != null) listener.onLogin(user);
					else if (listener != null) listener.onLoginFail(errorMessage);
				}

			}
			
		}).start(); 
	}
	

} 

