package org.matheclipse.core.rubi.issues;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

// Integrate[Csch[x]*Log[Tanh[x]]*Sech[x], x]
public class DerivativeDevides extends AbstractRubiTestCase {
	public DerivativeDevides(String name) {
		super(name, false);
	}

	public void test0000() {
		check("PolynomialQ[Csch[x]*Sech[x],x]", //
				"False");
	}
	
	public void test0000b() {
		check("PolynomialQ[Log[Tanh[x]],x]", //
				"False");
	}
	
	public void test0000c() {
		check("MatchQ[Log[Tanh[x]],a_.*x /; FreeQ[a,x]]", //
				"False");
	}
	
	public void test0000d() {
		check("Module[{v=Block[{$ShowSteps=False}, ReplaceAll[D[Log[Tanh[x]],x],Sinc[z_]->Sin[z]/z]]}, v]", //
				"Csch(x)*Sech(x)");
	}
	
	public void test0000e() {
		check("1/(Coth[x]*Sech[x])", //
				"Sinh[x]");
	}
	
	public void test0000fa() {
		check("Coth[x]*Sech[x]^2", //Csch(x)^2*Sech(x)
				"Csch(x)*Sech(x)");
	}
	public void test0000fb() {
		check("1/(Coth[x]*Sech[x]^2)", //
				"Cosh(x)*Sinh(x)");
	}
	
	public void test0000g() {
		check("Module[{v=Block[{$ShowSteps=False}, ReplaceAll[D[Sech[x],x],Sinc[z_]->Sin[z]/z]]}, v]", //
				"-Sech(x)*Tanh(x)");
	}
	
	public void test0000h() {
		check("Module[{v=Block[{$ShowSteps=False}, ReplaceAll[D[Csch[x],x],Sinc[z_]->Sin[z]/z]]}, v]", //
				"-Coth(x)*Csch(x)");
	}
	
	public void test0000i() {
		check("Module[{v=Block[{$ShowSteps=False}, ReplaceAll[D[Csch[x]*Sech[x],x],Sinc[z_]->Sin[z]/z]]}, v]", //
				"-Csch(x)^2-Sech(x)^2");
	}
	
	public void test0000j() {
		check("Integrate::Simp[(-4*Coth[2*x]*Csch[2*x]*(Log[Tanh[x]])^(2))/(2), x]", //
				"-2*Coth(2*x)*Csch(2*x)*Log(Tanh(x))^2");
	}
	
	public void test0000k() {
		check("Integrate::Simp[(-Coth[x]*Csch[x]*Sech[x]-Csch[x]*Sech[x]*Tanh[x]*(Log[Tanh[x]])^(2))/(2), x]", //
				"-(Csch(x)^2+Log(Tanh(x))^2*Sech(x)^2)/2");
	}
	
	/**
	 * <pre>
	 * Int[(u_)*(y_)^(m_.), x_Symbol] := 
	 *   With[{q = DerivativeDivides[y, u, x]}, Simp[(q*y^(m + 1))/(m + 1), x] 
	 *       /;  !FalseQ[q]] 
	 *   /; FreeQ[m, x] && NeQ[m, -1]
	 * </pre>
	 */
	public void test0001() {
		check("Integrate::DerivativeDivides[Log[Tanh[x]]*Sech[x], Csch[x], x]", //
				"False");
	}

	public void test0002() {
		check("Integrate::DerivativeDivides[Log[Tanh[x]], Csch[x]*Sech[x], x]", //
				"1");
	}

	public void test0003() {
		check("Integrate::DerivativeDivides[Log[Tanh[x]]*Csch[x], Sech[x], x]", //
				"False");
	}
	
	public void test0004() {
		check("Integrate::EasyDQ[Log[Tanh[x]]*Sech[x], x]", //
				"False");
	}

	public void test0005() {
		check("Integrate::EasyDQ[Log[Tanh[x]],  x]", //
				"True");
	}
	
	public void test0006() {
		check("Integrate::EasyDQ[Csch[x],  x]", //
				"True");
	}
	
	public void test0007() {
		check("Integrate::EasyDQ[Sech[x],  x]", //
				"True");
	}

	public void test0008() {
		check("Integrate::EasyDQ[Log[Tanh[x]]*Csch[x], x]", //
				"False");
	}
}