/*
 * SpectralColor.java  -  Star colors
 * Copyright (C) 2011-2026 Brian Simpson
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
 * Modified 2026 for Symja: the star color table and the spectral class lookup of
 * com.nvastro.nvj.StarDB (Night Vision 5.5) ported to package org.matheclipse.astro.meeus;
 * java.awt.Color was replaced by int RGB triples so the package has no AWT dependency.
 */

package org.matheclipse.astro.meeus;

/**
 * Star colors by spectral class.
 *
 * <p>
 * The following resource was used for O,B,A,F,G,K,M star colors
 * http://www.vendian.org/mncharity/dir3/starcolor/ by Mitchell Charity
 *
 * @author Brian Simpson
 */
public final class SpectralColor {

  // @formatter:off
  private static final int[][] Colors = {
      {155, 176, 255}, // O "Oh,"
      {170, 191, 255}, // B "Be"
      {202, 215, 255}, // A "A"
      {248, 247, 255}, // F "Fine"
      {255, 244, 234}, // G "Girl,"
      {255, 210, 161}, // K "Kiss"
      {255, 204, 111}, // M "Me,"
      {255, 190,  80}, // R "Right"
      {255, 150,  60}, // N "Now"
      {255, 120,  50}, // S "Smack"
      {255, 170,  70}, // C Carbon
      {225, 225, 225}  // X Spectrum not specified
  };
  // @formatter:on

  /** The index returned by {@link #index} when no spectrum is specified. */
  public static final int UNSPECIFIED = Colors.length - 1;

  private SpectralColor() {}

  /**
   * The color index of a spectral type.
   *
   * @param spect a spectral type such as <code>"A1"</code> or <code>"K0"</code>; may be empty
   * @return an index for {@link #rgb(int)}
   */
  public static int index(CharSequence spect) {
    if (spect == null || spect.length() == 0) {
      return UNSPECIFIED;
    }
    return index(spect.charAt(0));
  }

  /**
   * The color index of the first letter of a spectral type.
   *
   * @return an index for {@link #rgb(int)}
   */
  public static int index(char letter) {
    switch (letter) {
      case 'O':
        return 0;
      case 'B':
        return 1;
      case 'A':
        return 2;
      case 'F':
        return 3;
      case 'G':
        return 4;
      case 'K':
        return 5;
      case 'M':
        return 6;
      case 'R':
        return 7;
      case 'N':
        return 8;
      case 'S':
        return 9;
      case 'C':
        return 10;
      default:
        return UNSPECIFIED; // Shouldn't happen
    }
  }

  /**
   * @param index a value returned by {@link #index}
   * @return a new <code>{red, green, blue}</code> array with components 0..255
   */
  public static int[] rgb(int index) {
    return Colors[index].clone();
  }

  /** The color of a spectral type as <code>{red, green, blue}</code> with components 0..255. */
  public static int[] rgb(CharSequence spect) {
    return rgb(index(spect));
  }
}
