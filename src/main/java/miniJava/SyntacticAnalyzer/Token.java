package miniJava.SyntacticAnalyzer;

public class Token {
	private final TokenType type;
	private final String value;
	private final SourcePosition position;

	public Token(TokenType type, SourcePosition position){
		this(type, null, position);
	}

	public Token(TokenType type, String value, SourcePosition position){
		this.type = type;
		this.value = value;
		this.position = position;
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
	
	public SourcePosition getPosition(){
		return position;
	}
}
