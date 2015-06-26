package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.reflection.system.Apart;
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
				fFactory.tag(buf, "mo", "+");
			}
			fFactory.tagStart(buf, "mfrac");
			// insert numerator in buffer:
			if (!numerator.isTimes()) {
				fFactory.convert(buf, numerator, fPrecedence);
			} else {
				convertMultiply(buf, (IAST) numerator, precedence, NO_SPECIAL_CALL);
			}
			if (!denominator.isTimes()) {
				fFactory.convert(buf, denominator, 0);
			} else {
				convertMultiply(buf, (IAST) denominator, precedence, NO_SPECIAL_CALL);
			}
			fFactory.tagEnd(buf, "mfrac");
		} else {
			// if (numerator.size() <= 2) {
			if (!numerator.isTimes()) {
				convertMultiply(buf, f, precedence, caller);
			} else {
				convertMultiply(buf, (IAST) numerator, precedence, caller);
			}
		}

		return true;
	}

	private boolean convertMultiply(final StringBuffer buf, final IAST f, final int precedence, final int caller) {
		int size = f.size();
		if (size > 1) {
			IExpr arg1 = f.arg1();
			if (arg1.isMinusOne()) {
				if (size == 2) {
					fFactory.tagStart(buf, fFirstTag);
					precedenceOpen(buf, precedence);
					fFactory.convert(buf, arg1, fPrecedence);
				} else {
					if (caller == PLUS_CALL) {
						fFactory.tag(buf, "mo", "-");
						if (size == 3) {
							fFactory.convert(buf, f.arg2(), fPrecedence);
							return true;
						}
						fFactory.tagStart(buf, fFirstTag);
					} else {
						fFactory.tagStart(buf, fFirstTag);
						precedenceOpen(buf, precedence);
						fFactory.tag(buf, "mo", "-");
					}
				}
			} else if (arg1.isOne()) {
				if (size == 2) {
					fFactory.tagStart(buf, fFirstTag);
					precedenceOpen(buf, precedence);
					fFactory.convert(buf, arg1, fPrecedence);
				} else {
					if (caller == PLUS_CALL) {
						if (size == 3) {
							fFactory.convert(buf, f.arg2(), fPrecedence);
							return true;
						}
						fFactory.tagStart(buf, fFirstTag);
					} else {
						fFactory.tagStart(buf, fFirstTag);
						precedenceOpen(buf, precedence);
					}
				}
			} else {
				if (caller == PLUS_CALL) {
					if ((arg1 instanceof ISignedNumber) && (((ISignedNumber) arg1).isNegative())) {
						fFactory.tag(buf, "mo", "-");
						fFactory.tagStart(buf, fFirstTag);
						arg1 = ((ISignedNumber) arg1).negate();
					} else {
						fFactory.tag(buf, "mo", "+");
						fFactory.tagStart(buf, fFirstTag);
					}
				} else {
					fFactory.tagStart(buf, fFirstTag);
					precedenceOpen(buf, precedence);
				}
				fFactory.convert(buf, arg1, fPrecedence);
				if (fOperator.compareTo("") != 0) {
					fFactory.tag(buf, "mo", fOperator);
				}
			}
		}

		for (int i = 2; i < size; i++) {
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