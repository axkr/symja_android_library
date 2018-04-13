package org.matheclipse.core.system;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class DistributionTest extends AbstractTestCase {
	public DistributionTest(String name) {
		super(name);
	}

	public void testExponentialDistribution() {
		check("Quantile(ExponentialDistribution(x), {1/4, 1/2, 3/4})", //
				"{Log(4/3)/x,Log(2)/x,Log(4)/x}");
	}
	
	public void testNormalDistribution() {
		check("CDF(NormalDistribution( ), x)", //
				"1/2*(2-Erfc(x/Sqrt(2)))");
		check("PDF(NormalDistribution( ), x)", //
				"1/(Sqrt(2)*E^(x^2/2)*Sqrt(Pi))");
		
		check("CDF(NormalDistribution(m, s), x)", //
				"Erfc((m-x)/(Sqrt(2)*s))/2");
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
