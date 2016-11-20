/*
 * @(#)Family.java
 */
package examples;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Example obtained from "Finite Domain Constraint Programming in Oz"
 * 
 * Maria and Clara are both heads of households, and both families have three
 * boys and three girls. Neither family includes any children closer in age than
 * one year, and all children are under age 10. The youngest child in Maria's
 * family is a girl, and Clara has just given birth to a little girl.
 * 
 * In each family, the sum of the ages of the boys equals the sum of the ages of
 * the girls, and the sum of the squares of the ages of the boys equals the sum
 * of the the squares of ages of the girls. The sum of the ages of all children
 * is 60.
 * 
 * What are the ages of the children in each family?
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Family {

	private static IntVariable sum(IntVariable[][] v) {
		IntVariable sum = null;
		for (int i = 0; i < v.length; i++) {
			for (int j = 0; j < v[i].length; j++) {
				sum = (sum == null) ? v[i][j] : sum.add(v[i][j]);
			}
		}
		return sum;
	}

	private static IntVariable sum(IntVariable[] v) {
		IntVariable sum = null;
		for (int i = 0; i < v.length; i++) {
			sum = (sum == null) ? v[i] : sum.add(v[i]);
		}
		return sum;
	}

	private static IntVariable sum2(IntVariable[] v) {
		IntVariable sum = null;
		for (int i = 0; i < v.length; i++) {
			IntVariable x = v[i].multiply(v[i]);
			sum = (sum == null) ? x : sum.add(x);
		}
		return sum;
	}

	public static void solve() {
		int FAMILIES = 2;
		int CHILDREN = 6;
		int maxAge = 9;

		Network net = new Network();

		IntVariable[][] isBoy = new IntVariable[FAMILIES][CHILDREN];
		IntVariable[][] age = new IntVariable[FAMILIES][CHILDREN];
		IntVariable[][] boyAge = new IntVariable[FAMILIES][CHILDREN];
		IntVariable[][] girlAge = new IntVariable[FAMILIES][CHILDREN];

		for (int family = 0; family < FAMILIES; family++) {
			for (int child = 0; child < CHILDREN; child++) {
				isBoy[family][child] = new IntVariable(net, 0, 1);
				age[family][child] = new IntVariable(net, 0, maxAge);
				if (child > 0)
					age[family][child].gt(age[family][child - 1]);
				boyAge[family][child] = age[family][child]
						.multiply(isBoy[family][child]);
				girlAge[family][child] = age[family][child]
						.subtract(boyAge[family][child]);
			}
		}

		isBoy[0][0].equals(0);
		isBoy[1][0].equals(0);
		age[1][0].equals(0);

		for (int family = 0; family < FAMILIES; family++) {
			sum(isBoy[family]).equals(3);
			sum(boyAge[family]).equals(sum(girlAge[family]));
			sum2(boyAge[family]).equals(sum2(girlAge[family]));
		}
		sum(age).equals(60);

		Solver solver = new DefaultSolver(net);
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			for (int family = 0; family < FAMILIES; family++) {
				System.out.print("Family " + family + ": ");
				for (int child = 0; child < CHILDREN; child++) {
					int _isBoy = solution.getIntValue(isBoy[family][child]);
					int _age = solution.getIntValue(age[family][child]);
					System.out.print(_isBoy != 0 ? "Boy  " : "Girl ");
					System.out.print(_age + "  ");
				}
				System.out.println();
			}
		}
	}

	public static void main(String[] args) {
		solve();
	}
}
