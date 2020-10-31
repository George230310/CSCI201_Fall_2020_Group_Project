package Client;

import java.io.*;
import java.net.*;
import java.util.*;

import Server.Server;

public class Player implements Runnable{
	
	private String hostname;
	private String username;
	private int myPort;
	public static final Scanner scan = new Scanner(System.in);
	private Socket serverSocket;
	private PrintWriter toServer;
	private BufferedReader fromServer;
	
	public Player(String host, int port)
	{
		hostname = host;
		myPort = port;
	}
	
	//main method to run the player interface of the game
	public static void main(String[] args) {
		new Thread(new Player(Server.serverName, Server.serverPort)).start();
	}
	
	public void run()
	{
		try
		{
			//establish connections with the server
			serverSocket = new Socket(hostname, myPort);
			fromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			toServer = new PrintWriter(serverSocket.getOutputStream(), true);
			
			//ascii art welcome slogan
			System.out.println("==============================");
			System.out.println("     Connect 4 2P Game !!!");
			System.out.println("==============================");
			
			//ask for username and send to server
			System.out.print("Please provide your username: ");
			username = scan.nextLine();
			toServer.println(username);
			
			//ask for password and send to server
			System.out.print("Please provide your password: ");
			String password = scan.nextLine();
			toServer.println(password);
			
			//rask for option
			System.out.println("1) Start a new game\n2) Join an existing game");
			String option = scan.nextLine();
			option = option.trim();
			toServer.println(option);
			
			//communicate with server to create a new game
			if(option.equals("1"))
			{
				System.out.print("Enter a new game name: ");
				boolean input_fails = Boolean.parseBoolean(fromServer.readLine());
				String newGameName = scan.nextLine();
				toServer.println(newGameName);
				while(input_fails)
				{
					boolean gameFound = Boolean.parseBoolean(fromServer.readLine());
					if(gameFound)
					{
						System.out.print("Game already exists, enter a new game name: ");
						newGameName = scan.nextLine();
						toServer.println(newGameName);
					}
					else
					{
						input_fails = false;
					}
				}
				
				//print the waiting message
				System.out.println(fromServer.readLine());
				
				//print the game start message
				System.out.println(fromServer.readLine());
			}
			//communicate with server to join a game
			else
			{
				System.out.print("Enter a game name: ");
				boolean input_fails = Boolean.parseBoolean(fromServer.readLine());
				String existingGameName = scan.nextLine();
				toServer.println(existingGameName);
				
				while(input_fails)
				{
					boolean gameFound = Boolean.parseBoolean(fromServer.readLine());
					if(gameFound)
					{
						boolean gameFull = Boolean.parseBoolean(fromServer.readLine());
						if(gameFull)
						{
							System.out.print("Game already full, enter a new game name: ");
							String newGameName = scan.nextLine();
							toServer.println(newGameName);
						}
						else
						{
							input_fails = false;
						}
					}
					else
					{
						System.out.print("No such game exists, enter a new game name: ");
						String newGameName = scan.nextLine();
						toServer.println(newGameName);
					}
				}
				
				//print the start message
				System.out.println(fromServer.readLine());
			}
			
			/*********************************
			 
			 TODO: Implement the player side game loop.
			 This loop will communicate with HandleGameSession class
			 
			 
			**********************************/
			
			serverSocket.close();
			fromServer.close();
			toServer.close();
		}
		catch(UnknownHostException ue)
		{
			System.out.println("Unknown host");
		}
		catch(SocketException se)
		{
			System.out.println("Connection reset");
		}
		catch(IOException ie)
		{
			ie.printStackTrace();
		}
	}
}
