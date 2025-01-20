/*
 * PSIQS 4.0 is a Java library for integer factorization, including a parallel self-initializing quadratic sieve (SIQS).
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Smooth;

/**
 * A congruence equation system solver doing Gaussian elimination in parallel.
 * 
 * @author David McGuigan (adapted from Tilman Neumann's single-threaded Gauss solver)
 */
public class MatrixSolverPGauss01 extends MatrixSolverBase03 {

	private static final Logger LOG = LogManager.getLogger(MatrixSolverPGauss01.class);

	final int solveThreads;

	public MatrixSolverPGauss01(int threads) {
		super();
		this.solveThreads = threads;
	}

	@Override
	public String getName() {
		return "PGaussSolver01("+solveThreads+")";
	}
		
	// the threads need access to these
	List<Smooth> congruences;  // input list of congruences, needed when a null vector is found
	List<MatrixRow> rows;      // input list of rows, needed when a null vector is found
	private MatrixRow[] pivotRowsForColumns; // storage for the pivot rows as they are found
	private ReentrantLock[] locks;  // for guarding writing to pivotRowsForColumns

	private volatile FactorException factorFound;  // needed to pass the exception back from the threads
	
	// simple ticket system to iterate over the rows through the threads
	private int nextRow = 0;	
	
	private synchronized int getNextRow() {
		return nextRow++;
	}
	
	// Main processing thread.
	// Takes a row from the matrix and iteratively applies the pivot rows. 
	// if it's the first row to be resolved down to a column, it becomes
	// the pivot row for further rows having that column
	private class PivotThread extends Thread {
		
		public void process(int rowIndex) throws FactorException {
			MatrixRow row = rows.get(rowIndex);
			int columnIndex = row.getBiggestColumnIndex();
			MatrixRow possibleSolution = null;
			while (columnIndex >= 0) {
				int lockColumn = columnIndex;
				MatrixRow pivot;
				locks[lockColumn].lock();
				try {
					pivot = pivotRowsForColumns[columnIndex];
					if (pivot == null) {
						pivotRowsForColumns[columnIndex] = row;
						return;
					}
					if (row.getColumnCount()<pivot.getColumnCount()) {
						// switch pivots
						MatrixRow t = pivot;
						pivot = row;
						pivotRowsForColumns[columnIndex] = row;
						row = t;
					}			
				
					// solution operations taken directly from original MatrixSolverGauss01
					row.addXor(pivot); // This operation should be fast!
					if (!row.isNullVector()) {
						columnIndex = row.getBiggestColumnIndex();
					} else {
						possibleSolution = row;
						break;
					}
				} finally {
					locks[lockColumn].unlock();
				}
			}
			
			if (possibleSolution != null) {
				//LOG.debug("solve(): 5: Found null-vector: " + row);
				// Found null vector -> recover the set of AQ-pairs from its row index history
				HashSet<AQPair> totalAQPairs = new HashSet<AQPair>(); // Set required for the "xor"-operation below
				for (int rowIndex2 : possibleSolution.getRowIndexHistoryAsList()) {
					Smooth congruence = congruences.get(rowIndex2);
					// add the new AQ-pairs via "xor"
					congruence.addMyAQPairsViaXor(totalAQPairs);
				}
				// "return" the AQ-pairs of the null vector
				processNullVector(totalAQPairs);
			}
		}

		@Override
		public void run() {
			while (factorFound == null) {
				int rowNum = getNextRow();
				if (rowNum>=rows.size()) {
					return;
				}
				try {
					process(rowNum);
				} catch (FactorException e) {
					// trap the exception so run() is compatible with override
					factorFound = e;
				}
			}
		}		
	}

	@Override
	protected void solve(List<Smooth> congruences, Map<Integer, Integer> factors_2_columnIndices) throws FactorException {
		
		//set the global structures
		this.congruences = congruences;
		this.rows = RichMatrixFactory.createMatrix(congruences, factors_2_columnIndices);
		
		int numColumn = factors_2_columnIndices.size();
		pivotRowsForColumns = new MatrixRow[numColumn];
		
		locks = new ReentrantLock[numColumn];
		for (int i=0; i<numColumn; i++) {
			locks[i] = new ReentrantLock();
		}

		//set up for iteration
		nextRow = 0;
		factorFound = null;
		
		// release the hounds!
		PivotThread[] threads = new PivotThread[solveThreads];
		for (int i=0; i<solveThreads; i++) {
			threads[i] = new PivotThread();
			threads[i].setName("S-"+i);
			threads[i].start();
		}

		// take a nap while the threads work
		for (PivotThread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				LOG.debug(e, e);
			}
		}
		
		if (factorFound != null) {
			throw factorFound;  // pass on the exception found by the threads
		}
	}
}

