package org.matheclipse.core.system;

import java.io.StringWriter;

import javax.script.ScriptEngine;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Tests system.reflection classes
 */
public class MainTestCase extends AbstractTestCase {

	public MainTestCase(String name) {
		super(name);
		Config.SERVER_MODE = true;
	}

	@Override
	public void check(String evalString, String expectedResult) {
		check(fScriptEngine, evalString, expectedResult, -1);
	}

	@Override
	public void check(ScriptEngine scriptEngine, String evalString, String expectedResult, int resultLength) {
		try {
			if (evalString.length() == 0 && expectedResult.length() == 0) {
				return;
			}
			// scriptEngine.put("STEPWISE",Boolean.TRUE);
			scriptEngine.put("RELAXED_SYNTAX", Boolean.TRUE);
			scriptEngine.put("ENABLE_HISTORY", Boolean.TRUE);

			String evaledResult = (String) scriptEngine.eval(evalString);

			assertEquals(expectedResult, evaledResult);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(e.getMessage(), "");
		}
	}

	@Override
	public void check(IAST ast, String strResult) {
		check(EvalEngine.get(), true, ast, strResult);
	}

	@Override
	public void check(EvalEngine engine, boolean configMode, IAST ast, String strResult) {
		boolean mode = Config.SERVER_MODE;
		try {
			StringWriter buf = new StringWriter();

			Config.SERVER_MODE = configMode;
			if (Config.SERVER_MODE) {
				IAST inExpr = ast;
				TimeConstrainedEvaluator utility = new TimeConstrainedEvaluator(engine, false, Config.FOREVER, true);
				utility.constrainedEval(buf, inExpr);
			} else {
				if (ast != null) {
					OutputFormFactory off = OutputFormFactory.get();
					off.setIgnoreNewLine(true);
					off.convert(buf, ast);
				}
			}

			assertEquals(buf.toString(), strResult);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(e.getMessage(), "");
		} finally {
			Config.SERVER_MODE = mode;
		}
	}

	/**
	 * Test system functions
	 */
	public void testSystem000() {
		// assertEquals(PrimeList.getMersennePrime(4).toString(), "15");
		// assertEquals(PrimeList.getMersennePrime(128).toString(), "15");
		check("1^(-1)", "1");
		check("test+0", "test");
		check("Times(3, Power(1, -1))", "3");
		check("%", "3");
		check("%%%*%2", "test^2");
		check("%10", "Out(10)");
		check("1-x", "1-x");
		check("5+x^4*(33+x^2)", "5+(33+x^2)*x^4");
		check("x^(-7)", "1/x^7");
		check("x^(-7.0)", "1/x^7.0");
		check("x^(1+I*3)", "x^(1+I*3)");
		check("x^(-1+I*3)", "1/x^(1-I*3)");
		check("x^(1.0+I*3)", "x^(1.0+I*3.0)");
		check("x^(-1+I*3.0)", "1/x^(1.0+I*(-3.0))");
		check("x^(I*3)", "x^(I*3)");
		check("x^(I*3.0)", "x^(I*3.0)");
		check("x^(-I*3)", "1/x^(I*3)");
		check("x^(-I*3.0)", "1/x^(I*3.0)");
		check("Sin(3/10*Pi)", "1/4*(1+Sqrt(5))");

		check("Sin(Pi/5)", "Sqrt(1/2*(5-Sqrt(5)))/2");
		check("Sin({a,b,c})", "{Sin(a),Sin(b),Sin(c)}");
		check("2^(-1)", "1/2");
		check("x^3+x^2+x+42", "42+x+x^2+x^3");
		check("x^3+2*x^2+4*x+3", "3+4*x+2*x^2+x^3");
		check("y*x^3+y*x^2+y*x+y+x+42", "42+x+y+x*y+x^2*y+x^3*y");
		check("2*I", "I*2");

		check("a+Sin(x)^2+Cos(x)^2+2/3", "5/3+a");
		check("a+Sin(x)^2+Cos(y)^2+2/3", "2/3+a+Cos(y)^2+Sin(x)^2");
		check("a+ArcSin(x)+ArcCos(x)+2/3", "2/3+a+Pi/2");
		check("a+ArcTan(17)+ArcTan(1/17)+2/3", "2/3+a+Pi/2");
		check("a+ArcTan(-2)+ArcTan(-1/2)+2/3", "2/3+a-Pi/2");
		check("ArcTan((-1+2*x)*3^(-1/2))", "ArcTan((-1+2*x)/Sqrt(3))");

		check("Tan(x)^(-2)", "Cot(x)^2");
		check("Cot(x)^(-2)", "Tan(x)^2");
		check("Sec(x)^(-2)", "Cos(x)^2");
		check("Cos(x)^(-2)", "Sec(x)^2");
		check("Csc(x)^(-2)", "Sin(x)^2");
		check("Sin(x)^(-2)", "Csc(x)^2");

		check("x - (11 + (7 - x))", "-18+2*x");

		check("1/Sqrt(x)*a", "a/Sqrt(x)");

		check("-Cos(x)", "-Cos(x)");
		check("4-Cos(x)", "4-Cos(x)");
		check("x*(a+b)", "(a+b)*x");

		check("x*E^x", "E^x*x");
		check("x*Cos(x)", "x*Cos(x)");
		check("Cos(x)*x", "x*Cos(x)");

		check("3.0*x+x^3.0+5.0", "5.0+3.0*x+x^3.0");
		check("5.0+3.0*x+x^3.0", "5.0+3.0*x+x^3.0");

		check("I^2", "-1");
		check("i^2", "i^2");

		check("Pi<E", "False");

		check("z/.(a/.b)", "z/.(a/.b)");
		check("z/.a/.b", "z/.a/.b");
		check("(z/.a)/.b", "z/.a/.b");

		check("x/(1+x)/(1+x)", "x/(1+x)^2");

		check("10!", "3628800");

		check("\\[Alpha]", "α");
		check("\\[Alpha]+\\[Phi]\\[Pi]", "α+ϕπ");

		check("(a+b)[x]", "(a+b)[x]");
		check("f[x,y,]", "f(x,y,Null)");
		check("f(x,y,)", "f(x,y,Null)");
	}

	public void testOut() {
		check("1+1", "2");
		check("Out()", "2");
	}

	public void testPower() {
		check("(0.0+I*0.0)^10.0", "0.0");

		check("(Infinity)^(-Infinity)", "0");
		check("(ComplexInfinity)^(-Infinity)", "0");
		check("(ComplexInfinity)^(Infinity)", "ComplexInfinity");
		check("(-1)^(-Infinity)", "Indeterminate");
		check("(-10)^(-Infinity)", "0");
		check("(10)^(Infinity)", "Infinity");
		check("(2.5)^(Infinity)", "Infinity");
		check("(-10)^(Infinity)", "ComplexInfinity");
		check("(-2.5)^(Infinity)", "ComplexInfinity");
		check("(-1/2)^(Infinity)", "0");
		check("(-1/2)^(-Infinity)", "ComplexInfinity");
		check("(1/2)^(-Infinity)", "Infinity");
		check("0^(-3+I*4)", "ComplexInfinity");
		check("0^(3+I*4)", "0");
		check("0^a", "0^a");

		check("(z^a)^b", "(z^a)^b");
		check("z^a^b", "z^a^b");
		check("(2/3)^(-2)", "9/4");
		check("(0.0+I*0.0)^10.0", "0.0");
		check("(4+I*3)^720", //
				"-116167855361272797036694467869451869602145196288671871518854133570041727318170\\\n"
						+ "4574910556344606994083723362744059070407184159952656087011020271881499618065414\\\n"
						+ "4914197632473665845768182768874385002516108364828159454093271358889777980716324\\\n"
						+ "7041962572874606265474607397744891281751906241660086025161497507011651269350915\\\n"
						+ "8674355526705677239476087139525686235117532827111124982508671522523788049278212\\\n"
						+ "6235876151080441938607898345940031290124186034319083712733577325127486326608917\\\n"
						+ "855213832266042995350553133887-\n"
						+ "I*1809296693954676260529187500834196135867651919576860382545117330497788765779097\\\n"
						+ "4742403980507905105518242113616427518927624584927900824873140815065574125686415\\\n"
						+ "5416939947880232833784547024385946234599377598525567810457380378085881679338736\\\n"
						+ "7076731699624806835349224807633401876451100977053525154350851520373609014172315\\\n"
						+ "7686545081990878497982644868332129719310811001025960592418274299563487953111168\\\n"
						+ "0655212821076523818404143071188918164969744044588128042634517402654783225640653\\\n"
						+ "896022203469563104855904821184");
		check("(4+I*3)^(-24)", //
				"-57540563024581727/3552713678800500929355621337890625-\n"
						+ "I*15549832333971936/3552713678800500929355621337890625");
	}

	public void testSystem000a() {
		check("Rationalize(0.25+I*0.33333)", "1/4+I*33333/100000");
		check("Rational(2,3)", "2/3");
		check("Rational(3,1)", "3");
		check("Rationalize(6.75)", "27/4");
		check("Rationalize(42)", "42");
	}

	public void testSystem000b() {
		// github issue #42
		check(" ((1 - (i/10)) / (1.0 - (i/10)))", //
				"1.0");
		check("(1 - ((1 - (i/10)) / (1.0 - (i/10))))", //
				"0.0");

		check("(1 - ((1 - (i/10)) / (1 - (i/10))))", "0");
		check("((1 - (i/10)) / (1 - (i/10)))", "1");
		check("Simplify(1+(1-x)^2/(-1+x))", "x");
		check("(1-x)^2/(-1+x)^3", "(1-x)^2/(-1+x)^3");
		check("(1-x)^4/(-1+x)^8", "1/(1-x)^4");
		check("1+(-1+x)/(1-x)", "1+(-1+x)/(1-x)");
		check("i * ((1 - ((i * 1) / 10)) / (1 - ((i * 1) / 10)))", "i");
		check("((1 - ((i * 1.0) / 10)) / (1.0 - ((i * 1.0) / 10)))", "1.0");

		check("Simplify((i * (1.0 - 0.1 * i)) / (1.0 - 0.1 * i))", "i");
		check("a-2*I", "-I*2+a");
		check("Complex(2,1/3)", "2+I*1/3");
		check("Complex(3/4,3)", "3/4+I*3");
		check("(1-I)^2", "-I*2");
		check("(1+I)^2", "I*2");
		check("(2+I)^2", "3+I*4");
		check("Sqrt(-I*2)", "1-I");
		check("Sqrt(I*2)", "1+I");
		check("Sqrt(-I*162)", "9-I*9");
		check("Sqrt(I*162)", "9+I*9");
		check("Sqrt(-I*2/9)", "1/3-I*1/3");
		check("Sqrt(I*2/9)", "1/3+I*1/3");
		check("-I", "-I");

		check("Sqrt(-I*(1/3))", "Sqrt(-I*1/3)");
		check("Sqrt(I*(1/3))", "Sqrt(I*1/3)");
		check("Sqrt(-I*(7/3))", "Sqrt(-I*7/3)");
		check("Sqrt(I*(7/3))", "Sqrt(I*7/3)");

		check("(-I*1/16)/E^(I*4*x)+I*1/8/E^(I*2*x)", //
				"(-I*1/16)/E^(I*4*x)+(I*1/8)/E^(I*2*x)");
	}

	public void testSystem000c() {
		String s = System.getProperty("os.name");
		if (s.contains("Windows")) {

			check("N(0.5,50)", "5*10^-1");

			check("N(Sqrt(2)+I*0.3,50)", "1.41421356237309504880168872420969807856967187537694+I*3*10^-1");

			check("N(I*Sqrt(2)+I*Sqrt(3))", "I*3.14626");
			check("N(I*Sqrt(2)+I*Sqrt(3),50)", "I*3.1462643699419723423291350657155704455124771291873");

			check("N(Sqrt(2),50)", "1.41421356237309504880168872420969807856967187537694");

			check("N(2+Sqrt(2))", "3.41421");
			check("N(2+Sqrt(2),50)", "3.4142135623730950488016887242096980785696718753769");

			check("N(42-Sqrt(2))", "40.58579");
			check("N(42-Sqrt(2),50)", //
					"4.0585786437626904951198311275790301921430328124623*10^1");

			check("N(42/Sqrt(2))", "29.69848");
			check("N(42/Sqrt(2),50)", "2.9698484809834996024835463208403659649963109382915*10^1");

			check("N(42*Sqrt(2))", "59.39697");
			check("N(42*Sqrt(2),50)", "5.9396969619669992049670926416807319299926218765831*10^1");

			check("N(Sqrt(0.5)^5.0)", "0.176777");
			check("N(Sqrt(0.5)^5.0,50)", "1.7677669529663688110021109052621225982120898442211*10^-1");

			check("N(0.5^5.0)", "0.03125");
			check("N(0.5^5.0,50)", "3.125*10^-2");

			check("N(Pi,50)", "3.1415926535897932384626433832795028841971693993751");
			check("N(E,50)", "2.7182818284590452353602874713526624977572470936999");

			check("N(E*Pi)", "8.53973");
			check("N(E*Pi,50)", "8.5397342226735670654635508695465744950348885357651");

			check("N(E+Pi)", "5.85987");
			check("N(E+Pi,50)", "5.859874482048838473822930854632165381954416493075");

			check("N(Sin(2))", "0.909297");
			check("N(Sin(2), 50)", "9.0929742682568169539601986591174484270225497144789*10^-1");

			check("N(Cot(2))", "-0.457658");
			check("N(Cot(2), 50)", "-4.5765755436028576375027741043204727642848632923167*10^-1");

			check("N(Cosh(2))", "3.7622");
			check("N(Cosh(2), 50)", "3.7621956910836314595622134777737461082939735582307");

			check("N(Coth(2))", "1.03731");
			check("N(Coth(2), 50)", "1.0373147207275480958778097647678207116623912692491");

			check("N(Csch(2))", "0.275721");
			check("N(Csch(2), 50)", "2.7572056477178320775835148216302712124962267199125*10^-1");

			check("N(Csch(2+3*I))", "-0.272549+I*(-0.0403006)");
			check("N(Csch(2+3*I), 50)",
					"-2.7254866146294019951249847793270892405353986449544*10^-1+I*(-4.0300578856891521875132479542869867780842475424268*10^-2)");

			check("N(ArcCot(3))", "0.321751");
			check("N(ArcCot(3), 50)", "3.217505543966421934014046143586613190207552955576*10^-1");

			check("N(ArcCot(I+3))", "0.294001+I*(-0.0919312)");
			check("N(ArcCot(I+3), 50)",
					"2.940013017737837756228055403125427138008536230279*10^-1+I*(-9.1931195031329338315749241930066798877571707278677*10^-2)");

			check("N(ArcCoth(3))", "0.346574");
			check("N(ArcCoth(3), 50)", "3.465735902799726547086160607290882840377500671801*10^-1");

			check("N(ArcCoth(I+3))", "0.305944+I*(-0.109334)");
			check("N(ArcCoth(I+3), 50)",
					"3.059438579055289264121938211617347240156504145793*10^-1+I*(-1.093344729369709810210868751249692955571964779524*10^-1)");

			check("N(Sqrt(10), 50)", "3.1622776601683793319988935444327185337195551393252");
			check("N(I, 50)", "I*1");

			check("Round(1.5)", "2");
			check("N(Round(1.5))", "2.0");
			check("N(Round(1.5),50)", "2");
			check("N(Round(1.4))", "1.0");
			check("N(Round(1.4),50)", "1");

			// test alias EvalF
			check("evalf(0.5,50)", //
					"5*10^-1");

			check("evalf(Sqrt(2)+I*0.3,50)", //
					"1.41421356237309504880168872420969807856967187537694+I*3*10^-1");

			check("evalf(I*Sqrt(2)+I*Sqrt(3))", //
					"I*3.14626");
			check("evalf(I*Sqrt(2)+I*Sqrt(3),50)", //
					"I*3.1462643699419723423291350657155704455124771291873");

			check("evalf(Sqrt(2),50)", //
					"1.41421356237309504880168872420969807856967187537694");

			check("EvalF(2+Sqrt(2))", //
					"3.41421");
			check("EvalF(2+Sqrt(2),50)", //
					"3.4142135623730950488016887242096980785696718753769");
		}
	}

	public void testSystem001() {
		check("Sin(0.5)", "0.479426");
		check("Cot(0.5)", "1.83049");
		check("Csc(0.5)", "2.08583");
		check("Sec(0.5)", "1.13949");
		check("N(Cot(I))", "I*(-1.31304)");
		check("N(Cot(I),50)", "I*(-1.3130352854993313036361612469308478329120139412404)");
		check("N(Csc(I))", "I*(-0.850918)");
		check("N(Sec(I))", "0.648054");
	}

	public void testSystem002() {
		check("Sin(x-218*Pi)", "Sin(x)");

		check("Log(-E)", "1+I*Pi");
		check("ArcCsc(-x)", "-ArcCsc(x)");
		check("ArcCsch(-x)", "-ArcCsch(x)");

		check("Sin(I)", "I*Sinh(1)");
		check("Sin(3+I*42)", "Sin(3+I*42)");
		check("Sin(I*x)", "I*Sinh(x)");
		check("Sin(4*I*x)", "I*Sinh(4*x)");

		check("Cos(I)", "Cosh(1)");
		check("Cos(3+I*42)", "Cos(3+I*42)");
		check("Cos(I*x)", "Cosh(x)");
		check("Cos(4*I*x)", "Cosh(4*x)");
		check("Cos(2*I)", "Cosh(2)");
		check("Cos(x+Pi/2)", "-Sin(x)");
		// check("Cos(x-Pi/2)", "Sin(x)");

		check("Cot(I*x)", "-I*Coth(x)");
		check("Csc(I*x)", "-I*Csch(x)");
		check("Sec(I*x)", "Sech(x)");
		check("Tan(I*x)", "I*Tanh(x)");

		check("Cosh(I*x)", "Cos(x)");
		check("Csch(I*x)", "-I*Csc(x)");
		check("Sech(I*x)", "Sec(x)");
		check("Sinh(I*x)", "I*Sin(x)");
		check("Tanh(I*x)", "I*Tan(x)");
		check("Coth(I*x)", "-I*Cot(x)");

		check("ArcCos(I*x)", "ArcCos(I*x)");
		check("ArcCot(I*x)", "-I*ArcCoth(x)");
		check("ArcCsc(I*x)", "-I*ArcCsch(x)");
		check("ArcSec(I*x)", "ArcSec(I*x)");
		check("ArcSin(I*x)", "I*ArcSinh(x)");
		check("ArcTan(I*x)", "I*ArcTanh(x)");

		check("ArcCosh(I*x)", "ArcCosh(I*x)");
		check("ArcCoth(I*x)", "-I*ArcCot(x)");
		check("ArcCsch(I*x)", "-I*ArcCsc(x)");
		check("ArcSech(I*x)", "ArcSech(I*x)");
		check("ArcSinh(I*x)", "I*ArcSin(x)");
		check("ArcTanh(I*x)", "I*ArcTan(x)");

		check("Log(-E)", "1+I*Pi");
		check("Log(-1)", "I*Pi");
		check("Log(E)", "1");
		check("Log(9)*Log(3)^(-1)", "2");

		check("Sin(x+2*Pi)", "Sin(x)");
		check("Sin(x-218*Pi)", "Sin(x)");
		check("Sin(x+11*Pi)", "-Sin(x)");
		check("Sin(x-3*Pi)", "-Sin(x)");

		check("Cos(x+218*Pi)", "Cos(x)");
		check("Cos(x-218*Pi)", "Cos(x)");
		check("Cos(x+11*Pi)", "-Cos(x)");
		check("Cos(x-3*Pi)", "-Cos(x)");

		check("Sec(x+218*Pi)", "Sec(x)");
		check("Sec(x-218*Pi)", "Sec(x)");
		check("Sec(x+11*Pi)", "-Sec(x)");
		check("Sec(x-3*Pi)", "-Sec(x)");

		check("Csc(x+218*Pi)", "Csc(x)");
		check("Csc(x-218*Pi)", "Csc(x)");
		check("Csc(x+11*Pi)", "-Csc(x)");
		check("Csc(x-3*Pi)", "-Csc(x)");

		check("Cot(x+218*Pi)", "Cot(x)");
		check("Cot(x-218*Pi)", "Cot(x)");
		check("Cot(x+11*Pi)", "Cot(x)");
		check("Cot(x-3*Pi)", "Cot(x)");

		check("Tan(x+218*Pi)", "Tan(x)");
		check("Tan(x-218*Pi)", "Tan(x)");
		check("Tan(x+11*Pi)", "Tan(x)");
		check("Tan(x-3*Pi)", "Tan(x)");
	}

	public void testSystem003() {
		// TODO use ExprParser#getReal() if apfloat problems are fixed
		// check("1.6969545188681513E4", "1.6969545188681513e4");
		// check("1.6969545188681513*^4", "1.69695451886815129e4");
		// check("1.6969545188681513*^-10", "1.6969545188681513e-10");
		if (!Config.EXPLICIT_TIMES_OPERATOR) {
			// implicit times operator '*' allowed
			check("1.6969545188681513E4", "1.69695*e4");
			check("1.6969545188681513*^-10", "1.69695*10^-10");

			check("-0.0001*E+15", "14.99973");
			check("-0.0001E+15", "14.99973");
			check("-0.0001*E-15", "-15.00027");
			check("-0.0001E-15", "-15.00027");
		} else {
			check("1.6969545188681513E4", "16969.54519");
			check("1.6969545188681513*^-10", "0.0");

			check("-0.0001*E+15", "14.99973");
			check("-0.0001E+15", "-100000000000.0");
			check("-0.0001*E-15", "-15.00027");
			check("-0.0001E-15", "0.0");
		}
	}

	public void testSystem004() {
		check("1.0-(1.0-1*(2))", "2.0");
		check("1-(1-1*(2))", "2");

		if (!Config.EXPLICIT_TIMES_OPERATOR) {
			// implicit times operator '*' allowed
			check("Chop((-2.4492935982947064E-16)+I*(-1.0E-19))", "-22.65787+I*(-21.71828)");
			check("Chop(2.0+I*(-2.4492935982947064E-16))", "2.0+I*(-22.65787)");
			check("Chop((-2.4492935982947064E-16)+I*0.5)", "-22.65787+I*0.5");

			check("Chop({2.0+I*(-2.4492935982947064E-16),(-2.4492935982947064E-16)+I*0.5})",
					"{2.0+I*(-22.65787),-22.65787+I*0.5}");
		} else {
			check("Chop((-2.4492935982947064E-16)+I*(-1.0E-19))", "0");
			check("Chop(2.0+I*(-2.4492935982947064E-16))", "2.0");
			check("Chop((-2.4492935982947064E-16)+I*0.5)", "I*0.5");

			check("Chop({2.0+I*(-2.4492935982947064E-16),(-2.4492935982947064E-16)+I*0.5})", "{2.0,I*0.5}");

		}
	}

	public void testSystem005() {
		check("1/3+1/4", "7/12");
	}

	public void testSystem006() {
		check("2/3*3/4", "1/2");
	}

	public void testSystem007() {
		check("32^(1/4)", "2*2^(1/4)");
		check("(-1)^(1/3)", "(-1)^(1/3)");
		check("-12528^(1/2)", "-12*Sqrt(87)");
		check("(-27)^(1/3)", "3*(-1)^(1/3)");
		check("(-27)^(2/3)", //
				"9*(-1)^(2/3)");
		check("8^(1/3)", "2");
		check("81^(3/4)", "27");
		check("82^(3/4)", "82^(3/4)");
		check("(20/7)^(-1)", "7/20");
		check("(-27/64)^(1/3)", "3/4*(-1)^(1/3)");
		check("(27/64)^(-2/3)", "16/9");
		// check("16/9","");
		check("10^4", "10000");
		check("(-80/54)^(2/3)", //
				"4/9*(-5)^(2/3)");
	}

	public void testSystem008() {
		check("I^(-1)", "-I");
	}

	public void testSystem009() {
		check("1/2-I", "1/2-I");
	}

	public void testSystem010() {
		check("1/2+I*(-1/3)", "1/2-I*1/3");
	}

	public void testSystem011() {
		check("0.5-I", "0.5+I*(-1.0)");
	}

	public void testSystem012() {
		check("$a=2;$a+=b", "2+b");
	}

	public void testSystem013() {
		check("$a=2;$a-=b", "2-b");
	}

	public void testSystem014() {
		check("$a=2;$a*=b", "2*b");
	}

	public void testSystem015() {
		check("$a=2;$a/=b", "2/b");
	}

	public void testSystem016() {
		check("Depth(13)", "1");
	}

	public void testSystem017() {
		check("Depth({})", "2");
	}

	public void testSystem018() {
		check("Depth(f(x))", "2");
	}

	public void testSystem019() {
		check("Depth(f(x,g(y)))", "3");

	}

	public void testSystem020() {
		check("LeafCount(s(x, y))", "3");
		// issue #90
		check("LeafCount(x^2-x)", "7");
		check("LeafCount(x*(x-1))", "5");
	}

	public void testSystem021() {
		check("LeafCount(s(a)[x, y])", "4");
	}

	public void testSystem022() {
		check("LeafCount(a)", "1");
	}

	public void testSystem023() {
		check("LeafCount({})", "1");
	}

	public void testSystem024() {
		check("Map(f,a)", "a");
		check("f/@a", "a");
	}

	public void testSystem025() {
		check("Map(f, s(g(u,v), y),{-3,-2})", "f(s(f(g(u,v)),y))");
	}

	public void testSystem026() {
		check("Map(f, s(g(u,v), y),{-2,-1})", "s(f(g(f(u),f(v))),f(y))");

	}

	public void testSystem027() {
		check("Map(f, s(g(u,v), y),{-2})", "s(f(g(u,v)),y)");
	}

	public void testSystem028() {
		check("Map(f, s(x, y))", "s(f(x),f(y))");
		check("f/@s(x, y)", "s(f(x),f(y))");
	}

	public void testSystem029() {
		check("Map((#+2)&, s(x, y))", "s(2+x,2+y)");
		check("(#+2)&/@s(x, y)", "s(2+x,2+y)");
	}

	public void testSystem030() {
		check("Map(f, s(g(u,v), y))", "s(f(g(u,v)),f(y))");
		check("f/@ s(g(u,v), y)", "s(f(g(u,v)),f(y))");
	}

	public void testSystem031() {
		check("Map(f, s(g(u,v), y),{2})", "s(g(f(u),f(v)),y)");
	}

	public void testSystem032() {
		check("Map((#+2)&, s(g(u,v), y),{2})", "s(g(2+u,2+v),y)");
	}

	public void testSystem033() {
		check("Map(f, s(g(u(x(1)),v), y),{2,-1})", "s(g(f(u(f(x(f(1))))),f(v)),y)");
	}

	public void testSystem034() {
		check("Map(f, s(g(u,v), y),{2,-1})", "s(g(f(u),f(v)),y)");

	}

	public void testSystem035() {
		check("MapAll(f, s(x, y))", "f(s(f(x),f(y)))");
		check("f//@s(x, y)", "f(s(f(x),f(y)))");
		check("MapAll(f,a)", "f(a)");
		check("f//@a", "f(a)");
	}

	public void testSystem036() {
		check("Thread(f({x,y,z},{u,v,w}))", "{f(x,u),f(y,v),f(z,w)}");
		check("Thread({x,y,z}=={u,v,w})", "{x==u,y==v,z==w}");
		check("Thread(f(x==y),Equal)", "f(x)==f(y)");
		// check("MapThread(f, {{x,y,z},{u,v,w}})", "{f(x,u),f(y,v),f(z,w)}");
		// check("MapThread(f, {{{x,y,z},{u,v,w}}}, 2)", "");
	}

	public void testSystem037() {
		check("Trace(a)", "{}");
	}

	public void testSystem038() {
		// bitbucket issue#15
		check("Together(-(2*x-6)^(-1)-2*(-x+2)*(2*x-6)^(-2))", "-1/(-18+12*x-2*x^2)");
		check("Simplify(-(2*x-6)^(-1)-2*(-x+2)*(2*x-6)^(-2))", "1/(2*(3-x)^2)");

		check("D(Cosh(x),x)", "Sinh(x)"); // issue#39
		check("D(x^4, x)", "4*x^3");
		check("D((-x+2)/(2*x-6), x)", //
				"(-2*(2-x))/(6-2*x)^2-1/(-6+2*x)");
		check("D((2*x-6)^(-1), x)", "-2/(6-2*x)^2");

		// (-x+2)*(2*x-6)^(-2)*2

		check("D((1+x^2)^Sin(x),x)", "(1+x^2)^Sin(x)*(Cos(x)*Log(1+x^2)+(2*x*Sin(x))/(1+x^2))");
		check("D(Sin(x^2),x)", "2*x*Cos(x^2)");
		check("D(Sin(x)^2,x)", "2*Cos(x)*Sin(x)");
		check("D(f(Sin(x)),{x,3})", "-Cos(x)*f'(Sin(x))-3*Cos(x)*Sin(x)*f''(Sin(x))+Cos(x)^3*Derivative(3)[f][Sin(x)]");

		check("D(f(x)/g(x), x)", "f'(x)/g(x)+(-f(x)*g'(x))/g(x)^2");

		check("Trace(D(Sin(x),x))", "{{NotListQ(x),True},{{D(x,x),1},1*Cos(x),Cos(x)},Cos(x)}");
		check("D(Sin(x)^Cos(x),x)", "(Cos(x)*Cot(x)-Log(Sin(x))*Sin(x))*Sin(x)^Cos(x)");
		check("Trace(D(Sin(x)^Cos(x),x))",
				"{{{IntegerQ(#1)&&#1<0&[Cos(x)],IntegerQ(Cos(x))&&Cos(x)<0,{IntegerQ(Cos(x)),False},False}},Sin(x)^Cos(x)*(D(Cos(x),x)*Log(Sin(x))+(Cos(x)*D(Sin(x),x))/Sin(x)),{{IntegerQ(#1)&&#1<\n"
						+ "0&[Cos(x)],IntegerQ(Cos(x))&&Cos(x)<0,{IntegerQ(Cos(x)),False},False}},{{{{NotListQ(x),True},{{D(x,x),\n"
						+ "1},(-1)*1*Sin(x),-Sin(x)},-Sin(x)},Log(Sin(x))*-Sin(x),-Log(Sin(x))*Sin(x)},{{{NotListQ(x),True},{{D(x,x),\n"
						+ "1},1*Cos(x),Cos(x)},Cos(x)},{{IntegerQ(#1)&&#1<0&[-1],IntegerQ(-1)&&-1<0,{IntegerQ(\n"
						+ "-1),True},{-1<0,True},True},{{(-1)*(-1),1},Csc(x)^1,{IntegerQ(#1)&&#1<0&[1],IntegerQ(\n"
						+ "1)&&1<0,{IntegerQ(1),True},{1<0,False},False},Csc(x)},Csc(x)},Cos(x)*Cos(x)*Csc(x),Cot(x)^\n"
						+ "1*Cos(x),{{IntegerQ(#1)&&#1<0&[1],IntegerQ(1)&&1<0,{IntegerQ(1),True},{1<0,False},False},Cot(x)},Cos(x)*Cot(x)},Cos(x)*Cot(x)-Log(Sin(x))*Sin(x)},(Cos(x)*Cot(x)-Log(Sin(x))*Sin(x))*Sin(x)^Cos(x),{{IntegerQ(#1)&&#1<\n"
						+ "0&[Cos(x)],IntegerQ(Cos(x))&&Cos(x)<0,{IntegerQ(Cos(x)),False},False}}}");
	}

	public void testSystem039() {
		check("a+a", "2*a");

		// test numericMode:
	}

	public void testSystem040() {
		checkNumeric("(-15.0)^.5", "2.3715183290419594E-16+I*3.872983346207417");
		checkNumeric("(-15.0)^0.5", "2.3715183290419594E-16+I*3.872983346207417");
		checkNumeric(".5^.5", "0.7071067811865476");
		checkNumeric("N((-15)^(1/2))", "2.3715183290419594E-16+I*3.872983346207417");
		checkNumeric("N(Sin(1/2))", "0.479425538604203");
		checkNumeric("N(1/6*(I*44^(1/2)+2))", "0.3333333333333333+I*1.1055415967851332");
		// test automatic numericMode (triggered by double value "0.5"):
	}

	public void testSystem041() {
		checkNumeric("Sin(0.5)", "0.479425538604203");
	}

	public void testSystem042() {
		check("Sin(Pi)", "0");
	}

	public void testSystem043() {
		check("Sin(ArcSin(a))", "a");

	}

	public void testSystem044() {

		check("$test(F_(a_)):={a,b,F,m,x};$test(g(h))", "{h,b,g,m,x}");
		check("clear($test);$test(F_(a_.*x_^m_.)):={a,b,F,m,x};$test(g(h*y^2))", "{h,b,g,2,y}");
		check("$test(F_(a_.*x_^l_.),x_Symbol,b_.*x_^m_):={a,b,F,l,m,x};$test(g(h*y^2),y,k*y^3)", "{h,k,g,2,3,y}");
		check("$test(F_(a_.*x_^l_.),x_Symbol,b_.*x_^m_):={a,b,F,l,m,x};$test(g(y),y,y^3)", "{1,1,g,1,3,y}");
		check("$test(F_(a_.*x_^l_.),x_Symbol,b_.*x_^m_):={a,b,F,l,m,x};$test(g(y),y,k*y^3)", "{1,k,g,1,3,y}");
		check("$test(F_(a_.*x_^l_.),x_Symbol,b_.*x_^m_):={a,b,F,l,m,x};$test(g(2*y),y,k*y^3)", "{2,k,g,1,3,y}");
		check("a(b(x_),_)", "a(b(x_),_)");
	}

	public void testSystem045() {
		check("MemberQ({g(x), f(a)}, f(x_))", "True");
	}

	public void testSystem046() {
		check("MemberQ({g(x, c), f(a, b)}, f(x_, b))", "True");
	}

	public void testSystem047() {
		check("MemberQ({g(x), f(a, b)}, f(x_))", "False");
	}

	public void testSystem048() {
		check("MemberQ({g(x, c), f(a, b, c)}, f(x_, b))", "False");

	}

	public void testSystem049() {
		check("FreeQ({g(x), f(a)}, f(x_))", "False");
		check("FreeQ({g(x), f(a)}, h(x_))", "True");
	}

	public void testSystem050() {
		check("FreeQ({g(x,3), f(a)}, 3)", "False");
	}

	public void testSystem051() {
		check("FreeQ(3, 2)", "True");
	}

	public void testSystem052() {
		check("FreeQ({g(x,3), f(a)}, 2)", "True");
	}

	public void testSystem053() {
		check("FreeQ({g(x), f(a)}, f(_Integer))", "True");

	}

	public void testSystem054() {
		check("NumberQ(1/3)", "True");
	}

	public void testSystem055() {
		check("NumberQ(2.5)", "True");

	}

	public void testSystem056() {
		check("PrimeQ(997)", "True");
		check("NextPrime(41)", "43");
		check("NextPrime(37)", "41");
		check("NextPrime(37,2)", "43");
		check("NextPrime(37,3)", "47");
		check("CoprimeQ(6,35)", "True");
		check("CoprimeQ(6,27)", "False");
		check("CoprimeQ(6,35,49)", "False");
	}

	public void testSystem057() {
		check("$var=10", "10");
		check("$var", "10");
		check("$var+$var", "20");
		check("$function(2)=42", "42");
		check("$function(2)", "42");
	}

	public void testSystem058() {
		check("$g(x__):={x};$h(x_Integer,y__):={x,y}", "");
		check("$g(test)", "{test}");
		check("$g(test1, test2)", "{test1,test2}");
		check("$h(42, test1)", "{42,test1}");
		check("$h(42, test1, test2)", "{42,test1,test2}");
		check("$h(test1, test2)", "$h(test1,test2)");
	}

	// two rules are associated with condf
	public void testSystem059() {
		check("$condf(x_,y_):={x,y}/;NumberQ(x);$condf(x_,z_):={z,x}/;NumberQ(z)", "");
		check("$condf(c,7)", "{7,c}");
		check("{$condf(a,a),$condf(42,b),$condf(c,7)}", "{$condf(a,a),{42,b},{7,c}}");
		// only the last rule is associated with condg
		check("$condg(x_,y_)={x,y};$condg(x_,y_):={y,x};{$condg(a,b),$condg(42,b),$condg(c,7)}",
				"{{b,a},{b,42},{7,c}}");

	}

	public void testSystem060() {
		check("$pf(y_):={y}", "");
		check("$pf(test)", "{test}");
		check("$pf(y_,a):={y}", "");
		check("$pf(test,a)", "{test}");
		check("$pf(test,b)", "$pf(test,b)");
	}

	// test attribute ISymbol.FLAT
	public void testSystem061() {
		check("SetAttributes($f, Flat)", "");
		check("$f(a,b,$f(x,y,$f(u,v)),z)", "$f(a,b,x,y,u,v,z)");
		check("$f(x_,y_):={x,y}", "");
		check("$f(a,b,c)", "{$f(a),{$f(b),$f(c)}}");
	}

	// test attribute ISymbol.ORDERLESS
	public void testSystem062() {
		check("$i(a_+(b_.*x_^n_)^p_) := {a,b,n,p,x}", "");
		check("SetAttributes($o, Orderless)", "");
		check("$o(z,d,a,b,g)", "$o(a,b,d,g,z)");
		check("$o(9,12,3,33)", "$o(3,9,12,33)");
		check("$o(x_,y_):={x,y}", "");

		check("$o(a,b,10)", "$o(10,a,b)");
		check("$o(a,10)", "{10,a}");
	}

	// test attribute ISymbol.ONEIDENTITY
	public void testSystem063() {
		check("SetAttributes($oi, OneIdentity)", "");
		check("$oi($oi(test))", "$oi($oi(test))");

	}

	public void testSystem064() {
		check("SetAttributes($ooi, {Orderless, OneIdentity})", "");
		check("$ooi(z,d,a,b,g)", "$ooi(a,b,d,g,z)");
		check("$ooi(9,12,3,33)", "$ooi(3,9,12,33)");
		check("$ooi(x_?NumberQ,y_):={x,y}", "");
		check("$ooi(a,10)", "{10,a}");
	}

	// test attribute ISymbol.ORDERLESS && ISymbol.FLAT
	public void testSystem065() {
		check("SetAttributes($of, {Orderless,Flat})", "");
		check("$of(z,d,a,b,g)", "$of(a,b,d,g,z)");
		check("$of(9,12,3,33)", "$of(3,9,12,33)");
		check("$of(x_,y_):={x,y}", "");
		check("$of(a,10)", "{10,a}");
		check("$of(a,10,b)", "{10,{a,b}}");
		check("$of(a,10,b,c)", "{10,{a,{b,c}}}");
	}

	// test attribute ISymbol.ORDERLESS && ISymbol.FLAT &&
	// ISymbol.ONEIDENTITY
	public void testSystem066() {
		check("SetAttributes($ofoi, {Orderless,Flat,OneIdentity})", "");
		check("$ofoi(z,d,a,$ofoi(b),g)", "$ofoi(a,b,d,g,z)");
		check("$ofoi(9,12,$ofoi(3,33))", "$ofoi(3,9,12,33)");
		check("$ofoi(x_,y_):={x,y}", "");
		check("$ofoi(a,10)", "{10,a}");
		check("$ofoi(a,10,b,c)", "{10,{a,{b,c}}}");
	}

	public void testSystem067() {
		check("$int(Sin(x_)/Sqrt(x_),x_Symbol):={x}", "");
		check("$int(Sin(a)/Sqrt(a),a)", "{a}");
		check("$int(Sin(x)/Sqrt(x),x)", "{x}");
		check("$int(x^(-1/2)*Sin(x),x)", "{x}");
	}

	public void testSystem068() {
		// test attribute ISymbol.LISTABLE
		check("Sin({a,b,c})", "{Sin(a),Sin(b),Sin(c)}");
		check("NumberQ({a,b,c})", "False");
	}

	public void testSystem069() {
		check("SetAttributes($l, Listable)", "");
		check("$l({a,b,c},d)", "{$l(a,d),$l(b,d),$l(c,d)}");

	}

	public void testSystem070() {
		check("Degree", "Pi/180");
		check("GoldenRatio+GoldenRatio", "2*GoldenRatio");
	}

	public void testSystem071() {
		checkNumeric("N(EulerGamma)", "0.5772156649015329");
		check("D(Sinh(x),x)", "Cosh(x)");
	}

	public void testSystem072() {
		check("$isatom(_?AtomQ) := True;$isatom(10)", "True");

	}

	public void testSystem073() {
		check("D(Log(Sin(x)),x)", "Cot(x)");
	}

	public void testSystem074() {
		check("D(f(a)^2+g(x)^3,x)", "3*g(x)^2*g'(x)");
	}

	public void testSystem075() {
		check("D(f(x)^2+g(x)^3,x)", "2*f(x)*f'(x)+3*g(x)^2*g'(x)");
	}

	public void testSystem076() {
		check("D(2*x^2 + 1,x) ", "4*x");
	}

	public void testSystem077() {
		check("D(Sin(x)*Cos(x),x)", "Cos(x)^2-Sin(x)^2");
	}

	public void testSystem078() {
		check("D(Sin(x) + Cos(y), {x, y})", "D(Cos(y)+Sin(x),{x,y})");
		check("D(Sin(x)^Cos(x),x)", "(Cos(x)*Cot(x)-Log(Sin(x))*Sin(x))*Sin(x)^Cos(x)");
		check("D(Cos(x)^10,{x,3})", "280*Cos(x)^9*Sin(x)-720*Cos(x)^7*Sin(x)^3");
		check("D(Cos(x*y)/(x+y),x,y)",
				"(2*Cos(x*y))/(x+y)^3+(-x*y*Cos(x*y))/(x+y)+(x*Sin(x*y))/(x+y)^2+(y*Sin(x*y))/(x+y)^\n"
						+ "2-Sin(x*y)/(x+y)");
		check("D(x^2*Sin(y), x, y)", "2*x*Cos(y)");
		check("D(x^2*Sin(y), y, x)", "2*x*Cos(y)");
		check("D(x^2*Sin(y), {{x, y}})", "{2*x*Sin(y),x^2*Cos(y)}");
		check("D({Sin(y), Sin(x) + Cos(y)}, {{x, y}}, {{x,y}})", "{{{0,0},{0,-Sin(y)}},{{-Sin(x),0},{0,-Cos(y)}}}");
		check("D(Sin(y),{{x,y}},{{x,y}})", "{{0,0},{0,-Sin(y)}}");
		check("D(Sin(y),{{x,y},2})", "{{0,0},{0,-Sin(y)}}");
		check("D({Sin(y), Sin(x) + Cos(y)}, {{x, y}, 2})", "{{{0,0},{0,-Sin(y)}},{{-Sin(x),0},{0,-Cos(y)}}}");
	}

	public void testSystem079() {
		check("{{1,2},{3,4}}.{{1,2},{3,4}}", "{{7,10},\n" + " {15,22}}");
		check("$x.$y", "$x.$y");
	}

	public void testSystem080() {
		check("{{1,2},{3,4}}.{{1,2},{3,-4}}.{{1,2},{3,4}}", "{{-11,-10},\n" + " {-15,-10}}");
	}

	public void testSystem081() {
		check("Inverse(s*{{1,0,0},{0,1,0},{0,0,1}}-{{-1,1,1},{-4,-4,1},{1,1,1}})",
				"{{(5-3*s-s^2)/(10-s-4*s^2-s^3),s/(-10+s+4*s^2+s^3),(5+s)/(-10+s+4*s^2+s^3)},\n"
						+ " {(5-4*s)/(-10+s+4*s^2+s^3),(2-s^2)/(10-s-4*s^2-s^3),(-3+s)/(-10+s+4*s^2+s^3)},\n"
						+ " {-s/(10-s-4*s^2-s^3),(-2-s)/(10-s-4*s^2-s^3),(8+5*s+s^2)/(-10+s+4*s^2+s^3)}}");
		check("N(Inverse({{1,2.0},{3,4}}),50)", //
				"{{-2,1},\n" + " {1.5,-5*10^-1}}");

		check("Inverse({{1,2},{3,4}})", "{{-2,1},\n" + " {3/2,-1/2}}");
		check("Inverse({{1,2.0},{3,4}})", "{{-2.0,1.0},\n" + " {1.5,-0.5}}");

	}

	public void testSystem082() {
		check("N(Det({{Pi,2.0},{3,4}}))", "6.56637");
		check("N(Det({{Pi,2.0},{3,4}}),50)", "6.5663706143591729538505735331180115367886775975");

		check("Det({{1,2},{3,4}})", "-2");
		check("Det({{1,2.0},{3,4}})", "-2.0");
		check("Det({{a,b},{c,d}})", "-b*c+a*d");
	}

	public void testSystem083() {
		check("Eigenvalues({{0.0,1.0,-1.0},{1.0,1.0,0.0},{-1.0,0.0,1.0}})", "{2.0,1.0,-1.0}");
		check("Eigenvalues({{1,0,0},{0,1,0},{0,0,1}})", "{1.0,1.0,1.0}");
		check("Eigenvalues({{1,0,0},{-2,1,0},{0,0,1}})", "{1.0,1.0,1.0}");

		check("Fit({2,3,5,7,11,13},3,x)", "3.0-1.95238*x+1.10714*x^2.0-0.0833333*x^3.0");
		check("Fit({{1,1},{2,4},{3,9},{4,16}},2,x)", "x^2.0");
		check("Fit({1,4,9,16},2,x)", "x^2.0");

		// double()() m = { { 0.0, 1.0, -1.0 }, { 1.0, 1.0, 0.0 }, { -1.0, 0.0,
		// 1.0
		// } };
		// RealMatrix rm = new Array2DRowRealMatrix(m);
		// assertEquals(rm.toString(),
		// "Array2DRowRealMatrix{{0.0,1.0,-1.0},{1.0,1.0,0.0},{-1.0,0.0,1.0}}");
		// EigenDecompositionImpl ed = new EigenDecompositionImpl(rm,
		// MathUtils.SAFE_MIN);
		// RealVector rv0 = ed.getEigenvector(0);
		// RealVector rv1 = ed.getEigenvector(1);
		// RealVector rv2 = ed.getEigenvector(2);
		// assertEquals(rv0.toString(), "{-0,58; -0,58; 0,58}");
		// assertEquals(rv1.toString(), "{-0; -0,71; -0,71}");
		// assertEquals(rv2.toString(), "{0,82; -0,41; 0,41}");
		// check("Eigenvectors({{0.0,1.0,-1.0},{1.0,1.0,0.0},{-1.0,0.0,1.0}})",
		// "{{-0.5773502691896258,-0.5773502691896254,0.5773502691896258},{-1.7326808563254102E-16,-0.7071067811865475,-0.7071067811865475},{0.816496580927726,-0.40824829046386296,0.40824829046386296}}");
		// check("{{0.0,1.0,-1.0},{1.0,1.0,0.0},{-1.0,0.0,1.0}}.{-1.73268085632541E-16,-0.7071067811865476,-0.7071067811865474}",
		// "{-2.220446049250313E-16,-0.7071067811865478,-0.7071067811865471}");
	}

	public void testSystem084() {
		// trace of a matrix
		check("Tr({{1,2},{3,4}})", "5");
		check("Tr({{1,2},{3,4},{5,6}})", "5");
		check("Tr({{1,2,5},{3,4,6}})", "5");
	}

	public void testSystem085() {
		check("Tr({{a,b,c},{d,e,f}})", "a+e");
	}

	public void testSystem086() {
		check("Transpose({{1,2},{3,4}})", "{{1,3},\n" + " {2,4}}");
	}

	public void testSystem087() {
		check("Transpose({{1,2},{3,4},{5,6}})", "{{1,3,5},\n" + " {2,4,6}}");
	}

	public void testSystem088() {
		check("MatrixPower({{1,2},{3,4}},3)", "{{37,54},\n" + " {81,118}}");
	}

	public void testSystem089() {
		check("MatrixPower({{1,2},{3,4}},1)", "{{1,2},\n {3,4}}");
	}

	public void testSystem090() {
		check("MatrixPower({{1,2},{3,4}},0)", "{{1,0},\n" + " {0,1}}");
	}

	public void testSystem091() {
		check("HilbertMatrix(4)",
				"{{1,1/2,1/3,1/4},\n" + " {1/2,1/3,1/4,1/5},\n" + " {1/3,1/4,1/5,1/6},\n" + " {1/4,1/5,1/6,1/7}}");
		check("HilbertMatrix(2,3)", "{{1,1/2,1/3},\n" + " {1/2,1/3,1/4}}");
		check("HilbertMatrix(3,2)", "{{1,1/2},\n" + " {1/2,1/3},\n" + " {1/3,1/4}}");
	}

	public void testSystem092() {
		check("IdentityMatrix(0)", "{}");
		check("IdentityMatrix(1)", "{{1}}");
	}

	public void testSystem093() {
		check("IdentityMatrix(3)", "{{1,0,0},\n" + " {0,1,0},\n" + " {0,0,1}}");
	}

	public void testSystem094() {
		check("VandermondeMatrix({a,b,c})", "{{1,a,a^2},\n" + " {1,b,b^2},\n" + " {1,c,c^2}}");
		check("VandermondeMatrix({a,b,c,d})",
				"{{1,a,a^2,a^3},\n" + " {1,b,b^2,b^3},\n" + " {1,c,c^2,c^3},\n" + " {1,d,d^2,d^3}}");
	}

	public void testSystem095() {
		check("MatrixQ({{1, 2, 3}, {3, 4, 11}, {13, 7, 8}})", "True");
	}

	public void testSystem096() {
		check("MatrixQ({{1, 2, 3}, {3, 4, 11}, {13, 7, 8, 6}})", "False");
	}

	public void testSystem097() {
		check("VectorQ({-11/4,33/4,-5/4})", "True");
	}

	public void testSystem098() {
		check("VectorQ({-11/4,33/4,{-5/4,b}})", "False");
	}

	public void testSystem099() {
		check("{{1, 2, 3}, {3, 4, 11}, {13, 7, 8}}.{-11/4,33/4,-5/4}", "{10,11,12}");
	}

	public void testSystem100() {
		check("{-11/4,33/4,-5/4}.{{1, 2, 3}, {3, 4, 11}, {13, 7, 8}}", "{23/4,75/4,145/2}");
	}

	public void testSystem101() {
		// check("LUDecomposition({{1,2},{3,4}})", "{{{1,2},{3,-2}},{1,2},0}");
		check("LUDecomposition({{1,2},{3,4}})", "{\n" + "{{1,0},\n" + " {3,1}},\n" + "{{1,2},\n" + " {0,-2}},{1,2}}");
		check("LUDecomposition({{1,1},{5,-8}})", "{\n" + "{{1,0},\n" + " {5,1}},\n" + "{{1,1},\n" + " {0,-13}},{1,2}}");
	}

	// public void testSystem102() {
	// check("LUBackSubstitution({{{1,2},{3,-2}},{1,2},0},{1,2})", "{0,1/2}");
	// }
	public void testSystem102() {
		check("LinearSolve({ { 1/10, 6/5, 1/9 },{ 1, 59/45, 1/10 },{6/5, 1/10, 1/9 } },{ 1/10, 6/5, 1/9 })",
				"{99109/101673,10898/11297,-9034/869}");
		check("{ { 1/10, 6/5, 1/9 },{ 1, 59/45, 1/10 },{6/5, 1/10, 1/9 } }.{99109/101673,10898/11297,-9034/869}",
				"{1/10,6/5,1/9}");
	}

	public void testSystem103() {
		// check("LUDecomposition({{1, 2, 3}, {3, 4, 11}, {13, 7, 8}})",
		// "{{{1,2,3},{3,-2,2},{13,19/2,-50}},{1,2,3},0}");
		check("LUDecomposition({{1, 2, 3}, {3, 4, 11}, {13, 7, 8}})", "{\n" + "{{1,0,0},\n" + " {3,1,0},\n"
				+ " {13,19/2,1}},\n" + "{{1,2,3},\n" + " {0,-2,2},\n" + " {0,0,-50}},{1,2,3}}");

		// check(
		// "LUBackSubstitution({{{1,2,3},{3,-2,2},{13,19/2,-50}},{1,2,3},0},{10,11,12})"
		// ,
		// "{-11/4,33/4,-5/4}");
		check("SingularValueDecomposition({{ 24.0/25.0, 43.0/25.0 },{57.0/25.0, 24.0/25.0 }})", //
				"{{{0.6,0.8},\n" + " {0.8,-0.6}},{{3.0,0.0},\n" + " {0.0,1.0}},{{0.8,-0.6},\n" + " {0.6,0.8}}}");

		// See http://issues.apache.org/jira/browse/MATH-320:
		check("SingularValueDecomposition({{1,2},{1,2}})", //
				"{{{-0.707107,-0.707107},\n" + " {-0.707107,0.707107}},{{3.16228,0.0},\n"
						+ " {0.0,0.0}},{{-0.447214,-0.894427},\n" + " {-0.894427,0.447214}}}");
	}

	public void testSystem104() {
		check("{{a,2}}.{{a},{3}}", "{{6+a^2}}");
	}

	public void testSystem105() {
		check("{{3,4}}.{{a},{3}}", "{{12+3*a}}");
	}

	public void testSystem106() {
		check("{{a,2},{3,4}}.{{a,2},{3,4}}", "{{6+a^2,8+2*a},\n" + " {12+3*a,22}}");
	}

	public void testSystem107() {
		check("MatrixPower({{a,2},{3,4}},3)", "{{24+12*a+a^3,44+8*a+2*a^2},\n" + " {66+12*a+3*a^2,112+6*a}}");

	}

	public void testSystem108() {
		check("10!", "3628800");
	}

	public void testSystem109() {
		check("10!!", "3840");
		check("11!!", "10395");
		check("-10!!", "-3840");
		check("-11!!", "-10395");
		check("-12!!", "-46080");
		check("-13!!", "-135135");
	}

	public void testSystem110() {
		check("(n!)*x(3)", "n!*x(3)");
		check("Factorial2(x)", "x!!");
		check("Gamma(1/2)", "Sqrt(Pi)");
		check("Gamma(3/2)", "Sqrt(Pi)/2");
		check("Gamma(5/2)", "3/4*Sqrt(Pi)");
		check("Gamma(7/2)", "15/8*Sqrt(Pi)");
		check("Table(Gamma(x),{x,10})", "{1,1,2,6,24,120,720,5040,40320,362880}");
		check("Table(Gamma(x),{x,10.0})", "{1.0,1.0,2.0,6.0,24.0,120.0,720.0,5040.0,40320.0,362880.0}");

		check("Factorial2(0)", "1");
		check("Factorial2(-1)", "1");
	}

	public void testSystem111() {
		check("ArcCos(I)", "Pi/2+I*Log(-1+Sqrt(2))");
		check("Exp(Pi*I)", "-1");
		check("E^(Pi*I)", "-1");
	}

	public void testSystem112() {
		check("Table(x!,{x,10})", "{1,2,6,24,120,720,5040,40320,362880,3628800}");
		check("Table(x,{x,10.0})", "{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0}");
		check("Table(x!,{x,10.0})", "{1.0,2.0,6.0,24.0,120.0,720.0,5040.0,40320.0,362880.0,3.62880*10^6}");
	}

	public void testSystem113() {

	}

	public void testSystem114() {

	}

	public void testSystem115() {
		check("\"test1 \"<>\"test2 \"<>\"test3 \"", "test1 test2 test3 ");
	}

	public void testSystem116() {
		check("b**(a**c)", "b**a**c");
	}

	public void testSystem117() {
		check("b**13**14**d", "b**13**14**d");
	}

	public void testSystem118() {
		check("a==a==a", "True");
		check("3*x==6", "x==2");
		check("-3*x==6", "x==-2");
		check("3+x==6", "x==3");
		check("-3+x==6", "x==9");
	}

	public void testSystem119() {
		check("a!=b!=a", "a!=b!=a");
		check("3*x!=6", "x!=2");
		check("-3*x!=6", "x!=-2");
		check("3+x!=6", "x!=3");
		check("-3+x!=6", "x!=9");
	}

	public void testSystem120() {
		check("a===a===a", "True");
	}

	public void testSystem121() {
		check("a=!=b=!=c=!=d", "True");
	}

	public void testSystem122() {
		check("a=!=b=!=c=!=a", "False");
	}

	public void testSystem123() {
		check("I!=2/3!=42", "True");
	}

	public void testSystem124() {
		check("42!=2/3!=42", "False");
	}

	public void testSystem125() {
		check("SameQ(42)", "True");
	}

	public void testSystem126() {
		check("42.01===42.0", "False");
	}

	public void testSystem127() {
		check("23/29>Infinity", "False");

		check("Infinity>Infinity", "False");
		check("Infinity>=Infinity", "True");
		check("Infinity<Infinity", "False");
		check("Infinity<=Infinity", "True");

		check("23/29>Infinity", "False");
		check("42>Infinity", "False");

		check("23/29<Infinity", "True");
		check("42<Infinity", "True");

		check("23/29>=Infinity", "False");
		check("42>=Infinity", "False");

		check("23/29<=Infinity", "True");
		check("42<=Infinity", "True");

		check("5>4", "True");
		check("3*x>6", "x>2");
		check("-3*x>6", "x<-2");
		check("3+x>6", "x>3");
		check("-3+x>6", "x>9");

		check("1/3*x>6", "x>18");
		check("-1/3*x>6", "x<-18");
		check("1/3+x>6", "x>17/3");
		check("-1/3+x>6", "x>19/3");
		check("2*x+1>3*x", "x<1");

		check("Sin(Pi/100)>Sin(Pi/101)", "True");
	}

	public void testSystem128() {
		check("5>4>a>3", "4>a>3");
	}

	public void testSystem129() {
		check("5>4>a>3>2>c", "4>a>3>2>c");
	}

	public void testSystem130() {
		check("5>4>a>3>2>1>z>w", "4>a>3>1>z>w");
	}

	public void testSystem131() {
		check("5>=5", "True");
		check("3*x>=6", "x>=2");
		check("-3*x>=6", "x<=-2");
		check("3+x>=6", "x>=3");
		check("-3+x>=6", "x>=9");
	}

	public void testSystem132() {
		check("5>=4>=a>=3", "4>=a>=3");
	}

	public void testSystem133() {
		check("5>=4>=a>=3>=2>=c", "4>=a>=3>=2>=c");
	}

	public void testSystem134() {
		check("5>=4>=a>=3>=2>=1>=z>=w", "4>=a>=3>=1>=z>=w");
	}

	public void testSystem135() {
		check("3<4<a<5<6", "4<a<5");
		check("-1<1/2<1", "True");
		check("3*x<6", "x<2");
		check("-3*x<6", "x>-2");
		check("3+x<6", "x<3");
		check("-3+x<6", "x<9");
	}

	public void testSystem136() {
		check("0<1<=1", "True");
		check("4<=4<=a<=5<=6", "4<=a<=5");
		check("0<(1<2)", "True");
		check("0>1>=1", "False");
		check("0>(3>2)", "False");
		check("4>=4>3", "True");
		check("4>3>=3", "True");

		check("4<=4<5", "True");
		check("3*x<=6", "x<=2");
		check("-3*x<=6", "x>=-2");
		check("3+x<=6", "x<=3");
		check("-3+x<=6", "x<=9");
	}

	public void testSystem137() {
		check("Fibonacci(10)", "55");
		check("StirlingS2(6,3)", "90");
		check("StirlingS2(1300,1300)", "1");
		check("StirlingS2(10,2)", "511");
		check("StirlingS2(10,6)", "22827");
		check("StirlingS2(10,8)", "750");
	}

	public void testSystem138() {
		check("Append(z(a,b,c),d)", "z(a,b,c,d)");
	}

	public void testSystem139() {
		check("Prepend(z(a,b,c),d)", "z(d,a,b,c)");
	}

	public void testSystem140() {
		check("First(z(1,2,3))", "1");
		check("Last(z(1,2,3))", "3");
		check("Rest(z(1,2,3))", "z(2,3)");
		check("Most(z(1,2,3))", "z(1,2)");
	}

	public void testSystem141() {
		check("10==10", "True");
	}

	public void testSystem142() {
		check("10==1/2", "False");
	}

	public void testSystem143() {
		check("I==1/3", "False");
	}

	public void testSystem144() {
		check("I!=1/3", "True");
	}

	public void testSystem145() {
		check("qr==10", "qr==10");
	}

	public void testSystem146() {
		check("I", "I");
	}

	public void testSystem147() {
		check("I*I", "-1");
	}

	public void testSystem148() {
		check("1+I", "1+I");
	}

	public void testSystem149() {
		check("1/3+(1/4)*I", "1/3+I*1/4");
	}

	public void testSystem150() {
		check("3*a+4*a", "7*a");
	}

	public void testSystem151() {
		check("4*a+a", "5*a");
	}

	public void testSystem152() {
		check("(2*a*b)^(1/3)", //
				"2^(1/3)*(a*b)^(1/3)");
	}

	public void testSystem153() {
		check("a^1/3*b*a^3", "1/3*a^4*b");
	}

	public void testSystem154() {
		check("a^(1/3)*b*a^3", "a^(10/3)*b");
	}

	public void testSystem155() {
		check("a^2*b*a^3", "a^5*b");
	}

	public void testSystem156() {
		check("Sqrt(-42)", "I*Sqrt(42)");
	}

	public void testSystem157() {
		check("Sqrt(x)", "Sqrt(x)");
	}

	public void testSystem158() {
		check("a*(-1/3)+b*(-1/3)", "-a/3-b/3");

	}

	public void testSystem159() {
		check("Binomial(10,3)", "120");
		check("Binomial(2,4)", "0");
		check("Binomial(4,2)", "6");
		check("Binomial(100!,100!)", "1");
	}

	public void testSystem160() {
		check("CatalanNumber(-4)", "0");
	}

	public void testSystem161() {
		check("CatalanNumber(0)", "1");
	}

	public void testSystem162() {
		check("CatalanNumber(1)", "1");
	}

	public void testSystem163() {
		check("CatalanNumber(4)", "14");
		check("CatalanNumber(10)", "16796");
	}

	public void testSystem164() {
		check("HarmonicNumber(0)", "0");
		check("HarmonicNumber(1)", "1");
		check("HarmonicNumber(2)", "3/2");
		check("HarmonicNumber(10)", "7381/2520");
		check("HarmonicNumber(20)", "55835135/15519504");
	}

	public void testSystem165() {
		// check("Expand(1 / ((x-1)(1+x)) )", "(x^2-1)^(-1)");
		check("Expand(1/((x-1)*(1+x)))", "1/(-1+x^2)");
		check("Expand((x+y+z)^3)", "x^3+3*x^2*y+3*x*y^2+y^3+3*x^2*z+6*x*y*z+3*y^2*z+3*x*z^2+3*y*z^2+z^3");
		check("Expand((a+b)*(c+d))", "a*c+b*c+a*d+b*d");
		// check("Expand((x+3)/((x+4)*(x+2)))", "(x+3)*(x^2+6*x+8)^(-1)");
		check("Expand((x+3)/((x+4)*(x+2)))", "3/((2+x)*(4+x))+x/((2+x)*(4+x))");
	}

	public void testSystem166() {
		check("Expand((a+b)^2)", "a^2+2*a*b+b^2");
		check("Expand((a+b+c+d)^2)", "a^2+2*a*b+b^2+2*a*c+2*b*c+c^2+2*a*d+2*b*d+2*c*d+d^2");
	}

	public void testSystem167() {
		check("Expand(c^2+16*b^2)", "16*b^2+c^2");
		check("Expand((a+b+c)^2)", "a^2+2*a*b+b^2+2*a*c+2*b*c+c^2");
		check("Expand((a+4*b+c)^2)", "a^2+8*a*b+16*b^2+2*a*c+8*b*c+c^2");
		check("Expand((a+b+c)^10)",
				"a^10+10*a^9*b+45*a^8*b^2+120*a^7*b^3+210*a^6*b^4+252*a^5*b^5+210*a^4*b^6+120*a^3*b^\n"
						+ "7+45*a^2*b^8+10*a*b^9+b^10+10*a^9*c+90*a^8*b*c+360*a^7*b^2*c+840*a^6*b^3*c+1260*a^\n"
						+ "5*b^4*c+1260*a^4*b^5*c+840*a^3*b^6*c+360*a^2*b^7*c+90*a*b^8*c+10*b^9*c+45*a^8*c^\n"
						+ "2+360*a^7*b*c^2+1260*a^6*b^2*c^2+2520*a^5*b^3*c^2+3150*a^4*b^4*c^2+2520*a^3*b^5*c^\n"
						+ "2+1260*a^2*b^6*c^2+360*a*b^7*c^2+45*b^8*c^2+120*a^7*c^3+840*a^6*b*c^3+2520*a^5*b^\n"
						+ "2*c^3+4200*a^4*b^3*c^3+4200*a^3*b^4*c^3+2520*a^2*b^5*c^3+840*a*b^6*c^3+120*b^7*c^\n"
						+ "3+210*a^6*c^4+1260*a^5*b*c^4+3150*a^4*b^2*c^4+4200*a^3*b^3*c^4+3150*a^2*b^4*c^4+\n"
						+ "1260*a*b^5*c^4+210*b^6*c^4+252*a^5*c^5+1260*a^4*b*c^5+2520*a^3*b^2*c^5+2520*a^2*b^\n"
						+ "3*c^5+1260*a*b^4*c^5+252*b^5*c^5+210*a^4*c^6+840*a^3*b*c^6+1260*a^2*b^2*c^6+840*a*b^\n"
						+ "3*c^6+210*b^4*c^6+120*a^3*c^7+360*a^2*b*c^7+360*a*b^2*c^7+120*b^3*c^7+45*a^2*c^8+\n"
						+ "90*a*b*c^8+45*b^2*c^8+10*a*c^9+10*b*c^9+c^10");
		check("Expand(x*(x+1))", "x+x^2");
	}

	public void testSystem168() {
		check("Exponent(2,x)", "0");
		check("Coefficient(2,x,1)", "0");
		check("Exponent(Cos(a+b*x)^2+Cos(a+b*x)^ex,Cos(a+b*x))", "Max(2,ex)");
		check("Exponent(Cos(a+b*x)^2+Cos(a+b*x)^(-1/2),Cos(a+b*x))", "2");
	}

	public void testSystem170() {
		check("Cross({1, 2, 3}, {a, b, c})", "{-3*b+2*c,3*a-c,-2*a+b}");
	}

	public void testSystem171() {
		check("N(Integrate(Sin(x),x))", "-Cos(x)");
		check("N(Sin(x))", "Sin(x)");
		check("Cancel((1*x+1/2*2)^((-1)*2)*1^(-1)^(-1))", "1/(1+x)^2");
		check("Integrate(1/(a+b*x),x)", "Log(a+b*x)/b");
		check("Integrate((a+b*x)^(1/3),x)", "3/4*(a+b*x)^(4/3)/b");
	}

	public void testSystem171a() {
		check("Integrate(1/(x^5+x-7),x)", //
				"Integrate(1/(-7+x+x^5),x)");

		check("Rubi`PolyQ(x/(2*Sqrt(2)),x,1)", //
				"True");
		check("Rubi`PolyQ((2+2*x)/(2*Sqrt(2)),x)", //
				"True");
		// check("Rubi`PolyQ(2+2 *x,x,1)", //
		// "True");
		// check("Rubi`PolyQ(-(ArcTan((1+x)/Sqrt(2))/(2 Sqrt(2))),x )", //
		// "True");
		check("Rubi`substaux(-ArcTan(x/(2*Sqrt(2)))/(2*Sqrt(2)),x,2+2*x,True)", //
				"-ArcTan((1+x)/Sqrt(2))/(2*Sqrt(2))");
		check("Integrate((x^2+2*x+3)^(-1),x)", //
				"ArcTan((1+x)/Sqrt(2))/Sqrt(2)");

		check("Integrate((x-2)^(-3),x)", "-1/(2*(2-x)^2)");
		check("D(-1/(2*(2-x)^2),x)", "-1/(2-x)^3");
		check("Integrate(Log(x)*x^2,x)", //
				"-x^3/9+1/3*x^3*Log(x)");
		check("Integrate((x^2+1)*Log(x),x)", //
				"-x-x^3/9+1/3*(3*x+x^3)*Log(x)");
		check("Simplify(D(ArcTan((2*x-1)*3^(-1/2))*3^(-1/2)+1/6*Log(x^2-x+1)-1/3*Log(x+1),x))", "x/(1+x^3)");

		check("Integrate(x/(x^3+1),x)", //
				"-ArcTan((1-2*x)/Sqrt(3))/Sqrt(3)-Log(1+x)/3+Log(1-x+x^2)/6");
		// check("Simplify(D(ArcTan((2*x-1)*3^(-1/2))*3^(-1/2)+1/6*Log(x^2-x+1)-1/3*Log(x+1),x))",
		// "x*(x^3+1)^(-1)");
		check("Integrate(x*Log(x),x)", //
				"-x^2/4+1/2*x^2*Log(x)");
		check("D(-1/2*Log(x)*x^2+3/4*x^2+x*(x*Log(x)-x),x)", //
				"x*Log(x)");
		check("integrate(x*Exp(-x^2),x)", "-1/(2*E^x^2)");
		check("D(-Gamma(1,x^2)/2, x)", "x/E^x^2");
		check("Simplify(x*E^(-x^2))", "x/E^x^2");

		check("Integrate(x^a,x)", "x^(1+a)/(1+a)");
		check("Integrate(a^x,x)", "a^x/Log(a)");
		check("Integrate(x^(-1),x)", "Log(x)");
		check("Integrate(x^a,x)", "x^(1+a)/(1+a)");
		check("Integrate(x^10,x)", "x^11/11");
		check("Simplify(1/2*(2*x+2))", "1+x");
		check("Simplify(1/2*(2*x+2)*(1/2)^(1/2))", //
				"(1+x)/Sqrt(2)");
		check("Simplify(Integrate((8*x+1)/(x^2+x+1)^2,x))", //
				"-(5+2*x)/(1+x+x^2)+(-4*ArcTan((1+2*x)/Sqrt(3)))/Sqrt(3)");

		check("Apart(1/(x^3+1))", //
				"1/(3*(1+x))+(2-x)/(3*(1-x+x^2))");
		check("Integrate(1/(x^5+x-7),x)", //
				"Integrate(1/(-7+x+x^5),x)");
		check("Integrate(1/(x-2),x)", //
				"Log(2-x)");
		check("Integrate((x-2)^(-2),x)", //
				"1/(2-x)");

		check("Integrate(1/(x^2+1),x)", //
				"ArcTan(x)");
		check("Integrate((2*x+5)/(x^2-2*x+5),x)", //
				"7/2*ArcTan(1/2*(-1+x))+Log(5-2*x+x^2)");
		check("Integrate((8*x+1)/(x^2+2*x+1),x)", //
				"7/(1+x)+8*Log(1+x)");
	}

	public void testSystem171b() {
		// check("D(2*E^x-Gamma(3,-x),x)", //
		// "2*E^x-E^x*x^2");

		// check("Simplify(Integrate(1/3*(2-x)*(x^2-x+1)^(-1),x))",
		// "ArcTan((2*x-1)*3^(-1/2))*3^(-1/2)-1/6*Log(x^2-x+1)");
		check("Integrate(1/3*(2-x)*(x^2-x+1)^(-1)+1/3*(x+1)^(-1),x)", //
				"-ArcTan((1-2*x)/Sqrt(3))/Sqrt(3)+Log(1+x)/3-Log(1-x+x^2)/6");
		check("Integrate(E^x*(2-x^2),x)", //
				"2*E^x*x-E^x*x^2");
		check("D(2*E^x-Gamma(3,-x),x)", //
				"2*E^x-E^x*x^2");
		check("Integrate((x^2+1)*Log(x),x)", //
				"-x-x^3/9+1/3*(3*x+x^3)*Log(x)");
		check("D(-x-Gamma(2,-3*Log(x))/9+x*Log(x),x)", //
				"Log(x)+x^2*Log(x)");

		check("Apart(2*x^2/(x^3+1))", //
				"2/3*1/(1+x)+2/3*(-1+2*x)/(1-x+x^2)");

		check("Integrate(2*x^2/(x^3+1),x)", //
				"2/3*Log(1+x^3)");
		check("Integrate(Sin(x)^3,x)", //
				"-Cos(x)+Cos(x)^3/3");

		check("Integrate(Cos(2*x)^3,x)", //
				"Sin(2*x)/2-Sin(2*x)^3/6");
		check("Integrate(x,x)", //
				"x^2/2");
		check("Integrate(2*x,x)", //
				"x^2");
		check("Integrate(h(x),x)", //
				"Integrate(h(x),x)");
		check("Integrate(f(x)+g(x)+h(x),x)", //
				"Integrate(f(x),x)+Integrate(g(x),x)+Integrate(h(x),x)");
		check("Integrate(Sin(x),x)", //
				"-Cos(x)");
		check("Integrate(Sin(10*x),x)", //
				"-Cos(10*x)/10");
		check("Integrate(Sin(Pi+10*x),x)", //
				"Cos(10*x)/10");
		check("Integrate(E^(a*x),x)", //
				"E^(a*x)/a");
		check("Integrate(x*E^(a*x),x)", //
				"-E^(a*x)/a^2+(E^(a*x)*x)/a");
		check("Integrate(x*E^x,x)", //
				"-E^x+E^x*x");
		check("Integrate(x^2*E^x,x)", //
				"2*E^x-2*E^x*x+E^x*x^2");
		check("Integrate(x^2*E^(a*x),x)", //
				"(2*E^(a*x))/a^3+(-2*E^(a*x)*x)/a^2+(E^(a*x)*x^2)/a");
		check("Integrate(x^3*E^(a*x),x)", //
				"(-6*E^(a*x))/a^4+(6*E^(a*x)*x)/a^3+(-3*E^(a*x)*x^2)/a^2+(E^(a*x)*x^3)/a");
		check("(-1.0)/48", //
				"-0.0208333");

		// to low max points
		checkNumeric("NIntegrate(1/(x^2), {x, 1, 1000}, Method->LegendreGauss, MaxPoints->7)", //
				"0.1045310822478283");
		checkNumeric("NIntegrate(1/(x^2), {x, 1, 1000}, Method->LegendreGauss, MaxPoints->100)", //
				"0.9988852159737868");

		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1},Method->Trapezoid)", //
				"-0.0208333271245165");
		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1},Method->Simpson, MaxIterations->10)", //
				"NIntegrate((-1+x)*(-0.5+x)*x*(0.5+x)*(x+1),{x,0,1},Method->simpson,MaxIterations->\n" + "10)");
		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1},Method->Simpson)", //
				"-0.0208333320915699");

		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1},Method->Romberg, MaxIterations->10)", //
				"-0.0208333333333333");
		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1},Method->Romberg)", //
				"-0.0208333333333333");
		checkNumeric("NIntegrate((x-1)*(x-0.5)*x*(x+0.5)*(x+1),{x,0,1})", //
				"-0.0208333333333333");

	}

	// public void testSystem171c() {
	// check("Integrate(ArcCoth(x^16)^2,x)", //
	// "1");
	// }

	public void testSystem172() {
		check("Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))", //
				"Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))");
		check("Integrate(Cos(a*x)*Sin(b*x),x)", //
				"Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))");
		check("Integrate(Cos(a*x)*Sin(b*x),x)", //
				"Cos((a-b)*x)/(2*(a-b))-Cos((a+b)*x)/(2*(a+b))");

		check("Csc(b*x)^0", //
				"1");
		check("Integrate(Cos(x*(a+b)),x)", //
				"Sin((a+b)*x)/(a+b)");
		// Integrate[Cos[a*x]*Sin[b*x]^2,x]
		check("Integrate(Cos(a*x)*Sin(b*x)^2,x)", //
				"Sin(a*x)/(2*a)-Sin((a-2*b)*x)/(4*(a-2*b))-Sin((a+2*b)*x)/(4*(a+2*b))");
		check("Integrate(Cos(a*x)^2*Sin(b*x),x)", //
				"Cos((2*a-b)*x)/(4*(2*a-b))-Cos(b*x)/(2*b)-Cos((2*a+b)*x)/(4*(2*a+b))");
		check("Integrate(Cos(b*x)^2*Sin(a*x)^2,x)", //
				"x/4-Sin(2*a*x)/(8*a)-Sin(2*(a-b)*x)/(16*(a-b))+Sin(2*b*x)/(8*b)-Sin(2*(a+b)*x)/(\n" + "16*(a+b))");
	}

	public void testSystem173() {
		check("N(1.0)", "1.0");
	}

	public void testSystem174() {
		check("N(42)", "42.0");
	}

	public void testSystem175() {
		check("N(I)", "I*1.0");
	}

	public void testSystem176() {
		check("True && a", "a");
		check("b && False && a", "False");
		check("a && !a", "False");
	}

	public void testSystem177() {
		check("False || a", "a");
		check("b || True || a", "True");
		check("a || !a", "True");
	}

	public void testSystem178() {
		check("!False", "True");
	}

	public void testSystem179() {
		check("!True", "False");

	}

	public void testSystem180() {
		check("Pi==E", "False");
	}

	public void testSystem181() {
		check("Pi!=E", "True");
	}

	public void testSystem182() {
		check("I==I", "True");
	}

	public void testSystem183() {
		check("(#^3)&", "#1^3&");
	}

	public void testSystem184() {
		check("(#^3)&()", "#1^3");
	}

	public void testSystem185() {
		check("Function(x,(x^3))[]", "Expected number of arguments: 1 but got 0 arguments:\n" + "Function(x,x^3)[]");
	}

	public void testSystem186() {
		check("Function(x,(x^3))[x,y]", "x^3");
		check("Hold(f(#1, y)& [x])", "Hold(f(#1,y)&[x])");
	}

	public void testSystem187() {
		check("(#^3)&(x)", "x^3");
	}

	public void testSystem188() {
		check("$i = 10; $i=$i-1", "9");
	}

	public void testSystem189() {
		check("$i = 10; If($i>0, 1, -1)", "1");
	}

	public void testSystem190() {
		check("$i = 10; $result = 1; While($i >= 0, $result = $result + $i; $i=$i-1); $result", "56");
	}

	public void testSystem191() {
		check("$i = 10; $result = 1; While($i >= 0, $result = $result + $i; If($result>20, Break()); $i=$i-1); $result",
				"28");
	}

	public void testSystem192() {
		check("Block({ShowSteps=False,StepCounter=Null}, test)", "test");
		check("$blck=Block({$i=0}, $i=$i+1; Return($i))", "1");
		check("$blck=Module({$i=0}, $i=$i+1; Return($i))", "1");
		check("$y=$x^3;Module({$x=42},$x+$y)", "42+$x^3");
	}

	public void testSystem193() {
		check("Sum(x,{x,10})", "55");
		check("Sum(x,{a,10,z})", "x*(-9+z)");
		check("Sum(x,{x,1,1})", "1");
		check("Sum(x,{x,3,2,-1})", "5");
		check("Sum(x,{x,10,3,-4})", "16");
		// use default value "0" for iterator with invalid range
		check("Sum(x,{x,1,0})", "0");
		check("Sum(x,{x,2,3,-1})", "0");
		// 1*1 + 2*1 + 3*1 + 2*2 + 2*3 + 3*3
		check("Sum(x*y, {x, 1, 3}, {y, 1, x})", "25");
		check("Sum(k+a,{k,1,n})", "a*n+1/2*n*(1+n)");
	}

	public void testSystem194() {
		check("(n!)^x", "(n!)^x");
		check("(n++)^(n!)", "(n++)^n!");
		check("(n++)^(n!)^a", "(n++)^(n!)^a");
		check("Product(x,{x,10})", "3628800");
		check("Product(x,{x,0,1})", "0");
		check("Product(x,{x,0,10,2})", "0");
		check("Product(x,{x,1,1})", "1");
		check("Product(x,{x,1,5})", "120");
		check("Product(x,{x,3,2,-1})", "6");
		check("Product(x,{x,10,3,-4})", "60");
		// use default value "1" for iterator with invalid range
		check("Product(x,{x,1,0})", "1");
		check("Product(x,{x,2,3,-1})", "1");
		check("Product(x,{x,0,-1,2})", "1");
		// check("Product(x,{a,10,z})", "x^(-10)*x^(z+1)");
		check("Product(i^(x),{i,1,n})", "(n!)^x");
	}

	public void testSystem195() {
		check("ComposeList({x, y, z}, a)", "{a,x(a),y(x(a)),z(y(x(a)))}");
	}

	public void testSystem196() {
		check("Fold(fl, 0, {1, 2, 3})", "fl(fl(fl(0,1),2),3)");
	}

	public void testSystem197() {
		check("FoldList(fl, 0, {1, 2, 3})", "{0,fl(0,1),fl(fl(0,1),2),fl(fl(fl(0,1),2),3)}");
	}

	public void testSystem198() {
		check("Nest(n0,10,0)", "10");
	}

	public void testSystem199() {
		check("Nest(n0,10,4)", "n0(n0(n0(n0(10))))");
	}

	public void testSystem200() {
		check("NestList(n0,10,0)", "{10}");
	}

	public void testSystem201() {
		check("NestList(n0,10,4)", "{10,n0(10),n0(n0(10)),n0(n0(n0(10))),n0(n0(n0(n0(10))))}");
	}

	public void testSystem202() {
		check("Outer(List, {a, b, c, d}, {{{1, 2}}})",
				"{{{{{a,1},{a,2}}}},{{{{b,1},{b,2}}}},{{{{c,1},{c,2}}}},{{{{d,1},{d,2}}}}}");
	}

	public void testSystem203() {
		check("Outer(List, {1, 2, 3}, {4, 5})", "{{{1,4},{1,5}},{{2,4},{2,5}},{{3,4},{3,5}}}");
	}

	public void testSystem204() {
		check("Outer(Times, {a, b, c}, {{{1, 2}, {3, 4}}})",
				"{{{{a,2*a},{3*a,4*a}}},{{{b,2*b},{3*b,4*b}}},{{{c,2*c},{3*c,4*c}}}}");
	}

	public void testSystem205() {
		check("Array(hd,3)", "{hd(1),hd(2),hd(3)}");
	}

	public void testSystem206() {
		check("Array(hd,4,2,g)", "g(hd(2),hd(3),hd(4),hd(5))");
	}

	public void testSystem207() {
		check("Array(hd,{3,4})", "{{hd(1,1),hd(1,2),hd(1,3),hd(1,4)},{hd(2,1),hd(2,2),hd(2,3),hd(2,4)},{hd(3,1),hd(\n"
				+ "3,2),hd(3,3),hd(3,4)}}");
	}

	public void testSystem208() {
		check("Table(i0,{i0,3})", "{1,2,3}");
		check("f @@ Table(i0+1,List(i0,1,5))", "f(2,3,4,5,6)");
		check("Table(i0,{i0,1,1})", "{1}");
		check("Table(i0,{i0,1,0})", "{}");
	}

	public void testSystem209() {
		check("Table(i0*j,{i0,3},{j,2})", "{{1,2},{2,4},{3,6}}");
	}

	public void testSystem210() {
		check("Table(i0*j,{i0,3,10,2},{j,2})", "{{3,6},{5,10},{7,14},{9,18}}");
	}

	public void testSystem211() {
		check("Range(5)", "{1,2,3,4,5}");
	}

	public void testSystem211a() {
		check("$g(x_Integer):=x+1; Range/@ $g /@Range(3)", "{{1,2},{1,2,3},{1,2,3,4}}");
	}

	public void testSystem212() {
		check("Range(3,10,2)", "{3,5,7,9}");
	}

	public void testSystem213() {
		check("Range(3,10,1/2)", "{3,7/2,4,9/2,5,11/2,6,13/2,7,15/2,8,17/2,9,19/2,10}");
	}

	public void testSystem214() {
		check("Range(1,2,0.25)", "{1.0,1.25,1.5,1.75,2.0}");

	}

	public void testSystem215() {
		check("Extract({u+v+w^5, 42, w^10, 12, u+w^3, w^2}, {3, 2})", "10");
	}

	public void testSystem216() {
		check("Extract({ArcCsc,ArcSec,ArcCot,ArcTan}, Position({ArcSin,ArcCos,ArcTan,ArcCot},ArcCos))[[1]]", "ArcSec");

		check("{ArcCsc,ArcSec,ArcCot,ArcTan}[[1]][x]", "ArcCsc(x)");
		check("Extract({u+v+w^5, 42, w^10, 12, x(u,w^3), w^2}, {5, 2, 2})", "3");
		check("Position({ArcSin,ArcCos,ArcTan,ArcCot},ArcCos)", "{{2}}");
		check("Extract({ArcCsc,ArcSec,ArcCot,ArcTan}, {{2}})", "{ArcSec}");
		check("Extract({ArcCsc,ArcSec,ArcCot,ArcTan}, Position({ArcSin,ArcCos,ArcTan,ArcCot},ArcCos))[[1]]", "ArcSec");
		check("Extract({ArcCsc,ArcSec,ArcCot,ArcTan}, Position({ArcSin,ArcCos,ArcTan,ArcCot},ArcCos))[[1]][x]",
				"ArcSec(x)");
		check("{f(a)}[[1]][x]", "f(a)[x]");
	}

	public void testSystem217() {
		check("Position({42}, 42)", "{{1}}");
	}

	public void testSystem218() {
		check("Position({u+v+w^5, 42, w^10}, w^_)", "{{1,3},{3}}");
	}

	public void testSystem219() {
		check("Position({u+v+w^5, 42, w^10}, w^_, {1})", "{{3}}");
	}

	public void testSystem220() {
		check("Position({u+v+w^5, 42, w^10, 12, u+w^3, w^2}, w^_, {1,2})", "{{1,3},{3},{5,2},{6}}");
	}

	public void testSystem221() {
		check("Take({1,2,3,4,5,6,7,8,9,10},3)", "{1,2,3}");
	}

	public void testSystem222() {
		check("Take({1,2,3,4,5,6,7,8,9,10},-3)", "{8,9,10}");
	}

	public void testSystem223() {
		check("Take({1,2,3,4,5,6,7,8,9,10},{2,7,3})", "{2,5}");
	}

	public void testSystem224() {
		check("Take({1,2,3,4,5,6,7,8,9,10},{2})", "{2}");
	}

	public void testSystem225() {
		check("Take({1,2,3,4,5,6,7,8,9,10},{2,2})", "{2}");
	}

	public void testSystem226() {
		check("Take({1,2,3,4,5,6,7,8,9,10},{2,7})", "{2,3,4,5,6,7}");
	}

	public void testSystem227() {
		check("Take({{1,2,3,4},{5,6,7,8,9,10}},-1,3)", "{{5,6,7}}");
	}

	public void testSystem228() {
		check("Take({{1,2,3,4},{{5,6,7},{8,9,10}}},-1,1,2)", "{{{5,6}}}");
	}

	public void testSystem229() {
		check("Take({{1,2,3,4},{w(5,6,7),{8,9,10}}},-1,1,2)", "{{w(5,6)}}");

	}

	public void testSystem230() {
		check("Drop({1,2,3,4,5,6,7,8,9,10},3)", "{4,5,6,7,8,9,10}");
	}

	public void testSystem231() {
		check("Drop({1,2,3,4,5,6,7,8,9,10},-3)", "{1,2,3,4,5,6,7}");
	}

	public void testSystem232() {
		check("Drop({1,2,3,4,5,6,7,8,9,10},{2,7,3})", "{1,3,4,6,7,8,9,10}");
	}

	public void testSystem233() {
		check("Drop({1,2,3,4,5,6,7,8,9,10},{2})", "{1,3,4,5,6,7,8,9,10}");
	}

	public void testSystem234() {
		check("Drop({1,2,3,4,5,6,7,8,9,10},{2,2})", "{1,3,4,5,6,7,8,9,10}");
	}

	public void testSystem235() {
		check("Drop({1,2,3,4,5,6,7,8,9,10},{2,7})", "{1,8,9,10}");

	}

	public void testSystem236() {
		check("Sign(0.0)", "0");
	}

	public void testSystem237() {
		check("Sign(-(6/12))", "-1");
	}

	public void testSystem238() {
		check("Sign(42)", "1");
	}

	public void testSystem239() {
		check("SignCmp(0.0)", "0.0");
	}

	public void testSystem240() {
		check("SignCmp(-(6/12))", "-1");
	}

	public void testSystem241() {
		check("SignCmp(42)", "1");
	}

	public void testSystem242() {
		check("SignCmp(I)", "1");
	}

	public void testSystem243() {
		check("SignCmp(-I)", "-1");
	}

	public void testSystem244() {
		check("SignCmp(3-I)", "1");
	}

	public void testSystem245() {
		check("SignCmp(-3+I)", "-1");

	}

	public void testSystem246() {
		check("Ceiling(42+x+y)", "42+Ceiling(x+y)");
		check("Ceiling(42)", "42");
		check("Ceiling(Pi)", "4");

		check("Floor(42)", "42");
		check("Floor(Pi)", "3");
		check("Floor(42+x+y)", "42+Floor(x+y)");
	}

	public void testSystem247() {
		check("Round(1.1)", "1");
		check("Round(1.5)", "2");
		check("Round(2.5)", "2");
		check("Round(2.6)", "3");
		check("Round(3.5)", "4");
		check("Round(7/3.0)", "2");
		check("Round(-7/3.0)", "-2");
		check("Round(-42)", "-42");
		check("Round(42)", "42");
		check("Round(3/2)", "2");
		check("Round(5/2)", "2");
		check("Round(7/2)", "4");
		check("Round(9/2)", "4");
		check("Round(6/2)", "3");
		check("Round(-3/2)", "-2");
		check("Round(-5/2)", "-2");
		check("Round(-7/2)", "-4");
		check("Round(-9/2)", "-4");
		check("Round(-6/2)", "-3");
		check("Round(7/3)", "2");
		check("Round(-7/3)", "-2");
		check("Round(Pi)", "3");
		check("Round(42+x+y)", "42+Round(x+y)");
	}

	public void testSystem248() {
		check("IntegerPart(42)", "42");
	}

	public void testSystem249() {
		check("Ceiling(3/4)", "1");
	}

	public void testSystem250() {
		check("Floor(3/4)", "0");
	}

	public void testSystem251() {
		check("IntegerPart(3/4)", "0");
	}

	public void testSystem252() {
		check("Ceiling(-3/4)", "0");
	}

	public void testSystem253() {
		check("Floor(-3/4)", "-1");
	}

	public void testSystem254() {
		check("IntegerPart(-3/4)", "0");
	}

	public void testSystem255() {
		check("Ceiling(42.0)", "42");
	}

	public void testSystem256() {
		check("Floor(42.0)", "42");
	}

	public void testSystem257() {
		check("IntegerPart(42.0)", "42");
	}

	public void testSystem258() {
		check("Ceiling(0.75)", "1");
	}

	public void testSystem259() {
		check("Floor(0.75)", "0");
	}

	public void testSystem260() {
		check("IntegerPart(0.75)", "0");
	}

	public void testSystem261() {
		check("Ceiling(-0.75)", "0");
	}

	public void testSystem262() {
		check("Floor(-0.75)", "-1");
	}

	public void testSystem263() {
		check("IntegerPart(-0.75)", "0");
	}

	public void testSystem264() {
		check("IntegerPart(Pi)", "3");
	}

	public void testSystem265() {
		check("IntegerPart(-42*x)", "-IntegerPart(42*x)");
	}

	public void testSystem266() {
		check("GCD(6,35)", "1");
		check("GCD(6,27)", "3");
		check("GCD(12,54,66)", "6");

		check("LCM(14,6,9)", "126");
		check("LCM(12,54,66)", "1188");

		check("PowerMod(3,4,7)", "4");
		check("PowerMod(5,-1,3)", "2");
		check("PowerMod(7,-1,5)", "3");

		check("ExtendedGCD(3,4)", "{1,{-1,1}}");

		check("ExtendedGCD(12,60)", "{12,{1,0}}");
		check("ExtendedGCD(12,256)", "{4,{-21,1}}");
		check("ExtendedGCD(12,60,256)", "{4,{-21,0,1}}");
		check("ExtendedGCD(12,60,256,282)", "{2,{1470,0,-70,1}}");

	}

	public void testSystem267() {
		check("Negative(0.0)", "False");
	}

	public void testSystem268() {
		check("Negative(-(6/12))", "True");
	}

	public void testSystem269() {
		check("Negative(42)", "False");
	}

	public void testSystem270() {
		check("Positive(1/3)", "True");
	}

	public void testSystem271() {
		check("Positive(42)", "True");
	}

	public void testSystem272() {
		check("Negative(I)", "False");
	}

	public void testSystem273() {
		check("Negative(-I)", "False");
		check("Negative(0)", "False");
	}

	public void testSystem274() {
		check("NonNegative(3-I)", "False");
		check("NonNegative(0)", "True");
	}

	public void testSystem275() {
		check("Positive(-3+I)", "False");

	}

	public void testSystem276() {
		check("{10,9,8,7}[[4]]", "7");
	}

	public void testSystem278() {
		check("{10,9,8,7}[[1]]", "10");
	}

	public void testSystem279() {
		check("{10,9,8,7}[[0]]", "List");
	}

	public void testSystem280() {
		check("2<9/4", "True");
	}

	public void testSystem281() {
		check("5/4<9/4", "True");
	}

	public void testSystem282() {
		check("Order(x,y)", "1");
	}

	public void testSystem283() {
		check("Order(y,x)", "-1");
	}

	public void testSystem284() {
		check("OrderedQ({x,y,z})", "True");
		check("OrderedQ({abc,abc})", "True");
	}

	public void testSystem285() {
		check("OrderedQ({2,5/2,3,10/3,a,-b})", "True");
	}

	public void testSystem286() {
		check("OrderedQ({-2*a,a^2,b^3,c})", "True");
	}

	public void testSystem287() {
		check("OrderedQ({x,a,z})", "False");
	}

	public void testSystem288() {
		check("Sort({a^2,b^3,c,-2*a})", "{-2*a,a^2,b^3,c}");
		check("Sort({3,4,2,5,6,42,21,33,15}, Less)", "{2,3,4,5,6,15,21,33,42}");
		check("Sort(gogo(3,4,2,5,6,42,21,33,15), Greater)", "gogo(42,33,21,15,6,5,4,3,2)");
	}

	public void testSystem289() {
		check("Head(hello)", "Symbol");
		check("Head(fun(a)[b][c])", "fun(a)[b]");
		check("FixedPoint(Head, fun(a)[b][c])", "Symbol");
	}

	public void testSystem290() {
		check("Head(\"hello world\")", "String");
	}

	public void testSystem291() {
		check("Head(2/3)", "Rational");
	}

	public void testSystem292() {
		check("Head(17+2)", "Integer");
	}

	public void testSystem293() {
		check("Head(I+1)", "Complex");
	}

	public void testSystem294() {
		check("Head(I*0.5)", "Complex");
	}

	public void testSystem295() {
		check("Head(3.12)", "Real");
	}

	public void testSystem296() {
		check("Head(q+p)", "Plus");
	}

	public void testSystem297() {
		check("Head(g(x)[y])", "g(x)");

	}

	public void testSystem298() {
		check("Length(g(x,y,z))", "3");
	}

	public void testSystem299() {
		check("Length(g(x,y,z)[u,v])", "2");

	}

	public void testSystem300() {
		check("RotateLeft(r(1,2,3,4))", "r(2,3,4,1)");
	}

	public void testSystem301() {
		check("RotateRight(r(1,2,3,4))", "r(4,1,2,3)");
	}

	public void testSystem302() {
		check("RotateLeft(r(1,2,3,4),2)", "r(3,4,1,2)");
	}

	public void testSystem303() {
		check("RotateRight(r(1,2,3,4),3)", "r(2,3,4,1)");
	}

	public void testSystem304() {
		check("Reverse(r(1,2,3,4))", "r(4,3,2,1)");

	}

	public void testSystem305() {
		// ReplaceAll
		check("u(v(w,x,y) /. x->y)", "u(v(w,y,y))");
		check("u(v(w,x,y) /. { x->y, w->y})", "u(v(y,y,y))");
		check("{x,x,x} /. x->y+1", "{1+y,1+y,1+y}");
		check("{a+b,x,c+d+e} /. x_+y_->{x,y}", "{{a,b},x,{c,d+e}}");

		check("u2(v(w,x,y)) /. { {x->y}, {w->y, v->k}}", "{u2(v(w,y,y)),u2(k(y,x,y))}");
	}

	public void testSystem306() {
		// ReplaceRepeated
		check("{a+b,x,c+d+e} //. x_+y_->{x,y}", "{{a,b},x,{c,{d,e}}}");
		check("{a+b,x,c+d+e} //. {{x_+y_->{x,y}}, {x_+y_->rr(x,y)}}",
				"{{{a,b},x,{c,{d,e}}},{rr(a,b),x,rr(c,rr(d,e))}}");
	}

	public void testSystem307() {
		check("Apply(u, v(w,x,y))", "u(w,x,y)");
		check("u@@v(w,x,y)", "u(w,x,y)");
	}

	public void testSystem308() {
		check("Apply(u, v(w,x,y(z)),{1})", "v(w,x,u(z))");
	}

	public void testSystem309() {
		check("Apply(g, {{{u(w)}}}, -2)", "{g(g(g(w)))}");
	}

	public void testSystem310() {
		check("Apply(g, {{{u(w)}}}, {2,-2})", "{{g(g(w))}}");
	}

	public void testSystem311() {
		check("Apply(g, {{{u(w)}}}, {-4, -2})", "{g(g(g(w)))}");
	}

	public void testSystem312() {
		check("Level(w(w(g(a), a), h(a), u(b), w),2)", "{g(a),a,w(g(a),a),a,h(a),b,u(b),w}");
		check("Level(w(w(g(a), a), h(a), u(b), w),{2})", "{g(a),a,a,b}");
		check("Level(w(w(g(a), a), h(a), u(b), w),{-1})", "{a,a,a,b,w}");
	}

	public void testSystem313() {
		check("Total({{2,4*x^5, y*x}},{2})", "{2+4*x^5+x*y}");
		check("Total(w({1,2,3},{4,5,6},{7,8,9}))", "{12,15,18}");
		check("Total(w({1,2,3},{4,5,6},{7,8,9}),2)", "45");
	}

	public void testSystem315() {
		check("Arg(-3)", "Pi");
	}

	public void testSystem316() {

		checkNumeric("Arg(1.0+I)", "0.7853981633974483");
		checkNumeric("Arg(1.0-I)", "-0.7853981633974483");

		checkNumeric("Arg(-1.0-I)", "-2.356194490192345");
		checkNumeric("Arg(-1.0+I)", "2.356194490192345");

		checkNumeric("Arg(3.0+5*I)", "1.0303768265243125");
		checkNumeric("Arg(3.0-5*I)", "-1.0303768265243125");

		checkNumeric("Arg(-3.0-5*I)", "-2.1112158270654806");
		checkNumeric("Arg(-3.0+5*I)", "2.1112158270654806");

		check("Arg(1+I)", "Pi/4");
		check("Arg(1-I)", "-Pi/4");

		check("Arg(-1-I)", "-3/4*Pi");
		check("Arg(-1+I)", "3/4*Pi");

		check("Arg(3+5*I)", "ArcTan(5/3)");
		check("Arg(3-5*I)", "-ArcTan(5/3)");

		check("Arg(-3-5*I)", "-Pi+ArcTan(5/3)");
		check("Arg(-3+5*I)", "Pi-ArcTan(5/3)");

	}

	public void testSystem317() {
		check("Arg(-3*I)", "-Pi/2");
		check("Arg(3*I)", "Pi/2");

		check("Arg(-5*I)", "-Pi/2");
		check("Arg(5*I)", "Pi/2");
	}

	public void testSystem318() {
		check("Arg(42)", "0");
	}

	public void testSystem319() {
		check("Arg(-2.1)", "Pi");
		// } public void testSystem300() { check("Arg(-2.1*I)", "");
		// } public void testSystem300() { check("Arg(2.1 I)",
		// "1.5707963267948966");
	}

	public void testSystem320() {
		check("Arg(42.2)", "0");

	}

	public void testSystem321() {
		check("ArcCos(Infinity)", "I*Infinity");
	}

	public void testSystem322() {
		check("ArcSin(-3*f)", "-ArcSin(3*f)");
	}

	public void testSystem323() {
		check("ArcTan(-3*f)", "-ArcTan(3*f)");
	}

	public void testSystem324() {
		check("Sin(-3*Pi)", "0");
	}

	public void testSystem325() {
		check("Cos(-3*Pi)", "-1");
	}

	public void testSystem326() {
		check("Tan(-3*Pi)", "0");

	}

	public void testSystem327() {
		check("Union({},{})", "{}");
	}

	public void testSystem328() {
		check("Union({1},{2})", "{1,2}");
	}

	public void testSystem329() {
		check("Union({1,2,2,4},{2,3,4,5})", "{1,2,3,4,5}");
	}

	public void testSystem330() {
		check("Intersection({},{})", "{}");
	}

	public void testSystem331() {
		check("Intersection({1},{2})", "{}");
	}

	public void testSystem332() {
		check("Intersection({1,2,2,4},{2,3,4,5})", "{2,4}");
		check("Intersection({2,3,4,5},{1,2,2,4})", "{2,4}");
	}

	public void testSystem333() {
		check("Complement({},{})", "{}");
	}

	public void testSystem334() {
		check("Complement({1,2,3},{2,3,4})", "{1}");
		check("Complement({2,3,4},{1,2,3})", "{4}");
		check("Complement({1},{2})", "{1}");
		check("Complement({1,2,2,4,6},{2,3,4,5})", "{1,6}");
	}

	public void testSystem335() {
		check("CartesianProduct({},{})", "{}");
		check("CartesianProduct({a},{})", "{}");
		check("CartesianProduct({},{b})", "{}");
		check("CartesianProduct({a},{b})", "{{a,b}}");
		check("CartesianProduct({a},{b},{c})", "{{a,b,c}}");
		check("CartesianProduct({a,b},{c},{d,e,f})", "{{a,c,d},{a,c,e},{a,c,f},{b,c,d},{b,c,e},{b,c,f}}");
		check("CartesianProduct({a,b},{c,d},{e,f},{g,h})",
				"{{a,c,e,g},{a,c,e,h},{a,c,f,g},{a,c,f,h},{a,d,e,g},{a,d,e,h},{a,d,f,g},{a,d,f,h},{b,c,e,g},{b,c,e,h},{b,c,f,g},{b,c,f,h},{b,d,e,g},{b,d,e,h},{b,d,f,g},{b,d,f,h}}");
		check("CartesianProduct({1,2,2,4,6},{2,3,4,5})",
				"{{1,2},{1,3},{1,4},{1,5},{2,2},{2,3},{2,4},{2,5},{2,2},{2,3},{2,4},{2,5},{4,2},{\n"
						+ "4,3},{4,4},{4,5},{6,2},{6,3},{6,4},{6,5}}");
	}

	public void testSystem336() {
		check("Join({},{})", "{}");
	}

	public void testSystem337() {
		check("Join({1},{2})", "{1,2}");
		// not evaluated:
	}

	public void testSystem338() {
		check("Join({1},{2},3)", "Join({1},{2},3)");
	}

	public void testSystem339() {
		check("Join({1,2,2,4},{2,3,4,5})", "{1,2,2,4,2,3,4,5}");

	}

	public void testSystem340() {
		check("Select({1, I, f, g(2), 3/4}, NumberQ)", "{1,I,3/4}");
	}

	public void testSystem341() {
		check("Select(a*c+b*c+a*d+b*d, FreeQ(#,a)&)", "b*c+b*d");
	}

	public void testSystem342() {
		check("Select(a*c+b*c+a*d+b*d, FreeQ(#,a)&, 1)", "b*c");
	}

	public void testSystem343() {
		check("Cases({a(b), a(b,c), a(b(c), d), a(b(c), d(e)), a(b(c), d, e)}, a(b(_),_))", "{a(b(c),d),a(b(c),d(e))}");
	}

	public void testSystem344() {
		check("Cases({a, b, 3/4, I, 4, 1/2}, a_Rational)", "{3/4,1/2}");
	}

	public void testSystem345() {
		check("Exp(x)", "E^x");
	}

	public void testSystem346() {
		checkNumeric("Exp(1.0)", "2.718281828459045");
		check("Exp(Log(a+b))", "a+b");
	}

	public void testSystem347() {
		check("Log(1.0)", "0.0");
		check("Log(1)", "0");
		check("Log(Exp(0.5))", "0.5");
		check("Log(Exp(1/2))", "1/2");
		check("Log(Exp(-42))", "-42");
		check("Log(Exp(I))", "I");
		check("Log(Exp(-I))", "-I");
		check("Log(Exp(1+2*I))", "1+I*2");
		check("Log(Exp(a+b))", "Log(E^(a+b))");
	}

	public void testSystem348() {
		check("PolynomialQ(2, x)", "True");
		check("PolynomialQ(13*x^4*y^7+a^7*x, {x,y})", "True");
	}

	public void testSystem349() {
		check("PolynomialQ(x + f(x), x)", "False");
		check("PolynomialQ(x + Sin(x^2), x)", "False");
	}

	public void testSystem350() {
		check("PolynomialQ((2 + a)^2*(a - b - c^2)^2, a)", "True");
		check("PolynomialQ((2 + a)^2*(a - b - c^2)^2, {a,b,c})", "True");
	}

	public void testSystem352() {
		check("Variables((2 + a)^2 * (a - b - c^2)^2)", "{a,b,c}");
	}

	public void testSystem353() {
		check("DigitQ(\"0123456789\")", "True");
	}

	public void testSystem354() {
		check("LetterQ(\"abcJHFHG\")", "True");
	}

	public void testSystem355() {
		check("LowerCaseQ(\"abc\")", "True");
	}

	public void testSystem356() {
		check("UpperCaseQ(\"JHFHG\")", "True");
	}

	public void testSystem357() {
		check("ToCharacterCode(\"123abcABC\")", "{49,50,51,97,98,99,65,66,67}");
	}

	public void testSystem358() {
		check("FromCharacterCode(55)", "7");
	}

	public void testSystem359() {
		check("FromCharacterCode({49,50,51,97,98,99,65,66,67})", //
				"123abcABC");
	}

	public void testSystem360() {
		check("ToUnicode(\"123abcABC\")", //
				"\\u0031\\u0032\\u0033\\u0061\\u0062\\u0063\\u0041\\u0042\\u0043");
	}

	public void testSystem361() {
		check("SyntaxQ(\"a+b)*3\")", "False");
	}

	public void testSystem362() {
		check("SyntaxLength(\"a+b)*3\")", "3");
		check("SyntaxLength(\"(a+b)*3\")", "7");
	}

	public void testSystem363() {
		check("$decr=10;$decr--", "10");
	}

	public void testSystem364() {
		check("$decr=10;$decr--;$decr", "9");
	}

	public void testSystem365() {
		check("$predecr=10;--$predecr", "9");
	}

	public void testSystem366() {
		check("$predecr=10;--$predecr;$predecr", "9");
	}

	public void testSystem367() {
		check("$incr=10;$incr++", "10");
	}

	public void testSystem368() {
		check("$incr=10;$incr++;$incr", "11");
	}

	public void testSystem369() {
		check("$preincr=10;++$preincr", "11");
	}

	public void testSystem370() {
		check("$preincr=10;++$preincr;$preincr", "11");
	}

	public void testSystem371() {
		check("Mean({a,b,2,3})", "1/4*(5+a+b)");
		check("Mean({1., 0.3, 4.7})", "2.0");
	}

	public void testSystem372() {
		check("Median({1,5,2,8,7})", "5");
		check("Median({1,5,2,10,8,7})", "6");
		check("Median({a,b,c,d,e})", "c");
		check("Median({f,g,h,x,y,z})", "1/2*(h+x)");
	}

	public void testSystem373() {
		check("Max(7,3,8)", "8");
	}

	public void testSystem374() {
		check("Max({7,3,8,11,22,15,4,3},{{47,15}})", "47");
		check("Max(x,x,x)", "x");
	}

	public void testSystem375() {
		check("Max({7,3,8,11,22,15,4,3},{{ft(at),15}})", "Max(22,ft(at))");
	}

	public void testSystem376() {
		check("Min(7,3,8)", "3");
		check("Min(y,y,y)", "y");
	}

	public void testSystem377() {
		check("Min({7,3,8,11,22,-15,4,3},{{47,15}})", "-15");
	}

	public void testSystem378() {
		check("Min({7,3,8,11,-22,15,4,3},{{ft(at),15}})", "Min(-22,ft(at))");

	}

	public void testSystem383() {
		check("Conjugate(I)", "-I");
		check("Conjugate(I+c)", "-I+Conjugate(c)");
		check("Conjugate(I*c)", "-I*Conjugate(c)");
		check("Conjugate(a+I+c)", "-I+Conjugate(a)+Conjugate(c)");
		check("Conjugate(a*I*c)", "-I*Conjugate(a*c)");
	}

	public void testSystem384() {
		check("Conjugate(2.0-I)", "2.0+I*1.0");
	}

	public void testSystem385() {
		check("Conjugate(1/3)", "1/3");
	}

	// public void testSystem386() {
	// check("JCall(\"JCall\", \"test1\", Sin(x))", "\"Sin(x)\"");
	// }

	public void testSystem387() {
		check("FullForm(3/4+ #2+b+c*3)", //
				"Plus(Rational(3,4), b, Times(3, c), Slot(2))");
		check("$a=1+I;FullForm($a)", //
				"Complex(1,1)");
		check("FullForm(1/3+I)", //
				"Complex(Rational(1,3),1)");
		check("FullForm(ff(x_*y_))", //
				"ff(Times(Pattern(x, Blank()), Pattern(y, Blank())))");
	}

	public void testSystem387a() {
		check("JavaForm(Pi*x_NumberQ)", "Times(Pi,$p(x,NumberQ))");
		check("JavaForm(x_NumberQ)", "$p(x,NumberQ)");
		check("JavaForm(Cosh(Im(x))*Cos(Re(x))+I*Sinh(Im(x))*Sin(Re(x)))",
				"Plus(Times(Cos(Re(x)),Cosh(Im(x))),Times(CI,Sin(Re(x)),Sinh(Im(x))))");
		check("JavaForm((1/2 * (m + n^(1/2))) ^ (1/3))", //
				"Times(Power(C2,CN1D3),Power(Plus(m,Sqrt(n)),C1D3))");

		check("JavaForm(-1/4+ #2+b+c*3)", "Plus(CN1D4,b,Times(C3,c),Slot2)");

		check("JavaForm(11+a)", "Plus(ZZ(11L),a)");
		check("JavaForm(1/11+a)", "Plus(QQ(1L,11L),a)");

		check("$a=1+I;JavaForm($a)", "CC(1L,1L,1L,1L)");
		check("JavaForm(1/3+I)", "CC(1L,3L,1L,1L)");

		check("JavaForm(ff(x_*y_))", "$(ff,Times(x_,y_))");
		check("JavaForm(Log(b*x+c)/b)", "Times(Power(b,CN1),Log(Plus(c,Times(b,x))))");

		check("JavaForm(B)", "B");
		check("JavaForm(B*Log(p*x+q)/p)", "Times(B,Power(p,CN1),Log(Plus(q,Times(p,x))))");
		// check(
		// "JavaForm(B*((2*a*x+b)/((k-1)*(4*a*c-b^2)*(a*x^2+b*x+c)^(k-1))+
		// (4*k*a-6*a)/((k-1)*(4*a*c-b^2))*Integrate((a*x^2+b*x+c)^(-k+1),x)))",
		// "Times(B,Plus(Times(Integrate(Power(Plus(c,Times(b,x),Times(a,Power(x,C2))),Plus(C1,Times(CN1,k))),x),Plus(Times(integer(-6L),a),Times(C4,a,k)),Power(Plus(CN1,k),CN1),Power(Plus(Times(CN1,Power(b,C2)),Times(C4,a,c)),CN1)),Times(Plus(b,Times(C2,a,x)),Power(Plus(CN1,k),CN1),Power(Plus(Times(CN1,Power(b,C2)),Times(C4,a,c)),CN1),Power(Plus(c,Times(b,x),Times(a,Power(x,C2))),Plus(C1,Times(CN1,k))))))");
		// check(
		// "JavaForm((-A)/(2*a*(k-1)*(a*x^2+b*x+c)^(k-1))+(B-A*b/(2*a))*Integrate((a*x^2+b*x+c)^(-k),x))",
		// "Plus(Times(Integrate(Power(Plus(c,Times(b,x),Times(a,Power(x,C2))),Times(CN1,k)),x),Plus(B,Times(CN1D2,A,Power(a,CN1),b))),Times(CN1D2,A,Power(a,CN1),Power(Plus(CN1,k),CN1),Power(Plus(c,Times(b,x),Times(a,Power(x,C2))),Plus(C1,Times(CN1,k)))))");
		check("JavaForm(A/2*Log(x^2+p*x+q)+(2*B-A*p)/(4*q-p^2)^(1/2)*ArcTan((2*x+p)/(4*q-p^2)^(1/2)))",
				"Plus(Times(Plus(Times(C2,B),Times(CN1,A,p)),Power(Plus(Negate(Sqr(p)),Times(C4,q)),CN1D2),ArcTan(Times(Power(Plus(Negate(Sqr(p)),Times(C4,q)),CN1D2),Plus(p,Times(C2,x))))),Times(C1D2,A,Log(Plus(q,Times(p,x),Sqr(x)))))");
	}

	public void testSystem388() {
		check("ff(Times(Pattern(x, Blank()), Pattern(y, Blank())))", "ff(x_*y_)");
	}

	public void testSystem389() {
		check("$f389(a_):={a}; Clear($f389); $f389($test)", "$f389($test)");
		check("$f389(a_):={a}; ClearAll($f389); $f389(a_):=g(a)", "");
		check("$f389($test)", "g($test)");
	}

	public void testSystem390() {
		check("Apply((1 + 1/#) &, 10)", "11/10");
		check("FixedPoint((1 + 1/#) &, 10, 3)", "32/21");
		checkNumeric("FixedPoint((Cos(#))&,0.8)", "0.7390851332151607");
	}

	public void testSystem391() {
		check("StringJoin(\"Hello\", \" World\")", "Hello World");
	}

	public void testSystem392() {
		check("StringDrop(\"Hello\", 2)", "llo");
		check("StringDrop(\"Hello\", -2)", "Hel");
	}

	public void testSystem393() {
		check("EulerPhi(EulerPhi(25))", "8");
		check("EulerPhi(10)", "4");
	}

	public void testSystem393a() {
		// http://exploringnumbertheory.wordpress.com/2013/09/09/finding-primitive-roots/
		check("PrimitiveRootList(127)",
				"{3,6,7,12,14,23,29,39,43,45,46,48,53,55,56,57,58,65,67,78,83,85,86,91,92,93,96,\n"
						+ "97,101,106,109,110,112,114,116,118}");
		// http://exploringnumbertheory.wordpress.com/2013/09/12/an-elementary-algorithm-for-finding-primitive-roots/
		check("PrimitiveRootList(11)", "{2,6,7,8}");
		// http://exploringnumbertheory.wordpress.com/2013/10/25/more-about-checking-for-primitive-roots/
		check("PrimitiveRootList(37)", "{2,5,13,15,17,18,19,20,22,24,32,35}");
		check("PrimitiveRootList(17)", "{3,5,6,7,10,11,12,14}");
	}

	public void testSystem394() {
		check("PrimitiveRootList(8)", "{}");
		check("PrimitiveRootList(9)", "{2,5}");
		check("PrimitiveRootList(13)", "{2,6,7,11}");
		check("PrimitiveRootList(25)", "{2,3,8,12,13,17,22,23}");
	}

	public void testSystem395() {
		check("MoebiusMu(990)", "0");
		check("MoebiusMu(991)", "-1");
		check("MoebiusMu(992)", "0");
		check("MoebiusMu(993)", "1");
		check("MoebiusMu(994)", "-1");
		check("MoebiusMu(995)", "1");
		check("MoebiusMu(996)", "0");
		check("MoebiusMu(997)", "-1");
		check("MoebiusMu(998)", "1");
		check("MoebiusMu(999)", "0");
		check("MoebiusMu(1000)", "0");
	}

	public void testSystem396() {
		check("JacobiSymbol(1,111)", "1");
		check("JacobiSymbol(2,13)", "-1");
		check("JacobiSymbol(4,13)", "1");
	}

	public void testSystem397() {
		check("Re(42+I)", "42");
		check("Im(1/3+I)", "1");
		check("Re(0.5+I)", "0.5");
		check("Im(0.5+I*1.5)", "1.5");
		check("Re(42)", "42");
		check("Im(42)", "0");
		check("Re(-x)", "-Re(x)");
		check("Im(-x)", "-Im(x)");
	}

	public void testSystem398() {
		check("Numerator(3/4)", "3");
		check("Denominator(3/4)", "4");
		check("Numerator(42)", "42");
		check("Denominator(42)", "1");
		check("Numerator(3/4*x^(-3))", "3");
		check("Denominator(3/4*x^(-3))", "4*x^3");
		check("Numerator(x+3/4*x^(-3))", "3/4*1/x^3+x");
		check("Denominator(x+3/4*x^(-3))", "1");
		check("Numerator((x - 1)*(x - 2)/(x - 3)^2)", "(-2+x)*(-1+x)");
		check("Numerator(1/3*(3*a-1/2*b))", "3*a-b/2");
		check("Denominator(1/3*(3*a-1/2*b))", "3");

		check("Denominator(Csc(x))", "1");
		check("Denominator(Csc(x)^4)", "1");
		check("Denominator(42*Csc(x))", "1");
		check("Denominator(42*Csc(x)^3)", "1");

		check("Numerator(E^(-x)*x^(1/2))", "Sqrt(x)");
		check("Denominator(E^(-x)*x^(1/2))", "E^x");

		check("Together(x+3/4*x^(-3))", "(3+4*x^4)/(4*x^3)");
		check("Together((x^2-2)^3/(x^2-2)+(x^2-2)^2/(x^2-2))", "2-3*x^2+x^4");
		check("Together(a/b+c/d)", "(b*c+a*d)/(b*d)");
		check("Together((-x+3)*(x^2+2)^(-1)+6*x*(x^2+2)^(-2)+x^(-1))", "(4+6*x+8*x^2+3*x^3)/(4*x+4*x^3+x^5)");
	}

	public void testSystem399() {
		checkNumeric("Erf(3.0)", "0.9999779095030015");
	}

	public void testSystem400() {
		EvalEngine engine = EvalEngine.get();
		IExpr exprNumerator = engine.parse("8+12*x+20*x^2+12*x^3+8*x^4+3*x^5");
		IExpr exprDenominator = engine.parse("8*x+12*x^3+6*x^5+x^7");
		IExpr[] result = Algebra.cancelGCD(exprNumerator, exprDenominator);
		assertEquals(result[0].toString(), "1");
		assertEquals(result[1].toString(), "4+6*x+8*x^2+3*x^3");
	}

	public void testSystem401() {
		check("Expand((b^2*c^2-12)^(1/2))", "Sqrt(-12+b^2*c^2)");
		check("ExpandAll(1/2*((b^2*c^2-12)^(1/2)-b*c))", "-1/2*b*c+Sqrt(-12+b^2*c^2)/2");
		check("Expand(1/2*((b^2*c^2-12)^(1/2)-b*c))", "-1/2*b*c+Sqrt(-12+b^2*c^2)/2");

		check("ExpandAll(3.0+x*(4.0+x*(5.0+(33.0+x^2.0)*x^4.0)))", "3.0+4.0*x+5.0*x^2.0+33.0*x^6.0+x^8.0");
		check("HornerForm(3+4*x+5*x^2+33*x^6.0+x^8)", "3.0+x*(4.0+x*(5.0+(33.0+x^2.0)*x^4.0))");
		check("ExpandAll(3+x*(4+x*(5+(33+x^2)*x^4)))", "3+4*x+5*x^2+33*x^6+x^8");
		check("HornerForm(3+4*x+5*x^2+33*x^6+x^8)", "3+x*(4+x*(5+(33+x^2)*x^4))");
	}

	public void testSystem402() {
		check("Expand((x-1)^10)", "1-10*x+45*x^2-120*x^3+210*x^4-252*x^5+210*x^6-120*x^7+45*x^8-10*x^9+x^10");
		check("Expand((x-1)^20)",
				"1-20*x+190*x^2-1140*x^3+4845*x^4-15504*x^5+38760*x^6-77520*x^7+125970*x^8-167960*x^\n"
						+ "9+184756*x^10-167960*x^11+125970*x^12-77520*x^13+38760*x^14-15504*x^15+4845*x^16\n"
						+ "-1140*x^17+190*x^18-20*x^19+x^20");
		check("ExpandAll(3+x*(4+x*(Sin(5+(33+x^2)*x^4))))", "3+4*x+x^2*Sin(5+33*x^4+x^6)");
		check("ExpandAll(1/3*(3*a-1/2*b))", "a-b/6");
	}

	public void testSystem403() {
		check("ToString(a^2+2*a*b+b^2)", "a^2+2*a*b+b^2");
	}

	public void testSystem404() {
		check("Cos(0)", "1");
	}

	public void testSystem405() {
		// check("Series(Exp(x),{x,0,4})", "");
		check("Taylor(Cos(x),{x,0,4})", "1-x^2/2+x^4/24");
		check("Taylor(Exp(x),{x,0,10})",
				"1+x+x^2/2+x^3/6+x^4/24+x^5/120+x^6/720+x^7/5040+x^8/40320+x^9/362880+x^10/\n" + "3628800");
	}

	public void testSystem406() {
		check("JacobiMatrix({f(u),f(v),f(w),f(x)}, {u,v,w})", "{{f'(u),0,0},{0,f'(v),0},{0,0,f'(w)},{0,0,0}}");
		check("Divergence({f(u,v,w),f(v,w,u),f(w,u,v)}, {u,v,w})",
				"Derivative(1,0,0)[f][u,v,w]+Derivative(1,0,0)[f][v,w,u]+Derivative(1,0,0)[f][w,u,v]");
	}

	public void testSystem407() {
		check("ContinuedFraction(45/16)", "{2,1,4,3}");
		check("FromContinuedFraction({2,1,4,3})", "45/16");
		check("ContinuedFraction(0.753)", "{0,1,3,20,1,1,2,1,1}");
		check("FromContinuedFraction({0.0,1.0,3.0,20.0,1.0,1.0,2.0,1.0,1.0})", "0.753");
		check("FromContinuedFraction({0,1,3,20,1,1,2,1,1})", "753/1000");
		check("FromContinuedFraction({3})", "3");
		check("FromContinuedFraction({2,3})", "7/3");
		check("FromContinuedFraction({1,2,3})", "10/7");
	}

	public void testSystem408() {
		check("Infinity-Infinity", "Indeterminate");
		check("0*Infinity", "Indeterminate");
		check("0*(-Infinity)", "Indeterminate");
		check("42*Infinity", "Infinity");
		check("(-3/4)*Infinity", "-Infinity");
		check("(-3/4)*(-Infinity)", "Infinity");
		check("Infinity^(-1)", "0");
		check("Infinity", "Infinity");
		check("FullForm(Infinity)", "DirectedInfinity(1)");
		check("Infinity*Infinity", "Infinity");
		check("ComplexInfinity", "ComplexInfinity");
	}

	public void testSystem409() {//
		check("Abs(-0.5)", "0.5");
		check("Abs(-3)", "3");
		check("Abs(Pi)", "Pi");
		check("Abs(E)", "E");
		check("Abs(42)", "42");
		check("Abs(-12/90)", "2/15");
		check("Abs(-12/90*Pi*x*y)", "2/15*Pi*Abs(x*y)");
		check("Abs(2/15)", "2/15");
		checkNumeric("Abs(-5.0 +  3.0*I )", "5.8309518948453");
		check("Abs(3-4*I)", "5");
		check("Norm(-0.5)", "0.5");
		check("Norm(-3)", "3");
		check("Norm(42)", "42");
		checkNumeric("Norm(-5.0 +  3.0*I )", "5.8309518948453");
		check("Norm(3-4*I)", "5");
		check("Norm(-12/90)", "2/15");
		check("Norm(2/15)", "2/15");
		check("Norm({3,I,x,y})", "Sqrt(10+Abs(x)^2+Abs(y)^2)");
		check("Norm({3.0,I,x,y})", "Sqrt(10.0+Abs(x)^2.0+Abs(y)^2.0)");
		check("EuclideanDistance({1,2,3,4},{5,6,7,8})", "8");
		check("SquaredEuclideanDistance({1,2,3,4},{5,6,7,8})", "64");
		check("ChessboardDistance({1,2,3,4},{5,6,9,8})", "6");
		check("ManhattanDistance({1,2,3,4},{5,6,7,8})", "16");
	}

	public void testSystem410() {
		check("        $l2 = {}; \n" + "          For($j = 1, $j <= 10, $j++,\n"
				+ "             $l2 = Append($l2, $j ) \n" + "          ); $l2", "{1,2,3,4,5,6,7,8,9,10}");
	}

	public void testSystem411() {
		check("Dimensions({{{},{}}})", "{1,2,0}");
		check("Dimensions({{{},{{},{a}}}})", "{1,2}");
		check("Dimensions({{{{{a,b}}},{{},{a}}}})", "{1,2}");
		check("Dimensions({{{0,0}}})", "{1,1,2}");
		check("Dimensions({{1,0,0},{0,1,0},{0,0,1}})", "{3,3}");
		check("Dimensions({{1,0},{0,1},{0,0}})", "{3,2}");
		check("Dimensions({{{1},{0}},{{0},{1}},{{0},{0}}})", "{3,2,1}");
	}

	public void testSystem412() {
		check("DiagonalMatrix({1,2,3,4})", "{{1,0,0,0},\n" + " {0,2,0,0},\n" + " {0,0,3,0},\n" + " {0,0,0,4}}");
		check("DiagonalMatrix({1,2,3,4},2)", "{{0,0,1,0},\n" + " {0,0,0,2},\n" + " {0,0,0,0},\n" + " {0,0,0,0}}");
		check("DiagonalMatrix({1,2,3,4},-2)", "{{0,0,0,0},\n" + " {0,0,0,0},\n" + " {3,0,0,0},\n" + " {0,4,0,0}}");
	}

	public void testSystem413() {
		check("Inner(r,{1,2,3,4},{5,6,7,8},t)", "t(r(1,5),r(2,6),r(3,7),r(4,8))");
	}

	public void testSystem414() {
		check("Through(f(g, h)[x,y])", "f(g(x,y),h(x,y))");
		check("Through(f(g, h)[x,y], f)", "f(g(x,y),h(x,y))");
		check("Through(f(g, h)[x,y], g)", "f(g,h)[x,y]");
	}

	public void testSystem415() {
		check("Multinomial(1,4,4,2)", "34650");
		check("Multinomial(11,3,5)", "4232592");
	}

	public void testSystem416() {
		// check("ValueQ($valueQVar)", "False");
		// check("$valueQVar=10;ValueQ($valueQVar)", "True");
		// check("ValueQ($valueQVar(10))", "True");
		//
		// check("ClearAll($valueQVar);$valueQVar(x_):={x*y};ValueQ($valueQVar(10))",
		// "True");
		check("ValueQ(g(h,i,j))", "False");
	}

	public void testSystem417() {
		check("NumericQ(Pi)", "True");
		check("NumericQ(Sin(Cos(1/2*Pi^3)))", "True");
		check("NumericQ(Sin(Cos(1/2*x^3)))", "False");
	}

	public void testSystem418() {

		check("Limit(Sin(x)/x, x->0)", "1");
		// check("Limit(Sum(1/k*(k+1),{k,1,x}),x->Infinity)", "");
		// check("Apart((4-x^4)/(2*x^3-5*x^4))",
		// "-621/50*(x-2/5)^(-1)+25/2*x^(-1)+5*x^(-2)+2*x^(-3)+1/5");

		// check("Trace(Limit(x*Sin(1/x),x->Infinity))", "");
		check("Limit(Log(x), x->Infinity)", "Infinity");
		check("Limit(100/x, x->Infinity)", "0");
		check("Limit(2*x, x->Infinity)", "Infinity");
		check("Limit(x^(2/3), x->Infinity)", "Infinity");
		check("Limit((5/4)^x, x->Infinity)", "Infinity");
		check("Limit(1^x, x->Infinity)", "1");
		check("Limit((3/4)^x, x->Infinity)", "0");
		check("Limit((5/4)^(-x), x->Infinity)", "0");

		// the Basel problem: http://en.wikipedia.org/wiki/Basel_problem
		check("Limit(Sum(x^(-2),{x,1,m}), m->Infinity)", "Pi^2/6");
		check("Limit(Sum(x^(-5),{x,1,m}), m->Infinity)", "Limit(HarmonicNumber(m,5),m->Infinity)");
		check("Limit(Sum(x^(-10),{x,1,m}), m->Infinity)", "Pi^10/93555");

		check("Limit((4-x^4)/(2*x^3-5*x^4),x->Infinity)", "1/5");
		check("Limit(Sum(k,{k,0,x}),x->5)", "15"); // issue 50
		check("Limit(product(k,{k,1,x}),x->5)", "120"); // issue 50
		check("Limit(Sum(k,{k,0,x}),x->Pi+2)", "1/2*(2+Pi)*(3+Pi)");
		check("Limit(Sin(Pi*x)/(Pi*x),x->0)", "1");
		check("Limit(1+Sin(x)/x,x->Infinity)", "1");
		check("Limit(Pi^42,x->42)", "Pi^42");
		check("Limit(x,x->42)", "42");
		check("Limit(a+b+2*x,x->42)", "84+a+b");
		check("Limit(a+b+2*x,x->Infinity)", "Infinity");
		check("Limit(a+b+2*x,x->-Infinity)", "-Infinity");
		check("Limit(-x,x->Infinity)", "-Infinity");
		check("Limit(x-x,x->Infinity)", "0");
		check("Limit((x^2-3*x+2)/(x-1),x->1)", "-1");
		check("Limit(Sin(2*n)/Sin(3*n),n->0)", "2/3");
		check("Limit((2*Sin(x)-Sin(2*x))/(x-Sin(x)),x->0)", "6");

		check("Limit(x^10,x->Infinity)", "Infinity");
		check("Limit(x^11,x->-Infinity)", "-Infinity");
		check("Limit(1/x,x->Infinity)", "0");
		check("Limit(6/x,x->Infinity)", "0");
		check("Limit(x^(-3),x->Infinity)", "0");
		check("Limit(1/x,x->-Infinity)", "0");
		check("Limit((1+1/x)^x,x->Infinity)", "E");
		check("Limit((1-1/x)^x,x->Infinity)", "1/E");

		// check("Limit(1/x,x->0)", "Infinity");
		check("Limit(Sin(x)/x,x->0)", "1");
		check("Limit((1-Cos(x))/x,x->0)", "0");
		check("Limit((1-Cos(x))/x^2,x->0)", "1/2");

		check("Limit((-2*x-5)/(x-3),x->Infinity)", "-2");
		check("Limit((-2*x-5)/(-3*x-3),x->Infinity)", "2/3");
		check("Limit((x-5)/(x-3),x->Infinity)", "1");
		check("Limit((x-5)/(x-3),x->-Infinity)", "1");
		check("Limit((x^3+x-6)/(x^2-4*x+3),x->Infinity)", "Infinity");
		check("Limit((x^3+x-6)/(x^2-4*x+3),x->-Infinity)", "-Infinity");

		check("Limit((-2*x^3+x-6)/(x^2-4*x+3),x->Infinity)", "-Infinity");
		check("Limit((-2*x^3+x-6)/(x^2-4*x+3),x->-Infinity)", "Infinity");

		check("Limit((-2*x^3+x-6)/(-10*x^2-4*x+3),x->Infinity)", "Infinity");
		check("Limit((-2*x^3+x-6)/(-10*x^2-4*x+3),x->-Infinity)", "-Infinity");

		check("Limit((x^3-1)/(2*x^3-3*x),x->Infinity)", "1/2");
		check("Limit((x^3-1)/(2*x^3+3*x),x->Infinity)", "1/2");
		check("Limit((4-x^4)/(2*(x^3)-5*(x^4)),x->Infinity)", "1/5");
		check("Limit((4-x^4)/(2*(x^3)-5*(x^4)+1),x->Infinity)", "1/5");

		check("Limit((1+(x)^(-1))^x, x->Infinity)", "E");
		check("Limit((1+(2*x)^(-1))^x, x->Infinity)", "Sqrt(E)");
		check("Limit((1-(2*x)^(-1))^x, x->Infinity)", "1/Sqrt(E)");
		check("Limit((1+a*(x^(-1)))^x, x->Infinity)", "E^a");
		check("Limit((1-a*(x^(-1)))^x, x->Infinity)", "E^(-a)");
	}

	public void testSystem419() {
		check("TrigToExp(a+b+Sin(c+d))", "a+b+(I*1/2)/E^(I*(c+d))-I*1/2*E^(I*(c+d))");
		check("TrigToExp(Cos(x)+f(a))", "1/(2*E^(I*x))+E^(I*x)/2+f(a)");
		check("TrigToExp(Tan(x))", "(I*(E^(-I*x)-E^(I*x)))/(E^(-I*x)+E^(I*x))");
		check("TrigToExp(ArcSin(x))", "-I*Log(I*x+Sqrt(1-x^2))");
		check("TrigToExp(ArcCos(x))", "Pi/2+I*Log(I*x+Sqrt(1-x^2))");
		check("TrigToExp(ArcTan(x))", "I*1/2*Log(1-I*x)-I*1/2*Log(1+I*x)");
	}

	public void testSystem420() {
		check("TrigReduce(Cos(x)*Cos(y)*Sin(x))", //
				"Sin(2*x-y)/4+Sin(2*x+y)/4");
		check("TrigReduce(Sin(x)*Cos(y))", //
				"1/2*(Sin(x-y)+Sin(x+y))");
		check("TrigReduce(Sin(x)*Cos(y)*x^2*y^4+42)", //
				"42+1/2*x^2*y^4*Sin(x-y)+1/2*x^2*y^4*Sin(x+y)");
		check("TrigReduce(Sin(10)*Cos(11)*x^2*y^4+42)", //
				"42-1/2*x^2*y^4*Sin(1)+1/2*x^2*y^4*Sin(21)");
		check("TrigReduce(Sin(x)^3)", //
				"3/4*Sin(x)-Sin(3*x)/4");
		check("TrigReduce(Cos(x)^3)", //
				"3/4*Cos(x)+Cos(3*x)/4");
	}

	public void testSystem421() {
		check("MatchQ(I, Complex(0,x_))", "True");

		check("MatchQ(linear(a+42+60*c,h), linear(a_. + b_. * x_, x_))", "False");
		// check("b_. x_","");
		check("MatchQ(a+b+c+d, HoldPattern(Plus(_,_)))", "True");
		check("Times(_,_)", "_^2");
		check("MatchQ(a+b+c+d, HoldPattern(Times(_,_)))", "False");

		check("MatchQ(I, Complex(0,x_))", "True");
		check("MatchQ(Sin(x)^3, Sin(a_.*x_)^n_?IntegerQ)", "True");
		check("MatchQ(powered(h,h), powered(x_ ^ a_., x_))", "True");
		check("MatchQ(powered(h^3,h), powered(x_ ^ a_., x_))", "True");
		check("MatchQ(42, _?IntegerQ)", "True");
		check("MatchQ(a+b+c+d, HoldPattern(Times(_,_)))", "False");
		check("MatchQ(a+b+c+d, HoldPattern(Plus(_,_)))", "True");
		check("MatchQ(Expand((a+b)^2), HoldPattern(Plus(_,_)))", "True");
		check("MatchQ(Expand((a*b)^2), HoldPattern(Plus(_,_)))", "False");
		check("MatchQ({a,b,c}, _List)", "True");
		check("MatchQ(linear(42+d,d), linear(a_. + b_. * x_, x_))", "True");
		check("MatchQ(linear(h,h), linear(a_. + b_. * x_, x_))", "True");
		check("MatchQ(linear(60*h,h), linear(a_. + b_. * x_, x_))", "True");
		check("MatchQ(linear(a+42+60*h,h), linear(a_. + b_. * x_, x_))", "True");
		check("MatchQ(linear(a+42+60*c,h), linear(a_. + b_. * x_, x_))", "False");
		check("PossibleZeroQ(Pi-Pi)", "True");
	}

	public void testSystem422() {
		check("Default(Power,2)", "1");
		check("Default(Plus)", "0");
		check("Default(Times)", "1");
	}

	// public void testSystem404() {
	// check("Plot3D(Sin(x)*Cos(y),{x,-10,10},{y,-10,10},{PlotRange->Automatic})",
	// "");
	// };
	//
	// public void testSystem405() {
	// check("Plot(Sin(x),{x,0,10})", "");
	// };

	public void testSystem803() {
		// see
		// http://google-opensource.blogspot.com/2009/06/introducing-apache-commons-math.html
		check("LinearProgramming({-2, 1, -5}, {{1, 2, 0},{3, 2, 0},{0,1,0},{0,0,1}}, {{6,-1},{12,-1},{0,1},{1,0}})",
				"{4.0,0.0,1.0}");
		// creates unbounded error
		// check("LinearProgramming({-2, 1, -5}, {{1, 2},{3, 2},{0,1}},
		// {{6,-1},{12,-1},{0,1}})",
		// "{4.0,0.0,1.0}");
	}

	public void testSystem804() {
		// check("Simplify(x*(x^2.00))", "x^3.0");
		// check("Simplify(5.0+4.0(x-0.0)+3.0(x-0.0)(x-1.0)+1.0(x-0.0)(x-1.0)(x-2.0))",
		// "x^3.0+3.0*x+5.0");
		// check("Simplify(D(Integrate(1/(x^2 + 2), x), x))", "(x^2+2)^(-1)");

		check("D(1/3*(-3*ArcTan((2*x+1)*3^(-1/2))*3^(-1/2)-1/2*Log(x^2-x+1))+1/3*Log(x+1), x)", //
				"1/(3*(1+x))+1/3*((1-2*x)/(2*(1-x+x^2))-2/(1+(1+2*x)^2/3))");
		check("Simplify(D(Integrate(1/(x^3 + 1), x), x))", //
				"1/(1+x^3)");
		// check("Apart((1+(1/x))/(1+(2/x)))","");
		// check("FullForm((1+(1/x))/(1+(2/x)))","");
		// check("Simplify((1+(1/x))/(1+(2/x)))","");
	}

	public void testSystem805() {
		check("Solve(4*x^(-2)-1==0,x)", //
				"{{x->-2},{x->2}}");

		check("Solve(x^2==a^2,x)", "{{x->-a},{x->a}}");
		check("Solve((x^2-1)/(x-1)==0,x)", "{{x->-1}}");

		// LinearSolve[{{1,1,1},{1,1,-1},{1,-1,-1}},{100,50,10}]
		// Fraction[][] testData = { { new Fraction(1), new Fraction(1), new
		// Fraction(1) },
		// { new Fraction(1), new Fraction(1), new Fraction(-1) }, { new
		// Fraction(1), new Fraction(-1), new Fraction(-1) } };
		// Fraction[] testVector = { new Fraction(100), new Fraction(50), new
		// Fraction(10) };
		// FieldMatrix<Fraction> aMatrix = new Array2DRowFieldMatrix(testData);
		// FieldVector<Fraction> bVector = new
		// ArrayFieldVector<Fraction>(testVector);
		// try {
		//
		// final FieldLUDecomposition<Fraction> lu = new
		// FieldLUDecomposition<Fraction>(aMatrix);
		//
		// FieldDecompositionSolver<Fraction> fds = lu.getSolver();
		// // lu.getL();
		// FieldVector<Fraction> xVector = fds.solve(bVector);
		// assertEquals("", xVector.toString());
		//
		// } catch (final ClassCastException e) {
		// if (Config.SHOW_STACKTRACE) {
		// e.printStackTrace();
		// }
		// } catch (final IndexOutOfBoundsException e) {
		// if (Config.SHOW_STACKTRACE) {
		// e.printStackTrace();
		// }
		// }

		// check("solve({x + y + z == 100,x + y - z == 50,x - y - z ==
		// 10},{x,y,z})", "{{x->55,y->20,z->25}}");
		// check("Solve(y+x/a==0,y)", "{{y->-a^(-1)*x}}");
		// check("Solve(((x-8.5)^2)+(y+9.5)^2==1.4,x)",
		// "{{x->0.5*(-4.0*y^2.0-76.0*y-355.4)^0.5+8.5},{x->-0.5*(-4.0*y^2.0-76.0*y-355.4)^0.5+8.5}}");
		// check("Solve(((x-8.5556577)^2)+(y+9.551234)^2==14/10,x)",
		// "{{x->0.5*(-4.0*y^2.0-76.409872*y-359.30428369102395)^0.5+8.5556577},{x->-0.5*(-4.0*y^2.0-76.409872*y-359.30428369102395)^0.5+8.5556577}}");
		//
		// check("Solve(a+2x==0,x)", "{{x->(-1/2)*a}}");
		// check("Together((x^2-1)/(x-1))", "x+1");

		// check("Solve(x*(-0.006*x^2.0+1.0)^2.0-0.1*x==7.217,x)",
		// "{{x->16.955857433561537},{x->-14.046984987941926+I*(-3.7076756332964123)},{x->-14.046984987941926+I*3.7076783744216715},{x->5.569057466623865+I*(-5.000248815113639)},{x->5.569057466623865+I*5.000251556238898}}");

		// issue #68
		check("Solve(x^(1/2)==0,x)", "{{x->0}}");
		check("solve(sqrt(112*x)==0,x)", "{{x->0}}");
		check("Solve(7^(1/2)*x^(1/2)==0,x)", "{{x->0}}");

		check("Solve({x+y==1, x-y==0}, {x,y})", "{{x->1/2,y->1/2}}");
		check("Solve(x*(-0.006*x^2.0+1.0)^2.0-0.1*x==7.217,x)",
				"{{x->16.95586},{x->-14.04698+I*(-3.70768)},{x->-14.04698+I*3.70768},{x->5.56906+I*(-5.00025)},{x->5.56906+I*5.00025}}");

		checkNumeric("CoefficientList(x*(-0.006*x^2.0+1.0)^2.0-0.1*x-7.217,x)", "{-7.217,0.9,0,-0.012,0,3.6E-5}");

		checkNumeric("Solve(2.5*x^2+1650==0,x)", //
				"{{x->I*(-25.69046515733026)},{x->I*25.69046515733026}}");
		checkNumeric("Solve(x*(x^2+1)^2==7,x)",
				"{{x->1.1927223989709494},{x->-0.9784917834108953+I*(-1.038932735856145)},{x->-0.9784917834108953+I*1.038932735856145},{x->0.38213058392542043+I*(-1.6538990550344321)},{x->0.38213058392542043+I*1.6538990550344321}}");
		checkNumeric("NSolve(x*(x^2+1)^2==7,x)",
				"{{x->1.1927223989709494},{x->-0.9784917834108953+I*(-1.038932735856145)},{x->-0.9784917834108953+I*1.038932735856145},{x->0.38213058392542043+I*(-1.6538990550344321)},{x->0.38213058392542043+I*1.6538990550344321}}");
		check("Solve(x^2==a^2,x)", "{{x->-a},{x->a}}");
		check("Solve(4*x^(-2)-1==0,x)", //
				"{{x->-2},{x->2}}");
		check("Solve((x^2-1)/(x-1)==0,x)", //
				"{{x->-1}}");

		check("Solve(x+5==a,x)", "{{x->-5+a}}");
		check("Solve(x+5==10,x)", "{{x->5}}");
		check("Solve(x^2==a,x)", "{{x->-Sqrt(a)},{x->Sqrt(a)}}");
		check("Solve(x^2+b*c*x+3==0, x)", //
				"{{x->1/2*(-b*c-Sqrt(-12+b^2*c^2))},{x->1/2*(-b*c+Sqrt(-12+b^2*c^2))}}");
		check("Solve({x+2*y==10,3*x+y==20},{x,y})", "{{x->6,y->2}}");
		check("Solve(x^2==0,{x,y,z})", "{{x->0}}");
		check("Solve(x^2==0,x)", "{{x->0}}");
		check("Solve(x^2==4,x)", "{{x->-2},{x->2}}");
		check("Solve({x^2==4,x+y==10},{x,y})", "{{x->-2,y->12},{x->2,y->8}}");
		check("Solve({x^2==4,x+20==10},x)", "{}");
		check("Solve({x^2==4,x+y^2==6},{x,y})",
				"{{x->-2,y->-2*Sqrt(2)},{x->-2,y->2*Sqrt(2)},{x->2,y->-2},{x->2,y->2}}");
		check("Solve({x^2==4,x+y^2==6,x+y^2+z^2==24},{x,y,z})",
				"{{x->-2,y->-2*Sqrt(2),z->-3*Sqrt(2)},{x->-2,y->-2*Sqrt(2),z->3*Sqrt(2)},{x->-2,y->\n"
						+ "2*Sqrt(2),z->-3*Sqrt(2)},{x->-2,y->2*Sqrt(2),z->3*Sqrt(2)},{x->2,y->-2,z->-3*Sqrt(\n"
						+ "2)},{x->2,y->-2,z->3*Sqrt(2)},{x->2,y->2,z->-3*Sqrt(2)},{x->2,y->2,z->3*Sqrt(2)}}");
	}

	public void testSystem806() {
		check("PowerExpand((a^b)^(1/2))", "a^(b/2)");
		check("PowerExpand((a*b)^(1/2))", "Sqrt(a)*Sqrt(b)");
		check("PowerExpand(Log((a^b)^c))", "b*c*Log(a)");
	}

	public void testSystem807() {
		check("Refine(Abs(n), n>=0)", "n");
		check("Refine(Abs(n+1), n>=0)", "1+n");
		check("Refine(Abs(n+Abs(m)), n>=0)", "n+Abs(m)");
		check("Refine(Abs(n), n<0)", "-n");
		check("Refine(Abs(n*Abs(m)), n<0)", "-n*Abs(m)");
	}

	public void testSystem991() {
		check("PolynomialQuotient(x^2+2*x+1,x+2,x)", "x");
		check("PolynomialQuotient(x^2+x+1,2*x+1,x)", "1/4+x/2");
		check("PolynomialQuotient(x^2-1,x-1,x)", "1+x");
	}

	public void testSystem992() {
		check("PolynomialRemainder(x^2+2*x+1,x+2,x)", "1");
		check("PolynomialRemainder(x^2+x+1,2*x+1,x)", "3/4");
		check("PolynomialRemainder(x^2-1,x-1,x)", "0");
	}

	public void testSystem993() {
		check("PolynomialQuotientRemainder(x^2+2*x+1,x+2,x)", "{x,1}");
		check("PolynomialQuotientRemainder(x^2+x+1,2*x+1,x)", "{1/4+x/2,3/4}");
		check("PolynomialQuotientRemainder(x^2-1,x-1,x)", "{1+x,0}");
	}

	public void testSystem994() {
		check("PolynomialGCD(3+3*x^3,3+3*x^3)", "3+3*x^3");
		check("PolynomialExtendedGCD(3+3*x^3,3+3*x^3,x)", "{1+x^3,{0,1/3}}");
		check("PolynomialGCD(x^2-1,x-1)", "-1+x");
		check("PolynomialGCD(x+1,x^2-1)", "1+x");
		check("PolynomialExtendedGCD(x+1,x^2-1,x)", "{1+x,{1,0}}");
		check("PolynomialGCD(-1+x^16,(x^2-1)*((1+x^4)))", "-1+x^2-x^4+x^6");
		check("PolynomialGCD(8*x^5+28*x^4+34*x^3+41*x^2+35*x-14,12*x^5+4*x^4-27*x^3-9*x^2-84*x-28)",
				"14+7*x+8*x^2+4*x^3");

		check("PolynomialGCD(2*x^5-2*x,(x^2-1)^2)", "-1+x^2");
		check("PolynomialGCD(2*x^5-2*x,(x^2-1)^2,Modulus->2)", "(1+x)^4");
		check("PolynomialExtendedGCD(2*x^5-2*x,(x^2-1)^2,x)", //
				"{-1+x^2,{x/4,1/2*(-2-x^2)}}");
		check("PolynomialExtendedGCD(2*x^5-2*x,(x^2-1)^2,x, Modulus->2)", "{1+x^4,{0,1}}");

		check("ExpandAll((1+x)^2*(7+x)*(11+x)*(17+x))", "1309+3001*x+2110*x^2+454*x^3+37*x^4+x^5");
		check("PolynomialLCM((1+x)^2*(7+x)*(17+x),(1+x)*(7+x)*(11+x))", "1309+3001*x+2110*x^2+454*x^3+37*x^4+x^5");
		check("PolynomialLCM((1+x)^2*(7+x)*(17+x),(1+x)*(7+x)*(11+x), Modulus->31)", "(7+x)*(11+x)*(17+x)*(1+x)^2");
	}

	public void testSystem996() {
		check("FactorTerms(3+3*x^3)", "3*(1+x^3)");
		check("FactorTerms(3+3/4*x^3+12/17*x^2,x)", "3/68*(68+16*x^2+17*x^3)");
	}

	public void testSystem997() {
		check("GroebnerBasis({a+b+c+d, a*b+a*d+b*c+c*d, a*b*c+a*b*d+a*c*d+b*c*d,1-a*b*c*d}, {d,c,b,a})",
				"{1-a^4-a^2*b^2+a^6*b^2,-a-b+a^3*b^2+a^2*b^3,-a+a^5-c+a^4*c,-2*a^2+a*b+a^4*b^2-a*c+b*c,a^\n"
						+ "2+2*a*c+c^2,a+b+c+d}");
		check("GroebnerBasis({a+b+c+d, a*b+a*d+b*c+c*d, a*b*c+a*b*d+a*c*d+b*c*d,1-a*b*c*d}, {d,c,b,a},MonomialOrder ->DegreeReverseLexicographic)",
				"{a+b+c+d,a^2+2*a*c+c^2,-a^3+a*b^2-a^2*c+b^2*c,-1-a^4+a^3*b+a^2*b^2-a^3*c+a^2*b*c,-a+a^\n"
						+ "5-c+a^4*c,-a-b+a^3*b^2+a^2*b^3,-2*a^2+a*b+a^4*b^2-a*c+b*c}");
		check("GroebnerBasis({x-1},{x})", "{-1+x}");
		// check(
		// "GroebnerBasis({a+b+c+d, a*b+a*d+b*c+c*d, a*b*c+a*b*d+a*c*d+b*c*d,
		// 1-a*b*c*d}, {d,c,b,a}, MonomialOrder->DegreeReverseLexicographic,
		// Modulus->1)",
		// "{a+b+c+d,a^2+2*a*c+c^2,a^3-a*b^2+a^2*c-b^2*c,1+a^4-a^3*b-a^2*b^2+a^3*c-a^2*b*c,a-a^5+c-a^4*c,a+b-a^3*b^2-a^2*b^3,2*a^2-a*b-a^4*b^2+a*c-b*c}");
	}

	public void testSystem998() {
		check("RootIntervals(x^4-2)", //
				"{{-1246977/1048576+I*5/1048576,-1246977/1048576-I*1/2097152,-2493943/2097152-\n"
						+ "I*1/2097152,-2493943/2097152+I*5/1048576},{-1/2097152-I*2493943/2097152,-1/\n"
						+ "2097152-I*1246977/1048576,5/1048576-I*1246977/1048576,5/1048576-\n"
						+ "I*2493943/2097152},{-1/2097152+I*4871/4096,-1/2097152+I*2493941/2097152,5/\n"
						+ "1048576+I*2493941/2097152,5/1048576+I*4871/4096},{2493941/2097152+I*5/1048576,\n"
						+ "2493941/2097152-I*1/2097152,4871/4096-I*1/2097152,4871/4096+I*5/1048576}}");
		check("RootIntervals(4+x^2+2*x+3*x^3)", //
				"{{-3145745/3145728+I*17/3145728,-3145745/3145728-I*1/1179648,-1179647/1179648-\n"
						+ "I*1/1179648,-1179647/1179648+I*17/3145728},{1048565/3145728-I*10433155/9437184,\n"
						+ "1048565/3145728-I*579623/524288,1572877/4718592-I*579623/524288,1572877/4718592-\n"
						+ "I*10433155/9437184},{1048565/3145728+I*10433257/9437184,1048565/3145728+\n"
						+ "I*5216599/4718592,1572877/4718592+I*5216599/4718592,1572877/4718592+\n"
						+ "I*10433257/9437184}}");
		check("Expand((x-1)^3)", "-1+3*x-3*x^2+x^3");
		check("RootIntervals(x^3-3*x^2+3*x-1)", //
				"{{262143/262144+I*1/524288,262143/262144-I*3/1048576,1048577/1048576-I*3/1048576,\n"
						+ "1048577/1048576+I*1/524288}}");
	}

	public void testSystem999() {
		// check("Roots(x^6 - 4*x^3 + 8==0, x)",
		// "x==-1-I||x==-1+I||x==1/2-I*1/2-Sqrt(I*6)/2||x==1/2-I*1/2+Sqrt(I*6)/2||x==1/2+I*1/\n"
		// + "2-Sqrt(-I*6)/2||x==1/2+I*1/2+Sqrt(-I*6)/2");

		// check("Factor(1+x^2,GaussianIntegers->True)", "");
		check("-5/2+I*1/2*Sqrt(15)", //
				"-5/2+I*1/2*Sqrt(15)");
		check("Roots(Expand((x-1)^3)==0, x)", //
				"x==1");
		check("-1/2*(-I*3^(1/2)+1)", //
				"1/2*(-1+I*Sqrt(3))");
		check("Roots(x^3-3*x-2==0, x)", "x==-1||x==2");

		check("Roots((x^2-1)/(x-1)==0, x)", "x==-1");
		check("Roots(3/4*x^2+9/16==0, x)", //
				"x==I*1/2*Sqrt(3)||x==-I*1/2*Sqrt(3)");

		check("Factor(3*x^2+6)", "3*(2+x^2)");
		check("Factor(3/4*x^2+9/16)", "3/16*(3+4*x^2)");
		check("Factor(3/4*x^2+9/16+7)", "1/16*(121+12*x^2)");
		check("Factor(3/4*x^2+9/16*x+7)", "1/16*(112+9*x+12*x^2)");

		check("Roots(x^2 - 4*x + 8==0, x)", "x==2-I*2||x==2+I*2");
		// not reduccible with current factor method
		check("Factor(x^4-2*x^3+2*x^2-4*x+4)", "4-4*x+2*x^2-2*x^3+x^4");
		// check("Roots(x^6 - 4*x^3 + 8==0, x)",
		// "x==-1-I||x==-1+I||x==1/2-I*1/2-Sqrt(I*6)/2||x==1/2-I*1/2+Sqrt(I*6)/2||x==1/2+I*1/\n"
		// + "2-Sqrt(-I*6)/2||x==1/2+I*1/2+Sqrt(-I*6)/2");

		check("Roots(x^2 + 5*x + 10==0, x)", //
				"x==1/2*(-5-I*Sqrt(15))||x==1/2*(-5+I*Sqrt(15))");
		check("Roots(4+x^2+2*x+3*x^3==0, x)", //
				"x==-1||x==1/3-I*1/3*Sqrt(11)||x==1/3+I*1/3*Sqrt(11)");
		check("Roots(x^3-4*x^2+x+6==0, x)", //
				"x==-1||x==2||x==3");
		// check("Roots(x^3+4*x^2+x+2)",
		// "{(-1/3)*((1/2)^(1/3)*(12*87^(1/2)+146)^(1/3)+(1/2)^(1/3)*(-12*87^(1/2)+146)^(1/3)+\n"
		// +
		// "4),(-1/3)*((-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(12*87^(1/2)+146)^(1/3)+(I*1/2*3^(1/\n"
		// +
		// "2)-1/2)*(1/2)^(1/3)*(-12*87^(1/2)+146)^(1/3)+4),(-1/3)*((I*1/2*3^(1/2)-1/2)*(1/2)^(\n"
		// +
		// "1/3)*(12*87^(1/2)+146)^(1/3)+(-I*1/2*3^(1/2)-1/2)*(1/2)^(1/3)*(-12*87^(1/2)+146)^(\n"
		// + "1/3)+4)}");
		checkNumeric("N({1/2*(I*15^(1/2)-5),1/2*(-I*15^(1/2)-5)})",
				"{-2.5+I*1.9364916731037085,-2.5+I*(-1.9364916731037085)}");

		check("Roots(x^6-1==0, x)", //
				"x==(-1)^(2/3)||x==-(-1)^(2/3)||x==(-1)^(1/3)||x==-(-1)^(1/3)||x==-1||x==1");

		check("Factor(x^5 - x^3 - x^2 + 1)", //
				"(1-x)^2*(1+x)*(1+x+x^2)");
		check("FactorSquareFree(x^5 - x^3 - x^2 + 1)", //
				"(1-x)^2*(1+2*x+2*x^2+x^3)");
		check("SquareFreeQ(x^5 - x^3 - x^2 + 1)", //
				"False");
		check("SquareFreeQ(12.0)", //
				"False");

		check("Factor(4+x^2+2*x+3*x^3)", //
				"(1+x)*(4-2*x+3*x^2)");
		check("Factor( Expand((x^3+4*x^2+3*x+2)*(x^3+4*x^2+x+2)))", //
				"(2+x+4*x^2+x^3)*(2+3*x+4*x^2+x^3)");
		check("Factor(4+8*x+19*x^2+20*x^3+20*x^4+8*x^5+x^6)", //
				"(2+x+4*x^2+x^3)*(2+3*x+4*x^2+x^3)");
		check("Factor(Expand((x-1)^3))", //
				"(-1+x)^3");
		check("Factor(x^6-1)", //
				"(-1+x)*(1+x)*(1-x+x^2)*(1+x+x^2)");

		check("Expand((y+x)*(-y+x)*(y^4+x*y^3+x^2*y^2+x^3*y+x^4)*(y^4-x*y^3+x^2*y^2-x^3*y+x^4))", //
				"x^10-y^10");
		check("Factor(x^10-y^10)", //
				"(x-y)*(x+y)*(x^4+x^3*y+x^2*y^2+x*y^3+y^4)*(x^4-x^3*y+x^2*y^2-x*y^3+y^4)");

		check("Expand((-1+x)*(1+x)*(1+x+x^2)*(1-x+x^2))", //
				"-1+x^6");

		check("Factor(x^5+x^4+x+1)", //
				"(1+x)*(1+x^4)");

		check("Factor(-1+x^16)", //
				"(-1+x)*(1+x)*(1+x^2)*(1+x^4)*(1+x^8)");
		check("Expand((-1+x)*(1+x)*(1+x^2)*(1+x^4)*(1+x^8))", //
				"-1+x^16");

		check("Factor(5+x^12,Modulus->7)", //
				"(2+x^3)*(5+x^3)*(4+x^6)");
		check("Factor(1+x^10,Modulus->2)", //
				"(1+x)^2*(1+x+x^2+x^3+x^4)^2");// "(1+x)^2*(1+x+x^2+x^3+x^4)^2");
		check("Factor(1+x^4, Extension->I)", //
				"(-I+x^2)*(I+x^2)");
	}

	public void testSystem1000() {
		check("NRoots(4*x^(-2)-1)", "{-2.0,2.0}");
		check("N(Sin(13*Degree))", "0.224951");
		check("NRoots(0.224951054343865*x^2 + 5*x + 10)", "{-2.22216,-20.0049}");
		check("NRoots(Sin(13*Degree)*x^2 + 5*x + 10)", "{-2.22216,-20.0049}");
		check("NRoots(x^2-4)", "{2.0,-2.0}");
		check("NRoots(2.5*x^2+1650)", "{I*(-25.69047),I*25.69047}");
		check("NRoots(2*x^3 - 4*x^2 -22*x + 24)", "{1.0,-3.0,4.0}");

		check("NRoots(x^2 + 5*x + 10)", "{-2.5+I*(-1.93649),-2.5+I*1.93649}");
		check("NRoots(x^3+2*x^2+2*x+2)", //
				"{-0.228155+I*(-1.11514),-0.228155+I*1.11514,-1.54369}");
		check("NRoots(x^3+4*x^2+x+2)", //
				"{-0.0624351+I*(-0.715691),-0.0624351+I*0.715691,-3.87513}");
	}

	// public void testSystem999() {
	// check("FactorI(4+x^2+2*x+3*x^3, x)", "(1+x)*(4-2*x+3*x^2)");
	// check("FactorI(4+8*x+19*x^2+20*x^3+20*x^4+8*x^5+x^6, x)",
	// "(2+x+4*x^2+x^3)*(2+3*x+4*x^2+x^3)");
	// check("FactorI(Expand((x-1)^3), x)", "(-1+x)^3");
	// check("FactorI(x^6-1, x)", "(-1+x)*(1+x)*(1+x+x^2)*(1-x+x^2)");
	// check("FactorI(x^5+x^4+x+1, x)", "(1+x)*(1+x^4)");
	// check("FactorI(-1+x^16, x)", "(-1+x)*(1+x)*(1+x^2)*(1+x^4)*(1+x^8)");
	// check("FactorI(243-405*x+270*x^2-90*x^3+15*x^4-x^5,x)", "(-1)*(-3+x)^5");
	// }

	// public void testSystemTest() { check("TestIt(x^2+Sin(x)*Cos(y))",
	// "Cos(#2)*Sin(#1)+#1^2")};

	public void testSystem1100() {
		check("Coefficient(10*(x^2)+2*(y^2)+2*x,x,2)", "10");

		// see Issue#28
		check("Coefficient(y^2, x,2)", "0");
		check("Coefficient(10*(x^2)+2*(y^2)+2*x,x,2)", "10");
		check("Coefficient(10*(x^2)+1*(y^2)+2*x,x,2)", "10");
		// see Issue#21
		check("Coefficient(a*(x^3) + 0.5*(x^2) + 0.25*x + d, x, 2)", "0.5");
		check("Coefficient(a*(x^3) + 0.5*(x^2) + 0.25*x + d, x, 2)", "0.5");
	}

	public void testSystem1101() {
		check("MatrixRank({{ 0.0, 13.0, 25.0, 43.0, 81.0, 0.0, 39.0, 60.0, 70.0, 21.0, 44.0, 0.0 },\n"
				+ "{ 44.0, 0.0, 13.0, 67.0, 35.0, 0.0, 84.0, 35.0, 23.0, 88.0, 11.0, 0.0 },\n"
				+ "{ 5.0, 34.0, 0.0, 143.0, 35.0, 0.0, 65.0, 99.0, 22.0, 13.0, 26.0, 0.0 },\n"
				+ "{ 89.0, 23.0, 13.0, 0.0, 78.0, 0.0, 13.0, 24.0, 98.0, 65.0, 0.0, 0.0 },\n"
				+ "{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },\n"
				+ "{ 56.0,  4.0, 24.0, 56.0, 78.0, 0.0, 13.0, 0.0, 24.0, 57.0, 8.0, 1.0 },\n"
				+ "{ 0.0, 0.0, 46.0, 666.0, 34.0, 13.0, 67.0, 9.0, 12.0, 45.0, 38.0, 0.0 }})", "6");
		// see Issue#25
		check("MatrixRank({{2, 0, -1, 0, 0},{1, 0, 0, -1, 0},{3, 0, 0, -2, -1},{0, 1, 0, 0, -2},{0, 1, -1, 0, 0}})",
				"4");
		check("MatrixRank({{1, 2, 3, 4 },\n" + "{ 1, 1, 1, 1 },\n" + "{ 2, 3, 4, 5 },\n" + "{ 2, 2, 2, 2 }})", "2");
		check("MatrixRank({{ 0.0, 13.0, 25.0, 43.0, 81.0, 0.0, 39.0, 60.0, 70.0, 21.0, 44.0, 0.0 },\n"
				+ "{ 44.0, 0.0, 13.0, 67.0, 35.0, 0.0, 84.0, 35.0, 23.0, 88.0, 11.0, 0.0 },\n"
				+ "{ 5.0, 34.0, 0.0, 143.0, 35.0, 0.0, 65.0, 99.0, 22.0, 13.0, 26.0, 0.0 },\n"
				+ "{ 89.0, 23.0, 13.0, 0.0, 78.0, 0.0, 13.0, 24.0, 98.0, 65.0, 0.0, 0.0 },\n"
				+ "{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },\n"
				+ "{ 56.0,  4.0, 24.0, 56.0, 78.0, 0.0, 13.0, 0.0, 24.0, 57.0, 8.0, 1.0 },\n"
				+ "{ 0.0, 0.0, 46.0, 666.0, 34.0, 13.0, 67.0, 9.0, 12.0, 45.0, 38.0, 0.0 }})", "6");
		check("MatrixRank({{ 0, 0, 0, 0 }})", "0");
		check("MatrixRank({{ 0.0, 1.0, 0.0, 0.0 }})", "1");
		check("MatrixRank({{1, 2, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 1},\n"
				+ "{0, 0, -1},\n" + "{1, 2, 1}})", "2");

		check("MatrixRank({{ 5.0, 7.0, 10.0, 3.0, 5.0, 8.0 },\n" + "{ 5.0, 2.0, 3.0, 10.0, 11.0, 9.0 },\n"
				+ "{ 4.0, 3.0, 9.0, 12.0, 8.0, 9.0 }})", "3");
	}

	public void testSystem1102() {
		// see Issue#25
		check("RowReduce({{2, 0, -1, 0, 0},{1, 0, 0, -1, 0},{3, 0, 0, -2, -1},{0, 1, 0, 0, -2},{0, 1, -1, 0, 0}})",
				"{{1,0,0,0,-1},\n" + " {0,1,0,0,-2},\n" + " {0,0,1,0,-2},\n" + " {0,0,0,1,-1},\n" + " {0,0,0,0,0}}");
		check("RowReduce({{1, 2, 3, 4 },\n" + "{ 1, 1, 1, 1 },\n" + "{ 2, 3, 4, 5 },\n" + "{ 2, 2, 2, 2 }})",
				"{{1,0,-1,-2},\n" + " {0,1,2,3},\n" + " {0,0,0,0},\n" + " {0,0,0,0}}");
		check("RowReduce({{ 0, 13, 25, 43, 81, 0, 39, 60, 70, 21, 44, 0 },\n"
				+ "{ 44, 0, 13, 67, 35, 0, 84, 35, 23, 88, 11, 0 },\n"
				+ "{ 5, 34, 0, 143, 35, 0, 65, 99, 22, 13, 26, 0 },\n"
				+ "{ 89, 23, 13, 0, 78, 0, 13, 24, 98, 65, 0, 0 },\n" + "{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },\n"
				+ "{ 56,  4, 24, 56, 78, 0, 13, 0, 24, 57, 8, 1 },\n"
				+ "{ 0, 0, 46, 666, 34, 13, 67, 9, 12, 45, 38, 0 }})",
				"{{1,0,0,0,0,0,1925039/915778,7255405/8242002,14896709/12363003,50118311/24726006,\n"
						+ "658759/12363003,-294443/12363003},\n"
						+ " {0,1,0,0,0,0,7292554/457889,49317121/4121001,140022961/12363003,105413240/\n"
						+ "12363003,50521031/12363003,-3016555/12363003},\n"
						+ " {0,0,1,0,0,0,32243633/915778,20460745/915778,9585730/457889,19202927/915778,\n"
						+ "3942636/457889,-219173/457889},\n"
						+ " {0,0,0,1,0,0,-125327/457889,-1963265/8242002,-10889221/12363003,-2155982/\n"
						+ "12363003,-1614647/12363003,372365/24726006},\n"
						+ " {0,0,0,0,1,0,-11718565/915778,-10919696/1373667,-28629710/4121001,-61720409/\n"
						+ "8242002,-11130250/4121001,1474495/8242002},\n"
						+ " {0,0,0,0,0,1,-428241609/5952557,-62325871/1373667,-528223006/53573013,\n"
						+ "-2261685887/53573013,-738773132/53573013,24338692/53573013},\n"
						+ " {0,0,0,0,0,0,0,0,0,0,0,0}}");

		check("RowReduce({{ 0.0, 13.0, 25.0, 43.0, 81.0, 0.0, 39.0, 60.0, 70.0, 21.0, 44.0, 0.0 },\n"
				+ "{ 44.0, 0.0, 13.0, 67.0, 35.0, 0.0, 84.0, 35.0, 23.0, 88.0, 11.0, 0.0 },\n"
				+ "{ 5.0, 34.0, 0.0, 143.0, 35.0, 0.0, 65.0, 99.0, 22.0, 13.0, 26.0, 0.0 },\n"
				+ "{ 89.0, 23.0, 13.0, 0.0, 78.0, 0.0, 13.0, 24.0, 98.0, 65.0, 0.0, 0.0 },\n"
				+ "{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },\n"
				+ "{ 56.0,  4.0, 24.0, 56.0, 78.0, 0.0, 13.0, 0.0, 24.0, 57.0, 8.0, 1.0 },\n"
				+ "{ 0.0, 0.0, 46.0, 666.0, 34.0, 13.0, 67.0, 9.0, 12.0, 45.0, 38.0, 0.0 }})", //
				"{{1.0,0.0,0.0,0.0,0.0,0.0,2.10208,0.880296,1.20494,2.02695,0.0532847,-0.0238165},\n"
						+ " {0.0,1.0,0.0,0.0,0.0,0.0,15.92647,11.96727,11.32597,8.52651,4.08647,-0.243999},\n"
						+ " {0.0,0.0,1.0,0.0,0.0,0.0,35.20901,22.34247,20.93462,20.96898,8.61046,-0.47866},\n"
						+ " {0.0,0.0,0.0,1.0,0.0,0.0,-0.273706,-0.238202,-0.880791,-0.17439,-0.130603,0.0150597},\n"
						+ " {0.0,0.0,0.0,0.0,1.0,0.0,-12.79629,-7.9493,-6.94727,-7.48852,-2.70086,0.1789},\n"
						+ " {0.0,0.0,0.0,0.0,0.0,1.0,-71.94246,-45.37189,-9.85987,-42.21689,-13.79002,0.454309},\n"
						+ " {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0}}");

		check("RowReduce({{ 0, 0, 0, 0 }})", "{{0,0,0,0}}");
		check("RowReduce({{ 0.0, 1.0, 0.0, 0.0 }})", "{{0.0,1.0,0.0,0.0}}");

		check("RowReduce({{1, 2, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 1},\n"
				+ "{0, 0, -1},\n" + "{1, 2, 1}})",
				"{{1,2,0},\n" + " {0,0,1},\n" + " {0,0,0},\n" + " {0,0,0},\n" + " {0,0,0},\n" + " {0,0,0},\n"
						+ " {0,0,0}}");

		check("RowReduce({{ 5.0, 7.0, 10.0, 3.0, 5.0, 8.0 },\n" + "{ 5.0, 2.0, 3.0, 10.0, 11.0, 9.0 },\n"
				+ "{ 4.0, 3.0, 9.0, 12.0, 8.0, 9.0 }})",
				"{{1.0,0.0,0.0,2.50862,2.67241,1.86207},\n" + " {0.0,1.0,0.0,-3.19828,-1.46552,-0.827586},\n"
						+ " {0.0,0.0,1.0,1.28448,0.189655,0.448276}}");
		check("RowReduce({{1,2,3},{4,5,6}})", "{{1,0,-1},\n" + " {0,1,2}}");

		check("RowReduce({{1,1,0,1,5},{1,0,0,2,2},{0,0,1,4,-1},{0,0,0,0,0}})",
				"{{1,0,0,2,2},\n" + " {0,1,0,-1,3},\n" + " {0,0,1,4,-1},\n" + " {0,0,0,0,0}}");

		check("RowReduce({{0,0,0},{0,0,0}})", "{{0,0,0},\n" + " {0,0,0}}");
	}

	public void testSystem1103() {
		// see Issue#77
		check("NullSpace({{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}})", "{}");
		check("NullSpace({{1,1.4,0},{3,2.5,7},{0.2546,2,0}})", "{}");
		// see Issue#25
		check("NullSpace({{1,0,-3,0,2,-8}," + "{0,1,5,0,-1,4}," + "{0,0,0,1,7,-9}," + "{0,0,0,0,0,0}})", //
				"{{8,-4,0,9,0,1},\n" + //
						" {3,-5,1,0,0,0},\n" + //
						" {-2,1,0,-7,1,0}}");
		check("NullSpace({{0,0,0}," + "{0,0,0}," + "{0,0,0}," + "{0,0,0}})",
				"{{1,0,0},\n" + " {0,1,0},\n" + " {0,0,1}}");
		check("NullSpace({{0,0}," + "{0,0}," + "{0,0}," + "{0,0}})", "{{1,0},\n" + " {0,1}}");
	}

	public void testSystem1105() {
		check("$p(Sin(x_)^m_IntegerQ):=f(x)^(-m)/;m<0;$p(Sin(x)^2)", "$p(Sin(x)^2)");
	}

	public void testSystem1106() {
		check("InterpolatingPolynomial({{1,7},{3,11},{5,27}},x)", "7+(2+3/2*(-3+x))*(-1+x)");
	}

	public void testSystem1107() {
		check("f(4) /. f(x_) /; x > 0 -> x ^ 2", "16");

		check("a + b + c /. a + b -> t", "c+t");
		check("a + b + c /. a + c -> t", "b+t");
		// check("a + 2 + b + c + x * y /. n_Integer + s__Symbol + rest_ -> {n,
		// s, rest}", "");
		check("f(a, b, c, d) /. f(first_, rest___) -> {first, {rest}}", "{a,{b,c,d}}");

		check("f(4) /. f(x_?(# > 0&)) -> x ^ 2", "16");
		check("f(4) /. f(x_) /; x > 0 -> x ^ 2", "16");

		check("f(a, b, c, d) /. f(start__, end__) -> {{start}, {end}}", "{{a},{b,c,d}}");
	}

	// public void testSystem1108() {
	// check("LaplaceTransform(t^3*E^(-3*t), t, s)", "6*(3+s)^(-4)");
	// check("LaplaceTransform(2*t^5+ t^2/2, t, s)", "240*s^(-6)+s^(-3)");
	// check("LaplaceTransform(Exp(t^2), t, s)", "LaplaceTransform(E^t^2,t,s)");
	// check("LaplaceTransform(Log(t), t, s)", "(-EulerGamma-Log(s))*s^(-1)");
	//
	// check("LaplaceTransform(1, t, s)", "s^(-1)");
	// check("LaplaceTransform(t^3, t, s)", "6*s^(-4)");
	// check("LaplaceTransform(t, t, s)", "s^(-2)");
	// check("LaplaceTransform(t*Exp(4*t), t, s)", "(4-s)^(-2)");
	//
	// check("LaplaceTransform(t^3*Cosh(t), t, s)", "");
	// check("LaplaceTransform(t*Sin(2*t)*Exp(-3*t), t, s)", "");
	// }

	public void testIssue80() {
		// issue #80: LinearProgramming with expressions
		check("NMinimize({-2*x+y-5, 2*y+x<=6&&2*y+3*x<=12&&y>=0},{x,y})", "{-13.0,{x->4.0,y->0.0}}");
		check("NMaximize({-2*x+y-5, 2*y+x<=6&&2*y+3*x<=12&&y>=0},{x,y})", "{-2.0,{x->0.0,y->3.0}}");
	}

	public void testIssue95() {
		// check("Solve((-5+x)^(3/4)==5*x,x)", "{{x->5+5*5^(1/3)}}");
		// check("Solve(Sqrt(x-5)+Sqrt(x+5)==5,x)", "{{x->29/4}}");
		check("Solve((-5+x)^(3/4)==5,x)", "{{x->5+5*5^(1/3)}}");
		check("Solve((-5+x)^(1/2)==5,x)", "{{x->30}}");
	}

	public void testIssue96() {
		// check("LinearSolve({{2,8},{-5,-20}},{6,-15})", "{3,0}");
		check("Solve({2*x+7*y==6,-5*x-20*y==-15},{x,y})", "{{x->3,y->0}}");
		check("Solve({2*x+8*y==6,-5*x-20*y==-15},{x,y})", "{{x->3-4*y}}");
		check("Solve({3*x+2*y-z==1,2*x-2*y+4*z==-2,-x+1/2*y-z==0},{x,y,z})", "{{x->1,y->-2,z->-2}}");
		check("Solve({x+3*y-2*z==5,3*x+5*y+6*z==7},{x,y,z})", "{{x->-1-7*z,y->2+3*z}}");
		check("Solve({x+y==62,x-6==4*(y-6)},{x,y})", "{{x->46,y->16}}");
		check("Solve({3*x==2,4*x==2},{x})", "{}");
	}

	public void testIssue99() {
		check("Limit(1/x,x->0)", "Infinity");
		check("Limit(x^(-3),x->0, Direction->-1)", "Infinity");
		check("Limit(x^(-3),x->0, Direction->1)", "-Infinity");
		check("Limit(x^(-4),x->0, Direction->-1)", "Infinity");
		check("Limit(x^(-4),x->0, Direction->1)", "Infinity");
		check("Limit(x^(-37/4),x->0, Direction->-1)", "Infinity");
		check("Limit(x^(-37/4),x->0, Direction->1)", "Limit(1/x^(37/4),x->0,Direction->1)");
		check("Limit(x^(-37/4),x->0, Direction->Automatic)", "Infinity");
		check("Limit(1/x^2,x->0)", "Infinity");
		check("Solve(-1-4/3*Limit(1/x^2,x->0)==(2*a2)/a3+(-2*a5)/a3,a3)", "Solve(-Infinity==(2*a2)/a3+(-2*a5)/a3,a3)");
	}

	public void testHMCLinearSolve() {
		// https://www.math.hmc.edu/calculus/tutorials/linearsystems
		check("LinearSolve({{1,0,0},{0,1,0},{0,0,1}},{2,3,-4})", "{2,3,-4}");
		check("LinearSolve({{1,0,-3},{0,1,2},{0,0,0}},{-5,4,0})", "{-5,4,0}");

		// prints additional message to console
		check("LinearSolve({{1,0,0},{0,1,0},{0,0,0}},{3,2,1})",
				"LinearSolve(\n" + "{{1,0,0},\n" + " {0,1,0},\n" + " {0,0,0}},{3,2,1})");

		check("LinearSolve({{1,2,3},{2,-1,1},{3,0,-1}},{9,8,3})", "{2,-1,3}");
	}

	public void testHMCSolve() {
		// https://www.math.hmc.edu/calculus/tutorials/linearsystems
		check("Solve({x+2*y+3*z==9,2*x-y+z==8,3*x-z==3},{x,y,z})", "{{x->2,y->-1,z->3}}");
	}

	public void testIssue102() {
		check("Collect((a+b)/c,b)", //
				"a/c+b/c");
		check("Collect((a+b)/c,c)", //
				"(a+b)/c");

		check("Collect(a/c+b/c,c)", //
				"(a+b)/c");
		check("Collect((a+b)/c,c)", //
				"(a+b)/c");
		check("Collect((a+b)/c,b)", //
				"a/c+b/c");
		check("Collect((a+b)/c,a)", //
				"a/c+b/c");
		check("(a+b)/c==a/c+b/c", //
				"True");
		check("(a+b)/c==a/c+b/c==(a+b)/c", //
				"True");
		check("Pi==Pi==Pi", //
				"True");
	}

	public void testGithub18() {
		// github issue #18
		boolean old = Config.EXPLICIT_TIMES_OPERATOR;
		try {
			Config.EXPLICIT_TIMES_OPERATOR = false;
			if (!Config.EXPLICIT_TIMES_OPERATOR) {
				Config.DOMINANT_IMPLICIT_TIMES = true;
				if (Config.DOMINANT_IMPLICIT_TIMES) {
					check("Hold(1/2Pi) // FullForm", "Hold(Power(Times(2, Pi), -1))");
					check("1/2Pi // FullForm", "Times(Rational(1,2), Power(Pi, -1))");
					check("1/2(a+b) // FullForm", "Times(Rational(1,2), Power(Plus(a, b), -1))");
					check("1/(a+b)2 // FullForm", "Times(Rational(1,2), Power(Plus(a, b), -1))");
					check("a^(b)(c) // FullForm", "Power(a, Times(b, c))");
				}
				Config.DOMINANT_IMPLICIT_TIMES = false;
				if (!Config.DOMINANT_IMPLICIT_TIMES) {
					check("Hold(1/2Pi) // FullForm", "Hold(Times(Times(Rational(1,2), 1), Pi))");
					check("1/2Pi // FullForm", "Times(Rational(1,2), Pi)");
					check("1/2(a+b) // FullForm", "Times(Rational(1,2), Plus(a, b))");
					check("1/(a+b)2 // FullForm", "Times(2, Power(Plus(a, b), -1))");
					check("a^(b)(c) // FullForm", "Times(Power(a, b), c)");
				}

				check("2(b+c) // FullForm", //
						"Times(2, Plus(b, c))");
				check("2(b+c)3 // FullForm", //
						"Times(6, Plus(b, c))");
				check("a(b+c) // FullForm", //
						"a(Plus(b, c))");
				check("a*(b+c) // FullForm", //
						"Times(a, Plus(b, c))");
				check("1E-2 // FullForm", //
						"Plus(-2, E)");
				check("1E+2 // FullForm", //
						"Plus(2, E)");
				checkNumeric("1.0E-2 // FullForm", //
						"0.7182818284590451");
				checkNumeric("1.0e-2 // FullForm", //
						"Plus(-2.0, e)");
				checkNumeric("1x-2 // FullForm", //
						"Plus(-2, x)");
				check("N(1E-2)", "0.718282");
				check("0x1  // FullForm", "0");
				check("0xf  // FullForm", "0");
				check("0y1  // FullForm ", "0");
			}
			Config.EXPLICIT_TIMES_OPERATOR = true;
			if (Config.EXPLICIT_TIMES_OPERATOR) {
				check("2(b+c)", //
						"Syntax error in line: 1 - End-of-file not reached.\n" + "2(b+c)\n" + " ^");
				check("2(b+c)3 // FullForm",
						"Syntax error in line: 1 - End-of-file not reached.\n" + "2(b+c)3 // FullForm\n" + " ^");
				check("2*(b+c) // FullForm", //
						"Times(2, Plus(b, c))");
				check("2*(b+c)*3 // FullForm", //
						"Times(6, Plus(b, c))");
				check("a(b+c) // FullForm", //
						"a(Plus(b, c))");
				check("a*(b+c) // FullForm", //
						"Times(a, Plus(b, c))");
				check("1E-2 // FullForm", //
						"0.01");
				check("1E+2 // FullForm", //
						"100.0");
				checkNumeric("1.0E-2 // FullForm", //
						"0.01");
				checkNumeric("1x-2 // FullForm", //
						"Syntax error in line: 1 - End-of-file not reached.\n" + //
								"1x-2 // FullForm\n" + //
								" ^");
				check("N(1E-2)", "0.01");
				check("0x1", "1");
				check("0xf", "15");
				check("0y1", //
						"Syntax error in line: 1 - End-of-file not reached.\n" + //
								"0y1\n" + //
								" ^");
				checkNumeric("-3.1434555694057773E-11", //
						"-3.143455569405777E-11");
			}
		} finally {
			Config.EXPLICIT_TIMES_OPERATOR = old;
		}
	}

	public void testNCalcGithub42() {
		// https://github.com/tranleduy2000/ncalc/issues/42
		check("D((x+2)/(x-3),x)", "1/(-3+x)-(2+x)/(3-x)^2");
		check("Together(1/(-3+x)-(2+x)/(3-x)^2)", "-5/(9-6*x+x^2)");
		check("Simplify(1/(-3+x)-(2+x)/(3-x)^2)", "-5/(3-x)^2");
	}

	// public void testOutputformats() {
	// check("{6.7^-4, 6.7^6, 6.7^8}", //
	// "{0.0005,90458.38217,4.06067678*10^6}");
	// check("{8.^5, 11.^7, 13.^9}", //
	// "{32768.0,1.94871710*10^7,1.06044994*10^10}");
	//
	// checkNumeric("{6.7^-4, 6.7^6, 6.7^8}", //
	// "{4.962503078612847E-4,90458.38216900002,4060676.7755664107}");
	// }
}
