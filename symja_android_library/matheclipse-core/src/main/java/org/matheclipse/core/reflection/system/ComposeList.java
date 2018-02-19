package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import java.util.function.BiFunction;

/**
 * <pre>
 * ComposeList(list - of - symbols, variable)
 * </pre>
 * 
 * <h3>Examples</h3>
 */
public class ComposeList extends AbstractEvaluator {

	public ComposeList() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		return evaluateComposeList(ast, F.ListAlloc(8));
	}

	public static IExpr evaluateComposeList(final IAST ast, final IASTAppendable resultList) {
		try {
			if ((ast.isAST2()) && (ast.arg1().isAST())) {
				// final EvalEngine engine = EvalEngine.get();
				final IAST list = (IAST) ast.arg1();
				final IAST constant = F.ast(ast.arg1());
				ListFunctions.foldLeft(ast.arg2(), list, 1, list.size(), new BiFunction<IExpr, IExpr, IExpr>() {
                    @Override
                    public IExpr apply(IExpr x, IExpr y) {
                        final IASTAppendable a = constant.apply(y);
                        a.append(x);
                        return a;
                    }
                }, resultList);
				return resultList;
			}
		} catch (final ArithmeticException e) {

		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
