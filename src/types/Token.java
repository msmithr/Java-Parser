/**
 * Enum for valid token types, used for lexical analyzer and recursive desecent parser for the Java
 * programming language class definition
 */

package types;

public enum Token {
	IDENTIFIER,
	INT_LITERAL,

	// keywords
	KEYWORD_CLASS,
	KEYWORD_EXTENDS,
	KEYWORD_IMPLEMENTS,
	KEYWORD_ACCESSMODIFIER,
	KEYWORD_CLASSMODIFIER,

	// brackets
	LEFT_PAREN,
	RIGHT_PAREN,
	LEFT_BRACE,
	RIGHT_BRACE,
	LEFT_BRACKET,
	RIGHT_BRACKET,

<<<<<<< HEAD
	COMMA,
	SEMICOLON,

=======
	SEMICOLON,


>>>>>>> e7d5ccaad46faae60a2c98d6085e4946eeec5ac2

}
