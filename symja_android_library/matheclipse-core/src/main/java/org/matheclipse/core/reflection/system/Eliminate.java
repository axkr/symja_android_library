package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
import org.matheclipse.core.reflection.system.rulesets.EliminateRules;
import org.matheclipse.core.visit.AbstractVisitorBoolean;
import com.google.common.base.Suppliers;

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
public class Eliminate extends AbstractFunctionEvaluator implements EliminateRules {
  private static final Logger LOGGER = LogManager.getLogger();

  private static Supplier<Matcher> INVERSE_MATCHER;
  private static Supplier<Matcher> ZERO_MATCHER;

  private static Matcher inverseMatcher() {
    return INVERSE_MATCHER.get();
  }

  private static Matcher zeroMatcher() {
    return ZERO_MATCHER.get();
  }

  static class VariableCounterVisitor extends AbstractVisitorBoolean
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
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return <code>F.NIL</code> if we can't find an equation for the given <code>variable</code>.
   */
  private static IExpr eliminateAnalyze(
      IAST equalAST, IExpr variable, boolean multipleValues, EvalEngine engine) {
    if (equalAST.isEqual()) {
      IExpr arg1 = equalAST.arg1();
      IExpr arg2 = equalAST.arg2();
      Predicate<IExpr> predicate = Predicates.in(variable);
      boolean boolArg1 = arg1.isFree(predicate, true);
      boolean boolArg2 = arg2.isFree(predicate, true);
      IExpr result = F.NIL;
      if (!boolArg1 && boolArg2) {
        result = extractVariableRecursive(arg1, arg2, predicate, variable, multipleValues, engine);
      } else if (boolArg1 && !boolArg2) {
        result = extractVariableRecursive(arg2, arg1, predicate, variable, multipleValues, engine);
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
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return <code>F.NIL</code> if we can't find an equation for the given <code>variable</code>.
   */
  public static IExpr extractVariable(
      IExpr expr, IExpr variable, boolean multipleValues, EvalEngine engine) {
    Predicate<IExpr> predicate = Predicates.in(variable);
    IExpr result = F.NIL;
    if (!expr.isFree(predicate, true)) {
      result = extractVariableRecursive(expr, F.C0, predicate, variable, multipleValues, engine);
    }
    return result;
  }

  /**
   * Extract a value for the given <code>variabe</code>.
   *
   * @param exprWithVariable expression which contains the given <code>variabe</code>.
   * @param exprWithoutVariable expression which doesn't contain the given <code>variabe</code>.
   * @param x the variable which should be eliminated.
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return <code>F.NIL</code> if we can't find an equation for the given variable <code>x</code>.
   */
  private static IExpr extractVariableRecursive(
      IExpr exprWithVariable,
      IExpr exprWithoutVariable,
      Predicate<IExpr> predicate,
      IExpr x,
      boolean multipleValues,
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
              return extractVariableRecursive(
                  ast.arg1(), inverseFunction, predicate, x, multipleValues, engine);
            }
            return S.True;
          } else {
            // example: Sin(f(x)) == y -> f(x) == ArcSin(y)
            inverseFunction.append(exprWithoutVariable);
            return extractVariableRecursive(
                ast.arg1(), inverseFunction, predicate, x, multipleValues, engine);
          }
        }
      } else {
        int size = ast.size();
        if (size > 2) {
          if (exprWithoutVariable.isZero()) {
            IExpr result = zeroMatcher().apply(F.binaryAST2(elimzero, ast, x));
            if (result.isPresent()) {
              return resultWithIfunMessage(result, x, exprWithoutVariable, multipleValues, engine);
            }
          }
          IExpr result = inverseMatcher().apply(F.binaryAST2(eliminv, ast, x));
          if (result.isPresent()) {
            return resultWithIfunMessage(result, x, exprWithoutVariable, multipleValues, engine);
          }
        }

        if (ast.isPlus()) {
          if (exprWithoutVariable.isNumericFunction()
              && !exprWithoutVariable.isZero()
              && ast.isPolynomial(x)) {
            IExpr solve =
                S.Solve.ofNIL(engine, F.Equal(F.Subtract(ast, exprWithoutVariable), F.C0), x);
            if (solve.isList() && solve.size() > 1) {
              IExpr result = listOfRulesToValues(solve, x, multipleValues);
              if (result.isPresent()) {
                return result;
              }
            }
          }

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
            return F.NIL;
          }
          IExpr value = engine.evaluate(F.Subtract(exprWithoutVariable, plusClone));
          return extractVariableRecursive(
              rest.oneIdentity0(), value, predicate, x, multipleValues, engine);
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
          return extractVariableRecursive(
              rest.oneIdentity1(), value, predicate, x, multipleValues, engine);
        } else if (ast.isPower()) {
          IExpr base = ast.base();
          IExpr exponent = ast.exponent();
          if (exponent.isFree(predicate, true)) {
            // f(x) ^ a
            printIfunMessage(engine);
            IExpr value = engine.evaluate(F.Power(exprWithoutVariable, F.Divide(F.C1, exponent)));
            return extractVariableRecursive(base, value, predicate, x, multipleValues, engine);
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
              return extractVariableRecursive(exponent, temp, predicate, x, multipleValues, engine);
            }
            // a ^ f(x)
            IExpr value = F.Divide(F.Log(exprWithoutVariable), F.Log(base));
            return extractVariableRecursive(exponent, value, predicate, x, multipleValues, engine);
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * @param listOfRules
   * @param multipleValues if <code>false</code> return only the first found value in the list
   * @return
   */
  private static IExpr listOfRulesToValues(
      IExpr listOfRules, IExpr variable, boolean multipleValues) {
    if (multipleValues) {
      IASTAppendable solveValues = F.ListAlloc(listOfRules.size());
      ((IAST) listOfRules)
          .map(
              a -> {
                if (a.isList1() //
                    && a.first().isRuleAST()
                    && a.first().first().equals(variable)) {
                  solveValues.append(a.first().second());
                }

                return F.NIL;
              });
      if (solveValues.size() > 1) {
        return solveValues;
      }
    } else {
      if (listOfRules.first().isRuleAST() //
          && listOfRules.first().equals(variable)) {
        return listOfRules.first().second();
      }
    }
    return F.NIL;
  }

  /**
   * Print message "Inverse functions are being used. Values may be lost for multivalued inverses."
   * and return the {@code result} by substituting {@code subExpr} with {@code replacementExpr}.
   *
   * @param result
   * @param subExpr
   * @param replacementExpr
   * @param multipleValues if <code>true</code> multiple results are returned as lsit of values
   * @param engine
   * @return
   */
  private static IExpr resultWithIfunMessage(
      IExpr result,
      IExpr subExpr,
      IExpr replacementExpr,
      boolean multipleValues,
      EvalEngine engine) {
    printIfunMessage(engine);
    IExpr expr = F.subst(result, subExpr, replacementExpr);
    if (!multipleValues && expr.isList() && expr.size() > 1) {
      return expr.first();
    }
    return expr;
  }

  /**
   * Print message "Inverse functions are being used. Values may be lost for multivalued inverses."
   *
   * @param engine
   */
  private static void printIfunMessage(EvalEngine engine) {
    IOFunctions.printMessage(S.InverseFunction, "ifun", F.CEmptyList, engine);
  }

  public Eliminate() {}

  /**
   * Analyze the <code>Equal()</code> terms, if we find an expression which equals the given <code>
   * variabe</code>
   *
   * @param analyzerList the list of <code>Equal()</code> terms with statistics of it's equations.
   * @param variable the variable which should be eliminated.
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>
   *     variable</code> or the eliminated list of equations in index <code>[0]</code> and the last
   *     rule which is used for variable elimination in index <code>[1]</code>.
   */
  protected static IAST[] eliminateOneVariable(
      ArrayList<VariableCounterVisitor> analyzerList,
      IExpr variable,
      boolean multipleValues,
      EvalEngine engine) {
    IASTAppendable eliminatedResultEquations = F.ListAlloc(analyzerList.size());
    for (int i = 0; i < analyzerList.size(); i++) {
      IExpr variableValues =
          eliminateAnalyze(analyzerList.get(i).getExpr(), variable, multipleValues, engine);
      if (variableValues.isPresent()) {
        analyzerList.remove(i);
        IAST[] result = new IAST[2];
        if (variableValues.isList()) {
          IAST listOfRules =
              ((IAST) variableValues)
                  .map(
                      x -> {
                        return applyRuleToAnalyzer(
                            variable, x, eliminatedResultEquations, analyzerList, engine);
                      });
          result[0] = eliminatedResultEquations;
          result[1] = listOfRules;
        } else {
          IAST rule =
              applyRuleToAnalyzer(
                  variable, variableValues, eliminatedResultEquations, analyzerList, engine);

          result[0] = eliminatedResultEquations;
          result[1] = rule;
        }
        return result;
      }
    }
    return null;
  }

  private static IAST applyRuleToAnalyzer(
      IExpr variable,
      IExpr variableValue,
      IASTAppendable eliminatedResultEquations,
      ArrayList<VariableCounterVisitor> analyzerList,
      EvalEngine engine) {
    variableValue = engine.evalQuiet(variableValue);
    IExpr expr;
    IAST rule = F.Rule(variable, variableValue);
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
    return rule;
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

        temp = eliminateOneVariable(result, variable, false, engine);
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
   * @param ast
   * @param variable
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @param engine
   * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>
   *     variable</code> or the eliminated list of equations in index <code>[0]</code> and the last
   *     rule which is used for variable elimination in index <code>[1]</code>.
   */
  public static IAST[] eliminateOneVariable(
      IAST ast, IExpr variable, boolean multipleValues, EvalEngine engine) {
    IAST equalAST;
    VariableCounterVisitor exprAnalyzer;
    ArrayList<VariableCounterVisitor> analyzerList = new ArrayList<VariableCounterVisitor>();
    for (int j = 1; j < ast.size(); j++) {
      equalAST = ast.getAST(j);
      exprAnalyzer = new VariableCounterVisitor(equalAST, variable);
      equalAST.accept(exprAnalyzer);
      analyzerList.add(exprAnalyzer);
    }
    Collections.sort(analyzerList);

    return eliminateOneVariable(analyzerList, variable, multipleValues, engine);
  }

  public void setUp(final ISymbol newSymbol) {
    INVERSE_MATCHER = Suppliers.memoize(EliminateRules::init1);
    ZERO_MATCHER = Suppliers.memoize(EliminateRules::init2);
  }
}
