package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.DLeibnitzRule;
import org.matheclipse.core.eval.DSymbolicOrder;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.ArrayDerivative;
import org.matheclipse.core.eval.SymbolicArrayUtil;
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
    final IExpr header = functionArg1.head();
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
      if (ast.exists(arg -> arg.isList())) {
        // an iterator or option list like in Table(f, {k, 1, n}) has no partial derivative
        return F.NIL;
      }
    }
    IAST[] deriv = ast.isDerivative();
    int size = ast.size();
    if (deriv != null) {
      if (deriv[2] == null) {
        // the operator Derivative(n)[f] applied to nothing
        return F.NIL;
      }
      IASTAppendable plus = F.PlusAlloc(size);
      ast.forEach(size, (expr, i) -> {
        if (!expr.isFree(x)) { // Cut off AST explosion for constant terms
          plus.append(F.Times(F.D(expr, x), addDerivative(i, deriv[0], deriv[1].arg1(), ast)));
        }
      });
      return engine.addTraceStep(ast, plus, "ChainRule");
    }
    if (isFunctionHead(head, x)) {
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
   * Create <code>Derivative(0,...,n,...,0)[header][args]</code> for the <code>n</code>-th derivative
   * with respect to <code>x</code>, if <code>x</code> is exactly one of the arguments in
   * <code>args</code> and all other arguments are free of <code>x</code>. Return {@link F#NIL}
   * otherwise - an argument depending on <code>x</code> would need the chain rule, which has no
   * closed form for a symbolic order.
   *
   * @param header
   * @param args
   * @param x
   * @param n the symbolic order
   * @return
   */
  private static IAST createDerivativeN(final IExpr header, final IAST args, IExpr x, IExpr n) {
    final int size = args.size();
    IASTAppendable derivativeHead1 = F.ast(S.Derivative, size);
    boolean evaled = false;
    for (int i = 1; i < size; i++) {
      if (args.get(i).equals(x) && !evaled) {
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

  /**
   * Test if <code>head(args...)</code> is a function whose partial derivatives can be written as
   * <code>Derivative(...)[head][args...]</code>: a symbol, or a compound head like
   * <code>f(a)</code> which doesn't depend on <code>x</code>.
   *
   * @param head
   * @param x the differentiation variable
   * @return
   */
  private static boolean isFunctionHead(IExpr head, IExpr x) {
    if (head.isSymbol()) {
      return true;
    }
    // D(f(a)[x], x) -> Derivative(1)[f(a)][x]; an operator Derivative(n)[f] applied to the wrong
    // number of arguments stays unevaluated
    return head.isAST() && !head.head().isAST(S.Derivative) && head.isFree(x, true);
  }

  /**
   * Test if the <code>n</code>-th derivative of a function with this head can be written as
   * <code>Derivative(..., n, ...)[head][...]</code> for a symbolic order <code>n</code>: a user
   * defined function symbol or a built-in numeric function. Structural built-ins like
   * <code>Piecewise</code> or <code>Integrate</code> are excluded.
   *
   * @param head
   * @return
   */
  private static boolean isSymbolicOrderHead(IExpr head) {
    if (!head.isSymbol() || head == S.Plus || head == S.Times || head == S.Power) {
      return false;
    }
    return !head.isBuiltInSymbol() || ((ISymbol) head).hasNumericFunctionAttribute();
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
    IExpr nonConstants = options.length > 0 && options[0] != null ? options[0] : F.CEmptyList;
    IAST nonConstantsList = normalizeNonConstants(nonConstants);
    if (nonConstantsList.isEmpty()) {
      // the down rules of D only saw the expression with the options; give them the plain form
      IExpr ruleResult = S.D.evalDownRule(engine, dAST);
      if (ruleResult.isPresent()) {
        return ruleResult;
      }
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

  /**
   * Test if <code>x</code> can be used as the differentiation variable of <code>D(fx, x)</code>.
   *
   * <p>
   * Besides symbols, a general function expression like <code>x(k)</code> or <code>Sin(x)</code> is
   * accepted and is differentiated as if it were an independent variable. Sums, products and powers
   * are structured arithmetic expressions and are rejected with message <code>General::ivar</code>,
   * so <code>D(y, x^2)</code> or <code>D(y, 2*x)</code> stay unevaluated.
   *
   * @param x the second argument of <code>D</code>
   * @return <code>true</code> if <code>x</code> is a valid differentiation variable
   */
  private static boolean isDerivativeVariable(IExpr x) {
    if (x.isVariable()) {
      return true;
    }
    return x.isAST() && !x.isList() && !x.isPlusTimesPower() && !isOptionLike(x);
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

      if (!(isDerivativeVariable(x) || x.isList())) {
        // `1` is not a valid variable.
        return Errors.printMessage(ast.topHead(), "ivar", F.list(x), engine);
      }

      if (fx.isAST() && fx.head().isAST(S.Inactive, 2) && fx.head().first() == S.Integrate
          && fx.argSize() >= 2 && isDerivativeVariable(x)) {
        return inactiveIntegrate((IAST) fx, x, engine);
      }

      if (fx.isList()) {
        IAST list = (IAST) fx;
        // thread over first list
        return list.mapThreadEvaled(engine, F.ListAlloc(list.size()), ast, 1);
      }

      if (x instanceof IArraySymbol) {
        // differentiating by a whole vector, matrix or array; an unsupported case has to stay
        // unevaluated rather than fall through to the scalar rules below
        return ArrayDerivative.arrayD(fx, (IArraySymbol) x, engine);
      }
      if (fx.isAST1() && fx.head() instanceof IArraySymbol && !fx.isFree(x, true)) {
        // a vector, matrix or array valued function like MatrixSymbol("a", {m,n})[x]; its
        // derivative is written with Derivative just as for a function symbol
        IExpr arg1 = fx.first();
        if (!SymbolicArrayUtil.isArrayValued(arg1)) {
          IAST derivative = Derivative.createDerivative(1, fx.head(), arg1);
          return x.equals(arg1) ? derivative : F.Times(F.D(arg1, x), derivative);
        }
      }
      if (fx.isAST() && ArrayDerivative.isArrayHead(fx.head()) && !fx.isFree(x, true)) {
        // an array valued function of a scalar; the product rule of a Dot has to keep the order of
        // its factors, so this must run before the generic Derivative chain rule
        IExpr arrayResult = ArrayDerivative.dArrayValuedInScalar((IAST) fx, x, engine);
        if (arrayResult.isPresent()) {
          return arrayResult;
        }
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
          int n = arg2.toMachineInt();
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
              return DLeibnitzRule.nThDerivative(timesAST, x, n, (int) numberOfTerms, engine);
            }
            if (n >= 2 && fx.isAST1() && fx.head().isSymbol() && !fx.first().isFree(x, true)) {
              IExpr head = fx.head();
              if (head == S.Boole) {
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
              if (filter[0].argSize() > 0) {
                return F.Times(filter[0], F.D(filter[1].oneIdentity1(), xList));
              }
            }
            if (fx.isPower() && fx.base().isE() && fx.exponent().equals(x)) {
              // D(E^x, x) -> E^x
              return F.Power(S.E, x);
            }
            if (arg2.isSymbol()) {
              // D(Sin(x)*Cos(x), {x, n}) -> 2^(-1+n)*Sin(2*x+1/2*n*Pi)
              IExpr closedForm = DSymbolicOrder.nThDerivative(fx, x, arg2, engine);
              if (closedForm.isPresent()) {
                return closedForm;
              }
            }
            if (fx.isTimes()) {
              return F.NIL;
            }
            if (arg2.isSymbol() && fx.isAST() && isSymbolicOrderHead(fx.head())) {
              // D(f(a, x, b), {x, n}) -> Derivative(0, n, 0)[f][a, x, b]; like in Mathematica a
              // compound order like n-1 stays unevaluated
              return createDerivativeN(fx.head(), (IAST) fx, x, arg2);
            }
            return F.NIL;
          }
          if (!isDerivativeVariable(x)) {
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

      if (!isDerivativeVariable(x)) {
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
      return dPiecewise(dim, (IAST) functionOfX, x, ast, engine);
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
            // the product rule builds n products of n factors, it never expands them
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
            if (function.argSize() >= 2) {
              IExpr integrateResult = integrate(function, x, engine);
              if (integrateResult.isPresent()) {
                return integrateResult;
              }
              return F.NIL;
            }
            break;

          case ID.Sum:
            if (function.argSize() >= 2) {
              return sum(function, x, engine);
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
                IExpr rootSumResult = engine.evaluate(F.RootSum(f, newForm));
                return engine.addEvaluatedTraceStep(ast, rootSumResult, "RootSumRule");
              }
            }
            break;
        }
      }

      if (function.isAST1() && isFunctionHead(function.head(), x)) {
        return chainRuleArg1(function, x, engine);
      }
      // the chain rule for f(a1, a2, ...) and Derivative(n1, n2, ...)[f][a1, a2, ...]
      return getDerivativeArgN(x, function, function.head(), engine);
    }
    return F.NIL;
  }

  /**
   * Differentiate the inert <code>Inactive(Integrate)(f, ...)</code> by the same rules as the active
   * integral, keeping every integral which is left inert:
   * <code>D(Inactive(Integrate)(f(t), {t, 1, y(x)}), x) == f(y(x))*y'(x)</code> and
   * <code>D(Inactive(Integrate)(f(t, x), {t, a, b}), x) ==
   * Inactive(Integrate)(D(f(t, x), x), {t, a, b})</code>, as in Mathematica. Without this the
   * general rule for a compound head took the iterator list for an argument.
   *
   * @return the derivative, or the unevaluated <code>D</code> ({@link F#NIL}) for an iterator the
   *         rule does not cover
   */
  private static IExpr inactiveIntegrate(final IAST inactive, final IExpr x, EvalEngine engine) {
    if (inactive.isFree(x, true)) {
      return F.C0;
    }
    IExpr active = integrate(inactive.setAtCopy(0, S.Integrate), x, engine);
    if (active.isNIL()) {
      return F.NIL;
    }
    // the integrals the rule builds are active ones; they must not be evaluated
    final IExpr inactiveHead = inactive.head();
    if (active.isAST(S.Integrate)) {
      return ((IAST) active).setAtCopy(0, inactiveHead);
    }
    if (active.isPlus()) {
      return ((IAST) active).map(term -> term.isAST(S.Integrate)
          ? ((IAST) term).setAtCopy(0, inactiveHead)
          : F.NIL);
    }
    return active;
  }

  /**
   * Differentiate <code>Integrate(f, {t, a, b})</code> or <code>Integrate(f, t)</code> with respect
   * to <code>x</code> by the Leibniz integral rule:
   *
   * <pre>
   * D(Integrate(f, {t, a, b}), x) = f(t = b)*D(b, x) - f(t = a)*D(a, x) + Integrate(D(f, x), {t, a, b})
   * </pre>
   *
   * <p>
   * <code>Integrate</code> holds its arguments, so the derivative of the integrand is evaluated
   * before it is put back under the integral sign.
   *
   * @param integrate an <code>Integrate(...)</code> expression which isn't free of <code>x</code>
   * @param x the differentiation variable
   * @param engine the evaluation engine
   * @return {@link F#NIL} for an unsupported iterator
   */
  private static IExpr integrate(final IAST integrate, final IExpr x, EvalEngine engine) {
    final IExpr f = integrate.arg1();
    if (integrate.argSize() > 2) {
      // several iterators: only differentiate under the integral sign
      for (int i = 2; i < integrate.size(); i++) {
        if (!integrate.get(i).isFree(x, true)) {
          return F.NIL;
        }
      }
      return integrate.setAtCopy(1, engine.evaluate(F.D(f, x)));
    }
    final IExpr iterator = integrate.arg2();
    if (iterator.isVariable()) {
      if (iterator.equals(x)) {
        // D(Integrate(f, x), x) -> f
        return f;
      }
      return F.Integrate(engine.evaluate(F.D(f, x)), iterator);
    }
    if (!iterator.isList3() || !iterator.first().isVariable()) {
      return F.NIL;
    }
    final IAST list = (IAST) iterator;
    final IExpr t = list.arg1();
    final IExpr lower = list.arg2();
    final IExpr upper = list.arg3();
    if (t.equals(x)) {
      // the integration variable is bound, the value doesn't depend on it
      return lower.isFree(x, true) && upper.isFree(x, true) ? F.C0 : F.NIL;
    }
    if (!lower.isFree(t, true) || !upper.isFree(t, true)) {
      return F.NIL;
    }
    IASTAppendable plus = F.PlusAlloc(3);
    if (!upper.isFree(x, true)) {
      plus.append(F.Times(F.subst(f, arg -> arg.equals(t) ? upper : F.NIL), F.D(upper, x)));
    }
    if (!lower.isFree(x, true)) {
      plus.append(
          F.Times(F.CN1, F.subst(f, arg -> arg.equals(t) ? lower : F.NIL), F.D(lower, x)));
    }
    if (!f.isFree(x, true)) {
      plus.append(F.Integrate(engine.evaluate(F.D(f, x)), list));
    }
    return plus.oneIdentity0();
  }

  /**
   * Differentiate <code>Sum(f, iterators...)</code> with respect to <code>x</code>, if all
   * iterators are free of <code>x</code>:
   *
   * <pre>
   * D(Sum(f, {k, a, b}), x) = Sum(D(f, x), {k, a, b})
   * </pre>
   *
   * @param sum a <code>Sum(...)</code> expression
   * @param x the differentiation variable
   * @param engine the evaluation engine
   * @return {@link F#NIL} if an iterator depends on <code>x</code>
   */
  private static IExpr sum(final IAST sum, final IExpr x, EvalEngine engine) {
    for (int i = 2; i < sum.size(); i++) {
      if (!sum.get(i).isFree(x, true)) {
        return F.NIL;
      }
    }
    // Sum holds its arguments, so the summand is evaluated here
    return sum.setAtCopy(1, engine.evaluate(F.D(sum.arg1(), x)));
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

  /**
   * Open the comparisons of a {@link S#Piecewise} condition which contain the differentiation
   * variable, i.e. rewrite <code>x&gt;=c</code> to <code>x&gt;c</code> and <code>x&lt;=c</code> to
   * <code>x&lt;c</code>.
   *
   * <p>
   * A piece is only differentiable in the interior of its condition - at the boundary the left and
   * the right derivative generally differ - so a closed range of the value becomes an open range of
   * the derivative. Comparisons which are free of the differentiation variable don't describe a
   * transition point and are left unchanged.
   *
   * @param condition the condition of a <code>Piecewise</code> piece
   * @param x the differentiation variable
   * @return the condition with the comparisons containing <code>x</code> opened
   */
  private static IExpr openCondition(IExpr condition, IExpr x) {
    if (condition.isFree(x, true)) {
      return condition;
    }
    if (condition.isAST(S.Equal)) {
      // a point of the differentiation variable has no interior
      return S.False;
    }
    if (condition.isAST(S.LessEqual)) {
      return ((IAST) condition).apply(S.Less);
    }
    if (condition.isAST(S.GreaterEqual)) {
      return ((IAST) condition).apply(S.Greater);
    }
    if (condition.isAST(S.Inequality)) {
      // Inequality(lhs, operator, expr, operator, rhs, ...) - the operators are at even indices
      IAST inequality = (IAST) condition;
      IASTMutable result = inequality.copy();
      for (int i = 2; i < inequality.size(); i += 2) {
        IExpr operator = inequality.get(i);
        if (operator == S.LessEqual) {
          result.set(i, S.Less);
        } else if (operator == S.GreaterEqual) {
          result.set(i, S.Greater);
        }
      }
      return result;
    }
    if (condition.isAnd() || condition.isOr()) {
      // Not() isn't mapped - opening the negated comparison would close the condition
      IAST logical = (IAST) condition;
      IASTMutable result = logical.copy();
      for (int i = 1; i < logical.size(); i++) {
        result.set(i, openCondition(logical.get(i), x));
      }
      return result;
    }
    return condition;
  }

  private static IExpr dPiecewise(int[] dim, final IAST piecewiseFunction, final IExpr x,
      final IAST ast, EvalEngine engine) {

    IAST list = (IAST) piecewiseFunction.arg1();
    if (list.size() > 1) {
      IASTAppendable pwResult = F.ListAlloc(list.size() + 1);
      IASTAppendable conditions = F.ast(S.Or, list.size());
      for (int i = 1; i < list.size(); i++) {
        IASTMutable piecewiseD = ast.copy();
        piecewiseD.set(1, list.get(i).first());
        pwResult.append(F.list(piecewiseD, openCondition(list.get(i).second(), x)));
        conditions.append(list.get(i).second());
      }
      // The default value holds where no condition is true. Where that region has an interior,
      // the derivative of the default value applies; the transition points which remain
      // uncovered get the default Indeterminate, because there the derivative doesn't exist.
      IExpr defaultValue = piecewiseFunction.argSize() > 1 ? piecewiseFunction.arg2() : F.C0;
      IExpr complement = engine.evaluate(F.Not(conditions));
      if (!complement.isFalse() && complement.isFree(S.Not, true)) {
        IExpr openedComplement = engine.evaluate(openCondition(complement, x));
        if (!openedComplement.isFalse()) {
          IASTMutable defaultD = ast.copy();
          defaultD.set(1, defaultValue);
          pwResult.append(F.list(defaultD, openedComplement));
        }
      }
      return F.Piecewise(pwResult, S.Indeterminate);
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
      if (node.isAST(S.D) && node.size() > 2 && !node.isFree(x -> backward.containsKey(x), true)) {
        // an unevaluated D(...) expression which still contains a dummy function
        IASTAppendable dResult = F.ast(S.D, node.size() + 1);
        boolean hasOption = false;
        for (int i = 1; i < node.size(); i++) {
          IExpr arg = node.get(i);
          if (i > 1 && arg.isRuleAST() && arg.first() == S.NonConstants) {
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
