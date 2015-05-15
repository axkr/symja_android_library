package org.matheclipse.core.basic;

/**
 * Allocate double arrays.
 *
 */
public class Alloc {

	private Alloc() {
	}

	/**
	 * Allocate a <code>double[]</code> array for vector creation.
	 * 
	 * @param i
	 *            size of array
	 * @return
	 */
	public static final double[] vector(int i) {
		return new double[i];
	}

	/**
	 * Allocate a <code>double[][]</code> array for matrix creation.
	 * 
	 * @param i
	 *            number of rows
	 * @param j
	 *            number of columns
	 * @return
	 */
	public static final double[][] matrix(int i, int j) {
		return new double[i][j];
	}

}
