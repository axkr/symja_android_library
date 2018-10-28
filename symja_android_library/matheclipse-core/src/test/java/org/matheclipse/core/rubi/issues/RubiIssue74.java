package org.matheclipse.core.rubi.issues;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

// Integrate[ArcCos[x+1],x] 
public class RubiIssue74 extends AbstractRubiTestCase {
	public RubiIssue74(String name) {
		super(name, false);
	}

	public void test0001() {
		check("Part[Rubi`SignOfFactor[-I]*Rubi`SignOfFactor[x]*Rubi`SignOfFactor[1/Sqrt[2*x+x^2]],1]", //
				"1");
		check("(Rubi`SignOfFactor[-I]*Rubi`SignOfFactor[x]*Rubi`SignOfFactor[1/Sqrt[2*x+x^2]])[[1]]==1", //
				"True");
	}

	public void test0098() {
		check("Rubi`FunctionOfTrig[1, Null, x]", "Null");
	}

	public void test0118() {
		check("Rubi`InverseFunctionFreeQ[ArcCos[x], x]", "False");
	}

	public void test0119() {
		check("Rubi`InverseFunctionFreeQ[ArcCos[1 + x], x]", "False");
	}

	public void test0120() {
		check("Rubi`InverseFunctionQ[ArcCos[x]]", "True");
	}

	public void test0121() {
		check("Rubi`InverseFunctionQ[ArcCos[1 + x]]", "True");
	}

	public void test0122() {
		check("Rubi`InverseTrigQ[ArcCos[x]]", "True");
	}

	public void test0123() {
		check("Rubi`InverseTrigQ[ArcCos[1 + x]]", "True");
	}

	public void test0263() {
		check("RealNumberQ[Sqrt[2]]", "False");
		check("RealNumberQ[0]", "True");
	}

	public void test0264() {
		check("RealNumberQ[1]", "True");
	}
}