package edu.usc.csci201.connect4.board;

public class Board
{

	private static int row = 7;
	private static int col = 8;
	private int currRow = 1;
	private int currCol = 1;
	private int turns = 0;

	private int[][] board = new int[row][col];

	// Initialize the board to 0's
	public Board()
	{
		for (int i = 1; i < row; i++)
		{
			for (int j = 1; j < col; j++)
			{
				board[i][j] = 0;
			}
		}
	}

	// print board
	public void printBoard()
	{
		for (int i = row - 1; i > 0; i--)
		{
			for (int j = 1; j < col; j++)
			{
				if(board[i][j] == 0)
				{
					System.out.print(".");
				}
				else if(board[i][j] == 1)
				{
					System.out.print("O");
				}
				else
				{
					System.out.print("X");
				}
			}

			System.out.println();
		}
	}

	// board is full
	public boolean isFull()
	{
		if (Integer.compare(turns, 42) == 0)
			return true;

		else
			return false;
	}
	
	//checking legal move
	public boolean isLegalMove(int col) throws RuntimeException
	{
		if (col > 7 || col < 1)
		{
			throw new RuntimeException();
		}
		else if(board[row - 1][col] == 1 || board[row - 1][col] == 2)
		{
			throw new RuntimeException();
		}
		
		return true;
	}

	// place piece
	public void placePiece(int column, boolean isP1) throws RuntimeException
	{
		if(isLegalMove(column))
		{
			boolean placedPiece = false;
			currRow = 1;
			currCol = column;
			
			while (currRow < row && !placedPiece)
			{
				if (board[currRow][column] == 0)
				{
					if (isP1)
					{
						board[currRow][column] = 1;
						turns++;
					}
					else
					{
						board[currRow][column] = 2;
						turns++;
					}
					placedPiece = true;
				}
				else
				{
					currRow++;
				}
			}
		}
	}
	
	//Returns 0 if there is no winner, 1 if player 1 wins, 2 if player 2 wins
	public int isGameOver()
	{
		boolean foundOtherPiece = false;
		int fourCount = 1;
		int checkRow = currRow;
		int checkCol = currCol;
		
		//Check above us
		checkRow++;
		while (checkRow < row && !foundOtherPiece)
		{
			if (Integer.compare(board[checkRow][currCol] , board[currRow][currCol]) == 0)
			{
				fourCount++;
				checkRow++;
			}
			else
			{
				foundOtherPiece = true;
			}
		}
		
		//Check below us
		checkRow = currRow - 1;
		foundOtherPiece = false;
		
		while (checkRow > 0 && !foundOtherPiece)
		{
			if (Integer.compare(board[checkRow][currCol] , board[currRow][currCol]) == 0)
			{
				fourCount++;
				checkRow--;
			}
			else
			{
				foundOtherPiece = true;
			}
		}
		
		//Check to see if we got more than 3 in a row (4 or greater)
		if (Integer.compare(fourCount , 3) == 1)
		{
			return board[currRow][currCol];
		}
		
		//Check to the right of us
		fourCount = 1;
		checkCol++;
		foundOtherPiece = false;
		while (checkCol < col && !foundOtherPiece)
		{
			if (Integer.compare(board[currRow][checkCol] , board[currRow][currCol]) == 0)
			{
				fourCount++;
				checkCol++;
			}
			else
			{
				foundOtherPiece = true;
			}
		}
		
		
		
		//Check to the left of us
		checkCol = currCol - 1;
		foundOtherPiece = false;

		while (checkCol > 0 && !foundOtherPiece)
		{
			if (Integer.compare(board[currRow][checkCol] , board[currRow][currCol]) == 0)
			{
				fourCount++;
				checkCol--;
			}
			else
			{
				foundOtherPiece = true;
			}
		}
		
		//Check to see if we got more than 3 in a row (4 or greater)
		if (Integer.compare(fourCount , 3) == 1)
		{
			return board[currRow][currCol];
		}
		
		//Check diagonally up/left
		fourCount = 1;
		checkRow = currRow + 1;
		checkCol = currCol - 1;
		foundOtherPiece = false;
		
		while (checkRow < row && checkCol > 0 && !foundOtherPiece)
		{
			if (Integer.compare(board[checkRow][checkCol] , board[currRow][currCol]) == 0)
			{
				fourCount++;
				checkRow++;
				checkCol--;
			}
			else
			{
				foundOtherPiece = true;
			}
		}
		
		//Check diagonally down/right
		checkRow = currRow - 1;
		checkCol = currCol + 1;
		foundOtherPiece = false;
		
		while (checkRow > 0 && checkCol < col && !foundOtherPiece)
		{
			if (Integer.compare(board[checkRow][checkCol] , board[currRow][currCol]) == 0)
			{
				fourCount++;
				checkRow--;
				checkCol++;
			}
			else
			{
				foundOtherPiece = true;
			}
		}
		
		//Check to see if we got more than 3 in a row (4 or greater)
		if (Integer.compare(fourCount , 3) == 1)
		{
			return board[currRow][currCol];
		}
		
		//Check diagonally up/right
		fourCount = 1;
		checkRow = currRow + 1;
		checkCol = currCol + 1;
		foundOtherPiece = false;
		
		while (checkRow < row && checkCol < col && !foundOtherPiece)
		{
			if (Integer.compare(board[checkRow][checkCol] , board[currRow][currCol]) == 0)
			{
				fourCount++;
				checkRow++;
				checkCol++;
			}
			else
			{
				foundOtherPiece = true;
			}
		}
		
		//Check diagonally down/left
		
		checkRow = currRow - 1;
		checkCol = currCol - 1;
		foundOtherPiece = false;

		while (checkRow > 0 && checkCol > 0 && !foundOtherPiece)
		{
			if (Integer.compare(board[checkRow][checkCol] , board[currRow][currCol]) == 0)
			{
				fourCount++;
				checkRow--;
				checkCol--;
			}
			else
			{
				foundOtherPiece = true;
			}
		}
		
		//Check to see if we got more than 3 in a row (4 or greater)
		if (Integer.compare(fourCount , 3) == 1)
		{
			return board[currRow][currCol];
		}
		
		return 0;
	}
}
