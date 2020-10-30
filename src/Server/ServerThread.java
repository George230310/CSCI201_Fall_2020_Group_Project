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
	
	public void signalMe()
	{
		lock.lock();
		try
		{
			startGame.signal();
		}
		finally
		{
			lock.unlock();
		}
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
			writingToPlayer.println("Please provide your username: ");
			
			String userName = readingFromPlayer.readLine();
			System.out.println("user enter name: " + userName);
			
			/************************************
			 Verify the existence of username with
			 database
			 
			 
			 
			*************************************/
			
			writingToPlayer.println("Please provide your password: ");
			
			String password = readingFromPlayer.readLine();
			System.out.println("user enter password: " + password);
			
			/************************************
			 Verify the existence of password with
			 database
			 
			 
			 
			*************************************/
			
			//once the credentials are verified, prompt the player for options
			writingToPlayer.println("1) Start a new game 2) Join an existing game");
			
			String option = readingFromPlayer.readLine();
			
			//make a new game --- need error checking here
			if(option.equals("1"))
			{
				//ask player for a game name
				writingToPlayer.println("Enter a new game name: ");
				
				//validate game name
				boolean input_fails = true;
				writingToPlayer.println(input_fails);
				String newGameName = readingFromPlayer.readLine();
				while(input_fails)
				{
					boolean gameFound = Server.nameToServerThreads.containsKey(newGameName);
					writingToPlayer.println(gameFound);
					//if the game exists
					if(gameFound)
					{
						writingToPlayer.println("Game already exists, enter a new game name: ");
						
						newGameName = readingFromPlayer.readLine();
					}
					else
					{
						input_fails = false;
						writingToPlayer.println(input_fails);
						System.out.println("A new game named " + newGameName + " has been created");
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
					writingToPlayer.println("Waiting for player 2...");
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
			//join an existing game
			else
			{
				writingToPlayer.println("Enter a game name: ");
				
				boolean input_fails = true;
				writingToPlayer.println(input_fails);
				String GameName = readingFromPlayer.readLine();
				while(input_fails)
				{
					boolean gameFound = Server.nameToServerThreads.containsKey(GameName);
					writingToPlayer.println(gameFound);
					if(gameFound)
					{
						//if the game exists but is full
						boolean gameFull = (Server.nameToServerThreads.get(GameName).size() >= 2);
						writingToPlayer.println(gameFull);
						if(gameFull)
						{
							writingToPlayer.println("Game already full, enter a new game name: ");
							GameName = readingFromPlayer.readLine();
						}
						else
						{
							input_fails = false;
						}						
					}
					else
					{
						writingToPlayer.println("No such game exists, enter a new game name: ");
						
						GameName = readingFromPlayer.readLine();
					}
				}
				
				//register me to the game and wake up the other player
				Server.nameToServerThreads.get(GameName).add(this);
				writingToPlayer.println("You start the game as player 2");
				Server.nameToServerThreads.get(GameName).get(0).signalMe();
				try {
					Server.nameToServerThreads.get(GameName).get(0).join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//start the game session
				new Thread
				(new HandleGameSession
						(Server.nameToServerThreads.get(GameName).get(0).playerSocket, playerSocket, GameName)).start();
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
