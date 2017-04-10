package parser;
import interfaces.ParserInterface;
import types.InvalidInputException;
import types.Lexeme;
import types.Token;

public class Parser implements ParserInterface{

	LexicalAnalyzer lex;
	Lexeme nextLexeme;

	/**
	 * Constructor creates the lexical analyzer and initializes nextLexeme, given an input string
	 *
	 * @param inputString
	 * @throws InvalidInputException
	 */
	public Parser(String inputString) throws InvalidInputException {
		lex = new LexicalAnalyzer(inputString);
		nextLexeme = lex.nextLexeme();
	}

	@Override
	public void start() throws InvalidInputException {
		class_rule();
	}

	// <class>
	private void class_rule() throws InvalidInputException {
		System.out.println("Enter <class>");

		classModifiers();

		processLexeme(Token.KEYWORD_CLASS);
		processLexeme(Token.IDENTIFIER);

		extendsRule();
		implementsRule();

		processLexeme(Token.LEFT_BRACE);
		classBody();
		processLexeme(Token.RIGHT_BRACE);


		System.out.println("Exit <class>");
	}

	// <class modifiers>
	private void classModifiers() throws InvalidInputException {
		System.out.println("Enter <class modifiers>");
		
		accessModifiers();
		otherClassModifiers();
		
		System.out.println("Exit <class modifiers>");
	}
	
	private void accessModifiers() throws InvalidInputException {
		System.out.println("Enter <access modifiers>");
		
		processLexeme(Token.KEYWORD_ACCESSMODIFIER);
		
		System.out.println("Exit <access modifiers>");
	}
	
	private void otherClassModifiers() throws InvalidInputException {
		while (nextLexeme.getToken() == Token.KEYWORD_CLASSMODIFIER) {
			processLexeme(Token.KEYWORD_CLASSMODIFIER);
		}
	}

	// <extends>
	private void extendsRule() throws InvalidInputException {
		System.out.println("Enter <extends>");

		if (processLexeme(Token.KEYWORD_EXTENDS, true) == true)
			processLexeme(Token.IDENTIFIER);

		System.out.println("Exit <extends>");
	}

	// <implements>
	private void implementsRule() throws InvalidInputException {
		System.out.println("Enter <implements>");
		
		if (processLexeme(Token.KEYWORD_IMPLEMENTS, true) == true) {
			processLexeme(Token.IDENTIFIER);
			while (nextLexeme.getToken() == Token.COMMA) {
				processLexeme(Token.COMMA);
				processLexeme(Token.IDENTIFIER);
			}
		}

		System.out.println("Exit <implements>");
	}

	// <class body>
	private void classBody() {
		System.out.println("Enter <class body>");

		System.out.println("Exit <class body>");
	}


	/**
	 * Checks if the current lexeme's associated token is equal to the given token,
	 * prints out the current lexeme, and moves to the next lexeme in the input string
	 *
	 * @param token Expected token
	 * @param optional True is this token is optional, false if not
	 * @throws InvalidInputException
	 */
	private boolean processLexeme(Token token, boolean optional) throws InvalidInputException {
		if (nextLexeme.getToken() == token) {
			System.out.println(nextLexeme);
			nextLexeme = lex.nextLexeme();
			return true;
		} else if (optional == false){
			throw new InvalidInputException("Invalid input: " + nextLexeme.getLexeme());
		}
		return false;
	}

	/**
	 * Wrapper class for processLexeme, always required, ignores return
	 */
	private void processLexeme(Token token) throws InvalidInputException {
		processLexeme(token, false);
	}
}
