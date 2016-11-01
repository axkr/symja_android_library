package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** 
 *
 */
public class DirectedInfinity extends AbstractCoreFunctionEvaluator {

	public DirectedInfinity() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 1, 2);

		if (ast.isAST1()) {
			boolean numericMode = engine.isNumericMode();
			try {
				engine.setNumericMode(false);
				IExpr temp = engine.evalLoop(ast.arg1());
				if (temp.isPresent()) {
					if (temp.isIndeterminate()) {
						return F.CComplexInfinity;
					}
					return F.DirectedInfinity(temp);
				}
				if (ast.arg1().isIndeterminate()) {
					return F.CComplexInfinity;
				}
			} finally {
				engine.setNumericMode(numericMode);
			}
		}
		return F.NIL;
	}

	public static IExpr timesInf(IAST inf, IExpr a2) {
		if (inf.isAST1()) {
			IExpr result;
			IExpr a1 = inf.arg1();
			if (a1.isNumber()) {
				if (a2.isNumber()) {
					result = a1.times(a2);
					if (result.isSignedNumber()) {
						if (result.isNegative()) {
							return F.CNInfinity;
						} else {
							return F.CInfinity;
						}
					} else if (result.isImaginaryUnit()) {
						return F.DirectedInfinity(F.CI);
					} else if (result.isNegativeImaginaryUnit()) {
						return F.DirectedInfinity(F.CNI);
					}
				} else if (a2.isSymbol()) {
					if (a1.isOne()) {
						return F.DirectedInfinity(a2);
					} else if (a1.isMinusOne() || a1.equals(F.CI) || a1.equals(F.CNI)) {
						return F.DirectedInfinity(F.Times(a1, a2));
					}
				}
			} else if (a1.isSymbol()) {
				if (a2.isSignedNumber()) {
					if (a2.isNegative()) {
						return F.DirectedInfinity(F.Times(F.CN1, F.Sign(a1)));
					} else {
						return F.DirectedInfinity(a1);
					}
				} else if (a2.equals(F.CI)) {
					return F.DirectedInfinity(F.Times(F.CI, a1));
				} else if (a2.equals(F.CNI)) {
					return F.DirectedInfinity(F.Times(F.CNI, F.Sign(a1)));
				}
			}
			result = F.Divide(F.Times(a1, a2), F.Abs(F.Times(a1, a2)));
			return F.DirectedInfinity(result);
		}
		// ComplexInfinity
		return inf;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		// don't set ISymbol.NUMERICFUNCTION);
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}

}
