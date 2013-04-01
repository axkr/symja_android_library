package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IConstantHeaders;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Times extends AbstractOperator {
	public final static int NO_SPECIAL_CALL = 0;

	public final static int PLUS_CALL = 1;

	public Times() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "mrow", "&#x2062;");
	}

	/**
	 * Converts a given function into the corresponding MathML output
	 * 
	 * @param buf
	 *          StringBuffer for MathML output
	 * @param f
	 *          The math function which should be converted to MathML
	 */
	@Override
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		return convert(buf, f, precedence, NO_SPECIAL_CALL);
	}

	/**
	 * Converts a given function into the corresponding MathML output
	 * 
	 * @param buf
	 *          StringBuffer for MathML output
	 * @param f
	 *          The math function which should be converted to MathML
	 */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence, final int caller) {
		final IAST numerator = F.function(IConstantHeaders.Times);
		final IAST denominator = F.function(IConstantHeaders.Times);
		boolean flag = false;
		IExpr expr;
		for (int i = 1; i < f.size(); i++) {
			expr = f.get(i);
			if (expr instanceof IRational) {
				IInteger num = ((IRational) expr).getNumerator();
				IInteger den = ((IRational) expr).getDenominator();
				if (!NumberUtil.isOne(num)) {
					numerator.add(num);
				}
				if (!NumberUtil.isOne(den)) {
					denominator.add(den);
				}
				flag = true;
				continue;
			}
			if ((f.get(i) instanceof IAST) && ((IAST) f.get(i)).head().toString().equals(IConstantHeaders.Power)) {
				// filter negative Powers:
				final IAST p = (IAST) f.get(i);
				if ((p.size() == 3) && (p.get(2) instanceof ISignedNumber) && ((ISignedNumber) p.get(2)).isNegative()) {
					if (NumberUtil.isMinusOne(p.get(2))) {
						// x_^(-1) ?
						denominator.add(p.get(1));
						flag = true;
						continue;
					}

					denominator.add(F.function(F.Power, p.get(1), F.function(F.Times, F.integer(-1), p.get(2))));
					flag = true;
					continue;
				}
			}
			if (!NumberUtil.isOne(f.get(i))) {
				numerator.add(f.get(i));
			}
		}
		// do the output:
		if (denominator.size() > 1 && flag) {
			if (caller == PLUS_CALL) {
				fFactory.tag(buf, "mo", "+");
			}
			fFactory.tagStart(buf, "mfrac");
			// insert numerator in buffer:
			if (numerator.size() > 1) {
				if (numerator.size() == 2) {
					fFactory.convert(buf, numerator.get(1), 0);
				} else {
					fFactory.convert(buf, numerator, fPrecedence);
				}

			} else {
				fFactory.tag(buf, "mn", "1");
			}
			if (denominator.size() == 2) {
				fFactory.convert(buf, denominator.get(1), 0);
			} else {
				fFactory.convert(buf, denominator, fPrecedence);
			}
			fFactory.tagEnd(buf, "mfrac");
		} else {
			if (numerator.size() <= 2) {
				convertMultiply(buf, f, precedence, caller);
			} else {
				convertMultiply(buf, numerator, precedence, caller);
			}
		}

		return true;
	}

	private boolean convertMultiply(final StringBuffer buf, final IAST f, final int precedence, final int caller) {
		IExpr expr;

		if (f.size() > 1) {
			expr = f.get(1);
			if (NumberUtil.isMinusOne(expr)) {
				if (f.size() == 2) {
					fFactory.tagStart(buf, fFirstTag);
					precedenceOpen(buf, precedence);
					fFactory.convert(buf, expr, fPrecedence);
				} else {
					if (caller == PLUS_CALL) {
						fFactory.tag(buf, "mo", "-");
						if (f.size() == 3) {
							fFactory.convert(buf, f.get(2), fPrecedence);
							return true;
						}

						fFactory.tagStart(buf, fFirstTag);
					} else {
						fFactory.tagStart(buf, fFirstTag);
						precedenceOpen(buf, precedence);
						fFactory.tag(buf, "mo", "-");
					}
				}
			} else {
				if (caller == PLUS_CALL) {
					if ((expr instanceof ISignedNumber) && (((ISignedNumber) expr).isNegative())) {
						fFactory.tag(buf, "mo", "-");
						fFactory.tagStart(buf, fFirstTag);
						expr = ((ISignedNumber) expr).negate();
					} else {
						fFactory.tag(buf, "mo", "+");
						fFactory.tagStart(buf, fFirstTag);
					}
				} else {
					fFactory.tagStart(buf, fFirstTag);
					precedenceOpen(buf, precedence);
				}
				fFactory.convert(buf, expr, fPrecedence);
				if (fOperator.compareTo("") != 0) {
					fFactory.tag(buf, "mo", fOperator);
				}
			}
		}

		for (int i = 2; i < f.size(); i++) {
			fFactory.convert(buf, f.get(i), fPrecedence);
			if ((i < f.size() - 1) && (fOperator.compareTo("") != 0)) {
				fFactory.tag(buf, "mo", fOperator);
			}
		}
		precedenceClose(buf, precedence);
		fFactory.tagEnd(buf, fFirstTag);
		return true;
	}
}