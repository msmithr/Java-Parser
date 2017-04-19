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
		classRule();
	}

	// <class>
	private void classRule() throws InvalidInputException {
		System.out.println("Enter <class>");

		while (nextLexeme.getToken() == Token.MODIFIER) {
			processLexeme(Token.MODIFIER);
		}

		classDeclaration();
		
		System.out.println("Exit <class>");
	}
	
	// <class_declaration>
	private void classDeclaration() throws InvalidInputException {
		System.out.println("Enter <class_delcaration>");
		
		processLexeme(Token.KEYWORD_CLASS);
		processLexeme(Token.IDENTIFIER);

		extendsRule();
		implementsRule();

		classBody();
		
		System.out.println("Exit <class_delcaration>");
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

	// <class_body>
	private void classBody() throws InvalidInputException {
		System.out.println("Enter <class_body>");
		processLexeme(Token.LEFT_BRACE);
		
		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			classBodyStatement();
		}
		
		processLexeme(Token.RIGHT_BRACE);
		System.out.println("Exit <class_body>");
	}


	// <class_body_statement>
	private void classBodyStatement() throws InvalidInputException {
		System.out.println("Enter <class_body_statement>");
		
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
				while (nextLexeme.getToken() == Token.MODIFIER) {
					processLexeme(Token.MODIFIER);
				}
				classBodyDeclaration();
			}
		
		} // end switch

		System.out.println("Exit <class_body_statement>");
	} // end classBodyStatement()
	
	// <class_body_declaration>
	private void classBodyDeclaration() throws InvalidInputException {
		System.out.println("Enter <class_body_declaration>");
		
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
		System.out.println("Exit <class_body_declaration>");
	} // end classBodyDeclaration
	
	// <field_declaration>
	private void fieldDeclaration() throws InvalidInputException {
		System.out.println("Enter <field_declaration>");
		
		if (nextLexeme.getToken() == Token.EQUALS) {
			processLexeme(Token.EQUALS);
			if (nextLexeme.getToken() == Token.LEFT_BRACE) ; //arrayInit();
			else ; //expression();
		}
		
		while (nextLexeme.getToken() == Token.COMMA) {
			processLexeme(Token.COMMA);
			variableDeclarators();
		}
		
		System.out.println("Exit <field_declaration>");
	}
	
	// <variable_declarators>
	private void variableDeclarators() throws InvalidInputException {
		System.out.println("Enter <variable_declarators>");
		
		processLexeme(Token.IDENTIFIER);
		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}
		
		if (nextLexeme.getToken() == Token.EQUALS) {
			processLexeme(Token.EQUALS);
			if (nextLexeme.getToken() == Token.LEFT_BRACE) ; //arrayInit();
			else ; //expression();
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
				if (nextLexeme.getToken() == Token.LEFT_BRACE) ; //arrayInit();
				else ; //expression();
			}
		}
		
		System.out.println("Exit <variable_declarators>");
	}
	
	// <method_declaration>
	private void methodDeclaration() throws InvalidInputException {
		System.out.println("Enter <method_declaration>");
		
		parameters();
		if (nextLexeme.getToken() == Token.KEYWORD_THROWS) ; //throws();
		
		if (nextLexeme.getToken() == Token.SEMICOLON) processLexeme(Token.SEMICOLON);
		else block();
		
		System.out.println("Exit <method_declaration>");
	}
	
	// <block>
	private void block() throws InvalidInputException {
		System.out.println("Enter <block>");
		processLexeme(Token.LEFT_BRACE);
		
		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			switch (nextLexeme.getToken()) {
			case MODIFIER:
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
					if (nextLexeme.getToken() == Token.LEFT_BRACE) ; //arrayInit();
					else ; //expression();
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
		System.out.println("Exit <block>");
	}
	
	// <local_variable_declaration>
	private void localVariableDeclaration() throws InvalidInputException {
		System.out.println("Enter <local_variable_declaration>");
		
		type();
		variableDeclarators();
		
		System.out.println("Exit <local_variable_declaration>");
	}
	
	// <type>
	private void type() throws InvalidInputException {
		System.out.println("Enter <type>");
		
		if (nextLexeme.getToken() == Token.PRIMITIVE_TYPE) {
			processLexeme(Token.PRIMITIVE_TYPE);
		} else {
			processLexeme(Token.IDENTIFIER);
		}
		
		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}
		
		System.out.println("Exit <type>");
	}

	// <parameters>
	private void parameters() throws InvalidInputException {
		System.out.println("Enter <parameters>");
		
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
		
		System.out.println("Exit <parameters>");
	}

	//<statement> rule (Nathan added this)
	private void statement() throws InvalidInputException {
		System.out.println("Enter <statement>");

		switch (nextLexeme.getToken()) {//each of the method calls are commented out until they are implemented
		case KEYWORD_IF:
			//ifStatement();
			break;
		case KEYWORD_WHILE:
			//whileLoop();
			break;
		case KEYWORD_DO:
			//doWhileLoop();
			break;
		case KEYWORD_FOR:
			//forLoop();
			break;
		case KEYWORD_SWITCH:
			//switchCase();
			break;
		case KEYWORD_RETURN:
			//returnCall();
			break;
		case KEYWORD_BREAK:
			//breakStatement();
			break;
		case KEYWORD_CONTINUE:
			//continueStatement();//Only need a method because it could have a label (ie goto workaround in Java)
			break;
		case KEYWORD_THROW:
			//throwStatement();
			break;
		case KEYWORD_TRY:
			//tryBlock();
			break;
		//case KEYWORD_SYNCHRONIZED:
			//synchronizedBlock();
			//break;
		case LEFT_BRACE:
			block();//call block method
			break;
		case SEMICOLON:
			processLexeme(Token.SEMICOLON);
			break;
		default:
			throw new InvalidInputException("Invalid input: " + nextLexeme.getLexeme());
		}//end switch
		System.out.println("Exit <statement>");
	}

	// <break>
	private void breakStatement() throws InvalidInputException {
		System.out.println("Enter <break>");

		processLexeme(Token.KEYWORD_BREAK);
		if (nextLexeme.getToken() == Token.IDENTIFIER)
			processLexeme(Token.IDENTIFIER);
		processLexeme(Token.SEMICOLON);

		System.out.println("Exit <break>");
	}

	// <continue>
	private void continueStatement() throws InvalidInputException {
		System.out.println("Enter <continue>");

		processLexeme(Token.KEYWORD_CONTINUE);
		if (nextLexeme.getToken() == Token.IDENTIFIER)
			processLexeme(Token.IDENTIFIER);
		processLexeme(Token.SEMICOLON);

		System.out.println("Exit <continue>");
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
