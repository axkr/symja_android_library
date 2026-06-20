package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class SolveAlways extends AbstractFunctionOptionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger(SolveAlways.class);

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

      IASTAppendable newEqns = F.ListAlloc();
      for (int i = 1; i <= termsEqualZeroList.argSize(); i++) {
        IExpr term = termsEqualZeroList.get(i);
        IExpr poly = engine.evaluate(F.Expand(term));
        IExpr rules = engine.evaluate(F.CoefficientRules(poly, variables));

        if (rules.isListOfRules(false)) {
          for (int j = 1; j <= rules.argSize(); j++) {
            IAST rule = (IAST) rules.get(j);
            newEqns.append(F.Equal(rule.second(), F.C0));
          }
        } else {
          // It's not a standard polynomial expression; use literal fallback equivalent
          return fallbackEliminate(originalAST, variables, argSize, engine);
        }
      }

      // Extract algebraic parameters strictly explicitly avoiding the `vars` defined
      IAST equationVariables = VariablesSet.getAlgebraicVariables(ast.arg1(), false);
      IASTAppendable parameters = F.ListAlloc();
      for (int i = 1; i <= equationVariables.argSize(); i++) {
        if (!variables.contains(equationVariables.get(i))) {
          parameters.append(equationVariables.get(i));
        }
      }

      IASTAppendable solveAST = F.ast(S.Solve);
      solveAST.append(newEqns);
      solveAST.append(parameters);

      // Append standard options requested for the Solve evaluation
      for (int i = argSize; i <= originalAST.argSize(); i++) {
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
      LOGGER.debug("SolveAlways.evaluate() failed", rex);
    }
    return F.NIL;
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
    IBuiltInSymbol[] optionKeys = new IBuiltInSymbol[] {S.GenerateConditions, S.MaxRoots};
    IExpr[] optionValues = new IExpr[] {S.True, F.C1000};
    setOptions(newSymbol, optionKeys, optionValues);
  }
}