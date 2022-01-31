package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

public class MosesProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public MosesProblemsTests(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 60;
      if (init) {
        System.out.println("Moses");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void test1() {
    check( //
        "Integrate[Cot[x]^4, x]", //
        "x+Cot[x]-Cot[x]^3/3" //
    );
  }

  public void test2() {
    check( //
        "Integrate[1/(x^4*(1+x^2)), x]", //
        "-1/(3*x^3)+1/x+ArcTan[x]" //
    );
  }

  public void test3() {
    check( //
        "Integrate[(x^2+x)/Sqrt[x], x]", //
        "1/3*2*x^(3/2)+1/5*2*x^(5/2)" //
    );
  }

  public void test4() {
    check( //
        "Integrate[Cos[x], x]", //
        "Sin[x]" //
    );
  }

  public void test5() {
    check( //
        "Integrate[x*E^x^2, x]", //
        "E^x^2/2" //
    );
  }

  public void test6() {
    check( //
        "Integrate[Tan[x]*Sec[x]^2, x]", //
        "Sec[x]^2/2" //
    );
  }

  public void test7() {
    check( //
        "Integrate[x*Sqrt[1+x^2], x]", //
        "(1+x^2)^(3/2)/3" //
    );
  }

  public void test8() {
    check( //
        "Integrate[Sin[x]*E^x, x]", //
        "-1/2*E^x*Cos[x]+1/2*E^x*Sin[x]" //
    );
  }

  public void test9() {
    check( //
        "Integrate[Csc[x]^2*Cos[x]/Sin[x]^2, x]", //
        "-Csc[x]^3/3" //
    );
  }

  public void test10() {
    check( //
        "Integrate[Sin[E^x], x]", //
        "SinIntegral[E^x]" //
    );
  }

  public void test11() {
    check( //
        "Integrate[Sin[y]/y, y]", //
        "SinIntegral[y]" //
    );
  }

  public void test12() {
    check( //
        "Integrate[Sin[x]+E^x, x]", //
        "E^x-Cos[x]" //
    );
  }

  public void test13() {
    check( //
        "Integrate[E^x^2+2*x^2*E^x^2, x]", //
        "E^x^2*x" //
    );
  }

  public void test14() {
    check( //
        "Integrate[(x+E^x)^2, x]", //
        "-2*E^x+E^(2*x)/2+2*E^x*x+x^3/3" //
    );
  }

  public void test15() {
    check( //
        "Integrate[x^2+2*E^x+E^(2*x), x]", //
        "2*E^x+E^(2*x)/2+x^3/3" //
    );
  }

  public void test16() {
    check( //
        "Integrate[Sin[x]*Cos[x], x]", //
        "Sin[x]^2/2" //
    );
  }

  public void test17() {
    check( //
        "Integrate[x*E^x^2, x]", //
        "E^x^2/2" //
    );
  }

  public void test18() {
    check( //
        "Integrate[x*Sqrt[1+x^2], x]", //
        "(1+x^2)^(3/2)/3" //
    );
  }

  public void test19() {
    check( //
        "Integrate[E^x/(1+E^x), x]", //
        "Log[1+E^x]" //
    );
  }

  public void test20() {
    check( //
        "Integrate[x^(3/2), x]", //
        "1/5*2*x^(5/2)" //
    );
  }

  public void test21() {
    check( //
        "Integrate[Cos[2*x+3], x]", //
        "Sin[3+2*x]/2" //
    );
  }

  public void test22() {
    check( //
        "Integrate[2*y*z*E^(2*x), x]", //
        "E^(2*x)*y*z" //
    );
  }

  public void test23() {
    check( //
        "Integrate[Cos[E^x]^2*Sin[E^x]*E^x, x]", //
        "-Cos[E^x]^3/3" //
    );
  }

  public void test24() {
    check( //
        "Integrate[x*Sqrt[x+1], x]", //
        "-2/3*(1+x)^(3/2)+2/5*(1+x)^(5/2)" //
    );
  }

  public void test25() {
    check( //
        "Integrate[1/(x^4+(-1)*1), x]", //
        "-ArcTan[x]/2-ArcTanh[x]/2" //
    );
  }

  public void test26() {
    check( //
        "Integrate[E^x/(2+3*E^(2*x)), x]", //
        "ArcTan[Sqrt[3/2]*E^x]/Sqrt[6]" //
    );
  }

  public void test27() {
    check( //
        "Integrate[E^(2*x)/(A+B*E^(4*x)), x]", //
        "ArcTan[(Sqrt[B]*E^(2*x))/Sqrt[A]]/(2*Sqrt[A]*Sqrt[B])" //
    );
  }

  public void test28() {
    check( //
        "Integrate[E^(x+1)/(1+E^x), x]", //
        "E*Log[1+E^x]" //
    );
  }

  public void test29() {
    check( //
        "Integrate[10^x*E^x, x]", //
        "(10*E)^x/(1+Log[10])" //
    );
  }

  public void test30() {
    check( //
        "Integrate[x^3*Sin[x^2], x]", //
        "-1/2*x^2*Cos[x^2]+Sin[x^2]/2" //
    );
  }

  public void test31() {
    check( //
        "Integrate[x^7/(x^12+1), x]", //
        "-ArcTan[(1-2*x^4)/Sqrt[3]]/(4*Sqrt[3])-Log[1+x^4]/12+Log[1-x^4+x^8]/24" //
    );
  }

  public void test32() {
    check( //
        "Integrate[x^(3*a)*Sin[x^(2*a)], x]", //
        "(I*x^(1+3*a)*Gamma[1/2*(3+1/a),(-1)*I*x^(2*a)])/(((-1)*I*x^(2*a))^((1+3*a)/(2*a))*4*a)-(I*x^(1+3*a)*Gamma[1/2*(3+1/a),I*x^(2*a)])/((I*x^(2*a))^((1+3*a)/(2*a))*4*a)" //
    );
  }

  public void test33() {
    check( //
        "Integrate[Cos[Sqrt[x]], x]", //
        "2*Cos[Sqrt[x]]+2*Sqrt[x]*Sin[Sqrt[x]]" //
    );
  }

  public void test34() {
    check( //
        "Integrate[x*Sqrt[x+1], x]", //
        "-2/3*(1+x)^(3/2)+2/5*(1+x)^(5/2)" //
    );
  }

  public void test35() {
    check( //
        "Integrate[1/(Sqrt[x]+x^(1/3)), x]", //
        "6*x^(1/6)-3*x^(1/3)+2*Sqrt[x]-6*Log[1+x^(1/6)]" //
    );
  }

  public void test36() {
    check( //
        "Integrate[Sqrt[(x+1)/(2*x+3)], x]", //
        "1/2*Sqrt[1+x]*Sqrt[3+2*x]-ArcSinh[Sqrt[2]*Sqrt[1+x]]/(2*Sqrt[2])" //
    );
  }

  public void test37() {
    check( //
        "Integrate[x^4/(1-x^2)^(5/2), x]", //
        "x^3/(3*(1-x^2)^(3/2))-x/Sqrt[1-x^2]+ArcSin[x]" //
    );
  }

  public void test38() {
    check( //
        "Integrate[Sqrt[x]*(1+x)^(5/2), x]", //
        "5/64*Sqrt[x]*Sqrt[1+x]+5/32*x^(3/2)*Sqrt[1+x]+5/24*x^(3/2)*(1+x)^(3/2)+1/4*x^(3/2)*(1+x)^(5/2)-1/64*5*ArcSinh[Sqrt[x]]" //
    );
  }

  public void test39() {
    check( //
        "Integrate[x^4/(1-x^2)^(5/2), x]", //
        "x^3/(3*(1-x^2)^(3/2))-x/Sqrt[1-x^2]+ArcSin[x]" //
    );
  }

  public void test40() {
    check( //
        "Integrate[Sqrt[A^2+B^2-B^2*y^2]/(1-y^2), y]", //
        "B*ArcTan[(B*y)/Sqrt[A^2+B^2-B^2*y^2]]+A*ArcTanh[(A*y)/Sqrt[A^2+B^2-B^2*y^2]]" //
    );
  }

  public void test41() {
    check( //
        "Integrate[Sin[x]^2, x]", //
        "x/2-1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test42() {
    check( //
        "Integrate[Sqrt[A^2+B^2*Sin[x]^2]/Sin[x], x]", //
        "-B*ArcTan[(B*Cos[x])/Sqrt[A^2+B^2*Sin[x]^2]]-A*ArcTanh[(A*Cos[x])/Sqrt[A^2+B^2*Sin[x]^2]]" //
    );
  }

  public void test43() {
    check( //
        "Integrate[1/(1+Cos[x]), x]", //
        "Sin[x]/(1+Cos[x])" //
    );
  }

  public void test44() {
    check( //
        "Integrate[x*E^x, x]", //
        "-E^x+E^x*x" //
    );
  }

  public void test45() {
    check( //
        "Integrate[x/(x+1)^2*E^x, x]", //
        "E^x/(1+x)" //
    );
  }

  public void test46() {
    check( //
        "Integrate[(1+2*x^2)*E^x^2, x]", //
        "E^x^2*x" //
    );
  }

  public void test47() {
    check( //
        "Integrate[E^x^2, x]", //
        "1/2*Sqrt[Pi]*Erfi[x]" //
    );
  }

  public void test48() {
    check( //
        "Integrate[E^x/x, x]", //
        "ExpIntegralEi[x]" //
    );
  }

  public void test49() {
    check( //
        "Integrate[x/(x^3+1), x]", //
        "-ArcTan[(1-2*x)/Sqrt[3]]/Sqrt[3]-Log[1+x]/3+Log[1-x+x^2]/6" //
    );
  }

  public void test50() {
    check( //
        "Integrate[1/(x^6+(-1)*1), x]", //
        "-ArcTan[(Sqrt[3]*x)/(1-x^2)]/(2*Sqrt[3])-ArcTanh[x]/3-ArcTanh[x/(1+x^2)]/6" //
    );
  }

  public void test51() {
    check( //
        "Integrate[1/((B^2-A^2)*x^2-A^2*B^2+A^4), x]", //
        "ArcTanh[x/A]/(A*(A^2-B^2))" //
    );
  }

  public void test52() {
    check( //
        "Integrate[x*Log[x], x]", //
        "-x^2/4+1/2*x^2*Log[x]" //
    );
  }

  public void test53() {
    check( //
        "Integrate[x^2*ArcSin[x], x]", //
        "Sqrt[1-x^2]/3-(1-x^2)^(3/2)/9+1/3*x^3*ArcSin[x]" //
    );
  }

  public void test54() {
    check( //
        "Integrate[1/(x^2+2*x+1), x]", //
        "-1/(1+x)" //
    );
  }

  public void test55() {
    check( //
        "Integrate[Log[x]/(Log[x]+1)^2, x]", //
        "x/(1+Log[x])" //
    );
  }

  public void test56() {
    check( //
        "Integrate[1/(x*(1+Log[x]^2)), x]", //
        "ArcTan[Log[x]]" //
    );
  }

  public void test57() {
    check( //
        "Integrate[1/Log[x], x]", //
        "LogIntegral[x]" //
    );
  }

  public void test58() {
    check( //
        "Integrate[x*(Cos[x]+Sin[x]), x]", //
        "Cos[x]-x*Cos[x]+Sin[x]+x*Sin[x]" //
    );
  }

  public void test59() {
    check( //
        "Integrate[(x+E^x)/E^x, x]", //
        "-1/E^x+x-x/E^x" //
    );
  }

  public void test60() {
    check( //
        "Integrate[x*(1+E^x)^2, x]", //
        "-2*E^x-E^(2*x)/4+2*E^x*x+1/2*E^(2*x)*x+x^2/2" //
    );
  }

  public void test61() {
    check( //
        "Integrate[x*Cos[x], x]", //
        "Cos[x]+x*Sin[x]" //
    );
  }

  public void test62() {
    check( //
        "Integrate[Cos[Sqrt[x]], x]", //
        "2*Cos[Sqrt[x]]+2*Sqrt[x]*Sin[Sqrt[x]]" //
    );
  }

  public void test63() {
    check( //
        "Integrate[x*Cos[x], x]", //
        "Cos[x]+x*Sin[x]" //
    );
  }

  public void test64() {
    check( //
        "Integrate[x*Log[x]^2, x]", //
        "x^2/4-1/2*x^2*Log[x]+1/2*x^2*Log[x]^2" //
    );
  }

  public void test65() {
    check( //
        "Integrate[Cos[x]*(1+Sin[x]^3), x]", //
        "Sin[x]+Sin[x]^4/4" //
    );
  }

  public void test66() {
    check( //
        "Integrate[1/(x*(1+Log[x]^2)), x]", //
        "ArcTan[Log[x]]" //
    );
  }

  public void test67() {
    check( //
        "Integrate[1/(Sqrt[1-x^2]*(1+ArcSin[x]^2)), x]", //
        "ArcTan[ArcSin[x]]" //
    );
  }

  public void test68() {
    check( //
        "Integrate[Sin[x]/(Sin[x]+Cos[x]), x]", //
        "x/2-Log[Cos[x]+Sin[x]]/2" //
    );
  }

  public void test69() {
    check( //
        "Integrate[-Sqrt[A^2+B^2*(1-y^2)]/(1-y^2), y]", //
        "-B*ArcTan[(B*y)/Sqrt[A^2+B^2-B^2*y^2]]-A*ArcTanh[(A*y)/Sqrt[A^2+B^2-B^2*y^2]]" //
    );
  }

  public void test70() {
    check( //
        "Integrate[-(A^2+B^2)*Cos[z]^2/(B*(1-(A^2+B^2)/B^2*Sin[z]^2)), z]", //
        "-B*z-A*ArcTanh[(A*Tan[z])/B]" //
    );
  }

  public void test71() {
    check( //
        "Integrate[-(A^2+B^2)/(B*(1+w^2)^2*(1-(A^2+B^2)/B^2*w^2/(1+w^2))), w]", //
        "-B*ArcTan[w]-A*ArcTanh[(A*w)/B]" //
    );
  }

  public void test72() {
    check( //
        "Integrate[-B*(A^2+B^2)/((1+w^2)*(B^2-A^2*w^2)), w]", //
        "-B*ArcTan[w]-A*ArcTanh[(A*w)/B]" //
    );
  }

  public void test73() {
    check( //
        "Integrate[x^4/(1-x^2)^(5/2), x]", //
        "x^3/(3*(1-x^2)^(3/2))-x/Sqrt[1-x^2]+ArcSin[x]" //
    );
  }

  public void test74() {
    check( //
        "Integrate[Sin[y]^4/Cos[y]^4, y]", //
        "y-Tan[y]+Tan[y]^3/3" //
    );
  }

  public void test75() {
    check( //
        "Integrate[z^4/(1+z^2), z]", //
        "-z+z^3/3+ArcTan[z]" //
    );
  }

  public void test76() {
    check( //
        "Integrate[(2*x^2+1)*E^x^2, x]", //
        "E^x^2*x" //
    );
  }

  public void test77() {
    check( //
        "Integrate[(2*x^6+5*x^4+x^3+4*x^2+1)/(x^2+1)^2*E^x^2, x]", //
        "E^x^2*x+E^x^2/(2*(1+x^2))" //
    );
  }

  public void test78() {
    check( //
        "Integrate[1/(E*E^x), x]", //
        "-1/E^(1+x)" //
    );
  }

  public void test79() {
    check( //
        "Integrate[(x+1/x)*Log[x], x]", //
        "-x^2/4+1/2*x^2*Log[x]+Log[x]^2/2" //
    );
  }

  public void test80() {
    check( //
        "Integrate[x/(1+x^4), x]", //
        "ArcTan[x^2]/2" //
    );
  }

  public void test81() {
    check( //
        "Integrate[x^5/(1+x^4), x]", //
        "x^2/2-ArcTan[x^2]/2" //
    );
  }

  public void test82() {
    check( //
        "Integrate[1/(1+Tan[x]^2), x]", //
        "x/2+1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test83() {
    check( //
        "Integrate[x^4/(1-x^2)^(5/2), x]", //
        "x^3/(3*(1-x^2)^(3/2))-x/Sqrt[1-x^2]+ArcSin[x]" //
    );
  }

  public void test84() {
    check( //
        "Integrate[-x^2/(1-x^2)^(3/2), x]", //
        "-x/Sqrt[1-x^2]+ArcSin[x]" //
    );
  }

  public void test85() {
    check( //
        "Integrate[Sin[x]*E^x, x]", //
        "-1/2*E^x*Cos[x]+1/2*E^x*Sin[x]" //
    );
  }

  public void test86() {
    check( //
        "Integrate[1/x, x]", //
        "Log[x]" //
    );
  }

  public void test87() {
    check( //
        "Integrate[Sec[2*t]/(1+Sec[t]^2+3*Tan[t]), t]", //
        "-Log[Cos[t]-Sin[t]]/12-Log[Cos[t]+Sin[t]]/4+Log[2*Cos[t]+Sin[t]]/3-1/(2*(1+Tan[t]))" //
    );
  }

  public void test88() {
    check( //
        "Integrate[1/Sec[x]^2, x]", //
        "x/2+1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test89() {
    check( //
        "Integrate[(x^2+1)/Sqrt[x], x]", //
        "2*Sqrt[x]+1/5*2*x^(5/2)" //
    );
  }

  public void test90() {
    check( //
        "Integrate[x/Sqrt[x^2+2*x+5], x]", //
        "Sqrt[5+2*x+x^2]-ArcSinh[1/2*(1+x)]" //
    );
  }

  public void test91() {
    check( //
        "Integrate[Sin[x]^2*Cos[x], x]", //
        "Sin[x]^3/3" //
    );
  }

  public void test92() {
    check( //
        "Integrate[E^x/(1+E^x), x]", //
        "Log[1+E^x]" //
    );
  }

  public void test93() {
    check( //
        "Integrate[E^(2*x)/(1+E^x), x]", //
        "E^x-Log[1+E^x]" //
    );
  }

  public void test94() {
    check( //
        "Integrate[1/(1-Cos[x]), x]", //
        "-Sin[x]/(1-Cos[x])" //
    );
  }

  public void test95() {
    check( //
        "Integrate[Tan[x]*Sec[x]^2, x]", //
        "Sec[x]^2/2" //
    );
  }

  public void test96() {
    check( //
        "Integrate[x*Log[x], x]", //
        "-x^2/4+1/2*x^2*Log[x]" //
    );
  }

  public void test97() {
    check( //
        "Integrate[Sin[x]*Cos[x], x]", //
        "Sin[x]^2/2" //
    );
  }

  public void test98() {
    check( //
        "Integrate[(x+1)/Sqrt[2*x-x^2], x]", //
        "-Sqrt[2*x-x^2]-2*ArcSin[1-x]" //
    );
  }

  public void test99() {
    check( //
        "Integrate[2*E^x/(2+3*E^(2*x)), x]", //
        "Sqrt[2/3]*ArcTan[Sqrt[3/2]*E^x]" //
    );
  }

  public void test100() {
    check( //
        "Integrate[x^4/(1-x^2)^(5/2), x]", //
        "x^3/(3*(1-x^2)^(3/2))-x/Sqrt[1-x^2]+ArcSin[x]" //
    );
  }

  public void test101() {
    check( //
        "Integrate[E^(6*x)/(E^(4*x)+1), x]", //
        "E^(2*x)/2-ArcTan[E^(2*x)]/2" //
    );
  }

  public void test102() {
    check( //
        "Integrate[Log[2+3*x^2], x]", //
        "-2*x+2*Sqrt[2/3]*ArcTan[Sqrt[3/2]*x]+x*Log[2+3*x^2]" //
    );
  }

  public void test103() {
    check( //
        "Integrate[1/(r*Sqrt[2*H*r^2-a^2]), x]", //
        "x/(r*Sqrt[-a^2+2*H*r^2])" //
    );
  }

  public void test104() {
    check( //
        "Integrate[1/(r*Sqrt[2*H*r^2-a^2-e^2]), x]", //
        "x/(r*Sqrt[-a^2-e^2+2*H*r^2])" //
    );
  }

  public void test105() {
    check( //
        "Integrate[1/(r*Sqrt[2*H*r^2-a^2-2*K*r^4]), x]", //
        "x/(r*Sqrt[-a^2+2*H*r^2-2*K*r^4])" //
    );
  }

  public void test106() {
    check( //
        "Integrate[1/(r*Sqrt[2*H*r^2-a^2-e^2-2*K*r^4]), x]", //
        "x/(r*Sqrt[-a^2-e^2+2*H*r^2-2*K*r^4])" //
    );
  }

  public void test107() {
    check( //
        "Integrate[1/(r*Sqrt[2*H*r^2-a^2-2*K*r]), x]", //
        "x/(r*Sqrt[-a^2-2*r*(K-H*r)])" //
    );
  }

  public void test108() {
    check( //
        "Integrate[1/(r*Sqrt[2*H*r^2-a^2-e^2-2*K*r]), x]", //
        "If[$VersionNumber>=8,x/(r*Sqrt[-a^2-e^2-2*r*(K-H*r)]),x/(r*Sqrt[-a^2-e^2-2*K*r+2*H*r^2])]" //
    );
  }

  public void test109() {
    check( //
        "Integrate[r/Sqrt[2*E*r^2-a^2], x]", //
        "(r*x)/Sqrt[-a^2+2*E*r^2]" //
    );
  }

  public void test110() {
    check( //
        "Integrate[r/Sqrt[2*E*r^2-a^2-e^2], x]", //
        "(r*x)/Sqrt[-a^2-e^2+2*E*r^2]" //
    );
  }

  public void test111() {
    check( //
        "Integrate[r/Sqrt[2*E*r^2-a^2-2*K*r^4], x]", //
        "(r*x)/Sqrt[-a^2+2*E*r^2-2*K*r^4]" //
    );
  }

  public void test112() {
    check( //
        "Integrate[r/Sqrt[2*E*r^2-a^2-e^2-2*K*r^4], x]", //
        "(r*x)/Sqrt[-a^2-e^2+2*E*r^2-2*K*r^4]" //
    );
  }

  public void test113() {
    check( //
        "Integrate[r/Sqrt[2*H*r^2-a^2-e^2-2*K*r], x]", //
        "If[$VersionNumber>=8,(r*x)/Sqrt[-a^2-e^2-2*r*(K-H*r)],(r*x)/Sqrt[-a^2-e^2-2*K*r+2*H*r^2]]" //
    );
  }
}
