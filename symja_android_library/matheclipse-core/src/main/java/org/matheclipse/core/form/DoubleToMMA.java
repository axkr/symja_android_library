package org.matheclipse.core.form;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Convert a Java <code>double</code> value into a string similar to the Mathematica output format.
 *
 */
public class DoubleToMMA {
	private final static String HASH_STR = "##############################";

	/**
	 * Convert a Java <code>double</code> value into a string similar to the Mathematica output format.
	 * 
	 * @param buf
	 *            a string builder where the output should be appended
	 * @param value
	 * @param exponent
	 *            use scientific notation for all numbers with exponents outside the range <code>-exponent</code> to
	 *            <code>exponent</code>.
	 * @param significantFigures
	 */
	public static void doubleToMMA(Appendable buf, double value, int exponent, int significantFigures)
			throws IOException {
		String s = String.format(Locale.US, "%16.16E", value);
		int start = s.indexOf('E');
		int exp = Integer.parseInt(s.substring(start + 1));
		if (-exponent <= exp && exp <= exponent) {
			DecimalFormatSymbols usSymbols = new DecimalFormatSymbols(Locale.US);
			DecimalFormat format;
			int hashSize;
			if (exp > 0) {
				hashSize = significantFigures - exp - 1;
				if (hashSize < 0) {
					hashSize = 1;
				}
				if (hashSize >= HASH_STR.length()) {
					hashSize = HASH_STR.length();
				}
				format = new DecimalFormat(//
						HASH_STR.substring(0, exp) + //
								"0." + //
								HASH_STR.substring(0, hashSize),
						usSymbols);
			} else {
				hashSize = -exp + significantFigures - 2;
				if (hashSize < 0) {
					hashSize = 1;
				}
				if (hashSize >= HASH_STR.length()) {
					hashSize = HASH_STR.length();
				}
				format = new DecimalFormat(//
						"#." + //
								HASH_STR.substring(0, -exp + significantFigures - 2),
						usSymbols);
			}
			String test = format.format(value);
			start = test.indexOf('E');
			if (start > 0) {
				test = test.substring(0, start);
			}
			test = test.trim();
			if (test.contains(".")) {
				for (int i = test.length() - 1; i >= 0; i--) {
					if (test.charAt(i) != '0') {
						if (test.charAt(i) == '.') {
							// ensure trailing zero after decimal point
							test = test.substring(0, i + 2);
							break;
						}
						test = test.substring(0, i + 1);
						break;
					}
				}
			}

			buf.append(test);
			if (test.indexOf(".") < 0) {
				buf.append(".0");
			}
			return;
		}
		doubleToScientific(buf, value, significantFigures - 1, exp);
	}

	/**
	 * Convert a Java <code>double</code> value into a string similar to the Mathematica output format.
	 * 
	 * @param buf
	 *            a string builder where the output should be appended
	 * @param value
	 *            the double value which should be formatted
	 * @param exponent
	 *            use scientific notation for all numbers with exponents outside the range <code>-exponent</code> to
	 *            <code>exponent</code>.
	 * @param significantFigures
	 *            the number of significant figures which should be printed
	 */
	public static void doubleToMMA(StringBuilder buf, double value, int exponent, int significantFigures) {
		try {
			doubleToMMA((Appendable) buf, value, exponent, significantFigures);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	public static void doubleToScientific(Appendable buf, double value, int significantFigures, int exp)
			throws IOException {
		String s;
		int start;
		s = String.format(Locale.US, "%1." + (significantFigures - 1) + "E", value);
		start = s.indexOf('E');
		if (exp == Integer.MIN_VALUE) {
			exp = Integer.parseInt(s.substring(start + 1));
		}
		s = s.substring(0, start);
		buf.append(s.trim());
		buf.append("*10^");
		buf.append(Integer.toString(exp));
	}

	/**
	 * Convert a Java <code>double</code> value into a string similar to the Mathematica scientific output format.
	 * 
	 * @param buf
	 *            a string builder where the output should be appended
	 * @param value
	 *            the double value which should be formatted
	 * @param significantFigures
	 *            the number of significant figures which should be printed
	 */
	public static void doubleToScientific(StringBuilder buf, double value, int significantFigures) {
		try {
			doubleToScientific(buf, value, significantFigures, Integer.MIN_VALUE);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	private DoubleToMMA() {
	}

}
