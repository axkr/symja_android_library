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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Integer partition generator, derived from fast multipartite number partition generator.
 * @author Tilman Neumann
 */
public class IntegerPartitionGenerator implements Generator<int[]> {

	private static final long serialVersionUID = -1077231419311209122L;

	private static final Logger LOG = LogManager.getLogger(IntegerPartitionGenerator.class);
	
	private static final boolean DEBUG = false;

	/** Internal class for stack elements. */
	private static class IntegerPartitionStackElem {
		private int rest;
		private int[] partitionPrefix;
		
		private IntegerPartitionStackElem(int[] prefix, int rest) {
			this.partitionPrefix = prefix;
			this.rest = rest;
		}
	}

	// stack behaviour is faster than queue behaviour,
	// and ArrayDeque is slightly faster than LinkedList than Stack (which extends Vector)
	private ArrayDeque<IntegerPartitionStackElem> stack = new ArrayDeque<IntegerPartitionStackElem>();
	private int maxStackSize = 0;
	
	/**
	 * Complete constructor for a generator of the partitions of n
	 * @param n
	 */
	public IntegerPartitionGenerator(int n) {
		if (DEBUG) LOG.debug("n = " + n);
		
		IntegerPartitionStackElem firstStackElem = new IntegerPartitionStackElem(new int[0], n);
		stack.push(firstStackElem);
	}

	/**
	 * @return true if there is another partition
	 */
	public boolean hasNext() {
		return (!stack.isEmpty());
	}
	
	/**
	 * Compute the next partition of the input.
	 * The result is an array of the parts, which is much faster than creating Multisets.
	 * The parts of a partition are sorted biggest part first;
	 * the order in which partitions appear is undefined.
	 *
	 * @return next partition
	 */
	public int[] next() {
		if (stack.size()>maxStackSize) {
			maxStackSize = stack.size();
		}
		IntegerPartitionStackElem stackElem = stack.pop();
		if (DEBUG) LOG.debug("POP prefix=" + Arrays.asList(stackElem.partitionPrefix) + ", rest=" + stackElem.rest);
		
		// rest will be the biggest of all parts when the recursion ends
		int rest = stackElem.rest;
		
		// prefix is a list of the other parts sorted biggest first
		int[] prefix = stackElem.partitionPrefix;
		int prefixSize = prefix.length;
		
		// determine max next part
		int maxNextPart = (prefixSize>0) ? Math.min(rest-prefix[0], prefix[prefixSize-1]) : (rest>>1);
		
		// create next parts
		for (int part=1; part<=maxNextPart; part++) {
			int[] newPrefix = new int[prefixSize+1];
			System.arraycopy(prefix, 0, newPrefix, 0, prefixSize);
			newPrefix[prefixSize] = part; // next part
			// new rest is (rest-next part), the complement of next part
			if (DEBUG) LOG.debug("PUSH rest=" + rest + ", part=" + part + " -> newRest=" + (rest-part) + ", newPrefix=" + Arrays.toString(newPrefix));
			stack.push(new IntegerPartitionStackElem(newPrefix, rest-part));
		}
		// prepare result: array is much, much faster than Multiset !
		int[] result = new int[prefixSize+1];
		result[0] = rest; // biggest part
		System.arraycopy(prefix, 0, result, 1, prefixSize);
		if (DEBUG) LOG.debug("RETURN " + Arrays.toString(result));
		return result;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}
	
	/**
	 * Computes the partitions of the given number.
	 * This is much slower than iterating over the results of the next() method and may give
	 * memory problems for big arguments, but the result is correctly ordered and has nice String output.
	 * 
	 * @param n
	 * @return partitions = SortedSet<IntegerPartition>,
	 * partition = IntegerPartition,
	 * part = Integer
	 */
	public static SortedSet<IntegerPartition> partitionsOf(int n) {
		SortedSet<IntegerPartition> partitions = new TreeSet<IntegerPartition>(Collections.reverseOrder());
		IntegerPartitionGenerator partGen = new IntegerPartitionGenerator(n);
		while (partGen.hasNext()) {
			int[] flatPartition = partGen.next();
			IntegerPartition expPartition = new IntegerPartition(flatPartition);
			partitions.add(expPartition);
		}
		if (DEBUG) LOG.debug("maxStackSize = " + partGen.maxStackSize);
		return partitions;
	}

	public static long numberOfPartitionsOf(int n) {
		IntegerPartitionGenerator partGen = new IntegerPartitionGenerator(n);
		long count = 0;
		while (partGen.hasNext()) {
			partGen.next();
			count++;
		}
		return count;
	}
}
