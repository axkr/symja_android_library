package org.matheclipse.core.system.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import junit.framework.TestCase;

public class ArchUnitTests extends TestCase {
	public ArchUnitTests() {
		super("ArchUnitTests");
	}

	public void testConsoleMethods() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse.core.eval");

		ArchRule rule = methods().that().areDeclaredIn("org.matheclipse.core.eval.Console").//
				and().doNotHaveName("main").//
				should().notBePublic().andShould().notBeProtected();
		rule.check(importedClasses);
	}

	public void testMMAConsoleMethods() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse.core.eval");

		ArchRule rule = methods().that().areDeclaredIn("org.matheclipse.core.eval.MMAConsole").//
				and().doNotHaveName("main").//
				should().notBePublic().andShould().notBeProtected();
		rule.check(importedClasses);
	}

	public void testAST() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

		ArchRule myRule = classes().that().resideInAPackage("org.matheclipse.core.expression..").//
				and().haveSimpleName("AST"). //
				// TODO change dependency from "org.matheclipse.core.convert"
				should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

		myRule.check(importedClasses);
	}

	public void testAST0() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

		ArchRule myRule = classes().that().resideInAPackage("org.matheclipse.core.expression..").//
				and().haveSimpleName("AST0"). //
				should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

		myRule.check(importedClasses);
	}

	public void testAST1() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

		ArchRule myRule = classes().that().resideInAPackage("org.matheclipse.core.expression..").//
				and().haveSimpleName("AST1"). //
				should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

		myRule.check(importedClasses);
	}

	public void testAST2() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

		ArchRule myRule = classes().that().resideInAPackage("org.matheclipse.core.expression..").//
				and().haveSimpleName("AST2"). //
				should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

		myRule.check(importedClasses);
	}

	public void testAST3() {
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org.matheclipse");

		ArchRule myRule = classes().that().resideInAPackage("org.matheclipse.core.expression..").//
				and().haveSimpleName("AST3"). //
				should().onlyBeAccessed().byAnyPackage("org.matheclipse.core.expression..");

		myRule.check(importedClasses);
	}

	public void testLogicNG() {
		// LogicNG library
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org");

		ArchRule myRule = classes().that().resideInAPackage("org.logicng..").//
				should().onlyBeAccessed().byAnyPackage("org.logicng..", "..core.builtin..");

		myRule.check(importedClasses);
	}

	public void testJGraphT() {
		// JGraphT library
		// TODO reduce package dependencies
		JavaClasses importedClasses = new ClassFileImporter().importPackages("org");

		ArchRule myRule = classes().that().resideInAPackage("org.jgrapht..").//
				should().onlyBeAccessed().byAnyPackage(//
						"org.jgrapht..", //
						"..core.builtin..", //
						"..core.expression", //
						"..core.expression.data", //
						"..core.reflection.system..");

		myRule.check(importedClasses);
	}

}
