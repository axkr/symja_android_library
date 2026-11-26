package org.matheclipse.core.reflection.system;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class SolveAlways extends AbstractFunctionOptionEvaluator {

  public SolveAlways() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    try {
      IAST variables =
          Validate.checkIsAlgebraicVariableOrAlgebraicVariableList(ast, 2, ast.topHead(), engine);

      IAST termsList = Validate.checkEquationsAndInequations(ast, 1);
      if (termsList.isEmpty() && ast.arg1().isTrue()) {
        return F.list(F.CEmptyList);
      } else if (termsList.isEmpty()) {
        return F.NIL;
      }

      boolean[] isNumeric = new boolean[] {false};
      IASTMutable[] lists = SolveUtils.filterSolveLists(termsList, F.NIL, isNumeric);

      if (lists[2].isPresent()) {
        return lists[2];
      }

      IASTMutable termsEqualZeroList = lists[0];
      IASTMutable inequationsList = lists[1];

      // Coefficient rules extraction cannot apply to inequations; trigger fallback immediately
      if (!inequationsList.isEmpty()) {
        return fallbackEliminate(originalAST, variables, argSize, engine);
      }

      // Extract algebraic parameters strictly explicitly avoiding the `vars` defined
      IAST equationVariables = VariablesSet.getAlgebraicVariables(ast.arg1(), false);
      IASTAppendable parameters = F.ListAlloc();
      for (int i = 1; i <= equationVariables.argSize(); i++) {
        if (!variables.contains(equationVariables.get(i))) {
          parameters.append(equationVariables.get(i));
        }
      }

      IASTAppendable newEqns = F.ListAlloc();
      for (int i = 1; i <= termsEqualZeroList.argSize(); i++) {
        IExpr term = termsEqualZeroList.get(i);
        IExpr poly = engine.evaluate(F.Expand(term));
        if (!poly.isPolynomial(variables)) {
          // a rational function is 0 where its numerator is
          poly = engine.evaluate(F.Expand(F.Numerator(F.Together(term))));
        }
        if (!poly.isPolynomial(variables)) {
          // an identity in a transcendental function of the variables
          return solveBySamples(termsEqualZeroList, variables, parameters, engine);
        }
        IExpr rules = engine.evaluate(F.CoefficientRules(poly, variables));
        if (rules.isListOfRules(false)) {
          for (int j = 1; j <= rules.argSize(); j++) {
            IAST rule = (IAST) rules.get(j);
            newEqns.append(F.Equal(rule.second(), F.C0));
          }
        } else {
          return solveBySamples(termsEqualZeroList, variables, parameters, engine);
        }
      }

      IASTAppendable solveAST = F.ast(S.Solve);
      solveAST.append(newEqns);
      solveAST.append(parameters);

      // Append standard options requested for the Solve evaluation
      for (int i = argSize + 1; i <= originalAST.argSize(); i++) {
        solveAST.append(originalAST.get(i));
      }

      IExpr result = engine.evaluate(solveAST);
      if (result.isAST(S.Solve)) {
        return F.NIL; // Function couldn't completely evaluate internally. Let SolveAlways stay
                      // unevaluated.
      }
      return result;

    } catch (ValidateException ve) {
      return Errors.printMessage(ast.topHead(), ve, engine);
    } catch (RuntimeException rex) {
      return Errors.printMessage(ast.topHead(), rex, engine);
    }
  }

  /**
   * The parameter values with which the expressions <code>terms</code> are <code>0</code> for all
   * values of the variables, for terms which are no polynomials in them. The terms are sampled at
   * exact points, which gives equations for the parameters, and a solution of those equations is
   * kept if every term is then zero as a function of the variables. A solution never depends on the
   * variables: <code>SolveAlways(a*Sin(x)+b==0, x)</code> is <code>{{a-&gt;0,b-&gt;0}}</code>.
   *
   * @return the list of solutions, or {@link F#NIL} if the sample equations couldn't be solved
   */
  private static IExpr solveBySamples(IAST terms, IAST variables, IAST parameters,
      EvalEngine engine) {
    if (parameters.isEmpty()) {
      for (int i = 1; i < terms.size(); i++) {
        if (!engine.evalQuiet(F.PossibleZeroQ(terms.get(i))).isTrue()) {
          return F.CEmptyList;
        }
      }
      return F.list(F.CEmptyList);
    }
    final int samples = parameters.argSize() + 3;
    IASTAppendable sampleEquations = F.ListAlloc();
    for (int i = 1; i < terms.size(); i++) {
      IExpr term = terms.get(i);
      for (int k = 0; k < samples; k++) {
        IASTAppendable point = F.ListAlloc(variables.argSize());
        for (int j = 1; j <= variables.argSize(); j++) {
          // the points (2k+5)/(k+2) + (j-1)/7 lie above 2 and are no integers
          point.append(F.Rule(variables.get(j),
              F.Plus(F.QQ(2 * k + 5, k + 2), F.QQ(j - 1, 7))));
        }
        IExpr value = engine.evalQuiet(F.ReplaceAll(term, point));
        if (value.isFree(x -> x.isIndeterminate() || x.isDirectedInfinity(), true)) {
          sampleEquations.append(F.Equal(value, F.C0));
        }
      }
    }
    IExpr solutions = engine.evalQuiet(F.Solve(sampleEquations, parameters));
    if (!solutions.isList()) {
      return F.NIL;
    }
    IASTAppendable verified = F.ListAlloc();
    for (IExpr solution : (IAST) solutions) {
      boolean identity = true;
      for (int i = 1; i < terms.size() && identity; i++) {
        IExpr substituted = engine.evalQuiet(F.ReplaceAll(terms.get(i), solution));
        identity = engine.evalQuiet(F.PossibleZeroQ(substituted)).isTrue();
      }
      if (identity) {
        verified.append(simplifyValues((IAST) solution, engine));
      }
    }
    return verified;
  }

  /**
   * The values of a solution which were solved from samples are numeric expressions in the sample
   * points; a value which is a rational number in disguise, like
   * <code>(Cos(9/4)^2*Sec(13/6)^2*...)/(...)</code> for <code>1</code>, is replaced by it.
   */
  private static IExpr simplifyValues(IAST solution, EvalEngine engine) {
    if (!solution.isList()) {
      return solution;
    }
    IASTMutable result = solution.copy();
    for (int i = 1; i < solution.size(); i++) {
      IExpr rule = solution.get(i);
      if (rule.isRuleAST() && !rule.second().isRational()
          && rule.second().isNumericFunction(true)) {
        IExpr value = rule.second();
        IExpr approximation = engine.evalQuiet(F.N(value, F.ZZ(30)));
        if (approximation.isReal()) {
          IExpr rational = engine.evalQuiet(F.Rationalize(approximation, F.Power(F.C10, F.ZZ(-25))));
          if (rational.isRational() && engine
              .evalQuiet(F.PossibleZeroQ(F.Subtract(value, rational))).isTrue()) {
            result.set(i, F.binaryAST2(rule.head(), rule.first(), rational));
          }
        }
      }
    }
    return result;
  }

  /**
   * Equivalent fallback: SolveAlways[eqns,vars] := Solve[!Eliminate[!eqns,vars]] Used for
   * non-polynomials and equations failing standard CoefficientRules tracking.
   */
  private IExpr fallbackEliminate(IAST originalAST, IAST variables, int argSize,
      EvalEngine engine) {
    IExpr eqns = originalAST.arg1();
    IExpr vars = originalAST.arg2();
    if (eqns.isList()) {
      eqns = F.Apply(S.And, eqns);
    }

    IExpr notEqns = F.Not(eqns);
    IExpr eliminated = engine.evaluate(F.Eliminate(notEqns, vars));
    IExpr notEliminated = F.Not(eliminated);

    IASTAppendable solveAST = F.ast(S.Solve);
    solveAST.append(notEliminated);

    IAST allVars = VariablesSet.getAlgebraicVariables(originalAST.arg1(), false);
    IASTAppendable parameters = F.ListAlloc();
    for (int j = 1; j <= allVars.argSize(); j++) {
      if (!variables.contains(allVars.get(j))) {
        parameters.append(allVars.get(j));
      }
    }
    solveAST.append(parameters);

    for (int j = argSize; j <= originalAST.argSize(); j++) {
      solveAST.append(originalAST.get(j));
    }

    IExpr result = engine.evaluate(solveAST);
    if (result.isAST(S.Solve)) {
      return F.NIL;
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, SolveOptions.SOLVE_ALWAYS_KEYS, SolveOptions.SOLVE_ALWAYS_DEFAULTS);
  }
}
