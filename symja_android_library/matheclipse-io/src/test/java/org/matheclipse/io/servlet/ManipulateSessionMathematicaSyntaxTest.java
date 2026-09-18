package org.matheclipse.io.servlet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.ParserConfig;

/**
 * {@link ManipulateSessionTest}'s example read in Wolfram Language syntax, as
 * <code>MMAServletServer</code> reads it.
 *
 * <p>
 * The built-in symbol table is keyed by name and built once per JVM, lower-cased for Symja's syntax
 * and not for this one, so this class runs in a JVM of its own - see the
 * "wolfram-language-syntax" surefire execution in the module's pom.
 */
public class ManipulateSessionMathematicaSyntaxTest {

  static {
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
  }

  @BeforeAll
  public static void beforeAll() {
    F.initSymbols();
    org.matheclipse.astro.AstroInit.init();
  }

  @Test
  public void testSolarSystemWidgetShowsTheMovingPlanets() throws Exception {
    ManipulateSessionTest.checkSolarSystem(
        new EvalEngine("manipulate-test", 256, System.out, false),
        "Manipulate[Graphics3D[{Sphere[#1, Scaled[0.015]] & /@"
            + " (orbitPos[dateAt[baseYear, dayFrac], #1] & /@ Range[8]),"
            + " {Yellow, Sphere[{0, 0, 0}, Scaled[0.02]]}},"
            + " PlotRange -> Exp[4*(zoom - 1)], ImageSize -> {360, 360},"
            + " PlotLabel -> DateString[dateAt[baseYear, dayFrac],"
            + " {\"MonthName\", \" \", \"Year\"}],"
            + " SphericalRegion -> True],"
            + " {{dayFrac, 0, \"day offset\"}, 0, 1, ControlType -> Slider},"
            + " {{baseYear, 2020, \"year\"}, 2020, 2170, 1, ControlType -> Slider},"
            + " {{zoom, 1, \"zoom\"}, 0, 1},"
            + " SaveDefinitions -> True, SynchronousUpdating -> False,"
            + " Initialization :> (dateAt[y_, d_] := DatePlus[{y}, {d, \"Year\"}];"
            + " orbitPos[t_, k_] := AstronomicalData[AstronomicalData[k],"
            + " {\"Position\", t}]/(7*10^12);)]");
  }

  @Test
  public void testReadOutsShowTheValues() throws Exception {
    ManipulateSessionTest.checkReadOuts(
        new EvalEngine("manipulate-test", 256, System.out, false),
        "Manipulate[status = If[target >= 8, \"reached\", \"not yet\"]; target,"
            + " Row[{Control[{{target, 1, \"target\"}, 0, 10}], Style[\" \"],"
            + " Style[Dynamic[target]]}],"
            + " Style[Dynamic[status], Bold, Red], {{status, \"\", \"\"}, ControlType -> None}]");
  }
}
