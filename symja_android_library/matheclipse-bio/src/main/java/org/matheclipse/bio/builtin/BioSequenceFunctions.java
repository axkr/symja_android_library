package org.matheclipse.bio.builtin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.exceptions.TranslationException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.compound.AmbiguityRNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.IUPACParser.IUPACTable;
import org.biojava.nbio.core.sequence.template.Sequence;
import org.biojava.nbio.core.sequence.transcription.TranscriptionEngine;
import org.matheclipse.bio.convert.GeneticCodeTables;
import org.matheclipse.bio.expression.data.BioSequenceExpr;
import org.matheclipse.bio.expression.data.BioSequenceType;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Tier 1 of the <code>matheclipse-bio</code> module: construction and transformation of
 * biomolecular sequences, backed by <code>biojava-core</code>.
 */
public class BioSequenceFunctions {

  /** Default cap on generated sequence lists. */
  private static final int DEFAULT_MAX_ITEMS = 10000;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BioSequence.setEvaluator(new BioSequence());
      S.BioSequenceQ.setEvaluator(new BioSequenceQ());
      S.BioSequenceComplement.setEvaluator(new BioSequenceComplement());
      S.BioSequenceReverseComplement.setEvaluator(new BioSequenceReverseComplement());
      S.BioSequenceTranscribe.setEvaluator(new BioSequenceTranscribe());
      S.BioSequenceTranslate.setEvaluator(new BioSequenceTranslate());
      S.BioSequenceBackTranslateList.setEvaluator(new BioSequenceBackTranslateList());
      S.BioSequenceInstances.setEvaluator(new BioSequenceInstances());
      S.BioSequenceModify.setEvaluator(new BioSequenceModify());
    }
  }

  /** The IUPAC ambiguity letters, mapped to the unambiguous letters each one stands for. */
  private static final String[][] DNA_AMBIGUITY = { //
      {"A", "A"}, {"C", "C"}, {"G", "G"}, {"T", "T"}, //
      {"R", "AG"}, {"Y", "CT"}, {"S", "CG"}, {"W", "AT"}, {"K", "GT"}, {"M", "AC"}, //
      {"B", "CGT"}, {"D", "AGT"}, {"H", "ACT"}, {"V", "ACG"}, {"N", "ACGT"}};

  private static final String[][] RNA_AMBIGUITY = { //
      {"A", "A"}, {"C", "C"}, {"G", "G"}, {"U", "U"}, //
      {"R", "AG"}, {"Y", "CU"}, {"S", "CG"}, {"W", "AU"}, {"K", "GU"}, {"M", "AC"}, //
      {"B", "CGU"}, {"D", "AGU"}, {"H", "ACU"}, {"V", "ACG"}, {"N", "ACGU"}};

  /**
   * <code>BioSequence("letters")</code> infers the type;
   * <code>BioSequence("type", "letters")</code> builds the named type.
   */
  private static class BioSequence extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        IExpr arg1 = ast.arg1();
        if (arg1 instanceof BioSequenceExpr) {
          return arg1;
        }
        if (arg1.isString()) {
          BioSequenceExpr result = BioSequenceExpr.inferSequence(arg1.toString());
          if (result != null) {
            return result;
          }
          return Errors.printMessage(ast.topHead(), "bioseq", F.List(arg1), engine);
        }
        return F.NIL;
      }
      if (ast.isAST2() && ast.arg1().isString() && ast.arg2().isString()) {
        String typeStr = ast.arg1().toString();
        String sequenceStr = ast.arg2().toString();
        BioSequenceType type = BioSequenceType.of(typeStr);
        if (type == null) {
          return Errors.printMessage(ast.topHead(), "biotype", F.List(ast.arg1()), engine);
        }
        try {
          return BioSequenceExpr.newSequence(type, sequenceStr);
        } catch (CompoundNotFoundException e) {
          return Errors.printMessage(ast.topHead(), "bioseq", F.List(ast.arg2()), engine);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class BioSequenceQ extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.booleSymbol(ast.arg1() instanceof BioSequenceExpr);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>BioSequenceComplement(seq)</code> — the base-paired complement, same 5'->3' order. */
  private static class BioSequenceComplement extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BioSequenceExpr seq = nucleotideArg(ast, 1, engine);
      if (seq == null) {
        return F.NIL;
      }
      String complement = complementOf(seq.getSequenceAsString(), seq.getType());
      return rebuild(ast, seq.getType(), complement, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>BioSequenceReverseComplement(seq)</code> — the complement, read 3'->5'. */
  private static class BioSequenceReverseComplement extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BioSequenceExpr seq = nucleotideArg(ast, 1, engine);
      if (seq == null) {
        return F.NIL;
      }
      String complement = complementOf(seq.getSequenceAsString(), seq.getType());
      String reversed = new StringBuilder(complement).reverse().toString();
      return rebuild(ast, seq.getType(), reversed, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>BioSequenceTranscribe(seq)</code> — DNA to RNA and back. */
  private static class BioSequenceTranscribe extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BioSequenceExpr seq = nucleotideArg(ast, 1, engine);
      if (seq == null) {
        return F.NIL;
      }
      BioSequenceType type = seq.getType();
      String letters = seq.getSequenceAsString();
      if (type.toLinear() == BioSequenceType.RNA) {
        // reverse transcription: RNA -> DNA
        BioSequenceType target =
            type.isCircular() ? BioSequenceType.CIRCULAR_DNA : BioSequenceType.DNA;
        return rebuild(ast, target, letters.replace('U', 'T').replace('u', 't'), engine);
      }
      BioSequenceType target =
          type.isCircular() ? BioSequenceType.CIRCULAR_RNA : BioSequenceType.RNA;
      return rebuild(ast, target, letters.replace('T', 'U').replace('t', 'u'), engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>BioSequenceTranslate(seq)</code> and <code>BioSequenceTranslate(seq, gtt)</code> — codons
   * to amino acids, using the NCBI translation table <code>gtt</code> (number or name).
   */
  private static class BioSequenceTranslate extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BioSequenceExpr seq = nucleotideArg(ast, 1, engine);
      if (seq == null) {
        return F.NIL;
      }
      IUPACTable table = GeneticCodeTables.standard();
      if (ast.isAST2()) {
        table = GeneticCodeTables.resolve(ast.arg2());
        if (table == null) {
          return Errors.printMessage(ast.topHead(), "biogtt", F.List(ast.arg2()), engine);
        }
      }
      String letters = seq.getSequenceAsString();
      // BioJava translates from DNA; transcribe RNA back to DNA first
      if (seq.getType().toLinear() == BioSequenceType.RNA) {
        letters = letters.replace('U', 'T').replace('u', 't');
      }
      try {
        TranscriptionEngine transcriptionEngine =
            new TranscriptionEngine.Builder().table(table).initMet(false).trimStop(false).build();
        DNASequence dna = new DNASequence(letters, AmbiguityDNACompoundSet.getDNACompoundSet());
        Sequence<org.biojava.nbio.core.sequence.compound.AminoAcidCompound> protein =
            transcriptionEngine.translate(dna);
        return BioSequenceExpr.newPeptideSequence(protein.getSequenceAsString());
      } catch (CompoundNotFoundException e) {
        return Errors.printMessage(ast.topHead(), "bioseq", F.List(ast.arg1()), engine);
      } catch (TranslationException e) {
        // BioJava will not translate a sequence it cannot split into codons, an empty one above
        // all. Mathematica answers BioSequenceTranslate("") for that rather than reporting it.
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>BioSequenceBackTranslateList(peptide)</code> — every RNA sequence which translates to the
   * given peptide, capped by the <code>MaxItems</code> option.
   */
  private static class BioSequenceBackTranslateList extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      BioSequenceExpr seq = toBioSequence(arg1);
      if (seq == null) {
        return F.NIL;
      }
      if (seq.getType() != BioSequenceType.PEPTIDE) {
        return Errors.printMessage(ast.topHead(), "biopept", F.List(arg1), engine);
      }
      IUPACTable table = GeneticCodeTables.standard();
      if (ast.argSize() >= 2 && !ast.arg2().isRule()) {
        table = GeneticCodeTables.resolve(ast.arg2());
        if (table == null) {
          return Errors.printMessage(ast.topHead(), "biogtt", F.List(ast.arg2()), engine);
        }
      }

      int maxItems = maxItemsOption(ast, 2, engine);
      if (maxItems < 0) {
        return F.NIL;
      }

      Map<String, List<String>> codonMap = GeneticCodeTables.aminoAcidToCodons(table);
      List<String> letters = seq.toLetters();

      // total number of back-translations; bail out before building anything huge
      long total = 1;
      for (String letter : letters) {
        List<String> codons = codonMap.get(letter);
        if (codons == null || codons.isEmpty()) {
          return Errors.printMessage(ast.topHead(), "bioaa", F.List(F.stringx(letter)), engine);
        }
        total = total * codons.size();
        if (total > maxItems) {
          total = maxItems + 1L;
          break;
        }
      }
      if (total > maxItems) {
        return Errors.printMessage(ast.topHead(), "biomax",
            F.List(F.ZZ(maxItems), F.stringx("MaxItems")), engine);
      }

      List<StringBuilder> combinations = new ArrayList<StringBuilder>();
      combinations.add(new StringBuilder());
      for (String letter : letters) {
        List<String> codons = codonMap.get(letter);
        List<StringBuilder> next =
            new ArrayList<StringBuilder>(combinations.size() * codons.size());
        for (StringBuilder prefix : combinations) {
          for (String codon : codons) {
            next.add(new StringBuilder(prefix).append(codon));
          }
        }
        combinations = next;
      }

      IASTAppendable result = F.ListAlloc(combinations.size());
      for (StringBuilder combination : combinations) {
        try {
          result.append(BioSequenceExpr.newSequence(BioSequenceType.RNA, combination.toString()));
        } catch (CompoundNotFoundException e) {
          return Errors.printMessage(ast.topHead(), "bioseq", F.List(arg1), engine);
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, F.list(F.Rule(S.MaxItems, F.ZZ(DEFAULT_MAX_ITEMS))));
    }
  }

  /**
   * <code>BioSequenceInstances(seq)</code> — every unambiguous sequence the (possibly degenerate)
   * argument stands for.
   */
  private static class BioSequenceInstances extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      BioSequenceExpr seq = nucleotideArg(ast, 1, engine);
      if (seq == null) {
        return F.NIL;
      }
      int maxItems = maxItemsOption(ast, 1, engine);
      if (maxItems < 0) {
        return F.NIL;
      }
      String[][] ambiguity =
          seq.getType().toLinear() == BioSequenceType.RNA ? RNA_AMBIGUITY : DNA_AMBIGUITY;
      List<String> letters = seq.toLetters();

      long total = 1;
      for (String letter : letters) {
        String expansion = expand(ambiguity, letter);
        if (expansion == null) {
          return Errors.printMessage(ast.topHead(), "bioaa", F.List(F.stringx(letter)), engine);
        }
        total = total * expansion.length();
        if (total > maxItems) {
          return Errors.printMessage(ast.topHead(), "biomax",
              F.List(F.ZZ(maxItems), F.stringx("MaxItems")), engine);
        }
      }

      List<StringBuilder> combinations = new ArrayList<StringBuilder>();
      combinations.add(new StringBuilder());
      for (String letter : letters) {
        String expansion = expand(ambiguity, letter);
        List<StringBuilder> next =
            new ArrayList<StringBuilder>(combinations.size() * expansion.length());
        for (StringBuilder prefix : combinations) {
          for (int i = 0; i < expansion.length(); i++) {
            next.add(new StringBuilder(prefix).append(expansion.charAt(i)));
          }
        }
        combinations = next;
      }

      IASTAppendable result = F.ListAlloc(combinations.size());
      for (StringBuilder combination : combinations) {
        try {
          result.append(BioSequenceExpr.newSequence(seq.getType(), combination.toString()));
        } catch (CompoundNotFoundException e) {
          return Errors.printMessage(ast.topHead(), "bioseq", F.List(ast.arg1()), engine);
        }
      }
      return result;
    }

    private static String expand(String[][] ambiguity, String letter) {
      for (int i = 0; i < ambiguity.length; i++) {
        if (ambiguity[i][0].equalsIgnoreCase(letter)) {
          return ambiguity[i][1];
        }
      }
      return null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, F.list(F.Rule(S.MaxItems, F.ZZ(DEFAULT_MAX_ITEMS))));
    }
  }

  /** <code>BioSequenceModify("op", seq)</code> — structural edits of a sequence. */
  private static class BioSequenceModify extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      String operation = ast.arg1().toString();
      BioSequenceExpr seq = toBioSequence(ast.arg2());
      if (seq == null) {
        return F.NIL;
      }

      if ("MakeCircular".equals(operation)) {
        BioSequenceType circular = seq.getType().toCircular();
        if (circular == null) {
          return Errors.printMessage(ast.topHead(), "biocirc", F.List(ast.arg2()), engine);
        }
        return seq.withType(circular);
      }
      if ("MakeLinear".equals(operation)) {
        return seq.withType(seq.getType().toLinear());
      }
      if ("DropToStartCodon".equals(operation)) {
        return dropToStartCodon(ast, seq, engine);
      }
      if ("AddBond".equals(operation)) {
        if (ast.argSize() < 3) {
          return F.NIL;
        }
        return seq.withBonds(addBond(seq, ast.arg3()));
      }
      if ("DeleteBond".equals(operation)) {
        if (ast.argSize() < 3) {
          return F.NIL;
        }
        return seq.withBonds(deleteBond(seq, ast.arg3()));
      }
      return Errors.printMessage(ast.topHead(), "bioop", F.List(ast.arg1()), engine);
    }

    private static IExpr dropToStartCodon(IAST ast, BioSequenceExpr seq, EvalEngine engine) {
      if (!seq.getType().isNucleotide()) {
        return Errors.printMessage(ast.topHead(), "bionuc", F.List(ast.arg2()), engine);
      }
      String letters = seq.getSequenceAsString();
      String start = seq.getType().toLinear() == BioSequenceType.RNA ? "AUG" : "ATG";
      int index = letters.toUpperCase().indexOf(start);
      if (index < 0) {
        return Errors.printMessage(ast.topHead(), "biostart", F.List(ast.arg2()), engine);
      }
      try {
        return BioSequenceExpr.newSequence(seq.getType(), letters.substring(index));
      } catch (CompoundNotFoundException e) {
        return F.NIL;
      }
    }

    private static IAST addBond(BioSequenceExpr seq, IExpr bond) {
      IExpr existing = seq.getBonds();
      IASTAppendable bonds =
          existing.isList() ? ((IAST) existing).copyAppendable() : F.ListAlloc(1);
      if (!bonds.contains(bond)) {
        bonds.append(bond);
      }
      return bonds;
    }

    private static IAST deleteBond(BioSequenceExpr seq, IExpr bond) {
      IExpr existing = seq.getBonds();
      if (!existing.isList()) {
        return F.CEmptyList;
      }
      IAST list = (IAST) existing;
      IASTAppendable bonds = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        if (!list.get(i).equals(bond)) {
          bonds.append(list.get(i));
        }
      }
      return bonds;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  // ---------------------------------------------------------------- helpers

  /** The argument at <code>position</code> as a nucleotide sequence, or <code>null</code>. */
  private static BioSequenceExpr nucleotideArg(IAST ast, int position, EvalEngine engine) {
    BioSequenceExpr seq = toBioSequence(ast.get(position));
    if (seq == null) {
      return null;
    }
    if (!seq.getType().isNucleotide()) {
      Errors.printMessage(ast.topHead(), "bionuc", F.List(ast.get(position)), engine);
      return null;
    }
    return seq;
  }

  /**
   * Accept either a {@link BioSequenceExpr} or a plain string, so <code>BioSequenceComplement(
   * "ACGT")</code> works.
   */
  private static BioSequenceExpr toBioSequence(IExpr expr) {
    if (expr instanceof BioSequenceExpr) {
      return (BioSequenceExpr) expr;
    }
    if (expr.isString()) {
      return BioSequenceExpr.inferSequence(expr.toString());
    }
    return null;
  }

  private static IExpr rebuild(IAST ast, BioSequenceType type, String letters, EvalEngine engine) {
    try {
      return BioSequenceExpr.newSequence(type, letters);
    } catch (CompoundNotFoundException e) {
      return Errors.printMessage(ast.topHead(), "bioseq", F.List(ast.arg1()), engine);
    }
  }

  /**
   * @return the <code>MaxItems</code> option as a non-negative int, or <code>-1</code> when it is
   *         not a usable value
   */
  private static int maxItemsOption(IAST ast, int lastPositionalArg, EvalEngine engine) {
    if (ast.argSize() <= lastPositionalArg) {
      return DEFAULT_MAX_ITEMS;
    }
    final OptionArgs options = new OptionArgs(ast.topHead(), ast, lastPositionalArg + 1, engine);
    IExpr option = options.getOption(S.MaxItems);
    if (!option.isPresent()) {
      return DEFAULT_MAX_ITEMS;
    }
    if (option == S.Infinity || option == S.All) {
      return Integer.MAX_VALUE;
    }
    int value = option.toIntDefault();
    return value == Integer.MIN_VALUE || value < 0 ? -1 : value;
  }

  /** Base-pair complement, preserving the alphabet of <code>type</code>. */
  private static String complementOf(String letters, BioSequenceType type) {
    boolean rna = type.toLinear() == BioSequenceType.RNA;
    StringBuilder buf = new StringBuilder(letters.length());
    for (int i = 0; i < letters.length(); i++) {
      buf.append(complementOf(letters.charAt(i), rna));
    }
    return buf.toString();
  }

  private static char complementOf(char ch, boolean rna) {
    switch (Character.toUpperCase(ch)) {
      case 'A':
        return rna ? 'U' : 'T';
      case 'T':
      case 'U':
        return 'A';
      case 'C':
        return 'G';
      case 'G':
        return 'C';
      // IUPAC ambiguity letters complement pairwise
      case 'R':
        return 'Y';
      case 'Y':
        return 'R';
      case 'S':
        return 'S';
      case 'W':
        return 'W';
      case 'K':
        return 'M';
      case 'M':
        return 'K';
      case 'B':
        return 'V';
      case 'V':
        return 'B';
      case 'D':
        return 'H';
      case 'H':
        return 'D';
      case 'N':
        return 'N';
      default:
        return ch;
    }
  }

  /** Unused today, kept because BioJava's own complement view is the reference implementation. */
  static Sequence<NucleotideCompound> biojavaComplement(BioSequenceExpr seq)
      throws CompoundNotFoundException {
    if (seq.getType().toLinear() == BioSequenceType.RNA) {
      return new RNASequence(seq.getSequenceAsString(),
          AmbiguityRNACompoundSet.getRNACompoundSet());
    }
    return new DNASequence(seq.getSequenceAsString(), AmbiguityDNACompoundSet.getDNACompoundSet())
        .getComplement();
  }

  public static void initialize() {
    Initializer.init();
  }

  private BioSequenceFunctions() {}
}
