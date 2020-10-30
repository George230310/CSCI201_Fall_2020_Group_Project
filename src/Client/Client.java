package Client;

import Server.Server;

public class Client {

	public static void main(String[] args) {
		new Thread(new Player(Server.serverName, Server.serverPort)).start();
	}

}
