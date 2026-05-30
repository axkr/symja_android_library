package org.matheclipse.core.polynomials;

import java.util.LinkedHashSet;
import java.util.Set;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Utility methods for working with algebraic numbers.
 *
 * <p>
 * Currently provides extraction of algebraic generators occurring in an expression (for example as
 * coefficients of a polynomial), suitable to be passed as {@code Extension -> {a1, a2, ...}} to
 * {@code Factor} or {@code IrreduciblePolynomialQ}.
 */
public final class AlgebraicNumberUtils {

  private AlgebraicNumberUtils() {}

  /**
   * Walk the given expression and collect a deduplicated list of algebraic-number generators that
   * appear within it.
   *
   * <p>
   * Recognized generators are:
   * <ul>
   * <li>the imaginary unit {@link S#I} (added as itself);</li>
   * <li>{@code Sqrt[x]} (added as the {@code Sqrt[x]} expression);</li>
   * <li>{@code Power[x, e]} where {@code e} is a non-integer rational (added as the full
   * {@code Power[x, e]} expression);</li>
   * <li>{@code Root[...]} (added as the full {@code Root} expression);</li>
   * <li>{@code AlgebraicNumber[...]} (added as the full {@code AlgebraicNumber} expression).</li>
   * </ul>
   *
   * <p>
   * {@code RootSum} is intentionally not handled.
   *
   * @param expr the expression to scan (typically a polynomial)
   * @return a {@code List(...)} of distinct generators, in the order in which they were first
   *         encountered; an empty list when none are found
   */
  public static IAST extractAlgebraicGenerators(IExpr expr) {
    Set<IExpr> generators = new LinkedHashSet<>();
    collect(expr, generators);
    IASTAppendable result = F.ListAlloc(generators.size());
    for (IExpr g : generators) {
      result.append(g);
    }
    return result;
  }

  private static void collect(IExpr expr, Set<IExpr> generators) {
    if (expr == null) {
      return;
    }
    // Imaginary unit (matches both the symbol S.I and the Complex[0,1] literal F.CI)
    if (expr.isImaginaryUnit()) {
      generators.add(S.I);
      return;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      if (head == S.AlgebraicNumber || head == S.Root) {
        generators.add(ast);
        return;
      }
      if (head == S.Sqrt && ast.argSize() == 1) {
        generators.add(ast);
        // also descend into the radicand in case it contains further algebraic numbers
        collect(ast.arg1(), generators);
        return;
      }
      if (head == S.Power && ast.argSize() == 2) {
        IExpr exponent = ast.arg2();
        if (exponent.isRational() && !exponent.isInteger()) {
          generators.add(ast);
          collect(ast.arg1(), generators);
          return;
        }
      }
      // Recurse into sub-expressions (including the head, for safety)
      for (int i = 0; i < ast.size(); i++) {
        collect(ast.get(i), generators);
      }
      return;
    }
    // Complex literals with non-zero imaginary part contribute I as a generator
    if (expr.isComplex() || expr.isComplexNumeric()) {
      if (!expr.im().isZero()) {
        generators.add(S.I);
      }
    }
  }

  /**
   * Test whether the given expression lies in the field {@code Q(generators)}.
   *
   * <p>
   * The check is purely structural: every sub-expression of {@code expr} must be one of
   * <ul>
   * <li>a rational number, or a complex number whose imaginary part is rational when {@link S#I} is
   * one of the generators;</li>
   * <li>one of the supplied {@code generators} (compared by structural equality);</li>
   * <li>a {@code Plus}, {@code Times}, or {@code Power[_, _Integer]} combination of the above.</li>
   * </ul>
   *
   * <p>
   * In particular, any {@code Sqrt[_]}, {@code Surd[_, _]}, {@code Root[__]},
   * {@code AlgebraicNumber[__]} or {@code Power[_, _Rational]} (with non-integer rational exponent)
   * sub-expression that is not exactly one of the supplied generators causes the method to return
   * {@code false}. This is a sufficient (but not necessary) condition for an element to belong to
   * the extension and is intended for use as a quick reducibility test.
   *
   * @param expr the expression to inspect (typically a symbolic root)
   * @param generators the list of algebraic-number generators describing the extension field
   * @return {@code true} when {@code expr} can be syntactically expressed using only rationals and
   *         the given generators combined via {@code Plus}, {@code Times} and integer powers
   */
  public static boolean isInExtension(IExpr expr, IAST generators) {
    if (expr == null) {
      return false;
    }
    if (expr.isRational()) {
      return true;
    }
    if (expr.isImaginaryUnit() //
        || (expr.isTimes() && expr.argSize() == 2 //
            && expr.first().equals(F.CN1) && expr.second().isImaginaryUnit())) {
      return containsI(generators);
    }
    if (expr.isComplex() || expr.isComplexNumeric()) {
      return containsI(generators) && expr.re().isRational() && expr.im().isRational();
    }
    // Structural equality with one of the generators
    for (int i = 1; i <= generators.argSize(); i++) {
      if (expr.equals(generators.get(i))) {
        return true;
      }
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      if (head == S.Plus || head == S.Times) {
        for (int i = 1; i <= ast.argSize(); i++) {
          if (!isInExtension(ast.get(i), generators)) {
            return false;
          }
        }
        return true;
      }
      if (head == S.Power && ast.argSize() == 2) {
        IExpr exponent = ast.arg2();
        if (exponent.isInteger()) {
          return isInExtension(ast.arg1(), generators);
        }
        return false;
      }
      // Sqrt, Surd, Root, AlgebraicNumber, Power[_, _Rational], etc. -- only accepted when they
      // matched a generator above.
      return false;
    }
    return false;
  }

  private static boolean containsI(IAST generators) {
    for (int i = 1; i <= generators.argSize(); i++) {
      if (generators.get(i).isImaginaryUnit()) {
        return true;
      }
    }
    return false;
  }
}