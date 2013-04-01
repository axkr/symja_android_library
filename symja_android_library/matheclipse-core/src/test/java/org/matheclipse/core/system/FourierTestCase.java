package org.matheclipse.core.system;

import org.matheclipse.core.system.AbstractTestCase;

/**
 * Tests Fourier transformations
 * 
 */
public class FourierTestCase extends AbstractTestCase {
	public FourierTestCase(String name) {
		super(name);
	}

	public void testFourier() {
		check(
				"NFourierTransform[t+1,t,v]",
				"{7.0,-1.0+I*2.414213562373095,-1.0000000000000002+I*1.0,-1.0000000000000004+I*0.41421356237309503,-1.0,-0.9999999999999999+I*(-0.4142135623730949),-0.9999999999999998+I*(-1.0),-0.9999999999999997+I*(-2.414213562373095)}");
	}
}
