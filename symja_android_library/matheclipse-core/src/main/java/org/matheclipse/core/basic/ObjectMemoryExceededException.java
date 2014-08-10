package org.matheclipse.core.basic;

/**
 * 
 */
public class ObjectMemoryExceededException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3866841055146181865L;

	private String fObjectType;

	private int fSize;

	public ObjectMemoryExceededException(String memoryType, int size) {
		super();
		this.fObjectType = memoryType;
		this.fSize = size;
	}

	@Override
	public String getMessage() {
		return "Evaluation interrupted. Object memory limit exceeded: " + fSize
				+ " Object-Type: " + fObjectType + "\n";
	}

}
