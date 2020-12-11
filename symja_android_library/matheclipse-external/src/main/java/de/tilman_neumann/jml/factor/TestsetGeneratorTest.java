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
package de.tilman_neumann.jml.factor;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.TimeUtil;
import de.tilman_neumann.util.Timer;

public class TestsetGeneratorTest {
  private static final Logger LOG = Logger.getLogger(TestsetGeneratorTest.class);

  public static void main(String[] args) {
    ConfigUtil.initProject();
    Timer timer = new Timer();
    int nCount = 100;
    for (int bits = 20; ; bits += 10) {
      long start = timer.capture();
      BigInteger[] testNumbers =
          TestsetGenerator.generate(nCount, bits, TestNumberNature.MODERATE_SEMIPRIMES);
      long end = timer.capture();
      // Collect the true
      Map<Integer, Integer> sizeCounts = new TreeMap<>();
      for (BigInteger num : testNumbers) {
        int bitlen = num.bitLength();
        Integer count = sizeCounts.get(bitlen);
        count = (count == null) ? Integer.valueOf(1) : count.intValue() + 1;
        sizeCounts.put(bitlen, count);
      }
      String generatedBitLens = "";
      for (int bitlen : sizeCounts.keySet()) {
        generatedBitLens += sizeCounts.get(bitlen) + "x" + bitlen + ", ";
      }
      generatedBitLens = generatedBitLens.substring(0, generatedBitLens.length() - 2);
      LOG.info(
          "Requesting "
              + nCount
              + " "
              + bits
              + "-numbers took "
              + TimeUtil.timeDiffStr(start, end)
              + " ms and generated the following bit lengths: "
              + generatedBitLens);
      // Roughly 1/3 of generated numbers are one bit smaller than requested. No big problem though.
    }
  }
}
