package org.matheclipse.chem.builtin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.chem.convert.ChemConvert;
import org.matheclipse.chem.expression.data.MoleculeExpr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Tiers C-E of the <code>matheclipse-chem</code> module: structural editing, the molecular graph,
 * isotope data, and balancing chemical reactions.
 *
 * <p>
 * Reaction balancing is pure integer linear algebra over element counts, so it needs no CDK
 * machinery beyond the formula it already computes.
 */
public class ReactionFunctions {

  private static class Initializer {

    private static void init() {
      S.MoleculeModify.setEvaluator(new MoleculeModify());
      S.MoleculeGraph.setEvaluator(new MoleculeGraph());
      S.IsotopeData.setEvaluator(new IsotopeData());
      S.ReactionBalance.setEvaluator(new ReactionBalance());
      S.ReactionBalancedQ.setEvaluator(new ReactionBalancedQ());
    }
  }

  /** <code>MoleculeModify("operation", mol)</code> — structural edits. */
  private static class MoleculeModify extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      String operation = ast.arg1().toString();
      IAtomContainer molecule = MoleculeFunctions.molecule(ast.arg2());
      if (molecule == null) {
        return F.NIL;
      }
      try {
        if ("AddHydrogens".equals(operation)) {
          return MoleculeExpr.newInstance(MoleculeFunctions.withExplicitHydrogens(molecule));
        }
        if ("RemoveHydrogens".equals(operation)) {
          return MoleculeExpr.newInstance(AtomContainerManipulator.removeHydrogens(molecule));
        }
        if ("LargestFragment".equals(operation)) {
          IAtomContainerSet components = ConnectivityChecker.partitionIntoMolecules(molecule);
          IAtomContainer largest = null;
          for (int i = 0; i < components.getAtomContainerCount(); i++) {
            IAtomContainer candidate = components.getAtomContainer(i);
            if (largest == null || candidate.getAtomCount() > largest.getAtomCount()) {
              largest = candidate;
            }
          }
          return largest == null ? F.NIL : MoleculeExpr.newInstance(largest);
        }
        if ("Aromatize".equals(operation)) {
          IAtomContainer copy = molecule.clone();
          ChemConvert.AROMATICITY.apply(copy);
          return MoleculeExpr.newInstance(copy);
        }
        if ("Kekulize".equals(operation)) {
          IAtomContainer copy = molecule.clone();
          org.openscience.cdk.aromaticity.Kekulization.kekulize(copy);
          return MoleculeExpr.newInstance(copy);
        }
      } catch (Exception e) {
        return Errors.printMessage(ast.topHead(), "chemmod", F.List(ast.arg1()), engine);
      }
      return Errors.printMessage(ast.topHead(), "chemmod", F.List(ast.arg1()), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>MoleculeGraph(mol)</code> — the molecular skeleton as an ordinary Symja
   * <code>Graph</code>, so all the graph-theory functions apply to it.
   */
  private static class MoleculeGraph extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = MoleculeFunctions.molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      IASTAppendable edges = F.ListAlloc(molecule.getBondCount());
      for (IBond bond : molecule.bonds()) {
        int from = molecule.indexOf(bond.getBegin()) + 1;
        int to = molecule.indexOf(bond.getEnd()) + 1;
        edges.append(F.binaryAST2(S.UndirectedEdge, F.ZZ(from), F.ZZ(to)));
      }
      IASTAppendable vertices = F.ListAlloc(molecule.getAtomCount());
      for (int i = 1; i <= molecule.getAtomCount(); i++) {
        vertices.append(F.ZZ(i));
      }
      return F.binaryAST2(S.Graph, vertices, edges);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>IsotopeData("element")</code> and <code>IsotopeData("element", "property")</code>, over
   * CDK's bundled isotope table.
   *
   * <p>
   * Every property but <code>"Abundances"</code> describes the most abundant isotope;
   * <code>"Abundances"</code> describes them all, as <code>massNumber -&gt; percent</code> for the
   * isotopes that occur naturally.
   */
  private static class IsotopeData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      String element = ast.arg1().toString();
      try {
        org.openscience.cdk.config.IsotopeFactory factory =
            org.openscience.cdk.config.Isotopes.getInstance();
        org.openscience.cdk.interfaces.IIsotope[] isotopes = factory.getIsotopes(element);
        if (isotopes == null || isotopes.length == 0) {
          return F.Missing(F.stringx("NotAvailable"));
        }
        if (ast.isAST1()) {
          IASTAppendable result = F.ListAlloc(isotopes.length);
          for (int i = 0; i < isotopes.length; i++) {
            if (isotopes[i].getMassNumber() != null) {
              result.append(F.ZZ(isotopes[i].getMassNumber().intValue()));
            }
          }
          return result;
        }
        if (!ast.arg2().isString()) {
          return F.NIL;
        }
        String property = ast.arg2().toString();
        if ("Abundances".equals(property)) {
          // every isotope that occurs naturally, as massNumber -> percent. The rest of this
          // function speaks for the most abundant isotope alone, which cannot answer this.
          IASTAppendable abundances = F.ListAlloc(isotopes.length);
          for (int i = 0; i < isotopes.length; i++) {
            Double abundance = isotopes[i].getNaturalAbundance();
            if (isotopes[i].getMassNumber() != null && abundance != null
                && abundance.doubleValue() > 0.0) {
              abundances.append(F.Rule(F.ZZ(isotopes[i].getMassNumber().intValue()),
                  F.num(abundance.doubleValue())));
            }
          }
          return abundances;
        }
        org.openscience.cdk.interfaces.IIsotope major = factory.getMajorIsotope(element);
        if (major == null) {
          return F.Missing(F.stringx("NotAvailable"));
        }
        if ("AtomicNumber".equals(property)) {
          return major.getAtomicNumber() == null ? F.Missing(F.stringx("NotAvailable"))
              : F.ZZ(major.getAtomicNumber().intValue());
        }
        if ("MassNumber".equals(property)) {
          return major.getMassNumber() == null ? F.Missing(F.stringx("NotAvailable"))
              : F.ZZ(major.getMassNumber().intValue());
        }
        if ("AtomicMass".equals(property) || "IsotopeMass".equals(property)) {
          return major.getExactMass() == null ? F.Missing(F.stringx("NotAvailable"))
              : F.num(major.getExactMass().doubleValue());
        }
        if ("Abundance".equals(property) || "IsotopeAbundance".equals(property)) {
          return major.getNaturalAbundance() == null ? F.Missing(F.stringx("NotAvailable"))
              : F.num(major.getNaturalAbundance().doubleValue());
        }
        return F.Missing(F.stringx("NotAvailable"));
      } catch (Exception e) {
        return F.Missing(F.stringx("NotAvailable"));
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>ReactionBalancedQ({reactants}, {products})</code> — whether the two sides already have
   * the same element counts.
   */
  private static class ReactionBalancedQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      List<Map<String, Integer>> left = sideFormulas(ast.arg1());
      List<Map<String, Integer>> right = sideFormulas(ast.arg2());
      if (left == null || right == null) {
        return F.NIL;
      }
      Map<String, Integer> leftTotal = totalOf(left);
      Map<String, Integer> rightTotal = totalOf(right);
      return F.booleSymbol(leftTotal.equals(rightTotal));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>ReactionBalance({reactants}, {products})</code> — the smallest positive integer
   * coefficients which balance the reaction, as <code>{{a, b, ...}, {c, d, ...}}</code>.
   *
   * <p>
   * This is the nullspace of the element-count matrix. The search is a bounded brute force over
   * small coefficients, which is enough for the textbook reactions the function is used on and
   * avoids a rational-arithmetic dependency.
   */
  private static class ReactionBalance extends AbstractEvaluator {

    /** The largest stoichiometric coefficient this searches for. */
    private static final int MAX_COEFFICIENT = 12;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      List<Map<String, Integer>> left = sideFormulas(ast.arg1());
      List<Map<String, Integer>> right = sideFormulas(ast.arg2());
      if (left == null || right == null) {
        return F.NIL;
      }
      int n = left.size();
      int m = right.size();
      if (n == 0 || m == 0 || n + m > 6) {
        // beyond this the brute force is no longer honest; say so rather than hang
        return Errors.printMessage(ast.topHead(), "chembal", F.List(ast.arg1()), engine);
      }
      Set<String> elements = new LinkedHashSet<String>();
      for (Map<String, Integer> formula : left) {
        elements.addAll(formula.keySet());
      }
      for (Map<String, Integer> formula : right) {
        elements.addAll(formula.keySet());
      }

      int[] coefficients = new int[n + m];
      if (!search(coefficients, 0, left, right, elements)) {
        return Errors.printMessage(ast.topHead(), "chembal", F.List(ast.arg1()), engine);
      }
      IASTAppendable leftResult = F.ListAlloc(n);
      for (int i = 0; i < n; i++) {
        leftResult.append(F.ZZ(coefficients[i]));
      }
      IASTAppendable rightResult = F.ListAlloc(m);
      for (int i = 0; i < m; i++) {
        rightResult.append(F.ZZ(coefficients[n + i]));
      }
      return F.List(leftResult, rightResult);
    }

    /** Depth-first over coefficient vectors, smallest total first. */
    private static boolean search(int[] coefficients, int index, List<Map<String, Integer>> left,
        List<Map<String, Integer>> right, Set<String> elements) {
      if (index == coefficients.length) {
        return balances(coefficients, left, right, elements);
      }
      for (int value = 1; value <= MAX_COEFFICIENT; value++) {
        coefficients[index] = value;
        if (search(coefficients, index + 1, left, right, elements)) {
          return true;
        }
      }
      coefficients[index] = 0;
      return false;
    }

    private static boolean balances(int[] coefficients, List<Map<String, Integer>> left,
        List<Map<String, Integer>> right, Set<String> elements) {
      int n = left.size();
      for (String element : elements) {
        int leftCount = 0;
        for (int i = 0; i < n; i++) {
          Integer count = left.get(i).get(element);
          leftCount += coefficients[i] * (count == null ? 0 : count.intValue());
        }
        int rightCount = 0;
        for (int i = 0; i < right.size(); i++) {
          Integer count = right.get(i).get(element);
          rightCount += coefficients[n + i] * (count == null ? 0 : count.intValue());
        }
        if (leftCount != rightCount) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  // ---------------------------------------------------------------- helpers

  /** The element counts of every molecule on one side, or <code>null</code> if an entry is not one. */
  private static List<Map<String, Integer>> sideFormulas(IExpr side) {
    if (!side.isList()) {
      return null;
    }
    IAST list = (IAST) side;
    List<Map<String, Integer>> result = new ArrayList<Map<String, Integer>>(list.argSize());
    for (int i = 1; i < list.size(); i++) {
      IAtomContainer molecule = MoleculeFunctions.molecule(list.get(i));
      if (molecule == null) {
        return null;
      }
      result.add(elementCounts(molecule));
    }
    return result;
  }

  private static Map<String, Integer> elementCounts(IAtomContainer molecule) {
    Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
    IAtomContainer explicit = MoleculeFunctions.withExplicitHydrogens(molecule);
    for (IAtom atom : explicit.atoms()) {
      String symbol = atom.getSymbol();
      Integer count = counts.get(symbol);
      counts.put(symbol, Integer.valueOf(count == null ? 1 : count.intValue() + 1));
    }
    return counts;
  }

  private static Map<String, Integer> totalOf(List<Map<String, Integer>> side) {
    Map<String, Integer> total = new LinkedHashMap<String, Integer>();
    for (Map<String, Integer> formula : side) {
      for (Map.Entry<String, Integer> entry : formula.entrySet()) {
        Integer count = total.get(entry.getKey());
        total.put(entry.getKey(), Integer.valueOf(
            (count == null ? 0 : count.intValue()) + entry.getValue().intValue()));
      }
    }
    return total;
  }

  public static void initialize() {
    Initializer.init();
  }

  private ReactionFunctions() {}
}
