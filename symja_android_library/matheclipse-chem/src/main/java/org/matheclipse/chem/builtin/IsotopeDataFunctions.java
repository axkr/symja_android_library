package org.matheclipse.chem.builtin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.matheclipse.core.data.Entities;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IIsotope;

/**
 * <code>IsotopeData</code> over CDK's bundled BODR nuclide table - every isotope of every element,
 * not only the ones that occur in nature.
 *
 * <p>
 * A single nuclide is named by an entity, <code>Entity("Isotope", "Carbon12")</code>, so that it can
 * be handed to <code>EntityValue</code> like any other thing this system knows about. The element
 * forms answer for the whole element, and the two are told apart by the shape of the argument
 * rather than by the spelling of the property:
 *
 * <table>
 * <caption>what each form answers</caption>
 * <tr>
 * <td><code>IsotopeData("C")</code>, <code>IsotopeData(6)</code></td>
 * <td>that element's nuclides, as entities, lightest first</td>
 * </tr>
 * <tr>
 * <td><code>IsotopeData("Carbon12")</code>, <code>IsotopeData({"Carbon", 12})</code></td>
 * <td>the one nuclide, as an entity</td>
 * </tr>
 * <tr>
 * <td><code>IsotopeData()</code>, <code>IsotopeData(All)</code></td>
 * <td>every nuclide, by atomic number then mass number</td>
 * </tr>
 * </table>
 *
 * <p>
 * Abundances are reported in two different units, which is the one genuinely confusing thing here.
 * CDK tabulates percent, and the element forms hand that on unchanged -
 * <code>IsotopeData("C", "Abundances")</code> is <code>{12-&gt;98.93, 13-&gt;1.07}</code>, which is
 * what <code>ElementData("Carbon", "IsotopeAbundances")</code> reads. An entity's
 * <code>"IsotopeAbundance"</code> is a fraction of one, <code>0.9893</code>, which is what the
 * Wolfram Language answers for the same question.
 *
 * <p>
 * A nuclide that could exist but is not in the table - <code>"Carbon99"</code> - is no answer at
 * all rather than <code>Missing</code>, and so is an unknown property of a nuclide. That is what
 * lets <code>EntityValue</code> ask again and say which half of the question it did not know. The
 * element forms keep answering <code>Missing(NotAvailable)</code>, as they always have.
 */
public class IsotopeDataFunctions {

  private static final String ISOTOPE = "Isotope";

  private static class Initializer {

    private static void init() {
      S.IsotopeData.setEvaluator(new IsotopeData());
      Entities.register(ISOTOPE, S.IsotopeData);
    }
  }

  /** Mass of a free neutron in daltons (CODATA 2018). */
  private static final double NEUTRON_MASS_IN_DALTONS = 1.00866491595;

  /** 1 u expressed as an energy, in MeV/c² (CODATA 2018). */
  private static final double DALTON_IN_MEGAELECTRONVOLTS = 931.49410242;

  /**
   * The properties one nuclide answers for, in the order <code>IsotopeData("Properties")</code>
   * reports them. The switch below reads from this array, so the two cannot drift apart.
   */
  private static final String[] ISOTOPE_PROPERTIES = {"AtomicMass", "AtomicNumber", "BindingEnergy",
      "IsotopeAbundance", "MassNumber", "NeutronNumber", "StandardName"};

  private static volatile IsotopeFactory factory;

  /** Each element's nuclides, sorted by mass number, with the unusable ones dropped. */
  private static final Map<Integer, IIsotope[]> byElement = new ConcurrentHashMap<Integer, IIsotope[]>();

  /** Every nuclide as an entity, built once: there are more than three thousand of them. */
  private static volatile IAST allEntities;

  /** Symja's own spelling of each element's name, indexed by atomic number. */
  private static volatile String[] elementNames;

  /** Element name and atomic symbol to atomic number, both folded to lower case. */
  private static volatile Map<String, Integer> elementNumbers;

  private static IsotopeFactory factory() {
    IsotopeFactory local = factory;
    if (local == null) {
      synchronized (IsotopeDataFunctions.class) {
        local = factory;
        if (local == null) {
          try {
            local = Isotopes.getInstance();
          } catch (Exception e) {
            return null;
          }
          factory = local;
        }
      }
    }
    return local;
  }

  /**
   * The nuclides of one element, lightest first.
   *
   * <p>
   * CDK hands them back in its own file order and includes entries without a mass number or an
   * exact mass, which nothing here can answer for, so they are dropped once rather than guarded
   * against at every use.
   */
  private static IIsotope[] isotopesOf(int atomicNumber) {
    return byElement.computeIfAbsent(Integer.valueOf(atomicNumber), z -> {
      IsotopeFactory f = factory();
      if (f == null) {
        return new IIsotope[0];
      }
      IIsotope[] isotopes;
      try {
        isotopes = f.getIsotopes(org.openscience.cdk.config.Elements.ofNumber(z.intValue()).symbol());
      } catch (Exception e) {
        return new IIsotope[0];
      }
      if (isotopes == null) {
        return new IIsotope[0];
      }
      IIsotope[] usable = Arrays.stream(isotopes)
          .filter(i -> i.getMassNumber() != null && i.getExactMass() != null)
          .toArray(IIsotope[]::new);
      Arrays.sort(usable, (a, b) -> Integer.compare(a.getMassNumber(), b.getMassNumber()));
      return usable;
    });
  }

  private static IIsotope isotopeOf(int atomicNumber, int massNumber) {
    for (IIsotope isotope : isotopesOf(atomicNumber)) {
      if (isotope.getMassNumber().intValue() == massNumber) {
        return isotope;
      }
    }
    return null;
  }

  /**
   * The element names, read out of <code>ElementData</code> rather than out of CDK.
   *
   * <p>
   * The two tables do not spell every element the same way - CDK has <code>Aluminium</code> and
   * <code>Caesium</code> where Symja has <code>Aluminum</code> and <code>Cesium</code> - and an
   * isotope entity has to line up with the element entity it belongs to, so the name in
   * <code>Entity(Isotope, Aluminum27)</code> is the one <code>Entity(Element, Aluminum)</code>
   * carries. Asking for <code>"Name"</code> cannot come back here: it is a plain column of the
   * element table, never one of the computed properties that reach <code>IsotopeData</code>.
   */
  private static String[] elementNames(EvalEngine engine) {
    String[] local = elementNames;
    if (local == null) {
      synchronized (IsotopeDataFunctions.class) {
        local = elementNames;
        if (local == null) {
          local = new String[119];
          Map<String, Integer> numbers = new HashMap<String, Integer>();
          for (int z = 1; z <= 118; z++) {
            local[z] = readElement(engine, z, "Name", numbers);
            readElement(engine, z, "AtomicSymbol", numbers);
          }
          elementNumbers = numbers;
          elementNames = local;
        }
      }
    }
    return local;
  }

  /** One string-valued element column, also recorded as a way of naming that element. */
  private static String readElement(EvalEngine engine, int atomicNumber, String column,
      Map<String, Integer> numbers) {
    IExpr value = engine
        .evaluate(F.binaryAST2(S.ElementData, F.ZZ(atomicNumber), F.stringx(column)));
    if (!value.isString()) {
      return null;
    }
    String name = value.toString();
    numbers.put(name.toLowerCase(java.util.Locale.US), Integer.valueOf(atomicNumber));
    return name;
  }

  private static String elementName(EvalEngine engine, int atomicNumber) {
    String[] names = elementNames(engine);
    String name = atomicNumber >= 1 && atomicNumber <= 118 ? names[atomicNumber] : null;
    return name != null ? name
        : org.openscience.cdk.config.Elements.ofNumber(atomicNumber).name();
  }

  /** The atomic number an expression names, as a number, a name or an atomic symbol. */
  private static int elementNumber(IExpr expr, EvalEngine engine) {
    if (expr.isInteger()) {
      int z = expr.toIntDefault();
      return z >= 1 && z <= 118 ? z : -1;
    }
    if (!expr.isString()) {
      return -1;
    }
    elementNames(engine);
    Integer z = elementNumbers.get(expr.toString().toLowerCase(java.util.Locale.US));
    return z == null ? -1 : z.intValue();
  }

  /** <code>Carbon12</code> - the name an isotope entity carries. */
  private static String standardName(EvalEngine engine, int atomicNumber, int massNumber) {
    return elementName(engine, atomicNumber) + massNumber;
  }

  private static IExpr entityOf(EvalEngine engine, IIsotope isotope) {
    return Entities.entity(ISOTOPE, F.stringx(standardName(engine,
        isotope.getAtomicNumber().intValue(), isotope.getMassNumber().intValue())));
  }

  /**
   * The nuclide an expression names, or <code>null</code>.
   *
   * <p>
   * Accepted: <code>Entity("Isotope", name)</code>, a name with the mass number written onto it
   * (<code>"Carbon12"</code>, <code>"C12"</code>), and the element and mass number as a pair
   * (<code>{"Carbon", 12}</code>, <code>{6, 12}</code>). A name that parses but is not in the table
   * gives <code>null</code> just as an unparseable one does; the caller decides what that means.
   */
  private static IIsotope nuclideOf(IExpr expr, EvalEngine engine) {
    IExpr spec = Entities.nameOf(expr, ISOTOPE);
    if (spec.isList() && spec.argSize() == 2) {
      IAST pair = (IAST) spec;
      int z = elementNumber(pair.arg1(), engine);
      int a = pair.arg2().toIntDefault();
      return z < 1 || a < 1 ? null : isotopeOf(z, a);
    }
    if (!spec.isString()) {
      return null;
    }
    String name = spec.toString();
    int split = name.length();
    while (split > 0 && Character.isDigit(name.charAt(split - 1))) {
      split--;
    }
    if (split == 0 || split == name.length()) {
      // all digits, or no digits at all: an element name, not a nuclide
      return null;
    }
    int z = elementNumber(F.stringx(name.substring(0, split)), engine);
    if (z < 1) {
      return null;
    }
    try {
      return isotopeOf(z, Integer.parseInt(name.substring(split)));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static IAST entitiesOf(EvalEngine engine, IIsotope[] isotopes) {
    IASTAppendable result = F.ListAlloc(isotopes.length);
    for (IIsotope isotope : isotopes) {
      result.append(entityOf(engine, isotope));
    }
    return result;
  }

  private static IAST allEntities(EvalEngine engine) {
    IAST local = allEntities;
    if (local == null) {
      synchronized (IsotopeDataFunctions.class) {
        local = allEntities;
        if (local == null) {
          IASTAppendable result = F.ListAlloc(3200);
          for (int z = 1; z <= 118; z++) {
            for (IIsotope isotope : isotopesOf(z)) {
              result.append(entityOf(engine, isotope));
            }
          }
          local = result;
          allEntities = local;
        }
      }
    }
    return local;
  }

  /**
   * Total nuclear binding energy in MeV, from the mass excess:
   * <code>BE = (Z*m(H1) + N*m(n) - M(A,Z)) * c^2</code>.
   *
   * <p>
   * The mass of hydrogen-1 is read out of the same table as <code>M(A,Z)</code> rather than written
   * down here, so that both sides of the subtraction always come from one measurement campaign. The
   * electron binding energies this glosses over - the <code>Z</code> free hydrogen atoms against the
   * neutral isotope - are of order electronvolts, which does not show at this scale.
   */
  private static IExpr bindingEnergy(IIsotope isotope) {
    IIsotope hydrogen1 = isotopeOf(1, 1);
    if (hydrogen1 == null) {
      return F.Missing(S.NotAvailable);
    }
    int z = isotope.getAtomicNumber().intValue();
    int n = isotope.getMassNumber().intValue() - z;
    double massExcess = z * hydrogen1.getExactMass().doubleValue()
        + n * NEUTRON_MASS_IN_DALTONS - isotope.getExactMass().doubleValue();
    return F.binaryAST2(S.Quantity, F.num(massExcess * DALTON_IN_MEGAELECTRONVOLTS),
        F.stringx("Megaelectronvolts"));
  }

  private static IExpr nuclideProperty(IIsotope isotope, String property, EvalEngine engine) {
    int z = isotope.getAtomicNumber().intValue();
    int a = isotope.getMassNumber().intValue();
    switch (property) {
      case "AtomicMass":
        return F.binaryAST2(S.Quantity, F.num(isotope.getExactMass().doubleValue()),
            F.stringx("Daltons"));
      case "AtomicNumber":
        return F.ZZ(z);
      case "BindingEnergy":
        return bindingEnergy(isotope);
      case "IsotopeAbundance": {
        Double abundance = isotope.getNaturalAbundance();
        // CDK tabulates percent, the Wolfram Language answers a fraction of one
        return abundance == null || abundance.doubleValue() <= 0.0 ? F.Missing(S.NotAvailable)
            : F.num(abundance.doubleValue() / 100.0);
      }
      case "MassNumber":
        return F.ZZ(a);
      case "NeutronNumber":
        return F.ZZ(a - z);
      case "StandardName":
        return F.stringx(standardName(engine, z, a));
      default:
        // no answer, so that EntityValue can report which half of the question was unknown
        return F.NIL;
    }
  }

  /** The properties of the whole element, which the most abundant isotope alone cannot answer. */
  private static IExpr elementProperty(int atomicNumber, String property, EvalEngine engine) {
    IIsotope[] isotopes = isotopesOf(atomicNumber);
    if ("MassNumbers".equals(property)) {
      IASTAppendable result = F.ListAlloc(isotopes.length);
      for (IIsotope isotope : isotopes) {
        result.append(F.ZZ(isotope.getMassNumber().intValue()));
      }
      return result;
    }
    if ("Abundances".equals(property)) {
      // every isotope that occurs naturally, as massNumber -> percent
      IASTAppendable result = F.ListAlloc(isotopes.length);
      for (IIsotope isotope : isotopes) {
        Double abundance = isotope.getNaturalAbundance();
        if (abundance != null && abundance.doubleValue() > 0.0) {
          result.append(
              F.Rule(F.ZZ(isotope.getMassNumber().intValue()), F.num(abundance.doubleValue())));
        }
      }
      return result;
    }
    if ("StableIsotopes".equals(property)) {
      // "stable" as the table means it: an isotope that occurs in nature. That takes in the
      // long lived primordial ones - potassium-40, thorium-232 - and leaves technetium and
      // polonium with none at all, which is how a chemistry reference describes them.
      IASTAppendable result = F.ListAlloc(isotopes.length);
      for (IIsotope isotope : isotopes) {
        Double abundance = isotope.getNaturalAbundance();
        if (abundance != null && abundance.doubleValue() > 0.0) {
          result.append(entityOf(engine, isotope));
        }
      }
      return result;
    }
    return F.NIL;
  }

  private static class IsotopeData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (factory() == null) {
        return F.Missing(S.NotAvailable);
      }
      if (ast.isAST0()) {
        return allEntities(engine);
      }
      IExpr arg1 = Entities.nameOf(ast.arg1(), ISOTOPE);
      if (arg1 == S.All) {
        if (ast.isAST1()) {
          return allEntities(engine);
        }
        // loading the table is what "Preload" asks for, and it has now happened
        return ast.arg2().isString() && ast.arg2().toString().equals("Preload") ? S.Null : F.NIL;
      }
      if (ast.isAST1() && arg1.isString() && arg1.toString().equals("Properties")) {
        return F.mapRange(0, ISOTOPE_PROPERTIES.length,
            i -> Entities.property(ISOTOPE, ISOTOPE_PROPERTIES[i]));
      }

      IIsotope nuclide = nuclideOf(arg1, engine);
      if (nuclide != null) {
        if (ast.isAST1()) {
          return entityOf(engine, nuclide);
        }
        IExpr property = Entities.propertyOf(ast.arg2(), ISOTOPE);
        return property.isString() ? nuclideProperty(nuclide, property.toString(), engine) : F.NIL;
      }

      int atomicNumber = elementNumber(arg1, engine);
      if (atomicNumber < 1) {
        if (!arg1.isString()) {
          // nothing that names an element or a nuclide at all
          return F.NIL;
        }
        // a name that is neither an element nor a nuclide this table has. A nuclide which only
        // looks like one - "Carbon99" - is no answer at all, so that EntityValue can tell an
        // unknown thing from an unknown property; anything else is the missing data it has
        // always been.
        return isotopeLike(arg1, engine) ? F.NIL : F.Missing(S.NotAvailable);
      }
      IIsotope[] isotopes = isotopesOf(atomicNumber);
      if (isotopes.length == 0) {
        return F.Missing(S.NotAvailable);
      }
      if (ast.isAST1()) {
        return entitiesOf(engine, isotopes);
      }
      IExpr propertyExpr = Entities.propertyOf(ast.arg2(), ISOTOPE);
      if (!propertyExpr.isString()) {
        return F.NIL;
      }
      String property = propertyExpr.toString();
      IExpr whole = elementProperty(atomicNumber, property, engine);
      if (whole.isPresent()) {
        return whole;
      }
      // every other property speaks for the most abundant isotope of the element
      IIsotope major = majorIsotope(atomicNumber);
      if (major == null) {
        return F.Missing(S.NotAvailable);
      }
      if ("Abundance".equals(property) || "IsotopeAbundance".equals(property)) {
        // percent, as the element forms have always reported it
        Double abundance = major.getNaturalAbundance();
        return abundance == null ? F.Missing(S.NotAvailable) : F.num(abundance.doubleValue());
      }
      if ("IsotopeMass".equals(property)) {
        return F.num(major.getExactMass().doubleValue());
      }
      if ("AtomicMass".equals(property)) {
        return F.num(major.getExactMass().doubleValue());
      }
      IExpr value = nuclideProperty(major, property, engine);
      return value.isPresent() ? value : F.Missing(S.NotAvailable);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  /** Whether a name reads as a nuclide of a known element, whether or not the table has it. */
  private static boolean isotopeLike(IExpr expr, EvalEngine engine) {
    if (!expr.isString()) {
      return expr.isList() && expr.argSize() == 2
          && elementNumber(((IAST) expr).arg1(), engine) >= 1;
    }
    String name = expr.toString();
    int split = name.length();
    while (split > 0 && Character.isDigit(name.charAt(split - 1))) {
      split--;
    }
    return split > 0 && split < name.length()
        && elementNumber(F.stringx(name.substring(0, split)), engine) >= 1;
  }

  /** The most abundant naturally occurring isotope, or the lightest one when none occurs. */
  private static IIsotope majorIsotope(int atomicNumber) {
    IIsotope best = null;
    double bestAbundance = 0.0;
    for (IIsotope isotope : isotopesOf(atomicNumber)) {
      Double abundance = isotope.getNaturalAbundance();
      if (abundance != null && abundance.doubleValue() > bestAbundance) {
        best = isotope;
        bestAbundance = abundance.doubleValue();
      }
    }
    return best;
  }

  public static void initialize() {
    Initializer.init();
  }

  private IsotopeDataFunctions() {}
}
