package parser;

import interfaces.LexicalAnalyzerInterface;
import types.InvalidInputException;
import types.Lexeme;
import types.Token;

public class LexicalAnalyzer implements LexicalAnalyzerInterface{

	private String inputString;
	private int position;
	private boolean arrayType = false; // flag to indicate lexeme is an array type (such as int[])

	/**
	 * Constructor sets the input string and initializes the position index to 0.
	 * @param inputString The input string to lexically analyze
	 */
	public LexicalAnalyzer(String inputString){
		this.inputString = inputString;
		this.position = 0;
	}

	@Override
	public Lexeme nextLexeme() throws InvalidInputException {
		Token token;
		String lexeme;
		char nextChar;

		if (position >= inputString.length()) return null;

		nextChar = inputString.charAt(position);

		// skip to the next non whitespace character
		while (position < inputString.length() && (nextChar == ' ' || nextChar == '\n' || nextChar == '\t')) {
			position++;
			if (position == inputString.length()) return null; // if position reaches the end of the file, return null
			nextChar = inputString.charAt(position);
		}

		switch(nextChar) {

		// next lexeme is a comment and needs to be ignored
		case '/':

			// skip forward to next non comment lexeme
			position++;
			if (inputString.charAt(position) == '/') { // single line comment
				while (inputString.charAt(++position) != '\n');
			} else if (inputString.charAt(position) == '*') { // multi line comment /* */
				while (inputString.charAt(++position) != '*' || inputString.charAt(++position) != '/');
			}

			return nextLexeme(); // recursively call to return next lexeme

		// nextChar is left paren
		case '(':
			token = Token.LEFT_PAREN;
			lexeme = "(";
			position++;
			break;

		// nextChar is right paren
		case ')':
			token = Token.RIGHT_PAREN;
			lexeme = ")";
			position++;
			break;

		// nextChar is left brace
		case '{':
			token = Token.LEFT_BRACE;
			lexeme = "{";
			position++;
			break;

		// nextChar is right brace
		case '}':
			token = Token.RIGHT_BRACE;
			lexeme = "}";
			position++;
			break;

		// nextChar is left bracket
		case '[':
			token = Token.LEFT_BRACKET;
			lexeme = "[";
			position++;
			break;

		// nextChar is right bracket
		case ']':
			token = Token.RIGHT_BRACKET;
			lexeme = "]";
			position++;
			break;

		// nextChar is semicolon
		case ';':
			token = Token.SEMICOLON;
			lexeme = ";";
			position++;
			break;
			
		case ':':
			token = Token.COLON;
			lexeme = ";";
			position++;
			break;

		// nextChar is comma
		case ',':
			token = Token.COMMA;
			lexeme = ",";
			position++;
			break;
			
		case '=':
			token = Token.EQUALS;
			lexeme = "=";
			position++;
			break;

		// otherwise, next lexeme is either a number or a string
		default:
			String newLexeme = "";

			// if nextChar is alphabetic, lexeme is identifier or a reserved word
			if (Character.isAlphabetic(nextChar)) {

				// place the full lexeme in newLexeme
				while (position < inputString.length() && (Character.isAlphabetic(inputString.charAt(position)) || Character.isDigit(inputString.charAt(position)))) {
					newLexeme += inputString.charAt(position);
					position++;
				} // end while

				token = processIdentifier(newLexeme);
				if (arrayType) {
					newLexeme += "[]";
					arrayType = false;
				}
				lexeme = newLexeme;

			} else {
				throw new InvalidInputException("Invalid Input: " + inputString.charAt(position));
			}

		} // end switch case

		return new Lexeme(token, lexeme);

	} // end nextLexeme()


	// handles keywords and identifiers
	private Token processIdentifier(String newLexeme) {
		switch (newLexeme) {

		case "class":
			return Token.KEYWORD_CLASS;

		case "extends":
			return Token.KEYWORD_EXTENDS;

		case "implements":
			return Token.KEYWORD_IMPLEMENTS;

		case "public":
		case "private":
		case "protected":
		case "abstract":
		case "static":
		case "final":
		case "strictfp":
		case "transient":
		case "volatile":
		case "synchronized":
		case "native":
			return Token.MODIFIER;
			
		case "void":
			return Token.KEYWORD_VOID;
			
		case "if":
			return Token.KEYWORD_IF;

		case "while":
			return Token.KEYWORD_WHILE;

		case "do":
			return Token.KEYWORD_DO;

		case "for":
			return Token.KEYWORD_FOR;

		case "switch":
			return Token.KEYWORD_SWITCH;

		case "case":
			return Token.KEYWORD_CASE;

		case "return":
			return Token.KEYWORD_RETURN;

		case "continue":
			return Token.KEYWORD_CONTINUE;

		case "throw":
			return Token.KEYWORD_THROW;

		case "try":
			return Token.KEYWORD_TRY;
			
		case "throws":
			return Token.KEYWORD_THROWS;

		case "boolean":
		case "byte":
		case "char":
		case "short":
		case "int":
		case "long":
		case "float":
		case "double":
			return Token.PRIMITIVE_TYPE;

		default:
			return Token.IDENTIFIER;

		} // end switch case

	} // end processIdentifier


} // end LexicalAnalyzer
