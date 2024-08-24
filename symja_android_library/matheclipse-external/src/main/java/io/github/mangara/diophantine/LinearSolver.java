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
package io.github.mangara.diophantine;

import io.github.mangara.diophantine.iterators.IntegerIterator;
import io.github.mangara.diophantine.iterators.MappingIterator;
import io.github.mangara.diophantine.iterators.XYIterator;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Solves linear Diophantine equations in two variables.
 * <p>
 * The method is based on https://www.alpertron.com.ar/METHODS.HTM#Linear
 */
public class LinearSolver {

  /**
   * Solves the linear Diophantine equation d x + e y + f = 0.
   *
   * @param d
   * @param e
   * @param f
   * @return an iterator over all integer solutions (x, y)
   */
  public static Iterator<XYPair> solve(long d, long e, long f) {
    return solve(BigInteger.valueOf(d), BigInteger.valueOf(e), BigInteger.valueOf(f));
  }

  /**
   * Solves the linear Diophantine equation d x + e y + f = 0.
   *
   * @param d
   * @param e
   * @param f
   * @return an iterator over all integer solutions (x, y)
   */
  public static Iterator<XYPair> solve(BigInteger d, BigInteger e, BigInteger f) {
    if (d.signum() == 0 && e.signum() == 0) {
      return solveTrivial(f);
    } else if (d.signum() == 0) {
      return solveSingle(e, f, true);
    } else if (e.signum() == 0) {
      return solveSingle(d, f, false);
    } else {
      return solveGeneral(d, e, f);
    }
  }

  private static Iterator<XYPair> solveTrivial(BigInteger f) {
    if (f.signum() != 0) {
      return Collections.emptyIterator();
    }

    return new XYIterator();
  }

  /**
   * Solves the linear Diophantine equations g x + f = 0 or g y + f = 0.
   * <p>
   * If arbitraryX is true, the equation solved is g y + f = 0 and x ranges over all integers.
   * Otherwise, it is g x + f = 0, with y ranging over all integers.
   *
   * @param g
   * @param f
   * @param arbitraryX
   * @return an iterator over all integer solutions (x, y)
   */
  private static Iterator<XYPair> solveSingle(BigInteger g, BigInteger f, boolean arbitraryX) {
    if (f.mod(g.abs()).signum() != 0) {
      return Collections.emptyIterator();
    }

    BigInteger val = f.negate().divide(g);

    Function<BigInteger, XYPair> map;
    if (arbitraryX) {
      map = k -> new XYPair(k, val);
    } else {
      map = k -> new XYPair(val, k);
    }

    return new MappingIterator<>(new IntegerIterator(), map);
  }

  // Pre: d != 0 && e != 0
  private static Iterator<XYPair> solveGeneral(BigInteger d, BigInteger e, BigInteger f) {
    Eq reduced = reduce(d, e, f);

    if (reduced == null) {
      return Collections.emptyIterator();
    } else {
      return solveReduced(reduced);
    }
  }

  private static Eq reduce(BigInteger d, BigInteger e, BigInteger f) {
    BigInteger gcd = d.gcd(e).abs();

    if (f.mod(gcd).signum() != 0) {
      // No solutions, as d x + e y will always be a multiple of gcd for integer x and y
      return null;
    }

    return new Eq(d.divide(gcd), e.divide(gcd), f.divide(gcd));
  }

  // Pre: d != 0 && e != 0 && d and e are co-prime
  private static Iterator<XYPair> solveReduced(Eq eq) {
    XYPair solution = findAnySolution(eq);

    final BigInteger dx = eq.e;
    final BigInteger dy = eq.d.negate();

    return new MappingIterator<>(new IntegerIterator(),
        (k) -> new XYPair(solution.x.add(dx.multiply(k)), solution.y.add(dy.multiply(k))));
  }

  private static XYPair findAnySolution(Eq eq) {
    // Run the extended Euclidean algorithm (
    // https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm )
    BigInteger prevR = eq.d;
    BigInteger r = eq.e;
    BigInteger prevS = BigInteger.ONE;
    BigInteger s = BigInteger.ZERO;
    BigInteger prevT = BigInteger.ZERO;
    BigInteger t = BigInteger.ONE;

    while (r.signum() != 0) {
      BigInteger quotient = prevR.divide(r);
      BigInteger tempR = r;
      BigInteger tempS = s;
      BigInteger tempT = t;

      r = prevR.subtract(quotient.multiply(r));
      s = prevS.subtract(quotient.multiply(s));
      t = prevT.subtract(quotient.multiply(t));

      prevR = tempR;
      prevS = tempS;
      prevT = tempT;
    }

    // Results are in prevR (which is the gcd = +/- 1), prevS, and prevT
    // Thus, d * prevS + e * prevT = +/- 1
    // We want d * x + e * y = -f, so we need to multiply by f or -f, depending on the sign of prevR
    BigInteger factor = eq.f.negate().multiply(prevR);
    BigInteger x = factor.multiply(prevS); // -/+ f * prevS
    BigInteger y = factor.multiply(prevT); // -/+ f * prevT

    return new XYPair(x, y);
  }

  private static class Eq {

    BigInteger d, e, f;

    Eq(BigInteger d, BigInteger e, BigInteger f) {
      this.d = d;
      this.e = e;
      this.f = f;
    }
  }
}
