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
			return new Token(END, "", getSourcePosition());

		if(isValidIdentifierStartChar(curChar)){
			HalfPosition pos = getHalfPosition();
			
			StringBuilder word = new StringBuilder();

			while(!end && isValidIdenfifierChar(curChar)){
				word.append(curChar);
				readChar();
			}
			
			SourcePosition position = new SourcePosition(pos, getHalfPosition());

			switch(word.toString()){
				// Keywords
				case "class":
					return new Token(CLASS, "class", position);
				case "this":
					return new Token(THIS, "this", position);
				case "return":
					return new Token(RETURN, "return", position);
				case "new":
					return new Token(NEW, "new", position);
				case "int":
					return new Token(INT, "int", position);
				case "boolean":
					return new Token(BOOLEAN, "boolean", position);
				case "if":
					return new Token(IF, "if", position);
				case "else":
					return new Token(ELSE, "else", position);
				case "while":
					return new Token(WHILE, "while", position);
				case "public":
					return new Token(PUBLIC, "public", position);
				case "private":
					return new Token(PRIVATE, "private", position);
				case "static":
					return new Token(STATIC, "static", position);
				case "void":
					return new Token(VOID, "void", position);

				// Word literals
				case "true":
					return new Token(TRUE, "true", position);
				case "false":
					return new Token(FALSE, "false", position);

				default:
					return new Token(IDEN, word.toString(), position);
			}
		}else if(isDigitChar(curChar)){
			HalfPosition pos = getHalfPosition();
			
			StringBuilder num = new StringBuilder();

			while(!end && isValidIdenfifierChar(curChar)){
				num.append(curChar);
				readChar();
			}

			return new Token(NUM, num.toString(), new SourcePosition(pos, getHalfPosition()));
		}else{
			HalfPosition pos = getHalfPosition();
			
			SourcePosition oneCharPos = new SourcePosition(pos, pos);
			SourcePosition twoCharPos = new SourcePosition(pos, new HalfPosition(pos.getLineNum() + 1, pos.getLineWidth() + 1));
			
			switch(curChar){
				case '=':
					readChar();

					if(curChar == '='){ // ==
						readChar();
						return new Token(EQUALS_OP, "==", twoCharPos);
					}else // just =
						return new Token(EQUALS, "=", oneCharPos);
				case ';':
					readChar();
					return new Token(SEMI, ";", oneCharPos);
				case '.':
					readChar();
					return new Token(DOT, ".", oneCharPos);
				case ',':
					readChar();
					return new Token(COMMA, ",", oneCharPos);
				case '(':
					readChar();
					return new Token(L_PAREN, "(", oneCharPos);
				case ')':
					readChar();
					return new Token(R_PAREN, ")", oneCharPos);
				case '{':
					readChar();
					return new Token(L_BRACKET, "{", oneCharPos);
				case '}':
					readChar();
					return new Token(R_BRACKET, "}", oneCharPos);
				case '[':
					readChar();
					return new Token(L_SQ_BRACK, "[", oneCharPos);
				case ']':
					readChar();
					return new Token(R_SQ_BRACK, "]", oneCharPos);
				case '>':
					readChar();

					if(curChar == '='){ // >=
						readChar();
						return new Token(MORE_EQUAL, ">=", twoCharPos);
					}else // just >
						return new Token(MORE_THAN, ">", oneCharPos);
				case '<':
					readChar();

					if(curChar == '='){ // <=
						readChar();
						return new Token(LESS_EQUAL, "<=", twoCharPos);
					}else // just >
						return new Token(LESS_THAN, "<", oneCharPos);
				case '!':
					readChar();
					
					if(curChar == '='){ // !=
						readChar();
						return new Token(NOT_EQUALS, "!=", twoCharPos);
					}else // just !
						return new Token(NEG, "!", oneCharPos);
				case '&':
					readChar();
					
					if(curChar == '&'){ // &&
						readChar();
						return new Token(AND_LOG, "&&", twoCharPos);
					}else // just &
						return new Token(ERROR, "&", oneCharPos); // TODO: bitwise &
				case '|':
					readChar();
					
					if(curChar == '|'){ // ||
						readChar();
						return new Token(OR_LOG, "||", twoCharPos);
					}else // just |
						return new Token(ERROR, "|", oneCharPos); // TODO: bitwise |
				case '+':
					readChar();
					return new Token(PLUS, "+", oneCharPos);
				case '-':
					readChar();
					return new Token(MINUS, "-", oneCharPos);
				case '*':
					readChar();
					return new Token(TIMES, "*", oneCharPos);
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
						return new Token(DIV, "/", oneCharPos);
				default:
					return new Token(ERROR, "" + curChar, oneCharPos);
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
	
	public HalfPosition getHalfPosition(){
		return new HalfPosition(lineNum, lineWidth);
	}
	
	public SourcePosition getSourcePosition(){
		return new SourcePosition(getHalfPosition(), getHalfPosition());
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
