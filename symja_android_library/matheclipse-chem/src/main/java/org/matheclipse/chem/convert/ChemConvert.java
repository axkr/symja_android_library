package org.matheclipse.chem.convert;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Conversion between Symja expressions and CDK's {@link IAtomContainer}, and — more importantly —
 * <b>the one place a structure is normalised</b>.
 *
 * <p>
 * CDK results diverge from WMAs unless the same preparation runs before every operation.
 * {@link #normalize(IAtomContainer)} fixes that preparation once: atom typing, implicit hydrogens,
 * then one pinned aromaticity model. Changing {@link #AROMATICITY} silently changes
 * <code>MoleculeContainsQ</code>, <code>MoleculeEquivalentQ</code> and every SMARTS match in the
 * test suite, so it is a constant here rather than a per-call choice.
 */
public class ChemConvert {

  /** CDK's thread-safe, notification-free builder. */
  public static final IChemObjectBuilder BUILDER = SilentChemObjectBuilder.getInstance();

  /**
   * The canonical SMILES flavor. <code>SmiFlavor.Canonical</code> on its own emits the Kekule form,
   * which throws away the aromaticity {@link #normalize(IAtomContainer)} has just perceived, so
   * benzene would round-trip as <code>C=1C=CC=CC1</code>. Adding
   * <code>SmiFlavor.UseAromaticSymbols</code> keeps it as <code>c1ccccc1</code>.
   *
   * <p>
   * This is the identity of a molecule, so it must be one constant: two structures are the same
   * molecule exactly when the string produced with this flavor agrees.
   */
  public static final int CANONICAL_SMILES = org.openscience.cdk.smiles.SmiFlavor.Canonical
      | org.openscience.cdk.smiles.SmiFlavor.UseAromaticSymbols;

  /**
   * The pinned aromaticity model: Daylight electron donation over the union of all cycles and all
   * six-membered cycles. Do not vary this per call.
   */
  public static final Aromaticity AROMATICITY =
      new Aromaticity(ElectronDonation.daylight(), Cycles.or(Cycles.all(), Cycles.all(6)));

  /**
   * Parse a SMILES string into a normalised structure.
   *
   * @return <code>null</code> when the string is not valid SMILES
   */
  public static IAtomContainer fromSMILES(String smiles) {
    try {
      SmilesParser parser = new SmilesParser(BUILDER);
      IAtomContainer molecule = parser.parseSmiles(smiles);
      normalize(molecule);
      return molecule;
    } catch (CDKException e) {
      return null;
    } catch (RuntimeException e) {
      return null;
    }
  }

  /**
   * Atom typing, implicit hydrogens, aromaticity — in that order, which is the order CDK requires.
   *
   * @return <code>false</code> when the structure could not be prepared
   */
  public static boolean normalize(IAtomContainer molecule) {
    try {
      AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
      CDKHydrogenAdder.getInstance(molecule.getBuilder()).addImplicitHydrogens(molecule);
      AROMATICITY.apply(molecule);
      return true;
    } catch (CDKException e) {
      return false;
    } catch (RuntimeException e) {
      return false;
    }
  }

  /**
   * Build a structure from an explicit atom and bond list, as
   * <code>Molecule({"C", "O"}, {Bond({1, 2}, "Single")})</code> gives it.
   *
   * @return <code>null</code> when the lists are malformed
   */
  public static IAtomContainer fromAtomsAndBonds(IAST atoms, IAST bonds) {
    try {
      IAtomContainer molecule = BUILDER.newInstance(IAtomContainer.class);
      for (int i = 1; i < atoms.size(); i++) {
        IExpr atom = atoms.get(i);
        String symbol = atom.isString() ? atom.toString() : atom.toString();
        molecule.addAtom(BUILDER.newInstance(IAtom.class, symbol));
      }
      for (int i = 1; i < bonds.size(); i++) {
        IExpr bond = bonds.get(i);
        if (!bond.isAST()) {
          return null;
        }
        IAST bondAST = (IAST) bond;
        IExpr indices = bondAST.arg1();
        if (!indices.isList() || ((IAST) indices).argSize() != 2) {
          return null;
        }
        int from = ((IAST) indices).arg1().toIntDefault();
        int to = ((IAST) indices).arg2().toIntDefault();
        if (from == Integer.MIN_VALUE || to == Integer.MIN_VALUE //
            || from < 1 || to < 1 //
            || from > molecule.getAtomCount() || to > molecule.getAtomCount()) {
          return null;
        }
        IBond.Order order = IBond.Order.SINGLE;
        if (bondAST.argSize() >= 2 && bondAST.arg2().isString()) {
          order = orderOf(bondAST.arg2().toString());
          if (order == null) {
            return null;
          }
        }
        molecule.addBond(from - 1, to - 1, order);
      }
      normalize(molecule);
      return molecule;
    } catch (RuntimeException e) {
      return null;
    }
  }

  private static IBond.Order orderOf(String name) {
    if ("Single".equals(name)) {
      return IBond.Order.SINGLE;
    }
    if ("Double".equals(name)) {
      return IBond.Order.DOUBLE;
    }
    if ("Triple".equals(name)) {
      return IBond.Order.TRIPLE;
    }
    if ("Quadruple".equals(name)) {
      return IBond.Order.QUADRUPLE;
    }
    if ("Aromatic".equals(name)) {
      // CDK models aromaticity as a flag, not as a bond order
      return IBond.Order.SINGLE;
    }
    return null;
  }

  public static String nameOf(IBond.Order order) {
    if (order == null) {
      return "Single";
    }
    switch (order) {
      case DOUBLE:
        return "Double";
      case TRIPLE:
        return "Triple";
      case QUADRUPLE:
        return "Quadruple";
      default:
        return "Single";
    }
  }

  /**
   * <code>{"C", Atom("N", "FormalCharge" -&gt; 1), ...}</code> — the atoms of the molecule.
   *
   * <p>
   * A plain element symbol is used when the atom needs no annotation; an <code>Atom(...)</code>
   * wrapper carries a non-zero formal charge, matching the form <code>Molecule</code> produced
   * before this module existed.
   */
  public static IAST atomList(IAtomContainer molecule) {
    IASTAppendable result = F.ListAlloc(molecule.getAtomCount());
    for (IAtom atom : molecule.atoms()) {
      Integer charge = atom.getFormalCharge();
      if (charge != null && charge.intValue() != 0) {
        // an annotated atom also reports its hydrogen count, as Molecule did before this module
        Integer hydrogens = atom.getImplicitHydrogenCount();
        IASTAppendable annotated = F.ast(org.matheclipse.core.expression.S.Atom, 3);
        annotated.append(F.stringx(atom.getSymbol()));
        annotated.append(F.Rule(F.stringx("FormalCharge"), F.ZZ(charge.intValue())));
        if (hydrogens != null && hydrogens.intValue() > 0) {
          annotated.append(F.Rule(F.stringx("HydrogenCount"), F.ZZ(hydrogens.intValue())));
        }
        result.append(annotated);
      } else {
        result.append(F.stringx(atom.getSymbol()));
      }
    }
    return result;
  }

  /**
   * <code>{Bond({1, 2}, "Single"), ...}</code> — 1-based.
   */
  public static IAST bondList(IAtomContainer molecule) {
    IASTAppendable result = F.ListAlloc(molecule.getBondCount());
    for (IBond bond : molecule.bonds()) {
      int from = molecule.indexOf(bond.getBegin()) + 1;
      int to = molecule.indexOf(bond.getEnd()) + 1;
      String order = bond.isAromatic() ? "Aromatic" : nameOf(bond.getOrder());
      result.append(F.binaryAST2(org.matheclipse.core.expression.S.Bond,
          F.List(F.ZZ(from), F.ZZ(to)), F.stringx(order)));
    }
    return result;
  }

  /**
   * The third argument of the <code>Molecule</code> expression form: <code>{}</code>, or
   * <code>{StereochemistryElements -&gt; {...}}</code> when the structure carries stereochemistry.
   */
  public static IAST optionList(IAtomContainer molecule) {
    IASTAppendable stereo = F.ListAlloc(2);
    for (org.openscience.cdk.interfaces.IStereoElement<?, ?> element : molecule.stereoElements()) {
      stereo.append(F.stringx(element.getClass().getSimpleName()));
    }
    if (stereo.argSize() == 0) {
      return F.CEmptyList;
    }
    return F.List(F.Rule(org.matheclipse.core.expression.S.StereochemistryElements, stereo));
  }

  private ChemConvert() {}
}
