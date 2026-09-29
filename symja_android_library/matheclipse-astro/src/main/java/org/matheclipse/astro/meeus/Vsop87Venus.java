/*
 * Vsop87Venus.java  -  Abridged VSOP87 periodic terms for Venus
 * Copyright (C) 2011-2024 Brian Simpson
 * This file is part of Night Vision.
 *
 * Night Vision is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Night Vision is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Night Vision.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Modified 2026 for Symja: the "venus" table of com.nvastro.nvj.NearSkyDB (Night Vision 5.5)
 * moved to its own class in package org.matheclipse.astro.meeus, and the lbr objects flattened
 * into a double array. The numbers are unchanged. This file is generated from NearSkyDB.java.
 */

package org.matheclipse.astro.meeus;

/**
 * Periodic terms of the heliocentric longitude, latitude and radius vector of
 * Venus,
 * from Meeus, <i>Astronomical Algorithms</i> 2nd ed., Appendix III.
 *
 * <p>
 * <code>TERMS[coordinate][power]</code> holds triples <code>A, B, C</code> of the term
 * <code>A cos(B + C t)</code>, with <code>t</code> in Julian millennia from J2000.0 and
 * <code>A</code> in units of 1e-8. See {@link Vsop87#heliocentric}.
 */
final class Vsop87Venus {

  private Vsop87Venus() {}

  static final double[][][] TERMS = {
      { // L
          { // L0
              317614667, 0, 0,
              1353968, 5.5931332, 10213.2855462,
              89892, 5.30650, 20426.57109,
              5477, 4.4163, 7860.4194,
              3456, 2.6996, 11790.6291,
              2372, 2.9938, 3930.2097,
              1664, 4.2502, 1577.3435,
              1438, 4.1575, 9683.5946,
              1317, 5.1867, 26.2983,
              1201, 6.1536, 30639.8566,
              769, 0.816, 9437.763,
              761, 1.950, 529.691,
              708, 1.065, 775.523,
              585, 3.998, 191.448,
              500, 4.123, 15720.839,
              429, 3.586, 19367.189,
              327, 5.677, 5507.553,
              326, 4.591, 10404.734,
              232, 3.163, 9153.904,
              180, 4.653, 1109.379,
              155, 5.570, 19651.048,
              128, 4.226, 20.775,
              128, 0.962, 5661.332,
              106, 1.537, 801.821,
          },
          { // L1
              1021352943053L, 0, 0,
              95708, 2.46424, 10213.28555,
              14445, 0.51625, 20426.57109,
              213, 1.795, 30639.857,
              174, 2.655, 26.298,
              152, 6.106, 1577.344,
              82, 5.70, 191.45,
              70, 2.68, 9437.76,
              52, 3.60, 775.52,
              38, 1.03, 529.69,
              30, 1.25, 5507.55,
              25, 6.11, 10404.73,
          },
          { // L2
              54127, 0, 0,
              3891, 0.3451, 10213.2855,
              1338, 2.0201, 20426.5711,
              24, 2.05, 26.30,
              19, 3.54, 30639.86,
              10, 3.97, 775.52,
              7, 1.52, 1577.34,
              6, 1.00, 191.45,
          },
          { // L3
              136, 4.804, 10213.286,
              78, 3.67, 20426.57,
              26, 0, 0,
          },
          { // L4
              114, 3.1416, 0,
              3, 5.21, 20426.57,
              2, 2.51, 10213.29,
          },
          { // L5
              1, 3.14, 0,
          },
      },
      { // B
          { // B0
              5923638, 0.2670278, 10213.2855462,
              40108, 1.14737, 20426.57109,
              32815, 3.14159, 0,
              1011, 1.0895, 30639.8566,
              149, 6.254, 18073.705,
              138, 0.860, 1577.344,
              130, 3.672, 9437.763,
              120, 3.705, 2352.866,
              108, 4.539, 22003.915,
          },
          { // B1
              513348, 1.803643, 10213.285546,
              4380, 3.3862, 20426.5711,
              199, 0, 0,
              197, 2.530, 30639.857,
          },
          { // B2
              22378, 3.38509, 10213.28555,
              282, 0, 0,
              173, 5.256, 20426.571,
              27, 3.87, 30639.86,
          },
          { // B3
              647, 4.992, 10213.286,
              20, 3.14, 0,
              6, 0.77, 20426.57,
              3, 5.44, 30639.86,
          },
          { // B4
              14, 0.32, 10213.29,
          },
      },
      { // R
          { // R0
              72334821, 0, 0,
              489824, 4.021518, 10213.285546,
              1658, 4.9021, 20426.5711,
              1632, 2.8455, 7860.4194,
              1378, 1.1285, 11790.6291,
              498, 2.587, 9683.595,
              374, 1.423, 3930.210,
              264, 5.529, 9437.763,
              237, 2.551, 15720.839,
              222, 2.013, 19367.189,
              126, 2.728, 1577.344,
              119, 3.020, 10404.734,
          },
          { // R1
              34551, 0.89199, 10213.28555,
              234, 1.772, 20426.571,
              234, 3.142, 0,
          },
          { // R2
              1407, 5.0637, 10213.2855,
              16, 5.47, 20426.57,
              13, 0, 0,
          },
          { // R3
              50, 3.22, 10213.29,
          },
          { // R4
              1, 0.92, 10213.29,
          },
      },
  };
}
