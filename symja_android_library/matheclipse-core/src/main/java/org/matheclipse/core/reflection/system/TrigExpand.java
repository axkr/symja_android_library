package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.evalExpandAll;

import java.util.function.Function;

import org.matheclipse.core.builtin.Structure;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorPlusTimesPowerReplaceAll;

/**
 * <pre>
 * TrigExpand(expr)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * expands out trigonometric expressions in <code>expr</code>.
 * </p>
 * </blockquote>
 * <h3>Examples</h3>
 * 
 * <pre>
 * &gt;&gt; TrigExpand(Sin(x+y))
 * Cos(x)*Sin(y)+Cos(y)*Sin(x)
 * </pre>
 */
public class TrigExpand extends AbstractEvaluator {

	private final static Function<IExpr, IExpr> function = new TrigExpandFunction();
	private final static VisitorPlusTimesPowerReplaceAll visitor = new VisitorPlusTimesPowerReplaceAll(function);

	private final static class TrigExpandFunction implements Function<IExpr, IExpr> {
		@Override
		public IExpr apply(IExpr ast) {
			if (ast.isAST1()) {
				if (ast.getAt(1).isPlus()) {
					IAST plusAST = (IAST) ast.getAt(1);
					return expandPlus((IAST) ast, plusAST);
				} else if (ast.getAt(1).isTimes()) {
					IAST timesAST = (IAST) ast.getAt(1);
					return expandTimes((IAST) ast, timesAST);
				}
			}
			return F.NIL;
		}

		/**
		 * Expand <code>f(a+b+c+...)</code> and f a trig function.
		 * 
		 * @param n
		 * @param theta
		 * @return
		 */
		private IExpr expandPlus(IAST ast, IAST plusAST) {
			if (ast.isSin()) {
				return expandSinPlus(plusAST, 1);
			} else if (ast.isCos()) {
				return expandCosPlus(plusAST, 1);
			} else if (ast.isAST(F.Cot, 2)) {
				// Cos(x) / Sin(x)
				return F.Divide(expandCosPlus(plusAST, 1), expandSinPlus(plusAST, 1));
			} else if (ast.isTan()) {
				// Sin(x) / Cos(x)
				return F.Divide(expandSinPlus(plusAST, 1), expandCosPlus(plusAST, 1));
			} else if (ast.isAST(F.Csc, 2)) {
				// 1 / Sin(x)
				return F.Divide(F.C1, expandSinPlus(plusAST, 1));
			} else if (ast.isAST(F.Sec, 2)) {
				// 1 / Cos(x)
				return F.Divide(F.C1, expandCosPlus(plusAST, 1));
			} else if (ast.isSinh()) {
				return expandSinhPlus(plusAST, 1);
			} else if (ast.isCosh()) {
				return expandCoshPlus(plusAST, 1);
			} else if (ast.isTanh()) {
				return expandTanhPlus(plusAST, 1);
			}
			return F.NIL;
		}

		/**
		 * Expand <code>f(n*theta)</code> and f a trig function.
		 * 
		 * @param n
		 * @param theta
		 * @return
		 */
		private IExpr expandTimes(IAST ast, IAST timesAST) {
			if (timesAST.arg1().isInteger()) {
				IInteger n = (IInteger) timesAST.arg1();
				if (n.compareInt(0) > 0) {
					try {
						IExpr theta = timesAST.removeAtClone(1).getOneIdentity(F.C1);
						if (ast.isSin()) {
							return expandSinTimes(n, theta);
						} else if (ast.isCos()) {
							return expandCosTimes(n, theta);
						} else if (ast.isAST(F.Cot, 2)) {
							// Cos(x) / Sin(x)
							return F.Divide(expandCosTimes(n, theta), expandSinTimes(n, theta));
						} else if (ast.isTan()) {
							// Sin(x) / Cos(x)
							return F.Divide(expandSinTimes(n, theta), expandCosTimes(n, theta));
						} else if (ast.isAST(F.Csc, 2)) {
							// 1 / Sin(x)
							return F.Divide(F.C1, expandSinTimes(n, theta));
						} else if (ast.isAST(F.Sec, 2)) {
							// 1 / Cos(x)
							return F.Divide(F.C1, expandCosTimes(n, theta));
						} else if (ast.isSinh()) {
							int nInt = n.toInt();
							return expandSinhPlus(F.constantArray(F.Plus, theta, nInt), 1);
						} else if (ast.isCosh()) {
							int nInt = n.toInt();
							return expandCoshPlus(F.constantArray(F.Plus, theta, nInt), 1);
						}
					} catch (ArithmeticException ae) {

					}
				}
			}
			return F.NIL;
		}

		/**
		 * <code>Cos(n*theta)</code>
		 * 
		 * @param n
		 * @param theta
		 * @return
		 */
		private static IExpr expandCosTimes(IInteger n, IExpr theta) {
			int ni = n.toIntDefault(Integer.MIN_VALUE);
			if (ni > Integer.MIN_VALUE) {
				return F.sum(new Function<IExpr, IExpr>() {
					@Override
					public IExpr apply(IExpr i) {
						return Times(Times(Times(Power(CN1, Times(i, C1D2)), Binomial(n, i)),
								Power(Cos(theta), Plus(n, Times(CN1, i)))), Power(Sin(theta), i));
					}
				}, 0, ni, 2);
			}
			return F.NIL;
		}

		/**
		 * <code>Sin(n*theta)</code>
		 * 
		 * @param n
		 * @param theta
		 * @return
		 */
		private static IExpr expandSinTimes(IInteger n, IExpr theta) {
			int ni = n.toIntDefault(Integer.MIN_VALUE);
			if (ni > Integer.MIN_VALUE) {
				return F.sum(new Function<IExpr, IExpr>() {
                    @Override
                    public IExpr apply(IExpr i) {
                        return Times(Times(Times(Power(CN1, Times(Plus(i, CN1), C1D2)), Binomial(n, i)),
                                Power(Cos(theta), Plus(n, Times(CN1, i)))), Power(Sin(theta), i));
                    }
                }, 1, ni, 2);
			}
			return F.NIL;
		}

		/**
		 * <code>Sin(a+b+c+...)</code>
		 * 
		 * @param plusAST
		 * @param startPosition
		 * @return
		 */
		private static IExpr expandSinPlus(IAST plusAST, int startPosition) {
			IASTAppendable result = F.PlusAlloc(2);
			IExpr lhs = plusAST.get(startPosition);
			if (startPosition == plusAST.size() - 2) {
				IExpr rhs = plusAST.get(startPosition + 1);
				result.append(Times(Sin(lhs), Cos(rhs)));
				result.append(Times(Cos(lhs), Sin(rhs)));
			} else {
				result.append(Times(Sin(lhs), expandCosPlus(plusAST, startPosition + 1)));
				result.append(Times(Cos(lhs), expandSinPlus(plusAST, startPosition + 1)));
			}
			return result;
		}

		/**
		 * <code>Sinh(a+b+c+...)</code>
		 * 
		 * @param plusAST
		 * @param startPosition
		 * @return
		 */
		private static IExpr expandSinhPlus(IAST plusAST, int startPosition) {
			IASTAppendable result = F.PlusAlloc(2);
			IExpr lhs = plusAST.get(startPosition);
			if (startPosition == plusAST.size() - 2) {
				// Sinh(x)*Cosh(y) + Cosh(x)*Sinh(y)
				IExpr rhs = plusAST.get(startPosition + 1);
				result.append(Times(F.Sinh(lhs), F.Cosh(rhs)));
				result.append(Times(F.Cosh(lhs), F.Sinh(rhs)));
			} else {
				result.append(Times(F.Sinh(lhs), expandCoshPlus(plusAST, startPosition + 1)));
				result.append(Times(F.Cosh(lhs), expandSinhPlus(plusAST, startPosition + 1)));
			}
			return result;
		}

		/**
		 * <code>Sin(a+b+c+...)</code>
		 * 
		 * @param plusAST
		 * @param startPosition
		 * @return
		 */
		private static IExpr expandCosPlus(IAST plusAST, int startPosition) {
			IASTAppendable result = F.PlusAlloc(2);
			IExpr lhs = plusAST.get(startPosition);
			if (startPosition == plusAST.size() - 2) {
				IExpr rhs = plusAST.get(startPosition + 1);
				result.append(Times(Cos(lhs), Cos(rhs)));
				result.append(Times(CN1, Sin(lhs), Sin(rhs)));
			} else {
				result.append(Times(Cos(lhs), expandCosPlus(plusAST, startPosition + 1)));
				result.append(Times(CN1, Sin(lhs), expandSinPlus(plusAST, startPosition + 1)));
			}
			return result;
		}

		/**
		 * <code>Cosh(a+b+c+...)</code>
		 * 
		 * @param plusAST
		 * @param startPosition
		 * @return
		 */
		private static IExpr expandCoshPlus(IAST plusAST, int startPosition) {
			IASTAppendable result = F.PlusAlloc(2);
			IExpr lhs = plusAST.get(startPosition);
			if (startPosition == plusAST.size() - 2) {
				// Cosh(x)*Cosh(y) + Sinh(x)*Sinh(y)
				IExpr rhs = plusAST.get(startPosition + 1);
				result.append(Times(F.Cosh(lhs), F.Cosh(rhs)));
				result.append(Times(F.Sinh(lhs), F.Sinh(rhs)));
			} else {
				result.append(Times(F.Cosh(lhs), expandCoshPlus(plusAST, startPosition + 1)));
				result.append(Times(F.Sinh(lhs), expandSinhPlus(plusAST, startPosition + 1)));
			}
			return result;
		}

		/**
		 * <code>Cosh(a+b+c+...)</code>
		 * 
		 * @param plusAST
		 * @param startPosition
		 * @return
		 */
		private static IExpr expandTanhPlus(IAST plusAST, int startPosition) {
			IASTAppendable result = F.TimesAlloc(2);
			IExpr lhs = plusAST.get(startPosition);
			if (startPosition == plusAST.size() - 2) {
				// (Tanh(x)+Tanh(y)) / (1+Tanh(x)*Tanh(y))
				IExpr rhs = plusAST.get(startPosition + 1);
				result.append(Plus(F.Tanh(lhs), F.Tanh(rhs)));
				result.append(F.Power(Plus(F.C1, Times(F.Tanh(lhs), F.Tanh(rhs))), F.CN1));
			} else {
				result.append(Plus(F.Tanh(lhs), expandTanhPlus(plusAST, startPosition + 1)));
				result.append(
						F.Power(Plus(F.C1, Times(F.Tanh(lhs), expandTanhPlus(plusAST, startPosition + 1))), F.CN1));
			}
			return result;
		}
	}

	public TrigExpand() {
	}

	/**
	 * Expands the argument of sine and cosine functions.
	 * 
	 * <a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities" >List
	 * of trigonometric identities</a>
	 */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		IExpr temp = Structure.threadLogicEquationOperators(ast.arg1(), ast, 1);
		if (temp.isPresent()) {
			return temp;
		}

		temp = ast.arg1();
		IExpr result;
		do {
			result = evalExpandAll(temp, engine);
			temp = result.accept(visitor);
		} while (temp.isPresent());
		return result;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
		newSymbol.setAttributes(ISymbol.LISTABLE);
	}

}
