package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.visit.AbstractVisitorBoolean;

/**
 *
 *
 * <pre>
 * Eliminate(list - of - equations, list - of - variables)
 * </pre>
 *
 * <blockquote>
 *
 * <p>attempts to eliminate the variables from the <code>list-of-variables</code> in the <code>
 * list-of-equations</code>.
 *
 * </blockquote>
 *
 * <p>See:
 *
 * <ul>
 *   <li><a href=
 *       "http://en.wikipedia.org/wiki/System_of_linear_equations#Elimination_of_variables">Wikipedia
 *       - System of linear equations - Elimination of variables</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt;&gt; Eliminate({x==2+y, y==z}, y)
 * x==2+z
 * </pre>
 */
public class Eliminate extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  private static class VariableCounterVisitor extends AbstractVisitorBoolean
      implements Comparable<VariableCounterVisitor> {

    /** Count the number of nodes in <code>fExpr</code>, which equals <code>fVariable</code>. */
    int fVariableCounter;

    /** Count the total number of nodes in <code>fExpr</code>.. */
    int fNodeCounter;

    /**
     * The maximum number of recursion levels for visiting nodes, which equals <code>fVariable
     * </code>.
     */
    int fMaxVariableDepth;

    /** Holds the current recursion level for visiting nodes. */
    int fCurrentDepth;

    final IExpr fVariable;
    final IAST fExpr;

    public VariableCounterVisitor(final IAST expr, final IExpr variable) {
      super();
      fVariable = variable;
      fExpr = expr;
      fVariableCounter = 0;
      fNodeCounter = 0;
      fMaxVariableDepth = 0;
      fCurrentDepth = 0;
    }

    @Override
    public int compareTo(VariableCounterVisitor other) {
      if (fVariableCounter < other.fVariableCounter) {
        return -1;
      }
      if (fVariableCounter > other.fVariableCounter) {
        return 1;
      }
      if (fMaxVariableDepth < other.fMaxVariableDepth) {
        return -1;
      }
      if (fMaxVariableDepth > other.fMaxVariableDepth) {
        return 1;
      }
      if (fNodeCounter < other.fNodeCounter) {
        return -1;
      }
      if (fNodeCounter > other.fNodeCounter) {
        return 1;
      }
      return 0;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      VariableCounterVisitor other = (VariableCounterVisitor) obj;
      if (fCurrentDepth != other.fCurrentDepth) return false;
      if (fExpr == null) {
        if (other.fExpr != null) return false;
      } else if (!fExpr.equals(other.fExpr)) return false;
      if (fMaxVariableDepth != other.fMaxVariableDepth) return false;
      if (fNodeCounter != other.fNodeCounter) return false;
      if (fVariable == null) {
        if (other.fVariable != null) return false;
      } else if (!fVariable.equals(other.fVariable)) return false;
      if (fVariableCounter != other.fVariableCounter) return false;
      return true;
    }

    public IAST getExpr() {
      return fExpr;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + fCurrentDepth;
      result = prime * result + ((fExpr == null) ? 0 : fExpr.hashCode());
      result = prime * result + fMaxVariableDepth;
      result = prime * result + fNodeCounter;
      result = prime * result + ((fVariable == null) ? 0 : fVariable.hashCode());
      result = prime * result + fVariableCounter;
      return result;
    }

    @Override
    public boolean visit(IAST ast) {
      fNodeCounter++;
      if (ast.equals(fVariable)) {
        fVariableCounter++;
        if (fMaxVariableDepth < fCurrentDepth) {
          fMaxVariableDepth = fCurrentDepth;
        }
        return true;
      }
      try {
        fCurrentDepth++;
        ast.forEach(x -> x.accept(this));
      } finally {
        fCurrentDepth--;
      }

      return false;
    }

    @Override
    public boolean visit(ISymbol symbol) {
      fNodeCounter++;
      if (symbol.equals(fVariable)) {
        fVariableCounter++;
        if (fMaxVariableDepth < fCurrentDepth) {
          fMaxVariableDepth = fCurrentDepth;
        }
        return true;
      }
      return false;
    }

    @Override
    public boolean visit(IInteger element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(IFraction element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(IComplex element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(INum element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(IComplexNum element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(IPattern element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(IPatternSequence element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(IStringX element) {
      fNodeCounter++;
      return false;
    }
  }

  /**
   * Check if the argument at the given position is an equation (i.e. Equal[a,b]) or a list of
   * equations and return a list of expressions, which should be equal to <code>0</code>.
   *
   * @param ast
   * @param position
   * @return <code>F.NIL</code> if one of the elements is not a well-formed equation.
   */
  private static IAST checkEquations(final IAST ast, int position, EvalEngine engine) {
    IExpr arg = ast.get(position);
    if (arg.isList()) {
      IAST list = (IAST) arg;
      IASTAppendable equalList = F.ListAlloc(list.size());
      for (int i = 1; i < list.size(); i++) {
        if (list.get(i).isEqual()) {
          IAST equalAST = (IAST) list.get(i);
          // equalList.add(F.Equal(F.evalExpandAll(eq.arg1()),
          // F.evalExpandAll(eq.arg2())));
          equalList.append(BooleanFunctions.equals(equalAST));
        } else {
          // `1` is not a well-formed equation.
          return IOFunctions.printMessage(ast.topHead(), "eqf", F.List(list.get(i)), engine);
        }
      }
      return equalList;
    }
    if (arg.isEqual()) {
      IAST equalAST = (IAST) arg;
      return F.List(
          F.Equal(
              F.evalExpandAll(equalAST.arg1(), engine), F.evalExpandAll(equalAST.arg2(), engine)));
      // return equalList;
    }
    // `1` is not a well-formed equation.
    return IOFunctions.printMessage(ast.topHead(), "eqf", F.List(arg), engine);
  }

  /**
   * Analyze an <code>Equal()</code> expression.
   *
   * @param equalAST an <code>Equal()</code> expression.
   * @param variable the variable which should be eliminated.
   * @return <code>F.NIL</code> if we can't find an equation for the given <code>variable</code>.
   */
  private static IExpr eliminateAnalyze(IAST equalAST, IExpr variable, EvalEngine engine) {
    if (equalAST.isEqual()) {
      IExpr arg1 = equalAST.arg1();
      IExpr arg2 = equalAST.arg2();
      Predicate<IExpr> predicate = Predicates.in(variable);
      boolean boolArg1 = arg1.isFree(predicate, true);
      boolean boolArg2 = arg2.isFree(predicate, true);
      IExpr result = F.NIL;
      if (!boolArg1 && boolArg2) {
        result = extractVariableRecursive(arg1, arg2, predicate, variable, engine);
      } else if (boolArg1 && !boolArg2) {
        result = extractVariableRecursive(arg2, arg1, predicate, variable, engine);
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Extract the variable from the given <code>expr</code> assuming <code>expr == 0</code>.
   *
   * @param expr an expression.
   * @param variable the variable which should be eliminated.
   * @return <code>F.NIL</code> if we can't find an equation for the given <code>variable</code>.
   */
  public static IExpr extractVariable(IExpr expr, IExpr variable, EvalEngine engine) {
    Predicate<IExpr> predicate = Predicates.in(variable);
    IExpr result = F.NIL;
    if (!expr.isFree(predicate, true)) {
      result = extractVariableRecursive(expr, F.C0, predicate, variable, engine);
    }
    return result;
  }

  /**
   * Extract a value for the given <code>variabe</code>.
   *
   * @param exprWithVariable expression which contains the given <code>variabe</code>.
   * @param exprWithoutVariable expression which doesn't contain the given <code>variabe</code>.
   * @param x the variable which should be eliminated.
   * @return <code>F.NIL</code> if we can't find an equation for the given variable <code>x</code>.
   */
  private static IExpr extractVariableRecursive(
      IExpr exprWithVariable,
      IExpr exprWithoutVariable,
      Predicate<IExpr> predicate,
      IExpr x,
      EvalEngine engine) {
    if (exprWithVariable.equals(x)) {
      return exprWithoutVariable;
    }
    if (exprWithVariable.isAST()) {
      IAST ast = (IAST) exprWithVariable;
      if (ast.isAST1()) {
        IASTAppendable inverseFunction = InverseFunction.getUnaryInverseFunction(ast, true);
        if (inverseFunction.isPresent()) {
          if (exprWithVariable.isAbs()) {
            if (exprWithoutVariable.isNonNegativeResult()) {
              // example: Abs(x-1) == 1
              inverseFunction.append(exprWithoutVariable);
              return extractVariableRecursive(ast.arg1(), inverseFunction, predicate, x, engine);
            }
            return S.True;
          } else {
            // example: Sin(f(x)) == y -> f(x) == ArcSin(y)
            inverseFunction.append(exprWithoutVariable);
            return extractVariableRecursive(ast.arg1(), inverseFunction, predicate, x, engine);
          }
        }
      } else {
        int size = ast.size();
        if (ast.isPlus()) {
          // a + b + c....
          IASTAppendable rest = F.PlusAlloc(size);
          IASTAppendable plusClone = ast.copyAppendable();
          int j = 1;
          for (int i = 1; i < size; i++) {
            if (ast.get(i).isFree(predicate, true)) {
              j++;
            } else {
              rest.append(ast.get(i));
              plusClone.remove(j);
            }
          }
          if (plusClone.isAST0()) {
            // no change for given expression
            if (ast.size() == 3) {
              IExpr temp = matchSpecialExpressions(ast, exprWithoutVariable, x);
              if (temp.isPresent()) {
                return temp;
              }
            }
            return F.NIL;
          }
          IExpr value = engine.evaluate(F.Subtract(exprWithoutVariable, plusClone));
          return extractVariableRecursive(rest.oneIdentity0(), value, predicate, x, engine);
        } else if (ast.isTimes()) {
          // a * b * c....
          IASTAppendable rest = F.TimesAlloc(size);
          IASTAppendable timesClone = ast.copyAppendable();
          int j = 1;
          for (int i = 1; i < size; i++) {
            if (ast.get(i).isFree(predicate, true)) {
              j++;
            } else {
              rest.append(ast.get(i));
              timesClone.remove(j);
            }
          }
          if (timesClone.isAST0()) {
            IExpr[] numerDenom = Algebra.getNumeratorDenominator(ast, EvalEngine.get());
            if (!numerDenom[1].isOne()) {
              IExpr[] numerLinear = numerDenom[0].linear(x);
              if (numerLinear != null) {
                IExpr[] denomLinear = numerDenom[1].linear(x);
                if (denomLinear != null) {
                  IExpr temp =
                      EvalEngine.get()
                          .evaluate(
                              numerLinear[1].subtract(denomLinear[1].times(exprWithoutVariable)));
                  if (!temp.isZero()) {
                    return numerLinear[0]
                        .negate()
                        .plus(denomLinear[0].times(exprWithoutVariable))
                        .times(temp.power(-1L));
                  }
                }
              }
            }
            // no change for given expression
            return F.NIL;
          }
          IExpr value = F.Divide(exprWithoutVariable, timesClone);
          return extractVariableRecursive(rest.oneIdentity1(), value, predicate, x, engine);
        } else if (ast.isPower()) {
          IExpr base = ast.base();
          IExpr exponent = ast.exponent();
          if (exponent.isFree(predicate, true)) {
            // f(x) ^ a
            IExpr value = F.Power(exprWithoutVariable, F.Divide(F.C1, exponent));
            return extractVariableRecursive(base, value, predicate, x, engine);
          } else if (base.isFree(predicate, true)) {
            if (base.isE()) {
              // E ^ f(x)
              IExpr c1 = F.C(1);
              final IExpr exprwovar = exprWithoutVariable;
              IExpr temp =
                  // [$ ConditionalExpression(2*I*Pi*c1 + Log(exprwovar), Element(c1, Integers)) $]
                  F.ConditionalExpression(
                      F.Plus(F.Times(F.C2, F.CI, S.Pi, c1), F.Log(exprwovar)),
                      F.Element(c1, S.Integers)); // $$;
              return extractVariableRecursive(exponent, temp, predicate, x, engine);
            }
            // a ^ f(x)
            IExpr value = F.Divide(F.Log(exprWithoutVariable), F.Log(base));
            return extractVariableRecursive(exponent, value, predicate, x, engine);
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Match <code>a_.*variable^n_+b_.*variable^m_</code> to <code>
   * E^(((-I)*Pi + Log(a) - Log(b))/(m - n)) /; FreeQ(a,x)&&FreeQ(b,x)&&FreeQ(n,x)&&FreeQ(m,x)
   * </code>
   *
   * @param ast
   * @param x
   * @return
   */
  private static IExpr matchSpecialExpressions(IAST ast, IExpr exprWithoutVariable, IExpr x) {
    if (exprWithoutVariable.isZero()) {
      final Matcher matcher = initMatcher(x);
      return matcher.replaceAll(ast);
    }
    return F.NIL;
  }

  private static Matcher initMatcher(IExpr x) {
    final Matcher matcher = new Matcher();
    // match a_.*variable^n_.+b_.*variable^m_ to E^(((-I)*Pi + Log(a) - Log(b))/(m - n))
    matcher.caseOf(
        F.Plus(
            F.Times(F.b_DEFAULT, F.Power(x, F.m_)),
            F.Times(F.a_DEFAULT, F.Power(x, F.n_DEFAULT))), //
        F.Condition(
            F.Exp(
                F.Times(
                    F.Power(F.Plus(S.m, F.Negate(S.n)), -1),
                    F.Plus(F.Times(F.CNI, S.Pi), F.Log(S.a), F.Negate(F.Log(S.b))))),
            F.And(F.FreeQ(S.a, x), F.FreeQ(S.b, x), F.FreeQ(S.n, x), F.FreeQ(S.m, x))));
    return matcher;
  }

  public Eliminate() {}

  /**
   * Analyze the <code>Equal()</code> terms, if we find an expression which equals the given <code>
   * variabe</code>
   *
   * @param analyzerList the list of <code>Equal()</code> terms with statistics of it's equations.
   * @param variable the variable which should be eliminated.
   * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>
   *     variable</code> or the eliminated list of equations in index <code>[0]</code> and the last
   *     rule which is used for variable elimination in index <code>[1]</code>.
   */
  private static IAST[] eliminateOneVariable(
      ArrayList<VariableCounterVisitor> analyzerList, IExpr variable, EvalEngine engine) {
    IASTAppendable eliminatedResultEquations = F.ListAlloc(analyzerList.size());
    for (int i = 0; i < analyzerList.size(); i++) {
      IExpr variableExpr = eliminateAnalyze(analyzerList.get(i).getExpr(), variable, engine);
      if (variableExpr.isPresent()) {
        variableExpr = engine.evalQuiet(variableExpr);
        IExpr expr;
        IAST rule = F.Rule(variable, variableExpr);
        analyzerList.remove(i);
        for (int j = 0; j < analyzerList.size(); j++) {
          expr = analyzerList.get(j).getExpr();
          IExpr temp = expr.replaceAll(rule);
          if (temp.isPresent()) {
            temp = F.expandAll(temp, true, true);
            eliminatedResultEquations.append(temp);
          } else {
            eliminatedResultEquations.append(expr);
          }
        }
        IAST[] result = new IAST[2];
        result[0] = eliminatedResultEquations;
        result[1] = rule;
        return result;
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    try {
      IAST termsEqualZeroList = checkEquations(ast, 1, engine);
      if (!termsEqualZeroList.isPresent()) {
        return F.NIL;
      }
      IAST vars = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
      if (!vars.isPresent()) {
        return F.NIL;
      }

      IAST result = termsEqualZeroList;
      IAST[] temp;
      // IAST equalAST;
      ISymbol variable;
      // VariableCounterVisitor exprAnalyzer;
      for (int i = 1; i < vars.size(); i++) {
        variable = (ISymbol) vars.get(i);

        temp = eliminateOneVariable(result, variable, engine);
        if (temp != null) {
          result = temp[0];
        } else {
          return resultAsAndEquations(result);
        }
      }
      return resultAsAndEquations(result);
    } catch (Exception ex) {
      LOGGER.error("QuantityParser.of() failed", ex);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }

  private static IExpr resultAsAndEquations(IAST result) {
    if (result.isList()) {
      if (result.equals(F.CEmptyList)) {
        return S.True;
      }
      return result.apply(S.And);
    }
    return result;
  }

  /**
   * @param result
   * @param variable
   * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>
   *     variable</code> or the eliminated list of equations in index <code>[0]</code> and the last
   *     rule which is used for variable elimination in index <code>[1]</code>.
   */
  public static IAST[] eliminateOneVariable(IAST result, IExpr variable, EvalEngine engine) {
    IAST equalAST;
    VariableCounterVisitor exprAnalyzer;
    ArrayList<VariableCounterVisitor> analyzerList = new ArrayList<VariableCounterVisitor>();
    for (int j = 1; j < result.size(); j++) {
      equalAST = result.getAST(j);
      exprAnalyzer = new VariableCounterVisitor(equalAST, variable);
      equalAST.accept(exprAnalyzer);
      analyzerList.add(exprAnalyzer);
    }
    Collections.sort(analyzerList);

    return eliminateOneVariable(analyzerList, variable, engine);
  }

  /**
   * @param result
   * @param slot
   * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>
   *     variable</code> or the eliminated list of equations in index <code>[0]</code> and the last
   *     rule which is used for variable elimination in index <code>[1]</code>.
   */
  public static IAST[] eliminateSlot(IAST result, IExpr slot, EvalEngine engine) {

    VariableCounterVisitor exprAnalyzer;
    ArrayList<VariableCounterVisitor> analyzerList = new ArrayList<VariableCounterVisitor>();
    IAST equalAST = result;
    exprAnalyzer = new VariableCounterVisitor(equalAST, slot);
    equalAST.accept(exprAnalyzer);
    analyzerList.add(exprAnalyzer);
    Collections.sort(analyzerList);

    return eliminateOneVariable(analyzerList, slot, engine);
  }
}
