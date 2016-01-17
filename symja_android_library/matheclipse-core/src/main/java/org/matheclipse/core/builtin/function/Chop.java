package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/** 
 * <p>
 * See the online Symja function reference: <a href="https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Chop">Chop</a>
 * </p>
 */
public class Chop extends AbstractCoreFunctionEvaluator {
	public final static double DEFAULT_CHOP_DELTA = 1.0e-10;
	public Chop() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		IExpr arg1 = ast.arg1();
		double delta = DEFAULT_CHOP_DELTA;
		if (ast.size() == 3 && ast.arg2() instanceof INum) {
			delta = ((INum) ast.arg2()).getRealPart();
		}
		try {
			arg1 = engine.evaluate(arg1);
			if (arg1.isAST()) {
				IAST list = (IAST) arg1;
				// Chop[{a,b,c}] -> {Chop[a],Chop[b],Chop[c]}
				return list.mapAt(F.Chop(F.Null),1);
			}
			if (arg1.isNumber()) {
				return F.chopNumber((INumber)arg1, delta);
			}
		} catch (Exception e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return F.UNEVALED;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE);
	}
}
