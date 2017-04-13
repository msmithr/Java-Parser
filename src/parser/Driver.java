package parser;
import types.InvalidInputException;

public class Driver {
	public static void main(String[] args) throws InvalidInputException {
		String input = "Hello /* Comment comment \n comment comment */ world";
		
		LexicalAnalyzer lex = new LexicalAnalyzer(input);
		
		System.out.println(lex.nextLexeme());
		System.out.println(lex.nextLexeme());
		System.out.println(lex.nextLexeme());
		System.out.println(lex.nextLexeme());
		System.out.println(lex.nextLexeme());
		System.out.println(lex.nextLexeme());
		System.out.println(lex.nextLexeme());
		System.out.println(lex.nextLexeme());
		System.out.println(lex.nextLexeme());
	}
}
