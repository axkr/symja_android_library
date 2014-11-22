package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Power extends AbstractOperator {

	public Power() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Power").getPrecedence(), "^");
	}

	public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
		if (f.size() != 3) {
			return super.convert(buf, f, precedence);
		}
		IExpr arg1 = f.arg1();
		IExpr arg2 = f.arg2();
		if (arg2.isRationalValue(F.C1D2)) {
			buf.append("\\sqrt{");
			fFactory.convert(buf, arg1, fPrecedence);
			buf.append('}');
			return true;
		}
		if (arg2.isFraction()) {
			if (((IFraction) arg2).getNumerator().isOne()) {
				buf.append("\\sqrt[");
				fFactory.convert(buf, ((IFraction) arg2).getDenominator(), fPrecedence);
				buf.append("]{");
				fFactory.convert(buf, arg1, fPrecedence);
				buf.append('}');
				return true;
			}
		}

		precedenceOpen(buf, precedence);
		fFactory.convertSubExpr(buf, arg1, fPrecedence);
		if (fOperator.compareTo("") != 0) {
			buf.append(fOperator);
		}
		fFactory.convertSubExpr(buf, arg2, 0);
		precedenceClose(buf, precedence);
		return true;
	}
}
