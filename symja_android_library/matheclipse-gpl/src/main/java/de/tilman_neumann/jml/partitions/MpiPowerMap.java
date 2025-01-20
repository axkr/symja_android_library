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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.util.Multiset;
import de.tilman_neumann.util.Multiset_HashMapImpl;

/**
 * A map from all "subvalues" s of a multipartite number q with 1<s<=q (with regard to the "partwise comparison")
 * to a TreeMap of all "subvalues" t with 0<t<s (with regard to the "ordering comparison") mapped to their complements s-t.
 * 
 * Example: The power map for q=[5] is
 * {2={1=1}, 
 *  3={1=2, 2=1},
 *  4={1=3, 2=2, 3=1}, 
 *  5={1=4, 2=3, 3=2, 4=1}
 * }
 *
 * @author Tilman Neumann
 */
public class MpiPowerMap extends HashMap<Mpi, TreeMap<Mpi, Mpi>>{

	private static final long serialVersionUID = 8022823673465565644L;
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(MpiPowerMap.class);

	private static boolean logAccesses = false;
	private int dim;
	private Map<Mpi, Multiset<Mpi>> accessLog;
	
	/**
	 * 
	 * @param dim dimension of Mpi's
	 */
	private MpiPowerMap(int dim) {
		super();
		this.dim = dim;
		if (logAccesses) {
			this.accessLog = new HashMap<Mpi, Multiset<Mpi>>();
		}
	}
	
	public static MpiPowerMap create(Mpi q) {
		//long start = System.currentTimeMillis();
		MpiPowerMap result = new MpiPowerMap(q.getDim());
		if (q.getCardinality()>1) {
			result.addSubvalues(q);
		} // else no smaller elements
		//LOG.debug("time to compute power map of " + q + " = " + (System.currentTimeMillis()-start) + "ms");
		return result;
	}
	
	/**
	 * Add the exclusive subvalue-sets of r.
	 * @param r
	 * @return subvalues of r, exclusive.
	 */
	private TreeMap<Mpi, Mpi> addSubvalues(Mpi r) {
		TreeMap<Mpi, Mpi> result = super.get(r); // avoid access log
		if (result==null) {
			result = new TreeMap<Mpi, Mpi>();
			
			// end of recursion
			if (r.getCardinality()==2) {
				for (int idx=0; idx < dim; idx++) {
					// change the element at the given index
					int elemValue = r.getElem(idx);
					if (elemValue>0) {
						Mpi_IntegerArrayImpl subvalue = new Mpi_IntegerArrayImpl(r); 
						subvalue.values[idx]--;
						Mpi complement = r.complement(subvalue);
						result.put(subvalue, complement);
						//LOG.debug("r="+r + ", subvalue="+subvalue + "->complement="+complement);
					}
				}
				//LOG.debug("powermap of " + r + " = " + result);
			} else {
				// r has smaller elements...
				for (int idx=0; idx < dim; idx++) {
					// change the element at the given index
					int elemValue = r.getElem(idx);
					if (elemValue>0) {
						Mpi_IntegerArrayImpl subvalue = new Mpi_IntegerArrayImpl(r); 
						subvalue.values[idx]--;
						TreeMap<Mpi, Mpi> submpis = this.addSubvalues(subvalue);
						for (Map.Entry<Mpi, Mpi> subentry : submpis.entrySet()) {
							Mpi submpi = subentry.getKey();
							result.put(submpi, r.complement(submpi));
						}
						Mpi complement = r.complement(subvalue);
						result.put(subvalue, complement);
						//LOG.debug("r="+r + ", subvalue="+subvalue + "->complement="+complement);
					}
				}
			}
			//LOG.debug("powermap of " + r + " = " + result);
			this.put(r, result);
		} // else: already computed
		
		return result;
	}
	
	/**
	 * Delivers all subvalues (piece-wise relation) of x not bigger (ordering relation) than biggestElem.
	 * @param x
	 * @param biggestElem
	 * @return subvalues of x not bigger than biggestElem
	 */
	public SortedMap<Mpi, Mpi> getSubvaluesLessOrEqual(Mpi x, Mpi biggestElem) {
		if (logAccesses) {
			Multiset<Mpi> biggestElems = accessLog.get(x);
			if (biggestElems==null) {
				biggestElems = new Multiset_HashMapImpl<Mpi>();
			}
			biggestElems.add(biggestElem);
			accessLog.put(x, biggestElems);
		}
		TreeMap<Mpi, Mpi> subvaluesAndComplements = super.get(x);
		//LOG.debug("subvaluesAndComplements(" + x + ") = " + subvaluesAndComplements);
		return subvaluesAndComplements.headMap(biggestElem, true);
	}
	
	public String accessStats() {
		if (logAccesses) {
			String str = "Power map element access:\n";
			int total = 0;
			for (Mpi mpi : this.keySet()) {
				Multiset<Mpi> entry = accessLog.get(mpi);
				int count = (entry!=null) ? entry.totalCount() : 0;
				total += count;
				str += count + " * " + mpi + ": " + entry + "\n";
			}
			str += "Total number: " + total + "\n";
			return str;
		}
		return "access logging is turned off...";
	}
}
