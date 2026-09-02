package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * The Janino runtime compiler and the JavaPoet source generator belong to
 * <code>matheclipse-compile</code> and nowhere else.
 *
 * <p>
 * <code>matheclipse-core</code> in particular must stay free of them. Core owns the built-in symbol
 * table, so it declares <code>Compile</code>, <code>CompiledFunction</code> and
 * <code>CompilePrint</code> as symbols, but the evaluators live in <code>matheclipse-compile</code>
 * - the same arrangement <code>matheclipse-astro</code> uses for Orekit and
 * <code>matheclipse-chem</code> for CDK.
 *
 * <p>
 * This is not only tidiness: generating and loading bytecode at runtime is not available on Android,
 * and <code>symja_android_library</code>'s core is meant to run there.
 *
 * <p>
 * The companion rule for core's numerical functions, which must still be able to use a compiled
 * function when the module happens to be present, is the {@code BioSequenceFormat}-style inversion:
 * core declares {@link org.matheclipse.core.compile.IExprCompiler}, the module installs an
 * implementation.
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class JaninoDependencyTest {

  @ArchTest
  public static final ArchRule noJavaCompilerOutsideCompileModule = noClasses().that() //
      .resideOutsideOfPackage("org.matheclipse.compile..") //
      .should().dependOnClassesThat().resideInAnyPackage( //
          "org.codehaus.janino..", //
          "org.codehaus.commons.compiler..", //
          "com.squareup.javapoet..") //
      .because("the Java source generator and the runtime Java compiler are the private "
          + "dependencies of the matheclipse-compile module");

  /**
   * Self-check for the rule above: a <code>noClasses()</code> rule also passes when the module it
   * exempts isn't on the analyzed classpath at all, which would make the rule above vacuously
   * green. This asserts the opposite direction, so the exemption is known to be doing work.
   */
  @ArchTest
  public static final ArchRule compileModuleIsInScopeAndUsesJanino = classes().that() //
      .haveFullyQualifiedName("org.matheclipse.compile.builtin.CompilerFunctions") //
      .should().dependOnClassesThat().resideInAPackage("org.codehaus.janino..") //
      .because("matheclipse-compile has to be on the analyzed classpath for the rule above to "
          + "mean anything");
}
