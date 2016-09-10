package org.matheclipse.core.reflection.system;

import java.util.Set;
import java.util.TreeSet;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTRange;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Union of two sets. See
 * <a href="http://en.wikipedia.org/wiki/Union_(set_theory)">Union (set
 * theory)</a>
 */
public class Union extends AbstractFunctionEvaluator {

	public Union() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		if (ast.isAST1() && ast.arg1().isAST()) {
			final IAST result = F.List();
			IAST arg1 = (IAST) ast.arg1();
			Set<IExpr> set = arg1.asSet();
			for (IExpr IExpr : set) {
				result.add(IExpr);
			}
			EvalAttributes.sort(result, Comparators.ExprComparator.CONS);
			return result;
		}

		if (ast.arg1().isAST() && ast.arg2().isAST()) {
			IAST arg1AST = ((IAST) ast.arg1());
			IAST arg2AST = ((IAST) ast.arg2());
			final IAST result = F.List();
			return union(arg1AST, arg2AST, result);
		}
		return F.NIL;
	}

	/**
	 * Create the (ordered) union from both ASTs.
	 * 
	 * @param ast1
	 *            first AST set
	 * @param ast2
	 *            second AST set
	 * @param result
	 *            the AST where the elements of the union should be appended
	 * @return
	 */
	public static IExpr union(IAST ast1, IAST ast2, final IAST result) {
		Set<IExpr> resultSet = new TreeSet<IExpr>();
		int size = ast1.size();
		for (int i = 1; i < size; i++) {
			resultSet.add(ast1.get(i));
		}
		size = ast2.size();
		for (int i = 1; i < size; i++) {
			resultSet.add(ast2.get(i));
		}
		for (IExpr expr : resultSet) {
			result.add(expr);
		}
		return result;
	}

}
