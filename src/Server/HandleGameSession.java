package Server;

import java.net.*;

public class HandleGameSession implements Runnable{
	
	private Socket player1;
	private Socket player2;
	private String myGameName;
	
	public HandleGameSession(Socket p1, Socket p2, String gName)
	{
		player1 = p1;
		player2 = p2;
		myGameName = gName;
	}
	
	public void run()
	{
		/******************************
		 Handle the game
		 
		 
		
		*******************************/
	}
}
