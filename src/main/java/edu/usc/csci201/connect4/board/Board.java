package edu.usc.csci201.connect4.board;

public class Board {
	private int[][] board = new int[6][7];
	
	public Board()
	{
		
	}
	
	//print board
	public void printBoard()
	{
		for(int i = 0; i < 6; i++)
		{
			for(int j = 0; j < 7; j++)
			{
				if(board[i][j] == 0)
				{
					System.out.print(".");
				}
				else if(board[i][j] == 1)
				{
					System.out.print("o");
				}
				else
				{
					System.out.print("x");
				}
			}
			
			System.out.print("\n");
		}
	}
	
	//board is full
	public boolean isFull()
	{
		for(int i = 0; i < 6; i++)
		{
			for(int j = 0; j < 7; j++)
			{
				if(board[i][j] == 0)
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	//place piece
	public void placePiece(int col, boolean isP1)
	{
		for(int i = 5; i >= 0; i--)
		{
			if(board[i][col - 1] == 0)
			{
				if(isP1)
				{
					board[i][col - 1] = 1;
				}
				else
				{
					board[i][col - 1] = -1;
				}
				
				return;
			}
		}
	}
}
