package org.matheclipse.core.basic;

/**
 * 
 */
public class ThreadMemoryExceededException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3866841055146181865L;

	private int fmemoryType;

	private int fSize;

	public ThreadMemoryExceededException(int memoryType, int size) {
		this.fmemoryType = memoryType;
		this.fSize = size;
	}

	@Override
	public String getMessage() {
		return "Evaluation Interrupted. Thread memory limit exceeded: " + fSize
				+ " Memory-ID: " + fmemoryType + "\n";
	}

}
