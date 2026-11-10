package org.matheclipse.chem.builtin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
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
 * <code>IsotopeData</code> over the NUBASE2020 nuclide chart - every known ground state of every
 * element, with its half-life and decay modes - completed by CDK's bundled BODR table for the
 * measured masses and natural abundances.
 *
 * <p>
 * A single nuclide is named by an entity, <code>Entity("Isotope", "Carbon12")</code>, so that it
 * can be handed to <code>EntityValue</code> like any other thing this system knows about. The
 * element forms answer for the whole element, and the two are told apart by the shape of the
 * argument rather than by the spelling of the property:
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
 * <code>"IsotopeAbundance"</code> is a fraction of one, <code>0.9893</code>.
 *
 * <p>
 * A nuclide that could exist but is not in the table - <code>"Carbon99"</code> - is no answer at
 * all rather than <code>Missing</code>, and so is an unknown property of a nuclide. That is what
 * lets <code>EntityValue</code> ask again and say which half of the question it did not know. The
 * element forms keep answering <code>Missing(NotAvailable)</code>, as they always have.
 *
 * <p>
 * The chart is the resource <code>nuclides.tsv.gz</code>, written by
 * <code>tools/gen_isotope_data.py</code>; see the <code>NOTICE</code> beside it for the source.
 */
public class IsotopeDataFunctions {

  private static final String ISOTOPE = "Isotope";

  private static final String NUCLIDE_TABLE = "/org/matheclipse/chem/data/nuclides.tsv.gz";

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

  /** 1 u expressed as an energy, in keV/c² - the unit the chart gives mass excesses in. */
  private static final double DALTON_IN_KILOELECTRONVOLTS = 931494.10242;

  /**
   * The properties one nuclide answers for, in the order <code>IsotopeData("Properties")</code>
   * reports them. The switch below reads from this array, so the two cannot drift apart.
   */
  private static final String[] ISOTOPE_PROPERTIES = {"AtomicMass", "AtomicNumber", "BindingEnergy",
      "BranchingRatios", "DaughterNuclides", "DecayModes", "HalfLife", "IsotopeAbundance",
      "Lifetime", "MassNumber", "NeutronNumber", "StandardName", "Symbol"};

  /**
   * The stable isotopes of each element, by mass number, indexed by atomic number.
   *
   * <p>
   * Stability is not in CDK's table, which records natural abundance and nothing about decay, and
   * the two are not the same thing: uranium occurs in nature and has no stable isotope. So this is
   * copied from the reference implementation - <code>ElementData[z, "StableIsotopes"]</code> for
   * every element, measured on 2026-09-19 - rather than worked out, and it keeps that answer's
   * order, which is the order of the entity names. That is why ruthenium lists 100, 101, 102 and
   * 104 ahead of 96, 98 and 99.
   *
   * <p>
   * Its choices are not the textbook's in a handful of places, and are kept as they are:
   * thorium-232 counts as stable while bismuth-209 and every uranium isotope do not, samarium-149
   * is in and samarium-147 and 148 are out, and osmium-184 and 187 are in.
   */
  private static final int[][] STABLE_ISOTOPES = {{}, {1, 2}, {3, 4}, {6, 7}, {9}, {10, 11},
      {12, 13}, {14, 15}, {16, 17, 18}, {19}, {20, 21, 22}, {23}, {24, 25, 26}, {27}, {28, 29, 30},
      {31}, {32, 33, 34, 36}, {35, 37}, {36, 38, 40}, {39, 41}, {40, 42, 43, 44, 46}, {45},
      {46, 47, 48, 49, 50}, {51}, {50, 52, 53, 54}, {55}, {54, 56, 57, 58}, {59},
      {58, 60, 61, 62, 64}, {63, 65}, {64, 66, 67, 68, 70}, {69, 71}, {70, 72, 73, 74}, {75},
      {74, 76, 77, 78, 80}, {79, 81}, {78, 80, 82, 83, 84, 86}, {85}, {84, 86, 87, 88}, {89},
      {90, 91, 92, 94}, {93}, {92, 94, 95, 96, 97, 98}, {}, {100, 101, 102, 104, 96, 98, 99}, {103},
      {102, 104, 105, 106, 108, 110}, {107, 109}, {106, 108, 110, 111, 112, 114}, {113},
      {112, 114, 115, 116, 117, 118, 119, 120, 122, 124}, {121, 123}, {120, 122, 124, 125, 126},
      {127}, {124, 126, 128, 129, 130, 131, 132, 134, 136}, {133},
      {130, 132, 134, 135, 136, 137, 138}, {139}, {136, 138, 140, 142}, {141},
      {142, 143, 145, 146, 148}, {}, {144, 149, 150, 152, 154}, {151, 153},
      {154, 155, 156, 157, 158, 160}, {159}, {156, 158, 160, 161, 162, 163, 164}, {165},
      {162, 164, 166, 167, 168, 170}, {169}, {168, 170, 171, 172, 173, 174, 176}, {175},
      {176, 177, 178, 179, 180}, {181}, {180, 182, 183, 184, 186}, {185},
      {184, 187, 188, 189, 190, 192}, {191, 193}, {192, 194, 195, 196, 198}, {197},
      {196, 198, 199, 200, 201, 202, 204}, {203, 205}, {204, 206, 207, 208}, {}, {}, {}, {}, {}, {},
      {}, {232}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
      {}, {}, {}, {}, {}, {}, {}};

  /** The chart's half-life units, by the abbreviation it writes them with. */
  private static final Map<String, String> TIME_UNITS = new HashMap<String, String>();

  static {
    String[] units = {"ys", "Yoctoseconds", "zs", "Zeptoseconds", "as", "Attoseconds", "fs",
        "Femtoseconds", "ps", "Picoseconds", "ns", "Nanoseconds", "us", "Microseconds", "ms",
        "Milliseconds", "s", "Seconds", "m", "Minutes", "h", "Hours", "d", "Days", "y", "Years",
        "ky", "Kiloyears", "My", "Megayears", "Gy", "Gigayears", "Ty", "Terayears", "Py",
        "Petayears", "Ey", "Exayears", "Zy", "Zettayears", "Yy", "Yottayears"};
    for (int i = 0; i < units.length; i += 2) {
      TIME_UNITS.put(units[i], units[i + 1]);
    }
  }

  /**
   * A way a nuclide decays: its name, and the change it makes to the atomic and the mass number.
   * The change of a fission is not one number, so it has none.
   */
  private static final class DecayMode {
    final String name;
    final int deltaZ;
    final int deltaA;
    final boolean variable;

    DecayMode(String name, int deltaZ, int deltaA) {
      this.name = name;
      this.deltaZ = deltaZ;
      this.deltaA = deltaA;
      this.variable = false;
    }

    DecayMode(String name) {
      this.name = name;
      this.deltaZ = 0;
      this.deltaA = 0;
      this.variable = true;
    }
  }

  /** The chart's decay codes; the emission of a heavier cluster is worked out from its name. */
  private static final Map<String, DecayMode> DECAY_MODES = new HashMap<String, DecayMode>();

  static {
    DECAY_MODES.put("A", new DecayMode("alpha emission", -2, -4));
    DECAY_MODES.put("B-", new DecayMode("electron emission", 1, 0));
    DECAY_MODES.put("B+", new DecayMode("positron emission", -1, 0));
    DECAY_MODES.put("e+", new DecayMode("positron emission", -1, 0));
    DECAY_MODES.put("EC", new DecayMode("electron capture", -1, 0));
    DECAY_MODES.put("EC+B+", new DecayMode("electron capture", -1, 0));
    DECAY_MODES.put("2B-", new DecayMode("double beta decay", 2, 0));
    DECAY_MODES.put("2B+", new DecayMode("double beta decay", -2, 0));
    DECAY_MODES.put("2EC", new DecayMode("double electron capture", -2, 0));
    DECAY_MODES.put("n", new DecayMode("neutron emission", 0, -1));
    DECAY_MODES.put("2n", new DecayMode("two-neutron emission", 0, -2));
    DECAY_MODES.put("3n", new DecayMode("three-neutron emission", 0, -3));
    DECAY_MODES.put("p", new DecayMode("proton emission", -1, -1));
    DECAY_MODES.put("2p", new DecayMode("two-proton emission", -2, -2));
    DECAY_MODES.put("3p", new DecayMode("three-proton emission", -3, -3));
    DECAY_MODES.put("B-n", new DecayMode("electron + delayed neutron", 1, -1));
    DECAY_MODES.put("B-2n", new DecayMode("electron + delayed 2 neutrons", 1, -2));
    DECAY_MODES.put("B-3n", new DecayMode("electron + delayed 3 neutrons", 1, -3));
    DECAY_MODES.put("B-4n", new DecayMode("electron + delayed 4 neutrons", 1, -4));
    DECAY_MODES.put("B-p", new DecayMode("electron + delayed proton", 0, -1));
    DECAY_MODES.put("B-d", new DecayMode("electron + delayed deuteron", 0, -2));
    DECAY_MODES.put("B-t", new DecayMode("electron + delayed triton", 0, -3));
    DECAY_MODES.put("B-A", new DecayMode("electron + delayed \u03b1", -1, -4));
    DECAY_MODES.put("B-nA", new DecayMode("electron + delayed neutron, \u03b1", -1, -5));
    DECAY_MODES.put("B-SF", new DecayMode("electron + delayed fission"));
    DECAY_MODES.put("B+p", new DecayMode("positron + delayed proton", -2, -1));
    DECAY_MODES.put("B+2p", new DecayMode("positron + delayed 2 protons", -3, -2));
    DECAY_MODES.put("B+3p", new DecayMode("positron + delayed 3 protons", -4, -3));
    DECAY_MODES.put("B+A", new DecayMode("positron + delayed \u03b1", -3, -4));
    DECAY_MODES.put("B+pA", new DecayMode("positron + delayed proton, \u03b1", -4, -5));
    DECAY_MODES.put("B+SF", new DecayMode("positron + delayed fission"));
    DECAY_MODES.put("SF", new DecayMode("spontaneous fission"));
  }

  /** One decay branch of a nuclide: how it decays, and how often. */
  private static final class Branch {
    final String code;
    final DecayMode mode;
    /** The intensity in percent, or <code>null</code> when the chart has no number. */
    final BigDecimal percent;
    /** The significant digits the intensity is known to. */
    final int digits;

    Branch(String code, DecayMode mode, BigDecimal percent, int digits) {
      this.code = code;
      this.mode = mode;
      this.percent = percent;
      this.digits = digits;
    }

    /** Whether this is a particle emission that follows the beta decay {@code parent}. */
    boolean isDelayedAfter(String parent) {
      return code.length() > parent.length() && code.startsWith(parent);
    }
  }

  /** One ground state of the chart. */
  private static final class Nuclide {
    final int z;
    final int a;
    /** Atomic mass in daltons, <code>NaN</code> when it is not known. */
    final double mass;
    /** Natural abundance in percent, or <code>null</code>. */
    final Double abundance;
    /** The half-life as the chart writes it: a number, "stbl", "p-unst" or empty. */
    final String halfLife;
    final String unit;
    /** The uncertainty of the half-life in the same unit, or empty. */
    final String uncertainty;
    final List<Branch> branches;

    Nuclide(int z, int a, double mass, Double abundance, String halfLife, String unit,
        String uncertainty, List<Branch> branches) {
      this.z = z;
      this.a = a;
      this.mass = mass;
      this.abundance = abundance;
      this.halfLife = halfLife;
      this.unit = unit;
      this.uncertainty = uncertainty;
      this.branches = branches;
    }

    boolean isStable() {
      return "stbl".equals(halfLife);
    }
  }

  private static volatile IsotopeFactory factory;

  /**
   * Each element's nuclides, sorted by mass number, indexed by atomic number; index 0 is empty.
   * Built once from the chart and CDK's table together.
   */
  private static volatile Nuclide[][] chart;

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

  private static Nuclide[][] chart() {
    Nuclide[][] local = chart;
    if (local == null) {
      synchronized (IsotopeDataFunctions.class) {
        local = chart;
        if (local == null) {
          local = buildChart();
          chart = local;
        }
      }
    }
    return local;
  }

  /**
   * The chart's ground states, each with CDK's mass and abundance where CDK has the nuclide - those
   * are what the masses have always been - and with the mass worked out of the chart's mass excess
   * where it does not. A nuclide only CDK knows is kept too.
   */
  private static Nuclide[][] buildChart() {
    List<Map<Integer, Nuclide>> byElement = new ArrayList<Map<Integer, Nuclide>>(119);
    for (int z = 0; z <= 118; z++) {
      byElement.add(new TreeMap<Integer, Nuclide>());
    }
    for (String[] row : readTable()) {
      int z = Integer.parseInt(row[0]);
      int a = Integer.parseInt(row[1]);
      if (z < 1 || z > 118) {
        // the free neutron is in the chart, but it is no isotope of an element
        continue;
      }
      IIsotope known = cdkIsotope(z, a);
      double mass;
      Double abundance = null;
      if (known != null) {
        mass = known.getExactMass().doubleValue();
        abundance = known.getNaturalAbundance();
      } else {
        String excess = row[2].replace("#", "").trim();
        // the diproton has no measured mass
        mass = excess.isEmpty() ? Double.NaN
            : a + Double.parseDouble(excess) / DALTON_IN_KILOELECTRONVOLTS;
      }
      byElement.get(z).put(Integer.valueOf(a), new Nuclide(z, a, mass, abundance, row[3], row[4],
          row[5], branchesOf(row.length > 6 ? row[6] : "")));
    }
    for (int z = 1; z <= 118; z++) {
      for (IIsotope isotope : cdkIsotopes(z)) {
        Integer a = isotope.getMassNumber();
        if (!byElement.get(z).containsKey(a)) {
          byElement.get(z).put(a, new Nuclide(z, a.intValue(),
              isotope.getExactMass().doubleValue(), isotope.getNaturalAbundance(), "", "", "",
              Collections.<Branch>emptyList()));
        }
      }
    }
    Nuclide[][] result = new Nuclide[119][];
    for (int z = 0; z <= 118; z++) {
      result[z] = byElement.get(z).values().toArray(new Nuclide[0]);
    }
    return result;
  }

  /** The rows of the chart resource, the header left out; none when it cannot be read. */
  private static List<String[]> readTable() {
    List<String[]> rows = new ArrayList<String[]>(3600);
    try (InputStream raw = IsotopeDataFunctions.class.getResourceAsStream(NUCLIDE_TABLE)) {
      if (raw == null) {
        return rows;
      }
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(new GZIPInputStream(raw), StandardCharsets.UTF_8));
      String line = reader.readLine();
      while ((line = reader.readLine()) != null) {
        if (!line.isEmpty()) {
          rows.add(line.split("\t", -1));
        }
      }
    } catch (IOException e) {
      rows.clear();
    }
    return rows;
  }

  /**
   * The decay field of the chart - <code>"A=100;SF=5.44e-5 7;2B-=2.2e-10 3"</code> - as exclusive
   * branches, most frequent first. A code this table does not know is left out.
   *
   * <p>
   * The chart counts a beta-delayed emission inside the beta decay it follows: carbon-9 has
   * <code>B+=100;B+p=7.5;B+A=38.4</code>. Each branch here is one way the nucleus ends up, so the
   * plain positron emission keeps what the delayed emissions leave of it, 54.1%. Branches of equal
   * intensity list the one carrying away more nucleons first; those without a measured intensity
   * come last, in the chart's order.
   */
  private static List<Branch> branchesOf(String field) {
    if (field.isEmpty()) {
      return Collections.emptyList();
    }
    List<Branch> result = new ArrayList<Branch>(3);
    for (String entry : field.split(";")) {
      int end = 0;
      while (end < entry.length() && "=<>~ ?".indexOf(entry.charAt(end)) < 0) {
        end++;
      }
      String code = entry.substring(0, end);
      DecayMode mode = decayMode(code);
      if (mode == null) {
        continue;
      }
      String rest = entry.substring(end).trim();
      BigDecimal percent = null;
      int digits = 0;
      if (!rest.isEmpty() && "=<>~".indexOf(rest.charAt(0)) >= 0) {
        String value = rest.substring(1).trim();
        int space = value.indexOf(' ');
        if (space > 0) {
          // the uncertainty in the last digits
          value = value.substring(0, space);
        }
        try {
          percent = new BigDecimal(value);
          digits = significantDigits(value);
        } catch (NumberFormatException e) {
          // "A=?": the branch is seen, its intensity not measured
          percent = null;
        }
      }
      result.add(new Branch(code, mode, percent, digits));
    }
    exclusive(result, "B-");
    exclusive(result, "B+");
    List<Branch> sorted = new ArrayList<Branch>(result);
    sorted.sort((x, y) -> {
      if (x.percent == null || y.percent == null) {
        return x.percent == null ? (y.percent == null ? 0 : 1) : -1;
      }
      int byPercent = y.percent.compareTo(x.percent);
      return byPercent != 0 ? byPercent
          : Integer.compare(Math.abs(y.mode.deltaA), Math.abs(x.mode.deltaA));
    });
    return sorted;
  }

  /**
   * Replaces the beta decay {@code parent} by what its delayed emissions leave of it, and drops it
   * when they leave nothing.
   */
  private static void exclusive(List<Branch> branches, String parent) {
    for (int i = 0; i < branches.size(); i++) {
      Branch branch = branches.get(i);
      if (!branch.code.equals(parent) || branch.percent == null) {
        continue;
      }
      BigDecimal rest = branch.percent;
      boolean delayed = false;
      for (Branch other : branches) {
        if (other.isDelayedAfter(parent) && other.percent != null) {
          rest = rest.subtract(other.percent);
          delayed = true;
        }
      }
      if (!delayed) {
        return;
      }
      if (rest.signum() <= 0) {
        branches.remove(i);
      } else {
        branches.set(i, new Branch(branch.code, branch.mode, rest, branch.digits));
      }
      return;
    }
  }

  /** The significant digits of a decimal as written: <code>100</code> has three, 7e-9 one. */
  private static int significantDigits(String decimal) {
    String mantissa = decimal.split("[eE]")[0].replace("-", "").replace("+", "").replace(".", "");
    int start = 0;
    while (start < mantissa.length() - 1 && mantissa.charAt(start) == '0') {
      start++;
    }
    return Math.max(1, mantissa.length() - start);
  }

  /** A decay code, or the emission of a cluster named by its mass number and element. */
  private static DecayMode decayMode(String code) {
    DecayMode mode = DECAY_MODES.get(code);
    if (mode != null) {
      return mode;
    }
    // "24Ne"; and "24Ne+26Ne", two clusters emitted together
    String[] clusters = code.split("\\+");
    if (clusters.length > 2) {
      return null;
    }
    int deltaZ = 0;
    int deltaA = 0;
    for (String cluster : clusters) {
      int digits = 0;
      while (digits < cluster.length() && Character.isDigit(cluster.charAt(digits))) {
        digits++;
      }
      if (digits == 0 || digits == cluster.length()) {
        return null;
      }
      try {
        int atomicNumber =
            org.openscience.cdk.config.Elements.ofString(cluster.substring(digits)).number();
        if (atomicNumber < 1) {
          return null;
        }
        deltaZ -= atomicNumber;
        deltaA -= Integer.parseInt(cluster.substring(0, digits));
      } catch (RuntimeException e) {
        return null;
      }
    }
    return new DecayMode(clusters.length == 1 ? "cluster emission" : "double cluster emission",
        deltaZ, deltaA);
  }

  /** CDK's nuclides of one element, without the ones that lack a mass number or a mass. */
  private static IIsotope[] cdkIsotopes(int atomicNumber) {
    IsotopeFactory f = factory();
    if (f == null) {
      return new IIsotope[0];
    }
    IIsotope[] isotopes;
    try {
      isotopes = f.getIsotopes(org.openscience.cdk.config.Elements.ofNumber(atomicNumber).symbol());
    } catch (Exception e) {
      return new IIsotope[0];
    }
    if (isotopes == null) {
      return new IIsotope[0];
    }
    List<IIsotope> usable = new ArrayList<IIsotope>(isotopes.length);
    for (IIsotope isotope : isotopes) {
      if (isotope.getMassNumber() != null && isotope.getExactMass() != null) {
        usable.add(isotope);
      }
    }
    return usable.toArray(new IIsotope[0]);
  }

  private static IIsotope cdkIsotope(int atomicNumber, int massNumber) {
    if (atomicNumber < 1) {
      return null;
    }
    for (IIsotope isotope : cdkIsotopes(atomicNumber)) {
      if (isotope.getMassNumber().intValue() == massNumber) {
        return isotope;
      }
    }
    return null;
  }

  /** The nuclides of one element, lightest first. */
  private static Nuclide[] isotopesOf(int atomicNumber) {
    Nuclide[][] local = chart();
    return atomicNumber >= 0 && atomicNumber < local.length ? local[atomicNumber] : new Nuclide[0];
  }

  private static Nuclide isotopeOf(int atomicNumber, int massNumber) {
    for (Nuclide nuclide : isotopesOf(atomicNumber)) {
      if (nuclide.a == massNumber) {
        return nuclide;
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
    IExpr value =
        engine.evaluate(F.binaryAST2(S.ElementData, F.ZZ(atomicNumber), F.stringx(column)));
    if (!value.isString()) {
      return null;
    }
    String name = value.toString();
    numbers.put(name.toLowerCase(Locale.US), Integer.valueOf(atomicNumber));
    return name;
  }

  private static String elementName(EvalEngine engine, int atomicNumber) {
    String[] names = elementNames(engine);
    String name = atomicNumber >= 1 && atomicNumber <= 118 ? names[atomicNumber] : null;
    return name != null ? name : org.openscience.cdk.config.Elements.ofNumber(atomicNumber).name();
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
    Integer z = elementNumbers.get(expr.toString().toLowerCase(Locale.US));
    return z == null ? -1 : z.intValue();
  }

  /** <code>Carbon12</code> - the name an isotope entity carries. */
  private static String standardName(EvalEngine engine, int atomicNumber, int massNumber) {
    return elementName(engine, atomicNumber) + massNumber;
  }

  private static IAST entityOf(EvalEngine engine, int atomicNumber, int massNumber) {
    return Entities.entity(ISOTOPE, F.stringx(standardName(engine, atomicNumber, massNumber)));
  }

  private static IAST entityOf(EvalEngine engine, Nuclide nuclide) {
    return entityOf(engine, nuclide.z, nuclide.a);
  }

  /**
   * The nuclide an expression names, or <code>null</code>.
   *
   * <p>
   * Accepted: <code>Entity("Isotope", name)</code> and a name with the mass number written onto it
   * (<code>"Carbon12"</code>, <code>"C12"</code>). An element and a mass number as a pair is not a
   * specifier, which is the reference implementation's rule. A name that parses but is not in the
   * table gives <code>null</code> just as an unparseable one does - the free neutron among them,
   * which is no isotope of any element; the caller decides what that means.
   */
  private static Nuclide nuclideOf(IExpr expr, EvalEngine engine) {
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

  private static IAST entitiesOf(EvalEngine engine, Nuclide[] nuclides) {
    IASTAppendable result = F.ListAlloc(nuclides.length);
    for (Nuclide nuclide : nuclides) {
      result.append(entityOf(engine, nuclide));
    }
    return result;
  }

  private static IAST allEntities(EvalEngine engine) {
    IAST local = allEntities;
    if (local == null) {
      synchronized (IsotopeDataFunctions.class) {
        local = allEntities;
        if (local == null) {
          IASTAppendable result = F.ListAlloc(3600);
          for (int z = 1; z <= 118; z++) {
            for (Nuclide nuclide : isotopesOf(z)) {
              result.append(entityOf(engine, nuclide));
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
   * the quantity the curve of binding energy is drawn from - carbon-12 answers 7.68 MeV rather than
   * its 92.16 MeV total, and iron-56 the 8.79 MeV at the peak.
   *
   * <p>
   * The mass of hydrogen-1 is read out of the same table as <code>M(A,Z)</code> rather than written
   * down here, so that both sides of the subtraction always come from one measurement campaign. The
   * electron binding energies this glosses over - the <code>Z</code> free hydrogen atoms against
   * the neutral isotope - are of order electronvolts, which does not show at this scale.
   */
  private static IExpr bindingEnergy(Nuclide nuclide) {
    Nuclide hydrogen1 = isotopeOf(1, 1);
    if (hydrogen1 == null || Double.isNaN(nuclide.mass)) {
      return F.Missing(S.NotAvailable);
    }
    int z = nuclide.z;
    int a = nuclide.a;
    double massExcess = z * hydrogen1.mass + (a - z) * NEUTRON_MASS_IN_DALTONS - nuclide.mass;
    return F.binaryAST2(S.Quantity, F.num(massExcess * DALTON_IN_MEGAELECTRONVOLTS / a),
        F.stringx("Megaelectronvolts"));
  }

  /**
   * The natural abundance, as a percentage.
   *
   * <p>
   * A nuclide that does not occur in nature answers zero percent rather than missing data, which is
   * what the reference implementation gives for carbon-14.
   */
  private static IExpr abundanceOf(Nuclide nuclide) {
    Double abundance = nuclide.abundance;
    double percent =
        abundance == null || abundance.doubleValue() <= 0.0 ? 0.0 : abundance.doubleValue();
    return F.binaryAST2(S.Quantity, percent == 0.0 ? F.C0 : F.num(percent), F.stringx("Percent"));
  }

  /**
   * The half-life in the unit the chart states it in, as a number whose precision is what the
   * chart's uncertainty leaves of it - <code>5.70 ky</code> known to <code>0.03</code> has the
   * precision <code>Log10(5.70/0.03)</code>. Without an uncertainty, or for a bound, the number is
   * a machine number. A stable nuclide's half-life is infinite.
   *
   * <p>
   * Two units are not used as they stand: yoctoseconds become zeptoseconds, and a half-life of
   * years is given in years below 10^5 years, in megayears below 10^8, in gigayears below 10^13 and
   * in years again above that.
   *
   * @param lifetime the mean lifetime, the half-life over <code>Log(2)</code>, instead
   */
  private static IExpr halfLife(Nuclide nuclide, boolean lifetime) {
    if (nuclide.isStable()) {
      return F.Quantity(F.CInfinity, F.stringx("Seconds"));
    }
    if ("p-unst".equals(nuclide.halfLife)) {
      // particle unstable: it falls apart before a half-life could mean anything
      return F.Missing("Unstable");
    }
    String unit = nuclide.unit;
    String value = nuclide.halfLife;
    boolean bound = false;
    while (!value.isEmpty() && "<>~".indexOf(value.charAt(0)) >= 0) {
      value = value.substring(1).trim();
      bound = true;
    }
    if (!TIME_UNITS.containsKey(unit) || value.isEmpty()) {
      return F.Missing(S.NotAvailable);
    }
    BigDecimal magnitude;
    double precision = -1.0;
    try {
      magnitude = new BigDecimal(value);
      if (!bound && !nuclide.uncertainty.isEmpty()) {
        double uncertainty = Double.parseDouble(nuclide.uncertainty);
        if (uncertainty > 0.0) {
          precision = Math.log10(magnitude.doubleValue() / uncertainty);
        }
      }
    } catch (NumberFormatException e) {
      return F.Missing(S.NotAvailable);
    }
    if ("ys".equals(unit)) {
      magnitude = magnitude.movePointLeft(3);
      unit = "zs";
    }
    int yearExponent = YEAR_EXPONENTS.indexOf(unit);
    if (yearExponent >= 0) {
      BigDecimal years = magnitude.movePointRight(3 * yearExponent);
      if (years.compareTo(BigDecimal.TEN.pow(5)) < 0) {
        magnitude = years;
        unit = "y";
      } else if (years.compareTo(BigDecimal.TEN.pow(8)) < 0) {
        magnitude = years.movePointLeft(6);
        unit = "My";
      } else if (years.compareTo(BigDecimal.TEN.pow(13)) < 0) {
        magnitude = years.movePointLeft(9);
        unit = "Gy";
      } else {
        magnitude = years;
        unit = "y";
      }
    }
    double scale = lifetime ? 1.0 / Math.log(2.0) : 1.0;
    IExpr number;
    if (precision > 0.0) {
      String digits = lifetime
          ? new BigDecimal(magnitude.doubleValue() * scale).round(java.math.MathContext.DECIMAL64)
              .toString()
          : magnitude.toString();
      number = F.num(digits, precision);
    } else {
      number = F.num(magnitude.doubleValue() * scale);
    }
    return F.Quantity(number, F.stringx(TIME_UNITS.get(unit)));
  }

  /** The year units by their power of a thousand: "y", "ky", "My", ... */
  private static final List<String> YEAR_EXPONENTS =
      java.util.Arrays.asList("y", "ky", "My", "Gy", "Ty", "Py", "Ey", "Zy", "Yy");

  /** The branches a property lists: none for a stable nuclide, which is not a missing answer. */
  private static IExpr decayProperty(Nuclide nuclide, String property, EvalEngine engine) {
    if (nuclide.isStable()) {
      return F.Missing(S.NotApplicable);
    }
    if (nuclide.branches.isEmpty()) {
      return F.Missing(S.NotAvailable);
    }
    IASTAppendable result = F.ListAlloc(nuclide.branches.size());
    for (Branch branch : nuclide.branches) {
      switch (property) {
        case "DecayModes":
          result.append(F.stringx(branch.mode.name));
          break;
        case "DaughterNuclides":
          result.append(daughter(nuclide, branch.mode, engine));
          break;
        default:
          // a percentage known to the digits the chart gives, but never fewer than two; a branch
          // that is seen but not measured has no number
          result.append(branch.percent == null ? F.Missing(S.Unknown)
              : F.Quantity(F.num(branch.percent.toString(), Math.max(2, branch.digits)),
                  F.stringx("Percent")));
          break;
      }
    }
    return result;
  }

  private static IExpr daughter(Nuclide nuclide, DecayMode mode, EvalEngine engine) {
    if (mode.variable) {
      // a fission leaves one of many pairs of fragments
      return F.Missing(S.Variable);
    }
    int z = nuclide.z + mode.deltaZ;
    int a = nuclide.a + mode.deltaA;
    if (z < 0 || z > 118 || a < 1) {
      return F.Missing(S.NotAvailable);
    }
    return entityOf(engine, z, a);
  }

  /** <code>Row({Superscript("", 209), "Th"})</code> - the mass number written before the symbol. */
  private static IExpr symbolOf(Nuclide nuclide) {
    String element = org.openscience.cdk.config.Elements.ofNumber(nuclide.z).symbol();
    return F.unaryAST1(S.Row,
        F.List(F.Superscript(F.stringx(""), F.ZZ(nuclide.a)), F.stringx(element)));
  }

  private static IExpr nuclideProperty(Nuclide nuclide, String property, EvalEngine engine) {
    int z = nuclide.z;
    int a = nuclide.a;
    switch (property) {
      case "AtomicMass":
        return Double.isNaN(nuclide.mass) ? F.Missing(S.NotAvailable)
            : F.binaryAST2(S.Quantity, F.num(nuclide.mass), F.stringx("AtomicMassUnit"));
      case "AtomicNumber":
        return F.ZZ(z);
      case "BindingEnergy":
        return bindingEnergy(nuclide);
      case "BranchingRatios":
      case "DaughterNuclides":
      case "DecayModes":
        return decayProperty(nuclide, property, engine);
      case "HalfLife":
        return halfLife(nuclide, false);
      case "IsotopeAbundance":
        // a percentage, and zero rather than missing for a nuclide that does not occur in nature
        return abundanceOf(nuclide);
      case "Lifetime":
        return halfLife(nuclide, true);
      case "MassNumber":
        return F.ZZ(a);
      case "NeutronNumber":
        return F.ZZ(a - z);
      case "StandardName":
        return F.stringx(standardName(engine, z, a));
      case "Symbol":
        return symbolOf(nuclide);
      default:
        // no answer, so that EntityValue can report which half of the question was unknown
        return F.NIL;
    }
  }

  /** The properties of the whole element, which the most abundant isotope alone cannot answer. */
  private static IExpr elementProperty(int atomicNumber, String property, EvalEngine engine) {
    Nuclide[] nuclides = isotopesOf(atomicNumber);
    if ("MassNumbers".equals(property)) {
      IASTAppendable result = F.ListAlloc(nuclides.length);
      for (Nuclide nuclide : nuclides) {
        result.append(F.ZZ(nuclide.a));
      }
      return result;
    }
    if ("Abundances".equals(property)) {
      // every isotope that occurs naturally, as isotope -> percent. An association keyed by the
      // entity, which is the shape ElementData("...", "IsotopeAbundances") reports.
      IASTAppendable rules = F.ListAlloc(nuclides.length);
      for (Nuclide nuclide : nuclides) {
        if (nuclide.abundance != null && nuclide.abundance.doubleValue() > 0.0) {
          rules.append(F.Rule(entityOf(engine, nuclide), abundanceOf(nuclide)));
        }
      }
      return F.assoc(rules);
    }
    if ("StableIsotopes".equals(property)) {
      int[] massNumbers =
          atomicNumber < STABLE_ISOTOPES.length ? STABLE_ISOTOPES[atomicNumber] : new int[0];
      IASTAppendable result = F.ListAlloc(massNumbers.length);
      for (int massNumber : massNumbers) {
        result.append(entityOf(engine, atomicNumber, massNumber));
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

      Nuclide nuclide = nuclideOf(arg1, engine);
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
        // a question about the whole element is answered before asking whether the table has any
        // of its isotopes: the stable ones come from a table of their own
        IExpr whole = elementProperty(atomicNumber, propertyExpr.toString(), engine);
        if (whole.isPresent()) {
          return whole;
        }
      }
      Nuclide[] nuclides = isotopesOf(atomicNumber);
      if (nuclides.length == 0) {
        return F.Missing(S.NotAvailable);
      }
      if (ast.isAST1()) {
        return entitiesOf(engine, nuclides);
      }
      String property = Entities.propertyOf(ast.arg2(), ISOTOPE).toString();
      // every other property speaks for the most abundant isotope of the element
      Nuclide major = majorIsotope(atomicNumber);
      if (major == null) {
        return F.Missing(S.NotAvailable);
      }
      if ("Abundance".equals(property) || "IsotopeAbundance".equals(property)) {
        // percent, as the element forms have always reported it
        return major.abundance == null ? F.Missing(S.NotAvailable)
            : F.num(major.abundance.doubleValue());
      }
      if ("IsotopeMass".equals(property) || "AtomicMass".equals(property)) {
        return F.num(major.mass);
      }
      IExpr value = nuclideProperty(major, property, engine);
      return value.isPresent() ? value : F.Missing(S.NotAvailable);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  /** The most abundant naturally occurring isotope, or <code>null</code> when none occurs. */
  private static Nuclide majorIsotope(int atomicNumber) {
    Nuclide best = null;
    double bestAbundance = 0.0;
    for (Nuclide nuclide : isotopesOf(atomicNumber)) {
      Double abundance = nuclide.abundance;
      if (abundance != null && abundance.doubleValue() > bestAbundance) {
        best = nuclide;
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
