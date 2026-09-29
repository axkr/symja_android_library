package org.matheclipse.astro.meeus;

import java.util.Locale;

/**
 * The solar system bodies the Meeus theories of this package cover, in the numbering Night Vision
 * uses for its near sky objects (0 = Mercury ... 7 = Pluto, 8 = Sun, 9 = Moon).
 */
public enum MeeusBody {
  MERCURY, VENUS, MARS, JUPITER, SATURN, URANUS, NEPTUNE, PLUTO, SUN, MOON;

  /** Whether this is one of the planets Mercury to Pluto. */
  public boolean isPlanet() {
    return ordinal() <= PLUTO.ordinal();
  }

  /**
   * @param name a body name such as <code>"Mars"</code>, compared ignoring case; also accepts the
   *        Orekit names, which are the same words
   * @return the body, or <code>null</code> if the Meeus theories do not cover it
   */
  public static MeeusBody of(String name) {
    if (name == null) {
      return null;
    }
    try {
      return valueOf(name.toUpperCase(Locale.US));
    } catch (IllegalArgumentException iae) {
      return null;
    }
  }
}
