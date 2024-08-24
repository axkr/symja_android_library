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

package io.github.mangara.diophantine.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import io.github.mangara.diophantine.XYPair;

public class ChineseRemainder {

  /**
   * Finds the least positive x that matches
   * 
   * x = r_1 mod n_1 x = r_2 mod n_2 ... x = r_k mod n_k
   * 
   * where (r_i, n_i) are the pairs in remainders.
   * 
   * The pre-conditions are that all the n_i are relatively prime, and {@literal 0 <= r_i < n_i}.
   * 
   * @param remainders
   * @return
   */
  public static BigInteger solveSystem(List<XYPair> remainders) {
    List<XYPair> equations = new ArrayList<>(remainders);

    while (equations.size() > 1) {
      List<XYPair> nextEquations = new ArrayList<>(equations.size() / 2 + 1);

      // Sort the list by divisors
      equations.sort((eq1, eq2) -> eq1.y.compareTo(eq2.y));

      for (int i = 0; i < equations.size() / 2; i++) {
        // Match up a small and a large divisor
        XYPair eq1 = equations.get(i);
        XYPair eq2 = equations.get(equations.size() - i - 1);

        // Reduce to one new equation
        nextEquations.add(reduce(eq1, eq2));
      }

      if (equations.size() % 2 == 1) {
        nextEquations.add(equations.get(equations.size() / 2));
      }

      equations = nextEquations;
    }

    return equations.get(0).x;
  }

  private static XYPair reduce(XYPair eq1, XYPair eq2) {
    XYPair bezoutPair = ExtendedEuclidean.gcdPair(eq1.y, eq2.y);

    // x = a_1 m_2 n_2 + a_2 m_1 n_1 (mod n_1 n_2)
    BigInteger newRemainder = eq1.x.multiply(bezoutPair.y).multiply(eq2.y)
        .add(eq2.x.multiply(bezoutPair.x).multiply(eq1.y));
    BigInteger newDivisor = eq1.y.multiply(eq2.y);

    while (newRemainder.compareTo(BigInteger.ZERO) < 0) {
      newRemainder = newRemainder.add(newDivisor);
    }
    while (newRemainder.compareTo(newDivisor) > 0) {
      newRemainder = newRemainder.subtract(newDivisor);
    }

    return new XYPair(newRemainder, newDivisor);
  }

}
