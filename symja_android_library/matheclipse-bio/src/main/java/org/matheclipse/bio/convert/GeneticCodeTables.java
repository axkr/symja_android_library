package org.matheclipse.bio.convert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.compound.RNACompoundSet;
import org.biojava.nbio.core.sequence.io.IUPACParser;
import org.biojava.nbio.core.sequence.io.IUPACParser.IUPACTable;
import org.biojava.nbio.core.sequence.transcription.Table;
import org.biojava.nbio.core.sequence.transcription.Table.Codon;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Resolves genetic-code specifications onto BioJava's {@link IUPACTable}s, and inverts a table for
 * back-translation.
 */
public class GeneticCodeTables {

  /** The NCBI "Standard" translation table, id 1. */
  public static final int STANDARD_TABLE_ID = 1;

  /**
   * Genetic-code names which do not match BioJava's, mapped onto the BioJava name. BioJava ships
   * table 1 as <code>UNIVERSAL</code>; NCBI calls it <code>Standard</code>. Everything else lines
   * up once case and underscores are ignored, see {@link #normalize(String)}.
   */
  private static final String[][] NAME_ALIASES = { //
      {"STANDARD", "UNIVERSAL"}, //
      {"STANDARDCODE", "UNIVERSAL"}, //
      {"EUBACTERIAL", "BACTERIAL"}, //
      {"BACTERIALANDPLANTPLASTID", "BACTERIAL"}, //
      {"MOLDPROTOZOANANDCOELENTERATEMITOCHONDRIAL", "MOLD_MITOCHONDRIAL"}};

  /**
   * Resolve a genetic-code specification: an NCBI table number, or a table name such as
   * <code>"Standard"</code> or <code>"VertebrateMitochondrial"</code>.
   *
   * @return the table, or <code>null</code> if <code>spec</code> matches none
   */
  public static IUPACTable resolve(IExpr spec) {
    IUPACParser parser = IUPACParser.getInstance();
    if (spec.isInteger()) {
      int id = spec.toIntDefault();
      if (id != Integer.MIN_VALUE) {
        for (IUPACTable table : parser.getTables()) {
          if (table.getId() != null && table.getId().intValue() == id) {
            return table;
          }
        }
      }
      return null;
    }
    if (spec.isString()) {
      String name = normalize(spec.toString());
      for (int i = 0; i < NAME_ALIASES.length; i++) {
        if (NAME_ALIASES[i][0].equals(name)) {
          name = normalize(NAME_ALIASES[i][1]);
          break;
        }
      }
      for (IUPACTable table : parser.getTables()) {
        if (table.getName() != null && normalize(table.getName()).equals(name)) {
          return table;
        }
      }
    }
    return null;
  }

  /**
   * BioJava's table names use underscores and upper case (<code>"VERTEBRATE_MITOCHONDRIAL"</code>),
   * WMA uses camel case (<code>"VertebrateMitochondrial"</code>). Strip case and non-alphanumeric
   * characters so both spellings resolve to the same key.
   */
  private static String normalize(String s) {
    StringBuilder buf = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (Character.isLetterOrDigit(ch)) {
        buf.append(Character.toUpperCase(ch));
      }
    }
    return buf.toString();
  }

  public static IUPACTable standard() {
    return IUPACParser.getInstance().getTable(STANDARD_TABLE_ID);
  }

  /**
   * Invert a translation table: amino-acid letter to the RNA codons which encode it.
   *
   * <p>
   * Stop codons are collected under <code>"*"</code>. The returned map preserves BioJava's codon
   * order so back-translation output is deterministic.
   */
  public static Map<String, List<String>> aminoAcidToCodons(Table table) {
    Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
    for (Codon codon : table.getCodons(RNACompoundSet.getRNACompoundSet(),
        AminoAcidCompoundSet.getAminoAcidCompoundSet())) {
      String aminoAcid = codon.isStop() ? "*" : codon.getAminoAcid().getShortName();
      List<String> codons = result.get(aminoAcid);
      if (codons == null) {
        codons = new ArrayList<String>();
        result.put(aminoAcid, codons);
      }
      codons.add(codon.getTriplet().toString());
    }
    return result;
  }

  private GeneticCodeTables() {}
}
