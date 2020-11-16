package Testing;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import edu.usc.csci201.connect4.board.Board;

public class BoardTest {
	
	//Construct an empty board
	private int[][] emptyBoard(int row, int col) {
		int[][] empty = new int[row][col];
		for (int i = 1; i < row; i++)
		{
			for (int j = 1; j < col; j++)
			{
				empty[i][j] = 0;
			}
		}
		return empty;
	}
	
	private void placePiece(int[][] testArray, Board board, int row, int col, Boolean isP1) {
		board.placePiece(col, isP1);
		if(isP1)
			testArray[row][col] = 1;
		else
			testArray[row][col] = 2;
	}
	
	private int[][] fullBoard(int row, int col, Board board) {
		int[][] full = new int[row][col];
		Boolean turn = true;
		for(int i = 1; i < col; i++) {
			//Alternate which piece comes first every other row
			if(i/2 % 2 == 1) turn = true;
			else turn = false;
			//Place pieces in alternating fashion on row
			for(int j = 1; j < row; j++) {
				placePiece(full, board, j, i, turn);
				turn = !turn;
			}
		}
		return full;
	}
	
	//Check two boards are the same
	private void assertEqualsBoard(int[][] board1, int[][] board2) {
		//Check rows
		int rows = board1.length;
		assertEquals(rows, board2.length);
		//Check columns, make sure they are all equal
		int cols = board1[0].length;
		for(int i = 0; i < rows; i++) {
			assertEquals(board1[i].length, board2[i].length);
			assertEquals(board1[i].length, cols);
		}
		
		for (int i = 1; i < rows; i++)
		{
			for (int j = 1; j < cols; j++)
			{
				assertEquals(board1[i][j], board2[i][j]);
			}
		}
	}

	//Makes sure no exceptions are thrown
	@Test
	public void testSingleConstructor() {
		Board board = new Board();
	}

	//No issues with multiple boards active at once
	@Test
	public void testMultipleBoards() {
		Board board1 = new Board();
		Board board2 = new Board();
		board1.toString();
		board2.toString();
	}
	
	//Try placing a piece
	@Test
	public void testSinglePlace() {
		Board board = new Board();
		int[][] expected = emptyBoard(7,8);
		placePiece(expected, board, 1, 4, true);
		
		assertEqualsBoard(board.getBoard(),expected);
		assertEquals(board.isFull(), false);
		assertEquals(board.isGameOver(), 0);
	}
	
	//Make sure pieces stack properly
	@Test
	public void testMultiplePlace() {
		Board board = new Board();
		int[][] expected = emptyBoard(7,8);
		placePiece(expected, board, 1, 4, true);
		placePiece(expected, board, 2, 4, false);
		
		assertEqualsBoard(board.getBoard(),expected);
		assertEquals(board.isFull(), false);
		assertEquals(board.isGameOver(), 0);
	}
	
	//Test filling up a board
	@Test
	public void testFull() {
		Board board = new Board();
		int[][] expected = fullBoard(7,8,board);
		
		assertEqualsBoard(board.getBoard(), expected);
		assertEquals(board.isFull(), true);
		assertEquals(board.isGameOver(), 0);
		
		for(int i = 1; i < 8; i++) {
			//Check to make sure you can't insert anymore
			try {
				board.isLegalMove(i);
				Assert.fail();
			}
			catch (RuntimeException e) {
				
			}
			catch (Exception e) {
				Assert.fail("Could still insert piece in col " + i);
			}
			
		}
	}
	
	//Test outofBounds
	@Test
	public void testOutofBounds() {
		Board board = new Board();
		try {
			board.isLegalMove(0);
			Assert.fail();
		}
		catch (RuntimeException e) {
			
		}
		catch (Exception e) {
			Assert.fail("Inserted Piece at 0");
		}
		
		try {
			board.isLegalMove(8);
			Assert.fail();
		}
		catch (RuntimeException e) {
			
		}
		catch (Exception e) {
			Assert.fail("Inserted Piece at 8");
		}
	}
	
	//Test horizontal win
	@Test
	public void testHorizontalWin() {
		Board board = new Board();
		//Last piece placed in on the right
		board.placePiece(2, true);
		board.placePiece(3, true);
		board.placePiece(4, true);
		assertEquals(board.isGameOver(), 0);
		board.placePiece(5, true);
		assertEquals(board.isGameOver(), 1);
		
		board = new Board();
		//Last piece placed in on the left
		board.placePiece(5, true);
		board.placePiece(4, true);
		board.placePiece(3, true);
		assertEquals(board.isGameOver(), 0);
		board.placePiece(2, true);
		assertEquals(board.isGameOver(), 1);
		
		board = new Board();
		//Last piece placed in on the middle
		board.placePiece(5, true);
		board.placePiece(4, true);
		board.placePiece(2, true);
		assertEquals(board.isGameOver(), 0);
		board.placePiece(3, true);
		assertEquals(board.isGameOver(), 1);
	}
	
	//Test Vertical win
	@Test
	public void testVerticalWin() {
		Board board = new Board();
		//Last piece placed in on the right
		board.placePiece(2, true);
		board.placePiece(2, true);
		board.placePiece(2, true);
		assertEquals(board.isGameOver(), 0);
		board.placePiece(2, true);
		assertEquals(board.isGameOver(), 1);
	}
		
	//Test Diagonal Win
	@Test
	public void testDiagonal() {
		Board board = new Board();
		/*
		 * Creates
		 * 1
		 * 11
		 * 111
		 * 2111
		 */
		board.placePiece(2, false);
		board.placePiece(2, true);
		board.placePiece(2, true);
		board.placePiece(2, true);
		board.placePiece(3, true);
		board.placePiece(3, true);
		board.placePiece(3, true);
		board.placePiece(4, true);
		board.placePiece(4, true);
		assertEquals(board.isGameOver(), 0);
		board.placePiece(5, true);
		assertEquals(board.isGameOver(), 1);
	}
}
