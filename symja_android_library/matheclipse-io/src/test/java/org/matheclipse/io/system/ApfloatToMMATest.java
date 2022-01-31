package org.matheclipse.io.system;

import org.apfloat.Apfloat;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.form.ApfloatToMMA;
import org.matheclipse.core.form.output.OutputFormFactory;
import junit.framework.TestCase;

/**
 * Convert a <code>Apfloat</code> value into a string similar to the Mathematica output format.
 *
 */
public class ApfloatToMMATest extends TestCase {

  public ApfloatToMMATest() {
    super("ApfloatToMMATest");
  }
  //
  // public void testTen() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, 10.0, 9, 6);
  // assertEquals("10.0", buf.toString());
  // }
  //
  // public void test0001() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, 32768.0, 5, 6);
  // assertEquals("32768.0", buf.toString());
  // }
  //
  // public void test0002() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, 19487171.0, 5, 6);
  // assertEquals("1.9487*10^7", buf.toString());
  // }
  //
  // public void test0003() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, 10604499373.0, 5, 6);
  // assertEquals("1.0604*10^10", buf.toString());
  // }
  //
  // public void test0004() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, 32768.0, 9, 6);
  // assertEquals("32768.0", buf.toString());
  // }
  //
  // public void test0005() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, 19487171.0, 9, 6);
  // assertEquals("19487171.0", buf.toString());
  // }
  //
  // public void test0006() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, 10604499373.0, 9, 6);
  // assertEquals("1.0604*10^10", buf.toString());
  // }
  //
  // public void test0008() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, Math.pow(6.7, -4), 5, 6);
  // assertEquals("0.00049625", buf.toString());
  // }
  //
  // public void test0009() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, Math.pow(6.7, 6), 5, 6);
  // assertEquals("90458.4", buf.toString());
  // }
  //
  // public void test0010() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, Math.pow(6.7, 8), 5, 6);
  // assertEquals("4.0607*10^6", buf.toString());
  // }
  //
  // public void test0011() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, Math.pow(6.7, -4), 3, 7);
  // assertEquals("4.96250*10^-4", buf.toString());
  // }
  //
  // public void test0012() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, Math.pow(6.7, 6), 3, 7);
  // assertEquals("9.04584*10^4", buf.toString());
  // }
  //
  // public void test0013() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, Math.pow(6.7, 8), 3, 7);
  // assertEquals("4.06068*10^6", buf.toString());
  // }
  //
  // public void test0014() {
  // StringBuilder buf = new StringBuilder();
  // ApfloatToMMA.apfloatToMMA(buf, 1234567.8, 6, 7);
  // assertEquals("1234567.8", buf.toString());
  // }

  public void test0015() {
    ApfloatNum apfloatNum = ApfloatNum.valueOf("1.234567123123", 12);
    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, false, 5, 5);
    String text = outputFormFactory.toString(apfloatNum, true);
    assertEquals("1.23456", text);
  }

  public void test0016() {
    ApcomplexNum apfloatNum = ApcomplexNum.valueOf("1.234567123123", "9.87654321", 12);
    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, false, 5, 5);
    String text = outputFormFactory.toString(apfloatNum, true);
    assertEquals("1.23456+I*9.87654", text);
  }

  public void test0017() {
    ApfloatNum apfloatNum = ApfloatNum.valueOf(Math.pow(6.7, 8));
    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, false, 5, 7);
    String text = outputFormFactory.toString(apfloatNum, true);
    assertEquals("4060676.7755664", text);
  }

  public void test0018() {
    StringBuilder buf = new StringBuilder();
    ApfloatToMMA.apfloatToMMA(buf, new Apfloat(Math.pow(6.7, 8)), 3, 5, true);
    assertEquals("4.06067*10^6", buf.toString());
  }
}
