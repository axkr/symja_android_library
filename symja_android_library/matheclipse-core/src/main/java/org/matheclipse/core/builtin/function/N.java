package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * N(expr)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the numerical value of <code>expr</code>.<br />
 * </p>
 * </blockquote>
 * 
 * <pre>
 * N(expr, precision)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * evaluates <code>expr</code> numerically with a precision of <code>prec</code>
 * digits.<br />
 * </p>
 * </blockquote>
 * <p>
 * <strong>Note</strong>: the upper case identifier <code>N</code> is different
 * from the lower case identifier <code>n</code>.
 * </p>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; N(Pi)
 * 3.141592653589793
 * 
 * &gt;&gt; N(Pi, 50)
 * 3.1415926535897932384626433832795028841971693993751
 * 
 * &gt;&gt; N(1/7)
 * 0.14285714285714285
 * 
 * &gt;&gt; N(1/7, 5)
 * 1.4285714285714285714e-1
 * </pre>
 */
public class N extends AbstractCoreFunctionEvaluator {

	public N() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		return numericEval(ast, engine);
	}

	@Override
	public IExpr numericEval(final IAST ast, EvalEngine engine) {
		Validate.checkRange(ast, 2, 3);

		final boolean numericMode = engine.isNumericMode();
		final int oldPrecision = engine.getNumericPrecision();
		try {
			int numericPrecision = Config.MACHINE_PRECISION;
			if (ast.isAST2()) {
				numericPrecision = Validate.checkIntType(ast.arg2());
			}
			engine.setNumericMode(true, numericPrecision);
			return engine.evalWithoutNumericReset(ast.arg1());
		} finally {
			engine.setNumericMode(numericMode);
			engine.setNumericPrecision(oldPrecision);
		}
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.HOLDALL);
	}

}
