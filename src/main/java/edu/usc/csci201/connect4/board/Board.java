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
	
	public boolean isValidMove(int col) {
		if(col > 6) {
			return false;
		}
		
		if(board[0][col] != 0) {
			return false;
		}
		
		return true;
	}
	
	public int isGameOver() {
		int count = 0;
		int row = 6;
		int col = 7;
		
		for(int i = 0; i < col; i++) {
			int piece = 2;
			for(int j = 0; j < row; j++) {
				if(piece == board[i][j]) {
					count++;
				}
				else {
					count = 0;
				}
				
				if(count >= 4) {
					return piece;
				}
				piece = board[j][i];
			}
			
		}
		
		for(int i = 0; i < row; i++) {
			int piece = 2;
			for(int j = 0; j < col; j++) {
				if(piece == board[i][j]) {
					count++;
				}
				else {
					count = 0;
				}
				
				if(count >= 4) {
					return piece;
				}
				piece = board[j][i];
			}
			
		}
		
		for(int i = 0; i < row; i++) {
			int piece = 2;
			for(int j = 0; j < col; j++) {
				if(board[i][j] != 0) {
					piece = board[i][j];
					
					if(board[i+1][j+1] == piece) {
						int currRow = i;
						int currCol = j;
						
						while(board[++currRow][++currCol] == piece) {
							count++;
						}
						
						currRow = i;
						currCol = j;
						
						while(board[--currRow][--currCol] == piece) {
							count++;
						}
						
						if(count >= 4) {
							return piece;
						}
						else {
							count = 0;
						}
					}
					
					if(board[i+1][j-1] == piece) {
						int currRow = i;
						int currCol = j;
						
						while(board[++currRow][--currCol] == piece) {
							count++;
						}
						
						currRow = i;
						currCol = j;
						
						while(board[--currRow][++currCol] == piece) {
							count++;
						}
						
						if(count >= 4) {
							return piece;
						}
						else {
							count = 0;
						}
					}
				}
			}
		}
		return 0;
	}
}
