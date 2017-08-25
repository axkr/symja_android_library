package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.ISymbol;

public class Sum extends AbstractConverter {

	public Sum() {
	}

	/** {@inheritDoc} */
	public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
		if (f.size() >= 3) {
			return iteratorStep(buf, "\\sum", f, 2);
		}
		return false;
	}

	/**
	 * See <a href="http://en.wikibooks.org/wiki/LaTeX/Mathematics">Wikibooks -
	 * LaTeX/Mathematics</a>
	 * 
	 * @param buf
	 * @param mathSymbol
	 *            the symbol for Sum or Product expressions
	 * @param f
	 * @param i
	 * 
	 * @return <code>true</code> if the expression could be transformed to LaTeX
	 */
	public boolean iteratorStep(final StringBuilder buf, final String mathSymbol, final IAST f, int i) {
		if (i >= f.size()) {
			buf.append(" ");
			fFactory.convertSubExpr(buf, f.arg1(), 0);
			return true;
		}
		if (f.get(i).isList()) {
			IIterator iterator = Iterator.create((IAST) f.get(i), EvalEngine.get());
			if (iterator.isValidVariable() && iterator.getStep().isOne()) {
				buf.append(mathSymbol);
				buf.append("_{");
				fFactory.convertSubExpr(buf, iterator.getVariable(), 0);
				buf.append(" = ");
				fFactory.convertSubExpr(buf, iterator.getLowerLimit(), 0);
				buf.append("}^{");
				fFactory.convert(buf, iterator.getUpperLimit(), 0);
				buf.append('}');
				if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
					return false;
				}
				return true;
			}
		} else if (f.get(i).isSymbol()) {
			ISymbol symbol = (ISymbol) f.get(i);
			buf.append(mathSymbol);
			buf.append("_{");
			fFactory.convertSymbol(buf, symbol);
			buf.append("}");
			if (!iteratorStep(buf, mathSymbol, f, i + 1)) {
				return false;
			}
			return true;
		}
		return false;
	}
}