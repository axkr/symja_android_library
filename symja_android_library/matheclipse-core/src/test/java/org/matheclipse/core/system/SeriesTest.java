package org.matheclipse.core.system;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based integrator</a>.
 * 
 */
public class SeriesTest extends AbstractTestCase {
	public SeriesTest(String name) {
		super(name);
	}

	public void testComposeSeries() {
		check("Series(Log(x), {x, 1, 1}) ", //
				"(-1+x)+O(-1+x)^2");
		check("Series(Log(x), {x, 1, 5}) ", //
				"(-1+x)-(1-x)^2/2+(-1+x)^3/3-(1-x)^4/4+(-1+x)^5/5+O(-1+x)^6");
		check("SeriesData(x, 0, {1,3,0,0,0}, 0, 5, 1)", //
				"1+3*x+O(x)^5");
		check("ComposeSeries(Series(Log(x), {x, 1, 1}), SeriesData(x, 0, {1,3,0,0,0}, 0, 5, 1))", //
				"3*x+O(x)^5");
		check("ComposeSeries(Series(Log(x), {x, 1, 2}), SeriesData(x, 0, {1,3,0,0,0}, 0, 5, 1))", //
				"3*x-9/2*x^2+O(x)^5");
		check("ComposeSeries(Series(Log(x), {x, 1, 5}), SeriesData(x, 0, {1,3,0,0,0}, 0, 5, 1))", //
				"3*x-9/2*x^2+9*x^3-81/4*x^4+O(x)^5");

		check("s2 = Series(Sin(x), {x, 0, 10})", //
				"x-x^3/6+x^5/120-x^7/5040+x^9/362880+O(x)^11");
		check("s2^0", //
				"1+O(x)^11");
		check("s1 = Series(Exp(x), {x, 0, 10})", //
				"1+x+x^2/2+x^3/6+x^4/24+x^5/120+x^6/720+x^7/5040+x^8/40320+x^9/362880+x^10/\n" + "3628800+O(x)^11");
		check("ComposeSeries(s1, s2)", //
				"1+x+x^2/2-x^4/8-x^5/15-x^6/240+x^7/90+31/5760*x^8+x^9/5670-2951/3628800*x^10+O(x)^\n" + "11");

		check("s1=SeriesData(x, 0, {1, 1,1,1}, 0, 4, 1)", //
				"1+x+x^2+x^3+O(x)^4");
		check("s2=SeriesData(x, 0, {1, 3}, 2, 4, 1)", //
				"x^2+3*x^3+O(x)^4");
		check("ComposeSeries(s2, s1)", "ComposeSeries(x^2+3*x^3+O(x)^4,1+x+x^2+x^3+O(x)^4)");
		check("ComposeSeries(s2, s1-1)", "x^2+5*x^3+O(x)^4");

		check("s1=SeriesData(x, 0, {1, 1,0,0}, 0, 4, 1)", //
				"1+x+O(x)^4");
		check("s2=SeriesData(x, 0, {1, 3}, 2, 4, 1)", //
				"x^2+3*x^3+O(x)^4");
		check("ComposeSeries(s2, s1-1)", //
				"x^2+3*x^3+O(x)^4");

		check("ComposeSeries(SeriesData(x, 0, {1, 3}, 2, 4, 1), SeriesData(x, 0, {1, 1,0,0}, 0, 4, 1) - 1)", //
				"x^2+3*x^3+O(x)^4");
	}

	public void testInverseSeries() {
		check("InverseSeries(Series(Log(x+1), {x, 0, 9}))", //
				"x+x^2/2+x^3/6+x^4/24+x^5/120+x^6/720+x^7/5040+x^8/40320+x^9/362880+O(x)^10");

		check("InverseSeries(Series(Sin(x), {x, 0, 9}))", //
				"x+x^3/6+3/40*x^5+5/112*x^7+35/1152*x^9+O(x)^10");
		check("InverseSeries(Series(ArcSin(x), {x, 0, 9}))", //
				"x-x^3/6+x^5/120-x^7/5040+x^9/362880+O(x)^10");
		check("InverseSeries(Series(Log(x+1), {x, 0, 9}))", //
				"x+x^2/2+x^3/6+x^4/24+x^5/120+x^6/720+x^7/5040+x^8/40320+x^9/362880+O(x)^10");
	}

	public void testNormal() {
		check("Normal(SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1))", //
				"1/x-x-4*x^2-17*x^3-88*x^4-549*x^5");
		check("Normal(Series(Exp(x),{x,0,5}))", //
				"1+x+x^2/2+x^3/6+x^4/24+x^5/120");
		check("Normal(SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2))", //
				"Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880");
	}

	public void testSeries() {
		check("Series(Log(x),{x,a,3})", //
				"Log(a)+(-a+x)/a-(-a+x)^2/(2*a^2)+(-a+x)^3/(3*a^3)+O(-a+x)^4");
		check("Series(f(x),{x,a,3})", //
				"f(a)+f'(a)*(-a+x)+1/2*f''(a)*(-a+x)^2+1/6*Derivative(3)[f][a]*(-a+x)^3+O(-a+x)^4");
		check("s1=SeriesData(x, 0, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 2, 9, 1)", //
				"x^2+x^3+x^4+x^5+x^6+x^7+x^8+O(x)^9");
		check("s2=SeriesData(x, 0, {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800}, 3, 11, 1)", //
				"x^3+x^4+2*x^5+6*x^6+24*x^7+120*x^8+720*x^9+5040*x^10+O(x)^11");
		// SeriesData[x, 0, {1, 2, 4, 10, 34, 154, 874}, 5, 12, 1]
		check("s1*s2", "x^5+2*x^6+4*x^7+10*x^8+34*x^9+154*x^10+874*x^11+O(x)^12");
		check("s1+s2", //
				"x^2+2*x^3+2*x^4+3*x^5+7*x^6+25*x^7+121*x^8+O(x)^9");

		check("s1=SeriesData(x, 0, {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 0, 11, 1)", //
				"1+x+x^2+x^3+x^4+x^5+x^6+x^7+x^8+x^9+x^10+O(x)^11");
		check("s2=SeriesData(x, 0, {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800}, 0, 11, 1)", //
				"1+x+2*x^2+6*x^3+24*x^4+120*x^5+720*x^6+5040*x^7+40320*x^8+362880*x^9+3628800*x^\n" //
						+ "10+O(x)^11");
		// check("s1*s2 // FullForm",
		// "1+2*x+4*x^2+10*x^3+34*x^4+154*x^5+874*x^6+5914*x^7+46234*x^8+409114*x^9+4037914*x^\n" + "10+O(x)^11");
		check("s1", //
				"1+x+x^2+x^3+x^4+x^5+x^6+x^7+x^8+x^9+x^10+O(x)^11");
		check("s2", //
				"1+x+2*x^2+6*x^3+24*x^4+120*x^5+720*x^6+5040*x^7+40320*x^8+362880*x^9+3628800*x^\n" + //
						"10+O(x)^11");
		check("s1*s2",
				"1+2*x+4*x^2+10*x^3+34*x^4+154*x^5+874*x^6+5914*x^7+46234*x^8+409114*x^9+4037914*x^\n" + "10+O(x)^11");

		check("Series(x*4+x^2-y*x^10, {x, 0, 10})", "4*x+x^2-y*x^10+O(x)^11");
		check("Series(x*4+x^2-y*x^11, {x, 0, 10})", "4*x+x^2+O(x)^11");

		check("D(Csc(x),{x,2})", "Cot(x)^2*Csc(x)+Csc(x)^3");
		check("D(Tan(x)/Sin(x),x)", "Sec(x)*Tan(x)");
		check("D(Tan(x)/Sin(x),{x,2})", "Sec(x)^3+Sec(x)*Tan(x)^2");
		check("Series(Tan(x)/Sin(x),{x,a,2})",
				"Sec(a)+Sec(a)*Tan(a)*(-a+x)+1/2*(Sec(a)^3+Sec(a)*Tan(a)^2)*(-a+x)^2+O(-a+x)^3");
		check("Series(Tan(x)*Csc(x),{x,a,2})",
				"Sec(a)+Sec(a)*Tan(a)*(-a+x)+1/2*(Sec(a)^3+Sec(a)*Tan(a)^2)*(-a+x)^2+O(-a+x)^3");
		check("Series(f(x)+g(x),{x,a,2})", "f(a)+g(a)+(f'(a)+g'(a))*(-a+x)+1/2*(f''(a)+g''(a))*(-a+x)^2+O(-a+x)^3");

		check("Series(Sin(f(x)),{x,0,2})",
				"Sin(f(0))+Cos(f(0))*f'(0)*x+1/2*(-Sin(f(0))*f'(0)^2+Cos(f(0))*f''(0))*x^2+O(x)^3");
		check("Series(Sin(x),{x,0,2})", "x+O(x)^3");

		check("Series(f(x),{x,a,3})",
				"f(a)+f'(a)*(-a+x)+1/2*f''(a)*(-a+x)^2+1/6*Derivative(3)[f][a]*(-a+x)^3+O(-a+x)^4");
		check("Series(Exp(x),{x,0,2})", "1+x+x^2/2+O(x)^3");
		check("Series(Exp(f(x)),{x,0,2})", "E^f(0)+E^f(0)*f'(0)*x+1/2*(E^f(0)*f'(0)^2+E^f(0)*f''(0))*x^2+O(x)^3");
		check("Series(Exp(x),{x,0,5})", "1+x+x^2/2+x^3/6+x^4/24+x^5/120+O(x)^6");
		check("Series(100,{x,a,5})", "100");
		check("Series(y,{x,a,5})", "y");
	}

	public void testSeriesData() {
		check("SeriesData(x, 0, {1, 0, -1, -4, -17, -88, -549}, -1, 6, 1)", //
				"1/x-x-4*x^2-17*x^3-88*x^4-549*x^5+O(x)^6");
		check("s1=SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)", //
				"x-x^3/6+x^5/120+O(x)^11");
		check("s1=SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^2", //
				"x^2-x^4/3+2/45*x^6-x^8/360+x^10/14400+O(x)^12");
		check("s1 // FullForm", //
				"\"SeriesData(x,0,{1,0,-1/3,0,2/45,0,-1/360,0,1/14400},2,12,1)\"");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^3", //
				"x^3-x^5/2+13/120*x^7-7/540*x^9+13/14400*x^11+O(x)^13");

		// check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^12", //
		// "x^12-2*x^14+29/15*x^16-649/540*x^18+3883/7200*x^20+O(x)^22");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^2", //
				"x^2-x^4/3+2/45*x^6-x^8/360+x^10/14400+O(x)^12");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)^3", //
				"x^3-x^5/2+13/120*x^7-7/540*x^9+13/14400*x^11+O(x)^13");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)*SeriesData(x, 0,{1,0,-1/5,0,1/110}, 1, 11, 1) // FullForm", //
				"\"SeriesData(x,0,{1,0,-11/30,0,67/1320,0,-7/2200,0,1/13200},2,12,1)\"");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)*SeriesData(x, 0,{1,0,-1/5,0,1/110}, 1, 11, 1)", //
				"x^2-11/30*x^4+67/1320*x^6-7/2200*x^8+x^10/13200+O(x)^12");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)-SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)", //
				"O(x)^11");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)+SeriesData(x, 0,{1,0,-1/6,0,1/120}, 1, 11, 1)", //
				"2*x-x^3/3+x^5/60+O(x)^11");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)*7",
				"7*Sqrt(x)-7/6*x^(3/2)+7/120*x^(5/2)-x^(7/2)/720+x^(9/2)/51840+O(x)^(11/2)");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)+4", //
				"4+Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)");
		check("SeriesData(x, 0,{1,0,-1/6,0,1/120,0,-1/5040,0,1/362880}, 1, 11, 2)",
				"Sqrt(x)-x^(3/2)/6+x^(5/2)/120-x^(7/2)/5040+x^(9/2)/362880+O(x)^(11/2)");

		check("SeriesData(100, 0, Table(i^2, {i, 10}), 0, 10, 1)", //
				"Indeterminate");
		check("SeriesData(x, 0, Table(i^2, {i, 10}), 0, 10, 1)", //
				"1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9+O(x)^10");
	}

	public void testSeriesCoefficient() {
		check("SeriesCoefficient(d+4*x^e+7*x^f+Sin(x),{x, a, n})", //
				"Piecewise({{(4*a^e*Binomial(e,n)+7*a^f*Binomial(f,n))/a^n,n>0},{4*a^e+7*a^f+d,n==\n"
						+ "0}},0)+Piecewise({{Sin(a+1/2*n*Pi)/n!,n>=0}},0)");
		check("SeriesCoefficient(d+4*x^e+7*x^f+Sin(x),{x, a, 10})", //
				"(4*a^e*Binomial(e,10)+7*a^f*Binomial(f,10))/a^10-Sin(a)/3628800");
		check("SeriesCoefficient(d+4*x^e+7*x^f,{x, a, n})", //
				"Piecewise({{(4*a^e*Binomial(e,n)+7*a^f*Binomial(f,n))/a^n,n>0},{4*a^e+7*a^f+d,n==\n" + "0}},0)");
		check("SeriesCoefficient(x^42,{x, a, n})", //
				"Piecewise({{a^(42-n)*Binomial(42,n),n>0},{a^42,n==0}},0)");

		check("SeriesCoefficient(f(x),{x, a, n})", //
				"SeriesCoefficient(f(x),{x,a,n})");
		check("SeriesCoefficient(b^x,{x, a, n})", //
				"Piecewise({{(b^a*Log(b)^n)/n!,n>=0}},0)");

		check("SeriesCoefficient(Sin(b),{x,a,2})", //
				"0");
		check("SeriesCoefficient(f(42),{x,a,n})", //
				"Piecewise({{f(42),n==0}},0)");
		check("SeriesCoefficient(f(x),{x,a,2})", //
				"f''(a)/2");

		check("SeriesCoefficient(Cos(x),{x,0,n})", //
				"Piecewise({{((1+(-1)^n)*I^n)/(2*n!),n>=0}},0)");
		check("SeriesCoefficient(Cos(x),{x,Pi/2,n})", //
				"Piecewise({{((-1)*I*(-1+(-1)^n)*I^n)/(2*n!),n>=0}},0)");
		check("SeriesCoefficient(Cos(x),{x,f+g,n})", //
				"Piecewise({{Cos(f+g+1/2*n*Pi)/n!,n>=0}},0)");

		check("SeriesCoefficient(Sin(x),{x,0,n})", //
				"Piecewise({{(I*(-1+(-1)^n)*I^n)/(2*n!),n>=0}},0)");
		check("SeriesCoefficient(Sin(x),{x,Pi/2,n})", //
				"Piecewise({{((1+(-1)^n)*I^n)/(2*n!),n>=0}},0)");
		check("SeriesCoefficient(Sin(x),{x,f+g,n})", //
				"Piecewise({{Sin(f+g+1/2*n*Pi)/n!,n>=0}},0)");

		check("SeriesCoefficient(Tan(x),{x,0,n})", //
				"Piecewise({{((-1+(-1)^n)*2^n*(-1+2^(1+n))*I^(1+n)*BernoulliB(1+n))/(1+n)!,n>=1}},\n" + //
						"0)");
		check("SeriesCoefficient(Tan(x),{x,0,3})", //
				"1/3");
		check("SeriesCoefficient(Tan(x),{x,Pi/2,n})", //
				"Piecewise({{-1,n==-1},{((-1+(-1)^n)*2^n*I^(1+n)*BernoulliB(1+n))/(1+n)!,n>=0}},0)");

		check("SeriesCoefficient(Cot(x),{x,0,n})", //
				"Piecewise({{1,n==-1},{((-1)*I*(-1+(-1)^n)*(2*I)^n*BernoulliB(1+n))/(1+n)!,n>=0}},\n" + //
						"0)");
		check("SeriesCoefficient(Cot(x),{x,0,3})", //
				"-1/45");
		check("SeriesCoefficient(Cot(x),{x,Pi/2,n})", //
				"Piecewise({{((-1)*I*(-1+(-1)^n)*(-1+2^(1+n))*(2*I)^n*BernoulliB(1+n))/(1+n)!,n>=\n" + //
						"1}},0)");
		check("SeriesCoefficient(Cot(x),{x,Pi/2,3})", //
				"-1/3");
	}
}
