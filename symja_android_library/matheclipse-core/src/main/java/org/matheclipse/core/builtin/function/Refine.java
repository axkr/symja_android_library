package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>Refine(expression, assumptions)
 * </pre>
 * <blockquote><p>evaluate the <code>expression</code> for the given <code>assumptions</code>.</p>
 * </blockquote>
 * <h3>Examples</h3>
 * <pre>&gt;&gt; Refine(Abs(n+Abs(m)), n&gt;=0)
 * Abs(m)+n
 * 
 * &gt;&gt; Refine(-Infinity&lt;x, x&gt;0)
 * True 
 * 
 * &gt;&gt; Refine(Max(Infinity,x,y), x&gt;0)
 * Max(Infinity,y)
 * 
 * &gt;&gt; Refine(Sin(k*Pi), Element(k, Integers))
 * 0
 * 
 * &gt;&gt; Sin(k*Pi)
 * Sin(k*Pi)
 * </pre>
 */
public class Refine extends AbstractCoreFunctionEvaluator {

	public Refine() {
		// default ctor
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);

		final IExpr arg2 = engine.evaluate(ast.arg2());
		IAssumptions assumptions = determineAssumptions(ast.topHead(), arg2, engine);
		if (assumptions != null) {
			return refineAssumptions(ast.arg1(), assumptions, engine);
		}
		return F.NIL;
	}

	public static IAssumptions determineAssumptions(final ISymbol symbol, final IExpr arg2, EvalEngine engine) {
		final Options options = new Options(symbol, arg2, engine);
		IExpr option = options.getOption("Assumptions");
		if (option.isPresent()) {
			return Assumptions.getInstance(option);
		} else {
			return Assumptions.getInstance(arg2);
		}
	}

	public static IExpr refineAssumptions(final IExpr expr, IAssumptions assumptions, EvalEngine engine) {
		try {
			engine.setAssumptions(assumptions);
			// System.out.println(expr.toString());
			return engine.evalWithoutNumericReset(expr);
		} finally {
			engine.setAssumptions(null);
		}
	}

}