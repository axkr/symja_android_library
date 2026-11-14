package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
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

  /** Set while a stage re-enters Integrate on rewritten pieces of an integrand. */
  private static final ThreadLocal<Boolean> RADICAL_TOWER_OFF =
      ThreadLocal.withInitial(() -> Boolean.FALSE);

  /**
   * Run a stage which re-enters Integrate on rewritten pieces of the integrand without the radical
   * tower stage: that stage has already been tried on the whole integrand, and the pieces are no
   * easier for it, only more numerous.
   */
  public static IExpr withoutRadicalTower(java.util.function.Supplier<IExpr> stage) {
    boolean old = RADICAL_TOWER_OFF.get().booleanValue();
    RADICAL_TOWER_OFF.set(Boolean.TRUE);
    try {
      return stage.get();
    } finally {
      RADICAL_TOWER_OFF.set(old);
    }
  }

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
    final long deadline = System.currentTimeMillis()
        + MachineProfile.millis(Config.INTEGRATE_RISCH_NORMAN_TIMELIMIT_MILLIS);
    try {
      if (RischNormanRadical.hasRoot(integrand, x)) {
        // one square root in the tower (and roots which flatten into a generator)
        return RischNormanRadical.integrate(integrand, x, engine, System.currentTimeMillis()
            + MachineProfile.millis(Config.INTEGRATE_RISCH_NORMAN_RADICAL_TIMELIMIT_MILLIS));
      }
      return integrateInternal(integrand, x, engine, deadline);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      ACTIVE.set(Boolean.FALSE);
    }
  }

  /**
   * Is <code>f</code> a function of a root and at least one <code>Log</code>, <code>Exp</code> or
   * <code>Tan</code>? That is the mixed class the rules leave unanswered, for which
   * {@link #integrate(IExpr, IExpr, EvalEngine)} is worth a try after them.
   */
  public static boolean isRadicalTower(IExpr f, IExpr x) {
    if (RADICAL_TOWER_OFF.get().booleanValue() || !RischNormanRadical.hasRoot(f, x)) {
      return false;
    }
    return hasTowerKernel(f, x);
  }

  /**
   * Is <code>f</code> a function of <code>Log</code>, <code>Exp</code> or <code>Tan</code> kernels
   * without a root? A sum of such terms, like the derivative of
   * <code>x*E^x/(1+E^x)</code>, is split by the rules into terms which are not elementary on their
   * own; {@link #integrate(IExpr, IExpr, EvalEngine)} takes the integrand as a whole.
   */
  public static boolean isTranscendentalTower(IExpr f, IExpr x) {
    if (RADICAL_TOWER_OFF.get().booleanValue() || RischNormanRadical.hasRoot(f, x)) {
      return false;
    }
    return hasTowerKernel(f, x);
  }

  private static boolean hasTowerKernel(IExpr f, IExpr x) {
    return !f.isFree(e -> (e.isAST(S.Log, 2) || e.isAST(S.Tan, 2)
        || (e.isPower() && e.base().isE())) && !e.isFree(x, true), true);
  }

  private static IExpr integrateInternal(IExpr integrand, IExpr x, EvalEngine engine,
      long deadline) {
    // 1. collect the transcendental kernels
    List<IExpr> kernels = collectKernels(integrand, x);
    // exponentials with commensurable exponents are powers of one generator
    Map<IExpr, IExpr[]> members = commonExponentials(kernels, engine);
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
    // the kernels first: a member inside a kernel, E^(2*x) in Log(1+E^(2*x)), stays with it
    IExpr substituted = replaceMembers(replaceKernels(integrand, kernels, dummies), members,
        kernels, dummies);
    if (!isRationalIn(substituted, x, dummies)) {
      return F.NIL;
    }
    // 3. derivatives of the kernels, expressed in x and the dummies
    IExpr[] derivatives = new IExpr[kernels.size()];
    for (int i = 0; i < kernels.size(); i++) {
      IExpr d = engine.evaluate(F.D(kernels.get(i), x));
      d = replaceMembers(replaceKernels(d, kernels, dummies), members, kernels, dummies);
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
      IExpr factored = engine.evaluate(F.FactorList(denom));
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
    IExpr eqNumer = clearedNumerator(engine.evaluate(F.Subtract(totalDerivative, substituted)),
        engine, deadline);
    if (eqNumer.isNIL()) {
      return F.NIL;
    }

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
    IExpr solution = engine.evaluateNIL(F.SolveDistinct(equations, unknownsList));
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
    // the rational part in its lowest terms: (t*x+t^2*x)/(1+t)^2 is x-x/(1+t)
    resolved = cancelRationalPart(resolved, dummies, engine);
    // 8. back-substitute the kernels
    for (int i = dummies.length - 1; i >= 0; i--) {
      resolved = F.subst(resolved, dummies[i], kernels.get(i));
    }
    return engine.evaluate(resolved);
  }

  /**
   * The terms of <code>expr</code> without a logarithm over one denominator with the common
   * factors cancelled - the ansatz is written over the denominator of the integrand, which has
   * factors the antiderivative doesn't need - and then as partial fractions in the first kernel
   * of the denominator, as the rules write it: <code>x*t/(2*(2+t))</code> is
   * <code>x/2-x/(2+t)</code>.
   */
  private static IExpr cancelRationalPart(IExpr expr, ISymbol[] dummies, EvalEngine engine) {
    IASTAppendable rational = F.PlusAlloc(expr.isPlus() ? expr.argSize() : 1);
    IASTAppendable rest = F.PlusAlloc(expr.isPlus() ? expr.argSize() + 1 : 2);
    for (IExpr term : expr.isPlus() ? (IAST) expr : F.List(expr)) {
      if (term.isFree(S.Log, true)) {
        rational.append(term);
      } else {
        rest.append(term);
      }
    }
    if (rational.argSize() == 0) {
      return expr;
    }
    IExpr cancelled = engine.evaluate(F.Cancel(F.Together(rational.oneIdentity0())));
    IExpr numerator = engine.evaluate(F.Numerator(cancelled));
    IExpr denominator = engine.evaluate(F.Denominator(cancelled));
    for (ISymbol dummy : dummies) {
      if (!denominator.isFree(dummy)) {
        // the polynomial part in the kernel and a proper fraction
        IExpr quotientRemainder =
            engine.evaluate(F.PolynomialQuotientRemainder(numerator, denominator, dummy));
        if (quotientRemainder.isList2() && !quotientRemainder.first().isZero()) {
          cancelled = engine.evaluate(F.Plus(quotientRemainder.first(),
              F.Divide(quotientRemainder.second(), denominator)));
        }
        break;
      }
    }
    rest.append(cancelled);
    return engine.evaluate(rest.oneIdentity0());
  }

  /**
   * The numerator of a sum of rational functions over a common multiple of the denominators of
   * its terms, multiplied out. Each term is put over its own denominator, which is cheap for the
   * small terms of the ansatz derivative; <code>Together</code> of the whole sum takes the gcd of
   * polynomials in all the unknowns of the ansatz and needed seconds for 17 of them. The
   * numerator over a multiple of the lowest denominator has the same zeros in the unknowns.
   *
   * @return {@link F#NIL} if the deadline passed
   */
  private static IExpr clearedNumerator(IExpr sum, EvalEngine engine, long deadline) {
    IAST terms = sum.isPlus() ? (IAST) sum : F.List(sum);
    List<IExpr> numerators = new ArrayList<>(terms.argSize());
    List<IExpr> denominators = new ArrayList<>(terms.argSize());
    // distinct denominators, the index of each term's one
    List<IExpr> distinct = new ArrayList<>();
    int[] index = new int[terms.argSize()];
    for (int i = 1; i < terms.size(); i++) {
      if (System.currentTimeMillis() > deadline) {
        return F.NIL;
      }
      IExpr together = engine.evaluate(F.Together(terms.get(i)));
      numerators.add(engine.evaluate(F.Numerator(together)));
      IExpr denominator = engine.evaluate(F.Expand(F.Denominator(together)));
      denominators.add(denominator);
      int k = distinct.indexOf(denominator);
      if (k < 0) {
        k = distinct.size();
        distinct.add(denominator);
      }
      index[i - 1] = k;
    }
    IASTAppendable result = F.PlusAlloc(numerators.size());
    for (int i = 0; i < numerators.size(); i++) {
      IASTAppendable product = F.TimesAlloc(distinct.size());
      product.append(numerators.get(i));
      for (int k = 0; k < distinct.size(); k++) {
        if (k != index[i]) {
          product.append(distinct.get(k));
        }
      }
      result.append(product.oneIdentity1());
    }
    return engine.evaluate(F.Expand(result.oneIdentity0()));
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

  /**
   * Exponential kernels whose exponents have rational ratios, <code>E^x</code> and
   * <code>E^(2*x)</code>, are algebraically dependent: an ansatz with a symbol for each of them
   * cannot represent <code>E^(2*x) == (E^x)^2</code>. They are replaced in <code>kernels</code> by
   * the one exponential <code>E^g</code> of which they are integer powers.
   *
   * @return the replaced kernels mapped to their generator and the integer power of it
   */
  private static Map<IExpr, IExpr[]> commonExponentials(List<IExpr> kernels,
      EvalEngine engine) {
    Map<IExpr, IExpr[]> members = new HashMap<>();
    List<IExpr> exponentials = new ArrayList<>();
    for (IExpr kernel : kernels) {
      if (kernel.isPower() && kernel.base().isE()) {
        exponentials.add(kernel);
      }
    }
    boolean[] done = new boolean[exponentials.size()];
    for (int i = 0; i < exponentials.size(); i++) {
      if (done[i]) {
        continue;
      }
      IExpr first = exponentials.get(i).exponent();
      List<Integer> group = new ArrayList<>();
      List<IRational> ratios = new ArrayList<>();
      for (int j = i; j < exponentials.size(); j++) {
        IExpr ratio =
            engine.evaluate(F.Together(F.Divide(exponentials.get(j).exponent(), first)));
        if (!done[j] && ratio.isRational()) {
          group.add(j);
          ratios.add((IRational) ratio);
        }
      }
      if (group.size() < 2) {
        continue;
      }
      // the generator exponent: first times the gcd of the ratios
      IInteger numeratorGcd = F.C0;
      IInteger denominatorLcm = F.C1;
      for (IRational r : ratios) {
        numeratorGcd = numeratorGcd.gcd(r.numerator());
        denominatorLcm = denominatorLcm.lcm(r.denominator());
      }
      IRational unit = F.QQ(numeratorGcd, denominatorLcm);
      IExpr generator = engine.evaluate(F.Exp(F.Times(first, unit)));
      for (int k = 0; k < group.size(); k++) {
        int j = group.get(k);
        done[j] = true;
        IExpr member = exponentials.get(j);
        if (!member.equals(generator)) {
          kernels.remove(member);
          members.put(member, new IExpr[] {generator, ratios.get(k).divideBy(unit).normalize()});
        }
      }
      if (!kernels.contains(generator)) {
        kernels.add(generator);
      }
    }
    return members;
  }

  /**
   * Replace the exponentials which {@link #commonExponentials(List, EvalEngine)} took out of the
   * kernels by the powers of the dummy symbol of their generator.
   */
  private static IExpr replaceMembers(IExpr expr, Map<IExpr, IExpr[]> members,
      List<IExpr> kernels, ISymbol[] dummies) {
    IExpr result = expr;
    for (Map.Entry<IExpr, IExpr[]> entry : members.entrySet()) {
      int index = kernels.indexOf(entry.getValue()[0]);
      if (index >= 0) {
        result = F.subst(result, entry.getKey(), F.Power(dummies[index], entry.getValue()[1]));
      }
    }
    return result;
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
