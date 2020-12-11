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
package de.tilman_neumann.jml.partitions;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.combinatorics.Factorial;
import de.tilman_neumann.util.ConfigUtil;

public class MpiPartitionGeneratorTest {

  private static final Logger LOG = Logger.getLogger(MpiPartitionGeneratorTest.class);

  /**
   * prints the number of essentially different factorizations of n, n=0,1,... A001055 (n=1...) = 1,
   * 1, 1, 2, 1, 2, 1, 3, 2, 2, 1, 4, 1, 2, 2, 5, 1, 4, 1, 4, 2, 2, 1, 7, 2, 2, 3, 4, 1, ...
   */
  private static void printNumberOfFactorizations() {
    for (int n = 0; n <= 100; n++) {
      BigInteger bigN = BigInteger.valueOf(n);
      long numberOfFactorizations = MpiPartitionGenerator.numberOfFactorizationsOf(bigN);
      LOG.info(bigN + " can be factored in " + numberOfFactorizations + " different ways");
    }
  }

  /**
   * prints record numbers of essentially different factorizations of n, n=0,1,... A033833 = 1, 4,
   * 8, 12, 16, 24, 36, 48, 72, 96, 120, 144, 192, 216, 240, 288, 360, 432, 480, 576, 720, 960,
   * 1080, 1152, 1440, 2160, 2880, 4320, 5040, 5760, 7200, 8640, 10080, 11520, ...
   */
  private static void printNumberOfFactorizationsRecords() {
    long record = 0;
    for (BigInteger n = I_1; ; n = n.add(I_1)) {
      long numberOfFactorizations = MpiPartitionGenerator.numberOfFactorizationsOf(n);
      if (numberOfFactorizations > record) { // same value does not count as new record
        LOG.info(n + " can be factored in " + numberOfFactorizations + " different ways");
        record = numberOfFactorizations;
      }
    }
  }

  /** prints record numbers of essentially different factorizations of n per bit. */
  // Note: factorizations per n is too restrictive, small n (like 1) would be unbeatable!
  private static void printNumberOfFactorizationsRecordsPerBit() {
    // long recordFactorizations = 0;
    // double recordBitLength;
    double recordRatio = 0;
    for (BigInteger n = I_1; ; n = n.add(I_1)) {
      long numberOfFactorizations = MpiPartitionGenerator.numberOfFactorizationsOf(n);
      double bits = n.equals(I_1) ? 1.0 : Math.log(n.doubleValue()) / Math.log(2.0); // ld(n)
      double ratio = numberOfFactorizations / bits;
      if (ratio > recordRatio) { // same value does not count as new record
        LOG.info(
            n
                + " ("
                + bits
                + " bit) can be factored in "
                + numberOfFactorizations
                + " different ways -> ratio = "
                + ratio);
        recordRatio = ratio;
        // recordBitLength = bits;
        // recordFactorizations = numberOfFactorizations;
      }
    }
  }

  /**
   * prints the number of essentially different factorizations of n!, n=0,1,... A076716 (n=2...)=
   * 1,2,7,21,98,392,2116,11830,70520,...
   */
  private static void printNumberOfFactorialFactorizations() {
    for (int i = 0; i < 14; i++) {
      long start = System.currentTimeMillis();
      BigInteger factorial = Factorial.factorial(i);
      long numberOfFactorizations = MpiPartitionGenerator.numberOfFactorizationsOf(factorial);
      LOG.info(
          i
              + "! = "
              + factorial
              + " can be factored in "
              + numberOfFactorizations
              + " different ways (computed in "
              + (System.currentTimeMillis() - start)
              + " ms)");
    }
  }

  /**
   * prints partitions of partitions A001970 = 1, 1, 3, 6, 14, 27, 58, 111, 223, 424, 817, 1527,
   * 2870, 5279, 9710, 17622, 31877, 57100, 101887, 180406, 318106, 557453, 972796, 1688797,
   * 2920123, ...
   */
  private static void printHyperPartitions() {
    for (int n = 1; n < 25; n++) {
      long start = System.currentTimeMillis();
      int totalNumberOfPartitions = 0;
      // run over all additive partition of n:
      IntegerPartitionGenerator partgen = new IntegerPartitionGenerator(n);
      while (partgen.hasNext()) {
        int[] flatPartition = partgen.next();
        // partition is in flat form, i.e. a list of all parts.
        // convert this into the multiset form:
        IntegerPartition expPartition = new IntegerPartition(flatPartition);
        // LOG.debug("expPartition from n=" + n + ": " + expPartition);
        // now we have all the multiplicities
        Mpi mpiFromPartition = new Mpi_IntegerArrayImpl(expPartition.values());
        MpiPartitionGenerator mpiPartGen = new MpiPartitionGenerator(mpiFromPartition);
        while (mpiPartGen.hasNext()) {
          mpiPartGen.next();
          totalNumberOfPartitions++;
        }
      }
      LOG.info(
          n
              + " has "
              + totalNumberOfPartitions
              + " hyper partitions! (computed in "
              + (System.currentTimeMillis() - start)
              + " ms)");
    }
  }

  /**
   * Test
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    ConfigUtil.initProject();

    printNumberOfFactorizationsRecordsPerBit();
    printNumberOfFactorizationsRecords();

    printNumberOfFactorizations();
    printNumberOfFactorialFactorizations();
    printHyperPartitions();
  }
}
