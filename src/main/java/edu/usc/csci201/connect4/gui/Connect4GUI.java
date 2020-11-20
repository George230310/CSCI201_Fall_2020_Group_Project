package edu.usc.csci201.connect4.gui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
	private Stage stage;
	
	private TextArea ta = new TextArea();
	private TextField tf = new TextField("");
	
	// TODO: setup fxml and controller to take in board parameter
	public void init() {
		Parameters params = getParameters();
	}
	
	// Add Board to StackPane
	private Parent createContentParent() {
		GridPane root = new GridPane();
		GridPane topPane = new GridPane();
		Shape theBoard = makeBoard();
		circlePane = new Pane();
		topPane.add(circlePane, 0, 0);
		topPane.add(theBoard,0 ,0);
		
		ta.setWrapText(true);
		ta.setEditable(false);
		tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode() == KeyCode.ENTER)  {
		            String text = tf.getText();
		            synchronized (input) {
		            	input = text;
		            }
		            tf.setText("");
		        }
		    }
		});
		
		topPane.add(ta, 1, 0);
		root.add(topPane, 0, 0);
		root.add(tf,0,1);
		return root;
	}
	
	// Make a board with holes
	private Shape makeBoard() {
		Shape board = new Rectangle(75 * (NUM_COLUMNS) - 15, 75 * (NUM_ROWS) - 15);
		
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
	
	//Add text to textbox
	private void addText(String text) {
		ta.appendText(text + "\n");
	}
	
	private String getText() {
		String response;
		while(true) {
			synchronized (input) {
				if(input != "") {
					response = input;
					input = "";
					break;
				}
			}
		}
		return response;
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
    	final Parent res = createContentParent();
        stage.setScene(new Scene(res, 980, 535));
    }
    
    @Override
    public void start(final Stage primaryStage) {
    	stage = primaryStage;
        primaryStage.setTitle("Connect4Game");
        //final Parent res = createContentParent();
        //primaryStage.setScene(new Scene(res, 580, 505)); //580 505
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
        //primaryStage.setResizable(false);
        primaryStage.show();
        
        clientThread = new Thread(() -> {
			try
			{
				socket = new Socket("localhost", Server.port);
	
				Platform.runLater(() -> addText("Connected to Server on port " + Server.port));
				os = new ObjectOutputStream(socket.getOutputStream());
				is = new ObjectInputStream(socket.getInputStream());
	
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
				Platform.runLater(() -> addText("Lost connection to host with message " + e.getMessage() + "\nPress enter to close window"));
				getText();
				isTerminated = true;
			}
			catch (IOException e)
			{
				Platform.runLater(() -> addText("IOException with error " + e.getMessage() + "\nPress enter to close window"));
				getText();
				isTerminated = true;
			}
			
			String welcome ="==========================================\n\n" +
							"           Welcome to Connect 4!          \n\n" +
							"==========================================\n" +
							"\nType help for a list of commands\n";
			Platform.runLater(() -> addText(welcome));
			
			while (!isTerminated)
			{
				//Keeps taking commands until 
				if (handleCommand(syncPrompt("")))
					handleResponse();
			}
	
			Platform.runLater(() -> addText("Thanks for playing!"));
			
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
	private ObjectOutputStream os;
	private ObjectInputStream is;
	private Socket socket;
	private boolean isTerminated = false;
	private String input = "";
	
	//talks to HandleGameSession
		private void PlayGame(ObjectInputStream in, ObjectOutputStream out, Boolean isP1)
		{
			Platform.runLater(() -> clearBoard());
			
			Board playerBoard = new Board();
			Boolean p1Wins = null;
			
			//print the board first
			//playerBoard.printBoard();
			
			while(!isTerminated)
			{
				try
				{
					if(isP1)
					{
						//get column input
						boolean input_fails = true;
						int myMove = 0;
						Platform.runLater(() -> addText("It is your turn now, enter an integer for column: "));
						
						//validate move
						while(input_fails)
						{
							try
							{
								myMove = Integer.parseInt(getText());
								playerBoard.placePiece(myMove, true);
								Platform.runLater(() -> placeCircle(6-playerBoard.getLastRow(), playerBoard.getLastCol()-1, "RED"));
								input_fails = false;
							}
							catch(NumberFormatException e)
							{
								Platform.runLater(() -> addText("Please enter an integer: "));
							}
							catch(RuntimeException e)
							{
								Platform.runLater(() -> addText("This is illegal move, please enter a valid move: "));
							}
						}
						
						
						//print board state after my move
						//playerBoard.printBoard();
						
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
						Platform.runLater(() -> addText("Waiting for player2 to move..."));
						
						GameCommand p2GameMove = (GameCommand)in.readObject();
						int otherMove = p2GameMove.getMove();
						playerBoard.placePiece(otherMove, false);
						Platform.runLater(() -> placeCircle(6-playerBoard.getLastRow(), playerBoard.getLastCol()-1, "YELLOW"));
						//print board state after my move
						//playerBoard.printBoard();
						Platform.runLater(() -> addText(p2GameMove.getResponse()));
						
						if(p2GameMove.isGameOver().booleanValue())
						{
							p1Wins = p2GameMove.isPlayer1Win();
							break;
						}
					}
					else
					{
						//print waiting message
						Platform.runLater(() -> addText("Waiting for player1 to move..."));
						
						GameCommand p1GameMove = (GameCommand)in.readObject();
						int otherMove = p1GameMove.getMove();
						playerBoard.placePiece(otherMove, true);
						Platform.runLater(() -> placeCircle(6-playerBoard.getLastRow(), playerBoard.getLastCol()-1, "RED"));
						//print board state after my move
						//playerBoard.printBoard();
						Platform.runLater(() -> addText(p1GameMove.getResponse()));
						
						if(p1GameMove.isGameOver().booleanValue())
						{
							p1Wins = p1GameMove.isPlayer1Win();
							break;
						}
						
						//get column input
						boolean input_fails = true;
						int myMove = 0;
						Platform.runLater(() -> addText("It is your turn now, enter an integer for column: "));
						while(input_fails)
						{
							try
							{
								myMove = Integer.parseInt(getText());
								playerBoard.placePiece(myMove, false);
								Platform.runLater(() -> placeCircle(6-playerBoard.getLastRow(), playerBoard.getLastCol()-1, "YELLOW"));
								input_fails = false;
							}
							catch(NumberFormatException e)
							{
								Platform.runLater(() -> addText("Please enter an integer: "));
							}
							catch(RuntimeException re)
							{
								Platform.runLater(() -> addText("This is illegal move, please enter a valid move: "));
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
					Platform.runLater(() -> addText(ce.getMessage()));
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
				Platform.runLater(() -> addText("Tie"));
			}
			else if(p1Wins.booleanValue() && isP1)
			{
				//player 1 wins
				Platform.runLater(() -> addText("You win!"));
			}
			else if(p1Wins.booleanValue() && !isP1)
			{
				//player 1 loses
				Platform.runLater(() -> addText("You lose. Maybe try another round?"));
			}
			else if(!p1Wins.booleanValue() && !isP1)
			{
				//player 2 wins
				Platform.runLater(() -> addText("You win!"));
			}
			else
			{
				//player 2 loses
				Platform.runLater(() -> addText("You lose. Maybe try another round?"));
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
			put("play", new String[0]);
			put("highscore", new String[0]);
		}
	};
	
	//Takes the input from user and returns it as a command
	public String syncPrompt(String prompt)
	{
		Log.print(prompt);
		String response = getText();
		
		return response;
	}

	private void handleResponse()
	{

		try
		{
			//ClientCommand is an interface which gets/sets responses
			ClientCommand rawCmd = (ClientCommand) is.readObject();
			if (rawCmd.getResponse() != null && rawCmd.getResponse() != "")
				Platform.runLater(() -> addText(rawCmd.getResponse()));
			
			//handle the create lobby response
			if(rawCmd.getClass() == CreateLobbyCommand.class)
			{
				if(((CreateLobbyCommand)rawCmd).isSuccessful())
				{
					//waiting for a signal to start the game
					Platform.runLater(() -> addText("Waiting for another player to join..."));
					ObjectInputStream gameIn = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream gameOut = new ObjectOutputStream(socket.getOutputStream());
					ClientCommand startSignal = (ClientCommand)gameIn.readObject();
					Platform.runLater(() -> addText(startSignal.getResponse()));
					
					//all game logics go below
					PlayGame(gameIn, gameOut, ((StartGameCommand)startSignal).isPlayer1());
				}
			}
			else if(rawCmd.getClass() == JoinLobbyCommand.class)
			{
				if(((JoinLobbyCommand)rawCmd).isSuccessful())
				{
					//inform the player the game shall start
					Platform.runLater(() -> addText("The game shall start in a moment..."));
					ObjectInputStream gameIn = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream gameOut = new ObjectOutputStream(socket.getOutputStream());
					ClientCommand startSignal = (ClientCommand)gameIn.readObject();
					Platform.runLater(() -> addText(startSignal.getResponse()));
					
					
					//all games logic go below
					PlayGame(gameIn, gameOut, ((StartGameCommand)startSignal).isPlayer1());
				}
			}
		}
		catch (ClassNotFoundException e)
		{
			Platform.runLater(() -> addText("Failed to parse object from reading object stream. "
					+ e.getMessage()));
		}
		catch (IOException e)
		{
			Platform.runLater(() -> addText(
					"Failed to read object from the connected socket. " + e.getMessage()));
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
			Platform.runLater(() -> addText("You will start to play, if you haven't logged in, your scores will not be saved"));
			Platform.runLater(() -> addText("Please select one of the following options:"));
			Platform.runLater(() -> addText("1) Create a new game"));
			Platform.runLater(() -> addText("2) Join an existing game"));
			
			//decide to create a new game or join an existing one
			//also do error checking for input
			boolean input_fails = true;
			int option = 0;
			while(input_fails)
			{
				try
				{
					String line = getText();
					option = Integer.parseInt(line);
					
					if(option <= 0 || option > 2)
					{
						throw new RuntimeException();
					}
					
					input_fails = false;
				}
				catch(NumberFormatException ne)
				{
					Platform.runLater(() -> addText("Cannot parse and interger, enter a new option: "));
				}
				catch(RuntimeException re)
				{
					Platform.runLater(() -> addText("No such option, enter a new option: "));
				}
			}
			
			//send create lobby command
			if(option == 1)
			{
				Platform.runLater(() -> addText("Enter a new lobby name: "));
				String lobbyName = getText();
				command = new CreateLobbyCommand(lobbyName);
			}
			//send join lobby command
			else
			{
				Platform.runLater(() -> addText("Enter the lobby name to join: "));
				String lobbyName = getText();
				command = new JoinLobbyCommand(lobbyName);
			}
		}
		
		//unknown command input
		else
		{
			Platform.runLater(() -> addText("Unknown command '" + cmd + "'"));
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
				Platform.runLater(() -> addText("Failed to write register command to the server. "
						+ e.getMessage()));
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
				if(cmds.get(args[1]).length != 0) {
					Platform.runLater(() -> addText("Arguments for " + args[1] + ": "));
					StringBuffer sb = new StringBuffer(args[1] + " ");
					
					//Looks in the HashMap for the command given and 
					//appends the input needed for the given command
					//to sb
					for (String subcmd : cmds.get(args[1]))
						sb.append(subcmd + ", ");
					sb.delete(sb.length() - 2, sb.length());
					Platform.runLater(() -> addText(sb.toString()));
				} else {
					Platform.runLater(() -> addText("No arguments for " + args[1] + " command"));

				}
			}
			
			//Command is not in our HashMap
			else
			{
				Platform.runLater(() -> addText("Could not find help for command " + args[1]));
			}
		}
		
		//No command was given after the help keyword
		else
		{
			Platform.runLater(() -> addText("================= Commands ================="));
			String printme = "";
			for (String cmd : cmds.keySet())
			{
				printme += cmd + ", ";
			}
			final String printme2 = printme.substring(0, printme.length() - 2);
			Platform.runLater(() -> addText(printme2));
			Platform.runLater(() -> addText("\nType help {command} for additional info"));
			Platform.runLater(() -> addText("============================================"));
		}
	}
}