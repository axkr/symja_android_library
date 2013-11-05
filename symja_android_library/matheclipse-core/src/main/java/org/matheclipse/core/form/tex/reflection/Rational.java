package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Rational extends AbstractOperator {

	public Rational() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getPrecedence(), "/");
	}

	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() != 3) {
			return super.convert(buf, f, precedence);
		}
		precedenceOpen(buf, precedence);
		buf.append("\\frac{");
		fFactory.convert(buf, f.arg1(), fPrecedence);
		buf.append("}{");  
		fFactory.convert(buf, f.arg2(), fPrecedence);
		buf.append('}');
		precedenceClose(buf, precedence);
		return true;
	}
}
