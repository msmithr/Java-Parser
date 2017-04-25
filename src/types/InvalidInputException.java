/**
 * Simple exception for an invalid input found during parsing process
 * 
 * @author Michael Smith
 */

package types;

@SuppressWarnings("serial")
public class InvalidInputException extends Exception{
	public InvalidInputException(String message) {
		super(message);
	}
}
