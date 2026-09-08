package org.matheclipse.core.rubi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Reads the human readable step descriptions out of Rubi's <code>ShowSteps</code> rule file and
 * writes them as the resource which
 * {@link org.matheclipse.core.eval.steps.RubiStepDescriptions} serves at run time.
 *
 * <p>
 * Rubi ships two spellings of the same rule set. The one Symja generates its Java rules from,
 * <code>RubiRules_4.17.3.0_FullLHS.m</code>, carries the bare rewrite:
 *
 * <pre>
 * Int[...lhs...] := Int[u*(b*x^n)^p, x] /; FreeQ[{a,b,n,p}, x] &amp;&amp; EqQ[a, 0]
 * </pre>
 *
 * The other, <code>RubiRules_4.17.3.0_ShowStepsFullLHS.m</code>, wraps the same right-hand-side in
 * a <code>ShowStep</code> which says, in words, what the rule does:
 *
 * <pre>
 * Int[...lhs...] := ShowStep[1, "If \!\(\*RowBox[{\"EqQ\", ...}]\)",
 *                            "Integrate[(u)*((a) + (b)*(x)^(n))^(p), x]",
 *                            "Integrate[u*(b*x^n)^p, x]",
 *                            Hold[Int[u*(b*x^n)^p, x]]] /; ... /; True
 * </pre>
 *
 * <h3>Why the files have to be aligned</h3>
 *
 * A step has to be found by the number Symja knows a rule by, and that number is the position of
 * the rule in <code>FullLHS</code> - {@link ConvertRubi} gives every parsed rule its line number as
 * its <code>IIntegrate(n, ...)</code> number, and the pattern matcher keeps it as the rule's
 * left-hand-side priority. The <code>ShowStep</code> numbers are the line numbers of the
 * <i>other</i> file, which holds 16 rules fewer, so from about rule 2980 on the two numberings
 * drift apart and one cannot be used for the other.
 *
 * <p>
 * Both files list the rules in Rubi's own order and the second is a subset of the first, so walking
 * them together and matching on the left-hand-side text recovers the correspondence exactly: every
 * line of the ShowSteps file finds its place, and the 16 rules which only exist in
 * <code>FullLHS</code> are simply skipped.
 *
 * <h3>Output</h3>
 *
 * One gzipped, tab separated line per rule, <code>ruleNumber, condition, before, after</code>,
 * where the condition has had Mathematica's box notation flattened back into readable text
 * (<code>SuperscriptBox["b", "2"]</code> becomes <code>b^2</code>, and so on).
 *
 * <pre>
 * mvn -o -pl tools exec:java -Dexec.mainClass=org.matheclipse.core.rubi.ConvertRubiShowSteps
 * </pre>
 */
public class ConvertRubiShowSteps {

  private static final String RUBI_DIR = "Rubi/";

  private static final String SHOW_STEPS_FILE = RUBI_DIR + "RubiRules_4.17.3.0_ShowStepsFullLHS.m";

  private static final String FULL_LHS_FILE = RUBI_DIR + "RubiRules_4.17.3.0_FullLHS.m";

  private static final String OUTPUT_FILE =
      "matheclipse-core/src/main/resources/rubi/rubi_steps.tsv.gz";

  /** What separates a rule's left-hand-side from its right-hand-side in the <code>.m</code> files. */
  private static final String ASSIGNMENT = " := ";

  /** One rule's description, as it goes into the resource. */
  private static final class Description {
    final int ruleNumber;
    final String condition;
    final String before;
    final String after;

    Description(int ruleNumber, String condition, String before, String after) {
      this.ruleNumber = ruleNumber;
      this.condition = condition;
      this.before = before;
      this.after = after;
    }
  }

  public static void main(String[] args) {
    try {
      List<String> showSteps = readLines(SHOW_STEPS_FILE);
      List<String> fullLHS = readLines(FULL_LHS_FILE);
      System.out.println("ShowSteps rules: " + showSteps.size());
      System.out.println("FullLHS rules:   " + fullLHS.size());

      List<Description> descriptions = extract(fullLHS, showSteps);
      write(descriptions);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Walk both files together and read the <code>ShowStep</code> of every rule which is in both.
   *
   * @param fullLHS the rules in the order which gives them their Symja rule number
   * @param showSteps the same rules, minus a few, each carrying its description
   */
  private static List<Description> extract(List<String> fullLHS, List<String> showSteps) {
    List<Description> descriptions = new ArrayList<Description>(showSteps.size());
    int failed = 0;
    int aligned = 0;
    int showStepIndex = 0;
    for (int i = 0; i < fullLHS.size(); i++) {
      String lhs = leftHandSide(fullLHS.get(i));
      if (lhs == null || showStepIndex >= showSteps.size()) {
        continue;
      }
      String line = showSteps.get(showStepIndex);
      if (!lhs.equals(leftHandSide(line))) {
        // a rule which only exists in FullLHS; it has no description and no step to show
        continue;
      }
      showStepIndex++;
      aligned++;
      // the rule number Symja knows this rule by: its position in FullLHS
      int ruleNumber = i + 1;
      try {
        Description description = readShowStep(line, ruleNumber);
        if (description != null) {
          descriptions.add(description);
        }
      } catch (RuntimeException rex) {
        failed++;
        System.out.println(">>>>> no description for rule " + ruleNumber + ": " + rex.getMessage());
      }
    }
    System.out.println("aligned rules:   " + aligned + " (" + (showSteps.size() - aligned)
        + " of the ShowSteps rules unmatched)");
    System.out.println("descriptions:    " + descriptions.size() + ", failed: " + failed);
    return descriptions;
  }

  /** The text left of the <code>:=</code>, or <code>null</code> for a line which is not a rule. */
  private static String leftHandSide(String line) {
    int i = line.indexOf(ASSIGNMENT);
    return i > 0 ? line.substring(0, i) : null;
  }

  /**
   * Read the <code>ShowStep[...]</code> of one rule.
   *
   * <p>
   * It comes in two shapes: <code>ShowStep[n, condition, before, after, Hold[rhs]]</code> and, for
   * the handful of steps Rubi shows without naming a rule,
   * <code>ShowStep["", before, after, Hold[rhs]]</code>.
   *
   * @return <code>null</code> if the rule carries no <code>ShowStep</code> at all
   */
  private static Description readShowStep(String line, int ruleNumber) {
    int start = line.indexOf("ShowStep[");
    if (start < 0) {
      return null;
    }
    List<String> arguments = arguments(line, start + "ShowStep".length());
    if (arguments.size() < 3) {
      throw new IllegalStateException("ShowStep with " + arguments.size() + " arguments");
    }
    String condition;
    String before;
    String after;
    if (arguments.get(0).trim().startsWith("\"")) {
      condition = "";
      before = unescape(arguments.get(1));
      after = unescape(arguments.get(2));
    } else {
      condition = flattenBoxes(unescape(arguments.get(1)));
      before = unescape(arguments.get(2));
      after = unescape(arguments.get(3));
    }
    return new Description(ruleNumber, oneLine(condition), oneLine(before), oneLine(after));
  }

  /** Collapse the runs of whitespace a multi line template was written with. */
  private static String oneLine(String text) {
    return text.replace('\t', ' ').replace('\n', ' ').replaceAll(" {2,}", " ").trim();
  }

  /**
   * Split the top level arguments of a bracketed call. <code>open</code> is the index of its
   * <code>[</code>.
   */
  private static List<String> arguments(String s, int open) {
    List<String> arguments = new ArrayList<String>();
    StringBuilder current = new StringBuilder();
    int depth = 0;
    boolean inString = false;
    for (int i = open; i < s.length(); i++) {
      char c = s.charAt(i);
      if (inString) {
        current.append(c);
        if (c == '\\' && i + 1 < s.length()) {
          current.append(s.charAt(++i));
        } else if (c == '"') {
          inString = false;
        }
        continue;
      }
      if (c == '"') {
        inString = true;
        current.append(c);
      } else if (c == '[' || c == '{' || c == '(') {
        depth++;
        if (!(depth == 1 && i == open)) {
          current.append(c);
        }
      } else if (c == ']' || c == '}' || c == ')') {
        depth--;
        if (depth == 0) {
          arguments.add(current.toString());
          return arguments;
        }
        current.append(c);
      } else if (c == ',' && depth == 1) {
        arguments.add(current.toString());
        current.setLength(0);
      } else {
        current.append(c);
      }
    }
    throw new IllegalStateException("unbalanced brackets");
  }

  /**
   * Undo the escapes of a Mathematica string literal, but leave the <code>\!\(\*</code> ...
   * <code>\)</code> markers which introduce box notation intact - {@link #flattenBoxes} still needs
   * them.
   */
  private static String unescape(String literal) {
    String s = literal.trim();
    if (!s.startsWith("\"") || !s.endsWith("\"")) {
      throw new IllegalStateException("not a string: " + s.substring(0, Math.min(60, s.length())));
    }
    String body = s.substring(1, s.length() - 1);
    StringBuilder out = new StringBuilder(body.length());
    for (int i = 0; i < body.length(); i++) {
      char c = body.charAt(i);
      if (c == '\\' && i + 1 < body.length()) {
        char next = body.charAt(i + 1);
        if (next == '"' || next == '\\') {
          out.append(next);
          i++;
          continue;
        }
        if (next == 'n') {
          out.append('\n');
          i++;
          continue;
        }
        if (next == 't') {
          out.append('\t');
          i++;
          continue;
        }
      }
      out.append(c);
    }
    return out.toString();
  }

  /**
   * Replace every <code>\!\(\* ... \)</code> box expression by the text it stands for, so that a
   * condition reads <code>EqQ[b^2 c+a^2 d,0]</code> rather than as its box notation.
   */
  private static String flattenBoxes(String text) {
    final String marker = "\\!\\(\\*";
    StringBuilder out = new StringBuilder(text.length());
    int i = 0;
    while (i < text.length()) {
      int k = text.indexOf(marker, i);
      if (k < 0) {
        out.append(text, i, text.length());
        break;
      }
      out.append(text, i, k);
      int[] next = new int[1];
      out.append(readBox(text, k + marker.length(), next));
      i = next[0];
      if (text.startsWith("\\)", i)) {
        i += 2;
      }
    }
    return out.toString();
  }

  /** Read one box expression, or one quoted leaf, starting at <code>i</code>. */
  private static String readBox(String s, int i, int[] next) {
    if (i < s.length() && s.charAt(i) == '"') {
      int j = i + 1;
      while (j < s.length() && s.charAt(j) != '"') {
        j += s.charAt(j) == '\\' ? 2 : 1;
      }
      next[0] = j + 1;
      return s.substring(i + 1, Math.min(j, s.length()));
    }
    int nameEnd = i;
    while (nameEnd < s.length() && Character.isLetter(s.charAt(nameEnd))) {
      nameEnd++;
    }
    if (nameEnd == i || nameEnd >= s.length() || s.charAt(nameEnd) != '['
        || !s.startsWith("Box[", nameEnd - 3)) {
      // not a box: a bare token, for example a number between two boxes
      int j = i;
      while (j < s.length() && ",]}\\".indexOf(s.charAt(j)) < 0) {
        j++;
      }
      next[0] = j;
      return s.substring(i, j);
    }
    String name = s.substring(i, nameEnd);
    List<String> arguments = arguments(s, nameEnd);
    next[0] = nameEnd + rawLength(s, nameEnd);
    List<String> parts = new ArrayList<String>(arguments.size());
    for (String argument : arguments) {
      parts.add(readBoxArgument(argument));
    }
    return combine(name, parts);
  }

  /** How many characters the bracketed group starting at <code>open</code> spans. */
  private static int rawLength(String s, int open) {
    int depth = 0;
    boolean inString = false;
    for (int i = open; i < s.length(); i++) {
      char c = s.charAt(i);
      if (inString) {
        if (c == '\\') {
          i++;
        } else if (c == '"') {
          inString = false;
        }
        continue;
      }
      if (c == '"') {
        inString = true;
      } else if (c == '[' || c == '{') {
        depth++;
      } else if (c == ']' || c == '}') {
        depth--;
        if (depth == 0) {
          return i - open + 1;
        }
      }
    }
    return s.length() - open;
  }

  /** An argument of a box is either a <code>{...}</code> list of boxes or a single box. */
  private static String readBoxArgument(String argument) {
    String a = argument.trim();
    int[] next = new int[1];
    if (a.startsWith("{") && a.endsWith("}")) {
      String inner = a.substring(1, a.length() - 1);
      StringBuilder out = new StringBuilder();
      int i = 0;
      while (i < inner.length()) {
        while (i < inner.length() && (inner.charAt(i) == ' ' || inner.charAt(i) == ',')) {
          i++;
        }
        if (i >= inner.length()) {
          break;
        }
        out.append(readBox(inner, i, next));
        i = next[0] > i ? next[0] : i + 1;
      }
      return out.toString();
    }
    return readBox(a, 0, next);
  }

  /** Write one kind of box back as the text it stands for. */
  private static String combine(String name, List<String> parts) {
    if (name.equals("SuperscriptBox") && parts.size() == 2) {
      return parts.get(0) + "^" + parts.get(1);
    }
    if (name.equals("SubscriptBox") && parts.size() == 2) {
      return parts.get(0) + "_" + parts.get(1);
    }
    if (name.equals("FractionBox") && parts.size() == 2) {
      return "(" + parts.get(0) + ")/(" + parts.get(1) + ")";
    }
    if (name.equals("SqrtBox") && parts.size() == 1) {
      return "Sqrt(" + parts.get(0) + ")";
    }
    if (name.equals("UnderoverscriptBox") && parts.size() >= 1) {
      return parts.get(0);
    }
    // RowBox and anything else: the parts stand next to each other
    StringBuilder out = new StringBuilder();
    for (String part : parts) {
      out.append(part);
    }
    return out.toString();
  }

  /** Every non empty line of a rule file. */
  private static List<String> readLines(String fileName) throws IOException {
    List<String> lines = new ArrayList<String>(8000);
    File file = new File(fileName);
    if (!file.isFile()) {
      throw new IOException("cannot read " + file.getAbsolutePath()
          + " - run this from the symja_android_library directory");
    }
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }
    return lines;
  }

  private static void write(List<Description> descriptions) throws IOException {
    File file = new File(OUTPUT_FILE);
    file.getParentFile().mkdirs();
    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
        new GZIPOutputStream(new FileOutputStream(file)), StandardCharsets.UTF_8))) {
      for (Description description : descriptions) {
        writer.write(Integer.toString(description.ruleNumber));
        writer.write('\t');
        writer.write(description.condition);
        writer.write('\t');
        writer.write(description.before);
        writer.write('\t');
        writer.write(description.after);
        writer.write('\n');
      }
    }
    System.out.println("wrote " + file.getPath() + " (" + file.length() + " bytes)");
  }
}
