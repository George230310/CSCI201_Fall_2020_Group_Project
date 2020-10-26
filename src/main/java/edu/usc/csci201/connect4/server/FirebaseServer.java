package edu.usc.csci201.connect4.server;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseServer {
	
	private FirebaseApp firebaseApp;
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
			this.firebaseApp = FirebaseApp.initializeApp(options);
			this.firebaseAuth = FirebaseAuth.getInstance();
			this.firebaseDatabase = FirebaseDatabase.getInstance();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public UserRecord login(String email, String pass) throws FirebaseAuthException {
		
		UserRecord potentialUser = firebaseAuth.getUserByEmail(email);
		
		
		return potentialUser;
	}
	
	public UserRecord registerUserWith(String email, String pass) 
			throws FirebaseAuthException, IllegalArgumentException {
		
		// TODO: Implement a check if the email already exists
		// TODO: Check if email is a valid email
		
		if (pass.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters long");
		
		CreateRequest request = new CreateRequest();
		request.setEmail(email);
		request.setPassword(pass);
		
		return firebaseAuth.createUser(request);
	}
	
	public synchronized UserRecord registerUserWith(String email, String pass, String username) 
			throws FirebaseAuthException, IllegalArgumentException {
		
		// TODO: Implement a check if the email already exists
		// TODO: Check if email is a valid email
		
		if (pass.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters long");
		
		CreateRequest request = new CreateRequest();
		request.setEmail(email);
		request.setDisplayName(username);
		request.setPassword(pass);
		
		return firebaseAuth.createUser(request);
	}
	

}
