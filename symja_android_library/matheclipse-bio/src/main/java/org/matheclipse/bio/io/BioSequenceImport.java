package org.matheclipse.bio.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.biojava.nbio.core.sequence.io.GenbankReaderHelper;
import org.matheclipse.bio.expression.data.BioSequenceExpr;
import org.matheclipse.bio.expression.data.BioSequenceType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * FASTA and GenBank readers and writers, so <code>Import</code> and <code>Export</code> can handle
 * biomolecular sequence files. This is the only place the <code>matheclipse-io</code> module reaches
 * into <code>matheclipse-bio</code> for file formats.
 */
public class BioSequenceImport {

  /**
   * Read a FASTA file.
   *
   * @param file the file to read
   * @param peptide read amino-acid sequences rather than DNA
   * @return a list of <code>accession -&gt; BioSequence</code> rules, or {@link F#NIL} on failure
   */
  public static IExpr importFASTA(File file, boolean peptide) {
    try {
      Map<String, ? extends org.biojava.nbio.core.sequence.template.Sequence<?>> sequences;
      if (peptide) {
        sequences = new LinkedHashMap<String, ProteinSequence>(
            FastaReaderHelper.readFastaProteinSequence(file));
      } else {
        sequences =
            new LinkedHashMap<String, DNASequence>(FastaReaderHelper.readFastaDNASequence(file));
      }
      return toRuleList(sequences, peptide ? BioSequenceType.PEPTIDE : BioSequenceType.DNA);
    } catch (IOException e) {
      return F.NIL;
    }
  }

  /**
   * Read a GenBank file.
   *
   * @return a list of <code>accession -&gt; BioSequence</code> rules, or {@link F#NIL} on failure
   */
  public static IExpr importGenBank(File file, boolean peptide) {
    try {
      Map<String, ? extends org.biojava.nbio.core.sequence.template.Sequence<?>> sequences;
      if (peptide) {
        sequences = new LinkedHashMap<String, ProteinSequence>(
            GenbankReaderHelper.readGenbankProteinSequence(file));
      } else {
        sequences =
            new LinkedHashMap<String, DNASequence>(GenbankReaderHelper.readGenbankDNASequence(file));
      }
      return toRuleList(sequences, peptide ? BioSequenceType.PEPTIDE : BioSequenceType.DNA);
    } catch (Exception e) {
      return F.NIL;
    }
  }

  /**
   * Write one or more sequences as FASTA.
   *
   * @param expr a {@link BioSequenceExpr}, or a list of them
   * @return <code>true</code> when everything was written
   */
  public static boolean exportFASTA(OutputStream out, IExpr expr) {
    try {
      if (expr instanceof BioSequenceExpr) {
        return writeOne(out, (BioSequenceExpr) expr);
      }
      if (expr.isList()) {
        IAST list = (IAST) expr;
        for (int i = 1; i < list.size(); i++) {
          if (!(list.get(i) instanceof BioSequenceExpr)) {
            return false;
          }
          if (!writeOne(out, (BioSequenceExpr) list.get(i))) {
            return false;
          }
        }
        return true;
      }
      return false;
    } catch (RuntimeException e) {
      return false;
    }
  }

  private static boolean writeOne(OutputStream out, BioSequenceExpr sequence) {
    try {
      if (sequence.getType() == BioSequenceType.PEPTIDE) {
        FastaWriterHelper.writeProteinSequence(out,
            java.util.Collections.singletonList(
                new ProteinSequence(sequence.getSequenceAsString())));
      } else {
        FastaWriterHelper.writeNucleotideSequence(out,
            java.util.Collections.singletonList(new DNASequence(sequence.getSequenceAsString())));
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private static IExpr toRuleList(
      Map<String, ? extends org.biojava.nbio.core.sequence.template.Sequence<?>> sequences,
      BioSequenceType type) {
    IASTAppendable result = F.ListAlloc(sequences.size());
    for (Map.Entry<String, ? extends org.biojava.nbio.core.sequence.template.Sequence<?>> entry : sequences
        .entrySet()) {
      result.append(F.Rule(F.stringx(entry.getKey()),
          BioSequenceExpr.newInstance(entry.getValue(), type)));
    }
    return result;
  }

  private BioSequenceImport() {}
}
