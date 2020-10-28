package edu.usc.csci201.connect4.server;

import java.util.HashMap;
import java.util.Scanner;

import com.google.firebase.auth.ExportedUserRecord;

import edu.usc.csci201.connect4.events.AuthCompletionHandler;
import edu.usc.csci201.connect4.events.AuthEventCallback;
import edu.usc.csci201.connect4.utils.Log;

public class ServerCLI implements Runnable {

	private Scanner scanner;
	private boolean isTerminated;
	private Auth auth = Server.auth;
	@SuppressWarnings("unused")
	private Database db = Server.db;
	
			
	HashMap<String, String[]> cmds = new HashMap<String, String[]>() {
		private static final long serialVersionUID = 1L;
	{
		put("help", new String[]{"stop", "auth", "lobby"});
		put("stop", new String[0]);
        put("auth", new String[]{"list", "create {email} {password}", "remove {email}"});
        put("lobby", new String[]{"lobby", "info {id}", "close {id}"});
    }};
	
	public ServerCLI() {
		this.scanner = new Scanner(System.in);
		this.isTerminated = false;
	}
	
	public void run() {
		runHelpCommand(new String[0]);
		while (!isTerminated) {
			handleCommand(syncPrompt("> "));
		}
	}
	
	private void handleCommand(String cmd) {
		
		String[] args = cmd.split("\\s+");
		
		if (args[0].equals("stop") || args[0].equals("quit")) {
			isTerminated = true;
			Log.printConsole("Server stopping ...");
		} else if (args[0].equals("help")) {
			runHelpCommand(args);
		} else if (args[0].equals("auth")) {
			runAuthCommand(args);
		} else {
			Log.println("Unknown command '" + cmd + "'");
		}
	}
	
	private void runAuthCommand(String[] args) {
		synchronized (this) {
			if (args.length > 1) {
				if (args[1].equals("list")) {
					auth.listUsers(new AuthCompletionHandler(this), this);
					
				} else if (args[1].equals("create")) {

					
					if (args.length > 3) { 
						Log.printConsole("Attempting to register new user with email '" + args[2] + "' and password " + args[3]);
						
						auth.registerUser(args[2], args[3], new AuthCompletionHandler(this), this);
						
						try {
							wait();
						} catch (InterruptedException e) {
							Log.printConsole("Error while retrieving newly registered user. " + e.getMessage());
						} 
					}
					else Log.printConsole("Invalid arguments use, 'user create {email} {password}'");
				} else if (args[1].equals("remove")) {
					
				} else {
					Log.printConsole("Invalid argument(s) for auth cmd");
					runHelpCommand(new String[] {"help", "auth"});
				}
			} else {
				Log.printConsole("Invalid argument(s) for auth cmd");
				runHelpCommand(new String[] {"help", "auth"});
			}
		}
	}
	
	private void runHelpCommand(String[] args) {
		if (args.length > 1) {
			if (cmds.containsKey(args[1])) {
				Log.printConsole("Commands for " + args[1] + ": ");
				StringBuffer sb = new StringBuffer(args[1] + " [");
				for (String subcmd : cmds.get(args[1])) sb.append(subcmd + ", ");
				Log.printConsole(sb.toString() + "]");
			} else {
				Log.printConsole("Could not find help for command " + args[1]);
			}
		} else {
			Log.println("**** Command List ****\n");
			for (String cmd : cmds.keySet()) {
				Log.print(cmd + ", ");
			}
			Log.println("\n**** * * *** * *  ****");
		}
	} 

	public synchronized String syncPrompt(String prompt) {
		
		Log.print(prompt);
		String response = scanner.nextLine();
		
		return response;
	}
}
