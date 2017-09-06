package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Complex extends AbstractOperator {

	public Complex() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "+");
	}

	@Override
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() != 3) {
			return super.convert(buf, f, precedence);
		}
		precedenceOpen(buf, precedence);
		IExpr arg1 = f.arg1();
		boolean reZero = arg1.isZero();
		IExpr arg2 = f.arg2();
		boolean imZero = arg2.isZero();

		if (!reZero) {
			fFactory.convert(buf, arg1, 0);
		}
		if (!imZero) {
			if (!reZero && !arg2.isNegativeSigned()) {
				buf.append(" + ");
			}
			if (arg2.isMinusOne()) {
				buf.append(" - ");
			} else if (!arg2.isOne()) {
				fFactory.convert(buf, arg2, 0);
				buf.append("\\,"); // InvisibleTimes
			}
			buf.append("\\imag");
		}
		return true;
	}
}
