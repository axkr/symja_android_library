package org.matheclipse.io.archunit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * Opt-in diagnostic which lists helper methods of {@code org.matheclipse.core} that may belong on
 * {@link IExpr} or {@link IAST}. It never fails; it writes a Markdown checklist in which the user
 * marks the candidates to implement.
 *
 * <pre>
 * mvn -o -pl matheclipse-io -am test -Dtest=IExprRefactoringScanTest -Diexpr.scan=true \
 *     -Diexpr.scan.out=/path/to/iexpr-candidates.md -Dsurefire.failIfNoSpecifiedTests=false
 * </pre>
 *
 * <p>
 * Checklist marks: {@code [ ]} open, {@code [x]} implement, {@code [-]} rejected. Text after
 * {@code note:} is kept. A re-run merges into the existing file: marks and notes of candidates
 * which are still found are kept; selected or annotated candidates which are no longer found move
 * to the "Gone" section with the date they disappeared.
 *
 * <p>
 * ArchUnit attributes calls and {@code instanceof} checks inside lambda bodies to the enclosing
 * method, so a helper which does its work in a lambda is counted completely.
 */
@EnabledIfSystemProperty(named = "iexpr.scan", matches = "true")
public class IExprRefactoringScanTest {

  private static final String BASE_PACKAGE = "org.matheclipse.core";
  private static final String EXPRESSION_PACKAGE = "org.matheclipse.core.expression";
  private static final String INTERFACES_PACKAGE = "org.matheclipse.core.interfaces";

  /** Generated sources, the same set the duplicate code check excludes. */
  private static final Pattern GENERATED = Pattern.compile(
      ".*/org/matheclipse/core/(integrate/rubi/|reflection/system/rules/|reflection/system/rulesets/"
          + "|expression/(ID|S|J)(\\$.*)?\\.class|data/ElementData[12](\\$.*)?\\.class).*");

  /** Minimum calls to IExpr/IAST to avoid flagging trivial 1-line delegators. */
  private static final int MIN_IEXPR_CALLS = 3;

  /** Maximum (non neutral) method calls of a feature envy candidate. */
  private static final int MAX_TOTAL_CALLS = 15;

  /** Maximum (non neutral) method calls of an instanceof dispatch candidate. */
  private static final int MAX_INSTANCEOF_CALLS = 30;

  /** Minimum ratio of IExpr/IAST calls to the non neutral method calls. */
  private static final double MIN_ENVY_RATIO = 0.70;

  /** Minimum overlap of the called method names for a cross class duplicate group. */
  private static final double MIN_DUPLICATE_SIMILARITY = 0.5;

  /** Unmarked rows per category; marked rows are always written. */
  private static final int DEFAULT_MAX_ROWS = 200;

  /** Calls to these owners are neither envy nor non envy: expression builders and boxing. */
  private static final Set<String> NEUTRAL_OWNERS = Set.of("org.matheclipse.core.expression.F",
      "org.matheclipse.core.expression.S", "org.matheclipse.core.expression.ID",
      "org.matheclipse.core.eval.Errors", "org.matheclipse.core.basic.Config", "java.lang.Math",
      "java.lang.Integer", "java.lang.Long", "java.lang.Double", "java.lang.Boolean",
      "java.util.Objects");

  /** Helper names too generic to say anything when they match an IExpr method name. */
  private static final Set<String> GENERIC_NAMES =
      Set.of("apply", "accept", "test", "evaluate", "eval", "of", "get", "set", "copy", "equals",
          "hashCode", "toString", "compareTo", "compare", "size", "create", "convert", "visit",
          "add", "remove", "map", "filter", "iterator", "init", "check", "print", "append");

  private static final String[] SECTIONS = {"DUPLICATES_EXISTING", "CROSS_CLASS_DUPLICATE",
      "INSTANCEOF_DISPATCH", "FEATURE_ENVY"};

  private static final String[] SECTION_HELP = {
      "a private helper with the name of an existing method of its parameter type: call that method",
      "the same private helper (name and parameter types) in several classes: merge into one method",
      "instanceof on expression types: polymorphic dispatch on IExpr, or the existing isXxx() predicate",
      "a small helper whose calls go mostly to its IExpr/IAST parameter: candidate default method"};

  private static final Pattern ROW = Pattern
      .compile("^- \\[([ xX-])\\] (C-[0-9a-f]{6}) (.*?)( · gone \\d{4}-\\d{2}-\\d{2})? · note:(.*)$");

  private record Candidate(String category, String id, double score, String text) {}

  /** {@code gone} is the " · gone <date>" suffix of a row which was already gone, or empty. */
  private record Mark(char mark, String text, String gone, String note) {}

  @Test
  public void scanForIExprRefactoringCandidates() throws IOException {
    JavaClasses classes = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .withImportOption(location -> !location.matches(GENERATED))//
        .importPackages(BASE_PACKAGE);

    JavaClass iexpr = classes.get(IExpr.class);
    List<Candidate> candidates = new ArrayList<>();
    Map<String, List<JavaMethod>> sameSignature = new HashMap<>();

    for (JavaClass javaClass : classes) {
      String pkg = javaClass.getPackageName();
      if (pkg.startsWith(EXPRESSION_PACKAGE) || pkg.startsWith(INTERFACES_PACKAGE)) {
        continue;
      }
      for (JavaMethod method : javaClass.getMethods()) {
        if (!isHelperCandidate(method)) {
          continue;
        }
        sameSignature.computeIfAbsent(method.getName() + parameterList(method, false),
            k -> new ArrayList<>()).add(method);
        analyzeExistingMethod(method, candidates);
        analyzeInstanceOfChecks(method, iexpr, candidates);
        analyzeFeatureEnvy(method, candidates);
      }
    }
    analyzeCrossClassDuplicates(sameSignature, candidates);

    Path out = Paths.get(System.getProperty("iexpr.scan.out", "target/iexpr-candidates.md"));
    int maxRows = Integer.getInteger("iexpr.scan.max", DEFAULT_MAX_ROWS);
    writeChecklist(out, candidates, maxRows);
  }

  /**
   * Private or package private, not compiler generated, at least one expression parameter and no
   * dependency on the {@link EvalEngine}: such helpers depend on evaluation state and stay with
   * the evaluators.
   */
  private static boolean isHelperCandidate(JavaMethod method) {
    Set<JavaModifier> modifiers = method.getModifiers();
    if (modifiers.contains(JavaModifier.PUBLIC) || modifiers.contains(JavaModifier.PROTECTED)
        || modifiers.contains(JavaModifier.SYNTHETIC) || modifiers.contains(JavaModifier.BRIDGE)
        || method.getName().startsWith("lambda$")) {
      return false;
    }
    if (method.getRawParameterTypes().stream().noneMatch(p -> p.isAssignableTo(IExpr.class))) {
      return false;
    }
    return method.getRawParameterTypes().stream().noneMatch(p -> p.isAssignableTo(EvalEngine.class))
        && method.getMethodCallsFromSelf().stream()
            .noneMatch(IExprRefactoringScanTest::evaluates);
  }

  /** A call into the {@link EvalEngine}, also through {@code F.eval(...)} or {@code expr.eval()}. */
  private static boolean evaluates(JavaMethodCall call) {
    JavaClass owner = call.getTargetOwner();
    if (owner.isAssignableTo(EvalEngine.class)) {
      return true;
    }
    String name = call.getTarget().getName();
    return name.startsWith("eval") && (owner.getName().equals("org.matheclipse.core.expression.F")
        || owner.isAssignableTo(IExpr.class));
  }

  /** A helper {@code foo(T x, ...)} where {@code T} already declares {@code foo(...)}. */
  private static void analyzeExistingMethod(JavaMethod method, List<Candidate> candidates) {
    String name = method.getName();
    if (GENERIC_NAMES.contains(name)) {
      return;
    }
    List<JavaClass> params = method.getRawParameterTypes();
    for (int i = 0; i < params.size(); i++) {
      JavaClass receiver = params.get(i);
      if (!receiver.isAssignableTo(IExpr.class)) {
        continue;
      }
      int arity = params.size() - 1;
      List<JavaMethod> existing = receiver.getAllMethods().stream()
          .filter(m -> m.getName().equals(name) && m.getRawParameterTypes().size() == arity
              && m.getModifiers().contains(JavaModifier.PUBLIC)
              && !m.getModifiers().contains(JavaModifier.STATIC))
          .collect(Collectors.toList());
      if (!existing.isEmpty()) {
        JavaMethod target = existing.get(0);
        String text = String.format("→ use existing %s.%s%s · %s · %s",
            target.getOwner().getSimpleName(), name, parameterList(target, true),
            describe(method), location(method));
        candidates.add(new Candidate(SECTIONS[0], id(SECTIONS[0], key(method)), 1.0, text));
        return;
      }
    }
  }

  private static void analyzeInstanceOfChecks(JavaMethod method, JavaClass iexpr,
      List<Candidate> candidates) {
    Map<String, Boolean> checked = new TreeMap<>();
    method.getInstanceofChecks().stream().map(check -> check.getRawType())
        .filter(type -> type.isAssignableTo(IExpr.class) && !type.isEquivalentTo(IExpr.class))
        .forEach(type -> checked.put(type.getSimpleName(),
            type.getPackageName().startsWith(EXPRESSION_PACKAGE)));
    if (checked.isEmpty()) {
      return;
    }
    Counts counts = Counts.of(method);
    if (counts.relevant() > MAX_INSTANCEOF_CALLS) {
      return;
    }
    double score = 0;
    List<String> details = new ArrayList<>();
    for (Map.Entry<String, Boolean> entry : checked.entrySet()) {
      boolean concrete = entry.getValue();
      score += concrete ? 2 : 1;
      String predicate = predicateFor(entry.getKey(), iexpr);
      details.add(entry.getKey() + (concrete ? " (concrete class)" : "")
          + (predicate == null ? "" : " ~ " + predicate + "()"));
    }
    String text = String.format("→ IExpr dispatch · %s · instanceof %s · %s", describe(method),
        String.join(", ", details), location(method));
    candidates.add(new Candidate(SECTIONS[2], id(SECTIONS[2], key(method)), score, text));
  }

  private static void analyzeFeatureEnvy(JavaMethod method, List<Candidate> candidates) {
    Counts counts = Counts.of(method);
    if (counts.iexpr() < MIN_IEXPR_CALLS || counts.relevant() > MAX_TOTAL_CALLS) {
      return;
    }
    double ratio = (double) counts.iexpr() / counts.relevant();
    if (ratio < MIN_ENVY_RATIO) {
      return;
    }
    boolean onlyIAST = method.getRawParameterTypes().stream()
        .filter(p -> p.isAssignableTo(IExpr.class)).allMatch(p -> p.isAssignableTo(IAST.class));
    String called = method.getMethodCallsFromSelf().stream()
        .filter(c -> c.getTargetOwner().isAssignableTo(IExpr.class))
        .map(c -> c.getTarget().getName() + "()").distinct().sorted().limit(6)
        .collect(Collectors.joining(", "));
    String text = String.format("→ %s · %s · %d/%d IExpr calls (%.0f%%) · calls %s · %s",
        onlyIAST ? "IAST default" : "IExpr default", describe(method), counts.iexpr(),
        counts.relevant(), ratio * 100.0, called, location(method));
    candidates.add(
        new Candidate(SECTIONS[3], id(SECTIONS[3], key(method)), ratio * counts.iexpr(), text));
  }

  /**
   * Groups of private helpers with the same name and parameter types in two or more top level
   * classes, whose called method names overlap by at least {@link #MIN_DUPLICATE_SIMILARITY}.
   */
  private static void analyzeCrossClassDuplicates(Map<String, List<JavaMethod>> sameSignature,
      List<Candidate> candidates) {
    for (List<JavaMethod> group : sameSignature.values()) {
      if (group.size() < 2 || GENERIC_NAMES.contains(group.get(0).getName())) {
        continue;
      }
      Set<String> topLevel = group.stream().map(m -> topLevelName(m.getOwner()))
          .collect(Collectors.toSet());
      if (topLevel.size() < 2) {
        continue;
      }
      double similarity = similarity(group);
      if (similarity < MIN_DUPLICATE_SIMILARITY) {
        continue;
      }
      group.sort(Comparator.comparing(m -> m.getOwner().getName()));
      String members = group.stream().map(IExprRefactoringScanTest::key).sorted()
          .collect(Collectors.joining(";"));
      String locations = group.stream().map(IExprRefactoringScanTest::location)
          .collect(Collectors.joining(", "));
      JavaMethod first = group.get(0);
      String text = String.format("→ one shared method · %s%s × %d · %.0f%% same calls · %s",
          first.getName(), parameterList(first, true), group.size(), similarity * 100.0,
          locations);
      candidates.add(new Candidate(SECTIONS[1], id(SECTIONS[1], members),
          similarity * group.size(), text));
    }
  }

  /** Jaccard index of the called method names, the smallest over all pairs. */
  private static double similarity(List<JavaMethod> group) {
    List<Set<String>> calls = group.stream()
        .map(m -> m.getMethodCallsFromSelf().stream()
            .map(c -> c.getTargetOwner().getSimpleName() + "." + c.getTarget().getName())
            .collect(Collectors.toSet()))
        .collect(Collectors.toList());
    double min = 1.0;
    for (int i = 0; i < calls.size(); i++) {
      for (int j = i + 1; j < calls.size(); j++) {
        Set<String> union = new HashSet<>(calls.get(i));
        union.addAll(calls.get(j));
        if (union.isEmpty()) {
          continue;
        }
        Set<String> intersection = new HashSet<>(calls.get(i));
        intersection.retainAll(calls.get(j));
        min = Math.min(min, (double) intersection.size() / union.size());
      }
    }
    return min;
  }

  /** {@code isInteger} for {@code IInteger} if {@code IExpr} declares it without parameters. */
  private static String predicateFor(String simpleName, JavaClass iexpr) {
    String base = simpleName.length() > 1 && simpleName.charAt(0) == 'I'
        && Character.isUpperCase(simpleName.charAt(1)) ? simpleName.substring(1) : simpleName;
    String predicate = "is" + base;
    boolean exists = iexpr.getAllMethods().stream()
        .anyMatch(m -> m.getName().equals(predicate) && m.getRawParameterTypes().isEmpty());
    return exists ? predicate : null;
  }

  /**
   * Method calls split into calls to expressions, neutral calls and the rest. Accesses to fields
   * of the method's own class count as non expression work: such a helper uses its own state.
   */
  private record Counts(int iexpr, int relevant) {
    static Counts of(JavaMethod method) {
      int iexpr = 0;
      int relevant = (int) method.getFieldAccesses().stream()
          .filter(a -> a.getTargetOwner().equals(method.getOwner())
              && !a.getTarget().resolveMember().map(f -> f.getModifiers().contains(JavaModifier.FINAL)
                  && f.getModifiers().contains(JavaModifier.STATIC)).orElse(false))
          .map(a -> a.getTarget().getName()).distinct().count();
      for (JavaMethodCall call : method.getMethodCallsFromSelf()) {
        JavaClass owner = call.getTargetOwner();
        if (NEUTRAL_OWNERS.contains(owner.getName())) {
          continue;
        }
        relevant++;
        if (owner.isAssignableTo(IExpr.class)) {
          iexpr++;
        }
      }
      return new Counts(iexpr, relevant);
    }
  }

  private static String topLevelName(JavaClass javaClass) {
    String name = javaClass.getName();
    int index = name.indexOf('$');
    return index < 0 ? name : name.substring(0, index);
  }

  /** Stable identity of a method: owner, name and erased parameter types. */
  private static String key(JavaMethod method) {
    return method.getOwner().getName() + "#" + method.getName() + parameterList(method, false);
  }

  private static String parameterList(JavaMethod method, boolean simple) {
    return method.getRawParameterTypes().stream()
        .map(p -> simple ? p.getSimpleName() : p.getName())
        .collect(Collectors.joining(", ", "(", ")"));
  }

  private static String describe(JavaMethod method) {
    String owner = method.getOwner().getName();
    owner = owner.substring(owner.lastIndexOf('.') + 1);
    return method.getRawReturnType().getSimpleName() + " " + owner + "." + method.getName()
        + parameterList(method, true);
  }

  /** Path relative to a source root, for example {@code org/matheclipse/core/eval/Foo.java:12}. */
  private static String location(JavaMethod method) {
    JavaClass owner = method.getOwner();
    String file = owner.getSourceCodeLocation().getSourceFileName();
    return owner.getPackageName().replace('.', '/') + "/" + file + ":"
        + method.getSourceCodeLocation().getLineNumber();
  }

  private static String id(String category, String key) {
    try {
      byte[] hash = MessageDigest.getInstance("SHA-1")
          .digest((category + "|" + key).getBytes(StandardCharsets.UTF_8));
      StringBuilder buf = new StringBuilder("C-");
      for (int i = 0; i < 3; i++) {
        buf.append(String.format("%02x", hash[i] & 0xff));
      }
      return buf.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  /** Marks and notes of the previous run, keyed by candidate id. */
  private static Map<String, Mark> readMarks(Path out) throws IOException {
    Map<String, Mark> marks = new LinkedHashMap<>();
    if (!Files.exists(out)) {
      return marks;
    }
    for (String line : Files.readAllLines(out, StandardCharsets.UTF_8)) {
      Matcher matcher = ROW.matcher(line);
      if (matcher.matches()) {
        char mark = Character.toLowerCase(matcher.group(1).charAt(0));
        marks.put(matcher.group(2), new Mark(mark, matcher.group(3),
            matcher.group(4) == null ? "" : matcher.group(4), matcher.group(5).trim()));
      }
    }
    return marks;
  }

  private static void writeChecklist(Path out, List<Candidate> candidates, int maxRows)
      throws IOException {
    Map<String, Mark> previous = readMarks(out);
    String today = LocalDate.now().toString();
    candidates.sort(Comparator.comparingDouble(Candidate::score).reversed()
        .thenComparing(Candidate::text));

    StringBuilder body = new StringBuilder();
    List<String> rejected = new ArrayList<>();
    Set<String> found = new HashSet<>();
    int open = 0;
    int selected = 0;
    for (int s = 0; s < SECTIONS.length; s++) {
      String section = SECTIONS[s];
      List<String> rows = new ArrayList<>();
      int cut = 0;
      int unmarked = 0;
      for (Candidate candidate : candidates) {
        if (!candidate.category().equals(section)) {
          continue;
        }
        found.add(candidate.id());
        Mark mark = previous.get(candidate.id());
        char m = mark == null ? ' ' : mark.mark();
        String note = mark == null ? "" : mark.note();
        String row = row(m, candidate.id(), candidate.text(), note);
        if (m == '-') {
          rejected.add(row);
        } else if (m == ' ' && note.isEmpty() && unmarked >= maxRows) {
          cut++;
        } else {
          rows.add(row);
          if (m == ' ') {
            unmarked++;
            open++;
          } else {
            selected++;
          }
        }
      }
      body.append("\n## ").append(section).append(" (").append(rows.size()).append(")\n\n");
      body.append("_").append(SECTION_HELP[s]).append("_\n\n");
      rows.forEach(r -> body.append(r).append('\n'));
      if (cut > 0) {
        body.append("\n_").append(cut)
            .append(" more below the cut; raise it with -Diexpr.scan.max=<rows>._\n");
      }
    }

    List<String> gone = new ArrayList<>();
    for (Map.Entry<String, Mark> entry : previous.entrySet()) {
      Mark mark = entry.getValue();
      // an open row without a note which is no longer found just disappears
      if (!found.contains(entry.getKey()) && (mark.mark() == 'x' || !mark.note().isEmpty())) {
        String since = mark.gone().isEmpty() ? " · gone " + today : mark.gone();
        gone.add(row(mark.mark(), entry.getKey(), mark.text() + since, mark.note()));
      }
    }

    body.append("\n## Rejected (").append(rejected.size()).append(")\n\n");
    if (!rejected.isEmpty()) {
      body.append("<details><summary>show</summary>\n\n");
      rejected.forEach(r -> body.append(r).append('\n'));
      body.append("\n</details>\n");
    }
    body.append("\n## Gone, done or renamed (").append(gone.size()).append(")\n\n");
    gone.forEach(r -> body.append(r).append('\n'));

    StringBuilder head = new StringBuilder();
    head.append("# IExpr / IAST refactoring candidates\n\n");
    head.append("_Last scan ").append(today).append(" · ").append(selected).append(" selected · ")
        .append(open).append(" open · ").append(rejected.size()).append(" rejected · ")
        .append(gone.size()).append(" gone_\n\n");
    head.append("Mark `[x]` to implement, `[-]` to reject; free text after `note:` is an ")
        .append("instruction for that candidate. A re-run keeps marks and notes.\n");

    Path parent = out.toAbsolutePath().getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    Files.writeString(out, head.append(body).toString(), StandardCharsets.UTF_8);
    System.out.printf("IExpr refactoring scan: %d candidates, %d selected, %d gone -> %s%n",
        found.size(), selected, gone.size(), out.toAbsolutePath());
  }

  private static String row(char mark, String id, String text, String note) {
    return "- [" + mark + "] " + id + " " + text + " · note:" + (note.isEmpty() ? "" : " " + note);
  }
}
