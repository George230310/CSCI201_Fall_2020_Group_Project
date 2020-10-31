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
			
			//receive player credentials
			String userName = readingFromPlayer.readLine();
			System.out.println(TimestampUtil.getTimestamp() + " user enter name: " + userName);
			
			/************************************
			 TODO: Verify the existence of username with
			 database. Now this just takes in the username
			 input.
			 
			 
			 
			 
			*************************************/
			
			//receive player password
			String password = readingFromPlayer.readLine();
			System.out.println(TimestampUtil.getTimestamp() + " user enter password: " + password);
			
			/************************************
			 TODO: Verify the existence of password with
			 database. Now this just takes in the password
			 input.
			 
			 
			 
			*************************************/
			
			//once the credentials are verified, receive player options
			String option = readingFromPlayer.readLine();
			
			//make a new game --- need error checking here
			if(option.equals("1"))
			{
				//receive game name and attempt to validate
				//send the validation result to player as well
				boolean input_fails = true;
				writingToPlayer.println(input_fails);
				
				String newGameName = readingFromPlayer.readLine();
				
				while(input_fails)
				{
					//send game found result to player
					boolean gameFound = Server.nameToServerThreads.containsKey(newGameName);
					writingToPlayer.println(gameFound);
					
					//if the game exists
					if(gameFound)
					{
						newGameName = readingFromPlayer.readLine();
					}
					else
					{
						input_fails = false;
						System.out.println(TimestampUtil.getTimestamp() + " A new game named " + newGameName + " has been created");
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
				//receive game name and attempt to validate
				//send the validation result to player as well
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
							GameName = readingFromPlayer.readLine();
						}
						else
						{
							input_fails = false;
						}						
					}
					else
					{
						//game not found, ask for another game name
						GameName = readingFromPlayer.readLine();
					}
				}
				
				//register me to the game list and wake up the other player
				Server.nameToServerThreads.get(GameName).add(this);
				writingToPlayer.println("You start the game as player 2");
				Server.nameToServerThreads.get(GameName).get(0).signalMe();
				try {
					Server.nameToServerThreads.get(GameName).get(0).join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//start the game session
				System.out.println(TimestampUtil.getTimestamp() + " " + GameName + " has been started");
				new Thread
				(new HandleGameSession
						(Server.nameToServerThreads.get(GameName).get(0).playerSocket, playerSocket, GameName)).start();
			}
			
		}
		catch(SocketException se)
		{
			System.out.println("Connection reset");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
