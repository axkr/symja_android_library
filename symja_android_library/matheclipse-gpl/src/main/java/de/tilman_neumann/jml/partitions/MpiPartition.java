/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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

import java.util.Map;

import de.tilman_neumann.util.SortedMultiset_TopDown;

/**
 * A partition of a multipartite integer.
 * @author Tilman Neumann
 */
public class MpiPartition extends SortedMultiset_TopDown<Mpi> {

	private static final long serialVersionUID = -3956646078614268223L;

	public MpiPartition() {
		super();
	}
	
	public MpiPartition(Mpi[] elements) {
		super(elements);
	}
	
	// TODO: Implement sum() of parts
	
	// TODO: Implement getNumberOfRealizations()

	/**
	 * Returns a sum-like representation of this partitions,
	 * with parts separated by "+" and the multiplicity indicated by "*".
	 * Biggest parts are shown first.
	 * 
	 * Some example partitions of the multipartite number [3, 2, 1]: 
	 * [3, 2, 1], 
	 * [3, 2, 0] + [0, 0, 1], 
	 * [3, 1, 1] + [0, 1, 0], 
	 * [3, 1, 0] + [0, 1, 1], 
	 * [3, 1, 0] + [0, 1, 0] + [0, 0, 1], 
	 * [3, 0, 1] + [0, 2, 0], 
	 * [3, 0, 1] + 2*[0, 1, 0],
	 * ...
	 */
	public String toString() {
		if (this.size()>0) {
			// Implementation note: Is faster with String than with StringBuffer!
			String factorStr = "";
			for (Map.Entry<Mpi, Integer> entry : this.entrySet()) {
				Integer multiplicity = entry.getValue();
				if (multiplicity.intValue() > 1) {
					factorStr += multiplicity + "*";
				}
				factorStr += entry.getKey();
				factorStr += " + ";
			}
			// remove the last " + "
			return factorStr.substring(0, factorStr.length()-3);
		}
		
		// no elements
		return "0";
	}
}
