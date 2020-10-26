package edu.usc.csci201.connect4.server;

import edu.usc.csci201.connect4.cli.AuthCLI;

public class Server {
	
	// Global Final Variables
	private final static String CREDENTIALS_PATH = "connect4-73290-firebase-adminsdk-tzztu-d4ca2ea0cd.json";
	
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
        
        FirebaseServer fb = new FirebaseServer (CREDENTIALS_PATH);
        Auth auth = new Auth(fb, new AuthCLI());
        
        // Examples
        auth.registerUser("email@gmail.com", "password123");
        auth.loginUser("email@gmail.com", "password123");
        
    }
    
    
}
