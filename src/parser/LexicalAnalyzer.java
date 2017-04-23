package parser;

import interfaces.LexicalAnalyzerInterface;
import types.Lexeme;
import types.Token;

public class LexicalAnalyzer implements LexicalAnalyzerInterface{

	private String inputString;
	private int position;
	int lineNumber;

	/**
	 * Constructor sets the input string and initializes the position index to 0.
	 * @param inputString The input string to lexically analyze
	 */
	public LexicalAnalyzer(String inputString){
		this.inputString = inputString;
		this.position = 0;
		this.lineNumber = 1;
	}
	
	@Override
	public int getLineNumber() {
		return this.lineNumber;
	}

	@Override
	public Lexeme nextLexeme() {
		Token token = null;
		String lexeme = null;
		char nextChar;


		if (position >= inputString.length()) return null;

		nextChar = inputString.charAt(position);

		// skip to the next non whitespace character
		while (position < inputString.length() && (nextChar == ' ' || nextChar == '\n' || nextChar == '\t')) {
			position++;
			if (nextChar == '\n') lineNumber++;
			if (position == inputString.length()) return null; // if position reaches the end of the file, return null
			nextChar = inputString.charAt(position);
		}

		switch(nextChar) {

		// next lexeme is a comment and needs to be ignored
		// need to rework this
		case '/':

			// skip forward to next non comment lexeme
			position++;
			
			if (inputString.charAt(position) == '/') { // single line comment
				while (inputString.charAt(++position) != '\n');
				return nextLexeme();
			} else if (inputString.charAt(position) == '*') { // multi line comment /* */
				while (position < inputString.length()) {
					while (inputString.charAt(++position) != '*') {
						if (inputString.charAt(position) == '\n') lineNumber++;
					}
					if (inputString.charAt(++position) == '/') {
						position+=1;
						return nextLexeme();
					}
				}
			} 
			
			if (inputString.charAt(position) == '=') {
				token = token.ASSIGNMENT_OPERATOR;
				lexeme = "/=";
				position++;
			} else {
				token = token.INFIX_OPERATOR;
				lexeme = "/";
			}
			break;

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
			lexeme = ":";
			position++;
			break;

		// nextChar is comma
		case ',':
			token = Token.COMMA;
			lexeme = ",";
			position++;
			break;
			
		case '=':
			position++;
			if (inputString.charAt(position) == '=') {
				token = Token.INFIX_OPERATOR;
				lexeme = "==";
				position++;
			} else {
				token = Token.ASSIGNMENT_OPERATOR;
				lexeme = "=";
			}
			break;
			
		case '.':
			token = Token.DOT;
			lexeme = ".";
			position++;
			break;
			
		case '*':
			position++;
			if (inputString.charAt(position) == '=') {
				token = Token.ASSIGNMENT_OPERATOR;
				lexeme = "*=";
				position++;
			} else {
				token = Token.INFIX_OPERATOR;
				lexeme = "*";
			}
			
			break;
			
		case '+':
			position++;
			if (inputString.charAt(position) == '=') {
				token = Token.ASSIGNMENT_OPERATOR;
				lexeme = "+=";
				position++;
			} else if (inputString.charAt(position) == '+') {
				token = Token.OPERATOR_INCREMENT;
				lexeme = "++";
				position++;
			} else {
				token = Token.OPERATOR_PLUS;
				lexeme = "+";
			}
			break;
			
		case '-':
			position++;
			if (inputString.charAt(position) == '=') {
				token = Token.ASSIGNMENT_OPERATOR;
				lexeme = "-=";
				position++;
			} else if (inputString.charAt(position) == '-') {
				token = Token.OPERATOR_DECREMENT;
				lexeme = "--";
				position++;
			} else {
				token = Token.OPERATOR_MINUS;
				lexeme = "-";
			}
			break;
			
		case '%':
			position++;
			if (inputString.charAt(position) == '=') {
				token = Token.ASSIGNMENT_OPERATOR;
				lexeme = "%=";
				position++;
			} else {
				token = Token.INFIX_OPERATOR;
				lexeme = "%";
			}
			break;

		case '\'':
			String charLiteral = "";
			position++;
			while (inputString.charAt(position) != '\'' || (inputString.charAt(position-1) == '\\') && inputString.charAt(position-2) != '\\') {
				charLiteral += inputString.charAt(position);
				position++;
			}
			token = Token.CHAR_LITERAL;
			lexeme = String.format("\'%s\'", charLiteral);
			position++;
			break;
			
		case '\\':
			token = Token.BACKSLASH;
			lexeme = "\\";
			position++;
			break;
			
		case '\"':
			String stringLiteral = "";
			position++;
			while (inputString.charAt(position) != '\"' || (inputString.charAt(position-1) == '\\' && inputString.charAt(position-1) != '\\')) {
				stringLiteral += inputString.charAt(position);
				position++;
			}
			token = Token.STRING_LITERAL;
			lexeme = String.format("\"%s\"", stringLiteral);
			position++;
			break;
			
		case '?':
			token = Token.QUESTION_MARK;
			lexeme = "?";
			position++;
			break;
			
		case '|':
			position++;
			if (inputString.charAt(position) == '|') {
				token = Token.INFIX_OPERATOR;
				lexeme = "||";
				position++;
			} else if (inputString.charAt(position) == '=') {
				token = Token.ASSIGNMENT_OPERATOR;
				lexeme = "|=";
				position++;
			} else {
				token = Token.INFIX_OPERATOR;
				lexeme = "|";
			}
			break;
			
		case '&':
			position++;
			if (inputString.charAt(position) == '&') {
				token = Token.INFIX_OPERATOR;
				lexeme = "&&";
				position++;
			} else if (inputString.charAt(position) == '=') {
				token = Token.ASSIGNMENT_OPERATOR;
				lexeme = "&=";
				position++;
			} else {
				token = Token.INFIX_OPERATOR;
				lexeme = "&";
			}
			break;
			
		case '^':
			position++;
			if (inputString.charAt(position) == '=') {
				token = Token.ASSIGNMENT_OPERATOR;
				lexeme = "^=";
				position++;
			} else {
				token = Token.INFIX_OPERATOR;
				lexeme = "^";
			}
			break;

		case '!':
			position++;
			if (inputString.charAt(position) == '=') {
				token = Token.INFIX_OPERATOR;
				lexeme = "!=";
				position++;
			} else {
				token = Token.PREFIX_OPERATOR;
				lexeme = "!";
			}
			break;
			
		case '<':
			token = Token.LEFT_ANGLEBRACKET;
			lexeme = "<";
			position++;
			break;
			
		case '>':
			token = Token.RIGHT_ANGLEBRACKET;
			lexeme = ">";
			position++;
			break;
			
		case '~':
			token = Token.PREFIX_OPERATOR;
			lexeme = "~";
			position++;
			break;

		// otherwise, next lexeme is either a number or a string
		default:
			String newLexeme = "";

			// if nextChar is alphabetic, lexeme is identifier or a reserved word
			if (Character.isAlphabetic(nextChar)) {

				// place the full lexeme in newLexeme
				while (position < inputString.length() && (Character.isAlphabetic(inputString.charAt(position)) || Character.isDigit(inputString.charAt(position)) || inputString.charAt(position) == '_')) {
					newLexeme += inputString.charAt(position);
					position++;
				} // end while

				token = processIdentifier(newLexeme);
				lexeme = newLexeme;
				
				
			} else if (Character.isDigit(nextChar)) {
				while (position < inputString.length() && Character.isDigit(inputString.charAt(position))) {
					newLexeme += inputString.charAt(position);
					position++;
				} // end while
				
				lexeme = newLexeme;
				token = Token.INT_LITERAL;
				

			} else {
				System.out.printf("ERROR: Invalid input: %c\n", inputString.charAt(position));
				System.exit(1);
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
			
		case "else":
			return Token.KEYWORD_ELSE;

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
			
		case "default":
			return Token.KEYWORD_DEFAULT;

		case "return":
			return Token.KEYWORD_RETURN;

		case "continue":
			return Token.KEYWORD_CONTINUE;

		case "throw":
			return Token.KEYWORD_THROW;

		case "try":
			return Token.KEYWORD_TRY;
			
		case "catch":
			return Token.KEYWORD_CATCH;
			
		case "finally":
			return Token.KEYWORD_FINALLY;
			
		case "throws":
			return Token.KEYWORD_THROWS;
			
		case "break":
			return Token.KEYWORD_BREAK;
			
		case "assert":
			return Token.KEYWORD_ASSERT;
			
		case "package":
			return Token.KEYWORD_PACKAGE;
			
		case "import":
			return Token.KEYWORD_IMPORT;
			
		case "true":
			return Token.KEYWORD_TRUE;
			
		case "false":
			return Token.KEYWORD_FALSE;
			
		case "null":
			return Token.KEYWORD_NULL;
			
		case "this":
			return Token.KEYWORD_THIS;
			
		case "super":
			return Token.KEYWORD_SUPER;
			
		case "new":
			return Token.KEYWORD_NEW;
		
		case "instanceOf":
			return Token.KEYWORD_INSTANCEOF;

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
