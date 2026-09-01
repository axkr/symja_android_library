package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * <code>matheclipse-core</code> and the modules built on it must not use the fastutil library any
 * more: the collection types they need are implemented in
 * <code>org.matheclipse.external.fastutil</code> in the <code>matheclipse-external</code> module,
 * so that the 6.6 MB <code>it.unimi.dsi:fastutil-core</code> artifact is no longer a dependency.
 * Only the vendored <code>tech.tablesaw</code> copy in <code>matheclipse-dataset</code> still uses
 * fastutil, which is why <code>org.matheclipse.io..</code> can be listed here now that the fork has
 * moved out of it.
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class FastutilDependencyTest {

  @ArchTest
  public static final ArchRule noFastutilOutsideTablesaw = noClasses().that() //
      .resideInAnyPackage("org.matheclipse.core..", "org.matheclipse.gpl..",
          "org.matheclipse.image..", "org.matheclipse.io..") //
      .should().dependOnClassesThat().resideInAPackage("it.unimi.dsi..") //
      .because("the slim replacements in org.matheclipse.external.fastutil are used instead");
}
