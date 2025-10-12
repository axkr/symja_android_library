package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * Derivative(n)[f]
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * represents the <code>n</code>-th derivative of the function <code>f</code>.<br>
 *
 * </blockquote>
 *
 * <pre>
 * Derivative(n1, n2, n3,...)[f]
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * represents a multivariate derivative.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Derivative(1)[Sin]
 * Cos(#1)&amp;
 *
 * &gt;&gt; Derivative(3)[Sin]
 * -Cos(#1)&amp;
 *
 * &gt;&gt; Derivative(2)[# ^ 3&amp;]
 * 6*(#1&amp;)
 * </pre>
 *
 * <p>
 * <code>Derivative</code> can be entered using <code>'</code>:<br>
 *
 * <pre>
 * &gt;&gt; Sin'(x)
 * Cos(x)
 *
 * &gt;&gt; (# ^ 4&amp;)''
 * 12*(#1^2&amp;)
 *
 * &gt;&gt; f'(x) // FullForm
 * "Derivative(1)[f][x]"
 * </pre>
 *
 * <p>
 * The <code>0</code>th derivative of any expression is the expression itself:
 *
 * <pre>
 * &gt;&gt; Derivative(0,0,0)[a+b+c]
 * a+b+c
 * </pre>
 *
 * <p>
 * Unknown derivatives:<br>
 *
 * <pre>
 * &gt;&gt; Derivative(2, 1)[h]
 * Derivative(2,1)[h]
 *
 * &gt;&gt; Derivative(2, 0, 1, 0)[h(g)]
 * Derivative(2,0,1,0)[h(g)]
 * </pre>
 */
public class Derivative extends AbstractFunctionEvaluator {

  public Derivative() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IAST[] derivativeAST = ast.isDerivative();
    if (derivativeAST != null) {
      IAST derivativeHead = derivativeAST[0];
      IAST functions = derivativeAST[1];
      if (functions.size() == 2 && functions.arg1().isNumber()) {
        return F.Function(F.C0);
      }
      boolean isZero = true;
      for (int i = 1; i < derivativeHead.size(); i++) {
        if (!derivativeHead.get(i).isZero()) {
          isZero = false;
          break;
        }
      }
      if (isZero) {
        if (derivativeAST[2] == null) {
          if (derivativeAST[1].size() > 1) {
            return derivativeAST[1].arg1();
          }
          return F.NIL;
        }
      }

      // if (derivativeHead.size() == 2) {
      // IExpr nTimes = derivativeHead.arg1();
      // if (functions.size() >= 2) {
      // int n = nTimes.toIntDefault();
      // if (n >= 0 || nTimes.isFree(num -> num.isNumber(), false)) {
      // IAST fullDerivative = derivativeAST[2];
      // return evaluateDArg1IfPossible(
      // nTimes, derivativeHead, (IAST) functions, fullDerivative, engine);
      // }
      // // Multiple derivative specifier `1` does not have the form {variable, n} where n
      // is a
      // // symbolic expression or a non-negative integer.
      // return IOFunctions.printMessage(
      // ast.topHead(), "dvar", F.List(F.List(F.Slot1, nTimes)), engine);
      // }
      // } else
      if (derivativeHead.size() >= 2) {
        IExpr result = F.NIL;
        for (int i = 1; i < derivativeHead.size(); i++) {
          IExpr nTimes = derivativeHead.get(i);
          if (result.isNIL()) {
            result = functions;
          }
          if (result.size() >= 2) {
            int n = nTimes.toIntDefault();
            if (n >= 0 || nTimes.isFree(num -> num.isNumber(), false)) {
              IAST fullDerivative = derivativeAST[2];
              return evaluateDIfPossible(derivativeHead, functions, fullDerivative, engine);
            }
            // Multiple derivative specifier `1` does not have the form {variable, n} where n is a
            // symbolic expression or a non-negative integer.
            return Errors.printMessage(ast.topHead(), "dvar", F.list(F.list(F.Slot1, nTimes)),
                engine);
          }
        }
        if (result.isPresent()) {
          return result;
        }
      }
      if (ast.head().isAST(S.Derivative, 2)) {
        // Derivative(n)
        IAST head = (IAST) ast.head();
        if (head.arg1().isInteger()) {
          try {
            int n = ((IInteger) head.arg1()).toInt();
            IExpr arg1 = ast.arg1();
            if (n >= 0) {
              if (arg1.isFunction()) {
                return derivative(n, (IAST) arg1, engine);
              }
            }
          } catch (ArithmeticException ae) {

          }
        }
        return F.NIL;
      }
    }
    return F.NIL;
  }

  /**
   * Evaluate a <code>Derivative(1)[f]</code> or <code>Derivative(1)[f][x]</code> expression.
   *
   * @param n the number of derivative depth
   * @param head
   * @param headDerivative
   * @param fullDerivative
   * @param engine
   * @return
   */
  private static IExpr evaluateDArg1IfPossible(IExpr n, IAST head, IAST headDerivative,
      IAST fullDerivative, EvalEngine engine) {
    IExpr newFunction;
    IExpr symbol = F.Slot1;
    if (fullDerivative != null) {
      if (fullDerivative.size() != 2) {
        return F.NIL;
      }
      symbol = fullDerivative.arg1();
      if (!symbol.isVariable()) {
        return F.NIL;
      }
    }
    newFunction = engine.evaluate(F.unaryAST1(headDerivative.arg1(), symbol));

    IAST dExpr;
    if (n.isOne()) {
      dExpr = F.D(newFunction, symbol);
    } else {
      int ni = n.toIntDefault();
      if (ni > 0) {
        int iterationLimit = engine.getIterationLimit();
        if (iterationLimit > 0 && iterationLimit < ni) {
          // Iteration limit of `1` exceeded.
          return Errors.printMessage(S.Derivative, "itlim", F.list(F.ZZ(iterationLimit)), engine);
        }
      }
      dExpr = F.D(newFunction, F.list(symbol, n));
    }
    dExpr.setEvalFlags(IAST.IS_DERIVATIVE_EVALED);

    IExpr dResult = engine.evalRules(S.D, dExpr);

    if (dResult.isPresent()) {
      dResult = engine.evaluate(dResult);
      return F.Function(dResult);
    }
    if (!n.isOne()) {
      if (!symbol.isVariable()) {
        return F.NIL;
      }
      int length = n.toIntDefault();
      if (length > 1) {
        for (int i = 0; i < length; i++) {
          dExpr = F.D(newFunction, symbol);
          dExpr.setEvalFlags(IAST.IS_DERIVATIVE_EVALED);
          dResult = engine.evalRules(S.D, dExpr);
          if (dResult.isNIL()) {
            return F.NIL;
          } else {
            newFunction = engine.evaluate(dResult);
          }
        }
        return F.Function(newFunction);
      }
    }
    return F.NIL;
  }

  private static IExpr evaluateDIfPossible(IAST head, IAST headDerivative, IAST fullDerivative,
      EvalEngine engine) {
    IASTAppendable newFunction = F.ast(headDerivative.arg1());
    IASTAppendable list = F.ListAlloc(headDerivative.size());
    IASTAppendable dExpr;
    for (int i = 1; i < head.size(); i++) {
      IExpr n = head.get(i);
      IExpr symbol = F.Slot(i);
      if (fullDerivative != null) {
        if (fullDerivative.size() != headDerivative.size()) {
          return F.NIL;
        }
        symbol = fullDerivative.get(i);
        if (!symbol.isVariable()) {
          return F.NIL;
        }
      }

      newFunction.append(symbol);

      if (n.isOne()) {
        list.append(symbol);
      } else {
        int ni = n.toIntDefault();
        if (ni < 0) {
          if (F.isNotPresent(ni)) {
            list.append(F.list(symbol, n));
          } else {
            return F.NIL;
          }
        } else if (ni > 0) {
          int iterationLimit = engine.getIterationLimit();
          if (iterationLimit > 0 && iterationLimit < ni) {
            // Iteration limit of `1` exceeded.
            return Errors.printMessage(S.Derivative, "itlim", F.list(F.ZZ(iterationLimit)), engine);
          }
          list.append(F.list(symbol, n));
        }
      }
    }
    boolean doEval = false;
    IExpr temp = newFunction;
    if (headDerivative.arg1().isBuiltInSymbol()) {
      IBuiltInSymbol builtin = (IBuiltInSymbol) headDerivative.arg1();
      if (builtin.hasNumericFunctionAttribute()) {
        if (head.isAST1()) {
          int n = head.first().toIntDefault();
          if (n > 0) {
            IExpr dResult = S.Derivative.evalDownRule(engine,
                (n == 1) ? headDerivative : headDerivative.setAtCopy(0, head.setAtCopy(1, F.C1)));
            if (dResult.isPresent()) {
              doEval = true;
            }
          }
        } else {
          IExpr dResult = S.Derivative.evalDownRule(engine, headDerivative);
          if (dResult.isPresent()) {
            doEval = true;
          } else if (builtin == S.Multinomial) {
            return multinomial(head);
          }
        }
      }
    } else {
      temp = engine.evaluateNIL(newFunction);
      if (temp.isPresent()) {
        doEval = true;
      }
    }
    if (doEval) {
      dExpr = F.ast(S.D, list.size() + 1);
      dExpr.append(temp);
      dExpr.appendArgs(list); // w.r.t these symbols
      return F.Function(engine.evaluate(dExpr));
    }
    return F.NIL;
  }

  private static IExpr multinomial(IAST head) {
    final int argSize = head.argSize();
    IASTAppendable multinomial = F.ast(S.Multinomial, argSize);
    IASTAppendable harmonicPlus = F.ast(S.Plus, argSize);
    int countOne = 0;
    int harmonicIndex = -1;
    for (int i = 1; i <= argSize; i++) {
      final IAST slot = F.Slot(i);
      multinomial.append(slot);
      harmonicPlus.append(slot);
      if (head.get(i).isOne()) {
        harmonicIndex = i;
        countOne++;
      }
    }
    if (countOne == 1) {
      // (-HarmonicNumber(#[i])+HarmonicNumber(harmonicPlus))*multinomial&
      return F.Function(F.Times(
          F.Plus(F.Negate(F.HarmonicNumber(F.Slot(harmonicIndex))), F.HarmonicNumber(harmonicPlus)),
          multinomial));
    }
    return F.NIL;
  }

  /**
   * @param n
   * @param function
   * @param engine
   * @return
   */
  private static IExpr derivative(int n, IAST function, EvalEngine engine) {
    if (n == 0) {
      return function;
    }
    if (n >= 1) {
      if (function.isAST1()) {
        // Derivative[1][(...)&]
        IExpr arg1 = function.arg1();
        if (arg1.isPower()) {
          IExpr exponent = arg1.exponent();
          if (arg1.base().equals(F.Slot1) && exponent.isFree(F.Slot1)) {
            return F.Times(exponent, createDerivative(n - 1,
                F.unaryAST1(S.Function, engine.evaluate(F.Power(F.Slot1, exponent.dec())))));
          }
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NHOLDALL);
    super.setUp(newSymbol);
  }

  /**
   * Create <code>Derivative(n)[header][arg1]</code>
   *
   * @param n
   * @param header
   * @param arg1
   * @returnW
   */
  public static IAST createDerivative(final int n, final IExpr header, final IExpr arg1) {
    IAST deriv = F.Derivative(F.ZZ(n));
    IASTAppendable fDeriv = F.ast(deriv);
    fDeriv.append(header);
    IASTAppendable fDerivParam = F.ast(fDeriv);
    fDerivParam.append(arg1);
    return fDerivParam;
  }

  /**
   * Create <code>Derivative(n)[header]</code>
   *
   * @param n
   * @param header
   * @return
   */
  public static IAST createDerivative(final int n, final IExpr header) {
    IAST deriv = F.Derivative(F.ZZ(n));
    IASTAppendable fDeriv = F.ast(deriv);
    fDeriv.append(header);
    return fDeriv;
  }
}
