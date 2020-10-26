/**
* DatabaseEventCallback.java is a base class used for both custom and
* the default callback methods. You can create an instance of this
* with the signature Database.method(..., AuthEventCallback.*EventListener() { // ... implemented methods });
* -- Or just create an instance of this before calling the Database method and pass
* it in as the last parameter.
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.events;

import com.google.firebase.database.DatabaseReference;

public class DatabaseEventCallback {
	
	private interface DatabaseEventListener {}
	
	public interface SetValueAsyncEventListener extends DatabaseEventListener { 
		public void onSetValueAtPathAsync(DatabaseReference dbr);
		public void onSetValueAtPathAsyncFail(DatabaseReference dbr, String error);
	} 
	
}
