package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y;
import static org.matheclipse.core.expression.F.y_;

import org.matheclipse.core.builtin.Structure;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;
import org.matheclipse.core.visit.VisitorExpr;

/**
 * <pre>
 * TrigReduce(expr)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * rewrites products and powers of trigonometric functions in <code>expr</code> in terms of trigonometric functions with
 * combined arguments.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; TrigReduce(2*Sin(x)*Cos(y))
 * Sin(-y+x)+Sin(y+x)
 * </pre>
 */
public class TrigReduce extends AbstractEvaluator {
	private static HashedOrderlessMatcher ORDERLESS_MATCHER = new HashedOrderlessMatcher();

	public TrigReduce() {
	}

	static class TrigReduceVisitor extends VisitorExpr {
		final EvalEngine fEngine;

		public TrigReduceVisitor(EvalEngine engine) {
			super();
			fEngine = engine;
		}

		@Override
		public IExpr visit(IASTMutable ast) {
			if (ast.isTimes()) {
				IAST result = ORDERLESS_MATCHER.evaluate(ast, fEngine);
				if (result.isPresent()) {
					return result;
				}
			} else if (ast.isPower()) {
				if (ast.base().isAST() && ast.exponent().isInteger()) {
					IInteger n = (IInteger) ast.exponent();
					if (n.isPositive()) {
						IAST base = (IAST) ast.base();
						IExpr x;
						if (base.isSin()) {
							x = base.arg1();
							// 1/2 * (1-Cos[2*x])*Sin[x]^(n-2)
							return Times(C1D2, Subtract(F.C1, Cos(Times(C2, x))), Power(Sin(x), n.subtract(C2)));
						} else if (base.isCos()) {
							x = base.arg1();
							// 1/2 * (1+Cos[2*x])*Cos[x]^(n-2)
							return Times(C1D2, Plus(F.C1, Cos(Times(C2, x))), Power(Cos(x), n.subtract(C2)));
						}
					}
				}
			}
			return visitAST(ast);
		}
	}

	/**
	 * Transform products of trigonometric functions into &quot;linear form&quot;.
	 * 
	 * <a href=
	 * "http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Product-to-sum_and_sum-to-product_identities"
	 * >List of trigonometric identities - Product-to-sum and sum-to-product identities</a>
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);
		IExpr temp = Structure.threadLogicEquationOperators(ast.arg1(), ast, 1);
		if (temp.isPresent()) {
			return temp;
		}

		TrigReduceVisitor trigReduceVisitor = new TrigReduceVisitor(engine);
		temp = ast.arg1();
		IExpr result = temp;
		while (temp.isPresent()) {
			result = temp;
			if (temp.isPlus() || temp.isTimes() || temp.isPower()) {
				result = F.evalExpand(temp);
			}

			temp = result.accept(trigReduceVisitor);
			if (temp.isPresent()) {
				result = temp;
			}
		}
		return result;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		ORDERLESS_MATCHER.defineHashRule(Sin(x_), Cos(y_),
				// [$ 1/2 * (Sin(x+y)+Sin(x-y)) $]
				F.Times(F.C1D2, F.Plus(F.Sin(F.Plus(x, y)), F.Sin(F.Plus(x, F.Negate(y)))))); // $$);
		ORDERLESS_MATCHER.defineHashRule(Sin(x_), Sin(y_),
				// [$ 1/2 * (Cos(x-y)-Cos(x+y)) $]
				F.Times(F.C1D2, F.Plus(F.Cos(F.Plus(x, F.Negate(y))), F.Negate(F.Cos(F.Plus(x, y)))))); // $$);
		ORDERLESS_MATCHER.defineHashRule(Cos(x_), Cos(y_),
				// [$ 1/2 * (Cos(x+y)+Cos(x-y)) $]
				F.Times(F.C1D2, F.Plus(F.Cos(F.Plus(x, y)), F.Cos(F.Plus(x, F.Negate(y)))))); // $$);
		ORDERLESS_MATCHER.defineHashRule(Sinh(x_), Cosh(y_),
				// [$ 1/2 * (Sinh(x-y)+Sinh(x+y)) $]
				F.Times(F.C1D2, F.Plus(F.Sinh(F.Plus(x, F.Negate(y))), F.Sinh(F.Plus(x, y))))); // $$);
		ORDERLESS_MATCHER.defineHashRule(Sin(x_), Tan(y_),
				// [$ 1/2 * (Cos(x-y)-Cos(x+y)) * Sec(y) $]
				F.Times(F.C1D2, F.Plus(F.Cos(F.Plus(x, F.Negate(y))), F.Negate(F.Cos(F.Plus(x, y)))), F.Sec(y))); // $$);
		ORDERLESS_MATCHER.defineHashRule(Cos(x_), Tan(y_),
				// [$ -(1/2) * (Sin(x-y)-Sin(x+y)) * Sec(y) $]
				F.Times(F.CN1D2, F.Plus(F.Sin(F.Plus(x, F.Negate(y))), F.Negate(F.Sin(F.Plus(x, y)))), F.Sec(y))); // $$);
		ORDERLESS_MATCHER.defineHashRule(Cos(x_), Cot(y_),
				// [$ 1/2 * (Cos(x-y)+Cos(x+y)) * Csc(y) $]
				F.Times(F.C1D2, F.Plus(F.Cos(F.Plus(x, F.Negate(y))), F.Cos(F.Plus(x, y))), F.Csc(y))); // $$);
		ORDERLESS_MATCHER.defineHashRule(Sin(x_), Cot(y_),
				// [$ 1/2 * (Sin(x-y)+Sin(x+y)) * Csc(y) $]
				F.Times(F.C1D2, F.Plus(F.Sin(F.Plus(x, F.Negate(y))), F.Sin(F.Plus(x, y))), F.Csc(y))); // $$);

		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
