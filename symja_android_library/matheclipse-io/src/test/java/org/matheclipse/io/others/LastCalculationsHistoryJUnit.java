package org.matheclipse.io.others;

import org.matheclipse.core.eval.EvalHistory;
import org.matheclipse.core.expression.F;
import org.matheclipse.parser.client.FEConfig;
import junit.framework.TestCase;

public class LastCalculationsHistoryJUnit extends TestCase {

  public LastCalculationsHistoryJUnit(String name) {
    super(name);
  }

  public void testSystem001() {
    FEConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
    EvalHistory history = new EvalHistory((short) 3);

    assertEquals(history.getOut(-1).toString(), "NIL");
    assertEquals(history.getOut(9).toString(), "NIL");
    history.addInOut(F.ZZ(1), F.ZZ(1));
    history.addInOut(F.ZZ(2), F.ZZ(2));
    history.addInOut(F.ZZ(3), F.ZZ(3));

    assertEquals(history.definitionOut().toString(), "{Set(Out(1),1),Set(Out(2),2),Set(Out(3),3)}");
    assertEquals(history.getIn(0).toString(), "NIL");
    assertEquals(history.getIn(-1).toString(), "3");
    assertEquals(history.getIn(3).toString(), "3");
    assertEquals(history.getIn(4).toString(), "NIL");
    assertEquals(history.getIn(-4).toString(), "NIL");

    history.addInOut(F.ZZ(4), F.ZZ(4));

    assertEquals(history.definitionOut().toString(), "{Set(Out(2),2),Set(Out(3),3),Set(Out(4),4)}");
    assertEquals(history.getIn(0).toString(), "NIL");
    assertEquals(history.getIn(-1).toString(), "4");
    assertEquals(history.getIn(3).toString(), "3");
    assertEquals(history.getIn(4).toString(), "4");
    assertEquals(history.getIn(-4).toString(), "NIL");
  }

  public void testSystem002() {
    EvalHistory history = new EvalHistory((short) 10);

    assertEquals(history.getOut(-1), F.NIL);
    assertEquals(history.getOut(9), F.NIL);
    history.addInOut(F.ZZ(1), F.ZZ(1));
    history.addInOut(F.ZZ(2), F.ZZ(2));
    history.addInOut(F.ZZ(3), F.ZZ(3));

    assertEquals(history.getIn(0), F.NIL);
    assertEquals(history.getIn(-1).toString(), "3");
    assertEquals(history.getIn(3).toString(), "3");
    assertEquals(history.getIn(12), F.NIL);
    assertEquals(history.getIn(-12), F.NIL);

    assertEquals(history.getOut(0), F.NIL);
    assertEquals(history.getOut(-1).toString(), "3");
    assertEquals(history.getOut(3).toString(), "3");
    assertEquals(history.getOut(12), F.NIL);
    assertEquals(history.getOut(-12), F.NIL);

    history.addInOut(F.ZZ(4), F.ZZ(4));
    history.addInOut(F.ZZ(5), F.ZZ(5));
    history.addInOut(F.ZZ(6), F.ZZ(6));
    history.addInOut(F.ZZ(7), F.ZZ(7));
    history.addInOut(F.ZZ(8), F.ZZ(8));
    history.addInOut(F.ZZ(9), F.ZZ(9));
    history.addInOut(F.ZZ(10), F.ZZ(10));

    assertEquals(history.getIn(-1).toString(), "10");
    assertEquals(history.getIn(10).toString(), "10");

    assertEquals(history.getOut(-1).toString(), "10");
    assertEquals(history.getOut(10).toString(), "10");

    history.addInOut(F.ZZ(11), F.ZZ(11));

    assertEquals(history.getIn(-1).toString(), "11");
    assertEquals(history.getIn(11).toString(), "11");
    assertEquals(history.getIn(-2).toString(), "10");
    assertEquals(history.getIn(10).toString(), "10");
    assertEquals(history.getIn(-5).toString(), "7");
    assertEquals(history.getIn(5).toString(), "5");

    assertEquals(history.getOut(-1).toString(), "11");
    assertEquals(history.getOut(11).toString(), "11");
    assertEquals(history.getOut(-2).toString(), "10");
    assertEquals(history.getOut(10).toString(), "10");
    assertEquals(history.getOut(-5).toString(), "7");
    assertEquals(history.getOut(5).toString(), "5");
    history.addInOut(F.ZZ(12), F.ZZ(12));
    history.addInOut(F.ZZ(13), F.ZZ(13));
    history.addInOut(F.ZZ(14), F.ZZ(14));
    history.addInOut(F.ZZ(15), F.ZZ(15));
    history.addInOut(F.ZZ(16), F.ZZ(16));
    history.addInOut(F.ZZ(17), F.ZZ(17));

    assertEquals(history.getIn(-1).toString(), "17");
    assertEquals(history.getIn(11).toString(), "11");

    assertEquals(history.getOut(-1).toString(), "17");
    assertEquals(history.getOut(11).toString(), "11");

    history.addInOut(F.ZZ(18), F.ZZ(18));
    history.addInOut(F.ZZ(19), F.ZZ(19));
    history.addInOut(F.ZZ(20), F.ZZ(20));

    assertEquals(history.getIn(-1).toString(), "20");
    assertEquals(history.getIn(11).toString(), "11");
    assertEquals(history.getIn(10), F.NIL);

    assertEquals(history.getOut(-1).toString(), "20");
    assertEquals(history.getOut(11).toString(), "11");
    assertEquals(history.getOut(10), F.NIL);

    history.addInOut(F.ZZ(21), F.ZZ(21));

    assertEquals(history.getIn(-4).toString(), "18");
    assertEquals(history.getIn(11), F.NIL);
    assertEquals(history.getIn(10), F.NIL);

    assertEquals(history.getOut(-4).toString(), "18");
    assertEquals(history.getOut(11), F.NIL);
    assertEquals(history.getOut(10), F.NIL);

    history.addInOut(F.ZZ(22), F.ZZ(22));

    assertEquals(history.getIn(Integer.MAX_VALUE), F.NIL);
    assertEquals(history.getIn(Integer.MIN_VALUE), F.NIL);

    assertEquals(history.getOut(Integer.MAX_VALUE), F.NIL);
    assertEquals(history.getOut(Integer.MIN_VALUE), F.NIL);
  }
}
