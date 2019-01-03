/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
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

import java.util.ArrayList;

//import org.apache.log4j.Logger;

/**
 * A congruence used by the matrix solver where the elements have been mapped to integer indices.
 * @author Tilman Neumann
 */
public class MatrixRow {
//	private static final Logger LOG = Logger.getLogger(MatrixRow.class);

	/** The indices of the factors with odd exponent in this row. */
	private IndexSet columnIndices;
	/** The indices of the original rows that where combined to this row (via xor) */
	private IndexSet rowIndexHistory;

	/**
	 * Full constructor. (no copy)
	 * @param columnIndices
	 * @param rowIndexHistory
	 */
	public MatrixRow(IndexSet columnIndices, IndexSet rowIndexHistory) {
		this.columnIndices = columnIndices;
		this.rowIndexHistory = rowIndexHistory;
	}
	
	public ArrayList<Integer> getRowIndexHistoryAsList() {
		return rowIndexHistory.toList();
	}
	
	public int getBiggestColumnIndex() {
		return columnIndices.last();		
	}

	/**
	 * Combine this and other in Z_2, modifying this.
	 * The operation in Z_2 is equivalent to "xor".
	 * @param other
	 */
	public void addXor(MatrixRow other) {
		this.columnIndices.addXor(other.columnIndices);
		this.rowIndexHistory.addXor(other.rowIndexHistory);
	}
	
	/**
	 * @return true if there are no odd exponent factor indices
	 */
	public boolean isNullVector() {
		return columnIndices.isEmpty();
	}
	
	@Override
	public int hashCode() {
//		LOG.debug("hashCode()", new Throwable()); // never used
		return this.rowIndexHistory.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null || !(o instanceof MatrixRow)) return false;
		MatrixRow other = (MatrixRow) o;
		return this.rowIndexHistory.equals(other.rowIndexHistory);
	}

	@Override
	public String toString() {
		return "[columnIndices = " + columnIndices + ", rowIndexHistory = " + rowIndexHistory + "]";
	}
	
//	@Override
//	protected void finalize() {
//		LOG.debug("release " + this);
//	}
}
