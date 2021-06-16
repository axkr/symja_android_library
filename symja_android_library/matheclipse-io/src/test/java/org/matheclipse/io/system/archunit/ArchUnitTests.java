package org.matheclipse.io.system.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import org.junit.runner.RunWith;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "org")
public class ArchUnitTests {

  @ArchTest
  public static final ArchRule consoleMethods = methods().that() //
      .areDeclaredIn(org.matheclipse.io.eval.Console.class).and().doNotHaveName("main") //
      .should().notBePublic().andShould().notBeProtected();

  @ArchTest
  public static final ArchRule MMAConsoleMethods = methods().that() //
      .areDeclaredIn(org.matheclipse.io.eval.MMAConsole.class).and().doNotHaveName("main") //
      .should().notBePublic().andShould().notBeProtected();

  @ArchIgnore
  @ArchTest
  public static final ArchRule expession_data_access = classes().that() //
      .resideInAPackage("org.matheclipse.core.expression.data..") //
      .should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

  @ArchTest
  public static final ArchRule expession_access =
      classes().that().resideInAPackage("org.matheclipse.core.expression..")
          .and(new DescribedPredicate<JavaClass>("") {
            @Override
            public boolean apply(JavaClass arg0) {
              return arg0.getSimpleName().equals("AST") || arg0.getSimpleName().equals("ExprField")
                  || arg0.getSimpleName().equals("ExprID");
            }
          })
          // TODO change dependency from "org.matheclipse.core.convert"
          .should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

  @ArchTest
  public static final ArchRule AST0 = classes().that() //
      .resideInAPackage("org.matheclipse.core.expression..").and().haveSimpleName("AST0") //
      .should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

  @ArchTest
  public static final ArchRule AST1 = classes().that() //
      .resideInAPackage("org.matheclipse.core.expression..").and().haveSimpleName("AST1") //
      .should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

  @ArchTest
  public static final ArchRule AST2 = classes().that() //
      .resideInAPackage("org.matheclipse.core.expression..").and().haveSimpleName("AST2") //
      .should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

  @ArchTest
  public static final ArchRule AST3 = classes().that() //
      .resideInAPackage("org.matheclipse.core.expression..").and().haveSimpleName("AST3") //
      .should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

  /**
   * <p>
   * Split in packages <code>org.matheclipse.core..</code> and
   * <code>org.matheclipse.parser..</code>. <code>org.matheclipse.parser..</code> should not call
   * Symja <code>IExpr</code> object hierarchy.
   * </p>
   * <b>Note:</b>The <code>ExprParser</code> in package <code>org.matheclipse.core.parser..</code>
   * is allowed to call Symja <code>IExpr</<code> object hierarchy.
   */
  @ArchTest
  public static final ArchRule noIExprInParser = classes().that() //
      .haveSimpleName("IExpr").or().haveSimpleName("IAST") //
      .should().onlyBeAccessed().byAnyPackage("org.matheclipse..");

  // LogicNG library
  @ArchTest
  public static final ArchRule LogicNG = classes().that() //
      .resideInAPackage("org.logicng..") //
      .should().onlyBeAccessed().byAnyPackage("org.logicng..", "..core.builtin..");

  // JGraphT library
  // TODO reduce package dependencies
  @ArchTest
  public static final ArchRule jGraphT = classes().that().resideInAPackage("org.jgrapht..") //
      .should().onlyBeAccessed().byAnyPackage( //
          "org.jgrapht..", //
          "..io.builtin..", //
          "..core.builtin..", //
          "..core.expression", //
          "..core.expression.data", //
          "..core.reflection.system..");

  @ArchIgnore
  @ArchTest
  public static final ArchRule cycleDependency = slices().matching("..matheclipse.(*)..") //
      .should().beFreeOfCycles();
}
