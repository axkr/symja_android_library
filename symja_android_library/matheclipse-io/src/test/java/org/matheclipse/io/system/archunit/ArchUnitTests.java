package org.matheclipse.io.system.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import org.matheclipse.core.expression.F;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import junit.framework.TestCase;

public class ArchUnitTests extends TestCase {
  public ArchUnitTests() {
    super("ArchUnitTests");
  }

  public void testConsoleMethods() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse.io.eval");

    ArchRule rule =
        methods()
            .that()
            .areDeclaredIn("org.matheclipse.io.eval.Console")
            .and()
            .doNotHaveName("main")
            .should()
            .notBePublic()
            .andShould()
            .notBeProtected();
    rule.check(importedClasses);
  }

  public void testMMAConsoleMethods() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse.io.eval");

    ArchRule rule =
        methods()
            .that()
            .areDeclaredIn("org.matheclipse.io.eval.MMAConsole")
            .and()
            .doNotHaveName("main")
            .should()
            .notBePublic()
            .andShould()
            .notBeProtected();
    rule.check(importedClasses);
  }

//  public void test_expession_data_access() {
//    JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");
//
//    ArchRule myRule =
//        classes()
//            .that()
//            .resideInAPackage("org.matheclipse.core.expression.data..")
//            .should()
//            .onlyBeAccessed()
//            .byAnyPackage("org.matheclipse.core.expression..");
//
//    myRule.check(importedClasses);
//  }

  public void test_expession_access() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("org.matheclipse.core.expression..")
            .and(
                new DescribedPredicate<JavaClass>("") {
                  @Override
                  public boolean apply(JavaClass arg0) {
                    return arg0.getSimpleName().equals("AST")
                        || arg0.getSimpleName().equals("ExprField")
                        || arg0.getSimpleName().equals("ExprID");
                  }
                })
            // TODO change dependency from "org.matheclipse.core.convert"
            .should()
            .onlyBeAccessed()
            .byAnyPackage("org.matheclipse.core.expression..");

    myRule.check(importedClasses);
  }

  public void testAST0() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("org.matheclipse.core.expression..")
            .and()
            .haveSimpleName("AST0")
            .should()
            .onlyBeAccessed()
            .byAnyPackage("org.matheclipse.core.expression..");

    myRule.check(importedClasses);
  }

  public void testAST1() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("org.matheclipse.core.expression..")
            .and()
            .haveSimpleName("AST1")
            .should()
            .onlyBeAccessed()
            .byAnyPackage("org.matheclipse.core.expression..");

    myRule.check(importedClasses);
  }

  public void testAST2() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("org.matheclipse.core.expression..")
            .and()
            .haveSimpleName("AST2")
            .should()
            .onlyBeAccessed()
            .byAnyPackage("org.matheclipse.core.expression..");

    myRule.check(importedClasses);
  }

  public void testAST3() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("org.matheclipse.core.expression..")
            .and()
            .haveSimpleName("AST3")
            .should()
            .onlyBeAccessed()
            .byAnyPackage("org.matheclipse.core.expression..");

    myRule.check(importedClasses);
  }

  /**
   * <p>
   * Split in packages <code>org.matheclipse.core..</code> and <code>org.matheclipse.parser..</code>.
   * <code>org.matheclipse.parser..</code> should not call Symja <code>IExpr</code> object hierarchy.
   * </p>
   * <b>Note:</b>The <code>ExprParser</code> in package <code>org.matheclipse.core.parser..</code> is allowed to call
   * Symja <code>IExpr</<code> object hierarchy.
   */
  public void testNoIExprInParser() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

    ArchRule rule =
        classes()
            .that()
            .haveSimpleName("IExpr")
            .or()
            .haveSimpleName("IAST")
            .should()
            .onlyBeAccessed()
            .byAnyPackage( //
                "org.matheclipse..");
    rule.check(importedClasses);
  }

  public void testLogicNG() {
    // LogicNG library
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org");

    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("org.logicng..")
            .should()
            .onlyBeAccessed()
            .byAnyPackage("org.logicng..", "..core.builtin..");

    myRule.check(importedClasses);
  }

  public void testJGraphT() {
    // JGraphT library
    // TODO reduce package dependencies
    JavaClasses importedClasses = new ClassFileImporter().importPackages("org");

    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("org.jgrapht..")
            .should()
            .onlyBeAccessed()
            .byAnyPackage( //
                "org.jgrapht..", //
                "..io.builtin..", //
                "..core.builtin..", //
                "..core.expression", //
                "..core.expression.data", //
                "..core.reflection.system..");

    myRule.check(importedClasses);
  }

  // public void testCycleDependency() {
  // JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");
  // ArchRule myRule = slices().matching("..matheclipse.(*)..").should().beFreeOfCycles();
  // myRule.check(importedClasses);
  // }

}
