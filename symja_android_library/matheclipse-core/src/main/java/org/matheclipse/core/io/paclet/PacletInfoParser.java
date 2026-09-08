package org.matheclipse.core.io.paclet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.PackageUtil;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Reads a <code>PacletInfo.wl</code> or <code>PacletInfo.m</code>.
 *
 * <p>
 * The file is parsed and looked at, never evaluated: it is data written by whoever wrote the paclet,
 * and reading it must not run their code just to find out what a paclet is called.
 *
 * <p>
 * Two spellings are accepted, because both are in use.
 * <code>PacletObject[&lt;|"Name" -&gt; …, "Extensions" -&gt; …|&gt;]</code> is what a paclet built
 * since version 12 says; before that it was <code>Paclet[Name -&gt; …, Extensions -&gt; …]</code>,
 * with symbols for keys, and paclets in that form are still shipped - the WLJS Notebook's own LetWL
 * is one.
 */
public final class PacletInfoParser {

  private PacletInfoParser() {}

  /** The names a paclet's description may be written under. */
  public static final String[] FILE_NAMES = {"PacletInfo.wl", "PacletInfo.m"};

  /**
   * Read the paclet described by <code>pacletInfoFile</code>.
   *
   * @return the paclet, or <code>null</code> when the file cannot be read or says nothing about a
   *         Kernel extension
   */
  public static PacletInfo parse(Path pacletInfoFile, EvalEngine engine) {
    IExpr expression = parseFile(pacletInfoFile, engine);
    if (expression == null || !expression.isAST()) {
      return null;
    }
    IAST paclet = (IAST) expression;
    String head = paclet.head().toString();
    if (!head.equalsIgnoreCase("PacletObject") && !head.equalsIgnoreCase("Paclet")) {
      return null;
    }

    IExpr fields = paclet.isAST1() && paclet.arg1().isAssociation() ? paclet.arg1() : paclet;
    String name = stringValue(fields, "Name");
    String version = stringValue(fields, "Version");
    IExpr extensions = value(fields, "Extensions");
    if (extensions == null || !extensions.isList()) {
      return null;
    }

    Path directory = pacletInfoFile.getParent();
    Path kernelRoot = directory;
    List<PacletInfo.ContextFile> contexts = new ArrayList<PacletInfo.ContextFile>();
    IAST extensionList = (IAST) extensions;
    for (int i = 1; i < extensionList.size(); i++) {
      IExpr extension = extensionList.get(i);
      if (!extension.isList() || extension.isAST0()) {
        continue;
      }
      IAST entries = (IAST) extension;
      if (!entries.arg1().toString().equalsIgnoreCase("Kernel")) {
        continue;
      }
      String root = stringValue(entries, "Root");
      if (root != null) {
        kernelRoot = directory.resolve(root);
      }
      IExpr contextValue = value(entries, "Context");
      if (contextValue != null) {
        collectContexts(contextValue, contexts);
      }
    }
    if (contexts.isEmpty()) {
      return null;
    }
    return new PacletInfo(name, version, directory, kernelRoot, contexts);
  }

  /** Every <code>PacletInfo</code> file in <code>directory</code> and its immediate subdirectories. */
  public static List<Path> findIn(Path directory) {
    List<Path> found = new ArrayList<Path>();
    addIfPresent(directory, found);
    try (java.util.stream.Stream<Path> children = Files.list(directory)) {
      List<Path> subdirectories = children.filter(Files::isDirectory).sorted()
          .collect(java.util.stream.Collectors.toList());
      for (Path subdirectory : subdirectories) {
        addIfPresent(subdirectory, found);
      }
    } catch (IOException | RuntimeException ex) {
      // an unreadable directory holds no paclet as far as this is concerned
    }
    return found;
  }

  private static void addIfPresent(Path directory, List<Path> found) {
    for (String fileName : FILE_NAMES) {
      Path candidate = directory.resolve(fileName);
      if (Files.isRegularFile(candidate)) {
        found.add(candidate);
        return;
      }
    }
  }

  /**
   * The contexts of one Kernel extension: a bare <code>"A`"</code>, a <code>{"A`", "A.wl"}</code>
   * pair, or a list of either.
   */
  private static void collectContexts(IExpr contextValue, List<PacletInfo.ContextFile> contexts) {
    if (contextValue.isString()) {
      contexts.add(new PacletInfo.ContextFile(contextValue.toString(), null));
      return;
    }
    if (!contextValue.isList()) {
      return;
    }
    IAST list = (IAST) contextValue;
    if (list.size() > 1 && list.arg1().isString() && !list.arg1().toString().isEmpty()
        && allStrings(list)) {
      // {"A`", "A.wl"} - a context and the file it lives in
      if (list.size() == 3) {
        contexts.add(new PacletInfo.ContextFile(list.arg1().toString(), list.arg2().toString()));
        return;
      }
      // {"A`", "B`"} - several contexts with no file named
      for (int i = 1; i < list.size(); i++) {
        contexts.add(new PacletInfo.ContextFile(list.get(i).toString(), null));
      }
      return;
    }
    for (int i = 1; i < list.size(); i++) {
      collectContexts(list.get(i), contexts);
    }
  }

  private static boolean allStrings(IAST list) {
    for (int i = 1; i < list.size(); i++) {
      if (!list.get(i).isString()) {
        return false;
      }
    }
    return true;
  }

  /**
   * The value of <code>key</code>, whether the file writes keys as strings (<code>"Name" -&gt;
   * …</code>, the current form) or as symbols (<code>Name -&gt; …</code>, the form before version
   * 12).
   */
  private static IExpr value(IExpr fields, String key) {
    if (fields.isAssociation()) {
      // an association answers by key; iterating it yields values, not the rules
      IExpr value =
          ((org.matheclipse.core.interfaces.IAssociation) fields).getValue(F.stringx(key));
      return value == null || value.isNIL() || value.isAST(org.matheclipse.core.expression.S.Missing) ? null : value;
    }
    if (!fields.isAST()) {
      return null;
    }
    IAST list = (IAST) fields;
    for (int i = 1; i < list.size(); i++) {
      IExpr entry = list.get(i);
      if (entry.isRuleAST() && entry.first().toString().equalsIgnoreCase(key)) {
        return entry.second();
      }
    }
    return null;
  }

  private static String stringValue(IExpr fields, String key) {
    IExpr value = value(fields, key);
    return value == null ? null : value.toString();
  }

  private static IExpr parseFile(Path file, EvalEngine engine) {
    try {
      String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
      List<ASTNode> nodes = PackageUtil.parseReader(source, engine);
      AST2Expr converter = new AST2Expr(engine.isRelaxedSyntax(), engine);
      for (int i = nodes.size() - 1; i >= 0; i--) {
        IExpr expression = converter.convert(nodes.get(i));
        if (expression.isAST()) {
          String head = expression.head().toString();
          if (head.equalsIgnoreCase("PacletObject") || head.equalsIgnoreCase("Paclet")) {
            return expression;
          }
        }
      }
    } catch (IOException | RuntimeException ex) {
      // a PacletInfo file that will not read describes no paclet
    }
    return null;
  }
}
