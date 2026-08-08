package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
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
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.PolynomialHomogenization;
import org.matheclipse.core.polynomials.QuarticSolver;
import org.matheclipse.parser.client.ParserConfig;

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
    final IExpr modulus;
    final Map<IExpr, IAST> intervalDataMap;
    final IASTAppendable intervalInequations;

    public SolveData() {
      this(defaultOptionValues(), F.C0);
    }

    /**
     * Wrap the options of a caller which doesn't define the {@link S#Modulus} option, for example
     * {@link NSolve} or {@link FindInstance}.
     */
    public SolveData(IExpr[] options) {
      this(options, F.C0);
    }

    /**
     * @param options the option values of the caller, at least
     *        <code>{GenerateConditions, MaxRoots}</code>
     * @param modulus the value of the {@link S#Modulus} option or <code>0</code> if the caller
     *        doesn't define that option
     */
    public SolveData(IExpr[] options, IExpr modulus) {
      this.options = options;
      this.modulus = modulus;
      this.intervalDataMap = new HashMap<IExpr, IAST>();
      this.intervalInequations = F.ListAlloc();
    }

    /**
     * Get the value for the option {@link S#GenerateConditions} * @return
     */
    protected IExpr generateConditions() {
      return options[0];
    }

    public boolean isGenerateConditions() {
      return options[0].isTrue();
    }

    /**
     * Get the value for the option {@link S#Modulus}. Callers which don't define the
     * {@link S#Modulus} option (for example {@link NSolve} or {@link FindInstance}) get the default
     * value <code>0</code>, i.e. &quot;no modulus&quot;.
     *
     * @return the modulus or {@link F#C0} if no {@link S#Modulus} option is defined
     */
    protected IExpr modulus() {
      return modulus;
    }

    /**
     * Test whether the solution <code>list</code> is parametric with respect to the active
     * constraints, i.e. some solved value still depends on a constraint variable that itself isn't
     * solved (a free parameter).
     */
    private boolean isParametricSolution(IAST list, IAST inequationsList,
        IAST intervalInequations) {
      Set<IExpr> solvedVars = new TreeSet<>();
      for (int i = 1; i < list.size(); i++) {
        solvedVars.add(((IAST) list.get(i)).first());
      }
      Set<IExpr> constraintVars = new TreeSet<>(intervalDataMap.keySet());
      if (inequationsList != null && inequationsList.isPresent()) {
        addVariables(inequationsList, constraintVars);
      }
      if (intervalInequations != null && intervalInequations.isPresent()) {
        addVariables(intervalInequations, constraintVars);
      }
      if (constraintVars.isEmpty()) {
        return false;
      }
      for (int i = 1; i < list.size(); i++) {
        IExpr value = ((IAST) list.get(i)).second();
        for (IExpr cv : constraintVars) {
          if (!solvedVars.contains(cv) && !value.isFree(cv)) {
            return true;
          }
        }
      }
      return false;
    }

    private static void addVariables(IAST expr, Set<IExpr> collector) {
      IAST varList = new VariablesSet(expr).getVarList();
      for (int i = 1; i < varList.size(); i++) {
        collector.add(varList.get(i));
      }
    }

    /**
     * Build a {@link S#ConditionalExpression} solution for a constrained parametric solution. All
     * constraints (reconstructed from the interval-data map with
     * {@link IntervalDataSym#intervalToOr(IAST, IExpr)} and the remaining inequations) are
     * substituted with the solution rules, projected onto the single free variable using
     * {@link S#Reduce}, and attached as the condition of every solved value.
     *
     * @return a "list of solution lists" like <code>{{x -> ConditionalExpression(.., ..)}}</code>,
     *         {@link F#CEmptyList} if the constraints are unsatisfiable, or {@link F#NIL} if no
     *         constrained condition could be derived
     */
    private IExpr conditionalParametricSolution(IAST list, IAST inequationsList,
        IAST intervalInequations, EvalEngine engine) {
      IASTAppendable conditions = F.ListAlloc();
      for (Map.Entry<IExpr, IAST> entry : intervalDataMap.entrySet()) {
        IExpr logic = IntervalDataSym.intervalToOr(entry.getValue(), entry.getKey());
        if (logic.isPresent()) {
          conditions.append(logic);
        }
      }
      if (inequationsList != null && inequationsList.isPresent()) {
        conditions.appendArgs(inequationsList);
      }
      if (intervalInequations != null && intervalInequations.isPresent()) {
        conditions.appendArgs(intervalInequations);
      }
      if (conditions.isEmpty()) {
        return F.NIL;
      }
      IExpr condition = conditions.argSize() == 1 //
          ? conditions.arg1() //
          : conditions.setAtCopy(0, S.And);
      condition = engine.evaluate(F.subst(condition, list));

      IAST freeVars = new VariablesSet(condition).getVarList();
      if (freeVars.argSize() == 1) {
        IExpr reduced = engine.evaluate(F.Reduce(condition, freeVars.arg1()));
        if (reduced.isPresent() && reduced.isFree(S.Reduce)) {
          condition = reduced;
        }
      }
      if (condition.isFalse()) {
        return F.CEmptyList;
      }
      IASTAppendable wrapped = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IAST rule = (IAST) list.get(i);
        if (condition.isTrue()) {
          wrapped.append(rule);
        } else {
          wrapped.append(F.Rule(rule.arg1(), F.ConditionalExpression(rule.arg2(), condition)));
        }
      }
      return F.list(wrapped);
    }

    /**
     * Recursively solve the list of analyzers.
     *
     * @param analyzerList list of analyzers, which determine, if an expression has linear,
     *        polynomial or other form
     * @param variables the list of variables
     * @param resultList the list of result values as rules assigned to each variable
     * @param maximumNumberOfResults the maximum number of results in <code>resultList</code>:
     *        <code>0</code> gives all results.
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

      // 1. Intercept high-degree polynomials or nested transcendental functions via Decompose
      if (exprAnalyzer.getVariableSet().size() == 1) {
        IExpr variable = exprAnalyzer.getVariableSet().iterator().next();
        IAST decompRules =
            solveViaDecomposition(exprAnalyzer.getNumerator(), variable, numericFlag, engine);

        // If decomposition was non-trivial and succeeded, return immediately
        if (decompRules.isPresent()) {
          return decompRules;
        }
      }

      // 2. Fallback to standard linear/polynomial or numeric root finding
      if (exprAnalyzer.isLinearOrPolynomial()) {
        listOfRules = rootsOfUnivariatePolynomial(exprAnalyzer, numericFlag, engine);
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

    /**
     * Solves a univariate equation f(x) == 0 by decomposing it into p1(p2(...(x))) == 0.
     * Iteratively solves from the outermost layer to the innermost layer.
     */
    private IAST solveViaDecomposition(IExpr equationLHS, IExpr x, boolean numericFlag,
        EvalEngine engine) {
      // Guard against deep mutual recursion (Solve -> Decompose -> NSolve -> Solve...)
      if (engine.getOptimizeExpressionDepth() > 3) {
        return F.NIL;
      }

      try {
        engine.incOptimizeExpressionDepth();

        // 1. Attempt to decompose the left-hand side
        IExpr decompEval = engine.evaluate(F.Decompose(equationLHS, x));
        if (!decompEval.isList()) {
          return F.NIL;
        }

        IAST decomposed = (IAST) decompEval;
        // If decomposition failed or is trivial (only 1 layer), fallback
        if (decomposed.argSize() <= 1) {
          return F.NIL;
        }

        // 2. Initialize target roots for the outermost polynomial (starts with f(x) = 0)
        IASTAppendable roots = F.ListAlloc(1);
        roots.append(F.C0);

        // 3. Process from outer (index 1) to inner (end of list)
        for (int i = 1; i < decomposed.size(); i++) {
          IExpr layer = decomposed.get(i);
          ISymbol dummyY = F.Dummy("y");

          // Sub-in the dummy variable so the solver doesn't collide with the target variable x
          IExpr layerEq = F.subst(layer, x, dummyY);
          IASTAppendable nextRoots = F.ListAlloc();

          // For each known root from the previous layer, solve: layerEq == previousRoot
          for (int j = 1; j < roots.size(); j++) {
            IExpr targetValue = roots.get(j);
            IExpr subEquation = F.Equal(layerEq, targetValue);

            // Invoke the internal solver on the reduced-degree/simplified equation layer
            IExpr solver = numericFlag ? F.Solve(subEquation, F.List(dummyY))
                : F.Solve(subEquation, F.List(dummyY));
            IExpr subSolutionsEval = engine.evaluate(solver);

            // Extract the roots from the rules (e.g. {{y -> r1}, {y -> r2}})
            if (subSolutionsEval.isListOfLists()) {
              IAST subSolutions = (IAST) subSolutionsEval;
              for (int k = 1; k < subSolutions.size(); k++) {
                IAST ruleList = (IAST) subSolutions.get(k);
                if (ruleList.argSize() >= 1 && ruleList.arg1().isRule()) {
                  nextRoots.append(ruleList.arg1().second());
                }
              }
            }
          }

          // If no roots were found at this layer, the equation has no solution on this branch
          if (nextRoots.isEmpty()) {
            return F.NIL;
          }
          // Push found roots down to act as targets for the next inner layer
          roots = nextRoots;
        }

        // 4. Format as a flat list of rules: {x -> r1, x -> r2, ...}
        // This explicitly matches the return contract expected by solveOneVariableEquation.
        IASTAppendable finalRules = F.ListAlloc(roots.size());
        for (int i = 1; i < roots.size(); i++) {
          finalRules.append(F.Rule(x, roots.get(i)));
        }

        return QuarticSolver.sortASTArguments(finalRules);

      } finally {
        engine.decOptimizeExpressionDepth();
      }
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
     * the <code>listOfRules</code> with the inverse functions. * @param listOfRules
     * 
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
     * list. * @param analyzerList
     * 
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
    private static IAST rootsOfUnivariatePolynomial(ExprAnalyzer exprAnalyzer, boolean numericFlag,
        EvalEngine engine) {
      IExpr numerator = exprAnalyzer.getNumerator();
      IExpr denominator = exprAnalyzer.getDenominator();
      // try to solve the expr for one of the variables in the symbol set
      for (IExpr variable : exprAnalyzer.getVariableSet()) {
        IAST temp =
            rootsOfUnivariatePolynomial(numerator, denominator, variable, numericFlag, engine);
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
        Comparators.EqualToComparator comparator = new Comparators.EqualToComparator(engine);
        for (IExpr variable : exprAnalyzer.getVariableSet()) {

          // heuristic to find roots: find maximum/minimum and search for roots around these values.
          // Guard against deep mutual recursion: the (N)Maximize/(N)Minimize optimizers may call
          // Solve again, which would re-enter this heuristic. Only run it at optimizer reentrancy
          // depth 0 (see EvalEngine#incOptimizeExpressionDepth()).
          if (engine.getOptimizeExpressionDepth() == 0) {
            engine.incOptimizeExpressionDepth();
            try {
              Set<IExpr> solutionSet = new TreeSet<>(comparator);
              // prefer the exact symbolic optimizers Maximize/Minimize (they may yield exact
              // roots),
              // fall back to the numeric NMaximize/NMinimize only if nothing was found.
              IExpr maximum = engine.evaluate(F.Maximize(originalExpr, variable));
              findRootsFromExtremum(maximum, originalExpr, variable, solutionSet, engine);
              IExpr minimum = engine.evaluate(F.Minimize(originalExpr, variable));
              findRootsFromExtremum(minimum, originalExpr, variable, solutionSet, engine);
              if (solutionSet.isEmpty()) {
                maximum = engine.evaluate(F.NMaximize(originalExpr, variable));
                findRootsFromExtremum(maximum, originalExpr, variable, solutionSet, engine);
                minimum = engine.evaluate(F.NMinimize(originalExpr, variable));
                findRootsFromExtremum(minimum, originalExpr, variable, solutionSet, engine);
              }
              if (!solutionSet.isEmpty()) {
                return F.ListAlloc(solutionSet);
              }
            } finally {
              engine.decOptimizeExpressionDepth();
            }
          }

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

    private static void findRootsFromExtremum(IExpr valueList, IExpr originalExpr, IExpr variable,
        Set<IExpr> solutionSet, EvalEngine engine) {
      if (valueList.isList2() && valueList.second().isList1()
          && valueList.second().first().isRule()) {
        IExpr extremumValue = valueList.first();
        IAST rule = (IAST) valueList.second().first();
        IExpr location = rule.second();
        if (location.isIndeterminate() || location.isDirectedInfinity()
            || !location.isFree(variable)) {
          return;
        }

        // exact tangent root: if the extremum value is exactly 0, the (exact, symbolic) extremum
        // location is itself an exact root of "originalExpr == 0".
        if (extremumValue.isZero()) {
          solutionSet.add(F.Rule(variable, location));
          return;
        }

        // otherwise use a real (possibly numerically evaluated) extremum location as a seed for
        // FindRoot. Symbolic locations such as Pi/2 are evaluated to a number first.
        IReal value = null;
        if (location.isReal()) {
          value = (IReal) location;
        } else {
          IExpr num = engine.evalN(location);
          if (num.isReal()) {
            value = (IReal) num;
          }
        }
        if (value != null) {
          IExpr list1Root = engine.evaluate( //
              F.FindRoot(originalExpr, //
                  F.List(variable, value.add(Config.DEFAULT_ROOTS_CHOP_DELTA))));
          if (list1Root.isList1()) {
            solutionSet.add(list1Root.first());
          }
          list1Root = engine.evaluate( //
              F.FindRoot(originalExpr, //
                  F.List(variable, value.subtract(Config.DEFAULT_ROOTS_CHOP_DELTA))));
          if (list1Root.isList1()) {
            solutionSet.add(list1Root.first());
          }
        }
      }
    }

    /**
     * Checks if the inequation is a simple bound strictly defining the given variable.
     */
    private static boolean isSimpleVarBound(IExpr ineq, IExpr var) {
      if (ineq.isAST(S.Between, 3) && ineq.first().equals(var)) {
        return true;
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
      return rootsOfUnivariatePolynomial(numerator, denominator, variable, false, engine);
    }

    /**
     * @param numericFlag if <code>true</code> the caller ({@link S#NSolve}) asked for a numerical
     *        solution. Note that this is not the same as
     *        {@link IExpr#isNumericMode()} of <code>numerator</code>: <code>NSolve</code> is
     *        regularly handed an <em>exact</em> polynomial like <code>x^5+2*x^3+x-7</code>, and
     *        without this flag the polynomial would be answered with inert {@link S#Root} objects
     *        which are only evaluated to numbers afterwards - in a different order, and running the
     *        numerical root finder once per root.
     */
    public static IAST rootsOfUnivariatePolynomial(IExpr numerator, IExpr denominator,
        IExpr variable, boolean numericFlag, EvalEngine engine) {
      IExpr temp = F.NIL;

      if (numerator.isNumericMode() && denominator.isOne()) {
        temp = RootsFunctions.complexRoots(numerator, F.list(variable), engine);
      }
      if (temp.isNIL()) {
        temp = RootsFunctions.rootsOfVariable(numerator, denominator, F.list(variable),
            numerator.isNumericMode(), !numericFlag, engine);
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

      IExpr result = F.NIL;
      if (termsEqualZeroList.size() == 2 && variables.size() == 2 && inequationsList.isEmpty()) {
        result = solveTwoVariableSystem(termsEqualZeroList, numericFlag, variables.arg1(), engine);
      } else if (termsEqualZeroList.size() > 2 && variables.size() >= 3) {
        result = solveMultiVariableSystem(termsEqualZeroList, inequationsList, numericFlag,
            variables, engine);
      }
      if (result.isPresent()) {
        return result;
      }

      // Fallback: kernel homogenization for systems in which every variable occurs only under a
      // single invertible kernel - radicals (fractional powers), trigonometric or hyperbolic
      // functions, or a mix of these. Runs last so it never disturbs the strategies above.
      if (variables.argSize() >= 2) {
        return solveViaKernelHomogenization(termsEqualZeroList, inequationsList, numericFlag,
            variables, engine);
      }
      return F.NIL;
    }

    /**
     * Solve an underdetermined system, i.e. a system with fewer equations than variables. Such a
     * system has no isolated solutions; the variables that cannot be determined stay free
     * parameters and the remaining ones are expressed in terms of them, e.g.
     * <code>Solve(x^2 - y^3 == 1, {x, y})</code> gives
     * <code>{{x -> -Sqrt(1 + y^3)}, {x -> Sqrt(1 + y^3)}}</code> with <code>y</code> free.
     *
     * <p>
     * Currently restricted to a single equation: the variable of lowest (positive) polynomial
     * degree is solved for - it yields the algebraically simplest radical - and every other
     * variable is left as a parameter. Ties are broken by the order in the variable list.
     *
     * @param termsEqualZeroList the equations as expressions which should be <code>== 0</code>
     * @param inequationsList inequality constraints; underdetermined solving is only attempted when
     *        this is empty
     * @param numericFlag if <code>true</code> evaluate the solutions numerically
     * @param variables the variables to solve for
     * @param engine the evaluation engine
     * @return a "list of solution lists", or {@link F#NIL} if the system isn't underdetermined or
     *         no solve variable could be determined
     */
    private IExpr solveUnderdetermined(IASTMutable termsEqualZeroList, IAST inequationsList,
        boolean numericFlag, IAST variables, EvalEngine engine) {
      if (termsEqualZeroList.argSize() != 1 || variables.argSize() < 2
          || !inequationsList.isEmpty()) {
        return F.NIL;
      }
      IExpr equation = termsEqualZeroList.arg1();
      // choose the variable with the lowest positive degree in the (polynomial) equation
      IExpr solveVariable = F.NIL;
      long minDegree = Long.MAX_VALUE;
      for (int i = 1; i < variables.size(); i++) {
        IExpr variable = variables.get(i);
        if (!variable.isSymbol() || equation.isFree(variable)
            || !equation.isPolynomial(F.list(variable))) {
          // a variable the equation isn't polynomial in (e.g. x in Sin(x) + y == 1) can't be
          // ranked by degree; leave it as a free parameter and try the remaining ones
          continue;
        }
        long degree = S.Exponent.of(engine, equation, variable).toLongDefault();
        // "<=" so that on equal degree the LAST such variable wins: the earlier variables stay
        // the free parameters, e.g. Solve({x*y == 1}, {x, y}) -> {{y -> 1/x}}
        if (degree > 0 && degree <= minDegree) {
          minDegree = degree;
          solveVariable = variable;
        }
      }
      if (solveVariable.isNIL()) {
        return F.NIL;
      }
      return solveRecursive(termsEqualZeroList, inequationsList, numericFlag,
          F.list(solveVariable), engine);
    }

    private IExpr solveMultiVariableSystem(IASTMutable termsEqualZeroList, IAST inequationsList,
        boolean numericFlag, final IAST vars, EvalEngine engine) {
      // expensive recursion try
      IExpr firstEquation = termsEqualZeroList.arg1();

      IASTMutable reducedEqualZeroList = termsEqualZeroList.copyAppendable();
      for (int i = 1; i < vars.size(); i++) {
        IExpr variable = vars.get(i);

        IAST[] reduced = Eliminate.eliminateOneVariable(F.list(F.Equal(firstEquation, F.C0)),
            variable, true, false, engine);
        if (reduced != null) {
          // oneVariableRule = ( firstVariable -> reducedExpression )
          final IAST oneVariableRule = reduced[1];
          // Eliminate.eliminateOneVariable() returns a *list* of rules when the variable has
          // several branches (e.g. x -> +/-Sqrt(y) from x^2 - y == 0). Substituting such a
          // multi-branch elimination as if it were a single ( variable -> value ) rule both drops
          // solution branches and builds a malformed nested rule ( variable -> ( variable ->
          // value ) ), so skip it (before removing the first equation) and prefer a different
          // variable whose elimination yields a single rule (e.g. y -> x^2 from the same
          // equation). A pure polynomial system always has such a variable; anything else is left
          // to the fall-through strategies (solveViaKernelHomogenization, ...).
          if (oneVariableRule != null && oneVariableRule.isListOfRules(false)) {
            continue;
          }
          final IAST variables = vars.splice(i);
          reducedEqualZeroList = reducedEqualZeroList.removeAtCopy(1);
          IExpr replaced = reducedEqualZeroList.replaceAll(oneVariableRule);
          if (replaced.isList()) {
            IExpr subResult = solveRecursive((IASTMutable) replaced, inequationsList, numericFlag,
                variables, engine);
            if (subResult.isListOfLists()) {
              IExpr value = oneVariableRule.second();
              IASTMutable result = F.mapList((IAST) subResult, t -> {
                final IAST listOfRules = (IAST) t;
                IExpr replaceAllExpr = value.replaceAll(listOfRules);
                if (replaceAllExpr.isPresent()) {
                  replaceAllExpr = S.Simplify.of(engine, replaceAllExpr);
                  return listOfRules.appendClone(F.Rule(variable, replaceAllExpr));
                }
                if (value.isFree(f -> vars.contains(f), true)) {
                  return listOfRules.appendClone(F.Rule(variable, value));
                }
                return F.NIL;
              });
              return crossChecking(termsEqualZeroList, result, engine);
            } else if (subResult.isList()) { // important for NSolve
              IExpr value = oneVariableRule.second();
              replaced = value.replaceAll((IAST) subResult);
              if (replaced.isPresent()) {
                IASTAppendable result = ((IAST) subResult).copyAppendable();
                result.append(F.Rule(variable, replaced));
                return crossChecking(termsEqualZeroList, result, engine);
              }
              if (value.isFree(f -> vars.contains(f), true)) {
                IASTAppendable result = ((IAST) subResult).copyAppendable();
                result.append(F.Rule(variable, value));
                return crossChecking(termsEqualZeroList, result, engine);
              }
            }
          }
        }
      }
      return F.NIL;
    }

    /**
     * Solve a multi-variable system in which every solve variable occurs only under a single
     * invertible kernel: a radical (fractional power of the variable), or a trigonometric /
     * hyperbolic function of the bare variable, or a mix of these. Examples:
     * <code>{3*Sqrt(x)+2*Sqrt(y)==16, 2*Sqrt(x)-3*Sqrt(y)==-11}</code>,
     * <code>{3*Sin(x)+2*Sin(y)==7/2, 2*Sin(x)-3*Sin(y)==-2}</code>,
     * <code>{3*Sqrt(x)+2*Sin(y)==7, 2*Sqrt(x)-2*Sin(y)==3}</code>.
     *
     * <p>
     * Reuses {@link PolynomialHomogenization}: a single shared instance substitutes each kernel
     * sub-expression with a fresh dummy variable (consistently across all equations), yielding a
     * polynomial system in the dummies. That system is solved for the kernel <em>values</em>. Each
     * kernel value then produces a decoupled equation <code>kernel(variable) == value</code> (via
     * {@link PolynomialHomogenization#replaceBackward(IExpr)}); handing the decoupled system back to
     * {@link S#Solve} lets the ordinary single-variable machinery invert every kernel - including
     * the periodic {@code ConditionalExpression} branches of the trig/hyperbolic inverses and the
     * cross-product across variables. Finally the candidates are cross-checked against the original
     * equations, which discards branches inconsistent with the principal root (e.g. a negative
     * square root).
     *
     * @param termsEqualZeroList the equations as expressions which should be <code>== 0</code>
     * @param inequationsList inequality constraints; kernel homogenization is only attempted when
     *        this is empty
     * @param numericFlag if <code>true</code> evaluate the sub-solutions numerically
     * @param variables the variables to solve for
     * @param engine the evaluation engine
     * @return a list-of-lists of solution rules, {@link F#CEmptyList} if the system is solvable but
     *         has no consistent solution, or {@link F#NIL} to fall through to the other strategies
     */
    private IExpr solveViaKernelHomogenization(IASTMutable termsEqualZeroList, IAST inequationsList,
        boolean numericFlag, IAST variables, EvalEngine engine) {
      if (variables.argSize() < 2 || !inequationsList.isEmpty()) {
        // need at least two variables; kernel homogenization ignores inequality constraints
        return F.NIL;
      }
      // All variables must be plain symbols.
      for (int i = 1; i < variables.size(); i++) {
        if (!variables.get(i).isSymbol()) {
          return F.NIL;
        }
      }
      // Cheap structural pre-check (before any homogenization): every trigonometric / hyperbolic
      // sub-expression that involves a solve variable must be a function of a *bare* solve variable
      // (e.g. Sin(x), not Sin(2*x+1) or Sin(c0/z+..)). This restricts us to invertible kernels and
      // prevents heavyweight / non-terminating trig homogenization on unrelated systems (e.g. the
      // internal systems built by RSolve / AsymptoticRSolveValue).
      for (int i = 1; i < termsEqualZeroList.size(); i++) {
        if (hasNonKernelTrig(termsEqualZeroList.get(i), variables)) {
          return F.NIL;
        }
      }
      try {
        // 1. Forward-substitute every equation with ONE shared instance so that identical kernels
        // map to the same dummy variable across all equations.
        PolynomialHomogenization homogenization = new PolynomialHomogenization(engine, false);
        IASTAppendable polyTerms = F.ListAlloc(termsEqualZeroList.argSize());
        for (int i = 1; i < termsEqualZeroList.size(); i++) {
          IExpr poly = homogenization.replaceForward(termsEqualZeroList.get(i));
          if (poly.isNIL() || !poly.isFree(v -> variables.contains(v), true)) {
            // a solve variable survived un-substituted -> not a clean kernel system
            return F.NIL;
          }
          polyTerms.append(poly);
        }

        // 2. Validate: a bijection between dummies and solve variables, where each kernel base is a
        // fractional power of a bare variable (radical) or an invertible unary trig/hyperbolic
        // function of a bare variable, and at least one kernel is non-trivial (so plain polynomial
        // systems are not intercepted here).
        Set<ISymbol> dummies = homogenization.substitutedVariablesSet();
        if (dummies.size() != variables.argSize()) {
          return F.NIL;
        }
        Map<ISymbol, IExpr> dummyToBase = homogenization.substitutedVariables();
        IASTAppendable dummyVarList = F.ListAlloc(dummies.size());
        Set<IExpr> underlyingVariables = new HashSet<IExpr>();
        boolean hasNonTrivialKernel = false;
        for (ISymbol dummy : dummies) {
          IExpr base = dummyToBase.get(dummy);
          IExpr underlying;
          if (base != null && base.isSymbol() && variables.contains(base)) {
            // radical / plain kernel: base is the variable itself, kernel = base^(1/LCM)
            underlying = base;
            if (!homogenization.getLCM(dummy).isOne()) {
              hasNonTrivialKernel = true; // genuine fractional power
            }
          } else if (base != null && base.isAST1() && isInvertibleKernelHead(base.head())
              && base.first().isSymbol() && variables.contains(base.first())) {
            // trigonometric / hyperbolic kernel g(variable)
            underlying = base.first();
            hasNonTrivialKernel = true;
          } else {
            return F.NIL;
          }
          if (!underlyingVariables.add(underlying)) {
            return F.NIL; // two kernels of the same variable (coupled) -> out of scope
          }
          dummyVarList.append(dummy);
        }
        if (!hasNonTrivialKernel || underlyingVariables.size() != variables.argSize()) {
          return F.NIL;
        }

        // 3. Solve the polynomial system for the kernel dummies. This cannot re-enter this method
        // because the dummy system contains neither radicals nor trig/hyperbolic kernels.
        IAST kernelEquations = polyTerms.mapThread(F.Equal(F.Slot1, F.C0), 1);
        IExpr kernelSolution = engine.evalQuiet(F.Solve(kernelEquations, dummyVarList));
        if (!kernelSolution.isListOfLists()) {
          return F.NIL;
        }

        // 4. For each kernel solution build the decoupled system {kernel(variable) == value} and
        // let the ordinary solver invert it (inverse-function branches / periodic families and the
        // cross-product across variables). The decoupled equations are single-variable, so this
        // does not re-enter kernel homogenization either.
        IASTAppendable results = F.ListAlloc();
        Set<IExpr> seenSolutions = new HashSet<IExpr>();
        boolean anyDecoupledPresent = false;
        for (int i = 1; i < kernelSolution.size(); i++) {
          IAST kernelRules = (IAST) kernelSolution.get(i);
          IASTAppendable decoupled = F.ListAlloc(kernelRules.size());
          boolean valid = true;
          for (int j = 1; j < kernelRules.size(); j++) {
            IExpr rule = kernelRules.get(j);
            if (!rule.isRuleAST() || !rule.first().isSymbol()) {
              valid = false;
              break;
            }
            IExpr kernelExpr = homogenization.replaceBackward(rule.first());
            if (kernelExpr.isNIL()) {
              valid = false;
              break;
            }
            decoupled.append(F.Equal(kernelExpr, rule.second()));
          }
          if (!valid || decoupled.argSize() != variables.argSize()) {
            continue;
          }
          IExpr decoupledSolution = engine.evalQuiet(F.Solve(decoupled, variables));
          if (decoupledSolution.isList()) {
            anyDecoupledPresent = true;
            if (decoupledSolution.isListOfLists()) {
              IAST decoupledList = (IAST) decoupledSolution;
              for (int k = 1; k < decoupledList.size(); k++) {
                IExpr candidate = decoupledList.get(k);
                // a degenerate branch (e.g. Sin(y)==1 -> two identical Pi/2 families) can produce
                // duplicate solutions in the cross-product; keep only distinct ones
                if (seenSolutions.add(candidate)) {
                  results.append(candidate);
                }
              }
            }
          }
        }

        if (results.argSize() > 0) {
          // Discard branches inconsistent with the original (principal-root) equations.
          IASTMutable crossChecked = crossChecking(termsEqualZeroList, results, engine);
          return solveNumeric(crossChecked, numericFlag, engine);
        }
        if (anyDecoupledPresent) {
          // homogenized and solved, but no consistent (principal-branch) solution exists
          return F.CEmptyList;
        }
        return F.NIL;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return F.NIL;
      }
    }

    /**
     * Whether {@code term} contains a trigonometric / hyperbolic sub-expression that involves a
     * solve variable but is <em>not</em> a function of a bare solve variable (e.g. {@code Sin(2*x)}
     * or {@code Sin(c0/z+..)}). Used to restrict {@link #solveViaKernelHomogenization} to invertible
     * kernels and to keep it away from heavyweight trig homogenization on unrelated systems.
     */
    private static boolean hasNonKernelTrig(IExpr term, IAST variables) {
      return !term.isFree(sub -> sub.isAST1() //
          && (sub.isTrigFunction() || sub.isHyperbolicFunction()) //
          && !sub.first().isFree(v -> variables.contains(v), true) //
          && !(sub.first().isSymbol() && variables.contains(sub.first())), true);
    }

    /**
     * Whether {@code head} is a (forward) trigonometric or hyperbolic function that
     * {@link #solveViaKernelHomogenization} knows how to invert. Inverse functions are intentionally
     * excluded.
     */
    private static boolean isInvertibleKernelHead(IExpr head) {
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.Sin:
          case ID.Cos:
          case ID.Tan:
          case ID.Cot:
          case ID.Sec:
          case ID.Csc:
          case ID.Sinh:
          case ID.Cosh:
          case ID.Tanh:
          case ID.Coth:
          case ID.Sech:
          case ID.Csch:
            return true;
          default:
            return false;
        }
      }
      return false;
    }

    private IExpr solveTwoVariableSystem(IASTMutable termsEqualZeroList, boolean numericFlag,
        IExpr firstVariable, EvalEngine engine) {
      IExpr res =
          eliminateOneVariable(termsEqualZeroList, firstVariable, true, false, numericFlag, engine);
      if (res.isNIL()) {
        if (numericFlag) {
          IExpr termEqualZero = termsEqualZeroList.arg1();
          // find numerically with start value 0
          res = engine.evalQuiet(F.FindRoot(termEqualZero, F.list(firstVariable, F.C0)));
        }
      }
      if (!res.isList() || !res.isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(), true)) {
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
     * @param periodicBranches if <code>true</code> the caller accepts periodic (multi-valued)
     *        complex solution branches to be returned as <code>ConditionalExpression</code> results
     * @param numeric evaluate in numericMode
     * @param engine
     * @return
     */
    private static IAST eliminateOneVariable(IAST termsEqualZeroList, IExpr variable,
        boolean multipleValues, boolean periodicBranches, boolean numeric, EvalEngine engine) {
      if (!termsEqualZeroList.arg1().isFree(t -> t.isIndeterminate() || t.isDirectedInfinity(),
          true)) {
        return F.NIL;
      }
      // copy the termsEqualZeroList back to a list of F.Equal(...) expressions
      // because Eliminate() operates on equations.
      IAST equalsASTList = termsEqualZeroList.mapThread(F.Equal(F.Slot1, F.C0), 1);
      IAST[] tempAST =
          Eliminate.eliminateOneVariable(equalsASTList, variable, multipleValues, periodicBranches,
              engine);
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
     * {@link F#NIL} * @param expr
     * 
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
        // LOGGER.debug("Solve.solveEquations() failed", e);
      }

      // rewrite some special expressions
      for (int i = 1; i < termsEqualZeroList.size(); i++) {
        IExpr equationTerm = termsEqualZeroList.get(i);
        if (equationTerm.isPlus()) {
          IExpr eq = S.Equal.of(engine, equationTerm, F.C0);
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
          // LOGGER.log(engine.getLogLevel(), "Solve: the system contains the wrong object: {}",
          // IS_WRONG_SOLVE_EXPRESSION.getWrongExpr());
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
     * Solve a linear equation <code>matrix.x == vector</code>. * @param matrix
     * 
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

      // Constrained parametric solutions: if a solved value still depends on a free (unsolved)
      // constraint variable, project every constraint onto the free variable(s) with Reduce and
      // return a ConditionalExpression, e.g.
      // Solve({x + y == 4, 1 <= x <= 3 && 0 <= y <= 2}, {x, y})
      // -> {{x -> ConditionalExpression(4 - y, 1 <= y <= 2)}}
      if (isParametricSolution(list, inequationsList, intervalInequations)) {
        IExpr conditional =
            conditionalParametricSolution(list, inequationsList, intervalInequations, engine);
        if (conditional.isPresent()) {
          return conditional;
        }
      }

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
            if (lists[1].argSize() > 0) {
              IExpr condition;
              if (lists[1].argSize() == 1) {
                condition = lists[1].arg1();
              } else {
                IASTAppendable andAST = F.ast(S.And);
                andAST.appendArgs(lists[1]);
                condition = andAST;
              }

              IASTAppendable newResultList = F.ListAlloc(resultList.argSize());
              for (int j = 1; j < resultList.size(); j++) {
                IAST solList = (IAST) resultList.get(j);
                IASTAppendable wrappedList = F.ListAlloc(solList.argSize());
                for (int i = 1; i < solList.size(); i++) {
                  IAST rule = (IAST) solList.get(i);
                  IExpr value = rule.arg2();
                  if (!value.isConditionalExpression()) {
                    wrappedList
                        .append(F.Rule(rule.arg1(), F.ConditionalExpression(value, condition)));
                  } else {
                    IAST condExpr = (IAST) value;
                    IExpr newCond = F.And(condExpr.arg2(), condition);
                    wrappedList.append(
                        F.Rule(rule.arg1(), F.ConditionalExpression(condExpr.arg1(), newCond)));
                  }
                }
                newResultList.append(wrappedList);
              }
              return newResultList;
            }
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
        // LOGGER.debug("Solve.solveTimesEquationsRecursively() failed", le);
        throw le;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        // LOGGER.debug("Solve.solveTimesEquationsRecursively() failed", rex);
        if (Config.SHOW_STACKTRACE) {
          rex.printStackTrace();
        }
      }
      return F.NIL;
    }

    /**
     * After finding a possible solution, the process of cross-checking involves substituting the
     * values of the variables into each equation in the system and checking to see if both sides of
     * each equation are equal. * @param termsEqualZero terms which should be equal to
     * <code>0</code>
     * 
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
     * each equation are equal. * @param termsEqualZero terms which should be equal to
     * <code>0</code>
     * 
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
     * <code>0</code> and solve the resulting equations recursively. * @param times the
     * <code>Times(..., ...)</code> expression
     * 
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
                  true, numericFlag, engine);
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
                subSolutionSet.add(solveNumeric(subResult.get(k), numericFlag, engine));
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
      return of(ast, numeric, false, engine);
    }

    /**
     * @param ast the <code>Solve(...)</code> ast
     * @param numeric if true, try to find a numerically solution
     * @param bareExpressionsAreEquations if <code>true</code> a term which isn't a relation is read
     *        as the equation <code>expr == 0</code>, as the polynomial solvers {@link S#NSolve} and
     *        {@link S#NSolveValues} do
     * @param engine the evaluation engine
     */
    public IExpr of(final IAST ast, final boolean numeric,
        final boolean bareExpressionsAreEquations, EvalEngine engine) {
      if (!bareExpressionsAreEquations && !isQuantifiedSystem(ast.arg1())) {
        // `1` is not a quantified system of equations and inequalities.
        return Errors.printMessage(ast.topHead(), "naqs", F.List(ast.arg1()), engine);
      }
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
        IAST equationVariables = VariablesSet.getAlgebraicVariables(ast.arg1(), false);
        IAST variables = F.NIL;
        if (ast.argSize() > 1 && !ast.arg2().isNIL() && !ast.arg2().isEmptyList()) {
          variables = Validate.checkIsAlgebraicVariableOrAlgebraicVariableList(ast, 2,
              ast.topHead(), engine);
        } else {
          variables = equationVariables;
        }

        IExpr modulus = modulus();
        if (!modulus.isZero() && variables.isPresent()) {
          IExpr modulusResult = solveModulus(ast, variables, modulus, engine);
          if (modulusResult.isListOfLists() && maxRoots < modulusResult.argSize()) {
            return ((IAST) modulusResult).subList(1, maxRoots + 1);
          }
          return modulusResult;
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
              // An infinite solution family may only be answered in closed form when the caller
              // didn't ask for a specific finite number of individual solutions: MaxRoots->10 is a
              // request to enumerate ten of them. S#Primes is always enumerated - the parametric
              // family is over the integers and its members aren't generally prime.
              boolean allowParametricSolution = domain == S.Integers //
                  && (options[1].isAutomatic() || options[1].isInfinity()
                      || options[1].equals(F.C1000));
              IExpr integersResult = solveIntegers(ast, equationVariables, variables, maxRoots,
                  domain, allowParametricSolution, engine);
              if (domain == S.Primes) {
                return checkDomain(integersResult, domain, maxRoots);
              }
              return integersResult;
            }

            if (domain != S.Reals && domain != S.Complexes && domain != S.Rationals) {
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
          IAST termsList =
              Validate.checkEquationsAndInequations(ast, 1, bareExpressionsAreEquations);
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

          // A pure-inequality system (no equations that could produce solution rules) is
          // decided directly: it is unsatisfiable iff the IntervalData intersection collapsed to
          // the empty set, or Reduce proves the whole condition False. In that case return the
          // empty solution set {} (consistent with Solve(False, ...) -> {}), covering numeric and
          // pure symbolic inequalities alike. Satisfiable pure-inequality systems are left
          // unevaluated on purpose.
          if (termsEqualZeroList.argSize() == 0
              && (inequationsList.argSize() > 0 || !intervalDataMap.isEmpty())) {
            for (IAST interval : intervalDataMap.values()) {
              if (IntervalDataSym.isEmptySet(interval)) {
                return F.CEmptyList;
              }
            }
            IExpr reduced = engine.evaluate(F.Reduce(ast.arg1(), equationVariables));
            if (reduced.isFalse()) {
              return F.CEmptyList;
            }
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

          if (numericFlag && inequationsList.isEmpty() && intervalDataMap.isEmpty()) {
            IExpr numericResult = solveNumericPolynomial(termsEqualZeroList, variables, domain,
                maxRoots, engine);
            if (numericResult.isPresent()) {
              return numericResult;
            }
          }

          IExpr result =
              solveRecursive(termsEqualZeroList, lists[1], numericFlag, variables, engine);
          if (result.isNIL()) {
            result = solveUnderdetermined(termsEqualZeroList, lists[1], numericFlag, variables,
                engine);
          }
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
        // LOGGER.log(engine.getLogLevel(), S.Solve, e);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        // LOGGER.debug("Solve.of() failed() failed", rex);
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
   * Check if some rule in the list has a value which isn't a rational number, i.e. whether the
   * solution has to be rejected for the {@link S#Rationals} domain. Integers and fractions are
   * rational; radicals like <code>Sqrt(2)</code> and complex values like <code>I</code> are not.
   *
   * @param listOfRules a list of rules <code>Rule(variable, value)</code>
   * @return <code>true</code> if the solution contains a non-rational value
   */
  private static boolean isNotRational(IExpr listOfRules) {
    if (listOfRules.isListOfRules(false)) {
      return listOfRules.exists(x -> !x.second().isRational());
    }
    return false;
  }


  /**
   * Check if all solutions are in the given domain (currently {@link S#Reals}, {@link S#Rationals}
   * and {@link S#Primes} are checked).
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
      } else if (domain.equals(S.Rationals)) {
        result = checkDomain(list, result, Solve::isNotRational);
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
   * Test if <code>expr</code> is a quantified system of equations and inequalities, i.e. a
   * relation, a logical combination or a list of those.
   *
   * <p>
   * {@link S#Solve} requires the relations to be written down: <code>Solve(-4+3*x+x^2, x)</code>
   * reports the message <code>naqs</code> and stays unevaluated, the equation has to be written as
   * <code>Solve(-4+3*x+x^2==0, x)</code>. Only the polynomial solvers {@link S#NSolve} and
   * {@link S#NSolveValues} read a bare expression as <code>expr == 0</code>.
   *
   * @param expr the first argument of the <code>Solve(...)</code> ast
   * @return <code>true</code> if <code>expr</code> is built from relations
   */
  private static boolean isQuantifiedSystem(IExpr expr) {
    return isQuantifiedSystem(expr, false);
  }

  /**
   * @param expr a term of the system
   * @param booleanArgument <code>true</code> if <code>expr</code> is an argument of a logical
   *        operator, where a symbol is a boolean proposition - <code>Xor(a,b,c,d) &amp;&amp; (a ||
   *        b)</code> is a system for the {@link S#Booleans} domain, a bare <code>x</code> is not
   */
  private static boolean isQuantifiedSystem(IExpr expr, boolean booleanArgument) {
    if (booleanArgument && expr.isSymbol()) {
      return true;
    }
    if (expr.isTrue() || expr.isFalse() || expr.isRelationalBinary() || expr.isEqual()
        || expr.isAST(S.Unequal) || expr.isAST(S.Inequality) || expr.isAST(S.Element)
        || expr.isAST(S.Less) || expr.isAST(S.LessEqual) || expr.isAST(S.Greater)
        || expr.isAST(S.GreaterEqual)) {
      return true;
    }
    if (expr.isAST(S.ForAll) || expr.isAST(S.Exists)) {
      // the quantified condition is the last argument
      return expr.isAST() && isQuantifiedSystem(((IAST) expr).last(), booleanArgument);
    }
    if (expr.isList()) {
      return ((IAST) expr).forAll(arg -> isQuantifiedSystem(arg, booleanArgument));
    }
    if (expr.isAnd() || expr.isOr() || expr.isNot() || expr.isAST(S.Xor) || expr.isAST(S.Nand)
        || expr.isAST(S.Nor) || expr.isAST(S.Implies) || expr.isAST(S.Equivalent)) {
      return ((IAST) expr).forAll(arg -> isQuantifiedSystem(arg, true));
    }
    return false;
  }

  /**
   * Convert the "list of solution lists" which {@link S#Solve} and {@link S#NSolve} return into the
   * "list of values" which {@link S#SolveValues} and {@link S#NSolveValues} return.
   *
   * <p>
   * The shape of the result follows the shape of the <code>variables</code> argument the caller
   * wrote down, not the number of variables: a single variable <code>x</code> gives a flat list of
   * values, a list <code>{x}</code> gives a list of one-element lists. The values are ordered by
   * the variables of the <code>variables</code> argument, which is not necessarily the order of the
   * rules in a solution - <code>Solve({x+y==2,x-y==0},{y,x})</code> returns the rules canonically
   * sorted as <code>{x-&gt;1,y-&gt;1}</code>.
   *
   * @param solveResult the result of {@link SolveData#of(IAST, boolean, EvalEngine)}
   * @param variables the variables argument of the <code>SolveValues(...)</code> ast
   * @return the list of solution values or {@link F#NIL} if <code>solveResult</code> isn't a list
   *         of solutions
   */
  public static IExpr solutionValues(IExpr solveResult, IExpr variables) {
    if (!solveResult.isList()) {
      return F.NIL;
    }
    IAST solutions = (IAST) solveResult;
    if (solutions.argSize() > 0 && solutions.isListOfRules(false)) {
      solutions = F.list(solutions);
    }
    if (variables.isList()) {
      IAST variableList = (IAST) variables;
      return F.mapList(solutions, solution -> solution.isList() //
          ? F.mapList(variableList, variable -> F.subst(variable, (IAST) solution)) //
          : F.NIL);
    }
    return F.mapList(solutions, solution -> solution.isList() //
        ? F.subst(variables, (IAST) solution) //
        : F.NIL);
  }

  /**
   * Order the solutions of a single variable by their real part and, for equal real parts, by
   * their imaginary part, the same way {@link #solveNumericPolynomial} orders the machine precision
   * solutions.
   *
   * @param solutions a "list of solution lists"
   * @return the ordered solutions or <code>solutions</code> itself if they aren't solutions of a
   *         single variable with numeric values
   */
  public static IExpr sortNumericSolutions(IExpr solutions) {
    if (!solutions.isListOfLists()) {
      return solutions;
    }
    IAST list = (IAST) solutions;
    for (int i = 1; i < list.size(); i++) {
      IAST solution = (IAST) list.get(i);
      if (solution.argSize() != 1 || !solution.arg1().isRule()
          || !solution.arg1().second().isNumber()) {
        return solutions;
      }
    }
    IASTMutable result = list.copy();
    EvalAttributes.sort(result, (IExpr o1, IExpr o2) -> {
      INumber value1 = (INumber) o1.first().second();
      INumber value2 = (INumber) o2.first().second();
      int comparison = Double.compare(value1.reDoubleValue(), value2.reDoubleValue());
      return comparison != 0 //
          ? comparison //
          : Double.compare(value1.imDoubleValue(), value2.imDoubleValue());
    });
    return result;
  }

  /**
   * Determine the number of significant digits which {@link S#NSolve} and {@link S#NSolveValues}
   * should compute with, either from the optional fourth argument
   * <code>NSolve(equations, vars, domain, precision)</code> or from the {@link S#WorkingPrecision}
   * option.
   *
   * @param ast the <code>NSolve(...)</code> ast
   * @param option the value of the {@link S#WorkingPrecision} option
   * @param engine the evaluation engine
   * @return the number of significant digits, {@link #MACHINE_PRECISION_REQUESTED} for machine
   *         precision or {@link #INVALID_PRECISION} if the requested precision isn't a positive
   *         integer
   */
  public static long workingPrecision(IAST ast, IExpr option, EvalEngine engine) {
    IExpr precisionExpr = F.NIL;
    if (ast.size() == 5) {
      precisionExpr = ast.arg4();
    } else if (option.isPresent() && !option.isAutomatic()) {
      precisionExpr = option;
    }
    if (precisionExpr.isNIL() || precisionExpr.isAutomatic()
        || precisionExpr == S.MachinePrecision) {
      return MACHINE_PRECISION_REQUESTED;
    }
    int precision = precisionExpr.toIntDefault();
    if (precision < 1) {
      // Requested precision `1` is smaller than `2`.
      Errors.printMessage(ast.topHead(), "precsm", F.List(precisionExpr, F.C1), engine);
      return INVALID_PRECISION;
    }
    return precision <= ParserConfig.MACHINE_PRECISION //
        ? MACHINE_PRECISION_REQUESTED //
        : precision;
  }

  /** {@link #workingPrecision(IAST, IExpr, EvalEngine)}: compute with machine numbers. */
  public static final long MACHINE_PRECISION_REQUESTED = -1L;

  /** {@link #workingPrecision(IAST, IExpr, EvalEngine)}: the requested precision is invalid. */
  public static final long INVALID_PRECISION = 0L;

  /**
   * The largest distance between a numerically determined root and a rational number which still
   * lets the root be tested as a member of the {@link S#Rationals} domain.
   */
  private static final double RATIONALIZE_TOLERANCE = 1.0e-10;

  /**
   * Solve a single univariate polynomial equation numerically.
   *
   * <p>
   * This is the path <code>NSolve</code> takes - and <code>Solve</code> for an equation which
   * already contains machine numbers. Determining the roots numerically rather than solving
   * exactly and applying <code>N</code> afterwards is what makes the result a machine number for
   * every degree (a symbolic solution of degree 5 or higher stays an inert <code>Root</code>
   * object), keeps a root of multiplicity <code>k</code> present <code>k</code> times, and orders
   * the solutions by real and then imaginary part.
   *
   * @param termsEqualZeroList the equations, as expressions which have to become <code>0</code>
   * @param variables the variables to solve for
   * @param domain {@link S#Complexes}, {@link S#Reals} or {@link S#Rationals}
   * @param maxRoots the maximum number of roots to return
   * @param engine the evaluation engine
   * @return the list of solution lists or {@link F#NIL} if this isn't a single univariate
   *         polynomial equation with numeric coefficients
   */
  private static IExpr solveNumericPolynomial(IAST termsEqualZeroList, IAST variables,
      ISymbol domain, int maxRoots, EvalEngine engine) {
    if (termsEqualZeroList.argSize() != 1 || variables.argSize() != 1
        || !variables.arg1().isSymbol()) {
      return F.NIL;
    }
    if (domain != S.Complexes && domain != S.Reals && domain != S.Rationals) {
      return F.NIL;
    }
    if (engine.isArbitraryMode()) {
      // a working precision beyond machine precision can only be reached by evaluating an exact
      // solution numerically
      return F.NIL;
    }
    final ISymbol variable = (ISymbol) variables.arg1();
    final IExpr term = termsEqualZeroList.arg1();
    // a fraction is zero exactly where its numerator is zero
    IExpr together = S.Together.of(engine, term);
    IExpr numerator = S.Numerator.of(engine, together);
    IExpr denominator = S.Denominator.of(engine, together);
    if (!numerator.isPolynomial(F.list(variable))) {
      // an equation like Sin(x)==0 or 3^x==2*x has to be solved symbolically
      return F.NIL;
    }
    IAST roots = RootsFunctions.allNumericRoots(numerator, variable, engine);
    if (roots.isNIL()) {
      return F.NIL;
    }
    boolean checkDenominator = !denominator.isFree(variable);
    IASTAppendable result = F.ListAlloc(roots.argSize());
    for (int i = 1; i < roots.size(); i++) {
      IExpr root = roots.get(i);
      if (checkDenominator) {
        IExpr value = engine.evalN(F.subst(denominator, F.Rule(variable, root)));
        if (!value.isNumber() || value.isZero()) {
          // the "root" is a pole of the original equation
          continue;
        }
      }
      if (domain == S.Reals && !root.isReal()) {
        continue;
      }
      if (domain == S.Rationals && !isRationalRoot(root, term, variable, engine)) {
        continue;
      }
      result.append(F.list(F.Rule(variable, root)));
      if (result.argSize() >= maxRoots) {
        break;
      }
    }
    return result;
  }

  /**
   * Test if a numerically determined root is a rational number, i.e. if the root is a member of
   * the {@link S#Rationals} domain.
   *
   * <p>
   * The test cannot be made on the machine number itself - every machine number <i>is</i> a
   * rational number - so the root is rationalized within {@link #RATIONALIZE_TOLERANCE} and the
   * resulting candidate is substituted into the equation: <code>2.0</code> becomes <code>2</code>,
   * which solves <code>x^2-4 == 0</code> exactly, while <code>1.4142135623730951</code> becomes a
   * fraction which doesn't solve <code>x^2-2 == 0</code>.
   *
   * @param root a numerically determined root
   * @param term the expression which has to become <code>0</code>
   * @param variable the variable of the equation
   * @param engine the evaluation engine
   * @return <code>true</code> if the root is a rational number
   */
  private static boolean isRationalRoot(IExpr root, IExpr term, ISymbol variable,
      EvalEngine engine) {
    if (!root.isReal()) {
      return false;
    }
    IExpr rationalized = engine.evaluate(F.Rationalize(root, F.num(RATIONALIZE_TOLERANCE)));
    if (!rationalized.isRational()) {
      return false;
    }
    IExpr substituted = engine.evaluate(F.subst(term, F.Rule(variable, rationalized)));
    return engine.evaluate(F.PossibleZeroQ(substituted)).isTrue();
  }

  /**
   * The maximum number of variable assignments which are enumerated for the {@link S#Modulus}
   * option, i.e. <code>modulus ^ numberOfVariables</code> must not exceed this limit.
   */
  private static final long MAX_MODULUS_SEARCH_SPACE = 1_000_000L;

  /**
   * Solve the equations in the residue class ring <code>Z / modulus Z</code>, i.e. determine all
   * variable assignments from <code>{0, 1, ..., modulus-1}</code> which fulfill every equation
   * modulo <code>modulus</code>.
   *
   * <p>
   * The complete residue system is enumerated, so the result is exact for arbitrary (also
   * non-prime) moduli, as long as the search space <code>modulus ^ numberOfVariables</code> stays
   * below {@link #MAX_MODULUS_SEARCH_SPACE}. Only polynomial equations with rational coefficients
   * are solved, all other systems are returned unevaluated.
   *
   * @param ast the <code>Solve(...)</code> ast
   * @param userDefinedVariables the variables the user asked to solve for
   * @param modulusOption the value of the {@link S#Modulus} option
   * @param engine the evaluation engine
   * @return the (possibly empty) list of solution lists or {@link F#NIL} if the system cannot be
   *         solved this way
   */
  private static IExpr solveModulus(IAST ast, IAST userDefinedVariables, IExpr modulusOption,
      EvalEngine engine) {
    int modulus = modulusOption.toIntDefault();
    if (!modulusOption.isInteger() || modulus < 1) {
      // Value of option `1` should be a prime number or zero.
      return Errors.printMessage(ast.topHead(), "modp",
          F.List(F.Rule(S.Modulus, modulusOption)), engine);
    }
    IInteger modulusValue = (IInteger) modulusOption;

    IAST termsList = Validate.checkEquationsAndInequations(ast, 1);
    IASTMutable[] lists = SolveUtils.filterSolveLists(termsList, F.NIL, new boolean[] {false});
    if (lists[2].isPresent()) {
      // either no solution possible or no equations at all
      return lists[2];
    }
    if (lists[1].argSize() > 0) {
      // inequalities aren't defined in a residue class ring
      return Errors.printMessage(ast.topHead(), "nsmet", F.list(ast.topHead()), engine);
    }
    IAST termsEqualZeroList = lists[0];

    // only the variables which really occur in the equations are enumerated; the remaining
    // user defined variables are unconstrained and therefore not part of the solution rules
    VariablesSet equationVariables = new VariablesSet(termsEqualZeroList);
    IAST varList = equationVariables.getVarList();
    for (int i = 1; i < varList.size(); i++) {
      if (!userDefinedVariables.contains(varList.get(i))) {
        // the equations contain a symbol which shouldn't be solved for
        return Errors.printMessage(ast.topHead(), "nsmet", F.list(ast.topHead()), engine);
      }
    }
    // A fraction is zero exactly where its numerator is zero. Together() additionally clears the
    // denominators of rational coefficients, so that an equation like 2*x == 3 - which the
    // evaluator already normalized to x == 3/2 - is enumerated as the integer polynomial 2*x-3.
    IASTAppendable numerators = F.ListAlloc(termsEqualZeroList.argSize());
    IASTAppendable denominators = F.ListAlloc(termsEqualZeroList.argSize());
    for (int i = 1; i < termsEqualZeroList.size(); i++) {
      IExpr together = S.Together.of(engine, termsEqualZeroList.get(i));
      IExpr numerator = S.Numerator.of(engine, together);
      IExpr denominator = S.Denominator.of(engine, together);
      if (!isModulusPolynomial(numerator, varList, engine)
          || !isModulusPolynomial(denominator, varList, engine)) {
        // only polynomials with rational coefficients have a meaning in a residue class ring
        return Errors.printMessage(ast.topHead(), "nsmet", F.list(ast.topHead()), engine);
      }
      numerators.append(numerator);
      denominators.append(denominator);
    }

    IASTAppendable searchVariables = F.ListAlloc(userDefinedVariables.argSize());
    long searchSpace = 1L;
    for (int i = 1; i < userDefinedVariables.size(); i++) {
      IExpr variable = userDefinedVariables.get(i);
      if (equationVariables.contains(variable)) {
        searchVariables.append(variable);
        searchSpace *= modulus;
        if (searchSpace > MAX_MODULUS_SEARCH_SPACE) {
          // The system cannot be solved with the methods available to Solve.
          return Errors.printMessage(ast.topHead(), "nsmet", F.list(ast.topHead()), engine);
        }
      }
    }

    int numberOfVariables = searchVariables.argSize();
    int[] residues = new int[numberOfVariables];
    IASTAppendable result = F.ListAlloc();
    do {
      IASTAppendable rules = F.ListAlloc(numberOfVariables);
      for (int i = 0; i < numberOfVariables; i++) {
        rules.append(F.Rule(searchVariables.get(i + 1), F.ZZ(residues[i])));
      }
      if (isModulusSolution(numerators, denominators, rules, modulusValue, engine)) {
        result.append(rules);
      }
    } while (nextResidues(residues, modulus));
    return result;
  }

  /**
   * Test if <code>expr</code> is a polynomial in the given variables and if all its coefficients
   * are rational numbers. Only such an expression can be mapped into a residue class ring.
   *
   * @param expr the expression to test
   * @param varList the variables of the polynomial
   * @param engine the evaluation engine
   * @return <code>true</code> if <code>expr</code> is a polynomial with rational coefficients
   */
  private static boolean isModulusPolynomial(IExpr expr, IAST varList, EvalEngine engine) {
    if (!expr.isPolynomial(varList)) {
      return false;
    }
    if (varList.isEmpty()) {
      return expr.isRational();
    }
    IExpr coefficientRules = S.CoefficientRules.of(engine, expr, varList);
    if (!coefficientRules.isList()) {
      return false;
    }
    return ((IAST) coefficientRules).forAll(rule -> rule.isRule() && rule.second().isRational());
  }

  /**
   * Test if all numerators are divisible by <code>modulus</code> after substituting the variables
   * with the values of the given <code>rules</code>. A residue for which a denominator becomes
   * divisible by <code>modulus</code> isn't a solution, because the corresponding term isn't
   * defined in the residue class ring.
   *
   * @param numerators the numerators of the expressions which should become <code>0</code>
   * @param denominators the corresponding denominators
   * @param rules the list of <code>variable -> residue</code> rules
   * @param modulus the modulus
   * @param engine the evaluation engine
   * @return <code>true</code> if every term is <code>0</code> modulo <code>modulus</code>
   */
  private static boolean isModulusSolution(IAST numerators, IAST denominators, IAST rules,
      IInteger modulus, EvalEngine engine) {
    for (int i = 1; i < numerators.size(); i++) {
      IExpr denominator = denominators.get(i);
      if (!denominator.isOne()
          && engine.evaluate(F.Mod(F.subst(denominator, rules), modulus)).isZero()) {
        // the term isn't defined for this residue
        return false;
      }
      IExpr numerator = F.subst(numerators.get(i), rules);
      if (!engine.evaluate(F.Mod(numerator, modulus)).isZero()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Step to the next tuple of the complete residue system in lexicographical order, i.e. increment
   * the last entry of <code>residues</code> and carry over to the entries on the left.
   *
   * @param residues the current tuple; modified in place
   * @param modulus the modulus
   * @return <code>false</code> if the last tuple was already reached
   */
  private static boolean nextResidues(int[] residues, int modulus) {
    for (int i = residues.length - 1; i >= 0; i--) {
      if (++residues[i] < modulus) {
        return true;
      }
      residues[i] = 0;
    }
    return false;
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
  /**
   * Solve a single univariate polynomial equation over the integers <em>exactly</em>.
   *
   * <p>
   * Every integer root of a polynomial with integer coefficients divides its constant term
   * (rational root theorem), so the candidates can be enumerated exactly and the result is
   * complete. A finite-domain constraint solver, by contrast, is only complete inside the box it
   * happens to search: outside that box it can neither find a root nor prove that none exists, so
   * it may report the empty set for a system that does have integer solutions - which is what
   * <code>Solve(-4-4*x+x^4+x^5==0, x, Integers)</code> used to do even though <code>x == -1</code>
   * is a root.
   *
   * <p>
   * A zero constant term is handled by treating the factored-out <code>x^k</code> separately, which
   * contributes the root <code>0</code>. Additional inequality constraints are applied as a filter
   * afterwards.
   *
   * @param equationsAndInequations the equations and inequality constraints
   * @param equationVariables the variables occurring in the equations
   * @param userDefinedVariables the variables the user asked to solve for
   * @param maximumNumberOfResults the maximum number of results to return
   * @param engine the evaluation engine
   * @return the (possibly empty) list of solution lists, or {@link F#NIL} if this isn't a single
   *         univariate polynomial equation with rational coefficients
   */
  private static IExpr solveIntegersPolynomial(IAST equationsAndInequations, IAST equationVariables,
      IAST userDefinedVariables, int maximumNumberOfResults, EvalEngine engine) {
    if (userDefinedVariables.argSize() != 1 || equationVariables.argSize() != 1
        || !equationVariables.arg1().isSymbol()
        || !userDefinedVariables.arg1().equals(equationVariables.arg1())) {
      return F.NIL;
    }
    final IExpr variable = equationVariables.arg1();
    IExpr equation = F.NIL;
    IASTAppendable constraints = F.ListAlloc();
    for (int i = 1; i < equationsAndInequations.size(); i++) {
      IExpr expr = equationsAndInequations.get(i);
      if (expr.isEqual() && expr.isAST2()) {
        if (equation.isPresent()) {
          return F.NIL; // more than one equation is not handled here
        }
        equation = F.Subtract(expr.first(), expr.second());
      } else if (expr.isRelationalBinary()) {
        constraints.append(expr);
      } else {
        return F.NIL;
      }
    }
    if (equation.isNIL()) {
      return F.NIL;
    }
    // a rational equation is zero exactly where its numerator is zero; Together() also clears the
    // denominators of rational coefficients, so the numerator has integer coefficients
    IExpr numerator = S.Numerator.of(engine, S.Together.of(engine, equation));
    if (numerator.isZero() || !numerator.isPolynomial(F.list(variable))) {
      return F.NIL;
    }
    IExpr coefficients = S.CoefficientList.of(engine, numerator, variable);
    if (!coefficients.isList() || coefficients.argSize() < 2) {
      return F.NIL;
    }
    IAST coefficientList = (IAST) coefficients;
    // index of the lowest non-zero coefficient: everything below it is a factored-out power of the
    // variable, and the coefficient at that index is the constant term of the reduced polynomial
    int lowest = -1;
    for (int i = 1; i < coefficientList.size(); i++) {
      IExpr coefficient = coefficientList.get(i);
      if (!coefficient.isInteger()) {
        return F.NIL;
      }
      if (lowest < 0 && !coefficient.isZero()) {
        lowest = i;
      }
    }
    if (lowest < 0) {
      return F.NIL; // identically zero: solved by every integer
    }
    IInteger constantTerm = ((IInteger) coefficientList.get(lowest)).abs();
    if (constantTerm.toLongDefault() == Long.MIN_VALUE
        || constantTerm.toLongDefault() > 1000000000L) {
      // factoring a huge constant term would cost more than it saves
      return F.NIL;
    }
    IExpr divisors = S.Divisors.of(engine, constantTerm);
    if (!divisors.isList()) {
      return F.NIL;
    }
    Set<IExpr> roots = new TreeSet<IExpr>();
    if (lowest > 1) {
      roots.add(F.C0); // the factored-out power of the variable contributes the root 0
    }
    IAST divisorList = (IAST) divisors;
    for (int i = 1; i < divisorList.size(); i++) {
      IExpr divisor = divisorList.get(i);
      if (!divisor.isInteger()) {
        continue;
      }
      for (int sign = 0; sign < 2; sign++) {
        IExpr candidate = sign == 0 ? divisor : divisor.negate();
        if (engine.evaluate(F.subst(numerator, variable, candidate)).isZero()) {
          roots.add(candidate);
        }
      }
    }

    IASTAppendable result = F.ListAlloc(roots.size());
    for (IExpr root : roots) {
      boolean satisfiesConstraints = true;
      for (int i = 1; i < constraints.size(); i++) {
        if (!engine.evaluate(F.subst(constraints.get(i), variable, root)).isTrue()) {
          satisfiesConstraints = false;
          break;
        }
      }
      if (satisfiesConstraints) {
        result.append(F.list(F.Rule(variable, root)));
        if (maximumNumberOfResults > 0 && result.argSize() >= maximumNumberOfResults) {
          break;
        }
      }
    }
    return result;
  }

  /**
   * Solve a single linear Diophantine equation whose variables aren't bounded by any inequality.
   *
   * <p>
   * Such a system has infinitely many integer solutions, so there is nothing for a finite-domain
   * constraint solver to enumerate: it would silently fall back on a default search box and return
   * an arbitrary truncated prefix of the solution family (for <code>x + y == 5</code> that used to
   * be a thousand tuples starting at <code>x == -499</code>). Instead the family is returned in
   * closed form, parametrized by <code>C(1)</code>, e.g.
   * <code>{{x -> ConditionalExpression(C(1), C(1) &isin; Integers), y -> ConditionalExpression(5 -
   * C(1), C(1) &isin; Integers)}}</code>.
   *
   * <p>
   * {@link S#Reduce} already solves linear Diophantine equations with the extended Euclidean
   * algorithm and reports <code>False</code> when the gcd of the coefficients doesn't divide the
   * right hand side; this method only rewrites its <code>And(...)</code> answer into the "list of
   * solution rules" shape that {@link S#Solve} returns.
   *
   * @return the parametrized solution, {@link F#CEmptyList} if no integer solution exists, or
   *         {@link F#NIL} if this isn't an unbounded single linear equation
   */
  private static IExpr solveIntegersLinearParametric(IAST equationsAndInequations,
      IAST userDefinedVariables, EvalEngine engine) {
    // argSize() == 1 also guarantees there are no inequality constraints: with bounds the
    // constraint solver can enumerate the finite solution set, which is more informative
    if (equationsAndInequations.argSize() != 1 || userDefinedVariables.argSize() < 2) {
      return F.NIL;
    }
    IExpr equation = equationsAndInequations.arg1();
    if (!equation.isEqual() || !equation.isAST2()) {
      return F.NIL;
    }
    IExpr difference = F.Subtract(equation.first(), equation.second());
    for (int i = 1; i < userDefinedVariables.size(); i++) {
      IExpr variable = userDefinedVariables.get(i);
      if (!variable.isSymbol() || !S.Exponent.of(engine, difference, variable).isOne()) {
        // every solve variable has to occur, and occur linearly
        return F.NIL;
      }
    }

    IExpr reduced = engine.evaluate(F.Reduce(equation, userDefinedVariables, S.Integers));
    if (reduced.isFalse()) {
      return F.CEmptyList;
    }
    if (!reduced.isAnd()) {
      return F.NIL;
    }
    IAST and = (IAST) reduced;
    IExpr condition = F.NIL;
    IASTAppendable equations = F.ListAlloc(and.argSize());
    for (int i = 1; i < and.size(); i++) {
      IExpr arg = and.get(i);
      if (arg.isAST(S.Element, 3)) {
        condition = condition.isNIL() ? arg : F.And(condition, arg);
      } else if (arg.isEqual() && arg.isAST2() && arg.first().isSymbol()
          && userDefinedVariables.indexOf(arg.first()) > 0) {
        equations.append(arg);
      } else {
        return F.NIL;
      }
    }
    if (condition.isNIL() || equations.argSize() != userDefinedVariables.argSize()) {
      return F.NIL;
    }

    IASTAppendable rules = F.ListAlloc(equations.argSize());
    for (int i = 1; i < userDefinedVariables.size(); i++) {
      IExpr variable = userDefinedVariables.get(i);
      IExpr value = F.NIL;
      for (int j = 1; j < equations.size(); j++) {
        if (equations.get(j).first().equals(variable)) {
          value = equations.get(j).second();
          break;
        }
      }
      if (value.isNIL()) {
        return F.NIL;
      }
      rules.append(F.Rule(variable, F.ConditionalExpression(value, condition)));
    }
    return F.list(rules);
  }

  public static IExpr solveIntegers(final IAST ast, IAST equationVariables,
      IAST userDefinedVariables, int maximumNumberOfResults, ISymbol domain, EvalEngine engine) {
    return solveIntegers(ast, equationVariables, userDefinedVariables, maximumNumberOfResults,
        domain, false, engine);
  }

  /**
   * @param allowParametricSolution if <code>true</code>, an unbounded linear Diophantine equation
   *        may be answered with its closed-form solution family instead of enumerated solutions.
   *        Callers that need concrete instances - {@link S#FindInstance}, or a {@link S#Solve} call
   *        which asked for a specific finite number of results via {@link S#MaxRoots} - pass
   *        <code>false</code> and get the enumeration.
   */
  public static IExpr solveIntegers(final IAST ast, IAST equationVariables,
      IAST userDefinedVariables, int maximumNumberOfResults, ISymbol domain,
      boolean allowParametricSolution, EvalEngine engine) {
    if (!userDefinedVariables.isEmpty()) {
      IAST equationsAndInequations = Validate.checkEquationsAndInequations(ast, 1);
      if (equationsAndInequations.isEmpty()) {
        return F.NIL;
      }
      try {
        // Exact path first: a single univariate polynomial equation has a finite, exactly
        // computable set of integer roots, so it never needs a (necessarily bounded, and therefore
        // incomplete) constraint-solver search.
        IExpr exactResult = solveIntegersPolynomial(equationsAndInequations, equationVariables,
            userDefinedVariables, maximumNumberOfResults, engine);
        if (exactResult.isPresent()) {
          return exactResult;
        }

        // Unbounded single linear equation: the solution family is infinite, so return it in
        // closed form. This runs before the Diophantine / constraint-solver paths below, which
        // would otherwise enumerate an arbitrary prefix out of a default search box.
        if (allowParametricSolution) {
          IExpr parametricResult =
              solveIntegersLinearParametric(equationsAndInequations, userDefinedVariables, engine);
          if (parametricResult.isPresent()) {
            return parametricResult;
          }
        }

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
          CreamConvert converter = new CreamConvert();
          IAST resultList = converter.integerSolve(equationsAndInequations, equationVariables,
              userDefinedVariables, maximumNumberOfResults, engine);
          if (resultList.isPresent()) {
            EvalAttributes.sort((IASTMutable) resultList);
            return resultList;
          }
        }
      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
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
    SolveData sd = new SolveData(options, options[2]);
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
    return new IExpr[] {S.True, F.C1000, F.C0};
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IBuiltInSymbol[] optionKeys =
        new IBuiltInSymbol[] {S.GenerateConditions, S.MaxRoots, S.Modulus};
    IExpr[] optionValues = defaultOptionValues();
    setOptions(newSymbol, optionKeys, optionValues);
  }
}


