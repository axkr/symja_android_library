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
package de.tilman_neumann.jml.factor.base.matrixSolver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Smooth;

/**
 * A simple congruence equation system solver, doing Gaussian elimination.
 * 
 * @author Tilman Neumann
 */
public class MatrixSolverGauss01 extends MatrixSolverBase01 {
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(MatrixSolverGauss01.class);
	
	@Override
	public String getName() {
		return "GaussSolver01";
	}
	
	@Override
	protected void solve(List<Smooth> congruences, Map<Integer, Integer> factors_2_columnIndices) throws FactorException {
		// create matrix
		List<MatrixRow> rows = RichMatrixFactory.createMatrix(congruences, factors_2_columnIndices);
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
				//LOG.debug("row = " + row);
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
			//LOG.debug("solve(): 2: " + rows.size() + " rows");
			while (rowIter.hasNext()) {
				MatrixRow row = rowIter.next();
				//LOG.debug("solve(): 3: current row " + row);
				if (row.getBiggestColumnIndex() == pivotColumnIndex) {
					//LOG.debug("solve(): 4: current row has pivotColumnIndex");
					// Add the pivot row to the current row in Z_2 ("xor"):
					// We can modify the current row object because its old state is not required anymore,
					// and because working on it does not affect the original congruences.
					row.addXor(pivotRow); // This operation should be fast!
					if (row.isNullVector()) {
						//LOG.debug("solve(): 5: Found null-vector: " + row);
						// Found null vector -> recover the set of AQ-pairs from its row index history
						HashSet<AQPair> totalAQPairs = new HashSet<AQPair>(); // Set required for the "xor"-operation below
						for (int rowIndex : row.getRowIndexHistoryAsList()) {
							Smooth congruence = congruences.get(rowIndex);
							// add the new AQ-pairs via "xor"
							congruence.addMyAQPairsViaXor(totalAQPairs);
						}
						// "return" the AQ-pairs of the null vector
						processNullVector(totalAQPairs);
						// no factor exception -> drop improper null vector
						rowIter.remove(); 
					} // else: current row is not a null-vector -> just keep it
				} // else: current row does not have the pivotColumnIndex -> just keep it
			}
		}
	}
}
