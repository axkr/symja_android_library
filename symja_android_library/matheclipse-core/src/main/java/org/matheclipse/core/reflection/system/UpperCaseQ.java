package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Predicate;

/**
 * Returns <code>True</code>, if the given expression is a string which only
 * contains upper case characters
 *
 */
public class UpperCaseQ extends AbstractFunctionEvaluator implements Predicate<IExpr> {

	public UpperCaseQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		if (!(ast.arg1() instanceof IStringX)) {
			throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
		}
		
		return F.bool(apply(ast.arg1()));
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	@Override
	public boolean apply(final IExpr obj) {
		final String str = obj.toString();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (!(Character.isUpperCase(ch))) {
				return false;
			}
		}
		return true;
	}
}
