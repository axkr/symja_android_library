package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.ExprComparator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Static methods for evaluating <code>ISymbol.FLAT, ISymbol.LISTABLE</code> and <code>ISymbol.ORDERLESS</code> attributes.
 *
 * @see org.matheclipse.core.interfaces.ISymbol#FLAT
 * @see org.matheclipse.core.interfaces.ISymbol#LISTABLE
 * @see org.matheclipse.core.interfaces.ISymbol#ORDERLESS
 */
public class EvalAttributes {

	private EvalAttributes() {

	}

	/**
	 * Flatten the list (i.e. typically the ASTs head has the attribute ISymbol.FLAT) example: suppose the head f should be
	 * flattened out: <code>f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]</code>
	 * 
	 * @param ast
	 *            the <code>AST</code> whose elements should be flattened.
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
	 * Flatten the list (i.e. the ASTs head element has the same head) example: suppose the head f should be flattened out:<br>
	 * <code>f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]</code>
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
	 * Flatten the list (i.e. the ASTs head element has the same head) element has the same head) example: suppose the head f should
	 * be flattened out:<br>
	 * <code>f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]</code>
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
	 * Sort the list (i.e. typically the ASTs head has the attribute ISymbol.ORDERLESS) example: suppose the Symbol f has the
	 * attribute ISymbol.ORDERLESS <code>f[z,d,a,b] ==> f[a,b,d,z]</code>
	 * 
	 * @param ast
	 *            the AST will be sorted in place.
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
				if (ast.arg1().compareTo(ast.arg2()) > 0) {
					// swap arguments
					temp = ast.arg2();
					ast.set(2, ast.arg1());
					ast.set(1, temp);
				}
				if (ast.arg2().compareTo(ast.arg3()) > 0) {
					// swap arguments
					temp = ast.arg3();
					ast.set(3, ast.arg2());
					ast.set(2, temp);
					if (ast.arg1().compareTo(ast.arg2()) > 0) {
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

	public final static void sort(final IAST ast, ExprComparator comparator) {
		ast.args().sort(comparator);
	}

	/**
	 * Thread through all (sub-)lists in the arguments of the IAST (i.e. typically the ASTs head has the attribute ISymbol.LISTABLE)
	 * example: <code>Sin[{2,x,Pi}] ==> {Sin[2],Sin[x],Sin[Pi]}<code>
	 * 
	 * @param ast
	 * @param listHead
	 *            the lists head (typically <code>F.List</code>)
	 * @param argHead
	 *            the arguments head (typically <code>ast.head()</code>)
	 * @param listLength
	 *            the length of the list
	 * @return the resulting ast with the <code>argHead</code> threaded into each ast argument.
	 */
	public static IAST threadList(final IAST ast, final IExpr listHead, final IExpr argHead, final int listLength) {

		final IAST result = F.ast(listHead, listLength, true);
		final int listSize = ast.size();
		for (int j = 1; j < listLength + 1; j++) {
			final IAST subResult = F.ast(argHead, listSize - 1, true);

			for (int i = 1; i < listSize; i++) {
				if (ast.get(i).isAST(listHead)) {
					final IAST arg = (IAST) ast.get(i);
					subResult.set(i, arg.get(j));
				} else {
					subResult.set(i, ast.get(i));
				}
			}

			result.set(j, subResult);
		}

		return result;
	}
}
