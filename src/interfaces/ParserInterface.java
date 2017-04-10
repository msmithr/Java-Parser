/**
 * Interface for a recursive descent parser for the class definition of 
 * the Java programming language, to be used with a lexical analyzer. 
 * The only public method asks as a starting point for the recursion.
 * 
 * @author Michael Smith
 *
 */

package interfaces;

import types.InvalidInputException;

public interface ParserInterface {
	/**
	 * Method acts as a starting point for the recursive descent
	 * @throws InvalidInputException 
	 */
	public void start() throws InvalidInputException;
	
}
