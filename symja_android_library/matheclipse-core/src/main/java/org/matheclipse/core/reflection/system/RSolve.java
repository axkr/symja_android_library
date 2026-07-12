package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * RSolve(equation, a(n), n)
 * </pre>
 */
public class RSolve extends AbstractFunctionEvaluator {

  private static void extractCVars(IExpr expr, IASTAppendable cVars) {
    if (expr.isAST(S.C, 2)) {
      boolean found = false;
      for (int i = 1; i <= cVars.argSize(); i++) {
        if (cVars.get(i).equals(expr)) {
          found = true;
          break;
        }
      }
      if (!found)
        cVars.append(expr);
    } else if (expr.isAST()) {
      ((IAST) expr).forEach(arg -> extractCVars(arg, cVars));
    }
  }

  // ====================================================================================
  // Canonical Sequences (Fibonacci & LucasL Mapping)
  // Direct Orthogonal Basis Projection for Golden Ratio polynomials
  // ====================================================================================
  private static IExpr recognizeFibonacciAndLucas(IExpr expr, IExpr nVar, EvalEngine engine) {
    if (expr.isFree(x -> x.isSqrt() || x == S.GoldenRatio, false)) {
      return F.NIL;
    }

    IASTAppendable rules = F.ListAlloc();
    ISymbol xSym = F.Dummy("x");
    IPattern xPat = F.$p(xSym);

    IExpr fibX = F.unaryAST1(S.Fibonacci, xSym);
    IExpr lucasX = F.unaryAST1(S.LucasL, xSym);

    // Identity: phi^n = 1/2 * L_n + Sqrt(5)/2 * F_n
    // Identity: psi^n = 1/2 * L_n - Sqrt(5)/2 * F_n
    IExpr phiSub = F.Plus(F.Times(F.C1D2, lucasX), F.Times(F.C1D2, F.CSqrt5, fibX));
    IExpr psiSub = F.Subtract(F.Times(F.C1D2, lucasX), F.Times(F.C1D2, F.CSqrt5, fibX));

    // Precompute exact canonical golden ratio bases
    IExpr phi = engine.evaluate(F.Times(F.C1D2, F.Plus(F.C1, F.CSqrt5)));
    IExpr psi = engine.evaluate(F.Times(F.C1D2, F.Subtract(F.C1, F.CSqrt5)));

    rules.append(F.RuleDelayed(F.Power(phi, xPat), phiSub));
    rules.append(F.RuleDelayed(engine.evaluate(F.Power(phi, xPat)), phiSub));

    rules.append(F.RuleDelayed(F.Power(psi, xPat), psiSub));
    rules.append(F.RuleDelayed(engine.evaluate(F.Power(psi, xPat)), psiSub));

    // mathematically equivalent to phi/psi but structurally different
    IExpr phiExp = engine.evaluate(F.Plus(F.C1D2, F.Times(F.C1D2, F.CSqrt5)));
    IExpr psiExp = engine.evaluate(F.Subtract(F.C1D2, F.Times(F.C1D2, F.CSqrt5)));

    rules.append(F.RuleDelayed(F.Power(phiExp, xPat), phiSub));
    rules.append(F.RuleDelayed(engine.evaluate(F.Power(phiExp, xPat)), phiSub));

    rules.append(F.RuleDelayed(F.Power(psiExp, xPat), psiSub));
    rules.append(F.RuleDelayed(engine.evaluate(F.Power(psiExp, xPat)), psiSub));

    IExpr mapped = engine.evaluate(F.subst(expr, rules));

    if (!mapped.equals(expr)) {
      mapped = engine.evaluate(F.ExpandAll(mapped));
      mapped = engine.evaluate(F.Simplify(mapped));
      return engine.evaluate(F.subst(mapped, xSym, nVar));
    }

    return F.NIL;
  }

  public RSolve() {}

  // ====================================================================================
  // Arbitrary Constant Rebaser (Canonical C(1), C(2) re-indexing)
  // Automatically absorbs fractional messy constants into fresh C identifiers.
  // Bypass Engine: If no overlapping constants exist, it preserves original AST structure!
  // ====================================================================================
  private IExpr absorbArbitraryConstants(IExpr expr, IExpr nVar, EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.ExpandAll(expr));
    IAST plus;
    if (expanded.isPlus()) {
      plus = (IAST) expanded;
    } else {
      plus = F.Plus(expanded);
    }

    IASTAppendable cVars = F.ListAlloc();
    extractCVars(expanded, cVars);
    if (cVars.argSize() == 0)
      return expr;

    Map<IExpr, IASTAppendable> basisMap = new HashMap<>();

    for (IExpr term : plus) {
      IExpr coeff = F.C1;
      IExpr basis = term;

      if (term.isTimes()) {
        IASTAppendable cTimes = F.TimesAlloc();
        IASTAppendable bTimes = F.TimesAlloc();
        for (IExpr arg : (IAST) term) {
          if (arg.isFree(nVar)) {
            cTimes.append(arg);
          } else {
            bTimes.append(arg);
          }
        }
        coeff = cTimes.argSize() == 1 ? cTimes.arg1() : (cTimes.argSize() == 0 ? F.C1 : cTimes);
        basis = bTimes.argSize() == 1 ? bTimes.arg1() : (bTimes.argSize() == 0 ? F.C1 : bTimes);
      } else if (term.isFree(nVar)) {
        coeff = term;
        basis = F.C1;
      }

      basisMap.putIfAbsent(basis, F.PlusAlloc());
      basisMap.get(basis).append(coeff);
    }

    // Check if any actual constant absorption is required
    boolean collapsed = false;
    for (IASTAppendable sumOfTerms : basisMap.values()) {
      if (sumOfTerms.argSize() > 1) {
        IExpr coeffSum = engine.evaluate(sumOfTerms);
        for (int i = 1; i <= cVars.argSize(); i++) {
          if (!coeffSum.isFree(cVars.get(i))) {
            collapsed = true;
            break;
          }
        }
      }
    }

    if (!collapsed) {
      // No mathematical basis coefficients were merged.
      // Re-index the constants in the original expression to preserve its exact structure!
      IASTAppendable rules = F.ListAlloc();
      int cCount = 1;

      List<IExpr> sortedCVars = new ArrayList<>();
      for (int i = 1; i <= cVars.argSize(); i++)
        sortedCVars.add(cVars.get(i));
      sortedCVars.sort((a, b) -> a.toString().compareTo(b.toString()));

      for (IExpr c : sortedCVars) {
        rules.append(F.Rule(c, F.C(cCount++)));
      }
      return rules.argSize() > 0 ? engine.evaluate(F.subst(expr, rules)) : expr;
    }

    // ... Proceed with destructive collapse if terms overlapped ...
    IASTAppendable result = F.PlusAlloc();
    int cCount = 1;

    List<IExpr> sortedBases = new ArrayList<>(basisMap.keySet());
    sortedBases.sort((a, b) -> a.toString().compareTo(b.toString()));

    for (IExpr basis : sortedBases) {
      IASTAppendable sumOfTerms = basisMap.get(basis);
      IExpr coeffSum = engine.evaluate(sumOfTerms);

      boolean hasC = false;
      for (int i = 1; i <= cVars.argSize(); i++) {
        if (!coeffSum.isFree(cVars.get(i))) {
          hasC = true;
          break;
        }
      }

      if (hasC) {
        if (sumOfTerms.argSize() > 1) {
          result.append(F.Times(F.C(cCount++), basis));
        } else {
          IExpr singleCoeff = sumOfTerms.arg1();
          IExpr freshC = F.C(cCount++);
          for (int i = 1; i <= cVars.argSize(); i++) {
            singleCoeff = engine.evaluate(F.subst(singleCoeff, cVars.get(i), freshC));
          }
          result.append(F.Times(singleCoeff, basis));
        }
      } else {
        result.append(F.Times(coeffSum, basis));
      }
    }

    // Final polish to ensure any freshly combined coefficients are neatly factored together
    IExpr evaluatedResult = engine.evaluate(result);
    IExpr factored = engine.evaluate(F.Factor(evaluatedResult));
    if (factored.leafCountSimplify() < evaluatedResult.leafCountSimplify()) {
      return factored;
    }
    return evaluatedResult;
  }

  // ====================================================================================
  // Hypergeometric Integration Constant Absorption
  // ====================================================================================
  private IExpr absorbIntegrationConstants(IExpr expr, IExpr cVar, EvalEngine engine) {
    if (expr.isFree(cVar))
      return expr;

    IExpr coeff = engine.evaluate(F.Coefficient(expr, cVar));
    if (coeff.isZero())
      return expr;

    IExpr rest = engine.evaluate(F.ExpandAll(F.Subtract(expr, F.Times(coeff, cVar))));

    if (coeff.isFreeAST(S.Pochhammer) && coeff.isFreeAST(S.Gamma)) {
      return expr;
    }

    IASTAppendable newCoeff = F.TimesAlloc();
    if (coeff.isTimes()) {
      for (IExpr arg : (IAST) coeff) {
        if (arg.isNumericFunction(true))
          continue;

        if (arg.isPower() && arg.exponent().isPlus()) {
          IAST plus = (IAST) arg.exponent();
          IASTAppendable newPlus = F.PlusAlloc();
          for (IExpr pArg : plus) {
            if (!pArg.isNumericFunction(true)) {
              newPlus.append(pArg);
            }
          }
          newPlus.append(F.C1);

          IExpr cleanPower = newPlus.argSize() == 1 ? newPlus.arg1() : newPlus;
          newCoeff.append(F.Power(arg.base(), cleanPower));
          continue;
        }
        newCoeff.append(arg);
      }
    } else if (coeff.isPower() && coeff.exponent().isPlus()) {
      IAST plus = (IAST) coeff.exponent();
      IASTAppendable newPlus = F.PlusAlloc();
      for (IExpr pArg : plus) {
        if (!pArg.isNumericFunction(true)) {
          newPlus.append(pArg);
        }
      }
      newPlus.append(F.C1);
      IExpr cleanPower = newPlus.argSize() == 1 ? newPlus.arg1() : newPlus;
      newCoeff.append(F.Power(coeff.base(), cleanPower));
    } else {
      newCoeff.append(coeff);
    }

    IExpr simplifiedCoeff = newCoeff.argSize() == 1 ? newCoeff.arg1() : newCoeff;
    if (newCoeff.argSize() == 0)
      simplifiedCoeff = F.C1;

    return engine.evaluate(F.Plus(rest, F.Times(cVar, simplifiedCoeff)));
  }

  // ====================================================================================
  // Helper to trigger the global SimplifyUtil Fibonacci identifier
  // ====================================================================================
  private IExpr applyFibonacciLucas(IExpr root, IExpr nVar, EvalEngine engine) {
    IExpr mapped = RSolve.recognizeFibonacciAndLucas(root, nVar, engine);
    return mapped.isPresent() ? mapped : root;
  }

  /**
   * Applies the boundary conditions to a candidate solution, acting as the single
   * general-solution-first gateway. When there are no boundary conditions the candidate is returned
   * unchanged; otherwise {@link #applyUnaryBCs} solves the arbitrary constants (or validates a
   * constant-free candidate) and returns {@link F#NIL} if the conditions cannot be satisfied, so the
   * caller can fall back to the next solver.
   */
  private IExpr acceptCandidate(IExpr candidate, IAST uFunction1Arg, IExpr nVar,
      IAST boundaryConditions, boolean hasBCs, EvalEngine engine) {
    if (candidate.isNIL()) {
      return F.NIL;
    }
    if (!hasBCs) {
      return candidate;
    }
    return applyUnaryBCs(candidate, uFunction1Arg, nVar, boundaryConditions, engine);
  }

  private IExpr applyUnaryBCs(IExpr root, IAST uFunction1Arg, IExpr nVar, IAST boundaryConditions,
      EvalEngine engine) {
    if (root.isNIL() || boundaryConditions.argSize() == 0)
      return root;

    IExpr head = uFunction1Arg.head();
    IAST headRules = F.List(F.Rule(head, F.Function(F.List(nVar), root)));

    IASTAppendable evaluatedBCs = F.ListAlloc(boundaryConditions.argSize());
    for (int k = 1; k <= boundaryConditions.argSize(); k++) {
      evaluatedBCs.append(engine.evaluate(F.subst(boundaryConditions.get(k), headRules)));
    }

    IASTAppendable cVars = F.ListAlloc();
    extractCVars(evaluatedBCs, cVars);

    if (cVars.argSize() == 0) {
      for (int k = 1; k <= evaluatedBCs.argSize(); k++) {
        IExpr bc = evaluatedBCs.get(k);
        if (bc.isEqual()) {
          if (!engine.evaluate(bc).isTrue())
            return F.NIL;
        } else if (!bc.isZero()) {
          return F.NIL;
        }
      }
      return root;
    }

    // Safely filter out Tautologies (like 0 == 0) ensuring Solve doesn't choke
    IASTAppendable cleanBCs = F.ListAlloc();
    for (int k = 1; k <= evaluatedBCs.argSize(); k++) {
      IExpr bc = evaluatedBCs.get(k);
      IExpr evalBC = engine.evaluate(bc);
      if (evalBC.isTrue())
        continue;
      if (evalBC.isFalse())
        return F.NIL; // Direct mathematical contradiction
      cleanBCs.append(evalBC.isEqual() ? evalBC : F.Equal(evalBC, F.C0));
    }

    if (cleanBCs.argSize() == 0)
      return root;

    IExpr cSols = engine.evaluate(F.Solve(cleanBCs, cVars));

    if (cSols.isList() && ((IAST) cSols).argSize() > 0) {
      IAST cSol = (IAST) ((IAST) cSols).arg1();
      return engine.evaluate(F.subst(root, cSol));
    }
    return F.NIL;
  }

  // ====================================================================================
  // Main Evaluate Method
  // ====================================================================================
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {

    if (ast.arg1().isList() && ast.arg2().isList()) {
      return solveSystem((IAST) ast.arg1(), (IAST) ast.arg2(), ast.arg3(), engine);
    }

    IAST arg1 = ast.arg1().makeList();
    IExpr arg2 = ast.arg2();
    IExpr nVar = ast.arg3();

    if (!nVar.isSymbol())
      return F.NIL;

    IAST uFunction1Arg = arg2.isAST1() ? (IAST) arg2 : F.unaryAST1(arg2, nVar);
    IExpr head = uFunction1Arg.head();

    IASTAppendable listOfEquations = F.ListAlloc();
    for (int i = 1; i <= arg1.argSize(); i++) {
      IExpr eq = arg1.get(i);
      listOfEquations.append(eq.isEqual() ? eq : F.Equal(eq, F.C0));
    }

    IASTAppendable boundaryConditions = F.ListAlloc();
    int idx = 1;
    while (idx <= listOfEquations.argSize()) {
      IExpr equation = listOfEquations.get(idx);
      if (equation.isFree(nVar)) {
        boundaryConditions.append(equation);
        listOfEquations.remove(idx);
      } else {
        idx++;
      }
    }

    if (listOfEquations.argSize() == 1) {
      IExpr equation = listOfEquations.arg1();
      IExpr lhs = equation.isEqual() ? F.Subtract(equation.first(), equation.second()) : equation;
      lhs = expandTrigsInVar(lhs, engine);
      boolean hasBCs = boundaryConditions.argSize() > 0;

      // ================================================================================
      // STRICT LINEARITY VALIDATION
      // ================================================================================
      Map<Integer, IExpr> shiftMap = new HashMap<>();
      findShifts(lhs, head, nVar, shiftMap, engine);

      boolean isNonLinear = shiftMap.isEmpty();
      IExpr rest = lhs;
      Map<Integer, IExpr> coeffs = new HashMap<>();

      if (!isNonLinear) {
        for (Map.Entry<Integer, IExpr> entry : shiftMap.entrySet()) {
          IExpr c_k = engine.evaluate(F.Simplify(F.D(rest, entry.getValue())));
          coeffs.put(entry.getKey(), c_k);
          rest = engine.evaluate(F.Simplify(F.Subtract(rest, F.Times(c_k, entry.getValue()))));
        }

        if (!rest.isFree(head)) {
          isNonLinear = true;
        } else {
          for (IExpr c : coeffs.values()) {
            if (!c.isFree(head)) {
              isNonLinear = true;
              break;
            }
          }
        }
      }

      if (isNonLinear) {
        IExpr logTransformSol = methodLogarithmicTransform(lhs, uFunction1Arg, nVar, engine);
        if (logTransformSol.isPresent()) {
          logTransformSol = applyFibonacciLucas(logTransformSol, nVar, engine);
          if (!hasBCs) {
            logTransformSol = absorbArbitraryConstants(logTransformSol, nVar, engine);
          } else {
            IExpr bcSol =
                applyUnaryBCs(logTransformSol, uFunction1Arg, nVar, boundaryConditions, engine);
            if (bcSol.isPresent())
              logTransformSol = bcSol;
            else
              return F.NIL;
          }
          return formatResult(arg2, nVar, logTransformSol);
        }
        return F.NIL;
      }

      IExpr f_n = engine.evaluate(F.Negate(rest));
      boolean isConstant = coeffs.values().stream().allMatch(c -> c.isFree(nVar));
      int startC = engine.incConstantCounter();
      IExpr root = F.NIL;

      if (hasBCs) {
        IExpr gfSol = methodGF(F.Equal(lhs, F.C0), uFunction1Arg, nVar, boundaryConditions, engine);
        if (gfSol.isPresent()) {
          gfSol = applyUnaryBCs(gfSol, uFunction1Arg, nVar, boundaryConditions, engine);
          if (gfSol.isPresent()) {
            gfSol = replaceUnboundInitialConditions(gfSol, head, engine);
            gfSol = applyFibonacciLucas(gfSol, nVar, engine);
            return formatResult(arg2, nVar, gfSol);
          }
        }

        IExpr egfSol =
            methodEGF(F.Equal(lhs, F.C0), uFunction1Arg, nVar, boundaryConditions, engine);
        if (egfSol.isPresent()) {
          egfSol = applyUnaryBCs(egfSol, uFunction1Arg, nVar, boundaryConditions, engine);
          if (egfSol.isPresent()) {
            egfSol = replaceUnboundInitialConditions(egfSol, head, engine);
            egfSol = applyFibonacciLucas(egfSol, nVar, engine);
            return formatResult(arg2, nVar, egfSol);
          }
        }
      }

      if (isConstant && f_n.isZero()) {
        if (hasBCs) {
          // Try the explicit Binet closed form first; accept it only if it satisfies the boundary
          // conditions (it is anchored at y(0)=0, y(1)=1).
          IExpr binetRoot = solveConstantCoefficientsBinet(coeffs, nVar, engine);
          root = acceptCandidate(binetRoot, uFunction1Arg, nVar, boundaryConditions, true, engine);
          if (root.isNIL()) {
            // Fall back to the general solution and solve the arbitrary constants from the BCs.
            IExpr general = solveConstantCoefficientsGeneric(coeffs, nVar, engine, startC);
            root = acceptCandidate(general, uFunction1Arg, nVar, boundaryConditions, true, engine);
          }
        } else {
          // No boundary conditions: return the general solution with arbitrary constants C(k).
          root = solveConstantCoefficientsGeneric(coeffs, nVar, engine, startC);
        }
      }

      if (root.isNIL() && coeffs.size() == 2) {
        IExpr candidate = solveFirstOrderLinear(coeffs, f_n, nVar, engine, startC);
        root = acceptCandidate(candidate, uFunction1Arg, nVar, boundaryConditions, hasBCs, engine);
      }

      if (root.isNIL() && coeffs.size() == 3) {
        IExpr candidate = solveReductionOfOrderDiscrete(coeffs, f_n, nVar, engine, startC);
        root = acceptCandidate(candidate, uFunction1Arg, nVar, boundaryConditions, hasBCs, engine);
      }

      if (root.isPresent()) {
        root = applyFibonacciLucas(root, nVar, engine);
        if (!hasBCs) {
          root = absorbArbitraryConstants(root, nVar, engine);
        }
        return formatResult(arg2, nVar, root);
      }

      if (!hasBCs) {
        IExpr gfSol = methodGF(F.Equal(lhs, F.C0), uFunction1Arg, nVar, boundaryConditions, engine);
        if (gfSol.isPresent()) {
          gfSol = replaceUnboundInitialConditions(gfSol, head, engine);
          gfSol = applyFibonacciLucas(gfSol, nVar, engine);
          gfSol = absorbArbitraryConstants(gfSol, nVar, engine);
          return formatResult(arg2, nVar, gfSol);
        }

        IExpr egfSol =
            methodEGF(F.Equal(lhs, F.C0), uFunction1Arg, nVar, boundaryConditions, engine);
        if (egfSol.isPresent()) {
          egfSol = replaceUnboundInitialConditions(egfSol, head, engine);
          egfSol = applyFibonacciLucas(egfSol, nVar, engine);
          egfSol = absorbArbitraryConstants(egfSol, nVar, engine);
          return formatResult(arg2, nVar, egfSol);
        }
      }
    }
    return F.NIL;
  }

  private IExpr expandTrigsInVar(IExpr expr, EvalEngine engine) {
    if (!expr.isFreeAST(x -> x.isTrigFunction())) {
      return engine.evaluate(F.TrigToExp(expr));
    }
    return expr;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  private void extractInitialValues(IExpr expr, IExpr head, IASTAppendable initVals) {
    if (expr.isAST(head, 2) && expr.first().isInteger()) {
      boolean found = false;
      for (int i = 1; i <= initVals.argSize(); i++) {
        if (initVals.get(i).equals(expr)) {
          found = true;
          break;
        }
      }
      if (!found) {
        initVals.append(expr);
      }
    } else if (expr.isAST()) {
      ((IAST) expr).forEach(arg -> extractInitialValues(arg, head, initVals));
    }
  }

  private void findShifts(IExpr expr, IExpr aHead, IExpr nVar, Map<Integer, IExpr> shiftMap,
      EvalEngine engine) {
    if (expr.isAST(aHead, 2)) {
      IExpr diff = engine.evaluate(F.Simplify(F.Subtract(expr.first(), nVar)));
      if (diff.isInteger())
        shiftMap.put(diff.toIntDefault(), expr);
    } else if (expr.isAST()) {
      for (IExpr child : (IAST) expr)
        findShifts(child, aHead, nVar, shiftMap, engine);
    }
  }

  private IExpr formatResult(IExpr targetVar, IExpr nVar, IExpr root) {
    IASTAppendable resultList = F.ListAlloc();
    if (targetVar.isSymbol()) {
      resultList.append(F.List(F.Rule(targetVar, F.Function(F.List(nVar), root))));
    } else {
      resultList.append(F.List(F.Rule(targetVar, root)));
    }
    return resultList;
  }

  private IExpr methodEGF(IExpr equation, IAST uFunction1Arg, IExpr nVar, IAST boundaryConditions,
      EvalEngine engine) {
    IExpr xDummy = F.Dummy("xEGF");
    IExpr eFunc = F.unaryAST1(uFunction1Arg.head(), xDummy);

    IExpr eLhs = engine
        .evaluate(F.ternaryAST3(S.ExponentialGeneratingFunction, equation.first(), nVar, xDummy));
    IExpr eRhs = engine
        .evaluate(F.ternaryAST3(S.ExponentialGeneratingFunction, equation.second(), nVar, xDummy));

    if (!eLhs.isFreeAST(S.ExponentialGeneratingFunction)
        || !eRhs.isFreeAST(S.ExponentialGeneratingFunction)) {
      return F.NIL;
    }
    IExpr eEq = F.Equal(eLhs, eRhs);

    IExpr dsolveRes = engine.evaluate(F.DSolve(eEq, eFunc, xDummy));
    if (dsolveRes.isList() && ((IAST) dsolveRes).argSize() > 0 && !dsolveRes.isAST(S.DSolve)) {
      IExpr ySol = F.NIL;
      IAST firstSol = (IAST) ((IAST) dsolveRes).arg1();
      for (int i = 1; i <= firstSol.argSize(); i++) {
        if (firstSol.get(i).isRule() && firstSol.get(i).first().equals(eFunc)) {
          ySol = firstSol.get(i).second();
        }
      }

      if (ySol.isPresent()) {
        IExpr coeff = engine.evaluate(
            F.Times(F.Factorial(nVar), F.SeriesCoefficient(ySol, F.List(xDummy, F.C0, nVar))));
        if (!coeff.isAST(S.SeriesCoefficient)) {
          // Allow `evaluate` wrapper to apply boundary conditions globally instead of duplicating
          // here
          return coeff;
        }
      }
    }
    return F.NIL;
  }

  // ====================================================================================
  // Generates and evaluates the ODE proxy system from Z-Transform differential properties
  // ====================================================================================
  private IExpr methodGF(IExpr equation, IAST uFunction1Arg, IExpr nVar, IAST boundaryConditions,
      EvalEngine engine) {
    IExpr zDummy = F.Dummy("zGF");
    IExpr gFunc = F.ZTransform(uFunction1Arg, nVar, zDummy);
    IExpr gDummyFunc = F.Dummy("G");
    IExpr gDummy = F.unaryAST1(gDummyFunc, zDummy);

    IExpr zLhs = engine.evaluate(F.ZTransform(equation.first(), nVar, zDummy));
    IExpr zRhs = engine.evaluate(F.ZTransform(equation.second(), nVar, zDummy));
    IExpr zEq = F.Equal(zLhs, zRhs);

    if (boundaryConditions.argSize() > 0) {
      IASTAppendable bcRules = F.ListAlloc();
      for (int i = 1; i <= boundaryConditions.argSize(); i++) {
        IExpr bc = boundaryConditions.get(i);
        if (bc.isEqual()) {
          bcRules.append(F.Rule(bc.first(), bc.second()));
        }
      }
      zEq = engine.evaluate(F.subst(zEq, bcRules));
    }

    // Intercept abstract derivatives spawned by ZTransform(n*a(n)) and map directly to G(z)
    IASTAppendable gRules = F.ListAlloc();
    gRules.append(F.Rule(gFunc, gDummy));
    for (int i = 1; i <= 5; i++) {
      IExpr diG = engine.evaluate(F.D(gDummy, F.List(zDummy, F.ZZ(i))));
      IExpr diZ = F.ternaryAST3(F.Derivative(F.C0, F.C0, F.ZZ(i)).apply(S.ZTransform),
          uFunction1Arg, nVar, zDummy);
      gRules.append(F.Rule(diZ, diG));
    }

    zEq = engine.evaluate(F.subst(zEq, gRules));

    IExpr gVal = F.NIL;
    boolean isODE = !zEq.isFree(S.Derivative) || !zEq.isFree(S.D);

    if (isODE) {
      int savedCounter = engine.getConstantCounter();
      try {
        IExpr dsolveRes = engine.evaluate(F.DSolve(F.List(zEq), F.List(gDummyFunc), zDummy));
        if (dsolveRes.isList() && ((IAST) dsolveRes).argSize() > 0 && !dsolveRes.isAST(S.DSolve)) {
          IAST firstSol = (IAST) ((IAST) dsolveRes).arg1();
          for (int j = 1; j <= firstSol.argSize(); j++) {
            IExpr rule = firstSol.get(j);
            if (rule.isRule() && rule.first().equals(gDummyFunc)) {
              gVal = rule.second();
              if (gVal.isAST(S.Function)) {
                gVal = engine.evaluate(F.unaryAST1(gVal, zDummy));
              }
              break;
            }
          }
        }
      } finally {
        engine.setConstantCounter(savedCounter);
      }
    } else {
      IExpr gSolList = engine.evaluate(F.Solve(zEq, F.List(gDummy)));
      if (gSolList.isList() && ((IAST) gSolList).argSize() > 0) {
        IAST firstSol = (IAST) ((IAST) gSolList).arg1();
        for (int j = 1; j <= firstSol.argSize(); j++) {
          IExpr rule = firstSol.get(j);
          if (rule.isRule() && rule.first().equals(gDummy)) {
            gVal = rule.second();
            break;
          }
        }
      }
    }

    if (gVal.isPresent()) {
      IExpr a_n = engine.evaluate(F.InverseZTransform(gVal, zDummy, nVar));
      if (!a_n.isFree(S.InverseZTransform) || !a_n.isFree(S.ZTransform)) {
        // Maclaurin Series Coefficient Fallback: Z^-1{G(z)} = [x^n] G(1/x)
        IExpr xSC = F.Dummy("xSC");
        IExpr gValX = engine.evaluate(F.subst(gVal, zDummy, F.Power(xSC, F.CN1)));
        IExpr sc = engine.evaluate(F.SeriesCoefficient(gValX, F.List(xSC, F.C0, nVar)));
        if (sc.isPresent() && !sc.isAST(S.SeriesCoefficient)) {
          a_n = sc;
        } else {
          return F.NIL;
        }
      }
      IExpr simplified = engine.evaluate(F.Simplify(a_n));
      if (simplified.isFree(uFunction1Arg)) {
        return simplified;
      }
    }
    return F.NIL;
  }

  private IExpr methodLogarithmicTransform(IExpr equation, IAST uFunction1Arg, IExpr nVar,
      EvalEngine engine) {
    IExpr head = uFunction1Arg.head();
    IExpr[] shiftPower = new IExpr[] {F.NIL};

    equation.accept(new org.matheclipse.core.visit.VisitorExpr() {
      @Override
      public IExpr visitAST(IAST ast) {
        if (ast.isAST(head, 2)) {
          IExpr arg = ast.arg1();
          if (arg.isPower() && arg.first().equals(nVar)) {
            shiftPower[0] = arg.second();
          } else if (arg.isAST(S.Sqrt, 1) && arg.first().equals(nVar)) {
            shiftPower[0] = F.C1D2;
          }
        }
        return super.visitAST(ast);
      }
    });

    if (!shiftPower[0].isPresent() || !shiftPower[0].isNumericFunction(true)) {
      return F.NIL;
    }

    IExpr q = shiftPower[0];
    IExpr b = engine.evaluate(F.Power(q, F.CN1));

    IExpr mDummy = F.Dummy("m");
    IExpr dDummy = F.Dummy("d");

    IExpr nSub = engine.evaluate(F.Power(S.E, F.Power(b, mDummy)));
    IExpr transformedLhs = engine.evaluate(F.subst(equation, nVar, nSub));
    transformedLhs = engine.evaluate(F.PowerExpand(transformedLhs));

    IASTAppendable dRules = F.ListAlloc();
    transformedLhs.accept(new org.matheclipse.core.visit.VisitorExpr() {
      @Override
      public IExpr visitAST(IAST ast) {
        if (ast.isAST(head, 2)) {
          IExpr arg = ast.arg1();
          IExpr indexCalculation = F.Divide(F.Log(F.Log(arg)), F.Log(b));
          IExpr cleanIndex = engine.evaluate(F.Simplify(F.PowerExpand(indexCalculation)));
          dRules.append(F.Rule(ast, F.unaryAST1(dDummy, cleanIndex)));
        }
        return super.visitAST(ast);
      }
    });

    if (dRules.argSize() == 0)
      return F.NIL;

    IExpr linearLhs = engine.evaluate(F.subst(transformedLhs, dRules));
    IExpr linearSolution =
        engine.evaluate(F.RSolve(F.Equal(linearLhs, F.C0), F.unaryAST1(dDummy, mDummy), mDummy));

    if (linearSolution.isList() && ((IAST) linearSolution).argSize() > 0) {
      IAST firstSol = (IAST) ((IAST) linearSolution).arg1();
      if (firstSol.argSize() > 0 && firstSol.arg1().isRule()) {
        IExpr dResult = firstSol.arg1().second();

        // Rejects non-closed-form solutions for logarithmic transformations.
        // This avoids returning mathematically ill-defined continuous sums (e.g., upper bounds like
        // Log(Log(n)))
        if (!dResult.isFreeAST(S.Sum) || !dResult.isFreeAST(S.Product)) {
          return F.NIL;
        }

        IExpr mResub = engine.evaluate(F.Divide(F.Log(F.Log(nVar)), F.Log(b)));
        IExpr finalAns = engine.evaluate(F.subst(dResult, mDummy, mResub));
        return engine.evaluate(F.Simplify(finalAns));
      }
    }
    return F.NIL;
  }

  private IExpr replaceUnboundInitialConditions(IExpr expr, IExpr head, EvalEngine engine) {
    IASTAppendable initVals = F.ListAlloc();
    extractInitialValues(expr, head, initVals);

    if (initVals.argSize() == 0)
      return expr;

    IASTAppendable rules = F.ListAlloc();
    int cIndex = engine.incConstantCounter();
    for (int i = 1; i <= initVals.argSize(); i++) {
      rules.append(F.Rule(initVals.get(i), F.C(cIndex++)));
    }

    IExpr res = engine.evaluate(F.subst(expr, rules));
    res = engine.evaluate(F.ExpandAll(res));
    IExpr cPattern = F.unaryAST1(S.C, F.$b());
    res = engine.evaluate(F.Collect(res, cPattern));

    return res;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
  }

  private IExpr solveFirstOrderLinear(Map<Integer, IExpr> coeffs, IExpr f_n, IExpr nVar,
      EvalEngine engine, int startC) {
    int max = Collections.max(coeffs.keySet());
    int min = Collections.min(coeffs.keySet());
    if (max - min != 1)
      return F.NIL;

    IExpr c0 = coeffs.get(min);
    IExpr c1 = coeffs.get(max);
    IExpr shiftExpr = F.Subtract(F.Plus(nVar, F.C1), F.ZZ(max));
    IExpr c1_sh = engine.evaluate(F.subst(c1, nVar, shiftExpr));
    IExpr c0_sh = engine.evaluate(F.subst(c0, nVar, shiftExpr));
    IExpr f_sh = engine.evaluate(F.subst(f_n, nVar, shiftExpr));

    IExpr p_n = engine.evaluate(F.Simplify(F.Divide(F.Negate(c0_sh), c1_sh)));
    IExpr q_n = engine.evaluate(F.Simplify(F.Divide(f_sh, c1_sh)));

    IExpr K = F.Dummy("K");
    IExpr p_K = engine.evaluate(F.subst(p_n, nVar, K));

    IExpr prod = org.matheclipse.core.reflection.system.Product.tryClosedFormReduction(p_K, K, F.C1,
        F.Subtract(nVar, F.C1), engine);
    if (!prod.isPresent()) {
      prod = F.Product(p_K, F.List(K, F.C1, F.Subtract(nVar, F.C1)));
    }

    IExpr C1 = F.C(startC);
    IExpr a_h_raw = engine.evaluate(F.Times(C1, prod));

    if (q_n.isZero()) {
      IExpr a_h_clean = absorbIntegrationConstants(a_h_raw, C1, engine);
      return engine.evaluate(F.ExpandAll(a_h_clean));
    } else {
      IExpr a_h_noC = engine.evaluate(F.subst(a_h_raw, C1, F.C1));
      IExpr a_h_Kp1 = engine.evaluate(F.subst(a_h_noC, nVar, F.Plus(K, F.C1)));
      IExpr q_K = engine.evaluate(F.subst(q_n, nVar, K));

      IExpr sumTerm = engine.evaluate(F.Simplify(F.PowerExpand(F.Divide(q_K, a_h_Kp1))));
      IExpr sum = engine.evaluate(F.Sum(sumTerm, F.List(K, F.C0, F.Subtract(nVar, F.C1))));
      IExpr a_p = engine.evaluate(F.Times(a_h_noC, sum));

      IExpr a_h_clean = absorbIntegrationConstants(a_h_raw, C1, engine);
      return engine.evaluate(F.ExpandAll(F.Plus(a_h_clean, a_p)));
    }
  }

  /**
   * Order-2 constant-coefficient closed form via the shared generalized Binet formula. Returns the
   * specific solution anchored at {@code y(0)=0, y(1)=1} (no arbitrary constants), or {@link F#NIL}
   * if the recurrence is not a homogeneous order-2 constant-coefficient equation.
   */
  private IExpr solveConstantCoefficientsBinet(Map<Integer, IExpr> coeffs, IExpr nVar,
      EvalEngine engine) {
    if (coeffs.size() != 3) {
      return F.NIL;
    }
    int minShift = Collections.min(coeffs.keySet());
    int maxShift = Collections.max(coeffs.keySet());
    if (maxShift - minShift != 2) {
      return F.NIL;
    }

    // Equation: c0*y(n) + c1*y(n+1) + c2*y(n+2) = 0
    // Normalized: y(n+2) = -c1/c2 * y(n+1) - c0/c2 * y(n)
    IExpr c0 = coeffs.get(minShift);
    IExpr c1 = coeffs.get(minShift + 1);
    IExpr c2 = coeffs.get(maxShift);

    IExpr a = engine.evaluate(F.Negate(F.Divide(c1, c2)));
    // generalizedBinet uses the convention y(n+2) = a*y(n+1) - b*y(n) (delta = a^2 - 4b),
    // so pass b = c0/c2 (not negated) to match the normalized recurrence.
    IExpr b = engine.evaluate(F.Divide(c0, c2));

    // Use the shared Binet formula. Bypass the canonical Fibonacci/ChebyshevU identities here
    // (useCanonicalForms=false) so the explicit Binet form is produced; RSolve canonicalizes
    // special sequences (Fibonacci/LucasL) in a later applyFibonacciLucas pass.
    return AlgebraUtil.generalizedBinet(a, b, nVar, engine, false);
  }

  /**
   * General solution of a homogeneous linear constant-coefficient recurrence via the characteristic
   * polynomial roots. Returns a combination of {@code C(startC + i) * n^m * root^n} terms carrying
   * arbitrary constants.
   */
  private IExpr solveConstantCoefficientsGeneric(Map<Integer, IExpr> coeffs, IExpr nVar,
      EvalEngine engine, int startC) {
    int minShift = Collections.min(coeffs.keySet());

    // Generic Solver Path ---
    IExpr xDummy = F.Dummy("x");
    IASTAppendable poly = F.PlusAlloc();

    for (Map.Entry<Integer, IExpr> entry : coeffs.entrySet()) {
      int p = entry.getKey() - minShift;
      IExpr term = p == 0 ? entry.getValue() : F.Times(entry.getValue(), F.Power(xDummy, F.ZZ(p)));
      poly.append(term);
    }

    IExpr rootsList = engine.evaluate(F.Solve(F.Equal(poly, F.C0), F.List(xDummy)));
    if (!rootsList.isList()) {
      return F.NIL;
    }

    IAST roots = (IAST) rootsList;
    Map<IExpr, Integer> rootMults = new HashMap<>();
    for (int i = 1; i <= roots.argSize(); i++) {
      IExpr sol = roots.get(i);
      if (sol.isList() && ((IAST) sol).argSize() > 0) {
        IExpr rule = ((IAST) sol).arg1();
        if (rule.isRule()) {
          IExpr val = rule.second();
          rootMults.put(val, rootMults.getOrDefault(val, 0) + 1);
        }
      }
    }

    IASTAppendable result = F.PlusAlloc();
    int cIndex = startC;
    for (Map.Entry<IExpr, Integer> entry : rootMults.entrySet()) {
      IExpr root = entry.getKey();
      int mult = entry.getValue();
      for (int m = 0; m < mult; m++) {
        IExpr constTerm = F.C(cIndex++);
        IExpr nPower = m == 0 ? F.C1 : F.Power(nVar, F.ZZ(m));
        IExpr rPower = engine.evaluate(F.Power(root, nVar));
        result.append(F.Times(constTerm, nPower, rPower));
      }
    }

    return engine.evaluate(result);
  }

  private IExpr solveReductionOfOrderDiscrete(Map<Integer, IExpr> coeffs, IExpr f_n, IExpr nVar,
      EvalEngine engine, int startC) {
    int max = Collections.max(coeffs.keySet());
    int min = Collections.min(coeffs.keySet());
    if (max - min != 2 || coeffs.size() != 3) {
      return F.NIL;
    }

    IExpr c2 = coeffs.get(max);
    IExpr c1 = coeffs.get(min + 1);
    IExpr c0 = coeffs.get(min);

    IExpr shiftExpr = F.Subtract(F.Plus(nVar, F.C2), F.ZZ(max));
    IExpr P = engine.evaluate(F.subst(c2, nVar, shiftExpr));
    IExpr Q = engine.evaluate(F.subst(c1, nVar, shiftExpr));
    IExpr R = engine.evaluate(F.subst(c0, nVar, shiftExpr));
    IExpr fn_sh = engine.evaluate(F.subst(f_n, nVar, shiftExpr));

    IExpr y1 = F.NIL;

    if (engine.evaluate(F.Simplify(F.Plus(P, Q, R))).isZero()) {
      y1 = F.C1;
    } else if (engine.evaluate(F.Simplify(F.Plus(P, F.Negate(Q), R))).isZero()) {
      y1 = engine.evaluate(F.Power(F.CN1, nVar));
    } else if (engine.evaluate(F.Simplify(
        F.Plus(F.Times(P, F.Plus(nVar, F.C2)), F.Times(Q, F.Plus(nVar, F.C1)), F.Times(R, nVar))))
        .isZero()) {
      y1 = nVar;
    } else if (engine.evaluate(F.Simplify(F.Plus(F.Times(P, F.Plus(nVar, F.C2), F.Plus(nVar, F.C1)),
        F.Times(Q, F.Plus(nVar, F.C1)), R))).isZero()) {
      y1 = engine.evaluate(F.Factorial(nVar));
    } else {
      IExpr cDummy = F.Dummy("c");
      IExpr charEq = F.Plus(F.Times(P, F.Sqr(cDummy)), F.Times(Q, cDummy), R);
      IExpr cSols = engine.evaluate(F.Solve(F.Equal(charEq, F.C0), F.List(cDummy)));
      if (cSols.isList() && ((IAST) cSols).argSize() > 0) {
        IAST firstSol = (IAST) ((IAST) cSols).arg1();
        if (firstSol.argSize() > 0 && firstSol.arg1().isRule()) {
          IExpr cVal = firstSol.arg1().second();
          if (cVal.isFree(nVar)) {
            y1 = engine.evaluate(F.Power(cVal, nVar));
          }
        }
      }
    }

    if (!y1.isPresent()) {
      return F.NIL;
    }

    IExpr y1_np1 = engine.evaluate(F.subst(y1, nVar, F.Plus(nVar, F.C1)));
    IExpr y1_np2 = engine.evaluate(F.subst(y1, nVar, F.Plus(nVar, F.C2)));

    IExpr A_v = engine.evaluate(F.Simplify(F.Times(P, y1_np2)));
    IExpr B_v = engine.evaluate(F.Simplify(F.Plus(A_v, F.Times(Q, y1_np1))));

    IExpr ratio = engine.evaluate(F.Simplify(F.Divide(F.Negate(B_v), A_v)));

    IExpr K = F.Dummy("K");
    IExpr ratio_K = engine.evaluate(F.subst(ratio, nVar, K));

    IExpr v_n = engine.evaluate(F.Product(ratio_K, F.List(K, F.C1, F.Subtract(nVar, F.C1))));

    IExpr j = F.Dummy("j");
    IExpr v_j = engine.evaluate(F.subst(v_n, nVar, j));
    IExpr sum_v = engine.evaluate(F.Sum(v_j, F.List(j, F.C1, F.Subtract(nVar, F.C1))));

    IExpr y2 = engine.evaluate(F.Times(y1, sum_v));

    IExpr C1 = F.C(startC);
    IExpr C2 = F.C(startC + 1);

    IExpr a_h = engine.evaluate(F.Plus(F.Times(C1, y1), F.Times(C2, y2)));

    if (fn_sh.isZero()) {
      IExpr a_h_clean = absorbIntegrationConstants(a_h, C1, engine);
      a_h_clean = absorbIntegrationConstants(a_h_clean, C2, engine);
      return engine.evaluate(F.ExpandAll(a_h_clean));
    } else {
      IExpr y2_np1 = engine.evaluate(F.subst(y2, nVar, F.Plus(nVar, F.C1)));
      IExpr y2_np2 = engine.evaluate(F.subst(y2, nVar, F.Plus(nVar, F.C2)));

      IExpr W =
          engine.evaluate(F.Simplify(F.Subtract(F.Times(y1_np1, y2_np2), F.Times(y2_np1, y1_np2))));
      IExpr F_n = engine.evaluate(F.Divide(fn_sh, P));

      IExpr cas_K = engine.evaluate(F.subst(W, nVar, K));
      IExpr F_K = engine.evaluate(F.subst(F_n, nVar, K));
      IExpr y1_Kp1 = engine.evaluate(F.subst(y1_np1, nVar, K));
      IExpr y2_Kp1 = engine.evaluate(F.subst(y2_np1, nVar, K));

      IExpr term1 = engine.evaluate(F.Simplify(F.Divide(F.Times(F.CN1, y2_Kp1, F_K), cas_K)));
      IExpr term2 = engine.evaluate(F.Simplify(F.Divide(F.Times(y1_Kp1, F_K), cas_K)));

      IExpr sum1 = engine.evaluate(F.Sum(term1, F.List(K, F.C0, F.Subtract(nVar, F.C1))));
      IExpr sum2 = engine.evaluate(F.Sum(term2, F.List(K, F.C0, F.Subtract(nVar, F.C1))));

      IExpr a_p = engine.evaluate(F.Plus(F.Times(y1, sum1), F.Times(y2, sum2)));

      IExpr a_h_clean = absorbIntegrationConstants(a_h, C1, engine);
      a_h_clean = absorbIntegrationConstants(a_h_clean, C2, engine);

      return engine.evaluate(F.ExpandAll(F.Plus(a_h_clean, a_p)));
    }
  }

  private IExpr solveSystem(IAST eqns, IAST funcs, IExpr nVar, EvalEngine engine) {
    IASTAppendable listOfEquations = F.ListAlloc();
    IASTAppendable boundaryConditions = F.ListAlloc();
    for (int i = 1; i <= eqns.argSize(); i++) {
      IExpr eq = eqns.get(i);
      IExpr nEq = eq.isEqual() ? eq : F.Equal(eq, F.C0);
      if (nEq.isFree(nVar)) {
        boundaryConditions.append(nEq);
      } else {
        listOfEquations.append(nEq);
      }
    }

    IExpr zDummy = F.Dummy("zSys");
    IASTAppendable zEqns = F.ListAlloc(listOfEquations.argSize());
    for (int i = 1; i <= listOfEquations.argSize(); i++) {
      IExpr eq = listOfEquations.get(i);
      IExpr zLhs = engine.evaluate(F.ZTransform(eq.first(), nVar, zDummy));
      IExpr zRhs = engine.evaluate(F.ZTransform(eq.second(), nVar, zDummy));
      zEqns.append(F.Equal(zLhs, zRhs));
    }

    IExpr evalZEqns = zEqns;
    if (boundaryConditions.argSize() > 0) {
      IASTAppendable bcRules = F.ListAlloc();
      for (int i = 1; i <= boundaryConditions.argSize(); i++) {
        IExpr bc = boundaryConditions.get(i);
        if (bc.isEqual()) {
          bcRules.append(F.Rule(bc.first(), bc.second()));
        }
      }
      evalZEqns = engine.evaluate(F.subst(evalZEqns, bcRules));
    }

    IASTAppendable dummyVars = F.ListAlloc(funcs.argSize());
    IASTAppendable subRules = F.ListAlloc(funcs.argSize());
    for (int i = 1; i <= funcs.argSize(); i++) {
      IExpr target = funcs.get(i);
      IExpr uFunc = target.isAST1() ? target : F.unaryAST1(target, nVar);

      IExpr gDummy = F.Dummy("gSys" + i);
      dummyVars.append(gDummy);

      IExpr zTransFunc = engine.evaluate(F.ZTransform(uFunc, nVar, zDummy));
      subRules.append(F.Rule(zTransFunc, gDummy));
    }

    IExpr algebraicSystem = engine.evaluate(F.subst(evalZEqns, subRules));
    IExpr gSols = engine.evaluate(F.Solve(algebraicSystem, dummyVars));

    if (gSols.isList() && ((IAST) gSols).argSize() > 0) {
      IAST firstSol = (IAST) ((IAST) gSols).arg1();
      IASTAppendable resultSystem = F.ListAlloc(funcs.argSize());
      IExpr[] cleanInverses = new IExpr[funcs.argSize()];

      for (int i = 1; i <= funcs.argSize(); i++) {
        IExpr gDummy = dummyVars.get(i);
        IExpr solvedValue = F.NIL;

        for (int j = 1; j <= firstSol.argSize(); j++) {
          IExpr rule = firstSol.get(j);
          if (rule.isRule() && rule.first().equals(gDummy)) {
            solvedValue = rule.second();
            break;
          }
        }

        if (solvedValue.isPresent()) {
          IExpr inverseZ = engine.evaluate(F.InverseZTransform(solvedValue, zDummy, nVar));
          IExpr cleanInverse = inverseZ.isAST(S.InverseZTransform) ? inverseZ
              : engine.evaluate(F.Simplify(inverseZ));

          if (!cleanInverse.isFree(S.ZTransform) || !cleanInverse.isFree(S.InverseZTransform)) {
            return F.NIL;
          }

          for (int h = 1; h <= funcs.argSize(); h++) {
            IExpr tFunc = funcs.get(h).isAST1() ? funcs.get(h) : F.unaryAST1(funcs.get(h), nVar);
            if (!cleanInverse.isFree(tFunc)) {
              return F.NIL;
            }
          }
          cleanInverses[i - 1] = cleanInverse;
        } else {
          return F.NIL;
        }
      }

      IASTAppendable initVals = F.ListAlloc();
      for (int i = 1; i <= funcs.argSize(); i++) {
        for (int h = 1; h <= funcs.argSize(); h++) {
          IExpr tFunc = funcs.get(h).isAST1() ? funcs.get(h) : F.unaryAST1(funcs.get(h), nVar);
          extractInitialValues(cleanInverses[i - 1], tFunc.head(), initVals);
        }
      }

      IASTAppendable rules = F.ListAlloc();
      int cIndex = engine.incConstantCounter();
      for (int i = 1; i <= initVals.argSize(); i++) {
        rules.append(F.Rule(initVals.get(i), F.C(cIndex++)));
      }

      for (int i = 1; i <= funcs.argSize(); i++) {
        IExpr cleanInverse = cleanInverses[i - 1];
        if (rules.argSize() > 0) {
          cleanInverse = engine.evaluate(F.subst(cleanInverse, rules));
        }

        cleanInverse = applyFibonacciLucas(cleanInverse, nVar, engine);

        if (rules.argSize() > 0) {
          cleanInverse = engine.evaluate(F.ExpandAll(cleanInverse));
          IExpr cPattern = F.unaryAST1(S.C, F.$b());
          cleanInverse = engine.evaluate(F.Collect(cleanInverse, cPattern));
        }

        IExpr originalTarget = funcs.get(i);
        if (originalTarget.isSymbol()) {
          resultSystem.append(F.Rule(originalTarget, F.Function(F.List(nVar), cleanInverse)));
        } else {
          resultSystem.append(F.Rule(originalTarget, cleanInverse));
        }
      }
      return F.List(resultSystem);
    }
    return F.NIL;
  }
}
