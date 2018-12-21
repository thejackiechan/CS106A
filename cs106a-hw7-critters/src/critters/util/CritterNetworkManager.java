/*
 * CS 106A Critters
 * Handles network responsibilities in the critter game.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import stanford.cs106.net.*;
import stanford.spl.Base64;

public class CritterNetworkManager extends NetworkManager {
	// class constants
	public static final int DEFAULT_PORT_1 = 5142;
	public static final int DEFAULT_PORT_2 = 5143;

	// Constructs a CritterNetworkManager at the default port.
	public CritterNetworkManager() {
		super(DEFAULT_PORT_1);
	}

	// Constructs a CritterNetworkManager at the given port.
	public CritterNetworkManager(int port) {
		super(port);
	}

	// Sends out a message to the given host requesting that they send
	// their class with the given name back to us.
	public void requestClass(String host, String className) {
		send(host, getHostName(), className);
	}

	// Sends out the text of the given class to the given host.
	public void sendText(String host, String className, String fileText) {
		send(host, getHostName(), className, fileText);
	}
	
	public String uploadClass(String webServiceURL, String userName, String fileText, byte[] classBytes) {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("name", userName);
		params.put("text", fileText);
		params.put("class", Base64.encodeBytes(classBytes));
		String response = sendHttp(webServiceURL, params);
		return response;
	}

	/*
	 // Sends out the text of the given class to the given host.
	 public void sendClass(String host, String className, byte[] bytes) {
	 String encodedText = Base64.encodeToString(bytes);
	 send(host, getHostName(), className, encodedText);
	 }
	 */
}

