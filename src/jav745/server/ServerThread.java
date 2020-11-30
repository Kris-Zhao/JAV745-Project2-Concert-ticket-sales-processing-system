package jav745.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

/**
 * This is a ServerThread defined by Yuhang Zhao. I also implement the run() method.
 * @author Yuhang Zhao, student number 150467199
 */
public class ServerThread extends Thread {
	private Socket clientSocket = null;
	private List<Concert> concertList = null;
	
	/**
	 * Constructor of ServerThread
	 * @param clientSocket
	 * @param concertList
	 */
	public ServerThread(Socket clientSocket, List<Concert> concertList) {
		this.clientSocket = clientSocket;
		this.concertList = concertList;
	}
	
	/**
	 * Inside this run() method, some specific tasks for every thread are implemented / some information is processed.
	 */
	public void run() {
		try(
				//get access to i/o streams connected to Client
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				)
		{
			
			// get input from Client	
			String inputFromClient1 = in.readLine();
			WebServer.clientIdentifier++;
			System.out.println("Received from client "+ WebServer.clientIdentifier + "\nOrder: "+inputFromClient1 + "\nChecking if there are tickets left for this order");
			
			//extract this order information
			String[] orderArray = inputFromClient1.split(",");
			String concertOrder = orderArray[0];
			int[] seatNumOrder = WebServer.extractSeatNum(orderArray);//call WebServer.extractSeatNum method to get the number of different seat types for this order

			//traverse the concertList to match the order information
			Iterator<Concert> it = concertList.iterator();
			Double totalPayment = 0.00;//create a total payment variable for this order
			while(it.hasNext()) {
				Concert currentConcert = it.next();
				if(currentConcert.getConcertName().equals(concertOrder)) {
					//check whether or not have enough tickets for this order
					int counter = 0 ;
					for(int i=0; i<seatNumOrder.length; i++) {
						int oneTypeSeatNum =  currentConcert.getVenue().getSeat().get(i).getSeatNumber();
						if(seatNumOrder[i]<=oneTypeSeatNum) {
							//calculate the payment for one type seat money
							Double payment = seatNumOrder[i] * (currentConcert.getVenue().getSeat().get(i).getSeatPrice());
							totalPayment+=payment;
							counter++;
							
						}
						//this case is that the tickets are out of stock
						else {
							WebServer.serverTransactionFile("Failed: " + currentConcert.getVenue().getSeat().get(i).getSeatType()+" tickets are sold out.");
							System.out.println(currentConcert.getVenue().getSeat().get(i).getSeatType()+ " tickets have been out of stock.\n");
							out.println("n,"+ currentConcert.getVenue().getSeat().get(i).getSeatType());
						}
					}
					
					//have enough tickets for this order subtract the seat number according to the order
					if(counter == seatNumOrder.length) {
						out.println("y," + totalPayment);
						//get response from client
						String inputFromClient2 = in.readLine();
						if(inputFromClient2.equals("y")) {
							
							//For each type of seat, subtract the number of seat type that this order purchases from concert venue's total number of seats
							for(int j=0;j<seatNumOrder.length;j++) {
								currentConcert.getVenue().getSeat().get(j).subtractSeatNumber(seatNumOrder[j]);
							}
							
							//For each corresponding concert, the payment from client would be added into each concert's accountBalance.
							for(int k=0;k<seatNumOrder.length;k++) {
								double eachSeatTypePayment= seatNumOrder[k] * (currentConcert.getVenue().getSeat().get(k).getSeatPrice());
								currentConcert.setAccountBalance(eachSeatTypePayment);
							}
							
							WebServer.serverTransactionFile("Successfull: "+ inputFromClient1);
							out.println("This is your order ticket confirmation from server confirming you've already successfully paid.");
							System.out.println("The tickets are paid by this client.   Order successful.\n");
						}
						//the case is that the wait time exceeds 60 seconds
						else if(inputFromClient2.equals("Failed: The order is automatically canceled, since the client spent more than 60 seconds to pay.")) {
							WebServer.serverTransactionFile(inputFromClient2);
							System.out.println(inputFromClient2+"\n");
							out.println();
						}
						else {
							String[] arrayInputFromClient2 = inputFromClient2.split(",");
							WebServer.serverTransactionFile("Failed: " + arrayInputFromClient2[1]);
							System.out.println("Failed !!!!!! \n");
							out.println();
							
						}
					}
				} 
			}
		} catch(IOException e) {
			System.err.println();
		}
	}
}
