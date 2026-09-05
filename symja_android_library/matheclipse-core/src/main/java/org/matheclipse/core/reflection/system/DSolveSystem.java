package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The system-of-equations solvers of {@link DSolve}, including the differential algebraic
 * equation index reduction.
 */
final class DSolveSystem {

  /** The largest system the matrix exponential is built for. */
  private static final int MAX_SYSTEM_SIZE = 6;

  /** How complicated a single eigenvalue may be before the matrix exponential is given up on. */
  private static final int MAX_EIGENVALUE_LEAF_COUNT = 40;

  private DSolveSystem() {}

  /**
   * Evaluates boundary conditions against the generated homogeneous constants
   */
  static IExpr applySystemBCs(IExpr roots, IAST listOfVariables, IExpr xVar, IAST bcs,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IASTAppendable headRules = F.ListAlloc(listOfVariables.argSize());
    if (roots.isList()) {
      IAST rootsList = (IAST) roots;
      for (int i = 1; i <= rootsList.argSize(); i++) {
        IExpr rule = rootsList.get(i);
        if (rule.isRule() && rule.first().isAST()) {
          IExpr head = rule.first().head();
          IExpr root = rule.second();
          headRules.append(F.Rule(head, F.Function(F.List(xVar), root)));
        }
      }
    }

    IASTAppendable evaluatedBCs = F.ListAlloc();
    for (int k = 1; k <= bcs.argSize(); k++) {
      IExpr evalBC = engine.evaluate(F.subst(bcs.get(k), headRules));
      evalBC = engine.evaluate(DSolveUtil.clearCorruptedIntegrals(evalBC));
      evaluatedBCs.append(evalBC);
    }

    // Use the guaranteed recursive constant extractor
    IASTAppendable cVars = F.ListAlloc();
    DSolveUtil.extractCVars(evaluatedBCs, cVars);

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
      return roots;
    }

    // Shield existing Equal expressions from being double-wrapped
    IAST evaluatedBCsEqualZero = evaluatedBCs.map(t -> {
      if (t.isEqual())
        return t;
      return F.Equal(t, F.C0);
    });

    IExpr cSols = engine.evaluate(F.Solve(evaluatedBCsEqualZero, cVars));
    if (cSols.isList() && ((IAST) cSols).argSize() > 0) {
      IAST cSol = (IAST) ((IAST) cSols).arg1();
      return DSolveUtil.togetherSolution(engine.evaluate(F.subst(roots, cSol)), engine);
    } else if (cSols.isEmptyList()) {
      ctx.addMessage("bvfail", F.CEmptyList);
      return F.NIL;
    }
    return F.NIL;
  }

  /**
   * Solves a system of first-order ordinary differential equations (ODEs) using the Matrix
   * Exponential method. Also handles Differential-Algebraic Equations (DAEs) by performing index
   * reduction: algebraic variables (those whose derivatives do not appear in any equation) are
   * eliminated via {@code Solve}, the reduced ODE subsystem is solved recursively, and the
   * algebraic variables are back-substituted.
   *
   * <p>
   * For a pure ODE system of dimension {@code n}, the method extracts the coefficient matrices
   * {@code M·Y' + N·Y = b}, computes {@code A = -M⁻¹·N} and {@code B = M⁻¹·b}, and returns the
   * general solution {@code Y(x) = exp(A·x)·C + exp(A·x)·∫exp(-A·x)·B dx} where {@code C} is the
   * vector of integration constants {@code C(1), C(2), ...}
   * </p>
   *
   * @param equations the mutable list of ODE/DAE equations (each either an {@code Equal} expression
   *        or an expression implicitly equal to zero); boundary conditions must already have been
   *        removed before calling this method
   * @param dependentFunctions the list of dependent variable applications (e.g.,
   *        {@code {y1(x), y2(x)}}), used internally for coefficient extraction and solution mapping
   * @param independentVariable the independent variable symbol (e.g., {@code x})
   * @param engine the evaluation engine
   * @return a nested list of rules {@code {{f1 -> sol1, f2 -> sol2, ...}}} representing the general
   *         or particular solution, or {@code F.NIL} if the system cannot be solved (e.g.,
   *         non-constant coefficients, singular derivative matrix, or dimension mismatch)
   */
  static IExpr solveSystemODE(IASTAppendable equations, IAST dependentFunctions,
      IExpr indepentVariable, DSolveContext ctx) {
    return solveSystemODE(equations, dependentFunctions, indepentVariable, F.NIL,
        dependentFunctions, ctx);
  }

  /**
   * Solves a system of first-order ordinary differential equations (ODEs) using the Matrix
   * Exponential method. Also handles Differential-Algebraic Equations (DAEs) by performing index
   * reduction: algebraic variables (those whose derivatives do not appear in any equation) are
   * eliminated via {@code Solve}, the reduced ODE subsystem is solved recursively, and the
   * algebraic variables are back-substituted.
   *
   * <p>
   * For a pure ODE system of dimension {@code n}, the method extracts the coefficient matrices
   * {@code M·Y' + N·Y = b}, computes {@code A = -M⁻¹·N} and {@code B = M⁻¹·b}, and returns the
   * general solution {@code Y(x) = exp(A·x)·C + exp(A·x)·∫exp(-A·x)·B dx} where {@code C} is the
   * vector of integration constants {@code C(1), C(2), ...}
   * </p>
   *
   * <p>
   * If boundary/initial conditions are provided, the integration constants are determined by
   * substituting the conditions and solving for the {@code C(k)} symbols.
   * </p>
   *
   * @param equations the mutable list of ODE/DAE equations (each either an {@code Equal} expression
   *        or an expression implicitly equal to zero); boundary conditions must already have been
   *        removed before calling this method
   * @param dependentFunctions the list of dependent variable applications (e.g.,
   *        {@code {y1(x), y2(x)}}), used internally for coefficient extraction and solution mapping
   * @param independentVariable the independent variable symbol (e.g., {@code x})
   * @param boundaryConditions the list of boundary/initial condition equations (each implicitly
   *        {@code == 0}), or {@code F.NIL} / an empty list if none are provided
   * @param outputFunctions the original user-supplied second argument of {@code DSolve}, used as
   *        the left-hand side of the result rules (e.g., {@code {y1, y2}} or
   *        {@code {y1(x), y2(x)}}). When bare symbols are given the result is wrapped in
   *        {@code Function}; may be the same object as {@code dependentFunctions} for internal
   *        recursive calls
   * @param engine the evaluation engine
   * @return a nested list of rules {@code {{f1 -> sol1, f2 -> sol2, ...}}} representing the general
   *         or particular solution, or {@code F.NIL} if the system cannot be solved (e.g.,
   *         non-constant coefficients, singular derivative matrix, or dimension mismatch)
   */
  static IExpr solveSystemODE(IASTAppendable equations, IAST dependentFunctions,
      IExpr indepentVariable, IAST boundaryConditions, IExpr outputFunctions, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    int n = dependentFunctions.argSize();
    if (equations.argSize() != n) {
      return F.NIL;
    }

    IASTAppendable normVars = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      IExpr v = dependentFunctions.get(i);
      if (v.isSymbol()) {
        normVars.append(F.unaryAST1(v, indepentVariable));
      } else {
        normVars.append(v);
      }
    }
    dependentFunctions = normVars;

    // Step 0: Equations which share no unknown are separate problems. Solving them apart lets a
    // system whose blocks are of different kinds be solved at all: neither the matrix path nor a
    // single scalar method fits {y'(x) == x^2*y(x), z'(x) == 5*z(x)} as a whole, because the first
    // equation has a coefficient which depends on x, while each equation on its own is routine.
    IExpr decoupled = solveDecoupled(equations, dependentFunctions, indepentVariable,
        boundaryConditions, outputFunctions, ctx);
    if (decoupled.isPresent()) {
      return decoupled;
    }

    // A single unknown is not a system at all, and the scalar cascade knows many more methods.
    if (n == 1) {
      IExpr scalar = solveScalarBlock(equations, dependentFunctions, indepentVariable,
          boundaryConditions, outputFunctions, ctx);
      if (scalar.isPresent()) {
        return scalar;
      }
    }

    // A system in which an unknown is differentiated more than once is carried to a first order
    // one before the shapes below are looked for, because the test for an algebraic variable knows
    // only about first derivatives and would read a second order equation as one.
    IExpr higherOrder = solveHigherOrderSystem(equations, dependentFunctions, indepentVariable,
        boundaryConditions, outputFunctions, ctx);
    if (higherOrder.isPresent()) {
      return higherOrder;
    }

    // Step 1: Detect DAE (Algebraic variable presence without derivatives)
    IASTAppendable dVars = F.ListAlloc();
    IASTAppendable aVars = F.ListAlloc();
    IASTAppendable dVarsDeriv = F.ListAlloc();

    for (int i = 1; i <= n; i++) {
      IExpr v = dependentFunctions.get(i);
      IExpr dv = engine.evaluate(F.D(v, indepentVariable));
      boolean hasDv = false;
      for (int j = 1; j <= equations.argSize(); j++) {
        if (LinearODEForm.highestDerivativeOrder(equations.get(j), v.head(),
            indepentVariable) >= 1) {
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
      IAST equationsEqualZero = equations.map(x -> F.Equal(x, F.C0));

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
        IExpr odeSols = solveSystemODE(newOdeSys, dVars, indepentVariable, ctx);
        if (odeSols.isList() && ((IAST) odeSols).argSize() > 0) {
          IAST odeSolList = (IAST) ((IAST) odeSols).arg1();

          // Build temporary rules for the boundary evaluator
          IASTAppendable tempRules = F.ListAlloc(n);

          for (int i = 1; i <= odeSolList.argSize(); i++) {
            IAST rule = (IAST) odeSolList.get(i);
            tempRules.append(F.Rule(rule.arg1(), DSolveUtil.stripConditionalExpression(rule.arg2())));
          }

          for (int i = 1; i <= newAlgRules.argSize(); i++) {
            IAST algRule = (IAST) newAlgRules.get(i);
            IExpr aVar = algRule.arg1();
            IExpr aExpr = algRule.arg2();
            IExpr aExprSol = engine.evaluate(F.subst(aExpr, odeSolList));
            tempRules.append(F.Rule(aVar, engine.evaluate(DSolveUtil.stripConditionalExpression(aExprSol))));
          }

          IExpr systemRoots = tempRules;
          if (boundaryConditions.isPresent() && boundaryConditions.isAST()
              && boundaryConditions.argSize() > 0) {
            systemRoots = applySystemBCs(systemRoots, dependentFunctions, indepentVariable,
                boundaryConditions, ctx);
            if (systemRoots.isNIL()) {
              return F.NIL;
            }
          }

          IASTAppendable fullSol = F.ListAlloc();
          for (int i = 1; i <= n; i++) {
            IExpr v = dependentFunctions.get(i);

            // Extract the final solved raw result for this variable
            IExpr rawResult = F.NIL;
            for (int j = 1; j <= ((IAST) systemRoots).argSize(); j++) {
              IExpr rule = ((IAST) systemRoots).get(j);
              if (rule.isRule() && rule.first().equals(v)) {
                rawResult = rule.second();
                break;
              }
            }
            if (rawResult.isNIL())
              return F.NIL;

            IExpr arg2Var =
                outputFunctions.isList() ? ((IAST) outputFunctions).get(i) : outputFunctions;
            if (arg2Var.isSymbol() && indepentVariable.isSymbol()) {
              fullSol.append(F.Rule(arg2Var, F.Function(F.List(indepentVariable), rawResult)));
            } else {
              fullSol.append(F.Rule(arg2Var, rawResult));
            }
          }
          return F.List(fullSol);
        }
      }
      return F.NIL;
    }

    // Standard ODE path: M.Y' + N.Y == b
    IAST residuals = equations.map(eq -> eq.isEqual() //
        ? engine.evaluate(F.Subtract(eq.first(), eq.second()))
        : eq);
    IExpr[] system = LinearODEForm.extractSystem(residuals, dependentFunctions, indepentVariable,
        engine);
    if (system == null) {
      // Not linear in the unknowns, so no matrix describes it.
      return F.NIL;
    }
    IAST mdAST = (IAST) system[0];
    IAST mvAST = (IAST) system[1];
    IAST bAST = (IAST) system[2];

    IExpr mdInv = engine.evaluate(F.Inverse(mdAST));
    if (!mdInv.isList()) {
      return F.NIL;
    }

    IExpr matrixA = engine.evaluate(F.Dot(F.Times(F.CN1, mdInv), mvAST));
    IExpr vectorB = engine.evaluate(F.Dot(mdInv, bAST));

    int savedCounter = engine.getConstantCounter();
    try {
      // A matrix which depends on the variable has no exponential in general, but the shapes in
      // which it commutes with its own integral do have one.
      IExpr bodies = matrixA.isFree(indepentVariable) //
          ? solveLinearFirstOrderSystem(matrixA, vectorB, n, indepentVariable, ctx)
          : DSolveSystemVarCoeff.solve(matrixA, vectorB, n, indepentVariable, ctx);
      if (bodies.isList()) {
        if (!DSolveVerify.acceptSystem(residuals, dependentFunctions, indepentVariable,
            (IAST) bodies, engine)) {
          return F.NIL;
        }
        return formatSystemResult((IAST) bodies, dependentFunctions, indepentVariable,
            boundaryConditions, outputFunctions, ctx);
      }
    } finally {
      engine.setConstantCounter(savedCounter);
    }

    return F.NIL;
  }

  /**
   * The matrix exponential <code>exp(A*x)</code> by Putzer's algorithm.
   *
   * <p>
   * With the eigenvalues <code>lambda[1..n]</code> of <code>A</code> in any order, and
   * <code>P[0] == I</code>, <code>P[k] == P[k-1].(A - lambda[k]*I)</code>, the exponential is
   * <code>Sum(r[k](x)*P[k-1])</code> where <code>r[1] == E^(lambda[1]*x)</code> and
   * <code>r[k]</code> solves <code>r[k]' == lambda[k]*r[k] + r[k-1]</code> with
   * <code>r[k](0) == 0</code>. Every integrand is a sum of terms <code>s^j*E^(mu*s)</code>, so all
   * of them are elementary.
   *
   * <p>
   * This is used instead of {@link org.matheclipse.core.expression.S#MatrixExp} because that runs
   * for minutes on a three by three matrix with integer eigenvalues, and because it produces
   * <code>Sqrt(x^2)</code> in the exponents of a matrix with symbolic entries, which no later
   * simplification recovers from.
   *
   * @return {@link F#NIL} if the eigenvalues are not available in closed form, or if the matrix is
   *         larger than this construction is worth
   */
  static IExpr matrixExponential(IExpr matrixA, int n, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (n < 1 || n > MAX_SYSTEM_SIZE || !matrixA.isList()) {
      return F.NIL;
    }
    IExpr eigenvalues = engine.evaluate(F.Eigenvalues(matrixA));
    if (!eigenvalues.isList() || ((IAST) eigenvalues).argSize() != n) {
      return F.NIL;
    }
    IAST lambda = (IAST) eigenvalues;
    for (int k = 1; k <= n; k++) {
      IExpr value = lambda.get(k);
      if (!value.isFree(S.Root, true) || !value.isFree(S.Eigenvalues, true)
          || !value.isFree(xVar)) {
        return F.NIL;
      }
      // Eigenvalues which are nested radicals of a cubic make every later step, and above all the
      // integrations below, grow out of all proportion. Declining leaves the equation unsolved,
      // which is a better answer than one which never arrives.
      if (value.leafCount() > MAX_EIGENVALUE_LEAF_COUNT) {
        return F.NIL;
      }
    }

    IExpr identity = engine.evaluate(F.IdentityMatrix(F.ZZ(n)));
    IExpr[] p = new IExpr[n];
    p[0] = identity;
    for (int k = 1; k < n; k++) {
      IExpr shifted = engine.evaluate(
          F.Subtract(matrixA, DSolveUtil.multiplyMatrixScalar(identity, lambda.get(k), n, engine)));
      p[k] = engine.evaluate(F.Dot(p[k - 1], shifted));
      if (!p[k].isList()) {
        return F.NIL;
      }
    }

    IExpr s = F.Dummy("s");
    IExpr[] r = new IExpr[n];
    r[0] = engine.evaluate(F.Exp(F.Times(lambda.arg1(), xVar)));
    for (int k = 1; k < n; k++) {
      IExpr previous = engine.evaluate(F.subst(r[k - 1], xVar, s));
      IExpr integrand = engine
          .evaluate(F.Expand(F.Times(F.Exp(F.Times(F.Negate(lambda.get(k + 1)), s)), previous)));
      IExpr integral = ctx.integrate(integrand, F.List(s, F.C0, xVar));
      if (integral.isNIL() || !integral.isFree(s, true)) {
        return F.NIL;
      }
      r[k] = engine.evaluate(F.Expand(F.Times(F.Exp(F.Times(lambda.get(k + 1), xVar)), integral)));
    }

    IExpr result = DSolveUtil.multiplyMatrixScalar(p[0], r[0], n, engine);
    for (int k = 1; k < n; k++) {
      result = engine.evaluate(F.Plus(result, DSolveUtil.multiplyMatrixScalar(p[k], r[k], n,
          engine)));
    }
    if (!result.isList()) {
      return F.NIL;
    }
    return realify(result, engine);
  }

  /**
   * Rewrites the complex exponentials a pair of conjugate eigenvalues leaves behind as sines and
   * cosines, so that a real system keeps a real solution.
   *
   * <p>
   * Only applied when the matrix actually contains a complex number, because the rewrite turns the
   * real <code>E^(2*x)</code> into <code>Cosh(2*x) + Sinh(2*x)</code> and would make a real answer
   * worse. The result is kept only if it came out free of complex numbers.
   */
  private static IExpr realify(IExpr matrix, EvalEngine engine) {
    if (!containsComplexNumber(matrix)) {
      return matrix;
    }
    IExpr trigForm = engine.evaluate(F.Simplify(F.ExpToTrig(matrix)));
    if (trigForm.isPresent() && trigForm.isList() && !containsComplexNumber(trigForm)) {
      return trigForm;
    }
    return matrix;
  }

  /**
   * Whether the expression contains a number which is not real. Testing for the head
   * <code>Complex</code> does not do: an imaginary unit is an atom, and the head of an atom is not
   * part of the expression a pattern is matched against.
   */
  private static boolean containsComplexNumber(IExpr expr) {
    return !expr.isFree(x -> x.isNumber() && !x.isRealResult(), true);
  }

  /**
   * Splits the system into groups of equations which share no unknown, solves each of them on its
   * own and joins the results.
   *
   * @return {@link F#NIL} if the system does not split, or if one of the groups cannot be solved
   */
  private static IExpr solveDecoupled(IASTAppendable equations, IAST dependentFunctions,
      IExpr xVar, IAST boundaryConditions, IExpr outputFunctions, DSolveContext ctx) {
    int n = dependentFunctions.argSize();
    if (n < 2) {
      return F.NIL;
    }
    // Union find over the unknowns: one equation ties together every unknown it mentions.
    int[] group = new int[n];
    for (int i = 0; i < n; i++) {
      group[i] = i;
    }
    for (int e = 1; e <= equations.argSize(); e++) {
      IExpr equation = equations.get(e);
      int first = -1;
      for (int i = 0; i < n; i++) {
        if (!equation.isFree(dependentFunctions.get(i + 1).head(), true)) {
          if (first < 0) {
            first = i;
          } else {
            int a = find(group, first);
            int b = find(group, i);
            group[Math.max(a, b)] = Math.min(a, b);
          }
        }
      }
    }
    java.util.LinkedHashMap<Integer, java.util.List<Integer>> blocks =
        new java.util.LinkedHashMap<>();
    for (int i = 0; i < n; i++) {
      blocks.computeIfAbsent(find(group, i), key -> new java.util.ArrayList<>()).add(i);
    }
    if (blocks.size() < 2) {
      return F.NIL;
    }

    IASTAppendable combined = F.ListAlloc(n);
    int used = 0;
    for (java.util.List<Integer> block : blocks.values()) {
      IASTAppendable blockFunctions = F.ListAlloc(block.size());
      IASTAppendable blockOutputs = F.ListAlloc(block.size());
      for (int index : block) {
        blockFunctions.append(dependentFunctions.get(index + 1));
        blockOutputs.append(outputFunctions.isList() //
            ? ((IAST) outputFunctions).get(index + 1)
            : outputFunctions);
      }
      IASTAppendable blockEquations = F.ListAlloc(block.size());
      for (int e = 1; e <= equations.argSize(); e++) {
        IExpr equation = equations.get(e);
        if (!blockFunctions.isFree(x -> !equation.isFree(x.head(), true), false)) {
          blockEquations.append(equation);
        }
      }
      if (blockEquations.argSize() != block.size()) {
        return F.NIL;
      }
      IASTAppendable blockConditions = F.ListAlloc();
      if (boundaryConditions.isPresent() && boundaryConditions.isAST()) {
        for (int c = 1; c <= boundaryConditions.argSize(); c++) {
          IExpr condition = boundaryConditions.get(c);
          if (!blockFunctions.isFree(x -> !condition.isFree(x.head(), true), false)) {
            blockConditions.append(condition);
          }
        }
      }
      IExpr blockSolution = solveSystemODE(blockEquations, blockFunctions, xVar, blockConditions,
          blockOutputs, ctx);
      if (!blockSolution.isList() || ((IAST) blockSolution).argSize() == 0) {
        return F.NIL;
      }
      IExpr rules = ((IAST) blockSolution).arg1();
      if (!rules.isList()) {
        return F.NIL;
      }
      // Each block numbers its arbitrary constants from the same starting point, so without
      // renumbering two independent blocks would both come back with C(1).
      rules = renumberConstants(rules, used, ctx.engine);
      used += countConstants(rules);
      combined.appendArgs((IAST) rules);
    }
    return F.List(combined);
  }

  private static int find(int[] group, int index) {
    while (group[index] != index) {
      index = group[index];
    }
    return index;
  }

  /** Solves a block of one unknown with the scalar cascade instead of the matrix construction. */
  private static IExpr solveScalarBlock(IASTAppendable equations, IAST dependentFunctions,
      IExpr xVar, IAST boundaryConditions, IExpr outputFunctions, DSolveContext ctx) {
    IExpr function = dependentFunctions.arg1();
    if (!function.isAST1()) {
      return F.NIL;
    }
    IExpr output = outputFunctions.isList() ? ((IAST) outputFunctions).arg1() : outputFunctions;
    IASTAppendable conditions = boundaryConditions.isPresent() && boundaryConditions.isAST() //
        ? F.ListAlloc(boundaryConditions.argSize())
        : F.ListAlloc();
    if (boundaryConditions.isPresent() && boundaryConditions.isAST()) {
      conditions.appendArgs(boundaryConditions);
    }
    return DSolveODE.unaryODE((IAST) function, output, xVar, equations, conditions, ctx);
  }
  /** Shifts the arbitrary constants of one block so that they follow those already handed out. */
  private static IExpr renumberConstants(IExpr rules, int offset, EvalEngine engine) {
    if (offset == 0) {
      return rules;
    }
    IASTAppendable constants = F.ListAlloc();
    DSolveUtil.extractCVars(rules, constants);
    if (constants.argSize() == 0) {
      return rules;
    }
    IASTAppendable substitutions = F.ListAlloc(constants.argSize());
    for (int i = 1; i <= constants.argSize(); i++) {
      substitutions.append(F.Rule(constants.get(i), F.C(offset + i)));
    }
    return engine.evaluate(F.subst(rules, substitutions));
  }

  /** How many distinct arbitrary constants a block solution uses. */
  private static int countConstants(IExpr rules) {
    IASTAppendable constants = F.ListAlloc();
    DSolveUtil.extractCVars(rules, constants);
    return constants.argSize();
  }

  /** How large a system this augments to a first order one. */
  private static final int MAX_AUGMENTED_SIZE = 6;

  /**
   * A coupled system in which some unknown is differentiated more than once, solved by carrying the
   * lower derivatives as unknowns of their own.
   *
   * <p>
   * With the state <code>(u1, u1', ..., u2, u2', ...)</code> every entry advances into the next one,
   * and the last of each unknown advances into its highest derivative, which comes from solving the
   * equations for those. That leaves a first order system with a constant coefficient matrix, which
   * is the one shape the engine below already solves.
   */
  static IExpr solveHigherOrderSystem(IASTAppendable equations, IAST dependentFunctions,
      IExpr xVar, IAST boundaryConditions, IExpr outputFunctions, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    int n = dependentFunctions.argSize();
    if (n < 2 || equations.argSize() != n) {
      return F.NIL;
    }

    IAST residuals = equations.map(eq -> eq.isEqual() //
        ? engine.evaluate(F.Subtract(eq.first(), eq.second()))
        : eq);

    int[] orders = new int[n];
    int[] offsets = new int[n];
    int size = 0;
    boolean higher = false;
    for (int j = 0; j < n; j++) {
      IExpr head = dependentFunctions.get(j + 1).head();
      int order = 0;
      for (int e = 1; e <= residuals.argSize(); e++) {
        order = Math.max(order, LinearODEForm.highestDerivativeOrder(residuals.get(e), head, xVar));
      }
      if (order < 1) {
        return F.NIL;
      }
      if (order >= 2) {
        higher = true;
      }
      orders[j] = order;
      offsets[j] = size;
      size += order;
    }
    if (!higher || size > MAX_AUGMENTED_SIZE) {
      // Without a higher derivative this is the first order system the engine below solves.
      return F.NIL;
    }

    // One symbol per state, and one for the highest derivative of each unknown.
    IExpr[][] states = new IExpr[n][];
    IExpr[] tops = new IExpr[n];
    for (int j = 0; j < n; j++) {
      states[j] = new IExpr[orders[j]];
      for (int k = 0; k < orders[j]; k++) {
        states[j][k] = F.Dummy("s" + j + "x" + k);
      }
      tops[j] = F.Dummy("d" + j);
    }

    IASTAppendable algebraic = F.ListAlloc(n);
    for (int e = 1; e <= residuals.argSize(); e++) {
      IExpr residual = residuals.get(e);
      for (int j = 0; j < n; j++) {
        IExpr unknown = dependentFunctions.get(j + 1);
        for (int k = orders[j]; k >= 0; k--) {
          IExpr derivative = k == 0 //
              ? unknown
              : engine.evaluate(F.D(unknown, F.List(xVar, F.ZZ(k))));
          residual = F.subst(residual, derivative, k == orders[j] ? tops[j] : states[j][k]);
        }
      }
      residual = engine.evaluate(F.ExpandAll(residual));
      for (int j = 0; j < n; j++) {
        if (!residual.isFree(dependentFunctions.get(j + 1).head(), true)) {
          // A derivative higher than the order counted, or the unknown in a form not accounted for.
          return F.NIL;
        }
      }
      algebraic.append(residual);
    }

    // The equations are solved for the highest derivatives, which asks them to be linear in those.
    IASTAppendable leading = F.ListAlloc(n);
    IASTAppendable rightHand = F.ListAlloc(n);
    for (int e = 0; e < n; e++) {
      IExpr residual = algebraic.get(e + 1);
      IASTAppendable row = F.ListAlloc(n);
      for (int j = 0; j < n; j++) {
        IExpr coefficient = engine.evaluate(F.D(residual, tops[j]));
        for (int i = 0; i < n; i++) {
          if (!coefficient.isFree(tops[i], true)) {
            return F.NIL;
          }
        }
        row.append(coefficient);
      }
      leading.append(row);
      IExpr constant = residual;
      for (int j = 0; j < n; j++) {
        constant = F.subst(constant, tops[j], F.C0);
      }
      rightHand.append(engine.evaluate(F.Negate(constant)));
    }
    IExpr solved = engine.evaluate(F.LinearSolve(leading, rightHand));
    if (!solved.isList() || ((IAST) solved).argSize() != n
        || !solved.isFree(S.LinearSolve, true)) {
      return F.NIL;
    }

    // The matrix of the augmented system, which has to be a constant one.
    IASTAppendable matrix = F.ListAlloc(size);
    IASTAppendable forcing = F.ListAlloc(size);
    for (int j = 0; j < n; j++) {
      for (int k = 0; k < orders[j]; k++) {
        IExpr advance = k < orders[j] - 1 //
            ? states[j][k + 1]
            : ((IAST) solved).get(j + 1);
        IASTAppendable row = F.ListAlloc(size);
        for (int i = 0; i < n; i++) {
          for (int l = 0; l < orders[i]; l++) {
            IExpr entry = engine.evaluate(F.D(advance, states[i][l]));
            if (!entry.isFree(xVar)) {
              // Variable coefficients are not this method's to solve.
              return F.NIL;
            }
            for (int i2 = 0; i2 < n; i2++) {
              for (int l2 = 0; l2 < orders[i2]; l2++) {
                if (!entry.isFree(states[i2][l2], true)) {
                  return F.NIL;
                }
              }
            }
            row.append(entry);
          }
        }
        matrix.append(row);
        IExpr constant = advance;
        for (int i = 0; i < n; i++) {
          for (int l = 0; l < orders[i]; l++) {
            constant = F.subst(constant, states[i][l], F.C0);
          }
        }
        forcing.append(engine.evaluate(constant));
      }
    }

    int savedCounter = engine.getConstantCounter();
    try {
      IExpr bodies = solveLinearFirstOrderSystem(matrix, forcing, size, xVar, ctx);
      if (!bodies.isList()) {
        return F.NIL;
      }
      IASTAppendable unknowns = F.ListAlloc(n);
      for (int j = 0; j < n; j++) {
        unknowns.append(((IAST) bodies).get(offsets[j] + 1));
      }
      if (!DSolveVerify.acceptSystem(residuals, dependentFunctions, xVar, unknowns, engine)) {
        return F.NIL;
      }
      return formatSystemResult(unknowns, dependentFunctions, xVar, boundaryConditions,
          outputFunctions, ctx);
    } finally {
      engine.setConstantCounter(savedCounter);
    }
  }

  /**
   * The general solution of a first order system <code>Y' == A.Y + b</code> with a constant
   * coefficient matrix, as one body per unknown.
   *
   * <p>
   * The homogeneous part is <code>Exp(A*x)</code> applied to a vector of arbitrary constants, and
   * the forced part is the variation of parameters integral
   * <code>Exp(A*x).Integrate(Exp(-A*x).b, x)</code>.
   */
  static IExpr solveLinearFirstOrderSystem(IExpr matrixA, IExpr vectorB, int n, IExpr xVar,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr expA = matrixExponential(matrixA, n, xVar, ctx);
    if (expA.isNIL()) {
      return F.NIL;
    }
    IASTAppendable cVector = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      cVector.append(ctx.nextConstant());
    }
    IExpr solFinal = engine.evaluate(F.Expand(F.Dot(expA, cVector)));

    boolean hasB = false;
    for (int i = 1; i <= n; i++) {
      if (!vectorB.isAST() || !((IAST) vectorB).get(i).isZero()) {
        hasB = true;
        break;
      }
    }
    if (hasB) {
      IExpr expMinusA = engine.evaluate(F.subst(expA, xVar, F.Negate(xVar)));
      IExpr integrand = engine.evaluate(F.Expand(F.Dot(expMinusA, vectorB)));
      IExpr integral = ctx.integrate(integrand, xVar);
      if (integral.isNIL()) {
        return F.NIL;
      }
      IExpr solP = engine.evaluate(F.Expand(F.Dot(expA, integral)));
      solFinal = engine.evaluate(F.Expand(F.Plus(solFinal, solP)));
      IExpr reduced = engine.evaluate(F.Expand(F.TrigReduce(solFinal)));
      if (reduced.isPresent() && reduced.leafCount() <= solFinal.leafCount()) {
        solFinal = reduced;
      }
    }
    return solFinal.isList() ? solFinal : F.NIL;
  }

  /**
   * Turns one body per unknown into the rules {@link DSolve} answers with, after fitting whatever
   * boundary conditions were given.
   */
  static IExpr formatSystemResult(IAST bodies, IAST dependentFunctions, IExpr xVar,
      IAST boundaryConditions, IExpr outputFunctions, DSolveContext ctx) {
    int n = dependentFunctions.argSize();
    if (bodies.argSize() != n) {
      return F.NIL;
    }
    IASTAppendable tempRules = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      tempRules.append(F.Rule(dependentFunctions.get(i),
          DSolveUtil.stripConditionalExpression(bodies.get(i))));
    }

    IExpr systemRoots = tempRules;
    if (boundaryConditions.isPresent() && boundaryConditions.isAST()
        && boundaryConditions.argSize() > 0) {
      systemRoots = applySystemBCs(systemRoots, dependentFunctions, xVar, boundaryConditions, ctx);
      if (systemRoots.isNIL()) {
        return F.NIL;
      }
    }

    IASTAppendable rules = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      IExpr v = dependentFunctions.get(i);
      IExpr rawResult = F.NIL;
      for (int j = 1; j <= ((IAST) systemRoots).argSize(); j++) {
        IExpr rule = ((IAST) systemRoots).get(j);
        if (rule.isRule() && rule.first().equals(v)) {
          rawResult = rule.second();
          break;
        }
      }
      if (rawResult.isNIL()) {
        return F.NIL;
      }
      IExpr arg2Var = outputFunctions.isList() ? ((IAST) outputFunctions).get(i) : outputFunctions;
      if (arg2Var.isSymbol() && xVar.isSymbol()) {
        rules.append(F.Rule(arg2Var, F.Function(F.List(xVar), rawResult)));
      } else {
        rules.append(F.Rule(arg2Var, rawResult));
      }
    }
    return F.List(rules);
  }
}
