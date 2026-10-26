package org.matheclipse.core.reflection.system;

import java.util.LinkedHashSet;
import java.util.Set;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeLimits;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Equations with square roots of the variables, as <code>Sqrt(1+Sqrt(x)) + Sqrt(x) == 3</code>.
 *
 * <p>
 * The product of an expression over both signs of one of its square roots
 * <code>r = Sqrt(b)</code> - its norm - no longer contains <code>r</code>: <code>A + B*r</code>
 * gives <code>A^2 - B^2*b</code>. Taking the norm over the outermost square root again and again
 * leaves a polynomial, whose roots contain every solution of the equation. A root of the
 * polynomial solves the equation only for one choice of the signs of the square roots, so each one
 * is substituted in the original equations, with the principal values of the square roots.
 */
final class RadicalEquations {

  /** The most square roots which are eliminated from one equation. */
  private static final int MAX_RADICALS = 8;

  /** The most variables of a system which is solved through the norms of its equations. */
  private static final int MAX_VARIABLES = 4;

  /** The largest norm, in leaves, which is solved. */
  private static final int MAX_NORM_LEAVES = 4000;

  /** The time the elimination of the square roots and the solution of the polynomials may take. */
  private static final long RADICAL_SOLVE_MILLIS = 4000L;

  /**
   * The time the last attempt of Solve may take, before it gives up on a system which no other
   * method solves.
   */
  static final long FALLBACK_SOLVE_MILLIS = 1500L;

  /** The number of digits an exact solution is checked with. */
  private static final long CHECK_PRECISION = 30L;

  /** The relative residual of an exact solution, checked with {@link #CHECK_PRECISION} digits. */
  private static final double EXACT_RESIDUAL = 1.0e-20;

  /** The relative residual of a machine number solution. */
  private static final double NUMERIC_RESIDUAL = 1.0e-8;

  private RadicalEquations() {}

  /**
   * Solve the square system <code>terms == 0</code>, in which a variable occurs under a square root,
   * through the polynomial norms of its terms.
   *
   * @param terms the expressions which have to become <code>0</code>
   * @param variables the variables, as many as there are terms
   * @param numeric if <code>true</code> the solutions are machine numbers
   * @param engine the evaluation engine
   * @return the list of solution lists or {@link F#NIL} if the terms aren't polynomial expressions
   *         with square roots of the variables or their norms cannot be solved in time
   */
  static IExpr solve(IAST terms, IAST variables, boolean numeric, EvalEngine engine) {
    return solve(terms, variables, numeric, RADICAL_SOLVE_MILLIS, engine);
  }

  /**
   * Solve the square system <code>terms == 0</code> through the polynomial norms of its terms,
   * within <code>millis</code> milliseconds.
   */
  static IExpr solve(IAST terms, IAST variables, boolean numeric, long millis,
      EvalEngine engine) {
    int n = variables.argSize();
    if (n < 1 || n > MAX_VARIABLES || terms.argSize() != n
        || !variables.forAll(x -> x.isSymbol())) {
      return F.NIL;
    }
    if (!terms.exists(term -> hasSquareRoot(term, variables))
        || !terms.forAll(term -> isRadicalExpression(term, variables))) {
      return F.NIL;
    }
    if (!terms.isFree(x -> x.isSymbol() && !variables.contains(x) && !x.isConstantAttribute(),
        false)) {
      // a solution with parameters cannot be checked numerically
      return F.NIL;
    }
    return TimeLimits.call(engine, () -> solveByNorms(terms, variables, numeric, engine),
        MachineProfile.millis(millis), () -> F.NIL);
  }

  private static IExpr solveByNorms(IAST terms, IAST variables, boolean numeric,
      EvalEngine engine) {
    IASTAppendable polynomials = F.ListAlloc(terms.argSize());
    for (IExpr term : terms) {
      // the norm is formed exactly: the machine numbers become the fractions of their values
      IExpr exact = term.has(x -> x.isInexactNumber(), false)
          ? engine.evaluate(NumberTheory.Rationalize.exact(term))
          : term;
      IExpr norm = norm(exact, variables, engine);
      if (norm.isNIL() || norm.isFree(x -> variables.contains(x), true)) {
        return F.NIL;
      }
      polynomials.append(F.Equal(norm, F.C0));
    }
    IExpr candidates = numeric ? numericSolutions(polynomials, variables, engine)
        : engine.evalQuiet(F.Solve(polynomials, variables));
    if (candidates.isEmptyList()) {
      return F.CEmptyList;
    }
    if (!candidates.isListOfLists()) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(candidates.argSize());
    for (IExpr candidate : (IAST) candidates) {
      IAST solution = (IAST) candidate;
      if (solution.argSize() != variables.argSize() || !solution.forAll(
          rule -> rule.isRuleAST() && rule.second().isFree(x -> variables.contains(x), true))) {
        // a family of solutions, which isn't checked
        return F.NIL;
      }
      solution = NSolve.inVariableOrder(solution, variables);
      if (isSolution(terms, solution, numeric, engine) && !result.contains(solution)) {
        result.append(solution);
      }
    }
    return result;
  }

  /**
   * The machine number solutions of the polynomial equations, also if the terms were given with
   * more digits: the polynomial solver works with machine numbers.
   */
  private static IExpr numericSolutions(IAST polynomials, IAST variables, EvalEngine engine) {
    final long precision = engine.getNumericPrecision();
    try {
      engine.setNumericPrecision(ParserConfig.MACHINE_PRECISION);
      return engine.evalQuiet(F.binaryAST2(S.NSolve, polynomials, variables));
    } finally {
      engine.setNumericPrecision(precision);
    }
  }

  /**
   * Whether the values of <code>solution</code> make every term <code>0</code>, with the principal
   * values of its square roots.
   */
  private static boolean isSolution(IAST terms, IAST solution, boolean numeric,
      EvalEngine engine) {
    for (IExpr term : terms) {
      IExpr substituted = term.replaceAll(solution).orElse(term);
      double residual = numericAbs(substituted, numeric, engine);
      if (Double.isNaN(residual)) {
        return false;
      }
      // the size of the addends, which the rounding errors are relative to
      double scale = 1.0;
      if (substituted.isPlus()) {
        for (IExpr addend : (IAST) substituted) {
          double size = numericAbs(addend, numeric, engine);
          if (Double.isNaN(size)) {
            return false;
          }
          scale = Math.max(scale, size);
        }
      }
      if (residual > (numeric ? NUMERIC_RESIDUAL : EXACT_RESIDUAL) * scale) {
        return false;
      }
    }
    return true;
  }

  /**
   * The absolute value of <code>expr</code>, with machine numbers or with {@link #CHECK_PRECISION}
   * digits, or <code>NaN</code> if it isn't a finite number.
   */
  private static double numericAbs(IExpr expr, boolean numeric, EvalEngine engine) {
    IExpr value;
    if (numeric) {
      value = engine.evalN(expr);
    } else {
      // the radicands are evaluated exactly, so that a negative one is on its branch cut
      IExpr exact = engine.evalQuiet(expr);
      if (exact.isZero()) {
        return 0.0;
      }
      value = engine.evalQuiet(F.N(exact, F.ZZ(CHECK_PRECISION)));
    }
    if (!value.isNumber()) {
      return Double.NaN;
    }
    double abs = engine.evalN(F.Abs(value)).evalf();
    return Double.isFinite(abs) ? abs : Double.NaN;
  }

  /**
   * The polynomial which is the product of <code>term</code> over all signs of its square roots of
   * the variables.
   *
   * @return {@link F#NIL} if the norm isn't a polynomial in the variables or grows too large
   */
  static IExpr norm(IExpr term, IAST variables, EvalEngine engine) {
    IExpr polynomial = numerator(term, engine);
    for (int i = 0; i <= MAX_RADICALS; i++) {
      if (polynomial.leafCount() > MAX_NORM_LEAVES) {
        return F.NIL;
      }
      IExpr radicand = outermostRadicand(polynomial, variables);
      if (radicand.isNIL()) {
        polynomial = engine.evaluate(F.Expand(polynomial));
        return polynomial.isPolynomial(variables) ? polynomial : F.NIL;
      }
      if (i == MAX_RADICALS) {
        return F.NIL;
      }
      // A + B*t with t == Sqrt(radicand)
      ISymbol t = F.Dummy("t");
      IExpr inT = polynomial.replaceAll(x -> isSquareRootOf(x, radicand)
          ? F.Power(t, x.exponent().times(F.C2))
          : F.NIL).orElse(polynomial);
      inT = numerator(inT, engine);
      if (!inT.isPolynomial(F.list(t))) {
        return F.NIL;
      }
      // (A + B*t)*(A - B*t) has only even powers of t, which are powers of the radicand
      IExpr product =
          engine.evaluate(F.Expand(F.Times(inT, F.subst(inT, t, F.Negate(t)))));
      IExpr reduced = product.replaceAll(x -> x.isPower() && x.base().equals(t)
          && x.exponent().isInteger() ? F.Power(radicand, x.exponent().times(F.C1D2)) : F.NIL)
          .orElse(product);
      if (!reduced.isFree(t)) {
        return F.NIL;
      }
      polynomial = engine.evaluate(F.Expand(reduced));
    }
    return F.NIL;
  }

  private static IExpr numerator(IExpr expr, EvalEngine engine) {
    return engine.evaluate(F.Numerator(F.Together(expr)));
  }

  /**
   * The radicand of a square root of the variables in <code>expr</code>, which isn't inside another
   * one of them.
   *
   * @return {@link F#NIL} if there is no such square root
   */
  private static IExpr outermostRadicand(IExpr expr, IAST variables) {
    Set<IExpr> radicands = new LinkedHashSet<IExpr>();
    collectRadicands(expr, variables, radicands);
    for (IExpr radicand : radicands) {
      boolean nested = false;
      for (IExpr other : radicands) {
        if (other != radicand && !other.isFree(x -> isSquareRootOf(x, radicand), true)) {
          nested = true;
          break;
        }
      }
      if (!nested) {
        return radicand;
      }
    }
    return F.NIL;
  }

  private static void collectRadicands(IExpr expr, IAST variables, Set<IExpr> radicands) {
    if (expr.isAST()) {
      if (isSquareRoot(expr, variables)) {
        radicands.add(expr.base());
      }
      for (IExpr arg : (IAST) expr) {
        collectRadicands(arg, variables, radicands);
      }
    }
  }

  /** Whether <code>expr</code> has a square root of the variables. */
  private static boolean hasSquareRoot(IExpr expr, IAST variables) {
    return !expr.isFree(x -> isSquareRoot(x, variables), true);
  }

  /** Whether <code>expr</code> is <code>b^(k/2)</code> with an odd <code>k</code>. */
  private static boolean isHalfPower(IExpr expr) {
    return expr.isPower() && expr.exponent().isFraction()
        && ((IFraction) expr.exponent()).denominator().equals(F.C2);
  }

  private static boolean isSquareRoot(IExpr expr, IAST variables) {
    return isHalfPower(expr) && !expr.base().isFree(x -> variables.contains(x), true);
  }

  private static boolean isSquareRootOf(IExpr expr, IExpr radicand) {
    return isHalfPower(expr) && expr.base().equals(radicand);
  }

  /**
   * Whether <code>expr</code> is built of the variables with sums, products, integer powers and
   * square roots only.
   */
  private static boolean isRadicalExpression(IExpr expr, IAST variables) {
    if (expr.isFree(x -> variables.contains(x), true) || variables.contains(expr)) {
      return true;
    }
    if (expr.isPlus() || expr.isTimes()) {
      return ((IAST) expr).forAll(x -> isRadicalExpression(x, variables));
    }
    if (expr.isPower() && (expr.exponent().isInteger() || isHalfPower(expr))) {
      return isRadicalExpression(expr.base(), variables);
    }
    return false;
  }
}
