package org.matheclipse.core.builtin.function;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * 
 */
public class Do extends AbstractCoreFunctionEvaluator {
	public static class DoIterator {

		final List<? extends IIterator<IExpr>> fIterList;
		final EvalEngine fEngine;
		int fIndex;

		public DoIterator(final List<? extends IIterator<IExpr>> iterList, EvalEngine engine) {
			fIterList = iterList;
			fEngine = engine;
			fIndex = 0;
		}

		public IExpr doIt(IExpr input) {
			if (fIndex < fIterList.size()) {
				final IIterator<IExpr> iter = fIterList.get(fIndex);
				if (iter.setUp()) {
					try {
						fIndex++;
						while (iter.hasNext()) {
							try {
								iter.next();
								fEngine.evaluate(input);
							} catch (final ReturnException e) {
								return e.getValue();
							} catch (final BreakException e) {
								return F.Null;
							} catch (final ContinueException e) {
								continue;
							}

						}
					} finally {
						--fIndex;
						iter.tearDown();
					}
				}
				return F.Null;
			}
			return F.NIL;
		}
	}

	public Do() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 3);
		try {
			final List<Iterator> iterList = new ArrayList<Iterator>();
			for (int i = 2; i < ast.size(); i++) {
				iterList.add(new Iterator((IAST) ast.get(i), engine));
			}
			final DoIterator generator = new DoIterator(iterList, engine);
			return generator.doIt(ast.arg1());
		} catch (final ClassCastException e) {
			// the iterators are generated only from IASTs
		} catch (final NoEvalException e) {
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
