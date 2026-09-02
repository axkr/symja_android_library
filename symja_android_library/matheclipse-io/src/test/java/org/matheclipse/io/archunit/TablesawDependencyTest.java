package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Tablesaw belongs to <code>matheclipse-dataset</code> and nowhere else.
 *
 * <p>
 * The <code>tech.tablesaw</code> sources in that module are a fork, not a repackaged release:
 * <code>ExprColumn</code>, <code>ExprColumnType</code> and <code>ColumnType.EXPR</code> are Symja
 * additions that <code>ASTDataset</code> is built on. The fork used to live in
 * <code>matheclipse-io</code> together with the nine dependencies it needs - fastutil,
 * RoaringBitmap, classgraph, jsoup, jackson-datatype-jsr310, json-flattener, poi-ooxml and the two
 * arrow artifacts - which is what this rule keeps from happening again.
 *
 * <p>
 * <code>matheclipse-core</code> owns the <code>Dataset</code> symbol and the
 * {@link org.matheclipse.core.interfaces.IASTDataset} interface; the implementation lives in
 * <code>matheclipse-dataset</code>. That is the same arrangement <code>matheclipse-image</code>
 * uses for BoofCV and <code>matheclipse-chem</code> for CDK, and it is why the servlets talk to a
 * <code>Dataset</code> through <code>IASTDataset</code> rather than through
 * <code>ASTDataset</code>.
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class TablesawDependencyTest {

  @ArchTest
  public static final ArchRule noTablesawOutsideDatasetModule = noClasses().that() //
      .resideOutsideOfPackage("org.matheclipse.dataset..") //
      .should().dependOnClassesThat().resideInAPackage("tech.tablesaw..") //
      .because("Tablesaw is the private dependency of the matheclipse-dataset module");

  /**
   * The <code>Dataset</code> object itself is reached through the core interface, so that
   * <code>matheclipse-io</code> - the servlets in particular - needs no compile time knowledge of
   * the implementation class.
   */
  @ArchTest
  public static final ArchRule astDatasetOnlyUsedInsideTheDatasetModule = noClasses().that() //
      .resideOutsideOfPackage("org.matheclipse.dataset..") //
      .should().dependOnClassesThat()
      .haveFullyQualifiedName("org.matheclipse.dataset.expression.ASTDataset") //
      .because("IASTDataset in matheclipse-core is the contract everything else uses");
}
