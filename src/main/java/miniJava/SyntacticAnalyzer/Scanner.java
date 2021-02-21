package miniJava.SyntacticAnalyzer;

import static miniJava.SyntacticAnalyzer.TokenType.*;

import java.io.IOException;
import java.io.InputStream;

public class Scanner {
	private InputStream stream;
	private boolean end;

	private char curChar;
	private int lineNum;
	private int lineWidth;

	public Scanner(InputStream stream){
		this.stream = stream;
		end = false;
		lineNum = 1;

		readChar();
	}

	public Token scan(){
		while(!end && isWhitespace(curChar))
			readChar();
		
		if(end)
			return new Token(END, new SourcePosition(lineNum, lineWidth));

		if(isValidIdentifierStartChar(curChar)){
			int startLineNum = lineNum;
			int endLineNum = lineWidth;
			
			StringBuilder word = new StringBuilder();

			while(!end && isValidIdenfifierChar(curChar)){
				word.append(curChar);
				readChar();
			}
			
			SourcePosition position = new SourcePosition(startLineNum, lineNum, endLineNum, lineWidth);

			switch(word.toString()){
				// Keywords
				case "class":
					return new Token(CLASS, position);
				case "this":
					return new Token(THIS, position);
				case "return":
					return new Token(RETURN, position);
				case "new":
					return new Token(NEW, position);
				case "int":
					return new Token(INT, position);
				case "boolean":
					return new Token(BOOLEAN, position);
				case "if":
					return new Token(IF, position);
				case "else":
					return new Token(ELSE, position);
				case "while":
					return new Token(WHILE, position);
				case "public":
					return new Token(PUBLIC, position);
				case "private":
					return new Token(PRIVATE, position);
				case "static":
					return new Token(STATIC, position);
				case "void":
					return new Token(VOID, position);

				// Word literals
				case "true":
					return new Token(TRUE, position);
				case "false":
					return new Token(FALSE, position);

				default:
					return new Token(IDEN, word.toString(), position);
			}
		}else if(isDigitChar(curChar)){
			int startLineNum = lineNum;
			int endLineNum = lineWidth;
			
			StringBuilder num = new StringBuilder();

			while(!end && isValidIdenfifierChar(curChar)){
				num.append(curChar);
				readChar();
			}

			return new Token(NUM, num.toString(), new SourcePosition(startLineNum, lineNum, endLineNum, lineWidth));
		}else{
			SourcePosition oneCharPosition = new SourcePosition(lineNum - 1, lineNum - 1, lineWidth - 1, lineWidth - 1);
			SourcePosition twoCharPosition = new SourcePosition(lineNum - 1, lineNum - 1, lineWidth, lineWidth);
			
			switch(curChar){
				case '=':
					readChar();

					if(curChar == '='){ // ==
						readChar();
						return new Token(EQUALS_OP, twoCharPosition);
					}else // just =
						return new Token(EQUALS, oneCharPosition);
				case ';':
					readChar();
					return new Token(SEMI, oneCharPosition);
				case '.':
					readChar();
					return new Token(DOT, oneCharPosition);
				case ',':
					readChar();
					return new Token(COMMA, oneCharPosition);
				case '(':
					readChar();
					return new Token(L_PAREN, oneCharPosition);
				case ')':
					readChar();
					return new Token(R_PAREN, oneCharPosition);
				case '{':
					readChar();
					return new Token(L_BRACKET, oneCharPosition);
				case '}':
					readChar();
					return new Token(R_BRACKET, oneCharPosition);
				case '[':
					readChar();
					return new Token(L_SQ_BRACK, oneCharPosition);
				case ']':
					readChar();
					return new Token(R_SQ_BRACK, oneCharPosition);
				case '>':
					readChar();

					if(curChar == '='){ // >=
						readChar();
						return new Token(MORE_EQUAL, twoCharPosition);
					}else // just >
						return new Token(MORE_THAN, oneCharPosition);
				case '<':
					readChar();

					if(curChar == '='){ // <=
						readChar();
						return new Token(LESS_EQUAL, twoCharPosition);
					}else // just >
						return new Token(LESS_THAN, oneCharPosition);
				case '!':
					readChar();
					
					if(curChar == '='){ // !=
						readChar();
						return new Token(NOT_EQUALS, twoCharPosition);
					}else // just !
						return new Token(NEG, oneCharPosition);
				case '&':
					readChar();
					
					if(curChar == '&'){ // &&
						readChar();
						return new Token(AND_LOG, twoCharPosition);
					}else // just &
						return new Token(ERROR, "&", oneCharPosition); // TODO: bitwise &
				case '|':
					readChar();
					
					if(curChar == '|'){ // ||
						readChar();
						return new Token(OR_LOG, twoCharPosition);
					}else // just |
						return new Token(ERROR, "|", oneCharPosition); // TODO: bitwise |
				case '+':
					readChar();
					return new Token(PLUS, oneCharPosition);
				case '-':
					readChar();
					return new Token(MINUS, oneCharPosition);
				case '*':
					readChar();
					return new Token(TIMES, oneCharPosition);
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
							//readChar();
							if(curChar == '*'){ // looking for */
								readChar();
								if(curChar == '/'){
									readChar();
									return scan(); // break, return whatever's next
								}
							}else
								readChar();
						}
					}else
						return new Token(DIV, oneCharPosition);
				default:
					return new Token(ERROR, "" + curChar, oneCharPosition);
			}
		}
	}

	private void readChar(){
		try{
			int c = stream.read();

			if(c == -1)
				end = true;
			
			if(c == '\n'){
				lineNum++;
				lineWidth = 0;
			}
			
			lineWidth++;

			curChar = (char) c;
		}catch(IOException e){
			e.printStackTrace();
			System.exit(4);
		}
	}
	
	public int getLineNum(){
		return lineNum;
	}
	
	public int getLineWidth() {
		return lineWidth;
	}

	private static boolean isWordChar(char c){
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private static boolean isDigitChar(char c){
		return (c >= '0' & c <= '9');
	}

	private static boolean isValidIdenfifierChar(char c){
		return isValidIdentifierStartChar(c) || isDigitChar(c) || c == '_';
	}

	private static boolean isValidIdentifierStartChar(char c){
		return isWordChar(c);
	}
	
	private static boolean isNewline(char c){
		return c == '\n' || c == '\r';
	}

	private static boolean isWhitespace(char c){
		return isNewline(c) || c == ' ' || c == '\t';
	}
}
