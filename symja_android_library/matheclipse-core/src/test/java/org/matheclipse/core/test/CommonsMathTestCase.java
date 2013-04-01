package org.matheclipse.core.test;

import junit.framework.TestCase;

import org.apache.commons.math3.dfp.Dfp;
import org.apache.commons.math3.dfp.DfpField;

/**
 * Tests visitors
 */
public class CommonsMathTestCase extends TestCase {

	public CommonsMathTestCase(String name) {
		super(name);
	}

	/**
	 */
	public void testSqrt() {
		DfpField f = new DfpField(100);
		Dfp dfp = f.newDfp(2);
		String res = dfp.sqrt().toString();
		assertEquals(res, "1.414213562373095048801688724209698078569671875376948073176679737990732478462107038850387534327642");
		assertEquals(res.length(), 98);

		f = new DfpField(101);
		dfp = f.newDfp(2);
		res = dfp.sqrt().toString();
		assertEquals(res, "1.4142135623730950488016887242096980785696718753769480731766797379907324784621070388503875343276415728");
		assertEquals(res.length(), 102);
		
		
		f = new DfpField(200);
		dfp = f.newDfp(2);
		res = dfp.sqrt().toString();
		assertEquals(res.length(), 198);
	}
}
