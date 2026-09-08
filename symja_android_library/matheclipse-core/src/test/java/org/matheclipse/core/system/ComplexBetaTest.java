package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.hipparchus.complex.Complex;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.numerics.functions.ComplexGamma;

/**
 * <code>Beta(z, 3)</code> against WMA at 25 digits, on both the complex and the real path.
 *
 * <p>
 * The grid deliberately includes the poles of <code>Beta</code> at the non-positive integers, and
 * those are the whole point of the test. <code>Beta</code> is not undefined there - it is merely
 * enormous, of order <code>10^15</code> at <code>z = -5</code> - and forming it as the quotient
 * <code>Gamma(a)Gamma(b)/Gamma(a+b)</code> throws the answer away. That quotient is what
 * {@code GammaJS.beta} does, and it is wrong by 25% to 60% at <code>z = -5, -12, -30</code>, which
 * is why neither path routes through it.
 */
public class ComplexBetaTest {

  private static final double MAX_ERROR = 1.0e-12;

  // {Re[z], Im[z], Re[Beta[z,3]], Im[Beta[z,3]]}
  private static final double[][] BETA_Z_3 = {{0.05, 0.0, 18.583042973286876, 0.0},
      {0.035355339059327376, 0.035355339059327376, 12.703848081887038, -14.084780436414823},
      {0.0, 0.05, -1.495324773637424, -19.912741568938365},
      {-0.035355339059327376, 0.035355339059327376, -15.703823472668136, -14.07540549867029},
      {-0.05, 0.0, -21.592442645074225, 0.0},
      {-0.035355339059327376, -0.035355339059327376, -15.703823472668136, 14.07540549867029},
      {0.0, -0.05, -1.495324773637424, 19.912741568938365},
      {0.035355339059327376, -0.035355339059327376, 12.703848081887038, 14.084780436414823},
      {0.3, 0.0, 2.229654403567447, 0.0},
      {0.21213203435596426, 0.21213203435596426, 1.2040040853589058, -2.1197987195814516},
      {0.0, 0.3, -1.345864830308876, -2.856224250988837},
      {-0.21213203435596426, 0.21213203435596426, -4.172370410515861, -1.7851799049015675},
      {-0.3, 0.0, -5.602240896358543, 0.0},
      {-0.21213203435596426, -0.21213203435596426, -4.172370410515861, 1.7851799049015675},
      {0.0, -0.3, -1.345864830308876, 2.856224250988837},
      {0.21213203435596426, -0.21213203435596426, 1.2040040853589058, 2.1197987195814516},
      {1.0, 0.0, 0.3333333333333333, 0.0},
      {0.7071067811865476, 0.7071067811865476, 0.05291146685950973, -0.3832187426918488},
      {0.0, 1.0, -0.6, -0.2},
      {-0.7071067811865476, 0.7071067811865476, -1.1117349962712744, 1.3814871396610924},
      {-0.7071067811865476, -0.7071067811865476, -1.1117349962712744, -1.3814871396610924},
      {0.0, -1.0, -0.6, 0.2},
      {0.7071067811865476, -0.7071067811865476, 0.05291146685950973, 0.3832187426918488},
      {2.0, 0.0, 0.08333333333333333, 0.0},
      {1.414213562373095, 1.414213562373095, -0.013227866714877431, -0.0958046856729622},
      {0.0, 2.0, -0.15, 0.05},
      {-1.414213562373095, 1.414213562373095, 0.2779337490678186, 0.3453717849152731},
      {-1.414213562373095, -1.414213562373095, 0.2779337490678186, -0.3453717849152731},
      {0.0, -2.0, -0.15, -0.05},
      {1.414213562373095, -1.414213562373095, -0.013227866714877431, 0.0958046856729622},
      {5.0, 0.0, 0.009523809523809523, 0.0},
      {3.5355339059327378, 3.5355339059327378, -0.004559560387439919, -0.009557870110283813},
      {0.0, 5.0, -0.007957559681697613, 0.01220159151193634},
      {-3.5355339059327378, 3.5355339059327378, 0.02313078246955004, -0.005819701414206896},
      {-5.0, 0.0, -0.03333333333333333, 0.0},
      {-3.5355339059327378, -3.5355339059327378, 0.02313078246955004, 0.005819701414206896},
      {0.0, -5.0, -0.007957559681697613, -0.01220159151193634},
      {3.5355339059327378, -3.5355339059327378, -0.004559560387439919, 0.009557870110283813},
      {12.0, 0.0, 0.0009157509157509158, 0.0},
      {8.48528137423857, 8.48528137423857, -0.000567878109818583, -0.0007874777276197302},
      {0.0, 12.0, -0.00027958993476234854, 0.0011028269648959304},
      {-8.48528137423857, 8.48528137423857, 0.0011459962006539518, -0.000767400309840381},
      {-12.0, 0.0, -0.0015151515151515152, 0.0},
      {-8.48528137423857, -8.48528137423857, 0.0011459962006539518, 0.000767400309840381},
      {0.0, -12.0, -0.00027958993476234854, -0.0011028269648959304},
      {8.48528137423857, -8.48528137423857, -0.000567878109818583, 0.0007874777276197302},
      {30.0, 0.0, 6.720430107526882e-05, 0.0},
      {21.213203435596427, 21.213203435596427, -4.537643765868363e-05, -5.2010049429343565e-05},
      {0.0, 30.0, -7.366446328072054e-06, 7.350076447343005e-05},
      {-21.213203435596427, 21.213203435596427, 6.019086839340661e-05, -5.1927746629939304e-05},
      {-30.0, 0.0, -8.210180623973728e-05, 0.0},
      {-21.213203435596427, -21.213203435596427, 6.019086839340661e-05, 5.1927746629939304e-05},
      {0.0, -30.0, -7.366446328072054e-06, -7.350076447343005e-05},
      {21.213203435596427, -21.213203435596427, -4.537643765868363e-05, 5.2010049429343565e-05},};

  @Test
  public void testComplexPathAgainstWMA() {
    for (double[] row : BETA_Z_3) {
      double magnitude = Math.hypot(row[2], row[3]);
      if (!Double.isFinite(magnitude) || magnitude > 1.0e290) {
        continue;
      }
      IExpr actual = F.complexNum(row[0], row[1]).beta(F.complexNum(3.0, 0.0));
      double actualRe;
      double actualIm;
      if (actual instanceof IComplexNum) {
        actualRe = ((IComplexNum) actual).reDoubleValue();
        actualIm = ((IComplexNum) actual).imDoubleValue();
      } else if (actual.isReal()) {
        actualRe = actual.evalf();
        actualIm = 0.0;
      } else {
        throw new AssertionError("Beta did not evaluate at " + row[0] + "+" + row[1] + "i");
      }
      double error =
          Math.hypot(actualRe - row[2], actualIm - row[3]) / Math.max(magnitude, 1.0e-300);
      assertTrue(error < MAX_ERROR, "Beta(" + row[0] + "+" + row[1] + "i, 3): expected (" + row[2]
          + ", " + row[3] + ") but was (" + actualRe + ", " + actualIm + ") - error " + error);
    }
  }

  /** The real path goes through {@code DMath.beta}, which uses the same log-gamma form. */
  @Test
  public void testRealPathAgainstWMA() {
    int compared = 0;
    for (double[] row : BETA_Z_3) {
      if (Math.abs(row[1]) > 1.0e-12
          || Math.abs(row[3]) > 1.0e-9 * Math.max(Math.abs(row[2]), 1.0)) {
        continue; // only the genuinely real grid points
      }
      IExpr actual = F.num(row[0]).beta(F.num(3.0));
      if (!actual.isReal()) {
        continue;
      }
      compared++;
      double error = Math.abs(actual.evalf() - row[2]) / Math.max(Math.abs(row[2]), 1.0e-300);
      assertTrue(error < MAX_ERROR,
          "Beta(" + row[0] + ", 3): expected " + row[2] + " but was " + actual.evalf());
    }
    assertTrue(compared >= 10, "only compared " + compared + " real points");
  }

  /**
   * <code>Beta(a, b) = Beta(b, a)</code> and <code>Beta(a, b) = Beta(a+1, b) + Beta(a, b+1)</code>
   * - identities that hold independently of how the implementation gets there.
   */
  @Test
  public void testIdentities() {
    for (double[] row : new double[][] {{0.7, 0.3}, {2.5, 1.5}, {-1.5, 0.5}, {3.0, -2.5},
        {0.25, 4.0}}) {
      Complex a = new Complex(row[0], 0.3);
      Complex b = new Complex(row[1], -0.2);
      Complex ab = ComplexGamma.beta(a, b);
      Complex ba = ComplexGamma.beta(b, a);
      assertNotNull(ab);
      assertNotNull(ba);
      assertTrue(ab.subtract(ba).norm() / Math.max(ab.norm(), 1.0e-300) < 1.0e-13,
          "Beta symmetry at " + a + ", " + b);

      Complex split = ComplexGamma.beta(a.add(1.0), b).add(ComplexGamma.beta(a, b.add(1.0)));
      assertTrue(ab.subtract(split).norm() / Math.max(ab.norm(), 1.0e-300) < 1.0e-12,
          "Beta(a,b) = Beta(a+1,b) + Beta(a,b+1) at " + a + ", " + b);
    }
  }
}
