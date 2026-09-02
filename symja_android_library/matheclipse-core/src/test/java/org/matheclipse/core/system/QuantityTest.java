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

  /**
   * Magnitude-level unary functions on a quantity: the value is taken from the magnitude and the
   * unit is kept, which is what Re, Im, Floor, Ceiling and Round already did. Abs was the missing
   * one, and its absence also left Norm of a quantity vector unevaluated.
   */
  @Test
  public void testQuantityAbsAndSign() {
    check("Abs(Quantity(-3,\"Meters\"))", //
        "Quantity(3,\"Meters\")");
    check("Abs(Quantity(-3.5,\"Meters\"))", //
        "Quantity(3.5,\"Meters\")");
    check("Abs(Quantity(x,\"Meters\"))", //
        "Quantity(Abs(x),\"Meters\")");
    // an affine unit takes the magnitude as written, consistent with Floor and Round
    check("Abs(Quantity(-3,\"DegreesCelsius\"))", //
        "Quantity(3,\"DegreesCelsius\")");

    // Sign is dimensionless. Without its own branch the generic path forms Quantity/Abs(Quantity),
    // which is 0/0 for a zero magnitude
    check("Sign(Quantity(-3,\"Meters\"))", //
        "-1");
    check("Sign(Quantity(0,\"Meters\"))", //
        "0");
    check("Sign(Quantity(x,\"Meters\"))", //
        "Sign(x)");

    // Norm follows from Abs
    check("Norm({Quantity(3,\"Meters\"),Quantity(4,\"Meters\")})", //
        "Quantity(5,\"Meters\")");
    check("Norm({Quantity(3,\"Meters\"),Quantity(4,\"Meters\")}, Infinity)", //
        "Quantity(4,\"Meters\")");
    check("Norm(Quantity(-3,\"Meters\"))", //
        "Quantity(3,\"Meters\")");

    // and so does MeanDeviation, which used to stall on Abs(Quantity(...))
    check("MeanDeviation({Quantity(1,\"Meters\"),Quantity(3,\"Meters\"),Quantity(2,\"Meters\")})", //
        "Quantity(2/3,\"Meters\")");
  }

  /** The sign of a quantity is the sign of its magnitude. */
  @Test
  public void testQuantitySignPredicates() {
    check("Positive(Quantity(3,\"Meters\"))", //
        "True");
    check("Positive(Quantity(0,\"Meters\"))", //
        "False");
    check("Negative(Quantity(-3,\"Meters\"))", //
        "True");
    check("Negative(Quantity(0,\"Meters\"))", //
        "False");
    check("NonNegative(Quantity(3,\"Meters\"))", //
        "True");
    check("NonNegative(Quantity(-1,\"Meters\"))", //
        "False");
    check("NonPositive(Quantity(0,\"Meters\"))", //
        "True");
    // an affine unit is judged on the magnitude as written, matching Quantity(-3,"DegreesCelsius")
    // < Quantity(0,"DegreesCelsius")
    check("Positive(Quantity(-3,\"DegreesCelsius\"))", //
        "False");
    // a symbolic magnitude decides nothing, and must not drop the unit
    check("Positive(Quantity(x,\"Meters\"))", //
        "Positive(Quantity(x,\"Meters\"))");
  }

  /**
   * Sort and Ordering rank quantities by magnitude, not by the canonical order of their structure.
   * Every value here was run in Mathematica.
   *
   * <p>
   * This lives in the Sort and Ordering evaluators rather than in IExpr#compareTo, because
   * canonical order is also what Orderless uses to canonicalize Plus and Times, where two
   * quantities of equal magnitude in different units have to stay distinguishable.
   */
  @Test
  public void testQuantitySortOrder() {
    check("Ordering({Quantity(3,\"Feet\"),Quantity(9,\"Inches\"),Quantity(1,\"Meters\")})", //
        "{2,1,3}");
    check("Sort({Quantity(3,\"Feet\"),Quantity(9,\"Inches\"),Quantity(1,\"Meters\")})", //
        "{Quantity(9,\"Inches\"),Quantity(3,\"Feet\"),Quantity(1,\"Meters\")}");

    // incompatible units fall back to canonical order, with no message
    check("Sort({Quantity(3,\"Meters\"),Quantity(1,\"Seconds\")})", //
        "{Quantity(1,\"Seconds\"),Quantity(3,\"Meters\")}");

    // sizes 2, 3 and 4 take specialized paths in EvalAttributes
    check("Sort({Quantity(3,\"Meters\"),Quantity(1,\"Meters\")})", //
        "{Quantity(1,\"Meters\"),Quantity(3,\"Meters\")}");
    check("Sort({Quantity(300,\"Centimeters\"),Quantity(1,\"Meters\"),Quantity(2,\"Meters\")})", //
        "{Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(300,\"Centimeters\")}");
    check(
        "Sort({Quantity(4,\"Meters\"),Quantity(300,\"Centimeters\"),Quantity(1,\"Meters\"),Quantity(2,\"Meters\")})", //
        "{Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(300,\"Centimeters\"),Quantity(4,\"Meters\")}");

    // equal magnitudes in different units compare equal, so the canonical fallback has to decide
    // them - otherwise the comparator is not a total order. Both input orders must agree.
    check("Sort({Quantity(100,\"Centimeters\"),Quantity(1,\"Meters\")})", //
        "{Quantity(1,\"Meters\"),Quantity(100,\"Centimeters\")}");
    check("Sort({Quantity(1,\"Meters\"),Quantity(100,\"Centimeters\")})", //
        "{Quantity(1,\"Meters\"),Quantity(100,\"Centimeters\")}");

    // a list that only partly holds quantities still sorts
    check("Sort({Quantity(3,\"Meters\"),1,Quantity(1,\"Meters\")})", //
        "{1,Quantity(1,\"Meters\"),Quantity(3,\"Meters\")}");

    // an explicit ordering function keeps precedence
    check("Sort({Quantity(3,\"Feet\"),Quantity(9,\"Inches\"),Quantity(1,\"Meters\")}, Greater)", //
        "{Quantity(1,\"Meters\"),Quantity(3,\"Feet\"),Quantity(9,\"Inches\")}");
    check("Ordering({Quantity(3,\"Feet\"),Quantity(9,\"Inches\"),Quantity(1,\"Meters\")}, 1)", //
        "{2}");

    // Orderless canonicalization is deliberately NOT affected
    check("Plus(Quantity(1,\"Meters\"),Quantity(2,\"Meters\"))", //
        "Quantity(3,\"Meters\")");
  }

  /**
   * The quantile family on quantity data. Every value here was run in Mathematica.
   *
   * <p>
   * Each of these heads answers in the unit of its data - unlike Variance, whose result is squared
   * - so the data is converted to one common unit, the plain magnitudes go through exactly the
   * same code as ordinary reals, and the unit is re-attached. The unit reported is the FIRST
   * element's, the convention Plus already uses.
   */
  @Test
  public void testQuantityStatistics() {
    // the leftmost unit wins, and the other elements are converted to it
    check("Median({Quantity(1,\"Meters\"),Quantity(300,\"Centimeters\"),Quantity(2,\"Meters\")})", //
        "Quantity(2,\"Meters\")");
    check("Median({Quantity(3,\"Feet\"),Quantity(9,\"Inches\"),Quantity(1,\"Meters\")})", //
        "Quantity(3,\"Feet\")");

    check(
        "Quartiles({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(3,\"Meters\"),Quantity(4,\"Meters\")})", //
        "{Quantity(3/2,\"Meters\"),Quantity(5/2,\"Meters\"),Quantity(7/2,\"Meters\")}");
    check(
        "Quantile({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(3,\"Meters\"),Quantity(4,\"Meters\")}, 1/2)", //
        "Quantity(2,\"Meters\")");
    check(
        "InterquartileRange({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(3,\"Meters\"),Quantity(4,\"Meters\")})", //
        "Quantity(2,\"Meters\")");
    check("MedianDeviation({Quantity(1,\"Meters\"),Quantity(3,\"Meters\"),Quantity(2,\"Meters\")})", //
        "Quantity(1,\"Meters\")");
    check("MeanDeviation({Quantity(1,\"Meters\"),Quantity(3,\"Meters\"),Quantity(2,\"Meters\")})", //
        "Quantity(2/3,\"Meters\")");
    check("MovingMedian({Quantity(1,\"Meters\"),Quantity(3,\"Meters\"),Quantity(2,\"Meters\")}, 2)", //
        "{Quantity(2,\"Meters\"),Quantity(5/2,\"Meters\")}");

    // the parameterizations still reach the magnitudes
    check(
        "Quartiles({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(3,\"Meters\"),Quantity(4,\"Meters\")}, {{0,0},{1,0}})", //
        "{Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(3,\"Meters\")}");
    check(
        "Quantile({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(3,\"Meters\"),Quantity(4,\"Meters\")}, {1/4,3/4})", //
        "{Quantity(1,\"Meters\"),Quantity(3,\"Meters\")}");

    // a QuantityArray contributes its array of quantities
    check("Median(QuantityArray({1,2,3,4},\"Meters\"))", //
        "Quantity(5/2,\"Meters\")");
    check("Quartiles(QuantityArray({1,2,3,4},\"Meters\"))", //
        "{Quantity(3/2,\"Meters\"),Quantity(5/2,\"Meters\"),Quantity(7/2,\"Meters\")}");
    check("MovingMedian(QuantityArray({1,2,5,6},\"Meters\"), 2)", //
        "{Quantity(3/2,\"Meters\"),Quantity(7/2,\"Meters\"),Quantity(11/2,\"Meters\")}");

    // one unit per column: the columnwise branch recurses, and each column is uniform. This is
    // why the wrapper only claims flat vectors.
    check("Median(QuantityArray({{1,2},{3,4}},{\"Meters\",\"Seconds\"}))", //
        "{Quantity(2,\"Meters\"),Quantity(3,\"Seconds\")}");
    check(
        "Median({{Quantity(1,\"Meters\"),Quantity(2,\"Seconds\")},{Quantity(3,\"Meters\"),Quantity(4,\"Seconds\")}})", //
        "{Quantity(2,\"Meters\"),Quantity(3,\"Seconds\")}");

    // incompatible units report Quantity::compat, and must NOT also report Median::rectn
    check("Median({Quantity(1,\"Meters\"),Quantity(2,\"Seconds\")})", //
        "Median({Quantity(1,\"Meters\"),Quantity(2,\"Seconds\")})");
    // mixing a quantity with a bare number is Median::rectn
    check("Median({Quantity(1,\"Meters\"), 2})", //
        "Median({Quantity(1,\"Meters\"),2})");

    check("Median({Quantity(1,\"Meters\")})", //
        "Quantity(1,\"Meters\")");
  }

  /**
   * A QuantityArray is measured as the array it stands for, not as its two arguments. Dimensions
   * and MatrixQ were run in Mathematica; the rest follow from those - once Dimensions of a four
   * element QuantityArray is {4}, there is only one coherent answer for the others.
   *
   * <p>
   * This is done in the functions that only INSPECT an array. It is deliberately not done inside
   * LinearAlgebraUtil#dimensions, whose callers take the dimensions and then index the expression
   * as a nested list - and position 2 of a QuantityArray is the unit, not a row.
   */
  @Test
  public void testQuantityArrayAsArray() {
    check("Dimensions(QuantityArray({1,2,3,4},\"Meters\"))", //
        "{4}");
    check("Dimensions(QuantityArray({{1,2},{3,4}},{\"Meters\",\"Seconds\"}))", //
        "{2,2}");
    check("Dimensions(QuantityArray({{1,2},{3,4}},\"Meters\"))", //
        "{2,2}");
    check("MatrixQ(QuantityArray({{1,2},{3,4}},{\"Meters\",\"Seconds\"}))", //
        "True");
    check("VectorQ(QuantityArray({1,2,3,4},\"Meters\"))", //
        "True");
    check("ArrayQ(QuantityArray({1,2,3,4},\"Meters\"))", //
        "True");
    check("ArrayDepth(QuantityArray({1,2,3,4},\"Meters\"))", //
        "1");
    check("ArrayDepth(QuantityArray({{1,2},{3,4}},\"Meters\"))", //
        "2");
    check("TensorRank(QuantityArray({1,2,3,4},\"Meters\"))", //
        "1");
    check("Length(QuantityArray({1,2,3,4},\"Meters\"))", //
        "4");

    // the same answers as the other non-List array in the system
    check("Dimensions(SparseArray({1,2,3,4}))", //
        "{4}");
    check("Length(SparseArray({1,2,3,4}))", //
        "4");
  }

  /**
   * Plotting quantity data used to drop it in silence: the pipeline reaches toDoubleVectorIgnore,
   * which cannot turn a quantity into a machine number, so the Graphics came back holding no Line
   * or Point at all. The magnitudes are now substituted at each plot entry.
   */
  @Test
  public void testQuantityListPlot() {
    // a flat list plots exactly as its magnitudes do
    check(
        "Cases(ListLinePlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(4,\"Meters\")}), _Line, Infinity)", //
        "{Line({{1.0,1},{2.0,2},{3.0,4}})}");
    check("Cases(ListLinePlot({1,2,4}), _Line, Infinity)", //
        "{Line({{1.0,1},{2.0,2},{3.0,4}})}");

    // mixed units are converted to the first element's, as everywhere else in this family
    check(
        "Cases(ListLinePlot({Quantity(100,\"Centimeters\"),Quantity(2,\"Meters\")}), _Line, Infinity)", //
        "{Line({{1.0,100},{2.0,200}})}");

    // a list of {x,y} pairs converts per column, so each axis keeps its own unit
    check(
        "Cases(ListPlot({{Quantity(1,\"Seconds\"),Quantity(3,\"Meters\")},{Quantity(2,\"Seconds\"),Quantity(6,\"Meters\")}}), _Point, Infinity)", //
        "{Point({{1,3},{2,6}})}");

    check("Cases(ListLinePlot(QuantityArray({1,2,4},\"Meters\")), _Line, Infinity)", //
        "{Line({{1.0,1},{2.0,2},{3.0,4}})}");

    // incompatible units are left alone rather than mixed
    check("Cases(ListLinePlot({Quantity(1,\"Meters\"),Quantity(2,\"Seconds\")}), _Line, Infinity)", //
        "{}");

    // every other list-data head, each of which used to produce an empty picture
    check(
        "Cases(ListLogPlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(4,\"Meters\")}), _Point, Infinity)", //
        "{Point({{1.0,1},{2.0,2},{3.0,4}})}");
    check(
        "Cases(ListLogLogPlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(4,\"Meters\")}), _Point, Infinity)", //
        "{Point({{1.0,1},{2.0,2},{3.0,4}})}");
    check(
        "Cases(ListLogLinearPlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(4,\"Meters\")}), _Point, Infinity)", //
        "{Point({{1.0,1},{2.0,2},{3.0,4}})}");
    check(
        "Length(Cases(ListStepPlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(4,\"Meters\")}), _Line|_Point, Infinity))", //
        "1");
    check(
        "Length(Cases(ListPolarPlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(4,\"Meters\")}), _Line|_Point, Infinity))", //
        "1");
    check(
        "Length(Cases(BarChart({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(4,\"Meters\")}), _Rectangle|_Polygon, Infinity))", //
        "3");
    check(
        "Length(Cases(PieChart({Quantity(1,\"Meters\"),Quantity(2,\"Meters\")}), _Disk|_Polygon, Infinity))", //
        "2");
    check(
        "Length(Cases(Histogram({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(2,\"Meters\")}), _Rectangle, Infinity))", //
        "1");
    check(
        "Length(Cases(BoxWhiskerChart({Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(4,\"Meters\")}), _Line|_Rectangle, Infinity))", //
        "6");
    check(
        "Length(Cases(NumberLinePlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\")}), _Point|_Line, Infinity))", //
        "2");
    check(
        "Length(Cases(ListPointPlot3D({{Quantity(1,\"Meters\"),Quantity(2,\"Meters\"),Quantity(3,\"Meters\")}}), _Point, Infinity))", //
        "1");
  }

  /**
   * TargetUnits chooses the unit the plotted magnitudes are expressed in: Automatic keeps each
   * column in its own first element's unit, a single unit names the value axis (y in 2D, z in 3D),
   * and a list is positional, one unit per column. Only the CONVERSION half is implemented - the
   * axis is not relabelled with the unit.
   */
  @Test
  public void testQuantityTargetUnits() {
    check(
        "Cases(ListLinePlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\")}, TargetUnits->\"Centimeters\"), _Line, Infinity)", //
        "{Line({{1.0,100},{2.0,200}})}");
    check(
        "Cases(ListLinePlot({Quantity(100,\"Centimeters\"),Quantity(2,\"Meters\")}, TargetUnits->\"Meters\"), _Line, Infinity)", //
        "{Line({{1.0,1},{2.0,2}})}");
    // Automatic is the default and keeps the data's own first unit
    check(
        "Cases(ListLinePlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\")}, TargetUnits->Automatic), _Line, Infinity)", //
        "{Line({{1.0,1},{2.0,2}})}");

    // positional, one unit per column
    check(
        "Cases(ListPlot({{Quantity(1,\"Seconds\"),Quantity(3,\"Meters\")},{Quantity(2,\"Seconds\"),Quantity(6,\"Meters\")}}, TargetUnits->{\"Milliseconds\",\"Centimeters\"}), _Point, Infinity)", //
        "{Point({{1000,300},{2000,600}})}");
    // a single unit names the value axis only, so x is left in its own unit
    check(
        "Cases(ListPlot({{Quantity(1,\"Seconds\"),Quantity(3,\"Meters\")},{Quantity(2,\"Seconds\"),Quantity(6,\"Meters\")}}, TargetUnits->\"Centimeters\"), _Point, Infinity)", //
        "{Point({{1,300},{2,600}})}");

    // an incompatible target leaves the data alone rather than mixing dimensions
    check(
        "Cases(ListLinePlot({Quantity(1,\"Meters\"),Quantity(2,\"Meters\")}, TargetUnits->\"Seconds\"), _Line, Infinity)", //
        "{}");

    // the option is declared, so the chart family does not report it as unknown
    check(
        "Length(Cases(BarChart({Quantity(1,\"Meters\"),Quantity(2,\"Meters\")}, TargetUnits->\"Centimeters\"), _Rectangle|_Polygon, Infinity))", //
        "2");
  }

  /**
   * A quantity valued plot function is plotted by its magnitude. Mathematica pins this:
   * {@code Plot[Quantity[x,"Meters"], {x,0,1}]} draws the line y == x, with no unit in the
   * coordinates.
   *
   * <p>
   * Detection has to probe rather than inspect: a function returning quantities is usually not
   * syntactically a Quantity until its variable is bound, so the function is evaluated once inside
   * its range and wrapped in QuantityMagnitude only if that yields a quantity.
   */
  @Test
  public void testQuantityFunctionPlot() {
    // the same coordinates as plotting the magnitude directly
    check("Take(Cases(Plot(Quantity(x,\"Meters\"), {x,0,1}), _Line, Infinity)[[1,1]], 3)", //
        "{{0.0,0.0},{0.0167158,0.0167158},{0.0330247,0.0330247}}");
    check("Take(Cases(Plot(x, {x,0,1}), _Line, Infinity)[[1,1]], 3)", //
        "{{0.0,0.0},{0.0167158,0.0167158},{0.0330247,0.0330247}}");

    // a function that only becomes a quantity once its variable is bound
    check(
        "Module({}, f(t_):=Quantity(t^2,\"Meters\"); Take(Cases(Plot(f(x), {x,0,1}), _Line, Infinity)[[1,1]], 3))", //
        "{{0.0,0.0},{0.0167158,0.000279419},{0.0330247,0.00109063}}");

    // the rest of the family, each of which used to draw nothing
    check("Length(Cases(LogPlot(Quantity(Exp(x),\"Meters\"), {x,1,2}), _Line, Infinity))", //
        "1");
    check("Length(Cases(LogLogPlot(Quantity(x^2,\"Meters\"), {x,1,2}), _Line, Infinity))", //
        "1");
    check("Length(Cases(LogLinearPlot(Quantity(x,\"Meters\"), {x,1,2}), _Line, Infinity))", //
        "1");
    check("Length(Cases(PolarPlot(Quantity(1,\"Meters\"), {x,0,2}), _Line, Infinity))", //
        "1");
    check("Length(Cases(DiscretePlot(Quantity(n,\"Meters\"), {n,1,5}), _Point|_Line, Infinity))", //
        "2");
    check("Length(Cases(Plot3D(Quantity(x*y,\"Meters\"), {x,0,1},{y,0,1}), _Polygon, Infinity))", //
        "1");

    // a list of functions, quantity valued or not, one unit each
    check("Length(Cases(Plot({x, Quantity(2*x,\"Meters\")}, {x,0,1}), _Line, Infinity))", //
        "2");

    // a parametric curve carries a unit per component
    check(
        "Take(Cases(ParametricPlot({Quantity(t,\"Meters\"),Quantity(t^2,\"Meters\")}, {t,0,1}), _Line, Infinity)[[1,1]], 3)", //
        "{{0.0,0.0},{0.000833333,6.94444*10^-7},{0.00166667,2.77778*10^-6}}");
    // TargetUnits is positional over the components: x in centimeters, y left in meters
    check(
        "Take(Cases(ParametricPlot({Quantity(t,\"Meters\"),Quantity(t^2,\"Meters\")}, {t,0,1}, TargetUnits->{\"Centimeters\",\"Meters\"}), _Line, Infinity)[[1,1]], 3)", //
        "{{0.0,0.0},{0.0833333,6.94444*10^-7},{0.166667,2.77778*10^-6}}");

    // TargetUnits on a value-axis function plot
    check(
        "Take(Cases(Plot(Quantity(x,\"Meters\"), {x,0,1}, TargetUnits->\"Centimeters\"), _Line, Infinity)[[1,1]], 3)", //
        "{{0.0,0.0},{0.0167158,1.67158},{0.0330247,3.30247}}");

    // plain functions are untouched
    check("Length(Cases(Plot(Sin(x), {x,0,Pi}), _Line, Infinity))", //
        "1");
    check("Length(Cases(ParametricPlot({t,t^2}, {t,0,1}), _Line, Infinity))", //
        "1");
    check("Length(Cases(Plot3D(x*y, {x,0,1},{y,0,1}), _Polygon, Infinity))", //
        "1");
  }

  /**
   * A quantity valued plot RANGE is stripped to magnitudes, with the plot variable bound to plain
   * numbers. Mathematica pins this: {@code Plot[Quantity[x^2,"Meters"], {x, Quantity[0,"Seconds"],
   * Quantity[2,"Seconds"]}]} draws y == x squared over x from 0 to 2, which it could not if x
   * carried the second.
   */
  @Test
  public void testQuantityPlotRange() {
    check(
        "Last(Cases(Plot(x, {x, Quantity(0,\"Seconds\"), Quantity(2,\"Seconds\")}), _Line, Infinity)[[1,1]])", //
        "{2.0,2.0}");
    check(
        "Take(Cases(Plot(x, {x, Quantity(0,\"Seconds\"), Quantity(2,\"Seconds\")}), _Line, Infinity)[[1,1]], 3)", //
        "{{0.0,0.0},{0.0334317,0.0334317},{0.0660493,0.0660493}}");
    // the same picture as the plain range it reduces to
    check("Take(Cases(Plot(x, {x,0,2}), _Line, Infinity)[[1,1]], 3)", //
        "{{0.0,0.0},{0.0334317,0.0334317},{0.0660493,0.0660493}}");

    // a quantity range AND a quantity valued body
    check(
        "Last(Cases(Plot(Quantity(x^2,\"Meters\"), {x, Quantity(0,\"Seconds\"), Quantity(2,\"Seconds\")}), _Line, Infinity)[[1,1]])", //
        "{2.0,4.0}");

    // endpoints in different units convert to the first one's
    check(
        "Last(Cases(Plot(x, {x, Quantity(0,\"Seconds\"), Quantity(2000,\"Milliseconds\")}), _Line, Infinity)[[1,1]])", //
        "{2.0,2.0}");

    // incompatible endpoints are still rejected
    check("Length(Cases(Plot(x, {x, Quantity(0,\"Seconds\"), Quantity(2,\"Meters\")}), _Line, Infinity))", //
        "0");

    // the other function heads take a quantity range too
    check(
        "Length(Cases(PolarPlot(Quantity(1,\"Meters\"), {t, Quantity(0,\"Radians\"), Quantity(2,\"Radians\")}), _Line, Infinity))", //
        "1");
    check(
        "Length(Cases(ParametricPlot({t,t^2}, {t, Quantity(0,\"Seconds\"), Quantity(1,\"Seconds\")}), _Line, Infinity))", //
        "1");
    check(
        "Length(Cases(Plot3D(Quantity(x*y,\"Meters\"), {x,Quantity(0,\"Seconds\"),Quantity(1,\"Seconds\")},{y,0,1}), _Polygon, Infinity))", //
        "1");
  }

  /**
   * $UnitSystem, and UnitConvert with a unit SYSTEM rather than a unit as its target. Every
   * conversion below was run in Mathematica.
   *
   * <p>
   * It is a lookup of a preferred unit per KIND of quantity, not a substitution of base units: a
   * compound unit has no system preference and comes back unchanged.
   */
  @Test
  public void testUnitSystem() {
    check("UnitConvert(Quantity(3,\"Feet\"), \"Metric\")", //
        "Quantity(1143/1250,\"Meters\")");
    // the Imperial length is the yard, not the foot
    check("UnitConvert(Quantity(3,\"Meters\"), \"Imperial\")", //
        "Quantity(1250/381,\"Yards\")");
    check("UnitConvert(Quantity(1,\"Kilograms\"), \"Imperial\")", //
        "Quantity(100000000/45359237,\"Pounds\")");
    // the affine Fahrenheit, not the absolute Rankine
    check("UnitConvert(Quantity(300,\"Kelvins\"), \"Imperial\")", //
        "Quantity(8033/100,\"DegreesFahrenheit\")");

    // a compound unit is left alone
    check("UnitConvert(Quantity(3,\"Meters\")/Quantity(1,\"Seconds\"), \"Imperial\")", //
        "Quantity(3,\"Meters\"*\"Seconds\"^(-1))");
    // and so is a dimension with no preferred unit in either system
    check("UnitConvert(Quantity(1,\"Hours\"), \"Imperial\")", //
        "Quantity(1,\"Hours\")");

    check("UnitConvert({Quantity(1,\"Meters\"),Quantity(2,\"Meters\")}, \"Imperial\")", //
        "{Quantity(1250/1143,\"Yards\"),Quantity(2500/1143,\"Yards\")}");

    // Mathematica seeds $UnitSystem from the machine's location; Symja has none, so it starts
    // Metric and is set for the session
    check("$UnitSystem", //
        "Metric");
    check("($UnitSystem = \"Imperial\"; UnitConvert(Quantity(3,\"Meters\"), $UnitSystem))", //
        "Quantity(1250/381,\"Yards\")");
    // and only those two values are accepted
    check("($UnitSystem = \"Metric\"; $UnitSystem = \"Fahrenheit\"; $UnitSystem)", //
        "Metric");

    // a unit name that is neither a unit nor a system is still reported
    check("UnitConvert(Quantity(3,\"Meters\"), \"Nonsense\")", //
        "UnitConvert(Quantity(3,\"Meters\"),Nonsense)");
  }

  /**
   * Around and the first-order propagation of its uncertainty. Every value below was run in
   * Mathematica.
   *
   * <p>
   * Each OCCURRENCE of an Around is an independent measurement, which is why the sum of two equal
   * ones is not twice one of them: 0.1*Sqrt(2) against 0.2. That forces the propagation to happen
   * before Plus collects equal terms and before Times collects equal factors into a power.
   */
  @Test
  public void testAround() {
    // both arguments are made numeric, and that is a fixed point
    check("Around(2, 1/10)", //
        "Around(2.0,0.1)");

    check("Around(2,0.1) + Around(2,0.1)", //
        "Around(4.0,0.141421)");
    check("2*Around(2,0.1)", //
        "Around(4.0,0.2)");
    check("Around(2,0.1) * Around(3,0.2)", //
        "Around(6.0,0.5)");

    // a value without an uncertainty shifts the centre and leaves the uncertainty alone
    check("Around(2,0.1) + 3", //
        "Around(5.0,0.1)");
    check("Around(2,0.1)/2", //
        "Around(1.0,0.05)");
    // two independent measurements of the same thing do not cancel their uncertainties
    check("Around(2,0.1) - Around(2,0.1)", //
        "Around(0.0,0.141421)");

    // f(Around(x,d)) is Around(f(x), Abs(f'(x))*d)
    check("Sin(Around(1.56,0.01))", //
        "Around(0.999942,0.000107961)");
    check("Exp(Around(1.0,0.1))", //
        "Around(2.71828,0.271828)");
    check("Sqrt(Around(4.0,0.1))", //
        "Around(2.0,0.025)");
    check("Log(Around(2.0,0.1))", //
        "Around(0.693147,0.05)");
    // D(Abs(x),x) is the unusable Abs'(x); the slope comes from RealAbs instead
    check("Abs(Around(-2.0,0.1))", //
        "Around(2.0,0.1)");

    // one argument forms
    check("Around(NormalDistribution(0,1))", //
        "Around(0.0,1.0)");
    // a quantity keeps its unit rather than being folded into the magnitude
    check("Around(Quantity(3,\"Meters\"), Quantity(5,\"Centimeters\"))", //
        "Around(Quantity(3.0,\"Meters\"),Quantity(5.0,\"Centimeters\"))");

    // ordinary arithmetic is untouched
    check("2 + 2", //
        "4");
    check("Sin(0.5)", //
        "0.479426");
    check("Abs(-2)", //
        "2");
  }

  /**
   * MeanAround - the mean of a sample together with the uncertainty OF THAT MEAN. Both values were
   * run in Mathematica.
   *
   * <p>
   * For plain numbers that is the standard error, sigma/Sqrt(n) with the sample standard
   * deviation, which is a factor Sqrt(n) smaller than the standard deviation that Around(list)
   * reports. For measured values it is the inverse-variance weighted mean instead: an element with
   * a smaller uncertainty counts for more.
   */
  @Test
  public void testMeanAround() {
    check("MeanAround({1, 2, 3, 4, 3, 2, 1})", //
        "Around(2.28571,0.42056)");
    check("MeanAround({Around(1, 0.1), Around(2, 0.5)})", //
        "Around(1.03846,0.0980581)");

    // Around(list) reports the standard deviation, MeanAround the standard error of the mean
    check("Around({1, 2, 3, 4, 3, 2, 1})", //
        "Around(2.28571,1.1127)");

    // one measurement keeps its own uncertainty
    check("MeanAround({Around(1,0.1)})", //
        "Around(1.0,0.1)");
    // one plain value has no spread to report, and answers itself
    check("MeanAround({5})", //
        "5");
    check("MeanAround({})", //
        "MeanAround({})");
    // a list mixing measured and exact values has no agreed weighting, so it is left alone
    check("MeanAround({1, Around(2,0.1)})", //
        "MeanAround({1,Around(2.0,0.1)})");
  }

  /**
   * AroundReplace - CORRELATED uncertainty propagation, where a variable stands for one value
   * however often it appears. The Mathematica value is the Sin one.
   *
   * <p>
   * This is the counterpart of plain Around arithmetic, which treats every occurrence as an
   * independent measurement. The pair of s+s cases below is the whole distinction.
   *
   * <p>
   * The optional series order defaults to 1. Order 2 is tested separately, in
   * {@link #testAroundReplaceSecondOrder()}.
   */
  @Test
  public void testAroundReplace() {
    check("AroundReplace(Sin(s), s -> Around(1.56, 0.01))", //
        "Around(0.999942,0.000107961)");
    // the same as the plain arithmetic path, which is the first-order rule
    check("AroundReplace(Sin(s), s -> Around(1.56, 0.01)) == Sin(Around(1.56,0.01))", //
        "True");

    // one value, differentiated: d(s+s)/ds is 2, so the uncertainty doubles
    check("AroundReplace(s + s, s -> Around(2, 0.1))", //
        "Around(4.0,0.2)");
    // two independent measurements instead: they add in quadrature
    check("Around(2,0.1) + Around(2,0.1)", //
        "Around(4.0,0.141421)");
    // and the same value cancels itself exactly, where two measurements would not
    check("AroundReplace(s - s, s -> Around(2, 0.1))", //
        "Around(0.0,0.0)");
    check("Around(2,0.1) - Around(2,0.1)", //
        "Around(0.0,0.141421)");

    check("AroundReplace(s^2, s -> Around(2, 0.1))", //
        "Around(4.0,0.4)");

    // different rules are still uncorrelated with each other
    check("AroundReplace(s*r, {s -> Around(2,0.1), r -> Around(3,0.2)})", //
        "Around(6.0,0.5)");
    check("AroundReplace(s + r, {s -> Around(2,0.1), r -> Around(2,0.1)})", //
        "Around(4.0,0.141421)");

    // an exact replacement contributes no uncertainty
    check("AroundReplace(s*r, {s -> Around(2,0.1), r -> 3})", //
        "Around(6.0,0.3)");
    // and with no uncertainty anywhere the answer is an ordinary value
    check("AroundReplace(s + r, {s -> 1, r -> 2})", //
        "3");
  }

  /**
   * VectorAround, as far as it goes: an inert container that keeps exact values and normalizes the
   * correlation-factor spelling into the covariance matrix. Verified against Mathematica for the
   * three input forms below; no arithmetic reads it yet.
   *
   * <p>
   * The guards matter more than the container: a list of vectors is a VectorAround, so Around and
   * MeanAround must decline it rather than build an Around whose two arguments are lists, which is
   * what they did before.
   */
  @Test
  public void testVectorAround() {
    // unlike Around, the arguments are NOT made numeric: MeanAround of exact vectors answers an
    // exact VectorAround
    check("VectorAround({1,2},{1/10,1/5})", //
        "VectorAround({1,2},{1/10,1/5})");
    // a pair of uncertainties with a correlation factor becomes the covariance matrix it stands
    // for, {{d1^2, rho*d1*d2}, {rho*d1*d2, d2^2}}
    check("VectorAround({1.8,2.4},{{0.3,0.4},0.5})", //
        "VectorAround({1.8,2.4},{{0.09,0.06},{0.06,0.16}})");
    check("VectorAround({1.8,2.4},{{0.09,0.02},{0.02,0.16}})", //
        "VectorAround({1.8,2.4},{{0.09,0.02},{0.02,0.16}})");

    // uncertainties with a correlation MATRIX are the general case of the pair form above:
    // Cov(i,j) = delta(i)*delta(j)*R(i,j). Not probed directly, but it has to agree with the pair
    // form on the input they describe in common, and it does.
    check("VectorAround({1.8,2.4},{{0.3,0.4},{{1,0.5},{0.5,1}}})", //
        "VectorAround({1.8,2.4},{{0.09,0.06},{0.06,0.16}})");
    check(
        "VectorAround({1.8,2.4},{{0.3,0.4},{{1,0.5},{0.5,1}}}) == VectorAround({1.8,2.4},{{0.3,0.4},0.5})", //
        "True");
    // an identity correlation leaves the variances on the diagonal and nothing off it
    check("VectorAround({1,2,3},{{1,2,3},{{1,0,0},{0,1,0},{0,0,1}}})", //
        "VectorAround({1,2,3},{{1,0,0},{0,4,0},{0,0,9}})");
    check("VectorAround({1,2,3},{{1,2,3},{{1,1/2,0},{1/2,1,1/3},{0,1/3,1}}})", //
        "VectorAround({1,2,3},{{1,1,0},{1,4,2},{0,2,9}})");
    // a correlation matrix that does not match the uncertainties is left alone
    check("VectorAround({1,2},{{0.3,0.4},{{1,0.5,0},{0.5,1,0},{0,0,1}}})", //
        "VectorAround({1,2},{{0.3,0.4},\n{{1,0.5,0},\n {0.5,1,0},\n {0,0,1}}})");
    // a scalar is not a vector of values, and a scalar is not an uncertainty specification
    check("VectorAround(3,{0.1})", //
        "VectorAround(3,{0.1})");
    check("VectorAround({1,2},0.1)", //
        "VectorAround({1,2},0.1)");

    // MeanAround of a list of vectors is a VectorAround carrying the covariance OF THE MEAN,
    // which is the sample covariance divided by the number of observations - and it stays exact
    check("MeanAround({{1,2},{3,5},{4,4},{2,3}})", //
        "VectorAround({5/2,7/2},{{5/12,1/3},{1/3,5/12}})");
    // that covariance is Covariance(data)/n
    check(
        "MeanAround({{1,2},{3,5},{4,4},{2,3}})[[2]] == Covariance({{1,2},{3,5},{4,4},{2,3}})/4", //
        "True");
    // and the values are the columnwise means
    check("MeanAround({{1,2},{3,5},{4,4},{2,3}})[[1]] == Mean({{1,2},{3,5},{4,4},{2,3}})", //
        "True");
    check("MeanAround({{1,2,3},{4,5,7},{2,2,2},{6,1,4}})", //
        "VectorAround({13/4,5/2,4},{{59/48,-1/24,7/12},{-1/24,3/4,3/4},{7/12,3/4,7/6}})");
    check("MeanAround({{1},{2},{3}})", //
        "VectorAround({2},{{1/3}})");
    // one observation has no covariance to report
    check("MeanAround({{1,2}})", //
        "MeanAround(\n{{1,2}})");

    // Around of a list of vectors is left alone in Mathematica too, so this is not a gap
    check("Around({{1,2},{3,5}})", //
        "Around({{1,2},{3,5}})");
    check("Around({{1,2},{3,5},{4,4},{2,3}})", //
        "Around({{1,2},{3,5},{4,4},{2,3}})");

    // addition is componentwise, with the uncertainties in quadrature - each VectorAround is an
    // independent measurement, as for a scalar Around
    check("VectorAround({1.8,2.4},{0.3,0.4}) + VectorAround({1.,1.},{0.1,0.1})", //
        "VectorAround({2.8,3.4},{0.316228,0.412311})");
    check("VectorAround({1,2},{3,4}) + VectorAround({1,1},{4,3})", //
        "VectorAround({2,3},{5,5})");
    check("VectorAround({1,2},{1,2}) + VectorAround({1,1},{2,2}) + VectorAround({0,0},{2,1})", //
        "VectorAround({2,3},{3,3})");
    // in the covariance spelling the matrices add instead
    check("VectorAround({1,2},{{1,0},{0,2}}) + VectorAround({3,4},{{2,1},{1,1}})", //
        "VectorAround({4,6},{{3,1},{1,3}})");

    // the two spellings are not mixed, and lengths have to agree
    check("VectorAround({1,2},{0.3,0.4}) + VectorAround({1,1},{{1,0},{0,1}})", //
        "VectorAround({1,1},\n{{1,0},\n {0,1}})+VectorAround({1,2},{0.3,0.4})");
    check("VectorAround({1,2},{0.3,0.4}) + VectorAround({1,1,1},{0.1,0.1,0.1})", //
        "VectorAround({1,2},{0.3,0.4})+VectorAround({1,1,1},{0.1,0.1,0.1})");
    check("VectorAround({1,2},{0.3,0.4}) + 5", //
        "5+VectorAround({1,2},{0.3,0.4})");

    // scaling multiplies the values by the factor and the uncertainties by its absolute value
    check("2*VectorAround({1.8, 2.4}, {0.3, 0.4})", //
        "VectorAround({3.6,4.8},{0.6,0.8})");
    check("-VectorAround({1,2},{3,4})", //
        "VectorAround({-1,-2},{3,4})");
    check("VectorAround({1,2},{3,4})/2", //
        "VectorAround({1/2,1},{3/2,2})");
    // a covariance scales by the SQUARE of the factor
    check("2*VectorAround({1,2},{{1,0},{0,2}})", //
        "VectorAround({2,4},{{4,0},{0,8}})");
    // a product of two measured vectors, or a symbolic factor, is left alone
    check("VectorAround({1,2},{3,4}) * VectorAround({1,1},{1,1})", //
        "VectorAround({1,1},{1,1})*VectorAround({1,2},{3,4})");
    check("x*VectorAround({1,2},{3,4})", //
        "x*VectorAround({1,2},{3,4})");

    // the flat vector cases are unaffected
    check("MeanAround({1, 2, 3, 4, 3, 2, 1})", //
        "Around(2.28571,0.42056)");
    check("Around({1, 2, 3, 4, 3, 2, 1})", //
        "Around(2.28571,1.1127)");
  }

  /**
   * FormulaData - named formulas as equations over QuantityVariable. The two OhmsLaw results were
   * run in Mathematica; the other formula NAMES are this implementation's own and may not match
   * Wolfram's catalogue, which holds hundreds.
   *
   * <p>
   * Solving happens on plain magnitudes: each given value is expressed in its variable's canonical
   * unit, the equation is solved over ordinary numbers, and the unknown's canonical unit is
   * attached. Solve cannot handle a quantity equation directly.
   */
  @Test
  public void testFormulaData() {
    check("FormulaData(\"OhmsLaw\")", //
        "QuantityVariable(V,ElectricPotential)==QuantityVariable(I,ElectricCurrent)*QuantityVariable(R,ElectricResistance)");
    check("FormulaData(\"OhmsLaw\", {\"V\" -> Quantity(1, \"Volts\"), \"R\" -> Quantity(1, \"Ohms\")})", //
        "QuantityVariable(I,ElectricCurrent)==Quantity(1,\"Amperes\")");

    // any variable can be the unknown
    check("FormulaData(\"OhmsLaw\", {\"I\" -> Quantity(2, \"Amperes\"), \"R\" -> Quantity(3, \"Ohms\")})", //
        "QuantityVariable(V,ElectricPotential)==Quantity(6,\"Volts\")");
    // and the given values are converted into the variable's canonical unit first
    check(
        "FormulaData(\"OhmsLaw\", {\"V\" -> Quantity(1000, \"Millivolts\"), \"R\" -> Quantity(1, \"Ohms\")})", //
        "QuantityVariable(I,ElectricCurrent)==Quantity(1,\"Amperes\")");

    // derived units come out named
    check(
        "FormulaData(\"NewtonsSecondLaw\", {\"m\" -> Quantity(2,\"Kilograms\"), \"a\" -> Quantity(3,\"Meters\")/Quantity(1,\"Seconds\")^2})", //
        "QuantityVariable(F,Force)==Quantity(6,\"Newtons\")");
    check(
        "FormulaData(\"KineticEnergy\", {\"m\" -> Quantity(2,\"Kilograms\"), \"v\" -> Quantity(3,\"Meters\")/Quantity(1,\"Seconds\")})", //
        "QuantityVariable(E,Energy)==Quantity(9,\"Joules\")");

    check("FormulaData(\"OhmsLaw\", \"QuantityVariableNames\")", //
        "{V,I,R}");
    check("FormulaData(\"Properties\")", //
        "{Equation,QuantityVariableNames,QuantityVariables}");
    check("Length(FormulaData()) > 0", //
        "True");
    check("FormulaData(\"NoSuchFormula\")", //
        "FormulaData(NoSuchFormula)");
  }

  /**
   * The asymmetric Around({x, {deltaMinus, deltaPlus}}) form.
   *
   * <p>
   * Mathematica-verified. Reading the pair as the interval from x-deltaMinus to x+deltaPlus
   * predicts that a decreasing map swaps the two sides, and multiplication by a negative number
   * does - but a general unary function does NOT, even a decreasing one. Mathematica is
   * inconsistent here and this follows it rather than the interval reading.
   *
   * <p>
   * It runs on its own path, entered only when an operand is asymmetric, so the verified symmetric
   * behaviour cannot be disturbed by it.
   */
  @Test
  public void testAroundAsymmetric() {
    check("Around(2, {1/10, 3/10})", //
        "Around(2.0,{0.1,0.3})");

    // the two sides propagate independently; addition cannot swap them
    check("Around(2,{0.1,0.3}) + Around(2,{0.1,0.3})", //
        "Around(4.0,{0.141421,0.424264})");
    check("Around(2,{0.1,0.3}) + 5", //
        "Around(7.0,{0.1,0.3})");
    // an asymmetric and a symmetric measurement combine side by side
    check("Around(2,{0.1,0.3}) + Around(1,0.4)", //
        "Around(3.0,{0.412311,0.5})");

    check("2*Around(2,{0.1,0.3})", //
        "Around(4.0,{0.2,0.6})");
    // a negative factor swaps the sides
    check("-Around(2,{0.1,0.3})", //
        "Around(-2.0,{0.3,0.1})");
    // subtracting a measurement from itself gives equal sides - negating swapped them - but the
    // pair is KEPT rather than collapsing to the one-uncertainty spelling
    check("Around(2,{0.1,0.3}) - Around(2,{0.1,0.3})", //
        "Around(0.0,{0.3162277660168379,0.3162277660168379})");

    // a unary function scales each side where it stands. It does NOT swap them, not even a
    // decreasing one - unlike multiplication by a negative number just above.
    check("Around(2,{0.1,0.3})^2", //
        "Around(4.0,{0.4,1.2})");
    check("1/Around(2,{0.1,0.3})", //
        "Around(0.5,{0.025,0.075})");
    check("Exp(Around(0.0,{0.1,0.3}))", //
        "Around(1.0,{0.1,0.3})");

    // and none of the verified symmetric behaviour moves
    check("Around(2,0.1)+Around(2,0.1)", //
        "Around(4.0,0.141421)");
    check("Around(2,0.1)*Around(3,0.2)", //
        "Around(6.0,0.5)");
    check("Sin(Around(1.56,0.01))", //
        "Around(0.999942,0.000107961)");
  }

  /**
   * Second-order AroundReplace: the centre moves off f(x) by f''(x)*delta^2/2, and the uncertainty
   * picks up the matching term.
   *
   * <p>
   * Pinned to Mathematica by the s^2 case, where first order would answer Around(4., 0.4).
   *
   * <p>
   * An earlier transcript appeared to contradict this, reporting the Sin case at order 2 as the
   * FIRST-order pair (Sin[1.56], Cos[1.56]*0.01). That value sat beneath two blank output lines
   * and turned out to be the neighbouring first-order line's answer; a clean re-probe gave the
   * second-order value asserted below. Both orders of the Sin case are pinned here so that the
   * apparent contradiction cannot be reintroduced.
   */
  @Test
  public void testAroundReplaceSecondOrder() {
    check("AroundReplace(s^2, s -> Around(2,0.1), 2)", //
        "Around(4.01,0.40025)");
    // where first order keeps the centre at 4
    check("AroundReplace(s^2, s -> Around(2,0.1))", //
        "Around(4.0,0.4)");

    // a linear function has no curvature, so the two orders have to agree - and do
    check("AroundReplace(3*s, s -> Around(2,0.1), 2) == AroundReplace(3*s, s -> Around(2,0.1))", //
        "True");
    check("AroundReplace(s + s, s -> Around(2,0.1), 1)", //
        "Around(4.0,0.2)");

    // a transcendental function moves too, and by the same rule
    check("N(AroundReplace(Sin(s), s -> Around(1.56,0.01), 2), 20)", //
        "Around(0.9998917231439548,0.0001290543744817026)");
    check("AroundReplace(Sin(s), s -> Around(1.56,0.01))", //
        "Around(0.999942,0.000107961)");

    // an exact rule alongside the uncertain one carries through
    check("AroundReplace(r*s^2, {s -> Around(2,0.1), r -> 3}, 2)", //
        "Around(12.03,1.20075)");

    // orders above two are not implemented, and several uncertain variables at order two would
    // need the mixed second derivatives - both are left unevaluated rather than answered wrongly
    check("AroundReplace(s^2, s -> Around(2,0.1), 3)", //
        "AroundReplace(s^2,s->Around(2.0,0.1),3)");
    check("AroundReplace(s*r, {s -> Around(2,0.1), r -> Around(3,0.2)}, 2)", //
        "AroundReplace(r*s,{s->Around(2.0,0.1),r->Around(3.0,0.2)},2)");
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

  @Test
  public void testAtomicMassUnit() {
    // the same physical unit as the dalton, but reported under its own name, as ElementData does
    check("KnownUnitQ(\"AtomicMassUnit\")", //
        "True");
    check("UnitConvert(Quantity(1.0, \"AtomicMassUnit\"), \"Daltons\")", //
        "Quantity(1.0,\"Daltons\")");
    check("UnitConvert(Quantity(1.0, \"AtomicMassUnit\"), \"Kilograms\")", //
        "Quantity(1.66054*10^-27,\"Kilograms\")");
  }

  @Test
  public void testMolarElectronvolts() {
    // One electronvolt per particle, so one per mole of them: the Faraday constant, 96.485 kJ/mol.
    // It is NOT electronvolts per mole, which is smaller by Avogadro's number - a plausible
    // reading that would put every ionization energy out by 6.022*10^23.
    check("UnitConvert(Quantity(1.0, \"MolarElectronvolts\"), \"Kilojoules\"/\"Moles\")", //
        "Quantity(96.48533,\"Kilojoules\"*\"Moles\"^(-1))");
    // tungsten's first ionization energy, 770 kJ/mol
    check("UnitConvert(Quantity(770.0, \"Kilojoules\"/\"Moles\"), \"MolarElectronvolts\")", //
        "Quantity(7.98049,\"MolarElectronvolts\")");
    check("CompatibleUnitQ(Quantity(1, \"MolarElectronvolts\"), "
        + "Quantity(1, \"Kilojoules\"/\"Moles\"))", //
        "True");
  }
}
