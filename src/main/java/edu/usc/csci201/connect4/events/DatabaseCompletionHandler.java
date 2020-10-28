/**
* DatabaseCompletionHandler.java is the default handler used for Database.java related
* events that occur. Changing these methods will effect any non-custom Database.method() callbacks
* 
* @author      Mario Figueroa
* @version     %I%, %G%
* @since       1.0
*/

package edu.usc.csci201.connect4.events;

import com.google.firebase.database.DatabaseReference;

import edu.usc.csci201.connect4.events.DatabaseEventCallback.SetValueAsyncEventListener;
import edu.usc.csci201.connect4.utils.Log;

public class DatabaseCompletionHandler implements SetValueAsyncEventListener {

	public void onSetValueAtPathAsync(DatabaseReference dbr) {
		Log.printServer("Asynchronous callback for onSetAsync() triggered."); 
		Log.printServer("DatabaseReference url is " + dbr.toString());
	}

	public void onSetValueAtPathAsyncFail(DatabaseReference dbr, String error) {
		Log.printServer("Asynchronous callback for onSetAsyncFail() triggered."); 
		Log.printServer("DatabaseReference url is " + dbr.toString() + " error message is " + error);
	}
	
	public class SetValueAsyncCompletionHandler implements SetValueAsyncEventListener { 
		public void onSetValueAtPathAsync(DatabaseReference dbr) { (new DatabaseCompletionHandler()).onSetValueAtPathAsync(dbr); }
		public void onSetValueAtPathAsyncFail(DatabaseReference dbr, String error) { (new DatabaseCompletionHandler()).onSetValueAtPathAsyncFail(dbr, error); }
	} 
	
}


