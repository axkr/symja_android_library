package org.matheclipse.core.reflection.system.steps;

import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.F.fraction;
import java.util.Set;
import java.util.TreeSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

public class QuarticSolve extends AbstractFunctionEvaluator {

  public QuarticSolve() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 2) {
      return linearSolve(ast.arg1(), ast.arg2(), F.NIL, F.NIL);
    }
    if (ast.argSize() == 3) {
      return quadraticSolve(ast.arg1(), ast.arg2(), ast.arg3());
    }
    if (ast.argSize() == 4) {
      return cubicSolve(ast.arg1(), ast.arg2(), ast.arg3(), ast.arg4(), F.NIL, null);
    }
    return F.NIL;
  }

  /**
   * <code>Solve(a*x^3+b*x^2+c*x+d==0,x)</code>. See
   * <a href= "http://en.wikipedia.org/wiki/Cubic_function#General_formula_of_roots"> Wikipedia -
   * Cubic function</a>
   *
   * @param a coefficient for <code>x^3</code>
   * @param b coefficient for <code>x^2</code>
   * @param c coefficient for <code>x</code>
   * @param d coefficient for <code>1</code>
   * @param additionalSolution ann additional solution, which should be appended to the result
   * @return a list of solutions or {@link F#NIL}
   */
  public static IASTAppendable cubicSolve(IExpr a, IExpr b, IExpr c, IExpr d, IExpr variable,
      IExpr additionalSolution) {
    if (a.isPossibleZero(false)) {
      return quadraticSolve(b, c, d, variable, additionalSolution, null);
    } else {
      if (d.isPossibleZero(false)) {
        return quadraticSolve(a, b, c, variable, additionalSolution, C0);
      }
      IExpr list = F.List(//
          F.$str("a*x^3 +b*x^2 + c*x + d"), //
          F.Rule("a", a), //
          F.Rule("b", b), //
          F.Rule("c", c), //
          F.Rule("d", d));
      EvalEngine.get().addTraceStep(list, //
          F.$(S.QuarticSolve, a, b, c, d), //
          F.List(S.QuarticSolve, F.$str("CubicEquation"), a, b, c, d));

      IASTAppendable result = F.ListAlloc(4);
      if (additionalSolution != null) {
        result.append(additionalSolution);
      }
      // 18*a*b*c*d-4*b^3*d+b^2*c^2-4*a*c^3-27*a^2*d^2
      IExpr discriminant = F.eval(Plus(Times(ZZ(18L), a, b, c, d), Times(CN4, Power(b, C3), d),
          Times(Power(b, C2), Power(c, C2)), Times(CN4, a, Power(c, C3)),
          Times(ZZ(-27L), Power(a, C2), Power(d, C2))));
      // b^2 - 3*a*c
      IExpr delta0 = F.eval(Plus(Power(b, C2), Times(CN1, C3, a, c)));
      // (-2)*b^3 + 9*a*b*c - 27*a^2*d
      IExpr delta1 = F.eval(Plus(Times(ZZ(-2L), Power(b, C3)), Times(ZZ(9L), a, b, c),
          Times(CN1, ZZ(27L), Power(a, C2), d)));
      // (delta1 + Sqrt[delta1^2-4*delta0^3])^(1/3)
      IExpr argDelta3 =
          F.eval(Plus(delta1, Sqrt(Plus(Power(delta1, C2), Times(CN1, C4, Power(delta0, C3))))));
      IExpr delta3 = F.eval(Power(argDelta3, C1D3));

      // IExpr C = F.eval(Times(ZZ(-27L), a.power(C2), discriminant));
      if (discriminant.isPossibleZero(false)) {
        if (delta0.isPossibleZero(false)) {
          // the three roots are equal
          // (-b)/(3*a)
          result.append(Times(CN1, b, Power(Times(C3, a), CN1)));
        } else {
          // the double root
          // (9*a*d-b*c)/(2*delta0)
          result.append(
              Times(Plus(Times(ZZ(9L), a, d), Times(CN1, b, c)), Power(Times(C2, delta0), CN1)));
          // and a simple root
          // (4*a*b*c-9*a^2*d-b^3)/(a*delta0)
          result.append(Times(Plus(Times(C4, a, b, c), Times(CN1, ZZ(9L), Power(a, C2), d),
              Times(CN1, Power(b, C3))), Power(Times(a, delta0), CN1)));
        }
      } else {
        // -(b/(3*a)) - (2^(1/3) (-delta0))/(3*a*delta3) +
        // delta3/(3*2^(1/3)*a)
        result.append(Plus(b.negate().times(C3.times(a).power(CN1)),
            Times(Power(C2, C1D3), delta0, Power(Times(C3, a, delta3), CN1)),
            Times(Power(argDelta3.timesDistributed(C1D2), C1D3), C3.times(a).power(CN1))));

        // -(b/(3*a)) + ((1 + I Sqrt[3]) (-delta0))/(3*2^(2/3)*a*delta3)
        // - ((1 - I Sqrt[3]) delta3)/(6*2^(1/3)*a)
        result.append(Plus(Times(CN1, b, Power(Times(C3, a), CN1)),
            Times(Plus(C1, Times(CI, Sqrt(C3))), CN1, delta0,
                Power(Times(C3, Power(C2, fraction(2L, 3L)), a, delta3), CN1)),
            Times(CN1, Plus(C1, Times(CN1, CI, Sqrt(C3))), delta3,
                Power(Times(ZZ(6L), Power(C2, C1D3), a), CN1))));

        // -(b/(3*a)) + ((1 - I Sqrt[3]) (-delta0))/(3*2^(2/3)*a*delta3)
        // - ((1 + I Sqrt[3]) delta3)/(6*2^(1/3)*a)
        result.append(Plus(Times(CN1, b, Power(Times(C3, a), CN1)),
            Times(Plus(C1, Times(CN1, CI, Sqrt(C3))), CN1, delta0,
                Power(Times(C3, Power(C2, fraction(2L, 3L)), a, delta3), CN1)),
            Times(CN1, Plus(C1, Times(CI, Sqrt(C3))), delta3,
                Power(Times(ZZ(6L), Power(C2, C1D3), a), CN1))));
      }
      return createSet(result);
    }
  }


  public static IASTAppendable createSet(IASTAppendable result) {
    Set<IExpr> set1 = new TreeSet<IExpr>();
    for (int i = 1; i < result.size(); i++) {
      IExpr temp = result.get(i);
      if (temp.isPlus() || temp.isTimes() || temp.isPower()) {
        temp = F.evalExpandAll(temp); // org.matheclipse.core.reflection.system.PowerExpand.powerExpand((IAST)
        // temp, false);
      }
      if (temp.isAtom() && !temp.isIndeterminate()) {
        set1.add(temp);
        continue;
      }
      temp = F.eval(temp);
      if (temp.isAtom() && !temp.isIndeterminate()) {
        set1.add(temp);
        continue;
      }
      temp = F.evalExpandAll(temp);
      if (!temp.isIndeterminate()) {
        set1.add(temp);
      }
    }
    result = F.ListAlloc(set1.size());
    for (IExpr e : set1) {
      result.append(e);
    }
    return result;
  }

  /**
   * <code>Solve(a*x^2+b*x+c==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a>
   *
   * @param a coefficient for <code>x^2</code>
   * @param b coefficient for <code>x</code>
   * @param c coefficient for <code>1</code>
   * @return a list of solutions or {@link F#NIL}
   */
  public static IASTAppendable quadraticSolve(IExpr a, IExpr b, IExpr c) {
    return quadraticSolve(a, b, c, F.NIL, null, null);
  }

  /**
   * <code>Solve(a*x^2+b*x+c==0,x)</code>. See
   * <a href="http://en.wikipedia.org/wiki/Quadratic_equation">Wikipedia - Quadratic equation</a>
   *
   * @param a
   * @param b
   * @param c
   * @param solution1 possible first solution from
   *        {@link #cubicSolve(IExpr, IExpr, IExpr, IExpr, IExpr, IExpr)}
   * @param solution2 possible second solution from
   *        {@link #cubicSolve(IExpr, IExpr, IExpr, IExpr, IExpr, IExpr)}
   * @return a list of solutions or {@link F#NIL}
   */
  private static IASTAppendable quadraticSolve(IExpr a, IExpr b, IExpr c, IExpr variable,
      IExpr solution1, IExpr solution2) {
    IASTAppendable result = F.ListAlloc(5);
    if (solution1 != null) {
      result.append(solution1);
    }
    if (solution2 != null) {
      result.append(solution2);
    }
    if (!a.isPossibleZero(false)) {
      EvalEngine engine = EvalEngine.get();
      if (variable.isNIL()) {
        variable = F.x;
      }

      IExpr list = F.List();
      // All equations of the form $a \cdot x^2+b \cdot x+c=0$ can be solved using the quadratic
      // formula: $2 \cdot a - b \pm \sqrt{b \cdot 2 - 4 \cdot a \cdot c}$. The quadratic formula
      // gives two solutions, one when ± is addition and one when it is subtraction.
      IAST equation = F.Equal(Plus(Times(a, Power(F.x, 2)), Times(b, F.x), c), C0);
      engine.addTraceStep(//
          list, //
          equation, //
          F.List(S.QuarticSolve, F.$str("QuadraticFormulaStart"), a, b, c));

      // This equation is in standard form: $a \cdot x^2+b \cdot x+c=0$. Substitute `3` for a, `4`
      // for b, and `5` for c in the quadratic formula, $2 \cdot a - b \pm \sqrt{b \cdot 2 - 4 \cdot
      // a \cdot c}$.
      list = F.List(F.$str("a*x^2 + b*x + c"), F.Rule("a", a), F.Rule("b", b), F.Rule("c", c));
      engine.addTraceStep(//
          list, //
          equation, //
          F.List(S.QuarticSolve, F.$str("QuadraticFormulaStandard"), a, b, c));

      IExpr bNegated = b.negate();
      IExpr bSquared = F.Sqr(b);
      IExpr discriminant;
      if (!a.isOne()) {
        discriminant = Plus(bSquared, F.Times(F.CN4, a, c));
      } else {
        discriminant = Plus(bSquared, F.Times(F.CN4, c));
      }
      // Square `3`.
      IExpr quadraticFormula =
          F.Divide(F.PlusMinus(bNegated, F.Sqrt(discriminant)), F.Times(F.C2, a));
      if (!c.isPossibleZero(false)) {
        engine.addTraceInfoStep(//
            quadraticFormula, //
            F.List(S.QuarticSolve, F.$str("QuadraticFormulaSquareB"), b));
      }


      IExpr factor = F.CN4.times(a);

      // Multiply `3` times `4`.
      discriminant = Plus(bSquared, F.Times(F.CN4, a, c));
      quadraticFormula = F.Divide(F.PlusMinus(bNegated, F.Sqrt(discriminant)), F.Times(F.C2, a));
      if (!c.isPossibleZero(false)) {
        bSquared = b.times(b);
        engine.addTraceInfoStep(//
            quadraticFormula, //
            F.List(S.QuarticSolve, F.$str("QuadraticFormulaTimes"), F.CN4, a));
      }

      // Multiply `3` times `4`.
      discriminant = Plus(bSquared, F.Times(factor, c));
      quadraticFormula = F.Divide(F.PlusMinus(bNegated, F.Sqrt(discriminant)), F.Times(F.C2, a));
      if (!c.isPossibleZero(false)) {
        engine.addTraceInfoStep(//
            quadraticFormula, //
            F.List(S.QuarticSolve, F.$str("QuadraticFormulaTimes"), factor, c));
      }
      // }
      factor = factor.times(c);

      // Add `3` to `4`.
      discriminant = Plus(bSquared, factor);
      quadraticFormula = F.Divide(F.PlusMinus(bNegated, F.Sqrt(discriminant)), F.Times(F.C2, a));
      if (!c.isPossibleZero(false)) {
        engine.addTraceInfoStep(//
            quadraticFormula, //
            F.List(S.QuarticSolve, F.$str("QuadraticFormulaPlus"), bSquared, factor));
      }

      // Take the square root of `3`.
      discriminant = bSquared.plus(factor);
      quadraticFormula = F.Divide(F.PlusMinus(bNegated, F.Sqrt(discriminant)), F.Times(F.C2, a));
      engine.addTraceInfoStep(//
          quadraticFormula, //
          F.List(S.QuarticSolve, F.$str("QuadraticFormulaSqrt"), discriminant));

      // Multiply `3` times `4`.
      discriminant = engine.evaluate(F.Sqrt(discriminant));
      quadraticFormula = F.Divide(F.PlusMinus(bNegated, discriminant), F.Times(F.C2, a));
      engine.addTraceInfoStep(//
          quadraticFormula, //
          F.List(S.QuarticSolve, F.$str("QuadraticFormulaTimes"), F.C2, a));
      IExpr aDouble = F.C2.times(a);

      // Now solve the equation `3` when ± is plus. Add `4` to `5`.
      quadraticFormula = F.Divide(F.PlusMinus(bNegated, discriminant), aDouble);
      IExpr res1 = F.Times(bNegated.plus(discriminant), aDouble.pow(F.CN1));
      IExpr xEquation = F.Equal(variable, quadraticFormula);
      engine.addTraceInfoStep(//
          res1, //
          F.List(S.QuarticSolve, F.$str("QuadraticFormulaSolvePlus"), xEquation, bNegated,
              discriminant));

      result.append(Times(Plus(bNegated, discriminant), Power(a.times(F.C2), -1L)));

      // Now solve the equation `3` when ± is minus. Subtract `4` from `5`.
      // quadraticFormula = F.Divide(F.PlusMinus(bNegated, discriminant), aDouble);
      IExpr res2 = F.Times(bNegated.subtract(discriminant), aDouble.pow(F.CN1));
      // xEquation = F.Equal(variable, temp);
      engine.addTraceInfoStep(//
          res2, //
          F.List(S.QuarticSolve, F.$str("QuadraticFormulaSolveMinus"), xEquation, discriminant,
              bNegated));
      result.append(Times(Plus(bNegated, discriminant.negate()), Power(a.times(F.C2), -1L)));

      return result;

    }
    if (!b.isPossibleZero(false)) {
      return linearSolve(b, c, variable, result);
    }
    return result;
  }

  /**
   * Solve linear equation <code>a*x + b</code>
   *
   * @param a
   * @param b
   * @param variable TODO
   * @param result
   * @return
   */
  private static IASTAppendable linearSolve(IExpr a, IExpr b, IExpr variable,
      IASTAppendable result) {
    EvalEngine engine = EvalEngine.get();
    if (!result.isPresent()) {
      result = F.ListAlloc(5);
    }
    if (variable.isNIL()) {
      variable = F.x;
    }
    IExpr current = F.Equal(F.Plus(F.Times(a, variable), b), F.C0);
    IExpr bNegative = b.negative();
    if (b.isNegative()) {
      // Add `3` from both sides. Anything plus zero gives itself.
      engine.addTraceStep(S.None, current,
          F.List(S.QuarticSolve, F.$str("LinearEquationPlus"), bNegative));
    } else {
      // Subtract `3` from both sides. Anything subtracted from zero gives its negation
      engine.addTraceStep(S.None, current,
          F.List(S.QuarticSolve, F.$str("LinearEquationSubtract"), b));
    }

    IExpr bNegated = b.negate();
    if (!a.isOne()) {
      // Divide both sides by `3`.
      IExpr list = F.List(current);
      current = F.Equal(F.Times(a, variable), bNegated);
      engine.addTraceStep(list, current, F.List(S.QuarticSolve, F.$str("LinearEquationDivide"), a));

      current = F.Divide(bNegated, a);
      divideRational(current, bNegated, a);
    }

    IExpr expr = F.binaryAST2(S.Times, bNegated, Power(a, -1L));
    IExpr list = F.List(expr);
    // The solution is `3` = `4`.
    IExpr exprResult = engine.addEvaluatedTraceStep(list, current, S.QuarticSolve,
        F.$str("LinearResult"), variable);
    result.append(exprResult);
    return result;
  }

  /**
   * Divides a rational number by an integer if the conditions are met.
   *
   * @param current the current expression being evaluated
   * @param numerator the negated numerator of the rational number
   * @param denominator the denominator of the rational number
   */
  private static void divideRational(IExpr current, IExpr numerator, IExpr denominator) {
    IExpr list;
    list = F.List(current);
    IExpr exprResult = F.eval(current);
    if (numerator.isInteger() && denominator.isInteger() && exprResult.isRational()) {
      IRational rat = (IRational) exprResult;
      if (!rat.numerator().equals(numerator) || !rat.denominator().equals(denominator)) {
        // Divide `3` by `4` to get `5`.
        EvalEngine.get().addTraceStep(list, current,
            F.List(S.QuarticSolve, F.$str("Divide"), numerator, denominator, rat));
      }
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_4;
  }
}
