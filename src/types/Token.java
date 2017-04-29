/**
 * Enum for valid token types, used for lexical analyzer and recursive desecent parser for the Java
 * programming language class definition
 *
 * @author Michael Smith and Nathan Jean
 */

package types;

public enum Token {

	/* literals and generic tokens */
	IDENTIFIER,			// simple string of characters
	INT_LITERAL,		// numbers
	STRING_LITERAL,		// string of characters surrounded by double quotes
	CHAR_LITERAL,		// character surrounded by single quotes
	RESERVED_WORD,		// goto, const
	PRIMITIVE_TYPE,		// boolean, byte, char, short, int, long, float, double
	MODIFIER,			/* public, private, protected, static, abstract, final, native,
						synchronized, transient, volatile, scrictfp, @IDENTIFIER */

	/* keywords */
	KEYWORD_ASSERT,		// assert
	KEYWORD_BREAK,		// break
	KEYWORD_CASE,		// case
	KEYWORD_CATCH,		// catch
	KEYWORD_CLASS,		// class
	KEYWORD_CONTINUE,	// continue
	KEYWORD_DEFAULT,	// default
	KEYWORD_DO,			// do
	KEYWORD_ELSE,		// else
	KEYWORD_ENUM,		// enum
	KEYWORD_EXTENDS,	// extends
	KEYWORD_FALSE,		// false
	KEYWORD_FINALLY,	// finally
	KEYWORD_FOR,		// for
	KEYWORD_IF,			// if
	KEYWORD_IMPLEMENTS,	// implements
	KEYWORD_IMPORT,		// import
	KEYWORD_INSTANCEOF,	// instanceof
	KEYWORD_INTERFACE,	// interface
	KEYWORD_NEW,		// new
	KEYWORD_NULL,		// null
	KEYWORD_PACKAGE,	// package
	KEYWORD_RETURN,		// return
	KEYWORD_SUPER,		// super
	KEYWORD_SWITCH,		// switch
	KEYWORD_THIS,		// this
	KEYWORD_THROW,		// throw
	KEYWORD_THROWS,		// throws
	KEYWORD_TRUE,		// true
	KEYWORD_TRY,		// try
	KEYWORD_VOID,		// void
	KEYWORD_WHILE,		// while

	/* brackets */
	LEFT_PAREN, 		// (
	RIGHT_PAREN,		// )
	LEFT_BRACE,			// {
	RIGHT_BRACE,		// }
	LEFT_BRACKET,		// [
	RIGHT_BRACKET,		// ]
	LEFT_ANGLEBRACKET,	// <
	RIGHT_ANGLEBRACKET,	// >

	/* operators */
	ASSIGNMENT_OPERATOR,// =, +=, -=, *=, /=, &=, |=, ^=, %=
	INFIX_OPERATOR,		// ||, &&, |, ^, &, ==, !=, *, /, %
	PREFIX_OPERATOR,	// !, ~
	OPERATOR_PLUS,		// +
	OPERATOR_MINUS,		// -
	OPERATOR_INCREMENT, // ++
	OPERATOR_DECREMENT, // --
	QUESTION_MARK,		// ?

	/* other symbols */
	SEMICOLON,			// ;
	COMMA,				// ,
	COLON,				// :
	DOT,				// .
	BACKSLASH,			// /

} // end enum
