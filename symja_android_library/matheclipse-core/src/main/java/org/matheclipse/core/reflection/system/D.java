package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryBindIth1st;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.DRules;

/**
 * <pre>
 * D(f, x)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the partial derivative of <code>f</code> with respect to <code>x</code>.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * D(f, x, y, ...)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * differentiates successively with respect to <code>x</code>, <code>y</code>, etc.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * D(f, {x,n})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the multiple derivative of order <code>n</code>.<br />
 * </p>
 * </blockquote>
 * 
 * <pre>
 * D(f, {{x1, x2, ...}})
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives the vector derivative of <code>f</code> with respect to <code>x1</code>, <code>x2</code>, etc.
 * </p>
 * </blockquote>
 * <p>
 * <strong>Note</strong>: the upper case identifier <code>D</code> is different from the lower case identifier
 * <code>d</code>.
 * </p>
 * <h3>Examples</h3>
 * <p>
 * First-order derivative of a polynomial:<br />
 * </p>
 * 
 * <pre>
 * &gt;&gt; D(x^3 + x^2, x)   
 * 2*x+3*x^2
 * </pre>
 * <p>
 * Second-order derivative:
 * </p>
 * 
 * <pre>
 * &gt;&gt; D(x^3 + x^2, {x, 2})    
 * 2+6*x
 * </pre>
 * <p>
 * Trigonometric derivatives:<br />
 * </p>
 * 
 * <pre>
 * &gt;&gt; D(Sin(Cos(x)), x)    
 * -Cos(Cos(x))*Sin(x) 
 * 
 * &gt;&gt; D(Sin(x), {x, 2})    
 * -Sin(x)    
 * 
 * &gt;&gt; D(Cos(t), {t, 2})    
 * -Cos(t)
 * </pre>
 * <p>
 * Unknown variables are treated as constant:
 * </p>
 * 
 * <pre>
 * &gt;&gt; D(y, x)    
 * 0    
 * 
 * &gt;&gt; D(x, x)    
 * 1    
 * 
 * &gt;&gt; D(x + y, x)    
 * 1
 * </pre>
 * <p>
 * Derivatives of unknown functions are represented using 'Derivative':<br />
 * </p>
 * 
 * <pre>
 * &gt;&gt; D(f(x), x)    
 * f'(x)    
 * 
 * &gt;&gt; D(f(x, x), x)    
 * Derivative(0,1)[f][x,x]+Derivative(1,0)[f][x,x]
 * </pre>
 * <p>
 * Chain rule:<br />
 * </p>
 * 
 * <pre>
 * &gt;&gt; D(f(2*x+1, 2*y, x+y)    
 * 2*Derivative(1,0,0)[f][1+2*x,2*y,x+y]+Derivative(0,0,1)[f][1+2*x,2*y,x+y]    
 * 
 * &gt;&gt; D(f(x^2, x, 2*y), {x,2}, y) // Expand    
 * 2*Derivative(0,2,1)[f][x^2,x,2*y]+4*Derivative(1,0,1)[f][x^2,x,2*y]+8*x*Derivative(
 * 1,1,1)[f][x^2,x,2*y]+8*x^2*Derivative(2,0,1)[f][x^2,x,2*y]
 * </pre>
 * <p>
 * Compute the gradient vector of a function:<br />
 * </p>
 * 
 * <pre>
 * &gt;&gt; D(x ^ 3 * Cos(y), {{x, y}})   
 * {3*x^2*Cos(y),-x^3*Sin(y)}
 * </pre>
 * <p>
 * Hesse matrix:<br />
 * </p>
 * 
 * <pre>
 * &gt;&gt; D(Sin(x) * Cos(y), {{x,y}, 2})    
 * {{-Cos(y)*Sin(x),-Cos(x)*Sin(y)},{-Cos(x)*Sin(y),-Cos(y)*Sin(x)}}  
 * 
 * &gt;&gt; D(2/3*Cos(x) - 1/3*x*Cos(x)*Sin(x) ^ 2,x)//Expand    
 * 1/3*x*Sin(x)^3-1/3*Sin(x)^2*Cos(x)-2/3*Sin(x)-2/3*x*Cos(x)^2*Sin(x)
 * 
 * &gt;&gt; D(f(#1), {#1,2})    
 * f''(#1)   
 * 
 * &gt;&gt; D((#1&amp;)(t),{t,4})    
 * 0    
 * 
 * &gt;&gt; Attributes(f) = {HoldAll}; Apart(f''(x + x))    
 * f''(2*x)  
 * 
 * &gt;&gt; Attributes(f) = {}; Apart(f''(x + x))    
 * f''(2*x)  
 * 
 * &gt;&gt; D({#^2}, #)
 * {2*#1}
 * </pre>
 */
public class D extends AbstractFunctionEvaluator implements DRules {

	public D() {
	}

	@Override
	public IAST getRuleAST() {
		return RULES;
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
	private IExpr getDerivativeArg1(IExpr x, final IExpr a1, final IExpr head, EvalEngine engine) {
		if (head.isSymbol()) {
			ISymbol header = (ISymbol) head;
			// IExpr der = Derivative.derivative(1, header, engine);
			// if (der.isPresent()) {
			// // we've found a derivative for a function of the form f[x_]
			// IExpr derivative = F.eval(F.unaryAST1(der, a1));
			// return F.Times(F.D(a1, x), derivative);
			// }
			IAST fDerivParam = Derivative.createDerivative(1, header, a1);
			if (x.equals(a1)) {
				// return F.NIL;
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
	private IExpr getDerivativeArgN(IExpr x, final IAST ast, final IExpr head) {
		IAST[] deriv = ast.isDerivative();
		if (deriv != null) {
			IAST plus = F.PlusAlloc(ast.size());
			for (int i = 1; i < ast.size(); i++) {
				plus.append(F.Times(F.D(ast.get(i), x), addDerivative(i, deriv[0], deriv[1].arg1(), ast)));
			}
			return plus;
		}
		if (head.isSymbol()) {
			IAST plus = F.PlusAlloc(ast.size());
			for (int i = 1; i < ast.size(); i++) {
				plus.append(F.Times(F.D(ast.get(i), x), createDerivative(i, head, ast)));
			}
			return plus;
		}
		return F.NIL;
	}

	/**
	 * Create <code>Derivative[...,1,...][header][arg1, arg2, ...]</code>
	 * 
	 * @param pos
	 *            the position of the <code>1</code>
	 * @param header
	 * @param arg1
	 * @return
	 */
	private IAST createDerivative(final int pos, final IExpr header, final IAST args) {
		IAST derivativeHead1 = F.ast(F.Derivative, args.size(), false);
		for (int i = 1; i < args.size(); i++) {
			if (i == pos) {
				derivativeHead1.append(F.C1);
			} else {
				derivativeHead1.append(F.C0);
			}
		}
		IAST derivativeHead2 = F.ast(derivativeHead1);
		derivativeHead2.append(header);
		IAST derivativeAST = F.ast(derivativeHead2, args.size(), false);
		for (int i = 1; i < args.size(); i++) {
			derivativeAST.append(args.get(i));
		}
		return derivativeAST;
	}

	private IAST addDerivative(final int pos, IAST deriveHead, final IExpr header, final IAST args) {
		IASTMutable derivativeHead1 = deriveHead.clone();
		for (int i = 1; i < derivativeHead1.size(); i++) {
			if (i == pos) {
				derivativeHead1.set(i, derivativeHead1.get(i).inc());
			}
		}
		IAST derivativeHead2 = F.ast(derivativeHead1);
		derivativeHead2.append(header);
		IAST derivativeAST = F.ast(derivativeHead2, args.size(), false);
		for (int i = 1; i < args.size(); i++) {
			derivativeAST.append(args.get(i));
		}
		return derivativeAST;
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() < 3) {
			return F.NIL;
		}

		IExpr fx = ast.arg1();
		if (fx.isIndeterminate()) {
			return F.Indeterminate;
		}
		if (ast.size() > 3) {
			// reduce arguments by folding D[fxy, x, y] to D[ D[fxy, x], y] ...
			return ast.foldLeft((x, y) -> engine.evaluate(F.D(x, y)), fx, 2);
		}

		if (fx.isList()) {
			IAST list = (IAST) fx;
			// thread over first list
			return list.mapThread(F.ListAlloc(list.size()), ast, 1);
		}

		IExpr x = ast.arg2();
		if (x.isList()) {
			// D[fx_, {...}]
			IAST xList = (IAST) x;
			if (xList.isAST1() && xList.arg1().isListOfLists()) {
				IAST subList = (IAST) xList.arg1();
				IAST result = F.ListAlloc(subList.size());
				for (int i = 1; i < subList.size(); i++) {
					result.append(F.D(fx, F.List(subList.get(i))));
				}
				return result;
			} else if (xList.isAST1() && xList.arg1().isList()) {
				IAST subList = (IAST) xList.arg1();
				return subList.mapLeft(F.List(), (a, b) -> engine.evaluate(F.D(a, b)), fx);
			} else if (xList.isAST2() && xList.arg2().isInteger()) {
				if (ast.isEvalFlagOn(IAST.IS_DERIVATIVE_EVALED)) {
					return F.NIL;
				}
				int n = Validate.checkIntType(xList, 2, 1);
				if (n >= 0) {
					if (xList.arg1().isList()) {
						x = F.List(xList.arg1());
					} else {
						x = xList.arg1();
					}
					for (int i = 0; i < n; i++) {
						fx = engine.evaluate(F.D(fx, x));
					}
					return fx;
				}
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
				return listArg1.mapThread(F.D(F.Null, x), 1);
			} else if (listArg1.isTimes()) {
				return listArg1.map(F.Plus(), new BinaryBindIth1st(listArg1, F.D(F.Null, x)));
			} else if (listArg1.isPower()) {// && !listArg1.isFreeAt(1, x) && !listArg1.isFreeAt(2, x)) {
				final IExpr f = listArg1.arg1();
				final IExpr g = listArg1.arg2();
				final IExpr y = ast.arg2();
				if (listArg1.isFreeAt(2, x)) {
					// g*D(f,y)*f^(g-1)
					return F.Times(g, F.D(f, y), F.Power(f, g.dec()));
				}
				if (listArg1.isFreeAt(1, x)) {
					// D(g,y)*Log(f)*f^g
					return F.Times(F.D(g, y), F.Log(f), F.Power(f, g));
				}

				// D[f_^g_,y_]:= f^g*(((g*D[f,y])/f)+Log[f]*D[g,y])
				final IAST resultList = F.Times();
				resultList.append(F.Power(f, g));
				resultList.append(F.Plus(F.Times(g, F.D(f, y), F.Power(f, F.CN1)), F.Times(F.Log(f), F.D(g, y))));
				return resultList;
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
			} else if (listArg1.isAST1() && ast.isEvalFlagOff(IAST.IS_DERIVATIVE_EVALED)) {
				IAST[] derivStruct = listArg1.isDerivativeAST1();
				if (derivStruct != null && derivStruct[2] != null) {
					IAST headAST = derivStruct[1];
					IAST a1Head = derivStruct[0];
					if (a1Head.isAST1() && a1Head.arg1().isInteger()) {
						try {
							int n = ((IInteger) a1Head.arg1()).toInt();
							IExpr arg1 = listArg1.arg1();
							if (n > 0) {
								IAST fDerivParam = Derivative.createDerivative(n + 1, headAST.arg1(), arg1);
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
				return getDerivativeArg1(x, listArg1.arg1(), header, engine);
				// } else if (listArg1.isAST2()) {
				// return getDerivativeArg2(x, listArg1.arg1(), listArg1.arg2(), header);
			} else if (listArg1.isAST() && ast.isEvalFlagOff(IAST.IS_DERIVATIVE_EVALED)) {
				return getDerivativeArgN(x, listArg1, header);
			}

		}

		return F.NIL;
	}

}