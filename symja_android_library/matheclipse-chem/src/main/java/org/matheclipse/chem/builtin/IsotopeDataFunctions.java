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

  /**
   * The stable isotopes of each element, by mass number, indexed by atomic number.
   *
   * <p>
   * Stability is not in CDK's table, which records natural abundance and nothing about decay, and
   * the two are not the same thing: uranium occurs in nature and has no stable isotope. So this is
   * copied from the reference implementation - <code>ElementData[z, "StableIsotopes"]</code> for
   * every element, measured in Mathematica on 2026-09-19 - rather than worked out, and it keeps
   * that answer's order, which is the order of the entity names. That is why ruthenium lists 100,
   * 101, 102 and 104 ahead of 96, 98 and 99.
   *
   * <p>
   * Its choices are not the textbook's in a handful of places, and are kept as they are:
   * thorium-232 counts as stable while bismuth-209 and every uranium isotope do not, samarium-149
   * is in and samarium-147 and 148 are out, and osmium-184 and 187 are in.
   */
  private static final int[][] STABLE_ISOTOPES = {
      {}, {1, 2}, {3, 4}, {6, 7}, {9}, {10, 11}, {12, 13}, {14, 15}, {16, 17, 18}, {19},
      {20, 21, 22}, {23}, {24, 25, 26}, {27}, {28, 29, 30}, {31}, {32, 33, 34, 36}, {35, 37},
      {36, 38, 40}, {39, 41}, {40, 42, 43, 44, 46}, {45}, {46, 47, 48, 49, 50}, {51},
      {50, 52, 53, 54}, {55}, {54, 56, 57, 58}, {59}, {58, 60, 61, 62, 64}, {63, 65},
      {64, 66, 67, 68, 70}, {69, 71}, {70, 72, 73, 74}, {75}, {74, 76, 77, 78, 80}, {79, 81},
      {78, 80, 82, 83, 84, 86}, {85}, {84, 86, 87, 88}, {89}, {90, 91, 92, 94}, {93},
      {92, 94, 95, 96, 97, 98}, {}, {100, 101, 102, 104, 96, 98, 99}, {103},
      {102, 104, 105, 106, 108, 110}, {107, 109}, {106, 108, 110, 111, 112, 114}, {113},
      {112, 114, 115, 116, 117, 118, 119, 120, 122, 124}, {121, 123}, {120, 122, 124, 125, 126},
      {127}, {124, 126, 128, 129, 130, 131, 132, 134, 136}, {133},
      {130, 132, 134, 135, 136, 137, 138}, {139}, {136, 138, 140, 142}, {141},
      {142, 143, 145, 146, 148}, {}, {144, 149, 150, 152, 154}, {151, 153},
      {154, 155, 156, 157, 158, 160}, {159}, {156, 158, 160, 161, 162, 163, 164}, {165},
      {162, 164, 166, 167, 168, 170}, {169}, {168, 170, 171, 172, 173, 174, 176}, {175},
      {176, 177, 178, 179, 180}, {181}, {180, 182, 183, 184, 186}, {185},
      {184, 187, 188, 189, 190, 192}, {191, 193}, {192, 194, 195, 196, 198}, {197},
      {196, 198, 199, 200, 201, 202, 204}, {203, 205}, {204, 206, 207, 208}, {}, {}, {}, {}, {},
      {}, {}, {232}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
      {}, {}, {}, {}, {}, {}, {}, {}, {}
  };

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
   * Accepted: <code>Entity("Isotope", name)</code> and a name with the mass number written onto it
   * (<code>"Carbon12"</code>, <code>"C12"</code>). An element and a mass number as a pair is not a
   * specifier, which is the reference implementation's rule. A name that parses but is not in the
   * table gives <code>null</code> just as an unparseable one does; the caller decides what that
   * means.
   */
  private static IIsotope nuclideOf(IExpr expr, EvalEngine engine) {
    IExpr spec = Entities.nameOf(expr, ISOTOPE);
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
   * Nuclear binding energy <b>per nucleon</b>, in MeV, from the mass excess:
   * <code>BE = (Z*m(H1) + N*m(n) - M(A,Z)) * c^2 / A</code>.
   *
   * <p>
   * Per nucleon, not in total, because that is what the reference implementation reports and it is
   * the quantity the curve of binding energy is drawn from - carbon-12 answers 7.68 MeV rather
   * than its 92.16 MeV total, and iron-56 the 8.79 MeV at the peak.
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
    int a = isotope.getMassNumber().intValue();
    double massExcess = z * hydrogen1.getExactMass().doubleValue()
        + (a - z) * NEUTRON_MASS_IN_DALTONS - isotope.getExactMass().doubleValue();
    return F.binaryAST2(S.Quantity, F.num(massExcess * DALTON_IN_MEGAELECTRONVOLTS / a),
        F.stringx("Megaelectronvolts"));
  }

  /**
   * The natural abundance, as a percentage.
   *
   * <p>
   * A nuclide that does not occur in nature answers zero percent rather than missing data, which
   * is what the reference implementation gives for carbon-14.
   */
  private static IExpr abundanceOf(IIsotope isotope) {
    Double abundance = isotope.getNaturalAbundance();
    double percent = abundance == null || abundance.doubleValue() <= 0.0 ? 0.0
        : abundance.doubleValue();
    return F.binaryAST2(S.Quantity, percent == 0.0 ? F.C0 : F.num(percent),
        F.stringx("Percent"));
  }

  private static IExpr nuclideProperty(IIsotope isotope, String property, EvalEngine engine) {
    int z = isotope.getAtomicNumber().intValue();
    int a = isotope.getMassNumber().intValue();
    switch (property) {
      case "AtomicMass":
        return F.binaryAST2(S.Quantity, F.num(isotope.getExactMass().doubleValue()),
            F.stringx("AtomicMassUnit"));
      case "AtomicNumber":
        return F.ZZ(z);
      case "BindingEnergy":
        return bindingEnergy(isotope);
      case "IsotopeAbundance":
        // a percentage, and zero rather than missing for a nuclide that does not occur in nature
        return abundanceOf(isotope);
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
      // every isotope that occurs naturally, as isotope -> percent. An association keyed by the
      // entity, which is the shape ElementData("...", "IsotopeAbundances") reports.
      IASTAppendable rules = F.ListAlloc(isotopes.length);
      for (IIsotope isotope : isotopes) {
        Double abundance = isotope.getNaturalAbundance();
        if (abundance != null && abundance.doubleValue() > 0.0) {
          rules.append(F.Rule(entityOf(engine, isotope), abundanceOf(isotope)));
        }
      }
      return F.assoc(rules);
    }
    if ("StableIsotopes".equals(property)) {
      int[] massNumbers = atomicNumber < STABLE_ISOTOPES.length ? STABLE_ISOTOPES[atomicNumber]
          : new int[0];
      IASTAppendable result = F.ListAlloc(massNumbers.length);
      for (int massNumber : massNumbers) {
        result.append(Entities.entity(ISOTOPE,
            F.stringx(standardName(engine, atomicNumber, massNumber))));
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
        // Nothing this table knows - an unknown element, or a nuclide which only looks like one
        // such as "Carbon99". No answer at all rather than missing data, both because that is
        // what the reference implementation does and because EntityValue needs it: it asks after
        // the name alone to tell an unknown thing from an unknown property.
        return F.NIL;
      }
      if (ast.isAST2()) {
        IExpr propertyExpr = Entities.propertyOf(ast.arg2(), ISOTOPE);
        if (!propertyExpr.isString()) {
          return F.NIL;
        }
        // a question about the whole element is answered before asking whether CDK has any of
        // its isotopes: the stable ones come from a table of their own, and oganesson has none
        // there, which is an empty list and not missing data
        IExpr whole = elementProperty(atomicNumber, propertyExpr.toString(), engine);
        if (whole.isPresent()) {
          return whole;
        }
      }
      IIsotope[] isotopes = isotopesOf(atomicNumber);
      if (isotopes.length == 0) {
        return F.Missing(S.NotAvailable);
      }
      if (ast.isAST1()) {
        return entitiesOf(engine, isotopes);
      }
      String property = Entities.propertyOf(ast.arg2(), ISOTOPE).toString();
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
