package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Nothing draws with JFreeChart any more.
 *
 * <p>
 * <code>matheclipse-image</code> used to carry a second set of plot evaluators that returned a
 * JFreeChart bitmap, and installed them over the ones in <code>matheclipse-core</code>. That made
 * a plot look different depending on whether the module happened to be on the classpath -
 * <code>ArrayPlot</code> in the console was a chart with pixel numbered axes and two colours,
 * while the same call in a core only test was a correctly scaled <code>Graphics</code> - and the
 * bitmap versions understood none of the options.
 *
 * <p>
 * Every plot now renders through <code>org.matheclipse.core.graphics.svg</code>, and
 * <code>Image[...]</code> turns the result into a bitmap for the callers that need one. This rule
 * keeps the second rendering path from growing back.
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class JFreeChartDependencyTest {

  @ArchTest
  public static final ArchRule nothingDependsOnJFreeChart = noClasses() //
      .should().dependOnClassesThat().resideInAnyPackage("org.jfree..") //
      .because("plots are rendered as SVG by matheclipse-core, and rasterized from there");
}
