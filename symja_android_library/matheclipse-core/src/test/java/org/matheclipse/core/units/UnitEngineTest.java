package org.matheclipse.core.units;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/** Engine-level tests for the units registry and algebraic conversion (no evaluator wiring). */
public class UnitEngineTest {

  @Test
  public void testResolveNames() {
    UnitRegistry r = UnitRegistry.get();
    assertEquals("Meters", r.resolve("Meters").name);
    assertEquals("Meters", r.resolve("m").name);
    assertEquals("Meters", r.resolve("meter").name);
    assertEquals("Meters", r.resolve("meters").name);
    assertEquals("Kilograms", r.resolve("kg").name);
    assertEquals("Grams", r.resolve("g").name);
    assertEquals("Minutes", r.resolve("min").name); // must not split as milli-"in"
    assertEquals("Ohms", r.resolve("Ohm").name); // legacy si.properties alias
    assertEquals("Feet", r.resolve("ft").name);
    assertEquals("Feet", r.resolve("Foot").name);
    // dynamic prefix splitting
    assertEquals("Kilometers", r.resolve("Kilometers").name);
    assertEquals("Kilometers", r.resolve("km").name);
    assertEquals("Kiloohms", r.resolve("kOhm").name);
    assertEquals("Microfarads", r.resolve("uF").name);
    assertEquals("Megaparsecs", r.resolve("Megaparsecs").name);
    assertNull(r.resolve("Blah"));
    assertNull(r.resolve(""));
  }

  @Test
  public void testConvertMagnitude() {
    EvalEngine engine = new EvalEngine(true);
    // 100 yards = 2286/25 meters (exact)
    IExpr m = Units.convertMagnitude(F.ZZ(100), F.stringx("Yards"), F.stringx("Meters"), engine);
    assertEquals("2286/25", m.toString());
    // 1 hour = 60 minutes
    assertEquals("60",
        Units.convertMagnitude(F.C1, F.stringx("Hours"), F.stringx("Minutes"), engine).toString());
    // incompatible dimensions
    assertTrue(Units
        .convertMagnitude(F.C1, F.stringx("Meters"), F.stringx("Seconds"), engine).isNIL());
    // Pi radians = 180 degrees - the Pi in the ratio must cancel against the magnitude
    assertEquals("180", Units
        .convertMagnitude(F.Pi, F.stringx("Radians"), F.stringx("AngularDegrees"), engine)
        .toString());
    // compound target: 1 mile/hour in meters/seconds = 1397/3125 (= 0.44704)
    IExpr mph = F.Times(F.stringx("Miles"), F.Power(F.stringx("Hours"), F.CN1));
    IExpr mps = F.Times(F.stringx("Meters"), F.Power(F.stringx("Seconds"), F.CN1));
    assertEquals("1397/3125", Units.convertMagnitude(F.C1, mph, mps, engine).toString());
  }

  @Test
  public void testTemperatureConversion() {
    EvalEngine engine = new EvalEngine(true);
    // 0 degC = 5463/20 K = 273.15 K
    assertEquals("5463/20", Units
        .convertMagnitude(F.C0, F.stringx("DegreesCelsius"), F.stringx("Kelvins"), engine)
        .toString());
    // 451 degF = 2095/9 degC (= 232.77...)
    assertEquals("2095/9",
        Units.convertMagnitude(F.ZZ(451), F.stringx("DegreesFahrenheit"),
            F.stringx("DegreesCelsius"), engine).toString());
    // difference units convert purely by scale: 12 degC-diff = 12 K
    assertEquals("12", Units.convertMagnitude(F.ZZ(12), F.stringx("DegreesCelsiusDifference"),
        F.stringx("Kelvins"), engine).toString());
    // absolute -> difference is allowed (relabel via Kelvins)
    assertEquals("5463/20", Units.convertMagnitude(F.C0, F.stringx("DegreesCelsius"),
        F.stringx("DegreesCelsiusDifference"), engine).toString());
    // difference -> absolute is forbidden
    assertTrue(Units.convertMagnitude(F.ZZ(3), F.stringx("DegreesCelsiusDifference"),
        F.stringx("DegreesFahrenheit"), engine).isNIL());
  }

  @Test
  public void testDimensionsAndCompatibility() {
    Map<String, IRational> newtons = Units.dimensions(F.stringx("Newtons"));
    assertEquals(3, newtons.size());
    assertEquals(F.C1, newtons.get("Kilograms"));
    assertEquals(F.C1, newtons.get("Meters"));
    assertEquals(F.ZZ(-2), newtons.get("Seconds"));
    assertTrue(Units.compatibleUnits(F.stringx("Feet"), F.stringx("Meters")));
    assertFalse(Units.compatibleUnits(F.stringx("Feet"), F.stringx("Pounds")));
    // temperature and temperature difference share the dimension
    assertTrue(
        Units.compatibleUnits(F.stringx("DegreesCelsiusDifference"), F.stringx("Kelvins")));
    // dimensionless
    assertTrue(Units.dimensions(F.stringx("Percent")).isEmpty());
    assertNull(Units.dimensions(F.stringx("Blah")));
  }

  @Test
  public void testNormalize() {
    IExpr canonical = F.stringx("Meters");
    // fixed point: identical instance comes back
    assertTrue(Units.normalize(canonical) == canonical);
    assertEquals("Meters", Units.normalize(F.stringx("m")).toString());
    assertEquals("Kilometers", Units.normalize(F.stringx("km")).toString());
    assertTrue(Units.normalize(F.stringx("Blah")).isNIL());
    assertTrue(Units.normalize(F.ZZ(3)).isNIL());
    // compound expressions normalize atom-wise
    IExpr compound = F.Times(F.stringx("m"), F.Power(F.stringx("s"), F.ZZ(-2)));
    IExpr normalized = Units.normalize(compound);
    assertTrue(normalized.isPresent());
    assertTrue(Units.normalize(F.Times(F.ZZ(2), F.stringx("m"))).isNIL());
  }

  @Test
  public void testToBaseSplit() {
    EvalEngine engine = new EvalEngine(true);
    // 1 kW*h = 3600000 J = 3600000 kg m^2/s^2
    IExpr[] split =
        Units.toBaseSplit(F.Times(F.stringx("Kilowatts"), F.stringx("Hours")), engine);
    assertEquals("3600000", split[0].toString());
    // dimensionless: Percent -> coefficient 1/100, no unit atoms
    IExpr[] percent = Units.toBaseSplit(F.stringx("Percent"), engine);
    assertEquals("1/100", percent[0].toString());
    assertTrue(percent[1].isOne());
  }
}
