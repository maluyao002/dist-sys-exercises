/******************************************************************************
 *  Author: Luyao Ma, Yu Ma
 *  CS 6421 - Multiple Conversation
 *  Compilation:  javac ProxyServer.java
 *  Execution:    java ProxyServer. portnumber
 *  Description: This is a proxy server that conncects five convesion server to fullfill the function of transferring grams of bananas to yens.
 ******************************************************************************/

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DiscoverServer {
	
	static HashMap hash = new HashMap();
	static HashMap hashtohash = new HashMap();
	
	private static boolean isInteger(String str) {    
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");    
        return pattern.matcher(str).matches();    
    }
	
    public static void process (Socket clientSocket) throws IOException {
        // open up IO streams
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        /* read and print the client's request */
        // readLine() blocks until the server receives a new line from client
        String userInput;
        userInput = in.readLine();
        //check if user input is valid
        if (userInput  == null) {
            System.out.println("Error reading message");
            out.close();
            in.close();
            clientSocket.close();
        }
        System.out.println("Received message: " + userInput);
        
        // spit input into an string array
        String[] tokens = userInput.split(" ");
        switch (tokens[0]){
        	case "ADD":{
        		//server register, the server register protocol is "ADD UNIT1 UNIT2 IP_address port_num"
        		if(tokens.length != 5 && !isInteger(tokens[4])){
        			out.println("FAILURE: INVALIDINPUT");
                    break;
        		}
                String key = tokens[1] + " "+ tokens[2];
                String serverInfo = tokens[3]+ " "+tokens[4];
                
                try{
                	register(key, serverInfo);
                	out.println("SUCCESS");
                }catch(Exception e){
                	out.println("FAILURE");
                }
                break;
            }
        	case "REMOVE":{
        		if(tokens.length != 3 && !isInteger(tokens[2])){
        			out.println("FAILURE: INVALIDINPUT");
                    break;
        		}
                //server remove, the remove protocol is "REMOVE IPaddress, port_num"
        		String target = tokens[1] + " " + tokens[2];
                if (delete(target)){
                	out.println("SUCCESS");
                }else{
                	out.println("FAILURE");
                }
                break;
        	}
        	case "LOOKUP":{
        		//search table for server, the input from user must obey the protocol" LOOKUP UNIT1 UNIT2"
        		if(tokens.length != 3){
        			out.println("FAILURE: INVALIDINPUT");
                    break;
        		}
                String search1 = tokens[1] + " "+ tokens[2];
                String search2 = tokens[2] + " "+ tokens[1];
                String serverInfo1 = searchServer(search1);
                String serverInfo2 = searchServer(search2);
                if (serverInfo1 == null && serverInfo2 == null){
                	out.println("none");
                }else if(serverInfo1 == null){
                	out.println(serverInfo2);
                }else{
                	out.println(serverInfo1);
                }
                break;
            }
        	default:
        		out.println("Input format is incorrect!");
        }
        // close IO streams, then socket
    	out.close();
    	in.close();
    	clientSocket.close();
    }
    
    public static String searchServer(String target){
    	String result;
    	String[] array;
    	try{
    		ArrayList a = (ArrayList)hash.get(target);
    		if(a == null){
    			return null;
    		}else{
    			array = (String[])a.toArray(new String[a.size()]);
    			result = array[0];
    		}
    		System.out.println("Search succeeded!");
    	}catch(Exception e){
    		return null;
    	}
		return result;
    }
    
    //Here is the reverse conversion function, which connect servers in a reverse order
    public static boolean register(String key, String value){
    		try{
    			if (hash.get(key) == null){
    				ArrayList hashArray = new ArrayList();
    				hash.put(key, hashArray);
    				hashArray.add(value);
    				hashtohash.put(value, key);
    			}else{
    				ArrayList hashArray = (ArrayList) hash.get(key);
    				hashArray.add(value);
    				hashtohash.put(value, key);
    				for(int i = 0; i<hashArray.size(); i++){
    					System.out.println(hashArray.get(i));
    				}
    			}
    			System.out.println("Server registed!");
    		}catch (Exception e){
    			return false;
    		}
    		return true;     
    }
    
    public static boolean delete(String key){
    	try{
    		String serverinfo = (String) hashtohash.get(key);
    		ArrayList removetar = (ArrayList) hash.get(serverinfo);
    		for(int i = 0; i<removetar.size(); i++){
    			if(removetar.get(i).equals(key)){
    				removetar.remove(i);
    				break;
    			}else return false;
    		}
    		System.out.println("Server log out!");
    	}catch(Exception e){
    		return false;
    	}
		return true;
    }
    public static void main(String[] args) throws Exception {

        //check if argument length is invalid
        if(args.length != 1) {
            System.err.println("Usage: java DiscoverServer port");
        }
        // create socket
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Started server on port " + port);
        // wait for connections, and process
        try {
            while(true) {
                // a "blocking" call which waits until a connection is requested
                Socket clientSocket = serverSocket.accept();
                System.err.println("\nAccepted connection from client");
                process(clientSocket);
            }

        }catch (IOException e) {
            System.err.println("Connection Error");
        }
        System.exit(0);
    }
}
