package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Solve.SolveData;
import org.matheclipse.core.eval.util.SolveUtils;

/** Try to solve a set of equations (i.e. <code>Equal[...]</code> expressions) numerically. */
public class NSolve extends AbstractFunctionOptionEvaluator {
  public NSolve() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    SolveOptions solveOptions = SolveOptions.of(SolveOptions.NSOLVE_KEYS, options);
    long precision = SolveUtils.workingPrecision(ast, solveOptions.workingPrecision(), engine);
    if (precision == SolveUtils.INVALID_PRECISION) {
      return F.NIL;
    }
    if (ast.size() == 5) {
      // the working precision was given as the fourth argument
      ast = ast.copyUntil(4);
    }
    return solveNumeric(solveOptions, ast, precision, engine);
  }

  /**
   * Solve the equations numerically, if requested with a working precision beyond machine
   * precision.
   *
   * <p>
   * A machine number carries about 16 significant digits, so a higher working precision cannot be
   * served by the numerical polynomial solver: the equations are solved exactly and the exact
   * solutions - <code>Sqrt(2)</code>, or an inert <code>Root</code> object for a polynomial which
   * has no closed radical form - are then evaluated to the requested number of digits. Equations
   * which have no exact solution fall back to the machine precision solution.
   *
   * @param options the option values of the <code>NSolve(...)</code> /
   *        <code>NSolveValues(...)</code> call
   * @param ast the <code>NSolve(...)</code> / <code>NSolveValues(...)</code> ast without the
   *        working precision argument
   * @param precision the number of significant digits or {@link Solve#MACHINE_PRECISION_REQUESTED}
   * @param engine the evaluation engine
   * @return the list of solution lists
   */
  static IExpr solveNumeric(SolveOptions options, IAST ast, long precision, EvalEngine engine) {
    if (precision == SolveUtils.MACHINE_PRECISION_REQUESTED) {
      SolveData solveData = new SolveData(options);
      IExpr result = solveData.of(ast, true, true, engine);
      if (!solveData.isCompleteSolutions()) {
        result = Solve.withRationalizedSolutions(ast, result, true, false, engine);
      }
      return distinctSolutions(result,
          ast.argSize() >= 2 ? ast.arg2().makeList() : F.CEmptyList);
    }
    IExpr result = new SolveData(options).of(ast, false, true, engine);
    if (result.isNIL()) {
      result = new SolveData(options).of(ast, true, true, engine);
    }
    if (result.isNIL()) {
      return result;
    }
    return Solve.sortNumericSolutions(Solve.withNumericValues(result,
        value -> engine.evaluate(F.N(value, precision))));
  }

  /**
   * The solutions of several variables without the copies which the branches of exact radicals
   * leave behind once their values are machine numbers: an imaginary part which is only rounding
   * noise is dropped, and solutions which agree to working precision are merged. The solutions of
   * one variable keep their multiplicities.
   */
  private static IExpr distinctSolutions(IExpr result, IAST variables) {
    if (!result.isListOfLists() || ((IAST) result).exists(s -> s.argSize() < 2)) {
      return result;
    }
    IAST list = (IAST) result;
    IASTAppendable distinct = F.ListAlloc(list.argSize());
    for (IExpr solution : list) {
      IAST cleaned = inVariableOrder(withoutImaginaryNoise((IAST) solution), variables);
      if (!distinct.exists(other -> sameSolution((IAST) other, cleaned))) {
        distinct.append(cleaned);
      }
    }
    return distinct;
  }

  /** The rules of the solution in the order of the variables they are for. */
  static IAST inVariableOrder(IAST solution, IAST variables) {
    if (variables.argSize() != solution.argSize()) {
      return solution;
    }
    IASTAppendable ordered = F.ListAlloc(solution.argSize());
    for (IExpr variable : variables) {
      IExpr rule = F.NIL;
      for (IExpr r : solution) {
        if (r.isRuleAST() && r.first().equals(variable)) {
          rule = r;
          break;
        }
      }
      if (rule.isNIL()) {
        return solution;
      }
      ordered.append(rule);
    }
    return ordered;
  }

  /** The solution with real values, if all its imaginary parts are rounding noise. */
  static IAST withoutImaginaryNoise(IAST solution) {
    IASTMutable real = solution.copy();
    for (int i = 1; i < solution.size(); i++) {
      IExpr rule = solution.get(i);
      if (!rule.isRuleAST() || !rule.second().isNumber() || !rule.second().isInexactNumber()) {
        return solution;
      }
      INumber value = (INumber) rule.second();
      double re = value.reDoubleValue();
      double im = value.imDoubleValue();
      if (Math.abs(im) > NOISE * Math.max(1.0, Math.abs(re))) {
        return solution;
      }
      real.set(i, F.Rule(rule.first(), F.num(re)));
    }
    return real;
  }

  private static boolean sameSolution(IAST a, IAST b) {
    if (a.argSize() != b.argSize()) {
      return false;
    }
    for (int i = 1; i < a.size(); i++) {
      IExpr ruleA = a.get(i);
      IExpr ruleB = b.get(i);
      if (!ruleA.isRuleAST() || !ruleB.isRuleAST() || !ruleA.first().equals(ruleB.first())
          || !ruleA.second().isNumber() || !ruleB.second().isNumber()) {
        return ruleA.equals(ruleB) && ruleA.isRuleAST();
      }
      INumber x = (INumber) ruleA.second();
      INumber y = (INumber) ruleB.second();
      double distance = Math.hypot(x.reDoubleValue() - y.reDoubleValue(),
          x.imDoubleValue() - y.imDoubleValue());
      double size = Math.max(1.0, Math.max(Math.hypot(x.reDoubleValue(), x.imDoubleValue()),
          Math.hypot(y.reDoubleValue(), y.imDoubleValue())));
      if (!(distance <= SAME * size)) {
        return false;
      }
    }
    return true;
  }

  /** The relative size below which an imaginary part is rounding noise. */
  private static final double NOISE = 1.0e-12;

  /**
   * The relative distance below which two solutions are the same: a few hundred units of the last
   * digit, the rounding error of the branches of exact radicals evaluated with machine numbers.
   */
  private static final double SAME = 1.0e-13;

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_4;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, SolveOptions.NSOLVE_KEYS, SolveOptions.NSOLVE_DEFAULTS);
  }
}
