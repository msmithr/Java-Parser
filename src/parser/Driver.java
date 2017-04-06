package parser;
import types.Lexeme;
import types.Token;

public class Driver {
	public static void main(String[] args) {
		Lexeme l = new Lexeme(Token.IDENTIFIER, "Hello");
		
		System.out.println(l);
	}
}