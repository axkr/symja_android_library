package org.matheclipse.core.list.algorithms;

import java.util.Collections;
import java.util.Comparator;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 */
public class EvaluationSupport {

	/**
	 * Flatten the list [i.e. the list getHeader() has the attribute
	 * ISymbol.FLAT] example: suppose the Symbol f has the attribute
	 * ISymbol.FLAT f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]
	 * 
	 * @param ast
	 * 
	 * @return returns the flattened list
	 */
	public static IAST flatten(final IAST ast) {
		if ((ast.getEvalFlags() & IAST.IS_FLATTENED) == IAST.IS_FLATTENED) {
			// already flattened
			return null;
		}
		final ISymbol sym = ast.topHead();
		if (ast.isAST(sym)) {
			final IAST res = F.ast(sym);

			if (flatten(sym, ast, res)) {
				res.setEvalFlags(IAST.IS_FLATTENED);
				return res;
			}
		}
		ast.setEvalFlags(IAST.IS_FLATTENED);
		return null;
	}

	/**
	 * Flatten the list [i.e. the lists <code>get(0)</code> element has the same
	 * head] example: suppose the head f should be flattened out:<br>
	 * f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]
	 * 
	 * @param argList
	 * @return <code>true</code> if a sublist was flattened out
	 */
	public static boolean flatten(final ISymbol head, final IAST argList, final IAST resultList) {
		boolean isEvaled = false;
		IAST list;
		final int astSize = argList.size();
		for (int i = 1; i < astSize; i++) {
			if (argList.get(i).isAST()) {
				list = (IAST) argList.get(i);
				if (list.isAST(head)) {
					isEvaled = true;
					flatten(head, list, resultList);
				} else {
					resultList.add(list);
				}
			} else {
				resultList.add(argList.get(i));
			}
		}
		return isEvaled;
	}

	/**
	 * Sort the list [i.e. the list getHeader() has the attribute
	 * ISymbol.ORDERLESS] example: suppose the Symbol s has the attribute
	 * ISymbol.ORDERLESS f[z,d,a,b] ==> f[a,b,d,z]
	 * 
	 * @param session
	 * @param list
	 * @return returns the sorted list
	 */
	public final static void sort(final IAST ast) {
		if ((ast.getEvalFlags() & IAST.IS_SORTED) == IAST.IS_SORTED) {
			return;
		}
		
		final int astSize = ast.size();
		if (astSize > 2) {
			IExpr temp;
			switch (astSize) {
			case 3:
				if (ast.get(1).compareTo(ast.get(2)) > 0) {
					// swap arguments
					temp = ast.get(2);
					ast.set(2, ast.get(1));
					ast.set(1, temp);
				}
				break;
			case 4:
				// http://stackoverflow.com/questions/4793251/sorting-int-array-with-only-3-elements
				if (ast.get(1).compareTo(ast.get(2)) > 0) {
					// swap arguments
					temp = ast.get(2);
					ast.set(2, ast.get(1));
					ast.set(1, temp);
				}
				if (ast.get(2).compareTo(ast.get(3)) > 0) {
					// swap arguments
					temp = ast.get(3);
					ast.set(3, ast.get(2));
					ast.set(2, temp);
					if (ast.get(1).compareTo(ast.get(2)) > 0) {
						// swap arguments
						temp = ast.get(2);
						ast.set(2, ast.get(1));
						ast.set(1, temp);
					}
				}
				break;
			default:
				ast.args().sort(ExprComparator.CONS);
			}
		}
		ast.setEvalFlags(IAST.IS_SORTED);
	}

	public final static void sort(final IAST list, Comparator<IExpr> comparator) {
		list.args().sort(comparator);
	}

	/**
	 * Thread through all lists in the arguments of the IAST [i.e. the list
	 * header has the attribute ISymbol.LISTABLE] example: Sin[{2,x,Pi}] ==>
	 * {Sin[2],Sin[x],Sin[Pi]}
	 * 
	 * @param list
	 * @param listLength
	 *            the length of the list
	 * 
	 * 
	 * @return Description of the Returned Value
	 */
	public static IAST threadList(final IAST list, final int listLength, final int headOffset) {

		final IAST res0 = F.ast(F.List, listLength, true);
		final int listSize = list.size();
		for (int j = headOffset; j < listLength + headOffset; j++) {
			final IAST res1 = F.ast(list.head(), listSize - headOffset, true);

			for (int i = headOffset; i < listSize; i++) {
				if (list.get(i).isList()) {
					final IAST arg = (IAST) list.get(i);
					res1.set(i, arg.get(j));
				} else {
					res1.set(i, list.get(i));
				}
			}

			res0.set(j, res1);
		}

		return res0;
	}
}
