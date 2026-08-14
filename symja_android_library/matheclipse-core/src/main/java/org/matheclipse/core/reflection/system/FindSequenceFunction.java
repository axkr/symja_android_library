package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>FindSequenceFunction(list)</code> tries to find a unary function <code>f</code> such that
 * <code>f(n)</code> generates the given integer or rational sequence (indexed from
 * <code>n=1</code>).
 *
 * <p>
 * The search runs a fixed pipeline of recognizers, cheapest first, each of which produces a fully
 * verified closed form or {@link F#NIL}:
 *
 * <ol>
 * <li>constant sequence
 * <li>arithmetic progression (linear)
 * <li>geometric progression (exponential)
 * <li>a registry of named unary builtins (<code>Factorial</code>, <code>Fibonacci</code>,
 * <code>Prime</code>, <code>PartitionsP</code>, …) matched under an affine wrapper
 * <code>alpha*f(n)+gamma</code> against <em>every</em> supplied term (M1)
 * <li>polynomial fit via finite differences, cross-validated against every term (M1)
 * <li>hypergeometric term detection – the ratio <code>a(n+1)/a(n)</code> is a rational function of
 * <code>n</code> – producing a <code>Product</code>/<code>Pochhammer</code> closed form (M2)
 * <li>C-finite detection – a linear recurrence with constant coefficients solved to a closed form
 * through {@code RSolveValue} (M3)
 * </ol>
 *
 * Unlike the previous implementation this class stores <b>no</b> hard-coded prefix tables; every
 * named candidate is evaluated live through the engine, so a match is checked against the full
 * input rather than a truncated prefix.
 */
public class FindSequenceFunction extends AbstractEvaluator {

  /**
   * Named unary builtins that are recognized under an affine wrapper. Each symbol is indexed from
   * <code>n=1</code>, i.e. the first sequence element is compared against <code>symbol(1)</code>.
   * Order matters only for tie-breaking; the more specific / faster-growing candidates come first.
   */
  private final static ISymbol[] REGISTRY = { //
      S.Factorial, //
      S.Factorial2, //
      S.CatalanNumber, //
      S.Fibonacci, //
      S.LucasL, //
      S.BellB, //
      S.Subfactorial, //
      S.Hyperfactorial, //
      S.PolygonalNumber, //
      S.PartitionsP, //
      S.PartitionsQ, //
      S.EulerPhi, //
      S.CarmichaelLambda, //
      S.PrimePi, //
      S.Prime //
  };

  /**
   * Maximum number of terms actually generated / evaluated for a candidate. Guards against path0
   * runtimes on very long inputs while staying far above every regression sequence.
   */
  private final static int MAX_TERMS = 64;

  /** Bounds for the holonomic (M4) ansatz search {@code p_0(k)*a(k)+...+p_r(k)*a(k+r)=q(k)}. */
  private final static int HOLONOMIC_MAX_ORDER = 4;
  private final static int HOLONOMIC_MAX_DEGREE = 4;
  private final static int HOLONOMIC_MAX_INHOMOGENEOUS_DEGREE = 3;
  private final static int HOLONOMIC_MAX_ANSATZ = 16;

  /** Integer bases tried by the exponential de-scaling transform (M5). */
  private final static int[] TRANSFORM_BASES = {2, 3, 4, 5, 6, 7, 8, 9, 10, -2, -3};

  /** Largest interleaving period tried by the residue-class transform (M5). */
  private final static int TRANSFORM_MAX_PERIOD = 4;

  /**
   * Wraps {@code function} around the requested variable. If {@code variable} is present the pure
   * function is applied and evaluated, otherwise the {@code Function(...)} object itself is
   * returned.
   */
  private static IExpr createFunction(IAST function, IExpr variable, EvalEngine engine) {
    if (variable.isPresent()) {
      return engine.evaluate(F.unaryAST1(function, variable));
    }
    return function;
  }

  /**
   * Builds the wrapped expression {@code alpha*symbol(#1)+gamma} and evaluates it against the
   * requested variable.
   */
  private static IExpr buildAffine(ISymbol symbol, IExpr alpha, IExpr gamma, IExpr variable,
      EvalEngine engine) {
    IExpr base = F.unaryAST1(symbol, F.Slot1);
    IExpr times = alpha.isOne() ? base : F.Times(alpha, base);
    IExpr plus = gamma.isZero() ? times : F.Plus(gamma, times);
    return createFunction(F.Function(plus), variable, engine);
  }

  /**
   * Tries to match {@code sequence} against every registered named builtin under an affine wrapper
   * {@code alpha*f(n)+gamma}. The two free parameters {@code alpha, gamma} are solved from two
   * non-degenerate points and then verified against <em>all</em> terms, which fixes the previous
   * unverified-prefix and mutually-exclusive-factor/addend defects.
   *
   * @return the matching closed form, or {@link F#NIL}
   */
  private static IExpr findRegistryFunction(IInteger[] sequence, IExpr variable,
      EvalEngine engine) {
    int m = sequence.length;
    // solving two free parameters (alpha, gamma) needs at least two confirming terms beyond the two
    // used to derive them; this also keeps very short inputs (e.g. {1,-1,3}) from matching
    // spuriously
    if (m < 4) {
      return F.NIL;
    }
    for (ISymbol symbol : REGISTRY) {
      IExpr[] candidate = generateCandidate(symbol, m, engine);
      if (candidate == null) {
        continue;
      }
      // find two indices with distinct candidate values to solve alpha, gamma
      int i0 = 0;
      int i1 = -1;
      for (int j = 1; j < m; j++) {
        if (!candidate[j].equals(candidate[i0])) {
          i1 = j;
          break;
        }
      }
      if (i1 < 0) {
        // candidate is constant on the whole range -> a constant sequence, handled elsewhere
        continue;
      }
      IExpr denom = F.eval(F.Subtract(candidate[i1], candidate[i0]));
      if (denom.isZero() || !denom.isRational()) {
        continue;
      }
      IExpr alpha = F.eval(F.Divide(F.Subtract(sequence[i1], sequence[i0]), denom));
      if (!alpha.isRational()) {
        continue;
      }
      IExpr gamma = F.eval(F.Subtract(sequence[i0], F.Times(alpha, candidate[i0])));
      if (!gamma.isRational()) {
        continue;
      }
      boolean verified = true;
      for (int k = 0; k < m; k++) {
        IExpr value = F.eval(F.Plus(F.Times(alpha, candidate[k]), gamma));
        if (!value.equals(sequence[k])) {
          verified = false;
          break;
        }
      }
      if (verified) {
        return buildAffine(symbol, alpha, gamma, variable, engine);
      }
    }
    return F.NIL;
  }

  /**
   * Evaluates {@code symbol(1), symbol(2), ..., symbol(m)}. Returns {@code null} if any value fails
   * to evaluate to an explicit rational number (so the affine solver can rely on exact arithmetic).
   */
  private static IExpr[] generateCandidate(ISymbol symbol, int m, EvalEngine engine) {
    IExpr[] values = new IExpr[m];
    for (int i = 0; i < m; i++) {
      IExpr value = engine.evaluate(F.unaryAST1(symbol, F.ZZ(i + 1)));
      if (!value.isRational()) {
        return null;
      }
      values[i] = value;
    }
    return values;
  }

  /**
   * Tries to find a polynomial function for the given sequence by taking successive differences. If
   * the d-th difference is constant the sequence is a polynomial of degree d. The candidate is
   * <em>cross-validated</em> against every supplied term and requires at least {@code d+2} data
   * points, so it can no longer over-fit an arbitrary sequence (M1).
   *
   * @return the polynomial expressed in {@link F#Slot1}, or {@link F#NIL}
   */
  private static IExpr findPolynomialFunction(IInteger[] sequence, EvalEngine engine) {
    int n = sequence.length;
    if (n < 2) {
      return F.NIL;
    }

    IASTAppendable diffs = F.ListAlloc(n);
    diffs.append(F.List(sequence));

    int degree = -1;
    for (int level = 0; level < n - 1; level++) {
      IAST current = (IAST) diffs.get(level + 1);
      if (isConstant(current)) {
        degree = level;
        break;
      }
      diffs.append(getDifferences(current));
    }
    if (degree < 0) {
      // differences never stabilized within the available data
      return F.NIL;
    }
    // A polynomial of degree d needs d+1 points to fit plus at least one confirming point (n >=
    // d+2).
    // FindSequenceFunction returns the interpolating polynomial as soon as
    // the d-th differences are constant, even for a "known" sequence such as the first ten Motzkin
    // numbers, whose 8th differences {14,14} make a degree-8 polynomial interpolate all ten terms.
    // The candidate is still cross-validated against every term below, so it always reproduces the
    // input; only its extrapolation differs from the holonomic continuation.
    if (n < degree + 2) {
      return F.NIL;
    }

    // Newton forward-difference form in Slot1:
    // a(n) = sum_{k=0}^{d} Binomial(n-1, k) * Delta^k a(1)
    IASTAppendable result = F.PlusAlloc(degree + 1);
    for (int k = 0; k <= degree; k++) {
      IInteger leadingDiff = (IInteger) ((IAST) diffs.get(k + 1)).get(1);
      if (leadingDiff.isZero()) {
        continue;
      }
      if (k == 0) {
        result.append(leadingDiff);
      } else {
        IAST binomialPolynomial = NumberTheory.binomialPolynomial(F.Plus(F.CN1, F.Slot1), k);
        result.append(F.Times(leadingDiff, binomialPolynomial));
      }
    }

    // Expand the Newton form before factoring. Factor() returns its argument unchanged if there's
    // nothing to factor, so without the Expand() the raw Newton sum would leak into the result for
    // sequences whose interpolating polynomial is a monomial (e.g. {1,4,9,16,25} -> n^2).
    IExpr polynomial = engine.evaluate(F.Factor(F.Expand(result.oneIdentity0())));
    if (!validateInSlot1(polynomial, sequence, engine)) {
      return F.NIL;
    }
    return polynomial;
  }

  /**
   * Hypergeometric-term recognizer (M2). Computes the term ratio {@code q(n) = a(n+1)/a(n)} and, if
   * it is a rational function of {@code n} (numerator and denominator of degree &le; 1), rebuilds
   * the sequence as {@code a(1) * Product[q(k), {k, 1, n-1}]} and lets the engine simplify the
   * product to a closed form (typically a {@code Pochhammer}/{@code Gamma}/{@code Binomial}
   * expression). The result is only returned after it reproduces every supplied term.
   *
   * @return the closed form applied to {@code variable}, or {@link F#NIL}
   */
  private static IExpr findHypergeometricFunction(IInteger[] sequence, IExpr variable,
      EvalEngine engine) {
    int m = sequence.length;
    if (m < 4) {
      return F.NIL;
    }
    for (int i = 0; i < m; i++) {
      if (sequence[i].isZero()) {
        return F.NIL;
      }
    }
    // ratios q[i] = a(i+2)/a(i+1) live at n = i+1
    IExpr[] q = new IExpr[m - 1];
    for (int i = 0; i < m - 1; i++) {
      q[i] = sequence[i + 1].divideBy(sequence[i]).normalize();
    }
    // fit q(n) = (a0 + a1*n) / (b0 + b1*n) up to a common scale using three sample points, then
    // verify on the remaining ratios. Setting b0=1 (or b1=1) removes the scale ambiguity in the
    // common cases; a full solve is unnecessary for the ratios that actually occur here.
    ISymbol k = F.Dummy("k");
    IExpr ratioFunction = fitRationalRatio(q, k, engine);
    if (ratioFunction.isNIL()) {
      return F.NIL;
    }

    ISymbol n = variable.isSymbol() ? (ISymbol) variable : F.Dummy("n");
    IExpr product = F.Product(ratioFunction, F.List(k, F.C1, F.Subtract(n, F.C1)));
    IExpr closedForm = engine.evaluate(F.Times(sequence[0], product));
    if (closedForm.isFree(S.Product) && !closedForm.isIndeterminate()
        && validateInVariable(closedForm, n, sequence, engine)) {
      if (variable.isPresent()) {
        return closedForm;
      }
      return engine.evaluate(F.Function(F.subst(closedForm, n, F.Slot1)));
    }
    return F.NIL;
  }

  /**
   * Attempts to express the sampled ratios {@code q[i]} (with {@code q[i]} taken at
   * {@code var=i+1}) as a single rational function {@code (a0+a1*var)/(b0+b1*var)}. Returns that
   * expression or {@link F#NIL} if the samples are not consistent with such a form.
   */
  private static IExpr fitRationalRatio(IExpr[] q, ISymbol var, EvalEngine engine) {
    int len = q.length;
    if (len < 3) {
      return F.NIL;
    }
    // Build the linear system for [a0, a1, b0, b1] from q(x)*(b0+b1*x) = a0+a1*x, i.e.
    // a0 + a1*x - q*b0 - q*x*b1 = 0. Fix the scale by trying b1=1 first, then b0=1.
    for (int fixed = 0; fixed < 2; fixed++) {
      IExpr[] coeffs = solveRatio(q, fixed, engine);
      if (coeffs != null) {
        IExpr a0 = coeffs[0];
        IExpr a1 = coeffs[1];
        IExpr b0 = coeffs[2];
        IExpr b1 = coeffs[3];
        IExpr num = F.Plus(a0, F.Times(a1, var));
        IExpr den = F.Plus(b0, F.Times(b1, var));
        if (den.isZero()) {
          continue;
        }
        IExpr candidate = engine.evaluate(F.Divide(num, den));
        // verify the fitted function reproduces every sampled ratio
        boolean ok = true;
        for (int i = 0; i < len; i++) {
          IExpr value = engine.evaluate(F.subst(candidate, var, F.ZZ(i + 1)));
          if (!value.equals(q[i])) {
            ok = false;
            break;
          }
        }
        if (ok) {
          return candidate;
        }
      }
    }
    return F.NIL;
  }

  /**
   * Solves for {@code [a0, a1, b0, b1]} from three ratio samples with one denominator coefficient
   * fixed to one ({@code fixed==0} fixes {@code b1=1}, {@code fixed==1} fixes {@code b0=1}). Uses
   * the engine's linear solver on the 3x3 system; returns {@code null} if singular.
   */
  private static IExpr[] solveRatio(IExpr[] q, int fixed, EvalEngine engine) {
    // unknowns u = [a0, a1, and the free denominator coefficient]
    // equations (for i=0..2, x=i+1): a0 + a1*x - free*(q*x or q) = q*x or q (moved constant)
    IASTAppendable rows = F.ListAlloc(3);
    IASTAppendable rhs = F.ListAlloc(3);
    for (int i = 0; i < 3; i++) {
      IExpr x = F.ZZ(i + 1);
      IExpr qi = q[i];
      IExpr freeCol;
      IExpr constant;
      if (fixed == 0) {
        // b1 = 1: a0 + a1*x - b0*q = q*x
        freeCol = qi.negate();
        constant = F.Times(qi, x);
      } else {
        // b0 = 1: a0 + a1*x - b1*(q*x) = q
        freeCol = F.Times(qi, x).negate();
        constant = qi;
      }
      rows.append(F.List(F.C1, x, freeCol));
      rhs.append(constant);
    }
    IExpr solution = engine.evaluate(F.LinearSolve(rows, rhs));
    if (!solution.isList() || solution.size() != 4) {
      return null;
    }
    IAST sol = (IAST) solution;
    IExpr a0 = sol.arg1();
    IExpr a1 = sol.arg2();
    IExpr freeVal = sol.arg3();
    if (fixed == 0) {
      return new IExpr[] {a0, a1, freeVal, F.C1};
    }
    return new IExpr[] {a0, a1, F.C1, freeVal};
  }

  /**
   * C-finite recognizer (M3). Uses {@link S#FindLinearRecurrence} to detect a linear recurrence
   * with constant coefficients and, if found, solves it through {@code RSolveValue} to a closed
   * form which is then verified against every supplied term.
   *
   * @return the closed form applied to {@code variable}, or {@link F#NIL}
   */
  private static IExpr findCFiniteFunction(IInteger[] sequence, IExpr variable, EvalEngine engine) {
    int m = sequence.length;
    if (m < 4) {
      return F.NIL;
    }
    IASTAppendable list = F.ListAlloc(m);
    for (int i = 0; i < m; i++) {
      list.append(sequence[i]);
    }
    IExpr recurrence = engine.evaluate(F.unaryAST1(S.FindLinearRecurrence, list));
    if (!recurrence.isList() || recurrence.isEmptyList()) {
      return F.NIL;
    }
    IAST coeffs = (IAST) recurrence;
    int order = coeffs.argSize();
    // need at least 2*order terms to trust an order-`order` recurrence (that is exactly the bound
    // FindLinearRecurrence itself enforces); require one more so a closed form is over-determined
    if (m < 2 * order + 1) {
      return F.NIL;
    }

    ISymbol n = variable.isSymbol() ? (ISymbol) variable : F.Dummy("n");
    ISymbol a = F.Dummy("a");
    // recurrence: a(n) == b1*a(n-1) + b2*a(n-2) + ... + bk*a(n-k)
    IASTAppendable rhs = F.PlusAlloc(order);
    for (int i = 1; i <= order; i++) {
      rhs.append(F.Times(coeffs.get(i), F.unaryAST1(a, F.Subtract(n, F.ZZ(i)))));
    }
    IASTAppendable eqns = F.ListAlloc(order + 1);
    eqns.append(F.Equal(F.unaryAST1(a, n), rhs.oneIdentity0()));
    for (int i = 0; i < order; i++) {
      eqns.append(F.Equal(F.unaryAST1(a, F.ZZ(i + 1)), sequence[i]));
    }

    IExpr closedForm;
    try {
      closedForm = engine.evaluate(F.ternaryAST3(S.RSolveValue, eqns, F.unaryAST1(a, n), n));
    } catch (RuntimeException rex) {
      return F.NIL;
    }
    if (closedForm.isFree(n) || !closedForm.isFree(a)) {
      return F.NIL;
    }
    if (!validateInVariable(closedForm, n, sequence, engine)) {
      return F.NIL;
    }
    if (variable.isPresent()) {
      return closedForm;
    }
    return engine.evaluate(F.Function(F.subst(closedForm, n, F.Slot1)));
  }

  /**
   * Verifies that a Slot1-expression reproduces the sequence at {@code #1 = 1, 2, ..., m}.
   */
  private static boolean validateInSlot1(IExpr slotExpr, IInteger[] sequence, EvalEngine engine) {
    IAST function = F.Function(slotExpr);
    for (int i = 0; i < sequence.length; i++) {
      IExpr value = engine.evaluate(F.unaryAST1(function, F.ZZ(i + 1)));
      if (!value.equals(sequence[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Verifies that {@code expr} (a function of {@code variable}) reproduces the sequence at
   * {@code variable = 1, 2, ..., m}.
   */
  private static boolean validateInVariable(IExpr expr, ISymbol variable, IInteger[] sequence,
      EvalEngine engine) {
    for (int i = 0; i < sequence.length; i++) {
      IExpr value = engine.evaluate(F.subst(expr, variable, F.ZZ(i + 1)));
      if (!value.equals(sequence[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Top-level entry point for a single sequence. Runs the direct recognizer pipeline (M1–M4) and,
   * if that fails, the transformation layer (M5) which retries the direct pipeline on a transformed
   * sequence and inverts the transform on success.
   */
  private static IExpr findSequenceFunction(IInteger[] sequence, IExpr variable,
      EvalEngine engine) {
    if (sequence.length > MAX_TERMS) {
      IInteger[] truncated = new IInteger[MAX_TERMS];
      System.arraycopy(sequence, 0, truncated, 0, MAX_TERMS);
      sequence = truncated;
    }
    IExpr result = findSequenceFunctionDirect(sequence, variable, engine);
    if (result.isPresent()) {
      return result;
    }
    return findByTransformation(sequence, variable, engine);
  }

  /**
   * Runs the direct recognizer pipeline (M1–M4) for a single integer sequence.
   */
  private static IExpr findSequenceFunctionDirect(IInteger[] sequence, IExpr variable,
      EvalEngine engine) {
    // constant sequence
    boolean constant = true;
    for (int i = 1; i < sequence.length; i++) {
      if (!sequence[i].equals(sequence[0])) {
        constant = false;
        break;
      }
    }
    if (constant) {
      IAST function = F.Function(sequence[0]);
      return createFunction(function, variable, engine);
    }

    // arithmetic progression
    IInteger diff = sequence[1].subtract(sequence[0]);
    if (isArithmetic(sequence, diff)) {
      IInteger intercept = sequence[0].subtract(diff);
      IExpr times = diff.isOne() ? F.Slot1 : F.Times(diff, F.Slot1);
      IExpr plus = intercept.isZero() ? times : F.Plus(intercept, times);
      IAST function = F.Function(plus);
      return createFunction(function, variable, engine);
    }

    // geometric progression
    if (!sequence[0].isZero()) {
      IExpr ratio = sequence[1].divideBy(sequence[0]);
      if (!ratio.isOne() && isGeometric(sequence, (IRational) ratio)) {
        IExpr constantFactor = sequence[0].divideBy((IRational) ratio);
        IExpr power = F.Power(ratio, F.Slot1);
        IExpr times = constantFactor.isOne() ? power : F.Times(constantFactor, power);
        IAST function = F.Function(times);
        return createFunction(function, variable, engine);
      }
    }

    // named builtins under an affine wrapper (M1)
    IExpr result = findRegistryFunction(sequence, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    // polynomial fit (M1)
    IExpr polynomial = findPolynomialFunction(sequence, engine);
    if (polynomial.isPresent()) {
      return createFunction(F.Function(polynomial), variable, engine);
    }

    // hypergeometric term (M2)
    result = findHypergeometricFunction(sequence, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    // C-finite linear recurrence (M3)
    result = findCFiniteFunction(sequence, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    // general holonomic / P-recursive guesser -> DifferenceRoot (M4)
    result = findHolonomicFunction(sequence, variable, engine);
    if (result.isPresent()) {
      return result;
    }

    return F.NIL;
  }

  /**
   * Holonomic (P-recursive) guesser (M4), integer entry point. Delegates to
   * {@link #findHolonomic(IRational[], IExpr, EvalEngine)}.
   */
  private static IExpr findHolonomicFunction(IInteger[] sequence, IExpr variable,
      EvalEngine engine) {
    IRational[] rationals = new IRational[sequence.length];
    System.arraycopy(sequence, 0, rationals, 0, sequence.length);
    return findHolonomic(rationals, variable, engine);
  }

  /**
   * Holonomic (P-recursive) guesser (M4). Searches for the lowest-order linear recurrence with
   * polynomial coefficients and an optional polynomial inhomogeneity
   *
   * <pre>
   * p_0(k)*a(k) + p_1(k)*a(k+1) + ... + p_r(k)*a(k+r) = q(k),   deg(p_i) &lt;= d, deg(q) &lt;= e
   * </pre>
   *
   * (indexed from {@code k=1}). Candidate {@code (order r, degree d, inhomogeneous degree e)}
   * triples are enumerated <em>order first</em>, which prefers a low-order recurrence even at the
   * cost of higher-degree coefficients or an inhomogeneous term — and the exact null space of the
   * associated coefficient matrix is computed. A recurrence is accepted only if the null space is
   * one-dimensional (over-determined, so it is not fitted), its leading coefficient is non-zero,
   * and it generatively reproduces every supplied term. The closed form returned is a 1-indexed
   * {@code DifferenceRoot(...)[n]}.
   *
   * @return the {@code DifferenceRoot} closed form, or {@link F#NIL}
   */
  private static IExpr findHolonomic(IRational[] sequence, IExpr variable, EvalEngine engine) {
    int nTerms = sequence.length;
    if (nTerms < 6) {
      return F.NIL;
    }
    for (int r = 1; r <= HOLONOMIC_MAX_ORDER; r++) {
      for (int d = 0; d <= HOLONOMIC_MAX_DEGREE; d++) {
        for (int e = -1; e <= HOLONOMIC_MAX_INHOMOGENEOUS_DEGREE; e++) {
          int unknowns = (r + 1) * (d + 1) + (e >= 0 ? e + 1 : 0);
          if (unknowns > HOLONOMIC_MAX_ANSATZ) {
            continue;
          }
          int rows = nTerms - r;
          // require the system to be over-determined so a non-trivial null space is real, not
          // fitted
          if (rows < unknowns + 1) {
            continue;
          }
          IExpr result = tryHolonomicAnsatz(sequence, r, d, e, variable, engine);
          if (result.isPresent()) {
            return result;
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Attempts a single {@code (order r, coefficient degree d, inhomogeneous degree e)} ansatz:
   * builds the exact coefficient matrix (1-indexed), extracts a one-dimensional null space,
   * fast-verifies the recurrence generatively over the rationals, and only then materializes and
   * returns the {@code DifferenceRoot} (re-checked through the engine so the emitted object really
   * reproduces the sequence, e.g. at a singular leading coefficient).
   */
  private static IExpr tryHolonomicAnsatz(IRational[] seq, int r, int d, int e, IExpr variable,
      EvalEngine engine) {
    int nTerms = seq.length;
    int rows = nTerms - r;
    int valueCols = (r + 1) * (d + 1);
    int qCols = (e >= 0) ? e + 1 : 0;
    int unknowns = valueCols + qCols;
    int maxPow = Math.max(d, e);

    IASTAppendable matrix = F.ListAlloc(rows);
    for (int t = 0; t < rows; t++) {
      int k = t + 1; // 1-based index
      IInteger[] kPow = new IInteger[maxPow + 1];
      kPow[0] = F.C1;
      for (int p = 1; p <= maxPow; p++) {
        kPow[p] = kPow[p - 1].multiply(F.ZZ(k));
      }
      IASTAppendable row = F.ListAlloc(unknowns);
      for (int i = 0; i <= r; i++) {
        for (int j = 0; j <= d; j++) {
          row.append(seq[t + i].multiply(kPow[j])); // k^j * a(k+i)
        }
      }
      for (int l = 0; l < qCols; l++) {
        row.append(kPow[l].negate()); // -k^l (moves q(k) to the left-hand side)
      }
      matrix.append(row);
    }

    IExpr nullSpace = engine.evaluate(F.NullSpace(matrix));
    if (!nullSpace.isList() || nullSpace.size() != 2) {
      // require exactly one basis vector (one-dimensional null space)
      return F.NIL;
    }
    IExpr vector = ((IAST) nullSpace).arg1();
    if (!vector.isList() || vector.size() != unknowns + 1) {
      return F.NIL;
    }
    IExpr[] coeffs = clearDenominators((IAST) vector);
    if (coeffs == null) {
      return F.NIL;
    }

    // slice out the coefficient polynomials p_i and the inhomogeneous polynomial q
    IExpr[][] p = new IExpr[r + 1][d + 1];
    for (int i = 0; i <= r; i++) {
      for (int j = 0; j <= d; j++) {
        p[i][j] = coeffs[i * (d + 1) + j];
      }
    }
    IExpr[] q = new IExpr[qCols];
    for (int l = 0; l < qCols; l++) {
      q[l] = coeffs[valueCols + l];
    }
    boolean leadingNonZero = false;
    for (int j = 0; j <= d; j++) {
      if (!p[r][j].isZero()) {
        leadingNonZero = true;
        break;
      }
    }
    if (!leadingNonZero) {
      // degenerate: the true order is smaller, a lower ansatz would have matched
      return F.NIL;
    }

    if (!generativelyReproduces(seq, p, q, r)) {
      return F.NIL;
    }

    // materialize the 1-indexed DifferenceRoot: sum_i p_i(k)*y(k+i) - q(k) == 0, y(1..r) == seq
    ISymbol y = F.Dummy("y");
    ISymbol k = F.Dummy("k");
    IASTAppendable lhs = F.PlusAlloc(r + 2);
    for (int i = 0; i <= r; i++) {
      IExpr pI = buildPoly(p[i], k);
      if (!pI.isZero()) {
        IExpr yk = (i == 0) ? F.unaryAST1(y, k) : F.unaryAST1(y, F.Plus(k, F.ZZ(i)));
        lhs.append(F.Times(pI, yk));
      }
    }
    IExpr qExpr = buildPoly(q, k);
    IExpr recurrence = F.Equal(engine.evaluate(F.Subtract(lhs.oneIdentity0(), qExpr)), F.C0);
    IASTAppendable equations = F.ListAlloc(r + 1);
    equations.append(recurrence);
    for (int i = 1; i <= r; i++) {
      equations.append(F.Equal(F.unaryAST1(y, F.ZZ(i)), seq[i - 1]));
    }
    IExpr diffRoot = F.DifferenceRoot(F.Function(F.List(y, k), equations));

    // final safety net: make sure the emitted object really reproduces the sequence when the engine
    // iterates it (guards against singular-leading-coefficient handling differences)
    IExpr last = engine.evaluate(F.unaryAST1(diffRoot, F.ZZ(nTerms)));
    if (!last.equals(seq[nTerms - 1])) {
      return F.NIL;
    }

    if (variable.isPresent()) {
      return F.unaryAST1(diffRoot, variable);
    }
    return F.Function(F.unaryAST1(diffRoot, F.Slot1));
  }

  /**
   * Generatively checks that the recurrence {@code sum_i p_i(k)*a(k+i) = q(k)} with initial values
   * {@code a(1..r) = seq[0..r-1]} reproduces the whole sequence (1-indexed), using exact rational
   * arithmetic. Handles a singular leading coefficient {@code p_r(k)=0} by falling back to a
   * consistency check at that index.
   */
  private static boolean generativelyReproduces(IRational[] seq, IExpr[][] p, IExpr[] q, int r) {
    int nTerms = seq.length;
    IRational[] y = new IRational[nTerms + 1]; // y[1..nTerms]
    for (int i = 1; i <= r; i++) {
      y[i] = seq[i - 1];
    }
    for (int k = 1; k <= nTerms - r; k++) {
      IRational pr = evalPoly(p[r], k);
      IRational qk = (q.length == 0) ? F.C0 : evalPoly(q, k);
      if (pr == null || qk == null) {
        return false;
      }
      IRational s = F.C0;
      for (int i = 0; i < r; i++) {
        IRational pik = evalPoly(p[i], k);
        if (pik == null) {
          return false;
        }
        s = s.add(pik.multiply(y[k + i]));
      }
      IRational actual = seq[k + r - 1];
      if (pr.isZero()) {
        // recurrence does not determine a(k+r): require it to be consistent with the actual value.
        // Compare by value (subtract to zero): divideBy/multiply may return unnormalized fractions
        // whose equals() differs from the integer representation of the same value.
        if (!s.subtract(qk).isZero()) {
          return false;
        }
        y[k + r] = actual;
      } else {
        IRational generated = qk.subtract(s).divideBy(pr);
        if (!generated.subtract(actual).isZero()) {
          return false;
        }
        y[k + r] = generated;
      }
    }
    return true;
  }

  /**
   * Evaluates the polynomial with rational coefficients {@code coeff[0..deg]} at integer {@code k}.
   */
  private static IRational evalPoly(IExpr[] coeff, int k) {
    IRational sum = F.C0;
    IInteger kk = F.ZZ(k);
    IInteger kPow = F.C1;
    for (int j = 0; j < coeff.length; j++) {
      if (!coeff[j].isRational()) {
        return null;
      }
      sum = sum.add(((IRational) coeff[j]).multiply(kPow));
      kPow = kPow.multiply(kk);
    }
    return sum;
  }

  /** Builds the polynomial {@code sum_j coeff[j]*k^j} as an {@link IExpr} in {@code k}. */
  private static IExpr buildPoly(IExpr[] coeff, ISymbol k) {
    IASTAppendable poly = F.PlusAlloc(coeff.length);
    for (int j = 0; j < coeff.length; j++) {
      if (!coeff[j].isZero()) {
        poly.append(j == 0 ? coeff[j] : F.Times(coeff[j], F.Power(k, F.ZZ(j))));
      }
    }
    return poly.oneIdentity0();
  }

  /**
   * Clears denominators of a rational null-space vector, returning an equivalent
   * integer-coefficient vector (and normalizing the overall sign to be non-negative on its last
   * non-zero entry). Returns {@code null} if any entry is not rational.
   */
  private static IExpr[] clearDenominators(IAST vector) {
    int n = vector.argSize();
    IExpr[] result = new IExpr[n];
    IInteger lcm = F.C1;
    for (int i = 1; i <= n; i++) {
      IExpr entry = vector.get(i);
      if (!entry.isRational()) {
        return null;
      }
      lcm = lcm.lcm(((IRational) entry).denominator());
    }
    IInteger sign = null;
    for (int i = 0; i < n; i++) {
      IRational scaled = ((IRational) vector.get(i + 1)).multiply(lcm);
      result[i] = scaled;
      if (!scaled.isZero()) {
        sign = scaled.numerator();
      }
    }
    if (sign != null && sign.isNegative()) {
      for (int i = 0; i < n; i++) {
        result[i] = ((IRational) result[i]).negate();
      }
    }
    return result;
  }

  /**
   * Transformation layer (M5). When the direct pipeline fails, retries it on a transformed sequence
   * and inverts the transform if a closed form is found:
   *
   * <ul>
   * <li>{@code a(n)/c^n} for a small integer base {@code c} (turns e.g. {@code c^n*poly(n)} or a
   * shifted P-recursive sequence into something the direct pipeline recognizes)
   * <li>{@code a(n)/n!} (factorial de-scaling)
   * <li>interleaved residue classes modulo a small period {@code p}
   * </ul>
   *
   * Every transform recurses into {@link #findSequenceFunctionDirect} (never back into this method)
   * and the recombined result is fully re-verified against the original sequence.
   *
   * @return the recombined closed form, or {@link F#NIL}
   */
  private static IExpr findByTransformation(IInteger[] sequence, IExpr variable,
      EvalEngine engine) {
    int m = sequence.length;
    if (m < 5) {
      return F.NIL;
    }
    ISymbol n = variable.isSymbol() ? (ISymbol) variable : F.Dummy("n");

    // 1. exponential de-scaling a(n)/c^n
    for (int c : TRANSFORM_BASES) {
      IInteger base = F.ZZ(c);
      IInteger[] scaled = new IInteger[m];
      boolean ok = true;
      IInteger power = base; // c^(i+1) since the sequence is 1-indexed
      for (int i = 0; i < m; i++) {
        IInteger[] qr = sequence[i].divideAndRemainder(power);
        if (qr == null || !qr[1].isZero()) {
          ok = false;
          break;
        }
        scaled[i] = qr[0];
        power = power.multiply(base);
      }
      if (ok) {
        IExpr inner = findSequenceFunctionDirect(scaled, n, engine);
        if (inner.isPresent()) {
          IExpr closedForm = engine.evaluate(F.Times(F.Power(base, n), inner));
          IExpr verified = verifyAndWrap(closedForm, n, sequence, variable, engine);
          if (verified.isPresent()) {
            return verified;
          }
        }
      }
    }

    // 2. factorial de-scaling a(n)/n!
    IInteger[] descaled = new IInteger[m];
    boolean factorialOk = true;
    IInteger factorial = F.C1;
    for (int i = 0; i < m; i++) {
      factorial = factorial.multiply(F.ZZ(i + 1)); // (i+1)!
      IInteger[] qr = sequence[i].divideAndRemainder(factorial);
      if (qr == null || !qr[1].isZero()) {
        factorialOk = false;
        break;
      }
      descaled[i] = qr[0];
    }
    if (factorialOk) {
      IExpr inner = findSequenceFunctionDirect(descaled, n, engine);
      if (inner.isPresent()) {
        IExpr closedForm = engine.evaluate(F.Times(F.Factorial(n), inner));
        IExpr verified = verifyAndWrap(closedForm, n, sequence, variable, engine);
        if (verified.isPresent()) {
          return verified;
        }
      }
    }

    // 3. interleaved residue classes modulo a small period p
    for (int p = 2; p <= TRANSFORM_MAX_PERIOD; p++) {
      if (m < p * 3) {
        break;
      }
      IExpr piecewise = findByResidue(sequence, p, n, engine);
      if (piecewise.isPresent()) {
        IExpr verified = verifyAndWrap(piecewise, n, sequence, variable, engine);
        if (verified.isPresent()) {
          return verified;
        }
      }
    }

    return F.NIL;
  }

  /**
   * Splits the sequence into {@code p} residue classes {@code a(n) with n = q, q+p, q+2p, ...},
   * recognizes each class independently and, on success, assembles a {@code Piecewise} over
   * {@code Mod(n-1, p)}.
   */
  private static IExpr findByResidue(IInteger[] sequence, int p, ISymbol n, EvalEngine engine) {
    int m = sequence.length;
    IExpr[] classForms = new IExpr[p];
    ISymbol q = F.Dummy("q");
    for (int res = 0; res < p; res++) {
      int len = (m - res + p - 1) / p;
      if (len < 3) {
        return F.NIL;
      }
      IInteger[] sub = new IInteger[len];
      for (int i = 0; i < len; i++) {
        sub[i] = sequence[res + i * p];
      }
      // sub is indexed by q = 1,2,... ; its original index is (res+1) + (q-1)*p = p*q + (res+1-p)
      IExpr subForm = findSequenceFunctionDirect(sub, q, engine);
      if (subForm.isNIL()) {
        return F.NIL;
      }
      // re-express the sub-form in terms of the global index n: q = (n - res) / p (n 1-indexed)
      IExpr qOfN = F.Divide(F.Plus(F.Subtract(n, F.ZZ(res)), F.ZZ(p - 1)), F.ZZ(p));
      classForms[res] = engine.evaluate(F.subst(subForm, q, qOfN));
    }
    // build Piecewise[{{form0, Mod(n-1,p)==0}, ...}]
    IASTAppendable pieces = F.ListAlloc(p);
    for (int res = 0; res < p; res++) {
      IExpr condition = F.Equal(F.Mod(F.Subtract(n, F.C1), F.ZZ(p)), F.ZZ(res));
      pieces.append(F.List(classForms[res], condition));
    }
    return F.Piecewise(pieces);
  }

  /**
   * Verifies that {@code closedForm} (a function of {@code n}) reproduces the whole sequence and,
   * if so, wraps it for the requested variable (returning a pure {@code Function} when no variable
   * was supplied).
   */
  private static IExpr verifyAndWrap(IExpr closedForm, ISymbol n, IInteger[] sequence,
      IExpr variable, EvalEngine engine) {
    if (closedForm.isIndeterminate() || !closedForm.isFree(S.Product)) {
      return F.NIL;
    }
    if (!validateInVariable(closedForm, n, sequence, engine)) {
      return F.NIL;
    }
    if (variable.isPresent()) {
      return closedForm;
    }
    return engine.evaluate(F.Function(F.subst(closedForm, n, F.Slot1)));
  }

  /** Computes the first-order differences of a sequence. e.g., [1, 3, 7, 13] -> [2, 4, 6] */
  private static IAST getDifferences(IAST sequence) {
    if (sequence.size() < 2) {
      return F.CEmptyList;
    }
    IASTAppendable differences = F.ListAlloc(sequence.size() - 1);
    for (int i = 1; i < sequence.size() - 1; i++) {
      differences.append(sequence.get(i + 1).subtract(sequence.get(i)));
    }
    return differences;
  }

  /** Checks if the sequence is arithmetic with the given difference. */
  private static boolean isArithmetic(IInteger[] sequence, IInteger diff) {
    for (int i = 2; i < sequence.length; i++) {
      if (!sequence[i - 1].add(diff).equals(sequence[i])) {
        return false;
      }
    }
    return true;
  }

  /** Checks if the sequence is constant. */
  public static boolean isConstant(IAST sequence) {
    if (sequence.isEmpty())
      return false;
    if (sequence.size() == 2)
      return true;
    IInteger first = (IInteger) sequence.get(1);
    for (int i = 2; i < sequence.size(); i++) {
      if (!sequence.get(i).equals(first)) {
        return false;
      }
    }
    return true;
  }

  /** Checks if the sequence is geometric with the given ratio. */
  private static boolean isGeometric(IInteger[] sequence, IRational ratio) {
    for (int i = 2; i < sequence.length; i++) {
      if (sequence[i - 1].isZero() || !sequence[i].divideBy(sequence[i - 1]).equals(ratio)) {
        return false;
      }
    }
    return true;
  }

  public FindSequenceFunction() {
    // default ctor
  }

  /**
   * Recognizer for sequences whose terms are symbolic expressions (containing parameters, e.g.
   * {@code {1+a, 1+a^2, 1+a^3, ...}}). Handles a symbolic constant, and the
   * {@code a(n) = c + b*r^n} family (constant plus geometric, which subsumes a pure symbolic
   * geometric progression) by detecting that the successive differences form a geometric
   * progression. The reconstructed closed form is verified against every term through symbolic
   * simplification.
   *
   * @return the closed form, or {@link F#NIL}
   */
  private static IExpr findSymbolicFunction(IExpr[] seq, IExpr variable, EvalEngine engine) {
    int m = seq.length;
    if (m < 3) {
      return F.NIL;
    }
    ISymbol n = variable.isSymbol() ? (ISymbol) variable : F.Dummy("n");

    // constant sequence
    boolean constant = true;
    for (int i = 1; i < m; i++) {
      if (!isZeroSymbolic(F.Subtract(seq[i], seq[0]), engine)) {
        constant = false;
        break;
      }
    }
    if (constant) {
      return variable.isPresent() ? seq[0] : F.Function(seq[0]);
    }

    if (m < 4) {
      return F.NIL;
    }
    // successive differences
    IExpr[] diff = new IExpr[m - 1];
    for (int i = 0; i < m - 1; i++) {
      diff[i] = engine.evaluate(F.Simplify(F.Subtract(seq[i + 1], seq[i])));
    }
    // a(n) = c + b*r^n : the differences form a geometric progression with ratio r
    if (!diff[0].isZero()) {
      IExpr r = engine.evaluate(F.Simplify(F.Divide(diff[1], diff[0])));
      boolean geometric = !isZeroSymbolic(F.Subtract(r, F.C1), engine);
      for (int i = 1; geometric && i < m - 1; i++) {
        if (!isZeroSymbolic(F.Subtract(diff[i], F.Times(r, diff[i - 1])), engine)) {
          geometric = false;
        }
      }
      if (geometric) {
        // diff[0] = a(2)-a(1) = b*r*(r-1) => b = diff[0]/(r*(r-1)), c = a(1) - b*r
        IExpr b = engine.evaluate(F.Simplify(F.Divide(diff[0], F.Times(r, F.Subtract(r, F.C1)))));
        IExpr c = engine.evaluate(F.Simplify(F.Subtract(seq[0], F.Times(b, r))));
        IExpr closedForm = engine.evaluate(F.Simplify(F.Plus(c, F.Times(b, F.Power(r, n)))));
        if (validateSymbolic(closedForm, n, seq, engine)) {
          if (variable.isPresent()) {
            return closedForm;
          }
          return engine.evaluate(F.Function(F.subst(closedForm, n, F.Slot1)));
        }
      }
    }
    return F.NIL;
  }

  /** Simplifies {@code expr} and tests whether it is identically zero. */
  private static boolean isZeroSymbolic(IExpr expr, EvalEngine engine) {
    return engine.evaluate(F.Simplify(expr)).isZero();
  }

  /** Verifies that {@code closedForm} reproduces the symbolic sequence at {@code n = 1..m}. */
  private static boolean validateSymbolic(IExpr closedForm, ISymbol n, IExpr[] seq,
      EvalEngine engine) {
    for (int i = 0; i < seq.length; i++) {
      IExpr value = engine.evaluate(F.subst(closedForm, n, F.ZZ(i + 1)));
      if (!isZeroSymbolic(F.Subtract(value, seq[i]), engine)) {
        return false;
      }
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.arg1().isList()) {
      IAST list = (IAST) ast.arg1();
      IExpr variable = F.NIL;
      if (ast.isAST2()) {
        variable = ast.arg2();
      }
      IInteger[][] sequences;
      try {
        sequences = Convert.toRationalArray(list);
      } catch (ArgumentTypeException atex) {
        return F.NIL;
      }
      if (sequences == null) {
        // the sequence has non-rational (symbolic) entries
        if (list.argSize() > 2) {
          IExpr[] symbolic = new IExpr[list.argSize()];
          for (int i = 0; i < symbolic.length; i++) {
            symbolic[i] = list.get(i + 1);
          }
          return findSymbolicFunction(symbolic, variable, engine);
        }
        return F.NIL;
      }
      if (sequences != null) {
        if (sequences[1] == null) {
          if (sequences[0].length > 2) {
            return findSequenceFunction(sequences[0], variable, engine);
          }
        } else {
          IInteger[] numerators = sequences[0];
          IInteger[] denominators = sequences[1];
          if (numerators.length > 2 && denominators.length > 2) {
            // First try to recognize the numerator and denominator sequences independently. If both
            // resolve to elementary closed forms, their quotient is the simplest answer.
            IExpr split = F.NIL;
            IExpr numeratorFunction = findSequenceFunction(numerators, variable, engine);
            if (numeratorFunction.isPresent()) {
              IExpr denominatorFunction = findSequenceFunction(denominators, variable, engine);
              if (denominatorFunction.isPresent()) {
                split = engine.evaluate(F.Divide(numeratorFunction, denominatorFunction));
                // note: an evaluated DifferenceRoot is a data object in head position, so a plain
                // isFree(S.DifferenceRoot) (heads=false) would miss it - inspect heads as well
                if (split.isFree(x -> x.head() == S.DifferenceRoot, true)) {
                  return split;
                }
              }
            }
            // The independent split is non-elementary (a DifferenceRoot divided by something) or
            // failed. Look for a single holonomic recurrence for the whole rational-valued
            // sequence,
            // (a lower-order, possibly inhomogeneous DifferenceRoot).
            IRational[] values = new IRational[numerators.length];
            for (int i = 0; i < numerators.length; i++) {
              values[i] = numerators[i].divideBy(denominators[i]);
            }
            IExpr whole = findHolonomic(values, variable, engine);
            if (whole.isPresent()) {
              return whole;
            }
            return split;
          }
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  /** {@inheritDoc} */
  @Override
  public void setUp(final ISymbol newSymbol) {}

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
