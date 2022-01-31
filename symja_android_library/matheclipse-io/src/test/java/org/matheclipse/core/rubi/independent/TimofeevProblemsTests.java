package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

public class TimofeevProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public TimofeevProblemsTests(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 30;
      if (init) {
        System.out.println("Timofeev");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void test1() {
    check( //
        "Integrate[1/(a^2-b^2*x^2), x]", //
        "ArcTanh[(b*x)/a]/(a*b)" //
    );
  }

  public void test2() {
    check( //
        "Integrate[1/(a^2+b^2*x^2), x]", //
        "ArcTan[(b*x)/a]/(a*b)" //
    );
  }

  public void test3() {
    check( //
        "Integrate[Sec[2*a*x], x]", //
        "ArcTanh[Sin[2*a*x]]/(2*a)" //
    );
  }

  public void test4() {
    check( //
        "Integrate[1/(4*Sin[x/3]), x]", //
        "1/4*-3*ArcTanh[Cos[x/3]]" //
    );
  }

  public void test5() {
    check( //
        "Integrate[1/Cos[3*Pi/4-2*x], x]", //
        "-ArcTanh[Sin[Pi/4+2*x]]/2" //
    );
  }

  public void test6() {
    check( //
        "Integrate[Sec[x]*Tan[x], x]", //
        "Sec[x]" //
    );
  }

  public void test7() {
    check( //
        "Integrate[Csc[x]*Cot[x], x]", //
        "-Csc[x]" //
    );
  }

  public void test8() {
    check( //
        "Integrate[Tan[x]/Sin[2*x], x]", //
        "Tan[x]/2" //
    );
  }

  public void test9() {
    check( //
        "Integrate[1/(1+Cos[x]), x]", //
        "Sin[x]/(1+Cos[x])" //
    );
  }

  public void test10() {
    check( //
        "Integrate[1/(1-Cos[x]), x]", //
        "-Sin[x]/(1-Cos[x])" //
    );
  }

  public void test11() {
    check( //
        "Integrate[Sin[x]/(a-b*Cos[x]), x]", //
        "Log[a-b*Cos[x]]/b" //
    );
  }

  public void test12() {
    check( //
        "Integrate[Cos[x]/(a^2+b^2*Sin[x]^2), x]", //
        "ArcTan[(b*Sin[x])/a]/(a*b)" //
    );
  }

  public void test13() {
    check( //
        "Integrate[Cos[x]/(a^2-b^2*Sin[x]^2), x]", //
        "ArcTanh[(b*Sin[x])/a]/(a*b)" //
    );
  }

  public void test14() {
    check( //
        "Integrate[Sin[2*x]/(a^2+b^2*Sin[x]^2), x]", //
        "Log[a^2+b^2*Sin[x]^2]/b^2" //
    );
  }

  public void test15() {
    check( //
        "Integrate[Sin[2*x]/(a^2-b^2*Sin[x]^2), x]", //
        "-Log[a^2-b^2*Sin[x]^2]/b^2" //
    );
  }

  public void test16() {
    check( //
        "Integrate[Sin[2*x]/(a^2+b^2*Cos[x]^2), x]", //
        "-Log[a^2+b^2*Cos[x]^2]/b^2" //
    );
  }

  public void test17() {
    check( //
        "Integrate[Sin[2*x]/(a^2-b^2*Cos[x]^2), x]", //
        "Log[a^2-b^2*Cos[x]^2]/b^2" //
    );
  }

  public void test18() {
    check( //
        "Integrate[1/(4-Cos[x]^2), x]", //
        "x/(2*Sqrt[3])+ArcTan[(Cos[x]*Sin[x])/(3+2*Sqrt[3]+Sin[x]^2)]/(2*Sqrt[3])" //
    );
  }

  public void test19() {
    check( //
        "Integrate[E^x/(-1+E^(2*x)), x]", //
        "-ArcTanh[E^x]" //
    );
  }

  public void test20() {
    check( //
        "Integrate[1/(x*Log[x]), x]", //
        "Log[Log[x]]" //
    );
  }

  public void test21() {
    check( //
        "Integrate[1/(x*(1+Log[x]^2)), x]", //
        "ArcTan[Log[x]]" //
    );
  }

  public void test22() {
    check( //
        "Integrate[1/(x*(1-Log[x])), x]", //
        "-Log[1-Log[x]]" //
    );
  }

  public void test23() {
    check( //
        "Integrate[1/(x*(1+Log[x/a])), x]", //
        "Log[1+Log[x/a]]" //
    );
  }

  public void test24() {
    check( //
        "Integrate[(1-Sqrt[x]+x)^2/x^2, x]", //
        "-1/x+4/Sqrt[x]-4*Sqrt[x]+x+3*Log[x]" //
    );
  }

  public void test25() {
    check( //
        "Integrate[(2-x^(2/3))*(x+Sqrt[x])/x^(3/2), x]", //
        "4*Sqrt[x]-1/2*3*x^(2/3)-1/7*6*x^(7/6)+2*Log[x]" //
    );
  }

  public void test26() {
    check( //
        "Integrate[(2*x+(-1)*1)/(2*x+3), x]", //
        "x-2*Log[2*x+3]" //
    );
  }

  public void test27() {
    check( //
        "Integrate[(2*x+(-1)*5)/(3*x^2+(-1)*2), x]", //
        "1/12*(4-5*Sqrt[6])*Log[Sqrt[6]-3*x]+1/12*(4+5*Sqrt[6])*Log[Sqrt[6]+3*x]" //
    );
  }

  public void test28() {
    check( //
        "Integrate[(2*x+(-1)*5)/(3*x^2+2), x]", //
        "(-5*ArcTan[Sqrt[3/2]*x])/Sqrt[6]+Log[3*x^2+2]/3" //
    );
  }

  public void test29() {
    check( //
        "Integrate[Sin[x/4]*Sin[x], x]", //
        "1/3*2*Sin[1/4*3*x]-1/5*2*Sin[1/4*5*x]" //
    );
  }

  public void test30() {
    check( //
        "Integrate[Cos[3*x]*Cos[4*x], x]", //
        "Sin[x]/2+Sin[7*x]/14" //
    );
  }

  public void test31() {
    check( //
        "Integrate[Tan[x]*Tan[x-a], x]", //
        "-x+Cot[a]*Log[Cos[x-a]]-Cot[a]*Log[Cos[x]]" //
    );
  }

  public void test32() {
    check( //
        "Integrate[Sin[x]^2, x]", //
        "x/2-1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test33() {
    check( //
        "Integrate[Cos[x]^2, x]", //
        "x/2+1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test34() {
    check( //
        "Integrate[Sin[x]*Cos[x]^3, x]", //
        "-Cos[x]^4/4" //
    );
  }

  public void test35() {
    check( //
        "Integrate[Cos[x]^3/Sin[x]^4, x]", //
        "1/Sin[x]-1/(3*Sin[x]^3)" //
    );
  }

  public void test36() {
    check( //
        "Integrate[1/(Sin[x]^2*Cos[x]^2), x]", //
        "Tan[x]-Cot[x]" //
    );
  }

  public void test37() {
    check( //
        "Integrate[Cot[3*x/4]^2, x]", //
        "-x-1/3*4*Cot[1/4*3*x]" //
    );
  }

  public void test38() {
    check( //
        "Integrate[(1+Tan[2*x])^2, x]", //
        "-Log[Cos[2*x]]+Tan[2*x]/2" //
    );
  }

  public void test39() {
    check( //
        "Integrate[(Tan[x]-Cot[x])^2, x]", //
        "-4*x-Cot[x]+Tan[x]" //
    );
  }

  public void test40() {
    check( //
        "Integrate[(Tan[x]-Sec[x])^2, x]", //
        "-x-(2*Cos[x])/(1+Sin[x])" //
    );
  }

  public void test41() {
    check( //
        "Integrate[Sin[x]/(1+Sin[x]), x]", //
        "x+Cos[x]/(1+Sin[x])" //
    );
  }

  public void test42() {
    check( //
        "Integrate[Cos[x]/(1-Cos[x]), x]", //
        "-x-Sin[x]/(1-Cos[x])" //
    );
  }

  public void test43() {
    check( //
        "Integrate[(E^(x/2)+(-1)*1)^3*E^(-x/2), x]", //
        "2/E^(x/2)-6*E^(x/2)+E^x+3*x" //
    );
  }

  public void test44() {
    check( //
        "Integrate[1/(5-6*x+x^2), x]", //
        "-Log[1-x]/4+Log[5-x]/4" //
    );
  }

  public void test45() {
    check( //
        "Integrate[x^2/(13-6*x^3+x^6), x]", //
        "ArcTan[1/2*(-3+x^3)]/6" //
    );
  }

  public void test46() {
    check( //
        "Integrate[(2+x)/(-1-4*x+x^2), x]", //
        "1/10*(5-4*Sqrt[5])*Log[2-Sqrt[5]-x]+1/10*(5+4*Sqrt[5])*Log[2+Sqrt[5]-x]" //
    );
  }

  public void test47() {
    check( //
        "Integrate[1/(1+(1+x)^(1/3)), x]", //
        "-3*(1+x)^(1/3)+1/2*3*(1+x)^(2/3)+3*Log[1+(1+x)^(1/3)]" //
    );
  }

  public void test48() {
    check( //
        "Integrate[1/(Sqrt[x]*(a*x+b)), x]", //
        "(2*ArcTan[(Sqrt[a]*Sqrt[x])/Sqrt[b]])/(Sqrt[a]*Sqrt[b])" //
    );
  }

  public void test49() {
    check( //
        "Integrate[x^3*Sqrt[1+x^2], x]", //
        "-(1+x^2)^(3/2)/3+(1+x^2)^(5/2)/5" //
    );
  }

  public void test50() {
    check( //
        "Integrate[x/Sqrt[a^4-x^4], x]", //
        "ArcTan[x^2/Sqrt[a^4-x^4]]/2" //
    );
  }

  public void test51() {
    check( //
        "Integrate[1/(x*Sqrt[x^2-a^2]), x]", //
        "ArcTan[Sqrt[x^2-a^2]/a]/a" //
    );
  }

  public void test52() {
    check( //
        "Integrate[1/(x*Sqrt[a^2-x^2]), x]", //
        "-ArcTanh[Sqrt[a^2-x^2]/a]/a" //
    );
  }

  public void test53() {
    check( //
        "Integrate[1/(x*Sqrt[x^2+a^2]), x]", //
        "-ArcTanh[Sqrt[x^2+a^2]/a]/a" //
    );
  }

  public void test54() {
    check( //
        "Integrate[1/Sqrt[2+x-x^2], x]", //
        "-ArcSin[1/3*(1-2*x)]" //
    );
  }

  public void test55() {
    check( //
        "Integrate[1/Sqrt[5-4*x+3*x^2], x]", //
        "-ArcSinh[(2-3*x)/Sqrt[11]]/Sqrt[3]" //
    );
  }

  public void test56() {
    check( //
        "Integrate[1/Sqrt[x-x^2], x]", //
        "-ArcSin[1-2*x]" //
    );
  }

  public void test57() {
    check( //
        "Integrate[(1+2*x)/Sqrt[2+x-x^2], x]", //
        "-2*Sqrt[2+x-x^2]-2*ArcSin[1/3*(1-2*x)]" //
    );
  }

  public void test58() {
    check( //
        "Integrate[1/(x*Sqrt[2+x-x^2]), x]", //
        "-ArcTanh[(4+x)/(2*Sqrt[2]*Sqrt[2+x-x^2])]/Sqrt[2]" //
    );
  }

  public void test59() {
    check( //
        "Integrate[1/((x+(-1)*2)*Sqrt[2+x-x^2]), x]", //
        "2*Sqrt[2+x-x^2]/(3*(x+(-1)*2))" //
    );
  }

  public void test60() {
    check( //
        "Integrate[(2+3*Sin[x])/(Sin[x]*(1-Cos[x])), x]", //
        "-ArcTanh[Cos[x]]-1/(1-Cos[x])-(3*Sin[x])/(1-Cos[x])" //
    );
  }

  public void test61() {
    check( //
        "Integrate[1/(2+3*Cos[x]^2), x]", //
        "x/Sqrt[10]-ArcTan[3*Cos[x]*Sin[x]/(2+Sqrt[10]+3*Cos[x]^2)]/Sqrt[10]" //
    );
  }

  public void test62() {
    check( //
        "Integrate[(1-Tan[x])/Sin[2*x], x]", //
        "Log[Tan[x]]/2-Tan[x]/2" //
    );
  }

  public void test63() {
    check( //
        "Integrate[(1+Tan[x]^2)/(1-Tan[x]^2), x]", //
        "ArcTanh[2*Cos[x]*Sin[x]]/2" //
    );
  }

  public void test64() {
    check( //
        "Integrate[(a^2-4*Cos[x]^2)^(3/4)*Sin[2*x], x]", //
        "(a^2-4*Cos[x]^2)^(7/4)/7" //
    );
  }

  public void test65() {
    check( //
        "Integrate[Sin[2*x]/(a^2-4*Sin[x]^2)^(1/3), x]", //
        "-3/8*(a^2-4*Sin[x]^2)^(2/3)" //
    );
  }

  public void test66() {
    check( //
        "Integrate[1/Sqrt[a^(2*x)+(-1)*1], x]", //
        "ArcTan[Sqrt[a^(2*x)+(-1)*1]]/Log[a]" //
    );
  }

  public void test67() {
    check( //
        "Integrate[E^(x/2)/Sqrt[E^x+(-1)*1], x]", //
        "2*ArcTanh[E^(x/2)/Sqrt[E^x+(-1)*1]]" //
    );
  }

  public void test68() {
    check( //
        "Integrate[ArcTan[x]^n/(1+x^2), x]", //
        "ArcTan[x]^(n+1)/(n+1)" //
    );
  }

  public void test69() {
    check( //
        "Integrate[ArcSin[x/a]^(3/2)/Sqrt[a^2-x^2], x]", //
        "(2*a*Sqrt[1-x^2/a^2]*ArcSin[x/a]^(5/2))/(5*Sqrt[a^2-x^2])" //
    );
  }

  public void test70() {
    check( //
        "Integrate[1/(ArcCos[x]^3*Sqrt[1-x^2]), x]", //
        "1/(2*ArcCos[x]^2)" //
    );
  }

  public void test71() {
    check( //
        "Integrate[x*Log[x]^2, x]", //
        "x^2/4-1/2*x^2*Log[x]+1/2*x^2*Log[x]^2" //
    );
  }

  public void test72() {
    check( //
        "Integrate[Log[x]/x^5, x]", //
        "-1/(16*x^4)-Log[x]/(4*x^4)" //
    );
  }

  public void test73() {
    check( //
        "Integrate[x^2*Log[(x+(-1)*1)/x], x]", //
        "-x/3-x^2/6+x^3*Log[(x+(-1)*1)/x]/3-Log[x+(-1)*1]/3" //
    );
  }

  public void test74() {
    check( //
        "Integrate[Cos[x]^5, x]", //
        "Sin[x]-1/3*2*Sin[x]^3+Sin[x]^5/5" //
    );
  }

  public void test75() {
    check( //
        "Integrate[Sin[x]^2*Cos[x]^4, x]", //
        "x/16+1/16*Cos[x]*Sin[x]+1/24*Cos[x]^3*Sin[x]-1/6*Cos[x]^5*Sin[x]" //
    );
  }

  public void test76() {
    check( //
        "Integrate[1/Sin[x]^5, x]", //
        "1/8*-3*ArcTanh[Cos[x]]-1/8*3*Cot[x]*Csc[x]-1/4*Cot[x]*Csc[x]^3" //
    );
  }

  public void test77() {
    check( //
        "Integrate[Sin[x]/E^x, x]", //
        "-Cos[x]/(2*E^x)-Sin[x]/(2*E^x)" //
    );
  }

  public void test78() {
    check( //
        "Integrate[E^(2*x)*Sin[3*x], x]", //
        "1/13*-3*E^(2*x)*Cos[3*x]+1/13*2*E^(2*x)*Sin[3*x]" //
    );
  }

  public void test79() {
    check( //
        "Integrate[a^x*Cos[x], x]", //
        "(a^x*Cos[x]*Log[a])/(1+Log[a]^2)+(a^x*Sin[x])/(1+Log[a]^2)" //
    );
  }

  public void test80() {
    check( //
        "Integrate[Cos[Log[x]], x]", //
        "1/2*x*Cos[Log[x]]+1/2*x*Sin[Log[x]]" //
    );
  }

  public void test81() {
    check( //
        "Integrate[Sec[x]^2*Log[Cos[x]], x]", //
        "-x+Tan[x]+Log[Cos[x]]*Tan[x]" //
    );
  }

  public void test82() {
    check( //
        "Integrate[x*Tan[x]^2, x]", //
        "-x^2/2+Log[Cos[x]]+x*Tan[x]" //
    );
  }

  public void test83() {
    check( //
        "Integrate[ArcSin[x]/x^2, x]", //
        "-ArcSin[x]/x-ArcTanh[Sqrt[1-x^2]]" //
    );
  }

  public void test84() {
    check( //
        "Integrate[ArcSin[x]^2, x]", //
        "-2*x+2*Sqrt[1-x^2]*ArcSin[x]+x*ArcSin[x]^2" //
    );
  }

  public void test85() {
    check( //
        "Integrate[ArcTan[x]*x^2/(1+x^2), x]", //
        "x*ArcTan[x]-ArcTan[x]^2/2-Log[1+x^2]/2" //
    );
  }

  public void test86() {
    check( //
        "Integrate[ArcCos[Sqrt[x/(1+x)]], x]", //
        "(1+x)*(Sqrt[1/(1+x)]*Sqrt[x/(1+x)]+ArcCos[Sqrt[x/(1+x)]])" //
    );
  }

  public void test87() {
    check( //
        "Integrate[(3*x^2+2*x)^3, x]", //
        "2*x^4+1/5*36*x^5+9*x^6+1/7*27*x^7" //
    );
  }

  public void test88() {
    check( //
        "Integrate[(3*x^2+2*x+(-1)*1)^2*(x+(-1)*1), x]", //
        "-x+1/2*5*x^2-1/3*2*x^3-1/2*7*x^4+1/5*3*x^5+1/2*3*x^6" //
    );
  }

  public void test89() {
    check( //
        "Integrate[(a+b*x^k)^n*x^(k+(-1)*1), x]", //
        "(a+b*x^k)^(1+n)/(b*k*(1+n))" //
    );
  }

  public void test90() {
    check( //
        "Integrate[x^3/(1+2*x), x]", //
        "x/8-x^2/8+x^3/6-Log[1+2*x]/16" //
    );
  }

  public void test91() {
    check( //
        "Integrate[x^6/(2+3*x^2), x]", //
        "1/27*4*x-1/27*2*x^3+x^5/15-4/27*Sqrt[2/3]*ArcTan[Sqrt[3/2]*x]" //
    );
  }

  public void test92() {
    check( //
        "Integrate[1/(3*x^2-7*x+2), x]", //
        "-Log[1-3*x]/5+Log[2-x]/5" //
    );
  }

  public void test93() {
    check( //
        "Integrate[(3*x+(-1)*1)/(x^2-x+1), x]", //
        "-ArcTan[(1-2*x)/Sqrt[3]]/Sqrt[3]+3/2*Log[1-x+x^2]" //
    );
  }

  public void test94() {
    check( //
        "Integrate[x^2/(5+2*x+x^2), x]", //
        "x-3/2*ArcTan[1/2*(1+x)]-Log[5+2*x+x^2]" //
    );
  }

  public void test95() {
    check( //
        "Integrate[(6*x^4-5*x^3+4*x^2)/(2*x^2-x+1), x]", //
        "-x^2/2+x^3-ArcTan[(1-4*x)/Sqrt[7]]/(2*Sqrt[7])+Log[1-x+2*x^2]/4" //
    );
  }

  public void test96() {
    check( //
        "Integrate[(x^2+x+(-1)*1)/(x^3+x^2-6*x), x]", //
        "Log[2-x]/2+Log[x]/6+Log[3+x]/3" //
    );
  }

  public void test97() {
    check( //
        "Integrate[(5*x^2-7*a*x+11*a^2)/(x^3-6*a*x^2+11*a^2*x-6*a^3), x]", //
        "9/2*Log[a-x]-17*Log[2*a-x]+35/2*Log[3*a-x]" //
    );
  }

  public void test98() {
    check( //
        "Integrate[(x^2-x+2)/(x^4-5*x^2+4), x]", //
        "-Log[1-x]/3+Log[2-x]/3+2/3*Log[1+x]-2/3*Log[2+x]" //
    );
  }

  public void test99() {
    check( //
        "Integrate[(2*x^2+(-1)*5)/(x^4-5*x^2+6), x]", //
        "-ArcTanh[x/Sqrt[2]]/Sqrt[2]-ArcTanh[x/Sqrt[3]]/Sqrt[3]" //
    );
  }

  public void test100() {
    check( //
        "Integrate[1/((x+(-1)*1)*(x+(-1)*2)*(x+(-1)*3)*(x+(-1)*4)), x]", //
        "-Log[1-x]/6+Log[2-x]/2-Log[3-x]/2+Log[4-x]/6" //
    );
  }

  public void test101() {
    check( //
        "Integrate[(x^2+1)/(x+(-1)*1)^3, x]", //
        "-1/(1-x)^2+2/(1-x)+Log[1-x]" //
    );
  }

  public void test102() {
    check( //
        "Integrate[x^5/(3+x)^2, x]", //
        "-108*x+1/2*27*x^2-2*x^3+x^4/4+243/(3+x)+405*Log[3+x]" //
    );
  }

  public void test103() {
    check( //
        "Integrate[(5*x^3+(-1)*2)/(x^4-8*x^3+18*x^2+(-1)*27), x]", //
        "-133/(8*(3-x)^2)+407/(16*(3-x))+313/64*Log[3-x]+7/64*Log[1+x]" //
    );
  }

  public void test104() {
    check( //
        "Integrate[(x^3-6*x^2+3*x+(-1)*9)/((x+3)^2*(x+4)^2), x]", //
        "99/(3+x)+181/(4+x)+264*Log[3+x]-263*Log[4+x]" //
    );
  }

  public void test105() {
    check( //
        "Integrate[(x^3+x^2+2)/(x*(x^2+(-1)*1)^2), x]", //
        "(3+x)/(2*(1-x^2))-3/4*Log[1-x]+2*Log[x]-5/4*Log[1+x]" //
    );
  }

  public void test106() {
    check( //
        "Integrate[1/(x^3-x^4-x^5+x^6), x]", //
        "1/(2*(1-x))-1/(2*x^2)-1/x-7/4*Log[1-x]+2*Log[x]-Log[1+x]/4" //
    );
  }

  public void test107() {
    check( //
        "Integrate[(x^4+1)/(x^3-x^2+x+(-1)*1), x]", //
        "x+x^2/2-ArcTan[x]+Log[1-x]-Log[1+x^2]/2" //
    );
  }

  public void test108() {
    check( //
        "Integrate[1/(x*(1+x)*(1+x^2)), x]", //
        "-ArcTan[x]/2+Log[x]-Log[1+x]/2-Log[1+x^2]/4" //
    );
  }

  public void test109() {
    check( //
        "Integrate[x^2/(x^4+x^2+(-1)*2), x]", //
        "1/3*Sqrt[2]*ArcTan[x/Sqrt[2]]-ArcTanh[x]/3" //
    );
  }

  public void test110() {
    check( //
        "Integrate[(x^3+4*x^2+6*x)/(x^4+2*x^3+3*x^2+4*x+2), x]", //
        "1/(1+x)+4/3*Sqrt[2]*ArcTan[x/Sqrt[2]]-Log[1+x]/3+2/3*Log[2+x^2]" //
    );
  }

  public void test111() {
    check( //
        "Integrate[x/((1+x)*(1+2*x)^2*(1+x^2)), x]", //
        "2/(5*(1+2*x))+ArcTan[x]/50-Log[1+x]/2+16/25*Log[1+2*x]-7/100*Log[1+x^2]" //
    );
  }

  public void test112() {
    check( //
        "Integrate[(3*x^2+x+(-1)*2)/((x+(-1)*1)^3*(x^2+1)), x]", //
        "-1/(2*(1-x)^2)+5/(2*(1-x))-ArcTan[x]-3/2*Log[1-x]+3/4*Log[1+x^2]" //
    );
  }

  public void test113() {
    check( //
        "Integrate[1/(x^4+x^2+1), x]", //
        "-ArcTan[(1-2*x)/Sqrt[3]]/(2*Sqrt[3])+ArcTan[(1+2*x)/Sqrt[3]]/(2*Sqrt[3])-Log[1-x+x^2]/4+Log[1+x+x^2]/4" //
    );
  }

  public void test114() {
    check( //
        "Integrate[(2*x^3+3)/(x^5-9*x), x]", //
        "ArcTan[x/Sqrt[3]]/Sqrt[3]-ArcTanh[x/Sqrt[3]]/Sqrt[3]-Log[x]/3+Log[9-x^4]/12" //
    );
  }

  public void test115() {
    check( //
        "Integrate[(5*x^3+8*x+(-1)*20)/((x+(-1)*4)^3*(x^2-4*x+8)), x]", //
        "-83/(4*(4-x)^2)+41/(4*(4-x))-3/16*ArcTan[1-x/2]-45/16*Log[4-x]+45/32*Log[8-4*x+x^2]" //
    );
  }

  public void test116() {
    check( //
        "Integrate[1/((x^2+1)*(x^2+2)*(x^2+3)*(x^2+4)), x]", //
        "-ArcTan[x/2]/12+ArcTan[x]/6-ArcTan[x/Sqrt[2]]/(2*Sqrt[2])+ArcTan[x/Sqrt[3]]/(2*Sqrt[3])" //
    );
  }

  public void test117() {
    check( //
        "Integrate[x/((x^2+1)*(x^2+2)*(x^2+3)*(x^2+4)), x]", //
        "Log[1+x^2]/12-Log[2+x^2]/4+Log[3+x^2]/4-Log[4+x^2]/12" //
    );
  }

  public void test118() {
    check( //
        "Integrate[1/(a^3+x^3), x]", //
        "-ArcTan[(a-2*x)/(Sqrt[3]*a)]/(Sqrt[3]*a^2)+Log[a+x]/(3*a^2)-Log[a^2-a*x+x^2]/(6*a^2)" //
    );
  }

  public void test119() {
    check( //
        "Integrate[x/(a^3+x^3), x]", //
        "-ArcTan[(a-2*x)/(Sqrt[3]*a)]/(Sqrt[3]*a)-Log[a+x]/(3*a)+Log[a^2-a*x+x^2]/(6*a)" //
    );
  }

  public void test120() {
    check( //
        "Integrate[x^2/(a^3+x^3), x]", //
        "Log[a^3+x^3]/3" //
    );
  }

  public void test121() {
    check( //
        "Integrate[1/(x*(a^3+x^3)), x]", //
        "Log[x]/a^3-Log[a^3+x^3]/(3*a^3)" //
    );
  }

  public void test122() {
    check( //
        "Integrate[1/(x^2*(a^3+x^3)), x]", //
        "-1/(a^3*x)+ArcTan[(a-2*x)/(Sqrt[3]*a)]/(Sqrt[3]*a^4)+Log[a+x]/(3*a^4)-Log[a^2-a*x+x^2]/(6*a^4)" //
    );
  }

  public void test123() {
    check( //
        "Integrate[1/(x^3*(a^3+x^3)), x]", //
        "-1/(2*a^3*x^2)+ArcTan[(a-2*x)/(Sqrt[3]*a)]/(Sqrt[3]*a^5)-Log[a+x]/(3*a^5)+Log[a^2-a*x+x^2]/(6*a^5)" //
    );
  }

  public void test124() {
    check( //
        "Integrate[1/(x^4*(a^3+x^3)), x]", //
        "-1/(3*a^3*x^3)-Log[x]/a^6+Log[a^3+x^3]/(3*a^6)" //
    );
  }

  public void test125() {
    check( //
        "Integrate[1/(x^5*(a^3+x^3)), x]", //
        "-1/(4*a^3*x^4)+1/(a^6*x)-ArcTan[(a-2*x)/(Sqrt[3]*a)]/(Sqrt[3]*a^7)-Log[a+x]/(3*a^7)+Log[a^2-a*x+x^2]/(6*a^7)" //
    );
  }

  public void test126() {
    check( //
        "Integrate[1/(x^m*(a^3+x^3)), x]", //
        "(x^(1-m)*Hypergeometric2F1[1,1/3*(1-m),1/3*(4-m),-x^3/a^3])/(a^3*(1-m))" //
    );
  }

  public void test127() {
    check( //
        "Integrate[1/(a^4-x^4), x]", //
        "ArcTan[x/a]/(2*a^3)+ArcTanh[x/a]/(2*a^3)" //
    );
  }

  public void test128() {
    check( //
        "Integrate[x/(a^4-x^4), x]", //
        "ArcTanh[x^2/a^2]/(2*a^2)" //
    );
  }

  public void test129() {
    check( //
        "Integrate[1/(x*(a^4-x^4)), x]", //
        "Log[x]/a^4-Log[a^4-x^4]/(4*a^4)" //
    );
  }

  public void test130() {
    check( //
        "Integrate[1/(x^2*(a^4-x^4)), x]", //
        "-1/(a^4*x)-ArcTan[x/a]/(2*a^5)+ArcTanh[x/a]/(2*a^5)" //
    );
  }

  public void test131() {
    check( //
        "Integrate[1/(x^3*(a^4-x^4)), x]", //
        "-1/(2*a^4*x^2)+ArcTanh[x^2/a^2]/(2*a^6)" //
    );
  }

  public void test132() {
    check( //
        "Integrate[1/(x^4*(a^4-x^4)), x]", //
        "-1/(3*a^4*x^3)+ArcTan[x/a]/(2*a^7)+ArcTanh[x/a]/(2*a^7)" //
    );
  }

  public void test133() {
    check( //
        "Integrate[1/(x^m*(a^4-x^4)), x]", //
        "(x^(1-m)*Hypergeometric2F1[1,1/4*(1-m),1/4*(5-m),x^4/a^4])/(a^4*(1-m))" //
    );
  }

  public void test134() {
    check( //
        "Integrate[x/(a^4+x^4), x]", //
        "ArcTan[x^2/a^2]/(2*a^2)" //
    );
  }

  public void test135() {
    check( //
        "Integrate[x^2/(a^4+x^4), x]", //
        "-ArcTan[1-(Sqrt[2]*x)/a]/(2*Sqrt[2]*a)+ArcTan[1+(Sqrt[2]*x)/a]/(2*Sqrt[2]*a)+Log[a^2-Sqrt[2]*a*x+x^2]/(4*Sqrt[2]*a)-Log[a^2+Sqrt[2]*a*x+x^2]/(4*Sqrt[2]*a)" //
    );
  }

  public void test136() {
    check( //
        "Integrate[1/(a^5+x^5), x]", //
        "-(Sqrt[1/2*(5+Sqrt[5])]*ArcTan[((1-Sqrt[5])*a-4*x)/(Sqrt[2*(5+Sqrt[5])]*a)])/(5*a^4)-(Sqrt[1/2*(5-Sqrt[5])]*ArcTan[(Sqrt[1/10*(5+Sqrt[5])]*((1+Sqrt[5])*a-4*x))/(2*a)])/(5*a^4)+Log[a+x]/(5*a^4)-((1-Sqrt[5])*Log[a^2-1/2*(1-Sqrt[5])*a*x+x^2])/(20*a^4)-((1+Sqrt[5])*Log[a^2-1/2*(1+Sqrt[5])*a*x+x^2])/(20*a^4)" //
    );
  }

  public void test137() {
    check( //
        "Integrate[x/(a^5+x^5), x]", //
        "(Sqrt[1/2*(5-Sqrt[5])]*ArcTan[((1-Sqrt[5])*a-4*x)/(Sqrt[2*(5+Sqrt[5])]*a)])/(5*a^3)-(Sqrt[1/2*(5+Sqrt[5])]*ArcTan[(Sqrt[1/10*(5+Sqrt[5])]*((1+Sqrt[5])*a-4*x))/(2*a)])/(5*a^3)-Log[a+x]/(5*a^3)+((1+Sqrt[5])*Log[a^2-1/2*(1-Sqrt[5])*a*x+x^2])/(20*a^3)+((1-Sqrt[5])*Log[a^2-1/2*(1+Sqrt[5])*a*x+x^2])/(20*a^3)" //
    );
  }

  public void test138() {
    check( //
        "Integrate[x^2/(a^5+x^5), x]", //
        "(Sqrt[1/2*(5-Sqrt[5])]*ArcTan[((1-Sqrt[5])*a-4*x)/(Sqrt[2*(5+Sqrt[5])]*a)])/(5*a^2)-(Sqrt[1/2*(5+Sqrt[5])]*ArcTan[(Sqrt[1/10*(5+Sqrt[5])]*((1+Sqrt[5])*a-4*x))/(2*a)])/(5*a^2)+Log[a+x]/(5*a^2)-((1+Sqrt[5])*Log[a^2-1/2*(1-Sqrt[5])*a*x+x^2])/(20*a^2)-((1-Sqrt[5])*Log[a^2-1/2*(1+Sqrt[5])*a*x+x^2])/(20*a^2)" //
    );
  }

  public void test139() {
    check( //
        "Integrate[x^3/(a^5+x^5), x]", //
        "-(Sqrt[1/2*(5+Sqrt[5])]*ArcTan[((1-Sqrt[5])*a-4*x)/(Sqrt[2*(5+Sqrt[5])]*a)])/(5*a)-(Sqrt[1/2*(5-Sqrt[5])]*ArcTan[(Sqrt[1/10*(5+Sqrt[5])]*((1+Sqrt[5])*a-4*x))/(2*a)])/(5*a)-Log[a+x]/(5*a)+((1-Sqrt[5])*Log[a^2-1/2*(1-Sqrt[5])*a*x+x^2])/(20*a)+((1+Sqrt[5])*Log[a^2-1/2*(1+Sqrt[5])*a*x+x^2])/(20*a)" //
    );
  }

  public void test140() {
    check( //
        "Integrate[x^4/(a^5+x^5), x]", //
        "Log[a^5+x^5]/5" //
    );
  }

  public void test141() {
    check( //
        "Integrate[1/(x*(a^5+x^5)), x]", //
        "Log[x]/a^5-Log[a^5+x^5]/(5*a^5)" //
    );
  }

  public void test142() {
    check( //
        "Integrate[1/(x^2*(a^5+x^5)), x]", //
        "-1/(a^5*x)+(Sqrt[1/2*(5+Sqrt[5])]*ArcTan[((1-Sqrt[5])*a-4*x)/(Sqrt[2*(5+Sqrt[5])]*a)])/(5*a^6)+(Sqrt[1/2*(5-Sqrt[5])]*ArcTan[(Sqrt[1/10*(5+Sqrt[5])]*((1+Sqrt[5])*a-4*x))/(2*a)])/(5*a^6)+Log[a+x]/(5*a^6)-((1-Sqrt[5])*Log[a^2-1/2*(1-Sqrt[5])*a*x+x^2])/(20*a^6)-((1+Sqrt[5])*Log[a^2-1/2*(1+Sqrt[5])*a*x+x^2])/(20*a^6)" //
    );
  }

  public void test143() {
    check( //
        "Integrate[1/(x^3*(a^5+x^5)), x]", //
        "-1/(2*a^5*x^2)-(Sqrt[1/2*(5-Sqrt[5])]*ArcTan[((1-Sqrt[5])*a-4*x)/(Sqrt[2*(5+Sqrt[5])]*a)])/(5*a^7)+(Sqrt[1/2*(5+Sqrt[5])]*ArcTan[(Sqrt[1/10*(5+Sqrt[5])]*((1+Sqrt[5])*a-4*x))/(2*a)])/(5*a^7)-Log[a+x]/(5*a^7)+((1+Sqrt[5])*Log[a^2-1/2*(1-Sqrt[5])*a*x+x^2])/(20*a^7)+((1-Sqrt[5])*Log[a^2-1/2*(1+Sqrt[5])*a*x+x^2])/(20*a^7)" //
    );
  }

  public void test144() {
    check( //
        "Integrate[1/(x^4*(a^5+x^5)), x]", //
        "-1/(3*a^5*x^3)-(Sqrt[1/2*(5-Sqrt[5])]*ArcTan[((1-Sqrt[5])*a-4*x)/(Sqrt[2*(5+Sqrt[5])]*a)])/(5*a^8)+(Sqrt[1/2*(5+Sqrt[5])]*ArcTan[(Sqrt[1/10*(5+Sqrt[5])]*((1+Sqrt[5])*a-4*x))/(2*a)])/(5*a^8)+Log[a+x]/(5*a^8)-((1+Sqrt[5])*Log[a^2-1/2*(1-Sqrt[5])*a*x+x^2])/(20*a^8)-((1-Sqrt[5])*Log[a^2-1/2*(1+Sqrt[5])*a*x+x^2])/(20*a^8)" //
    );
  }

  public void test145() {
    check( //
        "Integrate[1/(x^m*(a^5+x^5)), x]", //
        "(x^(1-m)*Hypergeometric2F1[1,1/5*(1-m),1/5*(6-m),-x^5/a^5])/(a^5*(1-m))" //
    );
  }

  public void test146() {
    check( //
        "Integrate[(x^4+1)/(x^6+1), x]", //
        "-ArcTan[Sqrt[3]-2*x]/3+1/3*2*ArcTan[x]+ArcTan[Sqrt[3]+2*x]/3" //
    );
  }

  public void test147() {
    check( //
        "Integrate[1/(x^2+3*x+5)^3, x]", //
        "(3+2*x)/(22*(5+3*x+x^2)^2)+(3*(3+2*x))/(121*(5+3*x+x^2))+(12*ArcTan[(3+2*x)/Sqrt[11]])/(121*Sqrt[11])" //
    );
  }

  public void test148() {
    check( //
        "Integrate[(x^4+x^2+1)/(x^2+1)^4, x]", //
        "x/(6*(1+x^2)^3)-x/(24*(1+x^2)^2)+(7*x)/(16*(1+x^2))+1/16*7*ArcTan[x]" //
    );
  }

  public void test149() {
    check( //
        "Integrate[(A*x+B)/(a*x^2+2*b*x+c)^2, x]", //
        "-(b*B-A*c-(A*b-a*B)*x)/(2*(b^2-a*c)*(c+2*b*x+a*x^2))-((A*b-a*B)*ArcTanh[(b+a*x)/Sqrt[b^2-a*c]])/(2*(b^2-a*c)^(3/2))" //
    );
  }

  public void test150() {
    check( //
        "Integrate[(5*x^3-27*x^2+55*x+(-1)*41)/(x^2-4*x+5)^2, x]", //
        "(1-x)/(5-4*x+x^2)-2*ArcTan[2-x]+5/2*Log[5-4*x+x^2]" //
    );
  }

  public void test151() {
    check( //
        "Integrate[1/(x^3+(-1)*1)^2, x]", //
        "x/(3*(1-x^3))+(2*ArcTan[(1+2*x)/Sqrt[3]])/(3*Sqrt[3])-2/9*Log[1-x]+Log[1+x+x^2]/9" //
    );
  }

  public void test152() {
    check( //
        "Integrate[(3*x^4+4)/(x^2*(x^2+1)^3), x]", //
        "-4/x-(7*x)/(4*(1+x^2)^2)-(25*x)/(8*(1+x^2))-1/8*57*ArcTan[x]" //
    );
  }

  public void test153() {
    check( //
        "Integrate[x/(x^6+1), x]", //
        "-ArcTan[(1-2*x^2)/Sqrt[3]]/(2*Sqrt[3])+Log[1+x^2]/6-Log[1-x^2+x^4]/12" //
    );
  }

  public void test154() {
    check( //
        "Integrate[(x^(n+(-1)*1)+(-1)*1)/(x^n-n*x), x]", //
        "Log[x^n-n*x]/n" //
    );
  }

  public void test155() {
    check( //
        "Integrate[x^3/(3*x^4-2*x^2+1), x]", //
        "-ArcTan[(1-3*x^2)/Sqrt[2]]/(6*Sqrt[2])+Log[1-2*x^2+3*x^4]/12" //
    );
  }

  public void test156() {
    check( //
        "Integrate[x^5/(3*x^4+x^2+(-1)*4), x]", //
        "x^2/6+Log[1-x^2]/14-8/63*Log[4+3*x^2]" //
    );
  }

  public void test157() {
    check( //
        "Integrate[x^2/(9-10*x^3+x^6), x]", //
        "-Log[1-x^3]/24+Log[9-x^3]/24" //
    );
  }

  public void test158() {
    check( //
        "Integrate[(x^3-4*x^2+1)/(x+(-1)*2)^4, x]", //
        "-7/(3*(2-x)^3)+2/(2-x)^2+2/(2-x)+Log[2-x]" //
    );
  }

  public void test159() {
    check( //
        "Integrate[x^3/(x+(-1)*1)^12, x]", //
        "1/(11*(1-x)^11)-3/(10*(1-x)^10)+1/(3*(1-x)^9)-1/(8*(1-x)^8)" //
    );
  }

  public void test160() {
    check( //
        "Integrate[(x^4-3*x)/(1+2*x)^5, x]", //
        "-25/(128*(1+2*x)^4)+7/(24*(1+2*x)^3)-3/(32*(1+2*x)^2)+1/(8*(1+2*x))+Log[1+2*x]/32" //
    );
  }

  public void test161() {
    check( //
        "Integrate[1/((x+1)^3*(x+(-1)*1)^2), x]", //
        "1/(8*(1-x))-1/(8*(1+x)^2)-1/(4*(1+x))+1/8*3*ArcTanh[x]" //
    );
  }

  public void test162() {
    check( //
        "Integrate[1/(x^2*(5-6*x)^2), x]", //
        "6/(25*(5-6*x))-1/(25*x)-12/125*Log[5-6*x]+1/125*12*Log[x]" //
    );
  }

  public void test163() {
    check( //
        "Integrate[1/(x^2-2*x+(-1)*3)^3, x]", //
        "(1-x)/(16*(3+2*x-x^2)^2)+(3*(1-x))/(128*(3+2*x-x^2))+3/512*Log[3-x]-3/512*Log[1+x]" //
    );
  }

  public void test164() {
    check( //
        "Integrate[1/(x^2-4*x+13)^3, x]", //
        "-(2-x)/(36*(13-4*x+x^2)^2)-(2-x)/(216*(13-4*x+x^2))+ArcTan[1/3*(-2+x)]/648" //
    );
  }

  public void test165() {
    check( //
        "Integrate[1/((x+2)^3*(x+3)^4), x]", //
        "-1/(2*(2+x)^2)+4/(2+x)+1/(3*(3+x)^3)+3/(2*(3+x)^2)+6/(3+x)+10*Log[2+x]-10*Log[3+x]" //
    );
  }

  public void test166() {
    check( //
        "Integrate[x^6/(x^2+(-1)*2)^2, x]", //
        "4*x+x^3/3-(2*x)/(x^2+(-1)*2)-5*Sqrt[2]*ArcTanh[x/Sqrt[2]]" //
    );
  }

  public void test167() {
    check( //
        "Integrate[x^8/(x^2+4)^4, x]", //
        "1/16*35*x-x^7/(6*(4+x^2)^3)-(7*x^5)/(24*(4+x^2)^2)-(35*x^3)/(48*(4+x^2))-35/8*ArcTan[x/2]" //
    );
  }

  public void test168() {
    check( //
        "Integrate[(7*x+(-1)*4)/(3*x^2+2*x+5)^2, x]", //
        "-(39+19*x)/(28*(5+2*x+3*x^2))-(19*ArcTan[(1+3*x)/Sqrt[14]])/(28*Sqrt[14])" //
    );
  }

  public void test169() {
    check( //
        "Integrate[(5-4*x)/(3*x^2-4*x+(-1)*2)^2, x]", //
        "-(18-7*x)/(20*(2+4*x-3*x^2))-(7*ArcTanh[(2-3*x)/Sqrt[10]])/(20*Sqrt[10])" //
    );
  }

  public void test170() {
    check( //
        "Integrate[x^5/(x^4+1)^3, x]", //
        "-x^2/(8*(1+x^4)^2)+x^2/(16*(1+x^4))+ArcTan[x^2]/16" //
    );
  }

  public void test171() {
    check( //
        "Integrate[x*(x^2+1)^3/(x^4+2*x^2+2)^2, x]", //
        "1/(4*(x^4+2*x^2+2))+Log[x^4+2*x^2+2]/4" //
    );
  }

  public void test172() {
    check( //
        "Integrate[x^3/(a^4+x^4)^3, x]", //
        "-1/(8*(a^4+x^4)^2)" //
    );
  }

  public void test173() {
    check( //
        "Integrate[1/(x*(a^4+x^4)^3), x]", //
        "1/(8*a^4*(a^4+x^4)^2)+1/(4*a^8*(a^4+x^4))+Log[x]/a^12-Log[a^4+x^4]/(4*a^12)" //
    );
  }

  public void test174() {
    check( //
        "Integrate[1/(x^2*(a^4+x^4)^3), x]", //
        "-45/(32*a^12*x)+1/(8*a^4*x*(a^4+x^4)^2)+9/(32*a^8*x*(a^4+x^4))+(45*ArcTan[1-(Sqrt[2]*x)/a])/(64*Sqrt[2]*a^13)-(45*ArcTan[1+(Sqrt[2]*x)/a])/(64*Sqrt[2]*a^13)-(45*Log[a^2-Sqrt[2]*a*x+x^2])/(128*Sqrt[2]*a^13)+(45*Log[a^2+Sqrt[2]*a*x+x^2])/(128*Sqrt[2]*a^13)" //
    );
  }

  public void test175() {
    check( //
        "Integrate[1/(x^3*(a^4+x^4)^3), x]", //
        "-15/(16*a^12*x^2)+1/(8*a^4*x^2*(a^4+x^4)^2)+5/(16*a^8*x^2*(a^4+x^4))-(15*ArcTan[x^2/a^2])/(16*a^14)" //
    );
  }

  public void test176() {
    check( //
        "Integrate[x^14/(3+2*x^5)^3, x]", //
        "-9/(80*(3+2*x^5)^2)+3/(20*(3+2*x^5))+Log[3+2*x^5]/40" //
    );
  }

  public void test177() {
    check( //
        "Integrate[x^6/(3+2*x^5)^3, x]", //
        "If[$VersionNumber<9,-x^2/(20*(3+2*x^5)^2)+x^2/(150*(3+2*x^5))-(Sqrt[5+Sqrt[5]]*ArcTan[Sqrt[1/5*(5+2*Sqrt[5])]-(2*2^(7/10)*x)/(3^(1/5)*Sqrt[5-Sqrt[5]])])/(250*2^(9/10)*3^(3/5))-(Sqrt[5-Sqrt[5]]*ArcTan[Sqrt[1/5*(5-2*Sqrt[5])]+(2*2^(7/10)*x)/(3^(1/5)*Sqrt[5+Sqrt[5]])])/(250*2^(9/10)*3^(3/5))-Log[3^(1/5)+2^(1/5)*x]/(250*2^(2/5)*3^(3/5))+((1+Sqrt[5])*Log[2^(3/5)*3^(2/5)-(3/2)^(1/5)*(1-Sqrt[5])*x+2*x^2])/(1000*2^(2/5)*3^(3/5))+((1-Sqrt[5])*Log[2^(3/5)*3^(2/5)-(3/2)^(1/5)*(1+Sqrt[5])*x+2*x^2])/(1000*2^(2/5)*3^(3/5)),-x^2/(20*(3+2*x^5)^2)+x^2/(150*(3+2*x^5))-(Sqrt[5+Sqrt[5]]*ArcTan[Sqrt[1/5*(5+2*Sqrt[5])]-(2*2^(7/10)*x)/(3^(1/5)*Sqrt[5-Sqrt[5]])])/(250*2^(9/10)*3^(3/5))-(Sqrt[5-Sqrt[5]]*ArcTan[Sqrt[1/5*(5-2*Sqrt[5])]+(2*2^(7/10)*x)/(3^(1/5)*Sqrt[5+Sqrt[5]])])/(250*2^(9/10)*3^(3/5))-Log[3^(1/5)+2^(1/5)*x]/(250*2^(2/5)*3^(3/5))+((1+Sqrt[5])*Log[3^(2/5)-(3^(1/5)*(1-Sqrt[5])*x)/2^(4/5)+2^(2/5)*x^2])/(1000*2^(2/5)*3^(3/5))+((1-Sqrt[5])*Log[3^(2/5)-(3^(1/5)*(1+Sqrt[5])*x)/2^(4/5)+2^(2/5)*x^2])/(1000*2^(2/5)*3^(3/5))]" //
    );
  }

  public void test178() {
    check( //
        "Integrate[9/(5*x^2*(3-2*x^2)^3), x]", //
        "-1/(8*x)+3/(20*x*(3-2*x^2)^2)+1/(8*x*(3-2*x^2))+ArcTanh[Sqrt[2/3]*x]/(4*Sqrt[6])" //
    );
  }

  public void test179() {
    check( //
        "Integrate[(3*x^4+4)/(x^2*(x^2+1)^3), x]", //
        "-4/x-(7*x)/(4*(1+x^2)^2)-(25*x)/(8*(1+x^2))-1/8*57*ArcTan[x]" //
    );
  }

  public void test180() {
    check( //
        "Integrate[(5-3*x+6*x^2+5*x^3-x^4)/(x^5-x^4-2*x^3+2*x^2+x+(-1)*1), x]", //
        "-3/(2*(1-x)^2)+2/(1-x)+1/(1+x)+Log[1-x]-2*Log[1+x]" //
    );
  }

  public void test181() {
    check( //
        "Integrate[(x^2+1)/(x*(x^3+1)^2), x]", //
        "(x*(x-x^2))/(3*(x^3+1))-ArcTan[(1-2*x)/Sqrt[3]]/(3*Sqrt[3])+Log[x]-4/9*Log[1+x]-5/18*Log[1-x+x^2]" //
    );
  }

  public void test182() {
    check( //
        "Integrate[(x^2-3*x+(-1)*2)/((x+1)^2*(x^2+x+1)^2), x]", //
        "-2/(1+x)-(7+5*x)/(3*(1+x+x^2))-(25*ArcTan[(1+2*x)/Sqrt[3]])/(3*Sqrt[3])-Log[1+x]+Log[1+x+x^2]/2" //
    );
  }

  public void test183() {
    check( //
        "Integrate[1/((2-3*x)*(1-4*x)^3), x]", //
        "1/(10*(1-4*x)^2)-3/(25*(1-4*x))-9/125*Log[1-4*x]+9/125*Log[2-3*x]" //
    );
  }

  public void test184() {
    check( //
        "Integrate[x^3/(2-5*x^2)^7, x]", //
        "1/(150*(2-5*x^2)^6)-1/(250*(2-5*x^2)^5)" //
    );
  }

  public void test185() {
    check( //
        "Integrate[x^7/(2-5*x^2)^3, x]", //
        "-x^2/250+2/(625*(2-5*x^2)^2)-6/(625*(2-5*x^2))-3/625*Log[2-5*x^2]" //
    );
  }

  public void test186() {
    check( //
        "Integrate[1/((x+(-1)*2)^3*(x+1)^2), x]", //
        "-1/(18*(x+(-1)*2)^2)+2/(27*(x+(-1)*2))+1/(27*(x+1))+Log[x+(-1)*2]/27-Log[x+1]/27" //
    );
  }

  public void test187() {
    check( //
        "Integrate[1/((x+2)^3*(x+3)^4), x]", //
        "-1/(2*(x+2)^2)+4/(x+2)+1/(3*(x+3)^3)+3/(2*(x+3)^2)+6/(x+3)+10*Log[x+2]-10*Log[x+3]" //
    );
  }

  public void test188() {
    check( //
        "Integrate[x^5/(3+x)^2, x]", //
        "-108*x+1/2*27*x^2-2*x^3+x^4/4+243/(3+x)+405*Log[3+x]" //
    );
  }

  public void test189() {
    check( //
        "Integrate[(b1+c1*x)*(a+2*b*x+c*x^2)^1, x]", //
        "a*b1*x+1/2*(2*b*b1+a*c1)*x^2+1/3*(b1*c+2*b*c1)*x^3+1/4*c*c1*x^4" //
    );
  }

  public void test190() {
    check( //
        "Integrate[(b1+c1*x)*(a+2*b*x+c*x^2)^2, x]", //
        "a^2*b1*x+1/2*a*(4*b*b1+a*c1)*x^2+2/3*(2*b^2*b1+a*b1*c+2*a*b*c1)*x^3+1/2*(2*b*b1*c+2*b^2*c1+a*c*c1)*x^4+1/5*c*(b1*c+4*b*c1)*x^5+1/6*c^2*c1*x^6" //
    );
  }

  public void test191() {
    check( //
        "Integrate[(b1+c1*x)*(a+2*b*x+c*x^2)^3, x]", //
        "a^3*b1*x+1/2*a^2*(6*b*b1+a*c1)*x^2+a*(4*b^2*b1+a*b1*c+2*a*b*c1)*x^3+1/4*(8*b^3*b1+12*a*b*b1*c+12*a*b^2*c1+3*a^2*c*c1)*x^4+1/5*(12*b^2*b1*c+3*a*b1*c^2+8*b^3*c1+12*a*b*c*c1)*x^5+1/2*c*(2*b*b1*c+4*b^2*c1+a*c*c1)*x^6+1/7*c^2*(b1*c+6*b*c1)*x^7+1/8*c^3*c1*x^8" //
    );
  }

  public void test192() {
    check( //
        "Integrate[(b1+c1*x)*(a+2*b*x+c*x^2)^4, x]", //
        "a^4*b1*x+1/2*a^3*(8*b*b1+a*c1)*x^2+4/3*a^2*(6*b^2*b1+a*b1*c+2*a*b*c1)*x^3+a*(8*b^3*b1+6*a*b*b1*c+6*a*b^2*c1+a^2*c*c1)*x^4+2/5*(8*b^4*b1+24*a*b^2*b1*c+3*a^2*b1*c^2+16*a*b^3*c1+12*a^2*b*c*c1)*x^5+1/3*(16*b^3*b1*c+12*a*b*b1*c^2+8*b^4*c1+24*a*b^2*c*c1+3*a^2*c^2*c1)*x^6+4/7*c*(6*b^2*b1*c+a*b1*c^2+8*b^3*c1+6*a*b*c*c1)*x^7+1/2*c^2*(2*b*b1*c+6*b^2*c1+a*c*c1)*x^8+1/9*c^3*(b1*c+8*b*c1)*x^9+1/10*c^4*c1*x^10" //
    );
  }

  public void test193() {
    check( //
        "Integrate[(b1+c1*x)*(a+2*b*x+c*x^2)^n, x]", //
        "(c1*(a+2*b*x+c*x^2)^(n+1))/(2*c*(n+1))-((b1*c-b*c1)*(a+2*b*x+c*x^2)^(n+1))/(2*c*(n+1)*Sqrt[b^2-a*c]*(-(b+c*x-Sqrt[b^2-a*c])/(2*Sqrt[b^2-a*c]))^(n+1))*Hypergeometric2F1[-n,1+n,2+n,(b+c*x+Sqrt[b^2-a*c])/(2*Sqrt[b^2-a*c])]" //
    );
  }

  public void test194() {
    check( //
        "Integrate[(b1+c1*x)/(a+2*b*x+c*x^2), x]", //
        "-((b1*c-b*c1)*ArcTanh[(b+c*x)/Sqrt[b^2-a*c]])/(c*Sqrt[b^2-a*c])+(c1*Log[a+2*b*x+c*x^2])/(2*c)" //
    );
  }

  public void test195() {
    check( //
        "Integrate[(b1+c1*x)/(a+2*b*x+c*x^2)^2, x]", //
        "-(b*b1-a*c1+(b1*c-b*c1)*x)/(2*(b^2-a*c)*(a+2*b*x+c*x^2))+((b1*c-b*c1)*ArcTanh[(b+c*x)/Sqrt[b^2-a*c]])/(2*(b^2-a*c)^(3/2))" //
    );
  }

  public void test196() {
    check( //
        "Integrate[(b1+c1*x)/(a+2*b*x+c*x^2)^3, x]", //
        "-(b*b1-a*c1+(b1*c-b*c1)*x)/(4*(b^2-a*c)*(a+2*b*x+c*x^2)^2)+(3*(b1*c-b*c1)*(b+c*x))/(8*(b^2-a*c)^2*(a+2*b*x+c*x^2))-(3*c*(b1*c-b*c1)*ArcTanh[(b+c*x)/Sqrt[b^2-a*c]])/(8*(b^2-a*c)^(5/2))" //
    );
  }

  public void test197() {
    check( //
        "Integrate[(b1+c1*x)/(a+2*b*x+c*x^2)^4, x]", //
        "-(b*b1-a*c1+(b1*c-b*c1)*x)/(6*(b^2-a*c)*(a+2*b*x+c*x^2)^3)+(5*(b1*c-b*c1)*(b+c*x))/(24*(b^2-a*c)^2*(a+2*b*x+c*x^2)^2)-(5*c*(b1*c-b*c1)*(b+c*x))/(16*(b^2-a*c)^3*(a+2*b*x+c*x^2))+(5*c^2*(b1*c-b*c1)*ArcTanh[(b+c*x)/Sqrt[b^2-a*c]])/(16*(b^2-a*c)^(7/2))" //
    );
  }

  public void test198() {
    check( //
        "Integrate[(b1+c1*x)/(a+2*b*x+c*x^2)^n, x]", //
        "(c1*(a+2*b*x+c*x^2)^(1-n))/(2*c*(1-n))-((b1*c-b*c1)*(-(b-Sqrt[b^2-a*c]+c*x)/Sqrt[b^2-a*c])^(-1+n)*(a+2*b*x+c*x^2)^(1-n)*Hypergeometric2F1[1-n,n,2-n,(b+Sqrt[b^2-a*c]+c*x)/(2*Sqrt[b^2-a*c])])/(2^n*c*Sqrt[b^2-a*c]*(1-n))" //
    );
  }

  public void test199() {
    check( //
        "Integrate[x/(3+6*x+2*x^2), x]", //
        "1/4*(1-Sqrt[3])*Log[3-Sqrt[3]+2*x]+1/4*(1+Sqrt[3])*Log[3+Sqrt[3]+2*x]" //
    );
  }

  public void test200() {
    check( //
        "Integrate[(2*x+(-1)*3)/(3+6*x+2*x^2)^3, x]", //
        "(5+4*x)/(4*(3+6*x+2*x^2)^2)-(3+2*x)/(2*(3+6*x+2*x^2))+ArcTanh[(3+2*x)/Sqrt[3]]/Sqrt[3]" //
    );
  }

  public void test201() {
    check( //
        "Integrate[(x+(-1)*1)/(x^2+5*x+4)^2, x]", //
        "(7*x+13)/(9*(x^2+5*x+4))+1/27*7*Log[x+1]-1/27*7*Log[x+4]" //
    );
  }

  public void test202() {
    check( //
        "Integrate[1/(x^2+3*x+2)^5, x]", //
        "-(2*x+3)/(4*(x^2+3*x+2)^4)+(7*(2*x+3))/(6*(x^2+3*x+2)^3)-(35*(2*x+3))/(6*(x^2+3*x+2)^2)+(35*(2*x+3))/(x^2+3*x+2)+70*Log[x+1]-70*Log[x+2]" //
    );
  }

  public void test203() {
    check( //
        "Integrate[1/(x^3*(7-6*x+2*x^2)^2), x]", //
        "-1/(490*x^2)-69/(1715*x)-(2-3*x)/(35*x^2*(7-6*x+2*x^2))-(234*ArcTan[(3-2*x)/Sqrt[5]])/(12005*Sqrt[5])+1/2401*80*Log[x]-1/2401*40*Log[7-6*x+2*x^2]" //
    );
  }

  public void test204() {
    check( //
        "Integrate[x^9/(x^2+3*x+2)^5, x]", //
        "735*x+(x^8*(4+3*x))/(4*(2+3*x+x^2)^4)-(x^6*(110+81*x))/(12*(2+3*x+x^2)^3)+(x^4*(184+135*x))/(2*(2+3*x+x^2)^2)-(x^2*(2206+1593*x))/(2*(2+3*x+x^2))-1471*Log[1+x]+1472*Log[2+x]" //
    );
  }

  public void test205() {
    check( //
        "Integrate[(1+2*x)^2/(3+5*x+2*x^2)^5, x]", //
        "((1+2*x)*(7+6*x))/(4*(3+5*x+2*x^2)^4)+(73+62*x)/(3*(3+5*x+2*x^2)^3)-(155*(5+4*x))/(3*(3+5*x+2*x^2)^2)+(620*(5+4*x))/(3+5*x+2*x^2)+2480*Log[x+1]-2480*Log[2*x+3]" //
    );
  }

  public void test206() {
    check( //
        "Integrate[(a-b*x^2)^3/x^7, x]", //
        "-a^3/(6*x^6)+(3*a^2*b)/(4*x^4)-(3*a*b^2)/(2*x^2)-b^3*Log[x]" //
    );
  }

  public void test207() {
    check( //
        "Integrate[x^13/(a^4+x^4)^5, x]", //
        "-x^10/(16*(a^4+x^4)^4)-(5*x^6)/(96*(a^4+x^4)^3)-(5*x^2)/(128*(a^4+x^4)^2)+(5*x^2)/(256*a^4*(a^4+x^4))+(5*ArcTan[x^2/a^2])/(256*a^6)" //
    );
  }

  public void test208() {
    check( //
        "Integrate[x^(3/2)*(1+x^2)*(2*Sqrt[x]-x)^2, x]", //
        "1/7*8*x^(7/2)-x^4+1/9*2*x^(9/2)+1/11*8*x^(11/2)-1/3*2*x^6+1/13*2*x^(13/2)" //
    );
  }

  public void test209() {
    check( //
        "Integrate[(x^(3/2)-3*x^(3/5))^2*(4*x^(3/2)-x^(2/3)/3), x]", //
        "-1/43*45*x^(43/15)+1/37*360*x^(37/10)+1/113*60*x^(113/30)-1/23*120*x^(23/5)-x^(14/3)/14+1/11*8*x^(11/2)" //
    );
  }

  public void test210() {
    check( //
        "Integrate[1/(1+Sqrt[1+x]), x]", //
        "2*Sqrt[1+x]-2*Log[1+Sqrt[1+x]]" //
    );
  }

  public void test211() {
    check( //
        "Integrate[x/(1+Sqrt[1+x]), x]", //
        "2/3*(1+x)^(3/2)-x" //
    );
  }

  public void test212() {
    check( //
        "Integrate[(Sqrt[1+x]+1)/(Sqrt[1+x]+(-1)*1), x]", //
        "x+4*Sqrt[1+x]+4*Log[1-Sqrt[1+x]]" //
    );
  }

  public void test213() {
    check( //
        "Integrate[1/((1+x)^(2/3)-Sqrt[1+x]), x]", //
        "6*(1+x)^(1/6)+3*(1+x)^(1/3)+6*Log[1-(1+x)^(1/6)]" //
    );
  }

  public void test214() {
    check( //
        "Integrate[(1+x^(1/4))^(1/3)/Sqrt[x], x]", //
        "12/7*(1+x^(1/4))^(7/3)-3*(1+x^(1/4))^(4/3)" //
    );
  }

  public void test215() {
    check( //
        "Integrate[1/(x^3*(1+x)^(3/2)), x]", //
        "15/(4*Sqrt[1+x])-1/(2*x^2*Sqrt[1+x])+5/(4*x*Sqrt[1+x])-15/4*ArcTanh[Sqrt[1+x]]" //
    );
  }

  public void test216() {
    check( //
        "Integrate[1/(x^5*(1-x)^(7/2)), x]", //
        "3003/(320*(1-x)^(5/2))+1001/(64*(1-x)^(3/2))+3003/(64*Sqrt[1-x])-1/(4*(1-x)^(5/2)*x^4)-13/(24*(1-x)^(5/2)*x^3)-143/(96*(1-x)^(5/2)*x^2)-429/(64*(1-x)^(5/2)*x)-3003/64*ArcTanh[Sqrt[1-x]]" //
    );
  }

  public void test217() {
    check( //
        "Integrate[1/(x^5*(x+(-1)*1)^(2/3)), x]", //
        "(x+(-1)*1)^(1/3)/(4*x^4)+(11*(x+(-1)*1)^(1/3))/(36*x^3)+(11*(x+(-1)*1)^(1/3))/(27*x^2)+(55*(x+(-1)*1)^(1/3))/(81*x)-110/(81*Sqrt[3])*ArcTan[(1-2*(x+(-1)*1)^(1/3))/Sqrt[3]]+55/81*Log[1+(x+(-1)*1)^(1/3)]-1/243*55*Log[x]" //
    );
  }

  public void test218() {
    check( //
        "Integrate[Sqrt[(1-x)/(1+x)], x]", //
        "(1+x)*Sqrt[(1-x)/(1+x)]-2*ArcTan[Sqrt[(1-x)/(1+x)]]" //
    );
  }

  public void test219() {
    check( //
        "Integrate[Sqrt[(x-a)/(b-x)]*x, x]", //
        "1/4*(a-5*b)*Sqrt[(x-a)/(b-x)]*(b-x)+1/2*Sqrt[(x-a)/(b-x)]*(b-x)^2-1/4*(a-b)*(a+3*b)*ArcTan[Sqrt[(x-a)/(b-x)]]" //
    );
  }

  public void test220() {
    check( //
        "Integrate[Sqrt[x+(-1)*5]*Sqrt[x+3]/((x+(-1)*1)*(x^2+(-1)*25)), x]", //
        "ArcTan[1/4*Sqrt[-5+x]*Sqrt[3+x]]/6+ArcTanh[(Sqrt[5]*Sqrt[3+x])/Sqrt[-5+x]]/(3*Sqrt[5])" //
    );
  }

  public void test221() {
    check( //
        "Integrate[x^2*(1-x^2)^(1/4)*Sqrt[1+x]/(Sqrt[1-x]*(Sqrt[1-x]-Sqrt[1+x])), x]", //
        "5/16*(1-x)^(3/4)*(1+x)^(1/4)-1/16*(1-x)^(1/4)*(1+x)^(3/4)+1/24*(1-x)^(5/4)*(1+x)^(3/4)+(7*(1-x^2)^(5/4))/(24*Sqrt[1-x])+(x*(1-x^2)^(5/4))/(6*Sqrt[1-x])+1/6*Sqrt[1+x]*(1-x^2)^(5/4)-(3*ArcTan[1-(Sqrt[2]*(1-x)^(1/4))/(1+x)^(1/4)])/(8*Sqrt[2])+(3*ArcTan[1+(Sqrt[2]*(1-x)^(1/4))/(1+x)^(1/4)])/(8*Sqrt[2])+Log[1+Sqrt[1-x]/Sqrt[1+x]-(Sqrt[2]*(1-x)^(1/4))/(1+x)^(1/4)]/(8*Sqrt[2])-Log[1+Sqrt[1-x]/Sqrt[1+x]+(Sqrt[2]*(1-x)^(1/4))/(1+x)^(1/4)]/(8*Sqrt[2])" //
    );
  }

  public void test222() {
    check( //
        "Integrate[x*(1+x)^(2/3)*Sqrt[1-x]/(Sqrt[1+x]*(1-x)^(2/3)-(1+x)^(1/3)*(1-x)^(5/6)), x]", //
        "-1/12*(1-3*x)*(1-x)^(2/3)*(1+x)^(1/3)+1/4*Sqrt[1-x]*x*Sqrt[1+x]-1/4*(1-x)*(3+x)+1/12*(1-x)^(1/3)*(1+x)^(2/3)*(1+3*x)+1/12*(1-x)^(1/6)*(1+x)^(5/6)*(2+3*x)-1/12*(1-x)^(5/6)*(1+x)^(1/6)*(10+3*x)+ArcTan[(1+x)^(1/6)/(1-x)^(1/6)]/6-(4*ArcTan[((1-x)^(1/3)-2*(1+x)^(1/3))/(Sqrt[3]*(1-x)^(1/3))])/(3*Sqrt[3])-5/6*ArcTan[((1-x)^(1/3)-(1+x)^(1/3))/((1-x)^(1/6)*(1+x)^(1/6))]+ArcTanh[(Sqrt[3]*(1-x)^(1/6)*(1+x)^(1/6))/((1-x)^(1/3)+(1+x)^(1/3))]/(6*Sqrt[3])" //
    );
  }

  public void test223() {
    check( //
        "Integrate[1/((x+1)^2*(x+(-1)*1)^4)^(1/3), x]", //
        "-(3*(x+(-1)*1)*(x+1))/(2*((x+1)^2*(x+(-1)*1)^4)^(1/3))" //
    );
  }

  public void test224() {
    check( //
        "Integrate[1/((x+(-1)*1)^3*(x+2)^5)^(1/4), x]", //
        "(4*(x+(-1)*1)*(2+x))/(3*((x+(-1)*1)^3*(x+2)^5)^(1/4))" //
    );
  }

  public void test225() {
    check( //
        "Integrate[1/((x+1)^2*(x+(-1)*1)^7)^(1/3), x]", //
        "-(3*(x+(-1)*1)*(x+1))/(8*((x+1)^2*(x+(-1)*1)^7)^(1/3))+(9*(x+(-1)*1)^2*(x+1))/(16*((x+1)^2*(x+(-1)*1)^7)^(1/3))" //
    );
  }

  public void test226() {
    check( //
        "Integrate[1/((x+(-1)*1)^2*(x+1))^(1/3), x]", //
        "Sqrt[3]*ArcTan[(1+(2*(x+(-1)*1))/((x+(-1)*1)^2*(x+1))^(1/3))/Sqrt[3]]-Log[x+1]/2-3/2*Log[1-(x+(-1)*1)/((x+(-1)*1)^2*(x+1))^(1/3)]" //
    );
  }

  public void test227() {
    check( //
        "Integrate[(x+1/x)/Sqrt[(x+1)^3*(x+(-1)*2)], x]", //
        "-(4*(x+(-1)*2)*(x+1))/(3*Sqrt[(x+1)^3*(x+(-1)*2)])+(2*Sqrt[x+(-1)*2]*(x+1)^(3/2)*ArcSinh[Sqrt[x+(-1)*2]/Sqrt[3]])/Sqrt[(x+1)^3*(x+(-1)*2)]-(Sqrt[2]*Sqrt[x+(-1)*2]*(x+1)^(3/2)*ArcTan[(Sqrt[2]*Sqrt[x+1])/Sqrt[x+(-1)*2]])/Sqrt[(x+1)^3*(x+(-1)*2)]" //
    );
  }

  public void test228() {
    check( //
        "Integrate[((x+(-1)*1)^2*(x+1))^(1/3)/x^2, x]", //
        "-((x+(-1)*1)^2*(x+1))^(1/3)/x-ArcTan[(1-(2*(x+(-1)*1))/((x+(-1)*1)^2*(x+1))^(1/3))/Sqrt[3]]/Sqrt[3]-Sqrt[3]*ArcTan[(1+(2*(x+(-1)*1))/((x+(-1)*1)^2*(x+1))^(1/3))/Sqrt[3]]+Log[x]/6-2/3*Log[x+1]-3/2*Log[1-(x+(-1)*1)/((x+(-1)*1)^2*(1+x))^(1/3)]-Log[1+(x+(-1)*1)/((x+(-1)*1)^2*(1+x))^(1/3)]/2" //
    );
  }

  public void test229() {
    check( //
        "Integrate[1/(x^2-2*x+(-1)*3)^(5/2), x]", //
        "(1-x)/(12*(x^2-2*x+(-1)*3)^(3/2))-(1-x)/(24*Sqrt[x^2-2*x+(-1)*3])" //
    );
  }

  public void test230() {
    check( //
        "Integrate[1/Sqrt[x^3-5*x^2+3*x+9], x]", //
        "((3-x)*Sqrt[1+x]*ArcTanh[Sqrt[1+x]/2])/Sqrt[x^3-5*x^2+3*x+9]" //
    );
  }

  public void test231() {
    check( //
        "Integrate[1/(x^3-5*x^2+3*x+9)^(3/2), x]", //
        "((3-x)*(1+x))/(8*(x^3-5*x^2+3*x+9)^(3/2))+(5*(3-x)^2*(1+x))/(64*(x^3-5*x^2+3*x+9)^(3/2))-(15*(3-x)^3*(1+x))/(256*(x^3-5*x^2+3*x+9)^(3/2))+(15*(3-x)^3*(1+x)^(3/2)*ArcTanh[Sqrt[1+x]/2])/(512*(x^3-5*x^2+3*x+9)^(3/2))" //
    );
  }

  public void test232() {
    check( //
        "Integrate[1/(x^3-5*x^2+3*x+9)^(1/3), x]", //
        "Sqrt[3]*ArcTan[(1+(2*(x+(-1)*3))/(x^3-5*x^2+3*x+9)^(1/3))/Sqrt[3]]-Log[x+1]/2-3/2*Log[1-(x+(-1)*3)/(x^3-5*x^2+3*x+9)^(1/3)]" //
    );
  }

  public void test233() {
    check( //
        "Integrate[1/(x^3-5*x^2+3*x+9)^(2/3), x]", //
        "(3*(3-x)*(1+x))/(4*(x^3-5*x^2+3*x+9)^(2/3))" //
    );
  }

  public void test234() {
    check( //
        "Integrate[1/(x^3-5*x^2+3*x+9)^(4/3), x]", //
        "(3*(3-x)*(1+x))/(20*(x^3-5*x^2+3*x+9)^(4/3))+(9*(3-x)^2*(1+x))/(80*(x^3-5*x^2+3*x+9)^(4/3))-(27*(3-x)^3*(1+x))/(320*(x^3-5*x^2+3*x+9)^(4/3))" //
    );
  }

  public void test235() {
    check( //
        "Integrate[1/Sqrt[4+3*x-2*x^2], x]", //
        "-1/Sqrt[2]*ArcSin[(3-4*x)/Sqrt[41]]" //
    );
  }

  public void test236() {
    check( //
        "Integrate[1/Sqrt[-3+4*x-x^2], x]", //
        "-ArcSin[2-x]" //
    );
  }

  public void test237() {
    check( //
        "Integrate[1/Sqrt[-2-5*x-3*x^2], x]", //
        "ArcSin[5+6*x]/Sqrt[3]" //
    );
  }

  public void test238() {
    check( //
        "Integrate[1/((x^2+4)*Sqrt[1-x^2]), x]", //
        "ArcTan[(Sqrt[5]*x)/(2*Sqrt[1-x^2])]/(2*Sqrt[5])" //
    );
  }

  public void test239() {
    check( //
        "Integrate[1/((x^2+4)*Sqrt[4*x^2+1]), x]", //
        "ArcTanh[(Sqrt[15]*x)/(2*Sqrt[1+4*x^2])]/(2*Sqrt[15])" //
    );
  }

  public void test240() {
    check( //
        "Integrate[x/((3-x^2)*Sqrt[5-x^2]), x]", //
        "ArcTanh[Sqrt[5-x^2]/Sqrt[2]]/Sqrt[2]" //
    );
  }

  public void test241() {
    check( //
        "Integrate[x/((5-x^2)*Sqrt[3-x^2]), x]", //
        "-1/Sqrt[2]*ArcTan[Sqrt[3-x^2]/Sqrt[2]]" //
    );
  }

  public void test242() {
    check( //
        "Integrate[1/((x^4+(-1)*1)*Sqrt[x^2+2]), x]", //
        "-ArcTan[x/Sqrt[2+x^2]]/2-ArcTanh[(Sqrt[3]*x)/Sqrt[2+x^2]]/(2*Sqrt[3])" //
    );
  }

  public void test243() {
    check( //
        "Integrate[x/((x^2+(-1)*1)*Sqrt[x^2+2*x+4]), x]", //
        "-1/(2*Sqrt[7])*ArcTanh[(5+2*x)/(Sqrt[7]*Sqrt[x^2+2*x+4])]-ArcTanh[Sqrt[x^2+2*x+4]/Sqrt[3]]/(2*Sqrt[3])" //
    );
  }

  public void test244() {
    check( //
        "Integrate[1/((x^3+(-1)*8)*Sqrt[x^2+2*x+5]), x]", //
        "-1/(4*Sqrt[3])*ArcTan[(1+x)/(Sqrt[3]*Sqrt[x^2+2*x+5])]-ArcTanh[(7+3*x)/(Sqrt[13]*Sqrt[x^2+2*x+5])]/(12*Sqrt[13])+ArcTanh[Sqrt[x^2+2*x+5]]/12" //
    );
  }

  public void test245() {
    check( //
        "Integrate[x/((x^2+x+4)*Sqrt[4*x^2+4*x+5]), x]", //
        "ArcTan[Sqrt[4*x^2+4*x+5]/Sqrt[11]]/Sqrt[11]-ArcTanh[(Sqrt[11/15]*(2*x+1))/Sqrt[4*x^2+4*x+5]]/Sqrt[165]" //
    );
  }

  public void test246() {
    check( //
        "Integrate[(x+3)/((x^2+1)*Sqrt[x^2+x+1]), x]", //
        "-2*Sqrt[2]*ArcTan[(1-x)/(Sqrt[2]*Sqrt[1+x+x^2])]+Sqrt[2]*ArcTanh[(1+x)/(Sqrt[2]*Sqrt[1+x+x^2])]" //
    );
  }

  public void test247() {
    check( //
        "Integrate[(2*x+1)/((3*x^2+4*x+4)*Sqrt[x^2+6*x+(-1)*1]), x]", //
        "-5/(6*Sqrt[14])*ArcTan[(Sqrt[7]*(2-x))/(2*Sqrt[2]*Sqrt[x^2+6*x+(-1)*1])]-ArcTanh[(Sqrt[7]*(1+x))/Sqrt[x^2+6*x+(-1)*1]]/(3*Sqrt[7])" //
    );
  }

  public void test248() {
    check( //
        "Integrate[(A*x+B)/((5*x^2-18*x+17)*Sqrt[10*x^2-22*x+13]), x]", //
        "-(2*A+B)/Sqrt[35]*ArcTan[(Sqrt[35]*(2-x))/Sqrt[10*x^2-22*x+13]]-(A+B)/(2*Sqrt[35])*ArcTanh[(Sqrt[35]*(1-x))/(2*Sqrt[10*x^2-22*x+13])]" //
    );
  }

  public void test249() {
    check( //
        "Integrate[(x+(-1)*2)/((5*x^2-18*x+17)*Sqrt[10*x^2-22*x+13]), x]", //
        "ArcTanh[(Sqrt[35]*(1-x))/(2*Sqrt[10*x^2-22*x+13])]/(2*Sqrt[35])" //
    );
  }

  public void test250() {
    check( //
        "Integrate[x^4*Sqrt[5-x^2], x]", //
        "-25/16*x*Sqrt[5-x^2]-5/24*x^3*Sqrt[5-x^2]+1/6*x^5*Sqrt[5-x^2]+125/16*ArcSin[x/Sqrt[5]]" //
    );
  }

  public void test251() {
    check( //
        "Integrate[1/(x^6*Sqrt[x^2+2]), x]", //
        "-Sqrt[x^2+2]/(10*x^5)+Sqrt[x^2+2]/(15*x^3)-Sqrt[x^2+2]/(15*x)" //
    );
  }

  public void test252() {
    check( //
        "Integrate[1/(2*x^2+3)^(7/2), x]", //
        "x/(15*(2*x^2+3)^(5/2))+(4*x)/(135*(2*x^2+3)^(3/2))+(8*x)/(405*Sqrt[2*x^2+3])" //
    );
  }

  public void test253() {
    check( //
        "Integrate[x/(1+x^2+a*Sqrt[1+x^2]), x]", //
        "Log[a+Sqrt[1+x^2]]" //
    );
  }

  public void test254() {
    check( //
        "Integrate[(x^2-x+1)/((1+x^2)*Sqrt[1+x^2]), x]", //
        "1/Sqrt[1+x^2]+ArcSinh[x]" //
    );
  }

  public void test255() {
    check( //
        "Integrate[Sqrt[1+x^2]/(2+x^2), x]", //
        "ArcSinh[x]-ArcTanh[x/(Sqrt[2]*Sqrt[1+x^2])]/Sqrt[2]" //
    );
  }

  public void test256() {
    check( //
        "Integrate[1/((2+x^2)^2*Sqrt[1+x^2]), x]", //
        "-(x*Sqrt[1+x^2])/(4*(2+x^2))+3/(4*Sqrt[2])*ArcTanh[x/(Sqrt[2]*Sqrt[1+x^2])]" //
    );
  }

  public void test257() {
    check( //
        "Integrate[x^2/((x^2+(-1)*6)*Sqrt[x^2+(-1)*2]), x]", //
        "ArcTanh[x/Sqrt[x^2+(-1)*2]]-Sqrt[3/2]*ArcTanh[(Sqrt[2/3]*x)/Sqrt[x^2+(-1)*2]]" //
    );
  }

  public void test258() {
    check( //
        "Integrate[(x^2+5)/((1+x^2)^2*Sqrt[1-x^2]), x]", //
        "(x*Sqrt[1-x^2])/(1+x^2)+2*Sqrt[2]*ArcTan[(Sqrt[2]*x)/Sqrt[1-x^2]]" //
    );
  }

  public void test259() {
    check( //
        "Integrate[(4*x-Sqrt[1-x^2])/(5+Sqrt[1-x^2]), x]", //
        "-x-4*Sqrt[1-x^2]+5*ArcSin[x]+(25*ArcTan[x/(2*Sqrt[6])])/(2*Sqrt[6])-(25*ArcTan[(5*x)/(2*Sqrt[6]*Sqrt[1-x^2])])/(2*Sqrt[6])+20*Log[5+Sqrt[1-x^2]]" //
    );
  }

  public void test260() {
    check( //
        "Integrate[(2-Sqrt[x^2+1])*x^2/(Sqrt[x^2+1]*((x^2+1)^(3/2)-x^3+1)), x]", //
        "1/9*8*x-x^2/6+1/9*8*Sqrt[x^2+1]-1/6*x*Sqrt[x^2+1]-1/54*41*ArcSinh[x]+4/27*Sqrt[2]*ArcTan[(1+3*x)/(2*Sqrt[2])]+4/27*Sqrt[2]*ArcTan[(1+x)/(Sqrt[2]*Sqrt[x^2+1])]+7/27*ArcTanh[(1-x)/(2*Sqrt[x^2+1])]-7/54*Log[3+2*x+3*x^2]" //
    );
  }

  public void test261() {
    check( //
        "Integrate[x*Sqrt[2*r*x-x^2], x]", //
        "-1/2*r*(r-x)*Sqrt[2*r*x-x^2]-(2*r*x-x^2)^(3/2)/3+r^3*ArcTan[x/Sqrt[2*r*x-x^2]]" //
    );
  }

  public void test262() {
    check( //
        "Integrate[x^2*Sqrt[2*r*x-x^2], x]", //
        "-5/8*r^2*(r-x)*Sqrt[2*r*x-x^2]-5/12*r*(2*r*x-x^2)^(3/2)-1/4*x*(2*r*x-x^2)^(3/2)+5/4*r^4*ArcTan[x/Sqrt[2*r*x-x^2]]" //
    );
  }

  public void test263() {
    check( //
        "Integrate[x^3*Sqrt[2*r*x-x^2], x]", //
        "-7/8*r^3*(r-x)*Sqrt[2*r*x-x^2]-7/12*r^2*(2*r*x-x^2)^(3/2)-7/20*r*x*(2*r*x-x^2)^(3/2)-1/5*x^2*(2*r*x-x^2)^(3/2)+7/4*r^5*ArcTan[x/Sqrt[2*r*x-x^2]]" //
    );
  }

  public void test264() {
    check( //
        "Integrate[1/((x^2+(-1)*1)*Sqrt[2*x+x^2]), x]", //
        "-ArcTan[Sqrt[2*x+x^2]]/2-ArcTanh[(1+2*x)/(Sqrt[3]*Sqrt[2*x+x^2])]/(2*Sqrt[3])" //
    );
  }

  public void test265() {
    check( //
        "Integrate[(3*x+(-1)*2)/((x+1)^3*Sqrt[2*x-x^2]), x]", //
        "-(5*Sqrt[2*x-x^2])/(6*(1+x)^2)-(2*Sqrt[2*x-x^2])/(3*(1+x))+ArcTan[(1-2*x)/(Sqrt[3]*Sqrt[2*x-x^2])]/(2*Sqrt[3])" //
    );
  }

  public void test266() {
    check( //
        "Integrate[1/Sqrt[1+x+x^2], x]", //
        "ArcSinh[(1+2*x)/Sqrt[3]]" //
    );
  }

  public void test267() {
    check( //
        "Integrate[x^3/Sqrt[1+x+x^2], x]", //
        "1/3*x^2*Sqrt[1+x+x^2]-1/24*(1+10*x)*Sqrt[1+x+x^2]+7/16*ArcSinh[(1+2*x)/Sqrt[3]]" //
    );
  }

  public void test268() {
    check( //
        "Integrate[1/(1+x+x^2)^(3/2), x]", //
        "(2*(1+2*x))/(3*Sqrt[1+x+x^2])" //
    );
  }

  public void test269() {
    check( //
        "Integrate[x/(1+x+x^2)^(3/2), x]", //
        "-(2*(2+x))/(3*Sqrt[1+x+x^2])" //
    );
  }

  public void test270() {
    check( //
        "Integrate[x^3/(1+x+x^2)^(3/2), x]", //
        "-(2*x^2*(2+x))/(3*Sqrt[1+x+x^2])+1/3*(5+2*x)*Sqrt[1+x+x^2]-3/2*ArcSinh[(1+2*x)/Sqrt[3]]" //
    );
  }

  public void test271() {
    check( //
        "Integrate[x^2*Sqrt[1+x+x^2], x]", //
        "1/64*(1+2*x)*Sqrt[1+x+x^2]-5/24*(1+x+x^2)^(3/2)+1/4*x*(1+x+x^2)^(3/2)+3/128*ArcSinh[(1+2*x)/Sqrt[3]]" //
    );
  }

  public void test272() {
    check( //
        "Integrate[(1+x+x^2)^(3/2), x]", //
        "9/64*(1+2*x)*Sqrt[1+x+x^2]+1/8*(1+2*x)*(1+x+x^2)^(3/2)+27/128*ArcSinh[(1+2*x)/Sqrt[3]]" //
    );
  }

  public void test273() {
    check( //
        "Integrate[(1+x+x^2)^(5/2), x]", //
        "45/512*(1+2*x)*Sqrt[1+x+x^2]+5/64*(1+2*x)*(1+x+x^2)^(3/2)+1/12*(1+2*x)*(1+x+x^2)^(5/2)+135/1024*ArcSinh[(1+2*x)/Sqrt[3]]" //
    );
  }

  public void test274() {
    check( //
        "Integrate[1/(x^2*Sqrt[1+x+x^2]), x]", //
        "-Sqrt[1+x+x^2]/x+ArcTanh[(2+x)/(2*Sqrt[1+x+x^2])]/2" //
    );
  }

  public void test275() {
    check( //
        "Integrate[1/(x^3*Sqrt[1+x+x^2]), x]", //
        "-Sqrt[1+x+x^2]/(2*x^2)+(3*Sqrt[1+x+x^2])/(4*x)+ArcTanh[(2+x)/(2*Sqrt[1+x+x^2])]/8" //
    );
  }

  public void test276() {
    check( //
        "Integrate[1/(x^2*(1+x+x^2)^(3/2)), x]", //
        "(2*(1-x))/(3*x*Sqrt[1+x+x^2])-(5*Sqrt[1+x+x^2])/(3*x)+3/2*ArcTanh[(2+x)/(2*Sqrt[1+x+x^2])]" //
    );
  }

  public void test277() {
    check( //
        "Integrate[1/(x^3*(1+x+x^2)^(3/2)), x]", //
        "(2*(1-x))/(3*x^2*Sqrt[1+x+x^2])-(7*Sqrt[1+x+x^2])/(6*x^2)+(37*Sqrt[1+x+x^2])/(12*x)-3/8*ArcTanh[(2+x)/(2*Sqrt[1+x+x^2])]" //
    );
  }

  public void test278() {
    check( //
        "Integrate[1/((x+1)*Sqrt[1+x+x^2]), x]", //
        "-ArcTanh[(1-x)/(2*Sqrt[1+x+x^2])]" //
    );
  }

  public void test279() {
    check( //
        "Integrate[1/((x^3-x)*Sqrt[x^2+2*x+4]), x]", //
        "ArcTanh[(4+x)/(2*Sqrt[x^2+2*x+4])]/2-ArcTanh[(5+2*x)/(Sqrt[7]*Sqrt[x^2+2*x+4])]/(2*Sqrt[7])-ArcTanh[Sqrt[x^2+2*x+4]/Sqrt[3]]/(2*Sqrt[3])" //
    );
  }

  public void test280() {
    check( //
        "Integrate[Sqrt[x^2+2*x+4]/(x+(-1)*1)^2, x]", //
        "Sqrt[x^2+2*x+4]/(1-x)+ArcSinh[(1+x)/Sqrt[3]]-2/Sqrt[7]*ArcTanh[(5+2*x)/(Sqrt[7]*Sqrt[x^2+2*x+4])]" //
    );
  }

  public void test281() {
    check( //
        "Integrate[(2*x+3)/((x^2+2*x+3)^2*Sqrt[x^2+2*x+4]), x]", //
        "-((3-x)*Sqrt[4+2*x+x^2])/(4*(3+2*x+x^2))-ArcTan[(1+x)/(Sqrt[2]*Sqrt[4+2*x+x^2])]/(4*Sqrt[2])+ArcTanh[Sqrt[4+2*x+x^2]]" //
    );
  }

  public void test282() {
    check( //
        "Integrate[(2*x^3+3*x^2)/((2*x^2+x+(-1)*3)*Sqrt[x^2+2*x+(-1)*3]), x]", //
        "Sqrt[x^2+2*x+(-1)*3]+Sqrt[x^2+2*x+(-1)*3]/(2*(1-x))" //
    );
  }

  public void test283() {
    check( //
        "Integrate[(x^4+1)/((x^2+x+1)*Sqrt[x^2+x+2]), x]", //
        "-7/4*Sqrt[x^2+x+2]+1/2*x*Sqrt[x^2+x+2]-ArcSinh[(1+2*x)/Sqrt[7]]/8+ArcTan[(1+2*x)/(Sqrt[3]*Sqrt[x^2+x+2])]/Sqrt[3]-ArcTanh[Sqrt[x^2+x+2]]" //
    );
  }

  public void test284() {
    check( //
        "Integrate[1/(x^2+2*x+4)^(7/2), x]", //
        "(1+x)/(15*(x^2+2*x+4)^(5/2))+(4*(1+x))/(135*(x^2+2*x+4)^(3/2))+(8*(1+x))/(405*Sqrt[x^2+2*x+4])" //
    );
  }

  public void test285() {
    check( //
        "Integrate[1/(3*x^2+8*x+1)^(5/2), x]", //
        "-(4+3*x)/(39*(3*x^2+8*x+1)^(3/2))+(2*(4+3*x))/(169*Sqrt[3*x^2+8*x+1])" //
    );
  }

  public void test286() {
    check( //
        "Integrate[1/(5+4*x-3*x^2)^(5/2), x]", //
        "-(2-3*x)/(57*(5+4*x-3*x^2)^(3/2))-(2*(2-3*x))/(361*Sqrt[5+4*x-3*x^2])" //
    );
  }

  public void test287() {
    check( //
        "Integrate[1/(1+Sqrt[x^2+2*x+2]), x]", //
        "1/(1+x)-Sqrt[x^2+2*x+2]/(1+x)+ArcSinh[1+x]" //
    );
  }

  public void test288() {
    check( //
        "Integrate[1/(x+Sqrt[1+x+x^2]), x]", //
        "-x+Sqrt[1+x+x^2]-3/2*ArcSinh[(1+2*x)/Sqrt[3]]+2*Log[x+Sqrt[1+x+x^2]]" //
    );
  }

  public void test289() {
    check( //
        "Integrate[x^2/(2*x+1+2*Sqrt[1+x+x^2]), x]", //
        "-x^3/9-x^4/6+1/96*(1+2*x)*Sqrt[1+x+x^2]-5/36*(1+x+x^2)^(3/2)+1/6*x*(1+x+x^2)^(3/2)+ArcSinh[(1+2*x)/Sqrt[3]]/64" //
    );
  }

  public void test290() {
    check( //
        "Integrate[(Sqrt[1+x+x^2]-3*x)/(Sqrt[1+x+x^2]+(-1)*1), x]", //
        "x-3*Sqrt[1+x+x^2]+5/2*ArcSinh[(1+2*x)/Sqrt[3]]+4*ArcTanh[(1-x)/(2*Sqrt[1+x+x^2])]-ArcTanh[(2+x)/(2*Sqrt[1+x+x^2])]+Log[x]-4*Log[1+x]" //
    );
  }

  public void test291() {
    check( //
        "Integrate[(x+1)/(Sqrt[x^2+2*x+4]-Sqrt[x^2+x+1]), x]", //
        "-2*Sqrt[x^2+x+1]+1/4*(1+2*x)*Sqrt[x^2+x+1]-2*Sqrt[x^2+2*x+4]+1/2*(1+x)*Sqrt[x^2+2*x+4]+11/2*ArcSinh[(1+x)/Sqrt[3]]+43/8*ArcSinh[(1+2*x)/Sqrt[3]]-2*Sqrt[7]*ArcTanh[(1+5*x)/(2*Sqrt[7]*Sqrt[x^2+x+1])]+2*Sqrt[7]*ArcTanh[(1-2*x)/(Sqrt[7]*Sqrt[x^2+2*x+4])]" //
    );
  }

  public void test292() {
    check( //
        "Integrate[1/(x^3*Sqrt[x+(-1)*1]), x]", //
        "Sqrt[x+(-1)*1]/(2*x^2)+(3*Sqrt[x+(-1)*1])/(4*x)+3/4*ArcTan[Sqrt[x+(-1)*1]]" //
    );
  }

  public void test293() {
    check( //
        "Integrate[1/(x^2*(1-3/x)^(4/3)), x]", //
        "-1/(1-3/x)^(1/3)" //
    );
  }

  public void test294() {
    check( //
        "Integrate[(3*x+(-1)*1)^(4/3)/x^2, x]", //
        "12*(3*x+(-1)*1)^(1/3)-(3*x+(-1)*1)^(4/3)/x+4*Sqrt[3]*ArcTan[(1-2*(3*x+(-1)*1)^(1/3))/Sqrt[3]]+2*Log[x]-6*Log[1+(3*x+(-1)*1)^(1/3)]" //
    );
  }

  public void test295() {
    check( //
        "Integrate[(4-3*x)^(4/3)*x^2, x]", //
        "-16/63*(4-3*x)^(7/3)+4/45*(4-3*x)^(10/3)-(4-3*x)^(13/3)/117" //
    );
  }

  public void test296() {
    check( //
        "Integrate[(1-2*x^(1/3))^(3/4)/x, x]", //
        "4*(1-2*x^(1/3))^(3/4)+6*ArcTan[(1-2*x^(1/3))^(1/4)]-6*ArcTanh[(1-2*x^(1/3))^(1/4)]" //
    );
  }

  public void test297() {
    check( //
        "Integrate[x/(3-2*Sqrt[x])^(3/4), x]", //
        "-27/2*(3-2*Sqrt[x])^(1/4)+27/10*(3-2*Sqrt[x])^(5/4)-(3-2*Sqrt[x])^(9/4)/2+(3-2*Sqrt[x])^(13/4)/26" //
    );
  }

  public void test298() {
    check( //
        "Integrate[(2*Sqrt[x]+(-1)*1)^(5/4)/x^2, x]", //
        "-(2*Sqrt[x]+(-1)*1)^(5/4)/x-(5*(2*Sqrt[x]+(-1)*1)^(1/4))/(2*Sqrt[x])-(5*ArcTan[1-Sqrt[2]*(2*Sqrt[x]+(-1)*1)^(1/4)])/(2*Sqrt[2])+(5*ArcTan[1+Sqrt[2]*(2*Sqrt[x]+(-1)*1)^(1/4)])/(2*Sqrt[2])-(5*Log[1-Sqrt[2]*(2*Sqrt[x]+(-1)*1)^(1/4)+Sqrt[2*Sqrt[x]+(-1)*1]])/(4*Sqrt[2])+(5*Log[1+Sqrt[2]*(2*Sqrt[x]+(-1)*1)^(1/4)+Sqrt[2*Sqrt[x]+(-1)*1]])/(4*Sqrt[2])" //
    );
  }

  public void test299() {
    check( //
        "Integrate[(x^7+1)^(1/3)*x^6, x]", //
        "3/28*(x^7+1)^(4/3)" //
    );
  }

  public void test300() {
    check( //
        "Integrate[x^6/(x^7+1)^(5/3), x]", //
        "-3/(14*(x^7+1)^(2/3))" //
    );
  }

  public void test301() {
    check( //
        "Integrate[1/(x*(2*x^7+(-1)*27)^(2/3)), x]", //
        "-1/(21*Sqrt[3])*ArcTan[(3-2*(2*x^7+(-1)*27)^(1/3))/(3*Sqrt[3])]-Log[x]/18+Log[3+(2*x^7+(-1)*27)^(1/3)]/42" //
    );
  }

  public void test302() {
    check( //
        "Integrate[(x^7+1)^(2/3)/x^8, x]", //
        "-(x^7+1)^(2/3)/(7*x^7)+(2*ArcTan[(1+2*(x^7+1)^(1/3))/Sqrt[3]])/(7*Sqrt[3])-Log[x]/3+Log[1-(x^7+1)^(1/3)]/7" //
    );
  }

  public void test303() {
    check( //
        "Integrate[(3+4*x^4)^(1/4)/x^2, x]", //
        "-(3+4*x^4)^(1/4)/x-ArcTan[(Sqrt[2]*x)/(3+4*x^4)^(1/4)]/Sqrt[2]+ArcTanh[(Sqrt[2]*x)/(3+4*x^4)^(1/4)]/Sqrt[2]" //
    );
  }

  public void test304() {
    check( //
        "Integrate[x^2*(3+4*x^4)^(5/4), x]", //
        "15/32*x^3*(3+4*x^4)^(1/4)+1/8*x^3*(3+4*x^4)^(5/4)-(45*ArcTan[(Sqrt[2]*x)/(3+4*x^4)^(1/4)])/(128*Sqrt[2])+(45*ArcTanh[(Sqrt[2]*x)/(3+4*x^4)^(1/4)])/(128*Sqrt[2])" //
    );
  }

  public void test305() {
    check( //
        "Integrate[x^6*(3+4*x^4)^(1/4), x]", //
        "3/128*x^3*(3+4*x^4)^(1/4)+1/8*x^7*(3+4*x^4)^(1/4)+(27*ArcTan[(Sqrt[2]*x)/(3+4*x^4)^(1/4)])/(512*Sqrt[2])-(27*ArcTanh[(Sqrt[2]*x)/(3+4*x^4)^(1/4)])/(512*Sqrt[2])" //
    );
  }

  public void test306() {
    check( //
        "Integrate[(x*(1-x^2))^(1/3), x]", //
        "1/2*x*(x*(1-x^2))^(1/3)+ArcTan[(2*x-(x*(1-x^2))^(1/3))/(Sqrt[3]*(x*(1-x^2))^(1/3))]/(2*Sqrt[3])+Log[x]/12-Log[x+(x*(1-x^2))^(1/3)]/4" //
    );
  }

  public void test307() {
    check( //
        "Integrate[Sqrt[x*(1+x^(1/3))], x]", //
        "7/64*Sqrt[x*(1+x^(1/3))]-(21*Sqrt[x*(1+x^(1/3))])/(128*x^(1/3))-7/80*x^(1/3)*Sqrt[x*(1+x^(1/3))]+3/40*x^(2/3)*Sqrt[x*(1+x^(1/3))]+3/5*x*Sqrt[x*(1+x^(1/3))]+21/128*ArcTanh[x^(2/3)/Sqrt[x*(1+x^(1/3))]]" //
    );
  }

  public void test308() {
    check( //
        "Integrate[x^3/((x^4+(-1)*1)*Sqrt[2*x^8+1]), x]", //
        "-1/(4*Sqrt[3])*ArcTanh[(2*x^4+1)/(Sqrt[3]*Sqrt[2*x^8+1])]" //
    );
  }

  public void test309() {
    check( //
        "Integrate[x^9*Sqrt[1+x^5+x^10], x]", //
        "-1/40*(1+2*x^5)*Sqrt[1+x^5+x^10]+(1+x^5+x^10)^(3/2)/15-3/80*ArcSinh[(1+2*x^5)/Sqrt[3]]" //
    );
  }

  public void test310() {
    check( //
        "Integrate[1/(x^5*Sqrt[4+2*x^2+x^4]), x]", //
        "-Sqrt[4+2*x^2+x^4]/(16*x^4)+(3*Sqrt[4+2*x^2+x^4])/(64*x^2)+ArcTanh[(4+x^2)/(2*Sqrt[4+2*x^2+x^4])]/128" //
    );
  }

  public void test311() {
    check( //
        "Integrate[(x^2+(-1)*1)/(x*Sqrt[1+3*x^2+x^4]), x]", //
        "ArcTanh[(1+x^2)/Sqrt[1+3*x^2+x^4]]" //
    );
  }

  public void test312() {
    check( //
        "Integrate[(x^4-3*x^2)^(3/5)*(2*x^3-3*x), x]", //
        "5/16*(x^4-3*x^2)^(8/5)" //
    );
  }

  public void test313() {
    check( //
        "Integrate[(3*x^8-2*x^5-x^2*(3*x^3+(-1)*1)^(2/3))/(3*x^3+(-1)*1)^(3/4), x]", //
        "-4/27*(3*x^3+(-1)*1)^(1/4)-4/33*(3*x^3+(-1)*1)^(11/12)+4/243*(3*x^3+(-1)*1)^(9/4)" //
    );
  }

  public void test314() {
    check( //
        "Integrate[1/((x^3+(-1)*1)*(x^3+2)^(1/3)), x]", //
        "-ArcTan[(1+(2*3^(1/3)*x)/(2+x^3)^(1/3))/Sqrt[3]]/3^(5/6)-Log[-1+x^3]/(6*3^(1/3))+Log[3^(1/3)*x-(2+x^3)^(1/3)]/(2*3^(1/3))" //
    );
  }

  public void test315() {
    check( //
        "Integrate[1/((x^4+1)*(x^4+2)^(1/4)), x]", //
        "-ArcTan[1-(Sqrt[2]*x)/(x^4+2)^(1/4)]/(2*Sqrt[2])+ArcTan[1+(Sqrt[2]*x)/(x^4+2)^(1/4)]/(2*Sqrt[2])-Log[1+x^2/Sqrt[x^4+2]-(Sqrt[2]*x)/(x^4+2)^(1/4)]/(4*Sqrt[2])+Log[1+x^2/Sqrt[x^4+2]+(Sqrt[2]*x)/(x^4+2)^(1/4)]/(4*Sqrt[2])" //
    );
  }

  public void test316() {
    check( //
        "Integrate[(x^3+(-1)*1)/(x^3+2)^(1/3), x]", //
        "1/3*x*(2+x^3)^(2/3)-(5*ArcTan[(1+(2*x)/(2+x^3)^(1/3))/Sqrt[3]])/(3*Sqrt[3])+5/6*Log[-x+(2+x^3)^(1/3)]" //
    );
  }

  public void test317() {
    check( //
        "Integrate[(x^4+1)^(3/4)/(x^4+2)^2, x]", //
        "(x*(x^4+1)^(3/4))/(8*(x^4+2))+(3*ArcTan[x/(2^(1/4)*(x^4+1)^(1/4))])/(16*2^(3/4))+(3*ArcTanh[x/(2^(1/4)*(x^4+1)^(1/4))])/(16*2^(3/4))" //
    );
  }

  public void test318() {
    check( //
        "Integrate[(x^5+(-1)*2)^2/((x^5+3)^3*(x^5+3)^(1/5)), x]", //
        "-5*x*(x^5+(-1)*2)/(33*(x^5+3)^(11/5))+(5*x)/(297*(x^5+3)^(6/5))+(97*x)/(891*(x^5+3)^(1/5))" //
    );
  }

  public void test319() {
    check( //
        "Integrate[1/((x^3+3*x^2+3*x)*(x^3+3*x^2+3*x+3)^(1/3)), x]", //
        "-ArcTan[(1+(2*3^(1/3)*(1+x))/(2+(1+x)^3)^(1/3))/Sqrt[3]]/3^(5/6)-Log[1-(1+x)^3]/(6*3^(1/3))+Log[3^(1/3)*(1+x)-(2+(1+x)^3)^(1/3)]/(2*3^(1/3))" //
    );
  }

  public void test320() {
    check( //
        "Integrate[(1-x^2)/((1+x^2)*Sqrt[1+x^4]), x]", //
        "ArcTan[(Sqrt[2]*x)/Sqrt[1+x^4]]/Sqrt[2]" //
    );
  }

  public void test321() {
    check( //
        "Integrate[(1+x^2)/((1-x^2)*Sqrt[1+x^4]), x]", //
        "ArcTanh[(Sqrt[2]*x)/Sqrt[1+x^4]]/Sqrt[2]" //
    );
  }

  public void test322() {
    check( //
        "Integrate[(x^2+1)/(x*Sqrt[1+x^4]), x]", //
        "ArcTanh[(x^2+(-1)*1)/Sqrt[1+x^4]]" //
    );
  }

  public void test323() {
    check( //
        "Integrate[(x^2+(-1)*1)/(x*Sqrt[1+x^4]), x]", //
        "ArcTanh[(x^2+1)/Sqrt[1+x^4]]" //
    );
  }

  public void test324() {
    check( //
        "Integrate[(1+x^2)/((1-x^2)*Sqrt[1+x^2+x^4]), x]", //
        "ArcTanh[(Sqrt[3]*x)/Sqrt[1+x^2+x^4]]/Sqrt[3]" //
    );
  }

  public void test325() {
    check( //
        "Integrate[(1-x^2)/((1+x^2)*Sqrt[1+x^2+x^4]), x]", //
        "ArcTan[x/Sqrt[1+x^2+x^4]]" //
    );
  }

  public void test326() {
    check( //
        "Integrate[(x^4+(-1)*1)/(x^2*Sqrt[x^4+x^2+1]), x]", //
        "Sqrt[x^4+x^2+1]/x" //
    );
  }

  public void test327() {
    check( //
        "Integrate[(1-x^2)/((1+2*a*x+x^2)*Sqrt[1+2*a*x+2*b*x^2+2*a*x^3+x^4]), x]", //
        "ArcTan[(a+2*(a^2-b+1)*x+a*x^2)/(Sqrt[2]*Sqrt[1-b]*Sqrt[1+2*a*x+2*b*x^2+2*a*x^3+x^4])]/(Sqrt[2]*Sqrt[1-b])" //
    );
  }

  public void test328() {
    check( //
        "Integrate[1/((1+x^4)*Sqrt[Sqrt[1+x^4]-x^2]), x]", //
        "ArcTan[x/Sqrt[Sqrt[1+x^4]-x^2]]" //
    );
  }

  public void test329() {
    check( //
        "Integrate[1/((1+x^(2*n))*Sqrt[(1+x^(2*n))^(1/n)-x^2]), x]", //
        "ArcTan[x/Sqrt[(1+x^(2*n))^(1/n)-x^2]]" //
    );
  }

  public void test330() {
    check( //
        "Integrate[Cos[x]^2, x]", //
        "x/2+1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test331() {
    check( //
        "Integrate[Cos[x]^3, x]", //
        "Sin[x]-Sin[x]^3/3" //
    );
  }

  public void test332() {
    check( //
        "Integrate[Sin[x]^4, x]", //
        "1/8*3*x-3/8*Cos[x]*Sin[x]-1/4*Cos[x]*Sin[x]^3" //
    );
  }

  public void test333() {
    check( //
        "Integrate[Cos[x]^6, x]", //
        "1/16*5*x+5/16*Cos[x]*Sin[x]+5/24*Cos[x]^3*Sin[x]+1/6*Cos[x]^5*Sin[x]" //
    );
  }

  public void test334() {
    check( //
        "Integrate[Sin[x]^8, x]", //
        "1/128*35*x-35/128*Cos[x]*Sin[x]-35/192*Cos[x]*Sin[x]^3-7/48*Cos[x]*Sin[x]^5-1/8*Cos[x]*Sin[x]^7" //
    );
  }

  public void test335() {
    check( //
        "Integrate[Cos[Pi/4+x/2]^4, x]", //
        "1/8*3*x+Cos[x]/2-1/8*Cos[x]*Sin[x]" //
    );
  }

  public void test336() {
    check( //
        "Integrate[Sin[3*x-Pi/12]^3, x]", //
        "-Cos[Pi/12-3*x]/3+Cos[Pi/12-3*x]^3/9" //
    );
  }

  public void test337() {
    check( //
        "Integrate[1/Sin[x]^6, x]", //
        "-Cot[x]-1/3*2*Cot[x]^3-Cot[x]^5/5" //
    );
  }

  public void test338() {
    check( //
        "Integrate[Csc[x]^7, x]", //
        "-5/16*ArcTanh[Cos[x]]-5/16*Cot[x]*Csc[x]-5/24*Cot[x]*Csc[x]^3-1/6*Cot[x]*Csc[x]^5" //
    );
  }

  public void test339() {
    check( //
        "Integrate[1/Cos[x]^12, x]", //
        "Tan[x]+1/3*5*Tan[x]^3+2*Tan[x]^5+1/7*10*Tan[x]^7+1/9*5*Tan[x]^9+Tan[x]^11/11" //
    );
  }

  public void test340() {
    check( //
        "Integrate[1/Cos[Pi/4+3*x]^3, x]", //
        "ArcTanh[Sin[Pi/4+3*x]]/6+1/6*Sec[Pi/4+3*x]*Tan[Pi/4+3*x]" //
    );
  }

  public void test341() {
    check( //
        "Integrate[Tan[x]^6, x]", //
        "-x+Tan[x]-Tan[x]^3/3+Tan[x]^5/5" //
    );
  }

  public void test342() {
    check( //
        "Integrate[1/Tan[x]^5, x]", //
        "Cot[x]^2/2-Cot[x]^4/4+Log[Sin[x]]" //
    );
  }

  public void test343() {
    check( //
        "Integrate[Cot[x/3-3*Pi/4]^4, x]", //
        "x+3*Cot[Pi/4+x/3]-Cot[Pi/4+x/3]^3" //
    );
  }

  public void test344() {
    check( //
        "Integrate[Sin[x]^4*Cos[x]^6, x]", //
        "1/256*3*x+3/256*Cos[x]*Sin[x]+1/128*Cos[x]^3*Sin[x]+1/160*Cos[x]^5*Sin[x]-3/80*Cos[x]^7*Sin[x]-1/10*Cos[x]^7*Sin[x]^3" //
    );
  }

  public void test345() {
    check( //
        "Integrate[Sin[x]^7*Cos[x]^6, x]", //
        "-Cos[x]^7/7+Cos[x]^9/3-1/11*3*Cos[x]^11+Cos[x]^13/13" //
    );
  }

  public void test346() {
    check( //
        "Integrate[Sin[x]^11/Cos[x], x]", //
        "1/2*5*Cos[x]^2-1/2*5*Cos[x]^4+1/3*5*Cos[x]^6-1/8*5*Cos[x]^8+Cos[x]^10/10-Log[Cos[x]]" //
    );
  }

  public void test347() {
    check( //
        "Integrate[1/(Sin[x]^6*Cos[x]^6), x]", //
        "-10*Cot[x]-1/3*5*Cot[x]^3-Cot[x]^5/5+10*Tan[x]+1/3*5*Tan[x]^3+Tan[x]^5/5" //
    );
  }

  public void test348() {
    check( //
        "Integrate[Sin[x]^2*Cos[x]^2, x]", //
        "x/8+1/8*Cos[x]*Sin[x]-1/4*Cos[x]^3*Sin[x]" //
    );
  }

  public void test349() {
    check( //
        "Integrate[Sin[x]^4*Cos[x]^4, x]", //
        "1/128*3*x+3/128*Cos[x]*Sin[x]+1/64*Cos[x]^3*Sin[x]-1/16*Cos[x]^5*Sin[x]-1/8*Cos[x]^5*Sin[x]^3" //
    );
  }

  public void test350() {
    check( //
        "Integrate[Sin[x]^6*Cos[x]^6, x]", //
        "1/1024*5*x+1/1024*5*Cos[x]*Sin[x]+1/1536*5*Cos[x]^3*Sin[x]+1/384*Cos[x]^5*Sin[x]-1/64*Cos[x]^7*Sin[x]-1/24*Cos[x]^7*Sin[x]^3-1/12*Cos[x]^7*Sin[x]^5" //
    );
  }

  public void test351() {
    check( //
        "Integrate[Sin[x]^8*Cos[x]^8, x]", //
        "1/32768*35*x+1/32768*35*Cos[x]*Sin[x]+1/49152*35*Cos[x]^3*Sin[x]+1/12288*7*Cos[x]^5*Sin[x]+1/2048*Cos[x]^7*Sin[x]-1/256*Cos[x]^9*Sin[x]-5/384*Cos[x]^9*Sin[x]^3-1/32*Cos[x]^9*Sin[x]^5-1/16*Cos[x]^9*Sin[x]^7" //
    );
  }

  public void test352() {
    check( //
        "Integrate[Sin[x]^(2*m)*Cos[x]^(2*m), x]", //
        "(Cos[x]^(-1+2*m)*(Cos[x]^2)^(1/2-m)*Hypergeometric2F1[1/2*(1-2*m),1/2*(1+2*m),1/2*(3+2*m),Sin[x]^2]*Sin[x]^(1+2*m))/(1+2*m)" //
    );
  }

  public void test353() {
    check( //
        "Integrate[1/(Sin[Pi/4+2*x]^3*Cos[Pi/4+2*x]), x]", //
        "-Cot[Pi/4+2*x]^2/4+Log[Tan[Pi/4+2*x]]/2" //
    );
  }

  public void test354() {
    check( //
        "Integrate[Tan[x]^2*Sec[x]^2, x]", //
        "Tan[x]^3/3" //
    );
  }

  public void test355() {
    check( //
        "Integrate[Cot[x]^3*Csc[x], x]", //
        "Csc[x]-Csc[x]^3/3" //
    );
  }

  public void test356() {
    check( //
        "Integrate[Tan[x]*Sec[x]^3, x]", //
        "Sec[x]^3/3" //
    );
  }

  public void test357() {
    check( //
        "Integrate[Cot[x]^2*Csc[x]^3, x]", //
        "ArcTanh[Cos[x]]/8+1/8*Cot[x]*Csc[x]-1/4*Cot[x]*Csc[x]^3" //
    );
  }

  public void test358() {
    check( //
        "Integrate[Cos[x]^3/Sin[x]^7, x]", //
        "Csc[x]^4/4-Csc[x]^6/6" //
    );
  }

  public void test359() {
    check( //
        "Integrate[Tan[x]^5*Sec[x]^(3/2), x]", //
        "2/3*Sec[x]^(3/2)-4/7*Sec[x]^(7/2)+2/11*Sec[x]^(11/2)" //
    );
  }

  public void test360() {
    check( //
        "Integrate[Tan[x]^(3/2)*Sec[x]^4, x]", //
        "2/5*Tan[x]^(5/2)+2/9*Tan[x]^(9/2)" //
    );
  }

  public void test361() {
    check( //
        "Integrate[Cot[x]^4*Csc[x]^3, x]", //
        "-ArcTanh[Cos[x]]/16-1/16*Cot[x]*Csc[x]+1/8*Cot[x]*Csc[x]^3-1/6*Cot[x]^3*Csc[x]^3" //
    );
  }

  public void test362() {
    check( //
        "Integrate[Tan[Pi/4+x/2]^2*Sec[Pi/4+x/2]^3, x]", //
        "-ArcTanh[Sin[Pi/4+x/2]]/4-1/4*Sec[Pi/4+x/2]*Tan[Pi/4+x/2]+1/2*Sec[Pi/4+x/2]^3*Tan[Pi/4+x/2]" //
    );
  }

  public void test363() {
    check( //
        "Integrate[(a*Sec[x]^2-Sin[2*x])^2*(Cot[x]^3+1), x]", //
        "x/2+4*a*x+2*Cos[x]^2+Cos[x]^4+4*a*Cot[x]-1/2*a^2*Cot[x]^2+(4-a)*a*Log[Cos[x]]+(4+a^2)*Log[Sin[x]]+1/2*Cos[x]*Sin[x]-Cos[x]^3*Sin[x]+a^2*Tan[x]+1/3*a^2*Tan[x]^3" //
    );
  }

  public void test364() {
    check( //
        "Integrate[(1-Sin[x]/2)^4*(4-3*Cos[x]), x]", //
        "1/32*227*x+10*Cos[x]-3*Cos[x]^2-1/3*2*Cos[x]^3-3*Sin[x]-99/32*Cos[x]*Sin[x]-1/2*3*Sin[x]^3-1/16*Cos[x]*Sin[x]^3+1/8*3*Sin[x]^4-1/80*3*Sin[x]^5" //
    );
  }

  public void test365() {
    check( //
        "Integrate[(3-2*Cot[x])^3*(1/2-3*Cot[x]), x]", //
        "-1/2*285*x+5*(3-2*Cot[x])^2+(3-2*Cot[x])^3-42*Cot[x]+4*Log[Sin[x]]" //
    );
  }

  public void test366() {
    check( //
        "Integrate[Cos[5*x]/Cos[x]^5, x]", //
        "16*x-15*Tan[x]+1/3*5*Tan[x]^3" //
    );
  }

  public void test367() {
    check( //
        "Integrate[Cos[4*x]/Cos[x], x]", //
        "ArcTanh[Sin[x]]-1/3*8*Sin[x]^3" //
    );
  }

  public void test368() {
    check( //
        "Integrate[Cos[4*x]*Cos[x], x]", //
        "Sin[3*x]/6+Sin[5*x]/10" //
    );
  }

  public void test369() {
    check( //
        "Integrate[Cos[4*x]/Cos[x]^5, x]", //
        "35/8*ArcTanh[Sin[x]]-29/8*Sec[x]*Tan[x]+1/4*Sec[x]^3*Tan[x]" //
    );
  }

  public void test370() {
    check( //
        "Integrate[Cos[4*x]*Cos[x]^4, x]", //
        "x/16+Sin[2*x]/8+3/32*Sin[4*x]+Sin[6*x]/24+Sin[8*x]/128" //
    );
  }

  public void test371() {
    check( //
        "Integrate[Cos[5*x]/Sin[x]^5, x]", //
        "6*Csc[x]^2-Csc[x]^4/4+16*Log[Sin[x]]" //
    );
  }

  public void test372() {
    check( //
        "Integrate[Sin[4*x]/Sin[x]^4, x]", //
        "-2*Csc[x]^2-8*Log[Sin[x]]" //
    );
  }

  public void test373() {
    check( //
        "Integrate[Cos[x]/(Sin[x]*(2+Sin[2*x])), x]", //
        "-x/(2*Sqrt[3])+ArcTan[(1-2*Cos[x]^2)/(2+Sqrt[3]+2*Cos[x]*Sin[x])]/(2*Sqrt[3])+Log[Sin[x]]/2-Log[1+Cos[x]*Sin[x]]/4" //
    );
  }

  public void test374() {
    check( //
        "Integrate[Cos[x]^2/(Sin[x]*Cos[3*x]), x]", //
        "-Log[Csc[x]^2+(-1)*4]/2" //
    );
  }

  public void test375() {
    check( //
        "Integrate[Sin[2*x]/(Cos[x]^4+Sin[x]^4), x]", //
        "-ArcTan[Cos[2*x]]" //
    );
  }

  public void test376() {
    check( //
        "Integrate[1/(4+Sqrt[3]*Cos[x]+Sin[x]), x]", //
        "x/(2*Sqrt[3])+ArcTan[(Cos[x]-Sqrt[3]*Sin[x])/(2*(2+Sqrt[3])+Sqrt[3]*Cos[x]+Sin[x])]/Sqrt[3]" //
    );
  }

  public void test377() {
    check( //
        "Integrate[1/(3+4*Cos[x]+4*Sin[x]), x]", //
        "-1/Sqrt[23]*ArcTanh[Sqrt[23]*(Cos[x]-Sin[x])/(8+3*Cos[x]+3*Sin[x])]" //
    );
  }

  public void test378() {
    check( //
        "Integrate[1/(4-3*Cos[x]^2+5*Sin[x]^2), x]", //
        "x/3+ArcTan[(2*Cos[x]*Sin[x])/(1+2*Sin[x]^2)]/3" //
    );
  }

  public void test379() {
    check( //
        "Integrate[1/(4+Tan[x]+4*Cot[x]), x]", //
        "1/25*4*x-3/25*Log[2*Cos[x]+Sin[x]]+2/(5*(2+Tan[x]))" //
    );
  }

  public void test380() {
    check( //
        "Integrate[1/(Sin[x]+2*Sec[x])^2, x]", //
        "(8*x)/(15*Sqrt[15])-8/(15*Sqrt[15])*ArcTan[(1-2*Cos[x]^2)/(4+Sqrt[15]+2*Cos[x]*Sin[x])]+(1+4*Tan[x])/(15*(2+Tan[x]+2*Tan[x]^2))" //
    );
  }

  public void test381() {
    check( //
        "Integrate[1/(Cos[x]+2*Sec[x])^2, x]", //
        "x/(6*Sqrt[6])-ArcTan[(Cos[x]*Sin[x])/(2+Sqrt[6]+Cos[x]^2)]/(6*Sqrt[6])+Tan[x]/(6*(3+2*Tan[x]^2))" //
    );
  }

  public void test382() {
    check( //
        "Integrate[(5-Tan[x]-6*Tan[x]^2)/(1+3*Tan[x])^3, x]", //
        "-1/250*67*x-28/125*Log[Cos[x]+3*Sin[x]]-7/(10*(1+3*Tan[x])^2)-29/(50*(1+3*Tan[x]))" //
    );
  }

  public void test383() {
    check( //
        "Integrate[Cos[x]^2/Cos[3*x], x]", //
        "ArcTanh[2*Sin[x]]/2" //
    );
  }

  public void test384() {
    check( //
        "Integrate[Sin[x]/Cos[2*x], x]", //
        "ArcTanh[Sqrt[2]*Cos[x]]/Sqrt[2]" //
    );
  }

  public void test385() {
    check( //
        "Integrate[Sin[x]^2/Cos[2*x], x]", //
        "-x/2+ArcTanh[2*Cos[x]*Sin[x]]/4" //
    );
  }

  public void test386() {
    check( //
        "Integrate[Sin[x]^3/Cos[3*x], x]", //
        "Log[Cos[x]]/3-Log[3-4*Cos[x]^2]/24" //
    );
  }

  public void test387() {
    check( //
        "Integrate[Cos[x]/Sin[3*x], x]", //
        "Log[Sin[x]]/3-Log[3-4*Sin[x]^2]/6" //
    );
  }

  public void test388() {
    check( //
        "Integrate[Sin[x]/Sin[4*x], x]", //
        "-ArcTanh[Sin[x]]/4+ArcTanh[Sqrt[2]*Sin[x]]/(2*Sqrt[2])" //
    );
  }

  public void test389() {
    check( //
        "Integrate[Sin[x]^3/Sin[4*x], x]", //
        "-ArcTanh[Sin[x]]/4+ArcTanh[Sqrt[2]*Sin[x]]/(4*Sqrt[2])" //
    );
  }

  public void test390() {
    check( //
        "Integrate[Sqrt[1+Sin[2*x]], x]", //
        "-Cos[2*x]/Sqrt[1+Sin[2*x]]" //
    );
  }

  public void test391() {
    check( //
        "Integrate[Sqrt[1-Sin[2*x]], x]", //
        "Cos[2*x]/Sqrt[1-Sin[2*x]]" //
    );
  }

  public void test392() {
    check( //
        "Integrate[1/Sqrt[1+Cos[2*x]], x]", //
        "ArcTanh[Sin[2*x]/(Sqrt[2]*Sqrt[1+Cos[2*x]])]/Sqrt[2]" //
    );
  }

  public void test393() {
    check( //
        "Integrate[1/Sqrt[1-Cos[2*x]], x]", //
        "-1/Sqrt[2]*ArcTanh[Sin[2*x]/(Sqrt[2]*Sqrt[1-Cos[2*x]])]" //
    );
  }

  public void test394() {
    check( //
        "Integrate[1/(1-Cos[3*x])^(3/2), x]", //
        "-ArcTanh[Sin[3*x]/(Sqrt[2]*Sqrt[1-Cos[3*x]])]/(6*Sqrt[2])-Sin[3*x]/(6*(1-Cos[3*x])^(3/2))" //
    );
  }

  public void test395() {
    check( //
        "Integrate[(1-Sin[2*x/3])^(5/2), x]", //
        "(32*Cos[1/3*2*x])/(5*Sqrt[1-Sin[1/3*2*x]])+8/5*Cos[1/3*2*x]*Sqrt[1-Sin[1/3*2*x]]+3/5*Cos[1/3*2*x]*(1-Sin[1/3*2*x])^(3/2)" //
    );
  }

  public void test396() {
    check( //
        "Integrate[(2*(1+2*Sin[x])^(1/4)-Cos[x]^2)/(1+2*Sin[x])^(3/2)*Cos[x], x]", //
        "3/(4*Sqrt[1+2*Sin[x]])-4/(1+2*Sin[x])^(1/4)-Sqrt[1+2*Sin[x]]/2+(1+2*Sin[x])^(3/2)/12" //
    );
  }

  public void test397() {
    check( //
        "Integrate[Sqrt[Tan[x]], x]", //
        "-ArcTan[1-Sqrt[2]*Sqrt[Tan[x]]]/Sqrt[2]+ArcTan[1+Sqrt[2]*Sqrt[Tan[x]]]/Sqrt[2]+Log[1-Sqrt[2]*Sqrt[Tan[x]]+Tan[x]]/(2*Sqrt[2])-Log[1+Sqrt[2]*Sqrt[Tan[x]]+Tan[x]]/(2*Sqrt[2])" //
    );
  }

  public void test398() {
    check( //
        "Integrate[1/Tan[5*x]^(1/3), x]", //
        "-1/10*Sqrt[3]*ArcTan[(1-2*Tan[5*x]^(2/3))/Sqrt[3]]+3/20*Log[1+Tan[5*x]^(2/3)]-Log[1+Tan[5*x]^2]/20" //
    );
  }

  public void test399() {
    check( //
        "Integrate[1/(4+3*Tan[2*x])^(3/2), x]", //
        "-(9*ArcTan[(1-3*Tan[2*x])/(Sqrt[2]*Sqrt[4+3*Tan[2*x]])])/(250*Sqrt[2])+(13*ArcTanh[(3+Tan[2*x])/(Sqrt[2]*Sqrt[4+3*Tan[2*x]])])/(250*Sqrt[2])-3/(25*Sqrt[4+3*Tan[2*x]])" //
    );
  }

  public void test400() {
    check( //
        "Integrate[(3*Tan[x]-Sqrt[4-3*Tan[x]])/(Cos[x]^2*(4-3*Tan[x])^(3/2)), x]", //
        "Log[4-3*Tan[x]]/3+8/(3*Sqrt[4-3*Tan[x]])+2/3*Sqrt[4-3*Tan[x]]" //
    );
  }

  public void test401() {
    check( //
        "Integrate[Tan[x]/(Sqrt[Tan[x]]+(-1)*1)^2, x]", //
        "-x/2+ArcTan[(1-Tan[x])/(Sqrt[2]*Sqrt[Tan[x]])]/Sqrt[2]+ArcTanh[(1+Tan[x])/(Sqrt[2]*Sqrt[Tan[x]])]/Sqrt[2]+Log[Cos[x]]/2+Log[1-Sqrt[Tan[x]]]+1/(1-Sqrt[Tan[x]])" //
    );
  }

  public void test402() {
    check( //
        "Integrate[Sin[x]/Sqrt[Sin[2*x]], x]", //
        "-ArcSin[Cos[x]-Sin[x]]/2-Log[Cos[x]+Sin[x]+Sqrt[Sin[2*x]]]/2" //
    );
  }

  public void test403() {
    check( //
        "Integrate[Cos[x]/Sqrt[Sin[2*x]], x]", //
        "-ArcSin[Cos[x]-Sin[x]]/2+Log[Cos[x]+Sin[x]+Sqrt[Sin[2*x]]]/2" //
    );
  }

  public void test404() {
    check( //
        "Integrate[Sqrt[Sin[2*x]]*Sin[x], x]", //
        "-ArcSin[Cos[x]-Sin[x]]/4+Log[Cos[x]+Sin[x]+Sqrt[Sin[2*x]]]/4-1/2*Cos[x]*Sqrt[Sin[2*x]]" //
    );
  }

  public void test405() {
    check( //
        "Integrate[(Cos[x]-Sin[x])*Sqrt[Sin[2*x]], x]", //
        "-Log[Cos[x]+Sin[x]+Sqrt[Sin[2*x]]]/2+1/2*Cos[x]*Sqrt[Sin[2*x]]+1/2*Sin[x]*Sqrt[Sin[2*x]]" //
    );
  }

  public void test406() {
    check( //
        "Integrate[Sin[x]^7/Sin[2*x]^(7/2), x]", //
        "-ArcSin[Cos[x]-Sin[x]]/16+Log[Cos[x]+Sin[x]+Sqrt[Sin[2*x]]]/16+Sin[x]^5/(5*Sin[2*x]^(5/2))-Sin[x]/(4*Sqrt[Sin[2*x]])" //
    );
  }

  public void test407() {
    check( //
        "Integrate[Cos[x]^7/Sin[2*x]^(7/2), x]", //
        "-ArcSin[Cos[x]-Sin[x]]/16-Log[Cos[x]+Sin[x]+Sqrt[Sin[2*x]]]/16-Cos[x]^5/(5*Sin[2*x]^(5/2))+Cos[x]/(4*Sqrt[Sin[2*x]])" //
    );
  }

  public void test408() {
    check( //
        "Integrate[Sin[2*x]^(3/2)/Sin[x]^5, x]", //
        "-1/5*Csc[x]^5*Sin[2*x]^(5/2)" //
    );
  }

  public void test409() {
    check( //
        "Integrate[1/(Cos[x]^3*Sqrt[Sin[2*x]]), x]", //
        "4/5*Sec[x]*Sqrt[Sin[2*x]]+1/5*Sec[x]^3*Sqrt[Sin[2*x]]" //
    );
  }

  public void test410() {
    check( //
        "Integrate[1/(Sin[x]*Sin[2*x]^(3/2)), x]", //
        "-(2*Cos[x])/(3*Sin[2*x]^(3/2))+(4*Sin[x])/(3*Sqrt[Sin[2*x]])" //
    );
  }

  public void test411() {
    check( //
        "Integrate[(Cos[2*x]-3*Tan[x])*Cos[x]^3/((Sin[x]^2-Sin[2*x])*Sin[2*x]^(5/2)), x]", //
        "33/32*ArcTanh[Sqrt[Sin[2*x]]/(2*Cos[x])]-(9*Cos[x])/(16*Sqrt[Sin[2*x]])-(5*Cos[x]*Cot[x])/(24*Sqrt[Sin[2*x]])+(Cos[x]*Cot[x]^2)/(20*Sqrt[Sin[2*x]])" //
    );
  }

  public void test412() {
    check( //
        "Integrate[Sqrt[Sin[x]/Cos[x]^5], x]", //
        "2/3*Cos[x]*Sin[x]*Sqrt[Sec[x]^4*Tan[x]]" //
    );
  }

  public void test413() {
    check( //
        "Integrate[Sqrt[Sin[x]^5/Cos[x]], x]", //
        "3/(4*Sqrt[2])*ArcTan[(1-Cot[x])*Csc[x]^2*Sqrt[Sin[x]^4*Tan[x]]/Sqrt[2]]+3/(4*Sqrt[2])*Log[Cos[x]+Sin[x]-Sqrt[2]*Cot[x]*Csc[x]*Sqrt[Sin[x]^4*Tan[x]]]-1/2*Cot[x]*Sqrt[Sin[x]^4*Tan[x]]" //
    );
  }

  public void test414() {
    check( //
        "Integrate[(Sin[x]^2/Cos[x]^14)^(1/3), x]", //
        "3/5*Cos[x]^3*Sin[x]*(Sec[x]^12*Tan[x]^2)^(1/3)+3/11*Cos[x]*Sin[x]^3*(Sec[x]^12*Tan[x]^2)^(1/3)" //
    );
  }

  public void test415() {
    check( //
        "Integrate[1/(Sin[x]^13*Cos[x]^11)^(1/4), x]", //
        "-(4*Cos[x]^5*Sin[x])/(9*(Cos[x]^11*Sin[x]^13)^(1/4))-(8*Cos[x]^3*Sin[x]^3)/(Cos[x]^11*Sin[x]^13)^(1/4)+(4*Cos[x]*Sin[x]^5)/(7*(Cos[x]^11*Sin[x]^13)^(1/4))" //
    );
  }

  public void test416() {
    check( //
        "Integrate[(Cos[2*x]-Sqrt[Sin[2*x]])/Sqrt[Sin[x]*Cos[x]^3], x]", //
        "-Sqrt[2]*Log[Cos[x]+Sin[x]-Sqrt[2]*Sec[x]*Sqrt[Cos[x]^3*Sin[x]]]-ArcSin[Cos[x]-Sin[x]]*Cos[x]*Sqrt[Sin[2*x]]/Sqrt[Cos[x]^3*Sin[x]]-ArcTanh[Sin[x]]*Cos[x]*Sqrt[Sin[2*x]]/Sqrt[Cos[x]^3*Sin[x]]-Sin[2*x]/Sqrt[Cos[x]^3*Sin[x]]" //
    );
  }

  public void test417() {
    check( //
        "Integrate[(Sqrt[Sin[x]^3*Cos[x]]-2*Sin[2*x])/(Sqrt[Tan[x]]-Sqrt[Sin[x]*Cos[x]^3]), x]", //
        "-2*Sqrt[2]*ArcCoth[(Cos[x]*(Cos[x]+Sin[x]))/(Sqrt[2]*Sqrt[Cos[x]^3*Sin[x]])]+2^(1/4)*ArcCoth[(Cos[x]*(Sqrt[2]*Cos[x]+Sin[x]))/(2^(3/4)*Sqrt[Cos[x]^3*Sin[x]])]-2^(1/4)*ArcCoth[(Sqrt[2]+Tan[x])/(2^(3/4)*Sqrt[Tan[x]])]-2*Sqrt[2]*ArcTan[(Cos[x]*(Cos[x]-Sin[x]))/(Sqrt[2]*Sqrt[Cos[x]^3*Sin[x]])]+2^(1/4)*ArcTan[(Cos[x]*(Sqrt[2]*Cos[x]-Sin[x]))/(2^(3/4)*Sqrt[Cos[x]^3*Sin[x]])]-2^(1/4)*ArcTan[(Sqrt[2]-Tan[x])/(2^(3/4)*Sqrt[Tan[x]])]+4*Csc[x]*Sec[x]*Sqrt[Cos[x]^3*Sin[x]]+1/4*Csc[x]^2*Log[1+Cos[x]^2]*Sec[x]^2*Sqrt[Cos[x]^3*Sin[x]]*Sqrt[Cos[x]*Sin[x]^3]+1/2*Csc[x]^2*Log[Sin[x]]*Sec[x]^2*Sqrt[Cos[x]^3*Sin[x]]*Sqrt[Cos[x]*Sin[x]^3]+4/Sqrt[Tan[x]]-1/4*Csc[x]^2*Log[1+Cos[x]^2]*Sqrt[Cos[x]*Sin[x]^3]*Sqrt[Tan[x]]+1/2*Csc[x]^2*Log[Sin[x]]*Sqrt[Cos[x]*Sin[x]^3]*Sqrt[Tan[x]]" //
    );
  }

  public void test418() {
    check( //
        "Integrate[((Sin[x]/Cos[x]^7)^(1/3)-3*Tan[x])/(Sin[x]*Cos[x]^5)^(2/3), x]", //
        "-(9*Sin[x]^4)/(10*(Cos[x]^5*Sin[x])^(2/3))-9/4*Sec[x]^8*(Cos[x]^5*Sin[x])^(4/3)+3/2*(Cos[x]^5*Sin[x])^(1/3)*(Sec[x]^6*Tan[x])^(1/3)+3/4*(Cos[x]^5*Sin[x])^(1/3)*Tan[x]^2*(Sec[x]^6*Tan[x])^(1/3)+3/14*(Cos[x]^5*Sin[x])^(1/3)*Tan[x]^4*(Sec[x]^6*Tan[x])^(1/3)" //
    );
  }

  public void test419() {
    check( //
        "Integrate[(2*Cos[x]^2+1)^(5/2)*Sin[x], x]", //
        "-(5*ArcSinh[Sqrt[2]*Cos[x]])/(16*Sqrt[2])-5/16*Cos[x]*Sqrt[1+2*Cos[x]^2]-5/24*Cos[x]*(1+2*Cos[x]^2)^(3/2)-1/6*Cos[x]*(1+2*Cos[x]^2)^(5/2)" //
    );
  }

  public void test420() {
    check( //
        "Integrate[(5*Cos[x]^2+Sin[x]^2)^(5/2)*Cos[x], x]", //
        "625/32*ArcSin[(2*Sin[x])/Sqrt[5]]+125/16*Sin[x]*Sqrt[5-4*Sin[x]^2]+25/24*Sin[x]*(5-4*Sin[x]^2)^(3/2)+1/6*Sin[x]*(5-4*Sin[x]^2)^(5/2)" //
    );
  }

  public void test421() {
    check( //
        "Integrate[(-Cos[x]^2-5*Sin[x]^2)^(3/2)*Cos[x], x]", //
        "3/16*ArcTan[(2*Sin[x])/Sqrt[-1-4*Sin[x]^2]]-3/8*Sin[x]*Sqrt[-1-4*Sin[x]^2]+1/4*Sin[x]*(-1-4*Sin[x]^2)^(3/2)" //
    );
  }

  public void test422() {
    check( //
        "Integrate[Sin[x]/(5*Cos[x]^2-2*Sin[x]^2)^(7/2), x]", //
        "Cos[x]/(10*(-2+7*Cos[x]^2)^(5/2))-Cos[x]/(15*(-2+7*Cos[x]^2)^(3/2))+Cos[x]/(15*Sqrt[-2+7*Cos[x]^2])" //
    );
  }

  public void test423() {
    check( //
        "Integrate[Cos[2*x]*Cos[x]/(2-5*Sin[x]^2)^(3/2), x]", //
        "(2*ArcSin[Sqrt[5/2]*Sin[x]])/(5*Sqrt[5])+Sin[x]/(10*Sqrt[2-5*Sin[x]^2])" //
    );
  }

  public void test424() {
    check( //
        "Integrate[Sin[5*x]/(5*Cos[x]^2+9*Sin[x]^2)^(5/2), x]", //
        "-ArcSin[1/3*2*Cos[x]]/2-(55*Cos[x])/(27*(9-4*Cos[x]^2)^(3/2))+(295*Cos[x])/(243*Sqrt[9-4*Cos[x]^2])" //
    );
  }

  public void test425() {
    check( //
        "Integrate[Cos[x]*Cos[2*x]*Sin[3*x]/(4*Sin[x]^2+(-1)*5)^(5/2), x]", //
        "-1/(4*(-5+4*Sin[x]^2)^(3/2))-5/(8*Sqrt[-5+4*Sin[x]^2])+Sqrt[-5+4*Sin[x]^2]/8" //
    );
  }

  public void test426() {
    check( //
        "Integrate[(Sin[x]*Cos[2*x]-2*(Sin[x]+(-1)*1)*Cos[x]^3)/(Sin[x]^2*Sqrt[Sin[x]^2+(-1)*5]), x]", //
        "2*ArcTan[Cos[x]/Sqrt[Sin[x]^2+(-1)*5]]-ArcTan[(Sqrt[5]*Cos[x])/Sqrt[Sin[x]^2+(-1)*5]]/Sqrt[5]-2/Sqrt[5]*ArcTan[Sqrt[Sin[x]^2+(-1)*5]/Sqrt[5]]-2*ArcTanh[Sin[x]/Sqrt[Sin[x]^2+(-1)*5]]+(2*Sqrt[Sin[x]^2+(-1)*5])/(5*Sin[x])+2*Sqrt[Sin[x]^2+(-1)*5]" //
    );
  }

  public void test427() {
    check( //
        "Integrate[Cos[3*x]/(Sqrt[3*Cos[x]^2-Sin[x]^2]-Sqrt[8*Cos[x]^2+(-1)*1]), x]", //
        "5/(4*Sqrt[2])*ArcSin[2*Sqrt[2/7]*Sin[x]]+3/4*ArcSin[(2*Sin[x])/Sqrt[3]]-3/4*ArcTan[Sin[x]/Sqrt[4*Cos[x]^2+(-1)*1]]-3/4*ArcTan[Sin[x]/Sqrt[8*Cos[x]^2+(-1)*1]]-1/2*Sin[x]*Sqrt[4*Cos[x]^2+(-1)*1]-1/2*Sin[x]*Sqrt[8*Cos[x]^2+(-1)*1]" //
    );
  }

  public void test428() {
    check( //
        "Integrate[(2-3*Sin[x]^2)^(3/5)*Sin[4*x], x]", //
        "5/36*(2-3*Sin[x]^2)^(8/5)-20/117*(2-3*Sin[x]^2)^(13/5)" //
    );
  }

  public void test429() {
    check( //
        "Integrate[Sqrt[Cos[2*x]]*Cos[x], x]", //
        "ArcSin[Sqrt[2]*Sin[x]]/(2*Sqrt[2])+1/2*Sin[x]*Sqrt[Cos[2*x]]" //
    );
  }

  public void test430() {
    check( //
        "Integrate[Cos[2*x]^(3/2)*Sin[x], x]", //
        "-3/(8*Sqrt[2])*ArcTanh[(Sqrt[2]*Cos[x])/Sqrt[Cos[2*x]]]+3/8*Cos[x]*Sqrt[Cos[2*x]]-1/4*Cos[x]*Cos[2*x]^(3/2)" //
    );
  }

  public void test431() {
    check( //
        "Integrate[Sin[x]/Cos[2*x]^(5/2), x]", //
        "-Cos[3*x]/(3*Cos[2*x]^(3/2))" //
    );
  }

  public void test432() {
    check( //
        "Integrate[Cos[2*x]^(3/2)/Cos[x]^3, x]", //
        "2*Sqrt[2]*ArcSin[Sqrt[2]*Sin[x]]-5/2*ArcTan[Sin[x]/Sqrt[Cos[2*x]]]-1/2*Sec[x]*Tan[x]*Sqrt[Cos[2*x]]" //
    );
  }

  public void test433() {
    check( //
        "Integrate[(3*Sin[x]^3-Cos[x]*Sin[4*x])/(Csc[x]^2*Cos[2*x]^(7/2)), x]", //
        "-ArcTanh[(Sqrt[2]*Cos[x])/Sqrt[Cos[2*x]]]/Sqrt[2]-(11*Cos[x])/(20*Cos[2*x]^(3/2))-(2*Cos[x]^3)/(3*Cos[2*x]^(3/2))+(63*Cos[x])/(20*Sqrt[Cos[2*x]])+(3*Cos[x]*Sin[x]^2)/(10*Cos[2*x]^(5/2))" //
    );
  }

  public void test434() {
    check( //
        "Integrate[(4-5*Sec[x]^2)^(3/2), x]", //
        "8*ArcTan[(2*Tan[x])/Sqrt[-1-5*Tan[x]^2]]-7/2*Sqrt[5]*ArcTan[(Sqrt[5]*Tan[x])/Sqrt[-1-5*Tan[x]^2]]-5/2*Tan[x]*Sqrt[-1-5*Tan[x]^2]" //
    );
  }

  public void test435() {
    check( //
        "Integrate[1/(4-5*Sec[x]^2)^(3/2), x]", //
        "ArcTan[(2*Tan[x])/Sqrt[-1-5*Tan[x]^2]]/8-(5*Tan[x])/(4*Sqrt[-1-5*Tan[x]^2])" //
    );
  }

  public void test436() {
    check( //
        "Integrate[(Sin[x]-2*Cot[x]^2)/(1+5*Tan[x]^2)^(3/2), x]", //
        "-ArcTanh[(2*Tan[x])/Sqrt[1+5*Tan[x]^2]]/4-Cos[x]/(4*Sqrt[1+5*Tan[x]^2])-(5*Cot[x])/(2*Sqrt[1+5*Tan[x]^2])-1/8*Cos[x]*Sqrt[1+5*Tan[x]^2]+9/2*Cot[x]*Sqrt[1+5*Tan[x]^2]" //
    );
  }

  public void test437() {
    check( //
        "Integrate[(Cos[2*x]+(-1)*3)/(Cos[x]^4*Sqrt[4-Cot[x]^2]), x]", //
        "-2/3*Sqrt[4-Cot[x]^2]*Tan[x]-1/3*Sqrt[4-Cot[x]^2]*Tan[x]^3" //
    );
  }

  public void test438() {
    check( //
        "Integrate[(3+Sin[x]^2)*Tan[x]^3/((Cos[x]^2+(-1)*2)*(5-4*Sec[x]^2)^(3/2)), x]", //
        "-ArcTanh[Sqrt[5-4*Sec[x]^2]/Sqrt[3]]/(6*Sqrt[3])-ArcTanh[Sqrt[5-4*Sec[x]^2]/Sqrt[5]]/(5*Sqrt[5])-2/(15*Sqrt[5-4*Sec[x]^2])" //
    );
  }

  public void test439() {
    check( //
        "Integrate[(Sec[x]^2-3*Tan[x]*Sqrt[4*Sec[x]^2+5*Tan[x]^2])/(Sin[x]^2*(4*Sec[x]^2+5*Tan[x]^2)^(3/2)), x]", //
        "-3/4*Log[Tan[x]]+3/8*Log[4+9*Tan[x]^2]-Cot[x]/(4*Sqrt[4+9*Tan[x]^2])-(7*Tan[x])/(8*Sqrt[4+9*Tan[x]^2])" //
    );
  }

  public void test440() {
    check( //
        "Integrate[(1+5*Tan[x]^2)^(5/2)*Tan[x], x]", //
        "-32*ArcTan[Sqrt[1+5*Tan[x]^2]/2]+16*Sqrt[1+5*Tan[x]^2]-4/3*(1+5*Tan[x]^2)^(3/2)+(1+5*Tan[x]^2)^(5/2)/5" //
    );
  }

  public void test441() {
    check( //
        "Integrate[Tan[x]/(1+5*Tan[x]^2)^(5/2), x]", //
        "ArcTan[Sqrt[1+5*Tan[x]^2]/2]/32-1/(12*(1+5*Tan[x]^2)^(3/2))+1/(16*Sqrt[1+5*Tan[x]^2])" //
    );
  }

  public void test442() {
    check( //
        "Integrate[Tan[x]/(a^3+b^3*Tan[x]^2)^(1/3), x]", //
        "(Sqrt[3]*ArcTan[(1+(2*(a^3+b^3*Tan[x]^2)^(1/3))/(a^3-b^3)^(1/3))/Sqrt[3]])/(2*(a^3-b^3)^(1/3))+Log[Cos[x]]/(2*(a^3-b^3)^(1/3))+(3*Log[(a^3-b^3)^(1/3)-(a^3+b^3*Tan[x]^2)^(1/3)])/(4*(a^3-b^3)^(1/3))" //
    );
  }

  public void test443() {
    check( //
        "Integrate[(1-7*Tan[x]^2)^(2/3)*Tan[x], x]", //
        "2*Sqrt[3]*ArcTan[(1+(1-7*Tan[x]^2)^(1/3))/Sqrt[3]]+2*Log[Cos[x]]+3*Log[2-(1-7*Tan[x]^2)^(1/3)]+3/4*(1-7*Tan[x]^2)^(2/3)" //
    );
  }

  public void test444() {
    check( //
        "Integrate[Cot[x]/(a^4+b^4*Csc[x]^2)^(1/4), x]", //
        "-ArcTan[(a^4+b^4*Csc[x]^2)^(1/4)/a]/a+ArcTanh[(a^4+b^4*Csc[x]^2)^(1/4)/a]/a" //
    );
  }

  public void test445() {
    check( //
        "Integrate[Cot[x]/(a^4-b^4*Csc[x]^2)^(1/4), x]", //
        "-ArcTan[(a^4-b^4*Csc[x]^2)^(1/4)/a]/a+ArcTanh[(a^4-b^4*Csc[x]^2)^(1/4)/a]/a" //
    );
  }

  public void test446() {
    check( //
        "Integrate[(3*Tan[x]^2+Sin[x]^2*(1-3*Sec[x]^2)^(1/3))/(Cos[x]^2*(1-3*Sec[x]^2)^(5/6)*(1-Sqrt[1-3*Sec[x]^2]))*Tan[x], x]", //
        "Sqrt[3]*ArcTan[(1+2*(1-3*Sec[x]^2)^(1/6))/Sqrt[3]]+Log[Sec[x]^2]/4-3/2*Log[1-(1-3*Sec[x]^2)^(1/6)]+Log[1-Sqrt[1-3*Sec[x]^2]]/3-(1-3*Sec[x]^2)^(1/6)-(1-3*Sec[x]^2)^(2/3)/4+1/(2*(1-Sqrt[1-3*Sec[x]^2]))" //
    );
  }

  public void test447() {
    check( //
        "Integrate[(2*Tan[x]^2-Cos[2*x])/(Cos[x]^2*(Tan[x]*Tan[2*x])^(3/2)), x]", //
        "2*ArcTanh[Tan[x]/Sqrt[Tan[x]*Tan[2*x]]]-11/(4*Sqrt[2])*ArcTanh[(Sqrt[2]*Tan[x])/Sqrt[Tan[x]*Tan[2*x]]]+Tan[x]/(2*(Tan[x]*Tan[2*x])^(3/2))+(2*Tan[x]^3)/(3*(Tan[x]*Tan[2*x])^(3/2))+(3*Tan[x])/(4*Sqrt[Tan[x]*Tan[2*x]])" //
    );
  }

  public void test448() {
    check( //
        "Integrate[Tan[x]/(a^3-b^3*Cos[x]^n)^(4/3), x]", //
        "-Sqrt[3]/(a^4*n)*ArcTan[(a+2*(a^3-b^3*Cos[x]^n)^(1/3))/(Sqrt[3]*a)]-3/(a^3*n*(a^3-b^3*Cos[x]^n)^(1/3))+Log[Cos[x]]/(2*a^4)-(3*Log[a-(a^3-b^3*Cos[x]^n)^(1/3)])/(2*a^4*n)" //
    );
  }

  public void test449() {
    check( //
        "Integrate[(1+2*Cos[x]^9)^(5/6)*Tan[x], x]", //
        "ArcTan[(1-(1+2*Cos[x]^9)^(1/3))/(Sqrt[3]*(1+2*Cos[x]^9)^(1/6))]/(3*Sqrt[3])+ArcTanh[(1+2*Cos[x]^9)^(1/6)]/3-ArcTanh[Sqrt[1+2*Cos[x]^9]]/9-2/15*(1+2*Cos[x]^9)^(5/6)" //
    );
  }

  public void test450() {
    check( //
        "Integrate[Sin[x]^9*Cot[x]/(2-5*Sin[x]^3)^(4/3), x]", //
        "4/(125*(2-5*Sin[x]^3)^(1/3))+2/125*(2-5*Sin[x]^3)^(2/3)-(2-5*Sin[x]^3)^(5/3)/625" //
    );
  }

  public void test451() {
    check( //
        "Integrate[(1+(1-8*Tan[x]^2)^(1/3))/(Cos[x]^2*(1-8*Tan[x]^2)^(2/3))*Tan[x], x]", //
        "-3/32*(1+(1-8*Tan[x]^2)^(1/3))^2" //
    );
  }

  public void test452() {
    check( //
        "Integrate[(1+(1-8*Tan[x]^2)^(1/3))/(Cos[x]^2*(1-8*Tan[x]^2)^(2/3))*Cot[x], x]", //
        "-Log[Tan[x]]+3/2*Log[1-(1-8*Tan[x]^2)^(1/3)]" //
    );
  }

  public void test453() {
    check( //
        "Integrate[(5*Cos[x]^2-Sqrt[5*Sin[x]^2+(-1)*1])/((5*Sin[x]^2+(-1)*1)^(1/4)*(2+Sqrt[5*Sin[x]^2+(-1)*1]))*Tan[x], x]", //
        "-3/Sqrt[2]*ArcTan[(-1+5*Sin[x]^2)^(1/4)/Sqrt[2]]-ArcTanh[(-1+5*Sin[x]^2)^(1/4)/Sqrt[2]]/(2*Sqrt[2])+2*(-1+5*Sin[x]^2)^(1/4)-(-1+5*Sin[x]^2)^(1/4)/(2*(2+Sqrt[-1+5*Sin[x]^2]))" //
    );
  }

  public void test454() {
    check( //
        "Integrate[Cos[x]^4*Cos[2*x]^(2/3)*Tan[x], x]", //
        "-3/40*Cos[2*x]^(5/3)-3/64*Cos[2*x]^(8/3)" //
    );
  }

  public void test455() {
    check( //
        "Integrate[Sin[x]^6*Tan[x]/Cos[2*x]^(3/4), x]", //
        "ArcTan[(1-Sqrt[Cos[2*x]])/(Sqrt[2]*Cos[2*x]^(1/4))]/Sqrt[2]-ArcTanh[(1+Sqrt[Cos[2*x]])/(Sqrt[2]*Cos[2*x]^(1/4))]/Sqrt[2]+7/4*Cos[2*x]^(1/4)-Cos[2*x]^(5/4)/5+Cos[2*x]^(9/4)/36" //
    );
  }

  public void test456() {
    check( //
        "Integrate[Sqrt[Tan[x]*Tan[2*x]], x]", //
        "-ArcTanh[Tan[x]/Sqrt[Tan[x]*Tan[2*x]]]" //
    );
  }

  public void test457() {
    check( //
        "Integrate[Sqrt[Cot[2*x]/Cot[x]], x]", //
        "-ArcSin[Tan[x]]/Sqrt[2]+ArcTan[(Sqrt[2]*Tan[x])/Sqrt[1-Tan[x]^2]]" //
    );
  }

  public void test458() {
    check( //
        "Integrate[1/(x^5*(5+x^2)), x]", //
        "-1/(20*x^4)+1/(50*x^2)+Log[x]/125-Log[5+x^2]/250" //
    );
  }

  public void test459() {
    check( //
        "Integrate[1/(x^6*(5+x^2)), x]", //
        "-1/(25*x^5)+1/(75*x^3)-1/(125*x)-ArcTan[x/Sqrt[5]]/(125*Sqrt[5])" //
    );
  }

  public void test460() {
    check( //
        "Integrate[1/(x*(x^2+(-1)*4)^4), x]", //
        "1/(24*(4-x^2)^3)+1/(64*(4-x^2)^2)+1/(128*(4-x^2))+Log[x]/256-Log[4-x^2]/512" //
    );
  }

  public void test461() {
    check( //
        "Integrate[1/(x*(x^2+(-1)*2)^(5/2)), x]", //
        "-1/(6*(x^2+(-1)*2)^(3/2))+1/(4*Sqrt[x^2+(-1)*2])+ArcTan[Sqrt[x^2+(-1)*2]/Sqrt[2]]/(4*Sqrt[2])" //
    );
  }

  public void test462() {
    check( //
        "Integrate[(x^2+(-1)*10)^(5/2)/x, x]", //
        "100*Sqrt[x^2+(-1)*10]-10/3*(x^2+(-1)*10)^(3/2)+(x^2+(-1)*10)^(5/2)/5-100*Sqrt[10]*ArcTan[Sqrt[x^2+(-1)*10]/Sqrt[10]]" //
    );
  }

  public void test463() {
    check( //
        "Integrate[x^(2*n+1), x]", //
        "x^(2*(n+1))/(2*(n+1))" //
    );
  }

  public void test464() {
    check( //
        "Integrate[x^7/(x^2+(-1)*5)^3, x]", //
        "x^2/2-125/(4*(5-x^2)^2)+75/(2*(5-x^2))+15/2*Log[5-x^2]" //
    );
  }

  public void test465() {
    check( //
        "Integrate[(3*x^5-4*x^3)/(x^2+(-1)*1)^5, x]", //
        "1/(8*(1-x^2)^4)+1/(3*(1-x^2)^3)-3/(4*(1-x^2)^2)" //
    );
  }

  public void test466() {
    check( //
        "Integrate[(1+x^2)^(9/14)*x^3, x]", //
        "-7/23*(1+x^2)^(23/14)+7/37*(1+x^2)^(37/14)" //
    );
  }

  public void test467() {
    check( //
        "Integrate[x^5/(x^2+(-1)*4)^(13/6), x]", //
        "-48/(7*(x^2+(-1)*4)^(7/6))-24/(x^2+(-1)*4)^(1/6)+3/5*(x^2+(-1)*4)^(5/6)" //
    );
  }

  public void test468() {
    check( //
        "Integrate[1/(1+2*x^2)^(5/2), x]", //
        "x/(3*(1+2*x^2)^(3/2))+(2*x)/(3*Sqrt[1+2*x^2])" //
    );
  }

  public void test469() {
    check( //
        "Integrate[1/(x^2-2*x+(-1)*1)^(5/2), x]", //
        "(1-x)/(6*(x^2-2*x+(-1)*1)^(3/2))-(1-x)/(6*Sqrt[x^2-2*x+(-1)*1])" //
    );
  }

  public void test470() {
    check( //
        "Integrate[1/(x^4*(x^2+(-1)*8)^(3/2)), x]", //
        "1/(24*x^3*Sqrt[x^2+(-1)*8])+1/(48*x*Sqrt[x^2+(-1)*8])-x/(192*Sqrt[x^2+(-1)*8])" //
    );
  }

  public void test471() {
    check( //
        "Integrate[(x^2+5)^2/(x^4*x^(1/3)), x]", //
        "-15/(2*x^(10/3))-15/(2*x^(4/3))+1/2*3*x^(2/3)" //
    );
  }

  public void test472() {
    check( //
        "Integrate[1/(x^7*(1+x^2)^3), x]", //
        "-1/(6*x^6)+3/(4*x^4)-3/x^2-1/(4*(1+x^2)^2)-2/(1+x^2)-10*Log[x]+5*Log[1+x^2]" //
    );
  }

  public void test473() {
    check( //
        "Integrate[((2+x^2)/x^2)^(7/9)/(2+x^2)^(3/2), x]", //
        "-(9*(1+2/x^2)^(7/9)*x)/(10*Sqrt[2+x^2])" //
    );
  }

  public void test474() {
    check( //
        "Integrate[x^4/(Sqrt[10]-x^2)^(9/2), x]", //
        "x^5/(7*Sqrt[10]*(Sqrt[10]-x^2)^(7/2))+x^5/(175*(Sqrt[10]-x^2)^(5/2))" //
    );
  }

  public void test475() {
    check( //
        "Integrate[x^2/(3-x^2)^(3/2), x]", //
        "x/Sqrt[3-x^2]-ArcSin[x/Sqrt[3]]" //
    );
  }

  public void test476() {
    check( //
        "Integrate[(25-x^2)^(3/2)/x^4, x]", //
        "Sqrt[25-x^2]/x-(25-x^2)^(3/2)/(3*x^3)+ArcSin[x/5]" //
    );
  }

  public void test477() {
    check( //
        "Integrate[1/(1-2*x^2)^(7/2), x]", //
        "x/(5*(1-2*x^2)^(5/2))+(4*x)/(15*(1-2*x^2)^(3/2))+(8*x)/(15*Sqrt[1-2*x^2])" //
    );
  }

  public void test478() {
    check( //
        "Integrate[1/(-x^2+6*x+(-1)*7)^(5/2), x]", //
        "-(3-x)/(6*(-x^2+6*x+(-1)*7)^(3/2))-(3-x)/(6*Sqrt[-x^2+6*x+(-1)*7])" //
    );
  }

  public void test479() {
    check( //
        "Integrate[(-2*x^2-2*x+1)^3, x]", //
        "x-3*x^2+2*x^3+4*x^4-1/5*12*x^5-4*x^6-1/7*8*x^7" //
    );
  }

  public void test480() {
    check( //
        "Integrate[(x^2-x+(-1)*1)^2*(5*x+(-1)*1), x]", //
        "-x+1/2*3*x^2+1/3*11*x^3-1/4*3*x^4-1/5*11*x^5+1/6*5*x^6" //
    );
  }

  public void test481() {
    check( //
        "Integrate[(3*x+1)/(2*x^2-8*x+1)^(5/2), x]", //
        "(1-2*x)/(6*(2*x^2-8*x+1)^(3/2))-(2*(2-x))/(21*Sqrt[2*x^2-8*x+1])" //
    );
  }

  public void test482() {
    check( //
        "Integrate[(8*x^3-8*x+(-1)*1)/(1+2*x-4*x^2)^(5/2), x]", //
        "-(4*(1+x))/(15*(1+2*x-4*x^2)^(3/2))-(7+122*x)/(75*Sqrt[1+2*x-4*x^2])" //
    );
  }

  public void test483() {
    check( //
        "Integrate[x^2*Cos[x]^5, x]", //
        "16/15*x*Cos[x]+8/45*x*Cos[x]^3+2/25*x*Cos[x]^5-1/225*298*Sin[x]+8/15*x^2*Sin[x]+4/15*x^2*Cos[x]^2*Sin[x]+1/5*x^2*Cos[x]^4*Sin[x]+1/675*76*Sin[x]^3-1/125*2*Sin[x]^5" //
    );
  }

  public void test484() {
    check( //
        "Integrate[x^3*Sin[x]^3, x]", //
        "40/9*x*Cos[x]-2/3*x^3*Cos[x]-1/9*40*Sin[x]+2*x^2*Sin[x]+2/9*x*Cos[x]*Sin[x]^2-1/3*x^3*Cos[x]*Sin[x]^2-1/27*2*Sin[x]^3+1/3*x^2*Sin[x]^3" //
    );
  }

  public void test485() {
    check( //
        "Integrate[x^2*Sin[x]^6, x]", //
        "-1/1152*245*x+1/48*5*x^3+1/1152*245*Cos[x]*Sin[x]-5/16*x^2*Cos[x]*Sin[x]+5/16*x*Sin[x]^2+1/1728*65*Cos[x]*Sin[x]^3-5/24*x^2*Cos[x]*Sin[x]^3+5/48*x*Sin[x]^4+1/108*Cos[x]*Sin[x]^5-1/6*x^2*Cos[x]*Sin[x]^5+1/18*x*Sin[x]^6" //
    );
  }

  public void test486() {
    check( //
        "Integrate[x^2*Sin[x]^2*Cos[x], x]", //
        "4/9*x*Cos[x]-1/9*4*Sin[x]+2/9*x*Cos[x]*Sin[x]^2-1/27*2*Sin[x]^3+1/3*x^2*Sin[x]^3" //
    );
  }

  public void test487() {
    check( //
        "Integrate[x*Cos[x]^4/Sin[x]^2, x]", //
        "-1/4*3*x^2-Cos[x]^2/4-x*Cot[x]+Log[Sin[x]]-1/2*x*Cos[x]*Sin[x]" //
    );
  }

  public void test488() {
    check( //
        "Integrate[x*Sin[x]^3/Cos[x]^4, x]", //
        "5/6*ArcTanh[Sin[x]]-x*Sec[x]+1/3*x*Sec[x]^3-1/6*Sec[x]*Tan[x]" //
    );
  }

  public void test489() {
    check( //
        "Integrate[x*Sin[x]/Cos[x]^3, x]", //
        "1/2*x*Sec[x]^2-Tan[x]/2" //
    );
  }

  public void test490() {
    check( //
        "Integrate[x*Sin[x]^3/Cos[x], x]", //
        "x/4+1/2*I*x^2-x*Log[1+E^(2*I*x)]+1/2*I*PolyLog[2,-E^(2*I*x)]-1/4*Cos[x]*Sin[x]-1/2*x*Sin[x]^2" //
    );
  }

  public void test491() {
    check( //
        "Integrate[x*Sin[x]^3/Cos[x]^3, x]", //
        "x/2-1/2*I*x^2+x*Log[1+E^(2*I*x)]-1/2*I*PolyLog[2,-E^(2*I*x)]-Tan[x]/2+1/2*x*Tan[x]^2" //
    );
  }

  public void test492() {
    check( //
        "Integrate[(2*x+Sin[2*x])/(x*Sin[x]+Cos[x])^2, x]", //
        "2/(1+Cot[x]/x)" //
    );
  }

  public void test493() {
    check( //
        "Integrate[(x/(x*Cos[x]-Sin[x]))^2, x]", //
        "-Cot[x]+(x*Csc[x])/(x*Cos[x]-Sin[x])" //
    );
  }

  public void test494() {
    check( //
        "Integrate[a^(m*x)*b^(n*x), x]", //
        "(a^(m*x)*b^(n*x))/(m*Log[a]+n*Log[b])" //
    );
  }

  public void test495() {
    check( //
        "Integrate[(a^x-b^x)^2/(a^x*b^x), x]", //
        "-2*x+(a^x/b^x-b^x/a^x)/(Log[a]-Log[b])" //
    );
  }

  public void test496() {
    check( //
        "Integrate[(E^x-1/E^x)^1, x]", //
        "E^x+E^(-x)" //
    );
  }

  public void test497() {
    check( //
        "Integrate[(E^x-1/E^x)^2, x]", //
        "-1/(2*E^(2*x))+E^(2*x)/2-2*x" //
    );
  }

  public void test498() {
    check( //
        "Integrate[(E^x-1/E^x)^3, x]", //
        "1/(3*E^(3*x))-3/E^x-3*E^x+E^(3*x)/3" //
    );
  }

  public void test499() {
    check( //
        "Integrate[(E^x-1/E^x)^4, x]", //
        "-1/(4*E^(4*x))+2/E^(2*x)-2*E^(2*x)+E^(4*x)/4+6*x" //
    );
  }

  public void test500() {
    check( //
        "Integrate[(E^x-1/E^x)^n, x]", //
        "-((-1/E^x+E^x)^n*(1-E^(2*x))*Hypergeometric2F1[1,1/2*(2+n),1-n/2,E^(2*x)])/n" //
    );
  }

  public void test501() {
    check( //
        "Integrate[(a^(-4*x)-a^(2*x))^3, x]", //
        "3*x-1/(a^(12*x)*12*Log[a])+1/(a^(6*x)*2*Log[a])-a^(6*x)/(6*Log[a])" //
    );
  }

  public void test502() {
    check( //
        "Integrate[(a^(k*x)+a^(l*x))^1, x]", //
        "a^(k*x)/(k*Log[a])+a^(l*x)/(l*Log[a])" //
    );
  }

  public void test503() {
    check( //
        "Integrate[(a^(k*x)+a^(l*x))^2, x]", //
        "a^(2*k*x)/(2*k*Log[a])+a^(2*l*x)/(2*l*Log[a])+(2*a^((k+l)*x))/((k+l)*Log[a])" //
    );
  }

  public void test504() {
    check( //
        "Integrate[(a^(k*x)+a^(l*x))^3, x]", //
        "a^(3*k*x)/(3*k*Log[a])+a^(3*l*x)/(3*l*Log[a])+(3*a^((2*k+l)*x))/((2*k+l)*Log[a])+(3*a^((k+2*l)*x))/((k+2*l)*Log[a])" //
    );
  }

  public void test505() {
    check( //
        "Integrate[(a^(k*x)+a^(l*x))^4, x]", //
        "a^(4*k*x)/(4*k*Log[a])+a^(4*l*x)/(4*l*Log[a])+(3*a^(2*(k+l)*x))/((k+l)*Log[a])+(4*a^((3*k+l)*x))/((3*k+l)*Log[a])+(4*a^((k+3*l)*x))/((k+3*l)*Log[a])" //
    );
  }

  public void test506() {
    check( //
        "Integrate[(a^(k*x)+a^(l*x))^n, x]", //
        "((1+a^((k-l)*x))*(a^(k*x)+a^(l*x))^n)/(l*n*Log[a])*Hypergeometric2F1[1,1+(k*n)/(k-l),1+(l*n)/(k-l),-a^((k-l)*x)]" //
    );
  }

  public void test507() {
    check( //
        "Integrate[(a^(k*x)-a^(l*x))^1, x]", //
        "a^(k*x)/(k*Log[a])-a^(l*x)/(l*Log[a])" //
    );
  }

  public void test508() {
    check( //
        "Integrate[(a^(k*x)-a^(l*x))^2, x]", //
        "a^(2*k*x)/(2*k*Log[a])+a^(2*l*x)/(2*l*Log[a])-(2*a^((k+l)*x))/((k+l)*Log[a])" //
    );
  }

  public void test509() {
    check( //
        "Integrate[(a^(k*x)-a^(l*x))^3, x]", //
        "a^(3*k*x)/(3*k*Log[a])-a^(3*l*x)/(3*l*Log[a])-(3*a^((2*k+l)*x))/((2*k+l)*Log[a])+(3*a^((k+2*l)*x))/((k+2*l)*Log[a])" //
    );
  }

  public void test510() {
    check( //
        "Integrate[(a^(k*x)-a^(l*x))^4, x]", //
        "a^(4*k*x)/(4*k*Log[a])+a^(4*l*x)/(4*l*Log[a])+(3*a^(2*(k+l)*x))/((k+l)*Log[a])-(4*a^((3*k+l)*x))/((3*k+l)*Log[a])-(4*a^((k+3*l)*x))/((k+3*l)*Log[a])" //
    );
  }

  public void test511() {
    check( //
        "Integrate[(a^(k*x)-a^(l*x))^n, x]", //
        "((1-a^((k-l)*x))*(a^(k*x)-a^(l*x))^n)/(l*n*Log[a])*Hypergeometric2F1[1,1+(k*n)/(k-l),1+(l*n)/(k-l),a^((k-l)*x)]" //
    );
  }

  public void test512() {
    check( //
        "Integrate[(1+a^(m*x))^1, x]", //
        "x+a^(m*x)/(m*Log[a])" //
    );
  }

  public void test513() {
    check( //
        "Integrate[(1+a^(m*x))^2, x]", //
        "x+(2*a^(m*x))/(m*Log[a])+a^(2*m*x)/(2*m*Log[a])" //
    );
  }

  public void test514() {
    check( //
        "Integrate[(1+a^(m*x))^3, x]", //
        "x+(3*a^(m*x))/(m*Log[a])+(3*a^(2*m*x))/(2*m*Log[a])+a^(3*m*x)/(3*m*Log[a])" //
    );
  }

  public void test515() {
    check( //
        "Integrate[(1+a^(m*x))^4, x]", //
        "x+(4*a^(m*x))/(m*Log[a])+(3*a^(2*m*x))/(m*Log[a])+(4*a^(3*m*x))/(3*m*Log[a])+a^(4*m*x)/(4*m*Log[a])" //
    );
  }

  public void test516() {
    check( //
        "Integrate[(1+a^(m*x))^n, x]", //
        "-((1+a^(m*x))^(1+n)*Hypergeometric2F1[1,1+n,2+n,1+a^(m*x)])/(m*(1+n)*Log[a])" //
    );
  }

  public void test517() {
    check( //
        "Integrate[(1-a^(m*x))^1, x]", //
        "x-a^(m*x)/(m*Log[a])" //
    );
  }

  public void test518() {
    check( //
        "Integrate[(1-a^(m*x))^2, x]", //
        "x-(2*a^(m*x))/(m*Log[a])+a^(2*m*x)/(2*m*Log[a])" //
    );
  }

  public void test519() {
    check( //
        "Integrate[(1-a^(m*x))^3, x]", //
        "x-(3*a^(m*x))/(m*Log[a])+(3*a^(2*m*x))/(2*m*Log[a])-a^(3*m*x)/(3*m*Log[a])" //
    );
  }

  public void test520() {
    check( //
        "Integrate[(1-a^(m*x))^4, x]", //
        "x-(4*a^(m*x))/(m*Log[a])+(3*a^(2*m*x))/(m*Log[a])-(4*a^(3*m*x))/(3*m*Log[a])+a^(4*m*x)/(4*m*Log[a])" //
    );
  }

  public void test521() {
    check( //
        "Integrate[(1-a^(m*x))^n, x]", //
        "-((1-a^(m*x))^(1+n)*Hypergeometric2F1[1,1+n,2+n,1-a^(m*x)])/(m*(1+n)*Log[a])" //
    );
  }

  public void test522() {
    check( //
        "Integrate[1/(a*E^(n*x)+b), x]", //
        "x/b-Log[b+a*E^(n*x)]/(b*n)" //
    );
  }

  public void test523() {
    check( //
        "Integrate[E^x/(a*E^(3*x)+b), x]", //
        "-ArcTan[(b^(1/3)-2*a^(1/3)*E^x)/(Sqrt[3]*b^(1/3))]/(Sqrt[3]*a^(1/3)*b^(2/3))+Log[b^(1/3)+a^(1/3)*E^x]/(2*a^(1/3)*b^(2/3))-Log[b+a*E^(3*x)]/(6*a^(1/3)*b^(2/3))" //
    );
  }

  public void test524() {
    check( //
        "Integrate[(E^x+(-1)*1)/(E^x+1), x]", //
        "-x+2*Log[1+E^x]" //
    );
  }

  public void test525() {
    check( //
        "Integrate[E^(4*x)/(3*E^(4*x)-2*E^(2*x)+1), x]", //
        "-ArcTan[(1-3*E^(2*x))/Sqrt[2]]/(6*Sqrt[2])+Log[1-2*E^(2*x)+3*E^(4*x)]/12" //
    );
  }

  public void test526() {
    check( //
        "Integrate[(E^(5*x)+E^x)/(E^(3*x)-E^(2*x)+E^x+(-1)*1), x]", //
        "E^x+E^(2*x)/2-ArcTan[E^x]+Log[1-E^x]-Log[1+E^(2*x)]/2" //
    );
  }

  public void test527() {
    check( //
        "Integrate[(a+b*E^(n*x))^(r/s)*E^(n*x), x]", //
        "s*(a+b*E^(n*x))^((s+r)/s)/(b*n*(s+r))" //
    );
  }

  public void test528() {
    check( //
        "Integrate[(1-2*E^(x/3))^(1/4), x]", //
        "12*(1-2*E^(x/3))^(1/4)-6*ArcTan[(1-2*E^(x/3))^(1/4)]-6*ArcTanh[(1-2*E^(x/3))^(1/4)]" //
    );
  }

  public void test529() {
    check( //
        "Integrate[(a+b*E^(n*x))^(r/s), x]", //
        "-((a+b*E^(n*x))^((r+s)/s)*s*Hypergeometric2F1[1,(r+s)/s,2+r/s,1+(b*E^(n*x))/a])/(a*n*(r+s))" //
    );
  }

  public void test530() {
    check( //
        "Integrate[E^x/Sqrt[E^(2*x)+a^2], x]", //
        "ArcTanh[E^x/Sqrt[a^2+E^(2*x)]]" //
    );
  }

  public void test531() {
    check( //
        "Integrate[E^x/Sqrt[E^(2*x)-a^2], x]", //
        "ArcTanh[E^x/Sqrt[-a^2+E^(2*x)]]" //
    );
  }

  public void test532() {
    check( //
        "Integrate[E^(3/4*x)/((E^(3/4*x)+(-1)*2)*Sqrt[E^(3/2*x)+E^(3/4*x)+(-1)*2]), x]", //
        "2/3*ArcTanh[(2-5*E^(1/4*3*x))/(4*Sqrt[-2+E^(1/4*3*x)+E^(1/2*3*x)])]" //
    );
  }

  public void test533() {
    check( //
        "Integrate[(E^(7*x)+(-1)*3)^(2/3)/E^(2*x), x]", //
        "(1/6*(-3+E^(7*x))^(5/3)*Hypergeometric2F1[1,29/21,5/7,E^(7*x)/3])/E^(2*x)" //
    );
  }

  public void test534() {
    check( //
        "Integrate[E^(2*x)/(3-E^(x/2))^(3/4), x]", //
        "-216*(3-E^(x/2))^(1/4)+216/5*(3-E^(x/2))^(5/4)-8*(3-E^(x/2))^(9/4)+8/13*(3-E^(x/2))^(13/4)" //
    );
  }

  public void test535() {
    check( //
        "Integrate[x^3/E^(x/2), x]", //
        "-96/E^(x/2)-(48*x)/E^(x/2)-(12*x^2)/E^(x/2)-(2*x^3)/E^(x/2)" //
    );
  }

  public void test536() {
    check( //
        "Integrate[1/(x^3*E^(x/2)), x]", //
        "-1/(E^(x/2)*2*x^2)+1/(E^(x/2)*4*x)+ExpIntegralEi[-x/2]/8" //
    );
  }

  public void test537() {
    check( //
        "Integrate[x^2*a^(3*x), x]", //
        "(2*a^(3*x))/(27*Log[a]^3)-(2*a^(3*x)*x)/(9*Log[a]^2)+(a^(3*x)*x^2)/(3*Log[a])" //
    );
  }

  public void test538() {
    check( //
        "Integrate[x*(x^2+1)*E^x^2, x]", //
        "1/2*E^x^2*x^2" //
    );
  }

  public void test539() {
    check( //
        "Integrate[x/(E^x+E^(-x))^2, x]", //
        "x/2-x/(2*(1+E^(2*x)))-Log[1+E^(2*x)]/4" //
    );
  }

  public void test540() {
    check( //
        "Integrate[(1-x-x^2)/Sqrt[1-x^2]*E^x, x]", //
        "E^x*Sqrt[1-x^2]" //
    );
  }

  public void test541() {
    check( //
        "Integrate[Cos[2*x]/E^(3*x), x]", //
        "(-3/13*Cos[2*x])/E^(3*x)+(2/13*Sin[2*x])/E^(3*x)" //
    );
  }

  public void test542() {
    check( //
        "Integrate[(Sin[x/2]+Cos[x/2])/(E^x)^(1/3), x]", //
        "-(30*Cos[x/2])/(13*(E^x)^(1/3))+(6*Sin[x/2])/(13*(E^x)^(1/3))" //
    );
  }

  public void test543() {
    check( //
        "Integrate[Cos[3*x/2]/(3^(3*x))^(1/4), x]", //
        "-(4*Cos[1/2*3*x]*Log[3])/(3*(3^(3*x))^(1/4)*(4+Log[3]^2))+(8*Sin[1/2*3*x])/(3*(3^(3*x))^(1/4)*(4+Log[3]^2))" //
    );
  }

  public void test544() {
    check( //
        "Integrate[E^(m*x)*Cos[x]^2, x]", //
        "(2*E^(m*x))/(m*(4+m^2))+(E^(m*x)*m*Cos[x]^2)/(4+m^2)+(2*E^(m*x)*Cos[x]*Sin[x])/(4+m^2)" //
    );
  }

  public void test545() {
    check( //
        "Integrate[E^(m*x)*Sin[x]^3, x]", //
        "-(6*E^(m*x)*Cos[x])/(9+10*m^2+m^4)+(6*E^(m*x)*m*Sin[x])/(9+10*m^2+m^4)-(3*E^(m*x)*Cos[x]*Sin[x]^2)/(9+m^2)+(E^(m*x)*m*Sin[x]^3)/(9+m^2)" //
    );
  }

  public void test546() {
    check( //
        "Integrate[Cos[x/3]^3/Sqrt[E^x], x]", //
        "-(48*Cos[x/3])/(65*Sqrt[E^x])-(2*Cos[x/3]^3)/(5*Sqrt[E^x])+(32*Sin[x/3])/(65*Sqrt[E^x])+(4*Cos[x/3]^2*Sin[x/3])/(5*Sqrt[E^x])" //
    );
  }

  public void test547() {
    check( //
        "Integrate[E^(2*x)*Sin[x]^2*Cos[x]^2, x]", //
        "E^(2*x)/16-1/80*E^(2*x)*Cos[4*x]-1/40*E^(2*x)*Sin[4*x]" //
    );
  }

  public void test548() {
    check( //
        "Integrate[E^(3*x)*Sin[3*x/2]^2*Cos[3*x/2]^2, x]", //
        "E^(3*x)/24-1/120*E^(3*x)*Cos[6*x]-1/60*E^(3*x)*Sin[6*x]" //
    );
  }

  public void test549() {
    check( //
        "Integrate[E^(m*x)*Tan[x]^2, x]", //
        "-E^(m*x)/m+4*E^((2*I+m)*x)*Hypergeometric2F1[2,1-1/2*I*m,2-1/2*I*m,-E^(2*I*x)]/(2*I+m)" //
    );
  }

  public void test550() {
    check( //
        "Integrate[E^(m*x)/Sin[x]^2, x]", //
        "-(4*E^((2*I+m)*x)*Hypergeometric2F1[2,1-1/2*I*m,2-1/2*I*m,E^(2*I*x)])/(2*I+m)" //
    );
  }

  public void test551() {
    check( //
        "Integrate[E^(m*x)/Cos[x]^3, x]", //
        "(8*E^((3*I+m)*x))/(3*I+m)*Hypergeometric2F1[3,1/2*(3-I*m),1/2*(5-I*m),-E^(2*I*x)]" //
    );
  }

  public void test552() {
    check( //
        "Integrate[E^x/(1+Cos[x]), x]", //
        "(1+(-1)*I)*E^((1+I)*x)*Hypergeometric2F1[2,1+(-1)*I,2+(-1)*I,-E^(I*x)]" //
    );
  }

  public void test553() {
    check( //
        "Integrate[E^x/(1-Cos[x]), x]", //
        "-(1+(-1)*I)*E^((1+I)*x)*Hypergeometric2F1[2,1+(-1)*I,2+(-1)*I,E^(I*x)]" //
    );
  }

  public void test554() {
    check( //
        "Integrate[E^x/(1+Sin[x]), x]", //
        "(-1+I)*E^((1+(-1)*I)*x)*Hypergeometric2F1[1+I,2,2+I,-I/E^(I*x)]" //
    );
  }

  public void test555() {
    check( //
        "Integrate[E^x/(1-Sin[x]), x]", //
        "(1+I)*E^((1+I)*x)*Hypergeometric2F1[1+(-1)*I,2,2+(-1)*I,(-1)*I*E^(I*x)]" //
    );
  }

  public void test556() {
    check( //
        "Integrate[E^x*(1-Sin[x])/(1-Cos[x]), x]", //
        "-E^x*Sin[x]/(1-Cos[x])" //
    );
  }

  public void test557() {
    check( //
        "Integrate[E^x*(1+Sin[x])/(1-Cos[x]), x]", //
        "(E^x*Sin[x])/(1-Cos[x])-2*(1+(-1)*I)*E^((1+I)*x)*Hypergeometric2F1[2,1+(-1)*I,2+(-1)*I,E^(I*x)]" //
    );
  }

  public void test558() {
    check( //
        "Integrate[E^x*(1+Sin[x])/(1+Cos[x]), x]", //
        "E^x*Sin[x]/(1+Cos[x])" //
    );
  }

  public void test559() {
    check( //
        "Integrate[E^x*(1-Sin[x])/(1+Cos[x]), x]", //
        "-(E^x*Sin[x])/(1+Cos[x])+2*(1+(-1)*I)*E^((1+I)*x)*Hypergeometric2F1[2,1+(-1)*I,2+(-1)*I,-E^(I*x)]" //
    );
  }

  public void test560() {
    check( //
        "Integrate[E^x*(1-Cos[x])/(1-Sin[x]), x]", //
        "-(E^x*Cos[x])/(1-Sin[x])+2*(1+I)*E^((1+I)*x)*Hypergeometric2F1[2,1+(-1)*I,2+(-1)*I,(-1)*I*E^(I*x)]" //
    );
  }

  public void test561() {
    check( //
        "Integrate[E^x*(1+Cos[x])/(1-Sin[x]), x]", //
        "E^x*Cos[x]/(1-Sin[x])" //
    );
  }

  public void test562() {
    check( //
        "Integrate[E^x*(1+Cos[x])/(1+Sin[x]), x]", //
        "(E^x*Cos[x])/(1+Sin[x])-2*(1+I)*E^((1+I)*x)*Hypergeometric2F1[2,1+(-1)*I,2+(-1)*I,I*E^(I*x)]" //
    );
  }

  public void test563() {
    check( //
        "Integrate[E^x*(1-Cos[x])/(1+Sin[x]), x]", //
        "-E^x*Cos[x]/(1+Sin[x])" //
    );
  }

  public void test564() {
    check( //
        "Integrate[x*E^x*Cos[x], x]", //
        "1/2*E^x*x*Cos[x]-1/2*E^x*Sin[x]+1/2*E^x*x*Sin[x]" //
    );
  }

  public void test565() {
    check( //
        "Integrate[x^2*E^x*Sin[x], x]", //
        "-1/2*E^x*Cos[x]+E^x*x*Cos[x]-1/2*E^x*x^2*Cos[x]-1/2*E^x*Sin[x]+1/2*E^x*x^2*Sin[x]" //
    );
  }

  public void test566() {
    check( //
        "Integrate[x^2*Sin[x]/E^(3*x), x]", //
        "(-13/250*Cos[x])/E^(3*x)-(3/25*x*Cos[x])/E^(3*x)-(1/10*x^2*Cos[x])/E^(3*x)-(9/250*Sin[x])/E^(3*x)-(4/25*x*Sin[x])/E^(3*x)-(3/10*x^2*Sin[x])/E^(3*x)" //
    );
  }

  public void test567() {
    check( //
        "Integrate[E^(x/2)*x^2*Cos[x]^3, x]", //
        "-132/125*E^(x/2)*Cos[x]+18/25*E^(x/2)*x*Cos[x]+48/185*E^(x/2)*x^2*Cos[x]+2/37*E^(x/2)*x^2*Cos[x]^3-1/50653*428*E^(x/2)*Cos[3*x]+1/1369*70*E^(x/2)*x*Cos[3*x]-24/125*E^(x/2)*Sin[x]-24/25*E^(x/2)*x*Sin[x]+96/185*E^(x/2)*x^2*Sin[x]+12/37*E^(x/2)*x^2*Cos[x]^2*Sin[x]-1/50653*792*E^(x/2)*Sin[3*x]-1/1369*24*E^(x/2)*x*Sin[3*x]" //
    );
  }

  public void test568() {
    check( //
        "Integrate[E^(2*x)*x^2*Sin[4*x], x]", //
        "1/250*E^(2*x)*Cos[4*x]+2/25*E^(2*x)*x*Cos[4*x]-1/5*E^(2*x)*x^2*Cos[4*x]-11/500*E^(2*x)*Sin[4*x]+3/50*E^(2*x)*x*Sin[4*x]+1/10*E^(2*x)*x^2*Sin[4*x]" //
    );
  }

  public void test569() {
    check( //
        "Integrate[E^(x/2)*x^2*Sin[x]^2*Cos[x], x]", //
        "-44/125*E^(x/2)*Cos[x]+6/25*E^(x/2)*x*Cos[x]+1/10*E^(x/2)*x^2*Cos[x]+1/50653*428*E^(x/2)*Cos[3*x]-1/1369*70*E^(x/2)*x*Cos[3*x]-1/74*E^(x/2)*x^2*Cos[3*x]-8/125*E^(x/2)*Sin[x]-8/25*E^(x/2)*x*Sin[x]+1/5*E^(x/2)*x^2*Sin[x]+1/50653*792*E^(x/2)*Sin[3*x]+1/1369*24*E^(x/2)*x*Sin[3*x]-3/37*E^(x/2)*x^2*Sin[3*x]" //
    );
  }

  public void test570() {
    check( //
        "Integrate[Cosh[x], x]", //
        "Sinh[x]" //
    );
  }

  public void test571() {
    check( //
        "Integrate[Sinh[x], x]", //
        "Cosh[x]" //
    );
  }

  public void test572() {
    check( //
        "Integrate[Tanh[x], x]", //
        "Log[Cosh[x]]" //
    );
  }

  public void test573() {
    check( //
        "Integrate[Coth[x], x]", //
        "Log[Sinh[x]]" //
    );
  }

  public void test574() {
    check( //
        "Integrate[Sech[x], x]", //
        "ArcTan[Sinh[x]]" //
    );
  }

  public void test575() {
    check( //
        "Integrate[Csch[x], x]", //
        "-ArcTanh[Cosh[x]]" //
    );
  }

  public void test576() {
    check( //
        "Integrate[Cosh[x]^2, x]", //
        "x/2+1/2*Cosh[x]*Sinh[x]" //
    );
  }

  public void test577() {
    check( //
        "Integrate[Sinh[x]^5, x]", //
        "Cosh[x]-1/3*2*Cosh[x]^3+Cosh[x]^5/5" //
    );
  }

  public void test578() {
    check( //
        "Integrate[Tanh[x]^4, x]", //
        "x-Tanh[x]-Tanh[x]^3/3" //
    );
  }

  public void test579() {
    check( //
        "Integrate[Csch[x]^3, x]", //
        "ArcTanh[Cosh[x]]/2-1/2*Coth[x]*Csch[x]" //
    );
  }

  public void test580() {
    check( //
        "Integrate[1/Cosh[x]^5, x]", //
        "3/8*ArcTan[Sinh[x]]+3/8*Sech[x]*Tanh[x]+1/4*Sech[x]^3*Tanh[x]" //
    );
  }

  public void test581() {
    check( //
        "Integrate[Tanh[x]^5/Sech[x]^4, x]", //
        "-Cosh[x]^2+Cosh[x]^4/4+Log[Cosh[x]]" //
    );
  }

  public void test582() {
    check( //
        "Integrate[Tanh[x]^5*Sech[x]^(3/4), x]", //
        "-4/3*Sech[x]^(3/4)+8/11*Sech[x]^(11/4)-4/19*Sech[x]^(19/4)" //
    );
  }

  public void test583() {
    check( //
        "Integrate[1/(a+b*Cosh[x]), x]", //
        "(2*ArcTanh[((a-b)*Tanh[x/2])/Sqrt[a^2-b^2]])/Sqrt[a^2-b^2]" //
    );
  }

  public void test584() {
    check( //
        "Integrate[1/(1+Cosh[x])^2, x]", //
        "Sinh[x]/(3*(1+Cosh[x])^2)+Sinh[x]/(3*(1+Cosh[x]))" //
    );
  }

  public void test585() {
    check( //
        "Integrate[1/(a+b*Tanh[x]), x]", //
        "(a*x)/(a^2-b^2)-(b*Log[a*Cosh[x]+b*Sinh[x]])/(a^2-b^2)" //
    );
  }

  public void test586() {
    check( //
        "Integrate[1/(a^2+b^2*Cosh[x]^2), x]", //
        "ArcTanh[(a*Tanh[x])/Sqrt[a^2+b^2]]/(a*Sqrt[a^2+b^2])" //
    );
  }

  public void test587() {
    check( //
        "Integrate[1/(a^2-b^2*Cosh[x]^2), x]", //
        "ArcTanh[(a*Tanh[x])/Sqrt[a^2-b^2]]/(a*Sqrt[a^2-b^2])" //
    );
  }

  public void test588() {
    check( //
        "Integrate[1/(1-Sinh[x]^4), x]", //
        "ArcTanh[Sqrt[2]*Tanh[x]]/(2*Sqrt[2])+Tanh[x]/2" //
    );
  }

  public void test589() {
    check( //
        "Integrate[(Cosh[x]^3-Sinh[x]^3)/(Cosh[x]^3+Sinh[x]^3), x]", //
        "-(4*ArcTan[(1-2*Tanh[x])/Sqrt[3]])/(3*Sqrt[3])-1/(3*(1+Tanh[x]))" //
    );
  }

  public void test590() {
    check( //
        "Integrate[Cosh[x]*Cosh[2*x]*Cosh[3*x], x]", //
        "x/4+Sinh[2*x]/8+Sinh[4*x]/16+Sinh[6*x]/24" //
    );
  }

  public void test591() {
    check( //
        "Integrate[Sinh[x]*Cosh[3*x/2]*Sinh[5*x/2], x]", //
        "-x/4+Sinh[2*x]/8-Sinh[3*x]/12+Sinh[5*x]/20" //
    );
  }

  public void test592() {
    check( //
        "Integrate[(Tanh[x]-Cosh[2*x])*Cosh[x]/((Sinh[2*x]+Sinh[x]^2)*Sqrt[Sinh[2*x]]), x]", //
        "Sqrt[2]*ArcTan[Sech[x]*Sqrt[Cosh[x]*Sinh[x]]]+ArcTan[Sinh[x]/Sqrt[Sinh[2*x]]]/6-1/3*Sqrt[2]*ArcTanh[Sech[x]*Sqrt[Cosh[x]*Sinh[x]]]+Cosh[x]/Sqrt[Sinh[2*x]]" //
    );
  }

  public void test593() {
    check( //
        "Integrate[Sinh[x]/(4*Cosh[x]^2+(-1)*9)^(5/2), x]", //
        "-Cosh[x]/(27*(-9+4*Cosh[x]^2)^(3/2))+(2*Cosh[x])/(243*Sqrt[-9+4*Cosh[x]^2])" //
    );
  }

  public void test594() {
    check( //
        "Integrate[Sinh[x]^2*Sinh[2*x]/(1-Sinh[x]^2)^(3/2), x]", //
        "2/Sqrt[1-Sinh[x]^2]+2*Sqrt[1-Sinh[x]^2]" //
    );
  }

  public void test595() {
    check( //
        "Integrate[Cosh[x]/Sqrt[Cosh[2*x]], x]", //
        "ArcSinh[Sqrt[2]*Sinh[x]]/Sqrt[2]" //
    );
  }

  public void test596() {
    check( //
        "Integrate[x*Tanh[x]^2, x]", //
        "x^2/2+Log[Cosh[x]]-x*Tanh[x]" //
    );
  }

  public void test597() {
    check( //
        "Integrate[x*Coth[x]^2, x]", //
        "x^2/2-x*Coth[x]+Log[Sinh[x]]" //
    );
  }

  public void test598() {
    check( //
        "Integrate[(x+Sinh[x]+Cosh[x])/(Cosh[x]-Sinh[x]), x]", //
        "-E^x+E^(2*x)/2+E^x*x" //
    );
  }

  public void test599() {
    check( //
        "Integrate[(x+Sinh[x]+Cosh[x])/(1+Cosh[x]), x]", //
        "x-(1-x)*Tanh[x/2]" //
    );
  }

  public void test600() {
    check( //
        "Integrate[E^(2*x)/Sinh[x]^4, x]", //
        "(8*E^(6*x))/(3*(1-E^(2*x))^3)" //
    );
  }

  public void test601() {
    check( //
        "Integrate[1/(E^(2*x)*Cosh[x]^4), x]", //
        "-8/(3*(1+E^(2*x))^3)" //
    );
  }

  public void test602() {
    check( //
        "Integrate[E^x/(Cosh[x]-Sinh[x]), x]", //
        "E^(2*x)/2" //
    );
  }

  public void test603() {
    check( //
        "Integrate[E^(m*x)/(Cosh[x]+Sinh[x]), x]", //
        "E^((m+(-1)*1)*x)/(m+(-1)*1)" //
    );
  }

  public void test604() {
    check( //
        "Integrate[E^x/(Cosh[x]+Sinh[x]), x]", //
        "x" //
    );
  }

  public void test605() {
    check( //
        "Integrate[E^x/(1-Cosh[x]), x]", //
        "-2/(1-E^x)-2*Log[1-E^x]" //
    );
  }

  public void test606() {
    check( //
        "Integrate[E^x*(1+Sinh[x])/(1+Cosh[x]), x]", //
        "E^x+2/(1+E^x)" //
    );
  }

  public void test607() {
    check( //
        "Integrate[E^x*(1-Sinh[x])/(1-Cosh[x]), x]", //
        "E^x-2/(1-E^x)" //
    );
  }

  public void test608() {
    check( //
        "Integrate[x^m*Log[x], x]", //
        "-x^(1+m)/(1+m)^2+(x^(1+m)*Log[x])/(1+m)" //
    );
  }

  public void test609() {
    check( //
        "Integrate[x^m*Log[x]^2, x]", //
        "(2*x^(1+m))/(1+m)^3-(2*x^(1+m)*Log[x])/(1+m)^2+(x^(1+m)*Log[x]^2)/(1+m)" //
    );
  }

  public void test610() {
    check( //
        "Integrate[Log[x]^2/x^(5/2), x]", //
        "-16/(27*x^(3/2))-(8*Log[x])/(9*x^(3/2))-(2*Log[x]^2)/(3*x^(3/2))" //
    );
  }

  public void test611() {
    check( //
        "Integrate[(a+b*x)*Log[x], x]", //
        "-a*x-1/4*b*x^2+a*x*Log[x]+1/2*b*x^2*Log[x]" //
    );
  }

  public void test612() {
    check( //
        "Integrate[(a+b*x)^3*Log[x], x]", //
        "-a^3*x-3/4*a^2*b*x^2-1/3*a*b^2*x^3-1/16*b^3*x^4-(a^4*Log[x])/(4*b)+((a+b*x)^4*Log[x])/(4*b)" //
    );
  }

  public void test613() {
    check( //
        "Integrate[3*Log[x]^3-8*Log[x]^2+(-1)*1, x]", //
        "-35*x+34*x*Log[x]-17*x*Log[x]^2+3*x*Log[x]^3" //
    );
  }

  public void test614() {
    check( //
        "Integrate[(x^4+1)*(Log[x]^3-2*Log[x]+1), x]", //
        "-3*x+1/625*169*x^5+4*x*Log[x]-44/125*x^5*Log[x]-3*x*Log[x]^2-3/25*x^5*Log[x]^2+x*Log[x]^3+1/5*x^5*Log[x]^3" //
    );
  }

  public void test615() {
    check( //
        "Integrate[1/(x^3*Log[x]^4), x]", //
        "-4/3*ExpIntegralEi[-2*Log[x]]-1/(3*x^2*Log[x]^3)+1/(3*x^2*Log[x]^2)-2/(3*x^2*Log[x])" //
    );
  }

  public void test616() {
    check( //
        "Integrate[Log[x]/(a+b*x), x]", //
        "(Log[x]*Log[1+(b*x)/a])/b+PolyLog[2,-(b*x)/a]/b" //
    );
  }

  public void test617() {
    check( //
        "Integrate[Log[x]/(a+b*x)^2, x]", //
        "(x*Log[x])/(a*(a+b*x))-Log[a+b*x]/(a*b)" //
    );
  }

  public void test618() {
    check( //
        "Integrate[Log[x]^n/x, x]", //
        "Log[x]^(1+n)/(1+n)" //
    );
  }

  public void test619() {
    check( //
        "Integrate[(a+b*Log[x])^n/x, x]", //
        "(a+b*Log[x])^(1+n)/(b*(1+n))" //
    );
  }

  public void test620() {
    check( //
        "Integrate[1/(x*(a+b*Log[x])), x]", //
        "Log[a+b*Log[x]]/b" //
    );
  }

  public void test621() {
    check( //
        "Integrate[1/(x*(a+b*Log[x])^n), x]", //
        "(a+b*Log[x])^(1-n)/(b*(1-n))" //
    );
  }

  public void test622() {
    check( //
        "Integrate[1/(x*Sqrt[Log[x]^2+a^2]), x]", //
        "ArcTanh[Log[x]/Sqrt[Log[x]^2+a^2]]" //
    );
  }

  public void test623() {
    check( //
        "Integrate[1/(x*Sqrt[Log[x]^2-a^2]), x]", //
        "ArcTanh[Log[x]/Sqrt[Log[x]^2-a^2]]" //
    );
  }

  public void test624() {
    check( //
        "Integrate[1/(x*Sqrt[a^2-Log[x]^2]), x]", //
        "ArcTan[Log[x]/Sqrt[a^2-Log[x]^2]]" //
    );
  }

  public void test625() {
    check( //
        "Integrate[1/(x*Log[x]*Sqrt[a^2+Log[x]^2]), x]", //
        "-ArcTanh[Sqrt[a^2+Log[x]^2]/a]/a" //
    );
  }

  public void test626() {
    check( //
        "Integrate[1/(x*Log[x]*Sqrt[a^2-Log[x]^2]), x]", //
        "-ArcTanh[Sqrt[a^2-Log[x]^2]/a]/a" //
    );
  }

  public void test627() {
    check( //
        "Integrate[1/(x*Log[x]*Sqrt[Log[x]^2-a^2]), x]", //
        "ArcTan[Sqrt[-a^2+Log[x]^2]/a]/a" //
    );
  }

  public void test628() {
    check( //
        "Integrate[Log[Log[x]]^1/x, x]", //
        "-Log[x]+Log[x]*Log[Log[x]]" //
    );
  }

  public void test629() {
    check( //
        "Integrate[Log[Log[x]]^2/x, x]", //
        "2*Log[x]-2*Log[x]*Log[Log[x]]+Log[x]*Log[Log[x]]^2" //
    );
  }

  public void test630() {
    check( //
        "Integrate[Log[Log[x]]^3/x, x]", //
        "-6*Log[x]+6*Log[x]*Log[Log[x]]-3*Log[x]*Log[Log[x]]^2+Log[x]*Log[Log[x]]^3" //
    );
  }

  public void test631() {
    check( //
        "Integrate[Log[Log[x]]^4/x, x]", //
        "24*Log[x]-24*Log[x]*Log[Log[x]]+12*Log[x]*Log[Log[x]]^2-4*Log[x]*Log[Log[x]]^3+Log[x]*Log[Log[x]]^4" //
    );
  }

  public void test632() {
    check( //
        "Integrate[Log[Log[x]]^n/x, x]", //
        "(Gamma[1+n,-Log[Log[x]]]*Log[Log[x]]^n)/(-Log[Log[x]])^n" //
    );
  }

  public void test633() {
    check( //
        "Integrate[Cot[x]/Log[Sin[x]], x]", //
        "Log[Log[Sin[x]]]" //
    );
  }

  public void test634() {
    check( //
        "Integrate[(E^Log[Cos[x]]+E^(-Log[Cos[x]]))*Tan[x], x]", //
        "-Cos[x]+Sec[x]" //
    );
  }

  public void test635() {
    check( //
        "Integrate[Sinh[x]*Log[Cosh[x]], x]", //
        "-Cosh[x]+Cosh[x]*Log[Cosh[x]]" //
    );
  }

  public void test636() {
    check( //
        "Integrate[Tanh[x]*Log[Cosh[x]], x]", //
        "Log[Cosh[x]]^2/2" //
    );
  }

  public void test637() {
    check( //
        "Integrate[Log[x-Sqrt[1+x^2]], x]", //
        "Sqrt[1+x^2]+x*Log[x-Sqrt[1+x^2]]" //
    );
  }

  public void test638() {
    check( //
        "Integrate[Log[x+(-1)*1]/x^3, x]", //
        "1/(2*x)+Log[1-x]/2-Log[-1+x]/(2*x^2)-Log[x]/2" //
    );
  }

  public void test639() {
    check( //
        "Integrate[(E^x-1/E^x)*Log[E^(2*x)+1], x]", //
        "-2*E^x+Log[1+E^(2*x)]/E^x+E^x*Log[1+E^(2*x)]" //
    );
  }

  public void test640() {
    check( //
        "Integrate[E^(3*x/2)*Log[E^x+(-1)*1], x]", //
        "-1/3*4*E^(x/2)-4/9*E^(1/2*3*x)+4/3*ArcTanh[E^(x/2)]+2/3*E^(1/2*3*x)*Log[-1+E^x]" //
    );
  }

  public void test641() {
    check( //
        "Integrate[Cos[x]^3*Log[Sin[x]], x]", //
        "-Sin[x]+Log[Sin[x]]*Sin[x]+Sin[x]^3/9-1/3*Log[Sin[x]]*Sin[x]^3" //
    );
  }

  public void test642() {
    check( //
        "Integrate[Log[Tan[x]]/Cos[x]^4, x]", //
        "-Tan[x]+Log[Tan[x]]*Tan[x]-Tan[x]^3/9+1/3*Log[Tan[x]]*Tan[x]^3" //
    );
  }

  public void test643() {
    check( //
        "Integrate[Log[Cos[x/2]]/(1+Cos[x]), x]", //
        "-x/2+(Log[Cos[x/2]]*Sin[x])/(1+Cos[x])+Tan[x/2]" //
    );
  }

  public void test644() {
    check( //
        "Integrate[Cos[x]*Log[Sin[x]]/(1+Cos[x])^2, x]", //
        "-1/3*2*x-Sin[x]/(9*(1+Cos[x])^2)+(8*Sin[x])/(9*(1+Cos[x]))-(Log[Sin[x]]*Sin[x])/(3*(1+Cos[x])^2)+(2*Log[Sin[x]]*Sin[x])/(3*(1+Cos[x]))" //
    );
  }

  public void test645() {
    check( //
        "Integrate[ArcCos[x]^2/x^5, x]", //
        "-1/(12*x^2)+(Sqrt[1-x^2]*ArcCos[x])/(6*x^3)+(Sqrt[1-x^2]*ArcCos[x])/(3*x)-ArcCos[x]^2/(4*x^4)+Log[x]/3" //
    );
  }

  public void test646() {
    check( //
        "Integrate[x^2*ArcSin[x]^2, x]", //
        "-1/9*4*x-1/27*2*x^3+4/9*Sqrt[1-x^2]*ArcSin[x]+2/9*x^2*Sqrt[1-x^2]*ArcSin[x]+1/3*x^3*ArcSin[x]^2" //
    );
  }

  public void test647() {
    check( //
        "Integrate[ArcTan[x]^2*x^3, x]", //
        "x^2/12+1/2*x*ArcTan[x]-1/6*x^3*ArcTan[x]-ArcTan[x]^2/4+1/4*x^4*ArcTan[x]^2-Log[1+x^2]/3" //
    );
  }

  public void test648() {
    check( //
        "Integrate[ArcTan[x]^2/x^5, x]", //
        "-1/(12*x^2)-ArcTan[x]/(6*x^3)+ArcTan[x]/(2*x)+ArcTan[x]^2/4-ArcTan[x]^2/(4*x^4)-1/3*2*Log[x]+Log[1+x^2]/3" //
    );
  }

  public void test649() {
    check( //
        "Integrate[ArcCsc[x]^2*x^3, x]", //
        "x^2/12+1/3*Sqrt[1-1/x^2]*x*ArcCsc[x]+1/6*Sqrt[1-1/x^2]*x^3*ArcCsc[x]+1/4*x^4*ArcCsc[x]^2+Log[x]/3" //
    );
  }

  public void test650() {
    check( //
        "Integrate[ArcSec[x]^4/x^5, x]", //
        "-3/(128*x^4)-45/(128*x^2)-(3*Sqrt[1-1/x^2]*ArcSec[x])/(32*x^3)-(45*Sqrt[1-1/x^2]*ArcSec[x])/(64*x)-1/128*45*ArcSec[x]^2+(3*ArcSec[x]^2)/(16*x^4)+(9*ArcSec[x]^2)/(16*x^2)+(Sqrt[1-1/x^2]*ArcSec[x]^3)/(4*x^3)+(3*Sqrt[1-1/x^2]*ArcSec[x]^3)/(8*x)+1/32*3*ArcSec[x]^4-ArcSec[x]^4/(4*x^4)" //
    );
  }

  public void test651() {
    check( //
        "Integrate[ArcSin[x]*Sqrt[1-x^2], x]", //
        "-x^2/4+1/2*x*Sqrt[1-x^2]*ArcSin[x]+ArcSin[x]^2/4" //
    );
  }

  public void test652() {
    check( //
        "Integrate[ArcCos[x]*Sqrt[1-x^2], x]", //
        "x^2/4+1/2*x*Sqrt[1-x^2]*ArcCos[x]-ArcCos[x]^2/4" //
    );
  }

  public void test653() {
    check( //
        "Integrate[ArcCos[x]*x*Sqrt[1-x^2], x]", //
        "-x/3+x^3/9-1/3*(1-x^2)^(3/2)*ArcCos[x]" //
    );
  }

  public void test654() {
    check( //
        "Integrate[ArcSin[x]*(1-x^2)^(3/2), x]", //
        "-1/16*5*x^2+x^4/16+3/8*x*Sqrt[1-x^2]*ArcSin[x]+1/4*x*(1-x^2)^(3/2)*ArcSin[x]+1/16*3*ArcSin[x]^2" //
    );
  }

  public void test655() {
    check( //
        "Integrate[ArcSin[x]*x*(1-x^2)^(3/2), x]", //
        "x/5-1/15*2*x^3+x^5/25-1/5*(1-x^2)^(5/2)*ArcSin[x]" //
    );
  }

  public void test656() {
    check( //
        "Integrate[ArcCos[x]*x^3*(1-x^2)^(3/2), x]", //
        "-2*x/35-x^3/105+1/175*8*x^5-x^7/49-1/5*(1-x^2)^(5/2)*ArcCos[x]+1/7*(1-x^2)^(7/2)*ArcCos[x]" //
    );
  }

  public void test657() {
    check( //
        "Integrate[(ArcCos[x]*(1-x^2)^(3/2))/x, x]", //
        "1/3*4*x-x^3/9+Sqrt[1-x^2]*ArcCos[x]+1/3*(1-x^2)^(3/2)*ArcCos[x]+2*I*ArcCos[x]*ArcTan[E^(I*ArcCos[x])]-I*PolyLog[2,(-1)*I*E^(I*ArcCos[x])]+I*PolyLog[2,I*E^(I*ArcCos[x])]" //
    );
  }

  public void test658() {
    check( //
        "Integrate[(ArcSin[x]*(1-x^2)^(3/2))/x^6, x]", //
        "-1/(20*x^4)+1/(5*x^2)-((1-x^2)^(5/2)*ArcSin[x])/(5*x^5)+Log[x]/5" //
    );
  }

  public void test659() {
    check( //
        "Integrate[(ArcSin[x]*x^2)/Sqrt[1-x^2], x]", //
        "x^2/4-1/2*x*Sqrt[1-x^2]*ArcSin[x]+ArcSin[x]^2/4" //
    );
  }

  public void test660() {
    check( //
        "Integrate[(ArcSin[x]*x^4)/Sqrt[1-x^2], x]", //
        "1/16*3*x^2+x^4/16-3/8*x*Sqrt[1-x^2]*ArcSin[x]-1/4*x^3*Sqrt[1-x^2]*ArcSin[x]+1/16*3*ArcSin[x]^2" //
    );
  }

  public void test661() {
    check( //
        "Integrate[(ArcSin[x]*x)/(1-x^2)^(3/2), x]", //
        "ArcSin[x]/Sqrt[1-x^2]-ArcTanh[x]" //
    );
  }

  public void test662() {
    check( //
        "Integrate[(ArcCos[x]*x)/(1-x^2)^(3/2), x]", //
        "ArcCos[x]/Sqrt[1-x^2]+ArcTanh[x]" //
    );
  }

  public void test663() {
    check( //
        "Integrate[ArcSin[x]/(1-x^2)^(5/2), x]", //
        "-1/(6*(1-x^2))+(x*ArcSin[x])/(3*(1-x^2)^(3/2))+(2*x*ArcSin[x])/(3*Sqrt[1-x^2])+Log[1-x^2]/3" //
    );
  }

  public void test664() {
    check( //
        "Integrate[(ArcSin[x]*x^3)/(1-x^2)^(3/2), x]", //
        "-x+ArcSin[x]/Sqrt[1-x^2]+Sqrt[1-x^2]*ArcSin[x]-ArcTanh[x]" //
    );
  }

  public void test665() {
    check( //
        "Integrate[ArcSin[x]/(x*(1-x^2)^(3/2)), x]", //
        "ArcSin[x]/Sqrt[1-x^2]-2*ArcSin[x]*ArcTanh[E^(I*ArcSin[x])]-ArcTanh[x]+I*PolyLog[2,-E^(I*ArcSin[x])]-I*PolyLog[2,E^(I*ArcSin[x])]" //
    );
  }

  public void test666() {
    check( //
        "Integrate[ArcCos[x]/(x^4*Sqrt[1-x^2]), x]", //
        "1/(6*x^2)-(Sqrt[1-x^2]*ArcCos[x])/(3*x^3)-(2*Sqrt[1-x^2]*ArcCos[x])/(3*x)-1/3*2*Log[x]" //
    );
  }

  public void test667() {
    check( //
        "Integrate[ArcCos[x]^2*x*Sqrt[1-x^2], x]", //
        "1/9*4*Sqrt[1-x^2]+2/27*(1-x^2)^(3/2)-2/3*x*ArcCos[x]+2/9*x^3*ArcCos[x]-1/3*(1-x^2)^(3/2)*ArcCos[x]^2" //
    );
  }

  public void test668() {
    check( //
        "Integrate[(ArcSin[x]^3*x^2)/Sqrt[1-x^2], x]", //
        "-1/8*3*x^2+3/4*x*Sqrt[1-x^2]*ArcSin[x]-1/8*3*ArcSin[x]^2+3/4*x^2*ArcSin[x]^2-1/2*x*Sqrt[1-x^2]*ArcSin[x]^3+ArcSin[x]^4/8" //
    );
  }

  public void test669() {
    check( //
        "Integrate[(ArcTan[x]*x)/(1+x^2)^2, x]", //
        "x/(4*(1+x^2))+ArcTan[x]/4-ArcTan[x]/(2*(1+x^2))" //
    );
  }

  public void test670() {
    check( //
        "Integrate[(ArcTan[x]*x)/(1+x^2)^3, x]", //
        "x/(16*(1+x^2)^2)+(3*x)/(32*(1+x^2))+1/32*3*ArcTan[x]-ArcTan[x]/(4*(1+x^2)^2)" //
    );
  }

  public void test671() {
    check( //
        "Integrate[(ArcTan[x]*x^2)/(1+x^2), x]", //
        "x*ArcTan[x]-ArcTan[x]^2/2-Log[1+x^2]/2" //
    );
  }

  public void test672() {
    check( //
        "Integrate[(ArcTan[x]*x^3)/(1+x^2), x]", //
        "-x/2+ArcTan[x]/2+1/2*x^2*ArcTan[x]+1/2*I*ArcTan[x]^2+ArcTan[x]*Log[2/(1+I*x)]+1/2*I*PolyLog[2,1-2/(1+I*x)]" //
    );
  }

  public void test673() {
    check( //
        "Integrate[(ArcTan[x]*x^2)/(1+x^2)^2, x]", //
        "-1/(4*(1+x^2))-(x*ArcTan[x])/(2*(1+x^2))+ArcTan[x]^2/4" //
    );
  }

  public void test674() {
    check( //
        "Integrate[(ArcTan[x]*x^3)/(1+x^2)^2, x]", //
        "-x/(4*(1+x^2))-ArcTan[x]/4+ArcTan[x]/(2*(1+x^2))-1/2*I*ArcTan[x]^2-ArcTan[x]*Log[2/(1+I*x)]-1/2*I*PolyLog[2,1-2/(1+I*x)]" //
    );
  }

  public void test675() {
    check( //
        "Integrate[(ArcTan[x]*x^5)/(1+x^2)^2, x]", //
        "-x/2+x/(4*(1+x^2))+1/4*3*ArcTan[x]+1/2*x^2*ArcTan[x]-ArcTan[x]/(2*(1+x^2))+I*ArcTan[x]^2+2*ArcTan[x]*Log[2/(1+I*x)]+I*PolyLog[2,1-2/(1+I*x)]" //
    );
  }

  public void test676() {
    check( //
        "Integrate[(ArcTan[x]*(1+x^2))/x^2, x]", //
        "-ArcTan[x]/x+x*ArcTan[x]+Log[x]-Log[1+x^2]" //
    );
  }

  public void test677() {
    check( //
        "Integrate[(ArcTan[x]*(1+x^2))/x^5, x]", //
        "-1/(12*x^3)-1/(4*x)-((1+x^2)^2*ArcTan[x])/(4*x^4)" //
    );
  }

  public void test678() {
    check( //
        "Integrate[(ArcTan[x]*(1+x^2)^2)/x^5, x]", //
        "-1/(12*x^3)-3/(4*x)-1/4*3*ArcTan[x]-ArcTan[x]/(4*x^4)-ArcTan[x]/x^2+1/2*I*PolyLog[2,(-1)*I*x]-1/2*I*PolyLog[2,I*x]" //
    );
  }

  public void test679() {
    check( //
        "Integrate[ArcTan[x]/(x^2*(1+x^2)), x]", //
        "-ArcTan[x]/x-ArcTan[x]^2/2+Log[x]-Log[1+x^2]/2" //
    );
  }

  public void test680() {
    check( //
        "Integrate[ArcTan[x]^2/x^3, x]", //
        "-ArcTan[x]/x-ArcTan[x]^2/2-ArcTan[x]^2/(2*x^2)+Log[x]-Log[1+x^2]/2" //
    );
  }

  public void test681() {
    check( //
        "Integrate[(ArcTan[x]^2*(1+x^2))/x^5, x]", //
        "-1/(12*x^2)-ArcTan[x]/(6*x^3)-ArcTan[x]/(2*x)-((1+x^2)^2*ArcTan[x]^2)/(4*x^4)+Log[x]/3-Log[1+x^2]/6" //
    );
  }

  public void test682() {
    check( //
        "Integrate[(ArcTan[x]^2*x^3)/(1+x^2)^3, x]", //
        "-1/(32*(1+x^2)^2)+5/(32*(1+x^2))+(x^3*ArcTan[x])/(8*(1+x^2)^2)+(3*x*ArcTan[x])/(16*(1+x^2))-1/32*3*ArcTan[x]^2+(x^4*ArcTan[x]^2)/(4*(1+x^2)^2)" //
    );
  }

  public void test683() {
    check( //
        "Integrate[(ArcSec[x]*Sqrt[x^2+(-1)*1])/x^2, x]", //
        "-Sqrt[x^2]/x^2-(Sqrt[-1+x^2]*ArcSec[x])/x-(2*I*Sqrt[x^2]*ArcSec[x]*ArcTan[E^(I*ArcSec[x])])/x+(I*Sqrt[x^2]*PolyLog[2,(-1)*I*E^(I*ArcSec[x])])/x-(I*Sqrt[x^2]*PolyLog[2,I*E^(I*ArcSec[x])])/x" //
    );
  }

  public void test684() {
    check( //
        "Integrate[(ArcCsc[x]*(x^2+(-1)*1)^(5/2))/x^3, x]", //
        "(3+2*x^4)/(12*x*Sqrt[x^2])-(5*(x^2+(-1)*1)^(3/2)*ArcCsc[x])/(3*x^2)-(5*Sqrt[x^2+(-1)*1]*ArcCsc[x])/(2*x^2)+((x^2+(-1)*1)^(5/2)*ArcCsc[x])/(3*x^2)-(5*x*ArcCsc[x]^2)/(4*Sqrt[x^2])-(7*x*Log[x])/(3*Sqrt[x^2])" //
    );
  }

  public void test685() {
    check( //
        "Integrate[(ArcSec[x]*Sqrt[x^2+(-1)*1])/x^4, x]", //
        "1/(3*Sqrt[x^2])-1/(9*x^2*Sqrt[x^2])+((x^2+(-1)*1)^(3/2)*ArcSec[x])/(3*x^3)" //
    );
  }

  public void test686() {
    check( //
        "Integrate[ArcSec[x]/(x^2+(-1)*1)^(5/2), x]", //
        "Sqrt[x^2]/(6*(1-x^2))-(x*ArcSec[x])/(3*(x^2+(-1)*1)^(3/2))+(2*x*ArcSec[x])/(3*Sqrt[x^2+(-1)*1])+1/6*5*ArcCoth[Sqrt[x^2]]" //
    );
  }

  public void test687() {
    check( //
        "Integrate[(ArcSec[x]*x^2)/(x^2+(-1)*1)^(5/2), x]", //
        "Sqrt[x^2]/(6*(1-x^2))-(x^3*ArcSec[x])/(3*(x^2+(-1)*1)^(3/2))-ArcCoth[Sqrt[x^2]]/6" //
    );
  }

  public void test688() {
    check( //
        "Integrate[(ArcSec[x]*x^3)/(x^2+(-1)*1)^(5/2), x]", //
        "x/(6*Sqrt[x^2]*(1-x^2))-ArcSec[x]/(3*(x^2+(-1)*1)^(3/2))-ArcSec[x]/Sqrt[x^2+(-1)*1]-(2*x*Log[x])/(3*Sqrt[x^2])+(x*Log[x^2+(-1)*1])/(3*Sqrt[x^2])" //
    );
  }

  public void test689() {
    check( //
        "Integrate[(ArcSec[x]*x^6)/(x^2+(-1)*1)^(5/2), x]", //
        "(Sqrt[x^2]*(2-3*x^2))/(6*(-1+x^2))-13/6*ArcCoth[Sqrt[x^2]]-(5*x^3*ArcSec[x])/(6*(-1+x^2)^(3/2))+(x^5*ArcSec[x])/(2*(-1+x^2)^(3/2))-(5*x*ArcSec[x])/(2*Sqrt[-1+x^2])-(5*I*Sqrt[x^2]*ArcSec[x]*ArcTan[E^(I*ArcSec[x])])/x+(5*I*Sqrt[x^2]*PolyLog[2,(-1)*I*E^(I*ArcSec[x])])/(2*x)-(5*I*Sqrt[x^2]*PolyLog[2,I*E^(I*ArcSec[x])])/(2*x)" //
    );
  }

  public void test690() {
    check( //
        "Integrate[ArcSec[x]/(x^2*Sqrt[x^2+(-1)*1]), x]", //
        "1/Sqrt[x^2]+(Sqrt[x^2+(-1)*1]*ArcSec[x])/x" //
    );
  }

  public void test691() {
    check( //
        "Integrate[ArcCsc[x]/(x^2*(x^2+(-1)*1)^(5/2)), x]", //
        "-1/Sqrt[x^2]+Sqrt[x^2]/(6*(x^2+(-1)*1))+((3-12*x^2+8*x^4)*ArcCsc[x])/(3*x*(x^2+(-1)*1)^(3/2))-1/6*11*ArcCoth[Sqrt[x^2]]" //
    );
  }

  public void test692() {
    check( //
        "Integrate[ArcCsc[x]^4/(x^2*Sqrt[x^2+(-1)*1]), x]", //
        "(24*Sqrt[x^2+(-1)*1])/x+(24*ArcCsc[x])/Sqrt[x^2]-(12*Sqrt[x^2+(-1)*1]*ArcCsc[x]^2)/x-(4*ArcCsc[x]^3)/Sqrt[x^2]+(Sqrt[x^2+(-1)*1]*ArcCsc[x]^4)/x" //
    );
  }

  public void test693() {
    check( //
        "Integrate[(ArcSec[x]^2*(x^2+(-1)*1)^(3/2))/x^5, x]", //
        "(Sqrt[x^2+(-1)*1]*(17*x^2+(-1)*2))/(64*x^4)-(3*ArcSec[x])/(8*x*Sqrt[x^2])+(9*x*ArcSec[x])/(64*Sqrt[x^2])+((x^2+(-1)*1)^2*ArcSec[x])/(8*x^3*Sqrt[x^2])-(3*Sqrt[x^2+(-1)*1]*ArcSec[x]^2)/(8*x^2)-((x^2+(-1)*1)^(3/2)*ArcSec[x]^2)/(4*x^4)+(x*ArcSec[x]^3)/(8*Sqrt[x^2])" //
    );
  }

  public void test694() {
    check( //
        "Integrate[(ArcSec[x]^3*Sqrt[x^2+(-1)*1])/x^4, x]", //
        "(2*(1-21*x^2))/(27*x^2*Sqrt[x^2])-(4*Sqrt[x^2+(-1)*1]*ArcSec[x])/(3*x)-(2*(x^2+(-1)*1)^(3/2)*ArcSec[x])/(9*x^3)+(2*ArcSec[x]^2)/(3*Sqrt[x^2])+((x^2+(-1)*1)*ArcSec[x]^2)/(3*x^2*Sqrt[x^2])+((x^2+(-1)*1)^(3/2)*ArcSec[x]^3)/(3*x^3)" //
    );
  }

  public void test695() {
    check( //
        "Integrate[ArcSin[Sqrt[(x-a)/(x+a)]], x]", //
        "-Sqrt[2]*a*Sqrt[(x-a)/(x+a)]/Sqrt[a/(x+a)]+(x+a)*ArcSin[Sqrt[(x-a)/(x+a)]]" //
    );
  }

  public void test696() {
    check( //
        "Integrate[ArcTan[Sqrt[(x-a)/(x+a)]], x]", //
        "x*ArcTan[Sqrt[-(a-x)/(a+x)]]-a*ArcTanh[Sqrt[-(a-x)/(a+x)]]" //
    );
  }

  public void test697() {
    check( //
        "Integrate[ArcTan[x]/(1+x)^3, x]", //
        "-1/(4*(1+x))-ArcTan[x]/(2*(1+x)^2)+Log[1+x]/4-Log[1+x^2]/8" //
    );
  }

  public void test698() {
    check( //
        "Integrate[ArcTan[x-a]/(x+a), x]", //
        "ArcTan[a-x]*Log[2/(1-I*(a-x))]-ArcTan[a-x]*Log[-(2*(a+x))/((I-2*a)*(1-I*(a-x)))]-1/2*I*PolyLog[2,1-2/(1-I*(a-x))]+1/2*I*PolyLog[2,1+(2*(a+x))/((I-2*a)*(1-I*(a-x)))]" //
    );
  }

  public void test699() {
    check( //
        "Integrate[ArcSin[Sqrt[1-x^2]]/Sqrt[1-x^2], x]", //
        "-Sqrt[x^2]*ArcSin[Sqrt[1-x^2]]^2/(2*x)" //
    );
  }

  public void test700() {
    check( //
        "Integrate[ArcTan[Sqrt[1+x^2]]*x/Sqrt[1+x^2], x]", //
        "Sqrt[1+x^2]*ArcTan[Sqrt[1+x^2]]-Log[2+x^2]/2" //
    );
  }

  public void test701() {
    check( //
        "Integrate[ArcSin[x]/(1-x)^(5/2), x]", //
        "-Sqrt[1+x]/(3*(1-x))+(2*ArcSin[x])/(3*(1-x)^(3/2))-ArcTanh[Sqrt[1+x]/Sqrt[2]]/(3*Sqrt[2])" //
    );
  }

  public void test702() {
    check( //
        "Integrate[ArcCsc[x]*(x+(-1)*1)^(5/2), x]", //
        "(4*x*(83-19*x+3*x^2)*Sqrt[x^2+(-1)*1])/(105*Sqrt[x+(-1)*1]*Sqrt[x^2])+2/7*(x+(-1)*1)^(7/2)*ArcCsc[x]+(4*x)/(7*Sqrt[x^2])*ArcTanh[Sqrt[x^2+(-1)*1]/Sqrt[x+(-1)*1]]" //
    );
  }

  public void test703() {
    check( //
        "Integrate[ArcSin[Sinh[x]]*Sech[x]^4, x]", //
        "-2/3*ArcSin[Cosh[x]/Sqrt[2]]+1/6*Sqrt[1-Sinh[x]^2]*Sech[x]+ArcSin[Sinh[x]]*Tanh[x]-1/3*ArcSin[Sinh[x]]*Tanh[x]^3" //
    );
  }

  public void test704() {
    check( //
        "Integrate[ArcCot[Cosh[x]]*Cosh[x]/Sinh[x]^4, x]", //
        "ArcTanh[Tanh[x]/Sqrt[2]]/(6*Sqrt[2])+Coth[x]/6-1/3*ArcCot[Cosh[x]]*Csch[x]^3" //
    );
  }

  public void test705() {
    check( //
        "Integrate[ArcSin[Tanh[x]]*E^x, x]", //
        "E^x*ArcSin[Tanh[x]]-Cosh[x]*Log[1+E^(2*x)]*Sqrt[Sech[x]^2]" //
    );
  }
}
