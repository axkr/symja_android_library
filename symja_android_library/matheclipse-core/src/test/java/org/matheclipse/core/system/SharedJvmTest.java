package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.S;

/**
 * The built-in symbols are one instance per JVM. In a {@link Config#SHARED_JVM} one engine must not
 * change what another one computes. If the JVM is one kernel, a definition may change a built-in
 * for all of the kernel.
 */
public class SharedJvmTest extends ExprEvaluatorTestCase {

  @AfterEach
  public void leaveSharedMode() {
    Config.SHARED_JVM = false;
  }

  /** The result of <code>input</code> in a new engine on a thread of its own. */
  private static String inNewEngine(String input) throws Exception {
    ExecutorService thread = Executors.newSingleThreadExecutor();
    try {
      Callable<String> task = () -> new ExprEvaluator().eval(input).toString();
      return thread.submit(task).get();
    } finally {
      thread.shutdownNow();
    }
  }

  /** <code>read</code> gives the same in another engine before and after <code>write</code>. */
  private static void assertIsolated(String write, String read) throws Exception {
    String before = inNewEngine(read);
    inNewEngine(write);
    assertEquals(before, inNewEngine(read), write);
  }

  /** Issue #1584: in a kernel this is a definition, which <code>D</code> leaves out. */
  @Test
  public void testDerivativeOfBuiltinInAKernel() {
    try {
      check("Sin' = Tan", //
          "Tan");
      check("{Sin'(x), D(Sin(x), x), D(Sin(x)^2, x), D(Sin(2*x), x)}", //
          "{Tan(x),Cos(x),2*Cos(x)*Sin(x),2*Cos(2*x)}");
      check("Sin' =.", //
          "");
      check("Sin'(x)", //
          "Cos(x)");
    } finally {
      check("Clear(Derivative)", //
          "");
    }
  }

  /** The rule which <code>ArcSin' = ...</code> replaces isn't lost with the definition. */
  @Test
  public void testUnsetKeepsBuiltinRules() {
    try {
      check("ArcSin' = Tan", //
          "Tan");
      check("{ArcSin'(x), D(ArcSin(x), x)}", //
          "{Tan(x),1/Sqrt(1-x^2)}");
      check("ArcSin' =.", //
          "");
      check("ArcSin'(x)", //
          "1/Sqrt(1-x^2)");
      check("Cos' =.", //
          "");
      check("D(Cos(x), x)", //
          "-Sin(x)");
    } finally {
      check("Clear(Derivative)", //
          "");
    }
  }

  @Test
  public void testDerivativeSymbolInAKernel() {
    check("Attributes(Derivative)", //
        "{NHoldAll}");
    try {
      check("Derivative = 5", //
          "5");
      check("D(Sin(x), x)", //
          "Cos(x)");
    } finally {
      check("Clear(Derivative)", //
          "");
    }
    // the derivatives of the built-in functions outlast it
    check("{D(Sin(x), x), D(Log(x), x), Sin'(x)}", //
        "{Cos(x),1/x,Cos(x)}");
  }

  @Test
  public void testDerivativeOfUserFunction() {
    check("sjtf'(x_) := 3*x", //
        "");
    check("{sjtf'(2), D(sjtf(y), y)}", //
        "{6,3*y}");
    // the rule is a rule of Derivative
    check("Clear(sjtf); sjtf'(2)", //
        "6");
    check("sjtg' = Cos", //
        "Cos");
    check("sjtg'(0)", //
        "1");
    check("sjtg' =.", //
        "");
    check("sjtg'(0)", //
        "sjtg'(0)");
    check("Derivative(1, 0)[sjth][x_, y_] := x*y", //
        "");
    check("D(sjth(a, b), a)", //
        "a*b");
  }

  /** One user owns the kernel, and a package may change a built-in for all of it. */
  @Test
  public void testSingleKernelChangesBuiltin() {
    try {
      check("Unprotect(Csch); Csch(sjtk) = 42; Csch(sjtk)", //
          "42");
    } finally {
      check("Csch(sjtk) =.; Protect(Csch)", //
          "{Csch}");
    }
  }

  @Test
  public void testSingleKernelSetOptionsHoldsForTheKernel() throws Exception {
    String before = inNewEngine("Options(Plot, PlotLabel)");
    check("old = Options(Plot, PlotLabel); SetOptions(Plot, PlotLabel -> sjtl); "
        + "Options(Plot, PlotLabel)", //
        "{PlotLabel->sjtl}");
    assertEquals("{PlotLabel->sjtl}", inNewEngine("Options(Plot, PlotLabel)").replace("Global`", ""));
    check("SetOptions(Plot, old); Options(Plot, PlotLabel)", //
        "{PlotLabel->None}");
    assertEquals(before, inNewEngine("Options(Plot, PlotLabel)"));
  }

  @Test
  public void testSharedEngineIsLocked() throws Exception {
    Config.SHARED_JVM = true;
    assertIsolated("Unprotect(Csch); Csch(1) = 42", "Csch(1)");
    assertIsolated("Unprotect(Sech)", "Attributes(Sech)");
    assertIsolated("Sin' = Tan", "{Sin'(x), D(Sin(x), x)}");
    assertIsolated("Derivative = 5", "D(Tan(x), x)");
    assertIsolated("Clear(Derivative)", "D(Tan(x), x)");
    assertIsolated("SetAttributes(Derivative, Listable)", "Attributes(Derivative)");
    assertEquals("sjtq'(2)", inNewEngine("sjtq'(x_) := 1; sjtq'(2)"));
  }

  @Test
  public void testSharedSymbolsTakeNoValue() throws Exception {
    Config.SHARED_JVM = true;
    assertIsolated("System`sjtv = 7", "System`sjtv");
    assertIsolated("Rubi`sjtw = 7", "Rubi`sjtw");
    // a value of the engine, not of the symbol
    assertIsolated("$IterationLimit = 2000", "$IterationLimit");
  }

  @Test
  public void testSharedSetOptionsHoldsForTheEngine() throws Exception {
    Config.SHARED_JVM = true;
    assertIsolated("SetOptions(Plot, PlotLabel -> sjtm)", "Options(Plot, PlotLabel)");
    String[] results = new String[2];
    ExecutorService thread = Executors.newSingleThreadExecutor();
    try {
      thread.submit(() -> {
        ExprEvaluator evaluator = new ExprEvaluator();
        evaluator.eval("SetOptions(Plot, PlotLabel -> sjtn)");
        results[0] = evaluator.eval("Options(Plot, PlotLabel)").toString();
        // a copy works for the same user
        EvalEngine copy = evaluator.getEvalEngine().copy();
        results[1] = new ExprEvaluator(copy, true, (short) 0).eval("Options(Plot, PlotLabel)")
            .toString();
      }).get();
    } finally {
      thread.shutdownNow();
    }
    assertEquals("{PlotLabel->sjtn}", results[0]);
    assertEquals(results[0], results[1]);
  }

  /** Two engines are two kernels, an engine and its copy are one. */
  @Test
  public void testAliasesAndPackagesBelongToTheKernel() throws Exception {
    assertIsolated("$ContextAliases = <|\"sjta`\" -> \"Sjtb`\"|>", "$ContextAliases");
    assertIsolated("BeginPackage(\"Sjtp`\"); EndPackage()", "$Packages");
    String[] results = new String[1];
    ExecutorService thread = Executors.newSingleThreadExecutor();
    try {
      thread.submit(() -> {
        ExprEvaluator evaluator = new ExprEvaluator();
        evaluator.eval("$ContextAliases = <|\"sjta`\" -> \"Sjtb`\"|>");
        EvalEngine copy = evaluator.getEvalEngine().copy();
        results[0] = new ExprEvaluator(copy, true, (short) 0).eval("$ContextAliases").toString();
      }).get();
    } finally {
      thread.shutdownNow();
    }
    assertEquals("<|sjta`->Sjtb`|>", results[0]);
  }

  @Test
  public void testSharedFlagIsOffByDefault() {
    assertTrue(!Config.SHARED_JVM);
    assertTrue(!S.Derivative.hasProtectedAttribute());
    Config.SHARED_JVM = true;
    assertTrue(S.Derivative.hasProtectedAttribute());
  }
}
