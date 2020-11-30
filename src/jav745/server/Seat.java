package jav745.server;

/**
 * This Seat class is to stand for every seat, which has seatType, seatPrice and seatNumber instance parameters 
 * and some get and set methods.
 * @author Yuhang Zhao, student number 150467199
 */
public class Seat {
	private String seatType;
	private double seatPrice;
	private int seatNumber;
	
	/**
	 * Constructor of Seat class for creating object of Seat class, three instance parameters:
	 * @param seatType
	 * @param seatPrice
	 * @param seatNumber
	 */
	public Seat(String seatType, double seatPrice,int seatNumber) {
		this.seatType = seatType;
		this.seatPrice = seatPrice;
		this.seatNumber = seatNumber;
	}
	
	/**
	 * get one Seat object's seatType
	 * @return seatType
	 */
	public String getSeatType() {
		return this.seatType;
	}
	
	/**
	 * get one Seat object's seatPrice
	 * @return seatPrice
	 */
	public double getSeatPrice() {
		return this.seatPrice;
	}
	
	/**
	 * get the number of one kind of seat  
	 * @return seatNumber
	 */
	public int getSeatNumber() {
		return this.seatNumber;
	}
	
	/**
	 * subtract i seats from the current total number of one kind of seat
	 * @param i
	 * @return void
	 */
	public void subtractSeatNumber(int i) {
		this.seatNumber = this.seatNumber - i ;
	}
	
	public void halfSeatNumber() {
		this.seatNumber = (this.seatNumber/2);
	}
}
