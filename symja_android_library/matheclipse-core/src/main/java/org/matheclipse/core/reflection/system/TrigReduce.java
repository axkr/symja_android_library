package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.HashedOrderlessMatcher;
import org.matheclipse.core.visit.VisitorExpr;

/**
 * Transform products of trigonometric functions into &quot;linear form&quot;.
 * 
 * <a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Product-to-sum_and_sum-to-product_identities" >List of
 * trigonometric identities - Product-to-sum and sum-to-product identities</a>
 */
public class TrigReduce implements IFunctionEvaluator {
	private static HashedOrderlessMatcher ORDERLESS_MATCHER = new HashedOrderlessMatcher(true);

	public TrigReduce() {
	}

	class TrigReduceVisitor extends VisitorExpr {
		public TrigReduceVisitor() {
			super();
		}

		@Override
		public IExpr visit(IAST ast) {
			if (ast.isTimes()) {
				IAST result = ORDERLESS_MATCHER.evaluate(ast);
				if (result != null) {
					return result;
				}
			} else if (ast.isPower()) {
				if (ast.get(1).isAST() && ast.get(2).isInteger()) {
					IInteger n = (IInteger) ast.get(2);
					if (n.isPositive()) {
						IAST powerArg1 = (IAST) ast.get(1);
						IExpr x;
						if (powerArg1.isSin()) {
							x = powerArg1.get(1);
							// (1/2-Cos[2*x]/2)*Sin[x]^(n-2)
							return Times(Subtract(C1D2, Times(C1D2, Cos(Times(C2, x)))), Power(Sin(x), n.subtract(C2)));
						} else if (powerArg1.isCos()) {
							x = powerArg1.get(1);
							// (1/2+Cos[2*x]/2)*Cos[x]^(n-2)
							return Times(Plus(C1D2, Times(C1D2, Cos(Times(C2, x)))), Power(Cos(x), n.subtract(C2)));
						}
					}
				}
			}
			return visitAST(ast);
		}
	}

	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);

		TrigReduceVisitor trigReduceVisitor = new TrigReduceVisitor();
		IExpr temp = ast.get(1);
		IExpr result = temp;
		while (temp != null) {
			result = temp;
			if (temp.isPlus() || temp.isTimes() || temp.isPower()) {
				result = F.evalExpand(temp);
			}

			temp = result.accept(trigReduceVisitor);
			if (temp != null) {
				result = temp;
			}
		}
		return result;
	}

	public IExpr numericEval(final IAST ast) {
		return evaluate(ast);
	}

	public void setUp(final ISymbol symbol) {
		ORDERLESS_MATCHER.setUpHashRule("Sin[x_]", "Cos[y_]", "Sin[x+y]/2+Sin[x-y]/2");
		ORDERLESS_MATCHER.setUpHashRule("Sin[x_]", "Sin[y_]", "Cos[x-y]/2-Cos[x+y]/2");
		ORDERLESS_MATCHER.setUpHashRule("Cos[x_]", "Cos[y_]", "Cos[x+y]/2+Cos[x-y]/2");
		symbol.setAttributes(ISymbol.LISTABLE);
	}

}
