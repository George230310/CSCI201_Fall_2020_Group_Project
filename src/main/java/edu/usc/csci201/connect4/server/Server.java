package edu.usc.csci201.connect4.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.usc.csci201.connect4.server.ClientHandler.ClientCommand;
import edu.usc.csci201.connect4.server.ClientHandler.LoginCommand;
import edu.usc.csci201.connect4.server.ClientHandler.RegisterCommand;
import edu.usc.csci201.connect4.utils.Log;

public class Server {
	
	// Global Final Variables
	private static final String CREDENTIALS_PATH = "connect4-73290-firebase-adminsdk-tzztu-d4ca2ea0cd.json";
	private static final FirebaseServer fb = new FirebaseServer(CREDENTIALS_PATH);
	public static final int port = 25666;
	
    public static void main(String[] args) {
    	
    	Log.printServer("Server Started ... ");
    	
    	// Thread pool can be expanded to be used for other threads
		ExecutorService pool = Executors.newFixedThreadPool(1);
		ClientManager clientManager = new ClientManager(fb);
		
		
		pool.execute(clientManager);
		pool.shutdown();
		
		
		while(!pool.isTerminated()) { }
		Log.printServer("Server stopped.");

    }
    

}

final class ClientManager implements Runnable {

	private ServerSocket server;
	private ArrayList<ClientReader> clientReaders;
	protected boolean isTerminated = false;
	private final FirebaseServer fb;
	
	public ClientManager(FirebaseServer fb) {
		this.fb = fb;
		clientReaders = new ArrayList<ClientReader>();
		try {
			server = new ServerSocket(Server.port);
			Log.printServer("Listening for connections on " + server.getLocalSocketAddress());
		} catch (IOException e) {
			Log.printServer("Failed to open ServerSocket on port " + Server.port + " because " + e.getMessage());
		}
	}
	
	public void run() {
		while (!isTerminated) {
			try {
				Socket client = server.accept();
				
				// Clean up any clients that have been disconnected
				// If last client '2' was disconnected the next client to connect
				// will take the '2' client ID
				List<ClientReader> found = new ArrayList<ClientReader>();
				for (ClientReader clientReader : clientReaders) {
					if (clientReader.isInterrupted()) {
						found.add(clientReader);
					}
				}
				
				clientReaders.removeAll(found);
				
				ClientReader r = new ClientReader(client, String.valueOf(clientReaders.size()), fb);
				clientReaders.add(r);
			} catch (IOException e) {
				Log.printServer("Problem accepting the incoming client socket. " + e.getMessage());
			} catch (NullPointerException e) {
				Log.printServer("Failed to setup ClientManager, aborting program..." + e.getMessage());
				return;
			}
		}
	}
	
}

final class ClientReader extends Thread {

	private final Socket socket;
	private final String id;
	private ObjectOutputStream os;
	private FirebaseServer fb;
	
	public ClientReader(Socket socket, String id, FirebaseServer fb) {
		this.socket = socket;
		this.fb = fb;
		this.id = id;
		this.start();
	}
	
	public void run() {
		try {
	        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
	        os = new ObjectOutputStream(socket.getOutputStream());
			Log.printServer("Client " + id +  " connected.");
			
			while(!socket.isClosed()) {
		        try {
					parseClientResponse((ClientCommand) is.readObject());
				} catch (ClassNotFoundException e) {
					Log.printServer("Object sent from client was not a ClientCommand");
					break;
				}
			}
			
			Log.printServer("Client " + id + " diconnected");
		} catch (IOException e) {
			if (e.getMessage() == null) Log.printServer("Client " + id + " disconnected.");
			else Log.printServer("ClientReader with id " + id + " failed with error " + e.getLocalizedMessage());
		}
		this.interrupt();
	}
	
	private void parseClientResponse(ClientCommand rawCommand) {
		
		ClientCompletionHandler handler = new ClientCompletionHandler(os, rawCommand);
		
		if (rawCommand.getClass() == RegisterCommand.class) {
			fb.registerUser(((RegisterCommand) rawCommand).getEmail(), ((RegisterCommand) rawCommand).getPassword(), handler, this);
		} else if (rawCommand.getClass() == LoginCommand.class) {
			fb.loginUser(((LoginCommand) rawCommand).getEmail(), ((LoginCommand) rawCommand).getPassword(), handler, this);
		}
	}
	
	public String getID() { return this.id; }
	
}
    