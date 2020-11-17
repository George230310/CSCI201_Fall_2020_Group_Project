package edu.usc.csci201.connect4.gui;

import javafx.application.Application;

public class GUIRunner {
	public static void main(String[] args){
		// int[][] testBoard = new int[6][7];
		 new Thread() {
            @Override
            public void run() {
                javafx.application.Application.launch(Connect4GUI.class);
            }
        }.start();
	}
}