package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * BioJava belongs to <code>matheclipse-bio</code> and nowhere else.
 *
 * <p>
 * It used to be a direct dependency of <code>matheclipse-io</code> (and, vestigially, of
 * <code>matheclipse-md2html</code>, which never imported a single BioJava class). Both were replaced
 * by a dependency on <code>matheclipse-bio</code>, so that <code>org.biojava</code> reaches the rest
 * of the build only transitively, through the one module which is allowed to use it.
 *
 * <p>
 * This rule keeps that separation from rotting. In particular <code>matheclipse-core</code> must
 * stay free of it: core owns the built-in symbol table, so it declares <code>BioSequence</code> and
 * friends as symbols, but their evaluators live in <code>matheclipse-bio</code> — the same
 * arrangement <code>matheclipse-astro</code> uses for Orekit.
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class BiojavaDependencyTest {

  @ArchTest
  public static final ArchRule noBiojavaOutsideBioModule = noClasses().that() //
      .resideOutsideOfPackage("org.matheclipse.bio..") //
      .should().dependOnClassesThat().resideInAPackage("org.biojava..") //
      .because("BioJava is the private dependency of the matheclipse-bio module");
}
