/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * The code in this file was written by Dario Alejandro Alpern as part of his ECM/Siqs applet. See https://github.com/alpertron/calculators.
 * I (Tilman Neumann) am very thankful for his permission to redistribute it under GPL3.
 * Copyright (C) 2018  Dario Alejandro Alpern (Buenos Aires - Argentina).
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.factor.base.matrixSolver;

import java.io.Serializable;

/**
 * Block-Lanczos matrix solver by Dario Alejandro Alpern.
 * Extracted from Dario Alpern's Siqs package by Tilman Neumann.
 */
public class BlockLanczos implements Serializable {
	private static final long serialVersionUID = 4895131221265947979L;

	private static final long DosALa31_1 = (1L << 31) - 1; // 2^31 - 1

	/**
	 * Block-Lanczos matrix solver.
	 * 
	 * @param matrixB The matrix holding the smooth relations. Each row = matrixB[rowIndex] represents one smooth congruence.
	 * A row contains the indices of the primes that occur in the smooth part of the relation with odd exponent.
	 * As such, the size of the sub-arrays depends on each relation. matrixB is not changed by the Block-Lanczos algorithm.
	 * 
	 * @param matrixBlength number of rows
	 * 
	 * @return The solution matrix matrixV. This matrix can encode 32 different potential solutions: one in bit 0 of all ints, 
	 * the next one in bit 1 of all ints, and so on.
	 */
	public int[] computeBlockLanczos(final int[][] matrixB, int matrixBlength) {
	    int i, j, k;
	    int oldDiagonalSSt, newDiagonalSSt;
	    int index, indexC, mask;
	    int[] matrixD = new int[32];
	    int[] matrixE = new int[32];
	    int[] matrixE2 = new int[32];
	    int[] matrixF = new int[32];
	    int[] matrixWinv = new int[32];
	    int[] matrixWinv1 = new int[32];
	    int[] matrixWinv2 = new int[32];
	    int[] matrixVtV0 = new int[32];
	    int[] matrixVt1V0 = new int[32];
	    int[] matrixVt2V0 = new int[32];
	    int[] matrixVtAV = new int[32];
	    int[] matrixVt1AV1 = new int[32];
	    int[] matrixAV = new int[matrixBlength];
	    int[] matrixCalcParenD = new int[32];
	    int[] vectorIndex = new int[64];
	    // The solution matrix, encoding up to 32 solutions, one in bit 0 of all ints, one in bit 1 of all ints, and so on
	    int[] matrixV = new int[matrixBlength];
	    int[] matrixV1 = new int[matrixBlength];
	    int[] matrixV2 = new int[matrixBlength];
	    // matrix X-Y
	    int[] matrixXmY = new int[matrixBlength];
	    int[] matrixCalc3 = new int[matrixBlength]; // Matrix that holds temporary data
	    int[] matrixTemp;
	    int[] matrixCalc1 = new int[32]; // Matrix that holds temporary data
	    int[] matrixCalc2 = new int[32]; // Matrix that holds temporary data
	    int[] matr;
	    int rowMatrixV;
	    int rowMatrixXmY;
	    long seed;
	    int Temp, Temp1;
	    int stepNbr = 0;
	    int currentOrder, currentMask;
	    int row, col;
	    int leftCol, rightCol;
	    int minind, min, minanswer;
	    int[] rowMatrixB;

	    newDiagonalSSt = oldDiagonalSSt = -1;

	    /* Initialize matrix X-Y and matrix V_0 with random data */
	    seed = 123456789L;
	    for (i = matrixBlength - 1; i >= 0; i--) {
	    	matrixXmY[i] = (int) seed;
	    	seed = (seed * 62089911L + 54325442L) % DosALa31_1;
	    	matrixXmY[i] += (int) (seed * 6543265L);
	    	seed = (seed * 62089911L + 54325442L) % DosALa31_1;
	    	matrixV[i] = (int) seed;
	    	seed = (seed * 62089911L + 54325442L) % DosALa31_1;
	    	matrixV[i] += (int) (seed * 6543265L);
	    	seed = (seed * 62089911L + 54325442L) % DosALa31_1;
	    }
	    // Compute matrix Vt(0) * V(0)
	    MatrTranspMult(matrixV, matrixV, matrixVtV0);
	    
	    // main loop: compute matrix X-Y
	    for (;;) {
			oldDiagonalSSt = newDiagonalSSt;
			stepNbr++;
			// Compute matrix A * V(i)
			MultiplyAByMatrix(matrixB, matrixV, matrixCalc3, matrixAV, matrixBlength);
			// Compute matrix Vt(i) * A * V(i)
			MatrTranspMult(matrixV, matrixAV, matrixVtAV);

			/* If Vt(i) * A * V(i) = 0, end of loop */
			for (i = matrixVtAV.length - 1; i >= 0; i--) {
		        if (matrixVtAV[i] != 0) {
		        	break;
		        }
			}
			if (i < 0) {
		        break;
			} /* End X-Y calculation loop */

			/* Selection of S(i) and W(i) */

			matrixTemp = matrixWinv2;
			matrixWinv2 = matrixWinv1;
			matrixWinv1 = matrixWinv;
			matrixWinv = matrixTemp;

			mask = 1;
			for (j = 31; j >= 0; j--) {
				matrixD[j] = matrixVtAV[j]; /*  D = VtAV    */
		        matrixWinv[j] = mask; /*  Winv = I    */
		        mask *= 2;
			}

			index = 31;
			indexC = 31;
			for (mask = 1; mask != 0; mask *= 2) {
		        if ((oldDiagonalSSt & mask) != 0) {
		        	matrixE[index] = indexC;
		        	matrixF[index] = mask;
		        	index--;
		        }
		        indexC--;
			}
			indexC = 31;
			for (mask = 1; mask != 0; mask *= 2) {
		        if ((oldDiagonalSSt & mask) == 0) {
		        	matrixE[index] = indexC;
		        	matrixF[index] = mask;
		        	index--;
		        }
		        indexC--;
			}
			newDiagonalSSt = 0;
			for (j = 0; j < 32; j++) {
		        currentOrder = matrixE[j];
		        currentMask = matrixF[j];
		        for (k = j; k < 32; k++) {
		        	if ((matrixD[matrixE[k]] & currentMask) != 0) {
		        		break;
		        	}
		        }
		        if (k < 32) {
		        	i = matrixE[k];
		        	Temp = matrixWinv[i];
		        	matrixWinv[i] = matrixWinv[currentOrder];
		        	matrixWinv[currentOrder] = Temp;
		        	Temp1 = matrixD[i];
		        	matrixD[i] = matrixD[currentOrder];
		        	matrixD[currentOrder] = Temp1;
		        	newDiagonalSSt |= currentMask;
		        	for (k = 31; k >= 0; k--) {
		        		if (k != currentOrder && ((matrixD[k] & currentMask) != 0)) {
		        			matrixWinv[k] ^= Temp;
		        			matrixD[k] ^= Temp1;
		        		}
		        	} /* end for k */
		        } else {
		        	for (k = j; k < 32; k++) {
		        		if ((matrixWinv[matrixE[k]] & currentMask) != 0) {
		        			break;
		        		}
		        	}
		        	// The loop above will always find a hit at k<=31.
		        	// Thus we do not need to worry that the following statement overflows matrixE.
		        	i = matrixE[k];
		        	Temp = matrixWinv[i];
		        	matrixWinv[i] = matrixWinv[currentOrder];
		        	matrixWinv[currentOrder] = Temp;
		        	Temp1 = matrixD[i];
		        	matrixD[i] = matrixD[currentOrder];
		        	matrixD[currentOrder] = Temp1;
		        	for (k = 31; k >= 0; k--) {
		        		if ((matrixWinv[k] & currentMask) != 0) {
		        			matrixWinv[k] ^= Temp;
		        			matrixD[k] ^= Temp1;
		        		}
		        	} /* end for k */
		        } /* end if */
			} /* end for j */
			/* Compute D(i), E(i) and F(i) */
			if (stepNbr >= 3) {
		        // F = -Winv(i-2) * (I - Vt(i-1)*A*V(i-1)*Winv(i-1)) * ParenD * S*St
		        MatrixMultiplication(matrixVt1AV1, matrixWinv1, matrixCalc2);
		        index = 31; /* Add identity matrix */
		        for (mask = 1; mask != 0; mask *= 2) {
		        	matrixCalc2[index] ^= mask;
		        	index--;
		        }
		        MatrixMultiplication(matrixWinv2, matrixCalc2, matrixCalc1);
		        MatrixMultiplication(matrixCalc1, matrixCalcParenD, matrixF);
		        MatrMultBySSt(matrixF, newDiagonalSSt, matrixF);
			}
			// E = -Winv(i-1) * Vt(i)*A*V(i) * S*St
			if (stepNbr >= 2) {
		        MatrixMultiplication(matrixWinv1, matrixVtAV, matrixE2);
		        MatrMultBySSt(matrixE2, newDiagonalSSt, matrixE2);
			}
			// ParenD = Vt(i)*A*A*V(i) * S*St + Vt(i)*A*V(i)
			// D = I - Winv(i) * ParenD
			MatrTranspMult(matrixAV, matrixAV, matrixCalc1); // Vt(i)*A*A*V(i)
			MatrMultBySSt(matrixCalc1, newDiagonalSSt, matrixCalc1);
			MatrixAddition(matrixCalc1, matrixVtAV, matrixCalcParenD);
			MatrixMultiplication(matrixWinv, matrixCalcParenD, matrixD);
			index = 31; /* Add identity matrix */
			for (mask = 1; mask != 0; mask *= 2) {
		        matrixD[index] ^= mask;
		        index--;
			}

			/* Update value of X - Y */
			MatrixMultiplication(matrixWinv, matrixVtV0, matrixCalc1);
			MatrixMultAdd(matrixV, matrixCalc1, matrixXmY);

			/* Compute value of new matrix V(i) */
			// V(i+1) = A * V(i) * S * St + V(i) * D + V(i-1) * E + V(i-2) * F
			MatrMultBySSt(matrixAV, newDiagonalSSt, matrixCalc3);
			MatrixMultAdd(matrixV, matrixD, matrixCalc3);
			if (stepNbr >= 2) {
		        MatrixMultAdd(matrixV1, matrixE2, matrixCalc3);
		        if (stepNbr >= 3) {
		        	MatrixMultAdd(matrixV2, matrixF, matrixCalc3);
		        }
			}
			/* Compute value of new matrix Vt(i)V0 */
			// Vt(i+1)V(0) = Dt * Vt(i)V(0) + Et * Vt(i-1)V(0) + Ft * Vt(i-2)V(0)
			MatrTranspMult(matrixD, matrixVtV0, matrixCalc2);
			if (stepNbr >= 2) {
		        MatrTranspMult(matrixE2, matrixVt1V0, matrixCalc1);
		        MatrixAddition(matrixCalc1, matrixCalc2, matrixCalc2);
		        if (stepNbr >= 3) {
		        	MatrTranspMult(matrixF, matrixVt2V0, matrixCalc1);
		        	MatrixAddition(matrixCalc1, matrixCalc2, matrixCalc2);
		        }
			}
			matrixTemp = matrixV2;
			matrixV2 = matrixV1;
			matrixV1 = matrixV;
			matrixV = matrixCalc3;
			matrixCalc3 = matrixTemp;
			matrixTemp = matrixVt2V0;
			matrixVt2V0 = matrixVt1V0;
			matrixVt1V0 = matrixVtV0;
			matrixVtV0 = matrixCalc2;
			matrixCalc2 = matrixTemp;
			matrixTemp = matrixVt1AV1;
			matrixVt1AV1 = matrixVtAV;
			matrixVtAV = matrixTemp;
	    } /* end while */

	    /* Find matrix V1:V2 = B * (X-Y:V) */
	    for (row = matrixBlength - 1; row >= 0; row--) {
	    	matrixV1[row] = matrixV2[row] = 0;
	    }
	    for (row = matrixBlength - 1; row >= 0; row--) {
	    	rowMatrixB = matrixB[row];
	    	rowMatrixXmY = matrixXmY[row];
	    	rowMatrixV = matrixV[row];
	    	// The vector rowMatrixB includes the indexes of the columns set to '1'.
	    	for (index = rowMatrixB.length - 1; index >= 0; index--) {
	    		col = rowMatrixB[index];
		        matrixV1[col] ^= rowMatrixXmY;
		        matrixV2[col] ^= rowMatrixV;
	    	}
	    }
	    rightCol = 64;
	    leftCol = 0;
	    while (leftCol < rightCol) {
	    	for (col = leftCol; col < rightCol; col++) {
	    		// For each column find the first row which has a '1'.
	    		// Columns outside this range must have '0' in all rows.
		        matr = (col >= 32 ? matrixV1 : matrixV2);
		        mask = 0x80000000 >>> (col & 31);
		        vectorIndex[col] = -1;    // indicate all rows in zero in advance.
		        for (row = 0; row < matr.length; row++) {
		        	if ((matr[row] & mask) != 0) {
		        		// First row for this mask is found. Store it.
		        		vectorIndex[col] = row;
		        		break;
		        	}
		        }
	    	}
	    	for (col = leftCol; col < rightCol; col++) {
		        if (vectorIndex[col] < 0) {
		        	// If all zeros in col 'col', exchange it with first column with
		        	// data different from zero (leftCol).
		        	colexchange(matrixXmY, matrixV, matrixV1, matrixV2, leftCol, col);
		        	vectorIndex[col] = vectorIndex[leftCol];
		        	vectorIndex[leftCol] = -1;  // This column now has zeros.
		        	leftCol++;                  // Update leftCol to exclude that column.
		        }
	    	}
	    	if (leftCol == rightCol) {
		        break;
	    	}
	    	// At this moment all columns from leftCol to rightCol are non-zero.
	    	// Get the first row that includes a '1'.
	    	min = vectorIndex[leftCol];
	    	minind = leftCol;
	    	for (col = leftCol+1; col < rightCol; col++) {
		        if (vectorIndex[col] < min) {
		        	min = vectorIndex[col];
		        	minind = col;
		        }
	    	}
	    	minanswer = 0;
	    	for (col = leftCol; col < rightCol; col++) {
		        if (vectorIndex[col] == min) {
		        	minanswer++;
		        }
	    	}
	    	if (minanswer > 1) {
	    		// Two columns with the same first row to '1'.
		        for (col = minind + 1; col < rightCol; col++) {
		        	if (vectorIndex[col] == min) {
		        		// Add first column which has '1' in the same row to
		        		// the other columns so they have '0' in this row after
		        		// this operation.
		        		coladd(matrixXmY, matrixV, matrixV1, matrixV2, minind, col);
		        	}
		        }
	    	} else {
		        rightCol--;
		        colexchange(matrixXmY, matrixV, matrixV1, matrixV2, minind, rightCol);
	    	}
	    }
	    
	    /* find linear independent solutions */
	    leftCol = 0;
	    while (leftCol < rightCol) {
	    	for (col = leftCol; col < rightCol; col++) {
	    		// For each column find the first row which has a '1'.
		        matr = (col >= 32 ? matrixXmY : matrixV);
		        mask = 0x80000000 >>> (col & 31);
		        vectorIndex[col] = -1;    // indicate all rows in zero in advance.
		        for (row = 0; row < matrixV1.length; row++) {
		        	if ((matr[row] & mask) != 0) {
		        		// First row for this mask is found. Store it.
		        		vectorIndex[col] = row;
		        		break;
		        	}
		        }
	    	}
	    	for (col = leftCol; col < rightCol; col++) {
	    		// If all zeros in col 'col', exchange it with last column with
	    		// data different from zero (rightCol).
		        if (vectorIndex[col] < 0) {
		        	rightCol--; // Update rightCol to exclude that column.
		        	colexchange(matrixXmY, matrixV, matrixV1, matrixV2, rightCol, col);
		        	vectorIndex[col] = vectorIndex[rightCol];
		        	vectorIndex[rightCol] = -1; // This column now has zeros.
		        }
	    	}
	    	if (leftCol == rightCol) {
		        break;
	    	}
	    	// At this moment all columns from leftCol to rightCol are non-zero.
	    	// Get the first row that includes a '1'.
	    	min = vectorIndex[leftCol];
	    	minind = leftCol;
	    	for (col = leftCol + 1; col < rightCol; col++) {
		        if (vectorIndex[col] < min) {
		        	min = vectorIndex[col];
		        	minind = col;
		        }
	    	}
	    	minanswer = 0;
	    	for (col = leftCol; col < rightCol; col++) {
		        if (vectorIndex[col] == min) {
		        	minanswer++;
		        }
	    	}
	    	if (minanswer > 1) {
	    		// At least two columns with the same first row to '1'.
		        for (col = minind + 1; col < rightCol; col++) {
		        	if (vectorIndex[col] == min) {
		        		// Add first column which has '1' in the same row to
		        		// the other columns so they have '0' in this row after
		        		// this operation.
		        		coladd(matrixXmY, matrixV, matrixV1, matrixV2, minind, col);
		        	}
		        }
	    	} else {
		        colexchange(matrixXmY, matrixV, matrixV1, matrixV2, minind, leftCol);
		        leftCol++;
	    	}
	    }
	    return matrixV;
	}

	/** 
	 * Multiply binary matrices of length m x 32 by 32 x 32.
	 * The product matrix has size m x 32. Then add it to a m x 32 matrix.
	 * 
	 * @param LeftMatr matrix of length m x 32
	 * @param RightMatr matrix of length 32 x 32
	 * @param ProdMatr result, a matrix of length m x 32
	 */
	private void MatrixMultAdd(int[] LeftMatr, int[] RightMatr, int[] ProdMatr) {
	    int leftMatr;
	    int matrLength = LeftMatr.length;
	    int prodMatr;
	    int row, col;
	    for (row = 0; row < matrLength; row++) {
	    	prodMatr = ProdMatr[row];
	    	leftMatr = LeftMatr[row];
	    	col = 0;
	    	while (leftMatr != 0) {
	    		if (leftMatr < 0) {
	    			prodMatr ^= RightMatr[col];
	    		}
	    		leftMatr *= 2;
	    		col++;
	    	}
	    	ProdMatr[row] = prodMatr;
	    }
	}
	  
	/**
	 * Multiply binary matrices of length m x 32 by 32 x 32.
	 * The product matrix has size m x 32.
	 * 
	 * @param LeftMatr matrix of length m x 32
	 * @param RightMatr matrix of length 32 x 32
	 * @param ProdMatr result, a matrix of length m x 32
	 */
	private void MatrixMultiplication(int[] LeftMatr, int[] RightMatr, int[] ProdMatr) {
	    int leftMatr;
	    int matrLength = LeftMatr.length;
	    int prodMatr;
	    int row, col;
	    for (row = 0; row < matrLength; row++) {
	    	prodMatr = 0;
	    	leftMatr = LeftMatr[row];
	    	col = 0;
	    	while (leftMatr != 0) {
	    		if (leftMatr < 0) {
	    			prodMatr ^= RightMatr[col];
	    		}
	    		leftMatr *= 2;
	    		col++;
	    	}
	    	ProdMatr[row] = prodMatr;
	    }
	}

	/**
	 * Multiply the transpose of a binary matrix of length n x 32 by another binary matrix of length n x 32.
	 * The product matrix has size 32 x 32.
	 * 
	 * @param LeftMatr matrix of length n x 32
	 * @param RightMatr matrix of length n x 32
	 * @param ProdMatr result, a matrix of length 32 x 32
	 */
	private void MatrTranspMult(int[] LeftMatr, int[] RightMatr, int[] ProdMatr) {
	    int prodMatr;
	    int matrLength = LeftMatr.length;
	    int row, col;
	    int iMask = 1;
	    for (col = 31; col >= 0; col--) {
	    	prodMatr = 0;
	    	for (row = 0; row < matrLength; row++) {
	    		if ((LeftMatr[row] & iMask) != 0) {
	    			prodMatr ^= RightMatr[row];
	    		}
	    	}
	    	ProdMatr[col] = prodMatr;
	    	iMask *= 2;
	    }
	}

	/**
	 * Addition (modulo 2) of two binary matrices of size m x 32.
	 * Addition (modulo 2) means that all corresponding bits are "xor"ed.
	 * 
	 * @param leftMatr
	 * @param rightMatr
	 * @param sumMatr result
	 */
	private void MatrixAddition(int[] leftMatr, int[] rightMatr, int[] sumMatr) {
	    for (int row = leftMatr.length - 1; row >= 0; row--) {
	    	sumMatr[row] = leftMatr[row] ^ rightMatr[row];
	    }
	}

	/** 
	 * binary matrix multiplied by scalar?
	 * 
	 * @param Matr
	 * @param diagS
	 * @param Prod
	 */
	private void MatrMultBySSt(int[] Matr, int diagS, int[] Prod) {
	    for (int row = Matr.length - 1; row >= 0; row--) {
	    	Prod[row] = diagS & Matr[row];
	    }
	}

	/** 
	 * Compute Bt * B * matrixV where B is the matrix that holds the factorization relations.
	 * 
	 * @param matrixB the matrix that holds the factorization relations
	 * @param matrixV input matrix
	 * @param TempMatr buffer
	 * @param matrixAV result
	 * @param matrixBlength matrix/vector size
	 */
	private void MultiplyAByMatrix(int[][] matrixB, int[] matrixV, int[] TempMatr, int[] matrixAV, int matrixBlength) {
	    int index;
	    int prodMatr;
	    int[] rowMatrixB = null;

	    /* Compute TempMatr = B * matrixV */
	    for (index = matrixBlength - 1; index >= 0; index--) {
	    	TempMatr[index] = 0;
	    }
	    
	    int row = matrixBlength - 1;
	    int congruenceColumn = -1, matrixVEntry = -1;
	    for (; row >= 0; row--) {
	    	rowMatrixB = matrixB[row];
	    	for (index = rowMatrixB.length - 1; index >= 0; index--) {
	    		// congruenceColumn is the index of a prime occuring in the congruence with odd exponent (an equation system column/variable)
	    		congruenceColumn = rowMatrixB[index];
	    		matrixVEntry = matrixV[row];
	    		// In the following line we would get an ArrayIndexOutOfBoundsException if the equation system is under-determined
	    		TempMatr[congruenceColumn] ^= matrixVEntry;
	    	}
	    }
	    
	    /* Compute ProdMatr = Bt * TempMatr */
	    for (row = matrixBlength - 1; row >= 0; row--) {
	    	prodMatr = 0;
	    	rowMatrixB = matrixB[row];
	    	for (index = rowMatrixB.length - 1; index >= 0; index--) {
	    		congruenceColumn = rowMatrixB[index];
	    		prodMatr ^= TempMatr[congruenceColumn];
	    	}
	    	matrixAV[row] = prodMatr;
	    }
	}

	/**
	 * Exchange columns.
	 * 
	 * @param XmY
	 * @param V
	 * @param V1
	 * @param V2
	 * @param col1 index of first column to exchange
	 * @param col2 index of second column to exchange
	 */
	private void colexchange(int[] XmY, int[] V, int[] V1, int[] V2, int col1, int col2) {
	    int row;
	    int mask1, mask2;
	    int[] matr1, matr2;

	    if (col1 == col2) {
	      return; // Cannot exchange the same column.
	    }
	    
	    // Exchange columns col1 and col2 of V1:V2
	    mask1 = 0x80000000 >>> (col1 & 31);
	    mask2 = 0x80000000 >>> (col2 & 31);
	    matr1 = (col1 >= 32 ? V1 : V2);
	    matr2 = (col2 >= 32 ? V1 : V2);
	    for (row = V.length - 1; row >= 0; row--) {             
	    	// If both bits are different toggle them.
	    	if (((matr1[row] & mask1) == 0) != ((matr2[row] & mask2) == 0)) {
	    		// If both bits are different toggle them.
	    		matr1[row] ^= mask1;
	    		matr2[row] ^= mask2;
	    	}
	    }
	    
	    // Exchange columns col1 and col2 of XmY:V
	    matr1 = (col1 >= 32 ? XmY : V);
	    matr2 = (col2 >= 32 ? XmY : V);
	    for (row = V.length - 1; row >= 0; row--) {
	    	// If both bits are different toggle them.
	    	if (((matr1[row] & mask1) == 0) != ((matr2[row] & mask2) == 0)) {
	    		matr1[row] ^= mask1;
	    		matr2[row] ^= mask2;
	    	}
	    }
	}

	/**
	 * Add columns.
	 * 
	 * @param XmY
	 * @param V
	 * @param V1
	 * @param V2
	 * @param col1
	 * @param col2
	 */
	private void coladd(int[] XmY, int[] V, int[] V1, int[] V2, int col1, int col2) {
	    int row;
	    int mask1, mask2;
	    int[] matr1, matr2;

	    if (col1 == col2) return;
	    
	    // Add column col1 to column col2 of V1:V2
	    mask1 = 0x80000000 >>> (col1 & 31);
	    mask2 = 0x80000000 >>> (col2 & 31);
	    matr1 = (col1 >= 32 ? V1 : V2);
	    matr2 = (col2 >= 32 ? V1 : V2);
	    for (row = V.length - 1; row >= 0; row--) {
	    	// If bit to add is '1'...
	    	if ((matr1[row] & mask1) != 0) {
	    		// Toggle bit in destination.
	    		matr2[row] ^= mask2;
	    	}
	    }
	    // Add column col1 to column col2 of XmY:V
	    matr1 = (col1 >= 32 ? XmY : V);
	    matr2 = (col2 >= 32 ? XmY : V);
	    for (row = V.length - 1; row >= 0; row--) {
	    	// If bit to add is '1'...
	    	if ((matr1[row] & mask1) != 0) {
	    		// Toggle bit in destination.
	    		matr2[row] ^= mask2;
	    	}
	    }
	}
}
