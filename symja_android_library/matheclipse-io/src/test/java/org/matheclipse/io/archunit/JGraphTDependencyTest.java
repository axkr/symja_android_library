package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * JGraphT belongs to <code>matheclipse-graphtheory</code> and nowhere else.
 *
 * <p>
 * It used to be a direct dependency of <code>matheclipse-core</code>, reaching as far as
 * <code>IExpr</code> itself: <code>IExpr.isListOfEdges()</code> returned an
 * <code>org.jgrapht.GraphType</code>, which made the graph library a compile-time dependency of
 * every Symja expression. That method now returns the core enum
 * {@link org.matheclipse.core.interfaces.EdgeListType}, and the graph module maps it onto JGraphT
 * in one place.
 *
 * <p>
 * The other direction — core needing to format or export a graph it can no longer name — goes
 * through {@link org.matheclipse.core.interfaces.IGraphExpr}, so <code>WL</code>,
 * <code>OutputFunctions</code>, <code>Export</code>, <code>ExportString</code>, the servlets and
 * the API server all work against the interface rather than the implementation.
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class JGraphTDependencyTest {

  @ArchTest
  public static final ArchRule noJGraphTOutsideGraphTheoryModule = noClasses().that() //
      .resideOutsideOfPackage("org.matheclipse.graphtheory..") //
      .should().dependOnClassesThat().resideInAPackage("org.jgrapht..") //
      .because("JGraphT is the private dependency of the matheclipse-graphtheory module");
}
