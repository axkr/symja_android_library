package org.matheclipse.dataset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;

/** The country and city tables of <code>CountryData</code> and <code>CityData</code>. */
public class GeoDataTest {

  @BeforeEach
  public void setUp() throws Exception {
    F.initSymja();
    DatasetInit.init();
    F.await();
  }

  private static void check(String input, String expected) {
    EvalEngine engine = EvalEngine.get();
    assertEquals(expected, engine.evaluate("ToString(" + input + ", InputForm)").toString());
  }

  @Test
  public void testCountryEntities() {
    // a country is named by its entity name, its name or one of its ISO codes
    check("{CountryData(\"FRA\"), CountryData(\"no\"), CountryData(\"Czech Republic\"), "
        + "CountryData(Entity(\"Country\", \"Peru\"))}", //
        "{Entity(\"Country\",\"France\"),Entity(\"Country\",\"Norway\"),"
            + "Entity(\"Country\",\"CzechRepublic\"),Entity(\"Country\",\"Peru\")}");
    check("Length /@ {CountryData(), CountryData(\"Oceania\"), CountryData(\"SouthAmerica\")}", //
        "{249,25,14}");
    // Antarctica is known by name, but it is not listed among the countries of a continent
    check("{CountryData(\"Antarctica\"), MemberQ(CountryData(), Entity(\"Country\", \"Antarctica\")), "
        + "CountryData(EntityClass(\"Country\", \"Antarctica\")), "
        + "Length(CountryData(\"Continents\"))}", //
        "{Entity(\"Country\",\"Antarctica\"),False,{},7}");
    check("CountryData(\"Atlantis\")", //
        "CountryData(\"Atlantis\")");
  }

  @Test
  public void testCountryProperties() {
    check("{CountryData(\"Norway\", \"CountryCode\"), CountryData(\"Norway\", \"UNCode\"), "
        + "CountryData(\"Kenya\", \"Continent\"), CountryData(\"Kenya\", \"FullName\")}", //
        "{\"NO\",\"NOR\",EntityClass(\"Country\",\"Africa\"),\"Republic of Kenya\"}");
    // codes which do not exist for a country
    check("{CountryData(\"Kosovo\", \"CountryCode\"), CountryData(\"GazaStrip\", \"UNCode\")}", //
        "{Missing(NotAvailable),Missing(NotApplicable)}");
    check("CountryData(\"Italy\", \"BorderingCountries\")", //
        "{Entity(\"Country\",\"Austria\"),Entity(\"Country\",\"France\"),"
            + "Entity(\"Country\",\"SanMarino\"),Entity(\"Country\",\"Slovenia\"),"
            + "Entity(\"Country\",\"Switzerland\"),Entity(\"Country\",\"VaticanCity\")}");
    check("CountryData({\"Chad\", \"Mali\"}, \"UNCode\")", //
        "{\"TCD\",\"MLI\"}");
    // an unknown property and a list of properties are no property
    check("{CountryData(\"Japan\", \"NoSuchThing\"), CountryData(\"Chad\", {\"Name\", \"UNCode\"}), "
        + "CountryData(\"Asia\", \"NoSuchThing\")}", //
        "{CountryData(\"Japan\",\"NoSuchThing\"),CountryData(\"Chad\",{\"Name\",\"UNCode\"}),"
            + "CountryData(\"Asia\",\"NoSuchThing\")}");
    check("EntityValue(Entity(\"Country\", \"Spain\"), \"UNCode\")", //
        "\"ESP\"");
  }

  @Test
  public void testCountryStatistics() {
    check("{CountryData(\"Japan\", {\"Population\", 1990}), "
        + "CountryData(\"Iceland\", {\"LandArea\", 2000}), "
        + "CountryData(\"Iceland\", {\"Population\", 1900})}", //
        "{Quantity(123478000,\"People\"),Quantity(100250.0`,\"Kilometers\"^2),"
            + "Missing(NotAvailable)}");
    // the GDP is a rate per year
    check("{QuantityUnit(CountryData(\"Chile\", \"GDP\")), Head(CountryData(\"Chile\", \"Area\"))}", //
        "{\"USDollars\"/\"Years\",Quantity}");
    // no one lives in Antarctica
    check("{CountryData(\"Antarctica\", \"Population\"), CountryData(\"Antarctica\", {\"Population\", 2000})}", //
        "{Missing(\"NoResidentPopulation\"),Missing(\"NoResidentPopulation\")}");
    // a property of a class is the list of the property of its members
    check("Length(CountryData(\"Oceania\", \"Population\"))", //
        "25");
    check("CountryData(EntityClass(\"Country\", \"EU\"), \"CountryCode\")[[;;4]]", //
        "{\"AT\",\"BE\",\"BG\",\"HR\"}");
    // a time series of the years in a range
    check("Head /@ {CountryData(\"Chile\", {{\"Population\"}, {2000, 2002}}), "
        + "CountryData(\"Chile\", {\"GDP\", {2000, 2002}})}", //
        "{TemporalData,TemporalData}");
  }

  @Test
  public void testCountryShape() {
    check("s = CountryData(\"Luxembourg\", \"Shape\"); "
        + "{Head(s), s[[1, 1]], s[[1, 2]], Head(s[[1, -1]]), Dimensions(s[[1, -1, 1]])}", //
        "{Graphics,RGBColor(0.896`,0.8878`,0.8548`),EdgeForm(GrayLevel(0.0`)),Polygon,{1,17,2}}");
    // the projection is centred on the country, in radians
    check("s = CountryData(\"Luxembourg\", \"Shape\"); Max(Abs(Flatten(s[[1, -1, 1]]))) < 0.01", //
        "True");
  }

  @Test
  public void testCities() {
    check("CityData({All, \"Norway\"})[[1]]", //
        "Entity(\"City\",{\"Oslo\",\"Oslo\",\"Norway\"})");
    check("CityData({All, \"Bavaria\", \"Germany\"})[[;;2]]", //
        "{Entity(\"City\",{\"Munich\",\"Bavaria\",\"Germany\"}),"
            + "Entity(\"City\",{\"Nuremberg\",\"Bavaria\",\"Germany\"})}");
    check("{CityData(\"Lyon\", \"Country\"), Head(CityData(\"Lyon\", \"Population\")), "
        + "CityData(\"Lyon\", \"Coordinates\")}", //
        "{Entity(\"Country\",\"France\"),Quantity,{45.75`,4.85`}}");
    // a country without first-level divisions is the region of its cities
    check("CityData({All, \"Gibraltar\"})", //
        "{Entity(\"City\",{\"Gibraltar\",\"Gibraltar\",\"Gibraltar\"})}");
    // the second argument is a property, never a country
    check("{CityData(\"Lyon\", \"France\"), Check(CityData(\"Lyon\", \"Altitude\"), failed)}", //
        "{CityData(\"Lyon\",\"France\"),failed}");
    check("CityData({\"Lilliput\", \"France\"}, \"Population\")", //
        "Missing(NotAvailable)");
  }
}
