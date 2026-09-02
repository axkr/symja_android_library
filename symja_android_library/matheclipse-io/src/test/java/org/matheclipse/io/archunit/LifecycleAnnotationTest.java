package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import org.junit.jupiter.api.BeforeEach;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * A <code>setUp</code> method that overrides an annotated one has to be annotated again.
 *
 * <p>
 * JUnit 5 does not carry a method level lifecycle annotation onto an overriding method, so a
 * subclass that writes
 *
 * <pre>
 * &#64;Override
 * public void setUp() {
 *   super.setUp();
 *   ...
 * }
 * </pre>
 *
 * over a <code>&#64;BeforeEach</code> method silently gets no setup at all - neither its own method
 * nor the one it overrides. Nothing fails at that point; the tests fail later, on whatever the setup
 * would have created, which is how <code>ExportImportFunctionsJUnit</code> spent a long time
 * throwing <code>NullPointerException</code> on a null script engine.
 *
 * <p>
 * The rule is scoped to <code>org.matheclipse.io..</code>, where every test class derives from a
 * <code>&#64;BeforeEach</code> base. Elsewhere the convention differs and the rule would not hold:
 * <code>ExprEvaluatorTestCase</code> in <code>matheclipse-core</code>, and the copies of it in
 * <code>matheclipse-compile</code> and <code>matheclipse-graphtheory</code>, call
 * <code>setUp()</code> from their constructor instead, and the Rubi suites under
 * <code>org.matheclipse.core.rubi</code> are JUnit 3 classes where <code>setUp</code> is a naming
 * convention rather than an annotation.
 */
@AnalyzeClasses(packages = "org.matheclipse.io", importOptions = ImportOption.DoNotIncludeJars.class)
public class LifecycleAnnotationTest {

  /**
   * <code>setUp</code> without parameters and without <code>static</code>: that leaves out the
   * <code>setUp(ISymbol)</code> hook every Symja evaluator has, which is unrelated to JUnit, and the
   * <code>&#64;BeforeAll</code> methods, which are static.
   */
  @ArchTest
  public static final ArchRule setUpIsAnnotated = methods().that() //
      .haveName("setUp") //
      .and().haveRawParameterTypes(new Class<?>[0]) //
      .and().areNotStatic() //
      .and().areDeclaredInClassesThat().resideInAPackage("org.matheclipse.io..") //
      .should().beAnnotatedWith(BeforeEach.class) //
      .because("JUnit 5 does not inherit @BeforeEach onto an overriding method, so an unannotated"
          + " setUp() means no setup runs at all");
}
