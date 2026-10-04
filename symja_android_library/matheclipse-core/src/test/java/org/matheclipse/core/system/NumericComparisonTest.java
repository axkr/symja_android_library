package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * The numeric comparison rules of <code>Less</code>, <code>Greater</code>, <code>Equal</code> and
 * the sign-type built-ins for github #1530: a relative tolerance of 7 bits for inexact numbers,
 * zero equal to zero only, the lower precision governs, and exact operands are compared with as
 * much precision as needed.
 */
public class NumericComparisonTest extends ExprEvaluatorTestCase {

  private static final String MUNFL =
      " is too small to represent as a normalized machine number; precision may be lost.\n";

  /** Evaluate <code>input</code> and return what was printed as messages. */
  private String messagesOf(String input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    PrintStream old = evaluator.getEvalEngine().getErrorPrintStream();
    evaluator.getEvalEngine().setErrorPrintStream(new PrintStream(buffer, true));
    try {
      evaluator.eval(input);
    } finally {
      evaluator.getEvalEngine().setErrorPrintStream(old);
    }
    return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
  }

  /** github #1530: <code>Min(Pi^1000,10^400)</code> returned the larger value. */
  @Test
  public void testIssue1530() {
    // github #1530: both values are beyond the double range
    check("Min(Pi^1000,7^450)==7^450", //
        "True");
    check("Min(7^450,Pi^1000)==7^450", //
        "True");
    check("Max(Pi^1000,7^450)", //
        "Pi^1000");
    check("Min(E^1000,7^450)==7^450", //
        "True");
    check("{7^450<Pi^1000,Pi^1000>7^450,7^450>Pi^1000,Pi^1000<7^450}", //
        "{True,True,False,False}");
    check("{7^450<=Pi^1000,Pi^1000>=7^450,7^450>=Pi^1000,Pi^1000<=7^450}", //
        "{True,True,False,False}");
    check("{7^450<N(Pi^1000,30),N(Pi^1000,30)<7^450,7^450+1/3<N(Pi^1000,30)}", //
        "{True,False,True}");

    // N underflows to 0.0 (General::munfl), a comparison still sees the value
    check("N(1/7^450)", //
        "0.0");
    check("N(Pi^(-1000))", //
        "0.0");
    // the underflow is reported as General::munfl, except inside a comparison
    check("{Check(N(1/7^450),m), Check(N(Pi^(-1000)),m), Check(7.^-450,m), Check(Exp(-1000.),m)}", //
        "{m,m,m,m}");
    check("{Check(Pi^(-1000)<1,m), Check(Pi^(-1000)==1/7^450,m), Check(2.^-10,m)}", //
        "{True,False,0.000976562}");
    // a small normalized machine number is no underflow
    check("Check(N(1/7^300),m)=!=m", //
        "True");
    check("{1/7^450<Pi^(-1000),Pi^(-1000)<1/7^450,1/7^450>0.0,1/7^450<N(1/7^449,30)}", //
        "{False,True,True,True}");

    // exact expressions whose machine value underflows are neither equal nor zero
    check("{Pi^(-1000)==1/7^450,1/7^450!=Pi^(-1000),Pi^(-1000)==0,Pi^(-1000)==1/Pi^1000}", //
        "{False,True,False,True}");
    check("{Pi^(-1000)<E^(-1000),E^(-1000)<Pi^(-1000),E^(-1000)==Pi^(-1000),Pi^(-1000)>0}", //
        "{True,False,False,True}");
    check("Min(Pi^(-1000),E^(-1000))", //
        "1/Pi^1000");
    // machine arithmetic keeps the underflow to 0.0
    check("{7.^-450==0,Sqrt(2)*Sqrt(3)-Sqrt(6)==0,Sqrt(2)*Sqrt(3)-Sqrt(6)<0}", //
        "{True,True,False}");
    // an arbitrary precision number next to an exact one
    check("{N(1+10^-30,50)>1,N(1-10^-30,50)<1,0<N(7^-450,50),N(2/3,20)<=2/3,N(2/3,20)==2/3}", //
        "{True,True,True,True,True}");
    check("Sort({N(7^451,20), Infinity, 7^452, 2.5, N(7^450,20)})[[{1,5}]]", //
        "{2.5,Infinity}");
  }

  @Test
  public void testMachineTolerance() {
    // two machine numbers are equal up to 2^-46 relative; columns Less, Equal, GreaterEqual
    check("Table(Boole({1.<1.+j*2^-52, 1.==1.+j*2^-52, 1.>=1.+j*2^-52}), {j,{1,64,65,200}})", //
        "{{0,1,1},{0,1,1},{1,0,0},{1,0,0}}");
    // the largest gap in units of the last place which is still equal: the smaller of the two
    // numbers sets the scale
    check("{LengthWhile(Range(200), 1.==1.+#*2.^-52&), LengthWhile(Range(200), 1.==1.-#*2.^-53&), " //
        + "LengthWhile(Range(200), 1.5==1.5+#*2.^-52&), LengthWhile(Range(200), -1.75==-1.75-#*2.^-52&)}", //
        "{64,127,96,112}");
    // a==b and a>b never both hold
    check("{0.1+0.2==0.3, 0.1+0.2>0.3, 0.1+0.2>=0.3, 0.1+0.2<0.3, 0.1+0.2<=0.3, 0.1+0.2!=0.3}", //
        "{True,False,True,False,True,False}");
    // but the difference is a number of its own
    check("{0.1+0.2-0.3>0, Sign(0.1+0.2-0.3), 0.1+0.2-0.3==0}", //
        "{True,1,False}");
  }

  @Test
  public void testZeroIsEqualToZeroOnly() {
    check("{3.*^-20==5.*^-20, 3.*^-300==5.*^-300, 7^-20==0., 3.*^-20==0, 0.==0}", //
        "{False,False,False,False,True}");
    check("{Pi^(-80)==E^(-80), Pi^(-80)<E^(-80), N(E^(-80))==Pi^(-80)}", //
        "{False,True,False}");
    check("{Sign(3.*^-20), Positive(3.*^-20), NonPositive(3.*^-20), NonNegative(-3.*^-20)}", //
        "{1,True,False,False}");
    check("{NonPositive(0.), NonPositive(-3.*^-20), NonPositive(0), NonPositive(1/7)}", //
        "{True,True,True,False}");
  }

  @Test
  public void testComplexTolerance() {
    // real and imaginary parts are compared separately, each with the tolerance of real numbers
    check("{1.+2.^-48*I==1., 2.+3.*I==2.+3.*I*(1+2.^-48), 1.+2.^-40*I==1., 2.+3.*I==2.+3.0001*I}", //
        "{False,True,False,False}");
    // zero is equal to zero only
    check("{3.*^-20*I==5.*^-20*I, 3.*^-20*I==0, 0.*I==0, 3.*^-20+5.*^-20*I==3.*^-20+5.*^-20*I}", //
        "{False,False,True,True}");
    check("{1.*^20+1.*I==1.*^20, 2.5+1.5*I==5/2+3/2*I, 2.5+1.5*I!=5/2+3/2*I, I==Sqrt(-1)}", //
        "{False,True,False,True}");
    // the machine number is computed first: inside one input N(_,30) raises the precision of N(_)
    check("Block({z=N(I/7)}, N(I/7,30)==z)", //
        "True");
    check("N(I/7,30)==N(I/7,30)+10^-20*I", //
        "False");
  }

  @Test
  public void testLowerPrecisionGoverns() {
    // one comparison per input: an arbitrary precision number in a list would raise the precision
    // of its machine neighbours
    check("N(1/7,30)>N(1/7)", //
        "False");
    check("N(1/7,30)==N(1/7)", //
        "True");
    check("N(1/7,30)<=N(1/7)", //
        "True");
    check("N(1/7,20)<N(1/7,30)", //
        "False");
    check("N(1/7,20)==N(1/7,30)", //
        "True");
    // an exact operand is taken at the precision of the inexact one
    check("N(E,30)>E", //
        "False");
    check("N(E,30)==E", //
        "True");
    check("N(E)<E", //
        "False");
    check("N(1/7,20)<1/7", //
        "False");
    check("N(1/7,20)==1/7", //
        "True");
    // rounding noise in the last digit is no difference
    check("N(Sqrt(3),30)^2<3", //
        "False");
    check("N(Sqrt(3),30)^2==3", //
        "True");
    // a real difference is one
    check("N(1+10^-25,30)>1", //
        "True");
  }

  @Test
  public void testExactOperandNextToAMachineNumber() {
    check("{0.7<7/10, 0.7>=7/10, 0.7==7/10, 1/7>N(1/7), Pi>N(Pi), 0.1+0.6>7/10}", //
        "{False,True,True,False,False,False}");
    // 2^53+2^k against 2.^53: equal within 7 bits
    check("{Select(Range(0,10), 2^53+2^#>2.^53&), Select(Range(0,10), 2^53+2^#==2.^53&)}", //
        "{{8,9,10},{0,1,2,3,4,5,6,7}}");
  }

  @Test
  public void testComparisonAfterNWithoutTolerance() {
    // IntervalMemberQ and Clip take the exact bound at machine precision, but have no tolerance:
    // 2^53+1 has the machine value 2.^53, 2^53+4 is larger
    check(
        "{IntervalMemberQ(Interval({7/10,1}),0.7), IntervalMemberQ(Interval({2^53+1,2^54}),2.^53), " //
            + "IntervalMemberQ(Interval({2^53+4,2^54}),2.^53)}", //
        "{True,True,False}");
    check(
        "{Clip(0.7,{7/10,1}), Clip(0.2,{7/10,1}), Clip(2^53+1,{0,2.^53}), Clip(2^53+4,{0,2.^53},{lo,hi})}", //
        "{0.7,7/10,9007199254740993,hi}");
    // Between is the tolerant <=
    check("{Between(0.7,{7/10,1}), Between(2.^53,{2^53+4,2^54})}", //
        "{True,True}");
    // Min and Max return the smaller and the larger one of two nearly equal numbers
    check(
        "{Min(1.,1.+2.^-50)-1., Max(1.,1.+2.^-50)-1.>0, Min(1.+2.^-50,1.)-1., Max(1.+2.^-50,1.)-1.>0}", //
        "{0.0,True,0.0,True}");
  }

  @Test
  public void testExactOperandsBeyondTheMachineRange() {
    check("{Pi^(-900)>0, Pi^(-900)==0, Pi^(-900)<E^(-900), Pi^(-900)==E^(-900)}", //
        "{True,False,True,False}");
    // an intermediate value underflows, the result is an ordinary number
    check("{E^(-900)*Pi^900>1, E^(-900)*Pi^900==0, E^(-900)*Pi^900<10^57}", //
        "{True,False,True}");
    check("{Min(Pi^(-900),E^(-900)), Max(Pi^(-900),0), Min(E^(-900)*Pi^900,1), Min(Pi^900,E^1030)}", //
        "{1/Pi^900,1/Pi^900,1,E^1030}");
    // against a machine number
    check("{Pi^(-900)>0., 1/7^400>0., 1/7^400==0., 1/7^400<=0.}", //
        "{True,True,False,False}");
    check("{Pi^900>1.*^300, Pi^900>N(10^400), Pi^900<Infinity}", //
        "{True,True,True}");
    // operands of very different size are compared without building their exact difference
    check("{E^(10^7)<3, E^(10^7)>Pi^900, 3<E^(10^7), E^(-10^7)<3, E^(10^7)==E^(10^7+1)}", //
        "{False,True,True,True,False}");
  }

  @Test
  public void testSignOfExactOperandsBeyondTheMachineRange() {
    check("{Sign(Pi^(-900)), Positive(Pi^(-900)), Negative(-Pi^(-900)), NonNegative(-Pi^(-900))}", //
        "{1,True,True,False}");
    check("{UnitStep(-Pi^(-900)), HeavisideTheta(-Pi^(-900)), Abs(-Pi^(-900))}", //
        "{0,0,1/Pi^900}");
    check("{Sign(E^(-900)*Pi^900-1), Positive(E^(-900)*Pi^900-1), Abs(1-E^(-900)*Pi^900)}", //
        "{1,True,-1+Pi^900/E^900}");
    check("{Floor(-Pi^(-900)), Ceiling(Pi^(-900)), Sign(1/7^400), -Pi^(-900)<0}", //
        "{-1,1,1,True}");
  }

  @Test
  public void testCanonicalOrderAndSelection() {
    // numbers of the same value are ordered integer, machine number, bignum, fraction
    check("{Order(1,1.), Order(1.,1), Order(0.7,7/10), Order(7/10,0.7), Order(2.^53,2^53+1)}", //
        "{1,-1,1,-1,-1}");
    check("{Ordering({1.+2^-52,1.,1}), OrderedQ({1.,1}), Order(1.,1.+2^-52)}", //
        "{{3,2,1},False,1}");
    // Min and Max return the first and the last one of them
    check("{Max(0.7,7/10), Max(7/10,0.7), Min(0.7,7/10), Min(7/10,0.7)} // InputForm", //
        "{7/10,7/10,0.7`,0.7`}");
    check("{Max(1,1.), Max(1.,1), Min(1,1.), Min(1.,1)} // InputForm", //
        "{1.0`,1.0`,1,1}");
    check("{0.==1.*^-320, 1.*^-320==2.*^-320}", //
        "{False,False}");
  }

  @Test
  public void testDifferenceOfExactOperands() {
    // the difference is simpler than its operands
    check("{Pi^900==Pi^900+1, Pi^900<Pi^900+1, 1+Pi^(-900)>1, 1+Pi^(-900)==1}", //
        "{False,True,True,False}");
    // gaps down to 10^-60 are resolved
    check("{Sqrt(1+Pi^(-40))>1, Sqrt(1+Pi^(-95))>1, Sqrt(1+Pi^(-120))>1}", //
        "{True,True,True}");
    check("{Floor(1-Pi^(-900)), Ceiling(1+Pi^(-900)), Floor(1+Pi^(-900)), Ceiling(1-Pi^(-900))}", //
        "{0,2,1,1}");
  }

  @Test
  public void testSameQTolerance() {
    // SameQ ignores the last bit of two inexact numbers
    check("{1.===1.+2.^-52, 1.===1.+2.^-51, 1.===1.-2.^-53, 0.1+0.2===0.3, 0.===1.*^-320, 1===1.}", //
        "{True,False,True,True,False,False}");
    check("{1.=!=1.+2.^-52, 1.=!=1.+2.^-51, UnsameQ(1.,2.,1.+2.^-52)}", //
        "{False,True,False}");
    check("Block({z=N(1/7)}, N(1/7,30)===z)", //
        "True");
    check("N(1/7,20)===N(1/7,30)", //
        "True");
    check("N(1,30)===N(1+10^-25,30)", //
        "False");
  }

  @Test
  public void testNumerics3() {
    // of a number and an exact expression of the same value Min returns the expression, Max the
    // number
    check(
        "{Max(N(E),E), Min(N(E),E), Max(N(Sqrt(3)),Sqrt(3)), Min(Sqrt(3),N(Sqrt(3)))} // InputForm", //
        "{2.718281828459045`,E,1.7320508075688772`,Sqrt(3)}");
    check(
        "{Max(2^53+1,2.^53), Min(2^53+1,2.^53), Max(2.^53,2^53+4), Min(2^53+4,2.^53)} // InputForm", //
        "{9.007199254740992`*^15,9007199254740993,9007199254740996,9.007199254740992`*^15}");
    check(
        "{Sort({1.,1}), Sort({1,1.}), Union({0.7,7/10,0.7}), NumericalOrder(0.7,7/10)} // InputForm", //
        "{{1,1.0`},{1,1.0`},{0.7`,7/10},0}");
    // a value below the machine range is no zero
    check(
        "{PossibleZeroQ(Pi^(-900)), E^(-900+I)==0, E^(-900+I)==E^(-900-I), E^(-900+I)==E^(-900+I)}", //
        "{False,False,False,True}");
    // a number beyond the double range times a machine number is a machine number again
    check("{N(10^400)*1.*^-300, N(10^400)*0., Precision(N(10^400)+1.)} // InputForm", //
        "{1.0`*^100,0.0`,16}");
  }

  @Test
  public void testIteratorElementsComeFromStartAndStep() {
    // the upper limit only bounds the elements
    check("{Range(7/10,0.7), Range(0,0.7,1/5), Range(1,3.5), Range(3.5)} // InputForm", //
        "{{7/10},{0,1/5,2/5,3/5},{1,2,3},{1,2,3}}");
    check(
        "{Table(x,{x,7/10,0.7}), Table(x,{x,0,0.7,1/5}), Table(x,{x,1,3.5}), Table(x,{x,3.5})} // InputForm", //
        "{{7/10},{0,1/5,2/5,3/5},{1,2,3},{1,2,3}}");
    // an inexact start or step makes them machine numbers
    check(
        "{Range(0,3/5,0.2), Range(0.,3/5,1/5), Table(x,{x,0,2/5,0.2}), Table(x,{x,1.,3})} // InputForm", //
        "{{0.0`,0.2`,0.4`,0.6`},{0.0`,0.2`,0.4`,0.6`},{0.0`,0.2`,0.4`},{1.0`,2.0`,3.0`}}");
    // the elements are min + k*step, not a running sum
    check("Range(0,1,0.1) // InputForm", //
        "{0.0`,0.1`,0.2`,0.30000000000000004`,0.4`,0.5`,0.6000000000000001`,0.7000000000000001`,0.8`,0.9`,1.0`}");
    check("Table(x,{x,0,1,0.1})==Range(0,1,0.1)", //
        "True");
    // a Range which reaches its upper limit ends in that limit, a Table in min+k*step
    check("{Range(0,7/10,0.1), Range(0.,7/10,1/10), Range(0,0.7,0.1)} // InputForm", //
        "{{0.0`,0.1`,0.2`,0.30000000000000004`,0.4`,0.5`,0.6000000000000001`,0.7`},"
            + "{0.0`,0.1`,0.2`,0.30000000000000004`,0.4`,0.5`,0.6000000000000001`,0.7`},"
            + "{0.0`,0.1`,0.2`,0.30000000000000004`,0.4`,0.5`,0.6000000000000001`,0.7`}}");
    check("Last(Table(x,{x,0,7/10,0.1})) // InputForm", //
        "0.7000000000000001`");
  }

  @Test
  public void testFourthProbe() {
    // a literal beyond the double range is an arbitrary precision number of machine precision
    check("{MachineNumberQ(2.5*^400), 2.5*^400==25*10^399, 2.5*^400>10^400, MachineNumberQ(2.5*^300)}", //
        "{False,True,True,True}");
    // such a number has the tolerance of a machine number
    check("{Select(Range(43,48), 7^500*(1+2^-#)>N(7^500)&), Select(Range(43,48), 7^500*(1+2^-#)==N(7^500)&)}", //
        "{{43,44,45},{46,47,48}}");
    // every machine number is smaller than a number beyond the double range
    check("{$MaxMachineNumber<2^1024, $MaxMachineNumber==2^1024, N(2^1024)>$MaxMachineNumber}", //
        "{True,False,True}");
    // equal values of different precision: the less precise one first
    check("{Order(N(1/7,20),N(1/7,30)), Order(N(1,30),1), Ordering({N(1/7,30),N(1/7),1/7})}", //
        "{1,-1,{2,1,3}}");
    check("NumericalOrder(0.1+0.2,0.3)", //
        "-1");
    check("{NumericalOrder(N(E),E), NumericalOrder(1,1.), NumericalOrder(1.,1.+2^-52)}", //
        "{0,0,1}");
    // a fraction in the subnormal range
    check("{N(1/7^365)==0., N(1/7^365)>3.*^-309, N(1/7^365)<4.*^-309}", //
        "{False,True,True}");
    check("Check(N(1/7^365),m)", //
        "m");
  }

  @Test
  public void testIteratorEndTest() {
    // an inexact upper limit may be missed by a few units in the last place only
    check("{Select(Range(46,53), Length(Range(0,1.-2.^-#,1/10))==11&), " //
        + "Select(Range(46,53), Length(Table(x,{x,0,1.-2.^-#,1/10}))==11&)}", //
        "{{51,52,53},{51,52,53}}");
    check("{Length(Range(0,100,0.1)), Length(Range(1,0,-0.1)), Length(Table(x,{x,0,100,0.1}))}", //
        "{1001,11,1001}");
    check("{Range(0,0.3,0.1), Range(-0.3,0,0.1), Range(0.,1,1/3)} // InputForm", //
        "{{0.0`,0.1`,0.2`,0.3`},{-0.3`,-0.19999999999999998`,-0.09999999999999998`,0.0`},{0.0`,0.3333333333333333`,0.6666666666666666`,1.0`}}");
  }

  @Test
  public void testFifthProbe() {
    // identical, SameQ and Equal are three different tests
    check("{Length(Union({1.,1.+2.^-52})), Length(DeleteDuplicates({1.,1.+2.^-52})), Length(Tally({1.,1.+2.^-52})), " //
        + "MemberQ({1.},1.+2.^-52), MatchQ(1.+2.^-52,1.)}", //
        "{2,1,1,False,False}");
    check("{Length(Union({0.7,7/10,0.1+0.6},SameTest->Equal)), Length(DeleteDuplicates({1,1.,2})), Union({1,1.})} // InputForm", //
        "{1,3,{1,1.0`}}");
    check("Tally({0.3,3/10,0.1+0.2}) // InputForm", //
        "{{0.3`,2},{3/10,1}}");
    // a product with a small machine number is no exact zero
    check("{$MinMachineNumber/2==0, $MinMachineNumber/2>0, Block({x=3.*^-20}, x/2) // InputForm}", //
        "{False,True,1.5`*^-20}");
    // Floor(x, a) is a*Quotient(x, a)
    check("{Floor(0.7,1/10), Floor(0.7,0.1), Quotient(0.7,0.1), Floor(7/10,1/10)} // InputForm", //
        "{3/5,0.6000000000000001`,6,7/10}");
    // the exact value decides next to a half
    check("{Round(1/2+Pi^(-900)), Round(1/2-Pi^(-900)), Round(3/2+Pi^(-900)), Round(5/2)}", //
        "{1,0,2,2}");
  }

  @Test
  public void testIteratorWithComputedLimits() {
    check("{Table(x,{x,0,1,1/3.}), Range(0,1,1/3.), Table(x,{x,-0.3,0,0.1}), Range(1,0,-0.25)} // InputForm", //
        "{{0.0`,0.3333333333333333`,0.6666666666666666`,1.0`},{0.0`,0.3333333333333333`,0.6666666666666666`,1.0`},"
            + "{-0.3`,-0.19999999999999998`,-0.09999999999999998`,0.0`},{1.0`,0.75`,0.5`,0.25`,0.0`}}");
    check("{Select(Range(48,53), Length(Range(0,1.-2.^-#,0.1))==11&), Select(Range(48,53), Length(Range(0.,1-2^-#,1/10))==11&)}", //
        "{{52,53},{52,53}}");
  }

  @Test
  public void testSixthProbe() {
    // set operations need identical real numbers, DuplicateFreeQ tests with SameQ
    check("{Complement({1.,2.},{1.+2.^-52}), Intersection({1.,2.},{1.+2.^-52}), SubsetQ({1.,2.},{1.+2.^-52}), " //
        + "ContainsAll({1.,2.},{1.+2.^-52}), DuplicateFreeQ({1.,1.+2.^-52}), DuplicateFreeQ({1,1.})} // InputForm", //
        "{{1.0`,2.0`},{},False,False,False,True}");
    // the remainder of two machine numbers is exact
    check("{Mod(0.7,0.1), Mod(0.3,0.1), Mod(1.,0.1), Mod(5.5,1/2), Mod(-0.7,0.1)>0, Quotient(1.,0.1)} // InputForm", //
        "{0.09999999999999992`,0.09999999999999998`,0.09999999999999995`,0.0`,True,10}");
    // Round(x, k) divides the machine numbers
    check("{Round(0.35,0.1), Round(0.25,0.1), Round(0.5), Round(1.5), Round(2.5), Round(-0.5)} // InputForm", //
        "{0.30000000000000004`,0.2`,0,2,2,0}");
    // Sum and Do use the end test of Table
    check("{Sum(1,{x,0,1,0.1}), Sum(x,{x,0,0.3,1/10}), Sum(x,{x,1,3.5}), Sum(1,{x,0,1.-2.^-51,0.1}), " //
        + "Length(Range(0,1.-2.^-51,0.1))}", //
        "{11,3/5,6,11,10}");
    // an exact complex number sorts before the machine number of the same value
    check("{Order(1.+I,1+I), Order(1+I,1.+I), Sort({1.+I,1+I,1-I,1.-I})} // InputForm", //
        "{-1,1,{1 - I,1 + I,1.0`+I*(-1.0`),1.0`+I*1.0`}}");
  }

  @Test
  public void testComplexBeyondTheMachineRange() {
    // a part beyond the double range is no ComplexInfinity
    check("{MachineNumberQ(N(7^450*I)), Im(N(7^450*I))==7^450, N(7^450*I)==ComplexInfinity}", //
        "{False,True,False}");
  }

  @Test
  public void testComplexUnderflowMessage() {
    assertEquals("General: Exp(-715.0+I*1.0)" + MUNFL, //
        messagesOf("Exp(-715.+I)"));
    assertEquals("General: Exp(-1015.0+I*1.0)" + MUNFL, //
        messagesOf("Exp(-1015.+I)"));
  }

  @Test
  public void testSeventhProbe() {
    // a number is zero only if it is
    check("{KroneckerDelta(3.*^-20), DiscreteDelta(3.*^-20), Unitize(3.*^-20), Unitize(3.*^-20,1.*^-10), " //
        + "KroneckerDelta(0.), Unitize(0.), DiscreteDelta(0,0.)}", //
        "{0,0,1,0,1,0,1}");
    // several arguments are compared as Equal does
    check("{KroneckerDelta(0.1+0.2,0.3), KroneckerDelta(1,1.), KroneckerDelta(1,2), KroneckerDelta(0.3,0.4)}", //
        "{1,1,0,0}");
    check("{PossibleZeroQ(3.*^-320), PossibleZeroQ(3.*^-20), PossibleZeroQ(0.1+0.2-0.3), PossibleZeroQ(0.), " //
        + "PossibleZeroQ(Sqrt(2)*Sqrt(3)-Sqrt(6)), PossibleZeroQ(7^-400)}", //
        "{False,False,False,True,True,False}");
    check("Block({z=N(7^-30,20)}, PossibleZeroQ(z))", //
        "False");
    // an arbitrary precision limit or step keeps its precision
    check("{Precision(Last(Range(0,1,N(1/4,20)))), Precision(Last(Range(N(1,20),2,1/2)))}", //
        "{20,20}");
    check("Table(x,{x,1.,2,0.5}) // InputForm", //
        "{1.0`,1.5`,2.0`}");
  }

  @Test
  public void testCancellationOfExactOperands() {
    // a machine residue which vanishes with more precision is no difference
    check("{(E+Pi)^2-E^2-Pi^2-2*E*Pi<0, (E+Pi)^2-E^2-Pi^2-2*E*Pi>=0, (E+Pi)^2-E^2-Pi^2-2*E*Pi==0}", //
        "{False,True,True}");
    check("Tan(5*ArcTan(29/278)+7*ArcTan(3/79))==1", //
        "True");
    // a difference which keeps its size is one, however small
    check("{Sqrt(2)+10^-20==Sqrt(2), Pi+10^-17>Pi, Exp(Pi*Sqrt(163))==262537412640768744}", //
        "{False,True,False}");
  }

  @Test
  public void testMachineUnderflowMessage() {
    // one message for one operation, with the operation as its argument
    assertEquals("General: Exp(-720.0)" + MUNFL, //
        messagesOf("Exp(-720.)"));
    assertEquals("General: Exp(-1020.0)" + MUNFL, //
        messagesOf("Exp(-1020.)"));
    // the same for a computed exponent
    assertEquals("General: Exp(-721.0)" + MUNFL, //
        messagesOf("Block({x=-721.}, Exp(x))"));
    // no message for a comparison or a sign: the machine value is not the result
    assertEquals("", //
        messagesOf(
            "{Pi^(-900)<1, Positive(Pi^(-901)), Sign(Pi^(-902)), Abs(-Pi^(-903)), Pi^(-904)==0}"));
  }

  @Test
  public void testGeneralStop() {
    // prints the first three General::munfl of a calculation
    assertEquals("General: Exp(-711.0)" + MUNFL //
        + "General: Exp(-722.0)" + MUNFL //
        + "General: Exp(-1011.0)" + MUNFL //
        + "General: Further output of General::munfl will be suppressed during this calculation.\n", //
        messagesOf("Exp({-711., -722., -1011., -1022., -1033.})"));
    // the next calculation starts again
    assertEquals("General: Exp(-733.0)" + MUNFL, //
        messagesOf("Exp(-733.)"));
    // Check sees every message, printed or not
    check(
        "{Check(Exp(-741.),m), Check(Exp(-742.),m), Check(Exp(-743.),m), Check(Exp(-744.),m), Check(Exp(-600.)>0,m)}", //
        "{m,m,m,m,True}");
  }

  @Test
  public void testMultiplesAndDuplicates() {
    // the quotient is not rounded before it is rounded up or down
    check("{Ceiling(0.6,0.2)==3*0.2, Floor(0.6,0.2)==2*0.2, Ceiling(-0.6,0.2)==-2*0.2, Ceiling(7,2), Ceiling(-7,2)}", //
        "{True,True,True,8,-6}");
    // machine numbers which differ in the last bit are one element, everything else is counted
    // by identity
    check("Tally({1.,1.+2.^-52,a,1,a,1.+2.^-50,3/10,0.3})", //
        "{{1.0,2},{a,2},{1,1},{1.0,1},{3/10,1},{0.3,1}}");
    check("{DeleteDuplicates({1.,1.+2.^-52,1,a,a}), DuplicateFreeQ({1.,1.+2.^-52}), DuplicateFreeQ({1.,1})}", //
        "{{1.0,1,a},False,True}");
    // a fraction whose numerator and denominator are both beyond the machine range
    check("N((10^400+1)/(3*10^390)) === N(10^10/3)", //
        "True");
    // a range which reaches its limit ends in it
    check("{Last(Range(0,7/10,0.1))===0.7, Last(Range(-0.3,0,0.1))===0., Last(Range(0.3,0,-0.1))===0., Range(0.5,0.5)}", //
        "{True,True,True,{0.5}}");
  }

  @Test
  public void testEighthProbe() {
    // a Range never goes beyond its limit, but it doesn't end in a limit it stays below
    check("{Last(Range(0,0.7000000000000001,0.1)), Last(Range(0,0.7+1.*^-12,0.1)), Last(Range(0,0.7-1.*^-12,0.1)), " //
        + "Last(Range(0.1,0.5,0.1)), Last(Range(1.,2.2,0.3)), Last(Range(0,2.1,0.7))} // InputForm", //
        "{0.7000000000000001`,0.7000000000000001`,0.6000000000000001`,0.5`,2.2`,2.0999999999999996`}");
    check("{Last(Range(0,7/10,0.1))===0.7, Last(Table(x,{x,0,7/10,0.1}))===7*0.1}", //
        "{True,True}");
    // the end test of Range is one bit stricter than the one of Table
    check("{Length(Range(0,1-2.^-49,0.1)), Length(Range(0,1-2.^-50,0.1)), Length(Range(0,1-2.^-51,0.1)), " //
        + "Length(Range(0,1-2.^-52,0.1)), Length(Table(x,{x,0,1-2.^-49,0.1})), Length(Table(x,{x,0,1-2.^-50,0.1})), " //
        + "Length(Table(x,{x,0,1-2.^-51,0.1})), Length(Table(x,{x,0,1-2.^-52,0.1}))}", //
        "{10,10,10,11,10,10,11,11}");
    // an exact number plus a small machine number
    check("Block({z=2.^-51}, {1-z, 1/2-z, I*z}) // InputForm", //
        "{0.9999999999999996`,0.49999999999999956`,I*4.440892098500626`*^-16}");
    // Floor, Ceiling and Round to a multiple: one machine division, the multiple keeps its type
    check("{Ceiling(0.6,1/5), Floor(0.6,1/5), Ceiling(3/5,0.2), Floor(3/5,0.2), Ceiling(0.7,1/10), Floor(0.7,1/10), " //
        + "Ceiling(7/10,0.1), Floor(7/10,0.1)} // InputForm", //
        "{3/5,2/5,0.6000000000000001`,0.4`,7/10,3/5,0.7000000000000001`,0.6000000000000001`}");
    check("{Round(0.35,0.1), Round(0.35,1/10), Round(7/20,0.1), Round(0.25,0.1), Round(0.25,1/10), Round(0.45,0.1), " //
        + "Round(2.5,1), Round(0.15,0.1)} // InputForm", //
        "{0.30000000000000004`,3/10,0.30000000000000004`,0.2`,1/5,0.4`,2,0.1`}");
    check("{Ceiling(0.7,-0.1), Floor(0.7,-0.1), Round(0.35,-0.1), Ceiling(7,-2), Floor(7,-2), Round(7,-2)} // InputForm", //
        "{0.6000000000000001`,0.7000000000000001`,0.30000000000000004`,6,8,8}");
    // complex numbers: by the real part, then by the size and the sign of the imaginary part
    check("{NumericalSort({1+I,1.,1-I,I,-I}), NumericalOrder(Pi,N(Pi)), NumericalOrder(Pi,3.14), NumericalOrder(I,1)}", //
        "{{-I,I,1.0,1-I,1+I},0,-1,1}");
    // a literal beyond the double range keeps the digits of its mantissa
    check("{Precision(1.*^400), Precision(1.2345678901234567890*^400)}", //
        "{16,20}");
  }
}
