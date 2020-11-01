package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import Board.Board;

public class HandleGameSession implements Runnable{
	
	private Socket player1;
	private Socket player2;
	private String myGameName;
	
	//TODO: add more member fields for reading and writing from players
	private PrintWriter toPlayer1;
	private BufferedReader fromPlayer1;
	
	private PrintWriter toPlayer2;
	private BufferedReader fromPlayer2;
	
	private Board serverBoard = new Board();
	
	//store the game result
	private boolean player1Wins = false;
	
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
		
		//establish all player connections
		try
		{
			toPlayer1 = new PrintWriter(player1.getOutputStream(), true);
			fromPlayer1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
			
			toPlayer2 = new PrintWriter(player2.getOutputStream(), true);
			fromPlayer2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
			
			//host game
			boolean continuePlay = true;
			while(continuePlay)
			{
				//receive input from player1
				int player1Col = 0;
				player1Col = Integer.parseInt(fromPlayer1.readLine());
				
				//update board on server
				serverBoard.placePiece(player1Col, true);
				//update player2's board
				toPlayer2.println(player1Col);
				
				//TODO: decide whether player1 wins
				
				
				//TODO: if player1 wins, end loop and signal both players to end game
				
				
				//if player1 doesn't win, player2 continues
				int player2Col = 0;
				player2Col = Integer.parseInt(fromPlayer2.readLine());
				
				//update board on server
				serverBoard.placePiece(player2Col, false);
				//update player2's board
				toPlayer1.println(player2Col);
				
				//TODO: decide whether player2 wins
				
				
				//TODO: if player2 wins, end loop and signal both players to end game
				
				
				
			}
			
			//TODO: broadcast the game result
			
			
			//TODO: save the game result to the database
			
			
			Server.nameToServerThreads.remove(myGameName);
		}
		catch(SocketException se)
		{
			System.out.println("Connection reset");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
