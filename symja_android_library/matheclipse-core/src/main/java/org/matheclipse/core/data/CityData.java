package org.matheclipse.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

/**
 * <code>CityData(name)</code> - the list of the <code>Entity("City", {name, region, country})
 * </code> of the cities with that name;<br>
 * <code>CityData({name, region, country})</code> - the entity of that city, or the list of the
 * matching cities;<br>
 * <code>CityData(city, property)</code> - the property <code>"Coordinates"</code>,
 * <code>"Latitude"</code>, <code>"Longitude"</code>, <code>"Country"</code> or <code>"Name"</code>
 * of the first matching city.
 *
 * <p>
 * The city is a name, which is matched ignoring case, spaces and punctuation, a list
 * <code>{name, region, country}</code> or <code>{name, country}</code>, or its entity; a list of
 * a name alone is no known entity. Only a fully qualified city which names exactly one city gives
 * its entity; the other forms search the table and give a list, which is empty for an unknown
 * city. A property of an unknown city is
 * <code>Missing("NotAvailable")</code>, and so is every component of a property of an argument
 * which is no city at all. Latitude and longitude are quantities in angular degrees.
 *
 * <p>
 * The data are a small table of major cities with their coordinates in degrees and a few other
 * cities sharing their names, some without coordinates; the lists of cities sharing a name are
 * incomplete.
 */
public class CityData extends AbstractFunctionEvaluator {

  /**
   * search name, entity name, region, country, latitude, longitude; the coordinates are
   * <code>null</code> if not available. The cities sharing a name follow the major city of that
   * name.
   */
  private static final Object[][] CITIES = { //
      {"Amsterdam", "Amsterdam", "NoordHolland", "Netherlands", 52.37, 4.89}, //
      {"Athens", "Athens", "Attiki", "Greece", 37.98, 23.73}, //
      {"Barcelona", "Barcelona", "Barcelona", "Spain", 41.4, 2.17}, //
      {"Berlin", "Berlin", "Berlin", "Germany", 52.52, 13.38}, //
      {"Bern", "Bern", "Bern", "Switzerland", 46.95, 7.44}, //
      {"Budapest", "Budapest", "Budapest", "Hungary", 47.51, 19.08}, //
      {"Copenhagen", "Copenhagen", "Copenhagen", "Denmark", 55.68, 12.57}, //
      {"Dublin", "Dublin", "Dublin", "Ireland", 53.33, -6.25}, //
      {"Frankfurt", "Frankfurt", "Hesse", "Germany", 50.12, 8.68}, //
      {"Hamburg", "Hamburg", "Hamburg", "Germany", 53.55, 10.0}, //
      {"Lisbon", "Lisbon", "Lisboa", "Portugal", 38.72, -9.14}, //
      {"Madrid", "Madrid", "Madrid", "Spain", 40.42, -3.71}, //
      {"Milan", "Milan", "Lombardy", "Italy", 45.47, 9.17}, //
      {"Munich", "Munich", "Bavaria", "Germany", 48.14, 11.58}, //
      {"Oslo", "Oslo", "Oslo", "Norway", 59.91, 10.75}, //
      {"Paris", "Paris", "IleDeFrance", "France", 48.86, 2.34}, //
      {"Paris", "Paris", "Texas", "UnitedStates", 33.6681441, -95.5439795}, //
      {"Rome", "Rome", "Lazio", "Italy", 41.89, 12.5}, //
      {"Stockholm", "Stockholm", "Stockholm", "Sweden", 59.33, 18.07}, //
      {"Vienna", "Vienna", "Vienna", "Austria", 48.22, 16.37}, //
      {"Warsaw", "Warsaw", "Mazowieckie", "Poland", 52.26, 21.02}, //
      {"Zurich", "Zurich", "Zurich", "Switzerland", 47.38, 8.54}, //
      {"Cairo", "Cairo", "Cairo", "Egypt", 30.06, 31.25}, //
      {"Nairobi", "Nairobi", "Nairobi", "Kenya", -1.29, 36.82}, //
      {"Bangkok", "Bangkok", "Bangkok", "Thailand", 13.73, 100.5}, //
      {"Beijing", "Beijing", "Beijing", "China", 39.93, 116.4}, //
      {"Delhi", "Delhi", "Delhi", "India", 28.67, 77.21}, //
      {"Istanbul", "Istanbul", "Istanbul", "Turkey", 41.1, 29.0}, //
      {"Mumbai", "Bombay", "Maharashtra", "India", 18.96, 72.82}, //
      {"Seoul", "Seoul", "Seoul", "SouthKorea", 37.55195533743872, 126.99171575152394}, //
      {"Shanghai", "Shanghai", "Shanghai", "China", 31.23, 121.47}, //
      {"Tokyo", "Tokyo", "Tokyo", "Japan", 35.67, 139.77}, //
      {"Melbourne", "Melbourne", "Victoria", "Australia", -37.81, 144.96}, //
      {"Sydney", "Sydney", "NewSouthWales", "Australia", -33.87, 151.21}, //
      {"Sydney", "Sydney", "NovaScotia", "Canada", null, null}, //
      {"Lima", "Lima", "Lima", "Peru", -12.043333, -77.028333}, //
      {"Chicago", "Chicago", "Illinois", "UnitedStates", 41.8375511, -87.6818441}, //
      {"Toronto", "Toronto", "Ontario", "Canada", 43.65, -79.38} //
  };

  /** Lower case letters and digits only, for matching names. */
  private static String normalize(String name) {
    StringBuilder buf = new StringBuilder(name.length());
    for (char c : name.toLowerCase(Locale.ROOT).toCharArray()) {
      if (Character.isLetterOrDigit(c)) {
        buf.append(c);
      }
    }
    return buf.toString();
  }

  /**
   * The parts <code>{name}</code>, <code>{name, country}</code> or
   * <code>{name, region, country}</code> of a city name, a list of strings or a city entity.
   *
   * @return <code>null</code> if the argument is no city
   */
  private static String[] parts(IExpr spec) {
    if (spec.isAST(S.Entity, 3) && spec.first().isString("City")) {
      return parts(spec.second());
    }
    if (spec.isString()) {
      return new String[] {spec.toString()};
    }
    if (spec.isList() && spec.argSize() >= 1 && spec.argSize() <= 3
        && ((IAST) spec).forAll(x -> x.isString())) {
      String[] result = new String[spec.argSize()];
      for (int i = 0; i < result.length; i++) {
        result[i] = ((IAST) spec).get(i + 1).toString();
      }
      return result;
    }
    return null;
  }

  /** The rows of the table matching the parts of a city, in the order of the table. */
  private static List<Object[]> find(String[] parts) {
    String key = normalize(parts[0]);
    String region = parts.length == 3 ? normalize(parts[1]) : null;
    String country = parts.length >= 2 ? normalize(parts[parts.length - 1]) : null;
    List<Object[]> result = new ArrayList<Object[]>();
    for (Object[] city : CITIES) {
      if ((normalize((String) city[0]).equals(key) || normalize((String) city[1]).equals(key))
          && (region == null || region.equals(normalize((String) city[2])))
          && (country == null || country.equals(normalize((String) city[3])))) {
        result.add(city);
      }
    }
    return result;
  }

  private static IAST entity(Object[] city) {
    return F.binaryAST2(S.Entity, F.stringx("City"), F.list(F.stringx((String) city[1]),
        F.stringx((String) city[2]), F.stringx((String) city[3])));
  }

  /** A coordinate of the city in angular degrees, if available. */
  private static IExpr degrees(Object coordinate) {
    if (coordinate == null) {
      return F.Missing(S.NotAvailable);
    }
    return F.Quantity(F.num((Double) coordinate), F.stringx("AngularDegrees"));
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 1 || ast.argSize() > 2) {
      return F.NIL;
    }
    String[] parts = parts(ast.arg1());
    if (parts != null && parts.length == 1 && ast.arg1().isList()) {
      // a list of a name alone is no search
      // `1` is not a known entity, class or tag for `2`. Use `2`[] for a list of entities.
      return Errors.printMessage(S.CityData, "notent", F.List(ast.arg1(), S.CityData), engine);
    }
    if (ast.isAST1()) {
      if (parts == null) {
        return F.NIL;
      }
      List<Object[]> cities = find(parts);
      if (parts.length == 3 && cities.size() == 1) {
        return entity(cities.get(0));
      }
      IASTAppendable result = F.ListAlloc(cities.size());
      for (Object[] city : cities) {
        result.append(entity(city));
      }
      return result;
    }
    if (!ast.arg2().isString()) {
      return F.NIL;
    }
    String property = ast.arg2().toString();
    switch (property) {
      case "Coordinates":
      case "Latitude":
      case "Longitude":
      case "Country":
      case "Name":
        break;
      default:
        return F.NIL;
    }
    if (parts == null) {
      // no city at all
      return property.equals("Coordinates")
          ? F.list(F.Missing(S.NotAvailable), F.Missing(S.NotAvailable))
          : F.Missing(S.NotAvailable);
    }
    List<Object[]> cities = find(parts);
    if (cities.isEmpty()) {
      return F.Missing(S.NotAvailable);
    }
    Object[] city = cities.get(0);
    switch (property) {
      case "Coordinates":
        if (city[4] == null) {
          return F.Missing(S.NotAvailable);
        }
        return F.list(F.num((Double) city[4]), F.num((Double) city[5]));
      case "Latitude":
        return degrees(city[4]);
      case "Longitude":
        return degrees(city[5]);
      case "Country":
        return F.binaryAST2(S.Entity, F.stringx("Country"), F.stringx((String) city[3]));
      default:
        return F.stringx((String) city[0]);
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
