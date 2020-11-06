package edu.usc.csci201.connect4.server;

import java.net.Socket;

public class HandleGameSession implements Runnable {
	
	private Socket player1;
	private Socket player2;
	private String myGameName;

	public HandleGameSession(Socket p1, Socket p2, String gName)
	{
		player1 = p1;
		player2 = p2;
		myGameName = gName;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
