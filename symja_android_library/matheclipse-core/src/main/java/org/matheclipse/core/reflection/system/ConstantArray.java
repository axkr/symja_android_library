package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.builtin.function.Array.ArrayIterator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.TableGenerator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.MultipleConstArrayFunction;
import org.matheclipse.core.generic.interfaces.IIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Array structure generator for constant (i,j) value.
 */
public class ConstantArray implements IFunctionEvaluator {

	public static class ArrayIterator implements IIterator<IExpr> {
		int fCurrent;

		final int fFrom;

		final int fTo;

		public ArrayIterator(final int to) {
			this(1, to);
		}

		public ArrayIterator(final int from, final int length) {
			fFrom = from;
			fCurrent = from;
			fTo = from + length - 1;
		}

		public boolean setUp() {
			return true;
		}

		public void tearDown() {
			fCurrent = fFrom;
		}

		public boolean hasNext() {
			return fCurrent <= fTo;
		}

		public IExpr next() {
			return F.integer(fCurrent++);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public ConstantArray() {
	}

	public IExpr evaluate(final IAST ast) {
		return evaluateArray(ast, List());
	}

	public static IExpr evaluateArray(final IAST ast, IAST resultList) {
		try {
			if ((ast.size() >= 3) && (ast.size() <= 5)) {
				int indx1, indx2;
				final EvalEngine engine = EvalEngine.get();
				final List<ArrayIterator> iterList = new ArrayList<ArrayIterator>();
				if ((ast.size() == 3) && (ast.get(2).isInteger())) {
					indx1 = Validate.checkIntType(ast, 2);
					iterList.add(new ArrayIterator(indx1));
				} else if ((ast.size() == 3) && ast.get(2).isList()) {
					final IAST dimIter = (IAST) ast.get(2);
					for (int i = 1; i < dimIter.size(); i++) {
						indx1 = Validate.checkIntType(dimIter, i);
						iterList.add(new ArrayIterator(indx1));
					}
				} else if (ast.size() >= 4) {
					if (ast.get(2).isInteger() && ast.get(3).isInteger()) {
						indx1 = Validate.checkIntType(ast, 3);
						indx2 = Validate.checkIntType(ast, 2);
						iterList.add(new ArrayIterator(indx1, indx2));
					} else if (ast.get(2).isList() && ast.get(3).isList()) {
						final IAST dimIter = (IAST) ast.get(2); // dimensions
						final IAST originIter = (IAST) ast.get(3); // origins
						for (int i = 1; i < dimIter.size(); i++) {
							indx1 = Validate.checkIntType(originIter, i);
							indx2 = Validate.checkIntType(dimIter, i);
							iterList.add(new ArrayIterator(indx1, indx2));
						}
					}
				}

				if (iterList.size() > 0) {
					if (ast.size() == 5) {
						resultList = F.ast(ast.get(4));
					}
					final IExpr constantExpr = ast.get(1);
					final TableGenerator generator = new TableGenerator(iterList, resultList, new MultipleConstArrayFunction(
							constantExpr));
					return generator.table();
				}

			}
		} catch (final ClassCastException e) {
			// the iterators are generated only from IASTs
		} catch (final ArithmeticException e) {
			// the toInt() function throws ArithmeticExceptions
		}
		return null;
	}

	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL);
	}
}
