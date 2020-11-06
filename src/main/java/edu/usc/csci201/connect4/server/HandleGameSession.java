package edu.usc.csci201.connect4.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import edu.usc.csci201.connect4.board.Board;

public class HandleGameSession implements Runnable{
	
	private Socket player1;
	private String player1Name;
	private Socket player2;
	private String player2Name;
	private String myGameName;
	private ClientReader p1Reader;
	private ClientReader p2Reader;
	
	//TODO: add more member fields for reading and writing from players
	private ObjectOutputStream toPlayer1;
	private ObjectInputStream fromPlayer1;
	
	private ObjectOutputStream toPlayer2;
	private ObjectInputStream fromPlayer2;
	
	private Board serverBoard = new Board();
	
	//store the game result
	private boolean player1Wins = false;
	
	public HandleGameSession(Socket p1, Socket p2, String gName, String p1N, String p2N, ClientReader p1R, ClientReader p2R)
	{
		myGameName = gName;
		player1 = p1;
		player2 = p2;
		player1Name = p1N;
		player2Name = p2N;
		p1Reader = p1R;
		p2Reader = p2R;
	}
	
	public void run()
	{
		try
		{
			toPlayer1 = new ObjectOutputStream(player1.getOutputStream());
			toPlayer2 = new ObjectOutputStream(player2.getOutputStream());
			fromPlayer1 = new ObjectInputStream(player1.getInputStream());
			fromPlayer2 = new ObjectInputStream(player2.getInputStream());
			
			//signal the start of the game
			toPlayer1.writeObject(new ClientHandler.StartGameCommand(true));
			toPlayer2.writeObject(new ClientHandler.StartGameCommand(false));
			
			toPlayer1.close();
			fromPlayer1.close();
			toPlayer2.close();
			fromPlayer2.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		//establish all player connections
		/*try
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
			
			
		}
		catch(SocketException se)
		{
			System.out.println("Connection reset");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}*/
		
		p1Reader.signalGameFinished();
		p2Reader.signalGameFinished();
	}
}
