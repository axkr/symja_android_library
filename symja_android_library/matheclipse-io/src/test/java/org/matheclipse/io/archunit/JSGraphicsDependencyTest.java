package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * The JavaScript graphics libraries - JSXGraph, Apache ECharts, MathCell and the pages that load
 * them from a CDN - belong to <code>matheclipse-jsgraphics</code>.
 *
 * <p>
 * <code>matheclipse-core</code> declares the <code>JSXGraph</code>, <code>ECharts</code> and
 * <code>MathCell</code> symbols and the {@link org.matheclipse.core.form.output.JSPageProvider}
 * interface; the module registers the evaluators and installs the provider. The servlets show a
 * <code>JSFormData</code> result through that interface only, so a deployment can leave the module
 * out of its war and the servlets still load.
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class JSGraphicsDependencyTest {

  @ArchTest
  public static final ArchRule coreDoesNotDependOnJSGraphics = noClasses().that() //
      .resideInAPackage("org.matheclipse.core..") //
      .should().dependOnClassesThat().resideInAPackage("org.matheclipse.jsgraphics..") //
      .because("matheclipse-core reaches the JavaScript pages only through JSPageProvider");

  @ArchTest
  public static final ArchRule servletsDoNotDependOnJSGraphics = noClasses().that() //
      .resideInAPackage("org.matheclipse.io.servlet..") //
      .should().dependOnClassesThat().resideInAPackage("org.matheclipse.jsgraphics..") //
      .because("the servlets must still load when matheclipse-jsgraphics is left out; "
          + "IOInit is the one place that names the module");

  /**
   * Self-check for the rules above: a <code>noClasses()</code> rule also passes when the module
   * isn't on the analyzed classpath at all. This asserts the module is there and installs its
   * pages the way the rules assume.
   */
  @ArchTest
  public static final ArchRule jsGraphicsModuleIsInScope = classes().that() //
      .haveFullyQualifiedName("org.matheclipse.jsgraphics.JSGraphicsInit") //
      .should().dependOnClassesThat()
      .haveFullyQualifiedName("org.matheclipse.core.form.output.JSPageProvider") //
      .because("matheclipse-jsgraphics has to be on the analyzed classpath for the rules above "
          + "to mean anything");
}
