package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.eval.util.TableGenerator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryRangeFunction;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class Range extends AbstractEvaluator {

	public Range() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.isAST1() && ast.arg1().isInteger()) {
			try {
				int size = ((IInteger) ast.arg1()).toInt();
				if (size >= 0) {
					IAST result = F.ListAlloc(size);
					for (int i = 1; i <= size; i++) {
						result.append(F.integer(i));
					}
					return result;
				}
				return F.List();
			} catch (final ArithmeticException ae) {
			}

			return F.NIL;
		}
		return evaluateTable(ast, List(), engine);
	}

	public IExpr evaluateTable(final IAST ast, final IAST resultList, EvalEngine engine) {
		List<IIterator<IExpr>> iterList = null;
		try {
			if ((ast.size() > 1) && (ast.size() <= 4)) {
				iterList = new ArrayList<IIterator<IExpr>>();
				iterList.add(Iterator.create(ast, null, engine));

				final TableGenerator generator = new TableGenerator(iterList, resultList, new UnaryRangeFunction());
				return generator.table();
			}
		} catch (final ClassCastException e) {
			// the iterators are generated only from IASTs
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}
}
