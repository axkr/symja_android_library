package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ITensorAccess;

public class MatrixLog extends MatrixExp {

  /**
   * Closed-form 2x2 matrix logarithm using the analytic formula: *
   * 
   * <pre>
   * Log(A) = (Log(λ₁) - Log(λ₂))/(λ₁ - λ₂) * A
   * + (λ₁*Log(λ₂) - λ₂*Log(λ₁))/(λ₁ - λ₂) * I
   * </pre>
   * 
   * * For the repeated-eigenvalue case (λ₁ == λ₂ == λ, A diagonalizable): *
   * 
   * <pre>
   * Log(A) = Log(λ)*I + (A - λI)/λ     [when (A-λI)^2 = 0]
   * </pre>
   */
  private IExpr computeMatrixLog2x2(ITensorAccess mat, EvalEngine engine) {
    IExpr a = mat.getIndex(1, 1);
    IExpr b = mat.getIndex(1, 2);
    IExpr c = mat.getIndex(2, 1);
    IExpr d = mat.getIndex(2, 2);

    // Diagonal matrix: the logarithm is the element-wise logarithm of the diagonal.
    if (b.isPossibleZero(true) && c.isPossibleZero(true)) {
      IExpr logA = engine.evaluate(F.Log(a));
      IExpr logD = engine.evaluate(F.Log(d));
      return F.list(F.list(logA, F.C0), F.list(F.C0, logD));
    }

    IExpr tr = engine.evaluate(F.Plus(a, d));
    IExpr det = engine.evaluate(F.Subtract(F.Times(a, d), F.Times(b, c)));

    // Eigenvalues: λ = (tr ± sqrt(tr²-4det)) / 2
    IExpr disc = engine.evaluate(F.Subtract(F.Sqr(tr), F.Times(F.C4, det)));

    if (disc.isPossibleZero(true)) {
      // Repeated eigenvalue λ = tr/2
      // Log(A) = Log(λ)*I + (A - λI)/λ (valid when (A-λI)^2 = 0, i.e. nilpotent part)
      IExpr lambda = engine.evaluate(F.Divide(tr, F.C2));
      IExpr logLam = engine.evaluate(F.Log(lambda));
      IExpr invLam = engine.evaluate(F.Power(lambda, F.CN1));
      // Log(A) = [[logLam + (a-λ)/λ, b/λ],
      // [c/λ, logLam + (d-λ)/λ]]
      IExpr r11 = engine.evaluate(F.Plus(logLam, F.Times(F.Subtract(a, lambda), invLam)));
      IExpr r12 = engine.evaluate(F.Times(b, invLam));
      IExpr r21 = engine.evaluate(F.Times(c, invLam));
      IExpr r22 = engine.evaluate(F.Plus(logLam, F.Times(F.Subtract(d, lambda), invLam)));
      return F.list(F.list(r11, r12), F.list(r21, r22));
    }

    // Distinct eigenvalues: λ₁, λ₂
    IExpr sqrtDisc = engine.evaluate(F.Sqrt(disc));
    IExpr lambda1 = engine.evaluate(F.Divide(F.Plus(tr, sqrtDisc), F.C2));
    IExpr lambda2 = engine.evaluate(F.Divide(F.Subtract(tr, sqrtDisc), F.C2));
    IExpr diffL = engine.evaluate(F.Subtract(lambda1, lambda2));

    if (diffL.isPossibleZero(true)) {
      return F.NIL; // shouldn't happen after disc != 0, but guard anyway
    }

    IExpr logL1 = engine.evaluate(F.Log(lambda1));
    IExpr logL2 = engine.evaluate(F.Log(lambda2));
    IExpr diffLog = engine.evaluate(F.Subtract(logL1, logL2));

    // scalar coefficients of Sylvester's formula:
    // Log(A) = alpha * A + beta * I
    // alpha = (Log(λ₁) - Log(λ₂)) / (λ₁ - λ₂)
    // beta = (λ₁*Log(λ₂) - λ₂*Log(λ₁)) / (λ₁ - λ₂)
    IExpr alpha = engine.evaluate(F.Divide(diffLog, diffL));
    IExpr beta = engine
        .evaluate(F.Divide(F.Subtract(F.Times(lambda1, logL2), F.Times(lambda2, logL1)), diffL));

    IExpr r11 = engine.evaluate(F.Plus(F.Times(alpha, a), beta));
    IExpr r12 = engine.evaluate(F.Times(alpha, b));
    IExpr r21 = engine.evaluate(F.Times(alpha, c));
    IExpr r22 = engine.evaluate(F.Plus(F.Times(alpha, d), beta));

    return F.list(F.list(r11, r12), F.list(r21, r22));
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int[] dim = arg1.isMatrix();
    if (dim == null || dim[0] != dim[1] || dim[0] == 0) {
      return F.NIL;
    }
    int n = dim[0];

    // MatrixLog requires an invertible matrix: Log is not analytic or defined at the
    // eigenvalue 0, which a singular matrix (det == 0) always has. Match Mathematica by
    // emitting the `fnand` message and leaving the expression unevaluated.
    if (!engine.isNumericMode() && !arg1.isNumericArgument(true)) {
      IExpr det = engine.evaluate(F.Det(arg1));
      if (det.isZero()) {
        return Errors.printMessage(S.MatrixLog, "fnand", F.List(S.Log, F.C0), engine);
      }
    }

    // --- 1x1 case ---
    if (n == 1) {
      IExpr a = ((ITensorAccess) arg1).getIndex(1, 1);
      return F.List(F.List(engine.evaluate(F.Log(a))));
    }

    // --- 2x2 closed-form symbolic case ---
    if (n == 2 && !engine.isNumericMode() && !arg1.isNumericArgument(true)) {
      IExpr result2x2 = computeMatrixLog2x2((ITensorAccess) arg1, engine);
      if (result2x2.isPresent()) {
        return result2x2;
      }
    }

    // --- Symbolic diagonalization: Log(A) = P * Log(D) * P^-1 ---
    if (!engine.isNumericMode() && !arg1.isNumericArgument(true)) {
      IExpr matX = arg1.normal(false);
      IExpr result = applyScalarFunctionViaEigensystem(matX, n, lambda -> F.Log(lambda), engine);
      if (result.isPresent()) {
        return result;
      }
    } else {
      // --- Numeric diagonalization: Log(A) = P * Log(D) * P^-1 ---
      boolean oldNumericMode = engine.isNumericMode();
      try {
        engine.setNumericMode(true);
        IExpr matX = arg1 instanceof ITensorAccess ? arg1.normal(false) : arg1;
        IExpr result = applyScalarFunctionViaEigensystem(matX, n,
            lambda -> engine.evaluate(F.Log(lambda)), engine);
        if (result.isPresent()) {
          return result;
        }
      } finally {
        engine.setNumericMode(oldNumericMode);
      }
    }

    // Fallback to MatrixFunction for defective matrices (which uses JordanDecomposition
    // internally)
    IExpr fallback = engine.evaluate(F.MatrixFunction(S.Log, arg1));
    if (fallback.isPresent() && !fallback.isAST(S.MatrixFunction)) {
      return fallback;
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
