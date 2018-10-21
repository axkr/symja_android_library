package org.matheclipse.core.builtin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class WXFFunctions {
	static {
		F.BinarySerialize.setEvaluator(new BinarySerialize());
		F.BinaryDeserialize.setEvaluator(new BinaryDeserialize());
	}

	private static class BinarySerialize extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				byte[] bArray = WL.serialize(arg1);
				return WL.toList(bArray);
			}
			return F.NIL;
		}
	}

	private static class BinaryDeserialize extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isList()) {
					try {
						byte[] bArray = WL.toByteArray((IAST) arg1);
						IExpr temp= WL.deserialize(bArray);
						System.out.println(temp);
						return temp;
					} catch (ClassCastException cce) {
						if (Config.SHOW_STACKTRACE) {
							cce.printStackTrace();
						}
					}
				}
			}
			return F.NIL;
		}
	}

	private final static WXFFunctions CONST = new WXFFunctions();

	public static WXFFunctions initialize() {
		return CONST;
	}

	private WXFFunctions() {

	}

}
