package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Power;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractArg2;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

public class Surd extends AbstractArg2 implements INumeric {
	@Override
	public IExpr e2ApfloatArg(final ApfloatNum af0, final ApfloatNum af1) {
		if (af1.isZero()) {
			EvalEngine ee = EvalEngine.get();
			ee.printMessage("Surd(a,b) division by zero");
			return F.Indeterminate;
		}
		if (af0.isNegative()) {
			return af0.negate().pow(af1.inverse()).negate();
		}
		return af0.pow(af1.inverse());
	}

	@Override
	public IExpr e2DblArg(INum d0, INum d1) {
		double val = d0.doubleValue();
		double r = d1.doubleValue();
		if (r == 0.0d) {
			EvalEngine ee = EvalEngine.get();
			ee.printMessage("Surd(a,b) division by zero");
			return F.Indeterminate;
		}
		if (val < 0.0d) {
			return F.num(-Math.pow(-val, 1.0d / r));
		}
		return F.num(Math.pow(val, 1.0d / r));
	}

	@Override
	public IExpr e2ObjArg(final IExpr o, final IExpr r) {
		if (r.isInteger()) {
			EvalEngine ee = EvalEngine.get();
			if (o.isNegative() && ((IInteger) r).isEven()) {
				ee.printMessage("Surd(a,b) is undefined for negative \"a\" and even \"b\"");
				return F.Indeterminate;
			}
			if (r.isZero()) {
				ee.printMessage("Surd(a,b) division by zero");
				return F.Indeterminate;
			}
			if (o.isMinusOne()) {
				return F.CN1;
			}
			return Power(o, ((IInteger) r).inverse());
		}
		return null;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
		super.setUp(symbol);
	}

	@Override
	public double evalReal(double[] stack, int top, int size) {
		if (size != 2) {
			throw new UnsupportedOperationException();
		}
		return doubleSurd(stack[top - 1], stack[top]);
	}

	private double doubleSurd(double val, double r) {
		if (r == 0.0d) {
			EvalEngine ee = EvalEngine.get();
			ee.printMessage("Surd(a,b) division by zero");
			return Double.NaN;
		}
		if (val < 0.0d) {
			return -Math.pow(-val, 1.0d / r);
		}
		return Math.pow(val, 1.0d / r);
	}
}