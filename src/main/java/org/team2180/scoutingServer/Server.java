package org.team2180.scoutingServer;
import java.io.IOException;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.json.JSONObject;

import javax.bluetooth.UUID;;
public class Server {
	
	public static final UUID serviceUUID = new UUID("94f39d297d6d437d973bfba39e49d4ee", false);
	public static String connectionString = "btspp://localhost:" + serviceUUID.toString() +";name=FRCScouting";
	public static boolean serverStarted= false;
	static LocalDevice locDev;//Lockdev monster
	public static final JSONObject TEAM_DATA = new JSONObject();
	public static void main(String[] args) {
		try {
			
			locDev = LocalDevice.getLocalDevice();
			System.out.println("Local Device: '" + locDev.getFriendlyName()+"' @ "+locDev.getBluetoothAddress());
			StreamConnectionNotifier streamConnNot = startServer();
			startListening(streamConnNot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static StreamConnectionNotifier startServer() throws Exception {
		if(serverStarted){return null;}
		boolean isNowDiscoverable = locDev.setDiscoverable(0x00);
		System.out.println("Local Device is Discoverables: "+locDev.getDiscoverable());
		System.out.println("Local Device URI: "+connectionString);
		
		StreamConnectionNotifier streamConnNot = (StreamConnectionNotifier) Connector.open(connectionString);
		System.out.println("Server: Created, waiting for clients . . . ");
		return streamConnNot;
	}
	
	public static void startListening(StreamConnectionNotifier streamConnNot) throws IOException {
		while(true) {
			StreamConnection connection = streamConnNot.acceptAndOpen();
			Thread connectedThread = new Thread(new ConnectionHandler(connection, TEAM_DATA));
			System.out.println("Sever: found a client, placed on thread:" + connectedThread.getId());
			connectedThread.start();
		}
	}
}
