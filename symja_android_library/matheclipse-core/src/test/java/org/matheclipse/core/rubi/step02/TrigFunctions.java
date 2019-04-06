package org.matheclipse.core.rubi.step02;

public class TrigFunctions extends AbstractRubiTestCase {

public TrigFunctions(String name) { super(name, false); }

// {2715, 8}
public void test0001() {
	check(//
"Integrate[Sin[a + b*x]^2, x]", //
 "x/2 - (Cos[a + b*x]*Sin[a + b*x])/(2*b)", //
2715, 8);}

// {2713}
public void test0002() {
	check(//
"Integrate[Sin[a + b*x]^3, x]", //
 "-(Cos[a + b*x]/b) + Cos[a + b*x]^3/(3*b)", //
2713);}

// {2713}
public void test0003() {
	check(//
"Integrate[Sin[a + b*x]^5, x]", //
 "-(Cos[a + b*x]/b) + (2*Cos[a + b*x]^3)/(3*b) - Cos[a + b*x]^5/(5*b)", //
2713);}

// {2713}
public void test0004() {
	check(//
"Integrate[Sin[a + b*x]^7, x]", //
 "-(Cos[a + b*x]/b) + Cos[a + b*x]^3/b - (3*Cos[a + b*x]^5)/(5*b) + Cos[a + b*x]^7/(7*b)", //
2713);}

// {2715, 2719}
public void test0005() {
	check(//
"Integrate[Sin[b*x]^(5/2), x]", //
 "(-6*EllipticE[Pi/4 - (b*x)/2, 2])/(5*b) - (2*Cos[b*x]*Sin[b*x]^(3/2))/(5*b)", //
2715, 2719);}

// {2715, 2720}
public void test0006() {
	check(//
"Integrate[Sin[b*x]^(3/2), x]", //
 "(-2*EllipticF[Pi/4 - (b*x)/2, 2])/(3*b) - (2*Cos[b*x]*Sqrt[Sin[b*x]])/(3*b)", //
2715, 2720);}

// {2716, 2719}
public void test0007() {
	check(//
"Integrate[Sin[b*x]^(-3/2), x]", //
 "(2*EllipticE[Pi/4 - (b*x)/2, 2])/b - (2*Cos[b*x])/(b*Sqrt[Sin[b*x]])", //
2716, 2719);}

// {2716, 2720}
public void test0008() {
	check(//
"Integrate[Sin[b*x]^(-5/2), x]", //
 "(-2*EllipticF[Pi/4 - (b*x)/2, 2])/(3*b) - (2*Cos[b*x])/(3*b*Sin[b*x]^(3/2))", //
2716, 2720);}

// {2715, 2719}
public void test0009() {
	check(//
"Integrate[Sin[a + b*x]^(5/2), x]", //
 "(6*EllipticE[(a - Pi/2 + b*x)/2, 2])/(5*b) - (2*Cos[a + b*x]*Sin[a + b*x]^(3/2))/(5*b)", //
2715, 2719);}

// {2715, 2720}
public void test0010() {
	check(//
"Integrate[Sin[a + b*x]^(3/2), x]", //
 "(2*EllipticF[(a - Pi/2 + b*x)/2, 2])/(3*b) - (2*Cos[a + b*x]*Sqrt[Sin[a + b*x]])/(3*b)", //
2715, 2720);}

// {2716, 2719}
public void test0011() {
	check(//
"Integrate[Sin[a + b*x]^(-3/2), x]", //
 "(-2*EllipticE[(a - Pi/2 + b*x)/2, 2])/b - (2*Cos[a + b*x])/(b*Sqrt[Sin[a + b*x]])", //
2716, 2719);}

// {2716, 2720}
public void test0012() {
	check(//
"Integrate[Sin[a + b*x]^(-5/2), x]", //
 "(2*EllipticF[(a - Pi/2 + b*x)/2, 2])/(3*b) - (2*Cos[a + b*x])/(3*b*Sin[a + b*x]^(3/2))", //
2716, 2720);}

// {2721, 2719}
public void test0013() {
	check(//
"Integrate[Sqrt[c*Sin[a + b*x]], x]", //
 "(2*EllipticE[(a - Pi/2 + b*x)/2, 2]*Sqrt[c*Sin[a + b*x]])/(b*Sqrt[Sin[a + b*x]])", //
2721, 2719);}

// {2721, 2720}
public void test0014() {
	check(//
"Integrate[1/Sqrt[c*Sin[a + b*x]], x]", //
 "(2*EllipticF[(a - Pi/2 + b*x)/2, 2]*Sqrt[Sin[a + b*x]])/(b*Sqrt[c*Sin[a + b*x]])", //
2721, 2720);}

// {20, 2722}
public void test0015() {
	check(//
"Integrate[(a*Sin[e + f*x])^m*(b*Sin[e + f*x])^n, x]", //
 "(Cos[e + f*x]*Hypergeometric2F1[1/2, (1 + m + n)/2, (3 + m + n)/2, Sin[e + f*x]^2]*(a*Sin[e + f*x])^(1 + m)*(b*Sin[e + f*x])^n)/(a*f*(1 + m + n)*Sqrt[Cos[e + f*x]^2])", //
20, 2722);}

// {2645, 30}
public void test0016() {
	check(//
"Integrate[Cos[a + b*x]^3*Sin[a + b*x], x]", //
 "-Cos[a + b*x]^4/(4*b)", //
2645, 30);}

// {2645, 30}
public void test0017() {
	check(//
"Integrate[Cos[a + b*x]^2*Sin[a + b*x], x]", //
 "-Cos[a + b*x]^3/(3*b)", //
2645, 30);}

// {2644, 30}
public void test0018() {
	check(//
"Integrate[Cos[a + b*x]*Sin[a + b*x], x]", //
 "Sin[a + b*x]^2/(2*b)", //
2644, 30);}

// {2686, 8}
public void test0019() {
	check(//
"Integrate[Sec[a + b*x]*Tan[a + b*x], x]", //
 "Sec[a + b*x]/b", //
2686, 8);}

// {2686, 30}
public void test0020() {
	check(//
"Integrate[Sec[a + b*x]^2*Tan[a + b*x], x]", //
 "Sec[a + b*x]^2/(2*b)", //
2686, 30);}

// {2686, 30}
public void test0021() {
	check(//
"Integrate[Sec[a + b*x]^3*Tan[a + b*x], x]", //
 "Sec[a + b*x]^3/(3*b)", //
2686, 30);}

// {2644, 30}
public void test0022() {
	check(//
"Integrate[Cos[a + b*x]*Sin[a + b*x]^2, x]", //
 "Sin[a + b*x]^3/(3*b)", //
2644, 30);}

// {3554, 8}
public void test0023() {
	check(//
"Integrate[Tan[a + b*x]^2, x]", //
 "-x + Tan[a + b*x]/b", //
3554, 8);}

// {2687, 30}
public void test0024() {
	check(//
"Integrate[Sec[a + b*x]^2*Tan[a + b*x]^2, x]", //
 "Tan[a + b*x]^3/(3*b)", //
2687, 30);}

// {2715, 8}
public void test0025() {
	check(//
"Integrate[Sin[a + b*x]^2, x]", //
 "x/2 - (Cos[a + b*x]*Sin[a + b*x])/(2*b)", //
2715, 8);}

// {2691, 3855}
public void test0026() {
	check(//
"Integrate[Sec[a + b*x]*Tan[a + b*x]^2, x]", //
 "-ArcTanh[Sin[a + b*x]]/(2*b) + (Sec[a + b*x]*Tan[a + b*x])/(2*b)", //
2691, 3855);}

// {2644, 30}
public void test0027() {
	check(//
"Integrate[Cos[a + b*x]*Sin[a + b*x]^3, x]", //
 "Sin[a + b*x]^4/(4*b)", //
2644, 30);}

// {3554, 3556}
public void test0028() {
	check(//
"Integrate[Tan[a + b*x]^3, x]", //
 "Log[Cos[a + b*x]]/b + Tan[a + b*x]^2/(2*b)", //
3554, 3556);}

// {2686}
public void test0029() {
	check(//
"Integrate[Sec[a + b*x]*Tan[a + b*x]^3, x]", //
 "-(Sec[a + b*x]/b) + Sec[a + b*x]^3/(3*b)", //
2686);}

// {2687, 30}
public void test0030() {
	check(//
"Integrate[Sec[a + b*x]^2*Tan[a + b*x]^3, x]", //
 "Tan[a + b*x]^4/(4*b)", //
2687, 30);}

// {2644, 30}
public void test0031() {
	check(//
"Integrate[Cos[a + b*x]*Sin[a + b*x]^4, x]", //
 "Sin[a + b*x]^5/(5*b)", //
2644, 30);}

// {2687, 30}
public void test0032() {
	check(//
"Integrate[Sec[a + b*x]^2*Tan[a + b*x]^4, x]", //
 "Tan[a + b*x]^5/(5*b)", //
2687, 30);}

// {2644, 30}
public void test0033() {
	check(//
"Integrate[Cos[a + b*x]*Sin[a + b*x]^5, x]", //
 "Sin[a + b*x]^6/(6*b)", //
2644, 30);}

// {2687, 30}
public void test0034() {
	check(//
"Integrate[Sec[a + b*x]^2*Tan[a + b*x]^5, x]", //
 "Tan[a + b*x]^6/(6*b)", //
2687, 30);}

// {2700, 29}
public void test0035() {
	check(//
"Integrate[Csc[a + b*x]*Sec[a + b*x], x]", //
 "Log[Tan[a + b*x]]/b", //
2700, 29);}

// {3554, 8}
public void test0036() {
	check(//
"Integrate[Cot[a + b*x]^2, x]", //
 "-x - Cot[a + b*x]/b", //
3554, 8);}

// {2686, 8}
public void test0037() {
	check(//
"Integrate[Cot[a + b*x]*Csc[a + b*x], x]", //
 "-(Csc[a + b*x]/b)", //
2686, 8);}

// {3554, 3556}
public void test0038() {
	check(//
"Integrate[Cot[a + b*x]^3, x]", //
 "-Cot[a + b*x]^2/(2*b) - Log[Sin[a + b*x]]/b", //
3554, 3556);}

// {2691, 3855}
public void test0039() {
	check(//
"Integrate[Cot[a + b*x]^2*Csc[a + b*x], x]", //
 "ArcTanh[Cos[a + b*x]]/(2*b) - (Cot[a + b*x]*Csc[a + b*x])/(2*b)", //
2691, 3855);}

// {2686, 30}
public void test0040() {
	check(//
"Integrate[Cot[a + b*x]*Csc[a + b*x]^2, x]", //
 "-Csc[a + b*x]^2/(2*b)", //
2686, 30);}

// {2686}
public void test0041() {
	check(//
"Integrate[Cot[a + b*x]^3*Csc[a + b*x], x]", //
 "Csc[a + b*x]/b - Csc[a + b*x]^3/(3*b)", //
2686);}

// {2687, 30}
public void test0042() {
	check(//
"Integrate[Cot[a + b*x]^2*Csc[a + b*x]^2, x]", //
 "-Cot[a + b*x]^3/(3*b)", //
2687, 30);}

// {2686, 30}
public void test0043() {
	check(//
"Integrate[Cot[a + b*x]*Csc[a + b*x]^3, x]", //
 "-Csc[a + b*x]^3/(3*b)", //
2686, 30);}

// {2687, 30}
public void test0044() {
	check(//
"Integrate[Cot[a + b*x]^3*Csc[a + b*x]^2, x]", //
 "-Cot[a + b*x]^4/(4*b)", //
2687, 30);}

// {2686, 30}
public void test0045() {
	check(//
"Integrate[Cot[a + b*x]*Csc[a + b*x]^4, x]", //
 "-Csc[a + b*x]^4/(4*b)", //
2686, 30);}

// {2645, 30}
public void test0046() {
	check(//
"Integrate[(d*Cos[a + b*x])^(3/2)*Sin[a + b*x], x]", //
 "(-2*(d*Cos[a + b*x])^(5/2))/(5*b*d)", //
2645, 30);}

// {2645, 30}
public void test0047() {
	check(//
"Integrate[Sqrt[d*Cos[a + b*x]]*Sin[a + b*x], x]", //
 "(-2*(d*Cos[a + b*x])^(3/2))/(3*b*d)", //
2645, 30);}

// {2645, 30}
public void test0048() {
	check(//
"Integrate[Sin[a + b*x]/Sqrt[d*Cos[a + b*x]], x]", //
 "(-2*Sqrt[d*Cos[a + b*x]])/(b*d)", //
2645, 30);}

// {2645, 30}
public void test0049() {
	check(//
"Integrate[Sin[a + b*x]/(d*Cos[a + b*x])^(3/2), x]", //
 "2/(b*d*Sqrt[d*Cos[a + b*x]])", //
2645, 30);}

// {2645, 30}
public void test0050() {
	check(//
"Integrate[Sin[a + b*x]/(d*Cos[a + b*x])^(5/2), x]", //
 "2/(3*b*d*(d*Cos[a + b*x])^(3/2))", //
2645, 30);}

// {2645, 30}
public void test0051() {
	check(//
"Integrate[Sin[a + b*x]/(d*Cos[a + b*x])^(7/2), x]", //
 "2/(5*b*d*(d*Cos[a + b*x])^(5/2))", //
2645, 30);}

// {2645, 30}
public void test0052() {
	check(//
"Integrate[Sin[a + b*x]/(d*Cos[a + b*x])^(9/2), x]", //
 "2/(7*b*d*(d*Cos[a + b*x])^(7/2))", //
2645, 30);}

// {2645, 30}
public void test0053() {
	check(//
"Integrate[(d*Cos[a + b*x])^(1/5)*Sin[a + b*x], x]", //
 "(-5*(d*Cos[a + b*x])^(6/5))/(6*b*d)", //
2645, 30);}

// {2652, 2719}
public void test0054() {
	check(//
"Integrate[Sqrt[d*Cos[a + b*x]]*Sqrt[c*Sin[a + b*x]], x]", //
 "(Sqrt[d*Cos[a + b*x]]*EllipticE[a - Pi/4 + b*x, 2]*Sqrt[c*Sin[a + b*x]])/(b*Sqrt[Sin[2*a + 2*b*x]])", //
2652, 2719);}

// {2651, 2643}
public void test0055() {
	check(//
"Integrate[Sqrt[c*Sin[a + b*x]]/(d*Cos[a + b*x])^(9/2), x]", //
 "(2*(c*Sin[a + b*x])^(3/2))/(7*b*c*d*(d*Cos[a + b*x])^(7/2)) + (8*(c*Sin[a + b*x])^(3/2))/(21*b*c*d^3*(d*Cos[a + b*x])^(3/2))", //
2651, 2643);}

// {2653, 2720}
public void test0056() {
	check(//
"Integrate[1/(Sqrt[d*Cos[a + b*x]]*Sqrt[c*Sin[a + b*x]]), x]", //
 "(EllipticF[a - Pi/4 + b*x, 2]*Sqrt[Sin[2*a + 2*b*x]])/(b*Sqrt[d*Cos[a + b*x]]*Sqrt[c*Sin[a + b*x]])", //
2653, 2720);}

// {2651, 2643}
public void test0057() {
	check(//
"Integrate[1/((d*Cos[a + b*x])^(7/2)*Sqrt[c*Sin[a + b*x]]), x]", //
 "(2*Sqrt[c*Sin[a + b*x]])/(5*b*c*d*(d*Cos[a + b*x])^(5/2)) + (8*Sqrt[c*Sin[a + b*x]])/(5*b*c*d^3*Sqrt[d*Cos[a + b*x]])", //
2651, 2643);}

// {2644, 30}
public void test0058() {
	check(//
"Integrate[Cos[a + b*x]*(c*Sin[a + b*x])^m, x]", //
 "(c*Sin[a + b*x])^(1 + m)/(b*c*(1 + m))", //
2644, 30);}

// {2644, 371}
public void test0059() {
	check(//
"Integrate[Sec[a + b*x]*(c*Sin[a + b*x])^m, x]", //
 "(Hypergeometric2F1[1, (1 + m)/2, (3 + m)/2, Sin[a + b*x]^2]*(c*Sin[a + b*x])^(1 + m))/(b*c*(1 + m))", //
2644, 371);}

// {2644, 371}
public void test0060() {
	check(//
"Integrate[Sec[a + b*x]^3*(c*Sin[a + b*x])^m, x]", //
 "(Hypergeometric2F1[2, (1 + m)/2, (3 + m)/2, Sin[a + b*x]^2]*(c*Sin[a + b*x])^(1 + m))/(b*c*(1 + m))", //
2644, 371);}

// {2645, 30}
public void test0061() {
	check(//
"Integrate[(d*Cos[a + b*x])^n*Sin[a + b*x], x]", //
 "-((d*Cos[a + b*x])^(1 + n)/(b*d*(1 + n)))", //
2645, 30);}

// {2645, 371}
public void test0062() {
	check(//
"Integrate[(d*Cos[a + b*x])^n*Csc[a + b*x], x]", //
 "-(((d*Cos[a + b*x])^(1 + n)*Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, Cos[a + b*x]^2])/(b*d*(1 + n)))", //
2645, 371);}

// {2645, 371}
public void test0063() {
	check(//
"Integrate[(d*Cos[a + b*x])^n*Csc[a + b*x]^3, x]", //
 "-(((d*Cos[a + b*x])^(1 + n)*Hypergeometric2F1[2, (1 + n)/2, (3 + n)/2, Cos[a + b*x]^2])/(b*d*(1 + n)))", //
2645, 371);}

// {2645, 371}
public void test0064() {
	check(//
"Integrate[(d*Cos[a + b*x])^n*Csc[a + b*x]^5, x]", //
 "-(((d*Cos[a + b*x])^(1 + n)*Hypergeometric2F1[3, (1 + n)/2, (3 + n)/2, Cos[a + b*x]^2])/(b*d*(1 + n)))", //
2645, 371);}

// {2702, 30}
public void test0065() {
	check(//
"Integrate[Sqrt[b*Sec[e + f*x]]*Sin[e + f*x], x]", //
 "(-2*b)/(f*Sqrt[b*Sec[e + f*x]])", //
2702, 30);}

// {3856, 2720}
public void test0066() {
	check(//
"Integrate[Sqrt[b*Sec[e + f*x]], x]", //
 "(2*Sqrt[Cos[e + f*x]]*EllipticF[(e + f*x)/2, 2]*Sqrt[b*Sec[e + f*x]])/f", //
3856, 2720);}

// {2702, 30}
public void test0067() {
	check(//
"Integrate[(b*Sec[e + f*x])^(3/2)*Sin[e + f*x], x]", //
 "(2*b*Sqrt[b*Sec[e + f*x]])/f", //
2702, 30);}

// {2702, 30}
public void test0068() {
	check(//
"Integrate[(b*Sec[e + f*x])^(5/2)*Sin[e + f*x], x]", //
 "(2*b*(b*Sec[e + f*x])^(3/2))/(3*f)", //
2702, 30);}

// {2702, 30}
public void test0069() {
	check(//
"Integrate[Sin[e + f*x]/Sqrt[b*Sec[e + f*x]], x]", //
 "(-2*b)/(3*f*(b*Sec[e + f*x])^(3/2))", //
2702, 30);}

// {3856, 2719}
public void test0070() {
	check(//
"Integrate[1/Sqrt[b*Sec[e + f*x]], x]", //
 "(2*EllipticE[(e + f*x)/2, 2])/(f*Sqrt[Cos[e + f*x]]*Sqrt[b*Sec[e + f*x]])", //
3856, 2719);}

// {2702, 30}
public void test0071() {
	check(//
"Integrate[Sin[e + f*x]/(b*Sec[e + f*x])^(3/2), x]", //
 "(-2*b)/(5*f*(b*Sec[e + f*x])^(5/2))", //
2702, 30);}

// {2702, 30}
public void test0072() {
	check(//
"Integrate[Sin[e + f*x]/(b*Sec[e + f*x])^(5/2), x]", //
 "(-2*b)/(7*f*(b*Sec[e + f*x])^(7/2))", //
2702, 30);}

// {2664, 2658}
public void test0073() {
	check(//
"Integrate[Sqrt[b*Sec[e + f*x]]/(a*Sin[e + f*x])^(7/2), x]", //
 "(-2*b)/(5*a*f*Sqrt[b*Sec[e + f*x]]*(a*Sin[e + f*x])^(5/2)) - (8*b)/(5*a^3*f*Sqrt[b*Sec[e + f*x]]*Sqrt[a*Sin[e + f*x]])", //
2664, 2658);}

// {2664, 2658}
public void test0074() {
	check(//
"Integrate[1/(Sqrt[b*Sec[e + f*x]]*Sin[e + f*x]^(9/2)), x]", //
 "(-2*b)/(7*f*(b*Sec[e + f*x])^(3/2)*Sin[e + f*x]^(7/2)) - (8*b)/(21*f*(b*Sec[e + f*x])^(3/2)*Sin[e + f*x]^(3/2))", //
2664, 2658);}

// {2667, 2657}
public void test0075() {
	check(//
"Integrate[(d*Sec[a + b*x])^(5/2)*(c*Sin[a + b*x])^m, x]", //
 "(d*(Cos[a + b*x]^2)^(3/4)*Hypergeometric2F1[7/4, (1 + m)/2, (3 + m)/2, Sin[a + b*x]^2]*(d*Sec[a + b*x])^(3/2)*(c*Sin[a + b*x])^(1 + m))/(b*c*(1 + m))", //
2667, 2657);}

// {2667, 2657}
public void test0076() {
	check(//
"Integrate[(d*Sec[a + b*x])^(3/2)*(c*Sin[a + b*x])^m, x]", //
 "(d*(Cos[a + b*x]^2)^(1/4)*Hypergeometric2F1[5/4, (1 + m)/2, (3 + m)/2, Sin[a + b*x]^2]*Sqrt[d*Sec[a + b*x]]*(c*Sin[a + b*x])^(1 + m))/(b*c*(1 + m))", //
2667, 2657);}

// {2666, 2657}
public void test0077() {
	check(//
"Integrate[Sqrt[d*Sec[a + b*x]]*(c*Sin[a + b*x])^m, x]", //
 "((Cos[a + b*x]^2)^(3/4)*Hypergeometric2F1[3/4, (1 + m)/2, (3 + m)/2, Sin[a + b*x]^2]*(d*Sec[a + b*x])^(3/2)*(c*Sin[a + b*x])^(1 + m))/(b*c*d*(1 + m))", //
2666, 2657);}

// {2666, 2657}
public void test0078() {
	check(//
"Integrate[(c*Sin[a + b*x])^m/Sqrt[d*Sec[a + b*x]], x]", //
 "((Cos[a + b*x]^2)^(1/4)*Hypergeometric2F1[1/4, (1 + m)/2, (3 + m)/2, Sin[a + b*x]^2]*Sqrt[d*Sec[a + b*x]]*(c*Sin[a + b*x])^(1 + m))/(b*c*d*(1 + m))", //
2666, 2657);}

// {2666, 2657}
public void test0079() {
	check(//
"Integrate[(c*Sin[a + b*x])^m/(d*Sec[a + b*x])^(3/2), x]", //
 "(Hypergeometric2F1[-1/4, (1 + m)/2, (3 + m)/2, Sin[a + b*x]^2]*(c*Sin[a + b*x])^(1 + m))/(b*c*d*(1 + m)*(Cos[a + b*x]^2)^(1/4)*Sqrt[d*Sec[a + b*x]])", //
2666, 2657);}

// {2667, 2656}
public void test0080() {
	check(//
"Integrate[Sec[e + f*x]^n*Sin[e + f*x]^m, x]", //
 "-((Hypergeometric2F1[(1 - m)/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*Sec[e + f*x]^(-1 + n)*Sin[e + f*x]^(-1 + m)*(Sin[e + f*x]^2)^((1 - m)/2))/(f*(1 - n)))", //
2667, 2656);}

// {2667, 2656}
public void test0081() {
	check(//
"Integrate[Sec[e + f*x]^n*(a*Sin[e + f*x])^m, x]", //
 "-((a*Hypergeometric2F1[(1 - m)/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*Sec[e + f*x]^(-1 + n)*(a*Sin[e + f*x])^(-1 + m)*(Sin[e + f*x]^2)^((1 - m)/2))/(f*(1 - n)))", //
2667, 2656);}

// {2667, 2656}
public void test0082() {
	check(//
"Integrate[(b*Sec[e + f*x])^n*Sin[e + f*x]^m, x]", //
 "-((b*Hypergeometric2F1[(1 - m)/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Sec[e + f*x])^(-1 + n)*Sin[e + f*x]^(-1 + m)*(Sin[e + f*x]^2)^((1 - m)/2))/(f*(1 - n)))", //
2667, 2656);}

// {2667, 2656}
public void test0083() {
	check(//
"Integrate[(b*Sec[e + f*x])^n*(a*Sin[e + f*x])^m, x]", //
 "-((a*b*Hypergeometric2F1[(1 - m)/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Sec[e + f*x])^(-1 + n)*(a*Sin[e + f*x])^(-1 + m)*(Sin[e + f*x]^2)^((1 - m)/2))/(f*(1 - n)))", //
2667, 2656);}

// {2702, 30}
public void test0084() {
	check(//
"Integrate[(b*Sec[e + f*x])^n*Sin[e + f*x], x]", //
 "-((b*(b*Sec[e + f*x])^(-1 + n))/(f*(1 - n)))", //
2702, 30);}

// {2702, 371}
public void test0085() {
	check(//
"Integrate[Csc[e + f*x]*(b*Sec[e + f*x])^n, x]", //
 "-((Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, Sec[e + f*x]^2]*(b*Sec[e + f*x])^(1 + n))/(b*f*(1 + n)))", //
2702, 371);}

// {2702, 371}
public void test0086() {
	check(//
"Integrate[Csc[e + f*x]^3*(b*Sec[e + f*x])^n, x]", //
 "(Hypergeometric2F1[2, (3 + n)/2, (5 + n)/2, Sec[e + f*x]^2]*(b*Sec[e + f*x])^(3 + n))/(b^3*f*(3 + n))", //
2702, 371);}

// {2712, 2656}
public void test0087() {
	check(//
"Integrate[(b*Sec[e + f*x])^n*Sin[e + f*x]^6, x]", //
 "-((b*Hypergeometric2F1[-5/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Sec[e + f*x])^(-1 + n)*Sin[e + f*x])/(f*(1 - n)*Sqrt[Sin[e + f*x]^2]))", //
2712, 2656);}

// {2712, 2656}
public void test0088() {
	check(//
"Integrate[(b*Sec[e + f*x])^n*Sin[e + f*x]^4, x]", //
 "-((b*Hypergeometric2F1[-3/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Sec[e + f*x])^(-1 + n)*Sin[e + f*x])/(f*(1 - n)*Sqrt[Sin[e + f*x]^2]))", //
2712, 2656);}

// {2712, 2656}
public void test0089() {
	check(//
"Integrate[(b*Sec[e + f*x])^n*Sin[e + f*x]^2, x]", //
 "-((b*Hypergeometric2F1[-1/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Sec[e + f*x])^(-1 + n)*Sin[e + f*x])/(f*(1 - n)*Sqrt[Sin[e + f*x]^2]))", //
2712, 2656);}

// {3857, 2722}
public void test0090() {
	check(//
"Integrate[(b*Sec[e + f*x])^n, x]", //
 "-((Cos[e + f*x]*Hypergeometric2F1[1/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Sec[e + f*x])^n*Sin[e + f*x])/(f*(1 - n)*Sqrt[Sin[e + f*x]^2]))", //
3857, 2722);}

// {2712, 2656}
public void test0091() {
	check(//
"Integrate[Csc[e + f*x]^2*(b*Sec[e + f*x])^n, x]", //
 "-((b*Csc[e + f*x]*Hypergeometric2F1[3/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Sec[e + f*x])^(-1 + n)*Sqrt[Sin[e + f*x]^2])/(f*(1 - n)))", //
2712, 2656);}

// {2712, 2656}
public void test0092() {
	check(//
"Integrate[Csc[e + f*x]^4*(b*Sec[e + f*x])^n, x]", //
 "-((b*Csc[e + f*x]*Hypergeometric2F1[5/2, (1 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Sec[e + f*x])^(-1 + n)*Sqrt[Sin[e + f*x]^2])/(f*(1 - n)))", //
2712, 2656);}

// {2667, 2656}
public void test0093() {
	check(//
"Integrate[(b*Sec[a + b*x])^n*(c*Sin[a + b*x])^(3/2), x]", //
 "-((c*Hypergeometric2F1[-1/4, (1 - n)/2, (3 - n)/2, Cos[a + b*x]^2]*(b*Sec[a + b*x])^(-1 + n)*Sqrt[c*Sin[a + b*x]])/((1 - n)*(Sin[a + b*x]^2)^(1/4)))", //
2667, 2656);}

// {2667, 2656}
public void test0094() {
	check(//
"Integrate[(b*Sec[a + b*x])^n*Sqrt[c*Sin[a + b*x]], x]", //
 "-((c*Hypergeometric2F1[1/4, (1 - n)/2, (3 - n)/2, Cos[a + b*x]^2]*(b*Sec[a + b*x])^(-1 + n)*(Sin[a + b*x]^2)^(1/4))/((1 - n)*Sqrt[c*Sin[a + b*x]]))", //
2667, 2656);}

// {2667, 2656}
public void test0095() {
	check(//
"Integrate[(b*Sec[a + b*x])^n/Sqrt[c*Sin[a + b*x]], x]", //
 "-((c*Hypergeometric2F1[3/4, (1 - n)/2, (3 - n)/2, Cos[a + b*x]^2]*(b*Sec[a + b*x])^(-1 + n)*(Sin[a + b*x]^2)^(3/4))/((1 - n)*(c*Sin[a + b*x])^(3/2)))", //
2667, 2656);}

// {2667, 2656}
public void test0096() {
	check(//
"Integrate[(b*Sec[a + b*x])^n/(c*Sin[a + b*x])^(3/2), x]", //
 "-((Hypergeometric2F1[5/4, (1 - n)/2, (3 - n)/2, Cos[a + b*x]^2]*(b*Sec[a + b*x])^(-1 + n)*(Sin[a + b*x]^2)^(1/4))/(c*(1 - n)*Sqrt[c*Sin[a + b*x]]))", //
2667, 2656);}

// {3856, 2720}
public void test0097() {
	check(//
"Integrate[Sqrt[d*Csc[e + f*x]], x]", //
 "(2*Sqrt[d*Csc[e + f*x]]*EllipticF[(e - Pi/2 + f*x)/2, 2]*Sqrt[Sin[e + f*x]])/f", //
3856, 2720);}

// {3856, 2719}
public void test0098() {
	check(//
"Integrate[1/Sqrt[d*Csc[e + f*x]], x]", //
 "(2*EllipticE[(e - Pi/2 + f*x)/2, 2])/(f*Sqrt[d*Csc[e + f*x]]*Sqrt[Sin[e + f*x]])", //
3856, 2719);}

// {2668, 2722}
public void test0099() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*(a*Sin[e + f*x])^m, x]", //
 "(Cos[e + f*x]*(b*Csc[e + f*x])^n*Hypergeometric2F1[1/2, (1 + m - n)/2, (3 + m - n)/2, Sin[e + f*x]^2]*(a*Sin[e + f*x])^(1 + m))/(a*f*(1 + m - n)*Sqrt[Cos[e + f*x]^2])", //
2668, 2722);}

// {3377, 2717}
public void test0100() {
	check(//
"Integrate[(c + d*x)*Sin[a + b*x], x]", //
 "-(((c + d*x)*Cos[a + b*x])/b) + (d*Sin[a + b*x])/b^2", //
3377, 2717);}

// {3391}
public void test0101() {
	check(//
"Integrate[(c + d*x)*Sin[a + b*x]^2, x]", //
 "(c*x)/2 + (d*x^2)/4 - ((c + d*x)*Cos[a + b*x]*Sin[a + b*x])/(2*b) + (d*Sin[a + b*x]^2)/(4*b^2)", //
3391);}

// {4269, 3556}
public void test0102() {
	check(//
"Integrate[(c + d*x)*Csc[a + b*x]^2, x]", //
 "-(((c + d*x)*Cot[a + b*x])/b) + (d*Log[Sin[a + b*x]])/b^2", //
4269, 3556);}

// {3386, 3432}
public void test0103() {
	check(//
"Integrate[Sin[f*x]/Sqrt[d*x], x]", //
 "(Sqrt[2*Pi]*FresnelS[(Sqrt[f]*Sqrt[2/Pi]*Sqrt[d*x])/Sqrt[d]])/(Sqrt[d]*Sqrt[f])", //
3386, 3432);}

// {3396}
public void test0104() {
	check(//
"Integrate[x/Sin[e + f*x]^(3/2) + x*Sqrt[Sin[e + f*x]], x]", //
 "(-2*x*Cos[e + f*x])/(f*Sqrt[Sin[e + f*x]]) + (4*Sqrt[Sin[e + f*x]])/f^2", //
3396);}

// {3396}
public void test0105() {
	check(//
"Integrate[x/Sin[e + f*x]^(5/2) - x/(3*Sqrt[Sin[e + f*x]]), x]", //
 "(-2*x*Cos[e + f*x])/(3*f*Sin[e + f*x]^(3/2)) - 4/(3*f^2*Sqrt[Sin[e + f*x]])", //
3396);}

// {2814, 2727}
public void test0106() {
	check(//
"Integrate[Sin[c + d*x]/(a + a*Sin[c + d*x]), x]", //
 "x/a + Cos[c + d*x]/(d*(a + a*Sin[c + d*x]))", //
2814, 2727);}

// {2846, 2813}
public void test0107() {
	check(//
"Integrate[Sin[c + d*x]^3/(a + a*Sin[c + d*x]), x]", //
 "(3*x)/(2*a) + (2*Cos[c + d*x])/(a*d) - (3*Cos[c + d*x]*Sin[c + d*x])/(2*a*d) + (Cos[c + d*x]*Sin[c + d*x]^2)/(d*(a + a*Sin[c + d*x]))", //
2846, 2813);}

// {2746, 31}
public void test0108() {
	check(//
"Integrate[Cos[c + d*x]/(a + a*Sin[c + d*x]), x]", //
 "Log[1 + Sin[c + d*x]]/(a*d)", //
2746, 31);}

// {2761, 8}
public void test0109() {
	check(//
"Integrate[Cos[c + d*x]^2/(a + a*Sin[c + d*x]), x]", //
 "x/a + Cos[c + d*x]/(a*d)", //
2761, 8);}

// {2746}
public void test0110() {
	check(//
"Integrate[Cos[c + d*x]^3/(a + a*Sin[c + d*x]), x]", //
 "Sin[c + d*x]/(a*d) - Sin[c + d*x]^2/(2*a*d)", //
2746);}

// {2747, 31}
public void test0111() {
	check(//
"Integrate[Cos[c + d*x]/(a + b*Sin[c + d*x]), x]", //
 "Log[a + b*Sin[c + d*x]]/(b*d)", //
2747, 31);}

// {2726, 2725}
public void test0112() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(3/2), x]", //
 "(-8*a^2*Cos[c + d*x])/(3*d*Sqrt[a + a*Sin[c + d*x]]) - (2*a*Cos[c + d*x]*Sqrt[a + a*Sin[c + d*x]])/(3*d)", //
2726, 2725);}

// {2728, 212}
public void test0113() {
	check(//
"Integrate[1/Sqrt[a + a*Sin[c + d*x]], x]", //
 "-((Sqrt[2]*ArcTanh[(Sqrt[a]*Cos[c + d*x])/(Sqrt[2]*Sqrt[a + a*Sin[c + d*x]])])/(Sqrt[a]*d))", //
2728, 212);}

// {2731, 2730}
public void test0114() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(4/3), x]", //
 "(-2*2^(5/6)*a*Cos[c + d*x]*Hypergeometric2F1[-5/6, 1/2, 3/2, (1 - Sin[c + d*x])/2]*(a + a*Sin[c + d*x])^(1/3))/(d*(1 + Sin[c + d*x])^(5/6))", //
2731, 2730);}

// {2731, 2730}
public void test0115() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(2/3), x]", //
 "(-2*2^(1/6)*Cos[c + d*x]*Hypergeometric2F1[-1/6, 1/2, 3/2, (1 - Sin[c + d*x])/2]*(a + a*Sin[c + d*x])^(2/3))/(d*(1 + Sin[c + d*x])^(7/6))", //
2731, 2730);}

// {2731, 2730}
public void test0116() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(1/3), x]", //
 "-((2^(5/6)*Cos[c + d*x]*Hypergeometric2F1[1/6, 1/2, 3/2, (1 - Sin[c + d*x])/2]*(a + a*Sin[c + d*x])^(1/3))/(d*(1 + Sin[c + d*x])^(5/6)))", //
2731, 2730);}

// {2731, 2730}
public void test0117() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(-1/3), x]", //
 "-((2^(1/6)*Cos[c + d*x]*Hypergeometric2F1[1/2, 5/6, 3/2, (1 - Sin[c + d*x])/2])/(d*(1 + Sin[c + d*x])^(1/6)*(a + a*Sin[c + d*x])^(1/3)))", //
2731, 2730);}

// {2731, 2730}
public void test0118() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(-2/3), x]", //
 "-((Cos[c + d*x]*Hypergeometric2F1[1/2, 7/6, 3/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^(1/6))/(2^(1/6)*d*(a + a*Sin[c + d*x])^(2/3)))", //
2731, 2730);}

// {2731, 2730}
public void test0119() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(-4/3), x]", //
 "-((Cos[c + d*x]*Hypergeometric2F1[1/2, 11/6, 3/2, (1 - Sin[c + d*x])/2])/(2^(5/6)*a*d*(1 + Sin[c + d*x])^(1/6)*(a + a*Sin[c + d*x])^(1/3)))", //
2731, 2730);}

// {2731, 2730}
public void test0120() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^n, x]", //
 "-((2^(1/2 + n)*Cos[c + d*x]*Hypergeometric2F1[1/2, 1/2 - n, 3/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^(-1/2 - n)*(a + a*Sin[c + d*x])^n)/d)", //
2731, 2730);}

// {2731, 2730}
public void test0121() {
	check(//
"Integrate[(a - a*Sin[c + d*x])^n, x]", //
 "(2^(1/2 + n)*Cos[c + d*x]*Hypergeometric2F1[1/2, 1/2 - n, 3/2, (1 + Sin[c + d*x])/2]*(1 - Sin[c + d*x])^(-1/2 - n)*(a - a*Sin[c + d*x])^n)/d", //
2731, 2730);}

// {2734, 2732}
public void test0122() {
	check(//
"Integrate[Sqrt[a + b*Sin[c + d*x]], x]", //
 "(2*EllipticE[(c - Pi/2 + d*x)/2, (2*b)/(a + b)]*Sqrt[a + b*Sin[c + d*x]])/(d*Sqrt[(a + b*Sin[c + d*x])/(a + b)])", //
2734, 2732);}

// {2742, 2740}
public void test0123() {
	check(//
"Integrate[1/Sqrt[a + b*Sin[c + d*x]], x]", //
 "(2*EllipticF[(c - Pi/2 + d*x)/2, (2*b)/(a + b)]*Sqrt[(a + b*Sin[c + d*x])/(a + b)])/(d*Sqrt[a + b*Sin[c + d*x]])", //
2742, 2740);}

// {2744, 143}
public void test0124() {
	check(//
"Integrate[(3 + 4*Sin[c + d*x])^n, x]", //
 "-((Sqrt[2]*7^n*AppellF1[1/2, 1/2, -n, 3/2, (1 - Sin[c + d*x])/2, (4*(1 - Sin[c + d*x]))/7]*Cos[c + d*x])/(d*Sqrt[1 + Sin[c + d*x]]))", //
2744, 143);}

// {2744, 143}
public void test0125() {
	check(//
"Integrate[(3 - 4*Sin[c + d*x])^n, x]", //
 "(Sqrt[2]*7^n*AppellF1[1/2, -n, 1/2, 3/2, (4*(1 + Sin[c + d*x]))/7, (1 + Sin[c + d*x])/2]*Cos[c + d*x])/(d*Sqrt[1 - Sin[c + d*x]])", //
2744, 143);}

// {2744, 143}
public void test0126() {
	check(//
"Integrate[(4 + 3*Sin[c + d*x])^n, x]", //
 "(Sqrt[2]*AppellF1[1/2, 1/2, -n, 3/2, (1 + Sin[c + d*x])/2, -3*(1 + Sin[c + d*x])]*Cos[c + d*x])/(d*Sqrt[1 - Sin[c + d*x]])", //
2744, 143);}

// {2744, 143}
public void test0127() {
	check(//
"Integrate[(4 - 3*Sin[c + d*x])^n, x]", //
 "(Sqrt[2]*7^n*AppellF1[1/2, -n, 1/2, 3/2, (3*(1 + Sin[c + d*x]))/7, (1 + Sin[c + d*x])/2]*Cos[c + d*x])/(d*Sqrt[1 - Sin[c + d*x]])", //
2744, 143);}

// {2744, 143}
public void test0128() {
	check(//
"Integrate[(-3 + 4*Sin[c + d*x])^n, x]", //
 "-((Sqrt[2]*AppellF1[1/2, 1/2, -n, 3/2, (1 - Sin[c + d*x])/2, 4*(1 - Sin[c + d*x])]*Cos[c + d*x])/(d*Sqrt[1 + Sin[c + d*x]]))", //
2744, 143);}

// {2744, 143}
public void test0129() {
	check(//
"Integrate[(-3 - 4*Sin[c + d*x])^n, x]", //
 "(Sqrt[2]*AppellF1[1/2, -n, 1/2, 3/2, 4*(1 + Sin[c + d*x]), (1 + Sin[c + d*x])/2]*Cos[c + d*x])/(d*Sqrt[1 - Sin[c + d*x]])", //
2744, 143);}

// {3377, 2717}
public void test0130() {
	check(//
"Integrate[(a + b*x)*Sin[c + d*x], x]", //
 "-(((a + b*x)*Cos[c + d*x])/d) + (b*Sin[c + d*x])/d^2", //
3377, 2717);}

// {3460, 2723}
public void test0131() {
	check(//
"Integrate[x*(a + b*Sin[c + d*x^2])^2, x]", //
 "((2*a^2 + b^2)*x^2)/4 - (a*b*Cos[c + d*x^2])/d - (b^2*Cos[c + d*x^2]*Sin[c + d*x^2])/(4*d)", //
3460, 2723);}

// {3460, 2723}
public void test0132() {
	check(//
"Integrate[x^2*(a + b*Sin[c + d*x^3])^2, x]", //
 "((2*a^2 + b^2)*x^3)/6 - (2*a*b*Cos[c + d*x^3])/(3*d) - (b^2*Cos[c + d*x^3]*Sin[c + d*x^3])/(6*d)", //
3460, 2723);}

// {3460, 2718}
public void test0133() {
	check(//
"Integrate[Sin[a + b/x]/x^2, x]", //
 "Cos[a + b/x]/b", //
3460, 2718);}

// {3460, 2718}
public void test0134() {
	check(//
"Integrate[Sin[a + b/x^2]/x^3, x]", //
 "Cos[a + b/x^2]/(2*b)", //
3460, 2718);}

// {3460, 2718}
public void test0135() {
	check(//
"Integrate[Sin[Sqrt[x]]/Sqrt[x], x]", //
 "-2*Cos[Sqrt[x]]", //
3460, 2718);}

// {3286, 2718}
public void test0138() {
	check(//
"Integrate[(c*Sin[a + b*x]^3)^(1/3), x]", //
 "-((Cot[a + b*x]*(c*Sin[a + b*x]^3)^(1/3))/b)", //
3286, 2718);}

// {2746, 31}
public void test0139() {
	check(//
"Integrate[Sec[c + d*x]*(a + a*Sin[c + d*x]), x]", //
 "-((a*Log[1 - Sin[c + d*x]])/d)", //
2746, 31);}

// {2746, 32}
public void test0140() {
	check(//
"Integrate[Cos[c + d*x]*(a + a*Sin[c + d*x])^2, x]", //
 "(a + a*Sin[c + d*x])^3/(3*a*d)", //
2746, 32);}

// {2746, 32}
public void test0141() {
	check(//
"Integrate[Sec[c + d*x]^3*(a + a*Sin[c + d*x])^2, x]", //
 "a^3/(d*(a - a*Sin[c + d*x]))", //
2746, 32);}

// {2746, 32}
public void test0142() {
	check(//
"Integrate[Cos[c + d*x]*(a + a*Sin[c + d*x])^3, x]", //
 "(a + a*Sin[c + d*x])^4/(4*a*d)", //
2746, 32);}

// {2749, 2750}
public void test0143() {
	check(//
"Integrate[Sec[c + d*x]^4*(a + a*Sin[c + d*x])^3, x]", //
 "(a^6*Cos[c + d*x]^3)/(3*d*(a - a*Sin[c + d*x])^3)", //
2749, 2750);}

// {2746, 32}
public void test0144() {
	check(//
"Integrate[Sec[c + d*x]^5*(a + a*Sin[c + d*x])^3, x]", //
 "a^5/(2*d*(a - a*Sin[c + d*x])^2)", //
2746, 32);}

// {2746, 32}
public void test0145() {
	check(//
"Integrate[Cos[c + d*x]*(a + a*Sin[c + d*x])^8, x]", //
 "(a + a*Sin[c + d*x])^9/(9*a*d)", //
2746, 32);}

// {2746}
public void test0146() {
	check(//
"Integrate[Cos[c + d*x]^3/(a + a*Sin[c + d*x]), x]", //
 "Sin[c + d*x]/(a*d) - Sin[c + d*x]^2/(2*a*d)", //
2746);}

// {2761, 8}
public void test0147() {
	check(//
"Integrate[Cos[c + d*x]^2/(a + a*Sin[c + d*x]), x]", //
 "x/a + Cos[c + d*x]/(a*d)", //
2761, 8);}

// {2746, 31}
public void test0148() {
	check(//
"Integrate[Cos[c + d*x]/(a + a*Sin[c + d*x]), x]", //
 "Log[1 + Sin[c + d*x]]/(a*d)", //
2746, 31);}

// {2746, 32}
public void test0149() {
	check(//
"Integrate[Cos[c + d*x]^5/(a + a*Sin[c + d*x])^2, x]", //
 "-(a - a*Sin[c + d*x])^3/(3*a^5*d)", //
2746, 32);}

// {2759, 8}
public void test0150() {
	check(//
"Integrate[Cos[c + d*x]^2/(a + a*Sin[c + d*x])^2, x]", //
 "-(x/a^2) - (2*Cos[c + d*x])/(d*(a^2 + a^2*Sin[c + d*x]))", //
2759, 8);}

// {2746, 32}
public void test0151() {
	check(//
"Integrate[Cos[c + d*x]/(a + a*Sin[c + d*x])^2, x]", //
 "-(1/(d*(a^2 + a^2*Sin[c + d*x])))", //
2746, 32);}

// {2746, 32}
public void test0152() {
	check(//
"Integrate[Cos[c + d*x]^7/(a + a*Sin[c + d*x])^3, x]", //
 "-(a - a*Sin[c + d*x])^4/(4*a^7*d)", //
2746, 32);}

// {2746, 32}
public void test0153() {
	check(//
"Integrate[Cos[c + d*x]/(a + a*Sin[c + d*x])^3, x]", //
 "-1/(2*a*d*(a + a*Sin[c + d*x])^2)", //
2746, 32);}

// {2746, 37}
public void test0154() {
	check(//
"Integrate[Cos[c + d*x]^7/(a + a*Sin[c + d*x])^8, x]", //
 "-(a - a*Sin[c + d*x])^4/(8*d*(a^3 + a^3*Sin[c + d*x])^4)", //
2746, 37);}

// {2751, 2750}
public void test0155() {
	check(//
"Integrate[Cos[c + d*x]^6/(a + a*Sin[c + d*x])^8, x]", //
 "-Cos[c + d*x]^7/(9*d*(a + a*Sin[c + d*x])^8) - Cos[c + d*x]^7/(63*a*d*(a + a*Sin[c + d*x])^7)", //
2751, 2750);}

// {2746, 32}
public void test0156() {
	check(//
"Integrate[Cos[c + d*x]/(a + a*Sin[c + d*x])^8, x]", //
 "-1/(7*a*d*(a + a*Sin[c + d*x])^7)", //
2746, 32);}

// {2753, 2752}
public void test0157() {
	check(//
"Integrate[Cos[c + d*x]^2*Sqrt[a + a*Sin[c + d*x]], x]", //
 "(-8*a^2*Cos[c + d*x]^3)/(15*d*(a + a*Sin[c + d*x])^(3/2)) - (2*a*Cos[c + d*x]^3)/(5*d*Sqrt[a + a*Sin[c + d*x]])", //
2753, 2752);}

// {2746, 32}
public void test0158() {
	check(//
"Integrate[Cos[c + d*x]*Sqrt[a + a*Sin[c + d*x]], x]", //
 "(2*(a + a*Sin[c + d*x])^(3/2))/(3*a*d)", //
2746, 32);}

// {2746, 32}
public void test0159() {
	check(//
"Integrate[Cos[c + d*x]*(a + a*Sin[c + d*x])^(3/2), x]", //
 "(2*(a + a*Sin[c + d*x])^(5/2))/(5*a*d)", //
2746, 32);}

// {2746, 32}
public void test0160() {
	check(//
"Integrate[Cos[c + d*x]*(a + a*Sin[c + d*x])^(5/2), x]", //
 "(2*(a + a*Sin[c + d*x])^(7/2))/(7*a*d)", //
2746, 32);}

// {2753, 2752}
public void test0161() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + a*Sin[c + d*x])^(5/2), x]", //
 "(8*a^2*Sec[c + d*x]*Sqrt[a + a*Sin[c + d*x]])/d - (2*a*Sec[c + d*x]*(a + a*Sin[c + d*x])^(3/2))/d", //
2753, 2752);}

// {2746, 32}
public void test0162() {
	check(//
"Integrate[Cos[c + d*x]*(a + a*Sin[c + d*x])^(7/2), x]", //
 "(2*(a + a*Sin[c + d*x])^(9/2))/(9*a*d)", //
2746, 32);}

// {2753, 2752}
public void test0163() {
	check(//
"Integrate[Sec[c + d*x]^4*(a + a*Sin[c + d*x])^(7/2), x]", //
 "(-8*a^2*Sec[c + d*x]^3*(a + a*Sin[c + d*x])^(3/2))/(3*d) + (2*a*Sec[c + d*x]^3*(a + a*Sin[c + d*x])^(5/2))/d", //
2753, 2752);}

// {2753, 2752}
public void test0164() {
	check(//
"Integrate[Cos[c + d*x]^4/Sqrt[a + a*Sin[c + d*x]], x]", //
 "(-8*a^2*Cos[c + d*x]^5)/(35*d*(a + a*Sin[c + d*x])^(5/2)) - (2*a*Cos[c + d*x]^5)/(7*d*(a + a*Sin[c + d*x])^(3/2))", //
2753, 2752);}

// {2746, 32}
public void test0165() {
	check(//
"Integrate[Cos[c + d*x]/Sqrt[a + a*Sin[c + d*x]], x]", //
 "(2*Sqrt[a + a*Sin[c + d*x]])/(a*d)", //
2746, 32);}

// {2753, 2752}
public void test0166() {
	check(//
"Integrate[Cos[c + d*x]^6/(a + a*Sin[c + d*x])^(3/2), x]", //
 "(-8*a^2*Cos[c + d*x]^7)/(63*d*(a + a*Sin[c + d*x])^(7/2)) - (2*a*Cos[c + d*x]^7)/(9*d*(a + a*Sin[c + d*x])^(5/2))", //
2753, 2752);}

// {2746, 32}
public void test0167() {
	check(//
"Integrate[Cos[c + d*x]/(a + a*Sin[c + d*x])^(3/2), x]", //
 "-2/(a*d*Sqrt[a + a*Sin[c + d*x]])", //
2746, 32);}

// {2753, 2752}
public void test0168() {
	check(//
"Integrate[Cos[c + d*x]^8/(a + a*Sin[c + d*x])^(5/2), x]", //
 "(-8*a^2*Cos[c + d*x]^9)/(99*d*(a + a*Sin[c + d*x])^(9/2)) - (2*a*Cos[c + d*x]^9)/(11*d*(a + a*Sin[c + d*x])^(7/2))", //
2753, 2752);}

// {2746, 32}
public void test0169() {
	check(//
"Integrate[Cos[c + d*x]/(a + a*Sin[c + d*x])^(5/2), x]", //
 "-2/(3*a*d*(a + a*Sin[c + d*x])^(3/2))", //
2746, 32);}

// {2751, 2750}
public void test0170() {
	check(//
"Integrate[Sqrt[a + a*Sin[c + d*x]]/(e*Cos[c + d*x])^(5/2), x]", //
 "(-2*Sqrt[a + a*Sin[c + d*x]])/(d*e*(e*Cos[c + d*x])^(3/2)) + (4*(a + a*Sin[c + d*x])^(3/2))/(3*a*d*e*(e*Cos[c + d*x])^(3/2))", //
2751, 2750);}

// {2751, 2750}
public void test0171() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(3/2)/(e*Cos[c + d*x])^(7/2), x]", //
 "(2*(a + a*Sin[c + d*x])^(3/2))/(d*e*(e*Cos[c + d*x])^(5/2)) - (4*(a + a*Sin[c + d*x])^(5/2))/(5*a*d*e*(e*Cos[c + d*x])^(5/2))", //
2751, 2750);}

// {2751, 2750}
public void test0172() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(5/2)/(e*Cos[c + d*x])^(9/2), x]", //
 "(2*(a + a*Sin[c + d*x])^(5/2))/(3*d*e*(e*Cos[c + d*x])^(7/2)) - (4*(a + a*Sin[c + d*x])^(7/2))/(21*a*d*e*(e*Cos[c + d*x])^(7/2))", //
2751, 2750);}

// {2751, 2750}
public void test0173() {
	check(//
"Integrate[1/((e*Cos[c + d*x])^(3/2)*Sqrt[a + a*Sin[c + d*x]]), x]", //
 "-2/(3*d*e*Sqrt[e*Cos[c + d*x]]*Sqrt[a + a*Sin[c + d*x]]) + (4*Sqrt[a + a*Sin[c + d*x]])/(3*a*d*e*Sqrt[e*Cos[c + d*x]])", //
2751, 2750);}

// {2751, 2750}
public void test0174() {
	check(//
"Integrate[1/(Sqrt[e*Cos[c + d*x]]*(a + a*Sin[c + d*x])^(3/2)), x]", //
 "(-2*Sqrt[e*Cos[c + d*x]])/(5*d*e*(a + a*Sin[c + d*x])^(3/2)) - (4*Sqrt[e*Cos[c + d*x]])/(5*a*d*e*Sqrt[a + a*Sin[c + d*x]])", //
2751, 2750);}

// {2751, 2750}
public void test0175() {
	check(//
"Integrate[Sqrt[e*Cos[c + d*x]]/(a + a*Sin[c + d*x])^(5/2), x]", //
 "(-2*(e*Cos[c + d*x])^(3/2))/(7*d*e*(a + a*Sin[c + d*x])^(5/2)) - (4*(e*Cos[c + d*x])^(3/2))/(21*a*d*e*(a + a*Sin[c + d*x])^(3/2))", //
2751, 2750);}

// {2767, 71}
public void test0176() {
	check(//
"Integrate[(e*Cos[c + d*x])^p*(a + a*Sin[c + d*x])^8, x]", //
 "-((2^(17/2 + p/2)*a^8*(e*Cos[c + d*x])^(1 + p)*Hypergeometric2F1[(-15 - p)/2, (1 + p)/2, (3 + p)/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^((-1 - p)/2))/(d*e*(1 + p)))", //
2767, 71);}

// {2767, 71}
public void test0177() {
	check(//
"Integrate[(e*Cos[c + d*x])^p*(a + a*Sin[c + d*x])^3, x]", //
 "-((2^(7/2 + p/2)*a^3*(e*Cos[c + d*x])^(1 + p)*Hypergeometric2F1[(-5 - p)/2, (1 + p)/2, (3 + p)/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^((-1 - p)/2))/(d*e*(1 + p)))", //
2767, 71);}

// {2767, 71}
public void test0178() {
	check(//
"Integrate[(e*Cos[c + d*x])^p*(a + a*Sin[c + d*x])^2, x]", //
 "-((2^(5/2 + p/2)*a^2*(e*Cos[c + d*x])^(1 + p)*Hypergeometric2F1[(-3 - p)/2, (1 + p)/2, (3 + p)/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^((-1 - p)/2))/(d*e*(1 + p)))", //
2767, 71);}

// {2767, 71}
public void test0179() {
	check(//
"Integrate[(e*Cos[c + d*x])^p*(a + a*Sin[c + d*x]), x]", //
 "-((2^(3/2 + p/2)*a*(e*Cos[c + d*x])^(1 + p)*Hypergeometric2F1[(-1 - p)/2, (1 + p)/2, (3 + p)/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^((-1 - p)/2))/(d*e*(1 + p)))", //
2767, 71);}

// {2767, 71}
public void test0180() {
	check(//
"Integrate[(e*Cos[c + d*x])^p/(a + a*Sin[c + d*x]), x]", //
 "-((2^(-1/2 + p/2)*(e*Cos[c + d*x])^(1 + p)*Hypergeometric2F1[(3 - p)/2, (1 + p)/2, (3 + p)/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^((-1 - p)/2))/(a*d*e*(1 + p)))", //
2767, 71);}

// {2767, 71}
public void test0181() {
	check(//
"Integrate[(e*Cos[c + d*x])^p/(a + a*Sin[c + d*x])^2, x]", //
 "-((2^((-3 + p)/2)*(e*Cos[c + d*x])^(1 + p)*Hypergeometric2F1[(5 - p)/2, (1 + p)/2, (3 + p)/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^((-1 - p)/2))/(a^2*d*e*(1 + p)))", //
2767, 71);}

// {2767, 71}
public void test0182() {
	check(//
"Integrate[(e*Cos[c + d*x])^p/(a + a*Sin[c + d*x])^3, x]", //
 "-((2^((-5 + p)/2)*(e*Cos[c + d*x])^(1 + p)*Hypergeometric2F1[(7 - p)/2, (1 + p)/2, (3 + p)/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^((-1 - p)/2))/(a^3*d*e*(1 + p)))", //
2767, 71);}

// {2767, 71}
public void test0183() {
	check(//
"Integrate[(e*Cos[c + d*x])^p/(a + a*Sin[c + d*x])^8, x]", //
 "-((2^((-15 + p)/2)*(e*Cos[c + d*x])^(1 + p)*Hypergeometric2F1[(17 - p)/2, (1 + p)/2, (3 + p)/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^((-1 - p)/2))/(a^8*d*e*(1 + p)))", //
2767, 71);}

// {2746, 32}
public void test0184() {
	check(//
"Integrate[Cos[c + d*x]*(a + a*Sin[c + d*x])^m, x]", //
 "(a + a*Sin[c + d*x])^(1 + m)/(a*d*(1 + m))", //
2746, 32);}

// {2746, 70}
public void test0185() {
	check(//
"Integrate[Sec[c + d*x]*(a + a*Sin[c + d*x])^m, x]", //
 "(Hypergeometric2F1[1, m, 1 + m, (1 + Sin[c + d*x])/2]*(a + a*Sin[c + d*x])^m)/(2*d*m)", //
2746, 70);}

// {2746, 70}
public void test0186() {
	check(//
"Integrate[Sec[c + d*x]^3*(a + a*Sin[c + d*x])^m, x]", //
 "-(a*Hypergeometric2F1[2, -1 + m, m, (1 + Sin[c + d*x])/2]*(a + a*Sin[c + d*x])^(-1 + m))/(4*d*(1 - m))", //
2746, 70);}

// {2746, 70}
public void test0187() {
	check(//
"Integrate[Sec[c + d*x]^5*(a + a*Sin[c + d*x])^m, x]", //
 "-(a^2*Hypergeometric2F1[3, -2 + m, -1 + m, (1 + Sin[c + d*x])/2]*(a + a*Sin[c + d*x])^(-2 + m))/(8*d*(2 - m))", //
2746, 70);}

// {2751, 2750}
public void test0188() {
	check(//
"Integrate[(e*Cos[c + d*x])^(-2 - m)*(a + a*Sin[c + d*x])^m, x]", //
 "-(((e*Cos[c + d*x])^(-1 - m)*(a + a*Sin[c + d*x])^m)/(d*e*(1 - m))) + ((e*Cos[c + d*x])^(-1 - m)*(a + a*Sin[c + d*x])^(1 + m))/(a*d*e*(1 - m^2))", //
2751, 2750);}

// {2753, 2752}
public void test0189() {
	check(//
"Integrate[(e*Cos[c + d*x])^(3 - 2*m)*(a + a*Sin[c + d*x])^m, x]", //
 "(-2*a^2*(e*Cos[c + d*x])^(4 - 2*m)*(a + a*Sin[c + d*x])^(-2 + m))/(d*e*(6 - 5*m + m^2)) - (a*(e*Cos[c + d*x])^(4 - 2*m)*(a + a*Sin[c + d*x])^(-1 + m))/(d*e*(3 - m))", //
2753, 2752);}

// {2747, 32}
public void test0190() {
	check(//
"Integrate[Cos[c + d*x]*(a + b*Sin[c + d*x])^2, x]", //
 "(a + b*Sin[c + d*x])^3/(3*b*d)", //
2747, 32);}

// {2747, 32}
public void test0191() {
	check(//
"Integrate[Cos[c + d*x]*(a + b*Sin[c + d*x])^3, x]", //
 "(a + b*Sin[c + d*x])^4/(4*b*d)", //
2747, 32);}

// {2770, 2813}
public void test0192() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + b*Sin[c + d*x])^3, x]", //
 "-3*a*b^2*x + (2*b*(a^2 + b^2)*Cos[c + d*x])/d + (a*b^2*Cos[c + d*x]*Sin[c + d*x])/d + (Sec[c + d*x]*(b + a*Sin[c + d*x])*(a + b*Sin[c + d*x])^2)/d", //
2770, 2813);}

// {2747, 32}
public void test0193() {
	check(//
"Integrate[Cos[c + d*x]*(a + b*Sin[c + d*x])^8, x]", //
 "(a + b*Sin[c + d*x])^9/(9*b*d)", //
2747, 32);}

// {2747, 31}
public void test0194() {
	check(//
"Integrate[Cos[c + d*x]/(a + b*Sin[c + d*x]), x]", //
 "Log[a + b*Sin[c + d*x]]/(b*d)", //
2747, 31);}

// {2747, 32}
public void test0195() {
	check(//
"Integrate[Cos[c + d*x]/(a + b*Sin[c + d*x])^2, x]", //
 "-(1/(b*d*(a + b*Sin[c + d*x])))", //
2747, 32);}

// {2747, 32}
public void test0196() {
	check(//
"Integrate[Cos[c + d*x]/(a + b*Sin[c + d*x])^3, x]", //
 "-1/(2*b*d*(a + b*Sin[c + d*x])^2)", //
2747, 32);}

// {2747, 32}
public void test0197() {
	check(//
"Integrate[Cos[c + d*x]/(a + b*Sin[c + d*x])^8, x]", //
 "-1/(7*b*d*(a + b*Sin[c + d*x])^7)", //
2747, 32);}

// {2747, 32}
public void test0198() {
	check(//
"Integrate[Cos[c + d*x]*Sqrt[a + b*Sin[c + d*x]], x]", //
 "(2*(a + b*Sin[c + d*x])^(3/2))/(3*b*d)", //
2747, 32);}

// {2747, 32}
public void test0199() {
	check(//
"Integrate[Cos[c + d*x]*(a + b*Sin[c + d*x])^(3/2), x]", //
 "(2*(a + b*Sin[c + d*x])^(5/2))/(5*b*d)", //
2747, 32);}

// {2747, 32}
public void test0200() {
	check(//
"Integrate[Cos[c + d*x]*(a + b*Sin[c + d*x])^(5/2), x]", //
 "(2*(a + b*Sin[c + d*x])^(7/2))/(7*b*d)", //
2747, 32);}

// {2747, 32}
public void test0201() {
	check(//
"Integrate[Cos[c + d*x]/Sqrt[a + b*Sin[c + d*x]], x]", //
 "(2*Sqrt[a + b*Sin[c + d*x]])/(b*d)", //
2747, 32);}

// {2747, 32}
public void test0202() {
	check(//
"Integrate[Cos[c + d*x]/(a + b*Sin[c + d*x])^(3/2), x]", //
 "-2/(b*d*Sqrt[a + b*Sin[c + d*x]])", //
2747, 32);}

// {2747, 32}
public void test0203() {
	check(//
"Integrate[Cos[c + d*x]/(a + b*Sin[c + d*x])^(5/2), x]", //
 "-2/(3*b*d*(a + b*Sin[c + d*x])^(3/2))", //
2747, 32);}

// {2748, 2722}
public void test0204() {
	check(//
"Integrate[(e*Cos[c + d*x])^p*(a + b*Sin[c + d*x]), x]", //
 "-((b*(e*Cos[c + d*x])^(1 + p))/(d*e*(1 + p))) - (a*(e*Cos[c + d*x])^(1 + p)*Hypergeometric2F1[1/2, (1 + p)/2, (3 + p)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(d*e*(1 + p)*Sqrt[Sin[c + d*x]^2])", //
2748, 2722);}

// {2783, 143}
public void test0205() {
	check(//
"Integrate[(e*Cos[c + d*x])^p*(a + b*Sin[c + d*x])^(5/2), x]", //
 "(2*e*AppellF1[7/2, (1 - p)/2, (1 - p)/2, 9/2, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(-1 + p)*(a + b*Sin[c + d*x])^(7/2)*(1 - (a + b*Sin[c + d*x])/(a - b))^((1 - p)/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^((1 - p)/2))/(7*b*d)", //
2783, 143);}

// {2783, 143}
public void test0206() {
	check(//
"Integrate[(e*Cos[c + d*x])^p*(a + b*Sin[c + d*x])^(3/2), x]", //
 "(2*e*AppellF1[5/2, (1 - p)/2, (1 - p)/2, 7/2, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(-1 + p)*(a + b*Sin[c + d*x])^(5/2)*(1 - (a + b*Sin[c + d*x])/(a - b))^((1 - p)/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^((1 - p)/2))/(5*b*d)", //
2783, 143);}

// {2783, 143}
public void test0207() {
	check(//
"Integrate[(e*Cos[c + d*x])^p*Sqrt[a + b*Sin[c + d*x]], x]", //
 "(2*e*AppellF1[3/2, (1 - p)/2, (1 - p)/2, 5/2, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(-1 + p)*(a + b*Sin[c + d*x])^(3/2)*(1 - (a + b*Sin[c + d*x])/(a - b))^((1 - p)/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^((1 - p)/2))/(3*b*d)", //
2783, 143);}

// {2783, 143}
public void test0208() {
	check(//
"Integrate[(e*Cos[c + d*x])^p/Sqrt[a + b*Sin[c + d*x]], x]", //
 "(2*e*AppellF1[1/2, (1 - p)/2, (1 - p)/2, 3/2, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(-1 + p)*Sqrt[a + b*Sin[c + d*x]]*(1 - (a + b*Sin[c + d*x])/(a - b))^((1 - p)/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^((1 - p)/2))/(b*d)", //
2783, 143);}

// {2783, 143}
public void test0209() {
	check(//
"Integrate[(e*Cos[c + d*x])^p/(a + b*Sin[c + d*x])^(3/2), x]", //
 "(-2*e*AppellF1[-1/2, (1 - p)/2, (1 - p)/2, 1/2, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(-1 + p)*(1 - (a + b*Sin[c + d*x])/(a - b))^((1 - p)/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^((1 - p)/2))/(b*d*Sqrt[a + b*Sin[c + d*x]])", //
2783, 143);}

// {2783, 143}
public void test0210() {
	check(//
"Integrate[(e*Cos[c + d*x])^p/(a + b*Sin[c + d*x])^(5/2), x]", //
 "(-2*e*AppellF1[-3/2, (1 - p)/2, (1 - p)/2, -1/2, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(-1 + p)*(1 - (a + b*Sin[c + d*x])/(a - b))^((1 - p)/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^((1 - p)/2))/(3*b*d*(a + b*Sin[c + d*x])^(3/2))", //
2783, 143);}

// {2783, 143}
public void test0211() {
	check(//
"Integrate[(e*Cos[c + d*x])^p*(a + b*Sin[c + d*x])^m, x]", //
 "(e*AppellF1[1 + m, (1 - p)/2, (1 - p)/2, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(-1 + p)*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^((1 - p)/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^((1 - p)/2))/(b*d*(1 + m))", //
2783, 143);}

// {2747, 32}
public void test0212() {
	check(//
"Integrate[Cos[c + d*x]*(a + b*Sin[c + d*x])^m, x]", //
 "(a + b*Sin[c + d*x])^(1 + m)/(b*d*(1 + m))", //
2747, 32);}

// {2783, 143}
public void test0213() {
	check(//
"Integrate[Cos[c + d*x]^4*(a + b*Sin[c + d*x])^m, x]", //
 "(AppellF1[1 + m, -3/2, -3/2, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*Cos[c + d*x]^3*(a + b*Sin[c + d*x])^(1 + m))/(b*d*(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(3/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^(3/2))", //
2783, 143);}

// {2783, 143}
public void test0214() {
	check(//
"Integrate[Cos[c + d*x]^2*(a + b*Sin[c + d*x])^m, x]", //
 "(AppellF1[1 + m, -1/2, -1/2, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*Cos[c + d*x]*(a + b*Sin[c + d*x])^(1 + m))/(b*d*(1 + m)*Sqrt[1 - (a + b*Sin[c + d*x])/(a - b)]*Sqrt[1 - (a + b*Sin[c + d*x])/(a + b)])", //
2783, 143);}

// {2783, 143}
public void test0215() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + b*Sin[c + d*x])^m, x]", //
 "(AppellF1[1 + m, 3/2, 3/2, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*Sec[c + d*x]^3*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(3/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^(3/2))/(b*d*(1 + m))", //
2783, 143);}

// {2783, 143}
public void test0216() {
	check(//
"Integrate[Sec[c + d*x]^4*(a + b*Sin[c + d*x])^m, x]", //
 "(AppellF1[1 + m, 5/2, 5/2, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*Sec[c + d*x]^5*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(5/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^(5/2))/(b*d*(1 + m))", //
2783, 143);}

// {2783, 143}
public void test0217() {
	check(//
"Integrate[(e*Cos[c + d*x])^(5/2)*(a + b*Sin[c + d*x])^m, x]", //
 "(e*AppellF1[1 + m, -3/4, -3/4, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(3/2)*(a + b*Sin[c + d*x])^(1 + m))/(b*d*(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(3/4)*(1 - (a + b*Sin[c + d*x])/(a + b))^(3/4))", //
2783, 143);}

// {2783, 143}
public void test0218() {
	check(//
"Integrate[(e*Cos[c + d*x])^(3/2)*(a + b*Sin[c + d*x])^m, x]", //
 "(e*AppellF1[1 + m, -1/4, -1/4, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*Sqrt[e*Cos[c + d*x]]*(a + b*Sin[c + d*x])^(1 + m))/(b*d*(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(1/4)*(1 - (a + b*Sin[c + d*x])/(a + b))^(1/4))", //
2783, 143);}

// {2783, 143}
public void test0219() {
	check(//
"Integrate[Sqrt[e*Cos[c + d*x]]*(a + b*Sin[c + d*x])^m, x]", //
 "(e*AppellF1[1 + m, 1/4, 1/4, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(1/4)*(1 - (a + b*Sin[c + d*x])/(a + b))^(1/4))/(b*d*(1 + m)*Sqrt[e*Cos[c + d*x]])", //
2783, 143);}

// {2783, 143}
public void test0220() {
	check(//
"Integrate[(a + b*Sin[c + d*x])^m/Sqrt[e*Cos[c + d*x]], x]", //
 "(e*AppellF1[1 + m, 3/4, 3/4, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(3/4)*(1 - (a + b*Sin[c + d*x])/(a + b))^(3/4))/(b*d*(1 + m)*(e*Cos[c + d*x])^(3/2))", //
2783, 143);}

// {2783, 143}
public void test0221() {
	check(//
"Integrate[(a + b*Sin[c + d*x])^m/(e*Cos[c + d*x])^(3/2), x]", //
 "(e*AppellF1[1 + m, 5/4, 5/4, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(5/4)*(1 - (a + b*Sin[c + d*x])/(a + b))^(5/4))/(b*d*(1 + m)*(e*Cos[c + d*x])^(5/2))", //
2783, 143);}

// {2783, 143}
public void test0222() {
	check(//
"Integrate[(a + b*Sin[c + d*x])^m/(e*Cos[c + d*x])^(5/2), x]", //
 "(e*AppellF1[1 + m, 7/4, 7/4, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(7/4)*(1 - (a + b*Sin[c + d*x])/(a + b))^(7/4))/(b*d*(1 + m)*(e*Cos[c + d*x])^(7/2))", //
2783, 143);}

// {2783, 143}
public void test0223() {
	check(//
"Integrate[(a + b*Sin[c + d*x])^m/(e*Cos[c + d*x])^m, x]", //
 "(e*AppellF1[1 + m, (1 + m)/2, (1 + m)/2, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(-1 - m)*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^((1 + m)/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^((1 + m)/2))/(b*d*(1 + m))", //
2783, 143);}

// {2783, 143}
public void test0224() {
	check(//
"Integrate[(e*Cos[c + d*x])^(1 - m)*(a + b*Sin[c + d*x])^m, x]", //
 "(e*AppellF1[1 + m, m/2, m/2, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^(m/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^(m/2))/(b*d*(1 + m)*(e*Cos[c + d*x])^m)", //
2783, 143);}

// {2783, 143}
public void test0225() {
	check(//
"Integrate[(e*Cos[c + d*x])^(2 - m)*(a + b*Sin[c + d*x])^m, x]", //
 "(e*AppellF1[1 + m, (-1 + m)/2, (-1 + m)/2, 2 + m, (a + b*Sin[c + d*x])/(a - b), (a + b*Sin[c + d*x])/(a + b)]*(e*Cos[c + d*x])^(1 - m)*(a + b*Sin[c + d*x])^(1 + m)*(1 - (a + b*Sin[c + d*x])/(a - b))^((-1 + m)/2)*(1 - (a + b*Sin[c + d*x])/(a + b))^((-1 + m)/2))/(b*d*(1 + m))", //
2783, 143);}

// {3526, 3432}
public void test0226() {
	check(//
"Integrate[Sin[1/4 + x + x^2], x]", //
 "Sqrt[Pi/2]*FresnelS[(1 + 2*x)/Sqrt[2*Pi]]", //
3526, 3432);}

// {2786, 75}
public void test0231() {
	check(//
"Integrate[Cot[c + d*x]^3*(a + a*Sin[c + d*x])^2, x]", //
 "-(Csc[c + d*x]^2*(a + a*Sin[c + d*x])^4)/(2*a^2*d)", //
2786, 75);}

// {2786, 67}
public void test0232() {
	check(//
"Integrate[Cot[e + f*x]*(a + a*Sin[e + f*x])^m, x]", //
 "-((Hypergeometric2F1[1, 1 + m, 2 + m, 1 + Sin[e + f*x]]*(a + a*Sin[e + f*x])^(1 + m))/(a*f*(1 + m)))", //
2786, 67);}

// {2731, 2730}
public void test0233() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^m, x]", //
 "-((2^(1/2 + m)*Cos[e + f*x]*Hypergeometric2F1[1/2, 1/2 - m, 3/2, (1 - Sin[e + f*x])/2]*(1 + Sin[e + f*x])^(-1/2 - m)*(a + a*Sin[e + f*x])^m)/f)", //
2731, 2730);}

// {2846, 2813}
public void test0234() {
	check(//
"Integrate[Sin[x]^3/(a + a*Sin[x]), x]", //
 "(3*x)/(2*a) + (2*Cos[x])/a - (3*Cos[x]*Sin[x])/(2*a) + (Cos[x]*Sin[x]^2)/(a + a*Sin[x])", //
2846, 2813);}

// {2814, 2727}
public void test0235() {
	check(//
"Integrate[Sin[x]/(a + a*Sin[x]), x]", //
 "x/a + Cos[x]/(a + a*Sin[x])", //
2814, 2727);}

// {2829, 2727}
public void test0236() {
	check(//
"Integrate[Sin[x]/(a + a*Sin[x])^2, x]", //
 "Cos[x]/(3*(a + a*Sin[x])^2) - (2*Cos[x])/(3*(a^2 + a^2*Sin[x]))", //
2829, 2727);}

// {2729, 2727}
public void test0237() {
	check(//
"Integrate[(a + a*Sin[x])^(-2), x]", //
 "-Cos[x]/(3*(a + a*Sin[x])^2) - Cos[x]/(3*(a^2 + a^2*Sin[x]))", //
2729, 2727);}

// {2830, 2725}
public void test0238() {
	check(//
"Integrate[Sin[c + d*x]*Sqrt[a + a*Sin[c + d*x]], x]", //
 "(-2*a*Cos[c + d*x])/(3*d*Sqrt[a + a*Sin[c + d*x]]) - (2*Cos[c + d*x]*Sqrt[a + a*Sin[c + d*x]])/(3*d)", //
2830, 2725);}

// {2852, 212}
public void test0239() {
	check(//
"Integrate[Csc[c + d*x]*Sqrt[a + a*Sin[c + d*x]], x]", //
 "(-2*Sqrt[a]*ArcTanh[(Sqrt[a]*Cos[c + d*x])/Sqrt[a + a*Sin[c + d*x]]])/d", //
2852, 212);}

// {2852, 212}
public void test0240() {
	check(//
"Integrate[Csc[c + d*x]*Sqrt[a - a*Sin[c + d*x]], x]", //
 "(-2*Sqrt[a]*ArcTanh[(Sqrt[a]*Cos[c + d*x])/Sqrt[a - a*Sin[c + d*x]]])/d", //
2852, 212);}

// {2852, 210}
public void test0241() {
	check(//
"Integrate[Csc[c + d*x]*Sqrt[-a + a*Sin[c + d*x]], x]", //
 "(2*Sqrt[a]*ArcTan[(Sqrt[a]*Cos[c + d*x])/Sqrt[-a + a*Sin[c + d*x]]])/d", //
2852, 210);}

// {2852, 210}
public void test0242() {
	check(//
"Integrate[Csc[c + d*x]*Sqrt[-a - a*Sin[c + d*x]], x]", //
 "(2*Sqrt[a]*ArcTan[(Sqrt[a]*Cos[c + d*x])/Sqrt[-a - a*Sin[c + d*x]]])/d", //
2852, 210);}

// {2726, 2725}
public void test0243() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(3/2), x]", //
 "(-8*a^2*Cos[c + d*x])/(3*d*Sqrt[a + a*Sin[c + d*x]]) - (2*a*Cos[c + d*x]*Sqrt[a + a*Sin[c + d*x]])/(3*d)", //
2726, 2725);}

// {2728, 212}
public void test0244() {
	check(//
"Integrate[1/Sqrt[a + a*Sin[c + d*x]], x]", //
 "-((Sqrt[2]*ArcTanh[(Sqrt[a]*Cos[c + d*x])/(Sqrt[2]*Sqrt[a + a*Sin[c + d*x]])])/(Sqrt[a]*d))", //
2728, 212);}

// {2853, 222}
public void test0245() {
	check(//
"Integrate[Sqrt[a + a*Sin[e + f*x]]/Sqrt[Sin[e + f*x]], x]", //
 "(-2*Sqrt[a]*ArcSin[(Sqrt[a]*Cos[e + f*x])/Sqrt[a + a*Sin[e + f*x]]])/f", //
2853, 222);}

// {2853, 222}
public void test0246() {
	check(//
"Integrate[Sqrt[a - a*Sin[e + f*x]]/Sqrt[-Sin[e + f*x]], x]", //
 "(2*Sqrt[a]*ArcSin[(Sqrt[a]*Cos[e + f*x])/Sqrt[a - a*Sin[e + f*x]]])/f", //
2853, 222);}

// {2860, 222}
public void test0247() {
	check(//
"Integrate[1/(Sqrt[Sin[x]]*Sqrt[1 + Sin[x]]), x]", //
 "-(Sqrt[2]*ArcSin[Cos[x]/(1 + Sin[x])])", //
2860, 222);}

// {2861, 211}
public void test0248() {
	check(//
"Integrate[1/(Sqrt[Sin[x]]*Sqrt[a + a*Sin[x]]), x]", //
 "-((Sqrt[2]*ArcTan[(Sqrt[a]*Cos[x])/(Sqrt[2]*Sqrt[Sin[x]]*Sqrt[a + a*Sin[x]])])/Sqrt[a])", //
2861, 211);}

// {2861, 212}
public void test0249() {
	check(//
"Integrate[1/(Sqrt[1 - Sin[x]]*Sqrt[Sin[x]]), x]", //
 "Sqrt[2]*ArcTanh[Cos[x]/(Sqrt[2]*Sqrt[1 - Sin[x]]*Sqrt[Sin[x]])]", //
2861, 212);}

// {2861, 214}
public void test0250() {
	check(//
"Integrate[1/(Sqrt[Sin[x]]*Sqrt[a - a*Sin[x]]), x]", //
 "(Sqrt[2]*ArcTanh[(Sqrt[a]*Cos[x])/(Sqrt[2]*Sqrt[Sin[x]]*Sqrt[a - a*Sin[x]])])/Sqrt[a]", //
2861, 214);}

// {2731, 2730}
public void test0251() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(2/3), x]", //
 "(-2*2^(1/6)*Cos[c + d*x]*Hypergeometric2F1[-1/6, 1/2, 3/2, (1 - Sin[c + d*x])/2]*(a + a*Sin[c + d*x])^(2/3))/(d*(1 + Sin[c + d*x])^(7/6))", //
2731, 2730);}

// {2731, 2730}
public void test0252() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(4/3), x]", //
 "(-2*2^(5/6)*a*Cos[c + d*x]*Hypergeometric2F1[-5/6, 1/2, 3/2, (1 - Sin[c + d*x])/2]*(a + a*Sin[c + d*x])^(1/3))/(d*(1 + Sin[c + d*x])^(5/6))", //
2731, 2730);}

// {2731, 2730}
public void test0253() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(-1/3), x]", //
 "-((2^(1/6)*Cos[c + d*x]*Hypergeometric2F1[1/2, 5/6, 3/2, (1 - Sin[c + d*x])/2])/(d*(1 + Sin[c + d*x])^(1/6)*(a + a*Sin[c + d*x])^(1/3)))", //
2731, 2730);}

// {2731, 2730}
public void test0254() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^(-4/3), x]", //
 "-((Cos[c + d*x]*Hypergeometric2F1[1/2, 11/6, 3/2, (1 - Sin[c + d*x])/2])/(2^(5/6)*a*d*(1 + Sin[c + d*x])^(1/6)*(a + a*Sin[c + d*x])^(1/3)))", //
2731, 2730);}

// {2855, 67}
public void test0255() {
	check(//
"Integrate[Sin[e + f*x]^n*Sqrt[1 + Sin[e + f*x]], x]", //
 "(-2*Cos[e + f*x]*Hypergeometric2F1[1/2, -n, 3/2, 1 - Sin[e + f*x]])/(f*Sqrt[1 + Sin[e + f*x]])", //
2855, 67);}

// {2855, 67}
public void test0256() {
	check(//
"Integrate[Sin[e + f*x]^n*Sqrt[a + a*Sin[e + f*x]], x]", //
 "(-2*a*Cos[e + f*x]*Hypergeometric2F1[1/2, -n, 3/2, 1 - Sin[e + f*x]])/(f*Sqrt[a + a*Sin[e + f*x]])", //
2855, 67);}

// {2855, 66}
public void test0257() {
	check(//
"Integrate[(d*Sin[e + f*x])^n*Sqrt[1 + Sin[e + f*x]], x]", //
 "(Cos[e + f*x]*Hypergeometric2F1[1/2, 1 + n, 2 + n, Sin[e + f*x]]*(d*Sin[e + f*x])^(1 + n))/(d*f*(1 + n)*Sqrt[1 - Sin[e + f*x]]*Sqrt[1 + Sin[e + f*x]])", //
2855, 66);}

// {2864, 138}
public void test0258() {
	check(//
"Integrate[Sin[e + f*x]^n*(1 + Sin[e + f*x])^m, x]", //
 "-((2^(1/2 + m)*AppellF1[1/2, -n, 1/2 - m, 3/2, 1 - Sin[e + f*x], (1 - Sin[e + f*x])/2]*Cos[e + f*x])/(f*Sqrt[1 + Sin[e + f*x]]))", //
2864, 138);}

// {2864, 138}
public void test0259() {
	check(//
"Integrate[(1 - Sin[e + f*x])^m*(-Sin[e + f*x])^n, x]", //
 "(2^(1/2 + m)*AppellF1[1/2, -n, 1/2 - m, 3/2, 1 + Sin[e + f*x], (1 + Sin[e + f*x])/2]*Cos[e + f*x])/(f*Sqrt[1 - Sin[e + f*x]])", //
2864, 138);}

// {2731, 2730}
public void test0260() {
	check(//
"Integrate[(a + a*Sin[c + d*x])^n, x]", //
 "-((2^(1/2 + n)*Cos[c + d*x]*Hypergeometric2F1[1/2, 1/2 - n, 3/2, (1 - Sin[c + d*x])/2]*(1 + Sin[c + d*x])^(-1/2 - n)*(a + a*Sin[c + d*x])^n)/d)", //
2731, 2730);}

// {2718}
public void test0261() {
	check(//
"Integrate[a + b*Sin[e + f*x], x]", //
 "a*x - (b*Cos[e + f*x])/f", //
2718);}

// {2814, 3855}
public void test0262() {
	check(//
"Integrate[Csc[e + f*x]*(a + b*Sin[e + f*x]), x]", //
 "b*x - (a*ArcTanh[Cos[e + f*x]])/f", //
2814, 3855);}

// {2832, 2813}
public void test0263() {
	check(//
"Integrate[Sin[e + f*x]*(a + b*Sin[e + f*x])^2, x]", //
 "a*b*x - (2*(a^2 + b^2)*Cos[e + f*x])/(3*f) - (a*b*Cos[e + f*x]*Sin[e + f*x])/(3*f) - (Cos[e + f*x]*(a + b*Sin[e + f*x])^2)/(3*f)", //
2832, 2813);}

// {2735, 2813}
public void test0264() {
	check(//
"Integrate[(a + b*Sin[e + f*x])^3, x]", //
 "(a*(2*a^2 + 3*b^2)*x)/2 - (2*b*(4*a^2 + b^2)*Cos[e + f*x])/(3*f) - (5*a*b^2*Cos[e + f*x]*Sin[e + f*x])/(6*f) - (b*Cos[e + f*x]*(a + b*Sin[e + f*x])^2)/(3*f)", //
2735, 2813);}

// {2734, 2732}
public void test0265() {
	check(//
"Integrate[Sqrt[a + b*Sin[e + f*x]], x]", //
 "(2*EllipticE[(e - Pi/2 + f*x)/2, (2*b)/(a + b)]*Sqrt[a + b*Sin[e + f*x]])/(f*Sqrt[(a + b*Sin[e + f*x])/(a + b)])", //
2734, 2732);}

// {2742, 2740}
public void test0266() {
	check(//
"Integrate[1/Sqrt[a + b*Sin[e + f*x]], x]", //
 "(2*EllipticF[(e - Pi/2 + f*x)/2, (2*b)/(a + b)]*Sqrt[(a + b*Sin[e + f*x])/(a + b)])/(f*Sqrt[a + b*Sin[e + f*x]])", //
2742, 2740);}

// {2886, 2884}
public void test0267() {
	check(//
"Integrate[Csc[e + f*x]/Sqrt[a + b*Sin[e + f*x]], x]", //
 "(2*EllipticPi[2, (e - Pi/2 + f*x)/2, (2*b)/(a + b)]*Sqrt[(a + b*Sin[e + f*x])/(a + b)])/(f*Sqrt[a + b*Sin[e + f*x]])", //
2886, 2884);}

// {2814, 2727}
public void test0268() {
	check(//
"Integrate[(a + a*Sin[e + f*x])/(c - c*Sin[e + f*x]), x]", //
 "-((a*x)/c) + (2*a*Cos[e + f*x])/(f*(c - c*Sin[e + f*x]))", //
2814, 2727);}

// {2815, 2750}
public void test0269() {
	check(//
"Integrate[(a + a*Sin[e + f*x])/(c - c*Sin[e + f*x])^2, x]", //
 "(a*c*Cos[e + f*x]^3)/(3*f*(c - c*Sin[e + f*x])^3)", //
2815, 2750);}

// {2815, 2750}
public void test0270() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^2/(c - c*Sin[e + f*x])^3, x]", //
 "(a^2*c^2*Cos[e + f*x]^5)/(5*f*(c - c*Sin[e + f*x])^5)", //
2815, 2750);}

// {2815, 2750}
public void test0271() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^3/(c - c*Sin[e + f*x])^4, x]", //
 "(a^3*c^3*Cos[e + f*x]^7)/(7*f*(c - c*Sin[e + f*x])^7)", //
2815, 2750);}

// {2814, 2727}
public void test0272() {
	check(//
"Integrate[(c - c*Sin[e + f*x])/(a + a*Sin[e + f*x]), x]", //
 "-((c*x)/a) - (2*c*Cos[e + f*x])/(f*(a + a*Sin[e + f*x]))", //
2814, 2727);}

// {2815, 2750}
public void test0273() {
	check(//
"Integrate[(c - c*Sin[e + f*x])/(a + a*Sin[e + f*x])^2, x]", //
 "-(a*c*Cos[e + f*x]^3)/(3*f*(a + a*Sin[e + f*x])^3)", //
2815, 2750);}

// {2815, 2750}
public void test0274() {
	check(//
"Integrate[(c - c*Sin[e + f*x])^2/(a + a*Sin[e + f*x])^3, x]", //
 "-(a^2*c^2*Cos[e + f*x]^5)/(5*f*(a + a*Sin[e + f*x])^5)", //
2815, 2750);}

// {2815, 2752}
public void test0275() {
	check(//
"Integrate[(a + a*Sin[e + f*x])*Sqrt[c - c*Sin[e + f*x]], x]", //
 "(2*a*c^2*Cos[e + f*x]^3)/(3*f*(c - c*Sin[e + f*x])^(3/2))", //
2815, 2752);}

// {2815, 2752}
public void test0276() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^2*Sqrt[c - c*Sin[e + f*x]], x]", //
 "(2*a^2*c^3*Cos[e + f*x]^5)/(5*f*(c - c*Sin[e + f*x])^(5/2))", //
2815, 2752);}

// {2815, 2752}
public void test0277() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^3*Sqrt[c - c*Sin[e + f*x]], x]", //
 "(2*a^3*c^4*Cos[e + f*x]^7)/(7*f*(c - c*Sin[e + f*x])^(7/2))", //
2815, 2752);}

// {2815, 2752}
public void test0278() {
	check(//
"Integrate[Sqrt[c - c*Sin[e + f*x]]/(a + a*Sin[e + f*x]), x]", //
 "(-2*Sec[e + f*x]*Sqrt[c - c*Sin[e + f*x]])/(a*f)", //
2815, 2752);}

// {2815, 2752}
public void test0279() {
	check(//
"Integrate[Sqrt[c - c*Sin[e + f*x]]/(a + a*Sin[e + f*x])^2, x]", //
 "(-2*Sec[e + f*x]^3*(c - c*Sin[e + f*x])^(3/2))/(3*a^2*c*f)", //
2815, 2752);}

// {2815, 2752}
public void test0280() {
	check(//
"Integrate[Sqrt[c - c*Sin[e + f*x]]/(a + a*Sin[e + f*x])^3, x]", //
 "(-2*Sec[e + f*x]^5*(c - c*Sin[e + f*x])^(5/2))/(5*a^3*c^2*f)", //
2815, 2752);}

// {2819, 2817}
public void test0281() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(3/2)*(c - c*Sin[e + f*x])^(7/2), x]", //
 "-(a^2*Cos[e + f*x]*(c - c*Sin[e + f*x])^(7/2))/(10*f*Sqrt[a + a*Sin[e + f*x]]) - (a*Cos[e + f*x]*Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(7/2))/(5*f)", //
2819, 2817);}

// {2819, 2817}
public void test0282() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(3/2)*(c - c*Sin[e + f*x])^(5/2), x]", //
 "-(a^2*Cos[e + f*x]*(c - c*Sin[e + f*x])^(5/2))/(6*f*Sqrt[a + a*Sin[e + f*x]]) - (a*Cos[e + f*x]*Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(5/2))/(4*f)", //
2819, 2817);}

// {2819, 2817}
public void test0283() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(3/2)*(c - c*Sin[e + f*x])^(3/2), x]", //
 "-(a^2*Cos[e + f*x]*(c - c*Sin[e + f*x])^(3/2))/(3*f*Sqrt[a + a*Sin[e + f*x]]) - (a*Cos[e + f*x]*Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(3/2))/(3*f)", //
2819, 2817);}

// {2822, 2821}
public void test0284() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(3/2)/(c - c*Sin[e + f*x])^(7/2), x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(3/2))/(6*f*(c - c*Sin[e + f*x])^(7/2)) + (Cos[e + f*x]*(a + a*Sin[e + f*x])^(3/2))/(24*c*f*(c - c*Sin[e + f*x])^(5/2))", //
2822, 2821);}

// {2818, 2817}
public void test0285() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(3/2)/(c - c*Sin[e + f*x])^(9/2), x]", //
 "(a*Cos[e + f*x]*Sqrt[a + a*Sin[e + f*x]])/(4*f*(c - c*Sin[e + f*x])^(9/2)) - (a^2*Cos[e + f*x])/(12*c*f*Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(7/2))", //
2818, 2817);}

// {2818, 2817}
public void test0286() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(3/2)/(c - c*Sin[e + f*x])^(11/2), x]", //
 "(a*Cos[e + f*x]*Sqrt[a + a*Sin[e + f*x]])/(5*f*(c - c*Sin[e + f*x])^(11/2)) - (a^2*Cos[e + f*x])/(20*c*f*Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(9/2))", //
2818, 2817);}

// {2819, 2817}
public void test0287() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(5/2)*(c - c*Sin[e + f*x])^(3/2), x]", //
 "(c^2*Cos[e + f*x]*(a + a*Sin[e + f*x])^(5/2))/(6*f*Sqrt[c - c*Sin[e + f*x]]) + (c*Cos[e + f*x]*(a + a*Sin[e + f*x])^(5/2)*Sqrt[c - c*Sin[e + f*x]])/(4*f)", //
2819, 2817);}

// {2822, 2821}
public void test0288() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(5/2)/(c - c*Sin[e + f*x])^(9/2), x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(5/2))/(8*f*(c - c*Sin[e + f*x])^(9/2)) + (Cos[e + f*x]*(a + a*Sin[e + f*x])^(5/2))/(48*c*f*(c - c*Sin[e + f*x])^(7/2))", //
2822, 2821);}

// {2819, 2817}
public void test0289() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(7/2)*(c - c*Sin[e + f*x])^(3/2), x]", //
 "(c^2*Cos[e + f*x]*(a + a*Sin[e + f*x])^(7/2))/(10*f*Sqrt[c - c*Sin[e + f*x]]) + (c*Cos[e + f*x]*(a + a*Sin[e + f*x])^(7/2)*Sqrt[c - c*Sin[e + f*x]])/(5*f)", //
2819, 2817);}

// {2822, 2821}
public void test0290() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(7/2)/(c - c*Sin[e + f*x])^(11/2), x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(7/2))/(10*f*(c - c*Sin[e + f*x])^(11/2)) + (Cos[e + f*x]*(a + a*Sin[e + f*x])^(7/2))/(80*c*f*(c - c*Sin[e + f*x])^(9/2))", //
2822, 2821);}

// {2820, 3855}
public void test0291() {
	check(//
"Integrate[1/(Sqrt[a + a*Sin[e + f*x]]*Sqrt[c - c*Sin[e + f*x]]), x]", //
 "(ArcTanh[Sin[e + f*x]]*Cos[e + f*x])/(f*Sqrt[a + a*Sin[e + f*x]]*Sqrt[c - c*Sin[e + f*x]])", //
2820, 3855);}

// {2819, 2817}
public void test0292() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(3/2), x]", //
 "(8*c^2*Cos[e + f*x]*(a + a*Sin[e + f*x])^m)/(f*(3 + 8*m + 4*m^2)*Sqrt[c - c*Sin[e + f*x]]) + (2*c*Cos[e + f*x]*(a + a*Sin[e + f*x])^m*Sqrt[c - c*Sin[e + f*x]])/(f*(3 + 2*m))", //
2819, 2817);}

// {2822, 2821}
public void test0293() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(-2 - m), x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(-2 - m))/(f*(3 + 2*m)) + (Cos[e + f*x]*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(-1 - m))/(c*f*(3 + 8*m + 4*m^2))", //
2822, 2821);}

// {2832, 2813}
public void test0294() {
	check(//
"Integrate[(a + a*Sin[e + f*x])*(c + d*Sin[e + f*x])^2, x]", //
 "(a*(2*c^2 + 2*c*d + d^2)*x)/2 - (2*a*(c^2 + 3*c*d + d^2)*Cos[e + f*x])/(3*f) - (a*d*(2*c + 3*d)*Cos[e + f*x]*Sin[e + f*x])/(6*f) - (a*Cos[e + f*x]*(c + d*Sin[e + f*x])^2)/(3*f)", //
2832, 2813);}

// {2718}
public void test0295() {
	check(//
"Integrate[a + a*Sin[e + f*x], x]", //
 "a*x - (a*Cos[e + f*x])/f", //
2718);}

// {2830, 2723}
public void test0296() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^2*(c + d*Sin[e + f*x]), x]", //
 "(a^2*(3*c + 2*d)*x)/2 - (2*a^2*(3*c + 2*d)*Cos[e + f*x])/(3*f) - (a^2*(3*c + 2*d)*Cos[e + f*x]*Sin[e + f*x])/(6*f) - (d*Cos[e + f*x]*(a + a*Sin[e + f*x])^2)/(3*f)", //
2830, 2723);}

// {2846, 2813}
public void test0297() {
	check(//
"Integrate[(c + d*Sin[e + f*x])^3/(a + a*Sin[e + f*x]), x]", //
 "(3*d*(2*c^2 - 2*c*d + d^2)*x)/(2*a) + (2*d*(c^2 - 3*c*d + d^2)*Cos[e + f*x])/(a*f) + ((2*c - 3*d)*d^2*Cos[e + f*x]*Sin[e + f*x])/(2*a*f) - ((c - d)*Cos[e + f*x]*(c + d*Sin[e + f*x])^2)/(f*(a + a*Sin[e + f*x]))", //
2846, 2813);}

// {2814, 2727}
public void test0298() {
	check(//
"Integrate[(c + d*Sin[e + f*x])/(a + a*Sin[e + f*x]), x]", //
 "(d*x)/a - ((c - d)*Cos[e + f*x])/(f*(a + a*Sin[e + f*x]))", //
2814, 2727);}

// {2829, 2727}
public void test0299() {
	check(//
"Integrate[(c + d*Sin[e + f*x])/(a + a*Sin[e + f*x])^2, x]", //
 "-((c - d)*Cos[e + f*x])/(3*f*(a + a*Sin[e + f*x])^2) - ((c + 2*d)*Cos[e + f*x])/(3*f*(a^2 + a^2*Sin[e + f*x]))", //
2829, 2727);}

// {2729, 2727}
public void test0300() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(-2), x]", //
 "-Cos[e + f*x]/(3*f*(a + a*Sin[e + f*x])^2) - Cos[e + f*x]/(3*f*(a^2 + a^2*Sin[e + f*x]))", //
2729, 2727);}

// {2830, 2725}
public void test0301() {
	check(//
"Integrate[Sqrt[a + a*Sin[e + f*x]]*(c + d*Sin[e + f*x]), x]", //
 "(-2*a*(3*c + d)*Cos[e + f*x])/(3*f*Sqrt[a + a*Sin[e + f*x]]) - (2*d*Cos[e + f*x]*Sqrt[a + a*Sin[e + f*x]])/(3*f)", //
2830, 2725);}

// {2852, 214}
public void test0302() {
	check(//
"Integrate[Sqrt[a + a*Sin[e + f*x]]/(c + d*Sin[e + f*x]), x]", //
 "(-2*Sqrt[a]*ArcTanh[(Sqrt[a]*Sqrt[d]*Cos[e + f*x])/(Sqrt[c + d]*Sqrt[a + a*Sin[e + f*x]])])/(Sqrt[d]*Sqrt[c + d]*f)", //
2852, 214);}

// {2726, 2725}
public void test0303() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^(3/2), x]", //
 "(-8*a^2*Cos[e + f*x])/(3*f*Sqrt[a + a*Sin[e + f*x]]) - (2*a*Cos[e + f*x]*Sqrt[a + a*Sin[e + f*x]])/(3*f)", //
2726, 2725);}

// {2728, 212}
public void test0304() {
	check(//
"Integrate[1/Sqrt[a + a*Sin[e + f*x]], x]", //
 "-((Sqrt[2]*ArcTanh[(Sqrt[a]*Cos[e + f*x])/(Sqrt[2]*Sqrt[a + a*Sin[e + f*x]])])/(Sqrt[a]*f))", //
2728, 212);}

// {2854, 211}
public void test0305() {
	check(//
"Integrate[Sqrt[a + a*Sin[e + f*x]]/Sqrt[c + d*Sin[e + f*x]], x]", //
 "(-2*Sqrt[a]*ArcTan[(Sqrt[a]*Sqrt[d]*Cos[e + f*x])/(Sqrt[a + a*Sin[e + f*x]]*Sqrt[c + d*Sin[e + f*x]])])/(Sqrt[d]*f)", //
2854, 211);}

// {2851, 2850}
public void test0306() {
	check(//
"Integrate[Sqrt[a + a*Sin[e + f*x]]/(c + d*Sin[e + f*x])^(5/2), x]", //
 "(-2*a*Cos[e + f*x])/(3*(c + d)*f*Sqrt[a + a*Sin[e + f*x]]*(c + d*Sin[e + f*x])^(3/2)) - (4*a*Cos[e + f*x])/(3*(c + d)^2*f*Sqrt[a + a*Sin[e + f*x]]*Sqrt[c + d*Sin[e + f*x]])", //
2851, 2850);}

// {2861, 214}
public void test0307() {
	check(//
"Integrate[1/(Sqrt[a + a*Sin[e + f*x]]*Sqrt[c + d*Sin[e + f*x]]), x]", //
 "-((Sqrt[2]*ArcTanh[(Sqrt[a]*Sqrt[c - d]*Cos[e + f*x])/(Sqrt[2]*Sqrt[a + a*Sin[e + f*x]]*Sqrt[c + d*Sin[e + f*x]])])/(Sqrt[a]*Sqrt[c - d]*f))", //
2861, 214);}

// {2731, 2730}
public void test0308() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^m, x]", //
 "-((2^(1/2 + m)*Cos[e + f*x]*Hypergeometric2F1[1/2, 1/2 - m, 3/2, (1 - Sin[e + f*x])/2]*(1 + Sin[e + f*x])^(-1/2 - m)*(a + a*Sin[e + f*x])^m)/f)", //
2731, 2730);}

// {22, 2727}
public void test0309() {
	check(//
"Integrate[(1 + Sin[e + f*x])^m*(3 + 3*Sin[e + f*x])^(-1 - m), x]", //
 "-((3^(-1 - m)*Cos[e + f*x])/(f*(1 + Sin[e + f*x])))", //
22, 2727);}

// {2867, 134}
public void test0310() {
	check(//
"Integrate[(1 + Sin[e + f*x])^m*(3 + 2*Sin[e + f*x])^(-1 - m), x]", //
 "-((2^(1/2 + m)*5^(-1/2 - m)*Cos[e + f*x]*Hypergeometric2F1[1/2, 1/2 - m, 3/2, (1 - Sin[e + f*x])/(2*(3 + 2*Sin[e + f*x]))]*(1 + Sin[e + f*x])^(-1 + m)*((1 + Sin[e + f*x])/(3 + 2*Sin[e + f*x]))^(1/2 - m))/(f*(3 + 2*Sin[e + f*x])^m))", //
2867, 134);}

// {2867, 134}
public void test0311() {
	check(//
"Integrate[(1 + Sin[e + f*x])^m*(3 + Sin[e + f*x])^(-1 - m), x]", //
 "-((2^(-1/2 - m)*Cos[e + f*x]*Hypergeometric2F1[1/2, 1/2 - m, 3/2, (1 - Sin[e + f*x])/(3 + Sin[e + f*x])]*(1 + Sin[e + f*x])^(-1 + m)*((1 + Sin[e + f*x])/(3 + Sin[e + f*x]))^(1/2 - m))/(f*(3 + Sin[e + f*x])^m))", //
2867, 134);}

// {12, 2730}
public void test0312() {
	check(//
"Integrate[3^(-1 - m)*(1 + Sin[e + f*x])^m, x]", //
 "-((2^(1/2 + m)*3^(-1 - m)*Cos[e + f*x]*Hypergeometric2F1[1/2, 1/2 - m, 3/2, (1 - Sin[e + f*x])/2])/(f*Sqrt[1 + Sin[e + f*x]]))", //
12, 2730);}

// {2867, 134}
public void test0313() {
	check(//
"Integrate[(3 - Sin[e + f*x])^(-1 - m)*(1 + Sin[e + f*x])^m, x]", //
 "-((Cos[e + f*x]*Hypergeometric2F1[1/2, 1 + m, 3/2, (-2*(1 - Sin[e + f*x]))/(1 + Sin[e + f*x])]*(3 - Sin[e + f*x])^(-1 - m)*((3 - Sin[e + f*x])/(1 + Sin[e + f*x]))^(1 + m)*(1 + Sin[e + f*x])^m)/f)", //
2867, 134);}

// {2867, 134}
public void test0314() {
	check(//
"Integrate[(3 - 2*Sin[e + f*x])^(-1 - m)*(1 + Sin[e + f*x])^m, x]", //
 "(Cos[e + f*x]*Hypergeometric2F1[1/2, -m, 1 - m, (2*(3 - 2*Sin[e + f*x]))/(1 + Sin[e + f*x])]*Sqrt[-((1 - Sin[e + f*x])/(1 + Sin[e + f*x]))]*(1 + Sin[e + f*x])^m)/(Sqrt[5]*f*m*(3 - 2*Sin[e + f*x])^m*(1 - Sin[e + f*x]))", //
2867, 134);}

// {23, 2727}
public void test0315() {
	check(//
"Integrate[(3 + 3*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "-((Cos[e + f*x]*(3 + 3*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m)/f)", //
23, 2727);}

// {2867, 134}
public void test0316() {
	check(//
"Integrate[(3 - 4*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "(Cos[e + f*x]*Hypergeometric2F1[1/2, -m, 1 - m, (-2*(3 - 4*Sin[e + f*x]))/(1 + Sin[e + f*x])]*Sqrt[(1 - Sin[e + f*x])/(1 + Sin[e + f*x])]*(a + a*Sin[e + f*x])^m)/(Sqrt[7]*f*m*(3 - 4*Sin[e + f*x])^m*(1 - Sin[e + f*x]))", //
2867, 134);}

// {2867, 134}
public void test0317() {
	check(//
"Integrate[(3 - 5*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "(Cos[e + f*x]*Hypergeometric2F1[1/2, -m, 1 - m, -((3 - 5*Sin[e + f*x])/(1 + Sin[e + f*x]))]*Sqrt[(1 - Sin[e + f*x])/(1 + Sin[e + f*x])]*(a + a*Sin[e + f*x])^m)/(4*f*m*(3 - 5*Sin[e + f*x])^m*(1 - Sin[e + f*x]))", //
2867, 134);}

// {2867, 134}
public void test0318() {
	check(//
"Integrate[(-3 + 2*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "-((Cos[e + f*x]*Hypergeometric2F1[1/2, -m, 1 - m, (2*(3 - 2*Sin[e + f*x]))/(1 + Sin[e + f*x])]*Sqrt[-((1 - Sin[e + f*x])/(1 + Sin[e + f*x]))]*(a + a*Sin[e + f*x])^m)/(Sqrt[5]*f*m*(1 - Sin[e + f*x])*(-3 + 2*Sin[e + f*x])^m))", //
2867, 134);}

// {2867, 134}
public void test0319() {
	check(//
"Integrate[(-3 + Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "-(Cos[e + f*x]*Hypergeometric2F1[1/2, -m, 1 - m, (3 - Sin[e + f*x])/(1 + Sin[e + f*x])]*Sqrt[-((1 - Sin[e + f*x])/(1 + Sin[e + f*x]))]*(a + a*Sin[e + f*x])^m)/(2*Sqrt[2]*f*m*(1 - Sin[e + f*x])*(-3 + Sin[e + f*x])^m)", //
2867, 134);}

// {2867, 134}
public void test0320() {
	check(//
"Integrate[(-3 - Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "-(Cos[e + f*x]*Hypergeometric2F1[1/2, -m, 1 - m, (3 + Sin[e + f*x])/(2*(1 + Sin[e + f*x]))]*Sqrt[-((1 - Sin[e + f*x])/(1 + Sin[e + f*x]))]*(a + a*Sin[e + f*x])^m)/(2*Sqrt[2]*f*m*(-3 - Sin[e + f*x])^m*(1 - Sin[e + f*x]))", //
2867, 134);}

// {2867, 134}
public void test0321() {
	check(//
"Integrate[(-3 - 2*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "-((Cos[e + f*x]*Hypergeometric2F1[1/2, -m, 1 - m, (2*(3 + 2*Sin[e + f*x]))/(5*(1 + Sin[e + f*x]))]*Sqrt[-((1 - Sin[e + f*x])/(1 + Sin[e + f*x]))]*(a + a*Sin[e + f*x])^m)/(Sqrt[5]*f*m*(-3 - 2*Sin[e + f*x])^m*(1 - Sin[e + f*x])))", //
2867, 134);}

// {23, 2727}
public void test0322() {
	check(//
"Integrate[(-3 - 3*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "-((Cos[e + f*x]*(-3 - 3*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m)/f)", //
23, 2727);}

// {2867, 134}
public void test0323() {
	check(//
"Integrate[(-3 - 4*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "(Cos[e + f*x]*Hypergeometric2F1[1/2, -m, 1 - m, (2*(3 + 4*Sin[e + f*x]))/(7*(1 + Sin[e + f*x]))]*Sqrt[(1 - Sin[e + f*x])/(1 + Sin[e + f*x])]*(a + a*Sin[e + f*x])^m)/(Sqrt[7]*f*m*(-3 - 4*Sin[e + f*x])^m*(1 - Sin[e + f*x]))", //
2867, 134);}

// {2867, 134}
public void test0324() {
	check(//
"Integrate[(-3 - 5*Sin[e + f*x])^(-1 - m)*(a + a*Sin[e + f*x])^m, x]", //
 "(Cos[e + f*x]*Hypergeometric2F1[1/2, -m, 1 - m, (3 + 5*Sin[e + f*x])/(4*(1 + Sin[e + f*x]))]*Sqrt[(1 - Sin[e + f*x])/(1 + Sin[e + f*x])]*(a + a*Sin[e + f*x])^m)/(4*f*m*(-3 - 5*Sin[e + f*x])^m*(1 - Sin[e + f*x]))", //
2867, 134);}

// {2867, 134}
public void test0325() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^m*(c + d*Sin[e + f*x])^(-1 - m), x]", //
 "-((2^(1/2 + m)*a*Cos[e + f*x]*Hypergeometric2F1[1/2, 1/2 - m, 3/2, ((c - d)*(1 - Sin[e + f*x]))/(2*(c + d*Sin[e + f*x]))]*(a + a*Sin[e + f*x])^(-1 + m)*(((c + d)*(1 + Sin[e + f*x]))/(c + d*Sin[e + f*x]))^(1/2 - m))/((c + d)*f*(c + d*Sin[e + f*x])^m))", //
2867, 134);}

// {2832, 2813}
public void test0326() {
	check(//
"Integrate[(a + b*Sin[e + f*x])*(c + d*Sin[e + f*x])^2, x]", //
 "((2*b*c*d + a*(2*c^2 + d^2))*x)/2 - (2*(3*a*c*d + b*(c^2 + d^2))*Cos[e + f*x])/(3*f) - (d*(2*b*c + 3*a*d)*Cos[e + f*x]*Sin[e + f*x])/(6*f) - (b*Cos[e + f*x]*(c + d*Sin[e + f*x])^2)/(3*f)", //
2832, 2813);}

// {2718}
public void test0327() {
	check(//
"Integrate[a + b*Sin[e + f*x], x]", //
 "a*x - (b*Cos[e + f*x])/f", //
2718);}

// {2832, 2813}
public void test0328() {
	check(//
"Integrate[(a + b*Sin[e + f*x])^2*(c + d*Sin[e + f*x]), x]", //
 "((2*a^2*c + b^2*c + 2*a*b*d)*x)/2 - (2*(3*a*b*c + a^2*d + b^2*d)*Cos[e + f*x])/(3*f) - (b*(3*b*c + 2*a*d)*Cos[e + f*x]*Sin[e + f*x])/(6*f) - (d*Cos[e + f*x]*(a + b*Sin[e + f*x])^2)/(3*f)", //
2832, 2813);}

// {2735, 2813}
public void test0329() {
	check(//
"Integrate[(a + b*Sin[e + f*x])^3, x]", //
 "(a*(2*a^2 + 3*b^2)*x)/2 - (2*b*(4*a^2 + b^2)*Cos[e + f*x])/(3*f) - (5*a*b^2*Cos[e + f*x]*Sin[e + f*x])/(6*f) - (b*Cos[e + f*x]*(a + b*Sin[e + f*x])^2)/(3*f)", //
2735, 2813);}

// {21, 8}
public void test0330() {
	check(//
"Integrate[((a*B)/b + B*Sin[x])/(a + b*Sin[x]), x]", //
 "(B*x)/b", //
21, 8);}

// {2833, 8}
public void test0331() {
	check(//
"Integrate[(a + b*Sin[x])/(b + a*Sin[x])^2, x]", //
 "-(Cos[x]/(b + a*Sin[x]))", //
2833, 8);}

// {2814, 2736}
public void test0332() {
	check(//
"Integrate[(2 - Sin[x])/(2 + Sin[x]), x]", //
 "-x + (4*x)/Sqrt[3] + (8*ArcTan[Cos[x]/(2 + Sqrt[3] + Sin[x])])/Sqrt[3]", //
2814, 2736);}

// {2886, 2884}
public void test0333() {
	check(//
"Integrate[1/((a + b*Sin[e + f*x])*Sqrt[c + d*Sin[e + f*x]]), x]", //
 "(2*EllipticPi[(2*b)/(a + b), (e - Pi/2 + f*x)/2, (2*d)/(c + d)]*Sqrt[(c + d*Sin[e + f*x])/(c + d)])/((a + b)*f*Sqrt[c + d*Sin[e + f*x]])", //
2886, 2884);}

// {2920, 2817}
public void test0334() {
	check(//
"Integrate[(Cos[e + f*x]^2*Sqrt[a + a*Sin[e + f*x]])/Sqrt[c - c*Sin[e + f*x]], x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(3/2))/(2*a*f*Sqrt[c - c*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2821}
public void test0335() {
	check(//
"Integrate[(Cos[e + f*x]^2*Sqrt[a + a*Sin[e + f*x]])/(c - c*Sin[e + f*x])^(7/2), x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(3/2))/(4*a*c*f*(c - c*Sin[e + f*x])^(5/2))", //
2920, 2821);}

// {2920, 2817}
public void test0336() {
	check(//
"Integrate[(Cos[e + f*x]^2*(a + a*Sin[e + f*x])^(3/2))/Sqrt[c - c*Sin[e + f*x]], x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(5/2))/(3*a*f*Sqrt[c - c*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2821}
public void test0337() {
	check(//
"Integrate[(Cos[e + f*x]^2*(a + a*Sin[e + f*x])^(3/2))/(c - c*Sin[e + f*x])^(9/2), x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(5/2))/(6*a*c*f*(c - c*Sin[e + f*x])^(7/2))", //
2920, 2821);}

// {2920, 2817}
public void test0338() {
	check(//
"Integrate[(Cos[e + f*x]^2*(a + a*Sin[e + f*x])^(5/2))/Sqrt[c - c*Sin[e + f*x]], x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(7/2))/(4*a*f*Sqrt[c - c*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2821}
public void test0339() {
	check(//
"Integrate[(Cos[e + f*x]^2*(a + a*Sin[e + f*x])^(5/2))/(c - c*Sin[e + f*x])^(11/2), x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(7/2))/(8*a*c*f*(c - c*Sin[e + f*x])^(9/2))", //
2920, 2821);}

// {2920, 2817}
public void test0340() {
	check(//
"Integrate[(Cos[e + f*x]^2*(a + a*Sin[e + f*x])^(7/2))/Sqrt[c - c*Sin[e + f*x]], x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(9/2))/(5*a*f*Sqrt[c - c*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2821}
public void test0341() {
	check(//
"Integrate[(Cos[e + f*x]^2*(a + a*Sin[e + f*x])^(7/2))/(c - c*Sin[e + f*x])^(13/2), x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(9/2))/(10*a*c*f*(c - c*Sin[e + f*x])^(11/2))", //
2920, 2821);}

// {2920, 2817}
public void test0342() {
	check(//
"Integrate[(Cos[e + f*x]^2*(c - c*Sin[e + f*x])^(5/2))/Sqrt[a + a*Sin[e + f*x]], x]", //
 "-(Cos[e + f*x]*(c - c*Sin[e + f*x])^(7/2))/(4*c*f*Sqrt[a + a*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2817}
public void test0343() {
	check(//
"Integrate[(Cos[e + f*x]^2*(c - c*Sin[e + f*x])^(3/2))/Sqrt[a + a*Sin[e + f*x]], x]", //
 "-(Cos[e + f*x]*(c - c*Sin[e + f*x])^(5/2))/(3*c*f*Sqrt[a + a*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2817}
public void test0344() {
	check(//
"Integrate[(Cos[e + f*x]^2*Sqrt[c - c*Sin[e + f*x]])/Sqrt[a + a*Sin[e + f*x]], x]", //
 "-(Cos[e + f*x]*(c - c*Sin[e + f*x])^(3/2))/(2*c*f*Sqrt[a + a*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2817}
public void test0345() {
	check(//
"Integrate[Cos[e + f*x]^2/(Sqrt[a + a*Sin[e + f*x]]*Sqrt[c - c*Sin[e + f*x]]), x]", //
 "-((Cos[e + f*x]*Sqrt[c - c*Sin[e + f*x]])/(c*f*Sqrt[a + a*Sin[e + f*x]]))", //
2920, 2817);}

// {2920, 2817}
public void test0346() {
	check(//
"Integrate[Cos[e + f*x]^2/(Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(5/2)), x]", //
 "Cos[e + f*x]/(c*f*Sqrt[a + a*Sin[e + f*x]]*(c - c*Sin[e + f*x])^(3/2))", //
2920, 2817);}

// {2920, 2817}
public void test0347() {
	check(//
"Integrate[Cos[e + f*x]^2/((a + a*Sin[e + f*x])^(5/2)*Sqrt[c - c*Sin[e + f*x]]), x]", //
 "-(Cos[e + f*x]/(a*f*(a + a*Sin[e + f*x])^(3/2)*Sqrt[c - c*Sin[e + f*x]]))", //
2920, 2817);}

// {2920, 2817}
public void test0348() {
	check(//
"Integrate[(Cos[e + f*x]^2*(a + a*Sin[e + f*x])^m)/Sqrt[c - c*Sin[e + f*x]], x]", //
 "(2*Cos[e + f*x]*(a + a*Sin[e + f*x])^(1 + m))/(a*f*(3 + 2*m)*Sqrt[c - c*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2817}
public void test0349() {
	check(//
"Integrate[(Cos[e + f*x]^2*(a + a*Sin[e + f*x])^m)/Sqrt[c - c*Sin[e + f*x]], x]", //
 "(2*Cos[e + f*x]*(a + a*Sin[e + f*x])^(1 + m))/(a*f*(3 + 2*m)*Sqrt[c - c*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2817}
public void test0350() {
	check(//
"Integrate[(Cos[e + f*x]^2*(c + c*Sin[e + f*x])^m)/Sqrt[a - a*Sin[e + f*x]], x]", //
 "(2*Cos[e + f*x]*(c + c*Sin[e + f*x])^(1 + m))/(c*f*(3 + 2*m)*Sqrt[a - a*Sin[e + f*x]])", //
2920, 2817);}

// {2920, 2821}
public void test0351() {
	check(//
"Integrate[Cos[e + f*x]^2*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(-3 - m), x]", //
 "(Cos[e + f*x]*(a + a*Sin[e + f*x])^(1 + m)*(c - c*Sin[e + f*x])^(-2 - m))/(a*c*f*(3 + 2*m))", //
2920, 2821);}

// {2925, 2923}
public void test0352() {
	check(//
"Integrate[(g*Cos[e + f*x])^(3 - 2*m)*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^n, x]", //
 "(-2*a^2*(g*Cos[e + f*x])^(4 - 2*m)*(a + a*Sin[e + f*x])^(-2 + m)*(c - c*Sin[e + f*x])^n)/(f*g*(2 - m + n)*(3 - m + n)) - (a*(g*Cos[e + f*x])^(4 - 2*m)*(a + a*Sin[e + f*x])^(-1 + m)*(c - c*Sin[e + f*x])^n)/(f*g*(3 - m + n))", //
2925, 2923);}

// {2928, 2927}
public void test0353() {
	check(//
"Integrate[(g*Cos[e + f*x])^(-1 - m - n)*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(-1 + n), x]", //
 "((g*Cos[e + f*x])^(-m - n)*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(-1 + n))/(f*g*(2 + m - n)) + ((g*Cos[e + f*x])^(-m - n)*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^n)/(c*f*g*(m - n)*(2 + m - n))", //
2928, 2927);}

// {2912, 66}
public void test0354() {
	check(//
"Integrate[(Cos[c + d*x]*Sin[c + d*x]^n)/(a + a*Sin[c + d*x]), x]", //
 "(Hypergeometric2F1[1, 1 + n, 2 + n, -Sin[c + d*x]]*Sin[c + d*x]^(1 + n))/(a*d*(1 + n))", //
2912, 66);}

// {2912, 66}
public void test0355() {
	check(//
"Integrate[(Cos[c + d*x]*Sin[c + d*x]^n)/(a + a*Sin[c + d*x])^2, x]", //
 "(Hypergeometric2F1[2, 1 + n, 2 + n, -Sin[c + d*x]]*Sin[c + d*x]^(1 + n))/(a^2*d*(1 + n))", //
2912, 66);}

// {2912, 66}
public void test0356() {
	check(//
"Integrate[(Cos[c + d*x]*Sin[c + d*x]^n)/(a + a*Sin[c + d*x])^3, x]", //
 "(Hypergeometric2F1[3, 1 + n, 2 + n, -Sin[c + d*x]]*Sin[c + d*x]^(1 + n))/(a^3*d*(1 + n))", //
2912, 66);}

// {2912, 66}
public void test0357() {
	check(//
"Integrate[(Cos[c + d*x]*Sin[c + d*x]^n)/(a + a*Sin[c + d*x])^4, x]", //
 "(Hypergeometric2F1[4, 1 + n, 2 + n, -Sin[c + d*x]]*Sin[c + d*x]^(1 + n))/(a^4*d*(1 + n))", //
2912, 66);}

// {2935, 2752}
public void test0358() {
	check(//
"Integrate[(Cos[c + d*x]^2*Sin[c + d*x])/Sqrt[a + a*Sin[c + d*x]], x]", //
 "(2*a*Cos[c + d*x]^3)/(15*d*(a + a*Sin[c + d*x])^(3/2)) - (2*Cos[c + d*x]^3)/(5*d*Sqrt[a + a*Sin[c + d*x]])", //
2935, 2752);}

// {2746}
public void test0359() {
	check(//
"Integrate[Cos[c + d*x]^3/(a + a*Sin[c + d*x]), x]", //
 "Sin[c + d*x]/(a*d) - Sin[c + d*x]^2/(2*a*d)", //
2746);}

// {2938, 2750}
public void test0360() {
	check(//
"Integrate[(Cos[e + f*x]^4*Sin[e + f*x])/(a + a*Sin[e + f*x])^6, x]", //
 "Cos[e + f*x]^5/(7*f*(a + a*Sin[e + f*x])^6) - (6*Cos[e + f*x]^5)/(35*a*f*(a + a*Sin[e + f*x])^5)", //
2938, 2750);}

// {2935, 2752}
public void test0361() {
	check(//
"Integrate[(Cos[c + d*x]^4*Sin[c + d*x])/(a + a*Sin[c + d*x])^(3/2), x]", //
 "(6*a*Cos[c + d*x]^5)/(35*d*(a + a*Sin[c + d*x])^(5/2)) - (2*Cos[c + d*x]^5)/(7*d*(a + a*Sin[c + d*x])^(3/2))", //
2935, 2752);}

// {2934, 2723}
public void test0362() {
	check(//
"Integrate[Sec[c + d*x]*(a + a*Sin[c + d*x])^3*Tan[c + d*x], x]", //
 "(-9*a^3*x)/2 + (6*a^3*Cos[c + d*x])/d + (3*a^3*Cos[c + d*x]*Sin[c + d*x])/(2*d) + (Sec[c + d*x]*(a + a*Sin[c + d*x])^3)/d", //
2934, 2723);}

// {2912, 70}
public void test0363() {
	check(//
"Integrate[(Cos[e + f*x]*(c + d*Sin[e + f*x])^n)/(a + a*Sin[e + f*x]), x]", //
 "-((Hypergeometric2F1[1, 1 + n, 2 + n, (c + d*Sin[e + f*x])/(c - d)]*(c + d*Sin[e + f*x])^(1 + n))/(a*(c - d)*f*(1 + n)))", //
2912, 70);}

// {2912, 70}
public void test0364() {
	check(//
"Integrate[(Cos[e + f*x]*(c + d*Sin[e + f*x])^n)/(a + a*Sin[e + f*x])^2, x]", //
 "(d*Hypergeometric2F1[2, 1 + n, 2 + n, (c + d*Sin[e + f*x])/(c - d)]*(c + d*Sin[e + f*x])^(1 + n))/(a^2*(c - d)^2*f*(1 + n))", //
2912, 70);}

// {2912, 70}
public void test0365() {
	check(//
"Integrate[(Cos[e + f*x]*(c + d*Sin[e + f*x])^n)/(a + a*Sin[e + f*x])^3, x]", //
 "-((d^2*Hypergeometric2F1[3, 1 + n, 2 + n, (c + d*Sin[e + f*x])/(c - d)]*(c + d*Sin[e + f*x])^(1 + n))/(a^3*(c - d)^3*f*(1 + n)))", //
2912, 70);}

// {2912, 70}
public void test0366() {
	check(//
"Integrate[(Cos[e + f*x]*(a + a*Sin[e + f*x])^m)/(c + d*Sin[e + f*x]), x]", //
 "(Hypergeometric2F1[1, 1 + m, 2 + m, -((d*(1 + Sin[e + f*x]))/(c - d))]*(a + a*Sin[e + f*x])^(1 + m))/(a*(c - d)*f*(1 + m))", //
2912, 70);}

// {2912, 70}
public void test0367() {
	check(//
"Integrate[(Cos[e + f*x]*(a + a*Sin[e + f*x])^m)/(c + d*Sin[e + f*x])^2, x]", //
 "(Hypergeometric2F1[2, 1 + m, 2 + m, -((d*(1 + Sin[e + f*x]))/(c - d))]*(a + a*Sin[e + f*x])^(1 + m))/(a*(c - d)^2*f*(1 + m))", //
2912, 70);}

// {2912, 70}
public void test0368() {
	check(//
"Integrate[(Cos[e + f*x]*(a + a*Sin[e + f*x])^m)/(c + d*Sin[e + f*x])^3, x]", //
 "(Hypergeometric2F1[3, 1 + m, 2 + m, -((d*(1 + Sin[e + f*x]))/(c - d))]*(a + a*Sin[e + f*x])^(1 + m))/(a*(c - d)^3*f*(1 + m))", //
2912, 70);}

// {2786, 67}
public void test0369() {
	check(//
"Integrate[Cot[c + d*x]*(a + a*Sin[c + d*x])^m, x]", //
 "-((Hypergeometric2F1[1, 1 + m, 2 + m, 1 + Sin[c + d*x]]*(a + a*Sin[c + d*x])^(1 + m))/(a*d*(1 + m)))", //
2786, 67);}

// {2934, 8}
public void test0370() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + a*Sin[c + d*x])*(A + B*Sin[c + d*x]), x]", //
 "-(a*B*x) + ((A + B)*Sec[c + d*x]*(a + a*Sin[c + d*x]))/d", //
2934, 8);}

// {2915, 37}
public void test0371() {
	check(//
"Integrate[Sec[c + d*x]^5*(a + a*Sin[c + d*x])^3*(A + B*Sin[c + d*x]), x]", //
 "(a^3*(a*A + a*B*Sin[c + d*x])^2)/(2*(A + B)*d*(a - a*Sin[c + d*x])^2)", //
2915, 37);}

// {2934, 2723}
public void test0372() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + a*Sin[c + d*x])^3*(A + B*Sin[c + d*x]), x]", //
 "(-3*a^3*(2*A + 3*B)*x)/2 + (2*a^3*(2*A + 3*B)*Cos[c + d*x])/d + (a^3*(2*A + 3*B)*Cos[c + d*x]*Sin[c + d*x])/(2*d) + ((A + B)*Sec[c + d*x]*(a + a*Sin[c + d*x])^3)/d", //
2934, 2723);}

// {2938, 2750}
public void test0373() {
	check(//
"Integrate[(g*Cos[e + f*x])^p*(A + B*Sin[e + f*x])*(c - c*Sin[e + f*x])^(-2 - p), x]", //
 "((A + B)*(g*Cos[e + f*x])^(1 + p)*(c - c*Sin[e + f*x])^(-2 - p))/(f*g*(3 + p)) + ((A - B*(2 + p))*(g*Cos[e + f*x])^(1 + p)*(c - c*Sin[e + f*x])^(-1 - p))/(c*f*g*(1 + p)*(3 + p))", //
2938, 2750);}

// {2967, 2895}
public void test0374() {
	check(//
"Integrate[(Sec[e + f*x]^2*Sqrt[a + b*Sin[e + f*x]])/Sqrt[d*Sin[e + f*x]], x]", //
 "(Sec[e + f*x]*Sqrt[d*Sin[e + f*x]]*Sqrt[a + b*Sin[e + f*x]])/(d*f) - (Sqrt[a + b]*Sqrt[(a*(1 - Csc[e + f*x]))/(a + b)]*Sqrt[(a*(1 + Csc[e + f*x]))/(a - b)]*EllipticF[ArcSin[(Sqrt[d]*Sqrt[a + b*Sin[e + f*x]])/(Sqrt[a + b]*Sqrt[d*Sin[e + f*x]])], -((a + b)/(a - b))]*Tan[e + f*x])/(Sqrt[d]*f)", //
2967, 2895);}

// {2783, 143}
public void test0375() {
	check(//
"Integrate[Cos[e + f*x]^2*(c + d*Sin[e + f*x])^(4/3), x]", //
 "(3*AppellF1[7/3, -1/2, -1/2, 10/3, (c + d*Sin[e + f*x])/(c - d), (c + d*Sin[e + f*x])/(c + d)]*Cos[e + f*x]*(c + d*Sin[e + f*x])^(7/3))/(7*d*f*Sqrt[1 - (c + d*Sin[e + f*x])/(c - d)]*Sqrt[1 - (c + d*Sin[e + f*x])/(c + d)])", //
2783, 143);}

// {2783, 143}
public void test0376() {
	check(//
"Integrate[Cos[e + f*x]^2*(c + d*Sin[e + f*x])^n, x]", //
 "(AppellF1[1 + n, -1/2, -1/2, 2 + n, (c + d*Sin[e + f*x])/(c - d), (c + d*Sin[e + f*x])/(c + d)]*Cos[e + f*x]*(c + d*Sin[e + f*x])^(1 + n))/(d*f*(1 + n)*Sqrt[1 - (c + d*Sin[e + f*x])/(c - d)]*Sqrt[1 - (c + d*Sin[e + f*x])/(c + d)])", //
2783, 143);}

// {3027, 3556}
public void test0377() {
	check(//
"Integrate[Csc[e + f*x]*Sqrt[a + a*Sin[e + f*x]]*Sqrt[c - c*Sin[e + f*x]], x]", //
 "(Log[Sin[e + f*x]]*Sec[e + f*x]*Sqrt[a + a*Sin[e + f*x]]*Sqrt[c - c*Sin[e + f*x]])/f", //
3027, 3556);}

// {3009, 211}
public void test0378() {
	check(//
"Integrate[Sqrt[a + a*Sin[e + f*x]]/(Sqrt[g*Sin[e + f*x]]*(c + d*Sin[e + f*x])), x]", //
 "(-2*Sqrt[a]*ArcTan[(Sqrt[a]*Sqrt[c]*Sqrt[g]*Cos[e + f*x])/(Sqrt[c + d]*Sqrt[g*Sin[e + f*x]]*Sqrt[a + a*Sin[e + f*x]])])/(Sqrt[c]*Sqrt[c + d]*f*Sqrt[g])", //
3009, 211);}

// {3022, 212}
public void test0379() {
	check(//
"Integrate[(Csc[e + f*x]*Sqrt[a + a*Sin[e + f*x]])/Sqrt[c + d*Sin[e + f*x]], x]", //
 "(-2*Sqrt[a]*ArcTanh[(Sqrt[a]*Sqrt[c]*Cos[e + f*x])/(Sqrt[a + a*Sin[e + f*x]]*Sqrt[c + d*Sin[e + f*x]])])/(Sqrt[c]*f)", //
3022, 212);}

// {3051, 2821}
public void test0380() {
	check(//
"Integrate[((a + a*Sin[e + f*x])^(3/2)*(A + B*Sin[e + f*x]))/(c - c*Sin[e + f*x])^(7/2), x]", //
 "((A + B)*Cos[e + f*x]*(a + a*Sin[e + f*x])^(3/2))/(6*f*(c - c*Sin[e + f*x])^(7/2)) + ((A - 5*B)*Cos[e + f*x]*(a + a*Sin[e + f*x])^(3/2))/(24*c*f*(c - c*Sin[e + f*x])^(5/2))", //
3051, 2821);}

// {3051, 2821}
public void test0381() {
	check(//
"Integrate[((a + a*Sin[e + f*x])^(5/2)*(A + B*Sin[e + f*x]))/(c - c*Sin[e + f*x])^(9/2), x]", //
 "((A + B)*Cos[e + f*x]*(a + a*Sin[e + f*x])^(5/2))/(8*f*(c - c*Sin[e + f*x])^(9/2)) + ((A - 7*B)*Cos[e + f*x]*(a + a*Sin[e + f*x])^(5/2))/(48*c*f*(c - c*Sin[e + f*x])^(7/2))", //
3051, 2821);}

// {3051, 2821}
public void test0382() {
	check(//
"Integrate[((a + a*Sin[e + f*x])^(7/2)*(A + B*Sin[e + f*x]))/(c - c*Sin[e + f*x])^(11/2), x]", //
 "((A + B)*Cos[e + f*x]*(a + a*Sin[e + f*x])^(7/2))/(10*f*(c - c*Sin[e + f*x])^(11/2)) + ((A - 9*B)*Cos[e + f*x]*(a + a*Sin[e + f*x])^(7/2))/(80*c*f*(c - c*Sin[e + f*x])^(9/2))", //
3051, 2821);}

// {3051, 2821}
public void test0383() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^m*(A + B*Sin[e + f*x])*(c - c*Sin[e + f*x])^(-2 - m), x]", //
 "((A + B)*Cos[e + f*x]*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(-2 - m))/(f*(3 + 2*m)) + ((A - 2*B*(1 + m))*Cos[e + f*x]*(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^(-1 - m))/(c*f*(1 + 2*m)*(3 + 2*m))", //
3051, 2821);}

// {3046, 2933}
public void test0384() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^3*(c - c*Sin[e + f*x])^n*(B*(3 - n) - B*(4 + n)*Sin[e + f*x]), x]", //
 "(a^3*B*c^3*Cos[e + f*x]^7*(c - c*Sin[e + f*x])^(-3 + n))/f", //
3046, 2933);}

// {3046, 2933}
public void test0385() {
	check(//
"Integrate[(a - a*Sin[e + f*x])^3*(c + c*Sin[e + f*x])^n*(B*(3 - n) + B*(4 + n)*Sin[e + f*x]), x]", //
 "-((a^3*B*c^3*Cos[e + f*x]^7*(c + c*Sin[e + f*x])^(-3 + n))/f)", //
3046, 2933);}

// {3046, 2933}
public void test0386() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^m*(c - c*Sin[e + f*x])^3*(B*(-3 + m) - B*(4 + m)*Sin[e + f*x]), x]", //
 "(a^3*B*c^3*Cos[e + f*x]^7*(a + a*Sin[e + f*x])^(-3 + m))/f", //
3046, 2933);}

// {3046, 2933}
public void test0387() {
	check(//
"Integrate[(a - a*Sin[e + f*x])^m*(c + c*Sin[e + f*x])^3*(B*(-3 + m) + B*(4 + m)*Sin[e + f*x]), x]", //
 "-((a^3*B*c^3*Cos[e + f*x]^7*(a - a*Sin[e + f*x])^(-3 + m))/f)", //
3046, 2933);}

// {2830, 2723}
public void test0388() {
	check(//
"Integrate[(a + a*Sin[e + f*x])^2*(A + B*Sin[e + f*x]), x]", //
 "(a^2*(3*A + 2*B)*x)/2 - (2*a^2*(3*A + 2*B)*Cos[e + f*x])/(3*f) - (a^2*(3*A + 2*B)*Cos[e + f*x]*Sin[e + f*x])/(6*f) - (B*Cos[e + f*x]*(a + a*Sin[e + f*x])^2)/(3*f)", //
2830, 2723);}

// {3056, 2813}
public void test0389() {
	check(//
"Integrate[((A + B*Sin[e + f*x])*(c + d*Sin[e + f*x])^2)/(a + a*Sin[e + f*x]), x]", //
 "((2*B*c*(c - 2*d) + 4*A*c*d - (2*A - 3*B)*d^2)*x)/(2*a) + (2*(A*(c - d) - B*(2*c - d))*d*Cos[e + f*x])/(a*f) + ((2*A - 3*B)*d^2*Cos[e + f*x]*Sin[e + f*x])/(2*a*f) - ((A - B)*Cos[e + f*x]*(c + d*Sin[e + f*x])^2)/(f*(a + a*Sin[e + f*x]))", //
3056, 2813);}

// {2814, 2727}
public void test0390() {
	check(//
"Integrate[(A + B*Sin[e + f*x])/(a + a*Sin[e + f*x]), x]", //
 "(B*x)/a - ((A - B)*Cos[e + f*x])/(f*(a + a*Sin[e + f*x]))", //
2814, 2727);}

// {2829, 2727}
public void test0391() {
	check(//
"Integrate[(A + B*Sin[e + f*x])/(a + a*Sin[e + f*x])^2, x]", //
 "-((A - B)*Cos[e + f*x])/(3*f*(a + a*Sin[e + f*x])^2) - ((A + 2*B)*Cos[e + f*x])/(3*f*(a^2 + a^2*Sin[e + f*x]))", //
2829, 2727);}

// {2830, 2725}
public void test0392() {
	check(//
"Integrate[Sqrt[a + a*Sin[e + f*x]]*(A + B*Sin[e + f*x]), x]", //
 "(-2*a*(3*A + B)*Cos[e + f*x])/(3*f*Sqrt[a + a*Sin[e + f*x]]) - (2*B*Cos[e + f*x]*Sqrt[a + a*Sin[e + f*x]])/(3*f)", //
2830, 2725);}

// {3852, 8}
public void test0393() {
	check(//
"Integrate[-Csc[e + f*x]^2, x]", //
 "Cot[e + f*x]/f", //
3852, 8);}

// {3286, 2718}
public void test0394() {
	check(//
"Integrate[Sqrt[a*Sin[x]^2], x]", //
 "-(Cot[x]*Sqrt[a*Sin[x]^2])", //
3286, 2718);}

// {3286, 3855}
public void test0395() {
	check(//
"Integrate[1/Sqrt[a*Sin[x]^2], x]", //
 "-((ArcTanh[Cos[x]]*Sin[x])/Sqrt[a*Sin[x]^2])", //
3286, 3855);}

// {3287, 2722}
public void test0396() {
	check(//
"Integrate[(c*Sin[a + b*x]^m)^(5/2), x]", //
 "(2*c^2*Cos[a + b*x]*Hypergeometric2F1[1/2, (2 + 5*m)/4, (6 + 5*m)/4, Sin[a + b*x]^2]*Sin[a + b*x]^(1 + 2*m)*Sqrt[c*Sin[a + b*x]^m])/(b*(2 + 5*m)*Sqrt[Cos[a + b*x]^2])", //
3287, 2722);}

// {3287, 2722}
public void test0397() {
	check(//
"Integrate[(c*Sin[a + b*x]^m)^(3/2), x]", //
 "(2*c*Cos[a + b*x]*Hypergeometric2F1[1/2, (2 + 3*m)/4, (3*(2 + m))/4, Sin[a + b*x]^2]*Sin[a + b*x]^(1 + m)*Sqrt[c*Sin[a + b*x]^m])/(b*(2 + 3*m)*Sqrt[Cos[a + b*x]^2])", //
3287, 2722);}

// {3287, 2722}
public void test0398() {
	check(//
"Integrate[Sqrt[c*Sin[a + b*x]^m], x]", //
 "(2*Cos[a + b*x]*Hypergeometric2F1[1/2, (2 + m)/4, (6 + m)/4, Sin[a + b*x]^2]*Sin[a + b*x]*Sqrt[c*Sin[a + b*x]^m])/(b*(2 + m)*Sqrt[Cos[a + b*x]^2])", //
3287, 2722);}

// {3287, 2722}
public void test0399() {
	check(//
"Integrate[1/Sqrt[c*Sin[a + b*x]^m], x]", //
 "(2*Cos[a + b*x]*Hypergeometric2F1[1/2, (2 - m)/4, (6 - m)/4, Sin[a + b*x]^2]*Sin[a + b*x])/(b*(2 - m)*Sqrt[Cos[a + b*x]^2]*Sqrt[c*Sin[a + b*x]^m])", //
3287, 2722);}

// {3287, 2722}
public void test0400() {
	check(//
"Integrate[(c*Sin[a + b*x]^m)^(-3/2), x]", //
 "(2*Cos[a + b*x]*Hypergeometric2F1[1/2, (2 - 3*m)/4, (3*(2 - m))/4, Sin[a + b*x]^2]*Sin[a + b*x]^(1 - m))/(b*c*(2 - 3*m)*Sqrt[Cos[a + b*x]^2]*Sqrt[c*Sin[a + b*x]^m])", //
3287, 2722);}

// {3287, 2722}
public void test0401() {
	check(//
"Integrate[(c*Sin[a + b*x]^m)^(-5/2), x]", //
 "(2*Cos[a + b*x]*Hypergeometric2F1[1/2, (2 - 5*m)/4, (6 - 5*m)/4, Sin[a + b*x]^2]*Sin[a + b*x]^(1 - 2*m))/(b*c^2*(2 - 5*m)*Sqrt[Cos[a + b*x]^2]*Sqrt[c*Sin[a + b*x]^m])", //
3287, 2722);}

// {3287, 2722}
public void test0402() {
	check(//
"Integrate[(b*Sin[c + d*x]^n)^p, x]", //
 "(Cos[c + d*x]*Hypergeometric2F1[1/2, (1 + n*p)/2, (3 + n*p)/2, Sin[c + d*x]^2]*Sin[c + d*x]*(b*Sin[c + d*x]^n)^p)/(d*(1 + n*p)*Sqrt[Cos[c + d*x]^2])", //
3287, 2722);}

// {3286, 2722}
public void test0403() {
	check(//
"Integrate[(c*Sin[a + b*x]^2)^p, x]", //
 "(Cos[a + b*x]*Hypergeometric2F1[1/2, (1 + 2*p)/2, (3 + 2*p)/2, Sin[a + b*x]^2]*Sin[a + b*x]*(c*Sin[a + b*x]^2)^p)/(b*(1 + 2*p)*Sqrt[Cos[a + b*x]^2])", //
3286, 2722);}

// {3286, 2722}
public void test0404() {
	check(//
"Integrate[(c*Sin[a + b*x]^3)^p, x]", //
 "(Cos[a + b*x]*Hypergeometric2F1[1/2, (1 + 3*p)/2, (3*(1 + p))/2, Sin[a + b*x]^2]*Sin[a + b*x]*(c*Sin[a + b*x]^3)^p)/(b*(1 + 3*p)*Sqrt[Cos[a + b*x]^2])", //
3286, 2722);}

// {3286, 2722}
public void test0405() {
	check(//
"Integrate[(c*Sin[a + b*x]^4)^p, x]", //
 "(Cos[a + b*x]*Hypergeometric2F1[1/2, (1 + 4*p)/2, (3 + 4*p)/2, Sin[a + b*x]^2]*Sin[a + b*x]*(c*Sin[a + b*x]^4)^p)/(b*(1 + 4*p)*Sqrt[Cos[a + b*x]^2])", //
3286, 2722);}

// {3287, 2718}
public void test0406() {
	check(//
"Integrate[(c*Sin[a + b*x]^n)^n^(-1), x]", //
 "-((Cot[a + b*x]*(c*Sin[a + b*x]^n)^n^(-1))/b)", //
3287, 2718);}

// {3287, 2722}
public void test0407() {
	check(//
"Integrate[(a*(b*Sin[c + d*x])^p)^n, x]", //
 "(Cos[c + d*x]*Hypergeometric2F1[1/2, (1 + n*p)/2, (3 + n*p)/2, Sin[c + d*x]^2]*Sin[c + d*x]*(a*(b*Sin[c + d*x])^p)^n)/(d*(1 + n*p)*Sqrt[Cos[c + d*x]^2])", //
3287, 2722);}

// {3092}
public void test0408() {
	check(//
"Integrate[Sin[c + d*x]*(a + b*Sin[c + d*x]^2), x]", //
 "-(((a + b)*Cos[c + d*x])/d) + (b*Cos[c + d*x]^3)/(3*d)", //
3092);}

// {3093, 3855}
public void test0409() {
	check(//
"Integrate[Csc[c + d*x]*(a + b*Sin[c + d*x]^2), x]", //
 "-((a*ArcTanh[Cos[c + d*x]])/d) - (b*Cos[c + d*x])/d", //
3093, 3855);}

// {3091, 3855}
public void test0410() {
	check(//
"Integrate[Csc[c + d*x]^3*(a + b*Sin[c + d*x]^2), x]", //
 "-((a + 2*b)*ArcTanh[Cos[c + d*x]])/(2*d) - (a*Cot[c + d*x]*Csc[c + d*x])/(2*d)", //
3091, 3855);}

// {3091, 8}
public void test0411() {
	check(//
"Integrate[Csc[c + d*x]^2*(a + b*Sin[c + d*x]^2), x]", //
 "b*x - (a*Cot[c + d*x])/d", //
3091, 8);}

// {3259, 3248}
public void test0412() {
	check(//
"Integrate[(a + b*Sin[x]^2)^3, x]", //
 "((2*a + b)*(8*a^2 + 8*a*b + 5*b^2)*x)/16 - (b*(64*a^2 + 54*a*b + 15*b^2)*Cos[x]*Sin[x])/48 - (5*b^2*(2*a + b)*Cos[x]*Sin[x]^3)/24 - (b*Cos[x]*Sin[x]*(a + b*Sin[x]^2)^2)/6", //
3259, 3248);}

// {3265, 214}
public void test0413() {
	check(//
"Integrate[Sin[c + d*x]/(a + b*Sin[c + d*x]^2), x]", //
 "-(ArcTanh[(Sqrt[b]*Cos[c + d*x])/Sqrt[a + b]]/(Sqrt[b]*Sqrt[a + b]*d))", //
3265, 214);}

// {3260, 211}
public void test0414() {
	check(//
"Integrate[(a + b*Sin[c + d*x]^2)^(-1), x]", //
 "ArcTan[(Sqrt[a + b]*Tan[c + d*x])/Sqrt[a]]/(Sqrt[a]*Sqrt[a + b]*d)", //
3260, 211);}

// {3265, 222}
public void test0415() {
	check(//
"Integrate[Sin[x]/Sqrt[1 + Sin[x]^2], x]", //
 "-ArcSin[Cos[x]/Sqrt[2]]", //
3265, 222);}

// {3265, 222}
public void test0416() {
	check(//
"Integrate[Sin[7 + 3*x]/Sqrt[3 + Sin[7 + 3*x]^2], x]", //
 "-ArcSin[Cos[7 + 3*x]/2]/3", //
3265, 222);}

// {3257, 3256}
public void test0417() {
	check(//
"Integrate[Sqrt[a + b*Sin[e + f*x]^2], x]", //
 "(EllipticE[e + f*x, -(b/a)]*Sqrt[a + b*Sin[e + f*x]^2])/(f*Sqrt[1 + (b*Sin[e + f*x]^2)/a])", //
3257, 3256);}

// {3262, 3261}
public void test0418() {
	check(//
"Integrate[1/Sqrt[a + b*Sin[e + f*x]^2], x]", //
 "(EllipticF[e + f*x, -(b/a)]*Sqrt[1 + (b*Sin[e + f*x]^2)/a])/(f*Sqrt[a + b*Sin[e + f*x]^2])", //
3262, 3261);}

// {3265, 197}
public void test0419() {
	check(//
"Integrate[Sin[e + f*x]/(a + b*Sin[e + f*x]^2)^(3/2), x]", //
 "-(Cos[e + f*x]/((a + b)*f*Sqrt[a + b - b*Cos[e + f*x]^2]))", //
3265, 197);}

// {3294, 1117}
public void test0420() {
	check(//
"Integrate[Sin[c + d*x]/Sqrt[a + b*Sin[c + d*x]^4], x]", //
 "-((a + b)^(1/4)*(1 + (Sqrt[b]*Cos[c + d*x]^2)/Sqrt[a + b])*Sqrt[(a + b - 2*b*Cos[c + d*x]^2 + b*Cos[c + d*x]^4)/((a + b)*(1 + (Sqrt[b]*Cos[c + d*x]^2)/Sqrt[a + b])^2)]*EllipticF[2*ArcTan[(b^(1/4)*Cos[c + d*x])/(a + b)^(1/4)], (1 + Sqrt[b]/Sqrt[a + b])/2])/(2*b^(1/4)*d*Sqrt[a + b - 2*b*Cos[c + d*x]^2 + b*Cos[c + d*x]^4])", //
3294, 1117);}

// {3289, 1117}
public void test0421() {
	check(//
"Integrate[1/Sqrt[a + b*Sin[c + d*x]^4], x]", //
 "(Cos[c + d*x]^2*EllipticF[2*ArcTan[((a + b)^(1/4)*Tan[c + d*x])/a^(1/4)], (1 - Sqrt[a]/Sqrt[a + b])/2]*(Sqrt[a] + Sqrt[a + b]*Tan[c + d*x]^2)*Sqrt[(a + 2*a*Tan[c + d*x]^2 + (a + b)*Tan[c + d*x]^4)/(Sqrt[a] + Sqrt[a + b]*Tan[c + d*x]^2)^2])/(2*a^(1/4)*(a + b)^(1/4)*d*Sqrt[a + b*Sin[c + d*x]^4])", //
3289, 1117);}

// {3254, 2717}
public void test0422() {
	check(//
"Integrate[Cos[x]^3/(a - a*Sin[x]^2), x]", //
 "Sin[x]/a", //
3254, 2717);}

// {3254, 3855}
public void test0423() {
	check(//
"Integrate[Cos[x]/(a - a*Sin[x]^2), x]", //
 "ArcTanh[Sin[x]]/a", //
3254, 3855);}

// {3254, 8}
public void test0424() {
	check(//
"Integrate[Cos[x]^2/(a - a*Sin[x]^2), x]", //
 "x/a", //
3254, 8);}

// {3254, 2717}
public void test0425() {
	check(//
"Integrate[Cos[x]^5/(a - a*Sin[x]^2)^2, x]", //
 "Sin[x]/a^2", //
3254, 2717);}

// {3254, 3855}
public void test0426() {
	check(//
"Integrate[Cos[x]^3/(a - a*Sin[x]^2)^2, x]", //
 "ArcTanh[Sin[x]]/a^2", //
3254, 3855);}

// {3254, 8}
public void test0427() {
	check(//
"Integrate[Cos[x]^4/(a - a*Sin[x]^2)^2, x]", //
 "x/a^2", //
3254, 8);}

// {3270}
public void test0428() {
	check(//
"Integrate[Sec[e + f*x]^4*(a + b*Sin[e + f*x]^2), x]", //
 "(a*Tan[e + f*x])/f + ((a + b)*Tan[e + f*x]^3)/(3*f)", //
3270);}

// {3269, 211}
public void test0429() {
	check(//
"Integrate[Cos[x]/(a + b*Sin[x]^2), x]", //
 "ArcTan[(Sqrt[b]*Sin[x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b])", //
3269, 211);}

// {3257, 3256}
public void test0430() {
	check(//
"Integrate[Sqrt[a + b*Sin[e + f*x]^2], x]", //
 "(EllipticE[e + f*x, -(b/a)]*Sqrt[a + b*Sin[e + f*x]^2])/(f*Sqrt[1 + (b*Sin[e + f*x]^2)/a])", //
3257, 3256);}

// {3262, 3261}
public void test0431() {
	check(//
"Integrate[1/Sqrt[a + b*Sin[e + f*x]^2], x]", //
 "(EllipticF[e + f*x, -(b/a)]*Sqrt[1 + (b*Sin[e + f*x]^2)/a])/(f*Sqrt[a + b*Sin[e + f*x]^2])", //
3262, 3261);}

// {3269, 197}
public void test0432() {
	check(//
"Integrate[Cos[e + f*x]/(a + b*Sin[e + f*x]^2)^(3/2), x]", //
 "Sin[e + f*x]/(a*f*Sqrt[a + b*Sin[e + f*x]^2])", //
3269, 197);}

// {3257, 3256}
public void test0433() {
	check(//
"Integrate[Sqrt[a + b*Sin[e + f*x]^2], x]", //
 "(EllipticE[e + f*x, -(b/a)]*Sqrt[a + b*Sin[e + f*x]^2])/(f*Sqrt[1 + (b*Sin[e + f*x]^2)/a])", //
3257, 3256);}

// {3262, 3261}
public void test0434() {
	check(//
"Integrate[1/Sqrt[a + b*Sin[e + f*x]^2], x]", //
 "(EllipticF[e + f*x, -(b/a)]*Sqrt[1 + (b*Sin[e + f*x]^2)/a])/(f*Sqrt[a + b*Sin[e + f*x]^2])", //
3262, 3261);}

// {3273, 70}
public void test0435() {
	check(//
"Integrate[(a + b*Sin[c + d*x]^2)^p*Tan[c + d*x], x]", //
 "(Hypergeometric2F1[1, 1 + p, 2 + p, (a + b*Sin[c + d*x]^2)/(a + b)]*(a + b*Sin[c + d*x]^2)^(1 + p))/(2*(a + b)*d*(1 + p))", //
3273, 70);}

// {3273, 67}
public void test0436() {
	check(//
"Integrate[Cot[c + d*x]*(a + b*Sin[c + d*x]^2)^p, x]", //
 "-(Hypergeometric2F1[1, 1 + p, 2 + p, 1 + (b*Sin[c + d*x]^2)/a]*(a + b*Sin[c + d*x]^2)^(1 + p))/(2*a*d*(1 + p))", //
3273, 67);}

// {3289, 1117}
public void test0437() {
	check(//
"Integrate[1/Sqrt[a + b*Sin[c + d*x]^4], x]", //
 "(Cos[c + d*x]^2*EllipticF[2*ArcTan[((a + b)^(1/4)*Tan[c + d*x])/a^(1/4)], (1 - Sqrt[a]/Sqrt[a + b])/2]*(Sqrt[a] + Sqrt[a + b]*Tan[c + d*x]^2)*Sqrt[(a + 2*a*Tan[c + d*x]^2 + (a + b)*Tan[c + d*x]^4)/(Sqrt[a] + Sqrt[a + b]*Tan[c + d*x]^2)^2])/(2*a^(1/4)*(a + b)^(1/4)*d*Sqrt[a + b*Sin[c + d*x]^4])", //
3289, 1117);}

// {2715, 8}
public void test0438() {
	check(//
"Integrate[Cos[a + b*x]^2, x]", //
 "x/2 + (Cos[a + b*x]*Sin[a + b*x])/(2*b)", //
2715, 8);}

// {2713}
public void test0439() {
	check(//
"Integrate[Cos[a + b*x]^3, x]", //
 "Sin[a + b*x]/b - Sin[a + b*x]^3/(3*b)", //
2713);}

// {2713}
public void test0440() {
	check(//
"Integrate[Cos[a + b*x]^5, x]", //
 "Sin[a + b*x]/b - (2*Sin[a + b*x]^3)/(3*b) + Sin[a + b*x]^5/(5*b)", //
2713);}

// {2713}
public void test0441() {
	check(//
"Integrate[Cos[a + b*x]^7, x]", //
 "Sin[a + b*x]/b - Sin[a + b*x]^3/b + (3*Sin[a + b*x]^5)/(5*b) - Sin[a + b*x]^7/(7*b)", //
2713);}

// {2715, 2719}
public void test0442() {
	check(//
"Integrate[Cos[a + b*x]^(5/2), x]", //
 "(6*EllipticE[(a + b*x)/2, 2])/(5*b) + (2*Cos[a + b*x]^(3/2)*Sin[a + b*x])/(5*b)", //
2715, 2719);}

// {2715, 2720}
public void test0443() {
	check(//
"Integrate[Cos[a + b*x]^(3/2), x]", //
 "(2*EllipticF[(a + b*x)/2, 2])/(3*b) + (2*Sqrt[Cos[a + b*x]]*Sin[a + b*x])/(3*b)", //
2715, 2720);}

// {2716, 2719}
public void test0444() {
	check(//
"Integrate[Cos[a + b*x]^(-3/2), x]", //
 "(-2*EllipticE[(a + b*x)/2, 2])/b + (2*Sin[a + b*x])/(b*Sqrt[Cos[a + b*x]])", //
2716, 2719);}

// {2716, 2720}
public void test0445() {
	check(//
"Integrate[Cos[a + b*x]^(-5/2), x]", //
 "(2*EllipticF[(a + b*x)/2, 2])/(3*b) + (2*Sin[a + b*x])/(3*b*Cos[a + b*x]^(3/2))", //
2716, 2720);}

// {2721, 2719}
public void test0446() {
	check(//
"Integrate[Sqrt[c*Cos[a + b*x]], x]", //
 "(2*Sqrt[c*Cos[a + b*x]]*EllipticE[(a + b*x)/2, 2])/(b*Sqrt[Cos[a + b*x]])", //
2721, 2719);}

// {2721, 2720}
public void test0447() {
	check(//
"Integrate[1/Sqrt[c*Cos[a + b*x]], x]", //
 "(2*Sqrt[Cos[a + b*x]]*EllipticF[(a + b*x)/2, 2])/(b*Sqrt[c*Cos[a + b*x]])", //
2721, 2720);}

// {3286, 2717}
public void test0448() {
	check(//
"Integrate[Sqrt[a*Cos[x]^2], x]", //
 "Sqrt[a*Cos[x]^2]*Tan[x]", //
3286, 2717);}

// {3286, 3855}
public void test0449() {
	check(//
"Integrate[1/Sqrt[a*Cos[x]^2], x]", //
 "(ArcTanh[Sin[x]]*Cos[x])/Sqrt[a*Cos[x]^2]", //
3286, 3855);}

// {3287, 2722}
public void test0450() {
	check(//
"Integrate[(b*Cos[c + d*x]^m)^n, x]", //
 "-((Cos[c + d*x]*(b*Cos[c + d*x]^m)^n*Hypergeometric2F1[1/2, (1 + m*n)/2, (3 + m*n)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(1 + m*n)*Sqrt[Sin[c + d*x]^2]))", //
3287, 2722);}

// {3287, 2722}
public void test0451() {
	check(//
"Integrate[(c*Cos[a + b*x]^m)^(5/2), x]", //
 "(-2*c^2*Cos[a + b*x]^(1 + 2*m)*Sqrt[c*Cos[a + b*x]^m]*Hypergeometric2F1[1/2, (2 + 5*m)/4, (6 + 5*m)/4, Cos[a + b*x]^2]*Sin[a + b*x])/(b*(2 + 5*m)*Sqrt[Sin[a + b*x]^2])", //
3287, 2722);}

// {3287, 2722}
public void test0452() {
	check(//
"Integrate[(c*Cos[a + b*x]^m)^(3/2), x]", //
 "(-2*c*Cos[a + b*x]^(1 + m)*Sqrt[c*Cos[a + b*x]^m]*Hypergeometric2F1[1/2, (2 + 3*m)/4, (3*(2 + m))/4, Cos[a + b*x]^2]*Sin[a + b*x])/(b*(2 + 3*m)*Sqrt[Sin[a + b*x]^2])", //
3287, 2722);}

// {3287, 2722}
public void test0453() {
	check(//
"Integrate[Sqrt[c*Cos[a + b*x]^m], x]", //
 "(-2*Cos[a + b*x]*Sqrt[c*Cos[a + b*x]^m]*Hypergeometric2F1[1/2, (2 + m)/4, (6 + m)/4, Cos[a + b*x]^2]*Sin[a + b*x])/(b*(2 + m)*Sqrt[Sin[a + b*x]^2])", //
3287, 2722);}

// {3287, 2722}
public void test0454() {
	check(//
"Integrate[1/Sqrt[c*Cos[a + b*x]^m], x]", //
 "(-2*Cos[a + b*x]*Hypergeometric2F1[1/2, (2 - m)/4, (6 - m)/4, Cos[a + b*x]^2]*Sin[a + b*x])/(b*(2 - m)*Sqrt[c*Cos[a + b*x]^m]*Sqrt[Sin[a + b*x]^2])", //
3287, 2722);}

// {3287, 2722}
public void test0455() {
	check(//
"Integrate[(c*Cos[a + b*x]^m)^(-3/2), x]", //
 "(-2*Cos[a + b*x]^(1 - m)*Hypergeometric2F1[1/2, (2 - 3*m)/4, (3*(2 - m))/4, Cos[a + b*x]^2]*Sin[a + b*x])/(b*c*(2 - 3*m)*Sqrt[c*Cos[a + b*x]^m]*Sqrt[Sin[a + b*x]^2])", //
3287, 2722);}

// {3287, 2722}
public void test0456() {
	check(//
"Integrate[(c*Cos[a + b*x]^m)^(-5/2), x]", //
 "(-2*Cos[a + b*x]^(1 - 2*m)*Hypergeometric2F1[1/2, (2 - 5*m)/4, (6 - 5*m)/4, Cos[a + b*x]^2]*Sin[a + b*x])/(b*c^2*(2 - 5*m)*Sqrt[c*Cos[a + b*x]^m]*Sqrt[Sin[a + b*x]^2])", //
3287, 2722);}

// {3287, 2717}
public void test0457() {
	check(//
"Integrate[(c*Cos[a + b*x]^m)^m^(-1), x]", //
 "((c*Cos[a + b*x]^m)^m^(-1)*Tan[a + b*x])/b", //
3287, 2717);}

// {3287, 2722}
public void test0458() {
	check(//
"Integrate[(a*(b*Cos[c + d*x])^p)^n, x]", //
 "-((Cos[c + d*x]*(a*(b*Cos[c + d*x])^p)^n*Hypergeometric2F1[1/2, (1 + n*p)/2, (3 + n*p)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(1 + n*p)*Sqrt[Sin[c + d*x]^2]))", //
3287, 2722);}

// {2721, 2719}
public void test0459() {
	check(//
"Integrate[Sqrt[b*Cos[c + d*x]], x]", //
 "(2*Sqrt[b*Cos[c + d*x]]*EllipticE[(c + d*x)/2, 2])/(d*Sqrt[Cos[c + d*x]])", //
2721, 2719);}

// {2721, 2720}
public void test0460() {
	check(//
"Integrate[1/Sqrt[b*Cos[c + d*x]], x]", //
 "(2*Sqrt[Cos[c + d*x]]*EllipticF[(c + d*x)/2, 2])/(d*Sqrt[b*Cos[c + d*x]])", //
2721, 2720);}

// {17, 2717}
public void test0461() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]*Sqrt[b*Cos[c + d*x]], x]", //
 "(Sqrt[b*Cos[c + d*x]]*Sin[c + d*x])/(d*Sqrt[Cos[c + d*x]])", //
17, 2717);}

// {17, 8}
public void test0462() {
	check(//
"Integrate[Sqrt[b*Cos[c + d*x]]/Sqrt[Cos[c + d*x]], x]", //
 "(x*Sqrt[b*Cos[c + d*x]])/Sqrt[Cos[c + d*x]]", //
17, 8);}

// {17, 3855}
public void test0463() {
	check(//
"Integrate[Sqrt[b*Cos[c + d*x]]/Cos[c + d*x]^(3/2), x]", //
 "(ArcTanh[Sin[c + d*x]]*Sqrt[b*Cos[c + d*x]])/(d*Sqrt[Cos[c + d*x]])", //
17, 3855);}

// {17, 2717}
public void test0464() {
	check(//
"Integrate[(b*Cos[c + d*x])^(3/2)/Sqrt[Cos[c + d*x]], x]", //
 "(b*Sqrt[b*Cos[c + d*x]]*Sin[c + d*x])/(d*Sqrt[Cos[c + d*x]])", //
17, 2717);}

// {17, 8}
public void test0465() {
	check(//
"Integrate[(b*Cos[c + d*x])^(3/2)/Cos[c + d*x]^(3/2), x]", //
 "(b*x*Sqrt[b*Cos[c + d*x]])/Sqrt[Cos[c + d*x]]", //
17, 8);}

// {17, 3855}
public void test0466() {
	check(//
"Integrate[(b*Cos[c + d*x])^(3/2)/Cos[c + d*x]^(5/2), x]", //
 "(b*ArcTanh[Sin[c + d*x]]*Sqrt[b*Cos[c + d*x]])/(d*Sqrt[Cos[c + d*x]])", //
17, 3855);}

// {17, 2717}
public void test0467() {
	check(//
"Integrate[(b*Cos[c + d*x])^(5/2)/Cos[c + d*x]^(3/2), x]", //
 "(b^2*Sqrt[b*Cos[c + d*x]]*Sin[c + d*x])/(d*Sqrt[Cos[c + d*x]])", //
17, 2717);}

// {17, 8}
public void test0468() {
	check(//
"Integrate[(b*Cos[c + d*x])^(5/2)/Cos[c + d*x]^(5/2), x]", //
 "(b^2*x*Sqrt[b*Cos[c + d*x]])/Sqrt[Cos[c + d*x]]", //
17, 8);}

// {17, 3855}
public void test0469() {
	check(//
"Integrate[(b*Cos[c + d*x])^(5/2)/Cos[c + d*x]^(7/2), x]", //
 "(b^2*ArcTanh[Sin[c + d*x]]*Sqrt[b*Cos[c + d*x]])/(d*Sqrt[Cos[c + d*x]])", //
17, 3855);}

// {17, 2717}
public void test0470() {
	check(//
"Integrate[Cos[c + d*x]^(3/2)/Sqrt[b*Cos[c + d*x]], x]", //
 "(Sqrt[Cos[c + d*x]]*Sin[c + d*x])/(d*Sqrt[b*Cos[c + d*x]])", //
17, 2717);}

// {17, 8}
public void test0471() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]/Sqrt[b*Cos[c + d*x]], x]", //
 "(x*Sqrt[Cos[c + d*x]])/Sqrt[b*Cos[c + d*x]]", //
17, 8);}

// {18, 3855}
public void test0472() {
	check(//
"Integrate[1/(Sqrt[Cos[c + d*x]]*Sqrt[b*Cos[c + d*x]]), x]", //
 "(ArcTanh[Sin[c + d*x]]*Sqrt[Cos[c + d*x]])/(d*Sqrt[b*Cos[c + d*x]])", //
18, 3855);}

// {17, 2717}
public void test0473() {
	check(//
"Integrate[Cos[c + d*x]^(5/2)/(b*Cos[c + d*x])^(3/2), x]", //
 "(Sqrt[Cos[c + d*x]]*Sin[c + d*x])/(b*d*Sqrt[b*Cos[c + d*x]])", //
17, 2717);}

// {17, 8}
public void test0474() {
	check(//
"Integrate[Cos[c + d*x]^(3/2)/(b*Cos[c + d*x])^(3/2), x]", //
 "(x*Sqrt[Cos[c + d*x]])/(b*Sqrt[b*Cos[c + d*x]])", //
17, 8);}

// {17, 3855}
public void test0475() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]/(b*Cos[c + d*x])^(3/2), x]", //
 "(ArcTanh[Sin[c + d*x]]*Sqrt[Cos[c + d*x]])/(b*d*Sqrt[b*Cos[c + d*x]])", //
17, 3855);}

// {17, 2717}
public void test0476() {
	check(//
"Integrate[Cos[c + d*x]^(7/2)/(b*Cos[c + d*x])^(5/2), x]", //
 "(Sqrt[Cos[c + d*x]]*Sin[c + d*x])/(b^2*d*Sqrt[b*Cos[c + d*x]])", //
17, 2717);}

// {17, 8}
public void test0477() {
	check(//
"Integrate[Cos[c + d*x]^(5/2)/(b*Cos[c + d*x])^(5/2), x]", //
 "(x*Sqrt[Cos[c + d*x]])/(b^2*Sqrt[b*Cos[c + d*x]])", //
17, 8);}

// {17, 3855}
public void test0478() {
	check(//
"Integrate[Cos[c + d*x]^(3/2)/(b*Cos[c + d*x])^(5/2), x]", //
 "(ArcTanh[Sin[c + d*x]]*Sqrt[Cos[c + d*x]])/(b^2*d*Sqrt[b*Cos[c + d*x]])", //
17, 3855);}

// {20, 2722}
public void test0479() {
	check(//
"Integrate[Cos[c + d*x]^m*(b*Cos[c + d*x])^(1/3), x]", //
 "(-3*Cos[c + d*x]^(1 + m)*(b*Cos[c + d*x])^(1/3)*Hypergeometric2F1[1/2, (4 + 3*m)/6, (10 + 3*m)/6, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(4 + 3*m)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {16, 2722}
public void test0480() {
	check(//
"Integrate[Cos[c + d*x]^2*(b*Cos[c + d*x])^(1/3), x]", //
 "(-3*(b*Cos[c + d*x])^(10/3)*Hypergeometric2F1[1/2, 5/3, 8/3, Cos[c + d*x]^2]*Sin[c + d*x])/(10*b^3*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0481() {
	check(//
"Integrate[Cos[c + d*x]*(b*Cos[c + d*x])^(1/3), x]", //
 "(-3*(b*Cos[c + d*x])^(7/3)*Hypergeometric2F1[1/2, 7/6, 13/6, Cos[c + d*x]^2]*Sin[c + d*x])/(7*b^2*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0482() {
	check(//
"Integrate[(b*Cos[c + d*x])^(1/3)*Sec[c + d*x], x]", //
 "(-3*(b*Cos[c + d*x])^(1/3)*Hypergeometric2F1[1/6, 1/2, 7/6, Cos[c + d*x]^2]*Sin[c + d*x])/(d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0483() {
	check(//
"Integrate[(b*Cos[c + d*x])^(1/3)*Sec[c + d*x]^2, x]", //
 "(3*b*Hypergeometric2F1[-1/3, 1/2, 2/3, Cos[c + d*x]^2]*Sin[c + d*x])/(2*d*(b*Cos[c + d*x])^(2/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0484() {
	check(//
"Integrate[(b*Cos[c + d*x])^(1/3)*Sec[c + d*x]^3, x]", //
 "(3*b^2*Hypergeometric2F1[-5/6, 1/2, 1/6, Cos[c + d*x]^2]*Sin[c + d*x])/(5*d*(b*Cos[c + d*x])^(5/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {20, 2722}
public void test0485() {
	check(//
"Integrate[Cos[c + d*x]^m*(b*Cos[c + d*x])^(2/3), x]", //
 "(-3*Cos[c + d*x]^(1 + m)*(b*Cos[c + d*x])^(2/3)*Hypergeometric2F1[1/2, (5 + 3*m)/6, (11 + 3*m)/6, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(5 + 3*m)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {16, 2722}
public void test0486() {
	check(//
"Integrate[Cos[c + d*x]^2*(b*Cos[c + d*x])^(2/3), x]", //
 "(-3*(b*Cos[c + d*x])^(11/3)*Hypergeometric2F1[1/2, 11/6, 17/6, Cos[c + d*x]^2]*Sin[c + d*x])/(11*b^3*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0487() {
	check(//
"Integrate[Cos[c + d*x]*(b*Cos[c + d*x])^(2/3), x]", //
 "(-3*(b*Cos[c + d*x])^(8/3)*Hypergeometric2F1[1/2, 4/3, 7/3, Cos[c + d*x]^2]*Sin[c + d*x])/(8*b^2*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0488() {
	check(//
"Integrate[(b*Cos[c + d*x])^(2/3)*Sec[c + d*x], x]", //
 "(-3*(b*Cos[c + d*x])^(2/3)*Hypergeometric2F1[1/3, 1/2, 4/3, Cos[c + d*x]^2]*Sin[c + d*x])/(2*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0489() {
	check(//
"Integrate[(b*Cos[c + d*x])^(2/3)*Sec[c + d*x]^2, x]", //
 "(3*b*Hypergeometric2F1[-1/6, 1/2, 5/6, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(b*Cos[c + d*x])^(1/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0490() {
	check(//
"Integrate[(b*Cos[c + d*x])^(2/3)*Sec[c + d*x]^3, x]", //
 "(3*b^2*Hypergeometric2F1[-2/3, 1/2, 1/3, Cos[c + d*x]^2]*Sin[c + d*x])/(4*d*(b*Cos[c + d*x])^(4/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {20, 2722}
public void test0491() {
	check(//
"Integrate[Cos[c + d*x]^m*(b*Cos[c + d*x])^(4/3), x]", //
 "(-3*b*Cos[c + d*x]^(2 + m)*(b*Cos[c + d*x])^(1/3)*Hypergeometric2F1[1/2, (7 + 3*m)/6, (13 + 3*m)/6, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(7 + 3*m)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {16, 2722}
public void test0492() {
	check(//
"Integrate[Cos[c + d*x]^2*(b*Cos[c + d*x])^(4/3), x]", //
 "(-3*(b*Cos[c + d*x])^(13/3)*Hypergeometric2F1[1/2, 13/6, 19/6, Cos[c + d*x]^2]*Sin[c + d*x])/(13*b^3*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0493() {
	check(//
"Integrate[Cos[c + d*x]*(b*Cos[c + d*x])^(4/3), x]", //
 "(-3*(b*Cos[c + d*x])^(10/3)*Hypergeometric2F1[1/2, 5/3, 8/3, Cos[c + d*x]^2]*Sin[c + d*x])/(10*b^2*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0494() {
	check(//
"Integrate[(b*Cos[c + d*x])^(4/3)*Sec[c + d*x], x]", //
 "(-3*(b*Cos[c + d*x])^(4/3)*Hypergeometric2F1[1/2, 2/3, 5/3, Cos[c + d*x]^2]*Sin[c + d*x])/(4*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0495() {
	check(//
"Integrate[(b*Cos[c + d*x])^(4/3)*Sec[c + d*x]^2, x]", //
 "(-3*b*(b*Cos[c + d*x])^(1/3)*Hypergeometric2F1[1/6, 1/2, 7/6, Cos[c + d*x]^2]*Sin[c + d*x])/(d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0496() {
	check(//
"Integrate[(b*Cos[c + d*x])^(4/3)*Sec[c + d*x]^3, x]", //
 "(3*b^2*Hypergeometric2F1[-1/3, 1/2, 2/3, Cos[c + d*x]^2]*Sin[c + d*x])/(2*d*(b*Cos[c + d*x])^(2/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {20, 2722}
public void test0497() {
	check(//
"Integrate[Cos[c + d*x]^m/(b*Cos[c + d*x])^(1/3), x]", //
 "(-3*Cos[c + d*x]^(1 + m)*Hypergeometric2F1[1/2, (2 + 3*m)/6, (8 + 3*m)/6, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(2 + 3*m)*(b*Cos[c + d*x])^(1/3)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {16, 2722}
public void test0498() {
	check(//
"Integrate[Cos[c + d*x]^2/(b*Cos[c + d*x])^(1/3), x]", //
 "(-3*(b*Cos[c + d*x])^(8/3)*Hypergeometric2F1[1/2, 4/3, 7/3, Cos[c + d*x]^2]*Sin[c + d*x])/(8*b^3*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0499() {
	check(//
"Integrate[Cos[c + d*x]/(b*Cos[c + d*x])^(1/3), x]", //
 "(-3*(b*Cos[c + d*x])^(5/3)*Hypergeometric2F1[1/2, 5/6, 11/6, Cos[c + d*x]^2]*Sin[c + d*x])/(5*b^2*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0500() {
	check(//
"Integrate[Sec[c + d*x]/(b*Cos[c + d*x])^(1/3), x]", //
 "(3*Hypergeometric2F1[-1/6, 1/2, 5/6, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(b*Cos[c + d*x])^(1/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0501() {
	check(//
"Integrate[Sec[c + d*x]^2/(b*Cos[c + d*x])^(1/3), x]", //
 "(3*b*Hypergeometric2F1[-2/3, 1/2, 1/3, Cos[c + d*x]^2]*Sin[c + d*x])/(4*d*(b*Cos[c + d*x])^(4/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0502() {
	check(//
"Integrate[Sec[c + d*x]^3/(b*Cos[c + d*x])^(1/3), x]", //
 "(3*b^2*Hypergeometric2F1[-7/6, 1/2, -1/6, Cos[c + d*x]^2]*Sin[c + d*x])/(7*d*(b*Cos[c + d*x])^(7/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {20, 2722}
public void test0503() {
	check(//
"Integrate[Cos[c + d*x]^m/(b*Cos[c + d*x])^(2/3), x]", //
 "(-3*Cos[c + d*x]^(1 + m)*Hypergeometric2F1[1/2, (1 + 3*m)/6, (7 + 3*m)/6, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(1 + 3*m)*(b*Cos[c + d*x])^(2/3)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {16, 2722}
public void test0504() {
	check(//
"Integrate[Cos[c + d*x]^2/(b*Cos[c + d*x])^(2/3), x]", //
 "(-3*(b*Cos[c + d*x])^(7/3)*Hypergeometric2F1[1/2, 7/6, 13/6, Cos[c + d*x]^2]*Sin[c + d*x])/(7*b^3*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0505() {
	check(//
"Integrate[Cos[c + d*x]/(b*Cos[c + d*x])^(2/3), x]", //
 "(-3*(b*Cos[c + d*x])^(4/3)*Hypergeometric2F1[1/2, 2/3, 5/3, Cos[c + d*x]^2]*Sin[c + d*x])/(4*b^2*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0506() {
	check(//
"Integrate[Sec[c + d*x]/(b*Cos[c + d*x])^(2/3), x]", //
 "(3*Hypergeometric2F1[-1/3, 1/2, 2/3, Cos[c + d*x]^2]*Sin[c + d*x])/(2*d*(b*Cos[c + d*x])^(2/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0507() {
	check(//
"Integrate[Sec[c + d*x]^2/(b*Cos[c + d*x])^(2/3), x]", //
 "(3*b*Hypergeometric2F1[-5/6, 1/2, 1/6, Cos[c + d*x]^2]*Sin[c + d*x])/(5*d*(b*Cos[c + d*x])^(5/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0508() {
	check(//
"Integrate[Sec[c + d*x]^3/(b*Cos[c + d*x])^(2/3), x]", //
 "(3*b^2*Hypergeometric2F1[-4/3, 1/2, -1/3, Cos[c + d*x]^2]*Sin[c + d*x])/(8*d*(b*Cos[c + d*x])^(8/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {20, 2722}
public void test0509() {
	check(//
"Integrate[Cos[c + d*x]^m/(b*Cos[c + d*x])^(4/3), x]", //
 "(3*Cos[c + d*x]^m*Hypergeometric2F1[1/2, (-1 + 3*m)/6, (5 + 3*m)/6, Cos[c + d*x]^2]*Sin[c + d*x])/(b*d*(1 - 3*m)*(b*Cos[c + d*x])^(1/3)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {16, 2722}
public void test0510() {
	check(//
"Integrate[Cos[c + d*x]^2/(b*Cos[c + d*x])^(4/3), x]", //
 "(-3*(b*Cos[c + d*x])^(5/3)*Hypergeometric2F1[1/2, 5/6, 11/6, Cos[c + d*x]^2]*Sin[c + d*x])/(5*b^3*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0511() {
	check(//
"Integrate[Cos[c + d*x]/(b*Cos[c + d*x])^(4/3), x]", //
 "(-3*(b*Cos[c + d*x])^(2/3)*Hypergeometric2F1[1/3, 1/2, 4/3, Cos[c + d*x]^2]*Sin[c + d*x])/(2*b^2*d*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0512() {
	check(//
"Integrate[Sec[c + d*x]/(b*Cos[c + d*x])^(4/3), x]", //
 "(3*Hypergeometric2F1[-2/3, 1/2, 1/3, Cos[c + d*x]^2]*Sin[c + d*x])/(4*d*(b*Cos[c + d*x])^(4/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0513() {
	check(//
"Integrate[Sec[c + d*x]^2/(b*Cos[c + d*x])^(4/3), x]", //
 "(3*b*Hypergeometric2F1[-7/6, 1/2, -1/6, Cos[c + d*x]^2]*Sin[c + d*x])/(7*d*(b*Cos[c + d*x])^(7/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0514() {
	check(//
"Integrate[Sec[c + d*x]^3/(b*Cos[c + d*x])^(4/3), x]", //
 "(3*b^2*Hypergeometric2F1[-5/3, 1/2, -2/3, Cos[c + d*x]^2]*Sin[c + d*x])/(10*d*(b*Cos[c + d*x])^(10/3)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {20, 2722}
public void test0515() {
	check(//
"Integrate[(a*Cos[e + f*x])^m*(b*Cos[e + f*x])^n, x]", //
 "-(((a*Cos[e + f*x])^(1 + m)*(b*Cos[e + f*x])^n*Hypergeometric2F1[1/2, (1 + m + n)/2, (3 + m + n)/2, Cos[e + f*x]^2]*Sin[e + f*x])/(a*f*(1 + m + n)*Sqrt[Sin[e + f*x]^2]))", //
20, 2722);}

// {16, 2722}
public void test0516() {
	check(//
"Integrate[Cos[c + d*x]^2*(b*Cos[c + d*x])^n, x]", //
 "-(((b*Cos[c + d*x])^(3 + n)*Hypergeometric2F1[1/2, (3 + n)/2, (5 + n)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(b^3*d*(3 + n)*Sqrt[Sin[c + d*x]^2]))", //
16, 2722);}

// {16, 2722}
public void test0517() {
	check(//
"Integrate[Cos[c + d*x]*(b*Cos[c + d*x])^n, x]", //
 "-(((b*Cos[c + d*x])^(2 + n)*Hypergeometric2F1[1/2, (2 + n)/2, (4 + n)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(b^2*d*(2 + n)*Sqrt[Sin[c + d*x]^2]))", //
16, 2722);}

// {16, 2722}
public void test0518() {
	check(//
"Integrate[(b*Cos[c + d*x])^n*Sec[c + d*x], x]", //
 "-(((b*Cos[c + d*x])^n*Hypergeometric2F1[1/2, n/2, (2 + n)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(d*n*Sqrt[Sin[c + d*x]^2]))", //
16, 2722);}

// {16, 2722}
public void test0519() {
	check(//
"Integrate[(b*Cos[c + d*x])^n*Sec[c + d*x]^2, x]", //
 "(b*(b*Cos[c + d*x])^(-1 + n)*Hypergeometric2F1[1/2, (-1 + n)/2, (1 + n)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(1 - n)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0520() {
	check(//
"Integrate[(b*Cos[c + d*x])^n*Sec[c + d*x]^3, x]", //
 "(b^2*(b*Cos[c + d*x])^(-2 + n)*Hypergeometric2F1[1/2, (-2 + n)/2, n/2, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(2 - n)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {16, 2722}
public void test0521() {
	check(//
"Integrate[(b*Cos[c + d*x])^n*Sec[c + d*x]^4, x]", //
 "(b^3*(b*Cos[c + d*x])^(-3 + n)*Hypergeometric2F1[1/2, (-3 + n)/2, (-1 + n)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(3 - n)*Sqrt[Sin[c + d*x]^2])", //
16, 2722);}

// {20, 2722}
public void test0522() {
	check(//
"Integrate[Cos[c + d*x]^(5/2)*(b*Cos[c + d*x])^n, x]", //
 "(-2*Cos[c + d*x]^(7/2)*(b*Cos[c + d*x])^n*Hypergeometric2F1[1/2, (7 + 2*n)/4, (11 + 2*n)/4, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(7 + 2*n)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {20, 2722}
public void test0523() {
	check(//
"Integrate[Cos[c + d*x]^(3/2)*(b*Cos[c + d*x])^n, x]", //
 "(-2*Cos[c + d*x]^(5/2)*(b*Cos[c + d*x])^n*Hypergeometric2F1[1/2, (5 + 2*n)/4, (9 + 2*n)/4, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(5 + 2*n)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {20, 2722}
public void test0524() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]*(b*Cos[c + d*x])^n, x]", //
 "(-2*Cos[c + d*x]^(3/2)*(b*Cos[c + d*x])^n*Hypergeometric2F1[1/2, (3 + 2*n)/4, (7 + 2*n)/4, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(3 + 2*n)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {20, 2722}
public void test0525() {
	check(//
"Integrate[(b*Cos[c + d*x])^n/Sqrt[Cos[c + d*x]], x]", //
 "(-2*Sqrt[Cos[c + d*x]]*(b*Cos[c + d*x])^n*Hypergeometric2F1[1/2, (1 + 2*n)/4, (5 + 2*n)/4, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(1 + 2*n)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {20, 2722}
public void test0526() {
	check(//
"Integrate[(b*Cos[c + d*x])^n/Cos[c + d*x]^(3/2), x]", //
 "(2*(b*Cos[c + d*x])^n*Hypergeometric2F1[1/2, (-1 + 2*n)/4, (3 + 2*n)/4, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(1 - 2*n)*Sqrt[Cos[c + d*x]]*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {20, 2722}
public void test0527() {
	check(//
"Integrate[(b*Cos[c + d*x])^n/Cos[c + d*x]^(5/2), x]", //
 "(2*(b*Cos[c + d*x])^n*Hypergeometric2F1[1/2, (-3 + 2*n)/4, (1 + 2*n)/4, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(3 - 2*n)*Cos[c + d*x]^(3/2)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {20, 2722}
public void test0528() {
	check(//
"Integrate[(b*Cos[c + d*x])^n/Cos[c + d*x]^(7/2), x]", //
 "(2*(b*Cos[c + d*x])^n*Hypergeometric2F1[1/2, (-5 + 2*n)/4, (-1 + 2*n)/4, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(5 - 2*n)*Cos[c + d*x]^(5/2)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {20, 2722}
public void test0529() {
	check(//
"Integrate[(b*Cos[c + d*x])^n/Cos[c + d*x]^(9/2), x]", //
 "(2*(b*Cos[c + d*x])^n*Hypergeometric2F1[1/2, (-7 + 2*n)/4, (-3 + 2*n)/4, Cos[c + d*x]^2]*Sin[c + d*x])/(d*(7 - 2*n)*Cos[c + d*x]^(7/2)*Sqrt[Sin[c + d*x]^2])", //
20, 2722);}

// {2668, 2722}
public void test0530() {
	check(//
"Integrate[(a*Cos[e + f*x])^m*(b*Sec[e + f*x])^n, x]", //
 "-(((a*Cos[e + f*x])^(1 + m)*Hypergeometric2F1[1/2, (1 + m - n)/2, (3 + m - n)/2, Cos[e + f*x]^2]*(b*Sec[e + f*x])^n*Sin[e + f*x])/(a*f*(1 + m - n)*Sqrt[Sin[e + f*x]^2]))", //
2668, 2722);}

// {2701, 30}
public void test0531() {
	check(//
"Integrate[Cos[a + b*x]*Sqrt[Csc[a + b*x]], x]", //
 "2/(b*Sqrt[Csc[a + b*x]])", //
2701, 30);}

// {2701, 30}
public void test0532() {
	check(//
"Integrate[Cos[a + b*x]/Sqrt[Csc[a + b*x]], x]", //
 "2/(3*b*Csc[a + b*x]^(3/2))", //
2701, 30);}

// {2701, 30}
public void test0533() {
	check(//
"Integrate[Cos[x]*Csc[x]^(7/3), x]", //
 "(-3*Csc[x]^(4/3))/4", //
2701, 30);}

// {2667, 2657}
public void test0534() {
	check(//
"Integrate[(d*Cos[a + b*x])^(3/2)*Csc[a + b*x]^p, x]", //
 "(d*Sqrt[d*Cos[a + b*x]]*Csc[a + b*x]^(-1 + p)*Hypergeometric2F1[-1/4, (1 - p)/2, (3 - p)/2, Sin[a + b*x]^2])/(b*(1 - p)*(Cos[a + b*x]^2)^(1/4))", //
2667, 2657);}

// {2667, 2657}
public void test0535() {
	check(//
"Integrate[Sqrt[d*Cos[a + b*x]]*Csc[a + b*x]^p, x]", //
 "(d*(Cos[a + b*x]^2)^(1/4)*Csc[a + b*x]^(-1 + p)*Hypergeometric2F1[1/4, (1 - p)/2, (3 - p)/2, Sin[a + b*x]^2])/(b*(1 - p)*Sqrt[d*Cos[a + b*x]])", //
2667, 2657);}

// {2667, 2657}
public void test0536() {
	check(//
"Integrate[Csc[a + b*x]^p/Sqrt[d*Cos[a + b*x]], x]", //
 "(d*(Cos[a + b*x]^2)^(3/4)*Csc[a + b*x]^(-1 + p)*Hypergeometric2F1[3/4, (1 - p)/2, (3 - p)/2, Sin[a + b*x]^2])/(b*(1 - p)*(d*Cos[a + b*x])^(3/2))", //
2667, 2657);}

// {2667, 2657}
public void test0537() {
	check(//
"Integrate[Csc[a + b*x]^p/(d*Cos[a + b*x])^(3/2), x]", //
 "((Cos[a + b*x]^2)^(1/4)*Csc[a + b*x]^(-1 + p)*Hypergeometric2F1[5/4, (1 - p)/2, (3 - p)/2, Sin[a + b*x]^2])/(b*d*(1 - p)*Sqrt[d*Cos[a + b*x]])", //
2667, 2657);}

// {2667, 2657}
public void test0538() {
	check(//
"Integrate[Csc[a + b*x]^p/(d*Cos[a + b*x])^(5/2), x]", //
 "((Cos[a + b*x]^2)^(3/4)*Csc[a + b*x]^(-1 + p)*Hypergeometric2F1[7/4, (1 - p)/2, (3 - p)/2, Sin[a + b*x]^2])/(b*d*(1 - p)*(d*Cos[a + b*x])^(3/2))", //
2667, 2657);}

// {2667, 2657}
public void test0539() {
	check(//
"Integrate[Cos[e + f*x]^m*Csc[e + f*x]^n, x]", //
 "(Cos[e + f*x]^(-1 + m)*(Cos[e + f*x]^2)^((1 - m)/2)*Csc[e + f*x]^(-1 + n)*Hypergeometric2F1[(1 - m)/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2])/(f*(1 - n))", //
2667, 2657);}

// {2667, 2657}
public void test0540() {
	check(//
"Integrate[(a*Cos[e + f*x])^m*Csc[e + f*x]^n, x]", //
 "(a*(a*Cos[e + f*x])^(-1 + m)*(Cos[e + f*x]^2)^((1 - m)/2)*Csc[e + f*x]^(-1 + n)*Hypergeometric2F1[(1 - m)/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2])/(f*(1 - n))", //
2667, 2657);}

// {2667, 2657}
public void test0541() {
	check(//
"Integrate[Cos[e + f*x]^m*(b*Csc[e + f*x])^n, x]", //
 "(b*Cos[e + f*x]^(-1 + m)*(Cos[e + f*x]^2)^((1 - m)/2)*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[(1 - m)/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2])/(f*(1 - n))", //
2667, 2657);}

// {2667, 2657}
public void test0542() {
	check(//
"Integrate[(a*Cos[e + f*x])^m*(b*Csc[e + f*x])^n, x]", //
 "(a*b*(a*Cos[e + f*x])^(-1 + m)*(Cos[e + f*x]^2)^((1 - m)/2)*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[(1 - m)/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2])/(f*(1 - n))", //
2667, 2657);}

// {2667, 2656}
public void test0543() {
	check(//
"Integrate[(a*Cos[e + f*x])^m*(b*Csc[e + f*x])^(5/2), x]", //
 "-((b*(a*Cos[e + f*x])^(1 + m)*(b*Csc[e + f*x])^(3/2)*Hypergeometric2F1[7/4, (1 + m)/2, (3 + m)/2, Cos[e + f*x]^2]*(Sin[e + f*x]^2)^(3/4))/(a*f*(1 + m)))", //
2667, 2656);}

// {2667, 2656}
public void test0544() {
	check(//
"Integrate[(a*Cos[e + f*x])^m*(b*Csc[e + f*x])^(3/2), x]", //
 "-((b*(a*Cos[e + f*x])^(1 + m)*Sqrt[b*Csc[e + f*x]]*Hypergeometric2F1[5/4, (1 + m)/2, (3 + m)/2, Cos[e + f*x]^2]*(Sin[e + f*x]^2)^(1/4))/(a*f*(1 + m)))", //
2667, 2656);}

// {2666, 2656}
public void test0545() {
	check(//
"Integrate[(a*Cos[e + f*x])^m*Sqrt[b*Csc[e + f*x]], x]", //
 "-(((a*Cos[e + f*x])^(1 + m)*(b*Csc[e + f*x])^(3/2)*Hypergeometric2F1[3/4, (1 + m)/2, (3 + m)/2, Cos[e + f*x]^2]*(Sin[e + f*x]^2)^(3/4))/(a*b*f*(1 + m)))", //
2666, 2656);}

// {2666, 2656}
public void test0546() {
	check(//
"Integrate[(a*Cos[e + f*x])^m/Sqrt[b*Csc[e + f*x]], x]", //
 "-(((a*Cos[e + f*x])^(1 + m)*Sqrt[b*Csc[e + f*x]]*Hypergeometric2F1[1/4, (1 + m)/2, (3 + m)/2, Cos[e + f*x]^2]*(Sin[e + f*x]^2)^(1/4))/(a*b*f*(1 + m)))", //
2666, 2656);}

// {2666, 2656}
public void test0547() {
	check(//
"Integrate[(a*Cos[e + f*x])^m/(b*Csc[e + f*x])^(3/2), x]", //
 "-(((a*Cos[e + f*x])^(1 + m)*Hypergeometric2F1[-1/4, (1 + m)/2, (3 + m)/2, Cos[e + f*x]^2])/(a*b*f*(1 + m)*Sqrt[b*Csc[e + f*x]]*(Sin[e + f*x]^2)^(1/4)))", //
2666, 2656);}

// {3377, 2718}
public void test0548() {
	check(//
"Integrate[(c + d*x)*Cos[a + b*x], x]", //
 "(d*Cos[a + b*x])/b^2 + ((c + d*x)*Sin[a + b*x])/b", //
3377, 2718);}

// {3391}
public void test0549() {
	check(//
"Integrate[(c + d*x)*Cos[a + b*x]^2, x]", //
 "(c*x)/2 + (d*x^2)/4 + (d*Cos[a + b*x]^2)/(4*b^2) + ((c + d*x)*Cos[a + b*x]*Sin[a + b*x])/(2*b)", //
3391);}

// {4269, 3556}
public void test0550() {
	check(//
"Integrate[(c + d*x)*Sec[a + b*x]^2, x]", //
 "(d*Log[Cos[a + b*x]])/b^2 + ((c + d*x)*Tan[a + b*x])/b", //
4269, 3556);}

// {3385, 3433}
public void test0551() {
	check(//
"Integrate[Cos[x]/Sqrt[x], x]", //
 "Sqrt[2*Pi]*FresnelC[Sqrt[2/Pi]*Sqrt[x]]", //
3385, 3433);}

// {2715, 2720}
public void test0552() {
	check(//
"Integrate[Cos[a + b*x]^(3/2), x]", //
 "(2*EllipticF[(a + b*x)/2, 2])/(3*b) + (2*Sqrt[Cos[a + b*x]]*Sin[a + b*x])/(3*b)", //
2715, 2720);}

// {3391}
public void test0553() {
	check(//
"Integrate[-x/(3*Sqrt[Cos[a + b*x]]) + x*Cos[a + b*x]^(3/2), x]", //
 "(4*Cos[a + b*x]^(3/2))/(9*b^2) + (2*x*Sqrt[Cos[a + b*x]]*Sin[a + b*x])/(3*b)", //
3391);}

// {2716, 2719}
public void test0554() {
	check(//
"Integrate[Cos[a + b*x]^(-3/2), x]", //
 "(-2*EllipticE[(a + b*x)/2, 2])/b + (2*Sin[a + b*x])/(b*Sqrt[Cos[a + b*x]])", //
2716, 2719);}

// {3396}
public void test0555() {
	check(//
"Integrate[x/Cos[a + b*x]^(3/2) + x*Sqrt[Cos[a + b*x]], x]", //
 "(4*Sqrt[Cos[a + b*x]])/b^2 + (2*x*Sin[a + b*x])/(b*Sqrt[Cos[a + b*x]])", //
3396);}

// {3396}
public void test0556() {
	check(//
"Integrate[x/Cos[x]^(3/2) + x*Sqrt[Cos[x]], x]", //
 "4*Sqrt[Cos[x]] + (2*x*Sin[x])/Sqrt[Cos[x]]", //
3396);}

// {3396}
public void test0557() {
	check(//
"Integrate[x/Cos[x]^(5/2) - x/(3*Sqrt[Cos[x]]), x]", //
 "-4/(3*Sqrt[Cos[x]]) + (2*x*Sin[x])/(3*Cos[x]^(3/2))", //
3396);}

// {3400, 3383}
public void test0558() {
	check(//
"Integrate[Sqrt[a + a*Cos[x]]/x, x]", //
 "Sqrt[a + a*Cos[x]]*CosIntegral[x/2]*Sec[x/2]", //
3400, 3383);}

// {3400, 3380}
public void test0559() {
	check(//
"Integrate[Sqrt[a - a*Cos[x]]/x, x]", //
 "Sqrt[a - a*Cos[x]]*Csc[x/2]*SinIntegral[x/2]", //
3400, 3380);}

// {2728, 212}
public void test0560() {
	check(//
"Integrate[1/Sqrt[a + a*Cos[c + d*x]], x]", //
 "(Sqrt[2]*ArcTanh[(Sqrt[a]*Sin[c + d*x])/(Sqrt[2]*Sqrt[a + a*Cos[c + d*x]])])/(Sqrt[a]*d)", //
2728, 212);}

// {2728, 212}
public void test0561() {
	check(//
"Integrate[1/Sqrt[a - a*Cos[x]], x]", //
 "-((Sqrt[2]*ArcTanh[(Sqrt[a]*Sin[x])/(Sqrt[2]*Sqrt[a - a*Cos[x]])])/Sqrt[a])", //
2728, 212);}

// {2726, 2725}
public void test0562() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(3/2), x]", //
 "(8*a^2*Sin[c + d*x])/(3*d*Sqrt[a + a*Cos[c + d*x]]) + (2*a*Sqrt[a + a*Cos[c + d*x]]*Sin[c + d*x])/(3*d)", //
2726, 2725);}

// {2728, 212}
public void test0563() {
	check(//
"Integrate[1/Sqrt[a + a*Cos[c + d*x]], x]", //
 "(Sqrt[2]*ArcTanh[(Sqrt[a]*Sin[c + d*x])/(Sqrt[2]*Sqrt[a + a*Cos[c + d*x]])])/(Sqrt[a]*d)", //
2728, 212);}

// {2731, 2730}
public void test0564() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(4/3), x]", //
 "(2*2^(5/6)*a*(a + a*Cos[c + d*x])^(1/3)*Hypergeometric2F1[-5/6, 1/2, 3/2, (1 - Cos[c + d*x])/2]*Sin[c + d*x])/(d*(1 + Cos[c + d*x])^(5/6))", //
2731, 2730);}

// {2731, 2730}
public void test0565() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(2/3), x]", //
 "(2*2^(1/6)*(a + a*Cos[c + d*x])^(2/3)*Hypergeometric2F1[-1/6, 1/2, 3/2, (1 - Cos[c + d*x])/2]*Sin[c + d*x])/(d*(1 + Cos[c + d*x])^(7/6))", //
2731, 2730);}

// {2731, 2730}
public void test0566() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(1/3), x]", //
 "(2^(5/6)*(a + a*Cos[c + d*x])^(1/3)*Hypergeometric2F1[1/6, 1/2, 3/2, (1 - Cos[c + d*x])/2]*Sin[c + d*x])/(d*(1 + Cos[c + d*x])^(5/6))", //
2731, 2730);}

// {2731, 2730}
public void test0567() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(-1/3), x]", //
 "(2^(1/6)*Hypergeometric2F1[1/2, 5/6, 3/2, (1 - Cos[c + d*x])/2]*Sin[c + d*x])/(d*(1 + Cos[c + d*x])^(1/6)*(a + a*Cos[c + d*x])^(1/3))", //
2731, 2730);}

// {2731, 2730}
public void test0568() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(-2/3), x]", //
 "((1 + Cos[c + d*x])^(1/6)*Hypergeometric2F1[1/2, 7/6, 3/2, (1 - Cos[c + d*x])/2]*Sin[c + d*x])/(2^(1/6)*d*(a + a*Cos[c + d*x])^(2/3))", //
2731, 2730);}

// {2731, 2730}
public void test0569() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(-4/3), x]", //
 "(Hypergeometric2F1[1/2, 11/6, 3/2, (1 - Cos[c + d*x])/2]*Sin[c + d*x])/(2^(5/6)*a*d*(1 + Cos[c + d*x])^(1/6)*(a + a*Cos[c + d*x])^(1/3))", //
2731, 2730);}

// {2731, 2730}
public void test0570() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^n, x]", //
 "(2^(1/2 + n)*(1 + Cos[c + d*x])^(-1/2 - n)*(a + a*Cos[c + d*x])^n*Hypergeometric2F1[1/2, 1/2 - n, 3/2, (1 - Cos[c + d*x])/2]*Sin[c + d*x])/d", //
2731, 2730);}

// {2731, 2730}
public void test0571() {
	check(//
"Integrate[(a - a*Cos[c + d*x])^n, x]", //
 "-((2^(1/2 + n)*(1 - Cos[c + d*x])^(-1/2 - n)*(a - a*Cos[c + d*x])^n*Hypergeometric2F1[1/2, 1/2 - n, 3/2, (1 + Cos[c + d*x])/2]*Sin[c + d*x])/d)", //
2731, 2730);}

// {2738, 212}
public void test0572() {
	check(//
"Integrate[(3 + 5*Cos[c + d*x])^(-1), x]", //
 "-Log[2*Cos[(c + d*x)/2] - Sin[(c + d*x)/2]]/(4*d) + Log[2*Cos[(c + d*x)/2] + Sin[(c + d*x)/2]]/(4*d)", //
2738, 212);}

// {2738, 213}
public void test0573() {
	check(//
"Integrate[(3 - 5*Cos[c + d*x])^(-1), x]", //
 "Log[Cos[(c + d*x)/2] - 2*Sin[(c + d*x)/2]]/(4*d) - Log[Cos[(c + d*x)/2] + 2*Sin[(c + d*x)/2]]/(4*d)", //
2738, 213);}

// {2738, 212}
public void test0574() {
	check(//
"Integrate[(-3 + 5*Cos[c + d*x])^(-1), x]", //
 "-Log[Cos[(c + d*x)/2] - 2*Sin[(c + d*x)/2]]/(4*d) + Log[Cos[(c + d*x)/2] + 2*Sin[(c + d*x)/2]]/(4*d)", //
2738, 212);}

// {2738, 213}
public void test0575() {
	check(//
"Integrate[(-3 - 5*Cos[c + d*x])^(-1), x]", //
 "Log[2*Cos[(c + d*x)/2] - Sin[(c + d*x)/2]]/(4*d) - Log[2*Cos[(c + d*x)/2] + Sin[(c + d*x)/2]]/(4*d)", //
2738, 213);}

// {2734, 2732}
public void test0576() {
	check(//
"Integrate[Sqrt[a + b*Cos[c + d*x]], x]", //
 "(2*Sqrt[a + b*Cos[c + d*x]]*EllipticE[(c + d*x)/2, (2*b)/(a + b)])/(d*Sqrt[(a + b*Cos[c + d*x])/(a + b)])", //
2734, 2732);}

// {2742, 2740}
public void test0577() {
	check(//
"Integrate[1/Sqrt[a + b*Cos[c + d*x]], x]", //
 "(2*Sqrt[(a + b*Cos[c + d*x])/(a + b)]*EllipticF[(c + d*x)/2, (2*b)/(a + b)])/(d*Sqrt[a + b*Cos[c + d*x]])", //
2742, 2740);}

// {3461, 2717}
public void test0578() {
	check(//
"Integrate[x*Cos[a + b*x^2], x]", //
 "Sin[a + b*x^2]/(2*b)", //
3461, 2717);}

// {3461, 2717}
public void test0579() {
	check(//
"Integrate[Cos[a + b/x]/x^2, x]", //
 "-(Sin[a + b/x]/b)", //
3461, 2717);}

// {3461, 2717}
public void test0580() {
	check(//
"Integrate[Cos[a + b/x^2]/x^3, x]", //
 "-Sin[a + b/x^2]/(2*b)", //
3461, 2717);}

// {3461, 2717}
public void test0581() {
	check(//
"Integrate[Cos[Sqrt[x]]/Sqrt[x], x]", //
 "2*Sin[Sqrt[x]]", //
3461, 2717);}

// {3461, 2717}
public void test0582() {
	check(//
"Integrate[Cos[x^(1/6)]/x^(5/6), x]", //
 "6*Sin[x^(1/6)]", //
3461, 2717);}

// {2746}
public void test0583() {
	check(//
"Integrate[Sin[x]^3/(a + a*Cos[x]), x]", //
 "-(Cos[x]/a) + Cos[x]^2/(2*a)", //
2746);}

// {2761, 8}
public void test0584() {
	check(//
"Integrate[Sin[x]^2/(a + a*Cos[x]), x]", //
 "x/a - Sin[x]/a", //
2761, 8);}

// {2746, 31}
public void test0585() {
	check(//
"Integrate[Sin[x]/(a + a*Cos[x]), x]", //
 "-(Log[1 + Cos[x]]/a)", //
2746, 31);}

// {2746, 32}
public void test0586() {
	check(//
"Integrate[Sin[x]/(1 + Cos[x])^2, x]", //
 "(1 + Cos[x])^(-1)", //
2746, 32);}

// {2746, 32}
public void test0587() {
	check(//
"Integrate[Sin[x]/(1 - Cos[x])^2, x]", //
 "-(1 - Cos[x])^(-1)", //
2746, 32);}

// {2759, 8}
public void test0588() {
	check(//
"Integrate[Sin[x]^2/(1 + Cos[x])^2, x]", //
 "-x + (2*Sin[x])/(1 + Cos[x])", //
2759, 8);}

// {2759, 8}
public void test0589() {
	check(//
"Integrate[Sin[x]^2/(1 - Cos[x])^2, x]", //
 "-x - (2*Sin[x])/(1 - Cos[x])", //
2759, 8);}

// {2746, 32}
public void test0590() {
	check(//
"Integrate[Sin[x]/(1 + Cos[x])^3, x]", //
 "1/(2*(1 + Cos[x])^2)", //
2746, 32);}

// {2746, 32}
public void test0591() {
	check(//
"Integrate[Sin[x]/(1 - Cos[x])^3, x]", //
 "-1/(2*(1 - Cos[x])^2)", //
2746, 32);}

// {2747, 31}
public void test0592() {
	check(//
"Integrate[Sin[x]/(a + b*Cos[x]), x]", //
 "-(Log[a + b*Cos[x]]/b)", //
2747, 31);}

// {2738, 211}
public void test0593() {
	check(//
"Integrate[(a + b*Cos[x])^(-1), x]", //
 "(2*ArcTan[(Sqrt[a - b]*Tan[x/2])/Sqrt[a + b]])/(Sqrt[a - b]*Sqrt[a + b])", //
2738, 211);}

// {3527, 3433}
public void test0594() {
	check(//
"Integrate[Cos[1/4 + x + x^2], x]", //
 "Sqrt[Pi/2]*FresnelC[(1 + 2*x)/Sqrt[2*Pi]]", //
3527, 3433);}

// {2717}
public void test0599() {
	check(//
"Integrate[a + a*Cos[c + d*x], x]", //
 "a*x + (a*Sin[c + d*x])/d", //
2717);}

// {2814, 3855}
public void test0600() {
	check(//
"Integrate[(a + a*Cos[c + d*x])*Sec[c + d*x], x]", //
 "a*x + (a*ArcTanh[Sin[c + d*x]])/d", //
2814, 3855);}

// {2846, 2813}
public void test0601() {
	check(//
"Integrate[Cos[c + d*x]^3/(a + a*Cos[c + d*x]), x]", //
 "(3*x)/(2*a) - (2*Sin[c + d*x])/(a*d) + (3*Cos[c + d*x]*Sin[c + d*x])/(2*a*d) - (Cos[c + d*x]^2*Sin[c + d*x])/(d*(a + a*Cos[c + d*x]))", //
2846, 2813);}

// {2814, 2727}
public void test0602() {
	check(//
"Integrate[Cos[c + d*x]/(a + a*Cos[c + d*x]), x]", //
 "x/a - Sin[c + d*x]/(d*(a + a*Cos[c + d*x]))", //
2814, 2727);}

// {2829, 2727}
public void test0603() {
	check(//
"Integrate[Cos[c + d*x]/(a + a*Cos[c + d*x])^2, x]", //
 "-Sin[c + d*x]/(3*d*(a + a*Cos[c + d*x])^2) + (2*Sin[c + d*x])/(3*d*(a^2 + a^2*Cos[c + d*x]))", //
2829, 2727);}

// {2729, 2727}
public void test0604() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(-2), x]", //
 "Sin[c + d*x]/(3*d*(a + a*Cos[c + d*x])^2) + Sin[c + d*x]/(3*d*(a^2 + a^2*Cos[c + d*x]))", //
2729, 2727);}

// {2830, 2725}
public void test0605() {
	check(//
"Integrate[Cos[c + d*x]*Sqrt[a + a*Cos[c + d*x]], x]", //
 "(2*a*Sin[c + d*x])/(3*d*Sqrt[a + a*Cos[c + d*x]]) + (2*Sqrt[a + a*Cos[c + d*x]]*Sin[c + d*x])/(3*d)", //
2830, 2725);}

// {2852, 212}
public void test0606() {
	check(//
"Integrate[Sqrt[a + a*Cos[c + d*x]]*Sec[c + d*x], x]", //
 "(2*Sqrt[a]*ArcTanh[(Sqrt[a]*Sin[c + d*x])/Sqrt[a + a*Cos[c + d*x]]])/d", //
2852, 212);}

// {2726, 2725}
public void test0607() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(3/2), x]", //
 "(8*a^2*Sin[c + d*x])/(3*d*Sqrt[a + a*Cos[c + d*x]]) + (2*a*Sqrt[a + a*Cos[c + d*x]]*Sin[c + d*x])/(3*d)", //
2726, 2725);}

// {2728, 212}
public void test0608() {
	check(//
"Integrate[1/Sqrt[a + a*Cos[c + d*x]], x]", //
 "(Sqrt[2]*ArcTanh[(Sqrt[a]*Sin[c + d*x])/(Sqrt[2]*Sqrt[a + a*Cos[c + d*x]])])/(Sqrt[a]*d)", //
2728, 212);}

// {2853, 222}
public void test0609() {
	check(//
"Integrate[Sqrt[a + a*Cos[c + d*x]]/Sqrt[Cos[c + d*x]], x]", //
 "(2*Sqrt[a]*ArcSin[(Sqrt[a]*Sin[c + d*x])/Sqrt[a + a*Cos[c + d*x]]])/d", //
2853, 222);}

// {2851, 2850}
public void test0610() {
	check(//
"Integrate[Sqrt[a + a*Cos[c + d*x]]/Cos[c + d*x]^(5/2), x]", //
 "(2*a*Sin[c + d*x])/(3*d*Cos[c + d*x]^(3/2)*Sqrt[a + a*Cos[c + d*x]]) + (4*a*Sin[c + d*x])/(3*d*Sqrt[Cos[c + d*x]]*Sqrt[a + a*Cos[c + d*x]])", //
2851, 2850);}

// {2841, 8}
public void test0611() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^(3/2)/Cos[c + d*x]^(5/4), x]", //
 "(4*a^2*Sin[c + d*x])/(d*Cos[c + d*x]^(1/4)*Sqrt[a + a*Cos[c + d*x]])", //
2841, 8);}

// {2853, 222}
public void test0612() {
	check(//
"Integrate[Sqrt[a + a*Cos[e + f*x]]/Sqrt[Cos[e + f*x]], x]", //
 "(2*Sqrt[a]*ArcSin[(Sqrt[a]*Sin[e + f*x])/Sqrt[a + a*Cos[e + f*x]]])/f", //
2853, 222);}

// {2853, 222}
public void test0613() {
	check(//
"Integrate[Sqrt[a - a*Cos[e + f*x]]/Sqrt[-Cos[e + f*x]], x]", //
 "(-2*Sqrt[a]*ArcSin[(Sqrt[a]*Sin[e + f*x])/Sqrt[a - a*Cos[e + f*x]]])/f", //
2853, 222);}

// {2861, 211}
public void test0614() {
	check(//
"Integrate[1/(Sqrt[Cos[c + d*x]]*Sqrt[a + a*Cos[c + d*x]]), x]", //
 "(Sqrt[2]*ArcTan[(Sqrt[a]*Sin[c + d*x])/(Sqrt[2]*Sqrt[Cos[c + d*x]]*Sqrt[a + a*Cos[c + d*x]])])/(Sqrt[a]*d)", //
2861, 211);}

// {2860, 222}
public void test0615() {
	check(//
"Integrate[1/(Sqrt[Cos[c + d*x]]*Sqrt[1 + Cos[c + d*x]]), x]", //
 "(Sqrt[2]*ArcSin[Sin[c + d*x]/(1 + Cos[c + d*x])])/d", //
2860, 222);}

// {2860, 222}
public void test0616() {
	check(//
"Integrate[1/(Sqrt[Cos[x]]*Sqrt[1 + Cos[x]]), x]", //
 "Sqrt[2]*ArcSin[Sin[x]/(1 + Cos[x])]", //
2860, 222);}

// {2861, 211}
public void test0617() {
	check(//
"Integrate[1/(Sqrt[Cos[x]]*Sqrt[a + a*Cos[x]]), x]", //
 "(Sqrt[2]*ArcTan[(Sqrt[a]*Sin[x])/(Sqrt[2]*Sqrt[Cos[x]]*Sqrt[a + a*Cos[x]])])/Sqrt[a]", //
2861, 211);}

// {2854, 213}
public void test0618() {
	check(//
"Integrate[Sqrt[a - a*Cos[c + d*x]]/Sqrt[Cos[c + d*x]], x]", //
 "(-2*Sqrt[a]*ArcTanh[(Sqrt[a]*Sin[c + d*x])/(Sqrt[Cos[c + d*x]]*Sqrt[a - a*Cos[c + d*x]])])/d", //
2854, 213);}

// {2851, 2850}
public void test0619() {
	check(//
"Integrate[Sqrt[a - a*Cos[c + d*x]]/Cos[c + d*x]^(5/2), x]", //
 "(2*a*Sin[c + d*x])/(3*d*Cos[c + d*x]^(3/2)*Sqrt[a - a*Cos[c + d*x]]) - (4*a*Sin[c + d*x])/(3*d*Sqrt[Cos[c + d*x]]*Sqrt[a - a*Cos[c + d*x]])", //
2851, 2850);}

// {2854, 213}
public void test0620() {
	check(//
"Integrate[Sqrt[1 - Cos[c + d*x]]/Sqrt[Cos[c + d*x]], x]", //
 "(-2*ArcTanh[Sin[c + d*x]/(Sqrt[1 - Cos[c + d*x]]*Sqrt[Cos[c + d*x]])])/d", //
2854, 213);}

// {2851, 2850}
public void test0621() {
	check(//
"Integrate[Sqrt[1 - Cos[c + d*x]]/Cos[c + d*x]^(5/2), x]", //
 "(2*Sin[c + d*x])/(3*d*Sqrt[1 - Cos[c + d*x]]*Cos[c + d*x]^(3/2)) - (4*Sin[c + d*x])/(3*d*Sqrt[1 - Cos[c + d*x]]*Sqrt[Cos[c + d*x]])", //
2851, 2850);}

// {2861, 214}
public void test0622() {
	check(//
"Integrate[1/(Sqrt[Cos[c + d*x]]*Sqrt[a - a*Cos[c + d*x]]), x]", //
 "-((Sqrt[2]*ArcTanh[(Sqrt[a]*Sin[c + d*x])/(Sqrt[2]*Sqrt[Cos[c + d*x]]*Sqrt[a - a*Cos[c + d*x]])])/(Sqrt[a]*d))", //
2861, 214);}

// {2861, 212}
public void test0623() {
	check(//
"Integrate[1/(Sqrt[1 - Cos[c + d*x]]*Sqrt[Cos[c + d*x]]), x]", //
 "-((Sqrt[2]*ArcTanh[Sin[c + d*x]/(Sqrt[2]*Sqrt[1 - Cos[c + d*x]]*Sqrt[Cos[c + d*x]])])/d)", //
2861, 212);}

// {4307, 2850}
public void test0624() {
	check(//
"Integrate[Sqrt[a + a*Cos[c + d*x]]*Sec[c + d*x]^(3/2), x]", //
 "(2*a*Sqrt[Sec[c + d*x]]*Sin[c + d*x])/(d*Sqrt[a + a*Cos[c + d*x]])", //
4307, 2850);}

// {2717}
public void test0625() {
	check(//
"Integrate[a + b*Cos[c + d*x], x]", //
 "a*x + (b*Sin[c + d*x])/d", //
2717);}

// {2814, 3855}
public void test0626() {
	check(//
"Integrate[(a + b*Cos[c + d*x])*Sec[c + d*x], x]", //
 "b*x + (a*ArcTanh[Sin[c + d*x]])/d", //
2814, 3855);}

// {2832, 2813}
public void test0627() {
	check(//
"Integrate[Cos[c + d*x]*(a + b*Cos[c + d*x])^2, x]", //
 "a*b*x + (2*(a^2 + b^2)*Sin[c + d*x])/(3*d) + (a*b*Cos[c + d*x]*Sin[c + d*x])/(3*d) + ((a + b*Cos[c + d*x])^2*Sin[c + d*x])/(3*d)", //
2832, 2813);}

// {2738, 211}
public void test0628() {
	check(//
"Integrate[(a + b*Cos[c + d*x])^(-1), x]", //
 "(2*ArcTan[(Sqrt[a - b]*Tan[(c + d*x)/2])/Sqrt[a + b]])/(Sqrt[a - b]*Sqrt[a + b]*d)", //
2738, 211);}

// {2734, 2732}
public void test0629() {
	check(//
"Integrate[Sqrt[a + b*Cos[c + d*x]], x]", //
 "(2*Sqrt[a + b*Cos[c + d*x]]*EllipticE[(c + d*x)/2, (2*b)/(a + b)])/(d*Sqrt[(a + b*Cos[c + d*x])/(a + b)])", //
2734, 2732);}

// {2742, 2740}
public void test0630() {
	check(//
"Integrate[1/Sqrt[a + b*Cos[c + d*x]], x]", //
 "(2*Sqrt[(a + b*Cos[c + d*x])/(a + b)]*EllipticF[(c + d*x)/2, (2*b)/(a + b)])/(d*Sqrt[a + b*Cos[c + d*x]])", //
2742, 2740);}

// {2886, 2884}
public void test0631() {
	check(//
"Integrate[Sec[c + d*x]/Sqrt[a + b*Cos[c + d*x]], x]", //
 "(2*Sqrt[(a + b*Cos[c + d*x])/(a + b)]*EllipticPi[2, (c + d*x)/2, (2*b)/(a + b)])/(d*Sqrt[a + b*Cos[c + d*x]])", //
2886, 2884);}

// {2893, 2892}
public void test0632() {
	check(//
"Integrate[1/(Sqrt[2 - 3*Cos[c + d*x]]*Sqrt[Cos[c + d*x]]), x]", //
 "(-2*Sqrt[-Cos[c + d*x]]*EllipticF[ArcSin[Sin[c + d*x]/(1 - Cos[c + d*x])], 1/5])/(Sqrt[5]*d*Sqrt[Cos[c + d*x]])", //
2893, 2892);}

// {2893, 2892}
public void test0633() {
	check(//
"Integrate[1/(Sqrt[-2 - 3*Cos[c + d*x]]*Sqrt[Cos[c + d*x]]), x]", //
 "(-2*Sqrt[-Cos[c + d*x]]*EllipticF[ArcSin[Sin[c + d*x]/(1 - Cos[c + d*x])], 5])/(d*Sqrt[Cos[c + d*x]])", //
2893, 2892);}

// {2896, 2894}
public void test0634() {
	check(//
"Integrate[1/(Sqrt[Cos[c + d*x]]*Sqrt[-3 + 2*Cos[c + d*x]]), x]", //
 "(-2*Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticF[ArcSin[Sqrt[-3 + 2*Cos[c + d*x]]/Sqrt[-Cos[c + d*x]]], -1/5]*Sqrt[-Tan[c + d*x]^2])/(Sqrt[5]*d)", //
2896, 2894);}

// {2896, 2894}
public void test0635() {
	check(//
"Integrate[1/(Sqrt[-3 - 2*Cos[c + d*x]]*Sqrt[Cos[c + d*x]]), x]", //
 "(-2*Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticF[ArcSin[Sqrt[-3 - 2*Cos[c + d*x]]/(Sqrt[5]*Sqrt[-Cos[c + d*x]])], -5]*Sqrt[-Tan[c + d*x]^2])/d", //
2896, 2894);}

// {2893, 2892}
public void test0636() {
	check(//
"Integrate[1/(Sqrt[-Cos[c + d*x]]*Sqrt[2 + 3*Cos[c + d*x]]), x]", //
 "(2*Sqrt[Cos[c + d*x]]*EllipticF[ArcSin[Sin[c + d*x]/(1 + Cos[c + d*x])], 1/5])/(Sqrt[5]*d*Sqrt[-Cos[c + d*x]])", //
2893, 2892);}

// {2893, 2892}
public void test0637() {
	check(//
"Integrate[1/(Sqrt[-Cos[c + d*x]]*Sqrt[-2 + 3*Cos[c + d*x]]), x]", //
 "(2*Sqrt[Cos[c + d*x]]*EllipticF[ArcSin[Sin[c + d*x]/(1 + Cos[c + d*x])], 5])/(d*Sqrt[-Cos[c + d*x]])", //
2893, 2892);}

// {2896, 2894}
public void test0638() {
	check(//
"Integrate[1/(Sqrt[-Cos[c + d*x]]*Sqrt[3 + 2*Cos[c + d*x]]), x]", //
 "(2*Cos[c + d*x]^(3/2)*Csc[c + d*x]*EllipticF[ArcSin[Sqrt[3 + 2*Cos[c + d*x]]/(Sqrt[5]*Sqrt[Cos[c + d*x]])], -5]*Sqrt[-Tan[c + d*x]^2])/(d*Sqrt[-Cos[c + d*x]])", //
2896, 2894);}

// {2896, 2894}
public void test0639() {
	check(//
"Integrate[1/(Sqrt[3 - 2*Cos[c + d*x]]*Sqrt[-Cos[c + d*x]]), x]", //
 "(2*Cos[c + d*x]^(3/2)*Csc[c + d*x]*EllipticF[ArcSin[Sqrt[3 - 2*Cos[c + d*x]]/Sqrt[Cos[c + d*x]]], -1/5]*Sqrt[-Tan[c + d*x]^2])/(Sqrt[5]*d*Sqrt[-Cos[c + d*x]])", //
2896, 2894);}

// {2889, 2888}
public void test0640() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]/Sqrt[2 - 3*Cos[c + d*x]], x]", //
 "(-4*Cos[c + d*x]^(3/2)*Csc[c + d*x]*EllipticPi[1/3, ArcSin[Sqrt[2 - 3*Cos[c + d*x]]/Sqrt[-Cos[c + d*x]]], 1/5]*Sqrt[-1 + Sec[c + d*x]]*Sqrt[1 + Sec[c + d*x]])/(3*Sqrt[5]*d*Sqrt[-Cos[c + d*x]])", //
2889, 2888);}

// {2889, 2888}
public void test0641() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]/Sqrt[-2 - 3*Cos[c + d*x]], x]", //
 "(-4*Cos[c + d*x]^(3/2)*Csc[c + d*x]*EllipticPi[5/3, ArcSin[Sqrt[-2 - 3*Cos[c + d*x]]/(Sqrt[5]*Sqrt[-Cos[c + d*x]])], 5]*Sqrt[-1 - Sec[c + d*x]]*Sqrt[1 - Sec[c + d*x]])/(3*d*Sqrt[-Cos[c + d*x]])", //
2889, 2888);}

// {2889, 2887}
public void test0642() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]/Sqrt[-3 + 2*Cos[c + d*x]], x]", //
 "(3*Cos[c + d*x]^(3/2)*Csc[c + d*x]*EllipticPi[-1/2, ArcSin[Sqrt[-3 + 2*Cos[c + d*x]]/Sqrt[-Cos[c + d*x]]], -1/5]*Sqrt[1 - Sec[c + d*x]]*Sqrt[1 + Sec[c + d*x]])/(Sqrt[5]*d*Sqrt[-Cos[c + d*x]])", //
2889, 2887);}

// {2889, 2887}
public void test0643() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]/Sqrt[-3 - 2*Cos[c + d*x]], x]", //
 "(-3*Cos[c + d*x]^(3/2)*Csc[c + d*x]*EllipticPi[5/2, ArcSin[Sqrt[-3 - 2*Cos[c + d*x]]/(Sqrt[5]*Sqrt[-Cos[c + d*x]])], -5]*Sqrt[1 - Sec[c + d*x]]*Sqrt[1 + Sec[c + d*x]])/(d*Sqrt[-Cos[c + d*x]])", //
2889, 2887);}

// {2889, 2888}
public void test0644() {
	check(//
"Integrate[Sqrt[-Cos[c + d*x]]/Sqrt[2 + 3*Cos[c + d*x]], x]", //
 "(-4*Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticPi[5/3, ArcSin[Sqrt[2 + 3*Cos[c + d*x]]/(Sqrt[5]*Sqrt[Cos[c + d*x]])], 5]*Sqrt[-1 - Sec[c + d*x]]*Sqrt[1 - Sec[c + d*x]])/(3*d)", //
2889, 2888);}

// {2889, 2888}
public void test0645() {
	check(//
"Integrate[Sqrt[-Cos[c + d*x]]/Sqrt[-2 + 3*Cos[c + d*x]], x]", //
 "(-4*Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticPi[1/3, ArcSin[Sqrt[-2 + 3*Cos[c + d*x]]/Sqrt[Cos[c + d*x]]], 1/5]*Sqrt[-1 + Sec[c + d*x]]*Sqrt[1 + Sec[c + d*x]])/(3*Sqrt[5]*d)", //
2889, 2888);}

// {2889, 2887}
public void test0646() {
	check(//
"Integrate[Sqrt[-Cos[c + d*x]]/Sqrt[3 + 2*Cos[c + d*x]], x]", //
 "(-3*Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticPi[5/2, ArcSin[Sqrt[3 + 2*Cos[c + d*x]]/(Sqrt[5]*Sqrt[Cos[c + d*x]])], -5]*Sqrt[1 - Sec[c + d*x]]*Sqrt[1 + Sec[c + d*x]])/d", //
2889, 2887);}

// {2889, 2887}
public void test0647() {
	check(//
"Integrate[Sqrt[-Cos[c + d*x]]/Sqrt[3 - 2*Cos[c + d*x]], x]", //
 "(3*Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticPi[-1/2, ArcSin[Sqrt[3 - 2*Cos[c + d*x]]/Sqrt[Cos[c + d*x]]], -1/5]*Sqrt[1 - Sec[c + d*x]]*Sqrt[1 + Sec[c + d*x]])/(Sqrt[5]*d)", //
2889, 2887);}

// {4307, 2890}
public void test0648() {
	check(//
"Integrate[Sqrt[a + b*Cos[c + d*x]]*Sqrt[Sec[c + d*x]], x]", //
 "(-2*Sqrt[Cos[c + d*x]]*Sqrt[(a*(1 - Cos[c + d*x]))/(a + b*Cos[c + d*x])]*Sqrt[(a*(1 + Cos[c + d*x]))/(a + b*Cos[c + d*x])]*(a + b*Cos[c + d*x])*Csc[c + d*x]*EllipticPi[b/(a + b), ArcSin[(Sqrt[a + b]*Sqrt[Cos[c + d*x]])/Sqrt[a + b*Cos[c + d*x]]], -((a - b)/(a + b))]*Sqrt[Sec[c + d*x]])/(Sqrt[a + b]*d)", //
4307, 2890);}

// {4307, 2895}
public void test0649() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[a + b*Cos[c + d*x]], x]", //
 "(2*Sqrt[a + b]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticF[ArcSin[Sqrt[a + b*Cos[c + d*x]]/(Sqrt[a + b]*Sqrt[Cos[c + d*x]])], -((a + b)/(a - b))]*Sqrt[(a*(1 - Sec[c + d*x]))/(a + b)]*Sqrt[(a*(1 + Sec[c + d*x]))/(a - b)])/(a*d*Sqrt[Sec[c + d*x]])", //
4307, 2895);}

// {4307, 2888}
public void test0650() {
	check(//
"Integrate[1/(Sqrt[a + b*Cos[c + d*x]]*Sqrt[Sec[c + d*x]]), x]", //
 "(-2*Sqrt[a + b]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticPi[(a + b)/b, ArcSin[Sqrt[a + b*Cos[c + d*x]]/(Sqrt[a + b]*Sqrt[Cos[c + d*x]])], -((a + b)/(a - b))]*Sqrt[(a*(1 - Sec[c + d*x]))/(a + b)]*Sqrt[(a*(1 + Sec[c + d*x]))/(a - b)])/(b*d*Sqrt[Sec[c + d*x]])", //
4307, 2888);}

// {2854, 210}
public void test0651() {
	check(//
"Integrate[Sqrt[1 - Cos[x]]/Sqrt[a - Cos[x]], x]", //
 "-2*ArcTan[Sin[x]/(Sqrt[1 - Cos[x]]*Sqrt[a - Cos[x]])]", //
2854, 210);}

// {21, 2725}
public void test0652() {
	check(//
"Integrate[(B + B*Cos[c + d*x])/Sqrt[a + a*Cos[c + d*x]], x]", //
 "(2*B*Sin[c + d*x])/(d*Sqrt[a + a*Cos[c + d*x]])", //
21, 2725);}

// {2833, 8}
public void test0653() {
	check(//
"Integrate[(a + b*Cos[c + d*x])/(b + a*Cos[c + d*x])^2, x]", //
 "Sin[c + d*x]/(d*(b + a*Cos[c + d*x]))", //
2833, 8);}

// {2814, 2736}
public void test0654() {
	check(//
"Integrate[(3 + Cos[c + d*x])/(2 - Cos[c + d*x]), x]", //
 "-x + (5*x)/Sqrt[3] + (10*ArcTan[Sin[c + d*x]/(2 + Sqrt[3] - Cos[c + d*x])])/(Sqrt[3]*d)", //
2814, 2736);}

// {17, 2813}
public void test0655() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]*Sqrt[b*Cos[c + d*x]]*(A + B*Cos[c + d*x]), x]", //
 "(B*x*Sqrt[b*Cos[c + d*x]])/(2*Sqrt[Cos[c + d*x]]) + (A*Sqrt[b*Cos[c + d*x]]*Sin[c + d*x])/(d*Sqrt[Cos[c + d*x]]) + (B*Sqrt[Cos[c + d*x]]*Sqrt[b*Cos[c + d*x]]*Sin[c + d*x])/(2*d)", //
17, 2813);}

// {17, 2813}
public void test0656() {
	check(//
"Integrate[((b*Cos[c + d*x])^(3/2)*(A + B*Cos[c + d*x]))/Sqrt[Cos[c + d*x]], x]", //
 "(b*B*x*Sqrt[b*Cos[c + d*x]])/(2*Sqrt[Cos[c + d*x]]) + (A*b*Sqrt[b*Cos[c + d*x]]*Sin[c + d*x])/(d*Sqrt[Cos[c + d*x]]) + (b*B*Sqrt[Cos[c + d*x]]*Sqrt[b*Cos[c + d*x]]*Sin[c + d*x])/(2*d)", //
17, 2813);}

// {17, 2813}
public void test0657() {
	check(//
"Integrate[((b*Cos[c + d*x])^(5/2)*(A + B*Cos[c + d*x]))/Cos[c + d*x]^(3/2), x]", //
 "(b^2*B*x*Sqrt[b*Cos[c + d*x]])/(2*Sqrt[Cos[c + d*x]]) + (A*b^2*Sqrt[b*Cos[c + d*x]]*Sin[c + d*x])/(d*Sqrt[Cos[c + d*x]]) + (b^2*B*Sqrt[Cos[c + d*x]]*Sqrt[b*Cos[c + d*x]]*Sin[c + d*x])/(2*d)", //
17, 2813);}

// {17, 2813}
public void test0658() {
	check(//
"Integrate[(Cos[c + d*x]^(3/2)*(A + B*Cos[c + d*x]))/Sqrt[b*Cos[c + d*x]], x]", //
 "(B*x*Sqrt[Cos[c + d*x]])/(2*Sqrt[b*Cos[c + d*x]]) + (A*Sqrt[Cos[c + d*x]]*Sin[c + d*x])/(d*Sqrt[b*Cos[c + d*x]]) + (B*Cos[c + d*x]^(3/2)*Sin[c + d*x])/(2*d*Sqrt[b*Cos[c + d*x]])", //
17, 2813);}

// {17, 2813}
public void test0659() {
	check(//
"Integrate[(Cos[c + d*x]^(5/2)*(A + B*Cos[c + d*x]))/(b*Cos[c + d*x])^(3/2), x]", //
 "(B*x*Sqrt[Cos[c + d*x]])/(2*b*Sqrt[b*Cos[c + d*x]]) + (A*Sqrt[Cos[c + d*x]]*Sin[c + d*x])/(b*d*Sqrt[b*Cos[c + d*x]]) + (B*Cos[c + d*x]^(3/2)*Sin[c + d*x])/(2*b*d*Sqrt[b*Cos[c + d*x]])", //
17, 2813);}

// {17, 2813}
public void test0660() {
	check(//
"Integrate[(Cos[c + d*x]^(7/2)*(A + B*Cos[c + d*x]))/(b*Cos[c + d*x])^(5/2), x]", //
 "(B*x*Sqrt[Cos[c + d*x]])/(2*b^2*Sqrt[b*Cos[c + d*x]]) + (A*Sqrt[Cos[c + d*x]]*Sin[c + d*x])/(b^2*d*Sqrt[b*Cos[c + d*x]]) + (B*Cos[c + d*x]^(3/2)*Sin[c + d*x])/(2*b^2*d*Sqrt[b*Cos[c + d*x]])", //
17, 2813);}

// {2830, 2723}
public void test0661() {
	check(//
"Integrate[(a + a*Cos[c + d*x])^2*(A + B*Cos[c + d*x]), x]", //
 "(a^2*(3*A + 2*B)*x)/2 + (2*a^2*(3*A + 2*B)*Sin[c + d*x])/(3*d) + (a^2*(3*A + 2*B)*Cos[c + d*x]*Sin[c + d*x])/(6*d) + (B*(a + a*Cos[c + d*x])^2*Sin[c + d*x])/(3*d)", //
2830, 2723);}

// {2814, 2727}
public void test0662() {
	check(//
"Integrate[(A + B*Cos[c + d*x])/(a + a*Cos[c + d*x]), x]", //
 "(B*x)/a + ((A - B)*Sin[c + d*x])/(d*(a + a*Cos[c + d*x]))", //
2814, 2727);}

// {2829, 2727}
public void test0663() {
	check(//
"Integrate[(A + B*Cos[c + d*x])/(a + a*Cos[c + d*x])^2, x]", //
 "((A - B)*Sin[c + d*x])/(3*d*(a + a*Cos[c + d*x])^2) + ((A + 2*B)*Sin[c + d*x])/(3*d*(a^2 + a^2*Cos[c + d*x]))", //
2829, 2727);}

// {2830, 2725}
public void test0664() {
	check(//
"Integrate[Sqrt[a + a*Cos[c + d*x]]*(A + B*Cos[c + d*x]), x]", //
 "(2*a*(3*A + B)*Sin[c + d*x])/(3*d*Sqrt[a + a*Cos[c + d*x]]) + (2*B*Sqrt[a + a*Cos[c + d*x]]*Sin[c + d*x])/(3*d)", //
2830, 2725);}

// {3059, 2850}
public void test0665() {
	check(//
"Integrate[(Sqrt[a + a*Cos[c + d*x]]*(A + B*Cos[c + d*x]))/Cos[c + d*x]^(5/2), x]", //
 "(2*a*A*Sin[c + d*x])/(3*d*Cos[c + d*x]^(3/2)*Sqrt[a + a*Cos[c + d*x]]) + (2*a*(2*A + 3*B)*Sin[c + d*x])/(3*d*Sqrt[Cos[c + d*x]]*Sqrt[a + a*Cos[c + d*x]])", //
3059, 2850);}

// {2832, 2813}
public void test0666() {
	check(//
"Integrate[(a + b*Cos[c + d*x])^2*(A + B*Cos[c + d*x]), x]", //
 "((2*a^2*A + A*b^2 + 2*a*b*B)*x)/2 + (2*(3*a*A*b + a^2*B + b^2*B)*Sin[c + d*x])/(3*d) + (b*(3*A*b + 2*a*B)*Cos[c + d*x]*Sin[c + d*x])/(6*d) + (B*(a + b*Cos[c + d*x])^2*Sin[c + d*x])/(3*d)", //
2832, 2813);}

// {21, 2717}
public void test0667() {
	check(//
"Integrate[(Cos[c + d*x]*(a*B + b*B*Cos[c + d*x]))/(a + b*Cos[c + d*x]), x]", //
 "(B*Sin[c + d*x])/d", //
21, 2717);}

// {21, 8}
public void test0668() {
	check(//
"Integrate[(a*B + b*B*Cos[c + d*x])/(a + b*Cos[c + d*x]), x]", //
 "B*x", //
21, 8);}

// {21, 3855}
public void test0669() {
	check(//
"Integrate[((a*B + b*B*Cos[c + d*x])*Sec[c + d*x])/(a + b*Cos[c + d*x]), x]", //
 "(B*ArcTanh[Sin[c + d*x]])/d", //
21, 3855);}

// {21, 2719}
public void test0670() {
	check(//
"Integrate[(Sqrt[Cos[c + d*x]]*(a*B + b*B*Cos[c + d*x]))/(a + b*Cos[c + d*x]), x]", //
 "(2*B*EllipticE[(c + d*x)/2, 2])/d", //
21, 2719);}

// {21, 2720}
public void test0671() {
	check(//
"Integrate[(a*B + b*B*Cos[c + d*x])/(Sqrt[Cos[c + d*x]]*(a + b*Cos[c + d*x])), x]", //
 "(2*B*EllipticF[(c + d*x)/2, 2])/d", //
21, 2720);}

// {21, 2884}
public void test0672() {
	check(//
"Integrate[(a*B + b*B*Cos[c + d*x])/(Sqrt[Cos[c + d*x]]*(a + b*Cos[c + d*x])^2), x]", //
 "(2*B*EllipticPi[(2*b)/(a + b), (c + d*x)/2, 2])/((a + b)*d)", //
21, 2884);}

// {21, 2888}
public void test0673() {
	check(//
"Integrate[(Sqrt[Cos[c + d*x]]*(a*B + b*B*Cos[c + d*x]))/(a + b*Cos[c + d*x])^(3/2), x]", //
 "(-2*Sqrt[a + b]*B*Cot[c + d*x]*EllipticPi[(a + b)/b, ArcSin[Sqrt[a + b*Cos[c + d*x]]/(Sqrt[a + b]*Sqrt[Cos[c + d*x]])], -((a + b)/(a - b))]*Sqrt[(a*(1 - Sec[c + d*x]))/(a + b)]*Sqrt[(a*(1 + Sec[c + d*x]))/(a - b)])/(b*d)", //
21, 2888);}

// {21, 2895}
public void test0674() {
	check(//
"Integrate[(a*B + b*B*Cos[c + d*x])/(Sqrt[Cos[c + d*x]]*(a + b*Cos[c + d*x])^(3/2)), x]", //
 "(2*Sqrt[a + b]*B*Cot[c + d*x]*EllipticF[ArcSin[Sqrt[a + b*Cos[c + d*x]]/(Sqrt[a + b]*Sqrt[Cos[c + d*x]])], -((a + b)/(a - b))]*Sqrt[(a*(1 - Sec[c + d*x]))/(a + b)]*Sqrt[(a*(1 + Sec[c + d*x]))/(a - b)])/(a*d)", //
21, 2895);}

// {3074, 3073}
public void test0675() {
	check(//
"Integrate[(1 + Cos[c + d*x])/(Sqrt[2 - 3*Cos[c + d*x]]*Cos[c + d*x]^(3/2)), x]", //
 "(Sqrt[5]*Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticE[ArcSin[Sqrt[2 - 3*Cos[c + d*x]]/Sqrt[-Cos[c + d*x]]], 1/5]*Sqrt[-1 + Sec[c + d*x]]*Sqrt[1 + Sec[c + d*x]])/d", //
3074, 3073);}

// {3074, 3073}
public void test0676() {
	check(//
"Integrate[(1 + Cos[c + d*x])/(Sqrt[-2 - 3*Cos[c + d*x]]*Cos[c + d*x]^(3/2)), x]", //
 "(Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticE[ArcSin[Sqrt[-2 - 3*Cos[c + d*x]]/(Sqrt[5]*Sqrt[-Cos[c + d*x]])], 5]*Sqrt[-1 - Sec[c + d*x]]*Sqrt[1 - Sec[c + d*x]])/d", //
3074, 3073);}

// {3074, 3073}
public void test0677() {
	check(//
"Integrate[(1 + Cos[c + d*x])/(Cos[c + d*x]^(3/2)*Sqrt[-3 + 2*Cos[c + d*x]]), x]", //
 "(-2*Sqrt[5]*Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticE[ArcSin[Sqrt[-3 + 2*Cos[c + d*x]]/Sqrt[-Cos[c + d*x]]], -1/5]*Sqrt[1 - Sec[c + d*x]]*Sqrt[1 + Sec[c + d*x]])/(3*d)", //
3074, 3073);}

// {3074, 3073}
public void test0678() {
	check(//
"Integrate[(1 + Cos[c + d*x])/(Sqrt[-3 - 2*Cos[c + d*x]]*Cos[c + d*x]^(3/2)), x]", //
 "(-2*Sqrt[-Cos[c + d*x]]*Sqrt[Cos[c + d*x]]*Csc[c + d*x]*EllipticE[ArcSin[Sqrt[-3 - 2*Cos[c + d*x]]/(Sqrt[5]*Sqrt[-Cos[c + d*x]])], -5]*Sqrt[1 - Sec[c + d*x]]*Sqrt[1 + Sec[c + d*x]])/(3*d)", //
3074, 3073);}

// {3092}
public void test0681() {
	check(//
"Integrate[Cos[c + d*x]*(A + C*Cos[c + d*x]^2), x]", //
 "((A + C)*Sin[c + d*x])/d - (C*Sin[c + d*x]^3)/(3*d)", //
3092);}

// {3093, 3855}
public void test0682() {
	check(//
"Integrate[(A + C*Cos[c + d*x]^2)*Sec[c + d*x], x]", //
 "(A*ArcTanh[Sin[c + d*x]])/d + (C*Sin[c + d*x])/d", //
3093, 3855);}

// {3091, 3855}
public void test0683() {
	check(//
"Integrate[(A + C*Cos[c + d*x]^2)*Sec[c + d*x]^3, x]", //
 "((A + 2*C)*ArcTanh[Sin[c + d*x]])/(2*d) + (A*Sec[c + d*x]*Tan[c + d*x])/(2*d)", //
3091, 3855);}

// {3091, 8}
public void test0684() {
	check(//
"Integrate[(A + C*Cos[c + d*x]^2)*Sec[c + d*x]^2, x]", //
 "C*x + (A*Tan[c + d*x])/d", //
3091, 8);}

// {3093, 2722}
public void test0685() {
	check(//
"Integrate[(b*Cos[c + d*x])^m*(A + C*Cos[c + d*x]^2), x]", //
 "(C*(b*Cos[c + d*x])^(1 + m)*Sin[c + d*x])/(b*d*(2 + m)) - ((A + (C*(1 + m))/(2 + m))*(b*Cos[c + d*x])^(1 + m)*Hypergeometric2F1[1/2, (1 + m)/2, (3 + m)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(b*d*(1 + m)*Sqrt[Sin[c + d*x]^2])", //
3093, 2722);}

// {3093, 2722}
public void test0686() {
	check(//
"Integrate[(b*Cos[c + d*x])^(1/3)*(A + C*Cos[c + d*x]^2), x]", //
 "(3*C*(b*Cos[c + d*x])^(4/3)*Sin[c + d*x])/(7*b*d) - (3*(7*A + 4*C)*(b*Cos[c + d*x])^(4/3)*Hypergeometric2F1[1/2, 2/3, 5/3, Cos[c + d*x]^2]*Sin[c + d*x])/(28*b*d*Sqrt[Sin[c + d*x]^2])", //
3093, 2722);}

// {3093, 2722}
public void test0687() {
	check(//
"Integrate[(b*Cos[c + d*x])^(2/3)*(A + C*Cos[c + d*x]^2), x]", //
 "(3*C*(b*Cos[c + d*x])^(5/3)*Sin[c + d*x])/(8*b*d) - (3*(8*A + 5*C)*(b*Cos[c + d*x])^(5/3)*Hypergeometric2F1[1/2, 5/6, 11/6, Cos[c + d*x]^2]*Sin[c + d*x])/(40*b*d*Sqrt[Sin[c + d*x]^2])", //
3093, 2722);}

// {3093, 2722}
public void test0688() {
	check(//
"Integrate[(b*Cos[c + d*x])^(4/3)*(A + C*Cos[c + d*x]^2), x]", //
 "(3*C*(b*Cos[c + d*x])^(7/3)*Sin[c + d*x])/(10*b*d) - (3*(10*A + 7*C)*(b*Cos[c + d*x])^(7/3)*Hypergeometric2F1[1/2, 7/6, 13/6, Cos[c + d*x]^2]*Sin[c + d*x])/(70*b*d*Sqrt[Sin[c + d*x]^2])", //
3093, 2722);}

// {3093, 2722}
public void test0689() {
	check(//
"Integrate[(A + C*Cos[c + d*x]^2)/(b*Cos[c + d*x])^(1/3), x]", //
 "(3*C*(b*Cos[c + d*x])^(2/3)*Sin[c + d*x])/(5*b*d) - (3*(5*A + 2*C)*(b*Cos[c + d*x])^(2/3)*Hypergeometric2F1[1/3, 1/2, 4/3, Cos[c + d*x]^2]*Sin[c + d*x])/(10*b*d*Sqrt[Sin[c + d*x]^2])", //
3093, 2722);}

// {3093, 2722}
public void test0690() {
	check(//
"Integrate[(A + C*Cos[c + d*x]^2)/(b*Cos[c + d*x])^(2/3), x]", //
 "(3*C*(b*Cos[c + d*x])^(1/3)*Sin[c + d*x])/(4*b*d) - (3*(4*A + C)*(b*Cos[c + d*x])^(1/3)*Hypergeometric2F1[1/6, 1/2, 7/6, Cos[c + d*x]^2]*Sin[c + d*x])/(4*b*d*Sqrt[Sin[c + d*x]^2])", //
3093, 2722);}

// {3091, 2722}
public void test0691() {
	check(//
"Integrate[(A + C*Cos[c + d*x]^2)/(b*Cos[c + d*x])^(4/3), x]", //
 "(3*A*Sin[c + d*x])/(b*d*(b*Cos[c + d*x])^(1/3)) + (3*(2*A - C)*(b*Cos[c + d*x])^(5/3)*Hypergeometric2F1[1/2, 5/6, 11/6, Cos[c + d*x]^2]*Sin[c + d*x])/(5*b^3*d*Sqrt[Sin[c + d*x]^2])", //
3091, 2722);}

// {3093, 2722}
public void test0692() {
	check(//
"Integrate[(b*Cos[c + d*x])^n*(A + C*Cos[c + d*x]^2), x]", //
 "(C*(b*Cos[c + d*x])^(1 + n)*Sin[c + d*x])/(b*d*(2 + n)) - ((A + (C*(1 + n))/(2 + n))*(b*Cos[c + d*x])^(1 + n)*Hypergeometric2F1[1/2, (1 + n)/2, (3 + n)/2, Cos[c + d*x]^2]*Sin[c + d*x])/(b*d*(1 + n)*Sqrt[Sin[c + d*x]^2])", //
3093, 2722);}

// {3103, 2813}
public void test0693() {
	check(//
"Integrate[(a + a*Cos[c + d*x])*(A + C*Cos[c + d*x]^2), x]", //
 "(a*(2*A + C)*x)/2 + (a*(3*A + C)*Sin[c + d*x])/(3*d) - (a*C*Cos[c + d*x]*Sin[c + d*x])/(6*d) + (C*(a + a*Cos[c + d*x])^2*Sin[c + d*x])/(3*a*d)", //
3103, 2813);}

// {3121, 2813}
public void test0694() {
	check(//
"Integrate[(Cos[c + d*x]*(A + C*Cos[c + d*x]^2))/(a + a*Cos[c + d*x]), x]", //
 "((2*A + 3*C)*x)/(2*a) - ((A + 2*C)*Sin[c + d*x])/(a*d) + ((2*A + 3*C)*Cos[c + d*x]*Sin[c + d*x])/(2*a*d) - ((A + C)*Cos[c + d*x]^2*Sin[c + d*x])/(d*(a + a*Cos[c + d*x]))", //
3121, 2813);}

// {3102, 2813}
public void test0695() {
	check(//
"Integrate[(a + a*Cos[c + d*x])*(B*Cos[c + d*x] + C*Cos[c + d*x]^2), x]", //
 "(a*(B + C)*x)/2 + (a*(3*B + C)*Sin[c + d*x])/(3*d) + (a*(3*B - C)*Cos[c + d*x]*Sin[c + d*x])/(6*d) + (C*(a + a*Cos[c + d*x])^2*Sin[c + d*x])/(3*a*d)", //
3102, 2813);}

// {3108, 2813}
public void test0696() {
	check(//
"Integrate[(a + a*Cos[c + d*x])*(B*Cos[c + d*x] + C*Cos[c + d*x]^2)*Sec[c + d*x], x]", //
 "(a*(2*B + C)*x)/2 + (a*(B + C)*Sin[c + d*x])/d + (a*C*Cos[c + d*x]*Sin[c + d*x])/(2*d)", //
3108, 2813);}

// {3102, 2813}
public void test0697() {
	check(//
"Integrate[Cos[c + d*x]*(A + B*Cos[c + d*x] + C*Cos[c + d*x]^2), x]", //
 "(B*x)/2 + ((3*A + 2*C)*Sin[c + d*x])/(3*d) + (B*Cos[c + d*x]*Sin[c + d*x])/(2*d) + (C*Cos[c + d*x]^2*Sin[c + d*x])/(3*d)", //
3102, 2813);}

// {3102, 2813}
public void test0698() {
	check(//
"Integrate[(a + a*Cos[c + d*x])*(A + B*Cos[c + d*x] + C*Cos[c + d*x]^2), x]", //
 "(a*(2*A + B + C)*x)/2 + (a*(3*A + 3*B + C)*Sin[c + d*x])/(3*d) + (a*(3*B - C)*Cos[c + d*x]*Sin[c + d*x])/(6*d) + (C*(a + a*Cos[c + d*x])^2*Sin[c + d*x])/(3*a*d)", //
3102, 2813);}

// {3120, 2813}
public void test0699() {
	check(//
"Integrate[(Cos[c + d*x]*(A + B*Cos[c + d*x] + C*Cos[c + d*x]^2))/(a + a*Cos[c + d*x]), x]", //
 "((2*A - 2*B + 3*C)*x)/(2*a) - ((A - 2*B + 2*C)*Sin[c + d*x])/(a*d) + ((2*A - 2*B + 3*C)*Cos[c + d*x]*Sin[c + d*x])/(2*a*d) - ((A - B + C)*Cos[c + d*x]^2*Sin[c + d*x])/(d*(a + a*Cos[c + d*x]))", //
3120, 2813);}

// {3103, 2813}
public void test0700() {
	check(//
"Integrate[(a + b*Cos[c + d*x])*(A + C*Cos[c + d*x]^2), x]", //
 "(a*(2*A + C)*x)/2 - ((a^2*C - b^2*(3*A + 2*C))*Sin[c + d*x])/(3*b*d) - (a*C*Cos[c + d*x]*Sin[c + d*x])/(6*d) + (C*(a + b*Cos[c + d*x])^2*Sin[c + d*x])/(3*b*d)", //
3103, 2813);}

// {3108, 2813}
public void test0701() {
	check(//
"Integrate[(a + b*Cos[c + d*x])*(B*Cos[c + d*x] + C*Cos[c + d*x]^2)*Sec[c + d*x], x]", //
 "((2*a*B + b*C)*x)/2 + ((b*B + a*C)*Sin[c + d*x])/d + (b*C*Cos[c + d*x]*Sin[c + d*x])/(2*d)", //
3108, 2813);}

// {3254, 2718}
public void test0702() {
	check(//
"Integrate[Sin[x]^3/(a - a*Cos[x]^2), x]", //
 "-(Cos[x]/a)", //
3254, 2718);}

// {3254, 8}
public void test0703() {
	check(//
"Integrate[Sin[x]^2/(a - a*Cos[x]^2), x]", //
 "x/a", //
3254, 8);}

// {3254, 3855}
public void test0704() {
	check(//
"Integrate[Sin[x]/(a - a*Cos[x]^2), x]", //
 "-(ArcTanh[Cos[x]]/a)", //
3254, 3855);}

// {3269, 211}
public void test0705() {
	check(//
"Integrate[Sin[x]/(a + b*Cos[x]^2), x]", //
 "-(ArcTan[(Sqrt[b]*Cos[x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]))", //
3269, 211);}

// {3260, 211}
public void test0706() {
	check(//
"Integrate[(a + b*Cos[x]^2)^(-1), x]", //
 "-(ArcTan[(Sqrt[a + b]*Cot[x])/Sqrt[a]]/(Sqrt[a]*Sqrt[a + b]))", //
3260, 211);}

// {3265, 214}
public void test0707() {
	check(//
"Integrate[Cos[x]/(a + b*Cos[x]^2), x]", //
 "ArcTanh[(Sqrt[b]*Sin[x])/Sqrt[a + b]]/(Sqrt[b]*Sqrt[a + b])", //
3265, 214);}

// {3260, 211}
public void test0708() {
	check(//
"Integrate[(a + b*Cos[x]^2)^(-1), x]", //
 "-(ArcTan[(Sqrt[a + b]*Cot[x])/Sqrt[a]]/(Sqrt[a]*Sqrt[a + b]))", //
3260, 211);}

// {3260, 209}
public void test0709() {
	check(//
"Integrate[(1 + Cos[x]^2)^(-1), x]", //
 "x/Sqrt[2] - ArcTan[(Cos[x]*Sin[x])/(1 + Sqrt[2] + Cos[x]^2)]/Sqrt[2]", //
3260, 209);}

// {3257, 3256}
public void test0710() {
	check(//
"Integrate[Sqrt[-1 - Cos[x]^2], x]", //
 "(Sqrt[-1 - Cos[x]^2]*EllipticE[Pi/2 + x, -1])/Sqrt[1 + Cos[x]^2]", //
3257, 3256);}

// {3257, 3256}
public void test0711() {
	check(//
"Integrate[Sqrt[a + b*Cos[x]^2], x]", //
 "(Sqrt[a + b*Cos[x]^2]*EllipticE[Pi/2 + x, -(b/a)])/Sqrt[1 + (b*Cos[x]^2)/a]", //
3257, 3256);}

// {3262, 3261}
public void test0712() {
	check(//
"Integrate[1/Sqrt[-1 - Cos[x]^2], x]", //
 "(Sqrt[1 + Cos[x]^2]*EllipticF[Pi/2 + x, -1])/Sqrt[-1 - Cos[x]^2]", //
3262, 3261);}

// {3262, 3261}
public void test0713() {
	check(//
"Integrate[1/Sqrt[a + b*Cos[x]^2], x]", //
 "(Sqrt[1 + (b*Cos[x]^2)/a]*EllipticF[Pi/2 + x, -(b/a)])/Sqrt[a + b*Cos[x]^2]", //
3262, 3261);}

// {3265, 222}
public void test0714() {
	check(//
"Integrate[Cos[x]/Sqrt[1 + Cos[x]^2], x]", //
 "ArcSin[Sin[x]/Sqrt[2]]", //
3265, 222);}

// {3265, 222}
public void test0715() {
	check(//
"Integrate[Cos[5 + 3*x]/Sqrt[3 + Cos[5 + 3*x]^2], x]", //
 "ArcSin[Sin[5 + 3*x]/2]/3", //
3265, 222);}

// {3265, 221}
public void test0716() {
	check(//
"Integrate[Cos[x]/Sqrt[4 - Cos[x]^2], x]", //
 "ArcSinh[Sin[x]/Sqrt[3]]", //
3265, 221);}

// {2908, 4058}
public void test0717() {
	check(//
"Integrate[1/((a + b*Cos[e + f*x])*Sqrt[c + d*Sec[e + f*x]]), x]", //
 "(2*EllipticPi[(2*a)/(a + b), ArcSin[Sqrt[1 - Sec[e + f*x]]/Sqrt[2]], (2*d)/(c + d)]*Sqrt[(c + d*Sec[e + f*x])/(c + d)]*Tan[e + f*x])/((a + b)*f*Sqrt[c + d*Sec[e + f*x]]*Sqrt[-Tan[e + f*x]^2])", //
2908, 4058);}

// {3554, 8}
public void test0718() {
	check(//
"Integrate[Tan[c + d*x]^2, x]", //
 "-x + Tan[c + d*x]/d", //
3554, 8);}

// {3554, 3556}
public void test0719() {
	check(//
"Integrate[Tan[c + d*x]^3, x]", //
 "Log[Cos[c + d*x]]/d + Tan[c + d*x]^2/(2*d)", //
3554, 3556);}

// {3557, 371}
public void test0720() {
	check(//
"Integrate[(b*Tan[c + d*x])^n, x]", //
 "(Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, -Tan[c + d*x]^2]*(b*Tan[c + d*x])^(1 + n))/(b*d*(1 + n))", //
3557, 371);}

// {3739, 3556}
public void test0721() {
	check(//
"Integrate[Sqrt[b*Tan[c + d*x]^2], x]", //
 "-((Cot[c + d*x]*Log[Cos[c + d*x]]*Sqrt[b*Tan[c + d*x]^2])/d)", //
3739, 3556);}

// {3739, 3556}
public void test0722() {
	check(//
"Integrate[1/Sqrt[b*Tan[c + d*x]^2], x]", //
 "(Log[Sin[c + d*x]]*Tan[c + d*x])/(d*Sqrt[b*Tan[c + d*x]^2])", //
3739, 3556);}

// {3740, 3556}
public void test0723() {
	check(//
"Integrate[(b*Tan[c + d*x]^p)^p^(-1), x]", //
 "-((Cot[c + d*x]*Log[Cos[c + d*x]]*(b*Tan[c + d*x]^p)^p^(-1))/d)", //
3740, 3556);}

// {2671, 30}
public void test0724() {
	check(//
"Integrate[Csc[a + b*x]^2*Sqrt[d*Tan[a + b*x]], x]", //
 "(-2*d)/(b*Sqrt[d*Tan[a + b*x]])", //
2671, 30);}

// {2671, 30}
public void test0725() {
	check(//
"Integrate[Csc[a + b*x]^2*(d*Tan[a + b*x])^(3/2), x]", //
 "(2*d*Sqrt[d*Tan[a + b*x]])/b", //
2671, 30);}

// {2671, 30}
public void test0726() {
	check(//
"Integrate[Csc[a + b*x]^2*(d*Tan[a + b*x])^(5/2), x]", //
 "(2*d*(d*Tan[a + b*x])^(3/2))/(3*b)", //
2671, 30);}

// {2671, 30}
public void test0727() {
	check(//
"Integrate[Csc[a + b*x]^2/Sqrt[d*Tan[a + b*x]], x]", //
 "(-2*d)/(3*b*(d*Tan[a + b*x])^(3/2))", //
2671, 30);}

// {2671, 30}
public void test0728() {
	check(//
"Integrate[Csc[a + b*x]^2/(d*Tan[a + b*x])^(3/2), x]", //
 "(-2*d)/(5*b*(d*Tan[a + b*x])^(5/2))", //
2671, 30);}

// {2671, 30}
public void test0729() {
	check(//
"Integrate[Csc[a + b*x]^2/(d*Tan[a + b*x])^(5/2), x]", //
 "(-2*d)/(7*b*(d*Tan[a + b*x])^(7/2))", //
2671, 30);}

// {2678, 2669}
public void test0730() {
	check(//
"Integrate[(a*Sin[e + f*x])^(5/2)*Sqrt[b*Tan[e + f*x]], x]", //
 "(-8*a^2*b*Sqrt[a*Sin[e + f*x]])/(5*f*Sqrt[b*Tan[e + f*x]]) - (2*b*(a*Sin[e + f*x])^(5/2))/(5*f*Sqrt[b*Tan[e + f*x]])", //
2678, 2669);}

// {2681, 2720}
public void test0731() {
	check(//
"Integrate[Sqrt[b*Tan[e + f*x]]/Sqrt[a*Sin[e + f*x]], x]", //
 "(2*Sqrt[Cos[e + f*x]]*EllipticF[(e + f*x)/2, 2]*Sqrt[b*Tan[e + f*x]])/(f*Sqrt[a*Sin[e + f*x]])", //
2681, 2720);}

// {2678, 2669}
public void test0732() {
	check(//
"Integrate[(a*Sin[e + f*x])^(3/2)*(b*Tan[e + f*x])^(3/2), x]", //
 "(8*a^2*b*Sqrt[b*Tan[e + f*x]])/(3*f*Sqrt[a*Sin[e + f*x]]) - (2*b*(a*Sin[e + f*x])^(3/2)*Sqrt[b*Tan[e + f*x]])/(3*f)", //
2678, 2669);}

// {2678, 2669}
public void test0733() {
	check(//
"Integrate[(a*Sin[e + f*x])^(7/2)/Sqrt[b*Tan[e + f*x]], x]", //
 "(-8*a^2*b*(a*Sin[e + f*x])^(3/2))/(21*f*(b*Tan[e + f*x])^(3/2)) - (2*b*(a*Sin[e + f*x])^(7/2))/(7*f*(b*Tan[e + f*x])^(3/2))", //
2678, 2669);}

// {2681, 2719}
public void test0734() {
	check(//
"Integrate[Sqrt[a*Sin[e + f*x]]/Sqrt[b*Tan[e + f*x]], x]", //
 "(2*EllipticE[(e + f*x)/2, 2]*Sqrt[a*Sin[e + f*x]])/(f*Sqrt[Cos[e + f*x]]*Sqrt[b*Tan[e + f*x]])", //
2681, 2719);}

// {2682, 2657}
public void test0735() {
	check(//
"Integrate[(b*Sin[e + f*x])^(4/3)*Sqrt[d*Tan[e + f*x]], x]", //
 "(6*(Cos[e + f*x]^2)^(3/4)*Hypergeometric2F1[3/4, 17/12, 29/12, Sin[e + f*x]^2]*(b*Sin[e + f*x])^(4/3)*(d*Tan[e + f*x])^(3/2))/(17*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0736() {
	check(//
"Integrate[(b*Sin[e + f*x])^(1/3)*Sqrt[d*Tan[e + f*x]], x]", //
 "(6*(Cos[e + f*x]^2)^(3/4)*Hypergeometric2F1[3/4, 11/12, 23/12, Sin[e + f*x]^2]*(b*Sin[e + f*x])^(1/3)*(d*Tan[e + f*x])^(3/2))/(11*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0737() {
	check(//
"Integrate[Sqrt[d*Tan[e + f*x]]/(b*Sin[e + f*x])^(1/3), x]", //
 "(6*(Cos[e + f*x]^2)^(3/4)*Hypergeometric2F1[7/12, 3/4, 19/12, Sin[e + f*x]^2]*(d*Tan[e + f*x])^(3/2))/(7*d*f*(b*Sin[e + f*x])^(1/3))", //
2682, 2657);}

// {2682, 2657}
public void test0738() {
	check(//
"Integrate[Sqrt[d*Tan[e + f*x]]/(b*Sin[e + f*x])^(4/3), x]", //
 "(6*(Cos[e + f*x]^2)^(3/4)*Hypergeometric2F1[1/12, 3/4, 13/12, Sin[e + f*x]^2]*(d*Tan[e + f*x])^(3/2))/(d*f*(b*Sin[e + f*x])^(4/3))", //
2682, 2657);}

// {2682, 2657}
public void test0739() {
	check(//
"Integrate[(b*Sin[e + f*x])^(4/3)*(d*Tan[e + f*x])^(3/2), x]", //
 "(6*(Cos[e + f*x]^2)^(5/4)*Hypergeometric2F1[5/4, 23/12, 35/12, Sin[e + f*x]^2]*(b*Sin[e + f*x])^(4/3)*(d*Tan[e + f*x])^(5/2))/(23*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0740() {
	check(//
"Integrate[(b*Sin[e + f*x])^(1/3)*(d*Tan[e + f*x])^(3/2), x]", //
 "(6*(Cos[e + f*x]^2)^(5/4)*Hypergeometric2F1[5/4, 17/12, 29/12, Sin[e + f*x]^2]*(b*Sin[e + f*x])^(1/3)*(d*Tan[e + f*x])^(5/2))/(17*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0741() {
	check(//
"Integrate[(d*Tan[e + f*x])^(3/2)/(b*Sin[e + f*x])^(1/3), x]", //
 "(6*(Cos[e + f*x]^2)^(5/4)*Hypergeometric2F1[13/12, 5/4, 25/12, Sin[e + f*x]^2]*(d*Tan[e + f*x])^(5/2))/(13*d*f*(b*Sin[e + f*x])^(1/3))", //
2682, 2657);}

// {2682, 2657}
public void test0742() {
	check(//
"Integrate[(d*Tan[e + f*x])^(3/2)/(b*Sin[e + f*x])^(4/3), x]", //
 "(6*(Cos[e + f*x]^2)^(5/4)*Hypergeometric2F1[7/12, 5/4, 19/12, Sin[e + f*x]^2]*(d*Tan[e + f*x])^(5/2))/(7*d*f*(b*Sin[e + f*x])^(4/3))", //
2682, 2657);}

// {2682, 2657}
public void test0743() {
	check(//
"Integrate[Sqrt[b*Sin[e + f*x]]*(d*Tan[e + f*x])^(4/3), x]", //
 "(6*(Cos[e + f*x]^2)^(7/6)*Hypergeometric2F1[7/6, 17/12, 29/12, Sin[e + f*x]^2]*Sqrt[b*Sin[e + f*x]]*(d*Tan[e + f*x])^(7/3))/(17*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0744() {
	check(//
"Integrate[Sqrt[b*Sin[e + f*x]]*(d*Tan[e + f*x])^(1/3), x]", //
 "(6*(Cos[e + f*x]^2)^(2/3)*Hypergeometric2F1[2/3, 11/12, 23/12, Sin[e + f*x]^2]*Sqrt[b*Sin[e + f*x]]*(d*Tan[e + f*x])^(4/3))/(11*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0745() {
	check(//
"Integrate[Sqrt[b*Sin[e + f*x]]/(d*Tan[e + f*x])^(1/3), x]", //
 "(6*(Cos[e + f*x]^2)^(1/3)*Hypergeometric2F1[1/3, 7/12, 19/12, Sin[e + f*x]^2]*Sqrt[b*Sin[e + f*x]]*(d*Tan[e + f*x])^(2/3))/(7*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0746() {
	check(//
"Integrate[Sqrt[b*Sin[e + f*x]]/(d*Tan[e + f*x])^(4/3), x]", //
 "(6*Hypergeometric2F1[-1/6, 1/12, 13/12, Sin[e + f*x]^2]*Sqrt[b*Sin[e + f*x]])/(d*f*(Cos[e + f*x]^2)^(1/6)*(d*Tan[e + f*x])^(1/3))", //
2682, 2657);}

// {2682, 2657}
public void test0747() {
	check(//
"Integrate[(b*Sin[e + f*x])^(3/2)*(d*Tan[e + f*x])^(4/3), x]", //
 "(6*(Cos[e + f*x]^2)^(7/6)*Hypergeometric2F1[7/6, 23/12, 35/12, Sin[e + f*x]^2]*(b*Sin[e + f*x])^(3/2)*(d*Tan[e + f*x])^(7/3))/(23*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0748() {
	check(//
"Integrate[(b*Sin[e + f*x])^(3/2)*(d*Tan[e + f*x])^(1/3), x]", //
 "(6*(Cos[e + f*x]^2)^(2/3)*Hypergeometric2F1[2/3, 17/12, 29/12, Sin[e + f*x]^2]*(b*Sin[e + f*x])^(3/2)*(d*Tan[e + f*x])^(4/3))/(17*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0749() {
	check(//
"Integrate[(b*Sin[e + f*x])^(3/2)/(d*Tan[e + f*x])^(1/3), x]", //
 "(6*(Cos[e + f*x]^2)^(1/3)*Hypergeometric2F1[1/3, 13/12, 25/12, Sin[e + f*x]^2]*(b*Sin[e + f*x])^(3/2)*(d*Tan[e + f*x])^(2/3))/(13*d*f)", //
2682, 2657);}

// {2682, 2657}
public void test0750() {
	check(//
"Integrate[(b*Sin[e + f*x])^(3/2)/(d*Tan[e + f*x])^(4/3), x]", //
 "(6*Hypergeometric2F1[-1/6, 7/12, 19/12, Sin[e + f*x]^2]*(b*Sin[e + f*x])^(3/2))/(7*d*f*(Cos[e + f*x]^2)^(1/6)*(d*Tan[e + f*x])^(1/3))", //
2682, 2657);}

// {2672, 371}
public void test0751() {
	check(//
"Integrate[(a*Sin[e + f*x])^m*Tan[e + f*x]^3, x]", //
 "(Hypergeometric2F1[2, (4 + m)/2, (6 + m)/2, Sin[e + f*x]^2]*(a*Sin[e + f*x])^(4 + m))/(a^4*f*(4 + m))", //
2672, 371);}

// {2672, 371}
public void test0752() {
	check(//
"Integrate[(a*Sin[e + f*x])^m*Tan[e + f*x], x]", //
 "(Hypergeometric2F1[1, (2 + m)/2, (4 + m)/2, Sin[e + f*x]^2]*(a*Sin[e + f*x])^(2 + m))/(a^2*f*(2 + m))", //
2672, 371);}

// {2672, 30}
public void test0753() {
	check(//
"Integrate[Cot[e + f*x]*(a*Sin[e + f*x])^m, x]", //
 "(a*Sin[e + f*x])^m/(f*m)", //
2672, 30);}

// {2680, 2657}
public void test0754() {
	check(//
"Integrate[(a*Sin[e + f*x])^m*Tan[e + f*x]^4, x]", //
 "(Sqrt[Cos[e + f*x]^2]*Hypergeometric2F1[5/2, (5 + m)/2, (7 + m)/2, Sin[e + f*x]^2]*Sec[e + f*x]*(a*Sin[e + f*x])^(5 + m))/(a^5*f*(5 + m))", //
2680, 2657);}

// {2680, 2657}
public void test0755() {
	check(//
"Integrate[(a*Sin[e + f*x])^m*Tan[e + f*x]^2, x]", //
 "(Sqrt[Cos[e + f*x]^2]*Hypergeometric2F1[3/2, (3 + m)/2, (5 + m)/2, Sin[e + f*x]^2]*Sec[e + f*x]*(a*Sin[e + f*x])^(3 + m))/(a^3*f*(3 + m))", //
2680, 2657);}

// {2680, 2657}
public void test0756() {
	check(//
"Integrate[Cot[e + f*x]^2*(a*Sin[e + f*x])^m, x]", //
 "-((a*Cos[e + f*x]*Hypergeometric2F1[-1/2, (-1 + m)/2, (1 + m)/2, Sin[e + f*x]^2]*(a*Sin[e + f*x])^(-1 + m))/(f*(1 - m)*Sqrt[Cos[e + f*x]^2]))", //
2680, 2657);}

// {2680, 2657}
public void test0757() {
	check(//
"Integrate[Cot[e + f*x]^4*(a*Sin[e + f*x])^m, x]", //
 "-((a^3*Cos[e + f*x]*Hypergeometric2F1[-3/2, (-3 + m)/2, (-1 + m)/2, Sin[e + f*x]^2]*(a*Sin[e + f*x])^(-3 + m))/(f*(3 - m)*Sqrt[Cos[e + f*x]^2]))", //
2680, 2657);}

// {2682, 2657}
public void test0758() {
	check(//
"Integrate[(a*Sin[e + f*x])^m*(b*Tan[e + f*x])^(3/2), x]", //
 "(2*(Cos[e + f*x]^2)^(5/4)*Hypergeometric2F1[5/4, (5 + 2*m)/4, (9 + 2*m)/4, Sin[e + f*x]^2]*(a*Sin[e + f*x])^m*(b*Tan[e + f*x])^(5/2))/(b*f*(5 + 2*m))", //
2682, 2657);}

// {2682, 2657}
public void test0759() {
	check(//
"Integrate[(a*Sin[e + f*x])^m*Sqrt[b*Tan[e + f*x]], x]", //
 "(2*(Cos[e + f*x]^2)^(3/4)*Hypergeometric2F1[3/4, (3 + 2*m)/4, (7 + 2*m)/4, Sin[e + f*x]^2]*(a*Sin[e + f*x])^m*(b*Tan[e + f*x])^(3/2))/(b*f*(3 + 2*m))", //
2682, 2657);}

// {2682, 2657}
public void test0760() {
	check(//
"Integrate[(a*Sin[e + f*x])^m/Sqrt[b*Tan[e + f*x]], x]", //
 "(2*(Cos[e + f*x]^2)^(1/4)*Hypergeometric2F1[1/4, (1 + 2*m)/4, (5 + 2*m)/4, Sin[e + f*x]^2]*(a*Sin[e + f*x])^m*Sqrt[b*Tan[e + f*x]])/(b*f*(1 + 2*m))", //
2682, 2657);}

// {2682, 2657}
public void test0761() {
	check(//
"Integrate[(a*Sin[e + f*x])^m/(b*Tan[e + f*x])^(3/2), x]", //
 "(-2*Hypergeometric2F1[-1/4, (-1 + 2*m)/4, (3 + 2*m)/4, Sin[e + f*x]^2]*(a*Sin[e + f*x])^m)/(b*f*(1 - 2*m)*(Cos[e + f*x]^2)^(1/4)*Sqrt[b*Tan[e + f*x]])", //
2682, 2657);}

// {2682, 2657}
public void test0762() {
	check(//
"Integrate[(a*Sin[e + f*x])^m*(b*Tan[e + f*x])^n, x]", //
 "((Cos[e + f*x]^2)^((1 + n)/2)*Hypergeometric2F1[(1 + n)/2, (1 + m + n)/2, (3 + m + n)/2, Sin[e + f*x]^2]*(a*Sin[e + f*x])^m*(b*Tan[e + f*x])^(1 + n))/(b*f*(1 + m + n))", //
2682, 2657);}

// {2671, 371}
public void test0763() {
	check(//
"Integrate[Sin[e + f*x]^4*(b*Tan[e + f*x])^n, x]", //
 "(Hypergeometric2F1[3, (5 + n)/2, (7 + n)/2, -Tan[e + f*x]^2]*(b*Tan[e + f*x])^(5 + n))/(b^5*f*(5 + n))", //
2671, 371);}

// {2671, 371}
public void test0764() {
	check(//
"Integrate[Sin[e + f*x]^2*(b*Tan[e + f*x])^n, x]", //
 "(Hypergeometric2F1[2, (3 + n)/2, (5 + n)/2, -Tan[e + f*x]^2]*(b*Tan[e + f*x])^(3 + n))/(b^3*f*(3 + n))", //
2671, 371);}

// {2671, 30}
public void test0765() {
	check(//
"Integrate[Csc[e + f*x]^2*(b*Tan[e + f*x])^n, x]", //
 "-((b*(b*Tan[e + f*x])^(-1 + n))/(f*(1 - n)))", //
2671, 30);}

// {2682, 2657}
public void test0766() {
	check(//
"Integrate[Sin[e + f*x]^3*(b*Tan[e + f*x])^n, x]", //
 "((Cos[e + f*x]^2)^((1 + n)/2)*Hypergeometric2F1[(1 + n)/2, (4 + n)/2, (6 + n)/2, Sin[e + f*x]^2]*Sin[e + f*x]^3*(b*Tan[e + f*x])^(1 + n))/(b*f*(4 + n))", //
2682, 2657);}

// {2682, 2657}
public void test0767() {
	check(//
"Integrate[Sin[e + f*x]*(b*Tan[e + f*x])^n, x]", //
 "((Cos[e + f*x]^2)^((1 + n)/2)*Hypergeometric2F1[(1 + n)/2, (2 + n)/2, (4 + n)/2, Sin[e + f*x]^2]*Sin[e + f*x]*(b*Tan[e + f*x])^(1 + n))/(b*f*(2 + n))", //
2682, 2657);}

// {2681, 2656}
public void test0768() {
	check(//
"Integrate[Csc[e + f*x]*(b*Tan[e + f*x])^n, x]", //
 "-((Cos[e + f*x]*Hypergeometric2F1[(1 - n)/2, (2 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Tan[e + f*x])^n)/(f*(1 - n)*(Sin[e + f*x]^2)^(n/2)))", //
2681, 2656);}

// {2681, 2656}
public void test0769() {
	check(//
"Integrate[Csc[e + f*x]^3*(b*Tan[e + f*x])^n, x]", //
 "-((Cos[e + f*x]*Hypergeometric2F1[(1 - n)/2, (4 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Tan[e + f*x])^n)/(f*(1 - n)*(Sin[e + f*x]^2)^(n/2)))", //
2681, 2656);}

// {2681, 2656}
public void test0770() {
	check(//
"Integrate[Csc[e + f*x]^5*(b*Tan[e + f*x])^n, x]", //
 "-((Cos[e + f*x]*Hypergeometric2F1[(1 - n)/2, (6 - n)/2, (3 - n)/2, Cos[e + f*x]^2]*(b*Tan[e + f*x])^n)/(f*(1 - n)*(Sin[e + f*x]^2)^(n/2)))", //
2681, 2656);}

// {2682, 2657}
public void test0771() {
	check(//
"Integrate[(a*Sin[e + f*x])^(3/2)*(b*Tan[e + f*x])^n, x]", //
 "(2*(Cos[e + f*x]^2)^((1 + n)/2)*Hypergeometric2F1[(1 + n)/2, (5 + 2*n)/4, (9 + 2*n)/4, Sin[e + f*x]^2]*(a*Sin[e + f*x])^(3/2)*(b*Tan[e + f*x])^(1 + n))/(b*f*(5 + 2*n))", //
2682, 2657);}

// {2682, 2657}
public void test0772() {
	check(//
"Integrate[Sqrt[a*Sin[e + f*x]]*(b*Tan[e + f*x])^n, x]", //
 "(2*(Cos[e + f*x]^2)^((1 + n)/2)*Hypergeometric2F1[(1 + n)/2, (3 + 2*n)/4, (7 + 2*n)/4, Sin[e + f*x]^2]*Sqrt[a*Sin[e + f*x]]*(b*Tan[e + f*x])^(1 + n))/(b*f*(3 + 2*n))", //
2682, 2657);}

// {2682, 2657}
public void test0773() {
	check(//
"Integrate[(b*Tan[e + f*x])^n/Sqrt[a*Sin[e + f*x]], x]", //
 "(2*(Cos[e + f*x]^2)^((1 + n)/2)*Hypergeometric2F1[(1 + n)/2, (1 + 2*n)/4, (5 + 2*n)/4, Sin[e + f*x]^2]*(b*Tan[e + f*x])^(1 + n))/(b*f*(1 + 2*n)*Sqrt[a*Sin[e + f*x]])", //
2682, 2657);}

// {2682, 2657}
public void test0774() {
	check(//
"Integrate[(b*Tan[e + f*x])^n/(a*Sin[e + f*x])^(3/2), x]", //
 "(-2*(Cos[e + f*x]^2)^((1 + n)/2)*Hypergeometric2F1[(1 + n)/2, (-1 + 2*n)/4, (3 + 2*n)/4, Sin[e + f*x]^2]*(b*Tan[e + f*x])^(1 + n))/(b*f*(1 - 2*n)*(a*Sin[e + f*x])^(3/2))", //
2682, 2657);}

// {2683, 2697}
public void test0775() {
	check(//
"Integrate[(a*Cos[e + f*x])^m*(b*Tan[e + f*x])^n, x]", //
 "((a*Cos[e + f*x])^m*(Cos[e + f*x]^2)^((1 - m + n)/2)*Hypergeometric2F1[(1 + n)/2, (1 - m + n)/2, (3 + n)/2, Sin[e + f*x]^2]*(b*Tan[e + f*x])^(1 + n))/(b*f*(1 + n))", //
2683, 2697);}

// {2687, 32}
public void test0776() {
	check(//
"Integrate[Sec[e + f*x]^2*Sqrt[d*Tan[e + f*x]], x]", //
 "(2*(d*Tan[e + f*x])^(3/2))/(3*d*f)", //
2687, 32);}

// {2687, 32}
public void test0777() {
	check(//
"Integrate[Sec[a + b*x]^2*(d*Tan[a + b*x])^(3/2), x]", //
 "(2*(d*Tan[a + b*x])^(5/2))/(5*b*d)", //
2687, 32);}

// {2687, 32}
public void test0778() {
	check(//
"Integrate[Sec[e + f*x]^2*(d*Tan[e + f*x])^(5/2), x]", //
 "(2*(d*Tan[e + f*x])^(7/2))/(7*d*f)", //
2687, 32);}

// {2687, 32}
public void test0779() {
	check(//
"Integrate[Sec[a + b*x]^2/(d*Tan[a + b*x])^(3/2), x]", //
 "-2/(b*d*Sqrt[d*Tan[a + b*x]])", //
2687, 32);}

// {2712, 2656}
public void test0780() {
	check(//
"Integrate[Sec[e + f*x]^(10/3)*Sin[e + f*x]^2, x]", //
 "(3*Hypergeometric2F1[-7/6, -1/2, -1/6, Cos[e + f*x]^2]*Sec[e + f*x]^(7/3)*Sin[e + f*x])/(7*f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2712, 2656}
public void test0781() {
	check(//
"Integrate[Sec[e + f*x]^(8/3)*Sin[e + f*x]^2, x]", //
 "(3*Hypergeometric2F1[-5/6, -1/2, 1/6, Cos[e + f*x]^2]*Sec[e + f*x]^(5/3)*Sin[e + f*x])/(5*f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2712, 2656}
public void test0782() {
	check(//
"Integrate[Sec[e + f*x]^(7/3)*Sin[e + f*x]^2, x]", //
 "(3*Hypergeometric2F1[-2/3, -1/2, 1/3, Cos[e + f*x]^2]*Sec[e + f*x]^(4/3)*Sin[e + f*x])/(4*f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2712, 2656}
public void test0783() {
	check(//
"Integrate[Sec[e + f*x]^(5/3)*Sin[e + f*x]^2, x]", //
 "(3*Hypergeometric2F1[-1/2, -1/3, 2/3, Cos[e + f*x]^2]*Sec[e + f*x]^(2/3)*Sin[e + f*x])/(2*f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2712, 2656}
public void test0784() {
	check(//
"Integrate[Sec[e + f*x]^(4/3)*Sin[e + f*x]^2, x]", //
 "(3*Hypergeometric2F1[-1/2, -1/6, 5/6, Cos[e + f*x]^2]*Sec[e + f*x]^(1/3)*Sin[e + f*x])/(f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2712, 2656}
public void test0785() {
	check(//
"Integrate[Sec[e + f*x]^(16/3)*Sin[e + f*x]^4, x]", //
 "(3*Hypergeometric2F1[-13/6, -3/2, -7/6, Cos[e + f*x]^2]*Sec[e + f*x]^(13/3)*Sin[e + f*x])/(13*f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2712, 2656}
public void test0786() {
	check(//
"Integrate[Sec[e + f*x]^(14/3)*Sin[e + f*x]^4, x]", //
 "(3*Hypergeometric2F1[-11/6, -3/2, -5/6, Cos[e + f*x]^2]*Sec[e + f*x]^(11/3)*Sin[e + f*x])/(11*f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2712, 2656}
public void test0787() {
	check(//
"Integrate[Sec[e + f*x]^(13/3)*Sin[e + f*x]^4, x]", //
 "(3*Hypergeometric2F1[-5/3, -3/2, -2/3, Cos[e + f*x]^2]*Sec[e + f*x]^(10/3)*Sin[e + f*x])/(10*f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2712, 2656}
public void test0788() {
	check(//
"Integrate[Sec[e + f*x]^(11/3)*Sin[e + f*x]^4, x]", //
 "(3*Hypergeometric2F1[-3/2, -4/3, -1/3, Cos[e + f*x]^2]*Sec[e + f*x]^(8/3)*Sin[e + f*x])/(8*f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2712, 2656}
public void test0789() {
	check(//
"Integrate[Sec[e + f*x]^(10/3)*Sin[e + f*x]^4, x]", //
 "(3*Hypergeometric2F1[-3/2, -7/6, -1/6, Cos[e + f*x]^2]*Sec[e + f*x]^(7/3)*Sin[e + f*x])/(7*f*Sqrt[Sin[e + f*x]^2])", //
2712, 2656);}

// {2692, 2685}
public void test0790() {
	check(//
"Integrate[Sqrt[b*Tan[e + f*x]]/(d*Sec[e + f*x])^(7/2), x]", //
 "(2*(b*Tan[e + f*x])^(3/2))/(7*b*f*(d*Sec[e + f*x])^(7/2)) + (8*(b*Tan[e + f*x])^(3/2))/(21*b*d^2*f*(d*Sec[e + f*x])^(3/2))", //
2692, 2685);}

// {2692, 2685}
public void test0791() {
	check(//
"Integrate[1/((d*Sec[e + f*x])^(5/2)*Sqrt[b*Tan[e + f*x]]), x]", //
 "(2*Sqrt[b*Tan[e + f*x]])/(5*b*f*(d*Sec[e + f*x])^(5/2)) + (8*Sqrt[b*Tan[e + f*x]])/(5*b*d^2*f*Sqrt[d*Sec[e + f*x]])", //
2692, 2685);}

// {2689, 2685}
public void test0792() {
	check(//
"Integrate[1/(Sqrt[d*Sec[e + f*x]]*(b*Tan[e + f*x])^(5/2)), x]", //
 "-2/(3*b*f*Sqrt[d*Sec[e + f*x]]*(b*Tan[e + f*x])^(3/2)) - (8*Sqrt[b*Tan[e + f*x]])/(3*b^3*f*Sqrt[d*Sec[e + f*x]])", //
2689, 2685);}

// {2686, 32}
public void test0793() {
	check(//
"Integrate[(b*Sec[e + f*x])^m*Tan[e + f*x], x]", //
 "(b*Sec[e + f*x])^m/(f*m)", //
2686, 32);}

// {2686, 371}
public void test0794() {
	check(//
"Integrate[Cot[e + f*x]*(b*Sec[e + f*x])^m, x]", //
 "-((Hypergeometric2F1[1, m/2, (2 + m)/2, Sec[e + f*x]^2]*(b*Sec[e + f*x])^m)/(f*m))", //
2686, 371);}

// {2686, 371}
public void test0795() {
	check(//
"Integrate[Cot[e + f*x]^3*(b*Sec[e + f*x])^m, x]", //
 "(Hypergeometric2F1[2, m/2, (2 + m)/2, Sec[e + f*x]^2]*(b*Sec[e + f*x])^m)/(f*m)", //
2686, 371);}

// {2686, 371}
public void test0796() {
	check(//
"Integrate[Cot[e + f*x]^5*(b*Sec[e + f*x])^m, x]", //
 "-((Hypergeometric2F1[3, m/2, (2 + m)/2, Sec[e + f*x]^2]*(b*Sec[e + f*x])^m)/(f*m))", //
2686, 371);}

// {2687, 32}
public void test0797() {
	check(//
"Integrate[Sec[a + b*x]^2*(d*Tan[a + b*x])^n, x]", //
 "(d*Tan[a + b*x])^(1 + n)/(b*d*(1 + n))", //
2687, 32);}

// {3557, 371}
public void test0798() {
	check(//
"Integrate[(d*Tan[a + b*x])^n, x]", //
 "(Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, -Tan[a + b*x]^2]*(d*Tan[a + b*x])^(1 + n))/(b*d*(1 + n))", //
3557, 371);}

// {2687, 371}
public void test0799() {
	check(//
"Integrate[Cos[a + b*x]^2*(d*Tan[a + b*x])^n, x]", //
 "(Hypergeometric2F1[2, (1 + n)/2, (3 + n)/2, -Tan[a + b*x]^2]*(d*Tan[a + b*x])^(1 + n))/(b*d*(1 + n))", //
2687, 371);}

// {2687, 371}
public void test0800() {
	check(//
"Integrate[Cos[a + b*x]^4*(d*Tan[a + b*x])^n, x]", //
 "(Hypergeometric2F1[3, (1 + n)/2, (3 + n)/2, -Tan[a + b*x]^2]*(d*Tan[a + b*x])^(1 + n))/(b*d*(1 + n))", //
2687, 371);}

// {2686, 371}
public void test0801() {
	check(//
"Integrate[(b*Csc[e + f*x])^m*Tan[e + f*x]^3, x]", //
 "-(((b*Csc[e + f*x])^m*Hypergeometric2F1[2, m/2, (2 + m)/2, Csc[e + f*x]^2])/(f*m))", //
2686, 371);}

// {2686, 371}
public void test0802() {
	check(//
"Integrate[(b*Csc[e + f*x])^m*Tan[e + f*x], x]", //
 "((b*Csc[e + f*x])^m*Hypergeometric2F1[1, m/2, (2 + m)/2, Csc[e + f*x]^2])/(f*m)", //
2686, 371);}

// {2686, 32}
public void test0803() {
	check(//
"Integrate[Cot[e + f*x]*(b*Csc[e + f*x])^m, x]", //
 "-((b*Csc[e + f*x])^m/(f*m))", //
2686, 32);}

// {3808, 2212}
public void test0804() {
	check(//
"Integrate[(c + d*x)^m/(a + I*a*Tan[e + f*x]), x]", //
 "(c + d*x)^(1 + m)/(2*a*d*(1 + m)) + (I*2^(-2 - m)*(c + d*x)^m*Gamma[1 + m, ((2*I)*f*(c + d*x))/d])/(a*E^((2*I)*(e - (c*f)/d))*f*((I*f*(c + d*x))/d)^m)", //
3808, 2212);}

// {3556}
public void test0812() {
	check(//
"Integrate[a + I*a*Tan[c + d*x], x]", //
 "a*x - (I*a*Log[Cos[c + d*x]])/d", //
3556);}

// {3567, 3855}
public void test0813() {
	check(//
"Integrate[Sec[c + d*x]*(a + I*a*Tan[c + d*x]), x]", //
 "(a*ArcTanh[Sin[c + d*x]])/d + (I*a*Sec[c + d*x])/d", //
3567, 3855);}

// {3567, 2717}
public void test0814() {
	check(//
"Integrate[Cos[c + d*x]*(a + I*a*Tan[c + d*x]), x]", //
 "((-I)*a*Cos[c + d*x])/d + (a*Sin[c + d*x])/d", //
3567, 2717);}

// {3568, 32}
public void test0815() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + I*a*Tan[c + d*x])^2, x]", //
 "((-I/3)*(a + I*a*Tan[c + d*x])^3)/(a*d)", //
3568, 32);}

// {3558, 3556}
public void test0816() {
	check(//
"Integrate[(a + I*a*Tan[c + d*x])^2, x]", //
 "2*a^2*x - ((2*I)*a^2*Log[Cos[c + d*x]])/d - (a^2*Tan[c + d*x])/d", //
3558, 3556);}

// {3568, 32}
public void test0817() {
	check(//
"Integrate[Cos[c + d*x]^2*(a + I*a*Tan[c + d*x])^2, x]", //
 "((-I)*a^3)/(d*(a - I*a*Tan[c + d*x]))", //
3568, 32);}

// {3577, 3855}
public void test0818() {
	check(//
"Integrate[Cos[c + d*x]*(a + I*a*Tan[c + d*x])^2, x]", //
 "-((a^2*ArcTanh[Sin[c + d*x]])/d) - ((2*I)*Cos[c + d*x]*(a^2 + I*a^2*Tan[c + d*x]))/d", //
3577, 3855);}

// {3577, 2717}
public void test0819() {
	check(//
"Integrate[Cos[c + d*x]^3*(a + I*a*Tan[c + d*x])^2, x]", //
 "(a^2*Sin[c + d*x])/(3*d) - (((2*I)/3)*Cos[c + d*x]^3*(a^2 + I*a^2*Tan[c + d*x]))/d", //
3577, 2717);}

// {3568, 32}
public void test0820() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + I*a*Tan[c + d*x])^3, x]", //
 "((-I/4)*(a + I*a*Tan[c + d*x])^4)/(a*d)", //
3568, 32);}

// {3568, 32}
public void test0821() {
	check(//
"Integrate[Cos[c + d*x]^4*(a + I*a*Tan[c + d*x])^3, x]", //
 "((-I/2)*a^5)/(d*(a - I*a*Tan[c + d*x])^2)", //
3568, 32);}

// {3578, 3569}
public void test0822() {
	check(//
"Integrate[Cos[c + d*x]^5*(a + I*a*Tan[c + d*x])^4, x]", //
 "((-I/15)*a*Cos[c + d*x]^3*(a + I*a*Tan[c + d*x])^3)/d - ((I/5)*Cos[c + d*x]^5*(a + I*a*Tan[c + d*x])^4)/d", //
3578, 3569);}

// {3568, 32}
public void test0823() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + I*a*Tan[c + d*x])^5, x]", //
 "((-I/6)*(a + I*a*Tan[c + d*x])^6)/(a*d)", //
3568, 32);}

// {3568, 32}
public void test0824() {
	check(//
"Integrate[Cos[c + d*x]^8*(a + I*a*Tan[c + d*x])^5, x]", //
 "((-I/4)*a^9)/(d*(a - I*a*Tan[c + d*x])^4)", //
3568, 32);}

// {3568, 32}
public void test0825() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + I*a*Tan[c + d*x])^8, x]", //
 "((-I/9)*(a + I*a*Tan[c + d*x])^9)/(a*d)", //
3568, 32);}

// {3568, 37}
public void test0826() {
	check(//
"Integrate[Cos[c + d*x]^8*(a + I*a*Tan[c + d*x])^8, x]", //
 "((-I/8)*(a^3 + I*a^3*Tan[c + d*x])^4)/(d*(a - I*a*Tan[c + d*x])^4)", //
3568, 37);}

// {3568, 32}
public void test0827() {
	check(//
"Integrate[Cos[c + d*x]^14*(a + I*a*Tan[c + d*x])^8, x]", //
 "((-I/7)*a^15)/(d*(a - I*a*Tan[c + d*x])^7)", //
3568, 32);}

// {3578, 3569}
public void test0828() {
	check(//
"Integrate[Cos[c + d*x]^9*(a + I*a*Tan[c + d*x])^8, x]", //
 "((-I/63)*a*Cos[c + d*x]^7*(a + I*a*Tan[c + d*x])^7)/d - ((I/9)*Cos[c + d*x]^9*(a + I*a*Tan[c + d*x])^8)/d", //
3578, 3569);}

// {3568}
public void test0829() {
	check(//
"Integrate[Sec[c + d*x]^4/(a + I*a*Tan[c + d*x]), x]", //
 "Tan[c + d*x]/(a*d) - ((I/2)*Tan[c + d*x]^2)/(a*d)", //
3568);}

// {3568, 31}
public void test0830() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + I*a*Tan[c + d*x]), x]", //
 "x/a + (I*Log[Cos[c + d*x]])/(a*d)", //
3568, 31);}

// {3560, 8}
public void test0831() {
	check(//
"Integrate[(a + I*a*Tan[c + d*x])^(-1), x]", //
 "x/(2*a) + (I/2)/(d*(a + I*a*Tan[c + d*x]))", //
3560, 8);}

// {3582, 3855}
public void test0832() {
	check(//
"Integrate[Sec[c + d*x]^3/(a + I*a*Tan[c + d*x]), x]", //
 "ArcTanh[Sin[c + d*x]]/(a*d) - (I*Sec[c + d*x])/(a*d)", //
3582, 3855);}

// {3583, 2717}
public void test0833() {
	check(//
"Integrate[Cos[c + d*x]/(a + I*a*Tan[c + d*x]), x]", //
 "(2*Sin[c + d*x])/(3*a*d) + ((I/3)*Cos[c + d*x])/(d*(a + I*a*Tan[c + d*x]))", //
3583, 2717);}

// {3568, 32}
public void test0834() {
	check(//
"Integrate[Sec[c + d*x]^6/(a + I*a*Tan[c + d*x])^2, x]", //
 "((I/3)*(a - I*a*Tan[c + d*x])^3)/(a^5*d)", //
3568, 32);}

// {3568, 32}
public void test0835() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + I*a*Tan[c + d*x])^2, x]", //
 "I/(d*(a^2 + I*a^2*Tan[c + d*x]))", //
3568, 32);}

// {3581, 3855}
public void test0836() {
	check(//
"Integrate[Sec[c + d*x]^3/(a + I*a*Tan[c + d*x])^2, x]", //
 "-(ArcTanh[Sin[c + d*x]]/(a^2*d)) + ((2*I)*Sec[c + d*x])/(d*(a^2 + I*a^2*Tan[c + d*x]))", //
3581, 3855);}

// {3583, 3569}
public void test0837() {
	check(//
"Integrate[Sec[c + d*x]/(a + I*a*Tan[c + d*x])^2, x]", //
 "((I/3)*Sec[c + d*x])/(d*(a + I*a*Tan[c + d*x])^2) + ((I/3)*Sec[c + d*x])/(d*(a^2 + I*a^2*Tan[c + d*x]))", //
3583, 3569);}

// {3568, 32}
public void test0838() {
	check(//
"Integrate[Sec[c + d*x]^8/(a + I*a*Tan[c + d*x])^3, x]", //
 "((I/4)*(a - I*a*Tan[c + d*x])^4)/(a^7*d)", //
3568, 32);}

// {3568, 32}
public void test0839() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + I*a*Tan[c + d*x])^3, x]", //
 "(I/2)/(a*d*(a + I*a*Tan[c + d*x])^2)", //
3568, 32);}

// {3568, 32}
public void test0840() {
	check(//
"Integrate[Sec[c + d*x]^10/(a + I*a*Tan[c + d*x])^4, x]", //
 "((I/5)*(a - I*a*Tan[c + d*x])^5)/(a^9*d)", //
3568, 32);}

// {3568, 34}
public void test0841() {
	check(//
"Integrate[Sec[c + d*x]^4/(a + I*a*Tan[c + d*x])^4, x]", //
 "Tan[c + d*x]/(d*(a^2 + I*a^2*Tan[c + d*x])^2)", //
3568, 34);}

// {3568, 32}
public void test0842() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + I*a*Tan[c + d*x])^4, x]", //
 "(I/3)/(a*d*(a + I*a*Tan[c + d*x])^3)", //
3568, 32);}

// {3583, 3569}
public void test0843() {
	check(//
"Integrate[Sec[c + d*x]^3/(a + I*a*Tan[c + d*x])^4, x]", //
 "((I/5)*Sec[c + d*x]^3)/(d*(a + I*a*Tan[c + d*x])^4) + ((I/15)*Sec[c + d*x]^3)/(a*d*(a + I*a*Tan[c + d*x])^3)", //
3583, 3569);}

// {3568, 37}
public void test0844() {
	check(//
"Integrate[Sec[c + d*x]^8/(a + I*a*Tan[c + d*x])^8, x]", //
 "((I/8)*(a - I*a*Tan[c + d*x])^4)/(d*(a^3 + I*a^3*Tan[c + d*x])^4)", //
3568, 37);}

// {3568, 32}
public void test0845() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + I*a*Tan[c + d*x])^8, x]", //
 "(I/7)/(a*d*(a + I*a*Tan[c + d*x])^7)", //
3568, 32);}

// {3583, 3569}
public void test0846() {
	check(//
"Integrate[Sec[c + d*x]^7/(a + I*a*Tan[c + d*x])^8, x]", //
 "((I/9)*Sec[c + d*x]^7)/(d*(a + I*a*Tan[c + d*x])^8) + ((I/63)*Sec[c + d*x]^7)/(a*d*(a + I*a*Tan[c + d*x])^7)", //
3583, 3569);}

// {3568, 32}
public void test0847() {
	check(//
"Integrate[Sec[c + d*x]^2*Sqrt[a + I*a*Tan[c + d*x]], x]", //
 "(((-2*I)/3)*(a + I*a*Tan[c + d*x])^(3/2))/(a*d)", //
3568, 32);}

// {3575, 3574}
public void test0848() {
	check(//
"Integrate[Sec[c + d*x]^3*Sqrt[a + I*a*Tan[c + d*x]], x]", //
 "(((8*I)/15)*a^2*Sec[c + d*x]^3)/(d*(a + I*a*Tan[c + d*x])^(3/2)) + (((2*I)/5)*a*Sec[c + d*x]^3)/(d*Sqrt[a + I*a*Tan[c + d*x]])", //
3575, 3574);}

// {3568, 32}
public void test0849() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + I*a*Tan[c + d*x])^(3/2), x]", //
 "(((-2*I)/5)*(a + I*a*Tan[c + d*x])^(5/2))/(a*d)", //
3568, 32);}

// {3575, 3574}
public void test0850() {
	check(//
"Integrate[Sec[c + d*x]*(a + I*a*Tan[c + d*x])^(3/2), x]", //
 "(((8*I)/3)*a^2*Sec[c + d*x])/(d*Sqrt[a + I*a*Tan[c + d*x]]) + (((2*I)/3)*a*Sec[c + d*x]*Sqrt[a + I*a*Tan[c + d*x]])/d", //
3575, 3574);}

// {3568, 32}
public void test0851() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + I*a*Tan[c + d*x])^(5/2), x]", //
 "(((-2*I)/7)*(a + I*a*Tan[c + d*x])^(7/2))/(a*d)", //
3568, 32);}

// {3575, 3574}
public void test0852() {
	check(//
"Integrate[Cos[c + d*x]*(a + I*a*Tan[c + d*x])^(5/2), x]", //
 "((-8*I)*a^2*Cos[c + d*x]*Sqrt[a + I*a*Tan[c + d*x]])/d + ((2*I)*a*Cos[c + d*x]*(a + I*a*Tan[c + d*x])^(3/2))/d", //
3575, 3574);}

// {3568, 32}
public void test0853() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + I*a*Tan[c + d*x])^(7/2), x]", //
 "(((-2*I)/9)*(a + I*a*Tan[c + d*x])^(9/2))/(a*d)", //
3568, 32);}

// {3575, 3574}
public void test0854() {
	check(//
"Integrate[Cos[c + d*x]^3*(a + I*a*Tan[c + d*x])^(7/2), x]", //
 "(((8*I)/3)*a^2*Cos[c + d*x]^3*(a + I*a*Tan[c + d*x])^(3/2))/d - ((2*I)*a*Cos[c + d*x]^3*(a + I*a*Tan[c + d*x])^(5/2))/d", //
3575, 3574);}

// {3568, 32}
public void test0855() {
	check(//
"Integrate[Sec[c + d*x]^2/Sqrt[a + I*a*Tan[c + d*x]], x]", //
 "((-2*I)*Sqrt[a + I*a*Tan[c + d*x]])/(a*d)", //
3568, 32);}

// {3575, 3574}
public void test0856() {
	check(//
"Integrate[Sec[c + d*x]^5/Sqrt[a + I*a*Tan[c + d*x]], x]", //
 "(((8*I)/35)*a^2*Sec[c + d*x]^5)/(d*(a + I*a*Tan[c + d*x])^(5/2)) + (((2*I)/7)*a*Sec[c + d*x]^5)/(d*(a + I*a*Tan[c + d*x])^(3/2))", //
3575, 3574);}

// {3570, 212}
public void test0857() {
	check(//
"Integrate[Sec[c + d*x]/Sqrt[a + I*a*Tan[c + d*x]], x]", //
 "(I*Sqrt[2]*ArcTanh[(Sqrt[a]*Sec[c + d*x])/(Sqrt[2]*Sqrt[a + I*a*Tan[c + d*x]])])/(Sqrt[a]*d)", //
3570, 212);}

// {3568, 32}
public void test0858() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + I*a*Tan[c + d*x])^(3/2), x]", //
 "(2*I)/(a*d*Sqrt[a + I*a*Tan[c + d*x]])", //
3568, 32);}

// {3575, 3574}
public void test0859() {
	check(//
"Integrate[Sec[c + d*x]^7/(a + I*a*Tan[c + d*x])^(3/2), x]", //
 "(((8*I)/63)*a^2*Sec[c + d*x]^7)/(d*(a + I*a*Tan[c + d*x])^(7/2)) + (((2*I)/9)*a*Sec[c + d*x]^7)/(d*(a + I*a*Tan[c + d*x])^(5/2))", //
3575, 3574);}

// {3568, 32}
public void test0860() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + I*a*Tan[c + d*x])^(5/2), x]", //
 "((2*I)/3)/(a*d*(a + I*a*Tan[c + d*x])^(3/2))", //
3568, 32);}

// {3575, 3574}
public void test0861() {
	check(//
"Integrate[Sec[c + d*x]^9/(a + I*a*Tan[c + d*x])^(5/2), x]", //
 "(((8*I)/99)*a^2*Sec[c + d*x]^9)/(d*(a + I*a*Tan[c + d*x])^(9/2)) + (((2*I)/11)*a*Sec[c + d*x]^9)/(d*(a + I*a*Tan[c + d*x])^(7/2))", //
3575, 3574);}

// {3568, 32}
public void test0862() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + I*a*Tan[c + d*x])^(7/2), x]", //
 "((2*I)/5)/(a*d*(a + I*a*Tan[c + d*x])^(5/2))", //
3568, 32);}

// {3575, 3574}
public void test0863() {
	check(//
"Integrate[Sec[c + d*x]^11/(a + I*a*Tan[c + d*x])^(7/2), x]", //
 "(((8*I)/143)*a^2*Sec[c + d*x]^11)/(d*(a + I*a*Tan[c + d*x])^(11/2)) + (((2*I)/13)*a*Sec[c + d*x]^11)/(d*(a + I*a*Tan[c + d*x])^(9/2))", //
3575, 3574);}

// {3578, 3569}
public void test0864() {
	check(//
"Integrate[Sqrt[a + I*a*Tan[c + d*x]]/(e*Sec[c + d*x])^(3/2), x]", //
 "(((4*I)/3)*a*Sqrt[e*Sec[c + d*x]])/(d*e^2*Sqrt[a + I*a*Tan[c + d*x]]) - (((2*I)/3)*Sqrt[a + I*a*Tan[c + d*x]])/(d*(e*Sec[c + d*x])^(3/2))", //
3578, 3569);}

// {3578, 3569}
public void test0865() {
	check(//
"Integrate[(a + I*a*Tan[c + d*x])^(3/2)/(e*Sec[c + d*x])^(5/2), x]", //
 "(((-4*I)/5)*a*Sqrt[a + I*a*Tan[c + d*x]])/(d*e^2*Sqrt[e*Sec[c + d*x]]) - (((2*I)/5)*(a + I*a*Tan[c + d*x])^(3/2))/(d*(e*Sec[c + d*x])^(5/2))", //
3578, 3569);}

// {3578, 3569}
public void test0866() {
	check(//
"Integrate[(a + I*a*Tan[c + d*x])^(5/2)/(e*Sec[c + d*x])^(7/2), x]", //
 "(((-4*I)/21)*a*(a + I*a*Tan[c + d*x])^(3/2))/(d*e^2*(e*Sec[c + d*x])^(3/2)) - (((2*I)/7)*(a + I*a*Tan[c + d*x])^(5/2))/(d*(e*Sec[c + d*x])^(7/2))", //
3578, 3569);}

// {3583, 3569}
public void test0867() {
	check(//
"Integrate[1/(Sqrt[e*Sec[c + d*x]]*Sqrt[a + I*a*Tan[c + d*x]]), x]", //
 "((2*I)/3)/(d*Sqrt[e*Sec[c + d*x]]*Sqrt[a + I*a*Tan[c + d*x]]) - (((4*I)/3)*Sqrt[a + I*a*Tan[c + d*x]])/(a*d*Sqrt[e*Sec[c + d*x]])", //
3583, 3569);}

// {3583, 3569}
public void test0868() {
	check(//
"Integrate[Sqrt[e*Sec[c + d*x]]/(a + I*a*Tan[c + d*x])^(3/2), x]", //
 "(((2*I)/5)*Sqrt[e*Sec[c + d*x]])/(d*(a + I*a*Tan[c + d*x])^(3/2)) + (((4*I)/5)*Sqrt[e*Sec[c + d*x]])/(a*d*Sqrt[a + I*a*Tan[c + d*x]])", //
3583, 3569);}

// {3583, 3569}
public void test0869() {
	check(//
"Integrate[(e*Sec[c + d*x])^(3/2)/(a + I*a*Tan[c + d*x])^(5/2), x]", //
 "(((2*I)/7)*(e*Sec[c + d*x])^(3/2))/(d*(a + I*a*Tan[c + d*x])^(5/2)) + (((4*I)/21)*(e*Sec[c + d*x])^(3/2))/(a*d*(a + I*a*Tan[c + d*x])^(3/2))", //
3583, 3569);}

// {3575, 3574}
public void test0870() {
	check(//
"Integrate[(d*Sec[e + f*x])^(2/3)*(a + I*a*Tan[e + f*x])^(5/3), x]", //
 "(((9*I)/2)*a^2*(d*Sec[e + f*x])^(2/3))/(f*(a + I*a*Tan[e + f*x])^(1/3)) + (((3*I)/4)*a*(d*Sec[e + f*x])^(2/3)*(a + I*a*Tan[e + f*x])^(2/3))/f", //
3575, 3574);}

// {3568, 32}
public void test0871() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + I*a*Tan[c + d*x])^n, x]", //
 "((-I)*(a + I*a*Tan[c + d*x])^(1 + n))/(a*d*(1 + n))", //
3568, 32);}

// {3568, 70}
public void test0872() {
	check(//
"Integrate[Cos[c + d*x]^2*(a + I*a*Tan[c + d*x])^n, x]", //
 "((I/4)*a*Hypergeometric2F1[2, -1 + n, n, (1 + I*Tan[c + d*x])/2]*(a + I*a*Tan[c + d*x])^(-1 + n))/(d*(1 - n))", //
3568, 70);}

// {3568, 70}
public void test0873() {
	check(//
"Integrate[Cos[c + d*x]^4*(a + I*a*Tan[c + d*x])^n, x]", //
 "((I/8)*a^2*Hypergeometric2F1[3, -2 + n, -1 + n, (1 + I*Tan[c + d*x])/2]*(a + I*a*Tan[c + d*x])^(-2 + n))/(d*(2 - n))", //
3568, 70);}

// {3568, 70}
public void test0874() {
	check(//
"Integrate[Cos[c + d*x]^6*(a + I*a*Tan[c + d*x])^n, x]", //
 "((I/16)*a^3*Hypergeometric2F1[4, -3 + n, -2 + n, (1 + I*Tan[c + d*x])/2]*(a + I*a*Tan[c + d*x])^(-3 + n))/(d*(3 - n))", //
3568, 70);}

// {3585, 3569}
public void test0875() {
	check(//
"Integrate[(e*Sec[c + d*x])^(-1 - n)*(a + I*a*Tan[c + d*x])^n, x]", //
 "(I*(e*Sec[c + d*x])^(-1 - n)*(a + I*a*Tan[c + d*x])^n)/(d*(1 - n)) - (I*(e*Sec[c + d*x])^(-1 - n)*(a + I*a*Tan[c + d*x])^(1 + n))/(a*d*(1 - n^2))", //
3585, 3569);}

// {3575, 3574}
public void test0876() {
	check(//
"Integrate[(e*Sec[c + d*x])^(4 - 2*n)*(a + I*a*Tan[c + d*x])^n, x]", //
 "((2*I)*a^2*(e*Sec[c + d*x])^(4 - 2*n)*(a + I*a*Tan[c + d*x])^(-2 + n))/(d*(6 - 5*n + n^2)) + (I*a*(e*Sec[c + d*x])^(4 - 2*n)*(a + I*a*Tan[c + d*x])^(-1 + n))/(d*(3 - n))", //
3575, 3574);}

// {3575, 3574}
public void test0877() {
	check(//
"Integrate[(d*Sec[e + f*x])^(2*n)*(a + I*a*Tan[e + f*x])^(2 - n), x]", //
 "(I*a*(d*Sec[e + f*x])^(2*n)*(a + I*a*Tan[e + f*x])^(1 - n))/(f*(1 + n)) + ((2*I)*a^2*(d*Sec[e + f*x])^(2*n))/(f*n*(1 + n)*(a + I*a*Tan[e + f*x])^n)", //
3575, 3574);}

// {3567, 3855}
public void test0878() {
	check(//
"Integrate[Sec[c + d*x]*(a + b*Tan[c + d*x]), x]", //
 "(a*ArcTanh[Sin[c + d*x]])/d + (b*Sec[c + d*x])/d", //
3567, 3855);}

// {3567, 2717}
public void test0879() {
	check(//
"Integrate[Cos[c + d*x]*(a + b*Tan[c + d*x]), x]", //
 "-((b*Cos[c + d*x])/d) + (a*Sin[c + d*x])/d", //
3567, 2717);}

// {3587, 32}
public void test0880() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + b*Tan[c + d*x])^2, x]", //
 "(a + b*Tan[c + d*x])^3/(3*b*d)", //
3587, 32);}

// {3587, 32}
public void test0881() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + b*Tan[c + d*x])^3, x]", //
 "(a + b*Tan[c + d*x])^4/(4*b*d)", //
3587, 32);}

// {3587, 31}
public void test0882() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + b*Tan[c + d*x]), x]", //
 "Log[a + b*Tan[c + d*x]]/(b*d)", //
3587, 31);}

// {3590, 212}
public void test0883() {
	check(//
"Integrate[Sec[c + d*x]/(a + b*Tan[c + d*x]), x]", //
 "-(ArcTanh[(Cos[c + d*x]*(b - a*Tan[c + d*x]))/Sqrt[a^2 + b^2]]/(Sqrt[a^2 + b^2]*d))", //
3590, 212);}

// {3587, 32}
public void test0884() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + b*Tan[c + d*x])^2, x]", //
 "-(1/(b*d*(a + b*Tan[c + d*x])))", //
3587, 32);}

// {3587, 32}
public void test0885() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + b*Tan[c + d*x])^3, x]", //
 "-1/(2*b*d*(a + b*Tan[c + d*x])^2)", //
3587, 32);}

// {3587, 32}
public void test0886() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + b*Tan[c + d*x])^n, x]", //
 "(a + b*Tan[c + d*x])^(1 + n)/(b*d*(1 + n))", //
3587, 32);}

// {3596, 3569}
public void test0887() {
	check(//
"Integrate[Sqrt[e*Cos[c + d*x]]*Sqrt[a + I*a*Tan[c + d*x]], x]", //
 "((-2*I)*Sqrt[e*Cos[c + d*x]]*Sqrt[a + I*a*Tan[c + d*x]])/d", //
3596, 3569);}

// {3596, 3569}
public void test0888() {
	check(//
"Integrate[1/(Sqrt[e*Cos[c + d*x]]*Sqrt[a + I*a*Tan[c + d*x]]), x]", //
 "(2*I)/(d*Sqrt[e*Cos[c + d*x]]*Sqrt[a + I*a*Tan[c + d*x]])", //
3596, 3569);}

// {3597, 67}
public void test0889() {
	check(//
"Integrate[Csc[c + d*x]^2*(a + b*Tan[c + d*x])^n, x]", //
 "(b*Hypergeometric2F1[2, 1 + n, 2 + n, 1 + (b*Tan[c + d*x])/a]*(a + b*Tan[c + d*x])^(1 + n))/(a^2*d*(1 + n))", //
3597, 67);}

// {3606, 3556}
public void test0890() {
	check(//
"Integrate[Tan[c + d*x]*(a + I*a*Tan[c + d*x]), x]", //
 "(-I)*a*x - (a*Log[Cos[c + d*x]])/d + (I*a*Tan[c + d*x])/d", //
3606, 3556);}

// {3556}
public void test0891() {
	check(//
"Integrate[a + I*a*Tan[c + d*x], x]", //
 "a*x - (I*a*Log[Cos[c + d*x]])/d", //
3556);}

// {3612, 3556}
public void test0892() {
	check(//
"Integrate[Cot[c + d*x]*(a + I*a*Tan[c + d*x]), x]", //
 "I*a*x + (a*Log[Sin[c + d*x]])/d", //
3612, 3556);}

// {3558, 3556}
public void test0893() {
	check(//
"Integrate[(a + I*a*Tan[c + d*x])^2, x]", //
 "2*a^2*x - ((2*I)*a^2*Log[Cos[c + d*x]])/d - (a^2*Tan[c + d*x])/d", //
3558, 3556);}

// {3607, 8}
public void test0894() {
	check(//
"Integrate[Tan[c + d*x]/(a + I*a*Tan[c + d*x]), x]", //
 "((-I/2)*x)/a - 1/(2*d*(a + I*a*Tan[c + d*x]))", //
3607, 8);}

// {3560, 8}
public void test0895() {
	check(//
"Integrate[(a + I*a*Tan[c + d*x])^(-1), x]", //
 "x/(2*a) + (I/2)/(d*(a + I*a*Tan[c + d*x]))", //
3560, 8);}

// {3561, 212}
public void test0896() {
	check(//
"Integrate[Sqrt[a + I*a*Tan[c + d*x]], x]", //
 "((-I)*Sqrt[2]*Sqrt[a]*ArcTanh[Sqrt[a + I*a*Tan[c + d*x]]/(Sqrt[2]*Sqrt[a])])/d", //
3561, 212);}

// {3614, 211}
public void test0897() {
	check(//
"Integrate[(a + I*a*Tan[e + f*x])/Sqrt[d*Tan[e + f*x]], x]", //
 "(-2*(-1)^(1/4)*a*ArcTan[((-1)^(3/4)*Sqrt[d*Tan[e + f*x]])/Sqrt[d]])/(Sqrt[d]*f)", //
3614, 211);}

// {3614, 214}
public void test0898() {
	check(//
"Integrate[(a - I*a*Tan[e + f*x])/Sqrt[d*Tan[e + f*x]], x]", //
 "(-2*(-1)^(1/4)*a*ArcTanh[((-1)^(3/4)*Sqrt[d*Tan[e + f*x]])/Sqrt[d]])/(Sqrt[d]*f)", //
3614, 214);}

// {3625, 211}
public void test0899() {
	check(//
"Integrate[Sqrt[a + I*a*Tan[c + d*x]]/Sqrt[Tan[c + d*x]], x]", //
 "((1 - I)*Sqrt[a]*ArcTanh[((1 + I)*Sqrt[a]*Sqrt[Tan[c + d*x]])/Sqrt[a + I*a*Tan[c + d*x]]])/d", //
3625, 211);}

// {3618, 66}
public void test0900() {
	check(//
"Integrate[(e*Tan[c + d*x])^m*(a + I*a*Tan[c + d*x]), x]", //
 "(a*Hypergeometric2F1[1, 1 + m, 2 + m, I*Tan[c + d*x]]*(e*Tan[c + d*x])^(1 + m))/(d*e*(1 + m))", //
3618, 66);}

// {3618, 66}
public void test0901() {
	check(//
"Integrate[(e*Tan[c + d*x])^m*(a - I*a*Tan[c + d*x]), x]", //
 "(a*Hypergeometric2F1[1, 1 + m, 2 + m, (-I)*Tan[c + d*x]]*(e*Tan[c + d*x])^(1 + m))/(d*e*(1 + m))", //
3618, 66);}

// {3618, 66}
public void test0902() {
	check(//
"Integrate[(d*Tan[e + f*x])^n*(a + I*a*Tan[e + f*x]), x]", //
 "(a*Hypergeometric2F1[1, 1 + n, 2 + n, I*Tan[e + f*x]]*(d*Tan[e + f*x])^(1 + n))/(d*f*(1 + n))", //
3618, 66);}

// {3618, 66}
public void test0903() {
	check(//
"Integrate[(d*Tan[e + f*x])^n*(a - I*a*Tan[e + f*x]), x]", //
 "(a*Hypergeometric2F1[1, 1 + n, 2 + n, (-I)*Tan[e + f*x]]*(d*Tan[e + f*x])^(1 + n))/(d*f*(1 + n))", //
3618, 66);}

// {3562, 70}
public void test0904() {
	check(//
"Integrate[(a + I*a*Tan[c + d*x])^m, x]", //
 "((-I/2)*Hypergeometric2F1[1, m, 1 + m, (1 + I*Tan[c + d*x])/2]*(a + I*a*Tan[c + d*x])^m)/(d*m)", //
3562, 70);}

// {3613, 211}
public void test0905() {
	check(//
"Integrate[(a + a*Tan[e + f*x])/Sqrt[d*Tan[e + f*x]], x]", //
 "-((Sqrt[2]*a*ArcTan[(Sqrt[d]*(1 - Tan[e + f*x]))/(Sqrt[2]*Sqrt[d*Tan[e + f*x]])])/(Sqrt[d]*f))", //
3613, 211);}

// {3606, 3556}
public void test0906() {
	check(//
"Integrate[Tan[c + d*x]*(a + b*Tan[c + d*x]), x]", //
 "-(b*x) - (a*Log[Cos[c + d*x]])/d + (b*Tan[c + d*x])/d", //
3606, 3556);}

// {3556}
public void test0907() {
	check(//
"Integrate[a + b*Tan[c + d*x], x]", //
 "a*x - (b*Log[Cos[c + d*x]])/d", //
3556);}

// {3612, 3556}
public void test0908() {
	check(//
"Integrate[Cot[c + d*x]*(a + b*Tan[c + d*x]), x]", //
 "b*x + (a*Log[Sin[c + d*x]])/d", //
3612, 3556);}

// {3558, 3556}
public void test0909() {
	check(//
"Integrate[(a + b*Tan[c + d*x])^2, x]", //
 "(a^2 - b^2)*x - (2*a*b*Log[Cos[c + d*x]])/d + (b^2*Tan[c + d*x])/d", //
3558, 3556);}

// {3612, 3611}
public void test0910() {
	check(//
"Integrate[Tan[c + d*x]/(a + b*Tan[c + d*x]), x]", //
 "(b*x)/(a^2 + b^2) - (a*Log[a*Cos[c + d*x] + b*Sin[c + d*x]])/((a^2 + b^2)*d)", //
3612, 3611);}

// {3565, 3611}
public void test0911() {
	check(//
"Integrate[(a + b*Tan[c + d*x])^(-1), x]", //
 "(a*x)/(a^2 + b^2) + (b*Log[a*Cos[c + d*x] + b*Sin[c + d*x]])/((a^2 + b^2)*d)", //
3565, 3611);}

// {3565, 3611}
public void test0912() {
	check(//
"Integrate[(3 + 5*Tan[c + d*x])^(-1), x]", //
 "(3*x)/34 + (5*Log[3*Cos[c + d*x] + 5*Sin[c + d*x]])/(34*d)", //
3565, 3611);}

// {3565, 3611}
public void test0913() {
	check(//
"Integrate[(5 + 3*Tan[c + d*x])^(-1), x]", //
 "(5*x)/34 + (3*Log[5*Cos[c + d*x] + 3*Sin[c + d*x]])/(34*d)", //
3565, 3611);}

// {3604, 37}
public void test0914() {
	check(//
"Integrate[Sqrt[c - I*c*Tan[e + f*x]]/Sqrt[a + I*a*Tan[e + f*x]], x]", //
 "(I*Sqrt[c - I*c*Tan[e + f*x]])/(f*Sqrt[a + I*a*Tan[e + f*x]])", //
3604, 37);}

// {3604, 37}
public void test0915() {
	check(//
"Integrate[(c - I*c*Tan[e + f*x])^(3/2)/(a + I*a*Tan[e + f*x])^(3/2), x]", //
 "((I/3)*(c - I*c*Tan[e + f*x])^(3/2))/(f*(a + I*a*Tan[e + f*x])^(3/2))", //
3604, 37);}

// {3604, 37}
public void test0916() {
	check(//
"Integrate[(c - I*c*Tan[e + f*x])^(5/2)/(a + I*a*Tan[e + f*x])^(5/2), x]", //
 "((I/5)*(c - I*c*Tan[e + f*x])^(5/2))/(f*(a + I*a*Tan[e + f*x])^(5/2))", //
3604, 37);}

// {3604, 37}
public void test0917() {
	check(//
"Integrate[Sqrt[a + I*a*Tan[e + f*x]]/Sqrt[c - I*c*Tan[e + f*x]], x]", //
 "((-I)*Sqrt[a + I*a*Tan[e + f*x]])/(f*Sqrt[c - I*c*Tan[e + f*x]])", //
3604, 37);}

// {3604, 39}
public void test0918() {
	check(//
"Integrate[1/(Sqrt[a + I*a*Tan[e + f*x]]*Sqrt[c - I*c*Tan[e + f*x]]), x]", //
 "Tan[e + f*x]/(f*Sqrt[a + I*a*Tan[e + f*x]]*Sqrt[c - I*c*Tan[e + f*x]])", //
3604, 39);}

// {3604, 37}
public void test0919() {
	check(//
"Integrate[(a + I*a*Tan[e + f*x])^(3/2)/(c - I*c*Tan[e + f*x])^(3/2), x]", //
 "((-I/3)*(a + I*a*Tan[e + f*x])^(3/2))/(f*(c - I*c*Tan[e + f*x])^(3/2))", //
3604, 37);}

// {3604, 37}
public void test0920() {
	check(//
"Integrate[(a + I*a*Tan[e + f*x])^(5/2)/(c - I*c*Tan[e + f*x])^(5/2), x]", //
 "((-I/5)*(a + I*a*Tan[e + f*x])^(5/2))/(f*(c - I*c*Tan[e + f*x])^(5/2))", //
3604, 37);}

// {3606, 3556}
public void test0921() {
	check(//
"Integrate[(a + I*a*Tan[e + f*x])*(c + d*Tan[e + f*x]), x]", //
 "a*(c - I*d)*x - (a*(I*c + d)*Log[Cos[e + f*x]])/f + (I*a*d*Tan[e + f*x])/f", //
3606, 3556);}

// {3607, 8}
public void test0922() {
	check(//
"Integrate[(c + d*Tan[e + f*x])/(a + I*a*Tan[e + f*x]), x]", //
 "((c - I*d)*x)/(2*a) + (I*c - d)/(2*f*(a + I*a*Tan[e + f*x]))", //
3607, 8);}

// {3612, 3611}
public void test0923() {
	check(//
"Integrate[(a + I*a*Tan[e + f*x])/(c + d*Tan[e + f*x]), x]", //
 "(a*x)/(c - I*d) + (a*Log[c*Cos[e + f*x] + d*Sin[e + f*x]])/((I*c + d)*f)", //
3612, 3611);}

// {3625, 214}
public void test0924() {
	check(//
"Integrate[Sqrt[a + I*a*Tan[e + f*x]]/Sqrt[c + d*Tan[e + f*x]], x]", //
 "((-I)*Sqrt[2]*Sqrt[a]*ArcTanh[(Sqrt[2]*Sqrt[a]*Sqrt[c + d*Tan[e + f*x]])/(Sqrt[c - I*d]*Sqrt[a + I*a*Tan[e + f*x]])])/(Sqrt[c - I*d]*f)", //
3625, 214);}

// {3618, 70}
public void test0925() {
	check(//
"Integrate[(a + I*a*Tan[e + f*x])*(c + d*Tan[e + f*x])^n, x]", //
 "(a*Hypergeometric2F1[1, 1 + n, 2 + n, (c + d*Tan[e + f*x])/(c - I*d)]*(c + d*Tan[e + f*x])^(1 + n))/((I*c + d)*f*(1 + n))", //
3618, 70);}

// {3606, 3556}
public void test0926() {
	check(//
"Integrate[(a + b*Tan[e + f*x])*(c + d*Tan[e + f*x]), x]", //
 "(a*c - b*d)*x - ((b*c + a*d)*Log[Cos[e + f*x]])/f + (b*d*Tan[e + f*x])/f", //
3606, 3556);}

// {3612, 3611}
public void test0927() {
	check(//
"Integrate[(c + d*Tan[e + f*x])/(a + b*Tan[e + f*x]), x]", //
 "((a*c + b*d)*x)/(a^2 + b^2) + ((b*c - a*d)*Log[a*Cos[e + f*x] + b*Sin[e + f*x]])/((a^2 + b^2)*f)", //
3612, 3611);}

// {3612, 3611}
public void test0928() {
	check(//
"Integrate[(a + b*Tan[e + f*x])/(c + d*Tan[e + f*x]), x]", //
 "((a*c + b*d)*x)/(c^2 + d^2) - ((b*c - a*d)*Log[c*Cos[e + f*x] + d*Sin[e + f*x]])/((c^2 + d^2)*f)", //
3612, 3611);}

// {3606, 3556}
public void test0929() {
	check(//
"Integrate[(a + I*a*Tan[c + d*x])*(A + B*Tan[c + d*x]), x]", //
 "a*(A - I*B)*x - (a*(I*A + B)*Log[Cos[c + d*x]])/d + (I*a*B*Tan[c + d*x])/d", //
3606, 3556);}

// {3607, 8}
public void test0930() {
	check(//
"Integrate[(A + B*Tan[c + d*x])/(a + I*a*Tan[c + d*x]), x]", //
 "((A - I*B)*x)/(2*a) + (I*A - B)/(2*d*(a + I*a*Tan[c + d*x]))", //
3607, 8);}

// {3606, 3556}
public void test0931() {
	check(//
"Integrate[(a + b*Tan[c + d*x])*(A + B*Tan[c + d*x]), x]", //
 "(a*A - b*B)*x - ((A*b + a*B)*Log[Cos[c + d*x]])/d + (b*B*Tan[c + d*x])/d", //
3606, 3556);}

// {3612, 3611}
public void test0932() {
	check(//
"Integrate[(A + B*Tan[c + d*x])/(a + b*Tan[c + d*x]), x]", //
 "((a*A + b*B)*x)/(a^2 + b^2) + ((A*b - a*B)*Log[a*Cos[c + d*x] + b*Sin[c + d*x]])/((a^2 + b^2)*d)", //
3612, 3611);}

// {21, 3556}
public void test0933() {
	check(//
"Integrate[(Tan[c + d*x]*(a*B + b*B*Tan[c + d*x]))/(a + b*Tan[c + d*x]), x]", //
 "-((B*Log[Cos[c + d*x]])/d)", //
21, 3556);}

// {21, 8}
public void test0934() {
	check(//
"Integrate[(a*B + b*B*Tan[c + d*x])/(a + b*Tan[c + d*x]), x]", //
 "B*x", //
21, 8);}

// {21, 3556}
public void test0935() {
	check(//
"Integrate[(Cot[c + d*x]*(a*B + b*B*Tan[c + d*x]))/(a + b*Tan[c + d*x]), x]", //
 "(B*Log[Sin[c + d*x]])/d", //
21, 3556);}

// {3612, 3611}
public void test0936() {
	check(//
"Integrate[(3 + Tan[c + d*x])/(2 - Tan[c + d*x]), x]", //
 "x - Log[2*Cos[c + d*x] - Sin[c + d*x]]/d", //
3612, 3611);}

// {3612, 3611}
public void test0937() {
	check(//
"Integrate[((b*B)/a + B*Tan[c + d*x])/(a + b*Tan[c + d*x]), x]", //
 "(2*b*B*x)/(a^2 + b^2) - ((a - b^2/a)*B*Log[a*Cos[c + d*x] + b*Sin[c + d*x]])/((a^2 + b^2)*d)", //
3612, 3611);}

// {3616, 209}
public void test0938() {
	check(//
"Integrate[(3 + Tan[x])/Sqrt[4 + 3*Tan[x]], x]", //
 "-(Sqrt[2]*ArcTan[(1 - 3*Tan[x])/(Sqrt[2]*Sqrt[4 + 3*Tan[x]])])", //
3616, 209);}

// {3616, 213}
public void test0939() {
	check(//
"Integrate[(1 - 3*Tan[x])/Sqrt[4 + 3*Tan[x]], x]", //
 "Sqrt[2]*ArcTanh[(3 + Tan[x])/(Sqrt[2]*Sqrt[4 + 3*Tan[x]])]", //
3616, 213);}

// {3669}
public void test0940() {
	check(//
"Integrate[(a + I*a*Tan[e + f*x])*(A + B*Tan[e + f*x])*(c - I*c*Tan[e + f*x]), x]", //
 "(a*A*c*Tan[e + f*x])/f + (a*B*c*Tan[e + f*x]^2)/(2*f)", //
3669);}

// {3606, 3556}
public void test0941() {
	check(//
"Integrate[(a + I*a*Tan[e + f*x])*(A + B*Tan[e + f*x]), x]", //
 "a*(A - I*B)*x - (a*(I*A + B)*Log[Cos[e + f*x]])/f + (I*a*B*Tan[e + f*x])/f", //
3606, 3556);}

// {3669, 37}
public void test0942() {
	check(//
"Integrate[((a + I*a*Tan[e + f*x])*(A + B*Tan[e + f*x]))/(c - I*c*Tan[e + f*x])^2, x]", //
 "(a*(A + B*Tan[e + f*x])^2)/(2*(I*A + B)*c^2*f*(1 - I*Tan[e + f*x])^2)", //
3669, 37);}

// {3607, 8}
public void test0943() {
	check(//
"Integrate[(A + B*Tan[e + f*x])/(a + I*a*Tan[e + f*x]), x]", //
 "((A - I*B)*x)/(2*a) + (I*A - B)/(2*f*(a + I*a*Tan[e + f*x]))", //
3607, 8);}

// {3669, 37}
public void test0944() {
	check(//
"Integrate[((A + B*Tan[e + f*x])*(c - I*c*Tan[e + f*x]))/(a + I*a*Tan[e + f*x])^2, x]", //
 "-(c*(A + B*Tan[e + f*x])^2)/(2*a^2*(I*A - B)*f*(1 + I*Tan[e + f*x])^2)", //
3669, 37);}

// {3669, 75}
public void test0945() {
	check(//
"Integrate[((c - I*c*Tan[e + f*x])^n*((-I)*(2 + n) + (-2 + n)*Tan[e + f*x]))/(-I + Tan[e + f*x])^2, x]", //
 "(c - I*c*Tan[e + f*x])^n/(f*(I - Tan[e + f*x])^2)", //
3669, 75);}

// {3739, 3556}
public void test0946() {
	check(//
"Integrate[Sqrt[b*Tan[e + f*x]^2], x]", //
 "-((Cot[e + f*x]*Log[Cos[e + f*x]]*Sqrt[b*Tan[e + f*x]^2])/f)", //
3739, 3556);}

// {3739, 3556}
public void test0947() {
	check(//
"Integrate[1/Sqrt[b*Tan[e + f*x]^2], x]", //
 "(Log[Sin[e + f*x]]*Tan[e + f*x])/(f*Sqrt[b*Tan[e + f*x]^2])", //
3739, 3556);}

// {3740, 3556}
public void test0948() {
	check(//
"Integrate[(b*Tan[e + f*x]^n)^n^(-1), x]", //
 "-((Cot[e + f*x]*Log[Cos[e + f*x]]*(b*Tan[e + f*x]^n)^n^(-1))/f)", //
3740, 3556);}

// {3745, 270}
public void test0949() {
	check(//
"Integrate[Sin[e + f*x]/Sqrt[a + b*Tan[e + f*x]^2], x]", //
 "-((Cos[e + f*x]*Sqrt[a - b + b*Sec[e + f*x]^2])/((a - b)*f))", //
3745, 270);}

// {3744, 270}
public void test0950() {
	check(//
"Integrate[Csc[e + f*x]^2/Sqrt[a + b*Tan[e + f*x]^2], x]", //
 "-((Cot[e + f*x]*Sqrt[a + b*Tan[e + f*x]^2])/(a*f))", //
3744, 270);}

// {3712, 3556}
public void test0951() {
	check(//
"Integrate[Tan[e + f*x]*(a + b*Tan[e + f*x]^2), x]", //
 "-(((a - b)*Log[Cos[e + f*x]])/f) + (b*Tan[e + f*x]^2)/(2*f)", //
3712, 3556);}

// {3710, 8}
public void test0952() {
	check(//
"Integrate[Cot[e + f*x]^2*(a + b*Tan[e + f*x]^2), x]", //
 "-((a - b)*x) - (a*Cot[e + f*x])/f", //
3710, 8);}

// {3757}
public void test0953() {
	check(//
"Integrate[Cos[c + d*x]^3*(a + b*Tan[c + d*x]^2), x]", //
 "(a*Sin[c + d*x])/d - ((a - b)*Sin[c + d*x]^3)/(3*d)", //
3757);}

// {3756}
public void test0954() {
	check(//
"Integrate[Sec[c + d*x]^2*(a + b*Tan[c + d*x]^2), x]", //
 "(a*Tan[c + d*x])/d + (b*Tan[c + d*x]^3)/(3*d)", //
3756);}

// {3757, 214}
public void test0955() {
	check(//
"Integrate[Sec[c + d*x]/(a + b*Tan[c + d*x]^2), x]", //
 "ArcTanh[(Sqrt[a - b]*Sin[c + d*x])/Sqrt[a]]/(Sqrt[a]*Sqrt[a - b]*d)", //
3757, 214);}

// {3756, 211}
public void test0956() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + b*Tan[c + d*x]^2), x]", //
 "ArcTan[(Sqrt[b]*Tan[c + d*x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*d)", //
3756, 211);}

// {3739, 2697}
public void test0957() {
	check(//
"Integrate[(d*Sec[e + f*x])^m*(b*Tan[e + f*x]^2)^p, x]", //
 "((Cos[e + f*x]^2)^((1 + m + 2*p)/2)*Hypergeometric2F1[(1 + 2*p)/2, (1 + m + 2*p)/2, (3 + 2*p)/2, Sin[e + f*x]^2]*(d*Sec[e + f*x])^m*Tan[e + f*x]*(b*Tan[e + f*x]^2)^p)/(f*(1 + 2*p))", //
3739, 2697);}

// {3740, 2697}
public void test0958() {
	check(//
"Integrate[(d*Sec[e + f*x])^m*(b*(c*Tan[e + f*x])^n)^p, x]", //
 "((Cos[e + f*x]^2)^((1 + m + n*p)/2)*Hypergeometric2F1[(1 + n*p)/2, (1 + m + n*p)/2, (3 + n*p)/2, Sin[e + f*x]^2]*(d*Sec[e + f*x])^m*Tan[e + f*x]*(b*(c*Tan[e + f*x])^n)^p)/(f*(1 + n*p))", //
3740, 2697);}

// {3740, 2697}
public void test0959() {
	check(//
"Integrate[Sec[e + f*x]^3*(b*(c*Tan[e + f*x])^n)^p, x]", //
 "((Cos[e + f*x]^2)^((4 + n*p)/2)*Hypergeometric2F1[(1 + n*p)/2, (4 + n*p)/2, (3 + n*p)/2, Sin[e + f*x]^2]*Sec[e + f*x]^3*Tan[e + f*x]*(b*(c*Tan[e + f*x])^n)^p)/(f*(1 + n*p))", //
3740, 2697);}

// {3740, 2697}
public void test0960() {
	check(//
"Integrate[Sec[e + f*x]*(b*(c*Tan[e + f*x])^n)^p, x]", //
 "((Cos[e + f*x]^2)^((2 + n*p)/2)*Hypergeometric2F1[(1 + n*p)/2, (2 + n*p)/2, (3 + n*p)/2, Sin[e + f*x]^2]*Sec[e + f*x]*Tan[e + f*x]*(b*(c*Tan[e + f*x])^n)^p)/(f*(1 + n*p))", //
3740, 2697);}

// {3740, 2697}
public void test0961() {
	check(//
"Integrate[Cos[e + f*x]*(b*(c*Tan[e + f*x])^n)^p, x]", //
 "((Cos[e + f*x]^2)^((n*p)/2)*Hypergeometric2F1[(n*p)/2, (1 + n*p)/2, (3 + n*p)/2, Sin[e + f*x]^2]*Sin[e + f*x]*(b*(c*Tan[e + f*x])^n)^p)/(f*(1 + n*p))", //
3740, 2697);}

// {3740, 2697}
public void test0962() {
	check(//
"Integrate[Cos[e + f*x]^3*(b*(c*Tan[e + f*x])^n)^p, x]", //
 "((Cos[e + f*x]^2)^((n*p)/2)*Hypergeometric2F1[(-2 + n*p)/2, (1 + n*p)/2, (3 + n*p)/2, Sin[e + f*x]^2]*Sin[e + f*x]*(b*(c*Tan[e + f*x])^n)^p)/(f*(1 + n*p))", //
3740, 2697);}

// {3554, 8}
public void test0963() {
	check(//
"Integrate[Cot[a + b*x]^2, x]", //
 "-x - Cot[a + b*x]/b", //
3554, 8);}

// {3554, 3556}
public void test0964() {
	check(//
"Integrate[Cot[a + b*x]^3, x]", //
 "-Cot[a + b*x]^2/(2*b) - Log[Sin[a + b*x]]/b", //
3554, 3556);}

// {3557, 371}
public void test0965() {
	check(//
"Integrate[Cot[a + b*x]^n, x]", //
 "-((Cot[a + b*x]^(1 + n)*Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, -Cot[a + b*x]^2])/(b*(1 + n)))", //
3557, 371);}

// {3557, 371}
public void test0966() {
	check(//
"Integrate[(b*Cot[c + d*x])^n, x]", //
 "-(((b*Cot[c + d*x])^(1 + n)*Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, -Cot[c + d*x]^2])/(b*d*(1 + n)))", //
3557, 371);}

// {3739, 3556}
public void test0967() {
	check(//
"Integrate[Sqrt[a*Cot[x]^2], x]", //
 "Sqrt[a*Cot[x]^2]*Log[Sin[x]]*Tan[x]", //
3739, 3556);}

// {3739, 3556}
public void test0968() {
	check(//
"Integrate[1/Sqrt[a*Cot[x]^2], x]", //
 "-((Cot[x]*Log[Cos[x]])/Sqrt[a*Cot[x]^2])", //
3739, 3556);}

// {2683, 2697}
public void test0969() {
	check(//
"Integrate[(b*Cot[e + f*x])^n*(a*Sin[e + f*x])^m, x]", //
 "-(((b*Cot[e + f*x])^(1 + n)*Hypergeometric2F1[(1 + n)/2, (1 - m + n)/2, (3 + n)/2, Cos[e + f*x]^2]*(a*Sin[e + f*x])^m*(Sin[e + f*x]^2)^((1 - m + n)/2))/(b*f*(1 + n)))", //
2683, 2697);}

// {2682, 2656}
public void test0970() {
	check(//
"Integrate[(a*Cos[e + f*x])^m*(b*Cot[e + f*x])^n, x]", //
 "-(((a*Cos[e + f*x])^m*(b*Cot[e + f*x])^(1 + n)*Hypergeometric2F1[(1 + n)/2, (1 + m + n)/2, (3 + m + n)/2, Cos[e + f*x]^2]*(Sin[e + f*x]^2)^((1 + n)/2))/(b*f*(1 + m + n)))", //
2682, 2656);}

// {2687, 32}
public void test0971() {
	check(//
"Integrate[(d*Cot[e + f*x])^n*Csc[e + f*x]^2, x]", //
 "-((d*Cot[e + f*x])^(1 + n)/(d*f*(1 + n)))", //
2687, 32);}

// {2687, 371}
public void test0972() {
	check(//
"Integrate[(d*Cot[e + f*x])^n*Sin[e + f*x]^2, x]", //
 "-(((d*Cot[e + f*x])^(1 + n)*Hypergeometric2F1[2, (1 + n)/2, (3 + n)/2, -Cot[e + f*x]^2])/(d*f*(1 + n)))", //
2687, 371);}

// {2687, 371}
public void test0973() {
	check(//
"Integrate[(d*Cot[e + f*x])^n*Sin[e + f*x]^4, x]", //
 "-(((d*Cot[e + f*x])^(1 + n)*Hypergeometric2F1[3, (1 + n)/2, (3 + n)/2, -Cot[e + f*x]^2])/(d*f*(1 + n)))", //
2687, 371);}

// {3808, 2212}
public void test0974() {
	check(//
"Integrate[(c + d*x)^m/(a + I*a*Cot[e + f*x]), x]", //
 "(c + d*x)^(1 + m)/(2*a*d*(1 + m)) + (I*2^(-2 - m)*E^((2*I)*(e - (c*f)/d))*(c + d*x)^m*Gamma[1 + m, ((-2*I)*f*(c + d*x))/d])/(a*f*(((-I)*f*(c + d*x))/d)^m)", //
3808, 2212);}

// {3583, 2718}
public void test0975() {
	check(//
"Integrate[Sin[x]/(I + Cot[x]), x]", //
 "((2*I)/3)*Cos[x] + ((I/3)*Sin[x])/(I + Cot[x])", //
3583, 2718);}

// {3568, 31}
public void test0976() {
	check(//
"Integrate[Csc[x]^2/(I + Cot[x]), x]", //
 "(-I)*x + Log[Sin[x]]", //
3568, 31);}

// {3582, 3855}
public void test0977() {
	check(//
"Integrate[Csc[x]^3/(I + Cot[x]), x]", //
 "I*ArcTanh[Cos[x]] - Csc[x]", //
3582, 3855);}

// {3568}
public void test0978() {
	check(//
"Integrate[Csc[x]^4/(I + Cot[x]), x]", //
 "I*Cot[x] - Cot[x]^2/2", //
3568);}

// {3587, 31}
public void test0979() {
	check(//
"Integrate[Csc[x]^2/(a + b*Cot[x]), x]", //
 "-(Log[a + b*Cot[x]]/b)", //
3587, 31);}

// {3590, 212}
public void test0980() {
	check(//
"Integrate[Csc[x]/(a + b*Cot[x]), x]", //
 "-(ArcTanh[((-b + a*Cot[x])*Sin[x])/Sqrt[a^2 + b^2]]/Sqrt[a^2 + b^2])", //
3590, 212);}

// {3587, 32}
public void test0981() {
	check(//
"Integrate[Csc[x]^2/(a + b*Cot[x])^2, x]", //
 "1/(b*(a + b*Cot[x]))", //
3587, 32);}

// {3587, 32}
public void test0982() {
	check(//
"Integrate[(a + b*Cot[x])^n*Csc[x]^2, x]", //
 "-((a + b*Cot[x])^(1 + n)/(b*(1 + n)))", //
3587, 32);}

// {3562, 70}
public void test0983() {
	check(//
"Integrate[(a + I*a*Cot[c + d*x])^n, x]", //
 "((I/2)*(a + I*a*Cot[c + d*x])^n*Hypergeometric2F1[1, n, 1 + n, (1 + I*Cot[c + d*x])/2])/(d*n)", //
3562, 70);}

// {3613, 211}
public void test0984() {
	check(//
"Integrate[(a + a*Cot[c + d*x])/Sqrt[e*Cot[c + d*x]], x]", //
 "(Sqrt[2]*a*ArcTan[(Sqrt[e]*(1 - Cot[c + d*x]))/(Sqrt[2]*Sqrt[e*Cot[c + d*x]])])/(d*Sqrt[e])", //
3613, 211);}

// {3612, 3611}
public void test0985() {
	check(//
"Integrate[(A + B*Cot[c + d*x])/(a + b*Cot[c + d*x]), x]", //
 "((a*A + b*B)*x)/(a^2 + b^2) - ((A*b - a*B)*Log[b*Cos[c + d*x] + a*Sin[c + d*x]])/((a^2 + b^2)*d)", //
3612, 3611);}

// {3852, 8}
public void test0986() {
	check(//
"Integrate[Sec[a + b*x]^2, x]", //
 "Tan[a + b*x]/b", //
3852, 8);}

// {3853, 3855}
public void test0987() {
	check(//
"Integrate[Sec[a + b*x]^3, x]", //
 "ArcTanh[Sin[a + b*x]]/(2*b) + (Sec[a + b*x]*Tan[a + b*x])/(2*b)", //
3853, 3855);}

// {3852}
public void test0988() {
	check(//
"Integrate[Sec[a + b*x]^4, x]", //
 "Tan[a + b*x]/b + Tan[a + b*x]^3/(3*b)", //
3852);}

// {3852}
public void test0989() {
	check(//
"Integrate[Sec[a + b*x]^6, x]", //
 "Tan[a + b*x]/b + (2*Tan[a + b*x]^3)/(3*b) + Tan[a + b*x]^5/(5*b)", //
3852);}

// {3852}
public void test0990() {
	check(//
"Integrate[Sec[a + b*x]^8, x]", //
 "Tan[a + b*x]/b + Tan[a + b*x]^3/b + (3*Tan[a + b*x]^5)/(5*b) + Tan[a + b*x]^7/(7*b)", //
3852);}

// {3856, 2720}
public void test0991() {
	check(//
"Integrate[Sqrt[Sec[a + b*x]], x]", //
 "(2*Sqrt[Cos[a + b*x]]*EllipticF[(a + b*x)/2, 2]*Sqrt[Sec[a + b*x]])/b", //
3856, 2720);}

// {3856, 2719}
public void test0992() {
	check(//
"Integrate[1/Sqrt[Sec[a + b*x]], x]", //
 "(2*Sqrt[Cos[a + b*x]]*EllipticE[(a + b*x)/2, 2]*Sqrt[Sec[a + b*x]])/b", //
3856, 2719);}

// {3856, 2720}
public void test0993() {
	check(//
"Integrate[Sqrt[c*Sec[a + b*x]], x]", //
 "(2*Sqrt[Cos[a + b*x]]*EllipticF[(a + b*x)/2, 2]*Sqrt[c*Sec[a + b*x]])/b", //
3856, 2720);}

// {3856, 2719}
public void test0994() {
	check(//
"Integrate[1/Sqrt[c*Sec[a + b*x]], x]", //
 "(2*EllipticE[(a + b*x)/2, 2])/(b*Sqrt[Cos[a + b*x]]*Sqrt[c*Sec[a + b*x]])", //
3856, 2719);}

// {3857, 2722}
public void test0995() {
	check(//
"Integrate[Sec[a + b*x]^(4/3), x]", //
 "(3*Hypergeometric2F1[-1/6, 1/2, 5/6, Cos[a + b*x]^2]*Sec[a + b*x]^(1/3)*Sin[a + b*x])/(b*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test0996() {
	check(//
"Integrate[Sec[a + b*x]^(2/3), x]", //
 "(-3*Hypergeometric2F1[1/6, 1/2, 7/6, Cos[a + b*x]^2]*Sin[a + b*x])/(b*Sec[a + b*x]^(1/3)*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test0997() {
	check(//
"Integrate[Sec[a + b*x]^(1/3), x]", //
 "(-3*Hypergeometric2F1[1/3, 1/2, 4/3, Cos[a + b*x]^2]*Sin[a + b*x])/(2*b*Sec[a + b*x]^(2/3)*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test0998() {
	check(//
"Integrate[Sec[a + b*x]^(-1/3), x]", //
 "(-3*Hypergeometric2F1[1/2, 2/3, 5/3, Cos[a + b*x]^2]*Sin[a + b*x])/(4*b*Sec[a + b*x]^(4/3)*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test0999() {
	check(//
"Integrate[Sec[a + b*x]^(-2/3), x]", //
 "(-3*Hypergeometric2F1[1/2, 5/6, 11/6, Cos[a + b*x]^2]*Sin[a + b*x])/(5*b*Sec[a + b*x]^(5/3)*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1000() {
	check(//
"Integrate[Sec[a + b*x]^(-4/3), x]", //
 "(-3*Hypergeometric2F1[1/2, 7/6, 13/6, Cos[a + b*x]^2]*Sin[a + b*x])/(7*b*Sec[a + b*x]^(7/3)*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1001() {
	check(//
"Integrate[(c*Sec[a + b*x])^(4/3), x]", //
 "(3*c*Hypergeometric2F1[-1/6, 1/2, 5/6, Cos[a + b*x]^2]*(c*Sec[a + b*x])^(1/3)*Sin[a + b*x])/(b*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1002() {
	check(//
"Integrate[(c*Sec[a + b*x])^(2/3), x]", //
 "(-3*Cos[a + b*x]*Hypergeometric2F1[1/6, 1/2, 7/6, Cos[a + b*x]^2]*(c*Sec[a + b*x])^(2/3)*Sin[a + b*x])/(b*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1003() {
	check(//
"Integrate[(c*Sec[a + b*x])^(1/3), x]", //
 "(-3*Cos[a + b*x]*Hypergeometric2F1[1/3, 1/2, 4/3, Cos[a + b*x]^2]*(c*Sec[a + b*x])^(1/3)*Sin[a + b*x])/(2*b*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1004() {
	check(//
"Integrate[(c*Sec[a + b*x])^(-1/3), x]", //
 "(-3*Cos[a + b*x]^2*Hypergeometric2F1[1/2, 2/3, 5/3, Cos[a + b*x]^2]*(c*Sec[a + b*x])^(2/3)*Sin[a + b*x])/(4*b*c*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1005() {
	check(//
"Integrate[(c*Sec[a + b*x])^(-2/3), x]", //
 "(-3*Cos[a + b*x]^2*Hypergeometric2F1[1/2, 5/6, 11/6, Cos[a + b*x]^2]*(c*Sec[a + b*x])^(1/3)*Sin[a + b*x])/(5*b*c*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1006() {
	check(//
"Integrate[(c*Sec[a + b*x])^(-4/3), x]", //
 "(-3*Cos[a + b*x]^3*Hypergeometric2F1[1/2, 7/6, 13/6, Cos[a + b*x]^2]*(c*Sec[a + b*x])^(2/3)*Sin[a + b*x])/(7*b*c^2*Sqrt[Sin[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1007() {
	check(//
"Integrate[Sec[a + b*x]^n, x]", //
 "-((Hypergeometric2F1[1/2, (1 - n)/2, (3 - n)/2, Cos[a + b*x]^2]*Sec[a + b*x]^(-1 + n)*Sin[a + b*x])/(b*(1 - n)*Sqrt[Sin[a + b*x]^2]))", //
3857, 2722);}

// {3857, 2722}
public void test1008() {
	check(//
"Integrate[(c*Sec[a + b*x])^n, x]", //
 "-((Cos[a + b*x]*Hypergeometric2F1[1/2, (1 - n)/2, (3 - n)/2, Cos[a + b*x]^2]*(c*Sec[a + b*x])^n*Sin[a + b*x])/(b*(1 - n)*Sqrt[Sin[a + b*x]^2]))", //
3857, 2722);}

// {4207, 221}
public void test1009() {
	check(//
"Integrate[Sqrt[Sec[x]^2], x]", //
 "ArcSinh[Tan[x]]", //
4207, 221);}

// {4207, 197}
public void test1010() {
	check(//
"Integrate[1/Sqrt[Sec[x]^2], x]", //
 "Tan[x]/Sqrt[Sec[x]^2]", //
4207, 197);}

// {4207, 197}
public void test1011() {
	check(//
"Integrate[1/Sqrt[a*Sec[x]^2], x]", //
 "Tan[x]/Sqrt[a*Sec[x]^2]", //
4207, 197);}

// {3856, 2720}
public void test1012() {
	check(//
"Integrate[Sqrt[b*Sec[c + d*x]], x]", //
 "(2*Sqrt[Cos[c + d*x]]*EllipticF[(c + d*x)/2, 2]*Sqrt[b*Sec[c + d*x]])/d", //
3856, 2720);}

// {3856, 2719}
public void test1013() {
	check(//
"Integrate[1/Sqrt[b*Sec[c + d*x]], x]", //
 "(2*EllipticE[(c + d*x)/2, 2])/(d*Sqrt[Cos[c + d*x]]*Sqrt[b*Sec[c + d*x]])", //
3856, 2719);}

// {17, 3855}
public void test1014() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]*Sqrt[b*Sec[c + d*x]], x]", //
 "(ArcTanh[Sin[c + d*x]]*Sqrt[b*Sec[c + d*x]])/(d*Sqrt[Sec[c + d*x]])", //
17, 3855);}

// {17, 8}
public void test1015() {
	check(//
"Integrate[Sqrt[b*Sec[c + d*x]]/Sqrt[Sec[c + d*x]], x]", //
 "(x*Sqrt[b*Sec[c + d*x]])/Sqrt[Sec[c + d*x]]", //
17, 8);}

// {17, 2717}
public void test1016() {
	check(//
"Integrate[Sqrt[b*Sec[c + d*x]]/Sec[c + d*x]^(3/2), x]", //
 "(Sqrt[b*Sec[c + d*x]]*Sin[c + d*x])/(d*Sqrt[Sec[c + d*x]])", //
17, 2717);}

// {17, 3855}
public void test1017() {
	check(//
"Integrate[(b*Sec[c + d*x])^(3/2)/Sqrt[Sec[c + d*x]], x]", //
 "(b*ArcTanh[Sin[c + d*x]]*Sqrt[b*Sec[c + d*x]])/(d*Sqrt[Sec[c + d*x]])", //
17, 3855);}

// {17, 8}
public void test1018() {
	check(//
"Integrate[(b*Sec[c + d*x])^(3/2)/Sec[c + d*x]^(3/2), x]", //
 "(b*x*Sqrt[b*Sec[c + d*x]])/Sqrt[Sec[c + d*x]]", //
17, 8);}

// {17, 2717}
public void test1019() {
	check(//
"Integrate[(b*Sec[c + d*x])^(3/2)/Sec[c + d*x]^(5/2), x]", //
 "(b*Sqrt[b*Sec[c + d*x]]*Sin[c + d*x])/(d*Sqrt[Sec[c + d*x]])", //
17, 2717);}

// {17, 3855}
public void test1020() {
	check(//
"Integrate[(b*Sec[c + d*x])^(5/2)/Sec[c + d*x]^(3/2), x]", //
 "(b^2*ArcTanh[Sin[c + d*x]]*Sqrt[b*Sec[c + d*x]])/(d*Sqrt[Sec[c + d*x]])", //
17, 3855);}

// {17, 8}
public void test1021() {
	check(//
"Integrate[(b*Sec[c + d*x])^(5/2)/Sec[c + d*x]^(5/2), x]", //
 "(b^2*x*Sqrt[b*Sec[c + d*x]])/Sqrt[Sec[c + d*x]]", //
17, 8);}

// {17, 2717}
public void test1022() {
	check(//
"Integrate[(b*Sec[c + d*x])^(5/2)/Sec[c + d*x]^(7/2), x]", //
 "(b^2*Sqrt[b*Sec[c + d*x]]*Sin[c + d*x])/(d*Sqrt[Sec[c + d*x]])", //
17, 2717);}

// {17, 3855}
public void test1023() {
	check(//
"Integrate[Sec[c + d*x]^(3/2)/Sqrt[b*Sec[c + d*x]], x]", //
 "(ArcTanh[Sin[c + d*x]]*Sqrt[Sec[c + d*x]])/(d*Sqrt[b*Sec[c + d*x]])", //
17, 3855);}

// {17, 8}
public void test1024() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[b*Sec[c + d*x]], x]", //
 "(x*Sqrt[Sec[c + d*x]])/Sqrt[b*Sec[c + d*x]]", //
17, 8);}

// {18, 2717}
public void test1025() {
	check(//
"Integrate[1/(Sqrt[Sec[c + d*x]]*Sqrt[b*Sec[c + d*x]]), x]", //
 "(Sqrt[Sec[c + d*x]]*Sin[c + d*x])/(d*Sqrt[b*Sec[c + d*x]])", //
18, 2717);}

// {17, 3855}
public void test1026() {
	check(//
"Integrate[Sec[c + d*x]^(5/2)/(b*Sec[c + d*x])^(3/2), x]", //
 "(ArcTanh[Sin[c + d*x]]*Sqrt[Sec[c + d*x]])/(b*d*Sqrt[b*Sec[c + d*x]])", //
17, 3855);}

// {17, 8}
public void test1027() {
	check(//
"Integrate[Sec[c + d*x]^(3/2)/(b*Sec[c + d*x])^(3/2), x]", //
 "(x*Sqrt[Sec[c + d*x]])/(b*Sqrt[b*Sec[c + d*x]])", //
17, 8);}

// {17, 2717}
public void test1028() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/(b*Sec[c + d*x])^(3/2), x]", //
 "(Sqrt[Sec[c + d*x]]*Sin[c + d*x])/(b*d*Sqrt[b*Sec[c + d*x]])", //
17, 2717);}

// {17, 3855}
public void test1029() {
	check(//
"Integrate[Sec[c + d*x]^(7/2)/(b*Sec[c + d*x])^(5/2), x]", //
 "(ArcTanh[Sin[c + d*x]]*Sqrt[Sec[c + d*x]])/(b^2*d*Sqrt[b*Sec[c + d*x]])", //
17, 3855);}

// {17, 8}
public void test1030() {
	check(//
"Integrate[Sec[c + d*x]^(5/2)/(b*Sec[c + d*x])^(5/2), x]", //
 "(x*Sqrt[Sec[c + d*x]])/(b^2*Sqrt[b*Sec[c + d*x]])", //
17, 8);}

// {17, 2717}
public void test1031() {
	check(//
"Integrate[Sec[c + d*x]^(3/2)/(b*Sec[c + d*x])^(5/2), x]", //
 "(Sqrt[Sec[c + d*x]]*Sin[c + d*x])/(b^2*d*Sqrt[b*Sec[c + d*x]])", //
17, 2717);}

// {3857, 2722}
public void test1032() {
	check(//
"Integrate[(b*Sec[c + d*x])^(1/3), x]", //
 "(-3*Cos[c + d*x]*Hypergeometric2F1[1/3, 1/2, 4/3, Cos[c + d*x]^2]*(b*Sec[c + d*x])^(1/3)*Sin[c + d*x])/(2*d*Sqrt[Sin[c + d*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1033() {
	check(//
"Integrate[(b*Sec[c + d*x])^(4/3), x]", //
 "(3*b*Hypergeometric2F1[-1/6, 1/2, 5/6, Cos[c + d*x]^2]*(b*Sec[c + d*x])^(1/3)*Sin[c + d*x])/(d*Sqrt[Sin[c + d*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1034() {
	check(//
"Integrate[(b*Sec[c + d*x])^(-1/3), x]", //
 "(-3*Cos[c + d*x]^2*Hypergeometric2F1[1/2, 2/3, 5/3, Cos[c + d*x]^2]*(b*Sec[c + d*x])^(2/3)*Sin[c + d*x])/(4*b*d*Sqrt[Sin[c + d*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1035() {
	check(//
"Integrate[(b*Sec[c + d*x])^(-4/3), x]", //
 "(-3*Cos[c + d*x]^3*Hypergeometric2F1[1/2, 7/6, 13/6, Cos[c + d*x]^2]*(b*Sec[c + d*x])^(2/3)*Sin[c + d*x])/(7*b^2*d*Sqrt[Sin[c + d*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1036() {
	check(//
"Integrate[(b*Sec[c + d*x])^n, x]", //
 "-((Cos[c + d*x]*Hypergeometric2F1[1/2, (1 - n)/2, (3 - n)/2, Cos[c + d*x]^2]*(b*Sec[c + d*x])^n*Sin[c + d*x])/(d*(1 - n)*Sqrt[Sin[c + d*x]^2]))", //
3857, 2722);}

// {2702, 30}
public void test1037() {
	check(//
"Integrate[(d*Sec[a + b*x])^(7/2)*Sin[a + b*x], x]", //
 "(2*d*(d*Sec[a + b*x])^(5/2))/(5*b)", //
2702, 30);}

// {2702, 30}
public void test1038() {
	check(//
"Integrate[(d*Sec[a + b*x])^(5/2)*Sin[a + b*x], x]", //
 "(2*d*(d*Sec[a + b*x])^(3/2))/(3*b)", //
2702, 30);}

// {2702, 30}
public void test1039() {
	check(//
"Integrate[(d*Sec[a + b*x])^(3/2)*Sin[a + b*x], x]", //
 "(2*d*Sqrt[d*Sec[a + b*x]])/b", //
2702, 30);}

// {2702, 30}
public void test1040() {
	check(//
"Integrate[Sqrt[d*Sec[a + b*x]]*Sin[a + b*x], x]", //
 "(-2*d)/(b*Sqrt[d*Sec[a + b*x]])", //
2702, 30);}

// {2702, 30}
public void test1041() {
	check(//
"Integrate[Sin[a + b*x]/Sqrt[d*Sec[a + b*x]], x]", //
 "(-2*d)/(3*b*(d*Sec[a + b*x])^(3/2))", //
2702, 30);}

// {2705, 2699}
public void test1042() {
	check(//
"Integrate[(d*Csc[a + b*x])^(7/2)*Sqrt[c*Sec[a + b*x]], x]", //
 "(-8*c*d^3*Sqrt[d*Csc[a + b*x]])/(5*b*Sqrt[c*Sec[a + b*x]]) - (2*c*d*(d*Csc[a + b*x])^(5/2))/(5*b*Sqrt[c*Sec[a + b*x]])", //
2705, 2699);}

// {2705, 2699}
public void test1043() {
	check(//
"Integrate[(d*Csc[a + b*x])^(5/2)*(c*Sec[a + b*x])^(3/2), x]", //
 "(8*c*d^3*Sqrt[c*Sec[a + b*x]])/(3*b*Sqrt[d*Csc[a + b*x]]) - (2*c*d*(d*Csc[a + b*x])^(3/2)*Sqrt[c*Sec[a + b*x]])/(3*b)", //
2705, 2699);}

// {2706, 2699}
public void test1044() {
	check(//
"Integrate[(d*Csc[a + b*x])^(3/2)*(c*Sec[a + b*x])^(5/2), x]", //
 "(-8*c^3*d*Sqrt[d*Csc[a + b*x]])/(3*b*Sqrt[c*Sec[a + b*x]]) + (2*c*d*Sqrt[d*Csc[a + b*x]]*(c*Sec[a + b*x])^(3/2))/(3*b)", //
2706, 2699);}

// {2705, 2699}
public void test1045() {
	check(//
"Integrate[(d*Csc[a + b*x])^(9/2)/Sqrt[c*Sec[a + b*x]], x]", //
 "(-8*c*d^3*(d*Csc[a + b*x])^(3/2))/(21*b*(c*Sec[a + b*x])^(3/2)) - (2*c*d*(d*Csc[a + b*x])^(7/2))/(7*b*(c*Sec[a + b*x])^(3/2))", //
2705, 2699);}

// {2711, 2657}
public void test1046() {
	check(//
"Integrate[Csc[e + f*x]^n*Sec[e + f*x]^m, x]", //
 "((Cos[e + f*x]^2)^((1 + m)/2)*Csc[e + f*x]^(-1 + n)*Hypergeometric2F1[(1 + m)/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*Sec[e + f*x]^(1 + m))/(f*(1 - n))", //
2711, 2657);}

// {2711, 2657}
public void test1047() {
	check(//
"Integrate[Csc[e + f*x]^n*(a*Sec[e + f*x])^m, x]", //
 "((Cos[e + f*x]^2)^((1 + m)/2)*Csc[e + f*x]^(-1 + n)*Hypergeometric2F1[(1 + m)/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*(a*Sec[e + f*x])^(1 + m))/(a*f*(1 - n))", //
2711, 2657);}

// {2711, 2657}
public void test1048() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*Sec[e + f*x]^m, x]", //
 "(b*(Cos[e + f*x]^2)^((1 + m)/2)*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[(1 + m)/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*Sec[e + f*x]^(1 + m))/(f*(1 - n))", //
2711, 2657);}

// {2711, 2657}
public void test1049() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*(a*Sec[e + f*x])^m, x]", //
 "(b*(Cos[e + f*x]^2)^((1 + m)/2)*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[(1 + m)/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*(a*Sec[e + f*x])^(1 + m))/(a*f*(1 - n))", //
2711, 2657);}

// {2701, 371}
public void test1050() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*Sec[e + f*x]^5, x]", //
 "((b*Csc[e + f*x])^(5 + n)*Hypergeometric2F1[3, (5 + n)/2, (7 + n)/2, Csc[e + f*x]^2])/(b^5*f*(5 + n))", //
2701, 371);}

// {2701, 371}
public void test1051() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*Sec[e + f*x]^3, x]", //
 "-(((b*Csc[e + f*x])^(3 + n)*Hypergeometric2F1[2, (3 + n)/2, (5 + n)/2, Csc[e + f*x]^2])/(b^3*f*(3 + n)))", //
2701, 371);}

// {2701, 371}
public void test1052() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*Sec[e + f*x], x]", //
 "((b*Csc[e + f*x])^(1 + n)*Hypergeometric2F1[1, (1 + n)/2, (3 + n)/2, Csc[e + f*x]^2])/(b*f*(1 + n))", //
2701, 371);}

// {2701, 30}
public void test1053() {
	check(//
"Integrate[Cos[e + f*x]*(b*Csc[e + f*x])^n, x]", //
 "(b*(b*Csc[e + f*x])^(-1 + n))/(f*(1 - n))", //
2701, 30);}

// {2711, 2657}
public void test1054() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*Sec[e + f*x]^6, x]", //
 "(b*Sqrt[Cos[e + f*x]^2]*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[7/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*Sec[e + f*x])/(f*(1 - n))", //
2711, 2657);}

// {2711, 2657}
public void test1055() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*Sec[e + f*x]^4, x]", //
 "(b*Sqrt[Cos[e + f*x]^2]*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[5/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*Sec[e + f*x])/(f*(1 - n))", //
2711, 2657);}

// {2711, 2657}
public void test1056() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*Sec[e + f*x]^2, x]", //
 "(b*Sqrt[Cos[e + f*x]^2]*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[3/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*Sec[e + f*x])/(f*(1 - n))", //
2711, 2657);}

// {3857, 2722}
public void test1057() {
	check(//
"Integrate[(b*Csc[e + f*x])^n, x]", //
 "(Cos[e + f*x]*(b*Csc[e + f*x])^n*Hypergeometric2F1[1/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*Sin[e + f*x])/(f*(1 - n)*Sqrt[Cos[e + f*x]^2])", //
3857, 2722);}

// {2711, 2657}
public void test1058() {
	check(//
"Integrate[Cos[e + f*x]^2*(b*Csc[e + f*x])^n, x]", //
 "(b*Cos[e + f*x]*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[-1/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2])/(f*(1 - n)*Sqrt[Cos[e + f*x]^2])", //
2711, 2657);}

// {2711, 2657}
public void test1059() {
	check(//
"Integrate[Cos[e + f*x]^4*(b*Csc[e + f*x])^n, x]", //
 "(b*Cos[e + f*x]*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[-3/2, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2])/(f*(1 - n)*Sqrt[Cos[e + f*x]^2])", //
2711, 2657);}

// {2711, 2657}
public void test1060() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*(c*Sec[e + f*x])^(3/2), x]", //
 "(b*(Cos[e + f*x]^2)^(5/4)*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[5/4, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*(c*Sec[e + f*x])^(5/2))/(c*f*(1 - n))", //
2711, 2657);}

// {2711, 2657}
public void test1061() {
	check(//
"Integrate[(b*Csc[e + f*x])^n*Sqrt[c*Sec[e + f*x]], x]", //
 "(b*(Cos[e + f*x]^2)^(3/4)*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[3/4, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*(c*Sec[e + f*x])^(3/2))/(c*f*(1 - n))", //
2711, 2657);}

// {2711, 2657}
public void test1062() {
	check(//
"Integrate[(b*Csc[e + f*x])^n/Sqrt[c*Sec[e + f*x]], x]", //
 "(b*(Cos[e + f*x]^2)^(1/4)*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[1/4, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2]*Sqrt[c*Sec[e + f*x]])/(c*f*(1 - n))", //
2711, 2657);}

// {2711, 2657}
public void test1063() {
	check(//
"Integrate[(b*Csc[e + f*x])^n/(c*Sec[e + f*x])^(3/2), x]", //
 "(b*(b*Csc[e + f*x])^(-1 + n)*Hypergeometric2F1[-1/4, (1 - n)/2, (3 - n)/2, Sin[e + f*x]^2])/(c*f*(1 - n)*(Cos[e + f*x]^2)^(1/4)*Sqrt[c*Sec[e + f*x]])", //
2711, 2657);}

// {3855}
public void test1074() {
	check(//
"Integrate[a + a*Sec[c + d*x], x]", //
 "a*x + (a*ArcTanh[Sin[c + d*x]])/d", //
3855);}

// {3862, 8}
public void test1075() {
	check(//
"Integrate[(a + a*Sec[c + d*x])^(-1), x]", //
 "x/a - Tan[c + d*x]/(d*(a + a*Sec[c + d*x]))", //
3862, 8);}

// {3882, 3879}
public void test1076() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + a*Sec[c + d*x])^2, x]", //
 "-Tan[c + d*x]/(3*d*(a + a*Sec[c + d*x])^2) + (2*Tan[c + d*x])/(3*d*(a^2 + a^2*Sec[c + d*x]))", //
3882, 3879);}

// {3881, 3879}
public void test1077() {
	check(//
"Integrate[Sec[c + d*x]/(a + a*Sec[c + d*x])^2, x]", //
 "Tan[c + d*x]/(3*d*(a + a*Sec[c + d*x])^2) + Tan[c + d*x]/(3*d*(a^2 + a^2*Sec[c + d*x]))", //
3881, 3879);}

// {3883, 3877}
public void test1078() {
	check(//
"Integrate[Sec[c + d*x]^2*Sqrt[a + a*Sec[c + d*x]], x]", //
 "(2*a*Tan[c + d*x])/(3*d*Sqrt[a + a*Sec[c + d*x]]) + (2*Sqrt[a + a*Sec[c + d*x]]*Tan[c + d*x])/(3*d)", //
3883, 3877);}

// {3859, 209}
public void test1079() {
	check(//
"Integrate[Sqrt[a + a*Sec[c + d*x]], x]", //
 "(2*Sqrt[a]*ArcTan[(Sqrt[a]*Tan[c + d*x])/Sqrt[a + a*Sec[c + d*x]]])/d", //
3859, 209);}

// {3878, 3877}
public void test1080() {
	check(//
"Integrate[Sec[c + d*x]*(a + a*Sec[c + d*x])^(3/2), x]", //
 "(8*a^2*Tan[c + d*x])/(3*d*Sqrt[a + a*Sec[c + d*x]]) + (2*a*Sqrt[a + a*Sec[c + d*x]]*Tan[c + d*x])/(3*d)", //
3878, 3877);}

// {3859, 209}
public void test1081() {
	check(//
"Integrate[Sqrt[a - a*Sec[c + d*x]], x]", //
 "(2*Sqrt[a]*ArcTan[(Sqrt[a]*Tan[c + d*x])/Sqrt[a - a*Sec[c + d*x]]])/d", //
3859, 209);}

// {3880, 209}
public void test1082() {
	check(//
"Integrate[Sec[c + d*x]/Sqrt[a + a*Sec[c + d*x]], x]", //
 "(Sqrt[2]*ArcTan[(Sqrt[a]*Tan[c + d*x])/(Sqrt[2]*Sqrt[a + a*Sec[c + d*x]])])/(Sqrt[a]*d)", //
3880, 209);}

// {3880, 209}
public void test1083() {
	check(//
"Integrate[Sec[c + d*x]/Sqrt[a - a*Sec[c + d*x]], x]", //
 "-((Sqrt[2]*ArcTan[(Sqrt[a]*Tan[c + d*x])/(Sqrt[2]*Sqrt[a - a*Sec[c + d*x]])])/(Sqrt[a]*d))", //
3880, 209);}

// {3886, 221}
public void test1084() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]*Sqrt[a + a*Sec[c + d*x]], x]", //
 "(2*Sqrt[a]*ArcSinh[(Sqrt[a]*Tan[c + d*x])/Sqrt[a + a*Sec[c + d*x]]])/d", //
3886, 221);}

// {3890, 3889}
public void test1085() {
	check(//
"Integrate[Sqrt[a + a*Sec[c + d*x]]/Sec[c + d*x]^(3/2), x]", //
 "(2*a*Sin[c + d*x])/(3*d*Sqrt[Sec[c + d*x]]*Sqrt[a + a*Sec[c + d*x]]) + (4*a*Sqrt[Sec[c + d*x]]*Sin[c + d*x])/(3*d*Sqrt[a + a*Sec[c + d*x]])", //
3890, 3889);}

// {3894, 3889}
public void test1086() {
	check(//
"Integrate[(a + a*Sec[c + d*x])^(3/2)/Sec[c + d*x]^(3/2), x]", //
 "(8*a^2*Sqrt[Sec[c + d*x]]*Sin[c + d*x])/(3*d*Sqrt[a + a*Sec[c + d*x]]) + (2*a*Sqrt[a + a*Sec[c + d*x]]*Sin[c + d*x])/(3*d*Sqrt[Sec[c + d*x]])", //
3894, 3889);}

// {3899, 8}
public void test1087() {
	check(//
"Integrate[(a + a*Sec[c + d*x])^(3/2)/Sec[c + d*x]^(1/4), x]", //
 "(4*a^2*Sec[c + d*x]^(3/4)*Sin[c + d*x])/(d*Sqrt[a + a*Sec[c + d*x]])", //
3899, 8);}

// {3886, 221}
public void test1088() {
	check(//
"Integrate[Sqrt[Sec[e + f*x]]*Sqrt[a + a*Sec[e + f*x]], x]", //
 "(2*Sqrt[a]*ArcSinh[(Sqrt[a]*Tan[e + f*x])/Sqrt[a + a*Sec[e + f*x]]])/f", //
3886, 221);}

// {3886, 221}
public void test1089() {
	check(//
"Integrate[Sqrt[-Sec[e + f*x]]*Sqrt[a - a*Sec[e + f*x]], x]", //
 "(2*Sqrt[a]*ArcSinh[(Sqrt[a]*Tan[e + f*x])/Sqrt[a - a*Sec[e + f*x]]])/f", //
3886, 221);}

// {3893, 212}
public void test1090() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[a + a*Sec[c + d*x]], x]", //
 "(Sqrt[2]*ArcTanh[(Sqrt[a]*Sqrt[Sec[c + d*x]]*Sin[c + d*x])/(Sqrt[2]*Sqrt[a + a*Sec[c + d*x]])])/(Sqrt[a]*d)", //
3893, 212);}

// {3892, 221}
public void test1091() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[1 + Sec[c + d*x]], x]", //
 "(Sqrt[2]*ArcSinh[Tan[c + d*x]/(1 + Sec[c + d*x])])/d", //
3892, 221);}

// {3891, 67}
public void test1092() {
	check(//
"Integrate[Sec[e + f*x]^n*Sqrt[1 + Sec[e + f*x]], x]", //
 "(2*Hypergeometric2F1[1/2, 1 - n, 3/2, 1 - Sec[e + f*x]]*Tan[e + f*x])/(f*Sqrt[1 + Sec[e + f*x]])", //
3891, 67);}

// {3891, 66}
public void test1093() {
	check(//
"Integrate[(-Sec[e + f*x])^n*Sqrt[1 + Sec[e + f*x]], x]", //
 "-((Hypergeometric2F1[1/2, n, 1 + n, Sec[e + f*x]]*(-Sec[e + f*x])^n*Tan[e + f*x])/(f*n*Sqrt[1 - Sec[e + f*x]]*Sqrt[1 + Sec[e + f*x]]))", //
3891, 66);}

// {3911, 141}
public void test1094() {
	check(//
"Integrate[(-Sec[e + f*x])^n/Sqrt[1 + Sec[e + f*x]], x]", //
 "-((AppellF1[n, 1/2, 1, 1 + n, Sec[e + f*x], -Sec[e + f*x]]*(-Sec[e + f*x])^n*Tan[e + f*x])/(f*n*Sqrt[1 - Sec[e + f*x]]*Sqrt[1 + Sec[e + f*x]]))", //
3911, 141);}

// {3911, 141}
public void test1095() {
	check(//
"Integrate[(-Sec[e + f*x])^n/(1 + Sec[e + f*x])^(3/2), x]", //
 "-((AppellF1[n, 1/2, 2, 1 + n, Sec[e + f*x], -Sec[e + f*x]]*(-Sec[e + f*x])^n*Tan[e + f*x])/(f*n*Sqrt[1 - Sec[e + f*x]]*Sqrt[1 + Sec[e + f*x]]))", //
3911, 141);}

// {3891, 66}
public void test1096() {
	check(//
"Integrate[(d*Sec[e + f*x])^n*Sqrt[1 + Sec[e + f*x]], x]", //
 "-((Hypergeometric2F1[1/2, n, 1 + n, Sec[e + f*x]]*(d*Sec[e + f*x])^n*Tan[e + f*x])/(f*n*Sqrt[1 - Sec[e + f*x]]*Sqrt[1 + Sec[e + f*x]]))", //
3891, 66);}

// {3912, 138}
public void test1097() {
	check(//
"Integrate[(d*Sec[e + f*x])^n/Sqrt[1 + Sec[e + f*x]], x]", //
 "-((AppellF1[n, 1/2, 1, 1 + n, Sec[e + f*x], -Sec[e + f*x]]*(d*Sec[e + f*x])^n*Tan[e + f*x])/(f*n*Sqrt[1 - Sec[e + f*x]]*Sqrt[1 + Sec[e + f*x]]))", //
3912, 138);}

// {3912, 138}
public void test1098() {
	check(//
"Integrate[(d*Sec[e + f*x])^n/(1 + Sec[e + f*x])^(3/2), x]", //
 "-((AppellF1[n, 1/2, 2, 1 + n, Sec[e + f*x], -Sec[e + f*x]]*(d*Sec[e + f*x])^n*Tan[e + f*x])/(f*n*Sqrt[1 - Sec[e + f*x]]*Sqrt[1 + Sec[e + f*x]]))", //
3912, 138);}

// {3891, 67}
public void test1099() {
	check(//
"Integrate[Sec[e + f*x]^n*Sqrt[a + a*Sec[e + f*x]], x]", //
 "(2*a*Hypergeometric2F1[1/2, 1 - n, 3/2, 1 - Sec[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a + a*Sec[e + f*x]])", //
3891, 67);}

// {3891, 67}
public void test1100() {
	check(//
"Integrate[(-Sec[e + f*x])^n*Sqrt[a - a*Sec[e + f*x]], x]", //
 "(2*a*Hypergeometric2F1[1/2, 1 - n, 3/2, 1 + Sec[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a - a*Sec[e + f*x]])", //
3891, 67);}

// {3910, 138}
public void test1101() {
	check(//
"Integrate[Sec[e + f*x]^n*(1 + Sec[e + f*x])^m, x]", //
 "(2^(1/2 + m)*AppellF1[1/2, 1 - n, 1/2 - m, 3/2, 1 - Sec[e + f*x], (1 - Sec[e + f*x])/2]*Tan[e + f*x])/(f*Sqrt[1 + Sec[e + f*x]])", //
3910, 138);}

// {3911, 138}
public void test1102() {
	check(//
"Integrate[(1 - Sec[e + f*x])^m*Sec[e + f*x]^n, x]", //
 "(Sqrt[2]*AppellF1[1/2 + m, 1 - n, 1/2, 3/2 + m, 1 - Sec[e + f*x], (1 - Sec[e + f*x])/2]*(1 - Sec[e + f*x])^m*Tan[e + f*x])/(f*(1 + 2*m)*Sqrt[1 + Sec[e + f*x]])", //
3911, 138);}

// {3911, 138}
public void test1103() {
	check(//
"Integrate[(-Sec[e + f*x])^n*(1 + Sec[e + f*x])^m, x]", //
 "(Sqrt[2]*AppellF1[1/2 + m, 1 - n, 1/2, 3/2 + m, 1 + Sec[e + f*x], (1 + Sec[e + f*x])/2]*(1 + Sec[e + f*x])^m*Tan[e + f*x])/(f*(1 + 2*m)*Sqrt[1 - Sec[e + f*x]])", //
3911, 138);}

// {3910, 138}
public void test1104() {
	check(//
"Integrate[(1 - Sec[e + f*x])^m*(-Sec[e + f*x])^n, x]", //
 "(2^(1/2 + m)*AppellF1[1/2, 1 - n, 1/2 - m, 3/2, 1 + Sec[e + f*x], (1 + Sec[e + f*x])/2]*Tan[e + f*x])/(f*Sqrt[1 - Sec[e + f*x]])", //
3910, 138);}

// {3912, 138}
public void test1105() {
	check(//
"Integrate[(d*Sec[e + f*x])^n*(1 + Sec[e + f*x])^m, x]", //
 "-((AppellF1[n, 1/2, 1/2 - m, 1 + n, Sec[e + f*x], -Sec[e + f*x]]*(d*Sec[e + f*x])^n*Tan[e + f*x])/(f*n*Sqrt[1 - Sec[e + f*x]]*Sqrt[1 + Sec[e + f*x]]))", //
3912, 138);}

// {3912, 138}
public void test1106() {
	check(//
"Integrate[(1 - Sec[e + f*x])^m*(d*Sec[e + f*x])^n, x]", //
 "-((AppellF1[n, 1/2 - m, 1/2, 1 + n, Sec[e + f*x], -Sec[e + f*x]]*(d*Sec[e + f*x])^n*Tan[e + f*x])/(f*n*Sqrt[1 - Sec[e + f*x]]*Sqrt[1 + Sec[e + f*x]]))", //
3912, 138);}

// {4349, 3889}
public void test1107() {
	check(//
"Integrate[Sqrt[Cos[c + d*x]]*Sqrt[a + a*Sec[c + d*x]], x]", //
 "(2*a*Sin[c + d*x])/(d*Sqrt[Cos[c + d*x]]*Sqrt[a + a*Sec[c + d*x]])", //
4349, 3889);}

// {3855}
public void test1108() {
	check(//
"Integrate[a + b*Sec[c + d*x], x]", //
 "a*x + (b*ArcTanh[Sin[c + d*x]])/d", //
3855);}

// {3868, 2736}
public void test1109() {
	check(//
"Integrate[(3 + 5*Sec[c + d*x])^(-1), x]", //
 "-x/12 + (5*ArcTan[Sin[c + d*x]/(3 + Cos[c + d*x])])/(6*d)", //
3868, 2736);}

// {3934, 2884}
public void test1110() {
	check(//
"Integrate[Sec[c + d*x]^(3/2)/(a + b*Sec[c + d*x]), x]", //
 "(2*Sqrt[Cos[c + d*x]]*EllipticPi[(2*a)/(a + b), (c + d*x)/2, 2]*Sqrt[Sec[c + d*x]])/((a + b)*d)", //
3934, 2884);}

// {3943, 2740}
public void test1111() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[2 + 3*Sec[c + d*x]], x]", //
 "(2*Sqrt[3 + 2*Cos[c + d*x]]*EllipticF[(c + d*x)/2, 4/5]*Sqrt[Sec[c + d*x]])/(Sqrt[5]*d*Sqrt[2 + 3*Sec[c + d*x]])", //
3943, 2740);}

// {3943, 2740}
public void test1112() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[-2 + 3*Sec[c + d*x]], x]", //
 "(2*Sqrt[3 - 2*Cos[c + d*x]]*EllipticF[(c + d*x)/2, -4]*Sqrt[Sec[c + d*x]])/(d*Sqrt[-2 + 3*Sec[c + d*x]])", //
3943, 2740);}

// {3943, 2740}
public void test1113() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[3 + 2*Sec[c + d*x]], x]", //
 "(2*Sqrt[2 + 3*Cos[c + d*x]]*EllipticF[(c + d*x)/2, 6/5]*Sqrt[Sec[c + d*x]])/(Sqrt[5]*d*Sqrt[3 + 2*Sec[c + d*x]])", //
3943, 2740);}

// {3943, 2740}
public void test1114() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[3 - 2*Sec[c + d*x]], x]", //
 "(2*Sqrt[-2 + 3*Cos[c + d*x]]*EllipticF[(c + d*x)/2, 6]*Sqrt[Sec[c + d*x]])/(d*Sqrt[3 - 2*Sec[c + d*x]])", //
3943, 2740);}

// {3943, 2741}
public void test1115() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[-3 + 2*Sec[c + d*x]], x]", //
 "(2*Sqrt[2 - 3*Cos[c + d*x]]*EllipticF[(c + Pi + d*x)/2, 6/5]*Sqrt[Sec[c + d*x]])/(Sqrt[5]*d*Sqrt[-3 + 2*Sec[c + d*x]])", //
3943, 2741);}

// {3943, 2741}
public void test1116() {
	check(//
"Integrate[Sqrt[Sec[c + d*x]]/Sqrt[-3 - 2*Sec[c + d*x]], x]", //
 "(2*Sqrt[-2 - 3*Cos[c + d*x]]*EllipticF[(c + Pi + d*x)/2, 6]*Sqrt[Sec[c + d*x]])/(d*Sqrt[-3 - 2*Sec[c + d*x]])", //
3943, 2741);}

// {3958, 67}
public void test1117() {
	check(//
"Integrate[(a + a*Sec[c + d*x])^n*Sin[c + d*x], x]", //
 "(Hypergeometric2F1[2, 1 + n, 2 + n, 1 + Sec[c + d*x]]*(a + a*Sec[c + d*x])^(1 + n))/(a*d*(1 + n))", //
3958, 67);}

// {3958, 70}
public void test1118() {
	check(//
"Integrate[Csc[c + d*x]*(a + a*Sec[c + d*x])^n, x]", //
 "-(Hypergeometric2F1[1, n, 1 + n, (1 + Sec[c + d*x])/2]*(a + a*Sec[c + d*x])^n)/(2*d*n)", //
3958, 70);}

// {3960, 3917}
public void test1119() {
	check(//
"Integrate[Csc[e + f*x]^2*Sqrt[a + b*Sec[e + f*x]], x]", //
 "(Sqrt[a + b]*Cot[e + f*x]*EllipticF[ArcSin[Sqrt[a + b*Sec[e + f*x]]/Sqrt[a + b]], (a + b)/(a - b)]*Sqrt[(b*(1 - Sec[e + f*x]))/(a + b)]*Sqrt[-((b*(1 + Sec[e + f*x]))/(a - b))])/f - (Cot[e + f*x]*Sqrt[a + b*Sec[e + f*x]])/f", //
3960, 3917);}

// {3959, 67}
public void test1120() {
	check(//
"Integrate[(a + b*Sec[c + d*x])^n*Sin[c + d*x], x]", //
 "(b*Hypergeometric2F1[2, 1 + n, 2 + n, 1 + (b*Sec[c + d*x])/a]*(a + b*Sec[c + d*x])^(1 + n))/(a^2*d*(1 + n))", //
3959, 67);}

// {3964, 31}
public void test1121() {
	check(//
"Integrate[Cot[c + d*x]*(a + a*Sec[c + d*x]), x]", //
 "(a*Log[1 - Cos[c + d*x]])/d", //
3964, 31);}

// {3967, 8}
public void test1122() {
	check(//
"Integrate[Cot[c + d*x]^2*(a + a*Sec[c + d*x]), x]", //
 "-(a*x) - (Cot[c + d*x]*(a + a*Sec[c + d*x]))/d", //
3967, 8);}

// {3964, 31}
public void test1123() {
	check(//
"Integrate[Tan[c + d*x]/(a + a*Sec[c + d*x]), x]", //
 "-(Log[1 + Cos[c + d*x]]/(a*d))", //
3964, 31);}

// {3965, 67}
public void test1124() {
	check(//
"Integrate[(a + a*Sec[c + d*x])^n*Tan[c + d*x], x]", //
 "-((Hypergeometric2F1[1, 1 + n, 2 + n, 1 + Sec[c + d*x]]*(a + a*Sec[c + d*x])^(1 + n))/(a*d*(1 + n)))", //
3965, 67);}

// {3967, 8}
public void test1125() {
	check(//
"Integrate[Cot[c + d*x]^2*(a + b*Sec[c + d*x]), x]", //
 "-(a*x) - (Cot[c + d*x]*(a + b*Sec[c + d*x]))/d", //
3967, 8);}

// {3970, 67}
public void test1126() {
	check(//
"Integrate[(a + b*Sec[c + d*x])^n*Tan[c + d*x], x]", //
 "-((Hypergeometric2F1[1, 1 + n, 2 + n, 1 + (b*Sec[c + d*x])/a]*(a + b*Sec[c + d*x])^(1 + n))/(a*d*(1 + n)))", //
3970, 67);}

// {3990, 3556}
public void test1127() {
	check(//
"Integrate[Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]], x]", //
 "(a*c*Log[Cos[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]])", //
3990, 3556);}

// {3996, 31}
public void test1128() {
	check(//
"Integrate[Sqrt[a + a*Sec[e + f*x]]/Sqrt[c - c*Sec[e + f*x]], x]", //
 "(a*Log[1 - Cos[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]])", //
3996, 31);}

// {3996, 31}
public void test1129() {
	check(//
"Integrate[Sqrt[c - c*Sec[e + f*x]]/Sqrt[a + a*Sec[e + f*x]], x]", //
 "(c*Log[1 + Cos[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]])", //
3996, 31);}

// {3990, 3556}
public void test1130() {
	check(//
"Integrate[1/(Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]]), x]", //
 "(Log[Sin[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]])", //
3990, 3556);}

// {3997, 141}
public void test1131() {
	check(//
"Integrate[(1 + Sec[e + f*x])^m*(c - c*Sec[e + f*x])^n, x]", //
 "(2^(1/2 + m)*AppellF1[1/2 + n, 1/2 - m, 1, 3/2 + n, (1 - Sec[e + f*x])/2, 1 - Sec[e + f*x]]*(c - c*Sec[e + f*x])^n*Tan[e + f*x])/(f*(1 + 2*n)*Sqrt[1 + Sec[e + f*x]])", //
3997, 141);}

// {3997, 67}
public void test1132() {
	check(//
"Integrate[Sqrt[a + a*Sec[e + f*x]]*(c - c*Sec[e + f*x])^n, x]", //
 "(2*a*Hypergeometric2F1[1, 1/2 + n, 3/2 + n, 1 - Sec[e + f*x]]*(c - c*Sec[e + f*x])^n*Tan[e + f*x])/(f*(1 + 2*n)*Sqrt[a + a*Sec[e + f*x]])", //
3997, 67);}

// {4019, 209}
public void test1133() {
	check(//
"Integrate[Sqrt[a + a*Sec[e + f*x]]/Sqrt[c + d*Sec[e + f*x]], x]", //
 "(2*Sqrt[a]*ArcTan[(Sqrt[a]*Sqrt[c]*Tan[e + f*x])/(Sqrt[a + a*Sec[e + f*x]]*Sqrt[c + d*Sec[e + f*x]])])/(Sqrt[c]*f)", //
4019, 209);}

// {4042, 3855}
public void test1134() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x]))/(c - c*Sec[e + f*x]), x]", //
 "-((a*ArcTanh[Sin[e + f*x]])/(c*f)) - (2*a*Tan[e + f*x])/(f*(c - c*Sec[e + f*x]))", //
4042, 3855);}

// {4036, 4035}
public void test1135() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x]))/(c - c*Sec[e + f*x])^3, x]", //
 "-((a + a*Sec[e + f*x])*Tan[e + f*x])/(5*f*(c - c*Sec[e + f*x])^3) - ((a + a*Sec[e + f*x])*Tan[e + f*x])/(15*c*f*(c - c*Sec[e + f*x])^2)", //
4036, 4035);}

// {4036, 4035}
public void test1136() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^2)/(c - c*Sec[e + f*x])^4, x]", //
 "-((a + a*Sec[e + f*x])^2*Tan[e + f*x])/(7*f*(c - c*Sec[e + f*x])^4) - ((a + a*Sec[e + f*x])^2*Tan[e + f*x])/(35*c*f*(c - c*Sec[e + f*x])^3)", //
4036, 4035);}

// {4036, 4035}
public void test1137() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^3)/(c - c*Sec[e + f*x])^5, x]", //
 "-((a + a*Sec[e + f*x])^3*Tan[e + f*x])/(9*f*(c - c*Sec[e + f*x])^5) - ((a + a*Sec[e + f*x])^3*Tan[e + f*x])/(63*c*f*(c - c*Sec[e + f*x])^4)", //
4036, 4035);}

// {4042, 3855}
public void test1138() {
	check(//
"Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x]))/(a + a*Sec[e + f*x]), x]", //
 "-((c*ArcTanh[Sin[e + f*x]])/(a*f)) + (2*c*Tan[e + f*x])/(f*(a + a*Sec[e + f*x]))", //
4042, 3855);}

// {4036, 4035}
public void test1139() {
	check(//
"Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x]))/(a + a*Sec[e + f*x])^3, x]", //
 "((c - c*Sec[e + f*x])*Tan[e + f*x])/(5*f*(a + a*Sec[e + f*x])^3) + ((c - c*Sec[e + f*x])*Tan[e + f*x])/(15*a*f*(a + a*Sec[e + f*x])^2)", //
4036, 4035);}

// {4040, 4038}
public void test1140() {
	check(//
"Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])*(c - c*Sec[e + f*x])^(3/2), x]", //
 "(-8*c^2*(a + a*Sec[e + f*x])*Tan[e + f*x])/(15*f*Sqrt[c - c*Sec[e + f*x]]) - (2*c*(a + a*Sec[e + f*x])*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(5*f)", //
4040, 4038);}

// {4040, 4038}
public void test1141() {
	check(//
"Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^2*(c - c*Sec[e + f*x])^(3/2), x]", //
 "(-8*c^2*(a + a*Sec[e + f*x])^2*Tan[e + f*x])/(35*f*Sqrt[c - c*Sec[e + f*x]]) - (2*c*(a + a*Sec[e + f*x])^2*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(7*f)", //
4040, 4038);}

// {4040, 4038}
public void test1142() {
	check(//
"Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^3*(c - c*Sec[e + f*x])^(3/2), x]", //
 "(-8*c^2*(a + a*Sec[e + f*x])^3*Tan[e + f*x])/(63*f*Sqrt[c - c*Sec[e + f*x]]) - (2*c*(a + a*Sec[e + f*x])^3*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(9*f)", //
4040, 4038);}

// {4039, 3877}
public void test1143() {
	check(//
"Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x])^(3/2))/(a + a*Sec[e + f*x]), x]", //
 "(4*c^2*Tan[e + f*x])/(a*f*Sqrt[c - c*Sec[e + f*x]]) + (2*c*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(f*(a + a*Sec[e + f*x]))", //
4039, 3877);}

// {4039, 4038}
public void test1144() {
	check(//
"Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x])^(3/2))/(a + a*Sec[e + f*x])^2, x]", //
 "(-4*c^2*Tan[e + f*x])/(3*f*(a^2 + a^2*Sec[e + f*x])*Sqrt[c - c*Sec[e + f*x]]) + (2*c*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(3*f*(a + a*Sec[e + f*x])^2)", //
4039, 4038);}

// {4039, 4038}
public void test1145() {
	check(//
"Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x])^(3/2))/(a + a*Sec[e + f*x])^3, x]", //
 "(-4*c^2*Tan[e + f*x])/(15*a*f*(a + a*Sec[e + f*x])^2*Sqrt[c - c*Sec[e + f*x]]) + (2*c*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(5*f*(a + a*Sec[e + f*x])^3)", //
4039, 4038);}

// {4040, 4038}
public void test1146() {
	check(//
"Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2)*(c - c*Sec[e + f*x])^(7/2), x]", //
 "(a^2*(c - c*Sec[e + f*x])^(7/2)*Tan[e + f*x])/(10*f*Sqrt[a + a*Sec[e + f*x]]) + (a*Sqrt[a + a*Sec[e + f*x]]*(c - c*Sec[e + f*x])^(7/2)*Tan[e + f*x])/(5*f)", //
4040, 4038);}

// {4040, 4038}
public void test1147() {
	check(//
"Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2)*(c - c*Sec[e + f*x])^(5/2), x]", //
 "(a^2*(c - c*Sec[e + f*x])^(5/2)*Tan[e + f*x])/(6*f*Sqrt[a + a*Sec[e + f*x]]) + (a*Sqrt[a + a*Sec[e + f*x]]*(c - c*Sec[e + f*x])^(5/2)*Tan[e + f*x])/(4*f)", //
4040, 4038);}

// {4040, 4038}
public void test1148() {
	check(//
"Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2)*(c - c*Sec[e + f*x])^(3/2), x]", //
 "-(c^2*(a + a*Sec[e + f*x])^(3/2)*Tan[e + f*x])/(3*f*Sqrt[c - c*Sec[e + f*x]]) - (c*(a + a*Sec[e + f*x])^(3/2)*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(3*f)", //
4040, 4038);}

// {4040, 4037}
public void test1149() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2))/Sqrt[c - c*Sec[e + f*x]], x]", //
 "(2*a^2*Log[1 - Sec[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]]) + (a*Sqrt[a + a*Sec[e + f*x]]*Tan[e + f*x])/(f*Sqrt[c - c*Sec[e + f*x]])", //
4040, 4037);}

// {4039, 4037}
public void test1150() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2))/(c - c*Sec[e + f*x])^(3/2), x]", //
 "-((a*Sqrt[a + a*Sec[e + f*x]]*Tan[e + f*x])/(f*(c - c*Sec[e + f*x])^(3/2))) - (a^2*Log[1 - Sec[e + f*x]]*Tan[e + f*x])/(c*f*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]])", //
4039, 4037);}

// {4036, 4035}
public void test1151() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2))/(c - c*Sec[e + f*x])^(7/2), x]", //
 "-((a + a*Sec[e + f*x])^(3/2)*Tan[e + f*x])/(6*f*(c - c*Sec[e + f*x])^(7/2)) - ((a + a*Sec[e + f*x])^(3/2)*Tan[e + f*x])/(24*c*f*(c - c*Sec[e + f*x])^(5/2))", //
4036, 4035);}

// {4039, 4038}
public void test1152() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2))/(c - c*Sec[e + f*x])^(9/2), x]", //
 "-(a*Sqrt[a + a*Sec[e + f*x]]*Tan[e + f*x])/(4*f*(c - c*Sec[e + f*x])^(9/2)) + (a^2*Tan[e + f*x])/(12*c*f*Sqrt[a + a*Sec[e + f*x]]*(c - c*Sec[e + f*x])^(7/2))", //
4039, 4038);}

// {4039, 4038}
public void test1153() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^(3/2))/(c - c*Sec[e + f*x])^(11/2), x]", //
 "-(a*Sqrt[a + a*Sec[e + f*x]]*Tan[e + f*x])/(5*f*(c - c*Sec[e + f*x])^(11/2)) + (a^2*Tan[e + f*x])/(20*c*f*Sqrt[a + a*Sec[e + f*x]]*(c - c*Sec[e + f*x])^(9/2))", //
4039, 4038);}

// {4040, 4038}
public void test1154() {
	check(//
"Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^(5/2)*(c - c*Sec[e + f*x])^(3/2), x]", //
 "-(c^2*(a + a*Sec[e + f*x])^(5/2)*Tan[e + f*x])/(6*f*Sqrt[c - c*Sec[e + f*x]]) - (c*(a + a*Sec[e + f*x])^(5/2)*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(4*f)", //
4040, 4038);}

// {4036, 4035}
public void test1155() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^(5/2))/(c - c*Sec[e + f*x])^(9/2), x]", //
 "-((a + a*Sec[e + f*x])^(5/2)*Tan[e + f*x])/(8*f*(c - c*Sec[e + f*x])^(9/2)) - ((a + a*Sec[e + f*x])^(5/2)*Tan[e + f*x])/(48*c*f*(c - c*Sec[e + f*x])^(7/2))", //
4036, 4035);}

// {4040, 4037}
public void test1156() {
	check(//
"Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x])^(3/2))/Sqrt[a + a*Sec[e + f*x]], x]", //
 "(-2*c^2*Log[1 + Sec[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]]) - (c*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a + a*Sec[e + f*x]])", //
4040, 4037);}

// {4044, 3855}
public void test1157() {
	check(//
"Integrate[Sec[e + f*x]/(Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]]), x]", //
 "-((ArcTanh[Cos[e + f*x]]*Tan[e + f*x])/(f*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]]))", //
4044, 3855);}

// {4039, 4037}
public void test1158() {
	check(//
"Integrate[(Sec[e + f*x]*(c - c*Sec[e + f*x])^(3/2))/(a + a*Sec[e + f*x])^(3/2), x]", //
 "(c^2*Log[1 + Sec[e + f*x]]*Tan[e + f*x])/(a*f*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c - c*Sec[e + f*x]]) + (c*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(f*(a + a*Sec[e + f*x])^(3/2))", //
4039, 4037);}

// {4040, 4038}
public void test1159() {
	check(//
"Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^m*(c - c*Sec[e + f*x])^(3/2), x]", //
 "(-8*c^2*(a + a*Sec[e + f*x])^m*Tan[e + f*x])/(f*(3 + 8*m + 4*m^2)*Sqrt[c - c*Sec[e + f*x]]) - (2*c*(a + a*Sec[e + f*x])^m*Sqrt[c - c*Sec[e + f*x]]*Tan[e + f*x])/(f*(3 + 2*m))", //
4040, 4038);}

// {4046, 70}
public void test1160() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^m)/Sqrt[c - c*Sec[e + f*x]], x]", //
 "-((Hypergeometric2F1[1, 1/2 + m, 3/2 + m, (1 + Sec[e + f*x])/2]*(a + a*Sec[e + f*x])^m*Tan[e + f*x])/(f*(1 + 2*m)*Sqrt[c - c*Sec[e + f*x]]))", //
4046, 70);}

// {4046, 70}
public void test1161() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^m)/(c - c*Sec[e + f*x])^(3/2), x]", //
 "-(Hypergeometric2F1[2, 1/2 + m, 3/2 + m, (1 + Sec[e + f*x])/2]*(a + a*Sec[e + f*x])^m*Tan[e + f*x])/(2*c*f*(1 + 2*m)*Sqrt[c - c*Sec[e + f*x]])", //
4046, 70);}

// {4046, 70}
public void test1162() {
	check(//
"Integrate[(Sec[e + f*x]*(a + a*Sec[e + f*x])^m)/(c - c*Sec[e + f*x])^(5/2), x]", //
 "-(Hypergeometric2F1[3, 1/2 + m, 3/2 + m, (1 + Sec[e + f*x])/2]*(a + a*Sec[e + f*x])^m*Tan[e + f*x])/(4*c^2*f*(1 + 2*m)*Sqrt[c - c*Sec[e + f*x]])", //
4046, 70);}

// {4036, 4035}
public void test1163() {
	check(//
"Integrate[Sec[e + f*x]*(a + a*Sec[e + f*x])^m*(c - c*Sec[e + f*x])^(-2 - m), x]", //
 "-(((a + a*Sec[e + f*x])^m*(c - c*Sec[e + f*x])^(-2 - m)*Tan[e + f*x])/(f*(1 + 2*m))) + ((a + a*Sec[e + f*x])^(1 + m)*(c - c*Sec[e + f*x])^(-2 - m)*Tan[e + f*x])/(a*f*(3 + 8*m + 4*m^2))", //
4036, 4035);}

// {4047, 2697}
public void test1164() {
	check(//
"Integrate[(g*Sec[e + f*x])^p*(a + a*Sec[e + f*x])*(c - c*Sec[e + f*x]), x]", //
 "-(a*c*(Cos[e + f*x]^2)^((3 + p)/2)*Hypergeometric2F1[3/2, (3 + p)/2, 5/2, Sin[e + f*x]^2]*(g*Sec[e + f*x])^p*Tan[e + f*x]^3)/(3*f)", //
4047, 2697);}

// {4052, 214}
public void test1165() {
	check(//
"Integrate[(Sec[e + f*x]*Sqrt[a + a*Sec[e + f*x]])/(c - d*Sec[e + f*x]), x]", //
 "(2*Sqrt[a]*ArcTanh[(Sqrt[a]*Sqrt[d]*Tan[e + f*x])/(Sqrt[c - d]*Sqrt[a + a*Sec[e + f*x]])])/(Sqrt[c - d]*Sqrt[d]*f)", //
4052, 214);}

// {4085, 3879}
public void test1166() {
	check(//
"Integrate[(Sec[e + f*x]*(c + d*Sec[e + f*x]))/(a + a*Sec[e + f*x])^2, x]", //
 "((c - d)*Tan[e + f*x])/(3*f*(a + a*Sec[e + f*x])^2) + ((c + 2*d)*Tan[e + f*x])/(3*f*(a^2 + a^2*Sec[e + f*x]))", //
4085, 3879);}

// {4065, 212}
public void test1167() {
	check(//
"Integrate[(Sec[e + f*x]*Sqrt[a + a*Sec[e + f*x]])/Sqrt[c + d*Sec[e + f*x]], x]", //
 "(2*Sqrt[a]*ArcTanh[(Sqrt[a]*Sqrt[d]*Tan[e + f*x])/(Sqrt[a + a*Sec[e + f*x]]*Sqrt[c + d*Sec[e + f*x]])])/(Sqrt[d]*f)", //
4065, 212);}

// {4068, 209}
public void test1168() {
	check(//
"Integrate[Sec[e + f*x]/(Sqrt[a + a*Sec[e + f*x]]*Sqrt[c + d*Sec[e + f*x]]), x]", //
 "(Sqrt[2]*ArcTan[(Sqrt[a]*Sqrt[c - d]*Tan[e + f*x])/(Sqrt[2]*Sqrt[a + a*Sec[e + f*x]]*Sqrt[c + d*Sec[e + f*x]])])/(Sqrt[a]*Sqrt[c - d]*f)", //
4068, 209);}

// {4052, 211}
public void test1169() {
	check(//
"Integrate[(Sec[e + f*x]*Sqrt[a + a*Sec[e + f*x]])/(c + d*Sec[e + f*x]), x]", //
 "(2*Sqrt[a]*ArcTan[(Sqrt[a]*Sqrt[d]*Tan[e + f*x])/(Sqrt[c + d]*Sqrt[a + a*Sec[e + f*x]])])/(Sqrt[d]*Sqrt[c + d]*f)", //
4052, 211);}

// {4004, 3879}
public void test1170() {
	check(//
"Integrate[(A + B*Sec[c + d*x])/(a + a*Sec[c + d*x]), x]", //
 "(A*x)/a - ((A - B)*Tan[c + d*x])/(d*(a + a*Sec[c + d*x]))", //
4004, 3879);}

// {4085, 3879}
public void test1171() {
	check(//
"Integrate[(Sec[c + d*x]*(A + B*Sec[c + d*x]))/(a + a*Sec[c + d*x])^2, x]", //
 "((A - B)*Tan[c + d*x])/(3*d*(a + a*Sec[c + d*x])^2) + ((A + 2*B)*Tan[c + d*x])/(3*d*(a^2 + a^2*Sec[c + d*x]))", //
4085, 3879);}

// {4086, 3877}
public void test1172() {
	check(//
"Integrate[Sec[c + d*x]*Sqrt[a + a*Sec[c + d*x]]*(A + B*Sec[c + d*x]), x]", //
 "(2*a*(3*A + B)*Tan[c + d*x])/(3*d*Sqrt[a + a*Sec[c + d*x]]) + (2*B*Sqrt[a + a*Sec[c + d*x]]*Tan[c + d*x])/(3*d)", //
4086, 3877);}

// {4098, 3889}
public void test1173() {
	check(//
"Integrate[(Sqrt[a + a*Sec[c + d*x]]*(A + B*Sec[c + d*x]))/Sec[c + d*x]^(3/2), x]", //
 "(2*a*(A + 3*B)*Sqrt[Sec[c + d*x]]*Sin[c + d*x])/(3*d*Sqrt[a + a*Sec[c + d*x]]) + (2*A*Sqrt[a + a*Sec[c + d*x]]*Sin[c + d*x])/(3*d*Sqrt[Sec[c + d*x]])", //
4098, 3889);}

// {21, 8}
public void test1174() {
	check(//
"Integrate[((a*B)/b + B*Sec[c + d*x])/(a + b*Sec[c + d*x]), x]", //
 "(B*x)/b", //
21, 8);}

// {4131, 3855}
public void test1175() {
	check(//
"Integrate[Sec[c + d*x]*(A + C*Sec[c + d*x]^2), x]", //
 "((2*A + C)*ArcTanh[Sin[c + d*x]])/(2*d) + (C*Sec[c + d*x]*Tan[c + d*x])/(2*d)", //
4131, 3855);}

// {4130, 3855}
public void test1176() {
	check(//
"Integrate[Cos[c + d*x]*(A + C*Sec[c + d*x]^2), x]", //
 "(C*ArcTanh[Sin[c + d*x]])/d + (A*Sin[c + d*x])/d", //
4130, 3855);}

// {4130, 8}
public void test1177() {
	check(//
"Integrate[Cos[c + d*x]^2*(A + C*Sec[c + d*x]^2), x]", //
 "((A + 2*C)*x)/2 + (A*Cos[c + d*x]*Sin[c + d*x])/(2*d)", //
4130, 8);}

// {3852, 8}
public void test1178() {
	check(//
"Integrate[-Sec[e + f*x]^2, x]", //
 "-(Tan[e + f*x]/f)", //
3852, 8);}

// {4219, 270}
public void test1179() {
	check(//
"Integrate[Sin[e + f*x]/Sqrt[a + b*Sec[e + f*x]^2], x]", //
 "-((Cos[e + f*x]*Sqrt[a + b*Sec[e + f*x]^2])/(a*f))", //
4219, 270);}

// {4217, 270}
public void test1180() {
	check(//
"Integrate[Csc[e + f*x]^2/Sqrt[a + b*Sec[e + f*x]^2], x]", //
 "-((Cot[e + f*x]*Sqrt[a + b + b*Tan[e + f*x]^2])/((a + b)*f))", //
4217, 270);}

// {4131, 3855}
public void test1181() {
	check(//
"Integrate[Sec[e + f*x]*(a + b*Sec[e + f*x]^2), x]", //
 "((2*a + b)*ArcTanh[Sin[e + f*x]])/(2*f) + (b*Sec[e + f*x]*Tan[e + f*x])/(2*f)", //
4131, 3855);}

// {4130, 3855}
public void test1182() {
	check(//
"Integrate[Cos[e + f*x]*(a + b*Sec[e + f*x]^2), x]", //
 "(b*ArcTanh[Sin[e + f*x]])/f + (a*Sin[e + f*x])/f", //
4130, 3855);}

// {4130, 8}
public void test1183() {
	check(//
"Integrate[Cos[e + f*x]^2*(a + b*Sec[e + f*x]^2), x]", //
 "((a + 2*b)*x)/2 + (a*Cos[e + f*x]*Sin[e + f*x])/(2*f)", //
4130, 8);}

// {4232, 214}
public void test1184() {
	check(//
"Integrate[Sec[e + f*x]/(a + b*Sec[e + f*x]^2), x]", //
 "ArcTanh[(Sqrt[a]*Sin[e + f*x])/Sqrt[a + b]]/(Sqrt[a]*Sqrt[a + b]*f)", //
4232, 214);}

// {4231, 211}
public void test1185() {
	check(//
"Integrate[Sec[e + f*x]^2/(a + b*Sec[e + f*x]^2), x]", //
 "ArcTan[(Sqrt[b]*Tan[e + f*x])/Sqrt[a + b]]/(Sqrt[b]*Sqrt[a + b]*f)", //
4231, 211);}

// {4231, 197}
public void test1186() {
	check(//
"Integrate[Sec[e + f*x]^2/(a + b*Sec[e + f*x]^2)^(3/2), x]", //
 "Tan[e + f*x]/((a + b)*f*Sqrt[a + b + b*Tan[e + f*x]^2])", //
4231, 197);}

// {4223, 266}
public void test1187() {
	check(//
"Integrate[Tan[e + f*x]/(a + b*Sec[e + f*x]^2), x]", //
 "-Log[b + a*Cos[e + f*x]^2]/(2*a*f)", //
4223, 266);}

// {4223, 266}
public void test1188() {
	check(//
"Integrate[Tan[e + f*x]/(a + b*Sec[e + f*x]^3), x]", //
 "-Log[b + a*Cos[e + f*x]^3]/(3*a*f)", //
4223, 266);}

// {3852, 8}
public void test1189() {
	check(//
"Integrate[Csc[a + b*x]^2, x]", //
 "-(Cot[a + b*x]/b)", //
3852, 8);}

// {3853, 3855}
public void test1190() {
	check(//
"Integrate[Csc[a + b*x]^3, x]", //
 "-ArcTanh[Cos[a + b*x]]/(2*b) - (Cot[a + b*x]*Csc[a + b*x])/(2*b)", //
3853, 3855);}

// {3852}
public void test1191() {
	check(//
"Integrate[Csc[a + b*x]^4, x]", //
 "-(Cot[a + b*x]/b) - Cot[a + b*x]^3/(3*b)", //
3852);}

// {3852}
public void test1192() {
	check(//
"Integrate[Csc[a + b*x]^6, x]", //
 "-(Cot[a + b*x]/b) - (2*Cot[a + b*x]^3)/(3*b) - Cot[a + b*x]^5/(5*b)", //
3852);}

// {3852}
public void test1193() {
	check(//
"Integrate[Csc[a + b*x]^8, x]", //
 "-(Cot[a + b*x]/b) - Cot[a + b*x]^3/b - (3*Cot[a + b*x]^5)/(5*b) - Cot[a + b*x]^7/(7*b)", //
3852);}

// {3856, 2720}
public void test1194() {
	check(//
"Integrate[Sqrt[Csc[a + b*x]], x]", //
 "(2*Sqrt[Csc[a + b*x]]*EllipticF[(a - Pi/2 + b*x)/2, 2]*Sqrt[Sin[a + b*x]])/b", //
3856, 2720);}

// {3856, 2719}
public void test1195() {
	check(//
"Integrate[1/Sqrt[Csc[a + b*x]], x]", //
 "(2*Sqrt[Csc[a + b*x]]*EllipticE[(a - Pi/2 + b*x)/2, 2]*Sqrt[Sin[a + b*x]])/b", //
3856, 2719);}

// {3856, 2720}
public void test1196() {
	check(//
"Integrate[Sqrt[c*Csc[a + b*x]], x]", //
 "(2*Sqrt[c*Csc[a + b*x]]*EllipticF[(a - Pi/2 + b*x)/2, 2]*Sqrt[Sin[a + b*x]])/b", //
3856, 2720);}

// {3856, 2719}
public void test1197() {
	check(//
"Integrate[1/Sqrt[c*Csc[a + b*x]], x]", //
 "(2*EllipticE[(a - Pi/2 + b*x)/2, 2])/(b*Sqrt[c*Csc[a + b*x]]*Sqrt[Sin[a + b*x]])", //
3856, 2719);}

// {3857, 2722}
public void test1198() {
	check(//
"Integrate[Csc[a + b*x]^(4/3), x]", //
 "(-3*Cos[a + b*x]*Csc[a + b*x]^(1/3)*Hypergeometric2F1[-1/6, 1/2, 5/6, Sin[a + b*x]^2])/(b*Sqrt[Cos[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1199() {
	check(//
"Integrate[Csc[a + b*x]^(2/3), x]", //
 "(3*Cos[a + b*x]*Hypergeometric2F1[1/6, 1/2, 7/6, Sin[a + b*x]^2])/(b*Sqrt[Cos[a + b*x]^2]*Csc[a + b*x]^(1/3))", //
3857, 2722);}

// {3857, 2722}
public void test1200() {
	check(//
"Integrate[Csc[a + b*x]^(1/3), x]", //
 "(3*Cos[a + b*x]*Hypergeometric2F1[1/3, 1/2, 4/3, Sin[a + b*x]^2])/(2*b*Sqrt[Cos[a + b*x]^2]*Csc[a + b*x]^(2/3))", //
3857, 2722);}

// {3857, 2722}
public void test1201() {
	check(//
"Integrate[Csc[a + b*x]^(-1/3), x]", //
 "(3*Cos[a + b*x]*Hypergeometric2F1[1/2, 2/3, 5/3, Sin[a + b*x]^2])/(4*b*Sqrt[Cos[a + b*x]^2]*Csc[a + b*x]^(4/3))", //
3857, 2722);}

// {3857, 2722}
public void test1202() {
	check(//
"Integrate[Csc[a + b*x]^(-2/3), x]", //
 "(3*Cos[a + b*x]*Hypergeometric2F1[1/2, 5/6, 11/6, Sin[a + b*x]^2])/(5*b*Sqrt[Cos[a + b*x]^2]*Csc[a + b*x]^(5/3))", //
3857, 2722);}

// {3857, 2722}
public void test1203() {
	check(//
"Integrate[Csc[a + b*x]^(-4/3), x]", //
 "(3*Cos[a + b*x]*Hypergeometric2F1[1/2, 7/6, 13/6, Sin[a + b*x]^2])/(7*b*Sqrt[Cos[a + b*x]^2]*Csc[a + b*x]^(7/3))", //
3857, 2722);}

// {3857, 2722}
public void test1204() {
	check(//
"Integrate[(c*Csc[a + b*x])^(4/3), x]", //
 "(-3*c*Cos[a + b*x]*(c*Csc[a + b*x])^(1/3)*Hypergeometric2F1[-1/6, 1/2, 5/6, Sin[a + b*x]^2])/(b*Sqrt[Cos[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1205() {
	check(//
"Integrate[(c*Csc[a + b*x])^(2/3), x]", //
 "(3*Cos[a + b*x]*(c*Csc[a + b*x])^(2/3)*Hypergeometric2F1[1/6, 1/2, 7/6, Sin[a + b*x]^2]*Sin[a + b*x])/(b*Sqrt[Cos[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1206() {
	check(//
"Integrate[(c*Csc[a + b*x])^(1/3), x]", //
 "(3*Cos[a + b*x]*(c*Csc[a + b*x])^(1/3)*Hypergeometric2F1[1/3, 1/2, 4/3, Sin[a + b*x]^2]*Sin[a + b*x])/(2*b*Sqrt[Cos[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1207() {
	check(//
"Integrate[(c*Csc[a + b*x])^(-1/3), x]", //
 "(3*Cos[a + b*x]*(c*Csc[a + b*x])^(2/3)*Hypergeometric2F1[1/2, 2/3, 5/3, Sin[a + b*x]^2]*Sin[a + b*x]^2)/(4*b*c*Sqrt[Cos[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1208() {
	check(//
"Integrate[(c*Csc[a + b*x])^(-2/3), x]", //
 "(3*Cos[a + b*x]*(c*Csc[a + b*x])^(1/3)*Hypergeometric2F1[1/2, 5/6, 11/6, Sin[a + b*x]^2]*Sin[a + b*x]^2)/(5*b*c*Sqrt[Cos[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1209() {
	check(//
"Integrate[(c*Csc[a + b*x])^(-4/3), x]", //
 "(3*Cos[a + b*x]*(c*Csc[a + b*x])^(2/3)*Hypergeometric2F1[1/2, 7/6, 13/6, Sin[a + b*x]^2]*Sin[a + b*x]^3)/(7*b*c^2*Sqrt[Cos[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1210() {
	check(//
"Integrate[Csc[a + b*x]^n, x]", //
 "(Cos[a + b*x]*Csc[a + b*x]^(-1 + n)*Hypergeometric2F1[1/2, (1 - n)/2, (3 - n)/2, Sin[a + b*x]^2])/(b*(1 - n)*Sqrt[Cos[a + b*x]^2])", //
3857, 2722);}

// {3857, 2722}
public void test1211() {
	check(//
"Integrate[(c*Csc[a + b*x])^n, x]", //
 "(Cos[a + b*x]*(c*Csc[a + b*x])^n*Hypergeometric2F1[1/2, (1 - n)/2, (3 - n)/2, Sin[a + b*x]^2]*Sin[a + b*x])/(b*(1 - n)*Sqrt[Cos[a + b*x]^2])", //
3857, 2722);}

// {4207, 221}
public void test1212() {
	check(//
"Integrate[Sqrt[Csc[x]^2], x]", //
 "-ArcSinh[Cot[x]]", //
4207, 221);}

// {4207, 197}
public void test1213() {
	check(//
"Integrate[1/Sqrt[Csc[x]^2], x]", //
 "-(Cot[x]/Sqrt[Csc[x]^2])", //
4207, 197);}

// {4207, 197}
public void test1214() {
	check(//
"Integrate[1/Sqrt[a*Csc[x]^2], x]", //
 "-(Cot[x]/Sqrt[a*Csc[x]^2])", //
4207, 197);}

// {3862, 8}
public void test1225() {
	check(//
"Integrate[(a + a*Csc[c + d*x])^(-1), x]", //
 "x/a + Cot[c + d*x]/(d*(a + a*Csc[c + d*x]))", //
3862, 8);}

// {3859, 209}
public void test1226() {
	check(//
"Integrate[Sqrt[a + a*Csc[x]], x]", //
 "-2*Sqrt[a]*ArcTan[(Sqrt[a]*Cot[x])/Sqrt[a + a*Csc[x]]]", //
3859, 209);}

// {3886, 221}
public void test1227() {
	check(//
"Integrate[Sqrt[Csc[e + f*x]]*Sqrt[a + a*Csc[e + f*x]], x]", //
 "(-2*Sqrt[a]*ArcSinh[(Sqrt[a]*Cot[e + f*x])/Sqrt[a + a*Csc[e + f*x]]])/f", //
3886, 221);}

// {3886, 221}
public void test1228() {
	check(//
"Integrate[Sqrt[-Csc[e + f*x]]*Sqrt[a - a*Csc[e + f*x]], x]", //
 "(-2*Sqrt[a]*ArcSinh[(Sqrt[a]*Cot[e + f*x])/Sqrt[a - a*Csc[e + f*x]]])/f", //
3886, 221);}

// {3891, 67}
public void test1229() {
	check(//
"Integrate[Csc[c + d*x]^n*Sqrt[a + a*Csc[c + d*x]], x]", //
 "(-2*a*Cot[c + d*x]*Hypergeometric2F1[1/2, 1 - n, 3/2, 1 - Csc[c + d*x]])/(d*Sqrt[a + a*Csc[c + d*x]])", //
3891, 67);}

// {3868, 2736}
public void test1230() {
	check(//
"Integrate[(3 + 5*Csc[c + d*x])^(-1), x]", //
 "-x/12 - (5*ArcTan[Cos[c + d*x]/(3 + Sin[c + d*x])])/(6*d)", //
3868, 2736);}

// {3964, 31}
public void test1231() {
	check(//
"Integrate[Cot[x]/(a + a*Csc[x]), x]", //
 "Log[1 + Sin[x]]/a", //
3964, 31);}

// {4373, 3855}
public void test1232() {
	check(//
"Integrate[Csc[2*a + 2*b*x]*Sin[a + b*x], x]", //
 "ArcTanh[Sin[a + b*x]]/(2*b)", //
4373, 3855);}

// {4373, 3556}
public void test1233() {
	check(//
"Integrate[Csc[2*a + 2*b*x]*Sin[a + b*x]^2, x]", //
 "-Log[Cos[a + b*x]]/(2*b)", //
4373, 3556);}

// {4373, 2717}
public void test1234() {
	check(//
"Integrate[Csc[a + b*x]*Sin[2*a + 2*b*x], x]", //
 "(2*Sin[a + b*x])/b", //
4373, 2717);}

// {4373, 3556}
public void test1235() {
	check(//
"Integrate[Csc[a + b*x]^2*Sin[2*a + 2*b*x], x]", //
 "(2*Log[Sin[a + b*x]])/b", //
4373, 3556);}

// {4387, 4390}
public void test1236() {
	check(//
"Integrate[Sin[a + b*x]*Sqrt[Sin[2*a + 2*b*x]], x]", //
 "-ArcSin[Cos[a + b*x] - Sin[a + b*x]]/(4*b) + Log[Cos[a + b*x] + Sin[a + b*x] + Sqrt[Sin[2*a + 2*b*x]]]/(4*b) - (Cos[a + b*x]*Sqrt[Sin[2*a + 2*b*x]])/(2*b)", //
4387, 4390);}

// {4389, 4376}
public void test1237() {
	check(//
"Integrate[Sin[a + b*x]/Sin[2*a + 2*b*x]^(5/2), x]", //
 "Sin[a + b*x]/(3*b*Sin[2*a + 2*b*x]^(3/2)) - (2*Cos[a + b*x])/(3*b*Sqrt[Sin[2*a + 2*b*x]])", //
4389, 4376);}

// {4383, 2719}
public void test1238() {
	check(//
"Integrate[Sin[a + b*x]^2*Sqrt[Sin[2*a + 2*b*x]], x]", //
 "EllipticE[a - Pi/4 + b*x, 2]/(2*b) - Sin[2*a + 2*b*x]^(3/2)/(6*b)", //
4383, 2719);}

// {4383, 2720}
public void test1239() {
	check(//
"Integrate[Sin[a + b*x]^2/Sqrt[Sin[2*a + 2*b*x]], x]", //
 "EllipticF[a - Pi/4 + b*x, 2]/(2*b) - Sqrt[Sin[2*a + 2*b*x]]/(2*b)", //
4383, 2720);}

// {4381, 2719}
public void test1240() {
	check(//
"Integrate[Sin[a + b*x]^2/Sin[2*a + 2*b*x]^(3/2), x]", //
 "-EllipticE[a - Pi/4 + b*x, 2]/(2*b) + Sin[a + b*x]^2/(b*Sqrt[Sin[2*a + 2*b*x]])", //
4381, 2719);}

// {4381, 2720}
public void test1241() {
	check(//
"Integrate[Sin[a + b*x]^2/Sin[2*a + 2*b*x]^(5/2), x]", //
 "EllipticF[a - Pi/4 + b*x, 2]/(6*b) + Sin[a + b*x]^2/(3*b*Sin[2*a + 2*b*x]^(3/2))", //
4381, 2720);}

// {4383, 4391}
public void test1242() {
	check(//
"Integrate[Sin[a + b*x]^3/Sqrt[Sin[2*a + 2*b*x]], x]", //
 "(-3*ArcSin[Cos[a + b*x] - Sin[a + b*x]])/(8*b) - (3*Log[Cos[a + b*x] + Sin[a + b*x] + Sqrt[Sin[2*a + 2*b*x]]])/(8*b) - (Sin[a + b*x]*Sqrt[Sin[2*a + 2*b*x]])/(4*b)", //
4383, 4391);}

// {4381, 4377}
public void test1243() {
	check(//
"Integrate[Sin[a + b*x]^3/Sin[2*a + 2*b*x]^(7/2), x]", //
 "Sin[a + b*x]^3/(5*b*Sin[2*a + 2*b*x]^(5/2)) + Sin[a + b*x]/(5*b*Sqrt[Sin[2*a + 2*b*x]])", //
4381, 4377);}

// {4393, 4390}
public void test1244() {
	check(//
"Integrate[Csc[a + b*x]*Sqrt[Sin[2*a + 2*b*x]], x]", //
 "-(ArcSin[Cos[a + b*x] - Sin[a + b*x]]/b) + Log[Cos[a + b*x] + Sin[a + b*x] + Sqrt[Sin[2*a + 2*b*x]]]/b", //
4393, 4390);}

// {4385, 2719}
public void test1245() {
	check(//
"Integrate[Csc[a + b*x]^2*Sqrt[Sin[2*a + 2*b*x]], x]", //
 "(-2*EllipticE[a - Pi/4 + b*x, 2])/b - (Csc[a + b*x]^2*Sin[2*a + 2*b*x]^(3/2))/b", //
4385, 2719);}

// {4385, 2720}
public void test1246() {
	check(//
"Integrate[Csc[a + b*x]^2/Sqrt[Sin[2*a + 2*b*x]], x]", //
 "(2*EllipticF[a - Pi/4 + b*x, 2])/(3*b) - (Csc[a + b*x]^2*Sqrt[Sin[2*a + 2*b*x]])/(3*b)", //
4385, 2720);}

// {4385, 4377}
public void test1247() {
	check(//
"Integrate[Csc[a + b*x]^3/Sqrt[Sin[2*a + 2*b*x]], x]", //
 "(-4*Csc[a + b*x]*Sqrt[Sin[2*a + 2*b*x]])/(5*b) - (Csc[a + b*x]^3*Sqrt[Sin[2*a + 2*b*x]])/(5*b)", //
4385, 4377);}

// {4395, 2657}
public void test1248() {
	check(//
"Integrate[Sin[a + b*x]^3*Sin[2*a + 2*b*x]^m, x]", //
 "((Cos[a + b*x]^2)^((1 - m)/2)*Hypergeometric2F1[(1 - m)/2, (4 + m)/2, (6 + m)/2, Sin[a + b*x]^2]*Sin[a + b*x]^3*Sin[2*a + 2*b*x]^m*Tan[a + b*x])/(b*(4 + m))", //
4395, 2657);}

// {4395, 2657}
public void test1249() {
	check(//
"Integrate[Sin[a + b*x]^2*Sin[2*a + 2*b*x]^m, x]", //
 "((Cos[a + b*x]^2)^((1 - m)/2)*Hypergeometric2F1[(1 - m)/2, (3 + m)/2, (5 + m)/2, Sin[a + b*x]^2]*Sin[a + b*x]^2*Sin[2*a + 2*b*x]^m*Tan[a + b*x])/(b*(3 + m))", //
4395, 2657);}

// {4395, 2657}
public void test1250() {
	check(//
"Integrate[Sin[a + b*x]*Sin[2*a + 2*b*x]^m, x]", //
 "((Cos[a + b*x]^2)^((1 - m)/2)*Hypergeometric2F1[(1 - m)/2, (2 + m)/2, (4 + m)/2, Sin[a + b*x]^2]*Sin[a + b*x]*Sin[2*a + 2*b*x]^m*Tan[a + b*x])/(b*(2 + m))", //
4395, 2657);}

// {4395, 2657}
public void test1251() {
	check(//
"Integrate[Csc[a + b*x]*Sin[2*a + 2*b*x]^m, x]", //
 "((Cos[a + b*x]^2)^((1 - m)/2)*Hypergeometric2F1[(1 - m)/2, m/2, (2 + m)/2, Sin[a + b*x]^2]*Sec[a + b*x]*Sin[2*a + 2*b*x]^m)/(b*m)", //
4395, 2657);}

// {4395, 2657}
public void test1252() {
	check(//
"Integrate[Csc[a + b*x]^2*Sin[2*a + 2*b*x]^m, x]", //
 "-(((Cos[a + b*x]^2)^((1 - m)/2)*Csc[a + b*x]*Hypergeometric2F1[(1 - m)/2, (-1 + m)/2, (1 + m)/2, Sin[a + b*x]^2]*Sec[a + b*x]*Sin[2*a + 2*b*x]^m)/(b*(1 - m)))", //
4395, 2657);}

// {4395, 2657}
public void test1253() {
	check(//
"Integrate[Csc[a + b*x]^3*Sin[2*a + 2*b*x]^m, x]", //
 "-(((Cos[a + b*x]^2)^((1 - m)/2)*Csc[a + b*x]^2*Hypergeometric2F1[(1 - m)/2, (-2 + m)/2, m/2, Sin[a + b*x]^2]*Sec[a + b*x]*Sin[2*a + 2*b*x]^m)/(b*(2 - m)))", //
4395, 2657);}

// {4372, 3855}
public void test1254() {
	check(//
"Integrate[Cos[a + b*x]*Csc[2*a + 2*b*x], x]", //
 "-ArcTanh[Cos[a + b*x]]/(2*b)", //
4372, 3855);}

// {4372, 3556}
public void test1255() {
	check(//
"Integrate[Cos[a + b*x]^2*Csc[2*a + 2*b*x], x]", //
 "Log[Sin[a + b*x]]/(2*b)", //
4372, 3556);}

// {4386, 4391}
public void test1256() {
	check(//
"Integrate[Cos[a + b*x]*Sqrt[Sin[2*a + 2*b*x]], x]", //
 "-ArcSin[Cos[a + b*x] - Sin[a + b*x]]/(4*b) - Log[Cos[a + b*x] + Sin[a + b*x] + Sqrt[Sin[2*a + 2*b*x]]]/(4*b) + (Sin[a + b*x]*Sqrt[Sin[2*a + 2*b*x]])/(2*b)", //
4386, 4391);}

// {4388, 4377}
public void test1257() {
	check(//
"Integrate[Cos[a + b*x]/Sin[2*a + 2*b*x]^(5/2), x]", //
 "-Cos[a + b*x]/(3*b*Sin[2*a + 2*b*x]^(3/2)) + (2*Sin[a + b*x])/(3*b*Sqrt[Sin[2*a + 2*b*x]])", //
4388, 4377);}

// {4382, 2719}
public void test1258() {
	check(//
"Integrate[Cos[a + b*x]^2*Sqrt[Sin[2*a + 2*b*x]], x]", //
 "EllipticE[a - Pi/4 + b*x, 2]/(2*b) + Sin[2*a + 2*b*x]^(3/2)/(6*b)", //
4382, 2719);}

// {4382, 2720}
public void test1259() {
	check(//
"Integrate[Cos[a + b*x]^2/Sqrt[Sin[2*a + 2*b*x]], x]", //
 "EllipticF[a - Pi/4 + b*x, 2]/(2*b) + Sqrt[Sin[2*a + 2*b*x]]/(2*b)", //
4382, 2720);}

// {4380, 2719}
public void test1260() {
	check(//
"Integrate[Cos[a + b*x]^2/Sin[2*a + 2*b*x]^(3/2), x]", //
 "-EllipticE[a - Pi/4 + b*x, 2]/(2*b) - Cos[a + b*x]^2/(b*Sqrt[Sin[2*a + 2*b*x]])", //
4380, 2719);}

// {4380, 2720}
public void test1261() {
	check(//
"Integrate[Cos[a + b*x]^2/Sin[2*a + 2*b*x]^(5/2), x]", //
 "EllipticF[a - Pi/4 + b*x, 2]/(6*b) - Cos[a + b*x]^2/(3*b*Sin[2*a + 2*b*x]^(3/2))", //
4380, 2720);}

// {4382, 4390}
public void test1262() {
	check(//
"Integrate[Cos[a + b*x]^3/Sqrt[Sin[2*a + 2*b*x]], x]", //
 "(-3*ArcSin[Cos[a + b*x] - Sin[a + b*x]])/(8*b) + (3*Log[Cos[a + b*x] + Sin[a + b*x] + Sqrt[Sin[2*a + 2*b*x]]])/(8*b) + (Cos[a + b*x]*Sqrt[Sin[2*a + 2*b*x]])/(4*b)", //
4382, 4390);}

// {4380, 4376}
public void test1263() {
	check(//
"Integrate[Cos[a + b*x]^3/Sin[2*a + 2*b*x]^(7/2), x]", //
 "-Cos[a + b*x]^3/(5*b*Sin[2*a + 2*b*x]^(5/2)) - Cos[a + b*x]/(5*b*Sqrt[Sin[2*a + 2*b*x]])", //
4380, 4376);}

// {4393, 4390}
public void test1264() {
	check(//
"Integrate[Csc[x]*Sqrt[Sin[2*x]], x]", //
 "-ArcSin[Cos[x] - Sin[x]] + Log[Cos[x] + Sin[x] + Sqrt[Sin[2*x]]]", //
4393, 4390);}

// {4394, 2656}
public void test1265() {
	check(//
"Integrate[Cos[a + b*x]^3*Sin[2*a + 2*b*x]^m, x]", //
 "-((Cos[a + b*x]^3*Cot[a + b*x]*Hypergeometric2F1[(1 - m)/2, (4 + m)/2, (6 + m)/2, Cos[a + b*x]^2]*(Sin[a + b*x]^2)^((1 - m)/2)*Sin[2*a + 2*b*x]^m)/(b*(4 + m)))", //
4394, 2656);}

// {4394, 2656}
public void test1266() {
	check(//
"Integrate[Cos[a + b*x]^2*Sin[2*a + 2*b*x]^m, x]", //
 "-((Cos[a + b*x]^2*Cot[a + b*x]*Hypergeometric2F1[(1 - m)/2, (3 + m)/2, (5 + m)/2, Cos[a + b*x]^2]*(Sin[a + b*x]^2)^((1 - m)/2)*Sin[2*a + 2*b*x]^m)/(b*(3 + m)))", //
4394, 2656);}

// {4394, 2656}
public void test1267() {
	check(//
"Integrate[Cos[a + b*x]*Sin[2*a + 2*b*x]^m, x]", //
 "-((Cos[a + b*x]*Cot[a + b*x]*Hypergeometric2F1[(1 - m)/2, (2 + m)/2, (4 + m)/2, Cos[a + b*x]^2]*(Sin[a + b*x]^2)^((1 - m)/2)*Sin[2*a + 2*b*x]^m)/(b*(2 + m)))", //
4394, 2656);}

// {3176, 3212}
public void test1268() {
	check(//
"Integrate[Sin[x]/(a*Cos[x] + b*Sin[x]), x]", //
 "(b*x)/(a^2 + b^2) - (a*Log[a*Cos[x] + b*Sin[x]])/(a^2 + b^2)", //
3176, 3212);}

// {3153, 212}
public void test1269() {
	check(//
"Integrate[(a*Cos[x] + b*Sin[x])^(-1), x]", //
 "-(ArcTanh[(b*Cos[x] - a*Sin[x])/Sqrt[a^2 + b^2]]/Sqrt[a^2 + b^2])", //
3153, 212);}

// {3152, 8}
public void test1270() {
	check(//
"Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^2, x]", //
 "((a^2 + b^2)*x)/2 - ((b*Cos[c + d*x] - a*Sin[c + d*x])*(a*Cos[c + d*x] + b*Sin[c + d*x]))/(2*d)", //
3152, 8);}

// {3167, 37}
public void test1271() {
	check(//
"Integrate[Sec[c + d*x]^4*(a*Cos[c + d*x] + b*Sin[c + d*x])^2, x]", //
 "((b + a*Cot[c + d*x])^3*Tan[c + d*x]^3)/(3*b*d)", //
3167, 37);}

// {3151}
public void test1272() {
	check(//
"Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^3, x]", //
 "-(((a^2 + b^2)*(b*Cos[c + d*x] - a*Sin[c + d*x]))/d) + (b*Cos[c + d*x] - a*Sin[c + d*x])^3/(3*d)", //
3151);}

// {3167, 37}
public void test1273() {
	check(//
"Integrate[Sec[c + d*x]^5*(a*Cos[c + d*x] + b*Sin[c + d*x])^3, x]", //
 "((b + a*Cot[c + d*x])^4*Tan[c + d*x]^4)/(4*b*d)", //
3167, 37);}

// {3167, 37}
public void test1274() {
	check(//
"Integrate[Sec[c + d*x]^6*(a*Cos[c + d*x] + b*Sin[c + d*x])^4, x]", //
 "((b + a*Cot[c + d*x])^5*Tan[c + d*x]^5)/(5*b*d)", //
3167, 37);}

// {3167, 37}
public void test1275() {
	check(//
"Integrate[Sec[c + d*x]^7*(a*Cos[c + d*x] + b*Sin[c + d*x])^5, x]", //
 "((b + a*Cot[c + d*x])^6*Tan[c + d*x]^6)/(6*b*d)", //
3167, 37);}

// {3177, 3212}
public void test1276() {
	check(//
"Integrate[Cos[c + d*x]/(a*Cos[c + d*x] + b*Sin[c + d*x]), x]", //
 "(a*x)/(a^2 + b^2) + (b*Log[a*Cos[c + d*x] + b*Sin[c + d*x]])/((a^2 + b^2)*d)", //
3177, 3212);}

// {3153, 212}
public void test1277() {
	check(//
"Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^(-1), x]", //
 "-(ArcTanh[(b*Cos[c + d*x] - a*Sin[c + d*x])/Sqrt[a^2 + b^2]]/(Sqrt[a^2 + b^2]*d))", //
3153, 212);}

// {3167, 37}
public void test1278() {
	check(//
"Integrate[Cos[c + d*x]^2/(a*Cos[c + d*x] + b*Sin[c + d*x])^4, x]", //
 "-Cot[c + d*x]^3/(3*b*d*(b + a*Cot[c + d*x])^3)", //
3167, 37);}

// {3155, 3154}
public void test1279() {
	check(//
"Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^(-4), x]", //
 "-(b*Cos[c + d*x] - a*Sin[c + d*x])/(3*(a^2 + b^2)*d*(a*Cos[c + d*x] + b*Sin[c + d*x])^3) + (2*Sin[c + d*x])/(3*a*(a^2 + b^2)*d*(a*Cos[c + d*x] + b*Sin[c + d*x]))", //
3155, 3154);}

// {3161, 8}
public void test1280() {
	check(//
"Integrate[Cos[c + d*x]/(a*Cos[c + d*x] + I*a*Sin[c + d*x]), x]", //
 "x/(2*a) + ((I/2)*Cos[c + d*x])/(d*(a*Cos[c + d*x] + I*a*Sin[c + d*x]))", //
3161, 8);}

// {3167, 37}
public void test1281() {
	check(//
"Integrate[Cos[c + d*x]/(a*Cos[c + d*x] + I*a*Sin[c + d*x])^3, x]", //
 "((I/2)*Cot[c + d*x]^2)/(a^3*d*(I + Cot[c + d*x])^2)", //
3167, 37);}

// {3244, 2727}
public void test1282() {
	check(//
"Integrate[Sec[x]/(Sec[x] + Tan[x]), x]", //
 "-(Cos[x]/(1 + Sin[x]))", //
3244, 2727);}

// {3244, 2727}
public void test1283() {
	check(//
"Integrate[Sec[x]/(Sec[x] - Tan[x]), x]", //
 "Cos[x]/(1 - Sin[x])", //
3244, 2727);}

// {3245, 2727}
public void test1284() {
	check(//
"Integrate[Csc[x]/(Cot[x] + Csc[x]), x]", //
 "Sin[x]/(1 + Cos[x])", //
3245, 2727);}

// {3245, 2727}
public void test1285() {
	check(//
"Integrate[Csc[x]/(-Cot[x] + Csc[x]), x]", //
 "-(Sin[x]/(1 - Cos[x]))", //
3245, 2727);}

// {4419, 266}
public void test1286() {
	check(//
"Integrate[Cos[c + d*x]/(Csc[c + d*x] + Sin[c + d*x]), x]", //
 "Log[1 + Sin[c + d*x]^2]/(2*d)", //
4419, 266);}

// {4423, 209}
public void test1287() {
	check(//
"Integrate[Cot[c + d*x]/(Csc[c + d*x] + Sin[c + d*x]), x]", //
 "ArcTan[Sin[c + d*x]]/d", //
4423, 209);}

// {209}
public void test1288() {
	check(//
"Integrate[Csc[c + d*x]/(Csc[c + d*x] + Sin[c + d*x]), x]", //
 "x/Sqrt[2] + ArcTan[(Cos[c + d*x]*Sin[c + d*x])/(1 + Sqrt[2] + Sin[c + d*x]^2)]/(Sqrt[2]*d)", //
209);}

// {4419, 266}
public void test1289() {
	check(//
"Integrate[Cos[c + d*x]/(Csc[c + d*x] - Sin[c + d*x]), x]", //
 "-(Log[Cos[c + d*x]]/d)", //
4419, 266);}

// {4423, 212}
public void test1290() {
	check(//
"Integrate[Cot[c + d*x]/(Csc[c + d*x] - Sin[c + d*x]), x]", //
 "ArcTanh[Sin[c + d*x]]/d", //
4423, 212);}

// {267}
public void test1291() {
	check(//
"Integrate[Sec[c + d*x]/(Csc[c + d*x] - Sin[c + d*x]), x]", //
 "Sec[c + d*x]^2/(2*d)", //
267);}

// {8}
public void test1292() {
	check(//
"Integrate[Csc[c + d*x]/(Csc[c + d*x] - Sin[c + d*x]), x]", //
 "Tan[c + d*x]/d", //
8);}

// {4495, 3855}
public void test1293() {
	check(//
"Integrate[(c + d*x)*Cot[a + b*x]*Csc[a + b*x], x]", //
 "-((d*ArcTanh[Cos[a + b*x]])/b^2) - ((c + d*x)*Csc[a + b*x])/b", //
4495, 3855);}

// {4494, 3855}
public void test1294() {
	check(//
"Integrate[(c + d*x)*Sec[a + b*x]*Tan[a + b*x], x]", //
 "-((d*ArcTanh[Sin[a + b*x]])/b^2) + ((c + d*x)*Sec[a + b*x])/b", //
4494, 3855);}

// {3525, 2719}
public void test1295() {
	check(//
"Integrate[(x*Sin[a + b*x])/Sqrt[Cos[a + b*x]], x]", //
 "(-2*x*Sqrt[Cos[a + b*x]])/b + (4*EllipticE[(a + b*x)/2, 2])/b^2", //
3525, 2719);}

// {3525, 2720}
public void test1296() {
	check(//
"Integrate[(x*Sin[a + b*x])/Cos[a + b*x]^(3/2), x]", //
 "(2*x)/(b*Sqrt[Cos[a + b*x]]) - (4*EllipticF[(a + b*x)/2, 2])/b^2", //
3525, 2720);}

// {3524, 2719}
public void test1297() {
	check(//
"Integrate[(x*Cos[a + b*x])/Sqrt[Sin[a + b*x]], x]", //
 "(-4*EllipticE[(a - Pi/2 + b*x)/2, 2])/b^2 + (2*x*Sqrt[Sin[a + b*x]])/b", //
3524, 2719);}

// {3524, 2720}
public void test1298() {
	check(//
"Integrate[(x*Cos[a + b*x])/Sin[a + b*x]^(3/2), x]", //
 "(4*EllipticF[(a - Pi/2 + b*x)/2, 2])/b^2 - (2*x)/(b*Sqrt[Sin[a + b*x]])", //
3524, 2720);}

// {2718}
public void test1299() {
	check(//
"Integrate[Sin[a + b*Log[c*x^n]]/x, x]", //
 "-(Cos[a + b*Log[c*x^n]]/(b*n))", //
2718);}

// {4575, 30}
public void test1300() {
	check(//
"Integrate[x^2*Sin[a + b*Log[c*x^n]]^2, x]", //
 "(2*b^2*n^2*x^3)/(3*(9 + 4*b^2*n^2)) - (2*b*n*x^3*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/(9 + 4*b^2*n^2) + (3*x^3*Sin[a + b*Log[c*x^n]]^2)/(9 + 4*b^2*n^2)", //
4575, 30);}

// {4575, 30}
public void test1301() {
	check(//
"Integrate[x*Sin[a + b*Log[c*x^n]]^2, x]", //
 "(b^2*n^2*x^2)/(4*(1 + b^2*n^2)) - (b*n*x^2*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/(2*(1 + b^2*n^2)) + (x^2*Sin[a + b*Log[c*x^n]]^2)/(2*(1 + b^2*n^2))", //
4575, 30);}

// {4565, 8}
public void test1302() {
	check(//
"Integrate[Sin[a + b*Log[c*x^n]]^2, x]", //
 "(2*b^2*n^2*x)/(1 + 4*b^2*n^2) - (2*b*n*x*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/(1 + 4*b^2*n^2) + (x*Sin[a + b*Log[c*x^n]]^2)/(1 + 4*b^2*n^2)", //
4565, 8);}

// {4575, 30}
public void test1303() {
	check(//
"Integrate[Sin[a + b*Log[c*x^n]]^2/x^2, x]", //
 "(-2*b^2*n^2)/((1 + 4*b^2*n^2)*x) - (2*b*n*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/((1 + 4*b^2*n^2)*x) - Sin[a + b*Log[c*x^n]]^2/((1 + 4*b^2*n^2)*x)", //
4575, 30);}

// {4575, 30}
public void test1304() {
	check(//
"Integrate[Sin[a + b*Log[c*x^n]]^2/x^3, x]", //
 "-(b^2*n^2)/(4*(1 + b^2*n^2)*x^2) - (b*n*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/(2*(1 + b^2*n^2)*x^2) - Sin[a + b*Log[c*x^n]]^2/(2*(1 + b^2*n^2)*x^2)", //
4575, 30);}

// {4575, 4573}
public void test1305() {
	check(//
"Integrate[x^2*Sin[a + b*Log[c*x^n]]^3, x]", //
 "(-2*b^3*n^3*x^3*Cos[a + b*Log[c*x^n]])/(3*(9 + 10*b^2*n^2 + b^4*n^4)) + (2*b^2*n^2*x^3*Sin[a + b*Log[c*x^n]])/(9 + 10*b^2*n^2 + b^4*n^4) - (b*n*x^3*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]]^2)/(3*(1 + b^2*n^2)) + (x^3*Sin[a + b*Log[c*x^n]]^3)/(3*(1 + b^2*n^2))", //
4575, 4573);}

// {4575, 4573}
public void test1306() {
	check(//
"Integrate[x*Sin[a + b*Log[c*x^n]]^3, x]", //
 "(-6*b^3*n^3*x^2*Cos[a + b*Log[c*x^n]])/(16 + 40*b^2*n^2 + 9*b^4*n^4) + (12*b^2*n^2*x^2*Sin[a + b*Log[c*x^n]])/(16 + 40*b^2*n^2 + 9*b^4*n^4) - (3*b*n*x^2*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]]^2)/(4 + 9*b^2*n^2) + (2*x^2*Sin[a + b*Log[c*x^n]]^3)/(4 + 9*b^2*n^2)", //
4575, 4573);}

// {4565, 4563}
public void test1307() {
	check(//
"Integrate[Sin[a + b*Log[c*x^n]]^3, x]", //
 "(-6*b^3*n^3*x*Cos[a + b*Log[c*x^n]])/(1 + 10*b^2*n^2 + 9*b^4*n^4) + (6*b^2*n^2*x*Sin[a + b*Log[c*x^n]])/(1 + 10*b^2*n^2 + 9*b^4*n^4) - (3*b*n*x*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]]^2)/(1 + 9*b^2*n^2) + (x*Sin[a + b*Log[c*x^n]]^3)/(1 + 9*b^2*n^2)", //
4565, 4563);}

// {4575, 4573}
public void test1308() {
	check(//
"Integrate[Sin[a + b*Log[c*x^n]]^3/x^2, x]", //
 "(-6*b^3*n^3*Cos[a + b*Log[c*x^n]])/((1 + 10*b^2*n^2 + 9*b^4*n^4)*x) - (6*b^2*n^2*Sin[a + b*Log[c*x^n]])/((1 + 10*b^2*n^2 + 9*b^4*n^4)*x) - (3*b*n*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]]^2)/((1 + 9*b^2*n^2)*x) - Sin[a + b*Log[c*x^n]]^3/((1 + 9*b^2*n^2)*x)", //
4575, 4573);}

// {4575, 4573}
public void test1309() {
	check(//
"Integrate[Sin[a + b*Log[c*x^n]]^3/x^3, x]", //
 "(-6*b^3*n^3*Cos[a + b*Log[c*x^n]])/((16 + 40*b^2*n^2 + 9*b^4*n^4)*x^2) - (12*b^2*n^2*Sin[a + b*Log[c*x^n]])/((16 + 40*b^2*n^2 + 9*b^4*n^4)*x^2) - (3*b*n*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]]^2)/((4 + 9*b^2*n^2)*x^2) - (2*Sin[a + b*Log[c*x^n]]^3)/((4 + 9*b^2*n^2)*x^2)", //
4575, 4573);}

// {4563}
public void test1310() {
	check(//
"Integrate[Sin[Log[a + b*x]], x]", //
 "-((a + b*x)*Cos[Log[a + b*x]])/(2*b) + ((a + b*x)*Sin[Log[a + b*x]])/(2*b)", //
4563);}

// {12, 29}
public void test1311() {
	check(//
"Integrate[Sin[a]/x, x]", //
 "Log[x]*Sin[a]", //
12, 29);}

// {12, 29}
public void test1312() {
	check(//
"Integrate[Sin[a]^2/x, x]", //
 "Log[x]*Sin[a]^2", //
12, 29);}

// {4575, 4573}
public void test1313() {
	check(//
"Integrate[x^m*Sin[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2]^3, x]", //
 "(-4*Sqrt[-((1 + m)^2/n^2)]*n*x^(1 + m)*Cos[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2])/(5*(1 + m)^2) + (8*x^(1 + m)*Sin[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2])/(5*(1 + m)) + (6*Sqrt[-((1 + m)^2/n^2)]*n*x^(1 + m)*Cos[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2]*Sin[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2]^2)/(5*(1 + m)^2) - (4*x^(1 + m)*Sin[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2]^3)/(5*(1 + m))", //
4575, 4573);}

// {12, 29}
public void test1314() {
	check(//
"Integrate[Sin[a]^3/x, x]", //
 "Log[x]*Sin[a]^3", //
12, 29);}

// {2719}
public void test1315() {
	check(//
"Integrate[Sqrt[Sin[a + b*Log[c*x^n]]]/x, x]", //
 "(2*EllipticE[(a - Pi/2 + b*Log[c*x^n])/2, 2])/(b*n)", //
2719);}

// {2720}
public void test1316() {
	check(//
"Integrate[1/(x*Sqrt[Sin[a + b*Log[c*x^n]]]), x]", //
 "(2*EllipticF[(a - Pi/2 + b*Log[c*x^n])/2, 2])/(b*n)", //
2720);}

// {4575, 4573}
public void test1317() {
	check(//
"Integrate[(e*x)^m*Sin[d*(a + b*Log[c*x^n])]^3, x]", //
 "(-6*b^3*d^3*n^3*(e*x)^(1 + m)*Cos[d*(a + b*Log[c*x^n])])/(e*((1 + m)^2 + b^2*d^2*n^2)*((1 + m)^2 + 9*b^2*d^2*n^2)) + (6*b^2*d^2*(1 + m)*n^2*(e*x)^(1 + m)*Sin[d*(a + b*Log[c*x^n])])/(e*((1 + m)^2 + b^2*d^2*n^2)*((1 + m)^2 + 9*b^2*d^2*n^2)) - (3*b*d*n*(e*x)^(1 + m)*Cos[d*(a + b*Log[c*x^n])]*Sin[d*(a + b*Log[c*x^n])]^2)/(e*((1 + m)^2 + 9*b^2*d^2*n^2)) + ((1 + m)*(e*x)^(1 + m)*Sin[d*(a + b*Log[c*x^n])]^3)/(e*((1 + m)^2 + 9*b^2*d^2*n^2))", //
4575, 4573);}

// {4575, 32}
public void test1318() {
	check(//
"Integrate[(e*x)^m*Sin[d*(a + b*Log[c*x^n])]^2, x]", //
 "(2*b^2*d^2*n^2*(e*x)^(1 + m))/(e*(1 + m)*((1 + m)^2 + 4*b^2*d^2*n^2)) - (2*b*d*n*(e*x)^(1 + m)*Cos[d*(a + b*Log[c*x^n])]*Sin[d*(a + b*Log[c*x^n])])/(e*((1 + m)^2 + 4*b^2*d^2*n^2)) + ((1 + m)*(e*x)^(1 + m)*Sin[d*(a + b*Log[c*x^n])]^2)/(e*((1 + m)^2 + 4*b^2*d^2*n^2))", //
4575, 32);}

// {2722}
public void test1319() {
	check(//
"Integrate[Sin[a + b*Log[c*x^n]]^p/x, x]", //
 "(Cos[a + b*Log[c*x^n]]*Hypergeometric2F1[1/2, (1 + p)/2, (3 + p)/2, Sin[a + b*Log[c*x^n]]^2]*Sin[a + b*Log[c*x^n]]^(1 + p))/(b*n*(1 + p)*Sqrt[Cos[a + b*Log[c*x^n]]^2])", //
2722);}

// {2717}
public void test1320() {
	check(//
"Integrate[Cos[a + b*Log[c*x^n]]/x, x]", //
 "Sin[a + b*Log[c*x^n]]/(b*n)", //
2717);}

// {4576, 30}
public void test1321() {
	check(//
"Integrate[x^2*Cos[a + b*Log[c*x^n]]^2, x]", //
 "(2*b^2*n^2*x^3)/(3*(9 + 4*b^2*n^2)) + (3*x^3*Cos[a + b*Log[c*x^n]]^2)/(9 + 4*b^2*n^2) + (2*b*n*x^3*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/(9 + 4*b^2*n^2)", //
4576, 30);}

// {4576, 30}
public void test1322() {
	check(//
"Integrate[x*Cos[a + b*Log[c*x^n]]^2, x]", //
 "(b^2*n^2*x^2)/(4*(1 + b^2*n^2)) + (x^2*Cos[a + b*Log[c*x^n]]^2)/(2*(1 + b^2*n^2)) + (b*n*x^2*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/(2*(1 + b^2*n^2))", //
4576, 30);}

// {4566, 8}
public void test1323() {
	check(//
"Integrate[Cos[a + b*Log[c*x^n]]^2, x]", //
 "(2*b^2*n^2*x)/(1 + 4*b^2*n^2) + (x*Cos[a + b*Log[c*x^n]]^2)/(1 + 4*b^2*n^2) + (2*b*n*x*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/(1 + 4*b^2*n^2)", //
4566, 8);}

// {4576, 30}
public void test1324() {
	check(//
"Integrate[Cos[a + b*Log[c*x^n]]^2/x^2, x]", //
 "(-2*b^2*n^2)/((1 + 4*b^2*n^2)*x) - Cos[a + b*Log[c*x^n]]^2/((1 + 4*b^2*n^2)*x) + (2*b*n*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/((1 + 4*b^2*n^2)*x)", //
4576, 30);}

// {4576, 4574}
public void test1325() {
	check(//
"Integrate[x^2*Cos[a + b*Log[c*x^n]]^3, x]", //
 "(2*b^2*n^2*x^3*Cos[a + b*Log[c*x^n]])/(9 + 10*b^2*n^2 + b^4*n^4) + (x^3*Cos[a + b*Log[c*x^n]]^3)/(3*(1 + b^2*n^2)) + (2*b^3*n^3*x^3*Sin[a + b*Log[c*x^n]])/(3*(9 + 10*b^2*n^2 + b^4*n^4)) + (b*n*x^3*Cos[a + b*Log[c*x^n]]^2*Sin[a + b*Log[c*x^n]])/(3*(1 + b^2*n^2))", //
4576, 4574);}

// {4576, 4574}
public void test1326() {
	check(//
"Integrate[x*Cos[a + b*Log[c*x^n]]^3, x]", //
 "(12*b^2*n^2*x^2*Cos[a + b*Log[c*x^n]])/(16 + 40*b^2*n^2 + 9*b^4*n^4) + (2*x^2*Cos[a + b*Log[c*x^n]]^3)/(4 + 9*b^2*n^2) + (6*b^3*n^3*x^2*Sin[a + b*Log[c*x^n]])/(16 + 40*b^2*n^2 + 9*b^4*n^4) + (3*b*n*x^2*Cos[a + b*Log[c*x^n]]^2*Sin[a + b*Log[c*x^n]])/(4 + 9*b^2*n^2)", //
4576, 4574);}

// {4566, 4564}
public void test1327() {
	check(//
"Integrate[Cos[a + b*Log[c*x^n]]^3, x]", //
 "(6*b^2*n^2*x*Cos[a + b*Log[c*x^n]])/(1 + 10*b^2*n^2 + 9*b^4*n^4) + (x*Cos[a + b*Log[c*x^n]]^3)/(1 + 9*b^2*n^2) + (6*b^3*n^3*x*Sin[a + b*Log[c*x^n]])/(1 + 10*b^2*n^2 + 9*b^4*n^4) + (3*b*n*x*Cos[a + b*Log[c*x^n]]^2*Sin[a + b*Log[c*x^n]])/(1 + 9*b^2*n^2)", //
4566, 4564);}

// {4576, 4574}
public void test1328() {
	check(//
"Integrate[Cos[a + b*Log[c*x^n]]^3/x^2, x]", //
 "(-6*b^2*n^2*Cos[a + b*Log[c*x^n]])/((1 + 10*b^2*n^2 + 9*b^4*n^4)*x) - Cos[a + b*Log[c*x^n]]^3/((1 + 9*b^2*n^2)*x) + (6*b^3*n^3*Sin[a + b*Log[c*x^n]])/((1 + 10*b^2*n^2 + 9*b^4*n^4)*x) + (3*b*n*Cos[a + b*Log[c*x^n]]^2*Sin[a + b*Log[c*x^n]])/((1 + 9*b^2*n^2)*x)", //
4576, 4574);}

// {4564}
public void test1329() {
	check(//
"Integrate[Cos[Log[6 + 3*x]], x]", //
 "((2 + x)*Cos[Log[3*(2 + x)]])/2 + ((2 + x)*Sin[Log[3*(2 + x)]])/2", //
4564);}

// {4576, 4574}
public void test1330() {
	check(//
"Integrate[x^m*Cos[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2]^3, x]", //
 "(8*x^(1 + m)*Cos[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2])/(5*(1 + m)) - (4*x^(1 + m)*Cos[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2]^3)/(5*(1 + m)) + (4*Sqrt[-((1 + m)^2/n^2)]*n*x^(1 + m)*Sin[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2])/(5*(1 + m)^2) - (6*Sqrt[-((1 + m)^2/n^2)]*n*x^(1 + m)*Cos[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2]^2*Sin[a + (Sqrt[-((1 + m)^2/n^2)]*Log[c*x^n])/2])/(5*(1 + m)^2)", //
4576, 4574);}

// {2719}
public void test1331() {
	check(//
"Integrate[Sqrt[Cos[a + b*Log[c*x^n]]]/x, x]", //
 "(2*EllipticE[(a + b*Log[c*x^n])/2, 2])/(b*n)", //
2719);}

// {2720}
public void test1332() {
	check(//
"Integrate[1/(x*Sqrt[Cos[a + b*Log[c*x^n]]]), x]", //
 "(2*EllipticF[(a + b*Log[c*x^n])/2, 2])/(b*n)", //
2720);}

// {4576, 4574}
public void test1333() {
	check(//
"Integrate[x^m*Cos[a + b*Log[c*x^n]]^3, x]", //
 "(6*b^2*(1 + m)*n^2*x^(1 + m)*Cos[a + b*Log[c*x^n]])/(((1 + m)^2 + b^2*n^2)*((1 + m)^2 + 9*b^2*n^2)) + ((1 + m)*x^(1 + m)*Cos[a + b*Log[c*x^n]]^3)/((1 + m)^2 + 9*b^2*n^2) + (6*b^3*n^3*x^(1 + m)*Sin[a + b*Log[c*x^n]])/(((1 + m)^2 + b^2*n^2)*((1 + m)^2 + 9*b^2*n^2)) + (3*b*n*x^(1 + m)*Cos[a + b*Log[c*x^n]]^2*Sin[a + b*Log[c*x^n]])/((1 + m)^2 + 9*b^2*n^2)", //
4576, 4574);}

// {4576, 30}
public void test1334() {
	check(//
"Integrate[x^m*Cos[a + b*Log[c*x^n]]^2, x]", //
 "(2*b^2*n^2*x^(1 + m))/((1 + m)*((1 + m)^2 + 4*b^2*n^2)) + ((1 + m)*x^(1 + m)*Cos[a + b*Log[c*x^n]]^2)/((1 + m)^2 + 4*b^2*n^2) + (2*b*n*x^(1 + m)*Cos[a + b*Log[c*x^n]]*Sin[a + b*Log[c*x^n]])/((1 + m)^2 + 4*b^2*n^2)", //
4576, 30);}

// {3556}
public void test1335() {
	check(//
"Integrate[Tan[a + b*Log[c*x^n]]/x, x]", //
 "-(Log[Cos[a + b*Log[c*x^n]]]/(b*n))", //
3556);}

// {3556}
public void test1336() {
	check(//
"Integrate[Cot[a + b*Log[c*x^n]]/x, x]", //
 "Log[Sin[a + b*Log[c*x^n]]]/(b*n)", //
3556);}

// {3855}
public void test1337() {
	check(//
"Integrate[Sec[a + b*Log[c*x^n]]/x, x]", //
 "ArcTanh[Sin[a + b*Log[c*x^n]]]/(b*n)", //
3855);}

// {3855}
public void test1338() {
	check(//
"Integrate[Csc[a + b*Log[c*x^n]]/x, x]", //
 "-(ArcTanh[Cos[a + b*Log[c*x^n]]]/(b*n))", //
3855);}

// {4525, 2291}
public void test1339() {
	check(//
"Integrate[F^(c*(a + b*x))*Sin[d + e*x]^n, x]", //
 "-((F^(c*(a + b*x))*Hypergeometric2F1[-n, -(e*n + I*b*c*Log[F])/(2*e), (2 - n - (I*b*c*Log[F])/e)/2, E^((2*I)*(d + e*x))]*Sin[d + e*x]^n)/((1 - E^((2*I)*(d + e*x)))^n*(I*e*n - b*c*Log[F])))", //
4525, 2291);}

// {4519, 4517}
public void test1340() {
	check(//
"Integrate[F^(c*(a + b*x))*Sin[d + e*x]^3, x]", //
 "(-6*e^3*F^(c*(a + b*x))*Cos[d + e*x])/(9*e^4 + 10*b^2*c^2*e^2*Log[F]^2 + b^4*c^4*Log[F]^4) + (6*b*c*e^2*F^(c*(a + b*x))*Log[F]*Sin[d + e*x])/(9*e^4 + 10*b^2*c^2*e^2*Log[F]^2 + b^4*c^4*Log[F]^4) - (3*e*F^(c*(a + b*x))*Cos[d + e*x]*Sin[d + e*x]^2)/(9*e^2 + b^2*c^2*Log[F]^2) + (b*c*F^(c*(a + b*x))*Log[F]*Sin[d + e*x]^3)/(9*e^2 + b^2*c^2*Log[F]^2)", //
4519, 4517);}

// {4519, 2225}
public void test1341() {
	check(//
"Integrate[F^(c*(a + b*x))*Sin[d + e*x]^2, x]", //
 "(2*e^2*F^(c*(a + b*x)))/(b*c*Log[F]*(4*e^2 + b^2*c^2*Log[F]^2)) - (2*e*F^(c*(a + b*x))*Cos[d + e*x]*Sin[d + e*x])/(4*e^2 + b^2*c^2*Log[F]^2) + (b*c*F^(c*(a + b*x))*Log[F]*Sin[d + e*x]^2)/(4*e^2 + b^2*c^2*Log[F]^2)", //
4519, 2225);}

// {4534, 4538}
public void test1342() {
	check(//
"Integrate[F^(c*(a + b*x))*Csc[d + e*x]^3, x]", //
 "-(F^(c*(a + b*x))*Cot[d + e*x]*Csc[d + e*x])/(2*e) - (b*c*F^(c*(a + b*x))*Csc[d + e*x]*Log[F])/(2*e^2) - (E^(I*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[1, (e - I*b*c*Log[F])/(2*e), (3 - (I*b*c*Log[F])/e)/2, E^((2*I)*(d + e*x))]*(e + I*b*c*Log[F]))/e^2", //
4534, 4538);}

// {4534, 4538}
public void test1343() {
	check(//
"Integrate[F^(c*(a + b*x))*Csc[d + e*x]^4, x]", //
 "-(F^(c*(a + b*x))*Cot[d + e*x]*Csc[d + e*x]^2)/(3*e) - (b*c*F^(c*(a + b*x))*Csc[d + e*x]^2*Log[F])/(6*e^2) + (2*E^((2*I)*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 - ((I/2)*b*c*Log[F])/e, 2 - ((I/2)*b*c*Log[F])/e, E^((2*I)*(d + e*x))]*((2*I)*e - b*c*Log[F]))/(3*e^2)", //
4534, 4538);}

// {4526, 2291}
public void test1344() {
	check(//
"Integrate[F^(c*(a + b*x))*Cos[d + e*x]^n, x]", //
 "-((F^(c*(a + b*x))*Cos[d + e*x]^n*Hypergeometric2F1[-n, -(e*n + I*b*c*Log[F])/(2*e), (2 - n - (I*b*c*Log[F])/e)/2, -E^((2*I)*(d + e*x))])/((1 + E^((2*I)*(d + e*x)))^n*(I*e*n - b*c*Log[F])))", //
4526, 2291);}

// {4520, 4518}
public void test1345() {
	check(//
"Integrate[F^(c*(a + b*x))*Cos[d + e*x]^3, x]", //
 "(b*c*F^(c*(a + b*x))*Cos[d + e*x]^3*Log[F])/(9*e^2 + b^2*c^2*Log[F]^2) + (6*b*c*e^2*F^(c*(a + b*x))*Cos[d + e*x]*Log[F])/(9*e^4 + 10*b^2*c^2*e^2*Log[F]^2 + b^4*c^4*Log[F]^4) + (3*e*F^(c*(a + b*x))*Cos[d + e*x]^2*Sin[d + e*x])/(9*e^2 + b^2*c^2*Log[F]^2) + (6*e^3*F^(c*(a + b*x))*Sin[d + e*x])/(9*e^4 + 10*b^2*c^2*e^2*Log[F]^2 + b^4*c^4*Log[F]^4)", //
4520, 4518);}

// {4520, 2225}
public void test1346() {
	check(//
"Integrate[F^(c*(a + b*x))*Cos[d + e*x]^2, x]", //
 "(2*e^2*F^(c*(a + b*x)))/(b*c*Log[F]*(4*e^2 + b^2*c^2*Log[F]^2)) + (b*c*F^(c*(a + b*x))*Cos[d + e*x]^2*Log[F])/(4*e^2 + b^2*c^2*Log[F]^2) + (2*e*F^(c*(a + b*x))*Cos[d + e*x]*Sin[d + e*x])/(4*e^2 + b^2*c^2*Log[F]^2)", //
4520, 2225);}

// {4533, 4536}
public void test1347() {
	check(//
"Integrate[F^(c*(a + b*x))*Sec[d + e*x]^3, x]", //
 "-((E^(I*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[1, (e - I*b*c*Log[F])/(2*e), (3 - (I*b*c*Log[F])/e)/2, -E^((2*I)*(d + e*x))]*(I*e - b*c*Log[F]))/e^2) - (b*c*F^(c*(a + b*x))*Log[F]*Sec[d + e*x])/(2*e^2) + (F^(c*(a + b*x))*Sec[d + e*x]*Tan[d + e*x])/(2*e)", //
4533, 4536);}

// {4533, 4536}
public void test1348() {
	check(//
"Integrate[F^(c*(a + b*x))*Sec[d + e*x]^4, x]", //
 "(-2*E^((2*I)*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 - ((I/2)*b*c*Log[F])/e, 2 - ((I/2)*b*c*Log[F])/e, -E^((2*I)*(d + e*x))]*((2*I)*e - b*c*Log[F]))/(3*e^2) - (b*c*F^(c*(a + b*x))*Log[F]*Sec[d + e*x]^2)/(6*e^2) + (F^(c*(a + b*x))*Sec[d + e*x]^2*Tan[d + e*x])/(3*e)", //
4533, 4536);}

// {4539, 2291}
public void test1349() {
	check(//
"Integrate[F^(c*(a + b*x))*Sec[d + e*x]^n, x]", //
 "((1 + E^((2*I)*(d + e*x)))^n*F^(a*c + b*c*x)*Hypergeometric2F1[n, (e*n - I*b*c*Log[F])/(2*e), (2 + n - (I*b*c*Log[F])/e)/2, -E^((2*I)*(d + e*x))]*Sec[d + e*x]^n)/(I*e*n + b*c*Log[F])", //
4539, 2291);}

// {4540, 2291}
public void test1350() {
	check(//
"Integrate[F^(c*(a + b*x))*Csc[d + e*x]^n, x]", //
 "-(((1 - E^((-2*I)*(d + e*x)))^n*F^(a*c + b*c*x)*Csc[d + e*x]^n*Hypergeometric2F1[n, (e*n + I*b*c*Log[F])/(2*e), (2 + n + (I*b*c*Log[F])/e)/2, E^((-2*I)*(d + e*x))])/(I*e*n - b*c*Log[F]))", //
4540, 2291);}

// {6847, 4518}
public void test1351() {
	check(//
"Integrate[E^(2*x^2)*x*Cos[2*x^2], x]", //
 "(E^(2*x^2)*Cos[2*x^2])/8 + (E^(2*x^2)*Sin[2*x^2])/8", //
6847, 4518);}

// {2320, 2718}
public void test1352() {
	check(//
"Integrate[E^x*Sin[E^x], x]", //
 "-Cos[E^x]", //
2320, 2718);}

// {2320, 2717}
public void test1353() {
	check(//
"Integrate[E^x*Cos[E^x], x]", //
 "Sin[E^x]", //
2320, 2717);}

// {2320, 2717}
public void test1354() {
	check(//
"Integrate[E^(2*x)*Cos[E^(2*x)], x]", //
 "Sin[E^(2*x)]/2", //
2320, 2717);}

// {2320, 2717}
public void test1355() {
	check(//
"Integrate[Cos[E^(-2*x)]/E^(2*x), x]", //
 "-Sin[E^(-2*x)]/2", //
2320, 2717);}

// {2320, 3556}
public void test1356() {
	check(//
"Integrate[E^x*Tan[E^x], x]", //
 "-Log[Cos[E^x]]", //
2320, 3556);}

// {2320, 3855}
public void test1357() {
	check(//
"Integrate[E^x*Sec[E^x], x]", //
 "ArcTanh[Sin[E^x]]", //
2320, 3855);}

// {4541, 4535}
public void test1358() {
	check(//
"Integrate[F^(c*(a + b*x))/(f + f*Sin[d + e*x]), x]", //
 "(-2*E^(I*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 - (I*b*c*Log[F])/e, 2 - (I*b*c*Log[F])/e, I*E^(I*(d + e*x))])/(f*(e - I*b*c*Log[F]))", //
4541, 4535);}

// {4542, 4536}
public void test1359() {
	check(//
"Integrate[F^(c*(a + b*x))/(f + f*Cos[d + e*x]), x]", //
 "(2*E^(I*(d + e*x))*F^(c*(a + b*x))*Hypergeometric2F1[2, 1 - (I*b*c*Log[F])/e, 2 - (I*b*c*Log[F])/e, -E^(I*(d + e*x))])/(f*(I*e + b*c*Log[F]))", //
4542, 4536);}

// {12, 2736}
public void test1360() {
	check(//
"Integrate[2/(3 - Cos[4 + 6*x]), x]", //
 "x/Sqrt[2] + ArcTan[Sin[4 + 6*x]/(3 + 2*Sqrt[2] - Cos[4 + 6*x])]/(3*Sqrt[2])", //
12, 2736);}

// {3260, 209}
public void test1361() {
	check(//
"Integrate[(1 + Sin[2 + 3*x]^2)^(-1), x]", //
 "x/Sqrt[2] + ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Sin[2 + 3*x]^2)]/(3*Sqrt[2])", //
3260, 209);}

// {3260, 209}
public void test1362() {
	check(//
"Integrate[(2 - Cos[2 + 3*x]^2)^(-1), x]", //
 "x/Sqrt[2] + ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Sin[2 + 3*x]^2)]/(3*Sqrt[2])", //
3260, 209);}

// {209}
public void test1363() {
	check(//
"Integrate[(Cos[2 + 3*x]^2 + 2*Sin[2 + 3*x]^2)^(-1), x]", //
 "x/Sqrt[2] + ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Sin[2 + 3*x]^2)]/(3*Sqrt[2])", //
209);}

// {3756, 209}
public void test1364() {
	check(//
"Integrate[Sec[2 + 3*x]^2/(1 + 2*Tan[2 + 3*x]^2), x]", //
 "x/Sqrt[2] + ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Sin[2 + 3*x]^2)]/(3*Sqrt[2])", //
3756, 209);}

// {3756, 209}
public void test1365() {
	check(//
"Integrate[Csc[2 + 3*x]^2/(2 + Cot[2 + 3*x]^2), x]", //
 "x/Sqrt[2] + ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Sin[2 + 3*x]^2)]/(3*Sqrt[2])", //
3756, 209);}

// {3260, 213}
public void test1366() {
	check(//
"Integrate[(-1 + 3*Sin[2 + 3*x]^2)^(-1), x]", //
 "Log[Cos[2 + 3*x] - Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Cos[2 + 3*x] + Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2])", //
3260, 213);}

// {3260, 212}
public void test1367() {
	check(//
"Integrate[(2 - 3*Cos[2 + 3*x]^2)^(-1), x]", //
 "Log[Cos[2 + 3*x] - Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Cos[2 + 3*x] + Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2])", //
3260, 212);}

// {213}
public void test1368() {
	check(//
"Integrate[(-Cos[2 + 3*x]^2 + 2*Sin[2 + 3*x]^2)^(-1), x]", //
 "Log[Cos[2 + 3*x] - Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Cos[2 + 3*x] + Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2])", //
213);}

// {3756, 213}
public void test1369() {
	check(//
"Integrate[Sec[2 + 3*x]^2/(-1 + 2*Tan[2 + 3*x]^2), x]", //
 "Log[Cos[2 + 3*x] - Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Cos[2 + 3*x] + Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2])", //
3756, 213);}

// {3756, 212}
public void test1370() {
	check(//
"Integrate[Csc[2 + 3*x]^2/(2 - Cot[2 + 3*x]^2), x]", //
 "Log[Cos[2 + 3*x] - Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Cos[2 + 3*x] + Sqrt[2]*Sin[2 + 3*x]]/(6*Sqrt[2])", //
3756, 212);}

// {12, 2736}
public void test1371() {
	check(//
"Integrate[2/(3 + Cos[4 + 6*x]), x]", //
 "x/Sqrt[2] - ArcTan[Sin[4 + 6*x]/(3 + 2*Sqrt[2] + Cos[4 + 6*x])]/(3*Sqrt[2])", //
12, 2736);}

// {3260, 209}
public void test1372() {
	check(//
"Integrate[(2 - Sin[2 + 3*x]^2)^(-1), x]", //
 "x/Sqrt[2] - ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Cos[2 + 3*x]^2)]/(3*Sqrt[2])", //
3260, 209);}

// {3260, 209}
public void test1373() {
	check(//
"Integrate[(1 + Cos[2 + 3*x]^2)^(-1), x]", //
 "x/Sqrt[2] - ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Cos[2 + 3*x]^2)]/(3*Sqrt[2])", //
3260, 209);}

// {209}
public void test1374() {
	check(//
"Integrate[(2*Cos[2 + 3*x]^2 + Sin[2 + 3*x]^2)^(-1), x]", //
 "x/Sqrt[2] - ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Cos[2 + 3*x]^2)]/(3*Sqrt[2])", //
209);}

// {3756, 209}
public void test1375() {
	check(//
"Integrate[Sec[2 + 3*x]^2/(2 + Tan[2 + 3*x]^2), x]", //
 "x/Sqrt[2] - ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Cos[2 + 3*x]^2)]/(3*Sqrt[2])", //
3756, 209);}

// {3756, 209}
public void test1376() {
	check(//
"Integrate[Csc[2 + 3*x]^2/(1 + 2*Cot[2 + 3*x]^2), x]", //
 "x/Sqrt[2] - ArcTan[(Cos[2 + 3*x]*Sin[2 + 3*x])/(1 + Sqrt[2] + Cos[2 + 3*x]^2)]/(3*Sqrt[2])", //
3756, 209);}

// {3260, 213}
public void test1377() {
	check(//
"Integrate[(-2 + 3*Sin[2 + 3*x]^2)^(-1), x]", //
 "Log[Sqrt[2]*Cos[2 + 3*x] - Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Sqrt[2]*Cos[2 + 3*x] + Sin[2 + 3*x]]/(6*Sqrt[2])", //
3260, 213);}

// {3260, 212}
public void test1378() {
	check(//
"Integrate[(1 - 3*Cos[2 + 3*x]^2)^(-1), x]", //
 "Log[Sqrt[2]*Cos[2 + 3*x] - Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Sqrt[2]*Cos[2 + 3*x] + Sin[2 + 3*x]]/(6*Sqrt[2])", //
3260, 212);}

// {213}
public void test1379() {
	check(//
"Integrate[(-2*Cos[2 + 3*x]^2 + Sin[2 + 3*x]^2)^(-1), x]", //
 "Log[Sqrt[2]*Cos[2 + 3*x] - Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Sqrt[2]*Cos[2 + 3*x] + Sin[2 + 3*x]]/(6*Sqrt[2])", //
213);}

// {3756, 213}
public void test1380() {
	check(//
"Integrate[Sec[2 + 3*x]^2/(-2 + Tan[2 + 3*x]^2), x]", //
 "Log[Sqrt[2]*Cos[2 + 3*x] - Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Sqrt[2]*Cos[2 + 3*x] + Sin[2 + 3*x]]/(6*Sqrt[2])", //
3756, 213);}

// {3756, 212}
public void test1381() {
	check(//
"Integrate[Csc[2 + 3*x]^2/(1 - 2*Cot[2 + 3*x]^2), x]", //
 "Log[Sqrt[2]*Cos[2 + 3*x] - Sin[2 + 3*x]]/(6*Sqrt[2]) - Log[Sqrt[2]*Cos[2 + 3*x] + Sin[2 + 3*x]]/(6*Sqrt[2])", //
3756, 212);}

// {2727}
public void test1382() {
	check(//
"Integrate[1/(x*(1 + Sin[Log[x]])), x]", //
 "-(Cos[Log[x]]/(1 + Sin[Log[x]]))", //
2727);}

// {6813, 3380}
public void test1383() {
	check(//
"Integrate[Sin[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", //
 "-(SinIntegral[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/a)", //
6813, 3380);}

// {6813, 3383}
public void test1384() {
	check(//
"Integrate[Cos[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/(1 - a^2*x^2), x]", //
 "-(CosIntegral[Sqrt[1 - a*x]/Sqrt[1 + a*x]]/a)", //
6813, 3383);}

// {3832, 3556}
public void test1385() {
	check(//
"Integrate[Tan[Sqrt[x]]/Sqrt[x], x]", //
 "-2*Log[Cos[Sqrt[x]]]", //
3832, 3556);}

// {3848}
public void test1386() {
	check(//
"Integrate[(b*Tan[a + b*x + c*x^2])/(2*c) + x*Tan[a + b*x + c*x^2], x]", //
 "-Log[Cos[a + b*x + c*x^2]]/(2*c)", //
3848);}

// {2908, 4053}
public void test1387() {
	check(//
"Integrate[Sqrt[a + b*Sec[c + d*x]]/(1 + Cos[c + d*x]), x]", //
 "(EllipticE[ArcSin[Tan[c + d*x]/(1 + Sec[c + d*x])], (a - b)/(a + b)]*Sqrt[(1 + Sec[c + d*x])^(-1)]*Sqrt[a + b*Sec[c + d*x]])/(d*Sqrt[(a + b*Sec[c + d*x])/((a + b)*(1 + Sec[c + d*x]))])", //
2908, 4053);}

// {4442, 213}
public void test1388() {
	check(//
"Integrate[Sec[2*x]*Sin[x], x]", //
 "ArcTanh[Sqrt[2]*Cos[x]]/Sqrt[2]", //
4442, 213);}

// {4373, 3855}
public void test1389() {
	check(//
"Integrate[Csc[2*x]*Sin[x], x]", //
 "ArcTanh[Sin[x]]/2", //
4373, 3855);}

// {212}
public void test1390() {
	check(//
"Integrate[Csc[3*x]*Sin[x], x]", //
 "-Log[Sqrt[3]*Cos[x] - Sin[x]]/(2*Sqrt[3]) + Log[Sqrt[3]*Cos[x] + Sin[x]]/(2*Sqrt[3])", //
212);}

// {4373, 2717}
public void test1391() {
	check(//
"Integrate[Csc[3*x]*Sin[6*x], x]", //
 "(2*Sin[3*x])/3", //
4373, 2717);}

// {4441, 212}
public void test1392() {
	check(//
"Integrate[Cos[x]*Sec[2*x], x]", //
 "ArcTanh[Sqrt[2]*Sin[x]]/Sqrt[2]", //
4441, 212);}

// {212}
public void test1393() {
	check(//
"Integrate[Cos[x]*Sec[3*x], x]", //
 "-Log[Cos[x] - Sqrt[3]*Sin[x]]/(2*Sqrt[3]) + Log[Cos[x] + Sqrt[3]*Sin[x]]/(2*Sqrt[3])", //
212);}

// {4372, 3855}
public void test1394() {
	check(//
"Integrate[Cos[x]*Csc[2*x], x]", //
 "-ArcTanh[Cos[x]]/2", //
4372, 3855);}

// {4485, 2669}
public void test1395() {
	check(//
"Integrate[Sqrt[Sin[x]*Tan[x]], x]", //
 "-2*Cot[x]*Sqrt[Sin[x]*Tan[x]]", //
4485, 2669);}

// {4485, 2669}
public void test1396() {
	check(//
"Integrate[Sqrt[Cos[x]*Cot[x]], x]", //
 "2*Sqrt[Cos[x]*Cot[x]]*Tan[x]", //
4485, 2669);}

// {3756, 211}
public void test1397() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + b*Tan[c + d*x]^2), x]", //
 "ArcTan[(Sqrt[b]*Tan[c + d*x])/Sqrt[a]]/(Sqrt[a]*Sqrt[b]*d)", //
3756, 211);}

// {211}
public void test1398() {
	check(//
"Integrate[Sec[c + d*x]^2/(a + c*Sec[c + d*x]^2 + b*Tan[c + d*x]^2), x]", //
 "ArcTan[(Sqrt[b + c]*Tan[c + d*x])/Sqrt[a + c]]/(Sqrt[a + c]*Sqrt[b + c]*d)", //
211);}

// {3157, 2722}
public void test1399() {
	check(//
"Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^n, x]", //
 "-((Cos[c + d*x - ArcTan[a, b]]^(1 + n)*Hypergeometric2F1[1/2, (1 + n)/2, (3 + n)/2, Cos[c + d*x - ArcTan[a, b]]^2]*(a*Cos[c + d*x] + b*Sin[c + d*x])^n*Sin[c + d*x - ArcTan[a, b]])/(d*(1 + n)*((a*Cos[c + d*x] + b*Sin[c + d*x])/Sqrt[a^2 + b^2])^n*Sqrt[Sin[c + d*x - ArcTan[a, b]]^2]))", //
3157, 2722);}

// {3156, 2722}
public void test1400() {
	check(//
"Integrate[(2*Cos[c + d*x] + 3*Sin[c + d*x])^n, x]", //
 "-((13^(n/2)*Cos[c + d*x - ArcTan[3/2]]^(1 + n)*Hypergeometric2F1[1/2, (1 + n)/2, (3 + n)/2, Cos[c + d*x - ArcTan[3/2]]^2]*Sin[c + d*x - ArcTan[3/2]])/(d*(1 + n)*Sqrt[Sin[c + d*x - ArcTan[3/2]]^2]))", //
3156, 2722);}

// {3151}
public void test1401() {
	check(//
"Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^3, x]", //
 "-(((a^2 + b^2)*(b*Cos[c + d*x] - a*Sin[c + d*x]))/d) + (b*Cos[c + d*x] - a*Sin[c + d*x])^3/(3*d)", //
3151);}

// {3152, 8}
public void test1402() {
	check(//
"Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^2, x]", //
 "((a^2 + b^2)*x)/2 - ((b*Cos[c + d*x] - a*Sin[c + d*x])*(a*Cos[c + d*x] + b*Sin[c + d*x]))/(2*d)", //
3152, 8);}

// {3153, 212}
public void test1403() {
	check(//
"Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^(-1), x]", //
 "-(ArcTanh[(b*Cos[c + d*x] - a*Sin[c + d*x])/Sqrt[a^2 + b^2]]/(Sqrt[a^2 + b^2]*d))", //
3153, 212);}

// {3155, 3154}
public void test1404() {
	check(//
"Integrate[(a*Cos[c + d*x] + b*Sin[c + d*x])^(-4), x]", //
 "-(b*Cos[c + d*x] - a*Sin[c + d*x])/(3*(a^2 + b^2)*d*(a*Cos[c + d*x] + b*Sin[c + d*x])^3) + (2*Sin[c + d*x])/(3*a*(a^2 + b^2)*d*(a*Cos[c + d*x] + b*Sin[c + d*x]))", //
3155, 3154);}

// {3157, 2719}
public void test1405() {
	check(//
"Integrate[Sqrt[a*Cos[c + d*x] + b*Sin[c + d*x]], x]", //
 "(2*EllipticE[(c + d*x - ArcTan[a, b])/2, 2]*Sqrt[a*Cos[c + d*x] + b*Sin[c + d*x]])/(d*Sqrt[(a*Cos[c + d*x] + b*Sin[c + d*x])/Sqrt[a^2 + b^2]])", //
3157, 2719);}

// {3157, 2720}
public void test1406() {
	check(//
"Integrate[1/Sqrt[a*Cos[c + d*x] + b*Sin[c + d*x]], x]", //
 "(2*EllipticF[(c + d*x - ArcTan[a, b])/2, 2]*Sqrt[(a*Cos[c + d*x] + b*Sin[c + d*x])/Sqrt[a^2 + b^2]])/(d*Sqrt[a*Cos[c + d*x] + b*Sin[c + d*x]])", //
3157, 2720);}

// {3156, 2719}
public void test1407() {
	check(//
"Integrate[Sqrt[2*Cos[c + d*x] + 3*Sin[c + d*x]], x]", //
 "(2*13^(1/4)*EllipticE[(c + d*x - ArcTan[3/2])/2, 2])/d", //
3156, 2719);}

// {3156, 2720}
public void test1408() {
	check(//
"Integrate[1/Sqrt[2*Cos[c + d*x] + 3*Sin[c + d*x]], x]", //
 "(2*EllipticF[(c + d*x - ArcTan[3/2])/2, 2])/(13^(1/4)*d)", //
3156, 2720);}

// {30}
public void test1409() {
	check(//
"Integrate[(Csc[x] - Sin[x])^(-2), x]", //
 "Tan[x]^3/3", //
30);}

// {30}
public void test1411() {
	check(//
"Integrate[(-Cos[x] + Sec[x])^(-2), x]", //
 "-Cot[x]^3/3", //
30);}

// {3195, 3193}
public void test1413() {
	check(//
"Integrate[(Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x])^(-2), x]", //
 "-(c*Cos[d + e*x] - b*Sin[d + e*x])/(3*Sqrt[b^2 + c^2]*e*(Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x])^2) - (c - Sqrt[b^2 + c^2]*Sin[d + e*x])/(3*c*Sqrt[b^2 + c^2]*e*(c*Cos[d + e*x] - b*Sin[d + e*x]))", //
3195, 3193);}

// {3203, 31}
public void test1414() {
	check(//
"Integrate[(2*a + 2*a*Cos[d + e*x] + 2*c*Sin[d + e*x])^(-1), x]", //
 "Log[a + c*Tan[(d + e*x)/2]]/(2*c*e)", //
3203, 31);}

// {3203, 31}
public void test1415() {
	check(//
"Integrate[(2*a + 2*a*Cos[d + e*x] + 2*a*Sin[d + e*x])^(-1), x]", //
 "Log[1 + Tan[(d + e*x)/2]]/(2*a*e)", //
3203, 31);}

// {3200, 31}
public void test1416() {
	check(//
"Integrate[(2*a - 2*a*Cos[d + e*x] + 2*c*Sin[d + e*x])^(-1), x]", //
 "-Log[a + c*Cot[(d + e*x)/2]]/(2*c*e)", //
3200, 31);}

// {3202, 31}
public void test1417() {
	check(//
"Integrate[(2*a + 2*b*Cos[d + e*x] + 2*a*Sin[d + e*x])^(-1), x]", //
 "-Log[a + b*Cot[d/2 + Pi/4 + (e*x)/2]]/(2*b*e)", //
3202, 31);}

// {3201, 31}
public void test1418() {
	check(//
"Integrate[(2*a + 2*b*Cos[d + e*x] - 2*a*Sin[d + e*x])^(-1), x]", //
 "Log[a + b*Tan[d/2 + Pi/4 + (e*x)/2]]/(2*b*e)", //
3201, 31);}

// {3197, 2732}
public void test1419() {
	check(//
"Integrate[Sqrt[2 + 3*Cos[d + e*x] + 5*Sin[d + e*x]], x]", //
 "(2*Sqrt[2 + Sqrt[34]]*EllipticE[(d + e*x - ArcTan[5/3])/2, (2*(17 - Sqrt[34]))/15])/e", //
3197, 2732);}

// {3205, 2740}
public void test1420() {
	check(//
"Integrate[1/Sqrt[2 + 3*Cos[d + e*x] + 5*Sin[d + e*x]], x]", //
 "(2*EllipticF[(d + e*x - ArcTan[5/3])/2, (2*(17 - Sqrt[34]))/15])/(Sqrt[2 + Sqrt[34]]*e)", //
3205, 2740);}

// {3198, 2732}
public void test1421() {
	check(//
"Integrate[Sqrt[a + b*Cos[d + e*x] + c*Sin[d + e*x]], x]", //
 "(2*EllipticE[(d + e*x - ArcTan[b, c])/2, (2*Sqrt[b^2 + c^2])/(a + Sqrt[b^2 + c^2])]*Sqrt[a + b*Cos[d + e*x] + c*Sin[d + e*x]])/(e*Sqrt[(a + b*Cos[d + e*x] + c*Sin[d + e*x])/(a + Sqrt[b^2 + c^2])])", //
3198, 2732);}

// {3206, 2740}
public void test1422() {
	check(//
"Integrate[1/Sqrt[a + b*Cos[d + e*x] + c*Sin[d + e*x]], x]", //
 "(2*EllipticF[(d + e*x - ArcTan[b, c])/2, (2*Sqrt[b^2 + c^2])/(a + Sqrt[b^2 + c^2])]*Sqrt[(a + b*Cos[d + e*x] + c*Sin[d + e*x])/(a + Sqrt[b^2 + c^2])])/(e*Sqrt[a + b*Cos[d + e*x] + c*Sin[d + e*x]])", //
3206, 2740);}

// {3192, 3191}
public void test1423() {
	check(//
"Integrate[(5 + 4*Cos[d + e*x] + 3*Sin[d + e*x])^(3/2), x]", //
 "(-40*(3*Cos[d + e*x] - 4*Sin[d + e*x]))/(3*e*Sqrt[5 + 4*Cos[d + e*x] + 3*Sin[d + e*x]]) - (2*(3*Cos[d + e*x] - 4*Sin[d + e*x])*Sqrt[5 + 4*Cos[d + e*x] + 3*Sin[d + e*x]])/(3*e)", //
3192, 3191);}

// {3192, 3191}
public void test1424() {
	check(//
"Integrate[(-5 + 4*Cos[d + e*x] + 3*Sin[d + e*x])^(3/2), x]", //
 "(40*(3*Cos[d + e*x] - 4*Sin[d + e*x]))/(3*e*Sqrt[-5 + 4*Cos[d + e*x] + 3*Sin[d + e*x]]) - (2*(3*Cos[d + e*x] - 4*Sin[d + e*x])*Sqrt[-5 + 4*Cos[d + e*x] + 3*Sin[d + e*x]])/(3*e)", //
3192, 3191);}

// {3192, 3191}
public void test1425() {
	check(//
"Integrate[(Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x])^(3/2), x]", //
 "(-8*Sqrt[b^2 + c^2]*(c*Cos[d + e*x] - b*Sin[d + e*x]))/(3*e*Sqrt[Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x]]) - (2*(c*Cos[d + e*x] - b*Sin[d + e*x])*Sqrt[Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x]])/(3*e)", //
3192, 3191);}

// {3192, 3191}
public void test1426() {
	check(//
"Integrate[(-Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x])^(3/2), x]", //
 "(8*Sqrt[b^2 + c^2]*(c*Cos[d + e*x] - b*Sin[d + e*x]))/(3*e*Sqrt[-Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x]]) - (2*(c*Cos[d + e*x] - b*Sin[d + e*x])*Sqrt[-Sqrt[b^2 + c^2] + b*Cos[d + e*x] + c*Sin[d + e*x]])/(3*e)", //
3192, 3191);}

// {4465, 8}
public void test1427() {
	check(//
"Integrate[(Cos[x]^2 + Sin[x]^2)^(-1), x]", //
 "x", //
4465, 8);}

// {4465, 8}
public void test1428() {
	check(//
"Integrate[(Cos[x]^2 + Sin[x]^2)^(-2), x]", //
 "x", //
4465, 8);}

// {4465, 8}
public void test1429() {
	check(//
"Integrate[(Cos[x]^2 + Sin[x]^2)^(-3), x]", //
 "x", //
4465, 8);}

// {212}
public void test1430() {
	check(//
"Integrate[(Cos[x]^2 - Sin[x]^2)^(-1), x]", //
 "ArcTanh[2*Cos[x]*Sin[x]]/2", //
212);}

// {391}
public void test1431() {
	check(//
"Integrate[(Cos[x]^2 - Sin[x]^2)^(-2), x]", //
 "Tan[x]/(1 - Tan[x]^2)", //
391);}

// {209}
public void test1432() {
	check(//
"Integrate[(Cos[x]^2 + a^2*Sin[x]^2)^(-1), x]", //
 "ArcTan[a*Tan[x]]/a", //
209);}

// {209}
public void test1433() {
	check(//
"Integrate[(b^2*Cos[x]^2 + Sin[x]^2)^(-1), x]", //
 "ArcTan[Tan[x]/b]/b", //
209);}

// {211}
public void test1434() {
	check(//
"Integrate[(b^2*Cos[x]^2 + a^2*Sin[x]^2)^(-1), x]", //
 "ArcTan[(a*Tan[x])/b]/(a*b)", //
211);}

// {209}
public void test1435() {
	check(//
"Integrate[(4*Cos[1 + 2*x]^2 + 3*Sin[1 + 2*x]^2)^(-1), x]", //
 "x/(2*Sqrt[3]) - ArcTan[(Cos[1 + 2*x]*Sin[1 + 2*x])/(3 + 2*Sqrt[3] + Cos[1 + 2*x]^2)]/(4*Sqrt[3])", //
209);}

// {4466, 8}
public void test1436() {
	check(//
"Integrate[(Sec[x]^2 - Tan[x]^2)^(-1), x]", //
 "x", //
4466, 8);}

// {4466, 8}
public void test1437() {
	check(//
"Integrate[(Sec[x]^2 - Tan[x]^2)^(-2), x]", //
 "x", //
4466, 8);}

// {4466, 8}
public void test1438() {
	check(//
"Integrate[(Sec[x]^2 - Tan[x]^2)^(-3), x]", //
 "x", //
4466, 8);}

// {4467, 8}
public void test1439() {
	check(//
"Integrate[(Cot[x]^2 - Csc[x]^2)^(-1), x]", //
 "-x", //
4467, 8);}

// {4467, 8}
public void test1440() {
	check(//
"Integrate[(Cot[x]^2 - Csc[x]^2)^(-2), x]", //
 "x", //
4467, 8);}

// {4467, 8}
public void test1441() {
	check(//
"Integrate[(Cot[x]^2 - Csc[x]^2)^(-3), x]", //
 "-x", //
4467, 8);}

// {211}
public void test1442() {
	check(//
"Integrate[(a + b*Cos[x]^2 + c*Sin[x]^2)^(-1), x]", //
 "ArcTan[(Sqrt[a + c]*Tan[x])/Sqrt[a + b]]/(Sqrt[a + b]*Sqrt[a + c])", //
211);}

// {3102, 2813}
public void test1443() {
	check(//
"Integrate[(a + b*Sin[d + e*x])*(b^2 + 2*a*b*Sin[d + e*x] + a^2*Sin[d + e*x]^2), x]", //
 "(a*(a^2 + 4*b^2)*x)/2 + ((a^4 - 8*a^2*b^2 - 3*b^4)*Cos[d + e*x])/(3*b*e) + (a*(a^2 - 6*b^2)*Cos[d + e*x]*Sin[d + e*x])/(6*e) - (a^2*Cos[d + e*x]*(a + b*Sin[d + e*x])^2)/(3*b*e)", //
3102, 2813);}

// {3371, 2813}
public void test1444() {
	check(//
"Integrate[(a + b*Sin[d + e*x])*Sqrt[b^2 + 2*a*b*Sin[d + e*x] + a^2*Sin[d + e*x]^2], x]", //
 "-(((a^2 + b^2)*Cos[d + e*x]*Sqrt[b^2 + 2*a*b*Sin[d + e*x] + a^2*Sin[d + e*x]^2])/(e*(b + a*Sin[d + e*x]))) + (3*a^2*b*x*Sqrt[b^2 + 2*a*b*Sin[d + e*x] + a^2*Sin[d + e*x]^2])/(2*(a*b + a^2*Sin[d + e*x])) - (a^2*b*Cos[d + e*x]*Sin[d + e*x]*Sqrt[b^2 + 2*a*b*Sin[d + e*x] + a^2*Sin[d + e*x]^2])/(2*e*(a*b + a^2*Sin[d + e*x]))", //
3371, 2813);}

// {2745, 2723}
public void test1445() {
	check(//
"Integrate[(a + b*Cos[c + d*x]*Sin[c + d*x])^2, x]", //
 "((8*a^2 + b^2)*x)/8 - (a*b*Cos[2*c + 2*d*x])/(2*d) - (b^2*Cos[2*c + 2*d*x]*Sin[2*c + 2*d*x])/(16*d)", //
2745, 2723);}

// {4482, 3877}
public void test1446() {
	check(//
"Integrate[Sec[2*(a + b*x)]*Sqrt[c*Tan[a + b*x]*Tan[2*(a + b*x)]], x]", //
 "(c*Tan[2*a + 2*b*x])/(b*Sqrt[-c + c*Sec[2*a + 2*b*x]])", //
4482, 3877);}

// {2747, 31}
public void test1447() {
	check(//
"Integrate[Sin[x]/(a + b*Cos[x]), x]", //
 "-(Log[a + b*Cos[x]]/b)", //
2747, 31);}

// {2747, 32}
public void test1448() {
	check(//
"Integrate[(a + b*Cos[x])^n*Sin[x], x]", //
 "-((a + b*Cos[x])^(1 + n)/(b*(1 + n)))", //
2747, 32);}

// {3269, 221}
public void test1449() {
	check(//
"Integrate[Sin[x]/Sqrt[1 + Cos[x]^2], x]", //
 "-ArcSinh[Cos[x]]", //
3269, 221);}

// {4420, 2717}
public void test1450() {
	check(//
"Integrate[Cos[Cos[x]]*Sin[x], x]", //
 "-Sin[Cos[x]]", //
4420, 2717);}

// {4420, 2718}
public void test1451() {
	check(//
"Integrate[Sin[3*x]*Sin[Cos[3*x]], x]", //
 "Cos[Cos[3*x]]/3", //
4420, 2718);}

// {4420, 2225}
public void test1452() {
	check(//
"Integrate[E^(n*Cos[a + b*x])*Sin[a + b*x], x]", //
 "-(E^(n*Cos[a + b*x])/(b*n))", //
4420, 2225);}

// {4420, 2225}
public void test1453() {
	check(//
"Integrate[E^(n*Cos[a*c + b*c*x])*Sin[c*(a + b*x)], x]", //
 "-(E^(n*Cos[c*(a + b*x)])/(b*c*n))", //
4420, 2225);}

// {4420, 2225}
public void test1454() {
	check(//
"Integrate[E^(n*Cos[c*(a + b*x)])*Sin[a*c + b*c*x], x]", //
 "-(E^(n*Cos[a*c + b*c*x])/(b*c*n))", //
4420, 2225);}

// {4424, 2209}
public void test1455() {
	check(//
"Integrate[E^(n*Cos[a + b*x])*Tan[a + b*x], x]", //
 "-(ExpIntegralEi[n*Cos[a + b*x]]/b)", //
4424, 2209);}

// {4424, 2209}
public void test1456() {
	check(//
"Integrate[E^(n*Cos[a*c + b*c*x])*Tan[c*(a + b*x)], x]", //
 "-(ExpIntegralEi[n*Cos[c*(a + b*x)]]/(b*c))", //
4424, 2209);}

// {4424, 2209}
public void test1457() {
	check(//
"Integrate[E^(n*Cos[c*(a + b*x)])*Tan[a*c + b*c*x], x]", //
 "-(ExpIntegralEi[n*Cos[a*c + b*c*x]]/(b*c))", //
4424, 2209);}

// {2747, 31}
public void test1458() {
	check(//
"Integrate[Cos[x]/(a + b*Sin[x]), x]", //
 "Log[a + b*Sin[x]]/b", //
2747, 31);}

// {2747, 32}
public void test1459() {
	check(//
"Integrate[Cos[x]*(a + b*Sin[x])^n, x]", //
 "(a + b*Sin[x])^(1 + n)/(b*(1 + n))", //
2747, 32);}

// {3269, 221}
public void test1460() {
	check(//
"Integrate[Cos[x]/Sqrt[1 + Sin[x]^2], x]", //
 "ArcSinh[Sin[x]]", //
3269, 221);}

// {3269, 222}
public void test1461() {
	check(//
"Integrate[Cos[x]/Sqrt[4 - Sin[x]^2], x]", //
 "ArcSin[Sin[x]/2]", //
3269, 222);}

// {3269, 222}
public void test1462() {
	check(//
"Integrate[Cos[3*x]/Sqrt[4 - Sin[3*x]^2], x]", //
 "ArcSin[Sin[3*x]/2]/3", //
3269, 222);}

// {3277, 267}
public void test1463() {
	check(//
"Integrate[Cos[x]*Sin[x]*Sqrt[1 + Sin[x]^2], x]", //
 "(1 + Sin[x]^2)^(3/2)/3", //
3277, 267);}

// {4419, 2717}
public void test1464() {
	check(//
"Integrate[Cos[x]*Cos[Sin[x]], x]", //
 "Sin[Sin[x]]", //
4419, 2717);}

// {4419, 3855}
public void test1465() {
	check(//
"Integrate[Cos[x]*Sec[Sin[x]], x]", //
 "ArcTanh[Sin[Sin[x]]]", //
4419, 3855);}

// {4419, 2240}
public void test1466() {
	check(//
"Integrate[(E^Sqrt[Sin[x]]*Cos[x])/Sqrt[Sin[x]], x]", //
 "2*E^Sqrt[Sin[x]]", //
4419, 2240);}

// {4419, 2225}
public void test1467() {
	check(//
"Integrate[E^(4 + Sin[x])*Cos[x], x]", //
 "E^(4 + Sin[x])", //
4419, 2225);}

// {4441, 2225}
public void test1468() {
	check(//
"Integrate[E^(Cos[x]*Sin[x])*Cos[2*x], x]", //
 "E^(Sin[2*x]/2)", //
4441, 2225);}

// {4441, 2225}
public void test1469() {
	check(//
"Integrate[E^(Cos[x/2]*Sin[x/2])*Cos[x], x]", //
 "2*E^(Sin[x]/2)", //
4441, 2225);}

// {4419, 2225}
public void test1470() {
	check(//
"Integrate[E^(n*Sin[a + b*x])*Cos[a + b*x], x]", //
 "E^(n*Sin[a + b*x])/(b*n)", //
4419, 2225);}

// {4419, 2225}
public void test1471() {
	check(//
"Integrate[E^(n*Sin[a*c + b*c*x])*Cos[c*(a + b*x)], x]", //
 "E^(n*Sin[c*(a + b*x)])/(b*c*n)", //
4419, 2225);}

// {4419, 2225}
public void test1472() {
	check(//
"Integrate[E^(n*Sin[c*(a + b*x)])*Cos[a*c + b*c*x], x]", //
 "E^(n*Sin[a*c + b*c*x])/(b*c*n)", //
4419, 2225);}

// {4423, 2209}
public void test1473() {
	check(//
"Integrate[E^(n*Sin[a + b*x])*Cot[a + b*x], x]", //
 "ExpIntegralEi[n*Sin[a + b*x]]/b", //
4423, 2209);}

// {4423, 2209}
public void test1474() {
	check(//
"Integrate[E^(n*Sin[a*c + b*c*x])*Cot[c*(a + b*x)], x]", //
 "ExpIntegralEi[n*Sin[c*(a + b*x)]]/(b*c)", //
4423, 2209);}

// {4423, 2209}
public void test1475() {
	check(//
"Integrate[E^(n*Sin[c*(a + b*x)])*Cot[a*c + b*c*x], x]", //
 "ExpIntegralEi[n*Sin[a*c + b*c*x]]/(b*c)", //
4423, 2209);}

// {3587, 31}
public void test1476() {
	check(//
"Integrate[Sec[x]^2/(a + b*Tan[x]), x]", //
 "Log[a + b*Tan[x]]/b", //
3587, 31);}

// {3756, 212}
public void test1477() {
	check(//
"Integrate[Sec[x]^2/(1 - Tan[x]^2), x]", //
 "ArcTanh[2*Cos[x]*Sin[x]]/2", //
3756, 212);}

// {3756, 209}
public void test1478() {
	check(//
"Integrate[Sec[x]^2/(9 + Tan[x]^2), x]", //
 "x/3 - ArcTan[(2*Cos[x]*Sin[x])/(1 + 2*Cos[x]^2)]/3", //
3756, 209);}

// {3587, 32}
public void test1479() {
	check(//
"Integrate[Sec[x]^2*(a + b*Tan[x])^n, x]", //
 "(a + b*Tan[x])^(1 + n)/(b*(1 + n))", //
3587, 32);}

// {4427, 267}
public void test1480() {
	check(//
"Integrate[(Sec[x]^2*Tan[x]^2)/(2 + Tan[x]^3)^2, x]", //
 "-1/(3*(2 + Tan[x]^3))", //
4427, 267);}

// {3091, 8}
public void test1481() {
	check(//
"Integrate[(1 + Cos[x]^2)*Sec[x]^2, x]", //
 "x + Tan[x]", //
3091, 8);}

// {4231, 222}
public void test1482() {
	check(//
"Integrate[Sec[x]^2/Sqrt[4 - Sec[x]^2], x]", //
 "ArcSin[Tan[x]/Sqrt[3]]", //
4231, 222);}

// {3756, 222}
public void test1483() {
	check(//
"Integrate[Sec[x]^2/Sqrt[1 - 4*Tan[x]^2], x]", //
 "ArcSin[2*Tan[x]]/2", //
3756, 222);}

// {4427, 2225}
public void test1484() {
	check(//
"Integrate[E^Tan[x]*Sec[x]^2, x]", //
 "E^Tan[x]", //
4427, 2225);}

// {3587, 31}
public void test1485() {
	check(//
"Integrate[Csc[x]^2/(a + b*Cot[x]), x]", //
 "-(Log[a + b*Cot[x]]/b)", //
3587, 31);}

// {3587, 32}
public void test1486() {
	check(//
"Integrate[(a + b*Cot[x])^n*Csc[x]^2, x]", //
 "-((a + b*Cot[x])^(1 + n)/(b*(1 + n)))", //
3587, 32);}

// {3091, 8}
public void test1487() {
	check(//
"Integrate[Csc[x]^2*(1 + Sin[x]^2), x]", //
 "x - Cot[x]", //
3091, 8);}

// {4429, 2225}
public void test1488() {
	check(//
"Integrate[Csc[x]^2/E^Cot[x], x]", //
 "E^(-Cot[x])", //
4429, 2225);}

// {4424, 209}
public void test1489() {
	check(//
"Integrate[(Sec[x]*Tan[x])/(1 + Sec[x]^2), x]", //
 "-ArcTan[Cos[x]]", //
4424, 209);}

// {4424, 209}
public void test1490() {
	check(//
"Integrate[(Sec[x]*Tan[x])/(9 + 4*Sec[x]^2), x]", //
 "-ArcTan[(3*Cos[x])/2]/6", //
4424, 209);}

// {4424, 31}
public void test1491() {
	check(//
"Integrate[(Sec[x]*Tan[x])/(Sec[x] + Sec[x]^2), x]", //
 "-Log[1 + Cos[x]]", //
4424, 31);}

// {270}
public void test1492() {
	check(//
"Integrate[(Sec[x]*Tan[x])/Sqrt[1 + Cos[x]^2], x]", //
 "Sqrt[1 + Cos[x]^2]*Sec[x]", //
270);}

// {4424, 2240}
public void test1493() {
	check(//
"Integrate[E^Sec[x]*Sec[x]*Tan[x], x]", //
 "E^Sec[x]", //
4424, 2240);}

// {4424, 2240}
public void test1494() {
	check(//
"Integrate[2^Sec[x]*Sec[x]*Tan[x], x]", //
 "2^Sec[x]/Log[2]", //
4424, 2240);}

// {4424, 267}
public void test1495() {
	check(//
"Integrate[(Sec[2*x]*Tan[2*x])/(1 + Sec[2*x])^(3/2), x]", //
 "-(1/Sqrt[1 + Sec[2*x]])", //
4424, 267);}

// {270}
public void test1496() {
	check(//
"Integrate[(Sec[3*x]*Tan[3*x])/Sqrt[1 + 5*Cos[3*x]^2], x]", //
 "(Sqrt[1 + 5*Cos[3*x]^2]*Sec[3*x])/3", //
270);}

// {4423, 2240}
public void test1497() {
	check(//
"Integrate[5^Csc[3*x]*Cot[3*x]*Csc[3*x], x]", //
 "-5^Csc[3*x]/(3*Log[5])", //
4423, 2240);}

// {4423, 209}
public void test1498() {
	check(//
"Integrate[(Cot[x]*Csc[x])/(1 + Csc[x]^2), x]", //
 "ArcTan[Sin[x]]", //
4423, 209);}

// {270}
public void test1499() {
	check(//
"Integrate[(Cot[x]*Csc[x])/Sqrt[1 + Sin[x]^2], x]", //
 "-(Csc[x]*Sqrt[1 + Sin[x]^2])", //
270);}

// {4269, 3556}
public void test1500() {
	check(//
"Integrate[x*Sec[x]^2, x]", //
 "Log[Cos[x]] + x*Tan[x]", //
4269, 3556);}

// {2645, 30}
public void test1501() {
	check(//
"Integrate[Sqrt[Cos[x]]*Sin[x], x]", //
 "(-2*Cos[x]^(3/2))/3", //
2645, 30);}

// {2320, 3556}
public void test1502() {
	check(//
"Integrate[Tan[E^(-2*x)]/E^(2*x), x]", //
 "Log[Cos[E^(-2*x)]]/2", //
2320, 3556);}

// {4269, 3556}
public void test1503() {
	check(//
"Integrate[x*Sec[3*x]^2, x]", //
 "Log[Cos[3*x]]/9 + (x*Tan[3*x])/3", //
4269, 3556);}

// {3833, 3556}
public void test1504() {
	check(//
"Integrate[x*Cot[x^2], x]", //
 "Log[Sin[x^2]]/2", //
3833, 3556);}

// {3269, 209}
public void test1505() {
	check(//
"Integrate[Cos[2*x]/(8 + Sin[2*x]^2), x]", //
 "ArcTan[Sin[2*x]/(2*Sqrt[2])]/(4*Sqrt[2])", //
3269, 209);}

// {3461, 2717}
public void test1506() {
	check(//
"Integrate[x*Cos[x^2], x]", //
 "Sin[x^2]/2", //
3461, 2717);}

// {3461, 2717}
public void test1507() {
	check(//
"Integrate[x^2*Cos[4*x^3], x]", //
 "Sin[4*x^3]/12", //
3461, 2717);}

// {3461, 2717}
public void test1508() {
	check(//
"Integrate[x^3*Cos[x^4], x]", //
 "Sin[x^4]/4", //
3461, 2717);}

// {3460, 2718}
public void test1509() {
	check(//
"Integrate[x*Sin[x^2/2], x]", //
 "-Cos[x^2/2]", //
3460, 2718);}

// {3832, 3556}
public void test1510() {
	check(//
"Integrate[x*Tan[1 + x^2], x]", //
 "-Log[Cos[1 + x^2]]/2", //
3832, 3556);}

// {6847, 4517}
public void test1511() {
	check(//
"Integrate[(x^2*Sin[2*x^3])/E^(3*x^3), x]", //
 "(-2*Cos[2*x^3])/(39*E^(3*x^3)) - Sin[2*x^3]/(13*E^(3*x^3))", //
6847, 4517);}

// {3460, 2718}
public void test1512() {
	check(//
"Integrate[x*Sin[1 + x^2], x]", //
 "-Cos[1 + x^2]/2", //
3460, 2718);}

// {3461, 2717}
public void test1513() {
	check(//
"Integrate[x*Cos[1 + x^2], x]", //
 "Sin[1 + x^2]/2", //
3461, 2717);}

// {3460, 2718}
public void test1514() {
	check(//
"Integrate[x^2*Sin[1 + x^3], x]", //
 "-Cos[1 + x^3]/3", //
3460, 2718);}

// {3377, 2717}
public void test1515() {
	check(//
"Integrate[(1 + x)*Sin[1 + x], x]", //
 "-((1 + x)*Cos[1 + x]) + Sin[1 + x]", //
3377, 2717);}

// {4419, 2717}
public void test1516() {
	check(//
"Integrate[Cos[x]*Cos[2*Sin[x]], x]", //
 "Sin[2*Sin[x]]/2", //
4419, 2717);}

// {4420, 266}
public void test1517() {
	check(//
"Integrate[(Cos[x]*Sin[x])/(1 + Cos[x]^2), x]", //
 "-Log[1 + Cos[x]^2]/2", //
4420, 266);}

// {4269, 3556}
public void test1518() {
	check(//
"Integrate[x*Csc[x]^2, x]", //
 "-(x*Cot[x]) + Log[Sin[x]]", //
4269, 3556);}

// {2746, 31}
public void test1519() {
	check(//
"Integrate[Sec[x]*(1 - Sin[x]), x]", //
 "Log[1 + Sin[x]]", //
2746, 31);}

// {2746, 31}
public void test1520() {
	check(//
"Integrate[(1 + Cos[x])*Csc[x], x]", //
 "Log[1 - Cos[x]]", //
2746, 31);}

// {3756, 391}
public void test1521() {
	check(//
"Integrate[Cos[x]^2*(1 - Tan[x]^2), x]", //
 "Cos[x]*Sin[x]", //
3756, 391);}

// {4419, 642}
public void test1522() {
	check(//
"Integrate[(Cos[x]*(-3 + 2*Sin[x]))/(2 - 3*Sin[x] + Sin[x]^2), x]", //
 "Log[2 - 3*Sin[x] + Sin[x]^2]", //
4419, 642);}

// {3339, 629}
public void test1523() {
	check(//
"Integrate[Cos[x]/(Sin[x] + Sin[x]^2), x]", //
 "Log[Sin[x]] - Log[1 + Sin[x]]", //
3339, 629);}

// {3460, 2718}
public void test1524() {
	check(//
"Integrate[x*Sin[2*x^2], x]", //
 "-Cos[2*x^2]/4", //
3460, 2718);}

// {3277, 267}
public void test1525() {
	check(//
"Integrate[-(Cos[1 - x]*Sin[1 - x]*Sqrt[1 + Sin[1 - x]^2]), x]", //
 "(1 + Sin[1 - x]^2)^(3/2)/3", //
3277, 267);}

// {2644, 30}
public void test1526() {
	check(//
"Integrate[Cos[(1 + 3*x)/2]*Sin[(1 + 3*x)/2]^3, x]", //
 "Sin[1/2 + (3*x)/2]^4/6", //
2644, 30);}

// {4289, 3855}
public void test1527() {
	check(//
"Integrate[x*Sec[5 - x^2], x]", //
 "-ArcTanh[Sin[5 - x^2]]/2", //
4289, 3855);}

// {4290, 3855}
public void test1528() {
	check(//
"Integrate[Csc[x^(-1)]/x^2, x]", //
 "ArcTanh[Cos[x^(-1)]]", //
4290, 3855);}

// {4419, 2225}
public void test1529() {
	check(//
"Integrate[2^Sin[x]*Cos[x], x]", //
 "2^Sin[x]/Log[2]", //
4419, 2225);}

// {4424, 267}
public void test1530() {
	check(//
"Integrate[Sec[x]*Sqrt[4 + 3*Sec[x]]*Tan[x], x]", //
 "(2*(4 + 3*Sec[x])^(3/2))/9", //
4424, 267);}

// {3460, 2718}
public void test1531() {
	check(//
"Integrate[x*Sin[x^2], x]", //
 "-Cos[x^2]/2", //
3460, 2718);}

// {3842, 3855}
public void test1532() {
	check(//
"Integrate[x*Sec[1 + x]*Tan[1 + x], x]", //
 "-ArcTanh[Sin[1 + x]] + x*Sec[1 + x]", //
3842, 3855);}

// {2702, 30}
public void test1533() {
	check(//
"Integrate[Sec[x]^(1 + m)*Sin[x], x]", //
 "Sec[x]^m/m", //
2702, 30);}

// {4269, 3556}
public void test1534() {
	check(//
"Integrate[(1 + 2*x)*Sec[1 + 2*x]^2, x]", //
 "Log[Cos[1 + 2*x]]/2 + ((1 + 2*x)*Tan[1 + 2*x])/2", //
4269, 3556);}

// {391}
public void test1535() {
	check(//
"Integrate[(-Csc[a + b*x]^2 + Sec[a + b*x]^2)/(Csc[a + b*x]^2 + Sec[a + b*x]^2), x]", //
 "-((Cos[a + b*x]*Sin[a + b*x])/b)", //
391);}

}
