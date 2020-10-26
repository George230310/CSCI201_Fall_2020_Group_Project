/**
* FirebaseServer.java is the closest class that interfaces with
* the FirebaseApp methods. All methods here are designed to throw
* exceptions if need be and should not be interfaced directly unless
* you are updating and/or implementing new methods in the Auth/Database.java
* classes.
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
			Log.printlnServer("Failed to find file for the Firebase GoogleCredentials");
		}
		
	}
	
	public DatabaseReference setDataAtPathAsync(String path, Object value) 
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
	
	public UserRecord login(String email, String pass) 
			throws FirebaseAuthException, IllegalArgumentException {
	
		UserRecord potentialUser = firebaseAuth.getUserByEmail(email);
		
		// Check if the password is correct
		if (Utils.decrypt(potentialUser.getDisplayName()).equals(pass)) return potentialUser;
		else throw new IllegalArgumentException("Password invalid.");
	
	}
	
	public UserRecord registerUserWith(String email, String pass) 
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

}
