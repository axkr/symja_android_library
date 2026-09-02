package org.matheclipse.chem.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.chem.convert.ChemColors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.Pattern;

/**
 * The optional second argument of the molecule plots: which parts to pick out, and what to call
 * them in the legend.
 *
 * <pre>
 * MoleculePlot3D(mol, &lt;|"carbonyl" -&gt; Bond({"C", "O"}, "Double"),
 *                      "ring carbons" -&gt; Atom("C", "RingAtomQ" -&gt; True)|&gt;)
 * </pre>
 *
 * <p>
 * An association names each highlight and those names become the <code>PlotLegends</code> of the
 * emitted graphics; a plain list highlights without naming. Colours come from the same chart cycle
 * the rest of Symja uses, so a highlight reads as a chart category rather than as a chemistry
 * convention.
 *
 * <p>
 * Highlights are matched in the order written and the first one to claim an atom or bond keeps it,
 * so overlapping specifications stay predictable.
 */
final class MoleculeHighlights {

  /** A specification that has not been matched against a molecule yet. */
  private final List<IExpr> specs;

  /** Legend labels, or an empty list when the argument was a plain list of specifications. */
  private final List<String> labels;

  private MoleculeHighlights(List<IExpr> specs, List<String> labels) {
    this.specs = specs;
    this.labels = labels;
  }

  /**
   * Reads the highlight argument.
   *
   * @return <code>null</code> when <code>arg</code> is not one - an option rule, or a list of them,
   *         belongs to the caller instead
   */
  static MoleculeHighlights parse(IExpr arg) {
    if (arg.isRule() || arg.isListOfRules(false)) {
      return null;
    }
    if (arg instanceof IAssociation) {
      IAssociation association = (IAssociation) arg;
      List<IExpr> specs = new ArrayList<IExpr>();
      List<String> labels = new ArrayList<String>();
      IAST keys = association.keys();
      for (int i = 1; i < keys.size(); i++) {
        IExpr key = keys.get(i);
        specs.add(association.getValue(key));
        labels.add(key.toString());
      }
      return specs.isEmpty() ? null : new MoleculeHighlights(specs, labels);
    }
    if (arg.isList()) {
      IAST list = (IAST) arg;
      List<IExpr> specs = new ArrayList<IExpr>();
      for (int i = 1; i < list.size(); i++) {
        specs.add(list.get(i));
      }
      return specs.isEmpty() ? null : new MoleculeHighlights(specs, new ArrayList<String>());
    }
    if (arg.isAST(S.Atom) || arg.isAST(S.Bond) || arg.isString()
        || arg.isAST(S.MoleculePattern, 2)) {
      List<IExpr> specs = new ArrayList<IExpr>();
      specs.add(arg);
      return new MoleculeHighlights(specs, new ArrayList<String>());
    }
    return null;
  }

  int size() {
    return specs.size();
  }

  /** The colour of highlight slot <code>index</code>. */
  IAST color(int index) {
    return ChemColors.highlightColorOf(index);
  }

  /** <code>true</code> when the highlights were named and so deserve a legend. */
  boolean hasLabels() {
    return !labels.isEmpty();
  }

  IAST legendLabels() {
    IASTAppendable result = F.ListAlloc(labels.size());
    for (String label : labels) {
      result.append(F.stringx(label));
    }
    return result;
  }

  IAST legendColors() {
    IASTAppendable result = F.ListAlloc(specs.size());
    for (int i = 0; i < specs.size(); i++) {
      result.append(color(i));
    }
    return result;
  }

  /**
   * Which highlight, if any, each atom and each bond of a molecule belongs to.
   *
   * <p>
   * Entries are highlight indices, or <code>-1</code> for the parts drawn normally.
   */
  static final class Match {

    private final int[] atoms;
    private final int[] bonds;

    /** How many distinct colours the match used. */
    private int slots;

    private Match(int atomCount, int bondCount) {
      this.atoms = new int[atomCount];
      this.bonds = new int[bondCount];
      Arrays.fill(this.atoms, -1);
      Arrays.fill(this.bonds, -1);
    }

    int slotCount() {
      return slots;
    }

    int ofAtom(int index) {
      return index >= 0 && index < atoms.length ? atoms[index] : -1;
    }

    int ofBond(int index) {
      return index >= 0 && index < bonds.length ? bonds[index] : -1;
    }
  }

  /** Matches every specification against <code>molecule</code>. */
  Match match(IAtomContainer molecule, IAST ast, EvalEngine engine) {
    Match match = new Match(molecule.getAtomCount(), molecule.getBondCount());
    // ring membership is asked for by name, so perceive it once up front
    try {
      Cycles.markRingAtomsAndBonds(molecule);
    } catch (RuntimeException e) {
      // ring flags stay unset; "RingAtomQ" specifications then simply match nothing
    }
    for (int i = 0; i < specs.size(); i++) {
      // a named highlight is one colour for the whole group; an unnamed one gives every match
      // found its own colour, which is what the reference implementation does
      apply(molecule, specs.get(i), hasLabels() ? i : -1, match, ast, engine);
    }
    if (hasLabels()) {
      match.slots = specs.size();
    }
    return match;
  }

  private void apply(IAtomContainer molecule, IExpr spec, int highlight, Match match, IAST ast,
      EvalEngine engine) {
    if (spec.isAST(S.Atom)) {
      applyAtomSpec(molecule, (IAST) spec, highlight, match);
      return;
    }
    if (spec.isAST(S.Bond)) {
      applyBondSpec(molecule, (IAST) spec, highlight, match);
      return;
    }
    applySmartsSpec(molecule, spec, highlight, match, ast, engine);
  }

  /**
   * <code>Atom("C", "RingAtomQ" -&gt; True)</code> — an element symbol and any number of property
   * tests. The recognised properties are <code>"RingAtomQ"</code>, <code>"AromaticQ"</code>,
   * <code>"FormalCharge"</code> and <code>"HydrogenCount"</code>, the last two matching the
   * vocabulary <code>AtomList</code> already emits.
   */
  private void applyAtomSpec(IAtomContainer molecule, IAST spec, int highlight, Match match) {
    if (spec.argSize() < 1 || !spec.arg1().isString()) {
      return;
    }
    String element = spec.arg1().toString();
    for (int i = 0; i < molecule.getAtomCount(); i++) {
      if (match.atoms[i] >= 0) {
        continue;
      }
      IAtom atom = molecule.getAtom(i);
      if (!element.equals(atom.getSymbol())) {
        continue;
      }
      if (atomMatchesProperties(atom, spec)) {
        match.atoms[i] = highlight >= 0 ? highlight : match.slots++;
      }
    }
  }

  private boolean atomMatchesProperties(IAtom atom, IAST spec) {
    for (int i = 2; i < spec.size(); i++) {
      IExpr rule = spec.get(i);
      if (!rule.isRule()) {
        continue;
      }
      IExpr key = ((IAST) rule).arg1();
      IExpr value = ((IAST) rule).arg2();
      if (!key.isString()) {
        return false;
      }
      String name = key.toString();
      if ("RingAtomQ".equals(name)) {
        if (atom.isInRing() != value.isTrue()) {
          return false;
        }
      } else if ("AromaticQ".equals(name)) {
        if (atom.isAromatic() != value.isTrue()) {
          return false;
        }
      } else if ("FormalCharge".equals(name)) {
        Integer charge = atom.getFormalCharge();
        if (value.toIntDefault(Integer.MIN_VALUE) != (charge == null ? 0 : charge.intValue())) {
          return false;
        }
      } else if ("HydrogenCount".equals(name)) {
        Integer hydrogens = atom.getImplicitHydrogenCount();
        int count = hydrogens == null ? 0 : hydrogens.intValue();
        if (value.toIntDefault(Integer.MIN_VALUE) != count) {
          return false;
        }
      } else {
        // an unknown property matches nothing rather than everything
        return false;
      }
    }
    return true;
  }

  /**
   * <code>Bond({"C", "O"}, "Double")</code> — the two element symbols in either order, and
   * optionally the bond type as <code>BondList</code> spells it.
   */
  private void applyBondSpec(IAtomContainer molecule, IAST spec, int highlight, Match match) {
    if (spec.argSize() < 1 || !spec.arg1().isList()) {
      return;
    }
    IAST pair = (IAST) spec.arg1();
    if (pair.argSize() != 2 || !pair.arg1().isString() || !pair.arg2().isString()) {
      return;
    }
    String first = pair.arg1().toString();
    String second = pair.arg2().toString();
    String order = spec.argSize() >= 2 && spec.arg2().isString() ? spec.arg2().toString() : null;

    for (int i = 0; i < molecule.getBondCount(); i++) {
      if (match.bonds[i] >= 0) {
        continue;
      }
      IBond bond = molecule.getBond(i);
      String begin = bond.getBegin().getSymbol();
      String end = bond.getEnd().getSymbol();
      boolean elementsMatch = (first.equals(begin) && second.equals(end)) //
          || (first.equals(end) && second.equals(begin));
      if (!elementsMatch) {
        continue;
      }
      if (order != null && !order.equals(orderName(bond))) {
        continue;
      }
      match.bonds[i] = highlight >= 0 ? highlight : match.slots++;
    }
  }

  private static String orderName(IBond bond) {
    if (bond.isAromatic()) {
      return "Aromatic";
    }
    IBond.Order order = bond.getOrder();
    if (order == IBond.Order.SINGLE) {
      return "Single";
    }
    if (order == IBond.Order.DOUBLE) {
      return "Double";
    }
    return order == IBond.Order.TRIPLE ? "Triple" : "Unknown";
  }

  /**
   * A SMARTS string or <code>MoleculePattern("...")</code>, matched with the same machinery the
   * substructure queries use. Every matched atom is highlighted, along with the bonds that run
   * between two atoms of one match.
   */
  private void applySmartsSpec(IAtomContainer molecule, IExpr spec, int highlight, Match match,
      IAST ast, EvalEngine engine) {
    Pattern pattern = SubstructureFunctions.pattern(ast, spec, engine);
    if (pattern == null) {
      return;
    }
    try {
      for (int[] mapping : pattern.matchAll(molecule)) {
        // one colour for the whole occurrence, whether it is named or not
        int slot = highlight >= 0 ? highlight : match.slots;
        boolean claimed = false;
        boolean[] inMatch = new boolean[molecule.getAtomCount()];
        for (int index : mapping) {
          if (index >= 0 && index < inMatch.length) {
            inMatch[index] = true;
            if (match.atoms[index] < 0) {
              match.atoms[index] = slot;
              claimed = true;
            }
          }
        }
        for (int i = 0; i < molecule.getBondCount(); i++) {
          if (match.bonds[i] >= 0) {
            continue;
          }
          IBond bond = molecule.getBond(i);
          int begin = molecule.indexOf(bond.getBegin());
          int end = molecule.indexOf(bond.getEnd());
          if (begin >= 0 && end >= 0 && inMatch[begin] && inMatch[end]) {
            match.bonds[i] = slot;
            claimed = true;
          }
        }
        if (claimed && highlight < 0) {
          match.slots++;
        }
      }
    } catch (RuntimeException e) {
      // a pattern that cannot be run against this molecule highlights nothing
    }
  }
}
