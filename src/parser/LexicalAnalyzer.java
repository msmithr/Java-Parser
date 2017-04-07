package parser;
import interfaces.LexicalAnalyzerInterface;
import types.Lexeme;
import types.Token;

public class LexicalAnalyzer implements LexicalAnalyzerInterface{

	private String inputString;
	private int position;

	/**
	 * Constructor sets the input string and initializes the position index to 0.
	 * @param inputString The input string to lexically analyze
	 */
	public LexicalAnalyzer(String inputString){
		this.inputString = inputString;
		this.position = 0;
	}

	@Override
	public Lexeme lex() {
		Token token;
		String lexeme;
		char nextChar;
		
		if (position >= inputString.length()) return null;

		nextChar = inputString.charAt(position);
		
		// skip to the next non whitespace character
		while (position < inputString.length() && (nextChar == ' ' || nextChar == '\n' || nextChar == '\t')) {
			position++;
			if (position == inputString.length()) return null; // if pos reaches the end of the file, return null
			nextChar = inputString.charAt(position);
		}
		
		switch(nextChar) {
		
		// nextChar is left paren
		case '(':
			token = Token.LEFT_PAREN;
			lexeme = "(";
			position++;
			break;
			
		// nextChar is right paren
		case ')':
			token = Token.RIGHT_PAREN;
			lexeme = ")";
			position++;
			break;
		
		// nextChar is left brace
		case '{':
			token = Token.LEFT_BRACE;
			lexeme = "{";
			position++;
			break;	
			
		// nextChar is right brace
		case '}':
			token = Token.RIGHT_BRACE;
			lexeme = "}";
			position++;
			break;
			
		// nextChar is left bracket
		case '[':
			token = Token.LEFT_BRACKET;
			lexeme = "[";
			position++;
			break;
			
		// nextChar is right bracket
		case ']':
			token = Token.RIGHT_BRACKET;
			lexeme = "]";
			position++;
			break;
			
		// nextChar is semicolon
		case ';':
			token = Token.SEMICOLON;
			lexeme = ";";
			position++;
			break;
		
		default:
			return null;
		
		}
		
		return new Lexeme(token, lexeme);
	}
}
