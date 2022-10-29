package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.BinaryBindIth1st;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.rules.DRules;

/**
 *
 *
 * <pre>
 * D(f, x)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives the partial derivative of <code>f</code> with respect to <code>x</code>.
 *
 * </blockquote>
 *
 * <pre>
 * D(f, x, y, ...)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * differentiates successively with respect to <code>x</code>, <code>y</code>, etc.
 *
 * </blockquote>
 *
 * <pre>
 * D(f, {x,n})
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives the multiple derivative of order <code>n</code>.<br>
 *
 * </blockquote>
 *
 * <pre>
 * D(f, {{x1, x2, ...}})
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives the vector derivative of <code>f</code> with respect to <code>x1</code>, <code>x2</code> ,
 * etc.
 *
 * </blockquote>
 *
 * <p>
 * <strong>Note</strong>: the upper case identifier <code>D</code> is different from the lower case
 * identifier <code>d</code>.
 *
 * <h3>Examples</h3>
 *
 * <p>
 * First-order derivative of a polynomial:<br>
 *
 * <pre>
 * &gt;&gt; D(x^3 + x^2, x)
 * 2*x+3*x^2
 * </pre>
 *
 * <p>
 * Second-order derivative:
 *
 * <pre>
 * &gt;&gt; D(x^3 + x^2, {x, 2})
 * 2+6*x
 * </pre>
 *
 * <p>
 * Trigonometric derivatives:<br>
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
 *
 * <p>
 * Unknown variables are treated as constant:
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
 *
 * <p>
 * Derivatives of unknown functions are represented using 'Derivative':<br>
 *
 * <pre>
 * &gt;&gt; D(f(x), x)
 * f'(x)
 *
 * &gt;&gt; D(f(x, x), x)
 * Derivative(0,1)[f][x,x]+Derivative(1,0)[f][x,x]
 * </pre>
 *
 * <p>
 * Chain rule:<br>
 *
 * <pre>
 * &gt;&gt; D(f(2*x+1, 2*y, x+y),x)
 * 2*Derivative(1,0,0)[f][1+2*x,2*y,x+y]+Derivative(0,0,1)[f][1+2*x,2*y,x+y]
 *
 * &gt;&gt; D(f(x^2, x, 2*y), {x,2}, y) // Expand
 * 2*Derivative(0,2,1)[f][x^2,x,2*y]+4*Derivative(1,0,1)[f][x^2,x,2*y]+8*x*Derivative(
 * 1,1,1)[f][x^2,x,2*y]+8*x^2*Derivative(2,0,1)[f][x^2,x,2*y]
 * </pre>
 *
 * <p>
 * Compute the gradient vector of a function:<br>
 *
 * <pre>
 * &gt;&gt; D(x ^ 3 * Cos(y), {{x, y}})
 * {3*x^2*Cos(y),-x^3*Sin(y)}
 * </pre>
 *
 * <p>
 * Hesse matrix:<br>
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
  private static final Logger LOGGER = LogManager.getLogger();

  public D() {}

  @Override
  public IAST getRuleAST() {
    return RULES;
  }

  /**
   * Search for one of the <code>Derivative[a1][head]</code> rules.
   *
   * @param x
   * @param a1
   * @param head
   * @param engine
   * @return
   */
  private static IExpr getDerivativeArg1(IExpr x, final IExpr a1, final IExpr head,
      EvalEngine engine) {
    if (head.isSymbol()) {
      ISymbol header = (ISymbol) head;
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
   * @param ast
   * @param head
   * @return
   */
  private static IExpr getDerivativeArgN(IExpr x, final IAST ast, final IExpr head) {
    IAST[] deriv = ast.isDerivative();
    int size = ast.size();
    if (deriv != null) {
      IASTAppendable plus = F.PlusAlloc(size);
      ast.forEach(size, (expr, i) -> {
        plus.append(F.Times(F.D(expr, x), addDerivative(i, deriv[0], deriv[1].arg1(), ast)));
      });
      return plus;
    }
    if (head.isSymbol()) {
      IASTAppendable plus = F.PlusAlloc(size);
      ast.forEach(size, (expr, i) -> {
        plus.append(F.Times(F.D(expr, x), createDerivative(i, head, ast)));
      });
      return plus;
    }
    return F.NIL;
  }

  /**
   * Create <code>Derivative[...,1,...][header][arg1, arg2, ...]</code>
   *
   * @param pos the position of the <code>1</code>
   * @param header
   * @param args
   * @return
   */
  private static IAST createDerivative(final int pos, final IExpr header, final IAST args) {
    final int size = args.size();
    IASTAppendable derivativeHead1 = F.ast(S.Derivative, size);
    for (int i = 1; i < size; i++) {
      derivativeHead1.append(i == pos ? F.C1 : F.C0);
    }
    IASTAppendable derivativeHead2 = F.ast(derivativeHead1);
    derivativeHead2.append(header);
    IASTAppendable derivativeAST = F.ast(derivativeHead2, size);
    derivativeAST.appendArgs(args);
    // args.forEach(x -> derivativeAST.append(x));
    return derivativeAST;
  }

  private static IAST addDerivative(final int pos, IAST deriveHead, final IExpr header,
      final IAST args) {
    IASTMutable derivativeHead1 = deriveHead.copyAppendable();
    for (int i = 1; i < derivativeHead1.size(); i++) {
      if (i == pos) {
        derivativeHead1.set(i, derivativeHead1.get(i).inc());
      }
    }
    IASTAppendable derivativeHead2 = F.ast(derivativeHead1);
    derivativeHead2.append(header);
    IASTAppendable derivativeAST = F.ast(derivativeHead2, args.size());
    derivativeAST.appendArgs(args.size(), i -> args.get(i));
    return derivativeAST;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      return ast.arg1();
    }
    try {
      final IExpr fx = ast.arg1();
      if (fx.isIndeterminate()) {
        return S.Indeterminate;
      }
      if (ast.size() > 3) {
        // reduce arguments by folding D[fxy, x, y] to D[ D[fxy, x], y] ...
        return ast.foldLeft((x, y) -> engine.evaluateNIL(F.D(x, y)), fx, 2);
      }

      IExpr x = ast.arg2();
      if (!(x.isVariable() || x.isList())) {
        // `1` is not a valid variable.
        return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(x), engine);
      }

      if (fx.isList()) {
        IAST list = (IAST) fx;
        // thread over first list
        return list.mapThreadEvaled(engine, F.ListAlloc(list.size()), ast, 1);
      }

      if (x.isList()) {
        // D[fx_, {...}]
        IAST xList = (IAST) x;
        if (xList.isAST1() && xList.arg1().isListOfLists()) {
          IAST subList = (IAST) xList.arg1();
          IASTAppendable result = F.ListAlloc(subList.size());
          result.appendArgs(subList.size(), i -> F.D(fx, F.list(subList.get(i))));
          return result;
        } else if (xList.isAST1() && xList.arg1().isList()) {
          IAST subList = (IAST) xList.arg1();
          return subList.mapLeft(F.ListAlloc(), (a, b) -> engine.evaluateNIL(F.D(a, b)), fx);
        } else if (xList.isAST2()) {
          if (ast.isEvalFlagOn(IAST.IS_DERIVATIVE_EVALED)) {
            return F.NIL;
          }
          if (xList.arg1().isList()) {
            x = F.list(xList.arg1());
          } else {
            x = xList.arg1();
          }
          IExpr arg2 = xList.arg2();
          int n = arg2.toIntDefault();
          if (n >= 0) {
            IExpr temp = fx;
            for (int i = 0; i < n; i++) {
              temp = S.D.ofNIL(engine, temp, x);
              if (temp.isNIL()) {
                return F.NIL;
              }
            }
            return temp;
          }
          if (arg2.isFree(num -> num.isNumber(), false)) {
            if (fx instanceof ASTSeriesData) {
              return F.NIL;
            }
            if (fx.isFree(x, true)) {
              // Piecewise({{fx, arg2 == 0}}, 0)
              return F.Piecewise(F.list(F.list(fx, F.Equal(arg2, F.C0))), F.C0);
            }
            if (fx.equals(x)) {
              // Piecewise({{fx, arg2 == 0}, {1, arg2 == 1}}, 0)
              return F.Piecewise(
                  F.list(F.list(fx, F.Equal(arg2, F.C0)), F.list(F.C1, F.Equal(arg2, F.C1))), F.C0);
            }
            if (fx.isAST()) {
              final IAST function = (IAST) fx;
              // final IExpr header = function.head();
              if (function.isPlus()) {
                // D(a_+b_+c_,x_) -> D(a,x)+D(b,x)+D(c,x)
                return function.mapThread(F.D(F.Slot1, xList), 1);
              }
            }
            return F.NIL;
          }
          if (!x.isVariable()) {
            // `1` is not a valid variable.
            return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(x), engine);
          }
          if (arg2.isAST()) {
            return F.NIL;
          }
          // Multiple derivative specifier `1` does not have the form {variable, n} where n is a
          // symbolic expression or a non-negative integer.
          return IOFunctions.printMessage(ast.topHead(), "dvar", F.list(xList), engine);
        }
        return F.NIL;
      }

      if (!x.isVariable()) {
        // `1` is not a valid variable.
        return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(x), engine);
      }
      return binaryD(fx, x, ast, engine);
    } catch (final ValidateException ve) {
      // int number validation
      LOGGER.log(engine.getLogLevel(), ve.getMessage(ast.topHead()), ve);
      return F.NIL;
    }
  }

  /**
   * Evaluate <code>D(functionO>fX, x)</code> for some general cases.
   *
   * @param functionOfX the function of <code>x</code>
   * @param x derive w.r.t this variable
   * @param ast
   * @param engine
   * @return
   */
  private static IExpr binaryD(final IExpr functionOfX, IExpr x, final IAST ast,
      EvalEngine engine) {
    int[] dim = functionOfX.isPiecewise();
    if (dim != null) {
      return dPiecewise(dim, (IAST) functionOfX, ast, engine);
    }

    if (functionOfX instanceof ASTSeriesData) {
      ASTSeriesData series = ((ASTSeriesData) functionOfX);
      if (series.getX().equals(x)) {
        final IExpr temp = ((ASTSeriesData) functionOfX).derive(x);
        if (temp != null) {
          return temp;
        }
        return F.NIL;
      }
      return F.C0;
    }
    if (functionOfX.isFree(x, true)) {
      return F.C0;
    }

    if (functionOfX.isNumber()) {
      // D[x_?NumberQ,y_] -> 0
      return F.C0;
    }
    if (functionOfX.equals(x)) {
      // D[x_,x_] -> 1
      return F.C1;
    }

    if (functionOfX.isAST()) {
      final IAST function = (IAST) functionOfX;
      final IExpr header = function.head();
      if (function.isPlus()) {
        // D(a_+b_+c_,x_) -> D(a,x)+D(b,x)+D(c,x)
        IExpr result = function.mapThread(F.D(F.Slot1, x), 1);
        return engine.evaluate(result);
      } else if (function.isTimes()) {
        return function.map(F.PlusAlloc(16), new BinaryBindIth1st(function, F.D(S.Null, x)));
      } else if (function.isPower()) {
        // f ^ g
        final IExpr f = function.base();
        final IExpr g = function.exponent();
        if (g.isFree(x)) {
          // g*D(f,y)*f^(g-1)
          return F.Times(g, F.D(f, x), F.Power(f, g.dec()));
        }
        if (f.isFree(x)) {
          if (f.isE()) {
            return F.Times(F.D(g, x), F.Exp(g));
          }
          // D(g,y)*Log(f)*f^g
          return F.Times(F.D(g, x), F.Log(f), F.Power(f, g));
        }

        // D[f_^g_,y_]:= f^g*(((g*D[f,y])/f)+Log[f]*D[g,y])
        final IASTAppendable resultList = F.TimesAlloc(2);
        resultList.append(F.Power(f, g));
        resultList
            .append(F.Plus(F.Times(g, F.D(f, x), F.Power(f, F.CN1)), F.Times(F.Log(f), F.D(g, x))));
        return resultList;
      } else if (function.isAST(S.Surd, 3)) {
        // Surd[f,g]
        final IExpr f = function.base();

        if (function.exponent().isInteger()) {
          final IInteger g = (IInteger) function.exponent();
          if (g.isMinusOne()) {
            return F.Times(F.CN1, F.D(f, x), F.Power(f, F.CN2));
          }
          final IRational gInverse = g.inverse();
          if (g.isNegative()) {
            if (g.isEven()) {
              return F.Times(gInverse, F.D(f, x), F.Power(F.Surd(f, g.negate()), g.dec()));
            }
            return F.Times(gInverse, F.D(f, x), F.Power(f, F.CN1),
                F.Power(F.Surd(f, g.negate()), F.CN1));
          }
          return F.Times(gInverse, F.D(f, x), F.Power(F.Surd(f, g), g.dec().negate()));
        }
      } else if ((header == S.Log) && (function.isAST2())) {
        if (function.isFreeAt(1, x)) {
          // D[Log[i_FreeQ(x), x_], z_]:= (x*Log[a])^(-1)*D[x,z];
          return F.Times(F.Power(F.Times(function.arg2(), F.Log(function.arg1())), F.CN1),
              F.D(function.arg2(), x));
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
      } else if (function.isAST1() && ast.isEvalFlagOff(IAST.IS_DERIVATIVE_EVALED)) {
        IAST[] derivStruct = function.isDerivativeAST1();
        if (derivStruct != null && derivStruct[2] != null) {
          IAST headAST = derivStruct[1];
          IAST a1Head = derivStruct[0];
          if (a1Head.isAST1() && a1Head.arg1().isInteger()) {
            try {
              int n = ((IInteger) a1Head.arg1()).toInt();
              IExpr arg1 = function.arg1();
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
        return getDerivativeArg1(x, function.arg1(), header, engine);
      } else if (function.isAST() && ast.isEvalFlagOff(IAST.IS_DERIVATIVE_EVALED)) {
        return getDerivativeArgN(x, function, header);
      }
    }
    return F.NIL;
  }

  private static IExpr dPiecewise(int[] dim, final IAST piecewiseFunction, final IAST ast,
      EvalEngine engine) {

    IAST list = (IAST) piecewiseFunction.arg1();
    if (list.size() > 1) {
      IASTAppendable pwResult = F.ListAlloc(list.size());
      for (int i = 1; i < list.size(); i++) {
        IASTMutable piecewiseD = ast.copy();
        piecewiseD.set(1, list.get(i).first());
        pwResult.append(F.list(piecewiseD, list.get(i).second()));
      }
      if (piecewiseFunction.size() > 2) {
        IASTMutable piecewiseD = ast.copy();
        piecewiseD.set(1, piecewiseFunction.arg2());
        pwResult.append(F.list(engine.evaluate(piecewiseD), S.True));
      }
      IASTMutable piecewise = piecewiseFunction.copy();
      piecewise.set(1, pwResult);
      if (piecewise.size() > 2) {
        piecewise.set(2, S.Indeterminate);
      }
      return piecewise;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }
}
