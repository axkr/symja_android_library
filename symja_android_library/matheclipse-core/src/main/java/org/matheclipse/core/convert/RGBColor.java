package org.matheclipse.core.convert;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.Serializable;

/**
 * The RGBColor class defines colors in the default sRGB color space or in the specified ColorSpace.
 * Every RGBColor contains alpha value. The alpha value defines the transparency of a color and can
 * be represented by a float value in the range 0.0 - 1.0 or 0 - 255.
 *
 * <p>
 * Copied from Apache harmony project sources and modified for Symja
 */
public class RGBColor implements Serializable {
  /** */
  private static final long serialVersionUID = 9105970624690865276L;
  /*
   * The values of the following colors are based on 1.5 release behavior which can be revealed
   * using the following or similar code: RGBColor c = RGBColor.white; System.out.println(c);
   */
  /** The color white. */
  public static final RGBColor white = new RGBColor(255, 255, 255);
  /** The color white. */
  public static final RGBColor WHITE = white;
  /** The color light gray. */
  public static final RGBColor lightGray = new RGBColor(192, 192, 192);
  /** The color light gray. */
  public static final RGBColor LIGHT_GRAY = lightGray;
  /** The color gray. */
  public static final RGBColor gray = new RGBColor(128, 128, 128);
  /** The color gray. */
  public static final RGBColor GRAY = gray;
  /** The color dark gray. */
  public static final RGBColor darkGray = new RGBColor(64, 64, 64);
  /** The color dark gray. */
  public static final RGBColor DARK_GRAY = darkGray;
  /** The color black. */
  public static final RGBColor black = new RGBColor(0, 0, 0);
  /** The color black. */
  public static final RGBColor BLACK = black;
  /** The color red. */
  public static final RGBColor red = new RGBColor(255, 0, 0);
  /** The color red. */
  public static final RGBColor RED = red;
  /** The color pink. */
  public static final RGBColor pink = new RGBColor(255, 175, 175);
  /** The color pink. */
  public static final RGBColor PINK = pink;
  /** The color orange. */
  public static final RGBColor orange = new RGBColor(255, 200, 0);
  /** The color orange. */
  public static final RGBColor ORANGE = orange;
  /** The color yellow. */
  public static final RGBColor yellow = new RGBColor(255, 255, 0);
  /** The color yellow. */
  public static final RGBColor YELLOW = yellow;
  /** The color green. */
  public static final RGBColor green = new RGBColor(0, 255, 0);
  /** The color green. */
  public static final RGBColor GREEN = green;
  /** The color magenta. */
  public static final RGBColor magenta = new RGBColor(255, 0, 255);
  /** The color magenta. */
  public static final RGBColor MAGENTA = magenta;
  /** The color cyan. */
  public static final RGBColor cyan = new RGBColor(0, 255, 255);
  /** The color cyan. */
  public static final RGBColor CYAN = cyan;
  /** The color blue. */
  public static final RGBColor blue = new RGBColor(0, 0, 255);
  /** The color blue. */
  public static final RGBColor BLUE = blue;
  /** integer RGB value. */
  int value;
  /** Float sRGB value. */
  private float[] frgbvalue;
  /**
   * RGBColor in an arbitrary color space with <code>float</code> components. If null, other value
   * should be used.
   */
  private float fvalue[];
  /** Float alpha value. If frgbvalue is null, this is not valid data. */
  private float falpha;

  /*
   * The value of the SCALE_FACTOR is based on 1.5 release behavior which can be revealed using the
   * following code: RGBColor c = new RGBColor(100, 100, 100); RGBColor bc = c.brighter();
   * System.out.println("Brighter factor: " + ((float)c.getRed())/((float)bc.getRed())); RGBColor dc
   * = c.darker(); System.out.println("Darker factor: " + ((float)dc.getRed())/((float)c.getRed()));
   * The result is the same for brighter and darker methods, so we need only one scale factor for
   * both.
   */
  /** The Constant SCALE_FACTOR. */
  private static final double SCALE_FACTOR = 0.7;
  /** The Constant MIN_SCALABLE. */
  private static final int MIN_SCALABLE = 3; // should increase when
  // multiplied by SCALE_FACTOR

  /**
   * Instantiates a new sRGB color with the specified combined RGBA value consisting of the alpha
   * component in bits 24-31, the red component in bits 16-23, the green component in bits 8-15, and
   * the blue component in bits 0-7. If the hasalpha argument is false, the alpha has default value
   * - 255.
   *
   * @param rgba the RGBA components.
   * @param hasAlpha the alpha parameter is true if alpha bits are valid, false otherwise.
   */
  public RGBColor(int rgba, boolean hasAlpha) {
    if (!hasAlpha) {
      value = rgba | 0xFF000000;
    } else {
      value = rgba;
    }
  }

  /**
   * Instantiates a new color with the specified red, green, blue and alpha components.
   *
   * @param r the red component.
   * @param g the green component.
   * @param b the blue component.
   * @param a the alpha component.
   */
  public RGBColor(int r, int g, int b, int a) {
    if ((r & 0xFF) != r || (g & 0xFF) != g || (b & 0xFF) != b || (a & 0xFF) != a) {
      // awt.109=RGBColor parameter outside of expected range.
      throw new IllegalArgumentException("RGBColor parameter outside of expected range"); //$NON-NLS-1$
    }
    value = b | (g << 8) | (r << 16) | (a << 24);
  }

  /**
   * Instantiates a new opaque sRGB color with the specified red, green, and blue values. The Alpha
   * component is set to the default - 1.0.
   *
   * @param r the red component.
   * @param g the green component.
   * @param b the blue component.
   */
  public RGBColor(int r, int g, int b) {
    if ((r & 0xFF) != r || (g & 0xFF) != g || (b & 0xFF) != b) {
      // awt.109=RGBColor parameter outside of expected range.
      throw new IllegalArgumentException("RGBColor parameter outside of expected range"); //$NON-NLS-1$
    }
    // 0xFF for alpha channel
    value = b | (g << 8) | (r << 16) | 0xFF000000;
  }

  /**
   * Instantiates a new sRGB color with the specified RGB value consisting of the red component in
   * bits 16-23, the green component in bits 8-15, and the blue component in bits 0-7. Alpha has
   * default value - 255.
   *
   * @param rgb the RGB components.
   */
  public RGBColor(int rgb) {
    value = rgb | 0xFF000000;
  }

  /**
   * Instantiates a new color with the specified red, green, blue and alpha components.
   *
   * @param r the red component.
   * @param g the green component.
   * @param b the blue component.
   * @param a the alpha component.
   */
  public RGBColor(float r, float g, float b, float a) {
    this((int) (r * 255 + 0.5), (int) (g * 255 + 0.5), (int) (b * 255 + 0.5),
        (int) (a * 255 + 0.5));
    falpha = a;
    fvalue = new float[3];
    fvalue[0] = r;
    fvalue[1] = g;
    fvalue[2] = b;
    frgbvalue = fvalue;
  }

  /**
   * Instantiates a new color with the specified red, green, and blue components and default alpha
   * value - 1.0.
   *
   * @param r the red component.
   * @param g the green component.
   * @param b the blue component.
   */
  public RGBColor(float r, float g, float b) {
    this(r, g, b, 1.0f);
  }

  /**
   * Returns a string representation of the RGBColor object.
   *
   * @return the string representation of the RGBColor object.
   */
  @Override
  public String toString() {
    /*
     * The format of the string is based on 1.5 release behavior which can be revealed using the
     * following code: RGBColor c = new RGBColor(1, 2, 3); System.out.println(c);
     */
    return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]"; //$NON-NLS-4$
  }

  /**
   * Compares the specified Object to the RGBColor.
   *
   * @param obj the Object to be compared.
   * @return true, if the specified Object is a RGBColor whose value is equal to this RGBColor,
   *         false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof RGBColor) {
      return ((RGBColor) obj).value == this.value;
    }
    return false;
  }

  /**
   * Creates a new RGBColor which is a darker than this RGBColor according to a fixed scale factor.
   *
   * @return the darker RGBColor.
   */
  public RGBColor darker() {
    return new RGBColor((int) (getRed() * SCALE_FACTOR), (int) (getGreen() * SCALE_FACTOR),
        (int) (getBlue() * SCALE_FACTOR));
  }

  /**
   * Creates a new RGBColor which is a brighter than this RGBColor.
   *
   * @return the brighter RGBColor.
   */
  public RGBColor brighter() {
    int r = getRed();
    int b = getBlue();
    int g = getGreen();
    if (r == 0 && b == 0 && g == 0) {
      return new RGBColor(MIN_SCALABLE, MIN_SCALABLE, MIN_SCALABLE);
    }
    if (r < MIN_SCALABLE && r != 0) {
      r = MIN_SCALABLE;
    } else {
      r = (int) (r / SCALE_FACTOR);
      r = (r > 255) ? 255 : r;
    }
    if (b < MIN_SCALABLE && b != 0) {
      b = MIN_SCALABLE;
    } else {
      b = (int) (b / SCALE_FACTOR);
      b = (b > 255) ? 255 : b;
    }
    if (g < MIN_SCALABLE && g != 0) {
      g = MIN_SCALABLE;
    } else {
      g = (int) (g / SCALE_FACTOR);
      g = (g > 255) ? 255 : g;
    }
    return new RGBColor(r, g, b);
  }

  /**
   * Returns a float array containing the color and alpha components of the RGBColor in the default
   * sRGB color space.
   *
   * @param components the results of this method will be written to this float array. A new float
   *        array will be created if this argument is null.
   * @return the RGB color and alpha components in a float array.
   */
  public float[] getRGBComponents(float[] components) {
    if (components == null) {
      components = new float[4];
    }
    if (frgbvalue != null) {
      components[3] = falpha;
    } else {
      components[3] = getAlpha() / 255f;
    }
    getRGBColorComponents(components);
    return components;
  }

  /**
   * Returns a float array containing the color components of the RGBColor in the default sRGB color
   * space.
   *
   * @param components the results of this method will be written to this float array. A new float
   *        array will be created if this argument is null.
   * @return the RGB color components in a float array.
   */
  public float[] getRGBColorComponents(float[] components) {
    if (components == null) {
      components = new float[3];
    }
    if (frgbvalue != null) {
      components[2] = frgbvalue[2];
      components[1] = frgbvalue[1];
      components[0] = frgbvalue[0];
    } else {
      components[2] = getBlue() / 255f;
      components[1] = getGreen() / 255f;
      components[0] = getRed() / 255f;
    }
    return components;
  }

  /**
   * Returns a float array which contains the color and alpha components of the RGBColor in the
   * ColorSpace of the RGBColor.
   *
   * @param components the results of this method will be written to this float array. A new float
   *        array will be created if this argument is null.
   * @return the color and alpha components in a float array.
   */
  public float[] getComponents(float[] components) {
    if (fvalue == null) {
      return getRGBComponents(components);
    }
    int nColorComps = fvalue.length;
    if (components == null) {
      components = new float[nColorComps + 1];
    }
    getColorComponents(components);
    components[nColorComps] = falpha;
    return components;
  }

  /**
   * Returns a float array which contains the color components of the RGBColor in the ColorSpace of
   * the RGBColor.
   *
   * @param components the results of this method will be written to this float array. A new float
   *        array will be created if this argument is null.
   * @return the color components in a float array.
   */
  public float[] getColorComponents(float[] components) {
    if (fvalue == null) {
      return getRGBColorComponents(components);
    }
    if (components == null) {
      components = new float[fvalue.length];
    }
    for (int i = 0; i < fvalue.length; i++) {
      components[i] = fvalue[i];
    }
    return components;
  }

  /**
   * Returns a hash code of this RGBColor object.
   *
   * @return a hash code of this RGBColor object.
   */
  @Override
  public int hashCode() {
    return value;
  }

  /**
   * Gets the red component of the RGBColor in the range 0-255.
   *
   * @return the red component of the RGBColor.
   */
  public int getRed() {
    return (value >> 16) & 0xFF;
  }

  /**
   * Gets the RGB value that represents the color in the default sRGB ColorModel.
   *
   * @return the RGB color value in the default sRGB ColorModel.
   */
  public int getRGB() {
    return value;
  }

  /**
   * Gets the green component of the RGBColor in the range 0-255.
   *
   * @return the green component of the RGBColor.
   */
  public int getGreen() {
    return (value >> 8) & 0xFF;
  }

  /**
   * Gets the blue component of the RGBColor in the range 0-255.
   *
   * @return the blue component of the RGBColor.
   */
  public int getBlue() {
    return value & 0xFF;
  }

  /**
   * Gets the alpha component of the RGBColor in the range 0-255.
   *
   * @return the alpha component of the RGBColor.
   */
  public int getAlpha() {
    return (value >> 24) & 0xFF;
  }

  /**
   * Gets the RGBColor from the specified string, or returns the RGBColor specified by the second
   * parameter.
   *
   * @param nm the specified string.
   * @param def the default RGBColor.
   * @return the color from the specified string, or the RGBColor specified by the second parameter.
   */
  public static RGBColor getColor(String nm, RGBColor def) {
    Integer integer = Integer.getInteger(nm);
    if (integer == null) {
      return def;
    }
    return new RGBColor(integer);
  }

  /**
   * Gets the RGBColor from the specified string, or returns the RGBColor converted from the second
   * parameter.
   *
   * @param nm the specified string.
   * @param def the default RGBColor.
   * @return the color from the specified string, or the RGBColor converted from the second
   *         parameter.
   */
  public static RGBColor getColor(String nm, int def) {
    Integer integer = Integer.getInteger(nm);
    if (integer == null) {
      return new RGBColor(def);
    }
    return new RGBColor(integer);
  }

  /**
   * Gets the RGBColor from the specified String.
   *
   * @param nm the specified string.
   * @return the RGBColor object, or null.
   */
  public static RGBColor getColor(String nm) {
    Integer integer = Integer.getInteger(nm);
    if (integer == null) {
      return null;
    }
    return new RGBColor(integer);
  }

  /**
   * Decodes a String to an integer and returns the specified opaque RGBColor.
   *
   * @param nm the String which represents an opaque color as a 24-bit integer.
   * @return the RGBColor object from the given String.
   * @throws NumberFormatException if the specified string can not be converted to an integer.
   */
  public static RGBColor decode(String nm) throws NumberFormatException {
    Integer integer = Integer.decode(nm);
    return new RGBColor(integer);
  }

  public static RGBColor getGrayLevel(float grayLevel) {
    return new RGBColor(grayLevel, grayLevel, grayLevel);
  }

  /**
   * Gets a RGBColor object using the specified values of the HSB color model.
   *
   * @param h the hue component of the RGBColor.
   * @param s the saturation of the RGBColor.
   * @param b the brightness of the RGBColor.
   * @return a color object with the specified hue, saturation and brightness values.
   */
  public static RGBColor getHSBColor(float h, float s, float b) {
    return new RGBColor(HSBtoRGB(h, s, b));
  }

  /**
   * Converts the RGBColor specified by the RGB model to an equivalent color in the HSB model.
   *
   * @param r the red component.
   * @param g the green component.
   * @param b the blue component.
   * @param hsbvals the array of result hue, saturation, brightness values or null.
   * @return the float array of hue, saturation, brightness values.
   */
  public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
    if (hsbvals == null) {
      hsbvals = new float[3];
    }
    int V = Math.max(b, Math.max(r, g));
    int temp = Math.min(b, Math.min(r, g));
    float H, S, B;
    B = V / 255.f;
    if (V == temp) {
      H = S = 0;
    } else {
      S = (V - temp) / ((float) V);
      float Cr = (V - r) / (float) (V - temp);
      float Cg = (V - g) / (float) (V - temp);
      float Cb = (V - b) / (float) (V - temp);
      if (r == V) {
        H = Cb - Cg;
      } else if (g == V) {
        H = 2 + Cr - Cb;
      } else {
        H = 4 + Cg - Cr;
      }
      H /= 6.f;
      if (H < 0) {
        H++;
      }
    }
    hsbvals[0] = H;
    hsbvals[1] = S;
    hsbvals[2] = B;
    return hsbvals;
  }

  /**
   * Converts the RGBColor specified by the HSB model to an equivalent color in the default RGB
   * model.
   *
   * @param hue the hue component of the RGBColor.
   * @param saturation the saturation of the RGBColor.
   * @param brightness the brightness of the RGBColor.
   * @return the RGB value of the color with the specified hue, saturation and brightness.
   */
  public static int HSBtoRGB(float hue, float saturation, float brightness) {
    float fr, fg, fb;
    if (saturation == 0) {
      fr = fg = fb = brightness;
    } else {
      float H = (hue - (float) Math.floor(hue)) * 6;
      int I = (int) Math.floor(H);
      float F = H - I;
      float M = brightness * (1 - saturation);
      float N = brightness * (1 - saturation * F);
      float K = brightness * (1 - saturation * (1 - F));
      switch (I) {
        case 0:
          fr = brightness;
          fg = K;
          fb = M;
          break;
        case 1:
          fr = N;
          fg = brightness;
          fb = M;
          break;
        case 2:
          fr = M;
          fg = brightness;
          fb = K;
          break;
        case 3:
          fr = M;
          fg = N;
          fb = brightness;
          break;
        case 4:
          fr = K;
          fg = M;
          fb = brightness;
          break;
        case 5:
          fr = brightness;
          fg = M;
          fb = N;
          break;
        default:
          fr = fb = fg = 0; // impossible, to supress compiler error
      }
    }
    int r = (int) (fr * 255. + 0.5);
    int g = (int) (fg * 255. + 0.5);
    int b = (int) (fb * 255. + 0.5);
    return (r << 16) | (g << 8) | b | 0xFF000000;
  }
}
