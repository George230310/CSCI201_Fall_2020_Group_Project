/**
* Database.java is the middle-man class used for interfacing 
* with the FirebaseDatabase system in an asynchronous manner
* Use it by calling .getDatabase().method() on an instance 
* of a FirebaseServer (Usually shared by the Server instance)
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.server;

import java.util.concurrent.ExecutionException;

import com.google.firebase.database.DatabaseReference;

import edu.usc.csci201.connect4.events.DatabaseEventCallback.SetValueAsyncEventListener;

public class Database {
	
	private final FirebaseServer fb;
	
	public Database(FirebaseServer fb) {
		this.fb = fb;
	}

	public void setValueAtPathAsync(final String path, final Object value, final SetValueAsyncEventListener listener) {
		
		new Thread(new Runnable() { 
			public void run() { 
				
				DatabaseReference dbr = null;
				String error = "";
				
				// Attempt to register a user using the FirebaseServer class
		        try {
					dbr = fb.setDataAtPathAsync(path, value);
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

} 

