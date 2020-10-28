/**
* AuthCompletionHandler.java is the default handler used for Auth.java related
* events that occur. Changing these methods will effect any non-custom Auth.method() callbacks
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.events;

import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.UserRecord;

import edu.usc.csci201.connect4.events.AuthEventCallback.ListEventListener;
import edu.usc.csci201.connect4.events.AuthEventCallback.LoginEventListener;
import edu.usc.csci201.connect4.events.AuthEventCallback.RegisterEventListener;
import edu.usc.csci201.connect4.utils.Log;

public class AuthCompletionHandler implements LoginEventListener, RegisterEventListener, ListEventListener {
	
	Object sender;
	
	public AuthCompletionHandler(Object sender) {
		this.sender = sender;
	} 
	
	public void onLogin(UserRecord user, Object sender) {
		Log.printServer("Asynchronous callback for onLogin() triggered."); 
		Log.printServer("Logged user UID is " + user.getUid());
		if (sender != null) {
			synchronized (sender) { sender.notify(); }
		}
	} 
	
	public void onLoginFail(String err, Object sender) {
		Log.printServer("Failed to login ... " + err);
		if (sender != null) {
			synchronized (sender) { sender.notify(); }
		}
	}
	
	public void onRegister(UserRecord user, Object sender) { 
		Log.printServer("Successfully created user with UID " + user.getUid());
		synchronized (sender) { sender.notify(); }
	}


	public void onRegisterFail(String err, Object sender) {
		Log.printServer("Failed to create user. " + err);
		synchronized (sender) { sender.notify(); }
	}
	
	public void onList(ExportedUserRecord[] users, Object sender) {
		Log.printServer("Successfully fetched all authenticated users. "); 
		if (sender != null) {
			synchronized (sender) { sender.notify(); }
		}
	} 
	
	public void onListFail(String err, Object sender) {
		Log.printServer("Failed to login ... " + err);
		if (sender != null) {
			synchronized (sender) { sender.notify(); }
		}
	}
	
	
//	public class LoginCompletionHandler implements LoginEventListener {
//		
//		public void onLogin(UserRecord user, Object sender) { (new AuthCompletionHandler(sender)).onLogin(user, sender); }
//		public void onLoginFail(String err, Object sender) { (new AuthCompletionHandler(sender)).onLoginFail(err, sender); }
//
//	} 
//
//	public class RegisterCompletionHandler implements RegisterEventListener {
//		
//		public void onRegister(UserRecord user, Object sender) { (new AuthCompletionHandler(sender)).onRegister(user, sender); }
//		public void onRegisterFail(String err, Object sender) { (new AuthCompletionHandler(sender)).onRegisterFail(err, sender); }
//		
//	}

	
}
