/**
 * Simple interface for a lexical analyzer for the class definition in the
 * Java programming language. The only public functionality should be the lex() method.
 *
 * @author Michael Smith
 *
 */

package interfaces;
import types.Lexeme;

public interface LexicalAnalyzerInterface {

	/**
	 * Iterates through each lexical unit in the input string.
	 *
	 * @return Each time this method is called, it will return the next lexical
	 * unit in the form of a Lexeme object.
	 */
	public Lexeme nextLexeme();
	
	
	/**
	 * Returns the current line number of the input string
	 * 
	 * @return Current line number of the input stirng
	 */
	public int getLineNumber();

}
