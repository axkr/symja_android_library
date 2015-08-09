package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Pi;
import static org.matheclipse.core.expression.F.Plus;

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
import org.matheclipse.core.reflection.system.rules.ArcCotRules;
import org.matheclipse.parser.client.SyntaxError;

/**
 * Arccotangent
 * 
 * See <a href="http://en.wikipedia.org/wiki/Inverse_trigonometric functions"> Inverse_trigonometric functions</a>
 */
public class ArcCot extends AbstractTrigArg1 implements ArcCotRules {

	@Override
	public IAST getRuleAST() {
		return RULES;
	}

	public ArcCot() {
	}

	@Override
	public IExpr e1DblArg(final double arg1) {
		if (F.isZero(arg1)) {
			// Pi / 2
			return F.num(Math.PI / 2.0);
		}
		return F.num(Math.atan(1 / arg1));
	}

	@Override
	public IExpr e1ComplexArg(final Complex arg1) {
		// I/arg1
		Complex c = Complex.I.divide(arg1);

		// (I/2) (Log(1 - I/arg1) - Log(1 + I/arg1))
		Complex result = Complex.I.divide(new Complex(2.0)).multiply(
				Complex.ONE.subtract(c).log().subtract(Complex.ONE.add(c).log()));
		return F.complexNum(result);
	}

	@Override
	public IExpr e1ApfloatArg(Apfloat arg1) {
		if (arg1.equals(Apcomplex.ZERO)) {
			// Pi / 2
			return F.num(ApfloatMath.pi(arg1.precision()).divide(new Apfloat(2)));
		}
		return F.num(ApfloatMath.atan(arg1.inverse()));
	}

	@Override
	public IExpr e1ApcomplexArg(Apcomplex arg1) {
		// I/arg1
		Apcomplex ac = Apcomplex.I.divide(arg1);

		// (I/2) (Log(1 - I/arg1) - Log(1 + I/arg1))
		Apcomplex result = Apcomplex.I.divide(new Apfloat(2)).multiply(
				ApcomplexMath.log(Apcomplex.ONE.subtract(ac)).subtract(ApcomplexMath.log(Apcomplex.ONE.add(ac))));
		return F.complexNum(result);
	}

	@Override
	public IExpr evaluateArg1(final IExpr arg1) {
		IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
		if (negExpr != null) {
			return Plus(Negate(Pi), ArcCot(negExpr));
		}
		IExpr imPart = AbstractFunctionEvaluator.getPureImaginaryPart(arg1);
		if (imPart != null) {
			return F.Times(F.CNI, F.ArcCoth(imPart));
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) throws SyntaxError {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}
}
