package jav745.server;

/**
 * This is EmptyFileException defined by Yuhang Zhao to stand for the case that when one file is empty, program
 * would throw this exception.
 * @author Yuhang Zhao, student number 150467199
 */
public class EmptyFileException extends Exception {
	
	/**
	 * inherit the constructor from Exception class
	 */
	public EmptyFileException() {
		super();
	}
	
	/**
	 * inherit another constructor from Exception class
	 * @param msg
	 */
	public EmptyFileException(String  msg) {
		super(msg);
	}
}
