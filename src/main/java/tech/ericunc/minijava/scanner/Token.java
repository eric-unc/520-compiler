package tech.ericunc.minijava.scanner;

public class Token {
	private final TokenType type;
	private final String value;

	public Token(TokenType type){
		this.type = type;
		this.value = null;
	}

	public Token(TokenType type, String value){
		this.type = type;
		this.value = value;
	}

	/**
	 * @return type of token
	 */
	public TokenType getType(){
		return type;
	}

	/**
	 * @return value of token (may/often will be <code>null</code>)
	 */
	public String getValue(){
		return value;
	}
}
