package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Times extends AbstractOperator {
	public final static int NO_SPECIAL_CALL = 0;

	public final static int PLUS_CALL = 1;

	public static Times CONST = new Times();

	public Times() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "\\,");
	}

	/**
	 * Converts a given function into the corresponding TeX output
	 * 
	 * @param buf
	 *            StringBuilder for TeX output
	 * @param f
	 *            The math function which should be converted to TeX
	 */
	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		return convertTimesFraction(buf, f, precedence, NO_SPECIAL_CALL);
	}

	/**
	 * Try to split a given <code>Times[...]</code> function into nominator and
	 * denominator and add the corresponding TeX output
	 * 
	 * @param buf
	 *            StringBuilder for TeX output
	 * @param f
	 *            The math function which should be converted to TeX
	 * @precedence
	 * @caller
	 */
	public boolean convertTimesFraction(final StringBuilder buf, final IAST f, final int precedence, final int caller) {
		IExpr[] parts = Algebra.fractionalPartsTimesPower(f, false, true, false, false);
		if (parts == null) {
			convertTimesOperator(buf, f, precedence, caller);
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
				convertTimesOperator(buf, (IAST) numerator, fPrecedence, NO_SPECIAL_CALL);
			} else {
				fFactory.convert(buf, numerator, precedence);
			}
			buf.append("}{");
			// insert denominator in buffer:
			if (denominator.isTimes()) {
				convertTimesOperator(buf, (IAST) denominator, fPrecedence, NO_SPECIAL_CALL);
			} else {
				fFactory.convert(buf, denominator, precedence);
			}
			buf.append('}');
		} else {
			if (numerator.isTimes()) {
				convertTimesOperator(buf, (IAST) numerator, fPrecedence, NO_SPECIAL_CALL);
			} else {
				fFactory.convert(buf, numerator, precedence);
			}
		}

		return true;
	}

	/**
	 * Does the TeX Form of <code>expr</code> begin with a number digit?
	 * 
	 * @param expr
	 * @return
	 */
	private boolean isTeXNumberDigit(IExpr expr) {
		if (expr.isNumber()) {
			return true;
		}
		if (expr.isPower() && ((IAST) expr).arg1().isNumber()) {
			IAST power = (IAST) expr;
			if (power.arg1().isNumber() && !power.arg2().isFraction()) {
				return true;
			}
		}
		return false;
	}

	private boolean convertTimesOperator(final StringBuilder buf, final IAST timesAST, final int precedence,
			final int caller) {
		int size = timesAST.size();

		if (size > 1) {
			IExpr arg1 = timesAST.arg1();
			if (arg1.isMinusOne()) {
				if (size == 2) {
					precedenceOpen(buf, precedence);
					fFactory.convert(buf, arg1, fPrecedence);
				} else {
					if (caller == PLUS_CALL) {
						buf.append("-");
						if (size == 3) {
							fFactory.convert(buf, timesAST.arg2(), fPrecedence);
							return true;
						}
					} else {
						precedenceOpen(buf, precedence);
						buf.append(" - ");
					}
				}
			} else if (arg1.isOne()) {
				if (size == 2) {
					precedenceOpen(buf, precedence);
					fFactory.convert(buf, arg1, fPrecedence);
				} else {
					if (caller == PLUS_CALL) {
						if (size == 3) {
							buf.append("+");
							fFactory.convert(buf, timesAST.arg2(), fPrecedence);
							return true;
						}
					} else {
						precedenceOpen(buf, precedence);
					}
				}
			} else {
				if (caller == PLUS_CALL) {
					if ((arg1.isSignedNumber()) && (((ISignedNumber) arg1).isNegative())) {
						buf.append(" - ");
						arg1 = ((ISignedNumber) arg1).opposite();
					} else {
						buf.append("+");
					}
				} else {
					precedenceOpen(buf, precedence);
				}
				fFactory.convert(buf, arg1, fPrecedence);
				if (fOperator.compareTo("") != 0) {
					if (size > 2) {
						if (timesAST.arg1().isNumber() && isTeXNumberDigit(timesAST.arg2())) {
							// Issue #67, #117: if we have 2 TeX number
							// expressions we use
							// the \cdot operator see
							// http://tex.stackexchange.com/questions/40794/when-should-cdot-be-used-to-indicate-multiplication
							buf.append("\\cdot ");
						} else {
							buf.append("\\,");
						}
					}
				}
			}
		}

		for (int i = 2; i < size; i++) {
			fFactory.convert(buf, timesAST.get(i), fPrecedence);
			if ((i < timesAST.argSize()) && (fOperator.compareTo("") != 0)) {
				if (timesAST.get(1).isNumber() && isTeXNumberDigit(timesAST.get(i + 1))) {
					// Issue #67, #117: if we have 2 TeX number expressions we
					// use
					// the \cdot operator see
					// http://tex.stackexchange.com/questions/40794/when-should-cdot-be-used-to-indicate-multiplication
					buf.append("\\cdot ");
				} else {
					buf.append("\\,");
				}
			}
		}
		precedenceClose(buf, precedence);
		return true;
	}
}