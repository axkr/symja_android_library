package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.Negate;

import org.apache.commons.math4.complex.Complex;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractTrigArg1;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.ArcCothRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Arccotangent hyperbolic
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_hyperbolic_function"> Inverse hyperbolic functions</a>
 */
public class ArcCoth extends AbstractTrigArg1 implements ArcCothRules {

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ArcCoth() {
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr != null) {
			return Negate(ArcCoth(negExpr));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart != null) {
			return F.Times(F.CNI, F.ArcCot(imPart));
		}
		return null;
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		if (F.isZero(arg1)) {
			// I*Pi / 2
			return F.complexNum(new Complex(0.0, Math.PI).divide(new Complex(2.0)));
		}
		double c = 1.0 / arg1;
		return F.num((Math.log(1.0 + c) - Math.log(1.0 - c)) / 2.0);
	}

	@Override
	public IExpr e1ComplexArg(final Complex arg1) {
		// 1/arg1
		Complex c = arg1.reciprocal();

		// (1/2) (Log(1 + 1/arg1) - Log(1 - 1/arg1))
		Complex result = new Complex(0.5).multiply(Complex.ONE.add(c).log().subtract(Complex.ONE.subtract(c).log()));
		return F.complexNum(result);
	}

	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		if (arg1.equals(Apcomplex.ZERO)) {
			// I*Pi / 2
			return F.complexNum(new Apcomplex(Apcomplex.ZERO, ApfloatMath.pi(arg1.precision())).divide(new Apfloat(2)));
		}
		return F.num(ApfloatMath.atanh(arg1.inverse()));
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		// 1/arg1
		Apcomplex c = arg1.inverse();

		// (1/2) (Log(1 + 1/arg1) - Log(1 - 1/arg1))
		Apcomplex result = ApcomplexMath.log(Apcomplex.ONE.add(c)).subtract(ApcomplexMath.log(Apcomplex.ONE.subtract(c)))
				.divide(new Apfloat(2));
		return F.complexNum(result);
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
