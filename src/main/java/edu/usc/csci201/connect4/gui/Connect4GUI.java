package edu.usc.csci201.connect4.gui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

import edu.usc.csci201.connect4.board.Board;
import edu.usc.csci201.connect4.server.Server;
import edu.usc.csci201.connect4.server.ClientHandler.ClientCommand;
import edu.usc.csci201.connect4.server.ClientHandler.CreateLobbyCommand;
import edu.usc.csci201.connect4.server.ClientHandler.GameCommand;
import edu.usc.csci201.connect4.server.ClientHandler.GetHighScoresCommand;
import edu.usc.csci201.connect4.server.ClientHandler.JoinLobbyCommand;
import edu.usc.csci201.connect4.server.ClientHandler.LoginCommand;
import edu.usc.csci201.connect4.server.ClientHandler.RegisterCommand;
import edu.usc.csci201.connect4.server.ClientHandler.StartGameCommand;
import edu.usc.csci201.connect4.utils.Log;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
 
public class Connect4GUI extends Application {
	private int NUM_COLUMNS = 8;
	private int NUM_ROWS = 7;
	private Pane circlePane = new Pane(); // circles represent player moves
	private Thread clientThread;
	
	// TODO: setup fxml and controller to take in board parameter
	public void init() {
		Parameters params = getParameters();
	}
	
	// Add Board to StackPane
	private Parent createContentParent() {
		Pane root = new Pane();
		Shape theBoard = makeBoard();
		root.getChildren().add(circlePane);
		root.getChildren().add(theBoard);
		
		return root;
	}
	
	// Make a board with holes
	private Shape makeBoard() {
		Shape board = new Rectangle(75 * (NUM_COLUMNS), 75 * (NUM_ROWS));
		
		for(int rowCounter = 0; rowCounter < NUM_ROWS-1; rowCounter++) {
			for(int colCounter = 0; colCounter < NUM_COLUMNS-1; colCounter++) {
				Circle theCircle = new Circle();
				theCircle.setCenterX(50);
				theCircle.setCenterY(50);
				theCircle.setRadius(25);
				// symmetrical spacing
				theCircle.setTranslateX((colCounter * 75) + 15);
				theCircle.setTranslateY((rowCounter * 75) + 15);
				board = Shape.subtract(board, theCircle);
			}
		}
		board.setFill(Color.BLUE);
		return board;
	}
	
	// Takes row and col and color and places it accordingly 
    private void placeCircle(int row, int col, String color) {
        // some checks for row and col validity?
    	Circle theCircle = new Circle();
		theCircle.setCenterX(50);
		theCircle.setCenterY(50);
		theCircle.setRadius(25);
		theCircle.setTranslateX((col * 75) + 15);
    	circlePane.getChildren().add(theCircle);
		if(color.equals("RED")) {
			theCircle.setFill(Color.RED);
    	}
    	else {
    		theCircle.setFill(Color.YELLOW);
    	}	
        TranslateTransition dropAnimation = new TranslateTransition(Duration.seconds(1), theCircle);
        dropAnimation.setToY((row * 75) + 15);
        dropAnimation.play();
    }
    
    private void clearBoard() {
    	
    }
    
    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Connect4Game");
        final Parent res = createContentParent();
        primaryStage.setScene(new Scene(res, 580, 505));
        // Initial Starting Pane
        clearBoard();
//        Button btn = new Button();
//        btn.setText("Start Game");
//        btn.setOnAction(new EventHandler<ActionEvent>() {
//            public void handle(ActionEvent event) {
//                System.out.println("Starting Game");
//                primaryStage.setScene(new Scene(res, 600, 600));
//            }
//        });
//        StackPane starter = new StackPane();
//        starter.getChildren().add(btn);
//        primaryStage.setScene(new Scene(starter, 300, 250));
        primaryStage.setResizable(false);
        primaryStage.show();
        
        clientThread = new Thread(() -> {
        
	        scanner = new Scanner(System.in);
	
			try
			{
				socket = new Socket("localhost", Server.port);
	
				Log.printClient("Connected to Server on port " + Server.port);
				os = new ObjectOutputStream(socket.getOutputStream());
				is = new ObjectInputStream(socket.getInputStream());
	
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
				Log.printClient("Lost connection to host with message " + e.getMessage());
			}
			catch (IOException e)
			{
				Log.printClient("IOException with error " + e.getMessage());
			}
	
			while (!isTerminated)
			{
				//Keeps taking commands until 
				if (handleCommand(syncPrompt("> ")))
					handleResponse();
			}
	
			Log.println("Thanks for playing!");
			
			Platform.exit();
        });
        clientThread.start();
    }
    
    public void stop() {
    	isTerminated = true;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    //Client Stuff
    private Scanner scanner;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private Socket socket;
	private boolean isTerminated = false;
	
	//talks to HandleGameSession
		private void PlayGame(ObjectInputStream in, ObjectOutputStream out, Boolean isP1)
		{
			Platform.runLater(() -> clearBoard());
			
			Board playerBoard = new Board();
			Boolean p1Wins = null;
			
			//print the board first
			//playerBoard.printBoard();
			
			while(true)
			{
				try
				{
					if(isP1)
					{
						//get column input
						boolean input_fails = true;
						int myMove = 0;
						Log.printConsole("It is your turn now, enter an integer for column: ");
						
						//validate move
						while(input_fails)
						{
							try
							{
								myMove = Integer.parseInt(scanner.nextLine());
								playerBoard.placePiece(myMove, true);
								Platform.runLater(() -> placeCircle(6-playerBoard.getLastRow(), playerBoard.getLastCol()-1, "RED"));
								input_fails = false;
							}
							catch(NumberFormatException e)
							{
								Log.printConsole("Please enter an integer: ");
							}
							catch(RuntimeException e)
							{
								Log.printConsole("This is illegal move, please enter a valid move: ");
							}
						}
						
						
						//print board state after my move
						playerBoard.printBoard();
						
						//generate game command
						GameCommand p1GameMove = new GameCommand(myMove);
						
						//send game command
						out.writeObject(p1GameMove);
						
						//read server response
						GameCommand p1Response = (GameCommand)in.readObject();
						if(p1Response.isGameOver().booleanValue())
						{
							p1Wins = p1Response.isPlayer1Win();
							break;
						}
						
						//print waiting message
						Log.printConsole("Waiting for player2 to move...");
						
						GameCommand p2GameMove = (GameCommand)in.readObject();
						int otherMove = p2GameMove.getMove();
						playerBoard.placePiece(otherMove, false);
						Platform.runLater(() -> placeCircle(6-playerBoard.getLastRow(), playerBoard.getLastCol()-1, "YELLOW"));
						//print board state after my move
						//playerBoard.printBoard();
						Log.printConsole(p2GameMove.getResponse());
						
						if(p2GameMove.isGameOver().booleanValue())
						{
							p1Wins = p2GameMove.isPlayer1Win();
							break;
						}
					}
					else
					{
						//print waiting message
						Log.printConsole("Waiting for player1 to move...");
						
						GameCommand p1GameMove = (GameCommand)in.readObject();
						int otherMove = p1GameMove.getMove();
						playerBoard.placePiece(otherMove, true);
						Platform.runLater(() -> placeCircle(6-playerBoard.getLastRow(), playerBoard.getLastCol()-1, "RED"));
						//print board state after my move
						//playerBoard.printBoard();
						Log.printConsole(p1GameMove.getResponse());
						
						if(p1GameMove.isGameOver().booleanValue())
						{
							p1Wins = p1GameMove.isPlayer1Win();
							break;
						}
						
						//get column input
						boolean input_fails = true;
						int myMove = 0;
						Log.printConsole("It is your turn now, enter an integer for column: ");
						while(input_fails)
						{
							try
							{
								myMove = Integer.parseInt(scanner.nextLine());
								playerBoard.placePiece(myMove, false);
								Platform.runLater(() -> placeCircle(6-playerBoard.getLastRow(), playerBoard.getLastCol()-1, "YELLOW"));
								input_fails = false;
							}
							catch(NumberFormatException e)
							{
								Log.printConsole("Please enter an integer: ");
							}
							catch(RuntimeException re)
							{
								Log.printConsole("This is illegal move, please enter a valid move: ");
							}
						}
						
						//print board state after my move
						//playerBoard.printBoard();
						
						//generate game command
						GameCommand p2GameMove = new GameCommand(myMove);
						
						//send game command
						out.writeObject(p2GameMove);
						
						//read server response
						GameCommand p2Response = (GameCommand)in.readObject();
						if(p2Response.isGameOver().booleanValue())
						{
							p1Wins = p2Response.isPlayer1Win();
							break;
						}
					}
				}
				catch(ClassNotFoundException ce)
				{
					Log.printConsole(ce.getMessage());
				}
				catch(IOException ie)
				{
					ie.printStackTrace();
				}
			}
			
			//print victory message
			if(p1Wins == null)
			{
				//tie
				Log.printConsole("Tie");
			}
			else if(p1Wins.booleanValue() && isP1)
			{
				//player 1 wins
				Log.printConsole("You win!");
			}
			else if(p1Wins.booleanValue() && !isP1)
			{
				//player 1 loses
				Log.printConsole("You lose. Maybe try another round?");
			}
			else if(!p1Wins.booleanValue() && !isP1)
			{
				//player 2 wins
				Log.printConsole("You win!");
			}
			else
			{
				//player 2 loses
				Log.printConsole("You lose. Maybe try another round?");
			}
		}

	// Create a HashMap for:
	// When a user is not logged in
	// When a user is logged in, but not in a game
	// When a user is logged in and in a game
	
	//This HashMap is for unregistered users
	private final static HashMap<String, String[]> cmds = new HashMap<String, String[]>()
	{
		private static final long serialVersionUID = 1L;
		{
			put("help", new String[]
			{
					"register", "login", "quit", "guest", "play", "highscore"
			});
			put("register", new String[]
			{
					"{email} {password}"
			});
			put("login", new String[]
			{
					"{email} {password}"
			});
			put("quit", new String[0]);
			put("guest", new String[0]);
			put("play", new String[0]);
			put("highscore", new String[0]);
		}
	};
	
	//Takes the input from user and returns it as a command
	public String syncPrompt(String prompt)
	{
		Log.print(prompt);
		String response = scanner.nextLine();
		return response;
	}

	private void handleResponse()
	{

		try
		{
			//ClientCommand is an interface which gets/sets responses
			ClientCommand rawCmd = (ClientCommand) is.readObject();
			if (rawCmd.getResponse() != null && rawCmd.getResponse() != "")
				Log.printServer(rawCmd.getResponse());
			
			//handle the create lobby response
			if(rawCmd.getClass() == CreateLobbyCommand.class)
			{
				if(((CreateLobbyCommand)rawCmd).isSuccessful())
				{
					//waiting for a signal to start the game
					Log.printClient("Waiting for another player to join...");
					ObjectInputStream gameIn = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream gameOut = new ObjectOutputStream(socket.getOutputStream());
					ClientCommand startSignal = (ClientCommand)gameIn.readObject();
					Log.printClient(startSignal.getResponse());
					
					//all game logics go below
					PlayGame(gameIn, gameOut, ((StartGameCommand)startSignal).isPlayer1());
				}
			}
			else if(rawCmd.getClass() == JoinLobbyCommand.class)
			{
				if(((JoinLobbyCommand)rawCmd).isSuccessful())
				{
					//inform the player the game shall start
					Log.printClient("The game shall start in a moment...");
					ObjectInputStream gameIn = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream gameOut = new ObjectOutputStream(socket.getOutputStream());
					ClientCommand startSignal = (ClientCommand)gameIn.readObject();
					Log.printClient(startSignal.getResponse());
					
					
					//all games logic go below
					PlayGame(gameIn, gameOut, ((StartGameCommand)startSignal).isPlayer1());
				}
			}
		}
		catch (ClassNotFoundException e)
		{
			Log.printConsole("Failed to parse object from reading object stream. "
					+ e.getMessage());
		}
		catch (IOException e)
		{
			Log.printConsole(
					"Failed to read object from the connected socket. " + e.getMessage());
		}

	}

	//Takes the command from syncPrompt returns true if the right command is found
	//so that the request can be processed by handleResponse or returns false 
	//if it is a help request or the user quits
	//A false return will prompt the user for input again.
	private boolean handleCommand(String cmd)
	{

		String[] args = cmd.split("\\s+");
		ClientCommand command = null;

		//Check to see if the first word input is quit or disconnect. 
		//In which case, we terminate the program
		if (args[0].equals("quit") || args[0].equals("disconnect"))
		{
			isTerminated = true;
			return false;
		}
		
		//If the first word is help, we run the runHelpCommand
		else if (args[0].equals("help"))
		{
			runHelpCommand(args);
			return false;
		}
		
		//register command, which should be followed by an email and password
		else if (args[0].equals("register"))
		{
			command = new RegisterCommand(args[1], args[2]);
		}
		
		//login command, which should be followed by an email and password
		else if (args[0].equals("login"))
		{
			command = new LoginCommand(args[1], args[2]);
		}
		
		//high score command, which showcases the high score
		else if(args[0].equals("highscore"))
		{
			command = new GetHighScoresCommand();
		}
		
		//play the game command, which should prompt player for create lobby or join lobby
		else if(args[0].equals("play"))
		{
			//continue to interact as a guest
			Log.printConsole("You will start to play, if you haven't logged in, your scores will not be saved");
			Log.printConsole("Please select one of the following options:");
			Log.printConsole("1) Create a new game");
			Log.printConsole("2) Join an existing game");
			
			//decide to create a new game or join an existing one
			//also do error checking for input
			boolean input_fails = true;
			int option = 0;
			while(input_fails)
			{
				try
				{
					String line = scanner.nextLine();
					option = Integer.parseInt(line);
					
					if(option <= 0 || option > 2)
					{
						throw new RuntimeException();
					}
					
					input_fails = false;
				}
				catch(NumberFormatException ne)
				{
					Log.printConsole("Cannot parse and interger, enter a new option: ");
				}
				catch(RuntimeException re)
				{
					Log.printConsole("No such option, enter a new option: ");
				}
			}
			
			//send create lobby command
			if(option == 1)
			{
				Log.printConsole("Enter a new lobby name: ");
				String lobbyName = scanner.nextLine();
				command = new CreateLobbyCommand(lobbyName);
			}
			//send join lobby command
			else
			{
				Log.printConsole("Enter the lobby name to join: ");
				String lobbyName = scanner.nextLine();
				command = new JoinLobbyCommand(lobbyName);
			}
		}
		
		//unknown command input
		else
		{
			Log.println("Unknown command '" + cmd + "'");
		}

		// Run the command if it's not null
		if (command != null)
		{
			try
			{
				os.writeObject(command);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Log.printClient("Failed to write register command to the server. "
						+ e.getMessage());
			}
			return true;
		}

		return false;
	}

	//If the first word in the command is help, we handle the request
	//Ex. > help register
	//This will print out what you need to fill out when you use the 
	//command register.
	private void runHelpCommand(String[] args)
	{
		if (args.length > 1)
		{
			//If the command is found, proceed to give instructions
			//on how to use the command
			if (cmds.containsKey(args[1]))
			{
				Log.printConsole("Commands for " + args[1] + ": ");
				StringBuffer sb = new StringBuffer(args[1] + " [");
				
				//Looks in the HashMap for the command given and 
				//appends the input needed for the given command
				//to sb
				for (String subcmd : cmds.get(args[1]))
					sb.append(subcmd + ", ");
				Log.printConsole(sb.toString() + "]");
			}
			
			//Command is not in our HashMap
			else
			{
				Log.printConsole("Could not find help for command " + args[1]);
			}
		}
		
		//No command was given after the help keyword
		else
		{
			Log.println("**** Welcome to Connect4! ****\n");
			for (String cmd : cmds.keySet())
			{
				Log.print(cmd + ", ");
			}
			Log.println("\n****** * * **** * *  *******");
		}
	}
}