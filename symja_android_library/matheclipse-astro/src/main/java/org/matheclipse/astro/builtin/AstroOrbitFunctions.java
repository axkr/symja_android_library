package org.matheclipse.astro.builtin;

import java.util.Locale;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroBodies;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;

/**
 * The osculating Keplerian elements of a solar system body.
 *
 * <p>
 * The elements are referred to the ecliptic of date and to the body the target orbits - the Earth
 * for the Moon, the Sun for everything else. They are <em>osculating</em>: the two body orbit which
 * matches the position and the velocity at the given instant, not a mean orbit fitted over a
 * revolution, so the values wobble slightly from date to date.
 */
public class AstroOrbitFunctions {

  /** The elements returned when no element is asked for by name. */
  private static final String[] DEFAULT_ELEMENTS = {"SemimajorAxis", "Eccentricity", "Inclination",
      "AscendingNodeLongitude", "PeriapsisArgument", "MeanAnomaly"};

  /** Every element this implementation can return, in a readable order. */
  private static final String[] ALL_ELEMENTS = {"SemimajorAxis", "SemiminorAxis",
      "SemilatusRectum", "Eccentricity", "PeriapsisDistance", "ApoapsisDistance", "Inclination",
      "AscendingNodeLongitude", "PeriapsisArgument", "PeriapsisLongitude", "MeanAnomaly",
      "TrueAnomaly", "EccentricAnomaly", "MeanLongitude", "TrueLongitude", "EccentricLongitude",
      "LatitudeArgument", "StandardGravitationalParameter", "MeanMotion", "OrbitalPeriod",
      "OrbitalEnergy", "OrbitalAngularMomentum", "EquinoctialF", "EquinoctialG", "EquinoctialH",
      "EquinoctialK", "Radius", "Velocity", "PositionVector", "VelocityVector",
      "AngularMomentumVector", "Date"};

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.OrbitalElements.setEvaluator(new OrbitalElements());
    }
  }

  /**
   * Build the osculating orbit of {@code bodyName} about the body it orbits, expressed in the
   * ecliptic frame of date.
   */
  static KeplerianOrbit orbitOf(String bodyName, AbsoluteDate date) {
    Frame ecliptic = FramesFactory.getEcliptic(IERSConventions.IERS_2010);
    CelestialBody body = CelestialBodyFactory.getBody(bodyName);
    CelestialBody primary = CelestialBodyFactory.getBody(AstroBodies.primaryOf(bodyName));
    // the ecliptic frame is centred on the Earth, so the primary has to be subtracted to get the
    // relative state the elements describe
    PVCoordinates relative = new PVCoordinates(primary.getPVCoordinates(date, ecliptic),
        body.getPVCoordinates(date, ecliptic));
    return new KeplerianOrbit(relative, ecliptic, date, primary.getGM());
  }

  /**
   * <code>OrbitalElements(body, elements, date)</code>.
   */
  private static final class OrbitalElements extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(S.OrbitalElements, engine)) {
        return F.NIL;
      }
      String bodyName = AstroBodies.nameOf(ast.arg1());
      if (bodyName == null) {
        return Errors.printMessage(S.OrbitalElements, "astrobody", F.List(ast.arg1(), ast), engine);
      }
      // the element specification is optional, so a second argument which parses as a date is
      // the date and not an element name
      IExpr elementSpec = S.Automatic;
      AbsoluteDate date = null;
      for (int i = 2; i <= argSize; i++) {
        IExpr arg = ast.get(i);
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (argDate != null) {
          date = argDate;
        } else {
          elementSpec = arg;
        }
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      IExpr method = options[0];
      if (method != null && method != S.Automatic) {
        // VSOP87 and SecularVSOP2013 have no Orekit equivalent; the DE ephemerides are the only
        // source available here
        return Errors.printMessage(S.OrbitalElements, "astromethod", F.List(method, ast), engine);
      }
      boolean dimensionless = isDimensionless(options[1]);

      try {
        KeplerianOrbit orbit = orbitOf(bodyName, date);
        if (elementSpec == S.Automatic) {
          return association(orbit, DEFAULT_ELEMENTS, dimensionless, ast, engine);
        }
        if (elementSpec == S.All) {
          return association(orbit, ALL_ELEMENTS, dimensionless, ast, engine);
        }
        if (elementSpec.isList()) {
          IAST names = (IAST) elementSpec;
          IASTAppendable result = F.ListAlloc(names.argSize());
          for (int i = 1; i <= names.argSize(); i++) {
            IExpr value = element(orbit, names.get(i), dimensionless, ast, engine);
            if (value.isNIL()) {
              return F.NIL;
            }
            result.append(value);
          }
          return result;
        }
        return element(orbit, elementSpec, dimensionless, ast, engine);
      } catch (OrekitException oex) {
        return Errors.printMessage(S.OrbitalElements, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    /** {@code UnitSystem -> None} or {@code "Dimensionless"} strips the units off the result. */
    private static boolean isDimensionless(IExpr unitSystem) {
      if (unitSystem == null || unitSystem == S.Automatic) {
        return false;
      }
      return unitSystem == S.None
          || (unitSystem.isString() && "Dimensionless".equalsIgnoreCase(unitSystem.toString()));
    }

    private IExpr association(KeplerianOrbit orbit, String[] names, boolean dimensionless,
        IAST ast, EvalEngine engine) {
      IASTAppendable rules = F.ListAlloc(names.length);
      for (String name : names) {
        IExpr value = element(orbit, F.stringx(name), dimensionless, ast, engine);
        if (value.isNIL()) {
          return F.NIL;
        }
        rules.append(F.Rule(F.stringx(name), value));
      }
      return F.assoc(rules);
    }

    private IExpr element(KeplerianOrbit orbit, IExpr nameExpr, boolean dimensionless, IAST ast,
        EvalEngine engine) {
      if (!nameExpr.isString()) {
        return Errors.printMessage(S.OrbitalElements, "astroprop", F.List(nameExpr, ast), engine);
      }
      String name = nameExpr.toString();
      double a = orbit.getA();
      double e = orbit.getE();
      switch (name.toLowerCase(Locale.US)) {
        case "semimajoraxis":
          return length(a, dimensionless);
        case "semiminoraxis":
          return length(a * FastMath.sqrt(1.0 - e * e), dimensionless);
        case "semilatusrectum":
          return length(a * (1.0 - e * e), dimensionless);
        case "eccentricity":
          return F.num(e);
        case "periapsisdistance":
          return length(a * (1.0 - e), dimensionless);
        case "apoapsisdistance":
          return length(a * (1.0 + e), dimensionless);
        case "inclination":
          return angle(orbit.getI(), dimensionless);
        case "ascendingnodelongitude":
          return angle(orbit.getRightAscensionOfAscendingNode(), dimensionless);
        case "periapsisargument":
          return angle(orbit.getPeriapsisArgument(), dimensionless);
        case "periapsislongitude":
          return angle(orbit.getRightAscensionOfAscendingNode() + orbit.getPeriapsisArgument(),
              dimensionless);
        case "meananomaly":
          return angle(orbit.getMeanAnomaly(), dimensionless);
        case "trueanomaly":
          return angle(orbit.getTrueAnomaly(), dimensionless);
        case "eccentricanomaly":
          return angle(orbit.getEccentricAnomaly(), dimensionless);
        case "meanlongitude":
          return angle(orbit.getLM(), dimensionless);
        case "truelongitude":
          return angle(orbit.getLv(), dimensionless);
        case "eccentriclongitude":
          return angle(orbit.getLE(), dimensionless);
        case "latitudeargument":
          return angle(orbit.getPeriapsisArgument() + orbit.getTrueAnomaly(), dimensionless);
        case "standardgravitationalparameter":
          return F.num(orbit.getMu());
        case "meanmotion":
          // in radians per second; a compound unit would not survive UnitConvert cleanly
          return F.num(orbit.getKeplerianMeanMotion());
        case "orbitalperiod":
          return dimensionless ? F.num(orbit.getKeplerianPeriod())
              : F.Quantity(F.num(orbit.getKeplerianPeriod()), F.stringx("Seconds"));
        case "orbitalenergy":
          // the specific orbital energy -mu/(2a), in joules per kilogram
          return F.num(-orbit.getMu() / (2.0 * a));
        case "orbitalangularmomentum":
          return F.num(Vector3D.crossProduct(orbit.getPosition(),
              orbit.getPVCoordinates().getVelocity()).getNorm());
        case "equinoctialf":
          return F.num(orbit.getEquinoctialEx());
        case "equinoctialg":
          return F.num(orbit.getEquinoctialEy());
        case "equinoctialh":
          return F.num(orbit.getHx());
        case "equinoctialk":
          return F.num(orbit.getHy());
        case "radius":
          return length(orbit.getPosition().getNorm(), dimensionless);
        case "velocity":
          return F.num(orbit.getPVCoordinates().getVelocity().getNorm());
        case "positionvector":
          return vector(orbit.getPosition());
        case "velocityvector":
          return vector(orbit.getPVCoordinates().getVelocity());
        case "angularmomentumvector":
          return vector(Vector3D.crossProduct(orbit.getPosition(),
              orbit.getPVCoordinates().getVelocity()));
        case "date":
          return AstroConvert.toDateObject(orbit.getDate());
        default:
          return Errors.printMessage(S.OrbitalElements, "astroprop", F.List(nameExpr, ast), engine);
      }
    }

    private static IExpr length(double meters, boolean dimensionless) {
      return dimensionless ? F.num(meters) : AstroConvert.meters(meters);
    }

    private static IExpr angle(double radians, boolean dimensionless) {
      return dimensionless ? F.num(radians) : AstroConvert.degreesPositive(radians);
    }

    private static IExpr vector(Vector3D vector) {
      return F.List(F.num(vector.getX()), F.num(vector.getY()), F.num(vector.getZ()));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.Method, S.UnitSystem}, //
          new IExpr[] {S.Automatic, S.Automatic});
    }

    @Override
    public int status() {
      // the Laskar and Delaunay element sets, the orbit time series and the apsis dates are not
      // implemented, and Method is restricted to the DE ephemerides
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstroOrbitFunctions() {}
}
