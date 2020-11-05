package edu.usc.csci201.connect4.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DatabaseReference;

import edu.usc.csci201.connect4.server.AuthEventCallback.ListEventListener;
import edu.usc.csci201.connect4.server.AuthEventCallback.LoginEventListener;
import edu.usc.csci201.connect4.server.AuthEventCallback.RegisterEventListener;
import edu.usc.csci201.connect4.server.ClientHandler.ClientCommand;
import edu.usc.csci201.connect4.utils.Log;

public class ClientHandler {

	
	public interface ClientCommand {
		public String getResponse();
		public void setResponse(String response);
	}
	
	public static class RegisterCommand implements ClientCommand, Serializable {


		private static final long serialVersionUID = 2141357743627884944L;
		private final String email;
		private final String password;
		private String response;
		
		public RegisterCommand(String email, String password) {
			this.email = email;
			this.password = password;
		}
		
		public void setResponse(String response) { this.response = response; }
		public String getResponse() { return this.response; }
		public String getEmail() { return this.email; }
		public String getPassword() { return this.password; }
	}
	
	public static class LoginCommand implements ClientCommand, Serializable {


		private static final long serialVersionUID = -6609972115866339907L;
		private final String email;
		private final String password;
		private String response;
		
		public LoginCommand(String email, String password) {
			this.email = email;
			this.password = password;
		}
		
		public void setResponse(String response) { this.response = response; }
		public String getResponse() { return this.response; }
		public String getEmail() { return this.email; }
		public String getPassword() { return this.password; }
	}
	
}

class ClientCompletionHandler implements LoginEventListener, RegisterEventListener, ListEventListener {

	final ObjectOutputStream os;
	final ClientCommand cmd;
	
	public ClientCompletionHandler(ObjectOutputStream os, ClientCommand cmd) {
		this.os = os;
		this.cmd = cmd;
	}
	
	public void onLogin(UserRecord user, Object sender) {
		cmd.setResponse("Successfully logged in user with ID " + ((ClientReader) sender).getID() + " and UID " + user.getUid());
		try {
			os.writeObject(cmd);
		} catch (IOException e) {
			Log.printServer("Failed to write register response to client with ID " + ((ClientReader) sender).getID());
		}
		if (sender != null) { synchronized (sender) { sender.notify(); } }
	} 
	
	public void onLoginFail(String err, Object sender) {
		cmd.setResponse("Failed to login with error: " + err);
		try {
			os.writeObject(cmd);
		} catch (IOException e) {
			Log.printServer("Failed to write login failed response to client with ID " + ((ClientReader) sender).getID());
		}
		if (sender != null) { synchronized (sender) { sender.notify(); } }
	}
	
	public void onRegisterFail(String err, Object sender) {			
		cmd.setResponse("Failed to register with error: " + err);
		try {
			os.writeObject(cmd);
		} catch (IOException e) {
			Log.printServer("Failed to write register failed response to client with ID " + ((ClientReader) sender).getID());
		}
		if (sender != null) { synchronized (sender) { sender.notify(); } }
	}
	
	public void onRegister(UserRecord user, Object sender) {
		cmd.setResponse("Successfully registered user with id " + ((ClientReader) sender).getID() + " and UID " + user.getUid());
		try {
			os.writeObject(cmd);
		} catch (IOException e) {
			Log.printServer("Failed to write register response to client with ID " + ((ClientReader) sender).getID());
		}
		if (sender != null) { synchronized (sender) { sender.notify(); } }
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

}


class AuthEventCallback {
	
	public interface AuthEventListener {}
	
	public interface LoginEventListener extends AuthEventListener { 
		 public void onLogin(UserRecord user, Object sender);
		 public void onLoginFail(String err, Object sender);
	} 
	
	public interface RegisterEventListener extends AuthEventListener { 		
		public void onRegister(UserRecord user, Object sender);
		public void onRegisterFail(String err, Object sender);
	} 
	
	public interface ListEventListener extends AuthEventListener {
		public void onList(ExportedUserRecord[] users, Object sender);
		public void onListFail(String err, Object sender);
	}
}

class DatabaseEventCallback {
	
	private interface DatabaseEventListener {}
	
	public interface SetValueAsyncEventListener extends DatabaseEventListener { 
		public void onSetValueAtPathAsync(DatabaseReference dbr);
		public void onSetValueAtPathAsyncFail(DatabaseReference dbr, String error);
	} 
	
}

