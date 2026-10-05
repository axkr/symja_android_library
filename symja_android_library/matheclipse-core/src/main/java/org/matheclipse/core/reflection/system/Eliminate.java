package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.RootsFunctions;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.InverseFunctionExpander;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.COMPARE_TERNARY;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.polynomials.PolynomialHomogenization;
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
 * <p>
 * attempts to eliminate the variables from the <code>list-of-variables</code> in the <code>
 * list-of-equations</code>.
 *
 * </blockquote>
 *
 * <p>
 * See:
 *
 * <ul>
 * <li><a href=
 * "http://en.wikipedia.org/wiki/System_of_linear_equations#Elimination_of_variables">Wikipedia -
 * System of linear equations - Elimination of variables</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt;&gt; Eliminate({x==2+y, y==z}, y)
 * x-z==2
 * </pre>
 */
public class Eliminate extends AbstractFunctionOptionEvaluator implements EliminateRules {

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
      this.fVariableDegree = degreeOf(expr, variable);
    }

    /**
     * The polynomial degree of the variable in the equation, or {@link Integer#MAX_VALUE} if the
     * equation isn't a polynomial in it.
     */
    private final int fVariableDegree;

    /** @see #fVariableDegree */
    private static int degreeOf(IAST equation, IExpr variable) {
      if (!equation.isEqual()) {
        return Integer.MAX_VALUE;
      }
      EvalEngine engine = EvalEngine.get();
      IExpr difference = engine.evalQuiet(F.Subtract(equation.arg1(), equation.arg2()));
      if (!difference.isPolynomial(variable)) {
        return Integer.MAX_VALUE;
      }
      int degree = S.Exponent.of(engine, difference, variable).toIntDefault();
      return F.isPresent(degree) && degree > 0 ? degree : Integer.MAX_VALUE;
    }

    @Override
    public int compareTo(VariableCounterVisitor other) {
      if (fVariableCounter < other.fVariableCounter) {
        return -1;
      }
      if (fVariableCounter > other.fVariableCounter) {
        return 1;
      }
      // the variable is isolated exactly from the equation of the lowest degree; a higher degree
      // needs a root, which may be one of several branches
      if (fVariableDegree < other.fVariableDegree) {
        return -1;
      }
      if (fVariableDegree > other.fVariableDegree) {
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
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      VariableCounterVisitor other = (VariableCounterVisitor) obj;
      if (fCurrentDepth != other.fCurrentDepth)
        return false;
      if (fExpr == null) {
        if (other.fExpr != null)
          return false;
      } else if (!fExpr.equals(other.fExpr))
        return false;
      if (fMaxVariableDepth != other.fMaxVariableDepth)
        return false;
      if (fNodeCounter != other.fNodeCounter)
        return false;
      if (fVariable == null) {
        if (other.fVariable != null)
          return false;
      } else if (!fVariable.equals(other.fVariable))
        return false;
      if (fVariableCounter != other.fVariableCounter)
        return false;
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
    public boolean visit(IComplex element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(IComplexNum element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(IFraction element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(IInteger element) {
      fNodeCounter++;
      return false;
    }

    @Override
    public boolean visit(INum element) {
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
  }

  /** Match <code>f(x) == y</code> expressions to determine the inverse function. */
  private static com.google.common.base.Supplier<Matcher> INVERSE_MATCHER;

  /** Match <code>Plus(....) == 0</code> expressions for a variable. */
  private static com.google.common.base.Supplier<Matcher> ZERO_PLUS_MATCHER;

  private static IAST applyRuleToAnalyzer(IExpr variable, IExpr variableValue,
      IASTAppendable eliminatedResultEquations, ArrayList<VariableCounterVisitor> analyzerList,
      EvalEngine engine) {
    variableValue = engine.evalQuiet(variableValue);
    IExpr expr;
    IAST rule = F.Rule(variable, variableValue);
    for (int j = 0; j < analyzerList.size(); j++) {
      expr = analyzerList.get(j).getExpr();
      IExpr temp = expr.replaceAll(rule);
      if (temp.isPresent()) {
        temp = F.expandAll(temp, true, true);
        if (temp.isEqual() && temp.size() == 3) {
          temp = F.Equal(F.Subtract.of(engine, temp.first(), temp.second()), F.C0);
        }
        eliminatedResultEquations.append(temp);
      } else {
        eliminatedResultEquations.append(expr);
      }
    }
    return rule;
  }

  /**
   * Returned by {@link #checkEquations(IAST, int, EvalEngine)} for a system which contains a
   * contradiction, so that the whole elimination is <code>False</code>. Compared by identity.
   */
  private static final IAST CONTRADICTION = F.list(S.False);

  /**
   * Check if the argument at the given position is an equation (i.e. <code>Equal[a,b]</code>), a
   * list of equations or a conjunction of equations and return a list of expressions, which should
   * be equal to <code>0</code>.
   *
   * <p>
   * A member which is <code>True</code> constrains nothing and is dropped; a member which is
   * <code>False</code> makes the whole system contradictory.
   *
   * @param ast
   * @param position
   * @return {@link #CONTRADICTION} if the system contains a contradiction, or {@link F#NIL} if one
   *         of the elements is not a well-formed equation.
   */
  private static IAST checkEquations(final IAST ast, int position, IASTAppendable constraints,
      EvalEngine engine) {
    IExpr arg = ast.get(position);
    if (arg.isList() || arg.isAnd()) {
      IASTAppendable equations = F.ListAlloc(arg.size());
      return collectEquations((IAST) arg, ast.topHead(), equations, constraints, engine) ? equations
          : F.NIL;
    }
    if (arg.isEqual()) {
      IAST equalAST = (IAST) arg;
      IAST components = Validate.splitListRelation(equalAST);
      if (components.isPresent()) {
        // a relation between two lists holds componentwise, e.g. `{x,y} == {1,2}` is the system
        // `x == 1` and `y == 2`
        return F.mapList(components,
            t -> F.Equal(F.evalExpandAll(t.first(), engine), F.evalExpandAll(t.second(), engine)));
      }
      return F.list(F.Equal(F.evalExpandAll(equalAST.arg1(), engine),
          F.evalExpandAll(equalAST.arg2(), engine)));
    }
    if (arg.isTrue()) {
      // a tautology constrains nothing
      return F.CEmptyList;
    }
    if (arg.isFalse()) {
      return CONTRADICTION;
    }
    // `1` is not a well-formed equation.
    return Errors.printMessage(ast.topHead(), "eqf", F.list(arg), engine);
  }

  /**
   * Collect the equations of a list or conjunction - both of which may be nested - into
   * <code>equations</code>.
   *
   * @return <code>false</code> if a member is not a well-formed equation; the message was printed
   *         in that case
   * @throws ContradictionException if a member is <code>False</code>
   */
  private static boolean collectEquations(IAST list, ISymbol head, IASTAppendable equations,
      IASTAppendable constraints, EvalEngine engine) {
    for (int i = 1; i < list.size(); i++) {
      IExpr t = list.get(i);
      if (t.isTrue()) {
        // a tautology constrains nothing
        continue;
      }
      if (t.isFalse()) {
        throw ContradictionException.CONST;
      }
      if (t.isList() || t.isAnd()) {
        if (!collectEquations((IAST) t, head, equations, constraints, engine)) {
          return false;
        }
        continue;
      }
      if (t.isRelationalBinary() && !t.isEqual()) {
        // an inequation is no equation to eliminate from, but it constrains the result
        constraints.append(t);
        continue;
      }
      if (t.isEqual()) {
        COMPARE_TERNARY b = t.first().equalTernary(t.second(), engine);
        if (b == IExpr.COMPARE_TERNARY.TRUE) {
          continue;
        }
        if (b == IExpr.COMPARE_TERNARY.FALSE) {
          throw ContradictionException.CONST;
        }
        equations.append(t);
        continue;
      }
      // `1` is not a well-formed equation.
      Errors.printMessage(head, "eqf", F.list(t), engine);
      return false;
    }
    return true;
  }

  /** Thrown while the equations are collected, if one of them is a contradiction. */
  private static final class ContradictionException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    static final ContradictionException CONST = new ContradictionException();

    private ContradictionException() {
      super(null, null, false, false);
    }
  }

  /**
   * Analyze an <code>Equal()</code> expression.
   *
   * @param equalAST an <code>Equal()</code> expression.
   * @param variable the variable which should be eliminated.
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return <code>F.NIL</code> if we can't find an equation for the given <code>variable</code>.
   */
  private static IExpr eliminateAnalyze(IAST equalAST, IExpr variable, boolean multipleValues,
      EvalEngine engine) {
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
   * Try to eliminate a variable using polynomial resultants when direct solving fails. If we have
   * at least 2 polynomial equations containing the variable, compute the resultant of a pair to
   * produce an equation free of the variable.
   *
   * @param analyzerList list of equation analyzers
   * @param variable the variable to eliminate
   * @param engine the evaluation engine
   * @return the elimination result, or {@code null} if resultant elimination also fails
   */
  private static IAST[] eliminateByResultant(ArrayList<VariableCounterVisitor> analyzerList,
      IExpr variable, EvalEngine engine) {
    if (analyzerList.size() < 2) {
      return null;
    }
    for (int i = 0; i < analyzerList.size(); i++) {
      IAST eq1 = analyzerList.get(i).getExpr();
      if (!eq1.isEqual()) {
        continue;
      }
      IExpr poly1 = engine.evaluate(F.Subtract(eq1.arg1(), eq1.arg2()));
      if (!poly1.isPolynomial(variable)) {
        continue;
      }
      for (int j = i + 1; j < analyzerList.size(); j++) {
        IAST eq2 = analyzerList.get(j).getExpr();
        if (!eq2.isEqual()) {
          continue;
        }
        IExpr poly2 = engine.evaluate(F.Subtract(eq2.arg1(), eq2.arg2()));
        if (!poly2.isPolynomial(variable)) {
          continue;
        }
        try {
          // Compute resultant to eliminate the variable
          IExpr resultant = engine.evaluate(F.ternaryAST3(S.Resultant, poly1, poly2, variable));
          if (resultant.isPresent() && !resultant.isZero() && resultant.isFree(variable)) {
            // Remove repeated factors (the resultant can introduce squares)
            resultant = extractSquareFreeFactors(resultant, engine);

            IAST[] result = new IAST[2];
            IASTAppendable eliminatedEquations = F.ListAlloc(analyzerList.size());
            eliminatedEquations.append(F.Equal(resultant, F.C0));
            for (int k = 0; k < analyzerList.size(); k++) {
              if (k != i && k != j) {
                eliminatedEquations.append(analyzerList.get(k).getExpr());
              }
            }
            result[0] = eliminatedEquations;
            result[1] = F.NIL;
            return result;
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          // continue trying other pairs
        }
      }
    }
    return null;
  }

  /**
   * Analyze the <code>Equal()</code> terms, if we find an expression which equals the given <code>
   * variabe</code>
   *
   * @param analyzerList the list of <code>Equal()</code> terms with statistics of it's equations.
   * @param variable the variable which should be eliminated.
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>
   *     variable</code> or the eliminated list of equations in index <code>[0]</code> and the last
   *         rule which is used for variable elimination in index <code>[1]</code>.
   */
  protected static IAST[] eliminateOneVariable(ArrayList<VariableCounterVisitor> analyzerList,
      IExpr variable, boolean multipleValues, EvalEngine engine) {
    IASTAppendable eliminatedResultEquations = F.ListAlloc(analyzerList.size());
    for (int i = 0; i < analyzerList.size(); i++) {
      IExpr variableValues =
          eliminateAnalyze(analyzerList.get(i).getExpr(), variable, multipleValues, engine);
      if (variableValues.isPresent()) {
        analyzerList.remove(i);
        IAST[] result = new IAST[2];
        if (variableValues.isList()) {
          IAST listOfRules = ((IAST) variableValues).map(x -> {
            return applyRuleToAnalyzer(variable, x, eliminatedResultEquations, analyzerList,
                engine);
          });
          result[0] = eliminatedResultEquations;
          result[1] = listOfRules;
        } else if (variableValues.isEqual()) {
          // It's a transformed equation, not a value for the variable.
          eliminatedResultEquations.append(variableValues);
          for (int j = 0; j < analyzerList.size(); j++) {
            eliminatedResultEquations.append(analyzerList.get(j).getExpr());
          }
          result[0] = eliminatedResultEquations;
          result[1] = F.NIL;
        } else {
          IAST rule = applyRuleToAnalyzer(variable, variableValues, eliminatedResultEquations,
              analyzerList, engine);
          result[0] = eliminatedResultEquations;
          result[1] = rule;
        }
        return result;
      }
    }
    // Fallback: try resultant-based elimination for polynomial equation pairs
    return eliminateByResultant(analyzerList, variable, engine);
  }

  /**
   * Eliminates one variable from the given list of equations.
   * 
   * @param ast
   * @param variable
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @param engine
   * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>
   *     variable</code> or the eliminated list of equations in index <code>[0]</code> and the last
   *         rule which is used for variable elimination in index <code>[1]</code>.
   */
  public static IAST[] eliminateOneVariable(IAST ast, IExpr variable, boolean multipleValues,
      EvalEngine engine) {
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

  /**
   * Extract the square-free part from a factored expression. For example, {@code (a+b)^2*(c+d)}
   * becomes {@code (a+b)*(c+d)}, removing exponent duplications introduced by resultant
   * computation.
   *
   * @param expr the expression (typically a resultant)
   * @param engine the evaluation engine
   * @return the square-free part of the expression
   */
  private static IExpr extractSquareFreeFactors(IExpr expr, EvalEngine engine) {
    IExpr factored = engine.evaluate(F.Factor(expr));
    if (factored.isPower() && factored.exponent().isPositiveResult()) {
      return factored.base();
    }
    if (factored.isTimes()) {
      IASTAppendable uniqueProduct = F.TimesAlloc(factored.size());
      for (int i = 1; i < ((IAST) factored).size(); i++) {
        IExpr factor = ((IAST) factored).get(i);
        if (factor.isPower() && factor.exponent().isPositiveResult()) {
          uniqueProduct.append(factor.base());
        } else {
          uniqueProduct.append(factor);
        }
      }
      return engine.evaluate(uniqueProduct);
    }
    return factored;
  }

  /**
   * Extract the variable from the given <code>expr</code> assuming <code>expr == 0</code>.
   *
   * @param expr an expression.
   * @param variable the variable which should be eliminated.
   * @param multipleValues if <code>true</code> multiple results are returned as list of values
   * @return <code>F.NIL</code> if we can't find an equation for the given <code>variable</code>.
   */
  public static IExpr extractVariable(IExpr expr, IExpr variable, boolean multipleValues,
      EvalEngine engine) {
    Predicate<IExpr> predicate = Predicates.in(variable);
    IExpr result = F.NIL;
    if (!expr.isFree(predicate, true)) {
      result = extractVariableRecursive(expr, F.C0, predicate, variable, multipleValues, engine);
    }
    return result;
  }

  /**
   * Extract a value for the given variable.
   *
   * @param exprWithVariable expression which contains the given variable.
   * @param exprWithoutVariable expression which doesn't contain the given variable.
   * @param predicate the predicate to check for the variable
   * @param variable the variable which should be eliminated.
   * @param multipleValues if true multiple results are returned as list of values
   * @param engine the evaluation engine
   * @return F.NIL if we can't find an equation for the given variable.
   */
  private static IExpr extractVariableRecursive(IExpr exprWithVariable, IExpr exprWithoutVariable,
      Predicate<IExpr> predicate, IExpr variable, boolean multipleValues, EvalEngine engine) {
    if (exprWithVariable.equals(variable)) {
      return exprWithoutVariable;
    }
    if (!exprWithoutVariable.isSpecialsFree()) {
      return F.NIL;
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
              return extractVariableRecursive(ast.arg1(), inverseFunction, predicate, variable,
                  multipleValues, engine);
            }
            return S.True;
          } else {
            // example: Sin(f(x)) == y -> f(x) == ArcSin(y)
            inverseFunction.append(exprWithoutVariable);
            return extractVariableRecursive(ast.arg1(), inverseFunction, predicate, variable,
                multipleValues, engine);
          }
        }
      } else {
        int size = ast.size();
        if (size > 2 && ast.leafCount() < Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT / 2) {
          IExpr lambertWEquationResult =
              solveLambertWEquation(ast, exprWithoutVariable, variable, multipleValues, engine);
          if (lambertWEquationResult.isPresent()) {
            return lambertWEquationResult;
          }
          if (exprWithoutVariable.isZero() && ast.isPlus()) {
            IExpr zeroPlus = applyMatcher(zeroPlusMatcher(), elimzeroplus, ast, exprWithoutVariable,
                variable, multipleValues, engine);
            if (zeroPlus.isPresent()) {
              return zeroPlus;
            }
          }
          IExpr inverse = applyMatcher(inverseMatcher(), eliminv, ast, exprWithoutVariable,
              variable, multipleValues, engine);
          if (inverse.isPresent()) {
            return inverse;
          }
        }

        if (ast.isPlus()) {
          // a + b + c....
          if (exprWithoutVariable.isNumericFunction() //
              && ast.isPolynomial(variable) && ast.isNumericFunction(variable)) {
            IAST temp =
                RootsFunctions.rootsOfVariable(F.Subtract.of(engine, ast, exprWithoutVariable),
                    F.C1, F.list(variable), engine.isNumericMode(), engine);
            if (temp.isList() && temp.size() > 1) {
              if (!multipleValues || temp.size() == 2) {
                return temp.first();
              }
              return temp;
            }
          }

          IAST[] plusFilter = ast.filter(x -> x.isFree(predicate, true));
          IAST plusWithoutVariable = plusFilter[0];
          IAST plusWithVariable = plusFilter[1];
          if (plusWithoutVariable.isAST0()) {
            IExpr factor = engine.evaluateNIL(F.Factor(ast));
            if (factor.isPresent() && factor.isTimes()) {
              // a * b * c....
              IAST times = (IAST) factor;
              IAST[] timesFilter = times.filter(x -> x.isFree(predicate, true));
              IAST timesWithoutVariable = timesFilter[0];
              IAST timesWithVariable = timesFilter[1];
              if (timesWithoutVariable.isAST0()) {
                return F.NIL;
              }
              // an inexact content can be pulled out of the remaining sum again and again
              // (rescaled by 2^24, 2^-24, ...), so dividing by it is no step forward
              boolean inexactContent = timesWithVariable.oneIdentity1().isPlus()
                  && timesWithoutVariable.forAll(IExpr::isInexactNumber);
              if (!inexactContent) {
                IExpr rhsWithoutVariable =
                    engine.evaluate(F.Divide(exprWithoutVariable, timesWithoutVariable));
                return extractVariableRecursive(timesWithVariable.oneIdentity1(),
                    rhsWithoutVariable, predicate, variable, multipleValues, engine);
              }
            }
          } else {
            IExpr rhsWithoutVariable =
                engine.evaluate(F.Subtract(exprWithoutVariable, plusWithoutVariable));
            IExpr res = extractVariableRecursive(plusWithVariable.oneIdentity0(),
                rhsWithoutVariable, predicate, variable, multipleValues, engine);
            if (res.isPresent()) {
              return res;
            }
          }
          if (!ast.isFree(x -> x.isTrigFunction(), true)) {
            // the closed form of a*Sin(u)+b*Cos(u)==c is preferred over the exponential rewrite
            IExpr linearSinCos = tryLinearSinCos(ast, exprWithoutVariable, predicate, variable,
                multipleValues, engine);
            if (linearSinCos.isPresent()) {
              return linearSinCos;
            }
            return tryTrigToExp(ast, exprWithoutVariable, variable, multipleValues, engine);
          } else if (ast.isFree(x -> x.isLog(), true)) {
            return tryPowerExpand(ast, exprWithoutVariable, variable, multipleValues, engine);
          } else {
            IExpr attracted = tryLogAttraction(ast, exprWithoutVariable, predicate, variable,
                multipleValues, engine);
            if (attracted.isPresent()) {
              return attracted;
            }
            return tryAffineLog(ast, exprWithoutVariable, variable, multipleValues, engine);
          }
        } else if (ast.isTimes()) {
          // a * b * c....
          IAST[] timesFilter = ast.filter(x -> x.isFree(predicate, true));
          IAST timesWithoutVariable = timesFilter[0];
          IAST timesWithVariable = timesFilter[1];
          if (timesWithoutVariable.isAST0()) {
            IExpr[] numerDenom = AlgebraUtil.numeratorDenominator(ast, true, EvalEngine.get());
            if (!numerDenom[1].isOne()) {
              IExpr[] numerLinear = numerDenom[0].linear(variable);
              if (numerLinear != null) {
                IExpr[] denomLinear = numerDenom[1].linear(variable);
                if (denomLinear != null) {
                  IExpr temp = EvalEngine.get()
                      .evaluate(numerLinear[1].subtract(denomLinear[1].times(exprWithoutVariable)));
                  if (!temp.isZero()) {
                    return numerLinear[0].negate().plus(denomLinear[0].times(exprWithoutVariable))
                        .times(temp.power(-1L));
                  }
                }
              }
              if (!exprWithoutVariable.isZero() && !numerDenom[1].isFree(
                  x -> x.isPower() && x.exponent().isFraction() && !x.base().isFree(variable),
                  false)) {
                // u/Sqrt(v) == c is u - c*Sqrt(v) == 0, the two terms tryPowerExpand is for. The
                // right side stays one factor, whatever it is made of: y/Sqrt(1+y^2) == x + c.
                // one symbol stands for the right side while the equation is solved, so that the
                // answer is written in x + c and not in its expanded powers
                boolean compound = exprWithoutVariable.isPlus();
                IExpr side = compound ? F.Dummy("k") : exprWithoutVariable;
                IExpr cleared =
                    engine.evaluate(F.Subtract(numerDenom[0], F.Times(side, numerDenom[1])));
                if (cleared.isPlus() && cleared.argSize() == 2
                    && isPolynomialAndRadical((IAST) cleared, variable)) {
                  IExpr solved =
                      tryPowerExpand((IAST) cleared, F.C0, variable, multipleValues, engine);
                  return compound && solved.isPresent()
                      ? engine.evaluate(F.subst(solved, side, exprWithoutVariable))
                      : solved;
                }
              }
            }
            return F.NIL;
          }
          IExpr value = engine.evaluate(F.Divide(exprWithoutVariable, timesWithoutVariable));
          return extractVariableRecursive(timesWithVariable.oneIdentity1(), value, predicate,
              variable, multipleValues, engine);
        } else if (ast.isPower()) {
          IExpr base = ast.base();
          IExpr exponent = ast.exponent();
          if (exponent.isFree(predicate, true)) {
            // f(x) ^ a
            IExpr reversedPower = exponent.inverse();
            if (!reversedPower.isMathematicalIntegerNonNegative()
                && !Errors.allowInverseFunctions(S.InverseFunction, engine)) {
              return F.NIL;
            }
            // the elimination is algebraic: raising the equation to a power may gain roots,
            // which `Solve` sorts out by cross checking its solutions
            IExpr value = engine.evaluate(F.Power(exprWithoutVariable, reversedPower));
            IExpr res1 =
                extractVariableRecursive(base, value, predicate, variable, multipleValues, engine);
            // For even integer exponent with multipleValues, also consider the negative root
            if (multipleValues && exponent.isInteger() && exponent.isEvenResult()) {
              IExpr negValue = engine.evaluate(F.Negate(value));
              if (!negValue.equals(value)) {
                IExpr res2 = extractVariableRecursive(base, negValue, predicate, variable,
                    multipleValues, engine);
                if (res2.isPresent()) {
                  if (res1.isPresent()) {
                    // Merge both result lists
                    IASTAppendable merged = F.ListAlloc();
                    if (res1.isList()) {
                      merged.appendArgs((IAST) res1);
                    } else {
                      merged.append(res1);
                    }
                    if (res2.isList()) {
                      merged.appendArgs((IAST) res2);
                    } else {
                      merged.append(res2);
                    }
                    return merged;
                  }
                  return res2;
                }
              }
            }
            return res1;
          } else if (base.isFree(predicate, true)) {
            if (!InverseFunctionExpander
                .isFiniteValue(engine.evaluate(F.Log(exprWithoutVariable)))) {
              // a power never takes the value 0: Log(0) would give f(x) == -Infinity, so the
              // equation has no solution
              return S.True;
            }
            // Decide between the single principal value and the full periodic family of complex
            // solutions for `base ^ f(x) == exprWithoutVariable`.
            final boolean principalOnly;
            if (base.isE()) {
              // Classic behavior for base E: a real exponent has a unique principal value,
              // otherwise return the periodic complex family.
              principalOnly = exponent.isRealResult();
            } else {
              // For a numeric base `a != E` expand into the periodic family
              // even for a bare variable: Solve(2^x == 60, x) gives
              // (2*I*Pi*C(1))/Log(2) + Log(60)/Log(2)
              principalOnly = exponent.isRealResult() //
                  || !base.isNumericFunction() //
                  // a single value - InverseFunction(2^# &) is Log(#1)/Log(2)&
                  || (!multipleValues && exponent.equals(variable));
            }
            if (principalOnly) {
              // base ^ f(x) == exprWithoutVariable -> f(x) == Log(exprWithoutVariable)/Log(base)
              IExpr value = base.isE() ? F.Log(exprWithoutVariable)
                  : F.Divide(F.Log(exprWithoutVariable), F.Log(base));
              return extractVariableRecursive(exponent, value, predicate, variable, multipleValues,
                  engine);
            }

            // base ^ f(x) == exprWithoutVariable /; Element(f(x), Complexes)
            // f(x) == (2*I*Pi*c_n + Log(exprWithoutVariable)) / Log(base)
            try {
              IExpr c_n = F.C(engine.incConstantCounter());
              final IExpr exprwovar = exprWithoutVariable;
              IExpr expr =
                  F.Plus(F.Times(F.C2, F.CI, S.Pi, c_n), engine.evaluate(F.Log(exprwovar)));
              if (!base.isE()) {
                expr = F.Divide(expr, F.Log(base));
              }
              IExpr temp = F.ConditionalExpression(expr, F.Element(c_n, S.Integers));
              return extractVariableRecursive(exponent, temp, predicate, variable, multipleValues,
                  engine);
            } finally {
              engine.decConstantCounter();
            }
          }
        }
      }
    }
    return F.NIL;
  }

  /** Get the matcher for <code>f(x) == y</code> expressions to determine the inverse function. */
  private static Matcher inverseMatcher() {
    return INVERSE_MATCHER.get();
  }

  /**
   * Solve <code>termsEqualZero == 0</code> for <code>variable</code>, after one of the
   * <code>try...</code> transformations rewrote the equation.
   *
   * <p>
   * The transformed equation is usually a polynomial - in the variable itself or in one kernel
   * <code>g(variable)</code> - so it is solved here instead of by {@link S#Solve}: the elimination
   * is the more primitive of the two, and <code>Solve</code> calls it, not the other way round.
   *
   * @param termsEqualZero the left hand side of the transformed equation
   * @param variable the variable to solve for
   * @param multipleValues if <code>true</code> multiple values are returned as a list
   * @param engine the evaluation engine
   * @return the value(s) of the variable or {@link F#NIL}
   */
  private static IExpr solveTransformed(IExpr termsEqualZero, IExpr variable,
      boolean multipleValues, EvalEngine engine) {
    if (termsEqualZero.isNIL() || termsEqualZero.isFree(variable)) {
      return F.NIL;
    }
    // the roots of a polynomial in the variable
    IExpr roots = rootsOrNIL(termsEqualZero, variable, multipleValues, engine);
    if (roots.isPresent()) {
      return roots;
    }
    // the isolation of the variable, which also inverts the elementary functions and applies the
    // rules of `EliminateRules`
    Predicate<IExpr> predicate = Predicates.in(variable);
    IExpr isolated =
        extractVariableRecursive(termsEqualZero, F.C0, predicate, variable, multipleValues, engine);
    if (isolated.isPresent() && !isolated.isTrue()) {
      return isolated;
    }
    // a rational equation is solved by the roots of its numerator
    IExpr numerator = termsEqualZero.isAST()
        ? AlgebraUtil.numeratorDenominator((IAST) termsEqualZero, true, engine)[0]
        : termsEqualZero;
    if (!numerator.equals(termsEqualZero)) {
      roots = rootsOrNIL(numerator, variable, multipleValues, engine);
      if (roots.isPresent()) {
        return roots;
      }
    }
    // a polynomial in one kernel g(variable): solve it for the kernel and invert the kernel
    IExpr kernelValues = solveByKernel(numerator, variable, multipleValues, engine);
    if (kernelValues.isPresent()) {
      return kernelValues;
    }
    // a product is zero, if one of its factors is zero
    return solveZeroProduct(numerator, variable, multipleValues, engine);
  }

  /**
   * Solve <code>numerator == 0</code> for a <code>numerator</code> which is a product, or which
   * factors into one: every factor which contains the variable is solved on its own.
   * <code>x^x == x</code> is <code>(x-1)*Log(x) == 0</code> after taking logarithms.
   *
   * @return the value(s) of the variable or {@link F#NIL}, if one of the factors can't be solved
   */
  private static IExpr solveZeroProduct(IExpr numerator, IExpr variable, boolean multipleValues,
      EvalEngine engine) {
    IExpr product = numerator.isTimes() ? numerator : engine.evaluateNIL(F.Factor(numerator));
    if (!product.isTimes()) {
      return F.NIL;
    }
    IAST times = (IAST) product;
    if (times.count(x -> !x.isFree(variable)) < 2) {
      // c*f(variable) is no simpler than the equation which was asked
      return F.NIL;
    }
    IASTAppendable values = F.ListAlloc(times.size());
    for (int i = 1; i < times.size(); i++) {
      IExpr factor = times.get(i);
      if (factor.isFree(variable)) {
        continue;
      }
      if (factor.isPower() && factor.exponent().isFree(variable)) {
        if (!factor.exponent().isPositiveResult()) {
          return F.NIL;
        }
        factor = factor.base();
      }
      IExpr value = solveTransformed(factor, variable, true, engine);
      if (value.isNIL()) {
        // the values of this factor would be lost
        return F.NIL;
      }
      if (value.isTrue()) {
        // the factor is never zero
        continue;
      }
      IAST valueList = value.isList() ? (IAST) value : F.list(value);
      for (int j = 1; j < valueList.size(); j++) {
        // an inverted function isn't evaluated yet: InverseFunction(Log)(0) is 1
        IExpr v = engine.evaluate(valueList.get(j));
        if (!values.exists(x -> x.equals(v))) {
          values.append(v);
        }
      }
    }
    if (values.argSize() == 0) {
      return F.NIL;
    }
    if (multipleValues) {
      return values;
    }
    return values.argSize() == 1 ? values.arg1() : F.NIL;
  }

  /**
   * The roots of <code>termsEqualZero</code>, if it is a polynomial in <code>variable</code>.
   *
   * @return the root(s) or {@link F#NIL} if it isn't a polynomial or has no root in radicals
   */
  private static IExpr rootsOrNIL(IExpr termsEqualZero, IExpr variable, boolean multipleValues,
      EvalEngine engine) {
    if (!termsEqualZero.isPolynomial(variable)) {
      return F.NIL;
    }
    IAST roots = RootsFunctions.rootsOfVariable(termsEqualZero, F.C1, F.list(variable),
        engine.isNumericMode(), engine);
    if (!roots.isList() || roots.size() <= 1 || !roots.isFree(S.Root, true)) {
      return F.NIL;
    }
    if (!multipleValues || roots.size() == 2) {
      return roots.first();
    }
    return roots;
  }

  /**
   * Solve an equation which is a polynomial in one kernel <code>g(variable)</code>: the roots of
   * that polynomial are determined and <code>g(variable) == root</code> is inverted for every one
   * of them.
   *
   * @return the value(s) of the variable or {@link F#NIL}
   */
  public static IExpr solveByKernel(IExpr termsEqualZero, IExpr variable, boolean multipleValues,
      EvalEngine engine) {
    PolynomialHomogenization homogenization = new PolynomialHomogenization(engine, true);
    IExpr poly = homogenization.replaceForward(termsEqualZero);
    Set<ISymbol> kernelVariables = homogenization.substitutedVariablesSet();
    if (poly.isNIL() || kernelVariables.size() != 1) {
      return F.NIL;
    }
    ISymbol kernelVariable = kernelVariables.iterator().next();
    IExpr kernel = homogenization.replaceBackward(kernelVariable);
    if (kernel.equals(variable) || kernel.isFree(variable)) {
      return F.NIL;
    }
    if (poly.isAST()) {
      // a Laurent polynomial in the kernel has the roots of its numerator
      poly = AlgebraUtil.numeratorDenominator((IAST) poly, true, engine)[0];
    }
    IExpr kernelRoots = rootsOrNIL(poly, kernelVariable, true, engine);
    if (kernelRoots.isNIL()) {
      return F.NIL;
    }
    IAST rootList = kernelRoots.isList() ? (IAST) kernelRoots : F.list(kernelRoots);
    Predicate<IExpr> predicate = Predicates.in(variable);
    IASTAppendable values = F.ListAlloc(rootList.size());
    for (int i = 1; i < rootList.size(); i++) {
      IExpr root = rootList.get(i);
      if (!InverseFunctionExpander.isFiniteValue(root)) {
        // the kernel never takes this value
        continue;
      }
      IExpr value = invertKernel(kernel, root, predicate, variable, multipleValues, engine);
      if (value.isNIL() || value.isTrue()) {
        continue;
      }
      if (value.isList()) {
        values.appendArgs((IAST) value);
      } else {
        values.append(value);
      }
    }
    if (values.argSize() == 0) {
      return F.NIL;
    }
    return multipleValues ? values : values.arg1();
  }

  /**
   * Solve <code>kernel == root</code> for <code>variable</code>. A periodic kernel like
   * <code>Cosh(variable)</code> has infinitely many solutions, which are returned as the
   * {@link S#ConditionalExpression} families of {@link InverseFunctionExpander}; every other kernel
   * is inverted by its principal inverse function.
   *
   * @return the value(s) of the variable or {@link F#NIL}
   */
  private static IExpr invertKernel(IExpr kernel, IExpr root, Predicate<IExpr> predicate,
      IExpr variable, boolean multipleValues, EvalEngine engine) {
    if (multipleValues && kernel.isAST1() && kernel.first().equals(variable)
        && kernel.head().isBuiltInSymbol()
        && isPeriodicFunction(((IBuiltInSymbol) kernel.head()).ordinal())) {
      IExpr periodic =
          InverseFunctionExpander.expandPeriodicInverse((IBuiltInSymbol) kernel.head(), root);
      if (periodic.isPresent()) {
        return periodic;
      }
    }
    return extractVariableRecursive(kernel, root, predicate, variable, multipleValues, engine);
  }

  /**
   * Whether the function head with this id is periodic, so that the equation
   * <code>head(variable) == value</code> has infinitely many solutions.
   */
  private static boolean isPeriodicFunction(int headID) {
    switch (headID) {
      case ID.Cos:
      case ID.Cosh:
      case ID.Cot:
      case ID.Coth:
      case ID.Csc:
      case ID.Csch:
      case ID.Sec:
      case ID.Sech:
      case ID.Sin:
      case ID.Sinh:
      case ID.Tan:
      case ID.Tanh:
        return true;
      default:
        return false;
    }
  }


  /**
   * Add the inequations of the input, which are no equations to eliminate from, to the result.
   */
  private static IExpr withConstraints(IExpr result, IAST constraints, EvalEngine engine) {
    if (constraints.isEmpty() || result.isFalse()) {
      return result;
    }
    IASTAppendable and = F.ast(S.And, constraints.size() + 1);
    and.append(result);
    and.appendArgs(constraints);
    return engine.evaluate(and);
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
   * Apply one of the rule sets of {@link EliminateRules} to
   * <code>ast == exprWithoutVariable</code>.
   *
   * @param matcher the rules to apply
   * @param head the dummy head the rules are defined for
   * @param ast the left hand side of the equation, which contains the variable
   * @param exprWithoutVariable the right hand side of the equation
   * @param variable the variable to isolate
   * @param multipleValues if <code>true</code> multiple values are returned as a list
   * @param engine the evaluation engine
   * @return the transformed equation, the value(s) of the variable, or {@link F#NIL}
   */
  private static IExpr applyMatcher(Matcher matcher, ISymbol head, IAST ast,
      IExpr exprWithoutVariable, IExpr variable, boolean multipleValues, EvalEngine engine) {
    IExpr result = matcher.apply(F.binaryAST2(head, ast, variable));
    if (result.isNIL()) {
      return F.NIL;
    }
    if (result.isEqual()) {
      // a transformed equation, not a value
      return Errors.allowInverseFunctions(S.InverseFunction, engine) ? result : F.NIL;
    }
    return resultWithIfunMessage(result, variable, exprWithoutVariable, multipleValues, engine);
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
  private static IExpr resultWithIfunMessage(IExpr result, IExpr subExpr, IExpr replacementExpr,
      boolean multipleValues, EvalEngine engine) {
    if (!Errors.allowInverseFunctions(S.InverseFunction, engine)) {
      return F.NIL;
    }
    IExpr expr = F.subst(result, subExpr, replacementExpr);
    if (!multipleValues && expr.isList() && expr.size() > 1) {
      return expr.first();
    }
    return expr;
  }

  /**
   * <p>
   * Solve <code>a*E^(b*f(x))*f(x) == z </code> as
   * <code>InverseFunction(f, 1, 1)[ProductLog((b*z)/a)/b]</code>
   * 
   * <p>
   * See: <a href=
   * "https://de.wikipedia.org/wiki/Lambertsche_W-Funktion#Verwendung_au%C3%9Ferhalb_der_Kombinatorik">DE:Wikipedia
   * - Lambertsche_W-Funktion Verwendung_ausserhalb der Kombinatorik</a>
   * 
   * @param exprWithVariable the left-hand-side expression which contains a variable
   * @param exprWithoutVariable the right-hand-side expression which contains no variable
   * @param variable the variable
   * @param multipleValues
   * @param engine
   * @return
   */
  private static IExpr solveLambertWEquation(IAST exprWithVariable, IExpr exprWithoutVariable,
      IExpr variable, boolean multipleValues, EvalEngine engine) {
    if (exprWithVariable.isTimes()) {
      IASTAppendable[] variableFilter = exprWithVariable.filter(x -> x.isFree(variable));
      int expIndexOf = variableFilter[1].indexOf(x -> x.isExp());
      if (expIndexOf > 0) {
        IExpr variableFunction1 = variableFilter[1].removeAtCopy(expIndexOf).oneIdentity1();
        if (variableFunction1.isAST1()) {
          IExpr expFunction = variableFilter[1].get(expIndexOf);

          IExpr a = variableFilter[0].oneIdentity1();
          IExpr b = F.C1;
          IExpr z = exprWithoutVariable;

          IExpr variableFunction2 = expFunction.exponent();
          if (variableFunction2.isTimes()) {
            IASTAppendable[] f1TimesFilter =
                ((IAST) variableFunction2).filter(x -> x.isFree(variable));
            if (f1TimesFilter[0].argSize() > 0) {
              b = f1TimesFilter[0].oneIdentity1();
              variableFunction2 = f1TimesFilter[1].oneIdentity1();
            }
          }

          if (variableFunction1.equals(variableFunction2)) {
            IExpr head = variableFunction1.head();
            // ProductLog((b*z)/a)
            IAST lambertW = F.ProductLog(F.Times(a.inverse(), b, z));
            IExpr result =
                F.unaryAST1(F.InverseFunction(head, F.C1, F.C1), F.Times(b.inverse(), lambertW));
            return resultWithIfunMessage(result, variable, exprWithoutVariable, multipleValues,
                engine);
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * <p>
   * Check if the <code>plusAST</code> has 2 arguments and try a {@link F#PowerExpand(IExpr)}
   * transformation step on the args.
   * 
   * <p>
   * See: <a href="//
   * https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">Solving
   * Symbolic Equations with PRESS</a>
   * 
   * @param plusAST
   * @param variable
   * @param multipleValues
   * @param engine
   * @return
   */
  /**
   * Whether the two terms are a polynomial in the variable and a factor free of the variable
   * times one fractional power of a polynomial in it: <code>y - (c+x)*Sqrt(1+y^2)</code>.
   *
   * <p>
   * That is the equation which clearing the radical solves. Anything wider is not worth offering:
   * a sum with nested roots or with logarithms beside them comes back unsolved after a long time
   * spent on its logarithms, which is what the equations of a higher degree in <code>y'</code>
   * hand over by the dozen.
   */
  private static boolean isPolynomialAndRadical(IAST twoTerms, IExpr variable) {
    IAST variables = F.list(variable);
    for (int i = 1; i <= 2; i++) {
      IExpr polynomial = twoTerms.get(i);
      IExpr radicalTerm = twoTerms.get(3 - i);
      if (!polynomial.isPolynomial(variables)) {
        continue;
      }
      IAST factors = radicalTerm.isTimes() ? (IAST) radicalTerm : F.Times(radicalTerm);
      int radicals = 0;
      boolean other = false;
      for (int j = 1; j <= factors.argSize(); j++) {
        IExpr factor = factors.get(j);
        if (factor.isFree(variable)) {
          continue;
        }
        if (factor.isPower() && factor.exponent().isFraction()
            && factor.base().isPolynomial(variables)) {
          radicals++;
        } else {
          other = true;
        }
      }
      if (radicals == 1 && !other) {
        return true;
      }
    }
    return false;
  }

  /**
   * The sum collected in the first power with a fractional exponent whose base contains the
   * variable, or {@link F#NIL} if there is none.
   *
   * @param radical receives that power
   */
  private static IExpr collectInRadical(IAST plusAST, IExpr variable, IExpr[] radical,
      EvalEngine engine) {
    plusAST.isFree(x -> {
      if (radical[0] == null && x.isPower() && x.exponent().isFraction()
          && !x.base().isFree(variable)) {
        radical[0] = x;
      }
      return false;
    }, true);
    if (radical[0] == null) {
      return F.NIL;
    }
    return engine.evaluate(F.Collect(plusAST, radical[0]));
  }

  /**
   * Solves <code>p*x + q*Log(gamma*x + delta) + rest == exprWithoutVariable</code>, with
   * <code>delta != 0</code>, through the unknown <code>u == gamma*x + delta</code>.
   *
   * <p>
   * In <code>u</code> the equation is <code>(p/gamma)*u + q*Log(u) + ... == 0</code>, which the
   * rules answer with <code>ProductLog</code>; with the logarithm of anything but a multiple of
   * the variable they do not match. <code>y + 2*Log(1+y) == x</code> is such an equation, and so
   * is the relation every equation <code>y' == (b+a*y)/(d+c*y)</code> integrates to.
   *
   * @return the value of the variable, or {@link F#NIL} if the logarithms of the equation do not
   *         all have the same argument of the first degree
   */
  private static IExpr tryAffineLog(IAST plusAST, IExpr exprWithoutVariable, IExpr variable,
      boolean multipleValues, EvalEngine engine) {
    IExpr[] argument = new IExpr[1];
    boolean[] several = new boolean[1];
    plusAST.isFree(x -> {
      if (x.isLog() && !x.first().isFree(variable)) {
        if (argument[0] == null) {
          argument[0] = x.first();
        } else if (!argument[0].equals(x.first())) {
          several[0] = true;
        }
      }
      return false;
    }, true);
    IExpr arg = argument[0];
    if (arg == null || several[0] || !arg.isPolynomial(F.list(variable))) {
      return F.NIL;
    }
    IExpr gamma = engine.evaluate(F.Coefficient(arg, variable, F.C1));
    IExpr delta = engine.evaluate(F.Coefficient(arg, variable, F.C0));
    if (gamma.isZero() || delta.isZero() || !gamma.isFree(variable) || !delta.isFree(variable)
        || !engine.evaluate(F.Expand(F.Subtract(arg, F.Plus(F.Times(gamma, variable), delta))))
            .isZero()) {
      return F.NIL;
    }
    ISymbol u = F.Dummy("u");
    IExpr transformed = F.subst(F.Subtract(plusAST, exprWithoutVariable), F.Log(arg), F.Log(u));
    transformed = F.subst(transformed, variable, F.Divide(F.Subtract(u, delta), gamma));
    transformed = engine.evaluate(F.Collect(F.ExpandAll(transformed), F.Log(u)));
    if (!transformed.isFree(variable)) {
      return F.NIL;
    }
    IExpr value = solveTransformed(transformed, u, multipleValues, engine);
    if (value.isNIL() || !value.isFree(u) || value.isTrue() || value.isFalse()) {
      return F.NIL;
    }
    IExpr back = engine.evaluate(F.Divide(F.Subtract(value, delta), gamma));
    // gamma and delta can share a factor, E^(4*x) in Log(3*E^(4*x)+2*E^(4*x)*y), which then
    // stands in the numerator and in the denominator of every term
    IExpr together = engine.evaluate(F.Together(back));
    return together.isPresent() && together.leafCount() < back.leafCount() ? together : back;
  }

  /** The equations {@link #tryPowerExpand} is solving on this thread. */
  private static final ThreadLocal<Set<IExpr>> POWER_EXPAND_IN_PROGRESS =
      ThreadLocal.withInitial(HashSet::new);

  private static IExpr tryPowerExpand(IAST plusAST, IExpr exprWithoutVariable, IExpr variable,
      boolean multipleValues, EvalEngine engine) {
    if (plusAST.argSize() > 2) {
      // y/Sqrt(1+y^2) == c + x arrives expanded, as y - c*Sqrt(1+y^2) - x*Sqrt(1+y^2), which is
      // the two terms y - (c+x)*Sqrt(1+y^2) this method is for once the radical is collected. One
      // symbol stands for the collected coefficient while the equation is solved, so that the
      // answer is written in c + x and not in its expanded powers.
      IExpr[] radical = new IExpr[1];
      IExpr collected = collectInRadical(plusAST, variable, radical, engine);
      if (collected.isPlus() && collected.argSize() == 2
          && isPolynomialAndRadical((IAST) collected, variable)) {
        for (int i = 1; i <= 2; i++) {
          IExpr term = ((IAST) collected).get(i);
          IExpr coefficient = engine.evaluate(F.Divide(term, radical[0]));
          if (coefficient.isPlus() && coefficient.isFree(variable)) {
            IExpr k = F.Dummy("k");
            // -c-x is written as -(c+x), so that the answer has c+x in it and no doubled sign
            boolean negative = ((IAST) coefficient).forAll(x -> x.isNegativeSigned());
            if (negative) {
              coefficient = engine.evaluate(F.Negate(coefficient));
            }
            IAST withSymbol = ((IAST) collected).setAtCopy(i,
                negative ? F.Times(F.CN1, k, radical[0]) : F.Times(k, radical[0]));
            IExpr evaluated = engine.evaluate(withSymbol);
            if (evaluated.isPlus() && evaluated.argSize() == 2) {
              IExpr solved = tryPowerExpand((IAST) evaluated, exprWithoutVariable, variable,
                  multipleValues, engine);
              return solved.isPresent() ? engine.evaluate(F.subst(solved, k, coefficient)) : solved;
            }
          }
        }
        plusAST = (IAST) collected;
      }
    }
    if (plusAST.argSize() == 2) {
      // The equation is a + b == exprWithoutVariable, so what the logarithm of the left side is
      // equated with is the logarithm of exprWithoutVariable - b. Reading it as -b instead solves
      // a different equation and answers it: E^(10*y)/(3/2+y) == E^(4*x) reaches here as
      // 2*E^(10*y) - 2*E^(4*x)*y == 3*E^(4*x), and dropping that right side turned it into
      // 10*y - Log(y) == 4*x, whose answer does not satisfy the equation which was asked.
      IExpr rhs = engine.evaluate(F.Subtract(exprWithoutVariable, plusAST.second()));
      // powerExpandLHS == powerExpandRHS
      IExpr powerExpandLHS = Algebra.powerExpand(F.Log(plusAST.first()), false);
      IExpr powerExpandRHS = Algebra.powerExpand(F.Log(rhs), false);
      if (powerExpandLHS.isPresent() || powerExpandRHS.isPresent()) {
        if (powerExpandLHS.isNIL()) {
          powerExpandLHS = F.Log(plusAST.first());
        }
        if (powerExpandRHS.isNIL()) {
          powerExpandRHS = F.Log(rhs);
        }
        IExpr termsEqualZero = engine.evaluate(F.Subtract(powerExpandLHS, powerExpandRHS));
        // Taking logarithms can restate an equation which is already being solved further up:
        // 1 + E^(1/v)*v == E^c becomes -Log(E^c - E^(1/v)*v) == 0, which exponentiates back to
        // E^c - E^(1/v)*v == 1, whose logarithms are c - Log(1 + E^(1/v)*v) == 0 again, and the
        // two handed each other back until the stack overflowed.
        Set<IExpr> inProgress = POWER_EXPAND_IN_PROGRESS.get();
        if (!inProgress.add(termsEqualZero)) {
          return F.NIL;
        }
        IExpr result;
        try {
          result = solveTransformed(termsEqualZero, variable, multipleValues, engine);
        } finally {
          inProgress.remove(termsEqualZero);
        }
        if (result.isPresent()) {
          // Inverse functions are being used. Values may be lost for multivalued inverses.
          if (!Errors.allowInverseFunctions(S.InverseFunction, engine)) {
            return F.NIL;
          }
          return result;
        }
      }
    }
    return F.NIL;
  }

  /**
   * Solve <code>a*Sin(u) + b*Cos(u) + rest == c</code>, where <code>a, b, rest</code> and
   * <code>c</code> are free of the variable. With <code>d = c - rest</code> and
   * <code>s = Sqrt(a^2+b^2-d^2)</code> the two families of solutions are
   * 
   * <pre>
   * u == ArcTan((b*d - a*s)/(a^2+b^2), (a*d + b*s)/(a^2+b^2)) + 2*Pi*C(1)
   * u == ArcTan((b*d + a*s)/(a^2+b^2), (a*d - b*s)/(a^2+b^2)) + 2*Pi*C(1)
   * </pre>
   * 
   * because the two arguments of <code>ArcTan</code> are <code>Cos(u)</code> and
   * <code>Sin(u)</code>: they satisfy the equation and the sum of their squares is <code>1</code>.
   * <p>
   * See: <a href=
   * "https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">Solving
   * Symbolic Equations with PRESS</a> - 3.7
   * 
   * @return {@link F#NIL} if the equation hasn't this form, or if it has numeric coefficients and
   *         no real solution
   */
  private static IExpr tryLinearSinCos(IAST plusAST, IExpr exprWithoutVariable,
      Predicate<IExpr> predicate, IExpr variable, boolean multipleValues, EvalEngine engine) {
    IASTAppendable sinCoefficient = F.PlusAlloc(2);
    IASTAppendable cosCoefficient = F.PlusAlloc(2);
    IASTAppendable rest = F.PlusAlloc(plusAST.argSize());
    IExpr u = F.NIL;
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr term = plusAST.get(i);
      if (term.isFree(predicate, true)) {
        rest.append(term);
        continue;
      }
      IExpr coefficient = F.C1;
      IExpr function = term;
      if (term.isTimes()) {
        IAST[] timesFilter = ((IAST) term).filter(x -> x.isFree(predicate, true));
        coefficient = timesFilter[0].oneIdentity1();
        function = timesFilter[1].oneIdentity1();
      }
      if (!(function.isSin() || function.isCos())) {
        return F.NIL;
      }
      if (u.isNIL()) {
        u = function.first();
      } else if (!u.equals(function.first())) {
        return F.NIL;
      }
      (function.isSin() ? sinCoefficient : cosCoefficient).append(coefficient);
    }
    if (sinCoefficient.isAST0() || cosCoefficient.isAST0()) {
      // a single Sin() or Cos() is isolated with its inverse function
      return F.NIL;
    }
    IExpr a = sinCoefficient.oneIdentity0();
    IExpr b = cosCoefficient.oneIdentity0();
    IExpr d = engine.evaluate(F.Subtract(exprWithoutVariable, rest.oneIdentity0()));
    IExpr norm = engine.evaluate(F.Expand(F.Plus(F.Sqr(a), F.Sqr(b))));
    if (norm.isPossibleZero(true)) {
      // a == +/- I*b
      return F.NIL;
    }
    IExpr discriminant = engine.evaluate(F.Expand(F.Subtract(norm, F.Sqr(d))));
    if (discriminant.isNegativeResult()) {
      // no real solution
      return F.NIL;
    }
    IExpr s = engine.evaluate(F.Sqrt(discriminant));
    IExpr c_n = F.C(engine.incConstantCounter());
    try {
      IASTAppendable solutions = F.ListAlloc(2);
      for (IExpr sign : new IExpr[] {F.CN1, F.C1}) {
        IExpr signS = engine.evaluate(F.Times(sign, s));
        IExpr cosU = F.Divide(F.Plus(F.Times(b, d), F.Times(a, signS)), norm);
        IExpr sinU = F.Divide(F.Subtract(F.Times(a, d), F.Times(b, signS)), norm);
        IExpr family = F.ConditionalExpression(
            F.Plus(engine.evaluate(F.ArcTan(cosU, sinU)), F.Times(F.C2, S.Pi, c_n)),
            F.Element(c_n, S.Integers));
        IExpr solution =
            extractVariableRecursive(u, family, predicate, variable, multipleValues, engine);
        if (solution.isNIL()) {
          return F.NIL;
        }
        if (!multipleValues) {
          return solution;
        }
        if (solution.isList()) {
          solutions.appendArgs((IAST) solution);
        } else {
          solutions.append(solution);
        }
        if (s.isZero()) {
          // a^2+b^2 == d^2: both families are the same
          break;
        }
      }
      return solutions;
    } finally {
      engine.decConstantCounter();
    }
  }

  /** The equations {@link #tryLogAttraction} is solving on this thread. */
  private static final ThreadLocal<Set<IExpr>> LOG_ATTRACTION_IN_PROGRESS =
      ThreadLocal.withInitial(HashSet::new);

  /**
   * The <i>Attraction</i> method of PRESS: if every term with the variable is an integer multiple
   * of a logarithm, <code>n1*Log(u1) + n2*Log(u2) + ... + rest == c</code>, bring the occurrences
   * of the variable together as <code>u1^n1 * u2^n2 * ... == E^(c - rest)</code> and solve this
   * equation.
   * <p>
   * <code>Log(u) + Log(v) == Log(u*v)</code> only holds on the principal branch, so a root is only
   * a solution if it satisfies the equation which was asked: <code>Log(x+1) + Log(x-1) == 3</code>
   * gives <code>x^2-1 == E^3</code> with the roots <code>-Sqrt(1+E^3)</code> and
   * <code>Sqrt(1+E^3)</code>, and only the second one is a solution.
   * <p>
   * See: <a href=
   * "https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">Solving
   * Symbolic Equations with PRESS</a> - 3.4 Attraction
   * 
   * @param plusAST
   * @param exprWithoutVariable
   * @param predicate
   * @param variable
   * @param multipleValues
   * @param engine
   * @return
   */
  private static IExpr tryLogAttraction(IAST plusAST, IExpr exprWithoutVariable,
      Predicate<IExpr> predicate, IExpr variable, boolean multipleValues, EvalEngine engine) {
    IASTAppendable product = F.TimesAlloc(plusAST.argSize());
    IASTAppendable rest = F.PlusAlloc(plusAST.argSize());
    for (int i = 1; i < plusAST.size(); i++) {
      IExpr term = plusAST.get(i);
      if (term.isFree(predicate, true)) {
        rest.append(term);
      } else if (term.isLog()) {
        product.append(term.first());
      } else if (term.isTimes() && term.size() == 3 && term.first().isInteger()
          && term.second().isLog()) {
        product.append(F.Power(term.second().first(), term.first()));
      } else {
        return F.NIL;
      }
    }
    if (product.argSize() < 2) {
      // a single logarithm is isolated with its inverse function
      return F.NIL;
    }
    if (product.argSize() == 2
        && product.arg1().isPowerReciprocal() != product.arg2().isPowerReciprocal()) {
      // Log(1+u) - Log(1-u) == 2*ArcTanh(u) is exact and solved as u == Tanh(c/2)
      IExpr numerator = product.arg1().isPowerReciprocal() ? product.arg2() : product.arg1();
      IExpr denominator =
          product.arg1().isPowerReciprocal() ? product.arg1().base() : product.arg2().base();
      if (engine.evaluate(F.Expand(F.Plus(numerator, denominator))).equals(F.C2)) {
        return F.NIL;
      }
    }
    IExpr rhs = engine.evaluate(F.Exp(F.Subtract(exprWithoutVariable, rest.oneIdentity0())));
    IExpr termsEqualZero = engine.evaluate(F.Subtract(product, rhs));
    // PowerExpand() restates the product as the sum of logarithms which is being solved here
    Set<IExpr> inProgress = LOG_ATTRACTION_IN_PROGRESS.get();
    if (!inProgress.add(termsEqualZero)) {
      return F.NIL;
    }
    IExpr result;
    try {
      result = solveTransformed(termsEqualZero, variable, true, engine);
    } finally {
      inProgress.remove(termsEqualZero);
    }
    if (result.isPresent()) {
      IExpr values = result.isList() ? result : F.list(result);
      {
        IExpr equationEqualZero = F.Subtract(plusAST, exprWithoutVariable);
        IAST solutions = ((IAST) values).select(value -> {
          IExpr residual = engine.evalQuiet(F.N(F.subst(equationEqualZero, variable, value)));
          // a root is only rejected, if it is known not to satisfy the equation
          return !residual.isNumber() || F.isZero(residual.evalfc(), 1e-10);
        });
        if (solutions.argSize() > 0) {
          return multipleValues ? solutions : solutions.first();
        }
      }
    }
    return F.NIL;
  }

  /**
   * Try to solve equations which contain trigonometric functions by using a
   * {@link F#TrigToExp(IExpr)} transformation step, so that the equation contains
   * <code>E^(...)</code> expressions.
   * 
   * @param plusAST
   * @param variable
   * @param multipleValues
   * @param engine
   * @return
   */
  private static IExpr tryTrigToExp(IAST plusAST, IExpr exprWithoutVariable, IExpr variable,
      boolean multipleValues, EvalEngine engine) {
    if (plusAST.leafCount() > Config.MAX_SIMPLIFY_TOGETHER_LEAFCOUNT) {
      return F.NIL;
    }
    // What is solved is the whole equation and not only its left side: plusAST is equal to
    // exprWithoutVariable, which is not always zero.
    IExpr termsEqualZero = exprWithoutVariable.isZero() //
        ? engine.evaluateNIL(F.TrigToExp(plusAST))
        : engine.evaluateNIL(F.TrigToExp(F.Subtract(plusAST, exprWithoutVariable)));
    if (termsEqualZero.isPresent()) {
      IExpr result = solveTransformed(termsEqualZero, variable, multipleValues, engine);
      if (result.isPresent()) {
        // Inverse functions are being used. Values may be lost for multivalued inverses.
        if (!Errors.allowInverseFunctions(S.InverseFunction, engine)) {
          return F.NIL;
        }
        return result;
      }
    }
    return F.NIL;
  }

  /** Match <code>Plus(....) == 0</code> expressions for a variable. */
  private static Matcher zeroPlusMatcher() {
    return ZERO_PLUS_MATCHER.get();
  }

  public Eliminate() {}

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    SolveOptions eliminateOptions = SolveOptions.of(SolveOptions.ELIMINATE_KEYS, options);
    for (int i = 3; i < originalAST.size(); i++) {
      IExpr option = originalAST.get(i);
      if (!option.isRuleAST() || !isDeclaredOption(option.first())) {
        // an option which isn't supported - `Mode->Modular` for instance - must not be ignored
        return F.NIL;
      }
    }
    if (argSize > 0 && argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }
    long precision = SolveUtils.workingPrecision(ast, eliminateOptions.workingPrecision(), engine);
    if (precision == SolveUtils.INVALID_PRECISION) {
      return F.NIL;
    }
    // the sites which apply an inverse function sit several layers below this call, so the
    // `InverseFunctions` mode travels with the engine for its dynamic extent
    int oldInverseFunctions = engine.setInverseFunctions(eliminateOptions.inverseFunctionsMode());
    IExpr result;
    try {
      result = eliminate(ast, engine);
    } finally {
      engine.setInverseFunctions(oldInverseFunctions);
    }
    if (result.isNIL() || precision == SolveUtils.MACHINE_PRECISION_REQUESTED) {
      return result;
    }
    // the elimination itself is exact; the requested precision is applied to its result
    return engine.evaluate(F.N(result, F.ZZ(precision)));
  }

  /** Whether <code>key</code> is one of the options {@link S#Eliminate} supports. */
  private static boolean isDeclaredOption(IExpr key) {
    for (IBuiltInSymbol declared : SolveOptions.ELIMINATE_KEYS) {
      if (key == declared) {
        return true;
      }
    }
    return false;
  }

  private static IExpr eliminate(final IAST ast, EvalEngine engine) {
    try {
      IASTAppendable constraints = F.ListAlloc();
      IAST termsEqualZeroList = checkEquations(ast, 1, constraints, engine);
      if (termsEqualZeroList.isNIL()) {
        return F.NIL;
      }
      if (termsEqualZeroList == CONTRADICTION) {
        return S.False;
      }
      IAST vars = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
      if (vars.isNIL()) {
        return F.NIL;
      }

      IAST result = termsEqualZeroList;
      IAST[] temp;
      ISymbol variable;
      for (int i = 1; i < vars.size(); i++) {
        variable = (ISymbol) vars.get(i);

        temp = eliminateOneVariable(result, variable, false, engine);
        if (temp != null) {
          result = temp[0];
        } else {
          return withConstraints(resultAsAndEquations(result), constraints, engine);
        }
      }
      return withConstraints(resultAsAndEquations(result), constraints, engine);
    } catch (ContradictionException cex) {
      return S.False;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.Eliminate, rex, EvalEngine.get());
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, SolveOptions.ELIMINATE_KEYS, SolveOptions.ELIMINATE_DEFAULTS);
    INVERSE_MATCHER = Suppliers.memoize(EliminateRules::init1);
    ZERO_PLUS_MATCHER = Suppliers.memoize(EliminateRules::init2);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
