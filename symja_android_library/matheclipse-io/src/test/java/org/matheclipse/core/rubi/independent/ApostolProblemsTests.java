package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

public class ApostolProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public ApostolProblemsTests(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 60;
      if (init) {
        System.out.println("Apostol");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void test1() {
    check( //
        "Integrate[Sqrt[2*x+1], x]", //
        "(1+2*x)^(3/2)/3" //
    );
  }

  public void test2() {
    check( //
        "Integrate[x*Sqrt[1+3*x], x]", //
        "-2/27*(1+3*x)^(3/2)+2/45*(1+3*x)^(5/2)" //
    );
  }

  public void test3() {
    check( //
        "Integrate[x^2*Sqrt[x+1], x]", //
        "2/3*(1+x)^(3/2)-4/5*(1+x)^(5/2)+2/7*(1+x)^(7/2)" //
    );
  }

  public void test4() {
    check( //
        "Integrate[x/Sqrt[2-3*x], x]", //
        "-4/9*Sqrt[2-3*x]+2/27*(2-3*x)^(3/2)" //
    );
  }

  public void test5() {
    check( //
        "Integrate[(x+1)/(x^2+2*x+2)^3, x]", //
        "-1/(4*(2+2*x+x^2)^2)" //
    );
  }

  public void test6() {
    check( //
        "Integrate[Sin[x]^3, x]", //
        "-Cos[x]+Cos[x]^3/3" //
    );
  }

  public void test7() {
    check( //
        "Integrate[z*(z+(-1)*1)^(1/3), z]", //
        "3/4*(-1+z)^(4/3)+3/7*(-1+z)^(7/3)" //
    );
  }

  public void test8() {
    check( //
        "Integrate[Cos[x]/Sin[x]^3, x]", //
        "-Csc[x]^2/2" //
    );
  }

  public void test9() {
    check( //
        "Integrate[Cos[2*x]*Sqrt[4-Sin[2*x]], x]", //
        "-(4-Sin[2*x])^(3/2)/3" //
    );
  }

  public void test10() {
    check( //
        "Integrate[Sin[x]/(3+Cos[x])^2, x]", //
        "1/(3+Cos[x])" //
    );
  }

  public void test11() {
    check( //
        "Integrate[Sin[x]/Sqrt[Cos[x]^3], x]", //
        "(2*Cos[x])/Sqrt[Cos[x]^3]" //
    );
  }

  public void test12() {
    check( //
        "Integrate[Sin[Sqrt[x+1]]/Sqrt[x+1], x]", //
        "-2*Cos[Sqrt[1+x]]" //
    );
  }

  public void test13() {
    check( //
        "Integrate[x^(n+(-1)*1)*Sin[x^n], x]", //
        "-Cos[x^n]/n" //
    );
  }

  public void test14() {
    check( //
        "Integrate[x^5/Sqrt[1-x^6], x]", //
        "-Sqrt[1-x^6]/3" //
    );
  }

  public void test15() {
    check( //
        "Integrate[t*(1+t)^(1/4), t]", //
        "-4/5*(1+t)^(5/4)+4/9*(1+t)^(9/4)" //
    );
  }

  public void test16() {
    check( //
        "Integrate[1/(x^2+1)^(3/2), x]", //
        "x/Sqrt[1+x^2]" //
    );
  }

  public void test17() {
    check( //
        "Integrate[x^2*(8*x^3+27)^(2/3), x]", //
        "(27+8*x^3)^(5/3)/40" //
    );
  }

  public void test18() {
    check( //
        "Integrate[(Sin[x]+Cos[x])/(Sin[x]-Cos[x])^(1/3), x]", //
        "3/2*(-Cos[x]+Sin[x])^(2/3)" //
    );
  }

  public void test19() {
    check( //
        "Integrate[x/Sqrt[1+x^2+(1+x^2)^(3/2)], x]", //
        "(2*Sqrt[(1+x^2)*(1+Sqrt[1+x^2])])/Sqrt[1+x^2]" //
    );
  }

  public void test20() {
    check( //
        "Integrate[x/(Sqrt[1+x^2]*Sqrt[1+Sqrt[1+x^2]]), x]", //
        "2*Sqrt[1+Sqrt[1+x^2]]" //
    );
  }

  public void test21() {
    check( //
        "Integrate[(x^2+1-2*x)^(1/5)/(1-x), x]", //
        "-5/2*(1-2*x+x^2)^(1/5)" //
    );
  }

  public void test22() {
    check( //
        "Integrate[x*Sin[x], x]", //
        "-x*Cos[x]+Sin[x]" //
    );
  }

  public void test23() {
    check( //
        "Integrate[x^2*Sin[x], x]", //
        "2*Cos[x]-x^2*Cos[x]+2*x*Sin[x]" //
    );
  }

  public void test24() {
    check( //
        "Integrate[x^3*Cos[x], x]", //
        "-6*Cos[x]+3*x^2*Cos[x]-6*x*Sin[x]+x^3*Sin[x]" //
    );
  }

  public void test25() {
    check( //
        "Integrate[x^3*Sin[x], x]", //
        "6*x*Cos[x]-x^3*Cos[x]-6*Sin[x]+3*x^2*Sin[x]" //
    );
  }

  public void test26() {
    check( //
        "Integrate[Sin[x]*Cos[x], x]", //
        "Sin[x]^2/2" //
    );
  }

  public void test27() {
    check( //
        "Integrate[x*Sin[x]*Cos[x], x]", //
        "-x/4+1/4*Cos[x]*Sin[x]+1/2*x*Sin[x]^2" //
    );
  }

  public void test28() {
    check( //
        "Integrate[Sin[x]^2, x]", //
        "x/2-1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test29() {
    check( //
        "Integrate[Sin[x]^3, x]", //
        "-Cos[x]+Cos[x]^3/3" //
    );
  }

  public void test30() {
    check( //
        "Integrate[Sin[x]^4, x]", //
        "1/8*3*x-3/8*Cos[x]*Sin[x]-1/4*Cos[x]*Sin[x]^3" //
    );
  }

  public void test31() {
    check( //
        "Integrate[Sin[x]^5, x]", //
        "-Cos[x]+1/3*2*Cos[x]^3-Cos[x]^5/5" //
    );
  }

  public void test32() {
    check( //
        "Integrate[Sin[x]^6, x]", //
        "1/16*5*x-5/16*Cos[x]*Sin[x]-5/24*Cos[x]*Sin[x]^3-1/6*Cos[x]*Sin[x]^5" //
    );
  }

  public void test33() {
    check( //
        "Integrate[x*Sin[x]^2, x]", //
        "x^2/4-1/2*x*Cos[x]*Sin[x]+Sin[x]^2/4" //
    );
  }

  public void test34() {
    check( //
        "Integrate[x*Sin[x]^3, x]", //
        "-2/3*x*Cos[x]+1/3*2*Sin[x]-1/3*x*Cos[x]*Sin[x]^2+Sin[x]^3/9" //
    );
  }

  public void test35() {
    check( //
        "Integrate[x^2*Sin[x]^2, x]", //
        "-x/4+x^3/6+1/4*Cos[x]*Sin[x]-1/2*x^2*Cos[x]*Sin[x]+1/2*x*Sin[x]^2" //
    );
  }

  public void test36() {
    check( //
        "Integrate[Cos[x]^2, x]", //
        "x/2+1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test37() {
    check( //
        "Integrate[Cos[x]^3, x]", //
        "Sin[x]-Sin[x]^3/3" //
    );
  }

  public void test38() {
    check( //
        "Integrate[Cos[x]^4, x]", //
        "1/8*3*x+3/8*Cos[x]*Sin[x]+1/4*Cos[x]^3*Sin[x]" //
    );
  }

  public void test39() {
    check( //
        "Integrate[(a^2-x^2)^(5/2), x]", //
        "5/16*a^4*x*Sqrt[a^2-x^2]+5/24*a^2*x*(a^2-x^2)^(3/2)+1/6*x*(a^2-x^2)^(5/2)+5/16*a^6*ArcTan[x/Sqrt[a^2-x^2]]" //
    );
  }

  public void test40() {
    check( //
        "Integrate[x^5/Sqrt[5+x^2], x]", //
        "25*Sqrt[5+x^2]-10/3*(5+x^2)^(3/2)+(5+x^2)^(5/2)/5" //
    );
  }

  public void test41() {
    check( //
        "Integrate[t^3/Sqrt[4+t^3], t]", //
        "2/5*t*Sqrt[4+t^3]-(8*2^(2/3)*Sqrt[2+Sqrt[3]]*(2^(2/3)+t)*Sqrt[(2*2^(1/3)-2^(2/3)*t+t^2)/(2^(2/3)*(1+Sqrt[3])+t)^2]*EllipticF[ArcSin[(2^(2/3)*(1-Sqrt[3])+t)/(2^(2/3)*(1+Sqrt[3])+t)],-7-4*Sqrt[3]])/(5*3^(1/4)*Sqrt[(2^(2/3)+t)/(2^(2/3)*(1+Sqrt[3])+t)^2]*Sqrt[4+t^3])" //
    );
  }

  public void test42() {
    check( //
        "Integrate[Tan[x]^2, x]", //
        "-x+Tan[x]" //
    );
  }

  public void test43() {
    check( //
        "Integrate[Tan[x]^4, x]", //
        "x-Tan[x]+Tan[x]^3/3" //
    );
  }

  public void test44() {
    check( //
        "Integrate[Cot[x]^2, x]", //
        "-x-Cot[x]" //
    );
  }

  public void test45() {
    check( //
        "Integrate[Cot[x]^4, x]", //
        "x+Cot[x]-Cot[x]^3/3" //
    );
  }

  public void test46() {
    check( //
        "Integrate[(2+3*x)*Sin[5*x], x]", //
        "-1/5*(2+3*x)*Cos[5*x]+3/25*Sin[5*x]" //
    );
  }

  public void test47() {
    check( //
        "Integrate[x*Sqrt[1+x^2], x]", //
        "(1+x^2)^(3/2)/3" //
    );
  }

  public void test48() {
    check( //
        "Integrate[x*(x^2+(-1)*1)^9, x]", //
        "(1-x^2)^10/20" //
    );
  }

  public void test49() {
    check( //
        "Integrate[(2*x+3)/(6*x+7)^3, x]", //
        "-(3+2*x)^2/(8*(7+6*x)^2)" //
    );
  }

  public void test50() {
    check( //
        "Integrate[x^4*(1+x^5)^5, x]", //
        "(1+x^5)^6/30" //
    );
  }

  public void test51() {
    check( //
        "Integrate[x^4*(1-x)^20, x]", //
        "-(1-x)^21/21+2/11*(1-x)^22-6/23*(1-x)^23+(1-x)^24/6-(1-x)^25/25" //
    );
  }

  public void test52() {
    check( //
        "Integrate[Sin[1/x]/x^2, x]", //
        "Cos[1/x]" //
    );
  }

  public void test53() {
    check( //
        "Integrate[Sin[(x+(-1)*1)^(1/4)], x]", //
        "24*(-1+x)^(1/4)*Cos[(-1+x)^(1/4)]-4*(-1+x)^(3/4)*Cos[(-1+x)^(1/4)]-24*Sin[(-1+x)^(1/4)]+12*Sqrt[-1+x]*Sin[(-1+x)^(1/4)]" //
    );
  }

  public void test54() {
    check( //
        "Integrate[x*Sin[x^2]*Cos[x^2], x]", //
        "Sin[x^2]^2/4" //
    );
  }

  public void test55() {
    check( //
        "Integrate[Sqrt[1+3*Cos[x]^2]*Sin[2*x], x]", //
        "-2/9*(4-3*Sin[x]^2)^(3/2)" //
    );
  }

  public void test56() {
    check( //
        "Integrate[1/(2+3*x), x]", //
        "Log[2+3*x]/3" //
    );
  }

  public void test57() {
    check( //
        "Integrate[Log[x]^2, x]", //
        "2*x-2*x*Log[x]+x*Log[x]^2" //
    );
  }

  public void test58() {
    check( //
        "Integrate[x*Log[x], x]", //
        "-x^2/4+1/2*x^2*Log[x]" //
    );
  }

  public void test59() {
    check( //
        "Integrate[x*Log[x]^2, x]", //
        "x^2/4-1/2*x^2*Log[x]+1/2*x^2*Log[x]^2" //
    );
  }

  public void test60() {
    check( //
        "Integrate[1/(1+t), t]", //
        "Log[1+t]" //
    );
  }

  public void test61() {
    check( //
        "Integrate[Cot[x], x]", //
        "Log[Sin[x]]" //
    );
  }

  public void test62() {
    check( //
        "Integrate[x^n*Log[a*x], x]", //
        "-x^(1+n)/(1+n)^2+(x^(1+n)*Log[a*x])/(1+n)" //
    );
  }

  public void test63() {
    check( //
        "Integrate[x^2*Log[x]^2, x]", //
        "1/27*2*x^3-2/9*x^3*Log[x]+1/3*x^3*Log[x]^2" //
    );
  }

  public void test64() {
    check( //
        "Integrate[1/(x*Log[x]), x]", //
        "Log[Log[x]]" //
    );
  }

  public void test65() {
    check( //
        "Integrate[Log[1-t]/(1-t), t]", //
        "-Log[1-t]^2/2" //
    );
  }

  public void test66() {
    check( //
        "Integrate[Log[x]/(x*Sqrt[1+Log[x]]), x]", //
        "-2*Sqrt[1+Log[x]]+2/3*(1+Log[x])^(3/2)" //
    );
  }

  public void test67() {
    check( //
        "Integrate[x^3*Log[x]^3, x]", //
        "-1/128*3*x^4+3/32*x^4*Log[x]-3/16*x^4*Log[x]^2+1/4*x^4*Log[x]^3" //
    );
  }

  public void test68() {
    check( //
        "Integrate[x^2*E^x^3, x]", //
        "E^x^3/3" //
    );
  }

  public void test69() {
    check( //
        "Integrate[2^Sqrt[x]/Sqrt[x], x]", //
        "2^(1+Sqrt[x])/Log[2]" //
    );
  }

  public void test70() {
    check( //
        "Integrate[Cos[x]*E^(2*Sin[x]), x]", //
        "E^(2*Sin[x])/2" //
    );
  }

  public void test71() {
    check( //
        "Integrate[E^x*Sin[x], x]", //
        "-1/2*E^x*Cos[x]+1/2*E^x*Sin[x]" //
    );
  }

  public void test72() {
    check( //
        "Integrate[E^x*Cos[x], x]", //
        "1/2*E^x*Cos[x]+1/2*E^x*Sin[x]" //
    );
  }

  public void test73() {
    check( //
        "Integrate[1/(1+E^x), x]", //
        "x-Log[1+E^x]" //
    );
  }

  public void test74() {
    check( //
        "Integrate[x*E^x, x]", //
        "-E^x+E^x*x" //
    );
  }

  public void test75() {
    check( //
        "Integrate[x/E^x, x]", //
        "-1/E^x-x/E^x" //
    );
  }

  public void test76() {
    check( //
        "Integrate[x^2*E^x, x]", //
        "2*E^x-2*E^x*x+E^x*x^2" //
    );
  }

  public void test77() {
    check( //
        "Integrate[x^2/E^(2*x), x]", //
        "-1/(4*E^(2*x))-(1/2*x)/E^(2*x)-(1/2*x^2)/E^(2*x)" //
    );
  }

  public void test78() {
    check( //
        "Integrate[E^Sqrt[x], x]", //
        "-2*E^Sqrt[x]+2*E^Sqrt[x]*Sqrt[x]" //
    );
  }

  public void test79() {
    check( //
        "Integrate[x^3/E^x^2, x]", //
        "-1/(E^x^2*2)-(1/2*x^2)/E^x^2" //
    );
  }

  public void test80() {
    check( //
        "Integrate[E^(a*x)*Cos[b*x], x]", //
        "(a*E^(a*x)*Cos[b*x])/(a^2+b^2)+(b*E^(a*x)*Sin[b*x])/(a^2+b^2)" //
    );
  }

  public void test81() {
    check( //
        "Integrate[E^(a*x)*Sin[b*x], x]", //
        "-(b*E^(a*x)*Cos[b*x])/(a^2+b^2)+(a*E^(a*x)*Sin[b*x])/(a^2+b^2)" //
    );
  }

  public void test82() {
    check( //
        "Integrate[ArcCot[x], x]", //
        "x*ArcCot[x]+Log[1+x^2]/2" //
    );
  }

  public void test83() {
    check( //
        "Integrate[ArcSec[x], x]", //
        "x*ArcSec[x]-ArcTanh[Sqrt[1-1/x^2]]" //
    );
  }

  public void test84() {
    check( //
        "Integrate[ArcCsc[x], x]", //
        "x*ArcCsc[x]+ArcTanh[Sqrt[1-1/x^2]]" //
    );
  }

  public void test85() {
    check( //
        "Integrate[ArcSin[x]^2, x]", //
        "-2*x+2*Sqrt[1-x^2]*ArcSin[x]+x*ArcSin[x]^2" //
    );
  }

  public void test86() {
    check( //
        "Integrate[ArcSin[x]/x^2, x]", //
        "-ArcSin[x]/x-ArcTanh[Sqrt[1-x^2]]" //
    );
  }

  public void test87() {
    check( //
        "Integrate[1/Sqrt[a^2-x^2], x]", //
        "ArcTan[x/Sqrt[a^2-x^2]]" //
    );
  }

  public void test88() {
    check( //
        "Integrate[1/Sqrt[1-2*x-x^2], x]", //
        "ArcSin[(1+x)/Sqrt[2]]" //
    );
  }

  public void test89() {
    check( //
        "Integrate[1/(a^2+x^2), x]", //
        "ArcTan[x/a]/a" //
    );
  }

  public void test90() {
    check( //
        "Integrate[1/(a+b*x^2), x]", //
        "ArcTan[(Sqrt[b]*x)/Sqrt[a]]/(Sqrt[a]*Sqrt[b])" //
    );
  }

  public void test91() {
    check( //
        "Integrate[1/(x^2-x+2), x]", //
        "-(2*ArcTan[(1-2*x)/Sqrt[7]])/Sqrt[7]" //
    );
  }

  public void test92() {
    check( //
        "Integrate[x*ArcTan[x], x]", //
        "-x/2+ArcTan[x]/2+1/2*x^2*ArcTan[x]" //
    );
  }

  public void test93() {
    check( //
        "Integrate[x^2*ArcCos[x], x]", //
        "-Sqrt[1-x^2]/3+(1-x^2)^(3/2)/9+1/3*x^3*ArcCos[x]" //
    );
  }

  public void test94() {
    check( //
        "Integrate[x*ArcTan[x]^2, x]", //
        "-x*ArcTan[x]+ArcTan[x]^2/2+1/2*x^2*ArcTan[x]^2+Log[1+x^2]/2" //
    );
  }

  public void test95() {
    check( //
        "Integrate[ArcTan[Sqrt[x]], x]", //
        "-Sqrt[x]+ArcTan[Sqrt[x]]+x*ArcTan[Sqrt[x]]" //
    );
  }

  public void test96() {
    check( //
        "Integrate[ArcTan[Sqrt[x]]/(Sqrt[x]*(1+x)), x]", //
        "ArcTan[Sqrt[x]]^2" //
    );
  }

  public void test97() {
    check( //
        "Integrate[Sqrt[1-x^2], x]", //
        "1/2*x*Sqrt[1-x^2]+ArcSin[x]/2" //
    );
  }

  public void test98() {
    check( //
        "Integrate[x*E^ArcTan[x]/(1+x^2)^(3/2), x]", //
        "-(E^ArcTan[x]*(1-x))/(2*Sqrt[1+x^2])" //
    );
  }

  public void test99() {
    check( //
        "Integrate[E^ArcTan[x]/(1+x^2)^(3/2), x]", //
        "(E^ArcTan[x]*(1+x))/(2*Sqrt[1+x^2])" //
    );
  }

  public void test100() {
    check( //
        "Integrate[x^2/(1+x^2)^2, x]", //
        "-x/(2*(1+x^2))+ArcTan[x]/2" //
    );
  }

  public void test101() {
    check( //
        "Integrate[E^x/(1+E^(2*x)), x]", //
        "ArcTan[E^x]" //
    );
  }

  public void test102() {
    check( //
        "Integrate[ArcCot[E^x]/E^x, x]", //
        "-x-ArcCot[E^x]/E^x+Log[1+E^(2*x)]/2" //
    );
  }

  public void test103() {
    check( //
        "Integrate[Sqrt[(a+x)/(a-x)], x]", //
        "-(a-x)*Sqrt[(a+x)/(a-x)]+2*a*ArcTan[Sqrt[(a+x)/(a-x)]]" //
    );
  }

  public void test104() {
    check( //
        "Integrate[Sqrt[(x-a)*(b-x)], x]", //
        "-1/4*(a+b-2*x)*Sqrt[-a*b+(a+b)*x-x^2]-1/8*(a-b)^2*ArcTan[(a+b-2*x)/(2*Sqrt[-a*b+(a+b)*x-x^2])]" //
    );
  }

  public void test105() {
    check( //
        "Integrate[1/Sqrt[(x-a)*(b-x)], x]", //
        "-ArcTan[(a+b-2*x)/(2*Sqrt[-a*b+(a+b)*x-x^2])]" //
    );
  }

  public void test106() {
    check( //
        "Integrate[(5*x+3)/(x^2+2*x+(-1)*3), x]", //
        "2*Log[1-x]+3*Log[3+x]" //
    );
  }

  public void test107() {
    check( //
        "Integrate[(2*x+5)/(x^2+2*x+(-1)*3), x]", //
        "7/4*Log[1-x]+Log[3+x]/4" //
    );
  }

  public void test108() {
    check( //
        "Integrate[(x^3+3*x)/(x^2-2*x+(-1)*3), x]", //
        "2*x+x^2/2+9*Log[3-x]+Log[1+x]" //
    );
  }

  public void test109() {
    check( //
        "Integrate[(2*x^2+5*x+(-1)*1)/(x^3+x^2-2*x), x]", //
        "2*Log[1-x]+Log[x]/2-Log[2+x]/2" //
    );
  }

  public void test110() {
    check( //
        "Integrate[(x^2+2*x+3)/((x+(-1)*1)*(x+1)^2), x]", //
        "1/(1+x)+3/2*Log[1-x]-Log[1+x]/2" //
    );
  }

  public void test111() {
    check( //
        "Integrate[(3*x^2+2*x+(-1)*2)/(x^3+(-1)*1), x]", //
        "(4*ArcTan[(1+2*x)/Sqrt[3]])/Sqrt[3]+Log[1-x^3]" //
    );
  }

  public void test112() {
    check( //
        "Integrate[(x^4-x^3+2*x^2-x+2)/((x+(-1)*1)*(x^2+2)^2), x]", //
        "1/(2*(2+x^2))-ArcTan[x/Sqrt[2]]/(3*Sqrt[2])+Log[1-x]/3+Log[2+x^2]/3" //
    );
  }

  public void test113() {
    check( //
        "Integrate[1/(Sin[x]+Cos[x]), x]", //
        "-ArcTanh[(Cos[x]-Sin[x])/Sqrt[2]]/Sqrt[2]" //
    );
  }

  public void test114() {
    check( //
        "Integrate[x/(4-x^2+Sqrt[4-x^2]), x]", //
        "-Log[1+Sqrt[4-x^2]]" //
    );
  }

  public void test115() {
    check( //
        "Integrate[(2*x+3)/((x+(-1)*2)*(x+5)), x]", //
        "Log[2-x]+Log[5+x]" //
    );
  }

  public void test116() {
    check( //
        "Integrate[x/((x+1)*(x+2)*(x+3)), x]", //
        "-Log[1+x]/2+2*Log[2+x]-3/2*Log[3+x]" //
    );
  }

  public void test117() {
    check( //
        "Integrate[x/(x^3-3*x+2), x]", //
        "1/(3*(1-x))+2/9*Log[1-x]-2/9*Log[2+x]" //
    );
  }

  public void test118() {
    check( //
        "Integrate[(x^4+2*x+(-1)*6)/(x^3+x^2-2*x), x]", //
        "-x+x^2/2-Log[1-x]+3*Log[x]+Log[2+x]" //
    );
  }

  public void test119() {
    check( //
        "Integrate[(8*x^3+7)/((x+1)*(2*x+1)^3), x]", //
        "-3/(1+2*x)^2+3/(1+2*x)+Log[1+x]" //
    );
  }

  public void test120() {
    check( //
        "Integrate[(4*x^2+x+1)/(x^3+(-1)*1), x]", //
        "2*Log[1-x]+Log[1+x+x^2]" //
    );
  }

  public void test121() {
    check( //
        "Integrate[x^4/(x^4+5*x^2+4), x]", //
        "x-8/3*ArcTan[x/2]+ArcTan[x]/3" //
    );
  }

  public void test122() {
    check( //
        "Integrate[(x+2)/(x^2+x), x]", //
        "2*Log[x]-Log[1+x]" //
    );
  }

  public void test123() {
    check( //
        "Integrate[1/(x*(x^2+1)^2), x]", //
        "1/(2*(1+x^2))+Log[x]-Log[1+x^2]/2" //
    );
  }

  public void test124() {
    check( //
        "Integrate[1/((x+1)*(x+2)^2*(x+3)^3), x]", //
        "1/(2+x)+1/(4*(3+x)^2)+5/(4*(3+x))+Log[1+x]/8+2*Log[2+x]-17/8*Log[3+x]" //
    );
  }

  public void test125() {
    check( //
        "Integrate[x/(x+1)^2, x]", //
        "1/(1+x)+Log[1+x]" //
    );
  }

  public void test126() {
    check( //
        "Integrate[1/(x^3-x), x]", //
        "-Log[x]+Log[1-x^2]/2" //
    );
  }

  public void test127() {
    check( //
        "Integrate[x^2/(x^2+x+(-1)*6), x]", //
        "x+4/5*Log[2-x]-9/5*Log[3+x]" //
    );
  }

  public void test128() {
    check( //
        "Integrate[(x+2)/(x^2-4*x+4), x]", //
        "4/(2-x)+Log[2-x]" //
    );
  }

  public void test129() {
    check( //
        "Integrate[1/((x^2-4*x+4)*(x^2-4*x+5)), x]", //
        "1/(2-x)+ArcTan[2-x]" //
    );
  }

  public void test130() {
    check( //
        "Integrate[(x+(-1)*3)/(x^3+3*x^2+2*x), x]", //
        "-1/2*3*Log[x]+4*Log[1+x]-5/2*Log[2+x]" //
    );
  }

  public void test131() {
    check( //
        "Integrate[1/(x^2+(-1)*1)^2, x]", //
        "x/(2*(1-x^2))+ArcTanh[x]/2" //
    );
  }

  public void test132() {
    check( //
        "Integrate[(x+1)/(x^3+(-1)*1), x]", //
        "2/3*Log[1-x]-Log[1+x+x^2]/3" //
    );
  }

  public void test133() {
    check( //
        "Integrate[(x^4+1)/(x*(x^2+1)^2), x]", //
        "1/(1+x^2)+Log[x]" //
    );
  }

  public void test134() {
    check( //
        "Integrate[1/(x^4-2*x^3), x]", //
        "1/(4*x^2)+1/(4*x)+Log[2-x]/8-Log[x]/8" //
    );
  }

  public void test135() {
    check( //
        "Integrate[(1-x^3)/(x*(x^2+1)), x]", //
        "-x+ArcTan[x]+Log[x]-Log[1+x^2]/2" //
    );
  }

  public void test136() {
    check( //
        "Integrate[1/(x^4+(-1)*1), x]", //
        "-ArcTan[x]/2-ArcTanh[x]/2" //
    );
  }

  public void test137() {
    check( //
        "Integrate[1/(x^4+1), x]", //
        "-ArcTan[1-Sqrt[2]*x]/(2*Sqrt[2])+ArcTan[1+Sqrt[2]*x]/(2*Sqrt[2])-Log[1-Sqrt[2]*x+x^2]/(4*Sqrt[2])+Log[1+Sqrt[2]*x+x^2]/(4*Sqrt[2])" //
    );
  }

  public void test138() {
    check( //
        "Integrate[x^2/(x^2+2*x+2)^2, x]", //
        "-(x*(2+x))/(2*(2+2*x+x^2))+ArcTan[1+x]" //
    );
  }

  public void test139() {
    check( //
        "Integrate[(4*x^5+(-1)*1)/(x^5+x+1)^2, x]", //
        "-x/(1+x+x^5)" //
    );
  }

  public void test140() {
    check( //
        "Integrate[1/(2*Sin[x]-Cos[x]+5), x]", //
        "x/(2*Sqrt[5])+ArcTan[(2*Cos[x]+Sin[x])/(5+2*Sqrt[5]-Cos[x]+2*Sin[x])]/Sqrt[5]" //
    );
  }

  public void test141() {
    check( //
        "Integrate[1/(1+a*Cos[x]), x]", //
        "(2*ArcTan[(Sqrt[1-a]*Tan[x/2])/Sqrt[1+a]])/Sqrt[1-a^2]" //
    );
  }

  public void test142() {
    check( //
        "Integrate[1/(1+2*Cos[x]), x]", //
        "-Log[Sqrt[3]*Cos[x/2]-Sin[x/2]]/Sqrt[3]+Log[Sqrt[3]*Cos[x/2]+Sin[x/2]]/Sqrt[3]" //
    );
  }

  public void test143() {
    check( //
        "Integrate[1/(1+Cos[x]/2), x]", //
        "(2*x)/Sqrt[3]-(4*ArcTan[Sin[x]/(2+Sqrt[3]+Cos[x])])/Sqrt[3]" //
    );
  }

  public void test144() {
    check( //
        "Integrate[Sin[x]^2/(1+Sin[x]^2), x]", //
        "x-x/Sqrt[2]-ArcTan[(Cos[x]*Sin[x])/(1+Sqrt[2]+Sin[x]^2)]/Sqrt[2]" //
    );
  }

  public void test145() {
    check( //
        "Integrate[1/(a^2*Sin[x]^2+b^2*Cos[x]^2), x]", //
        "ArcTan[(a*Tan[x])/b]/(a*b)" //
    );
  }

  public void test146() {
    check( //
        "Integrate[1/(a*Sin[x]+b*Cos[x])^2, x]", //
        "Sin[x]/(b*(b*Cos[x]+a*Sin[x]))" //
    );
  }

  public void test147() {
    check( //
        "Integrate[Sin[x]/(1+Sin[x]+Cos[x]), x]", //
        "x/2-Log[1+Cos[x]+Sin[x]]/2-Log[1+Tan[x/2]]/2" //
    );
  }

  public void test148() {
    check( //
        "Integrate[Sqrt[3-x^2], x]", //
        "1/2*x*Sqrt[3-x^2]+3/2*ArcSin[x/Sqrt[3]]" //
    );
  }

  public void test149() {
    check( //
        "Integrate[x/Sqrt[3-x^2], x]", //
        "-Sqrt[3-x^2]" //
    );
  }

  public void test150() {
    check( //
        "Integrate[Sqrt[3-x^2]/x, x]", //
        "Sqrt[3-x^2]-Sqrt[3]*ArcTanh[Sqrt[3-x^2]/Sqrt[3]]" //
    );
  }

  public void test151() {
    check( //
        "Integrate[Sqrt[x^2+x]/x, x]", //
        "Sqrt[x+x^2]+ArcTanh[x/Sqrt[x+x^2]]" //
    );
  }

  public void test152() {
    check( //
        "Integrate[Sqrt[x^2+5], x]", //
        "1/2*x*Sqrt[5+x^2]+5/2*ArcSinh[x/Sqrt[5]]" //
    );
  }

  public void test153() {
    check( //
        "Integrate[x/Sqrt[x^2+x+1], x]", //
        "Sqrt[1+x+x^2]-ArcSinh[(1+2*x)/Sqrt[3]]/2" //
    );
  }

  public void test154() {
    check( //
        "Integrate[1/Sqrt[x^2+x], x]", //
        "2*ArcTanh[x/Sqrt[x+x^2]]" //
    );
  }

  public void test155() {
    check( //
        "Integrate[Sqrt[2-x-x^2]/x^2, x]", //
        "-Sqrt[2-x-x^2]/x+ArcSin[1/3*(-1-2*x)]+ArcTanh[(4-x)/(2*Sqrt[2]*Sqrt[2-x-x^2])]/(2*Sqrt[2])" //
    );
  }

  public void test156() {
    check( //
        "Integrate[Log[t]/(t+1), t]", //
        "Log[t]*Log[1+t]+PolyLog[2,-t]" //
    );
  }

  public void test157() {
    check( //
        "Integrate[Log[E^Cos[x]], x]", //
        "-x*Cos[x]+x*Log[E^Cos[x]]+Sin[x]" //
    );
  }

  public void test158() {
    check( //
        "Integrate[E^t/t, t]", //
        "ExpIntegralEi[t]" //
    );
  }

  public void test159() {
    check( //
        "Integrate[E^(a*t)/t, t]", //
        "ExpIntegralEi[a*t]" //
    );
  }

  public void test160() {
    check( //
        "Integrate[E^t/t^2, t]", //
        "-E^t/t+ExpIntegralEi[t]" //
    );
  }

  public void test161() {
    check( //
        "Integrate[E^(1/t), t]", //
        "E^(1/t)*t-ExpIntegralEi[1/t]" //
    );
  }

  public void test162() {
    check( //
        "Integrate[1/(E^t*(t-a+(-1)*1)), t]", //
        "ExpIntegralEi[1+a-t]/E^(1+a)" //
    );
  }

  public void test163() {
    check( //
        "Integrate[t*E^t^2/(t^2+1), t]", //
        "ExpIntegralEi[1+t^2]/(2*E)" //
    );
  }

  public void test164() {
    check( //
        "Integrate[E^t/(t+1)^2, t]", //
        "-E^t/(1+t)+ExpIntegralEi[1+t]/E" //
    );
  }

  public void test165() {
    check( //
        "Integrate[E^t*Log[1+t], t]", //
        "-ExpIntegralEi[1+t]/E+E^t*Log[1+t]" //
    );
  }

  public void test166() {
    check( //
        "Integrate[t/E^t, t]", //
        "-1/E^t-t/E^t" //
    );
  }

  public void test167() {
    check( //
        "Integrate[t^2/E^t, t]", //
        "-2/E^t-(2*t)/E^t-t^2/E^t" //
    );
  }

  public void test168() {
    check( //
        "Integrate[t^3/E^t, t]", //
        "-6/E^t-(6*t)/E^t-(3*t^2)/E^t-t^3/E^t" //
    );
  }

  public void test169() {
    check( //
        "Integrate[(a1*Sin[x]+b1*Cos[x])/(a*Sin[x]+b*Cos[x]), x]", //
        "((a*a1+b*b1)*x)/(a^2+b^2)-((a1*b-a*b1)*Log[b*Cos[x]+a*Sin[x]])/(a^2+b^2)" //
    );
  }

  public void test170() {
    check( //
        "Integrate[1/Log[t], t]", //
        "LogIntegral[t]" //
    );
  }

  public void test171() {
    check( //
        "Integrate[1/Log[t]^2, t]", //
        "-t/Log[t]+LogIntegral[t]" //
    );
  }

  public void test172() {
    check( //
        "Integrate[1/Log[t]^(n+1), t]", //
        "(-Gamma[-n,-Log[t]]*(-Log[t])^n)/Log[t]^n" //
    );
  }

  public void test173() {
    check( //
        "Integrate[E^(2*t)/(t+(-1)*1), t]", //
        "E^2*ExpIntegralEi[-2*(1-t)]" //
    );
  }

  public void test174() {
    check( //
        "Integrate[E^(2*x)/(x^2-3*x+2), x]", //
        "E^4*ExpIntegralEi[-4+2*x]-E^2*ExpIntegralEi[-2+2*x]" //
    );
  }

  public void test175() {
    check( //
        "Integrate[1/Sqrt[1+t^3], t]", //
        "(2*Sqrt[2+Sqrt[3]]*(1+t)*Sqrt[(1-t+t^2)/(1+Sqrt[3]+t)^2]*EllipticF[ArcSin[(1-Sqrt[3]+t)/(1+Sqrt[3]+t)],-7-4*Sqrt[3]])/(3^(1/4)*Sqrt[(1+t)/(1+Sqrt[3]+t)^2]*Sqrt[1+t^3])" //
    );
  }
}
