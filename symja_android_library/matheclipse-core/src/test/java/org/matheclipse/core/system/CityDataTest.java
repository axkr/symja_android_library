package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for the city entities and properties of <code>CityData</code>. */
public class CityDataTest extends ExprEvaluatorTestCase {

  @Test
  public void testCityDataSearch() {
    // a name is a search, which gives a list even for a single city
    check("CityData(\"Oslo\")", //
        "{Entity(City,{Oslo,Oslo,Norway})}");
    // the search name of a city may differ from the name in its entity
    check("CityData(\"mumbai\")", //
        "{Entity(City,{Bombay,Maharashtra,India})}");
    check("CityData({\"Toronto\", \"Canada\"})", //
        "{Entity(City,{Toronto,Ontario,Canada})}");
    // cities sharing a name
    check("CityData(\"paris\")", //
        "{Entity(City,{Paris,IleDeFrance,France}),Entity(City,{Paris,Texas,UnitedStates})}");
    // unknown cities give an empty list
    check("{CityData(\"Lilliput\"), CityData({\"Rome\", \"France\"}), "
        + "CityData({\"Sydney\", \"Victoria\", \"Australia\"})}", //
        "{{},{},{}}");
    // no city at all
    check("CityData(42)", //
        "CityData(42)");
    // a list of a name alone is no known entity
    check("{Check(CityData({\"Oslo\"}), failed), Check(CityData({\"Oslo\"}, \"Name\"), failed), "
        + "Quiet(CityData({\"Rome\"}))}", //
        "{failed,failed,CityData({Rome})}");
  }

  @Test
  public void testCityDataQualified() {
    check("CityData({\"Sydney\", \"Nova Scotia\", \"Canada\"})", //
        "Entity(City,{Sydney,NovaScotia,Canada})");
    check("CityData(Entity(\"City\", {\"Madrid\", \"Madrid\", \"Spain\"}))", //
        "Entity(City,{Madrid,Madrid,Spain})");
  }

  @Test
  public void testCityDataProperties() {
    check("{CityData(\"Oslo\", \"Latitude\"), CityData(\"Lima\", \"Longitude\")}", //
        "{Quantity(59.91,\"AngularDegrees\"),Quantity(-77.02833,\"AngularDegrees\")}");
    check("CityData({\"Toronto\", \"Ontario\", \"Canada\"}, \"Coordinates\")", //
        "{43.65,-79.38}");
    check("{CityData(Entity(\"City\", {\"Bombay\", \"Maharashtra\", \"India\"}), \"Name\"), "
        + "CityData(\"Sydney\", \"Country\")}", //
        "{Mumbai,Entity(Country,Australia)}");
    check("{CityData({\"Paris\", \"Texas\", \"UnitedStates\"}, \"Latitude\"), "
        + "CityData({\"Paris\", \"UnitedStates\"}, \"Country\")}", //
        "{Quantity(33.66814,\"AngularDegrees\"),Entity(Country,UnitedStates)}");
    // the coordinates of some smaller cities sharing a name are not available
    check("CityData(Entity(\"City\", {\"Sydney\", \"NovaScotia\", \"Canada\"}), \"Coordinates\")", //
        "Missing(NotAvailable)");
    // an unknown city
    check("{CityData(\"Lilliput\", \"Coordinates\"), CityData(\"Lilliput\", \"Latitude\")}", //
        "{Missing(NotAvailable),Missing(NotAvailable)}");
    // no city at all: every component is missing
    check("{CityData(f(y), \"Coordinates\"), CityData(7, \"Name\")}", //
        "{{Missing(NotAvailable),Missing(NotAvailable)},Missing(NotAvailable)}");
    // an unknown property
    check("CityData(\"Oslo\", \"Elevation\")", //
        "CityData(Oslo,Elevation)");
  }
}
