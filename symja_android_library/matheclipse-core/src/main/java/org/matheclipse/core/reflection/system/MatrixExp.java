package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ITensorAccess;

public class MatrixExp extends AbstractFunctionEvaluator {

  /**
   * Applies a scalar function f to each eigenvalue and reconstructs the matrix via diagonalization:
   * result = P * diag(f(λ₁),...,f(λₙ)) * P⁻¹
   */
  protected static IExpr applyScalarFunctionViaEigensystem(IExpr matX, int n,
      java.util.function.Function<IExpr, IExpr> scalarFn, EvalEngine engine) {

    IExpr eigenSys = engine.evaluate(F.Eigensystem(matX));
    if (eigenSys.isList2()) {
      IAST eigenValues = (IAST) eigenSys.first();
      IAST eigenVectors = (IAST) eigenSys.second();

      // Eigensystem returns eigenvectors as rows; P needs them as columns
      IExpr P = engine.evaluate(F.Transpose(eigenVectors));
      IExpr Pinv = engine.evaluate(F.Inverse(P));

      if (Pinv.isMatrix() != null && !Pinv.has(S.Indeterminate) && !Pinv.isDirectedInfinity()) {
        IASTAppendable fD = F.ListAlloc(n);
        for (int i = 1; i <= n; i++) {
          IASTAppendable row = F.ListAlloc(n);
          IExpr expLogFunction = scalarFn.apply(eigenValues.get(i));
          for (int j = 1; j <= n; j++) {
            row.append(i == j ? expLogFunction : F.C0);
          }
          fD.append(row);
        }
        IExpr result = engine.evaluate(F.Dot(P, F.Dot(fD, Pinv)));
        if (result.isList()) {
          return result;
        }
      }
    }
    return F.NIL;
  }

  private IExpr computeMatrix2x2(IExpr a, IExpr b, IExpr c, IExpr d, EvalEngine engine) {
    if (b.isPossibleZero(true)) {
      if (a.isPossibleZero(true) && d.isPossibleZero(true)) {
        // b=0, a=0, d=0 → e^A = {{1,0},{c,1}}
        return F.List(F.List(F.C1, F.C0), F.List(c, F.C1));
      }
      // b=0, a==d → limit of c*(e^a-e^d)/(a-d) = c*e^a
      if (engine.evaluate(F.Subtract(a, d)).isPossibleZero(true)) {
        IExpr expA = engine.evaluate(F.Exp(a));
        return F.list(F.list(expA, F.C0), F.list(engine.evaluate(F.Times(c, expA)), expA));
      }
      // {{E^a,0},{(c*(E^a-E^d))/(a-d),E^d}}
      IExpr v2 = F.Exp(a);
      IExpr v1 = F.Exp(d);
      return F.list(F.list(v2, F.C0),
          F.list(F.Times(c, F.Power(F.Subtract(a, d), F.CN1), F.Subtract(v2, v1)), v1));
    }
    if (c.isPossibleZero(true)) {
      if (a.isPossibleZero(true) && d.isPossibleZero(true)) {
        // c=0, a=0, d=0 → e^A = {{1,b},{0,1}}
        return F.List(F.List(F.C1, b), F.List(F.C0, F.C1));
      }
      // c=0, a==d → limit of b*(e^a-e^d)/(a-d) = b*e^a
      if (engine.evaluate(F.Subtract(a, d)).isPossibleZero(true)) {
        IExpr expA = engine.evaluate(F.Exp(a));
        return F.list(F.list(expA, engine.evaluate(F.Times(b, expA))), F.list(F.C0, expA));
      }
      // {{E^a,(b*(E^a-E^d))/(a-d)},{0,E^d}}
      IExpr v2 = F.Exp(a);
      IExpr v1 = F.Exp(d);
      return F.list(F.list(v2, F.Times(b, F.Power(F.Subtract(a, d), F.CN1), F.Subtract(v2, v1))),
          F.list(F.C0, v1));
    }
    // Matches scaled rotation matrices of the form {{a, b}, {-b, a}}
    if (engine.evaluate(F.Subtract(a, d)).isPossibleZero(true) && b.plus(c).isPossibleZero(true)) {
      IExpr expA = engine.evaluate(F.Exp(a));
      IExpr cosB = engine.evaluate(F.Cos(b));
      IExpr sinB = engine.evaluate(F.Sin(b));

      IExpr e11 = engine.evaluate(F.Times(expA, cosB));
      IExpr e12 = engine.evaluate(F.Times(expA, sinB));
      IExpr e21 = engine.evaluate(F.Times(F.CN1, expA, sinB));

      return F.list(F.list(e11, e12), F.list(e21, e11));
    }
    if (a.isPossibleZero(true) && d.isPossibleZero(true)) {
      IExpr zeroDiagonalNegativeProduct = computeZeroDiagonalNegativeProduct(b, c, engine);
      if (zeroDiagonalNegativeProduct.isPresent()) {
        return zeroDiagonalNegativeProduct;
      }
      // {{1/(2*E^(Sqrt(b)*Sqrt(c)))+E^(Sqrt(b)*Sqrt(c))/2,-Sqrt(b)/(E^(Sqrt(b)*Sqrt(c))*
      // 2*Sqrt(c))+(Sqrt(b)*E^(Sqrt(b)*Sqrt(c)))/(2*Sqrt(c))},{-Sqrt(c)/(E^(Sqrt(b)*Sqrt(c))*
      // 2*Sqrt(b))+(Sqrt(c)*E^(Sqrt(b)*Sqrt(c)))/(2*Sqrt(b)),1/(2*E^(Sqrt(b)*Sqrt(c)))+E^(Sqrt(b)*Sqrt(c))/
      // 2}}
      IExpr v6 = F.Sqrt(b);
      IExpr v5 = F.Sqrt(c);
      IExpr v4 = F.Times(F.C2, v6);
      IExpr v3 = F.Times(F.C2, v5);
      IExpr v2 = F.Exp(F.Times(v6, v5));
      IExpr v1 = F.Plus(F.Times(F.C1D2, F.Power(v2, F.CN1)), F.Times(F.C1D2, v2));
      return F.list(
          F.list(v1,
              F.Plus(F.Times(F.CN1, v6, F.Power(F.Times(v2, v3), F.CN1)),
                  F.Times(v6, F.Power(v3, F.CN1), v2))),
          F.list(F.Plus(F.Times(F.CN1, F.Power(F.Times(v2, v4), F.CN1), v5),
              F.Times(F.Power(v4, F.CN1), v5, v2)), v1));
    }
    IExpr tr = engine.evaluate(F.Plus(a, d));
    IExpr det = engine.evaluate(F.Subtract(F.Times(a, d), F.Times(b, c)));

    // Discriminant: delta = tr^2 - 4*det
    IExpr delta = engine.evaluate(F.Subtract(F.Sqr(tr), F.Times(F.C4, det)));
    if (delta.isPossibleZero(true)) {
      // Defective / Repeated eigenvalue case: e^A = e^(tr/2) * (I + A - (tr/2)*I)
      IExpr trHalf = engine.evaluate(F.Divide(tr, F.C2));
      IExpr expTrHalf = engine.evaluate(F.Exp(trHalf));
      IExpr termI = engine.evaluate(F.Subtract(F.C1, trHalf));

      IASTAppendable row1 = F.ListAlloc(2);
      row1.append(engine.evaluate(F.Times(expTrHalf, F.Plus(termI, a))));
      row1.append(engine.evaluate(F.Times(expTrHalf, b)));

      IASTAppendable row2 = F.ListAlloc(2);
      row2.append(engine.evaluate(F.Times(expTrHalf, c)));
      row2.append(engine.evaluate(F.Times(expTrHalf, F.Plus(termI, d))));

      return F.list(row1, row2);
    } else {
      // Distinct eigenvalues case: e^A = (e^L1 - e^L2)/(L1 - L2) * A + (L1*e^L2 - L2*e^L1)/(L1
      // - L2) * I
      IExpr sqrtDelta = engine.evaluate(F.Power(delta, F.C1D2));
      IExpr lambda1 = engine.evaluate(F.Divide(F.Plus(tr, sqrtDelta), F.C2));
      IExpr lambda2 = engine.evaluate(F.Divide(F.Subtract(tr, sqrtDelta), F.C2));

      IExpr diffL = engine.evaluate(F.Subtract(lambda1, lambda2));
      if (!diffL.isPossibleZero(true)) {
        // {{(-(a-d-Sqrt(a^2+4*b*c-2*a*d+d^2))*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/(2*Sqrt(a^
        // 2+4*b*c-2*a*d+d^2))+((a-d+Sqrt(a^2+4*b*c-2*a*d+d^2))*E^(a/2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^
        // 2)/2))/(2*Sqrt(a^2+4*b*c-2*a*d+d^2)),(-b*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^
        // 2+4*b*c-2*a*d+d^2)+(b*E^(a/2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^2+4*b*c-2*a*d+d^
        // 2)},{(-c*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^2+4*b*c-2*a*d+d^2)+(c*E^(a/
        // 2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^2+4*b*c-2*a*d+d^2),(-(-a+d-Sqrt(a^2+4*b*c-
        // 2*a*d+d^2))*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/(2*Sqrt(a^2+4*b*c-2*a*d+d^2))+((-a+d+Sqrt(a^
        // 2+4*b*c-2*a*d+d^2))*E^(a/2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/(2*Sqrt(a^2+4*b*c-2*a*d+d^
        // 2))}}
        IExpr v8 = F.Negate(a);
        IExpr v7 = F.Negate(d);
        IExpr v6 =
            F.Power(F.Plus(F.Sqr(a), F.Times(F.C4, b, c), F.Times(F.CN2, a, d), F.Sqr(d)), F.CN1D2);
        IExpr v5 = F.Sqrt(F.Plus(F.Sqr(a), F.Times(F.C4, b, c), F.Times(F.CN2, a, d), F.Sqr(d)));
        IExpr v4 = F.Negate(v5);
        IExpr v3 = F.Power(F.Times(F.C2, v5), F.CN1);
        IExpr v2 = F.Exp(F.Plus(F.Times(F.C1D2, a), F.Times(F.C1D2, d), F.Times(F.CN1D2, v5)));
        IExpr v1 = F.Exp(F.Plus(F.Times(F.C1D2, a), F.Times(F.C1D2, d), F.Times(F.C1D2, v5)));
        return F.list(
            F.list(
                F.Plus(F.Times(F.CN1, v3, F.Plus(a, v7, v4), v2),
                    F.Times(v3, F.Plus(a, v7, v5), v1)),
                F.Plus(F.Times(F.CN1, b, v6, v2), F.Times(b, v6, v1))),
            F.list(F.Plus(F.Times(F.CN1, c, v6, v2), F.Times(c, v6, v1)), F.Plus(
                F.Times(F.CN1, v3, F.Plus(v8, d, v4), v2), F.Times(v3, F.Plus(v8, d, v5), v1))));

      }
      return F.NIL;
    }
  }

  private IExpr computeMatrixExp(ITensorAccess matrix, int n, EvalEngine engine) {
    if (n == 1) {
      IExpr a = matrix.getIndex(1, 1);
      return F.List(F.List(F.Exp(a)));
    }
    if (n == 2) {
      IExpr a = matrix.getIndex(1, 1);
      IExpr b = matrix.getIndex(1, 2);
      IExpr c = matrix.getIndex(2, 1);
      IExpr d = matrix.getIndex(2, 2);
      return computeMatrix2x2(a, b, c, d, engine);
    }
    if (!engine.isNumericMode() && !matrix.isNumericArgument()) {
      return applyScalarFunctionViaEigensystem(matrix.normal(false), n,
          lambda -> engine.evaluate(F.Exp(lambda)), engine);
    }
    // Numeric/fallback diagonalization path — now delegates to shared helper
    boolean oldNumericMode = engine.isNumericMode();
    try {
      engine.setNumericMode(true);
      return applyScalarFunctionViaEigensystem(matrix.normal(false), n,
          lambda -> engine.evaluate(F.Exp(lambda)), engine);
    } finally {
      engine.setNumericMode(oldNumericMode);
    }
  }

  private IExpr computeZeroDiagonalNegativeProduct(IExpr b, IExpr c, EvalEngine engine) {
    IExpr bc = engine.evaluate(F.Times(b, c));
    if (bc.isNegativeResult()) {
      IExpr omega = engine.evaluate(F.Sqrt(F.Negate(bc)));
      IExpr cosOmega = engine.evaluate(F.Cos(omega));
      IExpr sinOmegaOverOmega = engine.evaluate(F.Divide(F.Sin(omega), omega));
      return F.list(F.List(cosOmega, engine.evaluate(F.Times(b, sinOmegaOverOmega))), //
          F.List(engine.evaluate(F.Times(c, sinOmegaOverOmega)), cosOmega));
    }
    return F.NIL;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int[] dim = arg1.isMatrix();
    if (dim != null && dim[0] == dim[1]) {
      int n = dim[0];
      IExpr v = ast.argSize() == 2 ? ast.arg2() : null;

      IExpr expMat = computeMatrixExp((ITensorAccess) arg1, n, engine);
      if (expMat.isPresent()) {
        if (v != null) {
          return engine.evaluate(F.Dot(expMat, v));
        }
        return expMat;
      }

      // Fallback to MatrixFunction for defective matrices (which uses JordanDecomposition
      // internally)
      IExpr fallback = engine.evaluate(F.MatrixFunction(S.Exp, arg1));
      if (fallback.isPresent() && !fallback.isAST(S.MatrixFunction)) {
        if (v != null) {
          return engine.evaluate(F.Dot(fallback, v));
        }
        return fallback;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

}
