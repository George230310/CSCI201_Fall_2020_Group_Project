/**
* AuthCompletionHandler.java is the default handler used for Auth.java related
* events that occur. Changing these methods will effect any non-custom Auth.method() callbacks
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.cli;

import com.google.firebase.auth.UserRecord;

import edu.usc.csci201.connect4.events.AuthEventCallback.LoginEventListener;
import edu.usc.csci201.connect4.events.AuthEventCallback.RegisterEventListener;
import edu.usc.csci201.connect4.utils.Log;

public class AuthCompletionHandler implements LoginEventListener, RegisterEventListener {
	
	public void onLogin(UserRecord user) {
		Log.printlnServer("Asynchronous callback for onLogin() triggered."); 
		Log.printlnServer("Logged user UID is " + user.getUid());
	} 
	
	public void onLoginFail(String err) {
		Log.printlnServer("Failed to login ... " + err);
	}
	
	public void onRegister(UserRecord user) { 
		Log.printlnServer("Asynchronous callback for onRegister() triggered.");
		Log.printlnServer("Registered user UID is " + user.getUid());
	}


	public void onRegisterFail(String err) {
		Log.printlnServer("Failed to register user ... " + err);
	}
	
	
	public class LoginCompletionHandler implements LoginEventListener {
		
		public void onLogin(UserRecord user) { (new AuthCompletionHandler()).onLogin(user); }
		public void onLoginFail(String err) { (new AuthCompletionHandler()).onLoginFail(err); }

	} 

	// Default 
	public class RegisterCompletionHandler implements RegisterEventListener {
		
		public void onRegister(UserRecord user) { (new AuthCompletionHandler()).onRegister(user); }
		public void onRegisterFail(String err) { (new AuthCompletionHandler()).onRegisterFail(err); }
		
	}
	
}
