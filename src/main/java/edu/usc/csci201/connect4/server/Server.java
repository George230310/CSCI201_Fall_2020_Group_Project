package edu.usc.csci201.connect4.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DatabaseReference;

import edu.usc.csci201.connect4.events.AuthCompletionHandler;
import edu.usc.csci201.connect4.events.AuthEventCallback;
import edu.usc.csci201.connect4.events.DatabaseCompletionHandler;
import edu.usc.csci201.connect4.events.DatabaseEventCallback;
import edu.usc.csci201.connect4.utils.Log;

public class Server {
	
	// Global Final Variables
	private static final String CREDENTIALS_PATH = "connect4-73290-firebase-adminsdk-tzztu-d4ca2ea0cd.json";
	private static final FirebaseServer fb = new FirebaseServer(CREDENTIALS_PATH);
	protected static final Auth auth = new Auth(fb);
	protected static final Database db = new Database(fb);
	
    public static void main(String[] args) {
    	
    	// Create 3 thread for cli, database, and network
		ExecutorService pool = Executors.newFixedThreadPool(3);
		
		pool.execute(new ServerCLI());
		pool.shutdown();
		
		while(!pool.isTerminated()) { }
		Log.printServer("Server stopped.");
    }
    
    public void test() {
        System.out.println("Hello World!");
        

        // Example for logging/registering with pre-defined completion handler
        // auth.registerUser("email@gmail.com", "password123", authHandler);
        AuthCompletionHandler authHandler = new AuthCompletionHandler(this); 
        
        auth.loginUser("email@gmail.com", "password123", authHandler);
        auth.loginUser("test@gmail.com", "password123", new AuthEventCallback.LoginEventListener() {
			
        	public void onLoginFail(String err, Object sender) {
				// Custom onLoginFail callback function
        		Log.printServer("Unsuccessfully logged in asynchronously with a custom callback!");
			}
			
			public void onLogin(UserRecord user, Object sender) {
				// Custom onLogin callback function
				Log.printServer("Successfully logged in asynchronously with a custom callback!");
			}
		});
       
        
        // Example for setting values with pre-defined completion handler (dbHandler)
        // And a custom callback handler at the bottom
        DatabaseCompletionHandler dbHandler = new DatabaseCompletionHandler();
        
        Map<String, Integer> record = new HashMap<String, Integer>();
        Map<String, Map<String, Integer>> highscores = new HashMap<String, Map<String, Integer>>();
        
        record.put("wins", 3);
        record.put("losses", 1);
        highscores.put("USER_UID", record);
        
        db.setValueAtPathAsync("highscores", highscores, dbHandler);
        
        db.setValueAtPathAsync("highscores", highscores, new DatabaseEventCallback.SetValueAsyncEventListener() {
			
			public void onSetValueAtPathAsyncFail(DatabaseReference dbr, String error) {
				// Custom onSetValueAtPathAsyncFail callback function
				Log.printServer("Unsuccessfully set value at path asynchronously with a custom callback!");
			}
			
			public void onSetValueAtPathAsync(DatabaseReference dbr) {
				// Custom onSetValueAtPathAsyncFail callback function
				Log.printServer("Successfully set value at path asynchronously with a custom callback!");
			}
		});
    }
    
}