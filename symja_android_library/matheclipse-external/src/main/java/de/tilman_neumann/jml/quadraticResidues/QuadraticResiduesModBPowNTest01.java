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
package de.tilman_neumann.jml.quadraticResidues;

import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.tilman_neumann.util.ConfigUtil;

/**
 * Tests of quadratic residue computations modulo P^n.
 *
 * @author Tilman Neumann
 */
public class QuadraticResiduesModBPowNTest01 {

  private static final Logger LOG = Logger.getLogger(QuadraticResiduesModBPowNTest01.class);

  private static final int P = 23;

  private static final boolean SHOW_ELEMENTS = false;

  private static ArrayList<Long> powerUp(TreeSet<Long> lastSet, long lastM) {
    ArrayList<Long> nextList = new ArrayList<>(lastSet); // copy
    for (int i = 1; i < P; i++) {
      long offset = i * lastM;
      for (long elem : lastSet) {
        nextList.add(elem + offset);
      }
    }
    return nextList;
  }

  /**
   * Test.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();

    ArrayList<Integer> counts = new ArrayList<Integer>();
    ArrayList<Integer> removedCounts = new ArrayList<Integer>();

    // result for i=0
    TreeSet<Long> lastSet = new TreeSet<>();
    lastSet.add(0L);

    for (int n = 1; n < 5; n++) {
      long m = (long) Math.pow(P, n);
      long lastM = m / P;

      // last set contains entries between 0 and lastM. next we want entries between 0 and m
      ArrayList<Long> lastPoweredUp = powerUp(lastSet, lastM);
      if (SHOW_ELEMENTS) LOG.debug("n = " + n + ": lastPoweredUp = " + lastPoweredUp);

      TreeSet<Long> nextSet = QuadraticResidues.getQuadraticResidues(m);

      // this is what has been removed in nextSet from the power-up of lastSet
      lastPoweredUp.removeAll(nextSet);
      LOG.info(
          "n = "
              + n
              + ": After removing "
              + lastPoweredUp.size()
              + " elements "
              + lastPoweredUp
              + " from last set powered up, there are "
              + nextSet.size()
              + " quadratic residues mod "
              + P
              + "^"
              + n
              + (SHOW_ELEMENTS ? ": " + nextSet : ""));

      for (long removed : lastPoweredUp) {
        long quotient = removed / lastM;
        boolean isExact = quotient * lastM == removed;
        LOG.info(
            "lastM = "
                + lastM
                + ", removed = "
                + removed
                + ", quotient = "
                + quotient
                + ", isExact = "
                + isExact);
      }

      counts.add(nextSet.size());
      removedCounts.add(lastPoweredUp.size());

      // prepare next round
      lastSet = nextSet;
    }

    LOG.info("");
    LOG.info("counts = " + counts);
    // P = 2: A023105(n) = 1, 2, 2, 3, 4, 7, 12, 23, 44, 87, 172, 343, ...
    // P = 3: A039300(n) = 1, 2, 4, 11, 31, 92, 274, 821, 2461, 7382, 22144, 66431, ...
    // P = 5: A039302(n) = 1, 3, 11, 53, 261, 1303, 6511, 32553, 162761, ...
    // ...

    LOG.info("removedCounts = " + removedCounts);
  }
}
