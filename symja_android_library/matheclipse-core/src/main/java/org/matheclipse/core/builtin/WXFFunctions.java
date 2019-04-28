package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;

public class WXFFunctions {
	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.BinarySerialize.setEvaluator(new BinarySerialize());
			F.BinaryDeserialize.setEvaluator(new BinaryDeserialize());
			F.ByteArray.setEvaluator(new ByteArray());
		}
	}

	private static class BinarySerialize extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				byte[] bArray = WL.serialize(arg1);
				return DataExpr.newInstance(F.ByteArray, bArray);
			}
			return F.NIL;
		}
	}

	private static class ByteArray extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isList()) {
					try {
						byte[] bArray = WL.toByteArray((IAST) arg1);
						return DataExpr.newInstance(F.ByteArray, bArray);
					} catch (RuntimeException cce) {
						if (Config.SHOW_STACKTRACE) {
							cce.printStackTrace();
						}
					}
				}
				return engine.printMessage("ByteArray: list of byte values expected");
			}
			return F.NIL;
		}
	}

	private static class BinaryDeserialize extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (isByteArray(arg1)) {
					try {
						byte[] bArray = (byte[]) ((IDataExpr) arg1).toData();
						IExpr temp = WL.deserialize(bArray);
						// System.out.println(temp);
						return temp;
					} catch (RuntimeException cce) {
						if (Config.SHOW_STACKTRACE) {
							cce.printStackTrace();
						}
					}
				}
			}
			return F.NIL;
		}

	}

	public static boolean isByteArray(IExpr arg1) {
		return arg1 instanceof IDataExpr && arg1.head().equals(F.ByteArray);
	}

	public static void initialize() {
		Initializer.init();
	}

	private WXFFunctions() {

	}

}
