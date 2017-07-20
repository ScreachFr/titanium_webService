package services.servers.core;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observable;

import utils.rcon.RconAnswerReceiver;
import utils.rcon.RconClient;


// TODO notify observers
public class Server extends Observable {
	private String name;
	private String address;
	private int port;
	private String password;

	// Network
	private RconClient rcon;
	private Thread receiverThread;
	private RconAnswerReceiver receiver;

	// Stats
	private int maxCapacity;
	private long ping;
	
	private String currentMap;
	private String nextMap;
	
	public Server(String name, String address, int port, String password) {
		this.name = name;
		this.address = address;
		this.port = port;
		this.password = password;

		ping = -1;
		maxCapacity = -1;
		
		currentMap = "no data";
		nextMap = "no data";
	}




	public void connect() throws IllegalStateException, IOException, ConnectionFailureException, UnknownHostException {
		rcon = new RconClient(address, port);
		try {
			rcon.connect();
			rcon.authenticate(password);
		} catch (NegativeArraySizeException e) { // Happens when the host is unreachable.
			throw new ConnectionFailureException();
		}

		if (rcon.connected()) {
			System.out.println("Connection to " + address + ":" + port + " : OK");
		} else {
			System.out.println("Can't connect to " + address);
		}

		receiver = new RconAnswerReceiver(rcon);
		receiverThread = new Thread(receiver);



		receiverThread.start();
	}

	public void disconnect() {
		if (rcon.connected()) {
			try {
				rcon.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				receiverThread.interrupt();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	public void executeCommand(String command) {
		System.out.println("executing : " + command);

		try {
			rcon.executeCommand(command);
		} catch (IOException e) {
			System.out.println("Execution failed : " + e.getMessage());
		}
	}
	
	public String getAddress() {
		return address;
	}


	public int getPort() {
		return port;
	}


	public String getPassword() {
		return password;
	}


	public RconClient getRcon() {
		return rcon;
	}

	public String getName() {
		return name;
	}

	public boolean isConnected() {
		return rcon != null && rcon.connected();
	}

	public long getPing() {
		return ping;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public synchronized String getCurrentMap() {
		return currentMap;
	}
	
	public synchronized String getNextMap() {
		return nextMap;
	}
	
	public synchronized void setNextMap(String nextMap) {
		if (!this.nextMap.equals(nextMap)) {
			this.nextMap = nextMap;
			setChanged();
			notifyObservers(NotifyEventType.MAP_CHANGED);
		}
	}
	
	public synchronized void setCurrentMap(String currentMap) {
		if (!this.currentMap.equals(currentMap)) {
			this.currentMap = currentMap;
			setChanged();
			notifyObservers(NotifyEventType.MAP_CHANGED);
		}
	}
	
	public void setPing(long ping) {
		this.ping = ping;
		setChanged();
		notifyObservers(NotifyEventType.PING);
	}
	
	public void changeInformations(Server newInformations) {
		name = newInformations.getName();
		address = newInformations.getAddress();
		port = newInformations.getPort();
		password = newInformations.getPassword();
	}
	
	public String getNextAnswer() throws InterruptedException {
		return receiver.getNextAnswer();
	}
	
	public String getNextAnswer(long timeout) throws InterruptedException {
		return receiver.getNextAnswer(timeout);
	}
	
	@Override
	public String toString() {
		return name + " " + address + ":" + port;
	}
}
