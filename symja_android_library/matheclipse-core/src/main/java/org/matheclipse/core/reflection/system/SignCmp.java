package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.function.NumericQ;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Gets the signum value of a complex number
 * 
 * @return 0 for <code>this == 0</code>;<br/>
 *         +1 for <code>real(this) &gt; 0 || ( real(this) == 0 &amp;&amp; imaginary(this) &gt; 0 )</code> ;<br/>
 *         -1 for <code>real(this) &lt; 0 || ( real(this) == 0 &amp;&amp; imaginary(this) &lt; 0 )
 */
public class SignCmp implements IFunctionEvaluator {

	public SignCmp() {
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		IExpr arg1 = ast.arg1();
		INumber number = arg1.evalNumber();
		if (number != null) {
			final int signum = number.complexSign();
			return F.integer(signum);
		}
		return null;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
