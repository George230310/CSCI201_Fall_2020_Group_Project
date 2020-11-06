package edu.usc.csci201.connect4.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

import edu.usc.csci201.connect4.server.Server;
import edu.usc.csci201.connect4.server.ClientHandler.*;
import edu.usc.csci201.connect4.utils.Log;

public class Client {
	
	private static Scanner scanner;
	private static ObjectOutputStream os;
	private static ObjectInputStream is;
	private static Socket socket;
	private static boolean isTerminated = false;
	
	private final static HashMap<String, String[]> cmds = new HashMap<String, String[]>() {
		private static final long serialVersionUID = 1L;
	{
		put("help", new String[]{"register", "login", "quit", "guest"});
        put("register", new String[]{"{email} {password}"});
        put("login", new String[]{"{email} {password}"});
		put("quit", new String[0]);
		put("guest", new String[0]);        
    }};
	
	public static void main(String[] args) {
		
		scanner = new Scanner(System.in);
		
		try {
			socket = new Socket("localhost", Server.port);
			
			Log.printClient("Connected to Server on port " + Server.port);
			os = new ObjectOutputStream(socket.getOutputStream());
			is = new ObjectInputStream(socket.getInputStream());

		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.printClient("Lost connection to host with message " + e.getMessage());
		} catch (IOException e) {
			Log.printClient("IOException with error " + e.getMessage());
		}

		while (!isTerminated) {
			try {
			int num = scanner.nextInt();
			switch(num) {
			case 1:
				os.writeObject(new GetHighScoresCommand());
				break;
			case 2:
				os.writeObject(new CreateLobbyCommand("Testing"));
				break;
			case 3:
				os.writeObject(new JoinLobbyCommand("Testing"));
				break;
			case 4:
				os.writeObject(new LoginCommand("liuraymo@usc.edu", "password"));
			case 5:
				
			case 6:
			}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			try {
				ClientCommand rawCmd = (ClientCommand)is.readObject();
				System.out.println("Caught a command");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			//if (handleCommand(syncPrompt("> "))) handleResponse();
		}
		
		Log.println("Thanks for playing!");
	}
	
	public static String syncPrompt(String prompt) {
		Log.print(prompt);
		String response = scanner.nextLine();
		return response;
	}
	
	private static void handleResponse() {

		try {
			ClientCommand rawCmd = (ClientCommand) is.readObject();
			if (rawCmd.getResponse() != "") Log.printServer(rawCmd.getResponse());
		} catch (ClassNotFoundException e) {
			Log.printConsole("Failed to parse object from reading object stream. " + e.getMessage());
		} catch (IOException e) {
			Log.printConsole("Failed to read object from the connected socket. " + e.getMessage());
		}

	}
	
	
	private static boolean handleCommand(String cmd) {
		
		String[] args = cmd.split("\\s+");
		ClientCommand command = null;
		
		// Parse the first argument of the user's command
		if (args[0].equals("quit") || args[0].equals("disconnect")) {
			isTerminated = true;
			return false;
		} else if (args[0].equals("help")) {
			runHelpCommand(args);
			return false;
		} else if (args[0].equals("register")) {
			command = new RegisterCommand(args[1], args[2]);
		} else if (args[0].equals("login")) {
			command = new LoginCommand(args[1], args[2]);
		} else {
			Log.println("Unknown command '" + cmd + "'");
		}
		
		// Run the command if it's not null
		if (command != null) {
			try {
				os.writeObject(command);
			} catch (IOException e) {
				e.printStackTrace();
				Log.printClient("Failed to write register command to the server. " + e.getMessage());
			}
			return true;
		} 
		
		return false;
	}
	
	private static void runHelpCommand(String[] args) {
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
			Log.println("**** Welcome to Connect4! ****\n");
			for (String cmd : cmds.keySet()) {
				Log.print(cmd + ", ");
			}
			Log.println("\n****** * * **** * *  *******");
		}
	} 
}
