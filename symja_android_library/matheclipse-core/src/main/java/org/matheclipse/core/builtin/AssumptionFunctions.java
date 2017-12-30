package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class AssumptionFunctions {
	static {
		F.Element.setEvaluator(new Element());
		F.Refine.setEvaluator(new Refine());
	}

	/**
	 * Element(x, domain) - test if x is in the given domain.
	 * 
	 */
	private static class Element extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			final IExpr arg2 = engine.evaluate(ast.arg2());
			IExpr truthValue;
			if (arg2.isSymbol()) {
				final IExpr arg1 = engine.evaluate(ast.arg1());
				if (arg1.isAST(F.Alternatives)) {
					IAST list = (IAST) arg1;
					for (int i = 1; i < list.size(); i++) {
						truthValue = assumeDomain(arg1, (ISymbol) arg2);
						if (truthValue.isPresent()) {
							return truthValue;
						}
					}
					return F.True;
				} else {
					return assumeDomain(arg1, (ISymbol) arg2);
				}
			}
			return F.NIL;
		}

		private IExpr assumeDomain(final IExpr arg1, final ISymbol arg2) {
			if (arg2.equals(F.Algebraics)) {
				ISymbol truthValue = AbstractAssumptions.assumeAlgebraic(arg1);
				if (truthValue != null) {
					return truthValue;
				}
			} else if (arg2.equals(F.Booleans)) {
				ISymbol truthValue = AbstractAssumptions.assumeBoolean(arg1);
				if (truthValue != null) {
					return truthValue;
				}
			} else if (arg2.equals(F.Complexes)) {
				ISymbol truthValue = AbstractAssumptions.assumeComplex(arg1);
				if (truthValue != null) {
					return truthValue;
				}
			} else if (arg2.equals(F.Integers)) {
				ISymbol truthValue = AbstractAssumptions.assumeInteger(arg1);
				if (truthValue != null) {
					return truthValue;
				}
			} else if (arg2.equals(F.Primes)) {
				ISymbol truthValue = AbstractAssumptions.assumePrime(arg1);
				if (truthValue != null) {
					return truthValue;
				}
			} else if (arg2.equals(F.Rationals)) {
				ISymbol truthValue = AbstractAssumptions.assumeRational(arg1);
				if (truthValue != null) {
					return truthValue;
				}
			} else if (arg2.equals(F.Reals)) {
				ISymbol truthValue = AbstractAssumptions.assumeReal(arg1);
				if (truthValue != null) {
					return truthValue;
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Refine(expression, assumptions)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluate the <code>expression</code> for the given <code>assumptions</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Refine(Abs(n+Abs(m)), n&gt;=0)
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
	private static class Refine extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.size() == 3) {
				final IExpr arg2 = engine.evaluate(ast.arg2());
				IAssumptions assumptions = determineAssumptions(ast.topHead(), arg2, engine);
				if (assumptions != null) {
					return refineAssumptions(ast.arg1(), assumptions, engine);
				}
			}
			return ast.arg1();
		}

		

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

	private final static AssumptionFunctions CONST = new AssumptionFunctions();

	public static AssumptionFunctions initialize() {
		return CONST;
	}

	private AssumptionFunctions() {

	}

}
