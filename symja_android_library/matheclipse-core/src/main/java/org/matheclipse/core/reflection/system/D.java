package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryBindIth1st;
import org.matheclipse.core.generic.BinaryEval;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Differentiation of a function. See <a href="http://en.wikipedia.org/wiki/Derivative">Wikipedia:Derivative</a>
 */
public class D extends AbstractFunctionEvaluator {

	public D() {
	}

	/**
	 * Search for one of the <code>Derivative[..]</code> rules from the <code>System.mep</code> file read at startup time.
	 * 
	 * Examples for rules from the <code>System.mep</code> file:<br/>
	 * <code>Derivative[Cos]=(-1)*Sin[#]&</code><br/>
	 * <code>Derivative[Sin]=Cos[#]&</code><br/>
	 * <code>Derivative[Tan]=Cos[#]^(-2)&</code><br/>
	 * 
	 * @param x
	 * @param arg1
	 * @param header
	 * @return
	 */
	private IExpr getDerivativeArg1(IExpr x, final IExpr a1, final IExpr head) {
		if (head.isSymbol()) {
			ISymbol header = (ISymbol) head;
			IExpr der = Derivative.derivative(1, header);
			if (der != null) {
				// we've found a derivative for a function of the form f[x_]
				IExpr derivative = F.eval(F.$(der, a1));
				return F.Times(F.D(a1, x), derivative);
			}
			IAST fDerivParam = createDerivative(1, header, a1);
			if (x.equals(a1)) {
				return fDerivParam;
			}
			return F.Times(F.D(a1, x), fDerivParam);
		}
		return null;
	}

	/**
	 * Create <code>Derivative[n][header][arg1]</code>
	 * 
	 * @param n
	 * @param header
	 * @param arg1
	 * @return
	 */
	public IAST createDerivative(final int n, final IExpr header, final IExpr arg1) {
		IAST deriv = F.Derivative(F.integer(n));
		IAST fDeriv = F.ast(deriv);
		fDeriv.add(header);
		IAST fDerivParam = F.ast(fDeriv);
		fDerivParam.add(arg1);
		return fDerivParam;
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		if (ast.size() < 3) {
			return null;
		}

		IExpr fx = ast.arg1();
		if (ast.size() > 3) {
			// reduce arguments by folding D[fxy, x, y] to D[ D[fxy, x], y] ...
			return ast.range(2).foldLeft(new BinaryEval(F.D), fx);
		}

		if (fx.isList()) {
			// thread over first list
			return ((IAST) fx).mapAt(F.List(), ast, 1);
		}

		IExpr x = ast.arg2();
		if (x.isList()) {
			// D[fx_, {...}]
			IAST xList = (IAST) x;
			if (xList.size() == 2 && xList.arg1().isList()) {
				IAST subList = (IAST) xList.arg1();
				return subList.args().mapLeft(F.List(), new BinaryEval(F.D), fx);
			} else if (xList.size() == 3 && xList.arg2().isInteger()) {
				int n = Validate.checkIntType(xList, 2, 1);

				if (xList.arg1().isList()) {
					x = F.List(xList.arg1());
				} else {
					x = xList.arg1();
				}
				for (int i = 0; i < n; i++) {
					fx = F.eval(F.D, fx, x);
				}

				return fx;

			}
			return null;

		}

		if (!(x.isList()) && fx.isFree(x, true)) {
			return F.C0;
		}
		if (fx.isNumber()) {
			// D[x_NumberQ,y_] -> 0
			return F.C0;
		}
		if (fx.equals(x)) {
			// D[x_,x_] -> 1
			return F.C1;
		}

		if (fx.isAST()) {
			final IAST listArg1 = (IAST) fx;
			final IExpr header = listArg1.head();
			if (listArg1.isPlus()) {
				// D[a_+b_+c_,x_] -> D[a,x]+D[b,x]+D[c,x]
				return listArg1.mapAt(F.D(F.Null, x), 1);
			} else if (listArg1.isTimes()) {
				return listArg1.args().map(F.Plus(), new BinaryBindIth1st(listArg1, F.D(F.Null, x)));
			} else if (listArg1.isPower()) {
				if (listArg1.isFreeAt(2, x)) {
					// D[x_^i_NumberQ, z_]:= i*x^(i-1)*D[x,z];
					final IAST timesList = F.Times();
					timesList.add(listArg1.arg2());
					final IAST powerList = F.Power();
					powerList.add(listArg1.arg1());
					final IAST plusList = F.Plus();
					plusList.add(listArg1.arg2());
					plusList.add(F.CN1);
					powerList.add(plusList);
					timesList.add(powerList);
					timesList.add(F.D(listArg1.arg1(), ast.arg2()));
					return timesList;
				} else {
					// D[f_^g_,y_]:= f^g*(((g*D[f,y])/f)+Log[f]*D[g,y])
					final IAST resultList = F.Times();
					final IExpr f = listArg1.arg1();
					final IExpr g = listArg1.arg2();
					final IExpr y = ast.arg2();

					IAST powerList = F.Power();
					powerList.add(f);
					powerList.add(g);
					resultList.add(powerList);

					final IAST plusList = F.Plus();
					IAST timesList = F.Times();
					timesList.add(g);
					timesList.add(F.D(f, y));
					timesList.add(F.Power(f, F.CN1));
					plusList.add(timesList);

					timesList = F.Times(F.Log(f), F.D(g, y));
					plusList.add(timesList);

					resultList.add(plusList);
					return resultList;
				}
			} else if ((header == F.Log) && (listArg1.size() == 3)) {
				if (listArg1.isFreeAt(1, x)) {
					// D[Log[i_FreeQ(x), x_], z_]:= (x*Log[a])^(-1)*D[x,z];
					return F.Times(F.Power(F.Times(listArg1.arg2(), F.Log(listArg1.arg1())), F.CN1), F.D(listArg1.arg2(), x));
				}
				// } else if (header == F.LaplaceTransform && (listArg1.size() == 4)) {
				// if (listArg1.arg3().equals(x) && listArg1.arg1().isFree(x, true)) {
				// // D(LaplaceTransform(c,t,s), s) -> -c / s^2
				// return F.Times(-1L, listArg1.arg2(), F.Power(x, -2L));
				// } else if (listArg1.arg1().equals(x)) {
				// // D(LaplaceTransform(c,t,s), c) -> 1/s
				// return F.Power(x, -1L);
				// } else if (listArg1.arg1().isFree(x, true) && listArg1.arg2().isFree(x, true) && listArg1.arg3().isFree(x, true))
				// {
				// // D(LaplaceTransform(c,t,s), w) -> 0
				// return F.C0;
				// } else if (listArg1.arg2().equals(x)) {
				// // D(LaplaceTransform(c,t,s), t) -> 0
				// return F.C0;
				// }
			} else if (listArg1.size() == 2) {
				IAST[] derivStruct = listArg1.isDerivative();
				if (derivStruct != null && derivStruct[2] != null) {
					IAST headAST = derivStruct[1];
					IAST a1Head = derivStruct[0];
					if (a1Head.size() == 2 && a1Head.arg1().isInteger()) {
						try {
							int n = ((IInteger) a1Head.arg1()).toInt();
							IExpr arg1 = listArg1.arg1();
							if (n > 0) {
								IAST fDerivParam = createDerivative(n + 1, headAST.arg1(), arg1);
								if (x.equals(arg1)) {
									return fDerivParam;
								}
								return F.Times(F.D(arg1, x), fDerivParam);
							}
						} catch (ArithmeticException ae) {

						}
					}
					return null;
				}
				return getDerivativeArg1(x, listArg1.arg1(), header);
			}

		}

		return null;
	}

}