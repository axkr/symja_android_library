package org.matheclipse.core.rubi.step02;

public class IndependentTestSuites extends AbstractRubiTestCase {

public IndependentTestSuites(String name) { super(name, false); }

// {45}
public void test0001() {
	check(//
"Integrate[x*Sqrt[1 + 3*x], x]", //
 "(-2*(1 + 3*x)^(3/2))/27 + (2*(1 + 3*x)^(5/2))/45", //
45);}

// {45}
public void test0002() {
	check(//
"Integrate[x^2*Sqrt[1 + x], x]", //
 "(2*(1 + x)^(3/2))/3 - (4*(1 + x)^(5/2))/5 + (2*(1 + x)^(7/2))/7", //
45);}

// {45}
public void test0003() {
	check(//
"Integrate[x/Sqrt[2 - 3*x], x]", //
 "(-4*Sqrt[2 - 3*x])/9 + (2*(2 - 3*x)^(3/2))/27", //
45);}

// {2713}
public void test0004() {
	check(//
"Integrate[Sin[x]^3, x]", //
 "-Cos[x] + Cos[x]^3/3", //
2713);}

// {45}
public void test0005() {
	check(//
"Integrate[(-1 + z)^(1/3)*z, z]", //
 "(3*(-1 + z)^(4/3))/4 + (3*(-1 + z)^(7/3))/7", //
45);}

// {2686, 30}
public void test0006() {
	check(//
"Integrate[Cot[x]*Csc[x]^2, x]", //
 "-Csc[x]^2/2", //
2686, 30);}

// {2747, 32}
public void test0007() {
	check(//
"Integrate[Cos[2*x]*Sqrt[4 - Sin[2*x]], x]", //
 "-(4 - Sin[2*x])^(3/2)/3", //
2747, 32);}

// {2747, 32}
public void test0008() {
	check(//
"Integrate[Sin[x]/(3 + Cos[x])^2, x]", //
 "(3 + Cos[x])^(-1)", //
2747, 32);}

// {3460, 2718}
public void test0009() {
	check(//
"Integrate[x^(-1 + n)*Sin[x^n], x]", //
 "-(Cos[x^n]/n)", //
3460, 2718);}

// {45}
public void test0010() {
	check(//
"Integrate[t*(1 + t)^(1/4), t]", //
 "(-4*(1 + t)^(5/4))/5 + (4*(1 + t)^(9/4))/9", //
45);}

// {657, 643}
public void test0011() {
	check(//
"Integrate[(1 - 2*x + x^2)^(1/5)/(1 - x), x]", //
 "(-5*(1 - 2*x + x^2)^(1/5))/2", //
657, 643);}

// {3377, 2717}
public void test0012() {
	check(//
"Integrate[x*Sin[x], x]", //
 "-(x*Cos[x]) + Sin[x]", //
3377, 2717);}

// {2644, 30}
public void test0013() {
	check(//
"Integrate[Cos[x]*Sin[x], x]", //
 "Sin[x]^2/2", //
2644, 30);}

// {2715, 8}
public void test0014() {
	check(//
"Integrate[Sin[x]^2, x]", //
 "x/2 - (Cos[x]*Sin[x])/2", //
2715, 8);}

// {2713}
public void test0015() {
	check(//
"Integrate[Sin[x]^3, x]", //
 "-Cos[x] + Cos[x]^3/3", //
2713);}

// {2713}
public void test0016() {
	check(//
"Integrate[Sin[x]^5, x]", //
 "-Cos[x] + (2*Cos[x]^3)/3 - Cos[x]^5/5", //
2713);}

// {3391, 30}
public void test0017() {
	check(//
"Integrate[x*Sin[x]^2, x]", //
 "x^2/4 - (x*Cos[x]*Sin[x])/2 + Sin[x]^2/4", //
3391, 30);}

// {2715, 8}
public void test0018() {
	check(//
"Integrate[Cos[x]^2, x]", //
 "x/2 + (Cos[x]*Sin[x])/2", //
2715, 8);}

// {2713}
public void test0019() {
	check(//
"Integrate[Cos[x]^3, x]", //
 "Sin[x] - Sin[x]^3/3", //
2713);}

// {327, 224}
public void test0020() {
	check(//
"Integrate[t^3/Sqrt[4 + t^3], t]", //
 "(2*t*Sqrt[4 + t^3])/5 - (8*2^(2/3)*Sqrt[2 + Sqrt[3]]*(2^(2/3) + t)*Sqrt[(2*2^(1/3) - 2^(2/3)*t + t^2)/(2^(2/3)*(1 + Sqrt[3]) + t)^2]*EllipticF[ArcSin[(2^(2/3)*(1 - Sqrt[3]) + t)/(2^(2/3)*(1 + Sqrt[3]) + t)], -7 - 4*Sqrt[3]])/(5*3^(1/4)*Sqrt[(2^(2/3) + t)/(2^(2/3)*(1 + Sqrt[3]) + t)^2]*Sqrt[4 + t^3])", //
327, 224);}

// {3554, 8}
public void test0021() {
	check(//
"Integrate[Tan[x]^2, x]", //
 "-x + Tan[x]", //
3554, 8);}

// {3554, 8}
public void test0022() {
	check(//
"Integrate[Cot[x]^2, x]", //
 "-x - Cot[x]", //
3554, 8);}

// {3377, 2717}
public void test0023() {
	check(//
"Integrate[(2 + 3*x)*Sin[5*x], x]", //
 "-((2 + 3*x)*Cos[5*x])/5 + (3*Sin[5*x])/25", //
3377, 2717);}

// {45}
public void test0024() {
	check(//
"Integrate[(1 - x)^20*x^4, x]", //
 "-(1 - x)^21/21 + (2*(1 - x)^22)/11 - (6*(1 - x)^23)/23 + (1 - x)^24/6 - (1 - x)^25/25", //
45);}

// {3460, 2718}
public void test0025() {
	check(//
"Integrate[Sin[x^(-1)]/x^2, x]", //
 "Cos[x^(-1)]", //
3460, 2718);}

// {2333, 2332}
public void test0026() {
	check(//
"Integrate[Log[x]^2, x]", //
 "2*x - 2*x*Log[x] + x*Log[x]^2", //
2333, 2332);}

// {2342, 2341}
public void test0027() {
	check(//
"Integrate[x*Log[x]^2, x]", //
 "x^2/4 - (x^2*Log[x])/2 + (x^2*Log[x]^2)/2", //
2342, 2341);}

// {2342, 2341}
public void test0028() {
	check(//
"Integrate[x^2*Log[x]^2, x]", //
 "(2*x^3)/27 - (2*x^3*Log[x])/9 + (x^3*Log[x]^2)/3", //
2342, 2341);}

// {2339, 29}
public void test0029() {
	check(//
"Integrate[1/(x*Log[x]), x]", //
 "Log[Log[x]]", //
2339, 29);}

// {2437, 2338}
public void test0030() {
	check(//
"Integrate[Log[1 - t]/(1 - t), t]", //
 "-Log[1 - t]^2/2", //
2437, 2338);}

// {4419, 2225}
public void test0031() {
	check(//
"Integrate[E^(2*Sin[x])*Cos[x], x]", //
 "E^(2*Sin[x])/2", //
4419, 2225);}

// {2207, 2225}
public void test0032() {
	check(//
"Integrate[E^x*x, x]", //
 "-E^x + E^x*x", //
2207, 2225);}

// {2207, 2225}
public void test0033() {
	check(//
"Integrate[x/E^x, x]", //
 "-E^(-x) - x/E^x", //
2207, 2225);}

// {2243, 2240}
public void test0034() {
	check(//
"Integrate[x^3/E^x^2, x]", //
 "-1/(2*E^x^2) - x^2/(2*E^x^2)", //
2243, 2240);}

// {4931, 266}
public void test0035() {
	check(//
"Integrate[ArcCot[x], x]", //
 "x*ArcCot[x] + Log[1 + x^2]/2", //
4931, 266);}

// {223, 209}
public void test0036() {
	check(//
"Integrate[1/Sqrt[a^2 - x^2], x]", //
 "ArcTan[x/Sqrt[a^2 - x^2]]", //
223, 209);}

// {633, 222}
public void test0037() {
	check(//
"Integrate[1/Sqrt[1 - 2*x - x^2], x]", //
 "ArcSin[(1 + x)/Sqrt[2]]", //
633, 222);}

// {632, 210}
public void test0038() {
	check(//
"Integrate[(2 - x + x^2)^(-1), x]", //
 "(-2*ArcTan[(1 - 2*x)/Sqrt[7]])/Sqrt[7]", //
632, 210);}

// {201, 222}
public void test0039() {
	check(//
"Integrate[Sqrt[1 - x^2], x]", //
 "(x*Sqrt[1 - x^2])/2 + ArcSin[x]/2", //
201, 222);}

// {294, 209}
public void test0040() {
	check(//
"Integrate[x^2/(1 + x^2)^2, x]", //
 "-x/(2*(1 + x^2)) + ArcTan[x]/2", //
294, 209);}

// {2281, 209}
public void test0041() {
	check(//
"Integrate[E^x/(1 + E^(2*x)), x]", //
 "ArcTan[E^x]", //
2281, 209);}

// {907}
public void test0042() {
	check(//
"Integrate[(3 + 2*x + x^2)/((-1 + x)*(1 + x)^2), x]", //
 "(1 + x)^(-1) + (3*Log[1 - x])/2 - Log[1 + x]/2", //
907);}

// {3153, 212}
public void test0043() {
	check(//
"Integrate[(Cos[x] + Sin[x])^(-1), x]", //
 "-(ArcTanh[(Cos[x] - Sin[x])/Sqrt[2]]/Sqrt[2])", //
3153, 212);}

// {78}
public void test0044() {
	check(//
"Integrate[(3 + 2*x)/((-2 + x)*(5 + x)), x]", //
 "Log[2 - x] + Log[5 + x]", //
78);}

// {153}
public void test0045() {
	check(//
"Integrate[x/((1 + x)*(2 + x)*(3 + x)), x]", //
 "-Log[1 + x]/2 + 2*Log[2 + x] - (3*Log[3 + x])/2", //
153);}

// {2099}
public void test0046() {
	check(//
"Integrate[x/(2 - 3*x + x^3), x]", //
 "1/(3*(1 - x)) + (2*Log[1 - x])/9 - (2*Log[2 + x])/9", //
2099);}

// {1634}
public void test0047() {
	check(//
"Integrate[(7 + 8*x^3)/((1 + x)*(1 + 2*x)^3), x]", //
 "-3/(1 + 2*x)^2 + 3/(1 + 2*x) + Log[1 + x]", //
1634);}

// {645}
public void test0048() {
	check(//
"Integrate[(2 + x)/(x + x^2), x]", //
 "2*Log[x] - Log[1 + x]", //
645);}

// {90}
public void test0049() {
	check(//
"Integrate[1/((1 + x)*(2 + x)^2*(3 + x)^3), x]", //
 "(2 + x)^(-1) + 1/(4*(3 + x)^2) + 5/(4*(3 + x)) + Log[1 + x]/8 + 2*Log[2 + x] - (17*Log[3 + x])/8", //
90);}

// {45}
public void test0050() {
	check(//
"Integrate[x/(1 + x)^2, x]", //
 "(1 + x)^(-1) + Log[1 + x]", //
45);}

// {205, 213}
public void test0051() {
	check(//
"Integrate[(-1 + x^2)^(-2), x]", //
 "x/(2*(1 - x^2)) + ArcTanh[x]/2", //
205, 213);}

// {2738, 211}
public void test0052() {
	check(//
"Integrate[(1 + a*Cos[x])^(-1), x]", //
 "(2*ArcTan[(Sqrt[1 - a]*Tan[x/2])/Sqrt[1 + a]])/Sqrt[1 - a^2]", //
2738, 211);}

// {2738, 212}
public void test0053() {
	check(//
"Integrate[(1 + 2*Cos[x])^(-1), x]", //
 "-(Log[Sqrt[3]*Cos[x/2] - Sin[x/2]]/Sqrt[3]) + Log[Sqrt[3]*Cos[x/2] + Sin[x/2]]/Sqrt[3]", //
2738, 212);}

// {211}
public void test0054() {
	check(//
"Integrate[(b^2*Cos[x]^2 + a^2*Sin[x]^2)^(-1), x]", //
 "ArcTan[(a*Tan[x])/b]/(a*b)", //
211);}

// {201, 222}
public void test0055() {
	check(//
"Integrate[Sqrt[3 - x^2], x]", //
 "(x*Sqrt[3 - x^2])/2 + (3*ArcSin[x/Sqrt[3]])/2", //
201, 222);}

// {201, 221}
public void test0056() {
	check(//
"Integrate[Sqrt[5 + x^2], x]", //
 "(x*Sqrt[5 + x^2])/2 + (5*ArcSinh[x/Sqrt[5]])/2", //
201, 221);}

// {634, 212}
public void test0057() {
	check(//
"Integrate[1/Sqrt[x + x^2], x]", //
 "2*ArcTanh[x/Sqrt[x + x^2]]", //
634, 212);}

// {2354, 2438}
public void test0058() {
	check(//
"Integrate[Log[t]/(1 + t), t]", //
 "Log[t]*Log[1 + t] + PolyLog[2, -t]", //
2354, 2438);}

// {2208, 2209}
public void test0059() {
	check(//
"Integrate[E^t/t^2, t]", //
 "-(E^t/t) + ExpIntegralEi[t]", //
2208, 2209);}

// {2237, 2241}
public void test0060() {
	check(//
"Integrate[E^t^(-1), t]", //
 "E^t^(-1)*t - ExpIntegralEi[t^(-1)]", //
2237, 2241);}

// {6847, 2209}
public void test0061() {
	check(//
"Integrate[(E^t^2*t)/(1 + t^2), t]", //
 "ExpIntegralEi[1 + t^2]/(2*E)", //
6847, 2209);}

// {2208, 2209}
public void test0062() {
	check(//
"Integrate[E^t/(1 + t)^2, t]", //
 "-(E^t/(1 + t)) + ExpIntegralEi[1 + t]/E", //
2208, 2209);}

// {2634, 2209}
public void test0063() {
	check(//
"Integrate[E^t*Log[1 + t], t]", //
 "-(ExpIntegralEi[1 + t]/E) + E^t*Log[1 + t]", //
2634, 2209);}

// {2207, 2225}
public void test0064() {
	check(//
"Integrate[t/E^t, t]", //
 "-E^(-t) - t/E^t", //
2207, 2225);}

// {2334, 2335}
public void test0065() {
	check(//
"Integrate[Log[t]^(-2), t]", //
 "-(t/Log[t]) + LogIntegral[t]", //
2334, 2335);}

// {2336, 2212}
public void test0066() {
	check(//
"Integrate[Log[t]^(-1 - n), t]", //
 "-((Gamma[-n, -Log[t]]*(-Log[t])^n)/Log[t]^n)", //
2336, 2212);}

// {2729, 2727}
public void test0067() {
	check(//
"Integrate[(1 + Cos[x])^(-2), x]", //
 "Sin[x]/(3*(1 + Cos[x])^2) + Sin[x]/(3*(1 + Cos[x]))", //
2729, 2727);}

// {2139}
public void test0068() {
	check(//
"Integrate[Sqrt[1 + Sqrt[x] + Sqrt[1 + 2*Sqrt[x] + 2*x]], x]", //
 "(2*Sqrt[1 + Sqrt[x] + Sqrt[1 + 2*Sqrt[x] + 2*x]]*(2 + Sqrt[x] + 6*x^(3/2) - (2 - Sqrt[x])*Sqrt[1 + 2*Sqrt[x] + 2*x]))/(15*Sqrt[x])", //
2139);}

// {3561, 212}
public void test0069() {
	check(//
"Integrate[Sqrt[1 + Tanh[4*x]], x]", //
 "ArcTanh[Sqrt[1 + Tanh[4*x]]/Sqrt[2]]/(2*Sqrt[2])", //
3561, 212);}

// {6818}
public void test0070() {
	check(//
"Integrate[x^(-1) + (1 + x^(-1))/(x + Log[x])^(3/2), x]", //
 "Log[x] - 2/Sqrt[x + Log[x]]", //
6818);}

// {4767, 8}
public void test0071() {
	check(//
"Integrate[(x*ArcSin[x])/Sqrt[1 - x^2], x]", //
 "x - Sqrt[1 - x^2]*ArcSin[x]", //
4767, 8);}

// {2634, 8}
public void test0072() {
	check(//
"Integrate[(x*Log[x + Sqrt[-1 + x^2]])/Sqrt[-1 + x^2], x]", //
 "-x + Sqrt[-1 + x^2]*Log[x + Sqrt[-1 + x^2]]", //
2634, 8);}

// {2634, 8}
public void test0073() {
	check(//
"Integrate[(x*Log[x + Sqrt[1 + x^2]])/Sqrt[1 + x^2], x]", //
 "-x + Sqrt[1 + x^2]*Log[x + Sqrt[1 + x^2]]", //
2634, 8);}

// {5050, 221}
public void test0074() {
	check(//
"Integrate[(x*ArcTan[x])/Sqrt[1 + x^2], x]", //
 "-ArcSinh[x] + Sqrt[1 + x^2]*ArcTan[x]", //
5050, 221);}

// {4771, 29}
public void test0075() {
	check(//
"Integrate[ArcSin[x]/(x^2*Sqrt[1 - x^2]), x]", //
 "-((Sqrt[1 - x^2]*ArcSin[x])/x) + Log[x]", //
4771, 29);}

// {5344, 29}
public void test0076() {
	check(//
"Integrate[(x*ArcSec[x])/Sqrt[-1 + x^2], x]", //
 "Sqrt[-1 + x^2]*ArcSec[x] - (x*Log[x])/Sqrt[x^2]", //
5344, 29);}

// {3265, 212}
public void test0077() {
	check(//
"Integrate[Sin[x]/(1 + Sin[x]^2), x]", //
 "-(ArcTanh[Cos[x]/Sqrt[2]]/Sqrt[2])", //
3265, 212);}

// {1713, 212}
public void test0078() {
	check(//
"Integrate[(1 + x^2)/((1 - x^2)*Sqrt[1 + x^4]), x]", //
 "ArcTanh[(Sqrt[2]*x)/Sqrt[1 + x^4]]/Sqrt[2]", //
1713, 212);}

// {1713, 209}
public void test0079() {
	check(//
"Integrate[(1 - x^2)/((1 + x^2)*Sqrt[1 + x^4]), x]", //
 "ArcTan[(Sqrt[2]*x)/Sqrt[1 + x^4]]/Sqrt[2]", //
1713, 209);}

// {14}
public void test0080() {
	check(//
"Integrate[x*(1 + 2*x + x^2), x]", //
 "x^2/2 + (2*x^3)/3 + x^4/4", //
14);}

// {45}
public void test0081() {
	check(//
"Integrate[(1 + x)^3/(-1 + x)^4, x]", //
 "8/(3*(1 - x)^3) - 6/(1 - x)^2 + 6/(1 - x) + Log[1 - x]", //
45);}

// {84}
public void test0082() {
	check(//
"Integrate[1/((-1 + x)*x*(1 + x)^2), x]", //
 "-1/(2*(1 + x)) + Log[1 - x]/4 - Log[x] + (3*Log[1 + x])/4", //
84);}

// {78}
public void test0083() {
	check(//
"Integrate[(b + a*x)/((-p + x)*(-q + x)), x]", //
 "((b + a*p)*Log[p - x])/(p - q) - ((b + a*q)*Log[q - x])/(p - q)", //
78);}

// {632, 212}
public void test0084() {
	check(//
"Integrate[(c + b*x + a*x^2)^(-1), x]", //
 "(-2*ArcTanh[(b + 2*a*x)/Sqrt[b^2 - 4*a*c]])/Sqrt[b^2 - 4*a*c]", //
632, 212);}

// {632, 210}
public void test0085() {
	check(//
"Integrate[(3 - 2*x + x^2)^(-1), x]", //
 "-(ArcTan[(1 - x)/Sqrt[2]]/Sqrt[2])", //
632, 210);}

// {153}
public void test0086() {
	check(//
"Integrate[x/((-a + x)*(-b + x)*(-c + x)), x]", //
 "(a*Log[a - x])/((a - b)*(a - c)) - (b*Log[b - x])/((a - b)*(b - c)) + (c*Log[c - x])/((a - c)*(b - c))", //
153);}

// {45}
public void test0087() {
	check(//
"Integrate[x*(a + b*x)^p, x]", //
 "-((a*(a + b*x)^(1 + p))/(b^2*(1 + p))) + (a + b*x)^(2 + p)/(b^2*(2 + p))", //
45);}

// {45}
public void test0088() {
	check(//
"Integrate[x^2*(a + b*x)^p, x]", //
 "(a^2*(a + b*x)^(1 + p))/(b^3*(1 + p)) - (2*a*(a + b*x)^(2 + p))/(b^3*(2 + p)) + (a + b*x)^(3 + p)/(b^3*(3 + p))", //
45);}

// {45}
public void test0089() {
	check(//
"Integrate[x/(a + b*x), x]", //
 "x/b - (a*Log[a + b*x])/b^2", //
45);}

// {45}
public void test0090() {
	check(//
"Integrate[x^2/(a + b*x), x]", //
 "-((a*x)/b^2) + x^2/(2*b) + (a^2*Log[a + b*x])/b^3", //
45);}

// {46}
public void test0091() {
	check(//
"Integrate[1/(x^2*(a + b*x)), x]", //
 "-(1/(a*x)) - (b*Log[x])/a^2 + (b*Log[a + b*x])/a^2", //
46);}

// {46}
public void test0092() {
	check(//
"Integrate[1/(x^2*(a + b*x)^2), x]", //
 "-(1/(a^2*x)) - b/(a^2*(a + b*x)) - (2*b*Log[x])/a^3 + (2*b*Log[a + b*x])/a^3", //
46);}

// {2333, 2332}
public void test0093() {
	check(//
"Integrate[Log[x]^2, x]", //
 "2*x - 2*x*Log[x] + x*Log[x]^2", //
2333, 2332);}

// {2339, 30}
public void test0094() {
	check(//
"Integrate[Log[x]^2/x, x]", //
 "Log[x]^3/3", //
2339, 30);}

// {2436, 2335}
public void test0095() {
	check(//
"Integrate[Log[1 + x]^(-1), x]", //
 "LogIntegral[1 + x]", //
2436, 2335);}

// {2339, 29}
public void test0096() {
	check(//
"Integrate[1/(x*Log[x]), x]", //
 "Log[Log[x]]", //
2339, 29);}

// {2339, 30}
public void test0097() {
	check(//
"Integrate[Log[x]^p/x, x]", //
 "Log[x]^(1 + p)/(1 + p)", //
2339, 30);}

// {2350}
public void test0098() {
	check(//
"Integrate[(b + a*x)*Log[x], x]", //
 "-(b*x) - (a*x^2)/4 + b*x*Log[x] + (a*x^2*Log[x])/2", //
2350);}

// {2351, 31}
public void test0099() {
	check(//
"Integrate[Log[x]/(b + a*x)^2, x]", //
 "(x*Log[x])/(b*(b + a*x)) - Log[b + a*x]/(a*b)", //
2351, 31);}

// {3564, 3611}
public void test0100() {
	check(//
"Integrate[(1 + Tan[x])^(-2), x]", //
 "Log[Cos[x] + Sin[x]]/2 - 1/(2*(1 + Tan[x]))", //
3564, 3611);}

// {2715, 8}
public void test0101() {
	check(//
"Integrate[Sin[x]^2, x]", //
 "x/2 - (Cos[x]*Sin[x])/2", //
2715, 8);}

// {2713}
public void test0102() {
	check(//
"Integrate[Sin[x]^3, x]", //
 "-Cos[x] + Cos[x]^3/3", //
2713);}

// {2715, 8}
public void test0103() {
	check(//
"Integrate[Cos[x]^2, x]", //
 "x/2 + (Cos[x]*Sin[x])/2", //
2715, 8);}

// {2713}
public void test0104() {
	check(//
"Integrate[Cos[x]^3, x]", //
 "Sin[x] - Sin[x]^3/3", //
2713);}

// {3852, 8}
public void test0105() {
	check(//
"Integrate[Sec[x]^2, x]", //
 "Tan[x]", //
3852, 8);}

// {3377, 2717}
public void test0106() {
	check(//
"Integrate[x*Sin[x], x]", //
 "-(x*Cos[x]) + Sin[x]", //
3377, 2717);}

// {3391, 30}
public void test0107() {
	check(//
"Integrate[x*Sin[x]^2, x]", //
 "x^2/4 - (x*Cos[x]*Sin[x])/2 + Sin[x]^2/4", //
3391, 30);}

// {3377, 2718}
public void test0108() {
	check(//
"Integrate[x*Cos[x], x]", //
 "Cos[x] + x*Sin[x]", //
3377, 2718);}

// {3391, 30}
public void test0109() {
	check(//
"Integrate[x*Cos[x]^2, x]", //
 "x^2/4 + Cos[x]^2/4 + (x*Cos[x]*Sin[x])/2", //
3391, 30);}

// {3378, 3383}
public void test0110() {
	check(//
"Integrate[Sin[x]/x^2, x]", //
 "CosIntegral[x] - Sin[x]/x", //
3378, 3383);}

// {3554, 3556}
public void test0111() {
	check(//
"Integrate[Tan[x]^3, x]", //
 "Log[Cos[x]] + Tan[x]^2/2", //
3554, 3556);}

// {2715, 8}
public void test0112() {
	check(//
"Integrate[Sin[a + b*x]^2, x]", //
 "x/2 - (Cos[a + b*x]*Sin[a + b*x])/(2*b)", //
2715, 8);}

// {2713}
public void test0113() {
	check(//
"Integrate[Sin[a + b*x]^3, x]", //
 "-(Cos[a + b*x]/b) + Cos[a + b*x]^3/(3*b)", //
2713);}

// {2715, 8}
public void test0114() {
	check(//
"Integrate[Cos[a + b*x]^2, x]", //
 "x/2 + (Cos[a + b*x]*Sin[a + b*x])/(2*b)", //
2715, 8);}

// {2713}
public void test0115() {
	check(//
"Integrate[Cos[a + b*x]^3, x]", //
 "Sin[a + b*x]/b - Sin[a + b*x]^3/(3*b)", //
2713);}

// {3852, 8}
public void test0116() {
	check(//
"Integrate[Sec[a + b*x]^2, x]", //
 "Tan[a + b*x]/b", //
3852, 8);}

// {3554, 3556}
public void test0117() {
	check(//
"Integrate[Cot[x]^3, x]", //
 "-Cot[x]^2/2 - Log[Sin[x]]", //
3554, 3556);}

// {2259, 2225}
public void test0118() {
	check(//
"Integrate[E^(2*x + a*x), x]", //
 "E^((2 + a)*x)/(2 + a)", //
2259, 2225);}

// {2320, 211}
public void test0119() {
	check(//
"Integrate[(b/E^(m*x) + a*E^(m*x))^(-1), x]", //
 "ArcTan[(Sqrt[a]*E^(m*x))/Sqrt[b]]/(Sqrt[a]*Sqrt[b]*m)", //
2320, 211);}

// {2207, 2225}
public void test0120() {
	check(//
"Integrate[E^(a*x)*x, x]", //
 "-(E^(a*x)/a^2) + (E^(a*x)*x)/a", //
2207, 2225);}

// {2325, 2225}
public void test0121() {
	check(//
"Integrate[a^x/b^x, x]", //
 "a^x/(b^x*(Log[a] - Log[b]))", //
2325, 2225);}

// {2325, 2225}
public void test0122() {
	check(//
"Integrate[a^x*b^x, x]", //
 "(a^x*b^x)/(Log[a] + Log[b])", //
2325, 2225);}

// {2208, 2209}
public void test0123() {
	check(//
"Integrate[a^x/x^2, x]", //
 "-(a^x/x) + ExpIntegralEi[x*Log[a]]*Log[a]", //
2208, 2209);}

// {2634, 2209}
public void test0124() {
	check(//
"Integrate[E^x*Log[x], x]", //
 "-ExpIntegralEi[x] + E^x*Log[x]", //
2634, 2209);}

// {45}
public void test0125() {
	check(//
"Integrate[x*Sqrt[a + b*x], x]", //
 "(-2*a*(a + b*x)^(3/2))/(3*b^2) + (2*(a + b*x)^(5/2))/(5*b^2)", //
45);}

// {45}
public void test0126() {
	check(//
"Integrate[x^2*Sqrt[a + b*x], x]", //
 "(2*a^2*(a + b*x)^(3/2))/(3*b^3) - (4*a*(a + b*x)^(5/2))/(5*b^3) + (2*(a + b*x)^(7/2))/(7*b^3)", //
45);}

// {45}
public void test0127() {
	check(//
"Integrate[x/Sqrt[a + b*x], x]", //
 "(-2*a*Sqrt[a + b*x])/b^2 + (2*(a + b*x)^(3/2))/(3*b^2)", //
45);}

// {45}
public void test0128() {
	check(//
"Integrate[x^2/Sqrt[a + b*x], x]", //
 "(2*a^2*Sqrt[a + b*x])/b^3 - (4*a*(a + b*x)^(3/2))/(3*b^3) + (2*(a + b*x)^(5/2))/(5*b^3)", //
45);}

// {65, 214}
public void test0129() {
	check(//
"Integrate[1/(x*Sqrt[a + b*x]), x]", //
 "(-2*ArcTanh[Sqrt[a + b*x]/Sqrt[a]])/Sqrt[a]", //
65, 214);}

// {45}
public void test0130() {
	check(//
"Integrate[x*(a + b*x)^(p/2), x]", //
 "(-2*a*(a + b*x)^((2 + p)/2))/(b^2*(2 + p)) + (2*(a + b*x)^((4 + p)/2))/(b^2*(4 + p))", //
45);}

// {223, 212}
public void test0131() {
	check(//
"Integrate[1/Sqrt[-1 + x^2], x]", //
 "ArcTanh[x/Sqrt[-1 + x^2]]", //
223, 212);}

// {281, 222}
public void test0132() {
	check(//
"Integrate[x/Sqrt[1 - x^4], x]", //
 "ArcSin[x^2]/2", //
281, 222);}

// {223, 212}
public void test0133() {
	check(//
"Integrate[1/Sqrt[-alpha^2 + 2*h*r^2], r]", //
 "ArcTanh[(Sqrt[2]*Sqrt[h]*r)/Sqrt[-alpha^2 + 2*h*r^2]]/(Sqrt[2]*Sqrt[h])", //
223, 212);}

// {738, 210}
public void test0134() {
	check(//
"Integrate[1/(r*Sqrt[-alpha^2 - 2*k*r + 2*h*r^2]), r]", //
 "-(ArcTan[(alpha^2 + k*r)/(alpha*Sqrt[-alpha^2 - 2*k*r + 2*h*r^2])]/alpha)", //
738, 210);}

// {738, 210}
public void test0135() {
	check(//
"Integrate[1/(r*Sqrt[-alpha^2 - epsilon^2 - 2*k*r + 2*h*r^2]), r]", //
 "-(ArcTan[(alpha^2 + epsilon^2 + k*r)/(Sqrt[alpha^2 + epsilon^2]*Sqrt[-alpha^2 - epsilon^2 - 2*k*r + 2*h*r^2])]/Sqrt[alpha^2 + epsilon^2])", //
738, 210);}

// {3377, 2717}
public void test0136() {
	check(//
"Integrate[x*Sin[a + x], x]", //
 "-(x*Cos[a + x]) + Sin[a + x]", //
3377, 2717);}

// {47, 37}
public void test0137() {
	check(//
"Integrate[Sqrt[x]/(1 + x)^(7/2), x]", //
 "(2*x^(3/2))/(5*(1 + x)^(5/2)) + (4*x^(3/2))/(15*(1 + x)^(3/2))", //
47, 37);}

// {65, 213}
public void test0138() {
	check(//
"Integrate[1/(Sqrt[x]*(-1 + 2*x)), x]", //
 "-(Sqrt[2]*ArcTanh[Sqrt[2]*Sqrt[x]])", //
65, 213);}

// {14}
public void test0139() {
	check(//
"Integrate[Sqrt[x]*(1 + x^2), x]", //
 "(2*x^(3/2))/3 + (2*x^(7/2))/7", //
14);}

// {3377, 2717}
public void test0140() {
	check(//
"Integrate[x*Sinh[x], x]", //
 "x*Cosh[x] - Sinh[x]", //
3377, 2717);}

// {3377, 2718}
public void test0141() {
	check(//
"Integrate[x*Cosh[x], x]", //
 "-Cosh[x] + x*Sinh[x]", //
3377, 2718);}

// {281, 221}
public void test0142() {
	check(//
"Integrate[x/Sqrt[36 + x^4], x]", //
 "ArcSinh[x^2/6]/2", //
281, 221);}

// {45}
public void test0143() {
	check(//
"Integrate[(1 + 2*x)/(2 + 3*x), x]", //
 "(2*x)/3 - Log[2 + 3*x]/9", //
45);}

// {281, 213}
public void test0144() {
	check(//
"Integrate[x/(-1 + x^4), x]", //
 "-ArcTanh[x^2]/2", //
281, 213);}

// {201, 221}
public void test0145() {
	check(//
"Integrate[Sqrt[3 + x^2], x]", //
 "(x*Sqrt[3 + x^2])/2 + (3*ArcSinh[x/Sqrt[3]])/2", //
201, 221);}

// {45}
public void test0146() {
	check(//
"Integrate[x/(1 + x)^2, x]", //
 "(1 + x)^(-1) + Log[1 + x]", //
45);}

// {4715, 267}
public void test0147() {
	check(//
"Integrate[ArcSin[x], x]", //
 "Sqrt[1 - x^2] + x*ArcSin[x]", //
4715, 267);}

// {2715, 8}
public void test0148() {
	check(//
"Integrate[Cos[x]^2, x]", //
 "x/2 + (Cos[x]*Sin[x])/2", //
2715, 8);}

// {907}
public void test0149() {
	check(//
"Integrate[(-2 - 3*x + 5*x^2)/((-2 + x)*x^2), x]", //
 "-x^(-1) + 3*Log[2 - x] + 2*Log[x]", //
907);}

// {632, 210}
public void test0150() {
	check(//
"Integrate[(10 - 12*x + 9*x^2)^(-1), x]", //
 "-ArcTan[(2 - 3*x)/Sqrt[6]]/(3*Sqrt[6])", //
632, 210);}

// {1626}
public void test0151() {
	check(//
"Integrate[(d + c*x + b*x^2 + a*x^3)/((-3 + x)*x*(1 + x)), x]", //
 "a*x + ((27*a + 9*b + 3*c + d)*Log[3 - x])/12 - (d*Log[x])/3 - ((a - b + c - d)*Log[1 + x])/4", //
1626);}

// {4372, 2718}
public void test0152() {
	check(//
"Integrate[Sec[x]*Sin[2*x], x]", //
 "-2*Cos[x]", //
4372, 2718);}

// {6839, 2209}
public void test0153() {
	check(//
"Integrate[(E^(E^x + x)*(1 + E^x))/(E^x + x), x]", //
 "ExpIntegralEi[E^x + x]", //
6839, 2209);}

// {12, 2736}
public void test0154() {
	check(//
"Integrate[3/(5 - 4*Cos[x]), x]", //
 "x + 2*ArcTan[Sin[x]/(2 - Cos[x])]", //
12, 2736);}

// {12, 2736}
public void test0155() {
	check(//
"Integrate[3/(5 + 4*Sin[x]), x]", //
 "x + 2*ArcTan[Cos[x]/(2 + Sin[x])]", //
12, 2736);}

// {14}
public void test0156() {
	check(//
"Integrate[(x + x^2)/Sqrt[x], x]", //
 "(2*x^(3/2))/3 + (2*x^(5/2))/5", //
14);}

// {2686, 30}
public void test0157() {
	check(//
"Integrate[Sec[x]^2*Tan[x], x]", //
 "Sec[x]^2/2", //
2686, 30);}

// {2686, 30}
public void test0158() {
	check(//
"Integrate[Cot[x]*Csc[x]^3, x]", //
 "-Csc[x]^3/3", //
2686, 30);}

// {2320, 3380}
public void test0159() {
	check(//
"Integrate[Sin[E^x], x]", //
 "SinIntegral[E^x]", //
2320, 3380);}

// {2644, 30}
public void test0160() {
	check(//
"Integrate[Cos[x]*Sin[x], x]", //
 "Sin[x]^2/2", //
2644, 30);}

// {2278, 31}
public void test0161() {
	check(//
"Integrate[E^x/(1 + E^x), x]", //
 "Log[1 + E^x]", //
2278, 31);}

// {12, 2225}
public void test0162() {
	check(//
"Integrate[2*E^(2*x)*y*z, x]", //
 "E^(2*x)*y*z", //
12, 2225);}

// {45}
public void test0163() {
	check(//
"Integrate[x*Sqrt[1 + x], x]", //
 "(-2*(1 + x)^(3/2))/3 + (2*(1 + x)^(5/2))/5", //
45);}

// {2281, 209}
public void test0164() {
	check(//
"Integrate[E^x/(2 + 3*E^(2*x)), x]", //
 "ArcTan[Sqrt[3/2]*E^x]/Sqrt[6]", //
2281, 209);}

// {2281, 211}
public void test0165() {
	check(//
"Integrate[E^(2*x)/(A + B*E^(4*x)), x]", //
 "ArcTan[(Sqrt[B]*E^(2*x))/Sqrt[A]]/(2*Sqrt[A]*Sqrt[B])", //
2281, 211);}

// {45}
public void test0166() {
	check(//
"Integrate[x*Sqrt[1 + x], x]", //
 "(-2*(1 + x)^(3/2))/3 + (2*(1 + x)^(5/2))/5", //
45);}

// {2715, 8}
public void test0167() {
	check(//
"Integrate[Sin[x]^2, x]", //
 "x/2 - (Cos[x]*Sin[x])/2", //
2715, 8);}

// {2207, 2225}
public void test0168() {
	check(//
"Integrate[E^x*x, x]", //
 "-E^x + E^x*x", //
2207, 2225);}

// {27, 32}
public void test0169() {
	check(//
"Integrate[(1 + 2*x + x^2)^(-1), x]", //
 "-(1 + x)^(-1)", //
27, 32);}

// {209}
public void test0170() {
	check(//
"Integrate[1/(x*(1 + Log[x]^2)), x]", //
 "ArcTan[Log[x]]", //
209);}

// {3377, 2718}
public void test0171() {
	check(//
"Integrate[x*Cos[x], x]", //
 "Cos[x] + x*Sin[x]", //
3377, 2718);}

// {3377, 2718}
public void test0172() {
	check(//
"Integrate[x*Cos[x], x]", //
 "Cos[x] + x*Sin[x]", //
3377, 2718);}

// {2342, 2341}
public void test0173() {
	check(//
"Integrate[x*Log[x]^2, x]", //
 "x^2/4 - (x^2*Log[x])/2 + (x^2*Log[x]^2)/2", //
2342, 2341);}

// {3302}
public void test0174() {
	check(//
"Integrate[Cos[x]*(1 + Sin[x]^3), x]", //
 "Sin[x] + Sin[x]^4/4", //
3302);}

// {209}
public void test0175() {
	check(//
"Integrate[1/(x*(1 + Log[x]^2)), x]", //
 "ArcTan[Log[x]]", //
209);}

// {6828, 209}
public void test0176() {
	check(//
"Integrate[1/(Sqrt[1 - x^2]*(1 + ArcSin[x]^2)), x]", //
 "ArcTan[ArcSin[x]]", //
6828, 209);}

// {3176, 3212}
public void test0177() {
	check(//
"Integrate[Sin[x]/(Cos[x] + Sin[x]), x]", //
 "x/2 - Log[Cos[x] + Sin[x]]/2", //
3176, 3212);}

// {281, 209}
public void test0178() {
	check(//
"Integrate[x/(1 + x^4), x]", //
 "ArcTan[x^2]/2", //
281, 209);}

// {294, 222}
public void test0179() {
	check(//
"Integrate[-(x^2/(1 - x^2)^(3/2)), x]", //
 "-(x/Sqrt[1 - x^2]) + ArcSin[x]", //
294, 222);}

// {2715, 8}
public void test0180() {
	check(//
"Integrate[Cos[x]^2, x]", //
 "x/2 + (Cos[x]*Sin[x])/2", //
2715, 8);}

// {14}
public void test0181() {
	check(//
"Integrate[(1 + x^2)/Sqrt[x], x]", //
 "2*Sqrt[x] + (2*x^(5/2))/5", //
14);}

// {2644, 30}
public void test0182() {
	check(//
"Integrate[Cos[x]*Sin[x]^2, x]", //
 "Sin[x]^3/3", //
2644, 30);}

// {2278, 31}
public void test0183() {
	check(//
"Integrate[E^x/(1 + E^x), x]", //
 "Log[1 + E^x]", //
2278, 31);}

// {2686, 30}
public void test0184() {
	check(//
"Integrate[Sec[x]^2*Tan[x], x]", //
 "Sec[x]^2/2", //
2686, 30);}

// {2644, 30}
public void test0185() {
	check(//
"Integrate[Cos[x]*Sin[x], x]", //
 "Sin[x]^2/2", //
2644, 30);}

// {3852, 8}
public void test0186() {
	check(//
"Integrate[Sec[x]^2, x]", //
 "Tan[x]", //
3852, 8);}

// {3852, 8}
public void test0187() {
	check(//
"Integrate[Csc[x]^2, x]", //
 "-Cot[x]", //
3852, 8);}

// {2686, 8}
public void test0188() {
	check(//
"Integrate[Sec[x]*Tan[x], x]", //
 "Sec[x]", //
2686, 8);}

// {2686, 8}
public void test0189() {
	check(//
"Integrate[Cot[x]*Csc[x], x]", //
 "-Csc[x]", //
2686, 8);}

// {3377, 2717}
public void test0190() {
	check(//
"Integrate[x*Sin[x], x]", //
 "-(x*Cos[x]) + Sin[x]", //
3377, 2717);}

// {4930, 266}
public void test0191() {
	check(//
"Integrate[ArcTan[x], x]", //
 "x*ArcTan[x] - Log[1 + x^2]/2", //
4930, 266);}

// {2207, 2225}
public void test0192() {
	check(//
"Integrate[E^(2*x)*x, x]", //
 "-E^(2*x)/4 + (E^(2*x)*x)/2", //
2207, 2225);}

// {3377, 2718}
public void test0193() {
	check(//
"Integrate[x*Cos[x], x]", //
 "Cos[x] + x*Sin[x]", //
3377, 2718);}

// {3377, 2717}
public void test0194() {
	check(//
"Integrate[x*Sin[4*x], x]", //
 "-(x*Cos[4*x])/4 + Sin[4*x]/16", //
3377, 2717);}

// {2333, 2332}
public void test0195() {
	check(//
"Integrate[Log[x]^2, x]", //
 "2*x - 2*x*Log[x] + x*Log[x]^2", //
2333, 2332);}

// {4715, 267}
public void test0196() {
	check(//
"Integrate[ArcSin[x], x]", //
 "Sqrt[1 - x^2] + x*ArcSin[x]", //
4715, 267);}

// {4269, 3556}
public void test0197() {
	check(//
"Integrate[t*Sec[t]^2, t]", //
 "Log[Cos[t]] + t*Tan[t]", //
4269, 3556);}

// {3377, 2717}
public void test0198() {
	check(//
"Integrate[y*Sinh[y], y]", //
 "y*Cosh[y] - Sinh[y]", //
3377, 2717);}

// {3377, 2718}
public void test0199() {
	check(//
"Integrate[y*Cosh[a*y], y]", //
 "-(Cosh[a*y]/a^2) + (y*Sinh[a*y])/a", //
3377, 2718);}

// {2207, 2225}
public void test0200() {
	check(//
"Integrate[t/E^t, t]", //
 "-E^(-t) - t/E^t", //
2207, 2225);}

// {3377, 2718}
public void test0201() {
	check(//
"Integrate[x*Cos[2*x], x]", //
 "Cos[2*x]/4 + (x*Sin[2*x])/2", //
3377, 2718);}

// {4716, 267}
public void test0202() {
	check(//
"Integrate[ArcCos[x], x]", //
 "-Sqrt[1 - x^2] + x*ArcCos[x]", //
4716, 267);}

// {4269, 3556}
public void test0203() {
	check(//
"Integrate[x*Csc[x]^2, x]", //
 "-(x*Cot[x]) + Log[Sin[x]]", //
4269, 3556);}

// {2634, 2717}
public void test0204() {
	check(//
"Integrate[Cos[x]*Log[Sin[x]], x]", //
 "-Sin[x] + Log[Sin[x]]*Sin[x]", //
2634, 2717);}

// {2243, 2240}
public void test0205() {
	check(//
"Integrate[E^x^2*x^3, x]", //
 "-E^x^2/2 + (E^x^2*x^2)/2", //
2243, 2240);}

// {2207, 2225}
public void test0206() {
	check(//
"Integrate[E^x*(3 + 2*x), x]", //
 "-2*E^x + E^x*(3 + 2*x)", //
2207, 2225);}

// {2207, 2225}
public void test0207() {
	check(//
"Integrate[5^x*x, x]", //
 "-(5^x/Log[5]^2) + (5^x*x)/Log[5]", //
2207, 2225);}

// {3377, 2718}
public void test0208() {
	check(//
"Integrate[x*Cos[Pi*x], x]", //
 "Cos[Pi*x]/Pi^2 + (x*Sin[Pi*x])/Pi", //
3377, 2718);}

// {2715, 8}
public void test0209() {
	check(//
"Integrate[Sin[3*x]^2, x]", //
 "x/2 - (Cos[3*x]*Sin[3*x])/6", //
2715, 8);}

// {2715, 8}
public void test0210() {
	check(//
"Integrate[Cos[x]^2, x]", //
 "x/2 + (Cos[x]*Sin[x])/2", //
2715, 8);}

// {2713}
public void test0211() {
	check(//
"Integrate[Sin[x]^3, x]", //
 "-Cos[x] + Cos[x]^3/3", //
2713);}

// {2713}
public void test0212() {
	check(//
"Integrate[Sin[x]^5, x]", //
 "-Cos[x] + (2*Cos[x]^3)/3 - Cos[x]^5/5", //
2713);}

// {2746, 31}
public void test0213() {
	check(//
"Integrate[Sec[x]*(1 - Sin[x]), x]", //
 "Log[1 + Sin[x]]", //
2746, 31);}

// {3554, 8}
public void test0214() {
	check(//
"Integrate[Tan[x]^2, x]", //
 "-x + Tan[x]", //
3554, 8);}

// {3852}
public void test0215() {
	check(//
"Integrate[Sec[x]^4, x]", //
 "Tan[x] + Tan[x]^3/3", //
3852);}

// {3852}
public void test0216() {
	check(//
"Integrate[Sec[x]^6, x]", //
 "Tan[x] + (2*Tan[x]^3)/3 + Tan[x]^5/5", //
3852);}

// {2687, 30}
public void test0217() {
	check(//
"Integrate[Sec[x]^2*Tan[x]^4, x]", //
 "Tan[x]^5/5", //
2687, 30);}

// {2686, 30}
public void test0218() {
	check(//
"Integrate[Sec[x]^3*Tan[x], x]", //
 "Sec[x]^3/3", //
2686, 30);}

// {2686, 30}
public void test0219() {
	check(//
"Integrate[Sec[x]^6*Tan[x], x]", //
 "Sec[x]^6/6", //
2686, 30);}

// {2686, 30}
public void test0220() {
	check(//
"Integrate[Sec[x]^2*Tan[x], x]", //
 "Sec[x]^2/2", //
2686, 30);}

// {2691, 3855}
public void test0221() {
	check(//
"Integrate[Sec[x]*Tan[x]^2, x]", //
 "-ArcTanh[Sin[x]]/2 + (Sec[x]*Tan[x])/2", //
2691, 3855);}

// {3554, 8}
public void test0222() {
	check(//
"Integrate[Cot[x]^2, x]", //
 "-x - Cot[x]", //
3554, 8);}

// {3554, 3556}
public void test0223() {
	check(//
"Integrate[Cot[x]^3, x]", //
 "-Cot[x]^2/2 - Log[Sin[x]]", //
3554, 3556);}

// {3853, 3855}
public void test0224() {
	check(//
"Integrate[Csc[x]^3, x]", //
 "-ArcTanh[Cos[x]]/2 - (Cot[x]*Csc[x])/2", //
3853, 3855);}

// {3852}
public void test0225() {
	check(//
"Integrate[Csc[x]^4, x]", //
 "-Cot[x] - Cot[x]^3/3", //
3852);}

// {2645, 30}
public void test0226() {
	check(//
"Integrate[Cos[x]^5*Sin[x], x]", //
 "-Cos[x]^6/6", //
2645, 30);}

// {3756, 391}
public void test0227() {
	check(//
"Integrate[Cos[x]^2*(1 - Tan[x]^2), x]", //
 "Cos[x]*Sin[x]", //
3756, 391);}

// {2686, 30}
public void test0228() {
	check(//
"Integrate[Sec[x]^3*Tan[x], x]", //
 "Sec[x]^3/3", //
2686, 30);}

// {283, 222}
public void test0229() {
	check(//
"Integrate[Sqrt[9 - x^2]/x^2, x]", //
 "-(Sqrt[9 - x^2]/x) - ArcSin[x/3]", //
283, 222);}

// {223, 212}
public void test0230() {
	check(//
"Integrate[1/Sqrt[-a^2 + x^2], x]", //
 "ArcTanh[x/Sqrt[-a^2 + x^2]]", //
223, 212);}

// {201, 222}
public void test0231() {
	check(//
"Integrate[Sqrt[1 - 4*x^2], x]", //
 "(x*Sqrt[1 - 4*x^2])/2 + ArcSin[2*x]/4", //
201, 222);}

// {201, 221}
public void test0232() {
	check(//
"Integrate[Sqrt[1 + x^2], x]", //
 "(x*Sqrt[1 + x^2])/2 + ArcSinh[x]/2", //
201, 221);}

// {327, 222}
public void test0233() {
	check(//
"Integrate[x^2/Sqrt[5 - x^2], x]", //
 "-(x*Sqrt[5 - x^2])/2 + (5*ArcSin[x/Sqrt[5]])/2", //
327, 222);}

// {12, 267}
public void test0234() {
	check(//
"Integrate[5*x*Sqrt[1 + x^2], x]", //
 "(5*(1 + x^2)^(3/2))/3", //
12, 267);}

// {633, 221}
public void test0235() {
	check(//
"Integrate[1/Sqrt[8 + 4*x + x^2], x]", //
 "ArcSinh[(2 + x)/2]", //
633, 221);}

// {635, 212}
public void test0236() {
	check(//
"Integrate[1/Sqrt[-8 + 6*x + 9*x^2], x]", //
 "ArcTanh[(1 + 3*x)/Sqrt[-8 + 6*x + 9*x^2]]/3", //
635, 212);}

// {628, 627}
public void test0237() {
	check(//
"Integrate[(5 - 4*x - x^2)^(-5/2), x]", //
 "(2 + x)/(27*(5 - 4*x - x^2)^(3/2)) + (2*(2 + x))/(243*Sqrt[5 - 4*x - x^2])", //
628, 627);}

// {223, 212}
public void test0238() {
	check(//
"Integrate[1/Sqrt[a^2 + x^2], x]", //
 "ArcTanh[x/Sqrt[a^2 + x^2]]", //
223, 212);}

// {2099}
public void test0239() {
	check(//
"Integrate[(1 + 4*x - 2*x^2 + x^4)/(1 - x - x^2 + x^3), x]", //
 "2/(1 - x) + x + x^2/2 + Log[1 - x] - Log[1 + x]", //
2099);}

// {205, 209}
public void test0240() {
	check(//
"Integrate[(1 + x^2)^(-2), x]", //
 "x/(2*(1 + x^2)) + ArcTan[x]/2", //
205, 209);}

// {907}
public void test0241() {
	check(//
"Integrate[(-4 + 3*x + x^2)/((-1 + 2*x)^2*(3 + 2*x)), x]", //
 "-9/(32*(1 - 2*x)) + (41*Log[1 - 2*x])/128 - (25*Log[3 + 2*x])/128", //
907);}

// {45}
public void test0242() {
	check(//
"Integrate[x^2/(1 + x), x]", //
 "-x + x^2/2 + Log[1 + x]", //
45);}

// {45}
public void test0243() {
	check(//
"Integrate[x/(-5 + x), x]", //
 "x + 5*Log[5 - x]", //
45);}

// {78}
public void test0244() {
	check(//
"Integrate[(-1 + 4*x)/((-1 + x)*(2 + x)), x]", //
 "Log[1 - x] + 3*Log[2 + x]", //
78);}

// {45}
public void test0245() {
	check(//
"Integrate[(-5 + 6*x)/(3 + 2*x), x]", //
 "3*x - 7*Log[3 + 2*x]", //
45);}

// {45}
public void test0246() {
	check(//
"Integrate[(3 + 2*x)/(1 + x)^2, x]", //
 "-(1 + x)^(-1) + 2*Log[1 + x]", //
45);}

// {84}
public void test0247() {
	check(//
"Integrate[1/(x*(1 + x)*(3 + 2*x)), x]", //
 "Log[x]/3 - Log[1 + x] + (2*Log[3 + 2*x])/3", //
84);}

// {46}
public void test0248() {
	check(//
"Integrate[1/((-1 + x)^2*(4 + x)), x]", //
 "1/(5*(1 - x)) - Log[1 - x]/25 + Log[4 + x]/25", //
46);}

// {90}
public void test0249() {
	check(//
"Integrate[x^2/((-3 + x)*(2 + x)^2), x]", //
 "4/(5*(2 + x)) + (9*Log[3 - x])/25 + (16*Log[2 + x])/25", //
90);}

// {2099}
public void test0250() {
	check(//
"Integrate[(18 - 2*x - 4*x^2)/(-6 + x + 4*x^2 + x^3), x]", //
 "Log[1 - x] - 2*Log[2 + x] - 3*Log[3 + x]", //
2099);}

// {46}
public void test0251() {
	check(//
"Integrate[1/((-1 + x)^2*x^2), x]", //
 "(1 - x)^(-1) - x^(-1) - 2*Log[1 - x] + 2*Log[x]", //
46);}

// {45}
public void test0252() {
	check(//
"Integrate[x^2/(1 + x)^3, x]", //
 "-1/(2*(1 + x)^2) + 2/(1 + x) + Log[1 + x]", //
45);}

// {4419, 642}
public void test0253() {
	check(//
"Integrate[(Cos[x]*(-3 + 2*Sin[x]))/(2 - 3*Sin[x] + Sin[x]^2), x]", //
 "Log[2 - 3*Sin[x] + Sin[x]^2]", //
4419, 642);}

// {3153, 212}
public void test0254() {
	check(//
"Integrate[(-4*Cos[x] + 3*Sin[x])^(-1), x]", //
 "-ArcTanh[(3*Cos[x] + 4*Sin[x])/5]/5", //
3153, 212);}

// {65, 213}
public void test0255() {
	check(//
"Integrate[1/(x*Sqrt[1 + x]), x]", //
 "-2*ArcTanh[Sqrt[1 + x]]", //
65, 213);}

// {1607, 266}
public void test0256() {
	check(//
"Integrate[(-x^(1/3) + x)^(-1), x]", //
 "(3*Log[1 - x^(2/3)])/2", //
1607, 266);}

// {45}
public void test0257() {
	check(//
"Integrate[x^2/Sqrt[-1 + x], x]", //
 "2*Sqrt[-1 + x] + (4*(-1 + x)^(3/2))/3 + (2*(-1 + x)^(5/2))/5", //
45);}

// {3339, 629}
public void test0258() {
	check(//
"Integrate[Cos[x]/(Sin[x] + Sin[x]^2), x]", //
 "Log[Sin[x]] - Log[1 + Sin[x]]", //
3339, 629);}

// {3153, 212}
public void test0259() {
	check(//
"Integrate[(Cos[x] + Sin[x])^(-1), x]", //
 "-(ArcTanh[(Cos[x] - Sin[x])/Sqrt[2]]/Sqrt[2])", //
3153, 212);}

// {3200, 31}
public void test0260() {
	check(//
"Integrate[(1 - Cos[x] + Sin[x])^(-1), x]", //
 "-Log[1 + Cot[x/2]]", //
3200, 31);}

// {3153, 212}
public void test0261() {
	check(//
"Integrate[(4*Cos[x] + 3*Sin[x])^(-1), x]", //
 "-ArcTanh[(3*Cos[x] - 4*Sin[x])/5]/5", //
3153, 212);}

// {3153, 212}
public void test0262() {
	check(//
"Integrate[(b*Cos[x] + a*Sin[x])^(-1), x]", //
 "-(ArcTanh[(a*Cos[x] - b*Sin[x])/Sqrt[a^2 + b^2]]/Sqrt[a^2 + b^2])", //
3153, 212);}

// {211}
public void test0263() {
	check(//
"Integrate[(b^2*Cos[x]^2 + a^2*Sin[x]^2)^(-1), x]", //
 "ArcTan[(a*Tan[x])/b]/(a*b)", //
211);}

// {14}
public void test0264() {
	check(//
"Integrate[(1 + Sqrt[x])*Sqrt[x], x]", //
 "(2*x^(3/2))/3 + x^2/2", //
14);}

// {2691, 3855}
public void test0265() {
	check(//
"Integrate[Sec[x]*Tan[x]^2, x]", //
 "-ArcTanh[Sin[x]]/2 + (Sec[x]*Tan[x])/2", //
2691, 3855);}

// {2339, 30}
public void test0266() {
	check(//
"Integrate[1/(x*Sqrt[Log[x]]), x]", //
 "2*Sqrt[Log[x]]", //
2339, 30);}

// {45}
public void test0267() {
	check(//
"Integrate[(5 + 2*x)/(-3 + x), x]", //
 "2*x + 11*Log[3 - x]", //
45);}

// {2320, 2225}
public void test0268() {
	check(//
"Integrate[E^(E^x + x), x]", //
 "E^E^x", //
2320, 2225);}

// {45}
public void test0269() {
	check(//
"Integrate[x/(2 + x)^2, x]", //
 "2/(2 + x) + Log[2 + x]", //
45);}

// {3269, 209}
public void test0270() {
	check(//
"Integrate[Cos[x]/(1 + Sin[x]^2), x]", //
 "ArcTan[Sin[x]]", //
3269, 209);}

// {2243, 2240}
public void test0271() {
	check(//
"Integrate[x^5/E^x^3, x]", //
 "-1/(3*E^x^3) - x^3/(3*E^x^3)", //
2243, 2240);}

// {3554, 8}
public void test0272() {
	check(//
"Integrate[Tan[4*x]^2, x]", //
 "-x + Tan[4*x]/4", //
3554, 8);}

// {635, 212}
public void test0273() {
	check(//
"Integrate[1/Sqrt[-5 + 12*x + 9*x^2], x]", //
 "ArcTanh[(2 + 3*x)/Sqrt[-5 + 12*x + 9*x^2]]/3", //
635, 212);}

// {14}
public void test0274() {
	check(//
"Integrate[(1 - Sqrt[x])/x^(1/3), x]", //
 "(3*x^(2/3))/2 - (6*x^(7/6))/7", //
14);}

// {2320, 213}
public void test0275() {
	check(//
"Integrate[(-E^(-x) + E^x)^(-1), x]", //
 "-ArcTanh[E^x]", //
2320, 213);}

// {1607, 266}
public void test0276() {
	check(//
"Integrate[(x^(-1/3) + x)^(-1), x]", //
 "(3*Log[1 + x^(4/3)])/4", //
1607, 266);}

// {633, 222}
public void test0277() {
	check(//
"Integrate[1/Sqrt[5 - 4*x - x^2], x]", //
 "-ArcSin[(-2 - x)/3]", //
633, 222);}

// {2746, 31}
public void test0278() {
	check(//
"Integrate[(1 + Cos[x])*Csc[x], x]", //
 "Log[1 - Cos[x]]", //
2746, 31);}

// {2281, 213}
public void test0279() {
	check(//
"Integrate[E^x/(-1 + E^(2*x)), x]", //
 "-ArcTanh[E^x]", //
2281, 213);}

// {45}
public void test0280() {
	check(//
"Integrate[x*(c + x)^(1/3), x]", //
 "(-3*c*(c + x)^(4/3))/4 + (3*(c + x)^(7/3))/7", //
45);}

// {4420, 2717}
public void test0281() {
	check(//
"Integrate[Cos[Cos[x]]*Sin[x], x]", //
 "-Sin[Cos[x]]", //
4420, 2717);}

// {45}
public void test0282() {
	check(//
"Integrate[x^3/(1 + x)^10, x]", //
 "1/(9*(1 + x)^9) - 3/(8*(1 + x)^8) + 3/(7*(1 + x)^7) - 1/(6*(1 + x)^6)", //
45);}

// {281, 209}
public void test0283() {
	check(//
"Integrate[x^4/(16 + x^10), x]", //
 "ArcTan[x^5/4]/20", //
281, 209);}

// {3842, 3855}
public void test0284() {
	check(//
"Integrate[x*Sec[x]*Tan[x], x]", //
 "-ArcTanh[Sin[x]] + x*Sec[x]", //
3842, 3855);}

// {281, 213}
public void test0285() {
	check(//
"Integrate[x/(-a^4 + x^4), x]", //
 "-ArcTanh[x^2/a^2]/(2*a^2)", //
281, 213);}

// {4946, 31}
public void test0286() {
	check(//
"Integrate[ArcTan[Sqrt[x]]/Sqrt[x], x]", //
 "2*Sqrt[x]*ArcTan[Sqrt[x]] - Log[1 + x]", //
4946, 31);}

// {65, 209}
public void test0287() {
	check(//
"Integrate[1/(x*Sqrt[-25 + 2*x]), x]", //
 "(2*ArcTan[Sqrt[-25 + 2*x]/5])/5", //
65, 209);}

// {327, 222}
public void test0288() {
	check(//
"Integrate[x^2/Sqrt[5 - 4*x^2], x]", //
 "-(x*Sqrt[5 - 4*x^2])/8 + (5*ArcSin[(2*x)/Sqrt[5]])/16", //
327, 222);}

// {3853, 3855}
public void test0289() {
	check(//
"Integrate[Csc[x/2]^3, x]", //
 "-ArcTanh[Cos[x/2]] - Cot[x/2]*Csc[x/2]", //
3853, 3855);}

// {2320, 3855}
public void test0290() {
	check(//
"Integrate[E^x*Sech[E^x], x]", //
 "ArcTan[Sinh[E^x]]", //
2320, 3855);}

// {2713}
public void test0291() {
	check(//
"Integrate[Cos[x]^5, x]", //
 "Sin[x] - (2*Sin[x]^3)/3 + Sin[x]^5/5", //
2713);}

// {45}
public void test0292() {
	check(//
"Integrate[x*Sqrt[1 + 2*x], x]", //
 "-(1 + 2*x)^(3/2)/6 + (1 + 2*x)^(5/2)/10", //
45);}

// {3554, 3556}
public void test0293() {
	check(//
"Integrate[Tan[x]^3, x]", //
 "Log[Cos[x]] + Tan[x]^2/2", //
3554, 3556);}

// {12, 3855}
public void test0294() {
	check(//
"Integrate[Csc[x/3]/4, x]", //
 "(-3*ArcTanh[Cos[x/3]])/4", //
12, 3855);}

// {2686, 8}
public void test0295() {
	check(//
"Integrate[Sec[x]*Tan[x], x]", //
 "Sec[x]", //
2686, 8);}

// {2686, 8}
public void test0296() {
	check(//
"Integrate[Cot[x]*Csc[x], x]", //
 "-Csc[x]", //
2686, 8);}

// {8}
public void test0297() {
	check(//
"Integrate[Csc[2*x]*Tan[x], x]", //
 "Tan[x]/2", //
8);}

// {2747, 31}
public void test0298() {
	check(//
"Integrate[Sin[x]/(a - b*Cos[x]), x]", //
 "Log[a - b*Cos[x]]/b", //
2747, 31);}

// {3269, 211}
public void test0299() {
	check(//
"Integrate[Cos[x]/(a^2 + b^2*Sin[x]^2), x]", //
 "ArcTan[(b*Sin[x])/a]/(a*b)", //
3269, 211);}

// {3269, 214}
public void test0300() {
	check(//
"Integrate[Cos[x]/(a^2 - b^2*Sin[x]^2), x]", //
 "ArcTanh[(b*Sin[x])/a]/(a*b)", //
3269, 214);}

// {3260, 209}
public void test0301() {
	check(//
"Integrate[(4 - Cos[x]^2)^(-1), x]", //
 "x/(2*Sqrt[3]) + ArcTan[(Cos[x]*Sin[x])/(3 + 2*Sqrt[3] + Sin[x]^2)]/(2*Sqrt[3])", //
3260, 209);}

// {2281, 213}
public void test0302() {
	check(//
"Integrate[E^x/(-1 + E^(2*x)), x]", //
 "-ArcTanh[E^x]", //
2281, 213);}

// {2339, 29}
public void test0303() {
	check(//
"Integrate[1/(x*Log[x]), x]", //
 "Log[Log[x]]", //
2339, 29);}

// {209}
public void test0304() {
	check(//
"Integrate[1/(x*(1 + Log[x]^2)), x]", //
 "ArcTan[Log[x]]", //
209);}

// {2339, 29}
public void test0305() {
	check(//
"Integrate[1/(x*(1 - Log[x])), x]", //
 "-Log[1 - Log[x]]", //
2339, 29);}

// {2339, 29}
public void test0306() {
	check(//
"Integrate[1/(x*(1 + Log[x/a])), x]", //
 "Log[1 + Log[x/a]]", //
2339, 29);}

// {45}
public void test0307() {
	check(//
"Integrate[(-1 + 2*x)/(3 + 2*x), x]", //
 "x - 2*Log[3 + 2*x]", //
45);}

// {2715, 8}
public void test0308() {
	check(//
"Integrate[Sin[x]^2, x]", //
 "x/2 - (Cos[x]*Sin[x])/2", //
2715, 8);}

// {2715, 8}
public void test0309() {
	check(//
"Integrate[Cos[x]^2, x]", //
 "x/2 + (Cos[x]*Sin[x])/2", //
2715, 8);}

// {2645, 30}
public void test0310() {
	check(//
"Integrate[Cos[x]^3*Sin[x], x]", //
 "-Cos[x]^4/4", //
2645, 30);}

// {2686}
public void test0311() {
	check(//
"Integrate[Cot[x]^3*Csc[x], x]", //
 "Csc[x] - Csc[x]^3/3", //
2686);}

// {3554, 8}
public void test0312() {
	check(//
"Integrate[Cot[(3*x)/4]^2, x]", //
 "-x - (4*Cot[(3*x)/4])/3", //
3554, 8);}

// {3558, 3556}
public void test0313() {
	check(//
"Integrate[(1 + Tan[2*x])^2, x]", //
 "-Log[Cos[2*x]] + Tan[2*x]/2", //
3558, 3556);}

// {2814, 2727}
public void test0314() {
	check(//
"Integrate[Sin[x]/(1 + Sin[x]), x]", //
 "x + Cos[x]/(1 + Sin[x])", //
2814, 2727);}

// {2814, 2727}
public void test0315() {
	check(//
"Integrate[Cos[x]/(1 - Cos[x]), x]", //
 "-x - Sin[x]/(1 - Cos[x])", //
2814, 2727);}

// {65, 211}
public void test0316() {
	check(//
"Integrate[1/(Sqrt[x]*(b + a*x)), x]", //
 "(2*ArcTan[(Sqrt[a]*Sqrt[x])/Sqrt[b]])/(Sqrt[a]*Sqrt[b])", //
65, 211);}

// {633, 222}
public void test0317() {
	check(//
"Integrate[1/Sqrt[2 + x - x^2], x]", //
 "-ArcSin[(1 - 2*x)/3]", //
633, 222);}

// {633, 221}
public void test0318() {
	check(//
"Integrate[1/Sqrt[5 - 4*x + 3*x^2], x]", //
 "-(ArcSinh[(2 - 3*x)/Sqrt[11]]/Sqrt[3])", //
633, 221);}

// {633, 222}
public void test0319() {
	check(//
"Integrate[1/Sqrt[x - x^2], x]", //
 "-ArcSin[1 - 2*x]", //
633, 222);}

// {738, 212}
public void test0320() {
	check(//
"Integrate[1/(x*Sqrt[2 + x - x^2]), x]", //
 "-(ArcTanh[(4 + x)/(2*Sqrt[2]*Sqrt[2 + x - x^2])]/Sqrt[2])", //
738, 212);}

// {212}
public void test0321() {
	check(//
"Integrate[(1 + Tan[x]^2)/(1 - Tan[x]^2), x]", //
 "ArcTanh[2*Cos[x]*Sin[x]]/2", //
212);}

// {4737}
public void test0322() {
	check(//
"Integrate[ArcSin[x/a]^(3/2)/Sqrt[a^2 - x^2], x]", //
 "(2*a*Sqrt[1 - x^2/a^2]*ArcSin[x/a]^(5/2))/(5*Sqrt[a^2 - x^2])", //
4737);}

// {2342, 2341}
public void test0323() {
	check(//
"Integrate[x*Log[x]^2, x]", //
 "x^2/4 - (x^2*Log[x])/2 + (x^2*Log[x]^2)/2", //
2342, 2341);}

// {2713}
public void test0324() {
	check(//
"Integrate[Cos[x]^5, x]", //
 "Sin[x] - (2*Sin[x]^3)/3 + Sin[x]^5/5", //
2713);}

// {625}
public void test0325() {
	check(//
"Integrate[(2*x + 3*x^2)^3, x]", //
 "2*x^4 + (36*x^5)/5 + 9*x^6 + (27*x^7)/7", //
625);}

// {645}
public void test0326() {
	check(//
"Integrate[(-1 + x)*(-1 + 2*x + 3*x^2)^2, x]", //
 "-x + (5*x^2)/2 - (2*x^3)/3 - (7*x^4)/2 + (3*x^5)/5 + (3*x^6)/2", //
645);}

// {45}
public void test0327() {
	check(//
"Integrate[x^3/(1 + 2*x), x]", //
 "x/8 - x^2/8 + x^3/6 - Log[1 + 2*x]/16", //
45);}

// {2099}
public void test0328() {
	check(//
"Integrate[(11*a^2 - 7*a*x + 5*x^2)/(-6*a^3 + 11*a^2*x - 6*a*x^2 + x^3), x]", //
 "(9*Log[a - x])/2 - 17*Log[2*a - x] + (35*Log[3*a - x])/2", //
2099);}

// {186}
public void test0329() {
	check(//
"Integrate[1/((-4 + x)*(-3 + x)*(-2 + x)*(-1 + x)), x]", //
 "-Log[1 - x]/6 + Log[2 - x]/2 - Log[3 - x]/2 + Log[4 - x]/6", //
186);}

// {711}
public void test0330() {
	check(//
"Integrate[(1 + x^2)/(-1 + x)^3, x]", //
 "-(1 - x)^(-2) + 2/(1 - x) + Log[1 - x]", //
711);}

// {45}
public void test0331() {
	check(//
"Integrate[x^5/(3 + x)^2, x]", //
 "-108*x + (27*x^2)/2 - 2*x^3 + x^4/4 + 243/(3 + x) + 405*Log[3 + x]", //
45);}

// {2099}
public void test0332() {
	check(//
"Integrate[(-2 + 5*x^3)/(-27 + 18*x^2 - 8*x^3 + x^4), x]", //
 "-133/(8*(3 - x)^2) + 407/(16*(3 - x)) + (313*Log[3 - x])/64 + (7*Log[1 + x])/64", //
2099);}

// {1634}
public void test0333() {
	check(//
"Integrate[(-9 + 3*x - 6*x^2 + x^3)/((3 + x)^2*(4 + x)^2), x]", //
 "99/(3 + x) + 181/(4 + x) + 264*Log[3 + x] - 263*Log[4 + x]", //
1634);}

// {2083}
public void test0334() {
	check(//
"Integrate[(x^3 - x^4 - x^5 + x^6)^(-1), x]", //
 "1/(2*(1 - x)) - 1/(2*x^2) - x^(-1) - (7*Log[1 - x])/4 + 2*Log[x] - Log[1 + x]/4", //
2083);}

// {281, 212}
public void test0335() {
	check(//
"Integrate[x/(a^4 - x^4), x]", //
 "ArcTanh[x^2/a^2]/(2*a^2)", //
281, 212);}

// {281, 209}
public void test0336() {
	check(//
"Integrate[x/(a^4 + x^4), x]", //
 "ArcTan[x^2/a^2]/(2*a^2)", //
281, 209);}

// {1864}
public void test0337() {
	check(//
"Integrate[(1 - 4*x^2 + x^3)/(-2 + x)^4, x]", //
 "-7/(3*(2 - x)^3) + 2/(2 - x)^2 + 2/(2 - x) + Log[2 - x]", //
1864);}

// {45}
public void test0338() {
	check(//
"Integrate[x^3/(-1 + x)^12, x]", //
 "1/(11*(1 - x)^11) - 3/(10*(1 - x)^10) + 1/(3*(1 - x)^9) - 1/(8*(1 - x)^8)", //
45);}

// {46}
public void test0339() {
	check(//
"Integrate[1/((5 - 6*x)^2*x^2), x]", //
 "6/(25*(5 - 6*x)) - 1/(25*x) - (12*Log[5 - 6*x])/125 + (12*Log[x])/125", //
46);}

// {46}
public void test0340() {
	check(//
"Integrate[1/((2 + x)^3*(3 + x)^4), x]", //
 "-1/(2*(2 + x)^2) + 4/(2 + x) + 1/(3*(3 + x)^3) + 3/(2*(3 + x)^2) + 6/(3 + x) + 10*Log[2 + x] - 10*Log[3 + x]", //
46);}

// {2099}
public void test0341() {
	check(//
"Integrate[(5 - 3*x + 6*x^2 + 5*x^3 - x^4)/(-1 + x + 2*x^2 - 2*x^3 - x^4 + x^5), x]", //
 "-3/(2*(1 - x)^2) + 2/(1 - x) + (1 + x)^(-1) + Log[1 - x] - 2*Log[1 + x]", //
2099);}

// {46}
public void test0342() {
	check(//
"Integrate[1/((1 - 4*x)^3*(2 - 3*x)), x]", //
 "1/(10*(1 - 4*x)^2) - 3/(25*(1 - 4*x)) - (9*Log[1 - 4*x])/125 + (9*Log[2 - 3*x])/125", //
46);}

// {46}
public void test0343() {
	check(//
"Integrate[1/((2 + x)^3*(3 + x)^4), x]", //
 "-1/(2*(2 + x)^2) + 4/(2 + x) + 1/(3*(3 + x)^3) + 3/(2*(3 + x)^2) + 6/(3 + x) + 10*Log[2 + x] - 10*Log[3 + x]", //
46);}

// {45}
public void test0344() {
	check(//
"Integrate[x^5/(3 + x)^2, x]", //
 "-108*x + (27*x^2)/2 - 2*x^3 + x^4/4 + 243/(3 + x) + 405*Log[3 + x]", //
45);}

// {645}
public void test0345() {
	check(//
"Integrate[(b1 + c1*x)*(a + 2*b*x + c*x^2), x]", //
 "a*b1*x + ((2*b*b1 + a*c1)*x^2)/2 + ((b1*c + 2*b*c1)*x^3)/3 + (c*c1*x^4)/4", //
645);}

// {645}
public void test0346() {
	check(//
"Integrate[(b1 + c1*x)*(a + 2*b*x + c*x^2)^2, x]", //
 "a^2*b1*x + (a*(4*b*b1 + a*c1)*x^2)/2 + (2*(2*b^2*b1 + a*b1*c + 2*a*b*c1)*x^3)/3 + ((2*b*b1*c + 2*b^2*c1 + a*c*c1)*x^4)/2 + (c*(b1*c + 4*b*c1)*x^5)/5 + (c^2*c1*x^6)/6", //
645);}

// {645}
public void test0347() {
	check(//
"Integrate[(b1 + c1*x)*(a + 2*b*x + c*x^2)^3, x]", //
 "a^3*b1*x + (a^2*(6*b*b1 + a*c1)*x^2)/2 + a*(4*b^2*b1 + a*b1*c + 2*a*b*c1)*x^3 + ((8*b^3*b1 + 12*a*b*b1*c + 12*a*b^2*c1 + 3*a^2*c*c1)*x^4)/4 + ((12*b^2*b1*c + 3*a*b1*c^2 + 8*b^3*c1 + 12*a*b*c*c1)*x^5)/5 + (c*(2*b*b1*c + 4*b^2*c1 + a*c*c1)*x^6)/2 + (c^2*(b1*c + 6*b*c1)*x^7)/7 + (c^3*c1*x^8)/8", //
645);}

// {645}
public void test0348() {
	check(//
"Integrate[(b1 + c1*x)*(a + 2*b*x + c*x^2)^4, x]", //
 "a^4*b1*x + (a^3*(8*b*b1 + a*c1)*x^2)/2 + (4*a^2*(6*b^2*b1 + a*b1*c + 2*a*b*c1)*x^3)/3 + a*(8*b^3*b1 + 6*a*b*b1*c + 6*a*b^2*c1 + a^2*c*c1)*x^4 + (2*(8*b^4*b1 + 24*a*b^2*b1*c + 3*a^2*b1*c^2 + 16*a*b^3*c1 + 12*a^2*b*c*c1)*x^5)/5 + ((16*b^3*b1*c + 12*a*b*b1*c^2 + 8*b^4*c1 + 24*a*b^2*c*c1 + 3*a^2*c^2*c1)*x^6)/3 + (4*c*(6*b^2*b1*c + a*b1*c^2 + 8*b^3*c1 + 6*a*b*c*c1)*x^7)/7 + (c^2*(2*b*b1*c + 6*b^2*c1 + a*c*c1)*x^8)/2 + (c^3*(b1*c + 8*b*c1)*x^9)/9 + (c^4*c1*x^10)/10", //
645);}

// {654, 638}
public void test0349() {
	check(//
"Integrate[(b1 + c1*x)*(a + 2*b*x + c*x^2)^n, x]", //
 "(c1*(a + 2*b*x + c*x^2)^(1 + n))/(2*c*(1 + n)) - (2^n*(b1*c - b*c1)*(-((b - Sqrt[b^2 - a*c] + c*x)/Sqrt[b^2 - a*c]))^(-1 - n)*(a + 2*b*x + c*x^2)^(1 + n)*Hypergeometric2F1[-n, 1 + n, 2 + n, (b + Sqrt[b^2 - a*c] + c*x)/(2*Sqrt[b^2 - a*c])])/(c*Sqrt[b^2 - a*c]*(1 + n))", //
654, 638);}

// {654, 638}
public void test0350() {
	check(//
"Integrate[(b1 + c1*x)/(a + 2*b*x + c*x^2)^n, x]", //
 "(c1*(a + 2*b*x + c*x^2)^(1 - n))/(2*c*(1 - n)) - ((b1*c - b*c1)*(-((b - Sqrt[b^2 - a*c] + c*x)/Sqrt[b^2 - a*c]))^(-1 + n)*(a + 2*b*x + c*x^2)^(1 - n)*Hypergeometric2F1[1 - n, n, 2 - n, (b + Sqrt[b^2 - a*c] + c*x)/(2*Sqrt[b^2 - a*c])])/(2^n*c*Sqrt[b^2 - a*c]*(1 - n))", //
654, 638);}

// {378}
public void test0351() {
	check(//
"Integrate[x/(1 + Sqrt[1 + x]), x]", //
 "-x + (2*(1 + x)^(3/2))/3", //
378);}

// {628, 627}
public void test0352() {
	check(//
"Integrate[(-3 - 2*x + x^2)^(-5/2), x]", //
 "(1 - x)/(12*(-3 - 2*x + x^2)^(3/2)) - (1 - x)/(24*Sqrt[-3 - 2*x + x^2])", //
628, 627);}

// {633, 222}
public void test0353() {
	check(//
"Integrate[1/Sqrt[4 + 3*x - 2*x^2], x]", //
 "-(ArcSin[(3 - 4*x)/Sqrt[41]]/Sqrt[2])", //
633, 222);}

// {633, 222}
public void test0354() {
	check(//
"Integrate[1/Sqrt[-3 + 4*x - x^2], x]", //
 "-ArcSin[2 - x]", //
633, 222);}

// {633, 222}
public void test0355() {
	check(//
"Integrate[1/Sqrt[-2 - 5*x - 3*x^2], x]", //
 "ArcSin[5 + 6*x]/Sqrt[3]", //
633, 222);}

// {385, 209}
public void test0356() {
	check(//
"Integrate[1/(Sqrt[1 - x^2]*(4 + x^2)), x]", //
 "ArcTan[(Sqrt[5]*x)/(2*Sqrt[1 - x^2])]/(2*Sqrt[5])", //
385, 209);}

// {385, 212}
public void test0357() {
	check(//
"Integrate[1/((4 + x^2)*Sqrt[1 + 4*x^2]), x]", //
 "ArcTanh[(Sqrt[15]*x)/(2*Sqrt[1 + 4*x^2])]/(2*Sqrt[15])", //
385, 212);}

// {1043, 212}
public void test0358() {
	check(//
"Integrate[(-2 + x)/((17 - 18*x + 5*x^2)*Sqrt[13 - 22*x + 10*x^2]), x]", //
 "ArcTanh[(Sqrt[35]*(1 - x))/(2*Sqrt[13 - 22*x + 10*x^2])]/(2*Sqrt[35])", //
1043, 212);}

// {1828, 221}
public void test0359() {
	check(//
"Integrate[(1 - x + x^2)/(1 + x^2)^(3/2), x]", //
 "1/Sqrt[1 + x^2] + ArcSinh[x]", //
1828, 221);}

// {633, 221}
public void test0360() {
	check(//
"Integrate[1/Sqrt[1 + x + x^2], x]", //
 "ArcSinh[(1 + 2*x)/Sqrt[3]]", //
633, 221);}

// {738, 212}
public void test0361() {
	check(//
"Integrate[1/((1 + x)*Sqrt[1 + x + x^2]), x]", //
 "-ArcTanh[(1 - x)/(2*Sqrt[1 + x + x^2])]", //
738, 212);}

// {628, 627}
public void test0362() {
	check(//
"Integrate[(1 + 8*x + 3*x^2)^(-5/2), x]", //
 "-(4 + 3*x)/(39*(1 + 8*x + 3*x^2)^(3/2)) + (2*(4 + 3*x))/(169*Sqrt[1 + 8*x + 3*x^2])", //
628, 627);}

// {628, 627}
public void test0363() {
	check(//
"Integrate[(5 + 4*x - 3*x^2)^(-5/2), x]", //
 "-(2 - 3*x)/(57*(5 + 4*x - 3*x^2)^(3/2)) - (2*(2 - 3*x))/(361*Sqrt[5 + 4*x - 3*x^2])", //
628, 627);}

// {45}
public void test0364() {
	check(//
"Integrate[(4 - 3*x)^(4/3)*x^2, x]", //
 "(-16*(4 - 3*x)^(7/3))/63 + (4*(4 - 3*x)^(10/3))/45 - (4 - 3*x)^(13/3)/117", //
45);}

// {396, 245}
public void test0365() {
	check(//
"Integrate[(-1 + x^3)/(2 + x^3)^(1/3), x]", //
 "(x*(2 + x^3)^(2/3))/3 - (5*ArcTan[(1 + (2*x)/(2 + x^3)^(1/3))/Sqrt[3]])/(3*Sqrt[3]) + (5*Log[-x + (2 + x^3)^(1/3)])/6", //
396, 245);}

// {1713, 209}
public void test0366() {
	check(//
"Integrate[(1 - x^2)/((1 + x^2)*Sqrt[1 + x^4]), x]", //
 "ArcTan[(Sqrt[2]*x)/Sqrt[1 + x^4]]/Sqrt[2]", //
1713, 209);}

// {1713, 212}
public void test0367() {
	check(//
"Integrate[(1 + x^2)/((1 - x^2)*Sqrt[1 + x^4]), x]", //
 "ArcTanh[(Sqrt[2]*x)/Sqrt[1 + x^4]]/Sqrt[2]", //
1713, 212);}

// {1712, 212}
public void test0368() {
	check(//
"Integrate[(1 + x^2)/((1 - x^2)*Sqrt[1 + x^2 + x^4]), x]", //
 "ArcTanh[(Sqrt[3]*x)/Sqrt[1 + x^2 + x^4]]/Sqrt[3]", //
1712, 212);}

// {1712, 209}
public void test0369() {
	check(//
"Integrate[(1 - x^2)/((1 + x^2)*Sqrt[1 + x^2 + x^4]), x]", //
 "ArcTan[x/Sqrt[1 + x^2 + x^4]]", //
1712, 209);}

// {2153, 209}
public void test0370() {
	check(//
"Integrate[1/((1 + x^4)*Sqrt[-x^2 + Sqrt[1 + x^4]]), x]", //
 "ArcTan[x/Sqrt[-x^2 + Sqrt[1 + x^4]]]", //
2153, 209);}

// {2153, 209}
public void test0371() {
	check(//
"Integrate[1/((1 + x^(2*n))*Sqrt[-x^2 + (1 + x^(2*n))^n^(-1)]), x]", //
 "ArcTan[x/Sqrt[-x^2 + (1 + x^(2*n))^n^(-1)]]", //
2153, 209);}

// {2715, 8}
public void test0372() {
	check(//
"Integrate[Cos[x]^2, x]", //
 "x/2 + (Cos[x]*Sin[x])/2", //
2715, 8);}

// {2713}
public void test0373() {
	check(//
"Integrate[Cos[x]^3, x]", //
 "Sin[x] - Sin[x]^3/3", //
2713);}

// {2713}
public void test0374() {
	check(//
"Integrate[-Sin[Pi/12 - 3*x]^3, x]", //
 "-Cos[Pi/12 - 3*x]/3 + Cos[Pi/12 - 3*x]^3/9", //
2713);}

// {3852}
public void test0375() {
	check(//
"Integrate[Csc[x]^6, x]", //
 "-Cot[x] - (2*Cot[x]^3)/3 - Cot[x]^5/5", //
3852);}

// {3852}
public void test0376() {
	check(//
"Integrate[Sec[x]^12, x]", //
 "Tan[x] + (5*Tan[x]^3)/3 + 2*Tan[x]^5 + (10*Tan[x]^7)/7 + (5*Tan[x]^9)/9 + Tan[x]^11/11", //
3852);}

// {3853, 3855}
public void test0377() {
	check(//
"Integrate[Sec[Pi/4 + 3*x]^3, x]", //
 "ArcTanh[Sin[Pi/4 + 3*x]]/6 + (Sec[Pi/4 + 3*x]*Tan[Pi/4 + 3*x])/6", //
3853, 3855);}

// {2687, 30}
public void test0378() {
	check(//
"Integrate[Sec[x]^2*Tan[x]^2, x]", //
 "Tan[x]^3/3", //
2687, 30);}

// {2686}
public void test0379() {
	check(//
"Integrate[Cot[x]^3*Csc[x], x]", //
 "Csc[x] - Csc[x]^3/3", //
2686);}

// {2686, 30}
public void test0380() {
	check(//
"Integrate[Sec[x]^3*Tan[x], x]", //
 "Sec[x]^3/3", //
2686, 30);}

// {209}
public void test0381() {
	check(//
"Integrate[(4 - 3*Cos[x]^2 + 5*Sin[x]^2)^(-1), x]", //
 "x/3 + ArcTan[(2*Cos[x]*Sin[x])/(1 + 2*Sin[x]^2)]/3", //
209);}

// {212}
public void test0382() {
	check(//
"Integrate[Cos[x]^2*Sec[3*x], x]", //
 "ArcTanh[2*Sin[x]]/2", //
212);}

// {4442, 213}
public void test0383() {
	check(//
"Integrate[Sec[2*x]*Sin[x], x]", //
 "ArcTanh[Sqrt[2]*Cos[x]]/Sqrt[2]", //
4442, 213);}

// {2728, 212}
public void test0384() {
	check(//
"Integrate[1/Sqrt[1 + Cos[2*x]], x]", //
 "ArcTanh[Sin[2*x]/(Sqrt[2]*Sqrt[1 + Cos[2*x]])]/Sqrt[2]", //
2728, 212);}

// {2728, 212}
public void test0385() {
	check(//
"Integrate[1/Sqrt[1 - Cos[2*x]], x]", //
 "-(ArcTanh[Sin[2*x]/(Sqrt[2]*Sqrt[1 - Cos[2*x]])]/Sqrt[2])", //
2728, 212);}

// {4387, 4390}
public void test0386() {
	check(//
"Integrate[Sin[x]*Sqrt[Sin[2*x]], x]", //
 "-ArcSin[Cos[x] - Sin[x]]/4 + Log[Cos[x] + Sin[x] + Sqrt[Sin[2*x]]]/4 - (Cos[x]*Sqrt[Sin[2*x]])/2", //
4387, 4390);}

// {4384, 4376}
public void test0387() {
	check(//
"Integrate[Sec[x]^3/Sqrt[Sin[2*x]], x]", //
 "(4*Sec[x]*Sqrt[Sin[2*x]])/5 + (Sec[x]^3*Sqrt[Sin[2*x]])/5", //
4384, 4376);}

// {4427, 6818}
public void test0388() {
	check(//
"Integrate[(Sec[x]^2*Tan[x]*(1 + (1 - 8*Tan[x]^2)^(1/3)))/(1 - 8*Tan[x]^2)^(2/3), x]", //
 "(-3*(1 + (1 - 8*Tan[x]^2)^(1/3))^2)/32", //
4427, 6818);}

// {198, 197}
public void test0389() {
	check(//
"Integrate[(1 + 2*x^2)^(-5/2), x]", //
 "x/(3*(1 + 2*x^2)^(3/2)) + (2*x)/(3*Sqrt[1 + 2*x^2])", //
198, 197);}

// {628, 627}
public void test0390() {
	check(//
"Integrate[(-1 - 2*x + x^2)^(-5/2), x]", //
 "(1 - x)/(6*(-1 - 2*x + x^2)^(3/2)) - (1 - x)/(6*Sqrt[-1 - 2*x + x^2])", //
628, 627);}

// {276}
public void test0391() {
	check(//
"Integrate[(5 + x^2)^2/x^(13/3), x]", //
 "-15/(2*x^(10/3)) - 15/(2*x^(4/3)) + (3*x^(2/3))/2", //
276);}

// {294, 222}
public void test0392() {
	check(//
"Integrate[x^2/(3 - x^2)^(3/2), x]", //
 "x/Sqrt[3 - x^2] - ArcSin[x/Sqrt[3]]", //
294, 222);}

// {628, 627}
public void test0393() {
	check(//
"Integrate[(-7 + 6*x - x^2)^(-5/2), x]", //
 "-(3 - x)/(6*(-7 + 6*x - x^2)^(3/2)) - (3 - x)/(6*Sqrt[-7 + 6*x - x^2])", //
628, 627);}

// {625}
public void test0394() {
	check(//
"Integrate[(1 - 2*x - 2*x^2)^3, x]", //
 "x - 3*x^2 + 2*x^3 + 4*x^4 - (12*x^5)/5 - 4*x^6 - (8*x^7)/7", //
625);}

// {645}
public void test0395() {
	check(//
"Integrate[(-1 + 5*x)*(-1 - x + x^2)^2, x]", //
 "-x + (3*x^2)/2 + (11*x^3)/3 - (3*x^4)/4 - (11*x^5)/5 + (5*x^6)/6", //
645);}

// {652, 627}
public void test0396() {
	check(//
"Integrate[(1 + 3*x)/(1 - 8*x + 2*x^2)^(5/2), x]", //
 "(1 - 2*x)/(6*(1 - 8*x + 2*x^2)^(3/2)) - (2*(2 - x))/(21*Sqrt[1 - 8*x + 2*x^2])", //
652, 627);}

// {1674, 650}
public void test0397() {
	check(//
"Integrate[(-1 - 8*x + 8*x^3)/(1 + 2*x - 4*x^2)^(5/2), x]", //
 "(-4*(1 + x))/(15*(1 + 2*x - 4*x^2)^(3/2)) - (7 + 122*x)/(75*Sqrt[1 + 2*x - 4*x^2])", //
1674, 650);}

// {6843, 32}
public void test0398() {
	check(//
"Integrate[(2*x + Sin[2*x])/(Cos[x] + x*Sin[x])^2, x]", //
 "2/(1 + Cot[x]/x)", //
6843, 32);}

// {2325, 2225}
public void test0399() {
	check(//
"Integrate[a^(m*x)*b^(n*x), x]", //
 "(a^(m*x)*b^(n*x))/(m*Log[a] + n*Log[b])", //
2325, 2225);}

// {2225}
public void test0400() {
	check(//
"Integrate[1 + a^(m*x), x]", //
 "x + a^(m*x)/(m*Log[a])", //
2225);}

// {2320, 67}
public void test0401() {
	check(//
"Integrate[(1 + a^(m*x))^n, x]", //
 "-(((1 + a^(m*x))^(1 + n)*Hypergeometric2F1[1, 1 + n, 2 + n, 1 + a^(m*x)])/(m*(1 + n)*Log[a]))", //
2320, 67);}

// {2225}
public void test0402() {
	check(//
"Integrate[1 - a^(m*x), x]", //
 "x - a^(m*x)/(m*Log[a])", //
2225);}

// {2320, 67}
public void test0403() {
	check(//
"Integrate[(1 - a^(m*x))^n, x]", //
 "-(((1 - a^(m*x))^(1 + n)*Hypergeometric2F1[1, 1 + n, 2 + n, 1 - a^(m*x)])/(m*(1 + n)*Log[a]))", //
2320, 67);}

// {2278, 32}
public void test0404() {
	check(//
"Integrate[E^(n*x)*(a + b*E^(n*x))^(r/s), x]", //
 "((a + b*E^(n*x))^((r + s)/s)*s)/(b*n*(r + s))", //
2278, 32);}

// {2320, 67}
public void test0405() {
	check(//
"Integrate[(a + b*E^(n*x))^(r/s), x]", //
 "-(((a + b*E^(n*x))^((r + s)/s)*s*Hypergeometric2F1[1, (r + s)/s, 2 + r/s, 1 + (b*E^(n*x))/a])/(a*n*(r + s)))", //
2320, 67);}

// {2319, 4518}
public void test0406() {
	check(//
"Integrate[Cos[(3*x)/2]/(3^(3*x))^(1/4), x]", //
 "(-4*Cos[(3*x)/2]*Log[3])/(3*(3^(3*x))^(1/4)*(4 + Log[3]^2)) + (8*Sin[(3*x)/2])/(3*(3^(3*x))^(1/4)*(4 + Log[3]^2))", //
2319, 4518);}

// {4520, 2225}
public void test0407() {
	check(//
"Integrate[E^(m*x)*Cos[x]^2, x]", //
 "(2*E^(m*x))/(m*(4 + m^2)) + (E^(m*x)*m*Cos[x]^2)/(4 + m^2) + (2*E^(m*x)*Cos[x]*Sin[x])/(4 + m^2)", //
4520, 2225);}

// {4519, 4517}
public void test0408() {
	check(//
"Integrate[E^(m*x)*Sin[x]^3, x]", //
 "(-6*E^(m*x)*Cos[x])/(9 + 10*m^2 + m^4) + (6*E^(m*x)*m*Sin[x])/(9 + 10*m^2 + m^4) - (3*E^(m*x)*Cos[x]*Sin[x]^2)/(9 + m^2) + (E^(m*x)*m*Sin[x]^3)/(9 + m^2)", //
4519, 4517);}

// {4542, 4536}
public void test0409() {
	check(//
"Integrate[E^x/(1 + Cos[x]), x]", //
 "(1 - I)*E^((1 + I)*x)*Hypergeometric2F1[1 - I, 2, 2 - I, -E^(I*x)]", //
4542, 4536);}

// {4543, 4538}
public void test0410() {
	check(//
"Integrate[E^x/(1 - Cos[x]), x]", //
 "(-1 + I)*E^((1 + I)*x)*Hypergeometric2F1[1 - I, 2, 2 - I, E^(I*x)]", //
4543, 4538);}

// {4541, 4535}
public void test0411() {
	check(//
"Integrate[E^x/(1 + Sin[x]), x]", //
 "(-1 + I)*E^((1 - I)*x)*Hypergeometric2F1[1 + I, 2, 2 + I, (-I)/E^(I*x)]", //
4541, 4535);}

// {4541, 4535}
public void test0412() {
	check(//
"Integrate[E^x/(1 - Sin[x]), x]", //
 "(1 + I)*E^((1 + I)*x)*Hypergeometric2F1[1 - I, 2, 2 - I, (-I)*E^(I*x)]", //
4541, 4535);}

// {2715, 8}
public void test0413() {
	check(//
"Integrate[Cosh[x]^2, x]", //
 "x/2 + (Cosh[x]*Sinh[x])/2", //
2715, 8);}

// {2713}
public void test0414() {
	check(//
"Integrate[Sinh[x]^5, x]", //
 "Cosh[x] - (2*Cosh[x]^3)/3 + Cosh[x]^5/5", //
2713);}

// {3853, 3855}
public void test0415() {
	check(//
"Integrate[Csch[x]^3, x]", //
 "ArcTanh[Cosh[x]]/2 - (Coth[x]*Csch[x])/2", //
3853, 3855);}

// {2729, 2727}
public void test0416() {
	check(//
"Integrate[(1 + Cosh[x])^(-2), x]", //
 "Sinh[x]/(3*(1 + Cosh[x])^2) + Sinh[x]/(3*(1 + Cosh[x]))", //
2729, 2727);}

// {3565, 3611}
public void test0417() {
	check(//
"Integrate[(a + b*Tanh[x])^(-1), x]", //
 "(a*x)/(a^2 - b^2) - (b*Log[a*Cosh[x] + b*Sinh[x]])/(a^2 - b^2)", //
3565, 3611);}

// {3260, 214}
public void test0418() {
	check(//
"Integrate[(a^2 + b^2*Cosh[x]^2)^(-1), x]", //
 "ArcTanh[(a*Tanh[x])/Sqrt[a^2 + b^2]]/(a*Sqrt[a^2 + b^2])", //
3260, 214);}

// {3260, 214}
public void test0419() {
	check(//
"Integrate[(a^2 - b^2*Cosh[x]^2)^(-1), x]", //
 "ArcTanh[(a*Tanh[x])/Sqrt[a^2 - b^2]]/(a*Sqrt[a^2 - b^2])", //
3260, 214);}

// {4441, 221}
public void test0420() {
	check(//
"Integrate[Cosh[x]/Sqrt[Cosh[2*x]], x]", //
 "ArcSinh[Sqrt[2]*Sinh[x]]/Sqrt[2]", //
4441, 221);}

// {2320, 30}
public void test0421() {
	check(//
"Integrate[E^x/(Cosh[x] - Sinh[x]), x]", //
 "E^(2*x)/2", //
2320, 30);}

// {2320, 29}
public void test0422() {
	check(//
"Integrate[E^x/(Cosh[x] + Sinh[x]), x]", //
 "x", //
2320, 29);}

// {2342, 2341}
public void test0423() {
	check(//
"Integrate[x^m*Log[x]^2, x]", //
 "(2*x^(1 + m))/(1 + m)^3 - (2*x^(1 + m)*Log[x])/(1 + m)^2 + (x^(1 + m)*Log[x]^2)/(1 + m)", //
2342, 2341);}

// {2342, 2341}
public void test0424() {
	check(//
"Integrate[Log[x]^2/x^(5/2), x]", //
 "-16/(27*x^(3/2)) - (8*Log[x])/(9*x^(3/2)) - (2*Log[x]^2)/(3*x^(3/2))", //
2342, 2341);}

// {2350}
public void test0425() {
	check(//
"Integrate[(a + b*x)*Log[x], x]", //
 "-(a*x) - (b*x^2)/4 + a*x*Log[x] + (b*x^2*Log[x])/2", //
2350);}

// {2354, 2438}
public void test0426() {
	check(//
"Integrate[Log[x]/(a + b*x), x]", //
 "(Log[x]*Log[1 + (b*x)/a])/b + PolyLog[2, -((b*x)/a)]/b", //
2354, 2438);}

// {2351, 31}
public void test0427() {
	check(//
"Integrate[Log[x]/(a + b*x)^2, x]", //
 "(x*Log[x])/(a*(a + b*x)) - Log[a + b*x]/(a*b)", //
2351, 31);}

// {2339, 30}
public void test0428() {
	check(//
"Integrate[Log[x]^n/x, x]", //
 "Log[x]^(1 + n)/(1 + n)", //
2339, 30);}

// {2339, 30}
public void test0429() {
	check(//
"Integrate[(a + b*Log[x])^n/x, x]", //
 "(a + b*Log[x])^(1 + n)/(b*(1 + n))", //
2339, 30);}

// {2339, 29}
public void test0430() {
	check(//
"Integrate[1/(x*(a + b*Log[x])), x]", //
 "Log[a + b*Log[x]]/b", //
2339, 29);}

// {2339, 30}
public void test0431() {
	check(//
"Integrate[1/(x*(a + b*Log[x])^n), x]", //
 "(a + b*Log[x])^(1 - n)/(b*(1 - n))", //
2339, 30);}

// {2634, 2718}
public void test0432() {
	check(//
"Integrate[Log[Cosh[x]]*Sinh[x], x]", //
 "-Cosh[x] + Cosh[x]*Log[Cosh[x]]", //
2634, 2718);}

// {4426, 2338}
public void test0433() {
	check(//
"Integrate[Log[Cosh[x]]*Tanh[x], x]", //
 "Log[Cosh[x]]^2/2", //
4426, 2338);}

// {2614, 267}
public void test0434() {
	check(//
"Integrate[Log[x - Sqrt[1 + x^2]], x]", //
 "Sqrt[1 + x^2] + x*Log[x - Sqrt[1 + x^2]]", //
2614, 267);}

// {4768}
public void test0435() {
	check(//
"Integrate[x*Sqrt[1 - x^2]*ArcCos[x], x]", //
 "-x/3 + x^3/9 - ((1 - x^2)^(3/2)*ArcCos[x])/3", //
4768);}

// {4767, 212}
public void test0436() {
	check(//
"Integrate[(x*ArcSin[x])/(1 - x^2)^(3/2), x]", //
 "ArcSin[x]/Sqrt[1 - x^2] - ArcTanh[x]", //
4767, 212);}

// {4768, 212}
public void test0437() {
	check(//
"Integrate[(x*ArcCos[x])/(1 - x^2)^(3/2), x]", //
 "ArcCos[x]/Sqrt[1 - x^2] + ArcTanh[x]", //
4768, 212);}

// {5054, 5004}
public void test0438() {
	check(//
"Integrate[(x^2*ArcTan[x])/(1 + x^2)^2, x]", //
 "-1/(4*(1 + x^2)) - (x*ArcTan[x])/(2*(1 + x^2)) + ArcTan[x]^2/4", //
5054, 5004);}

// {5346, 30}
public void test0439() {
	check(//
"Integrate[ArcSec[x]/(x^2*Sqrt[-1 + x^2]), x]", //
 "1/Sqrt[x^2] + (Sqrt[-1 + x^2]*ArcSec[x])/x", //
5346, 30);}

// {4918, 4737}
public void test0440() {
	check(//
"Integrate[ArcSin[Sqrt[1 - x^2]]/Sqrt[1 - x^2], x]", //
 "-(Sqrt[x^2]*ArcSin[Sqrt[1 - x^2]]^2)/(2*x)", //
4918, 4737);}

// {5315, 266}
public void test0441() {
	check(//
"Integrate[(x*ArcTan[Sqrt[1 + x^2]])/Sqrt[1 + x^2], x]", //
 "Sqrt[1 + x^2]*ArcTan[Sqrt[1 + x^2]] - Log[2 + x^2]/2", //
5315, 266);}

// {2157, 212}
public void test0442() {
	check(//
"Integrate[Sqrt[x^2 + Sqrt[1 + x^4]]/Sqrt[1 + x^4], x]", //
 "ArcTanh[(Sqrt[2]*x)/Sqrt[x^2 + Sqrt[1 + x^4]]]/Sqrt[2]", //
2157, 212);}

// {2157, 209}
public void test0443() {
	check(//
"Integrate[Sqrt[-x^2 + Sqrt[1 + x^4]]/Sqrt[1 + x^4], x]", //
 "ArcTan[(Sqrt[2]*x)/Sqrt[-x^2 + Sqrt[1 + x^4]]]/Sqrt[2]", //
2157, 209);}

// {6820}
public void test0444() {
	check(//
"Integrate[((-1 + x)^(3/2) + (1 + x)^(3/2))/((-1 + x)^(3/2)*(1 + x)^(3/2)), x]", //
 "-2/Sqrt[-1 + x] - 2/Sqrt[1 + x]", //
6820);}

// {2147, 30}
public void test0445() {
	check(//
"Integrate[(x + Sqrt[a + x^2])^b/Sqrt[a + x^2], x]", //
 "(x + Sqrt[a + x^2])^b/b", //
2147, 30);}

// {2147, 30}
public void test0446() {
	check(//
"Integrate[(x - Sqrt[a + x^2])^b/Sqrt[a + x^2], x]", //
 "-((x - Sqrt[a + x^2])^b/b)", //
2147, 30);}

// {2320, 267}
public void test0447() {
	check(//
"Integrate[(b/E^(p*x) + a*E^(p*x))^(-2), x]", //
 "-1/(2*a*(b + a*E^(2*p*x))*p)", //
2320, 267);}

// {2147, 30}
public void test0448() {
	check(//
"Integrate[Sqrt[x + Sqrt[a^2 + x^2]]/Sqrt[a^2 + x^2], x]", //
 "2*Sqrt[x + Sqrt[a^2 + x^2]]", //
2147, 30);}

// {2147, 30}
public void test0449() {
	check(//
"Integrate[Sqrt[b*x + Sqrt[a + b^2*x^2]]/Sqrt[a + b^2*x^2], x]", //
 "(2*Sqrt[b*x + Sqrt[a + b^2*x^2]])/b", //
2147, 30);}

// {2147, 30}
public void test0450() {
	check(//
"Integrate[(x + Sqrt[b + x^2])^a/Sqrt[b + x^2], x]", //
 "(x + Sqrt[b + x^2])^a/a", //
2147, 30);}

// {1608, 1761}
public void test0451() {
	check(//
"Integrate[(6 + 3*x^a + 2*x^(2*a))^a^(-1)*(x^a + x^(2*a) + x^(3*a)), x]", //
 "(x^(1 + a)*(6 + 3*x^a + 2*x^(2*a))^(1 + a^(-1)))/(6*(1 + a))", //
1608, 1761);}

// {2125, 2115}
public void test0452() {
	check(//
"Integrate[-((84 + 576*x + 400*x^2 - 2560*x^3)/(9 + 24*x - 12*x^2 + 80*x^3 + 320*x^4)), x]", //
 "2*Sqrt[11]*ArcTan[(7 - 40*x)/(5*Sqrt[11])] - 2*Sqrt[11]*ArcTan[(57 + 30*x - 40*x^2 + 800*x^3)/(6*Sqrt[11])] + 2*Log[9 + 24*x - 12*x^2 + 80*x^3 + 320*x^4]", //
2125, 2115);}

// {2162, 209}
public void test0453() {
	check(//
"Integrate[(1 - 2^(1/3)*x)/((2^(2/3) + x)*Sqrt[1 + x^3]), x]", //
 "(2*ArcTan[(Sqrt[3]*(1 + 2^(1/3)*x))/Sqrt[1 + x^3]])/Sqrt[3]", //
2162, 209);}

// {2163, 212}
public void test0454() {
	check(//
"Integrate[(1 + x)/((-2 + x)*Sqrt[1 + x^3]), x]", //
 "(-2*ArcTanh[(1 + x)^2/(3*Sqrt[1 + x^3])])/3", //
2163, 212);}

// {1754, 213}
public void test0455() {
	check(//
"Integrate[(1 - Sqrt[3] + x)/((1 + Sqrt[3] + x)*Sqrt[-4 + 4*Sqrt[3]*x^2 + x^4]), x]", //
 "(Sqrt[-3 + 2*Sqrt[3]]*ArcTanh[(1 - Sqrt[3] + x)^2/(Sqrt[3*(-3 + 2*Sqrt[3])]*Sqrt[-4 + 4*Sqrt[3]*x^2 + x^4])])/3", //
1754, 213);}

// {1754, 209}
public void test0456() {
	check(//
"Integrate[(1 + Sqrt[3] + x)/((1 - Sqrt[3] + x)*Sqrt[-4 - 4*Sqrt[3]*x^2 + x^4]), x]", //
 "-(Sqrt[3 + 2*Sqrt[3]]*ArcTan[(1 + Sqrt[3] + x)^2/(Sqrt[3*(3 + 2*Sqrt[3])]*Sqrt[-4 - 4*Sqrt[3]*x^2 + x^4])])/3", //
1754, 209);}

// {45}
public void test0457() {
	check(//
"Integrate[(-5 + 3*x)^2/(-1 + 2*x)^(7/2), x]", //
 "-49/(20*(-1 + 2*x)^(5/2)) + 7/(2*(-1 + 2*x)^(3/2)) - 9/(4*Sqrt[-1 + 2*x])", //
45);}

// {2320, 213}
public void test0458() {
	check(//
"Integrate[(-5/E^(m*x) + 2*E^(m*x))^(-1), x]", //
 "-(ArcTanh[Sqrt[2/5]*E^(m*x)]/(Sqrt[10]*m))", //
2320, 213);}

// {2738, 211}
public void test0459() {
	check(//
"Integrate[(a + b*Cos[x])^(-1), x]", //
 "(2*ArcTan[(Sqrt[a - b]*Tan[x/2])/Sqrt[a + b]])/(Sqrt[a - b]*Sqrt[a + b])", //
2738, 211);}

// {3203, 31}
public void test0460() {
	check(//
"Integrate[(3 + 3*Cos[x] + 4*Sin[x])^(-1), x]", //
 "Log[3 + 4*Tan[x/2]]/4", //
3203, 31);}

// {3202, 31}
public void test0461() {
	check(//
"Integrate[(4 + 3*Cos[x] + 4*Sin[x])^(-1), x]", //
 "-Log[4 + 3*Cot[Pi/4 + x/2]]/3", //
3202, 31);}

}
