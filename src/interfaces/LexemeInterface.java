/**
 *
 * Interface for a lexical unit, containing a string of characters
 * and the token it corresponds to.
 *
 * @author Michael Smith
 *
 */

package interfaces;
import types.Token;

public interface LexemeInterface {

	/**
     * Gets the token that this lexical unit corresponds to
     * @return The token of this lexeme represented as an enum type
     */
	public Token getToken();


	/**
	 * Gets the string, or lexeme, of this lexical unit,
     * or null if the end of the input string has been reached.
	 * @return The lexeme represented as a String
	 */
    public String getLexeme();

} // end interface
