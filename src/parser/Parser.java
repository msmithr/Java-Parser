/**
 * Implementation of a recursive descent parser for the Java programming
 * language.
 *
 * @author Michael Smith and Nathan Jean
 */

package parser;

import java.util.ArrayDeque;

import interfaces.ParserInterface;
import types.InvalidInputException;
import types.Lexeme;
import types.Token;

public class Parser implements ParserInterface{

	LexicalAnalyzer lex;
	Lexeme nextLexeme;
	int indentationLevel; // level of indentation for the output
	ArrayDeque<String> outputQueue; // queue containing all of the output strings

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
		outputQueue = new ArrayDeque<String>();
	} // end constructor

	@Override
	public ArrayDeque<String> getOutputQueue(){
		return outputQueue;
	}

	@Override
	public String getErrorMessage() {
		return String.format("ERROR: Line %d: Invalid input: %s\n", lex.getLineNumber(), nextLexeme.getLexeme());
	}

	// begin the recursive descent process
	@Override
	public void start() throws InvalidInputException {
		program(); // <program>
	} // end start()

	// <qualified_identifier> = <identifier> {"." <identifier>};
	private void qualifiedIdentifier() throws InvalidInputException {
		output("Enter <qualified_identifier>", 1);

		parseLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.DOT) {
			parseLexeme(Token.DOT);
			parseLexeme(Token.IDENTIFIER);
		} // end while

		output("Exit <qualified_identifier>", -1);
	} // end qualifiedIdentifier()

	// <program> = ["package" <qualified_identifier>] ";" {<import>} <class>;
	private void program() throws InvalidInputException {
		output("Enter <program>", 1);

		if (nextLexeme.getToken() == Token.KEYWORD_PACKAGE) {
			parseLexeme(Token.KEYWORD_PACKAGE);
			qualifiedIdentifier(); // <qualified_identifier>
			parseLexeme(Token.SEMICOLON);
		} // end if

		while (nextLexeme.getToken() == Token.KEYWORD_IMPORT) {
			importRule(); // <import>
		}

		classRule(); // <class>

		output("Exit <program>", -1);
	} // end program()

	// <import> = "import" ["static"] <identifier> {"." <identifier>} [".*"] ";" ;
	private void importRule() throws InvalidInputException {
		output("Enter <import>", 1);

		parseLexeme(Token.KEYWORD_IMPORT);
		// must check lexeme itself as "static" is a modifier
		if (nextLexeme.getLexeme().equals("static")) {
			parseLexeme(Token.MODIFIER);
		}

		parseLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.DOT) {
			parseLexeme(Token.DOT);
			if (nextLexeme.getLexeme().equals("*")) {
				// asterisk is an infix operator
				parseLexeme(Token.INFIX_OPERATOR);
				break;
			} else {
				parseLexeme(Token.IDENTIFIER);
			} // end if/else
		} // end while

		parseLexeme(Token.SEMICOLON);

		output("Exit <import>", -1);
	} // end importRule()

	// <class> = {<modifier>} <class declaration>;
	private void classRule() throws InvalidInputException {
		output("Enter <class>", 1);

		while (nextLexeme.getToken() == Token.MODIFIER) {
			parseLexeme(Token.MODIFIER);
		} // end while

		classDeclaration(); // <class_declaration>

		output("Exit <class>", -1);
	} // end classRule()

	// <class_declaration> = "class" <identifier> [<type_arguments>][<extends>]
	// [<implements>] <class_body>;
	private void classDeclaration() throws InvalidInputException {
		output("Enter <class_declaration>", 1);

		parseLexeme(Token.KEYWORD_CLASS);
		parseLexeme(Token.IDENTIFIER);

		if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET)
			typeArguments(); // <type_arguments>
		if (nextLexeme.getToken() == Token.KEYWORD_EXTENDS)
			extendsRule(); // <extends>
		if (nextLexeme.getToken() == Token.KEYWORD_IMPLEMENTS)
			implementsRule(); // <implements>

		classBody(); // <class_body>

		output("Exit <class_declaration>", -1);
	} // end classDeclaration()

	// <extends> = "extends" <identifier>;
	private void extendsRule() throws InvalidInputException {
		output("Enter <extends>", 1);

		parseLexeme(Token.KEYWORD_EXTENDS);
		parseLexeme(Token.IDENTIFIER);

		output("Exit <extends>", -1);
	} // end extendsRule()

	// <implements> = "implements" <identifier> {',' <identifier>};
	private void implementsRule() throws InvalidInputException {
		output("Enter <implements>", 1);

		parseLexeme(Token.KEYWORD_IMPLEMENTS);
		parseLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.COMMA) {
			parseLexeme(Token.COMMA);
			parseLexeme(Token.IDENTIFIER);
		} // end while

		output("Exit <implements>", -1);
	} // end implementsRule()

	// <class_body> = '{' {<class body statement>} '}';
	private void classBody() throws InvalidInputException {
		output("Enter <class_body>", 1);

		parseLexeme(Token.LEFT_BRACE);

		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			classBodyStatement();
		}

		parseLexeme(Token.RIGHT_BRACE);

		output("Exit <class_body>", -1);
	} // end <class_body>

	// <class_body_statement> = ';'
	//   | ["static"] <block>;
	//   | {<modifier>} <class_body_declaration>
	private void classBodyStatement() throws InvalidInputException {
		output("Enter <class_body_statement>", 1);

		switch (nextLexeme.getToken()) {

		// semicolon: this is an empty statement
		case SEMICOLON:
			parseLexeme(Token.SEMICOLON);
			break;

		// left brace: this is the opening of a block
		case LEFT_BRACE:
			block(); // <block>
			break;

		// otherwise, assume this is a declaration
		default:
			while (nextLexeme.getToken() == Token.MODIFIER) {
				parseLexeme(Token.MODIFIER);
			} // end while

			if (nextLexeme.getToken() == Token.LEFT_BRACE) {
				block(); // <block>
			} else {
				classBodyDeclaration(); // <class_body_declaration>
			} // end if/else

		} // end switch

		output("Exit <class_body_statement>", -1);
	} // end classBodyStatement()

	// <class_body_declaration> = <class_declaration>
	//    | "void" <identifier> <method_declaration>
	//    | <identifier> <method_declaration>
	//    | <type> <identifier> <method_declaration>
	//    | <type> <identifier> <field_declaration> ";";
	private void classBodyDeclaration() throws InvalidInputException {
		output("Enter <class_body_declaration>", 1);

		switch (nextLexeme.getToken()) {

		// "class": this is a class declaration
		case KEYWORD_CLASS:
			classDeclaration(); // <class_declaration>
			break;

		// "void": this is a void method declaration
		case KEYWORD_VOID:
			parseLexeme(Token.KEYWORD_VOID);
			parseLexeme(Token.IDENTIFIER);
			methodDeclaration(); // <method_declaration>
			break;

		// identifiers and primitive types are handled together
		// as they could both by types
		case IDENTIFIER:
		case PRIMITIVE_TYPE:
			parseLexeme(nextLexeme.getToken());

			if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET) {
				typeArguments();
			}

			if (nextLexeme.getToken() == Token.LEFT_PAREN) {
				// if immediately followed by a left paren,
				// this must be a constructor delcaration
				methodDeclaration(); // <method_declaration>
			}  else { // otherwise, we know that this is a type
				// {"[]"}
				while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
					parseLexeme(Token.LEFT_BRACKET);
					parseLexeme(Token.RIGHT_BRACKET);
				} // end while

				parseLexeme(Token.IDENTIFIER);

				if (nextLexeme.getToken() == Token.LEFT_PAREN) {
					// if identifier followed by left paren,
					// this is a method declaration
					methodDeclaration(); // <method_declaration>
				} else {
					// if it isn't a left paren, this is a field declaration
					fieldDeclaration(); // <field_declaration>
					parseLexeme(Token.SEMICOLON);
				}
			}

			break;

		default:
			error();

		} // end switch

		output("Exit <class_body_declaration>", -1);
	} // end classBodyDeclaration()

	// <field_declaration> = {"[]"} ["=" <variable_init>] <variable_declarators_half>;
	private void fieldDeclaration() throws InvalidInputException {
		output("Enter <field_declaration>", 1);

		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			parseLexeme(Token.LEFT_BRACKET);
			parseLexeme(Token.RIGHT_BRACKET);
		}

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR) {
			parseLexeme(Token.ASSIGNMENT_OPERATOR);
			variableInit(); // <variable_init>
		}

		variableDeclaratorsHalf(); // <variable_declarators_half>

		output("Exit <field_declaration>", -1);
	} // end fieldDeclaration()

	// <variable_declarators> = <variable_declarator> <variable_declarators_half>;
	private void variableDeclarators() throws InvalidInputException {
		output("Enter <variable_declarators>", 1);

		variableDeclarator(); // <variable_declarator>

		variableDeclaratorsHalf(); // <variable_declarators_half>

		output("Exit <variable_declarators>", -1);
	} // end variableDeclarators()

	// <variable_declarator> = <identifier> {'[]'} ["=" <variable_init>];
	private void variableDeclarator() throws InvalidInputException {
		output("Enter <variable_declarator>", 1);

		parseLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			parseLexeme(Token.LEFT_BRACKET);
			parseLexeme(Token.RIGHT_BRACKET);
		}

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR) {
			parseLexeme(Token.ASSIGNMENT_OPERATOR);
			variableInit(); // <variable_init>
		}

		output("Exit <variable_declarators>", -1);
	} // end variableDeclarator()

	// <variable_declarators_half> = {"," <variable_declarator>};
	private void variableDeclaratorsHalf() throws InvalidInputException {
		output("Enter <variable_declarators_half>", 1);

		while (nextLexeme.getToken() == Token.COMMA) {
			parseLexeme(Token.COMMA);
			variableDeclarator(); // <variable_declarator>
		}

		output("Exit <variable_declarators_half>", -1);
	} // end variableDeclaratorsHalf()

	//<variable_declarators_afterID> = {'[]'} ["=" <variable_init>] <variable_declarators_half>;
	private void variableDeclaratorsAfterID() throws InvalidInputException {
		output("Enter <variable_declarators_afterID", 1);

		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			parseLexeme(Token.LEFT_BRACKET);
			parseLexeme(Token.RIGHT_BRACKET);
		} // end while

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR) {
			parseLexeme(Token.ASSIGNMENT_OPERATOR);
			variableInit(); // <variable_init>
		}

		variableDeclaratorsHalf(); // <variable_declarators_half>

		output("Exit <variable_declarators_afterID", -1);
	} // end variableDeclaratorsAfterID();

	// <method_declaration> = <parameters>
	// ["throws" <qualified_identifier> {"," <qualified_identifier>}]
	private void methodDeclaration() throws InvalidInputException {
		output("Enter <method_declaration>", 1);

		parameters(); // <parameters>

		if (nextLexeme.getToken() == Token.KEYWORD_THROWS) {
			parseLexeme(Token.KEYWORD_THROWS);
			qualifiedIdentifier(); // <qualified_identifier>

			while (nextLexeme.getToken() == Token.COMMA) {
				parseLexeme(Token.COMMA);
				qualifiedIdentifier(); // <qualified_identifier>
			} // end while

		} // end if

		if (nextLexeme.getToken() == Token.SEMICOLON) {
			parseLexeme(Token.SEMICOLON); // empty statement
		} else  {
			block(); // <block>
		}

		output("Exit <method_declaration>", -1);
	} // end methodDeclaration()

	// <parameters> = "(" [<parameter> {, <parameter>}] ")";
	private void parameters() throws InvalidInputException {
		output("Enter <parameters>", 1);

		parseLexeme(Token.LEFT_PAREN);

		if (nextLexeme.getToken() != Token.RIGHT_PAREN) {

			parameter(); // <parameter>
			while (nextLexeme.getToken() == Token.COMMA) {
				parseLexeme(Token.COMMA);
				parameter(); // <parameter>
			} // end while

		} // end if

		parseLexeme(Token.RIGHT_PAREN);

		output("Exit <parameters>", -1);
	} // end parameters()

	// <parameter> = {<modifier>} <type> <identifier>{"[]"};
	private void parameter() throws InvalidInputException {
		output("Enter <parameter>", 1);

		while (nextLexeme.getToken() == Token.MODIFIER) {
			parseLexeme(Token.MODIFIER);
		}

		type(); // <type>

		parseLexeme(Token.IDENTIFIER);

		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			parseLexeme(Token.LEFT_BRACKET);
			parseLexeme(Token.RIGHT_BRACKET);
		}

		output("Exit <parameter>", -1);
	} // end parameter()

	// <block> = '{' {<block_statement> }"}";
	private void block() throws InvalidInputException {
		output("Enter <block>", 1);

		parseLexeme(Token.LEFT_BRACE);

		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			blockStatement(); // <block_statement>
		}

		parseLexeme(Token.RIGHT_BRACE);

		output("Exit <block>", -1);
	} // end block()

	// <block_statement> = {<modifier>} (
	// <class_declaration>
	//   | <local_variable_declaration>
	//   | <identifier> ":" <statement>
	//   | <identifier> [<type_arguments>] {"." <identifier> [<type_arguments>]} {"[]"} <variable_declarators_afterID>
	//   | <identifier> {"." <identifier>} <expression_from_block>
	private void blockStatement() throws InvalidInputException {
		output("Enter <block_statement>", 1);

		// if the first lexeme is "synchronized," this is a synchronized block
		// which is handled in statement();
		if (nextLexeme.getLexeme().equals("synchronized")) {
			statement(); // <statement>

			// exit the method
			output("Exit <block_statement>", -1);

			return;
		} // end if

		// cycle through all modifiers
		while (nextLexeme.getToken() == Token.MODIFIER) {
			parseLexeme(Token.MODIFIER);
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

			parseLexeme(Token.IDENTIFIER);

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
				parseLexeme(Token.COLON);
				statement(); // <statement>
			} else {
				if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET) {
					typeArguments(); // <type_arguments>
					typeArguments = true;
				} // end if
				while (nextLexeme.getToken() == Token.DOT) {
					parseLexeme(Token.DOT);
					if (nextLexeme.getToken() != Token.IDENTIFIER) {
						//EXPRESSION: IDENTIFIER SUFFIX STARTING WITH DOT
						if (typeArguments)
							error(); // if any type arguments have occurred before this, error
						expressionFromBlock(); // <expression_from_block>
						break;
					} // end if
					parseLexeme(Token.IDENTIFIER);
					if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET) {
						typeArguments(); // <type_arguments>
						typeArguments = true;
					} // end if
				} // end while

				if (nextLexeme.getToken() == Token.LEFT_PAREN) {
					// EXPRESSION: IDENTIFIER SUFFIX STARTING WITH LEFT PAREN
					if (typeArguments) error();
					expressionFromBlock(); // <expression_from_block
					break;
				}
				while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
					parseLexeme(Token.LEFT_BRACKET);
					parseLexeme(Token.RIGHT_BRACKET);
				} // end while
				//variableDeclarators(); // <variable_declarators>
				variableDeclaratorsAfterID(); // <variable_declarators_afterID>
			}
			break;

		default:
			statement(); // <statement>
			break;

		} // end switch

		output("Exit <block_statement>", -1);
	} // end blockStatement()

	// <expression_from_block> = ( <arguments>
    //    | "." ("class" | "this" | "super" <arguments> | "new" [<type_arguments>] <inner_creator>)
	// )
	// [<postfix_operator>] [<assignment_operator> <expression1>] ";";
	private void expressionFromBlock() throws InvalidInputException {
		output("Enter <expression_from_block>", 1);

		if (nextLexeme.getToken() == Token.LEFT_PAREN) {
			arguments();
		} else if (nextLexeme.getToken() == Token.DOT) {
			switch (nextLexeme.getToken()) {
			
			case KEYWORD_CLASS:
			case KEYWORD_THIS:
				parseLexeme(nextLexeme.getToken());
				break;
			
			case KEYWORD_SUPER:
				parseLexeme(Token.KEYWORD_SUPER);
				arguments(); // <arguments>
				break;
			
			case KEYWORD_NEW:
				parseLexeme(Token.KEYWORD_NEW);
				if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET)
					typeArguments(); // <type_arguments>
				innerAllocator(); // <inner_allocator>
			
			default:
				error();
			} // end switch/case
		} // end if/else

		if (nextLexeme.getToken() == Token.OPERATOR_INCREMENT || nextLexeme.getToken() == Token.OPERATOR_DECREMENT) {
			postfixOperator(); // <postfix_operator>
		} // end if

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR
			|| nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET
			|| nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET
		) {
			assignmentOperator(); // <assignment_operator>
			expression1(); // <assignment_operator>
		}

		parseLexeme(Token.SEMICOLON);

		output("Exit <expression_from_block>", -1);
	} // end expressionFromBlock()

	// <local_variable_declaration> = <type> <variable_declarators>;
	private void localVariableDeclaration() throws InvalidInputException {
		output("Enter <local_variable_declaration>", 1);

		type(); // <type>
		variableDeclarators(); // <variable_declarators>

		output("Exit <local_variable_declaration>", -1);
	} // end <local_variable_declaration>

	// <type> = <primitive_type> {"[]"}
	//   | <identifier> <type_half>;
	private void type() throws InvalidInputException {
		output("Enter <type>", 1);

		if (nextLexeme.getToken() == Token.PRIMITIVE_TYPE) {
			parseLexeme(Token.PRIMITIVE_TYPE);
			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				parseLexeme(Token.LEFT_BRACKET);
				parseLexeme(Token.RIGHT_BRACKET);
			} // end while
		} else {
			parseLexeme(Token.IDENTIFIER);
			typeHalf(); // <type_half>
		} // end else

		output("Exit <type>", -1);
	} // end type()

	// <type_half> = [<type_arguments>] {"." <identifier> [type_arguments]}  {"[]"};
	private void typeHalf() throws InvalidInputException {
		output("Enter <type_half>", 1);

		if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET)
			typeArguments(); // <type_arguments>

		while (nextLexeme.getToken() == Token.DOT) {
			parseLexeme(Token.DOT);
			parseLexeme(Token.IDENTIFIER);
			if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET)
				typeArguments(); // <type_arguments>
		} // end while

		while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			parseLexeme(Token.LEFT_BRACKET);
			parseLexeme(Token.RIGHT_BRACKET);
		} // end while

		output("Exit <type_half>", -1);
	} // end typeHalf()

	// <type_arguments> = "<" <type_argument> {"," <type_argument>} ">";
	private void typeArguments() throws InvalidInputException {
		output("Enter <type_arguments>", 1);

		parseLexeme(Token.LEFT_ANGLEBRACKET);

		typeArgument(); // <type_argument>

		while (nextLexeme.getToken() == Token.COMMA) {
			parseLexeme(Token.COMMA);
			typeArgument(); // <type_argument>
		}

		parseLexeme(Token.RIGHT_ANGLEBRACKET);

		output("Exit <type_arguments>", -1);
	} // end typeArguments()

	// <type_argument> = <type> | "?" [ ("super" | "extends") <type>];
	private void typeArgument() throws InvalidInputException {
		output("Enter <type_argument>", 1);

		if (nextLexeme.getToken() == Token.QUESTION_MARK) {
			parseLexeme(Token.QUESTION_MARK);
			if (nextLexeme.getToken() == Token.KEYWORD_SUPER || nextLexeme.getToken() == Token.KEYWORD_EXTENDS) {
				parseLexeme(nextLexeme.getToken());
				type(); // <type>
			}
		} else {
			type(); // <type>
		}

		output("Exit <type_argument>", -1);
	} // end typeArgument()

	// <variable_init> = <expression> | <array_init>;
	private void variableInit() throws InvalidInputException {
		output("Enter <variable_init>", 1);

		if (nextLexeme.getToken() == Token.LEFT_BRACE) {
			arrayInit(); // <array_init>
		} else {
			expression(); // <expression>
		}

		output("Exit <variable_init>", -1);
	} // end variableInit()

	// <array_init> = "{" [<variable_init> {"," <variable_init>}] "}";
	private void arrayInit() throws InvalidInputException {
		output("Enter <array_init>", 1);

		parseLexeme(Token.LEFT_BRACE);

		if (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			variableInit(); // <variable_init>
			while(nextLexeme.getToken() != Token.RIGHT_BRACE) {
				parseLexeme(Token.COMMA);
				variableInit(); // <variable_init>
			} // end while
		} // end if

		parseLexeme(Token.RIGHT_BRACE);

		output("Exit <array_init>", -1);
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
	private void statement() throws InvalidInputException {
		output("Enter <statement>", 1);

		switch (nextLexeme.getToken()) {

		// "if" <paren_expression> ["else" <statement>]
		case KEYWORD_IF:
			parseLexeme(Token.KEYWORD_IF);
			parenExpression(); // <paren_expression>
			statement(); // <statement>
			if (nextLexeme.getToken() == Token.KEYWORD_ELSE) {
				parseLexeme(Token.KEYWORD_ELSE);
				statement(); // <statement>
			} // end if
			break;

		// "while" <paren_expression> <statement>
		case KEYWORD_WHILE:
			parseLexeme(Token.KEYWORD_WHILE);
			parenExpression(); // <paren_expression>
			statement(); // <statement>
			break;

		// "do" <statement> "while" <paren_expression> ";"
		case KEYWORD_DO:
			parseLexeme(Token.KEYWORD_DO);
			statement(); // <statement>
			parseLexeme(Token.KEYWORD_WHILE);
			parenExpression(); // <paren_expression>
			parseLexeme(Token.SEMICOLON);
			break;

		// this is a bit messy

		//"for" "(" [{<modifier>} <type> <identifier> {"[]"}]
		// ["=" <variable_init>] <for_arguments> ")" <statement>
		case KEYWORD_FOR:
			parseLexeme(Token.KEYWORD_FOR);
			parseLexeme(Token.LEFT_PAREN);
			if (nextLexeme.getToken() != Token.COLON && nextLexeme.getToken() != Token.SEMICOLON) {
				while (nextLexeme.getToken() == Token.MODIFIER)
					parseLexeme(Token.MODIFIER);
				type(); // <type>
				parseLexeme(Token.IDENTIFIER);
				while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
					parseLexeme(Token.LEFT_BRACKET);
					parseLexeme(Token.RIGHT_BRACKET);
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
			parseLexeme(Token.RIGHT_PAREN);
			statement(); // <statement>
			break;

		// "switch" <paren_expression> "{" <cases> "}"
		case KEYWORD_SWITCH:
			parseLexeme(Token.KEYWORD_SWITCH);
			parenExpression(); // <paren_expression>
			parseLexeme(Token.LEFT_BRACE);
			cases(); // <cases>
			parseLexeme(Token.RIGHT_BRACE);
			break;

		// "assert" <expression> [:<expression>] ";"
		case KEYWORD_ASSERT:
			parseLexeme(Token.KEYWORD_ASSERT);
			expression(); // <expression>
			if (nextLexeme.getToken() == Token.COLON) {
				parseLexeme(Token.COLON);
				expression(); // <expression>
			}
			parseLexeme(Token.SEMICOLON);
			break;

		// "return" [<expression>] ";"
		case KEYWORD_RETURN:
			parseLexeme(Token.KEYWORD_RETURN);
			if (nextLexeme.getToken() != Token.SEMICOLON) {
				expression(); // <expression>
			}
			parseLexeme(Token.SEMICOLON);
			break;

		// "break" [<identifier>] ";"
		case KEYWORD_BREAK:
			parseLexeme(Token.KEYWORD_BREAK);
			if (nextLexeme.getToken() == Token.IDENTIFIER)
				parseLexeme(Token.IDENTIFIER);
			parseLexeme(Token.SEMICOLON);
			break;

		// "continue" [<identifier>] ";"
		case KEYWORD_CONTINUE:
			parseLexeme(Token.KEYWORD_CONTINUE);
			if (nextLexeme.getToken() == Token.IDENTIFIER)
				parseLexeme(Token.IDENTIFIER);
			parseLexeme(Token.SEMICOLON);
			break;

		// "throw" <expression> ";"
		case KEYWORD_THROW:
			parseLexeme(Token.KEYWORD_THROW);
			expression(); // <expression>
			parseLexeme(Token.SEMICOLON);
			break;

		// "try" <block> [<catches>] ["finally" <block>]
		case KEYWORD_TRY:
			parseLexeme(Token.KEYWORD_TRY);
			block(); // <block>
			if (nextLexeme.getToken() == Token.KEYWORD_CATCH) {
				catches(); // <catches>
			}
			if (nextLexeme.getToken() == Token.KEYWORD_FINALLY) {
				parseLexeme(Token.KEYWORD_FINALLY);
				block(); // <block>
			}
			break;

		// "synchronized" <paren_expression> <block>
		case MODIFIER:
			if (!nextLexeme.getLexeme().equals("synchronized"))
				error();
			parseLexeme(Token.MODIFIER);
			parenExpression(); // <paren_expression>
			block(); // <block>
			break;

		// <block>
		case LEFT_BRACE:
			block();
			break;

		// ";"
		case SEMICOLON:
			parseLexeme(Token.SEMICOLON);
			break;

		// <identifier> ":" <statement>
		// | <identifier> <expression_half>
		case IDENTIFIER:
			parseLexeme(Token.IDENTIFIER);

			if (nextLexeme.getToken() != Token.COLON) {
				expressionHalf(); // <expression_half>
				parseLexeme(Token.SEMICOLON);
			} else {
				parseLexeme(Token.COLON);
				statement(); // <statement>
			}
			break;

		// <expression>
		default:
			expression(); // <expression>
			parseLexeme(Token.SEMICOLON);
			break;

		}//end switch

		output("Exit <statement>", -1);
	} // end statement()

	// <cases> = { ("case" (<identifier> | <expression>) | "default") ":" {<block_statement>} };
	private void cases() throws InvalidInputException {
		output("Enter <cases>", 1);

		while (nextLexeme.getToken() != Token.RIGHT_BRACE) {
			switch (nextLexeme.getToken()) {

			case KEYWORD_DEFAULT:
				parseLexeme(Token.KEYWORD_DEFAULT);
				parseLexeme(Token.COLON);
				break;

			case KEYWORD_CASE:
				parseLexeme(Token.KEYWORD_CASE);
				if (nextLexeme.getToken() == Token.IDENTIFIER) {
					parseLexeme(Token.IDENTIFIER);
				} else {
					expression(); // <expression>
				}
				parseLexeme(Token.COLON);
				break;

			default:
				error();

			} // end switch

			while (nextLexeme.getToken() != Token.RIGHT_BRACE && nextLexeme.getToken() != Token.KEYWORD_CASE && nextLexeme.getToken() != Token.KEYWORD_DEFAULT) {
				blockStatement(); // <block_statement>
			}
		} // end cases

		output("Exit <cases>", -1);
	} // end cases()

	// <catches> = <catch> {<catch>};
	private void catches() throws InvalidInputException {
		output("Enter <catches>", 1);

		while (nextLexeme.getToken() == Token.KEYWORD_CATCH) {
			catchRule(); // <catch>
		} // end while

		output("Exit <catches>", -1);
	} // end catches()

	// <catch> = "catch" "(" {<modifier>} <qualified_identifier> <identifier> ")" <block>;
	private void catchRule() throws InvalidInputException {
		output("Enter <catch>", 1);

		parseLexeme(Token.KEYWORD_CATCH);
		parseLexeme(Token.LEFT_PAREN);

		while (nextLexeme.getToken() == Token.MODIFIER) {
			parseLexeme(Token.MODIFIER);
		} // end while

		qualifiedIdentifier(); // <qualified_identifier>
		parseLexeme(Token.IDENTIFIER);
		parseLexeme(Token.RIGHT_PAREN);
		block(); // <block>

		output("Exit <catch>", -1);
	} // end catchRule()

	// <for_arguments> = ";" [<expression>] ";" <expression> {"," <expression>}
	//    | ":" <expression>;
	private void forArguments() throws InvalidInputException {
		output("Enter <for_arguments>", 1);

		if (nextLexeme.getToken() == Token.SEMICOLON) {
			parseLexeme(Token.SEMICOLON);
			if (nextLexeme.getToken() != Token.SEMICOLON) expression();
			parseLexeme(Token.SEMICOLON);
			if (nextLexeme.getToken() != Token.RIGHT_PAREN) {
				expression(); // <expression>
				while (nextLexeme.getToken() == Token.COMMA) {
					parseLexeme(Token.COMMA);
					expression(); // <expression>
				} // end while
			} // end if
		} else {
			parseLexeme(Token.COLON);
			expression(); // <expression>
		} // end else

		output("Exit <for_arguments>", -1);
	} // end forArguments()

	//<expression> = <expression1> [<assignment_operator> <expression1>];
	private void expression() throws InvalidInputException {
		output("Enter <expression>", 1);

		expression1(); // <expression1>

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR
				|| nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET
				|| nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET
			) {
			assignmentOperator(); // <assignment_operator>
			expression1(); // <expression1>
		}

		output("Exit <expression>", -1);
	} // end expression()

	// <expression1> = <expression2> ["?" <expression> ":" <expression1>];
	private void expression1() throws InvalidInputException {
		output("Enter <expression1>", 1);

		expression2(); // <expression2>

		if (nextLexeme.getToken() == Token.QUESTION_MARK) {
			parseLexeme(Token.QUESTION_MARK);
			expression(); // <expression>
			parseLexeme(Token.COLON);
			expression1(); // <expression>
		} // end if

		output("Exit <expression1>", -1);
	} // end expression1()

	// <expression2> = <expression3> [("instanceOf" <type> | {<infix_operator> <expression3>})];
	private void expression2() throws InvalidInputException {
		output("Enter <expression2>", 1);

		expression3(); // <expression3>

		if (nextLexeme.getToken() == Token.KEYWORD_INSTANCEOF) {
			parseLexeme(Token.KEYWORD_INSTANCEOF);
			type(); // <type>
		} else {
			while (nextLexeme.getToken() == Token.INFIX_OPERATOR
					|| nextLexeme.getToken() == Token.OPERATOR_PLUS
					|| nextLexeme.getToken() == Token.OPERATOR_MINUS
					|| nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET
					|| nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET ) {
				infixOperator(); // <infix_operator>
				expression3(); // <expression3>
			} // end while
		} // end if/else

		output("Exit <expression2>", -1);
	} // expression2()

	// <expression3> = <prefix_operator> <expression3>
    //   | "(" <type> ")" <expression3> (*implemented ebnf *)
    //   | <expression_unit> {<selector>} {<postfix_operator>};
	private void expression3() throws InvalidInputException {
		output("Enter <expression3>", 1);

		switch (nextLexeme.getToken()) {

		case PREFIX_OPERATOR:
		case OPERATOR_PLUS:
		case OPERATOR_MINUS:
		case OPERATOR_INCREMENT:
		case OPERATOR_DECREMENT:
			prefixOperator(); // <prefix_operator>
			expression3(); // <expression3>
			break;

		case LEFT_PAREN:
			parseLexeme(Token.LEFT_PAREN);
			expression();
			parseLexeme(Token.RIGHT_PAREN);
			break;

		default:
			expressionUnit(); // <expression_unit>
			while (nextLexeme.getToken() == Token.DOT || nextLexeme.getToken() == Token.LEFT_BRACKET) {
				selector(); // <selector>
			} // end while
			while (nextLexeme.getToken() == Token.OPERATOR_INCREMENT || nextLexeme.getToken() == Token.OPERATOR_DECREMENT) {
				postfixOperator(); // <postfix_operator>
			} // end while
			break;

		} // end switch/case

		output("Exit <expression3>", -1);
	} // end expression3()

	// <expression_half> = [<identifier_rest>] [<postfix_operator>] [<assignment_operator> <expression1>];
	private void expressionHalf() throws InvalidInputException {
		output("Enter <expression_half>", 1);

		/*
		if (nextLexeme.getToken() == Token.DOT) {
			parseLexeme(Token.DOT);
		} // end if
		*/

		identifierRest(); // <identifier_rest>

		if (nextLexeme.getToken() == Token.OPERATOR_INCREMENT || nextLexeme.getToken() == Token.OPERATOR_DECREMENT) {
			postfixOperator(); // <postfix_operator>
		} // end if

		if (nextLexeme.getToken() == Token.ASSIGNMENT_OPERATOR
				|| nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET
				|| nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET
			) {
			assignmentOperator(); // <assignment_operator>
			expression1(); // <expression1>
		} // end if

		output("Exit <expression_half>", -1);
	} // end expressionHalf()

	// <expression_unit> = <literal>
	//    | <paren_expression>
	//    | "this" [<arguments>]
	//    | "super" (<arguments> | "." <identifier> [<arguments>])
	//    | "new" <allocator>
	//    | <identifier> [<identifier_rest>]
	//    | <primitive_type> {"[]"} "." "class"
	//    | "void" "." "class";
	private void expressionUnit() throws InvalidInputException {
		output("Enter <expression_unit>", 1);

		switch (nextLexeme.getToken()) {

		// <paren_expression>
		case LEFT_PAREN:
			parenExpression(); // <paren_expression>
			break;

		// "this" [<arguments>]
		case KEYWORD_THIS:
			parseLexeme(Token.KEYWORD_THIS);
			if (nextLexeme.getToken() == Token.LEFT_PAREN)
				arguments(); // <arguments>
			break;

		// "super" (<arguments> | "." <identifier> [<arguments>])
		case KEYWORD_SUPER:
			parseLexeme(Token.KEYWORD_SUPER);
			if (nextLexeme.getToken() == Token.LEFT_PAREN)
				arguments(); // <arguments>
			else {
				parseLexeme(Token.DOT);
				parseLexeme(Token.IDENTIFIER);
				if (nextLexeme.getToken() == Token.LEFT_PAREN)
					arguments(); // arguments>
			} // end if/else
			break;

		// "new" <allocator>
		case KEYWORD_NEW:
			parseLexeme(Token.KEYWORD_NEW);
			allocator(); // <allocator>
			break;

		// <identifier> [<identifier_rest>]
		case IDENTIFIER:
			parseLexeme(Token.IDENTIFIER);
			identifierRest(); // <identifier_rest>
			break;

		// <primitive_type> {"[]"} "." "class"
		case PRIMITIVE_TYPE:
			parseLexeme(Token.PRIMITIVE_TYPE);
			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				parseLexeme(Token.LEFT_BRACKET);
				parseLexeme(Token.RIGHT_BRACKET);
			}
			parseLexeme(Token.DOT);
			parseLexeme(Token.KEYWORD_CLASS);
			break;

		// "void" "." "class";
		case KEYWORD_VOID:
			parseLexeme(Token.KEYWORD_VOID);
			parseLexeme(Token.DOT);
			parseLexeme(Token.KEYWORD_CLASS);
			break;

		// <literal>
		default:
			literal(); // <literal>
			break;
		} // end switch/case

		output("Exit <expression_unit>", -1);
	} // end expressionUnit()


	// <selector> = "." (
	//   <identifier> [<arguments>]
	//       | "this"
	//       | "super" (<arguments> | "." <identifier> [<arguments>])
	//       | "new" [<type_arguments>] <inner_allocator>
	//   )
	//   | "[" <expression> "]";
	private void selector() throws InvalidInputException {
		output("Enter <selector>", 1);

		if (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			parseLexeme(Token.LEFT_BRACKET);
			expression(); // <expression>
			parseLexeme(Token.RIGHT_BRACKET);
		} else {

			parseLexeme(Token.DOT);

			switch (nextLexeme.getToken()) {

			// <identifier> [<arguments>]
			case IDENTIFIER:
				parseLexeme(Token.IDENTIFIER);
				if (nextLexeme.getToken() == Token.LEFT_PAREN)
					arguments(); // <arguments>
				break;

			// "this"
			case KEYWORD_THIS:
				parseLexeme(Token.KEYWORD_THIS);
				break;

			// "super" (<arguments> | "." <identifier> [<arguments>])
			case KEYWORD_SUPER:
				parseLexeme(Token.KEYWORD_SUPER);
				if (nextLexeme.getToken() == Token.LEFT_PAREN) {
					arguments(); // <arguments>
				} else {
					parseLexeme(Token.DOT);
					parseLexeme(Token.IDENTIFIER);
					if (nextLexeme.getToken() == Token.LEFT_PAREN) {
						arguments(); // <arguments>
					} // end if
				} // end if/else
				break;

			case KEYWORD_NEW:
				parseLexeme(Token.KEYWORD_NEW);
				if (nextLexeme.getToken() == Token.LEFT_BRACKET) {
					typeArguments(); // <type_arguments>
				}
				innerAllocator(); // <inner_allocator>
				break;

			default:
				error();
			} // end switch case

		} // end if/else

		output("Exit <selector>", -1);
	} // end selector()

	// <allocator> = <identifier> [<type_arguments>] {"." <identifier> [<type_arguments>]} (<class_allocator> | <array_allocator>);
	private void allocator() throws InvalidInputException {
		output("Enter <allocator>", 1);

		parseLexeme(Token.IDENTIFIER);

		if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET) {
			typeArguments(); // <type_arguments>
		}

		while (nextLexeme.getToken() == Token.DOT) {
			parseLexeme(Token.DOT);
			parseLexeme(Token.IDENTIFIER);

			if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET) {
				typeArguments(); // <type_arguments>
			}

		} // end while

		if (nextLexeme.getToken() == Token.LEFT_PAREN) {
			classAllocator(); // <class_allocator>
		} else {
			arrayAllocator(); // <array_allocator>
		}

		output("Exit <allocator>", -1);
	} // end allocator()

	// <class_allocator> = <arguments> [<class_body>];
	private void classAllocator() throws InvalidInputException{
		output("Enter <class_allocator>", 1);

		arguments(); // <arguments>

		if (nextLexeme.getToken() == Token.LEFT_BRACE) {
			classBody(); // <class_body>
		}

		output("Exit <class_allocator>", -1);
	} // end classAllocator()

	// <array_allocator> = "[]" {"[]"} <array_init>
	//   | "[" <expression> "]" {"[" <expression> "]"} {"[]"};
	private void arrayAllocator() throws InvalidInputException {
		output("Enter <array_allocator>", 1);

		parseLexeme(Token.LEFT_BRACKET);

		if (nextLexeme.getToken() == Token.RIGHT_BRACKET) {
			parseLexeme(Token.RIGHT_BRACKET);

			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				parseLexeme(Token.LEFT_BRACKET);
				parseLexeme(Token.RIGHT_BRACKET);
			} // end while

			arrayInit(); // <array_init>
		} else {
			expression();
			parseLexeme(Token.RIGHT_BRACKET);

			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				parseLexeme(Token.LEFT_BRACKET);

				if (nextLexeme.getToken() == Token.RIGHT_BRACKET) {
					parseLexeme(Token.RIGHT_BRACKET);
				} else {
					expression();
					parseLexeme(Token.RIGHT_BRACKET);
				} // end if/ese

			} // end while

			while (nextLexeme.getToken() == Token.LEFT_BRACKET) {
				parseLexeme(Token.LEFT_BRACKET);
				parseLexeme(Token.RIGHT_BRACKET);
			} // end while
		} // end if/else

		output("Exit <array_allocator>", -1);
	} // end arrayAllocator()

	// <inner_allocator> = <identifier> [<type_arguments>] <class_allocator>;
	private void innerAllocator() throws InvalidInputException {
		output("Enter <inner_allocator>", 1);

		parseLexeme(Token.IDENTIFIER);

		if (nextLexeme.getToken() == Token.LEFT_BRACKET) {
			typeArguments(); // <type_arguments>
		}

		classAllocator(); // <class_allocator>

		output("Exit <inner_allocator>", -1);
	} // end innerAllocator()

	// <identifier_rest> = {"." <identifier>} (
	//    <arguments>
	//    	| "." ("class" | "this" | "super" <arguments> | "new" [<type_arguments>] <inner_allocator>)
	private void identifierRest() throws InvalidInputException {
		output("Enter <identifier_rest>", 1);
		
		while (nextLexeme.getToken() == Token.DOT) {
			parseLexeme(Token.DOT);
			if (nextLexeme.getToken() != Token.IDENTIFIER) break;
			parseLexeme(Token.IDENTIFIER);
		}
		
		if (nextLexeme.getToken() != Token.DOT && nextLexeme.getToken() != Token.LEFT_PAREN) {
			output("Exit <identifier_rest>", -1);
			return;
		}

		if (nextLexeme.getToken() == Token.LEFT_PAREN) {
			arguments(); // end <arguments>
		} else {
			parseLexeme(Token.DOT);
			switch (nextLexeme.getToken()) {
				case KEYWORD_CLASS:
					parseLexeme(Token.KEYWORD_CLASS);
					break;

				case KEYWORD_THIS:
					parseLexeme(Token.KEYWORD_THIS);
					break;

				case KEYWORD_SUPER:
					parseLexeme(Token.KEYWORD_SUPER);
					arguments(); // <arguments>
					break;

				case KEYWORD_NEW:
					parseLexeme(Token.KEYWORD_NEW);
					if (nextLexeme.getToken() == Token.LEFT_ANGLEBRACKET) {
						typeArguments(); // <type_arguments>
					}
					innerAllocator(); // <inner_allocator>
					break;

				default:
					error();
			} // end switch/case
		} // end if/else

		output("Exit <identifier_rest>", -1);
	} // end identifierRest()

	// <paren_expression> = "(" <expression> ")";
	private void parenExpression() throws InvalidInputException {
		output("Enter <paren_expression>", 1);

		parseLexeme(Token.LEFT_PAREN);
		expression(); // <expression>
		parseLexeme(Token.RIGHT_PAREN);

		output("Exit <paren_expression>", -1);
	} // end parenExpression()

	// <arguments> = "(" [<expression> {"," <expression>}] ")";
	private void arguments() throws InvalidInputException {
		output("Enter <arguments>", 1);

		parseLexeme(Token.LEFT_PAREN);

		if (nextLexeme.getToken() != Token.RIGHT_PAREN) {
			expression(); // <expression>
			while (nextLexeme.getToken() != Token.RIGHT_PAREN) {
				parseLexeme(Token.COMMA);
				expression(); // <expression>
			} // end while
		} // end if

		parseLexeme(Token.RIGHT_PAREN);

		output("Exit <arguments>", -1);
	} // end arguments()

	// <literal> = <int_literal>
	//	 | <int_lit> "." <int_lit> (*Float literal*)
	//	 | <char_literal>
	//	 | <string_literal>
	//	 | ("true" | "false");
	//	 | "null";
	private void literal() throws InvalidInputException {
		output("Enter <literal>", 1);

		switch (nextLexeme.getToken()) {

		case INT_LITERAL:
			parseLexeme(Token.INT_LITERAL);
			if (nextLexeme.getToken() == Token.DOT) {
				parseLexeme(Token.DOT);
				parseLexeme(Token.INT_LITERAL);
			}
			break;

		case CHAR_LITERAL:
			parseLexeme(Token.CHAR_LITERAL);
			break;

		case STRING_LITERAL:
			parseLexeme(Token.STRING_LITERAL);
			break;

		case KEYWORD_TRUE:
			parseLexeme(Token.KEYWORD_TRUE);
			break;

		case KEYWORD_FALSE:
			parseLexeme(Token.KEYWORD_FALSE);
			break;

		case KEYWORD_NULL:
			parseLexeme(Token.KEYWORD_NULL);
			break;

		default:
			error();
		} // end switch/case

		output("Exit <literal>", -1);
	} // end literal()


	// OPERATORS

	//<assignment_operator> = <ASSIGNMENT_OPERATOR>
	//   | ">>="
	//   | "<<="
	//   | ">>>=";
	private void assignmentOperator() throws InvalidInputException {
		output("Enter <assignment_operator>", 1);

		switch (nextLexeme.getToken()) {

		case ASSIGNMENT_OPERATOR:
			parseLexeme(Token.ASSIGNMENT_OPERATOR);
			break;

		case LEFT_ANGLEBRACKET:
			parseLexeme(Token.LEFT_ANGLEBRACKET);
			parseLexeme(Token.LEFT_ANGLEBRACKET);
			parseLexeme(Token.ASSIGNMENT_OPERATOR);
			break;

		case RIGHT_ANGLEBRACKET:
			parseLexeme(Token.RIGHT_ANGLEBRACKET);
			parseLexeme(Token.RIGHT_ANGLEBRACKET);
			if (nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET)
				parseLexeme(Token.RIGHT_ANGLEBRACKET);
			parseLexeme(Token.ASSIGNMENT_OPERATOR);
			break;

		default:
			error();
		} // end switch/case

		output("Exit <assignment_operator>", -1);
	} // end assignmentOperator()

	// <infix_operator> = <INFIX_OPERATOR>
	//	  | "+"
	//	  | "-"
	//    | ">"
	//    | ">="
	//    | ">>"
	//    | ">>>"
	//    | "<"
	//    | "<="
	//    | "<<";
	private void infixOperator() throws InvalidInputException {
		output("Enter <infix_operator>", 1);

		switch (nextLexeme.getToken()) {
		case INFIX_OPERATOR:
		case OPERATOR_PLUS:
		case OPERATOR_MINUS:
			parseLexeme(nextLexeme.getToken());
			break;

		case LEFT_ANGLEBRACKET:
			parseLexeme(Token.LEFT_ANGLEBRACKET);

			switch (nextLexeme.getToken()) {
			case ASSIGNMENT_OPERATOR:
				parseLexeme(Token.ASSIGNMENT_OPERATOR);
				break;
			case LEFT_ANGLEBRACKET:
				parseLexeme(Token.LEFT_ANGLEBRACKET);
				break;
			default:
				break;
			} // end switch

			break;

		case RIGHT_ANGLEBRACKET:
			parseLexeme(Token.RIGHT_ANGLEBRACKET);

			switch (nextLexeme.getToken()) {
			case ASSIGNMENT_OPERATOR:
				parseLexeme(Token.ASSIGNMENT_OPERATOR);
				break;
			case RIGHT_ANGLEBRACKET:
				parseLexeme(Token.RIGHT_ANGLEBRACKET);
				if (nextLexeme.getToken() == Token.RIGHT_ANGLEBRACKET)
					parseLexeme(Token.RIGHT_ANGLEBRACKET);
				break;
			default:
				break;
			} // end switch

			break;

		default:
			error();
		} // end switch/case

		output("Exit <infix_operator>", -1);
	} // end infixOperator()

	//<prefix_operator> = <PREFIX_OPERATOR>
	//	| "+"
	//	| "-"
	//	| "++"
	//	| "--";
	private void prefixOperator() throws InvalidInputException {
		output("Enter <prefix_operator>", 1);

		switch (nextLexeme.getToken()) {
		case PREFIX_OPERATOR:
		case OPERATOR_PLUS:
		case OPERATOR_MINUS:
		case OPERATOR_INCREMENT:
		case OPERATOR_DECREMENT:
			parseLexeme(nextLexeme.getToken());
			break;
		default:
			error();
		} // end switch/case

		output("Exit <prefix_operator>", -1);
	} // end prefixOperator()

	// <postfix_operator> = "++"
	//	| "--";
	private void postfixOperator() throws InvalidInputException {
		output("Enter <postfix_operator>", 1);

		switch (nextLexeme.getToken()) {
		case OPERATOR_INCREMENT:
		case OPERATOR_DECREMENT:
			parseLexeme(nextLexeme.getToken());
			break;
		default:
			error();
		} // end switch/case

		output("Exit <postfix_operator>", -1);
	} // end postfixOperator()

	/**
	* Checks if the current lexeme's associated token is equal to the given token,
	* prints out the current lexeme, and moves to the next lexeme in the input string
	*
	* @param token Expected token
	* @throws InvalidInputException
	*/
	private void parseLexeme(Token token) throws InvalidInputException {
		if (nextLexeme.getToken() == token) {
			output(nextLexeme.toString(), 0);
			nextLexeme = lex.nextLexeme();
		} else{
			error();
		} // end if/else
	} // end processLexeme()

	/**
	 * Modified print statement to maintain proper indentation, and redirect output
	 * to an output queue
	 *
	 * @param toPrint String to print
	 * @param direction Positive integer to decrease indentation, negative to decrease,
	 *  or 0 to leave it the same.
	 */
	private void output(String toPrint, int direction) {
		if (direction < 0) indentationLevel--;

		String output = "";

		for (int i = 0; i < indentationLevel; i++) {
			output = output + "    ";
		}
		output = output + toPrint + "\n";

		outputQueue.add(output);
		if (direction > 0) indentationLevel++;
	} // end printIndented()

	// throws an exception
	private void error() throws InvalidInputException {
		String message = String.format("ERROR: Line %d: Invalid input: %s\n", lex.getLineNumber(), nextLexeme.getLexeme());
		throw new InvalidInputException(message);
	} // end error()

} // end class
