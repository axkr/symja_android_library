package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.PrintStream;
import java.io.StringWriter;
import org.junit.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.WriterOutputStream;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class QuietTestCase {
  @Test
  public void test001() {
    final StringWriter outWriter = new StringWriter();
    WriterOutputStream wouts = new WriterOutputStream(outWriter);
    final StringWriter errorWriter = new StringWriter();
    WriterOutputStream werrors = new WriterOutputStream(errorWriter);
    PrintStream outs = new PrintStream(wouts);
    PrintStream errors = new PrintStream(werrors);
    EvalEngine engine = new EvalEngine("", 256, 256, outs, errors, true);
    EvalEngine.set(engine);
    IExpr result = F.eval(
        F.Quiet(F.Times(F.Power(F.num(1000.0), F.ZZ(1000)), F.Power(F.num(1000.0), F.ZZ(-1000)))));
    // try (PrintStream errorPrintStream = engine.getErrorPrintStream()) {
    String message = errorWriter.toString();
    // don't get message: "Infinity: Indeterminate expression 0.0*Infinity encountered."
    assertEquals("", message);
    // }

    engine.setQuietMode(true);
    result = F.eval(F.symjify(1000.0).pow(1000).divide(F.symjify(1000.0).pow(1000)));
    // IExpr result = F.eval("0.0*Infinity");
    System.out.println(result.toString());
    // try (PrintStream errorPrintStream = engine.getErrorPrintStream()) {
    message = errorWriter.toString();
    // don't get message: "Infinity: Indeterminate expression 0.0*Infinity encountered."
    assertEquals("", //
        message);
    // }
  }

  @Test
  public void test002() {
    IExpr result = F.eval(F.Quiet(F.symjify(1000.0).pow(1000).divide(F.symjify(1000.0).pow(1000))));
    assertEquals("Indeterminate", result.toString());
  }

  @Test
  public void test003() {
    IExpr xVar = F.symjify("x");
    IExpr yVar = F.symjify("y");
    IExpr result =
        F.eval(F.Quiet(F.Solve(F.List(F.Equal(xVar.multiply(yVar), F.ZZ(1))), F.List(xVar, yVar))));
    assertEquals("Solve({x*y==1},{x,y})", result.toString());
  }
}
