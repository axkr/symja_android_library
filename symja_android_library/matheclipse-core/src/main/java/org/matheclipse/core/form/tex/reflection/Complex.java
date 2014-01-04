package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Complex extends AbstractOperator {

	public Complex() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Plus").getPrecedence(), "+");
	}

	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() != 3) {
			return super.convert(buf, f, precedence);
		}
		precedenceOpen(buf, precedence);
		fFactory.convert(buf, f.arg1(), 0);
		buf.append(" + ");
		fFactory.convert(buf, f.arg2(), 0);
		buf.append("\\,"); // InvisibleTimes
		buf.append("\\imag");
		return true;
	}
}
