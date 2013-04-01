package org.matheclipse.core.system;

import org.matheclipse.core.system.AbstractTestCase;

/**
 * Tests for the Java port of the <a
 * href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class IntegrateTestCase extends AbstractTestCase {
	public IntegrateTestCase(String name) {
		super(name);
	}

	public void testSystem001() {
		check("Integrate[x^(-1),x]", "Log[x]");
		check("Integrate[x^a,x]", "x^(a+1)*(a+1)^(-1)");
		check("Integrate[x^10,x]", "1/11*x^11");
		check("Simplify[1/2*(2*x+2)]", "x+1");
		check("Simplify[1/2*(2*x+2)*(1/2)^(1/2)]", "(x+1)*(1/2)^(1/2)");
		check("Simplify[Integrate[(8*x+1)/(x^2+x+1)^2,x]]", "(-2*x-5)*(x^2+x+1)^(-1)-4*ArcTan[(2*x+1)*3^(-1/2)]*3^(-1/2)");

		check("Apart[1/(x^3+1)]", "(-1/3*x+2/3)*(x^2-x+1)^(-1)+1/3*(x+1)^(-1)");
		check("Integrate[1/(x^5+x-7),x]", "Integrate[(x^5+x-7)^(-1),x]");
		check("Integrate[1/(x^5-7),x]", "Integrate[(x^5-7)^(-1),x]");
		check("Integrate[1/(x-2),x]", "Log[x-2]");
		check("Integrate[(x-2)^(-2),x]", "-(x-2)^(-1)");
		check("Integrate[(x-2)^(-3),x]", "(-1/2)*(x-2)^(-2)");
		check("Integrate[(x^2+2*x+3)^(-1),x]", "ArcTan[1/2*(2*x+2)*(1/2)^(1/2)]*(1/2)^(1/2)");
		check("Integrate[1/(x^2+1),x]", "ArcTan[x]");
		check("Integrate[(2*x+5)/(x^2-2*x+5),x]", "7/2*ArcTan[1/4*(2*x-2)]+Log[x^2-2*x+5]");
		check("Integrate[(8*x+1)/(x^2+2*x+1),x]", "7*(x+1)^(-1)+8*Log[x+1]");

		check("Integrate[1/(x^3+1),x]", "ArcTan[(2*x-1)*3^(-1/2)]*3^(-1/2)-1/6*Log[x^2-x+1]+1/3*Log[x+1]");
		check("Simplify[Integrate[1/3*(2-x)*(x^2-x+1)^(-1),x]]", "ArcTan[(2*x-1)*3^(-1/2)]*3^(-1/2)-1/6*Log[x^2-x+1]");
		check("Integrate[1/3*(2-x)*(x^2-x+1)^(-1)+1/3*(x+1)^(-1),x]", "ArcTan[(2*x-1)*3^(-1/2)]*3^(-1/2)-1/6*Log[x^2-x+1]+1/3*Log[x+1]");
		check("Integrate[E^x*(2-x^2),x]", "-E^x*x^2+2*E^x*x");
		check("Integrate[(x^2+1)Log[x],x]", "1/3*Log[x]*x^3-1/9*x^3+x*Log[x]-x");
		check("Integrate[x*Log[x],x]", "1/2*Log[x]*x^2-1/4*x^2");

		check("Apart[2*x^2/(x^3+1)]", "(4/3*x-2/3)*(x^2-x+1)^(-1)+2/3*(x+1)^(-1)");

		check("Integrate[2*x^2/(x^3+1),x]", "2*(1/3*Log[x^2-x+1]+1/3*Log[x+1])");
		// check("Integrate[Sin[x]^3,x]", "-1/3*Cos[x]*Sin[x]^2-2/3*Cos[x]");
		check("Integrate[Sin[x]^3,x]", "1/3*Cos[x]^3-Cos[x]");
		// check("Integrate[Cos[2x]^3,x]", "1/6*Cos[2*x]^2*Sin[2*x]+1/3*Sin[2*x]");
		check("Integrate[Cos[2x]^3,x]", "1/2*Sin[2*x]-1/6*Sin[2*x]^3");
		check("Integrate[x,x]", "1/2*x^2");
		check("Integrate[2x,x]", "x^2");
		check("Integrate[h[x],x]", "Integrate[h[x],x]");
		check("Integrate[f[x]+g[x]+h[x],x]", "Integrate[h[x],x]+Integrate[g[x],x]+Integrate[f[x],x]");
		check("Integrate[Sin[x],x]", "-Cos[x]");
		check("Integrate[Sin[10*x],x]", "(-1/10)*Cos[10*x]");
		check("Integrate[Sin[Pi+10*x],x]", "(-1/10)*Cos[10*x+Pi]");
		check("Integrate[E^(a*x),x]", "E^(a*x)*a^(-1)");
		check("Integrate[x*E^(a*x),x]", "E^(a*x)*a^(-1)*x-E^(a*x)*a^(-2)");
		check("Integrate[x*E^x,x]", "E^x*x-E^x");
		check("Integrate[x^2*E^x,x]", "E^x*x^2-2*E^x*x+2*E^x");
		check("Integrate[x^2*E^(a*x),x]", "E^(a*x)*a^(-1)*x^2-2*E^(a*x)*a^(-2)*x+2*E^(a*x)*a^(-3)");
		check("Integrate[x^3*E^(a*x),x]", "E^(a*x)*a^(-1)*x^3-3*E^(a*x)*a^(-2)*x^2+6*E^(a*x)*a^(-3)*x-6*E^(a*x)*a^(-4)");
		check("(-1.0)/48", "-0.020833333333333332");
		check("NIntegrate[(x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1}]", "-0.020833327124516472");
		check("NIntegrate[(x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1},Simpson]", "-0.0208333320915699");
		check("NIntegrate[(x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1},Romberg]", "-0.020833333333333332");
		check("NIntegrate[(x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1},LegendreGauss]", "-0.020833333333333336");
	}

	public void testSystem002() {
//		check("Integrate[(x^7 - 24*x^4 - 4*x^2 + 8*x - 8)/(x^8 + 6*x^6 + 12*x^4 + 8*x^2),x]",
//				"-x*(x^2+2)^(-1)+3*(x^2+2)^(-1)+6*x*(x^2+2)^(-2)+x^(-1)+Log[x]");
//		check("Integrate[(x^7-24*x^4-4*x^2+8*x-8)*x^(-2)*(x^2+2)^(-3),x]",
//				"-x*(x^2+2)^(-1)+3*(x^2+2)^(-1)+6*x*(x^2+2)^(-2)+x^(-1)+Log[x]");
		check("Simplify[D[(x+2)*(3*x^2+2*x+2)*x^(-1)*(x^2+2)^(-2)+Log[x],x]]", "(x^7-24*x^4-4*x^2+8*x-8)*x^(-2)*(x^2+2)^(-3)");

		check("Integrate[10/(x-3)^4,x]", "(-10/3)*(x-3)^(-3)");

		check("Integrate[(-1+x)*(1+x)*(1+x^2)*(1+x^4)*(1+x^8),x]", "1/17*x^17-x");
		check("Integrate[(10 x^2 - 63 x + 29)/(x^3 - 11 x^2 + 40 x -48),x]", "63*(x-4)^(-1)-70*Log[x-3]+80*Log[x-4]");

		check("Apart[(x^7 - 24*x^4 - 4*x^2 + 8*x - 8)/(x^8 + 6*x^6 + 12*x^4 + 8*x^2)]",
				"(x^2+2)^(-1)+(-6*x-22)*(x^2+2)^(-2)+48*(x^2+2)^(-3)+x^(-1)-x^(-2)");
		check("Integrate[(x^2+2)^(-1),x]", "ArcTan[x*(1/2)^(1/2)]*(1/2)^(1/2)");
		check("Integrate[(-6*x-22)*(x^2+2)^(-2),x]", "3*(x^2+2)^(-1)-22*(1/4*x*(x^2+2)^(-1)+1/4*ArcTan[x*(1/2)^(1/2)]*(1/2)^(1/2))");
		check("Integrate[48*(x^2+2)^(-3),x]", "48*(1/8*x*(x^2+2)^(-2)+3/8*(1/4*x*(x^2+2)^(-1)+1/4*ArcTan[x*(1/2)^(1/2)]*(1/2)^(\n" + 
				"1/2)))");
		check("Integrate[-x^(-2),x]", "x^(-1)");
		check("Integrate[x^(-1),x]", "Log[x]");
		check("Integrate[Exp[-x^4],x]", "(-1/4)*x*Gamma[1/4,x^4]*x^4^(-1/4)");

	}

}
