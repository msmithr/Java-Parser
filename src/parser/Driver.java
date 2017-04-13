package parser;
import types.InvalidInputException;

public class Driver {
	public static void main(String[] args) throws InvalidInputException {
		String input = "public abstract strictfp class /* comment comment */ main extends test implements a, b { static }";
		
		Parser p = new Parser(input);
		
		p.start();
	}
}
