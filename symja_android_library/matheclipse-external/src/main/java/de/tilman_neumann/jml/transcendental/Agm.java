/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.transcendental;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.precision.Magnitude;
import de.tilman_neumann.jml.precision.Scale;
import de.tilman_neumann.jml.roots.SqrtInt;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.TimeUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

public class Agm {
  private static final Logger LOG = Logger.getLogger(Agm.class);

  private static final boolean DEBUG = false;

  /**
   * Arithmetic-geometric mean of a and b. The core is computed in BigIntegers, which is much faster
   * than a BigDecimal-core.
   *
   * <p>agm(a, b) will be pretty slow if |ln(|a/b|)| is large, because then the initial sqrt guess
   * is not good enough.
   *
   * @param a
   * @param b
   * @param outScale desired precision in after-floating point digits
   * @return agm(a, b)
   */
  public static BigDecimal agm /*_intCore*/(BigDecimal a, BigDecimal b, Scale outScale) {
    // LOG.debug("a.scale = " + a.scale() + ", b.scale = " + b.scale() + ", outScale = " +
    // outScale);
    int outMag = getResultMagnitude(a, b);
    // precision must be positive! Otherwise we could get x=0 or y=0 and very slow convergence.
    int outPrecision =
        Math.max(2, outMag + outScale.digits() + 2); // 2 extra digits for precise rounding
    int outPrecisionBits = Magnitude.decimalToBinary(outPrecision);
    if (DEBUG) LOG.debug("outMag = " + outMag + ", outPrecision=" + outPrecision);

    // Find the smallest scale > max(a.scale(), b.scale()) that allows us to extract integers x, y
    // from the
    // unscaled values of a, b satisfying x/y = a/b
    int aScale0 = a.scale();
    int aMag = Magnitude.of(a);
    int aPrecision = aMag + aScale0;
    int aScale1 = aScale0 + outPrecision - aPrecision;
    if (DEBUG)
      LOG.debug(
          "aScale0 = "
              + aScale0
              + ", aMag = "
              + aMag
              + ", aPrecision="
              + aPrecision
              + ", aScale1 = "
              + aScale1);
    int bScale0 = b.scale();
    int bMag = Magnitude.of(b);
    int bPrecision = bMag + bScale0;
    int bScale1 = bScale0 + outPrecision - bPrecision;
    if (DEBUG)
      LOG.debug(
          "bScale0 = "
              + bScale0
              + ", bMag = "
              + bMag
              + ", bPrecision="
              + bPrecision
              + ", bScale1 = "
              + bScale1);
    int internalScale = Math.max(aScale1, bScale1);
    if (DEBUG) LOG.debug("internalScale = " + internalScale);

    // Set the scale of a, b to the computed scale and extract x, y
    BigInteger x = a.setScale(internalScale, RoundingMode.HALF_EVEN).unscaledValue();
    BigInteger y = b.setScale(internalScale, RoundingMode.HALF_EVEN).unscaledValue();
    BigInteger err;
    int totalShifts = 0;

    // The core loop is computed in integers x, y, which is much faster than a float loop in a, b.
    // The core loop is computed with a few extra precision bits.
    do {
      // LOG.debug("x=" + x + ", y=" + y);
      BigInteger t = x.add(y).shiftRight(1);
      y = SqrtInt.iSqrt(x.multiply(y))[0];
      x = t;

      int minBits = Math.min(x.bitLength(), y.bitLength());
      int shifts = Math.max(0, minBits - outPrecisionBits - 5);
      x = x.shiftRight(shifts);
      y = y.shiftRight(shifts);
      totalShifts += shifts;

      // Check stopping criterion:
      err = y.subtract(x).abs();
      // LOG.debug("err=" + err);
    } while (err.compareTo(I_1) > 0);
    // LOG.debug("x=" + x + ", y=" + y);

    // Assign output, remove extra digits again
    return new BigDecimal(y.shiftLeft(totalShifts), internalScale)
        .setScale(outScale.digits(), RoundingMode.HALF_EVEN);
  }

  /**
   * Computes an estimate of the size of agm(a, b) in decimal digits.
   *
   * @param a
   * @param b
   * @return estimated magnitude of agm(a, b)
   */
  public static int getResultMagnitude(BigDecimal a, BigDecimal b) {
    BigDecimal aAbs = a.abs();
    BigDecimal bAbs = b.abs();
    BigDecimal low, high;
    if (aAbs.compareTo(bAbs) > 0) {
      low = b;
      high = a;
    } else {
      low = a;
      high = b;
    }
    // agm ~ high / ln(high/low)
    // mag(agm) ~ log10(high / ln(high/low)) = log10(high) - log10(ln(high/low))
    // = mag(high) - mag(ln(high) - ln(low)) = mag(high) - mag(mag(high)*ln(10) - mag(low)*ln(10))
    int highMag = Magnitude.of(high);
    int lowMag = Magnitude.of(low);
    int magDiff = highMag - lowMag;
    return magDiff > 0 ? highMag - Magnitude.of(magDiff * Math.log(10.0)) : highMag;
  }

  // Test inputs:
  // agm(1,2,100), agm(1,2,110), agm(1,2,120), ...
  // agm(1, 0.00000000000000000001, 100), ...
  public static void main(String[] args) {
    ConfigUtil.initProject();
    while (true) {
      String input;
      BigDecimal a, b;
      Scale maxScale;
      try {
        LOG.info("Insert <a> <b> <scale>:");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line = in.readLine();
        input = line.trim();
        // LOG.debug("input = >" + input + "<");
        StringTokenizer tok = new StringTokenizer(input);
        a = new BigDecimal(tok.nextToken());
        b = new BigDecimal(tok.nextToken());
        maxScale = Scale.valueOf(Integer.parseInt(tok.nextToken()));
      } catch (Exception e) {
        LOG.error("Error occuring on input: " + e.getMessage());
        continue;
      }

      long t0, t1;
      BigDecimal agm;

      t0 = System.currentTimeMillis();
      for (Scale scale = Scale.valueOf(2); scale.compareTo(maxScale) <= 0; scale = scale.add(1)) {
        // long t2 = System.currentTimeMillis();
        agm = agm /*_intCore*/(a, b, scale);
        LOG.debug("agm_intCore(" + a + ", " + b + ", " + scale + ") = " + agm);
        // long t3 = System.currentTimeMillis();
        // LOG.debug("agm_intCore took " + TimeUtil.timeDiffStr(t2,t3));
      }
      t1 = System.currentTimeMillis();
      LOG.debug("Time of agm_intCore: " + TimeUtil.timeDiffStr(t0, t1));
    }
  }
}
