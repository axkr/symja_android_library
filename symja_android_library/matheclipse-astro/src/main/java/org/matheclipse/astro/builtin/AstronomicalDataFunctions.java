package org.matheclipse.astro.builtin;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.matheclipse.astro.convert.AstroBodies;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.StaticTransform;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;

/**
 * Properties of the solar system bodies, in the calling convention the Demonstrations use.
 *
 * <p>
 * <code>AstronomicalData(n)</code> names the <code>n</code>th major body counting outwards from the
 * Sun and <code>AstronomicalData(body, {"Position", date})</code> gives where it is, so that the
 * two compose: <code>AstronomicalData(AstronomicalData(k), {"Position", t})</code> is how a
 * notebook walks the planets.
 *
 * <p>
 * The position is heliocentric and referred to the mean ecliptic and equinox of J2000 - a frame
 * which does not turn with the date, so positions at different dates can be drawn in one picture.
 * It comes from the bundled DE ephemerides rather than from a two body approximation, which is
 * what {@link AstroOrbitFunctions} would give.
 */
public class AstronomicalDataFunctions {

  /**
   * The major bodies in the order <code>AstronomicalData(n)</code> counts them, outwards from the
   * Sun.
   *
   * <p>
   * Pluto is the ninth: this is the classic list the pre-Entity convention was written for, and
   * dropping it would renumber nothing else but would make <code>AstronomicalData(9)</code> a hole.
   */
  private static final String[] MAJOR_BODIES = {"Mercury", "Venus", "Earth", "Mars", "Jupiter",
      "Saturn", "Uranus", "Neptune", "Pluto"};

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AstronomicalData.setEvaluator(new AstronomicalData());
    }
  }

  /**
   * <code>AstronomicalData(n)</code>, <code>AstronomicalData(body)</code> or
   * <code>AstronomicalData(body, {"Position", date})</code>.
   */
  private static final class AstronomicalData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int index = arg1.toIntDefault();
      if (index != Config.INVALID_INT) {
        // AstronomicalData(n) is a name, which needs no ephemerides and no data files
        return ast.isAST1() && index >= 1 && index <= MAJOR_BODIES.length
            ? F.stringx(MAJOR_BODIES[index - 1])
            : F.NIL;
      }
      String bodyName = AstroBodies.nameOf(arg1);
      if (bodyName == null) {
        return Errors.printMessage(S.AstronomicalData, "astrobody", F.List(arg1, ast), engine);
      }
      if (ast.isAST1()) {
        // the body named as this function spells it, which is what the position form takes
        return F.stringx(bodyName);
      }
      IExpr property = ast.arg2();
      IExpr dateExpr = F.NIL;
      if (property.isList()) {
        IAST spec = (IAST) property;
        if (spec.argSize() < 1) {
          return Errors.printMessage(S.AstronomicalData, "astroprop", F.List(property, ast),
              engine);
        }
        property = spec.arg1();
        if (spec.argSize() >= 2) {
          dateExpr = spec.arg2();
        }
      }
      if (!property.isString("Position")) {
        return Errors.printMessage(S.AstronomicalData, "astroprop", F.List(property, ast), engine);
      }
      if (!AstroDataContext.checkAvailable(S.AstronomicalData, engine)) {
        return F.NIL;
      }
      AbsoluteDate date;
      if (dateExpr.isPresent()) {
        date = AstroConvert.toAbsoluteDate(dateExpr);
        if (date == null) {
          return AstroConvert.reportUnreadableArgument(S.AstronomicalData, dateExpr, ast, engine);
        }
      } else {
        date = AstroConvert.nowUTC();
      }
      try {
        Vector3D position = heliocentricEclipticPosition(bodyName, date);
        return F.List(F.num(position.getX()), F.num(position.getY()), F.num(position.getZ()));
      } catch (OrekitException oex) {
        return Errors.printMessage(S.AstronomicalData, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * Where {@code bodyName} is at {@code date}, seen from the Sun, in metres along the axes of the
   * mean ecliptic and equinox of J2000.
   *
   * <p>
   * The ephemerides are read in the Earth centred GCRF and the Sun is subtracted there, since the
   * difference of two positions in one frame is the same vector whichever of them is the origin.
   * The ecliptic frame Orekit builds is the ecliptic <em>of date</em>, so its orientation is taken
   * once at J2000 and used at every date - otherwise a planet plotted over centuries would also
   * carry the slow turning of the frame itself.
   */
  static Vector3D heliocentricEclipticPosition(String bodyName, AbsoluteDate date) {
    Frame gcrf = FramesFactory.getGCRF();
    Vector3D heliocentric = CelestialBodyFactory.getBody(bodyName).getPosition(date, gcrf)
        .subtract(CelestialBodyFactory.getSun().getPosition(date, gcrf));
    StaticTransform toEcliptic = gcrf.getStaticTransformTo(
        FramesFactory.getEcliptic(IERSConventions.IERS_2010), AbsoluteDate.J2000_EPOCH);
    return toEcliptic.transformVector(heliocentric);
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstronomicalDataFunctions() {}
}
