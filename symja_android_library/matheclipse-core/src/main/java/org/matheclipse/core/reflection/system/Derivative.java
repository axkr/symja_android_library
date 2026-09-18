package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
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
 * 6*#1&amp;
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
 * 12*#1^2&amp;
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
    if (isDiracDeltaDerivativeAwayFromZero(ast)) {
      // like DiracDelta(x) itself, every derivative of it vanishes where x is a nonzero real
      return F.C0;
    }
    IAST[] derivativeAST = ast.isDerivative();
    if (derivativeAST == null) {
      return F.NIL;
    }
    IAST derivativeHead = derivativeAST[0];
    IAST functions = derivativeAST[1];
    boolean isZero = derivativeHead.forAll(n -> n.isZero());
    if (isZero && derivativeAST[2] == null) {
      // Derivative(0, 0, ...)[f] -> f
      return functions.size() > 1 ? functions.arg1() : F.NIL;
    }
    if (functions.size() != 2) {
      return F.NIL;
    }
    if (functions.arg1().isNumber()) {
      return F.Function(F.C0);
    }
    for (int i = 1; i < derivativeHead.size(); i++) {
      if (isInvalidDerivativeOrder(derivativeHead.get(i))) {
        // like in Mathematica an explicit negative or non-integer order stays unevaluated
        return F.NIL;
      }
    }
    return evaluateDIfPossible(derivativeHead, functions, derivativeAST[2], engine);
  }

  /**
   * Whether <code>ast</code> is <code>Derivative(n1, n2, ...)[DiracDelta][x1, x2, ...]</code> with
   * non-negative integer orders and one of the <code>xi</code> a nonzero real: the residual of a
   * step-forced equation, differentiated twice, holds <code>DiracDelta'(7/10 - Pi)</code>.
   */
  private static boolean isDiracDeltaDerivativeAwayFromZero(IAST ast) {
    IExpr head = ast.head();
    if (ast.argSize() < 1 || !head.isAST1() || head.first() != S.DiracDelta
        || !head.head().isAST(S.Derivative) || ((IAST) head.head()).argSize() != ast.argSize()) {
      return false;
    }
    IAST orders = (IAST) head.head();
    for (int i = 1; i <= orders.argSize(); i++) {
      if (!orders.get(i).isInteger() || orders.get(i).isNegative()) {
        return false;
      }
    }
    return ast.exists(x -> x.isNonZeroRealResult());
  }

  /**
   * Test if <code>n</code> cannot be used as the order of a derivative.
   *
   * <p>
   * Valid orders are non-negative integers and symbolic expressions which don't evaluate to an
   * explicit number (for example <code>n</code>, <code>-1+n</code> or <code>2*n</code>). This is
   * the same test which {@link D} uses for the <code>{variable, n}</code> specifier.
   *
   * @param n the order of the derivative
   * @return <code>true</code> if <code>n</code> is an explicit negative or non-integer number
   */
  private static boolean isInvalidDerivativeOrder(IExpr n) {
    return n.isNegativeResult() || (!n.isInteger() && n.isNumericFunction());
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
          int n = head.first().toMachineInt();
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
            temp = multinomial(head);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
        int[] orders = headDerivative.head().toIntVector();
        if (orders != null) {
          temp = pureMultipleOrderDerivative(builtin, engine, orders);
          if (temp.isPresent()) {
            return temp;
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

  /**
   * Generates a symbolic expression for the partial derivative of a given multi-argument function.
   *
   * @param function the built-in symbol of the Symja function
   * @param orders an array of integers representing the derivative orders for each argument.
   * @return the symbolic expression of the derivative as an {@link IAST} object.
   */
  public static IAST pureMultipleOrderDerivative(IBuiltInSymbol function, EvalEngine engine,
      int... orders) {
    if (isEndlessRecursion(orders)) {
      return F.NIL;
    }

    int numberOfSlots = orders.length;
    List<IAST> slots = new ArrayList<>(numberOfSlots);
    IASTAppendable functionExpr = F.ast(function, numberOfSlots);
    for (int i = 0; i < numberOfSlots; i++) {
      IAST slot = F.Slot(i + 1);
      slots.add(slot);
      functionExpr.append(slot);
    }

    // Dynamically create the base symbolic expression from the function name.
    IExpr exprToDifferentiate = functionExpr;
    for (int i = 0; i < numberOfSlots; i++) {
      IAST currentVar = slots.get(i);
      int order = orders[i];
      for (int j = 0; j < order; j++) {
        IExpr temp = engine.evaluateNIL(F.D(exprToDifferentiate, currentVar));
        if (temp.isPresent() && temp.topHead() != S.Derivative) {
          exprToDifferentiate = temp;
        } else {
          return F.NIL;
        }
      }
    }

    return F.Function(exprToDifferentiate);

  }

  /**
   * Check for the case <code>orders.length == 0</code> or the case
   * <code>Derivative(1,0,0,...,0)[f]</code> which could lead to endless recursion if <code>f</code>
   * is a built-in numeric function symbol
   * 
   * @param orders
   * @return
   */
  private static boolean isEndlessRecursion(int... orders) {
    if (orders.length == 0) {
      return true;
    }
    int countOne = 0;
    int countUnequalZero = 0;
    for (int i = 0; i < orders.length; i++) {
      if (orders[i] < 0) {
        return false;
      }
      if (orders[i] == 1) {
        countOne++;
      } else if (orders[i] != 0) {
        countUnequalZero++;
      }
    }
    return countUnequalZero == 0 && countOne == 1;
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

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.NHOLDALL);
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
