package org.matheclipse.astro.builtin;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.convert.SatelliteTle;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.core.data.Entities;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.TimeStampedPVCoordinates;

/**
 * <code>SatelliteData(sat, property)</code> - data of an Earth satellite given by its NORAD
 * two-line element set, propagated with Orekit's SGP4/SDP4.
 *
 * <p>
 * Symja has no satellite catalogue: <code>sat</code> is the element set itself,
 * <code>{line1, line2}</code> or <code>{name, line1, line2}</code>, bare or as
 * <code>Entity("Satellite", {...})</code>. The same element set is accepted by
 * <code>AstroPosition</code>, <code>AstroDistance</code>, <code>AstroAngularSeparation</code> and
 * <code>AstroSubpoint</code>.
 *
 * <p>
 * The orbital elements are the mean elements of the set; the dated properties propagate it to the
 * date, which defaults to now. <code>"Altitude"</code>, <code>"Azimuth"</code> and
 * <code>"DistanceFromLocation"</code> are seen from a location given as an argument, or else from
 * <code>$GeoLocation</code>.
 *
 * <p>
 * Distances are in miles and the speed in miles per hour. Angles, the horizon altitude included,
 * are plain <code>"AngularDegrees"</code> as everywhere in this module, not degree-minute-second
 * mixed units.
 */
public class SatelliteFunctions {

  /** The properties returned when none is asked for. */
  private static final String[] DEFAULT_PROPERTIES = {"Name", "NORADNumber", "Epoch",
      "Inclination", "Eccentricity", "SemimajorAxis", "ApoapsisDistance", "PeriapsisDistance"};

  /** Everything {@link #property} can return. */
  private static final String[] ALL_PROPERTIES = {"Altitude", "ApoapsisDistance",
      "AscendingNodeLongitude", "Azimuth", "DistanceFromLocation", "Eccentricity",
      "ElevationWGS84", "Epoch", "Inclination", "MeanAnomaly", "MeanMotion", "Name", "NORADNumber",
      "NSSDCID", "PeriapsisArgument", "PeriapsisDistance", "PeriapsisLongitude", "PeriodOsculating",
      "Position", "SemimajorAxis", "SemiminorAxis", "Velocity"};

  private static class Initializer {

    private static void init() {
      S.SatelliteData.setEvaluator(new SatelliteData());
      Entities.register(SatelliteTle.SATELLITE, S.SatelliteData);
    }
  }

  private static final class SatelliteData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1() && ast.arg1().isString("Properties")) {
        IASTAppendable properties = F.ListAlloc(ALL_PROPERTIES.length);
        for (String name : ALL_PROPERTIES) {
          properties.append(Entities.property(SatelliteTle.SATELLITE, name));
        }
        return properties;
      }
      if (!AstroDataContext.checkAvailable(S.SatelliteData, engine)) {
        return F.NIL;
      }
      IExpr satelliteSpec = ast.arg1();
      if (!SatelliteTle.isCandidate(satelliteSpec)) {
        // a list of satellites
        if (satelliteSpec.isList() && satelliteSpec.argSize() > 0
            && ((IAST) satelliteSpec).forAll(x -> SatelliteTle.isCandidate(x))) {
          IASTAppendable result = F.ListAlloc(satelliteSpec.argSize());
          for (IExpr element : (IAST) satelliteSpec) {
            IExpr value = evaluateSingle(element, ast, engine);
            if (value.isNIL()) {
              return F.NIL;
            }
            result.append(value);
          }
          return result;
        }
        return F.NIL;
      }
      return evaluateSingle(satelliteSpec, ast, engine);
    }

    private static IExpr evaluateSingle(IExpr satelliteSpec, IAST ast, EvalEngine engine) {
      SatelliteTle satellite;
      try {
        satellite = SatelliteTle.of(satelliteSpec);
      } catch (RuntimeException rex) {
        // `1` is not a valid two-line element set: `2`
        return Errors.printMessage(S.SatelliteData, "astrotle",
            F.List(satelliteSpec, F.stringx(String.valueOf(rex.getMessage()))), engine);
      }

      IExpr property = null;
      AbsoluteDate date = null;
      GeodeticPoint observer = null;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = Entities.propertyOf(ast.get(i), SatelliteTle.SATELLITE);
        // {property, date}, as PlanetData writes a dated property
        if (arg.isList2() && arg.first().isString()) {
          AbsoluteDate listDate = AstroConvert.toAbsoluteDate(arg.second());
          if (listDate != null) {
            property = arg.first();
            date = listDate;
            continue;
          }
        }
        if (property == null && (arg.isString() || (arg.isList() && arg.argSize() > 0
            && ((IAST) arg).forAll(x -> x.isString())))) {
          property = arg;
          continue;
        }
        GeodeticPoint argPoint = AstroConvert.toGeodeticPoint(arg);
        if (observer == null && argPoint != null) {
          observer = argPoint;
          continue;
        }
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (date == null && argDate != null) {
          date = argDate;
          continue;
        }
        return AstroConvert.reportUnreadableArgument(S.SatelliteData, ast.get(i), ast, engine);
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      try {
        State state = new State(satellite, date, observer, engine);
        if (property == null) {
          IASTAppendable rules = F.ListAlloc(DEFAULT_PROPERTIES.length);
          for (String name : DEFAULT_PROPERTIES) {
            rules.append(F.Rule(F.stringx(name), property(name, state)));
          }
          return F.assoc(rules);
        }
        if (property.isList()) {
          IASTAppendable result = F.ListAlloc(property.argSize());
          for (IExpr p : (IAST) property) {
            result.append(propertyOrMissing(p, state));
          }
          return result;
        }
        return propertyOrMissing(property, state);
      } catch (OrekitException oex) {
        return Errors.printMessage(S.SatelliteData, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    /** The value, or <code>Missing("UnknownProperty", {"Satellite", name})</code>. */
    private static IExpr propertyOrMissing(IExpr name, State state) {
      IExpr value = property(name.toString(), state);
      if (value == null) {
        return F.Missing(F.stringx("UnknownProperty"),
            F.List(F.stringx(SatelliteTle.SATELLITE), name));
      }
      return value;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }
  }

  /** A satellite at a date; the propagation is done once and only if a dated property asks. */
  private static final class State {
    final SatelliteTle satellite;
    final AbsoluteDate date;
    private final GeodeticPoint observer;
    private final EvalEngine engine;
    private PVCoordinates pv;
    private Frame frame;
    private GeodeticPoint subpoint;

    State(SatelliteTle satellite, AbsoluteDate date, GeodeticPoint observer, EvalEngine engine) {
      this.satellite = satellite;
      this.date = date;
      this.observer = observer;
      this.engine = engine;
    }

    /** Position and velocity in TEME, the frame SGP4 works in. */
    PVCoordinates pv() {
      if (pv == null) {
        TLEPropagator propagator = satellite.propagator();
        frame = propagator.getFrame();
        pv = propagator.getPVCoordinates(date, frame);
      }
      return pv;
    }

    Frame frame() {
      pv();
      return frame;
    }

    GeodeticPoint subpoint() {
      if (subpoint == null) {
        subpoint = AstroConvert.earthEllipsoid().transform(pv().getPosition(), frame(), date);
      }
      return subpoint;
    }

    /** The observer's frame, or <code>null</code> when no location is known. */
    TopocentricFrame station() {
      GeodeticPoint point = observer != null ? observer : AstroConvert.defaultObserver(engine);
      return point == null ? null : AstroConvert.toTopocentricFrame(point);
    }
  }

  /** @return the value, or <code>null</code> if {@code name} is not a property */
  private static IExpr property(String name, State state) {
    SatelliteTle satellite = state.satellite;
    double a = satellite.semiMajorAxis();
    double e = satellite.tle.getE();
    switch (name) {
      case "Name":
        return F.stringx(satellite.name);
      case "NORADNumber":
        return F.ZZ(satellite.tle.getSatelliteNumber());
      case "NSSDCID":
        return F.stringx(String.format("%04d-%03d%s", internationalYear(satellite),
            satellite.tle.getLaunchNumber(), satellite.tle.getLaunchPiece().trim()));
      case "Epoch":
        return AstroConvert.toDateObject(satellite.tle.getDate());
      case "Inclination":
        return AstroConvert.degrees(satellite.tle.getI());
      case "Eccentricity":
        return F.num(e);
      case "MeanMotion":
        // revolutions per day, as the element set writes it
        return F.Quantity(F.num(satellite.tle.getMeanMotion() * 86400.0 / (2.0 * FastMath.PI)),
            F.Divide(F.stringx("Revolutions"), F.stringx("Days")));
      case "SemimajorAxis":
        return miles(a);
      case "SemiminorAxis":
        return miles(a * FastMath.sqrt(1.0 - e * e));
      case "ApoapsisDistance":
        return miles(a * (1.0 + e));
      case "PeriapsisDistance":
        return miles(a * (1.0 - e));
      case "AscendingNodeLongitude":
        return AstroConvert.degreesPositive(satellite.tle.getRaan());
      case "PeriapsisArgument":
        return AstroConvert.degreesPositive(satellite.tle.getPerigeeArgument());
      case "PeriapsisLongitude":
        return AstroConvert
            .degreesPositive(satellite.tle.getRaan() + satellite.tle.getPerigeeArgument());
      case "MeanAnomaly":
        return AstroConvert.degreesPositive(satellite.tle.getMeanAnomaly());
      case "PeriodOsculating": {
        KeplerianOrbit orbit =
            new KeplerianOrbit(new TimeStampedPVCoordinates(state.date, state.pv()),
                state.frame(), TLEPropagator.getMU());
        return F.Quantity(F.num(orbit.getKeplerianPeriod() / 60.0), F.stringx("Minutes"));
      }
      case "Position": {
        GeodeticPoint point = state.subpoint();
        return GeoPositionExpr.newInstance(FastMath.toDegrees(point.getLatitude()),
            FastMath.toDegrees(point.getLongitude()), point.getAltitude());
      }
      case "ElevationWGS84":
        return miles(state.subpoint().getAltitude());
      case "Velocity":
        // inertial speed in the TEME frame
        return F.Quantity(F.num(state.pv().getVelocity().getNorm() * 3600.0 / METERS_PER_MILE),
            F.Divide(F.stringx("Miles"), F.stringx("Hours")));
      case "Altitude":
      case "Azimuth":
      case "DistanceFromLocation": {
        TopocentricFrame station = state.station();
        if (station == null) {
          return F.Missing(F.stringx("NotAvailable"));
        }
        Vector3D position = state.pv().getPosition();
        if ("Altitude".equals(name)) {
          return AstroConvert.degrees(station.getElevation(position, state.frame(), state.date));
        }
        if ("Azimuth".equals(name)) {
          return AstroConvert
              .degreesPositive(station.getAzimuth(position, state.frame(), state.date));
        }
        return miles(station.getRange(position, state.frame(), state.date));
      }
      default:
        return null;
    }
  }

  /**
   * The four digit launch year. Orekit already expands the two digits of a parsed element set (57
   * is 1957); the expansion here only covers a TLE built with a two digit year.
   */
  private static int internationalYear(SatelliteTle satellite) {
    int year = satellite.tle.getLaunchYear();
    if (year >= 100) {
      return year;
    }
    return year >= 57 ? 1900 + year : 2000 + year;
  }

  /** International mile in meters. */
  private static final double METERS_PER_MILE = 1609.344;

  /** Distances are given in miles, the unit of the satellite data elsewhere. */
  private static IExpr miles(double meters) {
    return F.Quantity(F.num(meters / METERS_PER_MILE), F.stringx("Miles"));
  }

  public static void initialize() {
    Initializer.init();
  }

  private SatelliteFunctions() {}
}
