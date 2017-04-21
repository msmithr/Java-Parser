package parser;

import interfaces.ParserInterface;
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
	public Parser(String inputString) {
		lex = new LexicalAnalyzer(inputString);
		nextLexeme = lex.nextLexeme();
		indentationLevel = 0;
	}

	@Override
	public void start() {
		program();
	}
	
	private void qualifiedIdentifier() {
		printIndented("Enter <qualified_identifier>");
		indentationLevel++;
		
		processLexeme(Token.IDENTIFIER);
		while (nextLexeme.getToken() == Token.DOT) {
			processLexeme(Token.DOT);
			processLexeme(Token.IDENTIFIER);
		}
		
		indentationLevel--;
		printIndented("Exit <qualified_identifier>");
	}
	
	private void program() {
		printIndented("Enter <program>");
		indentationLevel++;
		
		if (nextLexeme.getToken() == Token.KEYWORD_PACKAGE) {
			processLexeme(Token.KEYWORD_PACKAGE);
			qualifiedIdentifier();
		}
		
		while (nextLexeme.getToken() == Token.KEYWORD_IMPORT) importRule();
		
		classRule();
		
		indentationLevel--;
		printIndented("Exit <program>");
	}
	
	private void importRule() {
		printIndented("Enter <import>");
		indentationLevel++;
		
		processLexeme(Token.KEYWORD_IMPORT);
		if (nextLexeme.getLexeme().equals("static"))
			processLexeme(Token.MODIFIER);
		
		processLexeme(Token.IDENTIFIER);
		
		while (nextLexeme.getToken() == Token.DOT) {
			processLexeme(Token.DOT);
			if (nextLexeme.getToken() == Token.INFIX_OPERATOR) {
				if (!nextLexeme.getLexeme().equals("*")) error();
				processLexeme(Token.INFIX_OPERATOR);
				break;
			} else {
				processLexeme(Token.IDENTIFIER);
			}
		}
		
		processLexeme(Token.SEMICOLON);
		
		indentationLevel--;
		printIndented("Exit <import>");
	}
	
	// <class>
	private void classRule() {
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
	private void classDeclaration() {
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
	private void extendsRule() {
		printIndented("Enter <extends>");
		indentationLevel++;

		processLexeme(Token.KEYWORD_EXTENDS);
		processLexeme(Token.IDENTIFIER);

		indentationLevel--;
		printIndented("Exit <extends>");
	}

	// <implements>
	private void implementsRule() {
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
	private void classBody() {
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
	private void classBodyStatement() {
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
	private void classBodyDeclaration() {
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
			error();
		
		} // end switch
		
		indentationLevel--;
		printIndented("Exit <class_body_declaration>");
	} // end classBodyDeclaration
	
	// <field_declaration>
	private void fieldDeclaration() {
		printIndented("Enter <field_declaration>");
		indentationLevel++;
		
		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}
		
		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR) {
			processLexeme(Token.ASSIGNMENT_OPERATOR);
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
	private void variableDeclarators() {
		printIndented("Enter <variable_declarators>");
		indentationLevel++;
		
		processLexeme(Token.IDENTIFIER);
		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}
		
		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR) {
			processLexeme(Token.ASSIGNMENT_OPERATOR);
			variableInit();
		}
		
		while (nextLexeme.getToken() == Token.COMMA) {
			processLexeme(Token.COMMA);
			
			processLexeme(Token.IDENTIFIER);
			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				processLexeme(Token.LEFT_BRACKET);
				processLexeme(Token.RIGHT_BRACKET);
			}
			
			if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR) {
				processLexeme(Token.ASSIGNMENT_OPERATOR);
				variableInit();
			}
		}
		
		indentationLevel--;
		printIndented("Exit <variable_declarators>");
	}
	
	// <method_declaration>
	private void methodDeclaration() {
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
	private void parameters() {
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
	private void block() {
		printIndented("Enter <block>");
		indentationLevel++;
		
		processLexeme(Token.LEFT_BRACE);
		
		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			blockStatement();
		}
		
		processLexeme(Token.RIGHT_BRACE);
		
		indentationLevel--;
		printIndented("Exit <block>");
	}

	private void blockStatement() {
		printIndented("Enter <block_statement>");
		indentationLevel++;
		
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
			
			if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR) {
				processLexeme(Token.ASSIGNMENT_OPERATOR);
				variableInit();
			}
			variableDeclarators();
			processLexeme(Token.SEMICOLON);
			break;
		
		default:
			statement();
			break;
		}
		
		indentationLevel--;
		printIndented("Exit <block_statement>");
	}
	
	// <local_variable_declaration>
	private void localVariableDeclaration() {
		printIndented("Enter <local_variable_declaration>");
		indentationLevel++;
		
		type();
		variableDeclarators();
		
		indentationLevel--;
		printIndented("Exit <local_variable_declaration>");
	}
	
	// <type>
	private void type() {
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
	private void statement() {
		printIndented("Enter <statement>");
		indentationLevel++;

		switch (nextLexeme.getToken()) {//each of the method calls are commented out until they are implemented
		case KEYWORD_IF:
			processLexeme(Token.KEYWORD_IF);
			parenExpression();
			statement();
			if (nextLexeme.getToken() == Token.KEYWORD_ELSE) {
				processLexeme(Token.KEYWORD_ELSE);
				statement();
			}
			break;
		
		case KEYWORD_WHILE:
			processLexeme(Token.KEYWORD_WHILE);
			parenExpression();
			statement();
			break;
			
		case KEYWORD_DO:
			processLexeme(Token.KEYWORD_DO);
			statement();
			processLexeme(Token.KEYWORD_WHILE);
			parenExpression();
			processLexeme(Token.SEMICOLON);
			break;
			
		case KEYWORD_FOR:
			//forLoop();
			break;
			
		case KEYWORD_SWITCH:
			processLexeme(Token.KEYWORD_SWITCH);
			parenExpression();
			processLexeme(Token.LEFT_BRACE);
			cases();
			processLexeme(Token.RIGHT_BRACE);
			break;
		
		case KEYWORD_ASSERT:
			processLexeme(Token.KEYWORD_ASSERT);
			//expression();
			if (nextLexeme.getToken() == Token.COLON) {
				processLexeme(Token.COLON);
				//expression();
			}
			processLexeme(Token.SEMICOLON);
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
				error();
			processLexeme(Token.MODIFIER);
			parenExpression();
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

	private void cases() {
		printIndented("Enter <cases>");
		indentationLevel++;
		
		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			switch (nextLexeme.getToken()) {
			
			case KEYWORD_DEFAULT:
				processLexeme(Token.KEYWORD_DEFAULT);
				processLexeme(Token.COLON);
				break;
			
			case KEYWORD_CASE:
				processLexeme(Token.KEYWORD_CASE);
				if (nextLexeme.getToken() == Token.IDENTIFIER) processLexeme(Token.IDENTIFIER);
				else ; //expression();
				processLexeme(Token.COLON);
				break;
				
			default:
				error();
				
			} // end switch
			
			while (nextLexeme.getToken() != Token.RIGHT_BRACE && nextLexeme.getToken() != Token.KEYWORD_CASE && nextLexeme.getToken() != Token.KEYWORD_DEFAULT) {
				blockStatement();
				//System.exit(0);
			}
		}
		
		indentationLevel--;
		printIndented("Exit <cases>");
	}
	
	// <variable_init>
	private void variableInit() {
		printIndented("Enter <variable_init>");
		indentationLevel++;
		
		if (nextLexeme.getToken() == Token.LEFT_BRACE) arrayInit();
		else ; //expression();
		
		indentationLevel--;
		printIndented("Exit <variable_init>");
	}
	
	// <array_init>
	private void arrayInit() {
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
	
	private void expression_unit() {
		printIndented("Enter <expression_unit>");
		indentationLevel++;
		
		switch (nextLexeme.getToken()) {
		
		case LEFT_PAREN:
			parenExpression();
			break;
			
		case KEYWORD_THIS:
			processLexeme(Token.KEYWORD_THIS);
			if (nextLexeme.getToken() == Token.LEFT_PAREN) arguments();
			break;
		
		case KEYWORD_SUPER:
			processLexeme(Token.KEYWORD_SUPER);
			if (nextLexeme.getToken() == Token.LEFT_PAREN) arguments();
			else {
				processLexeme(Token.DOT);
				processLexeme(Token.IDENTIFIER);
				if (nextLexeme.getToken() == Token.LEFT_PAREN) arguments();
			}
			break;
			
		case KEYWORD_NEW:
			processLexeme(Token.KEYWORD_NEW);
			//creator?
			break;
			
		case IDENTIFIER:
			processLexeme(Token.IDENTIFIER);
			while (nextLexeme.getToken() == Token.DOT) {
				processLexeme(Token.DOT);
				processLexeme(Token.IDENTIFIER);
			}
			//identifier suffix?
			break;
			
		case PRIMITIVE_TYPE:
			processLexeme(Token.PRIMITIVE_TYPE);
			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				processLexeme(Token.LEFT_BRACKET);
				processLexeme(Token.RIGHT_BRACKET);
			}
			processLexeme(Token.DOT);
			processLexeme(Token.KEYWORD_CLASS);
			break;
			
		case KEYWORD_VOID:
			processLexeme(Token.KEYWORD_VOID);
			processLexeme(Token.DOT);
			processLexeme(Token.KEYWORD_CLASS);
			break;
			
		default:
			literal();
			break;
		}
		
		indentationLevel--;
		printIndented("Exit <expression_unit>");
	}
	
	//<arguments>
	private void arguments() {
		printIndented("Enter <arguments>");
		indentationLevel++;
		
		processLexeme(Token.LEFT_PAREN);
		
		if (nextLexeme.getToken() != Token.RIGHT_PAREN) {
			//expression();
			while (nextLexeme.getToken() != Token.RIGHT_PAREN) {
				processLexeme(Token.COMMA);
				//expression();
			}
		}
		
		processLexeme(Token.RIGHT_PAREN);
		
		indentationLevel--;
		printIndented("Exit <arguments>");
	}
	
	// <paren_expression>
	private void parenExpression() {
		printIndented("Enter <paren_expression>");
		indentationLevel++;
		
		processLexeme(Token.LEFT_PAREN);
		//expression();
		processLexeme(Token.RIGHT_PAREN);
		
		indentationLevel--;
		printIndented("Exit <paren_expression>");
	}
	
	// <literal>
	private void literal() {
		printIndented("Enter <literal>");
		indentationLevel++;
		
		switch (nextLexeme.getToken()) {
		
		case INT_LITERAL:
			processLexeme(Token.INT_LITERAL);
			if (nextLexeme.getToken() == Token.DOT) {
				processLexeme(Token.DOT);
				processLexeme(Token.INT_LITERAL);
			}
			break;
			
		case SINGLE_QUOTE:
			processLexeme(Token.SINGLE_QUOTE);
			processLexeme(Token.IDENTIFIER);
			processLexeme(Token.SINGLE_QUOTE);
			break;
			
		case DOUBLE_QUOTE:
			processLexeme(Token.DOUBLE_QUOTE);
			processLexeme(Token.IDENTIFIER);
			processLexeme(Token.DOUBLE_QUOTE);
			break;
			
		case KEYWORD_TRUE:
			processLexeme(Token.KEYWORD_TRUE);
			break;
		
		case KEYWORD_FALSE:
			processLexeme(Token.KEYWORD_FALSE);
			break;
			
		case KEYWORD_NULL:
			processLexeme(Token.KEYWORD_NULL);
			break;
			
		default:
			error();
		}
		
		indentationLevel--;
		printIndented("Exit <literal>");
	}
	
	
	// OPERATORS
	
	private void infix_operator() {
		processLexeme(Token.INFIX_OPERATOR);
	}
	
	private void prefix_operator() {
		switch (nextLexeme.getToken()) {
		case PREFIX_OPERATOR:
		case OPERATOR_PLUS:
		case OPERATOR_MINUS:
		case OPERATOR_INCREMENT:
		case OPERATOR_DECREMENT:
			processLexeme(nextLexeme.getToken());
			break;
		default:
			error();
		}
	}
	
	private void postfix_operator() {
		switch (nextLexeme.getToken()) {
		case OPERATOR_INCREMENT:
		case OPERATOR_DECREMENT:
			processLexeme(nextLexeme.getToken());
			break;
		default:
			error();
		}
	}

	/**
	* Checks if the current lexeme's associated token is equal to the given token,
	* prints out the current lexeme, and moves to the next lexeme in the input string
	*
	* @param token Expected token
	*/
	private void processLexeme(Token token) {
		if (nextLexeme.getToken() == token) {
			printIndented(nextLexeme.toString());
			nextLexeme = lex.nextLexeme();
		} else{
			error();
		}
	}
	
	private void printIndented(String toPrint) {
		for (int i = 0; i < indentationLevel; i++) {
			System.out.print("  ");
		}
		System.out.println(toPrint);
	}
	
	private void error() {
		System.out.printf("ERROR: Invalid input: %s\n", nextLexeme.getLexeme());
		System.exit(1);
	}

} // end class
