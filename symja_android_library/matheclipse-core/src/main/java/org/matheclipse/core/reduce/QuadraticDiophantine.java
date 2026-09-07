package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import io.github.mangara.diophantine.QuadraticSolver;
import io.github.mangara.diophantine.Utils;
import io.github.mangara.diophantine.XYPair;

/**
 * Classifies a two variable quadratic Diophantine equation by whether its solution set is finite,
 * and enumerates it when it is.
 *
 * <p>
 * The vendored solvers in <code>io.github.mangara.diophantine</code> answer with an
 * <code>Iterator</code> which for most shapes never ends: the solutions of a Pell equation, of a
 * parabola, or of a pair of lines are infinite families. A caller which drains a fixed number of
 * them and prints the result has not answered the question, it has printed a prefix. This class
 * decides which shapes are finite, so that a complete answer is only claimed when there is one.
 *
 * <p>
 * The classification follows the discriminant, as in K. R. Matthews, <i>Solving the Diophantine
 * equation ax^2 + bxy + cy^2 + dx + ey + f = 0</i>.
 */
public final class QuadraticDiophantine {

  /** The shape of a two variable quadratic equation, and whether it has finitely many solutions. */
  public enum Kind {
    /** Not quadratic: the solutions are a lattice, handled by {@link LatticeSolver}. */
    LINEAR(false),
    /** A degenerate case with at most one solution. */
    TRIVIAL(true),
    /** An ellipse: it encloses finitely many lattice points. */
    ELLIPTICAL(true),
    /** A product of two linear forms equal to a non zero constant: finitely many divisor pairs. */
    FACTORABLE(true),
    /** A pair of lines, each carrying infinitely many points. */
    LINE_PAIR(false),
    /** A parabola. */
    PARABOLIC(false),
    /** A hyperbola, including the Pell equations. */
    HYPERBOLIC(false);

    private final boolean finite;

    Kind(boolean finite) {
      this.finite = finite;
    }

    /** Whether every equation of this shape has finitely many integer solutions. */
    public boolean isFinite() {
      return finite;
    }
  }

  private QuadraticDiophantine() {}

  /**
   * Read the coefficients of <code>a x^2 + b x y + c y^2 + d x + e y + f</code> off a polynomial.
   *
   * <p>
   * The reading is verified by rebuilding the polynomial from the coefficients, so a term of
   * higher degree, or one with a coefficient which is not an integer, rejects the whole
   * expression rather than being silently ignored.
   *
   * @return the six coefficients, or <code>null</code> if the expression is not an integer
   *         quadratic in these two variables
   */
  public static BigInteger[] coefficients(IExpr polynomial, IExpr x, IExpr y, EvalEngine engine) {
    IExpr inX = engine.evaluate(F.Coefficient(polynomial, x, F.C1));
    IExpr inY = engine.evaluate(F.Coefficient(polynomial, y, F.C1));
    IExpr[] values = new IExpr[] { //
        engine.evaluate(F.Coefficient(polynomial, x, F.C2)), //
        engine.evaluate(F.Coefficient(inX, y, F.C1)), //
        engine.evaluate(F.Coefficient(polynomial, y, F.C2)), //
        engine.evaluate(F.Coefficient(inX, y, F.C0)), //
        engine.evaluate(F.Coefficient(inY, x, F.C0)), //
        engine.evaluate(F.subst(polynomial, F.List(F.Rule(x, F.C0), F.Rule(y, F.C0))))};
    BigInteger[] coefficients = new BigInteger[values.length];
    for (int index = 0; index < values.length; index++) {
      if (!values[index].isInteger()) {
        return null;
      }
      coefficients[index] = ((IInteger) values[index]).toBigNumerator();
    }
    IExpr rebuilt = F.Plus(F.Times(values[0], F.Sqr(x)), F.Times(values[1], x, y),
        F.Times(values[2], F.Sqr(y)), F.Times(values[3], x), F.Times(values[4], y), values[5]);
    if (!engine.evaluate(F.Expand(F.Subtract(polynomial, rebuilt))).isZero()) {
      return null;
    }
    return coefficients;
  }

  /**
   * Classify <code>a x^2 + b x y + c y^2 + d x + e y + f == 0</code>.
   */
  public static Kind classify(BigInteger a, BigInteger b, BigInteger c, BigInteger d, BigInteger e,
      BigInteger f) {
    if (a.signum() == 0 && b.signum() == 0 && c.signum() == 0) {
      return Kind.LINEAR;
    }
    BigInteger discriminant = Utils.discriminant(a, b, c);
    if (discriminant.signum() == 0) {
      return Kind.PARABOLIC;
    }
    boolean square = Utils.isSquare(discriminant);
    BigInteger legendre = Utils.legendreConstant(a, b, c, d, e, f, discriminant);
    if (square) {
      // the quadratic part factors into two linear forms; their product is the Legendre constant
      return legendre.signum() == 0 ? Kind.LINE_PAIR : Kind.FACTORABLE;
    }
    if (legendre.signum() == 0) {
      // the conic degenerates to its centre, which is a solution only if it is a lattice point
      return Kind.TRIVIAL;
    }
    return discriminant.signum() < 0 ? Kind.ELLIPTICAL : Kind.HYPERBOLIC;
  }

  /**
   * Every integer solution, for an equation whose solution set is finite.
   *
   * @param limit the largest number of solutions to accept
   * @return the complete solution list, or <code>null</code> when the shape has infinitely many
   *         solutions or the enumeration exceeded <code>limit</code>
   */
  public static List<BigInteger[]> finiteSolutions(BigInteger a, BigInteger b, BigInteger c,
      BigInteger d, BigInteger e, BigInteger f, int limit) {
    if (!classify(a, b, c, d, e, f).isFinite()) {
      return null;
    }
    List<BigInteger[]> solutions = new ArrayList<BigInteger[]>();
    try {
      Iterator<XYPair> iterator = QuadraticSolver.solve(a, b, c, d, e, f);
      while (iterator.hasNext()) {
        if (solutions.size() >= limit) {
          // the shape is finite but larger than the caller is willing to materialize
          return null;
        }
        XYPair pair = iterator.next();
        solutions.add(new BigInteger[] {pair.x, pair.y});
      }
    } catch (ArithmeticException | UnsupportedOperationException exception) {
      // the vendored solvers convert some intermediate values through a machine integer
      return null;
    }
    return solutions;
  }
}
