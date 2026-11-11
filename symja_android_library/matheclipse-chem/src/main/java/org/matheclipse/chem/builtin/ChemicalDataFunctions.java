package org.matheclipse.chem.builtin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import javax.vecmath.Point3d;
import org.matheclipse.chem.convert.ChemConvert;
import org.matheclipse.core.data.Entities;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * <code>ChemicalData</code> over a table of compounds taken from PubChem.
 *
 * <p>
 * The table is the resource <code>chemicals.tsv.gz</code>, written by
 * <code>tools/gen_chemical_data.py</code>. It keeps PubChem's own atom order, bond list and 2D
 * depiction coordinates, so the structure properties are PubChem's record: atom <code>i</code> of
 * <code>"VertexTypes"</code> is atom <code>i</code> of the record, an edge <code>a -&gt; b</code>
 * has <code>a &lt; b</code>, and bonds are given in their Kekulé form, never as
 * <code>"Aromatic"</code>.
 *
 * <p>
 * A compound is named by its entity name (<code>"Caffeine"</code>) or by
 * <code>Entity("Chemical", name)</code>. A name the table does not hold leaves the call standing,
 * and so does a property it does not know, with the message <code>ChemicalData::notprop</code>.
 */
public class ChemicalDataFunctions {

  private static final String CHEMICAL = "Chemical";

  private static final String COMPOUND_TABLE = "/org/matheclipse/chem/data/chemicals.tsv.gz";

  /** The properties a compound answers for, in the order <code>"Properties"</code> lists them. */
  private static final String[] PROPERTIES = {"CASNumber", "CompoundFormulaDisplay", "EdgeRules",
      "EdgeTypes", "ElementTally", "IUPACName", "MolecularFormulaString", "MolecularMass",
      "MoleculePlot", "Name", "PubChemCompoundID", "SMILES", "SpaceFillingMoleculePlot",
      "StructureDiagram", "VertexCoordinates", "VertexTypes"};

  /** PubChem's depiction coordinates are in units of a bond length; these are in hundredths. */
  private static final double COORDINATE_SCALE = 100.0;

  /**
   * Standard atomic weights as decimals, to the digits they are known to: the conventional value
   * for an element whose weight varies in nature (carbon 12.011), the full value for one with a
   * single stable isotope. The digits written are what the precision of a molecular mass is
   * worked out from.
   */
  private static final Map<String, String> ATOMIC_WEIGHTS = new TreeMap<String, String>();

  static {
    String[] weights = {"H", "1.008", "Li", "6.94", "B", "10.81", "C", "12.011", "N", "14.007",
        "O", "15.999", "F", "18.998403163", "Na", "22.98976928", "Mg", "24.305", "Al",
        "26.9815385", "Si", "28.085", "P", "30.973761998", "S", "32.06", "Cl", "35.45", "K",
        "39.0983", "Ca", "40.078", "Fe", "55.845", "Cu", "63.546", "Zn", "65.38", "Se", "78.971",
        "Br", "79.904", "I", "126.90447"};
    for (int i = 0; i < weights.length; i += 2) {
      ATOMIC_WEIGHTS.put(weights[i], weights[i + 1]);
    }
  }

  private static final String NOTPROP = "`1` is not a known property for ChemicalData. Use"
      + " ChemicalData[\"Properties\"] for a list of properties.";

  private static final String[] BOND_TYPES = {"", "Single", "Double", "Triple"};

  private static class Initializer {

    private static void init() {
      S.ChemicalData.setEvaluator(new ChemicalData());
      Entities.register(CHEMICAL, S.ChemicalData);
    }
  }

  /** One compound of the table. */
  private static final class Compound {
    final String entity;
    final int cid;
    final String name;
    final String iupacName;
    final String formula;
    final String smiles;
    final String mass;
    final String[] atoms;
    /** Formal charge per atom, zero for most. */
    final int[] atomCharges;
    /** The 2D coordinates as written, so that scaling them by a hundred adds no digits. */
    final String[][] xy;
    /** <code>{a, b, order}</code> per bond, 1-based, <code>a &lt; b</code>. */
    final int[][] bonds;
    /** The 3D conformer, or <code>null</code> when PubChem has none. */
    final double[][] xyz;
    /** The CAS registry number without its prefix, or empty. */
    final String cas;

    Compound(String[] row) {
      entity = row[0];
      cid = Integer.parseInt(row[1]);
      name = row[2];
      iupacName = row[3];
      formula = row[4];
      smiles = row[5];
      mass = row[7];
      atoms = row[8].split(",");
      atomCharges = new int[atoms.length];
      if (!row[9].isEmpty()) {
        for (String entry : row[9].split(",")) {
          String[] pair = entry.split(":");
          atomCharges[Integer.parseInt(pair[0]) - 1] = Integer.parseInt(pair[1]);
        }
      }
      String[] points = row[10].split(";");
      xy = new String[points.length][];
      for (int i = 0; i < points.length; i++) {
        xy[i] = points[i].split(" ");
      }
      if (row[11].isEmpty()) {
        bonds = new int[0][];
      } else {
        String[] entries = row[11].split(";");
        bonds = new int[entries.length][];
        for (int i = 0; i < entries.length; i++) {
          String[] parts = entries[i].split(" ");
          // the aromatic mark after the order is not used: bonds are reported in Kekulé form
          bonds[i] = new int[] {Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
              Integer.parseInt(parts[2].substring(0, 1))};
        }
      }
      if (row.length > 12 && !row[12].isEmpty()) {
        String[] conformer = row[12].split(";");
        xyz = new double[conformer.length][];
        for (int i = 0; i < conformer.length; i++) {
          String[] parts = conformer[i].split(" ");
          xyz[i] = new double[] {Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
              Double.parseDouble(parts[2])};
        }
      } else {
        xyz = null;
      }
      cas = row.length > 13 ? row[13] : "";
    }
  }

  /** The compounds by entity name, in table order; read on first use. */
  private static volatile Map<String, Compound> compounds;

  /** The same compounds by entity name folded to lower case. */
  private static volatile Map<String, Compound> byLowerCase;

  private static Map<String, Compound> compounds() {
    Map<String, Compound> local = compounds;
    if (local == null) {
      synchronized (ChemicalDataFunctions.class) {
        local = compounds;
        if (local == null) {
          local = readTable();
          Map<String, Compound> folded = new TreeMap<String, Compound>();
          for (Compound compound : local.values()) {
            folded.put(compound.entity.toLowerCase(Locale.US), compound);
          }
          byLowerCase = folded;
          compounds = local;
        }
      }
    }
    return local;
  }

  private static Map<String, Compound> readTable() {
    Map<String, Compound> result = new LinkedHashMap<String, Compound>();
    try (InputStream raw = ChemicalDataFunctions.class.getResourceAsStream(COMPOUND_TABLE)) {
      if (raw == null) {
        return Collections.emptyMap();
      }
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(new GZIPInputStream(raw), StandardCharsets.UTF_8));
      String line = reader.readLine();
      while ((line = reader.readLine()) != null) {
        if (!line.isEmpty()) {
          Compound compound = new Compound(line.split("\t", -1));
          result.put(compound.entity, compound);
        }
      }
    } catch (IOException | RuntimeException e) {
      return Collections.emptyMap();
    }
    return result;
  }

  /** The compound an argument names, or <code>null</code>. */
  private static Compound compoundOf(IExpr expr) {
    IExpr name = Entities.nameOf(expr, CHEMICAL);
    if (!name.isString()) {
      return null;
    }
    Compound compound = compounds().get(name.toString());
    return compound != null ? compound : byLowerCase.get(name.toString().toLowerCase(Locale.US));
  }

  // ---------------------------------------------------------------- formula

  /**
   * Every element and how often it occurs, in Hill order: carbon first, hydrogen second, the rest
   * alphabetically - and everything alphabetically when there is no carbon.
   */
  private static List<Map.Entry<String, Integer>> tally(Compound compound) {
    Map<String, Integer> counts = new TreeMap<String, Integer>();
    for (String atom : compound.atoms) {
      counts.merge(atom, Integer.valueOf(1), Integer::sum);
    }
    List<Map.Entry<String, Integer>> result =
        new ArrayList<Map.Entry<String, Integer>>(counts.entrySet());
    if (counts.containsKey("C")) {
      result.sort((a, b) -> Integer.compare(hillRank(a.getKey()), hillRank(b.getKey())));
    }
    return result;
  }

  /** Carbon before hydrogen before the rest; the sort is stable, so the rest stay alphabetical. */
  private static int hillRank(String element) {
    if ("C".equals(element)) {
      return 0;
    }
    return "H".equals(element) ? 1 : 2;
  }

  private static IExpr elementTally(Compound compound) {
    List<Map.Entry<String, Integer>> tally = tally(compound);
    IASTAppendable result = F.ListAlloc(tally.size());
    for (Map.Entry<String, Integer> entry : tally) {
      result.append(F.List(F.stringx(entry.getKey()), F.ZZ(entry.getValue().intValue())));
    }
    return result;
  }

  /**
   * The formula for display: <code>Style(Row({Subscript("C", 4), Subscript("H", 10)}),
   * ScriptBaselineShifts -&gt; {0.5, 0.75})</code>, an element that occurs once written bare.
   */
  private static IExpr formulaDisplay(Compound compound) {
    List<Map.Entry<String, Integer>> tally = tally(compound);
    IASTAppendable row = F.ListAlloc(tally.size());
    for (Map.Entry<String, Integer> entry : tally) {
      IExpr element = F.stringx(entry.getKey());
      int count = entry.getValue().intValue();
      row.append(count == 1 ? element : F.Subscript(element, F.ZZ(count)));
    }
    return F.Style(F.unaryAST1(S.Row, row),
        F.Rule(S.ScriptBaselineShifts, F.List(F.num(0.5), F.num(0.75))));
  }

  // ---------------------------------------------------------------- structure

  private static IExpr vertexTypes(Compound compound) {
    IASTAppendable result = F.ListAlloc(compound.atoms.length);
    for (String atom : compound.atoms) {
      result.append(F.stringx(atom));
    }
    return result;
  }

  private static IExpr edgeRules(Compound compound) {
    IASTAppendable result = F.ListAlloc(compound.bonds.length);
    for (int[] bond : compound.bonds) {
      result.append(F.Rule(F.ZZ(bond[0]), F.ZZ(bond[1])));
    }
    return result;
  }

  private static IExpr edgeTypes(Compound compound) {
    IASTAppendable result = F.ListAlloc(compound.bonds.length);
    for (int[] bond : compound.bonds) {
      result.append(F.stringx(BOND_TYPES[Math.min(Math.max(bond[2], 1), 3)]));
    }
    return result;
  }

  /** PubChem's 2D depiction, scaled from bond lengths to hundredths of one. */
  private static IExpr vertexCoordinates(Compound compound) {
    IASTAppendable result = F.ListAlloc(compound.xy.length);
    for (String[] point : compound.xy) {
      result.append(F.List(scaled(point[0]), scaled(point[1])));
    }
    return result;
  }

  /** A coordinate in hundredths, scaled in machine arithmetic. */
  private static IExpr scaled(String coordinate) {
    return F.num(Double.parseDouble(coordinate) * COORDINATE_SCALE);
  }

  /**
   * The molecular mass from the standard atomic weights, with the precision the digits of those
   * weights allow: each weight is uncertain in its last digit relative to its size, so the mass
   * <code>M = Sum(n_i w_i)</code> is uncertain by <code>Sum(n_i w_i 10^-d_i)</code>, where
   * <code>d_i</code> counts the digits of <code>w_i</code>. Ethanol is <code>46.069</code> to a
   * precision of 4.66. A compound with an element the table has no weight for falls back to
   * PubChem's molecular weight.
   */
  private static IExpr molecularMass(Compound compound) {
    BigDecimal mass = BigDecimal.ZERO;
    double uncertainty = 0.0;
    for (Map.Entry<String, Integer> entry : tally(compound)) {
      String weight = ATOMIC_WEIGHTS.get(entry.getKey());
      if (weight == null) {
        return F.Quantity(F.num(Double.parseDouble(compound.mass)), F.stringx("AtomicMassUnit"));
      }
      BigDecimal w = new BigDecimal(weight);
      int count = entry.getValue().intValue();
      mass = mass.add(w.multiply(BigDecimal.valueOf(count)));
      uncertainty += count * w.doubleValue() * Math.pow(10.0, -w.precision());
    }
    double precision = Math.log10(mass.doubleValue() / uncertainty);
    return F.Quantity(F.num(mass.toPlainString(), precision), F.stringx("AtomicMassUnit"));
  }

  /**
   * The compound as a CDK molecule with 3D coordinates: PubChem's conformer, or the 2D depiction
   * in the plane when there is none.
   */
  private static IAtomContainer positioned(Compound compound) {
    IAtomContainer molecule = ChemConvert.BUILDER.newInstance(IAtomContainer.class);
    for (int i = 0; i < compound.atoms.length; i++) {
      IAtom atom = ChemConvert.BUILDER.newInstance(IAtom.class, compound.atoms[i]);
      atom.setImplicitHydrogenCount(Integer.valueOf(0));
      atom.setFormalCharge(Integer.valueOf(compound.atomCharges[i]));
      if (compound.xyz != null && i < compound.xyz.length) {
        double[] p = compound.xyz[i];
        atom.setPoint3d(new Point3d(p[0], p[1], p[2]));
      } else {
        atom.setPoint3d(new Point3d(Double.parseDouble(compound.xy[i][0]),
            Double.parseDouble(compound.xy[i][1]), 0.0));
      }
      molecule.addAtom(atom);
    }
    for (int[] bond : compound.bonds) {
      IBond.Order order = bond[2] == 3 ? IBond.Order.TRIPLE
          : bond[2] == 2 ? IBond.Order.DOUBLE : IBond.Order.SINGLE;
      molecule.addBond(bond[0] - 1, bond[1] - 1, order);
    }
    return molecule;
  }

  /** The 2D structure diagram, drawn from the SMILES by <code>MoleculePlot</code>. */
  private static IExpr structureDiagram(Compound compound, EvalEngine engine) {
    IExpr diagram = engine.evaluate(
        F.unaryAST1(S.MoleculePlot, F.unaryAST1(S.Molecule, F.stringx(compound.smiles))));
    return diagram.isAST(S.Graphics) ? diagram : F.Missing(S.NotAvailable);
  }

  private static IExpr property(Compound compound, String property, EvalEngine engine) {
    switch (property) {
      case "CASNumber":
        return compound.cas.isEmpty() ? F.Missing(S.NotAvailable)
            : F.stringx("CAS" + compound.cas);
      case "CompoundFormulaDisplay":
        return formulaDisplay(compound);
      case "EdgeRules":
        return edgeRules(compound);
      case "EdgeTypes":
        return edgeTypes(compound);
      case "ElementTally":
        return elementTally(compound);
      case "IUPACName":
        return compound.iupacName.isEmpty() ? F.Missing(S.NotAvailable)
            : F.stringx(compound.iupacName);
      case "MolecularFormulaString":
        return F.stringx(compound.formula);
      case "MolecularMass":
        return molecularMass(compound);
      case "MoleculePlot":
        return DepictionFunctions.model3D(positioned(compound), false);
      case "Name":
        return F.stringx(compound.name);
      case "PubChemCompoundID":
        return F.ZZ(compound.cid);
      case "SMILES":
        return F.stringx(compound.smiles);
      case "SpaceFillingMoleculePlot":
        return DepictionFunctions.model3D(positioned(compound), true);
      case "StructureDiagram":
        return structureDiagram(compound, engine);
      case "VertexCoordinates":
        return vertexCoordinates(compound);
      case "VertexTypes":
        return vertexTypes(compound);
      default:
        return F.NIL;
    }
  }

  private static class ChemicalData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        Map<String, Compound> table = compounds();
        IASTAppendable result = F.ListAlloc(table.size());
        for (String entity : table.keySet()) {
          result.append(Entities.entity(CHEMICAL, F.stringx(entity)));
        }
        return result;
      }
      if (ast.isAST1() && ast.arg1().isString() && ast.arg1().toString().equals("Properties")) {
        return F.mapRange(0, PROPERTIES.length, i -> Entities.property(CHEMICAL, PROPERTIES[i]));
      }
      Compound compound = compoundOf(ast.arg1());
      if (compound == null) {
        // a name this table does not hold is no answer at all
        return F.NIL;
      }
      if (ast.isAST1()) {
        return structureDiagram(compound, engine);
      }
      IExpr property = Entities.propertyOf(ast.arg2(), CHEMICAL);
      if (!property.isString()) {
        return F.NIL;
      }
      IExpr value = property(compound, property.toString(), engine);
      if (value.isPresent()) {
        return value;
      }
      // a property this table does not know is reported, and the call stays as it is
      return Errors.printMessage(S.ChemicalData, "notprop", F.List(property), engine);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.putMessage(IPatternMatcher.SET, "notprop", F.stringx(NOTPROP));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ChemicalDataFunctions() {}
}
