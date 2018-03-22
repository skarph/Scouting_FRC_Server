package org.team2180.scoutingServer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.io.Files;

import javax.bluetooth.UUID;

/**
 * 
 * @author root
 *
 */
public class Server {
	//Intialize variables
	public static final UUID serviceUUID = new UUID("94f39d297d6d437d973bfba39e49d4ed", false);//Server UUID, must be harcoded onto both server and client
	public static String connectionString = "btspp://localhost:" + serviceUUID.toString() +";name=FRCScouting";
	public static boolean serverStarted= false;
	static LocalDevice locDev;//Lockdev monster (Local device to be defined in main)
	public static JSONObject TEAM_DATA = new JSONObject();//Keeps master record of all
	public static File exportFile;
	public static File loadData;
	public static double saveTime = 0.0;
	public static boolean saveAtConnection = false;
	//data in an easy-to-write-out JSON format
	
	public static void main(String[] args) {
		
		if(args.length!=3) {
			System.out.println("!!Not valid arguments!!\n"+HELP_INFO);
			return;
		}
		try {
			loadData = new File(args[0]);
			if(!loadData.exists()) {System.out.println("!!Can't find the load file, did you type the name correctly!!\n"+HELP_INFO);return;}
			if(!loadData.canRead()){System.out.println("!!Can't read from the load file, did you give the program proper permissions!!\n"+HELP_INFO);return;}
			exportFile = new File(args[1]);
			if(!exportFile.exists()) {
				if(exportFile.createNewFile()) {
					if(!exportFile.canWrite()){//Somemone has messed up permissions
						System.out.println("!!Can't write to the newly created file, did you give the program proper permissions!!\n"+HELP_INFO);
						exportFile.delete();
						return;
					}else {
						//Success!
					}
				}else{System.out.println("!!Could not create an export file, did you give the program proper permisssions!!\n"+HELP_INFO);return;}
			}else{System.out.println("!!Export file exists, please remove!!+\n"+HELP_INFO);return;}
			
			if(Double.parseDouble(args[2])==0.0) {
				saveTime = -1.0;
			}else if(Double.parseDouble(args[2])==-1.0) {
				saveTime = -1.0;
				saveAtConnection = true;
			}else {
				saveTime = Double.parseDouble(args[2]);
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(HELP_INFO);
			return;
		}
		try {
			TEAM_DATA = new JSONObject(Files.asCharSource(loadData, Charset.defaultCharset()));
		} catch (Exception err) {
			err.printStackTrace();
			System.out.println(HELP_INFO);
			return;
		}
		if(!saveAtConnection && saveTime!=-1.0) {
			ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
			exec.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					System.out.println("[AUTOSAVE]: Saving...:");
					try {
						FileWriter fW = new FileWriter(exportFile);
						fW.write(TEAM_DATA.toString());
						fW.flush();
					} catch (IOException e) {
						System.out.println("[AUTOSAVE]: Couldn't save because:");
						e.printStackTrace();
						return;
					}
					System.out.println("[AUTOSAVE]: Done, next autosave in: "+saveTime+" minutes!");
				}
			},0, (long) saveTime, TimeUnit.MINUTES);
		}
		try {
			locDev = LocalDevice.getLocalDevice();
			System.out.println("Local Device: '" + locDev.getFriendlyName()+"' @ "+locDev.getBluetoothAddress());
			StreamConnectionNotifier streamConnNot = startServer();//Intializes server
			startListening(streamConnNot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static StreamConnectionNotifier startServer() throws Exception {
		if(serverStarted){return null;}
		boolean isNowDiscoverable = locDev.setDiscoverable(DiscoveryAgent.GIAC);
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
	public static final String HELP_INFO = "Welcome to the Bluetooth FIRST Scouting tool by Max Neyraval (2018)!\n"
			+ "Simple program to handle scouters over bluetooth. Please turn on the bluetooth radio and give the program the appropriate permissions!\n\n"
			+ "Anyway, here's the proper usage of this program:\n\n"
			+ "java -jar ScoutingBluetoothServer.jar <LOAD DATA> <EXPORT PATH> <TIME BETWEEN SAVES>\n\n"
			+ "Where <LOAD DATA> is a valid path to valid JSON data that can be loaded\n"
			+ "\tData is a JSON file that either has been exported or has team initialization data. See example.\n"
			+ "Where <EXPORT PATH> is a path to the desired output (make sure you have proper permissions!)\n"
			+ "And Where <TIME BETWEEN SAVES> Is the time between autoexporting data, in minutes\n"
			+ "\t Set this value to 0 to not export OR -1 to export every time a client issues a connection.\n";
}
