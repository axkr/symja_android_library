package org.matheclipse.core.config;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.OverflowException;
import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.*;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

import static org.junit.Assert.*;

public class SystemConfigurationTest extends ExprEvaluatorTestCase {


  @Test
  public void testApfloatStorage() {
    try {
      Apfloat apfloat = new Apfloat("9".repeat(1_000_000) + "." + "9".repeat(1_000_000));
      apfloat = ApfloatMath.pow(apfloat, new Apfloat("91212312" + ".1231236"));
      ApfloatNum apfloatNum = ApfloatNum.valueOf(apfloat);
      IInteger integerPart = apfloatNum.integerPart();
      System.out.println(integerPart.bitLength());
      fail("Should be fail");
    } catch (OverflowException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testOverflowError() {
    ExprEvaluator evaluator = new ExprEvaluator();
    String expr = "N(1.7*10^1,100)/N(2.5*10^1,100)*N(0,100)";

    IExpr result = evaluator.eval(expr);
    assertEquals(result.toString(), "0");
  }

  @Test
  public void testOverflowError02() {
    ExprEvaluator evaluator = new ExprEvaluator();
    String expr = "ArcSin(N(1.7*10^1,100)/N(2.5*10^1,100)*N(0,100))";

    IExpr result = evaluator.eval(expr);
    assertEquals(result.toString(), "0");
  }

  @Test
  public void testDeterminePrecision() {
    ApfloatNum zero = ApfloatNum.valueOf(new Apfloat(new BigDecimal(BigInteger.ZERO), 30));
    ApfloatNum num = ApfloatNum.valueOf("1.7", 30);

    IASTMutable times = F.Times(num, zero);
    assertEquals(times.determinePrecision(true), 30);
  }

  @Test
  public void testRoundingMode() {
    Config.ROUNDING_MODE = RoundingMode.HALF_EVEN;
    assertEquals(ApfloatNum.valueOf("394.5", 30).roundExpr().toString(),
      "394");
    assertEquals(ComplexNum.valueOf(394.5, 0).roundExpr().toString(),
      "394+I*0");
    assertEquals(Num.valueOf(394.5).roundExpr().toString(),
      "394");

    Config.ROUNDING_MODE = RoundingMode.HALF_UP;
    assertEquals(ApfloatNum.valueOf("394.5", 30).roundExpr().toString(),
      "395");
    assertEquals(ComplexNum.valueOf(394.5, 0).roundExpr().toString(),
      "395+I*0");
    assertEquals(Num.valueOf(394.5).roundExpr().toString(),
      "395");
  }

}
