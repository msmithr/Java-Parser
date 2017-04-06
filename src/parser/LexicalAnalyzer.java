package parser;
import interfaces.LexicalAnalyzerInterface;
import types.Lexeme;

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
		// TODO Auto-generated method stub
		return null;
	}
}
