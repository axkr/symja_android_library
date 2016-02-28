package org.matheclipse.core.system;

import org.matheclipse.core.expression.FractionSym;
import org.matheclipse.core.interfaces.IFraction;

import junit.framework.TestCase;

public class NumberTestCases extends TestCase {
	public void testPower() {
		IFraction f =  FractionSym.valueOf(2,3);
		
		assertEquals(f.pow(-2).toString(), "9/4");
		
		IFraction f0 =  FractionSym.valueOf(5,14);
		assertEquals(f0.pow(2).toString(), "25/196");
	}

}
