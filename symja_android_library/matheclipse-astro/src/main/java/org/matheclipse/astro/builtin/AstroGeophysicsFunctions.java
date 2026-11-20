package org.matheclipse.astro.builtin;

import java.time.Year;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleFunction;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.errors.OrekitException;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.models.earth.GeoMagneticElements;
import org.orekit.models.earth.GeoMagneticField;
import org.orekit.models.earth.GeoMagneticFieldFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateTimeComponents;
import org.orekit.time.TimeScalesFactory;

/**
 * Models of the Earth's fields: <code>GeomagneticModelData</code> on the World Magnetic Model and
 * IGRF, and <code>GeogravityModelData</code> on the EIGEN-6S spherical harmonic gravity field, both
 * evaluated by Orekit from the coefficient files bundled in this module.
 *
 * <p>
 * Locations are WGS84 geodetic positions. Symja has no terrain elevation model, so a position
 * without a height is taken on the ellipsoid; at an inland site the ground can be a few hundred
 * meters higher, which weakens both fields slightly. Both functions describe a field vector the same way: its
 * magnitude, its components in the local north-east-down frame, the horizontal component, the
 * declination (the bearing of the horizontal component, east of north), the inclination (the angle
 * below the horizontal) and the potential.
 */
public class AstroGeophysicsFunctions {

  /** The properties of a field vector, in the order of the default association. */
  private static final String[] FIELD_PROPERTIES = {"Magnitude", "NorthComponent", "EastComponent",
      "DownComponent", "HorizontalComponent", "Declination", "Inclination", "Potential"};

  private static final String NANOTESLAS = "Nanoteslas";

  /** First year of the bundled WMM-2025. */
  private static final int WMM_FROM = 2025;

  /** First year of the bundled IGRF-14. */
  private static final int IGRF_FROM = 1900;

  /** End of validity of both bundled magnetic models (exclusive). */
  private static final int MODELS_TO = 2030;

  /** Degree and order of the bundled EIGEN-6S file. */
  private static final int GRAVITY_DEGREE = 240;

  /** Angular velocity of the Earth (GRS80/WGS84 value), rad/s. */
  private static final double EARTH_ROTATION_RATE = 7.292115e-5;

  /** The gravity field provider; reading the file is the expensive part, so it is kept. */
  private static final Map<Integer, NormalizedSphericalHarmonicsProvider> GRAVITY_PROVIDERS =
      new ConcurrentHashMap<Integer, NormalizedSphericalHarmonicsProvider>();

  private static class Initializer {

    private static void init() {
      S.GeomagneticModelData.setEvaluator(new GeomagneticModelData());
      S.GeogravityModelData.setEvaluator(new GeogravityModelData());
    }
  }

  /** The arguments of a model data function after the location. */
  private static final class Spec {
    GeodeticPoint point;
    AbsoluteDate date;
    IExpr property;
    IExpr method;
  }

  /**
   * Split <code>f(loc, [date], [property], options...)</code>. An unknown option is reported and
   * ignored. A list of locations would be a region, which is not supported, and a list of
   * properties is not accepted either.
   *
   * @return <code>null</code> if the arguments cannot be read
   */
  private static Spec parse(IBuiltInSymbol symbol, IAST ast, boolean withDate,
      EvalEngine engine) {
    Spec spec = new Spec();
    spec.point = AstroConvert.toGeodeticPoint(ast.arg1());
    if (spec.point == null) {
      return null;
    }
    for (int i = 2; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST()) {
        if (arg.first() == S.Method) {
          spec.method = arg.second();
        } else {
          // Unknown option `1` in `2`.
          Errors.printMessage(symbol, "optx", F.List(arg, symbol), engine);
        }
        continue;
      }
      // a single property; a list of them is left unevaluated
      if (spec.property == null && arg.isString()) {
        spec.property = arg;
        continue;
      }
      if (withDate && spec.date == null) {
        AbsoluteDate date = AstroConvert.toAbsoluteDate(arg);
        if (date != null) {
          spec.date = date;
          continue;
        }
      }
      return null;
    }
    return spec;
  }

  /**
   * One value for a property name, and an association of all available properties when none is
   * given. An unknown name leaves the call unevaluated.
   *
   * @param values the property values in the order of {@link #FIELD_PROPERTIES}; a
   *        <code>null</code> entry is a property the model cannot give
   */
  private static IExpr select(IExpr[] values, IExpr property) {
    if (property == null) {
      IASTAppendable rules = F.ListAlloc(FIELD_PROPERTIES.length);
      for (int i = 0; i < FIELD_PROPERTIES.length; i++) {
        if (values[i] != null) {
          rules.append(F.Rule(F.stringx(FIELD_PROPERTIES[i]), values[i]));
        }
      }
      return F.assoc(rules);
    }
    String name = property.toString();
    for (int i = 0; i < FIELD_PROPERTIES.length; i++) {
      if (FIELD_PROPERTIES[i].equals(name)) {
        return values[i] == null ? F.Missing(F.stringx("NotAvailable")) : values[i];
      }
    }
    return F.NIL;
  }

  /**
   * The values of {@link #FIELD_PROPERTIES} for a field vector given by its north, east and down
   * components.
   *
   * @param unit makes a quantity of a component
   * @param potential the potential, or <code>null</code> if it is not available
   */
  private static IExpr[] fieldValues(double north, double east, double down,
      DoubleFunction<IExpr> unit, IExpr potential) {
    double horizontal = FastMath.hypot(north, east);
    return new IExpr[] { //
        unit.apply(FastMath.sqrt(horizontal * horizontal + down * down)), //
        unit.apply(north), //
        unit.apply(east), //
        unit.apply(down), //
        unit.apply(horizontal), //
        AstroConvert.degrees(FastMath.atan2(east, north)), //
        AstroConvert.degrees(FastMath.atan2(down, horizontal)), //
        potential};
  }

  /** The year with fraction which the magnetic models are parametrised by. */
  static double decimalYear(AbsoluteDate date) {
    DateTimeComponents components = date.getComponents(TimeScalesFactory.getUTC());
    int year = components.getDate().getYear();
    double days = Year.isLeap(year) ? 366.0 : 365.0;
    double dayOfYear = components.getDate().getDayOfYear() - 1
        + components.getTime().getSecondsInLocalDay() / 86400.0;
    return year + dayOfYear / days;
  }

  /**
   * <code>GeomagneticModelData(loc, date, property)</code> - the Earth's main magnetic field at a
   * location and date. The date defaults to now and the property to all of them. The model is the
   * World Magnetic Model WMM-2025 from 2025 to 2030 and IGRF-14 from 1900 to 2025;
   * <code>Method -&gt; {"Model" -&gt; "IGRF"}</code> or <code>"WMM"</code> forces one. The magnetic
   * potential is not available.
   */
  private static final class GeomagneticModelData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!AstroDataContext.checkAvailable(S.GeomagneticModelData, engine)) {
        return F.NIL;
      }
      Spec spec = parse(S.GeomagneticModelData, ast, true, engine);
      if (spec == null) {
        return F.NIL;
      }
      AbsoluteDate date = spec.date == null ? AstroConvert.nowUTC() : spec.date;
      double year = decimalYear(date);
      GeoMagneticFieldFactory.FieldModel type = magneticModel(spec.method, engine);
      if (type == null) {
        type = year >= WMM_FROM ? GeoMagneticFieldFactory.FieldModel.WMM
            : GeoMagneticFieldFactory.FieldModel.IGRF;
      }
      int from = type == GeoMagneticFieldFactory.FieldModel.WMM ? WMM_FROM : IGRF_FROM;
      if (year < from || year >= MODELS_TO) {
        // Calculations for `1` are only valid between the years `2` and `3`.
        Errors.printMessage(S.GeomagneticModelData, "modelrng",
            F.List(F.stringx(type.name()), F.ZZ(from), F.ZZ(MODELS_TO)), engine);
        return F.Missing(F.stringx("NotAvailable"));
      }
      try {
        GeoMagneticField field = GeoMagneticFieldFactory.getField(type, year);
        GeodeticPoint point = spec.point;
        GeoMagneticElements elements = field.calculateField(point.getLatitude(),
            point.getLongitude(), point.getAltitude());
        Vector3D b = elements.getFieldVector();
        IExpr[] values = fieldValues(b.getX(), b.getY(), b.getZ(),
            teslas -> F.Quantity(F.num(teslas * 1.0e9), F.stringx(NANOTESLAS)), null);
        return select(values, spec.property);
      } catch (OrekitException oex) {
        return Errors.printMessage(S.GeomagneticModelData, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    /**
     * The model of <code>Method -&gt; {"Model" -&gt; "WMM"}</code> or <code>"IGRF"</code>. Any other
     * form is reported and replaced by the automatic choice.
     *
     * @return <code>null</code> for the automatic choice by date
     */
    private static GeoMagneticFieldFactory.FieldModel magneticModel(IExpr method,
        EvalEngine engine) {
      if (method == null || method == S.Automatic) {
        return null;
      }
      IAST suboptions = method.isRuleAST() ? F.List(method) : method.isList() ? (IAST) method : null;
      if (suboptions == null) {
        // Improper model specification.
        Errors.printMessage(S.GeomagneticModelData, "model", F.CEmptyList, engine);
        // Method option `1` in `2` is not one of `3`.
        Errors.printMessage(S.GeomagneticModelData, "moptx",
            F.List(method, method, F.List(F.stringx("Model"))), engine);
        return null;
      }
      GeoMagneticFieldFactory.FieldModel model = null;
      for (IExpr suboption : suboptions) {
        if (!suboption.isRuleAST() || !suboption.first().isString("Model")) {
          // Method option `1` in `2` is not one of `3`.
          Errors.printMessage(S.GeomagneticModelData, "moptx",
              F.List(suboption, method, F.List(F.stringx("Model"))), engine);
          continue;
        }
        IExpr value = suboption.second();
        if (value.isString("WMM")) {
          model = GeoMagneticFieldFactory.FieldModel.WMM;
        } else if (value.isString("IGRF")) {
          model = GeoMagneticFieldFactory.FieldModel.IGRF;
        } else if (value != S.Automatic) {
          // Improper model specification.
          Errors.printMessage(S.GeomagneticModelData, "model", F.CEmptyList, engine);
        }
      }
      return model;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_5;
    }
  }

  /**
   * <code>GeogravityModelData(loc, property)</code> - the Earth's gravity at a location: the
   * attraction of the EIGEN-6S spherical harmonic model together with the centrifugal acceleration
   * of the Earth's rotation, as a gravimeter at rest on the ground measures it. The potential is
   * given with the sign of the potential energy, negative.
   */
  private static final class GeogravityModelData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!AstroDataContext.checkAvailable(S.GeogravityModelData, engine)) {
        return F.NIL;
      }
      Spec spec = parse(S.GeogravityModelData, ast, false, engine);
      if (spec == null) {
        return F.NIL;
      }
      try {
        NormalizedSphericalHarmonicsProvider provider = provider(GRAVITY_DEGREE);
        OneAxisEllipsoid ellipsoid = AstroConvert.earthEllipsoidGeometryOnly();
        HolmesFeatherstoneAttractionModel model =
            new HolmesFeatherstoneAttractionModel(ellipsoid.getBodyFrame(), provider);
        return select(gravity(model, ellipsoid, spec.point, provider.getMu()), spec.property);
      } catch (OrekitException oex) {
        return Errors.printMessage(S.GeogravityModelData, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }
  }

  /** The gravity field of the requested degree, frozen at J2000, read once. */
  static NormalizedSphericalHarmonicsProvider provider(int degree) {
    return GRAVITY_PROVIDERS.computeIfAbsent(degree, d -> GravityFieldFactory
        .getConstantNormalizedProvider(d, d, AbsoluteDate.J2000_EPOCH));
  }

  /**
   * The values of {@link #FIELD_PROPERTIES} for gravity at a point. The field is static, so the
   * body frame needs no Earth orientation: the position is simply the Earth-fixed Cartesian vector.
   */
  static IExpr[] gravity(HolmesFeatherstoneAttractionModel model, OneAxisEllipsoid ellipsoid,
      GeodeticPoint point, double mu) {
    Vector3D position = ellipsoid.transform(point);
    AbsoluteDate date = AbsoluteDate.J2000_EPOCH;
    // the attraction model's gradient holds only the non-central part of the acceleration; Orekit
    // keeps mu/r apart in NewtonianAttraction, so the Kepler term is added here. value() already
    // includes it.
    double r = position.getNorm();
    Vector3D acceleration =
        new Vector3D(model.gradient(date, position, mu)).add(-mu / (r * r * r), position);
    double w2 = EARTH_ROTATION_RATE * EARTH_ROTATION_RATE;
    double axisDistance2 = position.getX() * position.getX() + position.getY() * position.getY();
    double potential = model.value(date, position, mu) + 0.5 * w2 * axisDistance2;
    acceleration = acceleration.add(new Vector3D(w2 * position.getX(), w2 * position.getY(), 0));
    IExpr potentialEnergy = F.Quantity(F.num(-potential),
        F.Divide(F.stringx("Joules"), F.stringx("Kilograms")));
    return fieldValues(Vector3D.dotProduct(acceleration, point.getNorth()),
        Vector3D.dotProduct(acceleration, point.getEast()),
        -Vector3D.dotProduct(acceleration, point.getZenith()),
        a -> F.Quantity(F.num(a), F.Divide(F.stringx("Meters"), F.Sqr(F.stringx("Seconds")))),
        potentialEnergy);
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstroGeophysicsFunctions() {}
}
