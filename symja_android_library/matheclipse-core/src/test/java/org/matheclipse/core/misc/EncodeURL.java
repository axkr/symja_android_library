package org.matheclipse.core.misc;

import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncodeURL extends TestCase {
	public EncodeURL(String name) {
		super(name);
	}

	public void testEncode001() {
		String encoded = encodeIt("http://matheclipse.org/eval.jsp?ci=x1:X:i:10|y1:Y:i:2&ca=x!:x1!|Fibonacci[x]:Fibonacci[x1]|Binomial[x,y]:Binomial[x1,y1]");
		assertEquals(
				"http%3A%2F%2Fmatheclipse.org%2Feval.jsp%3Fci%3Dx1%3AX%3Ai%3A10%7Cy1%3AY%3Ai%3A2%26ca%3Dx%21%3Ax1%21%7CFibonacci%5Bx%5D%3AFibonacci%5Bx1%5D%7CBinomial%5Bx%2Cy%5D%3ABinomial%5Bx1%2Cy1%5D",
				encoded);
	}

	public void testEncode002() {
		String encoded = encodeIt("http://matheclipse.org/eval.jsp?ci=ta1:Insert an expression for evaluation:t:D[Sin[x]*Cos[x],x]&ca=Symbolic:ta1|Numeric:N[ta1]");
		assertEquals("http%3A%2F%2Fmatheclipse.org%2Feval.jsp%3Fci%3Dta1%3AInsert+an+expression+for+evaluation%3At%3AD%5BSin%5Bx%5D*Cos%5Bx%5D%2Cx%5D%26ca%3DSymbolic%3Ata1%7CNumeric%3AN%5Bta1%5D", encoded);
	}

	public void testEncode003() {
		String encoded = encodeIt("http://matheclipse.org/eval.jsp?ci=ta1:Insert an expression depending on <b>x</b>:t:4+x^2+2*x+3*x^3&ca=Differentiate:D[ta1,x]|Integrate:Integrate[ta1,x]|Factor:Factor[ta1]");
		assertEquals("http%3A%2F%2Fmatheclipse.org%2Feval.jsp%3Fci%3Dta1%3AInsert+an+expression+depending+on+%3Cb%3Ex%3C%2Fb%3E%3At%3A4%2Bx%5E2%2B2*x%2B3*x%5E3%26ca%3DDifferentiate%3AD%5Bta1%2Cx%5D%7CIntegrate%3AIntegrate%5Bta1%2Cx%5D%7CFactor%3AFactor%5Bta1%5D", encoded);
	}

	private String encodeIt(String str) {
		String encoded = "";
		try {
			encoded = URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encoded;
	}
}
