package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.Primality;

public class PrimeZetaP extends AbstractFunctionEvaluator {

  public PrimeZetaP() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isOne()) {
      return F.CComplexInfinity;
    }

    // PrimeZetaP(1/k) -> ComplexInfinity for positive integer k >= 2
    if (arg1.isFraction()) {
      IRational r = (IRational) arg1;
      if (r.numerator().isOne() && r.denominator().isPositive()) {
        return F.CComplexInfinity;
      }
    }
    return F.NIL;
  }

  @Override
  public IExpr numericFunction(IAST ast, final EvalEngine engine) {
    if (ast.argSize() == 1) {
      try {
        double s = ast.arg1().evalf();
        // Only evaluate numerically for Real (approximate) arguments greater than 1.0
        if (!Double.isNaN(s) && s > 1.0) {
          double result = evaluatePrimeZetaP(s, engine);
          return F.num(result);
        }
      } catch (RuntimeException rex) {
        // return Errors.printMessage(S.PrimeZetaP, ate);
      }

      // Handle Complex Numbers
      Complex cArg = ast.arg1().evalfc();
      double re = cArg.getReal();
      double im = cArg.getImaginary();

      // Convergence requires Re(s) > 0
      if (Double.isNaN(re) || Double.isNaN(im) || re <= 0.0) {
        return F.NIL;
      }

      return evaluatePrimeZetaPComplex(cArg, engine);

    }
    return F.NIL;
  }

  /**
   * Evaluates PrimeZetaP(s) using Möbius inversion: P(s) = sum_{k=1}^K mu(k)/k * log(zeta(ks))
   */
  private double evaluatePrimeZetaP(double s, EvalEngine engine) {
    int maxK = 60;
    double result = 0.0;

    for (int k = 1; k <= maxK; k++) {
      int mu = Primality.moebiusMu(BigInteger.valueOf(k));
      if (mu == 0) {
        continue;
      }

      double ks = k * s;

      // Use the evaluator's Zeta function for accuracy
      IAST zetaAst = F.Zeta(F.num(ks));
      double zetaVal = engine.evaluate(zetaAst).evalf();

      // Default or invalid calculation boundaries
      if (Double.isNaN(zetaVal) || zetaVal <= 1.0) {
        continue;
      }

      double term = (mu / (double) k) * Math.log(zetaVal);
      result += term;

      // Series converges very rapidly for s > 1
      if (k > 5 && Math.abs(term) < 1e-16) {
        break;
      }
    }

    return result;
  }

  /**
   * Evaluates PrimeZetaP(s) for Complex arguments using Möbius inversion and Phase Unwrapping.
   */
  private IExpr evaluatePrimeZetaPComplex(Complex c, EvalEngine engine) {
    int maxK = 60;
    double sRe = c.getReal();
    double sIm = c.getImaginary();
    double resultRe = 0.0;
    double resultIm = 0.0;

    for (int k = 1; k <= maxK; k++) {
      int mu = Primality.moebiusMu(BigInteger.valueOf(k));
      if (mu == 0) {
        continue;
      }

      double ksRe = k * sRe;
      double ksIm = k * sIm;

      double logRe = 0.0;
      double logIm = 0.0;

      if (ksIm == 0.0) {
        // Purely real arguments do not need phase unwrapping.
        // Explicitly catch the pole at Zeta(1.0).
        if (Math.abs(ksRe - 1.0) < 1e-12) {
          return F.CComplexInfinity;
        }

        IAST zetaAst = F.Zeta(F.num(ksRe));
        IExpr zetaVal = engine.evaluate(zetaAst);

        if (zetaVal.isDirectedInfinity()) {
          return F.CComplexInfinity;
        }

        double[] zC = getComplexAsDouble(zetaVal);
        if (zC == null)
          return F.NIL;

        double zRe = zC[0];
        double zIm = zC[1];

        if (zIm != 0.0) {
          double abs = Math.hypot(zRe, zIm);
          if (abs <= 0.0)
            return F.NIL;
          logRe = Math.log(abs);
          logIm = Math.atan2(zIm, zRe);
        } else {
          double abs = Math.abs(zRe);
          if (abs <= 0.0)
            return F.NIL;
          logRe = Math.log(abs);
          logIm = (zRe < 0) ? Math.PI : 0.0;
        }

      } else if (ksRe >= 1.5) {
        // For Re(ks) >= 1.5, the principal branch is safely continuous
        IAST zetaAst = F.Zeta(F.complexNum(ksRe, ksIm));
        IExpr zetaVal = engine.evaluate(zetaAst);
        double[] zC = getComplexAsDouble(zetaVal);

        if (zC == null)
          return F.NIL;

        double abs = Math.hypot(zC[0], zC[1]);
        if (abs <= 0.0)
          return F.NIL;

        logRe = Math.log(abs);
        logIm = Math.atan2(zC[1], zC[0]);

      } else {
        // For Re(ks) < 1.5, track the phase continuously from safe region
        // Start offset slightly to avoid landing precisely on simple coordinate values
        double currentX = 1.501;
        IAST startAst = F.Zeta(F.complexNum(currentX, ksIm));
        IExpr startVal = engine.evaluate(startAst);
        double[] startC = getComplexAsDouble(startVal);

        if (startC == null)
          return F.NIL;

        double currentPhase = Math.atan2(startC[1], startC[0]);
        double step = 0.05;

        while (currentX > ksRe) {
          double nextX = currentX - step;
          if (nextX < ksRe + 1e-9) {
            nextX = ksRe; // Final step lands exactly on ksRe
          }

          IAST nextAst = F.Zeta(F.complexNum(nextX, ksIm));
          IExpr nextVal = engine.evaluate(nextAst);

          if (nextVal.isDirectedInfinity()) {
            return F.CComplexInfinity;
          }

          double[] nextC = getComplexAsDouble(nextVal);
          if (nextC == null)
            return F.NIL;

          double nextPrincipalPhase = Math.atan2(nextC[1], nextC[0]);
          double delta = nextPrincipalPhase - Math.atan2(startC[1], startC[0]);

          // Unwrap phase to ensure continuity
          if (delta > Math.PI)
            delta -= 2 * Math.PI;
          if (delta < -Math.PI)
            delta += 2 * Math.PI;

          currentPhase += delta;
          startC = nextC;
          currentX = nextX;
        }

        double abs = Math.hypot(startC[0], startC[1]);
        if (abs <= 0.0)
          return F.NIL;

        logRe = Math.log(abs);
        logIm = currentPhase;
      }

      double termRe = (mu / (double) k) * logRe;
      double termIm = (mu / (double) k) * logIm;

      resultRe += termRe;
      resultIm += termIm;

      // Only break if we are safely past the critical strip and terms have decayed
      if (ksRe >= 2.0 && Math.abs(termRe) < 1e-16 && Math.abs(termIm) < 1e-16) {
        break;
      }
    }

    return F.complexNum(resultRe, resultIm);
  }

  /**
   * Helper method to extract primitive double components from numeric complex/real evaluation.
   */
  private double[] getComplexAsDouble(IExpr expr) {
    if (expr instanceof IComplexNum) {
      return new double[] {((IComplexNum) expr).getRealPart(),
          ((IComplexNum) expr).getImaginaryPart()};
    } else if (expr instanceof IInexactNumber) {
      return new double[] {expr.evalf(), 0.0};
    } else {
      Complex c = expr.evalfc();
      if (c != null) {
        return new double[] {c.getReal(), c.getImaginary()};
      }
    }
    return null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
  }
}
