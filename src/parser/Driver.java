package parser;
import types.InvalidInputException;

public class Driver {
	public static void main(String[] args) throws InvalidInputException {
		String input = "class main { public int[] teast() boolean[] }[] char[] char";
		
		LexicalAnalyzer lex = new LexicalAnalyzer(input);
		
		for (int i = 0; i < 20; i++) {
			System.out.println(lex.nextLexeme());
		}
		
	}
}
