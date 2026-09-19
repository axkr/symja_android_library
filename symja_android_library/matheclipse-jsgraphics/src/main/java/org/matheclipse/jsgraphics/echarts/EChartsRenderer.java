package org.matheclipse.jsgraphics.echarts;

import static org.matheclipse.jsgraphics.JSWriter.color;
import static org.matheclipse.jsgraphics.JSWriter.finiteRuns;
import static org.matheclipse.jsgraphics.JSWriter.num;
import static org.matheclipse.jsgraphics.JSWriter.opacity;
import static org.matheclipse.jsgraphics.JSWriter.pairs;
import static org.matheclipse.jsgraphics.JSWriter.str;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.form.output.OutputFormats;
import org.matheclipse.core.graphics.svg.GraphicsOptions2D;
import org.matheclipse.core.graphics.svg.LegendRenderer;
import org.matheclipse.core.graphics.svg.Prim2D;
import org.matheclipse.core.graphics.svg.Scene2D;
import org.matheclipse.core.graphics.svg.Style2D;
import org.matheclipse.core.graphics.svg.Viewport2D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.jsgraphics.JSRenderer;

/**
 * Draws a 2D plot as an <a href="https://echarts.apache.org">Apache ECharts</a> chart.
 *
 * <p>
 * ECharts is a charting library, not a drawing one: it takes series of data, so it is offered for
 * what plots are made of - lines, points and labels. Each curve becomes a <code>line</code> series
 * and each point set a <code>scatter</code> series, on value or logarithmic axes spanning the plot
 * range. Shapes such as the bars of a chart or the fill of a plot are refused rather than drawn
 * wrongly.
 */
public final class EChartsRenderer implements JSRenderer {

  @Override
  public String type() {
    return OutputFormats.ECHARTS_STR;
  }

  @Override
  public String unsupported(Scene2D scene) {
    for (List<Prim2D> list : List.of(scene.primitives, scene.prolog, scene.epilog)) {
      for (Prim2D p : list) {
        if (!(p instanceof Prim2D.LinePrim || p instanceof Prim2D.PointsPrim
            || p instanceof Prim2D.TextPrim)) {
          return p.getClass().getSimpleName().replace("Prim", "");
        }
      }
    }
    return null;
  }

  @Override
  public String toJavaScript(Scene2D scene) {
    Viewport2D vp = scene.viewport;
    GraphicsOptions2D options = scene.options;
    String[] legends = legendLabels(options.plotLegends);
    List<String> series = new ArrayList<>();
    int curve = 0;
    for (List<Prim2D> list : List.of(scene.prolog, scene.primitives, scene.epilog)) {
      for (Prim2D p : list) {
        if (p instanceof Prim2D.LinePrim) {
          String name = seriesName(legends, curve++);
          lineSeries((Prim2D.LinePrim) p, name, series);
        } else if (p instanceof Prim2D.PointsPrim) {
          String name = seriesName(legends, curve++);
          pointSeries((Prim2D.PointsPrim) p, name, series);
        } else if (p instanceof Prim2D.TextPrim) {
          textSeries((Prim2D.TextPrim) p, series);
        }
      }
    }

    StringBuilder js = new StringBuilder(4096);
    js.append("var eChart = echarts.init(document.getElementById('main'));\n");
    js.append("var option = {\n");
    if (options.plotLabel != null && !options.plotLabel.isNone()) {
      js.append("  title: { text: ").append(str(labelText(options.plotLabel)))
          .append(", left: 'center' },\n");
    }
    js.append("  tooltip: { trigger: 'item' },\n");
    if (legends != null) {
      js.append("  legend: { bottom: 0 },\n");
    }
    js.append("  grid: { containLabel: true, left: 16, right: 24, top: ")
        .append(options.plotLabel != null ? 40 : 16).append(", bottom: ")
        .append(legends != null ? 36 : 16).append(" },\n");
    js.append("  toolbox: { feature: { dataZoom: {}, restore: {}, saveAsImage: {} } },\n");
    String[] labels = labelPair(options.axesLabel);
    js.append("  xAxis: ").append(axis(vp.isLogX(), vp.rawMinX, vp.rawMaxX, labels[0], options.axesX))
        .append(",\n");
    js.append("  yAxis: ").append(axis(vp.isLogY(), vp.rawMinY, vp.rawMaxY, labels[1], options.axesY))
        .append(",\n");
    js.append("  series: [\n");
    for (int i = 0; i < series.size(); i++) {
      js.append("    ").append(series.get(i)).append(i < series.size() - 1 ? ",\n" : "\n");
    }
    js.append("  ]\n};\n");
    js.append("eChart.setOption(option);\n");
    js.append("window.addEventListener('resize', function () { eChart.resize(); });\n");
    return js.toString();
  }

  /**
   * One axis over the plot range. The range is given in data units, which is what ECharts takes
   * for a logarithmic axis too.
   */
  private static String axis(boolean log, double min, double max, String name, boolean show) {
    StringBuilder a = new StringBuilder("{ type: '").append(log ? "log" : "value")
        .append("', min: ").append(num(min)).append(", max: ").append(num(max))
        .append(", axisLabel: { formatter: function (v) { return +v.toPrecision(6); } }")
        .append(", splitLine: { show: false }");
    if (!show) {
      a.append(", axisLine: { show: false }");
    }
    if (name != null) {
      a.append(", name: ").append(str(name)).append(", nameLocation: 'end'");
    }
    return a.append(" }").toString();
  }

  /**
   * A curve as line series, one per run of finite points. They share the curve's name, so the
   * legend shows a curve broken at a pole once and hides all its pieces together.
   */
  private static void lineSeries(Prim2D.LinePrim p, String name, List<String> series) {
    Style2D s = p.style;
    for (List<double[]> segment : p.segments) {
      for (List<double[]> run : finiteRuns(segment)) {
        if (run.size() < 2) {
          continue;
        }
        StringBuilder a = new StringBuilder("{ type: 'line', name: ").append(str(name))
            .append(", showSymbol: false, animation: false, color: ").append(color(s.strokeColor))
            .append(", lineStyle: { width: ").append(num(Math.max(0.5, s.strokeWidth)))
            .append(", opacity: ").append(num(opacity(s.strokeColor, s)));
        if (!"none".equals(s.dashArray)) {
          a.append(", type: 'dashed'");
        }
        a.append(" }, data: ").append(pairs(run)).append(" }");
        series.add(a.toString());
      }
    }
  }

  private static void pointSeries(Prim2D.PointsPrim p, String name, List<String> series) {
    Style2D s = p.style;
    List<double[]> points = new ArrayList<>(p.points.size());
    for (double[] q : p.points) {
      if (Double.isFinite(q[0]) && Double.isFinite(q[1])) {
        points.add(q);
      }
    }
    if (points.isEmpty()) {
      return;
    }
    series.add("{ type: 'scatter', name: " + str(name) + ", animation: false, symbolSize: "
        + num(Math.max(2, 2 * s.pointRadius)) + ", color: " + color(s.strokeColor)
        + ", itemStyle: { opacity: " + num(opacity(s.strokeColor, s)) + " }, data: "
        + pairs(points) + " }");
  }

  /** A label, as an invisible point that shows its text. */
  private static void textSeries(Prim2D.TextPrim p, List<String> series) {
    if (!Double.isFinite(p.x) || !Double.isFinite(p.y)) {
      return;
    }
    Style2D s = p.style;
    String position = p.offsetY >= 0.5 ? "bottom" : p.offsetY <= -0.5 ? "top" : "inside";
    series.add("{ type: 'scatter', silent: true, symbolSize: 0, animation: false, tooltip: { show: false }, data: [["
        + num(p.x) + ", " + num(p.y) + "]], label: { show: true, position: '" + position
        + "', color: " + color(s.strokeColor) + ", fontSize: " + num(s.fontSize)
        + ", formatter: function () { return " + str(p.text) + "; } } }");
  }

  private static String seriesName(String[] legends, int i) {
    if (legends != null && i < legends.length) {
      return legends[i];
    }
    return "curve " + (i + 1);
  }

  /** The labels the <code>PlotLegends</code> of the picture give its curves, or <code>null</code>. */
  private static String[] legendLabels(IExpr legends) {
    List<String> labels = LegendRenderer.labels(legends);
    return labels.isEmpty() ? null : labels.toArray(new String[0]);
  }

  private static String[] labelPair(IExpr expr) {
    String[] out = new String[2];
    if (expr == null) {
      return out;
    }
    if (expr.isList() && expr.argSize() >= 2) {
      out[0] = nullIfBlank(((IAST) expr).arg1());
      out[1] = nullIfBlank(((IAST) expr).arg2());
    } else {
      out[0] = nullIfBlank(expr);
    }
    return out;
  }

  private static String nullIfBlank(IExpr expr) {
    return expr.isNone() || expr.isAutomatic() ? null : labelText(expr);
  }

  private static String labelText(IExpr expr) {
    String s = expr.toString();
    return s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")
        ? s.substring(1, s.length() - 1)
        : s;
  }
}
