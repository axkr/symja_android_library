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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.base.IntCollectionUtil;

/**
 * int[] implementation of a multipartite number [n_1, n_2, ..., n_k] like [1,3,4,2,0,1].
 * Each n_i signifies a count of things of some type i.
 * Thus in theory the order of the n_i should not make any difference.
 * However, this class implements an ordering between MPIs where the first element that differs decides, i.e. [2,0,0] is bigger than [1,2,3],
 * which is required by the MpiPartitionGenerator to be able to enumerate partitions in an efficient way.
 * 
 * For other purposes it might be convenient to overwrite <code>compareTo()</code> in a subclass.
 * 
 * @author Tilman Neumann
 */
public class Mpi_IntegerArrayImpl implements Mpi {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Mpi_IntegerArrayImpl.class);
	
	int[] values;
	
	/**
	 * Constructor for zero-initialized mpi with dim entries.
	 * @param dim
	 */
	public Mpi_IntegerArrayImpl(int dim) {
		this.values = new int[dim];
	}

	/**
	 * Constructor from element array, with element copy.
	 * @param values
	 */
	public Mpi_IntegerArrayImpl(int[] values) {
		this.values = new int[values.length];
		for (int i=0; i<values.length; i++) {
			this.values[i] = values[i];
		}
	}
	
	/**
	 * Constructor from value collection.
	 * @param values
	 */
	public Mpi_IntegerArrayImpl(Collection<Integer> values) {
		this.values = new int[values.size()];
		int i=0;
		for (Integer value : values) {
			this.values[i] = value;
			i++;
		}
	}
	
	/**
	 * Copy constructor.
	 * @param original
	 */
	public Mpi_IntegerArrayImpl(Mpi original) {
		this(original.getDim());
		Iterator<Integer> valueIter = original.iterator();
		int i=0;
		while (valueIter.hasNext()) {
			values[i] = valueIter.next().intValue();
			i++;
		}
	}
	
	/**
	 * Constructor from a comma-separated string of values.
	 * @param str
	 */
	public Mpi_IntegerArrayImpl(String str) {
		this(IntCollectionUtil.stringToList(str));
	}

	public int getDim() {
		return values.length;
	}

	public int getCardinality() {
		int elemSum = 0;
		for (int elem : values) {
			elemSum += elem;
		}
		return elemSum;
	}

	public int getElem(int index) {
		return values[index];
	}

	public void setElem(int index, int value) {
		values[index] = value;
	}

	public int firstNonZeroPartIndex() {
		int dim = values.length;
		for (int i=0; i<dim; i++) {
			if (values[i]>0) return i;
		}
		return -1; // value is (0,0, .., 0)
	}

	public Mpi[] subtract(Mpi other) {
		int dim = values.length;
		Mpi_IntegerArrayImpl lower = new Mpi_IntegerArrayImpl(dim);
		Mpi_IntegerArrayImpl upper = new Mpi_IntegerArrayImpl(dim);
		int lastNonNull = -1;
		for (int i=0; i<dim; i++) {
			int myValue = values[i];
			int otherValue = other.getElem(i);
			int diff = myValue-otherValue;
			if (diff > 0) {
				lower.values[i] = diff;
				upper.values[i] = diff;
				lastNonNull = i; // the last non-null value before the first negative diff
			} else if (diff < 0) {
				// new element difference is the first negative one.
				// - upper is finished, all remaining entries are set to zero.
				// - for lower we have to decrement entry[lastNonNull] and set all the following
				//   entries to those of this.
				if (lastNonNull > -1) {
					// decrement the last non null value before the negative one:
					lower.values[lastNonNull]--;
					// set all the other entries to those of this.
					for (int j=lastNonNull+1; j<dim; j++) {
						lower.values[j] = values[j];
					}
				}
				// else we had a negative value but all entries before are zero. so what is lower?
				// e.g. [0,0,0,0, -1, ???) -> I think the result must be the null vector.
				// (which has already been achieved :)
				break;
			} // else diff==0 -> entries remain 0
		}
		return new Mpi[] {lower, upper};
	}
	
	public Mpi complement(Mpi other) {
		int dim = values.length;
		Mpi_IntegerArrayImpl result = new Mpi_IntegerArrayImpl(dim);
		Iterator<Integer> otherIter = other.iterator();
		for (int i=0; i<dim ; i++) {
			Integer otherElem = otherIter.next();
			int otherValue = (otherElem!=null) ? otherElem.intValue() : 0;
			result.values[i] = values[i]-otherValue;
		}
		return result;
	}

	// see handwritten notes 2010-11-08
	public Mpi[] div2() {
		int dim = values.length;
		Mpi_IntegerArrayImpl lower = new Mpi_IntegerArrayImpl(dim); // zero-init
		Mpi_IntegerArrayImpl upper = new Mpi_IntegerArrayImpl(dim); // zero-init
		boolean isEven = true;
		for (int index = 0; index<dim; index++) {
			int iValue = values[index];
			if (isEven) {
				if (iValue%2 == 0) {
					lower.values[index] = iValue>>1;
					upper.values[index] = iValue>>1;
				} else {
					// new value is the first odd one
					lower.values[index] = iValue>>1; // round down
					upper.values[index] = (iValue+1)>>1; // round up
					isEven = false;
				}
			} else {
				// some entry before was odd...
				lower.values[index] = iValue;
				// upper[index] remains 0
			}
		}
		return new Mpi[] {lower, upper};
	}

	public Mpi maxNextPart(Mpi firstPart, Mpi lastPart) {
		// compute lower of this-firstPart, check on the run if lastPart is bigger

		// start inline of divide method...
		int dim = values.length;
		Mpi_IntegerArrayImpl restMinusFirstPart = new Mpi_IntegerArrayImpl(dim);
		int lastNonNull = -1;
		boolean lastPartSmaller = true;
		for (int i=0; i<dim; i++) {
			int diff = values[i] - firstPart.getElem(i);
			if (lastPartSmaller) {
				int lastPartValue = lastPart.getElem(i);
				if (lastPartValue<diff) return lastPart;
				if (lastPartValue>diff) lastPartSmaller = false;
			}
			if (diff > 0) {
				restMinusFirstPart.values[i] = diff;
				lastNonNull = i; // the last non-null value before the first negative diff
			} else if (diff < 0) {
				// new element difference is the first negative one
				if (lastNonNull > -1) {
					// decrement the last non null value before the negative one:
					restMinusFirstPart.values[lastNonNull]--;
					// set all the other entries to those of this.
					for (int j=lastNonNull+1; j<dim; j++) {
						restMinusFirstPart.values[j] = values[j];
					}
				}
				// else we had a negative value but all entries before are zero. so what is lower?
				// e.g. [0,0,0,0, -1, ???) -> I think the result must be the null vector.
				// (which has already been achieved :)
				break;
			} // else diff==0 -> entries remain 0
		}
		// end inline of divide method...
		return restMinusFirstPart;
	}
	
//	public Mpi maxNextPart(Mpi firstPart, Mpi lastPart) {
//		Mpi restMinusFirstPart = this.subtract(firstPart)[0]; // get lower
//		return (lastPart.compareTo(restMinusFirstPart)<0) ? lastPart : restMinusFirstPart;
//	}

	/**
	 * {@inheritDoc}
	 * In this implementation, the first element that differs decides, i.e. [2,0,0] is bigger than [1,2,3].
	 * This ordering is required by the MpiPartitionGenerator in order to enumerate partitions in an efficient way.
	 * For other purposes it might be convenient to overwrite this implementation in a subclass.

	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(Mpi other) {
		int myDim = values.length;
		if (other == null) {
			return myDim; // >0 if this has some elements, =0 (equals) if not
		}
		int otherDim = other.getDim();
		int minDim = Math.min(myDim, otherDim);
		for (int i=0; i<minDim; i++) {
			int myElem = values[i];
			int otherElem = other.getElem(i);
			if (myElem != otherElem) {
				return myElem - otherElem;
			}
		}
		return myDim - otherDim;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(values);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mpi_IntegerArrayImpl other = (Mpi_IntegerArrayImpl) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String ret = "[";
		for (int i=0; i<values.length; i++) {
			if (i>0) ret += ", ";
			ret += values[i];
		}
		ret += "]";
		return ret;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new ElemIterator();
	}
	
	class ElemIterator implements Iterator<Integer> {

		private int idx=0;
		
		@Override
		public boolean hasNext() {
			return idx<values.length;
		}

		@Override
		public Integer next() {
			return Integer.valueOf(values[idx++]);
		}

		@Override
		public void remove() {
			throw new IllegalStateException("remove not supported yet");
		}
	}
}
