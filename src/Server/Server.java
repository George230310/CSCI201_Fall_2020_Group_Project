package Server;

import java.util.concurrent.*;
import java.util.*;
import java.io.IOException;
import java.net.*;

public class Server {
	
	public static int serverPort = 8080;
	public static String serverName = "localhost";
	protected static ConcurrentHashMap<String, ArrayList<ServerThread>> nameToServerThreads = new ConcurrentHashMap<>();
	
	//main method for server class
	public static void main(String[] args)
	{
		//set up configurations here???
		
		
		/****************************************
		 Establish connection to firestore here ???
		 
		 
		 
		*****************************************/
		
		//establish server socket
		try(ServerSocket ss = new ServerSocket(serverPort))
		{
			System.out.println(TimestampUtil.getTimestamp() + " Server started at port: " + serverPort);
			//looping and accept unlimited number of players
			while(true)
			{
				Socket newPlayerSocket = ss.accept();
				System.out.println(TimestampUtil.getTimestamp() + " Accepted a new player");
				new ServerThread(newPlayerSocket).start();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}