package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Operator function conversions
 */
public class Plus extends AbstractOperator {

	public Plus() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "+");
	}

	/** {@inheritDoc} */
	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		IExpr expr;
		precedenceOpen(buf, precedence);
		final Times timesConverter = new org.matheclipse.core.form.tex.reflection.Times();
		timesConverter.setFactory(fFactory);
		for (int i = 1; i < f.size(); i++) {
			expr = f.get(i);

			if ((i > 1) && (expr instanceof IAST) && expr.isTimes()) {
				timesConverter.convertTimesFraction(buf, (IAST) expr, fPrecedence, Times.PLUS_CALL);
			} else {
				if (i > 1) {
					if (expr.isNumber() && (((INumber) expr).complexSign() < 0)) {
						buf.append("-");
						expr = ((INumber) expr).negate();
					} else if (expr.isNegativeSigned()) {
					} else {
						buf.append("+");
					}
				}
				fFactory.convert(buf, expr, fPrecedence);
			}
		}
		precedenceClose(buf, precedence);
		return true;
	}

}
