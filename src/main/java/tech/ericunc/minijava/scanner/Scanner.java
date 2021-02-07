package tech.ericunc.minijava.scanner;

import java.io.IOException;
import java.io.InputStream;

import static tech.ericunc.minijava.scanner.TokenType.*;

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
			return new Token(END);

		if(isValidIdentifierStartChar(curChar)){
			StringBuilder word = new StringBuilder();

			while(!end && isValidIdenfifierChar(curChar)){
				word.append(curChar);
				readChar();
			}

			switch(word.toString()){
				// Keywords
				case "class":
					return new Token(CLASS);
				case "this":
					return new Token(THIS);
				case "return":
					return new Token(RETURN);
				case "new":
					return new Token(NEW);
				case "int":
					return new Token(INT);
				case "boolean":
					return new Token(BOOLEAN);
				case "if":
					return new Token(IF);
				case "else":
					return new Token(ELSE);
				case "while":
					return new Token(WHILE);
				case "public":
					return new Token(PUBLIC);
				case "private":
					return new Token(PRIVATE);
				case "static":
					return new Token(STATIC);
				case "void":
					return new Token(VOID);

				// Word literals
				case "true":
					return new Token(TRUE);
				case "false":
					return new Token(FALSE);

				default:
					return new Token(IDEN, word.toString());
			}
		}else if(isDigitChar(curChar)){
			StringBuilder num = new StringBuilder();

			while(!end && isValidIdenfifierChar(curChar)){
				num.append(curChar);
				readChar();
			}

			return new Token(NUM, num.toString());
		}else{
			switch(curChar){
				case '=':
					readChar();

					if(curChar == '='){ // ==
						readChar();
						return new Token(EQUALS_OP);
					}else // just =
						return new Token(EQUALS);
				case ';':
					readChar();
					return new Token(SEMI);
				case '.':
					readChar();
					return new Token(DOT);
				case ',':
					readChar();
					return new Token(COMMA);
				case '(':
					readChar();
					return new Token(L_PAREN);
				case ')':
					readChar();
					return new Token(R_PAREN);
				case '{':
					readChar();
					return new Token(L_BRACKET);
				case '}':
					readChar();
					return new Token(R_BRACKET);
				case '[':
					readChar();
					return new Token(L_SQ_BRACK);
				case ']':
					readChar();
					return new Token(R_SQ_BRACK);
				case '>':
					readChar();

					if(curChar == '='){ // >=
						readChar();
						return new Token(MORE_EQUAL);
					}else // just >
						return new Token(MORE_THAN);
				case '<':
					readChar();

					if(curChar == '='){ // <=
						readChar();
						return new Token(LESS_EQUAL);
					}else // just >
						return new Token(LESS_THAN);
				case '!':
					readChar();
					
					if(curChar == '='){ // !=
						readChar();
						return new Token(NOT_EQUALS);
					}else // just !
						return new Token(NEG);
				case '&':
					readChar();
					
					if(curChar == '&'){ // &&
						readChar();
						return new Token(AND_LOG);
					}else // just &
						return new Token(ERROR, "&"); // TODO: bitwise &
				case '|':
					readChar();
					
					if(curChar == '|'){ // ||
						readChar();
						return new Token(OR_LOG);
					}else // just |
						return new Token(ERROR, "|"); // TODO: bitwise |
				case '+':
					readChar();
					return new Token(PLUS);
				case '-':
					readChar();
					return new Token(MINUS);
				case '*':
					readChar();
					return new Token(TIMES);
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
						return new Token(DIV);
				default:
					return new Token(ERROR, "" + curChar);
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
