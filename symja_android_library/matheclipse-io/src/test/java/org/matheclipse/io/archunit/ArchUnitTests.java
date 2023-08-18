package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

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
  public static final ArchRule expression_data_access = classes().that() //
      .resideInAPackage("org.matheclipse.core.expression.data..") //
      .should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.builtin..",
          "org.matheclipse.core.expression..", "org.matheclipse.core.interface..");

  @ArchTest
  public static final ArchRule expression_access =
      classes().that().resideInAPackage("org.matheclipse.core.expression..")
          .and(new DescribedPredicate<JavaClass>("") {

            @Override
            public boolean test(JavaClass arg0) {
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
      .should().onlyBeAccessed().byAnyPackage("org.logicng..", //
          "..core.builtin..", //
          "..core.expression.data..");

  // JGraphT library
  // TODO reduce package dependencies
  @ArchTest
  public static final ArchRule jGraphT = classes().that().resideInAPackage("org.jgrapht..") //
      .should().onlyBeAccessed().byAnyPackage( //
          "org.jgrapht..", //
          "org.chocosolver..", //
          "..io.builtin..", //
          "..core.builtin..", //
          "..core.expression", //
          "..core.expression.data", //
          "..core.reflection.system..");

  @ArchTest
  public static final ArchRule apfloatRule = classes().that().resideInAPackage("org.apfloat..") //
      .should().onlyBeAccessed().byAnyPackage( //
          "org.apfloat..", //
          "org.jgrapht..", //
          "..core.builtin..", //
          "..core.convert..", //
          "..core.eval..", //
          "..core.expression", //
          "..core.form..", //
          "..core.interfaces", //
          "..io.form.mathml", //
          "..io.others", //
          "..io.system");


  /**
   * Don't use a log4j Logger in Config startup methods
   * 
   */
  @ArchTest
  public static final ArchRule configClass = noClasses().that().haveSimpleName("Config") //
      .should().dependOnClassesThat().haveFullyQualifiedName("org.apache.logging.log4j.LogManager");


  // @ArchTest
  // // method added with API level 31
  // public static final ArchRule noLongValueExactMethodOnAndroid = noClasses().should()
  // .callMethod(java.math.BigInteger.class, "longValueExact", new Class<?>[0]);
  //
  // @ArchTest
  // // method added with API level 31
  // public static final ArchRule noIntValueExactMethodOnAndroid =
  // noClasses().should().callMethod(java.math.BigInteger.class, "intValueExact", new Class<?>[0]);

  @ArchTest
  public static final ArchRule noSystemLoggerOnAndroid =
      noClasses().should().callMethod(java.lang.System.class, "getLogger",
          new Class<?>[] {java.lang.String.class});

  @ArchIgnore
  @ArchTest
  public static final ArchRule cycleDependency = slices().matching("..matheclipse.(*)..") //
      .should().beFreeOfCycles();
}
