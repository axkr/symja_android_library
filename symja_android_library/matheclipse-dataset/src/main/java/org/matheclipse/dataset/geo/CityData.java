package org.matheclipse.dataset.geo;

import java.util.ArrayList;
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
import org.matheclipse.dataset.geo.CityTable.City;

/**
 * <code>CityData(name)</code> - the list of the <code>Entity("City", {name, region, country})
 * </code> of the cities with that name, the most populous first;<br>
 * <code>CityData({name, region, country})</code> - the entity of that city, or the list of the
 * matching cities;<br>
 * <code>CityData({All, country})</code> or <code>CityData({All, region, country})</code> - the
 * cities of a country or of one of its regions, the most populous first;<br>
 * <code>CityData(city, property)</code> - the property <code>"Coordinates"</code>,
 * <code>"Latitude"</code>, <code>"Longitude"</code>, <code>"Country"</code>, <code>"Name"</code>
 * or <code>"Population"</code> of the first matching city; the second argument is always a
 * property, and any other leaves the call unevaluated with a message.
 *
 * <p>
 * This is the table of the cities with a population of at least 15000 (see {@link CityTable}),
 * installed over the small table of <code>matheclipse-core</code> and read the same way: the city
 * is a name, which is matched ignoring case, spaces and punctuation, a list
 * <code>{name, region, country}</code> or <code>{name, country}</code>, or its entity; a list of a
 * name alone is no known entity. A city of a country without first-level divisions, like
 * Singapore, has the country as its region. A property of an unknown city is
 * <code>Missing("NotAvailable")</code>, and so is every component of a property of an argument
 * which is no city at all. Latitude and longitude are quantities in angular degrees, the population
 * a quantity in people.
 */
public class CityData extends AbstractFunctionEvaluator {

  /** The entity type of the cities. */
  public static final String CITY = "City";

  /**
   * The parts <code>{name}</code>, <code>{name, country}</code> or
   * <code>{name, region, country}</code> of a city name, a list of strings or a city entity; the
   * name is <code>null</code> for <code>All</code>.
   *
   * @return <code>null</code> if the argument is no city
   */
  private static String[] parts(IExpr spec) {
    if (spec.isAST(S.Entity, 3) && spec.first().isString(CITY)) {
      return parts(spec.second());
    }
    if (spec.isString()) {
      return new String[] {spec.toString()};
    }
    if (spec.isList() && spec.argSize() >= 1 && spec.argSize() <= 3) {
      IAST list = (IAST) spec;
      String[] result = new String[list.argSize()];
      for (int i = 1; i <= list.argSize(); i++) {
        IExpr part = list.get(i);
        if (part.isString()) {
          result[i - 1] = part.toString();
        } else if (i == 1 && part == S.All && list.argSize() >= 2) {
          result[0] = null;
        } else {
          return null;
        }
      }
      return result;
    }
    return null;
  }

  /** The cities matching the parts of a city, the most populous first. */
  private static List<City> find(String[] parts) {
    CityTable table = CityTable.get();
    String region = parts.length == 3 ? CountryTable.key(parts[1]) : null;
    String country = parts.length >= 2 ? CountryTable.key(parts[parts.length - 1]) : null;
    List<City> candidates =
        parts[0] == null ? table.inCountry(parts[parts.length - 1]) : table.named(parts[0]);
    List<City> result = new ArrayList<City>();
    for (City city : candidates) {
      if ((region == null || region.equals(CountryTable.key(city.region)))
          && (country == null || country.equals(CountryTable.key(city.country)))) {
        result.add(city);
      }
    }
    return result;
  }

  private static IAST entity(City city) {
    IAST name = city.region.isEmpty()
        ? F.list(F.stringx(city.entityName), F.stringx(city.country))
        : F.list(F.stringx(city.entityName), F.stringx(city.region), F.stringx(city.country));
    return Entities.entity(CITY, name);
  }

  private static IExpr degrees(double coordinate) {
    return F.Quantity(F.num(coordinate), F.stringx("AngularDegrees"));
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
      List<City> cities = find(parts);
      if (parts.length == 3 && parts[0] != null && cities.size() == 1) {
        return entity(cities.get(0));
      }
      return F.mapFunction(S.List, cities, city -> entity(city));
    }
    IExpr propertySpec = Entities.propertyOf(ast.arg2(), CITY);
    String property = propertySpec.isString() ? propertySpec.toString() : "";
    switch (property) {
      case "Coordinates":
      case "Latitude":
      case "Longitude":
      case "Country":
      case "Name":
      case "Population":
        break;
      default:
        // `1` is not a known property for `2`. Use `2`["Properties"] for a list of properties.
        return Errors.printMessage(S.CityData, "notprop", F.List(ast.arg2(), S.CityData),
            engine);
    }
    if (parts == null) {
      // no city at all
      return property.equals("Coordinates")
          ? F.list(F.Missing(S.NotAvailable), F.Missing(S.NotAvailable))
          : F.Missing(S.NotAvailable);
    }
    if (parts[0] == null) {
      // all the cities of a country
      return F.mapFunction(S.List, find(parts), city -> property(city, property));
    }
    List<City> cities = find(parts);
    if (cities.isEmpty()) {
      return F.Missing(S.NotAvailable);
    }
    return property(cities.get(0), property);
  }

  private static IExpr property(City city, String property) {
    switch (property) {
      case "Coordinates":
        return F.list(F.num(city.latitude), F.num(city.longitude));
      case "Latitude":
        return degrees(city.latitude);
      case "Longitude":
        return degrees(city.longitude);
      case "Country":
        return Entities.entity(CountryData.COUNTRY, F.stringx(city.country));
      case "Population":
        return city.population > 0 ? F.Quantity(F.ZZ(city.population), F.stringx("People"))
            : F.Missing(S.NotAvailable);
      default:
        return F.stringx(city.name);
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
