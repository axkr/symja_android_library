package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class QuantityTest extends ExprEvaluatorTestCase {

  @Test
  public void testKnownUnitQ() {
    check("KnownUnitQ(\"floz\")", //
        "True");
    check("KnownUnitQ(\"Foo\")", //
        "False");
    check("KnownUnitQ(\"Meters\"^2/\"Seconds\")", //
        "True");
    check("KnownUnitQ(\"Kilometers\")", //
        "True");
    check("KnownUnitQ(3)", //
        "False");
  }

  @Test
  public void testCompatibleUnitQ() {
    check("CompatibleUnitQ(Quantity(3, \"Feet\"), Quantity(8, \"Meters\"))", //
        "True");
    check("CompatibleUnitQ(\"Feet\", \"Pounds\")", //
        "False");
    check("CompatibleUnitQ(Quantity(3, \"Feet\"), Quantity(2, \"Seconds\"))", //
        "False");
    check("CompatibleUnitQ(\"DegreesCelsiusDifference\", \"DegreesFahrenheit\")", //
        "True");
  }

  @Test
  public void testQuantityPlusSymbolic() {
    check("Quantity(a,\"m\")+Quantity(b,\"m\")//FullForm", //
        "Quantity(Plus(a, b), \"Meters\")");
    check("a+Quantity(b,\"m\")//FullForm", //
        "Plus(a, Quantity(b, \"Meters\"))");
  }

  @Test
  public void testQuantityTimesSymbolic() {
    check("Quantity(a,\"m\")*Quantity(b,\"m\")//FullForm", //
        "Quantity(Times(a, b), Power(\"Meters\", 2))");
    // a non-quantity factor is absorbed into the magnitude (WMA behavior)
    check("a*Quantity(b,\"m\")//FullForm", //
        "Quantity(Times(a, b), \"Meters\")");
  }

  @Test
  public void testQuantityList() {
    check("Quantity(Sqrt(Range(3)), \"Meters\")", //
        "{Quantity(1,\"Meters\"),Quantity(Sqrt(2),\"Meters\"),Quantity(Sqrt(3),\"Meters\")}");
  }

  @Test
  public void testQuantity() {
    check("1/Quantity(0,\"s\")^I", //
        "1/Quantity(0,\"Seconds\")^I");
    check("Quantity(50, \"min\") + Quantity(1, \"s\")", //
        "Quantity(3001,\"Seconds\")");
    check("Quantity(\"StandardAccelerationOfGravity\")", //
        "Quantity(1,\"StandardAccelerationOfGravity\")");
    check("1+(3+Quantity(1.2,\"m\"))", //
        "4+Quantity(1.2,\"Meters\")");

    // incompatible units print a Quantity::compat message and stay unevaluated
    check("Quantity(0, \"kg\") + Quantity(0, \"A\") + Quantity(0, \"m\")", //
        "Quantity(0,\"Amperes\")+Quantity(0,\"Kilograms\")+Quantity(0,\"Meters\")");

    check("N(Quantity(2/3,\"m\"))", //
        "Quantity(0.666667,\"Meters\")");

    // Plus converts to the (canonically) first argument's unit - WMA behavior
    check("Quantity(50, \"s\") + Quantity(1, \"min\")", //
        "Quantity(11/6,\"Minutes\")");
    check("Quantity(1, \"min\") + Quantity(50, \"s\")", //
        "Quantity(11/6,\"Minutes\")");
    check("Quantity(1, \"min\") + Quantity(120, \"min\")", //
        "Quantity(121,\"Minutes\")");

    check("Table(i, {i, Quantity(5, \"s\"), Quantity(1, \"m\"), Quantity(4, \"s\")})", //
        "Table(i,{i,Quantity(5,\"s\"),Quantity(1,\"m\"),Quantity(4,\"s\")})");
    check("Table(i, {i, Quantity(5, \"s\"), Quantity(1, \"min\"), Quantity(4, \"s\")})", //
        "{Quantity(5,\"Seconds\"),Quantity(9,\"Seconds\"),Quantity(13,\"Seconds\"),Quantity(17,\"Seconds\"),Quantity(21,\"Seconds\"),Quantity(25,\"Seconds\"),Quantity(29,\"Seconds\"),Quantity(33,\"Seconds\"),Quantity(37,\"Seconds\"),Quantity(41,\"Seconds\"),Quantity(45,\"Seconds\"),Quantity(49,\"Seconds\"),Quantity(53,\"Seconds\"),Quantity(57,\"Seconds\")}");
    check("Table(i, {i, Quantity(50, \"s\"), Quantity(1, \"min\") })", //
        "{Quantity(50,\"Seconds\"),Quantity(51,\"Seconds\"),Quantity(52,\"Seconds\"),Quantity(53,\"Seconds\"),Quantity(54,\"Seconds\"),Quantity(55,\"Seconds\"),Quantity(56,\"Seconds\"),Quantity(57,\"Seconds\"),Quantity(58,\"Seconds\"),Quantity(59,\"Seconds\"),Quantity(60,\"Seconds\")}");
    check("Table(i, {i, Quantity(10, \"s\") })", //
        "{Quantity(1,\"Seconds\"),Quantity(2,\"Seconds\"),Quantity(3,\"Seconds\"),Quantity(4,\"Seconds\"),Quantity(5,\"Seconds\"),Quantity(6,\"Seconds\"),Quantity(7,\"Seconds\"),Quantity(8,\"Seconds\"),Quantity(9,\"Seconds\"),Quantity(10,\"Seconds\")}");
    check("Quantity(1, \"min\")<=Quantity(60, \"s\")", //
        "True");

    check("Quantity(1, \"min\")>Quantity(50, \"s\")", //
        "True");
    check("Quantity(1, \"min\")>Quantity(60, \"s\")", //
        "False");
    check("Quantity(1, \"min\")>=Quantity(60, \"s\")", //
        "True");
    check("Quantity(60, \"s\")<=Quantity(1, \"min\")", //
        "True");
    check("Quantity(60, \"s\")<=Quantity(2, \"min\")", //
        "True");

    check("Quantity(1, \"min\")<Quantity(50, \"s\")", //
        "False");
    check("Quantity(1, \"min\")<Quantity(60, \"s\")", //
        "False");
    check("Quantity(1, \"min\")<=Quantity(60, \"s\")", //
        "True");

    // leave unevaluated because of different unit types
    check("Quantity(1,\"s\")==Quantity(1,\"m\")", //
        "Quantity(1,\"Seconds\")==Quantity(1,\"Meters\")");

    check("Quantity(60, \"s\")==Quantity(1, \"min\")", //
        "True");
    check("Quantity(1, \"min\")==Quantity(60, \"s\")", //
        "True");

    check("Quantity(60, \"s\")!=Quantity(1, \"min\")", //
        "False");
    check("Quantity(1, \"min\")!=Quantity(60, \"s\")", //
        "False");

    check("Quantity(42, \"s\")!=Quantity(1, \"min\")", //
        "True");
    check("Quantity(42, \"min\")!=Quantity(60, \"s\")", //
        "True");

    // github #139
    check("-2+Quantity(1, \"ft\")", //
        "-2+Quantity(1,\"Feet\")");
    check("Quantity(9.8, \"m\")/Quantity(1, \"s\")", //
        "Quantity(9.8,\"Meters\"*\"Seconds\"^(-1))");
    check("Quantity(9.8, \"m\")/Quantity(0, \"s\")", //
        "Quantity(ComplexInfinity,\"Meters\"*\"Seconds\"^(-1))");
    check("Quantity(0, \"s\")^(-1)", //
        "Quantity(ComplexInfinity,\"Seconds\"^(-1))");
    check("2*Quantity(1, \"ft\")", //
        "Quantity(2,\"Feet\")");
    check("0+Quantity(1, \"ft\")", //
        "Quantity(1,\"Feet\")");

    check("0*Quantity(1, \"ft\")", //
        "Quantity(0,\"Feet\")");

    check("Quantity(\"m\")", //
        "Quantity(1,\"Meters\")");
    check("Quantity(3.25, \"m *rad\")", //
        "Quantity(3.25,\"Meters\"*\"Radians\")");
    check("Quantity(3, \"Hz^(-2)*N*m^(-1)\")", //
        "Quantity(3,\"Hertz\"^(-2)*\"Meters\"^(-1)*\"Newtons\")");
    check("0+Quantity(3, \"m\")", //
        "Quantity(3,\"Meters\")");
    check("0*Quantity(3, \"m\")", //
        "Quantity(0,\"Meters\")");
    check("1*Quantity(3, \"m\")", //
        "Quantity(3,\"Meters\")");
    check("Quantity(3, \"m\")", //
        "Quantity(3,\"Meters\")");
    check("Quantity(3, \"Meters\")", //
        "Quantity(3,\"Meters\")");
    // unknown units print Quantity::unkunit and stay inert (use IndependentUnit for ad-hoc units)
    check("Quantity(3, \"Blah\")", //
        "Quantity(3,\"Blah\")");
    check("QuantityQ(Quantity(3, \"Blah\"))", //
        "False");
  }

  @Test
  public void testQuantityQ() {
    check("QuantityQ(Quantity(2, x))", //
        "False");
    check("QuantityQ(Quantity(3, \"m\"))", //
        "True");
    check("QuantityQ(Quantity(3, \"Meters\"))", //
        "True");
  }

  @Test
  public void testQuantityMagnitude() {
    check("QuantityMagnitude(Quantity(2000000000000/8896443230521, \"lbf\"), \"N\")", //
        "1");
    check("QuantityMagnitude(Quantity(1290320000/8896443230521, \"psi\"), \"Pa\")", //
        "1");
    check("QuantityMagnitude(Quantity(6.241509125883258*10^9, \"GeV\"), \"J\")", //
        "1.0");
    check("QuantityMagnitude(Quantity(360, \"deg\"), \"rad\")", //
        "2*Pi");
    check("QuantityMagnitude(Quantity(3.4, \"m\"))", //
        "3.4");
    check("QuantityMagnitude(Quantity(3.4, \"km\"), \"m\")", //
        "3400.0");
  }

  @Test
  public void testQuantityUnit() {
    check("QuantityUnit(Quantity(42, \"Kilograms\"))", //
        "Kilograms");
    check("QuantityUnit(Quantity(3.4, \"Meters\"))", //
        "Meters");
    check("QuantityUnit(Quantity(19.25, \"Acres\"))", //
        "Acres");
  }

  @Test
  public void testUnitDimensions() {
    check("UnitDimensions(\"Newtons\")", //
        "{{LengthUnit,1},{MassUnit,1},{TimeUnit,-2}}");
    check("UnitDimensions(Quantity(3, \"Percent\"))", //
        "{}");
  }

  @Test
  public void testUnitConvert() {
    check("UnitConvert(Quantity(Pi, \"rad\"), \"deg\")", //
        "Quantity(180,\"AngularDegrees\")");
    check("UnitConvert(Quantity(Pi, \"deg\"), \"rad\")", //
        "Quantity(Pi^2/180,\"Radians\")");
    check("UnitConvert(Quantity(\"StandardAccelerationOfGravity\"),\"m/s^2\")", //
        "Quantity(196133/20000,\"Meters\"*\"Seconds\"^(-2))");
    check("UnitConvert(Quantity(111, \"cm\"),\"m\" )", //
        "Quantity(111/100,\"Meters\")");

    // gradian value fixed: a gradian is Pi/200 radians (the legacy database wrongly used Pi/180)
    check("UnitConvert(Quantity(Pi, \"grad\"), \"rad\")", //
        "Quantity(Pi^2/200,\"Radians\")");
    check("UnitConvert(Quantity(Pi, \"rad\"), \"grad\")", //
        "Quantity(200,\"Gradians\")");
    check("UnitConvert(Quantity(200, \"g\")*Quantity(981, \"cm*s^-2\") )", //
        "Quantity(981/500,\"Kilograms\"*\"Meters\"*\"Seconds\"^(-2))");
    check("UnitConvert(Quantity(10^(-6), \"MOhm\") )", //
        "Quantity(1,\"Amperes\"^(-2)*\"Kilograms\"*\"Meters\"^2*\"Seconds\"^(-3))");
    check("UnitConvert(Quantity(10^(-6), \"MOhm\"),\"Ohm\" )", //
        "Quantity(1,\"Ohms\")");
    check("UnitConvert(Quantity(1, \"nmi\"),\"km\" )", //
        "Quantity(463/250,\"Kilometers\")");
    check("UnitConvert(Quantity(360, \"mV^-1*mA*s^2\"),\"Ohm^-1*s^2\" )", //
        "Quantity(360,\"Ohms\"^(-1)*\"Seconds\"^2)");
    check("UnitConvert(Quantity(360, \"km*h^-1\"),\"m*s^-1\" )", //
        "Quantity(100,\"Meters\"*\"Seconds\"^(-1))");
    check("UnitConvert(Quantity(2, \"km^2\") )", //
        "Quantity(2000000,\"Meters\"^2)");
    check("UnitConvert(Quantity(2, \"km^2\"),\"cm^2\" )", //
        "Quantity(20000000000,\"Centimeters\"^2)");
    check("UnitConvert(Quantity(3, \"Hz^-2*N*m^-1\") )", //
        "Quantity(3,\"Kilograms\")");
    check("UnitConvert(Quantity(3.8, \"lb\") )", //
        "Quantity(1.72365,\"Kilograms\")");
    check("UnitConvert(Quantity(8.2, \"nmi\"), \"km\")", //
        "Quantity(15.1864,\"Kilometers\")");
    // incompatible conversion: UnitConvert::compat message + $Failed
    check("UnitConvert(Quantity(8, \"Feet\"), \"Pounds\")", //
        "$Failed");
  }

  @Test
  public void testUnitSimplify() {
    check("UnitSimplify(Quantity(1, \"Joules\"/\"Seconds\"))", //
        "Quantity(1,\"Watts\")");
    check("UnitSimplify(Quantity(1, \"Newtons\")*Quantity(10, \"Meters\"))", //
        "Quantity(10,\"Joules\")");
    // a single named unit stays as given
    check("UnitSimplify(Quantity(3, \"Becquerels\"))", //
        "Quantity(3,\"Becquerels\")");
  }

  @Test
  public void testCommonUnits() {
    // 30 in = 5/2 ft, 1 m = 1250/381 ft - the first unit of the dimension group wins
    check("CommonUnits({Quantity(2, \"Feet\"), Quantity(30, \"Inches\"), Quantity(1, \"Meters\")})", //
        "{Quantity(2,\"Feet\"),Quantity(5/2,\"Feet\"),Quantity(1250/381,\"Feet\")}");
  }

  @Test
  public void testIndependentUnit() {
    check("Quantity(3, IndependentUnit(\"Boxes\"))", //
        "Quantity(3,IndependentUnit(\"Boxes\"))");
    check("Quantity(3, IndependentUnit(\"Boxes\")) + Quantity(4, IndependentUnit(\"Boxes\"))", //
        "Quantity(7,IndependentUnit(\"Boxes\"))");
    check("KnownUnitQ(IndependentUnit(\"Boxes\"))", //
        "True");
    check("CompatibleUnitQ(IndependentUnit(\"Boxes\"), \"Meters\")", //
        "False");
  }

  @Test
  public void testTemperatureArithmetic() {
    // absolute + difference gives an absolute temperature in the absolute unit
    check("Quantity(20., \"DegreesCelsius\") + Quantity(5, \"DegreesCelsiusDifference\")", //
        "Quantity(25.0,\"DegreesCelsius\")");
    // absolute + absolute: both convert to Kelvins (WMA): (20+273.15)+(5+273.15) = 571.3 K
    check("Quantity(20, \"DegreesCelsius\") + Quantity(5, \"DegreesCelsius\")", //
        "Quantity(5713/10,\"Kelvins\")");
    // difference + difference converts by pure scale: 9 degF-diff = 5 degC-diff
    check(
        "Quantity(3, \"DegreesCelsiusDifference\") + Quantity(9, \"DegreesFahrenheitDifference\")", //
        "Quantity(8,\"DegreesCelsiusDifference\")");
  }

  @Test
  public void testMixedUnit() {
    check(
        "UnitConvert(Quantity(50000, \"Seconds\"), MixedUnit({\"Hours\", \"Minutes\", \"Seconds\"}))", //
        "Quantity(MixedMagnitude({13,53,20}),MixedUnit({\"Hours\",\"Minutes\",\"Seconds\"}))");
    check("UnitConvert(Quantity(MixedMagnitude({1, 30}), MixedUnit({\"Minutes\", \"Seconds\"})))", //
        "Quantity(90,\"Seconds\")");
    check("QuantityQ(Quantity(MixedMagnitude({5, 10}), MixedUnit({\"Feet\", \"Inches\"})))", //
        "True");
    check(
        "UnitConvert(Quantity(MixedMagnitude({5, 10}), MixedUnit({\"Feet\", \"Inches\"})), \"Centimeters\")", //
        "Quantity(889/5,\"Centimeters\")");
  }

  @Test
  public void testQuantityArray() {
    check("QuantityArray({2.3, 1.5, 9.}, \"m\")", //
        "QuantityArray({2.3,1.5,9.0},\"Meters\")");
    check("Normal(QuantityArray({2.3, 1.5}, \"Meters\"))", //
        "{Quantity(2.3,\"Meters\"),Quantity(1.5,\"Meters\")}");
    check("QuantityMagnitude(QuantityArray({2.3, 1.5}, \"Meters\"))", //
        "{2.3,1.5}");
    check("QuantityUnit(QuantityArray({2.3, 1.5}, \"Meters\"))", //
        "Meters");
    check("UnitConvert(QuantityArray({1, 2}, \"Kilometers\"), \"Meters\")", //
        "QuantityArray({1000,2000},\"Meters\")");
    check("QuantityArray({Quantity(1,\"m\"), Quantity(2,\"m\")})", //
        "QuantityArray({1,2},\"Meters\")");
    check("Normal(QuantityArray({{1, 2}, {3, 4}}, {\"Seconds\", \"Meters\"}))", //
        "{{Quantity(1,\"Seconds\"),Quantity(2,\"Meters\")},{Quantity(3,\"Seconds\"),Quantity(4,\"Meters\")}}");
  }

  @Test
  public void testDimensionSpecPredicates() {
    check("KnownUnitQ(\"Seconds\", \"Time\")", //
        "True");
    check("KnownUnitQ(\"Meters\", \"Time\")", //
        "False");
    check("KnownUnitQ(\"Meters\", {{\"LengthUnit\", 1}})", //
        "True");
    check("QuantityQ(Quantity(300, \"Seconds\"), \"Time\")", //
        "True");
    check("QuantityQ(Quantity(300, \"Meters\"), \"Time\")", //
        "False");
    check("QuantityQ(Quantity(300, \"Meters\"), QuantityVariable(\"x\", \"Length\"))", //
        "True");
  }

  @Test
  public void testQuantityVariable() {
    check("QuantityVariableCanonicalUnit(QuantityVariable(\"Time\"))", //
        "Seconds");
    check("QuantityVariableCanonicalUnit(QuantityVariable(\"R\", \"ElectricResistance\"))", //
        "Ohms");
    check("QuantityVariableDimensions(QuantityVariable(\"ElectricPotential\"))", //
        "{{ElectricCurrentUnit,-1},{LengthUnit,2},{MassUnit,1},{TimeUnit,-3}}");
    check("QuantityVariablePhysicalQuantity(QuantityVariable(\"V\", \"ElectricPotential\"))", //
        "ElectricPotential");
    check(
        "QuantityVariablePhysicalQuantity(QuantityVariable(\"V\", \"ElectricPotential\"), \"Entity\")", //
        "Entity(PhysicalQuantity,ElectricPotential)");
    check("QuantityVariableIdentifier(QuantityVariable(\"V\", \"ElectricPotential\"))", //
        "V");
    // products of physical quantities and derivatives of quantity variables
    check("QuantityVariableCanonicalUnit(QuantityVariable(\"x\", \"Mass\"*\"Distance\"))", //
        "Kilograms*Meters");
    check(
        "QuantityVariableCanonicalUnit(Derivative(1)[QuantityVariable(\"RadiantFluxDensity\")][QuantityVariable(\"Time\")])", //
        "Watts/(Meters^2*Seconds)");
  }

  @Test
  public void testUnitySimplifyDimensions() {
    // an angle is a real dimension by default
    check("UnitSimplify(Quantity(3, \"AngularDegrees\"/\"Seconds\"))", //
        "Quantity(3,\"AngularDegrees\"*\"Seconds\"^(-1))");
    // ... unless it is declared unity, then degrees/second becomes a frequency
    check(
        "UnitSimplify(Quantity(3, \"AngularDegrees\"/\"Seconds\"), UnityDimensions -> {\"AngleUnit\"})", //
        "Quantity(Pi/60,\"Hertz\")");
    check("UnitSimplify(Quantity(3, \"AngularDegrees\"/\"Seconds\"), UnityDimensions -> Automatic)", //
        "Quantity(Pi/60,\"Hertz\")");
  }

  @Test
  public void testTemperatureSubtraction() {
    // absolute - absolute is a temperature difference
    check("Quantity(9, \"DegreesCelsius\") - Quantity(3, \"DegreesCelsius\")", //
        "Quantity(6,\"DegreesCelsiusDifference\")");
    // a scalar factor stays outside an absolute temperature (its zero is not absolute zero)
    check("-Quantity(3, \"DegreesCelsius\")", //
        "-Quantity(3,\"DegreesCelsius\")");
    // Kelvins are their own difference unit
    check("Quantity(300, \"Kelvins\") - Quantity(100, \"Kelvins\")", //
        "Quantity(200,\"Kelvins\")");
  }

  @Test
  public void testPrefixedIndependentUnit() {
    check(
        "UnitConvert(Quantity(1, \"Mega\"*IndependentUnit(\"Coins\")), \"Kilo\"*IndependentUnit(\"Coins\"))", //
        "Quantity(1000,\"Kilo\"*IndependentUnit(\"Coins\"))");
    check("KnownUnitQ(\"Kilo\"*IndependentUnit(\"Coins\"))", //
        "True");
  }

  @Test
  public void testDimensionalCombinations() {
    // V/(I*R) is dimensionless
    check("DimensionalCombinations({QuantityVariable(\"V\", \"ElectricPotential\"), "
        + "QuantityVariable(\"I\", \"ElectricCurrent\"), QuantityVariable(\"R\", \"ElectricResistance\")})", //
        "{QuantityVariable(V,ElectricPotential)/(QuantityVariable(I,ElectricCurrent)*QuantityVariable(R,ElectricResistance))}");
    // force, distance and charge admit no dimensionless combination
    check(
        "DimensionalCombinations({QuantityVariable(\"Force\"), QuantityVariable(\"Distance\"), "
            + "QuantityVariable(\"ElectricCharge\")})", //
        "{}");
    // a combination with the dimensions of energy
    check(
        "DimensionalCombinations({QuantityVariable(\"Force\"), QuantityVariable(\"Distance\")}, \"Energy\")", //
        "{QuantityVariable(Distance)*QuantityVariable(Force)}");
  }

  @Test
  public void testNondimensionalizationTransform() {
    check(
        "NondimensionalizationTransform(QuantityVariable(\"x\", \"Length\") == "
            + "QuantityVariable(\"v\", \"Speed\")*QuantityVariable(\"t\", \"Time\"), "
            + "{QuantityVariable(\"x\", \"Length\")}, {y}, \"NondimensionalizationRules\")", //
        "{QuantityVariable(x,Length)->Quantity(y*K(1),\"Meters\")}");
    check(
        "NondimensionalizationTransform(QuantityVariable(\"x\", \"Length\") == 3, "
            + "{QuantityVariable(\"x\", \"Length\")}, {y}, \"NondimensionalizationMultipliers\")", //
        "{Quantity(K(1),\"Meters\")}");
    check(
        "NondimensionalizationTransform(QuantityVariable(\"x\", \"Length\") == 3, "
            + "{QuantityVariable(\"x\", \"Length\")}, {y}, \"NondimensionalizationMultipliers\", "
            + "GeneratedQuantityMagnitudes -> foo)", //
        "{Quantity(foo(1),\"Meters\")}");
  }

  @Test
  public void testQuantityDistribution() {
    check("Mean(QuantityDistribution(NormalDistribution(120, 16), \"Centimeters\"))", //
        "Quantity(120,\"Centimeters\")");
    check("StandardDeviation(QuantityDistribution(NormalDistribution(120, 16), \"Centimeters\"))", //
        "Quantity(16,\"Centimeters\")");
    check("Variance(QuantityDistribution(NormalDistribution(120, 16), \"Centimeters\"))", //
        "Quantity(256,\"Centimeters\"^2)");
    // the CDF is dimensionless and accepts any compatible unit
    check(
        "CDF(QuantityDistribution(NormalDistribution(0, 1), \"Meters\"), Quantity(0, \"Centimeters\"))", //
        "1/2");
    check("InverseCDF(QuantityDistribution(NormalDistribution(0, 1), \"Meters\"), 1/2)", //
        "Quantity(0,\"Meters\")");
    // a continuous density is per unit
    check(
        "PDF(QuantityDistribution(UniformDistribution({0, 2}), \"Meters\"), Quantity(1, \"Meters\"))", //
        "Quantity(1/2,\"Meters\"^(-1))");
    // a dimensionless unit collapses back to the plain distribution
    check("QuantityDistribution(NormalDistribution(0, 1), \"PureUnities\")", //
        "NormalDistribution(0,1)");
    check("UnitConvert(QuantityDistribution(NormalDistribution(1, 1), \"Kilometers\"), \"Meters\")", //
        "QuantityDistribution(TransformedDistribution(1000*#1,NormalDistribution(1,1)),\"Meters\")");
  }

  @Test
  public void testDocExamples() {
    check("UnitDimensions(IndependentUnit(\"Boxes\"))", //
        "{{IndependentUnit(Boxes),1}}");
    check(
        "DimensionalCombinations({QuantityVariable(\"Force\")}, \"Energy\", "
            + "IncludeQuantities -> {QuantityVariable(\"Distance\")})", //
        "{QuantityVariable(Distance)*QuantityVariable(Force)}");
    check(
        "QuantityVariableCanonicalUnit(QuantityVariable(\"f\", IndependentPhysicalQuantity(\"FoxPopulation\")))", //
        "IndependentUnit(FoxPopulation)");
  }

  @Test
  public void testQuantityForm() {
    check("QuantityForm(Quantity(3, \"Meters\"/\"Seconds\"), \"Abbreviation\")", //
        "3 m/s");
    check("QuantityForm(Quantity(3, \"Meters\"/\"Seconds\"), \"LongForm\")", //
        "3 meters per second");
    check("QuantityForm(Quantity(0.7, \"Seconds\"), {\"LongForm\", \"SingularForm\"})", //
        "0.7 second");
    check("QuantityForm(Quantity(3, \"Meters\"/\"Seconds\"^2), \"LongForm\")", //
        "3 meters per second squared");
    check("QuantityForm(Quantity(5, \"Feet\"), {\"LongForm\", \"SingularForm\"})", //
        "5 foot");
  }

  @Test
  public void testQuantityTeXForm() {
    check("TeXForm(Quantity(3, \"Kilometers\"))", //
        "3\\,\\text{km}");
    // the exponent has to leave the text mode: a caret inside \text{} is not valid TeX and
    // is rejected by a renderer instead of being shown as a superscript
    check("TeXForm(Quantity(9.8, \"Meters\"/\"Seconds\"^2))", //
        "9.8\\,\\text{m/s}^{2}");
  }

  @Test
  public void testUnitConvertTemperature() {
    check("UnitConvert(Quantity(0, \"DegreesCelsius\"), \"Kelvins\")", //
        "Quantity(5463/20,\"Kelvins\")");
    check("UnitConvert(Quantity(451, \"DegreesFahrenheit\"), \"DegreesCelsius\")", //
        "Quantity(2095/9,\"DegreesCelsius\")");
    check("UnitConvert(Quantity(12, \"DegreesCelsiusDifference\"), \"Kelvins\")", //
        "Quantity(12,\"Kelvins\")");
    // temperature difference -> absolute Celsius/Fahrenheit is not permitted
    check("UnitConvert(Quantity(3, \"DegreesCelsiusDifference\"), \"DegreesFahrenheit\")", //
        "$Failed");
  }
}
