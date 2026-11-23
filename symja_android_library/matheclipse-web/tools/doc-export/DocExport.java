/*
 * DocExport.java - build-time exporter for the notebook's documentation pane.
 *
 * The notebook page (matheclipse-io/src/main/resources/public/media/js/doc.js) asks the server for
 *
 *   GET /ajax/doc/<path>              -> answered by AJAXDocServlet
 *   GET /ajax/doc/search/?query=<q>   -> answered by AJAXSearchServlet
 *
 * A static site has no servlet, so this program runs the very same servlets once, at build time,
 * and writes every answer a visitor can ask for into plain files:
 *
 *   <outDir>/index.json                   the answer for "/" (and "/index")
 *   <outDir>/<page>.json                  one per top-level doc/*.md page, e.g. 98-function-by-category.json
 *   <outDir>/functions/<Name>.json        one per doc/functions/*.md page
 *   <outDir>/search/<Name>.json           the search answer when a query matches exactly one name
 *   <outDir>/search-index.json            what the browser needs to redo AJAXSearchServlet's search
 *   <outDir>/README.md                    format description (copied from doc-json-README.md by export-docs.sh)
 *
 * It is a single-file Java 21 program started as
 *
 *   java -cp <matheclipse-io classpath> DocExport.java <outDir>
 *
 * (export-docs.sh builds the classpath and calls it). It never ships to the browser.
 *
 * Why the servlets are called instead of re-implemented: the byte-for-byte promise. doGet() runs the
 * servlet's own path handling, Markdown rendering and JSON encoding; the exporter only fakes the
 * HttpServletRequest/Response it needs, with java.lang.reflect.Proxy, so no servlet container is
 * started and the servlet classes stay untouched.
 */

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.io.servlet.AJAXDocServlet;
import org.matheclipse.io.servlet.AJAXSearchServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DocExport {

  /** The text AJAXSearchServlet / AJAXDocServlet answer when there is nothing to show. */
  private static final String NOTHING_FOUND_MARK = "Insert a keyword and append a '*'";

  /** Counters for the final report. */
  private static int fileCount = 0;
  private static long byteCount = 0;

  /**
   * Run a servlet's doGet() with a faked request and return exactly the bytes it prints.
   *
   * <p>
   * The servlet only uses getPathInfo(), getParameter() on the request and the content-type/
   * encoding/header setters plus getWriter() on the response, so a dynamic proxy that answers those
   * is enough. Any other method returns null/0/false.
   *
   * @param servlet the servlet to run
   * @param pathInfo the part of the URL after the servlet mapping, or null
   * @param queryParam the value of the "query" parameter, or null
   * @return the response body, UTF-8 text
   */
  static String call(HttpServlet servlet, String pathInfo, String queryParam) throws Exception {
    StringWriter body = new StringWriter();
    PrintWriter writer = new PrintWriter(body);
    InvocationHandler reqHandler = (proxy, method, args) -> switch (method.getName()) {
      case "getPathInfo" -> pathInfo;
      case "getParameter" -> "query".equals(args[0]) ? queryParam : null;
      default -> defaultValue(method.getReturnType());
    };
    InvocationHandler resHandler = (proxy, method, args) -> switch (method.getName()) {
      case "getWriter" -> writer;
      default -> defaultValue(method.getReturnType());
    };
    HttpServletRequest req = (HttpServletRequest) Proxy.newProxyInstance(
        DocExport.class.getClassLoader(), new Class<?>[] {HttpServletRequest.class}, reqHandler);
    HttpServletResponse res = (HttpServletResponse) Proxy.newProxyInstance(
        DocExport.class.getClassLoader(), new Class<?>[] {HttpServletResponse.class}, resHandler);
    servlet.getClass().getMethod("doGet", HttpServletRequest.class, HttpServletResponse.class)
        .invoke(servlet, req, res);
    writer.flush();
    String text = body.toString();
    // The servlets end their answer with out.println(). In the real server (Undertow) that writes
    // "\r\n" whatever the platform, while this JVM's PrintWriter writes the platform's separator.
    // The line end is the only place where they differ, so normalise it to the server's, which
    // keeps the files byte-identical to the live answers.
    if (text.endsWith("\n") && !text.endsWith("\r\n")) {
      text = text.substring(0, text.length() - 1) + "\r\n";
    }
    return text;
  }

  /** The "do nothing" answer for a proxied method: null for objects, zero/false for primitives. */
  private static Object defaultValue(Class<?> type) {
    if (type == boolean.class) {
      return false;
    }
    if (type == int.class) {
      return 0;
    }
    if (type == long.class) {
      return 0L;
    }
    return null;
  }

  /**
   * The script AJAXDocServlet appends to every page (as it appears inside the JSON string). It
   * asks MathJax 2 to typeset the page. The static notebook loads KaTeX instead, so the global
   * MathJax does not exist there and the script would throw "MathJax is not defined". The servlet
   * is outside the experimental module and stays untouched; the export removes exactly this text
   * and nothing else. (The page's own TeX is rendered by the notebook's KaTeX code.)
   */
  static final String MATHJAX_SCRIPT = "<script type=\\\"text/javascript\\\">"
      + "MathJax.Hub.Queue(['Typeset',MathJax.Hub,'mjax']); </script>";

  /** Remove {@link #MATHJAX_SCRIPT} from a servlet answer; every other byte is kept. */
  static String stripMathJax(String body) {
    return body.replace(MATHJAX_SCRIPT, "");
  }

  /** Write one output file (creating directories) and count it. */
  static void write(Path outDir, String relative, String content) throws Exception {
    Path target = outDir.resolve(relative);
    Files.createDirectories(target.getParent());
    byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
    Files.write(target, bytes);
    fileCount++;
    byteCount += bytes.length;
  }

  /** The folder doc/ of the Markdown sources, found through the class path like the servlet does. */
  static Path docDirectory() throws Exception {
    URL index = Thread.currentThread().getContextClassLoader().getResource("doc/index.md");
    if (index == null || !"file".equals(index.getProtocol())) {
      throw new IllegalStateException(
          "doc/index.md must be a plain file on the class path (use target/classes), got " + index);
    }
    return Paths.get(index.toURI()).getParent();
  }

  /** The file names (without ".md") in a folder, sorted. */
  static List<String> markdownNames(Path dir) throws Exception {
    try (Stream<Path> s = Files.list(dir)) {
      return s.map(p -> p.getFileName().toString()).filter(n -> n.endsWith(".md")).sorted()
          .map(n -> n.substring(0, n.length() - 3)).toList();
    }
  }

  /** Minimal JSON string literal (names only hold ASCII letters, digits and '$'). */
  static String json(String s) {
    StringBuilder b = new StringBuilder("\"");
    for (char c : s.toCharArray()) {
      if (c == '"' || c == '\\') {
        b.append('\\').append(c);
      } else if (c == '\n') {
        b.append("\\n");
      } else {
        b.append(c);
      }
    }
    return b.append('"').toString();
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("usage: java -cp <classpath> DocExport.java <outDir>");
      System.exit(2);
    }
    Path out = Paths.get(args[0]).toAbsolutePath();
    Path docDir = docDirectory();
    AJAXDocServlet docServlet = new AJAXDocServlet();
    AJAXSearchServlet searchServlet = new AJAXSearchServlet();

    // ---- 1. documentation pages ---------------------------------------------------------------
    // The servlet maps "/" to index, "/<x>" to doc/<x>.md and "/functions/<x>" to
    // doc/functions/<x>.md, so we request exactly those paths.
    List<String> topLevel = markdownNames(docDir);
    List<String> functionNames = markdownNames(docDir.resolve("functions"));
    Set<String> exportedPaths = new HashSet<>();
    for (String page : topLevel) {
      String body = stripMathJax(call(docServlet, page.equals("index") ? "/" : "/" + page, null));
      if (body.contains(NOTHING_FOUND_MARK)) {
        System.err.println("WARN: no content for page " + page);
        continue;
      }
      write(out, page + ".json", body);
      exportedPaths.add(page);
    }
    // The pages link back with loadDoc('/index'); the servlet gives the same body for "/" and
    // "/index", so index.json serves both and no second file is needed.
    for (String name : functionNames) {
      String body = stripMathJax(call(docServlet, "/functions/" + name, null));
      if (body.contains(NOTHING_FOUND_MARK)) {
        System.err.println("WARN: no content for function " + name);
        continue;
      }
      write(out, "functions/" + name + ".json", body);
      exportedPaths.add("functions/" + name);
    }

    // Every loadDoc('<path>') link inside an exported page must resolve to an exported file,
    // otherwise the static docs pane would show a dead link.
    Pattern link = Pattern.compile("loadDoc\\(&#39;/?([^&']*)&#39;\\)|loadDoc\\('/?([^']*)'\\)");
    int dead = 0;
    for (String p : new ArrayList<>(exportedPaths)) {
      String body = Files.readString(out.resolve(p + ".json"));
      Matcher m = link.matcher(body.replace("\\\"", "\""));
      while (m.find()) {
        String target = m.group(1) != null ? m.group(1) : m.group(2);
        if (!exportedPaths.contains(target)) {
          // These are links the Markdown sources themselves have to pages that do not exist; the
          // server answers them with the "Insert a keyword" text, the static site with a 404 the
          // browser turns into the same text (see README). Counted, not an error.
          dead++;
        }
      }
    }

    // ---- 2. search index ----------------------------------------------------------------------
    // AJAXSearchServlet.findDocumentation() lists Documentation.getDocumentationByPrefix(query,
    // false) = AST2Expr.getSuggestTree() prefix matches on the lower-cased query, mapped back to
    // their canonical spelling. We collect every name the tree holds ...
    Set<Character> firstChars = new TreeSet<>();
    for (String s : AST2Expr.FUNCTION_STRINGS) {
      if (s.length() > 1) {
        firstChars.add(Character.toLowerCase(s.charAt(0)));
      }
    }
    for (char c = 'a'; c <= 'z'; c++) {
      firstChars.add(c);
    }
    firstChars.add('$');
    // ... by asking for every one-character prefix (symbols and constants live in the same tree).
    Map<String, String> canonicalByLower = new LinkedHashMap<>();
    for (char c : firstChars) {
      IAST list = Documentation.getDocumentationByPrefix(String.valueOf(c), false);
      for (int i = 1; i < list.size(); i++) {
        String n = list.get(i).toString();
        canonicalByLower.put(n.toLowerCase(Locale.US), n);
      }
    }
    int treeSize = AST2Expr.getSuggestTree().size();
    if (treeSize != canonicalByLower.size()) {
      System.err.println("WARN: suggest tree holds " + treeSize + " names, collected "
          + canonicalByLower.size());
    }

    // The tree answers in its own rank order (weight, then an order we do not want to guess at).
    // Every prefix starts with a fixed first character, so ranks only matter inside one first-
    // character group: we keep each group in the order the tree itself returned it and
    // concatenate the groups. Filtering that list by prefix must reproduce the servlet's lists;
    // the verification below checks this for every prefix of every name.
    List<String> lowerOrdered = new ArrayList<>(canonicalByLower.keySet());
    List<String> names = new ArrayList<>();
    for (String l : lowerOrdered) {
      names.add(canonicalByLower.get(l));
    }

    // Verify: for every prefix of every name, the servlet's list == names filtered by prefix.
    Set<String> prefixes = new TreeSet<>();
    for (String l : lowerOrdered) {
      for (int len = 1; len <= l.length(); len++) {
        prefixes.add(l.substring(0, len));
      }
    }
    int orderMismatch = 0;
    for (String p : prefixes) {
      IAST expected = Documentation.getDocumentationByPrefix(p, false);
      List<String> got = new ArrayList<>();
      for (String l : lowerOrdered) {
        if (l.startsWith(p)) {
          got.add(canonicalByLower.get(l));
        }
      }
      boolean same = got.size() == expected.argSize();
      for (int i = 0; same && i < got.size(); i++) {
        same = got.get(i).equals(expected.get(i + 1).toString());
      }
      if (!same && ++orderMismatch <= 5) {
        System.err.println("WARN: prefix rule differs for '" + p + "'");
      }
    }

    // Names that have a function page (the servlet only lists those) ...
    Set<String> hasPage = new HashSet<>(functionNames);
    List<String> pageNames = new ArrayList<>();
    for (String n : names) {
      if (hasPage.contains(n)) {
        pageNames.add(n);
      }
    }
    // ... and the queries that match exactly one name get the page's own text as the answer. A
    // name is such a "single" match iff no other name starts with it. The answer is stored once
    // per name; the browser fetches search/<Name>.json.
    List<String> singles = new ArrayList<>();
    for (String l : lowerOrdered) {
      boolean leaf = true;
      for (String other : lowerOrdered) {
        if (!other.equals(l) && other.startsWith(l)) {
          leaf = false;
          break;
        }
      }
      String n = canonicalByLower.get(l);
      if (leaf && hasPage.contains(n)) {
        write(out, "search/" + n + ".json", call(searchServlet, "/search/", n));
        singles.add(n);
      }
    }
    // A one-character query that matches no name at all falls back to the page of that character
    // (the C, D, E, I, N pages). Normally the tree has names for every such character.
    List<String> oneCharFallbacks = new ArrayList<>();
    for (String n : functionNames) {
      if (n.length() == 1
          && Documentation.getDocumentationByPrefix(n, false).argSize() == 0) {
        write(out, "search/" + n + ".json", call(searchServlet, "/search/", n));
        oneCharFallbacks.add(n);
      }
    }

    // Capture the exact HTML pieces the servlet wraps around a list of matches, by asking for a
    // prefix with several matches and an empty one. The browser rebuilds the list from these.
    String sample = call(searchServlet, "/search/", "sinh");
    String nothing = call(searchServlet, "/search/", "zzzzzz");
    // Sanity check of the template (see README): redo the servlet's list answer for every prefix
    // with 2..N matches and compare with the template output.
    int templateMismatch = 0;
    int templateChecked = 0;
    for (String p : prefixes) {
      List<String> got = new ArrayList<>();
      for (String l : lowerOrdered) {
        if (l.startsWith(p)) {
          got.add(canonicalByLower.get(l));
        }
      }
      if (got.size() < 2) {
        continue;
      }
      String expected = call(searchServlet, "/search/", p);
      String built = SearchTemplate.build(got, hasPage);
      templateChecked++;
      if (!expected.equals(built)) {
        if (++templateMismatch <= 5) {
          System.err.println("WARN: list template differs for '" + p + "'");
          System.err.println("  servlet : " + abbreviate(expected));
          System.err.println("  template: " + abbreviate(built));
        }
      }
    }

    StringBuilder idx = new StringBuilder(1 << 17);
    idx.append("{\n  \"version\": 1,\n");
    idx.append("  \"names\": [");
    appendStrings(idx, names);
    idx.append("],\n  \"pageNames\": [");
    appendStrings(idx, pageNames);
    idx.append("],\n  \"singles\": [");
    appendStrings(idx, singles);
    idx.append("],\n  \"oneCharFallbacks\": [");
    appendStrings(idx, oneCharFallbacks);
    idx.append("],\n  \"template\": {\n");
    idx.append("    \"listPrefix\": ").append(json(SearchTemplate.LIST_PREFIX)).append(",\n");
    idx.append("    \"item\": ").append(json(SearchTemplate.ITEM)).append(",\n");
    idx.append("    \"separator\": ").append(json(SearchTemplate.SEPARATOR)).append(",\n");
    idx.append("    \"listSuffix\": ").append(json(SearchTemplate.LIST_SUFFIX)).append(",\n");
    idx.append("    \"nothingFound\": ").append(json(SearchTemplate.NOTHING)).append("\n  }\n}\n");
    write(out, "search-index.json", idx.toString());

    System.out.println("pages: " + topLevel.size() + " top-level + " + functionNames.size()
        + " functions; search names: " + names.size() + ", with page: " + pageNames.size()
        + ", single-match files: " + singles.size());
    System.out.println("checks: links to pages without a file (404 -> nothingFound) " + dead + ", prefix-rule mismatches " + orderMismatch
        + " of " + prefixes.size() + " prefixes, list-template mismatches " + templateMismatch
        + " of " + templateChecked);
    System.out.println("sample 'sinh': " + abbreviate(sample));
    System.out.println("sample nothing: " + abbreviate(nothing));
    System.out.println("files written: " + fileCount + ", bytes: " + byteCount);
    if (orderMismatch > 0 || templateMismatch > 0) {
      System.exit(1);
    }
  }

  /** Cut long strings for the log. */
  private static String abbreviate(String s) {
    String t = s.replace("\n", "\\n");
    return t.length() > 300 ? t.substring(0, 300) + "..." : t;
  }

  /** Append the elements as comma separated JSON strings, one per line for readable diffs. */
  private static void appendStrings(StringBuilder b, List<String> items) {
    for (int i = 0; i < items.size(); i++) {
      b.append(i == 0 ? "\n    " : (i % 12 == 0 ? ",\n    " : ", ")).append(json(items.get(i)));
    }
    b.append(items.isEmpty() ? "" : "\n  ");
  }

  /**
   * The HTML AJAXSearchServlet wraps around a list of several matches, as a template the browser
   * can apply. Kept in one place so the exporter checks the same text it publishes.
   */
  static final class SearchTemplate {
    /** Start of the answer, up to the first item. */
    static final String LIST_PREFIX = "<div id=\"docContent\">\n<p>";
    /** One match; every {name} is replaced by the function name. */
    static final String ITEM =
        "<a href=\"javascript:loadDoc('/functions/{name}')\">{name}</a>";
    /** Written after every match but the last name of the full match list, page or not. */
    static final String SEPARATOR = ", ";
    /** End of the answer. */
    static final String LIST_SUFFIX = "</p>\n\n</div>";
    /** The content shown when the search has nothing to list. */
    static final String NOTHING =
        "<p>Insert a keyword and append a '*' to search for keywords. Example: <b>Int*</b>.</p>";

    /**
     * Rebuild the full response body (JSON + newline) for a list of two or more matches, the way
     * the browser does it: items for the names that have a page, a separator after each name but
     * the last, trailing white space dropped (Markdown does that to the paragraph's last line).
     */
    static String build(List<String> matches, Set<String> hasPage) {
      StringBuilder b = new StringBuilder();
      boolean any = false;
      for (int i = 0; i < matches.size(); i++) {
        String n = matches.get(i);
        if (hasPage.contains(n)) {
          b.append(ITEM.replace("{name}", n));
          any = true;
          if (i != matches.size() - 1) {
            b.append(SEPARATOR);
          }
        }
      }
      String content = any ? LIST_PREFIX + b.toString().stripTrailing() + LIST_SUFFIX : NOTHING;
      return "{\"content\":" + json(content) + "}\r\n";
    }
  }
}
