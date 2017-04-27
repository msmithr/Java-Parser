/**
 * Interface for a recursive descent parser for the class definition of 
 * the Java programming language, to be used with a lexical analyzer. 
 * The only public method acts as a starting point for the recursion.
 * 
 * @author Michael Smith
 *
 */

package interfaces;

import java.util.ArrayDeque;

import types.InvalidInputException;

public interface ParserInterface {
	
	/**
	 * Method acts as a starting point for the recursive descent
	 */
	public void start() throws InvalidInputException;
	
	/**
	 * Generates an error message containing the current lexeme and 
	 * line number
	 * @return Returns an error message as a String
	 */
	public String getErrorMessage();

	/**
	 * Gets a queue containing the output of the parser in order, to be used after calling start()
	 * @return Returns a queue containing all of the output strings of the parser
	 */
	ArrayDeque<String> getOutputQueue();
	
}
