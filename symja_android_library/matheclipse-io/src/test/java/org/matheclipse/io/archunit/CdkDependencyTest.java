package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * The Chemistry Development Kit belongs to <code>matheclipse-chem</code> and nowhere else.
 *
 * <p>
 * <code>matheclipse-core</code> in particular must stay free of it. Core owns the built-in symbol
 * table, so it declares <code>Molecule</code>, <code>MoleculeValue</code>, <code>ChemicalFormula</code>
 * and their relatives as symbols, but the evaluators live in <code>matheclipse-chem</code> — the
 * same arrangement <code>matheclipse-astro</code> uses for Orekit and <code>matheclipse-bio</code>
 * for BioJava.
 *
 * <p>
 * The companion rule for <code>Export</code>, which lives in core and must still be able to write
 * chemical formats, is the {@code BioSequenceFormat}-style inversion: core declares an interface,
 * the module installs an implementation.
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class CdkDependencyTest {

  @ArchTest
  public static final ArchRule noCdkOutsideChemModule = noClasses().that() //
      .resideOutsideOfPackage("org.matheclipse.chem..") //
      .should().dependOnClassesThat().resideInAPackage("org.openscience..") //
      .because("CDK is the private dependency of the matheclipse-chem module");
}
