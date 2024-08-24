/*
 * Copyright 2022 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.mangara.diophantine.quadratic;

import io.github.mangara.diophantine.Utils;
import io.github.mangara.diophantine.XYPair;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A solver for quadratic Diophantine equations a x^2 + b xy + c y^2 + d x + e y + f = 0, where D =
 * b^2 - 4ac > 0 and not a perfect square.
 */
public class HyperbolicSolver {

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + d x + e y + f = 0, given that
   * D = b^2 - 4ac > 0 and not a perfect square.
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @param e
   * @param f
   * @return an iterator over all integer solutions (x, y)
   */
  public static Iterator<XYPair> solve(long a, long b, long c, long d, long e, long f) {
    return solve(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(c),
        BigInteger.valueOf(d), BigInteger.valueOf(e), BigInteger.valueOf(f));
  }

  /**
   * Solves the quadratic Diophantine equation a x^2 + b xy + c y^2 + d x + e y + f = 0, given that
   * D = b^2 - 4ac > 0 and not a perfect square.
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @param e
   * @param f
   * @return an iterator over all integer solutions (x, y)
   */
  public static Iterator<XYPair> solve(BigInteger a, BigInteger b, BigInteger c, BigInteger d,
      BigInteger e, BigInteger f) {
    if (d.signum() == 0 && e.signum() == 0) {
      return RestrictedHyperbolicSolver.solve(a, b, c, f);
    }

    if (a.signum() > 0) {
      return solveForPositiveA(a, b, c, d, e, f);
    } else {
      return solveForPositiveA(a.negate(), b.negate(), c.negate(), d.negate(), e.negate(),
          f.negate());
    }
  }

  private static Iterator<XYPair> solveForPositiveA(BigInteger a, BigInteger b, BigInteger c,
      BigInteger d, BigInteger e, BigInteger f) {
    BigInteger g = a.gcd(b).gcd(c).gcd(d).gcd(e);

    if (g.equals(BigInteger.ONE)) {
      return solveWithFloridaTransform(a, b, c, d, e, f);
    } else {
      if (f.mod(g).signum() != 0) {
        return Collections.emptyIterator();
      } else {
        return solveWithFloridaTransform(a.divide(g), b.divide(g), c.divide(g), d.divide(g),
            e.divide(g), f.divide(g));
      }
    }
  }

  // Pre-condition: a > 0 && (d != 0 || e != 0) && D > 0 and non-square && ae^2 - bde + cd^2 + fD !=
  // 0 && gcd(a, b, c, d, e) = 1
  private static Iterator<XYPair> solveWithFloridaTransform(BigInteger a, BigInteger b,
      BigInteger c, BigInteger d, BigInteger e, BigInteger f) {
    Equation eq = new Equation(a, b, c, d, e, f);

    FloridaTransform ft = new FloridaTransform(eq);
    RestrictedEquation reduced = ft.reduced();

    List<XYPair> representativeSolutions = RestrictedHyperbolicSolver
        .getRepresentativeSolutions(reduced.a, reduced.b, reduced.c, reduced.f);
    List<XYPair> solutions = untransformSolutions(representativeSolutions, eq.D, reduced, ft);

    return ft.buildIterator(solutions);
  }

  private static List<XYPair> untransformSolutions(List<XYPair> representativeSolutions,
      BigInteger D, RestrictedEquation eq, FloridaTransform ft) {
    XYPair uv1 = PellsSolver.leastPositivePellsFourSolution(eq.D);
    BigInteger minU = uv1.x;
    BigInteger minV = uv1.y;

    int k = findK(minU, minV, D, eq.D, ft.phi2, ft.psi2);

    BigInteger u11 = minU.subtract(eq.b.multiply(minV)).divide(BigInteger.TWO);
    BigInteger u12 = minV.multiply(eq.c.negate());
    BigInteger u21 = minV.multiply(eq.a);
    BigInteger u22 = minU.add(eq.b.multiply(minV)).divide(BigInteger.TWO);

    List<XYPair> solutions = new ArrayList<>();

    // Test T_{mu^j}(X_i, Y_i) and T_{mu^j}(-X_i, -Y_i) for 0 <= j <= k - 1, for each representative
    // solution (X_i, Y_i)
    for (XYPair sol : representativeSolutions) {
      BigInteger x = sol.x, y = sol.y;

      for (int j = 0; j <= k - 1; j++) {
        testSolution(solutions, ft, x, y);
        testSolution(solutions, ft, x.negate(), y.negate());

        BigInteger nextX = u11.multiply(x).add(u12.multiply(y));
        BigInteger nextY = u21.multiply(x).add(u22.multiply(y));
        x = nextX;
        y = nextY;
      }
    }

    return solutions;
  }

  private static int findK(BigInteger minU, BigInteger minV, BigInteger D, BigInteger D2,
      BigInteger phi2, BigInteger psi2) {
    // Find k such that v^2 = mu^k
    BigInteger u = minU;
    BigInteger v = minV;

    BigInteger h = D.multiply(BigInteger.valueOf(4)).divide(D2).sqrt(); // This is guaranteed to be
                                                                        // an integer
    BigInteger uTarget = phi2;
    BigInteger vTarget = psi2.multiply(h).divide(BigInteger.TWO);

    int k = 1;

    while (u.compareTo(uTarget) != 0 || v.compareTo(vTarget) != 0) {
      BigInteger nextU = minU.multiply(u).add(minV.multiply(D2).multiply(v)).divide(BigInteger.TWO);
      BigInteger nextV = minU.multiply(v).add(minV.multiply(u)).divide(BigInteger.TWO);

      u = nextU;
      v = nextV;
      k++;
    }

    return k;
  }

  private static void testSolution(List<XYPair> solutions, FloridaTransform ft, BigInteger x,
      BigInteger y) {
    if (x.add(ft.r1).mod(ft.r2).signum() == 0 && y.add(ft.s1).mod(ft.s2).signum() == 0) {
      BigInteger transX = x.add(ft.r1).divide(ft.r2);
      BigInteger transY = y.add(ft.s1).divide(ft.s2);

      solutions.add(new XYPair(transX, transY));
    }
  }

  private static class Equation {

    /**
     * The coefficients of this equation.
     */
    public final BigInteger a, b, c, d, e, f;

    /**
     * The determinant D = b^2 - 4ac of this equation.
     */
    public final BigInteger D;

    /**
     * The Legendre constants alpha = 2cd - be and beta = 2ae - bd.
     */
    public final BigInteger alpha, beta;

    public Equation(BigInteger a, BigInteger b, BigInteger c, BigInteger d, BigInteger e,
        BigInteger f) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
      this.e = e;
      this.f = f;

      this.D = Utils.discriminant(a, b, c);
      this.alpha = Utils.legendreAlpha(b, c, d, e);
      this.beta = Utils.legendreBeta(a, b, d, e);
    }
  }

  private static class FloridaTransform {

    public final Equation eq;

    /**
     * The coefficients of this transformation.
     */
    public final BigInteger r1, r2, s1, s2;

    public final BigInteger phi1, psi1, phi2, psi2;

    public FloridaTransform(Equation eq) {
      this.eq = eq;

      BigInteger gcdA = eq.alpha.gcd(eq.D);
      BigInteger gcdB = eq.beta.gcd(eq.D);

      this.r1 = eq.alpha.divide(gcdA);
      this.r2 = eq.D.divide(gcdA);
      this.s1 = eq.beta.divide(gcdB);
      this.s2 = eq.D.divide(gcdB);

      XYPair phiPsi = PellsSolver.leastPositivePellsFourSolution(eq.D);
      this.phi1 = phiPsi.x;
      this.psi1 = phiPsi.y;

      // phi2 = (phi1 * phi1 + D * psi1 * psi1) / 2
      this.phi2 =
          phi1.multiply(phi1).add(eq.D.multiply(psi1).multiply(psi1)).divide(BigInteger.TWO);
      this.psi2 = phi1.multiply(psi1); // phi1 * psi1
    }

    public RestrictedEquation reduced() {
      BigInteger A = eq.a.multiply(s2).multiply(s2); // a * s2 * s2
      BigInteger B = eq.b.multiply(r2).multiply(s2); // b * r2 * s2;
      BigInteger C = eq.c.multiply(r2).multiply(r2); // c * r2 * r2;

      // M = -1 * r2 * r2 * s2 * s2 * (a * e * e - b * d * e + c * d * d + f * D) / D
      BigInteger M = r2.multiply(r2).multiply(s2).multiply(s2)
          .multiply(eq.a.multiply(eq.e).multiply(eq.e).subtract(eq.b.multiply(eq.d).multiply(eq.e))
              .add(eq.c.multiply(eq.d).multiply(eq.d)).add(eq.f.multiply(eq.D)))
          .divide(eq.D);

      return new RestrictedEquation(A, B, C, M).withoutCommonDivisor();
    }

    public Iterator<XYPair> buildIterator(List<XYPair> solutions) {
      // Solutions for (phi, psi)?
      BigInteger K1posD = K1D(phi1, psi1);
      BigInteger K2posD = K2D(phi1, psi1);

      if (givesSolutions(K1posD, K2posD)) {
        return new FloridaIterator(eq, solutions, phi1, psi1, K1posD.divide(eq.D),
            K2posD.divide(eq.D));
      }

      // Solutions for (-phi, -psi)?
      BigInteger K1negD = K1D(phi1.negate(), psi1.negate());
      BigInteger K2negD = K2D(phi1.negate(), psi1.negate());

      if (givesSolutions(K1negD, K2negD)) {
        return new FloridaIterator(eq, solutions, phi1.negate(), psi1.negate(), K1negD.divide(eq.D),
            K2negD.divide(eq.D));
      }

      // Solutions for (phi2, -psi2)!
      BigInteger K1squareD = K1D(phi2, psi2);
      BigInteger K2squareD = K2D(phi2, psi2);

      return new FloridaIterator(eq, solutions, phi2, psi2, K1squareD.divide(eq.D),
          K2squareD.divide(eq.D));
    }

    private BigInteger K1D(BigInteger phi, BigInteger psi) {
      // K1(v) = alpha - (alpha * (phi1 - b * psi1) / 2 + beta * -c * psi1)
      return eq.alpha.subtract(eq.alpha.multiply(phi.subtract(eq.b.multiply(psi)))
          .divide(BigInteger.TWO).add(eq.beta.multiply(eq.c.negate()).multiply(psi)));
    }

    private BigInteger K2D(BigInteger phi, BigInteger psi) {
      // K2(v) = beta - (alpha * a * psi1 + beta * (phi1 + b * psi1) / 2)
      return eq.beta.subtract(eq.alpha.multiply(eq.a).multiply(psi)
          .add(eq.beta.multiply(phi.add(eq.b.multiply(psi))).divide(BigInteger.TWO)));
    }

    private boolean givesSolutions(BigInteger K1D, BigInteger K2D) {
      return K1D.mod(eq.D).signum() == 0 && K2D.mod(eq.D).signum() == 0;
    }
  }

  private static class FloridaIterator implements Iterator<XYPair> {

    private final BigInteger v11;
    private final BigInteger v12;
    private final BigInteger v21;
    private final BigInteger v22;
    private final BigInteger K1;
    private final BigInteger K2;
    private final BigInteger K1neg;
    private final BigInteger K2neg;

    private int solIndex;
    private final int nSolutions;
    private final List<XYPair> positiveSolutions;
    private final List<XYPair> negativeSolutions;

    public FloridaIterator(Equation eq, List<XYPair> solutions, BigInteger phi, BigInteger psi,
        BigInteger K1, BigInteger K2) {
      this.v11 = phi.subtract(eq.b.multiply(psi)).divide(BigInteger.TWO); // (phi - b * psi) / 2
      this.v12 = eq.c.negate().multiply(psi); // -c * psi
      this.v21 = eq.a.multiply(psi); // a * psi
      this.v22 = phi.add(eq.b.multiply(psi)).divide(BigInteger.TWO); // (phi + b * psi) / 2
      this.K1 = K1;
      this.K2 = K2;
      this.K1neg = v22.negate().multiply(K1).add(v12.multiply(K2));
      this.K2neg = v21.multiply(K1).subtract(v11.multiply(K2));

      this.nSolutions = solutions.size();
      this.positiveSolutions = new ArrayList<>(solutions);
      this.negativeSolutions = new ArrayList<>(solutions);
      this.solIndex = -nSolutions - 1;
    }

    @Override
    public boolean hasNext() {
      return true;
    }

    @Override
    public XYPair next() {
      solIndex++;

      if (solIndex == 0) {
        advanceSolutions();
      } else if (solIndex >= nSolutions) {
        solIndex = -nSolutions;
      }

      return solIndex >= 0 ? positiveSolutions.get(solIndex)
          : negativeSolutions.get(solIndex + nSolutions);
    }

    private void advanceSolutions() {
      for (ListIterator<XYPair> it = positiveSolutions.listIterator(); it.hasNext();) {
        XYPair sol = it.next();
        BigInteger x = sol.x;
        BigInteger y = sol.y;

        BigInteger nextX = v11.multiply(x).add(v12.multiply(y)).add(K1);
        BigInteger nextY = v21.multiply(x).add(v22.multiply(y)).add(K2);

        it.set(new XYPair(nextX, nextY));
      }

      for (ListIterator<XYPair> it = negativeSolutions.listIterator(); it.hasNext();) {
        XYPair sol = it.next();
        BigInteger x = sol.x;
        BigInteger y = sol.y;

        BigInteger prevX = v22.multiply(x).subtract(v12.multiply(y)).add(K1neg);
        BigInteger prevY = v21.negate().multiply(x).add(v11.multiply(y)).add(K2neg);

        it.set(new XYPair(prevX, prevY));
      }
    }
  }
}
