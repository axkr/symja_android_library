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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.Congruence;

/**
 * Base implementation for a congruence equation system (the "LinAlg phase matrix") solver.
 * 
 * @author Tilman Neumann
 */
abstract public class MatrixSolver {
	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(MatrixSolver.class);

	protected NullVectorProcessor nullVectorProcessor;
	
	public abstract String getName();

	public void setNullVectorProcessor(NullVectorProcessor nullVectorProcessor) {
		this.nullVectorProcessor = nullVectorProcessor;
	}

	/**
	 * Main method to solve a congruence equation system.
	 * @param congruences the congruences forming the equation system
	 * @throws FactorException if a factor of N was found
	 */
	public void solve(Collection<? extends Congruence> congruences) throws FactorException {
		// 1. Create
		// a) a copy of the congruences list, to avoid that the original list is modified during singleton removal.
		// b) a map from (primes with odd power) to congruences. A sorted TreeMap would be nice because then
		//    small primes get small indices in step 4, but experiments showed that HashMap is faster.
		//LOG.debug("#congruences = " + congruences.size());
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ArrayList<Congruence> congruencesCopy = new ArrayList(congruences.size());
		Map<Integer, ArrayList<Congruence>> oddExpFactors_2_congruences = new HashMap<Integer, ArrayList<Congruence>>();
		for (Congruence congruence : congruences) {
			congruencesCopy.add(congruence);
			for (Integer factor : congruence.getMatrixElements()) {
				ArrayList<Congruence> congruenceList = oddExpFactors_2_congruences.get(factor);
				if (congruenceList == null) {
					congruenceList = new ArrayList<Congruence>();
					oddExpFactors_2_congruences.put(factor, congruenceList);
				}
				congruenceList.add(congruence);
			}
		}
		// 2. remove singletons
		removeSingletons(congruencesCopy, oddExpFactors_2_congruences);
		// 3. Map odd-exp-elements to column indices. Sorting is not required.
		Map<Integer, Integer> factors_2_indices = createFactor2ColumnIndexMap(oddExpFactors_2_congruences);
		// 4+5. Create & solve matrix
		solve(congruencesCopy, factors_2_indices);
	}
	
	/**
	 * Remove singletons from <code>congruences</code>.
	 * This can reduce the size of the equation system; actually it never diminishes the difference (#eqs - #vars).
	 * It is very fast, too - like 60ms for a matrix for which solution via Gauss elimination takes 1 minute.
	 */
	protected void removeSingletons(List<Congruence> congruences, Map<Integer, ArrayList<Congruence>> oddExpFactors_2_congruences) {
		// Parse all congruences as long as we find a singleton in a complete pass
		boolean foundSingleton;
		do {
			foundSingleton = false;
			Iterator<? extends Congruence> congruenceIter = congruences.iterator();
			while (congruenceIter.hasNext()) {
				Congruence congruence = congruenceIter.next();
				Integer[] oddExpFactors = congruence.getMatrixElements();
				for (Integer oddExpFactor : oddExpFactors) {
					if (oddExpFactors_2_congruences.get(oddExpFactor).size()==1) {
						// found singleton -> remove from list
						//LOG.debug("Found singleton -> remove " + congruence);
						congruenceIter.remove();
						// remove from oddExpFactors_2_congruences so we can detect further singletons
						for (Integer factor : oddExpFactors) {
							ArrayList<Congruence> congruenceList = oddExpFactors_2_congruences.get(factor);
							congruenceList.remove(congruence);
							if (congruenceList.size()==0) {
								// there are no more congruences with the current factor
								oddExpFactors_2_congruences.remove(factor);
							}
						}
						foundSingleton = true;
						break;
					}
				}
			} // one pass finished
		} while (foundSingleton && congruences.size()>0);
		// now all singletons have been removed from congruences.
		//LOG.debug("#congruences after removing singletons: " + congruences.size());
	}
	
	/**
	 * Create a map from odd-exp-elements to matrix column indices.
	 * @param oddExpFactors_2_congruences unsorted map from factors to the congruences in which they appear with odd exponent
	 * @return
	 */
	protected Map<Integer, Integer> createFactor2ColumnIndexMap(Map<Integer, ArrayList<Congruence>> oddExpFactors_2_congruences) {
		int index = 0;
		Map<Integer, Integer> factors_2_columnIndices = new HashMap<Integer, Integer>();
		for (Integer factor : oddExpFactors_2_congruences.keySet()) {
			factors_2_columnIndices.put(factor, index++);
		}
		return factors_2_columnIndices;
	}

	/**
	 * Create the matrix from the pre-processed congruences and solve it.
	 * @param congruences
	 * @param factors_2_columnIndices map from factors to matrix column indices
	 * @throws FactorException 
	 */
	abstract protected void solve(List<Congruence> congruences, Map<Integer, Integer> factors_2_columnIndices) throws FactorException;
}
