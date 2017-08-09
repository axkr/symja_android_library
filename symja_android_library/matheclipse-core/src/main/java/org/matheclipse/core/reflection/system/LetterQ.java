package org.matheclipse.core.reflection.system;

import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * LetterQ(expr)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * tests whether <code>expr</code> is a string, which only contains letters.
 * </p>
 * </blockquote>
 * <p>
 * A character is considered to be a letter if its general category type, provided by the Java method
 * <code>Character#getType()</code> is any of the following:
 * </p>
 * <ul>
 * <li><code>UPPERCASE_LETTER</code></li>
 * <li><code>LOWERCASE_LETTER</code></li>
 * <li><code>TITLECASE_LETTER</code></li>
 * <li><code>MODIFIER_LETTER</code></li>
 * <li><code>OTHER_LETTER</code></li>
 * </ul>
 * <p>
 * Not all letters have case. Many characters are letters but are neither uppercase nor lowercase nor titlecase.
 * </p>
 */
public class LetterQ extends AbstractFunctionEvaluator implements Predicate<IExpr> {

	public LetterQ() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		if (!(ast.arg1() instanceof IStringX)) {
			throw new WrongNumberOfArguments(ast, 1, ast.size() - 1);
		}

		return F.bool(test(ast.arg1()));
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

	@Override
	public boolean test(final IExpr obj) {
		final String str = obj.toString();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (!(Character.isLetter(ch))) {
				return false;
			}
		}
		return true;
	}
}
