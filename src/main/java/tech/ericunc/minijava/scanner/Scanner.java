package tech.ericunc.minijava.scanner;

import java.io.IOException;
import java.io.InputStream;

public class Scanner {
	private InputStream stream;
	private boolean end = false;

	//private char currentChar;
	//private StringBuilder currentText;

	public Scanner(InputStream stream){
		this.stream = stream;
	}

	public Token scan(){
		return null;
	}

	private char readChar(){
		try{
			int c = stream.read();
			
			if(c == -1)
				end = true;
			
			return (char) c;
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-4);
		}
		
		return '\0'; // not reachable
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
}
