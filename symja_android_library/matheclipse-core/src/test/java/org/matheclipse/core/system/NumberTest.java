package org.matheclipse.core.system;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FractionSym;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;

import junit.framework.TestCase;

public class NumberTest extends TestCase {

	public void testApfloat() {
		// simulate Symja expr: N(Pi, 30) + E
		Apfloat f = ApfloatMath.pi(30).add(new Apfloat(Math.E, 30));

		assertEquals(f.toString(), "5.85987448204883829099102473027");
	}

	public void testPower() {
		IFraction f = FractionSym.valueOf(2, 3);

		assertEquals(f.pow(-2).toString(), "9/4");

		IFraction f0 = FractionSym.valueOf(5, 14);
		assertEquals(f0.pow(2).toString(), "25/196");
	}

	/**
	 * Format a double value with a <code>java.text.DecimalFormat</code> object.
	 */
	public void testNumberFormat() {
		StringBuilder buf = new StringBuilder();
		try {
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
			DecimalFormat decimalFormat = new DecimalFormat("#.#####", otherSymbols);
			OutputFormFactory factory = OutputFormFactory.get(true, false, decimalFormat);

			IExpr expr = F.num("12345.123456789");
			factory.convert(buf, expr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(buf.toString(), "12345.12346");
	}
}
