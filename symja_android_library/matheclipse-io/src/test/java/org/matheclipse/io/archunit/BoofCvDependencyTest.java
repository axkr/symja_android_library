package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * BoofCV belongs to <code>matheclipse-image</code> and nowhere else.
 *
 * <p>
 * <code>matheclipse-core</code> in particular must stay free of it. Core owns the built-in symbol
 * table, so it declares <code>Dilation</code>, <code>GaussianFilter</code>, <code>EdgeDetect</code>
 * and the rest of the image processing symbols, but their evaluators live in
 * <code>matheclipse-image</code> - the same arrangement <code>matheclipse-chem</code> uses for CDK
 * and <code>matheclipse-astro</code> for Orekit.
 *
 * <p>
 * The second rule is narrower and about the module's own shape: everything that touches BoofCV goes
 * through <code>org.matheclipse.image.algo</code>, so that the conversion between
 * <code>ImageExpr</code> and BoofCV's image types - and the sample range and band order that come
 * with it - is written down once rather than once per built-in.
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class BoofCvDependencyTest {

  @ArchTest
  public static final ArchRule noBoofCvOutsideImageModule = noClasses().that() //
      .resideOutsideOfPackage("org.matheclipse.image..") //
      .should().dependOnClassesThat().resideInAnyPackage( //
          "boofcv..", //
          "georegression..", //
          "org.ddogleg..") //
      .because("BoofCV is the private dependency of the matheclipse-image module");

  @ArchTest
  public static final ArchRule boofCvOnlyThroughTheAlgoPackage = noClasses().that() //
      .resideOutsideOfPackage("org.matheclipse.image.algo") //
      .should().dependOnClassesThat().resideInAnyPackage( //
          "boofcv..", //
          "georegression..", //
          "org.ddogleg..") //
      .because("org.matheclipse.image.algo.Boof is the single bridge to BoofCV's image types");

  /**
   * The TwelveMonkeys plugins extend <code>javax.imageio</code> through the <code>ServiceLoader</code>.
   * Referencing one of their classes directly would tie code to a plugin that is only ever meant to
   * be discovered.
   */
  @ArchTest
  public static final ArchRule twelveMonkeysStaysAServiceProvider = noClasses() //
      .should().dependOnClassesThat().resideInAPackage("com.twelvemonkeys..") //
      .because("the extra ImageIO readers are discovered, never called directly");
}
