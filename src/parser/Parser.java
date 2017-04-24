package parser;

import interfaces.ParserInterface;
import types.Lexeme;
import types.Token;

public class Parser implements ParserInterface{

	LexicalAnalyzer lex;
	Lexeme nextLexeme;
	int indentationLevel;
	String returnString; //string to return (only needed for GUI)

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
		returnString = "";
	} // end constructor

	@Override
	// begin the recursive descent process
	public void start() {
		program(); // <program>
	} // end start()

	// <qualified_identifier> = <identifier> {"." <identifier>};
	private void qualifiedIdentifier() {
		printIndented("Enter <qualified_identifier>");
		indentationLevel++;

		processLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.DOT) {
			processLexeme(Token.DOT);
			processLexeme(Token.IDENTIFIER);
		} // end while

		indentationLevel--;
		printIndented("Exit <qualified_identifier>");
	} // end qualifiedIdentifier()

	// <program> = ["package" <qualified_identifier>] ";" {<import>} <class>;
	private void program() {
		printIndented("Enter <program>");
		indentationLevel++;

		if (nextLexeme.getToken() == Token.KEYWORD_PACKAGE) {
			processLexeme(Token.KEYWORD_PACKAGE);
			qualifiedIdentifier(); // <qualified_identifier>
			processLexeme(Token.SEMICOLON);
		} // end if

		while (nextLexeme.getToken() == Token.KEYWORD_IMPORT) {
			importRule(); // <import>
		}

		classRule(); // <class>

		indentationLevel--;
		printIndented("Exit <program>");
	} // end program()

	// <import> = "import" ["static"] <identifier> {"." <identifier>} [".*"] ";" ;
	private void importRule() {
		printIndented("Enter <import>");
		indentationLevel++;

		processLexeme(Token.KEYWORD_IMPORT);
		// must check lexeme itself as "static" is a modifier
		if (nextLexeme.getLexeme().equals("static")) {
			processLexeme(Token.MODIFIER);
		}

		processLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.DOT) {
			processLexeme(Token.DOT);
			if (nextLexeme.getLexeme().equals("*")) {
				// asterisk is an infix operator
				processLexeme(Token.INFIX_OPERATOR);
				break;
			} else {
				processLexeme(Token.IDENTIFIER);
			} // end if/else
		} // end while

		processLexeme(Token.SEMICOLON);

		indentationLevel--;
		printIndented("Exit <import>");
	} // end importRule()

	// <class> = {<modifier>} <class declaration>;
	private void classRule() {
		printIndented("Enter <class>");
		indentationLevel++;

		while (nextLexeme.getToken() == Token.MODIFIER) {
			processLexeme(Token.MODIFIER);
		} // end while

		classDeclaration(); // <class_declaration>

		indentationLevel--;
		printIndented("Exit <class>");
	} // end classRule()

	// <class_declaration> = "class" <identifier> [<type_arguments>][<extends>]
	// [<implements>] <class_body>;
	private void classDeclaration() {
		printIndented("Enter <class_declaration>");
		indentationLevel++;

		processLexeme(Token.KEYWORD_CLASS);
		processLexeme(Token.IDENTIFIER);

		if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET)
			typeArguments(); // <type_arguments>
		if (nextLexeme.getToken() == Token.KEYWORD_EXTENDS)
			extendsRule(); // <extends>
		if (nextLexeme.getToken() == Token.KEYWORD_IMPLEMENTS)
			implementsRule(); // <implements>

		classBody(); // <class_body>

		indentationLevel--;
		printIndented("Exit <class_declaration>");
	}

	// <extends> = "extends" <identifier>;
	private void extendsRule() {
		printIndented("Enter <extends>");
		indentationLevel++;

		processLexeme(Token.KEYWORD_EXTENDS);
		processLexeme(Token.IDENTIFIER);

		indentationLevel--;
		printIndented("Exit <extends>");
	} // end extendsRule()

	// <implements> = "implements" <identifier> {',' <identifier>};
	private void implementsRule() {
		printIndented("Enter <implements>");
		indentationLevel++;

		processLexeme(Token.KEYWORD_IMPLEMENTS);
		processLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.COMMA) {
			processLexeme(Token.COMMA);
			processLexeme(Token.IDENTIFIER);
		} // end while

		indentationLevel--;
		printIndented("Exit <implements>");
	} // end implementsRule()

	// <class_body> = '{' {<class body statement>} '}';
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
	} // end <class_body>

	// <class_body_statement> = ';'
	//   | ["static"] <block>;
	//   | {<modifier>} <class_body_declaration>
	private void classBodyStatement() {
		printIndented("Enter <class_body_statement>");
		indentationLevel++;

		switch (nextLexeme.getToken()) {

		// semicolon: this is an empty statement
		case SEMICOLON:
			processLexeme(Token.SEMICOLON);
			break;

		// left brace: this is the opening of a block
		case LEFT_BRACE:
			block(); // <block>
			break;

		// otherwise, assume this is a declaration
		default:
			while (nextLexeme.getToken() == Token.MODIFIER) {
				processLexeme(Token.MODIFIER);
			} // end while

			if (nextLexeme.getToken() == Token.LEFT_BRACE) {
				block(); // <block>
			} else {
				classBodyDeclaration(); // <class_body_declaration>
			} // end if/else

		} // end switch

		indentationLevel--;
		printIndented("Exit <class_body_statement>");
	} // end classBodyStatement()

	// <class_body_declaration> = <class_declaration>
	//    | "void" <identifier> <method_declaration>
	//    | <identifier> <method_declaration>
	//    | <type> <identifier> <method_declaration>
	//    | <type> <identifier> <field_declaration> ";";
	private void classBodyDeclaration() {
		printIndented("Enter <class_body_declaration>");
		indentationLevel++;

		switch (nextLexeme.getToken()) {

		// "class": this is a class declaration
		case KEYWORD_CLASS:
			classDeclaration(); // <class_declaration>
			break;

		// "void": this is a void method declaration
		case KEYWORD_VOID:
			processLexeme(Token.KEYWORD_VOID);
			processLexeme(Token.IDENTIFIER);
			methodDeclaration(); // <method_declaration>
			break;

		// identifiers and primitive types are handled together
		// as they could both by types
		case IDENTIFIER:
		case PRIMITIVE_TYPE:
			processLexeme(nextLexeme.getToken());
			if (nextLexeme.getToken() == Token.LEFT_PAREN) {
				// if immediately followed by a left paren,
				// this must be a constructor delcaration
				methodDeclaration(); // <method_declaration>
			}  else { // otherwise, we know that this is a type
				// {"[]"}
				while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
					processLexeme(Token.LEFT_BRACKET);
					processLexeme(Token.RIGHT_BRACKET);
				} // end while

				processLexeme(Token.IDENTIFIER);

				if (nextLexeme.getToken() == Token.LEFT_PAREN) {
					// if identifier followed by left paren,
					// this is a method declaration
					methodDeclaration(); // <method_declaration>
				} else {
					// if it isn't a left paren, this is a field declaration
					fieldDeclaration(); // <field_declaration>
					processLexeme(Token.SEMICOLON);
				}
			}

			break;

		default:
			error();

		} // end switch

		indentationLevel--;
		printIndented("Exit <class_body_declaration>");
	} // end classBodyDeclaration()

	// <field_declaration> = {"[]"} ["=" <variable_init>] <variable_declarators_half>;
	private void fieldDeclaration() {
		printIndented("Enter <field_declaration>");
		indentationLevel++;

		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR) {
			processLexeme(Token.ASSIGNMENT_OPERATOR);
			variableInit(); // <variable_init>
		}

		variableDeclaratorsHalf(); // <variable_declarators_half>

		indentationLevel--;
		printIndented("Exit <field_declaration>");
	} // end fieldDeclaration()

	// <variable_declarator> = <identifier> {'[]'} ["=" <variable_init>];
	private void variableDeclarator() {
		printIndented("Enter <variable_declarator>");
		indentationLevel++;

		processLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR) {
			processLexeme(Token.ASSIGNMENT_OPERATOR);
			variableInit(); // <variable_init>
		}

		indentationLevel--;
		printIndented("Exit <variable_declarators>");
	} // end variableDeclarator()

	// <variable_declarators> = <variable_declarator> <variable_declarators_half>;
	private void variableDeclarators() {
		printIndented("Enter <variable_declarators>");
		indentationLevel++;

		variableDeclarator(); // <variable_declarator>

		variableDeclaratorsHalf(); // <variable_declarators_half>

		indentationLevel--;
		printIndented("Exit <variable_declarators>");
	} // end variableDeclarators()

	// <variable_declarators_half> = {"," <variable_declarator>};
	private void variableDeclaratorsHalf() {
		printIndented("Enter <variable_declarators_half>");
		indentationLevel++;

		while (nextLexeme.getToken() == Token.COMMA) {
			processLexeme(Token.COMMA);
			variableDeclarator(); // <variable_declarator>
		}

		indentationLevel--;
		printIndented("Exit <variable_declarators_half>");
	}

	// <method_declaration> = <parameters>
	// ["throws" <qualified_identifier> {"," <qualified_identifier>}]
	private void methodDeclaration() {
		printIndented("Enter <method_declaration>");
		indentationLevel++;

		parameters(); // <parameters>

		if (nextLexeme.getToken() == Token.KEYWORD_THROWS) {
			processLexeme(Token.KEYWORD_THROWS);
			qualifiedIdentifier(); // <qualified_identifier>

			while (nextLexeme.getToken() == Token.COMMA) {
				processLexeme(Token.COMMA);
				qualifiedIdentifier(); // <qualified_identifier>
			} // end while

		} // end if

		if (nextLexeme.getToken() == Token.SEMICOLON) {
			processLexeme(Token.SEMICOLON); // empty statement
		} else  {
			block(); // <block>
		}

		indentationLevel--;
		printIndented("Exit <method_declaration>");
	}

	// <parameters> = "(" [<parameter> {, <parameter>}] ")";
	private void parameters() {
		printIndented("Enter <parameters>");
		indentationLevel++;

		processLexeme(Token.LEFT_PAREN);

		if (nextLexeme.getToken() != Token.RIGHT_PAREN) {

			parameter(); // <parameter>
			while (nextLexeme.getToken() == Token.COMMA) {
				processLexeme(Token.COMMA);
				parameter(); // <parameter>
			} // end while

		} // end if

		processLexeme(Token.RIGHT_PAREN);

		indentationLevel--;
		printIndented("Exit <parameters>");
	}

	// <parameter> = {<modifier>} <type> <identifier>{"[]"};
	private void parameter() {
		printIndented("Enter <parameter>");
		indentationLevel++;

		while (nextLexeme.getToken() == Token.MODIFIER) {
			processLexeme(Token.MODIFIER);
		}

		type(); // <type>

		processLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		}

		indentationLevel--;
		printIndented("Exit <parameter>");
	} // end parameter()

	// <block> = '{' {<block_statement> }"}";
	private void block() {
		printIndented("Enter <block>");
		indentationLevel++;

		processLexeme(Token.LEFT_BRACE);

		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			blockStatement(); // <block_statement>
		}

		processLexeme(Token.RIGHT_BRACE);

		indentationLevel--;
		printIndented("Exit <block>");
	} // end block()

	// <block_statement> = {<modifier>} (
	// <class_declaration>
	//   | <local_variable_declaration>
	//   | <identifier> ":" <statement>
	//   | <identifier> [<type_arguments>] {"." <identifier> [<type_arguments>]} {"[]"} <variable_declarators>
	//   | <identifier> {"." <identifier>} <expression_from_block>
	private void blockStatement() {
		printIndented("Enter <block_statement>");
		indentationLevel++;

		// if the first lexeme is "synchronized," this is a synchronized block
		// which is handled in statement();
		if (nextLexeme.getLexeme().equals("synchronized")) {
			statement(); // <statement>

			// exit the method
			indentationLevel--;
			printIndented("Exit <block_statement>");

			return;
		} // end if

		// cycle through all modifiers
		while (nextLexeme.getToken() == Token.MODIFIER) {
			processLexeme(Token.MODIFIER);
		}

		switch (nextLexeme.getToken()) {
		case KEYWORD_CLASS:
			classDeclaration(); // <class_declaration>
			break;

		case PRIMITIVE_TYPE:
			localVariableDeclaration(); // <local_variable_declaration>
			break;

		// identifier could either be a type or the start of an expression
		case IDENTIFIER:
			boolean typeArguments = false; // boolean to detect syntax errors

			processLexeme(Token.IDENTIFIER);

			// if the first token following the identifier is an
			// assignment operator, this is an expression
			if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR
					|| nextLexeme.getToken() == Token.OPERATOR_INCREMENT
					|| nextLexeme.getToken() == Token.OPERATOR_DECREMENT) {
				expressionFromBlock(); // <expression_from_block>
				break;
			}

			// if the next token is a colon, this is a statement following a label
			if (nextLexeme.getToken() == Token.COLON) {
				processLexeme(Token.COLON);
				statement(); // <statement>
			} else {
				if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET) {
					typeArguments(); // <type_arguments>
					typeArguments = true;
				} // end if
				while (nextLexeme.getToken() == Token.DOT) {
					processLexeme(Token.DOT);
					if (nextLexeme.getToken() != Token.IDENTIFIER) {
						//EXPRESSION: IDENTIFIER SUFFIX STARTING WITH DOT
						if (typeArguments)
							error(); // if any type arguments have occurred before this, error
						expressionFromBlock(); // <expression_from_block>
						break;
					} // end if
					processLexeme(Token.IDENTIFIER);
					if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET) {
						typeArguments(); // <type_arguments>
						typeArguments = true;
					} // end if
				} // end while

				if (nextLexeme.getToken() == Token.LEFT_PAREN) {
					// EXPRESSION: IDENTIFIER SUFFIX STARTING WITH LEFT PAREN
					if (typeArguments) error();
					expressionFromBlock();
					break;
				}
				while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
					processLexeme(Token.LEFT_BRACKET);
					processLexeme(Token.RIGHT_BRACKET);
				} // end while
				variableDeclarators(); // <variable_declarators>
			}
			break;

		default:
			statement(); // <statement>
			break;

		} // end switch

		indentationLevel--;
		printIndented("Exit <block_statement>");
	}

	// this needs to be finished
	private void expressionFromBlock() {
		printIndented("Enter <expression_from_block>");
		indentationLevel++;

		if (nextLexeme.getToken() == Token.LEFT_PAREN) {
			arguments();
		} else if (nextLexeme.getToken() == Token.DOT) {
			// etc
		}

		if (nextLexeme.getToken() == Token.OPERATOR_INCREMENT || nextLexeme.getToken() == Token.OPERATOR_DECREMENT) {
			postfixOperator();
		}

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR
			|| nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET
			|| nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET
		) {
			assignmentOperator();
			expression1();
		}

		processLexeme(Token.SEMICOLON);

		indentationLevel--;
		printIndented("Exit <expression_from_block>");
	} // end expressionFromBlock()

	// <local_variable_declaration> = <type> <variable_declarators>;
	private void localVariableDeclaration() {
		printIndented("Enter <local_variable_declaration>");
		indentationLevel++;

		type(); // <type>
		variableDeclarators(); // <variable_declarators>

		indentationLevel--;
		printIndented("Exit <local_variable_declaration>");
	} // end <local_variable_declaration>

	// <type> = <primitive_type> {"[]"}
	//   | <identifier> <type_half>;
	private void type() {
		printIndented("Enter <type>");
		indentationLevel++;

		if (nextLexeme.getToken() == Token.PRIMITIVE_TYPE) {
			processLexeme(Token.PRIMITIVE_TYPE);
			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				processLexeme(Token.LEFT_BRACKET);
				processLexeme(Token.RIGHT_BRACKET);
			} // end while
		} else {
			processLexeme(Token.IDENTIFIER);
			typeHalf(); // <type_half>
		} // end else

		indentationLevel--;
		printIndented("Exit <type>");
	} // end type()

	// <type_half> = [<type_arguments>] {"." <identifier> [type_arguments]}  {"[]"};
	private void typeHalf() {
		printIndented("Enter <type_half>");
		indentationLevel++;

		if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET)
			typeArguments(); // <type_arguments>

		while (nextLexeme.getToken() == Token.DOT) {
			processLexeme(Token.DOT);
			processLexeme(Token.IDENTIFIER);
			if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET)
				typeArguments(); // <type_arguments>
		} // end while

		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			processLexeme(Token.LEFT_BRACKET);
			processLexeme(Token.RIGHT_BRACKET);
		} // end while

		indentationLevel--;
		printIndented("Exit <type_half>");
	} // end typeHalf()

	// <type_arguments> = "<" <type_argument> {"," <type_argument>} ">";
	private void typeArguments() {
		printIndented("Enter <type_arguments>");
		indentationLevel++;

		processLexeme(Token.LEFT_ANGLEBRACKET);

		typeArgument(); // <type_argument>

		while (nextLexeme.getToken() == Token.COMMA) {
			processLexeme(Token.COMMA);
			typeArgument(); // <type_argument>
		}

		processLexeme(Token.RIGHT_ANGLEBRACKET);

		indentationLevel--;
		printIndented("Exit <type_arguments>");
	} // end typeArguments()

	// <type_argument> = <type> | "?" [ ("super" | "extends") <type>];
	private void typeArgument() {
		printIndented("Enter <type_argument>");
		indentationLevel++;

		if (nextLexeme.getToken() == Token.QUESTION_MARK) {
			processLexeme(Token.QUESTION_MARK);
			if (nextLexeme.getToken() == Token.KEYWORD_SUPER || nextLexeme.getToken() == Token.KEYWORD_EXTENDS) {
				processLexeme(nextLexeme.getToken());
				type(); // <type>
			}
		} else {
			type(); // <type>
		}

		indentationLevel--;
		printIndented("Exit <type_argument>");
	} // end typeArgument()

	// <variable_init> = <expression> | <array_init>;
	private void variableInit() {
		printIndented("Enter <variable_init>");
		indentationLevel++;

		if (nextLexeme.getToken() == Token.LEFT_BRACE) {
			arrayInit(); // <array_init>
		} else {
			expression(); // <expression>
		}

		indentationLevel--;
		printIndented("Exit <variable_init>");
	} // end variableInit()

	// <array_init> = "{" [<variable_init> {"," <variable_init>}] "}";
	private void arrayInit() {
		printIndented("Enter <array_init>");
		indentationLevel++;

		processLexeme(Token.LEFT_BRACE);

		if (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			variableInit(); // <variable_init>
			while(nextLexeme.getToken() != Token.RIGHT_BRACE) {
				processLexeme(Token.COMMA);
				variableInit(); // <variable_init>
			} // end while
		} // end if

		processLexeme(Token.RIGHT_BRACE);

		indentationLevel--;
		printIndented("Exit <array_init>");
	} // end array_init

	// <statement> = "if" <paren_expression> ["else" <statement>]
	//    | "while" <paren_expression> <statement>
	//    | "do" <statement> "while" <paren_expression> ";"
	//    | "for" "(" [{<modifier>} <type> <identifier> {"[]"}] ["=" <variable_init>] <for_arguments> ")" <statement>
	//    | "assert" <expression> [:<expression>] ";"
	//    | "switch" <paren_expression> "{" <cases> "}"
	//    | "return" [<expression>] ";"
	//    | "break" [<identifier>] ";"
	//    | "continue" [<identifier>] ";"
	//    | "throw" <expression> ";"
	//    | "try" <block> [<catches>] ["finally" <block>]
	//    | "synchronized" <paren_expression> <block>
	//    | <block>
	//    | ";"
	//    | <identifier> ":" <statement>
	//    | <identifier> <expression_half> ";"
	//    | <expression> ";" ;
	private void statement() {
		printIndented("Enter <statement>");
		indentationLevel++;

		switch (nextLexeme.getToken()) {

		// "if" <paren_expression> ["else" <statement>]
		case KEYWORD_IF:
			processLexeme(Token.KEYWORD_IF);
			parenExpression(); // <paren_expression>
			statement(); // <statement>
			if (nextLexeme.getToken() == Token.KEYWORD_ELSE) {
				processLexeme(Token.KEYWORD_ELSE);
				statement(); // <statement>
			} // end if
			break;

		// "while" <paren_expression> <statement>
		case KEYWORD_WHILE:
			processLexeme(Token.KEYWORD_WHILE);
			parenExpression(); // <paren_expression>
			statement(); // <statement>
			break;

		// "do" <statement> "while" <paren_expression> ";"
		case KEYWORD_DO:
			processLexeme(Token.KEYWORD_DO);
			statement(); // <statement>
			processLexeme(Token.KEYWORD_WHILE);
			parenExpression(); // <paren_expression>
			processLexeme(Token.SEMICOLON);
			break;

		// this is a bit messy

		//"for" "(" [{<modifier>} <type> <identifier> {"[]"}]
		// ["=" <variable_init>] <for_arguments> ")" <statement>
		case KEYWORD_FOR:
			processLexeme(Token.KEYWORD_FOR);
			processLexeme(Token.LEFT_PAREN);
			if (nextLexeme.getToken() != Token.COLON && nextLexeme.getToken() != Token.SEMICOLON) {
				while (nextLexeme.getToken() == Token.MODIFIER)
					processLexeme(Token.MODIFIER);
				type(); // <type>
				processLexeme(Token.IDENTIFIER);
				while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
					processLexeme(Token.LEFT_BRACKET);
					processLexeme(Token.RIGHT_BRACKET);
				}

				if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR
						|| nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET
						|| nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET
					) {
					assignmentOperator(); // <assignment_operator>
					variableInit(); // <variable_init>
				}
			}
			forArguments(); // <for_arguments>
			processLexeme(Token.RIGHT_PAREN);
			statement(); // <statement>
			break;

		// "switch" <paren_expression> "{" <cases> "}"
		case KEYWORD_SWITCH:
			processLexeme(Token.KEYWORD_SWITCH);
			parenExpression(); // <paren_expression>
			processLexeme(Token.LEFT_BRACE);
			cases(); // <cases>
			processLexeme(Token.RIGHT_BRACE);
			break;

		// "assert" <expression> [:<expression>] ";"
		case KEYWORD_ASSERT:
			processLexeme(Token.KEYWORD_ASSERT);
			expression(); // <expression>
			if (nextLexeme.getToken() == Token.COLON) {
				processLexeme(Token.COLON);
				expression(); // <expression>
			}
			processLexeme(Token.SEMICOLON);
			break;

		// "return" [<expression>] ";"
		case KEYWORD_RETURN:
			processLexeme(Token.KEYWORD_RETURN);
			if (nextLexeme.getToken() != Token.SEMICOLON) {
				expression(); // <expression>
			}
			processLexeme(Token.SEMICOLON);
			break;

		// "break" [<identifier>] ";"
		case KEYWORD_BREAK:
			processLexeme(Token.KEYWORD_BREAK);
			if (nextLexeme.getToken() == Token.IDENTIFIER)
				processLexeme(Token.IDENTIFIER);
			processLexeme(Token.SEMICOLON);
			break;

		// "continue" [<identifier>] ";"
		case KEYWORD_CONTINUE:
			processLexeme(Token.KEYWORD_CONTINUE);
			if (nextLexeme.getToken() == Token.IDENTIFIER)
				processLexeme(Token.IDENTIFIER);
			processLexeme(Token.SEMICOLON);
			break;

		// "throw" <expression> ";"
		case KEYWORD_THROW:
			processLexeme(Token.KEYWORD_THROW);
			expression(); // <expression>
			processLexeme(Token.SEMICOLON);
			break;

		// "try" <block> [<catches>] ["finally" <block>]
		case KEYWORD_TRY:
			processLexeme(Token.KEYWORD_TRY);
			block(); // <block>
			if (nextLexeme.getToken() == Token.KEYWORD_CATCH) {
				catches(); // <catches>
			}
			if (nextLexeme.getToken() == Token.KEYWORD_FINALLY) {
				processLexeme(Token.KEYWORD_FINALLY);
				block(); // <block>
			}
			break;

		// "synchronized" <paren_expression> <block>
		case MODIFIER:
			if (!nextLexeme.getLexeme().equals("synchronized"))
				error();
			processLexeme(Token.MODIFIER);
			parenExpression(); // <paren_expression>
			block(); // <block>
			break;

		// <block>
		case LEFT_BRACE:
			block();
			break;

		// ";"
		case SEMICOLON:
			processLexeme(Token.SEMICOLON);
			break;

		// <identifier> ":" <statement>
		// | <identifier> <expression_half>
		case IDENTIFIER:
			processLexeme(Token.IDENTIFIER);

			if (nextLexeme.getToken() != Token.COLON) {
				expressionHalf(); // <expression_half>
				processLexeme(Token.SEMICOLON);
			} else {
				processLexeme(Token.COLON);
				statement(); // <statement>
			}
			break;

		// <expression>
		default:
			expression(); // <expression>
			processLexeme(Token.SEMICOLON);
			break;

		}//end switch

		indentationLevel--;
		printIndented("Exit <statement>");
	} // end statement()

	// <catches> = <catch> {<catch>};
	private void catches() {
		printIndented("Enter <catches>");
		indentationLevel++;

		while (nextLexeme.getToken() == Token.KEYWORD_CATCH) {
			catchRule(); // <catch>
		} // end while

		indentationLevel--;
		printIndented("Exit <catches>");
	} // end catches()

	// <catch> = "catch" "(" {<modifier>} <qualified_identifier> <identifier> ")" <block>;
	private void catchRule() {
		printIndented("Enter <catch>");
		indentationLevel++;

		processLexeme(Token.KEYWORD_CATCH);
		processLexeme(Token.LEFT_PAREN);

		while (nextLexeme.getToken() == Token.MODIFIER) {
			processLexeme(Token.MODIFIER);
		} // end while

		qualifiedIdentifier(); // <qualified_identifier>
		processLexeme(Token.IDENTIFIER);
		processLexeme(Token.RIGHT_PAREN);
		block(); // <block>

		indentationLevel--;
		printIndented("Exit <catch>");
	}

	// <for_arguments> = ";" [<expression>] ";" <expression> {"," <expression>}
	//    | ":" <expression>;
	private void forArguments() {
		printIndented("Enter <for_arguments>");
		indentationLevel++;

		if (nextLexeme.getToken() == Token.SEMICOLON) {
			processLexeme(Token.SEMICOLON);
			if (nextLexeme.getToken() != Token.SEMICOLON) expression();
			processLexeme(Token.SEMICOLON);
			if (nextLexeme.getToken() != Token.RIGHT_PAREN) {
				expression(); // <expression>
				while (nextLexeme.getToken() == Token.COMMA) {
					processLexeme(Token.COMMA);
					expression(); // <expression>
				} // end while
			} // end if
		} else {
			processLexeme(Token.COLON);
			expression(); // <expression>
		} // end else

		indentationLevel--;
		printIndented("Exit <for_arguments>");
	} // end forArguments()

	// <cases> = { ("case" (<identifier> | <expression>) | "default") ":" {<block_statement>} };
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
				if (nextLexeme.getToken() == Token.IDENTIFIER) {
					processLexeme(Token.IDENTIFIER);
				} else {
					expression(); // <expression>
				}
				processLexeme(Token.COLON);
				break;

			default:
				error();

			} // end switch

			while (nextLexeme.getToken() != Token.RIGHT_BRACE && nextLexeme.getToken() != Token.KEYWORD_CASE && nextLexeme.getToken() != Token.KEYWORD_DEFAULT) {
				blockStatement(); // <block_statement>
			}
		} // end cases

		indentationLevel--;
		printIndented("Exit <cases>");
	} // end cases()

	private void expression() {
		printIndented("Enter <expression>");
		indentationLevel++;

		expression1();
		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR
				|| nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET
				|| nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET
			) {
			assignmentOperator();
			expression1();
		}

		indentationLevel--;
		printIndented("Exit <expression>");
	} // end <expression>

	private void expression1() {
		printIndented("Enter <expression1>");
		indentationLevel++;

		expression2();
		if (nextLexeme.getToken() == Token.QUESTION_MARK) {
			processLexeme(Token.QUESTION_MARK);
			expression();
			processLexeme(Token.COLON);
			expression1();
		}

		indentationLevel--;
		printIndented("Exit <expression1>");
	}

	private void expressionHalf() {
		printIndented("Enter <expression_half>");
		indentationLevel++;

		while (nextLexeme.getToken() == Token.DOT) {
			processLexeme(Token.DOT);
			if (nextLexeme.getToken() != Token.IDENTIFIER) break;
			processLexeme(Token.IDENTIFIER);
		}

		if (nextLexeme.getToken() == Token.LEFT_PAREN) {
			arguments();
		}

		if (nextLexeme.getToken() == Token.OPERATOR_INCREMENT || nextLexeme.getToken() == Token.OPERATOR_DECREMENT) {
			postfixOperator();
		}

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR
				|| nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET
				|| nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET
			) {
			assignmentOperator();
			expression1();
		}

		indentationLevel--;
		printIndented("Exit <expression_half>");
	}

	private void expression2() {
		printIndented("Enter <expression2>");
		indentationLevel++;

		expression3();

		if (nextLexeme.getToken() == Token.KEYWORD_INSTANCEOF) {
			processLexeme(Token.KEYWORD_INSTANCEOF);
			type();
		} else {
			while (nextLexeme.getToken() == Token.INFIX_OPERATOR
					|| nextLexeme.getToken() == Token.OPERATOR_PLUS
					|| nextLexeme.getToken() == Token.OPERATOR_MINUS
					|| nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET
					|| nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET ) {
				infixOperator();
				expression3();
			}
		}

		indentationLevel--;
		printIndented("Exit <expression2>");
	}

	private void expression3() {
		printIndented("Enter <expression3>");
		indentationLevel++;

		switch (nextLexeme.getToken()) {

		case PREFIX_OPERATOR:
		case OPERATOR_PLUS:
		case OPERATOR_MINUS:
		case OPERATOR_INCREMENT:
		case OPERATOR_DECREMENT:
			prefixOperator();
			expression3();
			break;

		default:
			expressionUnit();
			while (nextLexeme.getToken() == Token.DOT) {
				selector();
			}
			while (nextLexeme.getToken() == Token.OPERATOR_INCREMENT || nextLexeme.getToken() == Token.OPERATOR_DECREMENT) {
				postfixOperator();
			}
			break;

		}

		indentationLevel--;
		printIndented("Exit <expression3>");
	}

	private void expressionUnit() {
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
			identifierRest();
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

	private void identifierRest() {
		printIndented("Enter <identifier_rest>");
		indentationLevel++;

		while (nextLexeme.getToken() == Token.DOT) {
			processLexeme(Token.DOT);
			if (nextLexeme.getToken() != Token.IDENTIFIER) break;
			processLexeme(Token.IDENTIFIER);
		}

		if (nextLexeme.getToken() == Token.LEFT_PAREN) {
			arguments();
		}

		indentationLevel--;
		printIndented("Exit <identifier_rest>");
	}

	//<arguments>
	private void arguments() {
		printIndented("Enter <arguments>");
		indentationLevel++;

		processLexeme(Token.LEFT_PAREN);

		if (nextLexeme.getToken() != Token.RIGHT_PAREN) {
			expression();
			while (nextLexeme.getToken() != Token.RIGHT_PAREN) {
				processLexeme(Token.COMMA);
				expression();
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
		expression();
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

		case CHAR_LITERAL:
			processLexeme(Token.CHAR_LITERAL);
			break;

		case STRING_LITERAL:
			processLexeme(Token.STRING_LITERAL);
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

	private void selector() {
		printIndented("Enter <infix_operator>");
		indentationLevel++;

		processLexeme(Token.DOT);

		switch (nextLexeme.getToken()) {
		case IDENTIFIER:
			processLexeme(Token.IDENTIFIER);
			if (nextLexeme.getToken() == Token.LEFT_PAREN) arguments();
			break;
		case KEYWORD_THIS:
			processLexeme(Token.KEYWORD_THIS);
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
			// to be implemented
			break;
		default:
			error();
			// could something else be here?
		}

		indentationLevel--;
		printIndented("Exit <literal>");
	}


	// OPERATORS

	private void infixOperator() {
		printIndented("Enter <infix_operator>");
		indentationLevel++;

		switch (nextLexeme.getToken()) {
		case INFIX_OPERATOR:
		case OPERATOR_PLUS:
		case OPERATOR_MINUS:
			processLexeme(nextLexeme.getToken());
			break;

		case LEFT_ANGLEBRACKET:
			processLexeme(Token.LEFT_ANGLEBRACKET);

			switch (nextLexeme.getToken()) {
			case ASSIGNMENT_OPERATOR:
				processLexeme(Token.ASSIGNMENT_OPERATOR);
				break;
			case LEFT_ANGLEBRACKET:
				processLexeme(Token.LEFT_ANGLEBRACKET);
				break;
			default:
				break;
			} // end switch

			break;

		case RIGHT_ANGLEBRACKET:
			processLexeme(Token.RIGHT_ANGLEBRACKET);

			switch (nextLexeme.getToken()) {
			case ASSIGNMENT_OPERATOR:
				processLexeme(Token.ASSIGNMENT_OPERATOR);
				break;
			case RIGHT_ANGLEBRACKET:
				processLexeme(Token.RIGHT_ANGLEBRACKET);
				if (nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET)
					processLexeme(Token.RIGHT_ANGLEBRACKET);
				break;
			default:
				break;
			} // end switch

			break;

		default:
			error();
		} // end switch

		indentationLevel--;
		printIndented("Exit <infix_operator>");
	}

	private void prefixOperator() {
		printIndented("Enter <prefix_operator>");
		indentationLevel++;

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

		indentationLevel--;
		printIndented("Exit <prefix_operator>");
	}

	private void postfixOperator() {
		printIndented("Enter <postfix_operator>");
		indentationLevel++;

		switch (nextLexeme.getToken()) {
		case OPERATOR_INCREMENT:
		case OPERATOR_DECREMENT:
			processLexeme(nextLexeme.getToken());
			break;
		default:
			error();
		}

		indentationLevel--;
		printIndented("Exit <postfix_operator>");
	}

	private void assignmentOperator() {
		printIndented("Enter <assignment_operator>");
		indentationLevel++;

		switch (nextLexeme.getToken()) {
		case ASSIGNMENT_OPERATOR:
			processLexeme(Token.ASSIGNMENT_OPERATOR);
			break;
		case LEFT_ANGLEBRACKET:
			processLexeme(Token.LEFT_ANGLEBRACKET);
			processLexeme(Token.LEFT_ANGLEBRACKET);
			processLexeme(Token.ASSIGNMENT_OPERATOR);
			break;
		case RIGHT_ANGLEBRACKET:
			processLexeme(Token.RIGHT_ANGLEBRACKET);
			processLexeme(Token.RIGHT_ANGLEBRACKET);
			if (nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET)
				processLexeme(Token.RIGHT_ANGLEBRACKET);
			processLexeme(Token.ASSIGNMENT_OPERATOR);
			break;
		default:
			error();
		}

		indentationLevel--;
		printIndented("Exit <assignment_operator>");
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

	//terminial printing version of printIndented
	/*
	private void printIndented(String toPrint) {
		for (int i = 0; i < indentationLevel; i++) {
			System.out.print("  ");
		}
		System.out.println(toPrint);
	}
	*/

	//GUI version of printIndented
	private void printIndented(String toPrint) {
		for (int i = 0; i < indentationLevel; i++) {
			returnString = returnString + "  ";
		}
		returnString = returnString + toPrint + "\n";
	}

	private void error() {
		System.out.printf("ERROR: Line %d: Invalid input: %s\n", lex.getLineNumber(), nextLexeme.getLexeme());
		System.exit(1);
	}

	public String getReturnString(){
		return returnString;
	}

} // end class
