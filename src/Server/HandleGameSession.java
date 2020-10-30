package Server;

import java.net.*;

public class HandleGameSession implements Runnable{
	
	private Socket player1;
	private Socket player2;
	private String myGameName;
	
	//TODO: add more member fields for reading and writing from players
	
	public HandleGameSession(Socket p1, Socket p2, String gName)
	{
		player1 = p1;
		player2 = p2;
		myGameName = gName;
	}
	
	public void run()
	{
		/******************************
		 TODO: Handle the game
		 
		 
		
		
		
		 TODO: Save game records to the database
		*******************************/
		
		Server.nameToServerThreads.remove(myGameName);
	}
}
