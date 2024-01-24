package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;

/**
 *
 *
 * <pre>
 * DSolve(equation, f(var), var)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * attempts to solve a linear differential <code>equation</code> for the function <code>f(var)
 * </code> and variable <code>var</code>.
 *
 * </blockquote>
 *
 * <p>
 * See:<br>
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Ordinary_differential_equation">Wikipedia - Ordinary
 * differential equation</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; DSolve({y'(x)==y(x)+2},y(x), x)
 * {{y(x)-&gt;-2+E^x*C(1)}}
 *
 * &gt;&gt;&gt; DSolve({y'(x)==y(x)+2,y(0)==1},y(x), x)
 * {{y(x)-&gt;-2+3*E^x}}
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Factor.md">Factor</a>, <a href="FindRoot.md">FindRoot</a>,
 * <a href="NRoots.md">NRoots</a>,<a href="Solve.md">Solve</a>
 */
public class DSolve extends AbstractFunctionEvaluator {

  public DSolve() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!ToggleFeature.DSOLVE) {
      return F.NIL;
    }
    IAST uFunction1Arg = F.NIL;
    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    IExpr xVar = ast.arg3();
    if (arg2.isAST1() && arg2.first().equals(xVar)) {
      uFunction1Arg = (IAST) arg2;
      if (arg1.isFree(arg2.head()) || arg1.isFree(xVar)) {
        return F.NIL;
      }
    } else if (arg2.isSymbol() && xVar.isSymbol()) {
      if (arg1.isFree(arg2) || arg1.isFree(xVar)) {
        return F.NIL;
      }
      uFunction1Arg = F.unaryAST1(arg2, xVar);
    }

    if (uFunction1Arg.isPresent()) {

      IASTAppendable listOfEquations = Validate.checkEquations(ast, 1).copyAppendable();
      IExpr[] boundaryCondition = null;
      int i = 1;
      while (i < listOfEquations.size()) {
        IExpr equation = listOfEquations.get(i);
        if (equation.isFree(xVar)) {
          boundaryCondition = solveSingleBoundary(equation, uFunction1Arg, xVar, engine);
          if (boundaryCondition != null) {
            listOfEquations.remove(i);
            break;
          }
        }
        i++;
      }

      if (uFunction1Arg.isAST1() && uFunction1Arg.arg1().equals(xVar)) {
        return unaryODE(uFunction1Arg, arg2, xVar, listOfEquations, boundaryCondition, engine);
      }
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  /**
   * Solve unary ODE.
   *
   * @param uFunction1Arg
   * @param arg2
   * @param xVar
   * @param listOfEquations
   * @param boundaryCondition
   * @param engine
   * @return
   */
  private IExpr unaryODE(IAST uFunction1Arg, IExpr arg2, IExpr xVar, IASTAppendable listOfEquations,
      IExpr[] boundaryCondition, EvalEngine engine) {
    IAST listOfVariables = F.list(uFunction1Arg);
    if (listOfEquations.size() == 2) {
      IExpr c_n = F.C(engine.incConstantCounter()); // constant C(n)
      try {
        IExpr equation = listOfEquations.arg1();
        IExpr temp = solveSingleODE(equation, xVar, listOfVariables, c_n, engine);
        if (temp.isNIL()) {
          temp = odeSolve(engine, equation, xVar, uFunction1Arg, c_n);
        }
        if (temp.isPresent()) {
          if (boundaryCondition != null) {
            IExpr res = F.subst(temp, F.list(F.Rule(xVar, boundaryCondition[0])));
            IExpr C1 = S.Roots.of(engine, F.Equal(res, boundaryCondition[1]), c_n);
            if (C1.isAST(S.Equal, 3, c_n)) {
              res = F.subst(temp, F.list(F.Rule(c_n, C1.second())));
              temp = res;
            }
          }
          if (arg2.isSymbol() && xVar.isSymbol()) {
            return F.list(F.list(F.Rule(arg2, F.Function(F.list(xVar), temp))));
          }
          return F.list(F.list(F.Rule(arg2, temp)));
        }
      } finally {
        engine.decConstantCounter();
      }
    }
    return F.NIL;
  }

  private IExpr solveSingleODE(IExpr equation, IExpr xVar, IAST listOfVariables, IExpr C_1,
      EvalEngine engine) {
    ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, listOfVariables);

    if (equation.isAST()) {
      IASTAppendable eq = ((IAST) equation).copyAppendable();
      if (!eq.isPlus()) {
        // create artificial Plus(...) expression
        eq = F.Plus(eq);
      }

      int j = 1;
      IAST[] deriveExpr = null;
      while (j < eq.size()) {
        IAST[] temp = eq.get(j).isDerivativeAST1();
        if (temp != null) {
          if (deriveExpr != null) {
            // TODO manage multiple Derive() functions in one
            // expression
            return F.NIL;
          }
          deriveExpr = temp;
          // eliminate deriveExpr from Plus(...) expression
          eq.remove(j);
          continue;
        }
        j++;
      }
      if (deriveExpr != null) {

        int order = derivativeOrder(deriveExpr);
        if (order < 0) {
          return F.NIL;
        }
        try {
          ExprPolynomial poly = ring.create(eq.oneIdentity0(), false, true, false);
          if (order == 1 && poly.degree() <= 1) {
            IAST coeffs = poly.coefficientList();
            IExpr q = coeffs.arg1(); // degree 0
            IExpr p = F.C0;
            if (poly.degree() == 1) {
              p = coeffs.arg2(); // degree 1
            }
            return linearODE(p, q, xVar, C_1, engine);
          }
        } catch (RuntimeException rex) {
          return Errors.printMessage(S.DSolve, rex, engine);
        }
      }
    }
    return F.NIL;
  }

  public static int derivativeOrder(IAST[] deriveExpr) {
    int order = -1;
    try {
      if (deriveExpr.length == 3) {
        if (deriveExpr[0].isAST1() && deriveExpr[0].arg1().isInteger()) {
          order = ((IInteger) deriveExpr[0].arg1()).toInt();
          // TODO check how and that the uFunction and
          // xVar is used in the derive expression...
        }
      }
    } catch (RuntimeException rex) {
      Errors.printMessage(S.DSolve, rex, EvalEngine.get());
    }
    return order;
  }

  /**
   * Equation <code>-1+y(0)</code> gives <code>[0, 1]</code> (representing the boundary equation
   * y(0)==1)
   *
   * @param equation the equation
   * @param uFunction1Arg function name <code>y(x)</code>
   * @param xVar variable <code>x</code>
   * @param engine
   * @return
   */
  private IExpr[] solveSingleBoundary(IExpr equation, IAST uFunction1Arg, IExpr xVar,
      EvalEngine engine) {
    if (equation.isAST()) {
      IASTAppendable eq = ((IAST) equation).copyAppendable();
      if (!eq.isPlus()) {
        // create artificial Plus(...) expression
        eq = F.Plus(eq);
      }

      int j = 1;
      IExpr uArg1 = null;
      IExpr head = uFunction1Arg.head();
      while (j < eq.size()) {
        // TODO check for negative expression (i.e. Times[-1, eq.get(j)]
        if (eq.get(j).isAST(head, uFunction1Arg.size())) {
          uArg1 = eq.get(j).first();
          eq.remove(j);
          continue;
        }
        j++;
      }
      if (uArg1 != null) {
        IExpr[] result = new IExpr[2];
        result[0] = uArg1;
        result[1] = engine.evaluate(eq.oneIdentity0().negate());
        return result;
      }
    }
    return null;
  }

  /**
   * Solve linear ODE.
   *
   * @param coefficient1 coefficient of degree 1
   * @param coefficient0 coefficient of degree 0
   * @param xVar variable
   * @param engine the evaluation engine
   * @return <code>F.NIL</code> if the evaluation was not possible
   */
  private IExpr linearODE(IExpr coefficient1, IExpr coefficient0, IExpr xVar, IExpr C_1,
      EvalEngine engine) {
    // integrate p
    IExpr pInt = engine.evaluate(F.Exp(F.Integrate(coefficient1, xVar)));

    if (coefficient0.isZero()) {
      return F.Divide(C_1, pInt) //
          .eval(engine);
    } else {
      IExpr qInt = engine
          .evaluate(F.Plus(C_1, F.Expand(F.Integrate(F.Times(F.CN1, coefficient0, pInt), xVar))));
      return F.Expand(F.Divide(qInt, pInt)) //
          .eval(engine);
    }
  }

  private static IExpr odeSolve(EvalEngine engine, IExpr w, IExpr x, IExpr y, IExpr C_1) {
    IExpr[] p = odeTransform(engine, w, x, y);
    if (p != null) {
      IExpr m = p[0];
      IExpr n = p[1];
      IExpr f = odeSeparable(engine, m, n, x, y, C_1);
      if (f.isPresent()) {
        return f;
      }
      // return exactSolve(engine, m, n, x, y);
    }
    return F.NIL;
  }

  private static IExpr[] odeTransform(EvalEngine engine, IExpr w, IExpr x, IExpr y) {
    IExpr v = S.Together.of(engine, w);
    IExpr numerator = S.Numerator.of(engine, v);
    IExpr dyx = S.D.of(engine, y, x);
    IExpr m = S.Coefficient.of(engine, numerator, dyx, F.C0);
    IExpr n = S.Coefficient.of(engine, numerator, dyx, F.C1);
    return new IExpr[] {m, n};
  }

  private static IExpr odeSeparable(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y,
      IExpr C_1) {
    if (n.isOne()) {
      IExpr fxExpr = F.NIL;
      IExpr gyExpr = F.NIL;
      if (m.isFree(y)) {
        gyExpr = F.C1;
        fxExpr = m;
      } else if (m.isTimes()) {
        IAST timesAST = (IAST) m;
        IASTAppendable fx = F.TimesAlloc(timesAST.size());
        IASTAppendable gy = F.TimesAlloc(timesAST.size());
        timesAST.forEach(expr -> {
          if (expr.isFree(y)) {
            fx.append(expr);
          } else {
            gy.append(expr);
          }
        });
        fxExpr = engine.evaluate(fx);
        gyExpr = engine.evaluate(gy);
      }
      if (fxExpr.isPresent() && gyExpr.isPresent()) {
        gyExpr = S.Integrate.of(engine, gyExpr.inverse(), y);
        fxExpr = S.Plus.of(engine, F.Integrate(F.Times(F.CN1, fxExpr), x), C_1);
        IExpr yEquation = S.Subtract.of(engine, gyExpr, fxExpr);
        IExpr result = Eliminate.extractVariable(yEquation, y, false, engine);
        if (result.isPresent()) {
          return engine.evaluate(result);
        }
      }
    }
    return F.NIL;
  }

  /**
   * An implicit solution to the differential equation <code>m + n*(dy/dx) == 0</code> or <code>
   * F.NIL</code>.
   *
   * @param m algebraic expression
   * @param n algebraic expression
   * @param x symbol
   * @param y symbol
   * @return
   */
  private static IExpr exactSolve(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y) {

    if (n.isZero()) {
      return F.NIL;
    }
    if (m.isZero()) {
      return F.Equal(y, S.CSymbol);
    }
    IExpr my = engine.evaluate(F.D(m, y));
    IExpr nx = engine.evaluate(F.D(n, x));

    IExpr d = engine.evaluate(F.Subtract(my, nx));

    IExpr u = F.NIL;
    if (d.isZero()) {
      u = F.C1;
    } else {
      IExpr f = engine.evaluate(F.Together(F.Divide(d, n)));
      if (f.isFree(y)) {
        u = engine.evaluate(F.Exp(F.Integrate(f, x)));
        d = F.C0;
      } else {
        IExpr g = engine.evaluate(F.Simplify(F.Divide(d.negate(), m)));
        if (g.isFree(x)) {
          u = engine.evaluate(F.Exp(F.Integrate(g, y)));
          d = F.C0;
        }
      }
    }

    if (d.isZero()) {
      IExpr g = engine.evaluate(F.Integrate(F.Times(u, m), x));
      IExpr hp = engine.evaluate(F.Subtract(F.Times(u, n), F.D(g, y)));
      IExpr h = engine.evaluate(F.Integrate(hp, y));
      return F.Equal(engine.evaluate(F.Plus(g, h)), S.CSymbol);
    }

    return F.NIL;
  }
}
