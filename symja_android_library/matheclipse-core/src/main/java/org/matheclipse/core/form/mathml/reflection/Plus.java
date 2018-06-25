package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.mathml.MMLOperator;
import org.matheclipse.core.form.mathml.MathMLFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Operator function conversions
 */
public class Plus extends MMLOperator {
	public final static Plus CONST = new Plus();

	public Plus() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "mrow", "+");
	}

	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		IExpr expr;
		fFactory.tagStart(buf, fFirstTag);
		precedenceOpen(buf, precedence);
		final Times timesConverter = new org.matheclipse.core.form.mathml.reflection.Times();
		timesConverter.setFactory(fFactory);
		int size = f.argSize();
		for (int i = size; i > 0; i--) {
			expr = f.get(i);
			if ((i < size) && expr.isAST(F.Times)) {
				timesConverter.convertTimesFraction(buf, (IAST) expr, fPrecedence, MathMLFormFactory.PLUS_CALL);
			} else {
				if (i < size) {
					if (expr.isReal() && expr.isNegative()) {
						fFactory.tag(buf, "mo", "-");
						expr = ((ISignedNumber) expr).negate();
					} else {
						fFactory.tag(buf, "mo", "+");
					}
				}
				fFactory.convert(buf, expr, fPrecedence, false);
			}
		}
		precedenceClose(buf, precedence);
		fFactory.tagEnd(buf, fFirstTag);
		return true;
	}

}
