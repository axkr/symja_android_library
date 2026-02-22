package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.chocosolver.solver.constraints.extension.hybrid.HybridTuples;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.builtin.PolynomialFunctions;
import org.matheclipse.core.builtin.RootsFunctions;
import org.matheclipse.core.convert.ChocoConvert;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.CreamConvert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.Assumptions;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.ExprAnalyzer;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.QuarticSolver;

/**
 *
 *
 * <pre>
 * Solve(equations, vars)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * attempts to solve <code>equations</code> for the variables <code>vars</code>.
 *
 * </blockquote>
 *
 * <pre>
 * Solve(equations, vars, domain)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * attempts to solve <code>equations</code> for the variables <code>vars</code> in the given
 * <code>domain</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Solve({x^2==4,x+y^2==6}, {x,y})
 * {{x-&gt;2,y-&gt;2},{x-&gt;2,y-&gt;-2},{x-&gt;-2,y-&gt;2*2^(1/2)},{x-&gt;-2,y-&gt;(-2)*2^(1/2)}}
 *
 * &gt;&gt; Solve({2 x + 3*y == 4, 3*x - 4*y &lt;= 5,x - 2*y &gt; -21}, {x,  y}, Integers)
 * {{x-&gt;-7,y-&gt;6},{x-&gt;-4,y-&gt;4},{x-&gt;-1,y-&gt;2}}
 *
 * &gt;&gt; Solve(Xor(a, b, c, d) &amp;&amp; (a || b) &amp;&amp; ! (c || d), {a, b, c, d}, Booleans)
 * {{a-&gt;False,b-&gt;True,c-&gt;False,d-&gt;False},{a-&gt;True,b-&gt;False,c-&gt;False,d-&gt;False}}
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="DSolve.md">DSolve</a>, <a href="Eliminate.md">Eliminate</a>,
 * <a href="GroebnerBasis.md">GroebnerBasis</a>, <a href="FindRoot.md">FindRoot</a>,
 * <a href="NRoots.md">NRoots</a>
 */
public class Solve extends AbstractFunctionOptionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger(Solve.class);

  /** Check an expression, if it's an allowed object. */
  protected static final class IsWrongSolveExpression implements Predicate<IExpr> {
    IExpr wrongExpr;

    public IsWrongSolveExpression() {
      wrongExpr = null;
    }

    public IExpr getWrongExpr() {
      return wrongExpr;
    }

    @Override
    public boolean test(IExpr input) {
      if (input.isDirectedInfinity() || input.isIndeterminate()) {
        // input is representing a DirectedInfinity() or Indeterminate
        // object
        wrongExpr = input;
        return true;
      }
      return false;
    }
  }

  protected static class NoSolution extends Exception {
    private static final long serialVersionUID = -8578380756971796776L;

    /** Solution couldn't be found. */
    public static final int NO_SOLUTION_FOUND = 1;

    /** Definitely wrong solution. */
    public static final int WRONG_SOLUTION = 0;

    final int solType;

    public NoSolution(int solType) {
      super();
      this.solType = solType;
    }

    public int getType() {
      return solType;
    }
  }

  /**
   * Wraps the options set to {@link S#Solve}
   */
  public static class SolveData {
    final IExpr[] options;
    final Map<IExpr, IAST> intervalDataMap;
    final IASTAppendable intervalInequations;

    public SolveData() {
      this(defaultOptionValues());
    }

    public SolveData(IExpr[] options) {
      this.options = options;
      this.intervalDataMap = new HashMap<IExpr, IAST>();
      this.intervalInequations = F.ListAlloc();
    }

    /**
     * Get the value for the option {@link S#GenerateConditions}
     * 
     * @return
     */
    protected IExpr generateConditions() {
      return options[0];
    }

    public boolean isGenerateConditions() {
      return options[0].isTrue();
    }

    /**
     * Recursively solve the list of analyzers.
     *
     * @param analyzerList list of analyzers, which determine, if an expression has linear,
     *        polynomial or other form
     * @param variables the list of variables
     * @param resultList the list of result values as rules assigned to each variable
     * @param maximumNumberOfResults the maximum number of results in <code>resultList</code>:
     *        <code>0
     *     </code> gives all results.
     * @param matrix
     * @param vector
     * @param engine
     * @return throws NoSolution
     */
    protected IASTAppendable analyzeSublistRecursive(ArrayList<ExprAnalyzer> analyzerList,
        IAST variables, IASTAppendable resultList, int maximumNumberOfResults,
        IASTAppendable matrix, IASTAppendable vector, boolean numericFlag, EvalEngine engine)
        throws NoSolution {
      ExprAnalyzer exprAnalyzer;
      Collections.sort(analyzerList);
      int[] currEquation = new int[] {0};
      while (currEquation[0] < analyzerList.size()) {
        exprAnalyzer = analyzerList.get(currEquation[0]);
        if (exprAnalyzer.getNumberOfVars() == 0) {
          checkNoVariableEquation(exprAnalyzer, engine);
        } else if (exprAnalyzer.getNumberOfVars() == 1) {
          IAST listOfRules = solveOneVariableEquation(exprAnalyzer, numericFlag, engine);
          if (listOfRules.isPresent()) {
            IASTAppendable temp = substituteNumericResults(analyzerList, variables, resultList,
                matrix, vector, maximumNumberOfResults, exprAnalyzer, currEquation, listOfRules,
                numericFlag, engine);
            if (temp.isPresent()) {
              return temp;
            }
          }
          throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
        } else if (exprAnalyzer.isLinear()) {
          appendLinearEquation(exprAnalyzer, matrix, vector, engine);
        } else {
          throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
        }
        currEquation[0]++;
      }
      return resultList;
    }

    private IAST solveOneVariableEquation(ExprAnalyzer exprAnalyzer, boolean numericFlag,
        EvalEngine engine) {
      IAST listOfRules = F.NIL;
      if (exprAnalyzer.isLinearOrPolynomial()) {
        listOfRules = rootsOfUnivariatePolynomial(exprAnalyzer, engine);
        if (listOfRules.isPresent()) {
          listOfRules =
              exprAnalyzer.mapOnOriginal(exprAnalyzer.getPowerRewrittenExpr(), listOfRules);
        }
      } else if (numericFlag) {
        listOfRules = findRoot(exprAnalyzer, engine);
        if (listOfRules.isPresent()) {
          listOfRules = exprAnalyzer.mapOnOriginal(exprAnalyzer.getOriginalExpr(), listOfRules);
        }
      }
      return listOfRules;
    }

    private void appendLinearEquation(ExprAnalyzer exprAnalyzer, IASTAppendable matrix,
        IASTAppendable vector, EvalEngine engine) {
      matrix.append(engine.evaluate(exprAnalyzer.getRow()));
      vector.append(engine.evaluate(F.Negate(exprAnalyzer.getValue())));
    }

    private static void checkNoVariableEquation(ExprAnalyzer exprAnalyzer, EvalEngine engine)
        throws NoSolution {
      // check if the equation equals zero.
      IExpr expr = exprAnalyzer.getNumerator();
      if (!expr.isZero()) {
        if (expr.isNumber() || expr.isInfinity() || expr.isNegativeInfinity()) {
          throw new NoSolution(NoSolution.WRONG_SOLUTION);
        }
        if (!S.PossibleZeroQ.ofQ(engine, expr)) {
          throw new NoSolution(NoSolution.NO_SOLUTION_FOUND);
        }
      }
    }

    private IASTAppendable substituteNumericResults(ArrayList<ExprAnalyzer> analyzerList,
        IAST variables, IASTAppendable resultList, IASTAppendable matrix, IASTAppendable vector,
        int maximumNumberOfResults, ExprAnalyzer exprAnalyzer, int[] currEquation, IAST listOfRules,
        boolean numericFlag, EvalEngine engine) throws NoSolution {
      listOfRules = substituteInverseResults(listOfRules, engine);
      boolean evaled = false;
      ++currEquation[0];
      for (int k = 1; k < listOfRules.size(); k++) {
        if (currEquation[0] >= analyzerList.size()) {
          resultList.append(F.list(listOfRules.getAST(k)));
          if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
            return resultList;
          }
          evaled = true;
        } else {
          // collect linear and univariate polynomial equations:
          IAST substitutionRule = listOfRules.getAST(k);
          IExpr substitutionVariable = substitutionRule.arg1();
          IAST subVariables = variables.remove(x -> x.equals(substitutionVariable));
          if (subVariables.isPresent()) {
            ArrayList<ExprAnalyzer> subAnalyzerList = substituteRulesInAnalyzerList(analyzerList,
                currEquation[0], substitutionRule, subVariables, engine);
            try {
              IASTAppendable subMatrix = F.ListAlloc();
              IASTAppendable subVector = F.ListAlloc();
              IAST subResultList = analyzeSublistRecursive(subAnalyzerList, subVariables,
                  F.ListAlloc(), maximumNumberOfResults, subMatrix, subVector, numericFlag, engine);
              if (subResultList.isPresent()) {
                evaled = true;
                IASTAppendable tempResult = addSubResultsToResultsList(resultList, subResultList,
                    substitutionRule, maximumNumberOfResults);
                if (tempResult.isPresent()) {
                  return tempResult;
                }
                if (subVector.size() > 1) {
                  IASTAppendable linearSolution =
                      solveRowReducedMatrix(subMatrix, subVector, subVariables, F.NIL,
                          this.intervalInequations, substitutionRule, resultList, engine);
                  if (linearSolution.isPresent()) {
                    matrix.clear();
                    vector.clear();
                    return linearSolution;
                  }
                }
              }
            } catch (NoSolution e) {
              if (e.getType() == NoSolution.WRONG_SOLUTION) {
                evaled = true;
              }
            }
          }
        }
      }
      if (evaled) {
        return resultList;
      }
      return F.NIL;
    }

    /**
     * Substitute possible dummy {@link Solve#$InverseFunction(IBuiltInSymbol, IExpr)} objects in
     * the <code>listOfRules</code> with the inverse functions.
     * 
     * @param listOfRules
     * @param engine
     * @return
     */
    private static IASTAppendable substituteInverseResults(IAST listOfRules, EvalEngine engine) {
      IASTAppendable newListOfRules = F.ListAlloc(listOfRules.size() + 4);
      for (int i = 1; i < listOfRules.size(); i++) {
        IAST rule = (IAST) listOfRules.get(i);
        IExpr rhs = F.subst(rule.arg2(), SolveUtils::substitute$InverseFunction);
        rhs = engine.evaluate(rhs);
        if (rhs.isList()) {
          IAST rhsList = (IAST) rhs;
          for (int j = 1; j < rhsList.size(); j++) {
            newListOfRules.append(rule.setAtCopy(2, rhsList.get(j)));
          }
        } else {
          newListOfRules.append(rule.setAtCopy(2, rhs));
        }
      }
      return newListOfRules;
    }

    /**
     * Add the sub-results to the results list. If <code>maximumNumberOfResults</code> is reached
     * return the resultList, otherwise return <code>F#NIL</code>.
     *
     * @param resultList
     * @param subResultList
     * @param kListOfSolveRules
     * @param maximumNumberOfResults
     * @return if <code>maximumNumberOfResults</code> is reached return the resultList, otherwiaw
     *         return <code>F#NIL</code>.
     */
    private static IASTAppendable addSubResultsToResultsList(IASTAppendable resultList,
        IAST subResultList, IAST kListOfSolveRules, int maximumNumberOfResults) {
      for (IExpr expr : subResultList) {
        if (expr.isList()) {
          IASTAppendable list;
          if (expr instanceof IASTAppendable) {
            list = (IASTAppendable) expr;
          } else {
            list = ((IAST) expr).copyAppendable();
          }
          list.append(1, kListOfSolveRules);
          resultList.append(list);
          if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
            return resultList;
          }
        } else {
          resultList.append(expr);
          if (maximumNumberOfResults > 0 && maximumNumberOfResults <= resultList.size()) {
            return resultList;
          }
        }
      }
      return F.NIL;
    }

    /**
     * For all analyzers in <code>analyzerList</code> from position to the last element substitute
     * the variables by the rules in <code>kListOfSolveRules</code> and create a new (sub-)analyzer
     * list.
     * 
     * @param analyzerList
     * @param analyzerListStartPosition
     * @param substitutionRule
     * @param variablesList
     * @param engine
     *
     * @return
     */
    private ArrayList<ExprAnalyzer> substituteRulesInAnalyzerList(
        ArrayList<ExprAnalyzer> analyzerList, int analyzerListStartPosition, IAST substitutionRule,
        IAST variablesList, EvalEngine engine) {
      ExprAnalyzer exprAnalyzer;
      ArrayList<ExprAnalyzer> subAnalyzerList = new ArrayList<ExprAnalyzer>();
      for (int i = analyzerListStartPosition; i < analyzerList.size(); i++) {
        IExpr expr = analyzerList.get(i).getTogetherExpr();
        IExpr temp = expr.replaceAll(substitutionRule);
        if (temp.isPresent()) {
          expr = engine.evaluate(temp);
        }
        // reusing old analyzer not possible; we've removed 1 variable in variablesList
        exprAnalyzer = new ExprAnalyzer(expr, variablesList, isGenerateConditions(), engine);
        exprAnalyzer.simplifyAndAnalyze();
        subAnalyzerList.add(exprAnalyzer);
      }
      return subAnalyzerList;
    }

    /**
     * Evaluate the roots of a univariate polynomial with the Roots() function.
     *
     * @param exprAnalyzer
     * @param engine
     * @return
     */
    private static IAST rootsOfUnivariatePolynomial(ExprAnalyzer exprAnalyzer, EvalEngine engine) {
      IExpr numerator = exprAnalyzer.getNumerator();
      IExpr denominator = exprAnalyzer.getDenominator();
      // try to solve the expr for one of the variables in the symbol set
      for (IExpr variable : exprAnalyzer.getVariableSet()) {
        IAST temp = rootsOfUnivariatePolynomial(numerator, denominator, variable, engine);
        if (temp.isPresent()) {
          return temp;
        }
      }
      return F.NIL;
    }

    /**
     * Evaluate the roots of a univariate polynomial with the Roots() function.
     *
     * @param exprAnalyzer
     * @param engine
     * @return
     */
    private static IAST findRoot(ExprAnalyzer exprAnalyzer, EvalEngine engine) {
      // try to solve the original expr for one of the variables in the symbol set
      IExpr originalExpr = exprAnalyzer.getOriginalExpr();
      if (originalExpr != null) {
        for (IExpr variable : exprAnalyzer.getVariableSet()) {
          IExpr temp = engine.evaluate( //
              F.FindRoot(originalExpr, //
                  F.List(variable, F.C1)));
          if (temp.isList()) {
            return (IAST) temp;
          }
        }
      }
      return F.NIL;
    }

    /**
     * Checks if the inequation is a simple bound strictly defining the given variable.
     */
    private static boolean isSimpleVarBound(IExpr ineq, IExpr var) {
      if (ineq.isAST(S.Between, 3) && ineq.first().equals(var)) {
        return true;
      }
      if (ineq.isOr()) {
        System.out.println(ineq);
      }
      if (ineq.isRelationalBinary() && !ineq.isAST(S.Equal) && !ineq.isAST(S.Unequal)) {
        boolean containsVar = false;
        boolean complexVar = false;
        for (int i = 1; i < ineq.size(); i++) {
          if (ineq.get(i).equals(var)) {
            containsVar = true;
          } else if (!ineq.get(i).isFree(var)) {
            complexVar = true;
          }
        }
        return containsVar && !complexVar;
      }
      return false;
    }

    public static IAST rootsOfUnivariatePolynomial(IExpr numerator, IExpr denominator,
        IExpr variable, EvalEngine engine) {
      IExpr temp = F.NIL;

      if (numerator.isNumericMode() && denominator.isOne()) {
        temp = RootsFunctions.complexRoots(numerator, F.list(variable), engine);
      }
      if (temp.isNIL()) {
        temp = RootsFunctions.rootsOfVariable(numerator, denominator, F.list(variable),
            numerator.isNumericMode(), engine);
      }
      if (temp.isPresent()) {
        if (temp.isSameHeadSizeGE(S.List, 2)) {
          IAST rootsList = (IAST) temp;
          IASTAppendable resultList = F.mapList(rootsList, root -> F.Rule(variable, root));
          return QuarticSolver.sortASTArguments(resultList);
        }
      }
      return F.NIL;
    }

    /**
     * Solve the list of equations recursively. Return a list of rules <code>
     * {var1->expr1, var1->expr2, ...}</code> (typically for NSolve function) or return a &quot;list
     * of list of rules&quot; (typically for Solve function) <code>
     * {{var1->expr11, var1->expr12,...}, {var1->expr21, var1->expr22,...}, ...}</code>. The method
     * solves for the first variable from the <code>variables</code> list and inserts the solution
     * back in the remaining equations and calls the method recursively again with this new system.
     *
     * @param termsEqualZeroList the list of expressions, which should equal <code>0</code>
     * @param inequationsList a list of inequality constraints
     * @param numericFlag if <code>true</code>, try to find a numeric solution
     * @param variables the variables for which the equations should be solved
     * @param engine
     * @return a list of rules (typically NSolve) or a list of list of rules (typically Solve) of
     *         the solutions, <code>F.NIL</code> otherwise.
     */
    public IExpr solveRecursive(IASTMutable termsEqualZeroList, IAST inequationsList,
        boolean numericFlag, IAST variables, EvalEngine engine) {
      IASTMutable temp = solveTimesEquationsRecursively(termsEqualZeroList, inequationsList,
          numericFlag, variables, true, engine);
      if (temp.isPresent()) {
        return solveNumeric(QuarticSolver.sortASTArguments(temp), numericFlag, engine);
      }

      if (inequationsList.isEmpty() && termsEqualZeroList.size() == 2 && variables.size() == 2) {
        IExpr firstVariable = variables.arg1();
        IExpr res =
            eliminateOneVariable(termsEqualZeroList, firstVariable, true, numericFlag, engine);
        if (res.isNIL()) {
          if (numericFlag) {
            // find numerically with start value 0
            res = engine
                .evalQuiet(F.FindRoot(termsEqualZeroList.arg1(), F.list(firstVariable, F.C0)));
          }
        }
        if (!res.isList()
            || !res.isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(), true)) {
          return F.NIL;
        }
        IASTAppendable resultList = F.ListAlloc(1);
        resultList.append(res);

        IASTMutable crossChecking = crossChecking(termsEqualZeroList, resultList, engine);
        if (crossChecking.argSize() != 1) {
          return F.CEmptyList;
        }

        return solveNumeric(res, numericFlag, engine);
      }

      if (termsEqualZeroList.size() > 2 && variables.size() >= 3) {
        // expensive recursion try
        IExpr firstEquation = termsEqualZeroList.arg1();

        IASTMutable reducedEqualZeroList = termsEqualZeroList.copyAppendable();
        for (int i = 1; i < variables.size(); i++) {
          IExpr variable = variables.get(i);

          IAST[] reduced = Eliminate.eliminateOneVariable(F.list(F.Equal(firstEquation, F.C0)),
              variable, true, engine);
          if (reduced != null) {
            variables = variables.splice(i);
            reducedEqualZeroList = reducedEqualZeroList.removeAtCopy(1);
            // oneVariableRule = ( firstVariable -> reducedExpression )
            IAST oneVariableRule = reduced[1];
            IExpr replaced = reducedEqualZeroList.replaceAll(oneVariableRule);
            if (replaced.isList()) {
              IExpr subResult = solveRecursive((IASTMutable) replaced, inequationsList, numericFlag,
                  variables, engine);
              if (subResult.isListOfLists()) {
                IASTMutable result = F.mapList((IAST) subResult, t -> {
                  final IAST listOfRules = (IAST) t;
                  IExpr replaceAllExpr = oneVariableRule.second().replaceAll(listOfRules);
                  if (replaceAllExpr.isPresent()) {
                    replaceAllExpr = S.Simplify.of(engine, replaceAllExpr);
                    return listOfRules.appendClone(F.Rule(variable, replaceAllExpr));
                  }
                  return F.NIL;
                });
                return crossChecking(termsEqualZeroList, result, engine);
              } else if (subResult.isList()) { // important for NSolve
                replaced = oneVariableRule.second().replaceAll((IAST) subResult);
                if (replaced.isPresent()) {
                  IASTAppendable result = ((IAST) subResult).copyAppendable();
                  result.append(F.Rule(variable, replaced));
                  return crossChecking(termsEqualZeroList, result, engine);
                }
              }
            }
          }
        }
      }
      return F.NIL;
    }

    /**
     * if <code>isNumeric == true</code> do a numeric calculation
     *
     * @param expr
     * @param isNumeric
     * @param engine
     * @return
     */
    private static IExpr solveNumeric(IExpr expr, boolean isNumeric, EvalEngine engine) {
      return expr.isPresent() ? isNumeric ? engine.evalN(expr) : expr : F.NIL;
    }

    /**
     * Use the <code>Eliminate()</code> function to extract one variable.
     *
     * @param termsEqualZeroList a list of expressions which equals zero.
     * @param variable the variable which should be eliminated in the term
     * @param multipleValues if <code>true</code> multiple results are returned as list of values
     * @param numeric evaluate in numericMode
     * @param engine
     * @return
     */
    private static IAST eliminateOneVariable(IAST termsEqualZeroList, IExpr variable,
        boolean multipleValues, boolean numeric, EvalEngine engine) {
      if (!termsEqualZeroList.arg1().isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(),
          true)) {
        return F.NIL;
      }
      // copy the termsEqualZeroList back to a list of F.Equal(...) expressions
      // because Eliminate() operates on equations.
      IAST equalsASTList = termsEqualZeroList.mapThread(F.Equal(F.Slot1, F.C0), 1);
      IAST[] tempAST =
          Eliminate.eliminateOneVariable(equalsASTList, variable, multipleValues, engine);
      if (tempAST != null) {
        IAST lastRuleUsedForVariableElimination = tempAST[1];
        if (lastRuleUsedForVariableElimination != null) {
          if (lastRuleUsedForVariableElimination.isRule()
              && lastRuleUsedForVariableElimination.second().isTrue()) {
            return F.CEmptyList;
          }
          if (numeric && lastRuleUsedForVariableElimination.arg2().isConditionalExpression()) {
            // evaluate numerically
            IAST conditionalExpression = (IAST) lastRuleUsedForVariableElimination.arg2();
            if (conditionalExpression.arg2().isAST(S.Element, 3)) {
              IAST element = (IAST) conditionalExpression.arg2();
              IExpr constantSymbol = element.arg1();
              IExpr domain = element.arg2();
              if (constantSymbol.isAST(S.C, 2) //
                  && (domain == S.Integers || domain == S.Reals || domain == S.Complexes)) {
                // try constant value = 0.0
                IAST temp = substituteConstantSymbolByValue(conditionalExpression.arg1(),
                    constantSymbol, F.CD0, lastRuleUsedForVariableElimination, engine);
                if (temp.isPresent()) {
                  lastRuleUsedForVariableElimination = temp;
                } else {
                  // try constant value = 1.0
                  lastRuleUsedForVariableElimination =
                      substituteConstantSymbolByValue(conditionalExpression.arg1(), constantSymbol,
                          F.CD1, lastRuleUsedForVariableElimination, engine)
                              .orElse(lastRuleUsedForVariableElimination);
                }
              }
            }

          }

          if (lastRuleUsedForVariableElimination.isList()) {
            IAST list = lastRuleUsedForVariableElimination;
            return F.mapList(list, x -> F.list(x));
          }
          return F.list(F.list(lastRuleUsedForVariableElimination));
        }
      }
      return F.NIL;
    }

    /**
     * Substitute all (sub-) expressions <code>constantSymbol</code> in <code>expr</code> with
     * <code>numericValue</code>. If the substitution result is no number, the method returns
     * {@link F#NIL}
     * 
     * @param expr
     * @param constantSymbol
     * @param numericValue
     * @param lastRuleUsedForVariableElimination
     * @param engine
     * @return {@link F#NIL} if the substitution result is no number
     */
    private static IAST substituteConstantSymbolByValue(IExpr expr, IExpr constantSymbol,
        IExpr numericValue, IAST lastRuleUsedForVariableElimination, EvalEngine engine) {
      IExpr numericResult = engine.evalN(F.xreplace(expr, constantSymbol, numericValue));
      if (numericResult.isNumber()) {
        // Inverse functions are being used. Values may be lost for multivalued inverses.
        Errors.printMessage(S.Solve, "ifun", F.List());
        return lastRuleUsedForVariableElimination.setAtCopy(2, numericResult);
      }
      return F.NIL;
    }

    /**
     * @param termsEqualZeroList the list of expressions, which should equal <code>0</code>
     * @param variables the variables for which the equations should be solved
     * @param maximumNumberOfResults the maximum number of results which should be returned
     * @param numericFlag
     * @param engine the evaluation engine
     * @return a &quot;list of rules list&quot; which solves the equations, or an empty list if no
     *         solution exists, or <code>F.NIL</code> if the equations are not solvable by this
     *         algorithm.
     */
    protected IASTMutable solveEquations(IASTMutable termsEqualZeroList, IAST inequationsList,
        IAST variables, int maximumNumberOfResults, boolean numericFlag, EvalEngine engine) {
      try {
        IASTMutable list = PolynomialFunctions.solveGroebnerBasis(termsEqualZeroList, variables);
        if (list.isPresent()) {
          termsEqualZeroList = list;
        }
      } catch (JASConversionException e) {
        LOGGER.debug("Solve.solveEquations() failed", e);
      }

      // rewrite some special expressions
      for (int i = 1; i < termsEqualZeroList.size(); i++) {
        IExpr equationTerm = termsEqualZeroList.get(i);
        if (equationTerm.isPlus()) {
          IExpr eq = S.Equal.of(equationTerm, F.C0);
          if (eq.isEqual()) {
            IExpr arg1 = eq.first();
            if (arg1.isPlus2()) {
              IPair p1 = arg1.first().isSqrtExpr();
              IPair p2 = arg1.second().isSqrtExpr();
              if (p1.isPresent() && p2.isPresent()) {
                // +/- Sqrt(...) +/- Sqrt() == constant
                IExpr squared = S.Expand.of(engine, F.Sqr(arg1.second()));
                IExpr expandFirstAndSqr =
                    S.Expand.of(engine, F.Sqr(F.Subtract(eq.second(), arg1.first())));
                IExpr subtractFirstAndSqr = S.Subtract.of(engine, squared, //
                    expandFirstAndSqr);
                termsEqualZeroList.set(i, //
                    subtractFirstAndSqr);
              }
            }
          }
        }
      }

      int start = 1;
      ArrayList<ExprAnalyzer> analyzerList = new ArrayList<ExprAnalyzer>();

      return solveEquationsMultiple(termsEqualZeroList, start, inequationsList, variables,
          maximumNumberOfResults, numericFlag, analyzerList, engine);
    }

    private IASTMutable solveEquationsMultiple(IASTMutable termsEqualZeroList, int start,
        IAST inequationsList, IAST variables, int maximumNumberOfResults, boolean numericFlag,
        ArrayList<ExprAnalyzer> analyzerList, EvalEngine engine) {
      ExprAnalyzer exprAnalyzer;
      IsWrongSolveExpression IS_WRONG_SOLVE_EXPRESSION = new IsWrongSolveExpression();
      // collect linear and univariate polynomial equations:
      for (int i = start; i < termsEqualZeroList.size(); i++) {
        IExpr expr = termsEqualZeroList.get(i);
        if (expr.has(IS_WRONG_SOLVE_EXPRESSION, true)) {
          LOGGER.log(engine.getLogLevel(), "Solve: the system contains the wrong object: {}",
              IS_WRONG_SOLVE_EXPRESSION.getWrongExpr());
          throw new NoEvalException();
        }
        exprAnalyzer = new ExprAnalyzer(expr, variables, isGenerateConditions(), engine);
        IExpr rewrittenNumerator = exprAnalyzer.rewriteNumerator();
        if (rewrittenNumerator.isPresent()) {
          if (rewrittenNumerator.isList()) {
            IAST list = (IAST) rewrittenNumerator;
            IASTAppendable result = F.ListAlloc(list.argSize());
            for (int j = 1; j < list.size(); j++) {
              IASTMutable copy = termsEqualZeroList.copy();
              ArrayList<ExprAnalyzer> analyzersCopy =
                  (ArrayList<ExprAnalyzer>) analyzerList.clone();
              copy.set(i, list.get(j));
              IASTMutable solveEquationsMultiple = solveEquationsMultiple(copy, i, inequationsList,
                  variables, maximumNumberOfResults, numericFlag, analyzersCopy, engine);
              if (solveEquationsMultiple.isPresent()) {
                result.appendArgs(solveEquationsMultiple);
              }
            }
            if (result.size() > 1) {
              return result;
            }
            return F.NIL;
          }
        }
        exprAnalyzer.exprAnalyze(rewrittenNumerator);
        analyzerList.add(exprAnalyzer);
      }
      IASTAppendable matrix = F.ListAlloc();
      IASTAppendable vector = F.ListAlloc();
      try {
        IASTAppendable resultList = F.ListAlloc();
        resultList = analyzeSublistRecursive(analyzerList, variables, resultList,
            maximumNumberOfResults, matrix, vector, numericFlag, engine);
        if (vector.size() > 1) {
          return solveRowReducedMatrix(matrix, vector, variables, inequationsList,
              this.intervalInequations, F.NIL, resultList, engine);
        }
        return solveInequations(resultList, inequationsList, variables, this.intervalInequations,
            engine);
        // return sortASTArguments(resultList);
      } catch (NoSolution e) {
        if (e.getType() == NoSolution.WRONG_SOLUTION) {
          return F.ListAlloc();
        }
        return F.NIL;
      }
    }

    /**
     * Solve a linear equation <code>matrix.x == vector</code>.
     * 
     * @param matrix
     * @param vector
     * @param variables
     * @param inequationsList a list of inequations; maybe {@link F#NIL}
     * @param additionalRule an additional rule which will be appended to the result; maybe
     *        {@link F#NIL}
     * @param resultList
     * @param engine
     * @return {@link F#NIL} if no solution was found
     */
    private IASTAppendable solveRowReducedMatrix(IASTAppendable matrix, IASTAppendable vector,
        IAST variables, IAST inequationsList, IAST intervalInequations, IAST additionalRule,
        IASTAppendable resultList, EvalEngine engine) {
      FieldMatrix<IExpr> augmentedMatrix = Convert.list2Matrix(matrix, vector);
      if (augmentedMatrix != null) {
        IASTAppendable subSolutionList = LinearAlgebra.rowReduced2RulesList(augmentedMatrix,
            variables, additionalRule, resultList, engine);
        if (inequationsList.isPresent() || !intervalDataMap.isEmpty()) {
          return solveInequations(subSolutionList, inequationsList, variables, intervalInequations,
              engine);
        }
        return subSolutionList;
      }
      return F.NIL;
    }

    protected IASTAppendable solveInequations(IASTMutable subSolutionList, IAST inequationsList,
        IAST variables, IAST intervalInequations, EvalEngine engine) {
      if (inequationsList.isEmpty() && intervalDataMap.isEmpty()) {
        return (IASTAppendable) QuarticSolver.sortASTArguments(subSolutionList);
      }
      if (subSolutionList.isListOfLists()) {
        final boolean[] isNumeric = new boolean[] {false};
        IASTAppendable resultList = F.ListAlloc();
        for (int i = 1; i < subSolutionList.size(); i++) {
          IExpr t = filterSingleSolution((IAST) subSolutionList.get(i), inequationsList,
              intervalInequations, isNumeric, engine);
          if (t.isList()) {
            resultList.appendArgs((IAST) t);
          }
        }
        return resultList;
      }

      // TODO solve inequations here?
      return F.NIL;
    }

    private IExpr filterSingleSolution(IAST list, IAST inequationsList, IAST intervalInequations,
        final boolean[] isNumeric, EvalEngine engine) {

      // Merge the inequalities so that Conditional checks can still be processed properly.
      IASTAppendable fullInequationsList = inequationsList.copyAppendable();
      if (intervalInequations != null && !intervalInequations.isEmpty()) {
        fullInequationsList.appendArgs(intervalInequations);
      }

      if (!intervalDataMap.isEmpty()) {
        return filterSingleSolutionValue(F.CEmptyList, list, isNumeric, engine);
      }
      if (!fullInequationsList.isEmpty()) {
        IExpr temp = F.subst(fullInequationsList, list);
        temp = engine.evalQuiet(temp);
        if (temp.isAST()) {
          return filterSingleSolutionValue(temp, list, isNumeric, engine);
        }
      }
      return F.NIL;
    }

    private IExpr filterSingleSolutionValue(IExpr temp, IAST list, final boolean[] isNumeric,
        EvalEngine engine) {
      IASTMutable[] lists = SolveUtils.filterSolveLists((IAST) temp, list, isNumeric);
      if (lists[2].isPresent() && intervalDataMap.isEmpty()) {
        if (!lists[2].isEmptyList()) {
          return lists[2];
        }
      } else {
        if (lists[1].argSize() > 0 || !intervalDataMap.isEmpty()) {
          IASTAppendable resultList = F.ListAlloc();
          IASTAppendable singleList = F.ListAlloc();
          if (filterSingleSolutionRecursive(list, 1, singleList, resultList, engine)) {
            return resultList;
          }
        }
      }
      return F.NIL;
    }

    private boolean filterSingleSolutionRecursive(IAST oldResultList, int oldIndex,
        IASTAppendable subResultList, IASTAppendable newResultList, EvalEngine engine) {
      if (oldIndex >= oldResultList.size()) {
        newResultList.append(subResultList);
        return true;
      }
      IAST rule = (IAST) oldResultList.get(oldIndex);
      IExpr variable = rule.arg1();
      IAST intervalData = intervalDataMap.get(variable);
      if (intervalData == null) {
        subResultList.append(rule);
        return filterSingleSolutionRecursive(oldResultList, oldIndex + 1, subResultList,
            newResultList, engine);
      } else {
        IExpr val = rule.arg2();
        if (!val.isConditionalExpression()) {
          IExpr eval = engine.evaluate(F.IntervalMemberQ(intervalData, val));
          if (eval.isTrue()) {
            subResultList.append(rule);
            return filterSingleSolutionRecursive(oldResultList, oldIndex + 1, subResultList,
                newResultList, engine);
          } else if (eval.isFalse()) {
            return false;
          }
        } else {
          IAST condExpr = (IAST) val;
          IExpr condition = condExpr.arg2();
          if (condition.isAST(S.Element, 3) && condition.second().equals(S.Integers)) {
            return filterIntegerIntervalRecursive(oldResultList, oldIndex, intervalData,
                subResultList, newResultList, rule, val, engine);
          }
        }
      }

      return true;
    }

    private boolean filterIntegerIntervalRecursive(IAST oldResultList, int oldIndex,
        IAST intervalData, IASTAppendable subResultList, IASTAppendable newResultList, IAST rule,
        IExpr val, EvalEngine engine) {
      for (int j = 1; j < intervalData.size(); j++) {
        if (!intervalData.get(j).isList4()) {
          return false;
        }
        IAST interval = (IAST) intervalData.get(j);
        IExpr min = interval.arg1();
        IBuiltInSymbol minSymbol = (IBuiltInSymbol) interval.arg2();
        IBuiltInSymbol maxSymbol = (IBuiltInSymbol) interval.arg3();
        IExpr max = interval.arg4();
        if (min.isNumericFunction() && max.isNumericFunction()) {
          try {
            IASTAppendable collector = F.ListAlloc();
            SolveUtils.collectConstants(val, min, max, minSymbol, maxSymbol, collector, engine);
            if (collector.size() > 1) {
              for (int k = 1; k < collector.size(); k++) {
                IExpr element = collector.get(k);
                if (!element.isList()) {
                  IASTAppendable newSingleList = subResultList.copyAppendable();
                  newSingleList.append(F.Rule(rule.first(), element));
                  if (!filterSingleSolutionRecursive(oldResultList, oldIndex + 1, newSingleList,
                      newResultList, engine)) {
                    continue;
                  }
                } else if (element.isList()) {
                  IAST elementList = (IAST) element;
                  for (int l = 1; l < elementList.size(); l++) {
                    IASTAppendable newSingleList = subResultList.copyAppendable();
                    newSingleList.append(F.Rule(rule.first(), elementList.get(l)));
                    if (!filterSingleSolutionRecursive(oldResultList, oldIndex + 1, newSingleList,
                        newResultList, engine)) {
                      continue;
                    }
                  }
                }
              }
            }
          } catch (RuntimeException rex) {
            if (Config.SHOW_STACKTRACE) {
              rex.printStackTrace();
            }
            return false;
          }
        }
      }
      return true;
    }

    /**
     * Analyze the <code>termsEqualZeroList</code> if it contains a <code>Times[..., ,...]</code>
     * expression. If true, set the factors equal to <code>0</code> and solve the equations
     * recursively.
     *
     * @param termsEqualZero the list of expressions, which should equal <code>0</code>
     * @param numericFlag
     * @param variables the variables for which the equations should be solved
     * @param engine the evaluation engine
     * @return
     */
    private IASTMutable solveTimesEquationsRecursively(IASTMutable termsEqualZero,
        IAST inequationsList, boolean numericFlag, IAST variables, boolean multipleValues,
        EvalEngine engine) {
      IASTMutable originalTermsEqualZero = termsEqualZero.copy();
      try {
        IASTMutable resultList =
            solveEquations(termsEqualZero, inequationsList, variables, 0, numericFlag, engine);
        if (resultList.isPresent() && !resultList.isEmpty()) {
          return resultList;
        }
        Set<IExpr> subSolutionSet = new TreeSet<IExpr>();
        for (int i = 1; i < termsEqualZero.size(); i++) {
          IExpr termEQZero = termsEqualZero.get(i);
          if (termEQZero.isTimes()) {
            solveTimesAST((IAST) termEQZero, termsEqualZero, inequationsList, numericFlag,
                variables, multipleValues, subSolutionSet, i, engine);
          } else {
            if (termEQZero.isAST()) {
              // try factoring
              if (variables.argSize() == 1) {
                IExpr variable = variables.arg1();
                if (!termEQZero.isFree(variable)) {
                  IExpr temp = Algebra.Factor.evaluateSolve(termEQZero, this, engine);
                  if (temp.isList()) {
                    IAST listOfValues = (IAST) temp;
                    IASTAppendable listOfLists = F.ListAlloc(listOfValues.argSize());
                    listOfValues.forEach(x -> listOfLists.append(F.List(F.Rule(variable, x))));
                    solveInequations(listOfLists, inequationsList, variables,
                        this.intervalInequations, engine).forEach(x -> subSolutionSet.add(x));
                    continue;
                  }
                }
              }
              if (termEQZero.leafCount() < Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT / 2) {
                termEQZero = S.Factor.of(engine, termEQZero);
                if (termEQZero.isTimes()) {
                  solveTimesAST((IAST) termEQZero, termsEqualZero, inequationsList, numericFlag,
                      variables, multipleValues, subSolutionSet, i, engine);
                }
              }
            }
          }
        }
        if (subSolutionSet.size() > 0) {
          return crossChecking(originalTermsEqualZero, subSolutionSet, engine);
        }
        return resultList;
      } catch (LimitException le) {
        LOGGER.debug("Solve.solveTimesEquationsRecursively() failed", le);
        throw le;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.debug("Solve.solveTimesEquationsRecursively() failed", rex);
        if (Config.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
      }
      return F.NIL;
    }

    /**
     * After finding a possible solution, the process of cross-checking involves substituting the
     * values of the variables into each equation in the system and checking to see if both sides of
     * each equation are equal.
     * 
     * @param termsEqualZero terms which should be equal to <code>0</code>
     * @param subSolutionSet a set of rules which should solve the terms
     * @param engine
     * @return
     */
    private static IASTMutable crossChecking(IASTMutable termsEqualZero, Set<IExpr> subSolutionSet,
        EvalEngine engine) {
      IASTAppendable result = F.ListAlloc(subSolutionSet);
      return crossChecking(termsEqualZero, result, engine);
    }

    /**
     * After finding a possible solution, the process of cross-checking involves substituting the
     * values of the variables into each equation in the system and checking to see if both sides of
     * each equation are equal.
     * 
     * @param termsEqualZero terms which should be equal to <code>0</code>
     * @param engine
     * @param result list of result values which should be cross checked
     * @return
     */
    private static IASTMutable crossChecking(IASTMutable termsEqualZero, IASTMutable result,
        EvalEngine engine) {
      int[] removedPositions = new int[result.size()];
      int untilPosition = 0;
      for (int j = 1; j < result.size(); j++) {
        IExpr expr = result.get(j);
        if (expr.isListOfLists()) {
          IASTMutable list = (IASTMutable) expr;
          for (int i = 1; i < list.size(); i++) {
            IASTMutable subList = ((IAST) list.get(i)).copy();
            IASTMutable crossChecked = crossChecking(termsEqualZero, subList, engine);
            if (crossChecked.isEmptyList()) {
              list.set(i, S.Nothing);
            } else {
              list.set(i, crossChecked);
            }
          }
          continue;
        }
        // if (expr.isFree(S.ConditionalExpression, true)) {
        // TODO cross checking for ConditionalExpression
        for (int i = 1; i < termsEqualZero.size(); i++) {
          IExpr termEQZero = termsEqualZero.get(i);
          IExpr replaceAll = termEQZero.replaceAll((IAST) expr);
          if (replaceAll.isNumericFunction()) {
            IExpr possibleZero = engine.evaluate(replaceAll);
            if (possibleZero.isNumber()) {
              if (!((INumber) possibleZero).isZero(Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
                removedPositions[untilPosition++] = j;
                break;
              }
            } else {
              if (possibleZero.isIndeterminate()) {
                removedPositions[untilPosition++] = j;
                break;
              }

              if (!replaceAll.isPossibleZero(true, Config.DEFAULT_ROOTS_CHOP_DELTA)) {
                // if (!engine.evalTrue(F.PossibleZeroQ(replaceAll))) {
                removedPositions[untilPosition++] = j;
                break;
              }
            }
          }
        }
      }
      if (untilPosition > 0) {
        return result.removePositionsAtCopy(removedPositions, untilPosition);
      }

      return result;
    }

    /**
     * Analyze the <code>Time(..., ...)</code> expression in the given list of equations. If the
     * expression is of the form <code>Times(..., ...) == 0</code>, set each factor equal to
     * <code>0</code> and solve the resulting equations recursively.
     * 
     * @param times the <code>Times(..., ...)</code> expression
     * @param termsEqualZeroList the list of expressions, which should equal <code>0</code>
     * @param inequationsList a list of inequality constraints
     * @param numericFlag if <code>true</code>, try to find a numeric solution
     * @param variables the variables for which the equations should be solved
     * @param multipleValues if <code>true</code> multiple results are returned as list of values
     * @param subSolutionSet a set of rules which should solve the terms
     * @param i the index of the current equation in the list
     * @param engine the evaluation engine
     */
    private void solveTimesAST(IAST times, IAST termsEqualZeroList, IAST inequationsList,
        boolean numericFlag, IAST variables, boolean multipleValues, Set<IExpr> subSolutionSet,
        int i, EvalEngine engine) {
      IAST temp;
      for (int j = 1; j < times.size(); j++) {
        if (!times.get(j).isFree(Predicates.in(variables), true)) {
          // try to get a solution from this Times() factor
          IASTMutable clonedEqualZeroList = termsEqualZeroList.setAtCopy(i, times.get(j));
          temp = solveEquations(clonedEqualZeroList, inequationsList, variables, 0, numericFlag,
              engine);
          if (temp.size() > 1) {
            for (int k = 1; k < temp.size(); k++) {
              IExpr solution = temp.get(k);
              IExpr replaceAll = engine.evalQuiet(F.ReplaceAll(times, solution));
              IExpr zeroCrossCheck = engine.evalN(replaceAll);
              if (zeroCrossCheck.isZero()) {
                subSolutionSet.add(solution);
              } else {
                if (replaceAll.isPlusTimesPower() && //
                    S.PossibleZeroQ.ofQ(engine, replaceAll)) {
                  subSolutionSet.add(solution);
                } else if (!solution.isFree(S.ConditionalExpression)) {
                  // TODO create some cross-check for C(1),... variables?
                  subSolutionSet.add(solution);
                }
              }
            }
          } else {

            if (clonedEqualZeroList.size() == 2 && variables.size() == 2) {
              IExpr firstVariable = variables.arg1();
              IExpr res = eliminateOneVariable(clonedEqualZeroList, firstVariable, multipleValues,
                  numericFlag, engine);
              if (res.isNIL()) {
                if (numericFlag) {
                  // find numerically with start value 0
                  res = S.FindRoot.ofNIL(engine, clonedEqualZeroList.arg1(),
                      F.list(firstVariable, F.C0));
                }
              }
              if (!res.isList()
                  || !res.isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(), true)) {
                continue;
              }
              IAST subResult = (IAST) res;
              for (int k = 1; k < subResult.size(); k++) {
                subSolutionSet.add(solveNumeric(subResult.get(i), numericFlag, engine));
              }
            }
          }
        }
      }
    }

    /**
     * @param ast the <code>Solve(...)</code> ast
     * @param numeric if true, try to find a numerically solution
     * @param engine
     */
    public IExpr of(final IAST ast, final boolean numeric, EvalEngine engine) {
      boolean[] isNumeric = new boolean[] {numeric};
      int maxRoots = options[1].toIntDefault();
      if (maxRoots < 1) {
        if (options[1].isInfinity()) {
          maxRoots = Integer.MAX_VALUE;
        } else if (options[1].isAutomatic()) {
          maxRoots = 1000;
        } else {
          // The value `1` of the `2` options is not a positive integer, Infinity or Automatic
          return Errors.printMessage(S.NSolve, "maxrts", F.List(options[1]));
        }
      }
      try {
        if (ast.arg1().isEmptyList()) {
          return F.list(F.CEmptyList);
        }
        IAST equationVariables = VariablesSet.getVariables(ast.arg1());
        IAST variables = F.NIL;
        if (ast.argSize() > 1) {
          variables = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        } else {
          variables = equationVariables;
        }
        ISymbol domain = S.Complexes;
        if (ast.isAST3()) {
          if (!ast.arg3().isSymbol()) {
            // Warning: `1` is not a valid domain specification.
            Errors.printMessage(ast.topHead(), "bdomv", F.List(ast.arg3()), engine);
          } else {
            domain = (ISymbol) ast.arg3();
            if (domain == S.Booleans) {
              return BooleanFunctions.solveInstances(ast.arg1(), variables, maxRoots);
            }
            if (domain == S.Integers || domain == S.Primes) {
              IExpr integersResult =
                  solveIntegers(ast, equationVariables, variables, maxRoots, domain, engine);
              if (domain == S.Primes) {
                return checkDomain(integersResult, domain, maxRoots);
              }
              return integersResult;
            }

            if (domain != S.Reals && domain != S.Complexes) {
              // Warning: `1` is not a valid domain specification.
              Errors.printMessage(ast.topHead(), "bdomv", F.List(ast.arg3()), engine);
            }
          }

        }

        IAssumptions oldAssumptions = engine.getAssumptions();
        try {
          IAssumptions assum = setVariablesReals(variables, domain);
          if (assum != null) {
            engine.setAssumptions(assum);
          }
          IAST termsList = Validate.checkEquationsAndInequations(ast, 1);
          IASTMutable[] lists = SolveUtils.filterSolveLists(termsList, F.NIL, isNumeric);

          // Early extraction of IntervalData
          IASTMutable termsEqualZeroList = lists[0];
          IASTMutable inequationsList = lists[1];
          if ((domain == S.Reals || domain == S.Complexes) && inequationsList.argSize() > 0) {
            IASTAppendable remainingInequations = inequationsList.copyAppendable();
            IntervalDataSym.extractIntervalData(remainingInequations, variables, intervalDataMap,
                engine, true);
            lists[1] = remainingInequations;
            inequationsList = remainingInequations;
          }

          if (lists[1].argSize() > 0 && lists[1].isList()) {
            IExpr evaluate = engine.evaluate(F.Reduce(inequationsList, equationVariables));
            if (evaluate.isFalse()) {
              return F.CEmptyList;
            }
          }
          boolean numericFlag = isNumeric[0] || numeric;
          if (lists[2].isPresent()) {
            IExpr result = solveNumeric(lists[2], numericFlag, engine);
            if (result.isNIL()) {
              // The system cannot be solved with the methods available to Solve.
              return Errors.printMessage(ast.topHead(), "nsmet", F.list(ast.topHead()), engine);
            }
            return checkDomain(result, domain, maxRoots);
          }

          IExpr result =
              solveRecursive(termsEqualZeroList, lists[1], numericFlag, variables, engine);
          if (result.isNIL()) {
            // The system cannot be solved with the methods available to Solve.
            return Errors.printMessage(ast.topHead(), "nsmet", F.list(ast.topHead()), engine);
          }
          return checkDomain(result, domain, maxRoots);
        } finally {
          engine.setAssumptions(oldAssumptions);
        }


      } catch (ValidateException ve) {
        return Errors.printMessage(S.Solve, ve, engine);
      } catch (LimitException e) {
        LOGGER.log(engine.getLogLevel(), S.Solve, e);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.debug("Solve.of() failed() failed", rex);
      }
      return F.NIL;
    }

  }

  private static boolean chocoSolver(IExpr x) {
    return x.isPower() && (!x.second().isInteger() || x.second().greaterEqualThan(3).isTrue());
  }

  /**
   * Check if all rules in the list return a real result.
   *
   * @param listOfRules a list of rules <code>Rule(variable, value)</code>
   * @return
   */
  private static boolean isComplex(IExpr listOfRules) {
    if (listOfRules.isListOfRules(false)) {
      return listOfRules.exists(x -> !x.second().isRealResult());
    }
    return false;
  }

  private static boolean isPrime(IExpr listOfRules) {
    if (listOfRules.isListOfRules(false)) {
      return listOfRules.exists(x -> !x.second().isInteger() //
          || !((IInteger) x.second()).isProbablePrime());
    }
    return false;
  }


  /**
   * Check if all solutions are in the given domain (currently only {@link S#Reals} and
   * {@link S#Primes} are checked).
   *
   * @param expr
   * @param domain
   * @return
   */
  private static IExpr checkDomain(IExpr expr, ISymbol domain, int maxRoots) {
    if (expr.isListOfRules() && expr.argSize() > 0) {
      expr = F.list(expr);
    }
    IExpr result = expr;
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (domain.equals(S.Reals)) {
        result = checkDomain(list, result, Solve::isComplex);
      } else if (domain.equals(S.Primes)) {
        result = checkDomain(list, result, Solve::isPrime);
      }
    }
    if (result.isListOfLists() && maxRoots < result.argSize()) {
      return ((IAST) expr).subList(1, maxRoots + 1);
    }
    return result;
  }

  /**
   * Check if all solutions in the list or "list of lists" satisfy the given predicate. If not,
   * return an empty list.
   * 
   * @param list
   * @param result
   * @param predicate
   * @return
   */
  private static IExpr checkDomain(IAST list, IExpr result, Predicate<IExpr> predicate) {
    if (list.isListOfLists()) {
      result = F.mapList(list, x -> {
        final IAST listOfRules = (IAST) x;
        if (!predicate.test(listOfRules)) {
          return listOfRules;
        }
        return F.NIL;
      });
    } else {
      if (!predicate.test((list))) {
        result = list;
      } else {
        return F.CEmptyList;
      }
    }
    return result;
  }

  /**
   * Solve the given equations and inequations for {@link S#Integers} or {@link S#Primes} domains.
   * 
   * @param ast
   * @param equationVariables
   * @param userDefinedVariables
   * @param maximumNumberOfResults
   * @param domain {@link S#Integers} or {@link S#Primes}
   * @param engine
   * @return
   */
  public static IExpr solveIntegers(final IAST ast, IAST equationVariables,
      IAST userDefinedVariables, int maximumNumberOfResults, ISymbol domain, EvalEngine engine) {
    if (!userDefinedVariables.isEmpty()) {
      IAST equationsAndInequations = Validate.checkEquationsAndInequations(ast, 1);
      if (equationsAndInequations.isEmpty()) {
        return F.NIL;
      }
      try {
        // for model#table() method
        HybridTuples hybridTuples = null;
        IExpr[] hybridVars = null;
        // Create a constraint network
        if (ToggleFeature.SOLVE_DIOPHANTINE) {
          if (equationsAndInequations.argSize() == 1) {
            IExpr eq1 = equationsAndInequations.arg1();
            if (eq1.isEqual() && eq1.second().isZero() && equationVariables.argSize() == 2) {
              IAST diophantineResult = NumberTheory.diophantinePolynomial(eq1.first(),
                  equationVariables, maximumNumberOfResults);
              if (diophantineResult.isPresent()) {
                if (equationsAndInequations.argSize() > 1) {
                  hybridVars = new IExpr[] {F.NIL, F.NIL};
                  hybridTuples = ChocoConvert.listOfRulesToTuples(diophantineResult, ast.topHead(),
                      hybridVars, engine);
                } else {
                  return diophantineResult;
                }
              }
            }
          }
        }

        if (equationsAndInequations.isFreeAST(x -> chocoSolver(x))) {
          // choco-solver doesn't handle Power() expressions very well at the moment!
          try {
            // LOGGER.debug("Choco solver");
            IAST resultList = ChocoConvert.integerSolve(equationsAndInequations, equationVariables,
                userDefinedVariables, maximumNumberOfResults, hybridVars, hybridTuples, domain,
                engine);
            if (resultList.isPresent()) {
              EvalAttributes.sort((IASTMutable) resultList);
              return resultList;
            }
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            // try 2nd solver
            // if (Config.SHOW_STACKTRACE) {
            rex.printStackTrace();
            // }
          }
        } else {
          // call cream solver
          LOGGER.debug("Cream solver");
          CreamConvert converter = new CreamConvert();
          IAST resultList = converter.integerSolve(equationsAndInequations, equationVariables,
              userDefinedVariables, maximumNumberOfResults, engine);
          if (resultList.isPresent()) {
            EvalAttributes.sort((IASTMutable) resultList);
            return resultList;
          }
        }
      } catch (LimitException le) {
        LOGGER.debug("Solve.of() failed", le);
        throw le;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.log(engine.getLogLevel(), "Integers solution not found", rex);
        return F.NIL;
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    boolean isNumericArgument = !ast.arg1().isFree(x -> x.isInexactNumber(), false);
    if (argSize > 0 && argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }
    SolveData sd = new SolveData(options);
    return sd.of(ast, isNumericArgument, engine);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_3;
  }

  /**
   * If <code>domain</code> is {@link S#Reals} create the {@link F#Element(IExpr, Reals)} assumption
   * for each variable.
   * 
   * @param userDefinedVariables
   * @param domain
   * @return <code>null</code> if no assumption was created
   */
  private static IAssumptions setVariablesReals(IAST userDefinedVariables, ISymbol domain) {
    if (domain.equals(S.Reals)) {
      return Assumptions.getInstance(F.mapList(userDefinedVariables, t -> F.Element(t, domain)));
    }
    return null;
  }

  private static IExpr[] defaultOptionValues() {
    return new IExpr[] {S.True, F.C1000};
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.GenerateConditions, S.MaxRoots};
    IExpr[] optionValues = defaultOptionValues();
    setOptions(newSymbol, optionKeys, optionValues);
  }
}
