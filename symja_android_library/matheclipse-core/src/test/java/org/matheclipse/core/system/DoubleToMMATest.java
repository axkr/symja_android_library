package org.matheclipse.core.system;

import org.matheclipse.core.form.DoubleToMMA;

import junit.framework.TestCase;

/**
 * Convert a Java <code>double</code> value into a string similar to the Mathematica output format.
 *
 */
public class DoubleToMMATest extends TestCase {

	public DoubleToMMATest() {
		super("DoubleToString");
	}

	public void testTen() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, 10.0, 9, 6);
		assertEquals("10.0", buf.toString());
	}

	public void test0001() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, 32768.0, 5, 6);
		assertEquals("32768.0", buf.toString());
	}

	public void test0002() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, 19487171.0, 5, 6);
		assertEquals("1.9487*10^7", buf.toString());
	}

	public void test0003() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, 10604499373.0, 5, 6);
		assertEquals("1.0604*10^10", buf.toString());
	}

	public void test0004() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, 32768.0, 9, 6);
		assertEquals("32768.0", buf.toString());
	}

	public void test0005() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, 19487171.0, 9, 6);
		assertEquals("19487171.0", buf.toString());
	}

	public void test0006() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, 10604499373.0, 9, 6);
		assertEquals("1.0604*10^10", buf.toString());
	}

	public void test0008() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, Math.pow(6.7, -4), 5, 6);
		assertEquals("0.00049625", buf.toString());
	}

	public void test0009() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, Math.pow(6.7, 6), 5, 6);
		assertEquals("90458.4", buf.toString());
	}

	public void test0010() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, Math.pow(6.7, 8), 5, 6);
		assertEquals("4.0607*10^6", buf.toString());
	}

	public void test0011() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, Math.pow(6.7, -4), 3, 7);
		assertEquals("4.96250*10^-4", buf.toString());
	}

	public void test0012() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, Math.pow(6.7, 6), 3, 7);
		assertEquals("9.04584*10^4", buf.toString());
	}

	public void test0013() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToMMA(buf, Math.pow(6.7, 8), 3, 7);
		assertEquals("4.06068*10^6", buf.toString());
	}

	public void testScientific0001() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToScientific(buf, Math.pow(6.7, -4), 7);
		assertEquals("4.962503*10^-4", buf.toString());
	}

	public void testScientific0002() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToScientific(buf, Math.pow(6.7, 6), 7);
		assertEquals("9.045838*10^4", buf.toString());
	}

	public void testScientific0003() {
		StringBuilder buf = new StringBuilder();
		DoubleToMMA.doubleToScientific(buf, Math.pow(6.7, 8), 7);
		assertEquals("4.060677*10^6", buf.toString());
	}
}
