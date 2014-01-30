package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Operator function conversions
 */
public class Plus extends AbstractOperator {

	public Plus() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "mrow", "+");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.matheclipse.core.form.mathml.IConverter#convert(java.lang.StringBuffer, org.matheclipse.parser.interfaces.IAST, int)
	 */
	@Override
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		IExpr expr;
		fFactory.tagStart(buf, fFirstTag);
		precedenceOpen(buf, precedence);
		final Times timesConverter = (Times) fFactory.reflection(AST2Expr.TIMES_STRING);
		int size = f.size() - 1;
		for (int i = size; i > 0; i--) {
			expr = f.get(i);
			if ((i < size) && (expr instanceof IAST) && ((IAST) expr).head().equals(F.Times)) {
				timesConverter.convert(buf, (IAST) expr, fPrecedence, Times.PLUS_CALL);
			} else {
				if (i < size) {
					if ((expr instanceof ISignedNumber) && (((ISignedNumber) expr).isNegative())) {
						fFactory.tag(buf, "mo", "-");
						expr = ((ISignedNumber) expr).negate();
					} else {
						fFactory.tag(buf, "mo", "+");
					}
				}
				fFactory.convert(buf, expr, fPrecedence);
			}
		}
		precedenceClose(buf, precedence);
		fFactory.tagEnd(buf, fFirstTag);
		return true;
	}

}
