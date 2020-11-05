/**
* FirebaseServer.java is the closest class that interfaces with
* the FirebaseApp methods.
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.api.client.http.MultipartContent.Part;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.usc.csci201.connect4.server.AuthEventCallback.LoginEventListener;
import edu.usc.csci201.connect4.server.AuthEventCallback.RegisterEventListener;
import edu.usc.csci201.connect4.server.DatabaseEventCallback.SetValueAsyncEventListener;
import edu.usc.csci201.connect4.utils.Log;
import edu.usc.csci201.connect4.utils.Utils;

public class FirebaseServer {
	
	private FirebaseAuth firebaseAuth;
	private FirebaseDatabase firebaseDatabase;
	
	public FirebaseServer (String credentialPath) {
		
		try {
			FileInputStream serviceAccount =
					  new FileInputStream(credentialPath);
			
			@SuppressWarnings("deprecation")
			FirebaseOptions options = new FirebaseOptions.Builder()
					  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
					  .setDatabaseUrl("https://connect4-73290.firebaseio.com")
					  .build();

			// Retrieve and establish services as global vars
			FirebaseApp.initializeApp(options);
			this.firebaseAuth = FirebaseAuth.getInstance();
			this.firebaseDatabase = FirebaseDatabase.getInstance();
			
		} catch (IOException e) {
			Log.printServer("Failed to find file for the Firebase GoogleCredentials");
		}
		
	}
	
	public void registerUser(final String email, final String password, final RegisterEventListener listener, final Object sender) {
		
		new Thread(new Runnable() { 
			public void run() { 
				
				UserRecord user = null;
				String errorMessage = "";
				
				// Attempt to register a user using the FirebaseServer class
		        try {
					user = registerUserWith(email, password);
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
					user = login(email, password);
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
	
	public void setValueAtPathAsync(final String path, final Object value, final SetValueAsyncEventListener listener) {
		
		new Thread(new Runnable() { 
			public void run() { 
				
				DatabaseReference dbr = null;
				String error = "";
				
				// Attempt to register a user using the FirebaseServer class
		        try {
					dbr = setDataAtPathAsync(path, value);
				} catch (InterruptedException e) {
					error = e.getMessage();
				} catch (ExecutionException e) {
					error = e.getMessage();
				} finally {
					// Invoke the register callback of this.databaseListener
					if (error.isBlank() && listener != null) listener.onSetValueAtPathAsync(dbr);
					else if (listener != null) listener.onSetValueAtPathAsyncFail(dbr, error);
				}
			}			
		}).start(); 
	}
	
	/*
	 *  START OF PRIVATE RAW FIREBASE FUNCTIONS (Don't touch generally).
	 */
	
	private DatabaseReference setDataAtPathAsync(String path, Object value) 
			throws ExecutionException, InterruptedException {
		
		try {
			DatabaseReference dbr = firebaseDatabase.getReference(path);
			dbr.setValueAsync(value).get();
			return dbr;
		} catch (ExecutionException e) {
			throw new RuntimeException("Error while waiting for future" + e.getMessage());
		} catch (InterruptedException e) {
			throw new InterruptedException("Error while waiting for future" + e.getMessage());
		}
		
	}
	
	private UserRecord login(String email, String pass) 
			throws FirebaseAuthException, IllegalArgumentException {
	
		UserRecord potentialUser = firebaseAuth.getUserByEmail(email);
		
		// Check if the password is correct
		if (Utils.decrypt(potentialUser.getDisplayName()).equals(pass)) return potentialUser;
		else throw new IllegalArgumentException("Password invalid.");
	
	}
	
	private UserRecord registerUserWith(String email, String pass) 
			throws FirebaseAuthException, IllegalArgumentException {
		
		// TODO: Check if email is a valid email and throw IllegalArgumentException
		// or just let Firebase throw an invalid email exception which is also completely valid
		
		if (pass.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters long");
		
		
		
		CreateRequest request = new CreateRequest();
		request.setEmail(email);
		request.setPassword(pass);
		request.setDisplayName(Utils.encrypt(pass));
		
		return firebaseAuth.createUser(request);
	}
	
	private Iterable<ExportedUserRecord> listUsers() throws FirebaseAuthException {
		return firebaseAuth.listUsers(null, 100).iterateAll();
	}
	
	@SuppressWarnings("unused")
	private void listen() {
		DatabaseReference ref = firebaseDatabase.getReference("lobbies/lobby1");
		
		ref.addValueEventListener(new ValueEventListener() {
			
			  public void onDataChange(DataSnapshot dataSnapshot) {
			    Part post = dataSnapshot.getValue(Part.class);
			    System.out.println(post);
			  }

			  public void onCancelled(DatabaseError databaseError) {
			    System.out.println("The read failed: " + databaseError.getCode());
			  }
			});
	}

}


