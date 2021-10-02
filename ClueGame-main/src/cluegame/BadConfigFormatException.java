package cluegame;

@SuppressWarnings("serial")
public class BadConfigFormatException extends RuntimeException {

	//non-parameterized constructor
	public BadConfigFormatException() {
		super("Error: Bad Config Format :( ");		
	}
	//parameterized constructor
	public BadConfigFormatException(String str) {
		super(str);		
	}
}