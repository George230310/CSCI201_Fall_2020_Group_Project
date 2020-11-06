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
import edu.usc.csci201.connect4.utils.Log;

public class Client
{

	// Commands available during bootup (such as registering) and when you're in the game
	private static Scanner scanner;
	private static ObjectOutputStream os;
	private static ObjectInputStream is;
	private static Socket socket;
	private static boolean isTerminated = false;

	// Create a HashMap for:
	// When a user is not logged in
	// When a user is logged in, but not in a game
	// When a user is logged in and in a game
	
	//This HashMap is for unregistered users
	private final static HashMap<String, String[]> cmds = new HashMap<String, String[]>()
	{
		private static final long serialVersionUID = 1L;
		{
			put("help", new String[]
			{
					"register", "login", "quit", "guest"
			});
			put("register", new String[]
			{
					"{email} {password}"
			});
			put("login", new String[]
			{
					"{email} {password}"
			});
			put("quit", new String[0]);
			put("guest", new String[0]);
		}
	};

	public static void main(String[] args)
	{

		scanner = new Scanner(System.in);

		try
		{
			socket = new Socket("localhost", Server.port);

			Log.printClient("Connected to Server on port " + Server.port);
			os = new ObjectOutputStream(socket.getOutputStream());
			is = new ObjectInputStream(socket.getInputStream());

		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
			Log.printClient("Lost connection to host with message " + e.getMessage());
		}
		catch (IOException e)
		{
			Log.printClient("IOException with error " + e.getMessage());
		}

		while (!isTerminated)
		{
			//Keeps taking commands until 
			if (handleCommand(syncPrompt("> ")))
				handleResponse();
		}

		Log.println("Thanks for playing!");
	}
	
	//Takes the input from user and returns it as a command
	public static String syncPrompt(String prompt)
	{
		Log.print(prompt);
		String response = scanner.nextLine();
		return response;
	}

	private static void handleResponse()
	{

		try
		{
			//ClientCommand is an interface which gets/sets responses
			ClientCommand rawCmd = (ClientCommand) is.readObject();
			if (rawCmd.getResponse() != "")
				Log.printServer(rawCmd.getResponse());
		}
		catch (ClassNotFoundException e)
		{
			Log.printConsole("Failed to parse object from reading object stream. "
					+ e.getMessage());
		}
		catch (IOException e)
		{
			Log.printConsole(
					"Failed to read object from the connected socket. " + e.getMessage());
		}

	}

	//Takes the command from syncPrompt returns true if the right command is found
	//so that the request can be processed by handleResponse or returns false 
	//if it is a help request or the user quits
	//A false return will prompt the user for input again.
	private static boolean handleCommand(String cmd)
	{

		String[] args = cmd.split("\\s+");
		ClientCommand command = null;

		//Check to see if the first word input is quit or disconnect. 
		//In which case, we terminate the program
		if (args[0].equals("quit") || args[0].equals("disconnect"))
		{
			isTerminated = true;
			return false;
		}
		
		//If the first word is help, we run the runHelpCommand
		else if (args[0].equals("help"))
		{
			runHelpCommand(args);
			return false;
		}
		
		//register command, which should be followed by an email and password
		else if (args[0].equals("register"))
		{
			command = new RegisterCommand(args[1], args[2]);
		}
		
		//login command, which should be followed by an email and password
		else if (args[0].equals("login"))
		{
			command = new LoginCommand(args[1], args[2]);
		}
		
		//unknown command input
		else
		{
			Log.println("Unknown command '" + cmd + "'");
		}

		// Run the command if it's not null
		if (command != null)
		{
			try
			{
				os.writeObject(command);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Log.printClient("Failed to write register command to the server. "
						+ e.getMessage());
			}
			return true;
		}

		return false;
	}

	//If the first word in the command is help, we handle the request
	//Ex. > help register
	//This will print out what you need to fill out when you use the 
	//command register.
	private static void runHelpCommand(String[] args)
	{
		if (args.length > 1)
		{
			//If the command is found, proceed to give instructions
			//on how to use the command
			if (cmds.containsKey(args[1]))
			{
				Log.printConsole("Commands for " + args[1] + ": ");
				StringBuffer sb = new StringBuffer(args[1] + " [");
				
				//Looks in the HashMap for the command given and 
				//appends the input needed for the given command
				//to sb
				for (String subcmd : cmds.get(args[1]))
					sb.append(subcmd + ", ");
				Log.printConsole(sb.toString() + "]");
			}
			
			//Command is not in our HashMap
			else
			{
				Log.printConsole("Could not find help for command " + args[1]);
			}
		}
		
		//No command was given after the help keyword
		else
		{
			Log.println("**** Welcome to Connect4! ****\n");
			for (String cmd : cmds.keySet())
			{
				Log.print(cmd + ", ");
			}
			Log.println("\n****** * * **** * *  *******");
		}
	}
}
