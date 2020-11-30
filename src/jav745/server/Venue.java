package jav745.server;

import java.util.List;

/**
 * This venue class is to create venue objects.
 * @author Yuhang Zhao, student number 150467199
 */
public class Venue {
	private String venueName;
	private List<Seat> seat;
	
	/**
	 * Constructor of Venue class to create venue objects
	 * @param venueName
	 * @param seat
	 */
	public Venue(String venueName, List<Seat> seat) {
		this.venueName = venueName;
		this.seat = seat;
	}
	
	/**
	 * get venue name of one Venue obejct
	 * @return venueName  
	 */
	public String getVenueName() {
		return this.venueName;
	}
	
	/**
	 * get a List from one Venue Object
	 * @return a List called as seat, which is to store Seat class objects.
	 */
	public List<Seat> getSeat(){
		return this.seat;
	}
	
}
