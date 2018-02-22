package org.team2180.scoutingServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.microedition.io.StreamConnection;
import javax.bluetooth.RemoteDevice;
import org.json.*;

public class ConnectionHandler implements Runnable {
	final StreamConnection connection;
	final JSONObject TEAM_DATA;
	RemoteDevice remDev;
	String deviceIndex;
	public ConnectionHandler(StreamConnection connection,JSONObject TEAM_DATA) {
		this.connection = connection;
		this.TEAM_DATA = TEAM_DATA;
		try {
			this.remDev = RemoteDevice.getRemoteDevice(connection);
			this.deviceIndex = remDev.getFriendlyName(true)+'@'+remDev.getBluetoothAddress();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.remDev = null;
			this.deviceIndex = null;
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method
		try {
			System.out.println("Server: Found a friend in '"+remDev.getFriendlyName(true)+"' @ "+remDev.getBluetoothAddress());
			OutputStream out = connection.openOutputStream();
			InputStream in = connection.openInputStream();
			PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(out));
			BufferedReader bReader = new BufferedReader(new InputStreamReader(in)); 
				
			int handshake = in.read();
			if(handshake==1) {
				System.out.println(deviceIndex+" will now inform you of TOP SECRET_INFO");
				updateDatabase(remDev, pWriter, bReader);
				System.out.println(deviceIndex+" >\n"+ TEAM_DATA.getJSONObject(deviceIndex).getInt("entryCount"));
			}else if(handshake==2) {
				System.out.println(deviceIndex+" would like to copy your notes");
				updateDevice(remDev, pWriter, bReader);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(deviceIndex+"'s thread is terminating BADLY!");
			try {connection.close();} catch (IOException e1) {e1.printStackTrace();}
			return;
		}
		System.out.println(deviceIndex+"'s thread is terminating!");
		return;
	}
	
	public void updateDatabase(RemoteDevice remDev, PrintWriter ex, BufferedReader in) throws IOException, JSONException {
		//OK!
		ex.write(1);
		ex.flush();
		char[] charData = new char[8192];
		
		in.read(charData);
		String data = new String(charData);
		
		connection.close();
		//Connection should end here.
		if(data!=null) {	
		}
		JSONObject deviceLocalTable;
		
		try{
			deviceLocalTable = TEAM_DATA.getJSONObject(deviceIndex);
		}catch(JSONException e) {
			//e.printStackTrace();
			System.out.println(deviceIndex+" has no home. Sad!");
			//Give the device's data a local home, and put data in there. No need to check for dupilicates
			TEAM_DATA.put(deviceIndex, new JSONObject());
			deviceLocalTable = TEAM_DATA.getJSONObject(deviceIndex);
			deviceLocalTable.put("0", data);
			deviceLocalTable.put("entryCount", 1);
			return;
		}
		
		int i = 0;
		while(i<deviceLocalTable.getInt("entryCount")){
			//Make sure we don't have duplicates!
			String jsonText = (String)deviceLocalTable.get(i+"");
			if(jsonText.equals(data)) {
				//Don't save duplicates!
				System.out.println(deviceIndex+"NOC");
				return;
			}
			i++;
		}
		deviceLocalTable.put(i+"", data);
		deviceLocalTable.put("entryCount", deviceLocalTable.getInt("entryCount")+1);
		System.out.println(deviceIndex+" had OC");
		return;
	}
	public void updateDevice(RemoteDevice remDev, PrintWriter ex, BufferedReader in) {
		
	}

}
