package jav745.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 

/**
 * This WebServer class is the main class.
 * @author Yuhang Zhao, student number 150467199
 */
public class WebServer {
	//create clientIdentifier for every client and use it into the serverTranscation file
	public static int clientIdentifier = 0;
	public static int fileClientIdentifier = 0;
	
	/**
	 * read information from serverInformation.csv file, extract and store them in to a List storing some Concert objects 
	 * @param inputFile
	 * @return List<Concert>
	 */
	public static List<Concert> createConcerts(String inputFile) {
		Path path = Paths.get(inputFile);
		File file = new File(inputFile);
		List<Concert> concertList = new ArrayList<>();
		try(Scanner s = new Scanner(path)){
			if(file.length() == 0) throw new EmptyFileException("File is empty");
			while(s.hasNextLine()) {
				String line = s.nextLine();
				String[] lineArray = line.split(",");
				
				//extract the venue information
				String[] seatTypeArray = lineArray[3].split("\\s+");
				String[] seatPriceArray = lineArray[4].split("\\s+");
				String[] seatNumberArray = lineArray[5].split("\\s+");
				
				//create Seat objects
				List<Seat> seatList = new ArrayList<>();
				for(int i = 0; i<seatTypeArray.length;i++) {
					Seat seat = new Seat(seatTypeArray[i], Double.parseDouble(seatPriceArray[i]), Integer.parseInt(seatNumberArray[i]));
					seatList.add(seat);
				}
				
				//create Venue object
				Venue venue = new Venue(lineArray[2], seatList);
				
				//create Concert object
				Concert concert = new Concert(lineArray[0], lineArray[1], venue, Double.parseDouble(lineArray[6]));
				concertList.add(concert);
			}
		}catch(NoSuchFileException e) {
			System.err.println(e);
		}catch(EmptyFileException e) {
			System.err.println(e);
		}catch(IOException e) {
			System.err.println("IOException");
		}
	
		return concertList;
	}
	
	/**
	 * This static extractSeatNum method is to extract the number of seat from the order.
	 * @param lineArray
	 * @return numOfSeat(an int array)
	 */
	public static int[] extractSeatNum(String[] lineArray) {
		int[] numOfSeat = new int[lineArray.length-3];
		for(int i=3; i<lineArray.length; i++) {
			String[] seatString = lineArray[i].split("\\s+");
			numOfSeat[i-3] = Integer.parseInt(seatString[0]);
		}
		return numOfSeat;
	}
	
	/**
	 * This static serverTransactionFile method is to write the result of each transaction into a file called 
	 * "serverTranscation.txt".
	 * @param transaction
	 */
	public static void serverTransactionFile(String transaction) {
		try {
			fileClientIdentifier++;
            FileWriter writer = new FileWriter("serverTransaction.txt", true);
            writer.write("ClientIndentifier: "+WebServer.fileClientIdentifier+"\n"+transaction+"\n"+WebServer.currentTime()+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * This static currentTime method is to return one string standing for current time, which is used by the 
	 * above serverTransactionFile method.
	 * @return one string standing for current time.
	 */
	public static String currentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now);  
	}
	
	/**
	 * This is the main class.
	 * @param String[] args
	 * args[0]--server port number
	 * args[1]--input file
	 */
	public static void main(String[] args) {
		int portNumber = Integer.parseInt(args[0]);
		String inputFile = args[1];
		//check if the command follows the right format  
		if(args.length != 2) {
			System.err.println("Usage: java WebServer <port number> <inputFile>");
			System.exit(1);
		}
		//call the createConcerts method to return a concertList
		List<Concert> concertList = WebServer.createConcerts(inputFile);
		
		//establish connection with Client
			try(
					// create server socket object	
					ServerSocket serverSocket = new ServerSocket(portNumber, 100);
					)
			{		 
					while(true) {
						//create new thread, request is processed in thread, for processing requests from multiple clients concurrently
						new ServerThread(serverSocket.accept(), concertList).start();
					}
			} catch(IOException e) {
				System.out.println("Exception caught when trying to listen on port" + portNumber + " or listening for a conncetion");
				System.exit(1);
			}
	}

}
