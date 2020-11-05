package edu.usc.csci201.connect4.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

import edu.usc.csci201.connect4.server.Server;
import edu.usc.csci201.connect4.server.ClientHandler.ClientCommand;
import edu.usc.csci201.connect4.server.ClientHandler.LoginCommand;
import edu.usc.csci201.connect4.server.ClientHandler.RegisterCommand;
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
			
			//more guidance on user interface
			System.out.println("Enter 'help' for the list of avalaible commands");
			System.out.println("Enter 'help command' for detailed instructions");
			System.out.println("For example, 'help login'");
			
			os = new ObjectOutputStream(socket.getOutputStream());
			is = new ObjectInputStream(socket.getInputStream());

		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.printClient("Lost connection to host with message " + e.getMessage());
		} catch (IOException e) {
			Log.printClient("IOException with error " + e.getMessage());
		}

		while (!isTerminated) {
			if (handleCommand(syncPrompt("> "))) handleResponse();
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
			
			//handle the create lobby response
			if(rawCmd.getClass() == CreateLobbyCommand.class)
			{
				if(((CreateLobbyCommand)rawCmd).isSuccessful())
				{
					System.out.println("Waiting for another player to join...");
				}
			}
			
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
		} else if(args[0].equals("guest")) {
			//continue to interact as a guest
			System.out.println("You will continue as guest:");
			System.out.println("1)Create a new game\n2)Join an existing game");
			
			//decide to create a new game or join an existing one
			//also do error checking for input
			boolean input_fails = true;
			int option = 0;
			while(input_fails)
			{
				try
				{
					String line = scanner.nextLine();
					option = Integer.parseInt(line);
					
					if(option <= 0 || option > 2)
					{
						throw new RuntimeException();
					}
					
					input_fails = false;
				}
				catch(NumberFormatException ne)
				{
					System.out.println("Cannot parse and interger, enter a new option: ");
				}
				catch(RuntimeException re)
				{
					System.out.println("No such option, enter a new option: ");
				}
			}
			
			//send create lobby command
			if(option == 1)
			{
				System.out.print("Enter a new lobby name: ");
				String lobbyName = scanner.nextLine();
				command = new CreateLobbyCommand(lobbyName);
			}
			//send join lobby command
			else
			{
				System.out.print("Enter the lobby name to join: ");
				String lobbyName = scanner.nextLine();
				command = new JoinLobbyCommand(lobbyName);
			}
			
		}
		  else {
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
