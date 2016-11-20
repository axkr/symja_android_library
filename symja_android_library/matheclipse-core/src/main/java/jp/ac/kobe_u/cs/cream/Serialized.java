/*
 * @(#)Serialized.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Serialized constraints
 * constructed from
 * an array of <tt>n</tt> integer variables (<tt>v[0]</tt>, ..., <tt>v[n-1]</tt>) and
 * an array of <tt>n</tt> positive integer constants (<tt>a[0]</tt>, ..., <tt>a[n-1]</tt>).
 * This constraint means
 * <tt>v[i]+a[i] &lt;= v[j]</tt> or <tt>v[j]+a[j] &lt;= v[i]</tt>
 * for each pair of <tt>i</tt> and <tt>j</tt>. 
 * 
 * @see Sequential
 * @see LocalSearch
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Serialized extends Constraint {
	private Variable[] v;

	private int[] a;

	private int[] order;

	/**
	 * Adds a serialized constraint to the network.
	 * The length of two arrays should be the same.
	 * All elements of <tt>a</tt> should be positive.
	 * @param net the network
	 * @param v the array of integer variables
	 * @param a the array of positive integer constants
	 */
	public Serialized(Network net, Variable[] v, int[] a) {
		super(net);
		this.v = v.clone();
		this.a = a.clone();
		order = null;
	}

	public Constraint copy(Network net) {
		return new Serialized(net, Constraint.copy(v, net), a);
	}

	/**
	 * This class represents a condition on which the order of variables are chosen
	 * for this serialized constraint in the solution.
	 * The condition will be modified by a {@linkplain Serialized.Swap swap operation}
	 * to find a neighbor solution in the {@link LocalSearch} solver.
	 * 
	 * @see LocalSearch
	 * @since 1.0
	 * @version 1.4
	 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
	 */
	public class SerializedCondition extends Condition {
		private int[][] code;

		/**
		 * Constructs a condition for the serialized constraint
		 * under the current solution
		 * by extracting the order of variables.
		 */
		public SerializedCondition() {
			index = Serialized.this.getIndex();
			code = new int[v.length][3];
			for (int i = 0; i < code.length; i++) {
				Domain d = v[i].getDomain();
				code[i][0] = i;
				code[i][1] = ((IntDomain) d).value();
				code[i][2] = a[i];
			}
			Comparator<int[]> comp = new Comparator<int[]>() {
				public int compare(int[] p1, int[] p2) {
					int k1 = p1[1];
					int k2 = p2[1];
					return (k1 < k2) ? -1 : (k1 == k2) ? 0 : 1;
				}
			};
			Arrays.sort(code, comp);
		}

		public void setTo(Network network) {
			Serialized s = (Serialized) network.getConstraint(index);
			if (code == null) {
				s.order = null;
			} else {
				s.order = new int[code.length];
				for (int i = 0; i < s.order.length; i++) {
					s.order[i] = code[i][0];
				}
			}
		}

		public List<Operation> operations() {
			List<Operation> operations = new LinkedList<Operation>();
			for (int i = 0; i < code.length - 1; i++) {
				if (code[i][1] + code[i][2] == code[i + 1][1]) {
					// adjacent
					Operation op = new Swap(index, i, i + 1);
					operations.add(op);
				}
			}
			return operations;
		}
	}

	/**
	 * This class represents an operation of swapping an order of
	 * two variables.
	 * 
	 * @see LocalSearch
	 * @since 1.0
	 * @version 1.4
	 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
	 */
	public class Swap extends Operation {
		private int index;

		private int i;

		private int j;

		/**
		 * Constructs the swap operation exchanging the order of
		 * two variables <tt>v[i]</tt> and <tt>v[j]</tt>.
		 * 
		 * @param index index of the serialized constraint 
		 * @param i index of the first variable (<tt>v[i]</tt>)
		 * @param j index of the second variable (<tt>v[j]</tt>)
		 */
		public Swap(int index, int i, int j) {
			this.index = index;
			this.i = i;
			this.j = j;
		}

		public void applyTo(Network network) {
			Serialized s = (Serialized) network.getConstraint(index);
			int t = s.order[i];
			s.order[i] = s.order[j];
			s.order[j] = t;
		}

		public boolean isTaboo(Operation op) {
			if (!(op instanceof Swap))
				return false;
			Swap swap = (Swap) op;
			return index == swap.index && i == swap.i && j == swap.j;
		}
	}

	protected void clearCondition() {
		order = null;
	}

	protected Condition extractCondition() {
		return new SerializedCondition();
	}

	public boolean isModified() {
		return isModified(v);
	}

	private boolean satisfySequential(Trail trail) {
		if (order == null)
			return true;
		for (int k = 0; k < order.length - 1; k++) {
			int i = order[k];
			int j = order[k + 1];
			IntDomain d0 = (IntDomain) v[i].getDomain();
			IntDomain d1 = (IntDomain) v[j].getDomain();
			int diffMin = d1.max() - a[i] + 1;
			int diffMax = d0.min() + a[i] - 1;
			d0 = d0.delete(diffMin, IntDomain.MAX_VALUE);
			if (d0.isEmpty())
				return false;
			d1 = d1.delete(IntDomain.MIN_VALUE, diffMax);
			if (d1.isEmpty())
				return false;
			v[i].updateDomain(d0, trail);
			v[j].updateDomain(d1, trail);
		}
		return true;
	}

	private boolean satisfySerialized(Trail trail) {
		for (int i = 0; i < v.length; i++) {
			for (int j = 0; j < v.length; j++) {
				if (i == j)
					continue;
				IntDomain d0 = (IntDomain) v[i].getDomain();
				IntDomain d1 = (IntDomain) v[j].getDomain();
				int diffMin = d1.max() - a[i] + 1;
				int diffMax = d1.min() + a[j] - 1;
				if (diffMin <= diffMax) {
					d0 = d0.delete(diffMin, diffMax);
					if (d0.isEmpty())
						return false;
					v[i].updateDomain(d0, trail);
				}
			}
		}
		return true;
	}

	public boolean satisfy(Trail trail) {
		if (!satisfySequential(trail))
			return false;
		return satisfySerialized(trail);
	}

	public String toString() {
		return "Serialized(" + Constraint.toString(v) + ","
				+ Constraint.toString(a) + ")";
	}
}
