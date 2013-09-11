package org.matheclipse.core.builtin.function;

import static org.matheclipse.core.expression.F.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryMap;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.generic.interfaces.BiFunction;

public class FoldList extends AbstractCoreFunctionEvaluator {

	public FoldList() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 4);

		return evaluateNestList(ast, List());
	}

	public static IExpr evaluateNestList(final IAST ast, final IAST resultList) {

		try {
			IExpr temp = F.eval(ast.get(3));
			if (temp.isAST()) {
				final IAST list = (IAST) temp;
				foldLeft(ast.get(2), list, 1, list.size(), new BinaryMap(F.ast(ast.get(1))), resultList);
				return resultList;
			}
		} catch (final ArithmeticException e) {

		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}

	/**
	 * Fold the list from <code>start</code> index including to <code>end</code> index excluding into the
	 * <code>resultCollection</code>. If the <i>binaryFunction</i> returns <code>null</code>, the left element will be added to the
	 * result list, otherwise the result will be <i>folded</i> again with the next element in the list.
	 * 
	 * @param list
	 * @param start
	 * @param end
	 * @param binaryFunction
	 * @param resultCollection
	 */
	public static IAST foldLeft(final IExpr expr, final IAST list, final int start, final int end,
			final BiFunction<IExpr, IExpr, ? extends IExpr> binaryFunction, final IAST resultCollection) {
		if (start < end) {
			IExpr elem = expr;
			resultCollection.add(elem);
			for (int i = start; i < end; i++) {

				elem = binaryFunction.apply(elem, list.get(i));
				resultCollection.add(elem);
			}

		}
		return resultCollection;
	}
}
