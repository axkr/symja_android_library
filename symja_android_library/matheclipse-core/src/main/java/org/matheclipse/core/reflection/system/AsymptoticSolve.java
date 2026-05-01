package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * AsymptoticSolve(eqn, y -&gt; b, x -&gt; a)
 * </pre>
 *
 * <blockquote>
 * <p>
 * Computes asymptotic approximations of solutions <code>y(x)</code> of the equation
 * <code>eqn</code> passing through <code>{a, b}</code>.
 * </p>
 * </blockquote>
 */
public class AsymptoticSolve extends AbstractFunctionOptionEvaluator {

  public AsymptoticSolve() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {

    // 1. Parse equations
    IAST eqns = ast.arg1().isList() ? (IAST) ast.arg1() : F.List(ast.arg1());

    // 2. Parse target variables and optional base points (y -> b)
    IAST yList = ast.arg2().isList() ? (IAST) ast.arg2() : F.List(ast.arg2());
    IASTAppendable yVars = F.ListAlloc(yList.argSize());
    IASTAppendable yBases = F.ListAlloc(yList.argSize());

    for (int i = 1; i <= yList.argSize(); i++) {
      IExpr yItem = yList.get(i);
      if (yItem.isRule()) {
        yVars.append(yItem.first());
        yBases.append(yItem); // Store Rule(y, b)
      } else {
        yVars.append(yItem);
      }
    }

    // 3. Parse domain specification (e.g. Reals)
    IExpr domain = S.Complexes;
    if (argSize >= 4 && ast.arg4().isSymbol()) {
      domain = ast.arg4();
    }

    // 4. Parse expansion points and series order
    IASTAppendable xSpecs = F.ListAlloc();
    int order = 3; // Default SeriesTermGoal fallback if not supplied
    if (options != null && options.length > 5 && options[5].isInteger()) {
      order = options[5].toIntDefault();
    }

    IExpr arg3 = ast.arg3();
    if (arg3.isRule()) {
      xSpecs.append(F.List(arg3.first(), arg3.second(), F.ZZ(order)));
    } else if (arg3.isList()) {
      IAST list = (IAST) arg3;
      if (list.argSize() >= 2 && list.arg1().isList() && list.arg2().isList()) {
        // Format: {{x1, x2}, {a1, a2}, n}
        IAST xList = (IAST) list.arg1();
        IAST aList = (IAST) list.arg2();
        if (list.argSize() >= 3 && list.arg3().isInteger()) {
          order = list.arg3().toIntDefault();
        }
        for (int i = 1; i <= xList.argSize() && i <= aList.argSize(); i++) {
          xSpecs.append(F.List(xList.get(i), aList.get(i), F.ZZ(order)));
        }
      } else if (list.argSize() >= 2 && list.arg1().isSymbol()) {
        // Format: {x, a, n}
        if (list.argSize() >= 3 && list.arg3().isInteger()) {
          order = list.arg3().toIntDefault();
        }
        xSpecs.append(F.List(list.arg1(), list.arg2(), F.ZZ(order)));
      } else {
        // Format: {x1 -> a1, x2 -> a2}
        for (int i = 1; i <= list.argSize(); i++) {
          IExpr item = list.get(i);
          if (item.isRule()) {
            xSpecs.append(F.List(item.first(), item.second(), F.ZZ(order)));
          }
        }
      }
    }

    if (yVars.argSize() == 0 || xSpecs.argSize() == 0) {
      return F.NIL;
    }

    // ==============================================================================
    // STRATEGY 1: EXACT SOLUTION -> SERIES EXPANSION
    // ==============================================================================
    IAST solveCall = domain.equals(S.Reals) ? F.Solve(eqns, yVars, domain) : F.Solve(eqns, yVars);
    IExpr solveRes = engine.evaluate(solveCall);

    if (solveRes.isList() && !solveRes.isAST(S.Solve)) {
      IASTAppendable finalSols = F.ListAlloc();

      for (int b = 1; b <= ((IAST) solveRes).argSize(); b++) {
        IExpr branch = ((IAST) solveRes).get(b);

        if (branch.isList()) {
          IASTAppendable newBranch = F.ListAlloc();
          boolean validBranch = true;

          for (int r = 1; r <= ((IAST) branch).argSize(); r++) {
            IExpr rule = ((IAST) branch).get(r);

            if (rule.isRule()) {
              IExpr yVar = rule.first();
              IExpr ySol = rule.second();

              // Verify against expected base value (b) if it was supplied
              IExpr expectedB = F.NIL;
              for (int ybi = 1; ybi <= yBases.argSize(); ybi++) {
                if (((IAST) yBases.get(ybi)).first().equals(yVar)) {
                  expectedB = ((IAST) yBases.get(ybi)).second();
                  break;
                }
              }

              // Compute the exact limit of the branch at the expansion points
              IExpr limitB = ySol;
              for (int xi = 1; xi <= xSpecs.argSize(); xi++) {
                IAST spec = (IAST) xSpecs.get(xi);
                limitB = engine.evaluate(F.Limit(limitB, F.Rule(spec.arg1(), spec.arg2())));
              }

              if (expectedB.isPresent()) {
                if (expectedB.isDirectedInfinity() || expectedB.isInfinity()
                    || expectedB.isNegativeInfinity()) {
                  if (!limitB.equals(expectedB) && !(limitB.isInfinity())
                      && !(expectedB.isInfinity())) {
                    validBranch = false;
                    break;
                  }
                } else {
                  IExpr diffCheck = engine.evaluate(F.Simplify(F.Subtract(limitB, expectedB)));
                  if (!diffCheck.isZero()) {
                    validBranch = false;
                    break;
                  }
                }
              } else {
                // If base is not specified, discard any branches that diverge (Infinite limit)
                if (limitB.isDirectedInfinity() || limitB.isInfinity()
                    || limitB.isNegativeInfinity() || limitB.isIndeterminate()) {
                  validBranch = false;
                  break;
                }
              }

              // ----------------------------------------------------------------
              // Guard for expansion at Infinity / -Infinity:
              //
              // Sub-check 1 (FunctionSingularities):
              // Substitute t = 1/x into ySol and call FunctionSingularities(ySolT, t, Reals).
              // If the returned singularity condition evaluates to True at t=0 (i.e. x→∞),
              // the exact solution has a pole at infinity → reject.
              // Example: (x²+1)/(x-1) → under t=1/x becomes (1/t²+1)/(1/t-1) which has
              // a pole at t=0 → rejected (complementing the existing limit check).
              //
              // Sub-check 2 (Rationality):
              // Reject non-rational (transcendental) exact solutions at infinity.
              // Although Exp(1/x) has a finite limit of 1 at infinity, it is transcendental
              // in x and its infinite series is not a proper asymptotic polynomial expansion.
              // Example: Exp(1/x) → Together→Exp(1/x), PolynomialQ(Exp(1/x), x) = False → rejected.
              // ----------------------------------------------------------------
              for (int xi = 1; xi <= xSpecs.argSize() && validBranch; xi++) {
                IAST spec = (IAST) xSpecs.get(xi);
                IExpr xVarI = spec.arg1();
                IExpr x0I = spec.arg2();

                if (x0I.isInfinity() || x0I.isNegativeInfinity()) {
                  // Sub-check 1: FunctionSingularities via t = 1/x substitution
                  IExpr tVar = F.Dummy("t");
                  IExpr ySolT = engine.evaluate(F.subst(ySol, xVarI, F.Power(tVar, F.CN1)));
                  IExpr singCond =
                      engine.evaluate(F.ternaryAST3(S.FunctionSingularities, ySolT, tVar, S.Reals));
                  if (!singCond.isFalse()) {
                    IExpr singAtZero = engine.evaluate(F.subst(singCond, tVar, F.C0));
                    if (engine.evaluate(F.Simplify(singAtZero)).isTrue()) {
                      validBranch = false;
                      break;
                    }
                  }

                  // Sub-check 2: Reject non-rational (transcendental) exact solutions
                  if (validBranch) {
                    IExpr together = engine.evaluate(F.Together(ySol));
                    boolean isRational = engine
                        .evaluate(F.PolynomialQ(F.Numerator(together), xVarI)).isTrue()
                        && engine.evaluate(F.PolynomialQ(F.Denominator(together), xVarI)).isTrue();
                    if (!isRational) {
                      validBranch = false;
                    }
                  }
                }
              }

              if (!validBranch) {
                break;
              }

              // Compute Series sequentially over independent variables
              IExpr seriesSol = ySol;
              for (int xi = 1; xi <= xSpecs.argSize(); xi++) {
                IAST spec = (IAST) xSpecs.get(xi);
                seriesSol = engine.evaluate(F.Series(seriesSol, spec));
                // Abort if the series failed to evaluate
                if (seriesSol.isAST(S.Series)) {
                  validBranch = false;
                  break;
                }
                seriesSol = engine.evaluate(seriesSol.normal(false));
              }

              if (!validBranch)
                break;

              seriesSol = engine.evaluate(F.Simplify(seriesSol));
              newBranch.append(F.Rule(yVar, seriesSol));
            }
          }

          if (validBranch && newBranch.argSize() > 0) {
            finalSols.append(newBranch);
          }
        }
      }

      // Explicitly return the finalSols (even if empty {}) since we comprehensively
      // mapped the exact solutions and filtered out invalid/divergent branches.
      return finalSols;
    }

    // ==============================================================================
    // STRATEGY 2: ALGEBRAIC FALLBACK (Method of Undetermined Coefficients)
    // Invoked if Exact Solve fails (e.g. higher order quintic algebraic curves).
    // Supported natively for the primary univariate case (1 unknown, 1 curve).
    // ==============================================================================
    if (eqns.argSize() == 1 && yVars.argSize() == 1 && xSpecs.argSize() == 1) {
      IExpr eq = eqns.arg1();
      IExpr yVar = yVars.arg1();
      IAST xSpec = (IAST) xSpecs.arg1();
      IExpr xVar = xSpec.arg1();
      IExpr x0 = xSpec.arg2();

      IASTAppendable possibleBases = F.ListAlloc();
      IExpr givenBase = F.NIL;
      for (int i = 1; i <= yBases.argSize(); i++) {
        if (((IAST) yBases.get(i)).first().equals(yVar)) {
          givenBase = ((IAST) yBases.get(i)).second();
          break;
        }
      }

      if (givenBase.isPresent()) {
        possibleBases.append(givenBase);
      } else {
        // Formulate y bases by evaluating the target curve at local limit point x0
        IExpr eqAtX0;
        if (x0.isInfinity() || x0.isNegativeInfinity()) {
          eqAtX0 = engine.evaluate(F.Limit(eq, F.Rule(xVar, x0)));
        } else {
          eqAtX0 = engine.evaluate(F.subst(eq, xVar, x0));
        }

        IExpr rootSols = engine.evaluate(F.Solve(eqAtX0, F.List(yVar)));
        if (rootSols.isList() && !rootSols.isAST(S.Solve)) {
          for (int i = 1; i <= ((IAST) rootSols).argSize(); i++) {
            IExpr branch = ((IAST) rootSols).get(i);
            if (branch.isList() && ((IAST) branch).argSize() > 0) {
              IExpr rule = ((IAST) branch).arg1();
              if (rule.isRule()) {
                possibleBases.append(rule.second());
              }
            }
          }
        } else {
          return F.NIL; // Root formulation failed mathematically
        }
      }

      IASTAppendable finalAlgebraicSols = F.ListAlloc();

      for (int b = 1; b <= possibleBases.argSize(); b++) {
        IExpr baseB = possibleBases.get(b);

        // Build series polynomial ansatz: y(x) = b + c1*z + c2*z^2 ...
        IExpr z = (x0.isInfinity()) ? F.Power(xVar, F.CN1) : F.Subtract(xVar, x0);
        IASTAppendable cVars = F.ListAlloc();
        IASTAppendable ansatzPlus = F.PlusAlloc();

        ansatzPlus.append(baseB);
        for (int i = 1; i <= order; i++) {
          IExpr c_i = F.Dummy("c" + i);
          cVars.append(c_i);
          ansatzPlus.append(F.Times(c_i, F.Power(z, F.ZZ(i))));
        }
        IExpr ansatz = engine.evaluate(ansatzPlus);

        // Substitute into root equation
        IExpr subbed = engine.evaluate(F.subst(eq, yVar, ansatz));
        IExpr lhs = subbed.isEqual() ? F.Subtract(subbed.first(), subbed.second()) : subbed;

        // Force evaluate at local tangent mapped to zDummy -> 0
        IExpr zDummy = F.Dummy("z");
        IExpr zSub;
        if (x0.isInfinity()) {
          zSub = engine.evaluate(F.subst(lhs, xVar, F.Power(zDummy, F.CN1)));
        } else {
          zSub = engine.evaluate(F.subst(lhs, xVar, F.Plus(x0, zDummy)));
        }

        IExpr seriesZ = engine.evaluate(F.Series(zSub, F.List(zDummy, F.C0, F.ZZ(order))));
        if (seriesZ.isAST(S.Series)) {
          zSub = engine.evaluate(F.Simplify(zSub));
          seriesZ = engine.evaluate(F.Series(zSub, F.List(zDummy, F.C0, F.ZZ(order))));
        }

        IExpr normalZ = engine.evaluate(seriesZ.normal(false));
        IExpr collectedZ = engine.evaluate(F.Collect(normalZ, zDummy));

        IASTAppendable algebraicConstraints = F.ListAlloc();
        for (int k = 0; k <= order; k++) {
          IExpr coeff = engine.evaluate(F.Coefficient(collectedZ, zDummy, F.ZZ(k)));
          if (!coeff.isZero()) {
            algebraicConstraints.append(F.Equal(coeff, F.C0));
          }
        }

        IAST solveC = F.Solve(algebraicConstraints, cVars);
        IExpr solC = engine.evaluate(solveC);

        if (solC.isList() && ((IAST) solC).argSize() > 0) {
          IAST firstSol = (IAST) ((IAST) solC).arg1();
          IExpr finalSeries = engine.evaluate(F.subst(ansatz, firstSol));

          // Package any residual unspecified generic coefficients safely into canonical notation
          int cIndex = engine.incConstantCounter();
          IASTAppendable finalRules = F.ListAlloc();
          for (int i = 1; i <= cVars.argSize(); i++) {
            IExpr c = cVars.get(i);
            if (!finalSeries.isFree(c)) {
              finalRules.append(F.Rule(c, F.C(cIndex++)));
            }
          }
          if (finalRules.argSize() > 0) {
            finalSeries = engine.evaluate(F.subst(finalSeries, finalRules));
          }

          finalAlgebraicSols.append(F.List(F.Rule(yVar, finalSeries)));
        }
      }

      return finalAlgebraicSols;
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_4;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Assumptions, S.Direction, S.GenerateConditions, S.Method,
            S.PerformanceGoal, S.SeriesTermGoal}, //
        new IExpr[] {S.$Assumptions, S.Automatic, S.Automatic, S.Automatic, S.$PerformanceGoal,
            F.C3});
  }
}
