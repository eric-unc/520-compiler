package miniJava.ContextualAnalysis;

import java.util.ArrayList;

public class ErrorReporter {
	private final ArrayList<String> errors = new ArrayList<>();
	
	public void addError(String error){
		errors.add(error);
	}
	
	public boolean hasErrors(){
		return !errors.isEmpty();
	}
	
	public void printErrors(){
		errors.forEach(System.out::println);
	}
}
