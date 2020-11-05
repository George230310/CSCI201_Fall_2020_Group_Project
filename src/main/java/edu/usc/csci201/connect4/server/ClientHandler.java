package edu.usc.csci201.connect4.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DatabaseReference;

import edu.usc.csci201.connect4.server.AuthEventCallback.LoginEventListener;
import edu.usc.csci201.connect4.server.AuthEventCallback.RegisterEventListener;
import edu.usc.csci201.connect4.server.ClientHandler.ClientCommand;
import edu.usc.csci201.connect4.utils.Log;

public class ClientHandler {

	
	public interface ClientCommand {
		public String getResponse();
		public void setResponse(String response);
		public void setSuccessful();
	}
	
	public static class RegisterCommand implements ClientCommand, Serializable {


		private static final long serialVersionUID = 2141357743627884944L;
		private final String email;
		private final String password;
		private String response;
		private Boolean isSuccessful;
		
		public RegisterCommand(String email, String password) {
			this.email = email;
			this.password = password;
			this.isSuccessful = false;
		}
		
		public void setResponse(String response) { this.response = response; }
		public void setSuccessful()  { this.isSuccessful = true; }
		public String getResponse() { return this.response; }
		public String getEmail() { return this.email; }
		public String getPassword() { return this.password; }
		public Boolean isSuccessful() { return isSuccessful; }
	}
	
	public static class LoginCommand implements ClientCommand, Serializable {


		private static final long serialVersionUID = -6609972115866339907L;
		private final String email;
		private final String password;
		private String response;
		private Boolean isSuccessful;
		
		public LoginCommand(String email, String password) {
			this.email = email;
			this.password = password;
			this.isSuccessful = false;
		}
		
		public void setResponse(String response) { this.response = response; }
		public void setSuccessful()  { this.isSuccessful = true; }
		public String getResponse() { return this.response; }
		public String getEmail() { return this.email; }
		public String getPassword() { return this.password; }
		public Boolean isSuccessful() { return isSuccessful; }
	}
	
	public static class GetHighScoresCommand implements ClientCommand, Serializable {

		private static final long serialVersionUID = 2360988652046736774L;
		private String response;
		
		public GetHighScoresCommand() {}
		
		public void setResponse(String response) { this.response = response; }
		public String getResponse() { return this.response; }
		public void setSuccessful() {}
	}
	
	public static class CreateLobbyCommand implements ClientCommand, Serializable {

		private static final long serialVersionUID = -1130979628160107144L;
		private String lobbyName;
		private String response;
		private Boolean isSuccessful;
		
		public CreateLobbyCommand(String lobby) {
			this.lobbyName = lobby;
			this.isSuccessful = false;
		}
		
		public void setResponse(String response) { this.response = response; }
		public void setSuccessful()  { this.isSuccessful = true; }
		public String getResponse() { return this.response; }
		public String getLobby() { return this.lobbyName; }
		public Boolean isSuccessful() { return isSuccessful; }
	}
	
	public static class JoinLobbyCommand implements ClientCommand, Serializable {

		private static final long serialVersionUID = -4863303503934195L;
		private String lobbyName;
		private String response;
		private Boolean isSuccessful;
		
		public JoinLobbyCommand(String lobby) {
			this.lobbyName = lobby;
			this.isSuccessful = false;
		}
		
		public void setResponse(String response) { this.response = response; }
		public void setSuccessful()  { this.isSuccessful = true; }
		public String getResponse() { return this.response; }
		public String getLobby() { return this.lobbyName; }
		public Boolean isSuccessful() { return isSuccessful; }
	}
	
	public static class StartGameCommand implements ClientCommand, Serializable {

		private static final long serialVersionUID = -6631300258431935054L;
		private String response;
		private Boolean player1; 
		
		public StartGameCommand(Boolean player1) {
			this.player1 = player1;
		};
		
		public void setResponse(String response) { this.response = response; }
		public String getResponse() { return this.response; }
		public Boolean isPlayer1() { return this.player1; }
		public void setSuccessful() {}
	}
	
	public static class GameCommand implements ClientCommand, Serializable {

		private static final long serialVersionUID = 1456982245554602250L;
		private int colNum;
		private String response;
		private Boolean isSuccessful;
		private int responseCol;
		private Boolean gameOver;
		private Boolean player1Wins;
		
		public GameCommand(int col) {
			this.colNum = col;
			this.isSuccessful = false;
		}
		
		public void setResponse(String response) { this.response = response; }
		public void setSuccessful()  { this.isSuccessful = true; }
		public void setResponseCol(int col) {this.responseCol = col; this.gameOver = false;}
		public void setGameOver(Boolean player1Wins) {this.player1Wins = player1Wins; gameOver = true;}
		public int getMove() { return this.colNum; }
		public String getResponse() { return this.response; }
		public int getReturnMove() { return this.responseCol; }
		public Boolean isGameOver() { return this.gameOver; }
		public Boolean isPlayer1Win() { return this.player1Wins; }
		public Boolean isSuccessful() { return isSuccessful; }
	}
}

class ClientCompletionHandler implements LoginEventListener, RegisterEventListener {

	final ObjectOutputStream os;
	final ClientCommand cmd;
	
	public ClientCompletionHandler(ObjectOutputStream os, ClientCommand cmd) {
		this.os = os;
		this.cmd = cmd;
	}
	
	public void onLogin(UserRecord user, Object sender) {
		((ClientReader) sender).setID(user.getUid());
		cmd.setResponse("Successfully logged in user with ID " + ((ClientReader) sender).getID() + " and UID " + user.getUid());
		cmd.setSuccessful();
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
		((ClientReader) sender).setID(user.getUid());
		cmd.setResponse("Successfully registered user with id " + ((ClientReader) sender).getID() + " and UID " + user.getUid());
		cmd.setSuccessful();
		try {
			os.writeObject(cmd);
		} catch (IOException e) {
			Log.printServer("Failed to write register response to client with ID " + ((ClientReader) sender).getID());
		}
		if (sender != null) { synchronized (sender) { sender.notify(); } }
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
}

class DatabaseEventCallback {
	
	private interface DatabaseEventListener {}
	
	public interface SetValueAsyncEventListener extends DatabaseEventListener { 
		public void onSetValueAtPathAsync(DatabaseReference dbr);
		public void onSetValueAtPathAsyncFail(DatabaseReference dbr, String error);
		public void onGetValueAtPathAsync(DatabaseReference dbr);
		public void onGetValueAtPathAsyncFail(DatabaseReference dbr, String error);
	} 
	
}

