package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** QuantilePlot, ProbabilityPlot and ProbabilityScalePlot. */
public class ProbabilityPlotTest extends ExprEvaluatorTestCase {

  @Test
  public void testQuantilePlotReferenceLine() {
    // the line through the quartile points runs far beyond the data
    check("Round(Cases(Options(QuantilePlot({2, 4, 5, 7, 11}), Epilog), Line(p_) :> p, "
        + "Infinity), 0.001)", //
        "{{{-2.349,-3.559},{27.754,30.832}}}");
    // two invisible points on it stretch the plot range
    check("Round(Cases(QuantilePlot({2, 4, 5, 7, 11}), Point(p_) :> p, Infinity), 0.001)", //
        "{{{2.349,2.0},{4.325,4.0},{5.8,5.0},{7.275,7.0},{9.251,11.0}},"
            + "{{2.058,1.475},{9.398,9.861}}}");
    // equal values have a horizontal line through them
    check("Cases(QuantilePlot({6, 6}), Point(p_) :> p, Infinity)", //
        "{{{6.0,6.0},{6.0,6.0}},{{6.0,6.0},{6.0,6.0}}}");
    check("Cases(QuantilePlot({6, 6}), Line(p_) :> p, Infinity)", //
        "{{{-6.0,6.0},{18.0,6.0}}}");
    // tied values stay separate points
    check("Cases(QuantilePlot({3, 3, 8}), Point(p_) :> Round(1000*p), Infinity)", //
        "{{{2732,3000},{4667,3000},{6601,8000}},{{2569,2164},{6684,7481}}}");
    // one invisible pair per data set, all on the common range
    check("Cases(QuantilePlot({{1, 4, 6}, {2, 3, 8, 9}}), Point(p_) :> Round(1000*p), "
        + "Infinity)[[3 ;;]]", //
        "{{{1704,716},{8675,11049}},{{1704,-319},{8675,10367}}}");
  }

  @Test
  public void testProbabilityPlot() {
    check("Round(Cases(ProbabilityPlot({2, 4, 5, 7, 11}), Point(p_) :> p, Infinity), 0.001)", //
        "{{{0.107,0.2},{0.278,0.4},{0.397,0.6},{0.653,0.8},{0.955,1.0}},"
            + "{{0.0,0.0},{1.0,1.0}}}");
    // the empirical distribution of a second data set
    check("Cases(ProbabilityPlot({1, 4, 6}, {2, 3, 5, 8}), Point(p_) :> p, Infinity)[[1]]", //
        "{{0.0,0.333333},{0.5,0.666667},{0.75,1.0}}");
    check("Cases(ProbabilityPlot({3, 5, 8}, UniformDistribution({0, 10})), Point(p_) :> p, "
        + "Infinity)[[1]]", //
        "{{0.3,0.333333},{0.5,0.666667},{0.8,1.0}}");
    // the diagonal of the unit square
    check("Round(Cases(Options(ProbabilityPlot({3, 5, 8}), Epilog), Line(p_) :> p, Infinity))", //
        "{{{0,0},{1,1}}}");
    // one point for equal values, at the largest probability; a single value is drawn twice at
    // the probability 1/2 of the fitted distribution; invisible diagonal ends for each data set
    check("Cases(ProbabilityPlot({{1, 3, 3, 6}, {2, 2, 2}}), Point(p_) :> Round(1000*p), "
        + "Infinity)", //
        "{{{104,250},{444,750},{938,1000}},{{500,1000},{500,1000}},{{0,0},{1000,1000}},{{\n"
            + "0,0},{1000,1000}}}");
    check("ProbabilityPlot(abc)", //
        "ProbabilityPlot(abc)");
  }

  @Test
  public void testProbabilityScalePlot() {
    check("s = ProbabilityScalePlot({2, 4, 5, 7, 11}); "
        + "Round(Cases(s, Point(p_) :> p, Infinity)[[1]], 0.001)", //
        "{{2.0,-0.128},{4.0,0.518},{5.0,1.0},{7.0,1.482},{11.0,2.128}}");
    check("Options(s, FrameTicks)", //
        "{FrameTicks->{{Charting`ScaledTicks({NormalY,NormalYInverse}),"
            + "Charting`ScaledFrameTicks({NormalY,NormalYInverse})},{Automatic,Automatic}}}");
    check("Round(Cases(Options(s, Epilog), Line(p_) :> p, Infinity), 0.001)", //
        "{{{1.807,-0.128},{9.693,2.128}}}");
    check("Union(Cases(s, PointSize(r_) :> Round(360*r), Infinity))", //
        "{7}");
    check("ProbabilityScalePlot({2, 4}, \"Gumbel\")", //
        "ProbabilityScalePlot({2,4},Gumbel)");
  }

  @Test
  public void testProbabilityScalePlotWeibull() {
    // {Log(x), Log(-Log(1 - p))} with p = (i - 0.3)/(n + 0.4)
    check("Round(Cases(ProbabilityScalePlot({7, 2, 3}, \"Weibull\"), Point(p_) :> p, "
        + "Infinity)[[1]], 0.001)", //
        "{{0.693,-1.467},{1.099,-0.367},{1.946,0.458}}");
    // a value which is not positive rejects the whole plot with a message
    check("Check(ProbabilityScalePlot({7, -1, 2, 3}, \"Weibull\"), failed)", //
        "failed");
    check("ProbabilityScalePlot({0, 1}, \"Weibull\")", //
        "ProbabilityScalePlot({0,1},Weibull)");
    check("Options(ProbabilityScalePlot({2, 3, 30}, \"Weibull\"), FrameTicks)", //
        "{FrameTicks->{{Charting`ScaledTicks({WeibullY,WeibullYInverse}),"
            + "Charting`ScaledFrameTicks({WeibullY,WeibullYInverse})},"
            + "{Charting`ScaledTicks({Log,Exp}),Charting`ScaledFrameTicks({Log,Exp})}}}");
  }

  @Test
  public void testProbabilityScaleTicksInSVG() {
    // the symbolic ticks are drawn as percent on the vertical and on a log scale horizontally
    check("StringCases(ExportString(ProbabilityScalePlot({2, 3, 30}, \"Weibull\"), \"SVG\"), "
        + "RegularExpression(\">[0-9.]+</text>\"))", //
        "{>2</text>,>5</text>,>10</text>,>20</text>,>25</text>,>50</text>,>75</text>}");
    check("StringContainsQ(ExportString(ProbabilityScalePlot({2, 3, 30, 31, 40}), \"SVG\"), "
        + "\">50</text>\")", //
        "True");
  }
}
