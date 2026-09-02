package org.matheclipse.chem.builtin;

import org.matheclipse.chem.expression.data.MoleculeExpr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.smarts.SmartsPattern;

/**
 * Tier B of the <code>matheclipse-chem</code> module: substructure search and structural
 * comparison, backed by <code>cdk-smarts</code> and <code>cdk-isomorphism</code>.
 *
 * <p>
 * A query is either a SMARTS string wrapped in <code>MoleculePattern</code>, or a molecule used as
 * a substructure query.
 */
public class SubstructureFunctions {

  private static class Initializer {

    private static void init() {
      S.MoleculePattern.setEvaluator(new MoleculePattern());
      S.MoleculeContainsQ.setEvaluator(new MoleculeContainsQ());
      S.MoleculeFreeQ.setEvaluator(new MoleculeFreeQ());
      S.MoleculeMatchQ.setEvaluator(new MoleculeMatchQ());
      S.MoleculeSubstructureCount.setEvaluator(new MoleculeSubstructureCount());
      S.FindMoleculeSubstructure.setEvaluator(new FindMoleculeSubstructure());
      S.MoleculeEquivalentQ.setEvaluator(new MoleculeEquivalentQ());
    }
  }

  /** <code>MoleculePattern("SMARTS")</code> — held as-is and interpreted by the query functions. */
  private static class MoleculePattern extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isString()) {
        // validate eagerly so a bad SMARTS is reported where it is written
        if (compile(ast.arg1().toString()) == null) {
          return Errors.printMessage(ast.topHead(), "chemsmarts", F.List(ast.arg1()), engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class MoleculeContainsQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Integer count = countMatches(ast, engine);
      if (count == null) {
        return F.NIL;
      }
      return F.booleSymbol(count.intValue() > 0);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class MoleculeFreeQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Integer count = countMatches(ast, engine);
      if (count == null) {
        return F.NIL;
      }
      return F.booleSymbol(count.intValue() == 0);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /** <code>MoleculeMatchQ(mol, pattern)</code> — same test as {@code MoleculeContainsQ}. */
  private static class MoleculeMatchQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Integer count = countMatches(ast, engine);
      if (count == null) {
        return F.NIL;
      }
      return F.booleSymbol(count.intValue() > 0);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /** The number of distinct substructure matches, counting each set of atoms once. */
  private static class MoleculeSubstructureCount extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Integer count = countMatches(ast, engine);
      if (count == null) {
        return F.NIL;
      }
      return F.ZZ(count.intValue());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /** The matching atom index sets, 1-based. */
  private static class FindMoleculeSubstructure extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer molecule = MoleculeFunctions.molecule(ast.arg1());
      if (molecule == null) {
        return F.NIL;
      }
      Pattern pattern = pattern(ast, ast.arg2(), engine);
      if (pattern == null) {
        return F.NIL;
      }
      Mappings mappings = pattern.matchAll(molecule).uniqueAtoms();
      IASTAppendable result = F.ListAlloc(4);
      for (int[] mapping : mappings) {
        IASTAppendable indices = F.ListAlloc(mapping.length);
        for (int i = 0; i < mapping.length; i++) {
          indices.append(F.ZZ(mapping[i] + 1));
        }
        result.append(indices);
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>MoleculeEquivalentQ(a, b)</code> — the same molecule, compared by canonical SMILES.
   */
  private static class MoleculeEquivalentQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAtomContainer first = MoleculeFunctions.molecule(ast.arg1());
      IAtomContainer second = MoleculeFunctions.molecule(ast.arg2());
      if (first == null || second == null) {
        return F.NIL;
      }
      return F.booleSymbol(MoleculeExpr.newInstance(first) //
          .equals(MoleculeExpr.newInstance(second)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  // ---------------------------------------------------------------- helpers

  /** @return the number of unique-atom matches, or <code>null</code> when an argument is unusable */
  private static Integer countMatches(IAST ast, EvalEngine engine) {
    IAtomContainer molecule = MoleculeFunctions.molecule(ast.arg1());
    if (molecule == null) {
      return null;
    }
    Pattern pattern = pattern(ast, ast.arg2(), engine);
    if (pattern == null) {
      return null;
    }
    return Integer.valueOf(pattern.matchAll(molecule).uniqueAtoms().count());
  }

  /**
   * A query is <code>MoleculePattern("SMARTS")</code>, a bare SMARTS string, or a molecule used as
   * a substructure query.
   */
  static Pattern pattern(IAST ast, IExpr query, EvalEngine engine) {
    if (query.isAST(S.MoleculePattern, 2) && ((IAST) query).arg1().isString()) {
      String smarts = ((IAST) query).arg1().toString();
      Pattern pattern = compile(smarts);
      if (pattern == null) {
        Errors.printMessage(ast.topHead(), "chemsmarts", F.List(F.stringx(smarts)), engine);
      }
      return pattern;
    }
    if (query instanceof MoleculeExpr) {
      IAtomContainer substructure = ((MoleculeExpr) query).toData();
      return org.openscience.cdk.isomorphism.VentoFoggia.findSubstructure(substructure);
    }
    if (query.isString()) {
      // a bare string is read as SMARTS, which is a superset of SMILES
      String smarts = query.toString();
      Pattern pattern = compile(smarts);
      if (pattern == null) {
        Errors.printMessage(ast.topHead(), "chemsmarts", F.List(query), engine);
      }
      return pattern;
    }
    return null;
  }

  /** @return the compiled SMARTS pattern, or <code>null</code> when it does not compile */
  private static Pattern compile(String smarts) {
    try {
      return SmartsPattern.create(smarts);
    } catch (RuntimeException e) {
      return null;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private SubstructureFunctions() {}
}
