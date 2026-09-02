package org.matheclipse.chem.builtin;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.chem.convert.ChemConvert;
import org.matheclipse.chem.expression.data.MoleculeExpr;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Tier A of the <code>matheclipse-chem</code> module: constructing molecules and reading their
 * properties, backed by CDK.
 */
public class MoleculeFunctions {

  private static class Initializer {

    private static void init() {
      S.Molecule.setEvaluator(new Molecule());
      S.MoleculeQ.setEvaluator(new MoleculeQ());
      S.MoleculeValue.setEvaluator(new MoleculeValue());
      S.MoleculeProperty.setEvaluator(new MoleculeProperty());
      S.AtomCount.setEvaluator(new AtomCount());
      S.BondCount.setEvaluator(new BondCount());
      S.AtomList.setEvaluator(new AtomList());
      S.BondList.setEvaluator(new BondList());
      S.ChemicalFormula.setEvaluator(new ChemicalFormula());
      S.ChemicalConvert.setEvaluator(new ChemicalConvert());
      S.MoleculeName.setEvaluator(new MoleculeName());
      S.ConnectedMoleculeQ.setEvaluator(new ConnectedMoleculeQ());
      S.ConnectedMoleculeComponents.setEvaluator(new ConnectedMoleculeComponents());
    }
  }

  /**
   * A handful of common names, so <code>Molecule("water")</code> keeps working. The previous
   * hand-rolled implementation carried a twelve-entry table of this kind; it is kept because CDK
   * has no name resolver of its own (that would need OPSIN, which is not a dependency).
   */
  private static final Map<String, String> NAME_TO_SMILES = new HashMap<String, String>();

  static {
    NAME_TO_SMILES.put("acetaldehyde", "CC=O");
    NAME_TO_SMILES.put("acetic acid", "CC(=O)O");
    NAME_TO_SMILES.put("acetone", "CC(=O)C");
    NAME_TO_SMILES.put("acetylene", "C#C");
    NAME_TO_SMILES.put("ammonia", "N");
    NAME_TO_SMILES.put("benzene", "c1ccccc1");
    NAME_TO_SMILES.put("carbon dioxide", "O=C=O");
    NAME_TO_SMILES.put("carbon monoxide", "[C-]#[O+]");
    NAME_TO_SMILES.put("ethane", "CC");
    NAME_TO_SMILES.put("ethanol", "CCO");
    NAME_TO_SMILES.put("methane", "C");
    NAME_TO_SMILES.put("water", "O");
  }

  /**
   * <code>Molecule("SMILES")</code>, <code>Molecule("name")</code> or
   * <code>Molecule({atoms}, {bonds})</code>.
   */
  private static class Molecule extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();
        if (arg1 instanceof MoleculeExpr) {
          return arg1;
        }
        if (arg1.isString()) {
          String input = arg1.toString();
          String smiles = NAME_TO_SMILES.get(input.toLowerCase());
          if (smiles == null) {
            smiles = input;
          }
          IAtomContainer molecule = ChemConvert.fromSMILES(smiles);
          if (molecule == null) {
            return Errors.printMessage(ast.topHead(), "nointerp", F.List(arg1), engine);
          }
          return MoleculeExpr.newInstance(molecule);
        }
        return F.NIL;
      }
      if (ast.isAST2() && ast.arg1().isList() && ast.arg2().isList()) {
        IAtomContainer molecule =
            ChemConvert.fromAtomsAndBonds((IAST) ast.arg1(), (IAST) ast.arg2());
        if (molecule == null) {
          return Errors.printMessage(ast.topHead(), "nointerp", F.List(ast.arg1()), engine);
        }
        return MoleculeExpr.newInstance(molecule);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class MoleculeQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.booleSymbol(ast.arg1() instanceof MoleculeExpr);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>MoleculeValue(mol, "property")</code> and the list form. */
  private static class MoleculeValue extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      if (ast.isAST1()) {
        return propertyNames();
      }
      IExpr property = ast.arg2();
      if (property.isString()) {
        return valueOf(molecule, property.toString());
      }
      if (property.isList()) {
        IAST properties = (IAST) property;
        IASTAppendable result = F.ListAlloc(properties.argSize());
        for (int i = 1; i < properties.size(); i++) {
          IExpr value =
              properties.get(i).isString() ? valueOf(molecule, properties.get(i).toString())
                  : F.NIL;
          result.append(value.isPresent() ? value : F.Missing(F.stringx("NotAvailable")));
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>MoleculeProperty("name")</code> — the list of supported property names. */
  private static class MoleculeProperty extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return propertyNames();
      }
      if (ast.isAST1() && ast.arg1().isString()) {
        String name = ast.arg1().toString();
        for (int i = 0; i < PROPERTIES.length; i++) {
          if (PROPERTIES[i].equals(name)) {
            return F.stringx(name);
          }
        }
        return F.Missing(F.stringx("NotAvailable"));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  private static class AtomCount extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      return F.ZZ(withExplicitHydrogens(molecule).getAtomCount());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class BondCount extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      return F.ZZ(withExplicitHydrogens(molecule).getBondCount());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class AtomList extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      return ChemConvert.atomList(withExplicitHydrogens(molecule));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class BondList extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      return ChemConvert.bondList(withExplicitHydrogens(molecule));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>ChemicalFormula(mol)</code> — the Hill-notation molecular formula. */
  private static class ChemicalFormula extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      return F.stringx(hillFormula(molecule));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>ChemicalConvert(mol, "format")</code> — SMILES and formula output. */
  private static class ChemicalConvert extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      if (!ast.arg2().isString()) {
        return F.NIL;
      }
      String format = ast.arg2().toString();
      if ("SMILES".equals(format)) {
        return F.stringx(smiles(molecule, ChemConvert.CANONICAL_SMILES));
      }
      if ("IsomericSMILES".equals(format)) {
        return F.stringx(smiles(molecule, SmiFlavor.Isomeric));
      }
      if ("MolecularFormula".equals(format) || "ChemicalFormula".equals(format)) {
        return F.stringx(hillFormula(molecule));
      }
      return Errors.printMessage(ast.topHead(), "chemfmt", F.List(ast.arg2()), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>MoleculeName(mol)</code> — the common name, when the molecule is one of the few named
   * here. CDK ships no name resolver, so anything else is <code>Missing("NotAvailable")</code>.
   */
  private static class MoleculeName extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      String canonical = smiles(molecule, ChemConvert.CANONICAL_SMILES);
      for (Map.Entry<String, String> entry : NAME_TO_SMILES.entrySet()) {
        IAtomContainer known = ChemConvert.fromSMILES(entry.getValue());
        if (known != null && canonical.equals(smiles(known, ChemConvert.CANONICAL_SMILES))) {
          return F.stringx(entry.getKey());
        }
      }
      return F.Missing(F.stringx("NotAvailable"));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ConnectedMoleculeQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      return F.booleSymbol(ConnectivityChecker.isConnected(molecule));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ConnectedMoleculeComponents extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      IAtomContainerSet components = ConnectivityChecker.partitionIntoMolecules(molecule);
      IASTAppendable result = F.ListAlloc(components.getAtomContainerCount());
      for (int i = 0; i < components.getAtomContainerCount(); i++) {
        result.append(MoleculeExpr.newInstance(components.getAtomContainer(i)));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  // ---------------------------------------------------------------- helpers

  /** The property names {@code MoleculeValue} answers. */
  private static final String[] PROPERTIES = { //
      "AtomCount", //
      "BondCount", //
      "CanonicalSMILES", //
      "ExactMass", //
      "FormalCharge", //
      "HeavyAtomCount", //
      "MolecularFormula", //
      "MolecularMass", //
      "MonoisotopicMass"};

  private static IExpr valueOf(IAtomContainer molecule, String property) {
    if ("AtomCount".equals(property)) {
      return F.ZZ(withExplicitHydrogens(molecule).getAtomCount());
    }
    if ("HeavyAtomCount".equals(property)) {
      return F.ZZ(AtomContainerManipulator.removeHydrogens(molecule).getAtomCount());
    }
    if ("BondCount".equals(property)) {
      return F.ZZ(withExplicitHydrogens(molecule).getBondCount());
    }
    if ("MolecularFormula".equals(property)) {
      return F.stringx(hillFormula(molecule));
    }
    if ("CanonicalSMILES".equals(property)) {
      return F.stringx(smiles(molecule, ChemConvert.CANONICAL_SMILES));
    }
    if ("MolecularMass".equals(property)) {
      return F.num(AtomContainerManipulator.getMass(molecule, AtomContainerManipulator.MolWeight));
    }
    if ("MonoisotopicMass".equals(property) || "ExactMass".equals(property)) {
      return F
          .num(AtomContainerManipulator.getMass(molecule, AtomContainerManipulator.MonoIsotopic));
    }
    if ("FormalCharge".equals(property)) {
      int charge = 0;
      for (org.openscience.cdk.interfaces.IAtom atom : molecule.atoms()) {
        Integer formal = atom.getFormalCharge();
        if (formal != null) {
          charge += formal.intValue();
        }
      }
      return F.ZZ(charge);
    }
    return F.Missing(F.stringx("NotAvailable"));
  }

  private static IExpr propertyNames() {
    IASTAppendable result = F.ListAlloc(PROPERTIES.length);
    for (int i = 0; i < PROPERTIES.length; i++) {
      result.append(F.stringx(PROPERTIES[i]));
    }
    return result;
  }

  private static String hillFormula(IAtomContainer molecule) {
    IMolecularFormula formula =
        MolecularFormulaManipulator.getMolecularFormula(withExplicitHydrogens(molecule));
    return MolecularFormulaManipulator.getString(formula);
  }

  private static String smiles(IAtomContainer molecule, int flavor) {
    try {
      return new SmilesGenerator(flavor).create(molecule);
    } catch (CDKException e) {
      return "";
    } catch (RuntimeException e) {
      return "";
    }
  }

  /**
   * CDK keeps hydrogens implicit; WMAs counts them. Convert to a copy with explicit hydrogens
   * rather than mutating the stored structure.
   */
  static IAtomContainer withExplicitHydrogens(IAtomContainer molecule) {
    try {
      IAtomContainer copy = molecule.clone();
      AtomContainerManipulator.convertImplicitToExplicitHydrogens(copy);
      return copy;
    } catch (CloneNotSupportedException e) {
      return molecule;
    } catch (RuntimeException e) {
      return molecule;
    }
  }

  /**
   * The SMILES recorded alongside a picture of a structure, or {@code null} when it cannot be
   * written.
   *
   * <p>
   * This keeps the stereo descriptors, because a diagram draws the stereochemistry and the
   * annotation should say what was drawn. That rules out the canonical flavour, which drops them -
   * and canonical-with-stereo is not available, since CDK routes it through <code>cdk-inchi</code>,
   * a module this one deliberately does not depend on. So the atom order is the input's rather than
   * a canonical one; {@code MoleculeValue(mol, "CanonicalSMILES")} remains the canonical form.
   */
  static String depictionSmiles(IAtomContainer molecule) {
    String smiles = smiles(molecule, org.openscience.cdk.smiles.SmiFlavor.Isomeric);
    return smiles.isEmpty() ? null : smiles;
  }

  /** The argument as a CDK structure: a {@link MoleculeExpr}, or a SMILES/name string. */
  public static IAtomContainer molecule(IExpr expr) {
    if (expr instanceof MoleculeExpr) {
      return ((MoleculeExpr) expr).toData();
    }
    if (expr.isString()) {
      String input = expr.toString();
      String smiles = NAME_TO_SMILES.get(input.toLowerCase());
      return ChemConvert.fromSMILES(smiles == null ? input : smiles);
    }
    return null;
  }

  public static void initialize() {
    Initializer.init();
  }

  private MoleculeFunctions() {}
}
