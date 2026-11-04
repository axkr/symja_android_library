package org.matheclipse.core.data;

import java.util.Locale;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>CityData(city)</code> - the <code>Entity("City", {name, region, country})</code> of a city;
 * <code>CityData(city, property)</code> - the property <code>"Coordinates"</code>,
 * <code>"Latitude"</code>, <code>"Longitude"</code>, <code>"Country"</code> or <code>"Name"</code>
 * of the city.
 *
 * <p>
 * The city is a name, which is matched ignoring case, spaces and punctuation, a list
 * <code>{name, region, country}</code> or <code>{name, country}</code>, or its entity. The data
 * are a small table of major cities with their coordinates in degrees; any other city stays
 * unevaluated.
 */
public class CityData extends AbstractFunctionEvaluator {

  /** name, region, country, latitude, longitude */
  private static final Object[][] CITIES = { //
      {"Amsterdam", "NoordHolland", "Netherlands", 52.3667, 4.9}, //
      {"Athens", "Attica", "Greece", 37.9667, 23.7167}, //
      {"Barcelona", "Catalonia", "Spain", 41.3833, 2.1833}, //
      {"Berlin", "Berlin", "Germany", 52.5167, 13.4}, //
      {"Bern", "Bern", "Switzerland", 46.9167, 7.4667}, //
      {"Budapest", "Budapest", "Hungary", 47.5, 19.0833}, //
      {"Copenhagen", "Hovedstaden", "Denmark", 55.6667, 12.5833}, //
      {"Dublin", "Leinster", "Ireland", 53.3333, -6.25}, //
      {"Frankfurt", "Hessen", "Germany", 50.1167, 8.6833}, //
      {"Hamburg", "Hamburg", "Germany", 53.55, 10.0}, //
      {"Lisbon", "Lisbon", "Portugal", 38.7167, -9.1333}, //
      {"Madrid", "Madrid", "Spain", 40.4, -3.6833}, //
      {"Milan", "Lombardy", "Italy", 45.4667, 9.2}, //
      {"Munich", "Bavaria", "Germany", 48.1333, 11.5667}, //
      {"Oslo", "Oslo", "Norway", 59.9167, 10.75}, //
      {"Paris", "IleDeFrance", "France", 48.8667, 2.3333}, //
      {"Rome", "Lazio", "Italy", 41.9, 12.4833}, //
      {"Stockholm", "Stockholm", "Sweden", 59.3333, 18.05}, //
      {"Vienna", "Vienna", "Austria", 48.2, 16.3667}, //
      {"Warsaw", "Masovia", "Poland", 52.25, 21.0}, //
      {"Zurich", "Zurich", "Switzerland", 47.3667, 8.55}, //
      {"Cairo", "Cairo", "Egypt", 30.05, 31.25}, //
      {"Nairobi", "Nairobi", "Kenya", -1.2833, 36.8167}, //
      {"Bangkok", "Bangkok", "Thailand", 13.75, 100.5167}, //
      {"Beijing", "Beijing", "China", 39.9167, 116.3833}, //
      {"Delhi", "Delhi", "India", 28.6667, 77.2167}, //
      {"Istanbul", "Istanbul", "Turkey", 41.0167, 28.9667}, //
      {"Mumbai", "Maharashtra", "India", 19.0167, 72.85}, //
      {"Seoul", "Seoul", "SouthKorea", 37.5667, 126.9833}, //
      {"Shanghai", "Shanghai", "China", 31.2, 121.5}, //
      {"Tokyo", "Tokyo", "Japan", 35.685, 139.7514}, //
      {"Melbourne", "Victoria", "Australia", -37.8167, 144.9667}, //
      {"Sydney", "NewSouthWales", "Australia", -33.8833, 151.2167}, //
      {"Lima", "Lima", "Peru", -12.05, -77.05}, //
      {"Chicago", "Illinois", "UnitedStates", 41.85, -87.65}, //
      {"Toronto", "Ontario", "Canada", 43.7, -79.4} //
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
   * The row of the table for a city name, <code>{name, region, country}</code>,
   * <code>{name, country}</code> or its entity.
   *
   * @return <code>null</code> if the city is not in the table
   */
  private static Object[] find(IExpr spec) {
    if (spec.isAST(S.Entity, 3) && spec.first().isString("City")) {
      return find(spec.second());
    }
    String name;
    String region = null;
    String country = null;
    if (spec.isString()) {
      name = spec.toString();
    } else if (spec.isList() && ((IAST) spec).forAll(x -> x.isString())
        && spec.argSize() >= 1 && spec.argSize() <= 3) {
      name = spec.first().toString();
      if (spec.argSize() == 2) {
        country = spec.second().toString();
      } else if (spec.argSize() == 3) {
        region = spec.second().toString();
        country = ((IAST) spec).arg3().toString();
      }
    } else {
      return null;
    }
    String key = normalize(name);
    for (Object[] city : CITIES) {
      if (normalize((String) city[0]).equals(key)
          && (region == null || normalize(region).equals(normalize((String) city[1])))
          && (country == null || normalize(country).equals(normalize((String) city[2])))) {
        return city;
      }
    }
    return null;
  }

  private static IAST entity(Object[] city) {
    return F.binaryAST2(S.Entity, F.stringx("City"), F.list(F.stringx((String) city[0]),
        F.stringx((String) city[1]), F.stringx((String) city[2])));
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 1 || ast.argSize() > 2) {
      return F.NIL;
    }
    Object[] city = find(ast.arg1());
    if (city == null) {
      return F.NIL;
    }
    if (ast.isAST1()) {
      return entity(city);
    }
    if (!ast.arg2().isString()) {
      return F.NIL;
    }
    switch (ast.arg2().toString()) {
      case "Coordinates":
        return F.list(F.num((Double) city[3]), F.num((Double) city[4]));
      case "Latitude":
        return F.num((Double) city[3]);
      case "Longitude":
        return F.num((Double) city[4]);
      case "Country":
        return F.binaryAST2(S.Entity, F.stringx("Country"), F.stringx((String) city[2]));
      case "Name":
        return F.stringx((String) city[0]);
      default:
        return F.NIL;
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
