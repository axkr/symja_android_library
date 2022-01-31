package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.ode.AbstractIntegrator;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.OrdinaryDifferentialEquation;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See: <a href="https://en.wikipedia.org/wiki/Ordinary_differential_equation">Wikipedia:Ordinary
 * differential equation</a>
 */
public class NDSolve extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  private static class FirstODE implements OrdinaryDifferentialEquation {
    private final EvalEngine fEngine;
    private final IExpr[] fDotEquations;
    private final int fDimension;
    private final IAST fVariables;
    private final ISymbol fT;

    public FirstODE(EvalEngine engine, IExpr[] dotEquations, IAST variables, ISymbol t) {
      this.fEngine = engine;
      this.fDotEquations = dotEquations;
      this.fDimension = fDotEquations.length;
      this.fVariables = variables;
      this.fT = t;
    }

    @Override
    public int getDimension() {
      return fDimension;
    }

    @Override
    public double[] computeDerivatives(double t, double[] xyz) {
      double[] xyzDot = new double[fDimension];
      IExpr[] replacements = new IExpr[fDimension];
      IASTAppendable rules = F.ListAlloc(fDimension + 1);
      for (int i = 0; i < fDimension; i++) {
        replacements[i] = F.$(fVariables.get(i + 1), fT);
        rules.append(F.Rule(replacements[i], F.num(xyz[i])));
      }
      rules.append(F.Rule(fT, F.num(t)));
      IExpr[] dotEquations = new IExpr[fDimension];
      for (int i = 0; i < fDimension; i++) {
        dotEquations[i] = fDotEquations[i].replaceAll(rules);
      }
      for (int i = 0; i < fDimension; i++) {
        xyzDot[i] = ((INum) fEngine.evalN(dotEquations[i])).doubleValue();
      }
      return xyzDot;
    }
  }

  public NDSolve() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!ToggleFeature.DSOLVE) {
      return F.NIL;
    }
    if (ast.arg3().isList()) {
      final IAST tRangeList = (IAST) ast.arg3();
      if (!(tRangeList.isAST2() || tRangeList.isAST3())) {
        return F.NIL;
      }
      try {
        final IAST listOfVariables =
            Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
        if (!listOfVariables.isPresent()) {
          return F.NIL;
        }
        final int numberOfVariables = listOfVariables.argSize();

        final ISymbol timeVar = (ISymbol) tRangeList.arg1();
        IExpr tMinExpr = F.C0;
        IExpr tMaxExpr = tRangeList.arg2();
        if (tRangeList.isAST3()) {
          tMinExpr = tRangeList.arg2();
          tMaxExpr = tRangeList.arg3();
        }
        final double tMin = tMinExpr.evalDouble();
        final double tMax = tMaxExpr.evalDouble();
        final double tStep = 0.1;
        IASTAppendable listOfEquations = Validate.checkEquations(ast, 1).copyAppendable();
        IExpr[][] boundaryCondition = new IExpr[2][numberOfVariables];
        int i = 1;
        while (i < listOfEquations.size()) {
          IExpr equation = listOfEquations.get(i);
          if (equation.isFree(timeVar)) {
            if (determineSingleBoundary(equation, listOfVariables, timeVar, boundaryCondition,
                engine)) {
              listOfEquations.remove(i);
              continue;
            }
          }
          i++;
        }

        IExpr[] dotEquations = new IExpr[numberOfVariables];
        i = 1;
        while (i < listOfEquations.size()) {
          IExpr equation = listOfEquations.get(i);
          if (!equation.isFree(timeVar)) {
            if (determineSingleDotEquation(equation, listOfVariables, timeVar, dotEquations,
                engine)) {
              listOfEquations.remove(i);
              continue;
            }
          }
          i++;
        }

        if (listOfVariables.isList()) {
          AbstractIntegrator abstractIntegrator =
              new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
          // AbstractIntegrator abstractIntegrator = new ClassicalRungeKuttaIntegrator(1.0);
          double[] primaryState = new double[numberOfVariables];
          for (int j = 0; j < numberOfVariables; j++) {
            primaryState[j] = ((INum) engine.evalN(boundaryCondition[1][j])).doubleValue();
          }

          OrdinaryDifferentialEquation ode =
              new FirstODE(engine, dotEquations, listOfVariables, timeVar);

          if (listOfVariables.size() > 1) {
            IASTAppendable[] resultLists = new IASTAppendable[numberOfVariables];
            for (int j = 0; j < primaryState.length; j++) {
              resultLists[j] = F.ListAlloc();
            }
            for (double time = tMin; time < tMax; time += tStep) {
              final ODEStateAndDerivative finalstate =
                  abstractIntegrator.integrate(ode, new ODEState(time, primaryState), time + tStep);
              primaryState = finalstate.getPrimaryState();

              for (int j = 0; j < primaryState.length; j++) {
                resultLists[j].append(F.List(F.num(time), F.num(primaryState[j])));
              }
            }

            IASTAppendable primaryList = F.ListAlloc();
            IASTAppendable secondaryList = F.ListAlloc();
            for (int j = 1; j < listOfVariables.size(); j++) {
              secondaryList.append(F.Rule(listOfVariables.get(j),
                  engine.evaluate(F.Interpolation(resultLists[j - 1]))));
            }
            primaryList.append(secondaryList);
            return primaryList;
          }
        }

      } catch (LimitException le) {
        throw le;
      } catch (RuntimeException rex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  /**
   * Equation <code>-1+y(0)</code> gives <code>[0, 1]</code> (representing the boundary equation
   * y(0)==1)
   *
   * @param equation the equation
   * @param uFunctionSymbols
   * @param xVar
   * @param engine
   * @return
   */
  private static boolean determineSingleBoundary(IExpr equation, IAST uFunctionSymbols, IExpr xVar,
      IExpr boundaryCondition[][], EvalEngine engine) {
    if (equation.isAST()) {
      IASTAppendable eq = ((IAST) equation).copyAppendable();
      if (!eq.isPlus()) {
        // create artificial Plus(...) expression
        eq = F.Plus(eq);
      }

      int j = 1;
      IExpr uArg1 = null;
      IASTAppendable rest = F.PlusAlloc(16);
      boolean negate;
      while (j < eq.size()) {
        IExpr temp = eq.get(j);
        for (int i = 1; i < uFunctionSymbols.size(); i++) {
          negate = true;
          if (temp.isAST2() && temp.first().isMinusOne()) {
            temp = temp.second();
            negate = false;
          }
          if (temp.isAST(uFunctionSymbols.get(i))) {
            uArg1 = temp.first();
            if (boundaryCondition[0][i - 1] != null) {
              return false;
            }
            boundaryCondition[0][i - 1] = uArg1;
            return removeDeriveFromPlus(boundaryCondition[1], i - 1, eq, j, rest, negate, engine);
          }
        }
        rest.append(eq.get(j));
        j++;
      }
    }
    return false;
  }

  private static boolean determineSingleDotEquation(IExpr equation, IAST uFunctionSymbols,
      IExpr xVar, IExpr dotEquations[], EvalEngine engine) {
    if (equation.isAST()) {
      IASTAppendable eq = ((IAST) equation).copyAppendable();
      if (!eq.isPlus()) {
        // create artificial Plus(...) expression
        eq = F.Plus(eq);
      }

      int j = 1;
      IASTAppendable rest = F.PlusAlloc(16);
      boolean negate;
      while (j < eq.size()) {
        negate = true;
        IExpr temp = eq.get(j);
        if (temp.isAST2() && temp.first().isMinusOne()) {
          temp = temp.second();
          negate = false;
        }
        IAST[] deriveExpr = temp.isDerivativeAST1();
        if (deriveExpr != null) {
          for (int i = 1; i < uFunctionSymbols.size(); i++) {
            if (deriveExpr[1].arg1().equals(uFunctionSymbols.get(i))) {
              if (DSolve.derivativeOrder(deriveExpr) != 1) {
                return false;
              }
              if (dotEquations[i - 1] != null) {
                return false;
              }
              return removeDeriveFromPlus(dotEquations, i - 1, eq, j, rest, negate, engine);
            }
          }
        } else {
          rest.append(eq.get(j));
        }
        j++;
      }
    }
    return false;
  }

  /**
   * @param dotEquations
   * @param i the index in dotEquations which should be filled with the evaluated result from <code>
   *     expr</code>
   * @param plusAST the <code>Plus(...)</code> expression
   * @param j the index which should be removed in <code>plusAST</code>
   * @param expr
   * @param negate
   * @param engine
   * @return
   */
  private static boolean removeDeriveFromPlus(IExpr[] dotEquations, int i, IASTAppendable plusAST,
      int j, IASTAppendable expr, boolean negate, EvalEngine engine) {
    IExpr temp = negate ? expr.oneIdentity0().negate() : expr.oneIdentity0();
    dotEquations[i] = engine.evaluate(temp);
    // eliminate deriveExpr from Plus(...) expression
    plusAST.remove(j);
    return true;
  }
}
