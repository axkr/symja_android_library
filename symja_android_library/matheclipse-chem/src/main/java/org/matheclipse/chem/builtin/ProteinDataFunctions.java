package org.matheclipse.chem.builtin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.matheclipse.core.data.Entities;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>ProteinData</code> over a table of human actin-binding proteins taken from UniProtKB.
 *
 * <p>
 * The table is the resource <code>proteins.tsv.gz</code>, written by
 * <code>tools/gen_protein_data.py</code>: every reviewed human UniProtKB entry annotated with the
 * Gene Ontology term "actin binding" or one of its descendants, named by its gene symbol, with its
 * canonical sequence and its GO annotations.
 *
 * <p>
 * The classes are the GO terms, each named by splitting the term name at every character that is
 * neither a letter nor a digit and joining the capitalized parts: "actin filament binding" is the
 * class <code>"ActinFilamentBinding"</code>. A class holds the proteins annotated directly with
 * its term; <code>ProteinData(p, "MolecularFunctions")</code> lists the class names of a protein's
 * function terms and <code>ProteinData(p, "MolecularFunctions", "Name")</code> the term names at
 * the same positions. Entities, classes and the annotations of a protein are listed in canonical
 * order.
 */
public class ProteinDataFunctions {

  private static final String PROTEIN = "Protein";

  private static final String PROTEIN_TABLE = "/org/matheclipse/chem/data/proteins.tsv.gz";

  /** The properties a protein answers for, in the order <code>"Properties"</code> lists them. */
  private static final String[] PROPERTIES = {"BiologicalProcesses", "CellularComponents",
      "MolecularFunctions", "Name", "Sequence", "SequenceLength", "StandardName"};

  private static final Comparator<String> CANONICAL =
      (a, b) -> F.stringx(a).compareTo(F.stringx(b));

  private static class Initializer {

    private static void init() {
      S.ProteinData.setEvaluator(new ProteinData());
      Entities.register(PROTEIN, S.ProteinData);
    }
  }

  /** A Gene Ontology term of the table. */
  private static final class Term {
    final String className;
    final String name;

    Term(String className, String name) {
      this.className = className;
      this.name = name;
    }
  }

  /** One protein of the table. */
  private static final class Protein {
    final String symbol;
    final String name;
    final String sequence;
    final Term[] functions;
    final Term[] processes;
    final Term[] components;

    Protein(String[] row, List<Term> terms) {
      symbol = row[1];
      name = row[2];
      functions = terms(row[4], terms);
      processes = terms(row[5], terms);
      components = terms(row[6], terms);
      sequence = row[7];
    }

    private static Term[] terms(String indexes, List<Term> terms) {
      if (indexes.isEmpty()) {
        return new Term[0];
      }
      String[] parts = indexes.split(",");
      Term[] result = new Term[parts.length];
      for (int i = 0; i < parts.length; i++) {
        result[i] = terms.get(Integer.parseInt(parts[i]));
      }
      Arrays.sort(result, (a, b) -> CANONICAL.compare(a.className, b.className));
      return result;
    }

    boolean annotatedWith(String className) {
      return contains(functions, className) || contains(processes, className)
          || contains(components, className);
    }

    private static boolean contains(Term[] terms, String className) {
      for (Term term : terms) {
        if (term.className.equals(className)) {
          return true;
        }
      }
      return false;
    }
  }

  /** The table, read on first use. */
  private static final class Table {
    /** The proteins by symbol, in canonical order. */
    final Map<String, Protein> proteins;
    /** The class names in canonical order, each with its members in canonical order. */
    final Map<String, List<String>> classes;

    Table(Map<String, Protein> proteins, List<Term> terms) {
      List<String> symbols = new ArrayList<String>(proteins.keySet());
      symbols.sort(CANONICAL);
      this.proteins = new LinkedHashMap<String, Protein>();
      for (String symbol : symbols) {
        this.proteins.put(symbol, proteins.get(symbol));
      }
      List<String> classNames = new ArrayList<String>();
      for (Term term : terms) {
        if (!classNames.contains(term.className)) {
          classNames.add(term.className);
        }
      }
      classNames.sort(CANONICAL);
      this.classes = new LinkedHashMap<String, List<String>>();
      for (String className : classNames) {
        List<String> members = new ArrayList<String>();
        for (Protein protein : this.proteins.values()) {
          if (protein.annotatedWith(className)) {
            members.add(protein.symbol);
          }
        }
        this.classes.put(className, members);
      }
    }
  }

  private static volatile Table table;

  private static Table table() {
    Table local = table;
    if (local == null) {
      synchronized (ProteinDataFunctions.class) {
        local = table;
        if (local == null) {
          local = readTable();
          table = local;
        }
      }
    }
    return local;
  }

  private static Table readTable() {
    List<Term> terms = new ArrayList<Term>();
    Map<String, Protein> proteins = new LinkedHashMap<String, Protein>();
    try (InputStream raw = ProteinDataFunctions.class.getResourceAsStream(PROTEIN_TABLE)) {
      if (raw != null) {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(new GZIPInputStream(raw), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
          String[] row = line.split("\t", -1);
          if (row[0].equals("T")) {
            terms.add(new Term(row[3], row[4]));
          } else if (row[0].equals("P")) {
            Protein protein = new Protein(row, terms);
            proteins.put(protein.symbol, protein);
          }
        }
      }
    } catch (IOException | RuntimeException e) {
      terms = Collections.emptyList();
      proteins = Collections.emptyMap();
    }
    return new Table(proteins, terms);
  }

  /** The protein an argument names, or <code>null</code>. */
  private static Protein proteinOf(IExpr expr) {
    IExpr name = Entities.nameOf(expr, PROTEIN);
    return name.isString() ? table().proteins.get(name.toString()) : null;
  }

  private static IAST strings(Iterable<String> values, int size) {
    IASTAppendable result = F.ListAlloc(size);
    for (String value : values) {
      result.append(F.stringx(value));
    }
    return result;
  }

  /** The GO terms behind one of the three annotation properties, or <code>null</code>. */
  private static Term[] annotations(Protein protein, String property) {
    switch (property) {
      case "BiologicalProcesses":
        return protein.processes;
      case "CellularComponents":
        return protein.components;
      case "MolecularFunctions":
        return protein.functions;
      default:
        return null;
    }
  }

  private static IExpr property(Protein protein, String property) {
    switch (property) {
      case "Name":
        return F.stringx(protein.name);
      case "StandardName":
        return F.stringx(protein.symbol);
      case "Sequence":
        return F.stringx(protein.sequence);
      case "SequenceLength":
        return F.ZZ(protein.sequence.length());
      default:
        Term[] terms = annotations(protein, property);
        if (terms == null) {
          return F.Missing(S.NotAvailable);
        }
        return F.mapRange(0, terms.length, i -> F.stringx(terms[i].className));
    }
  }

  /** <code>ProteinData(p, property, "Name")</code>: the term names behind a class list. */
  private static IExpr propertyNames(Protein protein, String property) {
    Term[] terms = annotations(protein, property);
    if (terms == null) {
      return F.Missing(S.NotAvailable);
    }
    return F.mapRange(0, terms.length, i -> F.stringx(terms[i].name));
  }

  private static class ProteinData extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Table data = table();
      if (ast.isAST0()) {
        return strings(data.proteins.keySet(), data.proteins.size());
      }
      IExpr arg1 = ast.arg1();
      if (ast.isAST1()) {
        if (arg1.isString()) {
          String name = arg1.toString();
          switch (name) {
            case "Classes":
              return strings(data.classes.keySet(), data.classes.size());
            case "Properties":
              return F.mapRange(0, PROPERTIES.length, i -> F.stringx(PROPERTIES[i]));
            default:
              List<String> members = data.classes.get(name);
              if (members != null) {
                return strings(members, members.size());
              }
              break;
          }
        }
        if (proteinOf(arg1) == null) {
          return Errors.printMessage(S.ProteinData, "notent", F.List(arg1, S.ProteinData),
              engine);
        }
        return F.NIL;
      }
      if (arg1.isList()) {
        // a list of proteins threads over its elements
        return ((IAST) arg1).mapThread(ast, 1);
      }
      Protein protein = proteinOf(arg1);
      if (protein == null) {
        return Errors.printMessage(S.ProteinData, "notent", F.List(arg1, S.ProteinData), engine);
      }
      IExpr property = Entities.propertyOf(ast.arg2(), PROTEIN);
      if (!property.isString()) {
        return F.NIL;
      }
      if (ast.isAST2()) {
        return property(protein, property.toString());
      }
      if (ast.arg3().isString() && ast.arg3().toString().equals("Name")) {
        return propertyNames(protein, property.toString());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_3;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ProteinDataFunctions() {}
}
