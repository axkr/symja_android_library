package org.matheclipse.core.reflection.system;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * Sign(x)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives <code>-1</code>, <code>0</code> or <code>1</code> depending on whether <code>x</code> is negative, zero or
 * positive.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; Sign(-2.5)
 * -1
 * </pre>
 */
public class Sign extends AbstractCoreFunctionEvaluator {

	public Sign() {
	}

	private static final class SignTimesFunction implements Function<IExpr, IExpr> {
		@Override
		public IExpr apply(IExpr expr) {
			if (expr.isNumber()) {
				return numberSign((INumber) expr);
			}
			IExpr temp = F.eval(F.Sign(expr));
			if (!temp.topHead().equals(F.Sign)) {
				return temp;
			}
			return F.NIL;
		}
	}

	/**
	 * Gets the sign value of a number. See <a href="http://en.wikipedia.org/wiki/Sign_function">Wikipedia - Sign
	 * function</a>
	 * 
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		// IExpr arg1 = ast.arg1();

		IExpr result = F.NIL;
		IExpr arg1 = engine.evaluateNull(ast.arg1());
		if (arg1.isPresent()) {
			result = F.Sign(arg1);
		} else {
			arg1 = ast.arg1();
		}
		if (arg1.isList()) {
			return ((IAST) arg1).mapThread(F.Sign(F.Null), 1);
		}

		if (arg1.isNumber()) {
			if (arg1.isComplexNumeric()) {
				IComplexNum c = (IComplexNum) arg1;
				return c.divide(F.num(c.dabs()));
			}
			return numberSign((INumber) arg1);
		}
		if (arg1.isIndeterminate()) {
			return F.Indeterminate;
		}
		if (arg1.isDirectedInfinity()) {
			IAST directedInfininty = (IAST) arg1;
			if (directedInfininty.isComplexInfinity()) {
				return F.Indeterminate;
			}
			if (directedInfininty.isAST1()) {
				return F.Sign(directedInfininty.arg1());
			}
		} else if (arg1.isTimes()) {
			IASTAppendable[] res = ((IAST) arg1).filter(new SignTimesFunction());
			if (res[0].size() > 1) {
				if (res[1].size() > 1) {
					res[0].append(F.Sign(res[1]));
				}
				return res[0];
			}
		} else if (arg1.isPower() && arg1.exponent().isReal()) {
			return F.Power(F.Sign(arg1.base()), arg1.exponent());
		}
		if (AbstractAssumptions.assumeNegative(arg1)) {
			return F.CN1;
		}
		if (AbstractAssumptions.assumePositive(arg1)) {
			return F.C1;
		}

		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr.isPresent()) {
			return F.Times(F.CN1, F.Sign(negExpr));
		}
		INumber number = arg1.evalNumber();
		if (number != null) {
			IExpr temp = numberSign(number);
			if (temp.isPresent()) {
				return temp;
			}
		}
		if (arg1.isRealResult() && !arg1.isZero()) {
			return F.Divide(arg1, F.Abs(arg1));
		}
		IExpr y = AbstractFunctionEvaluator.imaginaryPart(arg1, true);
		if (y.isPresent() && y.isRealResult()) {
			IExpr x = AbstractFunctionEvaluator.realPart(arg1, false);
			if (x.isPresent() && x.isRealResult()) {
				// (x + I*y)/Sqrt(x^2 + y^2)
				return F.Times(F.Plus(x, F.Times(F.CI, y)), F.Power(F.Plus(F.Sqr(x), F.Sqr(y)), F.CN1D2));
			}
		}
		return result;
	}

	public static IExpr numberSign(INumber arg1) {
		if (arg1.isReal()) {
			final int signum = ((ISignedNumber) arg1).sign();
			return F.integer(signum);
		} else if (arg1.isComplex()) {
			IComplex c = (IComplex) arg1;
			return F.Times(c, F.Power(c.abs(), F.CN1));
		}
		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
	}

}
