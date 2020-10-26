/**
* AuthEventCallback.java is a base class used for both custom and
* the default callback methods. You can create an instance of this
* with the signature Auth.method(..., AuthEventCallback.*EventListener() { // ... implemented methods });
* -- Or just create an instance of this before calling the Auth method and pass
* it in as the last parameter.
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.events;

import com.google.firebase.auth.UserRecord;

public class AuthEventCallback {
	
	private interface AuthEventListener {}
	
	public interface LoginEventListener extends AuthEventListener { 
		 public void onLogin(UserRecord user);
		 public void onLoginFail(String err);
	} 
	
	public interface RegisterEventListener extends AuthEventListener { 		
		 public void onRegister(UserRecord user);
		 public void onRegisterFail(String err);
	} 
}