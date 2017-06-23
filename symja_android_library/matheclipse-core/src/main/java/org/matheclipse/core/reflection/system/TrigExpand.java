package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.evalExpandAll;

import java.util.function.Function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorPlusTimesPowerReplaceAll;

/**
 * Expands the argument of sine and cosine functions.
 * 
 * <a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities" >List of trigonometric identities</a>
 */
public class TrigExpand extends AbstractEvaluator {

	private final static Function<IExpr, IExpr> function = new TrigExpandFunction();
	private final static VisitorPlusTimesPowerReplaceAll visitor = new VisitorPlusTimesPowerReplaceAll(function);

	private final static class TrigExpandFunction implements Function<IExpr, IExpr> {
		@Override
		public IExpr apply(IExpr ast) {
			IExpr temp = F.NIL;
			if (ast.isAST1()) {
				if (ast.getAt(1).isPlus()) {
					if (ast.isSin()) {
						temp = expandSinPlus((IAST) ast.getAt(1), 1);
					} else if (ast.isCos()) {
						temp = expandCosPlus((IAST) ast.getAt(1), 1);
					}
				} else if (ast.getAt(1).isTimes()) {
					IAST timesAST = (IAST) ast.getAt(1);
					if (timesAST.arg1().isInteger()) {
						IInteger n = (IInteger) timesAST.arg1();
						if (n.compareInt(0) > 0) {
							IExpr theta = timesAST.removeAtClone(1).getOneIdentity(F.C1);
							if (ast.isSin()) {
								// Sin(n*theta)
								return Sum(
										Times(Times(Times(Power(CN1, Times(Plus(F.i, CN1), C1D2)), Binomial(n, F.i)),
												Power(Cos(theta), Plus(n, Times(CN1, F.i)))), Power(Sin(theta), F.i)),
										List(F.i, C1, n, C2));
							} else if (ast.isCos()) {
								// Cos(n*theta)
								return Sum(
										Times(Times(Times(Power(CN1, Times(F.i, C1D2)), Binomial(n, F.i)),
												Power(Cos(theta), Plus(n, Times(CN1, F.i)))), Power(Sin(theta), F.i)),
										List(F.i, C0, n, C2));
							}
						}
					}
				}
			}
			return temp;
		}

		private static IExpr expandSinPlus(IAST plusAST, int startPosition) {
			if (startPosition > plusAST.size() - 2) {
				return F.NIL;
			}
			IAST result = Plus();
			if (startPosition == plusAST.size() - 2) {
				result.append(Times(Sin(plusAST.get(startPosition)), Cos(plusAST.get(startPosition + 1))));
				result.append(Times(Cos(plusAST.get(startPosition)), Sin(plusAST.get(startPosition + 1))));
			} else {
				result.append(Times(Sin(plusAST.get(startPosition)), expandCosPlus(plusAST, startPosition + 1)));
				result.append(Times(Cos(plusAST.get(startPosition)), expandSinPlus(plusAST, startPosition + 1)));
			}
			return result;
		}

		private static IExpr expandCosPlus(IAST plusAST, int startPosition) {
			if (startPosition > plusAST.size() - 2) {
				return F.NIL;
			}
			IAST result = Plus();
			if (startPosition == plusAST.size() - 2) {
				result.append(Times(Cos(plusAST.get(startPosition)), Cos(plusAST.get(startPosition + 1))));
				result.append(Times(CN1, Sin(plusAST.get(startPosition)), Sin(plusAST.get(startPosition + 1))));
			} else {
				result.append(Times(Cos(plusAST.get(startPosition)), expandCosPlus(plusAST, startPosition + 1)));
				result.append(Times(CN1, Sin(plusAST.get(startPosition)), expandSinPlus(plusAST, startPosition + 1)));
			}
			return result;
		}
	}

	public TrigExpand() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr result;
		IExpr temp = ast.arg1();
		do {
			result = evalExpandAll(temp);
			temp = result.accept(visitor);
		} while (temp.isPresent());
		return result;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
