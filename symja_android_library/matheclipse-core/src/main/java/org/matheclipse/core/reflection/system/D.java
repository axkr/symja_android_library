package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
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
 * Differentiation of a function. See
 * <a href="http://en.wikipedia.org/wiki/Derivative">Wikipedia:Derivative</a>
 */
public class D extends AbstractFunctionEvaluator {

	public D() {
	}

	/**
	 * Search for one of the <code>Derivative[a1][head]</code> rules.
	 * 
	 * 
	 * @param x
	 * @param a1
	 * @param header
	 * @return
	 */
	private IExpr getDerivativeArg1(IExpr x, final IExpr a1, final IExpr head) {
		if (head.isSymbol()) {
			ISymbol header = (ISymbol) head;
			IExpr der = Derivative.derivative(1, header);
			if (der.isPresent()) {
				// we've found a derivative for a function of the form f[x_]
				IExpr derivative = F.eval(F.unaryAST1(der, a1));
				return F.Times(F.D(a1, x), derivative);
			}
			IAST fDerivParam = createDerivative(1, header, a1);
			if (x.equals(a1)) {
				return fDerivParam;
			}
			return F.Times(F.D(a1, x), fDerivParam);
		}
		return F.NIL;
	}

	/**
	 * Search for one of the <code>Derivative[a1, a2][head]</code> rules.
	 * 
	 * @param x
	 * @param a1
	 * @param a2
	 * @param header
	 * @return
	 */
	private IExpr getDerivativeArg2(IExpr x, final IExpr a1, final IExpr a2, final IExpr head) {
		if (head.isSymbol()) {
			int n = 1;
			int m = 1;
			ISymbol header = (ISymbol) head;
			if (a1.isFree(x)) {
				n = 0;
			}
			if (a2.isFree(x)) {
				m = 0;
			}
			IExpr der = Derivative.derivative(n, m, header);
			if (der != null) {
				// we've found a derivative for a function of the form f[x_, y_]
				IExpr derivative = F.eval(F.binaryAST2(der, a1, a2));
				IAST times = F.Times();
				if (n == 1) {
					times.add(F.D(a1, x));
				}
				if (m == 1) {
					times.add(F.D(a2, x));
				}
				times.add(derivative);
				return times;
			}
			IAST fDerivParam = createDerivative(n, m, header, a1, a2);
			if (x.equals(a1)) {
				return fDerivParam;
			}
			return F.Times(F.D(a1, x), fDerivParam);
		}
		return F.NIL;
	}

	/**
	 * Create <code>Derivative[n][header][arg1]</code>
	 * 
	 * @param n
	 * @param header
	 * @param arg1
	 * @return
	 */
	private IAST createDerivative(final int n, final IExpr header, final IExpr arg1) {
		IAST deriv = F.Derivative(F.integer(n));
		IAST fDeriv = F.ast(deriv);
		fDeriv.add(header);
		IAST fDerivParam = F.ast(fDeriv);
		fDerivParam.add(arg1);
		return fDerivParam;
	}

	/**
	 * Create <code>Derivative[n][header][arg1, arg2]</code>
	 * 
	 * @param n
	 * @param m
	 * @param header
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	private IAST createDerivative(final int n, final int m, final IExpr header, final IExpr arg1, final IExpr arg2) {
		IAST deriv = F.Derivative(F.integer(n), F.integer(m));
		IAST fDeriv = F.ast(deriv);
		fDeriv.add(header);
		IAST fDerivParam = F.ast(fDeriv);
		fDerivParam.add(arg1);
		fDerivParam.add(arg2);
		return fDerivParam;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() < 3) {
			return F.NIL;
		}

		IExpr fx = ast.arg1();
		if (ast.size() > 3) {
			// reduce arguments by folding D[fxy, x, y] to D[ D[fxy, x], y] ...
			return ast.range(2).foldLeft(new BinaryEval(F.D, engine), fx);
		}

		if (fx.isList()) {
			// thread over first list
			return ((IAST) fx).mapAt(F.List(), ast, 1);
		}

		IExpr x = ast.arg2();
		if (x.isList()) {
			// D[fx_, {...}]
			IAST xList = (IAST) x;
			if (xList.isAST1() && xList.arg1().isList()) {
				IAST subList = (IAST) xList.arg1();
				return subList.args().mapLeft(F.List(), new BinaryEval(F.D, engine), fx);
			} else if (xList.isAST2() && xList.arg2().isInteger()) {
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
			return F.NIL;

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
			} else if (listArg1.isPower() && !listArg1.isFreeAt(1, x) && !listArg1.isFreeAt(2, x)) {
				// if (listArg1.isFreeAt(2, x)) {
				// return getDerivativeArg2(x, listArg1.arg1(), listArg1.arg2(),
				// header);
				//
				// // D[x_^i_NumberQ, z_]:= i*x^(i-1)*D[x,z];
				// final IAST timesList = F.Times();
				// timesList.add(listArg1.arg2());
				// final IAST powerList = F.Power();
				// powerList.add(listArg1.arg1());
				// final IAST plusList = F.Plus();
				// plusList.add(listArg1.arg2());
				// plusList.add(F.CN1);
				// powerList.add(plusList);
				// timesList.add(powerList);
				// timesList.add(F.D(listArg1.arg1(), ast.arg2()));
				// return timesList;
				// } else {

				final IExpr f = listArg1.arg1();
				final IExpr g = listArg1.arg2();
				final IExpr y = ast.arg2();

				// D[f_^g_,y_]:= f^g*(((g*D[f,y])/f)+Log[f]*D[g,y])
				final IAST resultList = F.Times();
				resultList.add(F.Power(f, g));
				resultList.add(F.Plus(F.Times(g, F.D(f, y), F.Power(f, F.CN1)), F.Times(F.Log(f), F.D(g, y))));
				return resultList;
				// }
			} else if ((header == F.Log) && (listArg1.isAST2())) {
				if (listArg1.isFreeAt(1, x)) {
					// D[Log[i_FreeQ(x), x_], z_]:= (x*Log[a])^(-1)*D[x,z];
					return F.Times(F.Power(F.Times(listArg1.arg2(), F.Log(listArg1.arg1())), F.CN1),
							F.D(listArg1.arg2(), x));
				}
				// } else if (header == F.LaplaceTransform && (listArg1.size()
				// == 4)) {
				// if (listArg1.arg3().equals(x) && listArg1.arg1().isFree(x,
				// true)) {
				// // D(LaplaceTransform(c,t,s), s) -> -c / s^2
				// return F.Times(-1L, listArg1.arg2(), F.Power(x, -2L));
				// } else if (listArg1.arg1().equals(x)) {
				// // D(LaplaceTransform(c,t,s), c) -> 1/s
				// return F.Power(x, -1L);
				// } else if (listArg1.arg1().isFree(x, true) &&
				// listArg1.arg2().isFree(x, true) && listArg1.arg3().isFree(x,
				// true))
				// {
				// // D(LaplaceTransform(c,t,s), w) -> 0
				// return F.C0;
				// } else if (listArg1.arg2().equals(x)) {
				// // D(LaplaceTransform(c,t,s), t) -> 0
				// return F.C0;
				// }
			} else if (listArg1.isAST1()) {
				IAST[] derivStruct = listArg1.isDerivative();
				if (derivStruct != null && derivStruct[2] != null) {
					IAST headAST = derivStruct[1];
					IAST a1Head = derivStruct[0];
					if (a1Head.isAST1() && a1Head.arg1().isInteger()) {
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
					return F.NIL;
				}
				return getDerivativeArg1(x, listArg1.arg1(), header);
			} else if (listArg1.isAST2()) {
				return getDerivativeArg2(x, listArg1.arg1(), listArg1.arg2(), header);
			}

		}

		return F.NIL;
	}

}