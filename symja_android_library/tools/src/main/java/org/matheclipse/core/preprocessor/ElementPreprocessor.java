package org.matheclipse.core.preprocessor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Turns <code>element.csv</code> into the <code>ElementData1</code> and <code>ElementData2</code>
 * tables that {@link org.matheclipse.core.data.ElementData} serves.
 *
 * <p>
 * The two generated files are written directly, so regenerating is reproducible rather than a
 * copy-and-paste of console output. They carry a header saying as much: the CSV is the source of
 * truth and hand edits to the Java are lost on the next run.
 *
 * <p>
 * The table is split in two because a single class-initializer would be large; the split point is
 * arbitrary and only has to stay consistent with {@link #SPLIT_AFTER}.
 *
 * <p>
 * Usage: <code>ElementPreprocessor [element.csv [output-directory]]</code>. Both default to their
 * places in the repository, found by walking up from the working directory, so it runs the same
 * from the repository root or from the <code>tools</code> module.
 */
public class ElementPreprocessor {

  /** The last atomic number that goes into <code>ElementData1</code>; the rest go into the second. */
  private static final int SPLIT_AFTER = 59;

  private static final String CSV_RELATIVE =
      "tools/src/main/java/org/matheclipse/core/preprocessor/element.csv";

  private static final String OUTPUT_RELATIVE =
      "matheclipse-core/src/main/java/org/matheclipse/core/data";

  public static void main(String[] args) throws IOException {
    F.initSymja();
    Path root = repositoryRoot();
    Path csv = args.length > 0 ? Paths.get(args[0]) : root.resolve(CSV_RELATIVE);
    Path outputDir = args.length > 1 ? Paths.get(args[1]) : root.resolve(OUTPUT_RELATIVE);

    List<String> header = new ArrayList<String>();
    List<String> rows = readRows(csv, header);
    if (rows.size() != 118) {
      throw new IOException("expected 118 elements in " + csv + ", found " + rows.size());
    }
    // the column names travel with the data, so the lookup can never drift from the table
    write(outputDir.resolve("ElementData1.java"), "ElementData1",
        rows.subList(0, SPLIT_AFTER), header);
    write(outputDir.resolve("ElementData2.java"), "ElementData2",
        rows.subList(SPLIT_AFTER, rows.size()), null);
    System.out.println("wrote " + outputDir.resolve("ElementData1.java"));
    System.out.println("wrote " + outputDir.resolve("ElementData2.java"));
  }

  /** One Java expression per element, in atomic-number order, header row skipped. */
  private static List<String> readRows(Path csv, List<String> header)
      throws IOException {
    EvalEngine engine = EvalEngine.get();
    boolean relaxedSyntax = false;
    AST2Expr ast2Expr = new AST2Expr(relaxedSyntax, engine);
    final Parser parser = new Parser(relaxedSyntax, true);

    List<String> rows = new ArrayList<String>();
    CSVFormat csvFormat = CSVFormat.RFC4180.builder().setDelimiter('\t').build();
    try (Reader reader = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
      boolean first = true;
      for (CSVRecord record : csvFormat.parse(reader)) {
        if (first) {
          first = false;
          for (String column : record) {
            header.add(column.trim().replaceAll("^\"|\"$", ""));
          }
          continue;
        }
        IASTAppendable columnList = F.ListAlloc(record.size());
        for (String str : record) {
          str = str.trim();
          if (str.length() == 0) {
            // An empty cell is missing data, not an absent column. Skipping it used to shift every
            // later column of that row left by one, so phosphorus reported its van der Waals radius
            // as its atomic radius and its ionization energies as its covalent radius.
            columnList.append(F.Missing(F.NotAvailable));
            continue;
          }
          if (str.equalsIgnoreCase("Not_applicable")) {
            columnList.append(F.Missing(F.NotApplicable));
          } else if (str.equalsIgnoreCase("Not_available")) {
            columnList.append(F.Missing(F.NotAvailable));
          } else if (str.equalsIgnoreCase("Not_known")) {
            columnList.append(F.Missing(F.Unknown));
          } else {
            final ASTNode node = parser.parse(str);
            IExpr temp = ast2Expr.convert(node);
            if (temp.isList() || temp.isReal()) {
              columnList.append(temp);
            } else if (str.charAt(0) == '"') {
              columnList.append(str.substring(1, str.length() - 1));
            } else {
              columnList.append(str);
            }
          }
        }
        rows.add(((IAST) columnList)
            .internalJavaString(SourceCodeProperties.JAVA_FORM_PROPERTIES, 1, x -> null)
            .toString());
      }
    }
    return rows;
  }

  private static void write(Path file, String typeName, List<String> rows, List<String> header)
      throws IOException {
    StringBuilder body = new StringBuilder();
    for (String row : rows) {
      body.append("      ").append(row).append(",\n");
    }
    // the rendered expressions are F.- and S.-qualified, so plain imports are what they need
    String text = body.toString();
    StringBuilder out = new StringBuilder();
    out.append("package org.matheclipse.core.data;\n\n");
    if (text.contains("F.")) {
      out.append("import org.matheclipse.core.expression.F;\n");
    }
    if (text.contains("S.")) {
      out.append("import org.matheclipse.core.expression.S;\n");
    }
    out.append("import org.matheclipse.core.interfaces.IAST;\n\n");
    out.append("/**\n");
    out.append(" * Generated from <code>element.csv</code> by\n");
    out.append(" * {@link org.matheclipse.core.preprocessor.ElementPreprocessor} - do not edit by\n");
    out.append(" * hand, edit the CSV and regenerate.\n");
    out.append(" */\n");
    out.append("public interface ").append(typeName).append(" {\n\n");
    if (header != null) {
      out.append("  /** The column names of <code>element.csv</code>, in order. */\n");
      out.append("  static final String[] COLUMNS = {\n");
      for (String column : header) {
        out.append("      \"").append(column).append("\", //\n");
      }
      out.append("  };\n\n");
    }
    out.append("  static final IAST[] ELEMENTS = {\n");
    out.append(body);
    out.append("  };\n");
    out.append("}\n");

    Files.createDirectories(file.getParent());
    try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
      writer.write(out.toString());
    }
  }

  /** The <code>symja_android_library</code> directory, found by walking up from the caller. */
  private static Path repositoryRoot() throws IOException {
    Path candidate = Paths.get("").toAbsolutePath();
    for (int i = 0; i < 6 && candidate != null; i++) {
      if (Files.isDirectory(candidate.resolve("matheclipse-core"))
          && Files.isDirectory(candidate.resolve("tools"))) {
        return candidate;
      }
      candidate = candidate.getParent();
    }
    throw new IOException("could not locate the symja_android_library directory from "
        + Paths.get("").toAbsolutePath());
  }

  private ElementPreprocessor() {}
}
