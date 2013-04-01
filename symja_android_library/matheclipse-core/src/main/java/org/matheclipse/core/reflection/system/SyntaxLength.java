package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.SyntaxError;

/**
 *
 */
public class SyntaxLength extends AbstractFunctionEvaluator {

	public SyntaxLength() {
	} 

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast,2);
		if (!(ast.get(1) instanceof IStringX)) {
			return null;
		}

		final String str = ast.get(1).toString();
		try {
			new Parser().parse(str);
		} catch (final SyntaxError e) {
			return F.integer(e.getStartOffset());
		}
		return F.integer(str.length());
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
