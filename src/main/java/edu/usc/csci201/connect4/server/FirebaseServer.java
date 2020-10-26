package edu.usc.csci201.connect4.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.FirebaseDatabase;

import edu.usc.csci201.connect4.utils.Log;

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
	
	public UserRecord login(String email, String pass) throws FirebaseAuthException, IllegalArgumentException {
	
		UserRecord potentialUser = firebaseAuth.getUserByEmail(email);
		
		// Check if the password is correct
		if (decrypt(potentialUser.getDisplayName()).equals(pass)) return potentialUser;
		else throw new IllegalArgumentException("Password invalid.");
	
	}
	
	public UserRecord registerUserWith(String email, String pass) 
			throws FirebaseAuthException, IllegalArgumentException {
		
		// TODO: Implement a check if the email already exists
		// TODO: Check if email is a valid email
		
		if (pass.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters long");
		
		CreateRequest request = new CreateRequest();
		request.setEmail(email);
		request.setPassword(pass);
		request.setDisplayName(encrypt(pass));
		
		return firebaseAuth.createUser(request);
	}
	
	public String encrypt(String plain) {
	   String b64encoded = Base64.getEncoder().encodeToString(plain.getBytes());

	   // Reverse the string
	   String reverse = new StringBuffer(b64encoded).reverse().toString();

	   StringBuilder tmp = new StringBuilder();
	   final int OFFSET = 4;
	   for (int i = 0; i < reverse.length(); i++) {
	      tmp.append((char)(reverse.charAt(i) + OFFSET));
	   }
	   return tmp.toString();
	}
	
	public String decrypt(String secret) {
	   StringBuilder tmp = new StringBuilder();
	   final int OFFSET = 4;
	   for (int i = 0; i < secret.length(); i++) {
	      tmp.append((char)(secret.charAt(i) - OFFSET));
	   }

	   String reversed = new StringBuffer(tmp.toString()).reverse().toString();
	   return new String(Base64.getDecoder().decode(reversed));
	}

}
