package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

public class HearnProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public HearnProblemsTests(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 60;
      if (init) {
        System.out.println("Hearn");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void test1() {
    check( //
        "Integrate[1+x+x^2, x]", //
        "x+x^2/2+x^3/3" //
    );
  }

  public void test2() {
    check( //
        "Integrate[x^2*(2*x^2+x)^2, x]", //
        "x^5/5+1/3*2*x^6+1/7*4*x^7" //
    );
  }

  public void test3() {
    check( //
        "Integrate[x*(x^2+2*x+1), x]", //
        "x^2/2+1/3*2*x^3+x^4/4" //
    );
  }

  public void test4() {
    check( //
        "Integrate[1/x, x]", //
        "Log[x]" //
    );
  }

  public void test5() {
    check( //
        "Integrate[(x+1)^3/(x+(-1)*1)^4, x]", //
        "8/(3*(1-x)^3)-6/(1-x)^2+6/(1-x)+Log[1-x]" //
    );
  }

  public void test6() {
    check( //
        "Integrate[1/(x*(x+(-1)*1)*(x+1)^2), x]", //
        "-1/(2*(1+x))+Log[1-x]/4-Log[x]+3/4*Log[1+x]" //
    );
  }

  public void test7() {
    check( //
        "Integrate[(a*x+b)/((x-p)*(x-q)), x]", //
        "((b+a*p)*Log[p-x])/(p-q)-((b+a*q)*Log[q-x])/(p-q)" //
    );
  }

  public void test8() {
    check( //
        "Integrate[1/(a*x^2+b*x+c), x]", //
        "-(2*ArcTanh[(b+2*a*x)/Sqrt[b^2-4*a*c]])/Sqrt[b^2-4*a*c]" //
    );
  }

  public void test9() {
    check( //
        "Integrate[(a*x+b)/(1+x^2), x]", //
        "b*ArcTan[x]+1/2*a*Log[1+x^2]" //
    );
  }

  public void test10() {
    check( //
        "Integrate[1/(x^2-2*x+3), x]", //
        "-ArcTan[(1-x)/Sqrt[2]]/Sqrt[2]" //
    );
  }

  public void test11() {
    check( //
        "Integrate[1/((x+(-1)*1)*(x^2+1))^2, x]", //
        "1/(4*(1-x))-1/(4*(1+x^2))+ArcTan[x]/4-Log[1-x]/2+Log[1+x^2]/4" //
    );
  }

  public void test12() {
    check( //
        "Integrate[x/((x-a)*(x-b)*(x-c)), x]", //
        "(a*Log[a-x])/((a-b)*(a-c))-(b*Log[b-x])/((a-b)*(b-c))+(c*Log[c-x])/((a-c)*(b-c))" //
    );
  }

  public void test13() {
    check( //
        "Integrate[x/((x^2+a^2)*(x^2+b^2)), x]", //
        "-Log[a^2+x^2]/(2*(a^2-b^2))+Log[b^2+x^2]/(2*(a^2-b^2))" //
    );
  }

  public void test14() {
    check( //
        "Integrate[x^2/((x^2+a^2)*(x^2+b^2)), x]", //
        "(a*ArcTan[x/a])/(a^2-b^2)-(b*ArcTan[x/b])/(a^2-b^2)" //
    );
  }

  public void test15() {
    check( //
        "Integrate[x/((x+(-1)*1)*(x^2+1)), x]", //
        "ArcTan[x]/2+Log[1-x]/2-Log[1+x^2]/4" //
    );
  }

  public void test16() {
    check( //
        "Integrate[x/(1+x^3), x]", //
        "-ArcTan[(1-2*x)/Sqrt[3]]/Sqrt[3]-Log[1+x]/3+Log[1-x+x^2]/6" //
    );
  }

  public void test17() {
    check( //
        "Integrate[x^3/((x+(-1)*1)^2*(x^3+1)), x]", //
        "1/(2*(1-x))+3/4*Log[1-x]-Log[1+x]/12-Log[1-x+x^2]/3" //
    );
  }

  public void test18() {
    check( //
        "Integrate[1/(1+x^4), x]", //
        "-ArcTan[1-Sqrt[2]*x]/(2*Sqrt[2])+ArcTan[1+Sqrt[2]*x]/(2*Sqrt[2])-Log[1-Sqrt[2]*x+x^2]/(4*Sqrt[2])+Log[1+Sqrt[2]*x+x^2]/(4*Sqrt[2])" //
    );
  }

  public void test19() {
    check( //
        "Integrate[x^2/(1+x^4), x]", //
        "-ArcTan[1-Sqrt[2]*x]/(2*Sqrt[2])+ArcTan[1+Sqrt[2]*x]/(2*Sqrt[2])+Log[1-Sqrt[2]*x+x^2]/(4*Sqrt[2])-Log[1+Sqrt[2]*x+x^2]/(4*Sqrt[2])" //
    );
  }

  public void test20() {
    check( //
        "Integrate[1/(1+x^2+x^4), x]", //
        "-ArcTan[(1-2*x)/Sqrt[3]]/(2*Sqrt[3])+ArcTan[(1+2*x)/Sqrt[3]]/(2*Sqrt[3])-Log[1-x+x^2]/4+Log[1+x+x^2]/4" //
    );
  }

  public void test21() {
    check( //
        "Integrate[(a+b*x)^p, x]", //
        "(a+b*x)^(1+p)/(b*(1+p))" //
    );
  }

  public void test22() {
    check( //
        "Integrate[x*(a+b*x)^p, x]", //
        "-(a*(a+b*x)^(1+p))/(b^2*(1+p))+(a+b*x)^(2+p)/(b^2*(2+p))" //
    );
  }

  public void test23() {
    check( //
        "Integrate[x^2*(a+b*x)^p, x]", //
        "(a^2*(a+b*x)^(1+p))/(b^3*(1+p))-(2*a*(a+b*x)^(2+p))/(b^3*(2+p))+(a+b*x)^(3+p)/(b^3*(3+p))" //
    );
  }

  public void test24() {
    check( //
        "Integrate[1/(a+b*x), x]", //
        "Log[a+b*x]/b" //
    );
  }

  public void test25() {
    check( //
        "Integrate[1/(a+b*x)^2, x]", //
        "-1/(b*(a+b*x))" //
    );
  }

  public void test26() {
    check( //
        "Integrate[x/(a+b*x), x]", //
        "x/b-(a*Log[a+b*x])/b^2" //
    );
  }

  public void test27() {
    check( //
        "Integrate[x^2/(a+b*x), x]", //
        "-(a*x)/b^2+x^2/(2*b)+(a^2*Log[a+b*x])/b^3" //
    );
  }

  public void test28() {
    check( //
        "Integrate[1/(x*(a+b*x)), x]", //
        "Log[x]/a-Log[a+b*x]/a" //
    );
  }

  public void test29() {
    check( //
        "Integrate[1/(x^2*(a+b*x)), x]", //
        "-1/(a*x)-(b*Log[x])/a^2+(b*Log[a+b*x])/a^2" //
    );
  }

  public void test30() {
    check( //
        "Integrate[1/(x*(a+b*x))^2, x]", //
        "-1/(a^2*x)-b/(a^2*(a+b*x))-(2*b*Log[x])/a^3+(2*b*Log[a+b*x])/a^3" //
    );
  }

  public void test31() {
    check( //
        "Integrate[1/(c^2+x^2), x]", //
        "ArcTan[x/c]/c" //
    );
  }

  public void test32() {
    check( //
        "Integrate[1/(c^2-x^2), x]", //
        "ArcTanh[x/c]/c" //
    );
  }

  public void test33() {
    check( //
        "Integrate[1/(2*x^3+(-1)*1), x]", //
        "-ArcTan[(1+2*2^(1/3)*x)/Sqrt[3]]/(2^(1/3)*Sqrt[3])+Log[1-2^(1/3)*x]/(3*2^(1/3))-Log[1+2^(1/3)*x+2^(2/3)*x^2]/(6*2^(1/3))" //
    );
  }

  public void test34() {
    check( //
        "Integrate[1/(x^3+(-1)*2), x]", //
        "-ArcTan[(1+2^(2/3)*x)/Sqrt[3]]/(2^(2/3)*Sqrt[3])+Log[2^(1/3)-x]/(3*2^(2/3))-Log[2^(2/3)+2^(1/3)*x+x^2]/(6*2^(2/3))" //
    );
  }

  public void test35() {
    check( //
        "Integrate[1/(a*x^3-b), x]", //
        "-ArcTan[(b^(1/3)+2*a^(1/3)*x)/(Sqrt[3]*b^(1/3))]/(Sqrt[3]*a^(1/3)*b^(2/3))+Log[b^(1/3)-a^(1/3)*x]/(3*a^(1/3)*b^(2/3))-Log[b^(2/3)+a^(1/3)*b^(1/3)*x+a^(2/3)*x^2]/(6*a^(1/3)*b^(2/3))" //
    );
  }

  public void test36() {
    check( //
        "Integrate[1/(x^4+(-1)*2), x]", //
        "-ArcTan[x/2^(1/4)]/(2*2^(3/4))-ArcTanh[x/2^(1/4)]/(2*2^(3/4))" //
    );
  }

  public void test37() {
    check( //
        "Integrate[1/(5*x^4+(-1)*1), x]", //
        "-ArcTan[5^(1/4)*x]/(2*5^(1/4))-ArcTanh[5^(1/4)*x]/(2*5^(1/4))" //
    );
  }

  public void test38() {
    check( //
        "Integrate[1/(3*x^4+7), x]", //
        "If[$VersionNumber<9,-ArcTan[1-(3/7)^(1/4)*Sqrt[2]*x]/(2*Sqrt[2]*3^(1/4)*7^(3/4))+ArcTan[1+(3/7)^(1/4)*Sqrt[2]*x]/(2*Sqrt[2]*3^(1/4)*7^(3/4))-Log[Sqrt[21]-Sqrt[2]*3^(3/4)*7^(1/4)*x+3*x^2]/(4*Sqrt[2]*3^(1/4)*7^(3/4))+Log[Sqrt[21]+Sqrt[2]*3^(3/4)*7^(1/4)*x+3*x^2]/(4*Sqrt[2]*3^(1/4)*7^(3/4)),-ArcTan[1-(3/7)^(1/4)*Sqrt[2]*x]/(2*Sqrt[2]*3^(1/4)*7^(3/4))+ArcTan[1+(3/7)^(1/4)*Sqrt[2]*x]/(2*Sqrt[2]*3^(1/4)*7^(3/4))-Log[Sqrt[21]-Sqrt[2]*3^(3/4)*7^(1/4)*x+3*x^2]/(4*Sqrt[2]*3^(1/4)*7^(3/4))+Log[Sqrt[21]+Sqrt[2]*3^(3/4)*7^(1/4)*x+3*x^2]/(4*Sqrt[2]*3^(1/4)*7^(3/4))]" //
    );
  }

  public void test39() {
    check( //
        "Integrate[1/(x^4+3*x^2+(-1)*1), x]", //
        "-Sqrt[2/(13*(3+Sqrt[13]))]*ArcTan[Sqrt[2/(3+Sqrt[13])]*x]-Sqrt[1/26*(3+Sqrt[13])]*ArcTanh[Sqrt[2/(-3+Sqrt[13])]*x]" //
    );
  }

  public void test40() {
    check( //
        "Integrate[1/(x^4-3*x^2+(-1)*1), x]", //
        "-Sqrt[1/26*(3+Sqrt[13])]*ArcTan[Sqrt[2/(-3+Sqrt[13])]*x]-Sqrt[2/(13*(3+Sqrt[13]))]*ArcTanh[Sqrt[2/(3+Sqrt[13])]*x]" //
    );
  }

  public void test41() {
    check( //
        "Integrate[1/(x^4-3*x^2+1), x]", //
        "-Sqrt[2/(5*(3+Sqrt[5]))]*ArcTanh[Sqrt[2/(3+Sqrt[5])]*x]+Sqrt[1/10*(3+Sqrt[5])]*ArcTanh[Sqrt[1/2*(3+Sqrt[5])]*x]" //
    );
  }

  public void test42() {
    check( //
        "Integrate[1/(x^4-4*x^2+1), x]", //
        "ArcTanh[x/Sqrt[2-Sqrt[3]]]/(2*Sqrt[3*(2-Sqrt[3])])-ArcTanh[x/Sqrt[2+Sqrt[3]]]/(2*Sqrt[3*(2+Sqrt[3])])" //
    );
  }

  public void test43() {
    check( //
        "Integrate[1/(x^4+4*x^2+1), x]", //
        "ArcTan[x/Sqrt[2-Sqrt[3]]]/(2*Sqrt[3*(2-Sqrt[3])])-ArcTan[x/Sqrt[2+Sqrt[3]]]/(2*Sqrt[3*(2+Sqrt[3])])" //
    );
  }

  public void test44() {
    check( //
        "Integrate[1/(x^4+x^2+2), x]", //
        "-1/2*Sqrt[1/14*(-1+2*Sqrt[2])]*ArcTan[(Sqrt[-1+2*Sqrt[2]]-2*x)/Sqrt[1+2*Sqrt[2]]]+1/2*Sqrt[1/14*(-1+2*Sqrt[2])]*ArcTan[(Sqrt[-1+2*Sqrt[2]]+2*x)/Sqrt[1+2*Sqrt[2]]]-Log[Sqrt[2]-Sqrt[-1+2*Sqrt[2]]*x+x^2]/(4*Sqrt[2*(-1+2*Sqrt[2])])+Log[Sqrt[2]+Sqrt[-1+2*Sqrt[2]]*x+x^2]/(4*Sqrt[2*(-1+2*Sqrt[2])])" //
    );
  }

  public void test45() {
    check( //
        "Integrate[1/(x^4-x^2+2), x]", //
        "-1/2*Sqrt[1/14*(1+2*Sqrt[2])]*ArcTan[(Sqrt[1+2*Sqrt[2]]-2*x)/Sqrt[-1+2*Sqrt[2]]]+1/2*Sqrt[1/14*(1+2*Sqrt[2])]*ArcTan[(Sqrt[1+2*Sqrt[2]]+2*x)/Sqrt[-1+2*Sqrt[2]]]-Log[Sqrt[2]-Sqrt[1+2*Sqrt[2]]*x+x^2]/(4*Sqrt[2*(1+2*Sqrt[2])])+Log[Sqrt[2]+Sqrt[1+2*Sqrt[2]]*x+x^2]/(4*Sqrt[2*(1+2*Sqrt[2])])" //
    );
  }

  public void test46() {
    check( //
        "Integrate[1/(x^6+(-1)*1), x]", //
        "ArcTan[(1-2*x)/Sqrt[3]]/(2*Sqrt[3])-ArcTan[(1+2*x)/Sqrt[3]]/(2*Sqrt[3])-ArcTanh[x]/3+Log[1-x+x^2]/12-Log[1+x+x^2]/12" //
    );
  }

  public void test47() {
    check( //
        "Integrate[1/(x^6+(-1)*2), x]", //
        "ArcTan[1/Sqrt[3]-(2^(5/6)*x)/Sqrt[3]]/(2*2^(5/6)*Sqrt[3])-ArcTan[1/Sqrt[3]+(2^(5/6)*x)/Sqrt[3]]/(2*2^(5/6)*Sqrt[3])-ArcTanh[x/2^(1/6)]/(3*2^(5/6))+Log[2^(1/3)-2^(1/6)*x+x^2]/(12*2^(5/6))-Log[2^(1/3)+2^(1/6)*x+x^2]/(12*2^(5/6))" //
    );
  }

  public void test48() {
    check( //
        "Integrate[1/(x^6+2), x]", //
        "ArcTan[x/2^(1/6)]/(3*2^(5/6))-ArcTan[Sqrt[3]-2^(5/6)*x]/(6*2^(5/6))+ArcTan[Sqrt[3]+2^(5/6)*x]/(6*2^(5/6))-Log[2^(1/3)-2^(1/6)*Sqrt[3]*x+x^2]/(4*2^(5/6)*Sqrt[3])+Log[2^(1/3)+2^(1/6)*Sqrt[3]*x+x^2]/(4*2^(5/6)*Sqrt[3])" //
    );
  }

  public void test49() {
    check( //
        "Integrate[1/(x^8+1), x]", //
        "-ArcTan[(Sqrt[2-Sqrt[2]]-2*x)/Sqrt[2+Sqrt[2]]]/(4*Sqrt[2*(2-Sqrt[2])])-ArcTan[(Sqrt[2+Sqrt[2]]-2*x)/Sqrt[2-Sqrt[2]]]/(4*Sqrt[2*(2+Sqrt[2])])+ArcTan[(Sqrt[2-Sqrt[2]]+2*x)/Sqrt[2+Sqrt[2]]]/(4*Sqrt[2*(2-Sqrt[2])])+ArcTan[(Sqrt[2+Sqrt[2]]+2*x)/Sqrt[2-Sqrt[2]]]/(4*Sqrt[2*(2+Sqrt[2])])-1/16*Sqrt[2-Sqrt[2]]*Log[1-Sqrt[2-Sqrt[2]]*x+x^2]+1/16*Sqrt[2-Sqrt[2]]*Log[1+Sqrt[2-Sqrt[2]]*x+x^2]-1/16*Sqrt[2+Sqrt[2]]*Log[1-Sqrt[2+Sqrt[2]]*x+x^2]+1/16*Sqrt[2+Sqrt[2]]*Log[1+Sqrt[2+Sqrt[2]]*x+x^2]" //
    );
  }

  public void test50() {
    check( //
        "Integrate[1/(x^8+(-1)*1), x]", //
        "-ArcTan[x]/4+ArcTan[1-Sqrt[2]*x]/(4*Sqrt[2])-ArcTan[1+Sqrt[2]*x]/(4*Sqrt[2])-ArcTanh[x]/4+Log[1-Sqrt[2]*x+x^2]/(8*Sqrt[2])-Log[1+Sqrt[2]*x+x^2]/(8*Sqrt[2])" //
    );
  }

  public void test51() {
    check( //
        "Integrate[1/(x^8-x^4+1), x]", //
        "-ArcTan[(Sqrt[2-Sqrt[3]]-2*x)/Sqrt[2+Sqrt[3]]]/(2*Sqrt[6])-ArcTan[(Sqrt[2+Sqrt[3]]-2*x)/Sqrt[2-Sqrt[3]]]/(2*Sqrt[6])+ArcTan[(Sqrt[2-Sqrt[3]]+2*x)/Sqrt[2+Sqrt[3]]]/(2*Sqrt[6])+ArcTan[(Sqrt[2+Sqrt[3]]+2*x)/Sqrt[2-Sqrt[3]]]/(2*Sqrt[6])-Log[1-Sqrt[2-Sqrt[3]]*x+x^2]/(4*Sqrt[6])+Log[1+Sqrt[2-Sqrt[3]]*x+x^2]/(4*Sqrt[6])-Log[1-Sqrt[2+Sqrt[3]]*x+x^2]/(4*Sqrt[6])+Log[1+Sqrt[2+Sqrt[3]]*x+x^2]/(4*Sqrt[6])" //
    );
  }

  public void test52() {
    check( //
        "Integrate[x^7/(x^12+1), x]", //
        "-ArcTan[(1-2*x^4)/Sqrt[3]]/(4*Sqrt[3])-Log[1+x^4]/12+Log[1-x^4+x^8]/24" //
    );
  }

  public void test53() {
    check( //
        "Integrate[Log[x], x]", //
        "-x+x*Log[x]" //
    );
  }

  public void test54() {
    check( //
        "Integrate[x*Log[x], x]", //
        "-x^2/4+1/2*x^2*Log[x]" //
    );
  }

  public void test55() {
    check( //
        "Integrate[x^2*Log[x], x]", //
        "-x^3/9+1/3*x^3*Log[x]" //
    );
  }

  public void test56() {
    check( //
        "Integrate[x^p*Log[x], x]", //
        "-x^(1+p)/(1+p)^2+(x^(1+p)*Log[x])/(1+p)" //
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
        "Integrate[x^9*Log[x]^11, x]", //
        "-1/156250000*6237*x^10+1/15625000*6237*x^10*Log[x]-1/3125000*6237*x^10*Log[x]^2+1/312500*2079*x^10*Log[x]^3-1/125000*2079*x^10*Log[x]^4+1/62500*2079*x^10*Log[x]^5-1/12500*693*x^10*Log[x]^6+1/1250*99*x^10*Log[x]^7-1/1000*99*x^10*Log[x]^8+11/100*x^10*Log[x]^9-11/100*x^10*Log[x]^10+1/10*x^10*Log[x]^11" //
    );
  }

  public void test59() {
    check( //
        "Integrate[Log[x]^2/x, x]", //
        "Log[x]^3/3" //
    );
  }

  public void test60() {
    check( //
        "Integrate[1/Log[x], x]", //
        "LogIntegral[x]" //
    );
  }

  public void test61() {
    check( //
        "Integrate[1/Log[x+1], x]", //
        "LogIntegral[1+x]" //
    );
  }

  public void test62() {
    check( //
        "Integrate[1/(x*Log[x]), x]", //
        "Log[Log[x]]" //
    );
  }

  public void test63() {
    check( //
        "Integrate[1/(x*Log[x])^2, x]", //
        "-ExpIntegralEi[-Log[x]]-1/(x*Log[x])" //
    );
  }

  public void test64() {
    check( //
        "Integrate[Log[x]^p/x, x]", //
        "Log[x]^(1+p)/(1+p)" //
    );
  }

  public void test65() {
    check( //
        "Integrate[Log[x]*(a*x+b), x]", //
        "-b*x-1/4*a*x^2+b*x*Log[x]+1/2*a*x^2*Log[x]" //
    );
  }

  public void test66() {
    check( //
        "Integrate[(a*x+b)^2*Log[x], x]", //
        "-b^2*x-1/2*a*b*x^2-1/9*a^2*x^3-(b^3*Log[x])/(3*a)+((b+a*x)^3*Log[x])/(3*a)" //
    );
  }

  public void test67() {
    check( //
        "Integrate[Log[x]/(a*x+b)^2, x]", //
        "(x*Log[x])/(b*(b+a*x))-Log[b+a*x]/(a*b)" //
    );
  }

  public void test68() {
    check( //
        "Integrate[x*Log[a*x+b], x]", //
        "(b*x)/(2*a)-x^2/4-(b^2*Log[b+a*x])/(2*a^2)+1/2*x^2*Log[b+a*x]" //
    );
  }

  public void test69() {
    check( //
        "Integrate[x^2*Log[a*x+b], x]", //
        "-(b^2*x)/(3*a^2)+(b*x^2)/(6*a)-x^3/9+(b^3*Log[b+a*x])/(3*a^3)+1/3*x^3*Log[b+a*x]" //
    );
  }

  public void test70() {
    check( //
        "Integrate[Log[x^2+a^2], x]", //
        "-2*x+2*a*ArcTan[x/a]+x*Log[a^2+x^2]" //
    );
  }

  public void test71() {
    check( //
        "Integrate[x*Log[x^2+a^2], x]", //
        "-x^2/2+1/2*(a^2+x^2)*Log[a^2+x^2]" //
    );
  }

  public void test72() {
    check( //
        "Integrate[x^2*Log[x^2+a^2], x]", //
        "1/3*2*a^2*x-1/9*2*x^3-2/3*a^3*ArcTan[x/a]+1/3*x^3*Log[a^2+x^2]" //
    );
  }

  public void test73() {
    check( //
        "Integrate[x^4*Log[x^2+a^2], x]", //
        "-1/5*2*a^4*x+1/15*2*a^2*x^3-1/25*2*x^5+2/5*a^5*ArcTan[x/a]+1/5*x^5*Log[a^2+x^2]" //
    );
  }

  public void test74() {
    check( //
        "Integrate[Log[x^2-a^2], x]", //
        "-2*x+2*a*ArcTanh[x/a]+x*Log[-a^2+x^2]" //
    );
  }

  public void test75() {
    check( //
        "Integrate[Log[Log[Log[Log[x]]]], x]", //
        "CannotIntegrate[Log[Log[Log[Log[x]]]],x]", //
        "Integrate(Log(Log(Log(Log(x)))),x)", -1);
  }

  public void test76() {
    check( //
        "Integrate[Sin[x], x]", //
        "-Cos[x]" //
    );
  }

  public void test77() {
    check( //
        "Integrate[Cos[x], x]", //
        "Sin[x]" //
    );
  }

  public void test78() {
    check( //
        "Integrate[Tan[x], x]", //
        "-Log[Cos[x]]" //
    );
  }

  public void test79() {
    check( //
        "Integrate[1/Tan[x], x]", //
        "Log[Sin[x]]" //
    );
  }

  public void test80() {
    check( //
        "Integrate[1/(1+Tan[x])^2, x]", //
        "Log[Cos[x]+Sin[x]]/2-1/(2*(1+Tan[x]))" //
    );
  }

  public void test81() {
    check( //
        "Integrate[1/Cos[x], x]", //
        "ArcTanh[Sin[x]]" //
    );
  }

  public void test82() {
    check( //
        "Integrate[1/Sin[x], x]", //
        "-ArcTanh[Cos[x]]" //
    );
  }

  public void test83() {
    check( //
        "Integrate[Sin[x]^2, x]", //
        "x/2-1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test84() {
    check( //
        "Integrate[x^3*Sin[x^2], x]", //
        "-1/2*x^2*Cos[x^2]+Sin[x^2]/2" //
    );
  }

  public void test85() {
    check( //
        "Integrate[Sin[x]^3, x]", //
        "-Cos[x]+Cos[x]^3/3" //
    );
  }

  public void test86() {
    check( //
        "Integrate[Sin[x]^p, x]", //
        "(Cos[x]*Hypergeometric2F1[1/2,1/2*(1+p),1/2*(3+p),Sin[x]^2]*Sin[x]^(1+p))/((1+p)*Sqrt[Cos[x]^2])" //
    );
  }

  public void test87() {
    check( //
        "Integrate[(Sin[x]^2+1)^2*Cos[x], x]", //
        "Sin[x]+1/3*2*Sin[x]^3+Sin[x]^5/5" //
    );
  }

  public void test88() {
    check( //
        "Integrate[Cos[x]^2, x]", //
        "x/2+1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test89() {
    check( //
        "Integrate[Cos[x]^3, x]", //
        "Sin[x]-Sin[x]^3/3" //
    );
  }

  public void test90() {
    check( //
        "Integrate[1/Cos[x]^2, x]", //
        "Tan[x]" //
    );
  }

  public void test91() {
    check( //
        "Integrate[Sin[x]*Sin[2*x], x]", //
        "Sin[x]/2-Sin[3*x]/6" //
    );
  }

  public void test92() {
    check( //
        "Integrate[x*Sin[x], x]", //
        "-x*Cos[x]+Sin[x]" //
    );
  }

  public void test93() {
    check( //
        "Integrate[x^2*Sin[x], x]", //
        "2*Cos[x]-x^2*Cos[x]+2*x*Sin[x]" //
    );
  }

  public void test94() {
    check( //
        "Integrate[x*Sin[x]^2, x]", //
        "x^2/4-1/2*x*Cos[x]*Sin[x]+Sin[x]^2/4" //
    );
  }

  public void test95() {
    check( //
        "Integrate[x^2*Sin[x]^2, x]", //
        "-x/4+x^3/6+1/4*Cos[x]*Sin[x]-1/2*x^2*Cos[x]*Sin[x]+1/2*x*Sin[x]^2" //
    );
  }

  public void test96() {
    check( //
        "Integrate[x*Sin[x]^3, x]", //
        "-2/3*x*Cos[x]+1/3*2*Sin[x]-1/3*x*Cos[x]*Sin[x]^2+Sin[x]^3/9" //
    );
  }

  public void test97() {
    check( //
        "Integrate[x*Cos[x], x]", //
        "Cos[x]+x*Sin[x]" //
    );
  }

  public void test98() {
    check( //
        "Integrate[x^2*Cos[x], x]", //
        "2*x*Cos[x]-2*Sin[x]+x^2*Sin[x]" //
    );
  }

  public void test99() {
    check( //
        "Integrate[x*Cos[x]^2, x]", //
        "x^2/4+Cos[x]^2/4+1/2*x*Cos[x]*Sin[x]" //
    );
  }

  public void test100() {
    check( //
        "Integrate[x^2*Cos[x]^2, x]", //
        "-x/4+x^3/6+1/2*x*Cos[x]^2-1/4*Cos[x]*Sin[x]+1/2*x^2*Cos[x]*Sin[x]" //
    );
  }

  public void test101() {
    check( //
        "Integrate[x*Cos[x]^3, x]", //
        "1/3*2*Cos[x]+Cos[x]^3/9+2/3*x*Sin[x]+1/3*x*Cos[x]^2*Sin[x]" //
    );
  }

  public void test102() {
    check( //
        "Integrate[Sin[x]/x, x]", //
        "SinIntegral[x]" //
    );
  }

  public void test103() {
    check( //
        "Integrate[Cos[x]/x, x]", //
        "CosIntegral[x]" //
    );
  }

  public void test104() {
    check( //
        "Integrate[Sin[x]/x^2, x]", //
        "CosIntegral[x]-Sin[x]/x" //
    );
  }

  public void test105() {
    check( //
        "Integrate[Sin[x]^2/x, x]", //
        "-CosIntegral[2*x]/2+Log[x]/2" //
    );
  }

  public void test106() {
    check( //
        "Integrate[Tan[x]^3, x]", //
        "Log[Cos[x]]+Tan[x]^2/2" //
    );
  }

  public void test107() {
    check( //
        "Integrate[Sin[a+b*x], x]", //
        "-Cos[a+b*x]/b" //
    );
  }

  public void test108() {
    check( //
        "Integrate[Cos[a+b*x], x]", //
        "Sin[a+b*x]/b" //
    );
  }

  public void test109() {
    check( //
        "Integrate[Tan[a+b*x], x]", //
        "-Log[Cos[a+b*x]]/b" //
    );
  }

  public void test110() {
    check( //
        "Integrate[1/Tan[a+b*x], x]", //
        "Log[Sin[a+b*x]]/b" //
    );
  }

  public void test111() {
    check( //
        "Integrate[1/Sin[a+b*x], x]", //
        "-ArcTanh[Cos[a+b*x]]/b" //
    );
  }

  public void test112() {
    check( //
        "Integrate[1/Cos[a+b*x], x]", //
        "ArcTanh[Sin[a+b*x]]/b" //
    );
  }

  public void test113() {
    check( //
        "Integrate[Sin[a+b*x]^2, x]", //
        "x/2-(Cos[a+b*x]*Sin[a+b*x])/(2*b)" //
    );
  }

  public void test114() {
    check( //
        "Integrate[Sin[a+b*x]^3, x]", //
        "-Cos[a+b*x]/b+Cos[a+b*x]^3/(3*b)" //
    );
  }

  public void test115() {
    check( //
        "Integrate[Cos[a+b*x]^2, x]", //
        "x/2+(Cos[a+b*x]*Sin[a+b*x])/(2*b)" //
    );
  }

  public void test116() {
    check( //
        "Integrate[Cos[a+b*x]^3, x]", //
        "Sin[a+b*x]/b-Sin[a+b*x]^3/(3*b)" //
    );
  }

  public void test117() {
    check( //
        "Integrate[1/Cos[a+b*x]^2, x]", //
        "Tan[a+b*x]/b" //
    );
  }

  public void test118() {
    check( //
        "Integrate[1/(1+Cos[x]), x]", //
        "Sin[x]/(1+Cos[x])" //
    );
  }

  public void test119() {
    check( //
        "Integrate[1/(1-Cos[x]), x]", //
        "-Sin[x]/(1-Cos[x])" //
    );
  }

  public void test120() {
    check( //
        "Integrate[1/(1+Sin[x]), x]", //
        "-Cos[x]/(1+Sin[x])" //
    );
  }

  public void test121() {
    check( //
        "Integrate[1/(1-Sin[x]), x]", //
        "Cos[x]/(1-Sin[x])" //
    );
  }

  public void test122() {
    check( //
        "Integrate[1/(a+b*Sin[x]), x]", //
        "(2*ArcTan[(b+a*Tan[x/2])/Sqrt[a^2-b^2]])/Sqrt[a^2-b^2]" //
    );
  }

  public void test123() {
    check( //
        "Integrate[1/(a+b*Sin[x]+Cos[x]), x]", //
        "-(2*ArcTanh[(b-(1-a)*Tan[x/2])/Sqrt[1-a^2+b^2]])/Sqrt[1-a^2+b^2]" //
    );
  }

  public void test124() {
    check( //
        "Integrate[x^2*Sin[a+b*x]^2, x]", //
        "-x/(4*b^2)+x^3/6+(Cos[a+b*x]*Sin[a+b*x])/(4*b^3)-(x^2*Cos[a+b*x]*Sin[a+b*x])/(2*b)+(x*Sin[a+b*x]^2)/(2*b^2)" //
    );
  }

  public void test125() {
    check( //
        "Integrate[Cos[x]*Cos[2*x], x]", //
        "Sin[x]/2+Sin[3*x]/6" //
    );
  }

  public void test126() {
    check( //
        "Integrate[x^2*Cos[a+b*x]^2, x]", //
        "-x/(4*b^2)+x^3/6+(x*Cos[a+b*x]^2)/(2*b^2)-(Cos[a+b*x]*Sin[a+b*x])/(4*b^3)+(x^2*Cos[a+b*x]*Sin[a+b*x])/(2*b)" //
    );
  }

  public void test127() {
    check( //
        "Integrate[1/Tan[x]^3, x]", //
        "-Cot[x]^2/2-Log[Sin[x]]" //
    );
  }

  public void test128() {
    check( //
        "Integrate[x^3*Tan[x]^4, x]", //
        "-x^2/2+1/3*4*I*x^3+x^4/4-4*x^2*Log[1+E^(2*I*x)]+Log[Cos[x]]+4*I*x*PolyLog[2,-E^(2*I*x)]-2*PolyLog[3,-E^(2*I*x)]+x*Tan[x]-x^3*Tan[x]-1/2*x^2*Tan[x]^2+1/3*x^3*Tan[x]^3" //
    );
  }

  public void test129() {
    check( //
        "Integrate[x^3*Tan[x]^6, x]", //
        "1/20*19*x^2-1/15*23*I*x^3-x^4/4+23/5*x^2*Log[1+E^(2*I*x)]-2*Log[Cos[x]]-23/5*I*x*PolyLog[2,-E^(2*I*x)]+23/10*PolyLog[3,-E^(2*I*x)]-19/10*x*Tan[x]+x^3*Tan[x]-Tan[x]^2/20+4/5*x^2*Tan[x]^2+1/10*x*Tan[x]^3-1/3*x^3*Tan[x]^3-3/20*x^2*Tan[x]^4+1/5*x^3*Tan[x]^5" //
    );
  }

  public void test130() {
    check( //
        "Integrate[x*Tan[x]^2, x]", //
        "-x^2/2+Log[Cos[x]]+x*Tan[x]" //
    );
  }

  public void test131() {
    check( //
        "Integrate[Sin[2*x]*Cos[3*x], x]", //
        "Cos[x]/2-Cos[5*x]/10" //
    );
  }

  public void test132() {
    check( //
        "Integrate[Sin[x]^2*Cos[x]^2, x]", //
        "x/8+1/8*Cos[x]*Sin[x]-1/4*Cos[x]^3*Sin[x]" //
    );
  }

  public void test133() {
    check( //
        "Integrate[1/(Sin[x]^2*Cos[x]^2), x]", //
        "-Cot[x]+Tan[x]" //
    );
  }

  public void test134() {
    check( //
        "Integrate[d^x*Sin[x], x]", //
        "-(d^x*Cos[x])/(1+Log[d]^2)+(d^x*Log[d]*Sin[x])/(1+Log[d]^2)" //
    );
  }

  public void test135() {
    check( //
        "Integrate[d^x*Cos[x], x]", //
        "(d^x*Cos[x]*Log[d])/(1+Log[d]^2)+(d^x*Sin[x])/(1+Log[d]^2)" //
    );
  }

  public void test136() {
    check( //
        "Integrate[x*d^x*Sin[x], x]", //
        "(2*d^x*Cos[x]*Log[d])/(1+Log[d]^2)^2-(d^x*x*Cos[x])/(1+Log[d]^2)+(d^x*Sin[x])/(1+Log[d]^2)^2-(d^x*Log[d]^2*Sin[x])/(1+Log[d]^2)^2+(d^x*x*Log[d]*Sin[x])/(1+Log[d]^2)" //
    );
  }

  public void test137() {
    check( //
        "Integrate[x*d^x*Cos[x], x]", //
        "(d^x*Cos[x])/(1+Log[d]^2)^2-(d^x*Cos[x]*Log[d]^2)/(1+Log[d]^2)^2+(d^x*x*Cos[x]*Log[d])/(1+Log[d]^2)-(2*d^x*Log[d]*Sin[x])/(1+Log[d]^2)^2+(d^x*x*Sin[x])/(1+Log[d]^2)" //
    );
  }

  public void test138() {
    check( //
        "Integrate[x^2*d^x*Sin[x], x]", //
        "(2*d^x*Cos[x])/(1+Log[d]^2)^3-(6*d^x*Cos[x]*Log[d]^2)/(1+Log[d]^2)^3+(4*d^x*x*Cos[x]*Log[d])/(1+Log[d]^2)^2-(d^x*x^2*Cos[x])/(1+Log[d]^2)-(6*d^x*Log[d]*Sin[x])/(1+Log[d]^2)^3+(2*d^x*Log[d]^3*Sin[x])/(1+Log[d]^2)^3+(2*d^x*x*Sin[x])/(1+Log[d]^2)^2-(2*d^x*x*Log[d]^2*Sin[x])/(1+Log[d]^2)^2+(d^x*x^2*Log[d]*Sin[x])/(1+Log[d]^2)" //
    );
  }

  public void test139() {
    check( //
        "Integrate[x^2*d^x*Cos[x], x]", //
        "-(6*d^x*Cos[x]*Log[d])/(1+Log[d]^2)^3+(2*d^x*Cos[x]*Log[d]^3)/(1+Log[d]^2)^3+(2*d^x*x*Cos[x])/(1+Log[d]^2)^2-(2*d^x*x*Cos[x]*Log[d]^2)/(1+Log[d]^2)^2+(d^x*x^2*Cos[x]*Log[d])/(1+Log[d]^2)-(2*d^x*Sin[x])/(1+Log[d]^2)^3+(6*d^x*Log[d]^2*Sin[x])/(1+Log[d]^2)^3-(4*d^x*x*Log[d]*Sin[x])/(1+Log[d]^2)^2+(d^x*x^2*Sin[x])/(1+Log[d]^2)" //
    );
  }

  public void test140() {
    check( //
        "Integrate[x^3*d^x*Sin[x], x]", //
        "-(24*d^x*Cos[x]*Log[d])/(1+Log[d]^2)^4+(24*d^x*Cos[x]*Log[d]^3)/(1+Log[d]^2)^4+(6*d^x*x*Cos[x])/(1+Log[d]^2)^3-(18*d^x*x*Cos[x]*Log[d]^2)/(1+Log[d]^2)^3+(6*d^x*x^2*Cos[x]*Log[d])/(1+Log[d]^2)^2-(d^x*x^3*Cos[x])/(1+Log[d]^2)-(6*d^x*Sin[x])/(1+Log[d]^2)^4+(36*d^x*Log[d]^2*Sin[x])/(1+Log[d]^2)^4-(6*d^x*Log[d]^4*Sin[x])/(1+Log[d]^2)^4-(18*d^x*x*Log[d]*Sin[x])/(1+Log[d]^2)^3+(6*d^x*x*Log[d]^3*Sin[x])/(1+Log[d]^2)^3+(3*d^x*x^2*Sin[x])/(1+Log[d]^2)^2-(3*d^x*x^2*Log[d]^2*Sin[x])/(1+Log[d]^2)^2+(d^x*x^3*Log[d]*Sin[x])/(1+Log[d]^2)" //
    );
  }

  public void test141() {
    check( //
        "Integrate[x^3*d^x*Cos[x], x]", //
        "-(6*d^x*Cos[x])/(1+Log[d]^2)^4+(36*d^x*Cos[x]*Log[d]^2)/(1+Log[d]^2)^4-(6*d^x*Cos[x]*Log[d]^4)/(1+Log[d]^2)^4-(18*d^x*x*Cos[x]*Log[d])/(1+Log[d]^2)^3+(6*d^x*x*Cos[x]*Log[d]^3)/(1+Log[d]^2)^3+(3*d^x*x^2*Cos[x])/(1+Log[d]^2)^2-(3*d^x*x^2*Cos[x]*Log[d]^2)/(1+Log[d]^2)^2+(d^x*x^3*Cos[x]*Log[d])/(1+Log[d]^2)+(24*d^x*Log[d]*Sin[x])/(1+Log[d]^2)^4-(24*d^x*Log[d]^3*Sin[x])/(1+Log[d]^2)^4-(6*d^x*x*Sin[x])/(1+Log[d]^2)^3+(18*d^x*x*Log[d]^2*Sin[x])/(1+Log[d]^2)^3-(6*d^x*x^2*Log[d]*Sin[x])/(1+Log[d]^2)^2+(d^x*x^3*Sin[x])/(1+Log[d]^2)" //
    );
  }

  public void test142() {
    check( //
        "Integrate[Sin[x]*Sin[2*x]*Sin[3*x], x]", //
        "-Cos[2*x]/8-Cos[4*x]/16+Cos[6*x]/24" //
    );
  }

  public void test143() {
    check( //
        "Integrate[Cos[x]*Cos[2*x]*Cos[3*x], x]", //
        "x/4+Sin[2*x]/8+Sin[4*x]/16+Sin[6*x]/24" //
    );
  }

  public void test144() {
    check( //
        "Integrate[Sin[k*x]^3*x^2, x]", //
        "(14*Cos[k*x])/(9*k^3)-(2*x^2*Cos[k*x])/(3*k)-(2*Cos[k*x]^3)/(27*k^3)+(4*x*Sin[k*x])/(3*k^2)-(x^2*Cos[k*x]*Sin[k*x]^2)/(3*k)+(2*x*Sin[k*x]^3)/(9*k^2)" //
    );
  }

  public void test145() {
    check( //
        "Integrate[x*Cos[k/Sin[x]]*Cos[x]/Sin[x]^2, x]", //
        "CannotIntegrate[x*Cos[k*Csc[x]]*Cot[x]*Csc[x],x]", //
        "Integrate(x*Cos(k*Csc(x))*Cot(x)*Csc(x),x)", -1);
  }

  public void test146() {
    check( //
        "Integrate[Cos[x]/(Sin[x]*Tan[x/2]), x]", //
        "-x-Cot[x/2]" //
    );
  }

  public void test147() {
    check( //
        "Integrate[Sin[a*x]/(b+c*Sin[a*x])^2, x]", //
        "-(2*c*ArcTan[(c+b*Tan[1/2*a*x])/Sqrt[b^2-c^2]])/(a*(b^2-c^2)^(3/2))-(b*Cos[a*x])/(a*(b^2-c^2)*(b+c*Sin[a*x]))" //
    );
  }

  public void test148() {
    check( //
        "Integrate[Sin[Log[x]], x]", //
        "-1/2*x*Cos[Log[x]]+1/2*x*Sin[Log[x]]" //
    );
  }

  public void test149() {
    check( //
        "Integrate[Cos[Log[x]], x]", //
        "1/2*x*Cos[Log[x]]+1/2*x*Sin[Log[x]]" //
    );
  }

  public void test150() {
    check( //
        "Integrate[E^x, x]", //
        "E^x" //
    );
  }

  public void test151() {
    check( //
        "Integrate[a^x, x]", //
        "a^x/Log[a]" //
    );
  }

  public void test152() {
    check( //
        "Integrate[E^(a*x), x]", //
        "E^(a*x)/a" //
    );
  }

  public void test153() {
    check( //
        "Integrate[E^(a*x)/x, x]", //
        "ExpIntegralEi[a*x]" //
    );
  }

  public void test154() {
    check( //
        "Integrate[1/(a+b*E^(m*x)), x]", //
        "x/a-Log[a+b*E^(m*x)]/(a*m)" //
    );
  }

  public void test155() {
    check( //
        "Integrate[E^(2*x)/(1+E^x), x]", //
        "E^x-Log[1+E^x]" //
    );
  }

  public void test156() {
    check( //
        "Integrate[E^(2*x)*E^(a*x), x]", //
        "E^((2+a)*x)/(2+a)" //
    );
  }

  public void test157() {
    check( //
        "Integrate[1/(a*E^(m*x)+b*E^(-m*x)), x]", //
        "ArcTan[(Sqrt[a]*E^(m*x))/Sqrt[b]]/(Sqrt[a]*Sqrt[b]*m)" //
    );
  }

  public void test158() {
    check( //
        "Integrate[x*E^(a*x), x]", //
        "-E^(a*x)/a^2+(E^(a*x)*x)/a" //
    );
  }

  public void test159() {
    check( //
        "Integrate[x^20*E^x, x]", //
        "2432902008176640000*E^x-2432902008176640000*E^x*x+1216451004088320000*E^x*x^2-405483668029440000*E^x*x^3+101370917007360000*E^x*x^4-20274183401472000*E^x*x^5+3379030566912000*E^x*x^6-482718652416000*E^x*x^7+60339831552000*E^x*x^8-6704425728000*E^x*x^9+670442572800*E^x*x^10-60949324800*E^x*x^11+5079110400*E^x*x^12-390700800*E^x*x^13+27907200*E^x*x^14-1860480*E^x*x^15+116280*E^x*x^16-6840*E^x*x^17+380*E^x*x^18-20*E^x*x^19+E^x*x^20" //
    );
  }

  public void test160() {
    check( //
        "Integrate[a^x/b^x, x]", //
        "a^x/(b^x*(Log[a]-Log[b]))" //
    );
  }

  public void test161() {
    check( //
        "Integrate[a^x*b^x, x]", //
        "(a^x*b^x)/(Log[a]+Log[b])" //
    );
  }

  public void test162() {
    check( //
        "Integrate[a^x/x^2, x]", //
        "-a^x/x+ExpIntegralEi[x*Log[a]]*Log[a]" //
    );
  }

  public void test163() {
    check( //
        "Integrate[x*a^x/(1+b*x)^2, x]", //
        "a^x/(b^2*(1+b*x))+ExpIntegralEi[((1+b*x)*Log[a])/b]/(a^(1/b)*b^2)-(ExpIntegralEi[((1+b*x)*Log[a])/b]*Log[a])/(a^(1/b)*b^3)" //
    );
  }

  public void test164() {
    check( //
        "Integrate[x*E^(a*x)/(1+a*x)^2, x]", //
        "E^(a*x)/(a^2*(1+a*x))" //
    );
  }

  public void test165() {
    check( //
        "Integrate[x*k^x^2, x]", //
        "k^x^2/(2*Log[k])" //
    );
  }

  public void test166() {
    check( //
        "Integrate[E^x^2, x]", //
        "1/2*Sqrt[Pi]*Erfi[x]" //
    );
  }

  public void test167() {
    check( //
        "Integrate[x*E^x^2, x]", //
        "E^x^2/2" //
    );
  }

  public void test168() {
    check( //
        "Integrate[(x+1)*E^(1/x)/x^4, x]", //
        "-E^(1/x)-E^(1/x)/x^2+E^(1/x)/x" //
    );
  }

  public void test169() {
    check( //
        "Integrate[(2*x^3+x)*(E^x^2)^2*E^(1-x*E^x^2)/(1-x*E^x^2)^2, x]", //
        "-E^(1-E^x^2*x)/(-1+E^x^2*x)" //
    );
  }

  public void test170() {
    check( //
        "Integrate[E^E^E^E^x, x]", //
        "CannotIntegrate[E^E^E^E^x,x]" //
    );
  }

  public void test171() {
    check( //
        "Integrate[E^x*Log[x], x]", //
        "-ExpIntegralEi[x]+E^x*Log[x]" //
    );
  }

  public void test172() {
    check( //
        "Integrate[x*E^x*Log[x], x]", //
        "-E^x+ExpIntegralEi[x]-E^x*Log[x]+E^x*x*Log[x]" //
    );
  }

  public void test173() {
    check( //
        "Integrate[E^(2*x)*Log[E^x], x]", //
        "-E^(2*x)/4+1/2*E^(2*x)*Log[E^x]" //
    );
  }

  public void test174() {
    check( //
        "Integrate[Sqrt[2]*x^2+2*x, x]", //
        "x^2+1/3*Sqrt[2]*x^3" //
    );
  }

  public void test175() {
    check( //
        "Integrate[Log[x]/Sqrt[a*x+b], x]", //
        "-(4*Sqrt[b+a*x])/a+(4*Sqrt[b]*ArcTanh[Sqrt[b+a*x]/Sqrt[b]])/a+(2*Sqrt[b+a*x]*Log[x])/a" //
    );
  }

  public void test176() {
    check( //
        "Integrate[Sqrt[a+b*x]*Sqrt[c+d*x], x]", //
        "((b*c-a*d)*Sqrt[a+b*x]*Sqrt[c+d*x])/(4*b*d)+((a+b*x)^(3/2)*Sqrt[c+d*x])/(2*b)-((b*c-a*d)^2*ArcTanh[(Sqrt[d]*Sqrt[a+b*x])/(Sqrt[b]*Sqrt[c+d*x])])/(4*b^(3/2)*d^(3/2))" //
    );
  }

  public void test177() {
    check( //
        "Integrate[Sqrt[a+b*x], x]", //
        "(2*(a+b*x)^(3/2))/(3*b)" //
    );
  }

  public void test178() {
    check( //
        "Integrate[x*Sqrt[a+b*x], x]", //
        "-(2*a*(a+b*x)^(3/2))/(3*b^2)+(2*(a+b*x)^(5/2))/(5*b^2)" //
    );
  }

  public void test179() {
    check( //
        "Integrate[x^2*Sqrt[a+b*x], x]", //
        "(2*a^2*(a+b*x)^(3/2))/(3*b^3)-(4*a*(a+b*x)^(5/2))/(5*b^3)+(2*(a+b*x)^(7/2))/(7*b^3)" //
    );
  }

  public void test180() {
    check( //
        "Integrate[Sqrt[a+b*x]/x, x]", //
        "2*Sqrt[a+b*x]-2*Sqrt[a]*ArcTanh[Sqrt[a+b*x]/Sqrt[a]]" //
    );
  }

  public void test181() {
    check( //
        "Integrate[Sqrt[a+b*x]/x^2, x]", //
        "-Sqrt[a+b*x]/x-(b*ArcTanh[Sqrt[a+b*x]/Sqrt[a]])/Sqrt[a]" //
    );
  }

  public void test182() {
    check( //
        "Integrate[1/Sqrt[a+b*x], x]", //
        "(2*Sqrt[a+b*x])/b" //
    );
  }

  public void test183() {
    check( //
        "Integrate[x/Sqrt[a+b*x], x]", //
        "-(2*a*Sqrt[a+b*x])/b^2+(2*(a+b*x)^(3/2))/(3*b^2)" //
    );
  }

  public void test184() {
    check( //
        "Integrate[x^2/Sqrt[a+b*x], x]", //
        "(2*a^2*Sqrt[a+b*x])/b^3-(4*a*(a+b*x)^(3/2))/(3*b^3)+(2*(a+b*x)^(5/2))/(5*b^3)" //
    );
  }

  public void test185() {
    check( //
        "Integrate[1/(x*Sqrt[a+b*x]), x]", //
        "-(2*ArcTanh[Sqrt[a+b*x]/Sqrt[a]])/Sqrt[a]" //
    );
  }

  public void test186() {
    check( //
        "Integrate[1/(x^2*Sqrt[a+b*x]), x]", //
        "-Sqrt[a+b*x]/(a*x)+(b*ArcTanh[Sqrt[a+b*x]/Sqrt[a]])/a^(3/2)" //
    );
  }

  public void test187() {
    check( //
        "Integrate[(Sqrt[a+b*x])^p, x]", //
        "(2*(a+b*x)^(1/2*(2+p)))/(b*(2+p))" //
    );
  }

  public void test188() {
    check( //
        "Integrate[x*(Sqrt[a+b*x])^p, x]", //
        "-(2*a*(a+b*x)^(1/2*(2+p)))/(b^2*(2+p))+(2*(a+b*x)^(1/2*(4+p)))/(b^2*(4+p))" //
    );
  }

  public void test189() {
    check( //
        "Integrate[ArcTan[(-Sqrt[2]+2*x)/Sqrt[2]], x]", //
        "ArcTan[1-Sqrt[2]*x]/Sqrt[2]-x*ArcTan[1-Sqrt[2]*x]-Log[1-Sqrt[2]*x+x^2]/(2*Sqrt[2])" //
    );
  }

  public void test190() {
    check( //
        "Integrate[1/Sqrt[x^2+(-1)*1], x]", //
        "ArcTanh[x/Sqrt[-1+x^2]]" //
    );
  }

  public void test191() {
    check( //
        "Integrate[Sqrt[x+1]*Sqrt[x], x]", //
        "1/4*Sqrt[x]*Sqrt[1+x]+1/2*x^(3/2)*Sqrt[1+x]-ArcSinh[Sqrt[x]]/4" //
    );
  }

  public void test192() {
    check( //
        "Integrate[Sin[Sqrt[x]], x]", //
        "-2*Sqrt[x]*Cos[Sqrt[x]]+2*Sin[Sqrt[x]]" //
    );
  }

  public void test193() {
    check( //
        "Integrate[x/(Sqrt[1-x^2])^(9/4), x]", //
        "4/(1-x^2)^(1/8)" //
    );
  }

  public void test194() {
    check( //
        "Integrate[x/Sqrt[1-x^4], x]", //
        "ArcSin[x^2]/2" //
    );
  }

  public void test195() {
    check( //
        "Integrate[1/(x*Sqrt[1+x^4]), x]", //
        "-ArcTanh[Sqrt[1+x^4]]/2" //
    );
  }

  public void test196() {
    check( //
        "Integrate[x/Sqrt[1+x^2+x^4], x]", //
        "ArcSinh[(1+2*x^2)/Sqrt[3]]/2" //
    );
  }

  public void test197() {
    check( //
        "Integrate[1/(x*Sqrt[x^2+(-1)*1-x^4]), x]", //
        "-ArcTan[(2-x^2)/(2*Sqrt[-1+x^2-x^4])]/2" //
    );
  }

  public void test198() {
    check( //
        "Integrate[(1+x)/((1-x)^2*Sqrt[1+x^2]), x]", //
        "Sqrt[1+x^2]/(1-x)" //
    );
  }

  public void test199() {
    check( //
        "Integrate[1/Sqrt[1+x^2], x]", //
        "ArcSinh[x]" //
    );
  }

  public void test200() {
    check( //
        "Integrate[(Sqrt[x]*Sqrt[1+x]+Sqrt[x]*Sqrt[2+x]+Sqrt[1+x]*Sqrt[2+x])/(2*Sqrt[x]*Sqrt[1+x]*Sqrt[2+x]), x]", //
        "Sqrt[x]+Sqrt[1+x]+Sqrt[2+x]" //
    );
  }

  public void test201() {
    check( //
        "Integrate[(-2*Sqrt[1+x^3]+5*x^4*Sqrt[1+x^3]-3*x^2*Sqrt[1-2*x+x^5])/(2*Sqrt[1+x^3]*Sqrt[1-2*x+x^5]), x]", //
        "-Sqrt[1+x^3]+Sqrt[1-2*x+x^5]" //
    );
  }

  public void test202() {
    check( //
        "Integrate[1/Sqrt[x^2+(-1)*1]+10/Sqrt[x^2+(-1)*4], x]", //
        "10*ArcTanh[x/Sqrt[-4+x^2]]+ArcTanh[x/Sqrt[-1+x^2]]" //
    );
  }

  public void test203() {
    check( //
        "Integrate[Sqrt[x+Sqrt[x^2+a^2]]/x, x]", //
        "2*Sqrt[x+Sqrt[a^2+x^2]]-2*Sqrt[a]*ArcTan[Sqrt[x+Sqrt[a^2+x^2]]/Sqrt[a]]-2*Sqrt[a]*ArcTanh[Sqrt[x+Sqrt[a^2+x^2]]/Sqrt[a]]" //
    );
  }

  public void test204() {
    check( //
        "Integrate[(3*x^2)/(2*(1+x^3+Sqrt[1+x^3])), x]", //
        "Log[1+Sqrt[1+x^3]]" //
    );
  }

  public void test205() {
    check( //
        "Integrate[1/Sqrt[2*h*r^2-alpha^2], r]", //
        "ArcTanh[(Sqrt[2]*Sqrt[h]*r)/Sqrt[-alpha^2+2*h*r^2]]/(Sqrt[2]*Sqrt[h])" //
    );
  }

  public void test206() {
    check( //
        "Integrate[1/(r*Sqrt[2*h*r^2-alpha^2-epsilon^2]), r]", //
        "ArcTan[Sqrt[-alpha^2-epsilon^2+2*h*r^2]/Sqrt[alpha^2+epsilon^2]]/Sqrt[alpha^2+epsilon^2]" //
    );
  }

  public void test207() {
    check( //
        "Integrate[1/(r*Sqrt[2*h*r^2-alpha^2-2*k*r]), r]", //
        "-ArcTan[(alpha^2+k*r)/(alpha*Sqrt[-alpha^2-2*k*r+2*h*r^2])]/alpha" //
    );
  }

  public void test208() {
    check( //
        "Integrate[1/(r*Sqrt[2*h*r^2-alpha^2-epsilon^2-2*k*r]), r]", //
        "-ArcTan[(alpha^2+epsilon^2+k*r)/(Sqrt[alpha^2+epsilon^2]*Sqrt[-alpha^2-epsilon^2-2*k*r+2*h*r^2])]/Sqrt[alpha^2+epsilon^2]" //
    );
  }

  public void test209() {
    check( //
        "Integrate[r/Sqrt[2*e*r^2-alpha^2], r]", //
        "Sqrt[-alpha^2+2*e*r^2]/(2*e)" //
    );
  }

  public void test210() {
    check( //
        "Integrate[r/Sqrt[2*e*r^2-alpha^2-epsilon^2], r]", //
        "Sqrt[-alpha^2-epsilon^2+2*e*r^2]/(2*e)" //
    );
  }

  public void test211() {
    check( //
        "Integrate[r/Sqrt[2*e*r^2-alpha^2-2*k*r^4], r]", //
        "-ArcTan[(e-2*k*r^2)/(Sqrt[2]*Sqrt[k]*Sqrt[-alpha^2+2*e*r^2-2*k*r^4])]/(2*Sqrt[2]*Sqrt[k])" //
    );
  }

  public void test212() {
    check( //
        "Integrate[r/Sqrt[2*e*r^2-alpha^2-2*k*r], r]", //
        "Sqrt[-alpha^2-2*k*r+2*e*r^2]/(2*e)-(k*ArcTanh[(k-2*e*r)/(Sqrt[2]*Sqrt[e]*Sqrt[-alpha^2-2*k*r+2*e*r^2])])/(2*Sqrt[2]*e^(3/2))" //
    );
  }

  public void test213() {
    check( //
        "Integrate[1/(r*Sqrt[2*h*r^2-alpha^2-2*k*r^4]), r]", //
        "-ArcTan[(alpha^2-h*r^2)/(alpha*Sqrt[-alpha^2+2*h*r^2-2*k*r^4])]/(2*alpha)" //
    );
  }

  public void test214() {
    check( //
        "Integrate[1/(r*Sqrt[2*h*r^2-alpha^2-epsilon^2-2*k*r^4]), r]", //
        "-ArcTan[(alpha^2+epsilon^2-h*r^2)/(Sqrt[alpha^2+epsilon^2]*Sqrt[-alpha^2-epsilon^2+2*h*r^2-2*k*r^4])]/(2*Sqrt[alpha^2+epsilon^2])" //
    );
  }

  public void test215() {
    check( //
        "Integrate[a*Sin[3*x+5]^2*Cos[3*x+5], x]", //
        "1/9*a*Sin[5+3*x]^3" //
    );
  }

  public void test216() {
    check( //
        "Integrate[Log[x^2]/x^3, x]", //
        "-1/(2*x^2)-Log[x^2]/(2*x^2)" //
    );
  }

  public void test217() {
    check( //
        "Integrate[x*Sin[x+a], x]", //
        "-x*Cos[a+x]+Sin[a+x]" //
    );
  }

  public void test218() {
    check( //
        "Integrate[(Log[x]*(1-x)+(-1)*1)/(E^x*Log[x]^2), x]", //
        "x/(E^x*Log[x])" //
    );
  }

  public void test219() {
    check( //
        "Integrate[x^3/(a*x^2+b), x]", //
        "x^2/(2*a)-(b*Log[b+a*x^2])/(2*a^2)" //
    );
  }

  public void test220() {
    check( //
        "Integrate[Sqrt[x]/(x+1)^(7/2), x]", //
        "(2*x^(3/2))/(5*(1+x)^(5/2))+(4*x^(3/2))/(15*(1+x)^(3/2))" //
    );
  }

  public void test221() {
    check( //
        "Integrate[1/(x*(x+1)), x]", //
        "Log[x]-Log[1+x]" //
    );
  }

  public void test222() {
    check( //
        "Integrate[1/(Sqrt[x]*(2*x+(-1)*1)), x]", //
        "-Sqrt[2]*ArcTanh[Sqrt[2]*Sqrt[x]]" //
    );
  }

  public void test223() {
    check( //
        "Integrate[(x^2+1)*Sqrt[x], x]", //
        "1/3*2*x^(3/2)+1/7*2*x^(7/2)" //
    );
  }

  public void test224() {
    check( //
        "Integrate[(x-a)^(1/3)/x, x]", //
        "3*(-a+x)^(1/3)+Sqrt[3]*a^(1/3)*ArcTan[(a^(1/3)-2*(-a+x)^(1/3))/(Sqrt[3]*a^(1/3))]+1/2*a^(1/3)*Log[x]-3/2*a^(1/3)*Log[a^(1/3)+(-a+x)^(1/3)]" //
    );
  }

  public void test225() {
    check( //
        "Integrate[x*Sinh[x], x]", //
        "x*Cosh[x]-Sinh[x]" //
    );
  }

  public void test226() {
    check( //
        "Integrate[x*Cosh[x], x]", //
        "-Cosh[x]+x*Sinh[x]" //
    );
  }

  public void test227() {
    check( //
        "Integrate[Sinh[2*x]/Cosh[2*x], x]", //
        "Log[Cosh[2*x]]/2" //
    );
  }

  public void test228() {
    check( //
        "Integrate[(I*eps*Sinh[x]+(-1)*1)/(eps*I*Cosh[x]+I*a-x), x]", //
        "Log[a+I*x+eps*Cosh[x]]" //
    );
  }

  public void test229() {
    check( //
        "Integrate[Sin[2*x+3]*Cos[x]^2, x]", //
        "-Cos[3+2*x]/4-Cos[3+4*x]/16+1/4*x*Sin[3]" //
    );
  }

  public void test230() {
    check( //
        "Integrate[x*ArcTan[x], x]", //
        "-x/2+ArcTan[x]/2+1/2*x^2*ArcTan[x]" //
    );
  }

  public void test231() {
    check( //
        "Integrate[x*ArcCot[x], x]", //
        "x/2+1/2*x^2*ArcCot[x]-ArcTan[x]/2" //
    );
  }

  public void test232() {
    check( //
        "Integrate[x*Log[x^2+a], x]", //
        "-x^2/2+1/2*(a+x^2)*Log[a+x^2]" //
    );
  }

  public void test233() {
    check( //
        "Integrate[Sin[x+a]*Cos[x], x]", //
        "-Cos[a+2*x]/4+1/2*x*Sin[a]" //
    );
  }

  public void test234() {
    check( //
        "Integrate[Cos[x+a]*Sin[x], x]", //
        "-Cos[a+2*x]/4-1/2*x*Sin[a]" //
    );
  }

  public void test235() {
    check( //
        "Integrate[Sqrt[1+Sin[x]], x]", //
        "-(2*Cos[x])/Sqrt[1+Sin[x]]" //
    );
  }

  public void test236() {
    check( //
        "Integrate[Sqrt[1-Sin[x]], x]", //
        "(2*Cos[x])/Sqrt[1-Sin[x]]" //
    );
  }

  public void test237() {
    check( //
        "Integrate[Sqrt[1+Cos[x]], x]", //
        "(2*Sin[x])/Sqrt[1+Cos[x]]" //
    );
  }

  public void test238() {
    check( //
        "Integrate[Sqrt[1-Cos[x]], x]", //
        "-(2*Sin[x])/Sqrt[1-Cos[x]]" //
    );
  }

  public void test239() {
    check( //
        "Integrate[1/(Sqrt[x]-Sqrt[x+(-1)*1]), x]", //
        "2/3*(-1+x)^(3/2)+1/3*2*x^(3/2)" //
    );
  }

  public void test240() {
    check( //
        "Integrate[1/(1-Sqrt[x+1]), x]", //
        "-2*Sqrt[1+x]-2*Log[1-Sqrt[1+x]]" //
    );
  }

  public void test241() {
    check( //
        "Integrate[x/Sqrt[x^4+36], x]", //
        "ArcSinh[x^2/6]/2" //
    );
  }

  public void test242() {
    check( //
        "Integrate[1/(x^(1/3)+Sqrt[x]), x]", //
        "6*x^(1/6)-3*x^(1/3)+2*Sqrt[x]-6*Log[1+x^(1/6)]" //
    );
  }

  public void test243() {
    check( //
        "Integrate[Log[2+3*x^2], x]", //
        "-2*x+2*Sqrt[2/3]*ArcTan[Sqrt[3/2]*x]+x*Log[2+3*x^2]" //
    );
  }

  public void test244() {
    check( //
        "Integrate[Cot[x], x]", //
        "Log[Sin[x]]" //
    );
  }

  public void test245() {
    check( //
        "Integrate[Cot[x]^4, x]", //
        "x+Cot[x]-Cot[x]^3/3" //
    );
  }

  public void test246() {
    check( //
        "Integrate[Tanh[x], x]", //
        "Log[Cosh[x]]" //
    );
  }

  public void test247() {
    check( //
        "Integrate[Coth[x], x]", //
        "Log[Sinh[x]]" //
    );
  }

  public void test248() {
    check( //
        "Integrate[b^x, x]", //
        "b^x/Log[b]" //
    );
  }

  public void test249() {
    check( //
        "Integrate[Sqrt[x^4+1/x^4+2], x]", //
        "-(x*Sqrt[2+1/x^4+x^4])/(1+x^4)+(x^5*Sqrt[2+1/x^4+x^4])/(3*(1+x^4))" //
    );
  }

  public void test250() {
    check( //
        "Integrate[(2*x+1)/(3*x+2), x]", //
        "1/3*2*x-Log[2+3*x]/9" //
    );
  }

  public void test251() {
    check( //
        "Integrate[x*Log[x+Sqrt[x^2+1]], x]", //
        "-1/4*x*Sqrt[1+x^2]+ArcSinh[x]/4+1/2*x^2*Log[x+Sqrt[1+x^2]]" //
    );
  }

  public void test252() {
    check( //
        "Integrate[x*(E^x*Sin[x]+1)^2, x]", //
        "-1/32*3*E^(2*x)+1/8*E^(2*x)*x+x^2/2+E^x*Cos[x]-E^x*x*Cos[x]-1/32*E^(2*x)*Cos[2*x]+E^x*x*Sin[x]+1/16*E^(2*x)*Cos[x]*Sin[x]-1/4*E^(2*x)*x*Cos[x]*Sin[x]-1/16*E^(2*x)*Sin[x]^2+1/4*E^(2*x)*x*Sin[x]^2+1/32*E^(2*x)*Sin[2*x]" //
    );
  }

  public void test253() {
    check( //
        "Integrate[x*E^x*Cos[x], x]", //
        "1/2*E^x*x*Cos[x]-1/2*E^x*Sin[x]+1/2*E^x*x*Sin[x]" //
    );
  }

  public void test254() {
    check( //
        "Integrate[1/(x+(-1)*3)^4, x]", //
        "1/(3*(3-x)^3)" //
    );
  }

  public void test255() {
    check( //
        "Integrate[x/(x^3+(-1)*1), x]", //
        "ArcTan[(1+2*x)/Sqrt[3]]/Sqrt[3]+Log[1-x]/3-Log[1+x+x^2]/6" //
    );
  }

  public void test256() {
    check( //
        "Integrate[x/(x^4+(-1)*1), x]", //
        "-ArcTanh[x^2]/2" //
    );
  }

  public void test257() {
    check( //
        "Integrate[Log[x]*(x^3+1)/(x^4+2), x]", //
        "1/8*(2+I*(-2)^(1/4))*Log[x]*Log[1-((1+I)*x)/2^(3/4)]+1/16*(4+(1+(-1)*I)*2^(3/4))*Log[x]*Log[1+((1+I)*x)/2^(3/4)]+1/8*(2+(-2)^(1/4))*Log[x]*Log[1-((-1)^(3/4)*x)/2^(1/4)]+1/8*(2-(-2)^(1/4))*Log[x]*Log[1+((-1)^(3/4)*x)/2^(1/4)]+1/16*(4+(1+(-1)*I)*2^(3/4))*PolyLog[2,-((1+I)*x)/2^(3/4)]+1/8*(2+I*(-2)^(1/4))*PolyLog[2,((1+I)*x)/2^(3/4)]+1/8*(2-(-2)^(1/4))*PolyLog[2,-((-1)^(3/4)*x)/2^(1/4)]+1/8*(2+(-2)^(1/4))*PolyLog[2,((-1)^(3/4)*x)/2^(1/4)]" //
    );
  }

  public void test258() {
    check( //
        "Integrate[Log[x]+Log[x+1]+Log[x+2], x]", //
        "-3*x+x*Log[x]+(1+x)*Log[1+x]+(2+x)*Log[2+x]" //
    );
  }

  public void test259() {
    check( //
        "Integrate[1/(x^3+5), x]", //
        "-ArcTan[(5^(1/3)-2*x)/(Sqrt[3]*5^(1/3))]/(Sqrt[3]*5^(2/3))+Log[5^(1/3)+x]/(3*5^(2/3))-Log[5^(2/3)-5^(1/3)*x+x^2]/(6*5^(2/3))" //
    );
  }

  public void test260() {
    check( //
        "Integrate[1/Sqrt[1+x^2], x]", //
        "ArcSinh[x]" //
    );
  }

  public void test261() {
    check( //
        "Integrate[Sqrt[x^2+3], x]", //
        "1/2*x*Sqrt[3+x^2]+3/2*ArcSinh[x/Sqrt[3]]" //
    );
  }

  public void test262() {
    check( //
        "Integrate[x/(x+1)^2, x]", //
        "1/(1+x)+Log[1+x]" //
    );
  }

  public void test263() {
    check( //
        "Integrate[ArcSin[x], x]", //
        "Sqrt[1-x^2]+x*ArcSin[x]" //
    );
  }

  public void test264() {
    check( //
        "Integrate[x^2*ArcSin[x], x]", //
        "Sqrt[1-x^2]/3-(1-x^2)^(3/2)/9+1/3*x^3*ArcSin[x]" //
    );
  }

  public void test265() {
    check( //
        "Integrate[Sec[x]^2/(1+Sec[x]^2-3*Tan[x]), x]", //
        "-Log[Cos[x]-Sin[x]]+Log[2*Cos[x]-Sin[x]]" //
    );
  }

  public void test266() {
    check( //
        "Integrate[1/Sec[x]^2, x]", //
        "x/2+1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test267() {
    check( //
        "Integrate[(5*x^2-3*x+(-1)*2)/(x^2*(x+(-1)*2)), x]", //
        "-1/x+3*Log[2-x]+2*Log[x]" //
    );
  }

  public void test268() {
    check( //
        "Integrate[1/Sqrt[4*x^2+9], x]", //
        "ArcSinh[1/3*2*x]/2" //
    );
  }

  public void test269() {
    check( //
        "Integrate[1/Sqrt[x^2+4], x]", //
        "ArcSinh[x/2]" //
    );
  }

  public void test270() {
    check( //
        "Integrate[1/(9*x^2-12*x+10), x]", //
        "-ArcTan[(2-3*x)/Sqrt[6]]/(3*Sqrt[6])" //
    );
  }

  public void test271() {
    check( //
        "Integrate[1/(x^8-2*x^7+2*x^6-2*x^5+x^4), x]", //
        "1/(2*(1-x))-1/(3*x^3)-1/x^2-2/x-5/2*Log[1-x]+2*Log[x]+Log[1+x^2]/4" //
    );
  }

  public void test272() {
    check( //
        "Integrate[(a*x^3+b*x^2+c*x+d)/((x+1)*x*(x+(-1)*3)), x]", //
        "a*x+1/12*(27*a+9*b+3*c+d)*Log[3-x]-1/3*d*Log[x]-1/4*(a-b+c-d)*Log[1+x]" //
    );
  }

  public void test273() {
    check( //
        "Integrate[1/(2-Log[x^2+1])^5, x]", //
        "Unintegrable[1/(2-Log[1+x^2])^5,x]", //
        "Integrate(1/(2-Log(1+x^2))^5,x)", //
        new int[] {-1});
  }

  public void test274() {
    check( //
        "Integrate[2*x*E^x^2*Log[x]+E^x^2/x+(Log[x]+(-1)*2)/(Log[x]^2+x)^2+(2/x*Log[x]+1/x+1)/(Log[x]^2+x), x]", //
        "E^x^2*Log[x]-Log[x]/(x+Log[x]^2)+Log[x+Log[x]^2]" //
    );
  }

  public void test275() {
    check( //
        "Integrate[E^(x*z+x/2)*Sin[Pi*z]^4*x^4, z]", //
        "(24*E^(x/2+x*z)*Pi^4*x^3)/(64*Pi^4+20*Pi^2*x^2+x^4)-(24*E^(x/2+x*z)*Pi^3*x^4*Cos[Pi*z]*Sin[Pi*z])/(64*Pi^4+20*Pi^2*x^2+x^4)+(12*E^(x/2+x*z)*Pi^2*x^5*Sin[Pi*z]^2)/(64*Pi^4+20*Pi^2*x^2+x^4)-(4*E^(x/2+x*z)*Pi*x^4*Cos[Pi*z]*Sin[Pi*z]^3)/(16*Pi^2+x^2)+(E^(x/2+x*z)*x^5*Sin[Pi*z]^4)/(16*Pi^2+x^2)" //
    );
  }

  public void test276() {
    check( //
        "Integrate[Erf[x], x]", //
        "1/(E^x^2*Sqrt[Pi])+x*Erf[x]" //
    );
  }

  public void test277() {
    check( //
        "Integrate[Erf[x+a], x]", //
        "1/(E^(a+x)^2*Sqrt[Pi])+(a+x)*Erf[a+x]" //
    );
  }

  public void test278() {
    check( //
        "Integrate[(2*x^6+4*x^5+7*x^4-3*x^3-x*x-8*x+(-1)*8)/((2*x^2+(-1)*1)^2*Sqrt[x^4+4*x^3+2*x^2+1]), x]", //
        "((1+2*x)*Sqrt[1+2*x^2+4*x^3+x^4])/(2*(-1+2*x^2))-ArcTanh[(x*(2+x)*(7-x+27*x^2+33*x^3))/((2+37*x^2+31*x^3)*Sqrt[1+2*x^2+4*x^3+x^4])]" //
    );
  }

  public void test279() {
    check( //
        "Integrate[(1+2*y)*Sqrt[1-5*y-5*y^2]/(y*(1+y)*(2+y)*Sqrt[1-y-y^2]), y]", //
        "-ArcTanh[((1-3*y)*Sqrt[1-5*y-5*y^2])/((1-5*y)*Sqrt[1-y-y^2])]/4-ArcTanh[((4+3*y)*Sqrt[1-5*y-5*y^2])/((6+5*y)*Sqrt[1-y-y^2])]/2+9/4*ArcTanh[((11+7*y)*Sqrt[1-5*y-5*y^2])/(3*(7+5*y)*Sqrt[1-y-y^2])]" //
    );
  }

  public void test280() {
    check( //
        "Integrate[x*(Sqrt[x^2+(-1)*1]*x^2-4*Sqrt[x^2+(-1)*1]+Sqrt[x^2+(-1)*4]*x^2-Sqrt[x^2+(-1)*4])/((1+Sqrt[x^2+(-1)*4]+Sqrt[x^2+(-1)*1])*(x^4-5*x^2+4)), x]", //
        "Log[1+Sqrt[-4+x^2]+Sqrt[-1+x^2]]" //
    );
  }

  public void test281() {
    check( //
        "Integrate[Sqrt[-4*Sqrt[2]+9]*x-Sqrt[x^4+2*x^2+4*x+1]*Sqrt[2], x]", //
        "1/2*Sqrt[9-4*Sqrt[2]]*x^2-Sqrt[2]*(-Sqrt[1+4*x+2*x^2+x^4]/3+1/3*(1+x)*Sqrt[1+4*x+2*x^2+x^4]+(4*I*(-13+3*Sqrt[33])^(1/3)*Sqrt[1+4*x+2*x^2+x^4])/(4*2^(2/3)*((-1)*I+Sqrt[3])-2*I*(-13+3*Sqrt[33])^(1/3)+2^(1/3)*(I+Sqrt[3])*(-13+3*Sqrt[33])^(2/3)+6*I*(-13+3*Sqrt[33])^(1/3)*x)-(8*2^(2/3)*Sqrt[3/(-13+3*Sqrt[33]+4*(-26+6*Sqrt[33])^(1/3))]*Sqrt[(I*(-19899+3445*Sqrt[33]+(-26+6*Sqrt[33])^(2/3)*(-2574+466*Sqrt[33])+(-26+6*Sqrt[33])^(1/3)*(-19899+3445*Sqrt[33])+(59697-10335*Sqrt[33])*x))/((-39-13*I*Sqrt[3]+9*I*Sqrt[11]+9*Sqrt[33]+4*I*(3*I+Sqrt[3])*(-26+6*Sqrt[33])^(1/3))*(26-6*Sqrt[33]+(-13+13*I*Sqrt[3]-9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+(-4-4*I*Sqrt[3])*(-26+6*Sqrt[33])^(2/3)+6*(-13+3*Sqrt[33])*x))]*Sqrt[1+4*x+2*x^2+x^4]*EllipticE[ArcSin[Sqrt[26-6*Sqrt[33]+(-13-13*I*Sqrt[3]+9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+4*I*(I+Sqrt[3])*(-26+6*Sqrt[33])^(2/3)+6*(-13+3*Sqrt[33])*x]/(Sqrt[(39+13*I*Sqrt[3]-9*I*Sqrt[11]-9*Sqrt[33]+4*(3-I*Sqrt[3])*(-26+6*Sqrt[33])^(1/3))/(39-13*I*Sqrt[3]+9*I*Sqrt[11]-9*Sqrt[33]+4*(3+I*Sqrt[3])*(-26+6*Sqrt[33])^(1/3))]*Sqrt[26-6*Sqrt[33]+(-13+13*I*Sqrt[3]-9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+(-4-4*I*Sqrt[3])*(-26+6*Sqrt[33])^(2/3)+6*(-13+3*Sqrt[33])*x])],(4*(21+7*I*Sqrt[3]-3*I*Sqrt[11]-3*Sqrt[33])+(3-I*Sqrt[3]-3*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3))/(4*(21-7*I*Sqrt[3]+3*I*Sqrt[11]-3*Sqrt[33])+(3+I*Sqrt[3]+3*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3))])/((4*2^(2/3)-(-13+3*Sqrt[33])^(1/3)-2^(1/3)*(-13+3*Sqrt[33])^(2/3)+3*(-13+3*Sqrt[33])^(1/3)*x)*Sqrt[(I*(1+x))/((104-24*Sqrt[33]+(-13-13*I*Sqrt[3]+9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+4*I*(I+Sqrt[3])*(-26+6*Sqrt[33])^(2/3))*(26-6*Sqrt[33]+(-13+13*I*Sqrt[3]-9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+(-4-4*I*Sqrt[3])*(-26+6*Sqrt[33])^(2/3)+6*(-13+3*Sqrt[33])*x))]*Sqrt[26-6*Sqrt[33]+(-13+13*I*Sqrt[3]-9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+(-4-4*I*Sqrt[3])*(-26+6*Sqrt[33])^(2/3)+6*(-13+3*Sqrt[33])*x]*Sqrt[26-6*Sqrt[33]+(-13-13*I*Sqrt[3]+9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+4*I*(I+Sqrt[3])*(-26+6*Sqrt[33])^(2/3)+6*(-13+3*Sqrt[33])*x])+((2^(1/3)*(13-13*I*Sqrt[3]+9*I*Sqrt[11]-3*Sqrt[33])+4*2^(2/3)*(1+I*Sqrt[3])*(-13+3*Sqrt[33])^(1/3)+20*(-13+3*Sqrt[33])^(2/3))*(4*2^(2/3)*(I+Sqrt[3])+8*I*(-13+3*Sqrt[33])^(1/3)+2^(1/3)*((-1)*I+Sqrt[3])*(-13+3*Sqrt[33])^(2/3))*Sqrt[(52-12*Sqrt[33]-2^(1/3)*(-13+3*Sqrt[33])^(4/3)+4*(-26+6*Sqrt[33])^(2/3))/(-13+3*Sqrt[33]+4*(-26+6*Sqrt[33])^(1/3))]*Sqrt[((-8)*I*(-13+3*Sqrt[33])+((-43)*I-13*Sqrt[3]+9*Sqrt[11]+5*I*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+(2*I+4*Sqrt[3]-2*I*Sqrt[33])*(-26+6*Sqrt[33])^(2/3)+(8*I*(-13+3*Sqrt[33])+(13*I-13*Sqrt[3]+9*Sqrt[11]-3*I*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+4*(I+Sqrt[3])*(-26+6*Sqrt[33])^(2/3))*x)/(1+x)]*Sqrt[1+4*x+2*x^2+x^4]*EllipticF[ArcSin[(Sqrt[52-12*Sqrt[33]-2^(1/3)*(-13+3*Sqrt[33])^(4/3)+4*(-26+6*Sqrt[33])^(2/3)]*Sqrt[26-6*Sqrt[33]+(-13-13*I*Sqrt[3]+9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+4*I*(I+Sqrt[3])*(-26+6*Sqrt[33])^(2/3)+6*(-13+3*Sqrt[33])*x])/(2^(1/6)*Sqrt[3]*(-13+3*Sqrt[33])^(2/3)*Sqrt[39+13*I*Sqrt[3]-9*I*Sqrt[11]-9*Sqrt[33]+4*(3-I*Sqrt[3])*(-26+6*Sqrt[33])^(1/3)]*Sqrt[1+x])],(4*(21*I-7*Sqrt[3]+3*Sqrt[11]-3*I*Sqrt[33])+(3*I+Sqrt[3]+3*Sqrt[11]+3*I*Sqrt[33])*(-26+6*Sqrt[33])^(1/3))/(-56*Sqrt[3]+24*Sqrt[11]+2*(Sqrt[3]+3*Sqrt[11])*(-26+6*Sqrt[33])^(1/3))])/(3*2^(2/3)*3^(3/4)*(-13+3*Sqrt[33])^(1/3)*Sqrt[39+13*I*Sqrt[3]-9*I*Sqrt[11]-9*Sqrt[33]+4*(3-I*Sqrt[3])*(-26+6*Sqrt[33])^(1/3)]*Sqrt[1+x]*(4*2^(2/3)*((-1)*I+Sqrt[3])-2*I*(-13+3*Sqrt[33])^(1/3)+2^(1/3)*(I+Sqrt[3])*(-13+3*Sqrt[33])^(2/3)+6*I*(-13+3*Sqrt[33])^(1/3)*x)*Sqrt[26-6*Sqrt[33]+(-13-13*I*Sqrt[3]+9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+4*I*(I+Sqrt[3])*(-26+6*Sqrt[33])^(2/3)+6*(-13+3*Sqrt[33])*x]*Sqrt[(8*(-13+3*Sqrt[33])-(5-3*I*Sqrt[3]+3*I*Sqrt[11]+Sqrt[33])*(-26+6*Sqrt[33])^(2/3)+(-26+6*Sqrt[33])^(1/3)*(-41+15*I*Sqrt[3]-3*I*Sqrt[11]+7*Sqrt[33])+(104-24*Sqrt[33]+(-13-13*I*Sqrt[3]+9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+4*I*(I+Sqrt[3])*(-26+6*Sqrt[33])^(2/3))*x)/((-39-13*I*Sqrt[3]+9*I*Sqrt[11]+9*Sqrt[33]+4*I*(3*I+Sqrt[3])*(-26+6*Sqrt[33])^(1/3))*(1+x))])+((4*2^(2/3)+2*(-13+3*Sqrt[33])^(1/3)-2^(1/3)*(-13+3*Sqrt[33])^(2/3))*(4*2^(2/3)*(I+Sqrt[3])-4*I*(-13+3*Sqrt[33])^(1/3)+2^(1/3)*((-1)*I+Sqrt[3])*(-13+3*Sqrt[33])^(2/3))*(4*2^(2/3)*((-1)*I+Sqrt[3])+4*I*(-13+3*Sqrt[33])^(1/3)+2^(1/3)*(I+Sqrt[3])*(-13+3*Sqrt[33])^(2/3))*Sqrt[(-39+13*I*Sqrt[3]-9*I*Sqrt[11]+9*Sqrt[33]-4*I*((-3)*I+Sqrt[3])*(-26+6*Sqrt[33])^(1/3))/(104-24*Sqrt[33]+(-13+13*I*Sqrt[3]-9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+(-4-4*I*Sqrt[3])*(-26+6*Sqrt[33])^(2/3))]*Sqrt[1+x]*Sqrt[(104-24*Sqrt[33]+2*(1+14*I*Sqrt[3]-6*I*Sqrt[11]+Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+(-7-I*Sqrt[3]-3*I*Sqrt[11]+Sqrt[33])*(-26+6*Sqrt[33])^(2/3)+2*(-52+12*Sqrt[33]+2^(1/3)*(-13+3*Sqrt[33])^(4/3)-4*(-26+6*Sqrt[33])^(2/3))*x)/((-39+13*I*Sqrt[3]-9*I*Sqrt[11]+9*Sqrt[33]-4*I*((-3)*I+Sqrt[3])*(-26+6*Sqrt[33])^(1/3))*(1+x))]*Sqrt[(104-24*Sqrt[33]+2*(1-14*I*Sqrt[3]+6*I*Sqrt[11]+Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+(-7+I*Sqrt[3]+3*I*Sqrt[11]+Sqrt[33])*(-26+6*Sqrt[33])^(2/3)+2*(-52+12*Sqrt[33]+2^(1/3)*(-13+3*Sqrt[33])^(4/3)-4*(-26+6*Sqrt[33])^(2/3))*x)/((-39-13*I*Sqrt[3]+9*I*Sqrt[11]+9*Sqrt[33]+4*I*(3*I+Sqrt[3])*(-26+6*Sqrt[33])^(1/3))*(1+x))]*Sqrt[1+4*x+2*x^2+x^4]*EllipticPi[(2^(1/3)*(4*2^(1/3)*((-3)*I+Sqrt[3])+(3*I+Sqrt[3])*(-13+3*Sqrt[33])^(2/3)))/(4*2^(2/3)*((-1)*I+Sqrt[3])-8*I*(-13+3*Sqrt[33])^(1/3)+2^(1/3)*(I+Sqrt[3])*(-13+3*Sqrt[33])^(2/3)),ArcSin[Sqrt[13-3*Sqrt[33]-2^(1/3)*(-13+3*Sqrt[33])^(4/3)+4*(-26+6*Sqrt[33])^(2/3)+(-39+9*Sqrt[33])*x]/(2^(1/6)*Sqrt[3]*(-13+3*Sqrt[33])^(2/3)*Sqrt[(-39+13*I*Sqrt[3]-9*I*Sqrt[11]+9*Sqrt[33]-4*I*((-3)*I+Sqrt[3])*(-26+6*Sqrt[33])^(1/3))/(104-24*Sqrt[33]+(-13+13*I*Sqrt[3]-9*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3)+(-4-4*I*Sqrt[3])*(-26+6*Sqrt[33])^(2/3))]*Sqrt[1+x])],(4*(21-7*I*Sqrt[3]+3*I*Sqrt[11]-3*Sqrt[33])+(3+I*Sqrt[3]+3*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3))/(4*(21+7*I*Sqrt[3]-3*I*Sqrt[11]-3*Sqrt[33])+(3-I*Sqrt[3]-3*I*Sqrt[11]+3*Sqrt[33])*(-26+6*Sqrt[33])^(1/3))])/(2^(1/6)*Sqrt[3]*(4*2^(2/3)*(I+Sqrt[3])+2*I*(-13+3*Sqrt[33])^(1/3)+2^(1/3)*((-1)*I+Sqrt[3])*(-13+3*Sqrt[33])^(2/3)-6*I*(-13+3*Sqrt[33])^(1/3)*x)*(4*2^(2/3)*((-1)*I+Sqrt[3])-2*I*(-13+3*Sqrt[33])^(1/3)+2^(1/3)*(I+Sqrt[3])*(-13+3*Sqrt[33])^(2/3)+6*I*(-13+3*Sqrt[33])^(1/3)*x)*Sqrt[13-3*Sqrt[33]-2^(1/3)*(-13+3*Sqrt[33])^(4/3)+4*(-26+6*Sqrt[33])^(2/3)+(-39+9*Sqrt[33])*x]))" //
    );
  }

  public void test282() {
    check( //
        "Integrate[(12*Log[x/mc^2]*x^2*Pi^2*mc^3*(-8*x-12*mc^2+3*mc)+Pi^2*(12*x^4*mc+3*x^4+176*x^3*mc^3-24*x^3*mc^2-144*x^2*mc^5-48*x*mc^7+24*x*mc^6+4*mc^9-3*mc^8))/(384*E^(x/y)*x^2), x]", //
        "((3-4*mc)*mc^8*Pi^2)/(E^(x/y)*384*x)+(3/8*mc^5*Pi^2*y)/E^(x/y)+(1/48*(3-22*mc)*mc^2*Pi^2*x*y)/E^(x/y)-(1/128*(1+4*mc)*Pi^2*x^2*y)/E^(x/y)+(1/48*(3-22*mc)*mc^2*Pi^2*y^2)/E^(x/y)+(1/4*mc^3*Pi^2*y^2)/E^(x/y)-(1/64*(1+4*mc)*Pi^2*x*y^2)/E^(x/y)-(1/64*(1+4*mc)*Pi^2*y^3)/E^(x/y)+1/16*(1-2*mc)*mc^6*Pi^2*ExpIntegralEi[-x/y]+((3-4*mc)*mc^8*Pi^2*ExpIntegralEi[-x/y])/(384*y)+1/32*mc^3*Pi^2*(3*mc-12*mc^2-8*y)*y*ExpIntegralEi[-x/y]-(1/32*mc^3*Pi^2*(3*(1-4*mc)*mc-8*x)*y*Log[x/mc^2])/E^(x/y)+(1/4*mc^3*Pi^2*y^2*Log[x/mc^2])/E^(x/y)" //
    );
  }

  public void test283() {
    check( //
        "Integrate[Sin[2*x]/Cos[x], x]", //
        "-2*Cos[x]" //
    );
  }

  public void test284() {
    check( //
        "Integrate[(7*x^13+10*x^8+4*x^7-7*x^6-4*x^3-4*x^2+3*x+3)/(x^14-2*x^8-2*x^7-2*x^4-4*x^3-x^2+2*x+1), x]", //
        "1/2*((1+Sqrt[2])*Log[1+x+Sqrt[2]*x+Sqrt[2]*x^2-x^7]-(-1+Sqrt[2])*Log[-1+(-1+Sqrt[2])*x+Sqrt[2]*x^2+x^7])" //
    );
  }
}
