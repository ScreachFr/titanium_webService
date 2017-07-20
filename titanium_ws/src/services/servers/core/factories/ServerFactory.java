package services.servers.core.factories;

import services.servers.core.Server;

public class ServerFactory {
	public static Server newServer(String name, String address, int port, String password) {
		return new Server(name, address, port, password);
	}
}
