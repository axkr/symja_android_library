package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.reflection.system.Apart;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Times extends AbstractOperator {
	public final static int NO_SPECIAL_CALL = 0;

	public final static int PLUS_CALL = 1;

	public static Times CONST = new Times();

	public Times() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "\\,");
	}

	/**
	 * Converts a given function into the corresponding MathML output
	 * 
	 * @param buf
	 *            StringBuffer for MathML output
	 * @param f
	 *            The math function which should be converted to MathML
	 */
	@Override
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		return convert(buf, f, precedence, NO_SPECIAL_CALL);
	}

	/**
	 * Converts a given function into the corresponding MathML output
	 * 
	 * @param buf
	 *            StringBuffer for MathML output
	 * @param f
	 *            The math function which should be converted to MathML
	 */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence, final int caller) {
		IExpr[] parts = Apart.getFractionalPartsTimes(f, true);
		if (parts == null) {
			convertMultiply(buf, f, precedence, caller);
			return true;
		}
		final IExpr numerator = parts[0];
		final IExpr denominator = parts[1];
		if (!denominator.isOne()) {
			if (caller == PLUS_CALL) {
				buf.append('+');
			}
			buf.append("\\frac{");
			// insert numerator in buffer:
			if (numerator.isTimes()) {
				convertMultiply(buf, (IAST) numerator, fPrecedence, NO_SPECIAL_CALL);
			} else {
				fFactory.convert(buf, numerator, precedence);
			}
			buf.append("}{");
			// insert denominator in buffer:
			if (denominator.isTimes()) {
				convertMultiply(buf, (IAST) denominator, fPrecedence, NO_SPECIAL_CALL);
			} else {
				fFactory.convert(buf, denominator, precedence);
			}
			buf.append('}');
		} else {
			if (numerator.isTimes()) {
				convertMultiply(buf, (IAST) numerator, fPrecedence, NO_SPECIAL_CALL);
			} else {
				fFactory.convert(buf, numerator, precedence);
			}
		}

		return true;
	}

	private boolean convertMultiply(final StringBuffer buf, final IAST f, final int precedence, final int caller) {
		IExpr expr;

		if (f.size() > 1) {
			expr = f.get(1);
			if (expr.isMinusOne()) {
				if (f.size() == 2) {
					precedenceOpen(buf, precedence);
					fFactory.convert(buf, expr, fPrecedence);
				} else {
					if (caller == PLUS_CALL) {
						buf.append(" - ");
						if (f.size() == 3) {
							fFactory.convert(buf, f.get(2), fPrecedence);
							return true;
						}
					} else {
						precedenceOpen(buf, precedence);
						buf.append(" - ");
					}
				}
			} else {
				if (caller == PLUS_CALL) {
					if ((expr.isSignedNumber()) && (((ISignedNumber) expr).isNegative())) {
						buf.append(" - ");
						expr = ((ISignedNumber) expr).negate();
					} else {
						buf.append(" + ");
					}
				} else {
					precedenceOpen(buf, precedence);
				}
				fFactory.convert(buf, expr, fPrecedence);
				if (fOperator.compareTo("") != 0) {
					buf.append("\\,");
				}
			}
		}

		for (int i = 2; i < f.size(); i++) {
			fFactory.convert(buf, f.get(i), fPrecedence);
			if ((i < f.size() - 1) && (fOperator.compareTo("") != 0)) {
				buf.append("\\,");
			}
		}
		precedenceClose(buf, precedence);
		return true;
	}
}