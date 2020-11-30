package jav745.client;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;

/**
 * The Client class is the main class. 
 * @author Yuhang Zhao, student number 150467199
 */
public class Client {
	//create one static instance variable accountBalance standing for the money that all clients can use to pay tickets
	private static double accountBalance = 100000;
	
	/**
	 * This static clientTranscationFile method is to write the result of each transaction into a file called 
	 * "clientTranscation.txt".
	 * @param transaction
	 */
	private static void clientTranscationFile(String transaction) {
		try {
            FileWriter writer = new FileWriter("clientTransaction.txt", true);
            writer.write(transaction+"\n"+Client.currentTime()+"\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


	}
	
	/**
	 * This static currentTime method is to return one string standing for current time, which is used by the 
	 * above clientTranscationFile method.
	 * @return one string standing for current time.
	 */
	private static String currentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now);  
	}
	
	/**
	 * This is the main function for Client class. 
	 * This main method is getting orders or requests from the configurable files(orderInforamtion1.csv and 
	 * orderInformation2.csv).Communicate with the WebServer to process these requests. Check if these information
	 * in requests are valid, if the client has enough money to pay and etc.
	 * No matter the transaction succeeds or not, the result of transaction would be recored in the clientTranscation.txt
	 * file and displayed on the screen.  
	 * @param String[] args
	 * args[0]--server address
	 * args[1]--server port number
	 * args[2]--configuration file
	 * 
	 */
	public static void main(String[] args) {
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		String configFile = args[2];
		
		//check if the command follows the right format 
		if(args.length != 3) {
			System.err.println(
				"Usage: java Client <host name> <port number> <configuration file>");
			System.exit(1);
		}
		
		//Firstly read the orderInformation files and upload these requests into this client program
		Path path = Paths.get(configFile);
		try(Scanner s = new Scanner(path)) {
			while(s.hasNextLine()) {
				String line = s.nextLine();
				
				//process the request information
				String[] lineArray = line.split(",");
				int[] numOfSeat = new int[lineArray.length-3];
				for(int i=3; i<lineArray.length; i++) {
					String[] seatString = lineArray[i].split("\\s+");
					numOfSeat[i-3] = Integer.parseInt(seatString[0]);
				}
				
				//establish connection with WebServer
				try(
						//create socket which provides connection to WebServer
						Socket socket = new Socket(hostName, portNumber);
						//get access to i/o streams connected to server
						PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						)
				{
					 System.out.println("Try to send the order information to server: \n" + line);
					 out.println(line);//send the request to the WebServer 
					 
					 //get the input from WebServer
					 String inputFromServer = in.readLine();
					 //process and extract information
					 String[] readArray = inputFromServer.split(",");
					 //This case shows the WebServer tells the Client there are enough tickets for this request
					 if (readArray[0].equalsIgnoreCase("y")) {
						 System.out.println("Order has been accepted by the WebServer. Please pay the amount of money");
						 System.out.println("You need pay "+ readArray[1] );//show how much the client needs to pay for this request
						 //check if client still has enough money to pay
						 if(Double.parseDouble(readArray[1]) < accountBalance) {
							 accountBalance = accountBalance - Double.parseDouble(readArray[1]);
							//ask client if he wants to pay? 
							 Scanner keyboard = new Scanner(System.in);
							 System.out.println("Do you want to pay?");
							 //calculate the time between the client response to the WebServer, if the wait time exceeds 60 seconds, this 
							 //this request would be aborted on both sides
							 long startTime = System.currentTimeMillis();
							 String buyOrnot = keyboard.next();
							 long endTime = System.currentTimeMillis();
							 long waitTime = endTime - startTime;
							 
							 //check if the time spent for paying exceeds 60 seconds
							 if(waitTime > 60000) {
								 System.out.println("Your order is automatically canceled. You spent more than 60 seconds to pay.\n");
								 Client.clientTranscationFile("Failed: The order is automatically canceled, since the client spent more than 60 seconds to pay.");
								 out.println("Failed: The order is automatically canceled, since the client spent more than 60 seconds to pay.");
								 in.readLine();
								 
							 }else {
								 //this case is that client enter "yes", he paid his own order/request.
								 if (buyOrnot.equals("yes")) {
									 Client.clientTranscationFile("Succeessful: "+ line );
									 out.println("y");
									 System.out.println(in.readLine()+"\n");
									 //continue;
								 }
								 //this case is that client enter other things, showing he didn't want to pay. In other words, he canceled his order by himself.
								 else {
									 System.out.println("Client himself cancels this tickets order");
									 Client.clientTranscationFile("Failed: Client cancels this tickets order");
									 out.println("n,Client cancels this order");
									 in.readLine();
								 }
							 }
						 }
						 //this case is that client doesn't have enough money to pay his order.
						 else {
							 System.out.println("No enough money to pay these tickets");
							 Client.clientTranscationFile("Failed: No enough money to pay these tickets");
							 out.println("n,No enough money to pay these tickets");
							 in.readLine();
						 }
					 }
					 //this case is that some tickets for this client order are out of stock 
					 else {
						 String noTicketSeatType = readArray[1];
						 Client.clientTranscationFile("Failed: "+noTicketSeatType+" ticekets are out of stock");
						 System.out.println(noTicketSeatType+" seats are out of stock\n");
						 
						 //For the possibility of two types of tickets out of stock
						 if(in.ready()) {
							 String inputFromServer2 = in.readLine();
							 String[] arrayInputFromServer2 = inputFromServer2.split(","); 
							 System.out.println(arrayInputFromServer2[1]+" seats are out of stock\n");
						 }
						 
						//For the possibility of three types of tickets out of stock
						 if(in.ready()) {
							 String inputFromServer3 = in.readLine();
							 String[] arrayInputFromServer3 = inputFromServer3.split(","); 
							 System.out.println(arrayInputFromServer3[1]+" seats are out of stock\n");
						 } 
					 }
				} catch(IOException e) {
					System.err.println("Could't get I/O for the connection to "+ hostName);
					System.exit(1);
				}
			}
		} catch(IOException e) {
			System.out.println("Exception caught when trying to listen on port" + portNumber + "or listening for a conncetion");
			System.exit(1);
		}
			
	}

}
