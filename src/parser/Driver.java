package parser;
import types.InvalidInputException;

public class Driver {
	public static void main(String[] args) throws InvalidInputException {
		Parser p = new Parser("public abstract static final class Hello extends abcd implements test, tet2, asdf {}");
		
		p.start();
	}
}