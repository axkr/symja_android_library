package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * A pure function applied to too few arguments keeps the slots it cannot fill and reports each one:
 * <code>Function::slotn</code> for <code>#n</code>, <code>Function::slots</code> for
 * <code>##n</code>.
 */
public class SlotMessagesTest extends ExprEvaluatorTestCase {

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
  public void testEveryUnfilledSlotIsReported() {
    check("g(#3, #1, #2) &[u]", //
        "g(#3,u,#2)");
    // in the order of the body
    assertEquals(
        "Function: Slot number 3 in g(#3,#1,#2)& cannot be filled from g(#3,#1,#2)&[u].\n"
            + "Function: Slot number 2 in g(#3,#1,#2)& cannot be filled from g(#3,#1,#2)&[u].\n",
        messagesOf("g(#3, #1, #2) &[u]"));
    // a repeated slot is the same message, printed once
    assertEquals(
        "Function: Slot number 2 in h(#2,#2)& cannot be filled from h(#2,#2)&[v].\n",
        messagesOf("h(#2, #2) &[v]"));
  }

  @Test
  public void testFurtherSlotMessagesAreSuppressed() {
    check("k(#1, #2, #3, #4, #5) &[]", //
        "k(#1,#2,#3,#4,#5)");
    assertEquals("Function: Slot number 1 in k(#1,#2,#3,#4,#5)& cannot be filled from "
        + "k(#1,#2,#3,#4,#5)&[].\n"
        + "Function: Slot number 2 in k(#1,#2,#3,#4,#5)& cannot be filled from "
        + "k(#1,#2,#3,#4,#5)&[].\n"
        + "Function: Slot number 3 in k(#1,#2,#3,#4,#5)& cannot be filled from "
        + "k(#1,#2,#3,#4,#5)&[].\n"
        + "General: Further output of Function::slotn will be suppressed during this calculation.\n",
        messagesOf("k(#1, #2, #3, #4, #5) &[]"));
  }

  @Test
  public void testUnfilledSlotSequence() {
    // ##n needs at least n-1 arguments; ##(n) with exactly n-1 is the empty sequence
    check("m(##4) &[p, q, r]", //
        "m()");
    assertEquals("", messagesOf("m(##4) &[p, q, r]"));
    check("m(##4) &[p, q]", //
        "m(##4)");
    assertEquals("Function: SlotSequence[4] in m(##4)& cannot be filled from m(##4)&[p,q].\n",
        messagesOf("m(##4) &[p, q]"));
    // slots and slot sequences are counted apart
    assertEquals("Function: Slot number 2 in m(#2,##3)& cannot be filled from m(#2,##3)&[p].\n"
        + "Function: SlotSequence[3] in m(#2,##3)& cannot be filled from m(#2,##3)&[p].\n",
        messagesOf("m(#2, ##3) &[p]"));
  }

  @Test
  public void testStopLineIsAMessage() {
    // the line that suppresses further slot messages is the message General::stop
    check("Check(q(#1, #2, #3, #4) &[], caught, General::stop)", //
        "caught");
  }
}
