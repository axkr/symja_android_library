package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Power extends AbstractOperator {

	public Power() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Power").getPrecedence(), "^");
	}

	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() != 3) {
			return super.convert(buf, f, precedence);
		}
		precedenceOpen(buf, precedence);
		buf.append('{');
		fFactory.convert(buf, f.get(1), fPrecedence);
		buf.append('}');
		if (fOperator.compareTo("") != 0) {
			buf.append(fOperator);
		}
		buf.append('{');
		fFactory.convert(buf, f.get(2), fPrecedence);
		buf.append('}');
		precedenceClose(buf, precedence);
		return true;
	}
}
