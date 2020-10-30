package Client;

import java.io.*;
import java.net.*;
import java.util.*;

public class Player implements Runnable{
	
	String hostname;
	int myPort;
	private Socket serverSocket;
	private PrintWriter toServer;
	private BufferedReader fromServer;
	
	public Player(String host, int port)
	{
		hostname = host;
		myPort = port;
	}
	
	public void run()
	{
		try
		{
			//establish connections with the server
			serverSocket = new Socket(hostname, myPort);
			fromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			toServer = new PrintWriter(serverSocket.getOutputStream());
			
			//read username prompt from server
			System.out.println(fromServer.readLine());
			
			//-----haven't done yet
			serverSocket.close();
			fromServer.close();
			toServer.close();
		}
		catch(UnknownHostException ue)
		{
			System.out.println("Unknown host");
		}
		catch(IOException ie)
		{
			ie.printStackTrace();
		}
	}
}
