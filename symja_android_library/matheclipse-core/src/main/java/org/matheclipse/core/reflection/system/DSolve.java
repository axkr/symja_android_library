package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.convert.VariablesSet;
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

/**
 * <pre>
 * DSolve(equation, f(var), var)
 * </pre>
 *
 * <blockquote>
 * <p>
 * Attempts to solve a linear differential <code>equation</code> for the function
 * <code>f(var)</code> and variable <code>var</code>.
 * </p>
 * </blockquote>
 *
 * <p>
 * DSolve can solve ordinary differential equations (ODEs), partial differential equations (PDEs),
 * differential algebraic equations (DAEs), delay differential equations (DDEs), integral equations,
 * integro-differential equations, and hybrid differential equations.
 * </p>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; DSolve({y'(x)==y(x)+2},y(x), x)
 * {{y(x)-&gt;-2+E^x*C(1)}}
 *
 * &gt;&gt; DSolve({y''(x) + y(x) == 0}, y(x), x)
 * {{y(x)-&gt;C(1)*Cos(x)+C(2)*Sin(x)}}
 * </pre>
 */
public class DSolve extends AbstractFunctionEvaluator {

  public static int derivativeOrder(IAST[] deriveExpr) {
    // needed in NDSolve
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
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.DSolve, rex, EvalEngine.get());
    }
    return order;
  }

  private static IExpr odeExact(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y, IExpr C_1) {
    // Substitute y(x) with a dummy variable Y to treat it as an independent variable
    // for partial differentiation and integration without triggering the chain rule.
    IExpr yDummy = F.Dummy("Y");
    IAST ruleList = F.list(F.Rule(y, yDummy));

    IExpr mDummy = engine.evaluate(F.subst(m, ruleList));
    IExpr nDummy = engine.evaluate(F.subst(n, ruleList));

    // Check for exactness: dM/dY == dN/dx
    IExpr dMdy = engine.evaluate(S.D.of(engine, mDummy, yDummy));
    IExpr dNdx = engine.evaluate(S.D.of(engine, nDummy, x));

    IExpr diff = engine.evaluate(S.Simplify.of(engine, S.Subtract.of(engine, dMdy, dNdx)));

    if (diff.isZero()) {
      // f(x,Y) = Integrate(M, x)
      IExpr intM = engine.evaluate(F.Integrate(mDummy, x));

      // N - d/dY(intM)
      IExpr dIntMdy = engine.evaluate(S.D.of(engine, intM, yDummy));
      IExpr gPrime = engine.evaluate(S.Subtract.of(engine, nDummy, dIntMdy));

      // g(Y) = Integrate(gPrime, Y)
      IExpr gy = engine.evaluate(F.Integrate(gPrime, yDummy));

      // The implicit solution is intM + gy = C_1
      IExpr f_xy = engine.evaluate(F.Plus(intM, gy));

      // Substitute y(x) back
      IAST backRule = F.list(F.Rule(yDummy, y));
      IExpr f_xy_real = engine.evaluate(F.subst(f_xy, backRule));

      IExpr equation = F.Equal(f_xy_real, C_1);

      // Attempt to extract explicit y(x) from the implicit equation
      IExpr ySols = engine.evaluate(F.Solve(equation, F.List(y)));
      if (ySols.isList() && ((IAST) ySols).argSize() > 0) {
        IASTAppendable roots = F.ListAlloc();
        for (int i = 1; i <= ((IAST) ySols).argSize(); i++) {
          IAST solList = (IAST) ((IAST) ySols).get(i);
          if (solList.argSize() >= 1 && solList.arg1().isRule()) {
            IExpr sol = stripConditionalExpression(solList.arg1().second());
            roots.append(engine.evaluate(S.Simplify.of(engine, sol)));
          }
        }

        if (roots.argSize() == 1) {
          return roots.arg1();
        } else if (roots.argSize() > 1) {
          return roots; // Return all roots as a List
        }
      }
    }
    return F.NIL;
  }

  /**
   * Solves homogeneous first-order ODEs by applying the substitution y = v * x to reduce the
   * equation to a separable form.
   */
  private static IExpr odeHomogeneous(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y,
      IExpr C_1) {
    // Substitute y -> x * v
    IExpr v = F.Dummy("v");
    IAST ruleList = F.list(F.Rule(y, F.Times(x, v)));

    IExpr mSub = engine.evaluate(F.subst(m, ruleList));
    IExpr nSub = engine.evaluate(F.subst(n, ruleList));

    // Transform to separable: M_v dx + N_v dv = 0 => (mSub + v * nSub) dx + (x * nSub) dv = 0
    IExpr m_v = engine.evaluate(S.Simplify.of(engine, F.Plus(mSub, F.Times(v, nSub))));
    IExpr n_v = engine.evaluate(S.Simplify.of(engine, F.Times(x, nSub)));

    // Normalize so that the coefficient of dv is exactly 1
    IExpr normalizedM = engine.evaluate(S.Factor.of(engine, F.Divide(m_v, n_v)));

    // Try to solve the transformed equation using the existing separable solver
    IExpr vSol = odeSeparable(engine, normalizedM, F.C1, x, v, C_1);

    if (vSol.isPresent()) {
      // Substitute v -> y/x back into the solution
      IAST backRule = F.list(F.Rule(v, F.Divide(y, x)));
      IExpr yEquation = engine.evaluate(F.subst(vSol, backRule));

      // Extract explicit y(x)
      IExpr result = Eliminate.extractVariable(yEquation, y, false, engine);
      if (result.isPresent()) {
        result = stripConditionalExpression(result);
        return engine.evaluate(S.Simplify.of(engine, result));
      }
    }
    return F.NIL;
  }

  /**
   * Solves ODEs by finding an integrating factor to make the equation exact.
   */
  private static IExpr odeIntegratingFactor(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y,
      IExpr C_1) {
    // Substitute y(x) with a dummy variable Y for partial derivatives
    IExpr yDummy = F.Dummy("Y");
    IAST ruleList = F.List(F.Rule(y, yDummy));

    IExpr mDummy = engine.evaluate(F.subst(m, ruleList));
    IExpr nDummy = engine.evaluate(F.subst(n, ruleList));

    IExpr dMdy = engine.evaluate(S.D.of(engine, mDummy, yDummy));
    IExpr dNdx = engine.evaluate(S.D.of(engine, nDummy, x));

    // Case 1: Integrating factor depends only on x
    // Check if (dM/dy - dN/dx) / N == f(x)
    IExpr diff1 =
        engine.evaluate(S.Simplify.of(engine, F.Divide(S.Subtract.of(engine, dMdy, dNdx), nDummy)));

    if (diff1.isFree(yDummy)) {
      IExpr mu = engine.evaluate(F.Exp(F.Integrate(diff1, x)));
      IExpr exactM = engine.evaluate(F.Times(mu, m));
      IExpr exactN = engine.evaluate(F.Times(mu, n));

      // The equation is now exact, pass it back to our exact solver
      return odeExact(engine, exactM, exactN, x, y, C_1);
    }

    // Case 2: Integrating factor depends only on y
    // Check if (dN/dx - dM/dy) / M == g(y)
    IExpr diff2 =
        engine.evaluate(S.Simplify.of(engine, F.Divide(S.Subtract.of(engine, dNdx, dMdy), mDummy)));

    if (diff2.isFree(x)) {
      IExpr muDummy = engine.evaluate(F.Exp(F.Integrate(diff2, yDummy)));

      // Substitute back y(x) into the integrating factor
      IAST backRule = F.List(F.Rule(yDummy, y));
      IExpr mu = engine.evaluate(F.subst(muDummy, backRule));

      IExpr exactM = engine.evaluate(F.Times(mu, m));
      IExpr exactN = engine.evaluate(F.Times(mu, n));

      return odeExact(engine, exactM, exactN, x, y, C_1);
    }

    return F.NIL;
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
        IASTAppendable fx = F.TimesAlloc();
        IASTAppendable gy = F.TimesAlloc();

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
          result = stripConditionalExpression(result);
          return engine.evaluate(result);
        }
      }
    }
    return F.NIL;
  }

  private static IExpr odeSolve(EvalEngine engine, IExpr w, IExpr x, IExpr y, IExpr C_1) {
    IExpr[] p = odeTransform(engine, w, x, y);
    if (p != null) {
      IExpr m = p[0];
      IExpr n = p[1];

      // Try separable first
      IExpr f = odeSeparable(engine, m, n, x, y, C_1);
      if (f.isPresent()) {
        return f;
      }

      f = odeExact(engine, m, n, x, y, C_1);
      if (f.isPresent()) {
        return f;
      }

      f = odeIntegratingFactor(engine, m, n, x, y, C_1);
      if (f.isPresent()) {
        return f;
      }

      f = odeHomogeneous(engine, m, n, x, y, C_1);
      if (f.isPresent()) {
        return f;
      }
    }
    return F.NIL;
  }

  private static IExpr[] odeTransform(EvalEngine engine, IExpr w, IExpr x, IExpr y) {
    // Convert equation to an expression (lhs - rhs)
    IExpr expr = w;
    if (w.isEqual()) {
      expr = S.Subtract.of(engine, w.first(), w.second());
    }

    IExpr v = S.Together.of(engine, expr);
    IExpr numerator = S.Numerator.of(engine, v);
    IExpr dyx = S.D.of(engine, y, x);

    IExpr m = S.Coefficient.of(engine, numerator, dyx, F.C0);
    IExpr n = S.Coefficient.of(engine, numerator, dyx, F.C1);

    return new IExpr[] {m, n};
  }

  /**
   * Helper to securely unwrap rigorous ConditionalExpression outputs from Integration / Solvers
   * into generic formulas for Differential Equations.
   */
  private static IExpr stripConditionalExpression(IExpr expr) {
    if (expr.isAST(S.ConditionalExpression) && ((IAST) expr).argSize() >= 1) {
      return stripConditionalExpression(expr.first());
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IASTAppendable result = F.ast(ast.head(), ast.argSize());
      boolean changed = false;
      for (int i = 1; i <= ast.argSize(); i++) {
        IExpr arg = ast.get(i);
        IExpr stripped = stripConditionalExpression(arg);
        if (stripped != arg) {
          changed = true;
        }
        result.append(stripped);
      }
      return changed ? result : expr;
    }
    return expr;
  }

  public DSolve() {}

  /**
   * Absorbs numeric multipliers and overlapping basis terms into arbitrary constants cleanly. E.g.,
   * C(1)*2*Cos(x) - 7/4*Cos(x) collapses to simply C(1)*Cos(x).
   */
  private IExpr absorbConstants(IExpr expr, IAST cVector, EvalEngine engine) {
    IExpr expanded = engine.evaluate(S.ExpandAll.of(engine, expr));
    IAST terms;
    if (expanded.isPlus()) {
      terms = (IAST) expanded;
    } else {
      terms = F.Plus(expanded);
    }

    java.util.Set<IExpr> cSet = new java.util.HashSet<>();
    cVector.forEach(cSet::add);

    // Use LinkedHashMap to ensure deterministic assignment of C constants
    java.util.Map<IExpr, IASTAppendable> basisMap = new java.util.LinkedHashMap<>();
    java.util.Set<IExpr> basisWithC = new java.util.HashSet<>();

    for (IExpr term : terms) {
      IExpr[] cPart = new IExpr[] {null};
      IExpr basis = extractBasis(term, cSet, cPart);

      basisMap.putIfAbsent(basis, F.PlusAlloc());
      basisMap.get(basis).append(term);

      if (cPart[0] != null) {
        basisWithC.add(basis);
      }
    }

    IASTAppendable resultPlus = F.PlusAlloc();
    int cIndex = 1;

    for (java.util.Map.Entry<IExpr, IASTAppendable> entry : basisMap.entrySet()) {
      IExpr basis = entry.getKey();
      IASTAppendable sumOfTerms = entry.getValue();

      if (basisWithC.contains(basis)) {
        if (cIndex <= cVector.argSize()) {
          IExpr freshC = cVector.get(cIndex++);
          resultPlus.append(engine.evaluate(F.Times(freshC, basis)));
        } else {
          // Fallback if we have more bases with constants than available constants
          resultPlus.append(engine.evaluate(sumOfTerms));
        }
      } else {
        resultPlus.append(engine.evaluate(sumOfTerms));
      }
    }

    return engine.evaluate(resultPlus);
  }

  /**
   * Evaluates boundary conditions against the generated homogeneous constants
   */
  private void applySystemBCs(java.util.Map<IExpr, IExpr> solMap, IAST bcs, IExpr xVar,
      EvalEngine engine) {
    if (bcs == null || !bcs.isPresent() || bcs.argSize() == 0)
      return;

    IASTAppendable headRules = F.ListAlloc();
    for (java.util.Map.Entry<IExpr, IExpr> entry : solMap.entrySet()) {
      IExpr var = entry.getKey();
      if (var.isAST1()) {
        headRules.append(F.Rule(var.head(), F.Function(F.List(xVar), entry.getValue())));
      }
    }

    IASTAppendable evaluatedBCs = F.ListAlloc();
    for (int k = 1; k <= bcs.argSize(); k++) {
      evaluatedBCs.append(engine.evaluate(F.subst(bcs.get(k), headRules)));
    }

    IAST varsInBCs = VariablesSet.getAlgebraicVariables(evaluatedBCs, false);
    IASTAppendable cVars = F.ListAlloc();
    if (varsInBCs.isList()) {
      for (IExpr v : varsInBCs) {
        if (v.isAST(S.C, 2)) {
          cVars.append(v);
        }
      }
    }

    if (cVars.argSize() > 0) {
      IAST evaluatedBCsEqualZero = evaluatedBCs.map(t -> F.Equal(t, F.C0));
      IExpr cSols = engine.evaluate(F.Solve(evaluatedBCsEqualZero, cVars));
      if (cSols.isList() && ((IAST) cSols).argSize() > 0) {
        IAST cSol = (IAST) ((IAST) cSols).arg1();
        for (java.util.Map.Entry<IExpr, IExpr> entry : solMap.entrySet()) {
          IExpr updated = engine.evaluate(F.subst(entry.getValue(), cSol));
          solMap.put(entry.getKey(), engine.evaluate(S.Simplify.of(engine, updated)));
        }
      }
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!ToggleFeature.DSOLVE) {
      return F.NIL;
    }

    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    IExpr xVar = ast.arg3();

    IASTAppendable listOfVariables = F.ListAlloc();

    if (arg2.isList()) {
      listOfVariables = F.ListAlloc(((IAST) arg2).argSize());
      for (int i = 1; i <= ((IAST) arg2).argSize(); i++) {
        IExpr v = ((IAST) arg2).get(i);
        if (v.isAST1() && v.first().equals(xVar)) {
          listOfVariables.append(v);
        } else if (v.isSymbol() && xVar.isSymbol()) {
          listOfVariables.append(F.unaryAST1(v, xVar));
        } else {
          return F.NIL;
        }
      }
    } else if (arg2.isAST1() && arg2.first().equals(xVar)) {
      listOfVariables.append(arg2);
      if (arg1.isFree(arg2.head()) || arg1.isFree(xVar)) {
        return F.NIL;
      }
    } else if (arg2.isSymbol() && xVar.isSymbol()) {
      if (arg1.isFree(arg2) || arg1.isFree(xVar)) {
        return F.NIL;
      }
      listOfVariables.append(F.unaryAST1(arg2, xVar));
    }

    if (listOfVariables.isPresent()) {
      IASTAppendable listOfEquations = Validate.checkEquations(ast, 1).copyAppendable();

      // Extract the bare symbols of the target functions (e.g., y(x) -> y)
      java.util.Set<IExpr> fHeads = new java.util.HashSet<>();
      for (int i = 1; i <= listOfVariables.argSize(); i++) {
        IExpr var = listOfVariables.get(i);
        fHeads.add(var.isAST() ? var.head() : var);
      }

      // Validate all equations for missing arguments on functions or derivatives
      for (int i = 1; i <= listOfEquations.argSize(); i++) {
        IExpr eq = listOfEquations.get(i);
        IExpr badVar = findMissingArgs(eq, fHeads);
        if (badVar.isPresent()) {
          // The function `1` appears with no arguments.
          return Errors.printMessage(S.DSolve, "dvnoarg", F.List(badVar));
        }
      }

      if (listOfVariables.argSize() == 1) {
        // Try to extract boundary conditions for unary ODEs
        IExpr[] boundaryCondition = null;
        int i = 1;
        while (i <= listOfEquations.argSize()) {
          IExpr equation = listOfEquations.get(i);
          if (equation.isFree(xVar)) {
            boundaryCondition =
                solveSingleBoundary(equation, (IAST) listOfVariables.arg1(), xVar, engine);
            if (boundaryCondition != null) {
              listOfEquations.remove(i);
              break;
            }
          }
          i++;
        }
        return unaryODE((IAST) listOfVariables.arg1(), arg2.isList() ? ((IAST) arg2).arg1() : arg2,
            xVar, listOfEquations, boundaryCondition, engine);
      } else {
        // Extract boundary conditions for the system solver globally
        IASTAppendable bcs = F.ListAlloc();
        int i = 1;
        while (i <= listOfEquations.argSize()) {
          IExpr equation = listOfEquations.get(i);
          if (equation.isFree(xVar)) {
            bcs.append(equation);
            listOfEquations.remove(i);
          } else {
            i++;
          }
        }
        // Solve Linear System of ODEs / DAEs
        return solveSystemODE(listOfVariables, arg2, xVar, listOfEquations, bcs, engine);
      }
    }
    return F.NIL;
  }


  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  private IExpr extractBasis(IExpr term, java.util.Set<IExpr> cSet, IExpr[] cPart) {
    if (term.isTimes()) {
      IASTAppendable basis = F.TimesAlloc();
      for (IExpr arg : (IAST) term) {
        if (arg.isNumber()) {
          continue;
        } else if (cSet.contains(arg)) {
          cPart[0] = arg;
        } else {
          basis.append(arg);
        }
      }
      return basis.argSize() == 0 ? F.C1 : (basis.argSize() == 1 ? basis.arg1() : basis);
    } else {
      if (term.isNumber())
        return F.C1;
      if (cSet.contains(term)) {
        cPart[0] = term;
        return F.C1;
      }
      return term;
    }
  }

  /**
   * Recursively scans an expression to find any target function or derivative that is missing its
   * independent variable arguments (e.g., 'y' or 'y^P' instead of 'y(x)').
   */
  private IExpr findMissingArgs(IExpr expr, java.util.Set<IExpr> fHeads) {
    // 1. Bare symbol check: e.g., 'y'
    if (expr.isSymbol() && fHeads.contains(expr)) {
      return expr;
    }

    // 2. Bare derivative check: e.g., 'Derivative(1)[y]'
    if (isDerivativeOf(expr, fHeads)) {
      return expr;
    }

    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();

      // Check all arguments
      for (int i = 1; i <= ast.argSize(); i++) {
        IExpr bad = findMissingArgs(ast.get(i), fHeads);
        if (bad.isPresent()) {
          return bad;
        }
      }

      // Check the head, BUT skip if this AST is a valid application:
      // a) y(x) -> head is y
      // b) Derivative(n)[y][x] -> head is Derivative(n)[y]
      boolean isValidApplication = false;
      if (fHeads.contains(head) || isDerivativeOf(head, fHeads)) {
        isValidApplication = true;
      }

      if (!isValidApplication) {
        IExpr bad = findMissingArgs(head, fHeads);
        if (bad.isPresent()) {
          return bad;
        }
      }
    }
    return F.NIL;
  }

  private int getHighestDerivativeOrder(EvalEngine engine, IExpr lhs, IExpr yFunction, IExpr xVar) {
    int maxOrder = 0;
    for (int k = 1; k <= 10; k++) {
      IExpr dyx = engine.evaluate(F.D(yFunction, F.List(xVar, F.ZZ(k))));
      IExpr coeff = engine.evaluate(S.Coefficient.of(engine, lhs, dyx));
      if (!coeff.isZero()) {
        maxOrder = k;
      }
    }
    return maxOrder;
  }

  /**
   * Checks if the given expression is structurally Derivative(...)[f] where f is one of the target
   * function symbols.
   */
  private boolean isDerivativeOf(IExpr expr, java.util.Set<IExpr> fHeads) {
    if (expr.isAST() && expr.head().isAST(S.Derivative)) {
      if (expr.isAST1() && fHeads.contains(((IAST) expr).arg1())) {
        return true;
      }
    }
    return false;
  }

  private IExpr linearODE(IExpr coefficient1, IExpr coefficient0, IExpr xVar, IExpr C_1,
      EvalEngine engine) {
    IExpr pInt = engine.evaluate(F.Exp(F.Integrate(coefficient1, xVar)));

    if (coefficient0.isZero()) {
      return F.Divide(C_1, pInt).eval(engine);
    } else {
      IExpr qInt = engine
          .evaluate(F.Plus(C_1, F.Expand(F.Integrate(F.Times(F.CN1, coefficient0, pInt), xVar))));
      return F.Expand(F.Divide(qInt, pInt)).eval(engine);
    }
  }

  /**
   * Helper to cleanly multiply an N x N matrix by a scalar to avoid Times listability edge cases.
   */
  private IExpr multiplyMatrixScalar(IExpr matrix, IExpr scalar, int n, EvalEngine engine) {
    if (!matrix.isList())
      return F.NIL;
    IASTAppendable res = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      IExpr row = ((IAST) matrix).get(i);
      if (!row.isList())
        return F.NIL;
      IASTAppendable newRow = F.ListAlloc(n);
      for (int j = 1; j <= n; j++) {
        newRow.append(engine.evaluate(F.Times(((IAST) row).get(j), scalar)));
      }
      res.append(newRow);
    }
    return res;
  }

  private IExpr solveHigherOrderLinearODE(IExpr lhs, IExpr yFunction, IExpr xVar, int n,
      EvalEngine engine) {
    IExpr head = yFunction.head();
    IExpr[] coeffs = new IExpr[n + 1];
    IExpr rest = lhs;

    for (int i = n; i >= 1; i--) {
      IExpr dyx = engine.evaluate(F.D(yFunction, F.List(xVar, F.ZZ(i))));
      IExpr c = engine.evaluate(S.Coefficient.of(engine, rest, dyx));
      if (!c.isFree(head) || !c.isFree(xVar)) {
        return F.NIL; // Coefficients must be constant for matrix system method
      }
      coeffs[i] = c;
      rest = engine.evaluate(S.Subtract.of(engine, rest, F.Times(c, dyx)));
    }

    IExpr c0 = engine.evaluate(S.Coefficient.of(engine, rest, yFunction));
    if (!c0.isFree(head) || !c0.isFree(xVar)) {
      return F.NIL;
    }
    coeffs[0] = c0;

    IExpr freeTerm = engine.evaluate(S.Subtract.of(engine, rest, F.Times(c0, yFunction)));
    if (!freeTerm.isFree(head)) {
      return F.NIL;
    }

    IASTAppendable sysVars = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      sysVars.append(F.unaryAST1(F.Dummy("Y" + i), xVar));
    }

    IASTAppendable sysEqns = F.ListAlloc(n);
    for (int i = 1; i < n; i++) {
      sysEqns.append(F.Equal(S.D.of(engine, sysVars.get(i), xVar), sysVars.get(i + 1)));
    }

    IASTAppendable lastEqnRhs = F.PlusAlloc(n + 1);
    lastEqnRhs.append(F.Negate(freeTerm));
    for (int i = 0; i < n; i++) {
      lastEqnRhs.append(F.Times(F.CN1, coeffs[i], sysVars.get(i + 1)));
    }

    IExpr yNPrime = engine.evaluate(F.Divide(lastEqnRhs, coeffs[n]));
    sysEqns.append(F.Equal(S.D.of(engine, sysVars.get(n), xVar), yNPrime));

    int startC = engine.incConstantCounter();
    IExpr sysSol = solveSystemODE(sysVars, sysVars, xVar, sysEqns, F.NIL, engine);

    if (sysSol.isList() && ((IAST) sysSol).argSize() > 0) {
      IAST solutionList = (IAST) ((IAST) sysSol).arg1();
      IExpr y1Rule = solutionList.arg1();
      IExpr rawResult = stripConditionalExpression(y1Rule.second());

      // Safely apply absorbConstants here, as this represents a single extracted scalar function
      IASTAppendable cVector = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        cVector.append(F.C(startC + i));
      }
      rawResult = absorbConstants(rawResult, cVector, engine);

      IASTAppendable rules = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        rules.append(F.Rule(F.C(startC + i), F.C(i)));
      }
      return engine.evaluate(F.subst(rawResult, rules));
    }

    return F.NIL;
  }

  /**
   * Solves a Riccati equation of the form: y' = a*y^2 + b*y + c
   */
  private IExpr solveRiccati(IExpr a, IExpr b, IExpr c, IExpr xVar, IExpr yFunction, IExpr C_1,
      EvalEngine engine) {
    // Strategy 1: Constants Coefficients (Separation of Variables)
    if (a.isFree(xVar) && b.isFree(xVar) && c.isFree(xVar)) {
      IExpr ySym = F.Dummy("Y");
      IExpr denominator = F.Plus(c, F.Times(b, ySym), F.Times(a, F.Sqr(ySym)));
      IExpr integral = engine.evaluate(F.Integrate(F.Divide(F.C1, denominator), ySym));

      IExpr eq = F.Equal(integral, F.Plus(xVar, C_1));
      IExpr ySols = engine.evaluate(F.Solve(eq, F.List(ySym)));

      if (ySols.isList() && ((IAST) ySols).argSize() > 0) {
        IAST firstSol = (IAST) ((IAST) ySols).arg1();
        if (firstSol.argSize() >= 1 && firstSol.arg1().isRule()) {
          IExpr sol = stripConditionalExpression(firstSol.arg1().second());
          return engine.evaluate(S.Simplify.of(engine, sol));
        }
      }
    }

    // Strategy 2: Substitution into Second-Order Linear ODE
    // Projection: y = -u' / (a * u)
    IExpr uSym = F.Dummy("u");
    IExpr u = F.unaryAST1(uSym, xVar);
    IExpr uPrime = F.D(u, xVar);
    IExpr uDoublePrime = F.D(uPrime, xVar);

    IExpr aPrime = engine.evaluate(F.D(a, xVar));
    IExpr coeffUPrime = engine.evaluate(F.Subtract(b, F.Divide(aPrime, a)));

    IExpr uEq =
        F.Equal(F.Plus(uDoublePrime, F.Times(F.CN1, coeffUPrime, uPrime), F.Times(a, c, u)), F.C0);

    // Prevent infinite recursion: ensure transformed ODE remains natively solvable
    if (coeffUPrime.isFree(xVar) && engine.evaluate(F.Times(a, c)).isFree(xVar)) {
      IExpr uSols = engine.evaluate(F.DSolve(F.List(uEq), F.List(u), xVar));
      if (uSols.isList() && ((IAST) uSols).argSize() > 0) {
        IAST firstSol = (IAST) ((IAST) uSols).arg1();
        IExpr uSolExpr = F.NIL;

        if (firstSol.argSize() >= 1 && firstSol.arg1().isRule()) {
          uSolExpr = firstSol.arg1().second();
        }

        if (uSolExpr.isPresent()) {
          uSolExpr = stripConditionalExpression(uSolExpr);
          if (uSolExpr.isAST(S.Function)) {
            uSolExpr = engine.evaluate(F.unaryAST1(uSolExpr, xVar));
          }

          IExpr uSolPrime = engine.evaluate(F.D(uSolExpr, xVar));
          IExpr ySol = engine.evaluate(F.Divide(F.Negate(uSolPrime), F.Times(a, uSolExpr)));

          // Homogeneous Riccati reduction yields one redundant arbitrary constant. Let's strictly
          // absorb.
          IExpr cVarsList =
              engine.evaluate(S.Cases.of(engine, ySol, F.unaryAST1(S.C, F.$b()), F.Infinity));
          cVarsList = engine.evaluate(S.DeleteDuplicates.of(engine, cVarsList));

          if (cVarsList.isList() && ((IAST) cVarsList).argSize() >= 1) {
            IASTAppendable replaceRules = F.ListAlloc();
            IExpr cFirst = ((IAST) cVarsList).arg1();
            replaceRules.append(F.Rule(cFirst, C_1));
            if (((IAST) cVarsList).argSize() >= 2) {
              IExpr cSecond = ((IAST) cVarsList).arg2();
              replaceRules.append(F.Rule(cSecond, F.C1));
            }
            ySol = engine.evaluate(F.subst(ySol, replaceRules));
          }

          return engine.evaluate(S.Simplify.of(engine, ySol));
        }
      }
    }

    return F.NIL;
  }

  private IExpr[] solveSingleBoundary(IExpr equation, IAST uFunction1Arg, IExpr xVar,
      EvalEngine engine) {
    if (equation.isAST()) {
      IASTAppendable eq = ((IAST) equation).copyAppendable();
      if (!eq.isPlus()) {
        eq = F.Plus(eq);
      }

      int j = 1;
      IExpr uArg1 = null;
      IExpr head = uFunction1Arg.head();

      while (j <= eq.argSize()) {
        if (eq.get(j).isAST(head, uFunction1Arg.argSize() + 1)) {
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

  private IExpr solveSingleODE(IExpr equation, IExpr xVar, IAST listOfVariables, IExpr C_1,
      EvalEngine engine) {
    IExpr yFunction = listOfVariables.arg1();
    IExpr head = yFunction.head();

    IExpr lhs = equation;
    if (equation.isEqual()) {
      lhs = S.Subtract.of(engine, equation.first(), equation.second());
    }
    lhs = engine.evaluate(S.ExpandAll.of(engine, lhs));

    IExpr dyx = S.D.of(engine, yFunction, xVar);

    // Attempt: Clairaut's Equation
    // Substitute y'(x) with a dummy variable `p`. We use S.ReplaceAll.of instead of F.subst
    // to guarantee the derivative AST (Derivative(1)[y][x]) is structurally swapped out.
    IExpr pClairaut = F.Dummy("p");
    IExpr lhsP = engine.evaluate(S.ReplaceAll.of(engine, lhs, F.Rule(dyx, pClairaut)));

    // Matches forms where y - x*p - f(p) = 0 or -y + x*p + f(p) = 0
    // We use S.ExpandAll.of here to aggressively force algebraic cancellation of y and x*p.
    IExpr clairautTest1 = engine.evaluate(
        S.ExpandAll.of(engine, F.Subtract(lhsP, F.Subtract(yFunction, F.Times(xVar, pClairaut)))));
    IExpr clairautTest2 = engine.evaluate(
        S.ExpandAll.of(engine, F.Plus(lhsP, F.Subtract(yFunction, F.Times(xVar, pClairaut)))));

    if (clairautTest1.isFree(xVar) && clairautTest1.isFree(yFunction)) {
      // lhs = y - x*y' + clairautTest1 = 0 => y = C_1*x - clairautTest1(C_1)
      IExpr f_c = engine.evaluate(S.ReplaceAll.of(engine, clairautTest1, F.Rule(pClairaut, C_1)));
      IExpr ySol = engine.evaluate(S.ExpandAll.of(engine, F.Subtract(F.Times(C_1, xVar), f_c)));
      return engine.evaluate(S.Simplify.of(engine, ySol));
    } else if (clairautTest2.isFree(xVar) && clairautTest2.isFree(yFunction)) {
      // lhs = -y + x*y' + clairautTest2 = 0 => y = C_1*x + clairautTest2(C_1)
      IExpr f_c = engine.evaluate(S.ReplaceAll.of(engine, clairautTest2, F.Rule(pClairaut, C_1)));
      IExpr ySol = engine.evaluate(S.ExpandAll.of(engine, F.Plus(F.Times(C_1, xVar), f_c)));
      return engine.evaluate(S.Simplify.of(engine, ySol));
    }

    int n = getHighestDerivativeOrder(engine, lhs, yFunction, xVar);

    if (n > 1) {
      return solveHigherOrderLinearODE(lhs, yFunction, xVar, n, engine);
    }

    IExpr coeffDyx = engine.evaluate(S.Coefficient.of(engine, lhs, dyx));

    if (!coeffDyx.isZero() && coeffDyx.isFree(head)) {
      IExpr rest = engine.evaluate(S.Subtract.of(engine, lhs, F.Times(coeffDyx, dyx)));
      IExpr coeffY = engine.evaluate(S.Coefficient.of(engine, rest, yFunction));

      // Attempt 1: Standard First-Order Linear ODE
      if (coeffY.isFree(head)) {
        IExpr freeTerm = engine.evaluate(S.Subtract.of(engine, rest, F.Times(coeffY, yFunction)));

        if (freeTerm.isFree(head)) {
          IExpr p = engine.evaluate(F.Divide(coeffY, coeffDyx));
          IExpr q = engine.evaluate(F.Divide(freeTerm, coeffDyx));
          return linearODE(p, q, xVar, C_1, engine);
        }
      }


      // Attempt 1.5: General Bernoulli Equation
      // Look for the pattern: coeffDyx * y' + coeffY * y + coeffYn * y^n = 0
      IExpr nonLinearPart =
          engine.evaluate(S.Subtract.of(engine, rest, F.Times(coeffY, yFunction)));
      IExpr nExpr = F.NIL;
      IExpr coeffYn = F.NIL;

      if (!nonLinearPart.isZero()) {
        if (nonLinearPart.isPower() && nonLinearPart.first().equals(yFunction)) {
          coeffYn = F.C1;
          nExpr = nonLinearPart.second();
        } else if (nonLinearPart.isTimes()) {
          IASTAppendable remainingCoeff = F.TimesAlloc();
          for (int i = 1; i <= nonLinearPart.argSize(); i++) {
            IExpr arg = ((IAST) nonLinearPart).get(i);
            if (arg.isPower() && arg.first().equals(yFunction)) {
              nExpr = arg.second();
            } else {
              remainingCoeff.append(arg);
            }
          }
          if (nExpr.isPresent()) {
            coeffYn = remainingCoeff.argSize() == 1 ? remainingCoeff.arg1() : remainingCoeff;
          }
        }

        // If a valid exponent n is found and coefficients are independent of y
        if (nExpr.isPresent() && coeffYn.isFree(head) && nExpr.isFree(head) && nExpr.isFree(xVar)) {
          // Linearize using u = y^(1-n)
          IExpr oneMinusN = engine.evaluate(S.Subtract.of(engine, F.C1, nExpr));
          IExpr p_u = engine.evaluate(F.Times(oneMinusN, F.Divide(coeffY, coeffDyx)));
          IExpr q_u = engine.evaluate(F.Times(oneMinusN, F.Divide(coeffYn, coeffDyx)));

          // Negate C_1 if p_u is zero to match legacy separable behavior
          IExpr cConstant = C_1;
          if (p_u.isZero()) {
            cConstant = engine.evaluate(F.Times(F.CN1, C_1));
          }

          IExpr uSol = linearODE(p_u, q_u, xVar, cConstant, engine);

          if (uSol.isPresent()) {
            IExpr invPower = engine.evaluate(F.Divide(F.C1, oneMinusN));
            IExpr ySol = engine.evaluate(F.Power(uSol, invPower));
            return engine.evaluate(S.Simplify.of(engine, ySol));
          }
        }
      }

      // Attempt 2: Riccati or Bernoulli Polynomial Extraction
      IExpr expandedRest = engine.evaluate(S.ExpandAll.of(engine, rest));
      IExpr q0 = engine.evaluate(S.Coefficient.of(engine, expandedRest, yFunction, F.C0));
      IExpr q1 = engine.evaluate(S.Coefficient.of(engine, expandedRest, yFunction, F.C1));
      IExpr q2 = engine.evaluate(S.Coefficient.of(engine, expandedRest, yFunction, F.C2));

      IExpr remainder = engine.evaluate(S.ExpandAll.of(engine, S.Subtract.of(engine, expandedRest,
          F.Plus(q0, F.Times(q1, yFunction), F.Times(q2, F.Sqr(yFunction))))));

      if (remainder.isZero() && !q2.isZero()) {
        IExpr a = engine.evaluate(F.Divide(F.Negate(q2), coeffDyx));
        IExpr b = engine.evaluate(F.Divide(F.Negate(q1), coeffDyx));
        IExpr c = engine.evaluate(F.Divide(F.Negate(q0), coeffDyx));

        if (c.isZero()) {
          // Bernoulli Equation (n=2): Substitute and route dynamically to LinearODE
          IExpr cConstant = C_1;
          if (b.isZero()) {
            // For purely separable Bernoulli (y' = a*y^2), negate C_1 to exactly match legacy
            // odeSolve behavior
            cConstant = engine.evaluate(F.Times(F.CN1, C_1));
          }
          IExpr uSol = linearODE(b, a, xVar, cConstant, engine);
          if (uSol.isPresent()) {
            return engine.evaluate(F.Power(uSol, F.CN1));
          }
        } else {
          // Full Riccati Substitution Logic
          IExpr riccatiSol = solveRiccati(a, b, c, xVar, yFunction, C_1, engine);
          if (riccatiSol.isPresent()) {
            return riccatiSol;
          }
        }
      }
    }

    return F.NIL;
  }

  /**
   * Solves a system of ordinary differential equations using Matrix Exponentials, including DAE
   * index reduction.
   */
  private IExpr solveSystemODE(IAST listOfVariables, IExpr arg2, IExpr xVar,
      IASTAppendable listOfEquations, IAST bcs, EvalEngine engine) {
    int n = listOfVariables.argSize();
    if (listOfEquations.argSize() != n) {
      return F.NIL;
    }

    // Step 1: Detect DAE (Algebraic variable presence without derivatives)
    IASTAppendable dVars = F.ListAlloc();
    IASTAppendable aVars = F.ListAlloc();
    IASTAppendable dVarsDeriv = F.ListAlloc();

    for (int i = 1; i <= n; i++) {
      IExpr v = listOfVariables.get(i);
      IExpr dv = engine.evaluate(F.D(v, xVar));
      boolean hasDv = false;
      for (int j = 1; j <= listOfEquations.argSize(); j++) {
        if (!listOfEquations.get(j).isFree(dv)) {
          hasDv = true;
          break;
        }
      }
      if (hasDv) {
        dVars.append(v);
        dVarsDeriv.append(dv);
      } else {
        aVars.append(v);
      }
    }

    // Process DAEs explicitly
    if (aVars.argSize() > 0) {
      IASTAppendable solveVars = F.ListAlloc();
      solveVars.appendArgs(dVarsDeriv);
      solveVars.appendArgs(aVars);
      IAST equationsEqualZero = listOfEquations.map(x -> F.Equal(x, F.C0));

      IExpr solveRes = engine.evaluate(F.Solve(equationsEqualZero, solveVars));
      if (solveRes.isList() && ((IAST) solveRes).argSize() > 0) {
        IAST ruleList = (IAST) ((IAST) solveRes).arg1();
        IASTAppendable newOdeSys = F.ListAlloc();
        IASTAppendable newAlgRules = F.ListAlloc();

        for (int i = 1; i <= ruleList.argSize(); i++) {
          IAST rule = (IAST) ruleList.get(i);
          IExpr lhs = rule.arg1();
          IExpr rhs = rule.arg2();
          if (dVarsDeriv.contains(lhs)) {
            newOdeSys.append(F.Equal(lhs, rhs));
          } else if (aVars.contains(lhs)) {
            newAlgRules.append(rule);
          }
        }

        IExpr odeSols = solveSystemODE(dVars, dVars, xVar, newOdeSys, F.NIL, engine);
        if (odeSols.isList() && ((IAST) odeSols).argSize() > 0) {
          IAST odeSolList = (IAST) ((IAST) odeSols).arg1();

          java.util.Map<IExpr, IExpr> solMap = new java.util.HashMap<>();
          for (int i = 1; i <= odeSolList.argSize(); i++) {
            IAST rule = (IAST) odeSolList.get(i);
            solMap.put(rule.arg1(), stripConditionalExpression(rule.arg2()));
          }

          for (int i = 1; i <= newAlgRules.argSize(); i++) {
            IAST algRule = (IAST) newAlgRules.get(i);
            IExpr aVar = algRule.arg1();
            IExpr aExpr = algRule.arg2();
            IExpr aExprSol = engine.evaluate(F.subst(aExpr, odeSolList));
            solMap.put(aVar,
                engine.evaluate(S.Simplify.of(engine, stripConditionalExpression(aExprSol))));
          }

          applySystemBCs(solMap, bcs, xVar, engine);

          IASTAppendable fullSol = F.ListAlloc();
          for (int i = 1; i <= n; i++) {
            IExpr v = listOfVariables.get(i);
            IExpr rawResult = solMap.get(v);
            IExpr arg2Var = arg2.isList() ? ((IAST) arg2).get(i) : arg2;
            if (arg2Var.isSymbol() && xVar.isSymbol()) {
              fullSol.append(F.Rule(arg2Var, F.Function(F.List(xVar), rawResult)));
            } else {
              fullSol.append(F.Rule(arg2Var, rawResult));
            }
          }
          return F.List(fullSol);
        }
      }
      return F.NIL;
    }

    // Step 2: Standard ODE Matrix Exponential Path
    IASTAppendable mdAST = F.ListAlloc(n);
    IASTAppendable mvAST = F.ListAlloc(n);
    IASTAppendable bAST = F.ListAlloc(n);

    for (int i = 1; i <= n; i++) {
      IExpr eq = listOfEquations.get(i);
      IExpr lhs = F.NIL;
      if (eq.isEqual()) {
        lhs = S.Subtract.of(engine, eq.first(), eq.second());
      } else {
        lhs = eq;
      }
      lhs = engine.evaluate(S.ExpandAll.of(engine, lhs));

      IASTAppendable mdRow = F.ListAlloc(n);
      IASTAppendable mvRow = F.ListAlloc(n);
      IExpr rest = lhs;

      for (int j = 1; j <= n; j++) {
        IExpr dvar = S.D.of(engine, listOfVariables.get(j), xVar);
        IExpr coeffD = engine.evaluate(S.Coefficient.of(engine, rest, dvar));
        mdRow.append(coeffD);
        rest = engine.evaluate(S.Subtract.of(engine, rest, F.Times(coeffD, dvar)));
      }
      mdAST.append(mdRow);

      for (int j = 1; j <= n; j++) {
        IExpr var = listOfVariables.get(j);
        IExpr coeffV = engine.evaluate(S.Coefficient.of(engine, rest, var));
        mvRow.append(coeffV);
        rest = engine.evaluate(S.Subtract.of(engine, rest, F.Times(coeffV, var)));
      }
      mvAST.append(mvRow);

      bAST.append(engine.evaluate(F.Negate(rest)));
    }

    for (int i = 1; i <= n; i++) {
      if (!mdAST.get(i).isFree(xVar) || !mvAST.get(i).isFree(xVar)) {
        return F.NIL;
      }
    }

    IExpr mdInv = engine.evaluate(F.Inverse(mdAST));
    if (!mdInv.isList()) {
      return F.NIL;
    }

    IExpr matrixA = engine.evaluate(F.Dot(F.Times(F.CN1, mdInv), mvAST));
    IExpr vectorB = engine.evaluate(F.Dot(mdInv, bAST));

    IExpr matX = multiplyMatrixScalar(matrixA, xVar, n, engine);
    IExpr expA = engine.evaluate(F.MatrixExp(matX));

    // FIX: Sqrt(x^2) creates Abs(x) in eigenvalues, blocking analytical Integrate.
    // Ensure all Abs(xVar) are replaced with xVar.
    IExpr absRule = F.Rule(F.Abs(xVar), xVar);
    expA = engine.evaluate(S.ReplaceAll.of(engine, expA, absRule));

    IASTAppendable cVector = F.ListAlloc(n);
    try {
      for (int i = 1; i <= n; i++) {
        cVector.append(F.C(engine.incConstantCounter()));
      }

      IExpr solFinal = engine.evaluate(F.Dot(expA, cVector));
      solFinal = engine.evaluate(S.ReplaceAll.of(engine, solFinal, absRule));
      solFinal = engine.evaluate(S.PowerExpand.of(engine, solFinal));
      solFinal = engine.evaluate(S.ComplexExpand.of(engine, solFinal));
      solFinal = engine.evaluate(S.TrigReduce.of(engine, solFinal));
      solFinal = engine.evaluate(S.Simplify.of(engine, solFinal));

      boolean hasB = false;
      for (int i = 1; i <= n; i++) {
        if (!vectorB.isAST() || !((IAST) vectorB).get(i).isZero()) {
          hasB = true;
          break;
        }
      }

      if (hasB) {
        IExpr matMinusX =
            multiplyMatrixScalar(matrixA, engine.evaluate(F.Times(F.CN1, xVar)), n, engine);
        IExpr expMinusA = engine.evaluate(F.MatrixExp(matMinusX));
        expMinusA = engine.evaluate(S.ReplaceAll.of(engine, expMinusA, absRule));

        IExpr integrand = engine.evaluate(F.Dot(expMinusA, vectorB));

        integrand = engine.evaluate(S.ReplaceAll.of(engine, integrand, absRule));
        integrand = engine.evaluate(S.PowerExpand.of(engine, integrand));
        integrand = engine.evaluate(S.ComplexExpand.of(engine, integrand));
        integrand = engine.evaluate(S.TrigReduce.of(engine, integrand));
        integrand = engine.evaluate(S.Simplify.of(engine, integrand));

        IExpr integral = engine.evaluate(F.Integrate(integrand, xVar));
        IExpr solP = engine.evaluate(F.Dot(expA, integral));

        solFinal = engine.evaluate(F.Plus(solFinal, solP));
        solFinal = engine.evaluate(S.ReplaceAll.of(engine, solFinal, absRule));
        solFinal = engine.evaluate(S.PowerExpand.of(engine, solFinal));
        solFinal = engine.evaluate(S.ComplexExpand.of(engine, solFinal));
        solFinal = engine.evaluate(S.TrigReduce.of(engine, solFinal));
        solFinal = engine.evaluate(S.Simplify.of(engine, solFinal));
      }

      if (solFinal.isList()) {
        java.util.Map<IExpr, IExpr> solMap = new java.util.HashMap<>();
        for (int i = 1; i <= n; i++) {
          solMap.put(listOfVariables.get(i), ((IAST) solFinal).get(i));
        }

        applySystemBCs(solMap, bcs, xVar, engine);

        IASTAppendable rules = F.ListAlloc(n);
        for (int i = 1; i <= n; i++) {
          IExpr v = listOfVariables.get(i);
          IExpr rawResult = stripConditionalExpression(solMap.get(v));

          // DO NOT ABSORB CONSTANTS HERE IN SYSTEMS OF ODES

          IExpr arg2Var = arg2.isList() ? ((IAST) arg2).get(i) : arg2;

          if (arg2Var.isSymbol() && xVar.isSymbol()) {
            rules.append(F.Rule(arg2Var, F.Function(F.List(xVar), rawResult)));
          } else {
            rules.append(F.Rule(arg2Var, rawResult));
          }
        }
        return F.List(rules);
      }
    } finally {
      engine.decConstantCounter();
    }

    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  private IExpr unaryODE(IAST uFunction1Arg, IExpr arg2, IExpr xVar, IASTAppendable listOfEquations,
      IExpr[] boundaryCondition, EvalEngine engine) {
    IAST listOfVariables = F.list(uFunction1Arg);

    if (listOfEquations.argSize() == 1) {
      IExpr c_n = F.C(engine.incConstantCounter());
      try {
        IExpr equation = listOfEquations.arg1();
        IExpr temp = solveSingleODE(equation, xVar, listOfVariables, c_n, engine);

        if (temp.isNIL()) {
          temp = odeSolve(engine, equation, xVar, uFunction1Arg, c_n);
        }

        if (temp.isPresent()) {
          // Wrap in a list if it's a single root to uniformize processing
          IAST roots = temp.isList() ? (IAST) temp : F.list(temp);
          IASTAppendable resultList = F.ListAlloc();

          for (int r = 1; r <= roots.argSize(); r++) {
            IExpr root = roots.get(r);
            root = stripConditionalExpression(root);
            root = engine.evaluate(S.Simplify.of(engine, root));
            root = absorbConstants(root, F.list(c_n), engine);

            if (boundaryCondition != null) {
              IExpr res =
                  engine.evaluate(F.subst(root, F.list(F.Rule(xVar, boundaryCondition[0]))));
              IExpr c1Sol = S.Roots.of(engine, F.Equal(res, boundaryCondition[1]), c_n);
              if (c1Sol.isAST(S.Equal, 3, c_n)) {
                root = engine.evaluate(F.subst(root, F.list(F.Rule(c_n, c1Sol.second()))));
              } else {
                continue; // Skip this root branch if the boundary condition cannot be satisfied
              }
            }

            if (arg2.isSymbol() && xVar.isSymbol()) {
              resultList.append(F.list(F.Rule(arg2, F.Function(F.list(xVar), root))));
            } else {
              resultList.append(F.list(F.Rule(arg2, root)));
            }
          }

          return resultList.argSize() > 0 ? resultList : F.NIL;
        }
      } finally {
        engine.decConstantCounter();
      }
    }
    return F.NIL;
  }
}
