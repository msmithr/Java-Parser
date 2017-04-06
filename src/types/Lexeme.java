/**
 * Implementation of the lexeme interface, representing a lexical unit
 * and it's associated token
 * 
 * @author Michael Smith
 *
 */

package types;
import interfaces.LexemeInterface;


public class Lexeme implements LexemeInterface {

	private Token token;
	private String lexeme;
	
	public Lexeme(Token token, String lexeme) {
		this.token = token;
		this.lexeme = lexeme;
	}
	
	@Override
	public Token getToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLexeme() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("Next token is: %10s\t Next Lexeme is: %s", token, lexeme);
	}
	
}
