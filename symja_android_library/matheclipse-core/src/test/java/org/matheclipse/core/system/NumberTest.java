package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.apfloat.Apfloat;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.BigFractionSym;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;

public class NumberTest extends ExprEvaluatorTestCase {

  @Test
  public void testComplexNum() {
    // test for Android bug:
    // https://github.com/tranleduy2000/symja_android_library/commit/2f03d0b6c8095c2c71b1f56c8e5fc5f0b30f927d
    // 3802951800684688204490109616127/1267650600228229401496703205376
    IFraction f = AbstractFractionSym.valueOf(new BigInteger("3802951800684688204490109616127"),
      new BigInteger("1267650600228229401496703205376"));
    ComplexNum cn = f.complexNumValue();
    assertEquals(cn.toString(), "(3.0)");
    // 2535301200456458802993406410751/1267650600228229401496703205376
    f = AbstractFractionSym.valueOf(new BigInteger("2535301200456458802993406410751"),
      new BigInteger("1267650600228229401496703205376"));
    cn = f.complexNumValue();
    assertEquals(cn.toString(), "(2.0)");
  }

  @Test
  public void testPower() {
    IFraction f = AbstractFractionSym.valueOf(2, 3);

    assertEquals(f.powerRational(-2).toString(), "9/4");

    IFraction f0 = AbstractFractionSym.valueOf(5, 14);
    assertEquals(f0.powerRational(2).toString(), "25/196");
  }

  /**
   * Format a double value with a <code>java.text.DecimalFormat</code> object.
   *
   * <p>
   * See: <a href="https://docs.oracle.com/javase/tutorial/java/data/numberformat.html">numberformat
   * article</a>
   */
  @Test
  public void testNumberFormat() {
    StringBuilder buf = new StringBuilder();
    try {
      // DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
      // DecimalFormat decimalFormat = new DecimalFormat("0.0####", otherSymbols);
      OutputFormFactory factory = OutputFormFactory.get(true, false, 5, 7);

      IExpr expr = F.num("12345.123456789");
      if (!factory.convert(buf, expr)) {
        fail();
      }
    } catch (RuntimeException rex) {
      rex.printStackTrace();
      fail("NumberTest.testNumberFormat() failed");
    }
    assertEquals(buf.toString(), "12345.12");
  }

  @Test
  public void testDoubleFormat() {
    double a = 1.3;
    double b = 1.0;
    double result = a - b;

    assertEquals("0.30000000000000004", Double.toString(result));
    // prints 0.30000000000000004
    // System.out.println(result);

    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat decimalFormat = new DecimalFormat("0.0####", otherSymbols);
    assertEquals("0.3", decimalFormat.format(result));
    // prints 0.3
    // System.out.println(decimalFormat.format(result));
  }

  @Test
  public void testApfloatRounding() {
    int precision = 30;
    ApfloatNum num = (ApfloatNum) ApfloatNum.valueOf("3.306158858189456", precision)
      .divide(ApfloatNum.valueOf("0.01", precision));
    IInteger round = num.roundExpr();
    assertEquals(round.toString(), "331");
  }

  @Test
  public void testApfloatRounding2() {
    int precision = 30;
    ApfloatNum num = (ApfloatNum) ApfloatNum.valueOf("3.304158858189456", precision)
      .divide(ApfloatNum.valueOf("0.01", precision));
    IInteger round = num.roundExpr();
    assertEquals(round.toString(), "330");
  }

  @Test
  public void testFractionSymToDouble() {
    // (102/100)^181
    String input = "(102/100)^181";
    ExprEvaluator exprEvaluator = new ExprEvaluator();
    IExpr result = exprEvaluator.eval(input);
    assertInstanceOf(BigFractionSym.class, result);
    assertEquals(result.evalf(), 36.027247984128934, 1E-8);
    assertEquals(((BigFractionSym) result).complexNumValue().getRealPart(), 36.027247984128934, 1E-8);
  }

  @Test
  public void testFractionSymToDouble2() {
    String input = "N[140^(769/500)]";
    ExprEvaluator exprEvaluator = new ExprEvaluator();
    IExpr result = exprEvaluator.eval(input);
    assertEquals(result.toString(), "1998.688");
    assertEquals(result.evalf(), 1998.6876036465665, 1E-8);
  }

  @Test
  public void testFractionSymToDouble3() {
    String input = "N[140^(1538/1000)]";
    ExprEvaluator exprEvaluator = new ExprEvaluator();
    IExpr result = exprEvaluator.eval(input);
    assertEquals(result.toString(), "1998.688");
    assertEquals(result.evalf(), 1998.6876036465665, 1E-8);
  }

  @Test
  public void testApfloatNumToString() {
    ApfloatNum apfloatNum = ApfloatNum.valueOf(new Apfloat("-1.44224957030740838"));
    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, true, false, 3, 18);
    outputFormFactory.reset(true);
    StringBuilder buf = new StringBuilder();
    assertTrue(outputFormFactory.convert(buf, apfloatNum));
    assertEquals(buf.toString(), "-1.44224957030740838");
  }

  @Test
  public void testPrecisionOutOfConfigValue() {
    // Config.MAX_PRECISION_APFLOAT is 512 in ExprEvaluatorTestCase#setUp()
    assertTrue(Config.MAX_PRECISION_APFLOAT < 1000,
      "the 1001 digits requested below must exceed Config.MAX_PRECISION_APFLOAT="
        + Config.MAX_PRECISION_APFLOAT + ", otherwise nothing is truncated");
    boolean oldTruncate = Config.TRUNCATE_PRECISION_IN_N;
    try {
      Config.TRUNCATE_PRECISION_IN_N = true;
      // the 1001 requested digits are truncated to Config.MAX_PRECISION_APFLOAT == 512
      check("N(Simplify(-3858/3125+N(1.23456,1001)),1001)", //
        "0");
      check("Precision(N(Pi,1001))", //
        "512");

      // the truncation starts one digit above Config.MAX_PRECISION_APFLOAT, a request at or below
      // the limit is passed through unchanged
      check("Precision(N(Pi,513))", //
        "512");
      check("Precision(N(Pi,512))", //
        "512");
      check("Precision(N(Pi,511))", //
        "511");
      // SameQ compares the precision too, so this pins the clamp target exactly: an over-bound
      // request is the same number as a request for the bound, and not one digit less. Note that
      // Equal and Subtract would NOT pin it - they degrade to the lower operand precision, so
      // N(Pi,1001)==N(Pi,511) is True and N(Pi,1001)-N(Pi,17) is 0.
      check("N(Pi,1001)===N(Pi,512)", //
        "True");
      check("N(Pi,1001)===N(Pi,511)", //
        "False");
      check("N(Pi,512)===N(Pi,513)", //
        "True");
      // the clamped precision really is on the Apfloat, not only in Precision()'s bookkeeping
      assertEquals(512L, assertInstanceOf(ApfloatNum.class, evaluator.eval("N(Pi,1001)"))
        .apfloatValue().precision());
      check("StringLength(ToString(N(Pi,1001)))", //
        "517");
      check("StringTake(ToString(N(Pi,1001)),12)", //
        "3.1415926535");
      // head, leading digits, elision and the `512 precision marker in a single readable line
      check("Short(N(Pi,1001))", //
        "3.141592653589793238462643<<SHORT>>1830119491298336733624`512");
      // the truncation is silent: Check() would return -1 if N::precgt had been emitted
      check("Precision(Check(N(Pi,1001),-1))", //
        "512");

      // the List/Association/Rule threading in N() truncates element by element, and so does the
      // second mapping site in evalN2() which only sees the List after symbolic evaluation
      check("Map(Precision,N({Pi,E},1001))", //
        "{512,512}");
      check("Map(Precision,N(<|a->Pi,b->E|>,1001))", //
        "<|a->512,b->512|>");
      check("Map(Precision,N(a->Pi,1001))", //
        "Infinity->512");
      check("Map(Precision,N(Table(Pi,{2}),1001))", //
        "{512,512}");
      check("Map(Precision,N({x,Pi},1001))", //
        "{Infinity,512}");
      // truncating an already truncated number is idempotent, and the truncation never invents
      // digits which the inner call didn't produce
      check("Precision(N(N(Pi,1001),1001))", //
        "512");
      check("Precision(N(N(Pi,50),1001))", //
        "50");
      check("N(N(Pi,1001),20)", //
        "3.1415926535897932384");
      // the argument shape doesn't matter: exact rational, inexact double, complex, AST
      check("Precision(N(2/3,1001))", //
        "512");
      check("Precision(N(1.23456,1001))", //
        "512");
      check("Precision(N(Sqrt(-2),1001))", //
        "512");
      check("Precision(N(E^Pi,1001))", //
        "512");
      // the requested precision is Ceiling()ed before it is truncated, so the truncation starts at
      // a fractional request just above the limit
      check("Precision(N(Pi,1000.5))", //
        "512");
      check("Precision(N(Pi,2001/2))", //
        "512");
      check("Precision(N(Pi,512.5))", //
        "512");
      check("Precision(N(Pi,511.5))", //
        "512");
      check("Precision(N(Pi,Sqrt(2)*1000))", //
        "512");
      // the truncated result is a real number, not an unevaluated N(...)
      check("NumberQ(N(Pi,1001))", //
        "True");
      check("Head(N(Pi,1001))", //
        "Real");
      check("N(x,1001)", //
        "x");
      // N() restores the engine numeric mode, so the sibling fraction stays exact
      check("{Precision(N(Pi,1001)),2/3}", //
        "{512,2/3}");

      // the flag doesn't touch the lower bound, N::precsm is still reported. getMessageShortcut()
      // has to be asserted immediately after its own check() - EvalEngine#initInstance() nulls it
      // at the start of the next evaluation.
      check("N(Pi,0)", //
        "N(Pi,0)");
      assertEquals("precsm", evaluator.getEvalEngine().getMessageShortcut());
      check("N(Pi,-5)", //
        "N(Pi,-5)");
      assertEquals("precsm", evaluator.getEvalEngine().getMessageShortcut());
      // a second argument which isn't a real number is refused, the flag doesn't turn it into one
      check("N(Pi,1+I)", //
        "N(Pi,I+1)");
      assertEquals("precsm", evaluator.getEvalEngine().getMessageShortcut());
      // known limitation: the bound is tested on arg2.toIntDefault(), so a request which doesn't
      // fit an int collapses to Config.INVALID_INT and reports N::precsm instead of truncating.
      // 2147483646 is the largest request which is still truncated, Integer.MAX_VALUE is not.
      check("Precision(N(Pi,2147483646))", //
        "512");
      check("N(Pi,2147483647)", //
        "N(Pi,2147483647)");
      assertEquals("precsm", evaluator.getEvalEngine().getMessageShortcut(),
        "an int-overflowing precision must fall into the lower-bound branch");
      check("N(Pi,2^31)", //
        "N(Pi,2^31)");
      assertEquals("precsm", evaluator.getEvalEngine().getMessageShortcut());
      check("N(Pi,10^20)", //
        "N(Pi,10^20)");
      assertEquals("precsm", evaluator.getEvalEngine().getMessageShortcut());
      check("N(Pi,2^40)", //
        "N(Pi,2^40)");
      assertEquals("precsm", evaluator.getEvalEngine().getMessageShortcut());
      check("N(Pi,Infinity)", //
        "N(Pi,Infinity)");
      assertEquals("precsm", evaluator.getEvalEngine().getMessageShortcut());

      // second known limitation: the truncation doesn't survive into sibling subexpressions.
      // AbstractAST#determinePrecision() special-cases N(expr,prec) and returns arg2 unclamped, so
      // the engine numeric precision is still raised to the requested 1001 at parse time.
      check("Precision(N(Pi,1001)*1)", //
        "1001");
    } finally {
      Config.TRUNCATE_PRECISION_IN_N = oldTruncate;
    }
  }

  @Test
  public void testPrecisionGreaterThanConfigValue() {
    // default: N() prints the N::precgt message and leaves the expression unevaluated
    assertFalse(Config.TRUNCATE_PRECISION_IN_N,
      "Config.TRUNCATE_PRECISION_IN_N must default to false - if this fails either the default "
        + "changed or testPrecisionOutOfConfigValue() didn't restore it");
    assertTrue(Config.MAX_PRECISION_APFLOAT < 1000,
      "the 1001 digits requested below must exceed Config.MAX_PRECISION_APFLOAT="
        + Config.MAX_PRECISION_APFLOAT + ", otherwise N() doesn't report N::precgt");
    check("N(Pi,1001)", //
      "N(Pi,1001)");
    assertEquals("precgt", evaluator.getEvalEngine().getMessageShortcut(),
      "an over-bound precision must report N::precgt, not N::precsm");
    // the mirror of the truncating test's headline assertion: Precision() of the unevaluated N()
    check("Precision(N(Pi,1001))", //
      "Infinity");
    check("N(Pi,1001)===N(Pi,512)", //
      "False");
    // one digit above the limit is already refused, the limit itself still evaluates - the same
    // boundary after the Ceiling() of a fractional request
    check("N(Pi,513)", //
      "N(Pi,513)");
    check("Precision(N(Pi,512))", //
      "512");
    check("N(Pi,512.5)", //
      "N(Pi,512.5)");
    check("Precision(N(Pi,511.5))", //
      "512");
    // Check() returns its second argument, so the N::precgt message really was emitted
    check("Check(N(Pi,1001),\"msg\")", //
      "msg");

    // whatever the first argument looks like, the call comes back unevaluated
    check("N(1.23456,1001)", //
      "N(1.23456,1001)");
    check("N(2/3,1001)", //
      "N(2/3,1001)");
    check("N(x,1001)", //
      "N(x,1001)");
    // the List/Rule threading happens before the precision is validated, so every element refuses
    check("N({Pi,E},1001)", //
      "{N(Pi,1001),N(E,1001)}");
    check("Map(Precision,N({Pi,E},1001))", //
      "{Infinity,Infinity}");
    check("N(a->Pi,1001)", //
      "N(a,1001)->N(Pi,1001)");
    check("N(Simplify(-3858/3125+N(1.23456,1001)),1001)", //
      "N(Simplify(-3858/3125+N(1.23456,1001)),1001)");
    // no number is produced at all - contrast with the 517 characters of the truncated result
    check("StringLength(ToString(N(Pi,1001)))", //
      "10");
    check("Short(N(Pi,1001))", //
      "N(Pi,1001)");
    check("Head(N(Pi,1001))", //
      "N");
    check("NumberQ(N(Pi,1001))", //
      "False");

    // the lower bound reports N::precsm, unchanged by this feature
    check("N(Pi,0)", //
      "N(Pi,0)");
    check("N(Pi,-5)", //
      "N(Pi,-5)");
    check("N(Pi,2147483646)", //
      "N(Pi,2147483646)");
    assertEquals("precgt", evaluator.getEvalEngine().getMessageShortcut());
    check("N(Pi,10^20)", //
      "N(Pi,10^20)");
    assertEquals("precsm", evaluator.getEvalEngine().getMessageShortcut(),
      "the int-overflowing precision reports N::precsm with the flag off too");
  }
}
