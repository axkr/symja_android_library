package org.matheclipse.core.list.algorithms;

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
	 * Flatten the list [i.e. the list getHeader() has the attribute ISymbol.FLAT] example: suppose the Symbol f has the attribute
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
			final IAST result = ast.copyHead();
			if (flatten(sym, ast, result)) {
				result.addEvalFlags(IAST.IS_FLATTENED);
				return result;
			}
		}
		ast.setEvalFlags(IAST.IS_FLATTENED);
		return null;
	}

	/**
	 * Flatten the list (i.e. the lists <code>get(0)</code> element has the same head) example: suppose the head f should be
	 * flattened out:<br>
	 * f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]
	 * 
	 * @param head
	 *            the head of the expression, which should be flattened.
	 * @param sublist
	 *            the <code>sublist</code> which should be added to the <code>result</code> list.
	 * @param result
	 *            the <code>result</code> list, where all sublist elements with the same <code>head</code> should be appended.
	 * @return <code>true</code> if a sublist was flattened out into the <code>result</code> list.
	 */
	public static boolean flatten(final ISymbol head, final IAST sublist, final IAST result) {
		boolean isEvaled = false;
		IExpr expr;
		final int astSize = sublist.size();
		for (int i = 1; i < astSize; i++) {
			expr = sublist.get(i);
			if (expr.isAST(head)) {
				isEvaled = true;
				flatten(head, (IAST) expr, result);
			} else {
				result.add(expr);
			}
		}
		return isEvaled;
	}

	/**
	 * Flatten the list (i.e. the lists <code>get(0)</code> element has the same head) example: suppose the head f should be
	 * flattened out:<br>
	 * f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]
	 * 
	 * @param head
	 *            the head of the expression, which should be flattened.
	 * @param sublist
	 *            the <code>sublist</code> which should be added to the <code>result</code> list.
	 * @param result
	 *            the <code>result</code> list, where all sublist elements with the same <code>head</code> should be appended.
	 * @param level
	 *            the recursion level up to which the list should be flattened
	 * @return <code>true</code> if a sublist was flattened out into the <code>result</code> list.
	 */
	public static boolean flatten(final ISymbol head, final IAST sublist, final IAST result, final int recursionCounter,
			final int level) {
		boolean isEvaled = false;
		IExpr expr;
		final int astSize = sublist.size();
		for (int i = 1; i < astSize; i++) {
			expr = sublist.get(i);
			if (expr.isAST(head) && recursionCounter < level) {
				isEvaled = true;
				flatten(head, (IAST) expr, result, recursionCounter + 1, level);
			} else {
				result.add(expr);
			}
		}
		return isEvaled;
	}

	/**
	 * Sort the list [i.e. the list getHeader() has the attribute ISymbol.ORDERLESS] example: suppose the Symbol s has the attribute
	 * ISymbol.ORDERLESS f[z,d,a,b] ==> f[a,b,d,z]
	 * 
	 * @param session
	 * @param list
	 * @return returns the sorted list
	 */
	public final static void sortTimesPlus(final IExpr expr) {
		if (expr.isTimes() || expr.isPlus()) {
			sort((IAST)expr);
		}
	}

	/**
	 * Sort the list [i.e. the list getHeader() has the attribute ISymbol.ORDERLESS] example: suppose the Symbol s has the attribute
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
				if (ast.arg1().compareTo(ast.arg2()) > 0) {
					// swap arguments
					temp = ast.arg2();
					ast.set(2, ast.arg1());
					ast.set(1, temp);
				}
				break;
			case 4:
				// http://stackoverflow.com/questions/4793251/sorting-int-array-with-only-3-elements
				if (ast.get(1).compareTo(ast.arg2()) > 0) {
					// swap arguments
					temp = ast.arg2();
					ast.set(2, ast.arg1());
					ast.set(1, temp);
				}
				if (ast.arg2().compareTo(ast.arg3()) > 0) {
					// swap arguments
					temp = ast.arg3();
					ast.set(3, ast.get(2));
					ast.set(2, temp);
					if (ast.get(1).compareTo(ast.arg2()) > 0) {
						// swap arguments
						temp = ast.arg2();
						ast.set(2, ast.arg1());
						ast.set(1, temp);
					}
				}
				break;
			default:
				ast.args().sort(ExprComparator.CONS);
			}
		}
		ast.addEvalFlags(IAST.IS_SORTED);
	}

	public final static void sort(final IAST list, ExprComparator comparator) {
		list.args().sort(comparator);
	}

	/**
	 * Thread through all lists in the arguments of the IAST [i.e. the list header has the attribute ISymbol.LISTABLE] example:
	 * Sin[{2,x,Pi}] ==> {Sin[2],Sin[x],Sin[Pi]}
	 * 
	 * @param list
	 * @param listLength
	 *            the length of the list
	 * 
	 * 
	 * @return Description of the Returned Value
	 */
	public static IAST threadList(final IAST list, final IExpr head, final IExpr mapHead, final int listLength, final int headOffset) {

		final IAST result = F.ast(head, listLength, true);
		final int listSize = list.size();
		for (int j = headOffset; j < listLength + headOffset; j++) {
			final IAST subResult = F.ast(mapHead, listSize - headOffset, true);

			for (int i = headOffset; i < listSize; i++) {
				if (list.get(i).isAST(head)) {
					final IAST arg = (IAST) list.get(i);
					subResult.set(i, arg.get(j));
				} else {
					subResult.set(i, list.get(i));
				}
			}

			result.set(j, subResult);
		}

		return result;
	}
}
