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
 * Tests of quadratic residue computations modulo general m.
 *
 * @author Tilman Neumann
 */
public class QuadraticResiduesMod2PowNTest01 {

  private static final Logger LOG = Logger.getLogger(QuadraticResiduesMod2PowNTest01.class);

  /**
   * Test.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();
    TreeSet<Long> quadraticResiduesMod100 = QuadraticResidues.getQuadraticResidues(100);
    LOG.info(
        "m = 100 has "
            + quadraticResiduesMod100.size()
            + " quadratic residues: "
            + quadraticResiduesMod100);

    ArrayList<Integer> counts = new ArrayList<Integer>();
    ArrayList<Integer> evenCounts = new ArrayList<Integer>();

    for (int n = 0; n < 20; n++) {
      int m = 1 << n;

      TreeSet<Long> quadraticResiduesMod2PowN = QuadraticResidues.getQuadraticResidues(m);
      LOG.info(
          "m = "
              + m
              + " has "
              + quadraticResiduesMod2PowN.size()
              + " quadratic residues: "
              + quadraticResiduesMod2PowN);
      counts.add(quadraticResiduesMod2PowN.size());

      TreeSet<Long> evenQuadraticResiduesMod2PowN = QuadraticResidues.getEvenQuadraticResidues(m);
      LOG.info(
          "m = "
              + m
              + " has "
              + evenQuadraticResiduesMod2PowN.size()
              + " 'even' quadratic residues: "
              + evenQuadraticResiduesMod2PowN);
      evenCounts.add(evenQuadraticResiduesMod2PowN.size());
    }

    LOG.info("counts = " + counts);
    // A023105(n) = 1, 2, 2, 3, 4, 7, 12, 23, 44, 87, 172, 343, ...
    LOG.info("evenCounts = " + evenCounts);
    // a(n) = {1, 1} + A023105(n-2) = 1, 1, 1, 2, 2, 3, 4, 7, 12, 23, 44, 87, 172, 343, 684, 1367,
    // 2732, 5463, 10924, 21847
  }
}
