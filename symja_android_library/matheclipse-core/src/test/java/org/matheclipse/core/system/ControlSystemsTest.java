package org.matheclipse.core.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the linear control system functions: <code>TransferFunctionModel</code>, its poles,
 * zeros and responses, the conversions between continuous and sampled models and the
 * frequency-response plots.
 */
public class ControlSystemsTest extends ExprEvaluatorTestCase {

  /** The JUnit setup method */
  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testTransferFunctionModelNormalForm() {
    check("TransferFunctionModel((s+3)/(s^2+5*s+7), s)", //
        "TransferFunctionModel({{{3+s}},7+5*s+s^2},s)");
    // different denominators give a matrix of them
    check("TransferFunctionModel({{1/(s+4), (s+1)/s}}, s)", //
        "TransferFunctionModel({{{1,1+s}},{{4+s,s}}},s)");
    // a shared denominator is written once
    check("TransferFunctionModel({{2/(s+5)},{s/(s+5)}}, s)", //
        "TransferFunctionModel({{{2},{s}},5+s},s)");
    check("TransferFunctionModel(1/(s+1)-1/(s+3), s)", //
        "TransferFunctionModel({{{2}},(1+s)*(3+s)},s)");
    // nothing is cancelled
    check("TransferFunctionModel((s^2-4)/(s^2+5*s+6), s)", //
        "TransferFunctionModel({{{-4+s^2}},6+5*s+s^2},s)");
    check("TransferFunctionModel(a/(s+b), s)", //
        "TransferFunctionModel({{{a}},b+s},s)");
    check("TransferFunctionModel({{3}}, s, SamplingPeriod->0.2)", //
        "TransferFunctionModel({{{3}},1},s,SamplingPeriod->0.2)");
    // the normal form stays as it is
    check("TransferFunctionModel({{{3}},1}, s, SamplingPeriod->0.2)", //
        "TransferFunctionModel({{{3}},1},s,SamplingPeriod->0.2)");
    check("TransferFunctionModel({{{1,s}},{{2+s,4+s}}}, s)", //
        "TransferFunctionModel({{{1,s}},{{2+s,4+s}}},s)");
  }

  @Test
  public void testTransferFunctionModelDelay() {
    // a delay stays a factor of the numerator
    check("TransferFunctionModel(E^(-2*s)*(s+1)/(s+4), s)", //
        "TransferFunctionModel({{{(1+s)/E^(2*s)}},4+s},s)");
    // an inexact delay is written exactly
    check("TransferFunctionModel(4/(E^(0.5*s)*(2.0+s)), s)", //
        "TransferFunctionModel({{{4.0/E^(s/2)}},2.0+s},s)");
    check("SystemsModelDelay(1/2)", //
        "SystemsModelDelay(1/2)");
  }

  @Test
  public void testTransferFunctionPolesZeros() {
    check("TransferFunctionPoles(TransferFunctionModel((s+2)/(s^2+2*s+5), s))", //
        "{{{-1-I*2,-1+I*2}}}");
    check("TransferFunctionZeros(TransferFunctionModel((s+2)/(s^2+2*s+5), s))", //
        "{{{-2}}}");
    // repeated by multiplicity
    check("TransferFunctionPoles(TransferFunctionModel(1/((s+2)^2*(s+5)), s))", //
        "{{{-2,-2,-5}}}");
    check("TransferFunctionZeros(TransferFunctionModel({{(s-3)/(s+1), 2/(s+1)}}, s))", //
        "{{{3},{}}}");
    check("TransferFunctionPoles(TransferFunctionModel(E^(-s)/(s+7), s))", //
        "{{{-7}}}");
    check("TransferFunctionPoles(TransferFunctionModel(1/(s^2+0.5*s+2.0), s))", //
        "{{{-0.25+I*(-1.39194),-0.25+I*1.39194}}}");
  }

  @Test
  public void testOutputResponseSymbolic() {
    check("OutputResponse(TransferFunctionModel(2/(s+2), s), 1, tt)", //
        "{2*(1/2-1/(2*E^(2*tt)))}");
    // an input switched on at 0
    check("OutputResponse(TransferFunctionModel(2/(s+2), s), UnitStep(tt), tt)", //
        "{2*(1/2-1/(2*E^(2*tt)))*UnitStep(tt)}");
    check("OutputResponse(TransferFunctionModel(2/(s+2), s), DiracDelta(tt), tt)", //
        "{(2*HeavisideTheta(tt))/E^(2*tt)}");
    // one input per column
    check("OutputResponse(TransferFunctionModel({{1/(s+1), 1/(s+2)}}, s), {1, Sin(tt)}, tt)", //
        "{1+1/(5*E^(2*tt))-1/E^tt+1/5*(-Cos(tt)+2*Sin(tt))}");
    // a delay can't be simulated exactly
    check("OutputResponse(TransferFunctionModel(E^(-3*s)/(s+2), s), Sin(tt), tt)", //
        "OutputResponse(TransferFunctionModel({{{E^(-3*s)}},2+s},s),Sin(tt),tt)");
  }

  @Test
  public void testOutputResponseTimeSpan() {
    // without a delay the closed form
    check("rr=OutputResponse(TransferFunctionModel(1/(s+2), s), Sin(tt), {tt,0,4}); "
        + "{Head(rr[[1,0]]), Round(rr[[1]] /. tt->1.0, 10.0^-6)}", //
        "{Symbol,0.255595}");
    // a delay is simulated
    check("rr=OutputResponse(TransferFunctionModel(E^(-s)/(s+2), s), 1, {tt,0,5}); "
        + "{Head(rr[[1,0]]), Round(Table(rr[[1]] /. tt->x, {x, {0.5, 1.0, 3.0}}), 10.0^-6)}", //
        "{InterpolatingFunction,{0.0,0.0,0.490842}}");
    check("rr=OutputResponse(TransferFunctionModel(E^(-1/2*s)*4/(s^2+2*s+4), s), Sin(tt), {tt,0,8}); "
        + "Chop(Table((rr[[1]] /. tt->x) - (InverseLaplaceTransform(E^(-s/2)*4/(s^2+2*s+4)/(s^2+1), s, tt) /. tt->x), "
        + "{x, {0.3, 1.7, 4.2, 7.9}}), 10^-7)", //
        "{0,0,0,0}");
    // an input switched on later, and a model with two outputs
    check("rr=OutputResponse(TransferFunctionModel({{E^(-s)/(s+1)},{(s+2)/(s+3)}}, s), UnitStep(tt-1), {tt,0,5}); "
        + "{Length(rr), Round(rr[[1]] /. tt->3.0, 10.0^-6), Round(rr[[2]] /. tt->3.0, 10.0^-6)}", //
        "{2,0.632121,0.667493}");
  }

  @Test
  public void testToDiscreteTimeModel() {
    // the bilinear transform is the default
    check("ToDiscreteTimeModel(TransferFunctionModel(1/(s+3), s), 0.2)", //
        "TransferFunctionModel({{{0.2+0.2*z}},-1.4+2.6*z},z,SamplingPeriod->0.2)");
    check("ToDiscreteTimeModel(TransferFunctionModel(1/(s+3), s), 0.2, Method->\"ForwardRectangularRule\")", //
        "TransferFunctionModel({{{1}},-2.0+5.0*z},z,SamplingPeriod->0.2)");
    check("ToDiscreteTimeModel(TransferFunctionModel(1/(s+3), s), 0.2, w, Method->\"BackwardRectangularRule\")", //
        "TransferFunctionModel({{{0.2*w}},-1.0+1.6*w},w,SamplingPeriod->0.2)");
    check("ToDiscreteTimeModel(TransferFunctionModel(1/(s+3), s), 0.2, Method->\"Unknown\")", //
        "ToDiscreteTimeModel(TransferFunctionModel({{{1}},3+s},s),0.2,Method->Unknown)");
  }

  @Test
  public void testToContinuousTimeModel() {
    // the bilinear transform is undone
    check("ToContinuousTimeModel(ToDiscreteTimeModel(TransferFunctionModel(1/(s+3), s), 1/5), s)", //
        "TransferFunctionModel({{{4/5}},12/5+4/5*s},s,SamplingPeriod->None)");
    check("ToContinuousTimeModel(TransferFunctionModel(1/(z-1/2), z, SamplingPeriod->1/4), s, "
        + "Method->\"ForwardRectangularRule\")", //
        "TransferFunctionModel({{{1}},1/2+s/4},s,SamplingPeriod->None)");
    check("m=ToContinuousTimeModel(TransferFunctionModel(0.5/(z-0.8), z, SamplingPeriod->0.1), s); "
        + "{Round(CoefficientList(m[[1,1,1,1]], s), 10.0^-10), Round(CoefficientList(m[[1,2]], s), 10.0^-10), m[[3]]}", //
        "{{1.0,-0.05},{0.4,0.18},SamplingPeriod->None}");
  }

  @Test
  public void testFrequencyResponsePlots() {
    check("b=BodePlot(TransferFunctionModel((s+10)/(s^2+s+100), s)); {Head(b), Dimensions(b[[1]]), Head(b[[1,1,1]])}", //
        "{Grid,{2,1},Graphics}");
    check("{Head(NyquistPlot(2/(s^2+3*s+2), s)), Head(NicholsPlot(TransferFunctionModel(E^(-s/2)*5/(s+2), s))), "
        + "Head(BodePlot({TransferFunctionModel(1/(s+3), s), TransferFunctionModel(4/(s^2+s+4), s)}, {0.01, 10})), "
        + "Head(BodePlot(TransferFunctionModel(0.5/(z-0.8), z, SamplingPeriod->0.1)))}", //
        "{Graphics,Graphics,Grid,Grid}");
    check("StringQ(ExportString(BodePlot(TransferFunctionModel((s+10)/(s^2+s+100), s)), \"SVG\"))", //
        "True");
    check("StringQ(ExportString(NyquistPlot(2/(s^2+3*s+2), s), \"SVG\"))", //
        "True");
    check("StringQ(ExportString(NicholsPlot(TransferFunctionModel(E^(-s/2)*5/(s+2), s)), \"SVG\"))", //
        "True");
    check("BodePlot(x, y)", //
        "BodePlot(x,y)");
  }
}
