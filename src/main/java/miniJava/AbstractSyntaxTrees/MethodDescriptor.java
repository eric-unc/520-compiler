package miniJava.AbstractSyntaxTrees;

public class MethodDescriptor extends RuntimeDescriptor {
	/** code base offset */
	public int cdOffset;
	
	public MethodDescriptor(int cdOffset){
		this.cdOffset = cdOffset;
	}
}
