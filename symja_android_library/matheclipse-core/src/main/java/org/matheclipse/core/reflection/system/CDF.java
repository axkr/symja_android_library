package org.matheclipse.core.reflection.system;

import org.hipparchus.distribution.IntegerDistribution;
import org.hipparchus.distribution.RealDistribution;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Compute the cumulative distribution function 
 */
public class CDF extends AbstractFunctionEvaluator {

	public CDF() {
		// empty default constructor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		if (ast.arg1().isAST()) {
			IAST arg1 = (IAST) ast.arg1();
			IExpr xArg = ast.arg2();

			if (arg1.isAST()) {
				IAST dist = (IAST) arg1;

				if (dist.head().isSymbol()) {
					ISymbol head = (ISymbol) dist.head();
					if (engine.isNumericMode()) {
						// numeric calculations
						return evaluateNumericMode(dist, xArg, head);
					} else {
						// symbolic calculations
					}
				}
			}
		}
		return F.NIL;
	}

	private IExpr evaluateNumericMode(IAST dist, IExpr xArg, ISymbol head) {
		try {
			RealDistribution realDistribution;
			IntegerDistribution intDistribution;
			
			if (dist.isAST1()) {
				if (head.equals(F.BernoulliDistribution)) {
				} else if (head.equals(F.PoissonDistribution)) {
					int n = ((ISignedNumber) dist.arg1()).toInt();
					int k = ((ISignedNumber) xArg).toInt();
				}
			} else if (dist.isAST2()) {

				if (head.equals(F.BinomialDistribution)) {
					int n = ((ISignedNumber) dist.arg1()).toInt();
					double p = ((ISignedNumber) dist.arg2()).doubleValue();
					int k = ((ISignedNumber) xArg).toInt();
				} else if (head.equals(F.NormalDistribution)) {
					double mean = ((ISignedNumber) dist.arg1()).doubleValue();
					double stdDev = ((ISignedNumber) dist.arg2()).doubleValue();
					double x = ((ISignedNumber) xArg).doubleValue();
				}
			} else if (dist.isAST3()) {
				if (head.equals(F.HypergeometricDistribution)) {
					int n = ((ISignedNumber) dist.arg1()).toInt();
					int nSucc = ((ISignedNumber) dist.arg2()).toInt();
					int nTot = ((ISignedNumber) dist.arg3()).toInt();
					int k = ((ISignedNumber) xArg).toInt();
				}
			}
		} catch (ArithmeticException ae) {
		} catch (ClassCastException cca) {
		}
		return F.NIL;
	}

}
