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
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Smooth;

/**
 * A single-threaded congruence equation system solver, doing Gaussian elimination.
 * Much faster than the first version due to improvements by Dave McGuigan.
 * Best single-threaded Gaussian solver for about N<=130 bit.
 * 
 * @author Tilman Neumann, Dave McGuigan
 */
public class MatrixSolverGauss02 extends MatrixSolverBase02 {
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(MatrixSolverGauss02.class);
	
	@Override
	public String getName() {
		return "GaussSolver02";
	}

	@Override
	protected void solve(List<Smooth> congruences, Map<Integer, Integer> factors_2_columnIndices) throws FactorException {
		// create matrix
		List<MatrixRow> rows = RichMatrixFactory.createMatrix(congruences, factors_2_columnIndices);
		// solve
		MatrixRow[] pivotRowsForColumns = new MatrixRow[factors_2_columnIndices.size()]; // storage for the pivot rows as they are found
		for (MatrixRow row : rows) {
			int columnIndex = row.getBiggestColumnIndex();
			while (columnIndex >= 0) {
				MatrixRow pivot = pivotRowsForColumns[columnIndex];
				if (pivot == null) {
					pivotRowsForColumns[columnIndex] = row;
					break;
				}
				
				// solution operations taken directly from MatrixSolverGauss01
				row.addXor(pivot); // This operation should be fast!
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
					break;
				} else {
					// else: current row is not a null-vector, keep trying to reduce
					columnIndex = row.getBiggestColumnIndex();
				}
			}	
		}
	}
}
