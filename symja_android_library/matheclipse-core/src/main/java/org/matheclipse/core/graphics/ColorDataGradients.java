package org.matheclipse.core.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Function;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The named color gradients of <code>ColorData</code>. A gradient maps a number in
 * <code>[0, 1]</code> to the list <code>{r, g, b, a}</code> with components in the range
 * <code>0 ... 255</code>; a value that is not a finite number gives the transparent
 * <code>{0.0, 0.0, 0.0, 0.0}</code>.
 *
 * <p>
 * {@link #HUE} and {@link #GRAYSCALE} are computed. Every other gradient interpolates linearly
 * between the evenly spaced rows of the table <code>/img/colorscheme/&lt;name&gt;.csv</code>.
 *
 * @see ColorDataLists
 */
public enum ColorDataGradients implements Function<IExpr, IExpr> {
  /**
   * classic is default black, blue, cyan green, yellow, red, white
   */
  CLASSIC,
  /**
   * hue is backed by a hue circle with full saturation and brightness: red, yellow, green, cyan, blue,
   * violet cyclic
   */
  HUE(Kind.HUE),
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
  GRAYSCALE(Kind.GRAYSCALE),
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
  AURORA,
  /** red, blue, cyan, green, yellow, orange, each followed by a paler band of the same hue */
  BRIGHT_BANDS,
  /** blue, pale yellow, red - an approximation of Mathematica's TemperatureMap */
  TEMPERATURE_MAP,
  /** dark blue, green, yellow, orange, dark red - an approximation of the DarkRainbow gradient */
  DARK_RAINBOW,
  /*
   * The remaining gradients of the standard collection, each a table of evenly spaced colours named
   * after the scheme: ArmyColors is ARMY, BrassTones is BRASS_TONES and so on.
   */
  AQUAMARINE,
  ARMY,
  ATLANTIC,
  BLUE_GREEN_YELLOW,
  BRASS_TONES,
  CANDY,
  CHERRY_TONES,
  CMYK,
  COFFEE_TONES,
  DARK_BANDS,
  DARK_TERRAIN,
  FRUIT_PUNCH,
  FUCHSIA_TONES,
  GRAY_TONES,
  GRAY_YELLOW_TONES,
  GREEN_PINK_TONES,
  ISLAND,
  LAKE,
  LIGHT_TEMPERATURE_MAP,
  LIGHT_TERRAIN,
  PEARL,
  PIGEON_TONES,
  PLUM,
  RED_BLUE_TONES,
  RED_GREEN_SPLIT,
  RUST_TONES,
  SANDY_TERRAIN,
  SIENNA_TONES,
  VALENTINE_TONES,
  WATERMELON;

  /** How the colors of a gradient are computed. */
  private enum Kind {
    TABLE, HUE, GRAYSCALE
  }

  private final Kind kind;

  /**
   * The rows <code>{r, g, b, a}</code> of a {@link Kind#TABLE} gradient, read on first use;
   * <code>null</code> before that and for the computed gradients.
   */
  private volatile IAST table;

  ColorDataGradients(Kind kind) {
    this.kind = kind;
  }

  ColorDataGradients() {
    this.kind = Kind.TABLE;
  }

  /** The table of this gradient, read from its resource on the first call. */
  private IAST table() {
    IAST result = table;
    if (result == null) {
      synchronized (this) {
        result = table;
        if (result == null) {
          result = readRGBATable("/img/colorscheme/" + name().toLowerCase(Locale.US) + ".csv");
          table = result;
        }
      }
    }
    return result;
  }

  /**
   * The color at <code>scalar</code>.
   *
   * @param scalar a number in <code>[0, 1]</code>; a table gradient clamps any other finite number
   *        to that range
   * @return the list <code>{r, g, b, a}</code>
   */
  @Override
  public IExpr apply(IExpr scalar) {
    switch (kind) {
      case HUE:
        return hue(scalar.evalfNaN());
      case GRAYSCALE:
        return grayscale(scalar.evalfNaN());
      default:
        return interpolate(scalar);
    }
  }

  private IExpr interpolate(IExpr scalar) {
    final IAST rows = table();
    final int last = rows.argSize() - 1;
    double value = scalar.multiply((double) last).evalfNaN();
    if (!Double.isFinite(value)) {
      return transparent();
    }
    value = Math.min(last, Math.max(0.0, value));
    double floor = Math.floor(value);
    double remain = value - floor;
    int below = (int) floor;
    IAST lower = (IAST) rows.get(below + 1);
    if (remain == 0.0) {
      return lower.copy();
    }
    // the weighted sum (1 - remain) * row(below) + remain * row(below + 1)
    IAST upper = (IAST) rows.get(below + 2);
    double weight = 1.0 - remain;
    IASTAppendable result = F.ListAlloc(lower.argSize());
    for (int i = 1; i < lower.size(); i++) {
      result.append(F.num(weight * lower.get(i).evalf() + remain * upper.get(i).evalf()));
    }
    return result;
  }

  private static IExpr grayscale(double value) {
    if (Double.isNaN(value) || value > 1.0 || value < 0.0) {
      return transparent();
    }
    IExpr gray = F.ZZ((int) (value * 255 + 0.5));
    return F.List(gray, gray, gray, F.ZZ(255));
  }

  /**
   * The color of the hue circle at <code>hue</code> with full saturation, brightness and opacity.
   */
  private static IExpr hue(double hue) {
    if (!Double.isFinite(hue)) {
      return transparent();
    }
    hue %= 1;
    if (hue < 0) {
      hue += 1;
    }
    hue *= 6;
    if (hue >= 6) {
      // a tiny negative hue rounds up to a full turn, which is the start of the circle
      hue = 0;
    }
    int sector = (int) hue;
    double f = hue - sector;
    double falling = 1 - f;
    // kept as 1 - (1 - f), which is not always the same double as f
    double rising = 1 - falling;
    final double r;
    final double g;
    final double b;
    switch (sector) {
      case 0:
        r = 1;
        g = rising;
        b = 0;
        break;
      case 1:
        r = falling;
        g = 1;
        b = 0;
        break;
      case 2:
        r = 0;
        g = 1;
        b = rising;
        break;
      case 3:
        r = 0;
        g = falling;
        b = 1;
        break;
      case 4:
        r = rising;
        g = 0;
        b = 1;
        break;
      case 5:
      default:
        r = 1;
        g = 0;
        b = falling;
        break;
    }
    return new RGBColor(component(r), component(g), component(b), 255).toRGBAList();
  }

  /** A component in <code>[0, 1]</code> rounded through <code>float</code> to <code>0 ... 255</code>. */
  private static int component(double value) {
    return (int) ((float) value * 255 + 0.5);
  }

  private static IAST transparent() {
    return F.List(0.0, 0.0, 0.0, 0.0);
  }

  /**
   * A table component: an integer such as <code>255</code>, otherwise a machine number such as
   * <code>194.3</code> or <code>1.5e2</code>.
   *
   * @throws IllegalStateException if <code>cell</code> is no number
   */
  private static IExpr parseComponent(String cell, String resource) {
    try {
      if (cell.indexOf('.') < 0 && cell.indexOf('e') < 0 && cell.indexOf('E') < 0) {
        return F.ZZ(Integer.parseInt(cell));
      }
      return F.num(Double.parseDouble(cell));
    } catch (NumberFormatException nfe) {
      throw new IllegalStateException("no number \"" + cell + "\" in color table " + resource,
          nfe);
    }
  }

  /**
   * Read a table of colors, one row <code>r,g,b,a</code> per line, from a class path resource. A
   * component with a decimal point or an exponent is read as a machine number, every other one as
   * an integer.
   *
   * @param resource the absolute path of the resource
   * @return the list of rows <code>{r, g, b, a}</code>
   */
  static IAST readRGBATable(String resource) {
    try (InputStream inputStream = ColorDataGradients.class.getResourceAsStream(resource)) {
      if (inputStream == null) {
        throw new IllegalStateException("missing color table " + resource);
      }
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
      IASTAppendable rows = F.ListAlloc(256);
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) {
          continue;
        }
        String[] cells = line.split(",");
        IASTAppendable row = F.ListAlloc(cells.length);
        for (String cell : cells) {
          row.append(parseComponent(cell.trim(), resource));
        }
        rows.append(row);
      }
      return rows;
    } catch (IOException e) {
      throw new IllegalStateException("unreadable color table " + resource, e);
    }
  }
}
