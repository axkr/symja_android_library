package org.matheclipse.core.basic;

public class Alloc {
	public final static int VECTOR_MEMORY = 1;

	public final static int MATRIX_MEMORY = 2;

	private int fVectorCount;

	private int fMatrixCount;

	private static final ThreadLocal<Alloc> instance = new ThreadLocal<Alloc>() {
		@Override
		public Alloc initialValue() {
			return new Alloc();
		}
	};

	/**
	 * Get the thread local "allocation" instance
	 * 
	 * @return
	 */
	public static Alloc get() {
		return instance.get();
	}

	private Alloc() {
		init();
	}

	final public void init() {
		fVectorCount = 0;
		fMatrixCount = 0;
	}

	public final double[] vector(int i) {
		fVectorCount += i;
		// if (Config.SERVER_MODE && fVectorCount >
		// Config.MAX_DOUBLE_VECTOR_SIZE) {
		// throw new ThreadMemoryExceededException(VECTOR_MEMORY, fVectorCount);
		// }
		return new double[i];
	}

	public final double[] vectorCheck(int i) {
		fVectorCount += i;
		// if (Config.SERVER_MODE && fVectorCount >
		// Config.MAX_DOUBLE_VECTOR_SIZE) {
		// throw new ThreadMemoryExceededException(VECTOR_MEMORY, fVectorCount);
		// }
		fVectorCount -= i;
		return new double[i];
	}

	public void freeVector(int i) {
		fVectorCount -= i;
	}

	public final double[][] matrix(int i, int j) {
		fMatrixCount += (i * j);
		// if (Config.SERVER_MODE && fMatrixCount >
		// Config.MAX_DOUBLE_MATRIX_SIZE) {
		// throw new ThreadMemoryExceededException(MATRIX_MEMORY, fMatrixCount);
		// }
		return new double[i][j];
	}

	public final double[][] matrixCheck(int i, int j) {
		fMatrixCount += (i * j);
		// if (Config.SERVER_MODE && fMatrixCount >
		// Config.MAX_DOUBLE_MATRIX_SIZE) {
		// throw new ThreadMemoryExceededException(MATRIX_MEMORY, fMatrixCount);
		// }
		fMatrixCount -= (i * j);
		return new double[i][j];
	}

	public void freeMatrix(int i, int j) {
		fMatrixCount -= (i * j);
	}
}
