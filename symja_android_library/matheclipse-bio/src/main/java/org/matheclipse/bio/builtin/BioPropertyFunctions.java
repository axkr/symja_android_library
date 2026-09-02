package org.matheclipse.bio.builtin;

import java.util.Map;
import java.util.TreeMap;
import org.biojava.nbio.aaproperties.PeptideProperties;
import org.matheclipse.bio.expression.data.BioSequenceExpr;
import org.matheclipse.bio.expression.data.BioSequenceType;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Tier 3 of the <code>matheclipse-bio</code> module: peptide properties, backed by
 * <code>biojava-aa-prop</code>.
 *
 * <p>
 * <code>ProteinData</code> is entity-backed and exposes a far larger property list than anything
 * computable from the sequence alone. This implements the computable subset and answers
 * <code>Missing["NotAvailable"]</code> for everything else, rather than pretending to cover it.
 */
public class BioPropertyFunctions {

  private static class Initializer {

    private static void init() {
      S.ProteinData.setEvaluator(new ProteinData());
    }
  }

  /**
   * The property names computable from the sequence, in the order {@code "Properties"} lists them.
   */
  private static final String[] PROPERTIES = { //
      "Absorbance", //
      "AliphaticIndex", //
      "AminoAcidComposition", //
      "Aromaticity", //
      "AverageHydropathy", //
      "ExtinctionCoefficient", //
      "InstabilityIndex", //
      "IsoelectricPoint", //
      "Length", //
      "MolecularWeight", //
      "NetCharge", //
      "Sequence"};

  private static class ProteinData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BioSequenceExpr peptide = peptideArg(ast, engine);
      if (peptide == null) {
        return F.NIL;
      }
      if (ast.isAST1()) {
        // no property named: list the ones this implementation can answer
        return propertyNames();
      }
      if (!ast.arg2().isString()) {
        return F.NIL;
      }
      String property = ast.arg2().toString();
      String sequence = peptide.getSequenceAsString();

      if ("Properties".equals(property)) {
        return propertyNames();
      }
      if ("Sequence".equals(property)) {
        return F.stringx(sequence);
      }
      if ("Length".equals(property)) {
        return F.ZZ(sequence.length());
      }
      if (sequence.isEmpty()) {
        // every numeric property below divides by the length at some point
        return F.Missing(F.stringx("NotAvailable"));
      }
      try {
        if ("MolecularWeight".equals(property)) {
          return F.num(PeptideProperties.getMolecularWeight(sequence));
        }
        if ("IsoelectricPoint".equals(property)) {
          return F.num(PeptideProperties.getIsoelectricPoint(sequence));
        }
        if ("NetCharge".equals(property)) {
          return F.num(PeptideProperties.getNetCharge(sequence));
        }
        if ("ExtinctionCoefficient".equals(property)) {
          return F.num(PeptideProperties.getExtinctionCoefficient(sequence, true));
        }
        if ("Absorbance".equals(property)) {
          return F.num(PeptideProperties.getAbsorbance(sequence, true));
        }
        if ("InstabilityIndex".equals(property)) {
          return F.num(PeptideProperties.getInstabilityIndex(sequence));
        }
        if ("AliphaticIndex".equals(property)) {
          return F.num(PeptideProperties.getApliphaticIndex(sequence));
        }
        if ("AverageHydropathy".equals(property)) {
          return F.num(PeptideProperties.getAvgHydropathy(sequence));
        }
        if ("Aromaticity".equals(property)) {
          return F.num(PeptideProperties.getAromaticity(sequence));
        }
        if ("AminoAcidComposition".equals(property)) {
          return aminoAcidComposition(sequence);
        }
      } catch (RuntimeException e) {
        return Errors.printMessage(ast.topHead(), "bioseq", F.List(ast.arg1()), engine);
      }
      return F.Missing(F.stringx("NotAvailable"));
    }

    /**
     * The composition as a sorted list of <code>"letter" -&gt; fraction</code> rules.
     *
     * <p>
     * BioJava reports a fraction for every compound in the alphabet, including the gap and
     * ambiguity codes (<code>*</code>, <code>-</code>, <code>.</code>, <code>_</code>,
     * <code>B</code>, <code>J</code>, …). Only the residues actually present are of interest, so
     * the zero entries are dropped.
     */
    private static IExpr aminoAcidComposition(String sequence) {
      Map<String, Double> composition =
          new TreeMap<String, Double>(PeptideProperties.getAACompositionString(sequence));
      IASTAppendable result = F.ListAlloc(composition.size());
      for (Map.Entry<String, Double> entry : composition.entrySet()) {
        double fraction = entry.getValue().doubleValue();
        if (fraction != 0.0) {
          result.append(F.Rule(F.stringx(entry.getKey()), F.num(fraction)));
        }
      }
      return result;
    }

    private static IExpr propertyNames() {
      IASTAppendable result = F.ListAlloc(PROPERTIES.length);
      for (int i = 0; i < PROPERTIES.length; i++) {
        result.append(F.stringx(PROPERTIES[i]));
      }
      return result;
    }

    /** @return the first argument as a peptide sequence, or <code>null</code> */
    private static BioSequenceExpr peptideArg(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      BioSequenceExpr sequence = null;
      if (arg1 instanceof BioSequenceExpr) {
        sequence = (BioSequenceExpr) arg1;
      } else if (arg1.isString()) {
        try {
          sequence = BioSequenceExpr.newSequence(BioSequenceType.PEPTIDE, arg1.toString());
        } catch (org.biojava.nbio.core.exceptions.CompoundNotFoundException e) {
          Errors.printMessage(ast.topHead(), "bioseq", F.List(arg1), engine);
          return null;
        }
      }
      if (sequence == null) {
        return null;
      }
      if (sequence.getType() != BioSequenceType.PEPTIDE) {
        Errors.printMessage(ast.topHead(), "biopept", F.List(arg1), engine);
        return null;
      }
      return sequence;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private BioPropertyFunctions() {}
}
