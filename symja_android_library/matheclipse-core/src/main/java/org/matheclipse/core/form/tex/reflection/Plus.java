package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Operator function conversions
 */
public class Plus extends AbstractOperator {

	public Plus() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "+");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.matheclipse.core.form.mathml.IConverter#convert(java.lang.StringBuffer
	 * , org.matheclipse.parser.interfaces.IAST, int)
	 */
	@Override
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		IExpr expr;
		precedenceOpen(buf, precedence);
		final Times timesConverter = (Times) fFactory.reflection(AST2Expr.TIMES_STRING);
		for (int i = 1; i < f.size(); i++) {
			expr = f.get(i);

			if ((i > 1) && (expr instanceof IAST) && ((IAST) expr).head().toString().equals(AST2Expr.TIMES_STRING)) {
				timesConverter.convert(buf, (IAST) expr, fPrecedence, Times.PLUS_CALL);
			} else {
				if (i > 1) {
					if ((expr instanceof ISignedNumber) && (((ISignedNumber) expr).isNegative())) {
						buf.append(" - ");
						expr = ((ISignedNumber) expr).negate();
					} else {
						buf.append(" + ");
					}
				}
				fFactory.convert(buf, expr, fPrecedence);
			}
		}
		precedenceClose(buf, precedence);
		return true;
	}

}
