package edu.usc.csci201.connect4.gui;

import java.util.Scanner;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
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
	private int[][] board;
	private Pane circlePane = new Pane(); // circles represent player moves
	
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
		
		// Dummy Button for Testing placeCircle
		Button btn = new Button();
        btn.setText("Make Move");
		btn.setLayoutX(250);
		btn.setLayoutY(550);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { // input via terminal
                Scanner sc = new Scanner(System.in); 
                System.out.println("Enter row #");
                String row = sc.nextLine();
                System.out.println("Enter col #");
                String col = sc.nextLine();
                System.out.println("Enter color in CAPS");
                String color = sc.nextLine();
                placeCircle(Integer.parseInt(row), Integer.parseInt(col), color);
                System.out.println("Move Placed at [ " + row + "," + col + "] by color " + color);
            }
        });
		root.getChildren().add(btn);
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
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Connect4Game");
        // Initial Starting Pane
        Parent res = createContentParent();
        Button btn = new Button();
        btn.setText("Start Game");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Starting Game");
                primaryStage.setScene(new Scene(res, 600, 600));
            }
        });
        StackPane starter = new StackPane();
        starter.getChildren().add(btn);
        primaryStage.setScene(new Scene(starter, 300, 250));
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}