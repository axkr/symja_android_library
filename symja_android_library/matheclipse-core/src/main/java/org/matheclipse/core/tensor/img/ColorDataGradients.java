// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.img;

import java.util.Optional;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.io.ResourceData;

/**
 * To obtain a single color value use
 * 
 * <pre>
 * Color color = ColorFormat.toColor(ColorDataGradients.THERMOMETER.apply(RealScalar.of(0.78)));
 * </pre>
 * 
 * <p>
 * The {@link ColorDataGradients#HUE} and {@link ColorDataGradients#GRAYSCALE} are hard-coded, which
 * gives a performance advantage. The remaining color data gradients are backed by linear
 * interpolation over a predefined table of RGBA values implemented in {@link ColorDataGradient}.
 * 
 * @see ColorDataLists
 */
public enum ColorDataGradients implements ColorDataGradient {
  /**
   * classic is default black, blue, cyan green, yellow, red, white
   */
  CLASSIC,
  /**
   * hue is backed by {@link Hue#of(double, double, double, double)} red, yellow, green, cyan, blue,
   * violet cyclic
   */
  HUE(HueColorData.DEFAULT),
  /**
   * hsluv is hue with brightness equalized, see hsluv.org cyclic
   */
  HSLUV,
  /** black, violet, red, orange, yellow, white */
  SUNSET,
  /**
   * approximate default color scheme for Spectrogram white , yellow, orange, red, violet, black,
   */
  SUNSET_REVERSED,
  /** [0, 1] corresponds to wavelengths [380, 750] */
  VISIBLE_SPECTRUM,
  /**
   * extracted from https://www.yourweather.co.uk/weather-maps/temp2m-europ.html
   */
  TEMPERATURE_WEATHER,
  /**
   * blue, cyan, yellow, red inspired by Matlab::jet
   */
  JET,
  /** violet, blue, orange, red */
  RAINBOW,
  /** black, gray, yellow, magenta, cyan */
  CMYK_REVERSED,
  /** for mapmaking, encoding elevations between -6000...6000[m] */
  HYPSOMETRIC,
  /** blue to red, has yellow before turning red */
  TEMPERATURE,
  /** blue to red, has yellow before turning red */
  TEMPERATURE_LIGHT,
  /** blue to red, symmetric */
  THERMOMETER,
  /** brown red yellow green blue */
  SOUTH_WEST,
  /** red-brown, white, cyan */
  BROWN_CYAN,
  /** pink, yellow, cyan */
  PASTEL,
  /** heated black body object emitting temperatures between 1000...10000[K] */
  BLACK_BODY_SPECTRUM,
  /** orange, yellow, white */
  BEACH,
  /** green to red pastel, symmetric */
  MINT,
  /** matlab default */
  PARULA,
  /** Mathematica default */
  DENSITY,
  /** red, orange, yellow */
  SOLAR,
  /** black, gray, white */
  GRAYSCALE(GrayscaleColorData.DEFAULT),
  /** black, blue-gray, white */
  BONE,
  /** purple, blue, white */
  DEEP_SEA, //
  /** black, brown, orange */
  COPPER,
  /** black, green, yellow */
  AVOCADO,
  /**
   * the tensor library is made in Switzerland the alpine color scheme was added August 1st
   * gray-blue, gray-green, light-gray, white
   */
  ALPINE,
  /** black, pink, white */
  PINK,
  /** black, green, yellow, brown, white */
  GREEN_BROWN_TERRAIN,
  /** black, blue, yellow */
  STARRY_NIGHT,
  /** gray, brown, orange, yellow */
  FALL,
  /** green, brown, red */
  ROSE,
  /** yellow, orange, red, magenta */
  NEON,
  /** dark-gray, dark-yellow, magenta */
  AURORA;

  private final ColorDataGradient colorDataGradient;

  ColorDataGradients(ColorDataGradient colorDataGradient) {
    this.colorDataGradient = colorDataGradient;
  }

  ColorDataGradients() {
    colorDataGradient = new LinearColorDataGradient(_tableRgba());
  }

  @Override // from ColorDataGradient
  public IExpr apply(IExpr scalar) {
    return colorDataGradient.apply(scalar);
  }

  @Override // from ColorDataGradient
  public ColorDataGradient deriveWithOpacity(IExpr opacity) {
    return colorDataGradient.deriveWithOpacity(opacity);
  }

  /**
   * @return n x 4 table with entries between 0 and 255
   * @throws Exception if this color data gradient is not backed by such a table
   */
  private IAST _tableRgba() {
    return ResourceData.of("/img/colorscheme/" + name().toLowerCase() + ".csv");
  }

  /**
   * @return n x 4 table with entries between 0 and 255, or empty if this color data gradient is not
   *         backed by such a table
   */
  public Optional<IAST> queryTableRgba() {
    return colorDataGradient instanceof LinearColorDataGradient //
        ? Optional.of(_tableRgba())
        : Optional.empty();
  }
}
