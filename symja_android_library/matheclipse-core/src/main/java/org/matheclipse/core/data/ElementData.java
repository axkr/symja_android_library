package org.matheclipse.core.data;

import static org.matheclipse.core.expression.F.NIL;
import java.util.HashMap;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>ElementData(name, property)</code> - the value of a property of a chemical element.
 *
 * <p>
 * An element is named, numbered, given by symbol, or given as an <code>Entity("Element", name)</code>
 * ; a property is named or given as an <code>EntityProperty("Element", property)</code>. Without a
 * property the result is the element's entity, and with no argument at all the whole periodic
 * table.
 *
 * <p>
 * A property that is a measurement carries its unit as a {@link S#Quantity}; the rest - atomic
 * numbers, Pauling electronegativities, Mohs hardnesses, Poisson ratios, names and configurations -
 * are plain values. The property names are the reference implementation's; one that used to exist
 * under another name reports what replaced it rather than answering with missing data.
 *
 * <p>
 * The table is generated from <code>element.csv</code> by
 * <code>org.matheclipse.core.preprocessor.ElementPreprocessor</code>, which also writes the column
 * names this class looks properties up by. Its data comes from
 * <a href= "https://en.wikipedia.org/wiki/List_of_data_references_for_chemical_elements">Wikipedia
 * - List of data references for chemical elements</a>.
 *
 * <p>
 * See <code>doc/functions/ElementData.md</code> for examples.
 */
public class ElementData extends AbstractFunctionEvaluator {

  /**
   * Properties worked out from the table rather than stored in it.
   *
   * <p>
   * Four follow from the atomic number, the electron configuration and the two transition
   * temperatures, and are always available. The isotope ones are answered by
   * <code>IsotopeData</code>, which reads CDK's isotope table in <code>matheclipse-chem</code>;
   * without that module they report themselves unavailable, as every other function of that module
   * does.
   */
  private static final String[] COMPUTED_PROPERTIES = { //
      "ProtonCount", "ElectronCount", "ValenceElectronCount", "MolarMass", "Phase", //
      "NeutronCount", "KnownIsotopes", "IsotopeAbundances"};

  /**
   * The temperature a phase is read at, in degrees Celsius, standard conditions being 25 &deg;C at
   * one atmosphere.
   */
  private static final double STANDARD_TEMPERATURE = 25.0;

  /** Every property this table can answer, in alphabetical order, as the reference does. */
  public static final String[] PROPERTIES_DATA = sortedProperties();

  private static String[] sortedProperties() {
    String[] columns = ElementData1.COLUMNS;
    String[] properties = new String[columns.length + COMPUTED_PROPERTIES.length];
    System.arraycopy(columns, 0, properties, 0, columns.length);
    System.arraycopy(COMPUTED_PROPERTIES, 0, properties, columns.length,
        COMPUTED_PROPERTIES.length);
    java.util.Arrays.sort(properties);
    return properties;
  }

  private static boolean isComputed(String property) {
    for (int i = 0; i < COMPUTED_PROPERTIES.length; i++) {
      if (COMPUTED_PROPERTIES[i].equals(property)) {
        return true;
      }
    }
    return false;
  }

  /**
   * A property worked out rather than looked up.
   *
   * <p>
   * The valence count is the electrons of the outermost shell, plus those of an unfilled d subshell
   * one shell in and an unfilled f subshell two shells in - the usual reading, which gives carbon
   * four, iron eight and tungsten six.
   */
  private static IExpr computed(String property, IAST row, EvalEngine engine) {
    IExpr atomicNumber = column("AtomicNumber", row);
    if ("ProtonCount".equals(property) || "ElectronCount".equals(property)) {
      // a neutral atom has as many of either as its atomic number
      return atomicNumber;
    }
    if ("MolarMass".equals(property)) {
      IExpr mass = column("AtomicMass", row);
      double d = mass.evalfNaN();
      return Double.isNaN(d) ? F.Missing(S.NotAvailable)
          : F.binaryAST2(S.Quantity, F.num(d), F.Divide(unit("Grams"), unit("Moles")));
    }
    if ("ValenceElectronCount".equals(property)) {
      return valenceElectronCount(column("ElectronicConfiguration", row));
    }
    if ("Phase".equals(property)) {
      return phase(column("MeltingPoint", row), column("BoilingPoint", row));
    }
    return isotopeProperty(property, row, atomicNumber, engine);
  }

  /**
   * The state an element is in at standard conditions, read off the two temperatures at which it
   * changes state.
   *
   * <p>
   * An element that boils at or below the standard temperature is a gas there, one that melts at or
   * below it a liquid, and everything else a solid. Both temperatures have to be known to say which
   * of the three it is - a melting point alone settles it only when the element is still solid at
   * the standard temperature, and the fifteen heaviest elements have neither.
   *
   * @param meltingPoint the melting point in degrees Celsius, or missing data
   * @param boilingPoint the boiling point in degrees Celsius, or missing data
   */
  private static IExpr phase(IExpr meltingPoint, IExpr boilingPoint) {
    double melting = meltingPoint.evalfNaN();
    double boiling = boilingPoint.evalfNaN();
    if (!Double.isNaN(boiling) && boiling <= STANDARD_TEMPERATURE) {
      return F.stringx("Gas");
    }
    if (!Double.isNaN(melting) && melting > STANDARD_TEMPERATURE) {
      return F.stringx("Solid");
    }
    if (!Double.isNaN(melting) && !Double.isNaN(boiling)) {
      return F.stringx("Liquid");
    }
    return F.Missing(S.NotAvailable);
  }

  private static IExpr column(String property, IAST row) {
    Integer index = COLUMN_INDEX.get(property);
    if (index == null || index.intValue() >= row.size()) {
      return F.Missing(S.NotAvailable);
    }
    return row.get(index.intValue());
  }

  private static IExpr valenceElectronCount(IExpr configuration) {
    if (!configuration.isList()) {
      return F.Missing(S.NotAvailable);
    }
    IAST shells = (IAST) configuration;
    int count = shells.argSize();
    if (count == 0) {
      return F.Missing(S.NotAvailable);
    }
    int valence = 0;
    IExpr outermost = shells.get(count);
    if (outermost.isList()) {
      IAST subshells = (IAST) outermost;
      for (int i = 1; i <= subshells.argSize(); i++) {
        valence += subshells.get(i).toIntDefault(0);
      }
    }
    valence += unfilled(shells, count - 1, 3, 10);
    valence += unfilled(shells, count - 2, 4, 14);
    return F.ZZ(valence);
  }

  /** The electrons of one subshell of one shell, when that subshell is not yet full. */
  private static int unfilled(IAST shells, int shell, int subshell, int capacity) {
    if (shell < 1 || shell > shells.argSize()) {
      return 0;
    }
    IExpr entry = shells.get(shell);
    if (!entry.isList()) {
      return 0;
    }
    IAST subshells = (IAST) entry;
    if (subshells.argSize() < subshell) {
      return 0;
    }
    int electrons = subshells.get(subshell).toIntDefault(0);
    return electrons < capacity ? electrons : 0;
  }

  /**
   * The isotope properties, asked of <code>IsotopeData</code> through the evaluator rather than
   * through a dependency: the isotope table belongs to <code>matheclipse-chem</code>, and
   * <code>matheclipse-core</code> does not depend on it or on CDK.
   */
  private static IExpr isotopeProperty(String property, IAST row, IExpr atomicNumber,
      EvalEngine engine) {
    IExpr symbol = column("AtomicSymbol", row);
    if (!symbol.isString()) {
      return F.Missing(S.NotAvailable);
    }
    if ("KnownIsotopes".equals(property)) {
      return isotopeResult(engine.evaluate(F.unaryAST1(S.IsotopeData, symbol)));
    }
    if ("IsotopeAbundances".equals(property)) {
      return isotopeResult(
          engine.evaluate(F.binaryAST2(S.IsotopeData, symbol, F.stringx("Abundances"))));
    }
    // NeutronCount: the nucleons of the most abundant isotope that are not protons
    IExpr massNumber = isotopeResult(
        engine.evaluate(F.binaryAST2(S.IsotopeData, symbol, F.stringx("MassNumber"))));
    if (!massNumber.isInteger() || !atomicNumber.isInteger()) {
      return F.Missing(S.NotAvailable);
    }
    return F.ZZ(massNumber.toIntDefault(0) - atomicNumber.toIntDefault(0));
  }

  /** Without <code>matheclipse-chem</code> the call stays unevaluated, which is not an answer. */
  private static IExpr isotopeResult(IExpr result) {
    return result.isAST(S.IsotopeData) ? F.Missing(S.NotAvailable) : result;
  }

  private static java.util.Map<IExpr, IExpr> MAP_NUMBER_NAME = new HashMap<IExpr, IExpr>();

  private static java.util.Map<IExpr, IAST> MAP_NAME_DATA = new HashMap<IExpr, IAST>();

  /**
   * The unit each property is reported in, and the factor from the number stored in the table to
   * it.
   *
   * <p>
   * The table records what its sources recorded - densities in kilograms per cubic metre,
   * ionization energies in kilojoules per mole - while the reference implementation reports grams
   * per cubic centimetre and molar electronvolts. Those two are converted here so that the
   * quantities read the same; every other column is already in the unit it is reported in.
   *
   * <p>
   * A property absent from this table has no unit: an atomic number, a Pauling electronegativity,
   * a Mohs hardness and a Poisson ratio are all plain numbers, and the rest are names or
   * configurations.
   */
  private static final java.util.Map<String, IExpr> PROPERTY_UNIT = propertyUnits();

  /** Multiplier from the stored number to {@link #PROPERTY_UNIT}, where the two differ. */
  private static final java.util.Map<String, Double> PROPERTY_FACTOR = propertyFactors();

  /**
   * One molar electronvolt in kilojoules per mole - the Faraday constant, and the same number the
   * unit itself is registered with.
   */
  private static final double MOLAR_ELECTRONVOLT_IN_KJ_PER_MOL = 96.48533212331;

  private static IExpr unit(String name) {
    return F.stringx(name);
  }

  private static java.util.Map<String, IExpr> propertyUnits() {
    java.util.Map<String, IExpr> map = new HashMap<String, IExpr>();
    map.put("AtomicMass", unit("AtomicMassUnit"));
    IExpr density = F.Divide(unit("Grams"), F.Power(unit("Centimeters"), F.C3));
    map.put("MassDensity", density);
    map.put("LiquidDensity", density);
    map.put("MeltingPoint", unit("DegreesCelsius"));
    map.put("BoilingPoint", unit("DegreesCelsius"));
    map.put("SpecificHeat", F.Divide(unit("Joules"), F.Times(unit("Kilograms"), unit("Kelvins"))));
    IExpr molarEnergy = F.Divide(unit("Kilojoules"), unit("Moles"));
    map.put("FusionHeat", molarEnergy);
    map.put("VaporizationHeat", molarEnergy);
    map.put("ElectronAffinity", molarEnergy);
    map.put("IonizationEnergies", unit("MolarElectronvolts"));
    IExpr pressure = unit("Megapascals");
    map.put("VickersHardness", pressure);
    map.put("BrinellHardness", pressure);
    IExpr modulus = unit("Gigapascals");
    map.put("YoungModulus", modulus);
    map.put("BulkModulus", modulus);
    map.put("ShearModulus", modulus);
    IExpr length = unit("Picometers");
    map.put("AtomicRadius", length);
    map.put("VanDerWaalsRadius", length);
    map.put("CovalentRadius", length);
    map.put("ThermalConductivity",
        F.Divide(unit("Watts"), F.Times(unit("Kelvins"), unit("Meters"))));
    return map;
  }

  private static java.util.Map<String, Double> propertyFactors() {
    java.util.Map<String, Double> map = new HashMap<String, Double>();
    // kilograms per cubic metre to grams per cubic centimetre
    map.put("MassDensity", Double.valueOf(0.001));
    map.put("LiquidDensity", Double.valueOf(0.001));
    // kilojoules per mole to molar electronvolts
    map.put("IonizationEnergies",
        Double.valueOf(1.0 / MOLAR_ELECTRONVOLT_IN_KJ_PER_MOL));
    return map;
  }

  /**
   * The stored number as a {@link S#Quantity} in the property's unit.
   *
   * <p>
   * Missing data, names and electron configurations are returned untouched; a list of numbers -
   * the ionization energies - becomes a list of quantities, one per ionization.
   */
  private static IExpr withUnit(String property, IExpr value) {
    IExpr unit = PROPERTY_UNIT.get(property);
    if (unit == null || value.isString() || value.isAST(S.Missing)) {
      return value;
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      return list.map(x -> withUnit(property, x));
    }
    double magnitude = value.evalfNaN();
    if (Double.isNaN(magnitude)) {
      return value;
    }
    Double factor = PROPERTY_FACTOR.get(property);
    if (factor != null) {
      magnitude *= factor.doubleValue();
    }
    // a measurement is inexact, and an exact magnitude would make every conversion of it print as
    // a ratio of large integers rather than a number
    return F.binaryAST2(S.Quantity, F.num(magnitude), unit);
  }

  /**
   * Properties that used to exist under another name, and what replaced them.
   *
   * <p>
   * The old names are not accepted: the table follows the reference implementation's vocabulary,
   * and a name outside it is reported rather than quietly answered. The two absolute temperatures
   * are here too - they existed only because the values used to be bare numbers, and a temperature
   * that carries its unit can simply be converted.
   */
  private static final java.util.Map<String, String> REPLACED_PROPERTY = replacedProperties();

  private static java.util.Map<String, String> replacedProperties() {
    java.util.Map<String, String> map = new HashMap<String, String>();
    map.put("Abbreviation", "AtomicSymbol");
    map.put("StandardName", "Name");
    map.put("AtomicWeight", "AtomicMass");
    map.put("Density", "MassDensity");
    map.put("DiscoveryYear", "DiscoveryDate");
    map.put("ElectroNegativity", "Electronegativity");
    map.put("ElectronConfiguration", "ElectronicConfiguration");
    map.put("ElectronConfigurationString", "ShortElectronicConfiguration");
    map.put("AbsoluteMeltingPoint", "MeltingPoint");
    map.put("AbsoluteBoilingPoint", "BoilingPoint");
    return map;
  }

  /** Property name to its 1-based column in a row of {@link ElementData1#ELEMENTS}. */
  private static final java.util.Map<String, Integer> COLUMN_INDEX = columnIndex();

  private static java.util.Map<String, Integer> columnIndex() {
    java.util.Map<String, Integer> map = new HashMap<String, Integer>();
    for (int i = 0; i < ElementData1.COLUMNS.length; i++) {
      map.put(ElementData1.COLUMNS[i], Integer.valueOf(i + 1));
    }
    return map;
  }

  /** The entity type these entities and properties belong to. */
  private static final IExpr ELEMENT = F.stringx("Element");

  public ElementData() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST0()) {
      // ElementData() is the whole periodic table, as entities
      return F.mapRange(1, 119, z -> entityOf(F.ZZ(z)));
    }
    IExpr arg1 = elementOf(ast.arg1());
    if (ast.size() == 2) {
      if (arg1.isString() && arg1.toString().equals("Properties")) {
        return F.mapRange(0, PROPERTIES_DATA.length,
            i -> F.binaryAST2(S.EntityProperty, ELEMENT, F.stringx(PROPERTIES_DATA[i])));
      }
      // a lone element is the entity that stands for it
      return MAP_NAME_DATA.containsKey(arg1) ? entityOf(arg1) : F.NIL;
    }
    IAST propertyList = MAP_NAME_DATA.get(arg1);
    if (propertyList == null) {
      return F.NIL;
    }
    return dataPoint(ast, propertyList, engine);
  }

  /** The entity naming an element, given anything that identifies it. */
  private static IExpr entityOf(IExpr identifier) {
    IExpr name = MAP_NUMBER_NAME.get(identifier);
    if (name == null) {
      IAST data = MAP_NAME_DATA.get(identifier);
      if (data == null) {
        return F.NIL;
      }
      name = data.arg3();
    }
    return F.binaryAST2(S.Entity, ELEMENT, name);
  }

  /**
   * What identifies an element: its atomic number, its name, its symbol, or the entity standing
   * for it.
   */
  private static IExpr elementOf(IExpr expr) {
    if (expr.isAST(S.Entity, 3) && ELEMENT.equals(expr.first())) {
      return ((IAST) expr).arg2();
    }
    return expr;
  }

  /** The property being asked for, whether named directly or through an EntityProperty. */
  private static String propertyOf(IExpr expr) {
    if (expr.isAST(S.EntityProperty, 3) && ELEMENT.equals(expr.first())) {
      return ((IAST) expr).arg2().toString();
    }
    return expr.toString();
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_0_2;
  }

  /**
   * The value of one property of one element.
   *
   * <p>
   * Which column a property lives in comes from {@link ElementData1#COLUMNS}, which the same
   * generator writes as it writes the table itself. A property name that is not a column, or a row
   * too short to reach it, is missing data rather than an error.
   */
  private IExpr dataPoint(final IAST ast, IAST propertyList, EvalEngine engine) {
    String property = propertyOf(ast.arg2());
    if (isComputed(property)) {
      return computed(property, propertyList, engine);
    }
    Integer index = COLUMN_INDEX.get(property);
    if (index == null) {
      // a name this table does not know is reported, not answered with missing data
      String replacement = REPLACED_PROPERTY.get(property);
      if (replacement != null) {
        return Errors.printMessage(ast.topHead(), "elemdrepl",
            F.List(F.stringx(property), F.stringx(replacement)), engine);
      }
      return Errors.printMessage(ast.topHead(), "elemdprop", F.List(F.stringx(property)), engine);
    }
    if (index.intValue() >= propertyList.size()) {
      return F.Missing(S.NotAvailable);
    }
    return withUnit(property, propertyList.get(index.intValue()));
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IAST[] list = ElementData1.ELEMENTS;
    for (int i = 0; i < list.length; i++) {
      final IAST iList = list[i];
      MAP_NUMBER_NAME.put(iList.arg1(), iList.arg3());
      IASTAppendable subList = F.mapRange(1, iList.size(), j -> iList.get(j));
      MAP_NAME_DATA.put(iList.arg1(), subList);
      MAP_NAME_DATA.put(iList.arg2(), subList);
      MAP_NAME_DATA.put(iList.arg3(), subList);
    }
    list = ElementData2.ELEMENTS;
    for (int i = 0; i < list.length; i++) {
      final IAST iList = list[i];
      MAP_NUMBER_NAME.put(iList.arg1(), iList.arg3());
      IASTAppendable subList = F.mapRange(1, iList.size(), j -> iList.get(j));
      MAP_NAME_DATA.put(iList.arg1(), subList);
      MAP_NAME_DATA.put(iList.arg2(), subList);
      MAP_NAME_DATA.put(iList.arg3(), subList);
    }
  }
}
