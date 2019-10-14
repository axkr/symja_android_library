package org.matheclipse.core.system.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
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
}
