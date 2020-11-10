package edu.usc.csci201.connect4.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import edu.usc.csci201.connect4.utils.Log;
import edu.usc.csci201.connect4.utils.TimeStamp;

public class GameUniverse {
	
	protected static ConcurrentHashMap<String, ArrayList<ClientReader>> nameToServerThreads = new ConcurrentHashMap<String, ArrayList<ClientReader>>();
	
	/*
	 * Tries to create a new game called gameName and adds ServerThread to it
	 * gameName must not already exist
	 * throws IOException if game already exists
	 */
	public static void makeNewGame(String gameName, ClientReader player) throws IOException {
		
		//Look for Server
		boolean gameFound = GameUniverse.nameToServerThreads.containsKey(gameName);

		// if the game exists
		if (gameFound) {
			throw new IOException("Game " + gameName + " already exists and has " 
					+ GameUniverse.nameToServerThreads.get(gameName).size() + " players");
		}
		
		
		
		
		//register this game
		ArrayList<ClientReader> sThreads = new ArrayList<ClientReader>();
		GameUniverse.nameToServerThreads.put(gameName, sThreads);
		Log.printServer(TimeStamp.getTimestamp() + " A new game named " + gameName + " has been created");
		
		sThreads.add(player);
		Log.printServer(TimeStamp.getTimestamp() + " Player " + player.getID() + " has joined " + gameName);
		
	}
	
	/*
	 * Tries to join a game called gameName and adds ServerThread to it
	 * gameName must already exist
	 * gameName must not have more than 2 players
	 * throws IOException if game doesn't exist or already has 2 players
	 */
	public static void joinGame(String gameName, ClientReader player) throws IOException {
		//Check for game
		if (!GameUniverse.nameToServerThreads.containsKey(gameName)) {
			throw new IOException("Game " + gameName + " could not be found");
		}
		
		// if the game exists but is full
		if(GameUniverse.nameToServerThreads.get(gameName).size() >= 2) {
			throw new IOException("Game " + gameName + " already is full");
		} 
		
		//register me to the game list
		GameUniverse.nameToServerThreads.get(gameName).add(player);
		Log.printServer(TimeStamp.getTimestamp() + " Player " + player.getID() + " has joined " + gameName);
	}
	
	//this function starts the game session
	public static void startGame(String gameName)
	{
		//start the game session
				ArrayList<ClientReader> readers = GameUniverse.nameToServerThreads.get(gameName);
				Socket player1 = readers.get(0).getSocket();
				Socket player2 = readers.get(1).getSocket();
				ClientReader player1Reader = readers.get(0);
				ClientReader player2Reader = readers.get(1);
				String player1Name = readers.get(0).getID();
				String player2Name = readers.get(1).getID();
				new Thread(new HandleGameSession
						(player1, player2, gameName, player1Name, player2Name, player1Reader, player2Reader)).start();
				System.out.println(TimeStamp.getTimestamp() + " " + gameName + " has been started");
	}
	
	//removes a game from game universe
	public static void removeGame(String gameName)
	{
		nameToServerThreads.remove(gameName);
	}
}