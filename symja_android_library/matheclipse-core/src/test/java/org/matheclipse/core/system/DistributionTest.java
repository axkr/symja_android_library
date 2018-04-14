package org.matheclipse.core.system;

public class DistributionTest extends AbstractTestCase {
	public DistributionTest(String name) {
		super(name);
	}

	public void testExponentialDistribution() {
		check("Quantile(ExponentialDistribution(x), {1/4, 1/2, 3/4})", //
				"{Log(4/3)/x,Log(2)/x,Log(4)/x}");
	}
	
	public void testFrechetDistribution() {
		check("Quantile(FrechetDistribution(n, m), {1/4, 1/2, 3/4})", //
				"{m/Log(4)^(1/n),m/Log(2)^(1/n),m/Log(4/3)^(1/n)}");
	}
	
	public void testGammaDistribution() {
		check("Mean(GammaDistribution(a, b, g, d))", //
				"d+(b*Gamma(a+1/g))/Gamma(a)");
		check("CDF(GammaDistribution(a, b))", //
				"Piecewise({{GammaRegularized(a,0,#1/b),#1>0}},0)&");
		check("CDF(GammaDistribution(a, b, g, d))", //
				"Piecewise({{GammaRegularized(a,0,((-d+#1)/b)^g),#1>d}},0)&");
		check("PDF(GammaDistribution(a, b))", //
				"Piecewise({{1/(b^a*E^(#1/b)*Gamma(a)*#1^(1-a)),#1>0}},0)&");
		check("PDF(GammaDistribution(a, b, g, d))", //
				"Piecewise({{g/(E^((-d+#1)/b)^g*b*Gamma(a)*((-d+#1)/b)^(1-a*g)),#1>d}},0)&");
		check("Quantile(GammaDistribution(a, b), {1/4, 1/2, 3/4})", //
				"{b*InverseGammaRegularized(a,0,1/4),b*InverseGammaRegularized(a,0,1/2),b*InverseGammaRegularized(a,\n" + 
				"0,3/4)}");
		check("Quantile(GammaDistribution(a, b, g, d), {1/4, 1/2, 3/4})", //
				"{d+b*InverseGammaRegularized(a,0,1/4)^(1/g),d+b*InverseGammaRegularized(a,0,1/2)^(1/g),d+b*InverseGammaRegularized(a,\n" + 
				"0,3/4)^(1/g)}");
	}
	
	public void testNormalDistribution() {
		check("CDF(NormalDistribution( ), x)", //
				"1/2*(2-Erfc(x/Sqrt(2)))");
		check("CDF(NormalDistribution(n,m), x)", //
				"Erfc((n-x)/(Sqrt(2)*m))/2");
		check("PDF(NormalDistribution( ), x)", //
				"1/(Sqrt(2)*E^(x^2/2)*Sqrt(Pi))");
		check("PDF(NormalDistribution(m, s), x)", //
				"1/(Sqrt(2)*E^((-m+x)^2/(2*s^2))*Sqrt(Pi)*s)");
		
		check("Mean(NormalDistribution(m, s))", //
				"m");
		check("StandardDeviation(NormalDistribution(m, s))", //
				"s");
		check("Variance(NormalDistribution(m, s))", //
				"s^2");
		
		check("Quantile(NormalDistribution(m, s), q)", //
				"ConditionalExpression(m-Sqrt(2)*s*InverseErfc(2*q),0<=q<=1)");
		check("Quantile(NormalDistribution(2, 3), {1/4, 1/2, 3/4})", //
				"{2-3*Sqrt(2)*InverseErfc(1/2),2,2-3*Sqrt(2)*InverseErfc(3/2)}");
	}

	
}
