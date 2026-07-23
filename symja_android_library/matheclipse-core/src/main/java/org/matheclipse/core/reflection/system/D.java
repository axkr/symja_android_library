package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.DLeibnitzRule;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.BinaryBindIth1st;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IArraySymbol;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ISymbolicArray;
import com.google.common.math.LongMath;

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
 * </p>
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
 * </p>
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
 * </p>
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
 * </p>
 *
 * </blockquote>
 *
 * <pre>
 * D(f, x, NonConstants -&gt; {u1, ...})
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * specifies that <code>ui</code> depends on <code>x</code> and therefore does not have zero partial
 * derivative.
 *
 * </p>
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
 *
 * <p>
 * With <code>NonConstants</code> the symbol <code>a</code> is assumed to depend on <code>x</code>:
 *
 * <pre>
 * &gt;&gt; D(a*x^2, x, NonConstants -&gt; {a})
 * 2*a*x+x^2*D(a,x,NonConstants-&gt;{a})
 * </pre>
 */
public class D extends AbstractFunctionOptionEvaluator {
  private static final IStringX FUNCTION_RULE_STR = F.$str("FunctionRule");

  public D() {}

  /**
   * Search for one of the <code>Derivative[a1][head]</code> rules.
   * 
   * @param functionArg1
   * @param x
   * @param engine
   *
   * @return
   */
  private static IExpr chainRuleArg1(final IAST functionArg1, IExpr x, EvalEngine engine) {
    final ISymbol header = (ISymbol) functionArg1.head();
    IExpr arg1 = functionArg1.arg1();
    IAST fDerivParam = Derivative.createDerivative(1, header, arg1);
    IAST dDxArgFunction = F.D(functionArg1, x);
    if (x.equals(arg1)) {
      IExpr evaluated = engine.evaluate(fDerivParam);
      return engine.addTraceStep(dDxArgFunction, evaluated, S.D, FUNCTION_RULE_STR, header,
          fDerivParam.head().first());
    }
    IExpr formula = F.Times(F.D(arg1, x), fDerivParam);
    return engine.addEvaluatedTraceStep(dDxArgFunction, formula, "ChainRule");
  }

  /**
   * Search for one of the <code>Derivative[a1, a2][head]</code> rules.
   *
   * @param x
   * @param ast
   * @param head
   * @return
   */
  private static IExpr getDerivativeArgN(IExpr x, final IAST ast, final IExpr head,
      EvalEngine engine) {
    if (head instanceof IBuiltInSymbol) {
      int id = ((IBuiltInSymbol) head).ordinal();
      if (id == ID.LaplaceTransform //
          || id == ID.InverseLaplaceTransform //
          || id == ID.ZTransform //
          || id == ID.InverseZTransform) {
        return F.NIL;
      }
    }
    IAST[] deriv = ast.isDerivative();
    int size = ast.size();
    if (deriv != null) {
      IASTAppendable plus = F.PlusAlloc(size);
      ast.forEach(size, (expr, i) -> {
        if (!expr.isFree(x)) { // Cut off AST explosion for constant terms
          plus.append(F.Times(F.D(expr, x), addDerivative(i, deriv[0], deriv[1].arg1(), ast)));
        }
      });
      return engine.addTraceStep(ast, plus, "ChainRule");
    }
    if (head.isSymbol()) {
      IASTAppendable plus = F.PlusAlloc(size);
      ast.forEach(size, (expr, i) -> {
        plus.append(F.Times(F.D(expr, x), createDerivative(i, head, ast)));
      });
      return engine.addTraceStep(ast, plus, "ChainRule");
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

  /**
   * Find rule for <code>Derivative(0,...,n,...,n,...)[header]</code>. Set <code>0</code> where the
   * arguments in <code>args</code> are free of <code>x</code>. Set <code>1</code> where the
   * arguments in <code>args</code> equals <code>x</code>. Return {@link F.NIL} otherwise.
   * 
   * @param header
   * @param args
   * @param x
   * @param n
   * @return
   */
  private static IAST createDerivativeN(final IExpr header, final IAST args, IExpr x, IExpr n) {
    final int size = args.size();
    IASTAppendable derivativeHead1 = F.ast(S.Derivative, size);
    boolean evaled = false;
    for (int i = 1; i < size; i++) {
      if (args.get(i).equals(x)) {
        derivativeHead1.append(n);
        evaled = true;
      } else if (args.get(i).isFree(x)) {
        derivativeHead1.append(F.C0);
      } else {
        return F.NIL;
      }
    }
    if (evaled) {
      IASTAppendable derivativeHead2 = F.ast(derivativeHead1);
      derivativeHead2.append(header);
      IASTAppendable derivativeAST = F.ast(derivativeHead2, size);
      derivativeAST.appendArgs(args);
      return derivativeAST;
    }
    return F.NIL;
  }

  private static IAST addDerivative(final int pos, IAST deriveHead, final IExpr header,
      final IAST args) {
    IASTMutable derivativeHead1 = deriveHead.copy();
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
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    for (int i = 2; i <= argSize; i++) {
      if (isOptionLike(ast.get(i))) {
        // an unknown option was given - leave the expression unevaluated
        return F.NIL;
      }
    }
    if (argSize == ast.argSize()) {
      // no option rules were stripped from the end of the arguments
      return evaluateD(ast, engine);
    }

    IASTAppendable dAST = ast.copyUntil(argSize + 1);
    dAST.addEvalFlags(ast.getEvalFlags() & IAST.IS_DERIVATIVE_EVALED);
    IExpr nonConstants = options.length > 0 && options[0] != null ? options[0] : F.CEmptyList;
    IAST nonConstantsList = normalizeNonConstants(nonConstants);
    if (nonConstantsList.isEmpty()) {
      return evaluateD(dAST, engine);
    }
    return nonConstantsD(dAST, nonConstantsList, ast, engine);
  }

  /**
   * Test if <code>arg</code> is a rule or a list of rules and therefore can't be a valid
   * differentiation variable specification.
   *
   * @param arg
   * @return
   */
  private static boolean isOptionLike(IExpr arg) {
    return arg.isRuleAST() || (arg.isListOfRules(true) && !arg.isEmptyList());
  }

  private static IExpr evaluateD(final IAST ast, EvalEngine engine) {
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
      if (fx.isAST(S.Equal)) {
        return fx.mapThread(F.D(F.Slot1, x), 1);
      }

      if (!(x.isVariable() || x.isList())) {
        // `1` is not a valid variable.
        return Errors.printMessage(ast.topHead(), "ivar", F.list(x), engine);
      }

      if (fx.isList()) {
        IAST list = (IAST) fx;
        // thread over first list
        return list.mapThreadEvaled(engine, F.ListAlloc(list.size()), ast, 1);
      }

      if (x instanceof IArraySymbol) {
        if (fx.isFree(x, true)) {
          IExpr result = freeOfX(fx, engine);
          if (result.isAST(S.SymbolicZerosArray, 2)) {
            return result;
          }
          return F.SymbolicZerosArray(((IArraySymbol) x).getDimensions());
        }
        if (fx.equals(x)) {
          return F.SymbolicIdentityArray(((IArraySymbol) x).getDimensions());
        }
        return F.NIL;
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
          IExpr xListN = xList.arg2();
          if (xList.arg1().isList()) {
            x = F.list(xList.arg1());
          } else {
            x = xList.arg1();
            if (fx.isAST()) {
              if (xListN.isNegativeResult()
                  || (!xListN.isInteger() && xListN.isNumericFunction())) {
                // Multiple derivative specifier `1` does not have the form {variable, n} where n is
                // a symbolic expression or a non-negative integer.
                return Errors.printMessage(S.D, "dvar", F.List(xList), engine);
              }
            }
          }
          IExpr arg2 = xListN;
          int n = arg2.toIntDefault();
          if (n >= 0) {
            if (fx.isTimes() && fx.argSize() >= 2 && x.isVariable()) {
              IAST timesAST = (IAST) fx;
              int k = timesAST.argSize();
              final IExpr v = x;
              IASTAppendable[] filter = timesAST.filter(m -> m.isFree(v));
              if (filter[0].size() > 1) {
                return F.Times(filter[0], F.D(filter[1], xList));
              }
              int binomialArg1 = n + k - 1;
              if (binomialArg1 < 0) {
                throw new ASTElementLimitExceeded(binomialArg1);
              }
              long numberOfTerms = LongMath.binomial(binomialArg1, k - 1);
              if (numberOfTerms > Config.MAX_AST_SIZE || numberOfTerms >= Integer.MAX_VALUE) {
                throw new ASTElementLimitExceeded(numberOfTerms);
              }
              IExpr result =
                  DLeibnitzRule.nThDerivative(timesAST, x, n, (int) numberOfTerms, engine);
              // IExpr result = generalizedProductRule((IAST) fx, x, n, engine);
              // if (!result.isNIL()) {
              return result;
              // }
            }
            if (n >= 2 && fx.isAST1() && fx.head().isSymbol() && !fx.first().isFree(x, true)) {
              IExpr head = fx.head();
              if (head.equals(S.Boole)) {
                return F.C0;
              }
              IExpr gx = fx.first();
              IExpr faaDiBrunoResult = faaDiBrunoChainRule(head, gx, x, n, ast, engine);
              if (!faaDiBrunoResult.isNIL()) {
                return faaDiBrunoResult;
              }
            }

            IExpr temp = fx;
            for (int i = 0; i < n; i++) {
              temp = S.D.ofNIL(engine, temp, x);
              if (temp.isNIL() || temp.isZero()) {
                return temp;
              }
              // Prune AST explosion by grouping like-terms and expanding products
              // before feeding the AST into the next derivative iteration.
              if (i < n - 1 && temp.isAST() && temp.leafCount() > 64) {
                temp = engine.evaluate(F.Expand(temp));
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
            if (fx.isPlus()) {
              // D(a_+b_+c_,x_) -> D(a,x)+D(b,x)+D(c,x)
              return fx.mapThread(F.D(F.Slot1, xList), 1);
            }
            if (fx.isTimes()) {
              IAST timesAST = (IAST) fx;
              final IExpr v = x;
              IASTAppendable[] filter = timesAST.filter(m -> m.isFree(v));
              if (filter[0].size() > 0) {
                return F.Times(filter[0], F.D(filter[1], xList));
              }
            }
            if (fx.isPower() && fx.base().isE() && fx.exponent().equals(x)) {
              // D(E^x, x) -> E^x
              return F.Power(S.E, x);
            }
            return F.NIL;
          }
          if (!x.isVariable()) {
            // `1` is not a valid variable.
            return Errors.printMessage(ast.topHead(), "ivar", F.list(x), engine);
          }
          if (arg2.isAST()) {
            return F.NIL;
          }
          // Multiple derivative specifier `1` does not have the form {variable, n} where n is a
          // symbolic expression or a non-negative integer.
          return Errors.printMessage(ast.topHead(), "dvar", F.list(xList), engine);
        }
        return F.NIL;
      }

      if (!x.isVariable()) {
        // `1` is not a valid variable.
        return Errors.printMessage(ast.topHead(), "ivar", F.list(x), engine);
      }
      return binaryD(fx, x, ast, engine);
    } catch (final ValidateException ve) {
      // int number validation
      return Errors.printMessage(S.D, ve, engine);
    }
  }

  /**
   * Evaluate <code>D(functionOfX, x)</code> for some general cases.
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
      if (series.expansionVariable().equals(x)) {
        final IExpr temp = series.derive(x);
        if (temp != null) {
          return temp;
        }
        return F.NIL;
      }
      // x is not the expansion variable.
      // If the entire series (coefficients + expansion point) is free of x, return 0.
      if (series.isFree(x, true)) {
        return F.C0;
      }
      // Otherwise differentiate through the series (chain rule on expansion point and/or
      // coefficient differentiation).
      final IExpr temp = series.derive(x);
      if (temp != null) {
        return temp;
      }
      return F.NIL;
    }
    if (functionOfX.isFree(x, true)) {
      return freeOfX(functionOfX, engine);
    }

    if (functionOfX.isNumber()) {
      // D[x_?NumberQ,y_] -> 0
      engine.addTraceStep(() -> F.D(functionOfX, x), F.C0,
          F.List(S.D, F.$str("ConstantRule"), F.C0));
      return F.C0;
    }
    if (functionOfX.equals(x)) {
      // D[x_,x_] -> 1
      engine.addTraceStep(() -> F.D(functionOfX, x), F.C1,
          F.List(S.D, F.$str("IdentityRule"), F.C1));
      return F.C1;
    }

    if (functionOfX.isAST()) {
      final IAST function = (IAST) functionOfX;
      IExpr head = function.head();

      // Dispatch using a switch statement on the symbol's ordinal value
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Plus:
            // D(a_+b_+c_,x_) -> D(a,x)+D(b,x)+D(c,x)
            IExpr plusResult = function.mapThread(F.D(F.Slot1, x), 1);
            if (plusResult.isPolynomial(x)) {
              return engine.addEvaluatedTraceStep(ast, plusResult, "PolynomialPowerRule");
            }
            // Apply the sum/difference rule $(f \pm g)' = f' \pm g'$.
            return engine.addEvaluatedTraceStep(ast, plusResult, "PlusRule");

          case ID.Times:
            if (function.argSize() > 4) {
              // Apply Logarithmic Differentiation for products with 5 or more terms
              // to prevent combinatorial AST explosion of the standard product rule.
              IExpr logDeriv = logarithmicDerivative(function, x, engine);
              return engine.addEvaluatedTraceStep(F.D(function, x), logDeriv, S.D,
                  F.$str("LogarithmicDerivativeRule"));
            }
            IExpr result =
                function.map(F.PlusAlloc(16), new BinaryBindIth1st(function, F.D(S.Null, x)));
            return engine.addEvaluatedTraceStep(F.D(function, x), result, S.D, F.$str("MulRule"));

          case ID.Power:
            return power(function, x, engine);

          case ID.Surd:
            // Surd(f,g)
            if (function.argSize() == 2) {
              return surd(function, x, engine);
            }
            break;

          case ID.Log:
            if (function.argSize() == 2) {
              if (function.isFreeAt(1, x)) {
                // D(Log(i_FreeQ(x), x_), z_):= (x*Log(a))^(-1)*D(x,z);
                IExpr res =
                    F.Times(F.Power(F.Times(function.arg2(), F.Log(function.arg1())), F.CN1),
                        F.D(function.arg2(), x));
                return engine.addEvaluatedTraceStep(F.D(function, x), res, "LogRule");
              }
            }
            break;

          case ID.HypergeometricPFQ:
            if (function.argSize() == 3 && function.first().isList()
                && function.second().isList()) {
              return hypergeometricPFQ(function, x);
            }
            break;

          case ID.Integrate:
            if (function.argSize() == 2 && function.second().isList3()
                && function.second().getAt(3).equals(x)) {
              // D(Integrate(f(t), {t, a, x}),x) -> f(x)
              // https://en.wikipedia.org/wiki/Fundamental_theorem_of_calculus#First_part
              IAST list = (IAST) function.second();
              IExpr t = list.arg1();
              if (t.isFree(x, true) && list.arg2().isFree(x, true) && list.arg2().isFree(t, true)) {
                return F.subst(function.arg1(), arg -> arg.equals(t) ? x : F.NIL);
              }
            }
            break;

          case ID.Boole:
            if (function.argSize() == 1) {
              return F.C0;
            }
            break;

          case ID.RootSum:
            if (function.argSize() == 2) {
              IExpr f = function.arg1();
              IExpr form = function.arg2();
              // Evaluate derivation of the form inside the RootSum if the polynomial f is free of x
              if (f.isFree(x, true)) {
                ISymbol r = F.Dummy("r");
                IExpr formR = engine.evaluate(F.unaryAST1(form, r));
                IExpr dFormR = engine.evaluate(F.D(formR, x));

                // Safely construct a pure function trapping the dummy variable to prevent variable
                // collision
                IExpr newForm = F.Function(r, dFormR);
                // Force evaluation of the new RootSum so we return the resultant-resolved Rational
                // function
                IExpr rootSumResult = engine.evaluate(F.binaryAST2(S.RootSum, f, newForm));
                return engine.addEvaluatedTraceStep(ast, rootSumResult, "RootSumRule");
              }
            }
            break;
        }
      }

      // Fallback for AST1 and other derivatives
      if (function.isAST1() && ast.isEvalFlagOff(IAST.IS_DERIVATIVE_EVALED)) {
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
        if (function.head().isSymbol()) {
          return chainRuleArg1(function, x, engine);
        }
        return F.NIL;
      }
      if (ast.isEvalFlagOff(IAST.IS_DERIVATIVE_EVALED)) {
        return getDerivativeArgN(x, function, function.head(), engine);
      }
    }
    return F.NIL;
  }

  /**
   * Computes the n-th derivative of a composite function f(g(x)) using Faà di Bruno's formula via
   * BellY partial polynomials.
   * 
   * @param head the outer function symbol or expression (f)
   * @param gx the inner function expression (g(x))
   * @param x the variable of differentiation
   * @param n the order of differentiation
   * @param ast the original AST for error tracking
   * @param engine the evaluation engine
   * @return the n-th derivative expression, or F.NIL if not applicable
   */
  private static IExpr faaDiBrunoChainRule(final IExpr head, final IExpr gx, final IExpr x, int n,
      final IAST ast, EvalEngine engine) {
    // We need the derivatives of g(x) from order 1 up to n for the BellY variables
    int varsSize = n;
    IASTAppendable gDerivatives = F.ListAlloc(varsSize);

    for (int m = 1; m <= n; m++) {
      IExpr gDeriv = engine.evaluate(F.D(gx, F.list(x, F.ZZ(m))));
      if (gDeriv.isZero() && m == 1) {
        return F.C0; // If g'(x) is 0, higher derivatives of the composition are 0
      }
      gDerivatives.append(gDeriv);
    }

    IASTAppendable sum = F.PlusAlloc(n);
    IExpr[][] cache = new IExpr[n + 1][n + 1];

    for (int k = 1; k <= n; k++) {
      // Component 1: The k-th derivative of the outer function evaluated at g(x) -> f^(k)(g(x))
      IAST fDerivativeHead = Derivative.createDerivative(k, head, gx);

      // Component 2: BellY(n, k, {g'(x), g''(x), ..., g^(n-k+1)(x)})
      // We pass the list of variables directly to the BellY implementation
      IExpr bellPoly = BellY.bellY(n, k, gDerivatives, ast, engine, cache, false);

      if (!bellPoly.isZero()) {
        sum.append(F.Times(fDerivativeHead, bellPoly));
      }
    }

    return engine.evaluate(sum);
  }

  private static IExpr freeOfX(final IExpr functionOfX, EvalEngine engine) {
    if (functionOfX.isFree(
        t -> (t instanceof IArraySymbol) || t.headInstanceOf(ISymbolicArray.class) != null,
        false)) {
      return F.C0;
    }
    IExpr dimensions = engine.evaluateNIL(F.TensorDimensions(functionOfX));
    if (dimensions.isList()) {
      return F.SymbolicZerosArray(dimensions);
    }
    return F.NIL;
  }

  /**
   * Applies Logarithmic Differentiation: D(y, x) = y * D(Log(y), x). This avoids the combinatorial
   * AST explosion of the standard product rule for large multiplications and powers, keeping the
   * result strictly factored.
   * 
   * @param timesAST the product expression to differentiate
   * @param x the variable of differentiation
   * @param engine the evaluation engine
   * @return the logarithmically differentiated expression
   */
  private static IExpr logarithmicDerivative(final IAST timesAST, IExpr x, EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(timesAST.argSize());
    IASTAppendable constants = F.TimesAlloc(timesAST.argSize());
    IASTAppendable variables = F.TimesAlloc(timesAST.argSize());

    for (int i = 1; i <= timesAST.argSize(); i++) {
      IExpr fi = timesAST.get(i);
      if (fi.isFree(x)) {
        constants.append(fi);
      } else {
        variables.append(fi);

        // Fast path for nested powers to prevent unnecessary expansion
        if (fi.isPower()) {
          IExpr base = fi.base();
          IExpr exp = fi.exponent();
          if (exp.isFree(x)) {
            sum.append(F.Times(exp, F.D(base, x), F.Power(base, F.CN1)));
          } else {
            sum.append(F.Plus(F.Times(exp, F.D(base, x), F.Power(base, F.CN1)),
                F.Times(F.D(exp, x), F.Log(base))));
          }
        } else {
          // General case: f' / f
          sum.append(F.Times(F.D(fi, x), F.Power(fi, F.CN1)));
        }
      }
    }

    if (variables.argSize() == 0) {
      return F.C0;
    }

    IExpr y = engine.evaluate(variables);
    IExpr dy = engine.evaluate(F.Expand(F.Times(y, sum)));

    if (constants.argSize() > 0) {
      return engine.evaluate(F.Times(constants, dy));
    }
    return dy;
  }

  private static IExpr power(final IAST function, IExpr x, EvalEngine engine) {
    // f ^ g
    final IExpr f = function.base();
    final IExpr g = function.exponent();
    if (g.isFree(x)) {
      if (g.isMinusOne()) {
        // -D(f,x) / (f^2)
        IExpr result = F.Times(F.CN1, F.D(f, x), F.Power(f, F.CN2));
        return engine.addEvaluatedTraceStep(F.D(function, x), result, "ReciprocalRule");
      }
      // g*D(f,y)*f^(g-1)
      IExpr result = F.Times(g, F.D(f, x), F.Power(f, g.dec()));
      return engine.addEvaluatedTraceStep(F.D(function, x), result, "PowerRule");
    }
    if (f.isFree(x)) {
      if (f.isE()) {
        IExpr result = F.Times(F.D(g, x), F.Exp(g));
        return engine.addEvaluatedTraceStep(F.D(function, x), result, "ExpRule");
      }
      // D(g,y)*Log(f)*f^g
      IExpr result = F.Times(F.D(g, x), F.Log(f), F.Power(f, g));
      return engine.addEvaluatedTraceStep(F.D(function, x), result, "LogRule");
    }

    // D[f_^g_,y_]:= f^g*(((g*D[f,y])/f)+Log[f]*D[g,y])
    final IASTAppendable resultList = F.TimesAlloc(2);
    resultList.append(F.Power(f, g));
    resultList
        .append(F.Plus(F.Times(g, F.D(f, x), F.Power(f, F.CN1)), F.Times(F.Log(f), F.D(g, x))));
    return engine.addEvaluatedTraceStep(F.D(function, x), resultList, "PowerRule");
  }

  private static IExpr surd(final IAST function, IExpr x, EvalEngine engine) {
    final IExpr f = function.base();
    if (function.exponent().isInteger()) {
      final IInteger g = (IInteger) function.exponent();
      if (g.isMinusOne()) {
        IExpr result = F.Times(F.CN1, F.D(f, x), F.Power(f, F.CN2));
        return engine.addEvaluatedTraceStep(F.D(function, x), result, "PowerRule");

      }
      final IRational gInverse = g.inverse();
      if (g.isNegative()) {
        if (g.isEven()) {
          IExpr result = F.Times(gInverse, F.D(f, x), F.Power(F.Surd(f, g.negate()), g.dec()));
          return engine.addEvaluatedTraceStep(F.D(function, x), result, "PowerRule");

        }
        IExpr result =
            F.Times(gInverse, F.D(f, x), F.Power(f, F.CN1), F.Power(F.Surd(f, g.negate()), F.CN1));
        return engine.addEvaluatedTraceStep(F.D(function, x), result, "PowerRule");

      }
      return F.Times(gInverse, F.D(f, x), F.Power(F.Surd(f, g), g.dec().negate()));
    }
    return F.NIL;
  }

  private static IExpr hypergeometricPFQ(final IAST function, IExpr x) {
    IAST list1 = (IAST) function.first();
    IAST list2 = (IAST) function.second();
    if (list1.isFree(x) && list2.isFree(x)) {
      IExpr arg3 = function.arg3();
      if (list1.isEmpty() && list2.isEmpty()) {
        return F.Times(F.Exp(arg3), F.D(arg3, x));
      }
      IExpr timesNumerator = list1.argSize() == 0 ? F.C1 : list1.apply(S.Times, 1);
      IExpr timesDenominator = list2.argSize() == 0 ? F.C1 : list2.apply(S.Times, 1);
      IASTAppendable newList1 = F.ListAlloc(list1.argSize());

      for (int i = 1; i < list1.size(); i++) {
        newList1.append(F.Plus(F.C1, list1.get(i)));
      }
      IASTAppendable newList2 = F.ListAlloc(list2.argSize());
      for (int i = 1; i < list2.size(); i++) {
        newList2.append(F.Plus(F.C1, list2.get(i)));
      }
      return F.Times(timesNumerator, F.Power(timesDenominator, F.CN1), //
          F.HypergeometricPFQ(newList1, newList2, arg3), //
          F.D(arg3, x));
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

  /**
   * Normalize the value of the {@link S#NonConstants} option to a list of expressions.
   *
   * @param nonConstants the option value
   * @return an empty list if no &quot;non constant&quot; expressions were specified
   */
  private static IAST normalizeNonConstants(IExpr nonConstants) {
    if (nonConstants.isList()) {
      return (IAST) nonConstants;
    }
    if (nonConstants == S.None || nonConstants == S.Automatic || nonConstants.isFalse()) {
      return F.CEmptyList;
    }
    return F.list(nonConstants);
  }

  /**
   * Differentiate <code>dAST</code> under the assumption, that all expressions in
   * <code>nonConstantsList</code> depend on the differentiation variables.
   *
   * <p>
   * Every <code>ui</code> from <code>nonConstantsList</code> is temporarily replaced by
   * <code>gi(x1, x2, ...)</code> for a fresh &quot;dummy&quot; symbol <code>gi</code> and the
   * differentiation variables <code>x1, x2, ...</code>. That way the usual derivative rules can be
   * used unchanged. In the result the generated <code>Derivative(...)[gi][x1, x2, ...]</code>
   * expressions are mapped back to unevaluated <code>D(ui, ..., NonConstants-&gt;{...})</code>
   * expressions.
   *
   * @param dAST the <code>D(f, x, ...)</code> expression without the option rules
   * @param nonConstantsList the expressions which depend on the differentiation variables
   * @param originalAST the original <code>D(...)</code> expression including the option rules
   * @param engine the evaluation engine
   * @return {@link F#NIL} if no evaluation was possible
   */
  private static IExpr nonConstantsD(final IAST dAST, final IAST nonConstantsList,
      final IAST originalAST, EvalEngine engine) {
    final IExpr fx = dAST.arg1();
    Set<IExpr> variables = new LinkedHashSet<IExpr>();
    for (int i = 2; i < dAST.size(); i++) {
      collectVariables(dAST.get(i), variables);
    }
    if (variables.isEmpty()) {
      return evaluateD(dAST, engine);
    }

    Map<IExpr, IExpr> forward = new HashMap<IExpr, IExpr>();
    Map<IExpr, IExpr> backward = new HashMap<IExpr, IExpr>();
    for (int i = 1; i < nonConstantsList.size(); i++) {
      IExpr u = nonConstantsList.get(i);
      if (variables.contains(u) || fx.isFree(u, true)) {
        // a differentiation variable can't be a "non constant" expression and an expression which
        // doesn't occur in `fx` doesn't have to be substituted
        continue;
      }
      ISymbol dummy = F.Dummy();
      IASTAppendable dummyFunction = F.ast(dummy, variables.size());
      for (IExpr variable : variables) {
        dummyFunction.append(variable);
      }
      forward.put(u, dummyFunction);
      backward.put(dummy, u);
    }
    if (forward.isEmpty()) {
      return evaluateD(dAST, engine);
    }

    IASTAppendable dCall = F.ast(S.D, dAST.size());
    dCall.append(F.subst(fx, forward));
    for (int i = 2; i < dAST.size(); i++) {
      dCall.append(dAST.get(i));
    }
    IExpr result = engine.evaluate(dCall);
    IExpr backSubstituted = backSubstitute(result, backward, nonConstantsList);
    if (backSubstituted.isNIL() || backSubstituted.equals(originalAST)) {
      return F.NIL;
    }
    return backSubstituted;
  }

  /**
   * Replace the generated &quot;dummy functions&quot; in <code>expr</code> by the original
   * expressions of the {@link S#NonConstants} option.
   *
   * @param expr
   * @param backward maps a generated dummy symbol to the original expression
   * @param nonConstantsList the value of the {@link S#NonConstants} option
   * @return
   */
  private static IExpr backSubstitute(IExpr expr, final Map<IExpr, IExpr> backward,
      final IAST nonConstantsList) {
    return F.subst(expr, node -> {
      IAST[] deriv = node.isDerivative();
      if (deriv != null) {
        if (deriv[2] != null) {
          IExpr original = backward.get(deriv[1].arg1());
          if (original != null) {
            return derivativeAsD(deriv[0], deriv[2], original, nonConstantsList);
          }
        }
        return F.NIL;
      }
      if (node.isAST(S.D) && node.size() > 2
          && !node.isFree(x -> backward.containsKey(x), true)) {
        // an unevaluated D(...) expression which still contains a dummy function
        IASTAppendable dResult = F.ast(S.D, node.size() + 1);
        boolean hasOption = false;
        for (int i = 1; i < node.size(); i++) {
          IExpr arg = node.get(i);
          if (i > 1 && arg.isRuleAST() && arg.first().equals(S.NonConstants)) {
            hasOption = true;
            dResult.append(F.Rule(S.NonConstants, nonConstantsList));
          } else {
            dResult.append(backSubstitute(arg, backward, nonConstantsList));
          }
        }
        if (!hasOption) {
          dResult.append(F.Rule(S.NonConstants, nonConstantsList));
        }
        return dResult;
      }
      if (node.isAST() && backward.containsKey(node.head())) {
        return backward.get(node.head());
      }
      IExpr original = backward.get(node);
      return original == null ? F.NIL : original;
    });
  }

  /**
   * Map <code>Derivative(n1, n2, ...)[gi][x1, x2, ...]</code> back to an unevaluated
   * <code>D(ui, ..., NonConstants-&gt;{...})</code> expression.
   *
   * @param orders the <code>Derivative(n1, n2, ...)</code> part
   * @param args the arguments <code>x1, x2, ...</code> of the derivative
   * @param original the original expression <code>ui</code> of the {@link S#NonConstants} option
   * @param nonConstantsList the value of the {@link S#NonConstants} option
   * @return
   */
  private static IExpr derivativeAsD(final IAST orders, final IAST args, final IExpr original,
      final IAST nonConstantsList) {
    IASTAppendable dResult = F.ast(S.D, args.size() + 1);
    dResult.append(original);
    for (int i = 1; i < args.size(); i++) {
      IExpr n = orders.get(i);
      if (n.isZero()) {
        continue;
      }
      if (n.isOne()) {
        dResult.append(args.get(i));
      } else {
        dResult.append(F.list(args.get(i), n));
      }
    }
    if (dResult.argSize() == 1) {
      // all derivative orders are 0
      return original;
    }
    dResult.append(F.Rule(S.NonConstants, nonConstantsList));
    return dResult;
  }

  /**
   * Collect the differentiation variables of a single <code>D(f, spec)</code> variable
   * specification <code>spec</code>.
   *
   * @param spec a variable, <code>{x, n}</code>, <code>{{x1, x2, ...}}</code> or
   *        <code>{{x1, x2, ...}, n}</code>
   * @param variables the resulting set of variables
   */
  private static void collectVariables(IExpr spec, Set<IExpr> variables) {
    if (spec.isList()) {
      IAST list = (IAST) spec;
      if (list.isAST2()) {
        // {x, n} or {{x1, x2, ...}, n}
        collectAllVariables(list.arg1(), variables);
      } else {
        collectAllVariables(list, variables);
      }
    } else if (spec.isVariable()) {
      variables.add(spec);
    }
  }

  private static void collectAllVariables(IExpr expr, Set<IExpr> variables) {
    if (expr.isList()) {
      ((IAST) expr).forEach(x -> collectAllVariables(x, variables));
    } else if (expr.isVariable()) {
      variables.add(expr);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, S.NonConstants, F.CEmptyList);
  }
}
