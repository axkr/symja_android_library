package org.matheclipse.dataset.geo;

import java.util.List;
import org.matheclipse.core.data.Entities;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.dataset.geo.CountryTable.Country;
import org.matheclipse.dataset.geo.CountryTable.Series;

/**
 * <code>CountryData()</code> or <code>CountryData(All)</code> - the list of the
 * <code>Entity("Country", name)</code> of all countries;<br>
 * <code>CountryData(country)</code> - the entity of a country given by its name, entity name or
 * ISO code; this is also known for <code>"Antarctica"</code>, which is not listed among all
 * countries;<br>
 * <code>CountryData(class)</code> - the countries of a continent or of the class
 * <code>"EU"</code>;<br>
 * <code>CountryData("Continents")</code> - the continents as entity classes;<br>
 * <code>CountryData(country, property)</code> - a property of a country, mapped over the members
 * of a class or a list of countries;<br>
 * <code>CountryData(country, {property, year})</code> - the value of a yearly property in a
 * year;<br>
 * <code>CountryData(country, {property, {y1, y2}})</code> or
 * <code>CountryData(country, {{property}, {y1, y2}})</code> - the time series of a yearly
 * property from <code>y1</code> to <code>y2</code>.
 *
 * <p>
 * The yearly properties are <code>"Population"</code> in people, <code>"GDP"</code> in US
 * dollars per year, <code>"Area"</code> and <code>"LandArea"</code> in square kilometres; without
 * a year they give the latest value known. The other properties are <code>"Name"</code>,
 * <code>"FullName"</code>, <code>"CountryCode"</code> and <code>"UNCode"</code> (the two and three
 * letter ISO codes), <code>"Continent"</code>, <code>"BorderingCountries"</code> and
 * <code>"Shape"</code>, a graphics of the outer rings of the country. A property which has no
 * value for the country is a <code>Missing</code> value; a property which is not known leaves the
 * call unevaluated with a message.
 *
 * <p>
 * The data are read from <code>countries.tsv.gz</code>, see {@link CountryTable}.
 */
public class CountryData extends AbstractFunctionEvaluator {

  /** The entity type of the countries. */
  public static final String COUNTRY = "Country";

  /** The properties, in the order <code>CountryData("Properties")</code> gives them. */
  private static final String[] PROPERTIES = {"Area", "BorderingCountries", "Continent",
      "CountryCode", "FullName", "GDP", "LandArea", "Name", "Population", "Shape", "UNCode"};

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() > 2) {
      return F.NIL;
    }
    CountryTable table = CountryTable.get();
    if (ast.isAST0()) {
      return entities(table.countries);
    }
    IExpr arg1 = ast.arg1();
    if (ast.isAST1() && arg1.isString()) {
      switch (arg1.toString()) {
        case "Continents":
          return F.mapFunction(S.List, CountryTable.CONTINENTS, c -> entityClass(c));
        case "Properties":
          return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
        default:
          break;
      }
    }
    if (arg1.isList()) {
      // a list of countries
      return ((IAST) arg1).map(x -> engine.evaluate(ast.setAtCopy(1, x)), 1);
    }
    List<Country> members = members(table, arg1);
    if (members != null) {
      if (ast.isAST1()) {
        return entities(members);
      }
      if (!isPropertySpec(ast.arg2())) {
        return notProperty(ast.arg2(), engine);
      }
      IASTAppendable result = F.ListAlloc(members.size());
      for (Country country : members) {
        result.append(property(country, ast.arg2(), engine));
      }
      return result;
    }
    Country country = country(table, arg1);
    if (country == null) {
      if (arg1.isString() || arg1.isAST(S.Entity, 3)) {
        // `1` is not a known entity, class or tag for `2`. Use `2`[] for a list of entities.
        return Errors.printMessage(S.CountryData, "notent", F.List(arg1, S.CountryData), engine);
      }
      return F.NIL;
    }
    if (ast.isAST1()) {
      return entity(country);
    }
    if (!isPropertySpec(ast.arg2())) {
      return notProperty(ast.arg2(), engine);
    }
    return property(country, ast.arg2(), engine);
  }

  private static IExpr notProperty(IExpr spec, EvalEngine engine) {
    // `1` is not a known property for `2`. Use `2`["Properties"] for a list of properties.
    return Errors.printMessage(S.CountryData, "notprop", F.List(spec, S.CountryData), engine);
  }

  private static boolean isProperty(IExpr name) {
    if (name.isString()) {
      for (String property : PROPERTIES) {
        if (property.equals(name.toString())) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isYearly(IExpr name) {
    return name.isString() && Series.isSeries(name.toString());
  }

  /** Whether a year range is <code>{y1, y2}</code> with machine-sized integer years. */
  private static boolean isYearRange(IExpr range) {
    return range.isList2() && range.first().toIntDefault() != Integer.MIN_VALUE
        && range.second().toIntDefault() != Integer.MIN_VALUE;
  }

  /**
   * Whether a property specification is a property, <code>{yearly, year}</code>,
   * <code>{yearly, {y1, y2}}</code> or <code>{{yearly, ...}, {y1, y2}}</code>.
   */
  private static boolean isPropertySpec(IExpr spec) {
    spec = Entities.propertyOf(spec, COUNTRY);
    if (spec.isString()) {
      return isProperty(spec);
    }
    if (!spec.isList2()) {
      return false;
    }
    IExpr first = spec.first();
    IExpr second = spec.second();
    if (isYearly(first)) {
      return second.toIntDefault() != Integer.MIN_VALUE || isYearRange(second);
    }
    return first.isList() && first.argSize() > 0 && ((IAST) first).forAll(x -> isYearly(x))
        && isYearRange(second);
  }

  /** The country an argument names, or <code>null</code>. */
  private static Country country(CountryTable table, IExpr arg) {
    IExpr name = Entities.nameOf(arg, COUNTRY);
    return name.isString() ? table.country(name.toString()) : null;
  }

  /**
   * The countries of <code>All</code>, of an <code>EntityClass("Country", class)</code> or of the
   * name of a class which is not also the name of a country.
   *
   * @return <code>null</code> if the argument is no class
   */
  private static List<Country> members(CountryTable table, IExpr arg) {
    if (arg == S.All) {
      return table.countries;
    }
    if (arg.isAST(S.EntityClass, 3) && arg.first().isString(COUNTRY) && arg.second().isString()) {
      return classMembers(table, arg.second().toString());
    }
    if (arg.isString() && table.country(arg.toString()) == null) {
      return classMembers(table, arg.toString());
    }
    return null;
  }

  private static List<Country> classMembers(CountryTable table, String name) {
    List<Country> members = table.members(name);
    if (members != null) {
      return members;
    }
    String key = CountryTable.key(name);
    for (String className : table.classes.keySet()) {
      if (CountryTable.key(className).equals(key)) {
        return table.members(className);
      }
    }
    return "europeanunion".equals(key) ? table.members("EU") : null;
  }

  /**
   * A property of a country for a specification which {@link #isPropertySpec(IExpr)} accepts.
   */
  private static IExpr property(Country country, IExpr spec, EvalEngine engine) {
    spec = Entities.propertyOf(spec, COUNTRY);
    if (spec.isString()) {
      return property(country, spec.toString());
    }
    IExpr first = spec.first();
    IExpr second = spec.second();
    if (second.isList()) {
      int from = second.first().toIntDefault();
      int to = second.second().toIntDefault();
      if (first.isString()) {
        return timeSeries(country, first.toString(), from, to, engine);
      }
      IAST properties = (IAST) first;
      if (properties.argSize() == 1) {
        return timeSeries(country, properties.arg1().toString(), from, to, engine);
      }
      return properties.map(p -> timeSeries(country, p.toString(), from, to, engine), 1);
    }
    Series series = country.series(first.toString());
    return quantity(first.toString(), series, series.value(second.toIntDefault()));
  }

  private static IExpr property(Country country, String property) {
    switch (property) {
      case "Name":
        return F.stringx(country.name);
      case "FullName":
        return F.stringx(country.fullName);
      case "CountryCode":
        return text(country.code);
      case "UNCode":
        return text(country.unCode);
      case "Continent":
        return country.continent.isEmpty() ? F.Missing(S.NotAvailable)
            : entityClass(country.continent);
      case "BorderingCountries":
        return F.mapFunction(S.List, country.neighbours,
            n -> Entities.entity(COUNTRY, F.stringx(n)));
      case "Shape":
        return shape(country);
      default:
        Series series = country.series(property);
        if (series == null) {
          return F.Missing(S.NotAvailable);
        }
        int year = series.latestYear();
        return quantity(property, series, year < 0 ? Double.NaN : series.value(year));
    }
  }

  /** The time series of the known values of a yearly property from one year to another. */
  private static IExpr timeSeries(Country country, String property, int from, int to,
      EvalEngine engine) {
    Series series = country.series(property);
    IASTAppendable path = F.ListAlloc();
    for (int year = from; year <= to; year++) {
      double value = series.value(year);
      if (!Double.isNaN(value)) {
        IAST date = F.binaryAST2(S.DateObject, F.list(F.ZZ(year)), F.stringx("Year"));
        path.append(F.list(date, quantity(property, series, value)));
      }
    }
    if (path.isEmpty()) {
      return quantity(property, series, Double.NaN);
    }
    return engine.evaluate(F.unaryAST1(S.TimeSeries, path));
  }

  /**
   * A value of a yearly property with its unit; a missing value is <code>Missing</code> with the
   * reason the series gives, <code>"NotAvailable"</code> by default.
   */
  private static IExpr quantity(String property, Series series, double value) {
    if (Double.isNaN(value)) {
      return series.missingReason == null ? F.Missing(S.NotAvailable)
          : F.Missing(series.missingReason);
    }
    switch (property) {
      case "Population":
        return F.Quantity(F.ZZ(Math.round(value)), F.stringx("People"));
      case "GDP":
        return F.Quantity(F.num(value), F.Divide(F.stringx("USDollars"), F.stringx("Years")));
      default:
        return F.Quantity(F.num(value), F.Power(F.stringx("Kilometers"), F.C2));
    }
  }

  /**
   * The graphics of the outer rings of a country, in an equirectangular projection centred on the
   * middle of the country's bounding box: <code>x = (lon - lon0) cos(lat0)</code> and
   * <code>y = lat - lat0</code> in radians. The longitudes of a country reaching across the
   * antimeridian are taken modulo 360 degrees, so that it is drawn in one piece.
   */
  private static IExpr shape(Country country) {
    List<double[][]> rings = country.rings();
    if (rings.isEmpty()) {
      return F.Missing(S.NotAvailable);
    }
    double minLon = Double.MAX_VALUE;
    double maxLon = -Double.MAX_VALUE;
    double minLat = Double.MAX_VALUE;
    double maxLat = -Double.MAX_VALUE;
    for (double[][] ring : rings) {
      for (double[] p : ring) {
        minLon = Math.min(minLon, p[0]);
        maxLon = Math.max(maxLon, p[0]);
        minLat = Math.min(minLat, p[1]);
        maxLat = Math.max(maxLat, p[1]);
      }
    }
    final boolean wrap = maxLon - minLon > 180.0;
    if (wrap) {
      minLon = Double.MAX_VALUE;
      maxLon = -Double.MAX_VALUE;
      for (double[][] ring : rings) {
        for (double[] p : ring) {
          double lon = p[0] < 0.0 ? p[0] + 360.0 : p[0];
          minLon = Math.min(minLon, lon);
          maxLon = Math.max(maxLon, lon);
        }
      }
    }
    final double lon0 = (minLon + maxLon) / 2.0;
    final double lat0 = (minLat + maxLat) / 2.0;
    final double scaleX = Math.cos(Math.toRadians(lat0));
    IAST polygon = F.Polygon(F.mapFunction(S.List, rings, ring -> F.mapRange(0, ring.length, i -> {
      double lon = wrap && ring[i][0] < 0.0 ? ring[i][0] + 360.0 : ring[i][0];
      return F.list(F.num(Math.toRadians(lon - lon0) * scaleX),
          F.num(Math.toRadians(ring[i][1] - lat0)));
    })));
    return F.unaryAST1(S.Graphics, F.list(F.RGBColor(0.896, 0.8878, 0.8548),
        F.EdgeForm(F.GrayLevel(0.0)), polygon));
  }

  /** A code of the table, which may be a missing value like <code>Missing[NotApplicable]</code>. */
  private static IExpr text(String value) {
    if (value.isEmpty()) {
      return F.Missing(S.NotAvailable);
    }
    if (CountryTable.isMissing(value)) {
      return F.Missing(value.equals("Missing[NotApplicable]") ? S.NotApplicable : S.NotAvailable);
    }
    return F.stringx(value);
  }

  private static IAST entity(Country country) {
    return Entities.entity(COUNTRY, F.stringx(country.entity));
  }

  private static IAST entities(List<Country> countries) {
    return F.mapFunction(S.List, countries, c -> entity(c));
  }

  private static IAST entityClass(String name) {
    return F.EntityClass(F.stringx(COUNTRY), F.stringx(name));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_0_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
