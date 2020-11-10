package multi_chat_by_json_server;

import java.io.*;
import java.util.*;
import java.net.*;

public class MySocketInfo {
	private Socket clientSocket;
	private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public MySocketInfo(Socket clientSocket) {
    	this.clientSocket = clientSocket;
    	try {
	    	oos = new ObjectOutputStream(clientSocket.getOutputStream());
	    }
	    catch (IOException e) {
            e.printStackTrace();
        }

        try {
	    	ois = new ObjectInputStream(clientSocket.getInputStream());
	    }
	    catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
    	return clientSocket;
    }

    public ObjectInputStream getOIS() {
    	return ois;
    }

    public ObjectOutputStream getOOS() {
    	return oos;
    }
}