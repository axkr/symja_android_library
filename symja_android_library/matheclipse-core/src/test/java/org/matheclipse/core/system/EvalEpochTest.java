package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalEpochValidation;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The fixed point stamp of the evaluation loop: an expression the loop evaluated without a result
 * is skipped the next time, until a definition changes. These tests are about the cases in which
 * the stamp must <b>not</b> be trusted, and about the ones in which it has to survive.
 */
public class EvalEpochTest extends ExprEvaluatorTestCase {

  /** A stamped expression which is changed in place is a new expression. */
  @Test
  public void testStampIsDroppedWhenChangedInPlace() {
    EvalEngine engine = EvalEngine.get();
    ISymbol u = F.symbol("epj1");
    ISymbol v = F.symbol("epj2");

    IASTAppendable list = F.ListAlloc(4);
    list.append(u);
    list.append(v);
    assertSame(list, engine.evaluate(list));
    assertNotEquals(0L, list.getEvalEpoch(), "a fixed point is stamped");

    list.append(F.Plus(F.C1, F.C1));
    assertEquals(0L, list.getEvalEpoch(), "append");
    assertEquals("{epj1,epj2,2}", engine.evaluate(list).toString());

    IASTAppendable second = F.ListAlloc(4);
    second.append(u);
    second.append(v);
    engine.evaluate(second);
    second.set(1, F.Times(F.C2, F.C3));
    assertEquals(0L, second.getEvalEpoch(), "set");
    assertEquals("{6,epj2}", engine.evaluate(second).toString());

    IASTAppendable third = F.ListAlloc(4);
    third.append(u);
    third.append(v);
    engine.evaluate(third);
    third.remove(1);
    assertEquals(0L, third.getEvalEpoch(), "remove");

    // replacing the head makes it a call of that head
    IASTAppendable fourth = F.ListAlloc(4);
    fourth.append(F.C3);
    fourth.append(F.C4);
    engine.evaluate(fourth);
    fourth.set(0, F.Plus);
    assertEquals(0L, fourth.getEvalEpoch(), "head");
    assertEquals("7", engine.evaluate(fourth).toString());
  }

  /** Reading a variable is not a change: the stamp of its value outlives the read. */
  @Test
  public void testReadingAVariableKeepsTheStampOfItsValue() throws InterruptedException {
    // the Integrate rules are loaded by a thread of their own, and each rule it installs is a
    // definition which moves the epoch: wait until that is over
    S.Integrate.getEvaluator().await();
    check("epdata = {epf(1,x), epf(2,x)}", //
        "{epf(1,x),epf(2,x)}");
    ISymbol data = (ISymbol) evaluator.parse("epdata");
    IAST value = (IAST) data.assignedValue();

    // Not through check(): an input line also assigns its result to the output history, which is
    // a change. That is why the stamp is taken after a first read - the assignment above was
    // followed by one.
    EvalEngine engine = EvalEngine.get();
    assertSame(value, engine.evaluate(data));
    final long stamp = value.getEvalEpoch();
    assertNotEquals(0L, stamp);
    assertSame(value, engine.evaluate(data));
    assertEquals("2", engine.evaluate(F.Length(data)).toString());
    assertEquals("epf(2,x)", engine.evaluate(F.Part(data, F.C2)).toString());
    assertSame(value, engine.evaluate(data));
    assertEquals(stamp, value.getEvalEpoch(), "a read moved the epoch");

    // a definition is a change, and the value has to follow it
    check("epf(a_, b_) := a + b", //
        "");
    check("epdata", //
        "{1+x,2+x}");
    check("ClearAll(epf)", //
        "");
    check("epdata", //
        "{epf(1,x),epf(2,x)}");
  }

  @Test
  public void testValuesWhichChange() {
    // a delayed value is evaluated on every read
    check("epn = 0; epd := (epn = epn + 1); {epd, epd, epd}", //
        "{1,2,3}");
    // the value follows the variables it mentions
    check("epv = {epu + 1, epu^2}; epu = 3; epv", //
        "{4,9}");
    check("Block({epu = 10}, epv)", //
        "{11,100}");
    check("epu = 4; Table(epv, {2})", //
        "{{5,16},{5,16}}");
    // a chain of own values
    check("epx = epy; epy = epz; epz = 5; {epx, epy}", //
        "{5,5}");
    // a side effect runs once per call, not once per read of the result
    check("epc = 0; epg(k_) := (epc++; k^2); epr = {epg(2), epg(3)}; "
        + "{epr, epc, epr, epc}", //
        "{{4,9},2,{4,9},2}");
  }

  @Test
  public void testValueChangedInPlace() {
    check("epl = {p, q, r}; epl[[2]] = 7; epl", //
        "{p,7,r}");
    check("epl2 = epl; epl2[[1]] = 0; {epl, epl2}", //
        "{{p,7,r},{0,7,r}}");
    check("AppendTo(epl, s); epl", //
        "{p,7,r,s}");
  }

  /** An attribute decides how arguments are evaluated, so changing one is a change. */
  @Test
  public void testAttributeChange() {
    check("SetAttributes(eph, HoldAll); epq = eph(1 + 1)", //
        "eph(1+1)");
    check("ClearAttributes(eph, HoldAll); epq", //
        "eph(2)");
  }

  /** One expression, evaluated in both numeric modes: neither result may hide the other. */
  @Test
  public void testNumericAndSymbolicModeOnOneExpression() {
    EvalEngine engine = EvalEngine.get();
    IExpr expr = engine.evaluate(F.Plus(F.Sin(F.C1), F.Sqrt(F.C3)));
    assertEquals("Sqrt(3)+Sin(1)", expr.toString());
    for (int i = 0; i < 3; i++) {
      assertEquals(2.5735217923767738, engine.evalDouble(expr), 1e-12);
      assertEquals("Sqrt(3)+Sin(1)", engine.evaluate(expr).toString());
    }

    check("epe = Hold(Sin(1) + Sqrt(3)); N(ReleaseHold(epe))", //
        "2.57352");
    check("N(ReleaseHold(epe), 30)", //
        "2.57352179237677380017994866313");
    check("N(ReleaseHold(epe))", //
        "2.57352");
    check("ReleaseHold(epe)", //
        "Sqrt(3)+Sin(1)");
    check("epm = {1.5, ept}; {epm, N(epm), epm}", //
        "{{1.5,ept},{1.5,ept},{1.5,ept}}");
    check("ept = Pi; {epm, N(epm)}", //
        "{{1.5,Pi},{1.5,3.14159}}");
  }

  /**
   * The epoch belongs to all engines, the numeric mode to one. A constant which every engine
   * shares is stamped by a symbolic evaluation in one thread and has to evaluate to a number in
   * another one all the same.
   */
  @Test
  public void testNumericModeOfAnotherEngine() throws InterruptedException {
    final IAST shared = F.CSqrt2;
    final AtomicBoolean stop = new AtomicBoolean();
    final AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread symbolic = new Thread(() -> {
      try {
        EvalEngine engine = new EvalEngine(true);
        EvalEngine.set(engine);
        while (!stop.get()) {
          IExpr result = engine.evaluate(shared);
          if (!result.isSqrt()) {
            throw new AssertionError("symbolic: " + result);
          }
        }
      } catch (Throwable t) {
        failure.compareAndSet(null, t);
      }
    });
    symbolic.start();
    try {
      EvalEngine engine = EvalEngine.get();
      for (int i = 0; i < 20000 && failure.get() == null; i++) {
        assertEquals(1.4142135623730951, engine.evalDouble(shared), 1e-15);
      }
    } finally {
      stop.set(true);
      symbolic.join();
    }
    assertEquals(null, failure.get());
  }

  /** <code>Sequence</code> evaluates differently as an argument and at the top level. */
  @Test
  public void testSequenceIsNotStamped() {
    check("Evaluate(epa, epb)", //
        "Identity(epa,epb)");
    check("epf1(Evaluate(Sequence(epa, epb)))", //
        "epf1(epa,epb)");
    check("eps = Hold(Sequence(epa, epb)); {ReleaseHold(eps), eps}", //
        "{epa,epb,Hold(epa,epb)}");
  }

  /** Every stamp the loop trusts is checked by evaluating the expression anyway. */
  @Test
  public void testNoStaleStamp() {
    final boolean validate = Config.EVAL_EPOCH_VALIDATE;
    try {
      EvalEpochValidation.reset();
      Config.EVAL_EPOCH_VALIDATE = true;
      check("Expand((x + y + 1)^4) - Expand((x + y + 1)^4)", //
          "0");
      check("D(Sin(x^2)*Exp(x), {x, 3}) // Together // Length", //
          "6");
      check("epw = Table(epk(i, x)^2, {i, 4}); epk(a_, b_) := a*b; Total(epw)", //
          "30*x^2");
      check("Table(N(Sin(i) + Sqrt(i)*Pi), {i, 3})", //
          "{3.98306,5.35218,5.58252}");
      check("Solve(x^2 - 5*x + 6 == 0, x)", //
          "{{x->2},{x->3}}");
      check("Integrate(x*Cos(x), x)", //
          "Cos(x)+x*Sin(x)");
      check("Module({s = 0}, Do(s = s + epk(j, 2), {j, 5}); s)", //
          "30");
      assertTrue(EvalEpochValidation.checked() > 0, "no stamp was used at all");
      assertEquals(0L, EvalEpochValidation.stale(), EvalEpochValidation.firstStale());
    } finally {
      Config.EVAL_EPOCH_VALIDATE = validate;
      EvalEpochValidation.reset();
    }
  }
}
