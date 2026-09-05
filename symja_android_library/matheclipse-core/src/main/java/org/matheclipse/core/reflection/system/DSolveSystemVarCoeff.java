package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * First order systems <code>Y' == A(x).Y + b(x)</code> whose matrix depends on the variable.
 *
 * <p>
 * There is no fundamental matrix for such a system in general, because <code>A</code> at one place
 * and its integral need not commute. Two shapes where they do are solved here: a constant matrix
 * with a scalar function in front of it, and the two by two matrices which are a multiple of the
 * identity plus a multiple of one constant matrix. In both cases the fundamental matrix is the
 * exponential of the integral of <code>A</code>.
 */
final class DSolveSystemVarCoeff {

  private DSolveSystemVarCoeff() {}

  /** How large a system these shapes are looked for in. */
  private static final int MAX_SIZE = 6;

  /**
   * The general solution as one body per unknown, or {@link F#NIL} if the matrix is of neither
   * shape.
   */
  static IExpr solve(IExpr matrixA, IExpr vectorB, int n, IExpr xVar, DSolveContext ctx) {
    if (n < 1 || n > MAX_SIZE || !matrixA.isList()) {
      return F.NIL;
    }
    IExpr fundamental = scalarFactor((IAST) matrixA, n, xVar, ctx);
    if (fundamental.isNIL() && n == 2) {
      fundamental = commutative((IAST) matrixA, xVar, ctx);
    }
    if (fundamental.isNIL()) {
      return F.NIL;
    }
    return assemble(fundamental, vectorB, n, xVar, ctx);
  }

  /**
   * The fundamental matrix of <code>A(x) == f(x)*B</code> with a constant <code>B</code>, which is
   * <code>Exp(B*Integrate(f, x))</code>.
   */
  private static IExpr scalarFactor(IAST matrixA, int n, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr factor = F.NIL;
    for (int i = 1; i <= n && factor.isNIL(); i++) {
      IAST row = (IAST) matrixA.get(i);
      for (int j = 1; j <= n; j++) {
        if (!row.get(j).isZero() && !row.get(j).isFree(xVar)) {
          factor = row.get(j);
          break;
        }
      }
    }
    if (factor.isNIL()) {
      return F.NIL;
    }
    IASTAppendable constant = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      IAST row = (IAST) matrixA.get(i);
      IASTAppendable newRow = F.ListAlloc(n);
      for (int j = 1; j <= n; j++) {
        IExpr entry = engine.evaluate(F.Cancel(F.Together(F.Divide(row.get(j), factor))));
        if (!entry.isFree(xVar)) {
          return F.NIL;
        }
        newRow.append(entry);
      }
      constant.append(newRow);
    }
    IExpr integral = ctx.integrate(factor, xVar);
    if (integral.isNIL()) {
      return F.NIL;
    }
    IExpr tau = F.Dummy("tau");
    IExpr exponential = DSolveSystem.matrixExponential(constant, n, tau, ctx);
    if (exponential.isNIL()) {
      return F.NIL;
    }
    return engine.evaluate(F.subst(exponential, tau, integral));
  }

  /**
   * The fundamental matrix of a two by two <code>A == a(x)*I + b(x)*K</code> with a constant
   * <code>K</code>. Because <code>A</code> commutes with its own integral, that is
   * <code>Exp(Integrate(a, x))</code> times the exponential of <code>K*Integrate(b, x)</code>,
   * which is a cosine and a sine of the square root of minus the determinant of <code>K</code>.
   */
  private static IExpr commutative(IAST matrixA, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr trace = engine.evaluate(
        F.Divide(F.Plus(entry(matrixA, 1, 1), entry(matrixA, 2, 2)), F.C2));

    IExpr[][] rest = new IExpr[2][2];
    IExpr coefficient = F.NIL;
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        IExpr value = engine.evaluate(F.Subtract(entry(matrixA, i + 1, j + 1),
            i == j ? trace : F.C0));
        rest[i][j] = value;
        // Any nonzero entry will do, and it need not depend on the variable: the part which does
        // may be the multiple of the identity alone, as it is for a rotation of growing radius.
        if (coefficient.isNIL() && !value.isZero()) {
          coefficient = value;
        }
      }
    }
    if (coefficient.isNIL()) {
      return F.NIL;
    }
    IASTAppendable kernel = F.ListAlloc(2);
    for (int i = 0; i < 2; i++) {
      IASTAppendable row = F.ListAlloc(2);
      for (int j = 0; j < 2; j++) {
        IExpr value = engine.evaluate(F.Cancel(F.Together(F.Divide(rest[i][j], coefficient))));
        if (!value.isFree(xVar)) {
          return F.NIL;
        }
        row.append(value);
      }
      kernel.append(row);
    }

    IExpr scale = ctx.integrate(coefficient, xVar);
    if (scale.isNIL()) {
      return F.NIL;
    }
    IExpr prefactor = F.C1;
    if (!trace.isZero()) {
      IExpr integral = ctx.integrate(trace, xVar);
      if (integral.isNIL()) {
        return F.NIL;
      }
      prefactor = engine.evaluate(F.Exp(integral));
    }

    IExpr squared = engine.evaluate(F.Simplify(F.Negate(F.Det(kernel))));
    IExpr diagonal;
    IExpr offDiagonal;
    if (squared.isZero()) {
      // A nilpotent kernel: the exponential is a polynomial.
      diagonal = F.C1;
      offDiagonal = scale;
    } else if (numericSign(squared, engine) < 0) {
      IExpr frequency = engine.evaluate(F.Sqrt(F.Negate(squared)));
      diagonal = engine.evaluate(F.Cos(F.Times(frequency, scale)));
      offDiagonal = engine.evaluate(F.Divide(F.Sin(F.Times(frequency, scale)), frequency));
    } else {
      IExpr rate = engine.evaluate(F.Sqrt(squared));
      diagonal = engine.evaluate(F.Cosh(F.Times(rate, scale)));
      offDiagonal = engine.evaluate(F.Divide(F.Sinh(F.Times(rate, scale)), rate));
    }

    IASTAppendable fundamental = F.ListAlloc(2);
    for (int i = 0; i < 2; i++) {
      IASTAppendable row = F.ListAlloc(2);
      for (int j = 0; j < 2; j++) {
        row.append(engine.evaluate(F.Times(prefactor, F.Plus(i == j ? diagonal : F.C0,
            F.Times(offDiagonal, entry(kernel, i + 1, j + 1))))));
      }
      fundamental.append(row);
    }
    return fundamental;
  }

  /** Puts the arbitrary constants and the forcing term on the fundamental matrix. */
  private static IExpr assemble(IExpr fundamental, IExpr vectorB, int n, IExpr xVar,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IASTAppendable constants = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      constants.append(ctx.nextConstant());
    }
    IExpr solution = engine.evaluate(F.Expand(F.Dot(fundamental, constants)));

    boolean forced = false;
    for (int i = 1; i <= n; i++) {
      if (!vectorB.isAST() || !((IAST) vectorB).get(i).isZero()) {
        forced = true;
        break;
      }
    }
    if (forced) {
      IExpr inverse = engine.evaluate(F.Inverse(fundamental));
      if (!inverse.isList()) {
        return F.NIL;
      }
      IExpr integrand = engine.evaluate(F.Expand(F.Dot(inverse, vectorB)));
      IExpr integral = ctx.integrate(integrand, xVar);
      if (integral.isNIL()) {
        return F.NIL;
      }
      IExpr particular = engine.evaluate(F.Expand(F.Dot(fundamental, integral)));
      solution = engine.evaluate(F.Expand(F.Plus(solution, particular)));
    }
    return solution.isList() ? solution : F.NIL;
  }

  private static IExpr entry(IAST matrix, int i, int j) {
    return ((IAST) matrix.get(i)).get(j);
  }

  /** The sign of a parameter, or <code>0</code> if it does not evaluate to a real number. */
  private static int numericSign(IExpr expr, EvalEngine engine) {
    try {
      IExpr value = engine.evalN(expr);
      if (value instanceof INumber && ((INumber) value).isReal()) {
        double d = ((INumber) value).evalf();
        if (Double.isFinite(d) && d != 0.0) {
          return d > 0.0 ? 1 : -1;
        }
      }
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
    }
    return 0;
  }
}
