/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
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
package de.tilman_neumann.jml.factor.base.congruence;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.base.IntHolder;
import de.tilman_neumann.jml.factor.base.matrixSolver.IndexSet;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixRow;

/**
 * A Gaussian solver used to find smooth from partial relations.
 * Improved version by Dave McGuigan.
 * 
 * @author Tilman Neumann, Dave McGuigan
 */
public class PartialSolver02 implements PartialSolver {
	
	private static final Logger LOG = LogManager.getLogger(PartialSolver02.class);

	private static final boolean DEBUG = false;
	
	private static final int INIT_PARTIALS = 300;
	private static final int INIT_ROWS = 20;
	private static final int INIT_COLUMNS = 200;

	private int numRows;
	private Partial[] congruencesCopy = new Partial[INIT_PARTIALS];
		
	private MatrixRow[] matrixRows = new MatrixRow[INIT_ROWS];
	{
		for(int i=0; i<INIT_ROWS; i++) {
			matrixRows[i] = new MatrixRow(new IndexSet(0), new IndexSet(0));
		}
	}
	
	private MatrixRow[] pivotRows = new MatrixRow[INIT_COLUMNS];

	private Partial[] stackCongruence = new Partial[50];
	private int[] stackCurrentPrimeIndex = new int[50];
	
	private Partial[] onHoldCongruence = new Partial[0];
	private int[] onHoldCurrentPrimeIndex = new int[0];
	private boolean[] haveMatch = new boolean[0];
	
	/** the biggest matrix size found for some N */
	private int maxMatrixSize;

	@Override
	public String getName() {
		return "PartialSolver02";
	}

	@Override
	public void initializeForN() {
		maxMatrixSize = 0;
	}

	@Override
	public Smooth solve(Collection<? extends Partial> congruences) {
		// 1. Create
		// a) a copy of the congruences list, to avoid that the original list is modified during singleton removal.
		// b) a map from (primes with odd power) to congruences. A sorted TreeMap would be nice because then
		//    small primes get small indices in step 4, but experiments showed that HashMap is faster.
		if (DEBUG) LOG.debug("#congruences = " + congruences.size());
		if (congruences.size()>congruencesCopy.length) {
			congruencesCopy = congruences.toArray(new Partial[congruences.size()]);
			numRows = congruences.size();
		} else {
			numRows = 0;
			for (Partial p : congruences) {
				congruencesCopy[numRows++] = p;
			}
		}

		if (numRows == 2) {
			return solve2Partials();
		}
		
		Map<Long, Integer> factors_2_columnIndices = new HashMap<Long, Integer>();
		
		// 2. remove singletons
		if (numRows>0) {
			int index = 0;
			for (int i=0; i<numRows; i++) {
				Partial congruence = congruencesCopy[i];
				for (Long f : congruence.getLargeFactorsWithOddExponent()) {
					if (!factors_2_columnIndices.containsKey(f)) {
						factors_2_columnIndices.put(f, index++);
					}
				}
			}
			removeSingletonsLarge(factors_2_columnIndices);
			if (numRows<2) {
				return null;
			} else if (numRows == 2) {
				return solve2Partials();
			}
			
			// 3. Map odd-exp-elements to column indices. Sorting is not required.
			// re-map column indices
			factors_2_columnIndices.clear();
			index = 0;
			for (int i=0; i<numRows; i++) {
				for (Long f : congruencesCopy[i].getLargeFactorsWithOddExponent()) {
					if (!factors_2_columnIndices.containsKey(f)) {
						factors_2_columnIndices.put(f, index++);
					}
				}
			}
		} else {
			Map<Long, IntHolder> largeFactors_2_partialCount = new HashMap<Long, IntHolder>();
			for (int i=0; i<numRows; i++) {
				Partial congruence = congruencesCopy[i];
				addToColumn2RowCountMap(congruence, largeFactors_2_partialCount);
			}
			removeSingletons(largeFactors_2_partialCount);
			if (numRows<2) {
				return null;
			} else if (numRows == 2) {
				return solve2Partials();
			}
			// 3. Map odd-exp-elements to column indices. Sorting is not required.
			factors_2_columnIndices = createFactor2ColumnIndexMap(largeFactors_2_partialCount);			
		}
		if (numRows > maxMatrixSize) {
			maxMatrixSize = numRows;
		}

		// 4+5. Create & solve matrix
		Smooth s = solve(factors_2_columnIndices);
		// Done
		return s;
	}
	
	// Solving 2 partials is a special case. We just need to check if they have the
	// same factors. If so, success
	private Smooth solve2Partials() {
		Set<Long> unmatchedFactors = new HashSet<Long>();
		for (int i=0; i<numRows; i++) {
			Partial p = congruencesCopy[i];
			for (Long f : p.getLargeFactorsWithOddExponent()) {
				if (!unmatchedFactors.contains(f)) {
					unmatchedFactors.add(f);
				} else {
					unmatchedFactors.remove(f);
				}
			}
		}
		
		if (unmatchedFactors.size() != 0) {
			return null;				
		}
		
		// We found a smooth congruence from partials.
		HashSet<AQPair> totalAQPairs = new HashSet<AQPair>();
		for (int i=0; i<2; i++) {
			Partial congruence = congruencesCopy[i];
			totalAQPairs.add(congruence);
		}
		// Checking for exact squares is done in CongruenceCollector.addSmooth(), no need to do it here again...
		Smooth smoothCongruence = new SmoothComposite(totalAQPairs);
		return smoothCongruence;	
	}
	

	/**
	 * Remove singletons from <code>congruences</code>.
	 * This can reduce the size of the equation system; actually it never diminishes the difference (#eqs - #vars).
	 * It is very fast, too - like 60ms for a matrix for which solution via Gauss elimination takes 1 minute.
	 * 
	 * @param largeFactors_2_partialsCount 
	 */
	private void removeSingletons(Map<Long, IntHolder> largeFactors_2_partialsCount) {
		// Parse all congruences as long as we find a singleton in a complete pass
		boolean foundSingleton;
		do {
			foundSingleton = false;
			for (int i=0; i<numRows; i++) {
				Partial congruence = congruencesCopy[i];
				for (Long oddExpFactor : congruence.getLargeFactorsWithOddExponent()) {
					IntHolder count = largeFactors_2_partialsCount.get(oddExpFactor);
					if (count.value==1) {
						// found singleton -> remove from list
						if (DEBUG) LOG.debug("Found singleton -> remove " + congruence);
						//congruenceIter.remove();
						congruencesCopy[i] = congruencesCopy[--numRows];
						i--; // backup so can be incremented by loop and check the "new" ith entry
						// remove from oddExpFactors_2_congruences so we can detect further singletons
						removeFromColumn2RowCountMap(congruence, largeFactors_2_partialsCount);
//						foundSingleton = true;
						break;
					}
				}
			} // one pass finished
		} while (foundSingleton && numRows>0);
		// now all singletons have been removed from congruences.
		if (DEBUG) LOG.debug("#congruences after removing singletons: " + numRows);
		return;
	}
		
	private void removeSingletonsLarge(Map<Long, Integer> factors_2_columnIndices) {
		int tos = -1;
		if (factors_2_columnIndices.size()>onHoldCongruence.length) {
			onHoldCongruence = new Partial[factors_2_columnIndices.size()*2];
			onHoldCurrentPrimeIndex = new int[factors_2_columnIndices.size()*2];
			haveMatch = new boolean[factors_2_columnIndices.size()*2];
		} else {
			for (int i=0; i<factors_2_columnIndices.size(); i++) {
				onHoldCongruence[i] = null;
				haveMatch[i] = false;
			}
		}
//int loops = 0;
//		boolean looping = true;
//		while(looping) {
//loops++;
			int storedRows = 0;
			for (int i=0; i<numRows; i++) {
				Partial currentCongruence = congruencesCopy[i];
				int currentPrimeIndex = 0;
				while (currentCongruence != null) {
					long factor = currentCongruence.getLargeFactorsWithOddExponent()[currentPrimeIndex];
					int ci = factors_2_columnIndices.get(factor);
					if (haveMatch[ci]) {
						// This prime has been seen multiple times, just check the next prime.
						currentPrimeIndex++;
					} else {
						// This prime hasn't been matched yet.
						if (onHoldCongruence[ci] == null) {
							// First time seeing this prime. Hang on to the congruence.
							onHoldCongruence[ci] = currentCongruence;
							onHoldCurrentPrimeIndex[ci] = currentPrimeIndex;
							currentCongruence = null;
						} else {
							// Second time seeing this prime. It's now matched. 
							// Stack the held congruence for further processing.
							haveMatch[ci] = true;
							Partial heldCongruence = onHoldCongruence[ci];
							int heldCurrentPrimeIndex = onHoldCurrentPrimeIndex[ci]+1;
							if (heldCurrentPrimeIndex>=heldCongruence.getLargeFactorsWithOddExponent().length) {
								congruencesCopy[storedRows++] = heldCongruence;
							} else {
								++tos;
								stackCongruence[tos] = heldCongruence;
								stackCurrentPrimeIndex[tos] = heldCurrentPrimeIndex;
							}
							currentPrimeIndex++;
						}
					}
					// the current entry may have examined all it's primes
					if (currentCongruence != null) {
						if (currentPrimeIndex>=currentCongruence.getLargeFactorsWithOddExponent().length) {
							// every factor seen at least twice
							congruencesCopy[storedRows++] = currentCongruence;
							currentCongruence = null;						
						}
					}
					// if the current entry is complete, see it there's more in the stack to do.
					if (currentCongruence == null) {
						if (tos >= 0) {
							currentCongruence = stackCongruence[tos];
							currentPrimeIndex = stackCurrentPrimeIndex[tos];
							tos--;
						}
					}
				}
			}
//			if(storedRows == numRows) {
//				looping = false;
//			}
//if(looping && loops>1) {
//	LOG.debug("diff="+(numRows-storedRows));
//}
			numRows = storedRows;
//		}
		return;
	}
	
	private void addToColumn2RowCountMap(Partial congruence, Map<Long, IntHolder> largeFactors_2_partialsCount) {
		for (Long factor : congruence.getLargeFactorsWithOddExponent()) {
			IntHolder count = largeFactors_2_partialsCount.get(factor);
			if (count == null) {
				count = new IntHolder(0);
				largeFactors_2_partialsCount.put(factor, count);
			}
			count.value++;
		}
	}
	
	private void removeFromColumn2RowCountMap(Partial congruence, Map<Long, IntHolder> largeFactors_2_partialsCount) {
		for (Long factor : congruence.getLargeFactorsWithOddExponent()) {
			IntHolder count = largeFactors_2_partialsCount.get(factor);
			count.value--;
			if (count.value==0) {
				// there are no more congruences with the current factor
				largeFactors_2_partialsCount.remove(factor);
			}
		}
	}
	
	/**
	 * Create a map from factors appearing with odd exponent to matrix column indices.
	 * 
	 * @param largeFactors_2_partialsCount unsorted map from factors to the congruences in which they appear with odd exponent
	 * @return map from factors to column indices
	 */
	private Map<Long,Integer> createFactor2ColumnIndexMap(Map<Long, IntHolder> largeFactors_2_partialsCount) {
		int index = 0;
		Map<Long,Integer> factors_2_columnIndices = new HashMap<Long,Integer>();
		for (Long factor : largeFactors_2_partialsCount.keySet()) {
			factors_2_columnIndices.put(factor, index++);
		}
		return factors_2_columnIndices;
	}

	/**
	 * Create the matrix from the pre-processed congruences and solve it.
	 * @param congruencesCopy
	 * @param factors_2_columnIndices map from factors to matrix column indices
	 */
	private Smooth solve(Map<Long, Integer> factors_2_columnIndices) {
		// create matrix
		createMatrix(factors_2_columnIndices);

		Smooth s;
		if (numRows>0) {
			s = solveLarge(factors_2_columnIndices);
		} else {
			s = solveSmall(factors_2_columnIndices);
		}
		return s;
	}
	
	
	/**
	 * Create the matrix from the pre-processed congruences and solve it.
	 * @param congruencesCopy
	 * @param factors_2_columnIndices map from factors to matrix column indices
	 */
	private Smooth solveSmall(Map<Long, Integer> factors_2_columnIndices) {

		// solve the long way
		for (int i=0; i<numRows; i++) {
			// Find pivot column index and row:
			int chosenRow = i;
			int pivotColumnIndex = matrixRows[i].getBiggestColumnIndex();
			// Note that sorting the rows is not good for performance,
			// because the insort operation is more expensive then iterating over all rows.
			// Now check if there is a row having a bigger column
			for (int j=i+1; j<numRows; j++) {
				if (DEBUG) LOG.debug("row = " + matrixRows[j]);
				int biggestColumnIndex = matrixRows[j].getBiggestColumnIndex();
				if (biggestColumnIndex > pivotColumnIndex) {
					// Found a new pivot candidate
					chosenRow = j;
					pivotColumnIndex = biggestColumnIndex;
				}
			}
			// Now we have selected a pivotColumnIndex and a row having it.
			// -> remove the pivot row from the list and do one Gaussian elimination step
			MatrixRow pivotRow = matrixRows[chosenRow];
			matrixRows[chosenRow] = matrixRows[i];
			matrixRows[i] = pivotRow; //don't need to set [i] since never referenced again
			for (int j=i+1; j<numRows; j++) {
				MatrixRow row = matrixRows[j];
				if (DEBUG) LOG.debug("solve(): 3: current row " + row);
				if (row.getBiggestColumnIndex() == pivotColumnIndex) {
					if (DEBUG) LOG.debug("solve(): 4: current row has pivotColumnIndex");
					// Add the pivot row to the current row in Z_2 ("xor"):
					// We can modify the current row object because its old state is not required anymore,
					// and because working on it does not affect the original congruences.
					row.addXor(pivotRow); // This operation should be fast!
					if (row.isNullVector()) {
						if (DEBUG) LOG.debug("solve(): 5: Found null-vector: " + row);
						// Found null vector -> recover the set of AQ-pairs from its row index history
						HashSet<AQPair> totalAQPairs = new HashSet<AQPair>(); // Set required for the "xor"-operation below
						for (int rowIndex : row.getRowIndexHistoryAsList()) {
							Partial congruence = congruencesCopy[rowIndex];
							// add the new partial via "xor"
							if (!totalAQPairs.remove(congruence)) totalAQPairs.add(congruence);
						}
						// We found a smooth congruence from partials.
						// Checking for exact squares is done in CongruenceCollector.addSmooth(), no need to do it here again...
						Smooth smoothCongruence = new SmoothComposite(totalAQPairs);
						return smoothCongruence;
					} // else: current row is not a null-vector -> just keep it
				} // else: current row does not have the pivotColumnIndex -> just keep it
			}
		}
		return null;
	}
	
	private Smooth solveLarge(Map<Long, Integer> factors_2_columnIndices) {

		// solve the long way
		if (pivotRows.length<factors_2_columnIndices.size()) {
			pivotRows = new MatrixRow[factors_2_columnIndices.size()*2];
		} else {
			for (int i=0; i<factors_2_columnIndices.size(); i++) {
				pivotRows[i] = null;
			}
		}
		for (int i=0; i<numRows; i++) {
			MatrixRow row = matrixRows[i];
			int columnIndex = row.getBiggestColumnIndex();
			while (columnIndex >= 0) {
				MatrixRow pivot = pivotRows[columnIndex];
				if (pivot == null) {
					pivotRows[columnIndex] = row;
					break;
				}
				
				// solution operations taken directly from MatrixSolverGauss01
				row.addXor(pivot); // This operation should be fast!
				if (row.isNullVector()) {
					if (DEBUG) LOG.debug("solve(): 5: Found null-vector: " + row);
					// Found null vector -> recover the set of AQ-pairs from its row index history
					HashSet<AQPair> totalAQPairs = new HashSet<AQPair>(); // Set required for the "xor"-operation below
					for (int rowIndex : row.getRowIndexHistoryAsList()) {
						Partial congruence = congruencesCopy[rowIndex];
						// add the new partial via "xor"
						if (!totalAQPairs.remove(congruence)) totalAQPairs.add(congruence);
					}
					// We found a smooth congruence from partials.
					// Checking for exact squares is done in CongruenceCollector.addSmooth(), no need to do it here again...
					Smooth smoothCongruence = new SmoothComposite(totalAQPairs);
					return smoothCongruence;
				} else {
					// else: current row is not a null-vector, keep trying to reduce
					columnIndex = row.getBiggestColumnIndex();
				}
			}	
		}
		return null;
	}


	/**
	 * Create the matrix.
	 * @param congruencesCopy
	 * @param factors_2_columnIndices
	 * @return
	 */
	private void createMatrix(Map<Long, Integer> factors_2_columnIndices) {
		if (matrixRows.length<numRows) {
			matrixRows = new MatrixRow[numRows];
			for (int i=0; i<numRows; i++) {
				matrixRows[i] = new MatrixRow(new IndexSet(0), new IndexSet(0));
			}
		}
		for (int i=0; i<numRows; i++) {
			Partial congruence = congruencesCopy[i];
			// row entries = set of column indices where the congruence has a factor with odd exponent
			createColumnIndexSetFromCongruence(congruence, factors_2_columnIndices, matrixRows[i]);
			// initial row history = the current row index
			createRowIndexHistory(numRows, i, matrixRows[i]);
//			matrixRows[i] = new MatrixRow(columnIndexBitset,rowIndexHistory);
		}
		if (DEBUG) LOG.debug("constructed matrix with " + numRows + " rows and " + factors_2_columnIndices.size() + " columns");
		return;
	}
	
	/**
	 * Create the set of matrix column indices from the factors that the congruence has with odd exponent.
	 * @param congruence
	 * @param factors_2_columnIndices
	 * @return set of column indices
	 */
	private void createColumnIndexSetFromCongruence(Partial congruence, Map<Long, Integer> factors_2_columnIndices, MatrixRow row) {
		IndexSet columnIndexBitset = row.getColumnIndices();
		columnIndexBitset.setBits(factors_2_columnIndices.size());
		for (Long oddExpFactor : congruence.getLargeFactorsWithOddExponent()) {
			columnIndexBitset.add(factors_2_columnIndices.get(oddExpFactor));
		}
		return;
	}
	
	private void createRowIndexHistory(int numberOfRows, int rowIndex, MatrixRow row) {
		IndexSet rowIndexHistory = row.getRowIndexHistory(); 
		rowIndexHistory.setBits(numberOfRows);
		rowIndexHistory.add(rowIndex);
		if (DEBUG) LOG.debug("numberOfRows=" + numberOfRows + ", rowIndex=" + rowIndex + " -> rowIndexHistory = " + rowIndexHistory);
		return;
	}
	
	@Override
	public int getMaxMatrixSize() {
		return maxMatrixSize;
	}

	@Override
	public void cleanUp() {
		// nothing to do here
	}
}
