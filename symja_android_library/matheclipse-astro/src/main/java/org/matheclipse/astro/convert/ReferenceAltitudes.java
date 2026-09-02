package org.matheclipse.astro.convert;

import java.util.Locale;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The <code>ReferenceAltitude</code> option, which says <em>which part of a body</em> has to cross
 * <em>which altitude</em> for a rise or a set to have happened.
 *
 * <p>
 * Accepted values are
 *
 * <ul>
 * <li>{@link S#Automatic} - the upper limb crosses altitude zero, the usual definition of sunrise
 * and sunset;
 * <li>a number or an angle <code>Quantity</code> - the centre crosses that altitude;
 * <li><code>{altitude, limb}</code> - the given limb crosses that altitude, where the limb is
 * <code>"UpperLimb"</code>, <code>"Center"</code>, <code>"LowerLimb"</code> or a number between
 * <code>-1</code> and <code>1</code>;
 * <li><code>"Civil"</code>, <code>"Nautical"</code>, <code>"Astronomical"</code> - the centre at
 * -6, -12 and -18 degrees, the twilight definitions.
 * </ul>
 */
public class ReferenceAltitudes {

  /** Centre of the body at -6 degrees. */
  public static final double CIVIL_TWILIGHT = FastMath.toRadians(-6.0);

  /** Centre of the body at -12 degrees. */
  public static final double NAUTICAL_TWILIGHT = FastMath.toRadians(-12.0);

  /** Centre of the body at -18 degrees. */
  public static final double ASTRONOMICAL_TWILIGHT = FastMath.toRadians(-18.0);

  /** The altitude the chosen limb has to cross, in radians. */
  private final double altitude;

  /**
   * Which limb crosses it: <code>1</code> upper, <code>0</code> centre, <code>-1</code> lower.
   */
  private final double limb;

  /**
   * Whether atmospheric refraction applies. The twilight definitions are geometric and are
   * conventionally quoted for the true altitude, while rise and set are quoted for the apparent
   * one.
   */
  private final boolean refracted;

  private ReferenceAltitudes(double altitude, double limb, boolean refracted) {
    this.altitude = altitude;
    this.limb = limb;
    this.refracted = refracted;
  }

  /**
   * Parse the option value.
   *
   * @param option the option value, may be <code>null</code> or {@link S#Automatic}
   * @return the parsed reference altitude, or <code>null</code> if the value is not a legal one
   */
  public static ReferenceAltitudes of(IExpr option, EvalEngine engine) {
    if (option == null || option == S.Automatic) {
      // upper limb at zero altitude, refraction included: the standard sunrise
      return new ReferenceAltitudes(0.0, 1.0, true);
    }
    if (option.isString()) {
      switch (option.toString().toLowerCase(Locale.US)) {
        case "civil":
          return new ReferenceAltitudes(CIVIL_TWILIGHT, 0.0, false);
        case "nautical":
          return new ReferenceAltitudes(NAUTICAL_TWILIGHT, 0.0, false);
        case "astronomical":
          return new ReferenceAltitudes(ASTRONOMICAL_TWILIGHT, 0.0, false);
        case "upperlimb":
          return new ReferenceAltitudes(0.0, 1.0, true);
        case "center":
          return new ReferenceAltitudes(0.0, 0.0, true);
        case "lowerlimb":
          return new ReferenceAltitudes(0.0, -1.0, true);
        default:
          return null;
      }
    }
    if (option.isList() && ((IAST) option).size() == 3) {
      IAST list = (IAST) option;
      Double angle = AstroConvert.toRadians(list.arg1(), engine);
      Double limb = toLimb(list.arg2());
      if (angle == null || limb == null) {
        return null;
      }
      return new ReferenceAltitudes(angle, limb, true);
    }
    Double angle = AstroConvert.toRadians(option, engine);
    return angle == null ? null : new ReferenceAltitudes(angle, 0.0, true);
  }

  /**
   * @return the limb factor, or <code>null</code> if {@code expr} does not name a limb
   */
  private static Double toLimb(IExpr expr) {
    if (expr.isString()) {
      switch (expr.toString().toLowerCase(Locale.US)) {
        case "upperlimb":
          return 1.0;
        case "center":
          return 0.0;
        case "lowerlimb":
          return -1.0;
        default:
          return null;
      }
    }
    if (expr.isReal()) {
      double value = expr.evalf();
      return value >= -1.0 && value <= 1.0 ? Double.valueOf(value) : null;
    }
    return null;
  }

  /**
   * The elevation the <em>centre</em> of the body has to reach, which is the requested altitude
   * pushed down by the part of the disk which is above the centre.
   *
   * @param apparentRadius the apparent radius of the body as seen by the observer, in radians
   */
  public double centerElevation(double apparentRadius) {
    return altitude - limb * apparentRadius;
  }

  /** @return whether atmospheric refraction has to be applied for this definition */
  public boolean isRefracted() {
    return refracted;
  }
}
