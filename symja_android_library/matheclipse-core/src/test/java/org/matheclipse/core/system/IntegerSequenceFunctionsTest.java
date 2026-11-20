package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * ThueMorse, RudinShapiro, NumberExpand, NumberCompose, FiniteAbelianGroupCount,
 * ChampernowneNumber, NorlundB, PowerModList, QBinomial, QFactorial, DirichletConvolve,
 * CycleIndexPolynomial and the MixedRadix, Convergents, ChineseRemainder and FindLinearRecurrence
 * extensions. Sequence values are cross-checked with the OEIS entries named in the comments.
 */
public class IntegerSequenceFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testThueMorse() {
    // A010060
    check("Table(ThueMorse(n), {n, 0, 20})", //
        "{0,1,1,0,1,0,0,1,1,0,0,1,0,1,1,0,1,0,0,1,0}");
    check("{ThueMorse(2^100 + 3), ThueMorse({4, 5}), ThueMorse(x)}", //
        "{1,{1,0},ThueMorse(x)}");
    // ThueMorse(-n) == ThueMorse(n)
    check("{ThueMorse(-1), ThueMorse(-6)}", //
        "{1,0}");
    check("ThueMorse(3/2)", //
        "ThueMorse(3/2)");
  }

  @Test
  public void testRudinShapiro() {
    // A020985
    check("Table(RudinShapiro(n), {n, 0, 20})", //
        "{1,1,1,-1,1,1,-1,1,1,1,1,-1,-1,-1,1,-1,1,1,1,-1,1}");
    check("{RudinShapiro(y), RudinShapiro(-5)}", //
        "{RudinShapiro(y),RudinShapiro(-5)}");
  }

  @Test
  public void testNumberExpand() {
    check("NumberExpand(90714)", //
        "{90000,0,700,10,4}");
    check("NumberExpand(90714, 7)", //
        "{84035,4802,1715,147,14,1}");
    check("NumberExpand(90714, 10, 8)", //
        "{90000,0,700,10,4,0,0,0}");
    check("NumberExpand(-4031)", //
        "{-4000,0,-30,-1}");
    check("NumberExpand(255, 16, 3)", //
        "{240,15,0}");
    check("NumberExpand(0)", //
        "{0}");
    check("NumberExpand(17/8)", //
        "{17/8}");
    // 15 exact place values and the remainder of a machine real
    check("NumberExpand(3.25)", //
        "{3,1/5,1/20,0,0,0,0,0,0,0,0,0,0,0,0,0.0}");
  }

  @Test
  public void testNumberCompose() {
    check("NumberCompose({2, 1, 5}, {100, 10, 1})", //
        "215");
    check("NumberCompose({1, 2}, {a, b})", //
        "NumberCompose({1,2},{a,b})");
    check("NumberCompose({3, 4}, {60, 1/2, 1/4})", //
        "182");
    check("NumberCompose({1, 2, 3}, {1, 2})", //
        "NumberCompose({1,2,3},{1,2})");
  }

  @Test
  public void testFiniteAbelianGroupCount() {
    // A000688
    check("Table(FiniteAbelianGroupCount(n), {n, 1, 20})", //
        "{1,1,1,2,1,1,1,3,2,1,1,2,1,1,1,5,1,2,1,2}");
    // PartitionsP(10)*PartitionsP(4)*PartitionsP(1)
    check("FiniteAbelianGroupCount(2^10*3^4*7)", //
        "210");
    check("{FiniteAbelianGroupCount(0), FiniteAbelianGroupCount(-8)}", //
        "{0,FiniteAbelianGroupCount(-8)}");
  }

  @Test
  public void testChampernowneNumber() {
    check("ChampernowneNumber()", //
        "ChampernowneNumber()");
    check("N(ChampernowneNumber(), 40)", //
        "0.1234567891011121314151617181920212223243");
    // 0.1 10 11 100 101 ... in base 2
    check("N(ChampernowneNumber(2), 30)", //
        "0.862240125868054571557790283249");
  }

  @Test
  public void testNorlundB() {
    check("Table(NorlundB(n, a), {n, 0, 3})", //
        "{1,-a/2,-a/12+a^2/4,a^2/8-a^3/8}");
    check("NorlundB(4, 1) == BernoulliB(4)", //
        "True");
    check("Expand(NorlundB(5, 1, x) - BernoulliB(5, x))", //
        "0");
    check("NorlundB(3, 2, x)", //
        "-1/2+5/2*x-3*x^2+x^3");
    check("NorlundB(5, 3/2)", //
        "69/1024");
  }

  @Test
  public void testPowerModList() {
    check("PowerModList(4, 1/2, 15)", //
        "{2,7,8,13}");
    check("PowerModList(3, 1/2, 7)", //
        "{}");
    check("PowerModList(1, 1/4, 16)", //
        "{1,3,5,7,9,11,13,15}");
    check("PowerModList(0, 1/2, 9)", //
        "{0,3,6}");
    check("PowerModList(2, -1, 9)", //
        "{5}");
    check("PowerModList(3, -1, 9)", //
        "PowerModList(3,-1,9)");
    check("PowerModList(8, 1/3, 1001)", //
        "{2,57,123,200,288,431,695,772,981}");
    check("PowerModList(2, 1/2, 1000000007)", //
        "{59713600,940286407}");
  }

  @Test
  public void testQBinomial() {
    // A022166 (5, 2)
    check("QBinomial(5, 2, 2)", //
        "155");
    // a symbolic q stays unevaluated
    check("QBinomial(5, 2, q)", //
        "QBinomial(5,2,q)");
    check("QBinomial(6, 3, -1)", //
        "0");
    check("{QBinomial(4, 0, q), QBinomial(4, 5, q), QBinomial(n, 2, q)}", //
        "{1,QBinomial(4,5,q),QBinomial(n,2,q)}");
    check("FunctionExpand(QBinomial(n, k, q))", //
        "(QPochhammer(q^(1+k),q)*QPochhammer(q^(1-k+n),q))/(QPochhammer(q,q)*QPochhammer(q^(\n"
            + "1+n),q))");
  }

  @Test
  public void testQFactorial() {
    check("QFactorial(4, q)", //
        "QFactorial(4,q)");
    check("FunctionExpand(QFactorial(n, q))", //
        "QPochhammer(q,q)/((1-q)^n*QPochhammer(q^(1+n),q))");
    check("N(QFactorial(5/2, 1/3))", //
        "1.59425");
    check("{QFactorial(4, 3), QFactorial(4, 1), QFactorial(0, q)}", //
        "{2080,24,1}");
  }

  @Test
  public void testDirichletConvolve() {
    check("DirichletConvolve(1, 1, n, 12)", //
        "6");
    check("DirichletConvolve(n, 1, n, 28)", //
        "56");
    check("DirichletConvolve(MoebiusMu(n), n, n, 30)", //
        "8");
    check("DirichletConvolve(f(n), g(n), n, 6)", //
        "f(6)*g(1)+f(3)*g(2)+f(2)*g(3)+f(1)*g(6)");
    check("DirichletConvolve(1, 1, n, m)", //
        "DivisorSigma(0,m)");
    check("DirichletConvolve(n^2, 3, n, m)", //
        "3*DivisorSigma(2,m)");
    check("DirichletConvolve(1, 1, n, -6)", //
        "DirichletConvolve(1,1,n,-6)");
  }

  @Test
  public void testCycleIndexPolynomial() {
    check("CycleIndexPolynomial(SymmetricGroup(4), {x1, x2, x3, x4})", //
        "x1^4/24+1/4*x1^2*x2+x2^2/8+1/3*x1*x3+x4/4");
    check("CycleIndexPolynomial(CyclicGroup(6), {a, b, c, d, e, f})", //
        "a^6/6+b^3/6+c^2/3+f/3");
    check("CycleIndexPolynomial(AlternatingGroup(4), Array(z, 4))", //
        "z(1)^4/12+z(2)^2/4+2/3*z(1)*z(3)");
    // a cycle length without a variable counts as 1
    check("CycleIndexPolynomial(SymmetricGroup(3), {u, v})", //
        "1/3+u^3/6+1/2*u*v");
    // Polya counting: multisets of size n from 3 kinds
    check("Table(CycleIndexPolynomial(SymmetricGroup(n), Table(3, n)) == Binomial(n + 2, n), {n, 1, 8})", //
        "{True,True,True,True,True,True,True,True}");
    check("Table(CycleIndexPolynomial(AlternatingGroup(n), Table(1, n)), {n, 1, 6})", //
        "{1,1,1,1,1,1}");
  }

  @Test
  public void testMixedRadix() {
    check("IntegerDigits(100000, MixedRadix({24, 60, 60}))", //
        "{1,3,46,40}");
    check("IntegerDigits(1000, MixedRadix({24, 60, 60}))", //
        "{16,40}");
    check("IntegerDigits(719, MixedRadix(Range(6, 2, -1)))", //
        "{5,4,3,2,1}");
    check("IntegerDigits(1000, MixedRadix({2, 3, 5}), 6)", //
        "{0,0,33,0,2,0}");
    check("FromDigits({1, 3, 46, 40}, MixedRadix({24, 60, 60}))", //
        "100000");
    check("FromDigits({a, b, c}, MixedRadix({x, y}))", //
        "c+b*y+a*x*y");
  }

  @Test
  public void testConvergentsExtended() {
    check("Convergents(Pi, 5)", //
        "{3,22/7,333/106,355/113,103993/33102}");
    // a quadratic irrational: through the first period, then the number itself
    check("Convergents(Sqrt(7))", //
        "{2,3,5/2,8/3,Sqrt(7)}");
    check("Convergents(Sqrt(7), 6)", //
        "{2,3,5/2,8/3,37/14,45/17}");
    check("Convergents(17/12, 2)", //
        "{1,3/2}");
    check("Convergents({1, {1, 2}}, 5)", //
        "{1,2,5/3,7/4,19/11}");
    check("Convergents(x, 3)", //
        "Convergents(x,3)");
  }

  @Test
  public void testChineseRemainderExtended() {
    // moduli which are not coprime
    check("ChineseRemainder({1, 3}, {4, 6})", //
        "9");
    check("ChineseRemainder({5, 11}, {12, 18})", //
        "29");
    check("ChineseRemainder({1, 2}, {4, 6})", //
        "ChineseRemainder({1,2},{4,6})");
    // the smallest solution which is at least d
    check("ChineseRemainder({2, 3, 2}, {3, 5, 7}, 1000)", //
        "1073");
    check("ChineseRemainder({2, 3, 2}, {3, 5, 7}, -100)", //
        "-82");
  }

  @Test
  public void testFindLinearRecurrenceMaximumOrder() {
    check("FindLinearRecurrence(Table(Fibonacci(n) + n^2, {n, 1, 20}), 5)", //
        "{4,-5,1,2,-1}");
    check("FindLinearRecurrence({1, 3, 7, 15, 31, 63, 127}, 2)", //
        "{3,-2}");
    check("FindLinearRecurrence({1, 3, 7, 15, 31, 63, 127}, 1)", //
        "FindLinearRecurrence({1,3,7,15,31,63,127},1)");
  }
}
