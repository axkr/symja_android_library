package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * UnitStep(expr)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * returns <code>0</code>, if <code>expr</code> is less than <code>0</code> and returns <code>1</code>, if
 * <code>expr</code> is greater equal than <code>0</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; UnitStep(-42)
 * 0
 * </pre>
 */
public class UnitStep extends AbstractEvaluator implements INumeric {

	public UnitStep() {
	}

	@Override
	public double evalReal(double[] stack, int top, int size) {
		for (int i = top - size + 1; i < top + 1; i++) {
			if (stack[i] < 0.0) {
				return 0.0;
			}
		}
		return 1.0;
	}

	/**
	 * Unit step <code>1</code> for all x greater equal <code>0</code>. <code>0</code> in all other cases,
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		int size = ast.size();
		if (size > 1) {
			for (int i = 1; i < size; i++) {
				IExpr expr = ast.get(i);
				ISignedNumber temp = expr.evalSignedNumber();
				if (temp != null) {
					if (temp.sign() < 0) {
						return F.C0;
					} else {
						continue;
					}
				} else {
					if (expr.isNegativeInfinity()) {
						return F.C0;
					}
					if (expr.isInfinity()) {
						continue;
					}
					if (expr.isInterval1()) {
						IExpr l = expr.lower();
						IExpr u = expr.upper();
						if (l.isSignedNumber() && u.isSignedNumber()) {
							ISignedNumber min = (ISignedNumber) l;
							ISignedNumber max = (ISignedNumber) u;
							if (min.sign() < 0) {
								if (max.sign() < 0) {
									return F.Interval(F.List(F.C0, F.C0));
								} else {
									if (size == 2) {
										return F.Interval(F.List(F.C0, F.C1));
									}
								}
							} else {
								if (max.sign() < 0) {
									if (size == 2) {
										return F.Interval(F.List(F.C1, F.C0));
									}
								} else {
									if (size == 2) {
										return F.Interval(F.List(F.C1, F.C1));
									}
									continue;
								}
							}
						}
					}
				}
				return F.NIL;
			}
		}
		return F.C1;
	}

	@Override
	public void setUp(ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.ORDERLESS | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}
}
