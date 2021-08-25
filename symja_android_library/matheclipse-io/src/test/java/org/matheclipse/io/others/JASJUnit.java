package org.matheclipse.io.others;

import java.util.SortedMap;

import edu.jas.arith.BigRational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrderByName;
import edu.jas.ufd.FactorComplex;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import junit.framework.TestCase;

public class JASJUnit extends TestCase {

  public JASJUnit(String name) {
    super(name);
  }

  public void testSimplexFactor001() {
    final int variableSize = 1;
    String[] vars = new String[] {"x"};
    ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
    GenPolynomialRing<Complex<BigRational>> cpfac =
        new GenPolynomialRing<Complex<BigRational>>(
            cfac, variableSize, TermOrderByName.INVLEX, vars);
    GenPolynomial<Complex<BigRational>> a = cpfac.parse("x^2 + 4*x + 4");
    FactorComplex<BigRational> factorAbstract = new FactorComplex<BigRational>(cfac);
    SortedMap<GenPolynomial<Complex<BigRational>>, Long> map = factorAbstract.factors(a);

    for (SortedMap.Entry<GenPolynomial<Complex<BigRational>>, Long> entry : map.entrySet()) {
      if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
        continue;
      }
      System.out.println(" ( " + entry.getKey() + " ) ^ " + entry.getValue());
    }
  }

  public void testComplexFactor() {
    final int variableSize = 2;

    String[] vars = new String[] {"x0", "x1"};
    ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
    GenPolynomialRing<Complex<BigRational>> cpfac =
        new GenPolynomialRing<Complex<BigRational>>(
            cfac, variableSize, TermOrderByName.INVLEX, vars);
    GenPolynomial<Complex<BigRational>> a = cpfac.parse("x1^4 + x0^4");
    // GenPolynomial<Complex<BigRational>> a = cpfac.parse("x1^8 + x0^8") ;
    // GenPolynomial<Complex<BigRational>> a = cpfac.parse("x1^12 - x0^12") ;
    FactorComplex<BigRational> factorAbstract = new FactorComplex<BigRational>(cfac);
    SortedMap<GenPolynomial<Complex<BigRational>>, Long> map = factorAbstract.factors(a);

    for (SortedMap.Entry<GenPolynomial<Complex<BigRational>>, Long> entry : map.entrySet()) {
      if (entry.getKey().isONE() && entry.getValue().equals(1L)) {
        continue;
      }
      System.out.println(" ( " + entry.getKey() + " ) ^ " + entry.getValue());
    }
  }

  public void testGCD() {

    ComplexRing<BigRational> cfac = new ComplexRing<BigRational>(BigRational.ZERO);
    GreatestCommonDivisor<Complex<BigRational>> engine = GCDFactory.getImplementation(cfac);
    GenPolynomialRing<Complex<BigRational>> cpfac =
        new GenPolynomialRing<Complex<BigRational>>(
            cfac,
            new String[] {
              "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"
            },
            TermOrderByName.INVLEX);
    GenPolynomial<Complex<BigRational>> p1 =
        cpfac.parse(
            "-1/16777216*a^48*b+c^24*d-3/1048576*a^46*e*e^2+12*c^\r\n"
                + "22*a^2*d*e^2-69/1048576*a^44*f*e^4+66*c^20*a^4*d*e^\r\n"
                + "4+12*c^22*d^3*e^4-253/262144*a^42*g*e^6+220*c^18*a^\r\n"
                + "6*d*e^6+132*c^20*a^2*d^3*e^6-5313/524288*a^40*h*e^\r\n"
                + "8+495*c^16*a^8*d*e^8+660*c^18*a^4*d^3*e^8+66*c^\r\n"
                + "20*d^5*e^8-5313/65536*a^38*i*e^10+792*c^14*a^10*d*e^\r\n"
                + "10+1980*c^16*a^6*d^3*e^10+660*c^18*a^2*d^5*e^10\r\n"
                + "-33649/65536*a^36*j*e^12+924*c^12*a^12*d*e^12+3960*c^\r\n"
                + "14*a^8*d^3*e^12+2970*c^16*a^4*d^5*e^12+220*c^18*d^\r\n"
                + "7*e^12-43263/16384*a^34*l*e^14+792*c^10*a^14*d*e^\r\n"
                + "14+5544*c^12*a^10*d^3*e^14+7920*c^14*a^6*d^5*e^\r\n"
                + "14+1980*c^16*a^2*d^7*e^14-735471/65536*a^32*m*e^16+\r\n"
                + "495*c^8*a^16*d*e^16+5544*c^10*a^12*d^3*e^16+\r\n"
                + "13860*c^12*a^8*d^5*e^16+7920*c^14*a^4*d^7*e^16+\r\n"
                + "495*c^16*d^9*e^16-81719/2048*a^30*n*e^18+220*c^6*a^\r\n"
                + "18*d*e^18+3960*c^8*a^14*d^3*e^18+16632*c^10*a^10*d^\r\n"
                + "5*e^18+18480*c^12*a^6*d^7*e^18+3960*c^14*a^2*d^9*e^\r\n"
                + "18-245157/2048*a^28*o*e^20+66*c^4*a^20*d*e^20+1980*c^\r\n"
                + "6*a^16*d^3*e^20+13860*c^8*a^12*d^5*e^20+27720*c^\r\n"
                + "10*a^8*d^7*e^20+13860*c^12*a^4*d^9*e^20+792*c^14*d^\r\n"
                + "11*e^20-156009/512*a^26*p*e^22+12*c^2*a^22*d*e^\r\n"
                + "22+660*c^4*a^18*d^3*e^22+7920*c^6*a^14*d^5*e^22+\r\n"
                + "27720*c^8*a^10*d^7*e^22+27720*c^10*a^6*d^9*e^22+\r\n"
                + "5544*c^12*a^2*d^11*e^22-675015/1024*a^24*d*e^24+132*c^\r\n"
                + "2*a^20*d^3*e^24+2970*c^4*a^16*d^5*e^24+18480*c^6*a^\r\n"
                + "12*d^7*e^24+34650*c^8*a^8*d^9*e^24+16632*c^10*a^\r\n"
                + "4*d^11*e^24+924*c^12*d^13*e^24-154473/128*a^22*d^3*e^\r\n"
                + "26+660*c^2*a^18*d^5*e^26+7920*c^4*a^14*d^7*e^26+\r\n"
                + "27720*c^6*a^10*d^9*e^26+27720*c^8*a^6*d^11*e^26+\r\n"
                + "5544*c^10*a^2*d^13*e^26-236709/128*a^20*d^5*e^28+\r\n"
                + "1980*c^2*a^16*d^7*e^28+13860*c^4*a^12*d^9*e^28+\r\n"
                + "27720*c^6*a^8*d^11*e^28+13860*c^8*a^4*d^13*e^28+\r\n"
                + "792*c^10*d^15*e^28-74679/32*a^18*d^7*e^30+3960*c^2*a^\r\n"
                + "14*d^9*e^30+16632*c^4*a^10*d^11*e^30+18480*c^6*a^\r\n"
                + "6*d^13*e^30+3960*c^8*a^2*d^15*e^30-608751/256*a^16*d^\r\n"
                + "9*e^32+5544*c^2*a^12*d^11*e^32+13860*c^4*a^8*d^\r\n"
                + "13*e^32+7920*c^6*a^4*d^15*e^32+495*c^8*d^17*e^32\r\n"
                + "-30591/16*a^14*d^11*e^34+5544*c^2*a^10*d^13*e^34+\r\n"
                + "7920*c^4*a^6*d^15*e^34+1980*c^6*a^2*d^17*e^34\r\n"
                + "-18865/16*a^12*d^13*e^36+3960*c^2*a^8*d^15*e^36+2970*c^\r\n"
                + "4*a^4*d^17*e^36+220*c^6*d^19*e^36-2145/4*a^10*d^\r\n"
                + "15*e^38+1980*c^2*a^6*d^17*e^38+660*c^4*a^2*d^19*e^\r\n"
                + "38-1353/8*a^8*d^17*e^40+660*c^2*a^4*d^19*e^40+66*c^\r\n"
                + "4*d^21*e^40-33*a^6*d^19*e^42+132*c^2*a^2*d^21*e^\r\n"
                + "42-3*a^4*d^21*e^44+12*c^2*d^23*e^44");
    GenPolynomial<Complex<BigRational>> p2 = cpfac.parse("d");
    GenPolynomial<Complex<BigRational>> p3 = engine.gcd(p1, p2);
    System.out.println(p3.toString());
  }
}
