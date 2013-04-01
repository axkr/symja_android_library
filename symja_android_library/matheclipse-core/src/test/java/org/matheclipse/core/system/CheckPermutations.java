/*
 *
 */
package org.matheclipse.core.system;

import org.matheclipse.core.expression.AST;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.generic.combinatoric.KPermutationsIterable;

/**
 *
 */
public class CheckPermutations {
	public static void main(String args[]) {
		AST f = AST.newInstance(null);
		f.setHeader(new Symbol("List"));
		f.add(new Symbol("a"));
		f.add(new Symbol("b"));
		f.add(new Symbol("c"));
		f.add(new Symbol("d"));
		int k = 4;
		// KPermutationsIterator perm = new KPermutationsIterator(f, k, 1);
		KPermutationsIterable perm = new KPermutationsIterable(f, k, 1);
		// int j[];
		IAST temp;
		IAST result = AST.newInstance(null);
		result.setHeader(new Symbol("List"));
		// while ((j = (int[]) perm.nextElement()) != null) {
		for (int[] j : perm) {
			if (j == null) {
				break;
			}
			temp = AST.newInstance(null);
			temp.setHeader(new Symbol("p"));
			for (int i = 0; i < k; i++) {
				temp.add(f.get(j[i] + 1));
			}
			result.add(temp);
		}
		System.out.println(result);
	}
}
