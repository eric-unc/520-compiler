package miniJava.AbstractSyntaxTrees;

import miniJava.mJAM.Machine;

public class MethodDescriptor extends RuntimeDescriptor {
	/** code base offset */
	public int cbOffset;
	
	/** size (methods don't have sizes of course, but this is the ultimate displacement of the LB) */
	public int size;
	
	public int argsize = 0;
	
	public MethodDescriptor(int cbOffset){
		this.cbOffset = cbOffset;
		size = Machine.linkDataSize;
	}
}
