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

  /**
   * A variable which a condition reads decides whether a rule applies to an expression which does
   * not mention it. Its new value has to reach that expression, as every change did before own
   * values were tracked per symbol.
   */
  @Test
  public void testValueReadByAConditionReachesExpressionsWithoutIt() {
    // condition on the right-hand side
    check("epcf(x_) := x /; epflag; epflag = False; epcr = {epcf(1)}; epcr", //
        "{epcf(1)}");
    check("epflag = True; epcr", //
        "{1}");
    // pattern test
    check("ept(x_?(# > eplim &)) := big; eplim = 10; epq2 = {ept(5)}; epq2", //
        "{ept(5)}");
    check("eplim = 1; epq2", //
        "{big}");
    // a Module before the condition
    check("epm(x_) := Module({v = epg0}, x /; v > 0); epg0 = -1; epw2 = {epm(2)}; epw2", //
        "{epm(2)}");
    check("epg0 = 1; epw2", //
        "{2}");
    // a condition inside the pattern
    check("epc2(x_ /; x > epb) := bigger; epb = 10; epv2 = {epc2(5)}; epv2", //
        "{epc2(5)}");
    check("epb = 1; epv2", //
        "{bigger}");
    // a variable which a condition reads and assigns (with the same value, so that evaluating the
    // condition once more, as -Dsymja.evalEpoch.validate does, gives the same answer)
    check("epcnt = 0; epk2(x_) := x /; ((epcnt += 0) > 2); epv3 = {epk2(7)}; epv3", //
        "{epk2(7)}");
    check("epcnt = 5; epv3", //
        "{7}");
    // the rules of a symbol which only the condition calls, removed by ClearAll and by Clear
    check("epsrc(1) := False; epo2(x_) := yes /; epsrc(x) =!= False; epv4 = {epo2(1)}; epv4", //
        "{epo2(1)}");
    check("ClearAll(epsrc); epv4", //
        "{yes}");
    check("epsrc2(1) := False; epo3(x_) := yes /; epsrc2(x) =!= False; epv5 = {epo3(1)}; epv5", //
        "{epo3(1)}");
    check("Clear(epsrc2); epv5", //
        "{yes}");
  }

  /**
   * A new value of a variable which no condition reads leaves the stamps of the expressions without
   * it in place: a loop over a list of rules held in a variable used to evaluate every rule again
   * for each value of the loop counter.
   */
  @Test
  public void testUnrelatedValueKeepsTheStamp() throws InterruptedException {
    S.Integrate.getEvaluator().await();
    check("SetAttributes(epo, {Flat, OneIdentity}); epo(a_, b_Plus) := Map(epo(a, #) &, b); "
        + "eprels = Table(k_Integer*epo(a__, epn(i)) :> k*epo(a), {i, 3}); Length(eprels)", //
        "3");
    ISymbol rels = (ISymbol) evaluator.parse("eprels");
    EvalEngine engine = EvalEngine.get();
    IAST value = (IAST) engine.evaluate(rels);
    final long stamp = value.getEvalEpoch();
    assertNotEquals(0L, stamp);
    // a Module local and a loop counter get new values
    engine.evaluate(evaluator.parse("Module({t}, Do(t = j, {j, 3}); t)"));
    assertSame(value, engine.evaluate(rels));
    // a variable which occurs in the stamped expression still counts
    check("eprels2 = {epf3(epz3)}; epz3 = 5; eprels2", //
        "{epf3(5)}");
    // and a definition still changes everything
    check("epn(2) = two; eprels[[2]]", //
        "epo(a__,two)*k_Integer:>k*epo(a)");
  }

  /**
   * A Block over variables without rules, in the body of a rule with a condition, changes no
   * definition: a stored expression keeps its stamp.
   */
  @Test
  public void testBlockInAConditionalRuleKeepsTheStamp() throws InterruptedException {
    S.Integrate.getEvaluator().await();
    EvalEngine engine = EvalEngine.get();
    // epbc(1) counts its evaluations: its condition fails and increments epbcnt
    engine.evaluate(evaluator.parse("epbf(x_) := Block({epbt, epbu}, epbt = x; epbu = epbt + 1; "
        + "epbu) /; x > 0; epbcnt = 0; epbc(x_) := x /; (epbcnt = epbcnt + 1; False); "
        + "epbl = {epbc(1)}"));
    ISymbol stored = (ISymbol) evaluator.parse("epbl");
    ISymbol counter = (ISymbol) evaluator.parse("epbcnt");
    engine.evaluate(stored);
    IExpr before = engine.evaluate(counter);
    assertEquals("2", engine.evaluate(evaluator.parse("epbf(1)")).toString());
    engine.evaluate(stored);
    if (!Config.EVAL_EPOCH_VALIDATE) {
      // a validation run evaluates every stamped expression once more
      assertEquals(before, engine.evaluate(counter));
    }
    // the test of the condition still decides: a value read there is a hard change
    check("epbflag = False; epbh(x_) := x /; epbflag; epbk = epbh(1)", //
        "epbh(1)");
    check("epbflag = True; epbk", //
        "1");
  }

  /**
   * A function defined in a Block changes only the expressions which contain it: a stored
   * expression keeps its stamp.
   */
  @Test
  public void testLocalFunctionKeepsTheStamp() throws InterruptedException {
    S.Integrate.getEvaluator().await();
    EvalEngine engine = EvalEngine.get();
    // eplc(1) counts its evaluations: its condition fails and increments eplcnt
    engine.evaluate(evaluator.parse("eplf(x_) := Block({eploc}, eploc(y_) := y + 1; eploc(x)); "
        + "eplcnt = 0; eplc(x_) := x /; (eplcnt = eplcnt + 1; False); epll = {eplc(1)}"));
    ISymbol stored = (ISymbol) evaluator.parse("epll");
    ISymbol counter = (ISymbol) evaluator.parse("eplcnt");
    engine.evaluate(stored);
    IExpr before = engine.evaluate(counter);
    assertEquals("6", engine.evaluate(evaluator.parse("eplf(5)")).toString());
    engine.evaluate(stored);
    if (!Config.EVAL_EPOCH_VALIDATE) {
      // a validation run evaluates every stamped expression once more
      assertEquals(before, engine.evaluate(counter));
    }
  }

  /**
   * A new rule reaches every expression whose evaluation depended on it: the expressions which
   * contain the symbol, and the ones whose conditions, pattern tests or options used its rules.
   */
  @Test
  public void testNewRuleReachesDependentExpressions() {
    check("eprf(x_) := 0 /; eprs(x) === 1; epre = {eprf(2)}; "
        + "{ToString(epre), eprs(2) = 1; ToString(epre)}", //
        "{{eprf(2)},{0}}");
    check("Options(eprk) = {epra -> 2}; eprg(x_, OptionsPattern(eprk)) := 0 /; "
        + "OptionValue(epra) === 1; epre2 = {eprg(1)}; "
        + "{ToString(epre2), SetOptions(eprk, epra -> 1); ToString(epre2)}", //
        "{{eprg(1)},{0}}");
    check("epre3 = {eprh(1)}; {ToString(epre3), eprh(1) = 5; ToString(epre3)}", //
        "{{eprh(1)},{5}}");
    check("epru(x_) := 0 /; eprg2(eprt) === 1; epre4 = {epru(2)}; "
        + "{ToString(epre4), eprt /: eprg2(eprt) = 1; ToString(epre4)}", //
        "{{epru(2)},{0}}");
    check("eprp(x_?(eprq(#) &)) := 0; epre5 = {eprp(3)}; "
        + "{ToString(epre5), eprq(3) = True; ToString(epre5)}", //
        "{{eprp(3)},{0}}");
    check("eprm(x_) := Module({t = eprr(x)}, 0 /; t === 1); epre6 = {eprm(1)}; "
        + "{ToString(epre6), eprr(1) = 1; ToString(epre6)}", //
        "{{eprm(1)},{0}}");
  }

  /**
   * An iterator over a variable which a condition read before: the fixed points which depend on
   * the outer value are invalidated once, not at every step.
   */
  @Test
  public void testIteratorOverAConditionVariable() throws InterruptedException {
    S.Integrate.getEvaluator().await();
    EvalEngine engine = EvalEngine.get();
    engine.evaluate(evaluator.parse("epif(y_) := 0 /; epii === 3; epif(1); epicnt = 0; "
        + "epic(x_) := x /; (epicnt = epicnt + 1; False); epil = {epic(1)}"));
    ISymbol counter = (ISymbol) evaluator.parse("epicnt");
    engine.evaluate(evaluator.parse("epil"));
    long before = engine.evaluate(counter).toLongDefault();
    engine.evaluate(evaluator.parse("Table(epil; epii, {epii, 1, 20})"));
    if (!Config.EVAL_EPOCH_VALIDATE) {
      assertTrue(engine.evaluate(counter).toLongDefault() - before <= 2);
    }
  }

  /** The value of an iterator or a Block reaches the conditions which read it. */
  @Test
  public void testLocalValueReachesConditions() {
    check("epsh(y_) := 0 /; epsi === 3; epsi = 1; epsx = {epsh(2)}; "
        + "{ToString(epsx), Table(ToString(epsx), {epsi, 1, 4}), "
        + "Block({epsi = 3}, ToString(epsx)), ToString(epsx)}", //
        "{{epsh(2)},{{epsh(2)},{epsh(2)},{0},{epsh(2)}},{0},{epsh(2)}}");
    check("epsh4(y_) := 0 /; epsj === 3; epsx4 = {epsh4(2)}; Table(ToString(epsx4), {epsj, 1, 4})", //
        "{{epsh4(2)},{epsh4(2)},{0},{epsh4(2)}}");
    check("epsh6(y_) := 0 /; epsk(y) === 1; epsx6 = {epsh6(2)}; "
        + "{Block({epsk}, epsk(2) = 1; ToString(epsx6)), ToString(epsx6)}", //
        "{{0},{epsh6(2)}}");
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
        "2.57352179237677380017994866314");
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
