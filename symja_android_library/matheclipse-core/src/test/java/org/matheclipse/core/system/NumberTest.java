package org.matheclipse.core.system;

import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberTest extends TestCase {
  private static final Logger LOGGER = LogManager.getLogger();

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
      LOGGER.debug("NumberTest.testNumberFormat() failed", rex);
    }
    assertEquals(buf.toString(), "12345.12");
  }

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

  public void testApfloatRounding() {
    int precision = 30;
    ApfloatNum num = (ApfloatNum) ApfloatNum.valueOf("3.306158858189456", precision)
      .divide(ApfloatNum.valueOf("0.01", precision));
    IInteger round = num.roundExpr();
    assertEquals(round.toString(), "331");
  }

  public void testApfloatRounding2() {
    int precision = 30;
    ApfloatNum num = (ApfloatNum) ApfloatNum.valueOf("3.304158858189456", precision)
      .divide(ApfloatNum.valueOf("0.01", precision));
    IInteger round = num.roundExpr();
    assertEquals(round.toString(), "330");
  }

}
