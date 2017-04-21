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
			boolean evaled = false;
			try {
				engine.setNumericMode(false);
				IExpr arg1 = ast.arg1();
				IExpr temp = engine.evalLoop(arg1);
				if (temp.isPresent()) {
					arg1 = temp;
					evaled = true;
				}
				if (arg1.isIndeterminate() || arg1.isZero()) {
					return F.CComplexInfinity;
				}
				if (arg1.isSignedNumber()) {
					if (arg1.isOne() || arg1.isMinusOne()) {
						if (evaled) {
							return F.DirectedInfinity(arg1);
						}
						return F.NIL;
					}
					if (arg1.isNegative()) {
						return F.DirectedInfinity(F.CN1);
					} else {
						return F.DirectedInfinity(F.C1);
					}
				}
				if (arg1.isNumber()) {
					IExpr arg1Abs = engine.evaluate(F.Divide(arg1, F.Abs(arg1)));
					if (arg1.equals(arg1Abs)) {
						if (evaled) {
							return F.DirectedInfinity(arg1);
						}
						return F.NIL;
					}
					return F.DirectedInfinity(arg1Abs);
				}
				if (arg1.isNumericFunction()) {
					IExpr a1 = F.evaln(arg1);
					if (a1.isSignedNumber()) {
						if (a1.isZero()) {
							return F.CComplexInfinity;
						}
						if (a1.isNegative()) {
							return F.DirectedInfinity(F.CN1);
						} else {
							return F.DirectedInfinity(F.C1);
						}
					}
					// if (a1.isNumber()) {
					// return F.DirectedInfinity(engine.evaluate(F.Divide(arg1, F.Abs(arg1))));
					// }
				}
				if (evaled) {
					return F.DirectedInfinity(arg1);
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
