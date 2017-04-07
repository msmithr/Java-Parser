package parser;
import types.Lexeme;
import types.Token;

public class Driver {
	public static void main(String[] args) {
		LexicalAnalyzer lex = new LexicalAnalyzer("{}();[]");
		
		for (int i = 0; i < 10; i++) {
			System.out.println(lex.lex());
		}
	}
}