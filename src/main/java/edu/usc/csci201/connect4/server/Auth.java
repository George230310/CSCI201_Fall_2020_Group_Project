/**
* Auth.java is the middle-man class used for interfacing 
* with the FirebaseAuthentication system in an asynchronous manner
* Use it by evoking Auth.*() in any class within edu.usc.csci201.connect4
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.server;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import edu.usc.csci201.connect4.listeners.AuthEventListener;

public class Auth {
	
	private final FirebaseServer fb;
	private final AuthEventListener authListener; 
	
	public Auth(FirebaseServer fb, AuthEventListener authListener) {
		this.fb = fb;
		this.authListener = authListener;
	}
	
	public void registerUser(final String email, final String password) {
		
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
					if (errorMessage.isBlank() && authListener != null) authListener.onRegister(user);
					else authListener.onRegisterFail(errorMessage);
				}

			}
			
		}).start(); 
	}
	

} 

