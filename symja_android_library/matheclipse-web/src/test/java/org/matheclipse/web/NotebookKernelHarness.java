package org.matheclipse.web;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.matheclipse.core.basic.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Runs the requests of the servlet-vs-kernel comparison through {@link NotebookKernel} on a desktop
 * JVM, the way the page in the browser will: only through the kernel's static string and byte
 * array methods.
 *
 * <p>
 * The list is the one of <code>requests.md</code> in the comparison's scratch directory - the same
 * requests, in the same order and in one session, that were sent to the real
 * <code>ServletServer</code> with curl - so that every body written here can be compared with the
 * servlet's answer file for file. A test class and not part of the kernel jar: it is compiled to
 * <code>target/test-classes</code> and run with <code>target/notebook/jars/*</code> as the rest of
 * the class path, which exercises exactly the jar set the browser loads.
 *
 * <p>
 * Usage: <code>java -cp target/test-classes:target/notebook/jars/* org.matheclipse.web.NotebookKernelHarness &lt;outDir&gt;</code>
 */
public final class NotebookKernelHarness {

  private static final ObjectMapper JSON = new ObjectMapper();

  /** Where the bodies go, one file per request. */
  private static Path out;

  /** One line per request: number, name, elapsed milliseconds, size. */
  private static final StringBuilder SUMMARY = new StringBuilder();

  private NotebookKernelHarness() {}

  /**
   * Run every request and write its body.
   *
   * @param args the output directory
   */
  public static void main(String[] args) throws Exception {
    out = Paths.get(args.length > 0 ? args[0] : "kernel");
    Files.createDirectories(out);

    long t0 = System.currentTimeMillis();
    String started = NotebookKernel.start();
    SUMMARY.append("00 start ").append(System.currentTimeMillis() - t0).append(" ms ")
        .append(started).append('\n');
    // idempotent: the second start answers at once with the first result
    long t1 = System.currentTimeMillis();
    String again = NotebookKernel.start();
    SUMMARY.append("00 start-again ").append(System.currentTimeMillis() - t1).append(" ms same=")
        .append(again.equals(started)).append('\n');

    query("01", "integrate", "Integrate(x^2*Sin(x),x)");
    query("02", "message", "1/0");
    query("03", "multiline", "f(x_):=x^2\nf(3)");
    query("04", "plot", "Plot(Sin(x),{x,0,6})");
    String manipulate = query("05", "manipulate", "Manipulate(Plot(Sin(a*x),{x,0,6}),{a,1,5})");
    call("06", "manipulate-frame", "manipulate",
        params("id", firstId(manipulate), "bindings", "{\"a\":3}"));
    String dynamic = query("07", "dynamic", "{Slider(Dynamic(y)), Dynamic(y^2)}");
    call("08", "dynamic-control", "dynamic",
        params("id", firstId(dynamic), "control", "0", "value", "0.5", "poll", "0"));
    call("09", "notebook-get", "notebook", params("__method", "GET"));
    call("10", "notebook-post", "notebook", params("markdown",
        "[\"# Title\\n\\nInline $x^2$ and **bold** <script>alert(1)</script>\"]"));
    timed("11", "upload", () -> NotebookKernel.upload("data.csv",
        "a,b\n1,2\n3,4\n".getBytes(StandardCharsets.UTF_8)));
    query("12", "import", "Import(\"data.csv\")");
    download("13", "download", "data.csv");
    String export = query("14", "exportform", "ExportForm({{1,2},{3,4}},\"CSV\")");
    Matcher link = Pattern.compile("name=([A-Za-z0-9._-]+)").matcher(export);
    download("15", "download-export", link.find() ? link.group(1) : "missing-link");
    download("16", "download-missing", "nope.csv");
    String dialog = query("17", "tracedialog", "TraceDialog(D(x^2*Sin(x),x))");
    String dialogId = firstId(dialog);
    call("18", "tracedialog-continue", "tracedialog", params("id", dialogId, "action", "continue"));
    call("19", "tracedialog-finish", "tracedialog", params("id", dialogId, "action", "finish"));
    call("20", "no-query", "query", params());
    call("21", "unknown-widget", "manipulate", params("id", "nope", "bindings", "{}"));

    // The time limit: lowered from the server's 60 s so the run stays short. With Config.CHEEPRJ
    // (set by start()) the evaluation has to stay on this thread, so no executor thread may appear.
    long saved = Config.SERVER_REQUEST_TIMEOUT_SECONDS;
    Config.SERVER_REQUEST_TIMEOUT_SECONDS = 5;
    int threadsBefore = Thread.activeCount();
    query("22", "timeout", "While(True)");
    int threadsAfter = Thread.activeCount();
    Config.SERVER_REQUEST_TIMEOUT_SECONDS = saved;
    SUMMARY.append("22 threads before=").append(threadsBefore).append(" after=")
        .append(threadsAfter).append(" CHEEPRJ=").append(Config.CHEEPRJ).append('\n');
    dumpThreads();

    // the extra entry points: an unknown endpoint, bad parameters, and reset
    call("23", "unknown-endpoint", "nope", params());
    timed("24", "bad-params", () -> NotebookKernel.call("query", "not json"));
    timed("25", "reset", NotebookKernel::reset);
    query("26", "after-reset", "f(3)");

    // One representative call per library the slim jar set leaves out (see the exclusions in
    // pom.xml), to see how each fails: a clean fallback, an unevaluated result, or an error.
    // matheclipse-dataset (and fastutil, icu4j, poi, arrow, ...):
    query("P01", "dataset", "Dataset({<|\"a\"->1,\"b\"->2|>,<|\"a\"->3,\"b\"->4|>})");
    query("P02", "semanticimport", "SemanticImportString(\"a,b\\n1,2\\n3,4\")");
    // choco-solver (and trove4j, automaton, sizeof, args4j): the last resort of Solve over Integers
    query("P03", "solve-int-circle", "Solve(x^2+y^2==25,{x,y},Integers)");
    query("P04", "solve-int-linear", "Solve(3*x+5*y==7&&0<x<10&&-10<y<10,{x,y},Integers)");
    query("P05", "solve-int-system", "Solve({x+y+z==6,x*y*z==6,x>0,y>0,z>0},{x,y,z},Integers)");
    query("P06", "solve-int-ineq", "Solve(x^2+2*y^2<=6&&x>=0&&y>=0,{x,y},Integers)");
    query("P07", "findinstance-int", "FindInstance(x^3+y^3==z^3+1&&x>0&&y>0&&z>0,{x,y,z},Integers)");
    // jgrapht-io (kept: GraphExpr cannot load without it) - graph drawing, export and import
    query("P08", "graph", "Graph({1->2,2->3,3->1})");
    query("P09", "graph-export-dot", "ExportString(Graph({1->2,2->3}),\"DOT\")");
    query("P10", "graph-import-dot", "ImportString(\"digraph G { 1 -> 2; 2 -> 3; }\",\"DOT\")");
    // trove4j: the image functions run on boofcv, whose ddogleg dependency names trove4j
    query("P12", "image", "img=Image(Table(Mod(i*j,7)/7,{i,24},{j,24}))");
    query("P13", "image-gradient", "GradientFilter(img,1)");
    query("P14", "image-dilation", "Dilation(img,1)");
    query("P15", "image-adjust", "ImageAdjust(img)");
    // the quiet console of BrowserLogging: the cell keeps "file missing.csv not found!", while the
    // console gets one ERROR line without the FileNotFoundException stack (checked in the output)
    query("P16", "import-missing", "Import(\"missing.csv\")");
    // matheclipse-compile (Janino): Compile itself, and numerical functions that may compile
    query("P17", "compile", "cf=Compile({x},x^2+1); cf(3)");
    query("P18", "nintegrate", "NIntegrate(Sin(x)^2,{x,0,Pi})");
    query("P19", "findroot", "FindRoot(Cos(x)==x,{x,1})");
    query("P20", "ndsolve", "NDSolve({y'(x)==y(x),y(0)==1},y,{x,0,1})");
    query("P21", "plot3d", "Plot3D(Sin(x*y),{x,0,3},{y,0,3})");
    // twelvemonkeys ImageIO plugins: reading a JPEG goes through javax.imageio
    query("P22", "jpeg-roundtrip", "ImageDimensions(ImportString(ExportString(img,\"JPG\"),\"JPG\"))");
    // after a failure the session must still answer
    query("P11", "after-probes", "1+1");

    Files.write(out.resolve("SUMMARY.txt"), SUMMARY.toString().getBytes(StandardCharsets.UTF_8));
    System.out.print(SUMMARY);
    // TraceDialog and log4j leave daemon and non-daemon threads behind; the run is over
    System.exit(0);
  }

  /** A query cell, as the page posts it. */
  private static String query(String nn, String name, String source) throws Exception {
    return call(nn, name, "query", params("query", source));
  }

  /** One request through {@link NotebookKernel#call(String, String)}. */
  private static String call(String nn, String name, String endpoint, Map<String, String> p)
      throws Exception {
    String paramsJSON = JSON.writeValueAsString(p);
    return timed(nn, name, () -> NotebookKernel.call(endpoint, paramsJSON));
  }

  /** One download; the bytes go to <code>NN-name.bin</code>, a missing file is noted. */
  private static void download(String nn, String name, String file) throws Exception {
    long t = System.currentTimeMillis();
    byte[] bytes = NotebookKernel.download(file);
    long ms = System.currentTimeMillis() - t;
    if (bytes != null) {
      Files.write(out.resolve(nn + "-" + name + ".bin"), bytes);
    }
    SUMMARY.append(nn).append(' ').append(name).append(' ').append(ms).append(" ms ")
        .append(bytes == null ? "null (404)" : bytes.length + " bytes").append(" name=")
        .append(file).append('\n');
  }

  /** What a timed request does. */
  @FunctionalInterface
  private interface Request {
    String run() throws Exception;
  }

  /** Run a request, time it, and write its body to <code>NN-name.json</code>. */
  private static String timed(String nn, String name, Request request) throws Exception {
    long t = System.currentTimeMillis();
    String body = request.run();
    long ms = System.currentTimeMillis() - t;
    Files.write(out.resolve(nn + "-" + name + ".json"), body.getBytes(StandardCharsets.UTF_8));
    SUMMARY.append(nn).append(' ').append(name).append(' ').append(ms).append(" ms ")
        .append(body.length()).append(" chars on ").append(Thread.currentThread().getName())
        .append('\n');
    return body;
  }

  /** Parameters from name, value pairs. */
  private static Map<String, String> params(String... pairs) {
    Map<String, String> p = new LinkedHashMap<String, String>();
    for (int i = 0; i + 1 < pairs.length; i += 2) {
      p.put(pairs[i], pairs[i + 1]);
    }
    return p;
  }

  /**
   * The first <code>"id"</code> string anywhere in a JSON answer: the widget, live cell or dialog
   * id the next request names.
   */
  private static String firstId(String body) throws Exception {
    String id = findId(JSON.readTree(body));
    return id == null ? "no-id" : id;
  }

  private static String findId(JsonNode node) {
    if (node == null) {
      return null;
    }
    if (node.isObject()) {
      JsonNode id = node.get("id");
      if (id != null && id.isTextual()) {
        return id.textValue();
      }
      for (Iterator<JsonNode> it = node.elements(); it.hasNext();) {
        String found = findId(it.next());
        if (found != null) {
          return found;
        }
      }
    } else if (node.isArray()) {
      for (JsonNode child : node) {
        String found = findId(child);
        if (found != null) {
          return found;
        }
      }
    } else if (node.isTextual() && node.textValue().startsWith("{")) {
      // some answers carry their widget as a JSON text inside a string
      try {
        return findId(JSON.readTree(node.textValue()));
      } catch (Exception notJson) {
        return null;
      }
    }
    return null;
  }

  /** The names of the live threads, to show which ones the kernel started. */
  private static void dumpThreads() {
    SUMMARY.append("threads:");
    for (Thread thread : Thread.getAllStackTraces().keySet()) {
      SUMMARY.append(' ').append(thread.getName());
    }
    SUMMARY.append('\n');
  }
}
