package parser;

import interfaces.ParserInterface;
import types.InvalidInputException;
import types.Lexeme;
import types.Token;

public class Parser implements ParserInterface{

	LexicalAnalyzer lex;
	Lexeme nextLexeme;
	int indentationLevel;
	
	/**
	* Constructor creates the lexical analyzer and initializes nextLexeme, given an input string
	*
	* @param inputString
	* @throws InvalidInputException
	*/
	public Parser(String inputString) throws InvalidInputException {
		lex = new LexicalAnalyzer(inputString);
		nextLexeme = lex.nextLexeme();
		indentationLevel = 0;
	}

	@Override
	public void start() throws InvalidInputException {
		classRule();
	}
	
	// <class>
	private void classRule() throws InvalidInputException {
		printIndented("Enter <class>");
		indentationLevel++;
		
		while (nextLexeme.getToken() == Token.MODIFIER) {
			processLexeme(Token.MODIFIER);
		}

		classDeclaration();
		
		indentationLevel--;
		printIndented("Exit <class>");
	}
	
	// <class_declaration>
	private void classDeclaration() throws InvalidInputException {
		printIndented("Enter <class_declaration>");
		indentationLevel++;
		
		processLexeme(Token.KEYWORD_CLASS);
		processLexeme(Token.IDENTIFIER);

		if (nextLexeme.getToken() == Token.KEYWORD_EXTENDS) extendsRule();
		if (nextLexeme.getToken() == Token.KEYWORD_IMPLEMENTS) implementsRule();

		classBody();
		
		indentationLevel--;
		printIndented("Exit <class_declaration>");
	}

	// <extends>
	private void extendsRule() throws InvalidInputException {
		printIndented("Enter <extends>");
		indentationLevel++;

		processLexeme(Token.KEYWORD_EXTENDS);
		processLexeme(Token.IDENTIFIER);

		indentationLevel--;
		printIndented("Exit <extends>");
	}

	// <implements>
	private void implementsRule() throws InvalidInputException {
		printIndented("Enter <implements>");
		indentationLevel++;

		processLexeme(Token.KEYWORD_IMPLEMENTS);
		processLexeme(Token.IDENTIFIER);
		
		while (nextLexeme.getToken() == Token.COMMA) {
			processLexeme(Token.COMMA);
			processLexeme(Token.IDENTIFIER);
		}

		indentationLevel--;
		printIndented("Exit <implements>");
	}

	// <class_body>
	private void classBody() throws InvalidInputException {
		printIndented("Enter <class_body>");
		indentationLevel++;
		
		processLexeme(Token.LEFT_BRACE);
		
		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			classBodyStatement();
		}
		
		processLexeme(Token.RIGHT_BRACE);
		
		indentationLevel--;
		printIndented("Exit <class_body>");
	}


	// <class_body_statement>
	private void classBodyStatement() throws InvalidInputException {
		printIndented("Enter <class_body_statement>");
		indentationLevel++;
		
		switch (nextLexeme.getToken()) {
		
		case SEMICOLON:
			processLexeme(Token.SEMICOLON);
			break;
		
		case LEFT_BRACE:
			block();
			break;
		
		default:
			while (nextLexeme.getToken() == Token.MODIFIER) processLexeme(Token.MODIFIER);
			
			if (nextLexeme.getToken() == Token.LEFT_BRACE) block();
			else {
				classBodyDeclaration();
			}
		
		} // end switch

		indentationLevel--;
		printIndented("Exit <class_body_statement>");
	} // end classBodyStatement()
	
	// <class_body_declaration>
	private void classBodyDeclaration() throws InvalidInputException {
		printIndented("Enter <class_body_declaration>");
		indentationLevel++;
		
		switch (nextLexeme.getToken()) {
		
		case KEYWORD_CLASS:
			classDeclaration();
			break;
			
		case KEYWORD_VOID:
			processLexeme(Token.KEYWORD_VOID);
			processLexeme(Token.IDENTIFIER);
			methodDeclaration();
			break;
			
		case IDENTIFIER:
			processLexeme(Token.IDENTIFIER);
			if (nextLexeme.getToken() == Token.LEFT_PAREN) methodDeclaration(); // constructor declaration
			else {
				while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
					processLexeme(Token.LEFT_BRACKET);
					processLexeme(Token.RIGHT_BRACKET);
				}
				processLexeme(Token.IDENTIFIER);
				if (nextLexeme.getToken() == Token.LEFT_PAREN) methodDeclaration();
				else {
					fieldDeclaration();
					processLexeme(Token.SEMICOLON);
				}
			}
			
			break;
			
		case PRIMITIVE_TYPE:
			processLexeme(Token.PRIMITIVE_TYPE);
			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				processLexeme(Token.LEFT_BRACKET);
				processLexeme(Token.RIGHT_BRACKET);
			}
			processLexeme(Token.IDENTIFIER);
			if (nextLexeme.getToken() == Token.LEFT_PAREN) methodDeclaration();
			else {
				fieldDeclaration();
				processLexeme(Token.SEMICOLON);
			}
			break;
		
		default:
			throw new InvalidInputException("Invalid input: " + nextLexeme.getLexeme());
		
		} // end switch
		
		indentationLevel--;
		printIndented("Exit <class_body_declaration>");
	} // end classBodyDeclaration
	
	// <field_declaration>
	private void fieldDeclaration() throws InvalidInputException {
		printIndented("Enter <field_declaration>");
		indentationLevel++;
		
		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}
		
		if (nextLexeme.getToken() == Token.EQUALS) {
			processLexeme(Token.EQUALS);
			variableInit();
		}
		
		while (nextLexeme.getToken() == Token.COMMA) {
			processLexeme(Token.COMMA);
			variableDeclarators();
		}
		
		indentationLevel--;
		printIndented("Exit <field_declaration>");
	}
	
	// <variable_declarators>
	private void variableDeclarators() throws InvalidInputException {
		printIndented("Enter <variable_declarators>");
		indentationLevel++;
		
		processLexeme(Token.IDENTIFIER);
		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}
		
		if (nextLexeme.getToken() == Token.EQUALS) {
			processLexeme(Token.EQUALS);
			variableInit();
		}
		
		while (nextLexeme.getToken() == Token.COMMA) {
			processLexeme(Token.COMMA);
			
			processLexeme(Token.IDENTIFIER);
			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				processLexeme(Token.LEFT_BRACKET);
				processLexeme(Token.RIGHT_BRACKET);
			}
			
			if (nextLexeme.getToken() == Token.EQUALS) {
				processLexeme(Token.EQUALS);
				variableInit();
			}
		}
		
		indentationLevel--;
		printIndented("Exit <variable_declarators>");
	}
	
	// <method_declaration>
	private void methodDeclaration() throws InvalidInputException {
		printIndented("Enter <method_declaration>");
		indentationLevel++;
		
		parameters();
		if (nextLexeme.getToken() == Token.KEYWORD_THROWS) ; //throws();
		
		if (nextLexeme.getToken() == Token.SEMICOLON) processLexeme(Token.SEMICOLON);
		else block();
		
		indentationLevel--;
		printIndented("Exit <method_declaration>");
	}
	
	// <parameters>
	private void parameters() throws InvalidInputException {
		printIndented("Enter <parameters>");
		indentationLevel++;
		
		processLexeme(Token.LEFT_PAREN);
		
		while (nextLexeme.getToken() != Token.RIGHT_PAREN) {
			while (nextLexeme.getToken() == Token.MODIFIER) processLexeme(Token.MODIFIER);
			type();
			processLexeme(Token.IDENTIFIER);
			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				processLexeme(Token.LEFT_BRACKET);
				processLexeme(Token.RIGHT_BRACKET);
			} // end while
			if (nextLexeme.getToken() != Token.RIGHT_PAREN) processLexeme(Token.COMMA);
		} // end while
		
		processLexeme(Token.RIGHT_PAREN);
		
		indentationLevel--;
		printIndented("Exit <parameters>");
	}
	
	// <block>
	private void block() throws InvalidInputException {
		printIndented("Enter <block>");
		indentationLevel++;
		
		processLexeme(Token.LEFT_BRACE);
		
		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			switch (nextLexeme.getToken()) {
			case MODIFIER:
				if (nextLexeme.getLexeme().equals("synchronized")) {
					statement();
					break;
				}
				while (nextLexeme.getToken() == Token.MODIFIER) processLexeme(Token.MODIFIER);
			
			case KEYWORD_CLASS:
				classDeclaration();
				break;
			
			case PRIMITIVE_TYPE:
				localVariableDeclaration();
				processLexeme(Token.SEMICOLON);
				break;
				
			case IDENTIFIER:
				processLexeme(Token.IDENTIFIER);
				
				if (nextLexeme.getToken() == Token.COLON) {
					processLexeme(Token.COLON);
					statement();
					break;
				}

				while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
					processLexeme(Token.LEFT_BRACKET);
					processLexeme(Token.RIGHT_BRACKET);
				}
				
				if (nextLexeme.getToken() == Token.EQUALS) {
					processLexeme(Token.EQUALS);
					variableInit();
				}
				variableDeclarators();
				processLexeme(Token.SEMICOLON);
				break;
			
			default:
				statement();
				break;
			}
		}
		
		processLexeme(Token.RIGHT_BRACE);
		
		indentationLevel--;
		printIndented("Exit <block>");
	}
	
	// <local_variable_declaration>
	private void localVariableDeclaration() throws InvalidInputException {
		printIndented("Enter <local_variable_declaration>");
		indentationLevel++;
		
		type();
		variableDeclarators();
		
		indentationLevel--;
		printIndented("Exit <local_variable_declaration>");
	}
	
	// <type>
	private void type() throws InvalidInputException {
		printIndented("Enter <type>");
		indentationLevel++;
		
		if (nextLexeme.getToken() == Token.PRIMITIVE_TYPE) {
			processLexeme(Token.PRIMITIVE_TYPE);
		} else {
			processLexeme(Token.IDENTIFIER);
		}
		
		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}
		
		indentationLevel--;
		printIndented("Exit <type>");
	}

	//<statement> rule (Nathan added this)
	private void statement() throws InvalidInputException {
		printIndented("Enter <statement>");
		indentationLevel++;

		switch (nextLexeme.getToken()) {//each of the method calls are commented out until they are implemented
		case KEYWORD_IF:
			processLexeme(Token.KEYWORD_IF);
			// parenExpression();
			statement();
			if (nextLexeme.getToken() == Token.KEYWORD_ELSE) {
				processLexeme(Token.KEYWORD_ELSE);
				statement();
			}
			break;
		
		case KEYWORD_WHILE:
			processLexeme(Token.KEYWORD_WHILE);
			//parenExpression();
			statement();
			break;
			
		case KEYWORD_DO:
			processLexeme(Token.KEYWORD_DO);
			statement();
			processLexeme(Token.KEYWORD_WHILE);
			//parenExpression();
			processLexeme(Token.SEMICOLON);
			break;
			
		case KEYWORD_FOR:
			//forLoop();
			break;
		case KEYWORD_SWITCH:
			//switchCase();
			break;
		
		case KEYWORD_RETURN:
			processLexeme(Token.KEYWORD_RETURN);
			if (nextLexeme.getToken() != Token.SEMICOLON) ;//expression();
			processLexeme(Token.SEMICOLON);
			break;
		
		case KEYWORD_BREAK:
			processLexeme(Token.KEYWORD_BREAK);
			if (nextLexeme.getToken() == Token.IDENTIFIER)
				processLexeme(Token.IDENTIFIER);
			processLexeme(Token.SEMICOLON);
			break;

		case KEYWORD_CONTINUE:
			processLexeme(Token.KEYWORD_CONTINUE);
			if (nextLexeme.getToken() == Token.IDENTIFIER)
				processLexeme(Token.IDENTIFIER);
			processLexeme(Token.SEMICOLON);
			break;

		case KEYWORD_THROW:
			processLexeme(Token.KEYWORD_THROW);
			//expression();
			processLexeme(Token.SEMICOLON);
			break;
		
		case KEYWORD_TRY:
			// try
			break;
		
		case MODIFIER:
			if (!nextLexeme.getLexeme().equals("synchronized"))
				throw new InvalidInputException("Invalid input: " + nextLexeme.getLexeme());
			processLexeme(Token.MODIFIER);
			//parenExpression();
			block();
			break;
		
		case LEFT_BRACE:
			block();
			break;
		
		case SEMICOLON:
			processLexeme(Token.SEMICOLON);
			break;
			
		case IDENTIFIER:
			processLexeme(Token.IDENTIFIER);
			processLexeme(Token.COLON);
			statement();
			break;
		
		default:
			//expression();
			break;
			
		}//end switch
		
		indentationLevel--;
		printIndented("Exit <statement>");
	}

	// <variable_init>
	private void variableInit() throws InvalidInputException {
		printIndented("Enter <variable_init>");
		indentationLevel++;
		
		if (nextLexeme.getToken() == Token.LEFT_BRACE) arrayInit();
		else ; //expression();
		
		indentationLevel--;
		printIndented("Exit <variable_init>");
	}
	
	// <array_init>
	private void arrayInit() throws InvalidInputException {
		printIndented("Enter <array_init>");
		indentationLevel++;
		
		processLexeme(Token.LEFT_BRACE);
		
		if (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			variableInit();
			while(nextLexeme.getToken() != Token.RIGHT_BRACE) {
				processLexeme(Token.COMMA);
				variableInit();
			} // end while
		} // end if
		
		processLexeme(Token.RIGHT_BRACE);
		
		indentationLevel--;
		printIndented("Exit <array_init>");
	} // end array_init

	/**
	* Checks if the current lexeme's associated token is equal to the given token,
	* prints out the current lexeme, and moves to the next lexeme in the input string
	*
	* @param token Expected token
	* @param optional True is this token is optional, false if not
	* @throws InvalidInputException
	*/
	private void processLexeme(Token token) throws InvalidInputException {
		if (nextLexeme.getToken() == token) {
			printIndented(nextLexeme.toString());
			nextLexeme = lex.nextLexeme();
		} else{
			throw new InvalidInputException("Invalid input: " + nextLexeme.getLexeme());
		}
	}
	
	private void printIndented(String toPrint) {
		for (int i = 0; i < indentationLevel; i++) {
			System.out.print("  ");
		}
		System.out.println(toPrint);
	}
}
