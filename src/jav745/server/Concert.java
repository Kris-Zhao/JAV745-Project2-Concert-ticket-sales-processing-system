package jav745.server;

/**
 * This Concert class is to create Concert objects to store the concert information from serverInformaton.csv file.
 * @author Yuhang Zhao, student number 150467199
 */
public class Concert {
	private String concertName;
	private String date;
	private Venue venue;
	private double accountBalance;
	
	/**
	 * Constructor of Concert class to create Concert objects
	 * @param concertName
	 * @param date
	 * @param venue
	 * @param accountBalance
	 */
	public Concert(String concertName, String date, Venue venue, double accountBalance) {
		this.concertName = concertName;
		this.date = date;
		this.venue = venue;
		this.accountBalance = accountBalance;
	}
	
	/**
	 * get one Venue's information from one Concert object
	 * @return one Venue object
	 */
	public Venue getVenue() {
		return this.venue;
	}
	
	/**
	 * get concert name for one Concert object
	 * @return concertName
	 */
	public String getConcertName() {
		return this.concertName;
	}
	
	/**
	 * add some income from clients payment to concert accountBalance 
	 * @param income
	 */
	public void setAccountBalance(double income) {
		this.accountBalance = this.accountBalance + income;
	}
	
	
}
