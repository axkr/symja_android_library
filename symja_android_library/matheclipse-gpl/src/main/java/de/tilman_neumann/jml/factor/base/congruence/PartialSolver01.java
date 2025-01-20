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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.base.matrixSolver.IndexSet;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixRow;

/**
 * A Gaussian solver used to find smooth from partial relations.
 * 
 * @author Tilman Neumann
 */
public class PartialSolver01 implements PartialSolver {
	private static final Logger LOG = LogManager.getLogger(PartialSolver01.class);
	
	private static final boolean DEBUG = false;
	
	/** the biggest matrix size found for some N */
	private int maxMatrixSize;
	
	@Override
	public String getName() {
		return "PartialSolver01";
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
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<Partial> congruencesCopy = new ArrayList(congruences.size());
		Map<Long, ArrayList<Partial>> largeFactors_2_partials = new HashMap<>();
		for (Partial congruence : congruences) {
			congruencesCopy.add(congruence);
			addToColumn2RowMap(congruence, largeFactors_2_partials);
		}
		// 2. remove singletons
		removeSingletons(congruencesCopy, largeFactors_2_partials);
		if (congruencesCopy.size() > maxMatrixSize) {
			maxMatrixSize = congruencesCopy.size();
		}
		// 3. Map odd-exp-elements to column indices. Sorting is not required.
		Map<Long, Integer> factors_2_indices = createFactor2ColumnIndexMap(largeFactors_2_partials);
		// 4+5. Create & solve matrix
		Smooth s = solve(congruencesCopy, factors_2_indices);
		// Done
		return s;
	}
	
	/**
	 * Remove singletons from <code>congruences</code>.
	 * This can reduce the size of the equation system; actually it never diminishes the difference (#eqs - #vars).
	 * It is very fast, too - like 60ms for a matrix for which solution via Gauss elimination takes 1 minute.
	 * 
	 * @param congruences 
	 * @param largeFactors_2_partials 
	 */
	private void removeSingletons(List<Partial> congruences, Map<Long, ArrayList<Partial>> largeFactors_2_partials) {
		// Parse all congruences as long as we find a singleton in a complete pass
		boolean foundSingleton;
		do {
			foundSingleton = false;
			Iterator<? extends Partial> congruenceIter = congruences.iterator();
			while (congruenceIter.hasNext()) {
				Partial congruence = congruenceIter.next();
				for (Long oddExpFactor : congruence.getLargeFactorsWithOddExponent()) {
					if (largeFactors_2_partials.get(oddExpFactor).size()==1) {
						// found singleton -> remove from list
						if (DEBUG) LOG.debug("Found singleton -> remove " + congruence);
						congruenceIter.remove();
						// remove from oddExpFactors_2_congruences so we can detect further singletons
						removeFromColumn2RowMap(congruence, largeFactors_2_partials);
						foundSingleton = true;
						break;
					}
				}
			} // one pass finished
		} while (foundSingleton && congruences.size()>0);
		// now all singletons have been removed from congruences.
		if (DEBUG) LOG.debug("#congruences after removing singletons: " + congruences.size());
	}
	
	private void addToColumn2RowMap(Partial congruence, Map<Long, ArrayList<Partial>> largeFactors_2_partials) {
		for (Long factor : congruence.getLargeFactorsWithOddExponent()) {
			ArrayList<Partial> congruenceList = largeFactors_2_partials.get(factor);
			if (congruenceList == null) {
				congruenceList = new ArrayList<Partial>();
				largeFactors_2_partials.put(factor, congruenceList);
			}
			congruenceList.add(congruence);
		}
	}
	
	private void removeFromColumn2RowMap(Partial congruence, Map<Long, ArrayList<Partial>> largeFactors_2_partials) {
		for (Long factor : congruence.getLargeFactorsWithOddExponent()) {
			ArrayList<Partial> congruenceList = largeFactors_2_partials.get(factor);
			congruenceList.remove(congruence);
			if (congruenceList.size()==0) {
				// there are no more congruences with the current factor
				largeFactors_2_partials.remove(factor);
			}
		}
	}
	
	/**
	 * Create a map from factors appearing with odd exponent to matrix column indices.
	 * 
	 * @param factors_2_partials unsorted map from factors to the congruences in which they appear with odd exponent
	 * @return map from factors to column indices
	 */
	protected Map<Long, Integer> createFactor2ColumnIndexMap(Map<Long, ArrayList<Partial>> factors_2_partials) {
		int index = 0;
		Map<Long, Integer> factors_2_columnIndices = new HashMap<>();
		for (Long factor : factors_2_partials.keySet()) {
			factors_2_columnIndices.put(factor, index++);
		}
		return factors_2_columnIndices;
	}

	/**
	 * Create the matrix from the pre-processed congruences and solve it.
	 * @param congruences
	 * @param factors_2_columnIndices map from factors to matrix column indices
	 */
	private Smooth solve(List<Partial> congruences, Map<Long, Integer> factors_2_columnIndices) {
		// create matrix
		List<MatrixRow> rows = createMatrix(congruences, factors_2_columnIndices);
		// solve
		while (rows.size()>0) {
			// Find pivot column index and row:
			// Note that sorting the rows is not good for performance,
			// because the insort operation is more expensive then iterating over all rows.
			Iterator<MatrixRow> rowIter = rows.iterator();
			// Initialize pivot row with first element, which is ensured to exist
			MatrixRow pivotRow = rowIter.next();
			int pivotColumnIndex = pivotRow.getBiggestColumnIndex();
			// Now check if there is a row having a bigger column
			while (rowIter.hasNext()) {
				MatrixRow row = rowIter.next();
				if (DEBUG) LOG.debug("row = " + row);
				int biggestColumnIndex = row.getBiggestColumnIndex();
				if (biggestColumnIndex > pivotColumnIndex) {
					// Found a new pivot candidate
					pivotRow = row;
					pivotColumnIndex = biggestColumnIndex;
				}
			}
			// Now we have selected a pivotColumnIndex and a row having it.
			// -> remove the pivot row from the list and do one Gaussian elimination step
			rows.remove(pivotRow);
			rowIter = rows.iterator();
			if (DEBUG) LOG.debug("solve(): 2: " + rows.size() + " rows");
			while (rowIter.hasNext()) {
				MatrixRow row = rowIter.next();
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
							Partial congruence = congruences.get(rowIndex);
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

	/**
	 * Create the matrix.
	 * @param congruences
	 * @param factors_2_columnIndices
	 * @return
	 */
	private List<MatrixRow> createMatrix(List<Partial> congruences, Map<Long, Integer> factors_2_columnIndices) {
		ArrayList<MatrixRow> matrixRows = new ArrayList<MatrixRow>(congruences.size()); // ArrayList is faster than LinkedList, even with many remove() operations
		int rowIndex = 0;
		int numberOfRows = congruences.size();
		for (Partial congruence : congruences) {
			// row entries = set of column indices where the congruence has a factor with odd exponent
			IndexSet columnIndicesFromOddExpFactors = createColumnIndexSetFromCongruence(congruence, factors_2_columnIndices);
			// initial row history = the current row index
			IndexSet rowIndexHistory = createRowIndexHistory(numberOfRows, rowIndex++);
			MatrixRow matrixRow = new MatrixRow(columnIndicesFromOddExpFactors, rowIndexHistory);
			matrixRows.add(matrixRow);
		}
		if (DEBUG) LOG.debug("constructed matrix with " + matrixRows.size() + " rows and " + factors_2_columnIndices.size() + " columns");
		return matrixRows;
	}
	
	/**
	 * Create the set of matrix column indices from the factors that the congruence has with odd exponent.
	 * @param congruence
	 * @param factors_2_columnIndices
	 * @return set of column indices
	 */
	private IndexSet createColumnIndexSetFromCongruence(Partial congruence, Map<Long, Integer> factors_2_columnIndices) {
		Long[] oddExpFactors = congruence.getLargeFactorsWithOddExponent();
		IndexSet columnIndexBitset = new IndexSet(factors_2_columnIndices.size());
		for (Long oddExpFactor : oddExpFactors) {
			columnIndexBitset.add(factors_2_columnIndices.get(oddExpFactor));
		}
		return columnIndexBitset;
	}
	
	private IndexSet createRowIndexHistory(int numberOfRows, int rowIndex) {
		IndexSet rowIndexHistory = new IndexSet(numberOfRows);
		rowIndexHistory.add(rowIndex);
		if (DEBUG) LOG.debug("numberOfRows=" + numberOfRows + ", rowIndex=" + rowIndex + " -> rowIndexHistory = " + rowIndexHistory);
		return rowIndexHistory;
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
