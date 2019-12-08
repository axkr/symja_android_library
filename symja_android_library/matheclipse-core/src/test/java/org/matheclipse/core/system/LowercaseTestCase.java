package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.builtin.ConstantDefinitions;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Tests system.reflection classes
 */
public class LowercaseTestCase extends AbstractTestCase {

	public LowercaseTestCase(String name) {
		super(name);
	}

	public void test001() {
		// syntax error in relaxed mode
		// check("Sin[x]", "");
		check("1.2345678*^5", //
				"123456.8");
		check("1.2345678*^6", //
				"1.23457*10^6");
		check("1.2345678*^7", //
				"1.23457*10^7");
		check("1.23456789*^8", //
				"1.23457*10^8");
		// github #66
		Double d = Double.parseDouble("1231231236123216361256312631627.12312312");
		checkNumeric("1231231236123216361256312631627.12312312", //
				d.toString());
		checkNumeric("N(1231231236123216361256312631627.12312312,50)", //
				"1.23123123612321636125631263162712312312*10^30");

		check("f[[1,2]]", //
				"f[[1,2]]");
		check("-cos(x)", //
				"-Cos(x)");
		check("expand((a+b)^3)", //
				"a^3+3*a^2*b+3*a*b^2+b^3");
		check("expand((a+b)^8)", //
				"a^8+8*a^7*b+28*a^6*b^2+56*a^5*b^3+70*a^4*b^4+56*a^3*b^5+28*a^2*b^6+8*a*b^7+b^8");
		check("expand((a+b+c)^3)", //
				"a^3+3*a^2*b+3*a*b^2+b^3+3*a^2*c+6*a*b*c+3*b^2*c+3*a*c^2+3*b*c^2+c^3");
	}

	public void testAbort() {
		check("Print(\"a\"); Abort(); Print(\"b\")", //
				"$Aborted");
	}

	public void testAbs() {
		check("Abs(x*Sign(x))", //
				"Abs(x*Sign(x))");
		check("Abs(Abs(x))", //
				"Abs(x)");
		check("Abs(E-Pi)", //
				"-E+Pi");
		check("Abs(x^2)", //
				"Abs(x)^2");
		check("Abs(1/2*x)", //
				"Abs(x)/2");
		check("Abs(1/2*E^(\\[ImaginaryJ]*\\[Pi]/4))", //
				"1/2");
		check("Abs(-x^(a + b))", //
				"Abs(x^(a+b))");
		check("Abs(-x)", //
				"Abs(x)");
		check("Abs(Conjugate(z))", //
				"Abs(z)");
		check("Abs(3*a*b*c)", //
				"3*Abs(a*b*c)");
		// check("Abs(x^(-3))", "1/Abs(x)^3");

		check("Abs((1+I)/Sqrt(2))", //
				"1");
		check("Abs(0)", //
				"0");
		check("Abs(10/3)", //
				"10/3");
		check("Abs(-10/3)", //
				"10/3");
		check("Abs(Indeterminate)", //
				"Indeterminate");
		check("Abs(Infinity)", //
				"Infinity");
		check("Abs(-1*Infinity)", //
				"Infinity");
		check("Abs(ComplexInfinity)", //
				"Infinity");
		check("Abs(I*Infinity)", //
				"Infinity");
		check("Abs(Sqrt(Pi))", //
				"Sqrt(Pi)");
		check("Abs(-3*Sqrt(Pi))", //
				"3*Sqrt(Pi)");
	}

	public void testAbsArg() {
		check("AbsArg(z)", //
				"{Abs(z),Arg(z)}");
		check("AbsArg(-2*z)", //
				"{2*Abs(z),Arg(-z)}");
		check("AbsArg(2*z)", //
				"{2*Abs(z),Arg(z)}");
		check("AbsArg({a, {b, c}})", //
				"{{Abs(a),Arg(a)},{{Abs(b),Arg(b)},{Abs(c),Arg(c)}}}");
		check("AbsArg({{1, -1, 0}, {0, 1}})", //
				"{{{1,0},{1,Pi},{0,0}},{{0,0},{1,0}}}");

		check("AbsArg(Gamma(-1/2))", //
				"{2*Sqrt(Pi),Pi}");

		check("AbsArg({1, I, 0})", //
				"{{1,0},{1,Pi/2},{0,0}}");
		check("AbsArg(z) /. z -> {1, I, 0}", //
				"{{1,1,0},{0,Pi/2,0}}");
	}

	public void testAccumulate() {
		check("Accumulate({{a, b}, {c, d}, {e, f}})", //
				"{{a,b},{a+c,b+d},{a+c+e,b+d+f}}");
		check("Accumulate({})", //
				"{}");
		check("Accumulate({a})", //
				"{a}");
		check("Accumulate({a, b})", //
				"{a,a+b}");
		check("Accumulate({a, b, c, d})", //
				"{a,a+b,a+b+c,a+b+c+d}");
		check("Accumulate(f(a, b, c, d))", //
				"f(a,a+b,a+b+c,a+b+c+d)");
	}

	public void testAddTo() {
		// print: AddTo: d is not a variable with a value, so its value cannot be changed.
		check("d += 7", //
				"d+=7");
		// print: AddTo: test is not a variable with a value, so its value cannot be changed.
		check("\"test\" += 7", //
				"test+=7");
		// print: Part: Part specification k[[2]] is longer than depth of object.
		check("k[[2]] += x", //
				"k[[2]]+=x");
		check("a = 10", //
				"10");
		check("a += 2", //
				"12");
		check("a", //
				"12");
	}

	public void testAdjacencyMatrix() {
		check("AdjacencyMatrix(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}))", //
				"{{0,1,1,0},\n" + //
						" {0,0,1,0},\n" + //
						" {0,0,0,0},\n" + //
						" {0,1,0,0}}");
		check("AdjacencyMatrix(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))", //
				"{{0,1,1},\n" + //
						" {1,0,1},\n" + //
						" {1,1,0}}");
		check("AdjacencyMatrix(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
				"{{0,1,0},\n" + //
						" {0,0,1},\n" + //
						" {1,0,0}}");
	}

	public void testAllTrue() {
		check("AllTrue({}, EvenQ)", //
				"True");
		check("AllTrue({1, 2, 3, 4, 5, 6}, EvenQ)", //
				"False");
		check("AllTrue({2, 4, 6, 8}, EvenQ)", //
				"True");
		check("AllTrue({2, 6, x, 4, y}, # < 10 &)", //
				"x<10&&y<10");
		check("AllTrue({12, 16, x, 14, y}, TrueQ(# < 10) &)", //
				"False");
		check("AllTrue(f(1, 7, 3), OddQ)", //
				"True");
	}

	public void testAiryAi() {
		checkNumeric("AiryAi(1.8)", //
				"0.04703621686684607");
		checkNumeric("AiryAi(2.0)", //
				"0.03492413042327471");
		checkNumeric("AiryAi(2.5+I)", //
				"-0.001912089270597423+I*(-0.018032905765072448)");
		checkNumeric("AiryAi({1.2, 1.5, 1.8})", //
				"{0.10612576226331269,0.07174949700810541,0.04703621686684607}");
	}

	public void testAiryAiPrime() {
		checkNumeric("AiryAiPrime(0.5)", //
				"-0.224910532664684");
		checkNumeric("AiryAiPrime(2.5)", //
				"-0.026250881035902635");
		checkNumeric("AiryAiPrime(2.5+I)", //
				"-0.001879208610072348+I*0.031027624283794165");
		checkNumeric("AiryAiPrime({1.2, 1.5, 1.8})", //
				"{-0.13278537855722639,-0.0973820128423013,-0.06852478011861125}");
	}

	public void testAiryBi() {
		checkNumeric("AiryBi(1.8)", //
				"2.595869356743907");
		checkNumeric("AiryBi(2.0)", //
				"3.2980949999782143");
		checkNumeric("AiryBi(2.5+I)", //
				"0.5125437840155836+I*5.3349955792709824");
		checkNumeric("AiryBi({1.2, 1.5, 1.8})", //
				"{1.421133675610348,1.8789415037478954,2.595869356743907}");
	}

	public void testAiryBiPrime() {
		checkNumeric("AiryBiPrime(1.8)", //
				"2.9855400508465997");
		checkNumeric("AiryBiPrime(2.5)", //
				"9.421423317334302");
		checkNumeric("AiryBiPrime(2.5+I)", //
				"-1.20504840498167+I*8.290971678095266");
		checkNumeric("AiryBiPrime({1.2, 1.5, 1.8})", //
				"{1.221231398704895,1.8862122548481661,2.9855400508465997}");
	}

	public void testAlternatives() {
		check("a+b+c+d/.(a|b)->t", //
				"c+d+2*t");
		check("a+b+c+d/.Except(b,(a|c))->t", //
				"b+d+2*t");
	}

	public void testAnd() {
		check("True && True && False", //
				"False");
		check("a && b && True && c", //
				"a&&b&&c");
		check("And()", //
				"True");
		check("And(4)", //
				"4");
		check("2 > 1 && Pi > 3", //
				"True");
		check("a && b && ! c", //
				"a&&b&&!c");
		check("x + 2*y == 3 && 4*x + 5*y == 6", //
				"x+2*y==3&&4*x+5*y==6");
		check("FullForm( And(x, And(y, z)) )", //
				"And(x, y, z)");
		check("And(x, True, z)", //
				"x&&z");
		check("And(x, False, z)", //
				"False");
	}

	public void testAntihermitianMatrixQ() {
		check("AntihermitianMatrixQ({{42, 7 + 11*I}, {-7 + 11*I, 21}})", //
				"True");
		check("AntihermitianMatrixQ({{I, 3 + 4*I}, {-3 + 4*I, 0}})", //
				"True");
		check("AntihermitianMatrixQ({{I, 3 + 4*I}, {3 + 4*I, 0}})", //
				"False");
		check("AntihermitianMatrixQ(({{I, a, b},  {-Conjugate[a], 0, c}, {-Conjugate[b],-Conjugate[c],-I} }))", //
				"True");
	}

	public void testAntisymmetricMatrixQ() {
		check("AntisymmetricMatrixQ({{42, 7 + 11*I}, {-7 + 11*I, 21}})", //
				"False");
		check("AntisymmetricMatrixQ({{0, -2, 3}, {2, 0, -4}, {-3, 4, 0}})", //
				"True");
	}

	public void testAngleVector() {
		check("AngleVector(x)", //
				"{Cos(x),Sin(x)}");
		check("AngleVector(Pi/6)", //
				"{Sqrt(3)/2,1/2}");
		check("AngleVector(90*Degree)", //
				"{0,1}");
		check("AngleVector({1, 10}, a)", //
				"{1+Cos(a),10+Sin(a)}");
	}

	public void testAnyTrue() {
		check("AnyTrue({}, EvenQ)", //
				"False");
		check("AnyTrue({1, 2, 3, 4, 5, 6}, EvenQ)", //
				"True");
		check("AnyTrue({1, 3, 5}, EvenQ)", //
				"False");
		check("AnyTrue({12, 16, x, 14, y}, # < 10 &)", //
				"x<10||y<10");
		check("AnyTrue({12, 16, x, 14, y}, TrueQ(# < 10) &)", //
				"False");
		check("AnyTrue(f(2, 7, 6), OddQ)", //
				"True");
	}

	public void testApart() {
		// check("Factor(x^2 - y^2 )", "(x-y)*(x+y)");
		// check("Solve(x^2 - y^2==0, y)", "{{y->-x},{y->x}}");

		check("Apart(1 / (x^2 - y^2), x)", //
				"1/(2*(x-y)*y)-1/(2*y*(x+y))");

		check("Apart(1/(a*b+a*c),x)", //
				"1/(a*(b+c))");

		// TODO return 1/b - a/(b*(a + b*x))
		check("Apart((x/(a+(b*x))),x)", //
				"x/(a+b*x)");

		check("Apart((3*x-8)/((x+1)*(x-5)),x)", //
				"7/6*1/(-5+x)+11/6*1/(1+x)");

		check("Apart((a + b)^3)", //
				"a^3+3*a^2*b+3*a*b^2+b^3");

		check("Apart(1 / (x^2 - y^2))", //
				"1/(x^2-y^2)");

		check("Apart(x/(2*x + a^2))", //
				"x/(a^2+2*x)");

		check("Apart(y/(x + 2)/(x + 1),x)", //
				"y/(1+x)-y/(2+x)");

		check("Sin(1 / (x ^ 2 - y ^ 2)) // Apart", //
				"Sin(1/(x^2-y^2))");

		check("Apart(1 / (x^2 + 5*x + 6))", //
				"1/(2+x)-1/(3+x)");
		check("Apart(1 / (x^2 - y^2), x)", //
				"1/(2*(x-y)*y)-1/(2*y*(x+y))");

		check("Apart(1/((1 + x)*(5 + x)))", //
				"1/(4*(1+x))-1/(4*(5+x))");
		check("Apart(1 < (x + 1)/(x - 1) < 2)", //
				"1<1+2/(-1+x)<2");

		check("Apart(1/((1 + x)*(5 + x)))", //
				"1/(4*(1+x))-1/(4*(5+x))");
		check("Apart((x)/(x^2-1))", //
				"1/(2*(-1+x))+1/(2*(1+x))");
		check("Apart((x+3)/(x^2-3*x-40))", //
				"11/13*1/(-8+x)+2/13*1/(5+x)");
		check("Apart((10*x^2+12*x+20)/(x^3-8))", //
				"7/(-2+x)+(4+3*x)/(4+2*x+x^2)");
		check("Apart((3*x+5)*(1-2*x)^(-2))", //
				"13/2*1/(1-2*x)^2+3/2*1/(-1+2*x)");
		check("Apart((10*x^2+12*x+20)/(x^3-8))", //
				"7/(-2+x)+(4+3*x)/(4+2*x+x^2)");
		check("Apart((10*x^2-63*x+29)/((x+2)*(x+3)^5))", //
				"195/(2+x)-308/(3+x)^5-185/(3+x)^4-195/(3+x)^3-195/(3+x)^2-195/(3+x)");
	}

	public void testAppend() {
		check("Append({1, 2, 3}, 4) ", //
				"{1,2,3,4}");
		check("Append(f(a, b), c)", //
				"f(a,b,c)");
		check("Append({a, b}, {c, d})  ", //
				"{a,b,{c,d}}");
		check("Append(a, b)", //
				"Append(a,b)");
	}

	public void testAppendTo() {
		check("s = {}", //
				"{}");
		check("AppendTo(s, 1)", //
				"{1}");
		check("s", //
				"{1}");

		check("y = f()", //
				"f()");
		check("AppendTo(y, x)", //
				"f(x)");
		check("y", //
				"f(x)");

		check("AppendTo({}, 1)", //
				"AppendTo({},1)");
		check("AppendTo(a, b)", //
				"AppendTo(a,b)");

		check("$l = {1, 2, 4, 9};appendto($l, 16)", //
				"{1,2,4,9,16}");
		check("$l = {1, 2, 4, 9};appendto($l, 16);$l", //
				"{1,2,4,9,16}");
	}

	public void testApply() {
		check("Apply(f)[p(x, y)]", //
				"f(x,y)");
		check("Apply(f)[aaa]", //
				"aaa");
		//
		check("Apply(f, p[x][q[y]], {1}, Heads -> True)", //
				"f(x)[f(y)]");
		check("Apply(f, p[x][q[y]], {1}, Heads -> False)", //
				"p(x)[f(y)]");
		check("Apply(f, p[x][q[y]], {1})", //
				"p(x)[f(y)]");
		check("Apply(f, 1+2+3, {1}, Heads -> True)", //
				"6");
		check("List @@@ (1+2+3)", //
				"6");

		check("f@ g@ h@ i", //
				"f(g(h(i)))");

		// github issue #40
		check("((#+##&) @@#&) /@{{1,2},{2,2,2},{3,4}}", //
				"{4,8,10}");

		check("Times @@ {1, 2, 3, 4}", //
				"24");
		check("f @@ {{a, b}, {c}, d}", //
				"f({a,b},{c},d)");
		check("apply(head, {3,4,5})", //
				"Symbol");
		check("apply(f, a)", //
				"a");
		check("apply(f, {a, \"string\", 3}, {-1})", //
				"{a,string,3}");
		check("table(i0^j, ##) & @@ {{i0, 3}, {j, 4}}", //
				"{{1,1,1,1},{2,4,8,16},{3,9,27,81}}");

		check("apply(f, {{a, b, c}, {d, e}})", //
				"f({a,b,c},{d,e})");
		check("apply(f, {{a, b, c}, {d, e}}, {1})", //
				"{f(a,b,c),f(d,e)}");
		check("apply(f, {{a, b, c}, {d, e}}, {0, 1})", //
				"f(f(a,b,c),f(d,e))");
		// Apply down to level 2 (excluding level 0):
		check("apply(f, {{{{{a}}}}}, 2)", //
				"{f(f({{a}}))}");

		check("apply(f, {{{{{a}}}}}, {0, 2})", //
				"f(f(f({{a}})))");
		check("apply(f, {{{{{a}}}}}, Infinity)", //
				"{f(f(f(f(a))))}");
		check("apply(f, {{{{{a}}}}}, {0, Infinity})", //
				"f(f(f(f(f(a)))))");

		check("apply(f, {{{{{a}}}}}, -1)", //
				"{f(f(f(f(a))))}");
		check("apply(f, {{{{{a}}}}}, -2)", //
				"{f(f(f(f(a))))}");
		check("apply(f, {{{{{a}}}}}, -3)", //
				"{f(f(f({a})))}");

		check("apply(f, {{{{{a}}}}}, {2, -3})", //
				"{{f(f({a}))}}");
		check("apply(f, h0(h1(h2(h3(h4(a))))), {2, -3})", //
				"h0(h1(f(f(h4(a)))))");

		check("Apply(List,1+2+3)", //
				"6");
		check("f @@ {1, 2, 3}", //
				"f(1,2,3)");
		check("Plus @@ {1, 2, 3}", //
				"6");
		check("f @@ (a + b + c)", //
				"f(a,b,c)");
		check("Apply(f, {a + b, g(c, d, e * f), 3}, {1})", //
				"{f(a,b),f(c,d,e*f),3}");
		check("f @@@ {a + b, g(c, d, e * f), 3}", //
				"{f(a,b),f(c,d,e*f),3}");
		check("Apply(f, {a, b, c}, {0})", //
				"f(a,b,c)");
		check("Apply(f, {{{{{a}}}}}, {2, -3})", //
				"{{f(f({a}))}}");
		check("Apply(List, a + b * c ^ e * f(g), {0, Infinity})", //
				"{a,{b,{c,e},{g}}}");
		check("Apply(f, {a, b, c}, x+y)", //
				"Apply(f,{a,b,c},x+y)");
	}

	public void testArcCos() {
		check("ArcCos(Cos(-1/2))", //
				"1/2");
		check("ArcCos(Cos(-1))", //
				"1");
		check("ArcCos(Cos(1))", //
				"1");
		check("ArcCos(Cos(-42))", //
				"-42+14*Pi");
		check("ArcCos(Cos(42))", //
				"-42+14*Pi");
		check("ArcCos(Cos(Pi/4))", //
				"Pi/4");
		check("ArcCos(Cos(5))", //
				"-5+2*Pi");

		check("ArcCos(0)", //
				"Pi/2");
		check("ArcCos(1)", //
				"0");
		check("Integrate(ArcCos(x), {x, -1, 1})", //
				"Pi");

		check("arccos(-11)", //
				"ArcCos(-11)");
		check("arccos(-x)", //
				"ArcCos(-x)");
		check("D(ArcCos(x),x)", //
				"-1/Sqrt(1-x^2)");
		check("diff(ArcCos(x),x)", //
				"-1/Sqrt(1-x^2)");
	}

	public void testArcCosh() {
		check("ArcCosh(0)", //
				"I*1/2*Pi");
		checkNumeric("ArcCosh(0.0)", //
				"I*1.5707963267948966");
		checkNumeric("ArcCosh(1.4)", //
				"0.867014726490565");
		check("ArcCosh(-x)", //
				"ArcCosh(-x)");
		check("D(ArcCosh(x),x)", //
				"1/Sqrt(-1+x^2)");
		check("ArcCosh(-Infinity)", //
				"Infinity");
		check("ArcCosh(I*Infinity)", //
				"Infinity");
	}

	public void testArcCot() {
		check("ArcCot(Cot(-1/2))", //
				"-1/2");
		check("ArcCot(Cot(-1))", //
				"-1");
		check("ArcCot(Cot(1))", //
				"1");
		check("ArcCot(Cot(-42))", //
				"-42+13*Pi");
		check("ArcCot(Cot(42))", //
				"42-13*Pi");
		check("ArcCot(Cot(Pi/4))", //
				"Pi/4");
		check("ArcCot(Cot(5))", //
				"5-2*Pi");

		check("ArcCot(0)", //
				"Pi/2");
		check("ArcCot(1)", //
				"Pi/4");

		check("arccot(complexinfinity)", //
				"0");
		check("arccot(0)", //
				"Pi/2");
		check("arccot(-11)", //
				"-ArcCot(11)");
		check("arccot(-x)", //
				"-ArcCot(x)");
		check("D(ArcCot(x),x)", //
				"-1/(1+x^2)");
	}

	public void testArcCoth() {
		check("ArcCoth(0)", //
				"I*1/2*Pi");
		// TODO fails in bitbucket pipelines
		// check("ArcCoth(0.0)", "I*1.5707963267948966");
		// check("ArcCoth(0.5)", "0.5493061443340549+I*(-1.5707963267948966)");
		check("ArcCoth(-x)", //
				"-ArcCoth(x)");
		check("ArcCoth(-1)", //
				"-Infinity");
		check("D(ArcCoth(x),x)", //
				"1/(1-x^2)");
	}

	public void testArcCsc() {
		check("ArcCsc(3.5)", //
				"0.289752");
		check("ArcCsc(1.0+3.5*I)", //
				"0.073021+I*(-0.261854)");
		check("ArcCsc(1)", //
				"Pi/2");
		check("ArcCsc(-1)", //
				"-Pi/2");
		check("arccsc(0)", //
				"ComplexInfinity");
		check("arccsc(-x)", //
				"-ArcCsc(x)");
		check("D(ArcCsc(x),x)", //
				"-1/(Sqrt(1-1/x^2)*x^2)");
	}

	public void testArcCsch() {
		check("arccsch(0)", //
				"ComplexInfinity");
		checkNumeric("ArcCsch(1.0)", //
				"0.8813735870195429");
		check("ArcCsch(-Infinity)", //
				"0");

		check("arccsch(-x)", //
				"-ArcCsch(x)");
		check("diff(ArcCsch(x),x)", //
				"-1/(Sqrt(1+x^2)*Abs(x))");
	}

	public void testArcSec() {
		check("ArcSec(3.5)", //
				"1.28104");
		check("ArcSec(1.0+3.5*I)", //
				"1.49778+I*0.261854");
		check("ArcSec(1)", //
				"0");
		check("ArcSec(-1)", //
				"Pi");
		check("ArcSec(0)", //
				"ComplexInfinity");
		check("ArcSec(-x)", //
				"ArcSec(-x)");
		check("diff(ArcSec(x),x)", //
				"1/(Sqrt(1-1/x^2)*x^2)");
	}

	public void testArcSech() {
		check("ArcSech(0)", //
				"Infinity");
		check("ArcSech(0.0)", //
				"Indeterminate");
		check("ArcSech(1)", //
				"0");
		checkNumeric("ArcSech(0.5)", //
				"1.3169578969248166");
		check("ArcSech(-x)", //
				"ArcSech(-x)");
		check("ArcSech(-2)", //
				"I*2/3*Pi");
		check("D(ArcSech(x),x)", //
				"-1/(x*Sqrt(1-x^2))");
	}

	public void testArcSin() {
		check("ArcSin(Sin(-1/2))", //
				"-1/2");
		check("ArcSin(Sin(-1))", //
				"-1");
		check("ArcSin(Sin(1))", //
				"1");
		check("ArcSin(Sin(-42))", //
				"42-13*Pi");
		check("ArcSin(Sin(42))", //
				"-42+13*Pi");
		check("ArcSin(Sin(Pi/4))", //
				"Pi/4");
		check("ArcSin(Sin(5))", //
				"5-2*Pi");

		check("-3*ArcSin(x)-2*ArcCos(x)", //
				"-Pi-ArcSin(x)");
		check("-ArcSin(x)-2*ArcCos(x)", //
				"-Pi/2-ArcCos(x)");
		check("-5*ArcSin(x)-5*ArcCos(x)", //
				"-5/2*Pi");
		check("ArcSin(x)+ArcCos(x)", //
				"Pi/2");
		check("5*ArcSin(x)+5*ArcCos(x)", //
				"5/2*Pi");
		check("ArcSin(0)", //
				"0");
		check("ArcSin(1)", //
				"Pi/2");
		check("arcsin(-11)", //
				"-ArcSin(11)");
		check("arcsin(-x)", //
				"-ArcSin(x)");
		check("diff(ArcSin(x),x)", //
				"1/Sqrt(1-x^2)");
	}

	public void testArcSinh() {
		check("ArcSinh(0)", //
				"0");
		check("ArcSinh(0.0)", //
				"0.0");
		checkNumeric("ArcSinh(1.0)", //
				"0.8813735870195429");
		// check("ArcSinh(-x)", "-ArcSinh(x)");
		check("diff(ArcSinh(x),x)", //
				"1/Sqrt(1+x^2)");
	}

	public void testArcTan() {
		// github #110 avoid infinite recursion
		// check("ArcTan(Re(Sin(3+I*2)),Im(Sin(3+I*2)))", //
		// "ArcTan(Im(Sin(3+I*2))/Re(Sin(3+I*2)))");

		check("ArcTan(0, Pi)", //
				"Pi/2");
		check("ArcTan(0, -Pi/3)", //
				"-Pi/2");
		check("ArcTan(1, 0)", //
				"0");
		check("ArcTan(1/2, 1/2)", //
				"Pi/4");
		check("ArcTan(0, 1)", //
				"Pi/2");
		check("ArcTan(-1/2, 1/2)", //
				"3/4*Pi");
		check("ArcTan(-1, 0)", //
				"Pi");
		check("ArcTan(-1/2, -1/2)", //
				"-3/4*Pi");
		check("ArcTan(0, -1)", //
				"-Pi/2");
		check("ArcTan(1/2, -1/2)", //
				"-Pi/4");

		check("ArcTan(Tan(-1/2))", //
				"-1/2");
		check("ArcTan(Tan(-1))", //
				"-1");
		check("ArcTan(Tan(1))", //
				"1");
		check("ArcTan(Tan(42))", //
				"42-13*Pi");
		check("ArcTan(Tan(Pi/4))", //
				"Pi/4");
		check("ArcTan(Re(9*Cos(5)+I*9*Sin(5)),Im(9*Cos(5)+I*9*Sin(5)))", //
				"5-2*Pi");
		check("ArcTan(-9*Sqrt(2),0)", //
				"Pi");
		check("ArcTan(0.7071067811865476)", //
				"0.61548");
		check("-ArcTan(x/(2*Sqrt(2)))/(2*Sqrt(2))", //
				"-ArcTan(x/(2*Sqrt(2)))/(2*Sqrt(2))");
		check("7*ArcTan(1/2) + a+ArcTan(1/3)", //
				"a+Pi/4+6*ArcTan(1/2)");
		check("ArcTan(1/3) + ArcTan(1/7)", //
				"ArcTan(1/2)");

		check("ArcTan(a, -a)", //
				"ArcTan(a,-a)");
		check("ArcTan(-a, a)", //
				"ArcTan(-a,a)");
		check("ArcTan(a, a)", //
				"ArcTan(a,a)");
		check("2*ArcTan(x)+4*ArcCot(x)", //
				"Pi+2*ArcCot(x)");
		check("7*ArcTan(x)+3*ArcCot(x)", //
				"3/2*Pi+4*ArcTan(x)");
		check("ArcTan(x)+ArcCot(x)", //
				"Pi/2");
		check("4*ArcTan(x)+4*ArcCot(x)", //
				"2*Pi");

		// issue #180
		check("ArcTan(1,Sqrt(3))", //
				"Pi/3");

		check("ArcTan(1)", //
				"Pi/4");
		checkNumeric("ArcTan(1.0)", //
				"0.7853981633974483");
		checkNumeric("ArcTan(-1.0)", //
				"-0.7853981633974483");

		check("ArcTan(0, 0)", //
				"Indeterminate");
		check("ArcTan(1, 1)", //
				"Pi/4");
		check("ArcTan(-1, 1)", //
				"3/4*Pi");
		check("ArcTan(1, -1)", //
				"-Pi/4");
		check("ArcTan(-1, -1)", //
				"-3/4*Pi");

		check("ArcTan(17, 0)", //
				"0");
		check("arctan(Infinity,y)", //
				"0");
		check("arctan(-Infinity,y)", //
				"Pi*(-1+2*UnitStep(Re(y)))");

		check("Abs( ArcTan(ComplexInfinity) )", //
				"Pi/2");
		check("arctan(infinity)", //
				"Pi/2");
		check("arctan(1)", //
				"Pi/4");
		check("arctan(-11)", //
				"-ArcTan(11)");
		check("arctan(-x)", //
				"-ArcTan(x)");
		check("arctan(1,1)", //
				"Pi/4");
		check("arctan(-1,-1)", //
				"-3/4*Pi");
		check("arctan(0,0)", //
				"Indeterminate");
		checkNumeric("arctan(1.0,1.0)", //
				"0.7853981633974483");
		checkNumeric("N(1/4*pi)", //
				"0.7853981633974483");
		check("D(ArcTan(x),x)", //
				"1/(1+x^2)");
	}

	public void testArcTanh() {

		check("ArcTanh(0)", //
				"0");
		check("ArcTanh(1)", //
				"Infinity");
		check("ArcTanh(2+I)", //
				"ArcTanh(2+I)");
		checkNumeric("ArcTanh(0.5 + 2*I)", //
				"0.09641562020299621+I*1.1265564408348223");

		check("ArcTanh(I)", //
				"I*1/4*Pi");
		check("ArcTanh(Infinity)", //
				"-I*1/2*Pi");
		check("ArcTanh(-Infinity)", //
				"I*1/2*Pi");
		check("ArcTanh(I*Infinity)", //
				"I*1/2*Pi");
		check("ArcTanh(ComplexInfinity)", //
				"Pi/2");

		check("ArcTanh(-x)", //
				"-ArcTanh(x)");
		check("D(ArcTanh(x),x)", //
				"1/(1-x^2)");
	}

	public void testArg() {
		check("Arg(E^(-42-5*I))", //
				"-5+2*Pi");
		check("Arg(E^(7+I*3))", //
				"3");
		check("Arg(E^(I*3))", //
				"3");
		check("Arg(E^I)", //
				"1");
		check("Arg(Sqrt(z))", //
				"Arg(z)/2");
		check("Arg(-2*z)", //
				"Arg(-z)");
		check("Arg(1.3)", //
				"0");
		check("Arg(2*z)", //
				"Arg(z)");
		check("Arg(x)", //
				"Arg(x)");
		check("Arg(Infinity)", //
				"0");
		check("Arg(-Infinity)", //
				"Pi");
		check("Arg(2 + I*Pi)", //
				"ArcTan(Pi/2)");
		check("Arg(1+I*Sqrt(3))", //
				"Pi/3");
		check("Arg(I-Sqrt(3))", //
				"5/6*Pi");
		// issue #179
		check("N(Arg(1+I*Sqrt(3)))", //
				"1.0472");

		check("Arg(Pi)", //
				"0");
		check("Arg(-Pi*E)", //
				"Pi");

		check("Arg(0)", //
				"0");
		check("Arg(1)", //
				"0");
		check("Arg(-1)", //
				"Pi");
		check("Arg(I)", //
				"Pi/2");
		check("Arg(1+I)", //
				"Pi/4");
		check("Arg(-I)", //
				"-Pi/2");
		check("Arg(-2*Sqrt(Pi))", //
				"Pi");
		check("Arg(Indeterminate)", //
				"Indeterminate");
		check("Arg(0)", //
				"0");
		check("Arg(10/3)", //
				"0");
		check("Arg(-10/3)", //
				"Pi");
		check("Arg(I*Infinity)", //
				"Pi/2");
		check("Arg(-I*Infinity)", //
				"-Pi/2");
		check("Arg(ComplexInfinity)", //
				"Interval({-Pi,Pi})");
	}

	public void testArgMax() {
		check("Maximize(-2*x^2 - 3*x + 5, x)", //
				"{49/8,{x->-3/4}}");
		check("ArgMax(-2*x^2 - 3*x + 5, x)", //
				"-3/4");
	}

	public void testArgMin() {
		check("Minimize(2*x^2 - 3*x + 5, x)", //
				"{31/8,{x->3/4}}");
		check("ArgMin(2*x^2 - 3*x + 5, x)", //
				"3/4");
	}

	public void testArray() {
		check("Array(Cos(#/4)&, 12, 4)", //
				"{Cos(1),Cos(5/4),Cos(3/2),Cos(7/4),Cos(2),Cos(9/4),Cos(5/2),Cos(11/4),Cos(3),Cos(\n"
						+ "13/4),Cos(7/2),Cos(15/4)}");
		check("Array(Cos(#/4)&, 12, 4, Min)", //
				"Cos(13/4)");
		check("Array(f, {2, 3}, 1, Plus)", //
				"f(1,1)+f(1,2)+f(1,3)+f(2,1)+f(2,2)+f(2,3)");
		check("Array(f, {2, 3}, {1, 2, 3})", //
				"Array(f,{2,3},{1,2,3})");
		check("Array(f, 4)", //
				"{f(1),f(2),f(3),f(4)}");
		check("Array(f, 10, 0)", //
				"{f(0),f(1),f(2),f(3),f(4),f(5),f(6),f(7),f(8),f(9)}");
		check("Array(f, {2, 3})", //
				"{{f(1,1),f(1,2),f(1,3)},{f(2,1),f(2,2),f(2,3)}}");
		check("Array(f, {2, 3}, {4, 6})", //
				"{{f(4,6),f(4,7),f(4,8)},{f(5,6),f(5,7),f(5,8)}}");
		check("Array(f, {2, 3}, {0, 4})", //
				"{{f(0,4),f(0,5),f(0,6)},{f(1,4),f(1,5),f(1,6)}}");

		// TODO implement other non-integer based iterators
		// check("Array[f, 10, {0, 1}]", "");
	}

	public void testArrayPad() {
		check("ArrayPad({a, b, c}, 1, x)", //
				"{x,a,b,c,x}");
		check("ArrayPad({{1, 2}, {3, 4}}, {1,2})", //
				"{{0,0,0,0,0},{0,1,2,0,0},{0,3,4,0,0},{0,0,0,0,0},{0,0,0,0,0}}");
		check("ArrayPad({{1, 2}, {3, 4}}, 2)", //
				"{{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,1,2,0,0},{0,0,3,4,0,0},{0,0,0,0,0,0},{0,0,0,0,\n" + "0,0}}");
		check("ArrayPad({1, 2, 3}, {2,4})", //
				"{0,0,1,2,3,0,0,0,0}");
		check("ArrayPad({1, 2, 3}, 1)", //
				"{0,1,2,3,0}");
		check("ArrayPad({1, 2, 3}, 2, x)", //
				"{x,x,1,2,3,x,x}");
	}

	public void testArrayDepth() {
		check("ArrayDepth({{a,b},{c,d}})", //
				"2");
		check("ArrayDepth(x)", //
				"0");
		check("ArrayDepth({{1, 2}, {3, 4}})", //
				"2");
		check("ArrayDepth({1, 2, 3, 4})", //
				"1");
		check("ArrayDepth({{a, b}, {c}})", //
				"1");
		check("ArrayDepth(f(f(a, b), f(c, d)))", //
				"2");
		check("ArrayDepth(Array(a, {4, 5, 2}))", //
				"3");
	}

	public void testArrayQ() {
		check("ArrayQ({1, 2, 3, 4})", //
				"True");
		check("ArrayQ({1, 2, {3}, 4})", //
				"False");
		check("ArrayQ({{1, 2}, {3}})", //
				"False");
		check("ArrayQ({{1, 2}, {3, 4}})", //
				"True");
		check("ArrayQ({1, 2, 3, 4}, 2)", //
				"False");
		check("ArrayQ({{1, 2}, {3, 4}},2)", //
				"True");
		check("ArrayQ({1, 2, 3, x}, 1, NumericQ)", //
				"False");
		check("ArrayQ({1, 2, 3, 4}, 1, NumericQ)", //
				"True");
		check("ArrayQ({{{E, 1}, {Pi, 2}}, {{Sin(1), Cos(2)}, {Sinh(1), Cosh(1)}}}, _, NumericQ)", //
				"True");
		check("ArrayQ({1, 2., E, Pi + I}, 1)", //
				"True");
		check("ArrayQ({{1,2},{3,4}},2,NumericQ)", //
				"True");
		check("ArrayQ({{a, b}, {c, d}},2,SymbolQ)", //
				"True");
	}

	public void testArrayReshape() {
		check("ArrayReshape(Range(1000), {3, 2, 2})", //
				"{{{1,2},{3,4}},{{5,6},{7,8}},{{9,10},{11,12}}}");
		check("ArrayReshape({a, b, c, d, e, f}, {2, 3})", //
				"{{a,b,c},{d,e,f}}");
		check("ArrayReshape({a, b, c, d, e, f}, {2, 3, 1})", //
				"{{{a},{b},{c}},{{d},{e},{f}}}");
		check("ArrayReshape(Range(24), {2, 3, 4})", //
				"{{{1,2,3,4},{5,6,7,8},{9,10,11,12}},{{13,14,15,16},{17,18,19,20},{21,22,23,24}}}");
		check("ArrayReshape({a, b, c, d, e, f}, {2, 7})", //
				"{{a,b,c,d,e,f,0},{0,0,0,0,0,0,0}}");
		check("ArrayReshape({a, b, c, d, e, f}, {2, 3, 3,2}, x)", //
				"{{{{a,b},{c,d},{e,f}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}},{{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}}}");

	}

	public void testAtomQ() {
		check("AtomQ(Sin(Pi))", //
				"True");
		check("AtomQ(x)", //
				"True");
		check("AtomQ(1.2)", //
				"True");
		check("AtomQ(2 + I)", //
				"True");
		check("AtomQ(2 / 3)", //
				"True");
		check("AtomQ(x + y)", //
				"False");
	}

	public void testArithmeticGeometricMean() {
		check("ArithmeticGeometricMean({1.0, 2.0, 3.0, 4.0}, 42.0)", //
				"{12.874,14.88314,16.37375,17.62155}");
		check("ArithmeticGeometricMean(a, 1/a)", // orderless
				"ArithmeticGeometricMean(1/a,a)");
		check("ArithmeticGeometricMean(a, 0)", //
				"0");
		check("ArithmeticGeometricMean(0, b)", //
				"0");
		check("ArithmeticGeometricMean(d, d)", //
				"d");

		check("ArithmeticGeometricMean({1.0, 2.0, 3.0}, 5)", //
				"{2.60401,3.329,3.93624}");
		check("N(ArithmeticGeometricMean(1,2), 20)", //
				"1.4567910310469068691");

		check("ArithmeticGeometricMean(1 - I, 2.5 + I)", //
				"1.83463+I*(-0.191462)");
		check("N(ArithmeticGeometricMean(1 - I, 2.5 + I), 30)", //
				"1.83462883815328396810218573046+I*(-1.91461625197137083440493535055*10^-1)");
	}

	public void testAttributes() {
		check("Attributes(fun) = {ReadProtected, Protected}", //
				"{ReadProtected,Protected}");
		check("Attributes(Plus)", //
				"{Flat,Listable,NumericFunction,OneIdentity,Orderless,Protected}");
	}

	public void testBeginPackage() {
		check("BeginPackage(\"test`\")", //
				"");
		check("Context( )", //
				"test`");
		check("$ContextPath", //
				"{test`,System`}");
		check("testit::usage = \"testit(x) gives x^2\"", //
				"testit(x) gives x^2");
		check("testit(x_) :=  x^2 ", //
				"");
		check("xxx", //
				"xxx");
		check("testit(12)", //
				"144");
		check("Begin(\"`Private`\")", //
				"test`Private`");
		check("End( )", //
				"test`Private`");
		check("EndPackage( )", //
				"");
		check("$ContextPath", //
				"{test`,System`,Global`}");
		check("Context( )", //
				"Global`");
		// print usage message in console
		check("?testit", //
				"");
		check("testit(12)", //
				"144");
		check("xxx", //
				"xxx");
	}

	public void testBeginPackageNested() {
		check("BeginPackage(\"test`\")", //
				"");
		check("Context( )", //
				"test`");
		check("$ContextPath", //
				"{test`,System`}");
		check("testit::usage = \"testit(x) gives x^2\"", //
				"testit(x) gives x^2");
		check("testit(x_) :=  x^2 ", //
				"");
		check("Begin(\"`Private`\")", //
				"test`Private`");
		check("test2(x_) :=  x^3 ", //
				"");
		check("testit(12)", //
				"144");
		check("Context( )", //
				"test`Private`");
		check("$Context", //
				"test`Private`");
		check("$ContextPath", //
				"{test`,System`}");
		check("End( )", //
				"test`Private`");
		check("$ContextPath", //
				"{test`,System`}");
		check("Context( )", //
				"test`");
		check("EndPackage( )", //
				"");
		check("test`Private`test2(12)", //
				"1728");
		check("$ContextPath", //
				"{test`,System`,Global`}");
		check("Context( )", //
				"Global`");
		// print usage message in console
		check("?testit", //
				"");
		check("testit(12)", //
				"144");
	}

	public void testBaseForm() {
		check("36^^zZz", //
				"46655");
		check("BaseForm(46655, 36)", //
				"Subscript(zzz,36)");
		check("16^^abcdefff", //
				"2882400255");
		check("BaseForm(2882400255, 16)", //
				"Subscript(abcdefff,16)");
		check("37^^abcdefff", //
				"Syntax error in line: 1 - Base 37^^... is invalid. Only bases between 1 and 36 are allowed\n"
						+ "37^^abcdefff\n" + "  ^");
		check("16^^6z12xy", //
				"Syntax error in line: 1 - Number format error: 6z12xy\n" + "16^^6z12xy\n" + "         ^");
	}

	public void testBegin() {
		check("Begin(\"mytest`\")", //
				"mytest`");
		check("Context( )", //
				"mytest`");
		check("$ContextPath", //
				"{System`,Global`}");
		check("testit::usage = \"testit(x) gives x^2\"", //
				"testit(x) gives x^2");
		check("testit(x_) :=  x^2 ", //
				"");
		check("testit(12)", //
				"144");
		check("End( )", //
				"mytest`");
		check("$ContextPath", //
				"{System`,Global`}");
		check("Context( )", //
				"Global`");
		// print usage message in console
		check("?mytest`testit", //
				"");
		check("mytest`testit(12)", //
				"144");
		check("testit(12)", //
				"testit(12)");
	}

	public void testBellB() {
		check("BellB(1/2,z)", //
				"BellB(1/2,z)");

		check("BellB(10,x)", //
				"x+511*x^2+9330*x^3+34105*x^4+42525*x^5+22827*x^6+5880*x^7+750*x^8+45*x^9+x^10");
		check("BellB(0,z)", //
				"1");
		check("BellB(1,z)", //
				"z");
		check("BellB(42,0)", //
				"0");
		check("BellB(25)", //
				"4638590332229999353");
		check("BellB(n,0)", //
				"BellB(n,0)");
		check("BellB(n,1)", //
				"BellB(n)");
		check("BellB(5,x)", //
				"x+15*x^2+25*x^3+10*x^4+x^5");
		check("Table(BellB(k), {k, 0, 14})", //
				"{1,1,2,5,15,52,203,877,4140,21147,115975,678570,4213597,27644437,190899322}");
		check("BellB(10)", //
				"115975");
		check("BellB(15)", //
				"1382958545");
		check("BellB(100)", //
				"4758539127676483365879076884138720782636366968682561146661633463755911449789244\\\n"
						+ "2622672724044217756306953557882560751");
		check("BellB({1,2,3,4,5,6})", "{1,2,5,15,52,203}");
	}

	public void testBellY() {
		// check("x + Sum(x^m/(m!*(m - 1)!) * BellY(Table({(m + k - 2)!, -(k - 1)! * c(k)}, {k, 2, m})), {m, 2, 4}) ",
		// //
		// "");
		check("BellY(2,1,{1/2,0})", //
				"0");
		check("BellY(5, 2, {1})", //
				"0");
		check("BellY(5, 2, {1,2})", //
				"0");
		check("BellY(5, 2, {1,2,3})", //
				"60");
		check("BellY(5, 8, {1,2,3})", //
				"0");

		// https://en.wikipedia.org/wiki/Bell_polynomials
		check("BellY(6, 2, {x1, x2, x3, x4, x5})", //
				"10*x3^2+15*x2*x4+6*x1*x5");
		check("BellY(6, 3, {x1, x2, x3, x4})", //
				"15*x2^3+60*x1*x2*x3+15*x1^2*x4");

		check("BellY(4, 2, {x1, x2, x3})", //
				"3*x2^2+4*x1*x3");
		check("With({n = 7, k = 2}, BellY(n, k, Array(x, n)))", //
				"35*x(3)*x(4)+21*x(2)*x(5)+7*x(1)*x(6)");

	}

	public void testBernoulliB() {
		check("BernoulliB(42)", //
				"1520097643918070802691/1806");
		check("BernoulliB(2)", //
				"1/6");
		check("Table(BernoulliB(k), {k, 0, 10})", //
				"{1,-1/2,1/6,0,-1/30,0,1/42,0,-1/30,0,5/66}");
	}

	public void testBesselI() {
		checkNumeric("BesselI(I+1, -I*Infinity)", //
				"0");
		checkNumeric("BesselI(42, I*Infinity)", //
				"0");

		checkNumeric("BesselI(0, 0)", //
				"1");
		checkNumeric("BesselI(4, 0)", //
				"0");
		checkNumeric("BesselI(-42, 0)", //
				"0");
		checkNumeric("BesselI(-42.5, 0)", //
				"ComplexInfinity");
		checkNumeric("BesselI(I, 0)", //
				"Indeterminate");

		checkNumeric("BesselI(0,2.0 )", //
				"2.279585302336067");
		checkNumeric("BesselI(3 + I, 1.5 - I)", //
				"-0.2566499289085075+I*0.04927707435312517");
		checkNumeric("BesselI({0, 1, 2}, 1.)", //
				"{1.2660658777520082,0.565159103992485,0.1357476697670383}");
	}

	public void testBesselJ() {
		checkNumeric("BesselJ(0,5.2)", //
				"-0.1102904415027928");
		checkNumeric("BesselJ(0,4.0)", //
				"-0.3971498071738541");
		checkNumeric("BesselJ(7/3 + I, 4.5 - I)", //
				"1.1890836033637917+I*0.7156530815945776");

		checkNumeric("BesselJ(1,3.6 )", //
				"0.09546554705714085");
		check("BesselJ(-42, z)", //
				"BesselJ(42,z)");
		check("BesselJ(-43, z)", //
				"-BesselJ(43,z)");
		check("BesselJ(0.5, z)", //
				"(Sqrt(2)*Sin(z))/(Sqrt(Pi)*Sqrt(z))");
		check("BesselJ(-0.5, 1.2)", //
				"0.263929");
		check("BesselJ(-0.5, 17)", //
				"-0.0532484");
		check("BesselJ(-0.5, z)", //
				"(Sqrt(2)*Sin(1.5708+z))/(Sqrt(Pi)*Sqrt(z))");
		check("BesselJ(1/2, z)", //
				"(Sqrt(2)*Sin(z))/(Sqrt(Pi)*Sqrt(z))");
		check("BesselJ(-1/2, z)", //
				"(Sqrt(2)*Cos(z))/(Sqrt(Pi)*Sqrt(z))");
		check("BesselJ(-2.5, 1.333)", //
				"1.6236");
		check("BesselJ(-2.5, z)", //
				"(Sqrt(2)*((3.0*Cos(4.71239+z))/z+(1.0-3.0/z^2.0)*Sin(4.71239+z)))/(Sqrt(Pi)*Sqrt(z))");
		check("BesselJ(-5/2, z)", //
				"(Sqrt(2)*(-(1-3/z^2)*Cos(z)+(3*Sin(z))/z))/(Sqrt(Pi)*Sqrt(z))");
		check("BesselJ(0, 5.2)", "-0.11029");
		checkNumeric("BesselJ(3.5, 1.2)", //
				"0.013270419445928418");
		check("BesselJ(4.0, 0.0)", //
				"0.0");
		check("BesselJ(1.0, -3.0)", //
				"-0.339059");
		check("BesselJ(0.0, 0.0)", //
				"1.0");
		check("BesselJ(-3.0, 0.0)", //
				"0.0");
		check("BesselJ(-3, 0)", //
				"0");
		check("BesselJ(0, 0)", //
				"1");
		check("BesselJ(4, 0)", //
				"0");
		check("BesselJ(0.0, 4)", //
				"-0.39715");
		check("BesselJ(1, {0.5, 1.0, 1.5})", //
				"{0.242268,0.440051,0.557937}");
	}

	public void testBesselJZero() {
		checkNumeric("BesselJZero(0.0,1)", //
				"2.404825524156817");
		checkNumeric("BesselJZero(0.0,2)", //
				"5.520077944691967");
		checkNumeric("BesselJZero(1.0,5)", //
				"16.470629751205134");
		checkNumeric("BesselJZero(0, {1, 2, 3}) // N", //
				"{2.404825524156817,5.520077944691967,8.653727993213547}");
		checkNumeric("BesselJZero(1, 1)/Pi // N", //
				"1.2196698399880057");
	}

	public void testBesselK() {
		checkNumeric("BesselK(I+1, -I*Infinity)", //
				"0");
		checkNumeric("BesselK(42, I*Infinity)", //
				"0");

		checkNumeric("BesselK(0, 0)", //
				"Infinity");
		checkNumeric("BesselK(4, 0)", //
				"ComplexInfinity");
		checkNumeric("BesselK(I, 0)", //
				"Indeterminate");

		checkNumeric("BesselK(0,0.53)", //
				"0.876560380458217");
		checkNumeric("BesselK(0,4.0)", //
				"0.011159676384823675");
		checkNumeric("BesselK(1 + I, 3.0  - 2* I)", //
				"-0.022510755137173245+I*0.016960737347051183");
	}

	public void testBesselY() {
		checkNumeric("BesselY(I+1, -Infinity)", //
				"0");
		checkNumeric("BesselY(42, Infinity)", //
				"0");

		checkNumeric("BesselY(0, 0)", //
				"-Infinity");
		checkNumeric("BesselY(4, 0)", //
				"ComplexInfinity");
		checkNumeric("BesselY(I, 0)", //
				"Indeterminate");

		checkNumeric("BesselY(0,2.5)", //
				"0.49807035955610557");
		checkNumeric("BesselY(0,1.0)", //
				"0.08825696423952921");
		checkNumeric("BesselY(0.5*I, 3.0 - I)", //
				"1.0468646059976179+I*0.8847844476971232");
	}

	public void testBesselYZero() {
		// checkNumeric("BesselYZero(0.0,2)", //
		// " ");
		// checkNumeric("BesselYZero(0.0,1)", //
		// " ");
	}

	public void testBeta() {
		// TODO improve bad precision
		// check("Beta(0.5, 3.2, 1.5)", "0.19168");
		check("Beta(1,b) // FunctionExpand", //
				"1/b");
		check("Beta(10,b) // FunctionExpand", //
				"362880/(b*(1+b)*(2+b)*(3+b)*(4+b)*(5+b)*(6+b)*(7+b)*(8+b)*(9+b))");

		check("Beta(2.3, 3.2)", //
				"0.0540298");

		check("Beta(a, a+1)", //
				"1/(a*(1+a)*CatalanNumber(a))");
		check("Beta(b-1, b)", //
				"1/((-1+b)*b*CatalanNumber(-1+b))");

		check("Beta(5,4)", //
				"1/280");
		check("Beta(5/2,7/2)", //
				"3/256*Pi");
		check("Beta(2.3,3.2)", //
				"0.0540298");
		// check("Beta(2.5+I,1-I)", "0.05403");

		check("Beta(a, 0)", //
				"ComplexInfinity");
		check("Beta(0,b)", //
				"ComplexInfinity");

		check("Beta(-n-4, n+1)", //
				"0");
	}

	public void testBetaRegularized() {
		check("BetaRegularized(1.0000001,1,1)", //
				"BetaRegularized(1.0,1.0,1.0)");
		check("BetaRegularized(-0.000000001,1,1)", //
				"BetaRegularized(-1.00000*10^-9,1.0,1.0)");

		// TODO get Indeterminate
		check("BetaRegularized(10^20., 10^30., 10.^20.)", //
				"BetaRegularized(1.00000*10^20,1.00000*10^30,1.00000*10^20)");

		check("BetaRegularized({0.25, 0.5, 0.75}, 2.5, 0.5)", //
				"{0.0117248,0.0755868,0.25317}");
		check("BetaRegularized(0.99 , 255.0 , 2.0)", //
				"0.273655");
		check("BetaRegularized(2 , 2 , 3)", //
				"8");
		check("BetaRegularized(2 , 7 , 17)", //
				"5512320");
		check("BetaRegularized(2 , 7 , -17)", //
				"0");
	}

	public void testBlankSequence() {

		check("Clear(f);f(___,y__?(#>2&)):={y}", //
				"");
		check("{f(2,3),f(1,1,1,2),f(112,1,1,3,4)}", //
				"{{3},f(1,1,1,2),{3,4}}");

		check("Clear(f);f(x__Integer)=2", //
				"2");
		check("{f(2,3),f(a,2),f(2,a),f(2)}", //
				"{2,f(a,2),f(2,a),2}");

		check("Clear(f);f(x__Real) := Plus(x)/Length({x})", //
				"");
		check("{f(1.0,4.0),f(2,2),f(1.0,a)}", //
				"{2.5,f(2,2),f(1.0,a)}");

		check("Clear(f);f(x__,y__,z__):={x,y,z}/;Print({x},{y},{z})", //
				"");
		check("f(a,b,c,d,e)", "f(a,b,c,d,e)");
		// print the possible matches
		// {a}{b}{c,d,e}
		// {a}{b,c}{d,e}
		// {a,b}{c}{d,e}
		// {a}{b,c,d}{e}
		// {a,b}{c,d}{e}
		// {a,b,c}{d}{e}
		System.out.println("-------------");
		check("g(x___,y___,z___):={x,y,z}/;Print({x},{y},{z})", //
				"");
		check("g(a,b,c,d,e)", "g(a,b,c,d,e)");
		// print the possible matches
		// {}{}{a,b,c,d,e}
		// {a}{b}{c,d,e}
		// {}{a,b,c}{d,e}
		// {a}{b,c}{d,e}
		// {a,b}{c}{d,e}
		// {a,b,c}{}{d,e}
		// {}{a,b,c,d}{e}
		// {a}{b,c,d}{e}
		// {a,b}{c,d}{e}
		// {a,b,c}{d}{e}
		// {a,b,c,d}{}{e}
		// {}{a,b,c,d,e}{}
		// {a}{b,c,d,e}{}
		// {a,b}{c,d,e}{}
		// {a,b,c}{d,e}{}
		// {a,b,c,d}{e}{}
		// {a,b,c,d,e}{}{}
	}

	public void testBlock() {
		// http://oeis.org/A005132 - Recaman's sequence
		check("f(s_List) := Block({a = s[[-1]], len = Length@s}, Append(s, If(a > len && !MemberQ(s, a - len), a - len, a + len))); Nest(f, {0}, 70)", //
				"{0,1,3,6,2,7,13,20,12,21,11,22,10,23,9,24,8,25,43,62,42,63,41,18,42,17,43,16,44,\n"
						+ "15,45,14,46,79,113,78,114,77,39,78,38,79,37,80,36,81,35,82,34,83,33,84,32,85,31,\n"
						+ "86,30,87,29,88,28,89,27,90,26,91,157,224,156,225,155}");
		check("blck=Block({i=10}, i=i+1; Return(i))", //
				"11");
		check("xm=10;Block({xm=xm}, xm=xm+1;Print(xm));xm", //
				"10");
		check("Block({testVar}, testVar=2222;testVar)", //
				"2222");
		check("testVar=1111; Block({testVar}, testVar)", //
				"1111");
		check("Block({x=y,y=z,z=3}, Print({Hold(x),Hold(y),Hold(z)});{x,y,z})", //
				"{3,3,3}");
		check("Block({x,f}, f(0)=0;f(x_):=f(x-1)+x;f(3))", //
				"6");
		check("f(3)", //
				"f(3)");

		// Rubi rules use Block variable names in "sub-rules":
		check("Integrate(E^(E^x + x), x)", //
				"E^E^x");
	}

	public void testBinCounts() {
		check("BinCounts({1,2,3,4,5})", //
				"{0,1,1,1,1,1}");
		check("BinCounts({1,2,3,4,5},3)", //
				"{2,3}");
		check("BinCounts({1,2,3,4,5},4)", //
				"{3,2}");
		check("BinCounts({1,2,3,4,5},5)", //
				"{4,1}");
		check("BinCounts({1,2,3,4,5},10)", //
				"{5}");
		check("BinCounts({0.04, 0.75, 0.3333, 0.03344, 0.9999},0)", //
				"BinCounts({0.04,0.75,0.3333,0.03344,0.9999},0)");
		check("BinCounts({1,2,3,4,5},{3,3,-1})", //
				"BinCounts({1,2,3,4,5},{3,3,-1})");
		check("BinCounts({1,2,3,4,5},{3,3,1})", //
				"{}");
		check("BinCounts({0.04, 0.75, 0.3333, 0.03344, 0.9999},0.1)", //
				"{2,0,0,1,0,0,0,1,0,1}");
		check("BinCounts({0.04, 0.75, 0.3333, 0.03344, 0.9999},-0.1)", //
				"BinCounts({0.04,0.75,0.3333,0.03344,0.9999},-0.1)");
		check("BinCounts({1, 2, 3, 4, 5})", //
				"{0,1,1,1,1,1}");
		check("BinCounts({1.5, 3, a, 2.5, 1, I}, 2)", //
				"{2,2}");
		check("BinCounts({1, 3, 2, 1, 4, 5, 6, 2}, {0, 10, 1})", //
				"{0,2,2,1,1,1,1,0,0,0}");
		check("BinCounts({1, 3, 2, 1, 4, 5, 6, 2}, 2)", //
				"{2,3,2,1}");
		check("BinCounts({1.5, 3, N(3, 20), 2.5, 1, E}, 2)", //
				"{2,4}");
	}

	public void testBinomial() {
		check("Binomial(a,b,c)", //
				"Binomial(a,b,c)");
		check("Binomial(a)", //
				"Binomial(a)");

		check("Factorial(10)/Factorial(3)", //
				"604800");
		check("Gamma(11)/Gamma(4)", //
				"604800");
		check("Binomial(4.5, 3.76)", //
				"3.39253");
		check("Binomial(2+k, k)", //
				"1/2*(1+k)*(2+k)");
		check("Binomial(5+k, k)", //
				"1/120*(1+k)*(2+k)*(3+k)*(4+k)*(5+k)");
		check("Binomial(-200,-100)", //
				"0");
		check("Binomial(-100,-200)", //
				"45274257328051640582702088538742081937252294837706668420660");
		check("Binomial(k, -1)", //
				"0");
		check("Binomial(3,3)", //
				"1");
		check("Binomial(0,0)", //
				"1");
		check("Binomial(-3,-5)", //
				"6");
		check("Binomial(Infinity, 0)", //
				"1");
		check("Binomial(-Infinity, {-3,-2,-1,0,1,2,3,4,5,6})", //
				"{0,0,0,1,-Infinity,Infinity,-Infinity,Infinity,-Infinity,Binomial(-Infinity,6)}");
		check("Binomial(-Infinity, -12)", //
				"0");
		check("Binomial(Infinity, {-3,-2,-1,0,1,2,3,4,5,6})", //
				"{0,0,0,1,Infinity,Infinity,Infinity,Infinity,Infinity,Binomial(Infinity,6)}");
		check("Binomial(Infinity, -12)", //
				"0");
		check("Binomial({2, 3, 5, 7, 11}, 3)", //
				"{0,1,10,35,165}");
		check("With({eps = 10^-6.}, \n" + " Table(Binomial(-3 - p eps, -5 - eps), {p, {-3, -2, -1, \n"
				+ "    1, 2, 3, 4}}))", //
				"{-2.0,-3.0,-6.0,6.0,3.0,2.0,1.5}");

		check("Binomial(k, -1)", //
				"0");
		check("Binomial(k, -1.4)", //
				"Binomial(k,-1.4)");
		check("Binomial(k, 0)", //
				"1");
		check("Binomial(40,1)", //
				"40");
		check("Binomial(40.0,1.0)", //
				"40.0");
		check("Binomial(40.3,1.2)", //
				"76.37683");
		check("Binomial(n, n+1)", //
				"0");
		check("Binomial(n, n+2)", //
				"0");
		check("Binomial(4,2)", //
				"6");
		check("Binomial(5,3)", //
				"10");

		check("Binomial(n0, 2)", //
				"1/2*(-1+n0)*n0");
		check("Binomial(k/3, k)", //
				"Binomial(k/3,k)");
		check("Binomial(0, 0)", //
				"1");
		check("Binomial(1000, 500)", //
				"2702882409454365695156146936259752754961520084465482870073928751066254287055221\\\n"
						+ "9389861248392450237016536260608502154610480220975005067991754989421969951847542\\\n"
						+ "3665484263751733356162464079737887344364574161119497604571044985756287880514600\\\n"
						+ "994219426752366915856603136862602484428109296905863799821216320");
		check("Binomial(n0, n0)", //
				"1");
		check("Binomial(n0, 0)", //
				"1");
		check("Binomial(n0, n0-1)", //
				"n0");
		check("Binomial(n0, 1)", //
				"n0");
		check("Binomial(n0, 2)", //
				"1/2*(-1+n0)*n0");
		check("Binomial(n0, 3)", //
				"1/6*(-2+n0)*(-1+n0)*n0");
		// check("Binomial(-3, -5)", "0");

		check("Binomial(k, 2)", //
				"1/2*(-1+k)*k");
		check("Binomial(k, 5)", //
				"1/120*(-4+k)*(-3+k)*(-2+k)*(-1+k)*k");
		check("Binomial(k, 6)", //
				"Binomial(k,6)");
	}

	public void testBitLength() {
		check("BitLength(1023)", //
				"10");
		check("BitLength(100) ", //
				"7");
		check("BitLength(-5)", //
				"3");
		check("BitLength(0)", //
				"0");
		check("BitLength(2^123-1)", //
				"123");
		check("BitLength(-(2^123-1))", //
				"123");
	}

	public void testBoole() {
		check("Boole(2 == 2)", //
				"1");
		check("Boole(7 < 5)  ", //
				"0");
		check("Boole(a == 7)", //
				"Boole(a==7)");

		check("{Boole(False), Boole(True)}", //
				"{0,1}");
		check("Boole({True, False, True, True, False})", //
				"{1,0,1,1,0}");
		check("Boole({a, False, b, True, f()})", //
				"{Boole(a),0,Boole(b),1,Boole(f())}");
	}

	public void testBooleanConvert() {
		check("BooleanConvert((A && !B || !A && B) && "//
				+ "(C && B && !A && !D && !E || C && !B && A && !D && !E || C && !B && !A && D && !E || C && !B && !A && !D && E || !C && B && A && !D && !E || !C && B && !A && D && !E || !C && B && !A && !D && E || !C && !B && A && D && !E || !C && !B && A && !D && E || !C && !B && !A && D && E) && "
				+ "(A && !F || !A && F) && (E && !C || !E && C), \"CNF\")", //
				"(A||B)&&(A||B||C||D)&&(A||B||C||D||E)&&(A||B||C||D||!E)&&(A||B||C||!D||E)&&(A||B||C||E)&&(A||B||!C||D||E)&&(A||B||!C||!D||!E)&&(A||B||D||E)&&(A||!B||C||D||E)&&(A||!B||C||!D||!E)&&(A||!B||!C||D||!E)&&(A||!B||!C||!D)&&(A||!B||!C||!D||E)&&(A||!B||!C||!D||!E)&&(A||!B||!C||!E)&&(A||!B||!D||!E)&&(A||C||D||E)&&(A||!C||!D||!E)&&(A||F)&&(!A||B||C||D||E)&&(!A||B||C||!D||!E)&&(!A||B||!C||D||!E)&&(!A||B||!C||!D)&&(!A||B||!C||!D||E)&&(!A||B||!C||!D||!E)&&(!A||B||!C||!E)&&(!A||B||!D||!E)&&(!A||!B)&&(!A||!B||C||D||!E)&&(!A||!B||C||!D)&&(!A||!B||C||!D||E)&&(!A||!B||C||!D||!E)&&(!A||!B||C||!E)&&(!A||!B||!C)&&(!A||!B||!C||D)&&(!A||!B||!C||D||E)&&(!A||!B||!C||D||!E)&&(!A||!B||!C||!D)&&(!A||!B||!C||!D||E)&&(!A||!B||!C||!D||!E)&&(!A||!B||!C||E)&&(!A||!B||!C||!E)&&(!A||!B||D||!E)&&(!A||!B||!D)&&(!A||!B||!D||E)&&(!A||!B||!D||!E)&&(!A||!B||!E)&&(!A||C||!D||!E)&&(!A||!C||D||!E)&&(!A||!C||!D)&&(!A||!C||!D||E)&&(!A||!C||!D||!E)&&(!A||!C||!E)&&(!A||!D||!E)&&(!A||!F)&&(B||C||D||E)&&(B||!C||!D||!E)&&(!B||C||!D||!E)&&(!B||!C||D||!E)&&(!B||!C||!D)&&(!B||!C||!D||E)&&(!B||!C||!D||!E)&&(!B||!C||!E)&&(!B||!D||!E)&&(C||E)&&(!C||!D||!E)&&(!C||!E)");

		check("BooleanConvert((a||b)&&(c||d), \"CNF\")", //
				"(a||b)&&(c||d)");

		check("BooleanConvert(a&&!b||!a&&c||b&&!c, \"DNF\")", //
				"a&&!b||!a&&c||b&&!c");

		check("BooleanConvert(Implies(x, y), \"CNF\")", //
				"!x||y");
		check("BooleanConvert(! (a && b), \"CNF\")", //
				"!a||!b");
		check("BooleanConvert(! (a || b || c), \"CNF\")", //
				"!a&&!b&&!c");
		check("BooleanConvert(Xor(x,y), \"CNF\")", //
				"(x||y)&&(!x||!y)");
		check("BooleanConvert(Xor(p,q,r),\"CNF\")", //
				"(p||q||r)&&(p||!q||!r)&&(!p||q||!r)&&(!p||!q||r)");
		check("BooleanConvert(Nand(p, q, r), \"CNF\")", //
				"!p||!q||!r");
		check("BooleanConvert(!Nand(p, q, r), \"CNF\")", //
				"p&&q&&r");
		check("BooleanConvert(Nor(p, q, r), \"CNF\")", //
				"!p&&!q&&!r");
		check("BooleanConvert(!Nor(p, q, r), \"CNF\")", //
				"p||q||r");
		check("BooleanConvert(! (a && b), \"CNF\")", //
				"!a||!b");
		check("BooleanConvert(! (a || b || c), \"CNF\")", //
				"!a&&!b&&!c");
		check("BooleanConvert(Equivalent(x, y, z), \"CNF\")", //
				"(x||!y)&&(x||!z)&&(!x||y)&&(!x||z)&&(y||!z)&&(!y||z)");

		check("BooleanConvert(Implies(x, y))", //
				"!x||y");
		check("BooleanConvert(! (a && b))", //
				"!a||!b");
		check("BooleanConvert(! (a || b || c))", //
				"!a&&!b&&!c");
		check("BooleanConvert(Xor(x,y))", //
				"x&&!y||!x&&y");
		check("BooleanConvert(Xor(p,q,r))", //
				"p&&q&&r||p&&!q&&!r||!p&&q&&!r||!p&&!q&&r");
		check("BooleanConvert(Nand(p, q, r))", //
				"!p||!q||!r");
		check("BooleanConvert(!Nand(p, q, r))", //
				"p&&q&&r");
		check("BooleanConvert(Nor(p, q, r))", //
				"!p&&!q&&!r");
		check("BooleanConvert(!Nor(p, q, r))", //
				"p||q||r");
		check("BooleanConvert(! (a && b))", //
				"!a||!b");
		check("BooleanConvert(! (a || b || c))", //
				"!a&&!b&&!c");
		check("BooleanConvert(Equivalent(x, y, z))", //
				"x&&y&&z||!x&&!y&&!z");

		check("BooleanConvert(Implies(x, y), \"DNF\")", //
				"!x||y");
		check("BooleanConvert(! (a && b), \"DNF\")", //
				"!a||!b");
		check("BooleanConvert(! (a || b || c), \"DNF\")", //
				"!a&&!b&&!c");
		check("BooleanConvert(Xor(x,y), \"DNF\")", //
				"x&&!y||!x&&y");
		check("BooleanConvert(Nand(p, q, r), \"DNF\")", //
				"!p||!q||!r");
		check("BooleanConvert(!Nand(p, q, r), \"DNF\")", //
				"p&&q&&r");
		check("BooleanConvert(Nor(p, q, r), \"DNF\")", //
				"!p&&!q&&!r");
		check("BooleanConvert(!Nor(p, q, r), \"DNF\")", //
				"p||q||r");
		check("BooleanConvert(! (a && b), \"DNF\")", //
				"!a||!b");
		check("BooleanConvert(! (a || b || c), \"DNF\")", //
				"!a&&!b&&!c");
		check("BooleanConvert(Equivalent(x, y, z), \"DNF\")", //
				"x&&y&&z||!x&&!y&&!z");
	}

	public void testBooleanMinimize() {
		check("BooleanMinimize(Or(z,a, z))", //
				"a||z");
		check("BooleanMinimize((a&&b&&!c)||(a&&!b&&c)||(a&&!c&&d)||(!a&&b&&c)||(b&&c&&!d)||(b&&!c&&d)||(!b&&c&&d))", //
				"a&&b&&!c||a&&!b&&d||a&&c&&!d||!a&&b&&d||!a&&c&&d||b&&c&&!d");

		// https://github.com/logic-ng/LogicNG/issues/23
		// a4 & a2 & a0 | a5 & a2 & a0 | a4 & a3 & a0 | a5 & a3 & a0 | a4 & a2 & a1 | a5 & a2 & a1 | a4 & a3 & a1 | a5 &
		// a3 & a1
		// a8 & a6 | a9 & a6 | a8 & a7 | a9 & a7
		// a10 | a11 | a12
		check("BooleanMinimize(a4 && a2 && a0 || a5 && a2 && a0 || a4 && a3 && a0 || a5 && a3 && a0 || a4 && a2 && a1 || a5 && a2 && a1 || a4 && a3 && a1 || a5 && a3 && a1)", //
				"a0&&a2&&a4||a0&&a2&&a5||a0&&a3&&a4||a0&&a3&&a5||a1&&a2&&a4||a1&&a2&&a5||a1&&a3&&a4||a1&&a3&&a5");
		check("BooleanMinimize(a8 && a6 || a9 && a6 || a8 && a7 || a9 && a7)", //
				"a6&&a8||a6&&a9||a7&&a8||a7&&a9");
		check("BooleanMinimize(a4 && a2 && a0 || a5 && a2 && a0 || a4 && a3 && a0 || a5 && a3 && a0 || a4 && a2 && a1 || a5 && a2 && a1 || a4 && a3 && a1 || a5 && a3 && a1 || a8 && a6 || a9 && a6 || a8 && a7 || a9 && a7 || a10 || a11 || a12)", //
				"a10||a11||a12||a0&&a2&&a4||a0&&a2&&a5||a0&&a3&&a4||a0&&a3&&a5||a1&&a2&&a4||a1&&a2&&a5||a1&&a3&&a4||a1&&a3&&a5||a6&&a8||a6&&a9||a7&&a8||a7&&a9");
		check("BooleanMinimize((a0 || a1) && (a2 || a3) && (a4 || a5) || (a6 || a7) && (a8 || a9) || a10 || a11 || a12)", //
				"a10||a11||a12||a0&&a2&&a4||a0&&a2&&a5||a0&&a3&&a4||a0&&a3&&a5||a1&&a2&&a4||a1&&a2&&a5||a1&&a3&&a4||a1&&a3&&a5||a6&&a8||a6&&a9||a7&&a8||a7&&a9");

		check("BooleanMinimize(!e && !d && c && b && a || !c && !f && !b && a || !f && a && !c && b || !i && f && h && !a || c && d && b && a || !b && g && a || !j && !h && !a)", //
				"a&&b&&c&&d||a&&b&&c&&!e||a&&!b&&g||a&&!c&&!f||!a&&f&&h&&!i||!a&&!h&&!j");
		check("BooleanMinimize((A && !B || !A && B) && "//
				+ "(C && B && !A && !D && !E || C && !B && A && !D && !E || C && !B && !A && D && !E || C && !B && !A && !D && E || !C && B && A && !D && !E || !C && B && !A && D && !E || !C && B && !A && !D && E || !C && !B && A && D && !E || !C && !B && A && !D && E || !C && !B && !A && D && E) && "
				+ "(A && !F || !A && F) && (E && !C || !E && C))", //
				"A&&!B&&C&&!D&&!E&&!F||A&&!B&&!C&&!D&&E&&!F||!A&&B&&C&&!D&&!E&&F||!A&&B&&!C&&!D&&E&&F");
		check("BooleanMinimize((a&&!b)||(!a&&b)||(b&&!c)||(!b&&c))", //
				"a&&!b||!a&&c||b&&!c");
		check("BooleanMinimize((a||b)&&(c||d))", //
				"a&&c||a&&d||b&&c||b&&d");
		check("BooleanMinimize(a && b || ! a && b)", //
				"b");
		check("BooleanMinimize(Equivalent(x, y, z))", //
				"x&&y&&z||!x&&!y&&!z");

		// TODO CNF form not supported
		// check("BooleanMinimize((a&&!b)||(!a&&b)||(b&&!c)||(!b&&c), \"CNF\")", //
		// "(a||b||c)&&(!a||!b||!c)");
		// check("BooleanMinimize((a||b)&&(c||d), \"CNF\")", //
		// "(a||b)&&(a||b||c)&&(a||b||c||d)&&(a||b||d)&&(a||c||d)&&(b||c||d)&&(c||d)");
	}

	public void testBooleanQ() {
		check("BooleanQ(True)", //
				"True");
		check("BooleanQ(False)", //
				"True");
		check("BooleanQ(f(x))", //
				"False");
		check("BooleanQ(Together(x/y + y/x))", //
				"False");
	}

	public void testBooleanTable() {
		check("BooleanTable(Xor(p, q, r), {p, q, r})", //
				"{True,False,False,True,False,True,True,False}");
		check("BooleanTable(Implies(Implies(p, q), r), {p, q, r})", //
				"{True,False,True,True,True,False,True,False}");
		check("BooleanTable(p || q, {p, q})", //
				"{True,True,True,False}");
		check("BooleanTable(And(a, b, c), {a, b, c})", //
				"{True,False,False,False,False,False,False,False}");
	}

	public void testBooleanVariables() {
		check("BooleanVariables(a || ! b && b)", //
				"{a,b}");
		check("BooleanVariables(Xor(a, And(b, Or(c, d))))", //
				"{a,b,c,d}");
		check("BooleanVariables(a && b || ! a && b)", //
				"{a,b}");
		check("BooleanVariables(Xor(p,q,r))", //
				"{p,q,r}");
		check("BooleanVariables(a + b*c)", //
				"{}");
	}

	public void testBrayCurtisDistance() {
		check("BrayCurtisDistance({-1, -1}, {10, 10})", //
				"11/9");
	}

	public void testBreak() {
		check("n = 0", //
				"0");
		check("While(True, If(n>10, Break()); n=n+1)", //
				"");
		check("n", //
				"11");
	}

	public void testByteArray() {
		check("ba=ByteArray(\"AQIDBAUGBwg=\")", //
				"ByteArray[8 Bytes]");
		check("Normal(ba)", //
				"{1,2,3,4,5,6,7,8}");
	}

	public void testCancel() {
		// check("Cancel(((1+x)*f(x))/x^2)", //
		// "x/(-1+x)");
		// check("Cancel((x*(1+x))/(-1+x^2))", //
		// "x/(-1+x)");
		// see rubi rule 27:
		check("Cancel(d/(c*d+d^2*x))", //
				"1/(c+d*x)");
		check("Cancel((c*d+d^2*x)/d)", //
				"c+d*x");
		check("Cancel((c*d+d^2*x)^(-1)/(d^2)^(-1)  )", //
				"d/(c+d*x)");
		check("Cancel((c*d+d^2*x) /(d^2)   )", //
				"(c+d*x)/d");
		check("Cancel((c*d+d^2*x)^(-2)/(d^2)^(-1)  )", //
				"1/(c+d*x)^2");
		check("Cancel((c*d+d^2*x)^2/d^2 )", //
				"(c+d*x)^2");

		check("Cancel(x / x ^ 2)", //
				"1/x");
		check("Cancel(x / x ^ 2 + y / y ^ 2)", //
				"1/x+1/y");
		check("Cancel(f(x) / x + x * f(x) / x ^ 2)", //
				"(2*f(x))/x");

		check("Cancel((x - a)/(x^2 - a^2) == 0 && (x^2 - 2*x + 1)/(x - 1) >= 0)", //
				"1/(a+x)==0&&x>=1");
		check("9+3*x+x^2", "9+3*x+x^2");
		check("(9+3*x+x^2)*(3+x)^(-1)", //
				"(9+3*x+x^2)/(3+x)");
		check("1+(9+3*x+x^2)*(3+x)^(-1)+x+(x+y)^(-1)", //
				"1+x+(9+3*x+x^2)/(3+x)+1/(x+y)");

		check("Cancel(x / x ^ 2)", //
				"1/x");
		check("Cancel(f(x) / x + x * f(x) / x ^ 2)", //
				"(2*f(x))/x");
		check("Cancel(x / x ^ 2 + y / y ^ 2)", //
				"1/x+1/y");
		check("Cancel((x^2 - 1)/(x - 1))", //
				"1+x");
		check("Cancel((x - y)/(x^2 - y^2) + (x^3 - 27)/(x^2 - 9) + (x^3 + 1)/(x^2 - x + 1))", //
				"1+x+(9+3*x+x^2)/(3+x)-1/(-x-y)");
		check("cancel((x - 1)/(x^2 - 1) + (x - 2)/(x^2 - 4))", //
				"1/(1+x)+1/(2+x)");
		check("together((x - 1)/(x^2 - 1) + (x - 2)/(x^2 - 4))", //
				"(3+2*x)/(2+3*x+x^2)");

		check("Cancel(x/(1+x^3))", //
				"x/(1+x^3)");
	}

	public void testCarmichaelLambda() {
		check("CarmichaelLambda(-n)", //
				"CarmichaelLambda(n)");
		check("CarmichaelLambda(0)", //
				"0");
		check("CarmichaelLambda(1)", //
				"1");
		check("CarmichaelLambda(2)", //
				"1");
		check("CarmichaelLambda(10)", //
				"4");
		check("CarmichaelLambda(15)", //
				"4");
		check("CarmichaelLambda(11)", //
				"10");
		check("CarmichaelLambda(35)", //
				"12");
		check("CarmichaelLambda(50)", //
				"20");
		check("Table(CarmichaelLambda(-k), {k, 12})", //
				"{1,1,2,2,4,2,6,2,6,4,10,2}");
		check("Table(CarmichaelLambda(10^k), {k, 0, 10})", //
				"{1,4,20,100,500,5000,50000,500000,5000000,50000000,500000000}");
	}

	public void testCases() {
		check("Cases({{a, a}, {b, a}, {a, b, c}, {b, b}, {c, a}, {b, b, b}}, {__, a | b} | {c, __})", //
				"{{a,a},{b,a},{b,b},{c,a},{b,b,b}}");
		check("Cases({{2,1,0},{3,2,0},{2,0,0},{3,3,1}}, {_, 1 | 2, _})", //
				"{{2,1,0},{3,2,0}}");
		check("Cases({{a, a}, {b, a}, {a, b, c}, {b, b}, {c, a}, {b, b, b}}, {a|b, _})", //
				"{{a,a},{b,a},{b,b}}");
		check("Cases({a, 1, 2.5, \"string\"}, _Integer|_Real)", //
				"{1,2.5}");
		check("Cases(_Complex)[{1, 2*I, 3, 4-I, 5}]", //
				"{I*2,4-I}");
		check("Cases(1, 2)", //
				"{}");
		check("Cases(f(1, 2), 2)", //
				"{2}");
		check("Cases(f(f(1, 2), f(2)), 2)", //
				"{}");
		check("Cases(f(f(1, 2), f(2)), 2, 2)", //
				"{2,2}");
		check("Cases(f(f(1, 2), f(2), 2), 2, Infinity)", //
				"{2,2,2}");
		check("Cases({1, f(2), f(3, 3, 3), 4, f(5, 5)}, f(x__) :> Plus(x))", //
				"{2,9,10}");
		check("Cases({1, f(2), f(3, 3, 3), 4, f(5, 5)}, f(x__) -> Plus(x))", //
				"{2,3,3,3,5,5}");

		check("Cases(_Complex)[{1, 2*I, 3, 4-I, 5}]", //
				"{I*2,4-I}");
		check("Cases({x, a, b, x, c}, Except(x))", //
				"{a,b,c}");
		check("Cases({a, 0, b, 1, c, 2, 3}, Except(1, _Integer))", //
				"{0,2,3}");
		check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer)", //
				"{1,1,2,3,9}");
		check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -1)", //
				"{1,1,2,3,8,9,10}");
		check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -2)", //
				"{}");
		check("Cases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, {0,4})", //
				"{1,1,2,3,8,9,10}");

		check("Cases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x)", //
				"{a,10}");
		check("Cases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x, -2)", //
				"{a,8,10}");

		check("Cases({3, -4, 5, -2}, x_ /; x < 0)", //
				"{-4,-2}");
		check("Cases({3, 4, x, x^2, x^3}, x^_)", //
				"{x^2,x^3}");
		check("Cases({3, 4, x, x^2, x^3}, x^n_ -> n)", //
				"{2,3}");
		check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {_, _})", //
				"{{1,2},{5,4},{3,3}}");
		check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {a_, b_} -> a + b)", //
				"{3,9,6}");
		check("Cases(Sqrt(Range(100)), _Integer, {1}, 3)", //
				"{1,2,3}");
	}

	public void testCatch() {
		check("Catch(f(Catch(Throw(a, u), u)), v)", //
				"f(a)");
		check("Catch(f(Catch(Throw(a, u), v)), u)", //
				"a");
		check("Catch(f(Catch(Throw(a, u), u)), v, f)", //
				"f(a)");
		check("Catch(f(Catch(Throw(a, u), v)), u, f)", //
				"f(a,u)");
		check("Catch(Scan(If(IntegerQ(#1),Null,Throw(False))&,{2,3});True)", //
				"True");
		check("Catch(Scan(If(IntegerQ(#1),Null,Throw(False))&,{b+a});True)", //
				"False");
		check("Catch(Scan(If(# > 5, Throw(#)) &, {2, 4, 6, 8}))", //
				"6");
		check("Catch(a; b; Throw(c); d; e)", //
				"c");
		check("$f(x_) := If(x > 10, Throw(overflow), x!);Catch($f(2) + $f(11))", //
				"overflow");
		check("$f(x_) := If(x > 10, Throw(overflow), x!);Catch($f(2) + $f(3))", //
				"8");
		check("catch(do(If(i0! > 10^10, throw(i0)), {i0, 100}))", //
				"14");
		check("Catch(If(# < 0, Throw(#)) & /@ {1, 2, 0, -1, 5, 6})", //
				"-1");
		check("Catch(a^2 + b^2 + c^2 /. b :> Throw(bbb))", //
				"bbb");
		check("Catch({Catch({a, Throw(b), c}), d, e})", //
				"{b,d,e}");
		check("Catch(Throw /@ {a, b, c})", //
				"a");
		check("$f(x_) := (If(x < 0, Throw(\"negative\")); Sqrt(x));Catch(Sum($f(i0), {i0, 5, -5, -1}))", //
				"negative");
		// check("$lst={0,v1,n1};\n" +
		// " Catch(\n" +
		// " {Map(Function($lst=False;\n" +
		// " If($lst===False,Throw(False),$lst((1)))),\n" +
		// " u),$lst((2)),$lst((3))})","");
	}

	public void testCatalan() {
		checkNumeric("N(Catalan)", //
				"0.915965594177219");
	}

	public void testCatalanNumber() {
		checkNumeric("CatalanNumber(-10)", //
				"0");
		checkNumeric("CatalanNumber(-1)", //
				"-1");
		checkNumeric("CatalanNumber(0)", //
				"1");
		checkNumeric("CatalanNumber(1)", //
				"1");
		checkNumeric("CatalanNumber(3)", //
				"5");
		checkNumeric("CatalanNumber(10)", //
				"16796");
	}

	public void testCatenate() {
		check("Catenate({{1, 2, 3}, {4, 5}})", //
				"{1,2,3,4,5}");
		check("Catenate({{1,2,3},{a,b,c},{4,5,6}})", //
				"{1,2,3,a,b,c,4,5,6}");
	}

	public void testCDF() {

		check("CDF(BetaDistribution(2,3), 0.1)", //
				"0.0523");
		check("CDF(BetaDistribution(2,3), 0.9)", //
				"0.9963");
		check("CDF(ChiSquareDistribution(3), 0.1)", //
				"0.00816258");
		check("CDF(ChiSquareDistribution(3), 0.9)", //
				"0.174572");
		check("CDF(ExponentialDistribution(3), 0.1)", //
				"0.259182");
		check("CDF(ExponentialDistribution(3), 0.9)", //
				"0.932794");
		check("CDF(FRatioDistribution(2,3), 0.1)", //
				"0.0922695");
		check("CDF(FRatioDistribution(2,3), 0.9)", //
				"0.505894");
		check("CDF(GammaDistribution(2,3), 0.1)", //
				"0.000543363");
		check("CDF(GammaDistribution(2,3), 0.9)", //
				"0.0369363");
		check("CDF(GumbelDistribution(2,3), 0.1)", //
				"0.411877");
		check("CDF(GumbelDistribution(2,3), 0.9)", //
				"0.499947");
		check("CDF(LogNormalDistribution(2,3), 0.1)", //
				"0.0757583");
		check("CDF(LogNormalDistribution(2,3), 0.9)", //
				"0.241406");
		check("CDF(NakagamiDistribution(2,3), 0.1)", //
				"0.0000221237");
		check("CDF(NakagamiDistribution(2,3), 0.9)", //
				"0.102568");
		check("CDF(NormalDistribution(2,3), 0.1)", //
				"0.263258");
		check("CDF(NormalDistribution(2,3), 0.9)", //
				"0.356934");
		check("CDF(UniformDistribution({0,1}), 0.1)", //
				"0.1");
		check("CDF(UniformDistribution({0,1}), 0.9)", //
				"0.9");
		check("CDF(WeibullDistribution(2,3), 0.1)", //
				"0.00111049");
		check("CDF(WeibullDistribution(2,3), 0.9)", //
				"0.0860688");

		// github #56
		check("CDF(NormalDistribution(),-0.41)", //
				"0.340903");
		check("CDF(NormalDistribution(),0.41)", //
				"0.659097");
		check("Table(CDF(NormalDistribution(0, s), x), {s, {.75, 1, 2}}, {x, -6,6}) // N", //
				"{{6.22096*10^-16,1.30839*10^-11,4.82130*10^-8,0.0000316712,0.00383038,0.0912112,0.5,0.908789,0.99617,0.999968,1.0,1.0,1.0},{9.86588*10^-10,2.86652*10^-7,0.0000316712,0.0013499,0.0227501,0.158655,0.5,0.841345,0.97725,0.99865,0.999968,1.0,1.0},{0.0013499,0.00620967,0.0227501,0.0668072,0.158655,0.308538,0.5,0.691462,0.841345,0.933193,0.97725,0.99379,0.99865}}");
		checkNumeric("Table(CDF(NormalDistribution(0, s), x), {s, {.75, 1, 2}}, {x, -6,6}) // N", //
				"{{6.220960574271835E-16,1.308392468605314E-11,4.821303365114145E-8,3.167124183311997E-5,0.0038303805675897404,0.09121121972586804,0.5,0.9087887802741319,0.9961696194324102,0.9999683287581669,0.9999999517869663,0.999999999986916,0.9999999999999993},{9.865876450376937E-10,2.866515718791937E-7,3.1671241833119884E-5,0.0013498980316300926,0.022750131948179195,0.158655253931457,0.5,0.841344746068543,0.9772498680518208,0.9986501019683699,0.9999683287581669,0.9999997133484281,0.9999999990134123},{0.0013498980316300926,0.006209665325776133,0.022750131948179195,0.06680720126885804,0.158655253931457,0.3085375387259869,0.5,0.6914624612740131,0.841344746068543,0.9331927987311419,0.9772498680518208,0.9937903346742238,0.9986501019683699}}");

		check("CDF(NormalDistribution(n, m),k)", //
				"Erfc((-k+n)/(Sqrt(2)*m))/2");

		check("CDF(BernoulliDistribution(p),k)", //
				"Piecewise({{0,k<0},{1-p,0<=k&&k<1}},1)");
		check("CDF(BetaDistribution(a,b),k)", //
				"Piecewise({{BetaRegularized(k,a,b),0<k<1},{1,k>=1}},0)");
		check("CDF(BinomialDistribution(n, m),k)", //
				"Piecewise({{BetaRegularized(1-m,n-Floor(k),1+Floor(k)),0<=k&&k<n},{1,k>=n}},0)");
		check("CDF(ExponentialDistribution(n),k)", //
				"Piecewise({{1-1/E^(k*n),k>=0}},0)");
		check("CDF(PoissonDistribution(p),k)", //
				"Piecewise({{GammaRegularized(1+Floor(k),p),k>=0}},0)");
		check("CDF(DiscreteUniformDistribution({a, b}), k)", //
				"Piecewise({{(1-a+Floor(k))/(1-a+b),a<=k&&k<b},{1,k>=b}},0)");
		check("CDF(UniformDistribution({a, b}), k)", //
				"Piecewise({{(-a+k)/(-a+b),a<=k<=b},{1,k>b}},0)");
		check("CDF(ErlangDistribution(n, m),k)", //
				"Piecewise({{GammaRegularized(n,0,k*m),k>0}},0)");
		check("CDF(LogNormalDistribution(n,m),k)", //
				"Piecewise({{Erfc((n-Log(k))/(Sqrt(2)*m))/2,k>0}},0)");
		check("CDF(NakagamiDistribution(n, m),k)", //
				"Piecewise({{GammaRegularized(n,0,(k^2*n)/m),k>0}},0)");
		check("CDF(NormalDistribution(n, m),k)", //
				"Erfc((-k+n)/(Sqrt(2)*m))/2");
		check("CDF(FrechetDistribution(n, m),k)", //
				"Piecewise({{E^(-1/(k/m)^n),k>0}},0)");
		check("CDF(GammaDistribution(n, m),k)", //
				"Piecewise({{GammaRegularized(n,0,k/m),k>0}},0)");
		check("CDF(GeometricDistribution(n),k)", //
				"Piecewise({{1-(1-n)^(1+Floor(k)),k>=0}},0)");
		check("CDF(GumbelDistribution(n, m),k)", "1-1/E^E^((k-n)/m)");
		check("CDF(HypergeometricDistribution(n, ns, nt),k)", //
				"Piecewise({{1+(-ns!*(-ns+nt)!*HypergeometricPFQRegularized({1,1-n+Floor(k),1-ns+Floor(k)},{\n"
						+ "2+Floor(k),2-n-ns+nt+Floor(k)},1))/(Binomial(nt,n)*(-1+n-Floor(k))!*(-1+ns-Floor(k))!),\n"
						+ "0<=k&&n+ns-nt<=k&&k<n&&k<ns},{1,k>=n||k>=ns}},0)");
		check("CDF(StudentTDistribution(n),k)", //
				"Piecewise({{BetaRegularized(n/(k^2+n),n/2,1/2)/2,k<=0}},1/2*(1+BetaRegularized(k^\n"
						+ "2/(n+k^2),1/2,n/2)))");
		check("CDF(WeibullDistribution(n, m),k)", //
				"Piecewise({{1-1/E^(k/m)^n,k>0}},0)");
		check("CDF(BernoulliDistribution(4),k)", //
				"Piecewise({{0,k<0},{-3,0<=k&&k<1}},1)");

		check("CDF(DiscreteUniformDistribution({1, 5}), 3)", //
				"3/5");
	}

	public void testCeiling() {
		check("Ceiling(-9/4)", //
				"-2");
		check("Ceiling(1/3)", //
				"1");
		check("Ceiling(-1/3)", //
				"0");
		check("Ceiling(1.2)", //
				"2");
		check("Ceiling(3/2)", //
				"2");
		check("Ceiling(1.3 + 0.7*I)", //
				"2+I");
		check("Ceiling(2.6, 0.5)", //
				"3.0");
		check("Ceiling(10.4, -1) ", //
				"10");
		check("Ceiling(-10.4, -1) ", //
				"-11");

		check("Ceiling(1.5)", //
				"2");
		check("Ceiling(1.5 + 2.7*I)", //
				"2+I*3");
	}

	public void testCentralMoment() {
		check("CentralMoment({1.1, 1.2, 1.4, 2.1, 2.4}, 4)", //
				"0.100845");
	}

	public void testCharacteristicPolynomial() {
		check("CharacteristicPolynomial({{a, b}, {c, d}}, x)", //
				"-b*c+a*d-a*x-d*x+x^2");
		check("CharacteristicPolynomial({{1, 1, 1}, {1, 1/2, 1/3}, {1, 2, 3}},x)", //
				"-1/3-7/3*x+9/2*x^2-x^3");
		check("CharacteristicPolynomial(N({{1, 1, 1}, {1, 1/2, 1/3}, {1, 2, 3}}),x)", //
				"-0.333333-2.33333*x+4.5*x^2.0-x^3.0");
		check("CharacteristicPolynomial({{1, 2*I}, {3 + 4*I, 5}}, z)", //
				"13-I*6-6*z+z^2");
	}

	public void testCheck() {
		check("Check(2^(3), failure)", //
				"8");
		check("Check(0^(-42), failure)", //
				"failure");
		check("Check(0^0, failure)", //
				"failure");
	}

	public void testChebyshevT() {
		check("ChebyshevT(-1/2, z)", //
				"Cos(ArcCos(z)/2)");
		check("ChebyshevT(1/2, z)", //
				"Cos(ArcCos(z)/2)");
		check("ChebyshevT(1.5, 2+3*I)", //
				"0.692609+I*9.74575");
		check("ChebyshevT(8, x)", //
				"1-32*x^2+160*x^4-256*x^6+128*x^8");
		// TODO add non-integer args implementation
		// check("ChebyshevT(1 - I, 0.5)", "0.800143 + 1.08198 I");

		check("ChebyshevT(n,0)", //
				"Cos(1/2*n*Pi)");
		check("ChebyshevT({0,1,2,3,4}, x)", //
				"{1,x,-1+2*x^2,-3*x+4*x^3,1-8*x^2+8*x^4}");
		check("ChebyshevT({0,-1,-2,-3,-4}, x)", //
				"{1,x,-1+2*x^2,-3*x+4*x^3,1-8*x^2+8*x^4}");
		check("ChebyshevT(10, x)", //
				"-1+50*x^2-400*x^4+1120*x^6-1280*x^8+512*x^10");
	}

	public void testChebyshevU() {
		check("ChebyshevU(4, -42)", //
				"49765969");
		// http://oeis.org/A001906
		check("Table(ChebyshevU(n-1, 3/2), {n, 0, 30})", //
				"{0,1,3,8,21,55,144,377,987,2584,6765,17711,46368,121393,317811,832040,2178309,\n"
						+ "5702887,14930352,39088169,102334155,267914296,701408733,1836311903,4807526976,\n"
						+ "12586269025,32951280099,86267571272,225851433717,591286729879,1548008755920}");

		check("ChebyshevU(1.5, 2+3*I)", //
				"1.70238+I*19.36013");
		check("ChebyshevU(8, x)", //
				"1-40*x^2+240*x^4-448*x^6+256*x^8");
		// TODO add non-integer args implementation
		// check("ChebyshevU(1 - I, 0.5)", "1.60029 + 0.721322 I");
		check("ChebyshevU(n, 1)", //
				"1+n");
		check("ChebyshevU({0,1,2,3,4,5}, x)", //
				"{1,2*x,-1+4*x^2,-4*x+8*x^3,1-12*x^2+16*x^4,6*x-32*x^3+32*x^5}");
		check("ChebyshevU(0, x)", //
				"1");
		check("ChebyshevU(1, x)", //
				"2*x");
		check("ChebyshevU(10, x)", //
				"-1+60*x^2-560*x^4+1792*x^6-2304*x^8+1024*x^10");
	}

	public void testChessboardDistance() {
		check("ChessboardDistance({-1, -1}, {1, 1})", //
				"2");
	}

	public void testChineseRemainder() {
		check("ChineseRemainder({1,-15}, {284407855036305,47})", //
				"8532235651089151");
		check("ChineseRemainder({-2,-17}, {284407855036305,47})", //
				"9669867071234368");
		check("ChineseRemainder({2,17}, {284407855036305,47})", //
				"3697302115471967");
		check("ChineseRemainder({2123, 7213},{11,13})", //
				"11");
		// wikipedia example
		check("ChineseRemainder({0,3,4},{3,4,5})", //
				"39");

		check("ChineseRemainder({23},{17})", //
				"6");
		check("ChineseRemainder({91},{25})", //
				"16");
		check("ChineseRemainder({913},{25})", //
				"13");
		check("ChineseRemainder({3,4},{4,5})", //
				"19");

		check("ChineseRemainder({1, 2}, {6, 10})", //
				"ChineseRemainder({1,2},{6,10})");
	}

	public void testCholeskyDecomposition() {
		check("matG=CholeskyDecomposition({{11.0,3.0},{3.0, 5.0}})", //
				"{{3.31662,0.904534},\n" + //
						" {0.0,2.04495}}");
		check("Transpose(matG).matG", //
				"{{11.0,3.0},\n" + //
						" {3.0,5.0}}");
	}

	public void testChop() {
		check("Chop(0.00000000001)", //
				"0");
		check("Chop(18.35051+I*1.32213*10^-11)", //
				"18.35051");
	}

	public void testCirclePoints() {
		// check("CirclePoints(3)", "{{Sqrt(3)/2,-1/2},{0,1},{-Sqrt(3)/2,-1/2}}");
		check("CirclePoints(2)", //
				"{{1,0},{-1,0}}");

		check("CirclePoints(4)", //
				"{{1/Sqrt(2),-1/Sqrt(2)},{1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),\n" + "-1/Sqrt(2)}}");
		// check("CirclePoints(10)", "");
	}

	public void testClear() {
		check("x=100;y=42", //
				"42");
		check("x", //
				"100");
		check("y", //
				"42");
		check("Clear(x,y)", //
				"");
		check("x", //
				"x");
		check("y", //
				"y");
	}

	public void testClearAll() {
		check("x=100;y=42", //
				"42");
		check("x", //
				"100");
		check("y", //
				"42");
		check("ClearAll(x,y)", //
				"");
		check("x", //
				"x");
		check("y", //
				"y");
	}

	public void testClearAttributes() {
		check("SetAttributes(f, {Orderless, Flat})", //
				"");
		check("Attributes(f)", //
				"{Flat,Orderless}");
		check("ClearAttributes(f, Flat)", //
				"");
		check("Attributes(f)", //
				"{Orderless}");
		check("ClearAttributes(f, Flat)", //
				"");
		check("Attributes(f)", //
				"{Orderless}");
	}

	public void testClip() {
		check("Clip(Tan(E),{-1/2,1/2})", //
				"Tan(E)");
		check("Clip(Tan(2*E),{-1/2,1/2})", //
				"-1/2");
		check("Clip(Tan(-2*E),{-1/2,1/2})", //
				"1/2");

		check("Clip(Tan(E), {-1/2,1/2}, {a,b})", //
				"Tan(E)");
		check("Clip(Tan(2*E), {-1/2,1/2}, {a,b})", //
				"a");
		check("Clip(Tan(-2*E), {-1/2,1/2}, {a,b})", //
				"b");

		check("Clip(x)", //
				"Clip(x)");
		check("Clip(1)", //
				"1");
		check("Clip(-1)", //
				"-1");
		check("Clip(Sin(Pi/7))", //
				"Sin(Pi/7)");
		check("Clip(Tan(E))", //
				"Tan(E)");
		check("Clip(Tan(2*E))", //
				"-1");
		check("Clip(Tan(-2*E))", //
				"1");

	}

	public void testCoefficient() {
		check("Coefficient(7*y^w, y, w)", //
				"7");
		check("Coefficient(7*y^(3*w), y, 3*w)", //
				"7");
		check("Coefficient(c*x^(-2)+a+b*x,x,-2)", //
				"c");

		// TODO
		// check("Coefficient(2*x*y+5*x^3,x^3,0)", //
		// "0");
		check("Coefficient(x*y,z,0)", //
				"x*y");
		check("Coefficient(2*x*y+5*x^3,2*x)", //
				"y");
		check("Coefficient((2*x)^7*y+5*x^3,x^3)", //
				"5");
		check("Coefficient(2*x *y+5*x^3,x^3)", //
				"5");

		check("Coefficient(Cos(x*y), Cos(x*y))", //
				"1");
		check("Coefficient(2*x^2,x^2)", //
				"2");
		check("Coefficient(2*x^4,x^2)", //
				"0");
		check("Coefficient(2*x^2,x^3)", //
				"0");
		check("Coefficient((1+2*x)/Sqrt(3),x,1)", //
				"2/Sqrt(3)");
		check("g = (x + 3)^5;Coefficient(g, x, #) & /@ Range(0, Exponent(g, x))", //
				"{243,405,270,90,15,1}");
		// http://oeis.org/A133314
		check("b(0) = 1; " //
				+ "b(n_) := b(n)=-Sum(Binomial(n, j)*a(j)*b(n-j), {j, 1, n}); crow(0) = {1}; " //
				+ "crow(n_) := Coefficient(b(n), #)& /@ (Times @@ (a /@ #)&) /@ IntegerPartitions(n); " //
				+ "Table(crow(n), {n, 0, 8}) // Flatten", //
				"{1,-1,-1,2,-1,6,-6,-1,8,6,-36,24,-1,10,20,-60,-90,240,-120,-1,12,30,-90,20,-360,\n"
						+ "480,-90,1080,-1800,720,-1,14,42,-126,70,-630,840,-420,-630,5040,-4200,2520,\n"
						+ "-12600,15120,-5040,-1,16,56,-168,112,-1008,1344,70,-1680,-1260,10080,-8400,-1680,\n"
						+ "6720,20160,-67200,40320,2520,-50400,151200,-141120,40320}");
		check("Coefficient(a+b*x,x,0)", //
				"a");
		check("Coefficient(a+b*x,x,1)", //
				"b");
		check("Coefficient(a*b*x,x,1)", //
				"a*b");
		check("Coefficient(x*y,y,Exponent(x*y,y))", //
				"x");
		check("Coefficient(-3*a(1)*(2*a(1)^2-a(2))+3*a(1)*a(2)-a(3),a(1)*a(2))", //
				"6");

		check("Coefficient(SeriesData(x, 0, {1, 1, 0, 1, 1, 0, 1, 1}, 0, 9, 1), x, 4)", //
				"1");
		check("Coefficient(x^2*y^2 + 3*x + 4*y+y^w, y, 0)", //
				"3*x");
		check("Coefficient(x^2*y^2 + 3*x + 4*y+3/(y^4), x, 0)", //
				"3/y^4+4*y");
		check("Coefficient(x^2*y^2 + 3*x + 4*y+Sin(y), x, 0)", //
				"4*y+Sin(y)");
		check("Coefficient(x^2*y^2 + 3*x + 4*y+Sin(y), y, 0)", //
				"3*x+Sin(y)");
		check("Coefficient(x^2*y^2 + 3*x + 4*y+Sin(y)^3, x, 0)", //
				"4*y+Sin(y)^3");
		check("Coefficient(x^2*y^2 + 3*x + 4*y+Sin(y)^3, y, 0)", //
				"3*x+Sin(y)^3");

		check("poly=(c*x-2*y+z)^7", //
				"(c*x-2*y+z)^7");
		check("Coefficient(poly, x^2*y*z^4)", //
				"-210*c^2");

		check("coeff=CoefficientList(poly,{x,y,z})", //
				"{{{0,0,0,0,0,0,0,1},{0,0,0,0,0,0,-14,0},{0,0,0,0,0,84,0,0},{0,0,0,0,-280,0,0,0},{\n"
						+ "0,0,0,560,0,0,0,0},{0,0,-672,0,0,0,0,0},{0,448,0,0,0,0,0,0},{-128,0,0,0,0,0,0,0}},{{\n"
						+ "0,0,0,0,0,0,7*c,0},{0,0,0,0,0,-84*c,0,0},{0,0,0,0,420*c,0,0,0},{0,0,0,-1120*c,0,\n"
						+ "0,0,0},{0,0,1680*c,0,0,0,0,0},{0,-1344*c,0,0,0,0,0,0},{448*c,0,0,0,0,0,0,0},{0,0,\n"
						+ "0,0,0,0,0,0}},{{0,0,0,0,0,21*c^2,0,0},{0,0,0,0,-210*c^2,0,0,0},{0,0,0,840*c^2,0,\n"
						+ "0,0,0},{0,0,-1680*c^2,0,0,0,0,0},{0,1680*c^2,0,0,0,0,0,0},{-672*c^2,0,0,0,0,0,0,\n"
						+ "0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{0,0,0,0,35*c^3,0,0,0},{0,0,0,-280*c^3,\n"
						+ "0,0,0,0},{0,0,840*c^3,0,0,0,0,0},{0,-1120*c^3,0,0,0,0,0,0},{560*c^3,0,0,0,0,0,0,\n"
						+ "0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{0,0,0,35*c^4,0,0,0,0},{\n"
						+ "0,0,-210*c^4,0,0,0,0,0},{0,420*c^4,0,0,0,0,0,0},{-280*c^4,0,0,0,0,0,0,0},{0,0,0,\n"
						+ "0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{0,0,21*c^5,0,\n"
						+ "0,0,0,0},{0,-84*c^5,0,0,0,0,0,0},{84*c^5,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,\n"
						+ "0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{0,7*c^6,0,0,\n"
						+ "0,0,0,0},{-14*c^6,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,\n"
						+ "0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}},{{c^7,0,0,0,0,0,0,\n"
						+ "0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,\n"
						+ "0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}}}");
		check("Part(coeff, 3,2,5)", //
				"-210*c^2");

		check("Coefficient(poly, x^5)", //
				"84*c^5*y^2-84*c^5*y*z+21*c^5*z^2");
		check("Coefficient(poly, x, 5)", //
				"84*c^5*y^2-84*c^5*y*z+21*c^5*z^2");
		check("coeff[[6]]", //
				"{{0,0,21*c^5,0,0,0,0,0},{0,-84*c^5,0,0,0,0,0,0},{84*c^5,0,0,0,0,0,0,0},{0,0,0,0,\n"
						+ "0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0}}");
		check("Part(coeff, 6,1,3)", //
				"21*c^5");
		check("Part(coeff, 6,2,2)", //
				"-84*c^5");
		check("Part(coeff, 6,3,1)", //
				"84*c^5");
		// check("Apply(Plus,((Coefficient(x*(b+a),x,#1)*x^#1)&))", "");
		check("Coefficient(x*y,y,1)", //
				"x");
		check("Coefficient(Sin(x*y),y,1)", //
				"0");
		check("Coefficient(x*y,y,Exponent(x*y,y))", //
				"x");

		check("Coefficient(Sin(a)^3*#1 + b*y + c, #1)", //
				"Sin(a)^3");
		check("Coefficient((#1 + 2)^2 + (#1 + 3)^3, #1, 0)", //
				"31");
		check("Coefficient(42*#1^2+y^3*#1^2+(#1 + 2)^2*(#1 + 2)^2,#1,2)", //
				"66+y^3");
		check("Coefficient(#1,#1,1)", //
				"1");
		check("Coefficient(#1^2,#1,2)", //
				"1");

		check("Coefficient(Null,x,0)", //
				"");
		check("Coefficient(Null,x)", //
				"0");

		check("Coefficient(Sin(x^2),x^2)", //
				"0");

		check("Coefficient(Sin(x^2)^2,Sin(x^2),2)", //
				"1");
		check("Coefficient(2*Sin(x^2)^3,Sin(x^2),3)", //
				"2");
		check("Coefficient(f(x)+2*Sin(x^2)^3,Sin(x^2),3)", //
				"2");
		check("Coefficient(f(x^2)+2*f(x^2)^3,f(x^2),3)", //
				"2");
		check("ExpandAll((x + y)*(x + 2*y)*(3*x + 4*y + 5))", //
				"5*x^2+3*x^3+15*x*y+13*x^2*y+10*y^2+18*x*y^2+8*y^3");
		check("Coefficient(Sin(x^2),Sin(x^2))", //
				"1");
		check("Coefficient(x*(b+a),x,1)*x^1", //
				"(a+b)*x");
		check("Coefficient((x + 1)^3, x, 2)", //
				"3");
		check("Coefficient(a*x + b*y + c, x)", //
				"a");
		check("Coefficient(Sin(a)^3*x + b*y + c, x)", //
				"Sin(a)^3");
		check("Coefficient(Sin(a*x)^3*x + b*y + c, x)", //
				"Sin(a*x)^3");
		check("Coefficient((x + 2)^2 + (x + 3)^3, x, 0)", //
				"31");
		check("Coefficient(v,x,1)", //
				"0");
		check("Coefficient(42,x,0)", //
				"42");
		check("Coefficient(42*a,x,0)", //
				"42*a");
		check("Coefficient(x,x,1)", //
				"1");
		check("Coefficient(x^2,x,2)", //
				"1");
		check("Coefficient(42*x^2+y^3*x^2+(x + 2)^2*(x + 2)^2,x,2)", //
				"66+y^3");
		check("Coefficient(2*x*a,x,1)", //
				"2*a");
		check("Coefficient(2*x*a,x,2)", //
				"0");
		check("Coefficient(2*x*a,x,3)", //
				"0");
		check("Coefficient(2*x*a,x,4)", //
				"0");
		check("Coefficient(2*x^2*a+x,x,1)", //
				"1");
		check("Coefficient(2*x^2*a,x,2)", //
				"2*a");
		check("Coefficient(2*x^3*a,x,3)", //
				"2*a");
		check("Coefficient(2*x^4*a,x,4)", //
				"2*a");
		check("Coefficient(0,x,0)", //
				"0");

		// allow multinomials
		check("ExpandAll((x + y)^4)", //
				"x^4+4*x^3*y+6*x^2*y^2+4*x*y^3+y^4");
		check("Coefficient((x + y)^4, x*y^3)", //
				"4");
		check("Coefficient((x + y)^4,  y^4)", //
				"1");
		check("Coefficient((x + y)^4,  y,4)", //
				"1");

		check("Expand((x + y)*(x + 2*y)*(3*x + 4*y + 5))", //
				"5*x^2+3*x^3+15*x*y+13*x^2*y+10*y^2+18*x*y^2+8*y^3");
		check("ExpandAll((x + y)*(x + 2*y)*(3*x + 4*y + 5))", //
				"5*x^2+3*x^3+15*x*y+13*x^2*y+10*y^2+18*x*y^2+8*y^3");
		check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), x^2*y)", //
				"13");
		check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), x*y^2)", //
				"18");
		check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), x*y)", //
				"15");
		check("Coefficient((x + y)*(x + 2*y)*(3*x + 4*y + 5), y^3)", //
				"8");

	}

	public void testCoefficientList() {
		check("CoefficientList(7*y^w, y )", //
				"{7*y^w}");
		check("CoefficientList(2*x*y+5*x^3,2*x)", //
				"{5*x^3+2*x*y}");
		check("CoefficientList((2*x)^7*y+5*x^3,x^3)", //
				"{128*x^7*y,5}");
		check("CoefficientList(2*x *y+5*x^3,x^3)", //
				"{2*x*y,5}");

		check("CoefficientList(Cos(x*y), Cos(x*y))", //
				"{0,1}");
		// http://oeis.org/A000045 - Fibonacci numbers
		check("CoefficientList(Series(-(x/(-1 + x + x^2)), {x, 0, 20}), x)", //
				"{0,1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765}");

		check("CoefficientList(x^2*y^2 + 3*x + 4*y+y^w, {x, y})", //
				"{{y^w,4,0},{3,0,0},{0,0,1}}");
		check("CoefficientList(x^2*y^2 + 3*x + 4*y+3/(y^4), {x, y})", //
				"{{3/y^4,4,0},{3,0,0},{0,0,1}}");
		check("CoefficientList(x^2*y^2 + 3*x + 4*y+Sin(y), {x, y})", //
				"{{Sin(y),4,0},{3,0,0},{0,0,1}}");
		check("CoefficientList(x^2*y^2 + 3*x + 4*y+Sin(y)^3, {x, y})", //
				"{{Sin(y)^3,4,0},{3,0,0},{0,0,1}}");
		check("CoefficientList(0, {x})", //
				"{}");
		check("CoefficientList(0, {x, y})", //
				"{}");
		check("CoefficientList(x^2*y^2 + 3*x + 4*y + 7*y^5, {x, y})", //
				"{{0,4,0,0,0,7},{3,0,0,0,0,0},{0,0,1,0,0,0}}");
		check("CoefficientList(x^2*y^2 + 3*x + 4*y + 7*y^5, {x, y, z})", //
				"{{{0},{4},{0},{0},{0},{7}},{{3},{0},{0},{0},{0},{0}},{{0},{0},{1},{0},{0},{0}}}");
		check("CoefficientList(x^2*y^2 + 3*x + 4*y + 7*y^5+z, {x, y, z})", //
				"{{{0,1},{4,0},{0,0},{0,0},{0,0},{7,0}},{{3,0},{0,0},{0,0},{0,0},{0,0},{0,0}},{{0,\n"
						+ "0},{0,0},{1,0},{0,0},{0,0},{0,0}}}");

		check("CoefficientList(x^2*y^2 + 3*x + 4*y, {x, y})", //
				"{{0,4,0},{3,0,0},{0,0,1}}");
		check("CoefficientList(x^2*y^2 + 3*x + 4*y, {x, y, z})", //
				"{{{0},{4},{0}},{{3},{0},{0}},{{0},{0},{1}}}");

		check("CoefficientList(a+b*x, x)", //
				"{a,b}");
		check("CoefficientList(a+b*x+c*x^2, x)", //
				"{a,b,c}");
		check("CoefficientList(a+c*x^2, x)", //
				"{a,0,c}");
		check("CoefficientList(0, x)", //
				"{}");
		check("CoefficientList((x+3)^5, x)", //
				"{243,405,270,90,15,1}");
		check("CoefficientList(1 + 6*x - x^4, x)", //
				"{1,6,0,0,-1}");
		check("CoefficientList((1 + x)^10 , x)", //
				"{1,10,45,120,210,252,210,120,45,10,1}");
		check("CoefficientList(a*42*x^3+12*b*x+c*4, x)", //
				"{4*c,12*b,0,42*a}");
		check("CoefficientList((1.0 + x)^10 , x)", //
				"{1.0,10.0,45.0,120.0,210.0,252.0,210.0,120.0,45.0,10.0,1}");
	}

	public void testCoefficientRules() {
		check("CoefficientRules(x^3+3*x^2*y+3*x*y^2+y^3, {x,y})", //
				"{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");
		check("CoefficientRules(x^3+3*x^2*y+3*x*y^2+y^3)", //
				"{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");

		check("CoefficientRules(7*y^w, {y,z})", //
				"{{0,0}->7*y^w}");
		check("CoefficientRules(7*y^(3*w), y )", //
				"{{0}->7*y^(3*w)}");
		check("CoefficientRules(c*x^2+a+b*x,x)", //
				"{{2}->c,{1}->b,{0}->a}");
		check("CoefficientRules(c*x^(-2)+a+b*x,x)", //
				"{{0}->a+c/x^2+b*x}");

		check("CoefficientRules(SeriesData(x, 0, {1, 1, 0, 1, 1, 0, 1, 1}, 0, 9, 1))", //
				"{{0}->1+x+x^3+x^4+x^6+x^7+O(x)^9}");
		check("CoefficientRules((x + y)^3)", //
				"{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");
		check("CoefficientRules( a*x*y^2 + b*x^2*z, {x, y, z}, DegreeReverseLexicographic)", //
				"{{1,2,0}->a,{2,0,1}->b}");

		check("CoefficientRules((x + y)^3)", "{{3,0}->1,{2,1}->3,{1,2}->3,{0,3}->1}");
		// check("CoefficientRules(x^2 y^2 + x^3, {x, y})", "{x^3,x^2*y^2}");
		// check("CoefficientRules(x^2 y^2 + x^3, {x,
		// y},\"DegreeLexicographic\")", "{x^2*y^2,x^3}");
		check("CoefficientRules((x + 1)^5, x, Modulus -> 2)", //
				"{{5}->1,{4}->1,{1}->1,{0}->1}");

		check("CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z})", //
				"{{5,4,2}->-10,{2,5,3}->7,{2,1,5}->-10,{1,5,4}->-7,{1,4,3}->6,{1,3,3}->6,{1,2,1}->\n"
						+ "3,{0,4,1}->1,{0,2,1}->-7,{0,0,5}->2}");

		check("CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeLexicographic)", //
				"{{0,0,5}->2,{0,2,1}->-7,{0,4,1}->1,{1,2,1}->3,{1,3,3}->6,{1,4,3}->6,{1,5,4}->-7,{\n"
						+ "2,1,5}->-10,{2,5,3}->7,{5,4,2}->-10}");
		check("CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, DegreeLexicographic)", //
				"{{5,4,2}->-10,{2,5,3}->7,{1,5,4}->-7,{2,1,5}->-10,{1,4,3}->6,{1,3,3}->6,{0,4,1}->\n"
						+ "1,{0,0,5}->2,{1,2,1}->3,{0,2,1}->-7}");
		check("CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeDegreeReverseLexicographic)", //
				"{{0,2,1}->-7,{1,2,1}->3,{0,4,1}->1,{0,0,5}->2,{1,3,3}->6,{1,4,3}->6,{2,1,5}->-10,{\n"
						+ "2,5,3}->7,{1,5,4}->-7,{5,4,2}->-10}");
		check("CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, DegreeReverseLexicographic)", //
				"{{5,4,2}->-10,{2,5,3}->7,{1,5,4}->-7,{1,4,3}->6,{2,1,5}->-10,{1,3,3}->6,{0,4,1}->\n"
						+ "1,{0,0,5}->2,{1,2,1}->3,{0,2,1}->-7}");
		check("CoefficientRules(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeDegreeLexicographic)", //
				"{{0,2,1}->-7,{1,2,1}->3,{0,4,1}->1,{0,0,5}->2,{1,3,3}->6,{2,1,5}->-10,{1,4,3}->6,{\n"
						+ "2,5,3}->7,{1,5,4}->-7,{5,4,2}->-10}");
	}

	public void testCollect() {
		check("Collect(e+f*x, {})", //
				"e+f*x");
		check("Collect(e+f*x, {x})", //
				"e+f*x");
		check("Collect(e+f*x, x)", //
				"e+f*x");
		check("Collect((1 + a + x)^4, x, Simplify)", //
				"(1+a)^4+4*(1+a)^3*x+6*(1+a)^2*x^2+4*(1+a)*x^3+x^4");
		check("Collect(x^2 + y*x^2 + x*y + y + a*y, {x, y})", //
				"(1+a)*y+x*y+x^2*(1+y)");
		check("Collect(a*x^2 + b*x^2 + a*x - b*x + c, x)", //
				"c+(a-b)*x+(a+b)*x^2");
		check("Collect(a*Exp(2*x) + b*Exp(2*x), Exp(2*x))", //
				"(a+b)*E^(2*x)");
		check("a*Exp(2*x) + b*Exp(2*x)", //
				"a*E^(2*x)+b*E^(2*x)");
		// check("Collect(D(f(Sqrt(x^2 + 1)), {x, 3}), Derivative(_)[f][_],
		// Together)", "");
		check("x*(4*a^3+12*a^2+12*a+4)+x^4+(4*a+4)*x^3+(6*a^2+12*a+6)*x^2+a^4+4*a^3+6*a^2+4*a+1", //
				"1+4*a+6*a^2+4*a^3+a^4+(4+12*a+12*a^2+4*a^3)*x+(6+12*a+6*a^2)*x^2+(4+4*a)*x^3+x^4");
		check("x+x^4", //
				"x+x^4");
		check("Collect(a, x)", //
				"a");
		check("Collect(a*y, {x,y})", //
				"a*y");
		check("Collect(42*a, {x,y})", //
				"42*a");
		check("Collect(a*Sqrt(x) + Sqrt(x) + x^(2/3) - c*x + 3*x - 2*b*x^(2/3) + 5, x)", //
				"5+(1+a)*Sqrt(x)+(1-2*b)*x^(2/3)+(3-c)*x");
		check("Collect(3*b*x + x, x)", //
				"(1+3*b)*x");
		check("Collect(a*x^4 + b*x^4 + 2*a^2*x - 3*b*x + x - 7, x)", "-7+(1+2*a^2-3*b)*x+(a+b)*x^4");
		check("Collect((1 + a + x)^4, x)",
				"1+4*a+6*a^2+4*a^3+a^4+(4+12*a+12*a^2+4*a^3)*x+(6+12*a+6*a^2)*x^2+(4+4*a)*x^3+x^4");
		check("Collect((1 + a + x)^4, x, Simplify)", //
				"(1+a)^4+4*(1+a)^3*x+6*(1+a)^2*x^2+4*(1+a)*x^3+x^4");

		check("Collect(a*x + b*y + c*x, x)", //
				"(a+c)*x+b*y");
		check("Collect((x + y + z + 1)^4, {x, y})", //
				"1+x^4+y^4+4*z+y^3*(4+4*z)+x^3*(4+4*y+4*z)+6*z^2+y^2*(6+12*z+6*z^2)+x^2*(6+6*y^2+\n"
						+ "12*z+y*(12+12*z)+6*z^2)+4*z^3+y*(4+12*z+12*z^2+4*z^3)+x*(4+4*y^3+12*z+y^2*(12+12*z)+\n"
						+ "12*z^2+y*(12+24*z+12*z^2)+4*z^3)+z^4");
	}

	public void testCommonest() {
		check("Commonest({b, a, c, 2, a, b, 1, 2}, 4)", //
				"{b,a,2,c}");
		check("Commonest({b, a, c, 2, a, b, 1, 2})", //
				"{b,a,2}");
		check("Commonest({1, 2, 2, 3, 3, 3, 4})", //
				"{3}");
	}

	public void testComplement() {
		check("Complement({3, 2, 7, 5, 2, 2, 3, 4, 5, 6, 1}, {2, 3}, {4, 6, 27, 23})", //
				"{1,5,7}");
		check("Complement({1,2,3},{2,3,4})", //
				"{1}");
		check("Complement({2,3,4},{1,2,3})", //
				"{4}");

	}

	public void testComplex() {
		check("Complex(0,1)*2", //
				"I*2");
		check("Complex(a, I)", //
				"Complex(a,I)");
		check("a*((- 1/3 )*I)", //
				"-I*1/3*a");
		check("Head(2 + 3*I)", //
				"Complex");
		check("Complex(1, 2/3)", //
				"1+I*2/3");
		check("Abs(Complex(3, 4))", //
				"5");
		check("-2 / 3 - I", //
				"-2/3-I");
		check("Complex(10, 0)", //
				"10");
		check("0. + I", //
				"I*1.0");
		check("1 + 0*I", //
				"1");
		check("Head(1 + 0*I)", //
				"Integer");
		check("Complex(0.0, 0.0)", //
				"0.0");
		check("0.*I", //
				"0.0");
		check("0. + 0.*I", //
				"0.0");
		check("1. + 0.*I", //
				"1.0");
		check("0. + 1.*I", //
				"I*1.0");
		check("Complex(1, Complex(0, 1))", //
				"0");
		check("Complex(1, Complex(1, 0))", //
				"1+I");
		check("Complex(1, Complex(1, 1))", //
				"I");
		check("3/4+6/7", //
				"45/28");
		check("Complex(3/4,-(6/7)*I)", //
				"45/28");
	}

	public void testComplexExpand() {
		check("ComplexExpand(a)", //
				"a");
		check("ComplexExpand(42)", //
				"42");
		check("ComplexExpand((-1)^(1/3))", //
				"1/2+I*1/2*Sqrt(3)");
		check("ComplexExpand((-1)^(4/3))", //
				"-1/2-I*1/2*Sqrt(3)");
		check("ComplexExpand(2^(4/3))", //
				"2*2^(1/3)");
		check("ComplexExpand((-2)^(4/3))", //
				"-2*(1/2^(2/3)+(I*Sqrt(3))/2^(2/3))");
		check("ComplexExpand(a*(b+c))", "a*b+a*c");
		check("ComplexExpand((-1)^(1/3)*(1+I*Sqrt(3)))", //
				"1/2+I*1/2*Sqrt(3)+(I*1/2-Sqrt(3)/2)*Sqrt(3)");

		check("ComplexExpand(Cos(x+I*y))", //
				"Cos(x)*Cosh(y)+I*Sin(x)*Sinh(y)");
		check("ComplexExpand(Sin(x+I*y))", //
				"Cosh(y)*Sin(x)+I*Cos(x)*Sinh(y)");
		check("ComplexExpand(Cot(x+I*y))", //
				"-Sin(2*x)/(Cos(2*x)-Cosh(2*y))+(I*Sinh(2*y))/(Cos(2*x)-Cosh(2*y))");
		check("ComplexExpand(Csc(x+I*y))", //
				"(-2*Cosh(y)*Sin(x))/(Cos(2*x)-Cosh(2*y))+(I*2*Cos(x)*Sinh(y))/(Cos(2*x)-Cosh(2*y))");
		check("ComplexExpand(Sec(x+I*y))", //
				"(2*Cos(x)*Cosh(y))/(Cos(2*x)+Cosh(2*y))+(I*2*Sin(x)*Sinh(y))/(Cos(2*x)+Cosh(2*y))");
		check("ComplexExpand(Tan(x+I*y))", //
				"Sin(2*x)/(Cos(2*x)+Cosh(2*y))+(I*Sinh(2*y))/(Cos(2*x)+Cosh(2*y))");
		check("ComplexExpand(Cos(x))", //
				"Cos(x)");
		check("ComplexExpand(Sin(x))", //
				"Sin(x)");
		check("ComplexExpand(Cot(x))", //
				"-Sin(2*x)/(-1+Cos(2*x))");
		check("ComplexExpand(Csc(x))", //
				"(-2*Sin(x))/(-1+Cos(2*x))");
		check("ComplexExpand(Sec(x))", //
				"(2*Cos(x))/(1+Cos(2*x))");
		check("ComplexExpand(Tan(x))", //
				"Sin(2*x)/(1+Cos(2*x))");
	}

	public void testComplexInfinity() {
		check("1 / ComplexInfinity", //
				"0");
		check("ComplexInfinity + ComplexInfinity", //
				"Indeterminate");
		check("ComplexInfinity * Infinity", //
				"ComplexInfinity");
		check("FullForm(ComplexInfinity)", //
				"DirectedInfinity()");
	}

	public void testCompile() {
		if (ToggleFeature.COMPILE) {
			check("f=Compile({x}, E^3-Cos(Pi^2/x)); ", //
					"");
			check(" f(1.4567)", //
					"19.20421");

			check("f=Compile({x}, x^3+Cos(x^2)); ", //
					"");
			check(" f(1.4567)", //
					"2.56739");

			check("f=Compile({x}, x^3+Gamma(x^2)); ", //
					"");
			check(" f(1.4567)", //
					"4.14894");
		}
	}

	public void testComposeList() {
		check("ComposeList({f,g,h}, x)", //
				"{x,f(x),g(f(x)),h(g(f(x)))}");
		check("ComposeList({1 - # &, 1/# &}[[{2, 2, 1, 2, 2, 1}]], x)", //
				"{x,1/x,x,1-x,1/(1-x),1-x,x}");
		check("ComposeList({f, g}[[{1, 2, 1, 1, 2}]], x)", //
				"{x,f(x),g(f(x)),f(g(f(x))),f(f(g(f(x)))),g(f(f(g(f(x)))))}");
		check("ComposeList({a, b, c, d}, x)", //
				"{x,a(x),b(a(x)),c(b(a(x))),d(c(b(a(x))))}");

	}

	public void testComposition() {
		check("Composition(u, v, w)[x, y]", //
				"u(v(w(x,y)))");
		check("Composition(1 + #^# &, a*# &, #/(# + 1) &)[x]", //
				"1+((a*x)/(1+x))^((a*x)/(1+x))");
		check("Composition(f, g, h) @@ {x, y, z}", //
				"f(g(h(x,y,z)))");
	}

	public void testCompoundExpression() {
		check("1; 2; 3;", //
				"");
		check("1; 2; 3", //
				"3");
		check("a=100", //
				"100");
		check("a=100;", //
				"");
		check("a", //
				"100");
		check("Catch($a = 2; Throw($a); $a = 5)", //
				"2");
	}

	public void testCondition() {
		check("q(i_,j_):=q(i,j)=q(i-1,j)+q(i,j-1);q(i_,j_)/;i<0||j<0=0;q(0,0)=1", //
				"1");
		check("Definition(q)", //
				"Attributes(q)={}\n" + "q(0,0)=1\n" + "q(i_,j_)=0/;i<0||j<0\n"
						+ "q(i_,j_):=q(i,j)=q(-1+i,j)+q(i,-1+j)");
		check("q(5,5)", //
				"252");

		check("Condition(Condition(cond(x_),x>1),x<2):=Denominator(x)", //
				"");
		check("cond(3/2)", //
				"2");
		check("cond(0)", //
				"cond(0)");

		check("x /; x > 0", //
				"x/;x>0");
		check("x /; (10==10)", //
				"x/;10==10");

		check("fac(n_ /; n > 0) := n!", //
				"");
		check("fac(3)+fac(-4)", //
				"6+fac(-4)");

		check("Cases({3, -4, 5, -2}, x_ /; x < 0)", //
				"{-4,-2}");
		check("Cases({z(1, 1), z(-1, 1), z(-2, 2)}, z(x_ /; x < 0, y_))", //
				"{z(-1,1),z(-2,2)}");
		check("{1 + a, 2 + a, -3 + a} /. (x_ /; x < 0) + a -> p(x)", //
				"{1+a,2+a,p(-3)}");

		check("{6, -7, 3, 2, -1, -2} /. x_ /; x < 0 -> w", //
				"{6,w,3,2,w,w}");
		check("f(3) /. f(x_) /; x>0 -> t", //
				"t");
		check("f(-3) /. f(x_) /; x>0 -> t", //
				"f(-3)");
		check("f(x_) := p(x) /; x>0", //
				"");
		check("f(3)", //
				"p(3)");
		check("f(-3)", //
				"f(-3)");

		check("f(x_) := Module({u}, u^2 /; ((u = x - 1) > 0))", //
				"");
		check("f(0)", //
				"f(0)");
		check("g(x_) := Module({a}, a = Prime(10^x); (FactorInteger(a + 1)) /; a < 10^6)", //
				"");
		check("g(4)", //
				"{{2,1},{3,1},{5,1},{3491,1}}");
		check("g(5)", //
				"g(5)");
	}

	public void testConditionalExpression() {
		check("ConditionalExpression(a,True)", //
				"a");
		check("ConditionalExpression(a,False)", //
				"Undefined");
	}

	public void testConjugate() {
		check("Conjugate(3*E^(4*I))", //
				"3/E^(I*4)");
		check("Conjugate(Sin(Pi+I))", //
				"I*Sinh(1)");
		check("Conjugate(3 + 4*I)", //
				"3-I*4");
		check("Conjugate(3)", //
				"3");
		check("Conjugate(a + b * I)", //
				"Conjugate(a)-I*Conjugate(b)");
		check("Conjugate(a * b * I)", //
				"-I*Conjugate(a*b)");
		check("Conjugate({{1, 2 + I*4, a + I*b}, {I}})", //
				"{{1,2-I*4,Conjugate(a)-I*Conjugate(b)},{-I}}");
		check("{Conjugate(Pi), Conjugate(E)}", //
				"{Pi,E}");
		check("Conjugate(1.5 + 2.5*I)", //
				"1.5+I*(-2.5)");

		check("Conjugate(1-I)", //
				"1+I");
		check("Conjugate(1+I)", //
				"1-I");
		check("Conjugate(Conjugate(x))", //
				"x");
		check("Conjugate(3*a*z)", //
				"3*Conjugate(a*z)");
		check("Conjugate(E^z)", //
				"E^Conjugate(z)");
		check("Conjugate(Pi)", //
				"Pi");
		check("Conjugate(0)", //
				"0");
		check("Conjugate(I)", //
				"-I");
		check("Conjugate(Indeterminate)", //
				"Indeterminate");
		check("Conjugate(Infinity)", //
				"Infinity");
		check("Conjugate(-Infinity)", //
				"-Infinity");
		check("Conjugate(ComplexInfinity)", //
				"ComplexInfinity");
		check("Conjugate(Transpose({{1,2+I,3},{4,5-I,6},{7,8,9}}))", //
				"{{1,4,7},{2-I,5+I,8},{3,6,9}}");
		check("Conjugate(Zeta(x))", //
				"Zeta(Conjugate(x))");
		check("Conjugate(Zeta(11,7))", //
				"Zeta(11,7)");

		check("Conjugate(Erf(x))", //
				"Erf(Conjugate(x))");
	}

	public void testConjugateTranspose() {
		check("ConjugateTranspose({{1,2+I,3},{4,5-I,6},{7,8,9}})", //
				"{{1,4,7},\n" + " {2-I,5+I,8},\n" + " {3,6,9}}");
		check("ConjugateTranspose(N({{1,2+I,3},{4,5-I,6},{7,8,9}}))", //
				"{{1.0,4.0,7.0},\n" + " {2.0+I*(-1.0),5.0+I*1.0,8.0},\n" + " {3.0,6.0,9.0}}");

		check("ConjugateTranspose({{1, 2*I, 3}, {3 + 4*I, 5, I}})", //
				"{{1,3-I*4},\n" + " {-I*2,5},\n" + " {3,-I}}");
	}

	public void testConstant() {
		check("Attributes(E)", //
				"{Constant,Protected}");
		check("Solve(x + E == 0, E) ", //
				"Solve(E+x==0,E)");
	}

	public void testConstantArray() {
		check("ConstantArray(a, 3)", //
				"{a,a,a}");
		check("ConstantArray(a, {2, 3})", //
				"{{a,a,a},{a,a,a}}");
		check("ConstantArray(c, 10)", //
				"{c,c,c,c,c,c,c,c,c,c}");
		check("ConstantArray(c, {3, 4})", //
				"{{c,c,c,c},{c,c,c,c},{c,c,c,c}}");
	}

	public void testContainsAny() {
		check("ContainsAny({b, a, b}, {a, b, c})", //
				"True");
		check("ContainsAny({d,f,e}, {a, b, c})", //
				"False");
		check("ContainsAny({ }, {a, b, c})", //
				"False");

		check("ContainsAny(1, {1,2,3})", //
				"ContainsAny(1,{1,2,3})");
		check("ContainsAny({1,2,3}, 4)", //
				"ContainsAny({1,2,3},4)");

		check("ContainsAny({1.0,2.0}, {1,2,3})", //
				"False");
		check("ContainsAny({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
				"True");
	}

	public void testContainsAll() {
		check("ContainsAll({b,a,b,c}, {a, b})", //
				"True");
		check("ContainsAll({b,a,b,c}, {a, b, d})", //
				"False");
		check("ContainsAll({b, a, d}, {a, b, c})", //
				"False");
		check("ContainsAll({ }, {a, b, c})", //
				"False");
		check("ContainsAll({a, b, c},{ })", //
				"True");

		check("ContainsAll(1, {1,2,3})", //
				"ContainsAll(1,{1,2,3})");
		check("ContainsAll({1,2,3}, 4)", //
				"ContainsAll({1,2,3},4)");

		check("ContainsAll({1.0,2.0}, {1,2,3})", //
				"False");
		check("ContainsAll({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
				"False");

		check("ContainsAll({1,2,3}, {1.0,2.0})", //
				"False");
		check("ContainsAll({1,2,3}, {1.0,2.0}, SameTest->Equal)", //
				"True");
	}

	public void testContainsOnly() {
		check("ContainsOnly({b, a, a}, {a, b, c})", //
				"True");
		check("ContainsOnly({b, a, d}, {a, b, c})", //
				"False");
		check("ContainsOnly({ }, {a, b, c})", //
				"True");

		check("ContainsOnly(1, {1,2,3})", //
				"ContainsOnly(1,{1,2,3})");
		check("ContainsOnly({1,2,3}, 4)", //
				"ContainsOnly({1,2,3},4)");

		check("ContainsOnly({1.0,2.0}, {1,2,3})", //
				"False");
		check("ContainsOnly({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
				"True");
	}

	public void testContainsExactly() {
		check("ContainsExactly({b,a,b,c}, {a, b,c})", //
				"True");
		check("ContainsExactly({b,a,d,d}, {a, b,c})", //
				"False");
		check("ContainsExactly({b, a, d}, {a, b, c})", //
				"False");
		check("ContainsExactly({ }, {a, b, c})", //
				"False");
		check("ContainsExactly({a, b, c},{ })", //
				"False");

		check("ContainsExactly(1, {1,2,3})", //
				"ContainsExactly(1,{1,2,3})");
		check("ContainsExactly({1,2,3}, 4)", //
				"ContainsExactly({1,2,3},4)");

		check("ContainsExactly({1.0,2.0}, {1,2,3})", //
				"False");
		check("ContainsExactly({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
				"False");

		check("ContainsExactly({1,2,3}, {1.0,2.0})", //
				"False");
		check("ContainsExactly({1,2,1,2}, {1.0,2.0}, SameTest->Equal)", //
				"True");
	}

	public void testContainsNone() {
		check("ContainsNone({d,f,e}, {a, b, c})", //
				"True");
		check("ContainsNone({b, a, b}, {a, b, c})", //
				"False");
		check("ContainsNone({ }, {a, b, c})", //
				"True");

		check("ContainsNone(1, {1,2,3})", //
				"ContainsNone(1,{1,2,3})");
		check("ContainsNone({1,2,3}, 4)", //
				"ContainsNone({1,2,3},4)");

		check("ContainsNone({1.0,2.0}, {1,2,3})", //
				"True");
		check("ContainsNone({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
				"False");
	}

	public void testContext() {
		check("Context(a)", //
				"Global`");
		check("Context()", //
				"Global`");
		check("Context(Context)", //
				"System`");
		check("{x, Global`x}", //
				"{x,x}");
		check("a`b`x", //
				"a`b`x");
	}

	public void test$Context() {
		check("$Context", //
				"Global`");
	}

	public void test$ContextPath() {
		check("$ContextPath", //
				"{System`,Global`}");
	}

	public void test$MachineEpsilon() {
		// use the non-formatted output engine
		check("$MachineEpsilon", //
				"2.22045*10^-16");
		check("a1=(1.0+$MachineEpsilon)", //
				"1.0");
		check("a2=(1.0+$MachineEpsilon/2)", //
				"1.0");
		check("{a1-1.0, a2-1.0}", //
				"{2.22045*10^-16,0.0}");

		// use the non-formatted engine
		checkNumeric("$MachineEpsilon", //
				"2.220446049250313E-16");
		checkNumeric("a1=(1.0+$MachineEpsilon)", //
				"1.0000000000000002");
		checkNumeric("a2=(1.0+$MachineEpsilon/2)", //
				"1.0");
		checkNumeric("{a1-1.0, a2-1.0}", //
				"{2.220446049250313E-16,0.0}");
	}

	public void test$MachinePrecision() {
		check("$MachinePrecision", //
				"16");
		check("N(Sqrt(2), $MachinePrecision)", //
				"1.41421");
		// compare shorter string result to work under unix and windows:
		String evalStr = evalString("N(Sqrt(2), $MachinePrecision+1)");
		String resultStr = "1.4142135623730950";
		assertEquals(evalStr.substring(0, resultStr.length()), resultStr);
	}

	public void test$Version() {
		check("$Version", //
				ConstantDefinitions.VERSION);
	}

	public void testContinue() {
		check("For(i=1, i<=8, i=i+1, If(Mod(i,2) == 0, Continue()); Print(i))", //
				"");
	}

	public void testContinuedFraction() {
		// print message: ContinuedFraction: Positive integer (less than 2147483647) expected at position 2 in
		// ContinuedFraction(Pi,-20).
		check("ContinuedFraction(Pi,-20)", //
				"ContinuedFraction(Pi,-20)");
		check("ContinuedFraction(Pi,a)", //
				"ContinuedFraction(Pi,a)");

		// github #127
		check("ContinuedFraction( (10+(2*Sqrt(10)))/(Sqrt(5)+Sqrt(2))+8/(1-Sqrt(5)))", //
				"{-2}");
		check("(10+(2*Sqrt(10)))/(Sqrt(5)+Sqrt(2))+8/(1-Sqrt(5)) // N", //
				"-2.0");

		check("ContinuedFraction(E,100)", //
				"{2,1,2,1,1,4,1,1,6,1,1,8,1,1,10,1,1,12,1,1,11,3,2,1,3,1,73,6,1,1,1,1,1,2,31,1,1,\n"
						+ "1,2,1,1,2,1,2,15,9,1,3,1,4,2,1,2,1,2,5,5659,1,11,1,1,2,1,1,198,15,5,2,1,1,1,1,2,\n"
						+ "1,1,3,1,51,1,10,4,1,1,6,1,1,1,2,12,1,2,3,2,1,6,5,5,3,1,1}");
		// print message: ContinuedFraction: calculations of double number values require a iteration limit less equal
		// 100.
		check("ContinuedFraction(E,101)", //
				"ContinuedFraction(E,101)");
		check("ContinuedFraction(Sqrt(0))", //
				"{0}");
		check("ContinuedFraction(Sqrt(2))", //
				"{1,{2}}");
		check("ContinuedFraction(Sqrt(3))", //
				"{1,{1,2}}");
		check("ContinuedFraction(Sqrt(4729494))", //
				"{2174,{1,2,1,5,2,25,3,1,1,1,1,1,1,15,1,2,16,1,2,1,1,8,6,1,21,1,1,3,1,1,1,2,2,6,1,\n" //
						+ "1,5,1,17,1,1,47,3,1,1,6,1,1,3,47,1,1,17,1,5,1,1,6,2,2,1,1,1,3,1,1,21,1,6,8,1,1,2,\n" //
						+ "1,16,2,1,15,1,1,1,1,1,1,3,25,2,5,1,2,1,4348}}");
		check("ContinuedFraction(Sqrt(919))", //
				"{30,{3,5,1,2,1,2,1,1,1,2,3,1,19,2,3,1,1,4,9,1,7,1,3,6,2,11,1,1,1,29,1,1,1,11,2,6,\n" //
						+ "3,1,7,1,9,4,1,1,3,2,19,1,3,2,1,1,1,2,1,2,1,5,3,60}}");
		check("ContinuedFraction(Sqrt(13))", "{3,{1,1,1,1,6}}");

		check("ContinuedFraction(0.753)", //
				"{0,1,3,20,1,1,2,1,1}");
		check("ContinuedFraction(0.55)", //
				"{0,1,1,4,2}");
		check("ContinuedFraction(Pi,30)", //
				"{3,7,15,1,292,1,1,1,2,1,3,1,14,3,3,23,1,1,7,4,35,1,1,1,2,3,3,3,3,1}");
		check("ContinuedFraction(47/17)", //
				"{2,1,3,4}");
		check("ContinuedFraction(Sqrt(13),20)", //
				"{3,1,1,1,1,6,1,1,1,1,6,1,1,1,1,6,1,1,1,1}");
	}

	public void testConvergents() {
		check("Convergents({2,3,4,5})", //
				"{2,7/3,30/13,157/68}");
		check("Convergents({1,1,1,1,1})", //
				"{1,2,3/2,5/3,8/5}");
		check("Convergents({a,b,c,d})", //
				"{a,(1+a*b)/b,(a+c+a*b*c)/(1+b*c),(1+a*b+a*d+c*d+a*b*c*d)/(b+d+b*c*d)}");
	}

	public void testCoprimeQ() {
		check("CoprimeQ(7,3)", //
				"True");
		check("CoprimeQ(7)", //
				"False");
		check("CoprimeQ( )", //
				"False");
		check("CoprimeQ(8,9,11)", //
				"True");
		check("CoprimeQ({1, 2, 3, 4, 5}, 6)", //
				"{True,False,False,False,True}");
		check("CoprimeQ(2, 3, 5)", //
				"True");
	}

	public void testCos() {
		check("Cos(1.20000000000000000000000)", //
				"3.623577544766735776383733*10^-1");

		// print argx message
		check("Cos( )", //
				"Cos()");
		check("Cos(a,b)", //
				"Cos(a,b)");

		check("Cos(I*a+I*b*x)/b", //
				"Cosh(a+b*x)/b");
		// TODO return Cosh((1 + 2*I)*a - 3*I*b*x)
		check("Cos((-2+I)*a+3*b*x)", //
				"Cosh((1+I*2)*a-I*3*b*x)");

		check("Cos(-2*a+3*b*x)", //
				"Cos(2*a-3*b*x)");
		check("Cos(-2*a-3*b*x)", //
				"Cos(2*a+3*b*x)");
		check("Refine(Cos(x+k*Pi), Element(k, Integers))", //
				"(-1)^k*Cos(x)");
		check("Cos(5/8*Pi+2*x)", //
				"-Sin(Pi/8+2*x)");
		check("Cos(3/4*Pi+2*x)", //
				"-Sin(Pi/4+2*x)");
		check("Cos(Pi/4 - r/2 - s + x)", //
				"Sin(Pi/4+r/2+s-x)");
		check("Cos(I*x)-I*Sin(I*x)", //
				"Cosh(x)+Sinh(x)");
		check("Cos(3/4*Pi+x)", //
				"-Sin(Pi/4+x)"); // -Cos(Pi/4-x)
		check("Cos(-3/4*Pi+x)", //
				"-Cos(Pi/4+x)");
		check("Cos(e - Pi/2 + f*x)", //
				"Sin(e+f*x)");
		check("Cos(-1/2*E+z)", //
				"Cos(E/2-z)");
		check("Cos(-Pi/2+z)", //
				"Sin(z)");
		check("Cos(e-Pi/2+f*x)", //
				"Sin(e+f*x)");
		check("Cos(e-3/2*Pi+f*x)", //
				"-Sin(e+f*x)");
		check("Cos(e-1+f*x)", //
				"Cos(1-e-f*x)");
		check("Cos(I*a+I*b*x)/b", //
				"Cosh(a+b*x)/b");
		check("Cos(ArcSin(x))", //
				"Sqrt(1-x^2)");
		check("Cos(ArcCos(x))", //
				"x");
		check("Cos(ArcTan(x))", //
				"1/Sqrt(1+x^2)");
		check("Cos(ArcCot(x))", //
				"1/Sqrt(1+1/x^2)");
		check("Cos(ArcCsc(x))", //
				"Sqrt(1-1/x^2)");
		check("Cos(ArcSec(x))", //
				"1/x");
		check("Cos(0)", //
				"1");
		check("Cos(3*Pi)", //
				"-1");
		check("Cos(1.5*Pi)", //
				"0.0");

		check("Cos(z+1/2*Pi)", //
				"-Sin(z)");
		check("Cos(Pi)", //
				"-1");
		check("Cos(z+Pi)", //
				"-Cos(z)");
		check("Cos(z+42*Pi)", //
				"Cos(z)");
		check("Cos(x+y+z+43*Pi)", //
				"-Cos(x+y+z)");
		check("Cos(z+42*a*Pi)", //
				"Cos(42*a*Pi+z)");

		// this rule was moved to FunctionExpand
		check("Cos(Sqrt(x^2))", //
				"Cos(Sqrt(x^2))");
	}

	public void testCosh() {
		check("Cosh(10*Pi*I)", //
				"1");
		check("Cosh(43*Pi*I)", //
				"-1");
		check("Cosh(17/2*Pi*I)", //
				"0");

		check("Refine(Cosh(x+I*k*Pi), Element(k, Integers))", //
				"(-1)^k*Cosh(x)");
		check("Refine(Cosh(x-I*k*Pi), Element(k, Integers))", //
				"(-1)^k*Cosh(x)");
		check("Refine(Cosh(x+4*I*k*Pi), Element(k, Integers))", //
				"Cosh(x)");
		check("Cosh(Pi*I+x)", //
				"-Cosh(x)");
		check("Cosh(10*Pi*I+x)", //
				"Cosh(x)");
		check("Cosh(43*Pi*I+x)", //
				"-Cosh(x)");
		check("Cosh(0)", //
				"1");
		check("Cosh(1/6*Pi*I)", //
				"Sqrt(3)/2");
		check("Cosh(Infinity)", //
				"Infinity");
		check("Cosh(ComplexInfinity)", //
				"Indeterminate");
	}

	public void testCreateDirectory() {
		// Config.FILESYSTEM_ENABLED = true;
		// check("CreateDirectory()", //
		// "C:\\Users\\dev\\AppData\\Local\\Temp\\1539799359607-0");
	}

	public void testCanberraDistance() {
		check("CanberraDistance({-1, -1}, {1, 1})", //
				"2");
	}

	public void testCorrelation() {
		check("Correlation({10, 8, 13, 9, 11, 14, 6, 4, 12, 7, 5}, {8.04, 6.95, 7.58, 8.81, 8.33, 9.96, 7.24, 4.26, 10.84, 4.82, 5.68})", //
				"0.816421");
	}

	public void testCosineDistance() {
		check("N(CosineDistance({7, 9}, {71, 89}))", //
				"0.0000759646");
		check("CosineDistance({a, b}, {c, d})", //
				"1-(a*c+b*d)/(Sqrt(Abs(a)^2+Abs(b)^2)*Sqrt(Abs(c)^2+Abs(d)^2))");
		check("CosineDistance({a, b, c}, {x, y, z})", //
				"1-(a*x+b*y+c*z)/(Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)*Sqrt(Abs(x)^2+Abs(y)^2+Abs(z)^\n" + "2))");
	}

	public void testCosIntegral() {
		checkNumeric("CosIntegral(2.8)", //
				"0.18648838964317638");
	}

	public void testCot() {
		check("Cot(0.0)", //
				"ComplexInfinity");
		check("Cot(0)", //
				"ComplexInfinity");
		// check("Cot(z-Pi/3)", //
		// "-Tan(Pi/6+z)");
		check("Cot(e-Pi/2+f*x)", //
				"-Tan(e+f*x)");
		check("Cot(e+Pi/2+f*x)", //
				"-Tan(e+f*x)");
		check("Cot(ComplexInfinity)", //
				"Indeterminate");
		check("Cot(Indeterminate)", //
				"Indeterminate");
		check("Sin(x)*Cot(x)", //
				"Cos(x)");
		// check("Sin(x)^2*Cot(x)^2", "Cos(x)^2");
		// check("Sin(x)^2*Cot(x)^4", "Cos(x)^2*Cot(x)^2");
		// check("Sin(x)^4*Cot(x)^2", "Cos(x)^2*Sin(x)^2");

		check("Cot(ArcSin(x))", //
				"Sqrt(1-x^2)/x");
		check("Cot(ArcCos(x))", //
				"x/Sqrt(1-x^2)");
		check("Cot(ArcTan(x))", //
				"1/x");
		check("Cot(ArcCot(x))", //
				"x");
		check("Cot(ArcCsc(x))", //
				"Sqrt(1-1/x^2)*x");
		check("Cot(ArcSec(x))", //
				"1/(Sqrt(1-1/x^2)*x)");

		check("Cot(Pi/4)", //
				"1");
		check("Cot(0)", //
				"ComplexInfinity");
		check("Cot(1.)", //
				"0.642093");

		check("Cot(z+1/2*Pi)", //
				"-Tan(z)");
		check("Cot(Pi)", //
				"ComplexInfinity");
		check("Cot(z+Pi)", //
				"Cot(z)");
		check("Cot(z+42*Pi)", //
				"Cot(z)");
		check("Cot(x+y+z+43*Pi)", //
				"Cot(x+y+z)");
		check("Cot(z+42*a*Pi)", //
				"Cot(42*a*Pi+z)");
	}

	public void testCoth() {
		check("Coth(0)", //
				"ComplexInfinity");
		check("Coth(0.)", //
				"ComplexInfinity");
	}

	public void testCount() {
		check("Count({3, 7, 10, 7, 5, 3, 7, 10}, 3)", //
				"2");
		check("Count({{a, a}, {a, a, a}, a}, a, {2})", //
				"5");
		check("count({a, b, a, a, b, c, b}, b)", //
				"3");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b)", //
				"3");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, infinity)", //
				"4");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, {1,2})", //
				"3");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, {1,3})", //
				"4");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, 3)", //
				"4");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, {3})", //
				"1");
		check("count({a, b, f(g(b,a)), a, b, c, b}, b, {-1})", //
				"4");
		check("count({3, 4, x, x^2, x^3}, x^_)", //
				"2");
	}

	public void testCovariance() {
		check("Covariance({0.2, 0.3, 0.1}, {0.3, 0.3, -0.2})", //
				"0.025");
		check("Covariance({a, b, c,d,e}, {x, y, z,v,w})", //
				"1/20*(-(a+b+c-4*d+e)*Conjugate(v)-(a+b+c+d-4*e)*Conjugate(w)-(-4*a+b+c+d+e)*Conjugate(x)-(a\n"
						+ "-4*b+c+d+e)*Conjugate(y)-(a+b-4*c+d+e)*Conjugate(z))");
		check("Covariance({a, b, c}, {x, y, z})", //
				"1/6*(-(-2*a+b+c)*Conjugate(x)-(a-2*b+c)*Conjugate(y)-(a+b-2*c)*Conjugate(z))");
		check("Covariance({a, b}, {x, y})", //
				"1/2*(a-b)*(Conjugate(x)-Conjugate(y))");
		checkNumeric("Covariance({1.5, 3, 5, 10}, {2, 1.25, 15, 8})", //
				"11.260416666666666");
	}

	public void testCross() {
		// check("Cross({a1, b1, c1, d1}, {a2, b2, c2, d2}, {a3, b3, c3, d3})",
		// "{b3 c2 d1-b2 c3 d1-b3 c1 d2+b1 c3 d2+b2 c1 d3-b1 c2 d3,"
		// + "-a3 c2 d1+a2 c3 d1+a3 c1 d2-a1 c3 d2-a2 c1 d3+a1 c2 d3,"
		// + "a3 b2 d1-a2 b3 d1-a3 b1 d2+a1 b3 d2+a2 b1 d3-a1 b2 d3,"
		// + "-a3 b2 c1+a2 b3 c1+a3 b1 c2-a1 b3 c2-a2 b1 c3+a1 b2 c3}");
		check("Cross({a,b}, {c,d})", //
				"-b*c+a*d");
		check("Cross({a, b, c}, {x, y, z})", //
				"{-c*y+b*z,c*x-a*z,-b*x+a*y}");
		check("Cross({x, y})", //
				"{-y,x}");
		check("Cross({x1, y1, z1}, {x2, y2, z2})", //
				"{-y2*z1+y1*z2,x2*z1-x1*z2,-x2*y1+x1*y2}");
		check("Cross({1, 2}, {3, 4, 5})", //
				"Cross({1,2},{3,4,5})");

		check("Cross({1,2,3},{1,1/2,1/3})", //
				"{-5/6,8/3,-3/2}");
		check("Cross(N({1,2,3}),N({1,1/2,1/3}))", //
				"{-0.833333,2.66667,-1.5}");
	}

	public void testCsc() {
		check("Csc(-3/4*Pi+x)", //
				"-Csc(Pi/4+x)");

		check("Csc(5/7*Pi+x)", //
				"Sec(3/14*Pi+x)");
		check("Csc(3/4*Pi+x)", //
				"Sec(Pi/4+x)");

		check("Csc(e - Pi/2 + f*x)", //
				"-Sec(e+f*x)");
		check("Csc(x)^m*Cot(x)", //
				"Cos(x)*Csc(x)^(1+m)");
		check("Csc(x)^m*Cot(x)^3", //
				"Cos(x)^3*Csc(x)^(3+m)");
		check("Csc(3.5)", //
				"-2.85076");
		check("Csc(1.0+3.5*I)", //
				"0.0508282+I*(-0.0325769)");
		check("Csc(0)", //
				"ComplexInfinity");
		check("Csc(1)", //
				"Csc(1)");
		check("Csc(1.)", //
				"1.1884");
		check("Csc(2/5*Pi)", //
				"Sqrt(2-2/Sqrt(5))");
		check("Csc(23/12*Pi)", //
				"-2*Sqrt(2+Sqrt(3))");
		check("Csc(z+1/2*Pi)", //
				"Sec(z)");
		check("Csc(Pi)", //
				"ComplexInfinity");
		check("Csc(z+Pi)", //
				"-Csc(z)");
		check("Csc(z+42*Pi)", //
				"Csc(z)");
		check("Csc(x+y+z+43*Pi)", //
				"-Csc(x+y+z)");
		check("Csc(z+42*a*Pi)", //
				"Csc(42*a*Pi+z)");

		check("Csc(ArcSin(x))", //
				"1/x");
		check("Csc(ArcCos(x))", //
				"1/Sqrt(1-x^2)");
		check("Csc(ArcTan(x))", //
				"Sqrt(1+x^2)/x");
		check("Csc(ArcCot(x))", //
				"Sqrt(1+x^2)");
		check("Csc(ArcCsc(x))", //
				"x");
		check("Csc(ArcSec(x))", //
				"1/Sqrt(1-1/x^2)");
	}

	public void testCsch() {
		check("Refine(Csch(x+I*k*Pi), Element(k, Integers))", //
				"(-1)^k*Csch(x)");
		check("Refine(Csch(x-I*k*Pi), Element(k, Integers))", //
				"(-1)^k*Csch(x)");
		check("Refine(Csch(x-42*I*k*Pi), Element(k, Integers))", //
				"Csch(x)");
		check("Csch(x)^m*Coth(x)^2", //
				"Cosh(x)^2*Csch(x)^(2+m)");
		check("Csch(0)", //
				"ComplexInfinity");
		check("Csch(-x)", //
				"-Csch(x)");
		checkNumeric("Csch(1.8)", //
				"0.3398846914154937");
		check("D(Csch(x),x)", //
				"-Coth(x)*Csch(x)");
	}

	public void testCubeRoot() {
		check("CubeRoot(3 + 4*I)", //
				"(3+I*4)^(1/3)");
		check("CubeRoot(16)", //
				"2*2^(1/3)");
		check("CubeRoot(-5)", //
				"-5^(1/3)");
		check("CubeRoot(-510000)", //
				"-10*510^(1/3)");
		check("CubeRoot(-5.1)", //
				"-1.7213");
		check("CubeRoot(b)", //
				"b^(1/3)");
		check("CubeRoot(-0.5)", //
				"-0.793701");

		check("CubeRoot({-3, -2, -1, 0, 1, 2, 3})", //
				"{-3^(1/3),-2^(1/3),-1,0,1,2^(1/3),3^(1/3)}");
		check("CubeRoot(-2.)", //
				"-1.25992");
	}

	public void testCurl() {
		check("Curl({f(x, y, z), g(x, y, z), h(x, y, z)}, {x, y, z})", //
				"{-Derivative(0,0,1)[g][x,y,z]+Derivative(0,1,0)[h][x,y,z],Derivative(0,0,1)[f][x,y,z]-Derivative(\n"
						+ "1,0,0)[h][x,y,z],-Derivative(0,1,0)[f][x,y,z]+Derivative(1,0,0)[g][x,y,z]}");
	}

	public void testCyclotomic() {
		// https://en.wikipedia.org/wiki/Cyclotomic_polynomial
		check("Cyclotomic(0,x)", //
				"1");
		check("Cyclotomic(1,x)", //
				"-1+x");
		check("Cyclotomic(5,x)", //
				"1+x+x^2+x^3+x^4");
		check("Cyclotomic(10,x)", //
				"1-x+x^2-x^3+x^4");
		check("Cyclotomic(25,x)", //
				"1+x^5+x^10+x^15+x^20");
		check("Cyclotomic(32,x)", //
				"1+x^16");
		check("Cyclotomic(94,x)", //
				"1-x+x^2-x^3+x^4-x^5+x^6-x^7+x^8-x^9+x^10-x^11+x^12-x^13+x^14-x^15+x^16-x^17+x^18-x^\n"
						+ "19+x^20-x^21+x^22-x^23+x^24-x^25+x^26-x^27+x^28-x^29+x^30-x^31+x^32-x^33+x^34-x^\n"
						+ "35+x^36-x^37+x^38-x^39+x^40-x^41+x^42-x^43+x^44-x^45+x^46");
		// The case of the 105-th cyclotomic polynomial is interesting because 105 is the lowest integer that is the
		// product of three distinct odd prime numbers and this polynomial is the first one that has a coefficient other
		// than 1, 0, or −1:
		check("Cyclotomic(105, x)", //
				"1+x+x^2-x^5-x^6-2*x^7-x^8-x^9+x^12+x^13+x^14+x^15+x^16+x^17-x^20-x^22-x^24-x^26-x^\n"
						+ "28+x^31+x^32+x^33+x^34+x^35+x^36-x^39-x^40-2*x^41-x^42-x^43+x^46+x^47+x^48");
	}

	public void testD() {
		check("D(E^(E^x + x),x)", //
				"(1+E^x)*E^(E^x+x)");
		check("D((2*x*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^2),x)", //
				"(-4*e*(Sqrt(d)*Sqrt(-e)+e*x)*x^2)/(d+e*x^2)^2+(2*e*x)/(d+e*x^2)+(2*(Sqrt(d)*Sqrt(-e)+e*x))/(d+e*x^\n"
						+ "2)");
		check("D(ArcTan(x,y),x)", //
				"-y/(x^2+y^2)");
		check("D(ArcTan(x,y),y)", //
				"x/(x^2+y^2)");
		check("D(ArcTan(x,x),x)", //
				"0");
		check("D(Cosh(b*x),x)", //
				"b*Sinh(b*x)");
		check("D(Sinh(x),x)", //
				"Cosh(x)");
		check("D(Sinc(x),x)", //
				"Cos(x)/x-Sin(x)/x^2");
		check("D(SinIntegral(x),x)", //
				"Sinc(x)");
		check("D(SinhIntegral(x),x)", //
				"Sinh(x)/x");
		check("D(CoshIntegral(x),x)", //
				"Cosh(x)/x");
		check("D(Cosh(b*x)*CoshIntegral(b*x),x)", //
				"Cosh(b*x)^2/x+b*CoshIntegral(b*x)*Sinh(b*x)");

		// gradient
		check("D(f(x, y), {{x, y}})", //
				"{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]}");
		// hessian matrix
		check("D(f(x, y), {{x, y}}, {{x, y}})", //
				"{{Derivative(2,0)[f][x,y],Derivative(1,1)[f][x,y]},{Derivative(1,1)[f][x,y],Derivative(\n"
						+ "0,2)[f][x,y]}}");
		// generalization of Hessian matrix for n > 2
		check("D(f(x, y, z), {{x, y, z}, 3})", //
				"{{{Derivative(3,0,0)[f][x,y,z],Derivative(2,1,0)[f][x,y,z],Derivative(2,0,1)[f][x,y,z]},{Derivative(\n"
						+ "2,1,0)[f][x,y,z],Derivative(1,2,0)[f][x,y,z],Derivative(1,1,1)[f][x,y,z]},{Derivative(\n"
						+ "2,0,1)[f][x,y,z],Derivative(1,1,1)[f][x,y,z],Derivative(1,0,2)[f][x,y,z]}},{{Derivative(\n"
						+ "2,1,0)[f][x,y,z],Derivative(1,2,0)[f][x,y,z],Derivative(1,1,1)[f][x,y,z]},{Derivative(\n"
						+ "1,2,0)[f][x,y,z],Derivative(0,3,0)[f][x,y,z],Derivative(0,2,1)[f][x,y,z]},{Derivative(\n"
						+ "1,1,1)[f][x,y,z],Derivative(0,2,1)[f][x,y,z],Derivative(0,1,2)[f][x,y,z]}},{{Derivative(\n"
						+ "2,0,1)[f][x,y,z],Derivative(1,1,1)[f][x,y,z],Derivative(1,0,2)[f][x,y,z]},{Derivative(\n"
						+ "1,1,1)[f][x,y,z],Derivative(0,2,1)[f][x,y,z],Derivative(0,1,2)[f][x,y,z]},{Derivative(\n"
						+ "1,0,2)[f][x,y,z],Derivative(0,1,2)[f][x,y,z],Derivative(0,0,3)[f][x,y,z]}}}");

		check("Refine(D(Abs(x),x), Element(x, Reals))", //
				"x/Abs(x)");

		check("D(HarmonicNumber(x), x)", //
				"Pi^2/6-HarmonicNumber(x,2)");

		check("D(ArcCsc(x),{x,2})", //
				"(-1+2*x^2)/(Sqrt(1-1/x^2)*x^3*(-1+x^2))");
		check("D(ArcSec(x),{x,2})", //
				"(1-2*x^2)/(Sqrt(1-1/x^2)*x^3*(-1+x^2))");

		check("D(x*f(x)*f'(x), x)", //
				"f(x)*f'(x)+x*f'(x)^2+x*f(x)*f''(x)");
		check("D(f(x), x)", //
				"f'(x)");
		check("Sin'(2)", //
				"Cos(2)");
		check("D(Sin(x) + Cos(2*x), {x, 2}) /. x -> 0", //
				"-4");

		check("D(Sin(t), {t, 1})", //
				"Cos(t)");
		check("D(Derivative(0,1,0)[f][x,x*y,z+x^2],x)", //
				"2*x*Derivative(0,1,1)[f][x,x*y,x^2+z]+y*Derivative(0,2,0)[f][x,x*y,x^2+z]+Derivative(\n"
						+ "1,1,0)[f][x,x*y,x^2+z]");

		check("D(x^3 + x^2, x)", //
				"2*x+3*x^2");
		check("D(x^3 + x^2, {x, 2})", //
				"2+6*x");
		check("D(Sin(Cos(x)), x)", //
				"-Cos(Cos(x))*Sin(x)");
		check("D(Sin(x), {x, 2})", //
				"-Sin(x)");
		check("D(Cos(t), {t, 2})", //
				"-Cos(t)");
		check("D(y, x)", //
				"0");
		check("D(x, x)", //
				"1");
		check("D(f(x), x)", //
				"f'(x)");

		check("D(f(x, x), x)", //
				"Derivative(0,1)[f][x,x]+Derivative(1,0)[f][x,x]");
		// chain rule
		check("D(f(2*x+1, 2*y, x+y), x)", //
				"Derivative(0,0,1)[f][1+2*x,2*y,x+y]+2*Derivative(1,0,0)[f][1+2*x,2*y,x+y]");
		check("D(f(x^2, x, 2*y), {x,2}, y) // Expand", //
				"2*Derivative(0,2,1)[f][x^2,x,2*y]+4*Derivative(1,0,1)[f][x^2,x,2*y]+8*x*Derivative(\n"
						+ "1,1,1)[f][x^2,x,2*y]+8*x^2*Derivative(2,0,1)[f][x^2,x,2*y]");

		check("D(x ^ 3 * Cos(y), {{x, y}})", //
				"{3*x^2*Cos(y),-x^3*Sin(y)}");
		check("D(Sin(x) * Cos(y), {{x,y}, 2})", //
				"{{-Cos(y)*Sin(x),-Cos(x)*Sin(y)},{-Cos(x)*Sin(y),-Cos(y)*Sin(x)}}");
		check("D(2/3*Cos(x) - 1/3*x*Cos(x)*Sin(x) ^ 2,x)//Expand  ", //
				"-2/3*Sin(x)-2/3*x*Cos(x)^2*Sin(x)-1/3*Cos(x)*Sin(x)^2+1/3*x*Sin(x)^3");
		check("D(f(#1), {#1,2})", //
				"f''(#1)");
		check("D((#1&)*(t),{t,4})", //
				"0");
		// TODO allow Attributes(f) = {HoldAll}
		check("Attributes(f) = {HoldAll}; Apart(f''(x + x))", //
				"f''(2*x)");
		check("Attributes(f) = {}; Apart(f''(x + x)) ", //
				"f''(2*x)");

		check("D({#^2}, #)", //
				"{2*#1}");

		// Koepf Seite 40-43
		check("D(Sum(k*x^k, {k,0,10}),x)", //
				"1+4*x+9*x^2+16*x^3+25*x^4+36*x^5+49*x^6+64*x^7+81*x^8+100*x^9");
		check("D((x^2+3)*(3*x+2),x)", //
				"2*x*(2+3*x)+3*(3+x^2)");
		check("D(Sin(x^2),x)", //
				"2*x*Cos(x^2)");
		check("D((1+x^2)^Sin(x),x)", //
				"(1+x^2)^Sin(x)*(Cos(x)*Log(1+x^2)+(2*x*Sin(x))/(1+x^2))");
		check("D(Exp(x),x)", //
				"E^x");
		check("D((x^2+3)/(3*x+2),x)", //
				"(-3*(3+x^2))/(2+3*x)^2+(2*x)/(2+3*x)");

		// others -----
		check("D(InverseErf(x),x)", //
				"1/2*E^InverseErf(x)^2*Sqrt(Pi)");

		check("D(f(Sqrt(x^2 + 1)), {x, 3})", //
				"(3*x^3*f'(Sqrt(1+x^2)))/(1+x^2)^(5/2)+(-3*x*f'(Sqrt(1+x^2)))/(1+x^2)^(3/2)+(-3*x^\n"
						+ "3*f''(Sqrt(1+x^2)))/(1+x^2)^2+(3*x*f''(Sqrt(1+x^2)))/(1+x^2)+(x^3*Derivative(3)[f][Sqrt(\n"
						+ "1+x^2)])/(1+x^2)^(3/2)");

		check("f(x_) := x^5 + 6*x^3", //
				"");
		check("D(f(x), x)", //
				"18*x^2+5*x^4");
		check("f'(x)", //
				"18*x^2+5*x^4");
		check("D(f(x), x) /. x->5", //
				"3575");
		check("D(f(x), {x, 3}) /. x -> -1", //
				"96");

		check("D(x^2 * E^(5*y), x)", //
				"2*E^(5*y)*x");
		check("D(x^2 * E^(5*y), y)", //
				"5*E^(5*y)*x^2");
		check("D(x^2 * E^(5*y), {x,2}, {y,3})", //
				"250*E^(5*y)");

		check("D(Sin(g(x)) + g''(x), x)", //
				"Cos(g(x))*g'(x)+Derivative(3)[g][x]");

		check("D(Subscript(x, 1)^2 + Sin(Subscript(x, 1)*Subscript(x, 2)), Subscript(x, 1))", //
				"2*Subscript(x,1)+Cos(Subscript(x,1)*Subscript(x,2))*Subscript(x,2)");

		check("D({3*t^2, 4*t, Sin(t)}, t)", //
				"{6*t,4,Cos(t)}");
		check("D({x^n, {Exp(x), Log(x)}, {Sin(x), Cos(x), Tan(x)}}, x)", //
				"{n/x^(1-n),{E^x,1/x},{Cos(x),-Sin(x),Sec(x)^2}}");
		check("D(x^2 + 5*y^3, {{x, y}})", //
				"{2*x,15*y^2}");
		check("D(x^2 + 5*y^3, {{x, y}, 2})", //
				"{{2,0},{0,30*y}}");
		check("D((x^2+5*y^3+z^4)/E^w,{{x,y}})", //
				"{(2*x)/E^w,(15*y^2)/E^w}");
		check("D(E^(-w)*(x^2 + 5*y^3 + z^4), {{{x, y}, {z, w}}})", //
				"{{(2*x)/E^w,(15*y^2)/E^w},{(4*z^3)/E^w,-(x^2+5*y^3+z^4)/E^w}}");
		check("D(ExpIntegralEi(b*x),x)", //
				"E^(b*x)/x");
	}

	public void testDefault() {
		check("Default(test) = 1", //
				"1");
		check("test(x_., y_.) = {x, y}", //
				"{x,y}");
		check("test(a)", //
				"{a,1}");
		check("test( )", //
				"{1,1}");

		check("Default(Plus)", //
				"0");
		check("Default(Power)", //
				"Default(Power)");
		check("Default(Power, 2)", //
				"1");
		check("Default(Times)", //
				"1");
	}

	public void testDefer() {
		// check("Defer(3*2)", "3*2");
		check("Defer(6/8)==6/8", //
				"6/8==3/4");
	}

	public void testDefinition() {
		check("g(abc_):={abc}", //
				"");
		check("g(abc_):={abc}", //
				"");
		check("Definition(g)", //
				"Attributes(g)={}\n" + //
						"g(abc_):={abc}");

		check("SetAttributes(f,Listable)", //
				"");
		check("f(x_):={x}", //
				"");
		check("Definition(f)", //
				"Attributes(f)={Listable}\n" + "f(x_):={x}");
	}

	public void testDegree() {
		check("\\[Pi]", //
				"Pi");
		check("\\[Degree]", //
				"Pi/180");
		check("\u00B0", //
				"Pi/180");
		check("Sin(30*\\[Pi])", //
				"0");
		check("Sin(30*\\[Degree])", //
				"1/2");
		check("Sin(30\\[Degree])", //
				"1/2");
		check("Sin(30*Degree)", //
				"1/2");
		check("Degree == Pi / 180", //
				"True");
		check("Cos(Degree(x))", //
				"Cos(Degree(x))");
		checkNumeric("N(Degree)", //
				"0.017453292519943295");

		check("Round(Pi/Degree^2)", //
				"10313");
		check("Pi/4 < 60*Degree < Pi", //
				"True");
		check("FullSimplify(Pi/Degree)", //
				"180");
	}

	public void testDelete() {
		// TODO
		// check("Delete({{1,2},{3,4}},{{1,2},{2,1},{2,1}})", //
		// "{{1},{4}}");

		check("Delete(a+b+c,0)", //
				"Sequence(a,b,c)");
		check("Delete({ }, 0)", //
				"Sequence()");
		// print Cannot delete position 5 in {a,b,c,d}
		check("Delete({a, b, c, d}, 5)", //
				"Delete({a,b,c,d},5)");

		check("Delete({a, b, c, d}, 3)", //
				"{a,b,d}");
		check("Delete({a, b, c, d}, -2)", //
				"{a,b,d}");

		check("Delete({{1,2},{3,4}},{1,2})", //
				"{{1},{3,4}}");
		check("Delete({{1,2},{3,4}},{1,0})", //
				"{1,2,{3,4}}");
		// check("Delete({{1,2},{3,4}},{{1,2},{2,1}})", //
		// "{{1},{4}}");
		// check("Delete({{1,2},{3,4}},{{1,2},{2,1},{2,1}})", //
		// "{{1},{4}}");

		// test operator form
		check("Delete(pos)", //
				"Delete(pos)");
		check("Delete(pos)[x]", //
				"Delete(pos)[x]");
		check("Delete(2)[{1,2,3,4}]", //
				"{1,3,4}");
	}

	public void testDeleteCases() {
		check("DeleteCases({a, 1, 2.5, \"string\"}, _Integer|_Real)", //
				"{a,string}");
		check("DeleteCases({a, b, 1, c, 2, 3}, _Symbol)", //
				"{1,2,3}");
		check("Sqrt(Range(10))", //
				"{1,Sqrt(2),Sqrt(3),2,Sqrt(5),Sqrt(6),Sqrt(7),2*Sqrt(2),3,Sqrt(10)}");

		check("DeleteCases(Sqrt(Range(10)), _Integer, {1}, 3)", //
				"{Sqrt(2),Sqrt(3),Sqrt(5),Sqrt(6),Sqrt(7),2*Sqrt(2),Sqrt(10)}");

		check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer)", //
				"{f(a),y,f(8),f(10)}");
		check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -1)", //
				"{f(a),y,f(),f()}");
		check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, -2)", //
				"{1,1,f(a),2,3,y,f(8),9,f(10)}");
		check("DeleteCases({1, 1, f(a), 2, 3, y, f(8), 9, f(10)}, _Integer, {0,4})", //
				"{f(a),y,f(),f()}");

		check("DeleteCases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x)", //
				"{1,1,f(a),2,3,y,g(c,f(8)),9,f(10)}");
		check("DeleteCases({1, 1, f(a), 2, 3, y, g(c,f(8)), 9, f(10)}, f(x_) -> x, -2)", //
				"{1,1,f(a),2,3,y,g(c,f(8)),9,f(10)}");

		check("DeleteCases({1, 1, x, 2, 3, y, 9, y}, _Integer)", //
				"{x,y,y}");

		//
		// check("Cases({3, -4, 5, -2}, x_ /; x < 0)", "{-4,-2}");
		// check("Cases({3, 4, x, x^2, x^3}, x^_)", "{x^2,x^3}");
		// check("Cases({3, 4, x, x^2, x^3}, x^n_ -> n)", "{2,3}");
		// check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {_, _})",
		// "{{1,2},{5,4},{3,3}}");
		// check("Cases({{1, 2}, {2}, {3, 4, 1}, {5, 4}, {3, 3}}, {a_, b_} -> a
		// + b)", "{3,9,6}");

	}

	public void testDeleteDuplicates() {
		check("DeleteDuplicates({1, 7, 8, 4, 3, 4, 1, 9, 9, 2, 1})", //
				"{1,7,8,4,3,9,2}");
		check("DeleteDuplicates({3,2,1,2,3,4}, Less)", //
				"{3,2,1}");
		check("DeleteDuplicates({3,2,1,2,3,4}, Greater)", //
				"{3,3,4}");
		check("DeleteDuplicates({})", //
				"{}");
	}

	public void testDecrement() {
		check("a = 5", //
				"5");
		check("a--", //
				"5");
		check("a", //
				"4");
	}

	public void testDenominator() {
		check("Denominator(-1/p^(1-n))", //
				"p");
		check("Numerator(-1/p^(1-n))", //
				"-p^n");
		check("Denominator( a*x^n*y^- m*Exp(a - b - 2 c + 3 d) )", //
				"E^(b+2*c)*y^m");
		check("Denominator(a^-b/x)", //
				"a^b*x");

		// github #151
		check("N(Denominator(Pi/E))", //
				"2.71828");

		// test undefined option message in stderr:
		check("Denominator(x, y)", //
				"Denominator(x,y)");

		check("Denominator(a/(b*c))", //
				"b*c");
		check("Denominator(a^2*b)", //
				"1");
		check("Denominator(a^2*b^-2*c^-3)", //
				"b^2*c^3");
		check("Denominator(a^2*b^-a*c^-d)", //
				"b^a*c^d");

		check("Denominator(Csc(x))", //
				"1");
		check("Denominator(Csc(x), Trig->True)", //
				"Sin(x)");
		check("Denominator(Csc(x)^4)", //
				"1");
		check("Denominator(Csc(x)^4, Trig->True)", //
				"Sin(x)^4");
		check("Denominator(42*Csc(x))", //
				"1");
		check("Denominator(42*Csc(x), Trig->True)", //
				"Sin(x)");
		check("Denominator(42*Csc(x)^3)", //
				"1");
		check("Denominator(42*Csc(x)^3, Trig->True)", //
				"Sin(x)^3");
		check("Denominator(E^(-x)*x^(1/2))", //
				"E^x");

		check("Denominator(Sec(x))", //
				"1");
		check("Denominator(Tan(x))", //
				"1");
		check("Denominator(Tan(x), Trig->True)", //
				"Cos(x)");

		check("Denominator(a / b)", //
				"b");
		check("Denominator(2 / 3)", //
				"3");
		check("Denominator(a + b)", //
				"1");
	}

	public void testDepth() {
		check("Depth(x)", //
				"1");
		check("Depth(g(a))", //
				"2");
		check("Depth({{{a}, b}})", //
				"4");
		check("Depth(x + y)", //
				"2");

		check("Depth({{{{x}}}})", //
				"5");
		check("Depth(1 + 2*I)", //
				"1");
		check("Depth(f(a, b)[c])", //
				"2");
	}

	public void testDerivative() {
		check("f''(k^(1/3) x) - f(k^(1/3) x) == 0 /. x -> (x k^(-1/3))", //
				"-f(x)+f''(x)==0");
		check("h(x_):= 4 x / (x ^ 2 + 3*x + 5)", //
				"");
		check("extremes=Solve(h'(x)==0,x)", //
				"{{x->-Sqrt(5)},{x->Sqrt(5)}}");
		check("h''(x) /.extremes // N", //
				"{1.65086,-0.064079}");

		check("h(x_):=Sin(x)+x^2", //
				"");
		check("h'(x)", //
				"2*x+Cos(x)");
		check("h'(0.5)", //
				"1.87758");
		check("h''(x)", //
				"2-Sin(x)");

		check("h(x_):=x*Cos(x)", //
				"");
		check("h'", //
				"Cos(#1)-Sin(#1)*#1&");
		check("h''", //
				"-2*Sin(#1)-Cos(#1)*#1&");

		check("y''", //
				"Derivative(2)[y]");

		check("Derivative(1)[Sin]", //
				"Cos(#1)&");

		check("Derivative(0)[#1^2&]", //
				"#1^2&");
		check("Derivative(1)[#1^2&]", //
				"2*#1&");
		check("Derivative(1)[3*# ^ 2+5*# ^ 3&] ", //
				"6*#1+15*#1^2&");
		check("Derivative(1)[# ^ 3&] ", //
				"3*#1^2&");
		check("Derivative(2)[# ^ 3&] ", //
				"6*#1&");
		check("Derivative(1)[E ^ #&] ", //
				"E^#1&");
		check("Derivative(1)[Sin]", //
				"Cos(#1)&");
		check("Derivative(3)[Sin]", //
				"-Cos(#1)&");
		check("Sin'(x)", //
				"Cos(x)");
		check("(# ^ 4&)''", //
				"12*#1^2&");
		// check("f'(x) // FullForm", "Derivative(1)[f][x]");
		// TODO
		// check("Derivative(1)[#2 Sin(#1)+Cos(#2)&]", "Cos(#1)*#2&");
		// check("Derivative(1,2)[#2^3 Sin(#1)+Cos(#2)&]", "6*Cos(#1)*#2&");
		// TODO Deriving with respect to an unknown parameter yields 0
		// check("Derivative(1,2,1)[#2^3*Sin(#1)+Cos(#2)&]", "");
		check("Derivative(0,0,0)[a+b+c]", //
				"a+b+c");
		// TODO You can calculate the derivative of custom functions
		// check("f(x_) := x ^ 2", "");
		// check("f'(x)", "");
		check("Derivative(2, 1)[h]", //
				"Derivative(2,1)[h]");
		check("Derivative(2, 0, 1, 0)[k(g)]", //
				"Derivative(2,0,1,0)[k(g)]");

		// parser tests
		check("Hold(f'') // FullForm ", //
				"Hold(Derivative(2)[f])");
		check("Hold(f ' ') // FullForm ", //
				"Hold(Derivative(2)[f])");
		check("Hold(f '' '') // FullForm ", //
				"Hold(Derivative(4)[f])");
		check("Hold(Derivative(x)[4] ') // FullForm ", //
				"Hold(Derivative(1)[Derivative(x)[4]])");

		check("D(f(a,b),b)", //
				"Derivative(0,1)[f][a,b]");
		check("D(f(a,b),x)", //
				"0");
		check("g(u0_,u1_):=D(f(u0,u1),u1);g(a,b)", //
				"Derivative(0,1)[f][a,b]");
		check("Derivative(1)[ArcCoth]", //
				"1/(1-#1^2)&");
		check("y''", //
				"Derivative(2)[y]");
		check("y''(x)", //
				"y''(x)");
		check("y''''(x)", //
				"Derivative(4)[y][x]");

		check("x*x^a", //
				"x^(1+a)");
		check("x/x^(1-x)", //
				"x^x");
		check("Derivative(0,1)[BesselJ][a, x]", //
				"1/2*(BesselJ(-1+a,x)-BesselJ(1+a,x))");
		check("Derivative(1,0)[Power][x, 4]", //
				"4*x^3");
		check("Derivative(1,0)[Power][x, y]", //
				"y/x^(1-y)");
		check("Derivative(1,1)[Power][x, 4]", //
				"x^3+4*x^3*Log(x)");
		check("Derivative(1,1)[Power][x, y]", //
				"x^(-1+y)+(y*Log(x))/x^(1-y)");
		check("Derivative(0,1)[Power][a, x]", //
				"a^x*Log(a)");
		check("Derivative(1,1)[Power][a, x]", //
				"a^(-1+x)+(x*Log(a))/a^(1-x)");
		check("Derivative(1,1)[Power][x, x]", //
				"x^(-1+x)+x^x*Log(x)");

		check("Hold((-1)*Sin(#)&[x])", //
				"Hold(-Sin(#1)&[x])");
		check("Hold(Derivative(1)[Cos][x])", //
				"Hold(Cos'(x))");
		check("Derivative(1)[Cos][x]", //
				"-Sin(x)");
		check("Derivative(1)[Sin][x]", //
				"Cos(x)");
		check("Derivative(4)[Cos][x]", //
				"Cos(x)");
		check("Derivative(1)[Tan]", //
				"Sec(#1)^2&");
		check("Derivative(2)[Tan]", //
				"2*Sec(#1)^2*Tan(#1)&");
		check("Derivative(4)[Log][x]", //
				"-6/x^4");
		check("Derivative(2)[ArcSin][x]", //
				"x/(1-x^2)^(3/2)");

		check("Derivative(1)[2]", //
				"0&");
		check("Derivative(1)[2][x,y,z]", //
				"0");
		check("Derivative(10)[2][x,y,z]", //
				"0");
		check("Derivative(10,9,8)[2][a,b,c]", //
				"0");
		check("Derivative(1)[Cos[3]][z]", //
				"Cos(3)'[z]");
		check("y = x^2 + 1; x = 1", //
				"1");
		check("y'", //
				"0&");
	}

	public void testDesignMatrix() {
		// check("data = Table({i, i^(3/2) }, {i, 2})", //
		// "{{1,1},{2,2*Sqrt(2)}}");
		// check("DesignMatrix(data, x, x)", //
		// "{{1,1},{1,2}}");
		// check("DesignMatrix(data, {x, x^2}, x)", //
		// "{{1,{{x,x^2}},{x,x},{1}},{1,{{x,x^2}},{x,x},{2}}}");
		check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, x, x)", //
				"{{1,2},{1,3},{1,5},{1,7}}");
		check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, f(x), x)", //
				"{{1,f(2)},{1,f(3)},{1,f(5)},{1,f(7)}}");

		check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, x, x)", //
				"{{1,2},{1,3},{1,5},{1,7}}");
		check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, f(x), x)", //
				"{{1,f(2)},{1,f(3)},{1,f(5)},{1,f(7)}}");
	}

	public void testDet() {
		// github #121 - print error
		check("Det({{1, 1, 1}, {2, 2, 2}})", //
				"Det(\n" + "{{1,1,1},\n" + " {2,2,2}})");

		check("Det({{42}})", //
				"42");
		check("Det({{x}})", //
				"x");
		check("Det({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}})", //
				"-2");
		check("Det({{a11, a12},{a21,a22}})", //
				"-a12*a21+a11*a22");
		check("Det({{a,b,c},{d,e,f},{g,h,i}})", //
				"-c*e*g+b*f*g+c*d*h-a*f*h-b*d*i+a*e*i");
	}

	public void testDiagonal() {
		check("Diagonal({{1,2,3},{4,5,6},{7,8,9}})", //
				"{1,5,9}");
		check("Diagonal({{1,2,3},{4,5,6},{7,8,9}}, 1)", //
				"{2,6}");
		check("Diagonal({{1,2,3},{4,5,6},{7,8,9}}, -1)", //
				"{4,8}");
	}

	public void testDiceDissimilarity() {
		check("DiceDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
				"3/7");
		check("DiceDissimilarity({True, False, True}, {True, True, False})", //
				"1/2");
		check("DiceDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
				"0");
		check("DiceDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
				"1");
	}

	public void testDifferences() {
		check("Differences({ })", //
				"{}");
		check("Differences({a})", //
				"{}");
		check("Differences({a,b})", //
				"{-a+b}");
		check("Differences({a,b,c})", //
				"{-a+b,-b+c}");
	}

	public void testDigitQ() {
		check("DigitQ(\"1234\")", //
				"True");
		check("DigitQ(\".\")", //
				"False");
	}

	public void testDimensions() {
		check("Dimensions({a, b})", //
				"{2}");
		check("Dimensions({{a, b, c}, {d, e, f}})", //
				"{2,3}");
		check("Dimensions({{a, b, c}, {d, e}, {f}})", //
				"{3}");
		check("Dimensions({{{{a, b}}}})", //
				"{1,1,1,2}");
		check("Dimensions({{{{a, b}}}}, 2)", //
				"{1,1}");
		check("Dimensions(f(f(x, y), f(a, b), f(s, t)))", //
				"{3,2}");
		check("Dimensions(f(g(x, y), g(a, b), g(s, t)))", //
				"{3}");
		check("Dimensions(Array(a, {2, 1, 4, 3}))", //
				"{2,1,4,3}");
	}

	public void testDiscreteDelta() {
		check("DiscreteDelta(0)", //
				"1");
		check("DiscreteDelta(42)", //
				"0");
		check("DiscreteDelta(-1)", //
				"0");
		check("DiscreteDelta(-42)", //
				"0");
		check("DiscreteDelta({1.6, 1.6000000000000000000000000})", //
				"DiscreteDelta({1.6,1.6})");
		check("DiscreteDelta(1.6, 1.6000000000000000000000000)", //
				"0");
		check("DiscreteDelta(0, 0, 0.0)", //
				"1");
		check("DiscreteDelta(1, 2, 3)", //
				"0");
	}

	public void testDiracDelta() {
		check("DiracDelta(x)", //
				"DiracDelta(x)");
		check("DiracDelta(-x)", //
				"DiracDelta(x)");
		check("DiracDelta(-x, -y)", //
				"DiracDelta(x,y)");
		check("DiracDelta(x, y, z)", //
				"DiracDelta(x,y,z)");
		check("DiracDelta(-x, 0.5, z)", //
				"0");
		check("DiracDelta(0)", //
				"DiracDelta(0)");
		check("DiracDelta(1+I)", //
				"DiracDelta(1+I)");
		check("DiracDelta(-1)", //
				"0");
		check("DiracDelta(-42)", //
				"0");
		check("DiracDelta({1.6, 1.6000000000000000000000000})", //
				"{0,0}");
		check("DiracDelta({-1, 0, 1})", //
				"{0,DiracDelta(0),0}");
		check("DiracDelta(1, 2, 3)", //
				"0");
	}

	public void testDirectedInfinity() {
		check("DirectedInfinity(-2000)", //
				"-Infinity");
		check("DirectedInfinity(2001)", //
				"Infinity");
		check("Table(DirectedInfinity(i), {i, {1, -1, I, -I}})", //
				"{Infinity,-Infinity,I*Infinity,-I*Infinity}");

		check("DirectedInfinity(1 + I)^ -1", //
				"0");
		check("1/DirectedInfinity(1 + I)", //
				"0");
		check("DirectedInfinity(1 + I)", //
				"DirectedInfinity((1+I)/Sqrt(2))");
		check("DirectedInfinity(1+I)+DirectedInfinity(2+I)", //
				"DirectedInfinity((1+I)/Sqrt(2))+DirectedInfinity((2+I)/Sqrt(5))");

		check("DirectedInfinity(Sqrt(3))", //
				"Infinity");
		check("DirectedInfinity(1) + DirectedInfinity(-1)", //
				"Indeterminate");

		check("DirectedInfinity(1)", //
				"Infinity");
		check("DirectedInfinity()", //
				"ComplexInfinity");

		check("DirectedInfinity(Indeterminate)", //
				"ComplexInfinity");
		check("ComplexInfinity+b", //
				"ComplexInfinity");
		// Power()
		check("0^(-1)", //
				"ComplexInfinity");
		check("{Exp(Infinity), Exp(-Infinity)}", //
				"{Infinity,0}");
		check("1^Infinity", //
				"Indeterminate");
		check("1^(-Infinity)", //
				"Indeterminate");
		check("1^ComplexInfinity", //
				"Indeterminate");

		// Times()
		check("DirectedInfinity(x)*DirectedInfinity(y)", //
				"DirectedInfinity(x*y)");
		check("Table(DirectedInfinity(i), {i, {1, -1, I, -I}})", //
				"{Infinity,-Infinity,I*Infinity,-I*Infinity}");
		check("(1 + I)*Infinity", "DirectedInfinity((1+I)/Sqrt(2))");
		check("{DirectedInfinity(), DirectedInfinity(Indeterminate)}", //
				"{ComplexInfinity,ComplexInfinity}");
		check("Infinity/Infinity", //
				"Indeterminate");
		check("3*DirectedInfinity(z)", //
				"DirectedInfinity(z)");
		check("I*DirectedInfinity(z)", //
				"DirectedInfinity(I*z)");

		// Plus()

		check("1+1", //
				"2");
		check("1+Infinity", //
				"Infinity");
		check("1-Infinity", //
				"-Infinity");
		check("Infinity+Infinity", //
				"Infinity");
		check("-Infinity-Infinity", //
				"-Infinity");
		check("Infinity-Infinity", //
				"Indeterminate");
		check("1+Indeterminate", //
				"Indeterminate");
		check("0+ComplexInfinity", //
				"ComplexInfinity");
		check("ComplexInfinity+ComplexInfinity", //
				"Indeterminate");
		check("ComplexInfinity+Indeterminate", //
				"Indeterminate");
		check("1+ComplexInfinity", //
				"ComplexInfinity");
		check("DirectedInfinity(x) + DirectedInfinity(y)", //
				"DirectedInfinity(x)+DirectedInfinity(y)");
		check("DirectedInfinity(x) + DirectedInfinity(y) /. {x -> 1, y -> -1}", //
				"Indeterminate");
	}

	public void testDirichletEta() {
		check("DirichletEta(1)", //
				"Log(2)");
		check("DirichletEta(1.0)", //
				"0.693147");
		check("Table(DirichletEta(x), {x, -4, 4}) // N", //
				"{0.0,-0.125,0.0,0.25,0.5,0.693147,0.822467,0.901543,0.947033}");
	}

	public void testDiscriminant() {
		// github #122
		check("Discriminant((2*x^5)-(19*x^4)+(58*x^3)-(67*x^2)+(56*x)-48,x)", //
				"0");

		check("Discriminant(x^10 - 5*x^7 - 3*x + 9, x)", //
				"177945374758153510836");

		check("Resultant(f+g*x+h*x^2,g+2*h*x, x)", //
				"-g^2*h+4*f*h^2");
		check("Discriminant(x^(1/2), x)",
				"The function: Discriminant(Sqrt(x),x) has wrong argument Sqrt(x) at position:1:\n"
						+ "Polynomial expected!");

		check("Discriminant(f+g*x+h*x^2, x)", //
				"g^2-4*f*h");
		check("Discriminant(a*x^2 + b*x + c, x)", //
				"b^2-4*a*c");
		check("Discriminant(x^10 - 5*x^7 - 3*x + 9, x)", //
				"177945374758153510836");
		check("Discriminant(a*x^3 + b*x^2 + c*x + g, x)", //
				"b^2*c^2-4*a*c^3-4*b^3*g+18*a*b*c*g-27*a^2*g^2");
		check("Discriminant(a*x^4 + b*x^3 + c*x^2 + d*x + e, x)", //
				"b^2*c^2*d^2-4*a*c^3*d^2-4*b^3*d^3+18*a*b*c*d^3-27*a^2*d^4-4*b^2*c^3*e+16*a*c^4*e+\n"
						+ "18*b^3*c*d*e-80*a*b*c^2*d*e-6*a*b^2*d^2*e+144*a^2*c*d^2*e-27*b^4*e^2+144*a*b^2*c*e^\n"
						+ "2-128*a^2*c^2*e^2-192*a^2*b*d*e^2+256*a^3*e^3");
	}

	public void testDisjointQ() {
		check("DisjointQ(f(d,f,e), f(a, b, c))", //
				"True");
		check("DisjointQ(f(b, a, b), f(a, b, c))", //
				"False");

		// same as ContainsNone
		check("DisjointQ({d,f,e}, {a, b, c})", //
				"True");
		check("DisjointQ({b, a, b}, {a, b, c})", //
				"False");
		check("DisjointQ({ }, {a, b, c})", //
				"True");

		check("DisjointQ(1, {1,2,3})", //
				"DisjointQ(1,{1,2,3})");
		check("DisjointQ({1,2,3}, 4)", //
				"DisjointQ({1,2,3},4)");

		check("DisjointQ({1.0,2.0}, {1,2,3})", //
				"True");
		check("DisjointQ({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
				"False");
	}

	public void testDistribute() {
		check("Distribute((a + b).(x + y + z))", //
				"a.x+a.y+a.z+b.x+b.y+b.z");
		check("Distribute(f(a + b, c + d + e))", //
				"f(a,c)+f(a,d)+f(a,e)+f(b,c)+f(b,d)+f(b,e)");
		check("Distribute(f(g(a, b), g(c, d, e)), g)", //
				"g(f(a,c),f(a,d),f(a,e),f(b,c),f(b,d),f(b,e))");
		check("Distribute((a + b + c) (u + v))", //
				"a*u+b*u+c*u+a*v+b*v+c*v");
		check("Distribute((a + b + c) (u + v), Plus)", //
				"a*u+b*u+c*u+a*v+b*v+c*v");
		check("Distribute((a + b + c)*(u + v), Plus, Times)", //
				"a*u+b*u+c*u+a*v+b*v+c*v");
		check("Distribute((a + b + c)^(u + v), Plus, Times)", //
				"(a+b+c)^(u+v)");
		check("Distribute({{a, b}, {x, y, z}, {s, t}}, List)", //
				"{{a,x,s},{a,x,t},{a,y,s},{a,y,t},{a,z,s},{a,z,t},{b,x,s},{b,x,t},{b,y,s},{b,y,t},{b,z,s},{b,z,t}}");
		check("Distribute((x*y*z)^n0, Times)", //
				"x^n0*y^n0*z^n0");
		check("Distribute(And(Or(a, b, c), Or(u, v)), Or, And)", //
				"a&&u||a&&v||b&&u||b&&v||c&&u||c&&v");
		check("Distribute((a + b).(x + y + z))", //
				"a.x+a.y+a.z+b.x+b.y+b.z");
		check("Distribute(f(g(a, b), g(c, d, e)), g, f, gp, fp)", //
				"gp(fp(a,c),fp(a,d),fp(a,e),fp(b,c),fp(b,d),fp(b,e))");
		check("Distribute(Factor(x^10 - 1))", //
				"-1+x^10");
		check("Distribute(Factor(x^6 - 1), Plus, Times, List, Times)", //
				"{-1,-x,-x^2,x,x^2,x^3,-x^2,-x^3,-x^4,-x,-x^2,-x^3,x^2,x^3,x^4,-x^3,-x^4,-x^5,x,x^\n"
						+ "2,x^3,-x^2,-x^3,-x^4,x^3,x^4,x^5,x^2,x^3,x^4,-x^3,-x^4,-x^5,x^4,x^5,x^6}");
		check("Distribute((x*y*z)^n, Times)", //
				"x^n*y^n*z^n");

		// possibly not intended:
		check("Distribute((a + b)*(a + b))", //
				"a^2+b^2");
	}

	public void testDivide() {
		check("Divide(a,b) // FullForm", //
				"Times(a, Power(b, -1))");
		check("1/2/3/5", //
				"1/30");
		check("30 / 5", //
				"6");
		check("1 / 8", //
				"1/8");
		check("Pi / 4", //
				"Pi/4");
		check("Pi / 4.0", //
				"0.785398");
		check("N(1 / 8)", //
				"0.125");
		check("a / b / c", //
				"a/(b*c)");
		check("a / (b / c)", //
				"(a*c)/b");
		check("a / b / (c / (d / e))", //
				"(a*d)/(b*c*e)");
		check("a / (b ^ 2 * c ^ 3 / e)", //
				"(a*e)/(b^2*c^3)");
		check("1 / 4.0", //
				"0.25");
		check("10 / 3 // FullForm", //
				"Rational(10,3)");
		check("a / b // FullForm", //
				"Times(a, Power(b, -1))");
	}

	public void testDivideBy() {
		check("a = 10", //
				"10");
		check("a /= 2", //
				"5");
		check("a", //
				"5");
	}

	public void testDivisible() {
		check("Divisible(2*Pi, Pi/2)", //
				"True");
		check("Divisible(42,7)", //
				"True");
		check("Divisible(10,3)", //
				"False");
		check("Divisible(2^100-1,3)", //
				"True");
		check("Divisible({200, 201, 202, 203}, 3)", //
				"{False,True,False,False}");
		check("Divisible(3/4, 1/4)", //
				"True");
		check("Divisible(3 + I, 2 - I)", //
				"True");
	}

	public void testDivisors() {
		check("a178752(n_):=Sum((1/GCD(n, k))*2^s *EulerPhi(GCD(n, k)/s), {k, 0, n-1}, {s, Divisors(GCD(n, k))})", //
				"");
		check("Table(a178752(n),{n,30})", //
				"{2,5,8,13,16,28,32,56,80,136,208,400,656,1232,2240,4192,7744,14728,27632,52664,\n"
						+ "99968,190984,364768,699760,1342256,2582120,4971248,9588880,18512848,35795104}");
		check("Divisors(990)", //
				"{1,2,3,5,6,9,10,11,15,18,22,30,33,45,55,66,90,99,110,165,198,330,495,990}");
		check("Divisors(341550071728321)", //
				"{1,10670053,32010157,341550071728321}");
		check("Divisors(2010)", //
				"{1,2,3,5,6,10,15,30,67,134,201,335,402,670,1005,2010}");

		check("Divisors(1)", //
				"{1}");
		check("Divisors(6)", //
				"{1,2,3,6}");
		check("Divisors(-2)", //
				"{1,2}");
		check("Divisors(-6)", //
				"{1,2,3,6}");
		check("Divisors(24)", //
				"{1,2,3,4,6,8,12,24}");
		check("Divisors(1729)", //
				"{1,7,13,19,91,133,247,1729}");
		check("FactorInteger(1729)", //
				"{{7,1},{13,1},{19,1}}");

		check("Divisors({605,871,824})", //
				"{{1,5,11,55,121,605},{1,13,67,871},{1,2,4,8,103,206,412,824}}");
	}

	public void testDivisorSigma() {
		check("DivisorSigma(0,12)", //
				"6");
		check("DivisorSigma(1,12)", //
				"28");
		check("DivisorSigma(1,20)", //
				"42");
		check("DivisorSigma(2,20)", //
				"546");
		check("DivisorSigma(2, {1, 2, 3, 4, 5})", //
				"{1,5,10,21,26}");
		check("DivisorSigma(k,10)", //
				"1+2^k+5^k+10^k");
	}

	public void testDo() {
		check("g(x_) := (Do(If(x < 0, Return(0)), {i, {2, 1, 0, -1}}); x)", //
				"");
		check("g(-1)", //
				"-1");

		// http://oeis.org/A005132 - Recaman's sequence
		check("a = {1}; Do( If( a[ [ -1 ] ] - n > 0 && Position( a, a[ [ -1 ] ] - n ) == {}, a = Append( a, a[ [ -1 ] ] - n ), a = Append( a, a[ [ -1 ] ] + n ) ), {n, 2, 70} ); a", //
				"{1,3,6,2,7,13,20,12,21,11,22,10,23,9,24,8,25,43,62,42,63,41,18,42,17,43,16,44,15,\n"
						+ "45,14,46,79,113,78,114,77,39,78,38,79,37,80,36,81,35,82,34,83,33,84,32,85,31,86,\n"
						+ "30,87,29,88,28,89,27,90,26,91,157,224,156,225,155}");
		check("Do(Print(i), {i, 2, 4})", //
				"");
		check("Do(Print({i, j}), {i,1,2}, {j,3,5})", //
				"");
		check("Do(If(i > 10, Break(), If(Mod(i, 2) == 0, Continue()); Print(i)), {i, 5, 20})", "");
		check("Do(Print(\"hi\"),{1+1})", "");

		check("reap(do(if(primeQ(2^n0 - 1), sow(n0)), {n0, 100}))[[2, 1]]", //
				"{2,3,5,7,13,17,19,31,61,89}");
		check("$t = x; Do($t = 1/(1 + $t), {5}); $t", //
				"1/(1+1/(1+1/(1+1/(1+1/(1+x)))))");
		check("Nest(1/(1 + #) &, x, 5)", //
				"1/(1+1/(1+1/(1+1/(1+1/(1+x)))))");
	}

	public void testDot() {
		check("Dot({a,b,c})", //
				"{a,b,c}");
		check("Dot({{1, 2}, {3, 4}, {5, 6}})", //
				"{{1,2},{3,4},{5,6}}");

		// github #121 - print error
		check("Dot({{0,2},{-8,2}},{{{0},0},{0,3}})", //
				"{{0,2},\n" + " {-8,2}}.\n" + "{{{0},0},\n" + " {0,3}}");

		check("#1.#123 // FullForm", //
				"Dot(Slot(1), Slot(123))");
		check("{{1, 2}, {3.0, 4}, {5, 6}}.{1,1}", //
				"{3.0,7.0,11.0}");
		check("{{1, 2}, {3.0, 4}, {5, 6}}.{{1},{1}}", //
				"{{3.0},\n" + " {7.0},\n" + " {11.0}}");
		check("{1,1,1}.{{1, 2}, {3.0, 4}, {5, 6}}", //
				"{9.0,12.0}");
		check("{{1,1,1}}.{{1, 2}, {3.0, 4}, {5, 6}}", //
				"{{9.0,12.0}}");
		check("{1,2,3.0}.{4,5.0,6}", //
				"32.0");

		check("{{1, 2}, {3, 4}, {5, 6}}.{1,1}", //
				"{3,7,11}");
		check("{{1, 2}, {3, 4}, {5, 6}}.{{1},{1}}", //
				"{{3},\n" + " {7},\n" + " {11}}");
		check("{1,1,1}.{{1, 2}, {3, 4}, {5, 6}}", //
				"{9,12}");
		check("{{1,1,1}}.{{1, 2}, {3, 4}, {5, 6}}", //
				"{{9,12}}");
		check("{1,2,3}.{4,5,6}", //
				"32");
	}

	public void testDrop() {
		check("Drop({}, 0)", //
				"{}");
		check("Drop({}, 1)", //
				"Drop({},1)");

		check("Drop({a, b, c, d}, 3)", //
				"{d}");
		check("Drop({a, b, c, d}, -2)", //
				"{a,b}");
		check("Drop({a, b, c, d, e}, {2, -2})", //
				"{a,e}");

		check("A = Table(i*10 + j, {i, 4}, {j, 4})", //
				"{{11,12,13,14},{21,22,23,24},{31,32,33,34},{41,42,43,44}}");
		check("Drop(A, {2, 3}, {2, 3})", //
				"{{11,14},{41,44}}");
		check("Drop(Range(10), {-2, -6, -3})", //
				"{1,2,3,4,5,7,8,10}");
		check("Drop(Range(10), {10, 1, -3})", //
				"{2,3,5,6,8,9}");
		check("Drop(Range(6), {-5, -2, -2}) ", //
				"Drop(Range(6),{-5,-2,-2})");
		check("Drop(Range(6), {0, 3, 1}) ", //
				"Drop(Range(6),{0,3,1})");
		check("Drop(Range(6), {1, 3, 1}) ", //
				"{4,5,6}");

		check("Drop({a, b, c, d, e, f}, 2)", //
				"{c,d,e,f}");
		check("Drop({a, b, c, d, e, f}, -3)", //
				"{a,b,c}");
		check("Drop({a, b, c, d, e, f}, {2, 4})", //
				"{a,e,f}");
		check("Drop({{11, 12, 13}, {21, 22, 23}, {31, 32, 33}}, 1, 2)", //
				"{{23},{33}}");
		check("Drop({{11, 12, 13}, {21, 22, 23}, a, {31, 32, 33}}, 1, 2)", //
				"Drop({{11,12,13},{21,22,23},a,{31,32,33}},1,2)");
	}

	// public void testDSolve() {
	// check("DSolve({(2*y(x)-x^2)+(2*x-y(x)^2)*y'(x)==0},y(x), x)",
	// "{{y(x)->1/(x^2-C(1))}}");
	// check("DSolve({y'(x)==2*x*y(x)^2},y(x), x)", "{{y(x)->1/(-x^2-C(1))}}");
	// check("DSolve({(2*y(x)-x^2)+(2*x-y(x)^2)*y'(x)==0},y(x), x)",
	// "{{y(x)->1/(x^2-C(1))}}");
	// check("DSolve({(6*x*y(x)+y(x)^2)*y'(x)==-2*x-3*y[x]^2},y(x), x)",
	// "{{y(x)->E^x}}");
	// }

	public void testDSolve() {

		check("DSolve(y'(t)==t+y(t), y, t)", //
				"{{y->Function({t},-1-t+E^t*C(1))}}");
		check("DSolve(y'(t)==y(t), y, t)", "{{y->Function({t},E^t*C(1))}}");

		check("DSolve(y'(x)==2*x*y(x)^2, y, x)", "{{y->Function({x},1/(-x^2-C(1)))}}");
		check("DSolve(y'(x)==2*x*y(x)^2, y(x), x)", "{{y(x)->1/(-x^2-C(1))}}");
		check("DSolve({y'(x)==2*x*y(x)^2},y(x), x)", "{{y(x)->1/(-x^2-C(1))}}");

		check("DSolve(D(f(x, y), x) == D(f(x, y), y), f, {x, y})",
				"DSolve(Derivative(1,0)[f][x,y]==Derivative(0,1)[f][x,y],f,{x,y})");

		check("DSolve({y'(x)==y(x),y(0)==1},y(x), x)", "{{y(x)->E^x}}");
		check("DSolve({y'(x)==y(x)+2,y(0)==1},y(x), x)", "{{y(x)->-2+3*E^x}}");

		check("DSolve({y(0)==0,y'(x) + y(x) == a*Sin(x)}, y(x), x)", "{{y(x)->a/(2*E^x)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");
		check("DSolve({y'(x) + y(x) == a*Sin(x),y(0)==0}, y(x), x)", "{{y(x)->a/(2*E^x)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");

		check("DSolve(y'(x) + y(x) == a*Sin(x), y(x), x)", "{{y(x)->C(1)/E^x-1/2*a*Cos(x)+1/2*a*Sin(x)}}");

		check("DSolve(y'(x)-x ==0, y(x), x)", "{{y(x)->x^2/2+C(1)}}");
		check("DSolve(y'(x)+k*y(x) ==0, y(x), x)", "{{y(x)->C(1)/E^(k*x)}}");

		check("DSolve(y'(x)-3/x*y(x)-7==0, y(x), x)", //
				"{{y(x)->-7/2*x+x^3*C(1)}}");
		check("DSolve(y'(x)== 0, y(x), x)", //
				"{{y(x)->C(1)}}");
		check("DSolve(y'(x) + y(x)*Tan(x) == 0, y(x), x)", //
				"{{y(x)->C(1)*Cos(x)}}");
		check("DSolve(y'(x) + y(x)*Cos(x) == 0, y(x), x)", //
				"{{y(x)->C(1)/E^Sin(x)}}");
		check("DSolve(y'(x) == 3*y(x), y(x), x)", //
				"{{y(x)->E^(3*x)*C(1)}}");
		check("DSolve(y'(x) + 2*y(x)/(1-x^2) == 0, y(x), x)", //
				"{{y(x)->C(1)/E^(2*ArcTanh(x))}}");
		check("DSolve(y'(x) == -y(x), y(x), x)", //
				"{{y(x)->C(1)/E^x}}");
		check("DSolve(y'(x) == y(x)+a*Cos(x), y(x), x)", //
				"{{y(x)->E^x*C(1)-1/2*a*Cos(x)+1/2*a*Sin(x)}}");
		// not implemented yet
		check("DSolve(y'(x) == -3*y(x)^2, y(x), x)", //
				"{{y(x)->1/(3*x-C(1))}}");
		check("DSolve({y'(x) == -3*y(x)^2, y(0)==2}, y(x), x)", //
				"{{y(x)->1/(1/2+3*x)}}");
	}

	public void testEasterSunday() {
		check("EasterSunday(2000)", //
				"{2000,4,23}");
		check("EasterSunday(2030)", //
				"{2030,4,21}");
	}

	public void testEdgeList() {
		check("EdgeList(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}))", //
				"{1->2,2->3,1->3,4->2}");
		check("EdgeList(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))", //
				"{1<->2,2<->3,3<->1}");
		check("EdgeList(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
				"{1->2,2->3,3->1}");
	}

	public void testEdgeQ() {
		check("EdgeQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),2 -> 3)", //
				"True");
		check("EdgeQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),2 -> 4)", //
				"False");
	}

	public void testEffectiveInterest() {
		if (ToggleFeature.FINANCE) {
			check("EffectiveInterest(a,b)", //
					"-1+(1+a*b)^(1/b)");
			check("EffectiveInterest({.05, .065, .07, .085}, 1/12)", //
					"{0.0511619,0.0669719,0.0722901,0.0883909}");
			check("EffectiveInterest({.05, .065, .07, .085}, 1/12)", //
					"{0.0511619,0.0669719,0.0722901,0.0883909}");
			check("EffectiveInterest(.1, 0)", //
					"0.105171");
			check("EffectiveInterest(.06, 3)", //
					"0.0567218");
			check("EffectiveInterest({a,b,c,d})", //
					"-1+((1+a)*(1+b)*(1+c)*(1+d))^(1/4)");

			check("FindRoot(EffectiveInterest(r, 1/4) == .05, {r, .05})", //
					"{r->0.0490889}");
			check("Solve(EffectiveInterest(nr, 1/f) == eff, nr) // Quiet", //
					"{{nr->(-1+(1+eff)^(1/f))*f}}");
		}
	}

	public void testEigenvalues() {
		// check("m = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}", //
		// "{{1,2,3},{4,5,6},{7,8,9}}");
		// check("Roots(CharacteristicPolynomial(m,x)==0, x)",//
		// "x==0||x==15/2-3/2*Sqrt(33)||x==15/2+3/2*Sqrt(33)");
		// check("EigenValues(m)",//
		// "{15/2+3/2*Sqrt(33),15/2-3/2*Sqrt(33),0}");

		check("Eigenvalues(A)", "Eigenvalues(A)");
		check("Eigenvalues({{1.0, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
				"{16.11684,-1.11684,-9.29650*10^-16}");
		check("Eigenvalues({{a}})", //
				"{a}");
		check("Eigenvalues({{a, b}, {0, a}})", //
				"{a,a}");
		check("Eigenvalues({{a, b}, {0, d}})", //
				"{1/2*(a+d-Sqrt(a^2-2*a*d+d^2)),1/2*(a+d+Sqrt(a^2-2*a*d+d^2))}");
		check("Eigenvalues({{a,b}, {c,d}})", //
				"{1/2*(a+d-Sqrt(a^2+4*b*c-2*a*d+d^2)),1/2*(a+d+Sqrt(a^2+4*b*c-2*a*d+d^2))}");
		check("Eigenvalues({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})", //
				"{1.0,1.0,1.0}");
	}

	public void testEigenvectors() {
		check("Eigenvectors(A)", //
				"Eigenvectors(A)");
		check("Eigenvectors({{a}})", //
				"1");
		check("Eigenvectors({{a, b}, {0, a}})", //
				"{{1,0},{0,0}}");
		check("Eigenvectors({{a, b}, {0, d}})", //
				"{{1,0},{-b/(a-d),1}}");
		check("Eigenvectors({{a, b}, {c, d}})", //
				"{{(a-d-Sqrt(a^2+4*b*c-2*a*d+d^2))/(2*c),1},{(a-d+Sqrt(a^2+4*b*c-2*a*d+d^2))/(2*c),\n" + //
						"1}}");
		check("Eigenvectors({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})", //
				"{{1.0,0.0,0.0},{0.0,1.0,0.0},{0.0,0.0,1.0}}");
		check("Eigenvectors({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
				"{{0.231971,0.525322,0.818673},{0.816964,0.0901884,-0.636587},{0.408248,-0.816497,0.408248}}");
	}

	public void testElement() {
		check("Element(a | b | c, Reals)", //
				"Element(a|b|c,Reals)");
		check("Element(a | 2 | c, Reals)", //
				"True");
		check("Element(pi, reals)", //
				"True");
		check("Element(sin, reals)", //
				"Element(Sin,Reals)");
		// check("Element(Sqrt(2), #) & /@ {Complexes, Algebraics, Reals, Rationals, Integers, Primes}",//
		// "");
		check("Element(E, Algebraics)", //
				"False");
		check("Element(Pi, Algebraics)", //
				"False");
		check("Element(ComplexInfinity, Algebraics)", //
				"False");
		check("Element(I, Algebraics)", //
				"True");
	}

	public void testElementData() {
		// TODO
		// check("Length(ElementData(All))", "118");

		check("ElementData(74)", //
				"Tungsten");
		check("ElementData(\"He\", \"AbsoluteBoilingPoint\")", //
				"4.22");
		check("ElementData(\"Carbon\", \"IonizationEnergies\")", //
				"{1086.5,2352.6,4620.5,6222.7,37831,47277.0}");
		check("ElementData(16, \"ElectronConfigurationString\")", //
				"[Ne] 3s2 3p4");
		check("ElementData(73, \"ElectronConfiguration\")", //
				"{{2},{2,6},{2,6,10},{2,6,10,14},{2,6,3},{2}}");

		check("ElementData(\"He\", \"ElectroNegativity\")", //
				"Missing(NotApplicable)");
		check("ElementData(\"Tc\", \"SpecificHeat\")", //
				"Missing(NotAvailable)");
		check("ElementData(\"Properties\")", //
				"{Abbreviation,AbsoluteBoilingPoint,AbsoluteMeltingPoint,AtomicNumber,AtomicRadius,AtomicWeight,Block,BoilingPoint,BrinellHardness,BulkModulus,CovalentRadius,CrustAbundance,Density,DiscoveryYear,ElectroNegativity,ElectronAffinity,ElectronConfiguration,ElectronConfigurationString,ElectronShellConfiguration,FusionHeat,Group,IonizationEnergies,LiquidDensity,MeltingPoint,MohsHardness,Name,Period,PoissonRatio,Series,ShearModulus,SpecificHeat,StandardName,ThermalConductivity,VanDerWaalsRadius,VaporizationHeat,VickersHardness,YoungModulus}");

		check("ElementData(6)", "Carbon");
		check("ElementData(\"Carbon\", \"Name\")", //
				"carbon");
		check("ElementData(79, \"Abbreviation\")", //
				"Au");
		check("ElementData(\"Au\", \"StandardName\")", //
				"Gold");
		check("ElementData(\"Gold\", \"AtomicNumber\")", //
				"79");
		check("ElementData(\"Carbon\", \"AtomicNumber\")", //
				"6");
		check("ElementData(\"He\", \"AtomicNumber\")", //
				"2");

		check("ElementData(\"Chlorine\", \"BoilingPoint\")", //
				"-34.04");
		check("ElementData(\"C\", \"AtomicWeight\")", //
				"12.01");
		check("ElementData(117, \"AtomicWeight\")", //
				"294");

		// check("ElementData(\"Pd\", \"AtomicRadius\")", "140");
		check("ElementData(\"Pd\", \"VanDerWaalsRadius\")", //
				"163");
		// check("ElementData(\"Pd\", \"CovalentRadius\")", "131");
		check("ElementData(\"Pd\", \"IonizationEnergies\")", //
				"{804.4,1870,3177}");

		check("ElementData(\"Pd\", \"ElectronAffinity\")", //
				"54.24");
		check("ElementData(\"Pd\", \"ThermalConductivity\")", //
				"71.8");
		check("ElementData(\"Pd\", \"YoungModulus\")", //
				"121");
		check("ElementData(\"Pd\", \"PoissonRatio\")", //
				"0.39");
		check("ElementData(\"Pd\", \"BulkModulus\")", //
				"180");
		check("ElementData(\"Pd\", \"ShearModulus\")", //
				"44");
		check("ElementData(\"Pd\", \"ElectronConfiguration\")", //
				"{{2},{2,6},{2,6,10},{2,6,10}}");
		check("ElementData(\"Pd\", \"ElectronConfigurationString\")", //
				"[Kr] 4d10");
		check("ElementData(\"Pd\", \"ElectronShellConfiguration\")", //
				"{2,8,18,18}");

		// check("ElementData(\"Helium\", \"MeltingPoint\")",
		// "Missing(NotApplicable)");
		// check("ElementData(\"Tungsten\", \"ThermalConductivity\")", "173");

	}

	public void testEliminate() {
		// TODO
		// check("Eliminate({a0*x^p+a1*x^q==0},x)", //
		// "(-a1)*x^q == a0*x^p");

		check("Eliminate({(a*x + b)/(c*x + d)==y},x)", //
				"True");
		check("Eliminate({x == 2 + y, y == z}, y)", //
				"x==2+z");
		check("Eliminate({x == 2 + y, y == z}, {y,v})", //
				"x==2+z");
		check("Eliminate({2*x + 3*y + 4*z == 1, 9*x + 8*y + 7*z == 2}, z)", //
				"11/2*x+11/4*y==1/4");
		check("Eliminate({x^2 + y^2 + z^2 == 1, x - y + z == 2, x^3 - y^2 == z + 1}, {y, z})",
				"-4*x+2*x^2-4*z+2*x*z+2*z^2==-3&&-4+4*x-x^2+x^3+4*z-2*x*z-z^2==1+z");
		check("Eliminate({x == 2 + y^3, y^2 == z}, y)", //
				"x==2+z^(3/2)");

		// use evaluation step: Cos(ArcSin(y)) => Sqrt(1-y^2)
		check("Eliminate({Sin(x)==y, Cos(x) == z}, x)", //
				"Sqrt(1-y^2)==z");
		check("Eliminate({a^x==y, b^(2*x) == z}, x)", //
				"b^((2*Log(y))/Log(a))==z");
	}

	public void testEllipticE() {
		check("EllipticE(I-2.0,0.5)", //
				"-1.47153+I*0.714415");
		check("EllipticE(0, m)", //
				"0");
		check("EllipticE(z,0)", //
				"z");
		check("EllipticE(1/2)", //
				"(Pi^2+2*Gamma(3/4)^4)/(4*Sqrt(Pi)*Gamma(3/4)^2)");
		check("EllipticE(-1)", //
				"(Pi^2+2*Gamma(3/4)^4)/(2*Sqrt(2*Pi)*Gamma(3/4)^2)");
		check("EllipticE(Pi/2, m)", //
				"EllipticE(m)");
		check("EllipticE(5/4,1)", //
				"Sin(5/4)");
		check("EllipticE(0.4)", //
				"1.39939");
		// needs formula for complex numbers
		// check("Table(EllipticE(x ), {x,-2.0, 2.0, 1/4})", "");

		check("EllipticE(2,0.999999)", //
				"1.09071");
		check("EllipticE(-Pi/2,0.5)", //
				"-1.35064");

		check("Table(EllipticE(x,0.5), {x,-2.0, 2.0, 1/4})", //
				"{-1.6629,-1.47803,-1.30054,-1.12005,-0.92733,-0.717351,-0.489911,-0.248708,0.0," + //
						"0.248708,0.489911,0.717351,0.92733,1.12005,1.30054,1.47803,1.6629}");
		check("Table(EllipticE(I+x,0.5), {x,-2.0, 2.0, 1/4})", //
				"{-1.47153+I*0.714415,-1.34791+I*0.585355,-1.37918+I*0.53464,-1.29305+I*0.657158,-1.12766+I*0.789102," + //
						"-0.901309+I*0.911354,-0.627973+I*1.00908,-0.322266+I*1.07186,I*1.09348,0.322266+I*1.07186,0.627973+I*1.00908,"
						+ //
						"0.901309+I*0.911354,1.12766+I*0.789102,1.29305+I*0.657158,1.37918+I*0.53464,1.34791+I*0.585355,1.47153+I*0.714415}");
	}

	public void testEllipticF() {
		// see github #109
		check("EllipticF(3,1/2)//N", //
				"3.56632");
		check("EllipticF(17/2*Pi, m)", //
				"17*EllipticK(m)");
		check("EllipticF(Pi/2, m)", //
				"EllipticK(m)");
		check("EllipticF(a+42*Pi,m)", //
				"EllipticF(a,m)+84*EllipticK(m)");
		check("EllipticF(-z,m)", //
				"-EllipticF(z,m)");
		check("EllipticF(0, m)", //
				"0");
		check("EllipticF(z,0)", //
				"z");
		check("EllipticF(5/4, 1)", //
				"Log(Sec(5/4)+Tan(5/4))");
		check("EllipticF(3, 1)", //
				"ComplexInfinity");
		check("EllipticF(Pi, 1)", //
				"ComplexInfinity");
		check("Table(EllipticF(x,0.5), {x,-2.0, 2.0, 1/4})", //
				"{-2.44438,-2.10618,-1.75404,-1.4077,-1.08322,-0.785382,-0.510467,-0.251304,0.0,0.251304,0.510467,0.785382,1.08322,1.4077,1.75404,2.10618,2.44438}");
		check("Table(EllipticF(I+x,0.5), {x,-2.0, 2.0, 1/4})", //
				"{-2.77616+I*1.25663,-2.53768+I*1.51416,-1.26198+I*1.69875,-1.03395+I*1.34928,-0.804237+I*1.16274,-0.591003+I*1.04644,"
						+ //
						"-0.388636+I*0.973573,-0.192779+I*0.933104,I*0.920097,0.192779+I*0.933104,0.388636+I*0.973573,0.591003+I*1.04644,0.804237+I*1.16274,"
						+ //
						"1.03395+I*1.34928,1.26198+I*1.69875,2.53768+I*1.51416,2.77616+I*1.25663}");

	}

	public void testEllipticK() {
		check("EllipticK(0.5)", //
				"1.85407");
		check("EllipticK(-1.0+I)", //
				"1.26549+I*0.162237");
		check("Table(EllipticK(x), {x,-1.0, 1.0, 1/4})", //
				"{1.31103,1.35906,1.41574,1.48441,1.5708,1.68575,1.85407,2.15652,ComplexInfinity}");
		check("Table(EllipticK(x+I), {x,-1.0, 1.0, 1/4})", //
				"{1.26549+I*0.162237,1.30064+I*0.18478,1.33866+I*0.213052,1.37925+I*0.249038,1.42127+I*0.29538,1.46203+I*0.355241,1.49611+I*0.431362,1.51493+I*0.523542,1.50924+I*0.625146}");
	}

	public void testEllipticPi() {
		check("Table(EllipticPi(x+I,0.5), {x,-2.0, 2.0, 1/4})", //
				"{0.978856+I*0.171427,1.01788+I*0.193752,1.06089+I*0.221026,1.10832+I*0.254803,1.16047+I*0.297252," //
						+ "1.21733+I*0.351426,1.27808+I*0.421604,1.34015+I*0.513624,1.39738+I*0.63483,1.437+I*0.79243," //
						+ "1.43661+I*0.987709,1.36724+I*1.20422,1.21283+I*1.39928,0.996036+I*1.52334,0.768937+I*1.55932," //
						+ "0.57283+I*1.52838,0.420978+I*1.46215}");

		// TODO improve see discussion: https://github.com/paulmasson/math/issues/6
		check("EllipticPi(2.0, 0.5)", //
				"-0.313545+I*(-1.8138)");

		check("EllipticPi(0,z,m)", //
				"EllipticF(z,m)");
		check("EllipticPi(n,Pi/2,n)", //
				"EllipticE(n)/(1-n)");
		check("EllipticPi(n,Pi/2,x)", //
				"EllipticPi(n,x)");
		check("EllipticPi(n,0)", //
				"Pi/(2*Sqrt(1-n))");
		check("EllipticPi(n,1)", //
				"Infinity/Sign(1-n)");
		check("EllipticPi(n,n)", //
				"EllipticE(n)/(1-n)");
		check("EllipticPi(0.4,0.6)", //
				"2.59092");
		check("EllipticPi(1/3, Pi/5, 0.3)", //
				"0.668735");
		// TODO improve see discussion: https://github.com/paulmasson/math/issues/6
		check("Table(EllipticPi(x,0.5), {x,-2.0, 2.0, 1/4})", //
				"{1.0227,1.07184,1.12843,1.19454,1.27313,1.36859,1.48785,1.64253,1.85407,2.16762,2.70129,3.93061,ComplexInfinity,-0.592756+I*(-4.05578),-0.45672+I*(-2.7207),-0.371748+I*(-2.14612),-0.313545+I*(-1.8138)}");

	}

	public void testEllipticTheta() {
		check("EllipticTheta(1,0,x)", //
				"0");
		check("EllipticTheta(1,x,0)", //
				"0");
		check("EllipticTheta(2,x,0)", //
				"0");
		check("EllipticTheta(3,x,0)", //
				"1");
		check("EllipticTheta(4,x,0)", //
				"1");
		check("EllipticTheta(1,Pi,1/2)", //
				"0");
		check("EllipticTheta({1, 2, 3, 4}, z, q)", //
				"{EllipticTheta(1,z,q),EllipticTheta(2,z,q),EllipticTheta(3,z,q),EllipticTheta(4,z,q)}");

		check("EllipticTheta(3, 0.4+I, 0.5 )", //
				"2.89461+I*(-6.54061)");
		check("EllipticTheta(1, 2., 1/3)", //
				"1.42788");
		check("EllipticTheta(2, 5.0, 0.5)", //
				"0.183328");
		check("EllipticTheta(3, 0.4, 0.5)", //
				"1.69015");
		check("EllipticTheta(3, 1/4, 1/3) // N", //
				"1.5984");
		check("EllipticTheta(1, 0, 0) // N", //
				"0.0");

		check("Table(EllipticTheta(4, x,0.5), {x,-2.0, 2.0, 1/4})", //
				"{1.63213,2.03256,2.1136,1.83522,1.33069,0.806366," //
						+ "0.411527,0.189666,0.121124,0.189666,0.411527,0.806366," //
						+ "1.33069,1.83522,2.1136,2.03256,1.63213}");

		check("Table(EllipticTheta(4, x), {x,-0.9, 0.9, 1/4})", //
				"{5.46055,2.70051,1.85173,1.30101,0.8002,0.329855,0.0396032,2.24177*10^-6}");
		check("Table(EllipticTheta(4, x*I), {x,-0.9, 0.9, 1/4})", //
				"{2.73027+I*2.73027,1.35904+I*1.34147,1.0512+I*0.800524,1.00101+I*0.3,1.0002+I*(-0.2),1.03001+I*(-0.700158),1.25976+I*(-1.22016),2.19833+I*(-2.19833)}");

		// TODO: improve for case "EllipticTheta: Unsupported elliptic nome"
		check("EllipticTheta(3, 0.4+I, 0.5+I)", //
				"EllipticTheta(3,0.4+I*1.0,0.5+I*1.0)");
	}

	public void testJacobiZeta() {
		// check("JacobiZeta(2,0.5)", //
		// "-0.117772");

		check("JacobiZeta(z, 0)", //
				"0");
		check("JacobiZeta(-5/4, 1)", //
				"-Sin(5/4)");
		check("JacobiZeta(0, m)", //
				"0");
		check("JacobiZeta(Pi/2, m)", //
				"0");
		check("JacobiZeta(z, Infinity)", //
				"ComplexInfinity");
		check("JacobiZeta(z, -Infinity)", //
				"ComplexInfinity");
	}

	public void testEqual() {
		// https://github.com/axkr/symja_android_library/issues/142
		check("20-x*y*(x+y)==0", //
				"x*y*(x+y)==20");
		check("Tan(5*ArcTan(29/278) + 7 ArcTan(3/79))==1", //
				"True");
		check("-I==1", //
				"False");
		check("1/2*(1+Sqrt(5))==GoldenRatio", //
				"True");
		check("x^2+4*x+4==(x+2)^2", //
				"True");
		check("x^2+x==x*(x+1)", //
				"True");

		// github issue #42
		check("1-i==1.0-i", //
				"True");

		// Issue #174
		check("x/(y*x)==0.25", //
				"1/y==0.25");

		check("a==a", //
				"True");
		check("a==b", //
				"a==b");
		check("1==1.", //
				"True");
		check("{{1}, {2}} == {{1}, {2}}", //
				"True");
		check("{1, 2} == {1, 2, 3}", //
				"False");
		// check("N(E, 100) == N(E, 150)", "True");

		check("E > 1", //
				"True");
		check("Pi == 3.14", //
				"False");
		check("Pi ^ E == E ^ Pi", //
				"False");
		check("N(E, 3) == N(E)", //
				"True");
		check("{1, 2, 3} < {1, 2, 3}", //
				"{1,2,3}<{1,2,3}");
		check("E == N(E)", //
				"True");
		check("{Equal(Equal(0, 0), True), Equal(0, 0) == True}", //
				"{True,True}");
		check("{True,False,True==False,True!=False}", //
				"{True,False,False,True}");
		check("{Mod(6, 2) == 0, Mod(6, 4) == 0, (Mod(6, 2) == 0) == (Mod(6, 4) == 0), (Mod(6, 2) == 0) != (Mod(6, 4) == 0)}", //
				"{True,False,False,True}");
		check("a == a == a", //
				"True");
		check("{Equal(), Equal(x), Equal(1)}", //
				"{True,True,True}");

		check("{\"a\",\"b\"}=={\"a\",\"b\"}", //
				"True");
		check("{\"a\",\"b\"}=={\"b\",\"a\"}", //
				"False");
		check("{\"a\",b}=={\"a\",c}", //
				"{a,b}=={a,c}");
		check("a==a==b==c", //
				"a==b==c");
		check("a==a==a==a", //
				"True");
		check("Pi==3", //
				"False");
		check("(E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi==0", //
				"True");
	}

	public void testEquivalent() {
		check("Equivalent(True, True, False)", //
				"False");
		check("Equivalent(a,b,c)", //
				"Equivalent(a,b,c)");
		check("Equivalent(a,b,True,c)", //
				"a&&b&&c");
		check("Equivalent(a)", //
				"True");

		check("Equivalent()", //
				"True");
		check("Equivalent(4)", //
				"True");
		check("Equivalent(a,a)", //
				"True");
		check("Equivalent(a,b,a,b,c)", //
				"Equivalent(a,b,c)");
		check("Equivalent(a,b,c,True,False)", //
				"False");
		check("Equivalent(a,b,c,True)", //
				"a&&b&&c");
		check("Equivalent(a,b,c,False)", //
				"!a&&!b&&!c");
		check("Equivalent(a && (b || c), a && b || a && c) // TautologyQ", //
				"True");
	}

	public void testErf() {
		check("Erf(I*Infinity)", //
				"I*Infinity");
		check("Erf(-I*Infinity)", //
				"-I*Infinity");

		check("Erf(-x)", //
				"-Erf(x)");
		check("Erf(1.0)", //
				"0.842701");
		check("Erf(0)", //
				"0");
		check("{Erf(0, x), Erf(x, 0)}", //
				"{Erf(x),-Erf(x)}");

		check("Erf(ComplexInfinity)", //
				"Indeterminate");
		check("Erf(Infinity)", //
				"1");
		check("Erf(-Infinity)", //
				"-1");
		checkNumeric("Erf(0.95)", //
				"0.8208908072732779");
	}

	public void testErfc() {
		checkNumeric("Erfc(5/Sqrt(2))/2 // N", //
				"2.866515718791937E-7");
		check("Erfc(-0.28991)", //
				"1.31819");
		// don't transform negative arg
		check("Erfc(-x) / 2", //
				"Erfc(-x)/2");
		checkNumeric("Erfc(1.0)", //
				"0.15729920705028516");
		check("Erfc(0)", //
				"1");
	}

	public void testErfi() {
		check("Erfi(-42*I*x)", //
				"-I*Erf(42*x)");
		check("Erfi(43*I*x)", //
				"I*Erf(43*x)");
		check("Erfi((-1)^(1/4)*2.0^2)", //
				"(I*1.0)*Erf(-2.82843+I*2.82843)");
		check("Erfi(0)", //
				"0");
		check("Erfi(Infinity)", //
				"Infinity");
		check("Erfi(-Infinity)", //
				"-Infinity");
		check("Erfi(I*Infinity)", //
				"I");
		check("Erfi(-I*Infinity)", //
				"-I");
		check("Erfi(-x)", //
				"-Erfi(x)");
	}

	public void testEuclideanDistance() {
		check("EuclideanDistance({a, b, c}, {x, y, z})", //
				"Sqrt(Abs(a-x)^2+Abs(b-y)^2+Abs(c-z)^2)");
		check("EuclideanDistance({-1, -1}, {1, 1})", //
				"2*Sqrt(2)");
		check("EuclideanDistance({a, b}, {c, d})", //
				"Sqrt(Abs(a-c)^2+Abs(b-d)^2)");
	}

	public void testEulerE() {
		check("Table(EulerE(k), {k, 0, 15})", //
				"{1,0,-1,0,5,0,-61,0,1385,0,-50521,0,2702765,0,-199360981,0}");
	}

	public void testEulerPhi() {
		check("Refine(EulerPhi(p^n),Element(p, Primes)&&Element(n, Integers))", //
				"-1/p^(1-n)+p^n");

		check("EulerPhi(-a)", //
				"EulerPhi(a)");
		check("Table(EulerPhi(k), {k, 0, 20})", //
				"{0,1,1,2,2,4,2,6,4,6,4,10,4,12,6,8,8,16,6,18,8}");
		check("Table(EulerPhi(-k), {k, 0, 20})", //
				"{0,1,1,2,2,4,2,6,4,6,4,10,4,12,6,8,8,16,6,18,8}");
		check("EulerPhi(50!)", //
				"4218559200885839042679312107816703841788854953574400000000000000");
		check("Table(EulerPhi(10^k), {k, 0, 10})", //
				"{1,4,40,400,4000,40000,400000,4000000,40000000,400000000,4000000000}");
	}

	public void testEvaluate() {
		check("{f(2+2, 1+1, -1+2), f(Evaluate(2+2),Evaluate(1+1),-1+2,Evaluate(-1+2))}", //
				"{f(4,2,1),f(4,2,1,1)}");
		check("SetAttributes(hr,HoldRest); {hr(2+2, 1+1, -1+2), hr(2+2,Evaluate(1+1),-1+2,Evaluate(-1+2))}", //
				"{hr(4,1+1,-1+2),hr(4,2,-1+2,1)}");
		check("SetAttributes(hf,HoldFirst); {hf(1+1), hf(Evaluate(1+1))}", //
				"{hf(1+1),hf(2)}");
		check("cheb = ChebyshevT(5, x);Function(x, Evaluate(cheb))", //
				"Function(x,5*x-20*x^3+16*x^5)");
		check("Function(x, Evaluate(cheb))[10]", //
				"1580050");
		check("Hold(Evaluate(1+1),2+2)", //
				"Hold(2,2+2)");
		check("Evaluate(a,b)", //
				"Sequence(a,b)");
		check("x=Plus; {Attributes(x), Attributes(Evaluate(x))}", //
				"{{},{Flat,Listable,NumericFunction,OneIdentity,Orderless,Protected}}");
	}

	public void testExactNumberQ() {
		check("ExactNumberQ(10)", //
				"True");
		check("ExactNumberQ(4.0)", //
				"False");
		check("ExactNumberQ(n)", //
				"False");
		check("ExactNumberQ(1+I)", //
				"True");
		check("ExactNumberQ(1 + 1. * I)", //
				"False");
	}

	public void testExcept() {
		check("Cases({x, a, b, x, c}, Except(x))", //
				"{a,b,c}");
		check("Cases({a, 0, b, 1, c, 2, 3}, Except(1, _Integer))", //
				"{0,2,3}");

		check("Cases({1, 0, 2, 0, 3}, (0|2))", //
				"{0,2,0}");
		check("Cases({1, 0, 2, 0, 3}, Except(0))", //
				"{1,2,3}");
		check("Cases({a, b, 0, 1, 2, x, y}, Except(_Integer))", //
				"{a,b,x,y}");
		check("Cases({a, b, 0, 1, 2, x, y}, Except(0, _Integer))", //
				"{1,2}");
		check("Cases({1, 1, -5, EulerGamma, r, I, 0, Pi, 1/2}, Except(_Integer))", //
				"{EulerGamma,r,I,Pi,1/2}");
	}

	public void testExists() {
		check("Exists(a, f(b)>c)", //
				"f(b)>c");
	}

	public void testExp() {
		check("Exp(x*Log(n))", //
				"n^x");
		check("Exp(42+Log(a)+Log(b))", //
				"a*b*E^42");
		check("Exp(1)", //
				"E");
		checkNumeric("Exp(10.0)", //
				"22026.465794806703");
		check("Exp(x) //FullForm", //
				"Power(E, x)");
		// TODO check("Exp(1.*^20)", "Overflow()");
		check("Exp(1.*^20)", //
				"Infinity");

		check("Exp(a+b)", //
				"E^(a+b)");
		check("E^(I*Pi)", //
				"-1");
		check("E^(2*I*Pi)", //
				"1");
		check("E^(2*I*Pi*3)", //
				"1");
		check("E^(5*I*Pi)", //
				"-1");
		check("E^Infinity", //
				"Infinity");
		check("E^(-Infinity)", //
				"0");
		check("E^(I*Infinity)", //
				"Indeterminate");
		check("E^(-I*Infinity)", //
				"Indeterminate");
		check("E^ComplexInfinity", //
				"Indeterminate");
		check("Conjugate(E^z)", //
				"E^Conjugate(z)");
	}

	public void testExpand() {
		// performance test
		// check("Expand((x + y + z + w)^15 * ((x + y + z + w)^15+w));", //
		// "?");
		// check("test = (x + y + z + w)^15;Length(Expand(test*(test+w)))", //
		// "6272");

		check("Expand((x + 3)^(5/2)+(x + 1)^(3/2))", //
				"Sqrt(1+x)+x*Sqrt(1+x)+9*Sqrt(3+x)+6*x*Sqrt(3+x)+x^2*Sqrt(3+x)");
		check("Expand((x + 1)^(5/2))", //
				"Sqrt(1+x)+2*x*Sqrt(1+x)+x^2*Sqrt(1+x)");
		check("Expand((x + 1)^(-5/2))", //
				"1/(1+x)^(5/2)");

		check("Expand((x + y) ^ 3) ", //
				"x^3+3*x^2*y+3*x*y^2+y^3");
		check("Expand((a + b)*(a + c + d))", //
				"a^2+a*b+a*c+b*c+a*d+b*d");
		check("Expand((a + b)*(a + c + d)*(e + f) + e*a*a)", //
				"2*a^2*e+a*b*e+a*c*e+b*c*e+a*d*e+b*d*e+a^2*f+a*b*f+a*c*f+b*c*f+a*d*f+b*d*f");
		check("Expand((a + b) ^ 2 * (c + d))", //
				"a^2*c+2*a*b*c+b^2*c+a^2*d+2*a*b*d+b^2*d");
		check("Expand((x + y) ^ 2 + x*y)", //
				"x^2+3*x*y+y^2");
		check("Expand(((a + b)*(c + d)) ^ 2 + b (1 + a))", //
				"a^2*c^2+2*a*b*c^2+b^2*c^2+2*a^2*c*d+4*a*b*c*d+2*b^2*c*d+a^2*d^2+2*a*b*d^2+b^2*d^\n" + "2+b(1+a)");
		// TODO return {4 x + 4 y, 2 x + 2 y -> 4 x + 4 y}
		check("Expand({4*(x + y), 2*(x + y) -> 4*(x + y)})", //
				"{4*x+4*y,2*(x+y)->4*(x+y)}");
		check("Expand(Sin(x*(1 + y)))", //
				"Sin(x*(1+y))");
		check("a*(b*(c+d)+e) // Expand ", //
				"a*b*c+a*b*d+a*e");
		check("(y^2)^(1/2)/(2*x+2*y)//Expand", //
				"Sqrt(y^2)/(2*x+2*y)");

		check("2*(3+2*x)^2/(5+x^2+3*x)^3 // Expand ", //
				"18/(5+3*x+x^2)^3+(24*x)/(5+3*x+x^2)^3+(8*x^2)/(5+3*x+x^2)^3");

		check("Expand({x*(1+x)})", //
				"{x+x^2}");
		check("Expand((-g^2+4*f*h)*h)", //
				"-g^2*h+4*f*h^2");
		check("expand((1 + x)^10)", //
				"1+10*x+45*x^2+120*x^3+210*x^4+252*x^5+210*x^6+120*x^7+45*x^8+10*x^9+x^10");
		check("expand((1 + x + y)*(2 - x)^3)", //
				"8-4*x-6*x^2+5*x^3-x^4+8*y-12*x*y+6*x^2*y-x^3*y");
		check("expand((x + y)/z)", //
				"x/z+y/z");
		check("expand((x^s + y^s)^4)", //
				"x^(4*s)+4*x^(3*s)*y^s+6*x^(2*s)*y^(2*s)+4*x^s*y^(3*s)+y^(4*s)");

		check("Expand((1 + x)*(2 + x)*(3 + x))", //
				"6+11*x+6*x^2+x^3");
		check("Distribute((1 + x)*(2 + x)*(3 + x))", //
				"6+11*x+6*x^2+x^3");

		check("expand(2*(x + y)^2*Sin(x))", //
				"2*x^2*Sin(x)+4*x*y*Sin(x)+2*y^2*Sin(x)");
		check("expand(4*(a+b)*(c+d)*(f+g)^(-2))", //
				"(4*a*c)/(f+g)^2+(4*b*c)/(f+g)^2+(4*a*d)/(f+g)^2+(4*b*d)/(f+g)^2");
	}

	public void testExpandAll() {
		// IExpr[] temp=
		// Apart.getFractionalPartsTimes(F.Times(F.Plus(F.c,F.b),F.Power(F.a,F.CN1),F.b),
		// true);
		// issue#122
		// check("ExpandAll(( ( ( X3 - X1$c) * ( ( X1 + ( ( X4$c * X3 ) + X5$c))
		// + X3$b)) * ( ( X3 - X1 ) + ( X3$c + X5 ))))",
		// "");
		check("ExpandAll(Sum(9*x,{x,x,2*x}))", //
				"27/2*x+27/2*x^2");
		// github #113 - endless recursion
		check("ExpandAll(Sum(9*x,{x,x,x}))", //
				"9*x");
		// github #111 - loss of precision if you expand the expression
		check("t=ExpandAll((Pi*E-9)^13)", //
				"-2541865828329+3671583974253*E*Pi-2447722649502*E^2*Pi^2+997220338686*E^3*Pi^3\n"
						+ "-277005649635*E^4*Pi^4+55401129927*E^5*Pi^5-8207574804*E^6*Pi^6+911952756*E^7*Pi^\n"
						+ "7-75996063*E^8*Pi^8+4691115*E^9*Pi^9-208494*E^10*Pi^10+6318*E^11*Pi^11-117*E^12*Pi^\n"
						+ "12+E^13*Pi^13");
		check("N(t)", //
				"0.5");
		// shorten the result because of failing bitbucket pipeline
		check("N(t, 30)", //
				"-4.1601874<<SHORT>>", 10);

		check("N((Pi*E-9)^13)", //
				"-0.0000416019");
		check("ExpandAll(( ( ( X3 - X1_c) * ( ( X1 + ( ( X4_c * X3 ) + X5_c)) + X3_b)) * ( ( X3 - X1 ) + ( X3_c + X5 ))))", //
				"-x1^2*x3+x1*x3^2+x1*x3*x5+x1^2*x1_c-x1*x3*x1_c-x1*x5*x1_c-x1*x3*x3_b+x3^2*x3_b+x3*x5*x3_b+x1*x1_c*x3_b-x3*x1_c*x3_b-x5*x1_c*x3_b+x1*x3*x3_c-x1*x1_c*x3_c+x3*x3_b*x3_c-x1_c*x3_b*x3_c-x1*x3^\n"
						+ "2*x4_c+x3^3*x4_c+x3^2*x5*x4_c+x1*x3*x1_c*x4_c-x3^2*x1_c*x4_c-x3*x5*x1_c*x4_c+x3^\n"
						+ "2*x3_c*x4_c-x3*x1_c*x3_c*x4_c-x1*x3*x5_c+x3^2*x5_c+x3*x5*x5_c+x1*x1_c*x5_c-x3*x1_c*x5_c-x5*x1_c*x5_c+x3*x3_c*x5_c-x1_c*x3_c*x5_c");

		check("ExpandAll(1/(1 + x)^3 + Sin((1 + x)^3))", //
				"1/(1+3*x+3*x^2+x^3)+Sin(1+3*x+3*x^2+x^3)");
		check("Expand(1/(1 + x)^3 + Sin((1 + x)^3))", //
				"1/(1+x)^3+Sin((1+x)^3)");

		check("ExpandAll(2*x*(x^2-x+1)^(-1))", //
				"(2*x)/(1-x+x^2)");
		check("ExpandAll((2+x)*(x^2-x+1)^(-1))", //
				"2/(1-x+x^2)+x/(1-x+x^2)");
		check("ExpandAll(2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1))", //
				"(10*x^3)/(2+3*x^2)+(-8*x^4)/(2+3*x^2)+(4*x^6)/(2+3*x^2)");
		check("ExpandAll((b+c)*((b+c)*(a)^(-1)+1))", //
				"b+b^2/a+c+(2*b*c)/a+c^2/a");
		check("ExpandAll((-2*x^3+4*x-5)*((-2*x^3+4*x-5)*(a)^(-1)-2*x))", //
				"25/a+10*x+(-40*x)/a-8*x^2+(16*x^2)/a+(20*x^3)/a+4*x^4+(-16*x^4)/a+(4*x^6)/a");
		check("ExpandAll((-(-2*x^3+4*x-5)*(-(-2*x^3+4*x-5)*(3*x^2+2)^(-1)-2*x)*(3*x^2+2)^(-1)+x^2-2))", //
				"-2+x^2+(-10*x)/(2+3*x^2)+(8*x^2)/(2+3*x^2)+(-4*x^4)/(2+3*x^2)+25/(4+12*x^2+9*x^4)+(\n"
						+ "-40*x)/(4+12*x^2+9*x^4)+(16*x^2)/(4+12*x^2+9*x^4)+(20*x^3)/(4+12*x^2+9*x^4)+(-16*x^\n"
						+ "4)/(4+12*x^2+9*x^4)+(4*x^6)/(4+12*x^2+9*x^4)");
		check("ExpandAll(Sqrt((1 + x)^2))", //
				"Sqrt(1+2*x+x^2)");

		// TODO return a ^ 2 / (c ^ 2 + 2 c d + d ^ 2) + 2 a b / (c ^ 2 + 2 c d
		// + d ^ 2) + b ^ 2 / (c ^ 2 + 2 c d + d ^ 2)
		check("ExpandAll((a + b) ^ 2 / (c + d)^2)", //
				"a^2/(c^2+2*c*d+d^2)+(2*a*b)/(c^2+2*c*d+d^2)+b^2/(c^2+2*c*d+d^2)");
		check("ExpandAll((a + Sin(x*(1 + y)))^2)", //
				"a^2+2*a*Sin(x+x*y)+Sin(x+x*y)^2");
		check("ExpandAll(((1 + x)*(1 + y))[x])", //
				"(1+x+y+x*y)[x]");
	}

	public void testExponent() {
		check("Exponent(1 + x^(-2) + a*x^(-42), x, List)", //
				"{-42,-2,0}");
		check("Exponent(c*x^(-2)+a+b*x,x,-2)", //
				"-2[-2,0,1]");
		check("Exponent(7*y^w, y, List)", //
				"{w}");

		check("Exponent((2*x )^7*y+5*x^3,x^3)", //
				"7/3");
		check("Exponent(2*x *y+5*x^3,x^3)", //
				"1");
		check("Exponent(2*x*y+5*x^3,2*x)", //
				"1");
		check("Exponent(Cos(x*y), Cos(x*y))", //
				"1");
		check("Exponent(x^3,x^2)", //
				"3/2");
		check("Exponent(x^a,x^2)", //
				"a/2");
		check("Exponent(x^a,x^(2/3))", //
				"3/2*a");
		check("Exponent(2*x^a,x^(2/3))", //
				"3/2*a");
		check("Exponent(2*x^a,f(x))", //
				"0");

		check("Exponent((1+2*x)/Sqrt(3),x,List)", //
				"{0,1}");
		check("Exponent(Together((1+2*x)/Sqrt(3)),x,List)", //
				"{0,1}");
		check("Exponent((1+2*x)/Sqrt(3),x,List)", //
				"{0,1}");
		check("Exponent(a+b*x,x,List)", //
				"{0,1}");
		check("Exponent(SeriesData(x, 0, {1, 1, 0, 1, 1, 0, 1, 1}, 0, 9, 1), x)", //
				"7");
		check("Exponent(x*y,y,List)", //
				"{1}");
		check("Exponent(Sin(x*y),y)", //
				"0");

		check("Exponent(f(x^2),x)", //
				"0");
		check("Exponent(f(x^2),x,List)", //
				"{0}");
		check("Exponent(x*(b+a),x)", //
				"1");
		check("Exponent(x*(b+a),{a,b,x})", //
				"{1,1,1}");
		check("Exponent(x*(b+a),x,List)", //
				"{1}");
		check("Exponent(0, x)", //
				"-Infinity");
		check("Exponent(2, x)", //
				"0");
		check("Exponent(2*x, x)", //
				"1");
		check("Exponent(x, x)", //
				"1");
		check("Exponent(x^3, x)", //
				"3");
		check("Exponent(a*x^(-1), x)", //
				"-1");
		check("Exponent(x^(-3), x)", //
				"-3");
		check("Exponent(x^(-3)+x^(-2), x)", //
				"-2");
		check("Exponent(x+42, x)", //
				"1");
		check("Exponent(1 + x^2 + a*x^3, x)", //
				"3");
		check("Exponent((x^2 + 1)^3 + 1, x)", //
				"6");
		check("Exponent(x^(n0 + 1) + 2*Sqrt(x) + 1, x)", //
				"Max(1/2,1+n0)");
		check("Exponent((x^2 + 1)^3 - 1, x, Min)", //
				"2");
		check("Exponent((x^2 + 1)^3 + 1, x)", //
				"6");
		check("Exponent(1 + x^2 + a*x^3, x, List)", //
				"{0,2,3}");
		check("Exponent((a+b)/c, c)", //
				"-1");
		check("Exponent(a/c+b/c, c)", //
				"-1");
	}

	public void testExpToTrig() {
		check("TrigToExp(Sin(x))", //
				"(I*1/2)/E^(I*x)-I*1/2*E^(I*x)");
		check("ExpToTrig((I*1/2)/E^(I*x))", //
				"I*1/2*Cos(x)+Sin(x)/2");
		check("ExpToTrig(TrigToExp(Sin(x)))", //
				"Sin(x)");

		check("ExpToTrig((-1)^(1/3))", //
				"1/2+I*1/2*Sqrt(3)");
		check("ExpToTrig(Sqrt(I))", //
				"(1+I)/Sqrt(2)");

		check("Log(-17)", //
				"I*Pi+Log(17)");
		check("Cosh(a+b) // TrigExpand", //
				"Cosh(a)*Cosh(b)+Sinh(a)*Sinh(b)");

		check("ExpToTrig((-17)^a)", //
				"(Cos(a*Pi)+I*Sin(a*Pi))*(Cosh(a*Log(17))+Sinh(a*Log(17)))");
		check("ExpToTrig((-17/19)^a)", //
				"(Cos(a*Pi)+I*Sin(a*Pi))*(Cosh(a*Log(19/17))-Sinh(a*Log(19/17)))");
		check("ExpToTrig(I^a)", //
				"Cos(1/2*a*Pi)+I*Sin(1/2*a*Pi)");
		check("ExpToTrig(Exp(x) - Exp(-x))", //
				"2*Sinh(x)");
		check("ExpToTrig(Exp(x) - Exp(-x))", //
				"2*Sinh(x)");

		check("ExpToTrig(Exp(I*x) == -1)", //
				"Cos(x)+I*Sin(x)==-1");
		check("ExpToTrig(Exp(x)-Exp(-x))", //
				"2*Sinh(x)");
		check("ExpToTrig(E^(I*x))", //
				"Cos(x)+I*Sin(x)");
		check("ExpToTrig(E^(c*x))", //
				"Cosh(c*x)+Sinh(c*x)");
	}

	public void testExtendedGCD() {
		check("ExtendedGCD(2,3)", //
				"{1,{-1,1}}");
		check("ExtendedGCD(6,15,30)", //
				"{3,{-2,1,0}}");
		check("ExtendedGCD(3,{5,15})", //
				"{{1,{2,-1}},{3,{1,0}}}");
		check("ExtendedGCD(6,21)", //
				"{3,{-3,1}}");
		check("GCD(6,21)", //
				"3");

		check("ExtendedGCD(10, 15)", //
				"{5,{-1,1}}");
		check("ExtendedGCD(10, 15, 7)", //
				"{1,{-3,3,-2}}");

		check("$numbers = {10, 20, 14};", //
				"");
		check("{$gcd, $factors} = ExtendedGCD(Sequence @@ $numbers)", //
				"{2,{3,0,-2}}");
		check("Plus @@ ($numbers * $factors)", //
				"2");
	}

	public void testExpIntegralE() {
		check("ExpIntegralE(0,z)", //
				"1/(E^z*z)");
		check("ExpIntegralE(n,0)", //
				"ExpIntegralE(n,0)");
		check("ExpIntegralE(17,0)", //
				"1/16");
		check("ExpIntegralE(-17,0)", //
				"ComplexInfinity");
	}

	public void testExpIntegralEi() {
		check("ExpIntegralEi(I*Infinity)", //
				"I*Pi");
		check("ExpIntegralEi(-I*Infinity)", //
				"-I*Pi");
		check("Table(ExpIntegralEi(x), {x, 0.0, 3.0, 0.25})", //
				"{-Infinity,-0.542543,0.45422,1.20733,1.89512,2.58105,3.30129,4.08365,4.95423,5.94057,7.07377,8.3903,9.93383}");
		check("ExpIntegralEi(1.8)", //
				"4.24987");
	}

	public void testExtract() {
		check("Extract(a+b+c,2)", //
				"b");
		check("Extract(a+b+c,0)", //
				"Plus");
		check("Extract(a+b+c,-4)", //
				"Extract(a+b+c,-4)");
		check("Extract(a + b + c, {2})", //
				"b");
		check("Extract(a+b+c,{{2},{3}})", //
				"{b,c}");
		check("Extract({{a, b}, {c, d}}, {{1}, {2, 2}})", //
				"{{a,b},d}");
	}

	public void testFactor() {
		check("Factor(1+x^2, Extension->I)", //
				"(-I+x)*(I+x)");

		check("Factor(x^(-6)+1)", //
				"((1+x^2)*(1-x^2+x^4))/x^6");
		System.out.println();
		System.out.print('.');
		check("Factor(x+2*Sqrt(x)+1)", //
				"(1+Sqrt(x))^2");
		// check("Factor(E^x+E^(2*x))", //
		// "E^x*(1+E^x)");
		System.out.print('.');
		check("Factor(r^2+k*q*Q*r^4-E*r^6)", //
				"-r^2*(-1-k*q*Q*r^2+E*r^4)");
		System.out.print('.');
		check("Factor(x+2*Sqrt(x)+1)", //
				"(1+Sqrt(x))^2");
		System.out.print('.');
		check("Factor((a*d*e+(c*d^2+a*e^2)*x+c*d*e*x^2)^(3/2))", //
				"((a*e+c*d*x)*(d+e*x))^(3/2)");
		System.out.print('.');
		check("Factor(Cos(x)-I*Sin(x) )", //
				"Cos(x)-I*Sin(x)");
		System.out.print('.');
		check("Factor((Cos(x)-I*Sin(x))/(I*Cos(x)-Sin(x)))", //
				"(Cos(x)-I*Sin(x))/(I*Cos(x)-Sin(x))");

		// example from paper
		// https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
		System.out.print('.');
		check("Factor(4^(2*x+1)*5^(x-2)-6^(1-x))", //
				"(2^(1-x)*(-75+2^(1+5*x)*3^x*5^x))/(25*3^x)");
		System.out.print('.');
		check("Factor(E^x+E^(2*x))", //
				"E^x*(1+E^x)");

		// example from paper
		// https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
		System.out.print('.');
		check("Factor(Log(2,x)+4*Log(x,2)-5)", //
				"((-4*Log(2)+Log(x))*(-Log(2)+Log(x)))/(Log(2)*Log(x))");
		// TODO reduce negative signs
		// ((Log(2) - Log(x))*(4*Log(2) - Log(x)))/(Log(2)*Log(x))
		System.out.print('.');
		check("Factor( (4*a^2-5*a*b+b^2)/(a*b) )", //
				"((-4*a+b)*(-a+b))/(a*b)");

		// example from paper
		System.out.print('.');
		check("Factor(3*Tan(3*x)-Tan(x)+2)", //
				"2-Tan(x)+3*Tan(3*x)");
		System.out.print('.');
		check("TrigToExp(3*Tan(3*x)-Tan(x)+2)", //
				"2+(-I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))+(I*3*(E^(-I*3*x)-E^(I*3*x)))/(E^(-\n"
						+ "I*3*x)+E^(I*3*x))");
		System.out.print('.');
		check("Factor(2+(-I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))+(I*3*(E^(-I*3*x)-E^(I*3*x)))/(E^(-I*3*x)+E^(I*3*x)))", //
				"((2-I*2)*(I+(-1/2-I*1/2)*E^(I*2*x)+E^(I*4*x)))/((-1-I*E^(I*x)+E^(I*2*x))*(-1+I*E^(I*x)+E^(\n"
						+ "I*2*x)))");

		// example from paper
		System.out.print('.');
		check("Factor(3*Sech(x)^2+4*Tanh(x)+1)", //
				"1+3*Sech(x)^2+4*Tanh(x)");
		System.out.print('.');
		check("TrigToExp(3*Sech(x)^2+4*Tanh(x)+1)", //
				"1+12/(E^(-x)+E^x)^2+4*(-1/(E^x*(E^(-x)+E^x))+E^x/(E^(-x)+E^x))");

		// example from paper
		System.out.print('.');
		check("Factor(Log(x+1)+Log(x-1)-3)", //
				"-3+Log(-1+x)+Log(1+x)");
		System.out.print('.');
		check("TrigToExp(Log(x+1)+Log(x-1)-3)", //
				"-3+Log(-1+x)+Log(1+x)");
		check("-3+Log(1+x)+Log(-1+x)", //
				"-3+Log(-1+x)+Log(1+x)");

		// example from paper
		System.out.print('.');
		check("Factor(E^(3*x)-4*E^x+3*E^(-x))", //
				"((-1+E^x)*(1+E^x)*(-3+E^(2*x)))/E^x");

		// example from paper
		System.out.print('.');
		check("Factor(Cosh(x)-3*Sinh(y))", //
				"Cosh(x)-3*Sinh(y)");

		// 1/(E^x*2) + E^x/2 + 3/(E^y*2) - (3*E^y)/2
		System.out.print('.');
		check("TrigToExp(Cosh(x)-3*Sinh(y))", //
				"1/(2*E^x)+E^x/2-3*(-1/(2*E^y)+E^y/2)");
		System.out.print('.');
		check("TrigToExp(Cosh(x))", //
				"1/(2*E^x)+E^x/2");
		System.out.print('.');
		check("TrigToExp(Sinh(x))", //
				"-1/(2*E^x)+E^x/2");

		// example from paper
		System.out.print('.');
		check("Factor(2*Sinh(x)+6*Cosh(y)-5)", //
				"-5+6*Cosh(y)+2*Sinh(x)");
		System.out.print('.');
		check("TrigToExp(2*Sinh(x)+6*Cosh(y)-5)", //
				"-5+2*(-1/(2*E^x)+E^x/2)+6*(1/(2*E^y)+E^y/2)");

		// example from paper
		System.out.print('.');
		check("TrigToExp(Cos(x) + Cos(3*x) + Cos(5*x))", //
				"1/(2*E^(I*5*x))+1/(2*E^(I*3*x))+1/(2*E^(I*x))+E^(I*x)/2+E^(I*3*x)/2+E^(I*5*x)/2");
		//
		// // TODO determine more factors
		System.out.print('.');
		check("Factor(1/(2*E^(I*5*x))+1/(2*E^(I*3*x))+1/(2*E^(I*x))+E^(I*x)/2+E^(I*3*x)/2+E^(I*5*x)/2)", //
				"((-I+E^(I*x))*(I+E^(I*x))*(1-E^(I*x)+E^(I*2*x))*(1+E^(I*x)+E^(I*2*x))*(-1-I*E^(I*x)+E^(\n"
						+ "I*2*x))*(-1+I*E^(I*x)+E^(I*2*x)))/(2*E^(I*5*x))");
		// ((1/2)*(1 + E^(2*I*x))*(1 - E^(I*x) + E^(2*I*x))*(1 + E^(I*x) + E^(2*I*x))*
		// (1 - E^(2*I*x) + E^(4*I*x)))/E^(5*I*x)
		System.out.print('.');
		check("Factor(TrigToExp(Cos(x) + Cos(3*x) + Cos(5*x)))", //
				"((-I+E^(I*x))*(I+E^(I*x))*(1-E^(I*x)+E^(I*2*x))*(1+E^(I*x)+E^(I*2*x))*(-1-I*E^(I*x)+E^(\n"
						+ "I*2*x))*(-1+I*E^(I*x)+E^(I*2*x)))/(2*E^(I*5*x))");

		System.out.print('.');
		check("Factor(a*Cosh(x) + I*b*Cosh(x) - I*a*Sinh(x) + b*Sinh(x))", //
				"(-I*a+b)*(I*Cosh(x)+Sinh(x))");
		System.out.print('.');
		check("Factor(a*b+(4+4*x+x^2)^2)", //
				"16+a*b+32*x+24*x^2+8*x^3+x^4");

		// github #121
		System.out.print('.');
		check("Factor(x^(12)-y^(12), GaussianIntegers->True)", //
				"-(-x+y)*(x+y)*(-I*x+y)*(I*x+y)*(x^2+x*y+y^2)*(x^2-x*y+y^2)*(-x^2-I*x*y+y^2)*(-x^\n" + "2+I*x*y+y^2)");
		System.out.print('.');
		check("Factor(x^(2)+y^(2), GaussianIntegers->True)", //
				"(-I*x+y)*(I*x+y)");
		System.out.print('.');
		check("Factor(Sin(x), GaussianIntegers->True)", //
				"Sin(x)");

		System.out.print('.');
		check("Factor(1+x^2, GaussianIntegers->True)", //
				"(-I+x)*(I+x)");
		System.out.print('.');
		check("Factor(1+x^2, Extension->I)", //
				"(-I+x)*(I+x)");
		check("Factor(x^(2)+y^(2), GaussianIntegers->False)", //
				"x^2+y^2");

		// Homogenization example from
		// https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf
		System.out.print('.');
		check("Factor(E^(3*x)-4*E^x+3*E^(-x))", //
				"((-1+E^x)*(1+E^x)*(-3+E^(2*x)))/E^x");
		System.out.print('.');
		check("Factor(E^x+E^(2*x))", //
				"E^x*(1+E^x)");
		System.out.print('.');
		check("Factor(Sin(x))", //
				"Sin(x)");

		// TODO https://github.com/kredel/java-algebra-system/issues/8
		System.out.print('.');
		check("Factor(a*c+(b*c+a*d)*x+b*d*x^2)", //
				"(a+b*x)*(c+d*x)");

		System.out.print('.');
		check("Factor(b*c*n-a*d*n)", //
				"(b*c-a*d)*n");
		System.out.print('.');
		check("Factor(a*b*(4+4*x+x^2)^2)", //
				"a*b*(2+x)^4");

		System.out.print('.');
		check("Factor(x^2 - y^2)", //
				"(x-y)*(x+y)");
		System.out.print('.');
		check("Factor(1 / (x^2+2*x+1) + 1 / (x^4+2*x^2+1))", //
				"(2+2*x+3*x^2+x^4)/((1+x)^2*(1+x^2)^2)");

		System.out.print('.');
		check("Factor({x+x^2})", //
				"{x*(1+x)}");
		System.out.print('.');
		check("Factor(x^259+1)", //
				"(1+x)*(1-x+x^2-x^3+x^4-x^5+x^6)*(1-x+x^2-x^3+x^4-x^5+x^6-x^7+x^8-x^9+x^10-x^11+x^\n"
						+ "12-x^13+x^14-x^15+x^16-x^17+x^18-x^19+x^20-x^21+x^22-x^23+x^24-x^25+x^26-x^27+x^\n"
						+ "28-x^29+x^30-x^31+x^32-x^33+x^34-x^35+x^36)*(1+x-x^7-x^8+x^14+x^15-x^21-x^22+x^\n"
						+ "28+x^29-x^35-x^36-x^37-x^38+x^42+x^43+x^44+x^45-x^49-x^50-x^51-x^52+x^56+x^57+x^\n"
						+ "58+x^59-x^63-x^64-x^65-x^66+x^70+x^71+x^72+x^73+x^74+x^75-x^77-x^78-x^79-x^80-x^\n"
						+ "81-x^82+x^84+x^85+x^86+x^87+x^88+x^89-x^91-x^92-x^93-x^94-x^95-x^96+x^98+x^99+x^\n"
						+ "100+x^101+x^102+x^103-x^105-x^106-x^107-x^108-x^109-x^110-x^111+x^113+x^114+x^\n"
						+ "115+x^116+x^117+x^118-x^120-x^121-x^122-x^123-x^124-x^125+x^127+x^128+x^129+x^\n"
						+ "130+x^131+x^132-x^134-x^135-x^136-x^137-x^138-x^139+x^141+x^142+x^143+x^144+x^\n"
						+ "145+x^146-x^150-x^151-x^152-x^153+x^157+x^158+x^159+x^160-x^164-x^165-x^166-x^\n"
						+ "167+x^171+x^172+x^173+x^174-x^178-x^179-x^180-x^181+x^187+x^188-x^194-x^195+x^\n"
						+ "201+x^202-x^208-x^209+x^215+x^216)");
		System.out.print('.');
		check("Factor(x^258-1)", //
				"(-1+x)*(1+x)*(1-x+x^2)*(1+x+x^2)*(1-x+x^2-x^3+x^4-x^5+x^6-x^7+x^8-x^9+x^10-x^11+x^\n"
						+ "12-x^13+x^14-x^15+x^16-x^17+x^18-x^19+x^20-x^21+x^22-x^23+x^24-x^25+x^26-x^27+x^\n"
						+ "28-x^29+x^30-x^31+x^32-x^33+x^34-x^35+x^36-x^37+x^38-x^39+x^40-x^41+x^42)*(1+x+x^\n"
						+ "2+x^3+x^4+x^5+x^6+x^7+x^8+x^9+x^10+x^11+x^12+x^13+x^14+x^15+x^16+x^17+x^18+x^19+x^\n"
						+ "20+x^21+x^22+x^23+x^24+x^25+x^26+x^27+x^28+x^29+x^30+x^31+x^32+x^33+x^34+x^35+x^\n"
						+ "36+x^37+x^38+x^39+x^40+x^41+x^42)*(1-x+x^3-x^4+x^6-x^7+x^9-x^10+x^12-x^13+x^15-x^\n"
						+ "16+x^18-x^19+x^21-x^22+x^24-x^25+x^27-x^28+x^30-x^31+x^33-x^34+x^36-x^37+x^39-x^\n"
						+ "40+x^42-x^44+x^45-x^47+x^48-x^50+x^51-x^53+x^54-x^56+x^57-x^59+x^60-x^62+x^63-x^\n"
						+ "65+x^66-x^68+x^69-x^71+x^72-x^74+x^75-x^77+x^78-x^80+x^81-x^83+x^84)*(1+x-x^3-x^\n"
						+ "4+x^6+x^7-x^9-x^10+x^12+x^13-x^15-x^16+x^18+x^19-x^21-x^22+x^24+x^25-x^27-x^28+x^\n"
						+ "30+x^31-x^33-x^34+x^36+x^37-x^39-x^40+x^42-x^44-x^45+x^47+x^48-x^50-x^51+x^53+x^\n"
						+ "54-x^56-x^57+x^59+x^60-x^62-x^63+x^65+x^66-x^68-x^69+x^71+x^72-x^74-x^75+x^77+x^\n"
						+ "78-x^80-x^81+x^83+x^84)");
		System.out.print('.');
		check("Factor(4*x^2+3, Extension->I)", //
				"4*(3/4+x^2)");
		System.out.print('.');
		check("Factor(3/4*x^2+9/16, Extension->I)", //
				"3/4*(3/4+x^2)");

		System.out.print('.');
		check("Factor(x^10 - 1, Modulus -> 2)", //
				"(1+x)^2*(1+x+x^2+x^3+x^4)^2");

		System.out.print('.');
		check("factor(-1+x^16)", //
				"(-1+x)*(1+x)*(1+x^2)*(1+x^4)*(1+x^8)");
		System.out.print('.');
		check("factor((-3)*x^3 +10*x^2-11*x+4)", //
				"(1-x)^2*(4-3*x)");
		System.out.print('.');
		check("factor(x^2-a^2)", //
				"(-a+x)*(a+x)");

		// is sometimes slow, if it calls
		// FactorAbstract#factorsSquarefreeKronecker()
		System.out.print('.');
		check("factor(2*x^3*y - 2*a^2*x*y - 3*a^2*x^2 + 3*a^4)", //
				"(-a+x)*(a+x)*(-3*a^2+2*x*y)");
		System.out.print('.');
		check("expand((x+a)*(-x+a)*(-2*x*y+3*a^2))", //
				"3*a^4-3*a^2*x^2-2*a^2*x*y+2*x^3*y");
	}

	public void testFactorial() {

		check("Factorial(Factorial(x))", //
				"(x!)!");
		check("Factorial(2.5)", //
				"3.32335");
		check("Factorial(Infinity)", //
				"Infinity");

		check("Factorial2(-1)", //
				"1");
		check("Factorial2(-2)", //
				"ComplexInfinity");
		check("Factorial2(-3)", //
				"-1");
		check("Factorial2(-4)", //
				"ComplexInfinity");
		check("Factorial2(-5)", //
				"1/3");
		check("Factorial2(-6)", //
				"ComplexInfinity");
		check("Factorial2(-7)", //
				"-1/15");
		check("Factorial2(10)", //
				"3840");
		check("Factorial2(Infinity)", //
				"Infinity");
		check("Factorial(-Infinity)", //
				"Indeterminate");
		check("Factorial2(-Infinity)", //
				"Indeterminate");
		check("3!", "6");
		check("3!!", "3");

		check("Factorial(-1/2)", //
				"Sqrt(Pi)");
		check("Factorial(1/2)", //
				"Sqrt(Pi)/2");
		check("Factorial(0)", //
				"1");
		check("Factorial(1)", //
				"1");
		check("Factorial(-1)", //
				"ComplexInfinity");
		check("Factorial(10)", //
				"3628800");
		check("Factorial(-10)", //
				"ComplexInfinity");
		check("Factorial(11)", //
				"39916800");
		check("Factorial(-11)", //
				"ComplexInfinity");
		check("Factorial(19)", //
				"121645100408832000");
		check("Factorial(20)", //
				"2432902008176640000");
		check("Factorial(21)", //
				"51090942171709440000");
		checkNumeric("10.5!", //
				"1.1899423083962249E7");
		check("!a! //FullForm", //
				"Not(Factorial(a))");
	}

	// public void testExpand() {
	// check("expand(2*(x + y)^2*Sin(x))",
	// "2*y^2*Sin(x)+4*x*y*Sin(x)+2*x^2*Sin(x)");
	// check("expand(4*(a+b)*(c+d)*(f+g)^(-2))",
	// "4*b*d*(g+f)^(-2)+4*a*d*(g+f)^(-2)+4*b*c*(g+f)^(-2)+4*a*c*(g+f)^(-2)");
	// check("expand((1 + x)^10)",
	// "x^10+10*x^9+45*x^8+120*x^7+210*x^6+252*x^5+210*x^4+120*x^3+45*x^2+10*x+1");
	// check("expand((x + y)/z)", "y*z^(-1)+x*z^(-1)");
	// check("expand((x^s + y^s)^4)",
	// "y^(4*s)+4*x^s*y^(3*s)+6*x^(2*s)*y^(2*s)+4*x^(3*s)*y^s+x^(4*s)");
	// check("expand((1 + x + y)*(2 - x)^3)",
	// "-x^3*y+6*x^2*y-12*x*y+8*y-x^4+5*x^3-6*x^2-4*x+8");
	// }

	public void testFactorInteger() {
		check("FactorInteger(21) // N", //
				"{{3.0,1.0},{7.0,1.0}}");
		check("FactorInteger(10, GaussianIntegers->True)", //
				"{{-1,1},{1+I,2},{1+I*2,1},{2+I,1}}");
		check("Power @@@ FactorInteger(10, GaussianIntegers->True)", //
				"{-1,I*2,1+I*2,2+I}");
		check("Times @@ (Power @@@ FactorInteger(10, GaussianIntegers->True))", //
				"10");
		// 147 seconds on notebook
		// check("FactorInteger(10^79+5923)", "");
		// 32 seconds on notebook
		// check("FactorInteger(10^71-1)", "");
		if (Config.EXPENSIVE_JUNIT_TESTS) {
			System.out.println();
			check("FactorInteger(1593332576170570774181606244493046197050984933692181475920784855223341)", //
					"{{17,1},{1210508704285703,1},{2568160569265616473,1},{\n"
							+ "30148619026320753545829271787156467,1}}");
			System.out.print('.');
			check("FactorInteger(101546450935661953908994991437690198927080333663460351836152986526126114727314353555755712261904130976988029406423152881932996637460315302992884162068350429 )", //
					"{{123456789012419,1},{123456789012421,1},{123456789012437,1},{123456789012439,1},{\n"
							+ "123456789012463,1},{123456789012521,1},{123456789012523,1},{123456789012533,1},{\n"
							+ "123456789012577,1},{123456789012629,1},{123456789012637,1}}");
			System.out.print('.');
			check("FactorInteger(8225267468394993133669189614204532935183709603155231863020477010700542265332938919716662623)", //
					"{{1234567891,1},{1234567907,1},{1234567913,1},{1234567927,1},{1234567949,1},{\n"
							+ "1234567967,1},{1234567981,1},{1234568021,1},{1234568029,1},{1234568047,1}}");
			System.out.print('.');
			// 3225275494496681 (52 bits) = 56791489 * 56791529
			check("FactorInteger(3225275494496681)", //
					"{{56791489,1},{56791529,1}}");
			System.out.print('.');
			// 322527333642009919 (59 bits) = 567914891 * 567914909
			check("FactorInteger(322527333642009919)", //
					"{{567914891,1},{567914909,1}}");
			System.out.print('.');
			// 3225273260887418687 (62 bits) = 567914891 * 5679148957
			check("FactorInteger(3225273260887418687)", //
					"{{567914891,1},{5679148957,1}}");
			System.out.print('.');
			// very slow test
			check("FactorInteger(1794577685365897117833870712928656282041295031283603412289229185967719140138841093599)", //
					"{{42181796536350966453737572957846241893933,1},{\n"
							+ "42543889372264778301966140913837516662044603,1}}");
			System.out.print('.');
			// 10^79+5923 - very slow test
			check("FactorInteger(10^79+5923)", //
					"{{1333322076518899001350381760807974795003,1},{\n"
							+ "7500063320115780212377802894180923803641,1}}");
			System.out.print('.');
			check("1333322076518899001350381760807974795003*7500063320115780212377802894180923803641==10^79+5923", //
					"True");
			System.out.print('.');
			check("FactorInteger(10^71-1)", //
					"{{3,2},{241573142393627673576957439049,1},{\n" + "45994811347886846310221728895223034301839,1}}");
			System.out.print('.');
			check("3^2*241573142393627673576957439049*45994811347886846310221728895223034301839==10^71-1", //
					"True");
			System.out.println();
			System.out.print('.');
			check("FactorInteger(10^59+213)", //
					"{{213916881789829278910570173437,1},{467471286806848547076331371449,1}}");
			System.out.print('.');
			check("213916881789829278910570173437*467471286806848547076331371449== 10^59+213", //
					"True");
			System.out.print('.');
			check("FactorInteger(966983290915691193309978723256242679920691599725908954700676674631843021151)", //
					"{{2166660942804222727904664493239497749,1},{\n" + "446301159453293757389122758418041256099,1}}");
			System.out.print('.');
			check("FactorInteger(672924717570659549138949381690007452648932205241)", //
					"{{324557421200651278898953,1},{2073361056053736024795697,1}}");
			System.out.print('.');
			check("FactorInteger(475055344870856723877355576259313975012575765717)", //
					"{{387850060601719154011751,1},{1224842775926979010778467,1}}");
			System.out.print('.');
			check("FactorInteger(8438503049348381100385800049534923490020044110031)", //
					"{{59,1},{41387,1},{40320271,1},{85708917607365601059185614891297817,1}}");
			System.out.print('.');
			check("FactorInteger(10^100+1)", //
					"{{73,1},{137,1},{401,1},{1201,1},{1601,1},{1676321,1},{5964848081,1},{\n"
							+ "129694419029057750551385771184564274499075700947656757821537291527196801,1}}");
			System.out.print('.');
			check("FactorInteger(308119573764812073923)", //
					"{{19,2},{367,1},{132491,1},{17553335119,1}}");
			System.out.print('.');
			check("19^2*367*132491*17553335119", //
					"308119573764812073923");
			System.out.print('.');
			check("FactorInteger(132296607982211351148)", //
					"{{2,2},{3,4},{331,1},{107251,1},{11502026267,1}}");
			System.out.println();
			System.out.print('.');
			check("2^2*3^4*331*107251*11502026267", //
					"132296607982211351148");
			System.out.print('.');
			check("FactorInteger(44343535354351600000003434353)", //
					"{{149,1},{329569479697,1},{903019357561501,1}}");
			System.out.println();
			System.out.println("End of FactorInteger  EXPENSIVE_JUNIT_TESTS");

		}

		check("FactorInteger(341550071728321)", //
				"{{10670053,1},{32010157,1}}");
		check("FactorInteger(20568266680457)", //
				"{{19,1},{101,5},{103,1}}");
		check("FactorInteger(1524157877488187891)", //
				"{{9091,1},{167655689966801,1}}");

		// Java "long" numbers
		check("FactorInteger(621887327)", //
				"{{853,1},{729059,1}}");
		check("FactorInteger(676762483)", //
				"{{877,1},{771679,1}}");

		check("FactorInteger(2947524803)", //
				"{{1433,1},{2056891,1}}");
		check("FactorInteger(5616540799)", //
				"{{1777,1},{3160687,1}}");
		check("FactorInteger(35936505149)", //
				"{{3299,1},{10893151,1}}");
		check("FactorInteger(145682871839)", //
				"{{5261,1},{27691099,1}}");
		check("FactorInteger(317756737253)", //
				"{{6823,1},{46571411,1}}");
		check("FactorInteger(3294635112749)", //
				"{{14879,1},{221428531,1}}");
		check("FactorInteger(13293477682249)", //
				"{{398077,1},{33394237,1}}");

		check("FactorInteger(24596491225651)", //
				"{{3311299,1},{7428049,1}}");
		check("FactorInteger(44579405690563)", //
				"{{930889,1},{47889067,1}}");
		check("FactorInteger(72795445155721)", //
				"{{83459,1},{872230019,1}}");
		check("FactorInteger(155209074377713)", //
				"{{361909,1},{428862157,1}}");
		check("FactorInteger(293851765137859)", //
				"{{11736397,1},{25037647,1}}");
		// 67915439339311L == 2061599 * 32943089
		check("FactorInteger(67915439339311)", //
				"{{2061599,1},{32943089,1}}");

		// 5640012124823L,
		check("FactorInteger(5640012124823)", //
				"{{23117,1},{243976819,1}}");
		// 7336014366011L,
		check("FactorInteger(7336014366011)", //
				"{{24781,1},{296033831,1}}");
		// 19699548984827L,
		check("FactorInteger(19699548984827)", //
				"{{1464751,1},{13449077,1}}");
		// 52199161732031L,
		check("FactorInteger(52199161732031)", //
				"{{2065879,1},{25267289,1}}");
		// 73891306919159L,
		check("FactorInteger(73891306919159)", //
				"{{767827,1},{96234317,1}}");
		// 112454098638991L,
		check("FactorInteger(112454098638991)", //
				"{{80309,1},{1400267699,1}}");

		// 32427229648727L,
		check("FactorInteger(32427229648727)", //
				"{{1833401,1},{17686927,1}}");
		// 87008511088033L,
		check("FactorInteger(87008511088033)", //
				"{{125299,1},{694407067,1}}");
		// 92295512906873L,
		check("FactorInteger(92295512906873)", //
				"{{170123,1},{542522251,1}}");
		// 338719143795073L,
		check("FactorInteger(112454098638991)", //
				"{{80309,1},{1400267699,1}}");
		// 346425669865991L,
		check("FactorInteger(346425669865991)", //
				"{{70163,1},{4937440957,1}}");
		// 1058244082458461L,
		check("FactorInteger(1058244082458461)", //
				"{{65677,1},{16112856593,1}}");
		// 1773019201473077L,
		check("FactorInteger(1773019201473077)", //
				"{{6539893,1},{271108289,1}}");
		// 6150742154616377L,
		check("FactorInteger(6150742154616377)", //
				"{{181039,1},{33974680343,1}}");

		// 44843649362329L,
		check("FactorInteger(44843649362329)", //
				"{{673879,1},{66545551,1}}");
		// 67954151927287L,
		check("FactorInteger(67954151927287)", //
				"{{4919639,1},{13812833,1}}");
		// 134170056884573L,
		check("FactorInteger(134170056884573)", //
				"{{446333,1},{300605281,1}}");
		// 198589283218993L,
		check("FactorInteger(198589283218993)", //
				"{{1151581,1},{172449253,1}}");
		// 737091621253457L,
		check("FactorInteger(737091621253457)", //
				"{{1772291,1},{415897627,1}}");
		// 1112268234497993L,
		check("FactorInteger(1112268234497993)", //
				"{{68053,1},{16344146981,1}}");
		// 2986396307326613L,
		check("FactorInteger(2986396307326613)", //
				"{{27054757,1},{110383409,1}}");

		// 26275638086419L,
		check("FactorInteger(26275638086419)", //
				"{{92893,1},{282859183,1}}");
		// 62246008190941L,
		check("FactorInteger(62246008190941)", //
				"{{2874499,1},{21654559,1}}");
		// 209195243701823L,
		check("FactorInteger(209195243701823)", //
				"{{248231,1},{842744233,1}}");
		// 290236682491211L,
		check("FactorInteger(290236682491211)", //
				"{{12819941,1},{22639471,1}}");
		// 485069046631849L,
		check("FactorInteger(485069046631849)", //
				"{{287537,1},{1686979577,1}}");
		// 1239671094365611L,
		check("FactorInteger(1239671094365611)", //
				"{{5148659,1},{240775529,1}}");
		// 2815471543494793L,
		check("FactorInteger(2815471543494793)", //
				"{{3865469,1},{728364797,1}}");
		// 5682546780292609L,
		check("FactorInteger(5682546780292609)", //
				"{{52196827,1},{108867667,1}}");

		// SIQS numbers
		check("factors=FactorInteger(11111111111111111111111111)", //
				"{{11,1},{53,1},{79,1},{859,1},{265371653,1},{1058313049,1}}");
		check("factors=FactorInteger(5679148659138759837165981543)", //
				"{{3,3},{466932157,1},{450469808245315337,1}}");
		check("factors=FactorInteger(11111111111111111111111111155555555555111111111111111)", //
				"{{67,1},{157,1},{1056289676880987842105819104055096069503860738769,1}}");

		check("factors=FactorInteger(2900608971182010301486951469292513060638582965350239259380273225053930627446289431038392125)", //
				"{{3,11},{5,3},{7,6},{11,2},{13,2},{17,2},{19,1},{37,1},{41,1},{53,1},{59,1},{61,\n"
						+ "1},{73,1},{113,1},{151,1},{227,2},{271,1},{337,1},{433,1},{457,1},{547,1},{953,1},{\n"
						+ "11113,1},{11117,1},{11119,1},{33343,1},{33347,1},{33349,1},{33353,1},{33359,1}}");
		check("Times @@ Power @@@ factors", //
				"2900608971182010301486951469292513060638582965350239259380273225053930627446289\\\n" + //
						"431038392125");

		check("FactorInteger(5679148659138759837165981543 )", //
				"{{3,3},{466932157,1},{450469808245315337,1}}");
		check("FactorInteger(2^128 + 1 )", //
				"{{59649589127497217,1},{5704689200685129054721,1}}");
		check("FactorInteger(600851475143)", //
				"{{71,1},{839,1},{1471,1},{6857,1}}");
		check("FactorInteger(2^32-1)", //
				"{{3,1},{5,1},{17,1},{257,1},{65537,1}}");
		check("FactorInteger(0)", //
				"{{0,1}}");
		check("FactorInteger(1)", //
				"{{1,1}}");
		check("FactorInteger(990)", //
				"{{2,1},{3,2},{5,1},{11,1}}");
		check("FactorInteger(-993)", //
				"{{-1,1},{3,1},{331,1}}");
		check("FactorInteger(2^32-1)", //
				"{{3,1},{5,1},{17,1},{257,1},{65537,1}}");

		check("FactorInteger(10+30*I,GaussianIntegers->True)", //
				"{{-1,1},{1+I,3},{1+I*2,1},{2+I,2}}");
		check("FactorInteger(11+14*I,GaussianIntegers->True)", //
				"{{11+I*14,1}}");
		check("FactorInteger(8+21*I,GaussianIntegers->True)", //
				"{{1+I*2,1},{10+I,1}}");

		check("FactorInteger(16,GaussianIntegers->True)", //
				"{{1+I,8}}");
		check("FactorInteger(8+21*I,GaussianIntegers->True)", //
				"{{1+I*2,1},{10+I,1}}");
		check("FactorInteger(361 - 1767*I ,GaussianIntegers->True)", //
				"{{-1,1},{1+I,1},{2+I,1},{4+I,1},{7+I*2,1},{19,1}}");
		check("FactorInteger(440-55*I,GaussianIntegers->True)", //
				"{{-1,1},{1+I*2,1},{2+I,2},{2+I*3,1},{11,1}}");
		check("FactorInteger(5,GaussianIntegers->True)", //
				"{{-I,1},{1+I*2,1},{2+I,1}}");
		check("FactorInteger(12,GaussianIntegers->True)", //
				"{{-1,1},{1+I,4},{3,1}}");
		check("FactorInteger(5+7*I,GaussianIntegers->True)", //
				"{{1+I,1},{6+I,1}}");

		check("factors = FactorInteger(2010)", //
				"{{2,1},{3,1},{5,1},{67,1}}");
		check("Times @@ Power @@@ factors", //
				"2010");

		check("FactorInteger(50!*8392894255239922239)", //
				"{{2,47},{3,23},{5,12},{7,9},{11,4},{13,3},{17,2},{19,2},{23,2},{29,1},{31,1},{37,\n"
						+ "1},{41,1},{43,1},{47,1},{457,1},{11717,1},{84053,1},{887987,1}}");

		check("FactorInteger(8392894255239922239)", //
				"{{3,1},{7,1},{457,1},{11717,1},{84053,1},{887987,1}}");
		check("FactorInteger(4)", //
				"{{2,2}}");
		check("FactorInteger(3/8)", //
				"{{2,-3},{3,1}}");
		// sort is important for rational numbers
		check("FactorInteger(2345354/2424245)", //
				"{{2,1},{5,-1},{11,1},{17,1},{311,-1},{1559,-1},{6271,1}}");

		check("FactorInteger(-1)", //
				"{{-1,1}}");
		check("FactorInteger(-100)", //
				"{{-1,1},{2,2},{5,2}}");
		check("FactorInteger(-5!)", //
				"{{-1,1},{2,3},{3,1},{5,1}}");
		check("FactorInteger(-4)", //
				"{{-1,1},{2,2}}");
		check("FactorInteger(0)", //
				"{{0,1}}");
		check("FactorInteger(2941189)", //
				"{{1709,1},{1721,1}}");
		check("FactorInteger(12007001)", //
				"{{3001,1},{4001,1}}");
		check("FactorInteger(16843009)", //
				"{{257,1},{65537,1}}");
		check("FactorInteger(-5!)", //
				"{{-1,1},{2,3},{3,1},{5,1}}");
		check("Table(FactorInteger(2^2^n + 1), {n, 6})", //
				"{{{5,1}},{{17,1}},{{257,1}},{{65537,1}},{{641,1},{6700417,1}},{{274177,1},{\n"
						+ "67280421310721,1}}}");
		check("FactorInteger(2010 / 2011)", //
				"{{2,1},{3,1},{5,1},{67,1},{2011,-1}}");
	}

	public void testFactorSquareFree() {
		check("p = Expand((x + 1)^2 (x + 2)^2 (x + 3)^3)", //
				"108+432*x+711*x^2+625*x^3+318*x^4+94*x^5+15*x^6+x^7");
		check("FactorSquareFree(p)", //
				"(3+x)^3*(2+3*x+x^2)^2");
	}

	public void testFactorSquareFreeList() {
		check("FactorSquareFreeList(x^5 - x^3 - x^2 + 1)", //
				"{{-1+x,2},{1+2*x+2*x^2+x^3,1}}");
		check("FactorSquareFreeList(x^8 + 11*x^7 + 43*x^6 + 59*x^5 - 35*x^4 - 151*x^3 - 63*x^2 + 81*x + 54)", //
				"{{2+x,1},{3+x,3},{-1+x^2,2}}");
		check("FactorSquareFreeList((-3)*x^3 +10*x^2-11*x+4)", //
				"{{-1,1},{-1+x,2},{-4+3*x,1}}");
	}

	public void testFactorTerms() {
		// check("Expand((3 + 2 x)^2*(x + 2 y)^2)",//
		// "9*x^2+12*x^3+4*x^4+36*x*y+48*x^2*y+16*x^3*y+36*y^2+48*x*y^2+16*x^2*y^2");
		// check("FactorTerms(9*x^2+12*x^3+4*x^4+36*x*y+48*x^2*y+16*x^3*y+36*y^2+48*x*y^2+16*x^2*y^2, y)",//
		// "");

		// TODO create simpler form
		// check("FactorTerms(2*a*x^2*y + 2*x^2*y + 4*a*x^2 + 4*x^2 + 4*a^2*y^2 + 4*a*y^2 + 8*a^2*y + 2*a*y - 6*y -
		// 12*a- 12, x)", //
		// "");

		check("FactorTerms(100*Log(2))", //
				"100*Log(2)");
		check("FactorTerms(-136+40*Sqrt(17))", //
				"8*(-17+5*Sqrt(17))");

		check("FactorTerms(x^2 - y^2, x)", //
				"x^2-y^2");
		check("factorterms(3 + 6*x + 3*x^2)", //
				"3*(1+2*x+x^2)");
	}

	public void testFibonacci() {
		check("Fibonacci(0.2114411444411100011, 5)", //
				"1.598551917369153725*10^-1");
		check("Fibonacci(5.8)", //
				"7.26639");
		check("Fibonacci(1+I/2)//N", //
				"1.3763+I*0.00716945");
		check("Fibonacci(-10, x)", //
				"-5*x-20*x^3-21*x^5-8*x^7-x^9");
		check("Fibonacci(11, x)", //
				"1+15*x^2+35*x^4+28*x^6+9*x^8+x^10");
		check("Fibonacci(50, x)", //
				"25*x+2600*x^3+80730*x^5+1184040*x^7+10015005*x^9+54627300*x^11+206253075*x^13+\n"
						+ "565722720*x^15+1166803110*x^17+1855967520*x^19+2319959400*x^21+2310789600*x^23+\n"
						+ "1852482996*x^25+1203322288*x^27+635745396*x^29+273438880*x^31+95548245*x^33+\n"
						+ "26978328*x^35+6096454*x^37+1086008*x^39+148995*x^41+15180*x^43+1081*x^45+48*x^47+x^\n"
						+ "49");
		check("Table(Fibonacci(n, x), {n, 5})", //
				"{1,x,1+x^2,2*x+x^3,1+3*x^2+x^4}");

		check("Table(Fibonacci(n), {n, 45})", //
				"{1,1,2,3,5,8,13,21,34,55,89,144,233,377,610,987,1597,2584,4181,6765,10946,17711,\n"
						+ "28657,46368,75025,121393,196418,317811,514229,832040,1346269,2178309,3524578,\n"
						+ "5702887,9227465,14930352,24157817,39088169,63245986,102334155,165580141,\n"
						+ "267914296,433494437,701408733,1134903170}");
		// check("Fibonacci(10000)", "0");
		check("Fibonacci(0)", //
				"0");
		check("Fibonacci(1)", //
				"1");
		check("Fibonacci(10)", //
				"55");
		check("Fibonacci(-51)", //
				"20365011074");
		check("Fibonacci(-52)", //
				"-32951280099");
		check("Fibonacci(200)", //
				"280571172992510140037611932413038677189525");
		check("Table(Fibonacci(-n), {n, 10})", //
				"{1,-1,2,-3,5,-8,13,-21,34,-55}");
		check("Fibonacci(1000)", //
				"4346655768693745643568852767504062580256466051737178040248172908953655541794905\\\n"
						+ "1890403879840079255169295922593080322634775209689623239873322471161642996440906\\\n"
						+ "533187938298969649928516003704476137795166849228875");

	}

	public void testFindFit() {
		// https://stackoverflow.com/a/51696587/24819
		check("FindFit({ {1.3,0.5}, {2.8,0.9}, {5.0,2.6}, {10.2,7.1}, {16.5,12.3}, {21.3,15.3},{ 31.8,20.4}, {52.2,24.4}}, " //
				+ "d+((a-d)/ (1+(x/c)^ b)),  {{a, 1}, {b,2}, {c,20}, {d,20}}, x)", //
				"{a->0.174321,b->1.75938,c->19.69032,d->28.83068}");
		// initial guess [1.0, 1.0, 1.0] gives bad result:
		check("FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1})," //
				+ " a* Sin(w*t + f), {a,w,f}, t)", //
				"{a->0.668797,w->1.49588,f->3.74845}");
		// initial guess [2.0, 1.0, 1.0] gives better result:
		check("FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), "//
				+ "a* Sin(w*t + f), {{a, 2}, {w,1}, {f,1}}, t)", //
				"{a->3.0,w->3.0,f->1.0}");
		// initial guess [2.0, 1.0, 1.0] with 1.0 by default:
		check("FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), "//
				+ "a* Sin(w*t + f), {{a, 2}, w, f}, t)", //
				"{a->3.0,w->3.0,f->1.0}");

		check("FindFit({{1,1},{2,4},{3,9},{4,16}}, " //
				+ "a+b*x+c*x^2, {a, b, c}, x)", //
				"{a->-6.10623*10^-16,b->7.21645*10^-16,c->1.0}");
		check("FindFit({{15.2,8.9},{31.1,9.9},{38.6,10.3},{52.2,10.7},{75.4,11.4}}, " //
				+ "a*Log(b*x), {a, b}, x)", //
				"{a->1.54503,b->20.28258}");
		check("FindFit({2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71}, " //
				+ "a*x*Log(b + c*x), {a, b, c}, x)", //
				"{a->1.42076,b->1.65558,c->0.534644}");
		check("FindFit({{1.0, 12.}, {1.9, 10.}, {2.6, 8.2}, {3.4, 6.9}, {5.0, 5.9}}, " //
				+ "a*Exp(-k*t), {a, k}, t)", //
				"{a->14.38886,k->0.198208}");

		// initial guess [0, 0, 0] doesn't work
		check("FindFit({2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71}, " //
				+ "a*x*Log(b + c*x), {{a,0},{b,0},{c,0}}, x)", //
				"FindFit({2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71},a*x*Log(b+c*x),{{a,\n"
						+ "0},{b,0},{c,0}},x)");
	}

	public void testFindInstance() {
		check("FindInstance(-1+4*Sin(x)==0,x)", //
				"{{x->ArcSin(1/4)}}");
		check("FindInstance(2*Sin(x)==1/2,x)", //
				"{{x->ArcSin(1/4)}}");
		check("FindInstance((a || b || c) && (! a || ! b || ! c) && True, {a, b, c}, 2)",
				"{{a->False,b->True,c->True},{a->False,b->True,c->False}}");

		check("FindInstance({x^2==4,x+y^2==6}, {x,y})", //
				"{{x->-2,y->-2*Sqrt(2)}}");
		check("FindInstance(x+5.0==a,x)", "{{x->-5.0+a}}");

		check("FindInstance(Xor(a, b, c, d) && (a || b) && ! (c || d), {a, b, c, d}, Booleans)",
				"{{a->False,b->True,c->False,d->False}}");

		check("FindInstance(Sin((-3+x^2)/x) ==2,{x})", //
				"{{x->1/2*(ArcSin(2)+Sqrt(12+ArcSin(2)^2))}}");
		// check("FindInstance(Abs((-3+x^2)/x) ==2,{x})", "{{x->-3}}");
		check("FindInstance({x^2-11==y, x+y==-9}, {x,y})", "{{x->-2,y->-7}}");

		check("FindInstance(3+2*Cos(x)==1/2,x)", //
				"{{x->ArcCos(-5/4)}}");
		check("FindInstance(Sin(x)==0,x)", "{{x->0}}");
		check("FindInstance(Sin(x)==0.0,x)", "{{x->0}}");
		check("FindInstance(Sin(x)==1/2,x)", "{{x->Pi/6}}");
		checkNumeric("FindInstance(sin(x)==0.5,x)", "{{x->0.5235987755982989}}");
		check("FindInstance(x^2-2500.00==0,x)", //
				"{{x->-50.0}}");
		check("FindInstance(x^2+a*x+1 == 0, x)", //
				"{{x->1/2*(-a-Sqrt(-4+a^2))}}");
		check("FindInstance((-3)*x^3 +10*x^2-11*x == (-4), {x})", //
				"{{x->1}}");

		checkNumeric("FindInstance(x^2+50*x-2500.00==0,x)", //
				"{{x->-80.90169943749474}}");

		check("FindInstance(a*x + y == 7 && b*x - y == 1, {x, y})", //
				"{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");
		check("FindInstance({a*x + y == 7, b*x - y == 1}, {x, y})", //
				"{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");

	}

	// https://github.com/Hipparchus-Math/hipparchus/issues/40
	// public void testBisection() {
	// BisectionSolver solver = new BisectionSolver();
	// System.out.println(solver.solve(100, x->Math.cos(x)+2, 0.0, 5.0));
	// }

	public void testFindRoot() {
		checkNumeric("FindRoot(30*x/0.000002==30, {x, 0, 5}, Method->brent)", //
				"{x->2.0E-6}");
		// github issue #60
		check("FindRoot(cos(x) + 2, {x, 0, 5}, Method->brent)", //
				"{}");
		check("FindRoot(cos(x) + 2, {x, 0, 5}, Method->bisection)", //
				"{}");
		check("FindRoot(cos(x) + 2, {x, 0, 5}, Method->newton)", //
				"{}");

		// github issue #43
		check("findroot(abs(x-1)-2x-3==0, {x, -10, 10})", //
				"{x->-0.666667}");

		// github issue #103
		check("FindRoot(2^x==0,{x,-100, 100}, Method->Brent)", //
				"{}");
		// FindRoot: interval does not bracket a root: f(-1) = 0.5, f(100) =
		// 1,267,650,600,228,229,400,000,000,000,000
		check("FindRoot(2^x==0,{x,-1,100}, Method->Brent)", //
				"{}");

		checkNumeric("N(2^(-100))", //
				"0.0");

		check("FindRoot(Exp(x)-1 == 0,{x,-50,100}, Method->Muller)", //
				"{x->0.0}");
		if (!Config.EXPLICIT_TIMES_OPERATOR) {
			// implicit times operator '*' allowed
			check("Exp(1.2436240901689538 * E - 16) - 1", //
					"-0.999997");
			check("Exp(1.2436240901689538E-16) - 1", //
					"-0.999997");
		} else {
			check("Exp(1.2436240901689538E-16) - 1", //
					"0.0");
		}

		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10})", //
				"{x->3.434189629596888}");
		checkNumeric("$K=10000;\n" + "$g=0.0;\n" + "$n=10*12;\n" + "$Z=12;\n" + "$AA=0.0526;\n" + "$R=100;\n"
				+ "$d=0.00;\n" + "$vn=0;\n" + "$EAj=0;\n" + "$zj=0;\n" + "$sz=1;\n"
				+ "FindRoot((($K*(1+p-$g)^($n/$Z))/(1+$AA))+(Sum((($R*(1+$d)^(Floor(i0/$Z)))/(1+$AA))*(1+p-$g)^(($n-i0-$vn)/$Z),{i0,0,$n-1}))+(Sum(($EAj*(1+p-$g)^(($n-$zj)/$Z))/(1+$AA),{j,1,$sz})) - 30199, {p, 0, 0.1})", //
				"{p->0.04999709394010556}");
		checkNumeric(
				"$K=10000;\n" + "$g=0.0;\n" + "$n=10*12;\n" + "$Z=12;\n" + "$AA=0.0526;\n" + "$res=15474;\n"
						+ "FindRoot((($K*(1+p-$g)^($n/$Z))/(1+$AA)) - $res, {p, 0, 0.1})", //
				"{p->0.04999346433486661}");

		checkNumeric("Exp(3.4341896)", //
				"31.006274895944433");
		checkNumeric("Pi^3.0", //
				"31.006276680299816");
		// default to Newton method
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10})", //
				"{x->3.434189629596888}");
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Method->Newton)", //
				"{x->3.4341896575482007}");

		// only a start value is given:
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,3}, Method->Newton)", "{x->3.4341896575482007}");

		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Method->Bisection)", //
				"{x->3.434189647436142}");
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Method->Brent)", //
				"{x->3.434189629596888}");
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Muller)", //
				"{x->3.4341896575483015}");
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Ridders)", //
				"{x->3.4341896575482007}");
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Secant)", //
				"{x->3.4341896575036097}");
		// FindRoot: maximal count (100) exceeded
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Method->RegulaFalsi, MaxIterations->100)", //
				"FindRoot(E^x==Pi^3,{x,1,10},Method->regulafalsi,MaxIterations->100)");
		// FindRoot: convergence failed
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Method->RegulaFalsi, MaxIterations->32000)", //
				"FindRoot(E^x==Pi^3,{x,1,10},Method->regulafalsi,MaxIterations->32000)");
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Illinois)", //
				"{x->3.4341896915055257}");
		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,1,10}, Pegasus)", //
				"{x->3.4341896575481976}");

		checkNumeric("FindRoot(Exp(x)==Pi^3,{x,-1,10}, Brent)", //
				"{x->3.434189629596888}");
		check("FindRoot(Sin(x),{x,-0.5,0.5}, Secant)", //
				"{x->0.0}");
	}

	public void testEulerianGraphQ() {
		check("EulerianGraphQ({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
				"True");
		check("EulerianGraphQ({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
				"False");
	}

	public void testFindEulerianCycle() {
		check("FindEulerianCycle(Graph({1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1}))", //
				"{4->1,1->2,2->3,3->4}");
		check("FindEulerianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
				"{4->1,1->3,3->1,1->2,2->3,3->4}");
		check("FindEulerianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
				"{}");
	}

	public void testFindHamiltonianCycle() {
		check("FindHamiltonianCycle( {1 -> 2, 2 -> 3, 3 -> 4, 4 -> 1} )", //
				"{1->2,2->3,3->4,4->1}");
		check("FindHamiltonianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
				"{1->2,2->3,3->4,4->1}");
		check("FindHamiltonianCycle({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
				"{}");
	}

	public void testFindVertexCover() {
		// example from wikipedia: https://en.wikipedia.org/wiki/Vertex_cover
		check("FindVertexCover({1<->2,1<->3,2<->3,3<->4,3<->5,3<->6})", //
				"{3,1}");

		check("FindVertexCover({UndirectedEdge(2,1), UndirectedEdge(1,3), UndirectedEdge(3,6), UndirectedEdge(6,1),"//
				+ " UndirectedEdge(4,6), UndirectedEdge(1,5), UndirectedEdge(5,4) })", //
				"{1,6,4}");
		check("FindVertexCover({UndirectedEdge(1,2), UndirectedEdge(2,3), UndirectedEdge(3,4), UndirectedEdge(3,6),"//
				+ " UndirectedEdge(3,7), UndirectedEdge(6,4), UndirectedEdge(4,7), UndirectedEdge(4,5), UndirectedEdge(5,1)})", //
				"{3,4,1}");

		// print: Graph must be undirected
		// TODO implement for directed graphs
		check("FindVertexCover({DirectedEdge(2,1), DirectedEdge(1,3), DirectedEdge(3,6), DirectedEdge(6,1),"//
				+ " DirectedEdge(4,6), DirectedEdge(1,5), DirectedEdge(5,4) })", //
				"FindVertexCover({2->1,1->3,3->6,6->1,4->6,1->5,5->4})");

	}

	public void testFindShortestPath() {
		check("FindShortestPath(Graph({1 -> 2, 2 -> 4, 1 -> 3,  3 -> 2, 3 -> 4},{EdgeWeight->{3.0,1.0,1.0,1.0,3.0}}),1,4)", //
				"{1,3,2,4}");

		check("FindShortestPath({1 -> 2, 2 -> 3, 3 -> 1,  3 -> 4, 4 -> 5, 3 -> 5},1,4)", //
				"{1,2,3,4}");
	}

	public void testFindShortestTour() {
		check("FindShortestTour({{1,2},{2,3},{3,1}})", //
				"{Sqrt(2)+2*Sqrt(5),{1,3,2,1}}");

		check("FindShortestTour({GeoPosition({41, 20}), GeoPosition({5, 20}), GeoPosition({49, 32}), " //
				+ "GeoPosition({53, 28}), GeoPosition({47, 29})})", //
				"{6852.02461316151[mi],{1,2,5,3,4,1}}");
		check("FindShortestTour({{1, 1}, {1, 2}, {1, 3}, {1, 4}, {1, 5}, {2, 1}, {2, 3}, {2, 5}, {3, 1}, {3, 2},"//
				+ " {3, 4}, {3, 5}, {4, 1}, {4, 3}, {4, 5}, {5, 1}, {5, 2}, {5, 3}, {5, 4}})", //
				"{14+5*Sqrt(2),{1,6,9,13,16,17,18,19,14,10,7,11,15,12,8,5,4,3,2,1}}");
	}

	public void testFindSpanningTree() {
		// example from Wikipedia https://en.wikipedia.org/wiki/Minimum_spanning_tree
		check("FindSpanningTree(Graph({a,b,c,d,e,f},{a<->b,a<->d,b<->c,b<->d,b<->e,c<->e,c<->f,d<->e,e<->f}," //
				+ "{EdgeWeight->{1.0,3.0,6.0,5.0,1.0,5.0,2.0,1.0,4.0}}))", //
				"Graph({a,b,d,e,c,f},{a->b,d->e,c->f,b->e,e->f},{EdgeWeight->{1.0,1.0,2.0,1.0,4.0}})");

		check("g=Graph({1,2,3,4,5,6,7,8},\n"
				+ "{UndirectedEdge(1,2),UndirectedEdge(1,3),UndirectedEdge(1,4),UndirectedEdge(3,4),UndirectedEdge(2,6),\n"
				+ "UndirectedEdge(3,6),UndirectedEdge(5,3),UndirectedEdge(5,4),UndirectedEdge(5,6),UndirectedEdge(5,7),\n"
				+ "UndirectedEdge(5,8),UndirectedEdge(6,7),UndirectedEdge(7,8),UndirectedEdge(4,8)});", //
				"");

		check("FindSpanningTree(g)", //
				"Graph({1,2,3,4,6,5,7,8},{1->2,1->3,1->4,2->6,5->3,5->7,5->8})");
	}

	public void testHamiltonianGraphQ() {
		check("HamiltonianGraphQ(Graph({1,2,3},{1<->2,2<->3,3<->1}))", //
				"True");
		check("HamiltonianGraphQ(Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{2.0,3.0,4.0}}))", //
				"True");

		check("HamiltonianGraphQ({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1 })", //
				"True");
		check("HamiltonianGraphQ({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1, 4 -> 7})", //
				"False");
	}

	public void testHankelH1() {
		checkNumeric("HankelH1(3,1.2)", //
				"0.03287433692499494+I*(-3.5898996300536705)");
		checkNumeric("HankelH1(2.0,3)", //
				"0.4860912605858912+I*(-0.1604003934801459)");
		checkNumeric("HankelH1(4.0,Pi)", //
				"0.15142457763134973+I*(-0.8284254758327632)");
		checkNumeric("HankelH1(23/47,5.0-I)", //
				"-0.8840126453738697+I*(-0.37582703842509163)");
	}

	public void testHankelH2() {
		checkNumeric("HankelH2(3,1.2)", //
				"0.03287433692499494+I*3.5898996300536705");
		checkNumeric("HankelH2(0.2,3)", //
				"-0.13378696772476814+I*(-0.4383481561882018)");
		checkNumeric("HankelH2(4.0,3*Pi)", //
				"-0.2721262441569784+I*0.01563182604674291");
		checkNumeric("HankelH2(23/47,5.0-I)", //
				"-0.1272388528903579+I*0.026410835310926417");
	}

	public void testGraph() {
		check("Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{2.0,3.0,4.0}})", //
				"Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{2.0,3.0,4.0}})");
		check("Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}, {EdgeWeight -> {2, 3, 4}})", //
				"Graph({1,2,3},{1<->2,2<->3,3<->1},{EdgeWeight->{2.0,3.0,4.0}})");

		check("Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1})", //
				"Graph({1,2,3},{1<->2,2<->3,3<->1})");
		check("Graph({1,2,3},{1<->2,2<->3,3<->1})", //
				"Graph({1,2,3},{1<->2,2<->3,3<->1})");

		check("Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1})", //
				"Graph({1,2,3},{1->2,2->3,3->1})");
		check("Graph({1 -> 2, 2 -> 3, 3 -> 1, 1 -> 3, 3 -> 4, 4 -> 1})", //
				"Graph({1,2,3,4},{1->2,2->3,3->1,1->3,3->4,4->1})");

		check("Graph({1,2,3,4,5,6,7,8},\n"
				+ "{UndirectedEdge(1,2),UndirectedEdge(1,3),UndirectedEdge(1,4),UndirectedEdge(3,4),UndirectedEdge(2,6),\n"
				+ "UndirectedEdge(3,6),UndirectedEdge(5,3),UndirectedEdge(5,4),UndirectedEdge(5,6),UndirectedEdge(5,7),\n"
				+ "UndirectedEdge(5,8),UndirectedEdge(6,7),UndirectedEdge(7,8),UndirectedEdge(4,8)})", //
				"Graph({1,2,3,4,5,6,7,8},{1<->2,1<->3,1<->4,3<->4,2<->6,3<->6,5<->3,5<->4,5<->6,5<->7,5<->8,6<->7,7<->8,4<->8})");

	}

	public void testGraphData() {
		check("GraphData(\"PappusGraph\")", //
				"Graph({1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18}," //
						+ "{1<->2,1<->3,1<->4,2<->5,2<->6,5<->7,5<->8,7<->9,7<->\n" + //
						"10,9<->3,9<->11,3<->12,4<->13,4<->14,6<->15,6<->16,8<->13,8<->17,10<->16,10<->18,11<->17,11<->14,12<->15,12<->18,15<->17,13<->18,16<->\n"
						+ "14})");
		check("GraphData(\"PetersenGraph\")", //
				"Graph({1,2,3,4,5,6,7,8,9,10},{1<->3,1<->2,2<->6,3<->5,3<->4,4<->8,5<->7,5<->6,6<->10,7<->9,7<->8,8<->2,9<->1,9<->10,10<->4})");
	}

	public void testGraphCenter() {
		check("GraphCenter(Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)}))", //
				"{3}");

		check("GraphCenter(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
				+ //
				"{EdgeWeight->{1.6,2.0,1.4,1.9,0.62}}))", //
				"{1,3}");
		check("GraphCenter({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)})", //
				"{1,3}");
	}

	public void testGraphDiameter() {
		check("GraphDiameter(Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)}))", //
				"4");

		check("GraphDiameter(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
				+ //
				"{EdgeWeight->{1.6,2.0,1.4,1.9,0.62}}))", //
				"2.52");
		check("GraphDiameter({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)})", //
				"2");
	}

	public void testGraphRadius() {
		check("GraphRadius(Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)}))", //
				"2");

		check("GraphRadius(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
				+ //
				"{EdgeWeight->{1.6,2.0,1.4,1.9,0.62}}))", //
				"2.0");
		check("GraphRadius({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)})", //
				"1");
	}

	public void testGraphPeriphery() {
		check("GraphPeriphery(Graph({DirectedEdge(1, 2), DirectedEdge(2, 3), DirectedEdge(3, 1),  DirectedEdge(3, 4), DirectedEdge(4, 5), DirectedEdge(5, 3)}))", //
				"{1,4}");

		check("GraphPeriphery(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
				+ //
				"{EdgeWeight->{1.6,2.0,1.4,1.9,0.62}}))", //
				"{2,4}");
		check("GraphPeriphery({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)})", //
				"{2,4}");

		check("GraphPeriphery({UndirectedEdge(1, 2), UndirectedEdge(2, 3), UndirectedEdge(3, 1), UndirectedEdge(3, 4), UndirectedEdge(3, 4), UndirectedEdge(4, 5), UndirectedEdge(5, 3)})", //
				"{1,2,4,5}");
	}

	public void testGraphQ() {
		check("GraphQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}) )", //
				"True");
		check("GraphQ( Sin(x) )", //
				"False");
	}

	public void testFirst() {
		check("First(Infinity)", //
				"1");
		check("First(ComplexInfinity)", //
				"First(ComplexInfinity)");
		check("First({a, b, c})", //
				"a");
		check("First(a + b + c)", //
				"a");
		check("First(a)", //
				"First(a)");
		check("First(a, b)", //
				"b");
		check("First({}, b)", //
				"b");
		check("First({a,b}, x)", //
				"a");
	}

	public void testFit() {
		check("Fit({{0, 1}, {1, 0}, {3, 2}, {5, 4}}, 1, x)", //
				"0.186441+0.694915*x");
		check("Fit({{1,1},{2,4},{3,9},{4,16}},2,x)", //
				"x^2");
	}

	public void testFiveNum() {
		// example from https://en.wikipedia.org/wiki/Five-number_summary
		check("FiveNum({0, 0, 1, 2, 63, 61, 27, 13})", //
				"{0,1/2,15/2,44,63}");
		check("FiveNum({20,12,16,32,27,65,44,45,22,18})", //
				"{12,18,49/2,44,65}");
	}

	public void testFixedPoint() {
		check("FixedPoint(Cos, 1.0)", //
				"0.739085");
		check("FixedPoint(#+1 &, 1, 20)", //
				"21");
		check("FixedPoint(f, x, 0)", //
				"x");
		check("FixedPoint(f, x, -1)", //
				"FixedPoint(f,x,-1)");
		checkNumeric("FixedPoint(Cos, 1.0, Infinity)", //
				"0.7390851332151607");

		checkNumeric("FixedPoint((# + 2/#)/2 &, 1.)", //
				"1.414213562373095");
		check("FixedPoint(1 + Floor(#/2) &, 1000)", //
				"2");
		check("21!=0", //
				"True");
		check("{28, 21} /. {a_, b_}  -> {b, Mod(a, b)}", //
				"{21,7}");
		check("{28, 21} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}", //
				"{21,7}");
		check("{21, 7} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}", //
				"{7,0}");
		check("{7, 0} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}", //
				"{7,0}");
		check("FixedPoint(# /. {a_, b_} /; b != 0 -> {b, Mod(a, b)} &, {28, 21})", //
				"{7,0}");
	}

	public void testFixedPointList() {
		check("FixedPointList(Cos, 1.0, 4)", //
				"{1.0,0.540302,0.857553,0.65429,0.79348}");
		checkNumeric("newton(n_) := FixedPointList(.5*(# + n/#) &, 1.);  newton(9)", //
				"{1.0,5.0,3.4,3.023529411764706,3.00009155413138,3.000000001396984,3.0,3.0}");

		// Get the "hailstone" sequence of a number:
		check("collatz(1) := 1", //
				"");
		check("collatz(x_ ? EvenQ) := x / 2", //
				"");
		check("collatz(x_) := 3*x + 1", //
				"");
		check("FixedPointList(collatz, 14)", //
				"{14,7,22,11,34,17,52,26,13,40,20,10,5,16,8,4,2,1,1}");

		check("FixedPointList(f, x, 0)", //
				"{x}");
		check("FixedPointList(f, x, -1) ", //
				"FixedPointList(f,x,-1)");
		check("Last(FixedPointList(Cos, 1.0, Infinity))", //
				"0.739085");
	}

	public void testFlat() {
		// test https://github.com/mathics/Mathics/issues/747
		// Verified against Mathematica 11.3.0.0 by rebcabin [2018-10-07 Sun 06:31]
		check("SetAttributes(eqv, Flat);eqv(p, q, q, p) /. eqv(x_, y_) :> {x, y}", //
				"{eqv(p),eqv(q,q,p)}");

		check("SetAttributes(f, Flat)", //
				"");

		check("f(a, b, c) /. f(a, b) -> d", //
				"f(d,c)");
		check("f(a, f(b, c))", //
				"f(a,b,c)");
		check("SetAttributes({u, v}, Flat)", //
				"");
		check("u(x_) := {x}", //
				"");
		check("u()", //
				"u()");
		// `Flat` is taken into account in pattern matching
		check("u(a)", //
				"{a}");
		// stack overflow?
		// check("u(a, b)", "Iteration limit of 500 exceeded.");
		// check("u(a, b, c)", "Iteration limit of 500 exceeded.");

		check("v(x_) := x   ", "");
		check("v()", //
				"v()");
		check("v(a)", //
				"a");
		check("v(a, b)", //
				"Iteration limit of 500 exceeded.");
		check("v(a, b, c)", //
				"Iteration limit of 500 exceeded.");
	}

	public void testFlatOrderlessOneIdentity() {

		// see github issue 89

		check("SetAttributes(oi, {OneIdentity})", "");
		check("oi(p, q, r) /. {oi(x_,y_) :> {x,y}}", //
				"oi(p,q,r)");
		check("First@( oi(p, q, r) /. {oi(x_,y_) :> {x,y}} )", //
				"p");
		check("Rest@( oi(p, q, r) /. {oi(x_,y_) :> {x,y}} )", "oi(q,r)");

		check("SetAttributes(fo, {Flat, Orderless})", "");
		check("fo(p, q, r) /. {fo(x_,y_) :> {x,y}}", //
				"{p,fo(q,r)}");
		check("First@( fo(p, q, r) /. {fo(x_,y_) :> {x,y}} )", //
				"p");
		check("Rest@( fo(p, q, r) /. {fo(x_,y_) :> {x,y}} )", "{fo(q,r)}");

		check("Flatten(Table(Union(Sort/@Permutations({Flat,Orderless,OneIdentity}, {i})), {i,3}), 1)", //
				//
				"{{Flat}," //
						+ "{OneIdentity}," //
						+ "{Orderless}," //
						+ "{Flat,OneIdentity},"//
						+ "{Flat,Orderless}," //
						+ "{OneIdentity,Orderless}," //
						+ "{Flat,OneIdentity,Orderless}}");

		// Check when substitution is defined BEFORE setting attributes.
		check("Table(Module({ e = (eqv(p,q,r) /. {eqv(x_,y_) :> {x,y}}) }, ClearAll(eqv); " //
				+ "SetAttributes(eqv,j); "//
				+ "{j, First@e, Rest@e}),"//
				+ " {j, Flatten(Table(Union(Sort/@Permutations({Flat,Orderless,OneIdentity}, {i})), {i,3}), 1)})", //
				//
				"{{{Flat},p,eqv(q,r)}," //
						+ "{{OneIdentity},eqv(p),{eqv(q,r)}}," //
						+ "{{Orderless},p,eqv(q,r)}," //
						+ "{{Flat,OneIdentity},p,eqv(q,r)}," //
						+ "{{Flat,Orderless},p,{eqv(q,r)}}," //
						+ "{{OneIdentity,Orderless},p,{eqv(q,r)}}," //
						+ "{{Flat,OneIdentity,Orderless},p,eqv(q,r)}}");

		// Check when substitution is defined AFTER setting attributes.

		check("Table(Module({e},ClearAll(eqv);" //
				+ "SetAttributes(eqv,j);" //
				+ "e=(eqv(p,q,r)/.{eqv(x_,y_):>{x,y}});{j,First@e,Rest@e})," //
				+ "{j,Flatten(Table(Union(Sort/@Permutations({Flat,Orderless,OneIdentity},{i})),{i,3}),1)})", //
				//
				"{{{Flat},eqv(p),{eqv(q,r)}}," //
						+ "{{OneIdentity},p,eqv(q,r)},"//
						+ "{{Orderless},p,eqv(q,r)},"//
						+ "{{Flat,OneIdentity},p,{eqv(q,r)}}," //
						+ "{{Flat,Orderless},p,{eqv(q,r)}}," //
						+ "{{OneIdentity,Orderless},p,eqv(q,r)}," //
						+ "{{Flat,OneIdentity,Orderless},p,{eqv(q,r)}}}");

	}

	public void testFlatten() {
		// https://oeis.org/A049615
		check("Table(Length(Select(Flatten(Table({x, y}, {x, 0, n - k}, {y, 0, k}), 1), GCD @@ # > 1 &)), {n, 0, 11}, {k, 0, n}) // Flatten", //
				"{0,0,0,1,0,1,2,1,1,2,3,2,3,2,3,4,3,4,4,3,4,5,4,6,6,6,4,5,6,5,7,8,8,7,5,6,7,6,9,9,\n"
						+ "11,9,9,6,7,8,7,10,12,12,12,12,10,7,8,9,8,12,13,16,14,16,13,12,8,9,10,9,13,15,17,\n"
						+ "18,18,17,15,13,9,10}");
		check("Flatten({{a, b}, {c, {d}, e}, {f, {g, h}}})", //
				"{a,b,c,d,e,f,g,h}");
		check("Flatten({{a, b}, {c, {d}, e}, {f, {g, h}}}, 1)", //
				"{a,b,c,{d},e,f,{g,h}}");
		check("Flatten(f(f(x, y), z))", //
				"f(x,y,z)");
		check("Flatten({0, {1}, {{2, -2}}, {{{3}, {-3}}}, {{{{4}}}}}, 0)", //
				"{0,{1},{{2,-2}},{{{3},{-3}}},{{{{4}}}}}");
		check("Flatten(f(g(u, v), f(x, y)), Infinity, g)", //
				"f(u,v,f(x,y))");
		check("Flatten(f(g(u, v), f(x, y)), Infinity, f)", //
				"f(g(u,v),x,y)");

		check("m = {{{1, 2}, {3}}, {{4}, {5, 6}}}", //
				"{{{1,2},{3}},{{4},{5,6}}}");
		// check("Flatten(m, {2})", "");
		// check("Flatten(m, {{2}})", "");
		// check("Flatten(m, {{2}, {1}})", "");
		// check("Flatten(m, {{2}, {1}, {3}})", "");
		// check("m = {{{1, 2}, {3}}, {{4}, {5, 6}}}", "");
		// check("m = {{{1, 2}, {3}}, {{4}, {5, 6}}}", "");
	}

	public void testFlattenAt() {
		check("FlattenAt(2)[{a, {b, c}, {d, e}, {f}}]", //
				"{a,b,c,{d,e},{f}}");
		check("FlattenAt({a, {b, c}, {d, e}, {f}}, 2)", //
				"{a,b,c,{d,e},{f}}");
		check("FlattenAt({a, g(b,c), {d, e}, {f}}, 2)", //
				"{a,b,c,{d,e},{f}}");
		check("FlattenAt(f(a, g(b,c), {d, e}, {f}), -2)", //
				"f(a,g(b,c),d,e,{f})");
		check("FlattenAt(f(a, g(b,c), {d, e}, {f}), 4)", //
				"f(a,g(b,c),{d,e},f)");
		check("Table(FlattenAt(f({a}, {b}, {c}, {d}), i), {i, 4})", //
				"{f(a,{b},{c},{d}),f({a},b,{c},{d}),f({a},{b},c,{d}),f({a},{b},{c},d)}");
	}

	public void testFloor() {
		check("Floor(-9/4)", //
				"-3");
		check("Floor(1/3)", //
				"0");
		check("Floor(-1/3)", //
				"-1");
		check("Floor(10.4)", //
				"10");
		check("Floor(10/3)", //
				"3");
		check("Floor(10)", //
				"10");
		check("Floor(21, 2)", //
				"20");
		check("Floor(2.6, 0.5)", //
				"2.5");
		check("Floor(-10.4)", //
				"-11");
		check("Floor(1.5 + 2.7*I)", //
				"1+I*2");
		check("Floor(10.4, -1)", //
				"11");
		check("Floor(-10.4, -1) ", //
				"-10");

		check("Floor(1.5)", //
				"1");
		check("Floor(1.5 + 2.7*I)", //
				"1+I*2");
	}

	public void testFold() {
		check("Fold(test, t1, {a, b, c, d})", //
				"test(test(test(test(t1,a),b),c),d)");
		check("Fold(f, x, {a, b, c, d})", //
				"f(f(f(f(x,a),b),c),d)");
		check("Fold(List, x, {a, b, c, d})", //
				"{{{{x,a},b},c},d}");
		check("Fold(Times, 1, {a, b, c, d})", //
				"a*b*c*d");
		check("Fold(#1^#2 &, x, {a, b, c, d})", //
				"(((x^a)^b)^c)^d");
		check("Catch(Fold(If(# > 10^6, Throw(#), #^2 + #1) &, 2, Range(6)))", //
				"3263442");
		check("Fold(g(#2, #1) &, x, {a, b, c, d})", //
				"g(d,g(c,g(b,g(a,x))))");
		check("Fold(x *#1 + #2 &, 0, {a, b, c, d, e})", //
				"e+x*(d+x*(c+x*(b+a*x)))");
	}

	public void testFoldList() {
		// A002110 Primorial numbers: product of first n primes.
		// https://oeis.org/A002110
		check("FoldList(Times, 1, Prime(Range(20)))", //
				"{1,2,6,30,210,2310,30030,510510,9699690,223092870,6469693230,200560490130,\n"
						+ "7420738134810,304250263527210,13082761331670030,614889782588491410,\n"
						+ "32589158477190044730,1922760350154212639070,117288381359406970983270,\n"
						+ "7858321551080267055879090,557940830126698960967415390}");

		check("FoldList(f, {})", //
				"{}");
		check("FoldList(f, g(a))", //
				"g(a)");
		check("FoldList(f, {a, b, c, d})", //
				"{a,f(a,b),f(f(a,b),c),f(f(f(a,b),c),d)}");
		check("FoldList(Plus,{1,2,3})", //
				"{1,3,6}");

		check("foldlist(f, x, h())", //
				"h(x)");
		check("foldlist(f, x, h(c))", //
				"h(x,f(x,c))");
		check("foldlist(#^2 + #1 &, 2, range(6))", //
				"{2,6,42,1806,3263442,10650056950806,113423713055421844361000442}");
		check("foldlist(f, x, {a, b, c, d})", //
				"{x,f(x,a),f(f(x,a),b),f(f(f(x,a),b),c),f(f(f(f(x,a),b),c),d)}");
		// check("FoldList(Times, 1, Array(Prime, 10))", "");
		check("foldlist(1/(#2 + #1) &, x, reverse({a, b, c}))", //
				"{x,1/(c+x),1/(b+1/(c+x)),1/(a+1/(b+1/(c+x)))}");

	}

	public void testFor() {
		check("n := 1; For(i=1, i<=10, i=i+1, n = n * i);n", //
				"3628800");
		check("n==10!", //
				"True");
		check("n := 1;For(i=1, i<=10, i=i+1, If(i > 5, Return(i)); n = n * i)", //
				"6");
		check("n", //
				"120");

		check("For($i = 0, $i < 4, $i++, Print($i))", //
				"");
		check("For($i = 0, $i < 4, $i++)", //
				"");
		check("$i = 0;For($j = 0, $i < 4, $i++, Print($i));$i", //
				"4");
		check("$i = 0;For($j = 0, $i < 4, $i++);$i", //
				"4");
		check("$i = 0;For($j = 0, $i < 4, $i++)", //
				"");
		check("For($ = 1, $i < 1000, $i++, If($i > 10, Break())); $i", //
				"11");
		check("For($t = 1; $k = 1, $k <= 5, $k++, $t *= $k; Print($t); If($k < 2, Continue()); $t += 2)", //
				"");
	}

	public void testForAll() {
		check("ForAll(a, f(b)>c)", //
				"f(b)>c");
	}

	public void testFourier() {
		check("Fourier({1,2-I, -I, -1+2*I})", //
				"{1.0,2.0+I*2.0,I*(-1.0),-1.0+I*(-1.0)}");

		check("Fourier({1 + 2*I, 3 + 11*I})", //
				"{2.82843+I*9.19239,-1.41421+I*(-6.36396)}");
		check("Fourier({1,2,0,0})", //
				"{1.5,0.5+I*1.0,-0.5,0.5+I*(-1.0)}");

		// Fourier: Argument {1,2,0,0,7} is restricted to vectors with a length of power of 2.
		check("Fourier({1,2,0,0,7})", //
				"Fourier({1,2,0,0,7})");
	}

	public void testFourierMatrix() {
		check("FourierMatrix(4)", //
				"{{1/2,1/2,1/2,1/2},\n" + " {1/2,I*1/2,-1/2,-I*1/2},\n" + " {1/2,-1/2,1/2,-1/2},\n"
						+ " {1/2,-I*1/2,-1/2,I*1/2}}");
	}

	public void testFractionalPart() {
		check("FractionalPart(test)", //
				"FractionalPart(test)");
		check("FractionalPart({-2.4, -2.5, -3.0})", //
				"{-0.4,-0.5,0.0}");
		check("FractionalPart(14/32)", //
				"7/16");
		check("FractionalPart(4/(1 + 3 I))", //
				"2/5-I*1/5");
		check("FractionalPart(Pi^20)", //
				"-8769956796+Pi^20");
		check("FractionalPart(I*Infinity)", //
				"I*Interval({0,1})");
		check("FractionalPart(-I*Infinity)", //
				"-I*Interval({0,1})");
		check("FractionalPart(ComplexInfinity)", //
				"Interval({0,1})");

		check("FractionalPart(2.4+3.1*I)", //
				"0.4+I*0.1");
		check("FractionalPart(-5/3-(7/3)*I)", //
				"-2/3-I*1/3");
		check("FractionalPart(Cos(Pi/2))", //
				"0");
		check("FractionalPart(Sin(7/17))", //
				"Sin(7/17)");
		check("FractionalPart(Sin(-7/17))", //
				"-Sin(7/17)");

		check("FractionalPart(-Pi)", //
				"3-Pi");
		check("FractionalPart(GoldenRatio)", //
				"-1+GoldenRatio");
		check("FractionalPart(-9/4)", //
				"-1/4");
		check("FractionalPart(-9/4)+IntegerPart(-9/4)", //
				"-9/4");
		check("FractionalPart(-2.25)+IntegerPart(-2.25)", //
				"-2.25");
		check("FractionalPart(9/4)+IntegerPart(9/4)", //
				"9/4");
		check("FractionalPart(2.25)+IntegerPart(2.25)", //
				"2.25");
		check("FractionalPart(-2.25)+IntegerPart(-2.25)", //
				"-2.25");
		check("FractionalPart(0)+IntegerPart(0)", //
				"0");
		check("FractionalPart(0.0)+IntegerPart(0.0)", //
				"0.0");
		check("FractionalPart(1)+IntegerPart(1)", //
				"1");
		check("FractionalPart(1.0)+IntegerPart(1.0)", //
				"1.0");
		check("FractionalPart(-1)+IntegerPart(-1)", //
				"-1");
		check("FractionalPart(-1.0)+IntegerPart(-1.0)", //
				"-1.0");
		checkNumeric("FractionalPart(2.4)", //
				"0.3999999999999999");
		checkNumeric("FractionalPart(-2.4)", //
				"-0.3999999999999999");
		checkNumeric("FractionalPart({-2.4, -2.5, -3.0})", //
				"{-0.3999999999999999,-0.5,0.0}");
	}

	public void testFreeQ() {
		// see notes for MemberQ
		check("FreeQ(Sin(x*y),Sin)", //
				"False");
		check("s=Sin;FreeQ(Sin(x*y),s)", //
				"False");
		check("FreeQ(x_+y_+z_)[a+b]", //
				"True");
		check("FreeQ(a + b + c, a + c)", //
				"False");
	}

	public void testFresnelC() {
		check("FresnelC(0)", //
				"0");
		check("FresnelC(Infinity)", //
				"1/2");
		check("FresnelC(-Infinity)", //
				"-1/2");
		check("FresnelC(I*Infinity)", //
				"I*1/2");
		check("FresnelC(-I*Infinity)", //
				"-I*1/2");

		check("FresnelC(-z)", //
				"-FresnelC(z)");
		check("FresnelC(I*z)", //
				"I*FresnelC(z)");
		checkNumeric("FresnelC(1.8)", //
				"0.33363292722155624");

		check("D(FresnelC(x),x)", //
				"Cos(1/2*Pi*x^2)");
	}

	public void testFresnelS() {
		check("FresnelS(0)", //
				"0");
		check("FresnelS(Infinity)", //
				"1/2");
		check("FresnelS(-Infinity)", //
				"-1/2");
		check("FresnelS(I*Infinity)", //
				"-I*1/2");
		check("FresnelS(-I*Infinity)", //
				"I*1/2");

		check("FresnelS(-z)", //
				"-FresnelS(z)");
		check("FresnelS(I*z)", //
				"-I*FresnelS(z)");
		checkNumeric("FresnelS(1.8)", //
				"0.4509387692675837");
		check("D(Fresnels(x),x)", //
				"Sin(1/2*Pi*x^2)");
	}

	public void testFrobeniusNumber() {
		check("FrobeniusNumber({1000, 1476, 3764, 4864, 4871, 7773})", //
				"47350");
		check("FrobeniusNumber({12,16,20,27})", //
				"89");
		check("Table(FrobeniusNumber({i, i + 1}), {i, 15})", //
				"{-1,1,5,11,19,29,41,55,71,89,109,131,155,181,209}");
	}

	public void testFrobeniusSolve() {
		check("FrobeniusSolve({1000, 1476, 3764, 4864, 4871, 7773}, 47349)", //
				"{{5,2,4,2,3,0},{6,1,0,4,1,2},{7,5,1,3,3,0},{15,9,3,0,0,1},{17,12,0,1,0,1}}");
		check("FrobeniusSolve({1000, 1476, 3764, 4864, 4871, 7773}, 47350)", //
				"{}");
		check("FrobeniusSolve({1000, 1476, 3764, 4864, 4871, 7773}, 47351)", //
				"{{1,2,3,3,2,1},{3,5,0,4,2,1},{9,3,1,0,3,2},{12,13,3,0,1,0},{14,16,0,1,1,0},{32,2,\n" + "2,0,1,0}}");
		check("FrobeniusSolve({2, 3, 4}, 29)", //
				"{{0,3,5},{0,7,2},{1,1,6},{1,5,3},{1,9,0},{2,3,4},{2,7,1},{3,1,5},{3,5,2},{4,3,3},{\n"
						+ "4,7,0},{5,1,4},{5,5,1},{6,3,2},{7,1,3},{7,5,0},{8,3,1},{9,1,2},{10,3,0},{11,1,1},{\n"
						+ "13,1,0}}");
		check("frobeniussolve({ 12, 16, 20, 27},123 )", //
				"{{0,1,4,1},{0,6,0,1},{1,4,1,1},{2,2,2,1},{3,0,3,1},{4,3,0,1},{5,1,1,1},{8,0,0,1}}");
		check("frobeniussolve({ 12, 16, 20, 27},89 )", //
				"{}");
		check("frobeniussolve({1, 5, 10, 25}, 42)", //
				"{{2,0,4,0},{2,1,1,1},{2,2,3,0},{2,3,0,1},{2,4,2,0},{2,6,1,0},{2,8,0,0},{7,0,1,1},{\n"
						+ "7,1,3,0},{7,2,0,1},{7,3,2,0},{7,5,1,0},{7,7,0,0},{12,0,3,0},{12,1,0,1},{12,2,2,0},{\n"
						+ "12,4,1,0},{12,6,0,0},{17,0,0,1},{17,1,2,0},{17,3,1,0},{17,5,0,0},{22,0,2,0},{22,\n"
						+ "2,1,0},{22,4,0,0},{27,1,1,0},{27,3,0,0},{32,0,1,0},{32,2,0,0},{37,1,0,0},{42,0,0,\n"
						+ "0}}");
	}

	public void testFromCharacterCode() {
		check("FromCharacterCode(-42)", //
				"FromCharacterCode(-42)");
		check("FromCharacterCode({65,-10,67})", //
				"FromCharacterCode({65,-10,67})");
		check("FromCharacterCode({65,66,67,68,32,97,98,99,100})", //
				"ABCD abcd");
		check("ToCharacterCode(\"ABCD abcd\")", //
				"{65,66,67,68,32,97,98,99,100}");
	}

	public void testFromContinuedFraction() {
		check("FromContinuedFraction({1,1,1,1,1})", //
				"8/5");
		check("FromContinuedFraction({2,3,4,5})", //
				"157/68");
		check("ContinuedFraction(157/68)", //
				"{2,3,4,5}");
	}

	public void testFromDigits() {
		// https://oeis.org/A023391
		check("NestList(FromDigits(IntegerDigits(#, 8), 9) &, 8, 50)", //
				"{8,9,10,11,12,13,14,15,16,18,20,22,24,27,30,33,37,41,46,51,57,64,81,100,121,145,\n"
						+ "181,221,275,345,433,541,761,1036,1471,2014,2787,3927,5533,8537,13555,21441,34102,\n"
						+ "60891,103386,185033,329032,651411,1286139,2551404,5654254}");
		check("FromDigits({0})", //
				"0");
		check("FromDigits({1,2,3})", //
				"123");
		check("FromDigits({1,1,1,1,0,1,1}, 2)", //
				"123");
		check("FromDigits /@ IntegerDigits(Range(-10, 10))", //
				"{10,9,8,7,6,5,4,3,2,1,0,1,2,3,4,5,6,7,8,9,10}");
		check("FromDigits(\"123\")", //
				"123");
		check("FromDigits(\"1111011\", 2)", //
				"123");
		check("FromDigits(\"0\")", //
				"0");
		check("FromDigits(\"789ABC\")", //
				"790122");
		check("FromDigits(\"789ABC\", 16)", //
				"7903932");
		check("FromDigits(\"789abc\")", //
				"790122");
		check("FromDigits(\"789abc\", 16)", //
				"7903932");
		check("FromDigits(\"1A3C\")", //
				"2042");
	}

	public void testFromPolarCoordinates() {
		check("FromPolarCoordinates({r, t})", //
				"{r*Cos(t),r*Sin(t)}");
		check("FromPolarCoordinates({r, t, p})", //
				"{r*Cos(t),r*Cos(p)*Sin(t),r*Sin(p)*Sin(t)}");
		check("FromPolarCoordinates({{{r, t}, {1,0}}, {{2, Pi}, {1, Pi/2}}})", //
				"{{{r*Cos(t),r*Sin(t)},{1,0}},{{-2,0},{0,1}}}");
	}

	public void testFullForm() {
		check("( _. ) // FullForm", //
				"Optional(Blank())");
		check("Optional(Blank())", //
				"_.");
		check("( _:2 ) // FullForm", //
				"Optional(Blank(), 2)");
		check("Optional(Blank(),2)", //
				"_:2");
		check("( x_. ) // FullForm", //
				"Optional(Pattern(x, Blank()))");
		check("Optional(Pattern(x, Blank()))", //
				"x_.");
		check("( x_:2 ) // FullForm", //
				"Optional(Pattern(x, Blank()), 2)");
		check("Optional(Pattern(x,Blank()),2)", //
				"x_:2");
		// FullForm[Hold[(#1[[1]]*#1[[2]] & )[SignOfFactor[NormalizeSumFactors /@ u]]]]
		check("Hold(Function(#[[1]]*#[[2]])[SignOfFactor(Map(NormalizeSumFactors,u))]) // FullForm", //
				"Hold(Function(Times(Part(Slot(1), 1), Part(Slot(1), 2)))[signoffactor(Map(normalizesumfactors, u))])");
		check("FullForm(a:=b)", "Null");
	}

	public void testFullSimplify() {
		// see Logarithms#test0128() Rubi rule 2447 ==> -(Sqrt(-(e/d))/(2*e))
		check("FullSimplify( (d-2*d*Sqrt(-e/d)*x-e*x^2)/(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2) )", //
				"1/(2*d*Sqrt(-e/d))");
		check("PolynomialQuotientRemainder((d-2*d*Sqrt(-e/d)*x-e*x^2),(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2),x)", //
				"{1/(2*d*Sqrt(-e/d)),0}");

		// check("FullSimplify((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / " //
		// + "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) + (2*(d*Sqrt(-(e/d)) + e*x))/(d +
		// e*x^2)))", //
		// "(d-2*d*Sqrt(-e/d)*x-e*x^2)/(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2)");
		check("FullSimplify((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / " //
				+ "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) + (2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))", //
				"-Sqrt(-e/d)/(2*e)");

		check("FullSimplify(Numerator((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / " //
				+ "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) + (2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2))))", //
				"1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2)");
		check("FullSimplify(Denominator((1/(d + e*x^2) * (1-((2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)))) / " //
				+ "(-((4*e*x^2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2)^2) + (2*e*x)/(d + e*x^2) + (2*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2))))", //
				"(2*e*(d+e*x^2))/(-d*Sqrt(-e/d)+2*e*x+e*Sqrt(-e/d)*x^2)");
		check("Together( 1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2) )", //
				"(d-2*d*Sqrt(-e/d)*x-e*x^2)/(d+e*x^2)");
		check("Together( (d*(2*d*Sqrt(-e/d)+4*e*x-2*e*Sqrt(-e/d)*x^2))/(d+e*x^2) )", //
				"(2*d^2*Sqrt(-e/d)+4*d*e*x-2*d*e*Sqrt(-e/d)*x^2)/(d+e*x^2)");

		// #github #152
		check("FullSimplify(Sqrt(-9-4*Sqrt(5)))", //
				"I*(2+Sqrt(5))");
		check("FullSimplify( Sqrt(-9+4*Sqrt(5)))", //
				"I*(-2+Sqrt(5))");
		check("FullSimplify( Sqrt(9-4*Sqrt(5)))", //
				"-2+Sqrt(5)");
		check("FullSimplify(Sqrt(9+4*Sqrt(5)))", //
				"2+Sqrt(5)");
		check("FullSimplify(-Sqrt(9-4*Sqrt(5))+Sqrt(9+4*Sqrt(5)))", //
				"4");
		check("-Sqrt(9-4*Sqrt(5))+Sqrt(9+4*Sqrt(5.0))", //
				"4.0");

		check("FullSimplify(Sqrt(2) + Sqrt(3) - Sqrt(5 + 2*Sqrt(6)))", //
				"0");
		check("FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
				"True");

		// check("Factor((d^2+2*d*e*x^2+e^2*x^4))",//
		// "(d+e*x^2)^2");
		// check("Together(D( 1-(2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2),x))",//
		// "(-2*d^2*Sqrt(-e/d)-4*d*e*x+2*d*e*Sqrt(-e/d)*x^2)/(d^2+2*d*e*x^2+e^2*x^4)");
		check("FullSimplify(D( 1-(2*x*(d*Sqrt(-(e/d)) + e*x))/(d + e*x^2),x))", //
				"2/(-Sqrt(-d/e)-2*x+(e*x^2)/(d*Sqrt(-e/d)))");

		check("p = Expand((x + 1)^2 (x + 2)^2 (x + 3)^3)", //
				"108+432*x+711*x^2+625*x^3+318*x^4+94*x^5+15*x^6+x^7");
		check("FullSimplify(p)", //
				"(1+x)^2*(2+x)^2*(3+x)^3");
		check("FullSimplify(Cosh(x)/(b*Cosh(x)+c*Sinh(x)))", //
				"1/(b+c*Tanh(x))");
		check("FullSimplify((b*Cosh(x)+c*Sinh(x))/Cosh(x))", //
				"b+c*Tanh(x)");
		check("Simplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
				"-ChebyshevT(n,x)+Cos(n*ArcCos(x))==0");
		// FullSimplify uses FunctionExpand and can test the equation:
		check("FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))", //
				"True");
		check("FullSimplify(Cosh(x)+Sinh(x))", //
				"E^x");
	}

	public void testFunction() {
		check("(4*# + (2*# &)[a] &)[b]", //
				"2*a+4*b");
		check("fufufu=Function({x},Function({y},Function({z},x+y+z)))", //
				"Function({x},Function({y},Function({z},x+y+z)))");
		check("fufufu(a)", //
				"Function({y$1},Function({z$1},a+y$1+z$1))");
		check("fufufu(y)", //
				"Function({y$2},Function({z$2},y+y$2+z$2))");
		check("fufufu(y)[z]", //
				"Function({z$3$4},y+z+z$3$4)");
		check("fufufu(y)[x][z]", //
				"x+y+z");

		check("Function({x, y}, x^2 + y^3)[a, b]", //
				"a^2+b^3");
		check("f(x, ##, y, ##) &(a, b, c, d)", //
				"f(x,a,b,c,d,y,a,b,c,d)");
		check("f(x, ##2, y, ##3) &(a, b, c, d)", //
				"f(x,b,c,d,y,c,d)");
		check("If(# > 5, #, False) &(2)", //
				"False");
		check("{##} &(a, b, c)", //
				"{a,b,c}");
		check("{##2} &(a, b, c)", //
				"{b,c}");
		check("Table(a(i0, j), ##) & @@ {{i0, 3}, {j, 2}}", //
				"{{a(1,1),a(1,2)},{a(2,1),a(2,2)},{a(3,1),a(3,2)}}");
		check("Map(Function(-#),-z+w)", //
				"-w+z");
		check("f(#1) &(x, y, z)", //
				"f(x)");
		check("17 & /@ {1, 2, 3}", //
				"{17,17,17}");
		check("(p + #) & /. p -> q", //
				"q+#1&");
		check("FullForm(x -> y &)", //
				"Function(Rule(x, y))");
		check("FullForm(x -> (y &))", //
				"Rule(x, Function(y))");
		check("FullForm(Mod(#, 5) == 1 &)", //
				"Function(Equal(Mod(Slot(1), 5), 1))");
		check("FullForm(a == b && c == d &)", //
				"Function(And(Equal(a, b), Equal(c, d)))");
		check("FullForm(Mod(#, 3) == 1 && Mod(#, 5) == 1 &)", //
				"Function(And(Equal(Mod(Slot(1), 3), 1), Equal(Mod(Slot(1), 5), 1)))");

		check("({#1,Plus(##2)}&) @@@(Range/@Range(2,3))", "{{1,2},{1,5}}");
		check("(#[[1]]+#[[2]]&) /@{{1,2},{3,4,5},{6,7}}", "{3,7,13}");
		check("((#+##&) @@#&) /@{{1,2},{2,2,2},{3,4}}", "{4,8,10}");

		check("Function({x,y},x y)[2,3]", "6");
		check("Function(x,2 x)[5]", "10");
		check("(Function@@{{x},x==2})[2]", "True");
	}

	public void testFunctionRange() {
		// TODO
		// check("FunctionRange(Sqrt(x^2 - 1)/x, x, y)", //
		// "");
		check("FunctionRange(x/(1 + x^2), x, y)", //
				"-1/2<=y<=1/2");
		check("FunctionRange(x^2-1, x, y)", //
				"y>=-1");

		check("FunctionRange(1/(1 + x^2), x, y)", //
				"0<=y<=1");
		check("FunctionRange(Sqrt(Sin(2*x)),x,y)", //
				"0<=y<=1");
		check("FunctionRange(Sin(x)*Cos(x),x,y)", //
				"-1<=y<=1");
		check("FunctionRange(Sin(x),x,y)", //
				"-1<=y<=1");
		check("FunctionRange(Cos(x),x,y)", //
				"-1<=y<=1");
	}

	public void testFunctionExpand() {
		check("FunctionExpand(Sqrt(5/6 + 2*Sqrt(1/6)))", //
				"1/Sqrt(2)+1/Sqrt(3)");
		check("FunctionExpand(Sqrt(3+2*Sqrt(2)))", //
				"1+Sqrt(2)");
		check("FunctionExpand(Sqrt(5+2*Sqrt(6)))", //
				"Sqrt(2)+Sqrt(3)");

		check("FunctionExpand(Degree)", //
				"Pi/180");
		check("FunctionExpand(GoldenRatio)", //
				"1/2*(1+Sqrt(5))");
		check("FunctionExpand(Cos(Sqrt(x^2)))", //
				"Cos(x)");
		check("FunctionExpand(Sin(Sqrt(x^2)))", //
				"(Sqrt(x^2)*Sin(x))/x");
		check("FunctionExpand(Haversine(hf))", //
				"1/2*(1-Cos(hf))");
		check("FunctionExpand(InverseHaversine(ihf))", //
				"2*ArcSin(Sqrt(ihf))");

		check("FunctionExpand(E^ArcSinh(x))", //
				"x+Sqrt(1+x^2)");
		check("FunctionExpand(E^ArcCosh(x))", //
				"x+Sqrt(-1+x)*Sqrt(1+x)");
		check("FunctionExpand(E^ArcTanh(x))", //
				"(1+x)/Sqrt(1-x^2)");
		check("FunctionExpand(E^ArcCsch(x))", //
				"Sqrt(1+1/x^2)+1/x");
		check("FunctionExpand(E^ArcSech(x))", //
				"Sqrt(-1+1/x)*Sqrt(1+1/x)+1/x");
		check("FunctionExpand(E^ArcCoth(x))", //
				"1/Sqrt((-1+x)/(1+x))");

		check("FunctionExpand(Log(Sqrt(x^2)))", //
				"Log(x^2)/2");
		check("FunctionExpand(CosIntegral(Sqrt(x^2)))", //
				"CosIntegral(x)-Log(x)+Log(x^2)/2");
		check("FunctionExpand(SinIntegral(Sqrt(x^2)))", //
				"(Sqrt(x^2)*SinIntegral(x))/x");

		check("FunctionExpand((x+y)^3)", //
				"(x+y)^3");
		check("FunctionExpand((x+y)*(a+b))", //
				"(a+b)*(x+y)");

		check("FunctionExpand(Binomial(n,k))", //
				"Gamma(1+n)/(Gamma(1+k)*Gamma(1-k+n))");
		check("FunctionExpand(Binomial(n,7))", //
				"1/5040*(-6+n)*(-5+n)*(-4+n)*(-3+n)*(-2+n)*(-1+n)*n");
		check("FunctionExpand(Binomial(3,k))", //
				"(6*Sin(k*Pi))/((-3+k)*(-2+k)*(-1+k)*k*Pi)");
		check("FunctionExpand(Binomial(6,k))", //
				"(720*Sin(k*Pi))/((-6+k)*(-5+k)*(-4+k)*(-3+k)*(-2+k)*(-1+k)*k*Pi)");
		check("FunctionExpand(BlackmanHarrisWindow(x))", //
				"Piecewise({{1/100000*(35875+48829*Cos(2*Pi*x)+14128*Cos(4*Pi*x)+1168*Cos(6*Pi*x)),\n"
						+ "-1/2<=x<=1/2}},0)");
		check("BlackmanHarrisWindow(1/4)", //
				"21747/100000");
		check("FunctionExpand(BlackmanHarrisWindow(x))", //
				"Piecewise({{1/100000*(35875+48829*Cos(2*Pi*x)+14128*Cos(4*Pi*x)+1168*Cos(6*Pi*x)),\n"
						+ "-1/2<=x<=1/2}},0)");
		check("FunctionExpand(BlackmanNuttallWindow(x))", //
				"Piecewise({{1/10000000*(3635819+4891775*Cos(2*Pi*x)+1365995*Cos(4*Pi*x)+106411*Cos(\n"
						+ "6*Pi*x)),-1/2<=x<=1/2}},0)");
		check("FunctionExpand(BlackmanWindow(x))", //
				"Piecewise({{1/50*(21+25*Cos(2*Pi*x)+4*Cos(4*Pi*x)),-1/2<=x<=1/2}},0)");
		check("FunctionExpand(DirichletWindow(x))", //
				"Piecewise({{1,-1/2<=x<=1/2}},0)");
		check("FunctionExpand(FlatTopWindow(x))", //
				"Piecewise({{1/1000000000*(215578947+416631580*Cos(2*Pi*x)+277263158*Cos(4*Pi*x)+\n"
						+ "83578947*Cos(6*Pi*x)+6947368*Cos(8*Pi*x)),-1/2<=x<=1/2}},0)");
		check("FunctionExpand(GaussianWindow(x))", //
				"Piecewise({{E^(-50/9*x^2),-1/2<=x<=1/2}},0)");
		check("FunctionExpand(HammingWindow(x))", //
				"Piecewise({{25/46+21/46*Cos(2*Pi*x),-1/2<=x<=1/2}},0)");
		check("FunctionExpand(HannWindow(x))", //
				"Piecewise({{1/2+Cos(2*Pi*x)/2,-1/2<=x<=1/2}},0)");
		check("FunctionExpand(NuttallWindow(x))", //
				"Piecewise({{1/250000*(88942+121849*Cos(2*Pi*x)+36058*Cos(4*Pi*x)+3151*Cos(6*Pi*x)),\n"
						+ "-1/2<=x<=1/2}},0)");
		check("FunctionExpand(ParzenWindow(x))", //
				"Piecewise({{-2*(-1+2*x)^3,1/4<x&&x<=1/2},{2*(1+2*x)^3,-1/2<=x&&x<-1/4},{1-24*x^2\n"
						+ "-48*x^3,-1/4<=x&&x<0},{1-24*x^2+48*x^3,0<=x<=1/4}},0)");
		check("FunctionExpand(TukeyWindow(x))", //
				"Piecewise({{1,-1/3-2*x<=0&&-1/3+2*x<=0},{1/2*(1+Cos(3*Pi*(1/6+x))),x>=-1/2&&-1/3\n"
						+ "-2*x>0},{1/2*(1+Cos(3*Pi*(-1/6+x))),-1/3+2*x>0&&x<=1/2}},0)");
		check("FunctionExpand(Log(10*E))", //
				"1+Log(10)");
		check("FunctionExpand(PolyGamma(-2, 1))", //
				"1/2*(Log(2)+Log(Pi))");
		check("FunctionExpand(PolyGamma(-3, 1))", //
				"Log(Glaisher)+1/4*(Log(2)+Log(Pi))");
		check("FunctionExpand(Fibonacci(n + 3), Element(n, Integers))", //
				"2*Fibonacci(n)+LucasL(n)");
		check("FunctionExpand(CatalanNumber(x))", //
				"(2^(2*x)*Gamma(1/2+x))/(Sqrt(Pi)*Gamma(2+x))");
		check("FunctionExpand(ChebyshevT(n, x))", //
				"Cos(n*ArcCos(x))");
		check("FunctionExpand(ChebyshevU(n, x))", //
				"Sin((1+n)*ArcCos(x))/(Sqrt(1-x)*Sqrt(1+x))");
		check("FunctionExpand(Factorial(x))", //
				"Gamma(1+x)");
		check("FunctionExpand(Factorial(x+3))", //
				"Gamma(4+x)");
		check("FunctionExpand(LegendreQ(3/2,1/2,x))", //
				"-Pi*LegendreP(3/2,-1/2,x)");
		check("FunctionExpand({Degree, GoldenRatio})", //
				"{Pi/180,1/2*(1+Sqrt(5))}");
		check("FunctionExpand(Beta(z,3,b))", //
				"(2*(1-(1-z)^b*(1+b*z+1/2*b*(1+b)*z^2)))/(b*(1+b)*(2+b))");
		check("FunctionExpand(BetaRegularized(z, a, b))", //
				"(Beta(z,a,b)*Gamma(a+b))/(Gamma(a)*Gamma(b))");
	}

	public void testGamma() {

		check("Gamma(2.3 + I)", //
				"0.719141+I*0.540614");
		check("Gamma(2.20000000000000000000000000000000000000000)", //
				"1.101802490879712732769141986222996480824186");
		check("N(Gamma(100.000000000000000000000000000+374.000000000000000000000000000*I), 30)", //
				"4.74294943677064514689542753376*10^1+I*(-3.27488916473624576880974867017*10^1)");
		check("Gamma(1/2, a*x)", //
				"Gamma(1/2,a*x)");
		check("Gamma(3, a*x)", //
				"Gamma(3,a*x)");
		check("Gamma(0)", //
				"ComplexInfinity");
		check("Gamma(0.0+I*0.0)", //
				"ComplexInfinity");
		check("Table(Gamma(x+I*y), {x,-0.5, 0.5, 1/4}, {y,-0.5, 0.5, 1/4})", //
				"{{-1.58148+I*0.0548502,-2.75473+I*0.0310004,-3.54491+I*(-4.34126*10^-16),-2.75473+I*(-0.0310004),-1.58148+I*(-0.0548502)},{-1.31815+I*1.01078,-2.77536+I*1.61484,-4.90167+I*(-6.00281*10^-16),-2.77536+I*(-1.61484),-1.31815+I*(-1.01078)},{-0.399279+I*1.60339,-0.524105+I*3.76716,ComplexInfinity,-0.524105+I*(-3.76716),-0.399279+I*(-1.60339)},{0.515524+I*1.30733,1.65113+I*1.83788,3.62561,1.65113+I*(-1.83788),0.515524+I*(-1.30733)},{0.818164+I*0.763314,1.38511+I*0.673182,1.77245,1.38511+I*(-0.673182),0.818164+I*(-0.763314)}}");
		check("Table(Gamma(x+I ), {x,-10.0, 10.0, 1/2})", //
				"{5.57498*10^-8+I*5.54232*10^-8,-1.68355*10^-7+I*1.86667*10^-7,-6.12921*10^-7+I*(-4.98482*10^-7),1.41270*10^-6+I*(-1.94169*10^-6),6.01477*10^-6+I*3.87342*10^-6,-0.0000100663+I*0.0000179171,-0.0000519916+I*(-0.0000249726),0.00005758+I*(-0.000144445),0.000388914+I*0.000122817,-0.000229825+I*0.00099647,-0.0024563+I*(-0.000347985),0.00026757+I*(-0.00571041),0.0126295+I*(-0.000716374),0.00450635+I*0.0259644,-0.0498016+I*0.015495,-0.0417366+I*(-0.0863691),0.13391+I*(-0.0962865),0.190711+I*0.174186,-0.171533+I*0.326483,-0.460252+I*(-0.0705685),-0.15495+I*(-0.498016),0.300695+I*(-0.424968),0.498016+I*(-0.15495),0.575315+I*0.0882107,0.652965+I*0.343066,0.774762+I*0.707631,0.962865+I*1.3391,1.22927+I*2.54384,1.5495+I*4.98016,1.75862+I*10.13271,1.21784+I*21.47013,-2.21893+I*47.35583,-15.38094+I*108.5685,-59.55994+I*258.2382,-200.8541+I*636.0298,-645.3778+I*1618.988,-2042.009+I*4251.355,-6459.321+I*11497.03,-20587.42+I*31968.83,-66401.27+I*91265.46,-217255.6+I*267132.0}");

		check("Refine(Gamma(n), Element(n,Integers)&&n>=0)", //
				"(-1+n)!");

		check("Gamma(-1)", //
				"ComplexInfinity");
		check("Gamma(Infinity)", //
				"Infinity");
		check("Gamma(-Infinity)", //
				"Indeterminate");
		check("Gamma(I*Infinity)", //
				"0");
		check("Gamma(-I*Infinity)", //
				"0");
		check("Gamma(ComplexInfinity)", //
				"Indeterminate");

		checkNumeric("Gamma(1.5,7.5)", //
				"0.0016099632282723212");
		check("Gamma(-3/4, 0)", //
				"ComplexInfinity");
		check("Gamma(10, -1)", //
				"133496*E");
		check("Gamma(1/2, x)", //
				"Gamma(1/2,x)");
		check("Gamma(8)", //
				"5040");
		check("Gamma(1/2)", //
				"Sqrt(Pi)");
		// check("Gamma(1.0+I)", "");
		checkNumeric("Gamma(2.2)", //
				"1.1018024908797128");
	}

	public void testGammaRegularized() {
		check("GammaRegularized(1, 1.5)", //
				"0.22313");
		check("GammaRegularized(2,  2.2)", //
				"0.35457");
		check("GammaRegularized(2,  3.3)", //
				"0.158598");
		check("GammaRegularized(2,  3.3, 3.4)", //
				"0.0117552");

		// TODO improve output Format to E^(-x)-E^(-y)
		check("GammaRegularized(1,x,y)", //
				"E^(-x)-1/E^y");
		check("GammaRegularized(-42,x,y)", //
				"0");
		// check("GammaRegularized(a,z1,z2)", "GammaRegularized(a,z1)-GammaRegularized(a,z2)");

		check("GammaRegularized(1/2, z)", //
				"Erfc(Sqrt(z))");
		check("GammaRegularized(-4, z)", //
				"0");
		check("GammaRegularized(12, 0)", //
				"1");
		check("GammaRegularized(-42, 0)", //
				"0");
		check("GammaRegularized(1, x)", //
				"E^(-x)");
	}

	public void testGather() {
		check("Gather({1, 7, 3, 7, 2, 3, 9})", //
				"{{1},{7,7},{3,3},{2},{9}}");
		check("Gather({1/3, 2/6, 1/9})", //
				"{{1/3,1/3},{1/9}}");

		check("Gather({{a, 1}, {b, 1}, {a, 2}, {d, 1}, {b, 3}}, (First(#1) == First(#2)) &)", //
				"{{{a,1},{a,2}},{{b,1},{b,3}},{{d,1}}}");
		check("Gather({1,2,3,2,3,4,5,6,2,3})", //
				"{{1},{2,2,2},{3,3,3},{4},{5},{6}}");
		check("Gather(Range(0, 3, 1/3), Floor(#1) == Floor(#2) &)", //
				"{{0,1/3,2/3},{1,4/3,5/3},{2,7/3,8/3},{3}}");
	}

	public void testGatherBy() {
		check("GatherBy({{1, 2}, {2, 1}, {3, 5}, {5, 1}, {2, 2, 2}}, {})", //
				"{{{1,2}},{{2,1}},{{3,5}},{{5,1}},{{2,2,2}}}");
		check("GatherBy({{1, 3}, {2, 2}, {1, 1}}, Total)", //
				"{{{1,3},{2,2}},{{1,1}}}");
		check("GatherBy({\"xy\", \"abc\", \"ab\"}, StringLength)", //
				"{{xy,ab},{abc}}");
		check("GatherBy({{2, 0}, {1, 5}, {1, 0}}, Last)", //
				"{{{2,0},{1,0}},{{1,5}}}");
		check("GatherBy({{1, 2}, {2, 1}, {3, 5}, {5, 1}, {2, 2, 2}}, {Total, Length})", //
				"{{{{1,2},{2,1}}},{{{3,5}}},{{{5,1}},{{2,2,2}}}}");

		check("rr=Range(10); GatherBy(rr, OddQ)", //
				"{{1,3,5,7,9},{2,4,6,8,10}}");
		check("GatherBy({{a,10},{b,5},{a,7},{b,3},{b,10}}, First)", //
				"{{{a,10},{a,7}},{{b,5},{b,3},{b,10}}}");
		check("Tuples({{a, b}, {1, 2}, {x, y}})", //
				"{{a,1,x},{a,1,y},{a,2,x},{a,2,y},{b,1,x},{b,1,y},{b,2,x},{b,2,y}}");
		check("GatherBy({{a,1,x},{a,1,y},{a,2,x},{a,2,y},{b,1,x},{b,1,y},{b,2,x},{b,2,y}}, {First,Last})", //
				"{{{{a,1,x},{a,2,x}},{{a,1,y},{a,2,y}}},{{{b,1,x},{b,2,x}},{{b,1,y},{b,2,y}}}}");
	}

	public void testGegenbauerC() {
		check("GegenbauerC(3, l, z)", //
				"-2*l*(1+l)*z+4/3*l*(1+l)*(2+l)*z^3");
		check("GegenbauerC(4, l, z)", //
				"1/2*l*(1+l)+2*(-1-l)*l*(2+l)*z^2+2/3*l*(1+l)*(2+l)*(3+l)*z^4");
		check("GegenbauerC(0, l, z)", //
				"1");
		check("GegenbauerC(0, 0, z)", //
				"0");
		check("GegenbauerC(n, 0, z)", //
				"0");
		check("GegenbauerC(n, 1, z)", //
				"ChebyshevU(n,z)");
		check("GegenbauerC(n, 2, z)", //
				"((-2-n)*ChebyshevU(n,z)+(1+n)*z*ChebyshevU(1+n,z))/(2*(-1+z^2))");

		check("GegenbauerC(5,z)", //
				"2*z-8*z^3+32/5*z^5");
		check("GegenbauerC(1/2,z)", //
				"2*Sqrt(2)*Sqrt(1+z)");
		check("GegenbauerC(-1/2,z)", //
				"-2*Sqrt(2)*Sqrt(1+z)");
		check("GegenbauerC(v,0)", //
				"(2*Cos(1/2*Pi*v))/v");
		check("GegenbauerC(v,1)", //
				"2/v");
		check("GegenbauerC(v,-1)", //
				"(2*Cos(Pi*v))/v");
		check("GegenbauerC(v,i)", //
				"GegenbauerC(v,i)");

		check("GegenbauerC(0,z)", //
				"ComplexInfinity");
		check("GegenbauerC(1,z)", //
				"2*z");
		check("GegenbauerC(2,z)", //
				"-1+2*z^2");
		check("GegenbauerC(-v,z)", //
				"-GegenbauerC(v,z)");
		check("GegenbauerC(10,-z)", //
				"-1/5+10*z^2-80*z^4+224*z^6-256*z^8+512/5*z^10");
		check("GegenbauerC(11,-z)", //
				"2*z-40*z^3+224*z^5-512*z^7+512*z^9-2048/11*z^11");
	}

	public void testGCD() {
		check("GCD(0,Cos(b*x)[[2]])", //
				"GCD(0,Cos(b*x)[[2]])");
		check("GCD(0, CoshIntegral(b*x))", //
				"GCD(0,CoshIntegral(b*x))");
		check("GCD(x,x)", //
				"GCD(x,x)");
		check("GCD(-2147483648)", //
				"2147483648");
		check("GCD(-2147483648, -2147483648/2)", //
				"1073741824");
		check("GCD(I)", //
				"1");
		check("GCD(-I)", //
				"1");
		check("GCD(1,I)", //
				"1");

		check("GCD()", //
				"0");
		check("GCD(10)", //
				"10");
		check("GCD(2, 3, 5)", //
				"1");
		check("GCD(1/3, 2/5, 3/7)", //
				"1/105");
		check("GCD(-3, 9)", //
				"3");
		check("GCD(b, a)", //
				"GCD(a,b)");

		check("GCD(20, 30)", //
				"10");
		check("GCD(-20, 30)", //
				"10");
		check("GCD(20, -30)", //
				"10");
		check("GCD(-20, -30)", //
				"10");
		check("GCD(10, y)", //
				"GCD(10,y)");
		check("GCD(4, {10, 11, 12, 13, 14})", //
				"{2,1,4,1,2}");
	}

	public void testGeometricMean() {
		checkNumeric("GeometricMean({1, 2.0, 3, 4})", //
				"2.213363839400643");
		check("GeometricMean({Pi,E,2})", //
				"(2*E*Pi)^(1/3)");
		check("GeometricMean({1, 2, 3, 4})", //
				"2^(3/4)*3^(1/4)");

		check("GeometricMean({})", //
				"GeometricMean({})");
		check("GeometricMean({2, 6, 5, 15, 10, 1})", //
				"3^(1/3)*Sqrt(10)");
		check("GeometricMean({{5, 10}, {2, 1}, {4, 3}, {12, 15}})", //
				"{2*30^(1/4),2^(1/4)*Sqrt(15)}");
		checkNumeric("GeometricMean(N({2, 6, 5, 15, 10, 1}))", //
				"4.56079359657056");
	}

	public void testGeoDistance() {
		// distance between Oslo and Berlin
		check("GeoDistance({59.914, 10.752}, {52.523, 13.412})", //
				"521.4298968444851[mi]");
		check("UnitConvert(GeoDistance({59.914, 10.752}, {52.523, 13.412}),\"km\")", //
				"839.1600759072911[km]");

		check("GeoDistance({37, -109}, {40.113, -88.261})", //
				"1140.8428557329898[mi]");
		check("GeoDistance({30, 40}, {-40, 120})", //
				"7031.6370551943855[mi]");
	}

	public void testGet() {
		if (Config.FILESYSTEM_ENABLED) {
			String pathToVectorAnalysis = getClass().getResource("/symja/VectorAnalysis.m").getFile();
			// remove 'file:/'
			// pathToVectorAnalysis = pathToVectorAnalysis.substring(6);
			System.out.println(pathToVectorAnalysis);
			// PatternMatching.getFile(pathToVectorAnalysis, engine)
			evalString("Get(\"" + pathToVectorAnalysis + "\")");
			check("DotProduct({a,b,c},{d,e,f}, Spherical)", //
					"a*d*Cos(b)*Cos(e)+a*d*Cos(c)*Cos(f)*Sin(b)*Sin(e)+a*d*Sin(b)*Sin(c)*Sin(e)*Sin(f)");
			// check("Information(Sin)", "");
			// check("Information(DotProduct)", "");
		}
	}

	public void testGoldenAngle() {
		// check("IntegerPart[GoldenAngle^100]", //
		// "");
		check("NumericQ(GoldenAngle)", //
				"True");
		check("N(GoldenAngle)", //
				"2.39996");
		check("Attributes(GoldenAngle)", //
				"{Constant,Protected}");
		check("FunctionExpand[GoldenAngle]", //
				"(3-Sqrt(5))*Pi");

	}

	public void testGoldenRatio() {
		check("N(GoldenRatio)", //
				"1.61803");
		check("Log(GoldenRatio)", //
				"ArcCsch(2)");
	}

	public void testGrad() {
		// gradient
		check("Grad(f(x, y), {x, y})", //
				"{Derivative(1,0)[f][x,y],Derivative(0,1)[f][x,y]}");
		check("Grad(Sin(x^2 + y^2), {x, y})", //
				"{2*x*Cos(x^2+y^2),2*y*Cos(x^2+y^2)}");

		// wikipedia
		check("Grad(2*x+3*y^2-Sin(z), {x, y, z})", //
				"{2,6*y,-Cos(z)}");
	}

	public void testGreater() {
		check("x>x", //
				"x>x");
		check("x+1>x", //
				"1+x>x");

		check("42>Infinity", //
				"False");

		check("Infinity>Infinity", //
				"False");

		check("Refine(Infinity>x, x>0)", //
				"True");
		check("Refine(-Infinity>x, x>0)", //
				"False");

		check("{Greater(), Greater(x), Greater(1)}", //
				"{True,True,True}");
		check("Pi>0", //
				"True");
		check("Pi+E<8", //
				"True");
		check("2/17 > 1/5 > Pi/10", //
				"False");
		check("x<x", //
				"x<x");
		check("x<=x", //
				"x<=x");
		check("x>x", //
				"x>x");
		check("x>=x", //
				"x>=x");
	}

	public void testGreaterEqual() {
		check("x>=x", //
				"x>=x");
		check("Infinity>=Infinity", //
				"True");

		check("Refine(Infinity>=x, x>0)", //
				"True");
		check("Refine(-Infinity>=x, x>0)", //
				"False");

		check("{GreaterEqual(), GreaterEqual(x), GreaterEqual(1)}", //
				"{True,True,True}");
		check("Pi>=0", //
				"True");
		check("Pi+E<=8", //
				"True");
		check("2/17 >= 1/5 >= Pi/10", //
				"False");
		check("x>=x", //
				"x>=x");
		check("x>x", //
				"x>x");
	}

	public void testGroebnerBasis() {
		check("GroebnerBasis({x*y-2*y, 2*y^2-x^2}, {y, x})", //
				"{-2*x^2+x^3,-2*y+x*y,-x^2+2*y^2}");
		check("GroebnerBasis({x*y-2*y, 2*y^2-x^2}, {x, y})", //
				"{-2*y+y^3,-2*y+x*y,x^2-2*y^2}");
		check("GroebnerBasis({-5*x^2+y*z-x-1, 2*x+3*x*y+y^2,x-3*y+x*z-2*z^2},{x,y,z}, MonomialOrder ->DegreeReverseLexicographic)", //
				"{x-3*y+x*z-2*z^2,2*x+3*x*y+y^2,1+x+5*x^2-y*z,-1+27*y+5*y^2-z-29*y*z+18*z^2+y*z^2\n"
						+ "-20*z^3,6-156*y-20*y^2+6*z+174*y*z+y^2*z-104*z^2+120*z^3,180-20*x-4185*y-559*y^2+\n"
						+ "15*y^3+162*z+4680*y*z-2808*z^2+3240*z^3,4026-20*x-106386*y-17140*y^2+4086*z+\n"
						+ "114129*y*z-70866*z^2+78768*z^3+1560*z^4}");
		check("GroebnerBasis({x^2 - 2*y^2, x*y - 3}, {x, y})", //
				"{-9+2*y^4,3*x-2*y^3}");
		check("GroebnerBasis({x + y, x^2 - 1, y^2 - 2*x}, {x, y})", //
				"{1}");
		check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x*y - z + 2, z^2 - 2*x + 3*y}, {x, y, z})", //
				"{1024-832*z-215*z^2+156*z^3-25*z^4+24*z^5+13*z^6+z^8,-11552+2560*y+2197*z+2764*z^\n"
						+ "2+443*z^3+728*z^4+169*z^5+32*z^6+13*z^7,-34656+5120*x+6591*z+5732*z^2+1329*z^3+\n"
						+ "2184*z^4+507*z^5+96*z^6+39*z^7}");
		check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x*y - z + 2}, {x, y, z})", //
				"{4-y^2+y^4-4*z+z^2+y^2*z^2,-2*x-y+y^3+x*z+y*z^2,2+x*y-z,-1+x^2+y^2+z^2}");
		check("GroebnerBasis({x^2 + y^2 + z^2 - 1, x*y - z + 2, z^2 - 3 + x,x - y^2 + 1}, {x, y, z})", //
				"{1}");
	}

	public void testHarmonicMean() {
		check("HarmonicMean({a,b,c,d})", //
				"4/(1/a+1/b+1/c+1/d)");
		check("HarmonicMean({{1, 2}, {5, 10}, {5, 2}, {4, 8}})", //
				"{80/33,160/49}");
	}

	public void testHarmonicNumber() {
		check("HarmonicNumber(-Infinity)", //
				"ComplexInfinity");
		check("HarmonicNumber(Infinity)", //
				"Infinity");
		check("HarmonicNumber(-42)", //
				"ComplexInfinity");
		check("HarmonicNumber(2,-3/2)", //
				"1+2*Sqrt(2)");
		check("Table(HarmonicNumber(n), {n, 8})", //
				"{1,3/2,11/6,25/12,137/60,49/20,363/140,761/280}");
		check("HarmonicNumber(4,r)", //
				"1+2^(-r)+3^(-r)+4^(-r)");
		check("HarmonicNumber(1,r)", //
				"1");
		check("HarmonicNumber(0,r)", //
				"0");
		check("HarmonicNumber(Infinity,2)", //
				"Pi^2/6");
	}

	public void testHaversine() {
		checkNumeric("Haversine(1.5)", //
				"0.46463139916614854");
		checkNumeric("Haversine(0.5 + 2 * I)", //
				"-1.1508186664570472+I*0.8694047522371582");
		checkNumeric("Haversine(0.5)", //
				"0.06120871905481362");
		checkNumeric("Haversine(1.5+I)", //
				"0.44542339697277356+I*0.5861286494553962");
		check("Haversine(Pi/3)", //
				"1/4");
		check("Haversine(90*Degree)", //
				"1/2");
		check("Haversine({0, Pi/4, Pi/3, Pi/2})", //
				"{0,1/2*(1-1/Sqrt(2)),1/4,1/2}");
	}

	public void testHead() {
		check("Head(f(a, b))", //
				"f");
		check("Head(a + b + c)", //
				"Plus");
		check("Head(a / b)", //
				"Times");
		check("Head(45)", //
				"Integer");
		check("Head(x)", //
				"Symbol");
		check("Head(f(x)[y][z])", //
				"f(x)[y]");
		check("Head({3, 4, 5})", //
				"List");
		check("FixedPoint(Head, f(x)[y][z])", //
				"Symbol");
		check("FixedPoint(Head, {3, 4, 5})", //
				"Symbol");

		check("Head(a * b)", //
				"Times");
		check("Head(6)", //
				"Integer");
		check("Head(x)", //
				"Symbol");
	}

	public void testHeavisideTheta() {
		check("Derivative(1)[HeavisideTheta]", //
				"DiracDelta(#1)&");
		check("HeavisideTheta(x)", //
				"HeavisideTheta(x)");
		check("D(HeavisideTheta(x), x)", //
				"DiracDelta(x)");
		check("HeavisideTheta(0)", //
				"HeavisideTheta(0)");
		check("HeavisideTheta(42)", //
				"1");
		check("HeavisideTheta(-1)", //
				"0");
		check("HeavisideTheta(-42)", //
				"0");
		check("HeavisideTheta({1.6, 1.6000000000000000000000000})", //
				"{1,1}");
		check("HeavisideTheta({-1, 0, 1})", //
				"{0,HeavisideTheta(0),1}");
		check("HeavisideTheta(1, 2, 3)", //
				"1");
		check("HeavisideTheta(-2, -1, 1, 2)", //
				"0");
	}

	public void testHermiteH() {
		check("HermiteH(i, x)", //
				"HermiteH(i,x)");
		check("HermiteH(8, x)", //
				"1680-13440*x^2+13440*x^4-3584*x^6+256*x^8");
		check("HermiteH(3, 1 + I)", //
				"-28+I*4");
		// TODO add non integer arg implementation
		// check("HermiteH(4.2, 2)", "");
		check("HermiteH(10, x)", //
				"-30240+302400*x^2-403200*x^4+161280*x^6-23040*x^8+1024*x^10");
	}

	public void testHermitianMatrixQ() {
		// example from https://en.wikipedia.org/wiki/Hermitian_matrix
		check("HermitianMatrixQ({{2, 2 + I, 4}, {2-I, 3, I}, {4, -I, 1}})", //
				"True");

		check("HermitianMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})", //
				"True");
		check("HermitianMatrixQ({{1, 3 + 3*I}, {3 - 4*I, 2}})", //
				"False");
		check("HermitianMatrixQ(Table(Re(i)*Re(j), {i, 10}, {j, 10}))", //
				"True");
	}

	public void testHilbertMatrix() {
		check("Inverse(HilbertMatrix(3))", //
				"{{9,-36,30},\n" + " {-36,192,-180},\n" + " {30,-180,180}}");
	}

	public void testHold() {
		check("Hold(1<2<3!=a<2==3) // FullForm", //
				"Hold(Inequality(1, Less, 2, Less, 3, Unequal, a, Less, 2, Equal, 3))");
		check("Hold(3*2)", //
				"Hold(3*2)");
		check("Hold(2+2)", //
				"Hold(2+2)");
		check("lst = Hold(1 + 2, 2*3*4*5, 1/0, Quit())", //
				"Hold(1+2,2*3*4*5,1/0,Quit())");
		check("Apply(List, Map(Hold, lst))", //
				"{Hold(1+2),Hold(2*3*4*5),Hold(1/0),Hold(Quit())}");
		check("expr = Hold({1 + 2, g(3 + 4, 2*3), f(1 + g(2 + 3))})", //
				"Hold({1+2,g(3+4,2*3),f(1+g(2+3))})");
		check("pos = Position(expr, _Plus)", //
				"{{1,1},{1,2,1},{1,3,1,2,1},{1,3,1}}");
		check("val = Extract(expr, pos)", //
				"{3,7,5,1+g(5)}");
		check("ReplacePart(expr, Thread(pos -> val))", //
				"Hold({3,g(7,2*3),f(1+g(5))})");
		check("Hold(6/8)==6/8", //
				"Hold(6/8)==3/4");
	}

	public void testHoldAllComplete() {
		check("ClearAll(fump); "//
				+ "SetAttributes(fump, HoldAllComplete);"//
				+ "fump(e_) := (Print(ToString(Unevaluated(e)) <> \" ~~>\\n\" <> ToString(e)); e); "//
				+ "fump((42 + 3)*6)", //
				"270");
	}

	public void testHoldForm() {
		check("HoldForm(3*2)", //
				"3*2");
		check("HoldForm(6/8)==6/8", //
				"6/8==3/4");
	}

	public void testHoldPattern() {
		check("HoldPattern( 1/(sq(a_)*sq(b_)) ) //FullForm", //
				"HoldPattern(Power(Times(sq(Pattern(a, Blank())), sq(Pattern(b, Blank()))), -1))");
		check(" 1/(sq(a_)*sq(b_)) //FullForm", //
				"Times(Power(sq(Pattern(a, Blank())), -1), Power(sq(Pattern(b, Blank())), -1))");

		check("HoldPattern( Sqrt(2*Pi*x_) )", //
				"HoldPattern(Sqrt(2*Pi*x_))");
		check("MatchQ(And(x, y, z), p__)", //
				"True");
		// because of OneIdentity attribute for Times
		check("MatchQ(And(x, y, z), Times(p__))", //
				"True");
		check("Times(p__)===And(p__)", //
				"True");
		check("MatchQ(And(x, y, z), HoldPattern(Times(p__)))", //
				"False");
		check("HoldPattern(Times(p__))===HoldPattern(And(p__))", //
				"False");
		check("And(x, y, z)/.HoldPattern(And(a__)) ->List(a)", //
				"{x,y,z}");
		check("And(x, y, z)/.And->List", //
				"{x,y,z}");
		check("And(x, y, z)/.And(a_,b___)->List(a,b)", //
				"{x,y,z}");

		check("a + b /. HoldPattern(_ + _) -> 0", //
				"0");
		check("MatchQ(Log(a, b), HoldPattern(Log(_)/Log(_)))", //
				"True");
		check("Cases({a -> b, c -> d}, HoldPattern(a -> _))", //
				"{a->b}");
	}

	public void testHornerForm() {
		check("HornerForm(11*x^3 - 4*x^2 + 7*x + 2)", //
				"2+x*(7+x*(-4+11*x))");
		check("HornerForm(a+b*x+c*x^2,x)", //
				"a+x*(b+c*x)");
	}

	public void testHypergeometric0F1() {
		check("Hypergeometric0F1(b, 0)", //
				"1");
		check("Hypergeometric0F1(b, Infinity)", //
				"ComplexInfinity");

		check("Hypergeometric0F1(1/2, z)", //
				"Cosh(2*Sqrt(z))");
		check("Hypergeometric0F1(1/2, -a)", //
				"Cos(2*Sqrt(a))");
		check("Hypergeometric0F1(3/2, z)", //
				"Sinh(2*Sqrt(z))/(2*Sqrt(z))");
		check("Hypergeometric0F1(3/2, -a)", //
				"Sin(2*Sqrt(a))/(2*Sqrt(a))");

		check("Hypergeometric0F1({1, 2, 3}, 1.5)", //
				"{3.16559,1.96279,1.60374}");
		check("Hypergeometric0F1(1,-2.0)", //
				"-0.196548");
		checkNumeric("Hypergeometric0F1(1,1.5)", //
				"3.1655890675997793");
	}

	public void testHypergeometric1F1() {
		check("Hypergeometric1F1(1,b,z)", //
				"(-1+b)*E^z*z^(1-b)*(Gamma(-1+b)-Gamma(-1+b,z))");
		check("Hypergeometric1F1(2,b,z)", //
				"(-1+b)*(1+(2-b)*E^z*z^(1-b)*(Gamma(-1+b)-Gamma(-1+b,z))+E^z*z^(2-b)*(Gamma(-1+b)-Gamma(\n"
						+ "-1+b,z)))");
		check("Hypergeometric1F1(-2,-1,0)", //
				"1");
		check("Hypergeometric1F1(-2,-1,z)", //
				"ComplexInfinity");
		check("Hypergeometric1F1(-2,-7,z)", //
				"1+2/7*z+z^2/42");
		check("Hypergeometric1F1(-2,0,z)", //
				"ComplexInfinity");
		check("Hypergeometric1F1(a,a,z)", //
				"E^z");
		check("Hypergeometric1F1(0,1,z)", //
				"1");
		check("Hypergeometric1F1(a,1,z)", //
				"LaguerreL(-a,z)");
		check("Hypergeometric1F1(3,1,z)", //
				"LaguerreL(-3,z)");
		check("Hypergeometric1F1(-1,b,z)", //
				"1-z/b");
		check("Hypergeometric1F1(-1,2,3.0)", //
				"-0.5");
		check("Hypergeometric1F1(1,2,3.0)", //
				"6.36185");
		checkNumeric("Hypergeometric1F1(1,{2,3,4},5.0)", //
				"{29.4826318205153,11.393052728206118,6.235831636923671}");
	}

	public void testHypergeometric2F1() {
		// Hypergeometric2F1(1 - n, -n, 2, 1) == CatalanNumber(n)
		check("Hypergeometric2F1(-3, -4, 2, 1)==CatalanNumber(4)", //
				"True");

		check("Hypergeometric2F1(1,2,3/2,x^2/9)", //
				"3/2*(1/3*Sqrt(1-x^2/9)*Sqrt(x^2)+ArcSin(Sqrt(x^2)/3))/((1-x^2/9)^(3/2)*Sqrt(x^2))");

		check("Hypergeometric2F1(-2,b,c,1)", //
				"(-b+b^2+c-2*b*c+c^2)/(c*(1+c))");

		check("Hypergeometric2F1(0.5,0.333,0.666,0.5)", //
				"1.18566");
		checkNumeric("Hypergeometric2F1(0.5,Sin(Pi),0.666,-0.5)", //
				"1.0");
		checkNumeric("Hypergeometric2F1(0.5,0.333,0.666,-0.5)", //
				"0.9026782488379839");
		checkNumeric("Hypergeometric2F1(0.5,0.333,0.666,0.75)", //
				"1.397573218428824");
		checkNumeric("Hypergeometric2F1(0.5,0.333,0.666,-0.75)", //
				"0.8677508558430699");

		// print message: Hypergeometric2F1: No convergence after 50000
		// iterations! Limiting value: 9.789346
		check("Hypergeometric2F1(0.5,0.333,0.666,1)", //
				"Hypergeometric2F1(0.333,0.5,0.666,1.0)");
	}

	public void testI() {
		check("(3+I)*(3-I)", //
				"10");
	}

	public void testIdentity() {
		check("Composition(Through, {Identity, Sqrt}) /@ {0, 1.0, 2.0, 3.0, 4.0}", //
				"{{0.0,0.0},{1.0,1.0},{2.0,1.41421},{3.0,1.73205},{4.0,2.0}}");
		check("Identity'", //
				"1&");
		check("D(Identity(Sin(x)),x)", //
				"Cos(x)");
		check("Composition(f,g,Identity,h,Identity,g)", //
				"Composition(f,g,h,g)");
		check("Identity(0)", //
				"0");
	}

	public void testIf() {
		check("If(FreeQ(a+b*x,x),1,a+b*x)", //
				"a+b*x");
		check("If(1 == k, itstrue, itsfalse)", //
				"If(1==k,itstrue,itsfalse)");
		check("If(1<2, a, b)", //
				"a");
		check("If(1<2, a)", //
				"a");
		check("If(False, a) //FullForm", //
				"Null");
		check("If(a>b,true)", //
				"If(a>b,True)");
		check("If(a>b,1,0)", //
				"If(a>b,1,0)");
		check("If(a>b,1,0, Indeterminate)", //
				"Indeterminate");
		check("If(TrueQ(a>b),1,0)", //
				"0");
		check("If(TrueQ(a>b),1) // FullForm", //
				"Null");
	}

	public void testIm() {
		check("Im(I*x+y)", //
				"Im(y)+Re(x)");
		check("Im(I*x)", //
				"Re(x)");
		check("Im(I*Pi/4)", //
				"Pi/4");
		check("Im(E^(I*Pi/4))", //
				"1/Sqrt(2)");
		check("Im(E^(I*3))", //
				"Sin(3)");
		check("Im(Sin(42)*Cos(43))", //
				"0");
		check("Im(Sin(42))", //
				"0");
		check("Im(I*9*Sin(5))", //
				"9*Sin(5)");
		check("Im(3*(-2)^(3/4))", //
				"3*2^(1/4)");
		check("Im(3*2^(3/4))", //
				"0");

		check("Im(0)", //
				"0");
		check("Im(I)", //
				"1");
		check("Im(Indeterminate)", //
				"Indeterminate");
		check("Im(Infinity)", //
				"0");
		check("Im(-Infinity)", //
				"0");
		check("Im(ComplexInfinity)", //
				"Indeterminate");
	}

	public void testImplies() {
		check("Implies(!a,!a)", //
				"True");
		check("Implies(False, a)", //
				"True");
		check("Implies(True, a)", //
				"a");
		check("Implies(a,Implies(b,Implies(True,c)))", //
				"Implies(a,Implies(b,c))");

		check("Implies(p,q)", //
				"Implies(p,q)");

		check("Implies(a,True)", //
				"True");
		check("Implies(a,False)", //
				"!a");
		check("Implies(a,a)", //
				"True");
	}

	public void testImportExport() {
		if (Config.FILESYSTEM_ENABLED) {
			// check("Export(\"c:\\\\temp\\\\testgraph.csv\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3
			// \\[DirectedEdge] 1}))", //
			// "c:\\temp\\testgraph.csv");
			check("Export(\"c:\\\\temp\\\\dotgraph.dot\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
					"c:\\temp\\dotgraph.dot");
			check("Import(\"c:\\\\temp\\\\dotgraph.dot\")", //
					"Graph[([1, 2, 3], [(1,2), (2,3), (3,1)])]");

			check("Export(\"c:\\\\temp\\\\dotgraph.graphml\",Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}),\"GraphML\")", //
					"c:\\temp\\dotgraph.graphml");
			check("Import(\"c:\\\\temp\\\\dotgraph.graphml\", \"GraphML\")", //
					"Graph[([1, 2, 3], [(1,2), (2,3), (3,1)])]");

			check("Export(\"c:\\\\temp\\\\out.wxf\", {{5.7, 4.3}, {-1.2, 7.8}, {a, f(x)}}, \"WXF\")", //
					"c:\\temp\\out.wxf");
			check("Import(\"c:\\\\temp\\\\out.wxf\", \"WXF\")", //
					"{{5.7,4.3},{-1.2,7.8},{a,f(x)}}");

			check("Export(\"c:\\\\temp\\\\out.dat\", {{5.7, 4.3}, {-1.2, 7.8}, {a, f(x)}}, \"Table\")", //
					"c:\\temp\\out.dat");
			check("Import(\"c:\\\\temp\\\\out.dat\", \"Table\")", //
					"{{5.7,4.3},{-1.2,7.8},{a,f(x)}}");
			check("Export(\"c:\\\\temp\\\\data.txt\", Integrate(sin(x)^10,x), \"Data\")", //
					"c:\\temp\\data.txt");
			check("Import(\"c:\\\\temp\\\\data.txt\", \"String\")", //
					"63/256*x-63/256*Cos(x)*Sin(x)-21/128*Cos(x)*Sin(x)^3-21/160*Cos(x)*Sin(x)^5-9/80*Cos(x)*Sin(x)^\n"//
							+ "7-1/10*Cos(x)*Sin(x)^9");
		}
	}

	public void testIncrement() {
		check("a = 2", //
				"2");
		check("a++", //
				"2");
		check("a", //
				"3");
		check("++++a+++++2//Hold//FullForm", //
				"Hold(Plus(PreIncrement(PreIncrement(Increment(Increment(a)))), 2))");
	}

	public void testIndeterminate() {
		check("Tan(Indeterminate)", //
				"Indeterminate");
		check("{And(True, Indeterminate), And(False, Indeterminate)}", //
				"{Indeterminate,False}");
		check("Indeterminate==Indeterminate", //
				"Indeterminate==Indeterminate");
		check("Indeterminate===Indeterminate", //
				"True");
		check("{Re(Indeterminate), Im(Indeterminate)}", //
				"{Indeterminate,Indeterminate}");
		check("NumberQ(Indeterminate)", //
				"False");
		check("{1,2,3}*Indeterminate", //
				"{Indeterminate,Indeterminate,Indeterminate}");
		check("{1,2,3}+Indeterminate", //
				"{Indeterminate,Indeterminate,Indeterminate}");

		check("Integrate(Indeterminate,x)", //
				"Indeterminate");
		check("D(Indeterminate,x)", //
				"Indeterminate");
		check("DirectedInfinity(Indeterminate)", //
				"ComplexInfinity");
	}

	public void testInexactNumberQ() {
		check("InexactNumberQ(a)", //
				"False");
		check("InexactNumberQ(3.0)", //
				"True");
		check("InexactNumberQ(2/3)", //
				"False");
		check("InexactNumberQ(4.0+I)", //
				"True");
	}

	public void testInfinity() {
		check("1 / Infinity", //
				"0");
		check("Infinity + 100", //
				"Infinity");
		check("Sum(1/x^2, {x, 1, Infinity})", //
				"Pi^2/6");
		check("FullForm(Infinity)", //
				"DirectedInfinity(1)");
		check("(2 + 3.5*I) / Infinity", //
				"0.0");
		check("Infinity + Infinity", //
				"Infinity");
		check("Infinity / Infinity", //
				"Indeterminate");
	}

	public void testInformation() {
		// print documentation in console
		check("Information(Sin)", //
				"");
	}

	public void testInner() {
		check("Inner(Times, {a, b}, {x, y}, Plus)", //
				"a*x+b*y");
		check("Inner(Times, {a, b}, {x, y})", //
				"a*x+b*y");
		check("Inner(Power, {a, b, c}, {x, y, z}, Times)", //
				"a^x*b^y*c^z");
		check("Inner(f, {a, b}, {x, y}, g)", //
				"g(f(a,x),f(b,y))");
		check("Inner(f, {{a, b}, {c, d}}, {x, y}, g)", //
				"{g(f(a,x),f(b,y)),g(f(c,x),f(d,y))}");
		check("Inner(f, {{a, b}, {c, d}}, {{u, v}, {w, x}}, g)", //
				"{{g(f(a,u),f(b,w)),g(f(a,v),f(b,x))},{g(f(c,u),f(d,w)),g(f(c,v),f(d,x))}}");
		check("Inner(f, {x, y}, {{a, b}, {c, d}}, g)", //
				"{g(f(x,a),f(y,c)),g(f(x,b),f(y,d))}");
		check("Inner(s, f(1), f(2), t)", //
				"t(s(1,2))");
		check("Inner(And, {{False, False}, {False, True}}, {{True, False}, {True, True}}, Or)", //
				"{{False,False},{True,True}}");
		check("Inner(f, {{{a, b}}, {{x, y}}}, {{1}, {2}}, g)", //
				"{{{g(f(a,1),f(b,2))}},{{g(f(x,1),f(y,2))}}}");
	}

	public void testInputForm() {
		check("InputForm(Sin(0))", //
				"0");
	}

	public void testInsert() {
		check("Insert({a, b, c, d, e}, x, 3)", //
				"{a,b,x,c,d,e}");
		check("Insert({a, b, c, d, e}, x, -2)", //
				"{a,b,c,d,x,e}");

		// test operator form
		check("Insert(e, pos)", //
				"Insert(e,pos)");
		check("Insert(e, pos)[x]", //
				"Insert(e,pos)[x]");
		check("Insert(2, -1)[{a, b, c, d, e}]", //
				"{a,b,c,d,e,2}");
		check("Insert(2, -2)[{a, b, c, d, e}]", //
				"{a,b,c,d,2,e}");
	}

	public void testInteger() {
		check("Head(5)", //
				"Integer");
		check("{a, b} = {2^10000, 2^10000 + 1}; {a == b, a < b, a <= b}", //
				"{False,True,True}");
	}

	public void testIntegerDigits() {
		// https://oeis.org/A018900 - Sum of two distinct powers of 2
		check("Select(Range(1000), (Count(IntegerDigits(#, 2), 1)==2)&)", //
				"{3,5,6,9,10,12,17,18,20,24,33,34,36,40,48,65,66,68,72,80,96,129,130,132,136,144,\n"
						+ "160,192,257,258,260,264,272,288,320,384,513,514,516,520,528,544,576,640,768}");
		check("IntegerDigits(0)", //
				"{0}");
		check("IntegerDigits(123)", //
				"{1,2,3}");
		check("IntegerDigits(-123)", //
				"{1,2,3}");
		check("IntegerDigits(123, 2)", //
				"{1,1,1,1,0,1,1}");
		check("IntegerDigits(123, 2)", //
				"{1,1,1,1,0,1,1}");
		check("IntegerDigits(123, 2, 10)", //
				"{0,0,0,1,1,1,1,0,1,1}");
		check("IntegerDigits({123,456,789}, 2, 10)", //
				"{{0,0,0,1,1,1,1,0,1,1},{0,1,1,1,0,0,1,0,0,0},{1,1,0,0,0,1,0,1,0,1}}");
		check("IntegerDigits(123, -2)", //
				"IntegerDigits(123,-2)");
		check("IntegerDigits(58127, 2)", //
				"{1,1,1,0,0,0,1,1,0,0,0,0,1,1,1,1}");
		check("IntegerDigits(58127, 16)", //
				"{14,3,0,15}");

		check("IntegerDigits({6,7,2}, 2)", //
				"{{1,1,0},{1,1,1},{1,0}}");
		check("IntegerDigits(7, {2,3,4})", //
				"{{1,1,1},{2,1},{1,3}}");
		check("IntegerDigits(Range(0,7), 2)", //
				"{{0},{1},{1,0},{1,1},{1,0,0},{1,0,1},{1,1,0},{1,1,1}}");
		check("IntegerDigits(Range(0,7), 2, 3)", //
				"{{0,0,0},{0,0,1},{0,1,0},{0,1,1},{1,0,0},{1,0,1},{1,1,0},{1,1,1}}");
	}

	public void testIntegerExponent() {
		check("IntegerExponent(1230000)", //
				"4");
		check("IntegerExponent(2^10+2^7, 2)", //
				"7");
		check("IntegerExponent(0, 2)", //
				"Infinity");
		check("IntegerExponent(100,100)", //
				"1");
		check("Table(IntegerExponent(n!), {n, 50})", //
				"{0,0,0,0,1,1,1,1,1,2,2,2,2,2,3,3,3,3,3,4,4,4,4,4,6,6,6,6,6,7,7,7,7,7,8,8,8,8,8,9,\n"
						+ "9,9,9,9,10,10,10,10,10,12}");
		check("IntegerExponent(2524,2)", //
				"2");
		check("IntegerExponent(-510000)", //
				"4");

		check("IntegerExponent(16, 2)", //
				"4");
		check("IntegerExponent(-510000)", //
				"4");
		check("IntegerExponent(10, b)", //
				"IntegerExponent(10,b)");
	}

	public void testIntegerLength() {
		check("IntegerLength(123456)", //
				"6");
		check("IntegerLength(10^10000)", //
				"10001");
		check("IntegerLength(-10^1000)", //
				"1001");
		check("IntegerLength(8, 2)", //
				"4");
		check("IntegerLength /@ (10 ^ Range(100)) == Range(2, 101)", //
				"True");
		check("IntegerLength(3, -2)", //
				"IntegerLength(3,-2)");
		check("IntegerLength(0)", //
				"1");
		check("IntegerLength /@ (10 ^ Range(100) - 1) == Range(1, 100)", //
				"True");
	}

	public void testIntegerPart() {
		check("IntegerPart(Pi^20)", //
				"8769956796");
		check("IntegerPart(2^128-1)", //
				"340282366920938463463374607431768211455");
		check("IntegerPart(Infinity)", //
				"Infinity");
		check("IntegerPart(-Infinity)", //
				"-Infinity");
		check("IntegerPart(I*Infinity)", //
				"I*Infinity");
		check("IntegerPart(-I*Infinity)", //
				"-I*Infinity");

		check("IntegerPart(Pi)", //
				"3");
		check("IntegerPart(-Pi)", //
				"-3");
		check("IntegerPart(IntegerPart(Pi))", //
				"3");

		check("IntegerPart(-9/4)", //
				"-2");
		check("IntegerPart(2.4)", //
				"2");
		check("IntegerPart(-2.4)", //
				"-2");
		check("IntegerPart({-2.4, -2.5, -3.0})", //
				"{-2,-2,-3}");
	}

	public void testIntegerPartitions() {
		check("IntegerPartitions(4)", //
				"{{4},{3,1},{2,2},{2,1,1},{1,1,1,1}}");
		check("IntegerPartitions(6)", //
				"{{6},{5,1},{4,2},{4,1,1},{3,3},{3,2,1},{3,1,1,1},{2,2,2},{2,2,1,1},{2,1,1,1,1},{\n" //
						+ "1,1,1,1,1,1}}");
		check("IntegerPartitions(6, {3,4})", //
				"{{4,1,1},{3,2,1},{3,1,1,1},{2,2,2},{2,2,1,1}}");
		check("IntegerPartitions(10,2)", //
				"{{10},{9,1},{8,2},{7,3},{6,4},{5,5}}");
		check("IntegerPartitions(10,{2})", //
				"{{9,1},{8,2},{7,3},{6,4},{5,5}}");
		check("IntegerPartitions(0)", //
				"{{}}");
		check("IntegerPartitions(1)", //
				"{{1}}");
		check("IntegerPartitions(-1)", //
				"{}");
		check("IntegerPartitions(.5)", //
				"IntegerPartitions(0.5)");
		check("IntegerPartitions(1/2)", //
				"{}");
	}

	public void testIntegrate() {
		// see github #155
		check("Integrate(x^2,{x,-2,2})", //
				"16/3");
		check("Integrate((x-1)^2,{x,-2,2})", //
				"28/3");
		check("Integrate(x^3,{x,-2,2})", //
				"0");
		check("Integrate((x-1)^3,{x,-2,2})", //
				"-20");

		// see github #153
		check("Integrate(E^(-x^2.0),{x,-1/2,1/2})", //
				"Sqrt(Pi)*Erf(1/2)");
		check("Integrate(E^(-x^2.0),{x,-1/2,1/2}) //N", //
				"0.922562");
		check("N(Integrate(E^(-x^2),{x,-1/2,1/2}))", //
				"0.922562");

		check("Integrate(Log(x^3)/E^(2+x),{x,1,2})", //
				"(3*ExpIntegralEi(-2))/E^2+(-3*ExpIntegralEi(-1))/E^2-Log(8)/E^4");
		// check("Limit(1/9*x*(9-x^2)^(3/2)*Hypergeometric2F1(1,2,3/2,x^2/9),x->3)", //
		// "");

		// see github #128
		// check("Apart(a/((8/3*a*b^(2/3)-16/9*b)^2*(4/3*a*b^(2/3)+16/9*b)))", //
		// "a/(a^3-4/3*a*b^(2/3)+16/27*b)");
		check("Integrate(a/(a^3-4/3*a*b^(2/3)+16/27*b),a)", //
				"-1/(3*a-2*b^(1/3))+Log(3*a-2*b^(1/3))/(3*b^(1/3))-Log(3*a+4*b^(1/3))/(3*b^(1/3))");
		check("Simplify(D(-1/(3*a-2*b^(1/3))+Log(3*a-2*b^(1/3))/(3*b^(1/3))-Log(3*a+4*b^(1/3))/(3*b^(1/3)),a))", //
				"(27*a)/(27*a^3-36*a*b^(2/3)+16*b)");// "3/(3*a-2*b^(1/3))^2+1/(3*a*b^(1/3)-2*b^(2/3))-1/(3*a*b^(1/3)+4*b^(2/3))");
		// expensive JUnit test
		// check("Integrate(((a+b)/(a^(1/3)+b^(1/3))-(a*b)^(1/3))/(a^(1/3)-b^(1/3))^2,a)", //
		// "a+3/2*a^(2/3)*b^(1/3)+6*a^(1/3)*b^(2/3)+(-3*b^(4/3))/(a^(1/3)-b^(1/3))-3/2*a^(1/\n" +
		// "3)*(a*b)^(1/3)-6*b^(1/3)*(a*b)^(1/3)+(3*b*(a*b)^(1/3))/(a^(1/3)*(a^(1/3)-b^(1/3)))+\n" +
		// "3*b*Log(a^(1/3)-b^(1/3))+(-9*b^(2/3)*(a*b)^(1/3)*Log(a^(1/3)-b^(1/3)))/a^(1/3)-3*b*Log(a^(\n" +
		// "1/3)+b^(1/3))+3*b*Log(a-a^(2/3)*b^(1/3)-a^(1/3)*b^(2/3)+b)");

		// see github #120
		check("Integrate(Ln(x)^2, {x,0,2})", //
				"4-4*Log(2)+2*Log(2)^2");
		check("Integrate(Ln(x)^2, {x,0,2}) // N", //
				"2.18832");
		check("NIntegrate(Ln(x)^2, {x,0,2}) // N", //
				"2.1857");

		// see github #116
		// should give (2*ArcTan((1 + 2*x)/Sqrt(3)))/Sqrt(3)
		check(" Integrate(1/(x^2+x+1),x) ", //
				"(2*ArcTan((1+2*x)/Sqrt(3)))/Sqrt(3)");

		// see github #109
		check("Int(1/Sqrt(9*x^4+1),{x,0,999})//N", //
				"1.07012");
		check("Integrate(x^n,x)", //
				"x^(1+n)/(1+n)");
		check("Integrate(x^n,{x,0,1})", //
				"(1-ConditionalExpression(0,n>-1))/(1+n)");
		// https://github.com/RuleBasedIntegration/Rubi/issues/12
		check("Integrate(Tan(Log(x)),x)", //
				"-I*x+I*2*x*Hypergeometric2F1(1,-I*1/2,1-I*1/2,-x^(I*2))");

		check("Integrate(5*E^(3*x),{x,2,a})", //
				"1/3*(-5*E^6+5*E^(3*a))");
		check("Integrate(Sqrt(9-x^2),x)", //
				"1/2*x*Sqrt(9-x^2)+9/2*ArcSin(x/3)");
		check("Integrate(Sqrt(9-x^2),{x,0,3})", //
				"9/4*Pi");
		check("Integrate({Sin(x),Cos(x)},x)", //
				"{-Cos(x),Sin(x)}");
		check("Integrate({Sin(x),Cos(x)},{x,a,b})", //
				"{Cos(a)-Cos(b),-Sin(a)+Sin(b)}");
		check("Integrate(2*x,x)", "x^2");
		check("Integrate(Tan(x) ^ 5, x)", //
				"-Log(Cos(x))-Tan(x)^2/2+Tan(x)^4/4");
		check("Integrate(x*Sin(x),{x,1.0,2*Pi})", //
				"-6.58435");

		// check("Integrate(x/(1+x+x^7),x)", "");
		check("Integrate(1/y(x)^2,y(x))", //
				"-1/y(x)");
		check("Integrate(f(x,y),x)", //
				"Integrate(f(x,y),x)");
		check("Integrate(f(x,x),x)", //
				"Integrate(f(x,x),x)");
	}

	public void testInterpolatingFunction() {
		check("ipf=InterpolatingFunction({{0,17},{1,3},{2,5},{3,4},{4,3},{5,0},{6,23}}); ipf(19/4) ", //
				"-19/32");
		check("ipf=InterpolatingFunction({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}}); ipf(5/2) ", //
				"59/16");
		check("InterpolatingFunction({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}})", //
				"InterpolatingFunction({{0,0},{1,1},{2,3},{3,4},{4,3},{5,0}})");
		check("ipf=InterpolatingFunction({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}});{ipf(2.5),ipf(3.0),ipf(3.5)}", //
				"{3.6875,4.0,3.75}");
		check("InterpolatingFunction({{0, 0}, {1, 1}, {2, 3}, {3, 4}, {4, 3}, {5, 0}})", //
				"InterpolatingFunction({{0,0},{1,1},{2,3},{3,4},{4,3},{5,0}})");
	}

	public void testInterpolatingPolynomial() {
		check("InterpolatingPolynomial({1,4},x)", //
				"1+3*(-1+x)");
		check("InterpolatingPolynomial({1,4,9},x)", //
				"1+(-1+x)*(1+x)");
		check("InterpolatingPolynomial({1,4,9,16},x)", //
				"1+(-1+x)*(1+x)");
		check("InterpolatingPolynomial({1,2},x)", //
				"x");

		check("InterpolatingPolynomial({{-1, 4}, {0, 2}, {1, 6}}, x)", //
				"4+(1+x)*(-2+3*x)");
		check("Expand((3*x-2)*(x+1)+4)", //
				"2+x+3*x^2");

		check("InterpolatingPolynomial({{0, 1}, {a, 0}, {b, 0}, {c, 0}}, x)", //
				"1+x*(-1/a+(-a+x)*(1/(a*b)-(-b+x)/(a*b*c)))");

		check("InterpolatingPolynomial({1,2,3,5,8,5},x)", //
				"1+(1+(1/6+(-1/24+1/20*(5-x))*(-4+x))*(-3+x)*(-2+x))*(-1+x)");

		check("((x-1)*((x-3)*(x-2)*((x-4)*(-1/20*x+5/24)+1/6)+1)+1) /. x -> Range(6)", //
				"{1,2,3,5,8,5}");
	}

	public void testInequality() {
		// check("Inequality(-1,Less,0,Lest,1)", //
		// "Inequality(0,Lest,1)");

		check("Inequality(-1,Less,0,LessEqual,a,Less,3,Less,4,Less,5,Less,b,Less,10,Less,11)", //
				"0<=a<3<5<b<10");

		check("Inequality(c,Less,0,Less,a)", //
				"c<0<a");
		check("Inequality(-Pi,Less,0,LessEqual,Pi)", //
				"True");
		check("Inequality(c,Less,0)", //
				"c<0");

		check("Inequality(c,Less)", //
				"Inequality(c,Less)");
		check("Inequality(c)", //
				"True");
		check("Inequality(False)", //
				"True");
		check("Inequality( )", //
				"Inequality()");

		check("Inequality(-1,Less,a,Less,0,Less,1)", //
				"-1<a<0");
		check("Inequality(-1,Less,0,Less,a,Less,1)", //
				"0<a<1");
		check("Inequality(-1,Less,a,Less,-2)", //
				"False");
		check("Inequality(-Pi,Less,0,GreaterEqual,a)", //
				"0>=a");
		check("Inequality(0,Less,a,Greater,0,Greater,k)", //
				"0<a&&a>0>k");
		check("Inequality(0,Greater,a,Less,0)", //
				"0>a&&a<0");
		check("Inequality(0,Less,a,Less,1)", //
				"0<a<1");
		check("0<a && a<1", //
				"0<a&&a<1");
		check("Inequality(a,Less,b,LessEqual,c)", //
				"a<b<=c");

		check("Inequality(a,Less,b,LessEqual,c,Equal,d,GreaterEqual,e,Greater,f)", //
				"a<b<=c==d&&d>=e>f");
		check("Inequality(a,Greater,b,GreaterEqual,c,Equal,d,LessEqual,e,Less,f)", //
				"a>b>=c==d&&d<=e<f");
		check("Inequality(a,Greater,b,GreaterEqual,c,Equal,d,GreaterEqual,e,Less,f)", //
				"a>b>=c==d>=e&&e<f");
		check("Inequality(a,Greater,1,GreaterEqual,c,Equal,d,GreaterEqual,5,Less,f)", //
				"False");
		check("a<1<2<3<4<=b", //
				"a<1<4<=b");
		check("a<1<2<3<4<=b<5", //
				"a<1<4<=b<5");
		check("Inequality(-1,Less,0,Lest,1)", //
				"Inequality(-1,Less,0,lest,1)");
		check("Inequality(-1,Lest,0,Less,1)", //
				"Inequality(-1,lest,0,Less,1)");
	}

	public void testIntersectingQ() {
		check("IntersectingQ(f(b, a, b,d), f(c,c,c,d))", //
				"True");
		check("IntersectingQ(f(b, a, b,d), f(e,g))", //
				"False");

		// same as ContainsAny
		check("IntersectingQ({b, a, b}, {a, b, c})", //
				"True");
		check("IntersectingQ({b, a, b,d}, {c,c,c,d})", //
				"True");
		check("IntersectingQ({d,f,e}, {a, b, c})", //
				"False");
		check("IntersectingQ({ }, {a, b, c})", //
				"False");

		check("IntersectingQ(1, {1,2,3})", //
				"IntersectingQ(1,{1,2,3})");
		check("IntersectingQ({1,2,3}, 4)", //
				"IntersectingQ({1,2,3},4)");

		check("IntersectingQ({1.0,2.0}, {1,2,3})", //
				"False");
		check("IntersectingQ({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
				"True");
	}

	public void testInterrupt() {
		check("Print(test1); Interrupt(); Print(test2)", //
				"$Aborted");
	}

	public void testIntersection() {
		check("Intersection({a,a,b,c})", //
				"{a,b,c}");
		check("Intersection({a,a,b,c},{b,a})", //
				"{a,b}");
		check(" Intersection({a, b, c}, {a}, {b})", //
				"{}");
		check(" Intersection({}, {a, b, c}, {a,b}, {b,c})", //
				"{}");
	}

	public void testInterval() {
		// https://en.wikipedia.org/wiki/Interval_arithmetic
		// check("Sin(Interval({2, 8}))", "Interval({-1,1})");

		// check("Sin(Interval({3, 4}))", "Interval({Sin(4),Sin(3)})");
		// check("Sin(Interval({3, 5}))", "Interval({-1,Sin(3)})");
		// check("Sin(Interval({4, 7}))", "Interval({-1,Sin(7)})");
		// check("Sin(Interval({2, 7}))", "Interval({-1,Sin(2)})");
		// check("Sin(Interval({2, 8}))", "Interval({-1,1})");
		check("Interval({3,-1})", //
				"Interval({-1,3})");
		check("Interval({-1,1})*Interval({-1,1})", //
				"Interval({-1,1})");
		check("Interval({-1,1})/Infinity", //
				"0");
		check("Interval({1,1})", //
				"Interval({1,1})");

		check("Interval({1.5, 6}) * Interval({0.1, 2.7})", //
				"Interval({0.15,16.2})");
		check("Interval({1, 6}) * Interval({0, 2})", //
				"Interval({0,12})");
		check("Interval({1, 6}) + Interval({0, 2})", //
				"Interval({1,8})");
		check("Interval({-2, 5})^2", //
				"Interval({0,25})");
		check("Interval({-7, 5})^2", //
				"Interval({0,49})");
		check("Interval({-2, 5})^(-2)", //
				"1/Interval({0,25})");
		check("Interval({2, 5})^2", //
				"Interval({4,25})");
		check("Interval({-2, 5})^3", //
				"Interval({-8,125})");
		check("Interval({-10, -5})^2", //
				"Interval({25,100})");
		check("Pi>3", //
				"True");
		check("3>Pi", //
				"False");
		check("Pi<3", //
				"False");
		check("3<Pi", //
				"True");
		check("Pi>=3", //
				"True");
		check("3>=Pi", //
				"False");
		check("Pi<=3", //
				"False");
		check("3<=Pi", //
				"True");

		check("Interval({5,8})>2", //
				"True");
		check("Interval({3,4})>Pi", //
				"Interval({3,4})>Pi");
		check("Interval({1,2})>Pi", //
				"False");
		check("Interval({5,8})<2", //
				"False");
		check("Interval({3,4})<Pi", //
				"Interval({3,4})<Pi");
		check("Interval({1,2})<Pi", //
				"True");
		check("Interval({5,8})>=2", //
				"True");
		check("Interval({3,4})>=Pi", //
				"Interval({3,4})>=Pi");
		check("Interval({1,2})>=Pi", //
				"False");
		check("Interval({5,8})<=2", //
				"False");
		check("Interval({3,4})<=Pi", //
				"Interval({3,4})<=Pi");
		check("Interval({1,2})<=Pi", //
				"True");

		check("Interval({5,8})>Interval({1,2})", //
				"True");
		check("Interval({3,4})>Interval({Pi,5})", //
				"Interval({3,4})>Interval({Pi,5})");
		check("Interval({1,2})>Interval({Pi,5})", //
				"False");
		check("Interval({5,8})<Interval({1,2})", //
				"False");
		check("Interval({3,4})<Interval({Pi,5})", //
				"Interval({3,4})<Interval({Pi,5})");
		check("Interval({1,2})<Interval({Pi,5})", //
				"True");

		check("Limit(Sin(x),x->Infinity)", //
				"Interval({-1,1})");
		check("Limit(Sin(x),x->-Infinity)", //
				"Interval({-1,1})");
		check("Limit(Sin(1/x),x->0)", //
				"Indeterminate");
		check("Max(Interval({2,4}))", //
				"4");
		check("Min(Interval({2,4}))", //
				"2");
		check("u=Interval({-1,1});u+u^2", //
				"Interval({-1,2})");
	}

	public void testInverse() {
		check("Inverse({{a,b,c}, {d,e,f}, {x,y,z}})", //
				"{{(f*y-e*z)/(c*e*x-b*f*x-c*d*y+a*f*y+b*d*z-a*e*z),(-c*y+b*z)/(c*e*x-b*f*x-c*d*y+a*f*y+b*d*z-a*e*z),(c*e-b*f)/(c*e*x-b*f*x-c*d*y+a*f*y+b*d*z-a*e*z)},\n"
						+ //
						" {(f*x-d*z)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z),(-c*x+a*z)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z),(c*d-a*f)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z)},\n"
						+ //
						" {(-e*x+d*y)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z),(b*x-a*y)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z),(-b*d+a*e)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z)}}");
		check("Inverse({{1, 2, 0}, {2, 3, 0}, {3, 4, 1}})", //
				"{{-3,2,0},\n" + " {2,-1,0},\n" + " {1,-2,1}}");
		check("Inverse({{1, 0}, {0, 0}})", //
				"Inverse({{1,0},{0,0}})");
		check("Inverse({{1, 0, 0}, {0, Sqrt(3)/2, 1/2}, {0,-1 / 2, Sqrt(3)/2}})", //
				"{{1,0,0},\n" + " {0,Sqrt(3)/2,-1/2},\n" + " {0,1/2,Sqrt(3)/2}}");
		check("Inverse({{u, v}, {v, u}})", //
				"{{u/(u^2-v^2),-v/(u^2-v^2)},\n" + //
						" {-v/(u^2-v^2),u/(u^2-v^2)}}");
		check("Inverse({{1.4, 2}, {3, -6.7}})", //
				"{{0.435631,0.130039},\n" + //
						" {0.195059,-0.0910273}}");
		check("Inverse(HilbertMatrix(5))", //
				"{{25,-300,1050,-1400,630},\n" + //
						" {-300,4800,-18900,26880,-12600},\n" + //
						" {1050,-18900,79380,-117600,56700},\n" + //
						" {-1400,26880,-117600,179200,-88200},\n" + //
						" {630,-12600,56700,-88200,44100}}");
		check("Inverse({{u, v}, {v, u}}).{{u, v}, {v, u}}  // Simplify", //
				"{{1,0},{0,1}}");
		check("Inverse({{1,2}, {1,2}})", //
				"Inverse({{1,2},{1,2}})");
	}

	public void testInverseBetaRegularized() {
		check("InverseBetaRegularized(10, 12.0, 0.5)", //
				"InverseBetaRegularized(10.0,12.0,0.5)");
		check("InverseBetaRegularized(0.1, 12.0, 0.5)", //
				"0.891295");
		check("InverseBetaRegularized(0.8, 1, 2)", //
				"0.552786");
		check("Table(InverseBetaRegularized(s, 2, 3), {s, 0, 1,0.1})", //
				"{0.0,0.142559,0.212317,0.272384,0.329167,0.385728,0.4445,0.508405,0.582454,0.679539,1.0}");
		check("InverseBetaRegularized(0,42,b)", //
				"0");
		check("InverseBetaRegularized(1,47.11,b)", //
				"1");
		check("InverseBetaRegularized(z,0,a,b)", //
				"z");
		check("InverseBetaRegularized(0,z,a,b)", //
				"InverseBetaRegularized(z,a,b)");
	}

	public void testInverseCDF() {
		// https://github.com/axkr/symja_android_library/issues/147
		check("InverseCDF(StudentTDistribution(24), 0.95)", //
				"1.71088");

		check("InverseCDF(BetaDistribution(2,3), 0.1)", //
				"0.142559");
		check("InverseCDF(BetaDistribution(2,3), 0.9)", //
				"0.679539");
		check("InverseCDF(ChiSquareDistribution(3), 0.1)", //
				"0.584374");
		check("InverseCDF(ChiSquareDistribution(3), 0.9)", //
				"6.25139");
		check("InverseCDF(ExponentialDistribution(3), 0.1)", //
				"0.0351202");
		check("InverseCDF(ExponentialDistribution(3), 0.9)", //
				"0.767528");
		check("InverseCDF(FRatioDistribution(2,3), 0.1)", //
				"0.109149");
		check("InverseCDF(FRatioDistribution(2,3), 0.9)", //
				"5.46238");
		check("InverseCDF(GammaDistribution(2,3), 0.1)", //
				"1.59543");
		check("InverseCDF(GammaDistribution(2,3), 0.9)", //
				"11.66916");
		check("InverseCDF(GumbelDistribution(2,3), 0.1)", //
				"-4.7511");
		check("InverseCDF(GumbelDistribution(2,3), 0.9)", //
				"4.5021");
		check("InverseCDF(LogNormalDistribution(2,3), 0.1)", //
				"0.15808");
		check("InverseCDF(LogNormalDistribution(2,3), 0.9)", //
				"345.3833");
		check("InverseCDF(NakagamiDistribution(2,3), 0.1)", //
				"0.89315");
		check("InverseCDF(NakagamiDistribution(2,3), 0.9)", //
				"2.41549");
		check("InverseCDF(NormalDistribution(2,3), 0.1)", //
				"-1.84465");
		check("InverseCDF(NormalDistribution(2,3), 0.9)", //
				"5.84465");
		check("InverseCDF(UniformDistribution({0,1}), 0.1)", //
				"0.1");
		check("InverseCDF(UniformDistribution({0,1}), 0.9)", //
				"0.9");
		check("InverseCDF(WeibullDistribution(2,3), 0.1)", //
				"0.973779");
		check("InverseCDF(WeibullDistribution(2,3), 0.9)", //
				"4.55228");

		check("InverseCDF(GammaDistribution(a,b,g,d))", //
				"ConditionalExpression(Piecewise({{d+b*InverseGammaRegularized(a,0,#1)^(1/g),0<#1<\n"
						+ "1},{d,#1<=0}},Infinity),0<=#1<=1)&");
		check("InverseCDF(NormalDistribution(0,1))", //
				"ConditionalExpression(-Sqrt(2)*InverseErfc(2*#1),0<=#1<=1)&");
		check("InverseCDF(NormalDistribution( ))", //
				"ConditionalExpression(-Sqrt(2)*InverseErfc(2*#1),0<=#1<=1)&");
		check("InverseCDF(NormalDistribution( ), p)", //
				"ConditionalExpression(-Sqrt(2)*InverseErfc(2*p),0<=p<=1)");
		check("InverseCDF(NormalDistribution(n,m))", //
				"ConditionalExpression(n-Sqrt(2)*m*InverseErfc(2*#1),0<=#1<=1)&");
		check("InverseCDF(NormalDistribution(n,m), p)", //
				"ConditionalExpression(n-Sqrt(2)*m*InverseErfc(2*p),0<=p<=1)");

		check("InverseCDF(NormalDistribution(0, 1), {x, y})", //
				"{ConditionalExpression(-Sqrt(2)*InverseErfc(2*x),0<=x<=1),ConditionalExpression(-Sqrt(\n"
						+ "2)*InverseErfc(2*y),0<=y<=1)}");
		check("InverseCDF(StudentTDistribution(n))", //
				"ConditionalExpression(Piecewise({{-Sqrt(n)*Sqrt(-1+1/InverseBetaRegularized(2*#1,n/\n"
						+ "2,1/2)),0<#1<1/2},{0,#1==1/2},{Sqrt(n)*Sqrt(-1+1/InverseBetaRegularized(2*(1-#1),n/\n"
						+ "2,1/2)),1/2<#1<1},{-Infinity,#1<=0}},Infinity),0<=#1<=1)&");
	}

	public void testInverseErf() {
		check("InverseErf /@ {-1, 0, 1}", //
				"{-Infinity,0,Infinity}");
		checkNumeric("InverseErf /@ {0.9, 1.0, 1.1}", //
				"{1.1630871536766743,Infinity,InverseErf(1.1)}");
		check("InverseErf(1)", //
				"Infinity");
		check("InverseErf(-1)", //
				"-Infinity");
		checkNumeric("InverseErf(0.6)", //
				"0.5951160814499948");
		checkNumeric("Sqrt(2)*InverseErf(0.99)", //
				"2.5758293035489004");
		checkNumeric("InverseErf(1/{2., 3., 4., 5.})", //
				"{0.47693627620446977,0.3045701941739856,0.22531205501217808,0.17914345462129166}");
		checkNumeric("InverseErf(-1/{2., 3., 4., 5.})", //
				"{-0.47693627620446977,-0.3045701941739856,-0.22531205501217808,-0.17914345462129166}");
		checkNumeric("InverseErf({-2.,-3.,3.})", //
				"{InverseErf(-2.0),InverseErf(-3.0),InverseErf(3.0)}");
	}

	public void testInverseErfc() {
		check("InverseErfc /@ {0, 1, 2}", //
				"{Infinity,0,-Infinity}");
		check("InverseErfc(0)", //
				"Infinity");
		check("InverseErfc(1)", //
				"0");
		check("InverseErfc(2)", //
				"-Infinity");
		check("InverseErfc(0.6)", //
				"0.370807");
		checkNumeric("Sqrt(2)*InverseErfc(0.99)", //
				"0.012533469508069274");
		checkNumeric("InverseErfc(1/{2., 3., 4., 5.})", //
				"{0.47693627620446977,0.6840703496566226,0.8134198475976184,0.9061938024368233}");
		checkNumeric("InverseErfc(-1/{2., 3., 4., 5.})", //
				"{InverseErfc(-0.5),InverseErfc(-0.3333333333333333),InverseErfc(-0.25),InverseErfc(-0.2)}");
	}

	public void testInverseFourier() {
		check("InverseFourier({2.82843+I*9.19239,-1.41421+I*(-6.36396)})", //
				"{1.0+I*2.0,3.0+I*11.0}");
		check("InverseFourier({1.5,0.5+I*1.0,-0.5,0.5+I*(-1.0)})", //
				"{1.0,2.0+I*2.22045*10^-16,0.0,I*(-2.22045*10^-16)}");

		// InverseFourier: Argument {1,2,0,0,7} is restricted to vectors with a length of power of 2.
		check("InverseFourier({1,2,0,0,7})", //
				"InverseFourier({1,2,0,0,7})");
	}

	public void testInverseFunction() {
		check("D(InverseFunction(Sin)[x],x)", //
				"1/Sqrt(1-x^2)");
		check("D(InverseFunction(f)[x],x)", //
				"1/f'(InverseFunction(f)[x])");
		check("D(InverseFunction(f(g))[x],x)", //
				"1/f(g)'[InverseFunction(f(g))[x]]");
		check("InverseFunction((a*#)/(c*#) &)", //
				"InverseFunction((a*#1)/(c*#1)&)");
		check("InverseFunction((a*#)/(c*# + d) &)", //
				"(#1*d)/(a*(1+(-#1*c)/a))&");
		check("InverseFunction((a*# + b)/(c*# + d) &)", //
				"(-b+#1*d)/(a-#1*c)&");
		check("InverseFunction((a * # + b)&)", //
				"(#1-b)/a&");
		check("InverseFunction(Abs)", //
				"-#1&");
		check("InverseFunction(Sin)", //
				"ArcSin");
	}

	public void testInverseGammaRegularized() {
		check("InverseGammaRegularized(a, Infinity, z)", //
				"InverseGammaRegularized(a,-z)");
		check("InverseGammaRegularized(42,0)", //
				"Infinity");
		check("InverseGammaRegularized(10,1)", //
				"0");
	}

	public void testInverseHaversine() {
		checkNumeric("InverseHaversine(0.5)", //
				"1.5707963267948968");
		checkNumeric("InverseHaversine(1 + 2.5 * I)", //
				"1.764589463349828+I*2.3309746530493127");
		check("InverseHaversine(1/4)", //
				"Pi/3");
		checkNumeric("InverseHaversine(0.7)", //
				"1.9823131728623846");
		// Java double machine precision
		// check("ArcSin(1.3038404810405)",
		// "1.5707963267948966+I*(-0.7610396837317912)");
		// apfloat/apcomplex precision

		// TODO use ExprParser#getReal() if apfloat problems are fixed
		// check("ArcSin(1.3038404810405297)",
		// "1.5707963267948966+I*(-7.610396837318266e-1)");
		checkNumeric("ArcSin(1.3038404810405297)", //
				"1.5707963267948966+I*(-7.6103968373182658*10^-1)");
		checkNumeric("InverseHaversine(1.7)", //
				"3.141592653589793+I*(-1.5220793674636532)");
	}

	public void testJaccardDissimilarity() {
		check("JaccardDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
				"3/5");
		check("JaccardDissimilarity({True, False, True}, {True, True, False})", //
				"2/3");
		check("JaccardDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
				"0");
		check("JaccardDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
				"1");
	}

	public void testJacobiSymbol() {
		check("JacobiSymbol(10^10+1, Prime(1000))", //
				"1");
		check("JacobiSymbol(10^11+1, Prime(2000))", //
				"-1");
		check("JacobiSymbol(10, 5)", //
				"0");
		check("Table(f(n, m), {n, 0, 10}, {m, 1, n, 2})", //
				"{{},{f(1,1)},{f(2,1)},{f(3,1),f(3,3)},{f(4,1),f(4,3)},{f(5,1),f(5,3),f(5,5)},{f(\n"
						+ "6,1),f(6,3),f(6,5)},{f(7,1),f(7,3),f(7,5),f(7,7)},{f(8,1),f(8,3),f(8,5),f(8,7)},{f(\n"
						+ "9,1),f(9,3),f(9,5),f(9,7),f(9,9)},{f(10,1),f(10,3),f(10,5),f(10,7),f(10,9)}}");
		check("Table(JacobiSymbol(n, m), {n, 0, 10}, {m, 1, n, 2})", //
				"{{},{1},{1},{1,0},{1,1},{1,-1,0},{1,0,1},{1,1,-1,0},{1,-1,-1,1},{1,0,1,1,0},{1,1,\n" + "0,-1,1}}");
		check("JacobiSymbol(1001, 9907)", //
				"-1");
		check("JacobiSymbol({2, 3, 5, 7, 11}, 3)", //
				"{-1,0,-1,1,-1}");
		check("JacobiSymbol(3, {1, 3, 5, 7})", //
				"{1,0,-1,-1}");
		check("JacobiSymbol(7, 6)", //
				"1");
		// check("JacobiSymbol(n, 1)", "n");
		check("JacobiSymbol(-3, {1, 3, 5, 7})", //
				"{JacobiSymbol(-3,1),JacobiSymbol(-3,3),JacobiSymbol(-3,5),JacobiSymbol(-3,7)}");
	}

	public void testJavaForm() {
		check("JavaForm(E^3-Cos(Pi^2/x), Prefix->True)", //
				"F.Subtract(F.Exp(F.C3),F.Cos(F.Times(F.Sqr(F.Pi),F.Power(F.x,F.CN1))))");
		check("JavaForm(E^3-Cos(Pi^2/x), Float->True)", //
				"(20.085536923187664)-Math.cos((9.869604401089358)/x)");
		check("JavaForm(E^3-Cos(Pi^2/x), Float)", //
				"(20.085536923187664)-Math.cos((9.869604401089358)/x)");

		check("JavaForm(Hold(D(sin(x)*cos(x),x)), prefix->True)", //
				"F.D(F.Times(F.Sin(F.x),F.Cos(F.x)),F.x)");
		check("JavaForm(Hold(D(sin(x)*cos(x),x)))", //
				"D(Times(Sin(x),Cos(x)),x)");
		check("JavaForm(D(sin(x)*cos(x),x), prefix->True)", //
				"F.Subtract(F.Sqr(F.Cos(F.x)),F.Sqr(F.Sin(F.x)))");
		check("JavaForm(D(sin(x)*cos(x),x))", //
				"Subtract(Sqr(Cos(x)),Sqr(Sin(x)))");
		check("JavaForm(I/2*E^((-I)*x)-I/2*E^(I*x), Prefix->True)", //
				"F.Plus(F.Times(F.CC(0L,1L,1L,2L),F.Exp(F.Times(F.CNI,F.x))),F.Times(F.CC(0L,1L,-1L,2L),F.Exp(F.Times(F.CI,F.x))))");
		check("JavaForm(I/2*E^((-I)*x)-I/2*E^(I*x))", //
				"Plus(Times(CC(0L,1L,1L,2L),Exp(Times(CNI,x))),Times(CC(0L,1L,-1L,2L),Exp(Times(CI,x))))");
		check("JavaForm(a+b+x^2+I+7+3/4+x+y, Prefix->True)", //
				"F.Plus(F.CC(31L,4L,1L,1L),F.a,F.b,F.x,F.Sqr(F.x),F.y)");
		check("JavaForm(a+b+x^2+I+7+3/4+x+y)", //
				"Plus(CC(31L,4L,1L,1L),a,b,x,Sqr(x),y)");
	}

	public void testJSForm() {
		check("JSForm(Cot(x))", //
				"(1/Math.tan(x))");
		check("JSForm(ArcCot(x))", //
				"((Math.PI/2.0)-Math.atan(x))");
		check("JSForm( Piecewise({{x^2, x < 0}, {x, x >= 0&&x<1},{Cos(x-1), x >= 1}}) )", //
				"((x<0) ? Math.pow(x,2) : ((x>=0&&x<1) ? x : ((x>=1) ? Math.cos(1-x) : ( 0 ) ) ))");
		check("JSForm(ConditionalExpression(Log(1- q), 0 <=q<=1))", //
				"((0<=q<=1) ? (Math.log(1-q)) : ( Number.NaN ))");
		check("JSForm(x < 10 && y > 1)", //
				"x<10&&y>1");
		check("JSForm(a<b)", //
				"a<b");
		check("JSForm(a+b)", //
				"a+b");
		check("JSForm(E^3-Cos(Pi^2/x) )", //
				"(20.085536923187664)-Math.cos((9.869604401089358)/x)");
		// JSXGraph.org syntax
		check("JSForm(Manipulate(Plot(Sin(x)*Cos(1 + a*x), {x, 0, 2*Pi}, PlotRange->{-1,2}), {a,0,10}))", //
				"var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-0.3141592653589793,2.15,6.5973445725385655,-1.15]});\n"
						+ "board.suspendUpdate();\n"
						+ "var a = board.create('slider',[[0.37699111843077515,1.8199999999999998],[5.906194188748811,1.8199999999999998],[0,0,10]],{name:'a'});\n"
						+ "\n" + "function z1(x) { return mul(cos(add(1,mul(a.Value(),x))),sin(x)); }\n"
						+ "var p1 = board.create('functiongraph',[z1, 0, (6.283185307179586)]);\n"
						+ "var data = [ p1 ];\n" + "\n" + "\n" + "board.unsuspendUpdate();\n" + "");
		// Mathcell syntax / generate TeX for MathJAX
		check("JSForm(Manipulate(Factor(x^n + 1), {n, 1, 5, 1}))", //
				"MathCell( id, [ { type: 'slider', min: 1, max: 5, step: 1, name: 'n', label: 'n' }\n" + //
						" ] );\n" + //
						"\n" + //
						"parent.update = function( id ) {\n" + //
						"\n" + //
						"var n = getVariable(id, 'n');\n" + //
						"\n" + //
						"\n" + //
						"var expressions = [ '1+x',\n" + //
						"'1+{x}^{2}',\n" + //
						"'\\\\\\\\left( 1+x\\\\\\\\right) \\\\\\\\,\\\\\\\\left( 1 - x+{x}^{2}\\\\\\\\right) ',\n" + //
						"'1+{x}^{4}',\n" + //
						"'\\\\\\\\left( 1+x\\\\\\\\right) \\\\\\\\,\\\\\\\\left( 1 - x+{x}^{2} - {x}^{3}+{x}^{4}\\\\\\\\right) ' ];\n"
						+ //
						"\n" + //
						"  var data = '\\\\\\\\[' + expressions[n-1] + '\\\\\\\\]';\n" + //
						"\n" + //
						"  data = data.replace( /\\\\\\\\/g, '&#92;' );\n" + //
						"\n" + //
						"  var config = {type: 'text', center: true };\n" + //
						"\n" + //
						"  evaluate( id, data, config );\n" + //
						"\n" + //
						"  MathJax.Hub.Queue( [ 'Typeset', MathJax.Hub, id ] );\n" + //
						"\n" + //
						"}");
		// JSXGraph.org syntax
		check("JSForm(ListPlot(Prime(Range(25))))", //
				"var board = JXG.JSXGraph.initBoard('jxgbox', {axis:true,boundingbox:[-1.3,101.75,27.3,-2.75]});\n" + //
						"board.suspendUpdate();\n" + //
						"\n" + //
						"board.create('point', [function() {return 1;},function() {return 2;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 2;},function() {return 3;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 3;},function() {return 5;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 4;},function() {return 7;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 5;},function() {return 11;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 6;},function() {return 13;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 7;},function() {return 17;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 8;},function() {return 19;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 9;},function() {return 23;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 10;},function() {return 29;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 11;},function() {return 31;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 12;},function() {return 37;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 13;},function() {return 41;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 14;},function() {return 43;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 15;},function() {return 47;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 16;},function() {return 53;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 17;},function() {return 59;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 18;},function() {return 61;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 19;},function() {return 67;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 20;},function() {return 71;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 21;},function() {return 73;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 22;},function() {return 79;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 23;},function() {return 83;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 24;},function() {return 89;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"board.create('point', [function() {return 25;},function() {return 97;}],  {name:'', face:'o', size: 2 } );\n"
						+ //
						"\n" + //
						"\n" + //
						"board.unsuspendUpdate();\n" + //
						"");
	}

	public void testJoin() {
		// http://oeis.org/A001597 - Perfect powers: m^k where m > 0 and k >= 2. //
		check("Join({1}, Select(Range(1770), GCD@@FactorInteger(#)[[All, 2]]>1&))", //
				"{1,4,8,9,16,25,27,32,36,49,64,81,100,121,125,128,144,169,196,216,225,243,256,289,\n"
						+ "324,343,361,400,441,484,512,529,576,625,676,729,784,841,900,961,1000,1024,1089,\n"
						+ "1156,1225,1296,1331,1369,1444,1521,1600,1681,1728,1764}");

		check("Join({a, b}, {c, d, e})", //
				"{a,b,c,d,e}");
		check("Join({{a, b}, {c, d}}, {{1, 2}, {3, 4}})", //
				"{{a,b},{c,d},{1,2},{3,4}}");
		check("Join(a + b, c + d, e + f)", //
				"a+b+c+d+e+f");
		check("Join(a + b, c * d)", //
				"Join(a+b,c*d)");
		check("Join(x + y, z)", //
				"Join(x+y,z)");
		check("Join(x + y, y * z, a)", //
				"Join(x+y,y*z,a)");
		check("Join(x, y + z, y * z)", //
				"Join(x,y+z,y*z)");

		check("Join(x, y)", //
				"Join(x,y)");
		check("Join({a,b}, {x,y,z})", //
				"{a,b,x,y,z}");
		check("Join({{a, b}, {x, y}}, {{1, 2}, {3, 4}})", //
				"{{a,b},{x,y},{1,2},{3,4}}");
	}

	public void testKolmogorovSmirnovTest() {
		check("data1 = {\n"
				+ "            0.53236606, -1.36750258, -1.47239199, -0.12517888, -1.24040594, 1.90357309,\n"
				+ "            -0.54429527, 2.22084140, -1.17209146, -0.68824211, -1.75068914, 0.48505896,\n"
				+ "            2.75342248, -0.90675303, -1.05971929, 0.49922388, -1.23214498, 0.79284888,\n"
				+ "            0.85309580, 0.17903487, 0.39894754, -0.52744720, 0.08516943, -1.93817962,\n"
				+ "            0.25042913, -0.56311389, -1.08608388, 0.11912253, 2.87961007, -0.72674865,\n"
				+ "            1.11510699, 0.39970074, 0.50060532, -0.82531807, 0.14715616, -0.96133601,\n"
				+ "            -0.95699473, -0.71471097, -0.50443258, 0.31690224, 0.04325009, 0.85316056,\n"
				+ "            0.83602606, 1.46678847, 0.46891827, 0.69968175, 0.97864326, 0.66985742,\n"
				+ "-0.20922486, -0.15265994}", //
				"{0.532366,-1.3675,-1.47239,-0.125179,-1.24041,1.90357,-0.544295,2.22084,-1.17209<<SHORT>>", //
				80);

		check("KolmogorovSmirnovTest(data1)", //
				"0.744855");
		check("KolmogorovSmirnovTest(data1, NormalDistribution(), \"TestData\")", //
				"{0.0930213,0.744855}");

		check("data2 = {\n" + "            0.95791391, 0.16203847, 0.56622013, 0.39252941, 0.99126354, 0.65639108,\n"
				+ "            0.07903248, 0.84124582, 0.76718719, 0.80756577, 0.12263981, 0.84733360,\n"
				+ "            0.85190907, 0.77896244, 0.84915723, 0.78225903, 0.95788055, 0.01849366,\n"
				+ "            0.21000365, 0.97951772, 0.60078520, 0.80534223, 0.77144013, 0.28495121,\n"
				+ "0.41300867, 0.51547517, 0.78775718, 0.07564151, 0.82871088, 0.83988694}", //
				"{0.957914,0.162038,0.56622,0.392529,0.991264,0.656391,0.0790325,0.841246,0.76718<<SHORT>>", //
				80);
		check("KolmogorovSmirnovTest(data1, data2)", //
				"0.000438682");
		check("KolmogorovSmirnovTest(data1, data2, \"TestData\")", //
				"{0.46,0.000438682}");
	}

	public void testKroneckerDelta() {
		check("KroneckerDelta(2 - I, 2. - I)", //
				"1");
		check("KroneckerDelta(n,0)", //
				"KroneckerDelta(0,n)");

		check("KroneckerDelta( )", //
				"1");
		check("KroneckerDelta(0)", //
				"1");
		check("KroneckerDelta(1)", //
				"0");
		check("KroneckerDelta(42)", //
				"0");
		check("KroneckerDelta(42, 42.0, 42)", //
				"1");
		check("KroneckerDelta(0,1)", //
				"0");
		check("KroneckerDelta(2,2)", //
				"1");
		check("KroneckerDelta(2,2.0)", //
				"1");

		check("KroneckerDelta(1,1,1,2)", //
				"0");

		check("Table(KroneckerDelta(n), {n, -2, 2})", //
				"{0,0,1,0,0}");
		check("Array(KroneckerDelta, {3, 3})", //
				"{{1,0,0},{0,1,0},{0,0,1}}");
		check("Table((KroneckerDelta(i - j + 1) + KroneckerDelta(i - j + 2))*i*j^2, {i, 5}, {j, 5})", //
				"{{0,4,9,0,0},{0,0,18,32,0},{0,0,0,48,75},{0,0,0,0,100},{0,0,0,0,0}}");
	}

	public void testKurtosis() {
		check("Kurtosis({1.1, 1.2, 1.4, 2.1, 2.4})", //
				"1.42098");
	}

	public void testLaguerreL() {
		check("LaguerreL(0, l, z)", //
				"1");
		check("LaguerreL(1, l, z)", //
				"1+l-z");
		check("LaguerreL(2, l, z)", //
				"1/2*(-1-l+(1+l-z)*(3+l-z))");
		check("LaguerreL(3, l, z)", //
				"1/3*(-(2+l)*(1+l-z)+1/2*(-1-l+(1+l-z)*(3+l-z))*(5+l-z))");
		check("LaguerreL(4, l, z)", //
				"1/4*(1/2*(-3-l)*(-1-l+(1+l-z)*(3+l-z))+1/3*(-(2+l)*(1+l-z)+1/2*(-1-l+(1+l-z)*(3+l-z))*(\n" + //
						"5+l-z))*(7+l-z))");
		check("LaguerreL(n, 0)", //
				"LaguerreL(n,0)");
		check("LaguerreL(8, x)", //
				"1-8*x+14*x^2-28/3*x^3+35/12*x^4-7/15*x^5+7/180*x^6-x^7/630+x^8/40320");
		// TODO add non-integer implementation
		// check("LaguerreL(3/2, 1.7)", "");
		check("LaguerreL(3, x)", //
				"1-3*x+3/2*x^2-x^3/6");
		check("LaguerreL(4, x)", //
				"1-4*x+3*x^2-2/3*x^3+x^4/24");
		check("LaguerreL(5, x)", //
				"1-5*x+5*x^2-5/3*x^3+5/24*x^4-x^5/120");
		check("LaguerreL(0,z)", //
				"1");
		check("LaguerreL(-3,z)", //
				"LaguerreL(-3,z)");
	}

	public void testInverseLaplaceTransform() {

		check("InverseLaplaceTransform(f(x)*s,s,t)", //
				"f(x)*DiracDelta'(t)");
		check("InverseLaplaceTransform(f(x),s,t)", //
				"DiracDelta(t)*f(x)");
		check("InverseLaplaceTransform(1/s,s,t)", //
				"1");
		check("InverseLaplaceTransform(1/s^5,s,t)", //
				"t^4/24");
		check("InverseLaplaceTransform(1/(s^2 +a^2),s,t)", //
				"Sin(a*t)/a");
		check("InverseLaplaceTransform(s/(s^2 +a^2),s,t)", //
				"Cos(a*t)");
		check("InverseLaplaceTransform(1/(1+s),s,t)", //
				"E^(-t)");
		check("InverseLaplaceTransform(1/(s^2-4),s,t)", //
				"(-1+E^(4*t))/(4*E^(2*t))");
		// test partial fraction decomposition:
		check("InverseLaplaceTransform(Together(3/(s-1)+(2*s)/(s^2+4)),s,t)", //
				"3*E^t+2*Cos(2*t)");
		check("InverseLaplaceTransform(3/(s-1)+(2*s)/(s^2+4),s,t)", //
				"3*E^t+2*Cos(2*t)");
	}

	public void testInverseWeierstrassP() {
		// check("InverseWeierstrassP(2.0,{1,2})", //
		// "-0.715096");
		// check("Table[InverseWeierstrassP[x, {1, 2}], {x, 2.0, 6.0}]",
		// "");
	}

	public void testLaplaceTransform() {
		check("LaplaceTransform(E^2,t,-3+s)", //
				"E^2/(-3+s)");
		check("LaplaceTransform(c*t^2, t, s)", //
				"(2*c)/s^3");
		check("LaplaceTransform((t^3+t^4)*t^2, t, s)", //
				"720/s^7+120/s^6");
		check("LaplaceTransform(t^2*Exp(2+3*t), t, s)", //
				"(2*E^2)/(-3+s)^3");
		check("LaplaceTransform(Exp(2+3*t)/t, t, s)", //
				"E^2*LaplaceTransform(1/t,t,-3+s)");

		check("LaplaceTransform(y'(t),t,s)", //
				"s*LaplaceTransform(y(t),t,s)-y(0)");
		check("LaplaceTransform(y''(t),t,s)", //
				"s^2*LaplaceTransform(y(t),t,s)-s*y(0)-y'(0)");

		check("LaplaceTransform(t, t, t)", //
				"LaplaceTransform(t,t,t)");
		check("LaplaceTransform(t, t, s)", //
				"1/s^2");
		check("LaplaceTransform(t, s, t)", //
				"1");
		check("LaplaceTransform(s, t, t)", //
				"LaplaceTransform(s,t,t)");
		check("LaplaceTransform(E^(-t), t, s)", //
				"1/(1+s)");
		check("LaplaceTransform(t^4*Sin(t), t, s)", //
				"(384*s^4)/(1+s^2)^5+(-288*s^2)/(1+s^2)^4+24/(1+s^2)^3");
		check("LaplaceTransform(t^(1/2), t, s)", //
				"Sqrt(Pi)/(2*s^(3/2))");
		check("LaplaceTransform(t^(1/3), t, s)", //
				"Gamma(4/3)/s^(4/3)");
		check("LaplaceTransform(t^a, t, s)", //
				"Gamma(1+a)/s^(1+a)");
		check("LaplaceTransform(Sin(t), t, s)", //
				"1/(1+s^2)");
		check("LaplaceTransform(Sin(t), t, t)", //
				"1/(1+t^2)");
		check("LaplaceTransform(Cos(t), t, s)", //
				"s/(1+s^2)");
		check("LaplaceTransform(Sinh(t), t, s)", //
				"c/(-1+s^2)");
		check("LaplaceTransform(Cosh(t), t, s)", //
				"s/(-1+s^2)");
		check("LaplaceTransform(Log(t), t, s)", //
				"-(EulerGamma+Log(s))/s");
		check("LaplaceTransform(Log(t)^2, t, s)", //
				"(6*EulerGamma^2+Pi^2+6*Log(s)*(2*EulerGamma+Log(s)))/(6*s)");
		check("LaplaceTransform(Erf(t), t, s)", //
				"(E^(s^2/4)*Erfc(s/2))/s");
		check("LaplaceTransform(Erf(t^(1/2)), t, s)", //
				"1/(s*Sqrt(1+s))");

		check("LaplaceTransform(Sin(t)*Exp(t), t, s)", //
				"1/(1+(1-s)^2)");
	}

	public void testLast() {
		check("Last({a, b, c})", //
				"c");
		check("Last(a + b + c)", //
				"c");
		check("Last(a)", //
				"Last(a)");
	}

	public void testLCM() {
		// System.out.println(Integer.MIN_VALUE); =>-2147483648
		check("LCM(-2147483648)", //
				"2147483648");
		check("LCM(I)", //
				"1");
		check("LCM(-I)", //
				"1");
		check("LCM(-2)", //
				"2");
		check("LCM(10)", //
				"10");
		check("LCM(-2147483648, -2147483648/2)", //
				"2147483648");
		check("LCM(2, 3, 5)", //
				"30");
		check("LCM(-3, 7)", //
				"21");
		check("LCM(4)", //
				"4");
		check("LCM(2, {3, 5, 7})", //
				"{6,10,14}");
		check("LCM(0,0)", //
				"0");
		check("LCM(0,10)", //
				"0");
		check("LCM(10,0)", //
				"0");
		check("LCM(a)", //
				"LCM(a)");
		check("LCM(a,-a)", //
				"LCM(-a,a)");
		check("LCM(15, 20)", //
				"60");
		check("LCM(20, 30, 40, 50)", //
				"600");
		check("LCM(-36,45)", //
				"180");
		check("LCM(36,-45)", //
				"180");
		check("LCM(-36,-45)", //
				"180");
		check("LCM(2,3,4,5)", //
				"60");
		check("Sum(LCM(3, k), {k, 100})", //
				"11784");
		// check("LCM(1/3, 2/5, 3/7)", "");
	}

	public void testLeafCount() {
		check("LeafCount(1 + x + y^a)", //
				"6");
		check("LeafCount(f(x, y))", //
				"3");
		check("LeafCount({1 / 3, 1 + I})", //
				"7");
		check("LeafCount(Sqrt(2))", //
				"5");
		check("LeafCount(100!)", //
				"1");
		check("LeafCount(f(1, 2)[x, y])", //
				"5");
		check("LeafCount(10+I)", //
				"3");
	}

	public void testLeastSquares() {
		// {-1577780898195/827587904419-11087326045520/827587904419*I,
		// 35583840059240/5793115330933+275839049310660/5793115330933*I,
		// -3352155369084/827587904419-28321055437140/827587904419*I}
		check("Table(Complex(i,Rational(2 * i + 2 + j, 1 + 9 * i + j)),{i,0,3},{j,0,2})", //
				"{{I*2,I*3/2,I*4/3},{1+I*2/5,1+I*5/11,1+I*1/2},{2+I*6/19,2+I*7/20,2+I*8/21},{3+\n"
						+ "I*2/7,3+I*9/29,3+I*1/3}}");
		check("LeastSquares(Table(Complex(i,Rational(2 * i + 2 + j, 1 + 9 * i + j)),{i,0,3},{j,0,2}), {1,1,1,1})", //
				"{-1577780898195/827587904419-I*11087326045520/827587904419,35583840059240/\n"
						+ "5793115330933+I*275839049310660/5793115330933,-3352155369084/827587904419-\n"
						+ "I*28321055437140/827587904419}");
		check("LeastSquares({{1, 1}, {1, 2}, {1, 3.0}}, {7, 7, 8})", //
				"{6.33333,0.5}");
		check("LeastSquares({{1, 1}, {1, 2}, {1, 3}}, {7, 7, 8})", //
				"{19/3,1/2}");
		check("LeastSquares({{1, 1}, {1, 2}, {1, 3}}, {7, 7, x})", //
				"{35/3-2/3*x,-7/2+x/2}");
	}

	public void testLegendreP() {
		check("Sqrt(Pi)/(Gamma((1 - Pi)/2) * Gamma(1 + Pi/2))", //
				"Sqrt(Pi)/(Gamma(1/2*(1-Pi))*Gamma(1+Pi/2))");

		check("LegendreP(-(1/2), 1 - 2*z)", //
				"(2*EllipticK(z))/Pi");
		check("LegendreP(Pi,0)", //
				"Sqrt(Pi)/(Gamma(1/2*(1-Pi))*Gamma(1+Pi/2))");
		check("LegendreP(111,1)", //
				"1");
		check("LegendreP(4,x)", //
				"3/8-15/4*x^2+35/8*x^4");
		// TODO implement non integer args
		// check("LegendreP(5/2, 1.5) ", "x");

		check("LegendreP(0,x)", //
				"1");
		check("LegendreP(1,x)", //
				"x");
		check("LegendreP(2,x)", //
				"-1/2+3/2*x^2");
		check("LegendreP(7,x)", //
				"-35/16*x+315/16*x^3-693/16*x^5+429/16*x^7");
		check("LegendreP(10,x)", //
				"-63/256+3465/256*x^2-15015/128*x^4+45045/128*x^6-109395/256*x^8+46189/256*x^10");
	}

	public void testLegendreQ() {
		check("LegendreQ(-(1/2), 2*z - 1)", //
				"EllipticK(z)");
		check("LegendreQ(-3,z)", //
				"ComplexInfinity");
		check("LegendreQ(1,z)", //
				"-1+z*(-Log(1-z)/2+Log(1+z)/2)");
		check("LegendreQ(2,z)", //
				"-3/2*z+1/2*(-1/2+3/2*z^2)*(-Log(1-z)+Log(1+z))");
		check("LegendreQ(3,z)", //
				"-1/6+5/3*(1/2-3/2*z^2)+1/2*(-3/2*z+5/2*z^3)*(-Log(1-z)+Log(1+z))");
		check("Expand(LegendreQ(4,z))", //
				"55/24*z-35/8*z^3-3/16*Log(1-z)+15/8*z^2*Log(1-z)-35/16*z^4*Log(1-z)+3/16*Log(1+z)\n"
						+ "-15/8*z^2*Log(1+z)+35/16*z^4*Log(1+z)");
	}

	public void testLength() {
		check("Length(aa)", //
				"0");
		check("Length({1, 2, 3})", //
				"3");
		check("Length(Exp(x))", //
				"2");
		check("FullForm(Exp(x))", //
				"Power(E, x)");
		check("Length(a)", //
				"0");
		check("Length(1/3)", //
				"0");
		check("FullForm(1/3)", //
				"Rational(1,3)");

		check("Length(a + b + c + d)", //
				"4");
		check("Length(3 + I)", //
				"0");
		check("Map(Length, {{a, b}, {a, b, c}, {x}})", //
				"{2,3,1}");
	}

	public void testLess() {
		check("Infinity<Infinity", //
				"False");

		check("x<x", //
				"x<x");
		check("I<0", //
				"I<0");
		check("3+x<4+x", //
				"3+x<4+x");
		check("3+x>4+x", //
				"3+x>4+x");

		check("Refine(Infinity<x, x>0)", //
				"False");
		check("Refine(-Infinity<x, x>0)", //
				"True");

		check("3<4", //
				"True");
		check("3<4<5", //
				"True");
		check("{Less(), Less(x), Less(1)}", //
				"{True,True,True}");
		check("(2*x+5)<(5^(1/2))", //
				"x<1/2*(-5+Sqrt(5))");
		check("(-2*x+5)<(5^(1/2))", //
				"x>1/2*(5-Sqrt(5))");
	}

	public void testLessEqual() {
		check("x<=x", //
				"x<=x");
		check("3+x<=4+x", //
				"3+x<=4+x");
		check("3+x>=4+x", //
				"3+x>=4+x");

		check("Infinity<=Infinity", //
				"True");

		check("Refine(Infinity<=x, x>0)", //
				"False");
		check("Refine(-Infinity<=x, x>0)", //
				"True");

		check("3<=4", //
				"True");
		check("3<=4<=5", //
				"True");
		check("{LessEqual(), LessEqual(x), LessEqual(1)}", //
				"{True,True,True}");
		check("(2*x+5)<=(5^(1/2))", //
				"x<=1/2*(-5+Sqrt(5))");
		check("(-2*x+5)<=(5^(1/2))", //
				"x>=1/2*(5-Sqrt(5))");
	}

	public void testLetterQ() {
		check("LetterQ(\"a\")", //
				"True");
		check("LetterQ(\"2\")", //
				"False");
		check("LetterQ(\"äü\")", //
				"True");
	}

	public void testLevel() {
		check("Level(x, -1)", //
				"{}");
		check("Level(x, 0)", //
				"{}");
		check("Level(x, 1)", //
				"{}");
		check("demo=x+Cos(Pi/3*Sqrt(2+1/x))+Sin(Pi/6*Sqrt(x))", //
				"x+Cos(1/3*Pi*Sqrt(2+1/x))+Sin(1/6*Pi*Sqrt(x))");
		check("Level(demo, 1)", //
				"{x,Cos(1/3*Pi*Sqrt(2+1/x)),Sin(1/6*Pi*Sqrt(x))}");
		check("Level(demo, 2)", //
				"{x,1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x))}");
		check("Level(demo, {2})", //
				"{1/3*Pi*Sqrt(2+1/x),1/6*Pi*Sqrt(x)}");
		check("Level(demo, {25})", //
				"{}");
		check("Level(demo, {2,4})", //
				"{1/3,Pi,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x)}");
		check("Level(demo, {0,4})", //
				"{x,1/3,Pi,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),1/6,Pi,x,\n"
						+ "1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x)),x+Cos(1/3*Pi*Sqrt(2+1/x))+Sin(1/6*Pi*Sqrt(x))}");
		check("Level(demo, {-1})", //
				"{x,1/3,Pi,2,x,-1,1/2,1/6,Pi,x,1/2}");
		check("Level(demo, {-4})", //
				"{Sqrt(2+1/x),Sin(1/6*Pi*Sqrt(x))}");
		check("Level(demo, {-25})", //
				"{}");
		check("Level(demo, -4)", //
				"{Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),Sin(1/6*Pi*Sqrt(x))}");
		check("Level(demo, -1)", //
				"{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),\n"
						+ "1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x))}");
		check("Level(demo, Infinity)", //
				"{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),\n"
						+ "1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x))}");
		check("Level(demo, {1, -3})", //
				"{2+1/x,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),1/6*Pi*Sqrt(x),Sin(\n"
						+ "1/6*Pi*Sqrt(x))}");
		check("Level(demo, {-4, 2})", //
				"{x,1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x))}");
		check("Level(demo, {0, -1})", //
				"{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),\n"
						+ "1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x)),x+Cos(1/3*Pi*Sqrt(2+1/x))+Sin(\n"
						+ "1/6*Pi*Sqrt(x))}");
		check("Level(demo, {-Infinity, Infinity})", //
				"{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,Sqrt(2+1/x),1/3*Pi*Sqrt(2+1/x),Cos(1/3*Pi*Sqrt(2+1/x)),\n"
						+ "1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x),Sin(1/6*Pi*Sqrt(x)),x+Cos(1/3*Pi*Sqrt(2+1/x))+Sin(\n"
						+ "1/6*Pi*Sqrt(x))}");
		check("Level(demo, {3, -6})", //
				"{}");
		check("Level(demo, {-3, -1})", //
				"{x,1/3,Pi,2,x,-1,1/x,2+1/x,1/2,1/6,Pi,x,1/2,Sqrt(x),1/6*Pi*Sqrt(x)}");

		check("Level(a + f(x, y^n), {-1})", //
				"{a,x,y,n}");
		check("Level(a + b ^ 3 * f(2*x ^ 2), {-1}, g)", //
				"g(a,b,3,2,x,2)");
		check("Level(a + b ^ 3 * f(2*x ^ 2), {-1})", //
				"{a,b,3,2,x,2}");
		check("Level({{{{a}}}}, 3)", //
				"{{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, -4)", //
				"{{{{a}}}}");
		check("Level({{{{a}}}}, -5)", //
				"{}");
		check("Level(h0(h1(h2(h3(a)))), {0, -1})", //
				"{a,h3(a),h2(h3(a)),h1(h2(h3(a))),h0(h1(h2(h3(a))))}");
		check("Level({{{{a}}}}, 3, Heads -> True)", //
				"{List,List,List,{a},{{a}},{{{a}}}}");
		check("Level(x^2 + y^3, 3, Heads -> True)", //
				"{Plus,Power,x,2,x^2,Power,y,3,y^3}");
		check("Level(a ^ 2 + 2 * b, {-1}, Heads -> True)", //
				"{Plus,Power,a,2,Times,2,b}");
		check("Level(f(g(h))[x], {-1}, Heads -> True)", //
				"{f,g,h,x}");
		// TODO
		// check("Level(f(g(h))[x], {-2, -1}, Heads -> True)",
		// "{f,g,h,g(h),x,f(g(h))[x]}");

		check("Level(a + f(x, y^n), {-1})", //
				"{a,x,y,n}");
		check("Level(a + f(x, y^n0), 2)", //
				"{a,x,y^n0,f(x,y^n0)}");
		check("Level(a + f(x, y^n0), {0, Infinity})", //
				"{a,x,y,n0,y^n0,f(x,y^n0),a+f(x,y^n0)}");
		check("Level({{{{a}}}}, 1)", //
				"{{{{a}}}}");
		check("Level({{{{a}}}}, 2)", //
				"{{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, 3)", //
				"{{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, 4)", //
				"{a,{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, 5)", //
				"{a,{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, -1)", //
				"{a,{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, -2)", //
				"{{a},{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, -3)", //
				"{{{a}},{{{a}}}}");
		check("Level({{{{a}}}}, -4)", //
				"{{{{a}}}}");
		check("Level({{{{a}}}}, -5)", //
				"{}");
		check("Level({{{{a}}}}, {2, 3})", //
				"{{a},{{a}}}");
		check("Level({{{{a}}}}, {0, -1})", //
				"{a,{a},{{a}},{{{a}}},{{{{a}}}}}");
		check("Level(h0(h1(h2(h3(a)))), {0, -1})", //
				"{a,h3(a),h2(h3(a)),h1(h2(h3(a))),h0(h1(h2(h3(a))))}");
		check("Level({{{{a}}}}, 3, Heads -> True)", //
				"{List,List,List,{a},{{a}},{{{a}}}}");
		check("Level(x^2 + y^3, 3, Heads -> True)", //
				"{Plus,Power,x,2,x^2,Power,y,3,y^3}");
		check("Level(h1(h2(h3(x))), -1)", //
				"{x,h3(x),h2(h3(x))}");
		check("Level(h1(h2(h3(x))), {0, -1})", //
				"{x,h3(x),h2(h3(x)),h1(h2(h3(x)))}");

		check("Level(f(f(g(a), a), a, h(a), f), 2)", //
				"{g(a),a,f(g(a),a),a,a,h(a),f}");
		check("Level(f(f(g(a), a), a, h(a), f), {2})", //
				"{g(a),a,a}");
		check("Level(f(f(g(a), a), a, h(a), f), {-1})", //
				"{a,a,a,a,f}");
		check("Level(f(f(g(a), a), a, h(a), f), {-2})", //
				"{g(a),h(a)}");
	}

	public void testLevelQ() {
		check("LevelQ(2)", //
				"True");
		check("LevelQ({2, 4})", //
				"True");
		check("LevelQ(Infinity)", //
				"True");
		check("LevelQ(a + b)", //
				"False");
	}

	// public void testJacobianMatrix() {
	// check("JacobianMatrix({Rr, Ttheta, Zz}, Cylindrical)", "");
	// }

	public void testLimit() {
		check("Limit(Sqrt(x^2 - 1)/x, x->-Infinity)", //
				"-1");
		check("Limit(x/Sqrt(x^2 - 1), x->-Infinity)", //
				"-1");
		// gitlab #107
		check("Limit(x^2-1/x-2, x->0)", //
				"Indeterminate");

		check("Limit((x^2) /(3*x), x->Infinity)", //
				"Infinity");
		check("Limit(x^(-2/3),x->0 , Direction->-1)", //
				"Infinity");
		check("Limit(x^(-2/3),x->0 , Direction->1)", //
				"Limit(1/x^(2/3),x->0,Direction->1)");
		check("Limit(x^(-2/3),x->0)", //
				"Indeterminate");

		check("Limit(x^(-16/7),x->0 , Direction->-1)", //
				"Infinity");
		check("Limit(x^(-16/7),x->0 , Direction->1)", //
				"Limit(1/x^(16/7),x->0,Direction->1)");
		check("Limit(x^(-16/7),x->0)", //
				"Indeterminate");

		check("Limit(x^(-37/4),x->0 , Direction->-1)", //
				"Infinity");
		check("Limit(x^(-37/4),x->0 , Direction->1)", //
				"Limit(1/x^(37/4),x->0,Direction->1)");
		check("Limit(x^(-37/4),x->0)", //
				"Indeterminate");

		check("Limit((x^2-1)/(x-1)^2, x->1)", //
				"Indeterminate");
		check("Limit((3*x^2-6)^(-1/3), x->-Infinity)", //
				"0");

		check("Limit(Cosh(x) , x->3)", //
				"Cosh(3)");
		check("Limit(x^3-4*x^2+6, x->-Infinity)", //
				"-Infinity");
		check("Limit(42, x->Infinity)", //
				"42");
		check("Limit(x^2-x^4, x->Infinity)", //
				"-Infinity");
		check("Limit((4*x^3-3*x+2)/(2*x^3+2*x-1), x->Infinity)", //
				"2");
		check("Limit((x^2-3*x+2)/(x^3+2*x-1), x->Infinity)", //
				"0");
		check("Limit(Sqrt(3*x-2), x->-Infinity)", //
				"Sqrt(-Infinity)");

		check("Limit((x-1)^2/(x^2-1), x->1)", //
				"0");
		check("Limit((x-1)/(x^2-1), x->1)", //
				"1/2");
		check("Limit((x^2-4)/(x-2), x->2)", //
				"4");
		check("Limit((x^3-1)/(x^2-1), x->1)", //
				"3/2");

		// github #120
		check("Limit( x*Log(x) , x->0)", //
				"0");
		check("Limit(Log(x),x->0)", //
				"-Infinity");
		check("Limit(Log(x)^2,x->0)", //
				"Infinity");
		check("Limit(2*x-2*x*Log(x)+x*Log(x)^2, x->0)", //
				"0");
		check("Limit(E^(-x)*Sqrt(x), x -> Infinity)", //
				"0");

		// adjust LimitRules.m if these 2 tests fails
		// check("FullForm(x*(Sqrt(2*Pi*x)/(x!))^(1/x) )", //
		// "Times(Power(Power(Times(2, Pi), Rational(1,2)), Power(x, -1)), x, Power(Times(Power(x, Rational(1,2)),
		// Power(Factorial(x), -1)), Power(x, -1)))");
		// check("Limit(x*(Sqrt(2*Pi*x)/(x!))^(1/x), x->Infinity)", //
		// "E");
		// check("Limit(x/((x!)^(1/x)), x->Infinity)", //
		// "E");

		// github #115
		check("Limit(Sqrt(-4+2*x^2)/(4+3*x),x->Infinity)", //
				"Sqrt(2)/3");
		check("Limit((4+3*x)/Sqrt(-4+2*x^2),x->Infinity)", //
				"3/Sqrt(2)");
		check("Limit((4+3*x)^2/(-4+2*x^2),x->Infinity)", //
				"9/2");

		check("Limit(x^(13+n),x->0)", //
				"ConditionalExpression(0,n>-13)");
		// check("Limit(x^(13+n)/a,x->0)", //
		// "");

		check("Limit(E^(3*x), x->a)", //
				"E^(3*a)");

		check("Limit((1+k/x)^x, x->Infinity)", //
				"E^k");
		check("Limit((1-1/x)^x, x->Infinity)", //
				"1/E");
		// check("Limit((1 + Sinh(x))/E^x, x ->Infinity)", "Infinity*Limit(E^(-x),x->Infinity)");

		// issue #184
		check("N(Limit(tan(x),x->pi/2))", //
				"Indeterminate");

		check("Limit(Tan(x), x->Pi/2)", //
				"Indeterminate");
		check("Limit(Tan(x), x->Pi/2, Direction->1)", //
				"Infinity");
		check("Limit(Tan(x), x->Pi/2, Direction->-1)", //
				"-Infinity");
		check("Limit(Tan(x+3*Pi), x->Pi/2)", //
				"Indeterminate");
		check("Limit(Tan(x+3*Pi), x->Pi/2, Direction->1)", //
				"Infinity");
		check("Limit(Tan(x+3*Pi), x->Pi/2, Direction->-1)", //
				"-Infinity");
		check("Limit(Cot(x), x->0)", //
				"Indeterminate");
		check("Limit(Cot(x), x->0, Direction->1)", //
				"-Infinity");
		check("Limit(Cot(x), x->0, Direction->-1)", //
				"Infinity");
		check("Limit(Cot(x+Pi), x->0)", //
				"Indeterminate");
		check("Limit(Cot(x+Pi), x->0, Direction->1)", //
				"-Infinity");
		check("Limit(Cot(x+Pi), x->0, Direction->-1)", //
				"Infinity");

		check("Limit(Log(x^y), x->0)", //
				"DirectedInfinity(-y)");
		check("Limit(Log(y*x, b), x->1)", //
				"Limit(1/Log(x*y),x->1)*Log(b)");
		check("Limit(Log(y*x), x->0)", //
				"-Infinity");
		check("Limit(Log(x), x->Infinity)", //
				"Infinity");
		check("Limit(Log(x), x->-Infinity)", //
				"Infinity");
		check("Limit((y*x)/Abs(x), x->0)", //
				"Indeterminate");
		check("Limit((y*x)/Abs(x), x->0, Direction->1)", //
				"-y");
		check("Limit(x/Abs(x), x->0)", //
				"Indeterminate");
		check("Limit(x/Abs(x), x->0, Direction->-1)", //
				"1");
		check("Limit(x/Abs(x), x->0, Direction->1)", //
				"-1");
		check("Limit(Log(x), x -> 0)", //
				"-Infinity");
		check("Limit(x^x, x -> 0)", //
				"1");
		check("Limit(1/x, x -> Infinity, Direction->1)", //
				"0");
		check("Limit(1/x, x -> Infinity, Direction->-1)", //
				"0");
		check("Limit(1/x, x -> 0, Direction->1)", //
				"-Infinity");
		check("Limit(1/x, x -> 0, Direction->-1)", //
				"Infinity");

		// print additional message. Messages are typically suppressed in Limit() steps.
		check("1/0", //
				"ComplexInfinity");

		// check("Limit((4 - x), x -> 4)", "0");
		check("Limit(1/(4 - x), x -> 4)", //
				"Indeterminate");
		check("Limit(1/(x - 4), x -> 4)", //
				"Indeterminate");

		check("Infinity-1", //
				"Infinity");
		check("Limit(a+b+2*x,x->-Infinity)", //
				"-Infinity");
		check("Limit(a+b+2*x,x->Infinity)", //
				"Infinity");
		check("Limit(E^(-x)*Sqrt(x), x -> Infinity)", //
				"0");
		check("Limit(Sin(x)/x,x->0)", //
				"1");
		check("Limit(x*Sin(1/x),x->Infinity)", //
				"1");

		check("Limit(-x,x->Infinity)", //
				"-Infinity");
		check("Limit((1 + x/n)^n, n -> Infinity)", //
				"E^x");
		check("Limit((x^2 - 2*x - 8)/(x - 4), x -> 4)", //
				"6");
		check("Limit((x^3-1)/(2*x^3-3*x),x->Infinity)", //
				"1/2");
		check("Limit((x^3-1)/(2*x^3+3*x),x->Infinity)", //
				"1/2");

		check("Limit((2*x^3-3*x),x->Infinity)", //
				"Infinity");
		check("Limit((2*x^3+3*x),x->Infinity)", //
				"Infinity");

		check("Limit(E^x, x->Infinity)", //
				"Infinity");
		check("Limit(E^x, x->-Infinity)", //
				"0");
		check("Limit(a^x, x->0)", //
				"1");
		check("Limit(c*(x^(-10)), x->Infinity)", //
				"0");

	}

	public void testLinearProgramming() {
		check("LinearProgramming({1, 1}, {{1, 2}}, {3})", //
				"{0.0,1.5}");
		check("LinearProgramming({1, 1}, {{1, 2}}, {{3,0}})", //
				"{0.0,1.5}");
		check("LinearProgramming({1, 1}, {{1, 2}}, {{3,-1}})", //
				"{0.0,0.0}");
		check("LinearProgramming({1., 1.}, {{5., 2.}}, {3.})", //
				"{0.6,0.0}");
	}

	public void testLinearRecurrence() {
		check("LinearRecurrence({a, b}, {1, 1}, 5)", //
				"{1,1,a+b,b+a*(a+b),b*(a+b)+a*(b+a*(a+b))}");
		check("LinearRecurrence({-3, 1}, {7, 2}, 10)", //
				"{7,2,1,-1,4,-13,43,-142,469,-1549}");
		// Fibonacci sequence
		check("LinearRecurrence({1, 1}, {1, 1}, 10)", //
				"{1,1,2,3,5,8,13,21,34,55}");
		check("LinearRecurrence({1, 1}, {1, 1}, {8})", //
				"21");

		check("LinearRecurrence({a,b}, {c,d}, 5)", //
				"{c,d,b*c+a*d,b*d+a*(b*c+a*d),b*(b*c+a*d)+a*(b*d+a*(b*c+a*d))}");
		check("LinearRecurrence({1, 1}, {{1, 2}, {2, 1}}, 10)", //
				"{{1,2},{2,1},{3,3},{5,4},{8,7},{13,11},{21,18},{34,29},{55,47},{89,76}}");

		// A001608 Perrin sequence
		// https://oeis.org/A001608
		check("LinearRecurrence({0, 1, 1}, {3, 0, 2}, 50)", //
				"{3,0,2,3,2,5,5,7,10,12,17,22,29,39,51,68,90,119,158,209,277,367,486,644,853,1130,\n"
						+ "1497,1983,2627,3480,4610,6107,8090,10717,14197,18807,24914,33004,43721,57918,\n"
						+ "76725,101639,134643,178364,236282,313007,414646,549289,727653,963935}");

		// A016064 Shortest legs of Heronian triangles (sides are consecutive integers, area is an integer).
		// https://oeis.org/A016064
		check("LinearRecurrence({5, -5, 1}, {1, 3, 13}, 26)", //
				"{1,3,13,51,193,723,2701,10083,37633,140451,524173,1956243,7300801,27246963,\n"
						+ "101687053,379501251,1416317953,5285770563,19726764301,73621286643,274758382273,\n"
						+ "1025412242451,3826890587533,14282150107683,53301709843201,198924689265123}");

		// A251599 Centers of rows of the triangular array formed by the natural numbers.
		// https://oeis.org/A251599
		check("LinearRecurrence({1, 0, 2, -2, 0, -1, 1}, {1, 2, 3, 5, 8, 9, 13}, 60)", //
				"{1,2,3,5,8,9,13,18,19,25,32,33,41,50,51,61,72,73,85,98,99,113,128,129,145,162,\n"
						+ "163,181,200,201,221,242,243,265,288,289,313,338,339,365,392,393,421,450,451,481,\n"
						+ "512,513,545,578,579,613,648,649,685,722,723,761,800,801}");

		// A050250 Number of nonzero palindromes less than 10^n.
		// https://oeis.org/A050250
		check("LinearRecurrence({1, 10, -10}, {9, 18, 108}, 30)", //
				"{9,18,108,198,1098,1998,10998,19998,109998,199998,1099998,1999998,10999998,\n"
						+ "19999998,109999998,199999998,1099999998,1999999998,10999999998,19999999998,\n"
						+ "109999999998,199999999998,1099999999998,1999999999998,10999999999998,\n"
						+ "19999999999998,109999999999998,199999999999998,1099999999999998,1999999999999998}");
	}

	public void testLinearSolve() {
		check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1,1,1})", //
				"{-1,1,0}");
		// github issue #44
		check("LinearSolve({{1,0,-1,0},{0,1,0,-1},{1,-2,-1,0},{-1,0,3,1}},"//
				+ "{0.06,0.06,-0.4,-0.06})", //
				"{-0.025,0.23,-0.085,0.17}");

		check("LinearSolve({{a, b, c, d}}, {x})", //
				"{x/a,0,0,0}");
		check("LinearSolve({{a, b,c,d,e}, {f,g,h,i,j}}, {x, y})", //
				"{(g*x-b*y)/(-b*f+a*g),(-f*x+a*y)/(-b*f+a*g),0,0,0}");
		check("LinearSolve({{a,b,c,d,e}, {f,g,h,i,j}, {k,l,m,n,o}}, {x,y,z})", //
				"{(-h*l*x+g*m*x+c*l*y-b*m*y-c*g*z+b*h*z)/(-c*g*k+b*h*k+c*f*l-a*h*l-b*f*m+a*g*m),(h*k*x-f*m*x-c*k*y+a*m*y+c*f*z-a*h*z)/(-c*g*k+b*h*k+c*f*l-a*h*l-b*f*m+a*g*m),(-g*k*x+f*l*x+b*k*y-a*l*y-b*f*z+a*g*z)/(-c*g*k+b*h*k+c*f*l-a*h*l-b*f*m+a*g*m),\n"
						+ "0,0}");
		// underdetermined system:
		check("LinearSolve({{1, 2, 3}, {4, 5, 6}}, {6, 15})", //
				"{0,3,0}");
		// linear equations have no solution
		check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, -2, 1})", //
				"LinearSolve(\n" + "{{1,2,3},\n" + " {4,5,6},\n" + " {7,8,9}},{1,-2,1})");

		check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, 1, 1})", //
				"{-1,1,0}");
		check("LinearSolve({{1, 2}, {3, 4}}, {1, {2}})", //
				"LinearSolve(\n" + "{{1,2},\n" + " {3,4}},{1,{2}})");

		check("LinearSolve({{1, 1, 1}, {1, 2, 3}, {1, 4, 9}}, {1, 2, 3})", //
				"{-1/2,2,-1/2}");
		check("LinearSolve(N({{1, 1, 1}, {1, 2, 3}, {1, 4, 9}}), N({1, 2, 3}))", //
				"{-0.5,2.0,-0.5}");
		check("LinearSolve({{a, b}, {c, d}}, {x, y})", //
				"{(d*x-b*y)/(-b*c+a*d),(c*x-a*y)/(b*c-a*d)}");

		check("LinearSolve({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}}, {1, 2, 3})", //
				"{0,1,2}");
		check("{{1, 1, 0}, {1, 0, 1}, {0, 1, 1}} . {0, 1, 2}", //
				"{1,2,3}");
		check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, 1, 1})", //
				"{-1,1,0}");
		check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, -2, 3})", //
				"LinearSolve(\n" + "{{1,2,3},\n" + " {4,5,6},\n" + " {7,8,9}},{1,-2,3})");
		check("LinearSolve({1, {2}}, {1, 2})", //
				"LinearSolve({1,{2}},{1,2})");
	}

	public void testLiouvilleLambda() {
		check("LiouvilleLambda(3^5)", //
				"-1");
		check("LiouvilleLambda(2*3^5)", //
				"1");
		check("LiouvilleLambda(50!)", //
				"1");
		check("LiouvilleLambda({1,2,3,4,5,6,20})", //
				"{1,-1,-1,1,-1,1,-1}");
	}

	public void testList() {
		// prints error
		check("{1,2}+{4,5,6}", //
				"{1,2}+{4,5,6}");
	}

	public void testListable() {
		check("SetAttributes(f, Listable)", //
				"");
		check("f({1, 2, 3}, {4, 5, 6})", //
				"{f(1,4),f(2,5),f(3,6)}");
		check("f({1, 2, 3}, 4)", //
				"{f(1,4),f(2,4),f(3,4)}");
		check("{{1, 2}, {3, 4}} + {5, 6}", //
				"{{6,7},{9,10}}");
	}

	public void testListConvolve() {
		check("ListConvolve({x, y}, {a, b, c, d, e, f})", //
				"{b*x+a*y,c*x+b*y,d*x+c*y,e*x+d*y,f*x+e*y}");
	}

	public void testListCorrelate() {
		check("ListCorrelate({x, y}, {a, b, c, d, e, f})", //
				"{a*x+b*y,b*x+c*y,c*x+d*y,d*x+e*y,e*x+f*y}");
	}

	public void testListQ() {
		check("ListQ({1, 2, 3})", //
				"True");
		check("ListQ({{1, 2}, {3, 4}})", //
				"True");
		check("ListQ(x)", //
				"False");
	}

	public void testLog() {
		check("Log(Interval({0, 3}))", //
				"Interval({-Infinity,Log(3)})");
		check("Log(Interval({-1, 3}))", //
				"Log(Interval({-1,3}))");
		// github #134
		check("Log(10,1)", //
				"0");
		check("Log(0,0)", //
				"Indeterminate");

		check("Log(E^(7+13*I))", //
				"7+I*13-I*4*Pi");
		check("Log(E^(27*I))", //
				"I*27-I*8*Pi");
		check("Log(10*E) // Simplify", //
				"1+Log(10)");
		check("Log(10*E*x*y) // FunctionExpand", //
				"1+Log(10)+Log(x*y)");

		check("Log( )", //
				"Log()");

		check("Log(2/3)", //
				"-Log(3/2)");
		check("Log(3/2)", //
				"Log(3/2)");
		check("Log(-3/2)", //
				"I*Pi+Log(3/2)");
		check("Log(-2/3)", //
				"I*Pi-Log(3/2)");
		check("Log(0, 0)", ///
				"Indeterminate");
		check("Log(0, x)", ///
				"0");
		check("Log(2, 0)", //
				"-Infinity");
		check("Log(3/4, 0)", //
				"Infinity");
		check("Log(-2, 0)", //
				"(-Infinity)/(I*Pi+Log(2))");

		check("Exp(Log(x))", //
				"x");
		check("Refine(Log(Exp(x)),Element(x, Reals))", //
				"x");
		check("Log({0, 1, E, E * E, E ^ 3, E ^ x})", //
				"{-Infinity,0,1,2,3,Log(E^x)}");
		check("Log(0.)", //
				"Indeterminate");
		check("Log(1000) / Log(10)", //
				"3");
		check("Log(1.4)", //
				"0.336472");
		checkNumeric("Log(Exp(1.4))", //
				"1.3999999999999997");
		checkNumeric("Log(-1.4)", //
				"0.3364722366212129+I*3.141592653589793");

		check("Log(-1)", //
				"I*Pi");
		check("Log(-2)", //
				"I*Pi+Log(2)");
		// test alias
		check("Ln(E)", //
				"1");
		check("ln(-E)", //
				"1+I*Pi");

		check("Log(a, b)", //
				"Log(b)/Log(a)");
		check("Log(Pi^E)", //
				"E*Log(Pi)");
		check("Log(E^10)", //
				"10");
		check("Log(E)", //
				"1");
		check("Log(-E)", //
				"1+I*Pi");
		check("D(Log(a, x),x)", //
				"1/(x*Log(a))");
		checkNumeric("Log(1000.)", //
				"6.907755278982137");
		checkNumeric("Log(2.5 + I)", //
				"0.9905007344332918+I*0.3805063771123649");
		checkNumeric("Log({2.1, 3.1, 4.1})", //
				"{0.7419373447293773,1.1314021114911006,1.410986973710262}");
		check("Log(2, 16)", //
				"4");
		check("Log(10, 1000)", //
				"3");
		check("Log(10, 10)", //
				"1");
		check("Log(0)", //
				"-Infinity");
		check("Log(1)", //
				"0");
		check("Log(-x)", //
				"Log(-x)");
		check("Log(-1)", //
				"I*Pi");
		check("Log(I)", //
				"I*1/2*Pi");
		check("Log(-I)", //
				"-I*1/2*Pi");
		check("Log(GoldenRatio)", //
				"ArcCsch(2)");
		check("Log(Infinity)", //
				"Infinity");
		check("Log(-Infinity)", //
				"Infinity");

		check("Log(I*Infinity)", //
				"Infinity");
		check("Log(-I*Infinity)", //
				"Infinity");
		check("Log(ComplexInfinity)", //
				"Infinity");
	}

	public void testLog10() {
		check("Log10(1000)", //
				"3");
		checkNumeric("Log10({2., 5.})", //
				"{0.30102999566398114,0.6989700043360186}");
		check("Log10(E ^ 3)", //
				"3/Log(10)");

		check("Log10(x)", //
				"Log(x)/Log(10)");
	}

	public void testLog2() {
		check("Log2(4 ^ 8)", //
				"16");
		checkNumeric("Log2(5.6)", //
				"2.4854268271702415");
		check("Log2(E ^ 2) ", //
				"2/Log(2)");
		check("Log2(x)", //
				"Log(x)/Log(2)");
	}

	public void testLogGamma() {
		check("LogGamma(-11/2)", //
				"LogGamma(-11/2)");
		check("LogGamma(1/2)", //
				"Log(Pi)/2");
		check("LogGamma(11/2)", //
				"Log(945/32*Sqrt(Pi))");
		check("LogGamma(43/2)", //
				"Log(13113070457687988603440625/2097152*Sqrt(Pi))");
		check("LogGamma(7.7)", //
				"7.92654");
		check("LogGamma(-I*Infinity)", //
				"ComplexInfinity");
		check("LogGamma(6)", //
				"Log(120)");
		check("LogGamma(1)", //
				"0");
		check("LogGamma(0)", //
				"Infinity");
		check("LogGamma(-6)", //
				"Infinity");
	}

	public void testLogIntegral() {
		check("Attributes(LogIntegral)", //
				"{Listable,NumericFunction,Protected}");
		check("LogIntegral(20.0)", //
				"9.9053");
		check("LogIntegral(Infinity)", //
				"Infinity");
		check("LogIntegral(ComplexInfinity)", //
				"ComplexInfinity");
		check("Table(LogIntegral(x), {x,1.0, 20.0, 2/3})", //
				"{-Infinity,0.489727,1.47827,2.16359,2.71938,3.20126,3.63459,4.03318,4.40548,4.75705,5.09177,5.41245,5.72124,6.01976,6.30934,6.59101,6.86564,7.13395,7.39655,7.65394,7.90657,8.15482,8.39903,8.63949,8.87646,9.11018,9.34083,9.56863,9.79372}");

	}

	public void testLogisticSigmoid() {
		check("D(LogisticSigmoid(x),x)", //
				"(1-LogisticSigmoid(x))*LogisticSigmoid(x)");
		check("LogisticSigmoid(Infinity)", //
				"1");
		check("LogisticSigmoid(-Infinity)", //
				"0");
		check("LogisticSigmoid(0)", //
				"1/2");

		checkNumeric("LogisticSigmoid(0.5)", //
				"0.6224593312018546");
		checkNumeric("LogisticSigmoid(0.5 + 2.3*I)", //
				"1.0647505893884985+I*0.8081774171575826");
		checkNumeric("LogisticSigmoid({-0.2, 0.1, 0.3})", //
				"{0.45016600268752216,0.52497918747894,0.574442516811659}");
		check("LogisticSigmoid(I*Pi)", //
				"LogisticSigmoid(I*Pi)");
		checkNumeric("LogisticSigmoid(0.5 + 2.3*I)", //
				"1.0647505893884985+I*0.8081774171575826");
		checkNumeric("LogisticSigmoid({-0.2, 0.1, 0.3})", //
				"{0.45016600268752216,0.52497918747894,0.574442516811659}");
	}

	public void testLowerTriangularize() {
		check("LowerTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}}, -1)", //
				"{{0,0,0,0},\n" + " {d,0,0,0},\n" + " {h,i,0,0},\n" + " {l,m,n,0}}");
		check("LowerTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}})", //
				"{{a,0,0,0},\n" + " {d,e,0,0},\n" + " {h,i,j,0}}");

		check("LowerTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}})", //
				"{{a,0,0,0},\n" + " {d,e,0,0},\n" + " {h,i,j,0},\n" + " {l,m,n,o}}");

	}

	public void testLucasL() {
		check("LucasL(1+I/2)//N", //
				"0.0653384+I*0.755095");
		check("LucasL(1,0)", //
				"0");
		check("LucasL(0,0)", //
				"2");
		check("LucasL(-1, x)", //
				"-x");
		check("LucasL(-10, x)", //
				"2+25*x^2+50*x^4+35*x^6+10*x^8+x^10");
		check("LucasL(-11, x)", //
				"-11*x-55*x^3-77*x^5-44*x^7-11*x^9-x^11");
		check("LucasL(50, x)", //
				"2+625*x^2+32500*x^4+672750*x^6+7400250*x^8+50075025*x^10+227613750*x^12+\n"
						+ "736618125*x^14+1767883500*x^16+3241119750*x^18+4639918800*x^20+5272635000*x^22+\n"
						+ "4814145000*x^24+3562467300*x^26+2148789800*x^28+1059575660*x^30+427248250*x^32+\n"
						+ "140512125*x^34+37469900*x^36+8021650*x^38+1357510*x^40+177375*x^42+17250*x^44+\n"
						+ "1175*x^46+50*x^48+x^50");
		check("Table(LucasL(n, x), {n, 5})", //
				"{x,2+x^2,3*x+x^3,2+4*x^2+x^4,5*x+5*x^3+x^5}");

		check("LucasL(-11)", //
				"-199");
		check("LucasL(-12)", //
				"322");
		check("Table(LucasL(n), {n, 20})", //
				"{1,3,4,7,11,18,29,47,76,123,199,322,521,843,1364,2207,3571,5778,9349,15127}");
		check("LucasL(1000)", //
				"9719417773590817520798198207932647373779787915534568508272808108477251881844481\\\n"
						+ "5269080619149045968297679578305403209347401163036907660573971740862463751801641\\\n"
						+ "201490284097309096322681531675707666695323797578127");
	}

	public void testMachineNumberQ() {
		// TODO use ExprParser#getReal() if apfloat problems are fixed
		// check("MachineNumberQ(3.14159265358979324)", "False");
		// check("MachineNumberQ(2.71828182845904524 + 3.14159265358979324*I)",
		// "False");
		// check("MachineNumberQ(1.5 + 3.14159265358979324*I)", "False");
		check("MachineNumberQ(1.5 + 2.3*I)", //
				"True");
		check("MachineNumberQ(1.5 + 5 *I)", //
				"True");
	}

	public void testMangoldtLambda() {
		check("MangoldtLambda(3^5)", //
				"Log(3)");
		check("MangoldtLambda({1,2,3,4,5,6,7,8,9})", //
				"{0,Log(2),Log(3),Log(2),Log(5),0,Log(7),Log(2),Log(3)}");
		check("{MangoldtLambda(Prime(10^5)^10), MangoldtLambda(2*Prime(10^5)^10)}", //
				"{Log(1299709),0}");
	}

	public void testManhattanDistance() {
		check("ManhattanDistance({-1, -1}, {1, 1})", //
				"4");
	}

	public void testMap() {
		check("Map(f, {{{{a}}}}, -2)", //
				"{f({f({f({a})})})}");

		check("Map(List,Join({1,2,3},4-{1,2,3}))", //
				"{{1},{2},{3},{3},{2},{1}}");
		check("Map(f, {{{{{a}}}}}, 2)", //
				"{f({f({{{a}}})})}");
		check("Map(f, {{{{{a}}}}}, {2})", //
				"{{f({{{a}}})}}");
		check("Map(f, {{{{{a}}}}}, {0,2})", //
				"f({f({f({{{a}}})})})");
		check("Map(f, {{{{{a}}}}}, Infinity)", //
				"{f({f({f({f({f(a)})})})})}");
		check("Map(f, {{{{{a}}}}}, {0, Infinity})", //
				"f({f({f({f({f({f(a)})})})})})");
		check("Map(f, {{{{{a}}}}}, 3)", //
				"{f({f({f({{a}})})})}");
		check("Map(f, {{{{{a}}}}}, Infinity)", //
				"{f({f({f({f({f(a)})})})})}");
		check("Map(f, {{{{{a}}}}}, {0, Infinity})", //
				"f({f({f({f({f({f(a)})})})})})");

		check("Map(f, {{{{{a}}}}}, {2, -3})", //
				"{{f({f({{a}})})}}");
		check("Map(f, h0(h1(h2(h3(h4(a))))), {2, -3})", //
				"h0(h1(f(h2(f(h3(h4(a)))))))");
		check("Map(f, {{{{a}}}}, 2, Heads -> True)", //
				"f(List)[f(f(List)[f({{a}})])]");

		check("Map(f, {a, b, c})", //
				"{f(a),f(b),f(c)}");
		check("Map(f, {a, b, c}, Heads -> True)", //
				"f(List)[f(a),f(b),f(c)]");

		check("f /@ {1, 2, 3}", //
				"{f(1),f(2),f(3)}");
		check("#^2& /@ {1, 2, 3, 4}", //
				"{1,4,9,16}");
		check("Map(f, {{a, b}, {c, d, e}}, {2})", //
				"{{f(a),f(b)},{f(c),f(d),f(e)}}");
		check("Map(f, a + b + c, Heads->True)", //
				"f(Plus)[f(a),f(b),f(c)]");
		check("Map(f, expr, a+b, Heads->True)", //
				"Map(f,expr,a+b,Heads->True)");
		check("Map(f, {{{{a}}}}, -1)", //
				"{f({f({f({f(a)})})})}");
		check("Map(f, {{{{a}}}}, -2)", //
				"{f({f({f({a})})})}");
		check("Map(f, {{{{a}}}}, -3)", //
				"{f({f({{a}})})}");
		check("Map(f, {{{{a}}}}, -4)", //
				"{f({{{a}}})}");
		check("Map(f, {{{{a}}}}, -5)", //
				"{{{{a}}}}");
		check("Map(f, {{{{a}}}}, -6)", //
				"{{{{a}}}}");
	}

	public void testMapAt() {
		check("MapAt(f, a+b+c+d, 2)", //
				"a+c+d+f(b)");
		check("MapAt(f, {a, b, c, d}, 0)", //
				"f(List)[a,b,c,d]");

		check("MapAt(f, {a, b, c, d}, -2)", //
				"{a,b,f(c),d}");
		check("MapAt(f, {a, b, c, d}, -5)", //
				"f(List)[a,b,c,d]");
		check("MapAt(f, {a, b, c, d}, -4)", //
				"{f(a),b,c,d}");

		// print message:
		check("MapAt(f, {a, b, c, d}, -6)", //
				"MapAt(f,{a,b,c,d},-6)");
		check("MapAt(f, {a, b, c, d}, 2)", //
				"{a,f(b),c,d}");
	}

	public void testMapIndexed() {
		check("MapIndexed(f, {{{{a, b}}}}, -3)", //
				"{f({f({{a,b}},{1,1})},{1})}");
		check("MapIndexed(f, {{{{a, b},{c, d}}}}, -2)", //
				"{f({f({f({a,b},{1,1,1}),f({c,d},{1,1,2})},{1,1})},{1})}");
		check("MapIndexed(f, {{{{a}}}}, -5)", //
				"{{{{a}}}}");
		check("MapIndexed(f, {{{{a}}}}, -4)", //
				"{f({{{a}}},{1})}");
		check("MapIndexed(f, {{{{a}}}}, -3)", //
				"{f({f({{a}},{1,1})},{1})}");
		check("MapIndexed(f, {{{{a}}}}, -2)", //
				"{f({f({f({a},{1,1,1})},{1,1})},{1})}");
		check("MapIndexed(f, {{{{a}}}}, -1)", //
				"{f({f({f({f(a,{1,1,1,1})},{1,1,1})},{1,1})},{1})}");

		check("MapIndexed(f, {{a}, {c}}, {2})", //
				"{{f(a,{1,1})},{f(c,{2,1})}}");

		check("MapIndexed(f, {a, b})", //
				"{f(a,{1}),f(b,{2})}");
		check("MapIndexed(f, {{a, b}, {c, d, e}})", //
				"{f({a,b},{1}),f({c,d,e},{2})}");
		check("MapIndexed(f, {}, 1)", //
				"{}");
		check("MapIndexed(f, {a, b}, 1)", //
				"{f(a,{1}),f(b,{2})}");
		check("MapIndexed(f, {a, b}, 0)", //
				"{a,b}");
		check("MapIndexed(f, {}, 0)", //
				"{}");
		check("MapIndexed(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 2)", //
				"{f({f({{a,b},{c,d}},{1,1})},{1}),f({f({{u,v},{s,t}},{2,1})},{2})}");
		check("MapIndexed(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 3)", //
				"{f({f({f({a,b},{1,1,1}),f({c,d},{1,1,2})},{1,1})},{1}),f({f({f({u,v},{2,1,1}),f({s,t},{\n"
						+ "2,1,2})},{2,1})},{2})}");
		check("MapIndexed(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 4)", //
				"{f({f({f({f(a,{1,1,1,1}),f(b,{1,1,1,2})},{1,1,1}),f({f(c,{1,1,2,1}),f(d,{1,1,2,2})},{\n"
						+ "1,1,2})},{1,1})},{1}),f({f({f({f(u,{2,1,1,1}),f(v,{2,1,1,2})},{2,1,1}),f({f(s,{2,\n"
						+ "1,2,1}),f(t,{2,1,2,2})},{2,1,2})},{2,1})},{2})}");
		check("MapIndexed(f, {{{a, b}, {c, d}}, {{u, v}, {s, t}}}, 2)", //
				"{f({f({a,b},{1,1}),f({c,d},{1,2})},{1}),f({f({u,v},{2,1}),f({s,t},{2,2})},{2})}");
		check("MapIndexed(f, {{a, b, c}, {x, y, z}})", //
				"{f({a,b,c},{1}),f({x,y,z},{2})}");
		check("MapIndexed(First(#2) + f(#1) &, {a, b, c, d})", //
				"{1+f(a),2+f(b),3+f(c),4+f(d)}");
		check("MapIndexed(f, {{a, b}, {c, d, e}}, {1})", //
				"{f({a,b},{1}),f({c,d,e},{2})}");
		check("MapIndexed(f, {{a, b}, {c, d, e}}, {2})", //
				"{{f(a,{1,1}),f(b,{1,2})},{f(c,{2,1}),f(d,{2,2}),f(e,{2,3})}}");
		check("MapIndexed(f, {{a, b}, {c, d, e}}, {3})", //
				"{{a,b},{c,d,e}}");

	}

	public void testMapThread() {
		check("MapThread(f)[ {{a, b, c}, {x, y, z}}]", //
				"{f(a,x),f(b,y),f(c,z)}");
		check("MapThread(f, {}, 1)", //
				"{}");
		check("MapThread(f, {a, b}, 1)", //
				"MapThread(f,{a,b},1)");
		check("MapThread(f, {a, b}, 0)", //
				"f(a,b)");
		check("MapThread(f, {}, 0)", //
				"f()");
		check("MapThread(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 2)", //
				"{{f({a,b},{u,v}),f({c,d},{s,t})}}");
		check("MapThread(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 3)", //
				"{{{f(a,u),f(b,v)},{f(c,s),f(d,t)}}}");
		check("MapThread(f, {{{{a, b}, {c, d}}}, {{{u, v}, {s, t}}}}, 4)", //
				"MapThread(f,{{{{a,b},{c,d}}},{{{u,v},{s,t}}}},4)");
		check("MapThread(f, {{{a, b}, {c, d}}, {{u, v}, {s, t}}}, 2)", //
				"{{f(a,u),f(b,v)},{f(c,s),f(d,t)}}");
		check("MapThread(f, {{a, b, c}, {x, y, z}})", //
				"{f(a,x),f(b,y),f(c,z)}");

		check("MapThread(f, {{a, b, c}, {1, 2, 3}})", //
				"{f(a,1),f(b,2),f(c,3)}");
	}

	public void testMatchingDissimilarity() {
		check("MatchingDissimilarity({1, 0, 1, 1, 0, 1, 1}, {0, 1, 1, 0, 0, 0, 1})", //
				"4/7");
		check("MatchingDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
				"3/5");
		check("MatchingDissimilarity({True, False, True}, {True, True, False})", //
				"2/3");
		check("MatchingDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
				"0");
		check("MatchingDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
				"1");
	}

	public void testMatchQ() {
		check("MatchQ(Sin(Cos(x)), HoldPattern(F_(G_(v_))) /; F==Sin&&G==Cos&&v==x )", //
				"True");

		check("MatchQ({x*(Sqrt(2*Pi*x)/(x!))^(1/x),x->Infinity}, {x_*(Sqrt(2*Pi*x_)/(x_!))^(1/x_), x_->Infinity})", //
				"True");
		// TODO
		check("MatchQ({a}, {a, ___})", //
				"True");
		check("MatchQ(2*x, c1_Integer*a_Symbol)", //
				"True");
		check("MatchQ(a + b, x_Symbol + x_Symbol)", //
				"False");
		check("MatchQ({2^a, a}, {2^x_Symbol, x_Symbol})", //
				"True");
		check("MatchQ({2^a, b}, {2^x_Symbol, x_Symbol})", //
				"False");

		check("MatchQ({a, b}, {a, __})", //
				"True");
		check("MatchQ({a}, {a, __})", //
				"False");

		check("MatchQ(1,_Integer)", //
				"True");
		check("MatchQ(_Symbol, _Symbol)", //
				"False");
		check("MatchQ(_Symbol, _Blank)", //
				"True");
		check("MatchQ(_Symbol, test_Blank)", //
				"True");
		check("MatchQ(name_Symbol, test_Pattern)", //
				"True");
		check("MatchQ(_Symbol, s)", //
				"False");
		check("MatchQ(1.5,_Integer)", //
				"False");
		check("MatchQ(1.5,_Real)", //
				"True");

		check("MatchQ(f(2*I), f(Complex(i_Integer, r_Integer)) )", //
				"True");
		check("MatchQ(g(1/2), g(Rational(n_Integer, d_Integer)) )", //
				"True");

		check("MatchQ(Simplify(1 + 1/GoldenRatio - GoldenRatio), 0)", //
				"True");

		check("MatchQ(Sin(Cos(x)), HoldPattern(F_(G_(v_))) /; F==Sin&&G==Cos&&v==x )", //
				"True");
		check("MatchQ(Sin(x*y), HoldPattern(F_(G_(v_))) /; Print(F,G,v) )", //
				"False");

		check("MatchQ(Sin(Cos(x)), HoldPattern(F_(G_(v_))))", //
				"True");

		check("MatchQ(Sin(3*y),Sin(u_*v_) /; IntegerQ(u))", //
				"True");
		check("MatchQ(123, _Integer)", //
				"True");
		check("MatchQ(123, _Real)", //
				"False");

		check("MatchQ((-1-1*#^2-3*#)&, (a_.+c_.*#^2+b_.* #)&)", //
				"True");
		check("MatchQ(#-1*#^2, b_.* #+c_.*#^2)", //
				"True");

		check("MatchQ(_Integer)[123]", //
				"True");
		check("MatchQ(22/7, _Rational)", //
				"True");
		check("MatchQ(6/3, _Rational)", //
				"False");

		check("MatchQ(22/7, _Rational)", //
				"True");
		check("MatchQ(b*x,a_.+x^n_.*b_./;FreeQ({a,b,n},x))", //
				"True");
		check("MatchQ(x,a_.+x^n_.*b_./;FreeQ({a,b,n},x))", //
				"True");
	}

	public void testMathMLForm() {
		check("MathMLForm( f(#,#3)&  )", //
				"<?xml version=\"1.0\"?>\n"
						+ "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
						+ "<math mode=\"display\">\n"
						+ "<mrow><mrow><mi>f</mi><mo>&#x2061;</mo><mrow><mo>(</mo><mrow><mi>#1</mi><mo>,</mo><mi>#3</mi></mrow><mo>)</mo></mrow></mrow><mo>&amp;</mo></mrow></math>");
		check("MathMLForm(D(sin(x)*cos(x),x))", "<?xml version=\"1.0\"?>\n"
				+ "<!DOCTYPE math PUBLIC \"-//W3C//DTD MathML 2.0//EN\" \"http://www.w3.org/TR/MathML2/dtd/mathml2.dtd\">\n"
				+ "<math mode=\"display\">\n"
				+ "<mfrac><mrow><mo>&#x2202;</mo><mrow><mrow><mi>sin</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow><mo>&#0183;</mo><mrow><mi>cos</mi><mo>&#x2061;</mo><mo>(</mo><mi>x</mi><mo>)</mo></mrow></mrow></mrow><mrow><mo>&#x2202;</mo><mi>x</mi></mrow></mfrac></math>");
	}

	public void testMatrices() {
		check("Table(a(i0, j), {i0, 2}, {j, 2})", //
				"{{a(1,1),a(1,2)},{a(2,1),a(2,2)}}");
		check("Array(a, {2, 2})", //
				"{{a(1,1),a(1,2)},{a(2,1),a(2,2)}}");
		check("ConstantArray(0, {3, 2})", //
				"{{0,0},{0,0},{0,0}}");
		check("DiagonalMatrix({a, b, c})", //
				"{{a,0,0},\n" + " {0,b,0},\n" + " {0,0,c}}");
		check("IdentityMatrix(3)", //
				"{{1,0,0},\n" + " {0,1,0},\n" + " {0,0,1}}");
	}

	public void testMatrixExp() {
		// check("MatrixExp({{1.2, 5.6}, {3, 4}})", //
		// "");
		// check("MatrixExp({{0, 1}, {-1, 0}}*t)", //
		// "");
	}

	public void testMatrixMinimalPolynomial() {
		// wikipedia
		check("MatrixMinimalPolynomial({{1, -1, -1}, {1, -2, 1}, {0, 1, -3}}, x)", //
				"-1+x+4*x^2+x^3");

		check("MatrixMinimalPolynomial({{2, 0}, {0, 2}}, x)", //
				"-2+x");
		check("MatrixMinimalPolynomial({{3, -1, 0}, {0, 2, 0}, {1, -1, 2}}, x)", //
				"6-5*x+x^2");
		check("CharacteristicPolynomial({{3, -1, 0}, {0, 2, 0}, {1, -1, 2}}, x)", //
				"12-16*x+7*x^2-x^3");
		check("Factor(6-5*x+x^2)", //
				"(-3+x)*(-2+x)");
		check("Factor(12-16*x+7*x^2-x^3)", //
				"(2-x)^2*(3-x)");
	}

	public void testMatrixPower() {
		// github #121 - print error
		check("MatrixPower({{2},{1}},2)", //
				"MatrixPower({{2},{1}},2)");

		check("MatrixPower({{1, 2}, {2, 5}}, -3)", //
				"{{169,-70},\n" + " {-70,29}}");
		check("MatrixPower({{1, 2}, {1, 1}}, 10)", //
				"{{3363,4756},\n" + " {2378,3363}}");
		check("MatrixPower({{1, 0}, {0}}, 2)", //
				"MatrixPower({{1,0},{0}},2)");
	}

	public void testMatrixQ() {
		check("MatrixQ( )", //
				"MatrixQ()");
		check("MatrixQ({})", //
				"False");
		check("MatrixQ({{}})", //
				"True");
		check("MatrixQ({{}}, NumberQ)", //
				"True");
		check("MatrixQ({{a, b, f}, {c, d, e}})", //
				"True");
		check("MatrixQ({{1, 3}, {4.0, 3/2}}, NumberQ)", //
				"True");
	}

	public void testMatrixRank() {
		check("MatrixRank({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}})", //
				"3");

		check("MatrixRank({{a, b}, {3*a, 3*b}})", //
				"1");

		check("MatrixRank({{1, 0}, {0}})", //
				"MatrixRank({{1,0},{0}})");

		check("MatrixRank({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
				"2");

		check("MatrixRank({{1, 0}, {3, 2}, {7, 2}, {8, 1}})", //
				"2");

		check("MatrixRank({{a, b}, {c, d}})", //
				"2");

		check("MatrixRank({{a, b}, {2*a, 2*b}})", //
				"1");

		check("MatrixRank({{1., 2., 3.}, {4., 5., 6.}, {7., 8., 9.}})", //
				"2");

		check("MatrixRank({{1, I}, {I, -1}})", //
				"1");

		check("MatrixRank({{1, 2, 3, 4.0 },\n" + //
				"{ 1, 1, 1, 1 },\n" + //
				"{ 2, 3, 4, 5 },\n" + //
				"{ 2, 2, 2, 2 }})", //
				"2");

		check("MatrixRank({{ 1.0, 2.0, 3.0, 4.0 },\n" + //
				"{ 1.0, 1.0, 1.0, 1.0 },\n" + //
				"{ 2.0, 3.0, 4.0, 5.0 },\n" + //
				"{ 2.0, 2.0, 2.0, 2.0 }})", //
				"2");
	}

	public void testMax() {
		check("Max(Interval({1,2}))", //
				"2");

		check("Refine(Max(Infinity,x), x>0)", //
				"Infinity");
		check("Refine(Max(Infinity,x,y), x>0&&y>0)", //
				"Infinity");
		check("Refine(Max(Infinity,x,y), x>0)", //
				"Max(Infinity,y)");
		check("Refine(Max(x,Infinity), x>0)", //
				"Infinity");
		check("Refine(Max(x,y,Infinity), x>0&&y>0)", //
				"Infinity");

		check("Refine(Max(-Infinity,x), x>0)", //
				"x");
		check("Refine(Max(-Infinity,x,y), x>0&&y>0)", //
				"Max(x,y)");
		check("Refine(Max(x,-Infinity), x>0)", //
				"x");
		check("Refine(Max(x,y,-Infinity), x>0&&y>0)", //
				"Max(x,y)");
		check("Refine(Max(x,y,-Infinity), x>0)", //
				"Max(x,y)");

		check("Max(4, -8, 1)", //
				"4");
		check("Max({1,2},3,{-3,3.5,-Infinity},{{1/2}})", //
				"3.5");
		check("Max(x, y)", //
				"Max(x,y)");
		check("Max(5, x, -3, y, 40)", //
				"Max(40,x,y)");
		check("Max()", //
				"-Infinity");
		check("Max(x)", "x");
		check("Max(Abs(x), Abs(y))", //
				"Max(Abs(x),Abs(y))");
	}

	public void testMaxFilter() {
		check("MaxFilter({1, 2, 3, 2, 1}, 1)", //
				"{2,3,3,3,2}");
		check("MaxFilter({0, 3, 8, 2}, 1)", //
				"{3,8,8,8}");
		check("MaxFilter({a,b,c}, 1)", //
				"{Max(a,b),Max(a,b,c),Max(b,c)}");
	}

	public void testMaximize() {
		check("Maximize(-x^4-7*x^3+2*x^2 - 42,x)", //
				"{-42-7*(-21/8-Sqrt(505)/8)^3+2*(21/8+Sqrt(505)/8)^2-(21/8+Sqrt(505)/8)^4,{x->-21/\n"
						+ "8-Sqrt(505)/8}}");
		check("Maximize(x^4+7*Tan(x)-2*x^2 + 42, x)", //
				"Maximize(42-2*x^2+x^4+7*Tan(x),x)");
		check("Maximize(x^4+7*x^3-2*x^2 + 42, x)", //
				"{Infinity,{x->-Infinity}}");
		check("Maximize(-2*x^2 - 3*x + 5, x)", //
				"{49/8,{x->-3/4}}");
	}

	public void testMean() {
		check("Mean({26, 64, 36})", //
				"42");
		check("Mean({1, 1, 2, 3, 5, 8})", //
				"10/3");
		check("Mean({a, b})", //
				"1/2*(a+b)");

		check("Mean({{a, u}, {b, v}, {c, w}})", //
				"{1/3*(a+b+c),1/3*(u+v+w)}");
		check("Mean({1.21, 3.4, 2.15, 4, 1.55})", //
				"2.462");
		check("Mean({a,b,c,d})", //
				"1/4*(a+b+c+d)");

		check("Mean(BetaDistribution(a,b))", //
				"a/(a+b)");
		check("Mean(BernoulliDistribution(p))", //
				"p");
		check("Mean(BinomialDistribution(n, m))", //
				"m*n");
		check("Mean(ExponentialDistribution(n))", "1/n");
		check("Mean(PoissonDistribution(p))", //
				"p");
		check("Mean(BinomialDistribution(n, p))", //
				"n*p");
		check("Mean(DiscreteUniformDistribution({l, r}))", //
				"1/2*(l+r)");
		check("Mean(ErlangDistribution(n, m))", //
				"n/m");
		check("Mean(LogNormalDistribution(m,s))", //
				"E^(m+s^2/2)");
		check("Mean(NakagamiDistribution(n, m))", //
				"(Sqrt(m)*Pochhammer(n,1/2))/Sqrt(n)");
		check("Mean(NormalDistribution(n, p))", //
				"n");
		check("Mean(FrechetDistribution(n, m))", //
				"Piecewise({{m*Gamma(1-1/n),1<n}},Infinity)");
		check("Mean(GammaDistribution(n, m))", //
				"m*n");
		check("Mean(GeometricDistribution(n))", //
				"-1+1/n");
		check("Mean(GumbelDistribution(n, m))", //
				"-EulerGamma*m+n");
		check("Mean(HypergeometricDistribution(n, ns, nt))", //
				"(n*ns)/nt");
		check("Mean(StudentTDistribution(4))", //
				"0");
		check("Mean(StudentTDistribution(n))", //
				"Piecewise({{0,n>1}},Indeterminate)");
		check("Mean(WeibullDistribution(n, m))", //
				"m*Gamma(1+1/n)");
	}

	public void testMeanFilter() {
		check("MeanFilter({-3, 3, 6, 0, 0, 3, -3, -9}, 2)", //
				"{2,3/2,6/5,12/5,6/5,-9/5,-9/4,-3}");
		check("MeanFilter({1, 2, 3, 2, 1}, 1)", //
				"{3/2,2,7/3,2,3/2}");
		check("MeanFilter({0, 3, 8, 2}, 1)", //
				"{3/2,11/3,13/3,5}");
		check("MeanFilter({a,b,c}, 1)", //
				"{1/2*(a+b),1/3*(a+b+c),1/2*(b+c)}");
	}

	public void testMeanDeviation() {
		check("MeanDeviation({a, b, c})", //
				"1/3*(Abs(a+1/3*(-a-b-c))+Abs(b+1/3*(-a-b-c))+Abs(1/3*(-a-b-c)+c))");
		check("MeanDeviation({{1, 2}, {4, 8}, {5, 3}, {2, 15}})", //
				"{3/2,9/2}");
		check("MeanDeviation({1, 2, 3, 7})", //
				"15/8");
		check("MeanDeviation({Pi, E, 2})//Together", //
				"1/9*(-8+2*E+2*Pi)");
	}

	public void testMedian() {
		check("Median({{100, 1, 10, 50}, {-1, 1, -2, 2}})", //
				"{99/2,1,4,26}");

		check("Median({26, 64, 36})", //
				"36");
		check("Median({-11, 38, 501, 1183})", //
				"539/2");
		check("Median({{100, 1, 10, 50}, {-1, 1, -2, 2}})", //
				"{99/2,1,4,26}");

		check("Median({1,2,3,4,5,6,7.0})", //
				"4.0");
		check("Median({1,2,3,4,5,6,7.0,8})", //
				"4.5");
		check("Median({1,2,3,4,5,6,7})", //
				"4");

		check("Median(BernoulliDistribution(p))", //
				"Piecewise({{1,p>1/2}},0)");
		check("Median(BetaDistribution(a,b))", //
				"InverseBetaRegularized(1/2,a,b)");
		check("Median(BinomialDistribution(n, m))", //
				"Median(BinomialDistribution(n,m))");
		check("Median(ExponentialDistribution(n))", //
				"Log(2)/n");
		check("Median(PoissonDistribution(p))", //
				"Median(PoissonDistribution(p))");
		check("Median(DiscreteUniformDistribution({l, r}))", //
				"-1+l+Max(1,Ceiling(1/2*(1-l+r)))");
		check("Median(UniformDistribution({l, r}))", //
				"1/2*(l+r)");
		check("Median(ErlangDistribution(n, m))", //
				"InverseGammaRegularized(n,0,1/2)/m");
		check("Median(LogNormalDistribution(m,s))", //
				"E^m");
		check("Median(NakagamiDistribution(n, m))", //
				"Sqrt((m*InverseGammaRegularized(n,0,1/2))/n)");
		check("Median(NormalDistribution())", //
				"0");
		check("Median(NormalDistribution(n, p))", //
				"n");
		check("Median(FrechetDistribution(n, m))", //
				"m/Log(2)^(1/n)");
		check("Median(GammaDistribution(n, m))", //
				"m*InverseGammaRegularized(n,0,1/2)");
		check("Median(GammaDistribution(a,b,g,d))", //
				"d+b*InverseGammaRegularized(a,1/2)^(1/g)");
		check("Median(GeometricDistribution(n))", //
				"Median(GeometricDistribution(n))");
		check("Median(GumbelDistribution( ))", //
				"Log(Log(2))");
		check("Median(GumbelDistribution(n, m))", //
				"n+m*Log(Log(2))");
		check("Median(HypergeometricDistribution(n, ns, nt))", //
				"Median(HypergeometricDistribution(n,ns,nt))");
		check("Median(StudentTDistribution(4))", //
				"0");
		check("Median(StudentTDistribution(n))", //
				"0");
		check("Median(StudentTDistribution(m,s,v))", //
				"m");
		check("Median(WeibullDistribution(a, b))", //
				"b*Log(2)^(1/a)");
		check("Median(WeibullDistribution(a, b, m))", //
				"m+b*Log(2)^(1/a)");
	}

	public void testMedianFilter() {
		// Wikipedia example with "shrinking the window near the boundaries"
		check("MedianFilter({2,3,80,6}, 1)", //
				"{5/2,3,6,43}");

		check("MedianFilter({1, 2, 3, 2, 1}, 1)", //
				"{3/2,2,2,2,3/2}");
		check("MedianFilter({0, 3, 8, 2}, 1)", //
				"{3/2,3,3,5}");
		check("MedianFilter({a,b,c}, 1)", //
				"{1/2*(a+b),b,1/2*(b+c)}");
	}

	public void testMeijerG() {
		check("MeijerG({{}, {0.33}}, {{Pi}, {E}}, {-0.5,0.5})", //
				"{(0.0864683+I*0.0412186)*Hypergeometric1F1Regularized(3.81159,1.42331,-0.5),-0.0957901*Hypergeometric1F1Regularized(3.81159,1.42331,0.5)}");

		check("MeijerG({{}, {a2}}, {{b1}, {b2}}, z)", //
				"(z^b1*Hypergeometric1F1Regularized(1-a2+b1,1+b1-b2,z))/Gamma(a2-b1)");
		check("MeijerG({{a1}, {}}, {{b1}, {b2}}, z)", //
				"z^b1*Gamma(1-a1+b1)*Hypergeometric1F1Regularized(1-a1+b1,1+b1-b2,-z)");
		check("MeijerG({{a1}, {a2}}, {{b1}, {}}, z)", //
				"(Gamma(1-a1+b1)*Hypergeometric1F1Regularized(1-a1+b1,1-a1+a2,-1/z))/z^(1-a1)");
		check("MeijerG({{a1}, {a2}}, {{}, {b2}}, z)", //
				"Hypergeometric1F1Regularized(1-a1+b2,1-a1+a2,1/z)/(z^(1-a1)*Gamma(a1-b2))");
		check("MeijerG({{}, {}}, {{b1}, {-b1}}, z)", //
				"BesselJ(2*b1,2*Sqrt(z))");
		check("MeijerG({{a1}, {a2}}, {{}, {}}, z)", //
				"BesselJ(-a1+a2,2/Sqrt(z))/z^(1-a1-(-a1+a2)/2)");
		check("MeijerG({{a}, {}}, {{}, {b}}, 2)", //
				"2^b/Gamma(a-b)");
		check("MeijerG({{a}, {}}, {{}, {}}, z) ", //
				"1/(E^(1/z)*z^(1-a))");
		// print message
		check("MeijerG({{}, {}}, {{}, {}}, z) ", //
				"MeijerG({{},{}},{{},{}},z)");
	}

	public void testMemberQ() {
		check("MemberQ({Sin, Cos, Tan, Cot, Sec, Csc}, If(AtomQ(Cos),Cos,Head(Cos)))", //
				"True");
		check("MemberQ(x,x)", //
				"False");
		check("MemberQ({{x^2, y^2}}, x^_)", //
				"False");
		check("MemberQ({{x^2, y^2}}, x^_, 2)", //
				"True");
		check("MemberQ({{1, 1, 3, 0}, {2, 1, 2, 2}}, 0)", //
				"False");
		check("MemberQ({{1, 1, 3, 0}, {2, 1, 2, 2}}, 0, 2)", //
				"True");
		check("MemberQ({a, b, c}, b)", //
				"True");
		check("MemberQ({a, b, c}, d)", //
				"False");
		check("MemberQ({\"a\", b, f(x)}, _?NumericQ)", //
				"False");
		check("MemberQ({\"a\", 42, f(x)}, _?NumericQ)", //
				"True");
		check("MemberQ(_List)[{{}}]", //
				"True");

		check("MemberQ(x^_)[{x^2, y^2, x^3}]", //
				"True");
		check("MemberQ({1, 3, 4, 1, 2}, 2)", //
				"True");
		check("MemberQ({x^2, y^2, x^3}, x^_)", //
				"True");
		check("MemberQ(a + b + f(c), f)", //
				"False");
		check("MemberQ(a + b + f(c), f, Heads->True)", //
				"False");
		check("MemberQ(a + b + c, a + c)", //
				"False");
	}

	public void testMersennePrimeExponent() {
		check("Table(MersennePrimeExponent(i), {i,20})",
				"{2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423}");
		check("MersennePrimeExponent(1)", //
				"2");
		check("MersennePrimeExponent(21)", //
				"9689");
		check("MersennePrimeExponent(22)", //
				"9941");
		check("MersennePrimeExponent(23)", //
				"11213");
		check("MersennePrimeExponent(44)", //
				"32582657");
		check("MersennePrimeExponent(45)", //
				"37156667");
		check("MersennePrimeExponent(47)", //
				"43112609");
	}

	public void testMersennePrimeExponentQ() {
		check("Select(Range(10000), MersennePrimeExponentQ)",
				"{2,3,5,7,13,17,19,31,61,89,107,127,521,607,1279,2203,2281,3217,4253,4423,9689,\n" + "9941}");
	}

	public void testMessage() {
		check("Message(f::argx, 1, 2)", //
				"f: 1 called with 2 arguments; 1 argument is expected.");
		check("a::b:=\"Hello world\"", //
				"");
		check("Message(a::b)", //
				"a: Hello world");
		check("a::c:=\"Hello `1`, Mr 00`2`!\"", //
				"");
		check("Message(a::c, \"you\", 3 + 4)", //
				"a: Hello you, Mr 007!");

		check("f::failure=\"`1` called with wrong argument; `2`, `3`.\"", //
				"`1` called with wrong argument; `2`, `3`.");
		check("Message(f::failure, f, x, y)", //
				"f: f called with wrong argument; x, y.");
		check("Message(Rule::argr, Rule, 2)", //
				"Rule: Rule called with 1 argument; 2 arguments are expected.");

	}

	public void testMessageName() {
		check("FullForm(a::b)", //
				"MessageName(a, b)");
		check("FullForm(a::\"b\")", //
				"MessageName(a, \"b\")");

		// Set[MessageName(f,"usage"),"A usage message")
		check("f::usage=\"A usage message\"", //
				"A usage message");
		// print "A usage message" on the console. Evaluation returns Null (i.e. no output)
		check("Information(f)", //
				"");
		check("Information(Sin)", //
				"");
		check("Information(Sin, LongForm->False)", //
				"");
		// print "A usage message" on the console. Evaluation returns Null (i.e. no output)
		check("?f", //
				"");
		check("??Sin;??Cos", //
				"");
	}

	public void testMin() {
		check("Min(Interval({1,2}))", //
				"1");

		check("Refine(Min(-Infinity,x), x>0)", //
				"-Infinity");
		check("Refine(Min(-Infinity,x,y), x>0&&y>0)", //
				"-Infinity");
		check("Refine(Min(-Infinity,x,y), x>0)", //
				"Min(y,-Infinity)");
		check("Refine(Min(x,-Infinity), x>0)", //
				"-Infinity");
		check("Refine(Min(x,y,-Infinity), x>0&&y>0)", //
				"-Infinity");

		check("Refine(Min(Infinity,x), x>0)", //
				"x");
		check("Refine(Min(Infinity,x,y), x>0&&y>0)", //
				"Min(x,y)");
		check("Refine(Min(x,Infinity), x>0)", //
				"x");
		check("Refine(Min(x,y,Infinity), x>0&&y>0)", //
				"Min(x,y)");
		check("Refine(Min(x,y,Infinity), x>0&&y>0)", //
				"Min(x,y)");

		check("Refine(Infinity<x, x>0)", //
				"False");

		check("Min(5, x, -3, y, 40)", //
				"Min(-3,x,y)");
		check("Min(4, -8, 1)", //
				"-8");
		check("Min({1,2},3,{-3,3.5,-Infinity},{{1/2}})", //
				"-Infinity");
		check("Min(x, y)", //
				"Min(x,y)");
		check("Min(5, x, -3, y, 40)", //
				"Min(-3,x,y)");
		check("Min()", //
				"Infinity");
		check("Min(x)", //
				"x");
		check("Min(Abs(x), Abs(y))", //
				"Min(Abs(x),Abs(y))");
	}

	public void testMinFilter() {
		check("MinFilter({1, 2, 3, 2, 1}, 1)", //
				"{1,1,2,1,1}");
		check("MinFilter({0, 3, 8, 2}, 1)", //
				"{0,0,2,2}");
		check("MinFilter({a,b,c}, 1)", //
				"{Min(a,b),Min(a,b,c),Min(b,c)}");
	}

	public void testMinimize() {
		// check("Minimize(Sin(x),x)", //
		// "");
		check("Minimize(x^4+7*x^3-2*x^2 + 42, x)", //
				"{42+7*(-21/8-Sqrt(505)/8)^3-2*(21/8+Sqrt(505)/8)^2+(21/8+Sqrt(505)/8)^4,{x->-21/\n"
						+ "8-Sqrt(505)/8}}");
		check("Minimize(2*x^2 - 3*x + 5, x)", //
				"{31/8,{x->3/4}}");
	}

	public void testMinus() {
		check("Minus(a)", //
				"-a");
		check("-a //FullForm", //
				"Times(-1, a)");
		check("-(x - 2/3)", //
				"2/3-x");
		check("-Range(10)", //
				"{-1,-2,-3,-4,-5,-6,-7,-8,-9,-10}");
	}

	public void testMissingQ() {
		check("MissingQ(Missing(\"Test message\"))", //
				"True");
	}

	public void testMod() {
		check("Mod(5-Pi/2,Pi)", //
				"5-3/2*Pi");
		check("Mod(Sqrt(-113), 2)", //
				"-I*10+I*Sqrt(113)");
		check("Mod(Exp(Pi), 2)", //
				"-22+E^Pi");
		check("Mod(42,Pi)", //
				"42-13*Pi");
		check("Mod(-42,Pi)", //
				"-42+14*Pi");
		check("Mod(-10,3)", //
				"2");
		check("Mod(10,3)", //
				"1");
		check("Mod(10,-3)", //
				"-2");
		check("Mod(-10,-3)", //
				"-1");

		check("Mod(-23,7)", //
				"5");
		check("Mod(23,7)", //
				"2");
		check("Mod(23,-7)", //
				"-5");
		check("Mod(-23,-7)", //
				"-2");

		check("Mod(14, 6)", //
				"2");
		check("Mod(-3,4)", //
				"1");
		check("Mod(-3,-4)", //
				"-3");
		check("Mod(2,-4)", //
				"-2");
		check("Mod(3,-4)", //
				"-1");
		check("Mod(5,0)", //
				"Indeterminate");
	}

	public void testModule() {
		// check("num=Sin(3*I);Module({v=N(num)},If(PossibleZeroQ(Re(v)),Im(v)>0,Re(v)>0))",
		// "True");
		// check("Module({x=5}, Hold(x))", "Hold(x$1)");

		check("xm=10;Module({xm=xm}, xm=xm+1;xm);xm", //
				"10");
		check("xm=10;Module({t=xm}, xm=xm+1;t)", //
				"10");
		check("xm=10;Module({t=xm}, xm=xm+1;t);xm", //
				"11");
		check("Module({a}, Block({a}, a))", //
				"a$4");
		check("Module({a}, Block({}, a))", //
				"a$5");
		check("t === Module({t}, t)", //
				"False");
		check("$g(x_) := Module({v=x},int(v,x)/;v=!=x);$g(f(x))", //
				"$g(f(x))");
		check("$g(x_) := Module({v=x},int1(v,x)/;v===x);$g(f(x))", //
				"int1(f(x),f(x))");
		check("$h(x_) := Module({$u}, $u^2 /; (($u = x - 1) > 0));$h(6)", //
				"25");
		checkNumeric("$f(x0_) :=\n" + " Module({x = x0},\n" + "  While(x > 0, x = Log(x));\n" + "  x\n" + "  );$f(2.0)", //
				"-0.36651292058166435");

		check("$fib(n_) :=\n" + " Module({$f},\n" + "  $f(1) = $f(2) = 1;\n"
				+ "  $f(i0_) := $f(i0) = $f(i0 - 1) + $f(i0 - 2);\n" + "  $f(n)\n" + "  );$fib(5)", //
				"5");

		check("$gcd(m0_, n0_) :=\n" + " Module({m = m0, n = n0},\n" + "  While(n != 0, {m, n} = {n, Mod(m, n)});\n"
				+ "  m\n" + "  );$gcd(18, 21)", //
				"3");

		check("{Module({x}, x), Module({x}, x)}", //
				"{x$14,x$15}");
		check("Module({e = Expand((1 + x)^5)}, Function(x, e))", //
				"Function(x$16,e$16)");
		check("Module({a,b}, Block({c}, c+a))", //
				"a$17+c");

		if (Config.SERVER_MODE == false) {
			check("f(x0_) :=\n" + " Module({x = x0},\n" + "  While(x > 0, x = Log(x));\n" + "  x\n" + "  );f(2.0)",
					"-0.366513");

			check("fib(n_) :=\n" + " Module({f},\n" + "  f(1) = f(2) = 1;\n"
					+ "  f(i_) := f(i) = f(i - 1) + f(i - 2);\n" + "  f(n)\n" + "  );fib(5)", "5");

			check("myGCD(m0_, n0_) :=\n" + " Module({m = m0, n = n0},\n" + "  While(n != 0, {m, n} = {n, Mod(m, n)});\n"
					+ "  m\n" + "  );myGCD(18, 21)", "3");
		}

		check("xm=10;Module({xm=xm}, xm=xm+1;Print(xm));xm", //
				"10");
		check("Module({var1=2*2,var2=var1}, {var1,var2})", //
				"{4,var1}");
		check("Module({x=y,y=z,z=3}, Print({Hold(x),Hold(y),Hold(z)});{x,y,z})", //
				"{y,z,3}");
		check("Module({x,f}, f(0)=0;f(x_):=f(x-1)+x;f(3))", //
				"6");
		check("Module({x},Function(y,x+y))", //
				"Function(y$25,x$25+y$25)");
		// check("Module({y},Function(y,x+y))",//
		// "x$22$23+y");

		check("Module({x}, g2(x_)=Integrate(Sqrt(1+z^2),{z,0,x}));Table(g2(i),{i,3})", //
				"{1/Sqrt(2)+ArcSinh(1)/2,Sqrt(5)+ArcSinh(2)/2,3*Sqrt(5/2)+ArcSinh(3)/2}");

		check("v=Null;Module({w=v},Catch(Scan(Function(If(False ,Throw(False))),u); w))", //
				"");
	}

	public void testMoebiusMu() {
		check("MoebiusMu(-30)", //
				"-1");
		check("FactorInteger(30)", //
				"{{2,1},{3,1},{5,1}}");
		check("MoebiusMu(30)", //
				"-1");
		check("Table(MoebiusMu(k), {k, 0,50})", //
				"{0,1,-1,-1,0,-1,1,-1,0,0,1,-1,0,-1,1,1,0,-1,0,-1,0,1,1,-1,0,0,1,0,0,-1,-1,-1,0,1,\n"
						+ "1,1,0,-1,1,1,0,-1,-1,-1,0,0,1,-1,0,0,0}");
		check("MoebiusMu({1000,10000})", //
				"{0,0}");
		check("MoebiusMu(-a)", //
				"MoebiusMu(a)");
		check("MoebiusMu(47)", //
				"-1");
		check("MoebiusMu(51)", //
				"1");
		check("MoebiusMu(17291)", //
				"-1");
		check("MoebiusMu({2, 4, 7, 9})", //
				"{-1,0,-1,0}");
		check("MoebiusMu(-100)", //
				"0");
		check("Table(MoebiusMu(k), {k, 12})", //
				"{1,-1,-1,0,-1,1,-1,0,0,1,-1,0}");
		check("Table(MoebiusMu(-k), {k, 12})", //
				"{1,-1,-1,0,-1,1,-1,0,0,1,-1,0}");
		check("FactorInteger(183245)", //
				"{{5,1},{67,1},{547,1}}");
		check("MoebiusMu(183245)", //
				"-1");
		check("MoebiusMu(210)", //
				"1");
		check("MoebiusMu(192)", //
				"0");
	}

	public void testMonomialList() {
		check("MonomialList(7*y^w, {y,z})", //
				"{7*y^w}");
		check("MonomialList(7*y^(3*w), y )", //
				"{7*y^(3*w)}");
		check("MonomialList(c*x^2+a+b*x,x)", //
				"{c*x^2,b*x,a}");
		check("MonomialList(c*x^(-2)+a+b^2*x,b)", //
				"{b^2*x,a+c/x^2}");
		check("MonomialList(c*x^(-2)+a+b*x,x)", //
				"{a+c/x^2+b*x}");

		check("MonomialList((x + y)^3)", //
				"{x^3,3*x^2*y,3*x*y^2,y^3}");
		check("MonomialList(x^2*y^2 + x^3, {x, y})", //
				"{x^3,x^2*y^2}");
		check("MonomialList(x^2*y^2 + x^3, {x, y},DegreeLexicographic)", //
				"{x^2*y^2,x^3}");
		check("MonomialList((x + 1)^5, x, Modulus -> 2)", //
				"{x^5,x^4,x,1}");

		check("MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z})", //
				"{-10*x^5*y^4*z^2,7*x^2*y^5*z^3,-10*x^2*y*z^5,-7*x*y^5*z^4,6*x*y^4*z^3,6*x*y^3*z^\n"
						+ "3,3*x*y^2*z,y^4*z,-7*y^2*z,2*z^5}");

		check("MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeLexicographic)", //
				"{2*z^5,-7*y^2*z,y^4*z,3*x*y^2*z,6*x*y^3*z^3,6*x*y^4*z^3,-7*x*y^5*z^4,-10*x^2*y*z^\n"
						+ "5,7*x^2*y^5*z^3,-10*x^5*y^4*z^2}");
		check("MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, DegreeLexicographic)", //
				"{-10*x^5*y^4*z^2,7*x^2*y^5*z^3,-7*x*y^5*z^4,-10*x^2*y*z^5,6*x*y^4*z^3,6*x*y^3*z^\n"
						+ "3,y^4*z,2*z^5,3*x*y^2*z,-7*y^2*z}");
		check("MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y *z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeDegreeReverseLexicographic)", //
				"{-7*y^2*z,3*x*y^2*z,y^4*z,2*z^5,6*x*y^3*z^3,6*x*y^4*z^3,-10*x^2*y*z^5,7*x^2*y^5*z^\n"
						+ "3,-7*x*y^5*z^4,-10*x^5*y^4*z^2}");
		check("MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, DegreeReverseLexicographic)", //
				"{-10*x^5*y^4*z^2,7*x^2*y^5*z^3,-7*x*y^5*z^4,6*x*y^4*z^3,-10*x^2*y*z^5,6*x*y^3*z^\n"
						+ "3,y^4*z,2*z^5,3*x*y^2*z,-7*y^2*z}");
		check("MonomialList(-10*x^5*y^4*z^2 + 7*x^2*y^5*z^3 - 10*x^2*y*z^5 - 7*x*y^5*z^4 +  6*x*y^4*z^3 + 6*x*y^3*z^3 + 3*x*y^2*z + y^4*z - 7*y^2*z + 2*z^5, {x, y, z}, NegativeDegreeLexicographic)", //
				"{-7*y^2*z,3*x*y^2*z,y^4*z,2*z^5,6*x*y^3*z^3,-10*x^2*y*z^5,6*x*y^4*z^3,7*x^2*y^5*z^\n"
						+ "3,-7*x*y^5*z^4,-10*x^5*y^4*z^2}");
	}

	public void testMost() {
		check("Most({a, b, c})", "{a,b}");
		check("Most(a + b + c)", "a+b");
		check("Most(a)", "Most(a)");
	}

	public void testMultinomial() {
		check("Multinomial(1,1,1)", //
				"6");
		check("Multinomial(1,k,1)", //
				"(1+k)*(2+k)");
		check("Multinomial(10,f(x),2,3,4)", //
				"116396280*Binomial(19+f(x),f(x))");

		check("Multinomial(0,0,0,0,0)", //
				"1");
		check("Multinomial(a,b)", //
				"Binomial(a+b,b)");
		check("Multinomial(2, 3, 4, 5)", //
				"2522520");
		check("Multinomial( )", //
				"1");
		check("Multinomial(1)", //
				"1");
		check("Multinomial(2, 3)", //
				"10");
		check("Multinomial(f(x))", //
				"1");
		check("Multinomial(f(x), g(x))", //
				"Binomial(f(x)+g(x),g(x))");
		check("Multinomial(n-k, k)", //
				"Binomial(n,-k+n)");
		check("Multinomial(k, 2)", //
				"1/2*(1+k)*(2+k)");
	}

	public void testMultiplicativeOrder() {
		// https://oeis.org/A023394
		check("Select(Prime(Range(500)), IntegerQ(Log(2, MultiplicativeOrder(2, # )))&) ", //
				"{3,5,17,257,641}");

		check("Select(Range(2,200), MultiplicativeOrder(10, # )== # - 1 &)", //
				"{7,17,19,23,29,47,59,61,97,109,113,131,149,167,179,181,193}");

		check("MultiplicativeOrder(7, 108)", //
				"18");
		check("MultiplicativeOrder(10^100 + 1, Prime(1000))", //
				"3959");
		check("MultiplicativeOrder(-5, 7)", //
				"3");

		check("Select(Range(43), MultiplicativeOrder(#, 43) == EulerPhi(43) &)", //
				"{3,5,12,18,19,20,26,28,29,30,33,34}");
	}

	public void testN() {
		// TODO don't switch to numeric mode for Sqrt(10)
		check("(0.25)^x", //
				"0.25^x");

		// github #151
		check("N(Integrate(Sin(x*Pi/3), {x, 1, 2}))", //
				"0.95493");
		check("expr=Integrate(Sin(x*Pi/3), {x, 1, 2}); N(expr)", //
				"0.95493");

		// TODO don't switch to numeric mode for Sqrt(10)
		check("Sqrt(10)*(0.25)^x", //
				"0.25^x*Sqrt(10)");

		check("{1, 2} /. x_Integer -> N(x)", //
				"{1,2}");
		check("{1, 2} /. x_Integer :> N(x)", //
				"{1.0,2.0}");
		// check("N(Pi)", "3.141592653589793");
		// check("N(Pi, 50)", "3.1415926535897932384626433832795028841971693993751");
		// check("N(1/7)", "0.14285714285714285");
		// check("N(1/7, 20)", "1.4285714285714285714e-1");
	}

	public void testNames() {
		check("Names(\"Int*\" )",
				"{Interval,IntegerDigits,IntegerExponent,IntegerLength,IntegerPart,IntegerPartitions,IntegerQ,Integrate,Interpolation,InterpolatingFunction,InterpolatingPolynomial,IntersectingQ,Interrupt,Intersection,Integer,Integers}");
		check("Names(\"Integer*\" )",
				"{IntegerDigits,IntegerExponent,IntegerLength,IntegerPart,IntegerPartitions,IntegerQ,Integer,Integers}");
		check("Names(\"IntegerPart\" )", "{IntegerPart}");
	}

	public void testNand() {
		check("Nand( )", //
				"False");
		check("Nand(a)", //
				"!a");
		check("Nand(2+2)", //
				"!4");
		check("Nand(x,y,z)", //
				"Nand(x,y,z)");
		check("Nand(x,True,z)", //
				"Nand(x,z)");
		check("Nand(x,False,z)", //
				"True");
		check("Nand(True,False)", //
				"True");
		check("Nand(False, True)", //
				"True");
		check("Nand(Print(1); False, Print(2); True)", //
				"True");
		check("Nand(Print(1); True, Print(2); True)", //
				"False");
	}

	public void testNDSolve() {

		check("NDSolve({ y(x)*Cos(x + y(x))== (y'(x)), y(0)==1}, y, {x, 0, 30})", //
				"InterpolatingFunction(\n" + "{{0.0,1.04865},\n" + " {0.1,1.08548},\n" + " {0.2,1.10965},\n"
						+ " {0.3,1.12126},\n" + " {0.4,1.12111},\n" + " {0.5,1.11044},\n" + " {0.6,1.09069},\n"
						+ " {0.7,1.06335},\n" + " {0.8,1.02981},\n" + " {0.9,0.991387},\n" + " {1.0,0.949215},\n"
						+ " {1.1,0.904299},\n" + " {1.2,0.857506},\n" + " {1.3,0.809582},\n" + " {1.4,0.761165},\n"
						+ " {1.5,0.712805},\n" + " {1.6,0.664971},\n" + " {1.7,0.618066},\n" + " {1.8,0.572431},\n"
						+ " {1.9,0.528358},\n" + " {2.0,0.486087},\n" + " {2.1,0.445817},\n" + " {2.2,0.407704},\n"
						+ " {2.3,0.371865},\n" + " {2.4,0.338377},\n" + " {2.5,0.307285},\n" + " {2.6,0.278597},\n"
						+ " {2.7,0.252292},\n" + " {2.8,0.228319},\n" + " {2.9,0.206604},\n" + " {3.0,0.187051},\n"
						+ " {3.1,0.169549},\n" + " {3.2,0.153976},\n" + " {3.3,0.1402},\n" + " {3.4,0.128089},\n"
						+ " {3.5,0.117511},\n" + " {3.6,0.108337},\n" + " {3.7,0.100445},\n" + " {3.8,0.0937238},\n"
						+ " {3.9,0.0880718},\n" + " {4.0,0.0833997},\n" + " {4.1,0.0796308},\n" + " {4.2,0.0767018},\n"
						+ " {4.3,0.0745631},\n" + " {4.4,0.0731789},\n" + " {4.5,0.0725273},\n" + " {4.6,0.0726007},\n"
						+ " {4.7,0.0734058},\n" + " {4.8,0.0749641},\n" + " {4.9,0.0773122},\n" + " {5.0,0.0805022},\n"
						+ " {5.1,0.084602},\n" + " {5.2,0.0896949},\n" + " {5.3,0.0958795},\n" + " {5.4,0.103268},\n"
						+ " {5.5,0.111982},\n" + " {5.6,0.122152},\n" + " {5.7,0.133903},\n" + " {5.8,0.14735},\n"
						+ " {5.9,0.16258},\n" + " {6.0,0.179634},\n" + " {6.1,0.198484},\n" + " {6.2,0.219009},\n"
						+ " {6.3,0.240973},\n" + " {6.4,0.264005},\n" + " {6.5,0.287594},\n" + " {6.6,0.311101},\n"
						+ " {6.7,0.333787},\n" + " {6.8,0.354865},\n" + " {6.9,0.373561},\n" + " {7.0,0.389182},\n"
						+ " {7.1,0.401173},\n" + " {7.2,0.409157},\n" + " {7.3,0.412955},\n" + " {7.4,0.412579},\n"
						+ " {7.5,0.408208},\n" + " {7.6,0.400162},\n" + " {7.7,0.388856},\n" + " {7.8,0.374769},\n"
						+ " {7.9,0.358411},\n" + " {8.0,0.340294},\n" + " {8.1,0.320916},\n" + " {8.2,0.30074},\n"
						+ " {8.3,0.280193},\n" + " {8.4,0.259648},\n" + " {8.5,0.239432},\n" + " {8.6,0.219816},\n"
						+ " {8.7,0.201021},\n" + " {8.8,0.183217},\n" + " {8.9,0.166528},\n" + " {9.0,0.151036},\n"
						+ " {9.1,0.136784},\n" + " {9.2,0.123784},\n" + " {9.3,0.112018},\n" + " {9.4,0.10145},\n"
						+ " {9.5,0.0920238},\n" + " {9.6,0.0836749},\n" + " {9.7,0.0763301},\n" + " {9.8,0.0699134},\n"
						+ " {9.9,0.064349},\n" + " {10.0,0.0595635},\n" + " {10.1,0.0554881},\n" + " {10.2,0.05206},\n"
						+ " {10.3,0.0492234},\n" + " {10.4,0.0469303},\n" + " {10.5,0.0451403},\n"
						+ " {10.6,0.0438216},\n" + " {10.7,0.0429506},\n" + " {10.8,0.0425119},\n"
						+ " {10.9,0.0424987},\n" + " {11.0,0.0429126},\n" + " {11.1,0.0437635},\n"
						+ " {11.2,0.0450704},\n" + " {11.3,0.0468609},\n" + " {11.4,0.0491715},\n"
						+ " {11.5,0.0520479},\n" + " {11.6,0.0555442},\n" + " {11.7,0.0597228},\n"
						+ " {11.8,0.0646526},\n" + " {11.9,0.0704073},\n" + " {12.0,0.0770622},\n"
						+ " {12.1,0.0846894},\n" + " {12.2,0.093351},\n" + " {12.3,0.103092},\n" + " {12.4,0.113928},\n"
						+ " {12.5,0.125836},\n" + " {12.6,0.138741},\n" + " {12.7,0.152503},\n" + " {12.8,0.166913},\n"
						+ " {12.9,0.181682},\n" + " {13.0,0.196454},\n" + " {13.1,0.210812},\n" + " {13.2,0.224302},\n"
						+ " {13.3,0.236465},\n" + " {13.4,0.246869},\n" + " {13.5,0.255139},\n" + " {13.6,0.260991},\n"
						+ " {13.7,0.264247},\n" + " {13.8,0.264839},\n" + " {13.9,0.262814},\n" + " {14.0,0.258315},\n"
						+ " {14.1,0.251568},\n" + " {14.2,0.242859},\n" + " {14.3,0.232512},\n" + " {14.4,0.22087},\n"
						+ " {14.5,0.20828},\n" + " {14.6,0.195074},\n" + " {14.7,0.18156},\n" + " {14.8,0.168015},\n"
						+ " {14.9,0.154679},\n" + " {15.0,0.141755},\n" + " {15.1,0.129401},\n" + " {15.2,0.117741},\n"
						+ " {15.3,0.106861},\n" + " {15.4,0.0968124},\n" + " {15.5,0.0876215},\n"
						+ " {15.6,0.0792885},\n" + " {15.7,0.0717953},\n" + " {15.8,0.0651088},\n"
						+ " {15.9,0.0591859},\n" + " {16.0,0.0539769},\n" + " {16.1,0.0494287},\n"
						+ " {16.2,0.0454875},\n" + " {16.3,0.0421012},\n" + " {16.4,0.0392205},\n"
						+ " {16.5,0.0368005},\n" + " {16.6,0.0348011},\n" + " {16.7,0.0331878},\n"
						+ " {16.8,0.0319318},\n" + " {16.9,0.0310103},\n" + " {17.0,0.0304063},\n"
						+ " {17.1,0.0301088},\n" + " {17.2,0.0301128},\n" + " {17.3,0.0304193},\n"
						+ " {17.4,0.0310353},\n" + " {17.5,0.031974},\n" + " {17.6,0.0332549},\n"
						+ " {17.7,0.0349035},\n" + " {17.8,0.0369517},\n" + " {17.9,0.0394377},\n"
						+ " {18.0,0.0424049},\n" + " {18.1,0.0459019},\n" + " {18.2,0.0499805},\n"
						+ " {18.3,0.0546941},\n" + " {18.4,0.0600942},\n" + " {18.5,0.0662268},\n"
						+ " {18.6,0.0731268},\n" + " {18.7,0.0808116},\n" + " {18.8,0.0892737},\n"
						+ " {18.9,0.0984725},\n" + " {19.0,0.108327},\n" + " {19.1,0.118707},\n" + " {19.2,0.129434},\n"
						+ " {19.3,0.140277},\n" + " {19.4,0.15096},\n" + " {19.5,0.161175},\n" + " {19.6,0.170594},\n"
						+ " {19.7,0.178897},\n" + " {19.8,0.18579},\n" + " {19.9,0.191026},\n" + " {20.0,0.194427},\n"
						+ " {20.1,0.195889},\n" + " {20.2,0.195391},\n" + " {20.3,0.192991},\n" + " {20.4,0.188814},\n"
						+ " {20.5,0.183043},\n" + " {20.6,0.175906},\n" + " {20.7,0.167652},\n" + " {20.8,0.158545},\n"
						+ " {20.9,0.148847},\n" + " {21.0,0.138806},\n" + " {21.1,0.128651},\n" + " {21.2,0.118581},\n"
						+ " {21.3,0.108768},\n" + " {21.4,0.0993505},\n" + " {21.5,0.0904343},\n"
						+ " {21.6,0.082096},\n" + " {21.7,0.0743846},\n" + " {21.8,0.067325},\n" + " {21.9,0.060922},\n"
						+ " {22.0,0.0551642},\n" + " {22.1,0.0500277},\n" + " {22.2,0.0454801},\n"
						+ " {22.3,0.0414834},\n" + " {22.4,0.0379967},\n" + " {22.5,0.0349785},\n"
						+ " {22.6,0.0323884},\n" + " {22.7,0.0301882},\n" + " {22.8,0.0283432},\n"
						+ " {22.9,0.0268225},\n" + " {23.0,0.0255995},\n" + " {23.1,0.024652},\n"
						+ " {23.2,0.0239628},\n" + " {23.3,0.0235187},\n" + " {23.4,0.0233117},\n"
						+ " {23.5,0.0233382},\n" + " {23.6,0.0235993},\n" + " {23.7,0.0241006},\n"
						+ " {23.8,0.0248528},\n" + " {23.9,0.025871},\n" + " {24.0,0.0271755},\n"
						+ " {24.1,0.0287912},\n" + " {24.2,0.0307477},\n" + " {24.3,0.0330788},\n"
						+ " {24.4,0.0358222},\n" + " {24.5,0.039018},\n" + " {24.6,0.0427076},\n"
						+ " {24.7,0.0469309},\n" + " {24.8,0.0517239},\n" + " {24.9,0.0571141},\n"
						+ " {25.0,0.0631163},\n" + " {25.1,0.0697266},\n" + " {25.2,0.0769169},\n"
						+ " {25.3,0.0846285},\n" + " {25.4,0.0927679},\n" + " {25.5,0.101203},\n"
						+ " {25.6,0.109762},\n" + " {25.7,0.118239},\n" + " {25.8,0.1264},\n" + " {25.9,0.133994},\n"
						+ " {26.0,0.140769},\n" + " {26.1,0.146488},\n" + " {26.2,0.150947},\n" + " {26.3,0.153989},\n"
						+ " {26.4,0.155513},\n" + " {26.5,0.15548},\n" + " {26.6,0.153917},\n" + " {26.7,0.150905},\n"
						+ " {26.8,0.146578},\n" + " {26.9,0.141107},\n" + " {27.0,0.134688},\n" + " {27.1,0.127534},\n"
						+ " {27.2,0.119856},\n" + " {27.3,0.11186},\n" + " {27.4,0.103738},\n" + " {27.5,0.0956582},\n"
						+ " {27.6,0.0877652},\n" + " {27.7,0.0801771},\n" + " {27.8,0.0729854},\n"
						+ " {27.9,0.0662557},\n" + " {28.0,0.0600306},\n" + " {28.1,0.0543322},\n"
						+ " {28.2,0.0491655},\n" + " {28.3,0.0445218},\n" + " {28.4,0.040382},\n"
						+ " {28.5,0.0367198},\n" + " {28.6,0.0335042},\n" + " {28.7,0.0307017},\n"
						+ " {28.8,0.0282787},\n" + " {28.9,0.0262021},\n" + " {29.0,0.0244411},\n"
						+ " {29.1,0.0229675},\n" + " {29.2,0.0217563},\n" + " {29.3,0.0207862},\n"
						+ " {29.4,0.0200394},\n" + " {29.5,0.0195021},\n" + " {29.6,0.0191641},\n"
						+ " {29.7,0.0190191},\n" + " {29.8,0.0190646},\n" + " {29.9,0.0193018}})");

		// checkNumeric("NDSolve({ y(x)*Cos(x + y(x))== (y'(x)), y(0)==1}, y, {x, 0, 30})", //
		// "InterpolatingFunction(\n"
		// + "{{0.0,1.0486539247435627},\n" + " {0.1,1.0854808036771377},\n" + " {0.2,1.1096485558561129},\n"
		// + " {0.30000000000000004,1.1212578599391958},\n" + " {0.4,1.1211098516670583},\n"
		// + " {0.5,1.110440971080707},\n" + " {0.6,1.0906930188004873},\n" + " {0.7,1.0633460402333474},\n"
		// + " {0.7999999999999999,1.0298136072918775},\n" + " {0.8999999999999999,0.991387315006244},\n"
		// + " {0.9999999999999999,0.9492149070968016},\n" + " {1.0999999999999999,0.9042988579035411},\n"
		// + " {1.2,0.857505923891753},\n" + " {1.3,0.8095815052617051},\n"
		// + " {1.4000000000000001,0.7611651420556339},\n" + " {1.5000000000000002,0.7128051392128717},\n"
		// + " {1.6000000000000003,0.6649713621384138},\n" + " {1.7000000000000004,0.6180658664467911},\n"
		// + " {1.8000000000000005,0.5724313779772732},\n" + " {1.9000000000000006,0.5283578293741383},\n"
		// + " {2.0000000000000004,0.48608725600029157},\n" + " {2.1000000000000005,0.4458173974209415},\n"
		// + " {2.2000000000000006,0.4077043633624414},\n" + " {2.3000000000000007,0.37186471548866024},\n"
		// + " {2.400000000000001,0.3383772924103512},\n" + " {2.500000000000001,0.3072850658079258},\n"
		// + " {2.600000000000001,0.2785972606942865},\n" + " {2.700000000000001,0.2522919043406724},\n"
		// + " {2.800000000000001,0.22831889029854097},\n" + " {2.9000000000000012,0.20660356283726058},\n"
		// + " {3.0000000000000013,0.18705075124931517},\n" + " {3.1000000000000014,0.1695491213425783},\n"
		// + " {3.2000000000000015,0.15397566999707005},\n" + " {3.3000000000000016,0.14020017181526293},\n"
		// + " {3.4000000000000017,0.1280893946838693},\n" + " {3.5000000000000018,0.11751092979804098},\n"
		// + " {3.600000000000002,0.10833652492796243},\n" + " {3.700000000000002,0.10044485973079281},\n"
		// + " {3.800000000000002,0.09372375133539376},\n" + " {3.900000000000002,0.08807182139220295},\n"
		// + " {4.000000000000002,0.0833996886046539},\n" + " {4.100000000000001,0.07963077199182245},\n"
		// + " {4.200000000000001,0.07670180014927495},\n" + " {4.300000000000001,0.07456312212288695},\n"
		// + " {4.4,0.07317890821085377},\n" + " {4.5,0.07252731596246077},\n" + " {4.6,0.0726006791932994},\n"
		// + " {4.699999999999999,0.0734057565099308},\n" + " {4.799999999999999,0.07496405020983263},\n"
		// + " {4.899999999999999,0.07731217510263196},\n" + " {4.999999999999998,0.08050221749315989},\n"
		// + " {5.099999999999998,0.084601974272093},\n" + " {5.1999999999999975,0.08969489746733304},\n"
		// + " {5.299999999999997,0.0958794879078716},\n" + " {5.399999999999997,0.10326778201284252},\n"
		// + " {5.4999999999999964,0.11198246184539966},\n" + " {5.599999999999996,0.12215200271817592},\n"
		// + " {5.699999999999996,0.13390318126361409},\n" + " {5.799999999999995,0.14735024731166288},\n"
		// + " {5.899999999999995,0.16258018854901138},\n" + " {5.999999999999995,0.17963388536831357},\n"
		// + " {6.099999999999994,0.19848366765525352},\n" + " {6.199999999999994,0.21900890659080618},\n"
		// + " {6.299999999999994,0.24097273961477084},\n" + " {6.399999999999993,0.2640045482014827},\n"
		// + " {6.499999999999993,0.2875938184944301},\n" + " {6.5999999999999925,0.311100756322707},\n"
		// + " {6.699999999999992,0.33378687199758955},\n" + " {6.799999999999992,0.35486468555987505},\n"
		// + " {6.8999999999999915,0.37356070170896716},\n" + " {6.999999999999991,0.38918166156437567},\n"
		// + " {7.099999999999991,0.401172598399326},\n" + " {7.19999999999999,0.4091571693969712},\n"
		// + " {7.29999999999999,0.4129553215557175},\n" + " {7.39999999999999,0.41257870289475035},\n"
		// + " {7.499999999999989,0.4082084528387488},\n" + " {7.599999999999989,0.40016206914955754},\n"
		// + " {7.699999999999989,0.38885599961989714},\n" + " {7.799999999999988,0.37476918269614945},\n"
		// + " {7.899999999999988,0.3584108421644447},\n" + " {7.999999999999988,0.34029407839355436},\n"
		// + " {8.099999999999987,0.3209155086689302},\n" + " {8.199999999999987,0.30074044115694204},\n"
		// + " {8.299999999999986,0.2801927248235081},\n" + " {8.399999999999986,0.2596483599261049},\n"
		// + " {8.499999999999986,0.23943205150206048},\n" + " {8.599999999999985,0.21981604663931104},\n"
		// + " {8.699999999999985,0.20102075567302183},\n" + " {8.799999999999985,0.18321678667386834},\n"
		// + " {8.899999999999984,0.1665281105852},\n" + " {8.999999999999984,0.15103612290345214},\n"
		// + " {9.099999999999984,0.136784386073114},\n" + " {9.199999999999983,0.12378383728408018},\n"
		// + " {9.299999999999983,0.11201824175272579},\n" + " {9.399999999999983,0.10144967223258593},\n"
		// + " {9.499999999999982,0.09202380805440191},\n" + " {9.599999999999982,0.08367487371122105},\n"
		// + " {9.699999999999982,0.07633007619745596},\n" + " {9.799999999999981,0.06991344748031084},\n"
		// + " {9.89999999999998,0.06434904783208746},\n" + " {9.99999999999998,0.05956353167221671},\n"
		// + " {10.09999999999998,0.055488115733649444},\n" + " {10.19999999999998,0.052060017293655024},\n"
		// + " {10.29999999999998,0.0492234472683428},\n" + " {10.399999999999979,0.04693025003153322},\n"
		// + " {10.499999999999979,0.04514028068683145},\n" + " {10.599999999999978,0.043821603367067995},\n"
		// + " {10.699999999999978,0.042950583025463014},\n" + " {10.799999999999978,0.04251192976987961},\n"
		// + " {10.899999999999977,0.04249874014945849},\n" + " {10.999999999999977,0.042912564384982844},\n"
		// + " {11.099999999999977,0.043763512208115654},\n" + " {11.199999999999976,0.045070392103282626},\n"
		// + " {11.299999999999976,0.046860858299487156},\n" + " {11.399999999999975,0.049171515562746765},\n"
		// + " {11.499999999999975,0.052047902387254485},\n" + " {11.599999999999975,0.05554423757203267},\n"
		// + " {11.699999999999974,0.059722773229538936},\n" + " {11.799999999999974,0.0646525504791017},\n"
		// + " {11.899999999999974,0.07040730672504374},\n" + " {11.999999999999973,0.0770622441521745},\n"
		// + " {12.099999999999973,0.0846893525976165},\n" + " {12.199999999999973,0.09335100838707118},\n"
		// + " {12.299999999999972,0.10309167339440412},\n" + " {12.399999999999972,0.11392772849437467},\n"
		// + " {12.499999999999972,0.1258358199938911},\n" + " {12.599999999999971,0.138740580936534},\n"
		// + " {12.69999999999997,0.15250316894309834},\n" + " {12.79999999999997,0.16691262572026747},\n"
		// + " {12.89999999999997,0.18168242049828567},\n" + " {12.99999999999997,0.19645445463107747},\n"
		// + " {13.09999999999997,0.2108120803325641},\n" + " {13.199999999999969,0.22430228748955305},\n"
		// + " {13.299999999999969,0.23646536721975198},\n" + " {13.399999999999968,0.24686856012190703},\n"
		// + " {13.499999999999968,0.25513903798118154},\n" + " {13.599999999999968,0.26099149103034053},\n"
		// + " {13.699999999999967,0.26424665962609567},\n" + " {13.799999999999967,0.26483900018855316},\n"
		// + " {13.899999999999967,0.26281370586093794},\n" + " {13.999999999999966,0.2583149394045044},\n"
		// + " {14.099999999999966,0.2515680519994532},\n" + " {14.199999999999966,0.24285873137419975},\n"
		// + " {14.299999999999965,0.2325116382150581},\n" + " {14.399999999999965,0.220870422513877},\n"
		// + " {14.499999999999964,0.20828029902084735},\n" + " {14.599999999999964,0.19507375354460899},\n"
		// + " {14.699999999999964,0.1815595106786453},\n" + " {14.799999999999963,0.16801461631765874},\n"
		// + " {14.899999999999963,0.1546793400273539},\n" + " {14.999999999999963,0.14175453929296386},\n"
		// + " {15.099999999999962,0.12940111187045478},\n" + " {15.199999999999962,0.11774116741799541},\n"
		// + " {15.299999999999962,0.10686056081330114},\n" + " {15.399999999999961,0.09681244213729613},\n"
		// + " {15.499999999999961,0.08762149329437179},\n" + " {15.59999999999996,0.07928854218346183},\n"
		// + " {15.69999999999996,0.07179527544171443},\n" + " {15.79999999999996,0.06510881121842255},\n"
		// + " {15.89999999999996,0.05918594276300758},\n" + " {15.99999999999996,0.053976918199585305},\n"
		// + " {16.09999999999996,0.049428676909066306},\n" + " {16.19999999999996,0.04548751365173981},\n"
		// + " {16.29999999999996,0.042101184130222284},\n" + " {16.399999999999963,0.03922049785649547},\n"
		// + " {16.499999999999964,0.03680046534374525},\n" + " {16.599999999999966,0.03480107758305633},\n"
		// + " {16.699999999999967,0.0331877982037784},\n" + " {16.79999999999997,0.03193184479148035},\n"
		// + " {16.89999999999997,0.03101032767125873},\n" + " {16.99999999999997,0.030406303862182932},\n"
		// + " {17.099999999999973,0.03010879219599716},\n" + " {17.199999999999974,0.030112783566147657},\n"
		// + " {17.299999999999976,0.030419268228300628},\n" + " {17.399999999999977,0.03103528987978064},\n"
		// + " {17.49999999999998,0.031974023436000365},\n" + " {17.59999999999998,0.03325485929623045},\n"
		// + " {17.69999999999998,0.034903460631464545},\n" + " {17.799999999999983,0.036951741050493675},\n"
		// + " {17.899999999999984,0.03943768737279855},\n" + " {17.999999999999986,0.042404926190700305},\n"
		// + " {18.099999999999987,0.04590190449334592},\n" + " {18.19999999999999,0.049980526549024254},\n"
		// + " {18.29999999999999,0.054694066601254296},\n" + " {18.39999999999999,0.06009416801910799},\n"
		// + " {18.499999999999993,0.0662267563624869},\n" + " {18.599999999999994,0.07312675184871771},\n"
		// + " {18.699999999999996,0.08081158301761861},\n" + " {18.799999999999997,0.0892736922498442},\n"
		// + " {18.9,0.09847248889531121},\n" + " {19.0,0.10832652953813567},\n" + " {19.1,0.11870703751609235},\n"
		// + " {19.200000000000003,0.12943412780911714},\n" + " {19.300000000000004,0.1402771628385532},\n"
		// + " {19.400000000000006,0.15096041573160082},\n" + " {19.500000000000007,0.16117459959331792},\n"
		// + " {19.60000000000001,0.1705938815306118},\n" + " {19.70000000000001,0.17889692028539034},\n"
		// + " {19.80000000000001,0.18578953051456235},\n" + " {19.900000000000013,0.19102607653255335},\n"
		// + " {20.000000000000014,0.19442681148735713},\n" + " {20.100000000000016,0.19588908551204787},\n"
		// + " {20.200000000000017,0.19539143760805033},\n" + " {20.30000000000002,0.19299074958941248},\n"
		// + " {20.40000000000002,0.188813589734067},\n" + " {20.50000000000002,0.18304344050912236},\n"
		// + " {20.600000000000023,0.1759056646182722},\n" + " {20.700000000000024,0.1676519017849422},\n"
		// + " {20.800000000000026,0.15854523818283722},\n" + " {20.900000000000027,0.14884707766919583},\n"
		// + " {21.00000000000003,0.13880625822789994},\n" + " {21.10000000000003,0.12865064403209947},\n"
		// + " {21.20000000000003,0.11858119433695793},\n" + " {21.300000000000033,0.10876835547392645},\n"
		// + " {21.400000000000034,0.09935052412493195},\n" + " {21.500000000000036,0.09043427196672443},\n"
		// + " {21.600000000000037,0.08209599140343246},\n" + " {21.70000000000004,0.07438461231024102},\n"
		// + " {21.80000000000004,0.06732504714496039},\n" + " {21.90000000000004,0.0609220448460617},\n"
		// + " {22.000000000000043,0.05516417096178315},\n" + " {22.100000000000044,0.05002767965231217},\n"
		// + " {22.200000000000045,0.045480098453626217},\n" + " {22.300000000000047,0.04148340402665843},\n"
		// + " {22.40000000000005,0.03799672158222671},\n" + " {22.50000000000005,0.03497852815326329},\n"
		// + " {22.60000000000005,0.032388377671356924},\n" + " {22.700000000000053,0.030188192781695093},\n"
		// + " {22.800000000000054,0.02834318482532842},\n" + " {22.900000000000055,0.026822470808221865},\n"
		// + " {23.000000000000057,0.025599456420398434},\n" + " {23.10000000000006,0.02465204936600566},\n"
		// + " {23.20000000000006,0.023962759329861603},\n" + " {23.30000000000006,0.023518731365109482},\n"
		// + " {23.400000000000063,0.023311749398506023},\n" + " {23.500000000000064,0.02333823650818959},\n"
		// + " {23.600000000000065,0.02359926881772662},\n" + " {23.700000000000067,0.024100610123988526},\n"
		// + " {23.800000000000068,0.02485276434553134},\n" + " {23.90000000000007,0.025871031998059613},\n"
		// + " {24.00000000000007,0.027175544570715412},\n" + " {24.100000000000072,0.02879123634710425},\n"
		// + " {24.200000000000074,0.030747696564390598},\n" + " {24.300000000000075,0.03307882595701917},\n"
		// + " {24.400000000000077,0.035822201576675955},\n" + " {24.500000000000078,0.03901803437743281},\n"
		// + " {24.60000000000008,0.04270758911304822},\n" + " {24.70000000000008,0.046930931420902475},\n"
		// + " {24.800000000000082,0.051723880711205865},\n" + " {24.900000000000084,0.05711408982254241},\n"
		// + " {25.000000000000085,0.06311625424637678},\n" + " {25.100000000000087,0.06972658382886275},\n"
		// + " {25.200000000000088,0.07691685011526753},\n" + " {25.30000000000009,0.0846285415836176},\n"
		// + " {25.40000000000009,0.09276788599437955},\n" + " {25.500000000000092,0.10120268026019423},\n"
		// + " {25.600000000000094,0.10976193177974276},\n" + " {25.700000000000095,0.11823918766143336},\n"
		// + " {25.800000000000097,0.12640006310087093},\n" + " {25.900000000000098,0.13399389013035107},\n"
		// + " {26.0000000000001,0.14076868583650626},\n" + " {26.1000000000001,0.14648794906949023},\n"
		// + " {26.200000000000102,0.1509473266388399},\n" + " {26.300000000000104,0.15398909090144997},\n"
		// + " {26.400000000000105,0.15551268333713533},\n" + " {26.500000000000107,0.15548022163602201},\n"
		// + " {26.600000000000108,0.15391666739307647},\n" + " {26.70000000000011,0.15090510975131846},\n"
		// + " {26.80000000000011,0.14657818306953294},\n" + " {26.900000000000112,0.14110693216447398},\n"
		// + " {27.000000000000114,0.13468847749049695},\n" + " {27.100000000000115,0.1275336799610958},\n"
		// + " {27.200000000000117,0.11985574493622606},\n" + " {27.300000000000118,0.11186041177815408},\n"
		// + " {27.40000000000012,0.1037381005484644},\n" + " {27.50000000000012,0.09565815735831991},\n"
		// + " {27.600000000000122,0.08776516222812088},\n" + " {27.700000000000124,0.08017713485435946},\n"
		// + " {27.800000000000125,0.07298538709313364},\n" + " {27.900000000000126,0.06625571890495106},\n"
		// + " {28.000000000000128,0.06003063117527128},\n" + " {28.10000000000013,0.0543322298578955},\n"
		// + " {28.20000000000013,0.04916551743387415},\n" + " {28.300000000000132,0.04452180553916086},\n"
		// + " {28.400000000000134,0.04038203184386874},\n" + " {28.500000000000135,0.03671981933966998},\n"
		// + " {28.600000000000136,0.03350417158494679},\n" + " {28.700000000000138,0.03070174835662014},\n"
		// + " {28.80000000000014,0.02827870903992453},\n" + " {28.90000000000014,0.026202144002981702},\n"
		// + " {29.000000000000142,0.02444113673212715},\n" + " {29.100000000000144,0.0229675124577755},\n"
		// + " {29.200000000000145,0.02175633398892615},\n" + " {29.300000000000146,0.02078620446123},\n"
		// + " {29.400000000000148,0.02003943163828367},\n" + " {29.50000000000015,0.019502100971413434},\n"
		// + " {29.60000000000015,0.019164096103692605},\n" + " {29.700000000000152,0.01901909674905609},\n"
		// + " {29.800000000000153,0.019064575332356216},\n" + " {29.900000000000155,0.019301805546317868}})");

		// 10, 28, 8/3 as constants for the Lorenz equations
		// https://socialinnovationsimulation.com/2013/07/19/tutorial-differential-equations-2/
		check("NDSolve({x'(t) == 10*(y(t) - x(t)), \n"
				+ " y'(t) == x(t)*(28 - z(t)) - y(t), z'(t) == x(t)*y(t) - 8/3*z(t),\n"
				+ " x(0)== 0, y(0) == 1, z(0) == 0}, {x, y, z}, {t, 0, 20})",
				"InterpolatingFunction(\n" + "{{0.0,0.912039,2.09193,0.0624515},\n"
						+ " {0.1,3.05475,6.63831,0.767136},\n" + " {0.2,9.46273,19.37015,7.5869},\n"
						+ " {0.3,19.59444,23.40222,39.84913},\n" + " {0.4,9.81955,-6.62076,41.60318},\n"
						+ " {0.5,-2.04439,-8.94178,29.85607},\n" + " {0.6,-6.09749,-8.34167,26.23213},\n"
						+ " {0.7,-7.76133,-9.12044,25.46181},\n" + " {0.8,-8.95358,-9.90857,26.60081},\n"
						+ " {0.9,-9.44315,-9.3789,28.33779},\n" + " {1.0,-8.86073,-7.89266,28.67839},\n"
						+ " {1.1,-7.87718,-7.05609,27.35103},\n" + " {1.2,-7.45191,-7.47175,25.72583},\n"
						+ " {1.3,-7.9021,-8.72514,25.11763},\n" + " {1.4,-8.88391,-9.8687,26.1684},\n"
						+ " {1.5,-9.5194,-9.65551,28.11674},\n" + " {1.6,-9.0732,-8.13817,28.87241},\n"
						+ " {1.7,-8.0125,-7.02874,27.70426},\n" + " {1.8,-7.40054,-7.23626,25.92274},\n"
						+ " {1.9,-7.70908,-8.44952,24.99252},\n" + " {2.0,-8.69877,-9.79324,25.76637},\n"
						+ " {2.1,-9.5318,-9.91902,27.81475},\n" + " {2.2,-9.28378,-8.44474,29.01024},\n"
						+ " {2.3,-8.18726,-7.0562,28.08292},\n" + " {2.4,-7.38343,-7.01457,26.18567},\n"
						+ " {2.5,-7.52144,-8.14324,24.93205},\n" + " {2.6,-8.47925,-9.64481,25.3697},\n"
						+ " {2.7,-9.49142,-10.14219,27.42128},\n" + " {2.8,-9.48629,-8.80719,29.06665},\n"
						+ " {2.9,-8.40352,-7.15008,28.47473},\n" + " {3.0,-7.40816,-6.81619,26.51588},\n"
						+ " {3.1,-7.34726,-7.81402,24.94665},\n" + " {3.2,-8.23076,-9.42199,24.99878},\n"
						+ " {3.3,-9.39118,-10.30229,26.94321},\n" + " {3.4,-9.66605,-9.21563,29.01687},\n"
						+ " {3.5,-8.6595,-7.32349,28.86293},\n" + " {3.6,-7.48239,-6.65266,26.91311},\n"
						+ " {3.7,-7.19549,-7.47081,25.04583},\n" + " {3.8,-7.96063,-9.12716,24.67517},\n"
						+ " {3.9,-9.22657,-10.37681,26.39546},\n" + " {4.0,-9.80559,-9.65182,28.8363},\n"
						+ " {4.1,-8.94992,-7.58961,29.22414},\n" + " {4.2,-7.61396,-6.53809,27.37477},\n"
						+ " {4.3,-7.07616,-7.12378,25.23813},\n" + " {4.4,-7.67808,-8.76642,24.42064},\n"
						+ " {4.5,-8.99649,-10.34593,25.80142},\n" + " {4.6,-9.8855,-10.0877,28.5039},\n"
						+ " {4.7,-9.26452,-7.95971,29.52659},\n" + " {4.8,-7.81043,-6.49009,27.89475},\n"
						+ " {4.9,-7.00063,-6.78451,25.53105},\n" + " {5.0,-7.39411,-8.34924,24.25635},\n"
						+ " {5.1,-8.70394,-10.19507,25.19237},\n" + " {5.2,-9.88589,-10.48496,28.00686},\n"
						+ " {5.3,-9.58649,-8.4396,29.72869},\n" + " {5.4,-8.07814,-6.53088,28.46135},\n"
						+ " {5.5,-6.98192,-6.46665,25.93099},\n" + " {5.6,-7.12153,-7.88788,24.20234},\n"
						+ " {5.7,-8.35632,-9.91709,24.60593},\n" + " {5.8,-9.7887,-10.79683,27.34623},\n"
						+ " {5.9,-9.89088,-9.02421,29.7789},\n" + " {6.0,-8.42051,-6.688,29.05401},\n"
						+ " {6.1,-7.03503,-6.18714,26.44283},\n" + " {6.2,-6.87513,-7.39684,24.27727},\n"
						+ " {6.3,-7.96551,-9.51371,24.0837},\n" + " {6.4,-9.58046,-10.97265,26.54218},\n"
						+ " {6.5,-10.14386,-9.69005,29.61851},\n" + " {6.6,-8.83513,-6.99421,29.63833},\n"
						+ " {6.7,-7.1771,-5.9682,27.06898},\n" + " {6.8,-6.67222,-6.89253,24.49851},\n"
						+ " {6.9,-7.5476,-8.99568,23.66855},\n" + " {7.0,-9.2553,-10.96522,25.63739},\n"
						+ " {7.1,-10.30353,-10.38716,29.18948},\n" + " {7.2,-9.3093,-7.48453,30.15894},\n"
						+ " {7.3,-7.42711,-5.84066,27.80713},\n" + " {7.4,-6.53347,-6.39355,24.8825},\n"
						+ " {7.5,-7.12269,-8.38185,23.40228},\n" + " {7.6,-8.81734,-10.73971,24.69678},\n"
						+ " {7.7,-10.32357,-11.03357,28.4492},\n" + " {7.8,-9.81382,-8.18786,30.53108},\n"
						+ " {7.9,-7.80426,-5.84858,28.64545},\n" + " {8.0,-6.48438,-5.92204,25.44513},\n"
						+ " {8.1,-6.7148,-7.69725,23.32398},\n" + " {8.2,-8.28217,-10.28187,23.80279},\n"
						+ " {8.3,-10.16054,-11.51794,27.39154},\n" + " {8.4,-10.29577,-9.10891,30.63419},\n"
						+ " {8.5,-8.32365,-6.05488,29.55299},\n" + " {8.6,-6.55714,-5.50718,26.20158},\n"
						+ " {8.7,-6.35256,-6.97099,23.46984},\n" + " {8.8,-7.67671,-9.60279,23.04691},\n"
						+ " {8.9,-9.78433,-11.71596,26.06877},\n" + " {9.0,-10.67312,-10.19768,30.31662},\n"
						+ " {9.1,-8.98672,-6.54521,30.46111},\n" + " {9.2,-6.79256,-5.19232,27.16467},\n"
						+ " {9.3,-6.0712,-6.23476,23.87419},\n" + " {9.4,-7.03803,-8.7382,22.52007},\n"
						+ " {9.5,-9.18943,-11.52143,24.60355},\n" + " {9.6,-10.83727,-11.31189,29.42702},\n"
						+ " {9.7,-9.76292,-7.42089,31.23175},\n" + " {9.8,-7.24049,-5.04877,28.33828},\n"
						+ " {9.9,-5.91662,-5.52372,24.57196},\n" + " {10.0,-6.41207,-7.74242,22.30522},\n"
						+ " {10.1,-8.40273,-10.88284,23.17997},\n" + " {10.2,-10.67186,-12.19688,27.88667},\n"
						+ " {10.3,-10.56034,-8.75916,31.61397},\n" + " {10.4,-7.95394,-5.19933,29.6979},\n"
						+ " {10.5,-5.95247,-4.8828,25.60155},\n" + " {10.6,-5.85402,-6.67906,22.47514},\n"
						+ " {10.7,-7.48423,-9.82802,22.01177},\n" + " {10.8,-10.09203,-12.52743,25.78956},\n"
						+ " {10.9,-11.19255,-10.50598,31.22079},\n" + " {11.0,-8.96512,-5.84989,31.13793},\n"
						+ " {11.1,-6.27067,-4.3859,27.00566},\n" + " {11.2,-5.43368,-5.61261,23.09719},\n"
						+ " {11.3,-6.52039,-8.46104,21.30139},\n" + " {11.4,-9.09254,-12.03745,23.46466},\n"
						+ " {11.5,-11.37483,-12.29793,29.62438},\n" + " {11.6,-10.21884,-7.29456,32.34588},\n"
						+ " {11.7,-7.00082,-4.18498,28.81943},\n" + " {11.8,-5.25042,-4.60762,24.24512},\n"
						+ " {11.9,-5.61586,-6.93059,21.21243},\n" + " {12.0,-7.7753,-10.67962,21.41592},\n"
						+ " {12.1,-10.81199,-13.36887,26.69487},\n" + " {12.2,-11.43709,-9.75158,32.58389},\n"
						+ " {12.3,-8.29394,-4.62149,31.00125},\n" + " {12.4,-5.46597,-3.75213,26.01664},\n"
						+ " {12.5,-4.8954,-5.38612,21.87348},\n" + " {12.6,-6.33147,-8.68199,20.14726},\n"
						+ " {12.7,-9.39766,-12.88193,23.04138},\n" + " {12.8,-11.97723,-12.74954,30.64343},\n"
						+ " {12.9,-10.18676,-6.39608,33.1404},\n" + " {13.0,-6.36056,-3.26155,28.54431},\n"
						+ " {13.1,-4.53716,-3.94788,23.41856},\n" + " {13.2,-4.99306,-6.43265,20.01197},\n"
						+ " {13.3,-7.37637,-10.66244,19.94477},\n" + " {13.4,-11.02802,-14.37086,25.9002},\n"
						+ " {13.5,-12.12677,-10.32427,33.58501},\n" + " {13.6,-8.35565,-3.8449,31.88087},\n"
						+ " {13.7,-4.8826,-2.74552,26.0668},\n" + " {13.8,-4.01601,-4.28153,21.22534},\n"
						+ " {13.9,-5.25614,-7.46911,18.57342},\n" + " {14.0,-8.45193,-12.51758,20.28905},\n"
						+ " {14.1,-12.30205,-14.82899,28.99264},\n" + " {14.2,-11.52632,-7.53151,34.88634},\n"
						+ " {14.3,-6.69542,-2.30379,30.22281},\n" + " {14.4,-3.81541,-2.41687,24.06612},\n"
						+ " {14.5,-3.5656,-4.32747,19.44756},\n" + " {14.6,-5.27564,-7.99169,17.22378},\n"
						+ " {14.7,-9.0692,-13.77827,20.15008},\n" + " {14.8,-13.14323,-15.10863,30.95511},\n"
						+ " {14.9,-11.08696,-5.70149,35.57131},\n" + " {15.0,-5.60051,-1.21159,29.31798},\n"
						+ " {15.1,-2.91917,-1.74479,22.90013},\n" + " {15.2,-2.78083,-3.53463,18.15067},\n"
						+ " {15.3,-4.39978,-6.98413,15.50963},\n" + " {15.4,-8.21946,-13.30174,17.51674},\n"
						+ " {15.5,-13.39938,-17.09691,29.2323},\n" + " {15.6,-12.31368,-6.54634,37.32805},\n"
						+ " {15.7,-5.72264,-0.105556,30.64532},\n" + " {15.8,-2.12633,-0.289632,23.46979},\n"
						+ " {15.9,-1.33958,-1.33905,18.08308},\n" + " {16.0,-1.88377,-2.97785,14.14023},\n"
						+ " {16.1,-3.75043,-6.60333,11.98004},\n" + " {16.2,-8.13294,-14.23131,14.5945},\n"
						+ " {16.3,-14.68229,-19.47114,30.00974},\n" + " {16.4,-12.77045,-4.59812,39.69178},\n"
						+ " {16.5,-4.29966,2.30506,30.63397},\n" + " {16.6,-0.164312,2.07718,23.10135},\n"
						+ " {16.7,1.31236,2.43654,17.82889},\n" + " {16.8,2.66718,4.48694,14.24936},\n"
						+ " {16.9,5.45173,9.47665,13.35608},\n" + " {17.0,10.96539,17.54961,20.29495},\n"
						+ " {17.1,15.09786,14.1643,36.87492},\n" + " {17.2,8.98042,0.547132,35.78446},\n"
						+ " {17.3,2.51516,-1.55153,26.97207},\n" + " {17.4,0.140419,-1.06593,20.53381},\n"
						+ " {17.5,-0.658925,-1.29065,15.76008},\n" + " {17.6,-1.45783,-2.56792,12.24635},\n"
						+ " {17.7,-3.24189,-5.95241,10.23165},\n" + " {17.8,-7.54387,-13.75339,12.44971},\n"
						+ " {17.9,-14.82083,-21.01608,28.42227},\n" + " {18.0,-13.68318,-4.96594,41.19553},\n"
						+ " {18.1,-4.16179,3.40538,31.42646},\n" + " {18.2,0.608367,3.26625,23.68572},\n"
						+ " {18.3,2.50464,4.13666,18.67713},\n" + " {18.4,4.58159,7.37614,16.06875},\n"
						+ " {18.5,8.48897,13.51637,18.35024},\n" + " {18.6,13.35919,16.50029,29.88178},\n"
						+ " {18.7,11.91027,6.20956,36.79291},\n" + " {18.8,5.65664,0.440062,30.20684},\n"
						+ " {18.9,2.35898,0.734941,23.26002},\n" + " {19.0,1.77572,2.00969,18.03767},\n"
						+ " {19.1,2.66319,4.25106,14.39926},\n" + " {19.2,5.25925,9.0994,13.30001},\n"
						+ " {19.3,10.61664,17.16664,19.59813},\n" + " {19.4,15.11798,14.91818,36.20844},\n"
						+ " {19.5,9.43181,0.960717,36.24828},\n" + " {19.6,2.74899,-1.55516,27.37864},\n"
						+ " {19.7,0.221875,-1.05832,20.82784},\n" + " {19.8,-0.602115,-1.22027,15.97808},\n"
						+ " {19.9,-1.35635,-2.3873,12.38999}})");
	}

	public void testNegative() {
		check("Negative(Infinity)", //
				"False");
		check("Negative(-Infinity)", //
				"True");
		check("Negative(-9/4)", //
				"True");
		check("Negative(0.1+I)", //
				"False");
		check("Negative(0)", //
				"False");
		check("Negative(-3)", //
				"True");
		check("Negative(10/7)", //
				"False");
		check("Negative(1+2*I)", //
				"False");
		check("Negative(a + b)", //
				"Negative(a+b)");
		check("Negative(-E)", //
				"True");
		check("Negative(Sin({11, 14}))", //
				"{True,False}");
	}

	public void testNearest() {
		check("Nearest({1, 2, 4, 8, 16, 32}, 20)", //
				"{16}");
		check("Nearest({1, 2, 4, 8, 16, 24, 32}, 20)", //
				"{16,24}");
	}

	public void testNest() {
		check("Nest(f, x, 3)", //
				"f(f(f(x)))");
		check("Nest((1+#) ^ 2 &, x, 2)", //
				"(1+(1+x)^2)^2");

		check("Nest(f, x, 3)", //
				"f(f(f(x)))");
		check("Nest((1 + #)^2 &, 1, 3)", //
				"676");
		check("Nest((1 + #)^2 &, x, 5)", //
				"(1+(1+(1+(1+(1+x)^2)^2)^2)^2)^2");
		check("Nest(Sqrt, 100.0, 4)", //
				"1.33352");
	}

	public void testNestList() {
		check("NestList(f, x, 4)", //
				"{x,f(x),f(f(x)),f(f(f(x))),f(f(f(f(x))))}");
		check("NestList(2*# &, 1, 8)", //
				"{1,2,4,8,16,32,64,128,256}");
		check("NestList(Cos, 1.0, 10)", //
				"{1.0,0.540302,0.857553,0.65429,0.79348,0.701369,0.76396,0.722102,0.750418,0.731404,0.744237}");
		check("NestList((1 + #)^2 &, x, 3)", //
				"{x,(1+x)^2,(1+(1+x)^2)^2,(1+(1+(1+x)^2)^2)^2}");
	}

	public void testNestWhile() {
		check("NestWhile(#/2&, 10000, IntegerQ)", //
				"625/2");
		check("NestWhile(#/2 &, 123456, EvenQ)", //
				"1929");
		check("NestWhile(Log, 100, # > 0 &)", //
				"Log(Log(Log(Log(100))))");
	}

	public void testNestWhileList() {
		check("NestWhileList(#/2&, 10000, IntegerQ)", //
				"{10000,5000,2500,1250,625,625/2}");
		check("NestWhileList(#^2 &, 2, # < 256 &)", //
				"{2,4,16,256}");
		check("NestWhileList(#/2 &, 123456, EvenQ)", //
				"{123456,61728,30864,15432,7716,3858,1929}");
		check("NestWhileList(Log, 100, # > 0 &)", //
				"{100,Log(100),Log(Log(100)),Log(Log(Log(100))),Log(Log(Log(Log(100))))}");
	}

	public void testNextPrime() {
		// print NextPrime: Non-negative integer expected.
		check("NextPrime(-10000)", //
				"NextPrime(-10000)");
		// NextPrime: Positive integer (less equal 2147483647) expected at position 2 in NextPrime(10000,-3).
		check("NextPrime(10000, -3)", //
				"NextPrime(10000,-3)");

		check("NextPrime(10000)", //
				"10007");
		check("NextPrime(10000, 3)", //
				"10037");
		// TODO
		// check("NextPrime(100, -5)", "73");
		// check("NextPrime(10, -5)", "-2");
		// check("NextPrime(5.5, 100)", "563");
	}

	public void testNHoldAll() {
		check("N(f(2, 3))", "f(2.0,3.0)");
		check("SetAttributes(f, NHoldAll)", "");
		check("N(f(2, 3))", "f(2,3)");
	}

	public void testNIntegrate() {
		// github #150
		// NIntegrate: (method=LegendreGauss) 1,001 is larger than the maximum (1,000)
		checkNumeric("NIntegrate(1/x, {x, 0, 1}, MaxPoints->1001)", //
				"NIntegrate(1/x,{x,0,1},MaxPoints->1001)");
		// wrong result
		checkNumeric("NIntegrate(1/x, {x,0,5}, Method->LegendreGauss)", //
				"10.374755035279318");

		// github #61
		// these methods correctly show "NIntegrate(method=method-nsme) maximal count (xxxxx) exceeded"
		checkNumeric("NIntegrate(1/x, {x,0,5}, Method->Romberg)", //
				"NIntegrate(1/x,{x,0,5},Method->romberg)");
		checkNumeric("NIntegrate(1/x, {x,0,5}, Method->Simpson)", //
				"NIntegrate(1/x,{x,0,5},Method->simpson)");
		checkNumeric("NIntegrate(1/x, {x,0,5}, Method->Trapezoid)", //
				"NIntegrate(1/x,{x,0,5},Method->trapezoid)");

		// github #26
		checkNumeric("NIntegrate(ln(x^2), {x, -5, 99}, Method->Romberg, MaxPoints->400, MaxIterations->10000000)", //
				"717.9282476448197");

		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1})", //
				"-0.0208333333333333");
		// LegendreGauss is default method
		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->LegendreGauss)", //
				"-0.0208333333333333");
		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Simpson)", //
				"-0.0208333320915699");
		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Trapezoid)", //
				"-0.0208333271245165");
		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Trapezoid, MaxIterations->5000)", //
				"-0.0208333271245165");
		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1), {x,0,1}, Method->Romberg)", //
				"-0.0208333333333333");
		checkNumeric("NIntegrate (x, {x, 0,2}, Method->Simpson)", //
				"2.0");
		checkNumeric("NIntegrate(Cos(x), {x, 0, Pi})", //
				"0.0");
		checkNumeric("NIntegrate(1/Sin(Sqrt(x)), {x, 0, 1}, PrecisionGoal->10)", //
				"2.1108620052");
	}

	public void testNMaximize() {
		check("NMaximize({-2*x+y-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
				"{-2.0,{x->0.0,y->3.0}}");
		check("NMaximize({-x - y, 3*x + 2*y >= 7 && x + 2*y >= 6}, {x, y})", //
				"{-3.25,{x->0.5,y->2.75}}");
	}

	public void testNMinimize() {
		// TODO non-linear not supported
		// check("NMinimize({x^2 - (y - 1)^2, x^2 + y^2 <= 4}, {x, y})", "");
		check("NMinimize({-2*y+x-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
				"{-11.0,{x->0.0,y->3.0}}");
		check("NMinimize({-2*x+y-5, x+2*y<=6 && 3*x + 2*y <= 12 }, {x, y})", //
				"{-13.0,{x->4.0,y->0.0}}");
		check("NMinimize({x + 2*y, -5*x + y == 7 && x + y >= 26 && x >= 3 && y >= 4}, {x, y})", //
				"{48.83333,{x->3.16667,y->22.83333}}");
		check("NMinimize({x + y, 3*x + 2*y >= 7 && x + 2*y >= 6 }, {x, y})", //
				"{3.25,{x->0.5,y->2.75}}");
	}

	public void testNonCommutativeMultiply() {
		check("{0 ** a, 1 ** a}", //
				"{0**a,1**a}");
		check("{a*b == b*a, a ** b == b ** a}", //
				"{True,a**b==b**a}");
		check("a ** (b ** c) == (a ** b) ** c", //
				"True");
		check("NonCommutativeMultiply(a)", //
				"NonCommutativeMultiply(a)");
	}

	public void testNoneTrue() {
		check("NoneTrue({1, 3, 5}, EvenQ)", //
				"True");
		check("NoneTrue({1, 4, 5}, EvenQ)", //
				"False");
		check("NoneTrue({}, EvenQ)", //
				"True");

		check("NoneTrue({1, 2, 3, 4, 5, 6}, EvenQ)", //
				"False");
		check("NoneTrue({1, 3, 5, 7}, EvenQ)", //
				"True");
		check("NoneTrue({12, 16, x, 14, y}, # < 10 &)", //
				"Nor(x<10,y<10)");
		check("NoneTrue({12, 16, x, 14, y}, TrueQ(# < 10) &)", //
				"True");
		check("NoneTrue(f(1, 7, 3), OddQ)", //
				"False");
	}

	public void testNonNegative() {
		check("NonNegative(-Infinity)", //
				"False");

		check("NonNegative(Infinity)", //
				"True");
		check("NonNegative(-Infinity)", //
				"False");
		check("NonNegative(-9/4)", //
				"False");
		check("NonNegative(0.1+I)", //
				"False");
		check("NonNegative(I)", //
				"False");
		check("{Positive(0), NonNegative(0)}", //
				"{False,True}");
	}

	public void testNonPositive() {
		check("NonPositive(-9/4)", //
				"True");
		check("NonPositive(-0.1+I)", //
				"False");
		check("NonPositive(I)", //
				"False");
		check("{Negative(0), NonPositive(0)}", //
				"{False,True}");
	}

	public void testNor() {
		check("Nor( )", //
				"True");
		check("Nor(2+2)", //
				"!4");
		check("Nor(True,False)", //
				"False");
		check("Nor(x,y,z)", //
				"Nor(x,y,z)");
		check("Nor(x,True,z)", //
				"False");
		check("Nor(x,False,z)", //
				"Nor(x,z)");
		check("BooleanConvert(Nor(p, q, r))", //
				"!p&&!q&&!r");
		check("BooleanConvert(! Nor(p, q, r))", //
				"p||q||r");
	}

	public void testNorm() {
		check("Norm(0)", //
				"0");
		check("Norm({x, y}, 0)", //
				"Norm({x,y},0)");
		check("Norm({x, y}, 0.5)", //
				"Norm({x,y},0.5)");
		check("Norm({})", //
				"Norm({})");
		check("Norm({1, 2, 3, 4}, 2)", //
				"Sqrt(30)");
		check("Norm({10, 100, 200}, 1)", //
				"310");
		check("Norm({0,0,a,0,0})", //
				"Abs(a)");
		check("Norm({a,b,c})", //
				"Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)");

		check("Norm({x, y, z}, Infinity)", //
				"Max(Abs(x),Abs(y),Abs(z))");
		check("Norm({x, y, z})", //
				"Sqrt(Abs(x)^2+Abs(y)^2+Abs(z)^2)");
		check("Norm({x, y, z}, p)", //
				"(Abs(x)^p+Abs(y)^p+Abs(z)^p)^(1/p)");

		check("Norm(-2+I)", //
				"Sqrt(5)");
		check("Norm({1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1})", //
				"Sqrt(5)");
		check("Norm(N({1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1}))", //
				"2.23607");

		check("Norm({1, 2, 3, 4}, 2)", //
				"Sqrt(30)");
		check("Norm({10, 100, 200}, 1)", //
				"310");
		check("Norm({a, b, c})", //
				"Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)");
		check("Norm({-100, 2, 3, 4}, Infinity)", //
				"100");
		check("Norm(1 + I)", //
				"Sqrt(2)");
		check("Norm({1, {2, 3}}) ", //
				"Norm({1,{2,3}})");
		check("Norm({x, y})", //
				"Sqrt(Abs(x)^2+Abs(y)^2)");
		check("Norm({x, y}, 0)", //
				"Norm({x,y},0)");
		check("Norm({x, y}, 0.5)", //
				"Norm({x,y},0.5)");
		check("Norm({})", //
				"Norm({})");
	}

	public void testNormalize() {
		check("Normalize({0})", //
				"{0}");
		check("Normalize(0)", //
				"0");
		check("Normalize({1,5,1})", //
				"{1/(3*Sqrt(3)),5/3*1/Sqrt(3),1/(3*Sqrt(3))}");
		check("Normalize({x,y})", //
				"{x/Sqrt(Abs(x)^2+Abs(y)^2),y/Sqrt(Abs(x)^2+Abs(y)^2)}");
		check("Normalize({x,y}, f)", //
				"{x/f({x,y}),y/f({x,y})}");
		check("Normalize({1, 2*I, 3, 4*I, 5, 6*I})", //
				"{1/Sqrt(91),(I*2)/Sqrt(91),3/Sqrt(91),(I*4)/Sqrt(91),5/Sqrt(91),(I*6)/Sqrt(91)}");
		check("Normalize(N({1, 2*I, 3, 4*I, 5, 6*I}))", //
				"{0.104828,I*0.209657,0.314485,I*0.419314,0.524142,I*0.628971}");
		check("Normalize({{1, 2}, {4, 5}}, Norm)", //
				"{{1/Norm({{1,2},{4,5}}),2/Norm({{1,2},{4,5}})},{4/Norm({{1,2},{4,5}}),5/Norm({{1,\n" + "2},{4,5}})}}");
		check("Normalize(1 + x + x^2, Integrate(#^2, {x, -1, 1}) &)", //
				"5/22*(1+x+x^2)");

		check("Normalize({1, 1, 1, 1})", //
				"{1/2,1/2,1/2,1/2}");
		check("Normalize(1 + I)", //
				"(1+I)/Sqrt(2)");
		check("Normalize(0)", //
				"0");
		check("Normalize({0})", //
				"{0}");
		check("Normalize({})", //
				"{}");
	}

	public void testNot() {
		check("!True", //
				"False");
		check("!False", //
				"True");
		check("!b", //
				"!b");
		check("Not(Not(x))", //
				"x");
		check("Not(a<b)", //
				"a>=b");
		check("Not(a<=b)", //
				"a>b");
		check("Not(a>b)", //
				"a<=b");
		check("Not(a>=b)", //
				"a<b");
		check("Not(a==b)", //
				"a!=b");
		check("Not(x>1)", //
				"x<=1");
		check("!Exists(x, f(x))", //
				"ForAll(x,!f(x))");
		check("!ForAll(x, f(x))", //
				"Exists(x,!f(x))");
	}

	public void testNothing() {
		check("{1, 2, Nothing, 4, 5, Nothing}", //
				"{1,2,4,5}");
		check("ReplacePart({a, b, c, d, e, f, g}, {1 -> Nothing, 3 -> Nothing})", //
				"{b,d,e,f,g}");
	}

	public void testNSolve() {
		// check("125*2^(3-2*z)", //
		// "");
		checkNumeric("NSolve({2==x-0.091y, y==0.054-0.0171*z, x==Exp(z)+1}, {x,y,z})", //
				"{z->0.0048943867690357384,y->0.053916305986249774,x->2.004906383844749}");

		// check("Eliminate({sin(x)-11==y, x+y==-9}, {y,x})",
		// "x+Sin(x)==2");
		// checkNumeric("FindRoot(x+Sin(x)==2, {x,0})", //
		// "{x->1.1060601577062719}");

		// NSolve calls Solve in numeric mode
		checkNumeric("NSolve({Sin(x)-11==y, x+y==-9}, {y,x})", //
				"{x->1.1060601577062719,y->-10.106060157706272}");
		// Solve doesn't get the result
		checkNumeric("Solve({Sin(x)-11==y, x+y==-9}, {y,x})", //
				"Solve({-11+Sin(x)==y,x+y==-9},{y,x})");

		checkNumeric("NSolve(x+Sin(x)==2, x)", //
				"{x->1.1060601577062719}");
		checkNumeric("NSolve(x^3 + 2.0*x^2 - 5*x -3.0 ==0,x)", //
				"{{x->1.773387411650643},{x->-0.5199693720627907+I*2.220446049250313E-16},{x->-3.253418039587852}}");
		checkNumeric("NSolve(x^3 + 2*x^2 - 5*x -3 ==0,x)", //
				"{{x->1.773387411650643},{x->-0.5199693720627907+I*2.220446049250313E-16},{x->-3.253418039587852}}");
	}

	public void testNullSpace() {
		check("NullSpace({{1, 2, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 2, 1}})", //
				"{{-2,1,0}}");

		check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {0,0,0})", //
				"{0,0,0}");
		check("NullSpace({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
				"{{1,-2,1}}");

		check("NullSpace({{-1/3, 0, I}})", //
				"{{I*3,0,1},\n" + " {0,1,0}}");
		check("NullSpace({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
				"{{1,-2,1}}");
		check("A = {{1, 1, 0}, {1, 0, 1}, {0, 1, 1}}", //
				"{{1,1,0},{1,0,1},{0,1,1}}");
		check("NullSpace(A)", //
				"{}");
		check("MatrixRank(A)", //
				"3");
		check("NullSpace({1, {2}})", //
				"NullSpace({1,{2}})");

		check("NullSpace({{1, 2, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 1},\n" //
				+ "{0, 0, -1},\n" + "{1, 2, 1}})", //
				"{{-2,1,0}}");
		check("NullSpace({{1,2,3},{4,5,6},{7,8,9}})", //
				"{{1,-2,1}}");
		check("NullSpace({{1,1,0,1,5},{1,0,0,2,2},{0,0,1,4,-1},{0,0,0,0,0}})", "{{-2,1,-4,1,0},\n" + " {-2,-3,1,0,1}}");
		check("NullSpace({{a,b,c}," + "{c,b,a}})", //
				"{{1,(-a-c)/b,1}}");
		check("NullSpace({{1,2,3}," + "{5,6,7}," + "{9,10,11}})", "{{1,-2,1}}");
		check("NullSpace({{1,2,3,4}," //
				+ "{5,6,7,8}," //
				+ "{9,10,11,12}})", //
				"{{2,-3,0,1},\n" //
						+ " {1,-2,1,0}}");
		check("(-1/2+I*1/2)*(-I)", //
				"1/2+I*1/2");
		check("NullSpace({{1+I,1-I}, {-1+I,1+I}})", //
				"{{I,1}}");
		check("NullSpace({{1,1,1,1,1},{1,0,0,0,0},{0,0,0,0,1},{0,1,1,1,0},{1,0,0,0,1}})", //
				"{{0,-1,1,0,0},\n" + " {0,-1,0,1,0}}");
	}

	public void testNumberQ() {
		check("NumberQ(3,4)", //
				"NumberQ(3,4)");
		check("NumberQ(3+I)", //
				"True");
		check("NumberQ(5!)", //
				"True");
		check("NumberQ(Pi)", //
				"False");
	}

	public void testNumericQ() {
		check("NumericQ(GoldenRatio)", //
				"True");
		check("NumericQ(Sqrt(3))", //
				"True");
		check("NumericQ(Glaisher)", //
				"True");
		check("1<Glaisher<2<E<3", //
				"True");
		check("NumericQ(Sqrt(sin(2)))", //
				"True");
		check("NumericQ(E+Pi)", //
				"True");
		check("NumericQ(Pi)", //
				"True");
		check("NumericQ(3+I)", //
				"True");
		check("NumberQ(5!)", //
				"True");
		check("NumberQ(Pi)", //
				"False");

		check("SetAttributes(f, NumericFunction)", //
				"");
		check("NumericQ(f(Pi))", //
				"True");
	}

	public void testNumerator() {

		check("Numerator( a*x^n*y^- m*Exp(a - b - 2 c + 3 d) )", //
				"a*E^(a+3*d)*x^n");
		check("Numerator(a^-b/x)", //
				"1");

		// github #151
		check("N(Numerator(Cos(Pi)/Pi))", //
				"-1.0");

		check("Numerator(a / b)", //
				"a");
		check("Numerator(a^2*b)", //
				"a^2*b");
		check("Numerator(a^2*b^-2)", //
				"a^2");
		check("Numerator(a^2*b^-a*c)", //
				"a^2*c");

		check("Numerator(Csc(x))", //
				"Csc(x)");
		check("Numerator(Csc(x), Trig->True)", //
				"1");
		check("Numerator(Csc(x)^4)", //
				"Csc(x)^4");
		check("Numerator(Csc(x)^4, Trig->True)", //
				"1");
		check("Numerator(42*Csc(x))", //
				"42*Csc(x)");
		check("Numerator(42*Csc(x), Trig->True)", //
				"42");
		check("Numerator(42*Csc(x)^3)", //
				"42*Csc(x)^3");
		check("Numerator(42*Csc(x)^3, Trig->True)", //
				"42");
		check("Numerator(E^(-x)*x^(1/2))", //
				"Sqrt(x)");

		check("Numerator(Sec(x))", //
				"Sec(x)");
		check("Numerator(Sec(x), Trig->True)", //
				"1");
		check("Numerator(Tan(x))", //
				"Tan(x)");
		check("Numerator(Tan(x), Trig->True)", //
				"Sin(x)");

		check("Numerator(2 / 3)", //
				"2");
		check("Numerator(a + b)", //
				"a+b");
	}

	public void testOddQ() {
		check("OddQ(1/(b-a*c)[[2]])", //
				"False");
		check("OddQ((1/(b-a*c))[[2]])//Trace", //
				"{{(1/(b-a*c))[[2]],-1},True}");
		check("OddQ({1,3}) && OddQ({5,7})", //
				"{True,True}&&{True,True}");
	}

	public void testOneIdentity() {
		check("SetAttributes(f, OneIdentity)", //
				"");
		check("f(a)", //
				"f(a)");
	}

	public void testOneIdentityOrderless() {

		// github issue 89

		check("Module[{e}, ClearAll[eqv]; SetAttributes[eqv, {Flat}]; eqv[p, q, r] /. {eqv[x_, y_] :> {x, y}}]", //
				"{eqv(p),eqv(q,r)}");

		check("Module[{e}, ClearAll[eqv]; SetAttributes[eqv, {Flat, OneIdentity}]; eqv[p, q, r] /. {eqv[x_, y_] :> {x, y}}]", //
				"{p,eqv(q,r)}");

		// See discussion here:
		// https://mathematica.stackexchange.com/questions/183322/subtle-order-of-evaluation-issues-when-pattern-matching-with-attributes
		check("Module[{e}, ClearAll[eqv]; SetAttributes[eqv, {Flat, Orderless}]; eqv[p, q, r] /. {eqv[x_, y_] :> {x, y}}]", //
				"{p,eqv(q,r)}");

		check("Module[{e}, ClearAll[eqv]; SetAttributes[eqv, {Flat, OneIdentity, Orderless}]; eqv[p, q, r] /. {eqv[x_, y_] :> {x, y}}]", //
				"{p,eqv(q,r)}");

		check("SetAttributes(f,{Orderless,OneIdentity})", //
				"");
		check("f(p, q) /. {f(x_,y_) :> {x,y}}", //
				"{p,q}");
		check("f(q,f(p,r)) /. {f(x_,y_) :> {x,y}}", //
				"{q,f(p,r)}");
	}

	public void testOperate() {
		check("Operate(a, (a*c)[f(x)])", //
				"a(a*c)[f(x)]");
		check("Through(Operate(a, (a*c)[f(x)]))", //
				"a((a*c)[f(x)])");
		check("Through(Operate(p, f(x)))", //
				"p(f(x))");
		check("Composition(p, f)[x]", //
				"p(f(x))");
		check("Operate(g &, f(a, b, c))", //
				"g(a,b,c)");
		check("Operate(p, f(a, b))", //
				"p(f)[a,b]");
		check("Operate(p, f(a, b), 1)", //
				"p(f)[a,b]");
		check("Operate(p, f(a)[b][c], 0)", //
				"p(f(a)[b][c])");
		check("Operate(p, f(a)[b][c])", //
				"p(f(a)[b])[c]");
		check("Operate(p, f(a)[b][c], 1)", //
				"p(f(a)[b])[c]");
		check("Operate(p, f(a)[b][c], 2)", //
				"p(f(a))[b][c]");
		check("Operate(p, f(a)[b][c], 3)", //
				"p(f)[a][b][c]");
		check("Operate(p, f(a)[b][c], 4)", //
				"f(a)[b][c]");
		check("Operate(p, f, 0)", //
				"p(f)");
		check("Operate(p, f, -1)", //
				"Operate(p,f,-1)");

		check("Operate(p, f)", //
				"f");
		check("Operate(p, f, 0)", //
				"p(f)");
		check("Operate(p, f(a)[b][c],0)", //
				"p(f(a)[b][c])");
		check("Operate(p, f(a)[b][c])", //
				"p(f(a)[b])[c]");
		check("Operate(p, f(a)[b][c],1)", //
				"p(f(a)[b])[c]");
		check("Operate(p, f(a)[b][c],2)", //
				"p(f(a))[b][c]");
		check("Operate(p, f(a)[b][c],3)", //
				"p(f)[a][b][c]");
		check("Operate(p, f(a)[b][c],4)", //
				"f(a)[b][c]");
		check("Operate(p, f(x, y))", //
				"p(f)[x,y]");
	}

	public void testDP() {
		check("DP(k_,w_):=Sum(w^d/d, {d, Divisors(k)})", //
				"");
		check("DP(6,w)", //
				"w+w^2/2+w^3/3+w^6/6");
	}

	public void testOptimizeExpression() {
		check("OptimizeExpression( f(x))", //
				"{f(x)}");
		check("OptimizeExpression(-3*a - 2*a^3 + 4*Sqrt(1 + a^2)*(5 - 9*Log(2)) + \n"
				+ " 4*a^2*Sqrt(1 + a^2)*(5 - 9*Log(2)) + \n" + " 12*(1 + a^2)^(3/2)*Log(1 + Sqrt(1 + 1/a^2)) - \n"
				+ " 6*(4*(Sqrt(1 + a^2) - a*(2 + a^2 - a*Sqrt(1 + a^2)))*Log(a) + a*Log(1 + a^2)))", //
				//
				"{-3*a-2*a^3+4*v1*v4+4*v1*v2*v4+12*v3^(3/2)*Log(1+Sqrt(1+1/a^2))-6*(4*(v1-a*(2-a*v1+v2))*Log(a)+a*Log(v3)),"//
						+ "{v1->Sqrt(\n" + "1+a^2),"//
						+ "v2->a^2," //
						+ "v3->1+v2," //
						+ "v4->5-9*Log(2)}}");
		check("OptimizeExpression((3 + 3*a^2 + Sqrt(5 + 6*a + 5*a^2) + a*(4 + Sqrt(5 + 6*a + 5*a^2)))/6)", //
				"{1/6*(3+3*v1+v2+a*(4+v2)),{v1->a^2,v2->Sqrt(5+6*a+5*v1)}}");

		check("OptimizeExpression( Sin(x) + Cos(Sin(x)))", //
				"{v1+Cos(v1),{v1->Sin(x)}}");

		check("ReplaceRepeated@@OptimizeExpression( Sin(x) + Cos(Sin(x)))", //
				"Cos(Sin(x))+Sin(x)");
	}

	public void testReplaceRepeated() {

		check("f(g(x),y)//.{f(x_,y_):>k(g(x),g(y)),g(g(x_)):>g(x)}", //
				"k(g(x),g(y))");
		check("x//.x -> 1", //
				"1");

		check("a+b+c //. c->d", //
				"a+b+d");
		check("logrules = {Log(x_ * y_) :> Log(x) + Log(y), Log(x_^y_) :> y * Log(x)};", //
				"");
		check("Log(a * (b * c) ^ d ^ e * f) //. logrules", //
				"Log(a)+d^e*(Log(b)+Log(c))+Log(f)");

		// `ReplaceAll` just performs a single replacement:
		check("Log(a * (b * c) ^ d ^ e * f) /. logrules", //
				"Log(a)+Log((b*c)^d^e*f)");

		check("{f(f(x)), f(x), g(f(x)), f(g(f(x)))} //. f(x_) -> x", //
				"{x,x,g(x),g(x)}");
		check("ReplaceRepeated(f(x_) -> x)[{f(f(x)), f(g(f(x)))}]", //
				"{x,g(x)}");
		check("Log(Sqrt(a*(b*c^d)^e)) //. logrules", //
				"1/2*(Log(a)+e*(Log(b)+d*Log(c)))");

		check("ReplaceRepeated(1/6*(3+3*v1+v2+a*(4+v2)), {v1->a^2, v2->Sqrt(5+6*a+5*v1)})", //
				"1/6*(3+3*a^2+Sqrt(5+6*a+5*a^2)+a*(4+Sqrt(5+6*a+5*a^2)))");
		check("ReplaceRepeated(1/6*(3+3*v1+v2+a*(4+v2)), {v2->Sqrt(5+6*a+5*v1), v1->a^2})", //
				"1/6*(3+3*a^2+Sqrt(5+6*a+5*a^2)+a*(4+Sqrt(5+6*a+5*a^2)))");
	}

	public void testOptions() {
		check("Options(Plus)", //
				"{}");
		check("Options(Factor)", //
				"{GaussianIntegers->False,Modulus->0}");
	}

	public void testOptional() {
		check("f(a)/.f(a,b_.)->{a,b}", //
				"f(a)");
		check("f(a,b)/.f(a,b_.)->{a,b}", //
				"{a,b}");
		check("f(a)/.f(a,b_:c)->{a,b}", //
				"{a,c}");
		check("f(a,b)/.f(a,b_:c)->{a,b}", //
				"{a,b}");

		check("f(x,y)/.f(a__,b_.)->{{a},{b}}", //
				"{{x},{y}}");
		check("f(x,y)/.f(a___,b_.)->{{a},{b}}", //
				"{{x},{y}}");
		check("f(x,y)/.f(a__,b_:c)->{{a},{b}}", //
				"{{x},{y}}");
		check("f(x,y)/.f(a___,b_:c)->{{a},{b}}", //
				"{{x},{y}}");

		check("f(x,y)/.f(a___,b_:c,d_:e)->{{a},{b},{d}}", //
				"{{},{x},{y}}");
		check("f(x)/.f(a_,b_:y,c_:z)->{{a},{b},{c}}", //
				"{{x},{y},{z}}");
		check("f( )/.f(a_,b_:y,c_:z)->{{a},{b},{c}}", //
				"f()");
		check("f(x,i,j)/.f(a_,b_:y,c_:z)->{{a},{b},{c}}", //
				"{{x},{i},{j}}");
		check("a /.f(a,c_.)->{{c}}", //
				"a");

		check("a /. a + c_.->{{c}}", //
				"{{0}}");

		check("MatchQ(a,f(a,c_.))", //
				"False");
		check("MatchQ(a,a+c_.)", //
				"True");

		check("a/.a+c_.+d_.->{{c},{d}}", //
				"{{0},{0}}");
		check("Cos(x)/.(_+c_.+d_.)->{{c},{d}}", //
				"{{0},{0}}");

		check("5*a/.Optional(c1_?NumberQ)*a_->{{c1},{a}}", //
				"{{5},{a}}");
		check("a/.Optional(c1_?NumberQ)*a_->{{c1},{a}}", //
				"{{1},{a}}");

		check("MatchQ(f(a,b),f(c1__?NumberQ))", //
				"False");
		check("MatchQ(f(1,2),f(c1__?NumberQ))", //
				"True");
		check("MatchQ(f(1,2),f(Optional(c1__?NumberQ)))", //
				"False");
		check("MatchQ(f(1),f(Optional(c1__?NumberQ)))", //
				"True");

		check("ReplaceList({a,b,c},{a_:5,b__}->{{a},{b}})", //
				"{{{a},{b,c}},{{5},{a,b,c}}}");
		// TODO add missing combinations
		check("ReplaceList({a,b,c},{a_:5,b_:6,c___}->{{a},{b},{c}})", //
				"{{{a},{b},{c}},{{5},{6},{a,b,c}}}");

		check("MatchQ(-x,p_.)", //
				"True");
		check("MatchQ(-x*a,p_.*a)", //
				"True");
		check("MatchQ(__, Optional(1)*a_)", //
				"True");
		check("MatchQ(x^x, x^Optional(exp_))", //
				"True");

		check("f(a) /. f(x_, y_:3) -> {x, y}", "{a,3}");

		check("f(x_, Optional(y_,1)) := {x, y}", "");
		check("f(1, 2)", "{1,2}");
		check("f(a)", "{a,1}");

		check("g(x_, y_:1) := {x, y}", "");
		check("g(1, 2)", "{1,2}");
		check("g(a)", "{a,1}");

		// check("Default(h)=0", "0");
		// check("h(a) /. h(x_, y_.) -> {x, y}", "");

	}

	public void testOr() {
		check("Or(z, z)", //
				"z||z");
		check("Or(a, z, z)", //
				"a||z||z");
		check("Attributes(Or)", //
				"{Flat,HoldAll,OneIdentity,Protected}");
		check("Or(p, p, p) /. Or(a_, b_) :> {a, b}", //
				"{p,p||p}");
		check("Or(p, p, p) /. Or(a_., b_.) :> {a, b}", //
				"{p,p||p}");
		check("Or(p, p, p)", //
				"p||p||p");
		check("Or(p, q) === Or(q, p)", //
				"False");

		check("False || True", //
				"True");
		check("a || False || b", //
				"a||b");
		check("Or( )", //
				"False");
		check("Or(2+2)", "4");
		check("FullForm( Or(x, Or(y, z)) )", //
				"Or(x, y, z)");
		check("Or(x, False, z)", //
				"x||z");
		check("Or(x, True, z)", //
				"True");
	}

	public void testOrder() {
		check("Order(3,4)", //
				"1");
		check("Order(4,3)", //
				"-1");
		check("Order(6,Pi)", //
				"1");
		check("Order(6,N(Pi))", //
				"-1");
	}

	public void testOrdering() {
		check("Ordering({1,3,4,2,5,9,6})", //
				"{1,4,2,3,5,7,6}");
		check("Ordering({1,3,4,2,5,9,6},All,Greater)", //
				"{6,7,5,3,2,4,1}");

		check("Ordering({2, 6, 1, 9, 1, 2, 3}, -1)", //
				"{4}");
		check("Ordering({2, 6, 1, 9, 1, 2, 3}, -3)", //
				"{4,2,7}");

		check("Ordering({c,a,b})", //
				"{2,3,1}");
		check("Ordering(f(3, 1, 2))", //
				"{2,3,1}");
		check("Ordering({2, 6, 1, 9, 1, 2, 3},4)", //
				"{3,5,1,6}");
		check("Ordering({2, 6, 1, 9, 1, 2, 3},All,Greater)", //
				"{4,2,7,1,6,3,5}");

		check("l={2, 6, 1, 9, 1, 2, 3}", //
				"{2,6,1,9,1,2,3}");
		check("l[[ Ordering(l) ]]", //
				"{1,1,2,2,3,6,9}");

		check("Ordering({2, 6, 1, 9, 1, 2, 3}, -20)", //
				"{4,2,7,6,1,5,3}");
		check("Ordering({2, 6, 1, 9, 1, 2, 3}, 20)", //
				"{3,5,1,6,7,2,4}");
	}

	public void testOrderedQ() {
		check("OrderedQ({a, b})", //
				"True");
		check("OrderedQ({b, a})", //
				"False");

		check("OrderedQ({x^2, 4+6*x})", //
				"False");

		check("OrderedQ({x^2,x^3})", //
				"True");

		check("OrderedQ({x,x^6.0 })", //
				"True");
		check("OrderedQ({4.0*x,33.0*x^6.0 })", //
				"True");
		check("OrderedQ({x^3,4+4*a })", //
				"False");

		check("OrderedQ({x^2, 6*x})", //
				"False");
		check("OrderedQ({6*x,x^2})", //
				"True");
		check("OrderedQ({a,a})", //
				"True");
		check("OrderedQ({x, y, x + y})", //
				"True");
	}

	public void testOrderless() {
		check("SetAttributes(to, Orderless)", //
				"");
		check("to(b_.*x_^3, x_) := {b,x}", //
				"");

		check("to(x, x)", //
				"to(x,x)");
		check("to(a*x, x)", //
				"to(x,a*x)");
		check("to(x^3, x)", //
				"{1,x}");
		check("to(a*x^3, x)", //
				"{a,x}");
		check("to(a*x*z, x)", //
				"to(x,a*x*z)");
		check("to(a*x^3*z, x)", //
				"{a*z,x}");

		check("to(b_.*x_^n_., x_) := {b,n,x}", //
				"");

		check("to(x, x)", //
				"{1,1,x}");
		check("to(a*x, x)", //
				"{a,1,x}");
		check("to(x^2, x)", //
				"{1,2,x}");
		check("to(a*x^2, x)", //
				"{a,2,x}");
		check("to(a*x*z, x)", //
				"{a*z,1,x}");
		check("to(a*x^2*z, x)", //
				"{a*z,2,x}");
		// see https://github.com/mathics/Mathics/issues/747
		check("SetAttributes(ordl,{OneIdentity,Orderless});ordl(p,p,p)/.ordl(p_.,p_.):>p", //
				"ordl(p,p,p)");

		check("SetAttributes(f, Orderless)", //
				"");
		check("f(c, a, b, a + b, 3, 1.0)", //
				"f(1.0,3,a,b,a+b,c)");
		check("f(a, b) == f(b, a)", //
				"True");
		check("SetAttributes(f, Flat)", //
				"");
		check("Attributes(f)", //
				"{Flat,Orderless}");
		check("f(a, b, c) /. f(a, c) -> d", //
				"f(b,d)");

	}

	public void testOrthogonalize() {
		check("2/Sqrt(14)", //
				"Sqrt(2/7)");
		check("(1/4)*Sqrt(1/2)", //
				"1/(4*Sqrt(2))");
		check("4*Sqrt(2)", //
				"4*Sqrt(2)");
		check("1/Sqrt(14)", //
				"1/Sqrt(14)");
		check("2/Sqrt(14)", //
				"Sqrt(2/7)");
		check("5/Sqrt(42)", //
				"5/Sqrt(42)");
		check("-2/Sqrt(2/21)", //
				"-Sqrt(42)");
		check("1/Sqrt(3)", //
				"1/Sqrt(3)");
		check("-1/Sqrt(3)", //
				"-1/Sqrt(3)");

		check("Orthogonalize({{3,1},{2,2}})", //
				"{{3/Sqrt(10),1/Sqrt(10)},{-1/Sqrt(10),3/Sqrt(10)}}");
		check("Orthogonalize({{1,0,1},{1,1,1}})", //
				"{{1/Sqrt(2),0,1/Sqrt(2)},{0,1,0}}");
		check("Orthogonalize({{1, 2}, {3, 1}, {6, 9}, {7, 8}})", //
				"{{1/Sqrt(5),2/Sqrt(5)},{2/Sqrt(5),-1/Sqrt(5)},{0,0},{0,0}}");
		check("Orthogonalize({{2,3}, {2,7}, {4,5}})", //
				"{{2/Sqrt(13),3/Sqrt(13)},{-3/Sqrt(13),2/Sqrt(13)},{0,0}}");
		check("Orthogonalize({{1,2,3},{5,2,7},{3,5,1}})", //
				"{{1/Sqrt(14),Sqrt(2/7),3/Sqrt(14)},{5/Sqrt(42),-2*Sqrt(2/21),1/Sqrt(42)},{1/Sqrt(\n"
						+ "3),1/Sqrt(3),-1/Sqrt(3)}}");
		check("Orthogonalize({{1,0,0},{0,0,1}})", //
				"{{1,0,0},{0,0,1}}");
	}

	public void testOuter() {
		check("Outer(f, {a, b}, {x, y, z})", //
				"{{f(a,x),f(a,y),f(a,z)},{f(b,x),f(b,y),f(b,z)}}");
		check("Outer(Times, {1, 2, 3, 4}, {a, b, c})", //
				"{{a,b,c},{2*a,2*b,2*c},{3*a,3*b,3*c},{4*a,4*b,4*c}}");
		check("Outer(Times, {{1, 2}, {3, 4}}, {{a, b}, {c, d}})", //
				"{{{{a,b},{c,d}},{{2*a,2*b},{2*c,2*d}}},{{{3*a,3*b},{3*c,3*d}},{{4*a,4*b},{4*c,4*d}}}}");
		check("Outer(f, {a, b}, {x, y, z}, {u, v})", //
				"{{{f(a,x,u),f(a,x,v)},{f(a,y,u),f(a,y,v)},{f(a,z,u),f(a,z,v)}},{{f(b,x,u),f(b,x,v)},{f(b,y,u),f(b,y,v)},{f(b,z,u),f(b,z,v)}}}");
		check("Outer(Times, {{1, 2}, {3, 4}}, {{a, b, c}, {d, e}})", //
				"{{{{a,b,c},{d,e}},{{2*a,2*b,2*c},{2*d,2*e}}},{{{3*a,3*b,3*c},{3*d,3*e}},{{4*a,4*b,\n"
						+ "4*c},{4*d,4*e}}}}");
		check("Outer(g, f(a, b), f(x, y, z))", //
				"f(f(g(a,x),g(a,y),g(a,z)),f(g(b,x),g(b,y),g(b,z)))");
		check("Dimensions(Outer(f, {x, x, x}, {x, x, x, x}, {x, x}, {x, x, x, x, x}))", //
				"{3,4,2,5}");
		check("Dimensions(Outer(f, {{x, x}, {x, x}}, {x, x, x}, {{x}}))", //
				"{2,2,3,1,1}");
		check("Outer(f, {a, b}, {1,2,3})", //
				"{{f(a,1),f(a,2),f(a,3)},{f(b,1),f(b,2),f(b,3)}}");
		check("Outer(Times, {{1, 2}}, {{a, b}, {x, y, z}})", //
				"{{{{a,b},{x,y,z}},{{2*a,2*b},{2*x,2*y,2*z}}}}");

		check("trigs = Outer(Composition, {Sin, Cos, Tan}, {ArcSin, ArcCos, ArcTan})", //
				"{{Composition(Sin,ArcSin),Composition(Sin,ArcCos),Composition(Sin,ArcTan)},{Composition(Cos,ArcSin),Composition(Cos,ArcCos),Composition(Cos,ArcTan)},{Composition(Tan,ArcSin),Composition(Tan,ArcCos),Composition(Tan,ArcTan)}}");
		check("Map(#(0) &, trigs, {2})", //
				"{{0,1,0},{1,0,1},{0,ComplexInfinity,0}}");
		check("Outer(StringJoin, {\"\", \"re\", \"un\"}, {\"cover\", \"draw\", \"wind\"}, {\"\", \"ing\", \"s\"})", //
				"{{{cover,covering,covers},{draw,drawing,draws},{wind,winding,winds}},{{recover,recovering,recovers},{redraw,redrawing,redraws},{rewind,rewinding,rewinds}},{{uncover,uncovering,uncovers},{undraw,undrawing,undraws},{unwind,unwinding,unwinds}}}");
	}

	public void testPadLeft() {
		check("PadLeft({1, 2, 3}, 5)", //
				"{0,0,1,2,3}");
		check("PadLeft(x(a, b, c), 5) ", //
				"x(0,0,a,b,c)");
		check("PadLeft({1, 2, 3}, 2)", //
				"{2,3}");
		check("PadLeft({1, 2, 3}, 1)", //
				"{3}");
		check("PadLeft({{}, {1, 2}, {1, 2, 3}})", //
				"{{0,0,0},{0,1,2},{1,2,3}}");

		check("PadLeft({a, b, c}, 10)", //
				"{0,0,0,0,0,0,0,a,b,c}");
		check("PadLeft({a, b, c}, 10, {x, y, z})", //
				"{z,x,y,z,x,y,z,a,b,c}");
		check("PadLeft({a, b, c}, 9, {x, y, z})", //
				"{x,y,z,x,y,z,a,b,c}");
		check("PadLeft({a, b, c}, 8, {x, y, z})", //
				"{y,z,x,y,z,a,b,c}");
		check("PadLeft({a, b, c}, 10, 42)", //
				"{42,42,42,42,42,42,42,a,b,c}");
		// TODO
		// check("PadLeft({1, 2, 3}, 10, {a, b, c}, 2)", "{b, c, a, b, c, 1, 2, 3, a,
		// b}");

	}

	public void testPadRight() {
		// check("PadRight({1, 2, 3}, 5)", "{1,2,3,0,0}");
		// check("PadRight(x(a, b, c), 5) ", "x(a,b,c,0,0)");
		// check("PadRight({1, 2, 3}, 2)", "{1,2}");
		// check("PadRight({1, 2, 3}, 1)", "{1}");
		check("PadRight({{}, {1, 2}, {1, 2, 3}})", //
				"{{0,0,0},{1,2,0},{1,2,3}}");

		check("PadRight({a, b, c}, 10)", //
				"{a,b,c,0,0,0,0,0,0,0}");
		check("PadRight({a, b, c}, 10, {x, y, z})", //
				"{a,b,c,x,y,z,x,y,z,x}");
		check("PadRight({a, b, c}, 9, {x, y, z})", //
				"{a,b,c,x,y,z,x,y,z}");
		check("PadRight({a, b, c}, 8, {x, y, z})", //
				"{a,b,c,x,y,z,x,y}");
		check("PadRight({a, b, c}, 10, 42)", //
				"{a,b,c,42,42,42,42,42,42,42}");

	}

	public void testParserFixedPoint() {
		try {
			Parser p = new Parser(true);
			ASTNode obj = p.parse("{28, 21} /. {a_, b_} /; b != 0 -> {b, Mod(a, b)}");
			assertEquals(obj.toString(),
					"ReplaceAll(List(28, 21), Rule(Condition(List(a_, b_), Unequal(b, 0)), List(b, Mod(a, b))))");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals("", e.getMessage());
		}
	}

	public void testPart() {
		check("test={g,h,k}", //
				"{g,h,k}");
		check("test[[Position({x,v,w },{x,_,_})[[1,1]],2]] = ({a,b})[[2]]", //
				"b");
		check("f(a, b, c)[[{2, 3}]]", //
				"f(b,c)");
		check("f(g(a, b), h(c, d))[[{1, 2}, {2}]]", //
				"f(g(b),h(d))");
		check("{d, e, a, b, c}[[{3,4,5,1,2}]]", //
				"{a,b,c,d,e}");

		check("m = {a, b, c, d};", //
				"");
		check("m[[2]] += x", //
				"b+x");
		check("m", //
				"{a,b+x,c,d}");

		check("m[[2]] *= x", //
				"x*(b+x)");
		check("m", //
				"{a,x*(b+x),c,d}");

		check("m[[2]] -= y", //
				"x*(b+x)-y");
		check("m", //
				"{a,x*(b+x)-y,c,d}");

		check("m[[2]] /= z^2", //
				"(x*(b+x)-y)/z^2");
		check("m", //
				"{a,(x*(b+x)-y)/z^2,c,d}");

		// Part: Part 1000000000000 of a(x,y,z,f) does not exist.
		check("{a(x,y,z,f),b,c,d}[[1,1000000000000]]", //
				"{a(x,y,z,f),b,c,d}[[1,1000000000000]]");
		// Part: Part -30 of a(x,y,z,f) does not exist.
		check("{a(x,y,z,f),b,c,d}[[1,-30]]", //
				"{a(x,y,z,f),b,c,d}[[1,-30]]");
		// Part: The expression None cannot be used as a part specification.
		check("{{3,1},{5,1},{17,1},{257,1},{65537,1}}[[None]]", //
				"{{3,1},{5,1},{17,1},{257,1},{65537,1}}[[None]]");
		// Part: Part specification asdf[[{1,2}]] is longer than depth of object.
		check("Part[asdf,{1,2}]", //
				"asdf[[{1,2}]]");

		check("v = {a, b, c, d, e, f}", //
				"{a,b,c,d,e,f}");
		check("v[[2 ;; 4]] = x", //
				"x");
		check("v", //
				"{a,x,x,x,e,f}");
		check("f(g(a, b), g(c, d))[[2, 1]]", //
				"c");
		check("(1 + 2 * x^2 + y^2)[[3]]", //
				"y^2");
		check("{x -> 4, y -> 5}[[1, 2]]", //
				"4");
		check("{{a, b, c}, {d, e, f}}[[1]][[2]]", //
				"b");
		check("{{a, b, c}, {d, e, f}, {g, h, i}}[[{1, 3}, {2, 3}]]", //
				"{{b,c},{h,i}}");
		check("{a, b, c, d, e, f, g, h, i, j, k}[[3 ;; -3 ;; 2]]", //
				"{c,e,g,i}");
		check("{a, b, c, d, e, f, g, h, i, j, k}[[;; ;; 2]]", //
				"{a,c,e,g,i,k}");

		check("{{a, b, c}, {d, e, f}, {g, h, i}}[[All, 2]]", //
				"{b,e,h}");
		check("{a, b, c, d, e, f}[[-2]]", //
				"e");
		check("{a, b, c, d, e, f}[[{1, 3, 1, 2, -1, -1}]]", //
				"{a,c,a,b,f,f}");

		check("1/(b-a*c)[[2]]", //
				"-1/(a*c)");
		check("{{1,0,0},\n" + " {0,1,0},\n" + " {0,0,1}}[[2]]", //
				"{0,1,0}");
		check("T = {a, b, c, d}", //
				"{a,b,c,d}");
		check("T[[2]]=3", //
				"3");

		check("{a(x,y,z,f),b,c,d}[[1,2]]", //
				"y");
		check("{{3,1},{5,1},{17,1},{257,1},{65537,1}}[[All,2]]", //
				"{1,1,1,1,1}");

		check("lst=False;lst[[2]]", //
				"False[[2]]");
		check("T = {a, b, c, d}", //
				"{a,b,c,d}");
		check("T[[2]]=3", //
				"3");
		check("T", //
				"{a,3,c,d}");

		check("A = {a, b, c, d}", //
				"{a,b,c,d}");
		check("A[[3]]", //
				"c");
		check("{a, b, c}[[-2]]", //
				"b");
		check("(a + b + c)[[2]]", //
				"b");
		check("(a + b + c)[[0]]", //
				"Plus");
		check("M = {{a, b}, {c, d}}", //
				"{{a,b},{c,d}}");
		check("M[[1, 2]]", //
				"b");
		check("M[[1, 2]] = x", //
				"x");
		check("M[[2, 2]] = y", //
				"y");
		check("M", //
				"{{a,x},{c,y}}");
		check("M[[1, 1+1]] = y", //
				"y");
		check("M", //
				"{{a,y},{c,y}}");
		check("{1, 2, 3, 4}[[2;;4]]", //
				"{2,3,4}");
		check("{1, 2, 3, 4}[[2;;-1]]", //
				"{2,3,4}");
		check("{a, b, c, d}[[{1, 3, 3}]]", //
				"{a,c,c}");
		check("B = {{a, b, c}, {d, e, f}, {g, h, i}}", //
				"{{a,b,c},{d,e,f},{g,h,i}}");
		check("B[[;; 2]]", //
				"{{a,b,c},{d,e,f}}");
		check("B[[;;, 2]]", //
				"{b,e,h}");
		check("B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}", //
				"{{1,2,3},{4,5,6},{7,8,9}}");
		check("B[[{1, 3}, -2;;-1]]", //
				"{{2,3},{8,9}}");
		check("(a+b+c+d)[[-1;;-2]]", //
				"0");
		check("x[[2]]", //
				"x[[2]]");
		// Assignment
		check("B[[;;, 2]] = {10, 11, 12}", //
				"{10,11,12}");
		check("B", //
				"{{1,10,3},{4,11,6},{7,12,9}}");
		check("B[[;;, 3]] = 13", //
				"13");
		check("B", //
				"{{1,10,13},{4,11,13},{7,12,13}}");

		check("B[[1;;-2]] = t", //
				"t");
		check("B", //
				"{t,t,{7,12,13}}");

		check("F = Table(i*j*k, {i, 1, 3}, {j, 1, 3}, {k, 1, 3})",
				"{{{1,2,3},{2,4,6},{3,6,9}},{{2,4,6},{4,8,12},{6,12,18}},{{3,6,9},{6,12,18},{9,18,\n" + "27}}}");

		check("F[[;; All, 2 ;; 3, 2]] = t", //
				"t");
		check("F", //
				"{{{1,2,3},{2,t,6},{3,t,9}},{{2,4,6},{4,t,12},{6,t,18}},{{3,6,9},{6,t,18},{9,t,27}}}");
		check("F[[;; All, 1 ;; 2, 3 ;; 3]] = k", //
				"k");
		check("F", //
				"{{{1,2,k},{2,t,k},{3,t,9}},{{2,4,k},{4,t,k},{6,t,18}},{{3,6,k},{6,t,k},{9,t,27}}}");

		check("A[[1]] + B[[2]] + C[[3]] // Hold // FullForm", //
				"Hold(Plus(Part(A, 1), Part(B, 2), Part(C, 3)))");
		check("a = {2,3,4}; i = 1; a[[i]] = 0; a", //
				"{0,3,4}");

		check("{1,2,3,4,5}[[3;;1;;-1]]", //
				"{3,2,1}");
		check("{1, 2, 3, 4, 5}[[;; ;; -1]]", //
				"{5,4,3,2,1}");

		check("Range(11)[[-3 ;; 2 ;; -2]]", //
				"{9,7,5,3}");
		check("Range(11)[[-3 ;; -7 ;; -3]]", //
				"{9,6}");
		check("Range(11)[[7 ;; -7;; -2]]", //
				"{7,5}");

		check("{1, 2, 3, 4}[[1;;3;;-1]]", //
				"{1,2,3,4}[[1;;3;;-1]]");
		check("{1, 2, 3, 4}[[3;;1]]", //
				"{1,2,3,4}[[3;;1]]");
		check("{1, 2, 3, 4}[[3;;2]]", //
				"{}");

		check("(1 + 2*x^2 + y^2)[[2]]", //
				"2*x^2");
		check("(1 + 2*x^2 + y^2)[[1]]", //
				"1");
		check("(x/y)[[2]]", //
				"1/y");
		check("(y/x)[[2]]", //
				"y");

		check("{{a, b, c}, {d, e, f}}[[1]][[2]]", //
				"b");
		check("{{a, b, c}, {d, e, f}}[[1, 2]]", //
				"b");

	}

	public void testPartition() {
		check("Partition({a, b, c, d, e, f}, 2)", //
				"{{a,b},{c,d},{e,f}}");
		check("Partition({a, b, c, d, e, f}, 3, 1)", //
				"{{a,b,c},{b,c,d},{c,d,e},{d,e,f}}");
		check("Partition({a, b, c, d, e}, 2)", //
				"{{a,b},{c,d}}");
	}

	public void testPartitionsP() {
		check("PartitionsP({1,2,3,4,5,6,7})", //
				"{1,2,3,5,7,11,15}");
		check("PartitionsP(5)", //
				"7");
		check("PartitionsP(6)", //
				"11");
		check("PartitionsP(9)", //
				"30");
		check("PartitionsP(50)", //
				"204226");
		check("PartitionsP(100)", //
				"190569292");
		check("PartitionsP(200)", //
				"3972999029388");
		check("PartitionsP(300)", //
				"9253082936723602");
		// check("PartitionsP(1000)", "24061467864032622473692149727991");
	}

	public void testPartitionsQ() {
		check("PartitionsQ(3)", //
				"2");
		check("PartitionsQ(5)", //
				"3");
		check("PartitionsQ(6)", //
				"4");
		check("PartitionsQ(10)", //
				"10");
		check("PartitionsQ(15)", //
				"27");
		check("PartitionsQ(16)", //
				"32");
		check("PartitionsQ(17)", //
				"38");
		check("PartitionsQ(18)", //
				"46");
		check("PartitionsQ(20)", //
				"64");
		check("PartitionsQ(50)", //
				"3658");
		// upper limit to avid stack overflow
		check("PartitionsQ(201)", //
				"PartitionsQ(201)");
	}

	public void testPerfectNumber() {
		check("Table(PerfectNumber(i), {i,5})", //
				"{6,28,496,8128,33550336}");
		check("PerfectNumber(1)", //
				"6");
		check("PerfectNumber(2)", //
				"28");
		check("PerfectNumber(3)", //
				"496");
		check("PerfectNumber(6)", //
				"8589869056");
		check("PerfectNumber(7)", //
				"137438691328");
		check("PerfectNumber(8)", //
				"2305843008139952128");

		// big integer results:
		check("PerfectNumber(9)", //
				"2658455991569831744654692615953842176");
		check("PerfectNumber(10)", //
				"191561942608236107294793378084303638130997321548169216");

		check("PerfectNumber(48)", //
				"PerfectNumber(48)");
	}

	public void testPerfectNumberQ() {
		check("PerfectNumberQ(6)", //
				"True");
		check("PerfectNumberQ(1)", //
				"False");
		check("PerfectNumberQ(35)", //
				"False");
		check("Select(Range(1000), PerfectNumberQ)", //
				"{6,28,496}");

	}

	public void testPatternAndRules() {
		check("f(a_.*x_^j_.,x_):={a,x,j}; f(a*x*z,x)", //
				"{a*z,x,1}");
		check("f(a_.*x_^j_.,x_):={a,x,j}; f(a*b*x^3,x)", //
				"{a*b,x,3}");
		check("f(a_.*x_^j_.,x_):={a,x,j}; f(a*b*x^3*y*z,x)", //
				"{a*b*y*z,x,3}");

		check("a + b + c /. a + b -> t", //
				"c+t");
		check("a + 2 + b + c + x * y /. n_Integer + s__Symbol + rest_ -> {n, s, rest}", //
				"{2,a,b+c+x*y}");
		check("f(a, b, c, d) /. f(first_, rest___) -> {first, {rest}}", //
				"{a,{b,c,d}}");
		check("f(4) /. f(x_?(# > 0&)) -> x ^ 2", //
				"16");
		check("f(4) /. f(x_) /; x > 0 -> x ^ 2", //
				"16");
		check("f(a, b, c, d) /. f(start__, end__) -> {{start}, {end}}", //
				"{{a},{b,c,d}}");
		check("f(a) /. f(x_, y_:3) -> {x, y}", //
				"{a,3}");
		// check("f(y, a->3) /. f(x_, OptionsPattern({a->2, b->5})) -> {x,
		// OptionValue(a), OptionValue(b)}", "");
	}

	public void testPatternSequence() {
		check("integersQ(__Integer) = True", //
				"True");
		check("integersQ(__) = False", //
				"False");
		check("integersQ(1,2,3)", //
				"True");
		check("integersQ(1,2,a)", //
				"False");
	}

	public void testPatternOrder() {
		// see https://mathematica.stackexchange.com/questions/8619
		check("PatternOrder(x_, 1)", //
				"-1");
		// check("PatternOrder(g(_, _List), g(_, {___}))", //
		// "-1");
		// check("PatternOrder(g({}, _List), g(_, _List))", //
		// "1");
		check("PatternOrder(g({}, _List), g(_, {___}))", //
				"-1");
		check("PatternOrder(g(a), g(_))", //
				"1");
		check("PatternOrder(a,b)", //
				"1");
		check("PatternOrder(g(a), g(b))", //
				"1");
		// check("PatternOrder(__, (_) ..)", //
		// "-1");
	}

	public void testPatternTest() {
		check("MatchQ({1,8,Pi},{__?Positive})", //
				"True");

		check("$j(x_, y_:1, z_:2) := jp(x, y, z); $j(a,b)", //
				"jp(a,b,2)");
		check("$j(x_, y_:1, z_:2) := jp(x, y, z); $j(a)", //
				"jp(a,1,2)");
		check("$f(x_:2):={x};$f()", //
				"{2}");
		check("$f(x_:2):={x};$f(a)", //
				"{a}");

		check("MatchQ(3, _Integer?(#>0&))", //
				"True");
		check("MatchQ(-3, _Integer?(#>0&))", //
				"False");

		check("Cases({1,2,3,5,x,y,4},_?NumberQ)", //
				"{1,2,3,5,4}");
		check("MatchQ({1,8,Pi},{__?Positive})", //
				"True");
		check("MatchQ({1,I,0},{__?Positive})", //
				"False");

		check("f(x_?NumericQ):= NIntegrate(Sin(t^3), {t, 0, x})", //
				"");
		check("f(2)", //
				"0.451948");
		check("f((1+Sqrt(2))/5)", //
				"0.0135768");
		check("f(a)", //
				"f(a)");

		check("{3,-5,2,7,-6,3} /. _?Negative:>0", //
				"{3,0,2,7,0,3}");

		check("Cases(Range(0,350),_?(Divisible(#,7)&&Divisible(#,5)&))", //
				"{0,35,70,105,140,175,210,245,280,315,350}");

		check("$f(n_?NonNegative, p_?PrimeQ):=n^p; $f(2,3)", //
				"8");
		check("$f(n_?NonNegative, p_?PrimeQ):=n^p; $f(2,4)", //
				"$f(2,4)");

		check("MatchQ({{a,b},{c,d}},{_,_}?MatrixQ)", //
				"True");
		check("MatchQ({a,b},{_,_}?MatrixQ)", //
				"False");

		check("Cases({{a,b},{1,2,3},{{d,6},{d,10}}}, {_,_}?VectorQ)", //
				"{{a,b}}");
		check("Cases({{a,b},{1,2,3},{{d,6},{d,10}}}, {x_,y_}/;!ListQ(x)&&!ListQ(y))", //
				"{{a,b}}");

	}

	public void testPDF() {
		check("PDF(BetaDistribution(2,3), 0.1)", //
				"0.972");
		check("PDF(BetaDistribution(2,3), 0.9)", //
				"0.108");
		check("PDF(ChiSquareDistribution(3), 0.1)", //
				"0.120004");
		check("PDF(ChiSquareDistribution(3), 0.9)", //
				"0.241323");
		check("PDF(FRatioDistribution(2,3), 0.1)", //
				"0.850997");
		check("PDF(FRatioDistribution(2,3), 0.9)", //
				"0.308816");
		check("PDF(GammaDistribution(2,3), 0.1)", //
				"0.0107468");
		check("PDF(GammaDistribution(2,3), 0.9)", //
				"0.0740818");
		check("PDF(LogNormalDistribution(2,3), 0.1)", //
				"0.475483");
		check("PDF(LogNormalDistribution(2,3), 0.9)", //
				"0.115505");
		check("PDF(NakagamiDistribution(2,3), 0.1)", //
				"0.000882983");
		check("PDF(NakagamiDistribution(2,3), 0.9)", //
				"0.377621");
		check("PDF(NormalDistribution(2,3), 0.1)", //
				"0.108815");
		check("PDF(NormalDistribution(2,3), 0.9)", //
				"0.124335");
		check("PDF(StudentTDistribution(3), 0.1)", //
				"0.365114");
		check("PDF(StudentTDistribution(3), 0.9)", //
				"0.227883");
		check("PDF(UniformDistribution({0,1}), 0.1)", //
				"1");
		check("PDF(UniformDistribution({0,1}), 0.9)", //
				"1");
		check("PDF(WeibullDistribution(2,3), 0.1)", //
				"0.0221975");
		check("PDF(WeibullDistribution(2,3), 0.9)", //
				"0.182786");

		check("Table(PDF(NormalDistribution(m, 1.5), x), {m, {-1, 1, 2}},{x, {-1, 1, 2}}) ", //
				"{{0.265962,0.10934,0.035994},{0.10934,0.265962,0.212965},{0.035994,0.212965,0.265962}}");
		check("Table(PDF(NormalDistribution(0.0,1.0), x), {m, {-1, 1, 2}},{x, {-1, 1, 2}})//N ", //
				"{{0.241971,0.241971,0.053991},{0.241971,0.241971,0.053991},{0.241971,0.241971,0.053991}}");
		check("Table(PDF(NormalDistribution( ), x), {m, {-1, 1, 2}},{x, {-1, 1, 2}})//N ", //
				"{{0.241971,0.241971,0.053991},{0.241971,0.241971,0.053991},{0.241971,0.241971,0.053991}}");

		check("PDF(NormalDistribution(0, 1), {x, y})", //
				"{1/(E^(x^2/2)*Sqrt(2*Pi)),1/(E^(y^2/2)*Sqrt(2*Pi))}");

		check("PDF(NormalDistribution(n, m))", //
				"1/(E^((-n+#1)^2/(2*m^2))*m*Sqrt(2*Pi))&");
		check("PDF(NormalDistribution(n, m),k)", //
				"1/(E^((k-n)^2/(2*m^2))*m*Sqrt(2*Pi))");
		check("PDF(BernoulliDistribution(p),k)", //
				"Piecewise({{1-p,k==0},{p,k==1}},0)");
		check("PDF(BetaDistribution(a,b),k)", //
				"Piecewise({{1/((1-k)^(1-b)*k^(1-a)*Beta(a,b)),0<k<1}},0)");
		check("PDF(BinomialDistribution(n, m),k)", //
				"Piecewise({{(m^k*Binomial(n,k))/(1-m)^(k-n),0<=k<=n}},0)");
		check("PDF(ExponentialDistribution(n),k)", //
				"Piecewise({{n/E^(k*n),k>=0}},0)");
		check("PDF(PoissonDistribution(p),k)", //
				"Piecewise({{p^k/(E^p*k!),k>=0}},0)");
		check("PDF(DiscreteUniformDistribution({a, b}), k)", //
				"Piecewise({{1/(1-a+b),a<=k<=b}},0)");
		check("PDF(UniformDistribution({a, b}), k)", //
				"Piecewise({{1/(-a+b),a<=k<=b}},0)");
		check("PDF(ErlangDistribution(n, m),k)", //
				"Piecewise({{m^n/(E^(k*m)*k^(1-n)*Gamma(n)),k>0}},0)");
		check("PDF(LogNormalDistribution(n,m),k)", //
				"Piecewise({{1/(E^((-n+Log(k))^2/(2*m^2))*k*m*Sqrt(2*Pi)),k>0}},0)");
		check("PDF(NakagamiDistribution(n, m),k)", //
				"Piecewise({{(2*(n/m)^n)/(E^((k^2*n)/m)*k^(1-2*n)*Gamma(n)),k>0}},0)");

		check("PDF(FrechetDistribution(n, m),k)", //
				"Piecewise({{n/(E^(k/m)^(-n)*(k/m)^(1+n)*m),k>0}},0)");
		check("PDF(GammaDistribution(n, m),k)", //
				"Piecewise({{1/(E^(k/m)*k^(1-n)*m^n*Gamma(n)),k>0}},0)");
		check("PDF(GeometricDistribution(n),k)", //
				"Piecewise({{(1-n)^k*n,k>=0}},0)");
		check("PDF(GumbelDistribution(n, m),k)", //
				"1/(E^(E^((k-n)/m)-(k-n)/m)*m)");
		check("PDF(HypergeometricDistribution(n, ns, nt),k)", //
				"Piecewise({{(Binomial(ns,k)*Binomial(-ns+nt,-k+n))/Binomial(nt,n),0<=k<=n&&n+ns-nt<=k<=n&&\n"
						+ "0<=k<=ns&&n+ns-nt<=k<=ns}},0)");
		check("PDF(StudentTDistribution(n),k)", //
				"(n/(k^2+n))^(1/2*(1+n))/(Sqrt(n)*Beta(n/2,1/2))");
		check("PDF(WeibullDistribution(n, m),k)", //
				"Piecewise({{n/(E^(k/m)^n*(k/m)^(1-n)*m),k>0}},0)");
		check("PDF(StudentTDistribution(4),k)", //
				"12*((1/(4+k^2)))^(5/2)");

		check("PDF(DiscreteUniformDistribution({1, 5}), 3)", //
				"1/5");
		check("N(PDF(NormalDistribution(0, 1), 0))", //
				"0.398942");
		checkNumeric("N(PDF(BinomialDistribution(40, 0.5), 1))", //
				"3.637978807091713E-11");
		checkNumeric("N(PDF(HypergeometricDistribution(20,50,100), 10))", //
				"0.19687121770654945");
		checkNumeric("N(PDF(PoissonDistribution(10), 15))", //
				"0.03471806963068414");
	}

	public void testPermutations() {
		// TODO
		// check("Permutations({1, 2, 1} )", //
		// "");
		check("Permutations({1, 2, 3}, 2)", //
				"{{},{1},{2},{3},{1,2},{1,3},{2,1},{2,3},{3,1},{3,2}}");
		check("Permutations({1, 2, 3}, {2})", //
				"{{1,2},{1,3},{2,1},{2,3},{3,1},{3,2}}");
		check("Permutations({a,b,c})", //
				"{{a,b,c},{a,c,b},{b,a,c},{b,c,a},{c,a,b},{c,b,a}}");
		check("Permutations({a,b,c}, {2})", //
				"{{a,b},{a,c},{b,a},{b,c},{c,a},{c,b}}");

		check("Permutations({a},{0})", //
				"{{}}");
		check("Permutations({a,b,c,d},{3})", //
				"{{a,b,c},{a,b,d},{a,c,b},{a,c,d},{a,d,b},{a,d,c},{b,a,c},{b,a,d},{b,c,a},{b,c,d},{b,d,a},{b,d,c},{c,a,b},{c,a,d},{c,b,a},{c,b,d},{c,d,a},{c,d,b},{d,a,b},{d,a,c},{d,b,a},{d,b,c},{d,c,a},{d,c,b}}");
		check("Permutations({a,a,b})", //
				"{{a,a,b},{a,b,a},{b,a,a}}");
		check("Permutations({a,a,b,b})", //
				"{{a,a,b,b},{a,b,a,b},{a,b,b,a},{b,a,a,b},{b,a,b,a},{b,b,a,a}}");
		check("Permutations({a,a,b,b},{3})", //
				"{{a,a,b},{a,b,a},{a,b,b},{b,a,a},{b,a,b},{b,b,a}}");
	}

	public void testPiecewise() {
		check("Piecewise({{x^2, x < 0}, {x, x > 0}}) /. x->3", //
				"3");
		check("Piecewise({{(a^0*Log(a)^n)/n!,n>=0}},0)", //
				"Piecewise({{Log(a)^n/n!,n>=0}},0)");
		check("Piecewise({})", //
				"0");
		check("Piecewise({},0)", "0");
		check("Piecewise({{0, x <= 0}}, 1)", //
				"Piecewise({{0,x<=0}},1)");
		check("Piecewise({{1, False}})", //
				"0");
		check("Piecewise({{0 ^ 0, False}}, -1)", //
				"-1");

		check("$pw = Piecewise({{Sin(x)/x, x < 0}, {1, x == 0}}, -x^2/100 + 1); $pw /. {{x -> -5}, {x -> 0}, {x -> 5}}", //
				"{Sin(5)/5,1,3/4}");
		check("Piecewise({{e1, True}, {e2, d2}, {e3, d3}}, e0)", //
				"e1");
		check("Piecewise({{e1, d1}, {e2, d2}, {e3, True}, {e4, d4}, {e5, d5}}, e0)", //
				"Piecewise({{e1,d1},{e2,d2},{e3,True}})");
		check("Piecewise({{e1, d1}, {e2, d2}, {e3, d2 && d3}, {e4, d4}}, e0)", //
				"Piecewise({{e1,d1},{e2,d2},{e3,d2&&d3},{e4,d4}},e0)");
		check("Piecewise({{e1, d1}, {e2, d2}, {e3, False}, {e4, d4}, {e5, d5}}, e0)", //
				"Piecewise({{e1,d1},{e2,d2},{e4,d4},{e5,d5}},e0)");
	}

	public void testPlus() {
		check("1-{0,1,2}", //
				"{1,0,-1}");
		check("-Infinity+0.0", //
				"-Infinity");
		check("Infinity+0.0", //
				"Infinity");
		check("-Infinity+Sin(-2)", //
				"-Infinity");
		check("-Infinity+Log(2)", //
				"-Infinity");
		check("Infinity+Sin(-2)", //
				"Infinity");
		check("Infinity+Log(2)", //
				"Infinity");

		check("{1,2}+{4,5,6}", //
				"{1,2}+{4,5,6}");
		// check("2+4/3*2^b/c", //
		// "2+2^(2+b)/(3*c)");
		check("Refine(Infinity+x, x>0)", //
				"Infinity");

		// String s = System.getProperty("os.name");
		// if (s.contains("Windows")) {
		check("N(Pi, 30) + N(E, 30)", //
				"5.85987448204883847382293085463");
		check("N(Pi, 30) + N(E, 30) // Precision", //
				"30");
		check("N(Pi, 30) + I", //
				"3.14159265358979323846264338327+I*1");
		check("N(Pi, 30) + E", //
				"5.85987448204883832925824168169");
		// }
		check("1 + 2", //
				"3");
		check("a + b + a", //
				"2*a+b");
		check("a + a + 3 * a", //
				"5*a");
		check("a + b + 4.5 + a + b + a + 2 + 1.5*b", //
				"6.5+3*a+3.5*b");
		check("Plus @@ {2, 4, 6}", //
				"12");
		check("Plus @@ Range(1000)", //
				"500500");
		check("a /. n_. + x_ :> {n, x}", //
				"{0,a}");
		check("-2*a - 2*b", //
				"-2*a-2*b");
		check("1 - I * Sqrt(3)", //
				"1-I*Sqrt(3)");
		check("Head(3 + 2*I)", //
				"Complex");

		check("Interval({1,6})+Interval({0,2})", //
				"Interval({1,8})");
		check("Interval({a,b})+z", //
				"z+Interval({a,b})");
		check("(Interval({-1,1})+1/2)^2 - 1/4", //
				"Interval({-1/4,2})");
		check("f+Interval({a,b})+Interval({c,d})", //
				"f+Interval({a+c,b+d})");
		check("Interval({a,b})+Interval({c,d})", //
				"Interval({a+c,b+d})");
		check("1+Interval({2,3})", //
				"Interval({3,4})");
		check("Plus()", //
				"0");
	}

	public void testPochhammer() {
		check("Pochhammer(m, n) // FunctionExpand", //
				"Gamma(m+n)/Gamma(m)");
		check("Pochhammer(2.4, 8.5)", //
				"2.31022*10^6");
		check("Pochhammer(1.011111111111000000000000000, 8)", //
				"4.1552275849087711206044715725*10^4");

		// http://oeis.org/A054654
		check("crow(n_) := Reverse( CoefficientList( (-1)^n*Pochhammer(-x, n), x) ); Flatten( Table(crow(n), {n, 0, 8}))", //
				"{1,1,0,1,-1,0,1,-3,2,0,1,-6,11,-6,0,1,-10,35,-50,24,0,1,-15,85,-225,274,-120,0,1,\n"
						+ "-21,175,-735,1624,-1764,720,0,1,-28,322,-1960,6769,-13132,13068,-5040,0}");
		check("Pochhammer(0, 0)", //
				"1");
		check("Pochhammer(0, 42)", //
				"0");
		check("Pochhammer(0, -42)", //
				"1/1405006117752879898543142606244511569936384000000000");
		check("Pochhammer(a, -3)", //
				"1/((-3+a)*(-2+a)*(-1+a))");
		check("Pochhammer(a, -1)", //
				"1/(-1+a)");
		check("Pochhammer(b-c, -10)", //
				"1/((-10+b-c)*(-9+b-c)*(-8+b-c)*(-7+b-c)*(-6+b-c)*(-5+b-c)*(-4+b-c)*(-3+b-c)*(-2+b-c)*(\n"
						+ "-1+b-c))");

		check("Pochhammer(b-c, 2)", //
				"(b-c)*(1+b-c)");
		check("Pochhammer(b-c, 3)", //
				"(b-c)*(1+b-c)*(2+b-c)");
		check("Pochhammer(b-c, 10)", //
				"(b-c)*(1+b-c)*(2+b-c)*(3+b-c)*(4+b-c)*(5+b-c)*(6+b-c)*(7+b-c)*(8+b-c)*(9+b-c)");

		check("Pochhammer(2,3)", //
				"24");

		check("Pochhammer(4, 8)", //
				"6652800");
		check("Pochhammer(10, 6)", //
				"3603600");
		check("Pochhammer(10, -6)", //
				"1/60480");
		check("Pochhammer(-10, -6)", //
				"1/5765760");
		check("Pochhammer(-10, -7)", //
				"-1/98017920");
		check("Pochhammer(-10, -12)", //
				"1/309744468633600");
		check("Pochhammer(3/2, 1/2)", //
				"2/Sqrt(Pi)");
		check("Pochhammer(-5, -3)", //
				"-1/336");

	}

	public void testPolyGamma() {
		check("PolyGamma(0,-42)", //
				"ComplexInfinity");
		check("PolyGamma(-1,z)", //
				"LogGamma(z)");
		check("PolyGamma(-1,0)", //
				"Infinity");
		check("PolyGamma(-1,12)", //
				"Log(39916800)");
		check("PolyGamma(-1,-7)", //
				"Infinity");
		check("PolyGamma(-1)", //
				"ComplexInfinity");
		check("PolyGamma(-1,1)", //
				"0");
		check("PolyGamma(-2)", //
				"ComplexInfinity");
		check("PolyGamma(1)", //
				"-EulerGamma");
		check("PolyGamma(2)", //
				"1-EulerGamma");
		check("PolyGamma(3)", //
				"3/2-EulerGamma");
		check("PolyGamma(1,1/4)", //
				"8*Catalan+Pi^2");
		check("PolyGamma(1,3/4)", //
				"-8*Catalan+Pi^2");
		check("PolyGamma(2,5/6)", //
				"4*Sqrt(3)*Pi^3-182*Zeta(3)");
	}

	public void testPolyLog() {
		check("PolyLog(2,0)", //
				"0");
		check("PolyLog(2,-1)", //
				"-Pi^2/12");
		check("PolyLog(2,1)", //
				"Pi^2/6");
		check("PolyLog(2,1/2)", //
				"Pi^2/12-Log(2)^2/2");
		check("PolyLog(2,2)", //
				"Pi^2/4-I*Pi*Log(2)");
		check("PolyLog(2,I)", //
				"I*Catalan-Pi^2/48");
		check("PolyLog(2,-I)", //
				"-I*Catalan-Pi^2/48");
		check("PolyLog(2,1-I)", //
				"-I*Catalan+Pi^2/16-I*1/4*Pi*Log(2)");
		check("PolyLog(2,1+I)", //
				"I*Catalan+Pi^2/16+I*1/4*Pi*Log(2)");
		check("PolyLog(3,1)", //
				"Zeta(3)");
		check("PolyLog(f(x),-1)", //
				"(-1+2^(1-f(x)))*Zeta(f(x))");
		check("PolyLog(0,f(x))", //
				"f(x)/(1-f(x))");
		check("PolyLog(1,f(x))", //
				"-Log(1-f(x))");
		check("PolyLog(-1,f(x))", //
				"f(x)/(1-f(x))^2");
		check("PolyLog(-2,f(x))", //
				"(-f(x)*(1+f(x)))/(-1+f(x))^3");
		check("PolyLog(-3,f(x))", //
				"(f(x)*(1+4*f(x)+f(x)^2))/(1-f(x))^4");
	}

	public void testPolynomialExtendedGCD() {
		check("PolynomialExtendedGCD(2*x^2+3,3*x,f(x,y))", //
				"{1,{0,1/(3*x)}}");
		check("PolynomialExtendedGCD(a[x],b[x],x)", //
				"{b(x),{0,1}}");
		check("PolynomialExtendedGCD(a[x],b,x)", //
				"{1,{0,1/b}}");
		check("PolynomialExtendedGCD(a,b,x)", //
				"{1,{0,1/b}}");

		// TODO make result consistent with PolynomiaGCD
		check("PolynomialExtendedGCD(e*x^2 + d, ( -2*d*e^2*Sqrt(-e/d) )*x + 2*d*e^2, x )", //
				"{-1/Sqrt(-e/d)+x,{0,-1/(2*d*e^2*Sqrt(-e/d))}}");

		// Wikipedia: finite field GF(28) - p = x8 + x4 + x3 + x + 1, and a = x6 + x4 +
		// x + 1
		check("PolynomialExtendedGCD(x^8 + x^4 + x^3 + x + 1, x^6 + x^4 + x + 1, x, Modulus->2)", //
				"{1,{1+x^2+x^3+x^4+x^5,x+x^3+x^6+x^7}}");

		// check("PolynomialExtendedGCD((x - a)*(b*x - c)^2, (x - a)*(x^2 -
		// b*c), x)", "");
		check("PolynomialExtendedGCD((x - 1)*(x - 2)^2, (x - 1)*(x^2 - 3), x)", //
				"{-1+x,{7+4*x,9-4*x}}");

		check("PolynomialExtendedGCD((x - 1)^2*(x - 2)^2, (x - 1)*(x^2 - 3), x)", //
				"{-1+x,{1/2*(19+11*x),1/2*(-26+36*x-11*x^2)}}");
		check("PolynomialExtendedGCD((x - 1)^2*(x - 2)^2, (x - 1)*(x^2 - 3), x,  Modulus -> 2)", //
				"{1+x^2,{1,1+x}}");

		check("PolynomialExtendedGCD(a*x^2 + b*x + c, x - r, x)", //
				"{1,{1/(c+b*r+a*r^2),(-b-a*r-a*x)/(c+b*r+a*r^2)}}");

	}

	public void testPolynomialGCD() {
		check("PolynomialGCD(f(x),f(x)*x^2)", //
				"f(x)");

		// wikipedia example https://en.wikipedia.org/wiki/Polynomial_greatest_common_divisor
		check("PolynomialGCD(x^2 + 7*x + 6, x^2-5*x-6)", //
				"1+x");

		check("PolynomialGCD(a,b )", //
				"1");
		check("PolynomialGCD(e*x^2 + d, ( -2*d*e^2*Sqrt(-e/d) )*x + 2*d*e^2 )", //
				"1");

		// TODO difference to MMA handle Extension->Automatic correctly
		check("PolynomialGCD(x^2 - 2, x - Sqrt(2))", //
				"-Sqrt(2)+x");

		// check("PolynomialGCD(I*2,12)", "2");
		check("PolynomialGCD(a+b*x,c+d*x)", //
				"1");
		check("PolynomialGCD()", //
				"PolynomialGCD()");
		check("PolynomialGCD(x)", //
				"x");
		check("PolynomialGCD(-12)", //
				"12");
		check("PolynomialGCD(x,x)", //
				"x");

		check("PolynomialGCD((x + 1)^3, x^3 + x, Modulus -> 2)", //
				"(1+x)^2");
		check("PolynomialGCD((x - a)*(b*x - c)^2, (x - a)*(x^2 - b*c))", //
				"-a+x");
		check("PolynomialGCD((1 + x)^2*(2 + x)*(4 + x), (1 + x)*(2 + x)*(3 + x))", //
				"2+3*x+x^2");
		check("PolynomialGCD(x^4 - 4, x^4 + 4*x^2 + 4)", //
				"2+x^2");

		check("PolynomialGCD(x^2 + 2*x*y + y^2, x^3 + y^3)", //
				"x+y");
		check("PolynomialGCD(x^2 - 1, x^3 - 1, x^4 - 1, x^5 - 1, x^6 - 1, x^7 - 1)", //
				"-1+x");

		check("PolynomialGCD(x^2 - 4, x^2 + 4*x + 4)", //
				"2+x");
		check("PolynomialGCD(3*x + 9, 6*x^3 - 3*x + 12)", //
				"3");
	}

	public void testPolynomialLCM() {
		check("PolynomialLCM(f(x)*y^3,f(x)*x^2)", //
				"x^2*y^3*f(x)");
		// wikipedia example https://en.wikipedia.org/wiki/Polynomial_greatest_common_divisor
		check("PolynomialLCM(x^2 + 7*x + 6, x^2-5*x-6)", //
				"-36-36*x+x^2+x^3");

		check("PolynomialLCM(a+b*x,c+d*x)", //
				"(a+b*x)*(c+d*x)");
		check("PolynomialLCM()", //
				"PolynomialLCM()");
		check("PolynomialLCM(x)", //
				"x");
		check("PolynomialLCM(x,x)", //
				"x");
		check("PolynomialLCM(-12)", //
				"12");

		check("Expand((-1+x)*(1+x)*(1+x^2)*(1-x+x^2)*(1+x+x^2)*(1+x+x^2+x^3+x^4)*(1+x+x^2+x^3+x^4+x^5+x^6))", //
				"-1-2*x-4*x^2-6*x^3-8*x^4-9*x^5-9*x^6-7*x^7-4*x^8+4*x^10+7*x^11+9*x^12+9*x^13+8*x^\n"
						+ "14+6*x^15+4*x^16+2*x^17+x^18");

		check("PolynomialLCM((1 + x)^2*(2 + x)*(4 + x), (1 + x)*(2 + x)*(3 + x))", //
				"24+74*x+85*x^2+45*x^3+11*x^4+x^5");
		check("Expand((1+x)^2*(2+x)*(3+x)*(4+x))", //
				"24+74*x+85*x^2+45*x^3+11*x^4+x^5");

		check("PolynomialLCM(x^2 + 2*x*y + y^2, x^3 + y^3)", //
				"x^4+x^3*y+x*y^3+y^4");
		check("Expand((x+y)*(x^3+y^3))", //
				"x^4+x^3*y+x*y^3+y^4");

		check("PolynomialLCM(x^2 - 1, x^3 - 1, x^4 - 1, x^5 - 1, x^6 - 1, x^7 - 1)", //
				"-1-2*x-4*x^2-6*x^3-8*x^4-9*x^5-9*x^6-7*x^7-4*x^8+4*x^10+7*x^11+9*x^12+9*x^13+8*x^\n"
						+ "14+6*x^15+4*x^16+2*x^17+x^18");

	}

	public void testPolynomialQ() {
		check("PolynomialQ(7*y^w, y )", //
				"False");
		check("PolynomialQ(7*y^(3*w), y )", //
				"False");
		check("PolynomialQ(c*x^(-2)+a+b*x,x)", //
				"False");

		check("PolynomialQ(x^2 + 7*x + 6)", //
				"True");
		check("PolynomialQ(x^(1/2) + 6*Sin(x), {})", //
				"True");
		// only 1 arg gives always True ?
		check("PolynomialQ(x^(1/2) + 6*Sin(x))", //
				"True");

		check("PolynomialQ(Cos(x*y), Cos(x*y))", //
				"True");
		check("PolynomialQ(x^3,x^2)", //
				"False");
		check("PolynomialQ(2*x^3,x^2)", //
				"False");

		check("PolynomialQ(2*x,x^2)", //
				"False");
		check("PolynomialQ(x,x^2)", //
				"False");
		check("PolynomialQ(x^2,x^2)", //
				"True");

		check("PolynomialQ(3*a,x)", //
				"True");
		check("PolynomialQ(x^2*y^3+34*x^2+7-Sin(z^3)*x^34, {x,y})", //
				"True");
		check("PolynomialQ(x^2*y^3+34*x^2+7-Sin(z^3)*x^34, {x,y,z})", //
				"False");
		check("PolynomialQ(E^f(x),x)", //
				"False");
		check("PolynomialQ(E^f(y),x)", //
				"True");
		check("PolynomialQ(Tan(x),x)", //
				"False");
		check("PolynomialQ(f(x), x)", //
				"False");
		check("PolynomialQ(f(a)+f(a)^2, f(a))", //
				"True");
		check("PolynomialQ(Sin(f(a))+f(a)^2, f(a))", //
				"False");
		check("PolynomialQ(x^3 - 2*x/y + 3*x*z, x)", //
				"True");
		check("PolynomialQ(x^3 - 2*x/y + 3*x*z, y)", //
				"False");
		check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {x, y})", //
				"True");
		check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {a, b, c})", //
				"False");

		check("PolynomialQ((1+x)^3*(1-y-x)^2, x)", //
				"True");
		check("PolynomialQ(x+Sin(x), x)", //
				"False");
		check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {x, y})", //
				"True");
		check("PolynomialQ(x^2 + a*x*y^2 - b*Sin(c), {a, b, c})", //
				"False");

		check("PolynomialQ(f(a)+f(a)^2,f(a))", //
				"True");

		check("PolynomialQ(I*x^(3)+(x+2)^2,x)", //
				"True");
		check("PolynomialQ(I,x)", //
				"True");
		check("PolynomialQ(a,x)", //
				"True");
		check("PolynomialQ(a,{x,y,z})", //
				"True");
		check("PolynomialQ(x,x)", //
				"True");
		check("PolynomialQ(x,{x,y,z})", //
				"True");
	}

	public void testPolynomialQuotient() {
		check("PolynomialQuotient(x^2 + 7*x + 6, x^2-5*x-6, x)", //
				"1");
		check("PolynomialQuotient(a+b*x,1,x)^3", //
				"(a+b*x)^3");
		check("PolynomialQuotient(x^2, x + a,x)", //
				"-a+x");
		check("PolynomialQuotient(x^2 + x + 1, 2*x + 1, x)", //
				"1/4+x/2");
		check("PolynomialQuotient(x^2 + b*x + 1, a*x + 1, x)", //
				"-1/a^2+b/a+x/a");
		check("PolynomialQuotient(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 2)", //
				"1+x^2");
		check("PolynomialQuotient(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 3)", //
				"1+2*x");
	}

	public void testPolynomialQuotientRemainder() {
		check("PolynomialQuotientRemainder(2*x^2+3,3*x,f(x,y))", //
				"{(3+2*x^2)/(3*x),0}");
		check("PolynomialQuotientRemainder(x^2 + 7*x + 6, x^2-5*x-6, x)", //
				"{1,12+12*x}");
		check("PolynomialQuotientRemainder[a,b,x]", //
				"{a/b,0}");
		check("PolynomialQuotientRemainder(e*x^2 + d, ( -2*d*e^2*Sqrt(-e/d) )*x + 2*d*e^2, x )", //
				"{1/(2*e^2)-x/(2*d*e*Sqrt(-e/d)),0}");
		check("PolynomialQuotientRemainder(x^2, x + a,x)", //
				"{-a+x,a^2}");
		check("PolynomialQuotientRemainder(x^2 + x + 1, 2*x + 1, x)", //
				"{1/4+x/2,3/4}");
		check("PolynomialQuotientRemainder(x^2 + b*x + 1, a*x + 1, x)", //
				"{-1/a^2+b/a+x/a,1+1/a^2-b/a}");

		check("PolynomialQuotientRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 2)", //
				"{1+x^2,0}");
		check("PolynomialQuotientRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 3)", //
				"{1+2*x,0}");
	}

	public void testPolynomialRemainder() {
		check("PolynomialRemainder(x^2 + 7*x + 6, x^2-5*x-6, x)", //
				"12+12*x");
		check("PolynomialRemainder(1,Sin(e+f*x),x)", //
				"PolynomialRemainder(1,Sin(e+f*x),x)");
		check("PolynomialRemainder(x^2, x + a,x)", //
				"a^2");
		check("PolynomialRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 2)", //
				"0");
		check("PolynomialRemainder(x^2 + 4*x + 1, 2*x + 1, x, Modulus -> 5)", "3");
	}

	public void testPosition() {
		check("Count({1, \"f\", g, \"h\", \"7\"}, _?StringQ)", //
				"3");
		check("Length(Position({1, \"f\", g, \"h\", \"7\"}, _?StringQ))", //
				"3");
		check("Position({1 + x^2, 5, x^4, a + (1 + x^2)^2}, x^_)", //
				"{{1,2},{3},{4,2,1,2}}");
		check("Extract({1 + x^2, 5, x^4, a + (1 + x^2)^2}, {{1,2},{3},{4,2,1,2}})", //
				"{x^2,x^4,x^2}");
		check("Position(x^2 + y^2, Power, Heads->False)", //
				"{}");
		check("Position(x^2 + y^2, Power)", //
				"{{1,0},{2,0}}");
		check("Position(f(g(h(x))), _, Infinity)", //
				"{{0},{1,0},{1,1,0},{1,1,1},{1,1},{1}}");
		check("Position({a, b, a, a, b, c, b, a, b}, b, 1, 2)", //
				"{{2},{5}}");
		check("Position({1 + x^2, 5, x^4, a + (1 + x^2)^2}, x^_)", //
				"{{1,2},{3},{4,2,1,2}}");
		check("Position({1 + x^2, 5, x^4, a + (1 + x^2)^2}, x^_, 2)", //
				"{{1,2},{3}}");
		check("Position({1, 2, 2, 1, 2, 3, 2}, 2)", //
				"{{2},{3},{5},{7}}");
		check("Position({1 + Sin(x), x, (Tan(x) - y)^2}, x, 3)", //
				"{{1,2,1},{2}}");
		check("Position({1 + x^2, x*y ^ 2,  4*y,  x ^ z}, x^_)", //
				"{{1,2},{4}}");
		check("Position(_Integer)[{1.5, 2, 2.5}]", //
				"{{2}}");

		check("Position({1.0, 2+3, b}, _Integer)", //
				"{{2}}");
		check("Position(_Integer)[{1.0, 2+3, b}]", //
				"{{2}}");
		check("Position(_Integer)[{1.0, 2, b}]", //
				"{{2}}");
		check("Position(_Integer)[{a, 2, b}]", //
				"{{2}}");
		check("Position({x, {x, y}, y},x,1)", //
				"{{1}}");
		check("Position({x, {x, y}, y},x,2)", //
				"{{1},{2,1}}");
		check("Position({x, {x, y}, y},x,{2})", //
				"{{2,1}}");

		check("Position(f(f(g(a), a), a, h(a), f), a, {2, Infinity})", //
				"{{1,1,1},{1,2},{3,1}}");
		check("Position(f(f(g(a), a), a, h(a), f), f, Heads->False)", //
				"{{4}}");
		check("Position(f(f(g(a), a), a, h(a), f), f, Heads->True)", //
				"{{0},{1,0},{4}}");

		check("Position({f(a), g(b), f(c)}, f(x_))", //
				"{{1},{3}}");
	}

	public void testPositive() {
		check("Positive(Infinity)", //
				"True");
		check("Positive(-Infinity)", //
				"False");
		check("Positive(-9/4)", //
				"False");
		check("Positive(0.1+I)", //
				"False");
		check("Positive(1)", //
				"True");
		check("Positive(0)", //
				"False");
		check("Positive(1 + 2*I)", //
				"False");
		check("Positive(Pi)", //
				"True");
		check("Positive(x)", //
				"Positive(x)");
		check("Positive(Sin({11, 14}))", //
				"{False,True}");
	}

	public void testPossibleZeroQ() {
		check("PossibleZeroQ(-Cos(x)/(1-Cos(x))+Sin(x)^2/(1-Cos(x))^2-1/(1-Cos(x)))", //
				"True");
		check("PossibleZeroQ(2^(2*I) - 2^(-2*I) - 2*I*Sin(Log(4)))", //
				"True");
		check("PossibleZeroQ(E^Pi - Pi^E)", //
				"False");
		check("PossibleZeroQ((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)", //
				"True");
		check("PossibleZeroQ(E^(I*Pi/4) - (-1)^(1/4))", //
				"True");
		check("PossibleZeroQ((x + 1)*(x - 1) - x^2 + 1)", //
				"True");
		check("PossibleZeroQ(1/x + 1/y - (x + y)/(x*y))", //
				"True");
		check("PossibleZeroQ(Sqrt(x^2) - x)", //
				"False");
	}

	public void testPower() {
		// check("3^3^3^3^3^3^3", //
		// "Maximum AST size 3175 exceeded");
		check("Power((-x)^(1/2), 2)", //
				"-x");
		check("Power((-x)^(1/3), 3)", //
				"-x");
		check("( (-11)^(1/3))^3", //
				"-11");
		check("(-(-89)^(1/3))^3-89", //
				"0");
		check("Power(a,b,c,d) // FullForm", //
				"Power(a, Power(b, Power(c, d)))");
		check("(I)^(-0.5)", //
				"0.707107+I*(-0.707107)");
		check("(I)^(-1/2)", //
				"-(-1)^(3/4)");
		check("Sqrt(-1)*(-1)^(1/10)", //
				"(-1)^(3/5)");

		check("(-1)^(2/3)", //
				"(-1)^(2/3)");
		check("1/2*Sqrt(2)", //
				"1/Sqrt(2)");
		check("I*1/2*Sqrt(2)", //
				"I/Sqrt(2)");

		check("E^(I*2/3*Pi)", //
				"-1/2+I*1/2*Sqrt(3)");
		check("E^((-11/6)*I*Pi)", //
				"I*1/2+Sqrt(3)/2");
		check("E^((3/4)*I*Pi)", //
				"(-1+I)/Sqrt(2)");
		check("E^((7/6)*I*Pi)", //
				"-I*1/2-Sqrt(3)/2");

		// check("TimeConstrained(1^3^3^3^3, 10)", //
		// "$Aborted");
		// check("TimeConstrained(1^3^3^3, 10)", //
		// "1");
		check("0^(-1)", //
				"ComplexInfinity");

		check("Refine(Exp(I*k*Pi),Element(k,Integers))", //
				"(-1)^k");

		check("Exp(2*I*43*Pi)", //
				"1");
		check("Exp(I*43*Pi)", //
				"-1");
		check("E^(I/2*Pi)", //
				"I");
		check("(I)^(-1)", //
				"-I");
		check("(I)^(-1+k)", //
				"I^(-1+k)");
		check("2^(2/3)*(-5+3*Sqrt[3])^(2/3)", //
				"(2*(-5+3*Sqrt(3)))^(2/3)");
		check("(-1095912791)^(2/3)", //
				"1062961*(-1)^(2/3)");
		check("(-1062961)^(2/3)", //
				"1031*(-1)^(2/3)*1031^(1/3)");
		check("(-27)^(2/3)", //
				"9*(-1)^(2/3)");

		check("(-1)^(1/6)*9178829416159^(1/6)", //
				"(-9178829416159)^(1/6)");
		check("(-(50!)/30!)^(1/6)", //
				"24*(-9178829416159)^(1/6)*Sqrt(2)*5^(5/6)*7^(2/3)*33^(1/3)");
		check("(-27)^(2/3)", //
				"9*(-1)^(2/3)");
		check("(-(50!))^(1/6)", //
				"604800*(-2756205443)^(1/6)*2^(5/6)*Sqrt(13)*33^(2/3)*52003^(1/3)");
		check("604800^6* -621447116887301398870058090208==(-(50!)) ", //
				"True");
		check("(-2)^(-11/4)", //
				"-(-1)^(1/4)/(4*2^(3/4))");
		check("(-2)^(11/4)", //
				"4*(-2)^(3/4)");
		check("(-2)^(4/3)", //
				"-2*(-2)^(1/3)");
		check("1/(-8/27*(-2/3)^(2/5))", //
				"27/8*(-1)^(3/5)*(3/2)^(2/5)");

		check("(-2/3)^(5/2)", //
				"I*4/9*Sqrt(2/3)");

		check("(-2/3)^(17/5)", //
				"-8/27*(-2/3)^(2/5)");
		check("(-2/3)^(-17/5)", //
				"27/8*(-1)^(3/5)*(3/2)^(2/5)");
		check("(-1)^(8/5)", //
				"-(-1)^(3/5)");
		check("(-1)^(12/5)", //
				"(-1)^(2/5)");
		check("(-1)^(5/4)", //
				"-(-1)^(1/4)");
		check("(-1)^(7/4)", //
				"-(-1)^(3/4)");
		check("(-2/3)^(5/4)", //
				"-2/3*(-2/3)^(1/4)");
		check("I^(5/2) - Sqrt(I^5)", //
				"-2*(-1)^(1/4)");

		// github #114
		check("Sqrt(1/(2*Surd(-Cos(9/20*Pi),3)))", //
				"Sqrt(1/(2*Surd(-Cos(9/20*Pi),3)))");
		check("0^(13+n)/a", //
				"0^(13+n)/a");
		check(" 2^(2*I) - 2^(-2*I) - 2*I*Sin(Log(4)) ", //
				"0");
		check("2*Sqrt(2)", //
				"2*Sqrt(2)");
		check("-2*Sqrt(2)", //
				"-2*Sqrt(2)");

		check("(2/3)^(-m)", //
				"(3/2)^m");
		check("(3/2)^(-m)", //
				"(3/2)^(-m)");

		check("(-2/3)*(3/2)^m", //
				"-(2/3)^(1-m)");
		check("(2/3)*(3/2)^m", //
				"(2/3)^(1-m)");
		check("(-2/3)*(x)/(3/2)^m", //
				"-x/(3/2)^(1+m)");
		check("(-2/3)*(x)*(3/2)^m", //
				"-(2/3)^(1-m)*x");
		check("(2/3)*(x)/(3/2)^m", //
				"x/(3/2)^(1+m)");
		check("(2/3)*(x)*(3/2)^m", //
				"(2/3)^(1-m)*x");

		check("(2/3)^(1+m)*x", //
				"(2/3)^(1+m)*x");

		check("Sqrt(Pi/b)", "Sqrt(1/b)*Sqrt(Pi)");
		check("(2*x*y)^n", //
				"2^n*(x*y)^n");
		check("(0.3333*x*y)^n", //
				"0.3333^n*(x*y)^n");
		check("(a*3/4*E*x*y)^n", //
				"(3/4*E)^n*(a*x*y)^n");
		check("(a*42*(-Pi)*x*y)^n", //
				"(42*Pi)^n*(-a*x*y)^n");
		check("(-Infinity)^(42.0)", //
				"Infinity");
		check("(-Infinity)^(43.0)", //
				"-Infinity");
		check("(-Infinity)^(-42.0)", //
				"0");
		check("(-Infinity)^(-43.0)", //
				"0");
		check("(-5/6)^(1/2)", //
				"I*Sqrt(5/6)");
		check("(9/4)^(-3/8)", //
				"(2/3)^(3/4)");
		check("4^(-3/8)", //
				"1/2^(3/4)");
		check("5103^(1/3)", //
				"9*7^(1/3)");
		check("4^(3/8)", //
				"2^(3/4)");

		check("(-42)^Infinity", //
				"ComplexInfinity");
		check("(-1)^Infinity", //
				"Indeterminate");
		check("(-1/2)^Infinity", //
				"0");
		check("(1/4)^Infinity", //
				"0");
		check("(1)^Infinity", //
				"Indeterminate");
		check("(42)^Infinity", //
				"Infinity");

		check("(-42)^(-Infinity)", //
				"0");
		check("(-1)^(-Infinity)", //
				"Indeterminate");
		check("(-1/2)^(-Infinity)", //
				"ComplexInfinity");
		check("(1/4)^(-Infinity)", //
				"Infinity");
		check("(1)^(-Infinity)", //
				"Indeterminate");
		check("(42)^(-Infinity)", //
				"0");

		check("64^(2/3)", //
				"16");
		check("81^(3/4)", //
				"27");
		check("Sqrt(63/5)", //
				"3*Sqrt(7/5)");
		check("Sqrt(9/2)", //
				"3/Sqrt(2)");
		check("Sqrt(1/2)", //
				"1/Sqrt(2)");
		check("1/Sqrt(2)-Sqrt(1/2)", //
				"0");
		check("(2/3)^(-3/4)", //
				"(3/2)^(3/4)");
		check("(y*1/z)^(-1.0)", //
				"z/y");
		check("0^(3+I*4)", //
				"0");
		check("4 ^ (1/2)", //
				"2");
		check("4 ^ (1/3)", //
				"2^(2/3)");
		check("3^123", //
				"48519278097689642681155855396759336072749841943521979872827");
		check("(y ^ 2) ^ (1/2)", //
				"Sqrt(y^2)");
		check("(y ^ 2) ^ 3", //
				"y^6");
		check("4.0 ^ (1/3)", //
				"1.5874");
		check("a /. x_ ^ n_. :> {x, n}", //
				"{a,1}");
		check("(1.5 + 1.0*I) ^ 3.5", //
				"-3.68294+I*6.95139");
		check("(1.5 + 1.0*I) ^ (3.5 + 1.5*I)", //
				"-3.19182+I*0.645659");
		check("1/0", //
				"ComplexInfinity");
		check("0 ^ -2", //
				"ComplexInfinity");
		check("0 ^ (-1/2)", //
				"ComplexInfinity");
		check("0 ^ -Pi", //
				"ComplexInfinity");
		check("0 ^ I", //
				"Indeterminate");
		check("0 ^ (2*I*E)", //
				"Indeterminate");
		check("0 ^ - (Pi + 2*E*I)", //
				"ComplexInfinity");
		check("0^0", //
				"Indeterminate");
		check("Sqrt(-3+2.*I)", //
				"0.550251+I*1.81735");
		check("Sqrt(-3+2*I)", //
				"Sqrt(-3+I*2)");
		check("(3/2+1/2*I)^2", //
				"2+I*3/2");
		check("I ^ I", //
				"I^I");
		check("2 ^ 2.0", //
				"4.0");
		check("Pi ^ 4.", //
				"97.40909");
		check("a^b", //
				"a^b");

		check("54^(1/3)", //
				"3*2^(1/3)");
		check("Exp(y + Log(x))", //
				"E^y*x");
		check("E^(2*(y+Log(x)))", //
				"E^(2*y)*x^2");
		// don't change see issue #137
		check("2^(3+x)", //
				"2^(3+x)");

		check("I^(1/3)", //
				"(-1)^(1/6)");
		check("I^(1/4)", //
				"(-1)^(1/8)");
		check("I^(1/8)", //
				"(-1)^(1/16)");
		check("I^(3/8)", //
				"(-1)^(3/16)");
		check("I^(3/5)", //
				"(-1)^(3/10)");
		check("(-I)^(1/2)", //
				"-(-1)^(3/4)");
		check("(-I)^(1/3)", //
				"-(-1)^(5/6)");
		check("(-I)^(3/5)", //
				"-(-1)^(7/10)");
		check("(-I)^(21/32)", //
				"-(-1)^(43/64)");
		check("(-I)^(32/21)", //
				"-(-1)^(5/21)");
		check("(-I)^(1/4)", //
				"-(-1)^(7/8)");
		check("(-I)^(1/5)", //
				"-(-1)^(9/10)");
		check("(-I)^(1/6)", //
				"-(-1)^(11/12)");
		check("(-I)^(1/24)", //
				"-(-1)^(47/48)");
		check("(-I)^(64/7)", //
				"-(-1)^(3/7)");
		check("(-I)^(71/7)", //
				"(-1)^(13/14)");
		check("27^(1/3)", //
				"3");
		check("5103^(1/3)", //
				"9*7^(1/3)");
		check("5103^(1/2)", //
				"27*Sqrt(7)");
		check("Sqrt(75/4)", //
				"5/2*Sqrt(3)");

		check("0^(-1/2)", //
				"ComplexInfinity");

		check("E^(x+2*Pi*I)", //
				"E^x");
		check("E^(x+11*Pi*I)", //
				"-E^x");
		check("E^(x+Sin(a)+2*Pi*I)", //
				"E^(x+Sin(a))");

		check("(-9/5)*(3)^(-1/2)", //
				"-3/5*Sqrt(3)");
		check("(-1/9)*3^(1/2)", //
				"-1/(3*Sqrt(3))");
		check("3^(1/2)/9", //
				"1/(3*Sqrt(3))");
		check("0^0", //
				"Indeterminate");
		check("27^(1/3)", //
				"3");
		check("(-27)^(1/3)", //
				"3*(-1)^(1/3)");
		check("(-5)^(1/2)", //
				"I*Sqrt(5)");
		check("(-5)^(-1/2)", //
				"-I/Sqrt(5)");
		check("(-(2/3))^(-1/2)", //
				"-I*Sqrt(3/2)");
		check("FullForm(a^b^c)", //
				"Power(a, Power(b, c))");
		check("FullForm((a^b)^c)", //
				"Power(Power(a, b), c)");
		check("(a*b)^3", "a^3*b^3");
		check("(a*b)^(1/2)", //
				"Sqrt(a*b)");
		check("FullForm((a^b)^3)", //
				"Power(a, Times(3, b))");
		check("{2,3,4,5}^3", //
				"{8,27,64,125}");
		check("N(29^(1/3))", //
				"3.07232");
		check("50!^(1/6)", //
				"604800*2^(5/6)*Sqrt(13)*33^(2/3)*52003^(1/3)*2756205443^(1/6)");
		check("(z^(1/3))^3", //
				"z");
		check("(z^3)^(1/3)", //
				"(z^3)^(1/3)");
		check("Sqrt(x^2)", //
				"Sqrt(x^2)");

		check("E^(Log(x))", //
				"x");
		check("E^(y+Log(x))", //
				"E^y*x");
		check("E^(y+Log(x)-z)", //
				"E^(y-z)*x");
		check("E^(y-Log(x)-z)", //
				"E^(y-z)/x");
		check("E^(y+Log(x)-a*Log(v)*b*Log(u)-z)", //
				"E^(y-z-a*b*Log(u)*Log(v))*x");
		check("E^(y-Log(x)+Log(y)-a*Log(v)*b*Log(u)-z)", //
				"(E^(y-z-a*b*Log(u)*Log(v))*y)/x");
		check("Sqrt(1/a)", //
				"Sqrt(1/a)");
	}

	public void testPowerExpand() {
		check("PowerExpand((x*y*z)^n)", //
				"x^n*y^n*z^n");
		check("PowerExpand(Log(x*y))", //
				"Log(x)+Log(y)");
		check("PowerExpand(Log(x^k))", //
				"k*Log(x)");
		check("PowerExpand(Sqrt(-a))", //
				"I*Sqrt(a)");
		check("PowerExpand(Sqrt(a^2))", //
				"a");
		// check("PowerExpand(Sqrt(a/b))", "Sqrt(a)*Sqrt(1/b)");

		check("PowerExpand((a ^ b) ^ c)", //
				"a^(b*c)");
		check("PowerExpand((a * b) ^ c)", //
				"a^c*b^c");
		check("PowerExpand((x ^ 2) ^ (1/2))", //
				"x");

		check("PowerExpand(Log(a*b*c, d))", //
				"Log(d)/(Log(a)+Log(b)+Log(c))");
		check("PowerExpand(Log(a*b*c))", //
				"Log(a)+Log(b)+Log(c)");
		check("PowerExpand(Log(a*b^c,d))", //
				"Log(d)/(Log(a)+c*Log(b))");
		check("PowerExpand(Log(a*b^c))", //
				"Log(a)+c*Log(b)");
		check("PowerExpand(Log(a/b))", //
				"Log(a)-Log(b)");
		check("-2^(1/2)*3^(1/2)", //
				"-Sqrt(6)");
		check("Sqrt(x*y)", //
				"Sqrt(x*y)");
		check("{Sqrt(x*y), Sqrt(x)*Sqrt(y)} /. {x -> -2, y -> -3}", //
				"{Sqrt(6),-Sqrt(6)}");
		check("PowerExpand((a^b)^(1/2))", //
				"a^(b/2)");
		check("Powerexpand((a*b)^(1/2))", //
				"Sqrt(a)*Sqrt(b)");
		check("Powerexpand(Log((a^b)^c))", //
				"b*c*Log(a)");
		check("Powerexpand({y*(a^b)^g, x+(a*b)^42,Log(a^b)})", //
				"{a^(b*g)*y,a^42*b^42+x,b*Log(a)}");
		check("Powerexpand(Sqrt(x^2))", //
				"x");
		check("Powerexpand(Log(1/z))", //
				"-Log(z)");
		check("Powerexpand(2-Log(1/z^3))", //
				"2+3*Log(z)");
		check("Powerexpand(Log(z^a))", //
				"a*Log(z)");
		check("Powerexpand(Sqrt(a* b) + Sqrt(c*d))", //
				"Sqrt(a)*Sqrt(b)+Sqrt(c)*Sqrt(d)");
		check("PowerExpand(Sqrt(x*y))", //
				"Sqrt(x)*Sqrt(y)");

		check("PowerExpand(Log(z^a), Assumptions->True)", //
				"I*2*Pi*Floor((Pi-Im(a*Log(z)))/(2*Pi))+a*Log(z)");

		check("PowerExpand((E^x)^(y), Assumptions->True)", //
				"E^(x*y+I*2*Pi*y*Floor((Pi-Im(x))/(2*Pi)))");
		// "E^(x*y)*E^(I*2*Pi*y*Floor(1/2*(-Im(x)+Pi)*Pi^(-1)))");

		check("PowerExpand((x*y)^(1/2), Assumptions->True)", //
				"E^(I*Pi*Floor(1/2-Arg(x)/(2*Pi)-Arg(y)/(2*Pi)))*Sqrt(x)*Sqrt(y)");
		check("PowerExpand((a*b*c)^(1/3), Assumptions->True)", //
				"a^(1/3)*b^(1/3)*c^(1/3)*E^(I*2/3*Pi*Floor(1/2-Arg(a)/(2*Pi)-Arg(b)/(2*Pi)-Arg(c)/(\n" + "2*Pi)))");
	}

	public void testPowerMod() {
		// 2 is a primitive root for 13
		check("PowerMod(2, Range(12), 13)", //
				"{2,4,8,3,6,12,11,9,5,10,7,1}");
		// 3 is not a primitive root for 13
		check("PowerMod(3, Range(12), 13)", //
				"{3,9,1,3,9,1,3,9,1,3,9,1}");

		// check("PowerMod(6, 1/2, 10)", "1");

		check("PowerMod(2, 10, 3)", //
				"1");
		// similar to Java modInverse()
		check("PowerMod(3, -1, 7)", //
				"5");
		// prints warning
		check("PowerMod(0, -1, 2)", //
				"PowerMod(0,-1,2)");
		// prints warning
		check("PowerMod(5, 2, 0)", //
				"PowerMod(5,2,0)");

		check("PowerMod(2, 10^9, 18)", //
				"16");
		check("PowerMod(2, {10, 11, 12, 13, 14}, 5)", //
				"{4,3,1,2,4}");
		check("PowerMod(147198853397, -1, 73599183960)", //
				"43827926933");

		check("PowerMod(2, 10000000, 3)", //
				"1");
		check("PowerMod(3, -2, 10)", //
				"9");
		check("PowerMod(0, -1, 2)", //
				"PowerMod(0,-1,2)");
		check("PowerMod(5, 2, 0)", //
				"PowerMod(5,2,0)");

	}

	public void testPreDecrement() {
		check("a = 2", //
				"2");
		check("--a", //
				"1");
		check("a", //
				"1");
	}

	public void testPreIncrement() {
		check("a = 2", //
				"2");
		check("++a", //
				"3");
		check("a", //
				"3");
	}

	public void testPrepend() {
		check("Prepend({2, 3, 4}, 1)", //
				"{1,2,3,4}");
		check("Prepend(f(b, c), a)", //
				"f(a,b,c)");
		check("Prepend({c, d}, {a, b})", //
				"{{a,b},c,d}");
		check("Prepend(a, b)", //
				"Prepend(a,b)");

		// operator form
		check("Prepend(a)[{c, d}]", //
				"{a,c,d}");
	}

	public void testPrependTo() {
		check("s = {1, 2, 4, 9}", //
				"{1,2,4,9}");
		check("PrependTo(s, 0)", //
				"{0,1,2,4,9}");
		check("s", //
				"{0,1,2,4,9}");

		check("y = f(a, b, c)", //
				"f(a,b,c)");
		check("PrependTo(y, x)", //
				"f(x,a,b,c)");
		check("y", //
				"f(x,a,b,c)");

		check("PrependTo({a, b}, 1)", //
				"PrependTo({a,b},1)");
		check("PrependTo(a, b)", //
				"PrependTo(a,b)");
		check("x = 1 + 2", //
				"3");
		check("PrependTo(x, {3, 4}) ", //
				"PrependTo(x,{3,4})");

		check("$l = {1, 2, 4, 9};PrependTo($l, 16)", //
				"{16,1,2,4,9}");
		check("$l = {1, 2, 4, 9};PrependTo($l, 16);$l", //
				"{16,1,2,4,9}");
	}

	public void testPrimePi() {
		check("PrimePi(0)", //
				"0");
		check("PrimePi(3.5)", //
				"2");

		check("PrimePi(100)", //
				"25");
		check("PrimePi(-1)", "0");
		check("PrimePi(3.5)"//
				, "2");
		check("PrimePi(E)", //
				"1");

		check("PrimePi(1)", //
				"0");
		check("PrimePi(2)", //
				"1");

		// check("PrimePi(1000000)", "78498");
		check("PrimePi(10000)", //
				"1229");
		check("PrimePi(5.2)", //
				"3");

		check("PrimePi(997)", //
				"168");
		check("Prime(168)", //
				"997");
	}

	public void testPrime() {
		check("Prime(10^6)", //
				"15485863");
		check("Prime(10^7)", //
				"179424673");
		// check("Prime(10^8)", "2038074743");
		// check("Prime(103000000)", "2102429869");

		// above the limit return Prime(...)
		// check("Prime(10^9)", "22801763489");
		// check("Prime(10^10)", "252097800623");
		// check("Prime(10^11)", "2760727302517");
		check("Prime(1)", //
				"2");
		check("Prime(167)", //
				"991");
	}

	public void testPrimeOmega() {
		check("PrimeOmega(-n)", //
				"PrimeOmega(n)");
		check("PrimeOmega(0)", //
				"PrimeOmega(0)");
		check("PrimeOmega(990)", //
				"5");
		check("PrimeOmega(2010)", //
				"4");
		check("PrimeOmega(2^2)", //
				"2");
		check("PrimeOmega(3*2^2)", //
				"3");
		check("PrimeOmega(50!)", //
				"108");
		check("PrimeOmega({1,2,3,4,5,6,20})", //
				"{0,1,1,2,1,2,3}");
		check("PrimeOmega({-1,-2,-3,-4,-5,-6,-20})", //
				"{0,1,1,2,1,2,3}");
	}

	public void testPrimePowerQ() {
		check("PrimePowerQ(1)", //
				"False");

		check("13^9", //
				"10604499373");
		check("PrimePowerQ(10604499373)", //
				"True");
		check("PrimePowerQ(-8)", //
				"True");
		check("PrimePowerQ(9)", //
				"True");
		check("PrimePowerQ(52142)", //
				"False");
		check("PrimePowerQ(371293)", //
				"True");
		check("PrimePowerQ(1)", //
				"False");
	}

	public void testPrimeQ() {
		// Gaussian primes
		// https://en.wikipedia.org/wiki/Gaussian_integer#Gaussian_primes
		check("PrimeQ(-3*I, GaussianIntegers->True)", //
				"True");
		check("PrimeQ(-3*I, GaussianIntegers->False)", //
				"False");
		check("PrimeQ(3*I, GaussianIntegers->True)", //
				"True");
		check("PrimeQ(-3, GaussianIntegers->True)", //
				"True");
		check("PrimeQ(-3)", "True");

		check("PrimeQ({0,1,2,3,4,5,6,7,8,9,10,11}, GaussianIntegers->True)", //
				"{False,False,False,True,False,False,False,True,False,False,False,True}");

		check("PrimeQ({-5-4*I,-5-4*I, -5-2*I, -5+2*I, -5+4*I, " //
				+ "-4-5*I, -4-I, -4+I, -4+5*I, " //
				+ "-3-2*I, -3, -3+2*I, " //
				+ "-2-5*I, -2-3*I, -2-I, -2+I, -2+3*I, -2+5*I, " //
				+ "-1-4*I, -1-2*I, -1-I, -1+I, -1+2*I, -1+4*I, " //
				+ "-3*I, 3*I, 1-4*I, 1-2*I, 1-I, 1+I, 1+2*I, 1+4*I, 2-5*I, 2-3*I, 2-I, 2+I, 2+3*I, " //
				+ "2+5*I, 3-2*I, 3, 3+2*I, 4-5*I, 4-I, 4+I, 4+5*I, 5-4*I, 5-2*I, 5+2*I, 5+4*I}, GaussianIntegers->True)", //
				"{True,True,True,True,True,True,True,True,True,True,"//
						+ "True,True,True,True,True,True,True,True,True,True,"//
						+ "True,True,True,True,True,True,True,True,True,True,"//
						+ "True,True,True,True,True,True,True,True,True,True,"//
						+ "True,True,True,True,True,True,True,True,True}");

		// Mersenne Prime
		// https://en.wikipedia.org/wiki/Mersenne_prime
		check("PrimeQ(131071)", //
				"True");
		check("PrimeQ(524287)", //
				"True");
		check("PrimeQ(2147483647)", //
				"True");

		check("PrimeQ(99999999999971)", //
				"True");
		check("Select(Range(100), PrimeQ)", //
				"{2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97}");
		check("PrimeQ(Range(20))", //
				"{False,True,True,False,True,False,True,False,False,False,True,False,True,False,False,False,True,False,True,False}");
	}

	public void testPrimitiveRoot() {
		// check("Select(Range(100), PrimitiveRoot(#) != {} &)", //
		// "");
	}

	public void testPrimitiveRootList() {
		check("PrimitiveRootList(14)", //
				"{3,5}");
		check("PrimitiveRootList(2*Prime(5))", //
				"{7,13,17,19}");
		check("PrimitiveRootList(9)", //
				"{2,5}");
		check("PrimitiveRootList(10)", //
				"{3,7}");
		check("PrimitiveRootList(7)", //
				"{3,5}");
		check("PrimitiveRootList(12)", //
				"{}");
		check("PrimitiveRootList(19)", //
				"{2,3,10,13,14,15}");
		check("PrimitiveRootList(43)", //
				"{3,5,12,18,19,20,26,28,29,30,33,34}");
	}

	public void testPrint() {
		check("do(print(i0);if(i0>4,Return(toobig)), {i0,1,10})", //
				"toobig");
	}

	public void testProbability() {
		// check("RandomVariate(NormalDistribution(), 10)", //
		// "{-0.21848,1.67503,0.78687,0.9887,2.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223}");
		check("Probability(x^2 + 3*x < 11,Distributed(x,{-0.21848,1.67503,0.78687,0.9887,2.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223}))", //
				"9/10");
		check("Probability(#^2 + 3*# < 11 &, {-0.21848,1.67503,0.78687,0.9887,2.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223})", //
				"9/10");
		check("Probability(#^2 + 3*# < 11 &, {-0.21848,1.67503,0.78687,4.9887,7.06587,-1.27856,0.79225,-0.01164,2.48227,-0.07223})", //
				"7/10");
		check("PDF(PoissonDistribution(a))", //
				"Piecewise({{a^#1/(E^a*#1!),#1>=0}},0)&");
		//
		check("1/(2!*E) + 1/(3!*E)+ 1/(4!*E)+ 1/(5!*E)+ 1/(6!*E) ", //
				"517/720*1/E");
		check("Probability(x<=3, Distributed(x, GeometricDistribution(1/5)))", //
				"369/625");
		check("Probability(x<=3, Distributed(x, PoissonDistribution(m)))", //
				"E^(-m)+m/E^m+m^2/(2*E^m)+m^3/(6*E^m)");
		check("Probability(3 == x, Distributed(x, PoissonDistribution(1)))", //
				"1/(6*E)");
		check("Probability(1.1 <= x <= 6.9, Distributed(x, PoissonDistribution(1)))", //
				"517/720*1/E");
		check("Probability(1.1 < x < 6.9, Distributed(x, PoissonDistribution(1)))", //
				"517/720*1/E");
		check("Probability(1 < x < 7, Distributed(x, PoissonDistribution(1)))", //
				"517/720*1/E");
	}

	public void testProduct() {

		// prints RecursionLimitExeceeded
		check("Product(f(x), {x, x, x+1})", //
				"Product(f(x),{x,x,1+x})");

		check("Product(f(x), {x, x, x})", //
				"f(x)");
		check("Product(f(x), {x, a, a+1})", //
				"f(a)*f(1+a)");

		check("Product(k^3, {k, 1, n})", //
				"(n!)^3");

		check("Product(0, {k, a, Infinity})", //
				"0");
		check("Product(1, {k, a, Infinity})", //
				"1");
		check("Product(42, {k, a, Infinity})", //
				"Infinity");
		// {k,a,n} assumes a<=k<=n
		check("Product(2, {k, a, n})", //
				"2^(1-a+n)");
		// {k,1,n} assumes 1<=k<=n
		check("Product(k^3, {k, 1, n})", //
				"(n!)^3");

		check("Product(i^2, {i,11,2})", //
				"1");
		check("Product(i^2, {i,11,2,-1})", //
				"1593350922240000");
		check("Product(i^2, {i,2,11})", //
				"1593350922240000");
		check("Product(i^2, {i,m,n})", //
				"Pochhammer(m,1-m+n)^2");
		check("Product(i^2, {i,k,k+j})", //
				"Pochhammer(k,1+j)^2");

		check("Product(a, {a, 1, 5})", //
				"120");
		check("Product(f(a), {a, 1, 5})", //
				"f(1)*f(2)*f(3)*f(4)*f(5)");
		check("Product(a^2, {a, 4})", //
				"576");
		check("Product(a + b, {a, 1, 2}, {b, 1, 3})", //
				"1440");

		check("Product(k, {k, 1, 10})", //
				"3628800");
		check("10!", //
				"3628800");
		check("Product(x^k, {k, 2, 20, 2})", //
				"x^110");
		check("Product(2 ^ i, {i, 1, n})", //
				"2^(1/2*n*(1+n))");
		check("Product(k, {k, 3, n})", //
				"n!/2");
		check("Product(k, {k, 10, n})", //
				"n!/362880");

		check("primorial(0) = 1", //
				"1");
		check("primorial(n_Integer) := Product(Prime(k), {k, 1, n})", //
				"");
		check("primorial(12)", //
				"7420738134810");

		check("Product(i^2 - i + 10 ,{i,1,10})", //
				"1426481971200000");
		check("Product(a^i, {i, n})", //
				"a^(1/2*n*(1+n))");
		check("Product(c, {j, 2}, {i, 1, j})", //
				"c^3");
		check("Product(c, {i, 1, j}, {j, 2})", //
				"c^(2*j)");
		check("Product(c, {i, 1, j}, {j, 1, 2})", //
				"c^(2*j)");
		check("Product(c, {i, 1, n})", //
				"c^n");
		check("Product(c+n, {i, 1, n})", //
				"(c+n)^n");
		check("Product(c+n, {i, 0, n})", //
				"(c+n)^(1+n)");
		check("n!", //
				"n!");
		check("$prod(x_,{x_,1,m_}) := m!; $prod(i0, {i0, 1, n0})", //
				"n0!");
		check("Product(i0, {i0, 1, n0})", //
				"n0!");
		check("Product(i^2, {i, 1, n})", //
				"(n!)^2");
		check("Product(i0^2, {i0, 0, n0})", //
				"0");
		check("Product(4*i0^2, {i0, 0, n0})", //
				"0");
		check("Product(i0^3, {i0, 1, n0})", //
				"(n0!)^3");
		check("Product(i0^3+p^2, {i0, 1, n0})", //
				"Product(i0^3+p^2,{i0,1,n0})");
		check("Product(p, {i0, 1, n0})", //
				"p^n0");
		check("Product(p+q, {i0, 1, n0})", //
				"(p+q)^n0");
		check("Product(p, {i0, 0, n0})", //
				"p^(1+n0)");
		check("Product(4, {i0, 0, n0})", //
				"4^(1+n0)");

		check("Product(c, {i, 1, n}, {j, 1, n})", //
				"(c^n)^n");
		check("Product(c, {j, 1, n}, {i, 1, j})", //
				"c^(1/2*n*(1+n))");
		check("Product(f(i, j), {i, 1, 3}, {j, 1, 3})", //
				"f(1,1)*f(1,2)*f(1,3)*f(2,1)*f(2,2)*f(2,3)*f(3,1)*f(3,2)*f(3,3)");
		check("Product(f(i, j), {i, 1, 3, 2}, {j, 1, 3, 1/2})", //
				"f(1,1)*f(1,3/2)*f(1,2)*f(1,5/2)*f(1,3)*f(3,1)*f(3,3/2)*f(3,2)*f(3,5/2)*f(3,3)");
		// check("Product(2^(j + i0), {i0, 1, p}, {j, 1, i0})", "");
	}

	public void testProductLog() {
		check("ProductLog(-1, -(Pi/2))", //
				"-I*1/2*Pi");
		check("ProductLog(-1, -(1/E))", //
				"-1");
		check("Refine(ProductLog(k, 0), k>1)", //
				"-Infinity");

		check("ProductLog(2.5 + 2*I)", //
				"1.056167968948635+I*3.5256052020787*10^-1");

		check("z == ProductLog(z) * E ^ ProductLog(z)", //
				"True");
		check("ProductLog(0)", //
				"0");
		check("ProductLog(E)", //
				"1");

		String s = System.getProperty("os.name");
		if (s.contains("Windows")) {
			// TODO fix apfloat output for "exponential" format
			check("ProductLog(-1.5)", //
					"-3.2783735915572*10^-2+I*1.549643823350159");
			check("ProductLog({0.2, 0.5, 0.8})", //
					"{1.689159734991095*10^-1,3.517337112491958*10^-1,4.900678588015798*10^-1}");
			check("ProductLog(2.5 + 2*I)", //
					"1.056167968948635+I*3.5256052020787*10^-1");
			check("N(ProductLog(4/10),50)", //
					"2.9716775067313854677972696224702134190445810155012*10^-1");

			check("N(ProductLog(-1),20)", //
					"-3.181315052047641353*10^-1+I*1.3372357014306894089");
		}

		check("ProductLog(-Pi/2)", //
				"I*1/2*Pi");
		check("ProductLog(-1/E)", //
				"-1");

		check("ProductLog(Infinity)", //
				"Infinity");
		check("ProductLog(-Infinity)", //
				"-Infinity");
		check("ProductLog(I*Infinity)", //
				"Infinity");
		check("ProductLog(-I*Infinity)", //
				"Infinity");
		check("ProductLog(ComplexInfinity)", //
				"Infinity");

		check("ProductLog(0,a)", //
				"ProductLog(a)");
		check("ProductLog(42,0)", //
				"-Infinity");
		check("ProductLog(-1,(-1/2)*Pi)", //
				"-I*1/2*Pi");
		check("ProductLog(-1,-E^(-1))", //
				"-1");
	}

	public void testProjection() {
		check("Projection({5, 6, 7}, {1, 0, 0})", //
				"{5,0,0}");
		check("Projection({5, 6, 7}, {1, 1, 1})", //
				"{6,6,6}");
		check("Projection({5, I, 7}, {1, 1, 1})", //
				"{4+I*1/3,4+I*1/3,4+I*1/3}");
		check("Projection({x,y}, {a,b}, Dot)", //
				"{(a*(a*x+b*y))/(a^2+b^2),(b*(a*x+b*y))/(a^2+b^2)}");
		check("Projection({x,y}, {a,b})", //
				"{(a*(x*Conjugate(a)+y*Conjugate(b)))/(a*Conjugate(a)+b*Conjugate(b)),(b*(x*Conjugate(a)+y*Conjugate(b)))/(a*Conjugate(a)+b*Conjugate(b))}");
		check("ip(p1_, p2_) := Integrate(p1*p2, {x, -1, 1}); Projection(x^2, LegendreP(2, x), ip)", //
				"2/3*(-1/2+3/2*x^2)");
	}

	public void testProtect() {
		check("Protect(test1, test2, test3)", //
				"{test1,test2,test3}");
		check("Attributes({test1,test2,test3,Plus,Times})", //
				"{{Protected},{Protected},{Protected},{Flat,Listable,NumericFunction,OneIdentity,Orderless,Protected},{Flat,Listable,NumericFunction,OneIdentity,Orderless,Protected}}");
		check("Unprotect(test1, test2)", //
				"{test1,test2}");
		check("Unprotect(test1, test2, test3)", //
				"{test3}");
	}

	public void testPseudoInverse() {
		check("PseudoInverse({{1,2}, {1,2}})", //
				"{{0.1,0.1},\n" + " {0.2,0.2}}");
		check("PseudoInverse({1, {2}})", //
				"PseudoInverse({1,{2}})");
		check("PseudoInverse(PseudoInverse({{1, 2}, {2, 3}, {3, 4}}))", //
				"{{1.0,2.0},\n" + //
						" {2.0,3.0},\n" + //
						" {3.0,4.0}}");
		check("PseudoInverse({{1, 2, 0}, {2, 3, 0}, {3, 4, 1}})", "{{-3.0,2.0,4.44089*10^-16},\n" + //
				" {2.0,-1.0,-2.77556*10^-16},\n" + //
				" {1.0,-2.0,1.0}}");
		check("PseudoInverse({{1.0, 2.5}, {2.5, 1.0}})", //
				"{{-0.190476,0.47619},\n" + //
						" {0.47619,-0.190476}}");
		check("PseudoInverse({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}})", //
				"{{-0.483333,-0.244444,-0.00555556,0.233333},\n" + //
						" {-0.0333333,-0.0111111,0.0111111,0.0333333},\n" + //
						" {0.416667,0.222222,0.0277778,-0.166667}}");
		check("PseudoInverse(N({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}}))", //
				"{{-0.483333,-0.244444,-0.00555556,0.233333},\n" + //
						" {-0.0333333,-0.0111111,0.0111111,0.0333333},\n" + //
						" {0.416667,0.222222,0.0277778,-0.166667}}");
		check("PseudoInverse(N({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}))", //
				"{{-0.638889,-0.166667,0.305556},\n" + //
						" {-0.0555556,-3.50414*10^-16,0.0555556},\n" + //
						" {0.527778,0.166667,-0.194444}}");//

	}

	public void testPutGet() {
		if (Config.FILESYSTEM_ENABLED) {
			check("Put(x + y, \"c:/temp/example_file1.m\"); Get(\"c:/temp/example_file1.m\")", //
					"x+y");
			check("Put(x + y, 2x^2 + 4z!, Cos(x) + I Sin(x), \"c:/temp/example_file2.m\");"
					+ "Get(\"c:/temp/example_file2.m\")", //
					"Cos(x)+I*Sin(x)");
			check("Put(47!, \"c:/temp/test.m\"); Get(\"c:/temp/test.m\")", //
					"258623241511168180642964355153611979969197632389120000000000");
		}
	}

	public void testQRDecomposition() {
		check("QRDecomposition({{1, 2}, {3, 4}, {5, 6}})", //
				"{{{-0.169031,0.897085,0.408248},\n" + " {-0.507093,0.276026,-0.816497},\n"
						+ " {-0.845154,-0.345033,0.408248}},{{-5.91608,-7.43736},\n" + " {0.0,0.828079},\n"
						+ " {0.0,0.0}}}");
		check("QRDecomposition({{1, 2, 3}, {4, 5, 6}})", //
				"{{{-0.242536,0.970143},\n" + " {-0.970143,-0.242536}},{{-4.12311,-5.33578,-6.54846},\n"
						+ " {0.0,0.727607,1.45521}}}");
	}

	public void testQuantile() {
		check("Quantile({10, 50, 10, 15, 20}, 3/4, {{1/2, 0}, {0, 1}})", //
				"55/2");
		check("Quantile(NormalDistribution(m, s))", //
				"ConditionalExpression(m-Sqrt(2)*s*InverseErfc(2*#1),0<=#1<=1)&");
		check("Quantile(NormalDistribution(m, s), q)", //
				"ConditionalExpression(m-Sqrt(2)*s*InverseErfc(2*q),0<=q<=1)");

		check("Quantile({1, 2, 3, 4, 5, 6, 7}, 1/2)", //
				"4");

		check("Quantile({1,2}, 0.5)", //
				"1");
		check("Quantile({Sqrt(2), E, Pi, Sqrt(3)}, 1/4)", //
				"Sqrt(2)");
		check("Quantile({Sqrt(2), E, Pi, Sqrt(3)}, 3/4)", //
				"E");
		check("Quantile(N({E, Pi, Sqrt(2), Sqrt(3)}), 1/4)", //
				"1.41421");
		check("Quantile({1, 2, 3, 4, 5, 6, 7}, 1/2)", //
				"4");
		check("Quantile({1, 2, 3, 4, 5, 6, 7}, 1/4)", //
				"2");
		check("Quantile({1, 2, 3, 4, 5, 6, 7}, {1/4, 3/4})", //
				"{2,6}");
		check("Quantile({1.0, 2.0, 3.0, 4.0, 5, 6, 7}, {1/4, 3/4})", //
				"{2.0,6}");

		check("Quantile({{1,2,3,4},{ E, Pi, Sqrt(2),Sqrt(3)}}, 0.75)", //
				"{E,Pi,3,4}");
		check("Quantile({{1,2},{ E, Pi, Sqrt(2),Sqrt(3)}}, 0.75)", //
				"Quantile({{1,2},{E,Pi,Sqrt(2),Sqrt(3)}},0.75)");
	}

	public void testQuantity() {
		if (ToggleFeature.QUANTITY) {
			check("Quantity(50, \"s\") + Quantity(1, \"min\")", //
					"110[s]");
			check("Quantity(1, \"min\") + Quantity(50, \"s\")", //
					"110[s]");
			// TODO return unevaluated and print "compat" message that types are incompatible
			check("Quantity(0, \"kg\") + Quantity(0, \"A\") + Quantity(0, \"m\")", //
					"0[A]");
			check("Quantity(1, \"min\") + Quantity(120, \"min\")", //
					"121[min]");
			check("Quantity(1, \"min\") + Quantity(50, \"s\")", //
					"110[s]");
			check("Quantity(50, \"s\") + Quantity(1, \"min\") ", //
					"110[s]");

			check("Quantity(50, \"min\") + Quantity(1, \"s\")", //
					"3001[s]");

			check("Table(i, {i, Quantity(5, \"s\"), Quantity(1, \"m\"), Quantity(4, \"s\")})", //
					"Table(i,{i,Quantity(5,s),Quantity(1,m),Quantity(4,s)})");
			check("Table(i, {i, Quantity(5, \"s\"), Quantity(1, \"min\"), Quantity(4, \"s\")})", //
					"{5[s],9[s],13[s],17[s],21[s],25[s],29[s],33[s],37[s],41[s],45[s],49[s],53[s],57[s]}");
			check("Table(i, {i, Quantity(50, \"s\"), Quantity(1, \"min\") })", //
					"{50[s],51[s],52[s],53[s],54[s],55[s],56[s],57[s],58[s],59[s],60[s]}");
			check("Table(i, {i, Quantity(10, \"s\") })", //
					"{1[s],2[s],3[s],4[s],5[s],6[s],7[s],8[s],9[s],10[s]}");
			check("Quantity(1, \"min\")<=Quantity(60, \"s\")", //
					"True");

			check("Quantity(1, \"min\")>Quantity(50, \"s\")", //
					"True");
			check("Quantity(1, \"min\")>Quantity(60, \"s\")", //
					"False");
			check("Quantity(1, \"min\")>=Quantity(60, \"s\")", //
					"True");
			check("Quantity(60, \"s\")<=Quantity(1, \"min\")", //
					"True");
			check("Quantity(60, \"s\")<=Quantity(2, \"min\")", //
					"True");

			check("Quantity(1, \"min\")<Quantity(50, \"s\")", //
					"False");
			check("Quantity(1, \"min\")<Quantity(60, \"s\")", //
					"False");
			check("Quantity(1, \"min\")<=Quantity(60, \"s\")", //
					"True");

			// leave unevaluated because of different unit types
			check("Quantity(1,\"s\")==Quantity(1,\"m\")", //
					"1[s]==1[m]");

			check("Quantity(60, \"s\")==Quantity(1, \"min\")", //
					"True");
			check("Quantity(1, \"min\")==Quantity(60, \"s\")", //
					"True");

			check("Quantity(60, \"s\")!=Quantity(1, \"min\")", //
					"False");
			check("Quantity(1, \"min\")!=Quantity(60, \"s\")", //
					"False");

			check("Quantity(42, \"s\")!=Quantity(1, \"min\")", //
					"True");
			check("Quantity(42, \"min\")!=Quantity(60, \"s\")", //
					"True");

			// github #139
			check("-2+Quantity(1, \"ft\")", //
					"-2+1[ft]");
			check("Quantity(9.8, \"m\")/Quantity(1, \"s\")", //
					"9.8[m*s^-1]");
			check("Quantity(9.8, \"m\")/Quantity(0, \"s\")", //
					"ComplexInfinity[m*s^-1]");
			check("Quantity(0, \"s\")^(-1)", //
					"ComplexInfinity[s^-1]");
			check("2*Quantity(1, \"ft\")", //
					"2[ft]");
			check("0+Quantity(1, \"ft\")", //
					"1[ft]");

			check("0*Quantity(1, \"ft\")", //
					"0[ft]");

			check("Quantity(\"m\")", //
					"1[m]");
			check("Quantity(3.25, \"m *rad\")", //
					"3.25[m*rad]");
			check("Quantity(3, \"Hz^(-2)*N*m^(-1)\")", //
					"3[Hz^-2*N*m^-1]");
			check("0+Quantity(3, \"m\")", //
					"3[m]");
			check("0*Quantity(3, \"m\")", //
					"0[m]");
			check("1*Quantity(3, \"m\")", //
					"3[m]");
			check("Quantity(3, \"m\")", //
					"3[m]");
			check("Quantity(3, \"Meters\")", //
					"3[Meters]");
		}
	}

	public void testQuantityQ() {
		if (ToggleFeature.QUANTITY) {
			check("QuantityQ(Quantity(2, x))", //
					"False");
			check("QuantityQ(Quantity(3, \"m\"))", //
					"True");
			check("QuantityQ(Quantity(3, \"Meters\"))", //
					"True");
		}
	}

	public void testQuantityMagnitude() {
		if (ToggleFeature.QUANTITY) {
			check("QuantityMagnitude(Quantity(2000000000000/8896443230521, \"lbf\"), \"N\")", //
					"1");
			check("QuantityMagnitude(Quantity(1290320000/8896443230521, \"psi\"), \"Pa\")", //
					"1");
			check("QuantityMagnitude(Quantity(6.241509125883258*10^9, \"GeV\"), \"J\")", //
					"1.0");
			check("QuantityMagnitude(Quantity(360, \"deg\"), \"rad\")", //
					"2*Pi");
			check("QuantityMagnitude(Quantity(3.4, \"m\"))", //
					"3.4");
			check("QuantityMagnitude(Quantity(3.4, \"km\"), \"m\")", //
					"3400.0");
		}
	}

	public void testQuiet() {
		check("Quiet(1/0)", //
				"ComplexInfinity");
		check("1/0", //
				"ComplexInfinity");
	}

	public void testQuotient() {
		check("Quotient(Sqrt(-113), 2)", //
				"I*5");
		check("Quotient(42,Pi)", //
				"13");
		check("Quotient(13, 0)", //
				"ComplexInfinity");
		check("Quotient(-17, 7)", //
				"-3");
		check("Quotient(15, -5)", //
				"-3");
		check("Quotient(17, 5)", //
				"3");
		check("Quotient(-17, -4)", //
				"4");
		check("Quotient(-14, 7)", //
				"-2");
		check("Quotient(19, -4)", //
				"-5");
	}

	public void testQuotientRemainder() {
		check("QuotientRemainder(13, 0)", //
				"QuotientRemainder(13,0)");
		check("QuotientRemainder(-17, 7)", //
				"{-3,4}");
		check("QuotientRemainder(15, -5)", //
				"{-3,0}");
		check("QuotientRemainder(17, 5)", //
				"{3,2}");
		check("QuotientRemainder(-17, -4)", //
				"{4,-1}");
		check("QuotientRemainder(-14, 7)", //
				"{-2,0}");
		check("QuotientRemainder(19, -4)", //
				"{-5,-1}");
	}

	public void testExpectation() {
		check("Expectation((#^3)&, {a,b,c})", //
				"1/3*(a^3+b^3+c^3)");
		check("Expectation(2*x+3,Distributed(x,{a,b,c,d}))", //
				"1/4*(12+2*a+2*b+2*c+2*d)");
		check("Expectation(f(x),Distributed(x,{a,b}))", //
				"1/2*(f(a)+f(b))");
		// check("PDF( PoissonDistribution(m),x)", //
		// "Piecewise({{m^x/(E^m*x!),x>=0}},0)");
		// check("Expectation(x^2+7*x+8,Distributed(x,PoissonDistribution(m)))", //
		// "8+8*m+m^2");
		// check("Expectation(E^(2*x) + 3, Distributed( x, PoissonDistribution(l)))", //
		// "");
		//
		// check("Expectation(x,Distributed(x, DiscreteUniformDistribution({4, 9})))", "13/2");
		// check("Expectation(x,Distributed(x, DiscreteUniformDistribution({4, 10})))", "7");
		//
		// check("Expectation(2*x+3,Distributed(x, DiscreteUniformDistribution({4, 9})))", "16");
		// check("Expectation(2*x+3,Distributed(x, DiscreteUniformDistribution({4, 10})))", "17");

	}

	public void testRandom() {

		// message: RandomPrime: Positive integer value expected.
		check("RandomPrime(-11)", //
				"RandomPrime(-11)");
		// message: RandomPrime: There are no primes in the specified interval.
		check("RandomPrime(1)", //
				"RandomPrime(1)");
		// check("RandomPrime(100000000000000000000000000)", //
		// "87660272303062923753002687");
		//

		// check("RandomInteger()", //
		// "0");
		// check("RandomInteger(-10)", //
		// "-4");
		// check("RandomInteger(100)", "");
		// check("RandomReal()", "0.53275");
		//
		// check("{Exp[x], x^2, 1/x, 2}", "{E^x,x^2,1/x,2}");

		// check("RandomReal(5, {7, 11}) // TableForm", //
		// "{{2.18257,2.53954,2.41443,0.310558},{4.86753,4.93809,1.74144,0.188952},{3.15764,4.69217,2.01985,2.71322}}");
	}

	public void testRandomChoice() {
		// check("RandomChoice({1,2,3,4,5,6,7},11.0)", "{2,1,5,3,5,7,4,5,5,6,5}");
		// check("RandomChoice({1,2,3,4,5,6,7},10)", "{3,7,3,6,2,7,4,1,1,4}");
		// check("RandomChoice({False, True})", "True");
		// check("RandomChoice({1,2,3,4,5,6,7})", "3");
	}

	public void testRandomVariate() {
		// check("RandomVariate(BetaDistribution(0.5,0.6), {10})", //
		// "{0.651565,0.0687826,0.53907,0.511176,0.0419515,0.946387,0.995215,0.0896617,0.00242461,0.607517}");
		// check("RandomVariate(FrechetDistribution(0.5,0.6), {10})", //
		// "{288.7521,2.9714,0.403198,2.0156,0.21531,0.0399206,0.665026,1.49444,434.9269,118.3019}");
		// check("RandomVariate(GumbelDistribution(0.5,0.6), {10})", //
		// "{0.983572,1.01258,1.02586,0.351624,0.674945,0.549278,0.173217,0.464434,-0.335133,-0.151538}");
		// check("RandomVariate(GammaDistribution(0.5,0.6), {10})", //
		// "{0.08716,0.39611,0.04844,0.03546,0.57366,0.02071,0.01487,1.65639,0.75104,0.05348}");
		// check("RandomVariate(UniformDistribution({1,3}), {2})", //
		// "{1.95941,2.69658}");
		// check("RandomVariate(NormalDistribution(), 10^1)", //
		// "");
		// check("KolmogorovSmirnovTest({-0.79675,0.3841,-0.84567,0.3421,0.46447,-0.01124,-0.33517,0.82206,1.40563,0.48811},
		// NormalDistribution())", //
		// "0.56794");
		// check("KolmogorovSmirnovTest({-0.79675,0.3841,-0.84567,0.3421,0.46447,-0.01124,-0.33517,0.82206,1.40563,0.48811})",
		// //
		// "0.99446");
		// check("KolmogorovSmirnovTest({-0.79675,0.3841,-0.84567,0.3421,0.46447,-0.01124,-0.33517,0.82206,1.40563,0.48811},"
		// + "{1.64433,-0.31318,1.27263,0.16141,0.21162,-1.01509,-0.76259,0.73259,0.2478,1.28021})", //
		// "0.7869");
		// check("RandomVariate(BinomialDistribution(100,0.25), 10^1)", //
		// "{28,25,30,25,25,26,29,17,33,20}");
		// check("RandomVariate(BernoulliDistribution(0.25), 10^1)", //
		// "{1,0,0,0,1,0,0,0,0,0}");
		// check("RandomVariate(ExponentialDistribution(5.6), 10^1)", //
		// "{0.36309,0.10609,0.14096,0.11642,0.01146,0.11286,0.05236,0.00071,0.01648,0.0303}");
		// check("RandomVariate(DiscreteUniformDistribution({50,1000}), 10^1)", //
		// "{468,989,156,353,469,91,399,304,700,137}");
		// check("RandomVariate(PoissonDistribution(2.0), 10^1)", //
		// "{1,3,5,3,2,2,2,5,1,2}");
		// check("RandomVariate(NormalDistribution(2,3), 10^1)", //
		// "{3.16579,3.4267,6.43772,4.53451,3.45249,6.51662,2.10209,-3.8462,3.87387,-4.47763}");
		// check("RandomVariate(NormalDistribution(2,3))", //
		// "1.99583");
		// check("RandomVariate(NormalDistribution())", //
		// "-0.56291");
		// check("RandomVariate(DiscreteUniformDistribution({3,7}), {2})", "{3,7}");
		// check("RandomVariate(DiscreteUniformDistribution({3,7}), {2,3})", "{{5,4,7},{5,7,3}}");
		// check("RandomVariate(DiscreteUniformDistribution({3,7}), {2,3,4})",
		// "{{{4,5,5,3},{5,4,4,6},{6,3,4,7}},{{6,6,7,3},{4,6,5,6},{7,7,6,5}}}");
		// check("RandomVariate(DiscreteUniformDistribution({3,7}), 10)", "{6,5,7,7,7,7,4,5,6,3}");
		// check("RandomVariate(DiscreteUniformDistribution({1, 5}) )", "3");
	}

	public void testRandomSample() {
		// check("RandomSample(f(1,2,3,4,5),3)", //
		// "f(3,4,1)");
		// check("RandomSample(f(1,2,3,4,5))", //
		// "f(3,4,5,1,2)");
	}

	public void testRange() {
		check("Range(1,1+1/2,1/2)", //
				"{1,3/2}");
		check("Range(1,1.25,0.1)", //
				"{1.0,1.1,1.2}");
		check("Range(5.0)", //
				"{1,2,3,4,5}");
		check("Range(-5.0)", //
				"{}");

		check("Range(0,10,Pi)", //
				"{0,Pi,2*Pi,3*Pi}");
		check("x * Range(-1, 1, 1/5)", //
				"{-x,-4/5*x,-3/5*x,-2/5*x,-x/5,0,x/5,2/5*x,3/5*x,4/5*x,x}");
		check("a + Range(0, 3, Pi/8)", //
				"{a,a+Pi/8,a+Pi/4,a+3/8*Pi,a+Pi/2,a+5/8*Pi,a+3/4*Pi,a+7/8*Pi}");
		check("x^Range(n, n + 10, 2)", //
				"{x^n,x^(2+n),x^(4+n),x^(6+n),x^(8+n),x^(10+n)}");
		check("Range(a, a + 12*n, 2*n)", //
				"{a,a+2*n,a+4*n,a+6*n,a+8*n,a+10*n,a+12*n}");

		check("Range(5)", //
				"{1,2,3,4,5}");
		check("Range(-3, 2)", //
				"{-3,-2,-1,0,1,2}");
		check("Range(0, 2, 1/3)", //
				"{0,1/3,2/3,1,4/3,5/3,2}");
		check("Range(1.2, 2.2, 0.15)", //
				"{1.2,1.35,1.5,1.65,1.8,1.95,2.1}");

		check("Range(0)", //
				"{}");
		check("Range(1)", //
				"{1}");
		check("Range(-1)", //
				"{}");
		check("Range(10)", //
				"{1,2,3,4,5,6,7,8,9,10}");
		check("Range(1,10,2)", //
				"{1,3,5,7,9}");
		check("Range(10,20,3)", //
				"{10,13,16,19}");
		check("Range(10,1,-1)", //
				"{10,9,8,7,6,5,4,3,2,1}");

	}

	public void testRational() {
		check("Head(1/2)", //
				"Rational");
		check("Rational(1, 2)", //
				"1/2");
		check("-2/3", //
				"-2/3");
		check("f(22/7, 201/64, x/y) /. Rational(n_, d_) :> d/n", //
				"f(7/22,64/201,x/y)");
	}

	public void testRationalize() {
		check("Rationalize(x+E)", //
				"E+x");
		check("Rationalize(E)", //
				"E");
		check("Rationalize(E, 0.01)", //
				"19/7");
		check("Rationalize(x+E, 0.01)", //
				"E+x");
		check("Rationalize(x+y)", //
				"x+y");
		check("Rationalize(x+0.3333*y)", //
				"x+3333/10000*y");
		check("ArcCos(-Rationalize(0.5))", //
				"2/3*Pi");
		check("Rationalize(0.202898)", //
				"101449/500000");
		check("Rationalize(1.2 + 6.7*x)", //
				"6/5+67/10*x");
		check("Rationalize(Exp(Sqrt(2)), 2^-12)", //
				"218/53");
		check("Rationalize(6.75)", //
				"27/4");
		check("Rationalize(Pi)", //
				"Pi");
		check("Rationalize(Pi, .01)", //
				"22/7");
		check("Rationalize(Pi, .001)", //
				"333/106");
	}

	public void testRe() {
		check("Re(I*Pi/4 )", //
				"0");
		check("Re(E^(I*Pi/4))", //
				"1/Sqrt(2)");
		check("Re(Sin(42)*Cos(43))", //
				"Cos(43)*Sin(42)");
		check("Re(Sin(42))", //
				"Sin(42)");
		check("Re(I*9*Sin(5))", //
				"0");
		check("Re(3+4*I)", //
				"3");
		check("Re(0.5 + 2.3*I)", //
				"0.5");
		check("Im(0.5 + 2.3*I)", //
				"2.3");
		check("Re(0)", //
				"0");
		check("Re(I)", //
				"0");
		check("Re(Indeterminate)", //
				"Indeterminate");
		check("Re(Infinity)", //
				"Infinity");
		check("Re(-Infinity)", //
				"-Infinity");
		check("Re(ComplexInfinity)", //
				"Indeterminate");
	}

	public void testReal() {
		check("Head(1.5)", //
				"Real");
	}

	public void testRealNumberQ() {
		check("RealNumberQ(10)", //
				"True");
		check("RealNumberQ(4.0)", //
				"True");
		check("RealNumberQ(1+I)", //
				"False");
		check("RealNumberQ(0*I)", //
				"True");
		check("RealNumberQ(0.0*I)", //
				"False");
	}

	public void testReap() {
		check("depthFirstPreorder(expr_) := Module(\n" + "  {stack = {expr, {}}, el = expr},\n" + "  Reap(\n"
				+ "    While(stack =!= {},\n" + "      {el, stack} = stack;\n" + "      Sow(el);\n"
				+ "      If(Not(AtomQ(el)),\n" + "       Do(stack = {el[[j]], stack}, {j, Length(el), 1, -1}));\n"
				+ "      );\n" + "    )[[2, 1]]\n" + "  )", //
				"");
		check("depthFirstPreorder({{1, {2, 3}}, {4, 5}})", //
				"{{{1,{2,3}},{4,5}},{1,{2,3}},1,{2,3},2,3,{4,5},4,5}");

		check("Reap(Sow(1); Sow(2); Sow(3))", //
				"{3,{{1,2,3}}}");
		check("Reap(Sow(1, {x, x}); Sow(1); Sow(2); Sow(3, x) )", //
				"{3,{{1,1,3},{1,2}}}");
		check("Reap(Sow(1, {x, x}); Sow(2, y); Sow(3, x) )", //
				"{3,{{1,1,3},{2}}}");
		check("Reap(Sow(1, {x, x}); Sow(2, y); Sow(3, x), _ )", //
				"{3,{{1,1,3},{2}}}");
		check("Reap(Sow(1, {x, x}); Sow(2, y); Sow(3, x), _, f )", //
				"{3,{f(x,{1,1,3}),f(y,{2})}}");
		check("Reap(Sow(1, {x, x}); Sow(2, y); Sow(3, x), y, f )", //
				"{3,{f(y,{2})}}");
		check("Reap(Sow(1, {x, x}); Sow(2); Sow(3, x), _, f )", //
				"{3,{f(x,{1,1,3}),f(None,{2})}}");
		check("Reap(Sow(1, {x, x}); Sow(2,y); Sow(3, x), _, Rule )", //
				"{3,{x->{1,1,3},y->{2}}}");
		check("Reap(x)", //
				"{x,{}}");
		check("Reap(Sow(a); b; Sow(c); Sow(d); e)", //
				"{e,{{a,c,d}}}");
		check("Reap(Sum(Sow(i0^2) + 1, {i0, 10}))", //
				"{395,{{1,4,9,16,25,36,49,64,81,100}}}");
	}

	public void testRefine() {
		check("Refine(2*Im(x)+3, x>3)", //
				"3");
		check("Refine(x<x, x>0)", //
				"False");
		check("Refine(x>=x, x>0)", //
				"True");
		check("Refine((-1)^(43*k), Element(k, Integers))", //
				"(-1)^k");
		check("Refine((-1)^(42*k), Element(k, Integers))", //
				"1");
		check("Refine((-1)^(2+k), Element(k, Integers))", //
				"(-1)^k");
		check("Refine((-1)^(43+k), Element(k, Integers))", //
				"(-1)^(1+k)");
		check("Refine((-1)^(-43+k), Element(k, Integers))", //
				"(-1)^(1+k)");
		check("Refine((-1)^(4*k), Element(k, Integers))", //
				"1");

		check("Refine(Log(-3/4), x < 0)", //
				"I*Pi-Log(4/3)");
		check("Refine(Log(x), x < 0)", //
				"I*Pi+Log(-x)");

		check("Refine(EvenQ(4*k), Element(k, Integers))", //
				"True");
		// for EvenQ result is undetermined
		check("Refine(EvenQ(3*k), Element(k, Integers))", //
				"False");

		// for OddQ we cann not determine if true/false
		check("Refine(OddQ(4*k), Element(k, Integers))", //
				"False");
		check("Refine(OddQ(3*k), Element(k, Integers))", //
				"False");

		check("Refine(a>0)", //
				"a>0");
		check("Refine(MoebiusMu(p),Element(p, Primes))", //
				"-1");

		// TODO
		// check("Refine((a^b)^c, -1<b&&b<(-1))", "a^(b*c)");
		check("Refine(Log(x)>0, x>1)", //
				"True");
		check("Refine(Log(x)<0, x<1&&x>0)", //
				"True");
		check("Refine(Log(x)<0, x<1&&x>0)", //
				"True");
		check("Refine(Log(x)<0, x<1&&x>=0)", //
				"Log(x)<0");

		check("Refine(x^4<0,x<0)", //
				"False");
		check("Refine(x^(1/2)>=0, x>=0)", //
				"Sqrt(x)>=0");
		check("Refine(x^4>=0,Element(x, Reals))", //
				"True");
		check("Refine(x^4>0,Element(x, Reals))", //
				"x^4>0");
		check("Refine(x^3>=0,Element(x, Reals))", //
				"x^3>=0");
		check("Refine(x^4<0,Element(x, Reals))", //
				"x^4<0");
		check("Refine(x^4<0,x<0)", //
				"False");
		check("Refine(-x^4<=0,Element(x, Reals))", //
				"True");
		check("Refine(-x^4<0,Element(x, Reals))", //
				"x^4>0");
		check("Refine(E^x>0,Element(x, Reals))", //
				"True");

		check("Refine(DiscreteDelta(x),x>0)", //
				"0");
		check("Refine(DiscreteDelta(x),x<-1)", //
				"0");
		check("Refine(DiracDelta(x),x>0)", //
				"0");
		check("Refine(DiracDelta(x),x<-1)", //
				"0");
		check("Refine(UnitStep(-x),x>0)", //
				"0");
		check("Refine(UnitStep(x),x>0)", //
				"1");
		check("Refine(UnitStep(y,x), x>0&&y>0)", //
				"1");

		check("Refine(Re(a+I*b), Element(a, Reals)&&Element(b, Reals))", //
				"a");

		check("(x^3)^(1/3)", //
				"(x^3)^(1/3)");
		check("Refine((x^3)^(1/3), x>=0)", //
				"x");

		check("Refine(Sqrt(x^2), Element(x, Reals))", //
				"Abs(x)");
		check("Refine(Sqrt(x^2), Assumptions -> Element(x, Reals))", //
				"Abs(x)");
		check("Refine(Sqrt(x^2), Element(x, Integers))", //
				"Abs(x)");
		check("Refine(Sqrt(x^2), x>=0)", //
				"x");
		check("Refine(Power((-x)^(1/2), 2), Element(x, Reals))", //
				"-x");

		check("Refine((x^3)^(1/3), x >= 0)", //
				"x");

		check("Refine(Abs(x), x>0)", //
				"x");
		check("Refine(Abs(x), Assumptions -> x>0)", //
				"x");
		check("Refine(Abs(x), x>1)", //
				"x");
		check("Refine(Abs(x)>=0, Element(x, Reals))", //
				"True");

		check("Refine(x>0, x>0)", //
				"True");
		check("Refine(x>=0, x>0)", //
				"True");
		check("Refine(x<0, x>0)", //
				"False");

		check("Refine(x>-1, x>0)", //
				"True");
		check("Refine(x>=-1, x>0)", //
				"True");
		check("Refine(x<-1, x>0)", //
				"False");

		check("Refine(x<0, x<0)", //
				"True");
		check("Refine(x<=0, x<0)", //
				"True");
		check("Refine(x>0, x<0)", //
				"False");

		check("Refine(x<-1, x<0)", //
				"x<-1");
		check("Refine(x<=-1, x<0)", //
				"x<=-1");
		check("Refine(x>-1, x<0)", //
				"x>-1");
		check("Refine(x>-1, x>0)", //
				"True");
		check("Refine(x>-1, x>=0)", //
				"True");

		check("Refine(Log(-4), x<0)", //
				"I*Pi+Log(4)");

		check("Refine(Sin(k*Pi), Element(k, Integers))", //
				"0");
		check("Sin(k*Pi)", //
				"Sin(k*Pi)");
		check("Refine(Cos(x+k*Pi), Element(k, Integers))", //
				"(-1)^k*Cos(x)");

		check("Refine(Floor(2*a + 1), Element(a, Integers))", //
				"1+2*a");
		check("Floor(2*a + 1)", //
				"1+Floor(2*a)");

		check("Refine(Element(x, Integers), Element(x, integers))", //
				"True");
		check("Refine(Floor(x),Element(x,Integers))", //
				"x");

		check("Refine(Arg(x), Assumptions -> x>0)", //
				"0");
		check("Refine(Arg(x), Assumptions -> x<0)", //
				"Pi");

		check("Refine(x==0)", //
				"x==0");

	}

	public void testReplace() {

		check("Replace(x,x -> 1)", //
				"1");
		check("Replace({x,y},x -> 1)", //
				"{x,y}");
		check("Replace({x,y},{_,_} -> 1)", //
				"1");
		check("Replace({x,y,z},x -> 1,1)", //
				"{1,y,z}");
		check("Replace({{x},x,{{x}}},x -> 1,2)", //
				"{{1},1,{{x}}}");
		check("Replace({x,{x}},x -> 1,{2})", //
				"{x,{1}}");
		check("Replace({x,x(x)},x -> 1,2)", //
				"{1,x(1)}");

		// By default, only the top level is searched for matches
		check("Replace(1 + x, {x -> 2})", //
				"1+x");
		// use Replace() as an operator
		check("Replace({x_ -> x + 1})[10]", //
				"11");
		// Replace replaces the deepest levels first
		check("Replace(x(1), {x(1) -> y, 1 -> 2}, All)", //
				"x(2)");
		// Replace stops after the first replacement
		check("Replace(x, {x -> {}, _List -> y})", //
				"{}");
		check("Replace(x^2, x^2 -> a + b)", //
				"a+b");
		check("Replace(1+x^2, x^2 -> a + b)", //
				"1+x^2");

		check("Replace(x, {x -> a, x -> b})", //
				"a");
		check("Replace(x, {y -> a, x -> b, x->c})", //
				"b");

		check("Replace(x, {{x -> a}, {x -> b}})", //
				"{a,b}");
		check("Replace(x, {{e->q, x -> a}, {x -> b}})", //
				"{a,b}");

		// Test with level specification
		check("Replace(f(1, x^2,x^2), x^2 -> a + b, {1})", //
				"f(1,a+b,a+b)");
		check("Replace(f(1, x^2,x^2), z -> a + b, {1})", //
				"f(1,x^2,x^2)");
		check("Replace(f(1, x^2,x^2), {{1 -> a + b},{x^2 -> a + b}}, {1})", //
				"{f(a+b,x^2,x^2),f(1,a+b,a+b)}");
		check("Replace(f(1, x^2,x^2), {{z -> a + b},{w -> a + b}}, {1})", //
				"{f(1,x^2,x^2),f(1,x^2,x^2)}");
		check("Replace(f(1, x, x), {y -> a, x -> b, x->c}, {1})", //
				"f(1,b,b)");
		check("Replace(f(1, x, x), {y -> a, z -> b, w->c}, {1})", //
				"f(1,x,x)");
		// check("Replace({x, x, x}, x :> RandomReal(), {1})",
		// "{0.20251412388709988,0.7585256738344558,0.0882472501351631}");
	}

	public void testReplaceAll() {

		check("{x,y,z}/.x -> 1", //
				"{1,y,z}");
		check("{x(x),y}/.x -> 1", //
				"{1[1],y}");
		check("{{x,y}}/.x:>Sequence[2,3]", //
				"{{2,3,y}}");
		check("{{x,y},y}/.{_,_} -> {1,1}", //
				"{1,1}");

		check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z)} /. f_Power :> (f /. x->10)", //
				"{x,Sin(x),100,x*y,x+y,g(y,x),h(x,y,z)}");
		check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z)} /. Sin(x_) -> Sin(10)", //
				"{x,Sin(10),x^2,x*y,x+y,g(y,x),h(x,y,z)}");
		check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z),Cos(y)} /. HoldPattern(Plus(t__)) :> (Plus(t) /. x->10)", //
				"{x,Sin(x),x^2,x*y,10+y,g(y,x),h(x,y,z),Cos(y)}");
		check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z),Cos(y)} /. Plus(t__) :> (Plus(t) /. x->10)", //
				"{10,Sin(10),100,10*y,10+y,g(y,10),h(10,y,z),Cos(y)}");

		check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z),Cos(y)} /. Power(t__) :> (Power(t) /. x->10)", //
				"{10,Sin(10),100,10*y,10+y,g(y,10),h(10,y,z),Cos(y)}");
		check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z),Cos(y)} /. HoldPattern(Power(t__)) :> (Power(t) /. x->10)", //
				"{x,Sin(x),100,x*y,x+y,g(y,x),h(x,y,z),Cos(y)}");

		check("{f(x),g(x),f(x,y),Sin(x+y),f(),f(x,y,z),Cos(y)} /. f(t__) :> a*f(t)", //
				"{a*f(x),g(x),a*f(x,y),Sin(x+y),f(),a*f(x,y,z),Cos(y)}");
		check("{f(x),g(x),f(x,y),Sin(x+y),f(),f(x,y,z),Cos(y)} /. x_f :> a*x", //
				"{a*f(x),g(x),a*f(x,y),Sin(x+y),a*f(),a*f(x,y,z),Cos(y)}");
		check("{f(x),g(x),f(x,y),Sin(x+y),f(),f(x,y,z),Cos(y)} /. f(t___) :> a*f(t)", //
				"{a*f(x),g(x),a*f(x,y),Sin(x+y),a*f(),a*f(x,y,z),Cos(y)}");

		// prints
		// {{1,{2,3}},{4,5}}
		// {1,{2,3}}
		// {2,3}
		// {4,5}
		check("{{1, {2, 3}}, {4, 5}} /. {_, _} ? Print -> Null;", //
				"");
		// prints:
		// {{1,{2,3}},{4,5}}
		// List
		// {1,{2,3}}
		// List
		// 1
		// {2,3}
		// List
		// 2
		// 3
		// {4,5}
		// List
		// 4
		// 5
		check("{{1, {2, 3}}, {4, 5}} /. _?Print -> Null;", //
				"");
		check("g(a + b + c + d, b + d) /. g(x_ + y_, x_) -> p(x, y)", //
				"p(b+d,a+c)");

		check("2*x*y /. {x -> a, y -> b}", //
				"2*a*b");
		check("2*x*y /. {2*x -> a, 2*x*y -> b}", //
				"a*y");
		check("2*x*y /. {x -> a, 2*x*y -> b}", //
				"b");
		check("ReplaceAll(x^2+3*x+12, x->10)", //
				"142");
		check("x_Integer /. x->xvar", //
				"xvar_Integer");
		check("x__ /. x->xvar", //
				"xvar__");
		check("x___ /. x->xvar", //
				"xvar___");
		check("a + b + c /. a + c -> p", //
				"b+p");
		// check("g(a + b + c + d, b + d) /. g(x_ + y_, x_) -> p(x, y)", "p(b+d,a+c)");

		// TODO
		// check("ReplaceAll({a, b, c}, {___, x__, ___} -> {x})", "{a}");
		check("{g(1), Hold(g(1))} /. g(n_) -> n + 1", //
				"{2,Hold(1+1)}");

		check("x /. { }", //
				"x");
		check("x /. {{x -> 1}, {y -> 2}}", //
				"{1,x}");
		check("{a, b, c} /. {a -> b, b -> d}", //
				"{b,d,c}");
		check("{a, b, c} /. a -> b /. b -> d", //
				"{d,d,c}");
		check("{g(1), Hold(g(1))} /. g(n_) -> n + 1", //
				"{2,Hold(1+1)}");

		check("u(v(w,x,y) /. { {}, {w->y}})", //
				"u({v(w,x,y),v(y,x,y)})");
		check("u(v(w,x,y) /. { {}, w->y})", //
				"u(v(w,x,y)/.{{},w->y})");
		check("ReplaceAll(x -> a)[{x, x^2, y, z}]", //
				"{a,a^2,y,z}");
		check("x /. {y -> 2, z -> 3}", //
				"x");
		check("x /. {x -> 1, x -> 3, x -> 7}", //
				"1");
		check("x /. {{x -> 1}, {x -> 3}, {x -> 7}}", //
				"{1,3,7}");
		check("a == b /. _Equal -> 2", //
				"2");
		check("If(1 == k, itstrue, itsfalse) /. _If -> 99", //
				"99");

		check("ReplaceAll({a -> 1})[{a, b}]", //
				"{1,b}");
		check("{x, x^2, y, z} /. x -> a", //
				"{a,a^2,y,z}");
		check("{x, x^2, y, z} /. x -> {a, b}", //
				"{{a,b},{a^2,b^2},y,z}");
		check("Sin(x) /. Sin -> Cos", //
				"Cos(x)");
		check("1 + x^2 + x^4 /. x^p_ -> f(p)", //
				"1+f(2)+f(4)");
		check("x /. {x -> 1, x -> 3, x -> 7}", //
				"1");
		check("x /. {{x -> 1}, {x -> 3}, {x -> 7}}", //
				"{1,3,7}");
		check("x /. {{a->z, x -> 1}, {x -> 3}, {x -> 7}}", //
				"{1,3,7}");
		check("{a, b, c} /. List -> f", //
				"f(a,b,c)");
		check("SetAttributes($r, {OneIdentity, Flat});$r(a, b, b, c) /. $r(x_, x_) -> rp(x)", //
				"$r(a,rp(b),c)");

		check("f(a) + f(b) /. f(x_) -> x^2", //
				"a^2+b^2");
		check("(x_ /; x < 0)", //
				"x_/;x<0");
		check("{1 + a, 2 + a, -3 + a} /. (x_ /; x < 0) + a -> p(x)", //
				"{1+a,2+a,p(-3)}");
		check("$fac(x_ /; x > 0) := x!;$fac(6) + $fac(-4)", //
				"720+$fac(-4)");

		check("f(a + b) + f(a + c) /. f(a + x_) + f(c + y_) -> p(x, y)", //
				"p(b,a)");
		// wrong result
		check("f(a + b) + f(a + c) + f(b + d) /. f(a + x_) + f(c + y_) -> p(x, y)", //
				"f(b+d)+p(b,a)");

		check("g(a + b, a) /. g(x_ + y_, x_) -> p(x, y)", //
				"p(a,b)");
		check("g(a + b, b) /. g(x_ + y_, x_) -> p(x, y)", //
				"p(b,a)");
		check("h(a + b, a + b) /. h(x_ + y_, x_ + z_) -> p(x, y, z)", //
				"p(a,b,b)");
		check("SetAttributes($q, Orderless);f($q(a, b), $q(b, c)) /. f($q(x_, y_), $q(x_, z_)) -> p(x, y, z)", //
				"p(b,a,c)");
		check("g(a + b + c) /. g(x_ + y_) -> p(x, y)", //
				"p(a,b+c)");
		check("g(a + b + c + d) /. g(x_ + y_) -> p(x, y)", //
				"p(a,b+c+d)");
		check("g(a + b + c + d, b + d) /. g(x_ + y_, x_) -> p(x, y)", //
				"p(b+d,a+c)");
		check("a + b + c /. a + c -> p", //
				"b+p");
		check("u(a) + u(b) + v(c) + v(d) /. u(x_) + u(y_) -> u(x + y)", //
				"u(a+b)+v(c)+v(d)");
		check("SetAttributes($r, Flat);$r(a, b, a, b) /. $r(x_, x_) -> rp(x)", //
				"rp($r(a,b))");

		// correct because OneIdentity is set:
		check("SetAttributes($r, {OneIdentity, Flat});$r(a, b, b, c) /. $r(x_, x_) -> rp(x)", //
				"$r(a,rp(b),c)");
		check("SetAttributes($r, {OneIdentity, Flat});$r(a, b, b, c) /. $r(b, b) -> rp(b)", //
				"$r(a,rp(b),c)");

		// wrong because OneIdentity is not set:
		check("SetAttributes($r, Flat);$r(a, b, b, c) /. $r(x_, x_) -> rp(x)", //
				"$r(a,rp(b),c)");
		// wrong because OneIdentity is not set:
		check("SetAttributes($r, Flat);$r(a, b, b, c) /. $r(b, b) -> rp(b)", //
				"$r(a,rp(b),c)");
		// check("","");

		check("{c+d+e} /. x_+y_->{x,y}", //
				"{{c,d+e}}");
		check("{a+b,x,c+d+e} /. x_+y_->{x,y}", //
				"{{a,b},x,{c,d+e}}");
	}

	public void testReplacePart() {
		// TODO github #135
		// check("ReplacePart({{1, 2}, {3,4}}, {x_, x_} -> -1)", //
		// "");
		check("ReplacePart({a, b, c}, 1 -> t)", //
				"{t,b,c}");
		check("ReplacePart({{a, b}, {c, d}}, {2, 1} -> t)", //
				"{{a,b},{t,d}}");
		check("ReplacePart({{a, b}, {c, d}}, {{2, 1} -> t, {1, 1} -> t})", //
				"{{t,b},{t,d}}");
		check("ReplacePart({a, b, c}, {{1}, {2}} -> t)", //
				"{t,t,c}");

		check("n = 1", "1");
		check("ReplacePart({a, b, c, d}, {{1}, {3}} :> n++)", //
				"{1,b,2,d}");

		check("ReplacePart({a, b, c}, 4 -> t)", //
				"{a,b,c}");
		check("ReplacePart({a, b, c}, 0 -> Times)", //
				"a*b*c");
		check("ReplacePart({a, b, c}, -1 -> t)", //
				"{a,b,t}");

		check("ReplacePart({a, b, c, d, e}, 3 -> xxx)", //
				"{a,b,xxx,d,e}");
		check("ReplacePart({a, b, c, d, e}, {2 -> xx, 5 -> yy})", //
				"{a,xx,c,d,yy}");
		check("ReplacePart({a,b,c^n}, {{3, 2} -> x + y, 2 -> b^100})", //
				"{a,b^100,c^(x+y)}");

		check("ReplacePart({a, b, c, d, e}, xxx, 3)", //
				"{a,b,xxx,d,e}");
		check("ReplacePart({a,b,c^n}, x+y, {{3, 2}, 2})", //
				"{a,x+y,c^(x+y)}");
	}

	public void testReplaceList() {
		check("ReplaceList({b*x, a*b*x*z, x}, {a_*x_,a_.*x_^y_.*z_, x_ } -> {a,x,y,z})", //
				"{{b,x,1,a*z}}");
		check("ReplaceList({a, b, b, b, c, c, a}, {___, x_, x_, ___} -> x)", //
				"{b,b,c}");

		check("ReplaceList({a, b}, {___, x__, ___} -> {x})", //
				"{{a},{a,b},{b}}");
		check("ReplaceList({a, b, c, d}, {___, x__, ___} -> {x})", //
				"{{a},{a,b},{a,b,c},{a,b,c,d},{b},{b,c},{b,c,d},{c},{c,d},{d}}");

		check("ReplaceList(a + b + c, x_ + y_ :> {x, y})", //
				"{{a,b+c},{b,a+c},{c,a+b},{a+b,c},{a+c,b},{b+c,a}}");
		check("Replace(a + b + c, x_ + y_ :> {x, y})", //
				"{a,b+c}");

		check("ReplaceList(a + b + c, x_ + y_ -> g(x, y))", //
				"{g(a,b+c),"//
						+ "g(b,a+c),"//
						+ "g(c,a+b),"//
						+ "g(a+b,c),"//
						+ "g(a+c,b),"//
						+ "g(b+c,a)}");

		check("ReplaceList(x, {x -> a, x -> b, x -> c})", //
				"{a,b,c}");
		check("Replace(x, {x -> a, x -> b, x -> c})", //
				"a");

		// operator form
		check("ReplaceList({x__, y__} -> {{x}, {y}})[{a, b, c, d}]", //
				"{{{a},{b,c,d}}," //
						+ "{{a,b},{c,d}}," //
						+ "{{a,b,c},{d}}}");

		check("ReplaceList({a, b, c, d, e, f}, {x__, y__} -> {{x}, {y}})", //
				"{{{a},{b,c,d,e,f}},"//
						+ "{{a,b},{c,d,e,f}},"//
						+ "{{a,b,c},{d,e,f}},"//
						+ "{{a,b,c,d},{e,f}},"//
						+ "{{a,b,c,d,e},{f}}}");

		check("Replace({a, b, c, d, e, f}, {x__, y__} -> {{x}, {y}})", //
				"{{a},{b,c,d,e,f}}");

		check("ReplaceList(a+b,(x_+y_) :> {{x},{y}})", //
				"{{{a},{b}}," //
						+ "{{b},{a}}}");

		check("ReplaceList(a+b+c,(x_+y_) :> {{x},{y}})", //
				"{{{a},{b+c}},"//
						+ "{{b},{a+c}},"//
						+ "{{c},{a+b}},"//
						+ "{{a+b},{c}},"//
						+ "{{a+c},{b}},"//
						+ "{{b+c},{a}}}");

		check("ReplaceList(a+b+c+d+e+f,(x_+y_+z_) :> {{x},{y},{z}})", //
				"{{{a},{b},{c+d+e+f}},"//
						+ "{{a},{c},{b+d+e+f}},"//
						+ "{{a},{d},{b+c+e+f}},"//
						+ "{{a},{e},{b+c+d+f}},"//
						+ "{{a},{f},{b+c+d+e}},"//
						+ "{{b},{a},{c+d+e+f}},"//
						+ "{{b},{c},{a+d+e+f}},"//
						+ "{{b},{d},{a+c+e+f}},"//
						+ "{{b},{e},{a+c+d+f}},"//
						+ "{{b},{f},{a+c+d+e}},"//
						+ "{{c},{a},{b+d+e+f}},"//
						+ "{{c},{b},{a+d+e+f}},"//
						+ "{{c},{d},{a+b+e+f}},"//
						+ "{{c},{e},{a+b+d+f}},"//
						+ "{{c},{f},{a+b+d+e}},"//
						+ "{{d},{a},{b+c+e+f}},"//
						+ "{{d},{b},{a+c+e+f}},"//
						+ "{{d},{c},{a+b+e+f}},"//
						+ "{{d},{e},{a+b+c+f}},"//
						+ "{{d},{f},{a+b+c+e}},"//
						+ "{{e},{a},{b+c+d+f}},"//
						+ "{{e},{b},{a+c+d+f}},"//
						+ "{{e},{c},{a+b+d+f}},"//
						+ "{{e},{d},{a+b+c+f}},"//
						+ "{{e},{f},{a+b+c+d}},"//
						+ "{{f},{a},{b+c+d+e}},"//
						+ "{{f},{b},{a+c+d+e}},"//
						+ "{{f},{c},{a+b+d+e}},"//
						+ "{{f},{d},{a+b+c+e}},"//
						+ "{{f},{e},{a+b+c+d}},"//
						+ "{{a},{b+c},{d+e+f}},"//
						+ "{{a},{b+d},{c+e+f}},"//
						+ "{{a},{b+e},{c+d+f}},"//
						+ "{{a},{b+f},{c+d+e}},"//
						+ "{{a},{c+d},{b+e+f}},"//
						+ "{{a},{c+e},{b+d+f}},"//
						+ "{{a},{c+f},{b+d+e}},"//
						+ "{{a},{d+e},{b+c+f}},"//
						+ "{{a},{d+f},{b+c+e}},"//
						+ "{{a},{e+f},{b+c+d}},"//
						+ "{{b},{a+c},{d+e+f}},"//
						+ "{{b},{a+d},{c+e+f}},"//
						+ "{{b},{a+e},{c+d+f}},"//
						+ "{{b},{a+f},{c+d+e}},"//
						+ "{{b},{c+d},{a+e+f}},"//
						+ "{{b},{c+e},{a+d+f}},"//
						+ "{{b},{c+f},{a+d+e}},"//
						+ "{{b},{d+e},{a+c+f}},"//
						+ "{{b},{d+f},{a+c+e}},"//
						+ "{{b},{e+f},{a+c+d}},"//
						+ "{{c},{a+b},{d+e+f}},"//
						+ "{{c},{a+d},{b+e+f}},"//
						+ "{{c},{a+e},{b+d+f}},"//
						+ "{{c},{a+f},{b+d+e}},"//
						+ "{{c},{b+d},{a+e+f}},"//
						+ "{{c},{b+e},{a+d+f}},"//
						+ "{{c},{b+f},{a+d+e}},"//
						+ "{{c},{d+e},{a+b+f}},"//
						+ "{{c},{d+f},{a+b+e}},"//
						+ "{{c},{e+f},{a+b+d}},"//
						+ "{{d},{a+b},{c+e+f}},"//
						+ "{{d},{a+c},{b+e+f}},"//
						+ "{{d},{a+e},{b+c+f}},"//
						+ "{{d},{a+f},{b+c+e}},"//
						+ "{{d},{b+c},{a+e+f}},"//
						+ "{{d},{b+e},{a+c+f}},"//
						+ "{{d},{b+f},{a+c+e}},"//
						+ "{{d},{c+e},{a+b+f}},"//
						+ "{{d},{c+f},{a+b+e}},"//
						+ "{{d},{e+f},{a+b+c}},"//
						+ "{{e},{a+b},{c+d+f}},"//
						+ "{{e},{a+c},{b+d+f}},{{e},{a+d},{b+c+f}},{{e},{a+f},{b+c+d}},{{e},{b+c},{a+d+f}},{{e},{b+d},{a+c+f}},{{e},{b+f},{a+c+d}},{{e},{c+d},{a+b+f}},"
						+ "{{e},{c+f},{a+b+d}},{{e},{d+f},{a+b+c}},{{f},{a+b},{c+d+e}},"//
						+ "{{f},{a+c},{b+d+e}},{{f},{a+d},{b+c+e}},{{f},{a+e},{b+c+d}},{{f},{b+c},{a+d+e}},"//
						+ "{{f},{b+d},{a+c+e}},{{f},{b+e},{a+c+d}},{{f},{c+d},{a+b+e}},{{f},{c+e},{a+b+d}},"//
						+ "{{f},{d+e},{a+b+c}},{{a},{b+c+d},{e+f}},{{a},{b+c+e},{d+f}},{{a},{b+c+f},{d+e}},"//
						+ "{{a},{b+d+e},{c+f}},{{a},{b+d+f},{c+e}},{{a},{b+e+f},{c+d}},{{a},{c+d+e},{b+f}},"//
						+ "{{a},{c+d+f},{b+e}},{{a},{c+e+f},{b+d}},{{a},{d+e+f},{b+c}},{{b},{a+c+d},{e+f}},"//
						+ "{{b},{a+c+e},{d+f}},{{b},{a+c+f},{d+e}},{{b},{a+d+e},{c+f}},{{b},{a+d+f},{c+e}},"//
						+ "{{b},{a+e+f},{c+d}},{{b},{c+d+e},{a+f}},{{b},{c+d+f},{a+e}},{{b},{c+e+f},{a+d}},"//
						+ "{{b},{d+e+f},{a+c}},{{c},{a+b+d},{e+f}},{{c},{a+b+e},{d+f}},{{c},{a+b+f},{d+e}},{{c},{a+d+e},{b+f}},"//
						+ "{{c},{a+d+f},{b+e}},{{c},{a+e+f},{b+d}},{{c},{b+d+e},{a+f}},{{c},{b+d+f},{a+e}},{{c},{b+e+f},{a+d}},"//
						+ "{{c},{d+e+f},{a+b}},{{d},{a+b+c},{e+f}},{{d},{a+b+e},{c+f}},{{d},{a+b+f},{c+e}},{{d},{a+c+e},{b+f}},"//
						+ "{{d},{a+c+f},{b+e}},{{d},{a+e+f},{b+c}},{{d},{b+c+e},{a+f}},{{d},{b+c+f},{a+e}},{{d},{b+e+f},{a+c}},"//
						+ "{{d},{c+e+f},{a+b}},{{e},{a+b+c},{d+f}},{{e},{a+b+d},{c+f}},{{e},{a+b+f},{c+d}},{{e},{a+c+d},{b+f}},"//
						+ "{{e},{a+c+f},{b+d}},{{e},{a+d+f},{b+c}},{{e},{b+c+d},{a+f}},{{e},{b+c+f},{a+d}},{{e},{b+d+f},{a+c}},"//
						+ "{{e},{c+d+f},{a+b}},{{f},{a+b+c},{d+e}},{{f},{a+b+d},{c+e}},{{f},{a+b+e},{c+d}},{{f},{a+c+d},{b+e}},"//
						+ "{{f},{a+c+e},{b+d}},{{f},{a+d+e},{b+c}},{{f},{b+c+d},{a+e}},{{f},{b+c+e},{a+d}},{{f},{b+d+e},{a+c}},"//
						+ "{{f},{c+d+e},{a+b}},{{a},{b+c+d+e},{f}},{{a},{b+c+d+f},{e}},{{a},{b+c+e+f},{d}},{{a},{b+d+e+f},{c}},"//
						+ "{{a},{c+d+e+f},{b}},{{b},{a+c+d+e},{f}},{{b},{a+c+d+f},{e}},{{b},{a+c+e+f},{d}},{{b},{a+d+e+f},{c}},"//
						+ "{{b},{c+d+e+f},{a}},{{c},{a+b+d+e},{f}},{{c},{a+b+d+f},{e}},{{c},{a+b+e+f},{d}},{{c},{a+d+e+f},{b}},"//
						+ "{{c},{b+d+e+f},{a}},{{d},{a+b+c+e},{f}},{{d},{a+b+c+f},{e}},{{d},{a+b+e+f},{c}},{{d},{a+c+e+f},{b}},"//
						+ "{{d},{b+c+e+f},{a}},{{e},{a+b+c+d},{f}},{{e},{a+b+c+f},{d}},{{e},{a+b+d+f},{c}},{{e},{a+c+d+f},{b}},"//
						+ "{{e},{b+c+d+f},{a}},{{f},{a+b+c+d},{e}},{{f},{a+b+c+e},{d}},{{f},{a+b+d+e},{c}},{{f},{a+c+d+e},{b}},{{f},{b+c+d+e},{a}},{{a+b},{c},{d+e+f}},{{a+b},{d},{c+e+f}},{{a+b},{e},{c+d+f}},{{a+b},{f},{c+d+e}},{{a+c},{b},{d+e+f}},{{a+c},{d},{b+e+f}},{{a+c},{e},{b+d+f}},{{a+c},{f},{b+d+e}},{{a+d},{b},{c+e+f}},{{a+d},{c},{b+e+f}},{{a+d},{e},{b+c+f}},{{a+d},{f},{b+c+e}},{{a+e},{b},{c+d+f}},{{a+e},{c},{b+d+f}},{{a+e},{d},{b+c+f}},{{a+e},{f},{b+c+d}},{{a+f},{b},{c+d+e}},{{a+f},{c},{b+d+e}},{{a+f},{d},{b+c+e}},{{a+f},{e},{b+c+d}},{{b+c},{a},{d+e+f}},{{b+c},{d},{a+e+f}},{{b+c},{e},{a+d+f}},{{b+c},{f},{a+d+e}},{{b+d},{a},{c+e+f}},{{b+d},{c},{a+e+f}},{{b+d},{e},{a+c+f}},{{b+d},{f},{a+c+e}},{{b+e},{a},{c+d+f}},{{b+e},{c},{a+d+f}},{{b+e},{d},{a+c+f}},{{b+e},{f},{a+c+d}},{{b+f},{a},{c+d+e}},{{b+f},{c},{a+d+e}},{{b+f},{d},{a+c+e}},{{b+f},{e},{a+c+d}},{{c+d},{a},{b+e+f}},{{c+d},{b},{a+e+f}},{{c+d},{e},{a+b+f}},{{c+d},{f},{a+b+e}},{{c+e},{a},{b+d+f}},{{c+e},{b},{a+d+f}},{{c+e},{d},{a+b+f}},{{c+e},{f},{a+b+d}},{{c+f},{a},{b+d+e}},{{c+f},{b},{a+d+e}},{{c+f},{d},{a+b+e}},{{c+f},{e},{a+b+d}},{{d+e},{a},{b+c+f}},{{d+e},{b},{a+c+f}},{{d+e},{c},{a+b+f}},{{d+e},{f},{a+b+c}},{{d+f},{a},{b+c+e}},{{d+f},{b},{a+c+e}},{{d+f},{c},{a+b+e}},{{d+f},{e},{a+b+c}},{{e+f},{a},{b+c+d}},{{e+f},{b},{a+c+d}},{{e+f},{c},{a+b+d}},{{e+f},{d},{a+b+c}},{{a+b},{c+d},{e+f}},{{a+b},{c+e},{d+f}},{{a+b},{c+f},{d+e}},{{a+b},{d+e},{c+f}},{{a+b},{d+f},{c+e}},{{a+b},{e+f},{c+d}},{{a+c},{b+d},{e+f}},{{a+c},{b+e},{d+f}},{{a+c},{b+f},{d+e}},{{a+c},{d+e},{b+f}},{{a+c},{d+f},{b+e}},{{a+c},{e+f},{b+d}},{{a+d},{b+c},{e+f}},{{a+d},{b+e},{c+f}},{{a+d},{b+f},{c+e}},{{a+d},{c+e},{b+f}},{{a+d},{c+f},{b+e}},{{a+d},{e+f},{b+c}},{{a+e},{b+c},{d+f}},{{a+e},{b+d},{c+f}},{{a+e},{b+f},{c+d}},{{a+e},{c+d},{b+f}},{{a+e},{c+f},{b+d}},{{a+e},{d+f},{b+c}},{{a+f},{b+c},{d+e}},{{a+f},{b+d},{c+e}},{{a+f},{b+e},{c+d}},{{a+f},{c+d},{b+e}},{{a+f},{c+e},{b+d}},{{a+f},{d+e},{b+c}},{{b+c},{a+d},{e+f}},{{b+c},{a+e},{d+f}},{{b+c},{a+f},{d+e}},{{b+c},{d+e},{a+f}},{{b+c},{d+f},{a+e}},{{b+c},{e+f},{a+d}},{{b+d},{a+c},{e+f}},{{b+d},{a+e},{c+f}},{{b+d},{a+f},{c+e}},{{b+d},{c+e},{a+f}},{{b+d},{c+f},{a+e}},{{b+d},{e+f},{a+c}},{{b+e},{a+c},{d+f}},{{b+e},{a+d},{c+f}},{{b+e},{a+f},{c+d}},{{b+e},{c+d},{a+f}},{{b+e},{c+f},{a+d}},{{b+e},{d+f},{a+c}},{{b+f},{a+c},{d+e}},{{b+f},{a+d},{c+e}},{{b+f},{a+e},{c+d}},{{b+f},{c+d},{a+e}},{{b+f},{c+e},{a+d}},{{b+f},{d+e},{a+c}},{{c+d},{a+b},{e+f}},{{c+d},{a+e},{b+f}},{{c+d},{a+f},{b+e}},{{c+d},{b+e},{a+f}},{{c+d},{b+f},{a+e}},{{c+d},{e+f},{a+b}},{{c+e},{a+b},{d+f}},{{c+e},{a+d},{b+f}},{{c+e},{a+f},{b+d}},{{c+e},{b+d},{a+f}},{{c+e},{b+f},{a+d}},{{c+e},{d+f},{a+b}},{{c+f},{a+b},{d+e}},{{c+f},{a+d},{b+e}},{{c+f},{a+e},{b+d}},{{c+f},{b+d},{a+e}},{{c+f},{b+e},{a+d}},{{c+f},{d+e},{a+b}},{{d+e},{a+b},{c+f}},{{d+e},{a+c},{b+f}},{{d+e},{a+f},{b+c}},{{d+e},{b+c},{a+f}},{{d+e},{b+f},{a+c}},{{d+e},{c+f},{a+b}},{{d+f},{a+b},{c+e}},{{d+f},{a+c},{b+e}},{{d+f},{a+e},{b+c}},{{d+f},{b+c},{a+e}},{{d+f},{b+e},{a+c}},{{d+f},{c+e},{a+b}},{{e+f},{a+b},{c+d}},{{e+f},{a+c},{b+d}},"
						+ "{{e+f},{a+d},{b+c}},{{e+f},{b+c},{a+d}},{{e+f},{b+d},{a+c}},{{e+f},{c+d},{a+b}},{{a+b},{c+d+e},{f}},"//
						+ "{{a+b},{c+d+f},{e}},{{a+b},{c+e+f},{d}},{{a+b},{d+e+f},{c}},{{a+c},{b+d+e},{f}},{{a+c},{b+d+f},{e}},{{a+c},{b+e+f},{d}},{{a+c},{d+e+f},{b}},{{a+d},{b+c+e},{f}},{{a+d},{b+c+f},{e}},{{a+d},{b+e+f},{c}},{{a+d},{c+e+f},{b}},{{a+e},{b+c+d},{f}},{{a+e},{b+c+f},{d}},{{a+e},{b+d+f},{c}},{{a+e},{c+d+f},{b}},{{a+f},{b+c+d},{e}},{{a+f},{b+c+e},{d}},{{a+f},{b+d+e},{c}},{{a+f},{c+d+e},{b}},{{b+c},{a+d+e},{f}},{{b+c},{a+d+f},{e}},{{b+c},{a+e+f},{d}},{{b+c},{d+e+f},{a}},{{b+d},{a+c+e},{f}},{{b+d},{a+c+f},{e}},{{b+d},{a+e+f},{c}},{{b+d},{c+e+f},{a}},{{b+e},{a+c+d},{f}},{{b+e},{a+c+f},{d}},{{b+e},{a+d+f},{c}},{{b+e},{c+d+f},{a}},{{b+f},{a+c+d},{e}},{{b+f},{a+c+e},{d}},{{b+f},{a+d+e},{c}},{{b+f},{c+d+e},{a}},{{c+d},{a+b+e},{f}},"
						+ "{{c+d},{a+b+f},{e}},{{c+d},{a+e+f},{b}},{{c+d},{b+e+f},{a}},{{c+e},{a+b+d},{f}},{{c+e},{a+b+f},{d}},{{c+e},{a+d+f},{b}},{{c+e},{b+d+f},{a}},{{c+f},{a+b+d},{e}},{{c+f},{a+b+e},{d}},{{c+f},{a+d+e},{b}},{{c+f},{b+d+e},{a}},{{d+e},{a+b+c},{f}},{{d+e},{a+b+f},{c}},{{d+e},{a+c+f},{b}},{{d+e},{b+c+f},{a}},{{d+f},{a+b+c},{e}},{{d+f},{a+b+e},{c}},{{d+f},{a+c+e},{b}},{{d+f},{b+c+e},{a}},{{e+f},{a+b+c},{d}},{{e+f},{a+b+d},{c}},{{e+f},{a+c+d},{b}},{{e+f},{b+c+d},{a}},{{a+b+c},{d},{e+f}},{{a+b+c},{e},{d+f}},{{a+b+c},{f},{d+e}},{{a+b+d},{c},{e+f}},{{a+b+d},{e},{c+f}},{{a+b+d},{f},{c+e}},{{a+b+e},{c},{d+f}},{{a+b+e},{d},{c+f}},{{a+b+e},{f},{c+d}},{{a+b+f},{c},{d+e}},{{a+b+f},{d},{c+e}},{{a+b+f},{e},{c+d}},{{a+c+d},{b},{e+f}},{{a+c+d},{e},{b+f}},{{a+c+d},{f},{b+e}},{{a+c+e},{b},{d+f}},{{a+c+e},{d},{b+f}},{{a+c+e},{f},{b+d}},{{a+c+f},{b},{d+e}},{{a+c+f},{d},{b+e}},{{a+c+f},{e},{b+d}},{{a+d+e},{b},{c+f}},{{a+d+e},{c},{b+f}},{{a+d+e},{f},{b+c}},{{a+d+f},{b},{c+e}},{{a+d+f},{c},{b+e}},{{a+d+f},{e},{b+c}},{{a+e+f},{b},{c+d}},{{a+e+f},{c},{b+d}},{{a+e+f},{d},{b+c}},{{b+c+d},{a},{e+f}},{{b+c+d},{e},{a+f}},{{b+c+d},{f},{a+e}},{{b+c+e},{a},{d+f}},{{b+c+e},{d},{a+f}},{{b+c+e},{f},{a+d}},{{b+c+f},{a},{d+e}},{{b+c+f},{d},{a+e}},{{b+c+f},{e},{a+d}},"
						+ "{{b+d+e},{a},{c+f}},{{b+d+e},{c},{a+f}},{{b+d+e},{f},{a+c}},{{b+d+f},{a},{c+e}},{{b+d+f},{c},{a+e}},{{b+d+f},{e},{a+c}},{{b+e+f},{a},{c+d}},{{b+e+f},{c},{a+d}},{{b+e+f},{d},{a+c}},{{c+d+e},{a},{b+f}},{{c+d+e},{b},{a+f}},{{c+d+e},{f},{a+b}},{{c+d+f},{a},{b+e}},{{c+d+f},{b},{a+e}},{{c+d+f},{e},{a+b}},{{c+e+f},{a},{b+d}},{{c+e+f},{b},{a+d}},{{c+e+f},{d},{a+b}},{{d+e+f},{a},{b+c}},{{d+e+f},{b},{a+c}},{{d+e+f},{c},{a+b}},{{a+b+c},{d+e},{f}},{{a+b+c},{d+f},{e}},{{a+b+c},{e+f},{d}},{{a+b+d},{c+e},{f}},{{a+b+d},{c+f},{e}},{{a+b+d},{e+f},{c}},{{a+b+e},{c+d},{f}},{{a+b+e},{c+f},{d}},{{a+b+e},{d+f},{c}},{{a+b+f},{c+d},{e}},{{a+b+f},{c+e},{d}},{{a+b+f},{d+e},{c}},{{a+c+d},{b+e},{f}},{{a+c+d},{b+f},{e}},{{a+c+d},{e+f},{b}},{{a+c+e},{b+d},{f}},{{a+c+e},{b+f},{d}},{{a+c+e},{d+f},{b}},{{a+c+f},{b+d},{e}},{{a+c+f},{b+e},{d}},{{a+c+f},{d+e},{b}},{{a+d+e},{b+c},{f}},{{a+d+e},{b+f},{c}},{{a+d+e},{c+f},{b}},{{a+d+f},{b+c},{e}},{{a+d+f},{b+e},{c}},{{a+d+f},{c+e},{b}},{{a+e+f},{b+c},{d}},{{a+e+f},{b+d},{c}},{{a+e+f},{c+d},{b}},{{b+c+d},{a+e},{f}},{{b+c+d},{a+f},{e}},{{b+c+d},{e+f},{a}},{{b+c+e},{a+d},{f}},{{b+c+e},{a+f},{d}},{{b+c+e},{d+f},{a}},{{b+c+f},{a+d},{e}},{{b+c+f},{a+e},{d}},{{b+c+f},{d+e},{a}},{{b+d+e},{a+c},{f}},{{b+d+e},{a+f},{c}},{{b+d+e},{c+f},{a}},{{b+d+f},{a+c},{e}},{{b+d+f},{a+e},{c}},{{b+d+f},{c+e},{a}},{{b+e+f},{a+c},{d}},{{b+e+f},{a+d},{c}},{{b+e+f},{c+d},{a}},{{c+d+e},{a+b},{f}},{{c+d+e},{a+f},{b}},{{c+d+e},{b+f},{a}},{{c+d+f},{a+b},{e}},{{c+d+f},{a+e},{b}},{{c+d+f},{b+e},{a}},{{c+e+f},{a+b},{d}},{{c+e+f},{a+d},{b}},{{c+e+f},{b+d},{a}},{{d+e+f},{a+b},{c}},"
						+ "{{d+e+f},{a+c},{b}},{{d+e+f},{b+c},{a}},{{a+b+c+d},{e},{f}},{{a+b+c+d},{f},{e}},{{a+b+c+e},{d},{f}},{{a+b+c+e},{f},{d}},{{a+b+c+f},{d},{e}},{{a+b+c+f},{e},{d}},{{a+b+d+e},{c},{f}},{{a+b+d+e},{f},{c}},"
						+ "{{a+b+d+f},{c},{e}},{{a+b+d+f},{e},{c}},{{a+b+e+f},{c},{d}},{{a+b+e+f},{d},{c}},{{a+c+d+e},{b},{f}},"//
						+ "{{a+c+d+e},{f},{b}},{{a+c+d+f},{b},{e}},{{a+c+d+f},{e},{b}},{{a+c+e+f},{b},{d}},"//
						+ "{{a+c+e+f},{d},{b}},{{a+d+e+f},{b},{c}},{{a+d+e+f},{c},{b}},{{b+c+d+e},{a},{f}},"//
						+ "{{b+c+d+e},{f},{a}},{{b+c+d+f},{a},{e}},{{b+c+d+f},{e},{a}},{{b+c+e+f},{a},{d}},"//
						+ "{{b+c+e+f},{d},{a}},{{b+d+e+f},{a},{c}},{{b+d+e+f},{c},{a}},{{c+d+e+f},{a},{b}},"//
						+ "{{c+d+e+f},{b},{a}}}");

		check("ReplaceList({a, b, c, d, e, f}, {x__, y__, z__} :> {{x},{y},{z}})", //
				"{{{a},{b},{c,d,e,f}},"//
						+ "{{a},{b,c},{d,e,f}},"//
						+ "{{a},{b,c,d},{e,f}},"//
						+ "{{a},{b,c,d,e},{f}},"//
						+ "{{a,b},{c},{d,e,f}},"//
						+ "{{a,b},{c,d},{e,f}},"//
						+ "{{a,b},{c,d,e},{f}},"//
						+ "{{a,b,c},{d},{e,f}},"//
						+ "{{a,b,c},{d,e},{f}},"//
						+ "{{a,b,c,d},{e},{f}}}"); //

		// "{{{a},{b},{c,d,e,f}}, {{a}, {b, c}, {d, e, f}}," +
		// "{{a}, {b, c, d}, {e, f}}, {{a}, {b, c, d, e}, {f}}," +
		// "{{a, b}, {c}, {d, e, f}}, {{a, b}, {c, d}, {e, f}}," +
		// "{{a, b}, {c, d, e}, {f}}, {{a, b, c}, {d}, {e, f}}," +
		// "{{a, b, c}, {d, e}, {f}}, {{a, b, c, d}, {e}, {f}}}");
	}

	public void testReplaceTransformations() {
		check("f(f(f(1))) //. f(f(x_)) :> g(g(x))", //
				"g(g(f(1)))");

		check("x + y /. x -> 3", //
				"3+y");
		check("x + y /. {x -> a, y -> b}", //
				"a+b");
		check("x + y /. {{x -> 1, y -> 2}, {x -> 4, y -> 2}}", //
				"{3,6}");
		check("Solve(x^3 - 5*x^2 + 2*x + 8 == 0, x)", //
				"{{x->-1},{x->2},{x->4}}");
		check("x^2 + 6 /. {{x->-1},{x->2},{x->4}}", //
				"{7,10,22}");
		check("{x^2, x^3, x^4} /. {x^3 -> u, x^n_ -> p(n)}", //
				"{p(2),u,p(4)}");
		check("h(x + h(y)) /. h(u_) -> u^2", //
				"(x+h(y))^2");
		check("{x^2, y^3} /. {x -> y, y -> x}", //
				"{y^2,x^3}");
		check("x^2 /. x -> (1 + y) /. y -> b", //
				"(1+b)^2");

		check("x^2 + y^6 /. {x -> 2 + a, a -> 3}", //
				"(2+a)^2+y^6");
		check("x^2 + y^6 //. {x -> 2 + a, a -> 3}", //
				"25+y^6");
		check("mylog(a*b*c*d) /. mylog(x_*y_) -> mylog(x) + mylog(y)", //
				"mylog(a)+mylog(b*c*d)");
		check("mylog(a*b*c*d) //. mylog(x_*y_) -> mylog(x) + mylog(y)", //
				"mylog(a)+mylog(b)+mylog(c)+mylog(d)");

		// check("ReplaceList({a, b, c, d}, {x__, y__} -> g({x}, {y}))", "");
		// check("", "");
		// check("", "");
		// check("", "");
	}

	public void testRest() {
		check("Rest(f(x))", //
				"f()");
		check("Rest(E^(b*x))", //
				"b*x");
		check("Rest(a + b + c + d)", //
				"b+c+d");
		check("Rest(f(a, b, c, d))", //
				"f(b,c,d)");
		check("NestList(Rest, {a, b, c, d, e}, 3)", //
				"{{a,b,c,d,e},{b,c,d,e},{c,d,e},{d,e}}");
		check("Rest(1/b)", //
				"-1");

		check("Rest({a, b, c})", //
				"{b,c}");
		check("Rest(a + b + c)", //
				"b+c");
		check("Rest(a)", //
				"Rest(a)");
	}

	public void testRescale() {
		check("Rescale({-.7, .5, 1.2, 5.6, 1.8})", //
				"{0.0,0.190476,0.301587,1.0,0.396825}");
		check("Rescale({2.5, 3.5, 4.5, 6.5}, {0, 10})", //
				"{0.25,0.35,0.45,0.65}");
		check("Rescale({1, 2, 3, 4, 5, 6}, {0, a})", //
				"{1/a,2/a,3/a,4/a,5/a,6/a}");
		check("Rescale(1 + 0.5 I, {0, 1 + I})", //
				"0.75+I*(-0.25)");
		check("Rescale({a,b})", //
				"{a/(Max(a,b)-Min(a,b))-Min(a,b)/(Max(a,b)-Min(a,b)),b/(Max(a,b)-Min(a,b))-Min(a,b)/(Max(a,b)-Min(a,b))}");
		check("Rescale(x,{xmin, xmax})", //
				"x/(xmax-xmin)-xmin/(xmax-xmin)");
		check("Rescale(x,{xmin, xmax},{ymin, ymax})", //
				"(x*(ymax-ymin))/(xmax-xmin)-(xmin*ymax-xmax*ymin)/(xmax-xmin)");
		check("Rescale(2.5,{-10,10})", //
				"0.625");
		check("Rescale(2.5,{10,10})", //
				"Indeterminate");
		// celsius to fahrenheit in steps of 10 degrees from -40 to 100 degree
		check("Table({x, Rescale(x, {-40, 100}, {-40, 212})}, " //
				+ "{x, -40, 100, 10})", //
				"{{-40,-40},{-30,-22},{-20,-4},{-10,14},{0,32},{10,50},{20,68},{30,86},{40,104},{\n"
						+ "50,122},{60,140},{70,158},{80,176},{90,194},{100,212}}");
		check("Rescale({1, 2, 3, 4, 5}, {-100, 100})", //
				"{101/200,51/100,103/200,13/25,21/40}");
	}

	/**
	 * If this test fails try a change in <code>AbstractAST#isZERO()</code>.
	 *
	 * <pre>
	 * public boolean AbstractAST#isZERO() {
	 *     return PredicateQ.possibleZeroQ(this, EvalEngine.get());
	 * }
	 * </pre>
	 */
	public void testResultant() {
		check("Resultant(0, x^3+2*x, x)", //
				"0");
		check("Resultant(f(x), 0, x)", //
				"0");
		check("Resultant(1,a+x^2+c,x)", //
				"1");
		check("Resultant(a+x^2+c, 1, x)", //
				"1");
		// check("Resultant((x - a) (x - b), (x - c) (x - d) (x - e), x)",//
		// "(a*b+(-(-a^2*b-a*b^2+a*b*c+a*b*d+a*b*e-c*d*e)*(-a-b-(-a^2*b-a*b^2+a*b*c+a*b*d+a*b*e-c*d*e)/(a^\n" +
		// "2+a*b+b^2-a*c-b*c-a*d-b*d+c*d-a*e-b*e+c*e+d*e)))/(a^2+a*b+b^2-a*c-b*c-a*d-b*d+c*d-a*e-b*e+c*e+d*e))*(a^\n" +
		// "2+a*b+b^2-a*c-b*c-a*d-b*d+c*d-a*e-b*e+c*e+d*e)^2");
		check("PolynomialRemainder(-2+x^2-2*x*y+y^2,-5+4*x-2*x^3+2*y+3*x^2*y,y)",
				"-2+x^2-(-5+4*x-2*x^3)*(-(-5+4*x-2*x^3)/(2+3*x^2)^2+(-2*x)/(2+3*x^2))");
		check("Resultant((x-y)^2-2 , y^3-5, y)", //
				"17-60*x+12*x^2-10*x^3-6*x^4+x^6");
		check("Resultant(x^2 - 2*x + 7, x^3 - x + 5, x)", //
				"265");
		check("Resultant(x^2 + 2*x , x-c, x)", //
				"2*c+c^2");

		check("Resultant(x^2 - 4, x^2 + 4*x + 4, x)", //
				"0");
		check("Resultant(3*x + 9, 6*x^3 - 3*x + 12, x)", //
				"-3807");

		// check("Resultant[a x^3 + b x^2 + c x + f, f x^3 + c x^2 + b x + a,
		// x]", "");
	}

	public void testReturn() {
		check("f(x_) := (If(x < 0, Return(0)); x)", //
				"");
		check("f(-1)", //
				"0");

		check("Do(If(i > 3, Return()); Print(i), {i, 10})", //
				"");

		check("g(x_) := (Do(If(x < 0, Return(0)), {i, {2, 1, 0, -1}}); x)", //
				"");
		check("g(-1)", //
				"-1");

		check("h(x_) := (If(x < 0, Return()); x)", //
				"");
		check("h(1)", //
				"1");
		check("h(-1) // FullForm", "Null");

		check("f(x_) := Return(x)", //
				"");
		check("g(y_) := Module({}, z = f(y); 2)", //
				"");
		check("g(1)", //
				"2");

		check("$a(x_):=Return(1); $b(x_):=Module({},$c=$a(y);2); $b(1)", //
				"2");
		check("$f(x_) := (If(x > 5, Return(a)); x + 3); $f(6)", //
				"a");
		check("$g(x_) := (Do( If(x > 5, Return(a)), {3}); x); $g(6)", //
				"6");
		check("$h(x_) := Catch(Do(If(x > 5, Throw(a)), {3}); x); $h(6)", //
				"a");
	}

	public void testReverse() {
		check("Reverse({1, 2, 3})", //
				"{3,2,1}");
		check("Reverse(x(a,b,c))", //
				"x(c,b,a)");
		// check("Reverse({{1, 2}, {3, 4}}, 1)", "");
	}

	public void testRiffle() {
		check("Riffle({1, 2, 3, 4, 5, 6, 7, 8, 9}, x)", //
				"{1,x,2,x,3,x,4,x,5,x,6,x,7,x,8,x,9}");
		check("Riffle({1, 2, 3, 4, 5, 6, 7, 8, 9}, {x, y})", //
				"{1,x,2,y,3,x,4,y,5,x,6,y,7,x,8,y,9}");
		check("Riffle({1}, x)", //
				"{1}");
		check("Riffle({a, b, c, d}, {x, y, z, w})", //
				"{a,x,b,y,c,z,d,w}");
	}

	public void testRogersTanimotoDissimilarity() {
		check("RogersTanimotoDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
				"3/4");
		check("RogersTanimotoDissimilarity({True, False, True}, {True, True, False})", //
				"4/5");
		check("RogersTanimotoDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
				"0");
		check("RogersTanimotoDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
				"1");
	}

	public void testRoot() {
		check("Root((#^2 - 3*# - 1)&, 2)", //
				"3/2+Sqrt(13)/2");
		check("Root((-3*#-1)&, 1)", //
				"-1/3");
	}

	public void testRoots() {
		check("Roots(x^16-1==0,x)", //
				"x==(-1)^(1/8)||x==-(-1)^(1/8)||x==(-1)^(1/4)||x==-(-1)^(1/4)||x==(-1)^(3/8)||x==-(\n"
						+ "-1)^(3/8)||x==I||x==-I||x==(-1)^(5/8)||x==-(-1)^(5/8)||x==(-1)^(3/4)||x==-(-1)^(\n"
						+ "3/4)||x==(-1)^(7/8)||x==-(-1)^(7/8)||x==-1||x==1");
		// check("Roots(a*x^3+b*x^2+c^2+d, x)",
		// "{(-b/2-Sqrt(b^2-4*a*c)/2)/a,(-b/2+Sqrt(b^2-4*a*c)/2)/a}");
		check("Roots(x^2-2*x-3==0,x)", //
				"x==-1||x==3");
		check("Roots(a*x^2+b*x+c==0, x)", //
				"x==(-b-Sqrt(b^2-4*a*c))/(2*a)||x==(-b+Sqrt(b^2-4*a*c))/(2*a)");
		check("Roots(3*x^3-8*x^2+-11*x+10==0,x)", //
				"x==2/3||x==1-Sqrt(6)||x==1+Sqrt(6)");
		check("Roots(3*x^3-5*x^2+5*x-2==0,x)", //
				"x==2/3||x==1/2-I*1/2*Sqrt(3)||x==1/2+I*1/2*Sqrt(3)");
		check("Roots(x^3 - 5*x + 4==0,x)", //
				"x==1||x==-1/2-Sqrt(17)/2||x==-1/2+Sqrt(17)/2");

	}

	public void testRotateLeft() {
		check("RotateLeft({1, 2, 3})", //
				"{2,3,1}");
		check("RotateLeft(Range(10),3)", //
				"{4,5,6,7,8,9,10,1,2,3}");
		check("RotateLeft(x(a,b,c),2)", //
				"x(c,a,b)");

		check("RotateLeft({1,2,3,4,5},2)", //
				"{3,4,5,1,2}");

	}

	public void testRotateRight() {
		check("RotateRight({1, 2, 3})", //
				"{3,1,2}");
		check("RotateRight(Range(10),3)", //
				"{8,9,10,1,2,3,4,5,6,7}");
		check("RotateRight(x(a,b,c),2)", //
				"x(b,c,a)");

		check("RotateRight({1,2,3,4,5},2)", //
				"{4,5,1,2,3}");

	}

	public void testRotationMatrix() {
		check("RotationMatrix(90*Degree)", //
				"{{0,-1},{1,0}}");
		check("RotationMatrix(t,{0,0,1})", //
				"{{Cos(t),-Sin(t),0},{Sin(t),Cos(t),0},{0,0,1}}");
		check("RotationMatrix(t,{0,1,0})", //
				"{{Cos(t),0,Sin(t)},{0,1,0},{-Sin(t),0,Cos(t)}}");
		check("RotationMatrix(t,{1,0,0})", //
				"{{1,0,0},{0,Cos(t),-Sin(t)},{0,Sin(t),Cos(t)}}");
	}

	public void testRound() {
		// github #145
		// TODO add tests for big (Apfloat) numbers
		// Rationalize(2.1675 => 867/400
		check("Round(Rationalize(867/400),10^(-3))", //
				"271/125");
		check("Round(Rationalize(2.1675),10^(-3))", //
				"271/125");
		check("Round(2.1675, 0.001)", //
				"2.168");
		check("Round(2.1675, 1/1000)", //
				"271/125");
		check("Round(500,10^(-3))", //
				"500");
		check("Round(500, 10)", //
				"500");

		check("Round(75.345677/7.56)", //
				"10");
		check("Round(1.234512, 0.01)", //
				"1.23");
		check("Round(-1.234512, 0.01)", //
				"-1.23");
		check("Round(1.235512, 0.01)", //
				"1.24");
		check("Round(-1.235512, 0.01)", //
				"-1.24");
		check("Round(1.234512, -0.01)", //
				"1.23");
		check("Round(-1.234512, -0.01)", //
				"-1.23");
		check("Round(1.235512, -0.01)", //
				"1.24");
		check("Round(-1.235512, -0.01)", //
				"-1.24");
		check("Round(1.234512, 1/100)", //
				"123/100");
		check("Round(-1.235512, 1/100)", //
				"-31/25");

		check("Round(-1.235512, -100)", //
				"0");
		check("Round(-1.235512, 0)", //
				"Indeterminate");
		check("Refine(2/3*Round(x), Element(x,Integers))", //
				"2/3*x");

		check("Round(226, 10)", //
				"230");
		check("Round(226, -10)", //
				"230");
		check("Round({12.5, 62.1, 68.3, 74.5, 80.7}, 5)", //
				"{10,60,70,75,80}");
		check("Round({5, 15, 25, 35, 45}, 10)", //
				"{0,20,20,40,40}");
		check("Round(75.345677/7.56)", //
				"10");
		check("Table(Round(n), {n, {-5/3, 10/3, 13/2}})", //
				"{-2,3,6}");
		check("Table(Round((GoldenRatio^k)/Sqrt(5)), {k, 15}) ==" + //
				"Table(Fibonacci(k), {k, 15})", //
				"True");
		check("Round(Infinity)", //
				"Infinity");
		check("Round(-Infinity)", //
				"-Infinity");
		check("Round(Pi-E)", //
				"0");
		check("Round(5.37-I)", //
				"5-I");
		check("Round(4/(1+I))", //
				"2-I*2");
		check("Round(3.4)", //
				"3");
		check("Round(3.5)", //
				"4");
		check("Round(3.6)", //
				"4");
		check("Round(-3.4)", //
				"-3");
		check("Round(-3.5)", //
				"-4");
		check("Round(-3.6)", //
				"-4");
	}

	public void testRowReduce() {
		check("RowReduce({{1,5,7},{-2,-7,-5}})", //
				"{{1,0,-8},\n" + //
						" {0,1,3}}");
		check("RowReduce({{1,2,2,8},{-1,1,4,4},{-1,-1,1,9}})", //
				"{{1,0,0,26},\n" + //
						" {0,1,0,-22},\n" + //
						" {0,0,1,13}}");
		check("RowReduce({{1,2,3,4},{4,3,2,1}})", //
				"{{1,0,-1,-2},\n" + //
						" {0,1,2,3}}");
		check("RowReduce({{1,2,2,4,1},{0,1,1,2,1},{0,0,0,2,1},{0,0,0,1,1}})", //
				"{{1,0,0,0,0},\n" + //
						" {0,1,1,0,0},\n" + //
						" {0,0,0,1,0},\n" + //
						" {0,0,0,0,1}}");

		check("RowReduce({{2, 4, 8}, {3, 6, 9}})", //
				"{{1,2,0},\n" + //
						" {0,0,1}}");
		check("RowReduce({{1, -2, 1, 0}, {0, 2, -8, 8}, {5, 0, -5, 10}})", //
				"{{1,0,0,1},\n" + //
						" {0,1,0,0},\n" + //
						" {0,0,1,-1}}");
		check("RowReduce({{1, 2, 7}, {-2, 5, 4}, {-5, 6, 3}})", //
				"{{1,0,0},\n" + //
						" {0,1,0},\n" + //
						" {0,0,1}}");

		check("RowReduce({{1, 0, a}, {1, 1, b}})", //
				"{{1,0,a},\n" + //
						" {0,1,-a+b}}");

		check("RowReduce({{1, 2, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 2, 1}})", //
				"{{1,2,0},\n" + //
						" {0,0,1},\n" + //
						" {0,0,0},\n" + //
						" {0,0,0},\n" + //
						" {0,0,0},\n" + //
						" {0,0,0},\n" + //
						" {0,0,0}}");

		check("RowReduce({{1, 2, 3, 1}, {4, 5, 6, -1}, {7, 8, 9, 2}})", //
				"{{1,0,-1,0},\n" + //
						" {0,1,2,0},\n" + //
						" {0,0,0,1}}");

		check("RowReduce({{1,0,-1,0},{0,1,0,-1},{1,-2,-1,0},{-1,0,3,1}})", //
				"{{1,0,0,0},\n" + //
						" {0,1,0,0},\n" + //
						" {0,0,1,0},\n" + //
						" {0,0,0,1}}");

		check("RowReduce({{1, 0, a}, {1, 1, b}})", //
				"{{1,0,a},\n" + //
						" {0,1,-a+b}}");

		check("RowReduce({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
				"{{1,0,-1},\n" + //
						" {0,1,2},\n" + //
						" {0,0,0}}");

		check("RowReduce({{1, 0}, {0}})", //
				"RowReduce({{1,0},{0}})");

		check("RowReduce({{1, 2, 3, 1}, {4, 5, 6, 1}, {7, 8, 9, 1}})", //
				"{{1,0,-1,-1},\n" + //
						" {0,1,2,1},\n" + //
						" {0,0,0,0}}");

		check("RowReduce({{1, 2, 3, 1}, {4, 5, 6, -1}, {7, 8, 9, 2}})", //
				"{{1,0,-1,0},\n" + //
						" {0,1,2,0},\n" + //
						" {0,0,0,1}}");

		check("RowReduce({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
				"{{1,0,-1},\n" + //
						" {0,1,2},\n" + //
						" {0,0,0}}");

		check("RowReduce({{3, 1, a}, {2, 1, b}})", //
				"{{1,0,a-b},\n" + //
						" {0,1,-2*a+3*b}}");

		check("RowReduce({{1., 2., 3.}, {4., 5., 6.}, {7., 8., 9.}})", //
				"{{1.0,0.0,-1.0},\n" + //
						" {0.0,1.0,2.0},\n" + //
						" {0.0,0.0,0.0}}");

		check("RowReduce({{1, I}, {I, -1}})", //
				"{{1,I},\n" + //
						" {0,0}}");

		check("RowReduce({{1,2,3,1,0,0}, {4,5,6,0,1,0}, {7,8,9,0,0,1}})", //
				"{{1,0,-1,0,-8/3,5/3},\n" + //
						" {0,1,2,0,7/3,-4/3},\n" + //
						" {0,0,0,1,-2,1}}");
	}

	public void testRussellRaoDissimilarity() {
		check("RussellRaoDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
				"3/5");
		check("RussellRaoDissimilarity({True, False, True}, {True, True, False})", //
				"2/3");
		check("RussellRaoDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
				"0");
		check("RussellRaoDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
				"1");
	}

	public void testRule() {
		check("a+b+c /. c->d", //
				"a+b+d");
		check("{x,x^2,y} /. x->3", //
				"{3,9,y}");
		// Rule called with 3 arguments; 2 arguments are expected.
		check("a /. Rule(1, 2, 3) -> t ", //
				"a");
	}

	public void testSameQUnsameQ() {
		check("a===a", //
				"True");
		check("SameQ(0.0, 0)", //
				"False");
		check("UnsameQ(0.0, 0)", //
				"True");
		check("$g(f(x))===v", //
				"False");
		check("$g(f(x))===$g(f(x))", //
				"True");
		check("$g(f(x))=!=v", //
				"True");
		check("$g(f(x))=!=$g(f(x))", //
				"False");
		check("Boole(Array(UnsameQ, {3, 3, 3}))", //
				"{{{0,0,0},{0,0,1},{0,1,0}},{{0,0,1},{0,0,0},{1,0,0}},{{0,1,0},{1,0,0},{0,0,0}}}");
	}

	public void testSatisfiabilityCount() {
		check("SatisfiabilityCount(Equivalent(a, b), {a, b})", //
				"2");
		check("SatisfiabilityCount(a || b, {a, b})", //
				"3");
		check("SatisfiabilityCount(a || b)", //
				"3");
		check("SatisfiabilityCount(Xor(a, b, c), {a, b, c} )", //
				"4");

		check("SatisfiabilityCount(a&&!(b||!c) )", //
				"1");
		check("SatisfiabilityCount((a || b) && (! a || ! b) )", //
				"2");
		check("SatisfiabilityCount((a || b) && (! a || ! b), {a, b})", //
				"2");

		check("SatisfiabilityCount(!Implies(Implies(a, b) && ! b, ! a))", //
				"0");
		check("SatisfiabilityCount((a && b) && (! a || ! b) )", //
				"0");
		check("SatisfiabilityCount((a && b) && (! a || ! b), {a, b})", //
				"0");
	}

	public void testSatisfiabilityInstances() {
		check("SatisfiabilityInstances((a || b || c) && (! a || ! b || ! c), {a, b, c},All)", //
				"{{True,True,False},{True,False,True},{True,False,False},{False,True,True},{False,True,False},{False,False,True}}");
		check("SatisfiabilityInstances(a&&!(b||!c), {b,a,c}, All )", //
				"{{False,True,True}}");
		check("SatisfiabilityInstances(a&&!(b||!c), {a,b,c}, All )", //
				"{{True,False,True}}");

		check("SatisfiabilityInstances(a || b, {a, b}, All)", //
				"{{True,True},{True,False},{False,True}}");
		check("SatisfiabilityInstances(Equivalent(a, b), {a, b}, 4)", //
				"{{True,True},{False,False}}");
		check("SatisfiabilityInstances(Equivalent(a, b), {a, b})", //
				"{{False,False}}");
		check("SatisfiabilityInstances(Xor(a, b, c), {a, b, c}, 2^3)", //
				"{{True,True,True},{True,False,False},{False,True,False},{False,False,True}}");

		check("SatisfiabilityInstances(a&&!(b||!c) )", //
				"{{True,False,True}}");

		check("SatisfiabilityInstances((a || b) && (! a || ! b) )", //
				"{{False,True}}");
		check("SatisfiabilityInstances((a || b) && (! a || ! b), {a, b}, All)", //
				"{{True,False},{False,True}}");

		check("SatisfiabilityInstances(!Implies(Implies(a, b) && ! b, ! a))", //
				"{}");
		check("SatisfiabilityInstances((a && b) && (! a || ! b) )", //
				"{}");
		check("SatisfiabilityInstances((a && b) && (! a || ! b), {a, b})", //
				"{}");
	}

	public void testSatisfiableQ() {
		check("SatisfiableQ(a&&!(b||!c) )", //
				"True");
		check("SatisfiableQ((a || b) && (! a || ! b) )", //
				"True");
		check("SatisfiableQ((a || b) && (! a || ! b), {a, b})", //
				"True");

		check("SatisfiableQ(!Implies(Implies(a, b) && ! b, ! a))", //
				"False");
		check("SatisfiableQ((a && b) && (! a || ! b) )", //
				"False");
		check("SatisfiableQ((a && b) && (! a || ! b), {a, b})", //
				"False");
		check("SatisfiableQ((Equivalent(b11D, b21U)) && (Equivalent(b12D, b22U)) && \n"
				+ " (Equivalent(b13D, b23U)) && (Equivalent(b14D, b24U)) && \n"
				+ " (Equivalent(b15D, b25U)) && (Equivalent(b21D, b31U)) && \n"
				+ " (Equivalent(b22D, b32U)) && (Equivalent(b23D, b33U)) && \n"
				+ " (Equivalent(b24D, b34U)) && (Equivalent(b25D, b35U)) && \n"
				+ " (Equivalent(b31D, b41U)) && (Equivalent(b32D, b42U)) && \n"
				+ " (Equivalent(b33D, b43U)) && (Equivalent(b34D, b44U)) && \n"
				+ " (Equivalent(b35D, b45U)) && (Equivalent(b41D, b51U)) && \n"
				+ " (Equivalent(b42D, b52U)) && (Equivalent(b43D, b53U)) && \n"
				+ " (Equivalent(b44D, b54U)) && (Equivalent(b45D, b55U)) && \n"
				+ " (Equivalent(b11R, b12L)) && (Equivalent(b12R, b13L)) && \n"
				+ " (Equivalent(b13R, b14L)) && (Equivalent(b14R, b15L)) && \n"
				+ " (Equivalent(b21R, b22L)) && (Equivalent(b22R, b23L)) && \n"
				+ " (Equivalent(b23R, b24L)) && (Equivalent(b24R, b25L)) && \n"
				+ " (Equivalent(b31R, b32L)) && (Equivalent(b32R, b33L)) && \n"
				+ " (Equivalent(b33R, b34L)) && (Equivalent(b34R, b35L)) && \n"
				+ " (Equivalent(b41R, b42L)) && (Equivalent(b42R, b43L)) && \n"
				+ " (Equivalent(b43R, b44L)) && (Equivalent(b44R, b45L)) && \n"
				+ " (Equivalent(b51R, b52L)) && (Equivalent(b52R, b53L)) && \n"
				+ " (Equivalent(b53R, b54L)) && (Equivalent(b54R, b55L)) &&  !b11L &&  !b21L && \n"
				+ "  !b31L &&  !b41L &&  !b51L &&  !b15R &&  !b25R &&  !b35R &&  !b45R && \n"
				+ "  !b55R &&  !b11U &&  !b12U &&  !b13U &&  !b14U &&  !b15U &&  !b51D && \n"
				+ "  !b52D &&  !b53D &&  !b54D &&  !b55D && \n"
				+ " ((b11U &&  !b11D &&  !b11L &&  !b11R) || (b11D &&  !b11U &&  !b11L && \n"
				+ "    !b11R) || (b11L &&  !b11U &&  !b11D &&  !b11R) || \n"
				+ "  (b11R &&  !b11U &&  !b11D &&  !b11L)) && \n"
				+ " ((b13U &&  !b13D &&  !b13L &&  !b13R) || (b13D &&  !b13U &&  !b13L && \n"
				+ "    !b13R) || (b13L &&  !b13U &&  !b13D &&  !b13R) || \n"
				+ "  (b13R &&  !b13U &&  !b13D &&  !b13L)) && \n"
				+ " ((b15U &&  !b15D &&  !b15L &&  !b15R) || (b15D &&  !b15U &&  !b15L && \n"
				+ "    !b15R) || (b15L &&  !b15U &&  !b15D &&  !b15R) || \n"
				+ "  (b15R &&  !b15U &&  !b15D &&  !b15L)) && \n"
				+ " ((b23U &&  !b23D &&  !b23L &&  !b23R) || (b23D &&  !b23U &&  !b23L && \n"
				+ "    !b23R) || (b23L &&  !b23U &&  !b23D &&  !b23R) || \n"
				+ "  (b23R &&  !b23U &&  !b23D &&  !b23L)) && \n"
				+ " ((b25U &&  !b25D &&  !b25L &&  !b25R) || (b25D &&  !b25U &&  !b25L && \n"
				+ "    !b25R) || (b25L &&  !b25U &&  !b25D &&  !b25R) || \n"
				+ "  (b25R &&  !b25U &&  !b25D &&  !b25L)) && \n"
				+ " ((b42U &&  !b42D &&  !b42L &&  !b42R) || (b42D &&  !b42U &&  !b42L && \n"
				+ "    !b42R) || (b42L &&  !b42U &&  !b42D &&  !b42R) || \n"
				+ "  (b42R &&  !b42U &&  !b42D &&  !b42L)) && \n"
				+ " ((b44U &&  !b44D &&  !b44L &&  !b44R) || (b44D &&  !b44U &&  !b44L && \n"
				+ "    !b44R) || (b44L &&  !b44U &&  !b44D &&  !b44R) || \n"
				+ "  (b44R &&  !b44U &&  !b44D &&  !b44L)) && \n"
				+ " ((b52U &&  !b52D &&  !b52L &&  !b52R) || (b52D &&  !b52U &&  !b52L && \n"
				+ "    !b52R) || (b52L &&  !b52U &&  !b52D &&  !b52R) || \n"
				+ "  (b52R &&  !b52U &&  !b52D &&  !b52L)) && \n"
				+ " ((b53U &&  !b53D &&  !b53L &&  !b53R) || (b53D &&  !b53U &&  !b53L && \n"
				+ "    !b53R) || (b53L &&  !b53U &&  !b53D &&  !b53R) || \n"
				+ "  (b53R &&  !b53U &&  !b53D &&  !b53L)) && \n"
				+ " ((b55U &&  !b55D &&  !b55L &&  !b55R) || (b55D &&  !b55U &&  !b55L && \n"
				+ "    !b55R) || (b55L &&  !b55U &&  !b55D &&  !b55R) || \n"
				+ "  (b55R &&  !b55U &&  !b55D &&  !b55L)) && \n"
				+ " ((b12U && b12D &&  !b12L &&  !b12R) || (b12U && b12L &&  !b12D &&  !b12R) || \n"
				+ "  (b12U && b12R &&  !b12D &&  !b12L) || (b12D && b12L &&  !b12R &&  !b12U) || \n"
				+ "  (b12D && b12R &&  !b12L &&  !b12U) || (b12L && b12R &&  !b12D && \n"
				+ "    !b12U)) && ((b14U && b14D &&  !b14L &&  !b14R) || \n"
				+ "  (b14U && b14L &&  !b14D &&  !b14R) || (b14U && b14R &&  !b14D &&  !b14L) || \n"
				+ "  (b14D && b14L &&  !b14R &&  !b14U) || (b14D && b14R &&  !b14L &&  !b14U) || \n"
				+ "  (b14L && b14R &&  !b14D &&  !b14U)) && \n"
				+ " ((b21U && b21D &&  !b21L &&  !b21R) || (b21U && b21L &&  !b21D &&  !b21R) || \n"
				+ "  (b21U && b21R &&  !b21D &&  !b21L) || (b21D && b21L &&  !b21R &&  !b21U) || \n"
				+ "  (b21D && b21R &&  !b21L &&  !b21U) || (b21L && b21R &&  !b21D && \n"
				+ "    !b21U)) && ((b22U && b22D &&  !b22L &&  !b22R) || \n"
				+ "  (b22U && b22L &&  !b22D &&  !b22R) || (b22U && b22R &&  !b22D &&  !b22L) || \n"
				+ "  (b22D && b22L &&  !b22R &&  !b22U) || (b22D && b22R &&  !b22L &&  !b22U) || \n"
				+ "  (b22L && b22R &&  !b22D &&  !b22U)) && \n"
				+ " ((b24U && b24D &&  !b24L &&  !b24R) || (b24U && b24L &&  !b24D &&  !b24R) || \n"
				+ "  (b24U && b24R &&  !b24D &&  !b24L) || (b24D && b24L &&  !b24R &&  !b24U) || \n"
				+ "  (b24D && b24R &&  !b24L &&  !b24U) || (b24L && b24R &&  !b24D && \n"
				+ "    !b24U)) && ((b31U && b31D &&  !b31L &&  !b31R) || \n"
				+ "  (b31U && b31L &&  !b31D &&  !b31R) || (b31U && b31R &&  !b31D &&  !b31L) || \n"
				+ "  (b31D && b31L &&  !b31R &&  !b31U) || (b31D && b31R &&  !b31L &&  !b31U) || \n"
				+ "  (b31L && b31R &&  !b31D &&  !b31U)) && \n"
				+ " ((b32U && b32D &&  !b32L &&  !b32R) || (b32U && b32L &&  !b32D &&  !b32R) || \n"
				+ "  (b32U && b32R &&  !b32D &&  !b32L) || (b32D && b32L &&  !b32R &&  !b32U) || \n"
				+ "  (b32D && b32R &&  !b32L &&  !b32U) || (b32L && b32R &&  !b32D && \n"
				+ "    !b32U)) && ((b33U && b33D &&  !b33L &&  !b33R) || \n"
				+ "  (b33U && b33L &&  !b33D &&  !b33R) || (b33U && b33R &&  !b33D &&  !b33L) || \n"
				+ "  (b33D && b33L &&  !b33R &&  !b33U) || (b33D && b33R &&  !b33L &&  !b33U) || \n"
				+ "  (b33L && b33R &&  !b33D &&  !b33U)) && \n"
				+ " ((b34U && b34D &&  !b34L &&  !b34R) || (b34U && b34L &&  !b34D &&  !b34R) || \n"
				+ "  (b34U && b34R &&  !b34D &&  !b34L) || (b34D && b34L &&  !b34R &&  !b34U) || \n"
				+ "  (b34D && b34R &&  !b34L &&  !b34U) || (b34L && b34R &&  !b34D && \n"
				+ "    !b34U)) && ((b35U && b35D &&  !b35L &&  !b35R) || \n"
				+ "  (b35U && b35L &&  !b35D &&  !b35R) || (b35U && b35R &&  !b35D &&  !b35L) || \n"
				+ "  (b35D && b35L &&  !b35R &&  !b35U) || (b35D && b35R &&  !b35L &&  !b35U) || \n"
				+ "  (b35L && b35R &&  !b35D &&  !b35U)) && \n"
				+ " ((b41U && b41D &&  !b41L &&  !b41R) || (b41U && b41L &&  !b41D &&  !b41R) || \n"
				+ "  (b41U && b41R &&  !b41D &&  !b41L) || (b41D && b41L &&  !b41R &&  !b41U) || \n"
				+ "  (b41D && b41R &&  !b41L &&  !b41U) || (b41L && b41R &&  !b41D && \n"
				+ "    !b41U)) && ((b43U && b43D &&  !b43L &&  !b43R) || \n"
				+ "  (b43U && b43L &&  !b43D &&  !b43R) || (b43U && b43R &&  !b43D &&  !b43L) || \n"
				+ "  (b43D && b43L &&  !b43R &&  !b43U) || (b43D && b43R &&  !b43L &&  !b43U) || \n"
				+ "  (b43L && b43R &&  !b43D &&  !b43U)) && \n"
				+ " ((b45U && b45D &&  !b45L &&  !b45R) || (b45U && b45L &&  !b45D &&  !b45R) || \n"
				+ "  (b45U && b45R &&  !b45D &&  !b45L) || (b45D && b45L &&  !b45R &&  !b45U) || \n"
				+ "  (b45D && b45R &&  !b45L &&  !b45U) || (b45L && b45R &&  !b45D && \n"
				+ "    !b45U)) && ((b51U && b51D &&  !b51L &&  !b51R) || \n"
				+ "  (b51U && b51L &&  !b51D &&  !b51R) || (b51U && b51R &&  !b51D &&  !b51L) || \n"
				+ "  (b51D && b51L &&  !b51R &&  !b51U) || (b51D && b51R &&  !b51L &&  !b51U) || \n"
				+ "  (b51L && b51R &&  !b51D &&  !b51U)) && \n"
				+ " ((b54U && b54D &&  !b54L &&  !b54R) || (b54U && b54L &&  !b54D &&  !b54R) || \n"
				+ "  (b54U && b54R &&  !b54D &&  !b54L) || (b54D && b54L &&  !b54R &&  !b54U) || \n"
				+ "  (b54D && b54R &&  !b54L &&  !b54U) || (b54L && b54R &&  !b54D && \n"
				+ "    !b54U)) && ((c12a &&  !c12b &&  !c12c &&  !c12d &&  !c12e) || \n"
				+ "  (c12b &&  !c12a &&  !c12c &&  !c12d &&  !c12e) || \n"
				+ "  (c12c &&  !c12a &&  !c12b &&  !c12d &&  !c12e) || \n"
				+ "  (c12d &&  !c12a &&  !c12b &&  !c12c &&  !c12e) || \n"
				+ "  (c12e &&  !c12a &&  !c12b &&  !c12c &&  !c12d)) && \n"
				+ " ((c14a &&  !c14b &&  !c14c &&  !c14d &&  !c14e) || \n"
				+ "  (c14b &&  !c14a &&  !c14c &&  !c14d &&  !c14e) || \n"
				+ "  (c14c &&  !c14a &&  !c14b &&  !c14d &&  !c14e) || \n"
				+ "  (c14d &&  !c14a &&  !c14b &&  !c14c &&  !c14e) || \n"
				+ "  (c14e &&  !c14a &&  !c14b &&  !c14c &&  !c14d)) && \n"
				+ " ((c21a &&  !c21b &&  !c21c &&  !c21d &&  !c21e) || \n"
				+ "  (c21b &&  !c21a &&  !c21c &&  !c21d &&  !c21e) || \n"
				+ "  (c21c &&  !c21a &&  !c21b &&  !c21d &&  !c21e) || \n"
				+ "  (c21d &&  !c21a &&  !c21b &&  !c21c &&  !c21e) || \n"
				+ "  (c21e &&  !c21a &&  !c21b &&  !c21c &&  !c21d)) && \n"
				+ " ((c22a &&  !c22b &&  !c22c &&  !c22d &&  !c22e) || \n"
				+ "  (c22b &&  !c22a &&  !c22c &&  !c22d &&  !c22e) || \n"
				+ "  (c22c &&  !c22a &&  !c22b &&  !c22d &&  !c22e) || \n"
				+ "  (c22d &&  !c22a &&  !c22b &&  !c22c &&  !c22e) || \n"
				+ "  (c22e &&  !c22a &&  !c22b &&  !c22c &&  !c22d)) && \n"
				+ " ((c24a &&  !c24b &&  !c24c &&  !c24d &&  !c24e) || \n"
				+ "  (c24b &&  !c24a &&  !c24c &&  !c24d &&  !c24e) || \n"
				+ "  (c24c &&  !c24a &&  !c24b &&  !c24d &&  !c24e) || \n"
				+ "  (c24d &&  !c24a &&  !c24b &&  !c24c &&  !c24e) || \n"
				+ "  (c24e &&  !c24a &&  !c24b &&  !c24c &&  !c24d)) && \n"
				+ " ((c31a &&  !c31b &&  !c31c &&  !c31d &&  !c31e) || \n"
				+ "  (c31b &&  !c31a &&  !c31c &&  !c31d &&  !c31e) || \n"
				+ "  (c31c &&  !c31a &&  !c31b &&  !c31d &&  !c31e) || \n"
				+ "  (c31d &&  !c31a &&  !c31b &&  !c31c &&  !c31e) || \n"
				+ "  (c31e &&  !c31a &&  !c31b &&  !c31c &&  !c31d)) && \n"
				+ " ((c32a &&  !c32b &&  !c32c &&  !c32d &&  !c32e) || \n"
				+ "  (c32b &&  !c32a &&  !c32c &&  !c32d &&  !c32e) || \n"
				+ "  (c32c &&  !c32a &&  !c32b &&  !c32d &&  !c32e) || \n"
				+ "  (c32d &&  !c32a &&  !c32b &&  !c32c &&  !c32e) || \n"
				+ "  (c32e &&  !c32a &&  !c32b &&  !c32c &&  !c32d)) && \n"
				+ " ((c33a &&  !c33b &&  !c33c &&  !c33d &&  !c33e) || \n"
				+ "  (c33b &&  !c33a &&  !c33c &&  !c33d &&  !c33e) || \n"
				+ "  (c33c &&  !c33a &&  !c33b &&  !c33d &&  !c33e) || \n"
				+ "  (c33d &&  !c33a &&  !c33b &&  !c33c &&  !c33e) || \n"
				+ "  (c33e &&  !c33a &&  !c33b &&  !c33c &&  !c33d)) && \n"
				+ " ((c34a &&  !c34b &&  !c34c &&  !c34d &&  !c34e) || \n"
				+ "  (c34b &&  !c34a &&  !c34c &&  !c34d &&  !c34e) || \n"
				+ "  (c34c &&  !c34a &&  !c34b &&  !c34d &&  !c34e) || \n"
				+ "  (c34d &&  !c34a &&  !c34b &&  !c34c &&  !c34e) || \n"
				+ "  (c34e &&  !c34a &&  !c34b &&  !c34c &&  !c34d)) && \n"
				+ " ((c35a &&  !c35b &&  !c35c &&  !c35d &&  !c35e) || \n"
				+ "  (c35b &&  !c35a &&  !c35c &&  !c35d &&  !c35e) || \n"
				+ "  (c35c &&  !c35a &&  !c35b &&  !c35d &&  !c35e) || \n"
				+ "  (c35d &&  !c35a &&  !c35b &&  !c35c &&  !c35e) || \n"
				+ "  (c35e &&  !c35a &&  !c35b &&  !c35c &&  !c35d)) && \n"
				+ " ((c41a &&  !c41b &&  !c41c &&  !c41d &&  !c41e) || \n"
				+ "  (c41b &&  !c41a &&  !c41c &&  !c41d &&  !c41e) || \n"
				+ "  (c41c &&  !c41a &&  !c41b &&  !c41d &&  !c41e) || \n"
				+ "  (c41d &&  !c41a &&  !c41b &&  !c41c &&  !c41e) || \n"
				+ "  (c41e &&  !c41a &&  !c41b &&  !c41c &&  !c41d)) && \n"
				+ " ((c43a &&  !c43b &&  !c43c &&  !c43d &&  !c43e) || \n"
				+ "  (c43b &&  !c43a &&  !c43c &&  !c43d &&  !c43e) || \n"
				+ "  (c43c &&  !c43a &&  !c43b &&  !c43d &&  !c43e) || \n"
				+ "  (c43d &&  !c43a &&  !c43b &&  !c43c &&  !c43e) || \n"
				+ "  (c43e &&  !c43a &&  !c43b &&  !c43c &&  !c43d)) && \n"
				+ " ((c45a &&  !c45b &&  !c45c &&  !c45d &&  !c45e) || \n"
				+ "  (c45b &&  !c45a &&  !c45c &&  !c45d &&  !c45e) || \n"
				+ "  (c45c &&  !c45a &&  !c45b &&  !c45d &&  !c45e) || \n"
				+ "  (c45d &&  !c45a &&  !c45b &&  !c45c &&  !c45e) || \n"
				+ "  (c45e &&  !c45a &&  !c45b &&  !c45c &&  !c45d)) && \n"
				+ " ((c51a &&  !c51b &&  !c51c &&  !c51d &&  !c51e) || \n"
				+ "  (c51b &&  !c51a &&  !c51c &&  !c51d &&  !c51e) || \n"
				+ "  (c51c &&  !c51a &&  !c51b &&  !c51d &&  !c51e) || \n"
				+ "  (c51d &&  !c51a &&  !c51b &&  !c51c &&  !c51e) || \n"
				+ "  (c51e &&  !c51a &&  !c51b &&  !c51c &&  !c51d)) && \n"
				+ " ((c54a &&  !c54b &&  !c54c &&  !c54d &&  !c54e) || \n"
				+ "  (c54b &&  !c54a &&  !c54c &&  !c54d &&  !c54e) || \n"
				+ "  (c54c &&  !c54a &&  !c54b &&  !c54d &&  !c54e) || \n"
				+ "  (c54d &&  !c54a &&  !c54b &&  !c54c &&  !c54e) || \n"
				+ "  (c54e &&  !c54a &&  !c54b &&  !c54c &&  !c54d)) && \n"
				+ " Implies(b11D, c21a &&  !c21b &&  !c21c &&  !c21d &&  !c21e) && \n"
				+ " Implies(b12D, (Equivalent(c12a, c22a)) && (Equivalent(c12b, c22b)) && \n"
				+ "   (Equivalent(c12c, c22c)) && (Equivalent(c12d, c22d)) && \n"
				+ "   (Equivalent(c12e, c22e))) &&  !b13D && \n"
				+ " Implies(b14D, (Equivalent(c14a, c24a)) && (Equivalent(c14b, c24b)) && \n"
				+ "   (Equivalent(c14c, c24c)) && (Equivalent(c14d, c24d)) && \n"
				+ "   (Equivalent(c14e, c24e))) &&  !b15D && \n"
				+ " Implies(b21D, (Equivalent(c21a, c31a)) && (Equivalent(c21b, c31b)) && \n"
				+ "   (Equivalent(c21c, c31c)) && (Equivalent(c21d, c31d)) && \n"
				+ "   (Equivalent(c21e, c31e))) && Implies(b22D, (Equivalent(c22a, c32a)) && \n"
				+ "   (Equivalent(c22b, c32b)) && (Equivalent(c22c, c32c)) && \n"
				+ "   (Equivalent(c22d, c32d)) && (Equivalent(c22e, c32e))) && \n"
				+ " Implies(b23D,  !c33a &&  !c33b && c33c &&  !c33d &&  !c33e) && \n"
				+ " Implies(b24D, (Equivalent(c24a, c34a)) && (Equivalent(c24b, c34b)) && \n"
				+ "   (Equivalent(c24c, c34c)) && (Equivalent(c24d, c34d)) && \n"
				+ "   (Equivalent(c24e, c34e))) && Implies(b25D,  !c35a &&  !c35b &&  !c35c && \n"
				+ "    !c35d && c35e) && Implies(b31D, (Equivalent(c31a, c41a)) && \n"
				+ "   (Equivalent(c31b, c41b)) && (Equivalent(c31c, c41c)) && \n"
				+ "   (Equivalent(c31d, c41d)) && (Equivalent(c31e, c41e))) && \n"
				+ " Implies(b32D,  !c32a && c32b &&  !c32c &&  !c32d &&  !c32e) && \n"
				+ " Implies(b33D, (Equivalent(c33a, c43a)) && (Equivalent(c33b, c43b)) && \n"
				+ "   (Equivalent(c33c, c43c)) && (Equivalent(c33d, c43d)) && \n"
				+ "   (Equivalent(c33e, c43e))) && Implies(b34D,  !c34a &&  !c34b &&  !c34c && \n"
				+ "   c34d &&  !c34e) && Implies(b35D, (Equivalent(c35a, c45a)) && \n"
				+ "   (Equivalent(c35b, c45b)) && (Equivalent(c35c, c45c)) && \n"
				+ "   (Equivalent(c35d, c45d)) && (Equivalent(c35e, c45e))) && \n"
				+ " Implies(b41D, (Equivalent(c41a, c51a)) && (Equivalent(c41b, c51b)) && \n"
				+ "   (Equivalent(c41c, c51c)) && (Equivalent(c41d, c51d)) && \n"
				+ "   (Equivalent(c41e, c51e))) &&  !b42D && \n"
				+ " Implies(b43D,  !c43a &&  !c43b && c43c &&  !c43d &&  !c43e) && \n"
				+ " Implies(b44D,  !c54a &&  !c54b &&  !c54c && c54d &&  !c54e) && \n"
				+ " Implies(b45D,  !c45a &&  !c45b &&  !c45c &&  !c45d && c45e) && \n"
				+ " Implies(b11R, c12a &&  !c12b &&  !c12c &&  !c12d &&  !c12e) && \n"
				+ " Implies(b12R,  !c12a && c12b &&  !c12c &&  !c12d &&  !c12e) && \n"
				+ " Implies(b13R,  !c14a && c14b &&  !c14c &&  !c14d &&  !c14e) && \n"
				+ " Implies(b14R,  !c14a &&  !c14b &&  !c14c && c14d &&  !c14e) && \n"
				+ " Implies(b21R, (Equivalent(c21a, c22a)) && (Equivalent(c21b, c22b)) && \n"
				+ "   (Equivalent(c21c, c22c)) && (Equivalent(c21d, c22d)) && \n"
				+ "   (Equivalent(c21e, c22e))) && Implies(b22R,  !c22a &&  !c22b && c22c && \n"
				+ "    !c22d &&  !c22e) && Implies(b23R,  !c24a &&  !c24b && c24c &&  !c24d && \n"
				+ "    !c24e) && Implies(b24R,  !c24a &&  !c24b &&  !c24c &&  !c24d && c24e) && \n"
				+ " Implies(b31R, (Equivalent(c31a, c32a)) && (Equivalent(c31b, c32b)) && \n"
				+ "   (Equivalent(c31c, c32c)) && (Equivalent(c31d, c32d)) && \n"
				+ "   (Equivalent(c31e, c32e))) && Implies(b32R, (Equivalent(c32a, c33a)) && \n"
				+ "   (Equivalent(c32b, c33b)) && (Equivalent(c32c, c33c)) && \n"
				+ "   (Equivalent(c32d, c33d)) && (Equivalent(c32e, c33e))) && \n"
				+ " Implies(b33R, (Equivalent(c33a, c34a)) && (Equivalent(c33b, c34b)) && \n"
				+ "   (Equivalent(c33c, c34c)) && (Equivalent(c33d, c34d)) && \n"
				+ "   (Equivalent(c33e, c34e))) && Implies(b34R, (Equivalent(c34a, c35a)) && \n"
				+ "   (Equivalent(c34b, c35b)) && (Equivalent(c34c, c35c)) && \n"
				+ "   (Equivalent(c34d, c35d)) && (Equivalent(c34e, c35e))) && \n"
				+ " Implies(b41R,  !c41a && c41b &&  !c41c &&  !c41d &&  !c41e) && \n"
				+ " Implies(b42R,  !c43a && c43b &&  !c43c &&  !c43d &&  !c43e) && \n"
				+ " Implies(b43R,  !c43a &&  !c43b &&  !c43c && c43d &&  !c43e) && \n"
				+ " Implies(b44R,  !c45a &&  !c45b &&  !c45c && c45d &&  !c45e) && \n"
				+ " Implies(b51R, c51a &&  !c51b &&  !c51c &&  !c51d &&  !c51e) &&  !b52R && \n"
				+ " Implies(b53R,  !c54a &&  !c54b && c54c &&  !c54d &&  !c54e) && \n"
				+ " Implies(b54R,  !c54a &&  !c54b &&  !c54c &&  !c54d && c54e))", //
				"False");

	}

	public void testScan() {
		// TODO e1 ~ e2 ~ e3 => e2[e1, e3]
		// check("(Print@#; #0 ~Scan~ #)& @ {{1, {2, 3}}, {4, 5}}", //
		// "");

		// prints
		// 1
		// 2
		// 3
		// {2,3}
		// {1,{2,3}}
		// 4
		// 5
		// {4,5}
		// {{1,{2,3}},{4,5}}
		check("expr = {{1, {2, 3}}, {4, 5}}; Scan(Print, expr, {0, -1})", //
				"");

		check("Scan(Print)[{a, b, c}]", //
				"");
		check("Scan(Print, {1, 2, 3}, Heads->True)", //
				"");
		check("Scan(($u(#) = x) &, {55, 11, 77, 88});{$u(76), $u(77), $u(78)}", //
				"{$u(76),x,$u(78)}");
		check("Map(If(# > 5, #, False) &, {2, 4, 6, 8})", //
				"{False,False,6,8}");
		check("Catch(Map(If(# > 5, Throw(#)) &, {2, 4, 6, 8}))", //
				"6");
		check("Catch(Scan(If(# > 5, Throw(#)) &, {2, 4, 6, 8}))", //
				"6");
		check("Reap(Scan(\n" + "   If(# > 0, Sow(#)) &, {1, {-2, Pi}, -Sqrt(3)},Infinity))[[2, 1]]", //
				"{1,Pi,3,1/2,Sqrt(3)}");
		check("Scan(Return, {1, 2})", //
				"1");
		check("Reap(Scan(Sow, -(ArcTan((1 + 2*x)/Sqrt(3))/Sqrt(3)) + (1/3)*Log(1 - x) - (1/6)*Log(1 + x + x^2), {-1}))[[2, 1]]", //
				"{-1,3,-1/2,3,-1/2,1,2,x,1/3,1,-1,x,-1/6,1,x,x,2}");
	}

	public void testSec() {
		check("-Sec(Pi/4-x)", //
				"-Sec(Pi/4-x)");
		check("-Sec(Pi/3-x)", //
				"-Sec(Pi/3-x)");
		check("Sec(5/7*Pi+x)", //
				"-Csc(3/14*Pi+x)");
		check("Sec(3/4*Pi+x)", //
				"-Csc(Pi/4+x)");
		check("Sec(-3/4*Pi+x)", //
				"-Sec(Pi/4+x)");

		check("Sec(e - Pi/2 + f*x)", //
				"Csc(e+f*x)");
		check("Sec(e+f*x)^m*Tan(e+f*x)^2", //
				"Sec(e+f*x)^(2+m)*Sin(e+f*x)^2");
		check("Sec(e+f*x)^m*Tan(e+f*x)", //
				"Sec(e+f*x)^(1+m)*Sin(e+f*x)");

		check("Sec(Pi/2+Pi*n)", //
				"-Csc(n*Pi)");
		check("Sec(0)", //
				"1");
		check("Sec(1)", //
				"Sec(1)");
		checkNumeric("Sec(1.)", //
				"1.8508157176809255");

		check("Sec(Pi/2)", //
				"ComplexInfinity");
		check("Sec(0)", //
				"1");
		check("Sec(2/5*Pi)", //
				"1+Sqrt(5)");
		check("Sec(23/12*Pi)", //
				"-Sqrt(2)+Sqrt(6)");
		check("Sec(z+1/2*Pi)", //
				"-Csc(z)");
		check("Sec(Pi)", //
				"-1");
		check("Sec(33*Pi)", //
				"-1");
		check("Sec(z+Pi)", //
				"-Sec(z)");
		check("Sec(z+42*Pi)", //
				"Sec(z)");
		check("Sec(x+y+z+43*Pi)", //
				"-Sec(x+y+z)");
		check("Sec(z+42*a*Pi)", //
				"Sec(42*a*Pi+z)");
		check("Sec(z+4/3*Pi)", //
				"-Sec(Pi/3+z)");
		check("Sec(Sqrt(x^2))", //
				"Sec(x)");
	}

	public void testSech() {
		check("Refine(Sech(x+I*k*Pi), Element(k, Integers))", //
				"(-1)^k*Sech(x)");
		check("Refine(Sech(x-I*k*Pi), Element(k, Integers))", //
				"(-1)^k*Sech(x)");
		check("Refine(Sech(x-42*I*k*Pi), Element(k, Integers))", //
				"Sech(x)");
		check("Refine(Sech(x-43*I*k*Pi), Element(k, Integers))", //
				"(-1)^k*Sech(x)");

		check("Sech(x)^m*Tanh(x)^2", //
				"Sech(x)^(2+m)*Sinh(x)^2");
		check("Sech(0)", //
				"1");
		checkNumeric("Sech(1.8)", //
				"0.3218048695065878");
		check("Sech(-x)", //
				"Sech(x)");
		check("D(Sech(x),x)", //
				"-Sech(x)*Tanh(x)");
	}

	public void testSelect() {
		check("Select(Accumulate(Table({1,Prime(x)},{x,900,1000})), PrimeQ( #[[2]] )& )", //
				"{{1,6997},{3,21011},{7,49139},{11,77447},{87,644377},{93,691333}}");
		check("Select(# > 4 &) [{1, 2.2, 3, 4.5, 5, 6, 7.5, 8}]", //
				"{4.5,5,6,7.5,8}");
		check("Cases(_Integer)@Select(# > 4 &)@{1, 2.2, 3, 4.5, 5, 6, 7.5, 8}", //
				"{5,6,8}");
		check("Select({-3, 0, 1, 3, a}, #>0&)", //
				"{1,3}");
		check("Select(f(a, 2, 3), NumberQ)", //
				"f(2,3)");
		check("Select(a, True)", //
				"Select(a,True)");

		check("Select({1, 2, 4, 7, 6, 2}, EvenQ)", //
				"{2,4,6,2}");
		check("Select({1, 2, 4, 7, 6, 2}, # > 2 &)", //
				"{4,7,6}");
		check("Select({1, 2, 4, 7, 6, 2}, # > 2 &, 1)", //
				"{4}");
		check("Select({1, 2, 4, 7, x}, # > 2 &)", //
				"{4,7}");
		check("Select(f(1, a, 2, b, 3), IntegerQ)", //
				"f(1,2,3)");
		check("Select(Range(100), Mod(#, 3) == 1 && Mod(#, 5) == 1 &)", //
				"{1,16,31,46,61,76,91}");
	}

	public void testSequence() {
		check("Hold(a, Sequence(b, c))", //
				"Hold(a,b,c)");
		check("{Sequence( ),a}", //
				"{a}");
		check("f(a, Sequence( ),b,c)", //
				"f(a,b,c)");
		check("{u, u, u} /. u -> Sequence(a, b, c)", //
				"{a,b,c,a,b,c,a,b,c}");
		check("f(a, Sequence(b, c), d)", //
				"f(a,b,c,d)");
		check("$u = Sequence(a, b, c)", //
				"Sequence(a,b,c)");
		check("$u = Sequence(a, b, c);{$u,$u,$u}", //
				"{a,b,c,a,b,c,a,b,c}");
		check("f({{a, b}, {c, d}, {a}}) /. List -> Sequence", //
				"f(a,b,c,d,a)");
		check("f(a, b, c) /. f(x__) -> x", //
				"Sequence(a,b,c)");
		check("{a, Sequence(b), c, Identity(d)}", //
				"{a,b,c,d}");
	}

	public void testSet() {
		check("aVar=10", //
				"10");
		// integer not allowed as header is (Protected)
		check("aVar(x_):={x}", //
				"$Failed");

		// check("A = {{1, 2}, {3, 4}}", "{{1,2},{3,4}}");
		// check("A[[;;, 2]] = {6, 7} ", "{6,7}");
		// check("A", "{{1,6},{3,7}}");
		//

		// check("B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}} ", "{{1,2,3},{4,5,6},{7,8,9}}");
		// check("B[[1;;2, 2;;-1]] = {{t, u}, {y, z}}", "{{t,u},{y,z}}");
		// check("B", "{{1,t,u},{4,y,z},{7,8,9}}");

		check("foo=barf", //
				"barf");
		check("foo(x)=1", //
				"1");
		check("barf(x)=1", //
				"1");

		check("a = 3", //
				"3");
		check("a", //
				"3");
		check("{a, b, c} = {10, 2, 3}   ", //
				"{10,2,3}");
		check("{a, b, {c, {d}}} = {1, 2, {{c1, c2}, {a}}} ", //
				"{1,2,{{c1,c2},{10}}}");
		check("d", //
				"10");
		check("a", //
				"1");
		check("x = a", //
				"1");
		check("a = 2", //
				"2");
		check("x", //
				"1");

		check("a = b = c = 2", //
				"2");
		check("a == b == c == 2", //
				"True");

		check("A = {{1, 2}, {3, 4}}", //
				"{{1,2},{3,4}}");
		check("A[[1, 2]] = 5", //
				"5");
		check("A", //
				"{{1,5},{3,4}}");
		check("A[[;;, 2]] = {6, 7} ", //
				"{6,7}");
		check("A", "{{1,6},{3,7}}");

		check("B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}} ", //
				"{{1,2,3},{4,5,6},{7,8,9}}");
		check("B[[1;;2, 2;;-1]] = {{t, u}, {y, z}}", //
				"{{t,u},{y,z}}");
		check("B", //
				"{{1,t,u},{4,y,z},{7,8,9}}");
	}

	public void testSetAttributes() {
		check("SetAttributes({f, g}, {Flat, Orderless})", //
				"");
		check("Attributes(f)", //
				"{Flat,Orderless}");
		check("SetAttributes(h, Flat)", //
				"");
		check("Attributes(h)", //
				"{Flat}");
	}

	public void testSetDelayed() {
		check("Attributes(SetDelayed)  ", //
				"{HoldAll,Protected,SequenceHold}");
		check("a = 1", //
				"1");
		check("x := a", //
				"");
		check("x", //
				"1");
		check("a = 2", //
				"2");
		check("x", //
				"2");

		check("f(x_) := p(x) /; x>0", //
				"");
		check("f(3)", //
				"p(3)");
		check("f(-3)", //
				"f(-3)");
	}

	public void testShare() {
		check("Share(Table(j*(x + i), {i, 5}, {j, i}))", //
				"24");
		check("Share(Table(xi = x + i; Table(j*xi, {j, i}), {i, 5}))", //
				"0");
	}

	public void testSingularValueDecomposition() {
		check("SingularValueDecomposition({{1.5, 2.0}, {2.5, 3.0}})", //
				"{{{0.538954,0.842335},\n" + " {0.842335,-0.538954}},{{4.63555,0.0},\n"
						+ " {0.0,0.107862}},{{0.628678,-0.777666},\n" + " {0.777666,0.628678}}}");
		check("SingularValueDecomposition({{3/2, 2}, {5/2, 3}})", //
				"{{{0.538954,0.842335},\n" + " {0.842335,-0.538954}},{{4.63555,0.0},\n"
						+ " {0.0,0.107862}},{{0.628678,-0.777666},\n" + " {0.777666,0.628678}}}");
		check("SingularValueDecomposition({1, {2}})", //
				"SingularValueDecomposition({1,{2}})");
	}

	public void testSign() {
		check("Sign(Sign(z))", //
				"Sign(z)");
		check("Sign(Exp(z))", //
				"E^(I*Im(z))");

		check("Sign(2+I)", //
				"(2+I)/Sqrt(5)");
		check("Sign(1+I*Sqrt(3))", //
				"1/2*(1+I*Sqrt(3))");
		check("Sign(Sqrt(3)+I)", //
				"1/2*(I+Sqrt(3))");
		check("Sign(1.0+I)", //
				"0.707107+I*0.707107");

		check("Sign(Indeterminate)", //
				"Indeterminate");
		check("Sign(2.5)", //
				"1");
		check("Sign(-2.5)", //
				"-1");
		check("Sign(0.0)", //
				"0");
		check("Sign({-2, -1, 0, 1, 2})", //
				"{-1,-1,0,1,1}");
		check("Pi>E", //
				"True");
		check("Pi<E", //
				"False");
		check("Sign(1+I)", //
				"(1+I)/Sqrt(2)");

		check("Sign(E - Pi)", //
				"-1");
		check("Sign(0)", //
				"0");
		check("Sign(I)", //
				"I");
		check("Sign(-2*I)", //
				"-I");
		check("Sign(Indeterminate)", //
				"Indeterminate");
		check("Sign(Infinity)", //
				"1");
		check("Sign(-Infinity)", //
				"-1");
		check("Sign(DirectedInfinity(1+I*3))", //
				"(1+I*3)/Sqrt(10)");
		check("Sign(ComplexInfinity)", //
				"Indeterminate");
		check("Sign(I*Infinity)", //
				"I");

		check("Sign(-x)", //
				"-Sign(x)");
		check("Sign(-3*a*b*c)", //
				"-Sign(a*b*c)");
		check("Sign(1/z)", //
				"1/Sign(z)");
	}

	// public void testSimplify0() {
	// check("Factor(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
	// "2/((1+x)*(1/(x*(1+x))-ArcTan(Sqrt(x))/x^(3/2)))");
	// check("Simplify(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
	// "(-2*x^(3/2))/(-Sqrt(x) + (1 + x)*ArcTan(Sqrt(x)))");
	// }

	public void testSimplify() {

		// https://github.com/axkr/symja_android_library/issues/142
		check("Simplify({{x+y+x*y==9},{x*y*(x+y)==20}})", //
				"{{x+y+x*y==9},{x*y*(x+y)==20}}");
		check("Simplify(-3+2*x+x^2==0)", //
				"2*x+x^2==3");
		check("Simplify({Im(Exp(I*Pi/5)* x), Im(2*x + I)}, x > 3)", //
				"{Im(E^(I*1/5*Pi)*x),1}");

		// check("Simplify(1/((1+x)*(1/(2*x*(1+x))-ArcTan(Sqrt(x))/(2*x^(3/2)))))", //
		// "(-2*x^(3/2))/(-Sqrt(x) + (1 + x)*ArcTan(Sqrt(x)))");
		// check("Simplify((-1/(Sqrt(x)*(1+x))+(-1-x)/(2*x^(3/2)*(1+x)))/(1+x))", //
		// "(-1 - 3*x)/(2*x^(3/2)*(1 + x)^2)");
		// check("Simplify(1/(ArcTan(Sqrt(x))/(-1/(Sqrt(x)*(1+x)^2)-1/(2*x^(3/2)*(1+x))))", //
		// "(-2*x^(3/2)*(1 + x)^2*ArcTan(Sqrt(x)))/(1 + 3*x)");
		//
		// check("Simplify((1+(-2*x*(d*Sqrt(-e/d)+e*x))/(d+e*x^2))/((d+e*x^2)*((-4*e*(d*Sqrt(-e/d)+e*x)*x^2)/(d+e*x^2)^2+(2*e*x)/(d+e*x^2)+(2*(d*Sqrt(-e/d)+e*x))/(d+e*x^2))))",
		// //
		// "(-d+2*d*Sqrt(-e/d)*x+e*x^2)/(2*d*(-d*Sqrt(-e/d)-2*e*x+e*Sqrt(-e/d)*x^2))");//"-Sqrt(-(e/d))/(2*e)");
		check("Simplify((x*(-Sqrt(-4+x^2)+x^2*Sqrt(-4+x^2)-4*Sqrt(-1+x^2)+x^2*Sqrt(-1+x^2)))/((4-5*x^2+x^4)*(x/Sqrt(-4+x^2)+x/Sqrt(-1+x^2))))", //
				"1");
		check("Simplify((Cos(x)-I*Sin(x))/(I*Cos(x)-Sin(x)))", //
				"-I*Cos(2*x)-Sin(2*x)"); // -I*Cos(2*x)-Sin(2*x)

		check("Expand((-I*a+b)*(I*Cosh(x)+Sinh(x)))", //
				"a*Cosh(x)+I*b*Cosh(x)-I*a*Sinh(x)+b*Sinh(x)");
		check("Factor(TrigToExp( a*Cosh(x)+I*b*Cosh(x)-I*a*Sinh(x)+b*Sinh(x) ))", //
				"((1/2+I*1/2)*(-I*a+b)*(I+E^(2*x)))/E^x");
		check("Simplify((-I*a+b)*(I*Cosh(x)+Sinh(x)))", //
				"(a+I*b)*(Cosh(x)-I*Sinh(x))");
		check("Simplify(Element(x, Reals), x>0)", //
				"True");
		check("Simplify(Sin(n*Pi), Element(n, Integers))", //
				"0");
		check("Simplify(Tan(x + Pi*n), Element(n, Integers))", //
				"Tan(x)");
		check("Simplify(a/((a-I*b)*(a/(a-I*b)+(-I*b)/(a-I*b)))+(b*Sinh(x))/((a-I*b)*(a/(a-I*b)+(-I*b)/(a-I*b))))", //
				"(a+b*Sinh(x))/(a-I*b)");
		check("Simplify((a-I*b)*(a/(a-I*b)+(-I*b)/(a-I*b)))", //
				"a-I*b");
		check("Simplify(-2*Log(2))", //
				"-Log(4)");
		check("Simplify(Log(6)-Log(2))", //
				"Log(3)");
		check("Simplify(a+Log(1/6)+Log(1/7)+z())", //
				"a-Log(42)+z()");
		check("Simplify(a+Log(1/6)+Log(1/7)+Log(3/4)+z())", //
				"a-Log(56)+z()");
		check("Simplify(a+Log(1/6)-2*Log(1/7)+7*Log(3/4)+z())", //
				"a+Log(35721/32768)+z()");
		check("Simplify(1+n/2)", //
				"1/2*(2+n)");
		check("Simplify((9-Sqrt(57))*x^2)", //
				"-(-9+Sqrt(57))*x^2");
		check("Simplify(-a/(-b+a*c))", //
				"a/(b-a*c)");
		check("Simplify(1/(Cos(x)+I*Sin(x))-(c+d*x)^n)", //
				"-(c+d*x)^n+Cos(x)-I*Sin(x)");
		check("Simplify(1/(Cos(x)+I*Sin(x)))", //
				"Cos(x)-I*Sin(x)");
		check("Expand((Sqrt(-d)*e+d*Sqrt(e)*Sqrt(-e/d))*(Sqrt(-d)*e-d*Sqrt(e)*Sqrt(-e/d)))", //
				"0");
		check("Simplify((e*x^2)/(Sqrt(-d)*e-d*Sqrt(e)*Sqrt(-e/d)))", //
				"(e*x^2)/(Sqrt(-d)*e-d*Sqrt(e)*Sqrt(-e/d))");
		check("Simplify((-b^12*x^456)/x^12+(x^12*(a+b*x^37)^12)/x^12)", //
				"-b^12*x^444+(a+b*x^37)^12");
		check("Simplify(-Cos(x) +Sin(x)^2/(1-Cos(x)))", //
				"1");
		check("Simplify(-Cos(x)/(1-Cos(x))+Sin(x)^2/(1-Cos(x))^2-1/(1-Cos(x)))", //
				"0");

		check("Simplify(x^(5/2) - Sqrt(x^5), x>=0)", //
				"0");
		check("Simplify(-136+40*Sqrt(17))", //
				"8*(-17+5*Sqrt(17))");
		check("Simplify(Sqrt(17)/(5+Sqrt(17)))", //
				"1/8*(-17+5*Sqrt(17))");

		// check("Simplify(Cos(b*x)/(-Cos(b*x)/x^2-CosIntegral(b*x)/x^2))", //
		// "(x^2*Cos(b*x))/(-Cos(b*x)-CosIntegral(b*x))");
		check("Together(-Cos(b*x)/x^2+(-b*Sin(b*x))/x)", //
				"(-Cos(b*x)-b*x*Sin(b*x))/x^2");
		check("Simplify(-Cos(b*x)/x^2+(-b*Sin(b*x))/x)", //
				"(-Cos(b*x)-b*x*Sin(b*x))/x^2");

		check("Simplify(-(b/(2*Sqrt(c))+Sqrt(c)*x)^24+(a+b*x+c*x^2)^12)", //
				"-(b/(2*Sqrt(c))+Sqrt(c)*x)^24+(a+b*x+c*x^2)^12");
		check("Simplify(-ArcTan((1+x)/Sqrt(2))/(2*Sqrt(2)))", //
				"-ArcTan((1+x)/Sqrt(2))/(2*Sqrt(2))");
		check("Simplify(1 + 1/GoldenRatio - GoldenRatio)", //
				"0");
		// check("Simplify(-15-6*x)/(3*(1+x+x^2))", "");
		check("Simplify(Abs(x), x<0)", //
				"Abs(x)");
		check("complexity(x_) := 2*Count(x, _Abs, {0, 10}) + LeafCount(x)", //
				"");
		check("Simplify(Abs(x), x<0, ComplexityFunction->complexity)", //
				"-x");

		check("Simplify(100*Log(2))", //
				"100*Log(2)");
		check("Simplify(2*Sin(x)^2 + 2*Cos(x)^2)", //
				"2");
		check("Simplify(f(x))", //
				"f(x)");
		check("Simplify(a*x^2+b*x^2)", //
				"(a+b)*x^2");

		check("Simplify(5*x*(6*x+30))", //
				"30*x*(5+x)");
		check("Simplify(Sqrt(x^2), Assumptions -> x>0)", //
				"x");
		check("Simplify(Sqrt(x^2), x>0)", //
				"x");
		check("Together(2/(1/Tan(x) + Tan(x)))", //
				"2/(Cot(x)+Tan(x))");
		check("Together(2*Tan(x)/(1 + Tan(x)^2))", //
				"(2*Tan(x))/(1+Tan(x)^2)");
		check("Simplify(Sin(x)^2 + Cos(x)^2)", //
				"1");
		check("Simplify((x - 1)*(x + 1)*(x^2 + 1) + 1)", //
				"x^4");
		check("Simplify(3/(x + 3) + x/(x + 3))", //
				"1");

		check("Simplify(2*Tan(x)/(1 + Tan(x)^2))", //
				"(2*Tan(x))/(1+Tan(x)^2)");

	}

	public void testSin() {
		check("Sin(Interval({-Infinity,Infinity}))", //
				"Interval({-1,1})");
		check("Sin(5*Pi/12)", //
				"(1+Sqrt(3))/(2*Sqrt(2))");
		// check("Sin(Quantity(90,\"Degree\"))",
		// "");
		check("Sin(-37/3*Pi+x)", //
				"-Cos(Pi/6+x)");
		check("Sin(83/7*Pi+x)", //
				"-Cos(5/14*Pi+x)");
		check("Refine(Sin(x+k*Pi), Element(k, Integers))", //
				"(-1)^k*Sin(x)");
		check("Sin(3/4*Pi+x)", //
				"Cos(Pi/4+x)");
		check("Sin(5/7*Pi+x)", //
				"Cos(3/14*Pi+x)");
		check("Sin(-3/4*Pi+x)", //
				"-Sin(Pi/4+x)");

		check("Sin({})", //
				"{}");
		check("Sin(e - Pi/2 + f*x)", //
				"-Cos(e+f*x)");
		check("Sin( -3/x+x )", //
				"-Sin(3/x-x)");
		check("Sin((-3+x^2)/x)", //
				"Sin((-3+x^2)/x)");
		check("Sin(-Pi/2+z)", //
				"-Cos(z)");
		check("Sin(e-Pi/2+f*x)", //
				"-Cos(e+f*x)");
		check("Sin(e-3/2*Pi+f*x)", //
				"Cos(e+f*x)");
		check("Sin(e-1+f*x)", //
				"-Sin(1-e-f*x)");

		check("Sin(ArcSin(x))", //
				"x");
		check("Sin(ArcCos(x))", //
				"Sqrt(1-x^2)");
		check("Sin(ArcTan(x))", //
				"x/Sqrt(1+x^2)");
		check("Sin(ArcCot(x))", //
				"1/Sqrt(1+x^2)");
		check("Sin(ArcCsc(x))", //
				"1/x");
		check("Sin(ArcSec(x))", //
				"Sqrt(1-1/x^2)");

		check("Sin(Pi/4)", //
				"1/Sqrt(2)");
		check("Sin(0)", //
				"0");
		checkNumeric("Sin(0.5)", //
				"0.479425538604203");
		check("Sin(3*Pi)", //
				"0");
		checkNumeric("Sin(1.0 + I)", //
				"1.2984575814159773+I*0.6349639147847361");

		checkNumeric("Sin(1.1*Pi)", //
				"-0.30901699437494773");
		checkNumeric("Sin({-0.5,9.1})", //
				"{-0.479425538604203,0.3190983623493521}");
		checkNumeric("Sin({{0.5,1.1},{6.4,7.5}})", //
				"{{0.479425538604203,0.8912073600614354},\n" + " {0.11654920485049364,0.9379999767747389}}");
		check("Sin({1,2})", //
				"{Sin(1),Sin(2)}");
		check("Sin(z+1/4*Pi)", //
				"Sin(Pi/4+z)");
		check("Sin(z+1/2*Pi)", //
				"Cos(z)");
		check("Sin(z+1/3*Pi)", //
				"Sin(Pi/3+z)");
		check("Sin(Pi)", //
				"0");
		check("Sin(z+Pi)", //
				"-Sin(z)");
		check("Sin(z+42*Pi)", //
				"Sin(z)");
		check("Sin(x+y+z+43*Pi)", //
				"-Sin(x+y+z)");
		check("Sin(z+42*a*Pi)", //
				"Sin(42*a*Pi+z)");
	}

	public void testSinc() {
		check("Sinc(3.5)", //
				"-0.100224");
		check("Sinc(1.0+3.5*I)", //
				"3.41348+I*(-3.00916)");
		check("(2*Sqrt(2))/Pi", //
				"(2*Sqrt(2))/Pi");
		check("2+(-Sqrt(5))/8", //
				"2-Sqrt(5)/8");
		check("Sinc(0)", //
				"1");
		check("Sinc(1/6*Pi)", //
				"Pi/3");
		check("Sinc(1/4*Pi)", //
				"(2*Sqrt(2))/Pi");
		check("Sinc(1/3*Pi)", //
				"3/2*Sqrt(3)/Pi");
		check("Sinc(1/2*Pi)", //
				"2/Pi");
		check("Sinc(Pi)", //
				"0");
		check("Sinc(5/12*Pi)", //
				"3/5*(Sqrt(2)*(1+Sqrt(3)))/Pi");
		check("Sinc(Pi/5)", //
				"(5*Sqrt(5/8-Sqrt(5)/8))/Pi");
		check("Sinc(Pi/12)", //
				"(3*Sqrt(2)*(-1+Sqrt(3)))/Pi");
		check("Sinc(Pi/10)", //
				"5/2*(-1+Sqrt(5))/Pi");
		check("Sinc(2/5*Pi)", //
				"5/2*Sqrt(5/8+Sqrt(5)/8)/Pi");
		check("Sinc(3/10*Pi)", //
				"5/6*(1+Sqrt(5))/Pi");
		check("Sinc(I)", //
				"Sinh(1)");
		check("Sinc(ArcSin(x))", //
				"x/ArcSin(x)");
		check("Sinc(ArcCos(x))", //
				"Sqrt(1-x^2)/ArcCos(x)");
		check("Sinc(ArcTan(x))", //
				"x/(Sqrt(1+x^2)*ArcTan(x))");
		check("Sinc(I*Infinity)", //
				"Infinity");
		check("Sinc(ComplexInfinity)", //
				"Indeterminate");
	}

	public void testSinh() {
		check("Sinh(Pi*I+x)", //
				"-Sinh(x)");
		check("Sinh(10*Pi*I+x)", //
				"Sinh(x)");
		check("Sinh(43*Pi*I+x)", //
				"-Sinh(x)");
		check("Sinh(0)", //
				"0");
		check("Sinh(42*I*Pi)", //
				"0");
		check("Sinh(3/2*I*Pi)", //
				"-I");
		check("Sinh(5/3*Pi*I)", //
				"-I*1/2*Sqrt(3)");

		check("Sinh(Infinity)", //
				"Infinity");
		check("Sinh(ComplexInfinity)", //
				"Indeterminate");
	}

	public void testSinIntegral() {
		check("SinIntegral(-3/4*I*x)", //
				"-I*SinhIntegral(3/4*x)");
		check("SinIntegral(Infinity)", //
				"Pi/2");
		check("SinIntegral(-Infinity)", //
				"-Pi/2");
		check("SinIntegral(I*Infinity)", //
				"I*Infinity");
		check("SinIntegral((-I)*Infinity)", //
				"-I*Infinity");
		check("SinIntegral(I*1/2*x)", //
				"I*SinhIntegral(x/2)");
		checkNumeric("SinIntegral(2.8)", //
				"1.8320965890813214");
	}

	public void testSinhIntegral() {
		check("SinhIntegral(93/13*I*x)", //
				"I*SinIntegral(93/13*x)");
		check("SinIntegral(-x)", //
				"-SinIntegral(x)");
		check("SinhIntegral(Infinity)", //
				"Infinity");
		check("SinhIntegral(-Infinity)", //
				"-Infinity");
		check("SinhIntegral(I*Infinity)", //
				"I*1/2*Pi");
		check("SinhIntegral(-I*Infinity)", //
				"-I*1/2*Pi");
		check("SinhIntegral(I*1/2*x)", //
				"I*SinIntegral(x/2)");
		checkNumeric("SinIntegral(2.8)", //
				"1.8320965890813214");
	}

	public void testSkewness() {
		check("Skewness({1.1, 1.2, 1.4, 2.1, 2.4})", //
				"0.407041");
	}

	public void testSlot() {
		// check("x^2+x", "x+x^2");
		check("(# &)[a, b, c]", //
				"a");
		check("f = If(#1 == 1, 1, #1*#0(#1 - 1)) &", //
				"If(#1==1,1,#1*#0[-1+#1])&");
		check("f(10)", //
				"3628800");
		check("# &[1, 2, 3]", //
				"1");
		check("#1 &[1, 2, 3]", //
				"1");
		check("g(#0) &[x]", //
				"g(g(#0)&)");

		// check("#1^2+#1", "#1^2+#1");
		// check("#1+#1^7", "#1");
		check("#", //
				"#1");
		check("#42", //
				"#42");
	}

	public void testSlotSequence() {
		check("(## &)[a, b, c]", //
				"Sequence(a,b,c)");
		check("(##2 &)[a, b, c]", //
				"Sequence(b,c)");
		check("(##4 &)[a, b, c]", //
				"Sequence()");
		check("(##5 &)[a, b, c]", //
				"##5");
		check("(##-1 &)[a, b, c]", //
				"-1+a+b+c");
		check("(##2-7 &)[a, b, c]", //
				"-7+b+c");
		check("##", //
				"##1");
		check("##42", //
				"##42");
		check("f(x, ##, y, ##) &[a, b, c, d]", //
				"f(x,a,b,c,d,y,a,b,c,d)");
		check("f(##2) &[a, b, c, d]", //
				"f(b,c,d)");
		check("{##2} &[a, b, c]", //
				"{b,c}");
	}

	public void testSokalSneathDissimilarity() {
		check("SokalSneathDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
				"3/4");
		check("SokalSneathDissimilarity({True, False, True}, {True, True, False})", //
				"4/5");
		check("SokalSneathDissimilarity({1, 1, 1, 1}, {1, 1, 1, 1})", //
				"0");
		check("SokalSneathDissimilarity({0, 0, 0, 0}, {1, 1, 1, 1})", //
				"1");
	}

	public void testSolve() {
		check("ReplaceAll(x^3-89, {{x->-(-89)^(1/3)},{x->(-89)^(1/3)*(-1)^(1/3)},{x->-(-89)^(1/3)*(-1)^(2/3)}})", //
				"{0,0,0}");
		check("Solve(x^3-89==0, x)", //
				"{{x->-(-89)^(1/3)},{x->(-89)^(1/3)*(-1)^(1/3)},{x->-(-89)^(1/3)*(-1)^(2/3)}}");

		check("Solve((5.0*x)/y==(0.8*y)/x,x)", //
				"{{x->-0.4*y},{x->0.4*y}}");
		check("Solve(x==0,x)", //
				"{{x->0}}");
		check("Solve(5*y^x==8,x)", //
				"{{x->Log(8/5)/Log(y)}}");
		check("Solve(x^y+8==a*b,x)", //
				"{{x->(-8+a*b)^(1/y)}}");
		check("Solve(x^2==0,x)", //
				"{{x->0}}");
		check("Solve(x^3==0,x)", //
				"{{x->0}}");
		check("Solve(x+1==0,x)", //
				"{{x->-1}}");
		check("Solve(x^2+1==0,x)", //
				"{{x->-I},{x->I}}");
		check("Solve(2*x^2+1==0,x)", //
				"{{x->-I/Sqrt(2)},{x->I/Sqrt(2)}}");
		check("Solve(3*(x+5)*(x-4)==0,x)", //
				"{{x->-5},{x->4}}");
		check("Solve(3*(x+a)*(x-b)==0,x)", //
				"{{x->-a},{x->b}}");
		check("Solve(a*x^2+b==0,x)", //
				"{{x->(-I*Sqrt(b))/Sqrt(a)},{x->(I*Sqrt(b))/Sqrt(a)}}");
		check("Solve(x^2+2*x+1==0,x)", //
				"{{x->-1},{x->-1}}");
		// TODO simplify result
		check("Solve(-5*Sqrt(14)*x-14*x^2*Sqrt(83)-10==0,x)", //
				"{{x->(-5*Sqrt(14)-Sqrt(350-560*Sqrt(83)))/(28*Sqrt(83))},{x->(-5*Sqrt(14)+Sqrt(\n"
						+ "350-560*Sqrt(83)))/(28*Sqrt(83))}}");

		check("Solve(8*x^3-26x^2+3x+9==0,x)", //
				"{{x->-1/2},{x->3/4},{x->3}}");

		check("Solve((a*x^2+1)==0,x)", //
				"{{x->-I/Sqrt(a)},{x->I/Sqrt(a)}}");
		check("Solve(Sqrt(x)-2*x+x^2==0,x)", //
				"{{x->0},{x->1}}");
		check("Solve((2*x+x^2)^2-x==0,x)", //
				"{{x->0},{x->-4/3+(43/2+3/2*Sqrt(177))^(1/3)/3+4/3*2^(1/3)/(43+3*Sqrt(177))^(1/3)},{x->\n"
						+ "-4/3-2/3*2^(1/3)/(43+3*Sqrt(177))^(1/3)+(I*2*2^(1/3))/(Sqrt(3)*(43+3*Sqrt(177))^(\n"
						+ "1/3))-(43+3*Sqrt(177))^(1/3)/(6*2^(1/3))+(-I*1/2*(43+3*Sqrt(177))^(1/3))/(2^(1/3)*Sqrt(\n"
						+ "3))},{x->-4/3-2/3*2^(1/3)/(43+3*Sqrt(177))^(1/3)+(-I*2*2^(1/3))/(Sqrt(3)*(43+3*Sqrt(\n"
						+ "177))^(1/3))-(43+3*Sqrt(177))^(1/3)/(6*2^(1/3))+(I*1/2*(43+3*Sqrt(177))^(1/3))/(\n"
						+ "2^(1/3)*Sqrt(3))}}");
		check("Solve((5*x^4-2)/(x+1)/(x^2-1)==0,x)", //
				"{{x->((-2)^(1/4)*(-1)^(1/4))/5^(1/4)},{x->(-(-2)^(1/4)*(-1)^(1/4))/5^(1/4)},{x->((\n"
						+ "-2)^(1/4)*(-1)^(3/4))/5^(1/4)},{x->(-(-2)^(1/4)*(-1)^(3/4))/5^(1/4)}}");

		check("Solve({x^2-11==y, x+y==-9}, {x,y})", //
				"{{x->-2,y->-7},{x->1,y->-10}}");

		check("Solve(30*x/0.0002==30,{x})", //
				"{{x->0.0002}}");
		check("Solve(30*x/0.000000002==30,x)", //
				"{{x->2.00000*10^-9}}");

		// check("Factor(E^(3*x)-4*E^x+3*E^(-x))", //
		// "((-1+E^x)*(1+E^x)*(-3+E^(2*x)))/E^x");
		check("Solve((-3+E^(2*x))==0,x)", //
				"{{x->ConditionalExpression(I*2*Pi*C(1)+Log(3),Element(C(1),Integers))/2}}");
		check("Solve(E^(3*x)-4*E^x+3*E^(-x)==0,x)", //
				"{{x->ConditionalExpression(I*2*Pi*C(1),Element(C(1),Integers))},{x->ConditionalExpression(I*Pi+\n"
						+ "I*2*Pi*C(1),Element(C(1),Integers))},{x->ConditionalExpression(I*2*Pi*C(1)+Log(3),Element(C(\n"
						+ "1),Integers))/2}}");
		check("NSolve(E^(3*x)-4*E^x+3*E^(-x)==0,x)", //
				"{{x->0.5*ConditionalExpression(1.09861+(I*6.28319)*C(1.0),Element(C(1),Integers))},{x->ConditionalExpression(I*3.14159+(I*6.28319)*C(1.0),Element(C(\n"
						+ "1),Integers))},{x->ConditionalExpression((I*6.28319)*C(1.0),Element(C(1),Integers))}}");

		check("Solve(1+E^x==0,x)", //
				"{{x->ConditionalExpression(I*Pi+I*2*Pi*C(1),Element(C(1),Integers))}}");
		check("Solve(a+E^(b*x)==0,x)", //
				"{{x->ConditionalExpression(I*2*Pi*C(1)+Log(-a),Element(C(1),Integers))/b}}");

		check("Solve(E^x==b,x)", //
				"{{x->ConditionalExpression(I*2*Pi*C(1)+Log(b),Element(C(1),Integers))}}");
		check("Solve(a^x==42,x)", //
				"{{x->Log(42)/Log(a)}}");
		// check("Solve(2+(-I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))+(I*3*(E^(-I*3*x)-E^(I*3*x)))/(E^(-I*3*x)+E^(I*3*x))==0,x)",
		// //
		// "((2-I*2)*(I+(-1/2-I*1/2)*E^(I*2*x)+E^(I*4*x)))/((-1-I*E^(I*x)+E^(I*2*x))*(-1+I*E^(I*x)+E^(\n"
		// + "I*2*x)))");

		// check("Solve(4^(2*x+1)*5^(x-2)-6^(1-x)==0,x)", //
		// "");
		check("Solve(Log(2,x)+4*Log(x,2)-5==0,x)", //
				"{{x->2},{x->16}}");
		check("Solve(x^(1/Log(2))-1==0,x)", //
				"{{x->1}}");
		check("Solve(Log((x-1)*(x+1))==0,x)", //
				"{{x->-Sqrt(2)},{x->Sqrt(2)}}");
		check("{Re @ #, Im @ #} & /@ Last @@@ Solve(x^3 + 3 == 0, x)", //
				"{{3^(1/3)/2,3^(5/6)/2},{-3^(1/3),0},{3^(1/3)/2,-3^(5/6)/2}}");
		check("Solve((x^2 + 2)*(x^2 - 2) == 0, x, Reals)", //
				"{{x->-Sqrt(2)},{x->Sqrt(2)}}");
		// github #117
		check("Solve({x+y^2==9.1, y==2*x+2}, {x,y})", //
				"{{x->-2.71893,y->-3.43787},{x->0.468934,y->2.93787}}");

		check("Solve(-28 - 4*Sqrt(-1 + x) + 4*x==0,x)", //
				"{{x->10}}");
		check("Solve(Sqrt(5*x-25)-Sqrt(x-1)==2,x)", //
				"{{x->10}}");

		check("Solve(Sqrt(x+6)-Sqrt(x-1)==1,x)", //
				"{{x->10}}");

		check("Solve(Sin((x+1)*(x-1))==2,x)", //
				"{{x->-Sqrt(1+ArcSin(2))},{x->Sqrt(1+ArcSin(2))}}");//
		check("Solve(Log((x+1)*(x-1))==2,x)", //
				"{{x->-Sqrt(1+E^2)},{x->Sqrt(1+E^2)}}");
		check("Solve(Log(x^2-1)==3,x)", //
				"{{x->-Sqrt(1+E^3)},{x->Sqrt(1+E^3)}}");

		check("Solve(a^x==b,x)", //
				"{{x->Log(b)/Log(a)}}");

		checkNumeric("Eliminate(Abs(x-1)==(-1),x)", //
				"True");
		checkNumeric("Solve(Abs(x-1)==(-1),x)", //
				"{}");
		checkNumeric("Solve(Abs(x-1)==1,x)", //
				"{{x->0},{x->2}}");

		checkNumeric("Solve(30*x/0.000002==30,x)", //
				"{{x->2.0E-6}}");
		check("Solve((a*x + b)/(c*x + d)==y,x)", //
				"{{x->-(b-d*y)/(a-c*y)}}");

		check("E^((Log(a)+Log(b))/m)", //
				"E^((Log(a)+Log(b))/m)");
		check("Solve(a0*x^p+a1*x^q==0,x)", //
				"{{x->E^((-I*Pi+Log(a0)-Log(a1))/(-p+q))}}");

		check("Solve(a*x^2+b*x==0, x)", //
				"{{x->0},{x->-b/a}}");

		check("Solve({Cos(x)*x==0, x > 10}, x)", //
				"{{x->0},{x->Pi/2}}");
		// TODO select a better starting value for internally used FindRoot:
		check("NSolve({Cos(x)*x==0, x > 10}, x)", //
				"{{x->0.0},{x->1.5708}}");

		check("Solve({Cos(x)*x==0, x==0}, x)", //
				"{{x->0}}");
		check("Solve({Cos(x)*x==0, x < 10}, x)", //
				"{{x->0},{x->Pi/2}}");

		// check("Solve((x^4 - 1)*(x^4 - 4) == 0, x, Integers)", "");
		check("Solve(x == x, x)", //
				"{{}}");
		// check("Solve(x == 1 && x == 2, x)", //
		// "{}");

		check("Solve((5.0*x)/y==(0.8*y)/x,x)", //
				"{{x->-0.4*y},{x->0.4*y}}");

		// gh issue #2
		check("Solve(x^2+y^2==5,x)", //
				"{{x->-Sqrt(5-y^2)},{x->Sqrt(5-y^2)}}");

		// check("x=20.796855124168776", "20.79686");
		// check("Clear(x);Solve(x==(-1.0000000000000002)*Sqrt(y^2.0),y)",
		// "{{y->1.0*Sqrt(x^2.0)}}");

		// Issue #175
		check("Solve(Sqrt(-16.0+a^2.0)/(20.0-2.0*92)==0.5,a)", //
				"{}");

		// Issue #166
		check("Solve(2*x/y==x/z,x)", //
				"{{x->0}}");
		// Issue #165
		check("Solve((3.0*y)/x==(1.5*y)/z,x)", //
				"{{x->2.0*z}}");
		// Issue #162
		check("Solve((5.0*x)/y==(0.8*y)/x,x)", //
				"{{x->-0.4*y},{x->0.4*y}}");
		// Issue #161
		checkNumeric("Solve((0.6000000000000001*2.5)/y==z/x,x)", //
				"{{x->0.6666666666666665*y*z}}");
		// Issue #160
		checkNumeric("Solve((2.10937501*y)/(0.6923076944378698*z)==(0.6923076944378698*z)/x,x)", //
				"{{x->(0.2272189352323269*z^2.0)/y}}");
		// Issue #159
		check("Solve(x==2*Sqrt(y)*Sqrt(z),y)", //
				"{{y->x^2/(4*z)}}");
		check("Solve(x==2.0*Sqrt(y)*Sqrt(z),y)", //
				"{{y->(0.25*x^2.0)/z}}");

		// Issue #155
		check("Solve(x==2*Sqrt(y)*Sqrt(z),y)", //
				"{{y->x^2/(4*z)}}");

		// Issue #151
		check("Solve(60+abc==120.0,abc)", //
				"{{abc->60.0}}");

		// Issue #152
		checkNumeric("Solve(Sqrt(x)==16.1,x)", //
				"{{x->259.21}}");

		// TODO check type of result in Solve()
		// check("Solve(x^3 == 1, x, Reals)", "{{x->1}}");

		check("Solve(x+5.0==a,x)", //
				"{{x->-5.0+a}}");

		checkNumeric("Solve(-8828.206-582.222*b+55.999*b^2.0+4.8*b^3.0==0, b)", //
				"{{b->11.805307105741173},{b->-11.735882719537255+I*(-4.250200714726687)},{b->-11.735882719537255+I*4.250200714726687}}");
		// check("Solve(Abs((-3+x^2)/x) ==2,{x})",
		// "{{x->-3},{x->-1},{x->1},{x->3}}");
		check("Solve(x^3==-2,x)", //
				"{{x->(-2)^(1/3)},{x->-2^(1/3)},{x->-(-1)^(2/3)*2^(1/3)}}");

		check("Solve(1 - (i*1)/10 == 0, i, Integers)", //
				"{{i->10}}");
		check("Solve({x^2 + 2*y^3 == 3681, x > 0, y > 0}, {x, y}, Integers)", //
				"{{x->15,y->12},{x->41,y->10},{x->57,y->6}}");
		check("Solve({x>=0,y>=0,x+y==7,2*x+4*y==20},{x,y}, Integers)", //
				"{{x->4,y->3}}");
		check("Solve(x>=0 && y>=0 && x+y==7 && 2*x+4*y==20,{x,y}, Integers)", //
				"{{x->4,y->3}}");
		check("Solve({2*x + 3*y == 4, 3*x - 4*y <= 5,x - 2*y > -21}, {x,  y}, Integers)", //
				"{{x->-7,y->6},{x->-4,y->4},{x->-1,y->2}}");

		// timeouts in Cream engine
		// check("Solve({x^2 + x y + y^2 == 109}, {x, y}, Integers)", "");
		// check("Solve({x^12345 - 2 x^777 + 1 == 0}, {x}, Integers)", "");
		// check("Solve({2 x + 3 y - 5 z == 1 , 3 x - 4 y + 7 z == 3}, {x,
		// y, z}, Integers)", "");

		check("Solve((k*Q*q)/r^2+1/r^4==E,r)", //
				"{{r->Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(\n"
						+ "4*E+k^2*q^2*Q^2))},{r->Sqrt(1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(\n"
						+ "1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))}}");
		// issue #120
		check("Solve(Sin(x)*x==0, x)", //
				"{{x->0}}");
		check("Solve(Cos(x)*x==0, x)", //
				"{{x->0},{x->Pi/2}}");
		// issue #121
		check("Solve(Sqrt(x)==-1, x)", //
				"{}");
		check("Solve(x^2+1==0, x)", //
				"{{x->-I},{x->I}}");
		check("Solve((k*Q*q)/r^2==E,r)", //
				"{{r->Sqrt(k*q*Q)/Sqrt(E)},{r->-Sqrt(k*q*Q)/Sqrt(E)}}");
		check("Solve((k*Q*q)/r^2+1/r^4==E,r)", //
				"{{r->Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(1/(2*E))*Sqrt(k*q*Q-Sqrt(\n"
						+ "4*E+k^2*q^2*Q^2))},{r->Sqrt(1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))},{r->-Sqrt(\n"
						+ "1/(2*E))*Sqrt(k*q*Q+Sqrt(4*E+k^2*q^2*Q^2))}}");
		check("Solve((k*Q*q)/r^2+1/r^4==0,r)", //
				"{{r->-I/Sqrt(k*q*Q)},{r->I/Sqrt(k*q*Q)}}");
		check("Solve(Abs(x-1) ==1,{x})", //
				"{{x->0},{x->2}}");
		check("Solve(Abs(x^2-1) ==0,{x})", //
				"{{x->-1},{x->1}}");
		check("Solve(Xor(a, b, c, d) && (a || b) && ! (c || d), {a, b, c, d}, Booleans)", //
				"{{a->True,b->False,c->False,d->False},{a->False,b->True,c->False,d->False}}");
		check("Solve({x^2-11==y, x+y==-9}, {x,y})", //
				"{{x->-2,y->-7},{x->1,y->-10}}");

		// issue 42
		// check("$sol=Solve(x^3 + 2x^2 - 5x -3 ==0,x);N($sol)",
		// "{{x->-3.2534180395878516},{x->-0.5199693720627901},{x->1.773387411650642}}");

		// check("Solve(x^3 + 2x^2 - 5x -3 ==0, x)",
		// "{{x->(-1/3)*((1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(1/2)^(1/3)*(-I*9*331^(1/2)+25)^(\n"
		// +
		// "1/3)+2)},{x->(-1/3)*((-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(I*\n"
		// +
		// "1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(-I*9*331^(1/2)+25)^(1/3)+2)},{x->(-1/3)*((I*1/2*3^(\n"
		// +
		// "1/2)-1/2)*(1/2)^(1/3)*(I*9*331^(1/2)+25)^(1/3)+(-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(-I*\n"
		// + "9*331^(1/2)+25)^(1/3)+2)}}");
		check("Solve(2*Sin(x)==1/2,x)", //
				"{{x->ArcSin(1/4)}}");
		check("Solve(3+2*Cos(x)==1/2,x)", //
				"{{x->ArcCos(-5/4)}}");
		check("Solve(Sin(x)==0,x)", //
				"{{x->0}}");
		check("Solve(Sin(x)==0.0,x)", //
				"{{x->0}}");
		check("Solve(Sin(x)==1/2,x)", //
				"{{x->Pi/6}}");
		checkNumeric("Solve(sin(x)==0.5,x)", //
				"{{x->0.5235987755982988}}");
		check("Solve(x^2-2500.00==0,x)", //
				"{{x->-50.0},{x->50.0}}");
		check("Solve(x^2+a*x+1 == 0, x)", //
				"{{x->1/2*(-a-Sqrt(-4+a^2))},{x->1/2*(-a+Sqrt(-4+a^2))}}");
		check("Solve((-3)*x^3 +10*x^2-11*x == (-4), {x})", //
				"{{x->1},{x->4/3}}");

		checkNumeric("Solve(x^2+50*x-2500.00==0,x)", //
				"{{x->-80.90169943749474},{x->30.90169943749474}}");

		check("Solve(a*x + y == 7 && b*x - y == 1, {x, y})", //
				"{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");
		check("Solve({a*x + y == 7, b*x - y == 1}, {x, y})", //
				"{{x->-8/(-a-b),y->(a-7*b)/(-a-b)}}");

		check("Solve(-Infinity==(2*a2)/a3+(-2*a5)/a3,a3)", //
				"Solve(-Infinity==(2*a2)/a3+(-2*a5)/a3,a3)");

		// Issue #168
		checkNumeric("y=297.0004444386505", //
				"297.0004444386505");
		checkNumeric("z=22.13904248493947", // 7
				"22.13904248493947");
		checkNumeric("Solve(x/y==z/x,x)", //
				"{{x->-81.08825721072822},{x->81.08825721072822}}");
	}

	public void testSolveIssue130() {
		check("Sqrt(1-x)+Sqrt(3+x)", //
				"Sqrt(1-x)+Sqrt(3+x)");
		check("Sqrt(-1)*(-1)^(1/10)", //
				"(-1)^(3/5)");
		check("-1*Sqrt(-1)*(-1)^(1/10)", //
				"-(-1)^(3/5)");
		check("Solve(x^10-1==0,x)", //
				"{{x->-1},{x->1},{x->-(-1)^(1/5)},{x->(-1)^(1/5)},{x->-(-1)^(2/5)},{x->(-1)^(2/5)},{x->-(\n"
						+ "-1)^(3/5)},{x->(-1)^(3/5)},{x->-(-1)^(4/5)},{x->(-1)^(4/5)}}");

		// check("Trace(Solve(x^6-b==0,x))",
		// "???");
		check("Solve(x^4+b==0,x)", //
				"{{x->(-1)^(1/4)*b^(1/4)},{x->-(-1)^(1/4)*b^(1/4)},{x->(-1)^(3/4)*b^(1/4)},{x->-(\n"
						+ "-1)^(3/4)*b^(1/4)}}");
		check("Solve(x^4-b==0,x)", //
				"{{x->(-1)^(1/4)*(-b)^(1/4)},{x->-(-1)^(1/4)*(-b)^(1/4)},{x->(-1)^(3/4)*(-b)^(1/4)},{x->-(\n"
						+ "-1)^(3/4)*(-b)^(1/4)}}");
		check("Solve(x^8+b==0,x)", //
				"{{x->(-1)^(1/8)*b^(1/8)},{x->-(-1)^(1/8)*b^(1/8)},{x->(-1)^(3/8)*b^(1/8)},{x->-(\n"
						+ "-1)^(3/8)*b^(1/8)},{x->(-1)^(5/8)*b^(1/8)},{x->-(-1)^(5/8)*b^(1/8)},{x->(-1)^(7/\n"
						+ "8)*b^(1/8)},{x->-(-1)^(7/8)*b^(1/8)}}");
		check("Solve(x^8-b==0,x)", //
				"{{x->(-1)^(1/8)*(-b)^(1/8)},{x->-(-1)^(1/8)*(-b)^(1/8)},{x->(-1)^(3/8)*(-b)^(1/8)},{x->-(\n"
						+ "-1)^(3/8)*(-b)^(1/8)},{x->(-1)^(5/8)*(-b)^(1/8)},{x->-(-1)^(5/8)*(-b)^(1/8)},{x->(\n"
						+ "-1)^(7/8)*(-b)^(1/8)},{x->-(-1)^(7/8)*(-b)^(1/8)}}");
		check("Solve(x^10+b==0,x)", "{{x->-I*b^(1/10)},{x->I*b^(1/10)},{x->(-1)^(1/10)*b^(1/10)},{x->-(-1)^(1/10)*b^(\n"
				+ "1/10)},{x->(-1)^(3/10)*b^(1/10)},{x->-(-1)^(3/10)*b^(1/10)},{x->(-1)^(7/10)*b^(1/\n"
				+ "10)},{x->-(-1)^(7/10)*b^(1/10)},{x->(-1)^(9/10)*b^(1/10)},{x->-(-1)^(9/10)*b^(1/\n" + "10)}}");
		check("Solve(x^10-b==0,x)", //
				"{{x->-I*(-b)^(1/10)},{x->I*(-b)^(1/10)},{x->(-1)^(1/10)*(-b)^(1/10)},{x->-(-1)^(\n"
						+ "1/10)*(-b)^(1/10)},{x->(-1)^(3/10)*(-b)^(1/10)},{x->-(-1)^(3/10)*(-b)^(1/10)},{x->(\n"
						+ "-1)^(7/10)*(-b)^(1/10)},{x->-(-1)^(7/10)*(-b)^(1/10)},{x->(-1)^(9/10)*(-b)^(1/10)},{x->-(\n"
						+ "-1)^(9/10)*(-b)^(1/10)}}");
		check("Solve(x^6+b==0,x)", //
				"{{x->-I*b^(1/6)},{x->I*b^(1/6)},{x->(-1)^(1/6)*b^(1/6)},{x->-(-1)^(1/6)*b^(1/6)},{x->(\n"
						+ "-1)^(5/6)*b^(1/6)},{x->-(-1)^(5/6)*b^(1/6)}}");
		check("Solve(x^6-b==0,x)",
				"{{x->-I*(-b)^(1/6)},{x->I*(-b)^(1/6)},{x->(-1)^(1/6)*(-b)^(1/6)},{x->-(-1)^(1/6)*(-b)^(\n"
						+ "1/6)},{x->(-1)^(5/6)*(-b)^(1/6)},{x->-(-1)^(5/6)*(-b)^(1/6)}}");
		check("Solve(x^258==1,x)",
				"{{x->-1},{x->1},{x->-(-1)^(1/129)},{x->(-1)^(1/129)},{x->-(-1)^(2/129)},{x->(-1)^(\n"
						+ "2/129)},{x->-(-1)^(1/43)},{x->(-1)^(1/43)},{x->-(-1)^(4/129)},{x->(-1)^(4/129)},{x->-(\n"
						+ "-1)^(5/129)},{x->(-1)^(5/129)},{x->-(-1)^(2/43)},{x->(-1)^(2/43)},{x->-(-1)^(7/\n"
						+ "129)},{x->(-1)^(7/129)},{x->-(-1)^(8/129)},{x->(-1)^(8/129)},{x->-(-1)^(3/43)},{x->(\n"
						+ "-1)^(3/43)},{x->-(-1)^(10/129)},{x->(-1)^(10/129)},{x->-(-1)^(11/129)},{x->(-1)^(\n"
						+ "11/129)},{x->-(-1)^(4/43)},{x->(-1)^(4/43)},{x->-(-1)^(13/129)},{x->(-1)^(13/129)},{x->-(\n"
						+ "-1)^(14/129)},{x->(-1)^(14/129)},{x->-(-1)^(5/43)},{x->(-1)^(5/43)},{x->-(-1)^(\n"
						+ "16/129)},{x->(-1)^(16/129)},{x->-(-1)^(17/129)},{x->(-1)^(17/129)},{x->-(-1)^(6/\n"
						+ "43)},{x->(-1)^(6/43)},{x->-(-1)^(19/129)},{x->(-1)^(19/129)},{x->-(-1)^(20/129)},{x->(\n"
						+ "-1)^(20/129)},{x->-(-1)^(7/43)},{x->(-1)^(7/43)},{x->-(-1)^(22/129)},{x->(-1)^(\n"
						+ "22/129)},{x->-(-1)^(23/129)},{x->(-1)^(23/129)},{x->-(-1)^(8/43)},{x->(-1)^(8/43)},{x->-(\n"
						+ "-1)^(25/129)},{x->(-1)^(25/129)},{x->-(-1)^(26/129)},{x->(-1)^(26/129)},{x->-(-1)^(\n"
						+ "9/43)},{x->(-1)^(9/43)},{x->-(-1)^(28/129)},{x->(-1)^(28/129)},{x->-(-1)^(29/129)},{x->(\n"
						+ "-1)^(29/129)},{x->-(-1)^(10/43)},{x->(-1)^(10/43)},{x->-(-1)^(31/129)},{x->(-1)^(\n"
						+ "31/129)},{x->-(-1)^(32/129)},{x->(-1)^(32/129)},{x->-(-1)^(11/43)},{x->(-1)^(11/\n"
						+ "43)},{x->-(-1)^(34/129)},{x->(-1)^(34/129)},{x->-(-1)^(35/129)},{x->(-1)^(35/129)},{x->-(\n"
						+ "-1)^(12/43)},{x->(-1)^(12/43)},{x->-(-1)^(37/129)},{x->(-1)^(37/129)},{x->-(-1)^(\n"
						+ "38/129)},{x->(-1)^(38/129)},{x->-(-1)^(13/43)},{x->(-1)^(13/43)},{x->-(-1)^(40/\n"
						+ "129)},{x->(-1)^(40/129)},{x->-(-1)^(41/129)},{x->(-1)^(41/129)},{x->-(-1)^(14/43)},{x->(\n"
						+ "-1)^(14/43)},{x->-(-1)^(1/3)},{x->(-1)^(1/3)},{x->-(-1)^(44/129)},{x->(-1)^(44/\n"
						+ "129)},{x->-(-1)^(15/43)},{x->(-1)^(15/43)},{x->-(-1)^(46/129)},{x->(-1)^(46/129)},{x->-(\n"
						+ "-1)^(47/129)},{x->(-1)^(47/129)},{x->-(-1)^(16/43)},{x->(-1)^(16/43)},{x->-(-1)^(\n"
						+ "49/129)},{x->(-1)^(49/129)},{x->-(-1)^(50/129)},{x->(-1)^(50/129)},{x->-(-1)^(17/\n"
						+ "43)},{x->(-1)^(17/43)},{x->-(-1)^(52/129)},{x->(-1)^(52/129)},{x->-(-1)^(53/129)},{x->(\n"
						+ "-1)^(53/129)},{x->-(-1)^(18/43)},{x->(-1)^(18/43)},{x->-(-1)^(55/129)},{x->(-1)^(\n"
						+ "55/129)},{x->-(-1)^(56/129)},{x->(-1)^(56/129)},{x->-(-1)^(19/43)},{x->(-1)^(19/\n"
						+ "43)},{x->-(-1)^(58/129)},{x->(-1)^(58/129)},{x->-(-1)^(59/129)},{x->(-1)^(59/129)},{x->-(\n"
						+ "-1)^(20/43)},{x->(-1)^(20/43)},{x->-(-1)^(61/129)},{x->(-1)^(61/129)},{x->-(-1)^(\n"
						+ "62/129)},{x->(-1)^(62/129)},{x->-(-1)^(21/43)},{x->(-1)^(21/43)},{x->-(-1)^(64/\n"
						+ "129)},{x->(-1)^(64/129)},{x->-(-1)^(65/129)},{x->(-1)^(65/129)},{x->-(-1)^(22/43)},{x->(\n"
						+ "-1)^(22/43)},{x->-(-1)^(67/129)},{x->(-1)^(67/129)},{x->-(-1)^(68/129)},{x->(-1)^(\n"
						+ "68/129)},{x->-(-1)^(23/43)},{x->(-1)^(23/43)},{x->-(-1)^(70/129)},{x->(-1)^(70/\n"
						+ "129)},{x->-(-1)^(71/129)},{x->(-1)^(71/129)},{x->-(-1)^(24/43)},{x->(-1)^(24/43)},{x->-(\n"
						+ "-1)^(73/129)},{x->(-1)^(73/129)},{x->-(-1)^(74/129)},{x->(-1)^(74/129)},{x->-(-1)^(\n"
						+ "25/43)},{x->(-1)^(25/43)},{x->-(-1)^(76/129)},{x->(-1)^(76/129)},{x->-(-1)^(77/\n"
						+ "129)},{x->(-1)^(77/129)},{x->-(-1)^(26/43)},{x->(-1)^(26/43)},{x->-(-1)^(79/129)},{x->(\n"
						+ "-1)^(79/129)},{x->-(-1)^(80/129)},{x->(-1)^(80/129)},{x->-(-1)^(27/43)},{x->(-1)^(\n"
						+ "27/43)},{x->-(-1)^(82/129)},{x->(-1)^(82/129)},{x->-(-1)^(83/129)},{x->(-1)^(83/\n"
						+ "129)},{x->-(-1)^(28/43)},{x->(-1)^(28/43)},{x->-(-1)^(85/129)},{x->(-1)^(85/129)},{x->-(\n"
						+ "-1)^(2/3)},{x->(-1)^(2/3)},{x->-(-1)^(29/43)},{x->(-1)^(29/43)},{x->-(-1)^(88/\n"
						+ "129)},{x->(-1)^(88/129)},{x->-(-1)^(89/129)},{x->(-1)^(89/129)},{x->-(-1)^(30/43)},{x->(\n"
						+ "-1)^(30/43)},{x->-(-1)^(91/129)},{x->(-1)^(91/129)},{x->-(-1)^(92/129)},{x->(-1)^(\n"
						+ "92/129)},{x->-(-1)^(31/43)},{x->(-1)^(31/43)},{x->-(-1)^(94/129)},{x->(-1)^(94/\n"
						+ "129)},{x->-(-1)^(95/129)},{x->(-1)^(95/129)},{x->-(-1)^(32/43)},{x->(-1)^(32/43)},{x->-(\n"
						+ "-1)^(97/129)},{x->(-1)^(97/129)},{x->-(-1)^(98/129)},{x->(-1)^(98/129)},{x->-(-1)^(\n"
						+ "33/43)},{x->(-1)^(33/43)},{x->-(-1)^(100/129)},{x->(-1)^(100/129)},{x->-(-1)^(\n"
						+ "101/129)},{x->(-1)^(101/129)},{x->-(-1)^(34/43)},{x->(-1)^(34/43)},{x->-(-1)^(\n"
						+ "103/129)},{x->(-1)^(103/129)},{x->-(-1)^(104/129)},{x->(-1)^(104/129)},{x->-(-1)^(\n"
						+ "35/43)},{x->(-1)^(35/43)},{x->-(-1)^(106/129)},{x->(-1)^(106/129)},{x->-(-1)^(\n"
						+ "107/129)},{x->(-1)^(107/129)},{x->-(-1)^(36/43)},{x->(-1)^(36/43)},{x->-(-1)^(\n"
						+ "109/129)},{x->(-1)^(109/129)},{x->-(-1)^(110/129)},{x->(-1)^(110/129)},{x->-(-1)^(\n"
						+ "37/43)},{x->(-1)^(37/43)},{x->-(-1)^(112/129)},{x->(-1)^(112/129)},{x->-(-1)^(\n"
						+ "113/129)},{x->(-1)^(113/129)},{x->-(-1)^(38/43)},{x->(-1)^(38/43)},{x->-(-1)^(\n"
						+ "115/129)},{x->(-1)^(115/129)},{x->-(-1)^(116/129)},{x->(-1)^(116/129)},{x->-(-1)^(\n"
						+ "39/43)},{x->(-1)^(39/43)},{x->-(-1)^(118/129)},{x->(-1)^(118/129)},{x->-(-1)^(\n"
						+ "119/129)},{x->(-1)^(119/129)},{x->-(-1)^(40/43)},{x->(-1)^(40/43)},{x->-(-1)^(\n"
						+ "121/129)},{x->(-1)^(121/129)},{x->-(-1)^(122/129)},{x->(-1)^(122/129)},{x->-(-1)^(\n"
						+ "41/43)},{x->(-1)^(41/43)},{x->-(-1)^(124/129)},{x->(-1)^(124/129)},{x->-(-1)^(\n"
						+ "125/129)},{x->(-1)^(125/129)},{x->-(-1)^(42/43)},{x->(-1)^(42/43)},{x->-(-1)^(\n"
						+ "127/129)},{x->(-1)^(127/129)},{x->-(-1)^(128/129)},{x->(-1)^(128/129)}}");
		check("Solve(a*x^8+b==0,x)",
				"{{x->((-1)^(1/8)*b^(1/8))/a^(1/8)},{x->(-(-1)^(1/8)*b^(1/8))/a^(1/8)},{x->((-1)^(\n"
						+ "3/8)*b^(1/8))/a^(1/8)},{x->(-(-1)^(3/8)*b^(1/8))/a^(1/8)},{x->((-1)^(5/8)*b^(1/8))/a^(\n"
						+ "1/8)},{x->(-(-1)^(5/8)*b^(1/8))/a^(1/8)},{x->((-1)^(7/8)*b^(1/8))/a^(1/8)},{x->(-(\n"
						+ "-1)^(7/8)*b^(1/8))/a^(1/8)}}");
		check("Solve(a*x^10+b==0,x)",
				"{{x->(-I*b^(1/10))/a^(1/10)},{x->(I*b^(1/10))/a^(1/10)},{x->((-1)^(1/10)*b^(1/10))/a^(\n"
						+ "1/10)},{x->(-(-1)^(1/10)*b^(1/10))/a^(1/10)},{x->((-1)^(3/10)*b^(1/10))/a^(1/10)},{x->(-(\n"
						+ "-1)^(3/10)*b^(1/10))/a^(1/10)},{x->((-1)^(7/10)*b^(1/10))/a^(1/10)},{x->(-(-1)^(\n"
						+ "7/10)*b^(1/10))/a^(1/10)},{x->((-1)^(9/10)*b^(1/10))/a^(1/10)},{x->(-(-1)^(9/10)*b^(\n"
						+ "1/10))/a^(1/10)}}");

		check("Solve(x^10-1==0,x)",
				"{{x->-1},{x->1},{x->-(-1)^(1/5)},{x->(-1)^(1/5)},{x->-(-1)^(2/5)},{x->(-1)^(2/5)},{x->-(\n"
						+ "-1)^(3/5)},{x->(-1)^(3/5)},{x->-(-1)^(4/5)},{x->(-1)^(4/5)}}");

		check("Solve(a*x^3+b==0,x)",
				"{{x->-b^(1/3)/a^(1/3)},{x->((-1)^(1/3)*b^(1/3))/a^(1/3)},{x->(-(-1)^(2/3)*b^(1/3))/a^(\n" + "1/3)}}");
		check("Solve(a*x^5+b==0,x)",
				"{{x->-b^(1/5)/a^(1/5)},{x->((-1)^(1/5)*b^(1/5))/a^(1/5)},{x->(-(-1)^(2/5)*b^(1/5))/a^(\n"
						+ "1/5)},{x->((-1)^(3/5)*b^(1/5))/a^(1/5)},{x->(-(-1)^(4/5)*b^(1/5))/a^(1/5)}}");
		check("Solve(a*x^5-b==0,x)",
				"{{x->-(-b)^(1/5)/a^(1/5)},{x->((-1)^(1/5)*(-b)^(1/5))/a^(1/5)},{x->(-(-1)^(2/5)*(-b)^(\n"
						+ "1/5))/a^(1/5)},{x->((-1)^(3/5)*(-b)^(1/5))/a^(1/5)},{x->(-(-1)^(4/5)*(-b)^(1/5))/a^(\n"
						+ "1/5)}}");
		check("Solve(a*x^11-b==0,x)",
				"{{x->-(-b)^(1/11)/a^(1/11)},{x->((-1)^(1/11)*(-b)^(1/11))/a^(1/11)},{x->(-(-1)^(\n"
						+ "2/11)*(-b)^(1/11))/a^(1/11)},{x->((-1)^(3/11)*(-b)^(1/11))/a^(1/11)},{x->(-(-1)^(\n"
						+ "4/11)*(-b)^(1/11))/a^(1/11)},{x->((-1)^(5/11)*(-b)^(1/11))/a^(1/11)},{x->(-(-1)^(\n"
						+ "6/11)*(-b)^(1/11))/a^(1/11)},{x->((-1)^(7/11)*(-b)^(1/11))/a^(1/11)},{x->(-(-1)^(\n"
						+ "8/11)*(-b)^(1/11))/a^(1/11)},{x->((-1)^(9/11)*(-b)^(1/11))/a^(1/11)},{x->(-(-1)^(\n"
						+ "10/11)*(-b)^(1/11))/a^(1/11)}}");

		check("Solve(y==x+((1)/(x)),y)", //
				"{{y->-(-1-x^2)/x}}");
		check("Solve(y==((1-x)^(1/(2)))+((x+3)^(1/(2))),y)", //
				"{{y->Sqrt(1-x)+Sqrt(3+x)}}");

		check("Solve(x^24==1,x)",
				"{{x->-1},{x->1},{x->-I},{x->I},{x->-(-1)^(1/12)},{x->(-1)^(1/12)},{x->-(-1)^(1/6)},{x->(\n"
						+ "-1)^(1/6)},{x->-(-1)^(1/4)},{x->(-1)^(1/4)},{x->-(-1)^(1/3)},{x->(-1)^(1/3)},{x->-(\n"
						+ "-1)^(5/12)},{x->(-1)^(5/12)},{x->-(-1)^(7/12)},{x->(-1)^(7/12)},{x->-(-1)^(2/3)},{x->(\n"
						+ "-1)^(2/3)},{x->-(-1)^(3/4)},{x->(-1)^(3/4)},{x->-(-1)^(5/6)},{x->(-1)^(5/6)},{x->-(\n"
						+ "-1)^(11/12)},{x->(-1)^(11/12)}}");
	}

	public void testSort() {
		check("Sort(StringJoin /@ Tuples({\"a\",\"A\",\"b\",\"B\"},2))", //
				"{aa,aA,Aa,AA,ab,aB,Ab,AB,ba,bA,Ba,BA,bb,bB,Bb,BB}");
		check("Sort({a,A,a,b,B})", //
				"{a,a,A,b,B}");
		check("Sort@{ 1+x^2,2+x^2,x(x),-x,x,Cos(x^2),Sin(x^2)}", //
				"{-x,x,1+x^2,2+x^2,Cos(x^2),Sin(x^2),x(x)}");
		check("Sort@{x,1+x^2,1+x^2+y^3+z^4,Cos(x^2),Sin(x^2)}", //
				"{x,1+x^2,1+x^2+y^3+z^4,Cos(x^2),Sin(x^2)}");
		check("Sort({E,a,D,d,N,b,c, Adele, enigma})", //
				"{a,adele,b,c,d,D,E,enigma,N}");
		check("Sort({d, b, c, a})", //
				"{a,b,c,d}");
		check("Sort({4, 1, 3, 2, 2}, Greater)", //
				"{4,3,2,2,1}");
		check("Sort({4, 1, 3, 2, 2}, #1 > #2 &)", //
				"{4,3,2,2,1}");
		check("Sort({{a, 2}, {c, 1}, {d, 3}}, #1[[2]] < #2[[2]] &)", //
				"{{c,1},{a,2},{d,3}}");
		check("Sort({4, 1.0, a, 3+I})", //
				"{1.0,4,3+I,a}");
	}

	public void testSortBy() {
		check("SortBy({{5, 1}, {10, -1}}, Last)", //
				"{{10,-1},{5,1}}");
		check("SortBy(Total)[{{5, 1}, {10, -9}}]", //
				"{{10,-9},{5,1}}");
	}

	public void testSow() {
		check("Reap(Sow(a); b; Sow(c); Sow(d); e)", //
				"{e,{{a,c,d}}}");
		check("Reap(Sum(Sow(i0^2) + 1, {i0, 10}))", //
				"{395,{{1,4,9,16,25,36,49,64,81,100}}}");
	}

	public void testSphericalBesselJ() {
		checkNumeric("SphericalBesselJ(1,5.2)", //
				"-0.12277149950007821");
		checkNumeric("BesselJ(2.5,-5)", //
				"I*0.2403772011113174");
		checkNumeric("SphericalBesselJ(2.0,-5)", //
				"0.1347312100888303");
		checkNumeric("SphericalBesselJ(-0.5,1)", //
				"0.9590330784042144");
		checkNumeric("SphericalBesselJ(2.0+I,5.0+I)", //
				"0.1416392453449182+I*0.0050700991107376255");
	}

	public void testSphericalBesselY() {
		checkNumeric("SphericalBesselY(1,5.5)", //
				"0.10485295921804552");
		checkNumeric("BesselY(2.5,-5)", //
				"I*(-0.2943723749617645)");

		checkNumeric("SphericalBesselY(-0.5,1)", //
				"0.1106137009979505");
		checkNumeric("SphericalBesselY(2.0+I,5.0+I)", //
				"0.15456969798535944+I*(-0.050557879794783156)");
		checkNumeric("SphericalBesselY(2.0,-5)", //
				"-0.16499545760108872");
	}

	public void testSplit() {
		check("Split({x, x, x, y, x, y, y, z})", //
				"{{x,x,x},{y},{x},{y,y},{z}}");
		check("Split({x, x, x, y, x, y, y, z}, x)", //
				"{{x},{x},{x},{y},{x},{y},{y},{z}}");
		check("Split({1, 5, 6, 3, 6, 1, 6, 3, 4, 5, 4}, Less)", //
				"{{1,5,6},{3,6},{1,6},{3,4,5},{4}}");
		check("Split({1, 5, 6, 3, 6, 1, 6, 3, 4, 5, 4}, Greater)", //
				"{{1},{5},{6,3},{6,1},{6,3},{4},{5,4}}");
		check("Split({x -> a, x -> y, 2 -> a, z -> c, z -> a}, First(#1) === First(#2) &)", //
				"{{x->a,x->y},{2->a},{z->c,z->a}}");
		check("Split({})", //
				"{}");
	}

	public void testSplitBy() {
		check("SplitBy(Range(1, 3, 1/3), Round)", //
				"{{1,4/3},{5/3,2,7/3},{8/3,3}}");
		check("SplitBy({1, 2, 1, 1.2}, {Round, Identity})", //
				"{{{1}},{{2}},{{1},{1.2}}}");
		check("Tuples({1, 2}, 3)", //
				"{{1,1,1},{1,1,2},{1,2,1},{1,2,2},{2,1,1},{2,1,2},{2,2,1},{2,2,2}}");
		check("SplitBy(Tuples({1, 2}, 3), First)", //
				"{{{1,1,1},{1,1,2},{1,2,1},{1,2,2}},{{2,1,1},{2,1,2},{2,2,1},{2,2,2}}}");

	}

	public void testSqrt() {
		check("Sqrt(-Sqrt(3))", //
				"I*3^(1/4)");
		check("Sqrt(Sqrt(3))", //
				"3^(1/4)");
		check("Sqrt((1-a)*a)", //
				"Sqrt((1-a)*a)");
		check("(-3/4)/Sqrt(-3/4)", //
				"I*1/2*Sqrt(3)");
		check("(3/4)/Sqrt(3/4)", //
				"Sqrt(3)/2");
		check("(-3)/Sqrt(-3)", //
				"I*Sqrt(3)");
		check("3/Sqrt(3)", //
				"Sqrt(3)");
		check("Sqrt(4)", //
				"2");
		check("Sqrt(5)", //
				"Sqrt(5)");
		check("Sqrt(5) // N", //
				"2.23607");
		check("Sqrt(a)^2", //
				"a");
		check("Sqrt(-4)", //
				"I*2");
		check("I == Sqrt(-1)", //
				"True");
		// TODO use ExprParser#getReal() if apfloat problems are fixed
		// check("N(Sqrt(2), 50)",
		// "1.41421356237309504880168872420969807856967187537694");
	}

	public void testSquareFreeQ() {
		check("SquareFreeQ(9)", //
				"False");

		check("SquareFreeQ(5)", //
				"True");
		check("SquareFreeQ(9)", //
				"False");
		check("SquareFreeQ(20)", //
				"False");
		check("SquareFreeQ(10)", //
				"True");
		check("SquareFreeQ(12)", //
				"False");
		check("SquareFreeQ(105)", //
				"True");
		check("SquareFreeQ(x^4-1)", //
				"True");
		check("SquareFreeQ(x^4 - 2*x^2 + 1)", //
				"False");
		check("SquareFreeQ(x^2+1)", //
				"True");
		check("SquareFreeQ(9 + 6*x + x^2)", //
				"False");
		check("SquareFreeQ(x^2 + 1, Modulus -> 2)", //
				"False");
	}

	public void testSquareMatrixQ() {
		check("SquareMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})", //
				"True");
		check("SquareMatrixQ({{}})", //
				"False");
		check("SquareMatrixQ({{a,b,c}, {d,e,f}})", //
				"False");
	}

	public void testSquaredEuclideanDistance() {
		// check("SquaredEuclideanDistance(-7, 5)", "144");
		check("SquaredEuclideanDistance({-1, -1}, {1, 1})", //
				"8");

	}

	public void testSpan() {

		check("{a, b, c, d, e, f, g, h}[[2 ;; -3]]", //
				"{b,c,d,e,f}");

		check("FullForm( ;; )", //
				"Span(1, All)");
		check("FullForm(1;;4;;2)", //
				"Span(1, 4, 2)");
		check("FullForm(2;;-2)", //
				"Span(2, -2)");
		check("FullForm(;;3)", //
				"Span(1, 3)");
		// check("a ;; b ;; c ;; d", "(1;;d) (a;;b;;c)");

		check("{a, b, c, d, e, f, g, h}[[2 ;; -3]]", //
				"{b,c,d,e,f}");
		check("{a, b, c, d, e, f, g, h}[[2 ;; 5]]", //
				"{b,c,d,e}");
		check("{a, b, c, d, e, f, g, h}[[2 ;; All]]", //
				"{b,c,d,e,f,g,h}");
	}

	public void testStandardize() {
		check("Standardize({6.5, 3.8, 6.6, 5.7, 6.0, 6.4, 5.3})", //
				"{0.75705,-1.99453,0.85896,-0.0582346,0.247497,0.655139,-0.465877}");
		check("Standardize({{a,b},{c,d}})", //
				"{{(Sqrt(2)*(a+1/2*(-a-c)))/Sqrt((a-c)*(Conjugate(a)-Conjugate(c))),(Sqrt(2)*(b+1/\n"
						+ "2*(-b-d)))/Sqrt((b-d)*(Conjugate(b)-Conjugate(d)))},\n"
						+ " {(Sqrt(2)*(1/2*(-a-c)+c))/Sqrt((a-c)*(Conjugate(a)-Conjugate(c))),(Sqrt(2)*(1/2*(-b-d)+d))/Sqrt((b-d)*(Conjugate(b)-Conjugate(d)))}}");
	}

	public void testStandardDeviation() {
		check("StandardDeviation({1, 2, 3})", //
				"1");
		check("StandardDeviation({7, -5, 101, 100})", //
				"Sqrt(13297)/2");
		check("StandardDeviation({a, a})", //
				"0");
		check("StandardDeviation({{1, 10}, {-1, 20}})", //
				"{Sqrt(2),5*Sqrt(2)}");
		check("StandardDeviation({1.21, 3.4, 2, 4.66, 1.5, 5.61, 7.22})", //
				"2.27183");
		check("StandardDeviation(LogNormalDistribution(0, 1))", //
				"Sqrt((-1+E)*E)");
	}

	public void testStieltjesGamma() {
		check("StieltjesGamma(0)", //
				"EulerGamma");
		check("StieltjesGamma(0,a)", //
				"-PolyGamma(0,a)");
	}

	public void testStirlingS1() {
		check("StirlingS1(9,6)", //
				"-4536");
		check("StirlingS1(0,0)", //
				"1");
		check("StirlingS1(1,1)", //
				"1");
		check("StirlingS1(0,1)", //
				"0");
		check("StirlingS1(1,0)", //
				"0");
		check("StirlingS1(50,1)", //
				"-608281864034267560872252163321295376887552831379210240000000000");
		check("StirlingS1({2,4,6},2)", //
				"{1,11,274}");
		check("Table(StirlingS1(12, m), {m, 5})", //
				"{-39916800,120543840,-150917976,105258076,-45995730}");
		check("Table(Sum( StirlingS1(m, l)*StirlingS2(l, n), {l, 0, Max(n, m) + 1}), {n, 0,  5}, {m, 0, 5})", //
				"{{1,0,0,0,0,0},{0,1,0,0,0,0},{0,0,1,0,0,0},{0,0,0,1,0,0},{0,0,0,0,1,0},{0,0,0,0,\n" + "0,1}}");
	}

	public void testStirlingS2() {
		check("StirlingS2(-1,-3)", //
				"StirlingS2(-1,-3)");

		check("StirlingS2(6,10)", //
				"0");
		check("StirlingS2(10,6)", //
				"22827");
		check("StirlingS2(0,0)", //
				"1");
		check("StirlingS2(1,1)", //
				"1");
		check("StirlingS2(0,1)", //
				"0");
		check("StirlingS2(1,0)", //
				"0");
		check("StirlingS2(10,11)", //
				"0");
		check("Table(StirlingS2(10, m), {m, 10})", //
				"{1,511,9330,34105,42525,22827,5880,750,45,1}");
		check("StirlingS2({2, 4, 6}, 2)", //
				"{1,7,31}");
		check("StirlingS2(10,4)", //
				"34105");
		check("StirlingS2(1000, 500)", "11897164077580438091910055658742826<<SHORT>>", //
				35);
		check("StirlingS2(2000, 199)", "12783663313027805423901972026528914<<SHORT>>", //
				35);
	}

	public void testString() {
		check("\"Hello world!\\\n" //
				+ "next line\"", //
				"Hello world!\n" + "next line");
		check("\"Hello world!\\\n" //
				+ "next line\"", //
				"Hello world!\n" + //
						"next line");
	}

	public void testStringJoin() {
		check("StringJoin(\"test\")", //
				"test");
		check("\"Hello\" <> \" \" <> \"world!\"", //
				"Hello world!");
		check("\"Debian\" <> 6", //
				"Debian<>6");
		check("\"Debian\" <> ToString(6)", //
				"Debian6");
		check("\"Java\" <> ToString(8)", //
				"Java8");
		check("StringJoin(\"Java\", ToString(8))", //
				"Java8");
	}

	public void testStringLength() {
		check("StringLength(\"tiger\")", //
				"5");
	}

	public void testStringQ() {
		check("StringQ(a)", //
				"False");
		check("StringQ(\"a\")", //
				"True");
	}

	public void testStringReplace() {
		check("StringReplace(\"abbaabbaa\", \"ab\" -> \"X\")", //
				"XbaXbaa");
	}

	public void testStringRiffle() {
		check("StringRiffle({\"a\", \"b\", \"c\", \"d\", \"e\"})", //
				"a b c d e");
		check("StringRiffle({\"a\", \"b\", \"c\", \"d\", \"e\"}, {\"(\", \" \", \")\"})", //
				"(a b c d e)");
		check("StringRiffle({{\"a\", \"b\", \"c\"}, {\"d\", \"e\", \"f\"}})", //
				"a b c\n" + "d e f");
		check("StringRiffle({{\"a\", \"b\", \"c\"}, {\"d\", \"e\", \"f\"}}, \"\\n\", \"\\t\")", //
				"a	b	c\n" + "d	e	f");
		check("StringRiffle(Range(10), \", \")", //
				"1, 2, 3, 4, 5, 6, 7, 8, 9, 10");
		check("StringRiffle({{\"a\", \"b\", \"c\"}, {\"d\", \"e\", \"f\"}}, {\"(\", \"\\n\", \")\"},\"-\")", //
				"(a-b-c\n" + "d-e-f)");
	}

	public void testStringTake() {
		check("StringTake(\"abcdefghijklm\", 6)", //
				"abcdef");
		check("StringTake(\"abcdefghijklm\", -4)", //
				"jklm");
		// check("StringTake(\"abcdefghijklm\", {5, 10})", "efghij");
	}

	public void testStruveH() {
		check("StruveH(1.5, 3.5)", //
				"1.13199");
		check("StruveH(I,0)", //
				"0");
		check("StruveH(-1+I,0)", //
				"Indeterminate");
		check("StruveH(-2+I,0)", //
				"ComplexInfinity");
		check("StruveH(1/2,x)", //
				"Sqrt(2/Pi)*Sqrt(1/x)*(1-Cos(x))");
		check("StruveH(-1/2,x)", //
				"Sqrt(2/Pi)*Sqrt(1/x)*Sin(x)");
		check("StruveH(a,-x)", //
				"(-(-x)^a*StruveH(a,x))/x^a");
	}

	public void testStruveL() {
		check("StruveL(1.5, 3.5)", //
				"4.41417");
		check("StruveL(I,0)", //
				"0");
		check("StruveL(-1+I,0)", //
				"Indeterminate");
		check("StruveL(-2+I,0)", //
				"ComplexInfinity");
		check("StruveL(1/2,x)", //
				"Sqrt(2/Pi)*Sqrt(1/x)*(-1+Cosh(x))");
		check("StruveL(-1/2,x)", //
				"Sqrt(2/Pi)*Sqrt(1/x)*Sinh(x)");
		check("StruveL(a,-x)", //
				"(-(-x)^a*StruveL(a,x))/x^a");
	}

	public void testSubdivide() {
		check("Subdivide(10,  4)", //
				"{0,5/2,5,15/2,10}");
		check("Subdivide({10,5},  4)", //
				"{0,{5/2,5/4},{5,5/2},{15/2,15/4},{10,5}}");
		check("Subdivide({10,5}, {5,15}, 4)", //
				"{{10,5},{35/4,15/2},{15/2,10},{25/4,25/2},{5,15}}");
		check("Subdivide({10,5}, 3, 4)", //
				"{{10,5},{33/4,9/2},{13/2,4},{19/4,7/2},{3,3}}");
		check("Subdivide({10,5}, {1,5,15}, 4)", //
				"Subdivide({10,5},{1,5,15},4)");

		check("Subdivide(5)", //
				"{0,1/5,2/5,3/5,4/5,1}");
		check("Subdivide(10,15,5)", //
				"{10,11,12,13,14,15}");
		check("Subdivide(10,15,4)", //
				"{10,45/4,25/2,55/4,15}");
		check("Subdivide(-1, -4, 3)", //
				"{-1,-2,-3,-4}");
		check("Subdivide(10, 5, 4)", //
				"{10,35/4,15/2,25/4,5}");
		check("Subdivide(5, 15, 4)", //
				"{5,15/2,10,25/2,15}");

		check("Subdivide(10)", //
				"{0,1/10,1/5,3/10,2/5,1/2,3/5,7/10,4/5,9/10,1}");
		check("Subdivide(10, 5)", //
				"{0,2,4,6,8,10}");
		check("Subdivide(-1,2, 5)", //
				"{-1,-2/5,1/5,4/5,7/5,2}");
		check("Subdivide(-1.,1., 8)", //
				"{-1.0,-0.75,-0.5,-0.25,0.0,0.25,0.5,0.75,1.0}");
		check("Subdivide(E,Pi, 4)", //
				"{E,3/4*E+Pi/4,E/2+Pi/2,E/4+3/4*Pi,Pi}");
		check("Subdivide(a, b, 6)", //
				"{a,5/6*a+b/6,2/3*a+b/3,a/2+b/2,a/3+2/3*b,a/6+5/6*b,b}");
		check("Subdivide(N(E,21),N(Pi,21), 4)", //
				"{2.71828182845904523536,2.82410953474173223613,2.92993724102441923691,3.03576494730710623768,3.14159265358979323846}");
	}

	public void testSubfactorial() {
		check("Subfactorial(12)", //
				"176214841");
		check("Subfactorial(n)", //
				"Subfactorial(n)");
		check("Table(Subfactorial(n), {n, 10})", //
				"{0,1,2,9,44,265,1854,14833,133496,1334961}");

		// The only number equal to the sum of subfactorials of its digits:
		check("148349 == Total(Subfactorial({1, 4, 8, 3, 4, 9}))", //
				"True");

		// check("Subfactorial(10000)", "Subfactorial(10000)");
	}

	public void testSubsetQ() {
		check("SubsetQ(f(b,a,b,c), f(c, c, c))", //
				"True");
		check("SubsetQ(f(b,a,b,c), f(a, b, d))", //
				"False");

		// same as ContainsAll
		check("SubsetQ({b,a,b,c}, {a, b})", //
				"True");
		check("SubsetQ({b,a,b,c}, {c, c, c})", //
				"True");
		check("SubsetQ({b,a,b,c}, {a, b, d})", //
				"False");
		check("SubsetQ({b, a, d}, {a, b, c})", //
				"False");
		check("SubsetQ({ }, {a, b, c})", //
				"False");
		check("SubsetQ({ },{ })", //
				"True");
		check("SubsetQ({a, b, c},{ })", //
				"True");

		check("SubsetQ(1, {1,2,3})", //
				"SubsetQ(1,{1,2,3})");
		check("SubsetQ({1,2,3}, 4)", //
				"SubsetQ({1,2,3},4)");

		check("SubsetQ({1.0,2.0}, {1,2,3})", //
				"False");
		check("SubsetQ({1.0,2.0}, {1,2,3}, SameTest->Equal)", //
				"False");

		check("SubsetQ({1,2,3}, {1.0,2.0})", //
				"False");
		check("SubsetQ({1,2,3}, {1.0,2.0}, SameTest->Equal)", //
				"True");
	}

	public void testSubsets() {
		// https://oeis.org/A018900 - Sum of two distinct powers of 2
		check("Union(Total/@Subsets(2^Range(0, 10), {2}))", //
				"{3,5,6,9,10,12,17,18,20,24,33,34,36,40,48,65,66,68,72,80,96,129,130,132,136,144,\n"
						+ "160,192,257,258,260,264,272,288,320,384,513,514,516,520,528,544,576,640,768,1025,\n"
						+ "1026,1028,1032,1040,1056,1088,1152,1280,1536}");
		check("Subsets()", //
				"Subsets()");
		check("Subsets({})", //
				"{{}}");
		check("Subsets({a,b,c})", //
				"{{},{a},{b},{c},{a,b},{a,c},{b,c},{a,b,c}}");
		check("Subsets({a,b,c},2)", //
				"{{},{a},{b},{c},{a,b},{a,c},{b,c}}");
		check("Subsets({a,b,c},{2})", //
				"{{a,b},{a,c},{b,c}}");
		check("Subsets({a,b,c,d},{2})", //
				"{{a,b},{a,c},{a,d},{b,c},{b,d},{c,d}}");
	}

	public void testSubtract() {
		check("a - x + z", //
				"a-x+z");
		check("5 - 3", //
				"2");
		check("a - b // FullForm", //
				"Plus(a, Times(-1, b))");
		check("a - b - c", //
				"a-b-c");
		check("a - (b - c)", //
				"a-b+c");
	}

	public void testSubtractFrom() {
		check("a = 10", //
				"10");
		check("a -= 2", //
				"8");
		check("a", //
				"8");
	}

	public void testSum() {
		check("Sum((b+i*d)*a^i, {i,0,n})", //
				"((-1+a^(1+n))*b)/(-1+a)+(d*(a+a^(1+n)*(-1-n+a*n)))/(1-a)^2");

		check("Sum(i*a^i, {i,0,n})", //
				"(a+a^(1+n)*(-1-n+a*n))/(1-a)^2");
		check("Sum(i*a^i, {i,1,n})", //
				"(a+a^(1+n)*(-1-n+a*n))/(1-a)^2");

		check("Sum(a^i, {i,0,n})", //
				"(-1+a^(1+n))/(-1+a)");
		check("Sum((3/7)^i, {i,1,n})", //
				"-1+7/4*(1-(3/7)^(1+n))");
		check("Sum((3/7)^i, {i,0,n})", //
				"7/4*(1-(3/7)^(1+n))");

		// prints RecursionLimitExeceeded
		check("Sum(f(x), {x, x, x+1})", //
				"Sum(f(x),{x,x,1+x})");

		check("Sum(f(x), {x, x, x})", //
				"f(x)");
		check("Sum(f(x), {x, a, a+1})", //
				"f(a)+f(1+a)");

		check("Sum(Boole(x>0), {x,{1,2,3,-2,4,5}})", //
				"5");
		check("Sum(0, {k, a, Infinity})", //
				"0");
		check("Sum(1, {k, a, Infinity})", //
				"Infinity");
		check("Sum(-2, {k, a, Infinity})", //
				"-Infinity");
		check("Sum(42, {k, a, Infinity})", //
				"Infinity");

		// {k,a,n} assumes a<=k<=n
		check("Sum(2, {k, a, n})", //
				"2*(1-a+n)");

		check("Sum(i, {i, 5, 10})", //
				"45");
		check("Sum(i, {i, 0, 30000})", //
				"450015000");
		check("Sum(i, {i, 0, n})", //
				"1/2*n*(1+n)");
		check("Sum(i, {i, 3, n})", //
				"1/2*(-2+n)*(3+n)");
		check("Sum(a + b, {a, 0, 2}, {b, 0, 3})", //
				"30");
		check("Sum(a, {a, {b, c, d, e}})", //
				"b+c+d+e");
		check("Sum(a*f, {a, {b, c, d, e}}, {f, {g, h}})", //
				"b*g+c*g+d*g+e*g+b*h+c*h+d*h+e*h");

		check("Sum(f(k,j),{k,0,-1+2}, {j,0,-1+k})", //
				"f(1,0)");
		check("Sum(k, {k, 1, n})", //
				"1/2*n*(1+n)");
		check("Sum(k, {k, 1, 10})", //
				"55");
		check("Sum(g(i),{i,10,2})", //
				"0");

		// check("Table(f(k,j), {k,0,-1+3},{j,0,-1+k})",
		// "{{},{f(1,0)},{f(2,0),f(2,1)}}");
		check("Sum(f(k,j),{k,0,-1+2}, {j,0,-1+k})", //
				"f(1,0)");
		check("Sum(f(i,j), {i, 1, 2}, {j, 1, 3})", //
				"f(1,1)+f(1,2)+f(1,3)+f(2,1)+f(2,2)+f(2,3)");
		// check("Sum(f(k,j), {k,0,-1+2},{j,0,-1+k})", "f(1,0)");
		// check("Sum(((-1)^k*Binomial(-1+2,k)*2^((-1)*2*k+2)*Binomial(2*k,j)*Sin(1/2*2*Pi+2*(-j+k)*#1))/((\n"
		// + "1+k)*Cos(#1)^(2+2*k)*(-j+k)^(1-2)),{k,0,-1+2},{j,0,-1+k})", "");
		check("Sum(j+k, {k,0,-1+2},{j,0,-1+k})", "1");
		check("Sum(k, {k, 1, 10})", //
				"55");
		check("Sum(i * j, {i, 1, 10}, {j, 1, 10})", //
				"3025");
		check("Sum(k, {k, 1, n})", //
				"1/2*n*(1+n)");
		check("Sum(k, {k, n, 2*n})", //
				"3/2*n*(1+n)");
		check("Sum(k, {k, 2, 2})", //
				"2");
		check("Sum(k, {k, 2, 3})", //
				"5");
		check("Sum(k, {k, I, I + 1})", //
				"1+I*2");
		check("Sum(1 / k ^ 2, {k, 1, n})", //
				"HarmonicNumber(n,2)");
		check("Simplify(Sum(x ^ 2, {x, 1, y}) - y * (y + 1) * (2 * y + 1) / 6)", //
				"0");
		check("Sum( 2 ^ (-i), {i, 1, Infinity})", //
				"1");
		check("Sum( (1/2) ^ i, {i, 1, Infinity})", //
				"1");
		check("Sum(1 / k ^ 2, {k, 1, Infinity}) ", //
				"Pi^2/6");
		check("Sum(i / Log(i), {i, 1, Infinity})", //
				"Sum(i/Log(i),{i,1,Infinity})");
		check("Sum(Cos(Pi*i), {i, 1, Infinity})", //
				"Sum(Cos(i*Pi),{i,1,Infinity})");
		check("Sum(x^k*Sum(y^l,{l,0,4}),{k,0,4})", //
				"1+y+y^2+y^3+y^4+x*(1+y+y^2+y^3+y^4)+x^2*(1+y+y^2+y^3+y^4)+x^3*(1+y+y^2+y^3+y^4)+x^\n"
						+ "4*(1+y+y^2+y^3+y^4)");
		check("Sum(2^(-i), {i, 1, Infinity})", //
				"1");
		check("Sum((-3)^(-i), {i, 1, Infinity})", //
				"-1/4");

		check("Sum(k, {k, Range(5)})", //
				"15");
		check("Sum(i^2 - i + 10 ,{i,1,10})", //
				"430");
		check("Sum(i!,{i,3,n})", //
				"-4-Subfactorial(-1)+(-1)^(1+n)*Gamma(2+n)*Subfactorial(-2-n)");
		check("Sum(i!,{i,1,n})", //
				"-1-Subfactorial(-1)+(-1)^(1+n)*Gamma(2+n)*Subfactorial(-2-n)");

		check("Sum(g(i),{i,10,2})", //
				"0");
		check("Sum(0.5^i,{i,1,Infinity})", //
				"1.0");

		check("Sum((1/2)^i,{i,0,1})", //
				"3/2");
		check("Sum((1/2)^i,{i,0,Infinity})", //
				"2");
		check("Sum((1/2)^i,{i,1,Infinity})", //
				"1");
		check("Sum((1/2)^i,{i,3,Infinity})", //
				"1/4");

		check("Sum(a^i,{i,0,1})", //
				"1+a");
		check("Sum(a^i,{i,0,Infinity})", //
				"1-a/(-1+a)");
		check("Sum(a^i,{i,1,Infinity})", //
				"-a/(-1+a)");
		check("Sum(a^i,{i,3,Infinity})", //
				"-a-a/(-1+a)-a^2");

		check("Sum(0,{i,-4,Infinity})", //
				"0");
		check("Sum((-2)^i,{i,0,Infinity})", //
				"Sum((-2)^i,{i,0,Infinity})");
		check("Sum(42^i,{i,0,Infinity})", //
				"Sum(42^i,{i,0,Infinity})");

		check("Sum(i^k,{i,1,n})", //
				"HarmonicNumber(n,-k)");
		check("Sum(i^5,{i,1,n})", //
				"-n^2/12+5/12*n^4+n^5/2+n^6/6");
		check("Sum(f(i,1),{i,{a,b}})", //
				"f(a,1)+f(b,1)");

		check("Sum(c/(i-j+1), {j,i+1,n}, {i,1,n})", //
				"c*Sum(1/(i-j+1),{j,1+i,n},{i,1,n})");
		check("Sum(-(-c*j+c),{j,i+1,n})", //
				"-c*(-i+n)+1/2*c*(-i+n)*(1+i+n)");

		check("Sum(c*(i-j+1), {j,i+1,n}, {i,1,n})", //
				"c*n*(-i+n)+1/2*c*(i-n)*n*(1+i+n)+c*(1/2*(i-n)+1/2*(i-n)*n+1/2*(-i+n)+n*(-i+n)+1/\n" + "2*(-i+n)*n^2)");
		check("Simplify(1/2*c*(n-i)*n^2-1/2*c*n*(n+i+1)*(n-i)+3/2*c*n*(n-i))", //
				"1/2*c*n*(-2*i+i^2+2*n-i*n)");

		check("Sum(c*(n-1), {j,i,n-1})", //
				"-c*(-i+n)+c*n*(-i+n)");
		check("Sum(c, {j,i,n-1}, {i,1,n-1})", //
				"-c*(-i+n)+c*n*(-i+n)");
		check("Sum(1,{k,j+i,n})", //
				"1-i-j+n");
		check("Sum(k,{k,1,n+1})", //
				"1/2*(1+n)*(2+n)");
		check("Sum(i^(1/2), {i, 1, n} )", //
				"HarmonicNumber(n,-1/2)");
		check("Sum(1/i, {i, 1, n} )", //
				"HarmonicNumber(n)");
		check("Sum(i^(-3), {i, 1, n} )", //
				"HarmonicNumber(n,3)");
		check("Sum(Ceiling(Log(i)),{i,1,n})", //
				"(-E^Floor(Log(n))+n)*Ceiling(Log(n))+(1+E^(1+Floor(Log(n)))*Floor(Log(n))-E^Floor(Log(n))*(\n"
						+ "1+Floor(Log(n))))/(-1+E)");
		check("Sum(Ceiling(Log(a,i)),{i,1,n})", //
				"(-a^Floor(Log(n)/Log(a))+n)*Ceiling(Log(n)/Log(a))+(1+a^(1+Floor(Log(n)/Log(a)))*Floor(Log(n)/Log(a))-a^Floor(Log(n)/Log(a))*(\n"
						+ "1+Floor(Log(n)/Log(a))))/(-1+a)");
		check("Sum(i*1/2*i,{i,1,n})", //
				"1/2*(n/6+n^2/2+n^3/3)");
		check("Sum(k * k,{k,1,n+1})", //
				"1+13/6*n+3/2*n^2+n^3/3");
		check("Sum(k,{k,4,2})", //
				"0");
		check("Sum(k,{k,a,b})", //
				"1/2*(1-a+b)*(a+b)");
		check("Sum(c, {k, 1, Infinity} )", //
				"Sum(c,{k,1,Infinity})");
		check("Sum(k,{k,1,n+1})", //
				"1/2*(1+n)*(2+n)");
		check("Sum(f(i,1),{i,{a,b}})", //
				"f(a,1)+f(b,1)");
		check("Sum(f(i, j), {i, {a, b}}, {j, 1, 2})", //
				"f(a,1)+f(a,2)+f(b,1)+f(b,2)");
		check("Sum(c, {i, 1, j}, {j, 1, 2})", //
				"2*c*j");

		check("Sum(c, {k, -Infinity, 10} )", //
				"Sum(c,{k,-Infinity,10})");

		check("Sum(c+k, {k, 1, m} )", //
				"c*m+1/2*m*(1+m)");
		check("Sum(c, {k, l, m} )", //
				"c*(1-l+m)");
		check("Sum(c, {k, 1, m} )", //
				"c*m");
		check("Sum(a, {k, j, n} )", //
				"a*(1-j+n)");
		check("Sum(c, {i0, 1, n0} )", //
				"c*n0");
		check("Sum(c, {i0, 0, n0} )", //
				"c*(1+n0)");
		check("Sum(c*n0, {i0, 1, n0} )", //
				"c*n0^2");
		check("Sum(c*n0, {i0, 0, n0} )", //
				"c*n0*(1+n0)");

		check("Sum(c, {i0, 1, n0}, {j0, 1, n0})", //
				"c*n0^2");
		check("Sum(i0, {i0, 0, n0})", //
				"1/2*n0*(1+n0)");
		check("Sum(i^2, {i, 1, n})", //
				"n/6+n^2/2+n^3/3");
		check("Sum(4*i^2, {i, 0, n})", //
				"4*(n/6+n^2/2+n^3/3)");
		check("Sum(i0^3, {i0, 0, n0})", //
				"n0^2/4+n0^3/2+n0^4/4");
		check("Sum(i0^3+p^2, {i0, 0, n0})", //
				"n0^2/4+n0^3/2+n0^4/4+(1+n0)*p^2");
		check("Sum(Binomial(n0,i0), {i0, 0, n0})", //
				"2^n0");
		check("sum(i0*binomial(n0,i0), {i0, 0, n0})", //
				"n0/2^(1-n0)");
		check("sum(p, {i0, 1, n0})", //
				"n0*p");
		check("sum(p+q, {i0, 1, n0})", //
				"n0*p+n0*q");
		check("sum(p, {i0, 0, n0})", //
				"(1+n0)*p");
		check("sum(4, {i0, 0, n0})", //
				"4*(1+n0)");
		check("sum(lcm(3, k), {k, 100})", //
				"11784");
		check("Sum(sin(x), x)", //
				"Sum(Sin(x),x)");
		check("Sum(x, x)", //
				"1/2*x*(1+x)");
		check("Sum(x^2, x)", //
				"x/6+x^2/2+x^3/3");
		check("Sum(x^3, x)", //
				"x^2/4+x^3/2+x^4/4");
		check("Sum(x^4, x)", //
				"-x/30+x^3/3+x^4/2+x^5/5");
		check("Sum(c, {i, 1, n}, {j, 1, n})", //
				"c*n^2");
		check("Sum(c, {i, 1, j}, {j, 1, n})", //
				"c*j*n");
		check("Sum(c, {j, 1, n}, {i, 1, j})", //
				"1/2*c*n*(1+n)");
		check("Sum((i^2 + i)/2, {i,1,n})", //
				"1/4*n*(1+n)+1/2*(n/6+n^2/2+n^3/3)");
		check("Sum(i*(i + 1)/2, {i,1,n})", //
				"1/4*n*(1+n)+1/2*(n/6+n^2/2+n^3/3)");

		check("Sum(k^a,{k,j,n})", //
				"HurwitzZeta(-a,j)-HurwitzZeta(-a,1+n)");
		check("-1/4*a^4", //
				"-a^4/4");
		check("Sum(k^3,{k,a,b})", //
				"-a^2/4+a^3/2-a^4/4+b^2/4+b^3/2+b^4/4");
	}

	public void testSurd() {
		check("Surd(-16.0,2)", //
				"Indeterminate");

		checkNumeric("Surd(-3,3)", //
				"-3^(1/3)");
		checkNumeric("N((-3)^(1/3))", //
				"0.7211247851537043+I*1.2490247664834064");
		checkNumeric("Surd(-3,3)-(-3)^(1/3)", //
				"-(-3)^(1/3)-3^(1/3)");
		checkNumeric("Surd(-3.,3)-(-3)^(1/3)", //
				"-2.1633743554611127+I*(-1.2490247664834064)");
		checkNumeric("Surd(-3,3)", //
				"-3^(1/3)");
		checkNumeric("Surd(-3.,3)", //
				"-1.4422495703074083");
		checkNumeric("N(Surd(-3,3))", //
				"-1.4422495703074083");

		checkNumeric("1/9 * 3^(4/3)", //
				"1/3^(2/3)");
		// checkNumeric("1/9 * 3^(7/4)", "1/3^(1/4)");
		checkNumeric("1/9 * 3^(3/4)", //
				"1/(3*3^(1/4))");
		checkNumeric("1/9 * 3^(-1/2)", //
				"1/(9*Sqrt(3))");
		checkNumeric("1/9 * 3^(1/2)", //
				"1/(3*Sqrt(3))");
		checkNumeric("2^(1/4)*2^(-3)", //
				"1/(4*2^(3/4))");
		checkNumeric("2^(-3)", //
				"1/8");
		checkNumeric("2^(-3/4)", //
				"1/2^(3/4)");

		// checkNumeric("Trace((2^(1/4))/8)", "");
		checkNumeric("Surd(2,4)/8-(1/(4*2^(1/4.0)))", //
				"-0.061573214438088525");
		checkNumeric("1/(4*2^(1/4.0))", //
				"0.21022410381342865");
		checkNumeric("Surd(2,4)", //
				"2^(1/4)");
		checkNumeric("Surd(2,4)/8", //
				"1/(4*2^(3/4))");

		checkNumeric("Surd(-2.,5)", //
				"-1.148698354997035");
		// checkNumeric("(-2.0)^(1/5)", "-1.148698354997035");

		check("Surd(-16.0,2)", //
				"Indeterminate");
		checkNumeric("Surd(-2.,5)", //
				"-1.148698354997035");
		check("Surd(-3,2)", //
				"Indeterminate");
		check("Surd(-3,-2)", //
				"Indeterminate");

		check("Surd(I,2)", //
				"Surd(I,2)");
		check("Surd({-3, -2, -1, 0, 1, 2, 3}, 7)", //
				"{-3^(1/7),-2^(1/7),-1,0,1,2^(1/7),3^(1/7)}");
		checkNumeric("N(Surd({-3, -2, -1, 0, 1, 2, 3}, 7))", //
				"{-1.169930812758687,-1.1040895136738123,-1.0,0.0,1.0,1.1040895136738123,1.169930812758687}");
		checkNumeric("N(Surd( -2,  5),25)", //
				"-1.1486983549970350067986269");

	}

	public void testSurvivalFunction() {
		check("SurvivalFunction(GeometricDistribution(1/3), x)", //
				"1-Piecewise({{1-(2/3)^(1+Floor(x)),x>=0}},0)");
		check("SurvivalFunction(NormalDistribution(), {0.2, 0.3})", //
				"{0.42074,0.382089}");
		check("SurvivalFunction(BetaDistribution(1/2,1/2), {{0.0, 0.0}, {0.2, 0.2}, {0.3, 0.3}})", //
				"{{1.0,1.0},{0.704833,0.704833},{0.63099,0.63099}}");
		check("SurvivalFunction(NormalDistribution(0, 1), x)", //
				"1-Erfc(-x/Sqrt(2))/2");
		check("CDF(NormalDistribution(0, 1), x)", //
				"Erfc(-x/Sqrt(2))/2");
	}

	public void testSwitch() {
		check("Switch(2, 1, x, 2, y, 3, z)", //
				"y");
		check("Switch(5, 1, x, 2, y)", //
				"Switch(5,1,x,2,y)");
		check("Switch(5, 1, x, 2, y, _, z)", //
				"z");
		check("Switch(2, 1)", //
				"Switch(2,1)");
		check("$f(b_) := switch(b, True, 1, False, 0, _, -1);{$f(True), $f(False), $f(x)}", //
				"{1,0,-1}");
	}

	public void testSymbol() {
		check("Head(x)", //
				"Symbol");
		check("Symbol(\"x\") + Symbol(\"x\")", //
				"2*x");
		check("i\\[CapitalGamma]j\\(hjgg)", //
				"iγj\\(hjgg)");
		check("i\\[Alpha]j=10;i\\[Alpha]j", //
				"10");
	}

	public void testSymbolName() {
		check("Context(x)", //
				"Global`");
		check("SymbolName(x)", //
				"x");
		// TODO allow contexts
		// check("SymbolName(a`b`x)", "x");

	}

	public void testSymbolQ() {
		check("SymbolQ(a)", //
				"True");
		check("SymbolQ(1)", //
				"False");
		check("SymbolQ(a+b)", //
				"False");
	}

	public void testSymmetricMatrixQ() {
		// example from https://en.wikipedia.org/wiki/Symmetric_matrix
		check("SymmetricMatrixQ({{1,7,3}, {7,4,-5}, {3,-5,6}})", //
				"True");

		check("SymmetricMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})", //
				"False");
		check("SymmetricMatrixQ({{1, 3 + 3*I}, {3 + 3*I, 2}})", //
				"True");
		check("SymmetricMatrixQ(Table(Re(i)*Re(j), {i, 10}, {j, 10}))", //
				"True");
		check("Block({b = c}, SymmetricMatrixQ({{a, b}, {c, d}}))", //
				"True");
	}

	public void testSyntaxQ() {
		check("SyntaxQ(\"Integrate(f(x),{x,0,10})\")", //
				"True");
		check("SyntaxQ(\"Integrate(f(x),{x,0,10)\")", //
				"False");
	}

	public void testTable() {
		check("Table(Sum(k^n, {k, 0, m}), {n, 1, 5, 1})", //
				"{1/2*m*(1+m),m/6+m^2/2+m^3/3,m^2/4+m^3/2+m^4/4,-m/30+m^3/3+m^4/2+m^5/5,-m^2/12+5/\n" + //
						"12*m^4+m^5/2+m^6/6}");
		check("Table(f(x), {x, a, a+1})", //
				"{f(a),f(1+a)}");
		check("s=0;Table(s=i+s, {i, 0, 7})", //
				"{0,1,3,6,10,15,21,28}");
		check("Table(a + dx, {dx, 0, 3, Pi/8})", //
				"{a,a+Pi/8,a+Pi/4,a+3/8*Pi,a+Pi/2,a+5/8*Pi,a+3/4*Pi,a+7/8*Pi}");
		check("Table(0.0^x, {x, -1.0, 1.0, 0.25})", //
				"{ComplexInfinity,ComplexInfinity,ComplexInfinity,ComplexInfinity,Indeterminate,0.0,0.0,0.0,0.0}");
		check("Table(0^x, {x,-1, 1,1/4})", //
				"{ComplexInfinity,ComplexInfinity,ComplexInfinity,ComplexInfinity,Indeterminate,0,\n" + //
						"0,0,0}");
		check("Table(x, {4})", //
				"{x,x,x,x}");
		check("n=0", "0");
		check("Table(n= n + 1, {5})", //
				"{1,2,3,4,5}");
		check("Table(i, {i, 4})", //
				"{1,2,3,4}");
		check("Table(i, {i, 2, 5})", //
				"{2,3,4,5}");
		check("Table(i, {i, 2, 6, 2})", //
				"{2,4,6}");
		check("Table(i, {i, Pi, 2*Pi, Pi / 2})", //
				"{Pi,3/2*Pi,2*Pi}");
		check("Table(x^2, {x, {a, b, c}})", //
				"{a^2,b^2,c^2}");
		check("Table({i, j}, {i, {a, b}}, {j, 1, 2})", //
				"{{{a,1},{a,2}},{{b,1},{b,2}}}");
		check("Table(x, {x,0,1/3})", //
				"{0}");
		check("Table(x, {x, -0.2, 3.9})", //
				"{-0.2,0.8,1.8,2.8,3.8}");

		// check("Timing(Length(Table(i, {i, 1, 10000})))", "{0.159,10000}");
		check("Table(x,10)", //
				"{x,x,x,x,x,x,x,x,x,x}");
		check("Table(x,-1)", //
				"{}");
		check("Table(0,{4-1})", //
				"{0,0,0}");
		check("$a=10;Table($a^2, {$a, 10})", //
				"{1,4,9,16,25,36,49,64,81,100}");
		check("Table(f(a), {a, 0, 20, 2})", //
				"{f(0),f(2),f(4),f(6),f(8),f(10),f(12),f(14),f(16),f(18),f(20)}");
		check("Table(x, {10})", //
				"{x,x,x,x,x,x,x,x,x,x}");
		check("Table(10*a + j, {a, 4}, {j, 3})", //
				"{{11,12,13},{21,22,23},{31,32,33},{41,42,43}}");
		check("Table(f(a), {a, 10, -5, -2})", //
				"{f(10),f(8),f(6),f(4),f(2),f(0),f(-2),f(-4)}");
		check("Table(Sqrt(x), {x, {1, 4, 9, 16}})", //
				"{1,2,3,4}");
		check("Table(100*a + 10*j + k, {a, 3}, {j, 2}, {k, 4})", //
				"{{{111,112,113,114},{121,122,123,124}},{{211,212,213,214},{221,222,223,224}},{{\n"
						+ "311,312,313,314},{321,322,323,324}}}");
		check("Table(j^(1/a), {a, {1, 2, 4}}, {j, {1, 4, 9}})", //
				"{{1,4,9},{1,2,3},{1,Sqrt(2),Sqrt(3)}}");
		check("Table(2^x + x, {x, a, a + 5*b, b})", //
				"{2^a+a,2^(a+b)+a+b,2^(a+2*b)+a+2*b,2^(a+3*b)+a+3*b,2^(a+4*b)+a+4*b,2^(a+5*b)+a+5*b}");
		check("Table(a, {a, Pi, 2*Pi, Pi / 2})", //
				"{Pi,3/2*Pi,2*Pi}");

		check("b := 3 ; a := 1+b ; Length(Table(a*x^2,{x,-1.0,1.0605456,0.060606062},{y,-a+b^2,a+b^2,0.160606062}))", //
				"34");
	}

	public void testTableForm() {
		check("TableForm(Array(a, {2}))", //
				"a(1)\n" + "a(2)\n");
		check("TableForm(Array(a, {2,2}))", //
				"a(1,1) a(1,2)\n" + //
						"a(2,1) a(2,2)");
		// check("TableForm(RandomReal(5, {3, 4}))", //
		// "3.3966021212581032 0.37386691098759195 3.3714636398631437 4.888805919349176 \n" +
		// "3.977341548616806 0.7908229654057569 0.4052451831886883 1.455709563360077 \n" +
		// "1.0028599118204118 1.2764414981301953 4.024172644043268 1.3404548410990742");

	}

	public void testTake() {
		check("Take({a, b, c, d}, -2)", //
				"{c,d}");
		check("Take({a, b, c, d}, 3)", //
				"{a,b,c}");
		check("Take({a, b, c, d}, -2)", //
				"{c,d}");
		check("Take({a, b, c, d, e}, {2, -2})", //
				"{b,c,d}");

		check("A = {{a, b, c}, {d, e, f}}", //
				"{{a,b,c},{d,e,f}}");
		check("Take(A, 2, 2)", //
				"{{a,b},{d,e}}");
		check("Take(A, All, {2})", //
				"{{b},{e}}");

		check("Take(Range(10), {8, 2, -1})", //
				"{8,7,6,5,4,3,2}");
		check("Take(Range(10), {-3, -7, -2})", //
				"{8,6,4}");
		check("Take(Range(6), {-5, -2, -2})", //
				"Take(Range(6),{-5,-2,-2})");
		check("Take(l, {-1})", //
				"Take(l,{-1})");
		check("Take({1, 2, 3, 4, 5}, {-1, -2})", //
				"{}");
		check("Take({1, 2, 3, 4, 5}, {0, -1})", //
				"{}");
		check("Take({1, 2, 3, 4, 5}, {1, 0})", //
				"{}");
		check("Take({1, 2, 3, 4, 5}, {2, 1})", //
				"{}");
		check("Take({1, 2, 3, 4, 5}, {1, 0, 2})", //
				"{}");
		check("Take({1, 2, 3, 4, 5}, {1, 0, -1})", //
				"Take({1,2,3,4,5},{1,0,-1})");

		check("Take({a, b, c, d, e, f}, All)", //
				"{a,b,c,d,e,f}");
		check("Take({a, b, c, d, e, f}, 4)", //
				"{a,b,c,d}");
		check("Take({a, b, c, d, e, f}, -3)", //
				"{d,e,f}");
		check("Take({a, b, c, d, e, f}, {2,4})", //
				"{b,c,d}");
		check("Take({{11, 12, 13}, {21, 22, 23},{31, 32, 33}}, 2, 2)", //
				"{{11,12},{21,22}}");
		check("Take({{11, 12, 13}, {21, 22, 23},a,{31, 32, 33}}, 3, 2)", //
				"Take({{11,12,13},{21,22,23},a,{31,32,33}},3,2)");
		check("Take({a, b, c, d, e, f}, None)", //
				"{}");

	}

	public void testTally() {
		check("Tally({{a, b}, {w, x, y, z}, E, {w, x, y, z}, E}, Head(#1) === Head(#2) &)", //
				"{{{a,b},3},{E,2}}");
		check("Tally({a,a,b,a,c,b,a})", //
				"{{a,4},{b,2},{c,1}}");
		check("Tally({b,a,b,a,c,b,a})", //
				"{{b,3},{a,3},{c,1}}");
	}

	public void testTagSet() {
		check("f/: Format(f) = \"TagSet test\"", //
				"TagSet test");
		check("Format(f)", //
				"TagSet test");
	}

	public void testTagSetDelayed() {
		check("g/: Format(a_,g(x)) := \"TagSetDelayed test\"", //
				"");
		// check("Definition(g)", //
		// "TagSet test");
		check("Format(f,g(x))", //
				"TagSetDelayed test");
	}

	public void testTan() {
		check("Tan(Pi / 2.0)", //
				"1.63312*10^16");
		// TODO
		// check("Tan(z-Pi/3)", //
		// "-Cot(Pi/6+z)");
		check("Tan(e-Pi/2+f*x)", //
				"-Cot(e+f*x)");
		check("Tan(e+Pi/2+f*x)", //
				"-Cot(e+f*x)");
		check("Tan(Pi/2+Pi*n)", //
				"-Cot(n*Pi)");

		check("Tan(ArcSin(x))", //
				"x/Sqrt(1-x^2)");
		check("Tan(ArcCos(x))", //
				"Sqrt(1-x^2)/x");
		check("Tan(ArcTan(x))", //
				"x");
		check("Tan(ArcCot(x))", //
				"1/x");
		check("Tan(ArcCsc(x))", //
				"1/(Sqrt(1-1/x^2)*x)");
		check("Tan(ArcSec(x))", //
				"Sqrt(1-1/x^2)*x");

		check("Tan(0)", //
				"0");
		check("Tan(Pi / 2)", //
				"ComplexInfinity");
		checkNumeric("Tan(0.5*Pi)", //
				"1.633123935319537E16");

		check("Tan(Pi/2)", //
				"ComplexInfinity");
		check("Tan(1/6*Pi)", //
				"1/Sqrt(3)");
		check("Tan(Pi)", //
				"0");
		check("Tan(z+Pi)", //
				"Tan(z)");
		check("Tan(z+42*Pi)", //
				"Tan(z)");
		check("Tan(z+42*a*Pi)", //
				"Tan(42*a*Pi+z)");
		check("Tan(z+1/2*Pi)", //
				"-Cot(z)");
		check("Tan(Pi)", //
				"0");
		check("Tan(33*Pi)", //
				"0");
		check("Tan(z+Pi)", //
				"Tan(z)");
		check("Tan(z+42*Pi)", //
				"Tan(z)");
		check("Tan(x+y+z+43*Pi)", //
				"Tan(x+y+z)");
		check("Tan(z+42*a*Pi)", //
				"Tan(42*a*Pi+z)");
		check("Tan(z+4/3*Pi)", //
				"Tan(Pi/3+z)");
	}

	public void testTanh() {
		check("Tanh(0)", //
				"0");
	}

	public void testTautologyQ() {
		// https://en.wikipedia.org/wiki/Tautology_(logic)
		check("TautologyQ(a || !a)", //
				"True");

		check("TautologyQ((a || b) && (! a || ! b))", //
				"False");
		check("TautologyQ((a || b) || (! a && ! b))", //
				"True");
		check("TautologyQ((a || b) && (! a || ! b), {a, b})", //
				"False");
		check("TautologyQ((a || b) || (! a && ! b), {a, b})", //
				"True");
	}

	public void testTaylor() {
		check("Taylor(f(x),{x,a,2})", //
				"f(a)+(-a+x)*f'(a)+1/2*(-a+x)^2*f''(a)");
		check("Taylor(ArcSin(x),{x,0,10})", //
				"x+x^3/6+3/40*x^5+5/112*x^7+35/1152*x^9");
		check("Limit(ArcSin(x)/x,x->0)", //
				"1");
		check("(-0^2+1)^(-1/2)", //
				"1");
	}

	public void testTensorDimensions() {
		check("A=Array(a, {2, 3, 4});TensorDimensions(A)", //
				"{2,3,4}");
		check("TensorDimensions({{1,2},{3,4},{a,b}})", //
				"{3,2}");
	}

	// public void testTensorProduct() {
	// check("TensorProduct({2, 3}, {{a, b}, {c, d}}, {x, y})", "2");
	// }

	public void testTensorRank() {
		check("A=Array(a, {2, 3, 4});TensorRank(A)", //
				"3");
		check("TensorRank({{1,2},{3,4}})", //
				"2");

	}

	public void testTensorSymmetry() {
		check("TensorSymmetry({{a,b,c,d}, {b,e,f,g}, {c,f,h,i},{d,g,i,j}})", //
				"Symmetric({1,2})");
		check("TensorSymmetry({{0, a, b}, {-a, 0, c}, {-b, -c, 0}})", //
				"AntiSymmetric({1,2})");
		check("TensorSymmetry({{a}})", //
				"Symmetric({1,2})");
		check("TensorSymmetry({{0}})", //
				"ZeroSymmetric({})");
		check("TensorSymmetry({{0,0}, {0,0}})", //
				"ZeroSymmetric({})");
		check("TensorSymmetry({{a,b}, {b,c}})", //
				"Symmetric({1,2})");

	}

	public void testTeXForm() {
		check("TeXForm( f(#,#3)&  )", //
				"f(\\text{$\\#$1},\\text{$\\#$3})\\&");
		check("TeXForm( f(#,#3)*2&  )", //
				"f(\\text{$\\#$1},\\text{$\\#$3})\\,2\\&");
		check("TeXForm(N(1.1+Pi*I,30))", //
				"1.1 + 3.14159265358979323846264338327\\,i ");
		check("TeXForm(N(Pi,30))", //
				"3.14159265358979323846264338327");
		check("TeXForm(Infinity)", //
				"\\infty");
		check("TeXForm(-Infinity)", //
				"-\\infty");
		check("TeXForm(Hold(GoldenRatio))", //
				"\\text{Hold}(\\phi)");
		check("TeXForm(GoldenRatio)", //
				"\\phi");
		check("TeXForm(2+I*3)", //
				"2 + 3\\,i ");
		check("TeXForm(a+b^2)", //
				"a+{b}^{2}");
		check("TeXForm(Expand((x+y)^3))", //
				"{x}^{3} + 3\\,{x}^{2}\\,y + 3\\,x\\,{y}^{2}+{y}^{3}");
		check("TeXForm(3*a+b^2)", //
				"3\\,a+{b}^{2}");
		check("TeXForm(x/Sqrt(5))", //
				"\\frac{x}{\\sqrt{5}}");
		check("TeXForm(x^(1/3))", //
				"\\sqrt[3]{x}");
		check("TeXForm(alpha)", //
				"\\alpha");
		check("TeXForm({a,b,c})", //
				"\\{a,b,c\\}");
		check("TeXForm({{a,b},c})", //
				"\\{\\{a,b\\},c\\}");
		check("TeXForm({{a, b, c}, {d, e, f}})", //
				"\\{\\{a,b,c\\},\\{d,e,f\\}\\}");

		check("TeXForm(Integrate(f(x),x))", //
				"\\int  f(x)\\,\\mathrm{d}x");
		check("TeXForm(Limit(f(x), x ->Infinity))", //
				"\\lim_{x\\to {\\infty} }\\,{f(x)}");
		check("TeXForm(Sum(f(n), {n, 1, m}))", //
				"\\sum_{n = 1}^{m} {f(n)}");
		check("TeXForm(Product(f(n), {n, 1, m}))", //
				"\\prod_{n = 1}^{m} {f(n)}");
		check("TeXForm(Subscript(a,b))", //
				"{a}_{b}");
		check("TeXForm(Superscript(a,b))", //
				"{a}^{b}");
		check("TeXForm(Subscript(x,2*k+1))", //
				"{x}_{1 + 2\\,k}");
		check("TeXForm(Subsuperscript(a,b,c))", //
				"{a}_{b}^{c}");
		check("TeXForm(HarmonicNumber(n))", //
				"H_n");
		check("TeXForm(HarmonicNumber(m,n))", //
				"H_m^{(n)}");
		check("TeXForm(HurwitzZeta(m,n))", //
				"zeta (m,n)");
		check("TeXForm(Zeta(m,n))", //
				"zeta (m,n)");

		check("TeXForm(fgh(a,b))", //
				"\\text{fgh}(a,b)");
	}

	public void testTextString() {
		check("TextString(10) // InputForm", //
				"\"10\"");
	}

	public void testThread() {
		check("Thread(f({a, b, c}))", //
				"{f(a),f(b),f(c)}");
		check("Thread(f({a, b, c}, {x, y, z}))", //
				"{f(a,x),f(b,y),f(c,z)}");
		check("Thread(Log(x == y), Equal)", //
				"Log(x)==Log(y)");

		check("Thread(f({a, b, c}))", //
				"{f(a),f(b),f(c)}");
		check("Thread(f({a, b, c}, t))", //
				"{f(a,t),f(b,t),f(c,t)}");
		check("Thread(f(a + b + c), Plus)", //
				"f(a)+f(b)+f(c)");
		check("{a, b, c} + {d, e, f} + g", //
				"{a+d+g,b+e+g,c+f+g}");
	}

	public void testThrough() {

		check("Through(p(f, g)[])", //
				"p(f(),g())");
		check("Through(p(f,g)[x])", //
				"p(f(x),g(x))");
		check("Through(p(f,g)[])", //
				"p(f(),g())");
		check("Through(f()[x])", //
				"f()");
		check("Through(p(f,g))", //
				"p(f,g)");

		check("Through(p(f,g)[x,y])", //
				"p(f(x,y),g(x,y))");
		check("Through(f(g)[x])", //
				"f(g(x))");
		check("NestList(Through, f(a)[b][c][d], 3)", //
				"{f(a)[b][c][d],f(a)[b][c(d)],f(a)[b(c(d))],f(a(b(c(d))))}");
		check("Through( ((D(#, x) &) + (D(#, x, x) &))[f(x)] )", //
				"f'(x)+f''(x)");
		check("Through((f*g)[x,y],Plus)", //
				"(f*g)[x,y]");
		check("Through((f+g+h)[x,y],Plus)", //
				"f(x,y)+g(x,y)+h(x,y)");
	}

	public void testTimeConstrained() {
		if (!Config.JAS_NO_THREADS) {
			check("TimeConstrained(Do(i^2, {i, 10000000}), 1)", //
					"$Aborted");
		}
	}

	public void testTimes() {
		check("False*Log(x+y)", //
				"False*Log(x+y)");
		check("Csch(x)^3 * Sinh(x)^(-2)", //
				"Csch(x)^5");
		check("Csch(x)^3 * Sinh(x)^7", //
				"Sinh(x)^4");

		check("2*4^(1+p)", //
				"2^(3+2*p)");
		check("(2^(3/4)*x*7^(-15/4))", //
				"1/343*(2/7)^(3/4)*x");

		check("125*2^(2+3*b)", //
				"125*2^(2+3*b)");
		check("(1/2)*4^(1+p)", //
				"2^(1+2*p)");

		// same as in MMA
		check("-(1/3)*(2+n)", //
				"1/3*(-2-n)");
		check("-(2+n)", //
				"-2-n");
		check("-3*(2+n)", //
				"-3*(2+n)");
		check("2^(3+k)*4^(1+p)", //
				"2^(5+k+2*p)");
		check("2*2^(1+p)", //
				"2^(2+p)");
		check("(1/2)*4^(1+p)", //
				"2^(1+2*p)");
		check("-(-b*c+a*d)*n", //
				"(b*c-a*d)*n");
		check("5/7*Sqrt(7/6)", //
				"5/Sqrt(42)");
		check("(Sqrt(3)*x)/Sqrt(2)", //
				"Sqrt(3/2)*x");
		check("1/(sq(a)*sq(b))//FullForm", //
				"Times(Power(sq(a), -1), Power(sq(b), -1))");
		check("(-Infinity)/Sin(-2)", //
				"Infinity");
		check("(-Infinity)/(-2)", //
				"Infinity");
		check("(-Infinity)/Log(2)", //
				"-Infinity");
		check("(-Infinity)/2", //
				"-Infinity");

		// github #35
		check(" y^2*y^(-0.6666) ", //
				"y^1.3334");
		check(" y*y^(-0.6666) ", //
				"y^0.3334");

		check("x* y*y^(-0.6666) *z^(-1)", //
				"(x*y^0.3334)/z");
		check("5/7*Sqrt(7/6)", //
				"5/Sqrt(42)");

		check("Hold((-1)^(a) (b)) // FullForm", //
				"Hold(Times(Power(-1, a), b))");

		check("Sqrt(1/2)*(1+x)", //
				"(1+x)/Sqrt(2)");
		check("((5/21)^(2/3)*(7/6)^(2/7))  ", //
				"5^(2/3)/(2^(2/7)*3^(20/21)*7^(8/21))");
		check("x*y/(y*z)", //
				"x/z");
		check("x*y/(y^3*z)", //
				"x/(y^2*z)");
		check("x*y/(y^(2/3)*z)", //
				"(x*y^(1/3))/z");
		check("x*y/(y^(0.6666)*z) ", //
				"(x*y^0.3334)/z");
		check("N(Pi, 30) * I", //
				"I*3.14159265358979323846264338327");
		check("N(I*Pi, 30)", //
				"I*3.14159265358979323846264338327");
		check("N(Pi * E, 30)", //
				"8.53973422267356706546355086954");
		check("N(Pi, 30) * N(E, 30)", //
				"8.53973422267356706546355086954");
		check("N(Pi, 30) * E", //
				"8.53973422267356661130018539536");
		check("N(Pi, 30) * E // Precision", //
				"30");

		check("Floor(Log(7,1024))", //
				"3");
		check("10*2", //
				"20");
		check("a*a", //
				"a^2");
		check("x ^ 10 * x ^ -2", //
				"x^8");
		check("{1, 2, 3} * 4", //
				"{4,8,12}");
		check("Times @@ {1, 2, 3, 4}", //
				"24");
		check("IntegerLength(Times@@Range(100))", //
				"158");
		check("a /. n_. * x_ :> {n, x} ", //
				"{1,a}");
		check("-a*b // FullForm", //
				"Times(-1, a, b)");
		check("-(x - 2/3)", //
				"2/3-x");
		check("-(h/2) // FullForm", //
				"Times(Rational(-1,2), h)");
		check("x / x", //
				"1");
		check("2*x^2 / x^2", //
				"2");
		checkNumeric("3.*Pi", //
				"9.42477796076938");
		check("Head(3 * I) ", //
				"Complex");
		check("Head(Times(I, 1/2))", //
				"Complex");
		check("Head(Pi * I)", //
				"Times");
		checkNumeric("-2.123456789 * x", //
				"-2.123456789*x");
		checkNumeric("-2.123456789 * I", //
				"I*(-2.123456789)");

		// issue #137
		check("12*2^x*3^y", //
				"2^(2+x)*3^(1+y)");
		check("8*2^x", //
				"2^(3+x)");
		check("12*2^x", //
				"3*2^(2+x)");

		check("-Infinity", //
				"-Infinity");
		check("Times(I*Sqrt(2), I*Sqrt(3))", //
				"-Sqrt(6)");
		check("Sin(x)^(-2)/Tan(x)", //
				"Cot(x)*Csc(x)^2");
		check("Sin(x)/Tan(x)", //
				"Cos(x)");
		check("Sin(x)^2/Tan(x)^3", //
				"Cos(x)^2*Cot(x)");
		check("Sin(x)^3/Tan(x)^2", //
				"Cos(x)^2*Sin(x)");
		check("Sin(x)^2/Tan(x)", //
				"Cos(x)*Sin(x)");
		check("Sin(x)/Tan(x)^2", //
				"Cos(x)*Cot(x)");

		check("Sin(x)^(-2)", //
				"Csc(x)^2");
		check("Sin(x)/Tan(x)^(-2)", //
				"Sin(x)*Tan(x)^2");
		check("Sin(x)/Cos(x)", //
				"Tan(x)");
		check("Cos(x)*Tan(x)", //
				"Sin(x)");
		check("Cos(x)/Sin(x)", //
				"Cot(x)");
		check("Tan(x)/Sin(x)", //
				"Sec(x)");

		check("Times()", //
				"1");
		// OutputForm: I*Infinity is DirectedInfinity[I]
		check("I*Infinity", //
				"I*Infinity");

		check("Gamma(a)*Gamma(1-a)", //
				"Pi*Csc(a*Pi)");
		check("Gamma(a)^3*Gamma(1-a)^3", //
				"Pi^3*Csc(a*Pi)^3");
		check("Gamma(a)^3*Gamma(1-a)^2", //
				"Pi^2*Csc(a*Pi)^2*Gamma(a)");
	}

	public void testTimesBy() {
		check("a = 10", //
				"10");
		check("a *= 2", //
				"20");
		check("a", //
				"20");
	}

	public void testTimeValue() {
		if (ToggleFeature.FINANCE) {
			check("TimeValue(Annuity(100, 12), .01, 0)", //
					"1125.508");
			check("TimeValue(Annuity(100, 12), EffectiveInterest(.01, 0.25), 12)", //
					"1268.515");
			check("TimeValue(AnnuityDue(100, 12), 0.1, 0)", //
					"749.5061");

			check("TimeValue(Annuity(500,36,q), b, c)", //
					"(500*(-1+((1+b)^q)^(36/q)))/((-1+(1+b)^q)*((1+b)^q)^(36/q-c/q))");
			check("TimeValue(AnnuityDue(500,36,q), b, c)", //
					"(500*(-1+((1+b)^q)^(36/q))*((1+b)^q)^(1-36/q+c/q))/(-1+(1+b)^q)");
			check("TimeValue(Annuity(100, 12), 6/100, 0)", //
					"411863798761210257735000/491258904256726154641");
			check("TimeValue(a,b,c)", //
					"a*(1+b)^c");
			check("TimeValue(100, EffectiveInterest(.06, 1/12), 10)", //
					"181.9397");
			check("TimeValue(Annuity(1000, 10), .06, 0)", //
					"7360.087");
			check("TimeValue(Annuity(1000, 5), EffectiveInterest(.08, 1/4), 5)", //
					"5895.119");
			check("TimeValue(AnnuityDue(100, 12), 6/100, 0)", //
					"8237275975224205154700/9269035929372191597");
			check("TimeValue(AnnuityDue(1000, 10), .06, 0)", //
					"7801.692");
			check("TimeValue(AnnuityDue(1000, 5), EffectiveInterest(.08, 1/4), 5)", //
					"6381.066");
		}
	}

	public void testToeplitzMatrix() {
		check("ToeplitzMatrix({1, 2, 3, 4, 5, 6}, {1, a, b, c})", //
				"{{1,a,b,c},\n" + //
						" {2,1,a,b},\n" + //
						" {3,2,1,a},\n" + //
						" {4,3,2,1},\n" + //
						" {5,4,3,2},\n" + //
						" {6,5,4,3}}");

		check("ToeplitzMatrix({1, a, b, c}, {1, 2, 3, 4, 5, 6})", //
				"{{1,2,3,4,5,6},\n" + //
						" {a,1,2,3,4,5},\n" + //
						" {b,a,1,2,3,4},\n" + //
						" {c,b,a,1,2,3}}");

		check("ToeplitzMatrix(-3)", //
				"ToeplitzMatrix(-3)");
		check("ToeplitzMatrix(3)", //
				"{{1,2,3},\n" + " {2,1,2},\n" + " {3,2,1}}");
		check("ToeplitzMatrix({a,b,c,d})", //
				"{{a,b,c,d},\n" + " {b,a,b,c},\n" + " {c,b,a,b},\n" + " {d,c,b,a}}");
		check("ToeplitzMatrix(4)", //
				"{{1,2,3,4},\n" + " {2,1,2,3},\n" + " {3,2,1,2},\n" + " {4,3,2,1}}");
	}

	public void testTogether() {

		// TODO ((1+x)*f(x))/x^2
		check("f(x)/x+f(x)/x^2//Together", //
				"(f(x)+x*f(x))/x^2");
		check("together(x^2/(x^2 - 1) + x/(x^2 - 1))", //
				"x/(-1+x)");
		check("Together[(2 + 2*x)/(2*Sqrt[2])]", //
				"(1+x)/Sqrt(2)");

		// check("Together(-(5*c*f+5*b*g+2*c*f*m+b*g*m+2*c*f*n+3*b*g*n+30*c*g*x+10*c*g*m*x+15*c*g*n*x)^5/(3125*c^4*g^4*(6+2*m+3*n)^4))",
		// //
		// "");

		check("Together[(x+2*Sqrt(x)+1)/(1+Sqrt(x))]", //
				"1+Sqrt(x)");
		check("Together(-a/(-(b-a*c)))", //
				"-a/(-b+a*c)");
		check("Together(Simplify(Together(-a/(-(b-a*c)))))", //
				"a/(b-a*c)");
		check("Together(1/2+I/3 + 3*a^(-1))", //
				"(18+(3+I*2)*a)/(6*a)");
		check("Together(1/2 + 3/a )", //
				"(6+a)/(2*a)");
		check("Together[(2 + 2*x)/(2*Sqrt[2])]", //
				"(1+x)/Sqrt(2)");

		check("Together((1+a/(c+d)+b/(c+d))/(a+b))", //
				"(a+b+c+d)/((a+b)*(c+d))");

		check("Together((a+b*x+c*x^2)^13/13)", //
				"(a+b*x+c*x^2)^13/13");
		check("Together(1/(a + b) + 1/(c + d) - a)", //
				"(a+b+c-a^2*c-a*b*c+d-a^2*d-a*b*d)/(a*c+b*c+a*d+b*d)");
		check("Together(1/a+1/b)", //
				"(a+b)/(a*b)");
		check("Together(1/a+1/b+c)", //
				"(a+b+a*b*c)/(a*b)");
		check("Together(1/c+d)", //
				"(1+c*d)/c");
		check("Together(1/2+a)", //
				"1/2*(1+2*a)");
		check("Together((1+a/(c+d)+b/(c+d))/(a+b))", //
				"(a+b+c+d)/((a+b)*(c+d))");
		check("Together(1/a+1/b+c+d)", //
				"(a+b+a*b*c+a*b*d)/(a*b)");
		check("Together(2*a+2*b)", //
				"2*(a+b)");

		check("Together(-(a^2-c^2)/(a*b-b*c))", //
				"-(a+c)/b");

		check("Together(1/Sqrt(1+1/x) )", //
				"Sqrt(x/(1+x))");
		check("Together(1/Sqrt(1+1/x)+(1+1/x)^(3/2))", //
				"(Sqrt((1+x)/x)+x*Sqrt((1+x)/x)+x*Sqrt(x/(1+x)))/x");

		check("Together(1/(2*Sqrt(3))+Sqrt(3)/2)", //
				"2/Sqrt(3)");
		check("Together(1/2-Sqrt(5)/2+2/(1+Sqrt(5)))", //
				"0");
		// check("Together(1/Sqrt(1+1/x) + (1+1/x)^(3/2) )", " ");

		check("Together(1+1/(1+1/x))", //
				"(1+2*x)/(1+x)");

		check("Together(a/b+c/d)", //
				"(b*c+a*d)/(b*d)");
		// TODO return {x (2 + y) / (1 + y) ^ 2}
		check("Together({x / (y+1) + x / (y+1)^2})", //
				"{(2*x+x*y)/(1+2*y+y^2)}");

		check("Together(a / c + b / c)", //
				"(a+b)/c");

		check("Together(f(a / c + b / c))", //
				"f(a/c+b/c)");

		check("f(x)/x+f(x)/x^2//Together", //
				"(f(x)+x*f(x))/x^2");

		check("Together(1 < 1/x + 1/(1 + x) < 2)", //
				"1<(1+2*x)/(x+x^2)<2");
		check("Together(1/(1+1/(1+1/a)))", //
				"(1+a)/(1+2*a)");
		check("Together(1/(1+1/(1+1/(1+a))))", //
				"(2+a)/(3+2*a)");
		check("ExpandAll(a*b)", //
				"a*b");
		check("ExpandAll(a*b^(-1))", //
				"a/b");
		check("(a*b)^(-1)", //
				"1/(a*b)");
		check("Together(a/b + c/d)", //
				"(b*c+a*d)/(b*d)");
		check("Together((-7*a^(-1)*b+1)*(-a^(-1)*b-1)^(-1))", //
				"(a-7*b)/(-a-b)");
		check("Together(a*b^(-2)+c*d^(-3))", //
				"(b^2*c+a*d^3)/(b^2*d^3)");
		check("Together(-a*b^(-2)-c*d^(-3))", //
				"(-b^2*c-a*d^3)/(b^2*d^3)");
		check("Together((-8)*a^(-1)*(-a^(-1)*b-1)^(-1))", //
				"-8/(-a-b)");

		check("Together((2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1)-4*x*(2*x^3-4*x+5)*(3*x^2+2)^(-1)+5*(2*x^3-4*x+\n"
				+ "5)*(3*x^2+2)^(-1)-4*x^4+8*x^2-10*x)*(3*x^2+2)^(-1)+x^2-2)", //
				"(17-60*x+12*x^2-10*x^3-6*x^4+x^6)/(4+12*x^2+9*x^4)");
		check("Together((4*x^6-8*x^4+10*x^3)*(3*x^2+2)^(-1)+(-8*x^4+16*x^2-20*x)*(3*x^2+2)^(-1)+(10*x^3\n"
				+ "-20*x+25)*(3*x^2+2)^(-1)-4*x^4+8*x^2-10*x)", //
				"(25-60*x+32*x^2-10*x^3-8*x^6)/(2+3*x^2)");
		check("Together((2*(2*x^3-4*x+5)*x^3*(3*x^2+2)^(-1)-4*x*(2*x^3-4*x+5)*(3*x^2+2)^(-1)+5*(2*x^3-4*x+\n"
				+ "5)*(3*x^2+2)^(-1)-4*x^4+8*x^2-10*x)*(x)^(-1)-2)", //
				"(25-64*x+32*x^2-16*x^3-8*x^6)/(2*x+3*x^3)");
		check("Together(a + c/g)", //
				"(c+a*g)/g");
		check("Together(((7*b*a^(-1)-1)*(-b*a^(-1)-1)^(-1)+7)*a^(-1))", //
				"-8/(-a-b)");

		check("ExpandAll((x^2-1)*x^2+x*(x^2-1))", //
				"-x-x^2+x^3+x^4");
		check("together(x^2/(x^2 - 1) + x/(x^2 - 1))", //
				"x/(-1+x)");
		check("Together((1/3*x-1/6)*(x^2-x+1)^(-1))", //
				"(-1+2*x)/(6*(1-x+x^2))");
		check("Together((-a^2*r^2*x-a*b*r*x-a*c*x-a^2*r^3-2*a*b*r^2-a*c*r-b^2*r-b*c)*((a*r^2+b*r)^2+2*c*(a*r^2+b*r)+c^2)^(-1))", //
				"(-b-a*r-a*x)/(c+b*r+a*r^2)");
		check("Together((-8)*a^(-1)*(-a^(-1)*b-1)^(-1))", //
				"-8/(-a-b)");

		check("Together(a/b + c/d)", //
				"(b*c+a*d)/(b*d)");
		check("Together((-a^(-1)*b-1))", //
				"(-a-b)/a");
		check("((-b-a)*a^(-1))^(-1)", //
				"a/(-a-b)");
		check("Together(1/x + 1/(x + 1) + 1/(x + 2) + 1/(x + 3))", //
				"(6+22*x+18*x^2+4*x^3)/(6*x+11*x^2+6*x^3+x^4)");

	}

	public void testToExpression() {
		check("ToExpression(\"\\\\frac{x}{\\\\sqrt{5}}\", TeXForm)", //
				"x/Sqrt(5)");
		check("ToExpression(\"1 + 2 - x \\\\times 4 \\\\div 5\", TeXForm)", //
				"3-4/5*x");
	}

	public void testToPolarCoordinates() {
		check("-Pi/2 < 0", //
				"True");
		check("Arg(1)", //
				"0");
		check("-Pi/2 < Arg(1) ", //
				"True");
		check(" Arg(1) <= Pi/2", //
				"True");

		check("ToPolarCoordinates({x, y})", //
				"{Sqrt(x^2+y^2),ArcTan(x,y)}");
		check("ToPolarCoordinates({1, 1})", //
				"{Sqrt(2),Pi/4}");
		check("ToPolarCoordinates({x, y, z})", //
				"{Sqrt(x^2+y^2+z^2),ArcCos(x/Sqrt(x^2+y^2+z^2)),ArcTan(y,z)}");
		check("ToPolarCoordinates({{{x, y}, {1, 0}}, {{-2, 0}, {0, 1}}})", //
				"{{{Sqrt(x^2+y^2),ArcTan(x,y)},{1,0}},{{2,Pi},{1,Pi/2}}}");
		check("ToPolarCoordinates({{{1, -1}}})", //
				"{{{Sqrt(2),-Pi/4}}}");
		check("ToPolarCoordinates({{} , {}})", //
				"{{},{}}");
	}

	public void testToRadicals() {
		check("ToRadicals(Root((#^2 - 3*# - 1)&, 2))", //
				"3/2+Sqrt(13)/2");
		check("ToRadicals(Root((-3*#-1)&, 1))", //
				"-1/3");
		check("ToRadicals(Sin(Root((#^7-#^2-#+a)&, 1)))", //
				"Sin(Root(-#1-#1^2+#1^7+a&,1))");
		check("ToRadicals(Root((#^7-#^2-#+a)&, 1)+Root((#^6-#^2-#+a)&, 1))", //
				"Root(-#1-#1^2+#1^6+a&,1)+Root(-#1-#1^2+#1^7+a&,1)");
		check("ToRadicals(Root((#^3-#^2-#+a)&, 1))", //
				"1/3+4/3*2^(1/3)/(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3)+(11+Sqrt(-256+(11-27*a)^2)\n"
						+ "-27*a)^(1/3)/(3*2^(1/3))");
		check("ToRadicals(Root((#^3-#^2-#+a)&, 2))", //
				"1/3+4/3*(2^(1/3)*(-1/2-I*1/2*Sqrt(3)))/(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3)+((\n"
						+ "-1/2+I*1/2*Sqrt(3))*(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3))/(3*2^(1/3))");
		check("ToRadicals(Root((#^3-#^2-#+a)&, 3))", //
				"1/3+4/3*(2^(1/3)*(-1/2+I*1/2*Sqrt(3)))/(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3)+((\n"
						+ "-1/2-I*1/2*Sqrt(3))*(11+Sqrt(-256+(11-27*a)^2)-27*a)^(1/3))/(3*2^(1/3))");
	}

	public void testToString() {
		check("Outer((ToString(#1) <> \"(\" <> ToString(#2) <> \"(x)) == \" <> ToString(InputForm(#1(#2(x)))))&," //
				+ "{Sinh,Cosh,Tanh,Coth,Sech,Csch},"//
				+ "{ArcSinh, ArcCosh, ArcTanh, ArcCoth, ArcSech, ArcCsch}) //" //
				+ "Flatten // TableForm ", //
				"Sinh(ArcSinh(x)) == x\n" + "Sinh(ArcCosh(x)) == Sqrt(-1+x)*Sqrt(1+x)\n"
						+ "Sinh(ArcTanh(x)) == x/Sqrt(1-x^2)\n" + "Sinh(ArcCoth(x)) == 1/(Sqrt(-1+x)*Sqrt(1+x))\n"
						+ "Sinh(ArcSech(x)) == Sqrt(-1+1/x)*Sqrt(1+1/x)\n" + "Sinh(ArcCsch(x)) == 1/x\n"
						+ "Cosh(ArcSinh(x)) == Sqrt(1+x^2)\n" + "Cosh(ArcCosh(x)) == x\n"
						+ "Cosh(ArcTanh(x)) == 1/Sqrt(1-x^2)\n" + "Cosh(ArcCoth(x)) == 1/Sqrt(1-1/x^2)\n"
						+ "Cosh(ArcSech(x)) == 1/x\n" + "Cosh(ArcCsch(x)) == Sqrt(1+1/x^2)\n"
						+ "Tanh(ArcSinh(x)) == x/Sqrt(1+x^2)\n" + "Tanh(ArcCosh(x)) == (Sqrt(-1+x)*Sqrt(1+x))/x\n"
						+ "Tanh(ArcTanh(x)) == x\n" + "Tanh(ArcCoth(x)) == 1/x\n"
						+ "Tanh(ArcSech(x)) == Sqrt(-1+1/x)*Sqrt(1+1/x)*x\n"
						+ "Tanh(ArcCsch(x)) == 1/(Sqrt(1+1/x^2)*x)\n" + "Coth(ArcSinh(x)) == Sqrt(1+x^2)/x\n"
						+ "Coth(ArcCosh(x)) == x/(Sqrt(-1+x)*Sqrt(1+x))\n" + "Coth(ArcTanh(x)) == 1/x\n"
						+ "Coth(ArcCoth(x)) == x\n" + "Coth(ArcSech(x)) == 1/(Sqrt(-1+1/x)*Sqrt(1+1/x)*x)\n"
						+ "Coth(ArcCsch(x)) == Sqrt(1+1/x^2)*x\n" + "Sech(ArcSinh(x)) == 1/Sqrt(1+x^2)\n"
						+ "Sech(ArcCosh(x)) == 1/x\n" + "Sech(ArcTanh(x)) == Sqrt(1-x^2)\n"
						+ "Sech(ArcCoth(x)) == (Sqrt(-1+x)*Sqrt(1+x))/x\n" + "Sech(ArcSech(x)) == x\n"
						+ "Sech(ArcCsch(x)) == 1/Sqrt(1+1/x^2)\n" + "Csch(ArcSinh(x)) == 1/x\n"
						+ "Csch(ArcCosh(x)) == 1/(Sqrt(-1+x)*Sqrt(1+x))\n"
						+ "Csch(ArcTanh(x)) == (Sqrt(1-x)*Sqrt(1+x))/x\n" + "Csch(ArcCoth(x)) == Sqrt(-1+x)*Sqrt(1+x)\n"
						+ "Csch(ArcSech(x)) == x/((1+x)*Sqrt((1-x)/(1+x)))\n" + "Csch(ArcCsch(x)) == x\n" + "");
		check("Outer((ToString(#1) <> \"(\" <> ToString(#2) <> \"(x)) == \" <> ToString(InputForm(#1(#2(x)))))&," //
				+ "{Sin,Cos,Tan,Cot,Sec,Csc},"//
				+ "{ArcSin, ArcCos, ArcTan, ArcCot, ArcSec, ArcCsc}) //" //
				+ "Flatten // TableForm ", //
				"Sin(ArcSin(x)) == x\n" + //
						"Sin(ArcCos(x)) == Sqrt(1-x^2)\n" + //
						"Sin(ArcTan(x)) == x/Sqrt(1+x^2)\n" + //
						"Sin(ArcCot(x)) == 1/Sqrt(1+x^2)\n" + //
						"Sin(ArcSec(x)) == Sqrt(1-1/x^2)\n" + //
						"Sin(ArcCsc(x)) == 1/x\n" + //
						//
						"Cos(ArcSin(x)) == Sqrt(1-x^2)\n" + //
						"Cos(ArcCos(x)) == x\n" + //
						"Cos(ArcTan(x)) == 1/Sqrt(1+x^2)\n" + //
						"Cos(ArcCot(x)) == 1/Sqrt(1+1/x^2)\n" + //
						"Cos(ArcSec(x)) == 1/x\n" + //
						"Cos(ArcCsc(x)) == Sqrt(1-1/x^2)\n" + //
						//
						"Tan(ArcSin(x)) == x/Sqrt(1-x^2)\n" + //
						"Tan(ArcCos(x)) == Sqrt(1-x^2)/x\n" + //
						"Tan(ArcTan(x)) == x\n" + //
						"Tan(ArcCot(x)) == 1/x\n" + //
						"Tan(ArcSec(x)) == Sqrt(1-1/x^2)*x\n" + //
						"Tan(ArcCsc(x)) == 1/(Sqrt(1-1/x^2)*x)\n" + //
						//
						"Cot(ArcSin(x)) == Sqrt(1-x^2)/x\n" + //
						"Cot(ArcCos(x)) == x/Sqrt(1-x^2)\n" + //
						"Cot(ArcTan(x)) == 1/x\n" + //
						"Cot(ArcCot(x)) == x\n" + //
						"Cot(ArcSec(x)) == 1/(Sqrt(1-1/x^2)*x)\n" + //
						"Cot(ArcCsc(x)) == Sqrt(1-1/x^2)*x\n" + //
						//
						"Sec(ArcSin(x)) == 1/Sqrt(1-x^2)\n" + //
						"Sec(ArcCos(x)) == 1/x\n" + //
						"Sec(ArcTan(x)) == Sqrt(1+x^2)\n" + //
						"Sec(ArcCot(x)) == Sqrt(1+x^2)/x\n" + //
						"Sec(ArcSec(x)) == x\n" + //
						"Sec(ArcCsc(x)) == 1/Sqrt(1-1/x^2)\n" + //
						//
						"Csc(ArcSin(x)) == 1/x\n" + //
						"Csc(ArcCos(x)) == 1/Sqrt(1-x^2)\n" + //
						"Csc(ArcTan(x)) == Sqrt(1+x^2)/x\n" + //
						"Csc(ArcCot(x)) == Sqrt(1+x^2)\n" + //
						"Csc(ArcSec(x)) == 1/Sqrt(1-1/x^2)\n" + //
						"Csc(ArcCsc(x)) == x\n" + //
						"");
		check("ToString(InputForm(a+\"b\"))", //
				"\"b\"+a");
		check("ToString(InputForm(d/2+f(x)))", //
				"d/2+f(x)");
		check("ToString(FullForm(d/2))", //
				"Times(Rational(1,2), d)");
	}

	public void testTotal() {
		check("Total({x^2, 3*x^3, 1})", //
				"1+x^2+3*x^3");

		check("Total(f(1,2,1))", //
				"4");
		check("Total(Derivative(1, 2, 1))", //
				"4");

		check("Total( {{1, 2, 3}, {4, 5}, {6}})", //
				"Total({{1,2,3},{4,5},{6}})");
		check("Total( {{1, 2, 3}, {4, 5}, {6}}, Infinity)", //
				"21");
		check("Total( {{1, 2, 3}, {4, 5}, {6}}, {-1})", //
				"{6,9,6}");

		check("Total({1, 2, 3})", //
				"6");
		check("Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}})", //
				"{12,15,18}");
		check("Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}}, 2)", //
				"45");
		check("Total({{1, 2, 3}, {4, 5, 6}, {7, 8 ,9}}, {2})", //
				"{6,15,24}");

		check("Total({x^2, 3*x^3, 1},{1})", //
				"1+x^2+3*x^3");
		check("Total({x^2, 3*x^3, 1})", //
				"1+x^2+3*x^3");
		check("Total({{1,2,3},{4,5,6},{7,8,9}})", //
				"{12,15,18}");
		check("Total({{1,2,3},{4,5,6},{7,8,9}},{1})", //
				"{12,15,18}");
		// total the rows
		check("Total({{1,2,3},{4,5,6},{7,8,9}},{2})", //
				"{6,15,24}");
		check("Total({{1,2,3},{4,5,6},{7,8,9}},2)", //
				"45");
	}

	public void testTranspose() {
		check("Transpose({{1, 2, 3}, {4, 5, 6}}, {2,1})", //
				"{{1,4},{2,5},{3,6}}");
		check("Transpose({{1, 2, 3}, {4, 5, 6}}, {1,2})", //
				"{{1,2,3},{4,5,6}}");

		check("m = Array(a, {2, 3, 2})", //
				"{{{a(1,1,1),a(1,1,2)},{a(1,2,1),a(1,2,2)},{a(1,3,1),a(1,3,2)}},{{a(2,1,1),a(2,1,\n"
						+ "2)},{a(2,2,1),a(2,2,2)},{a(2,3,1),a(2,3,2)}}}");
		check("Transpose(m, {1,3,2})",
				"{{{a(1,1,1),a(1,2,1),a(1,3,1)},{a(1,1,2),a(1,2,2),a(1,3,2)}},{{a(2,1,1),a(2,2,1),a(\n"
						+ "2,3,1)},{a(2,1,2),a(2,2,2),a(2,3,2)}}}");
		check("Transpose(m, {3,2,1})", //
				"{{{a(1,1,1),a(2,1,1)},{a(1,2,1),a(2,2,1)},{a(1,3,1),a(2,3,1)}},{{a(1,1,2),a(2,1,\n"
						+ "2)},{a(1,2,2),a(2,2,2)},{a(1,3,2),a(2,3,2)}}}");
		check("Transpose(m, {2,1,3})", //
				"{{{a(1,1,1),a(1,1,2)},{a(2,1,1),a(2,1,2)}},{{a(1,2,1),a(1,2,2)},{a(2,2,1),a(2,2,\n"
						+ "2)}},{{a(1,3,1),a(1,3,2)},{a(2,3,1),a(2,3,2)}}}");
		check("Transpose({{1, 2, 3}, {4, 5, 6}})", //
				"{{1,4},\n" + " {2,5},\n" + " {3,6}}");
	}

	public void testTreeForm() {
		check("TreeForm(a+b)", //
				"JSFormData(var nodes = new vis.DataSet([\n" + //
						"  {id: 1, label: 'Plus', level: 0}\n" + //
						", {id: 2, label: 'a', level: 1}\n" + //
						", {id: 3, label: 'b', level: 1}\n" + //
						"]);\n" + //
						"var edges = new vis.DataSet([\n" + //
						"  {from: 1, to: 2 , arrows: { to: { enabled: true, type: 'arrow'}}}\n" + //
						", {from: 1, to: 3 , arrows: { to: { enabled: true, type: 'arrow'}}}\n" + //
						"]);\n" + //
						",treeform)");
	}

	public void testTr() {
		check("Tr({1,2,3})", //
				"6");
		check("Tr({{}})", //
				"0");
		check("Tr({{1, 2}, {4, 5}, {7, 8}})", //
				"6");
		check("Tr({{1, 2, 3}, {4, 5, 6} })", //
				"6");
		check("Tr({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
				"15");
		check("Tr({{a, b, c}, {d, e, f}, {g, h, i}})", //
				"a+e+i");
		check("Tr({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, f)", //
				"f(1,5,9)");
	}

	public void testTrace() {
		check("Trace(u = 2; Do(u = u*u, {3}); u, Times)", //
				"{{{{u*u,2*2}},{{u*u,4*4}},{{u*u,16*16}}}}");
		check("x=5;Trace(Mod((3 + x)^2, x - 1))", //
				"{{{{x,5},3+5,8},8^2,64},{{x,5},-1+5,4},Mod(64,4),0}");
		check("Trace(u = 2; Do(u = u*u, {3}); u)", //
				"{{u=2,2},{{{{u,2},{u,2},2*2,4},4},{{{u,4},{u,4},4*4,16},16},{{{u,16},{u,16},16*\n"
						+ "16,256},256},Null},{u,256},256}");

	}

	public void testTrigExpand() {
		check("TrigExpand( Csch(2*x) )", //
				"1/2*Csch(x)*Sech(x)");
		check("TrigExpand( Csch(3*x) )", //
				"Csch(x)/(-1+4*Cosh(x)^2)");
		check("TrigExpand( Csch(4*x) )", //
				"Csch(x)/(-4*Cosh(x)+8*Cosh(x)^3)");
		check("TrigExpand(Cosh(x)*Csch(2*x)*Sinh(x))", //
				"1/2");
		check("TrigExpand(Cos(x)*Csc(2*x)*Sin(x))", //
				"1/2");
		check("TrigExpand(Cot(2*x))", //
				"Cot(x)/2-Tan(x)/2");

		check("TrigExpand(Cosh(2*a))", //
				"Cosh(a)^2+Sinh(a)^2");
		check("TrigExpand(Cosh(3*a))", //
				"Cosh(a)^3+3*Cosh(a)*Sinh(a)^2");

		check("TrigExpand(Cosh(a+b))", //
				"Cosh(a)*Cosh(b)+Sinh(a)*Sinh(b)");
		check("TrigExpand(Cosh(a+b+c))", //
				"Cosh(a)*Cosh(b)*Cosh(c)+Cosh(c)*Sinh(a)*Sinh(b)+Cosh(b)*Sinh(a)*Sinh(c)+Cosh(a)*Sinh(b)*Sinh(c)");

		check("TrigExpand(Sinh(2*a))", //
				"2*Cosh(a)*Sinh(a)");
		check("TrigExpand(Sinh(3*a))", //
				"3*Cosh(a)^2*Sinh(a)+Sinh(a)^3");
		check("TrigExpand(Sinh(4*a))", //
				"4*Cosh(a)^3*Sinh(a)+4*Cosh(a)*Sinh(a)^3");

		check("TrigExpand(Sinh(a+b))", //
				"Cosh(b)*Sinh(a)+Cosh(a)*Sinh(b)");
		check("TrigExpand(Sinh(a+b+c))", //
				"Cosh(b)*Cosh(c)*Sinh(a)+Cosh(a)*Cosh(c)*Sinh(b)+Cosh(a)*Cosh(b)*Sinh(c)+Sinh(a)*Sinh(b)*Sinh(c)");

		check("TrigExpand(Tanh(a+b))", //
				"Tanh(a)/(1+Tanh(a)*Tanh(b))+Tanh(b)/(1+Tanh(a)*Tanh(b))");
		check("TrigExpand(Tanh(a+b+c))", //
				"Tanh(a)/(1+(Tanh(a)*Tanh(b))/(1+Tanh(b)*Tanh(c))+(Tanh(a)*Tanh(c))/(1+Tanh(b)*Tanh(c)))+Tanh(b)/((\n"
						+ "1+Tanh(b)*Tanh(c))*(1+(Tanh(a)*Tanh(b))/(1+Tanh(b)*Tanh(c))+(Tanh(a)*Tanh(c))/(1+Tanh(b)*Tanh(c))))+Tanh(c)/((\n"
						+ "1+Tanh(b)*Tanh(c))*(1+(Tanh(a)*Tanh(b))/(1+Tanh(b)*Tanh(c))+(Tanh(a)*Tanh(c))/(1+Tanh(b)*Tanh(c))))");

		check("TrigExpand(Csc(a+b+c))", //
				"1/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))");
		check("TrigExpand(Sec(a+b+c))", //
				"1/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))");
		check("TrigExpand(Cot(a+b+c))", //
				"(Cos(a)*Cos(b)*Cos(c))/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))+(-Cos(c)*Sin(a)*Sin(b))/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))+(-Cos(b)*Sin(a)*Sin(c))/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))+(-Cos(a)*Sin(b)*Sin(c))/(Cos(b)*Cos(c)*Sin(a)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)-Sin(a)*Sin(b)*Sin(c))");
		check("TrigExpand(Tan(a+b+c))", //
				"(Cos(b)*Cos(c)*Sin(a))/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))+(Cos(a)*Cos(c)*Sin(b))/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))+(Cos(a)*Cos(b)*Sin(c))/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))+(-Sin(a)*Sin(b)*Sin(c))/(Cos(a)*Cos(b)*Cos(c)-Cos(c)*Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c))");

		check("TrigExpand(Csc(2*x))", //
				"1/2*Csc(x)*Sec(x)");
		check("TrigExpand(Sec(2*x))", //
				"1/(Cos(x)^2-Sin(x)^2)");
		check("TrigExpand(Cot(2*x))", //
				"Cot(x)/2-Tan(x)/2");
		check("TrigExpand(Tan(2*x))", //
				"(2*Cos(x)*Sin(x))/(Cos(x)^2-Sin(x)^2)");
		check("TrigExpand(Sin(2*x+3*y))", //
				"2*Cos(x)*Cos(y)^3*Sin(x)+3*Cos(x)^2*Cos(y)^2*Sin(y)-3*Cos(y)^2*Sin(x)^2*Sin(y)-6*Cos(x)*Cos(y)*Sin(x)*Sin(y)^\n"
						+ "2-Cos(x)^2*Sin(y)^3+Sin(x)^2*Sin(y)^3");
		check("trigexpand(Sin(2*x))", //
				"2*Cos(x)*Sin(x)");
		check("trigexpand(Sin(x)*Tan(x))", //
				"Sin(x)*Tan(x)");
		check("trigexpand(Sin(x + y))", //
				"Cos(y)*Sin(x)+Cos(x)*Sin(y)");
		check("trigexpand(Cos(x + y))", //
				"Cos(x)*Cos(y)-Sin(x)*Sin(y)");
		check("trigexpand(Sin(x + y + z))", //
				"Cos(y)*Cos(z)*Sin(x)+Cos(x)*Cos(z)*Sin(y)+Cos(x)*Cos(y)*Sin(z)-Sin(x)*Sin(y)*Sin(z)");
		check("trigexpand(Cos(2*x))", //
				"Cos(x)^2-Sin(x)^2");
		check("trigexpand(Sin(5*x))", //
				"5*Cos(x)^4*Sin(x)-10*Cos(x)^2*Sin(x)^3+Sin(x)^5");
	}

	public void testTrigReduce() {
		// check("TrigReduce(Tan(x)^2)", //
		// "(1-Cos(2*x))/(1+Cos(2*x))");
		// check("TrigReduce(Tan(x)^3)", //
		// "");
		check("TrigReduce(Sin(x)*Cos(y))", //
				"1/2*(Sin(x-y)+Sin(x+y))");
		check("TrigReduce(-I*Cos(x)^2-2*Cos(x)*Sin(x)+I*Sin(x)^2)", //
				"-I*Cos(2*x)-Sin(2*x)");
		check("TrigReduce(I*Cos(x)^4+I*2*Cos(x)^2*Sin(x)^2+I*Sin(x)^4)", //
				"I");
		check("TrigReduce(Cos(a*x)*Sin(a*x)^2)", //
				"Cos(a*x)/4-Cos(3*a*x)/4");
		check("TrigReduce(-a*Cos(x)^2+a*Sin(x)^2 )", //
				"-a*Cos(2*x)");
		check("TrigReduce(Sin(x)*Tan(y))", //
				"1/2*(Cos(x-y)-Cos(x+y))*Sec(y)");
		check("TrigReduce(Sin(x)*Tan(y))", //
				"1/2*(Cos(x-y)-Cos(x+y))*Sec(y)");
		check("TrigReduce(Cos(x)*Tan(y))", //
				"1/2*Sec(y)*(-Sin(x-y)+Sin(x+y))");
		check("TrigReduce(2*Cos(x)^2)", //
				"1+Cos(2*x)");
		check("TrigReduce(2*Cos(x)*Sin(y))", //
				"-Sin(x-y)+Sin(x+y)");
		check("TrigReduce(15*Sin(12*x)^2 + 12*Sin(15*x)^2)", //
				"27/2-15/2*Cos(24*x)-6*Cos(30*x)");
		check("TrigReduce(2*Sinh(u)*Cosh(v))", //
				"Sinh(u-v)+Sinh(u+v)");
		check("TrigReduce(3*Sinh(u)*Cosh(v)*k)", //
				"3/2*k*Sinh(u-v)+3/2*k*Sinh(u+v)");
		// check("TrigReduce(2 Tan(x)*Tan(y))",
		// "(2*Cos(-y+x)-2*Cos(y+x))*(Cos(-y+x)+Cos(y+x))^(-1)");
	}

	public void testTrigToExp() {
		check("TrigToExp(Cos(Sin(x)))", //
				"1/(2*E^(I*((I*1/2)/E^(I*x)-I*1/2*E^(I*x))))+" + //
						"E^(I*((I*1/2)/E^(I*x)-I*1/2*E^(I*x)))/\n" + "2");
		check("Map(# == TrigToExp(#)&," //
				+ "{ArcSinh(x), ArcCosh(x), ArcTanh(x)," //
				+ "ArcCoth(x), ArcSech(x), ArcCsch(x)}) // TableForm", //
				"ArcSinh(x)==Log(x+Sqrt(1+x^2))\n" + //
						"ArcCosh(x)==Log(x+Sqrt(-1+x)*Sqrt(1+x))\n" + //
						"ArcTanh(x)==-Log(1-x)/2+Log(1+x)/2\n" + //
						"ArcCoth(x)==-Log(1-1/x)/2+Log(1+1/x)/2\n" + //
						"ArcSech(x)==Log(Sqrt(-1+1/x)*Sqrt(1+1/x)+1/x)\n" + //
						"ArcCsch(x)==Log(Sqrt(1+1/x^2)+1/x)\n");
		check("Map(# == TrigToExp(#)&," //
				+ "{ArcSin(x), ArcCos(x), ArcTan(x)," //
				+ "ArcCot(x), ArcSec(x), ArcCsc(x)}) // TableForm", //
				"ArcSin(x)==-I*Log(I*x+Sqrt(1-x^2))\n" + //
						"ArcCos(x)==Pi/2+I*Log(I*x+Sqrt(1-x^2))\n" + //
						"ArcTan(x)==I*1/2*Log(1-I*x)-I*1/2*Log(1+I*x)\n" + //
						"ArcCot(x)==I*1/2*Log(1-I/x)-I*1/2*Log(1+I/x)\n" + //
						"ArcSec(x)==Pi/2+I*Log(Sqrt(1-1/x^2)+I/x)\n" + //
						"ArcCsc(x)==-I*Log(Sqrt(1-1/x^2)+I/x)\n" + //
						"");
		check("I*y+x", //
				"x+I*y");
		check("TrigToExp(ArcTan(x, y))", //
				"-I*Log((x+I*y)/Sqrt(x^2+y^2))");
		check("TrigToExp(Sin(x) < x < Tan(x))", //
				"(I*1/2)/E^(I*x)-I*1/2*E^(I*x)<x<(I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))");
		check("a+E^x", //
				"a+E^x");
		check("TrigToExp(ArcCos(x))", //
				"Pi/2+I*Log(I*x+Sqrt(1-x^2))");
		check("TrigToExp(ArcCsc(x))", //
				"-I*Log(Sqrt(1-1/x^2)+I/x)");
		check("TrigToExp(ArcCot(x))", //
				"I*1/2*Log(1-I/x)-I*1/2*Log(1+I/x)");
		check("TrigToExp(ArcSec(x))", //
				"Pi/2+I*Log(Sqrt(1-1/x^2)+I/x)");
		check("TrigToExp(ArcSin(x))", //
				"-I*Log(I*x+Sqrt(1-x^2))");
		check("TrigToExp(ArcTan(x))", //
				"I*1/2*Log(1-I*x)-I*1/2*Log(1+I*x)");

		check("TrigToExp(ArcCosh(x))", //
				"Log(x+Sqrt(-1+x)*Sqrt(1+x))");
		check("TrigToExp(ArcCsch(x))", //
				"Log(Sqrt(1+1/x^2)+1/x)");
		check("TrigToExp(ArcCoth(x))", //
				"-Log(1-1/x)/2+Log(1+1/x)/2");
		check("TrigToExp(ArcSech(x))", //
				"Log(Sqrt(-1+1/x)*Sqrt(1+1/x)+1/x)");
		check("TrigToExp(ArcSinh(x))", //
				"Log(x+Sqrt(1+x^2))");
		check("TrigToExp(ArcTanh(x))", //
				"-Log(1-x)/2+Log(1+x)/2");
		check("TrigToExp(Cos(x))", //
				"1/(2*E^(I*x))+E^(I*x)/2");
		check("TrigToExp(Cot(x))", //
				"(-I*(E^(-I*x)+E^(I*x)))/(E^(-I*x)-E^(I*x))");
		check("TrigToExp(Csc(x))", //
				"(-I*2)/(E^(-I*x)-E^(I*x))");
		check("TrigToExp(Sec(x))", //
				"2/(E^(-I*x)+E^(I*x))");
		check("TrigToExp(Tan(x))", //
				"(I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))");
		check("TrigToExp(Cosh(x)+a)", //
				"a+1/(2*E^x)+E^x/2");
		check("TrigToExp(Csch(x)+a)", //
				"a+2/(-1/E^x+E^x)");
		check("TrigToExp(Coth(x)+a)", //
				"a+(E^(-x)+E^x)/(-1/E^x+E^x)");
		check("TrigToExp(Sech(x)+a)", //
				"a+2/(E^(-x)+E^x)");
		check("TrigToExp(Sinh(x)+a)", //
				"a-1/(2*E^x)+E^x/2");
		check("TrigToExp(Tanh(x))", //
				"-1/(E^x*(E^(-x)+E^x))+E^x/(E^(-x)+E^x)");
		check("TrigToExp(a+b)", //
				"a+b");
	}

	public void testTrueQ() {
		check("TrueQ(True)", //
				"True");
		check("TrueQ(False)", //
				"False");
		check("TrueQ(x)", //
				"False");
	}

	public void testTuples() {
		check("Tuples({}, 2)", //
				"{}");
		check("Tuples({a, b, c}, 0)", //
				"{{}}");
		check("tuples({{a, b}, {1, 2, 3, 4}})", //
				"{{a,1},{a,2},{a,3},{a,4},{b,1},{b,2},{b,3},{b,4}}");
		check("tuples({{a, b}, {1, 2, 3, 4}, {x}})", //
				"{{a,1,x},{a,2,x},{a,3,x},{a,4,x},{b,1,x},{b,2,x},{b,3,x},{b,4,x}}");

		check("tuples({0,1},3)", //
				"{{0,0,0},{0,0,1},{0,1,0},{0,1,1},{1,0,0},{1,0,1},{1,1,0},{1,1,1}}");
		check("tuples({1,0},3)", //
				"{{1,1,1},{1,1,0},{1,0,1},{1,0,0},{0,1,1},{0,1,0},{0,0,1},{0,0,0}}");

		// The head of list need not be 'List':
		check("Tuples(f(a, b, c), 2)", //
				"{f(a,a),f(a,b),f(a,c),f(b,a),f(b,b),f(b,c),f(c,a),f(c,b),f(c,c)}");
		// However, when specifying multiple expressions, 'List' is always used:
		check("Tuples({f(a, b), g(x, y)})", "{{a,x},{a,y},{b,x},{b,y}}");
	}

	public void testUnequal() {
		check("a!=a", //
				"False");
		check("a!=b", //
				"a!=b");
		check("1!=1.", //
				"False");
		check("{1} != {2}", //
				"True");
		check("{1, 2} != {1, 2}", //
				"False");
		check("{a} != {a}", //
				"False");
		check("\"a\" != \"b\"", //
				"True");
		check("\"a\" != \"a\"", //
				"False");
		check("Pi != N(Pi)", //
				"False");
		check("a_ != b_", //
				"a_!=b_");
		check("a != a != a", //
				"False");
		check("a != b != a", //
				"a!=b!=a");
		check("{Unequal(), Unequal(x), Unequal(1)}", //
				"{True,True,True}");

		check("{\"a\",\"b\"}!={\"a\",\"b\"}", //
				"False");
		check("{\"a\",\"b\"}!={\"b\",\"a\"}", //
				"True");
		check("{\"a\",b}!={\"a\",c}", //
				"{a,b}!={a,c}");
		check("(E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi!=0", //
				"False");
		check("a!=a!=a!=a", //
				"False");
	}

	public void testUnevaluate() {
		check("f(x_) := x^2", //
				"");
		check("ReplaceAll(Unevaluated(f(3)), 3 -> 1)", //
				"1");
	}

	public void testUnion() {
		// http://oeis.org/A001597 - Perfect powers: m^k where m > 0 and k >= 2.
		check("$min = 0; $max = 10^4;  "//
				+ "Union@ Flatten@ Table( n^expo, {expo, Prime@ Range@ PrimePi@ Log2@ $max}, {n, Floor(1 + $min^(1/expo)), $max^(1/expo)})", //
				"{1,4,8,9,16,25,27,32,36,49,64,81,100,121,125,128,144,169,196,216,225,243,256,289,\n"
						+ "324,343,361,400,441,484,512,529,576,625,676,729,784,841,900,961,1000,1024,1089,\n"
						+ "1156,1225,1296,1331,1369,1444,1521,1600,1681,1728,1764,1849,1936,2025,2048,2116,\n"
						+ "2187,2197,2209,2304,2401,2500,2601,2704,2744,2809,2916,3025,3125,3136,3249,3364,\n"
						+ "3375,3481,3600,3721,3844,3969,4096,4225,4356,4489,4624,4761,4900,4913,5041,5184,\n"
						+ "5329,5476,5625,5776,5832,5929,6084,6241,6400,6561,6724,6859,6889,7056,7225,7396,\n"
						+ "7569,7744,7776,7921,8000,8100,8192,8281,8464,8649,8836,9025,9216,9261,9409,9604,\n"
						+ "9801,10000}");

		check("Union({a,a,b,c})", //
				"{a,b,c}");
		check("Union({9, 0, 0, 3, 2, 3, 6, 2, 9, 8, 4, 9, 0, 2, 6, 5, 7, 4, 9, 8})", //
				"{0,2,3,4,5,6,7,8,9}");
		check("Union({a,a,b,c},{},{})", //
				"{a,b,c}");
		check("Union({a,a,b,c},{},{z,z,z,x,x,x,y,y,y})", //
				"{a,b,c,x,y,z}");
	}

	public void testUnique() {
		check("Unique()", //
				"$1");
		check("Unique(x)", //
				"x$2");
		check("Unique(\"x\")", //
				"x3");
	}

	public void testUnitConvert() {
		if (ToggleFeature.QUANTITY) {
			check("UnitConvert(Quantity(Pi, \"deg\"), \"rad\")", //
					"Pi^2/180[rad]");
			check("UnitConvert(Quantity(Pi, \"rad\"), \"deg\")", //
					"180[deg]");
			check("UnitConvert(Quantity(Pi, \"grad\"), \"rad\")", //
					"Pi^2/180[rad]");
			check("UnitConvert(Quantity(Pi, \"rad\"), \"grad\")", //
					"180[grad]");
			check("UnitConvert(Quantity(200, \"g\")*Quantity(981, \"cm*s^-2\") )", //
					"981/500[kg*m*s^-2]");
			check("UnitConvert(Quantity(10^(-6), \"MOhm\") )", //
					"1[A^-2*kg*m^2*s^-3]");
			check("UnitConvert(Quantity(10^(-6), \"MOhm\"),\"Ohm\" )", //
					"1[Ohm]");
			check("UnitConvert(Quantity(1, \"nmi\"),\"km\" )", //
					"463/250[km]");
			check("UnitConvert(Quantity(360, \"mV^-1*mA*s^2\"),\"Ohm^-1*s^2\" )", //
					"360[Ohm^-1*s^2]");
			check("UnitConvert(Quantity(360, \"km*h^-1\"),\"m*s^-1\" )", //
					"100[m*s^-1]");
			check("UnitConvert(Quantity(2, \"km^2\") )", //
					"2000000[m^2]");
			check("UnitConvert(Quantity(2, \"km^2\"),\"cm^2\" )", //
					"20000000000[cm^2]");
			check("UnitConvert(Quantity(3, \"Hz^-2*N*m^-1\") )", //
					"3[kg]");
			check("UnitConvert(Quantity(3.8, \"lb\") )", //
					"1.723651006[kg]");
			check("UnitConvert(Quantity(8.2, \"nmi\"), \"km\")", //
					"15.186399999999999[km]");
		}
	}

	public void testUnitize() {
		check("Unitize({0, -1})", //
				"{0,1}");
		check("Unitize((E + Pi)^2 - E^2 - Pi^2 - 2*E*Pi)", //
				"0");
		check("Unitize(2^(2*I) - 2^(-2*I) - 2*I*Sin(Log(4)))", //
				"0");
		check("Unitize(Sqrt(2) + Sqrt(3) - Sqrt(5 + 2*Sqrt(6)))", //
				"0");
		check("Unitize(0)", //
				"0");
		check("Unitize(0.0)", //
				"0");
		check("Unitize(x)", //
				"Unitize(x)");
		check("Unitize(3/4)", //
				"1");
		check("Unitize(3*I)", //
				"1");
		check("Unitize(Pi)", //
				"1");
	}

	public void testUnitStep() {
		check("UnitStep(-Infinity)", //
				"0");
		check("UnitStep(Infinity)", //
				"1");

		check("UnitStep(0)", //
				"1");
		check("UnitStep(1)", //
				"1");
		check("UnitStep(-1)", //
				"0");

		check("UnitStep(Interval({0,42}))", //
				"Interval({1,1})");
		check("UnitStep(Interval({-3,-1}))", //
				"Interval({0,0})");
		check("UnitStep(Interval({-1,2}))", //
				"Interval({0,1})");

		check("UnitStep(42)", //
				"1");
		check("UnitStep(-1)", //
				"0");
		check("UnitStep(-42)", //
				"0");
		check("UnitStep({1.6, 1.6000000000000000000000000})", //
				"{1,1}");
		check("UnitStep({-1, 0, 1})", //
				"{0,1,1}");
		check("UnitStep(1, 2, 3)", //
				"1");
	}

	public void testUnitVector() {
		check("UnitVector(-3)", //
				"UnitVector(-3)");
		check("UnitVector(4,-2)", //
				"UnitVector(4,-2)");
		check("UnitVector(2)", //
				"{0,1}");
		check("UnitVector(4,3)", //
				"{0,0,1,0}");
		check("UnitVector(4,4)", //
				"{0,0,0,1}");
		check("UnitVector(4,5)", //
				"UnitVector(4,5)");
	}

	public void testUnset() {
		check("a=.", //
				"");
		check("$x=5;$x=.;$x", //
				"$x");
		check("$f(x_):=x^2;$f(x_)=.;$f(3)", //
				"$f(3)");
	}

	public void testUpperCaseQ() {
		// print message: UpperCaseQ: String expected at position 1 in UpperCaseQ(abc).
		check("UpperCaseQ(abc)", //
				"UpperCaseQ(abc)");

		check("UpperCaseQ(\"ABCDEFGHIJKLMNOPQRSTUVWXYZ\")", //
				"True");
		check("UpperCaseQ(\"ABCDEFGHIJKLMNopqRSTUVWXYZ\")", //
				"False");
	}

	public void testUpperTriangularize() {
		check("UpperTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}}, 1)", //
				"{{0,b,c,d},\n" + " {0,0,f,g},\n" + " {0,0,0,k},\n" + " {0,0,0,0}}");
		check("UpperTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}})", //
				"{{a,b,c,d},\n" + " {0,e,f,g},\n" + " {0,0,j,k}}");
		check("UpperTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}})", //
				"{{a,b,c,d},\n" + " {0,e,f,g},\n" + " {0,0,j,k},\n" + " {0,0,0,o}}");

	}

	public void testUpSet() {
		check("ParameterRanges[Cartesian]^=Null;Null", //
				"");
		check("ParameterRanges[Cartesian]^=Null;Null", //
				"");
		check("$f($abc(0))^=100;$f($abc(0))", "100");
	}

	public void testUpSetDelayed() {
		check("$f($h(0)) ^= h0;$f($h(x_)) ^:= 2*$f($h(x - 1));$f($h(10))", //
				"1024*h0");
	}

	public void testValueQ() {
		check("ValueQ(x)", //
				"False");
		check("x=1; ValueQ(x)", //
				"True");
		check("ValueQ(True)", //
				"False");
	}

	public void testVariables() {
		check("Variables(x + f(x)+Pi*E)", //
				"{x,f(x)}");
		check("Variables(x^0.3 + f(x)+Pi*E)", //
				"{x^0.3,f(x)}");
		check("Variables(Sin(x) + Cos(x))", //
				"{Cos(x),Sin(x)}");
		check("Variables({a + b*x, c*y^2 + x/2})", //
				"{a,b,c,x,y}");
		check("Variables((x + y)^2 + 3*z^2 - y*z + 7)", //
				"{x,y,z}");
		check("Variables((a - b)/(x + y) - 2/z)", //
				"{a,b,x,y,z}");
		check("Variables(Sqrt(x + y - z^2) + (-2*t)^(2/3))", //
				"{t,x,y,z}");
		check("Variables(y + x*z)", "{x,y,z}");

		check("Variables(a*x^2 + b*x + c)", //
				"{a,b,c,x}");
		check("Variables({a + b*x, c*y^2 + x/2})", //
				"{a,b,c,x,y}");
		check("Variables(x + Sin(y))", //
				"{x,Sin(y)}");
		check("Variables(x + Sin(10))", //
				"{x}");
		check("Variables(E^x)", //
				"{}");
		check("Variables(a^x)", //
				"{a^x}");
	}

	public void testVariance() {
		check("Variance(BinomialDistribution(n, m))", //
				"(1-m)*m*n");
		check("Variance(BernoulliDistribution(n))", //
				"(1-n)*n");
		check("Variance(BetaDistribution(a,b))", //
				"(a*b)/((a+b)^2*(1+a+b))");
		check("Variance(DiscreteUniformDistribution({l, r}))", //
				"1/12*(-1+(1-l+r)^2)");
		check("Variance(ErlangDistribution(n, m))", //
				"n/m^2");
		check("Variance(ExponentialDistribution(n))", //
				"1/n^2");
		check("Variance(LogNormalDistribution(m,s))", //
				"(-1+E^s^2)*E^(2*m+s^2)");
		check("Variance(NakagamiDistribution(n, m))", //
				"m+(-m*Pochhammer(n,1/2)^2)/n");
		check("Variance(NormalDistribution(n, m))", //
				"m^2");
		check("Variance(FrechetDistribution(n, m))", //
				"Piecewise({{m^2*(Gamma(1-2/n)-Gamma(1-1/n)^2),n>2}},Infinity)");
		check("Variance(GammaDistribution(n, m))", //
				"m^2*n");
		check("Variance(GeometricDistribution(n))", //
				"(1-n)/n^2");
		check("Variance(GumbelDistribution(n, m))", //
				"1/6*m^2*Pi^2");
		check("Variance(HypergeometricDistribution(n, ns, nt))", //
				"(n*ns*(1-ns/nt)*(-n+nt))/((-1+nt)*nt)");
		check("Variance(PoissonDistribution(n))", //
				"n");
		check("Variance(StudentTDistribution(4))", //
				"2");
		check("Variance(StudentTDistribution(n))", //
				"Piecewise({{n/(-2+n),n>2}},Indeterminate)");

		check("Variance(WeibullDistribution(n, m))", //
				"m^2*(-Gamma(1+1/n)^2+Gamma(1+2/n))");

		check("Variance({1, 2, 3})", //
				"1");
		check("Variance({7, -5, 101, 3})", //
				"7475/3");
		check("Variance({1 + 2*I, 3 - 10*I})", //
				"74");
		check("Variance({a, a})", //
				"0");
		check("Variance({{1, 3, 5}, {4, 10, 100}})", //
				"{9/2,49/2,9025/2}");

		check("Variance({Pi,E,3})//Together", //
				"1/3*(9-3*E+E^2-3*Pi-E*Pi+Pi^2)");
		check("Variance({a,b,c,d})", //
				"1/12*(-(-3*a+b+c+d)*Conjugate(a)-(a-3*b+c+d)*Conjugate(b)-(a+b-3*c+d)*Conjugate(c)-(a+b+c\n"
						+ "-3*d)*Conjugate(d))");
		checkNumeric("Variance({1., 2., 3., 4.})", //
				"1.6666666666666667");
		checkNumeric("Variance({{5.2, 7}, {5.3, 8}, {5.4, 9}})", //
				"{0.010000000000000018,1.0}");
		checkNumeric("Variance({1.21, 3.4, 2, 4.66, 1.5, 5.61, 7.22})", //
				"5.16122380952381");
		check("Variance({1.21, 3.4, 2+3*I, 4.66-0.1*I, 1.5, 5.61, 7.22})", //
				"6.46265");
		// check("Variance(BernoulliDistribution(p))", "p*(1-p)");
		// check("Variance(BinomialDistribution(n, p))", "n*p*(1-p)");
	}

	public void testVectorAngle() {
		check("(1/2)*Sqrt(2)", //
				"1/Sqrt(2)");
		check("ArcCos(1/Sqrt(2))", //
				"Pi/4");
		check("VectorAngle({1,0},{0,1})", //
				"Pi/2");
		check("VectorAngle({1, 2}, {3, 1})", //
				"Pi/4");
		check("VectorAngle({1, 1, 0}, {1, 0, 1})", //
				"Pi/3");
		check("VectorAngle({0, 1}, {0, 1})", //
				"0");

		check("VectorAngle({1,0,0},{1,1,1})", //
				"ArcCos(1/Sqrt(3))");
		check("VectorAngle({1,0},{1,1})", //
				"Pi/4");

		check("Norm({1,0})", //
				"1");
		check("Norm({1,1})", //
				"Sqrt(2)");
		check("{1,0}.{1,1}", //
				"1");

	}

	public void testVectorQ() {
		check("VectorQ({a, b, c})", //
				"True");
		check("VectorQ({1, 1/2, 3, I}, NumberQ)", //
				"True");
	}

	public void testVertexList() {
		check("VertexList(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}))", //
				"{1,2,3,4}");
		check("VertexList(Graph({1 \\[UndirectedEdge] 2, 2 \\[UndirectedEdge] 3, 3 \\[UndirectedEdge] 1}))", //
				"{1,2,3}");
		check("VertexList(Graph({1 \\[DirectedEdge] 2, 2 \\[DirectedEdge] 3, 3 \\[DirectedEdge] 1}))", //
				"{1,2,3}");
	}

	public void testVertexEccentricity() {
		check("VertexEccentricity({1 -> 2, 2 -> 3, 3 -> 1, 3 -> 4, 4 -> 5, 5 -> 3}, 1)", //
				"4");

		check("VertexEccentricity(Graph({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, "
				+ //
				"{EdgeWeight->{1.6,1.4,0.62,1.9,2.1}}), 4)", //
				"2.22");
		check("VertexEccentricity({UndirectedEdge(1, 2), UndirectedEdge(1, 3), UndirectedEdge(1, 4),  UndirectedEdge(2, 3), UndirectedEdge(3, 4)}, 4)", //
				"2");

	}

	public void testVertexQ() {
		check("VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),3)", //
				"True");
		check("VertexQ(Graph({1 -> 2, 2 -> 3, 1 -> 3, 4 -> 2}),5)", //
				"False");
	}

	public void testWeierstrassHalfPeriods() {
		// TODO improve see discussion: https://github.com/paulmasson/math/issues/7
		check("WeierstrassHalfPeriods({1.0, 2.0*I} )", //
				"{1.30139+I*(-0.299127),2.30366+I*0.703139}");

		check("WeierstrassHalfPeriods({1.0, 2.0 } )", //
				"{1.30836,0.654182+I*1.22937}");
		check("Table(WeierstrassHalfPeriods({1.0,x*I} ), {x,-2.0, 2.0, 1/4})", //
				"{{1.30139+I*0.299127,2.30366+I*(-0.703139)},{1.32906+I*0.300747,2.35736+I*(-0.72756)}," //
						+ "{1.36146+I*0.301917,2.421+I*(-0.757623)},{1.40035+I*0.30217,2.49853+I*(-0.796011)}," //
						+ "{1.4486+I*0.300495,2.59671+I*(-0.84761)},{1.5113+I*0.294424,2.72817+I*(-0.922449)}," //
						+ "{1.59835+I*0.276625,2.92007+I*(-1.0451)},{1.72843+I*0.217942,3.23892+I*(-1.29255)}," //
						+ "{1.85407,I*(-1.85407)},{1.72843+I*(-0.217942),3.23892+I*1.29255},{1.59835+I*(-0.276625)," //
						+ "2.92007+I*1.0451},{1.5113+I*(-0.294424),2.72817+I*0.922449},{1.4486+I*(-0.300495)," //
						+ "2.59671+I*0.84761},{1.40035+I*(-0.30217),2.49853+I*0.796011},{1.36146+I*(-0.301917)," //
						+ "2.421+I*0.757623},{1.32906+I*(-0.300747),2.35736+I*0.72756},{1.30139+I*(-0.299127)," //
						+ "2.30366+I*0.703139}}");
	}

	public void testWeierstrassInvariants() {
		check("WeierstrassInvariants({1.0, 2.0*I} )", //
				"{8.12422,4.44305}");
		check("Table(WeierstrassInvariants({1.0,x*I} ), {x,-2.0, 2.0, 1/4})", //
				"{{8.12422,4.44305},{8.15011,4.41322},{8.27476,4.26937},{8.87636,3.56885},{11.81705,0.0}," //
						+ "{27.07395,-22.08742},{129.9875,-284.3553},{2078.061,-18230.79}," //
						+ "WeierstrassInvariants({1.0,0.0}),{2078.061,-18230.79},{129.9875,-284.3553},{27.07395,-22.08742}," //
						+ "{11.81705,0.0},{8.87636,3.56885},{8.27476,4.26937},{8.15011,4.41322},{8.12422,4.44305}}");
	}

	public void testWeierstrassP() {
		check("WeierstrassP(z, {0, 0}) ", //
				"1/z^2");
		check("WeierstrassP(z, {3, 1}) ", //
				"1+3/2*Cot(Sqrt(3/2)*z)^2");
		check("WeierstrassP(5., {1, 2}) ", //
				"18.35051+I*1.32213*10^-9");
		check("WeierstrassP(12., {3, 2}) ", //
				"77.23116+I*1.69844*10^-8");
		check("WeierstrassP(1/3, {1, 2}) // N // Chop", //
				"9.00644");

		check("WeierstrassP(2.0, {1,2} )", //
				"2.65854+I*3.30204*10^-11");

		check("Table(WeierstrassP(x,{1.0,3.0} ), {x,-2.0, 2.0, 1/4})", //
				"{4.55263+I*8.30099*10^-11,1.98649+I*2.01263*10^-11,1.20805+I*5.41756*10^-12," //
						+ "1.00064+I*1.93068*10^-13,1.16036+I*(-1.96698*10^-12),1.84015+I*(-2.78344*10^-12)," //
						+ "4.01922+I*(-2.96196*10^-12),16.00354+I*(-2.90978*10^-12),ComplexInfinity," //
						+ "16.00354+I*(-2.91156*10^-12),4.01922+I*(-2.96130*10^-12),1.84015+I*(-2.78255*10^-12)," //
						+ "1.16036+I*(-1.96720*10^-12),1.00064+I*1.92513*10^-13,1.20805+I*5.41822*10^-12," //
						+ "1.98649+I*2.01252*10^-11,4.55263+I*8.30084*10^-11}");

	}

	public void testWeierstrassPPrime() {
		check("WeierstrassPPrime(z, {0, 0}) ", //
				"-2/z^3");
		check("WeierstrassPPrime(z, {3, 1}) ", //
				"-3*Sqrt(3/2)*Cot(Sqrt(3/2)*z)*Csc(Sqrt(3/2)*z)^2");

		check("WeierstrassPPrime(2.0, {1,2} )", //
				"8.39655+I*1.76202*10^-10");
		check("WeierstrassPPrime(5., {1, 2}) ", //
				"157.1532+I*1.70244*10^-8");
		check("WeierstrassPPrime(12., {3, 2}) ", //
				"1357.348+I*4.47847*10^-7");
		check("WeierstrassPPrime(1/3, {1, 2}) // N // Chop", //
				"-53.95606");

		check("Table(WeierstrassPPrime(x,{1.0,3.0} ), {x,-2.0, 2.0, 1/4})", //
				"{-19.23245+I*(-5.53707*10^-10),-5.13514+I*(-1.02239*10^-10),-1.68643+I*(-3.21957*10^-11),"//
						+ "-0.0838866+I*(-1.29562*10^-11),1.44536+I*(-5.30698*10^-12),4.48151+I*(-1.65090*10^-12),"//
						+ "15.89616+I*(-4.97380*10^-14),127.9683+I*1.70530*10^-13,ComplexInfinity,"//
						+ "-127.9683+I*(-2.62901*10^-13),-15.89616+I*5.15143*10^-14,-4.48151+I*1.65057*10^-12,"//
						+ "-1.44536+I*5.30853*10^-12,0.0838866+I*1.29560*10^-11,1.68643+I*3.21959*10^-11,"//
						+ "5.13514+I*1.02236*10^-10,19.23245+I*5.53695*10^-10}");//

	}

	public void testWhich() {
		check("n=5;Which(n == 3, x, n == 5, y)", //
				"y");
		check("f(x_) := Which(x < 0, -x, x == 0, 0, x > 0, x);f(-3)", //
				"3");
		check("Which(False, a)", //
				"");
		check("Which(False, a, x, b, True, c)", //
				"Which(x,b,True,c)");
		check("Which(a, b, c)", //
				"Which(a,b,c)");

		check("$a = 2;which($a == 1, x, $a == 2, b)", //
				"b");
		check("Which(1 < 0, a,  x == 0, b,  0 < 1, c)", //
				"Which(x==0,b,0<1,c)");
		check("$a = 2;which($a == 1, x, $a == 3, b)", //
				"");
		check("$x=-2;Which($x < 0, -1, $x > 0, 1, True, Indeterminate)", //
				"-1");
		check("$x=0;Which($x < 0, -1, $x > 0, 1, True, Indeterminate)", //
				"Indeterminate");
		check("$x=3;Which($x < 0, -1, $x > 0, 1, True, Indeterminate)", //
				"1");
	}

	public void testWhile() {
		check("$n = 1; While($n \\[LessEqual] 4, $n++); $n", //
				"5");
		check("While(False)", //
				"");
		check("{a, b} = {27, 6}; While(b != 0, {a, b} = {b, Mod(a, b)});a", //
				"3");
		check("i = 1; While(True, If(i^2 > 100, Return(i + 1), i++))", //
				"12");
		check("$n = 1; While($n < 4, Print($n); $n++)", //
				"");
		check("$n = 1; While(++$n < 4); $n", //
				"4");
		check("$n = 1; While($n < 4, $n++); $n", //
				"4");
		check("$n = 1; While(True, If($n > 10, Break()); $n++);$n", //
				"11");
	}

	public void testWith() {
		check("xm=10;With({xm=xm}, Print(xm));xm", //
				"10");
		check("u=test;With({w=Map(Function({#,x,v,flag}),u)},  w)", //
				"test");
		check("With({x=2, y=16},x^y)", //
				"65536");
		check("With({x = 7, y = a + 1}, x/y)", //
				"7/(1+a)");
		check("With({x = a}, (1 + x^2) &)", //
				"1+a^2&");
		check("Table(With({i = j}, Hold(i)), {j, 5})", //
				"{Hold(1),Hold(2),Hold(3),Hold(4),Hold(5)}");

		check("With({y = Sin(1.0)}, Sum(y^i, {i, 0, 10}))", //
				"5.36323");
		check("With({e = Expand((1 + x)^5)}, Function(x, e))", //
				"Function(x$12,1+5*x+10*x^2+10*x^3+5*x^4+x^5)");
		check("With({e = Expand((1 + x)^5)}, Function @@ {x, e})", //
				"Function(x,1+5*x+10*x^2+10*x^3+5*x^4+x^5)");

		check("x=5;With({x = x}, Hold(x))", //
				"Hold(5)");

		check("newton(f_, x0_) := With({fp = f'}, FixedPoint(# - f(#)/fp(#) &, x0))", //
				"");
		check("newton(Cos,1.33)", //
				"1.5708");

		check("newton(Cos(#)-#&,1.33)", //
				"0.739085");

		check("With({f = Function(n, If(n == 0, 1, n*f(n - 1)))}, f(10))", //
				"10*f(9)");
		// check("Timing(Do(With({x = 5}, x;), {10^5}))", "");
		// check("Timing(Do(Module({x = 5}, x;), {10^5}))", "");
		check("With({x=y,y=z,z=3}, Print({Hold(x),Hold(y),Hold(z)});{x,y,z})", //
				"{y,z,3}");

		check("With({x=z},Module({x},x+y))", //
				"x$21+y");

		check("f(x_) := With({q = False},  test /; q==0) /;  x==1", //
				"");
		check("f(1)", //
				"f(1)");
	}

	public void testXor() {
		check("Xor(False, True)", //
				"True");
		check("Xor(True, True)", //
				"False");
		check("Xor(a, False, b)", //
				"Xor(a,b)");
		check("Xor(a, b)", //
				"Xor(a,b)");

		check("Xor()", //
				"False");
		check("Xor(False)", //
				"False");
		check("Xor(True)", //
				"True");
		check("Xor(f(x))", //
				"f(x)");
		check("Xor(a,a)", //
				"False");
		check("Xor(a,a,a,b)", //
				"Xor(a,b)");
		check("Xor(a,c,a,b)", //
				"Xor(b,c)");
		check("Xor(True, False, False)", //
				"True");
		check("Xor(True, True, True)", //
				"True");
		check("Xor(True, True, True, True)", //
				"False");
		check("Xor(False, False, False, False)", //
				"False");
		check("Xor(True, False, True)", //
				"False");
	}

	public void testYuleDissimilarity() {
		check("YuleDissimilarity({1, 0, 1, 1, 0}, {1, 1, 0, 1, 1})", //
				"2");
		check("YuleDissimilarity({True, False, True}, {True, True, False})", //
				"2");
		check("YuleDissimilarity({0, 0, 0, 0}, {0, 0, 0, 0})", //
				"0");
		check("YuleDissimilarity({0, 1, 0, 1}, {1, 0, 1, 0})", //
				"2");
	}

	public void testZeta() {
		check("Zeta(-3.0)", //
				"0.00833333");
		check("Zeta(4.0)", //
				"1.08232");
		check("Zeta(1.0+I)", //
				"0.582158+I*(-0.926849)");
		check("Zeta(-3.0+I*1.0^(-100))", //
				"0.0143825+I*0.0103497");

		check("Zeta(-1)", //
				"-1/12");
		check("Zeta(2)", //
				"Pi^2/6");
		// TODO add implementation
		// check("Zeta(-2.5 + I)", "0.0235936 + 0.0014078*I");
		check("Zeta(s, 0)", //
				"Zeta(s)");
		check("Zeta(s, 1/2)", //
				"(-1+s^2)*Zeta(s)");
		check("Zeta(s, -1)", //
				"1+Zeta(s)");
		check("Zeta(s, 2)", //
				"-1+Zeta(s)");
		check("Zeta(4, -12)", //
				"638942263173398977/590436101122560000+Pi^4/90");
		check("Zeta(11, -12)", //
				"Zeta(11,-12)");
		check("Zeta(-5, -12)", //
				"158938415/252");
		check("Zeta(6)", //
				"Pi^6/945");
		check("Zeta(-11)", //
				"691/32760");
		check("Zeta(-42)", //
				"0");
		check("Zeta(0)", //
				"-1/2");

		check("Zeta(Infinity)", //
				"1");
	}
}
