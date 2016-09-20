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
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Range extends AbstractEvaluator {

	public Range() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		return evaluateTable(ast, List(), engine);
	}

	public IExpr evaluateTable(final IAST ast, final IAST resultList, EvalEngine engine) {
		List<Iterator> iterList = null;
		try {
			if ((ast.size() > 1) && (ast.size() <= 4)) {
				iterList = new ArrayList<Iterator>();
				iterList.add(new Iterator(ast, null, engine));

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
