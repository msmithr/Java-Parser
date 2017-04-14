package parser;
import types.InvalidInputException;

public class Driver {
	public static void main(String[] args) throws InvalidInputException {
		String input = "class main { public int teast() }";
		
		Parser p = new Parser(input);
		
		p.start();
		
	}
}
