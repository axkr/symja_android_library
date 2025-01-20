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
package de.tilman_neumann.jml.partitions;

/**
 * A multipartite number [n_1, n_2, ..., n_k] like [1,3,4,2,0,1]. Each n_i signifies a count of things of some type i.
 * 
 * @author Tilman Neumann
 */
public interface Mpi extends Comparable<Mpi>, Iterable<Integer> {

	/**
	 * @return dimension of the multipartite number, it's "partiteness"
	 */
	int getDim();
	
	/**
	 * @return total number of entries = the sum of the elements of this multipartite number
	 */
	int getCardinality();

	/**
	 * Returns the entry of the given index, with 0<=index<dim.
	 * @param index
	 * @return entry at index 'index'
	 */
	int getElem(int index);
	
	/**
	 * Sets the entry of the given index, with 0<=index<dim.
	 * @param index
	 * @param value
	 */
	void setElem(int index, int value);

	/**
	 * @return the index of the first non-zero entry
	 */
	int firstNonZeroPartIndex();

	/**
	 * Returns the pair [lower, upper] of consecutive subvalues of this (according to the 
	 * ordering relation) such that lower + other <= this and upper + other >= this.
	 * @param other
	 * @return the lower and upper bound of this - other
	 */
	Mpi[] subtract(Mpi other);

	/**
	 * Like subtract() but when we know that other fits piece-wise into this. That means faster ;)
	 * @param other a multipartite integer that has no element greater than the corresponding element of this
	 * @return this - other
	 */
	Mpi complement(Mpi other);

	/**
	 * Computes a kind of division by 2 of this. The result is a pair of [lower, upper] values
	 * with lower + upper = this (element-wise addition) and lower<=upper.
	 * @return [lower, upper]
	 */
	Mpi[] div2();

	/**
	 * Special operation computing the biggest allowed subvalue of this
	 * that is not greater than lastPart and not greater than this-firstPart.
	 * @param firstPart
	 * @param lastPart
	 * @return min(lastPart, lower(this - firstPart))
	 */
	Mpi maxNextPart(Mpi firstPart, Mpi lastPart);
	
	/**
	 * Compare this with another multipartite integer.
	 */
	@Override
	int compareTo(Mpi other);
}
