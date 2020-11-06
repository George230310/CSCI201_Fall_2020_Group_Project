package edu.usc.csci201.connect4.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import edu.usc.csci201.connect4.board.Board;
import edu.usc.csci201.connect4.server.ClientHandler.*;
import edu.usc.csci201.connect4.utils.Log;

public class HandleGameSession implements Runnable{
	
	private Socket player1;
	private String player1Name;
	private Socket player2;
	private String player2Name;
	private String myGameName;
	private ClientReader p1Reader;
	private ClientReader p2Reader;
	
	//read and write from players
	private ObjectOutputStream toPlayer1;
	private ObjectInputStream fromPlayer1;
	
	private ObjectOutputStream toPlayer2;
	private ObjectInputStream fromPlayer2;
	
	private Board serverBoard = new Board();
	
	//store the game result
	private Boolean player1Wins = null;
	
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
			StartGameCommand p1StartGame = new StartGameCommand(true);
			p1StartGame.setResponse("You start the game as player 1");
			toPlayer1.writeObject(p1StartGame);
			StartGameCommand p2StartGame = new StartGameCommand(false);
			p2StartGame.setResponse("You start the game as player 2");
			toPlayer2.writeObject(p2StartGame);
			
			//game logic here
			while(true)
			{
				//receive input from player 1
				GameCommand p1GameMove = (GameCommand)fromPlayer1.readObject();
				int p1Col = ((GameCommand)p1GameMove).getMove();
				serverBoard.placePiece(p1Col, true);
				
				p1GameMove.setResponseCol(p1Col);
				p1GameMove.setResponse("Player 1 places a piece on column " + p1Col);
				p1GameMove.setSuccessful();
				
				//decide winning
				if(p1Col == 7)
				{
					p1GameMove.setGameOver(true);
					player1Wins = true;
				}
				
				if(serverBoard.isFull())
				{
					p1GameMove.setGameOver(null);
				}
				
				//inform player1 of result
				toPlayer1.writeObject(p1GameMove);
				//update player2's board
				toPlayer2.writeObject(p1GameMove);
				//break out if player 1 wins / board is full
				if(p1GameMove.isGameOver())
				{
					break;
				}
				
				//receive input from player 2
				GameCommand p2GameMove = (GameCommand)fromPlayer2.readObject();
				int p2Col = ((GameCommand)p2GameMove).getMove();
				serverBoard.placePiece(p2Col, false);
				
				p2GameMove.setResponseCol(p2Col);
				p2GameMove.setResponse("Player 2 places a piece on column " + p2Col);
				p2GameMove.setSuccessful();
				
				//decide winning
				if(p2Col == 7)
				{
					p2GameMove.setGameOver(false);
					player1Wins = false;
					
				}
				
				if(serverBoard.isFull())
				{
					p2GameMove.setGameOver(null);
				}
				
				//inform player2 of result
				toPlayer2.writeObject(p2GameMove);
				//update player1's board
				toPlayer1.writeObject(p2GameMove);
				//break out if player 2 wins / board is full
				if(p2GameMove.isGameOver())
				{
					break;
				}
			}
			
			if(player1Wins == null)
			{
				//nobody wins
			}
			else if(player1Wins && player1Name != null)
			{
				//save player1 score to database
				Log.printServer("Saving " + player1Name + "'s scores to database");
				
				//save
			}
			else if(player2Name != null)
			{
				//save player2 score to database
				Log.printServer("Saving " + player2Name + "'s scores to database");
				
				//save
			}
		}
		catch(ClassNotFoundException ce)
		{
			ce.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		Server.gameUniverse.remove(myGameName);
		p1Reader.signalGameFinished();
		p2Reader.signalGameFinished();
	}
}
