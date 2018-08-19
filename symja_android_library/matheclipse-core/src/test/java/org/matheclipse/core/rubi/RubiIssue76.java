package org.matheclipse.core.rubi;

// Int[1/((2+3x)(52-54x+27x^2)^(1/3)),x] 
public class RubiIssue76 extends AbstractRubiTestCase {
	public RubiIssue76(String name) {
		super(name, false);
	}

	public void test0220() { 
//		check("Integrate[1/((2+3*x)*(52-54*x+27*x^2)^(1/3)), x]",//
//				"");
	}
	
	public void test0221() {
		check("3/Sqrt[3]", "Sqrt[3]");
		check("1/Sqrt[3]", "1/Sqrt[3]");
		check("Integrate::PowerQ[1/Sqrt[3]]", "True");
		check("Integrate::RationalQ[1/Sqrt[3]]", "False");
		check("Integrate::FractionQ[1/Sqrt[3]]", "False");
		check("Integrate::NumericFactor[1/Sqrt[3]]", "1/3");
		check("(1/Sqrt[3])/(1/3)", "3/Sqrt[3]");
		check("Integrate::ContentFactor[1/Sqrt[3]]", "1/Sqrt[3]");
		check("Integrate::NonnumericFactors[1/Sqrt[3]]", "Sqrt[3]");
	}

	public void test0222() {
		check("Integrate::NonnumericFactors[5^(-1/3)]", "5^(2/3)");
	}

	public void test0223() {
		check("Integrate::NonnumericFactors[2^(2/3)/(27*Sqrt[3]*5^(1/3))]", "Sqrt[3]*10^(2/3)");
	}

	public void test0224() {
		check("Integrate::NonnumericFactors[1/(6*10^(2/3))]", "10^(1/3)");
	}

	public void test0225() {
		check("Integrate::NonnumericFactors[10^(-2/3)]", "10^(1/3)");
	}

	public void test0226() {
		check("Integrate::NonnumericFactors[1/(3*Sqrt[3]*10^(2/3))]", "Sqrt[3]*10^(1/3)");
	}
}