package Server;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerThread extends Thread {
	private Socket playerSocket = null;
	private PrintWriter writingToPlayer = null;
	private BufferedReader readingFromPlayer = null;
	private Lock lock = new ReentrantLock();
	private Condition startGame = lock.newCondition();
	
	public ServerThread(Socket s)
	{
		playerSocket = s;
	}
	
	public void run()
	{
		//prompt the client for credentials
		try
		{
			//establish connections (read and write) with the player
			writingToPlayer = new PrintWriter(playerSocket.getOutputStream(), true);
			readingFromPlayer = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
			
			//ask the player for credentials
			String promptForCredential = "Please provide your username: ";
			writingToPlayer.println(promptForCredential);
			
			String userName = readingFromPlayer.readLine();
			System.out.println("user enter name: " + userName);
			
			/************************************
			 Verify the existence of username with
			 database
			 
			 
			 
			*************************************/
			
			String promptForPassword = "Please provide your password: ";
			writingToPlayer.println(promptForPassword);
			
			String password = readingFromPlayer.readLine();
			
			/************************************
			 Verify the existence of password with
			 database
			 
			 
			 
			*************************************/
			
			//once the credentials are verified, prompt the player for options
			String promptForOptions = "Enter 1 to start a new game\nEnter 2 to join an existing game";
			writingToPlayer.println(promptForOptions);
			
			String option = readingFromPlayer.readLine();
			option = option.trim();
			
			//make a new game --- need error checking here
			if(option.equals("1"))
			{
				//ask player for a game name
				String promptForGameName = "Enter a new game name: ";
				writingToPlayer.println(promptForGameName);
				
				boolean input_fails = true;
				String newGameName = readingFromPlayer.readLine();
				while(input_fails)
				{
					if(Server.nameToServerThreads.containsKey(newGameName))
					{
						String repromptForGameName = "Game already exists, enter a new game name: ";
						writingToPlayer.println(repromptForGameName);
						
						newGameName = readingFromPlayer.readLine();
					}
					else
					{
						input_fails = false;
					}
				}
				
				//register this game
				ArrayList<ServerThread> sThreads = new ArrayList<>();
				sThreads.add(this);
				Server.nameToServerThreads.put(newGameName, sThreads);
				
				//wait for player 2
				lock.lock();
				try
				{
					writingToPlayer.println("Waiting for player 2");
					startGame.await();
				}
				catch(InterruptedException ie)
				{
					ie.printStackTrace();
				}
				finally
				{
					writingToPlayer.println("You start the game as player 1");
					lock.unlock();
				}
			}
			//join an existing game ---- DOES NOT check correct number of players now
			else if(option.equals("2"))
			{
				String promptForGameName = "Enter a game name: ";
				writingToPlayer.println(promptForGameName);
				
				boolean input_fails = true;
				String GameName = readingFromPlayer.readLine();
				while(input_fails)
				{
					if(Server.nameToServerThreads.containsKey(GameName))
					{
						
						input_fails = false;
					}
					else
					{
						String repromptForGameName = "No such game exists, enter a new game name: ";
						writingToPlayer.println(repromptForGameName);
						
						GameName = readingFromPlayer.readLine();
					}
				}
				
				//register me to the game and wake up the other player
				Server.nameToServerThreads.get(GameName).add(this);
				writingToPlayer.println("You start the game as player 2");
				Server.nameToServerThreads.get(GameName).get(0).startGame.signal();
				try {
					Server.nameToServerThreads.get(GameName).get(0).join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//start the game session
				new Thread
				(new HandleGameSession
						(playerSocket, Server.nameToServerThreads.get(GameName).get(0).playerSocket, GameName)).start();
			}
			
		}
		catch(SocketException se)
		{
			System.out.println("connection reset");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
