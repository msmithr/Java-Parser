package parser;
import interfaces.ParserInterface;
import types.Lexeme;

public class Parser implements ParserInterface{

	LexicalAnalyzer lex;
	Lexeme nextLexeme;
	
	/**
	 * Constructor creates the lexical analyzer and initializes nextLexeme, given an input string
	 * 
	 * @param inputString
	 */
	public Parser(String inputString) {
		lex = new LexicalAnalyzer(inputString);
		nextLexeme = lex.lex();
	}
	
	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
}
