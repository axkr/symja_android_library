package org.matheclipse.astro.system;

import org.junit.jupiter.api.Test;

/** The orbital elements of the planets, and a planet entity applied to a property. */
public class PlanetOrbitTest extends AbstractTestCase {

  @Test
  public void testOrbitalElements() {
    check("PlanetData(\"Earth\", \"Eccentricity\")", //
        "0.0167102");
    check("Entity(\"Planet\", \"Saturn\")[\"SemimajorAxis\"]", //
        "Quantity(9.53707,\"AstronomicalUnit\")");
    // the inner planets give their period in days, the others in Julian years
    check("Entity(\"Planet\", \"Venus\")[\"OrbitPeriod\"]", //
        "Quantity(224.7008,\"Days\")");
    check("Entity(\"Planet\", \"Uranus\")[\"OrbitPeriod\"]", //
        "Quantity(84.01685,\"JulianYears\")");
    check("EntityValue(Entity(\"Planet\", \"Neptune\"), \"Eccentricity\")", //
        "0.00858587");
  }

  @Test
  public void testHelioCoordinatesOfAnEntity() {
    check("Length(Entity(\"Planet\", \"Venus\")[\"HelioCoordinates\"])", //
        "3");
  }
}
