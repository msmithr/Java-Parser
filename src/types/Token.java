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
	KEYWORD_PUBLIC,
	KEYWORD_PRIVATE,
	KEYWORD_PROTECTED,
	KEYWORD_ABSTRACT,
	KEYWORD_STATIC,
	KEYWORD_FINAL,
	KEYWORD_STRICTFP,

	// brackets
	LEFT_PAREN,
	RIGHT_PAREN,
	LEFT_BRACE,
	RIGHT_BRACE,
	LEFT_BRACKET,
	RIGHT_BRACKET,
	
	SEMICOLON,
	
	

}
