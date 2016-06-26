package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;

public class Limit extends AbstractConverter {

	public Limit() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.isAST2() && f.arg2().isRuleAST()) {
			final IAST rule = (IAST) f.arg2();
			buf.append("\\lim_{");
			fFactory.convertSubExpr(buf, rule.arg1(), 0);
			buf.append("\\to ");
			fFactory.convertSubExpr(buf, rule.arg2(), 0);
			buf.append(" }\\,");
			fFactory.convertSubExpr(buf, f.arg1(), 0);

			// buf.append("\\mathop {\\lim }\\limits_{");
			// fFactory.convert(buf, rule.arg1(), 0);
			// buf.append(" \\to ");
			// fFactory.convert(buf, rule.arg2(), 0);
			// buf.append('}');
			// fFactory.convert(buf, f.arg1(), 0);
			return true;
		}
		return false;
	}
}