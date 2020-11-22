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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.EmailIdentifier;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GetUsersResult;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.PhoneIdentifier;
import com.google.firebase.auth.ProviderIdentifier;
import com.google.firebase.auth.UidIdentifier;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.usc.csci201.connect4.server.AuthEventCallback.LoginEventListener;
import edu.usc.csci201.connect4.server.AuthEventCallback.RegisterEventListener;
import edu.usc.csci201.connect4.server.DatabaseEventCallback.GetHighscoresEventListener;
import edu.usc.csci201.connect4.server.DatabaseEventCallback.SetValueAsyncEventListener;
import edu.usc.csci201.connect4.utils.Log;
import edu.usc.csci201.connect4.utils.Utils;

public class FirebaseServer {
	
	private FirebaseAuth firebaseAuth;
	private FirebaseDatabase firebaseDatabase;
	private final String HIGHSCORE_PATH = "highscores/";
	
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
					if(errorMessage == null) {
						errorMessage = "Unable to register the user, make sure you provided a valid email address and password";
					}
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
	
	public void incrementHighscore(String uid) {
		if (uid == null) return;
		
		final DatabaseReference dbr = firebaseDatabase.getReference(HIGHSCORE_PATH + uid);
		// Attach a listener to read the data at our highscores reference
		ValueEventListener listener = new ValueEventListener() {
			  
			  public void onDataChange(DataSnapshot dataSnapshot) {
				Object val = dataSnapshot.getValue();
			    if (val == null) {
			    	dbr.setValueAsync(1);
			    } else if (val.getClass() == Long.class) {
			    	Long newVal = ((Long) val)+1;
			    	dbr.setValueAsync(newVal);
			    } else {
			    	Log.printServer("[FATAL] Somehow we pushed a non-number to the highscore's database. Manually remove it or figure why this happened. typeof is " + val.getClass());
			    }
			  }

			  public void onCancelled(DatabaseError databaseError) {
			    System.out.println("The read failed: " + databaseError.getCode());
			  }
			};
		
		dbr.addListenerForSingleValueEvent(listener);
	}
	
	public void getHighscore(final String uid, final SetValueAsyncEventListener listener) {
		
		final DatabaseReference dbr = firebaseDatabase.getReference(HIGHSCORE_PATH + uid);
		// Attach a listener to read the data at our highscores reference
		dbr.addListenerForSingleValueEvent(new ValueEventListener() {
			  
			  public void onDataChange(DataSnapshot dataSnapshot) {
				Object val = dataSnapshot.getValue();
			    if (val == null) {
			    	listener.onGetValueAtPathAsync(0);
			    } else if (val.getClass() == Long.class) {
			    	listener.onGetValueAtPathAsync(val);
			    } else {
			    	Log.printServer("[FATAL] Somehow we pushed a non-number to the highscore's database. Manually remove it or figure why this happened. typeof is " + val.getClass());
			    }
			  }

			  public void onCancelled(DatabaseError databaseError) {
			    Log.printServer("Failed to get score for player with UID " + uid);
			    listener.onGetValueAtPathAsyncFail(dbr, databaseError);
			  }
			});
	}
	
	public void getHighscores(final GetHighscoresEventListener listener) {
		
		final DatabaseReference dbr = firebaseDatabase.getReference(HIGHSCORE_PATH);
		
		
		try {
			
			ListUsersPage page = firebaseAuth.listUsers(null);
			final Map<String, String> masterUsers = new HashMap<String, String>();
			while (page != null) {
			  for (ExportedUserRecord user : page.getValues()) {
			    masterUsers.put(user.getUid(), user.getEmail());
			  }
			  page = page.getNextPage();
			}
			
			// Attach a listener to read the data at our highscores reference
			dbr.addListenerForSingleValueEvent(new ValueEventListener() {
				  
				@SuppressWarnings("unchecked")
				public void onDataChange(DataSnapshot dataSnapshot) {
					Object val = dataSnapshot.getValue();
				    if (val == null) {
				    	listener.onGetHighscores("No highscores available.");
				    } else if (val.getClass() == HashMap.class) {
				    	StringBuffer sb = new StringBuffer("*** Highscores ***\n");
				    	for (Entry<String, Long> entry : ((HashMap<String, Long>) val).entrySet()) {
				    		if (masterUsers.containsKey(entry.getKey())) { 
				    			sb.append(masterUsers.get(entry.getKey()) + " - " + entry.getValue() +"\n"); 
				    		}
				    	}
				    	listener.onGetHighscores(sb.toString());

				    } else {
				    	listener.onGetHighscoresFail("Could not read highscores. Database fatal error. Highscore type is " + val.getClass() + " when it should be HashMap");
				    }
				  }

				  public void onCancelled(DatabaseError databaseError) {
				    Log.printServer("Failed to get score for players");
				    listener.onGetHighscoresFail(databaseError.getMessage());
				  }
				});
		} catch (FirebaseAuthException e) {
			listener.onGetHighscoresFail("Failed to get highscores. " + e.getMessage());
		}


		

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

}


