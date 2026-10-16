package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Numeric values of special functions which were compared with mpmath: the elliptic functions of
 * Jacobi and Weierstrass, regularized hypergeometric functions, the q-Pochhammer symbol, the
 * derivative of the zeta function and orthogonal polynomials of a high degree.
 */
public class SpecialFunctionsNumericTest extends ExprEvaluatorTestCase {

  @Test
  public void testJacobiReciprocal() {
    check("JacobiDC(0.75,0.3)", //
        "1.25037");
    check("JacobiNC(0.75,0.3)", //
        "1.34347");
    check("JacobiND(0.75,0.3)", //
        "1.07445");
    check("N(JacobiDC(5/2,1/3),30)", //
        "-1.47659400217380615696763527737");
    check("N(JacobiNC(1/2+I,9/10),30)", //
        "0.611100835427232125480142009723+I*0.418959938932342919139315755517");
    check("N(JacobiND(-7/4,-2),30)", //
        "0.755880200415203859596286035996");
    check("JacobiDC(0,m)", //
        "1");
    check("JacobiNC(x,0)", //
        "Sec(x)");
    check("JacobiND(x,1)", //
        "Cosh(x)");
    check("JacobiDC(x,m)", //
        "JacobiDC(x,m)");
  }

  @Test
  public void testInverseJacobi() {
    check("N(InverseJacobiSN(1/3,9/10),30)", //
        "0.34586687152379939419467492265");
    check("N(InverseJacobiCN(-1/2,1/3),30)", //
        "2.36206180970438163423301112904");
    check("N(InverseJacobiDN(97/100,1/3),30)", //
        "0.439139078841977456184593033346");
    check("N(InverseJacobiSC(5/2,1/100),30)", //
        "1.19241275821756794174126454149");
    check("N(InverseJacobiSD(9/10,1/3),30)", //
        "0.966357784001052657198626228476");
    check("N(InverseJacobiCD(1/3,9/10),30)", //
        "2.23222524182437379400789584917");
    check("N(InverseJacobiNC(3,1/3),30)", //
        "1.3215221757527599880130607017");
    check("N(InverseJacobiND(101/100,1/3),30)", //
        "0.246411026611444418617128714414");
    check("N(InverseJacobiDC(3,1/3),30)", //
        "1.39191281182950228476880955196");
    check("InverseJacobiDC(3.0,0.25)", //
        "1.3443");
    check("InverseJacobiNC(3.0,0.25)", //
        "1.29576");
    check("InverseJacobiND(1.01,0.25)", //
        "0.28552");
    check("InverseJacobiDC(1,m)", //
        "0");
  }

  @Test
  public void testJacobiAmplitudeZeta() {
    check("N(JacobiAmplitude(5/2,1/3),30)", //
        "2.21528294350988621609769659814");
    check("N(JacobiAmplitude(40,9/10),30)", //
        "24.0967706752357467381611460164");
    check("N(JacobiAmplitude(-7/4,1/100),30)", //
        "-1.74518116372737315458725580901");
    check("JacobiZeta(0.75,0.3)", //
        "0.0812118");
    check("N(JacobiZeta(1/3,9/10),30)", //
        "0.182540439115525124912353545034");
    check("N(JacobiEpsilon(5/2,1/3),30)", //
        "1.97285872241426512419463865321");
    check("JacobiZeta(x,m)", //
        "JacobiZeta(x,m)");
  }

  @Test
  public void testWeierstrass() {
    check("N(WeierstrassP(1/3,{2,3}),30)", //
        "9.01243889622971832168738209855");
    check("N(WeierstrassP(1/2+I/2,{12,4}),30)", //
        "-0.034300648279669172761253911961+I*(-1.71461455089026584943654281056)");
    check("N(WeierstrassP(1/100,{-3,1/2}),30)", //
        "9999.99998500017857892849837514");
    check("N(WeierstrassPPrime(1/3,{2,3}),30)", //
        "-53.9173668446853311323156934231");
    check("N(WeierstrassPPrime(-7/4,{1+I,2}),30)", //
        "-2.55774197562965252488558204222+I*(-1.09392780000021185391697170192)");
    check("WeierstrassP(0.75,{2,3})", //
        "1.86887");
    check("N(WeierstrassHalfPeriods({12,4}),30)[[1]]", //
        "0.943487520500501860596609400102");
    check("N(WeierstrassHalfPeriods({7,-3}))[[1]]", //
        "1.42758");
    // a lattice with complex roots, and half-periods with a quotient in the lower half plane
    check("Chop(N(WeierstrassInvariants(WeierstrassHalfPeriods({2,3}))))", //
        "{2.0,3.0}");
    check("Chop(WeierstrassInvariants({1.0,2.0+I}))", //
        "{11.81705,0}");
    check("Chop(WeierstrassInvariants({1.0,-I}))", //
        "{11.81705,0}");
    check("WeierstrassHalfPeriods({12,4})", //
        "WeierstrassHalfPeriods({12,4})");
  }

  @Test
  public void testHypergeometricPFQRegularized() {
    check("N(HypergeometricPFQRegularized({1/3,5/2},{7/3,3/2},1/2),30)", //
        "1.07833787326788651697687492184");
    check("HypergeometricPFQRegularized({1/3,5/2},{7/3,3/2},-7.0)", //
        "0.455633");
    check("N(HypergeometricPFQRegularized({1/3,5/2},{-2,3/2},1/2),30)", //
        "0.121780282928057408668341402432");
    check("HypergeometricPFQRegularized({1/3,5/2},{0,3/2},0.5)", //
        "0.503369");
    check("N(HypergeometricPFQRegularized({1/3},{7/3},1+2*I),30)", //
        "0.840831459254398084938910210567+I*0.308546166067114330013468641224");
    check("HypergeometricPFQRegularized({1/3,5/2},{-2,3/2},1/2)", //
        "HypergeometricPFQRegularized({1/3,5/2},{-2,3/2},1/2)");
    check("HypergeometricPFQRegularized({a,5/2},{-2,3/2},0.5)", //
        "HypergeometricPFQRegularized({a,5/2},{-2,3/2},0.5)");
  }

  @Test
  public void testQPochhammerInfinite() {
    check("QPochhammer(0.3,0.7)", //
        "0.33109");
    check("N(QPochhammer(1/3,-7/10),30)", //
        "0.726644937151787578197510179986");
    check("N(QPochhammer(5/2,1/4+I/3),30)", //
        "-0.175111929248196943555815195513+I*2.01116459517160317036600272186");
    check("N(QPochhammer(9/10),30)", //
        "0.00000128606743427661762745959391398");
    check("QPochhammer(0.5,1.5)", //
        "QPochhammer(0.5,1.5)");
    check("QPochhammer(a,0.5)", //
        "QPochhammer(a,0.5)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testZetaDerivative() {
    check("N(Zeta'(2),30)", //
        "-0.937548254315843753702574094568");
    check("Zeta'(2.0)", //
        "-0.937548");
    check("N(Zeta'(1/2),30)", //
        "-3.92264613920915172747153144671");
    check("N(Zeta'(-7/2),30)", //
        "0.0091542136299415124611607515345");
    check("N(Zeta'(-4),30)", //
        "0.00798381145026862428069667079879");
    check("N(Zeta'(1/2+20*I),30)", //
        "0.7145067908437759923766753826+I*1.00524088394701315547272407661");
    check("N(Zeta'(1001/1000),30)", //
        "-999999.927193845906045828989141");
    check("N(Zeta'(-7/2+30*I),30)", //
        "820.24123397478991246858757589+I*(-27.0280559779120281618570376903)");
    check("N(Zeta'(Pi))", //
        "-0.167603");
    check("Zeta'(2)", //
        "Zeta'(2)");
    check("Zeta'(x)", //
        "Zeta'(x)");
    check("Zeta'(1.0)", //
        "Zeta'(1.0)");
    check("Zeta'(0)", //
        "-Log(2*Pi)/2");
  }

  @Test
  public void testEllipticPiEqualParameters() {
    check("N(EllipticPi(-2,4,-2),30)", //
        "2.0202730034368504638854327855");
    check("EllipticPi(-2.0,4.0,-2.0)", //
        "2.02027");
    check("EllipticPi(-3.0,7.0,-3.0)", //
        "2.8822");
    check("N(EllipticPi(-1000,4,-1000),30)", //
        "0.0949962400493696085123990362503");
    check("N(EllipticPi(-1000,1,-900),30)", //
        "0.0322076064355402371136671991374");
    check("EllipticPi(-2.0,2.0,-2.0)", //
        "0.816109");
  }

  @Test
  public void testLaguerreLRecurrence() {
    check("N(LaguerreL(60,1/3,30),30)", //
        "281484.765224733219353362061248");
    check("LaguerreL(60,1/3,30.0)", //
        "281484.8");
    check("LaguerreL(100,150.0)", //
        "-1.44632*10^31");
    check("LaguerreL(200,0.5,150.0)", //
        "3.97426*10^30");
    check("N(LaguerreL(100,-3,2+I),30)", //
        "0.521459037377424589555222837506+I*(-0.294612550854968953367171000262)");
    check("LaguerreL(5,1/3,3)", //
        "37351/29160");
    check("LaguerreL(4,-2,3)", //
        "-9/8");
    check("LaguerreL(7,1/3)", //
        "-2013911/5511240");
    check("LaguerreL(3,x)", //
        "1-3*x+3/2*x^2-x^3/6");
    check("LaguerreL(3,l,z)", //
        "1/6*(6+11*l+6*l^2+l^3-18*z-15*l*z-3*l^2*z+9*z^2+3*l*z^2-z^3)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testPolynomialsOfHighDegree() {
    check("LegendreP(150,0.5)", //
        "0.06749829804674269");
    check("LegendreP(60,-0.9)", //
        "0.03178959241738747");
    check("LegendreP(60,2,-0.9)", //
        "-78.21507200124601");
    check("GegenbauerC(80,1/3,0.7)", //
        "0.03053538104138585");
    check("GegenbauerC(150,2.5,-0.9)", //
        "-1834.652340442329");
    check("JacobiP(80,1/3,2,0.7)", //
        "0.11568957665361553");
    check("JacobiP(60,0.5,-0.5,0.2)", //
        "0.1058000117447746");
    check("LegendreQ(60,0.3)", //
        "-0.06651569328662452");
    check("BellB(60,-0.9)", //
        "ScriptEngine: ERROR-IN-OUTPUTFORM");
    check("SphericalHarmonicY(60,3,0.3,0.5)", //
        "-0.03959101855479954+I*(-0.558289578777496)");
    check("LegendreP(60,0.5+0.25*I)", //
        "549900.3041401437+I*1203438.0442906923");
    check("LegendreP(60,0.3``30)", //
        "0.0961103249624579958148258041886");
    check("GegenbauerC(120,1/3,-0.9``30)", //
        "-0.030216675930620444449491150071");
    check("LegendreP(5,0.3)", //
        "0.345386");
  }

  @Test
  public void testJacobiCSDSNS() {
    check("JacobiCS(0.75,0.3)", //
        "1.11462");
    check("JacobiDS(0.75,0.3)", //
        "1.3937");
    check("JacobiNS(0.75,0.3)", //
        "1.49746");
    check("JacobiCS(10.0,1/3)", //
        "-2.36537");
    check("JacobiDS(-1.75,-2.0)", //
        "-2.16006");
    check("JacobiNS(0.5+I,0.9)", //
        "0.587852+I*(-0.480963)");
    check("N(JacobiCS(5/2,1/3),30)", //
        "-0.751540995624095502758676657487");
    check("N(JacobiDS(1/2+I,9/10),30)", //
        "0.301926284358379539438128527334+I*(-0.936437066692907622161660648004)");
    check("N(JacobiNS(-7/4,-2),30)", //
        "-1.63274748761595043221631483131");
    check("{JacobiCS(0,m),JacobiDS(0,m),JacobiNS(0,m)}", //
        "{ComplexInfinity,ComplexInfinity,ComplexInfinity}");
    check("{JacobiCS(x,0),JacobiDS(x,0),JacobiNS(x,0)}", //
        "{Cot(x),Csc(x),Csc(x)}");
    check("{JacobiCS(x,1),JacobiDS(x,1),JacobiNS(x,1)}", //
        "{Csch(x),Csch(x),Coth(x)}");
    check("JacobiNS(x,m)", //
        "JacobiNS(x,m)");
    check("JacobiCS({a,b},m)", //
        "{JacobiCS(a,m),JacobiCS(b,m)}");
  }

  @Test
  public void testJacobiCSDSNSRules() {
    check("FunctionExpand({JacobiCS(x,m),JacobiDS(x,m),JacobiNS(x,m)})", //
        "{JacobiCN(x,m)/JacobiSN(x,m),JacobiDN(x,m)/JacobiSN(x,m),1/JacobiSN(x,m)}");
    check("D(JacobiCS(x,m),x)", //
        "-JacobiDS(x,m)*JacobiNS(x,m)");
    check("D(JacobiDS(x,m),x)", //
        "-JacobiCS(x,m)*JacobiNS(x,m)");
    check("D(JacobiNS(x,m),x)", //
        "-JacobiCS(x,m)*JacobiDS(x,m)");
    check("D(JacobiNS(x,m),m)", //
        "(JacobiCS(x,m)*JacobiDS(x,m)*((-1+m)*x+JacobiEpsilon(x,m)-m*JacobiCD(x,m)*JacobiSN(x,m)))/(\n2*(1-m)*m)");
    check("D(JacobiCS(x^2,m),x)", //
        "-2*x*JacobiDS(x^2,m)*JacobiNS(x^2,m)");
  }

  @Test
  public void testInverseJacobiCSDSNS() {
    check("InverseJacobiCS(2.0,1/3)", //
        "0.469119");
    check("InverseJacobiDS(2.0,1/3)", //
        "0.507985");
    check("InverseJacobiNS(2.0,1/3)", //
        "0.531451");
    check("N(InverseJacobiCS(2/5,9/10),30)", //
        "1.54424943283267524020443210645");
    check("N(InverseJacobiDS(3,1/100),30)", //
        "0.339704502452996170162455725741");
    check("N(InverseJacobiNS(10/9,99/100),30)", //
        "1.46419794999349431375656844887");
    check("InverseJacobiCS(-2.0,1/3)", //
        "-0.469119");
    check("Chop(JacobiCS(InverseJacobiCS(0.5+I,0.3),0.3))", //
        "0.5+I*1.0");
    check("Chop(JacobiDS(InverseJacobiDS(-1.2+0.7*I,0.3),0.3))", //
        "-1.2+I*0.7");
    check("Chop(JacobiNS(InverseJacobiNS(0.3-1.5*I,-2.0),-2.0))", //
        "0.3+I*(-1.5)");
    check("{InverseJacobiCS(x,0),InverseJacobiDS(x,0),InverseJacobiNS(x,0)}", //
        "{ArcCot(x),ArcCsc(x),ArcCsc(x)}");
    check("{InverseJacobiCS(x,1),InverseJacobiDS(x,1),InverseJacobiNS(x,1)}", //
        "{ArcCsch(x),ArcCsch(x),ArcCoth(x)}");
    check("{InverseJacobiDC(x,0),InverseJacobiNC(x,0),InverseJacobiNC(x,1),InverseJacobiND(x,1)}", //
        "{ArcSec(x),ArcSec(x),ArcCosh(x),ArcCosh(x)}");
    check("{InverseJacobiCS(-x,m),InverseJacobiDS(-x,m),InverseJacobiNS(-x,m)}", //
        "{-InverseJacobiCS(x,m),-InverseJacobiDS(x,m),-InverseJacobiNS(x,m)}");
    check("{InverseJacobiNS(1,m),InverseJacobiNS(-1,m)}", //
        "{EllipticK(m),-EllipticK(m)}");
    check("InverseJacobiDS(x,m)", //
        "InverseJacobiDS(x,m)");
  }

  /**
   * On the real axis above 1 the inverse Jacobi functions are continuous from above. The values
   * are the ones of Mathematica; machine precision and arbitrary precision give the same value.
   */
  @Test
  public void testInverseJacobiOnBranchCuts() {
    // InverseJacobiSN[2.5, 0.3] = 0.8447480298725263 + 2.0753631352924686*I
    check("InverseJacobiSN(2.5,0.3)", //
        "0.844748+I*2.07536");
    check("N(InverseJacobiSN(5/2,3/10),30)", //
        "0.844748029872526773169338777606+I*2.07536313529246914385344055588");
    check("InverseJacobiSN(-2.5,0.3)", //
        "-0.844748+I*(-2.07536)");
    // InverseJacobiCN[2.5, 0.3] = -1.365204510707016*I
    check("InverseJacobiCN(2.5,0.3)", //
        "I*(-1.3652)");
    check("N(InverseJacobiCN(5/2,3/10),30)", //
        "I*(-1.36520451070701577952665176851)");
    // InverseJacobiCD[Pi, 0.3] = 1.0821567013903146 - 2.075363135292469*I
    check("InverseJacobiCD(Pi,0.3)", //
        "1.08216+I*(-2.07536)");
    // InverseJacobiDN[-0.4, 0.3] = 1.7138894481787919 + 2.588706183893212*I
    check("InverseJacobiDN(-0.4,0.3)", //
        "1.71389+I*2.58871");
    check("N(InverseJacobiDN(-2/5,3/10),30)", //
        "1.71388944817879106203893484504+I*2.58870618389321006913103238477");
    // InverseJacobiCS[-2., 1/3] = -0.46911948732715847
    check("InverseJacobiCS(-2.0,1/3)", //
        "-0.469119");
    // the function of the result is the argument again
    check("Chop(JacobiSN(InverseJacobiSN(2.5,0.3),0.3))", //
        "2.5");
    check("Chop(JacobiCN(InverseJacobiCN(-2.5,0.3),0.3))", //
        "-2.5");
    check("Chop(JacobiDN(InverseJacobiDN(-0.4,0.3),0.3))", //
        "-0.4");
    check("Chop(JacobiDN(InverseJacobiDN(-1.2+0.7*I,0.3),0.3))", //
        "-1.2+I*0.7");
    check("Chop(JacobiCN(InverseJacobiCN(-2.0*I,0.3),0.3))", //
        "I*(-2.0)");
    check("Chop(JacobiNS(InverseJacobiNS(0.4,0.3),0.3))", //
        "0.4");
  }

  @Test
  public void testCarlsonOfNegativeArguments() {
    check("CarlsonRF(-1.0,0.5,2.0)", //
        "1.07045+I*(-0.593549)");
    check("CarlsonRD(-2.0,1.0,0.5)", //
        "0.556253+I*(-1.83381)");
    check("CarlsonRC(-1.0,2.0)", //
        "0.9069+I*(-0.380173)");
    check("CarlsonRG(-1.0,0.5,2.0)", //
        "0.634467+I*0.178195");
    check("CarlsonRJ(0.0,3.0,1.0,-2.5)", //
        "-0.825915");
    check("CarlsonRF(-2.0,1.0,1.0)", //
        "0.9069+I*(-0.661768)");
    check("CarlsonRJ(0.0,3.0,1.0,-2.0)", //
        "-0.939369");
    check("N(CarlsonRF(-2,1,1),30)", //
        "0.906899682117108925297039128821+I*(-0.661768020759984578967052612674)");
    check("N(CarlsonRJ(0,3,1,-2),30)", //
        "-0.939368854404368605502028952652");
    check("N(CarlsonRF(-1,0,1),30)", //
        "1.31102877714605990523241979495+I*(-1.31102877714605990523241979495)");
    check("N(EllipticPi(3,-2),30)", //
        "0.232051229742401253424306927444");
    check("N(EllipticPi(7/4,1/2),30)", //
        "-0.371747869254145652156537442256");
    check("N(EllipticPi(3,4,-2),30)", //
        "1.00814374069003455522062785904");
    check("EllipticPi(3.0,-2.0)", //
        "0.232051");
  }

  @Test
  public void testEllipticPiOnAnotherSheet() {
    // Mathematica: EllipticPi[3, 1.5, 2. + I] = 1.016372454934197 - 2.3366727511877587 I
    check("EllipticPi(3, 1.5, 2.0+I)", //
        "1.01637+I*(-2.33667)");
    check("N(EllipticPi(3, 3/2, 2+I),30)", //
        "1.016372454934199953865727825+I*(-2.33667275118775614297180707886)");
    // Mathematica: EllipticPi[3, 1.5, 2. - I] = 1.0163724549341984 + 2.336672751187762 I
    check("EllipticPi(3, 1.5, 2.0-I)", //
        "1.01637+I*2.33667");
    check("N(EllipticPi(3, 3/2, 2-I),30)", //
        "1.016372454934199953865727825+I*2.33667275118775614297180707886");
    // Mathematica: EllipticPi[3, 4.5, 2. + I] = 1.8123368953332752 - 4.018358493952311 I
    check("EllipticPi(3, 4.5, 2.0+I)", //
        "1.81234+I*(-4.01836)");
    // Mathematica: EllipticPi[3, 1.57, 0.25 + 0.3 I] = 0.009509477541347122 - 1.2598460385030816 I
    check("EllipticPi(3, 1.57, 0.25+0.3*I)", //
        "0.00950948+I*(-1.25985)");
    // Mathematica: EllipticPi[3, 1.2, -1. + 0.5 I] = 0.25113212696998966 + 0.9252595732904365 I
    check("EllipticPi(3, 1.2, -1.0+0.5*I)", //
        "0.251132+I*0.92526");
    check("N(EllipticPi(3, 6/5, -1+I/2),30)", //
        "0.251132126969989078148088282351+I*0.925259573290436819534397299474");
  }

  @Test
  public void testModWithOffset() {
    // m-n*Floor((m-d)/n)
    check("Mod(5.5,2,-1)", //
        "-0.5");
    check("Mod(-5.5,2,1)", //
        "2.5");
    check("Mod(27/10,1/3,1)", //
        "31/30");
    check("Mod(N(27/10,30),N(1/3,30),1)", //
        "1.03333333333333333333333333333");
  }

  @Test
  public void testSquareRootOfInexactNumbers() {
    check("Sqrt(-2.0)", //
        "I*1.41421");
    check("1/Sqrt(-4.0)", //
        "I*(-0.5)");
    check("N(4,30)^(1/2)", //
        "2");
    check("Sqrt(N(-4,30))", //
        "I*2");
  }

  @Test
  public void testIntegerPowersOfInexactNumbers() {
    check("N(10,30)^6", //
        "1000000");
    check("N(2,30)^(10^12)", //
        "9.57624423149274328480505949570*10^301029995663");
    check("N(11/10,30)^(10^6)", //
        "4.84348796603303484786492117176*10^41392");
    check("N(-3,30)^1001", //
        "-3.96621245844241991067136577926*10^477");
  }

  @Test
  public void testNOfLongIntegers() {
    check("N(3^(10^6),20)", //
        "1.7977101166757438380*10^477121");
    check("N(10^(10^6)+1,20)", //
        "1.0000000000000000000*10^1000000");
    check("N(1/3^(10^6),20)", //
        "5.5626320991571288659*10^-477122");
  }

  @Test
  public void testNBeyondTheLimitsOfExactEvaluation() {
    // the engine of the tests does not calculate 999999! as an integer
    check("N(Gamma(10^6),30)", //
        "8.26393168833124006237664610317*10^5565702");
    check("N(Gamma(3^13),30)", //
        "4.88629604479844876483676166312*10^9196501");
  }

  @Test
  public void testDawsonFOfLargeArguments() {
    check("N(DawsonF(10^10),30)", //
        "0.00000000005000000000000000000025");
    check("N(DawsonF(10^7),30)", //
        "0.0000000500000000000002500000000000038");
  }

  @Test
  public void testSquareAndTriangleWave() {
    check("SquareWave(1/3)", //
        "1");
    check("SquareWave(0.7)", //
        "-1");
    check("SquareWave(-0.2)", //
        "-1");
    check("SquareWave({2,5},1/3)", //
        "5");
    check("TriangleWave(1/3)", //
        "2/3");
    check("TriangleWave(3/4)", //
        "-1");
    check("TriangleWave(0.2)", //
        "0.8");
    check("TriangleWave({2,5},1/4)", //
        "5");
    check("N(TriangleWave(37/10),30)", //
        "-0.8");
    check("SquareWave(x)", //
        "SquareWave(x)");
  }

  @Test
  public void testWeierstrassZetaAndSigma() {
    // the reference values satisfy zeta' = -P and sigma'/sigma = zeta
    check("N(WeierstrassZeta(7/10,{2,3}),30)", //
        "1.41348253907655934058931809616");
    check("N(WeierstrassSigma(7/10,{2,3}),30)", //
        "0.698304236929041685925127459507");
    check("N(WeierstrassZeta(3/10+2/5*I,{2,3}),30)", //
        "1.20394747402105432558630737907+I*(-1.60080024992974563227928588762)");
    check("N(WeierstrassSigma(5/2-5/4*I,{1+2*I,3-I}),30)", //
        "6.52677619915189243953185146102+I*(-1.69974921563731253459380117623)");
    check("N(WeierstrassZeta(-25/2+9*I,{2,3}),30)", //
        "-8.42065867827723731281952129555+I*(-3.91057081001207409839974200359)");
    check("N(WeierstrassSigma(121/4,{1,2}),30)", //
        "-3.80080244732552058544136942435*10^101");
    check("WeierstrassZeta(0.7,{2,3})", //
        "1.41348");
    check("WeierstrassSigma(0.7,{2,3})", //
        "0.698304");
    check("WeierstrassZeta(0.3+0.4*I,{2.0,3.0})", //
        "1.20395+I*(-1.6008)");
    check("WeierstrassZeta(0,{2,3})", //
        "ComplexInfinity");
    check("WeierstrassSigma(0,{2,3})", //
        "0");
    check("WeierstrassZeta(u,{2,3})", //
        "WeierstrassZeta(u,{2,3})");
  }

  @Test
  public void testWeierstrassZetaAndSigmaOfEqualRoots() {
    // g2^3 = 27*g3^2: elementary functions
    check("WeierstrassZeta(u,{0,0})", //
        "1/u");
    check("WeierstrassSigma(u,{0,0})", //
        "u");
    check("WeierstrassZeta(u,{3,1})", //
        "u/2+Sqrt(3/2)*Cot(Sqrt(3/2)*u)");
    check("WeierstrassSigma(u,{3,1})", //
        "Sqrt(2/3)*E^(u^2/4)*Sin(Sqrt(3/2)*u)");
    check("WeierstrassZeta(u,{3,-1})", //
        "-u/2+Sqrt(3/2)*Coth(Sqrt(3/2)*u)");
    check("N(WeierstrassZeta(1/2,{3,1}),30)", //
        "1.99351808559941166282358181837");
    check("N(WeierstrassSigma(1/2,{3,-1}),30)", //
        "0.499618567264718888689075819632");
  }

  @Test
  public void testNWithCancellation() {
    // an exact expression is evaluated again with more digits, when a difference cancels them;
    // the result of that is rounded
    check("N(Exp(Pi*Sqrt(163))-262537412640768744,30)", //
        "-0.000000000000749927402801814311120646143663");
    check("N(Cos(10^-20)-1,30)", //
        "-5.0000000000000000000000000000*10^-41");
    check("N(Exp(10^-25)-1,30)", //
        "0.000000000000000000000000100000000000000000000000005");
    check("N(Sqrt(10^40+1)-10^20,30)", //
        "0.000000000000000000005");
    check("N(Gamma(1/10^20)-10^20,30)", //
        "-0.577215664901532860596621530129");
    check("N(Pi-355/113,30)", //
        "-0.000000266764189062422312368932886496");
    check("N(1/(Cos(10^-20)-1),30)", //
        "-2.0000000000000000000000000000*10^40");
    check("N({Cos(10^-20)-1, x+Pi-3},30)", //
        "{-5.0000000000000000000000000000*10^-41,0.14159265358979323846264338328+x}");
  }

  @Test
  public void testNOfPeriodicFunctionsWithLargeArguments() {
    check("N(Sin(10^30+1/3),30)", //
        "-0.411019997625541925949186607518");
    check("N(Exp(I*10^30),30)", //
        "-0.995931194405395702394248587997+I*(-0.090116901912138058030386428953)");
    check("N(Cosh(1+10^25*I),30)", //
        "-1.02969630998980075531204768483+I*(-0.875277919258710323120260913302)");
    check("N(Mod(10^30,Pi),30)", //
        "0.0902393238980530280311815879054");
  }

  @Test
  public void testNAtTheLimitOfExtraPrecision() {
    // 87 more digits would be needed: a message, and the value which 50 more give
    check("N(Tanh(100)-1,30)", //
        "0");
    check("N(Tanh(40)-1,30)", //
        "-3.6097027756908303446242567147*10^-35");
  }

  @Test
  public void testInverseCircularFunctionsNextToOne() {
    check("N(ArcSin(1-10^-30)-Pi/2,30)", //
        "-0.00000000000000141421356237309504880168872421");
    check("N(ArcCos(1-10^-30),30)", //
        "0.00000000000000141421356237309504880168872421");
    check("N(ArcSin(-1+10^-20),30)", //
        "-1.57079632665347526299401218676");
  }

  @Test
  public void testFunctionsOfTinyNumbers() {
    // a number below the tolerance of isZero() is not the zero where the function is singular
    check("Log(1.0*^-16)", //
        "-36.84136");
    check("Gamma(1.0*^-20)", //
        "1.*10^20");
    check("Coth(1.0*^-20)", //
        "1.*10^20");
    check("ExpIntegralEi(1.0*^-20)", //
        "-45.47449");
    check("BesselK(0,1.0*^-20)", //
        "46.16763");
    check("SinIntegral(1.0*^-20)", //
        "1.*10^-20");
    check("PolyLog(2,1.0*^-20)", //
        "1.*10^-20");
    // I*10^-150
    check("Im(Sqrt(-1.0*^-300))*10^150", //
        "1.0");
    check("ArcCot(-1.0*^-300)", //
        "-1.5708");
    check("Log(N(10^-40,30))", //
        "-92.1034037197618273607196581874");
    check("N(Gamma(10^-40),30)", //
        "1.00000000000000000000000000000*10^40");
    check("Log(0.0)", //
        "Indeterminate");
    check("Gamma(0.0)", //
        "ComplexInfinity");
  }

  @Test
  public void testMachineArithmeticBeyondTheRangeOfADouble() {
    // an intermediate result which leaves the range is no Indeterminate or Infinity
    check("Coth(1000.0)", //
        "1.0");
    check("Factorial(1000.0)", //
        "4.023872600770938*10^2567");
    check("BesselI(0,1000.0)", //
        "2.485686096075864*10^432");
    check("ExpIntegralEi(1000.0)", //
        "1.972045137141238*10^431");
    check("ArcSinh(1.0*^300)", //
        "691.4687");
    check("Exp(1000.0)/Exp(999.0)", //
        "2.718281828459045");
    check("1/Exp(999.0)", //
        "1.379788683321370*10^-434");
    check("Floor(1.0*^19)", //
        "10000000000000000000");
    check("Quotient(1.0*^25,7)", //
        "1428571428571428815896576");
    // 2.6344821520881845*10^-291, without the seconds which the general routine takes
    check("AiryAi(100.0)*10^291", //
        "2.63448");
    check("AiryAi(300.0)", //
        "0.0");
    check("DawsonF(1.0*^20)", //
        "5.*10^-21");
    check("LogisticSigmoid(-1.0*^20)", //
        "0.0");
    check("Cosh(1.0*^20)", //
        "Overflow()");
  }

  @Test
  public void testComplexErfAndBesselReflection() {
    check("Erf(1.0+I)", //
        "1.31615+I*0.190453");
    check("Erfc(-5.0+5.0*I)", //
        "1.93038+I*(-0.0389362)");
    // E^(I*Pi*v) times the function of the positive number: 1.507100147*10^-40*I and
    // 1.786910888*10^-42*I
    check("BesselI(100.5,-30.0)*10^40", //
        "I*1.5071");
    check("BesselJ(100.5,-30.0)*10^42", //
        "I*1.78691");
  }

  @Test
  public void testCarlsonRJPrincipalValue() {
    // Mathematica: CarlsonRJ[0.005, -0.99 + 0.995 I, 1., -1.985] =
    // -0.8693848287408495 + 1.7092194456486596 I
    check("CarlsonRJ(0.005, -0.99+0.995*I, 1.0, -1.985)", //
        "-0.869385+I*1.70922");
    // Mathematica: CarlsonRJ[-1. + I, 2., 3. - I, -2.] = -0.1564786272277264 + 0.4356454459377581 I
    check("CarlsonRJ(-1.0+I, 2.0, 3.0-I, -2.0)", //
        "-0.156479+I*0.435645");
    check("N(CarlsonRJ(-1+I, 2, 3-I, -2),30)", //
        "-0.156478627227726450375451145092+I*0.435645445937758305221789403955");
    check("N(CarlsonRJ(0,3,1,-2),30)", //
        "-0.939368854404368605502028952652");
  }

  @Test
  public void testCarlsonAtSingularArguments() {
    // Mathematica: CarlsonRJ[1., 2., 3., -3.] = -0.24909920812969522, the Cauchy principal value
    check("CarlsonRJ(1.0,2.0,3.0,-3.0)", //
        "-0.249099");
    check("N(CarlsonRJ(1,2,3,-3),30)", //
        "-0.249099208129695969278125615806");
    check("{CarlsonRG(x,x,x),CarlsonRG(2,2,2),CarlsonRG(0,0,0),CarlsonRG(1/4,1/4,1/4)}", //
        "{Sqrt(x),Sqrt(2),0,1/2}");
    check("CarlsonRG(2.0,2.0,2.0)", //
        "1.41421");
    check("N(CarlsonRG(2,2,2),30)", //
        "1.41421356237309504880168872421");
    check("CarlsonRG(2.0,2.0,2.0000001)", //
        "1.41421");
    check("{CarlsonRF(0.0,0.0,1.0),CarlsonRF(0,0,1),N(CarlsonRF(0,0,1),30)}", //
        "{ComplexInfinity,ComplexInfinity,ComplexInfinity}");
    check("{CarlsonRD(1.0,2.0,0.0),CarlsonRD(0.0,0.0,1.0),CarlsonRD(0,0,x)}", //
        "{ComplexInfinity,ComplexInfinity,ComplexInfinity}");
    check("{CarlsonRJ(1.0,2.0,3.0,0.0),CarlsonRJ(0.0,0.0,3.0,2.0),CarlsonRJ(x,y,z,0)}", //
        "{ComplexInfinity,ComplexInfinity,ComplexInfinity}");
    check("CarlsonRF(1.*^-20,1.*^-20,1.0)", //
        "23.719");
    check("CarlsonRF(0.0,1.0,1.0)", //
        "1.5708");
    check("CarlsonRD(0.0,2.0,1.0)", //
        "1.79721");
    check("CarlsonRJ(0.0,2.0,3.0,1.0)", //
        "1.17122");
  }

  @Test
  public void testCarlsonOfComplexArguments() {
    check("N(CarlsonRG(-1,3*I,40),30)", //
        "3.16399601857860712141358091198+I*0.304621338233297772179140046275");
    check("CarlsonRG(-1.0,3.0*I,40.0)", //
        "3.164+I*0.304621");
    check("N(CarlsonRG(-I,1/2,-5/2),30)", //
        "-0.0104552077172780222150434518595+I*0.336119806491841304807102601882");
    check("N(CarlsonRG(1/1000,-2+I/2,-I),30)", //
        "-0.0183213593090012853090170217639+I*0.320621583284305583142456869051");
    check("N(CarlsonRC(1/2,-2+I/2),30)", //
        "0.387647022167916942597545041313+I*(-0.916162142381505352222544616543)");
    check("CarlsonRC(0.5,-2.0+0.5*I)", //
        "0.387647+I*(-0.916162)");
    check("N(CarlsonRC(2,-2+I/2),30)", //
        "0.477448140955910509734061295363+I*(-0.711229921980316103874572689302)");
    check("N(CarlsonRC(3*I,3*I),30)", //
        "0.408248290463863016366214012451+I*(-0.408248290463863016366214012451)");
    check("N(CarlsonRC(-I,-I),30)", //
        "0.707106781186547524400844362105+I*0.707106781186547524400844362105");
    check("N(CarlsonRC(1,-2),30)", //
        "0.38017299815047317376554712744");
    check("CarlsonRC(1.0,-2.0)", //
        "0.380173");
    check("{CarlsonRG(0.0,2.0,0.0),CarlsonRG(2.0,0.0,0.0),CarlsonRG(0,2,0),CarlsonRG(0,0,2)}", //
        "{0.707107,0.707107,1/Sqrt(2),1/Sqrt(2)}");
    check("N(CarlsonRG(0,2,0),30)", //
        "0.707106781186547524400844362105");
  }

  /** Only <code>I^(-1/2)</code> is <code>-(-1)^(3/4)</code>: every imaginary number was. */
  @Test
  public void testPowerOfImaginaryNumbers() {
    check("{I^(-1/2),(3*I)^(-1/2),(-I)^(-1/2),1/Sqrt(2*I)}", //
        "{-(-1)^(3/4),1/Sqrt(I*3),1/Sqrt(-I),1/Sqrt(I*2)}");
    check("N((3*I)^(-1/2),30)", //
        "0.408248290463863016366214012451+I*(-0.408248290463863016366214012451)");
    check("N((-I)^(-1/2),30)", //
        "0.707106781186547524400844362105+I*0.707106781186547524400844362105");
    check("N(1/Sqrt(2*I))", //
        "0.5+I*(-0.5)");
    check("N((I/3)^(-1/2))", //
        "1.22474+I*(-1.22474)");
    check("N((-4*I)^(-1/2))", //
        "0.353553+I*0.353553");
    check("Simplify(1/Sqrt(2*I)-(1-I)/2)", //
        "0");
    check("(3*I)^(-1/2)*Sqrt(3*I)", //
        "1");
  }

  @Test
  public void testModularFunctions() {
    check("ModularLambda(0.3+I)", //
        "0.434736+I*0.354315");
    check("N(ModularLambda(1/3+I),30)", //
        "0.414405480384997491318429003209+I*0.399584938884196014642324960988");
    check("N(ModularLambda(-2+I/5),30)", //
        "0.999997588775266382854576892572");
    check("DedekindEta(0.3+I)", //
        "0.767845+I*0.0590611");
    check("N(DedekindEta(1/3+I),30)", //
        "0.767562142473995134656213562591+I*0.0659058210877420180182611147165");
    check("N(DedekindEta(I),30)", //
        "0.768225422326056659002594179576");
    check("N(DedekindEta(-2+I/5),30)", //
        "0.523028659047484041798495271563+I*(-0.301970737094953904637642566458)");
    check("EllipticNomeQ(0.3)", //
        "0.0222774");
    check("N(EllipticNomeQ(1/3),30)", //
        "0.0253199133662851868666269711107");
    check("N(EllipticNomeQ(-2),30)", //
        "-0.0682378277453383330883815168277");
    check("N(EllipticNomeQ(1/4+I/3),30)", //
        "0.0123782491507937213458378924897+I*0.0261468440903113192113428526234");
    check("{ModularLambda(x),DedekindEta(1/2),EllipticNomeQ(m),DedekindEta(0.5)}", //
        "{ModularLambda(x),DedekindEta(1/2),EllipticNomeQ(m),DedekindEta(0.5)}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testRiemannSiegelZ() {
    check("RiemannSiegelZ(2.5)", //
        "-0.526283");
    check("N(RiemannSiegelZ(5/2),30)", //
        "-0.526283003737937610215813902643");
    check("RiemannSiegelZ(-30.5)", //
        "-0.0962212");
    check("N(RiemannSiegelZ(2+I),30)", //
        "-0.470144368030074417186935029963+I*0.034599242557948909654678220608");
    check("RiemannSiegelZ(100000.5)", //
        "4.63404");
    check("N(RiemannSiegelZ(200001/2),30)", //
        "4.63404259499813240205870441043");
    check("RiemannSiegelZ(t)", //
        "RiemannSiegelZ(t)");
  }

  @Test
  public void testParabolicCylinderD() {
    check("ParabolicCylinderD(1/3,2.5)", //
        "0.288894");
    check("N(ParabolicCylinderD(1/3,5/2),30)", //
        "0.288894041930229473108452533511");
    check("ParabolicCylinderD(2,0.5+I)", //
        "-1.74685+I*1.69098");
    check("ParabolicCylinderD(-2.5,-10.0)", //
        "4.30967*10^12");
    check("N(ParabolicCylinderD(5,5/2),30)", //
        "-4.42149019771846969386794801191");
    check("ParabolicCylinderD(1/3,0.0)", //
        "0.742649");
    check("N(ParabolicCylinderD(2+I,-7/4),30)", //
        "2.88490870181488711282150877559+I*2.83379558438984271791541814865");
    check("{ParabolicCylinderD(v,0),ParabolicCylinderD(v,z)}", //
        "{(2^(v/2)*Sqrt(Pi))/Gamma(1/2*(1-v)),ParabolicCylinderD(v,z)}");
  }

  @Test
  public void testScorer() {
    check("ScorerGi(2.5)", //
        "0.138278");
    check("N(ScorerGi(-7/4),30)", //
        "-0.475210340695153185649896205302");
    check("N(ScorerGi(30),30)", //
        "0.0106111160731938282303635345798");
    check("ScorerHi(0.5+I)", //
        "0.349305+I*0.3971");
    check("N(ScorerHi(-30),30)", //
        "0.0106095441701062579222454430168");
    check("ScorerHi(0.0)", //
        "0.409951");
    check("N(ScorerGi(0),30)", //
        "0.204975542482000245050307456365");
    check("{ScorerGi(0),ScorerHi(0),ScorerGi(x)}", //
        "{1/(3*3^(1/6)*Gamma(2/3)),2/(3*3^(1/6)*Gamma(2/3)),ScorerGi(x)}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testCoulomb() {
    check("CoulombF(0,1/3,7.0)", //
        "-0.336986");
    check("N(CoulombF(2,-2,7),30)", //
        "0.907006881603599837927793522678");
    check("CoulombF(1,0.5,2.0+I)", //
        "0.611231+I*0.603936");
    check("CoulombG(0,1/3,7.0)", //
        "0.968058");
    check("N(CoulombG(1/2,-2,1/2),30)", //
        "0.234942048659657340825208834042");
    check("{CoulombG(0,1.0,-2.0),CoulombF(l,eta,r)}", //
        "{CoulombG(0.0,1.0,-2.0),CoulombF(l,eta,r)}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testLerchPhiOutsideOfTheUnitCircle() {
    check("LerchPhi(3.0,2,1/3)", //
        "9.70953+I*(-2.39306)");
    check("N(LerchPhi(3,2,5/2),30)", //
        "-0.113891935915635448735805613698+I*(-0.221406918969568486024779752568)");
    check("LerchPhi(0.99,2,2.5)", //
        "0.458211");
    check("N(LerchPhi(-1,2,5/2),30)", //
        "0.108306821153320504662858504174");
    check("N(LerchPhi(I,1/2,1/3),30)", //
        "1.34384652177394509003106470814+I*0.554354269100931235476166782294");
    check("N(LerchPhi(2+3*I,3/2,1/4+I/2),30)", //
        "0.00281339032938935784495729812967+I*(-1.16663943708747741266579760602)");
    check("LerchPhi(-5.0,2,1/3)", //
        "7.69879");
    check("N(LerchPhi(101/100+I/100,2,5/2),30)", //
        "0.495662339953753939123780113004+I*0.0490440870425871068109818134859");
  }

  @Test
  public void testHypergeometricPFQOnTheUnitCircle() {
    check("HypergeometricPFQ({1/3,5/2,1},{7/3,5/2},1.0)", //
        "1.33333");
    check("N(HypergeometricPFQ({1/3,5/2,1},{1/2+I,4},1),30)", //
        "0.841936943209098418743683938158+I*(-0.262646309904234199439104167075)");
    check("N(HypergeometricPFQ({1/3,5/2,1,1/2},{7/3,3/2,2},1),30)", //
        "1.09711564923078738603669973702");
    check("N(HypergeometricPFQ({1/3,5/2,1},{7/3,5/2},-1),30)", //
        "0.895063595372589475565609225867");
    check("N(HypergeometricPFQ({1/3,5/2,1},{7/3,5/2},I),30)", //
        "0.955995213070746608862017780386+I*0.120729516925978849385060507579");
    check("N(HypergeometricPFQ({1/3,5/2,1},{7/3,5/2},3/5+4/5*I),30)", //
        "1.03504203291249498485693278177+I*0.15781536229600760320232137272");
    check("N(HypergeometricPFQ({1/3,5/2,1},{7/3,5/2},-201/200),30)", //
        "0.894667942269289989823739840327");
    check("{Hypergeometric2F1(1/3,2/3,1,1),Hypergeometric2F1(1/3,2/3,1,1.0),Hypergeometric2F1(1/3,2/3,2,1)}", //
        "{ComplexInfinity,ComplexInfinity,1/(Gamma(4/3)*Gamma(5/3))}");
    check("N(HypergeometricPFQ({1/3,5/2,1},{7/3,3/2},1),30)", //
        "HypergeometricPFQ({1/3,1,5/2},{3/2,7/3},1)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testBesselOfLargeMachineArguments() {
    // the phase of a large argument, with the double as the number it is: 6.6980090407034243*10^-12
    // and -7.9506819824254502*10^-11
    check("BesselJ(0,1.0*^20)*10^12", //
        "6.69801");
    check("BesselY(0,1.0*^20)*10^11", //
        "-7.95068");
    check("BesselJ(2.5,3.0*^12)*10^7", //
        "4.23921");
    check("BesselY(-7.25,4.5*^9)*10^5", //
        "-0.705328");
    check("BesselJ(1,1.0*^300)*10^151", //
        "-1.36814");
    check("HankelH1(3,2.0*^15)*10^8", //
        "0.514285+I*(-1.70839)");
    // an order which is large against the argument: the series with the digits which it cancels
    check("BesselJ(300.5,1000.0)", //
        "0.0156564");
    check("BesselY(300.5,1000.0)", //
        "0.020551");
    check("SphericalBesselJ(2.5,1.0*^100)*10^101", //
        "9.2303");
    check("SphericalBesselY(4,6.0*^18)*10^19", //
        "1.26187");
  }

  @Test
  public void testBesselOfLargeExactArguments() {
    // -7.33704873653861571243692861417547889*10^-51 and 6.38371677504482546122359215350099660*10^-51
    check("N(BesselJ(0,10^100),30)*10^51", //
        "-7.33704873653861571243692861418");
    check("N(BesselY(1/3,10^100),30)*10^51", //
        "6.3837167750448254612235921535");
  }

  @Test
  public void testBesselYOfNegativeHalfIntegerOrders() {
    // BesselY(-n-1/2,x) is (-1)^n*BesselJ(n+1/2,x): the sign of an odd n
    check("BesselY(-7/2,x)", //
        "(Sqrt(2/Pi)*(15*x*Cos(x)-x^3*Cos(x)-15*Sin(x)+6*x^2*Sin(x)))/x^(7/2)");
    // -0.04738639925270620012925540218343902 and -0.006557416351216627175545526368771718
    check("N(BesselY(-7/2,150),30)", //
        "-0.0473863992527062001292554021834");
    check("N(BesselY(-11/2,7/3),30)", //
        "-0.00655741635121662717554552636877");
    check("BesselY(-3.5,150.0)", //
        "-0.0473864");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testStruveHOfLargeArguments() {
    // BesselY and the expansion of the difference: -7.9506819817888303927*10^-11
    check("StruveH(0,1.0*^20)*10^11", //
        "-7.95068");
    check("StruveH(1,5000.5)", //
        "0.638083");
    check("StruveH(-3.5,2.0*^12)*10^7", //
        "-1.42613");
    check("StruveH(0,400.0)", //
        "-0.00758198");
    // beyond the range of a double: Log is 667.23305557102440608
    check("Abs(Log(StruveH(50,3.3*^7))-667.2330555710244)<1.0*^-10", //
        "True");
    // 0.001853132356588103297999701751300985, 2.335883204043466641944892828122949*10^411 and
    // 0.00002899393970232984635857300155683016
    check("N(StruveH(0,10^5),30)", //
        "0.0018531323565881032979997017513");
    check("N(StruveH(50,10^10),30)/10^411", //
        "2.33588320404346664194489282812");
    check("N(StruveH(-7/2,10^8),30)", //
        "0.0000289939397023298463585730015568");
  }

  @Test
  public void testStruveOfHalfOnTheNegativeAxis() {
    // as the other orders and the machine numbers: -0.48861995401644344619*I and
    // 1.1432089853159882074*I
    check("StruveH(1/2,x)", //
        "(Sqrt(2/Pi)*(1-Cos(x)))/Sqrt(x)");
    check("N(StruveH(1/2,-5/4),20)", //
        "I*(-0.48861995401644344619)");
    check("N(StruveL(-1/2,-5/4),20)", //
        "I*1.1432089853159882074");
  }

  @Test
  public void testBetaOfALargeArgument() {
    // 0.0017724540724622612378: the difference of the two logarithms left 9 digits
    check("Abs(Beta(0.5,1.0*^6)/0.001772454072462261-1)<1.0*^-14", //
        "True");
    // 1.7724538509055160273*10^-10, 3.3233508250512422286*10^-28, 3.2850276135970233798*10^-166
    // and 2.3764378931514157739*10^-17
    check("Beta(0.5,1.0*^20)*10^10", //
        "1.77245");
    check("Beta(1.0*^20,0.5)*10^10", //
        "1.77245");
    check("Beta(3.5,1.0*^8)*10^28", //
        "3.32335");
    check("Beta(250.5,300.25)*10^166", //
        "3.28503");
    check("Beta(25.0,30.0)*10^17", //
        "2.37644");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testLerchPhiWithANegativeShift() {
    // the sum of z^k/((k+a)^2)^(s/2), which is real: 2.5798050063195912038
    check("LerchPhi(0.3,2.5,-1.5)", //
        "2.57981");
    check("N(LerchPhi(3/10,5/2,-3/2),20)", //
        "2.5798050063195912038");
    check("LerchPhi(-0.7,1.5,-3.25)", //
        "-2.11493");
    // the sum of z^k/(k+a)^s: 0.51986136217066557510-2.0599436441489256287*I
    check("HurwitzLerchPhi(0.3,2.5,-1.5)", //
        "0.519861+I*(-2.05994)");
    check("N(HurwitzLerchPhi(3/10,5/2,-3/2),20)", //
        "0.5198613621706655751+I*(-2.0599436441489256287)");
    // the same for a positive shift
    check("{LerchPhi(0.3,2.5,1.5),HurwitzLerchPhi(0.3,2.5,1.5)}", //
        "{0.397944,0.397944}");
    // an odd exponent: 3.4248809850225147705 and -1.9677116075700778221
    check("{LerchPhi(0.3,3,-1.5),HurwitzLerchPhi(0.3,3,-1.5)}", //
        "{3.42488,-1.96771}");
    // no sum of ten million terms
    check("LerchPhi(0.3,2.5,-1.0*^7)", //
        "LerchPhi(0.3,2.5,-1.*10^7)");
    check("HurwitzLerchPhi(z,s,a)", //
        "HurwitzLerchPhi(z,s,a)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testAiryOfLargeNegativeArguments() {
    // the phase 2/3*x^(3/2) with all its digits: -5.5541288000569947*10^-3 and
    // -9.91282951914596*10^-4
    check("AiryAi(-1.0*^8)*10^3", //
        "-5.55413");
    check("AiryBi(-1.0*^8)*10^4", //
        "-9.91283");
    check("AiryAiPrime(-3.3*^7)", //
        "5.87282");
    check("AiryBiPrime(-2.5*^11)", //
        "149.0169");
    check("AiryAi(-1.0*^300)*10^76", //
        "-5.3324");
    check("{AiryAi(-12.0),AiryBi(-12.0)}", //
        "{-0.0665552,-0.29572}");
    // -1.31097721453938527842317268*10^-8 and -1.2101394980730367433729338339*10^7
    check("N(AiryBi(-10^30),25)*10^8", //
        "-1.310977214539385278423173");
    check("N(AiryBiPrime(-10^30),25)/10^7", //
        "-1.210139498073036743372933");
    // no digit of the phase is left
    check("AiryAi(-1.0*^15+1.0*I)", //
        "AiryAi(-1.*10^15+I*1.0)");
  }

  @Test
  public void testEllipticKOfAHugeParameter() {
    // Log(4*Sqrt(1-m))/Sqrt(1-m): 1.5707963267948966192-346.77405831022674322*I
    check("EllipticK(1.0*^300)*10^150", //
        "1.5708+I*(-346.7741)");
    check("EllipticK(1.0*^300+I)*10^150", //
        "1.5708+I*346.7741");
    check("EllipticK(-1.0*^300)*10^150", //
        "346.7741");
    check("N(EllipticK(10^300),20)*10^150", //
        "1.5707963267948966192+I*(-346.77405831022674322)");
    check("EllipticK(1.0*^25)*10^12", //
        "0.496729+I*(-9.54015)");
  }

  @Test
  public void testFunctionsNextToZero() {
    // 2/Sqrt(Pi)*x
    check("{Erf(1.0*^-300),Erfi(1.0*^-300)}*10^300", //
        "{1.12838,1.12838}");
    // Zeta(2)*x: 1.6449340192199473*10^-20, and 0.014707822262374456, -0.012405081112505715
    check("HarmonicNumber(1.0*^-20)*10^20", //
        "1.64493");
    check("{HarmonicNumber(0.009),HarmonicNumber(-0.0075)}", //
        "{0.0147078,-0.0124051}");
    // Sin(x/2)^2
    check("Haversine(1.0*^-20)*10^40", //
        "0.25");
    check("Haversine(0.3)", //
        "0.0223318");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testHankelWhereItDecays() {
    // BesselJ and BesselY grow there and their sum cancels: from BesselK
    check("HankelH1(7.0,9.75+23.0*I)*10^11", //
        "-2.49867+I*(-3.07254)");
    check("HankelH2(2.5,14.25-31.5*I)*10^15", //
        "-2.79088+I*(-1.2642)");
    check("HankelH1(0.0,-24.5+54.75*I)*10^25", //
        "0.704674+I*(-1.5645)");
    // in the other half plane
    check("HankelH1(2.5,3.0-2.0*I)", //
        "1.68286+I*(-1.15416)");
    check("HankelH2(1.0,4.0+3.0*I)", //
        "-3.06564+I*(-6.15487)");
    // 2.1080172146695837078102733203242-5.551305724132841373357724558562*I
    check("N(HankelH1(3,5+40*I),30)*10^19", //
        "2.10801721466958370781027332032+I*(-5.55130572413284137335772455856)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testBesselOfComplexMachineArguments() {
    // with guard digits: the series of the library gave -97+23*I for the first
    check("BesselI(100.5,60.0+20.0*I)*10^5", //
        "1.35885+I*0.19731");
    check("BesselJ(20.25,150.0-40.0*I)/10^15", //
        "4.53995+I*(-2.88081)");
    check("BesselK(2.5+0.5*I,25.0+10.0*I)*10^12", //
        "-2.72248+I*2.5944");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testBesselOfLargeOrders() {
    // the expansion of the library cancels nu^2/z digits: 3.453493500089281630815191839362314*10^149
    check("N(BesselI(3001/2,1200),30)/10^149", //
        "3.45349350008928163081519183936");
    check("BesselI(1500.5,1200.0)/10^149", //
        "3.45349");
    // the sum of 301 terms of the closed form cancels: 3.999967716748401964521724619941547*10^-45
    check("N(StruveL(601/2,150),30)*10^45", //
        "3.99996771674840196452172461994");
    check("StruveL(601/2,x)", //
        "StruveL(601/2,x)");
  }

  @Test
  public void testBesselOfHugeExactArguments() {
    // Hankel's expansion with the digits of the phase: -6.510549591714793416365648064807*10^-151,
    // -4.6124528453503425552941463605705*10^-151
    check("N(BesselJ(0,10^300),30)*10^150", //
        "-0.651054959171479341636564806481");
    check("N(BesselY(0,10^300),30)*10^150", //
        "-0.461245284535034255529414636057");
    check("N(HankelH1(1/2,10^300),30)*10^150", //
        "-0.786515045040325195278672422206+I*0.134215708070242555031253347018");
    check("N(StruveH(0,10^300),30)*10^150", //
        "-0.461245284535034255529414636057");
    check("N(BesselJ(5/2,10^16+I),30)*10^8", //
        "-0.959952041050074996893807872755+I*0.587142195207320209936659774335");
  }

  @Test
  public void testHurwitzZetaWithANegativeShift() {
    // the terms with a negative k+a and the function of the shifted argument:
    // 9.868177867807736703, 2.49154238551194-32.8404013863935*I, -62.0125409539019
    check("HurwitzZeta(2,-700.5)", //
        "9.86818");
    check("HurwitzZeta(2.5,-20.25)", //
        "2.49154+I*(-32.8404)");
    check("HurwitzZeta(3,-200.25)", //
        "-62.01254");
    check("HurwitzZeta(1.5+2.0*I,-3.75)", //
        "-106.7694+I*678.8793");
    check("N(HurwitzZeta(2,-1401/2),25)", //
        "9.868177867807736703042023");
    // Zeta sums the squares
    check("Zeta(2.5,-20.25)", //
        "35.33194");
  }

  @Test
  public void testBinomialOfLargeMachineNumbers() {
    // the gamma functions of the quotient leave the range of a double: -2.82094791774*10^-31,
    // 5.64189583548*10^-11, 1.66666666667*10^59 and -3.78770400751*10^-224
    check("Binomial(0.5,1.0*^20)*10^31", //
        "-2.82095");
    check("Binomial(0.5,10^20)*10^31", //
        "-2.82095");
    check("Binomial(-0.5,1.0*^20)*10^11", //
        "5.6419");
    check("Binomial(1.0*^20,3.0)/10^59", //
        "1.66667");
    check("Binomial(10.5,1.0*^20)*10^224", //
        "-3.7877");
    // -72.18153094*10^8 and -9.992306257*10^-5
    check("Binomial(-7.5,101.0)/10^8", //
        "-72.18153");
    check("Binomial(0.5,200.0)", //
        "-0.0000999231");
    check("{Binomial(10.5,3.0),Binomial(30.0,31.0),Binomial(25.0,-3.0)}", //
        "{141.3125,0.0,0.0}");
  }

  @Test
  public void testProductsOfInexactNumbers() {
    // the product of n factors for an arbitrary precision number: 13.125
    check("FactorialPower(N(7/2,20),3)", //
        "13.125");
    check("FactorialPower(3.5,3)", //
        "13.125");
    // h^n*Gamma(x/h+1)/Gamma(x/h+1-n): 18.93225066337866769184155
    check("FactorialPower(N(7/2,20),5/2,1/3)", //
        "18.932250663378667692");
    check("FactorialPower(3.5,2,0.5)", //
        "10.5");
    // the count of the factors may be an inexact integer: 0.5944879869374718685699588
    check("QPochhammer(N(1/3,20),N(1/4,20),5)", //
        "0.59448798693747186857");
    check("N(QPochhammer(1/3,1/4,5),20)", //
        "0.59448798693747186857");
    check("QPochhammer(0.5,0.25,-2)", //
        "0.142857");
  }

  @Test
  public void testPochhammerOfLargeNumbers() {
    // Gamma(n)/Beta(a,n) for machine numbers: 10^60, 1.00000000000000000001875*10^50 and
    // 1.01391138523023
    check("Pochhammer(1.0*^20,3.0)/10^60", //
        "1.0");
    check("Pochhammer(1.0*^20,2.5)/10^50", //
        "1.0");
    check("Abs(Pochhammer(1.0*^6,1.0*^-3)/1.0139113852302306-1)<1.0*^-14", //
        "True");
    // 1/2-10^20 is no integer
    check("Pochhammer(0.5,-1.0*^20)", //
        "0.0");
    check("{Pochhammer(21.0,-21.0),Pochhammer(21.5,-21.5)}", //
        "{ComplexInfinity,ComplexInfinity}");
    // an exact first argument: 26.171388892276760091
    check("Pochhammer(3,2.5)", //
        "26.17139");
    // the logarithms of the gamma functions with the digits of the argument:
    // 1.00000000000000000001875*10^50 and 4.6415888336127788924100763509194*10^-234
    check("N(Pochhammer(10^20,5/2),30)/10^50", //
        "1.00000000000000000001875");
    check("N(Pochhammer(10^100,-7/3),30)*10^234", //
        "4.64158883361277889241007635092");
  }

  @Test
  public void testRationalizeOfLargeIntegers() {
    // a machine number beyond 2^38 stays as it is, with no message: its digits do not
    // determine a fraction
    check("{Rationalize(2.0^38),Rationalize(2.0^38+1),Rationalize(-2.0^38-1)}", //
        "{274877906944,2.74878*10^11,-2.74878*10^11}");
    check("{Rationalize(1.0*^11),Rationalize(1.0*^12),Rationalize(2.0^53-1)}", //
        "{100000000000,1.*10^12,9.0072*10^15}");
    check("Rationalize(1.0*^20)", //
        "1.*10^20");
    check("Rationalize(1.0*^23)", //
        "1.*10^23");
    check("Rationalize(-2.5*^30)", //
        "-2.5*10^30");
    check("Rationalize(2.5*^30+1.0*^23*I)", //
        "2.5*10^30+I*1.*10^23");
    // within a tolerance it is the integer which the number is
    check("Rationalize(1.0*^23,0)", //
        "99999999999999991611392");
    // numbers which have these digits
    check("Rationalize(123456789012345678.0)", //
        "123456789012345678");
    check("Rationalize(N(10^40+7,50))", //
        "10000000000000000000000000000000000000007");
    // 20 digits of a number with 41 of them
    check("Rationalize(N(10^40,20))", //
        "1.0000000000000000000*10^40");
    check("{Rationalize(0.75),Rationalize(1234567.5)}", //
        "{3/4,2469135/2}");
  }

  @Test
  public void testPossibleZeroQOfSmallNumbers() {
    // small, and not 0: 9.36*10^-14, 10^-12, 5*10^-13, -4.25*10^-18, -2.77*10^-87
    check("PossibleZeroQ(E^(-30))", //
        "False");
    check("PossibleZeroQ(Sin(10^(-12)))", //
        "False");
    check("PossibleZeroQ(1-Cos(10^(-6)))", //
        "False");
    check("PossibleZeroQ(Tanh(20)-1)", //
        "False");
    check("PossibleZeroQ(Tanh(100)-1)", //
        "False");
    // -1.93*10^-130
    check("PossibleZeroQ(Tanh(150)-1)", //
        "False");
    check("PossibleZeroQ(Pi*10^(-20))", //
        "False");
    // differences which are 0
    check("PossibleZeroQ((E+Pi)^2-E^2-Pi^2-2*E*Pi)", //
        "True");
    check("PossibleZeroQ(Sqrt(2)*Sqrt(3)-Sqrt(6))", //
        "True");
    check("PossibleZeroQ(Log(2)+Log(3)-Log(6))", //
        "True");
    check("PossibleZeroQ(ArcTan(1/2)+ArcTan(1/3)-Pi/4)", //
        "True");
    check("PossibleZeroQ(E^(I*Pi/4)-(1+I)/Sqrt(2))", //
        "True");
    // no difference in it: what is left of 1 has as many digits fewer as there are more
    check("PossibleZeroQ(Log(Sqrt(2)*Sqrt(3)/Sqrt(6)))", //
        "True");
    // inexact numbers have the digits they have: the tolerance decides for the entries of
    // the product, which are 1 and 0 but for the last bit
    check("OrthogonalMatrixQ({{0.8660254037844386, -0.5}, {0.5, 0.8660254037844386}})", //
        "True");
    // a small entry of a matrix is an entry
    check("MatrixRank({{E^(-30),0},{0,1}})", //
        "2");
    check("NullSpace({{E^(-30),0},{0,1}})", //
        "{}");
    check("RowReduce({{E^(-30),1},{0,1}})", //
        "{{1,0},\n" + " {0,1}}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testStruveOfMachineNumbers() {
    // the machine path: the power series, BesselY plus the Laplace integral of DLMF 11.5.2, and
    // the expansion beyond 100. mpmath: -0.096098421554162110012, 0.34060073858721248729,
    // 1.3734323178701339789, 0.046497175488662673604, -0.015080633438955709018
    check("Max(Abs({StruveH(0.0,30.0)+0.096098421554162110012, "
        + "StruveH(1/3,10.0)-0.34060073858721248729, StruveH(2.5,4.5)-1.3734323178701339789, "
        + "StruveH(10.0,7.0)-0.046497175488662673604, "
        + "StruveH(-0.4,60.0)+0.015080633438955709018})) < 10^-14", //
        "True");
    // the series of positive terms. mpmath: 27.105917126558146553, 5.5925226694181572371*10^24,
    // 0.56816520732123649544, 3.7877259258668686766*10^128
    check("Max(Abs({StruveL(0.0,5.0)/27.105917126558146553, "
        + "StruveL(2.5,60.0)/(5.5925226694181572371*10^24), "
        + "StruveL(-1.2,0.5)/0.56816520732123649544, "
        + "StruveL(10.0,300.0)/(3.7877259258668686766*10^128)}-1)) < 10^-13", //
        "True");
    // orders below -1/2 by the recurrence in the order; a half-integer order is
    // (-1)^n*BesselJ(n+1/2,x). mpmath: -0.12499292073056974632, 0.0018347151945681310826,
    // -0.0076139960074709105531, 619639.28349723197988, 0.4298944397974863326,
    // BesselJ(5/2,1/2) = 0.0092364078193797244999
    check("Max(Abs({StruveH(-0.75,30.0)+0.12499292073056974632, "
        + "StruveH(-3.7,10.0)-0.0018347151945681310826, "
        + "StruveH(-10.3,30.0)+0.0076139960074709105531, "
        + "StruveH(-1.2,0.5)-0.4298944397974863326, "
        + "StruveH(-2.5,0.5)-0.0092364078193797244999})) < 10^-14", //
        "True");
    // 21 steps of the recurrence
    check("Abs(StruveH(-20.6,7.0)/619639.28349723197988-1) < 10^-13", //
        "True");
    // 1.4243375227689502 by mpmath; with the arbitrary precision series for each value of the
    // integrand this took 110 seconds
    check("NIntegrate(x^(-3/4)*StruveH(0, 2*x), {x,0,200})", //
        "1.42434");
  }

  @Test
  public void testZetaNextToZeroAndBinomialOfANegativeIndex() {
    // -1/2-Log(2*Pi)/2*s, where the series do not reach
    check("{Zeta(-1.0*^-300),Zeta(-1.0*^-20),Zeta(1.0*^-20),Zeta(-1.0*^-12)}", //
        "{-0.5,-0.5,-0.5,-0.5}");
    check("N(Zeta(-10^-40),30)", //
        "-0.5");
    // 1.5207242711004071*10^-5, -0.002159137935754174, -2.0327105737275044*10^-18: the lower
    // index below -1 by Binomial(n, n-k); 1.17*10^-673 is below the machine numbers
    check("{Binomial(0.5,-700.5),Binomial(0.5,-25.5),Binomial(30.5,-25.5),Binomial(2000.0,-700.5)}", //
        "{0.0000152072,-0.00215914,-2.03271*10^-18,0.0}");
  }

  @Test
  public void testPowersOfOverflow() {
    check("{Overflow()^2,Overflow()^(1/2),Overflow()^(-3),Overflow()^2.5}", //
        "{Overflow(),Overflow(),Underflow(),Overflow()}");
    check("{Underflow()^2,Underflow()^(-1),Underflow()^(-1/2)}", //
        "{Underflow(),Overflow(),Overflow()}");
    check("Overflow()^x", //
        "Overflow()^x");
    // 10^(10^21): the square of a number which is out of range already
    check("Factorial2(1.0*^20)", //
        "Overflow()");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testZetaAndBernoulliOfLargeArguments() {
    // the Dirichlet series: the routine of the library is 0 from 10^20 on
    check("Zeta(1.0*^20)", //
        "1.0");
    // 1.5282259408651872*10^-5 and (-8.82819551727+2.9662668026*I)*10^-10
    check("(Zeta(16.0)-1)*10^5", //
        "1.52823");
    check("(Zeta(30.0+5.0*I)-1)*10^10", //
        "-8.82819+I*2.96627");
    check("N(Zeta(10^20),30)", //
        "1");
    // 1.70465235973685338678416407976*10^-13+8.9337689784216794531042490981352*10^-13*I
    check("N(Zeta(40+10^5*I)-1,30)*10^13", //
        "1.70465235973685338678416407976+I*8.93376897842167945310424909814");
    // 2*n!*Zeta(n)/(2*Pi)^n: -5.3187044694155220364829137437671*10^1769, and the logarithm of
    // -5.82...*10^376755 is 867512.20840536787671852157815711
    check("N(BernoulliB(1000),30)/10^1769", //
        "-5.31870446941552203648291374377");
    check("N(Log(-BernoulliB(100000)),30)", //
        "867512.208405367876718521578157");
    check("BernoulliB(100.0)/10^78", //
        "-2.83822");
  }

  @Test
  public void testExtraPrecisionForExactFunctions() {
    // the exact evaluation which comes first is exact: no number of the precision in it
    // 8.673617380119933728342055067343*10^-19 and 7.8886090522101180735205378276608*10^-31
    check("N(Zeta(60)-1,30)*10^19", //
        "8.67361738011993372834205506734");
    check("N(Zeta(100)-1,30)*10^31", //
        "7.88860905221011807352053782766");
    // -6.1733400075059184119830549542739*10^-26 and 8.3905259853041068767653755421821*10^-25
    check("N(EllipticK(1/3)-EllipticK(1/3+10^-25),30)*10^26", //
        "-6.17334000750591841198305495427");
    check("N(Gamma(1/3)-Gamma(1/3+10^-25),30)*10^25", //
        "8.39052598530410687676537554218");
  }

  @Test
  public void testIntegralsOfLargeArguments() {
    // f(x)*Sin(x)-g(x)*Cos(x) with the sine of the argument itself: -4.87506025175*10^-11 and
    // -3.80637731005*10^-101
    check("CosIntegral(1.0*^10)*10^11", //
        "-4.87506");
    check("CosIntegral(1.0*^100)*10^101", //
        "-3.80638");
    check("Abs(SinIntegral(1.0*^10)-Pi/2+8.73119622628*^-11)<1.0*^-15", //
        "True");
    // the phase Pi*x^2/2 from the exact square: -3.18309886184*10^-11 and -3.85202218648*10^-7
    check("(FresnelS(1.0*^10)-0.5)*10^11", //
        "-3.1831");
    check("(FresnelC(-316228.5)+0.5)*10^7", //
        "-3.85202");
    // ExpIntegralEi(Log(x)): 37607950280.804865
    check("Abs(LogIntegral(1.0*^12)/37607950280.804865-1)<1.0*^-14", //
        "True");
    // 4505170185988091368013.875996972
    check("LogGamma(1.0*^20)/10^21", //
        "4.50517");
    check("N(LogGamma(10^20),30)/10^21", //
        "4.50517018598809136801387599697");
    check("AngerJ(1/3,1.0*^20)*10^11", //
        "-3.39528");
    check("Gamma(1.0*^20)", //
        "Overflow()");
  }

  @Test
  public void testHypergeometric0F1OfLargeNegativeArguments() {
    // the Bessel function with the phase of 2*Sqrt(-x): -4.45078179223*10^-16 and
    // 4.99344314704*10^-51
    check("Hypergeometric0F1(1.5,-1.0*^30)*10^16", //
        "-4.45078");
    check("Hypergeometric0F1(1.5,-1.0*^100)*10^51", //
        "4.99344");
    // -11.7939156282*10^-18 and 2.27332452253*10^15
    check("Hypergeometric0F1(3.25,-2.5*^12)*10^18", //
        "-11.79392");
    check("Hypergeometric0F1(-2.75,-4.0*^9)/10^15", //
        "2.27332");
    check("{Hypergeometric0F1(1.5,-3000.0),Hypergeometric0F1(1.5,-2000.0)}", //
        "{0.0036491,0.0111324}");
  }
}
