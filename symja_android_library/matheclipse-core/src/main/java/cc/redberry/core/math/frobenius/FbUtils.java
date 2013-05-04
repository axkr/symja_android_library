/*
 * Redberry: symbolic tensor computations.
 *
 * Copyright (c) 2010-2012:
 *   Stanislav Poslavsky   <stvlpos@mail.ru>
 *   Bolotin Dmitriy       <bolotin.dmitriy@gmail.com>
 *
 * This file is part of Redberry.
 *
 * Redberry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Redberry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Redberry. If not, see <http://www.gnu.org/licenses/>.
 */
package cc.redberry.core.math.frobenius;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class FbUtils {
	public static final Comparator<int[]> SOLUTION_COMPARATOR = new Comparator<int[]>() {
		@Override
		public int compare(int[] o1, int[] o2) {
			if (o1.length != o2.length)
				throw new IllegalArgumentException();
			// int c = 0;
			for (int i = 0; i < o1.length; ++i) {
				if (o1[i] > o2[i]) {
					return 1;
				}
				if (o1[i] < o2[i]) {
					return -1;
				}
				// c = Integer.compare(o1[i], o2[i]);
				// if (c != 0)
				// return c;
			}
			return 0;
		}
	};

	public static List<int[]> getAllSolutions(final int[]... equations) {
		List<int[]> solutions = new ArrayList<int[]>();
		FrobeniusSolver fbSolver = new FrobeniusSolver(equations);
		int[] solution;
		while ((solution = fbSolver.take()) != null)
			solutions.add(solution);
		return solutions;
	}

	public static Iterator<int[]> iterator(int[][] equations) {
		return new SolutionsIterator(equations);
	}

	public static Iterable<int[]> iterable(int[][] equations) {
		return new SolutionsIterator(equations);
	}

	private static class SolutionsIterator implements Iterator<int[]>, Iterable<int[]> {
		private final FrobeniusSolver fbSolver;
		private int[] solution;

		SolutionsIterator(int[][] equations) {
			this.fbSolver = new FrobeniusSolver(equations);
		}

		@Override
		public boolean hasNext() {
			return (solution = fbSolver.take()) != null;
		}

		@Override
		public int[] next() {
			return solution;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported.");
		}

		@Override
		public Iterator<int[]> iterator() {
			return this;
		}
	}
}
