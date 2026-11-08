package org.matheclipse.core.system;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** <code>Standardize</code> with location and scale functions which give a list or zero. */
public class StandardizeTest extends ExprEvaluatorTestCase {

  /** Evaluate <code>input</code> and return what was printed as messages. */
  private String messagesOf(String input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream old = evaluator.getEvalEngine().getErrorPrintStream();
    evaluator.getEvalEngine().setErrorPrintStream(new PrintStream(buffer, true));
    try {
      evaluator.eval(input);
    } finally {
      evaluator.getEvalEngine().setErrorPrintStream(old);
    }
    return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
  }

  @Test
  public void testLocationList() {
    check("Standardize({3,8,1},({0,1}&),Variance)", //
        "Standardize({3,8,1},{0,1}&,Variance)");
    check("Standardize({{3,8},{1,4},{2,2}},Median,({5,5}&))", //
        "Standardize(\n{{3,8},\n {1,4},\n {2,2}},Median,{5,5}&)");
  }

  @Test
  public void testScaleZero() {
    // the scale is zero for the data
    check("Standardize({7,7,7})", //
        "Standardize({7,7,7})");
    check("Standardize({{7,1},{7,5}})", //
        "Standardize(\n{{7,1},\n {7,5}})");
    check("Standardize({2,5},Mean,Min(#)-2&)", //
        "Standardize({2,5},Mean,Min(#1)-2&)");
    // a scale function which is zero for every argument gives the reciprocal ComplexInfinity
    check("Standardize({2,5},Mean,(0&))", //
        "{ComplexInfinity,ComplexInfinity}");
  }

  @Test
  public void testMessages() {
    String messages = messagesOf("Standardize({3,8,1},({0,1}&),Variance)");
    org.junit.jupiter.api.Assertions.assertTrue(
        messages.contains("The location function {0,1}& evaluated to a list."), messages);
    messages = messagesOf("Standardize({{3,8},{1,4}},Mean,({5,5}&))");
    org.junit.jupiter.api.Assertions.assertTrue(messages.contains(
        "The scale function {5,5}& evaluated to a list or zero for one or more of the columns."),
        messages);
    messages = messagesOf("Standardize({7,7,7})");
    org.junit.jupiter.api.Assertions.assertTrue(
        messages.contains("The scale function StandardDeviation evaluated to a list or zero."),
        messages);
  }

  @Test
  public void testValidScales() {
    check("Standardize({{3,8},{1,4},{2,0}},Min,Max)", //
        "{{2/3,1},{0,1/2},{1/3,0}}");
    check("Standardize({1,4,7})", //
        "{-1,0,1}");
  }
}
