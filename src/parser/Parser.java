package parser;
import java.util.ArrayDeque;
import java.util.ArrayList;

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

		classBody();

		System.out.println("Exit <class>");
	}

	// <class modifiers>
	private void classModifiers() throws InvalidInputException {
		System.out.println("Enter <class modifiers>");

		while (nextLexeme.getToken() != Token.KEYWORD_CLASS) {
			if (nextLexeme.getToken() == Token.KEYWORD_ACCESSMODIFIER) accessModifier();
			else classModifier();
		}

		System.out.println("Exit <class modifiers>");
	}

	// <access modifier>
	private void accessModifier() throws InvalidInputException {
		System.out.println("Enter <access modifiers>");

		processLexeme(Token.KEYWORD_ACCESSMODIFIER);

		System.out.println("Exit <access modifiers>");
	}

  // <class modifier>
	private void classModifier() throws InvalidInputException {
		System.out.println("Enter <other modifiers>");

		switch (nextLexeme.getToken()) {

		case KEYWORD_ABSTRACT:
		case KEYWORD_STATIC:
		case KEYWORD_FINAL:
		case KEYWORD_STRICTFP:
			processLexeme(nextLexeme.getToken());
			break;

		default:
			throw new InvalidInputException("Invalid input: " + nextLexeme.getLexeme());

		}

	System.out.println("Exit <other modifiers>");
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
	private void classBody() throws InvalidInputException {
		System.out.println("Enter <class body>");

		processLexeme(Token.LEFT_BRACE);

		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			classBodyStatement();
		}

		processLexeme(Token.RIGHT_BRACE);

		System.out.println("Exit <class body>");
	}

	// <class body statement>
	private void classBodyStatement() throws InvalidInputException {
		System.out.println("Enter <class body statement>");

		// class body statement can be a static initializer,
		// field declaration, method declaration, or constructor declaration

		// if the next lexeme is the static keyword, static initializer
		if (nextLexeme.getToken() == Token.KEYWORD_STATIC) {
			staticInitializer();
		} else {
			// otherwise, we have to look through the input, keeping track of each lexeme
			ArrayDeque<Lexeme> lexemeHistory = new ArrayDeque<Lexeme>();

			// skip forward to the next identifier, storing each lexeme in a queue
			while (nextLexeme.getToken() != Token.IDENTIFIER) {
				lexemeHistory.addLast(nextLexeme);
				nextLexeme = lex.nextLexeme();
			}

			// add the last lexeme to the queue
			lexemeHistory.addLast(nextLexeme);
			nextLexeme = lex.nextLexeme();

			// if the first lexeme after the identifier is not a parenthesis, field declaration
			if (nextLexeme.getToken() != Token.LEFT_PAREN) {
				// must "enter" in this method because of stored lexeme history
				System.out.println("Enter <field declaration>");

				// print out each lexeme stored
				while (!lexemeHistory.isEmpty()) {
					System.out.println(lexemeHistory.removeFirst());
				}

				fieldDeclaration();

			} else {
				// remove the last lexeme in the queue and store it as a temp,
				// so we can look at the previous lexeme
				Lexeme temp = lexemeHistory.removeLast();
				// if a type was defined, this is a method declaration
				if (lexemeHistory.peekLast().getToken() == Token.KEYWORD_TYPE) {
					lexemeHistory.addLast(temp); // put the identifier back in the queue

					// must "enter" in this method because of stored lexeme history
					System.out.println("Enter <method declaration>");

					// print out each stored lexeme
					while (!lexemeHistory.isEmpty()) {
						System.out.println(lexemeHistory.removeFirst());
					}

					methodDeclaration();

				} else { // if there was no type defined, this is a constructor declaration
					lexemeHistory.addLast(temp); // put the identifier back in the queue

					// must "enter" in this method because of stored lexeme history
					System.out.println("Enter <constructor declaration>");

					// print out each stored lexeme
					while (!lexemeHistory.isEmpty()) {
						System.out.println(lexemeHistory.removeFirst());
					} // end while

					constructorDeclaration();

				} // end else
			} // end else

		} // end else

		System.out.println("Exit <class body statement>");
	} // end classBodyStatement()

	private void fieldDeclaration() {
		// up to and including <id> has already been processed

		System.out.println("Exit <field declaration>");
	}

	private void methodDeclaration() {
		// up to and including <id> has already been processed
		System.out.println("Exit <method declaration>");
	}

	private void constructorDeclaration() {
		// up to and including <id> has already been processed
		System.out.println("Exit <constructor declaration>");
	}

	private void staticInitializer() throws InvalidInputException {
		System.out.println("Enter <static initializer>");

		processLexeme(Token.KEYWORD_STATIC);
		block();

		System.out.println("Exit <static initializer>");
	}

	private void block() throws InvalidInputException {
		System.out.println("Enter <block>");

		System.out.println("Exit <block>");
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
	 * Wrapper method for processLexeme, always required, ignores return
	 */
	private void processLexeme(Token token) throws InvalidInputException {
		processLexeme(token, false);
	}
}
