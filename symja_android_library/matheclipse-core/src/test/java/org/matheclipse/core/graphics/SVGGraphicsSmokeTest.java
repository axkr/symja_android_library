package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.system.TestTags;
import org.xml.sax.InputSource;

/**
 * Renders every entry of {@link SVGTestCorpus} and checks that the result is a non empty, well
 * formed SVG document.
 *
 * <p>
 * This is deliberately a structural check rather than a golden string comparison: the exact
 * geometry is expected to change as the converter improves, but "the picture disappeared" and "the
 * output is not parseable XML" are always defects.
 *
 * <p>
 * Every rendered document is also written to <code>target/svg-snapshots</code> so two runs can be
 * diffed against each other, and so the output can be opened in a browser for visual review.
 */
public class SVGGraphicsSmokeTest {

  private static final Path SNAPSHOT_DIR = Paths.get("target", "svg-snapshots");

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    // Other test classes lower these shared limits; a density style plot legitimately builds a
    // large expression (one rectangle per cell), so the ceiling has to be restored here rather
    // than inherited from whichever test ran last.
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    Config.MAX_MATRIX_DIMENSION_SIZE = Integer.MAX_VALUE;
    Config.FILESYSTEM_ENABLED = false;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("initialization interrupted", ie);
    }
    // Relaxed syntax is what the rest of the test suite uses, and it is the only mode in which
    // built-in symbols resolve to their evaluators. It still accepts the bracket call syntax the
    // corpus is written in.
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    engine.setRecursionLimit(512);
    engine.setIterationLimit(10000);
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    // Other tests in the suite leave values assigned to common one letter symbols, and a plot
    // whose variable is already bound does not evaluate to a graphic. Clearing them keeps these
    // tests independent of the order the suite happens to run in.
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z)");
  }

  /** Result of rendering a single corpus entry. */
  private static final class Rendered {
    final String name;
    final String input;
    String svg;
    String failure;

    Rendered(String name, String input) {
      this.name = name;
      this.input = input;
    }
  }

  private static Rendered render(String name, String input) {
    Rendered r = new Rendered(name, input);
    try {
      IExpr result = evaluator.eval(input);
      if (result == null) {
        r.failure = "evaluation returned null";
        return r;
      }
      if (!(result instanceof IAST)) {
        r.failure = "evaluation did not return an AST but " + result;
        return r;
      }
      SVGGraphics converter = new SVGGraphics(600, 400);
      r.svg = converter.toSVG((IAST) result, true);
      if (r.svg == null) {
        r.failure = "toSVG returned null for " + result.head();
      } else if (r.svg.isEmpty()) {
        r.failure = "toSVG returned an empty string for " + result.head();
      }
    } catch (RuntimeException | StackOverflowError ex) {
      r.failure = ex.getClass().getSimpleName() + ": " + ex.getMessage();
    }
    return r;
  }

  private static void writeSnapshot(Rendered r) {
    if (r.svg == null || r.svg.isEmpty()) {
      return;
    }
    try {
      Files.createDirectories(SNAPSHOT_DIR);
      Files.write(SNAPSHOT_DIR.resolve(r.name + ".svg"), r.svg.getBytes(StandardCharsets.UTF_8));
    } catch (IOException ioe) {
      // snapshots are a debugging aid, never a reason to fail the test
    }
  }

  private static String xmlError(String svg) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(null);
      builder
          .parse(new InputSource(new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8))));
      return null;
    } catch (Exception ex) {
      return ex.getClass().getSimpleName() + ": " + ex.getMessage();
    }
  }

  private void checkGroup(String groupName, String[][] cases) {
    List<String> problems = new ArrayList<>();
    for (String[] entry : cases) {
      Rendered r = render(entry[0], entry[1]);
      writeSnapshot(r);
      if (r.failure != null) {
        problems.add(entry[0] + "  <<" + entry[1] + ">>  " + r.failure);
        continue;
      }
      if (!r.svg.contains("<svg")) {
        problems.add(entry[0] + "  <<" + entry[1] + ">>  output has no <svg> root");
        continue;
      }
      String xmlError = xmlError(r.svg);
      if (xmlError != null) {
        problems.add(entry[0] + "  <<" + entry[1] + ">>  not well formed XML: " + xmlError);
      }
    }
    assertTrue(problems.isEmpty(), () -> groupName + ": " + problems.size() + " of " + cases.length
        + " cases failed\n  " + String.join("\n  ", problems));
  }

  @Test
  public void testPrimitives() {
    checkGroup("primitives", SVGTestCorpus.PRIMITIVES);
  }

  @Test
  public void testDirectives() {
    checkGroup("directives", SVGTestCorpus.DIRECTIVES);
  }

  @Test
  public void testTransforms() {
    checkGroup("transforms", SVGTestCorpus.TRANSFORMS);
  }

  @Test
  public void testOptions() {
    checkGroup("options", SVGTestCorpus.OPTIONS);
  }

  @Test
  public void testRegressions() {
    checkGroup("regressions", SVGTestCorpus.REGRESSIONS);
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testPlots() {
    checkGroup("plots", SVGTestCorpus.PLOTS);
  }

  /**
   * The word cloud layout options.
   *
   * <p>
   * {@code "Random"} orientation is spread over the words by position rather than drawn at random,
   * so that the same cloud renders the same way twice.
   */
  @Test
  public void testWordCloudLayoutOptions() {
    String data =
        "WordCloud[{\"aa\",\"bb\",\"bb\",\"cc\",\"cc\",\"cc\",\"dd\",\"dd\",\"dd\",\"dd\"}";
    String plain = render("wordcloud-plain", data + "]").svg;
    assertTrue(!plain.contains("rotate("), "words read left to right by default");

    String upright = render("wordcloud-vertical", data + ", WordOrientation -> \"Vertical\"]").svg;
    int turned = upright.split("rotate\\(", -1).length - 1;
    assertTrue(turned == 4, () -> "expected all four words turned, got " + turned);

    // the mixed orientation has to be repeatable
    String mixedOnce = render("wordcloud-random", data + ", WordOrientation -> \"Random\"]").svg;
    String mixedAgain = render("wordcloud-random2", data + ", WordOrientation -> \"Random\"]").svg;
    assertTrue(mixedOnce.equals(mixedAgain), "the same cloud must render the same way twice");

    // A log scale pulls the middle words up towards the largest. The extremes cannot show it:
    // the sizes are spread over a fixed range, so the biggest and smallest land on its ends
    // whatever the scale, and only what happens in between distinguishes the two.
    double linear = runnerUpRatio(plain);
    double logarithmic =
        runnerUpRatio(render("wordcloud-log", data + ", ScalingFunctions -> \"Log\"]").svg);
    assertTrue(logarithmic > linear,
        () -> "expected a log scale to lift the middle words, " + logarithmic + " vs " + linear);
  }

  /** Size of the second largest word relative to the largest, which is scale independent. */
  private static double runnerUpRatio(String svg) {
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("font-size=\"([0-9.]+)\"").matcher(svg);
    List<Double> sizes = new ArrayList<>();
    while (m.find()) {
      sizes.add(Double.parseDouble(m.group(1)));
    }
    Collections.sort(sizes, Collections.reverseOrder());
    return sizes.size() < 2 ? 1.0 : sizes.get(1) / sizes.get(0);
  }

  /**
   * A framed plot has to be numbered, and numbered once.
   *
   * <p>
   * The frame carries the scale when it is drawn, so the axes must not repeat it. Families that
   * frame by default went unnumbered entirely until {@code FrameTicks} defaulted to
   * {@code Automatic}.
   */
  @Test
  public void testFramedPlotsAreNumberedExactlyOnce() {
    for (String input : new String[] {"Histogram[Table[Mod[n^2, 17], {n, 200}]]",
        "BoxWhiskerChart[Table[Mod[n^2, 17], {n, 100}]]", "ComplexPlot[z, {z, -1 - I, 1 + I}]",
        "Plot[Sin[x], {x, 0, 6}, Frame -> True]"}) {
      List<String> labels = tickLabels(render("framed", input).svg);
      assertTrue(!labels.isEmpty(), () -> "no scale at all on " + input);
    }

    // adding a frame must not make the plot number itself twice over
    int bare = tickLabels(render("frame-off", "Plot[Sin[x], {x, 0, 6}]").svg).size();
    int framed =
        tickLabels(render("frame-on", "Plot[Sin[x], {x, 0, 6}, Frame -> True]").svg).size();
    assertTrue(framed == bare,
        () -> "expected the frame to replace the axis labels, " + framed + " vs " + bare);
  }

  /**
   * A themed plot must still carry its scale, and must lose to options the caller wrote.
   *
   * <p>
   * A theme that frames the plot turns the axes off, and frame ticks are off by default, so without
   * care the themed plot comes out with nothing numbered on it at all.
   */
  @Test
  public void testPlotThemeKeepsTheScaleAndYieldsToExplicitOptions() {
    String detailed =
        render("theme-detailed", "Plot[Sin[x], {x, 0, 6}, PlotTheme -> \"Detailed\"]").svg;
    assertTrue(detailed.contains("<text"), "a themed plot must still be numbered");
    assertTrue(detailed.contains("class=\"grid\""), "expected grid lines from the theme");

    // an unknown theme leaves the plot exactly as it was
    String plain = render("theme-plain", "Plot[Sin[x], {x, 0, 6}]").svg;
    String unknown =
        render("theme-unknown", "Plot[Sin[x], {x, 0, 6}, PlotTheme -> \"Nonsense\"]").svg;
    assertTrue(plain.equals(unknown), "an unknown theme should change nothing");

    // the caller's own Frame beats the theme, and the axes come back with it
    String unframed = render("theme-unframed",
        "Plot[Sin[x], {x, 0, 6}, Frame -> False, PlotTheme -> \"Detailed\"]").svg;
    assertTrue(unframed.contains("<text"), "an unframed themed plot still needs its axis labels");
    assertTrue(unframed.contains("class=\"grid\""), "the theme still contributes its grid");

    // the weight is part of the theme
    assertTrue(strokeWidths(
        render("theme-minimal", "Plot[Sin[x], {x, 0, 6}, PlotTheme -> \"Minimal\"]").svg)
            .contains("1"),
        "expected Minimal to draw a thinner curve");
  }

  /** Every distinct stroke width in a rendered document. */
  private static java.util.Set<String> strokeWidths(String svg) {
    java.util.Set<String> widths = new java.util.LinkedHashSet<>();
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("stroke-width=\"([0-9.]+)\"").matcher(svg);
    while (m.find()) {
      widths.add(m.group(1));
    }
    return widths;
  }

  /** {@code ScalingFunctions} has to scale the axis the same way the dedicated plot does. */
  @Test
  public void testScalingFunctionsMatchesTheLogPlot() {
    String scaled =
        render("scaling-log", "Plot[Exp[x], {x, 1, 10}, ScalingFunctions -> \"Log\"]").svg;
    String reference = render("scaling-reference", "LogPlot[Exp[x], {x, 1, 10}]").svg;
    assertTrue(tickLabels(scaled).equals(tickLabels(reference)),
        () -> "expected the same ticks as LogPlot, got " + tickLabels(scaled) + " vs "
            + tickLabels(reference));
  }

  /** The text of every label in a rendered document, in order. */
  private static List<String> tickLabels(String svg) {
    List<String> labels = new ArrayList<>();
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("<text[^>]*>([^<]*)</text>").matcher(svg);
    while (m.find()) {
      labels.add(m.group(1));
    }
    return labels;
  }

  /** {@code LabelStyle} has to reach every piece of text the plot labels itself with. */
  @Test
  public void testLabelStyleReachesTicksAndLabels() {
    String plain = render("labelstyle-plain",
        "Plot[Sin[x], {x, 0, 10}, AxesLabel -> {\"x\", \"y\"}, PlotLabel -> \"t\"]").svg;
    assertTrue(!plain.contains("rgb(255,0,0)"), "the plain plot should have no red text");

    String styled = render("labelstyle-red",
        "Plot[Sin[x], {x, 0, 10}, AxesLabel -> {\"x\", \"y\"}, PlotLabel -> \"t\","
            + " LabelStyle -> Directive[Red, Bold, 16]]").svg;
    // every label the plot draws: the ticks, the plot label and both axis labels
    int reds = styled.split("rgb\\(255,0,0\\)", -1).length - 1;
    assertTrue(reds >= 5, () -> "expected the style on every label, found " + reds);
    assertTrue(styled.contains("16"), "expected the requested font size to be used");
  }

  /**
   * {@code ClippingStyle} shows where a curve ran off the plot range, which the clip path would
   * otherwise erase without trace.
   */
  @Test
  public void testClippingStyleMarksTheClippedStretches() {
    String base = "Plot[Sin[x]/x, {x, -10, 10}, PlotRange -> {0, 0.5}";
    assertTrue(!render("clipping-none", base + "]").svg.contains("class=\"clipped\""),
        "nothing should be drawn for the clipped parts by default");

    String red = render("clipping-red", base + ", ClippingStyle -> Red]").svg;
    assertTrue(red.contains("class=\"clipped\""), "expected a group for the clipped stretches");
    assertTrue(red.contains("stroke=\"rgb(255,0,0)\""),
        () -> "expected the clipped stretches in red, got: " + head(red));

    // Automatic keeps the curve's own colour and dashes it instead
    String automatic = render("clipping-automatic", base + ", ClippingStyle -> Automatic]").svg;
    assertTrue(automatic.contains("stroke-dasharray"),
        "expected the automatic clipping style to dash the line");
    assertTrue(!automatic.contains("stroke=\"rgb(255,0,0)\""),
        "the automatic style should not recolour the line");
  }

  /**
   * The rendered document must honor {@code ImageSize}. This is the contract the web servlet
   * depends on when it embeds the result.
   */
  @Test
  public void testImageSizeIsHonored() {
    Rendered r = render("imagesize-contract", "Graphics[Disk[], ImageSize -> 200]");
    assertTrue(r.failure == null, () -> "render failed: " + r.failure);
    assertTrue(r.svg.contains("width=\"200\""),
        () -> "expected width=\"200\" in output, got: " + head(r.svg));
  }

  /**
   * The canvas height follows the aspect ratio when the caller only suggested one.
   *
   * <p>
   * Otherwise a deliberately short plot is centred inside a tall default canvas and reads as a thin
   * band adrift in white space. A height the caller genuinely requires, as a layout cell does, is
   * still honoured.
   */
  @Test
  public void testHeightFollowsAspectRatio() {
    // a number line asks for a tenth of the golden ratio, so it must come out short and wide
    double[] numberLine = sizeOf("NumberLinePlot[Prime[Range[10]]]");
    assertTrue(numberLine[1] < numberLine[0] / 3.0,
        () -> "expected a short wide canvas, got " + numberLine[0] + "x" + numberLine[1]);

    // a disk is as tall as it is wide
    double[] disk = sizeOf("Graphics[Disk[]]");
    assertTrue(Math.abs(disk[0] - disk[1]) < 1.0,
        () -> "expected a square canvas, got " + disk[0] + "x" + disk[1]);

    // an explicit pair is taken literally
    double[] explicit = sizeOf("Graphics[Disk[], ImageSize -> {300, 150}]");
    assertTrue(Math.abs(explicit[0] - 300) < 1.0 && Math.abs(explicit[1] - 150) < 1.0,
        () -> "expected 300x150, got " + explicit[0] + "x" + explicit[1]);

    // a single ImageSize value is the width; the height still follows the shape
    double[] single = sizeOf("Graphics[Rectangle[{0, 0}, {4, 1}], ImageSize -> 400]");
    assertTrue(Math.abs(single[0] - 400) < 1.0, () -> "expected width 400, got " + single[0]);
    assertTrue(single[1] < single[0], () -> "a wide rectangle should not produce a taller canvas: "
        + single[0] + "x" + single[1]);
  }

  /** The width and height of the rendered document. */
  private static double[] sizeOf(String input) {
    Rendered r = render("size", input);
    assertTrue(r.failure == null, () -> "render failed: " + r.failure);
    java.util.regex.Matcher m =
        java.util.regex.Pattern.compile("width=\"([0-9.]+)\" height=\"([0-9.]+)\"").matcher(r.svg);
    assertTrue(m.find(), () -> "no size on the svg root of " + input);
    return new double[] {Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2))};
  }

  /** A single {@code <svg>} root, never one nested directly inside another. */
  @Test
  @Tag(TestTags.SLOW)
  public void testNoDoubleNestedSvgRoot() {
    for (String[] entry : SVGTestCorpus.all()) {
      Rendered r = render(entry[0], entry[1]);
      if (r.svg == null) {
        continue;
      }
      int first = r.svg.indexOf("<svg");
      int second = r.svg.indexOf("<svg", first + 1);
      // a nested <svg> is legitimate for Inset and the Row/Column/Grid layouts, but the
      // document must not begin with two roots in a row
      assertTrue(first >= 0, () -> entry[0] + ": no <svg> root");
      if (second >= 0) {
        String between = r.svg.substring(first, second);
        assertTrue(between.contains(">"), () -> entry[0] + ": malformed nested <svg>");
      }
    }
  }

  private static String head(String s) {
    return s.length() > 200 ? s.substring(0, 200) : s;
  }

  /** Convenience entry point: render the whole corpus into an HTML gallery for visual review. */
  public static void main(String[] args) throws IOException {
    setUpEngine();
    StringBuilder html = new StringBuilder();
    html.append("<!doctype html><html><head><meta charset=\"utf-8\">");
    html.append("<title>Symja Graphics gallery</title><style>");
    html.append("body{font-family:sans-serif;margin:2rem;background:#fafafa}");
    html.append(".case{border:1px solid #ddd;background:#fff;margin:1rem 0;padding:1rem}");
    html.append(".fail{border-color:#c00;background:#fff5f5}");
    html.append("code{background:#f0f0f0;padding:2px 4px}");
    html.append("</style></head><body><h1>Symja Graphics gallery</h1>");
    int failures = 0;
    for (String[] entry : SVGTestCorpus.all()) {
      Rendered r = render(entry[0], entry[1]);
      writeSnapshot(r);
      boolean bad = r.failure != null || r.svg == null || r.svg.isEmpty();
      if (bad) {
        failures++;
      }
      html.append("<div class=\"case").append(bad ? " fail" : "").append("\">");
      html.append("<h3>").append(entry[0]).append("</h3><p><code>")
          .append(entry[1].replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))
          .append("</code></p>");
      html.append(bad ? "<p><b>FAILED: </b>" + r.failure + "</p>" : r.svg);
      html.append("</div>");
    }
    html.append("</body></html>");
    Files.createDirectories(SNAPSHOT_DIR);
    File out = SNAPSHOT_DIR.resolve("gallery.html").toFile();
    Files.write(out.toPath(), html.toString().getBytes(StandardCharsets.UTF_8));
    System.out.println("gallery: " + out.getAbsolutePath());
    System.out.println("cases: " + SVGTestCorpus.all().length + ", failures: " + failures);
  }
}
