/**
 * Enum for valid token types, used for lexical analyzer and recursive desecent parser for the Java
 * programming language class definition
 */

package types;

public enum Token {
	IDENTIFIER,


	// keywords
	KEYWORD_CLASS,
	KEYWORD_EXTENDS,
	KEYWORD_IMPLEMENTS,
	//KEYWORD_INTERFACE, NOTE: need to change EBNF and parser to account for this.  It can be a modifier to a class like abstract, static, and final
	KEYWORD_IF,
	KEYWORD_ELSE,// need now
	KEYWORD_WHILE,
	KEYWORD_DO,
	KEYWORD_FOR,
	KEYWORD_SWITCH,
	KEYWORD_CASE,
	KEYWORD_DEFAULT,
	KEYWORD_RETURN,
	KEYWORD_BREAK,
	KEYWORD_CONTINUE,
	KEYWORD_THROW,
	KEYWORD_THROWS,
	KEYWORD_TRY,
	//KEYWORD_CATCH,//don't need yet
	//KEYWORD_FINALLY,//don't need yet
	PRIMITIVE_TYPE,
	MODIFIER,

	//We don't account for these yet in the parser, but they are all Java keywords
	KEYWORD_VOID,
	KEYWORD_ASSERT,
	//KEYWORD_CONST, //NOTE:  does nothing in Java, but is a keyword
	//KEYWORD_ENUM,
	//KEYWORD_IMPORT, //NOTE:  we probably don't need to account for this one, but I went ahead and put it in here, since I put every other Java keyword in here
	//KEYWORD_INSTANCEOF,
	//KEYWORD_NEW,
	//KEYWORD_PACKAGE,  //NOTE:  we probably don't need to account for this one, but I went ahead and put it in here, since I put every other Java keyword in here
	//KEYWORD_SUPER,
	//KEYWORD_THIS,
	//KEYWORD_NULL,
	//KEYWORD_TRUE,
	//KEYWORD_FALSE,


	// brackets
	LEFT_PAREN,
	RIGHT_PAREN,
	LEFT_BRACE,
	RIGHT_BRACE,
	LEFT_BRACKET,
	RIGHT_BRACKET,
	//LEFT_ANGLE_BRACKET,// <
	//RIGHT_ANGLE_BRACKET,// >
	//DOUBLE_QUOTE,// "
	//SINGLE_QUOTE,// '


	SEMICOLON,
	COMMA,
	COLON,

	// operators and other symbols
	//PLUS,// +
	//MINUS,// -
	//ASTERISK,// *
	//BACKSLASH,// /
	//FORWARDSLASH,// \
	//MODULO,// %
	//AT,// @
	//EXCLAIMATION_POINT,// ! (aka NOT, or BANG)
	//AMPERSAND,// &
	//PIPE,// |
	EQUALS,// =
	//COLON,// :
	//QUESTION_MARK,// ?



}
