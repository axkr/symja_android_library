package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.PrintStream;
import java.io.StringWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.WriterOutputStream;
import org.matheclipse.core.interfaces.ISymbol;

/** Tests for SparseArray functions */
public class ConstantTest extends ExprEvaluatorTestCase {

  @Test
  public void testCharcterEncoding() {
    check("$CharacterEncoding", //
        "UTF-8");
  }

  @Test
  public void testIn() {
    check("x=1", //
        "1");
    check("x = x + 1", //
        "2");
    check("Do(In(2), {3})", //
        "");
    check("x", //
        "5");
    check("In(-1)", //
        "5");
    check("Definition(In)", //
        "Attributes(In)={Listable,NHoldFirst,Protected}\n" //
            + "\n" //
            + "In(1):=x=1\n" //
            + "\n" //
            + "In(2):=x=x + 1\n" //
            + "\n" //
            + "In(3):=Do(In(2),{3})\n" //
            + "\n" //
            + "In(4):=x\n" //
            + "\n" //
            + "In(5):=In(-1)");
  }

  @Test
  public void testLine() {
    check("$Line", //
        "1");
    check("$Line", //
        "2");
  }

  @Test
  public void testIterationLimit() {
    check("f(x_) := f(x + 1)", //
        "");
    check("f(x)", //
        "Hold(f(x))");
  }

  @Test
  public void testLimitInBlock() {
    // the message names the limit of the Block, not the global one restored after it
    check("Do(Evaluate(Symbol(\"lib\" <> ToString(k))) = "
        + "Symbol(\"lib\" <> ToString(k + 1)), {k, 0, 29})", //
        "");
    assertTrue(messages("Block({$IterationLimit = 20}, lib0)") //
        .contains("Iteration limit of 20 exceeded"));
    check("lib0", //
        "lib30");
    check("lir(n_) := If(n == 0, 0, 1 + lir(n - 1))", //
        "");
    assertTrue(messages("Block({$RecursionLimit = 30}, lir(100))") //
        .contains("Recursion depth of 30 exceeded"));
    check("lir(100)", //
        "100");
  }

  @Test
  public void testInfiniteLimits() {
    check("Block({$IterationLimit = Infinity}, $IterationLimit)", //
        "Infinity");
    check("Block({$RecursionLimit = Infinity}, $RecursionLimit)", //
        "Infinity");
    check("$IterationLimit == Infinity", //
        "False");
  }

  private String messages(String input) {
    EvalEngine engine = evaluator.getEvalEngine();
    PrintStream previous = engine.getErrorPrintStream();
    StringWriter errorWriter = new StringWriter();
    PrintStream errors = new PrintStream(new WriterOutputStream(errorWriter));
    engine.setErrorPrintStream(errors);
    try {
      evaluator.eval(input);
    } finally {
      errors.flush();
      engine.setErrorPrintStream(previous);
    }
    return errorWriter.toString();
  }

  @Test
  public void testOut() {
    check("42", //
        "42");
    check("%", //
        "42");
    check("43", //
        "43");
    check("%", //
        "43");
    check("44", //
        "44");
    check("%1", //
        "42");
    check("%%", //
        "44");
    check("Hold(Out(-1))", //
        "Hold(%)");
    check("Hold(%4)", //
        "Hold(Out(4))");
    check("Out(0)", //
        "Out(0)");
    check("Sin(Pi);", //
        "");
    check("%", //
        "");
    check("Definition(Out)", "Attributes(Out)={Listable,NHoldFirst,Protected}\n" //
        + "\n" //
        + "Out(2)=42\n" //
        + "\n" //
        + "Out(4)=43\n" //
        + "\n" //
        + "Out(5)=44\n" //
        + "\n" //
        + "Out(6)=42\n" //
        + "\n" //
        + "Out(7)=44\n" //
        + "\n" //
        + "Out(8)=Hold(%)\n" //
        + "\n" //
        + "Out(9)=Hold(Out(4))\n" //
        + "\n" //
        + "Out(10)=Out(0)\n" //
        + "\n" //
        + "Out(11)=Null\n" //
        + "\n" //
        + "Out(12)=Null");
  }

  @Test
  public void testRecursionLimit() {
    // messages $RecursionLimit: Recursion depth of ... exceeded during evaluation of ...
    check("a = a + a", //
        "Hold(a=a+a)");
    check("a", //
        "Hold(a)");

    check("a := a + a", //
        "");
    check("a", //
        "Hold(a)");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    Config.BUILTIN_PROTECTED = ISymbol.PROTECTED;
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    // // dummy eval
    // try {
    // fScriptEngine.eval("");
    // } catch (ScriptException e) {
    // }
    EvalEngine engine = EvalEngine.get();// (EvalEngine) fScriptEngine.get("EVAL_ENGINE");
    engine.setIterationLimit(50000);
    engine.setRecursionLimit(256);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
