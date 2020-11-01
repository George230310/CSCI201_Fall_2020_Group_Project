package Client;

import java.io.*;
import java.net.*;
import java.util.*;

import Board.Board;
import Server.Server;

public class Player implements Runnable{
	
	private String hostname;
	private String username;
	private int myPort;
	public static final Scanner scan = new Scanner(System.in);
	private Socket serverSocket;
	private PrintWriter toServer;
	private BufferedReader fromServer;
	
	//game related data fields
	private Board playerBoard = new Board();
	private boolean isMyTurn = true;
	private boolean isP1 = true;
	
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
				
				isMyTurn = true;
				isP1 = true;
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
				
				isMyTurn = false;
				isP1 = false;
			}
			
			/*********************************
			 
			 TODO: Implement the player side game loop.
			 This loop will communicate with HandleGameSession class
			 
			 
			**********************************/
			
			//run game loop
			boolean continuePlay = true;
			playerBoard.printBoard();
			while(continuePlay)
			{
				if(isMyTurn)
				{
					boolean input_fails = true;
					int myMove = 0;
					System.out.print("It is your turn now, enter an integer for column: ");
					while(input_fails)
					{
						try
						{
							myMove = Integer.parseInt(scan.nextLine());
							input_fails = false;
						}
						catch(NumberFormatException e)
						{
							System.out.print("Please enter an integer: ");
						}
					}
					
					playerBoard.placePiece(myMove, isP1);
					toServer.println(myMove);
					isMyTurn = false;
					
					//print board state after my move
					playerBoard.printBoard();
					
					//TODO: break out of game loop when the server thinks someone wins
					//e.g. continuePlay = Boolean.parseBoolean(fromServer.readLine())
				}
				else
				{
					System.out.println("Waiting for the other player to move...");
					int otherPlayerMove = Integer.parseInt(fromServer.readLine());
					playerBoard.placePiece(otherPlayerMove, !isP1);
					playerBoard.printBoard();
					System.out.println("The other player places a piece at column " + otherPlayerMove);
					
					isMyTurn = true;
					
					//TODO: break out of game loop when the server thinks someone wins
					//e.g. continuePlay = Boolean.parseBoolean(fromServer.readLine())
				}
			}
			
			//TODO: receive the game result
			
			//TODO: inform the server my username?? and save my record
			
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
