package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.mathml.MMLOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Power extends MMLOperator {

	public Power() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Power").getPrecedence(), "msup", "");
	}

	/**
	 * Converts a given function into the corresponding MathML output
	 * 
	 * @param buf
	 *            StringBuilder for MathML output
	 * @param f
	 *            The math function which should be converted to MathML
	 */
	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() != 3) {
			return false;
		}
		IExpr arg1 = f.arg1();
		IExpr arg2 = f.arg2();
		IExpr exp = arg2;
		IExpr den = F.C1;
		int useMROOT = 0;
		if (arg2.isFraction()) {
			IFraction f2 = ((IFraction) arg2);
			if (f2.isPositive()) {
				exp = f2.numerator();
				if (f2.equals(F.C1D2)) {
					fFactory.tagStart(buf, "msqrt");
					useMROOT = 1;
				} else {
					den = f2.denominator();
					fFactory.tagStart(buf, "mroot");
					useMROOT = 2;
				}

			}
		}
		if (useMROOT > 0 && exp.isOne()) {
			fFactory.convert(buf, arg1, Integer.MIN_VALUE, false);
		} else {
			if (exp.isNegative()) {
				exp = exp.negate();
				fFactory.tagStart(buf, "mfrac");
				fFactory.convert(buf, F.C1, Integer.MIN_VALUE, false);
				if (exp.isOne()) {
					fFactory.convert(buf, arg1, Integer.MIN_VALUE, false);
				} else {
					convert(buf, F.Power(arg1, exp), Integer.MIN_VALUE);
				}
				fFactory.tagEnd(buf, "mfrac");
			} else {
				precedenceOpen(buf, precedence);
				fFactory.tagStart(buf, "msup");
				fFactory.convert(buf, arg1, fPrecedence, false);
				fFactory.convert(buf, exp, fPrecedence, false);
				fFactory.tagEnd(buf, "msup");
				precedenceClose(buf, precedence);
			}
		}
		if (useMROOT == 1) {
			fFactory.tagEnd(buf, "msqrt");
		} else if (useMROOT == 2) {
			fFactory.convert(buf, den, fPrecedence, false);
			fFactory.tagEnd(buf, "mroot");
		}
		return true;
	}
}
