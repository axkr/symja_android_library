package org.matheclipse.core.basic;

public class Alloc {	 

	private Alloc() {
	}

	public static final double[] vector(int i) {
		return new double[i];
	}

	public static final double[][] matrix(int i, int j) {
		return new double[i][j];
	}

}
