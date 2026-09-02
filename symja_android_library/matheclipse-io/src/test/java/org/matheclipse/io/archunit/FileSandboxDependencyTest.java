package org.matheclipse.io.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * File names that come from user input are resolved by
 * <code>org.matheclipse.core.io.FileSandbox</code>, never by the built-in itself.
 *
 * <p>
 * Without that rule this erodes one commit at a time: <code>Config.isFileSystemEnabled</code> is
 * checked in some twenty five places in <code>FileFunctions</code> alone, and the built-ins open
 * files at around forty sites. A single new <code>new File(userSuppliedName)</code> is enough to
 * put a hole in the servlets' per-session directory, and nothing else would notice.
 *
 * <p>
 * The rule covers the packages the built-in evaluators live in. The engine's own I/O is
 * deliberately outside it and must stay outside it: the startup tables in
 * <code>org.matheclipse.core.expression</code>, Kryo serialization, the home directory, the
 * combinatorics caches, the consoles and the servlets reading their own resources are the kernel
 * acting for itself, not on a name a user typed.
 *
 * <p>
 * <code>ConstantDefinitions</code> is excluded for that reason: <code>$BaseDirectory</code>,
 * <code>$HomeDirectory</code>, <code>$RootDirectory</code> and <code>$UserBaseDirectory</code>
 * report the kernel's own directories, read out of system properties. They open nothing and there
 * is no user supplied name to resolve. (That they <i>name</i> host directories at all is a
 * separate question for a server, and not one a path resolver can answer.)
 */
@AnalyzeClasses(packages = "org.matheclipse")
public class FileSandboxDependencyTest {

  @ArchTest
  public static final ArchRule builtInsDoNotOpenFilesThemselves = noClasses().that() //
      .resideInAnyPackage( //
          "org.matheclipse.core.builtin", //
          "org.matheclipse.core.reflection.system", //
          "org.matheclipse.io.builtin", //
          "org.matheclipse.dataset.builtin") //
      // the nested evaluators are ConstantDefinitions$$BaseDirectory and friends, so this matches
      // on the fully qualified name rather than the simple one
      .and().haveNameNotMatching("org\\.matheclipse\\.core\\.builtin\\.ConstantDefinitions.*") //
      .should().callConstructor(java.io.File.class, String.class) //
      .because("org.matheclipse.core.io.FileSandbox resolves the names that come from user input");

  /**
   * <code>java.nio</code> is the other way in. <code>Path.of</code> and <code>Paths.get</code> build
   * the path that <code>Files.readString</code> and friends then open, so they are the call to
   * catch.
   */
  @ArchTest
  public static final ArchRule builtInsDoNotBuildPathsThemselves = noClasses().that() //
      .resideInAnyPackage( //
          "org.matheclipse.core.builtin", //
          "org.matheclipse.core.reflection.system", //
          "org.matheclipse.io.builtin", //
          "org.matheclipse.dataset.builtin") //
      // the nested evaluators are ConstantDefinitions$$BaseDirectory and friends, so this matches
      // on the fully qualified name rather than the simple one
      .and().haveNameNotMatching("org\\.matheclipse\\.core\\.builtin\\.ConstantDefinitions.*") //
      .should().callMethod(java.nio.file.Paths.class, "get", String.class, String[].class) //
      .because("org.matheclipse.core.io.FileSandbox resolves the names that come from user input");
}
