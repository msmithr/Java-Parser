/**
 * Enum for valid token types, used for lexical analyzer and recursive desecent parser for the Java
 * programming language class definition
 */

package types;

public enum Token {
	IDENTIFIER,
	INT_LITERAL,
	COMMENT,

	// keywords
	KEYWORD_CLASS,
	KEYWORD_EXTENDS,
	KEYWORD_IMPLEMENTS,
	KEYWORD_ACCESSMODIFIER, // public, private, or protected
	KEYWORD_ABSTRACT,
	KEYWORD_STATIC,
	KEYWORD_FINAL,
	KEYWORD_STRICTFP,
	KEYWORD_TRANSIENT,
	KEYWORD_VOLATILE,
	KEYWORD_SYNCHRONIZED,
	KEYWORD_NATIVE,
	
	// brackets
	LEFT_PAREN,
	RIGHT_PAREN,
	LEFT_BRACE,
	RIGHT_BRACE,
	LEFT_BRACKET,
	RIGHT_BRACKET,

	COMMA,
	SEMICOLON,

}