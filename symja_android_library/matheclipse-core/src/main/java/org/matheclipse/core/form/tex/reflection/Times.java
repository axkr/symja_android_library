package org.matheclipse.core.form.tex.reflection;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.NumberUtil;
import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Times extends AbstractOperator {
	public final static int NO_SPECIAL_CALL = 0;

	public final static int PLUS_CALL = 1;

	public Times() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "\\,");
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
		final IAST numerator = Times();
		final IAST denominator = Times( );
		for (int i = 1; i < f.size(); i++) {
			if ((f.get(i).isAST()) && f.get(i).isPower()) {
				// filter negative Powers:
				final IAST p = (IAST) f.get(i);
				if ((p.size() == 3) && (p.get(2) instanceof ISignedNumber) && ((ISignedNumber) p.get(2)).isNegative()) {
					if (NumberUtil.isMinusOne(p.get(2))) {
						// x_^(-1) ?
						denominator.add(p.get(1));
						continue;
					}
					denominator.add(Power(p.get(1), Times(CN1, p.get(2))));
					continue;
				}
			}
			if (!NumberUtil.isOne(f.get(i))) {
				numerator.add(f.get(i));
			}
		}
		// do the output:
		if (denominator.size() > 1) {
			if (caller == PLUS_CALL) {
				buf.append('+');
			}
			// fFactory.tagStart(buf, "mfrac");
			buf.append("\\frac{");
			// insert numerator in buffer:
			if (numerator.size() != 1) {
				if (numerator.size() == 2) {
					fFactory.convert(buf, numerator.get(1), 0);
				} else {
					fFactory.convert(buf, numerator, fPrecedence);
				}

			} else {
				buf.append('1');
			}
			buf.append("}{");
			if (denominator.size() == 2) {
				fFactory.convert(buf, denominator.get(1), 0);
			} else {
				fFactory.convert(buf, denominator, fPrecedence);
			}
			buf.append('}');
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