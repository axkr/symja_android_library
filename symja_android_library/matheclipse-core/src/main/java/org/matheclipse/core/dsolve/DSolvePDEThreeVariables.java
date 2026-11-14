package org.matheclipse.core.dsolve;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A linear partial differential equation of first order in three variables,
 * <code>P*w_x + Q*w_y + R*w_z == S(x, y, z, w)</code> with coefficients free of <code>w</code>,
 * by its characteristics.
 *
 * <p>
 * With one variable <code>b</code> as the parameter of a characteristic, the other two satisfy
 * <code>s'(b) == Q/P</code> and <code>t'(b) == R/P</code>. The method applies when one of these
 * doesn't depend on the other variable: its solution <code>s == Y(b, c1)</code> gives the first
 * integral <code>c1 == phi1(b, s)</code>, and with it the second equation is an ordinary one, which
 * gives <code>c2 == phi2(b, s, t)</code>. Along a characteristic <code>w</code> solves
 * <code>w'(b) == S/P</code>, and its constant is an arbitrary function of <code>phi1</code> and
 * <code>phi2</code>. Every answer is checked by substitution.
 */
final class DSolvePDEThreeVariables {

  private DSolvePDEThreeVariables() {}

  /** The orders in which the variables are the parameter, the first and the second coordinate. */
  private static final int[][] ORDERS =
      {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 1, 2}, {3, 2, 1}};

  /**
   * The general solution as a list of one list with one rule, or {@link F#NIL}.
   *
   * @param equation the equation, or an expression which is meant to be zero
   * @param uFunc the unknown, either <code>w</code> or <code>w(x,y,z)</code>
   * @param vars the three independent variables
   */
  static IExpr solve(IExpr equation, IExpr uFunc, IAST vars, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    for (IExpr v : vars) {
      if (!v.isSymbol()) {
        return F.NIL;
      }
    }
    final IExpr uApplied =
        uFunc.isAST() ? uFunc : F.ternaryAST3(uFunc, vars.arg1(), vars.arg2(), vars.arg3());
    if (!uApplied.isAST3() || !uApplied.first().equals(vars.arg1())
        || !uApplied.second().equals(vars.arg2()) || !uApplied.getAt(3).equals(vars.arg3())) {
      return F.NIL;
    }
    final int counter = engine.getConstantCounter();
    try {
      IExpr lhs = equation.isEqual()
          ? engine.evaluate(F.Subtract(equation.first(), equation.second()))
          : equation;
      lhs = engine.evaluate(F.ExpandAll(lhs));
      IExpr head = uApplied.head();
      IExpr[] derivative = new IExpr[4];
      IExpr[] coefficient = new IExpr[4];
      IASTAppendable derivativeTerms = F.PlusAlloc(3);
      for (int i = 1; i <= 3; i++) {
        derivative[i] = engine.evaluate(F.D(uApplied, vars.get(i)));
        coefficient[i] = engine.evaluate(F.Coefficient(lhs, derivative[i]));
        if (!coefficient[i].isFree(head, true)) {
          // not linear in the derivatives, or a coefficient with the unknown
          return F.NIL;
        }
        derivativeTerms.append(F.Times(coefficient[i], derivative[i]));
      }
      IExpr rest = engine.evaluate(F.Expand(F.Subtract(lhs, derivativeTerms)));
      if (!rest.isFree(x -> x.isAST() && x.head().isAST() && x.head().head().isAST(S.Derivative),
          true)) {
        return F.NIL;
      }
      for (int[] order : ORDERS) {
        engine.checkTimeLimit();
        if (coefficient[order[0]].isZero()) {
          continue;
        }
        engine.setConstantCounter(counter);
        IExpr body = byCharacteristics(lhs, rest, uApplied, vars, coefficient, order, engine);
        if (body.isPresent()) {
          IExpr value = uFunc.isAST() ? body : F.Function(vars, body);
          return F.list(F.list(F.Rule(uFunc, value)));
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    engine.setConstantCounter(counter);
    return F.NIL;
  }

  private static IExpr byCharacteristics(IExpr lhs, IExpr rest, IExpr uApplied, IAST vars,
      IExpr[] coefficient, int[] order, EvalEngine engine) {
    final IExpr b = vars.get(order[0]);
    final IExpr s = vars.get(order[1]);
    final IExpr t = vars.get(order[2]);
    IExpr ratioS =
        engine.evaluate(F.Simplify(F.Divide(coefficient[order[1]], coefficient[order[0]])));
    if (!ratioS.isFree(t)) {
      return F.NIL;
    }
    IExpr ratioT =
        engine.evaluate(F.Simplify(F.Divide(coefficient[order[2]], coefficient[order[0]])));
    ISymbol c1 = F.Dummy("pvC1");
    ISymbol c2 = F.Dummy("pvC2");
    ISymbol c3 = F.Dummy("pvC3");
    // s as a function of b on a characteristic, and the first integral
    IExpr onS = ratioS.isZero() ? c1 : alongCharacteristic(ratioS, s, b, c1, engine);
    if (onS.isNIL()) {
      return F.NIL;
    }
    IExpr phi1 = ratioS.isZero() ? s : solvedFor(F.Equal(s, onS), c1, engine);
    if (phi1.isNIL()) {
      return F.NIL;
    }
    // t as a function of b, with s on the characteristic
    IExpr ratioTOnS = engine.evaluate(F.subst(ratioT, s, onS));
    IExpr onT = ratioTOnS.isZero() ? c2 : alongCharacteristic(ratioTOnS, t, b, c2, engine);
    if (onT.isNIL()) {
      return F.NIL;
    }
    IExpr phi2 = ratioTOnS.isZero() ? t : solvedFor(F.Equal(t, onT), c2, engine);
    if (phi2.isNIL()) {
      return F.NIL;
    }
    phi2 = engine.evaluate(F.Simplify(F.subst(phi2, c1, phi1)));
    phi1 = engine.evaluate(F.Simplify(phi1));
    if (!phi2.isFree(c1) || !phi1.isFree(c2) || phi1.isFree(s) || phi2.isFree(t)) {
      return F.NIL;
    }
    IExpr arbitrary = F.binaryAST2(F.C(engine.incConstantCounter()), phi1, phi2);
    IExpr body;
    if (rest.isZero()) {
      body = arbitrary;
    } else {
      // w on the characteristic: w'(b) == -rest/P
      ISymbol w = F.Dummy("pvW");
      IExpr wOfB = F.unaryAST1(w, b);
      IExpr rate = F.Divide(F.Negate(rest), coefficient[order[0]]);
      rate = F.subst(rate, uApplied, wOfB);
      rate = engine.evaluate(F.subst(F.subst(rate, t, onT), s, onS));
      IExpr onW = generalSolution(F.Equal(F.D(wOfB, b), rate), wOfB, b, c3, engine);
      if (onW.isNIL()) {
        return F.NIL;
      }
      body = F.subst(onW, c3, arbitrary);
      body = F.subst(F.subst(body, c2, phi2), c1, phi1);
      body = engine.evaluate(F.Simplify(body));
      if (!body.isFree(c1) || !body.isFree(c2) || !body.isFree(c3) || !body.isFree(w, true)) {
        return F.NIL;
      }
    }
    return solves(lhs, uApplied, vars, body, engine) ? body : F.NIL;
  }

  /**
   * The solution <code>v == V(b, c)</code> of <code>v'(b) == rate</code> with the constant
   * <code>c</code>, or {@link F#NIL} if there isn't exactly one.
   */
  private static IExpr alongCharacteristic(IExpr rate, IExpr v, IExpr b, ISymbol c,
      EvalEngine engine) {
    ISymbol f = F.Dummy("pvF");
    IExpr fOfB = F.unaryAST1(f, b);
    return generalSolution(F.Equal(F.D(fOfB, b), F.subst(rate, v, fOfB)), fOfB, b, c, engine);
  }

  /** The general solution of an ordinary equation of first order, its constant named <code>c</code>. */
  private static IExpr generalSolution(IExpr equation, IExpr fOfB, IExpr b, ISymbol c,
      EvalEngine engine) {
    final int counter = engine.getConstantCounter();
    IExpr solution;
    try {
      solution = engine.evalQuiet(F.DSolve(equation, fOfB, b));
    } finally {
      engine.setConstantCounter(counter);
    }
    if (!solution.isListOfLists() || solution.argSize() != 1 || solution.first().argSize() != 1
        || !solution.first().first().isRule()) {
      return F.NIL;
    }
    IExpr value = solution.first().first().second();
    if (!value.isFree(fOfB.head(), true) || !value.isFree(S.Integrate, true)
        || !value.isFree(S.Inactive, true)) {
      return F.NIL;
    }
    IExpr[] constant = new IExpr[] {F.NIL};
    boolean[] several = new boolean[1];
    value.isFree(x -> {
      if (x.isAST(S.C, 2) && x.first().isInteger()) {
        if (constant[0].isPresent() && !constant[0].equals(x)) {
          several[0] = true;
        }
        constant[0] = x;
      }
      return false;
    }, true);
    if (several[0] || constant[0].isNIL()) {
      return F.NIL;
    }
    return F.subst(value, constant[0], c);
  }

  /** The one solution of <code>equation</code> for <code>c</code>, or {@link F#NIL}. */
  private static IExpr solvedFor(IExpr equation, ISymbol c, EvalEngine engine) {
    IExpr solution = engine.evalQuiet(F.Solve(equation, c));
    if (!solution.isListOfLists() || solution.argSize() != 1 || solution.first().argSize() != 1
        || !solution.first().first().isRule()) {
      return F.NIL;
    }
    IExpr value = solution.first().first().second();
    return value.isFree(c) && value.isFree(S.ConditionalExpression, true) ? value : F.NIL;
  }

  /** Whether <code>body</code> with its arbitrary function solves the equation. */
  private static boolean solves(IExpr lhs, IExpr uApplied, IAST vars, IExpr body,
      EvalEngine engine) {
    IExpr residual = engine.evaluate(
        F.ReplaceAll(lhs, F.Rule(uApplied.head(), F.Function(vars, body))));
    if (residual.isZero()) {
      return true;
    }
    if (!residual.isFree(uApplied.head(), true)) {
      return false;
    }
    // over one denominator first: the residual of an exponential factor times the arbitrary
    // function has more than a thousand leaves, which the zero test doesn't look at
    if (engine.evalQuiet(F.Together(residual)).isZero()) {
      return true;
    }
    return engine.evalQuiet(F.PossibleZeroQ(residual)).isTrue();
  }
}
