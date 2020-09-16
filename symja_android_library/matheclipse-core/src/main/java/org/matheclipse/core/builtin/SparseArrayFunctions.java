package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;

public class SparseArrayFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			S.ArrayRules.setEvaluator(new ArrayRules());
			S.SparseArray.setEvaluator(new SparseArray());
		}
	}

	private static class ArrayRules extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			IExpr defaultValue = F.C0;
			if (ast.isAST2()) {
				defaultValue = ast.arg2();
			}
			if (arg1.isSparseArray()) {

				ISparseArray sparseArray = (ISparseArray) arg1;
				IExpr d = sparseArray.getDefaultValue();
				if (ast.isAST1()) {
					defaultValue = d;
				} else if (ast.isAST2()) {
					if (!d.equals(defaultValue)) {
						return engine.printMessage(ast.topHead().toString() + ": Sparse array default value: "
								+ d.toString() + " unequals default value " + defaultValue.toString());
					}
				}
				return sparseArray.arrayRules();

			}
			if (arg1.isList()) {
				return SparseArrayExpr.arrayRules((IAST) arg1, defaultValue);
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}

	}

	private static class SparseArray extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isSparseArray()) {
				return F.NIL;
			}
			IExpr defaultValue = F.NIL;
			if (ast.isAST3()) {
				defaultValue = ast.arg3();
			}
			int defaultDimension = -1;
			int[] dimension = null;
			if (ast.isAST2()) {
				if (ast.arg2().isList()) {
					dimension = Validate.checkListOfInts(ast, ast.arg2(), 1, Integer.MAX_VALUE, engine);
				} else {
					defaultDimension = ast.arg2().toIntDefault();
					if (defaultDimension < 0) {
						return F.NIL;
					}
				}
			}
			ISparseArray result = null;

			IExpr arg1 = ast.arg1();
			if (arg1.isListOfRules()) {
				if (arg1.size() < 2) {
					// The dimensions cannot be determined from the position `1`.
					return IOFunctions.printMessage(ast.topHead(), "exdims", F.List(arg1), engine);
				}
				result = SparseArrayExpr.newInstance((IAST) arg1, dimension, defaultDimension, defaultValue);
			} else if (arg1.isList()) {
				if (arg1.size() < 2) {
					// The dimensions cannot be determined from the position `1`.
					return IOFunctions.printMessage(ast.topHead(), "exdims", F.List(arg1), engine);
				}
				result = SparseArrayExpr.newInstance((IAST) arg1, defaultValue);
			} else if (arg1.isRule()) {
				result = SparseArrayExpr.newInstance(F.List(arg1), dimension, defaultDimension, defaultValue);
			} else {
				// List expected at position `1` in `2`.
				return IOFunctions.printMessage(ast.topHead(), "list", F.List(F.C1, ast), engine);
			}
			if (result != null) {
				return result;
			}

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}
	}

	public static void initialize() {
		Initializer.init();
	}

	private SparseArrayFunctions() {

	}

}
