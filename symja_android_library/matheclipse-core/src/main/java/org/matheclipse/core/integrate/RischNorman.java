package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Heuristic Risch-Norman ("parallel Risch") integration.
 *
 * <p>
 * The integrand is rewritten as a rational function of the integration variable <code>x</code> and
 * a set of transcendental kernels <code>&theta;<sub>j</sub></code> (<code>Exp</code>,
 * <code>Log</code>, <code>Tan</code>, <code>Tanh</code>, <code>Sin/Cos</code>,
 * <code>Sinh/Cosh</code>) whose derivatives are again rational in <code>x</code> and the kernels.
 * An ansatz
 *
 * <pre>
 * F = (sum of c_i * monomial_i) / D + sum of b_j * Log(L_j)
 * </pre>
 *
 * with undetermined coefficients is differentiated and the resulting linear system for
 * <code>c_i, b_j</code> is solved. If the system is inconsistent the method fails fast and returns
 * {@link F#NIL} so that the caller can fall back to the Rubi rules.
 *
 * <p>
 * See: K. Geddes, L. Stefanus: "On the Risch-Norman integration method and its implementation in
 * MAPLE".
 */
public class RischNorman {

  /** Maximum number of transcendental kernels. */
  private static final int MAX_KERNELS = 4;

  /** Maximum number of undetermined coefficients in the ansatz. */
  private static final int MAX_UNKNOWNS = 150;

  /** Recursion guard. */
  private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> Boolean.FALSE);

  private RischNorman() {}

  /**
   * Try Risch-Norman integration.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_RISCH_NORMAN || ACTIVE.get().booleanValue()) {
      return F.NIL;
    }
    ACTIVE.set(Boolean.TRUE);
    final long deadline =
        System.currentTimeMillis() + Config.INTEGRATE_RISCH_NORMAN_TIMELIMIT_MILLIS;
    try {
      return integrateInternal(integrand, x, engine, deadline);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      ACTIVE.set(Boolean.FALSE);
    }
  }

  private static IExpr integrateInternal(IExpr integrand, IExpr x, EvalEngine engine,
      long deadline) {
    // 1. collect the transcendental kernels
    List<IExpr> kernels = collectKernels(integrand, x);
    if (kernels.isEmpty() || kernels.size() > MAX_KERNELS) {
      return F.NIL;
    }
    // kernels sorted by decreasing leaf count, so nested kernels are replaced first
    kernels.sort(Comparator.comparingLong(IExpr::leafCount).reversed());

    // 2. replace kernels by dummy symbols
    ISymbol[] dummies = new ISymbol[kernels.size()];
    for (int i = 0; i < dummies.length; i++) {
      dummies[i] = F.Dummy("rn$" + i);
    }
    IExpr substituted = replaceKernels(integrand, kernels, dummies);
    if (!isRationalIn(substituted, x, dummies)) {
      return F.NIL;
    }
    // 3. derivatives of the kernels, expressed in x and the dummies
    IExpr[] derivatives = new IExpr[kernels.size()];
    for (int i = 0; i < kernels.size(); i++) {
      IExpr d = engine.evaluate(F.D(kernels.get(i), x));
      d = replaceKernels(d, kernels, dummies);
      if (!isRationalIn(d, x, dummies)) {
        return F.NIL;
      }
      derivatives[i] = d;
    }

    // 4. build the ansatz
    IExpr together = engine.evaluate(F.Together(substituted));
    IExpr numer = engine.evaluate(F.Expand(F.Numerator(together)));
    IExpr denom = engine.evaluate(F.Expand(F.Denominator(together)));

    IASTAppendable varsList = F.ListAlloc(dummies.length + 1);
    varsList.append(x);
    for (ISymbol dummy : dummies) {
      varsList.append(dummy);
    }

    // degree bounds per variable
    int[] bounds = new int[dummies.length + 1];
    for (int v = 0; v < bounds.length; v++) {
      IExpr var = varsList.get(v + 1);
      int dn = degree(numer, var, engine);
      int dd = degree(denom, var, engine);
      if (dn < 0 || dd < 0) {
        return F.NIL;
      }
      bounds[v] = Math.max(dn, dd) + 2;
    }
    long count = 1;
    for (int bound : bounds) {
      count *= (bound + 1);
      if (count > MAX_UNKNOWNS) {
        return F.NIL;
      }
    }

    List<IExpr> unknowns = new ArrayList<>();
    IASTAppendable ansatzNumer = F.PlusAlloc((int) count);
    buildMonomials(varsList, bounds, new int[bounds.length], 0, ansatzNumer, unknowns);

    // logarithmic candidates: irreducible factors of the denominator and the special
    // polynomials of Tan/Tanh kernels
    Set<IExpr> logCandidates = new LinkedHashSet<>();
    if (!denom.isOne()) {
      IExpr factored = engine.evaluate(F.unaryAST1(S.FactorList, denom));
      if (factored.isList()) {
        for (IExpr pair : (IAST) factored) {
          if (pair.isList2()) {
            IExpr factor = pair.first();
            if (!factor.isNumber()) {
              logCandidates.add(factor);
            }
          }
        }
      }
    }
    for (int i = 0; i < kernels.size(); i++) {
      IExpr kernel = kernels.get(i);
      if (kernel.isAST(S.Tan, 2)) {
        logCandidates.add(F.Plus(F.C1, F.Sqr(dummies[i])));
      } else if (kernel.isAST(S.Tanh, 2)) {
        logCandidates.add(F.Subtract(F.C1, F.Sqr(dummies[i])));
      } else if (kernel.isAST(S.Log, 2)) {
        logCandidates.add(dummies[i]);
      }
    }

    IASTAppendable ansatz = F.PlusAlloc(logCandidates.size() + 1);
    ansatz.append(F.Divide(ansatzNumer, denom));
    for (IExpr candidate : logCandidates) {
      ISymbol bj = F.Dummy("rn$b" + unknowns.size());
      unknowns.add(bj);
      ansatz.append(F.Times(bj, F.Log(candidate)));
      if (unknowns.size() > MAX_UNKNOWNS) {
        return F.NIL;
      }
    }

    if (System.currentTimeMillis() > deadline) {
      return F.NIL;
    }

    // 5. total derivative of the ansatz: dF/dx + sum_j dF/dtheta_j * theta_j'
    IASTAppendable totalDerivative = F.PlusAlloc(dummies.length + 1);
    totalDerivative.append(F.D(ansatz, x));
    for (int i = 0; i < dummies.length; i++) {
      totalDerivative.append(F.Times(F.D(ansatz, dummies[i]), derivatives[i]));
    }
    IExpr equation = engine.evaluate(F.Together(F.Subtract(totalDerivative, substituted)));
    IExpr eqNumer = engine.evaluate(F.Expand(F.Numerator(equation)));

    if (System.currentTimeMillis() > deadline) {
      return F.NIL;
    }

    // 6. collect the coefficients of the monomials in (x, dummies); each must vanish
    IExpr coefficientRules = engine.evaluate(F.CoefficientRules(eqNumer, varsList));
    if (!coefficientRules.isList()) {
      return F.NIL;
    }
    IASTAppendable equations = F.ListAlloc(((IAST) coefficientRules).argSize());
    for (IExpr rule : (IAST) coefficientRules) {
      if (rule.isRuleAST()) {
        equations.append(F.Equal(rule.second(), F.C0));
      }
    }
    if (equations.isEmpty()) {
      return F.NIL;
    }
    IASTAppendable unknownsList = F.ListAlloc(unknowns.size());
    for (IExpr unknown : unknowns) {
      unknownsList.append(unknown);
    }

    // 7. solve the linear system
    IExpr solution = engine.evaluateNIL(F.Solve(equations, unknownsList));
    if (solution.isNIL() || !solution.isListOfLists() || solution.argSize() == 0) {
      return F.NIL;
    }
    IExpr rules = solution.first();
    IExpr resolved = engine.evaluate(F.ReplaceAll(ansatz, rules));
    // remaining free coefficients are set to zero
    for (IExpr unknown : unknowns) {
      resolved = F.subst(resolved, unknown, F.C0);
    }
    resolved = engine.evaluate(resolved);
    if (resolved.isZero() || !resolved.isFree(S.Solve)) {
      return F.NIL;
    }
    // 8. back-substitute the kernels
    for (int i = dummies.length - 1; i >= 0; i--) {
      resolved = F.subst(resolved, dummies[i], kernels.get(i));
    }
    return engine.evaluate(resolved);
  }

  /** Determine the polynomial degree of <code>expr</code> in <code>var</code>. */
  private static int degree(IExpr expr, IExpr var, EvalEngine engine) {
    IExpr exponent = engine.evaluate(F.Exponent(expr, var));
    int degree = exponent.toIntDefault();
    if (degree == Integer.MIN_VALUE || degree < 0) {
      return expr.isFree(var, true) ? 0 : -1;
    }
    return degree;
  }

  /** Recursively generate all candidate monomials with undetermined coefficients. */
  private static void buildMonomials(IAST vars, int[] bounds, int[] exponents, int index,
      IASTAppendable ansatzNumer, List<IExpr> unknowns) {
    if (index == bounds.length) {
      ISymbol c = F.Dummy("rn$c" + unknowns.size());
      unknowns.add(c);
      IASTAppendable monomial = F.TimesAlloc(bounds.length + 1);
      monomial.append(c);
      for (int v = 0; v < bounds.length; v++) {
        if (exponents[v] > 0) {
          monomial.append(F.Power(vars.get(v + 1), F.ZZ(exponents[v])));
        }
      }
      ansatzNumer.append(monomial.oneIdentity1());
      return;
    }
    for (int e = 0; e <= bounds[index]; e++) {
      exponents[index] = e;
      buildMonomials(vars, bounds, exponents, index + 1, ansatzNumer, unknowns);
    }
    exponents[index] = 0;
  }

  /** Replace each kernel (largest first) by its dummy symbol. */
  private static IExpr replaceKernels(IExpr expr, List<IExpr> kernels, ISymbol[] dummies) {
    IExpr result = expr;
    for (int i = 0; i < kernels.size(); i++) {
      result = F.subst(result, kernels.get(i), dummies[i]);
    }
    return result;
  }

  /** Check that <code>expr</code> is rational in <code>x</code> and the dummy variables. */
  private static boolean isRationalIn(IExpr expr, IExpr x, ISymbol[] dummies) {
    return isRationalRecursive(expr, x, dummies);
  }

  private static boolean isRationalRecursive(IExpr expr, IExpr x, ISymbol[] dummies) {
    if (expr.isNumber() || expr.equals(x)) {
      return true;
    }
    if (expr.isSymbol()) {
      return true; // free symbolic constants and dummies are allowed
    }
    if (expr.isPlus() || expr.isTimes()) {
      IAST ast = (IAST) expr;
      for (int i = 1; i < ast.size(); i++) {
        if (!isRationalRecursive(ast.get(i), x, dummies)) {
          return false;
        }
      }
      return true;
    }
    if (expr.isPower()) {
      return expr.exponent().isInteger() && isRationalRecursive(expr.base(), x, dummies);
    }
    return false;
  }

  /**
   * Collect the transcendental kernels of the integrand. For <code>Sin/Cos</code> and
   * <code>Sinh/Cosh</code> the partner function is added as well, because it occurs in the
   * derivative.
   */
  private static List<IExpr> collectKernels(IExpr integrand, IExpr x) {
    Set<IExpr> kernels = new LinkedHashSet<>();
    collectKernelsRecursive(integrand, x, kernels);
    List<IExpr> result = new ArrayList<>(kernels.size() + 2);
    result.addAll(kernels);
    for (IExpr kernel : kernels) {
      IExpr arg = kernel.first();
      if (kernel.isAST(S.Sin, 2)) {
        addIfAbsent(result, F.Cos(arg));
      } else if (kernel.isAST(S.Cos, 2)) {
        addIfAbsent(result, F.Sin(arg));
      } else if (kernel.isAST(S.Sinh, 2)) {
        addIfAbsent(result, F.Cosh(arg));
      } else if (kernel.isAST(S.Cosh, 2)) {
        addIfAbsent(result, F.Sinh(arg));
      }
    }
    return result;
  }

  private static void addIfAbsent(List<IExpr> list, IExpr expr) {
    if (!list.contains(expr)) {
      list.add(expr);
    }
  }

  private static void collectKernelsRecursive(IExpr expr, IExpr x, Set<IExpr> kernels) {
    if (expr.isFree(x, true)) {
      return;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (isKernel(ast, x)) {
        kernels.add(ast);
        // collect nested kernels inside the argument too
        collectKernelsRecursive(ast.arg1(), x, kernels);
        return;
      }
      if (ast.isPower()) {
        // E^u is normalized as Power(E, u)
        if (ast.base().isE() && !ast.exponent().isFree(x, true)) {
          kernels.add(ast);
          collectKernelsRecursive(ast.exponent(), x, kernels);
          return;
        }
        collectKernelsRecursive(ast.base(), x, kernels);
        if (!ast.exponent().isInteger()) {
          collectKernelsRecursive(ast.exponent(), x, kernels);
        }
        return;
      }
      for (int i = 1; i < ast.size(); i++) {
        collectKernelsRecursive(ast.get(i), x, kernels);
      }
    }
  }

  private static boolean isKernel(IAST ast, IExpr x) {
    if (ast.argSize() != 1 || ast.arg1().isFree(x, true)) {
      return false;
    }
    return ast.head() == S.Log || ast.head() == S.Tan || ast.head() == S.Tanh || ast.head() == S.Sin
        || ast.head() == S.Cos || ast.head() == S.Sinh || ast.head() == S.Cosh;
  }
}
