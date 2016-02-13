package org.matheclipse.core.reflection.system;

import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.PositionConverter;
import org.matheclipse.core.generic.interfaces.IPositionConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Extract extends AbstractFunctionEvaluator {

	public Extract() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3, 4);

		if (ast.arg1().isAST() && ast.arg2().isList()) {
			IAST arg1 = (IAST) ast.arg1();
			IAST arg2 = (IAST) ast.arg2();
			if (arg2.isListOfLists()) {
				IAST result = F.List();
				final int arg2Size = arg2.size();
				for (int i = 1; i < arg2Size; i++) {
					IExpr temp = extract(arg1, arg2.getAST(i));
					if (!temp.isPresent()) {
						return F.NIL;
					}
					result.add(temp);
				}
				return result;
			}
			return extract(arg1, arg2);
		}
		return F.NIL;
	}

	public static IExpr extract(final IAST list, final IAST position) {
		final PositionConverter converter = new PositionConverter();
		if ((position.size() > 1) && (position.arg1().isSignedNumber())) {
			return extract(list, position, converter, 1);
		} else {
			// construct an array
			// final IAST resultList = List();
			// NestedFinding.position(list, resultList, pos, 1);
			// return resultList;
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.NHOLDREST);
	}

	/**
	 * Traverse all <code>list</code> element's and filter out the elements in
	 * the given <code>positions</code> list.
	 * 
	 * @param list
	 * @param positions
	 * @param positionConverter
	 *            the <code>positionConverter</code> creates an <code>int</code>
	 *            value from the given position objects in
	 *            <code>positions</code>.
	 * @param headOffsez
	 */
	public static IExpr extract(final IAST list, final List<? extends IExpr> positions,
			final IPositionConverter<? super IExpr> positionConverter, int headOffset) {
		int p = 0;
		IAST temp = list;
		int posSize = positions.size() - 1;
		IExpr expr = list;
		for (int i = headOffset; i <= posSize; i++) {
			p = positionConverter.toInt(positions.get(i));
			if (temp == null || temp.size() <= p || p < 0) {
				return F.NIL;
			}
			expr = temp.get(p);
			if (expr.isAST()) {
				temp = (IAST) expr;
			} else {
				if (i < positions.size()) {
					temp = null;
				}
			}
		}
		return expr;
	}
}
