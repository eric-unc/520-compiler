package tech.ericunc.minijava.scanner;

import java.io.IOException;
import java.io.InputStream;

import tech.ericunc.minijava.MiniJavaCompiler;

public class Scanner {
	private InputStream stream;
	private boolean end;

	private char curChar;
	private int line;

	public Scanner(InputStream stream){
		this.stream = stream;
		end = false;
		line = 1;

		readChar();
	}

	public Token scan(){
		while(!end && isWhitespace(curChar))
			readChar();
		
		if(end)
			return new Token(TokenType.END);

		if(isValidIdentifierStartChar(curChar)){
			StringBuilder word = new StringBuilder();

			while(!end && isValidIdenfifierChar(curChar)){
				word.append(curChar);
				readChar();
			}

			switch(word.toString()){
				// Keywords
				case "class":
					return new Token(TokenType.CLASS);
				case "this":
					return new Token(TokenType.THIS);
				case "return":
					return new Token(TokenType.RETURN);
				case "new":
					return new Token(TokenType.NEW);
				case "int":
					return new Token(TokenType.INT);
				case "boolean":
					return new Token(TokenType.BOOLEAN);
				case "if":
					return new Token(TokenType.IF);
				case "else":
					return new Token(TokenType.ELSE);
				case "while":
					return new Token(TokenType.WHILE);
				case "public":
					return new Token(TokenType.PUBLIC);
				case "private":
					return new Token(TokenType.PRIVATE);
				case "static":
					return new Token(TokenType.STATIC);
				case "void":
					return new Token(TokenType.VOID);

				// Word literals
				case "true":
					return new Token(TokenType.TRUE);
				case "false":
					return new Token(TokenType.FALSE);

				default:
					return new Token(TokenType.IDEN, word.toString());
			}
		}else if(isDigitChar(curChar)){
			StringBuilder num = new StringBuilder();

			while(!end && isValidIdenfifierChar(curChar)){
				num.append(curChar);
				readChar();
			}

			return new Token(TokenType.NUM, num.toString());
		}else{
			switch(curChar){
				case '=':
					readChar();

					if(curChar == '='){ // ==
						readChar();
						return new Token(TokenType.EQUALS_OP);
					}else // just =
						return new Token(TokenType.EQUALS);
				case ';':
					readChar();
					return new Token(TokenType.SEMI);
				case '.':
					readChar();
					return new Token(TokenType.DOT);
				case ',':
					readChar();
					return new Token(TokenType.COMMA);
				case '(':
					readChar();
					return new Token(TokenType.L_PAREN);
				case ')':
					readChar();
					return new Token(TokenType.R_PAREN);
				case '{':
					readChar();
					return new Token(TokenType.L_BRACKET);
				case '}':
					readChar();
					return new Token(TokenType.R_BRACKET);
				case '[':
					readChar();
					return new Token(TokenType.L_SQ_BRACK);
				case ']':
					readChar();
					return new Token(TokenType.R_SQ_BRACK);
				case '>':
					readChar();

					if(curChar == '='){ // >=
						readChar();
						return new Token(TokenType.MORE_EQUAL);
					}else // just >
						return new Token(TokenType.MORE_THAN);
				case '<':
					readChar();

					if(curChar == '='){ // <=
						readChar();
						return new Token(TokenType.LESS_EQUAL);
					}else // just >
						return new Token(TokenType.LESS_THAN);
				case '!':
					readChar();
					
					if(curChar == '='){ // !=
						readChar();
						return new Token(TokenType.NOT_EQUALS);
					}else // just !
						return new Token(TokenType.NEG);
				case '&':
					readChar();
					
					if(curChar == '&'){ // &&
						readChar();
						return new Token(TokenType.AND_LOG);
					}else // just &
						return new Token(TokenType.ERROR, "&"); // TODO: bitwise &
				case '|':
					readChar();
					
					if(curChar == '|'){ // ||
						readChar();
						return new Token(TokenType.OR_LOG);
					}else // just |
						return new Token(TokenType.ERROR, "|"); // TODO: bitwise |
				case '+':
					readChar();
					return new Token(TokenType.PLUS);
				case '-':
					readChar();
					return new Token(TokenType.MINUS);
				case '*':
					readChar();
					return new Token(TokenType.TIMES);
				case '/':
					readChar();
					
					if(curChar == '/'){ // // (single-line comment)
						readChar();
						
						while(!end && !isNewline(curChar))
							readChar();
						
						return scan(); // break, return whatever's next
					}else if(curChar == '*'){ // /* (multiline comment)
						readChar();
						
						while(!end){
							readChar();
							if(curChar == '*'){ // looking for */
								readChar();
								if(curChar == '/'){
									readChar();
									return scan(); // break, return whatever's next
								}
							}
						}
					}else
						return new Token(TokenType.DIV);
				default:
					return new Token(TokenType.ERROR, "" + curChar);
			}
		}
	}

	private void readChar(){
		try{
			int c = stream.read();
			
			//MiniJavaCompiler.debug((char)c);

			if(c == -1)
				end = true;
			
			if(c == '\n')
				line++;

			curChar = (char) c;
		}catch(IOException e){
			e.printStackTrace();
			System.exit(4);
		}
	}
	
	public int getLineNum(){
		return line;
	}

	private static boolean isWordChar(char c){
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private static boolean isDigitChar(char c){
		return (c >= '0' & c <= '9');
	}

	private static boolean isValidIdenfifierChar(char c){
		return isValidIdentifierStartChar(c) || isDigitChar(c);
	}

	private static boolean isValidIdentifierStartChar(char c){
		return isWordChar(c) | c == '$' | c == '_';
	}
	
	private static boolean isNewline(char c){
		return c == '\n' || c == '\r';
	}

	private static boolean isWhitespace(char c){
		return isNewline(c) || c == ' ' || c == '\t';
	}
}
