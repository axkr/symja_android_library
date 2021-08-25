package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.AbstractAssumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class AssumptionFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Arrays.setEvaluator(new Arrays());
      S.Assuming.setEvaluator(new Assuming());
      S.Element.setEvaluator(new Element());
      S.NotElement.setEvaluator(new NotElement());
      S.Refine.setEvaluator(new Refine());
    }
  }

  private static final class Arrays extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      if (ast.size() == 2 && ast.arg1().isAST()) {
        return F.Arrays((IAST) ast.arg1());
      }
      if (ast.size() == 3 && ast.arg1().isAST() && ast.arg2().isSymbol()) {
        return F.Arrays((IAST) ast.arg1(), (ISymbol) ast.arg2());
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDALL);
    }
  }

  private static final class Assuming extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      IExpr oldValue = S.$Assumptions.assignedValue();
      IExpr value = S.True;
      if (oldValue == null) {
        if (ast.arg1().isList()) {
          value = (IAST) ast.arg1();
        } else {
          value = F.List(ast.arg1());
        }
      } else {
        value = oldValue;
        if (value.isList()) {
          value = ((IAST) value).appendClone(ast.arg1());
        } else {
          value = F.ListAlloc(value, ast.arg1());
        }
      }

      try {
        S.$Assumptions.assignValue(value);
        IExpr temp = engine.evaluate(ast.arg2());
        return temp;
      } finally {
        S.$Assumptions.assignValue(oldValue);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDREST);
    }
  }

  /**
   *
   *
   * <pre>
   * Element(symbol, dom)
   * </pre>
   *
   * <blockquote>
   *
   * <p>assume (or test) that the <code>symbol</code> is in the domain <code>dom</code>.
   *
   * </blockquote>
   *
   * <p>See:
   *
   * <ul>
   *   <li><a href="https://en.wikipedia.org/wiki/Domain_of_a_function">Wikipedia - Domain of a
   *       function</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Refine(Sin(k*Pi), Element(k, Integers))
   * 0
   * </pre>
   */
  private static class Element extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg2 = engine.evaluate(ast.arg2());

      if (arg2.isSymbol()) {
        final ISymbol domain = (ISymbol) arg2;
        final IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isAST()) {
          IAST arg1AST = (IAST) arg1;
          if (arg1.isList() || arg1.isAST(S.Alternatives)) {
            if (arg1AST.size() == 1) {
              return S.True;
            }
            if (arg1AST.size() == 2) {
              return F.Element(arg1AST.first(), domain);
            }
            IASTAppendable result = F.ast(arg1.head(), arg1AST.size(), false);
            boolean evaled = false;
            for (int i = 1; i < arg1AST.size(); i++) {
              final IExpr arg = arg1AST.get(i);
              IExpr assumeDomain = assumeDomain(arg, domain);
              if (assumeDomain.isFalse()) {
                evaled = true;
                return S.False;
              }
              if (assumeDomain.isTrue()) {
                evaled = true;
                continue;
              }
              result.append(arg);
            }
            return evaled ? F.Element(result, domain) : F.NIL;
          }
        }
        return assumeDomain(arg1, domain);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    /**
     * Return S.True or S.False if expr is assumed to be in the <code>domain</code> or not to be in
     * the <code>domain</code>.
     *
     * @param arg1
     * @param domain
     * @return S.True or S.False if expr is assumed to be in the <code>domain</code> or not to be in
     *     the <code>domain</code>. In all other cases return <code>F.NIL</code>.
     */
    private IExpr assumeDomain(final IExpr arg1, final ISymbol domain) {
      if (domain.isBuiltInSymbol()) {
        ISymbol truthValue;
        final int symbolID = ((IBuiltInSymbol) domain).ordinal();
        switch (symbolID) {
          case ID.Algebraics:
            truthValue = AbstractAssumptions.assumeAlgebraic(arg1);
            return (truthValue != null) ? truthValue : F.NIL;
          case ID.Arrays:
            truthValue = AbstractAssumptions.assumeArray(arg1);
            return (truthValue != null) ? truthValue : F.NIL;
          case ID.Booleans:
            truthValue = AbstractAssumptions.assumeBoolean(arg1);
            return (truthValue != null) ? truthValue : F.NIL;
          case ID.Complexes:
            truthValue = AbstractAssumptions.assumeComplex(arg1);
            return (truthValue != null) ? truthValue : F.NIL;
          case ID.Integers:
            truthValue = AbstractAssumptions.assumeInteger(arg1);
            return (truthValue != null) ? truthValue : F.NIL;
          case ID.Primes:
            return AbstractAssumptions.assumePrime(arg1);
          case ID.Rationals:
            truthValue = AbstractAssumptions.assumeRational(arg1);
            return (truthValue != null) ? truthValue : F.NIL;
          case ID.Reals:
            truthValue = AbstractAssumptions.assumeReal(arg1);
            return (truthValue != null) ? truthValue : F.NIL;
          default:
            break;
        }
      }
      return F.NIL;
    }
  }

  private static class NotElement extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg2 = engine.evaluate(ast.arg2());
      if (arg2.isSymbol()) {
        final IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isAST(S.Alternatives)) {
          IAST alternatives = (IAST) arg1;
          IASTAppendable andList = F.And();
          alternatives.forEach(x -> andList.append(F.Not(F.Element(x, arg2))));
          return andList;
        }
        return F.Not(F.Element(arg1, arg2));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Refine(expression, assumptions)
   * </pre>
   *
   * <blockquote>
   *
   * <p>evaluate the <code>expression</code> for the given <code>assumptions</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Refine(Abs(n+Abs(m)), n&gt;=0)
   * Abs(m)+n
   *
   * &gt;&gt; Refine(-Infinity&lt;x, x&gt;0)
   * True
   *
   * &gt;&gt; Refine(Max(Infinity,x,y), x&gt;0)
   * Max(Infinity,y)
   *
   * &gt;&gt; Refine(Sin(k*Pi), Element(k, Integers))
   * 0
   *
   * &gt;&gt; Sin(k*Pi)
   * Sin(k*Pi)
   * </pre>
   */
  private static class Refine extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      OptionArgs options = null;
      IAssumptions assumptions = null;
      if (ast.size() > 2) {
        options = new OptionArgs(S.Refine, ast, 2, engine);
      }
      IExpr assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);
      if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
        assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
      }
      return refineAssumptions(ast.arg1(), assumptions, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
      setOptions(newSymbol, F.List(F.Rule(S.Assumptions, S.$Assumptions)));
    }
  }

  public static IExpr refineAssumptions(
      final IExpr expr, IAssumptions assumptions, EvalEngine engine) {
    if (assumptions != null) {
      IAssumptions oldAssumptions = engine.getAssumptions();
      try {
        engine.setAssumptions(assumptions);
        // System.out.println(expr.toString());
        return engine.evalWithoutNumericReset(expr);
      } finally {
        engine.setAssumptions(oldAssumptions);
      }
    }
    return engine.evalWithoutNumericReset(expr);
  }

  public static void initialize() {
    Initializer.init();
  }

  private AssumptionFunctions() {}
}
