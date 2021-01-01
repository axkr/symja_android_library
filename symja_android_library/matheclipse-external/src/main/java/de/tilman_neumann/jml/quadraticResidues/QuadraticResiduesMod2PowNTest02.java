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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import de.tilman_neumann.util.ConfigUtil;

/**
 * Tests of quadratic residue computations modulo 2^n.
 *
 * @author Tilman Neumann
 */
public class QuadraticResiduesMod2PowNTest02 {

  private static final Logger LOG = Logger.getLogger(QuadraticResiduesMod2PowNTest02.class);

  private static final boolean SHOW_ELEMENTS = false;

  /**
   * Test.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();

    ArrayList<Integer> counts_v0 = new ArrayList<Integer>();
    ArrayList<Integer> counts_v1 = new ArrayList<Integer>();
    ArrayList<Integer> counts_v2 = new ArrayList<Integer>();
    ArrayList<Integer> counts_v3 = new ArrayList<Integer>();
    ArrayList<Integer> counts_v4 = new ArrayList<Integer>();

    for (int n = 0; n < 30; n++) {
      long t0, t1;

      t0 = System.currentTimeMillis();
      List<BigInteger> quadraticResiduesMod2PowN_v0 =
          QuadraticResiduesMod2PowN.getQuadraticResiduesMod2PowN_testAll_big(n);
      t1 = System.currentTimeMillis();
      LOG.info(
          "v0: n = "
              + n
              + " has "
              + quadraticResiduesMod2PowN_v0.size()
              + " quadratic residues"
              + (SHOW_ELEMENTS ? ": " + quadraticResiduesMod2PowN_v0 : "")
              + " -- duration = "
              + (t1 - t0)
              + "ms");
      counts_v0.add(quadraticResiduesMod2PowN_v0.size());

      t0 = System.currentTimeMillis();
      List<Long> quadraticResiduesMod2PowN_v1 =
          QuadraticResiduesMod2PowN.getQuadraticResiduesMod2PowN_testAll(n);
      t1 = System.currentTimeMillis();
      LOG.info(
          "v1: n = "
              + n
              + " has "
              + quadraticResiduesMod2PowN_v1.size()
              + " quadratic residues"
              + (SHOW_ELEMENTS ? ": " + quadraticResiduesMod2PowN_v1 : "")
              + " -- duration = "
              + (t1 - t0)
              + "ms");
      counts_v1.add(quadraticResiduesMod2PowN_v1.size());

      t0 = System.currentTimeMillis();
      List<Long> quadraticResiduesMod2PowN_v2 =
          QuadraticResiduesMod2PowN.getQuadraticResiduesMod2PowN_testAll_v2(n);
      t1 = System.currentTimeMillis();
      LOG.info(
          "v2: n = "
              + n
              + " has "
              + quadraticResiduesMod2PowN_v2.size()
              + " quadratic residues"
              + (SHOW_ELEMENTS ? ": " + quadraticResiduesMod2PowN_v2 : "")
              + " -- duration = "
              + (t1 - t0)
              + "ms");
      counts_v2.add(quadraticResiduesMod2PowN_v2.size());

      t0 = System.currentTimeMillis();
      List<Long> quadraticResiduesMod2PowN_v3 =
          QuadraticResiduesMod2PowN.getQuadraticResiduesMod2PowN(n);
      t1 = System.currentTimeMillis();
      LOG.info(
          "v3: n = "
              + n
              + " has "
              + quadraticResiduesMod2PowN_v3.size()
              + " quadratic residues"
              + (SHOW_ELEMENTS ? ": " + quadraticResiduesMod2PowN_v3 : "")
              + " -- duration = "
              + (t1 - t0)
              + "ms");
      counts_v3.add(quadraticResiduesMod2PowN_v3.size());

      t0 = System.currentTimeMillis();
      long[] array = new long[((1 << n) / 6) + 6];
      int count = QuadraticResiduesMod2PowN.getQuadraticResiduesMod2PowN(n, array);
      t1 = System.currentTimeMillis();
      LOG.info(
          "v4: n = "
              + n
              + " has "
              + count
              + " quadratic residues"
              + (SHOW_ELEMENTS ? ": " + Arrays.toString(array) : "")
              + " -- duration = "
              + (t1 - t0)
              + "ms");
      counts_v4.add(count);
    }

    // A023105(n) = 1, 2, 2, 3, 4, 7, 12, 23, 44, 87, 172, 343, 684, 1367, 2732, 5463, 10924, 21847,
    // 43692, 87383, ...
    LOG.info("v0 counts = " + counts_v0);
    LOG.info("v1 counts = " + counts_v1);
    LOG.info("v2 counts = " + counts_v2);
    LOG.info("v3 counts = " + counts_v3);
    LOG.info("v4 counts = " + counts_v4);
  }
}
