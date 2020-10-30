package Client;

import java.io.*;
import java.net.*;
import java.util.*;

public class Player implements Runnable{
	
	String hostname;
	int myPort;
	public static final Scanner scan = new Scanner(System.in);
	private Socket serverSocket;
	private PrintWriter toServer;
	private BufferedReader fromServer;
	
	public Player(String host, int port)
	{
		hostname = host;
		myPort = port;
	}
	
	public void run()
	{
		try
		{
			//establish connections with the server
			serverSocket = new Socket(hostname, myPort);
			fromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			toServer = new PrintWriter(serverSocket.getOutputStream(), true);
			
			//read username prompt from server and ask for username
			System.out.print(fromServer.readLine());
			String username = scan.nextLine();
			toServer.println(username);
			
			//read password prompt from server and ask for password
			System.out.print(fromServer.readLine());
			String password = scan.nextLine();
			toServer.println(password);
			
			//read option prompt from server and ask for option
			System.out.println(fromServer.readLine());
			String option = scan.nextLine();
			option = option.trim();
			toServer.println(option);
			
			//communicate with server to create a new game
			if(option.equals("1"))
			{
				System.out.print(fromServer.readLine());
				boolean input_fails = Boolean.parseBoolean(fromServer.readLine());
				String newGameName = scan.nextLine();
				toServer.println(newGameName);
				while(input_fails)
				{
					boolean gameFound = Boolean.parseBoolean(fromServer.readLine());
					if(gameFound)
					{
						System.out.print(fromServer.readLine());
						newGameName = scan.nextLine();
					}
					else
					{
						input_fails = Boolean.parseBoolean(fromServer.readLine());
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
				System.out.print(fromServer.readLine());
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
							System.out.print(fromServer.readLine());
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
						System.out.print(fromServer.readLine());
						String newGameName = scan.nextLine();
						toServer.println(newGameName);
					}
				}
				
				//print the start message
				System.out.println(fromServer.readLine());
			}
			
			/*********************************
			 
			 game logic goes here
			 
			 
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
