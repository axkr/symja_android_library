package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

public class StewartProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public StewartProblemsTests(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 60;
      if (init) {
        System.out.println("Stewart");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void test1() {
    check( //
        "Integrate[x^n, x]", //
        "x^(1+n)/(1+n)" //
    );
  }

  public void test2() {
    check( //
        "Integrate[E^x, x]", //
        "E^x" //
    );
  }

  public void test3() {
    check( //
        "Integrate[1/x, x]", //
        "Log[x]" //
    );
  }

  public void test4() {
    check( //
        "Integrate[a^x, x]", //
        "a^x/Log[a]" //
    );
  }

  public void test5() {
    check( //
        "Integrate[Sin[x], x]", //
        "-Cos[x]" //
    );
  }

  public void test6() {
    check( //
        "Integrate[Cos[x], x]", //
        "Sin[x]" //
    );
  }

  public void test7() {
    check( //
        "Integrate[Sec[x]^2, x]", //
        "Tan[x]" //
    );
  }

  public void test8() {
    check( //
        "Integrate[Csc[x]^2, x]", //
        "-Cot[x]" //
    );
  }

  public void test9() {
    check( //
        "Integrate[Sec[x]*Tan[x], x]", //
        "Sec[x]" //
    );
  }

  public void test10() {
    check( //
        "Integrate[Csc[x]*Cot[x], x]", //
        "-Csc[x]" //
    );
  }

  public void test11() {
    check( //
        "Integrate[Sinh[x], x]", //
        "Cosh[x]" //
    );
  }

  public void test12() {
    check( //
        "Integrate[Cosh[x], x]", //
        "Sinh[x]" //
    );
  }

  public void test13() {
    check( //
        "Integrate[Tan[x], x]", //
        "-Log[Cos[x]]" //
    );
  }

  public void test14() {
    check( //
        "Integrate[Cot[x], x]", //
        "Log[Sin[x]]" //
    );
  }

  public void test15() {
    check( //
        "Integrate[x*Sin[x], x]", //
        "-x*Cos[x]+Sin[x]" //
    );
  }

  public void test16() {
    check( //
        "Integrate[Log[x], x]", //
        "-x+x*Log[x]" //
    );
  }

  public void test17() {
    check( //
        "Integrate[x^2*E^x, x]", //
        "2*E^x-2*E^x*x+E^x*x^2" //
    );
  }

  public void test18() {
    check( //
        "Integrate[E^x*Sin[x], x]", //
        "-1/2*E^x*Cos[x]+1/2*E^x*Sin[x]" //
    );
  }

  public void test19() {
    check( //
        "Integrate[ArcTan[x], x]", //
        "x*ArcTan[x]-Log[1+x^2]/2" //
    );
  }

  public void test20() {
    check( //
        "Integrate[x*E^(2*x), x]", //
        "-E^(2*x)/4+1/2*E^(2*x)*x" //
    );
  }

  public void test21() {
    check( //
        "Integrate[x*Cos[x], x]", //
        "Cos[x]+x*Sin[x]" //
    );
  }

  public void test22() {
    check( //
        "Integrate[x*Sin[4*x], x]", //
        "-x*Cos[4*x]/4+Sin[4*x]/16" //
    );
  }

  public void test23() {
    check( //
        "Integrate[x*Log[x], x]", //
        "-x^2/4+1/2*x^2*Log[x]" //
    );
  }

  public void test24() {
    check( //
        "Integrate[x^2*Cos[3*x], x]", //
        "1/9*2*x*Cos[3*x]-1/27*2*Sin[3*x]+1/3*x^2*Sin[3*x]" //
    );
  }

  public void test25() {
    check( //
        "Integrate[x^2*Sin[2*x], x]", //
        "Cos[2*x]/4-1/2*x^2*Cos[2*x]+1/2*x*Sin[2*x]" //
    );
  }

  public void test26() {
    check( //
        "Integrate[Log[x]^2, x]", //
        "2*x-2*x*Log[x]+x*Log[x]^2" //
    );
  }

  public void test27() {
    check( //
        "Integrate[ArcSin[x], x]", //
        "Sqrt[1-x^2]+x*ArcSin[x]" //
    );
  }

  public void test28() {
    check( //
        "Integrate[t*Cos[t]*Sin[t], t]", //
        "-t/4+1/4*Cos[t]*Sin[t]+1/2*t*Sin[t]^2" //
    );
  }

  public void test29() {
    check( //
        "Integrate[t*Sec[t]^2, t]", //
        "Log[Cos[t]]+t*Tan[t]" //
    );
  }

  public void test30() {
    check( //
        "Integrate[t^2*Log[t], t]", //
        "-t^3/9+1/3*t^3*Log[t]" //
    );
  }

  public void test31() {
    check( //
        "Integrate[t^3*E^t, t]", //
        "-6*E^t+6*E^t*t-3*E^t*t^2+E^t*t^3" //
    );
  }

  public void test32() {
    check( //
        "Integrate[E^(2*t)*Sin[3*t], t]", //
        "1/13*-3*E^(2*t)*Cos[3*t]+1/13*2*E^(2*t)*Sin[3*t]" //
    );
  }

  public void test33() {
    check( //
        "Integrate[Cos[3*t]/E^t, t]", //
        "-Cos[3*t]/(10*E^t)+(3*Sin[3*t])/(10*E^t)" //
    );
  }

  public void test34() {
    check( //
        "Integrate[y*Sinh[y], y]", //
        "y*Cosh[y]-Sinh[y]" //
    );
  }

  public void test35() {
    check( //
        "Integrate[y*Cosh[a*y], y]", //
        "-Cosh[a*y]/a^2+(y*Sinh[a*y])/a" //
    );
  }

  public void test36() {
    check( //
        "Integrate[t/E^t, t]", //
        "-1/E^t-t/E^t" //
    );
  }

  public void test37() {
    check( //
        "Integrate[Sqrt[t]*Log[t], t]", //
        "1/9*-4*t^(3/2)+1/3*2*t^(3/2)*Log[t]" //
    );
  }

  public void test38() {
    check( //
        "Integrate[x*Cos[2*x], x]", //
        "Cos[2*x]/4+1/2*x*Sin[2*x]" //
    );
  }

  public void test39() {
    check( //
        "Integrate[x^2/E^x, x]", //
        "-2/E^x-(2*x)/E^x-x^2/E^x" //
    );
  }

  public void test40() {
    check( //
        "Integrate[ArcCos[x], x]", //
        "-Sqrt[1-x^2]+x*ArcCos[x]" //
    );
  }

  public void test41() {
    check( //
        "Integrate[x*Csc[x]^2, x]", //
        "-x*Cot[x]+Log[Sin[x]]" //
    );
  }

  public void test42() {
    check( //
        "Integrate[Sin[3*x]*Cos[5*x], x]", //
        "Cos[2*x]/4-Cos[8*x]/16" //
    );
  }

  public void test43() {
    check( //
        "Integrate[Sin[2*x]*Sin[4*x], x]", //
        "Sin[2*x]/4-Sin[6*x]/12" //
    );
  }

  public void test44() {
    check( //
        "Integrate[Cos[x]*Log[Sin[x]], x]", //
        "-Sin[x]+Log[Sin[x]]*Sin[x]" //
    );
  }

  public void test45() {
    check( //
        "Integrate[x^3*E^x^2, x]", //
        "-E^x^2/2+1/2*E^x^2*x^2" //
    );
  }

  public void test46() {
    check( //
        "Integrate[(3+2*x)*E^x, x]", //
        "-2*E^x+E^x*(3+2*x)" //
    );
  }

  public void test47() {
    check( //
        "Integrate[x*5^x, x]", //
        "-5^x/Log[5]^2+(5^x*x)/Log[5]" //
    );
  }

  public void test48() {
    check( //
        "Integrate[Cos[Log[x]], x]", //
        "1/2*x*Cos[Log[x]]+1/2*x*Sin[Log[x]]" //
    );
  }

  public void test49() {
    check( //
        "Integrate[E^Sqrt[x], x]", //
        "-2*E^Sqrt[x]+2*E^Sqrt[x]*Sqrt[x]" //
    );
  }

  public void test50() {
    check( //
        "Integrate[Log[Sqrt[x]], x]", //
        "-x/2+x*Log[Sqrt[x]]" //
    );
  }

  public void test51() {
    check( //
        "Integrate[Sin[Log[x]], x]", //
        "-x*Cos[Log[x]]/2+1/2*x*Sin[Log[x]]" //
    );
  }

  public void test52() {
    check( //
        "Integrate[Sin[Sqrt[x]], x]", //
        "-2*Sqrt[x]*Cos[Sqrt[x]]+2*Sin[Sqrt[x]]" //
    );
  }

  public void test53() {
    check( //
        "Integrate[x^5*Cos[x^3], x]", //
        "Cos[x^3]/3+1/3*x^3*Sin[x^3]" //
    );
  }

  public void test54() {
    check( //
        "Integrate[x^5*E^x^2, x]", //
        "E^x^2-E^x^2*x^2+1/2*E^x^2*x^4" //
    );
  }

  public void test55() {
    check( //
        "Integrate[x*ArcTan[x], x]", //
        "-x/2+ArcTan[x]/2+1/2*x^2*ArcTan[x]" //
    );
  }

  public void test56() {
    check( //
        "Integrate[x*Cos[Pi*x], x]", //
        "Cos[Pi*x]/Pi^2+(x*Sin[Pi*x])/Pi" //
    );
  }

  public void test57() {
    check( //
        "Integrate[Sqrt[x]*Log[x], x]", //
        "1/9*-4*x^(3/2)+1/3*2*x^(3/2)*Log[x]" //
    );
  }

  public void test58() {
    check( //
        "Integrate[Sin[3*x]^2, x]", //
        "x/2-1/6*Cos[3*x]*Sin[3*x]" //
    );
  }

  public void test59() {
    check( //
        "Integrate[Cos[x]^2, x]", //
        "x/2+1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test60() {
    check( //
        "Integrate[Cos[x]^4, x]", //
        "1/8*3*x+1/8*3*Cos[x]*Sin[x]+1/4*Cos[x]^3*Sin[x]" //
    );
  }

  public void test61() {
    check( //
        "Integrate[Sin[x]^3, x]", //
        "-Cos[x]+Cos[x]^3/3" //
    );
  }

  public void test62() {
    check( //
        "Integrate[Sin[x]^3*Cos[x]^4, x]", //
        "-Cos[x]^5/5+Cos[x]^7/7" //
    );
  }

  public void test63() {
    check( //
        "Integrate[Sin[x]^4*Cos[x]^3, x]", //
        "Sin[x]^5/5-Sin[x]^7/7" //
    );
  }

  public void test64() {
    check( //
        "Integrate[Sin[x]^4*Cos[x]^2, x]", //
        "x/16+1/16*Cos[x]*Sin[x]-1/8*Cos[x]^3*Sin[x]-1/6*Cos[x]^3*Sin[x]^3" //
    );
  }

  public void test65() {
    check( //
        "Integrate[Sin[x]^2*Cos[x]^2, x]", //
        "x/8+1/8*Cos[x]*Sin[x]-1/4*Cos[x]^3*Sin[x]" //
    );
  }

  public void test66() {
    check( //
        "Integrate[(1-Sin[2*x])^2, x]", //
        "1/2*3*x+Cos[2*x]-1/4*Cos[2*x]*Sin[2*x]" //
    );
  }

  public void test67() {
    check( //
        "Integrate[Sin[x+Pi/6]*Cos[x], x]", //
        "x/4-Cos[Pi/6+2*x]/4" //
    );
  }

  public void test68() {
    check( //
        "Integrate[Cos[x]^5*Sin[x]^5, x]", //
        "Sin[x]^6/6-Sin[x]^8/4+Sin[x]^10/10" //
    );
  }

  public void test69() {
    check( //
        "Integrate[Sin[x]^6, x]", //
        "1/16*5*x-1/16*5*Cos[x]*Sin[x]-1/24*5*Cos[x]*Sin[x]^3-1/6*Cos[x]*Sin[x]^5" //
    );
  }

  public void test70() {
    check( //
        "Integrate[Cos[x]^6, x]", //
        "1/16*5*x+5/16*Cos[x]*Sin[x]+5/24*Cos[x]^3*Sin[x]+1/6*Cos[x]^5*Sin[x]" //
    );
  }

  public void test71() {
    check( //
        "Integrate[Sin[2*x]^2*Cos[2*x]^4, x]", //
        "x/16+1/32*Cos[2*x]*Sin[2*x]+1/48*Cos[2*x]^3*Sin[2*x]-1/12*Cos[2*x]^5*Sin[2*x]" //
    );
  }

  public void test72() {
    check( //
        "Integrate[Sin[x]^5, x]", //
        "-Cos[x]+1/3*2*Cos[x]^3-Cos[x]^5/5" //
    );
  }

  public void test73() {
    check( //
        "Integrate[Sin[x]^4*Cos[x]^4, x]", //
        "1/128*3*x+3/128*Cos[x]*Sin[x]+1/64*Cos[x]^3*Sin[x]-1/16*Cos[x]^5*Sin[x]-1/8*Cos[x]^5*Sin[x]^3" //
    );
  }

  public void test74() {
    check( //
        "Integrate[Sin[x]^3*Sqrt[Cos[x]], x]", //
        "1/3*-2*Cos[x]^(3/2)+1/7*2*Cos[x]^(7/2)" //
    );
  }

  public void test75() {
    check( //
        "Integrate[Cos[x]^3*Sqrt[Sin[x]], x]", //
        "2/3*Sin[x]^(3/2)-2/7*Sin[x]^(7/2)" //
    );
  }

  public void test76() {
    check( //
        "Integrate[Cos[Sqrt[x]]^2/Sqrt[x], x]", //
        "Sqrt[x]+Cos[Sqrt[x]]*Sin[Sqrt[x]]" //
    );
  }

  public void test77() {
    check( //
        "Integrate[x*Sin[x^2]^3, x]", //
        "-Cos[x^2]/2+Cos[x^2]^3/6" //
    );
  }

  public void test78() {
    check( //
        "Integrate[Cos[x]^2*Tan[x]^3, x]", //
        "Cos[x]^2/2-Log[Cos[x]]" //
    );
  }

  public void test79() {
    check( //
        "Integrate[Cot[x]^5*Sin[x]^2, x]", //
        "-Csc[x]^2/2-2*Log[Sin[x]]+Sin[x]^2/2" //
    );
  }

  public void test80() {
    check( //
        "Integrate[(1-Sin[x])/Cos[x], x]", //
        "Log[1+Sin[x]]" //
    );
  }

  public void test81() {
    check( //
        "Integrate[1/(1-Sin[x]), x]", //
        "Cos[x]/(1-Sin[x])" //
    );
  }

  public void test82() {
    check( //
        "Integrate[Tan[x]^2, x]", //
        "-x+Tan[x]" //
    );
  }

  public void test83() {
    check( //
        "Integrate[Tan[x]^4, x]", //
        "x-Tan[x]+Tan[x]^3/3" //
    );
  }

  public void test84() {
    check( //
        "Integrate[Sec[x]^4, x]", //
        "Tan[x]+Tan[x]^3/3" //
    );
  }

  public void test85() {
    check( //
        "Integrate[Sec[x]^6, x]", //
        "Tan[x]+1/3*2*Tan[x]^3+Tan[x]^5/5" //
    );
  }

  public void test86() {
    check( //
        "Integrate[Tan[x]^4*Sec[x]^2, x]", //
        "Tan[x]^5/5" //
    );
  }

  public void test87() {
    check( //
        "Integrate[Tan[x]^2*Sec[x]^4, x]", //
        "Tan[x]^3/3+Tan[x]^5/5" //
    );
  }

  public void test88() {
    check( //
        "Integrate[Tan[x]*Sec[x]^3, x]", //
        "Sec[x]^3/3" //
    );
  }

  public void test89() {
    check( //
        "Integrate[Tan[x]^3*Sec[x]^3, x]", //
        "-Sec[x]^3/3+Sec[x]^5/5" //
    );
  }

  public void test90() {
    check( //
        "Integrate[Tan[x]^5, x]", //
        "-Log[Cos[x]]-Tan[x]^2/2+Tan[x]^4/4" //
    );
  }

  public void test91() {
    check( //
        "Integrate[Tan[x]^6, x]", //
        "-x+Tan[x]-Tan[x]^3/3+Tan[x]^5/5" //
    );
  }

  public void test92() {
    check( //
        "Integrate[Tan[x]^5*Sec[x], x]", //
        "Sec[x]-1/3*2*Sec[x]^3+Sec[x]^5/5" //
    );
  }

  public void test93() {
    check( //
        "Integrate[Tan[x]^5*Sec[x]^3, x]", //
        "Sec[x]^3/3-1/5*2*Sec[x]^5+Sec[x]^7/7" //
    );
  }

  public void test94() {
    check( //
        "Integrate[Tan[x]*Sec[x]^6, x]", //
        "Sec[x]^6/6" //
    );
  }

  public void test95() {
    check( //
        "Integrate[Tan[x]^3*Sec[x]^6, x]", //
        "-Sec[x]^6/6+Sec[x]^8/8" //
    );
  }

  public void test96() {
    check( //
        "Integrate[Sec[x]^2/Cot[x], x]", //
        "Sec[x]^2/2" //
    );
  }

  public void test97() {
    check( //
        "Integrate[Sec[x]*Tan[x]^2, x]", //
        "-ArcTanh[Sin[x]]/2+1/2*Sec[x]*Tan[x]" //
    );
  }

  public void test98() {
    check( //
        "Integrate[Cot[x]^2, x]", //
        "-x-Cot[x]" //
    );
  }

  public void test99() {
    check( //
        "Integrate[Cot[x]^3, x]", //
        "-Cot[x]^2/2-Log[Sin[x]]" //
    );
  }

  public void test100() {
    check( //
        "Integrate[Cot[x]^4*Csc[x]^4, x]", //
        "-Cot[x]^5/5-Cot[x]^7/7" //
    );
  }

  public void test101() {
    check( //
        "Integrate[Cot[x]^3*Csc[x]^4, x]", //
        "Csc[x]^4/4-Csc[x]^6/6" //
    );
  }

  public void test102() {
    check( //
        "Integrate[Csc[x], x]", //
        "-ArcTanh[Cos[x]]" //
    );
  }

  public void test103() {
    check( //
        "Integrate[Csc[x]^3, x]", //
        "-ArcTanh[Cos[x]]/2-1/2*Cot[x]*Csc[x]" //
    );
  }

  public void test104() {
    check( //
        "Integrate[Cos[x]^2/Sin[x], x]", //
        "-ArcTanh[Cos[x]]+Cos[x]" //
    );
  }

  public void test105() {
    check( //
        "Integrate[1/Sin[x]^4, x]", //
        "-Cot[x]-Cot[x]^3/3" //
    );
  }

  public void test106() {
    check( //
        "Integrate[Sin[5*x]*Sin[2*x], x]", //
        "Sin[3*x]/6-Sin[7*x]/14" //
    );
  }

  public void test107() {
    check( //
        "Integrate[Sin[3*x]*Cos[x], x]", //
        "-Cos[2*x]/4-Cos[4*x]/8" //
    );
  }

  public void test108() {
    check( //
        "Integrate[Cos[3*x]*Cos[4*x], x]", //
        "Sin[x]/2+Sin[7*x]/14" //
    );
  }

  public void test109() {
    check( //
        "Integrate[Sin[3*x]*Sin[6*x], x]", //
        "Sin[3*x]/6-Sin[9*x]/18" //
    );
  }

  public void test110() {
    check( //
        "Integrate[Sin[x]*Cos[x]^5, x]", //
        "-Cos[x]^6/6" //
    );
  }

  public void test111() {
    check( //
        "Integrate[Cos[x]*Cos[2*x]*Cos[3*x], x]", //
        "x/4+Sin[2*x]/8+Sin[4*x]/16+Sin[6*x]/24" //
    );
  }

  public void test112() {
    check( //
        "Integrate[(1-Tan[x]^2)/Sec[x]^2, x]", //
        "Cos[x]*Sin[x]" //
    );
  }

  public void test113() {
    check( //
        "Integrate[(Cos[x]+Sin[x])/Sin[2*x], x]", //
        "-ArcTanh[Cos[x]]/2+ArcTanh[Sin[x]]/2" //
    );
  }

  public void test114() {
    check( //
        "Integrate[Sin[x]^2*Tan[x], x]", //
        "Cos[x]^2/2-Log[Cos[x]]" //
    );
  }

  public void test115() {
    check( //
        "Integrate[Cos[x]^2*Cot[x]^3, x]", //
        "-Csc[x]^2/2-2*Log[Sin[x]]+Sin[x]^2/2" //
    );
  }

  public void test116() {
    check( //
        "Integrate[Sec[x]^3*Tan[x], x]", //
        "Sec[x]^3/3" //
    );
  }

  public void test117() {
    check( //
        "Integrate[Sec[x]^3*Tan[x]^3, x]", //
        "-Sec[x]^3/3+Sec[x]^5/5" //
    );
  }

  public void test118() {
    check( //
        "Integrate[Sqrt[9-x^2]/x^2, x]", //
        "-Sqrt[9-x^2]/x-ArcSin[x/3]" //
    );
  }

  public void test119() {
    check( //
        "Integrate[1/(x^2*Sqrt[4+x^2]), x]", //
        "-Sqrt[4+x^2]/(4*x)" //
    );
  }

  public void test120() {
    check( //
        "Integrate[x/Sqrt[4+x^2], x]", //
        "Sqrt[4+x^2]" //
    );
  }

  public void test121() {
    check( //
        "Integrate[1/Sqrt[-a^2+x^2], x]", //
        "ArcTanh[x/Sqrt[-a^2+x^2]]" //
    );
  }

  public void test122() {
    check( //
        "Integrate[x^3/(9+4*x^2)^(3/2), x]", //
        "9/(16*Sqrt[9+4*x^2])+Sqrt[9+4*x^2]/16" //
    );
  }

  public void test123() {
    check( //
        "Integrate[x/Sqrt[3-2*x-x^2], x]", //
        "-Sqrt[3-2*x-x^2]+ArcSin[1/2*(-1-x)]" //
    );
  }

  public void test124() {
    check( //
        "Integrate[1/(x^2*Sqrt[1-x^2]), x]", //
        "-Sqrt[1-x^2]/x" //
    );
  }

  public void test125() {
    check( //
        "Integrate[x^3*Sqrt[4-x^2], x]", //
        "1/3*-4*(4-x^2)^(3/2)+(4-x^2)^(5/2)/5" //
    );
  }

  public void test126() {
    check( //
        "Integrate[x/Sqrt[1-x^2], x]", //
        "-Sqrt[1-x^2]" //
    );
  }

  public void test127() {
    check( //
        "Integrate[x*Sqrt[4-x^2], x]", //
        "-(4-x^2)^(3/2)/3" //
    );
  }

  public void test128() {
    check( //
        "Integrate[Sqrt[1-4*x^2], x]", //
        "1/2*x*Sqrt[1-4*x^2]+ArcSin[2*x]/4" //
    );
  }

  public void test129() {
    check( //
        "Integrate[x^3/Sqrt[x^2+4], x]", //
        "-4*Sqrt[4+x^2]+(4+x^2)^(3/2)/3" //
    );
  }

  public void test130() {
    check( //
        "Integrate[1/Sqrt[9+x^2], x]", //
        "ArcSinh[x/3]" //
    );
  }

  public void test131() {
    check( //
        "Integrate[Sqrt[x^2+1], x]", //
        "1/2*x*Sqrt[1+x^2]+ArcSinh[x]/2" //
    );
  }

  public void test132() {
    check( //
        "Integrate[1/(x^3*Sqrt[x^2+(-1)*16]), x]", //
        "Sqrt[-16+x^2]/(32*x^2)+ArcTan[Sqrt[-16+x^2]/4]/128" //
    );
  }

  public void test133() {
    check( //
        "Integrate[Sqrt[x^2-a^2]/x^4, x]", //
        "(-a^2+x^2)^(3/2)/(3*a^2*x^3)" //
    );
  }

  public void test134() {
    check( //
        "Integrate[Sqrt[9*x^2+(-1)*4]/x, x]", //
        "Sqrt[-4+9*x^2]-2*ArcTan[Sqrt[-4+9*x^2]/2]" //
    );
  }

  public void test135() {
    check( //
        "Integrate[1/(x^2*Sqrt[16*x^2+(-1)*9]), x]", //
        "Sqrt[-9+16*x^2]/(9*x)" //
    );
  }

  public void test136() {
    check( //
        "Integrate[x^2/(a^2-x^2)^(3/2), x]", //
        "x/Sqrt[a^2-x^2]-ArcTan[x/Sqrt[a^2-x^2]]" //
    );
  }

  public void test137() {
    check( //
        "Integrate[x^2/Sqrt[5-x^2], x]", //
        "-x*Sqrt[5-x^2]/2+1/2*5*ArcSin[x/Sqrt[5]]" //
    );
  }

  public void test138() {
    check( //
        "Integrate[1/(x*Sqrt[3+x^2]), x]", //
        "-ArcTanh[Sqrt[3+x^2]/Sqrt[3]]/Sqrt[3]" //
    );
  }

  public void test139() {
    check( //
        "Integrate[x/(x^2+4)^(5/2), x]", //
        "-1/(3*(4+x^2)^(3/2))" //
    );
  }

  public void test140() {
    check( //
        "Integrate[x^3*Sqrt[4-9*x^2], x]", //
        "1/243*-4*(4-9*x^2)^(3/2)+(4-9*x^2)^(5/2)/405" //
    );
  }

  public void test141() {
    check( //
        "Integrate[x^2*Sqrt[9-x^2], x]", //
        "1/8*-9*x*Sqrt[9-x^2]+1/4*x^3*Sqrt[9-x^2]+1/8*81*ArcSin[x/3]" //
    );
  }

  public void test142() {
    check( //
        "Integrate[5*x*Sqrt[1+x^2], x]", //
        "1/3*5*(1+x^2)^(3/2)" //
    );
  }

  public void test143() {
    check( //
        "Integrate[1/(4*x^2+(-1)*25)^(3/2), x]", //
        "-x/(25*Sqrt[-25+4*x^2])" //
    );
  }

  public void test144() {
    check( //
        "Integrate[Sqrt[2*x-x^2], x]", //
        "-1/2*(1-x)*Sqrt[2*x-x^2]-ArcSin[1-x]/2" //
    );
  }

  public void test145() {
    check( //
        "Integrate[1/Sqrt[x^2+4*x+8], x]", //
        "ArcSinh[1/2*(2+x)]" //
    );
  }

  public void test146() {
    check( //
        "Integrate[1/Sqrt[9*x^2+6*x+(-1)*8], x]", //
        "ArcTanh[(1+3*x)/Sqrt[-8+6*x+9*x^2]]/3" //
    );
  }

  public void test147() {
    check( //
        "Integrate[x^2/Sqrt[4*x-x^2], x]", //
        "-3*Sqrt[4*x-x^2]-1/2*x*Sqrt[4*x-x^2]-6*ArcSin[1-x/2]" //
    );
  }

  public void test148() {
    check( //
        "Integrate[1/(2+2*x+x^2)^2, x]", //
        "(1+x)/(2*(2+2*x+x^2))+ArcTan[1+x]/2" //
    );
  }

  public void test149() {
    check( //
        "Integrate[1/(5-4*x-x^2)^(5/2), x]", //
        "(2+x)/(27*(5-4*x-x^2)^(3/2))+(2*(2+x))/(243*Sqrt[5-4*x-x^2])" //
    );
  }

  public void test150() {
    check( //
        "Integrate[E^t*Sqrt[9-E^(2*t)], t]", //
        "1/2*E^t*Sqrt[9-E^(2*t)]+1/2*9*ArcSin[E^t/3]" //
    );
  }

  public void test151() {
    check( //
        "Integrate[Sqrt[E^(2*t)+(-1)*9], t]", //
        "Sqrt[-9+E^(2*t)]-3*ArcTan[Sqrt[-9+E^(2*t)]/3]" //
    );
  }

  public void test152() {
    check( //
        "Integrate[1/Sqrt[a^2+x^2], x]", //
        "ArcTanh[x/Sqrt[a^2+x^2]]" //
    );
  }

  public void test153() {
    check( //
        "Integrate[(5+x)/(-2+x+x^2), x]", //
        "2*Log[1-x]-Log[2+x]" //
    );
  }

  public void test154() {
    check( //
        "Integrate[(x+x^3)/(-1+x), x]", //
        "2*x+x^2/2+x^3/3+2*Log[1-x]" //
    );
  }

  public void test155() {
    check( //
        "Integrate[(-1+2*x+x^2)/(-2*x+3*x^2+2*x^3), x]", //
        "Log[1-2*x]/10+Log[x]/2-Log[2+x]/10" //
    );
  }

  public void test156() {
    check( //
        "Integrate[(1+4*x-2*x^2+x^4)/(1-x-x^2+x^3), x]", //
        "2/(1-x)+x+x^2/2+Log[1-x]-Log[1+x]" //
    );
  }

  public void test157() {
    check( //
        "Integrate[(4-x+2*x^2)/(4*x+x^3), x]", //
        "-ArcTan[x/2]/2+Log[x]+Log[4+x^2]/2" //
    );
  }

  public void test158() {
    check( //
        "Integrate[(2-3*x+4*x^2)/(3-4*x+4*x^2), x]", //
        "x+ArcTan[(1-2*x)/Sqrt[2]]/(4*Sqrt[2])+Log[3-4*x+4*x^2]/8" //
    );
  }

  public void test159() {
    check( //
        "Integrate[(1+x^2+x^3)/((-1+x)*x*(1+x^2)^3*(1+x+x^2)), x]", //
        "(1+x)/(8*(1+x^2)^2)-(3*(1-x))/(8*(1+x^2))+(3*x)/(16*(1+x^2))+1/16*7*ArcTan[x]-ArcTan[(1+2*x)/Sqrt[3]]/Sqrt[3]+Log[1-x]/8-Log[x]+1/16*15*Log[1+x^2]-Log[1+x+x^2]/2" //
    );
  }

  public void test160() {
    check( //
        "Integrate[(1-3*x+2*x^2-x^3)/(x*(x^2+1)^2), x]", //
        "-(1+2*x)/(2*(1+x^2))-2*ArcTan[x]+Log[x]-Log[1+x^2]/2" //
    );
  }

  public void test161() {
    check( //
        "Integrate[1/(x^2+1)^2, x]", //
        "x/(2*(1+x^2))+ArcTan[x]/2" //
    );
  }

  public void test162() {
    check( //
        "Integrate[1/((x+(-1)*1)*(2+x)), x]", //
        "Log[1-x]/3-Log[2+x]/3" //
    );
  }

  public void test163() {
    check( //
        "Integrate[7/(-12+5*x+2*x^2), x]", //
        "1/11*7*Log[3-2*x]-1/11*7*Log[4+x]" //
    );
  }

  public void test164() {
    check( //
        "Integrate[(-4+3*x+x^2)/((-1+2*x)^2*(3+2*x)), x]", //
        "-9/(32*(1-2*x))+1/128*41*Log[1-2*x]-1/128*25*Log[3+2*x]" //
    );
  }

  public void test165() {
    check( //
        "Integrate[(-x^2+x^3)/((-6+x)*(3+5*x)^3), x]", //
        "-12/(1375*(3+5*x)^2)+201/(15125*(3+5*x))+1/3993*20*Log[6-x]+1/499125*1493*Log[3+5*x]" //
    );
  }

  public void test166() {
    check( //
        "Integrate[1/(-x^3+x^4), x]", //
        "1/(2*x^2)+1/x+Log[1-x]-Log[x]" //
    );
  }

  public void test167() {
    check( //
        "Integrate[(1-x-x^2+x^3+x^4)/(-x+x^3), x]", //
        "x+x^2/2-Log[x]+Log[1-x^2]/2" //
    );
  }

  public void test168() {
    check( //
        "Integrate[(x^2+(-1)*2)/(x*(x^2+2)), x]", //
        "-Log[x]+Log[2+x^2]" //
    );
  }

  public void test169() {
    check( //
        "Integrate[(2-4*x^2+x^3)/((1+x^2)*(2+x^2)), x]", //
        "6*ArcTan[x]-5*Sqrt[2]*ArcTan[x/Sqrt[2]]-Log[1+x^2]/2+Log[2+x^2]" //
    );
  }

  public void test170() {
    check( //
        "Integrate[(1+x^2+x^4)/((1+x^2)*(4+x^2)^2), x]", //
        "(-13*x)/(24*(4+x^2))+1/144*25*ArcTan[x/2]+ArcTan[x]/9" //
    );
  }

  public void test171() {
    check( //
        "Integrate[(1+16*x)/((5+x)^2*(-3+2*x)*(1+x+x^2)), x]", //
        "-79/(273*(5+x))+(451*ArcTan[(1+2*x)/Sqrt[3]])/(2793*Sqrt[3])+1/3211*200*Log[3-2*x]+1/24843*2731*Log[5+x]-1/5586*481*Log[1+x+x^2]" //
    );
  }

  public void test172() {
    check( //
        "Integrate[x^4/(9+x^2)^3, x]", //
        "-x^3/(4*(9+x^2)^2)-(3*x)/(8*(9+x^2))+ArcTan[x/3]/8" //
    );
  }

  public void test173() {
    check( //
        "Integrate[(19*x)/((-1+x)^3*(3+5*x+4*x^2)^2), x]", //
        "-399/(736*(1-x)^2)-1843/(4416*(1-x))+(19*(39+44*x))/(276*(1-x)^2*(3+5*x+4*x^2))+(114437*ArcTan[(5+8*x)/Sqrt[23]])/(52992*Sqrt[23])+1/2304*209*Log[1-x]-1/4608*209*Log[3+5*x+4*x^2]" //
    );
  }

  public void test174() {
    check( //
        "Integrate[(1+x^2+x^3)/(2*x^2+x^3+x^4), x]", //
        "-1/(2*x)+ArcTan[(1+2*x)/Sqrt[7]]/(4*Sqrt[7])-Log[x]/4+1/8*5*Log[2+x+x^2]" //
    );
  }

  public void test175() {
    check( //
        "Integrate[1/(-x^3+x^6), x]", //
        "1/(2*x^2)-ArcTan[(1+2*x)/Sqrt[3]]/Sqrt[3]+Log[1-x]/3-Log[1+x+x^2]/6" //
    );
  }

  public void test176() {
    check( //
        "Integrate[x^2/(1+x), x]", //
        "-x+x^2/2+Log[1+x]" //
    );
  }

  public void test177() {
    check( //
        "Integrate[x/(-5+x), x]", //
        "x+5*Log[5-x]" //
    );
  }

  public void test178() {
    check( //
        "Integrate[(-1+4*x)/((-1+x)*(2+x)), x]", //
        "Log[1-x]+3*Log[2+x]" //
    );
  }

  public void test179() {
    check( //
        "Integrate[1/((1+x)*(2+x)), x]", //
        "Log[1+x]-Log[2+x]" //
    );
  }

  public void test180() {
    check( //
        "Integrate[(-5+6*x)/(3+2*x), x]", //
        "3*x-7*Log[3+2*x]" //
    );
  }

  public void test181() {
    check( //
        "Integrate[1/((a+x)*(b+x)), x]", //
        "-Log[a+x]/(a-b)+Log[b+x]/(a-b)" //
    );
  }

  public void test182() {
    check( //
        "Integrate[(1+x^2)/(-x+x^2), x]", //
        "x+2*Log[1-x]-Log[x]" //
    );
  }

  public void test183() {
    check( //
        "Integrate[(1-12*x+x^2+x^3)/(-12+x+x^2), x]", //
        "x^2/2+Log[3-x]/7-Log[4+x]/7" //
    );
  }

  public void test184() {
    check( //
        "Integrate[(3+2*x)/(1+x)^2, x]", //
        "-1/(1+x)+2*Log[1+x]" //
    );
  }

  public void test185() {
    check( //
        "Integrate[1/(x*(1+x)*(3+2*x)), x]", //
        "Log[x]/3-Log[1+x]+1/3*2*Log[3+2*x]" //
    );
  }

  public void test186() {
    check( //
        "Integrate[(-3+5*x+6*x^2)/(-3*x+2*x^2+x^3), x]", //
        "2*Log[1-x]+Log[x]+3*Log[3+x]" //
    );
  }

  public void test187() {
    check( //
        "Integrate[x/(4+4*x+x^2), x]", //
        "2/(2+x)+Log[2+x]" //
    );
  }

  public void test188() {
    check( //
        "Integrate[1/((-1+x)^2*(4+x)), x]", //
        "1/(5*(1-x))-Log[1-x]/25+Log[4+x]/25" //
    );
  }

  public void test189() {
    check( //
        "Integrate[x^2/((-3+x)*(2+x)^2), x]", //
        "4/(5*(2+x))+1/25*9*Log[3-x]+1/25*16*Log[2+x]" //
    );
  }

  public void test190() {
    check( //
        "Integrate[(-2+3*x+5*x^2)/(2*x^2+x^3), x]", //
        "1/x+2*Log[x]+3*Log[2+x]" //
    );
  }

  public void test191() {
    check( //
        "Integrate[(18-2*x-4*x^2)/(-6+x+4*x^2+x^3), x]", //
        "Log[1-x]-2*Log[2+x]-3*Log[3+x]" //
    );
  }

  public void test192() {
    check( //
        "Integrate[(2*x+x^2)/(4+3*x^2+x^3), x]", //
        "Log[4+3*x^2+x^3]/3" //
    );
  }

  public void test193() {
    check( //
        "Integrate[1/((-1+x)^2*x^2), x]", //
        "1/(1-x)-1/x-2*Log[1-x]+2*Log[x]" //
    );
  }

  public void test194() {
    check( //
        "Integrate[x^2/(1+x)^3, x]", //
        "-1/(2*(1+x)^2)+2/(1+x)+Log[1+x]" //
    );
  }

  public void test195() {
    check( //
        "Integrate[1/(-x^2+x^4), x]", //
        "1/x-ArcTanh[x]" //
    );
  }

  public void test196() {
    check( //
        "Integrate[(-x+2*x^3)/(1-x^2+x^4), x]", //
        "Log[1-x^2+x^4]/2" //
    );
  }

  public void test197() {
    check( //
        "Integrate[x^3/(1+x^2), x]", //
        "x^2/2-Log[1+x^2]/2" //
    );
  }

  public void test198() {
    check( //
        "Integrate[(-1+x)/(2+2*x+x^2), x]", //
        "-2*ArcTan[1+x]+Log[2+2*x+x^2]/2" //
    );
  }

  public void test199() {
    check( //
        "Integrate[x/(1+x+x^2), x]", //
        "-ArcTan[(1+2*x)/Sqrt[3]]/Sqrt[3]+Log[1+x+x^2]/2" //
    );
  }

  public void test200() {
    check( //
        "Integrate[(7+5*x+4*x^2)/(5+4*x+4*x^2), x]", //
        "x+3/8*ArcTan[1/2+x]+Log[5+4*x+4*x^2]/8" //
    );
  }

  public void test201() {
    check( //
        "Integrate[(5-4*x+3*x^2)/((-1+x)*(1+x^2)), x]", //
        "-3*ArcTan[x]+2*Log[1-x]+Log[1+x^2]/2" //
    );
  }

  public void test202() {
    check( //
        "Integrate[(3+2*x)/(3*x+x^3), x]", //
        "(2*ArcTan[x/Sqrt[3]])/Sqrt[3]+Log[x]-Log[3+x^2]/2" //
    );
  }

  public void test203() {
    check( //
        "Integrate[1/(-1+x^3), x]", //
        "-ArcTan[(1+2*x)/Sqrt[3]]/Sqrt[3]+Log[1-x]/3-Log[1+x+x^2]/6" //
    );
  }

  public void test204() {
    check( //
        "Integrate[x^3/(1+x^3), x]", //
        "x+ArcTan[(1-2*x)/Sqrt[3]]/Sqrt[3]-Log[1+x]/3+Log[1-x+x^2]/6" //
    );
  }

  public void test205() {
    check( //
        "Integrate[(-1-2*x+x^2)/((-1+x)^2*(1+x^2)), x]", //
        "1/(-1+x)+ArcTan[x]+Log[1-x]-Log[1+x^2]/2" //
    );
  }

  public void test206() {
    check( //
        "Integrate[x^4/(-1+x^4), x]", //
        "x-ArcTan[x]/2-ArcTanh[x]/2" //
    );
  }

  public void test207() {
    check( //
        "Integrate[(-4+6*x-x^2+3*x^3)/((1+x^2)*(2+x^2)), x]", //
        "-3*ArcTan[x]+Sqrt[2]*ArcTan[x/Sqrt[2]]+1/2*3*Log[1+x^2]" //
    );
  }

  public void test208() {
    check( //
        "Integrate[(1+x-2*x^2+x^3)/(4+5*x^2+x^4), x]", //
        "1/2*-3*ArcTan[x/2]+ArcTan[x]+Log[4+x^2]/2" //
    );
  }

  public void test209() {
    check( //
        "Integrate[(-3+x)/(4+2*x+x^2)^2, x]", //
        "-(7+4*x)/(6*(4+2*x+x^2))-(2*ArcTan[(1+x)/Sqrt[3]])/(3*Sqrt[3])" //
    );
  }

  public void test210() {
    check( //
        "Integrate[(1+x^4)/(x*(1+x^2)^2), x]", //
        "1/(1+x^2)+Log[x]" //
    );
  }

  public void test211() {
    check( //
        "Integrate[(Cos[x]*(-3+2*Sin[x]))/(2-3*Sin[x]+Sin[x]^2), x]", //
        "Log[2-3*Sin[x]+Sin[x]^2]" //
    );
  }

  public void test212() {
    check( //
        "Integrate[(Cos[x]^2*Sin[x])/(5+Cos[x]^2), x]", //
        "Sqrt[5]*ArcTan[Cos[x]/Sqrt[5]]-Cos[x]" //
    );
  }

  public void test213() {
    check( //
        "Integrate[1/(x^2+2*x+(-1)*3), x]", //
        "Log[1-x]/4-Log[3+x]/4" //
    );
  }

  public void test214() {
    check( //
        "Integrate[1/(x^2-2*x), x]", //
        "Log[2-x]/2-Log[x]/2" //
    );
  }

  public void test215() {
    check( //
        "Integrate[(2*x+1)/(4*x^2+12*x+(-1)*7), x]", //
        "Log[1-2*x]/8+1/8*3*Log[7+2*x]" //
    );
  }

  public void test216() {
    check( //
        "Integrate[x/(x^2+x+(-1)*1), x]", //
        "1/10*(5-Sqrt[5])*Log[1-Sqrt[5]+2*x]+1/10*(5+Sqrt[5])*Log[1+Sqrt[5]+2*x]" //
    );
  }

  public void test217() {
    check( //
        "Integrate[(-32+5*x-27*x^2+4*x^3)/(-70-299*x-286*x^2+50*x^3-13*x^4+30*x^5), x]", //
        "(3988*ArcTan[(1+2*x)/Sqrt[19]])/(13685*Sqrt[19])-1/80155*3146*Log[7-3*x]-1/323*334*Log[1+2*x]+1/4879*4822*Log[2+5*x]+1/260015*11049*Log[5+x+x^2]" //
    );
  }

  public void test218() {
    check( //
        "Integrate[(8-13*x^2-7*x^3+12*x^5)/(4-20*x+41*x^2-80*x^3+116*x^4-80*x^5+100*x^6), x]", //
        "5828/(9075*(2-5*x))-(313+502*x)/(1452*(1+2*x^2))-(251*ArcTan[Sqrt[2]*x])/(726*Sqrt[2])+1/1331*272*Sqrt[2]*ArcTan[Sqrt[2]*x]-1/99825*59096*Log[2-5*x]+1/7986*2843*Log[1+2*x^2]" //
    );
  }

  public void test219() {
    check( //
        "Integrate[Sqrt[4+x]/x, x]", //
        "2*Sqrt[4+x]-4*ArcTanh[Sqrt[4+x]/2]" //
    );
  }

  public void test220() {
    check( //
        "Integrate[1/(-1/x^(1/3)+Sqrt[x]), x]", //
        "2*Sqrt[x]+3/5*Sqrt[2*(5-Sqrt[5])]*ArcTan[(1-Sqrt[5]+4*x^(1/6))/Sqrt[2*(5+Sqrt[5])]]-3/5*Sqrt[2*(5+Sqrt[5])]*ArcTan[1/2*Sqrt[1/10*(5+Sqrt[5])]*(1+Sqrt[5]+4*x^(1/6))]+6/5*Log[1-x^(1/6)]-3/10*(1+Sqrt[5])*Log[2+x^(1/6)-Sqrt[5]*x^(1/6)+2*x^(1/3)]-3/10*(1-Sqrt[5])*Log[2+x^(1/6)+Sqrt[5]*x^(1/6)+2*x^(1/3)]" //
    );
  }

  public void test221() {
    check( //
        "Integrate[1/(-4*Cos[x]+3*Sin[x]), x]", //
        "-ArcTanh[1/5*(3*Cos[x]+4*Sin[x])]/5" //
    );
  }

  public void test222() {
    check( //
        "Integrate[1/(1+Sqrt[x]), x]", //
        "2*Sqrt[x]-2*Log[1+Sqrt[x]]" //
    );
  }

  public void test223() {
    check( //
        "Integrate[1/(1+1/x^(1/3)), x]", //
        "3*x^(1/3)-1/2*3*x^(2/3)+x-3*Log[1+1/x^(1/3)]-Log[x]" //
    );
  }

  public void test224() {
    check( //
        "Integrate[Sqrt[x]/(1+x), x]", //
        "2*Sqrt[x]-2*ArcTan[Sqrt[x]]" //
    );
  }

  public void test225() {
    check( //
        "Integrate[1/(x*Sqrt[1+x]), x]", //
        "-2*ArcTanh[Sqrt[1+x]]" //
    );
  }

  public void test226() {
    check( //
        "Integrate[1/(-x^(1/3)+x), x]", //
        "1/2*3*Log[1-x^(2/3)]" //
    );
  }

  public void test227() {
    check( //
        "Integrate[1/(x-Sqrt[2+x]), x]", //
        "1/3*4*Log[2-Sqrt[2+x]]+1/3*2*Log[1+Sqrt[2+x]]" //
    );
  }

  public void test228() {
    check( //
        "Integrate[x^2/Sqrt[-1+x], x]", //
        "2*Sqrt[-1+x]+1/3*4*(-1+x)^(3/2)+1/5*2*(-1+x)^(5/2)" //
    );
  }

  public void test229() {
    check( //
        "Integrate[Sqrt[-1+x]/(1+x), x]", //
        "2*Sqrt[-1+x]-2*Sqrt[2]*ArcTan[Sqrt[-1+x]/Sqrt[2]]" //
    );
  }

  public void test230() {
    check( //
        "Integrate[1/Sqrt[1+Sqrt[x]], x]", //
        "-4*Sqrt[1+Sqrt[x]]+1/3*4*(1+Sqrt[x])^(3/2)" //
    );
  }

  public void test231() {
    check( //
        "Integrate[Sqrt[x]/(x+x^2), x]", //
        "2*ArcTan[Sqrt[x]]" //
    );
  }

  public void test232() {
    check( //
        "Integrate[(1+Sqrt[x])/(-1+Sqrt[x]), x]", //
        "4*Sqrt[x]+x+4*Log[1-Sqrt[x]]" //
    );
  }

  public void test233() {
    check( //
        "Integrate[(1+1/x^(1/3))/(-1+1/x^(1/3)), x]", //
        "-6*x^(1/3)-3*x^(2/3)-x-6*Log[1-x^(1/3)]" //
    );
  }

  public void test234() {
    check( //
        "Integrate[x^3/(1+x^2)^(1/3), x]", //
        "-3/4*(1+x^2)^(2/3)+3/10*(1+x^2)^(5/3)" //
    );
  }

  public void test235() {
    check( //
        "Integrate[Sqrt[x]/(-1/x^(1/3)+Sqrt[x]), x]", //
        "6*x^(1/6)+x-3/5*Sqrt[2*(5+Sqrt[5])]*ArcTan[(1-Sqrt[5]+4*x^(1/6))/Sqrt[2*(5+Sqrt[5])]]-3/5*Sqrt[2*(5-Sqrt[5])]*ArcTan[1/2*Sqrt[1/10*(5+Sqrt[5])]*(1+Sqrt[5]+4*x^(1/6))]+6/5*Log[1-x^(1/6)]-3/10*(1-Sqrt[5])*Log[2+x^(1/6)-Sqrt[5]*x^(1/6)+2*x^(1/3)]-3/10*(1+Sqrt[5])*Log[2+x^(1/6)+Sqrt[5]*x^(1/6)+2*x^(1/3)]" //
    );
  }

  public void test236() {
    check( //
        "Integrate[1/(1/x^(1/4)+Sqrt[x]), x]", //
        "2*Sqrt[x]+(4*ArcTan[(1-2*x^(1/4))/Sqrt[3]])/Sqrt[3]+1/3*4*Log[1+x^(1/4)]-1/3*2*Log[1-x^(1/4)+Sqrt[x]]" //
    );
  }

  public void test237() {
    check( //
        "Integrate[1/(1/x^(1/3)+1/x^(1/4)), x]", //
        "12*x^(1/12)-6*x^(1/6)+4*x^(1/4)-3*x^(1/3)+1/5*12*x^(5/12)-2*Sqrt[x]+1/7*12*x^(7/12)-1/2*3*x^(2/3)+1/3*4*x^(3/4)-1/5*6*x^(5/6)+1/11*12*x^(11/12)-x+1/13*12*x^(13/12)-1/7*6*x^(7/6)+1/5*4*x^(5/4)-12*Log[1+x^(1/12)]" //
    );
  }

  public void test238() {
    check( //
        "Integrate[Sqrt[(1-x)/x], x]", //
        "Sqrt[-1+1/x]*x-ArcTan[Sqrt[-1+1/x]]" //
    );
  }

  public void test239() {
    check( //
        "Integrate[Cos[x]/(Sin[x]+Sin[x]^2), x]", //
        "Log[Sin[x]]-Log[1+Sin[x]]" //
    );
  }

  public void test240() {
    check( //
        "Integrate[E^(2*x)/(2+3*E^x+E^(2*x)), x]", //
        "-Log[1+E^x]+2*Log[2+E^x]" //
    );
  }

  public void test241() {
    check( //
        "Integrate[1/Sqrt[1+E^x], x]", //
        "-2*ArcTanh[Sqrt[1+E^x]]" //
    );
  }

  public void test242() {
    check( //
        "Integrate[Sqrt[1-E^x], x]", //
        "2*Sqrt[1-E^x]-2*ArcTanh[Sqrt[1-E^x]]" //
    );
  }

  public void test243() {
    check( //
        "Integrate[1/(3-5*Sin[x]), x]", //
        "-Log[Cos[x/2]-3*Sin[x/2]]/4+Log[3*Cos[x/2]-Sin[x/2]]/4" //
    );
  }

  public void test244() {
    check( //
        "Integrate[1/(Cos[x]+Sin[x]), x]", //
        "-ArcTanh[(Cos[x]-Sin[x])/Sqrt[2]]/Sqrt[2]" //
    );
  }

  public void test245() {
    check( //
        "Integrate[1/(1-Cos[x]+Sin[x]), x]", //
        "-Log[1+Cot[x/2]]" //
    );
  }

  public void test246() {
    check( //
        "Integrate[1/(4*Cos[x]+3*Sin[x]), x]", //
        "-ArcTanh[1/5*(3*Cos[x]-4*Sin[x])]/5" //
    );
  }

  public void test247() {
    check( //
        "Integrate[1/(Sin[x]+Tan[x]), x]", //
        "-ArcTanh[Cos[x]]/2+1/2*Cot[x]*Csc[x]-Csc[x]^2/2" //
    );
  }

  public void test248() {
    check( //
        "Integrate[1/(2*Sin[x]+Sin[2*x]), x]", //
        "Log[Tan[x/2]]/4+Tan[x/2]^2/8" //
    );
  }

  public void test249() {
    check( //
        "Integrate[Sec[x]/(1+Sin[x]), x]", //
        "ArcTanh[Sin[x]]/2-1/(2*(1+Sin[x]))" //
    );
  }

  public void test250() {
    check( //
        "Integrate[1/(b*Cos[x]+a*Sin[x]), x]", //
        "-ArcTanh[(a*Cos[x]-b*Sin[x])/Sqrt[a^2+b^2]]/Sqrt[a^2+b^2]" //
    );
  }

  public void test251() {
    check( //
        "Integrate[1/(b^2*Cos[x]^2+a^2*Sin[x]^2), x]", //
        "ArcTan[(a*Tan[x])/b]/(a*b)" //
    );
  }

  public void test252() {
    check( //
        "Integrate[x/(-1+x^2), x]", //
        "Log[1-x^2]/2" //
    );
  }

  public void test253() {
    check( //
        "Integrate[(1+Sqrt[x])*Sqrt[x], x]", //
        "1/3*2*x^(3/2)+x^2/2" //
    );
  }

  public void test254() {
    check( //
        "Integrate[1/(1-Cos[x]), x]", //
        "-Sin[x]/(1-Cos[x])" //
    );
  }

  public void test255() {
    check( //
        "Integrate[Sec[x]*Tan[x]^2, x]", //
        "-ArcTanh[Sin[x]]/2+1/2*Sec[x]*Tan[x]" //
    );
  }

  public void test256() {
    check( //
        "Integrate[Sec[x]^3*Tan[x]^3, x]", //
        "-Sec[x]^3/3+Sec[x]^5/5" //
    );
  }

  public void test257() {
    check( //
        "Integrate[E^Sqrt[x], x]", //
        "-2*E^Sqrt[x]+2*E^Sqrt[x]*Sqrt[x]" //
    );
  }

  public void test258() {
    check( //
        "Integrate[(1+x^5)/(-10*x-3*x^2+x^3), x]", //
        "19*x+1/2*3*x^2+x^3/3+1/35*3126*Log[5-x]-Log[x]/10-1/14*31*Log[2+x]" //
    );
  }

  public void test259() {
    check( //
        "Integrate[1/(x*Sqrt[Log[x]]), x]", //
        "2*Sqrt[Log[x]]" //
    );
  }

  public void test260() {
    check( //
        "Integrate[(5+2*x)/(-3+x), x]", //
        "2*x+11*Log[3-x]" //
    );
  }

  public void test261() {
    check( //
        "Integrate[E^(E^x+x), x]", //
        "E^E^x" //
    );
  }

  public void test262() {
    check( //
        "Integrate[Cos[x]^2*Sin[x]^2, x]", //
        "x/8+1/8*Cos[x]*Sin[x]-1/4*Cos[x]^3*Sin[x]" //
    );
  }

  public void test263() {
    check( //
        "Integrate[(-Cos[x]+Sin[x])/(Cos[x]+Sin[x]), x]", //
        "-Log[Cos[x]+Sin[x]]" //
    );
  }

  public void test264() {
    check( //
        "Integrate[x/Sqrt[1-x^2], x]", //
        "-Sqrt[1-x^2]" //
    );
  }

  public void test265() {
    check( //
        "Integrate[x^3*Log[x], x]", //
        "-x^4/16+1/4*x^4*Log[x]" //
    );
  }

  public void test266() {
    check( //
        "Integrate[Sqrt[-2+x]/(2+x), x]", //
        "2*Sqrt[-2+x]-4*ArcTan[Sqrt[-2+x]/2]" //
    );
  }

  public void test267() {
    check( //
        "Integrate[x/(2+x)^2, x]", //
        "2/(2+x)+Log[2+x]" //
    );
  }

  public void test268() {
    check( //
        "Integrate[Log[1+x^2], x]", //
        "-2*x+2*ArcTan[x]+x*Log[1+x^2]" //
    );
  }

  public void test269() {
    check( //
        "Integrate[Sqrt[1+Log[x]]/(x*Log[x]), x]", //
        "-2*ArcTanh[Sqrt[1+Log[x]]]+2*Sqrt[1+Log[x]]" //
    );
  }

  public void test270() {
    check( //
        "Integrate[(1+Sqrt[x])^8, x]", //
        "1/9*-2*(1+Sqrt[x])^9+(1+Sqrt[x])^10/5" //
    );
  }

  public void test271() {
    check( //
        "Integrate[Sec[x]^4*Tan[x]^3, x]", //
        "-Sec[x]^4/4+Sec[x]^6/6" //
    );
  }

  public void test272() {
    check( //
        "Integrate[x/(2-2*x+x^2), x]", //
        "-ArcTan[1-x]+Log[2-2*x+x^2]/2" //
    );
  }

  public void test273() {
    check( //
        "Integrate[x*ArcSin[x], x]", //
        "1/4*x*Sqrt[1-x^2]-ArcSin[x]/4+1/2*x^2*ArcSin[x]" //
    );
  }

  public void test274() {
    check( //
        "Integrate[Sqrt[9-x^2]/x, x]", //
        "Sqrt[9-x^2]-3*ArcTanh[Sqrt[9-x^2]/3]" //
    );
  }

  public void test275() {
    check( //
        "Integrate[x/(2+3*x+x^2), x]", //
        "-Log[1+x]+2*Log[2+x]" //
    );
  }

  public void test276() {
    check( //
        "Integrate[x^2*Cosh[x], x]", //
        "-2*x*Cosh[x]+2*Sinh[x]+x^2*Sinh[x]" //
    );
  }

  public void test277() {
    check( //
        "Integrate[(1+x+x^3)/(4*x+2*x^2+x^4), x]", //
        "Log[4*x+2*x^2+x^4]/4" //
    );
  }

  public void test278() {
    check( //
        "Integrate[Cos[x]/(1+Sin[x]^2), x]", //
        "ArcTan[Sin[x]]" //
    );
  }

  public void test279() {
    check( //
        "Integrate[Cos[Sqrt[x]], x]", //
        "2*Cos[Sqrt[x]]+2*Sqrt[x]*Sin[Sqrt[x]]" //
    );
  }

  public void test280() {
    check( //
        "Integrate[Sin[Pi*x], x]", //
        "-Cos[Pi*x]/Pi" //
    );
  }

  public void test281() {
    check( //
        "Integrate[E^(2*x)/(1+E^x), x]", //
        "E^x-Log[1+E^x]" //
    );
  }

  public void test282() {
    check( //
        "Integrate[E^(3*x)*Cos[5*x], x]", //
        "1/34*3*E^(3*x)*Cos[5*x]+1/34*5*E^(3*x)*Sin[5*x]" //
    );
  }

  public void test283() {
    check( //
        "Integrate[Cos[3*x]*Cos[5*x], x]", //
        "Sin[2*x]/4+Sin[8*x]/16" //
    );
  }

  public void test284() {
    check( //
        "Integrate[1/(1+x+x^2+x^3), x]", //
        "ArcTan[x]/2+Log[1+x]/2-Log[1+x^2]/4" //
    );
  }

  public void test285() {
    check( //
        "Integrate[x^2*Log[1+x], x]", //
        "-x/3+x^2/6-x^3/9+Log[1+x]/3+1/3*x^3*Log[1+x]" //
    );
  }

  public void test286() {
    check( //
        "Integrate[x^5/E^x^3, x]", //
        "-1/(3*E^x^3)-x^3/(3*E^x^3)" //
    );
  }

  public void test287() {
    check( //
        "Integrate[Tan[4*x]^2, x]", //
        "-x+Tan[4*x]/4" //
    );
  }

  public void test288() {
    check( //
        "Integrate[1/Sqrt[-5+12*x+9*x^2], x]", //
        "ArcTanh[(2+3*x)/Sqrt[-5+12*x+9*x^2]]/3" //
    );
  }

  public void test289() {
    check( //
        "Integrate[x^2*ArcTan[x], x]", //
        "-x^2/6+1/3*x^3*ArcTan[x]+Log[1+x^2]/6" //
    );
  }

  public void test290() {
    check( //
        "Integrate[(1-Sqrt[x])/x^(1/3), x]", //
        "1/2*3*x^(2/3)-1/7*6*x^(7/6)" //
    );
  }

  public void test291() {
    check( //
        "Integrate[1/(-1/E^x+E^x), x]", //
        "-ArcTanh[E^x]" //
    );
  }

  public void test292() {
    check( //
        "Integrate[x/(10+2*x^2+x^4), x]", //
        "ArcTan[1/3*(1+x^2)]/6" //
    );
  }

  public void test293() {
    check( //
        "Integrate[1/(1/x^(1/3)+x), x]", //
        "1/4*3*Log[1+x^(4/3)]" //
    );
  }

  public void test294() {
    check( //
        "Integrate[Cos[x]^4*Sin[x]^2, x]", //
        "x/16+1/16*Cos[x]*Sin[x]+1/24*Cos[x]^3*Sin[x]-1/6*Cos[x]^5*Sin[x]" //
    );
  }

  public void test295() {
    check( //
        "Integrate[1/Sqrt[5-4*x-x^2], x]", //
        "-ArcSin[1/3*(-2-x)]" //
    );
  }

  public void test296() {
    check( //
        "Integrate[x/(1-x^2+Sqrt[1-x^2]), x]", //
        "-Log[1+Sqrt[1-x^2]]" //
    );
  }

  public void test297() {
    check( //
        "Integrate[(1+Cos[x])*Csc[x], x]", //
        "Log[1-Cos[x]]" //
    );
  }

  public void test298() {
    check( //
        "Integrate[E^x/(-1+E^(2*x)), x]", //
        "-ArcTanh[E^x]" //
    );
  }

  public void test299() {
    check( //
        "Integrate[1/(-8+x^3), x]", //
        "-ArcTan[(1+x)/Sqrt[3]]/(4*Sqrt[3])+Log[2-x]/12-Log[4+2*x+x^2]/24" //
    );
  }

  public void test300() {
    check( //
        "Integrate[x^5*Cosh[x], x]", //
        "-120*Cosh[x]-60*x^2*Cosh[x]-5*x^4*Cosh[x]+120*x*Sinh[x]+20*x^3*Sinh[x]+x^5*Sinh[x]" //
    );
  }

  public void test301() {
    check( //
        "Integrate[Log[Tan[x]]/(Sin[x]*Cos[x]), x]", //
        "Log[Tan[x]]^2/2" //
    );
  }

  public void test302() {
    check( //
        "Integrate[-2*x+x^2+x^3, x]", //
        "-x^2+x^3/3+x^4/4" //
    );
  }

  public void test303() {
    check( //
        "Integrate[(1+E^x)/(1-E^x), x]", //
        "x-2*Log[1-E^x]" //
    );
  }

  public void test304() {
    check( //
        "Integrate[x/((1+x^2)*(4+x^2)), x]", //
        "Log[1+x^2]/6-Log[4+x^2]/6" //
    );
  }

  public void test305() {
    check( //
        "Integrate[1/(4-5*Sin[x]), x]", //
        "-Log[Cos[x/2]-2*Sin[x/2]]/3+Log[2*Cos[x/2]-Sin[x/2]]/3" //
    );
  }

  public void test306() {
    check( //
        "Integrate[x*(c+x)^(1/3), x]", //
        "1/4*-3*c*(c+x)^(4/3)+1/7*3*(c+x)^(7/3)" //
    );
  }

  public void test307() {
    check( //
        "Integrate[E^x^(1/3), x]", //
        "6*E^x^(1/3)-6*E^x^(1/3)*x^(1/3)+3*E^x^(1/3)*x^(2/3)" //
    );
  }

  public void test308() {
    check( //
        "Integrate[1/(4+x+Sqrt[1+x]), x]", //
        "(-2*ArcTan[(1+2*Sqrt[1+x])/Sqrt[11]])/Sqrt[11]+Log[4+x+Sqrt[1+x]]" //
    );
  }

  public void test309() {
    check( //
        "Integrate[(1+x^3)/(-x^2+x^3), x]", //
        "1/x+x+2*Log[1-x]-Log[x]" //
    );
  }

  public void test310() {
    check( //
        "Integrate[(-3+4*x+x^2)*Sin[2*x], x]", //
        "1/4*7*Cos[2*x]-2*x*Cos[2*x]-1/2*x^2*Cos[2*x]+Sin[2*x]+1/2*x*Sin[2*x]" //
    );
  }

  public void test311() {
    check( //
        "Integrate[Cos[Cos[x]]*Sin[x], x]", //
        "-Sin[Cos[x]]" //
    );
  }

  public void test312() {
    check( //
        "Integrate[1/Sqrt[16-x^2], x]", //
        "ArcSin[x/4]" //
    );
  }

  public void test313() {
    check( //
        "Integrate[x^3/(1+x)^10, x]", //
        "1/(9*(1+x)^9)-3/(8*(1+x)^8)+3/(7*(1+x)^7)-1/(6*(1+x)^6)" //
    );
  }

  public void test314() {
    check( //
        "Integrate[Cot[2*x]^3*Csc[2*x]^3, x]", //
        "Csc[2*x]^3/6-Csc[2*x]^5/10" //
    );
  }

  public void test315() {
    check( //
        "Integrate[(x+Sin[x])^2, x]", //
        "x/2+x^3/3-2*x*Cos[x]+2*Sin[x]-1/2*Cos[x]*Sin[x]" //
    );
  }

  public void test316() {
    check( //
        "Integrate[E^ArcTan[x]/(1+x^2), x]", //
        "E^ArcTan[x]" //
    );
  }

  public void test317() {
    check( //
        "Integrate[1/(x*(1+x^4)), x]", //
        "Log[x]-Log[1+x^4]/4" //
    );
  }

  public void test318() {
    check( //
        "Integrate[t^3/E^(2*t), t]", //
        "-3/(8*E^(2*t))-(3*t)/(4*E^(2*t))-(3*t^2)/(4*E^(2*t))-t^3/(2*E^(2*t))" //
    );
  }

  public void test319() {
    check( //
        "Integrate[Sqrt[t]/(1+t^(1/3)), t]", //
        "-6*t^(1/6)+2*Sqrt[t]-1/5*6*t^(5/6)+1/7*6*t^(7/6)+6*ArcTan[t^(1/6)]" //
    );
  }

  public void test320() {
    check( //
        "Integrate[Sin[x]*Sin[2*x]*Sin[3*x], x]", //
        "-Cos[2*x]/8-Cos[4*x]/16+Cos[6*x]/24" //
    );
  }

  public void test321() {
    check( //
        "Integrate[Log[x/2], x]", //
        "-x+x*Log[x/2]" //
    );
  }

  public void test322() {
    check( //
        "Integrate[Sqrt[(1+x)/(1-x)], x]", //
        "-(1-x)*Sqrt[(1+x)/(1-x)]+2*ArcTan[Sqrt[(1+x)/(1-x)]]" //
    );
  }

  public void test323() {
    check( //
        "Integrate[(x*Log[x])/Sqrt[-1+x^2], x]", //
        "-Sqrt[-1+x^2]+ArcTan[Sqrt[-1+x^2]]+Sqrt[-1+x^2]*Log[x]" //
    );
  }

  public void test324() {
    check( //
        "Integrate[(a+x)/(a^2+x^2), x]", //
        "ArcTan[x/a]+Log[a^2+x^2]/2" //
    );
  }

  public void test325() {
    check( //
        "Integrate[Sqrt[1+x-x^2], x]", //
        "-(1-2*x)*Sqrt[1+x-x^2]/4-1/8*5*ArcSin[(1-2*x)/Sqrt[5]]" //
    );
  }

  public void test326() {
    check( //
        "Integrate[x^4/(16+x^10), x]", //
        "ArcTan[x^5/4]/20" //
    );
  }

  public void test327() {
    check( //
        "Integrate[(2+x)/(2+x+x^2), x]", //
        "(3*ArcTan[(1+2*x)/Sqrt[7]])/Sqrt[7]+Log[2+x+x^2]/2" //
    );
  }

  public void test328() {
    check( //
        "Integrate[x*Sec[x]*Tan[x], x]", //
        "-ArcTanh[Sin[x]]+x*Sec[x]" //
    );
  }

  public void test329() {
    check( //
        "Integrate[x/(-a^4+x^4), x]", //
        "-ArcTanh[x^2/a^2]/(2*a^2)" //
    );
  }

  public void test330() {
    check( //
        "Integrate[1/(Sqrt[x]+Sqrt[1+x]), x]", //
        "1/3*-2*x^(3/2)+1/3*2*(1+x)^(3/2)" //
    );
  }

  public void test331() {
    check( //
        "Integrate[1/(1-1/E^x+2*E^x), x]", //
        "Log[1-2*E^x]/3-Log[1+E^x]/3" //
    );
  }

  public void test332() {
    check( //
        "Integrate[ArcTan[Sqrt[x]]/Sqrt[x], x]", //
        "2*Sqrt[x]*ArcTan[Sqrt[x]]-Log[1+x]" //
    );
  }

  public void test333() {
    check( //
        "Integrate[Log[1+x]/x^2, x]", //
        "Log[x]-Log[1+x]-Log[1+x]/x" //
    );
  }

  public void test334() {
    check( //
        "Integrate[1/(-E^x+E^(3*x)), x]", //
        "E^(-x)-ArcTanh[E^x]" //
    );
  }

  public void test335() {
    check( //
        "Integrate[(1+Cos[x]^2)/(1-Cos[x]^2), x]", //
        "-x-2*Cot[x]" //
    );
  }

  public void test336() {
    check( //
        "Integrate[1/(x*Sqrt[-25+2*x]), x]", //
        "1/5*2*ArcTan[Sqrt[-25+2*x]/5]" //
    );
  }

  public void test337() {
    check( //
        "Integrate[Sin[2*x]/Sqrt[9-Cos[x]^4], x]", //
        "-ArcSin[Cos[x]^2/3]" //
    );
  }

  public void test338() {
    check( //
        "Integrate[x^2/Sqrt[5-4*x^2], x]", //
        "-x*Sqrt[5-4*x^2]/8+1/16*5*ArcSin[(2*x)/Sqrt[5]]" //
    );
  }

  public void test339() {
    check( //
        "Integrate[x^3*Sin[x], x]", //
        "6*x*Cos[x]-x^3*Cos[x]-6*Sin[x]+3*x^2*Sin[x]" //
    );
  }

  public void test340() {
    check( //
        "Integrate[x*Sqrt[4+2*x+x^2], x]", //
        "-(1+x)*Sqrt[4+2*x+x^2]/2+(4+2*x+x^2)^(3/2)/3-1/2*3*ArcSinh[(1+x)/Sqrt[3]]" //
    );
  }

  public void test341() {
    check( //
        "Integrate[x*(5+x^2)^8, x]", //
        "(5+x^2)^9/18" //
    );
  }

  public void test342() {
    check( //
        "Integrate[Cos[x]^2*Sin[x]^5, x]", //
        "-Cos[x]^3/3+1/5*2*Cos[x]^5-Cos[x]^7/7" //
    );
  }

  public void test343() {
    check( //
        "Integrate[Cos[4*x]/E^(3*x), x]", //
        "(-3*Cos[4*x])/(25*E^(3*x))+(4*Sin[4*x])/(25*E^(3*x))" //
    );
  }

  public void test344() {
    check( //
        "Integrate[Csc[x/2]^3, x]", //
        "-ArcTanh[Cos[x/2]]-Cot[x/2]*Csc[x/2]" //
    );
  }

  public void test345() {
    check( //
        "Integrate[Sqrt[-1+9*x^2]/x^2, x]", //
        "-Sqrt[-1+9*x^2]/x+3*ArcTanh[(3*x)/Sqrt[-1+9*x^2]]" //
    );
  }

  public void test346() {
    check( //
        "Integrate[Sqrt[4-3*x^2]/x, x]", //
        "Sqrt[4-3*x^2]-2*ArcTanh[Sqrt[4-3*x^2]/2]" //
    );
  }

  public void test347() {
    check( //
        "Integrate[E^(3*x)*x^2, x]", //
        "1/27*2*E^(3*x)-1/9*2*E^(3*x)*x+1/3*E^(3*x)*x^2" //
    );
  }

  public void test348() {
    check( //
        "Integrate[(Cos[x]*Sin[x])/Sqrt[1+Sin[x]], x]", //
        "-2*Sqrt[1+Sin[x]]+1/3*2*(1+Sin[x])^(3/2)" //
    );
  }

  public void test349() {
    check( //
        "Integrate[x*ArcSin[x^2], x]", //
        "Sqrt[1-x^4]/2+1/2*x^2*ArcSin[x^2]" //
    );
  }

  public void test350() {
    check( //
        "Integrate[x^3*ArcSin[x^2], x]", //
        "1/8*x^2*Sqrt[1-x^4]-ArcSin[x^2]/8+1/4*x^4*ArcSin[x^2]" //
    );
  }

  public void test351() {
    check( //
        "Integrate[E^x*Sech[E^x], x]", //
        "ArcTan[Sinh[E^x]]" //
    );
  }

  public void test352() {
    check( //
        "Integrate[x^2*Cos[3*x], x]", //
        "1/9*2*x*Cos[3*x]-1/27*2*Sin[3*x]+1/3*x^2*Sin[3*x]" //
    );
  }

  public void test353() {
    check( //
        "Integrate[Sqrt[5-4*x-x^2], x]", //
        "1/2*(2+x)*Sqrt[5-4*x-x^2]-1/2*9*ArcSin[1/3*(-2-x)]" //
    );
  }

  public void test354() {
    check( //
        "Integrate[x^5/(Sqrt[2]+x^2), x]", //
        "-x^2/Sqrt[2]+x^4/4+Log[Sqrt[2]+x^2]" //
    );
  }

  public void test355() {
    check( //
        "Integrate[Sec[x]^5, x]", //
        "1/8*3*ArcTanh[Sin[x]]+1/8*3*Sec[x]*Tan[x]+1/4*Sec[x]^3*Tan[x]" //
    );
  }

  public void test356() {
    check( //
        "Integrate[Sin[2*x]^6, x]", //
        "1/16*5*x-1/32*5*Cos[2*x]*Sin[2*x]-1/48*5*Cos[2*x]*Sin[2*x]^3-1/12*Cos[2*x]*Sin[2*x]^5" //
    );
  }

  public void test357() {
    check( //
        "Integrate[Cos[x]*Log[Sin[x]]*Sin[x]^2, x]", //
        "-Sin[x]^3/9+1/3*Log[Sin[x]]*Sin[x]^3" //
    );
  }

  public void test358() {
    check( //
        "Integrate[1/(E^x*(1+2*E^x)), x]", //
        "-1/E^x-2*x+2*Log[1+2*E^x]" //
    );
  }

  public void test359() {
    check( //
        "Integrate[Sqrt[2+3*Cos[x]]*Tan[x], x]", //
        "2*Sqrt[2]*ArcTanh[Sqrt[2+3*Cos[x]]/Sqrt[2]]-2*Sqrt[2+3*Cos[x]]" //
    );
  }

  public void test360() {
    check( //
        "Integrate[x/Sqrt[-4*x+x^2], x]", //
        "Sqrt[-4*x+x^2]+4*ArcTanh[x/Sqrt[-4*x+x^2]]" //
    );
  }

  public void test361() {
    check( //
        "Integrate[Cos[x]^5, x]", //
        "Sin[x]-1/3*2*Sin[x]^3+Sin[x]^5/5" //
    );
  }

  public void test362() {
    check( //
        "Integrate[x^4/E^x, x]", //
        "-24/E^x-(24*x)/E^x-(12*x^2)/E^x-(4*x^3)/E^x-x^4/E^x" //
    );
  }

  public void test363() {
    check( //
        "Integrate[x^4/Sqrt[-2+x^10], x]", //
        "ArcTanh[x^5/Sqrt[-2+x^10]]/5" //
    );
  }

  public void test364() {
    check( //
        "Integrate[E^x*Cos[4+3*x], x]", //
        "1/10*E^x*Cos[4+3*x]+1/10*3*E^x*Sin[4+3*x]" //
    );
  }

  public void test365() {
    check( //
        "Integrate[E^x*Log[1+E^x], x]", //
        "-E^x+(1+E^x)*Log[1+E^x]" //
    );
  }

  public void test366() {
    check( //
        "Integrate[x^2*ArcTan[x], x]", //
        "-x^2/6+1/3*x^3*ArcTan[x]+Log[1+x^2]/6" //
    );
  }

  public void test367() {
    check( //
        "Integrate[Sqrt[-1+E^(2*x)], x]", //
        "Sqrt[-1+E^(2*x)]-ArcTan[Sqrt[-1+E^(2*x)]]" //
    );
  }

  public void test368() {
    check( //
        "Integrate[E^Sin[x]*Sin[2*x], x]", //
        "-2*E^Sin[x]+2*E^Sin[x]*Sin[x]" //
    );
  }

  public void test369() {
    check( //
        "Integrate[x^2*Sqrt[5-x^2], x]", //
        "1/8*-5*x*Sqrt[5-x^2]+1/4*x^3*Sqrt[5-x^2]+1/8*25*ArcSin[x/Sqrt[5]]" //
    );
  }

  public void test370() {
    check( //
        "Integrate[x^2*(1+x^3)^4, x]", //
        "(1+x^3)^5/15" //
    );
  }

  public void test371() {
    check( //
        "Integrate[Cos[x]^3*Sin[x]^3, x]", //
        "Sin[x]^4/4-Sin[x]^6/6" //
    );
  }

  public void test372() {
    check( //
        "Integrate[Sec[x]^4*Tan[x]^2, x]", //
        "Tan[x]^3/3+Tan[x]^5/5" //
    );
  }

  public void test373() {
    check( //
        "Integrate[x*Sqrt[1+2*x], x]", //
        "-(1+2*x)^(3/2)/6+(1+2*x)^(5/2)/10" //
    );
  }

  public void test374() {
    check( //
        "Integrate[Sin[x]^4, x]", //
        "1/8*3*x-1/8*3*Cos[x]*Sin[x]-1/4*Cos[x]*Sin[x]^3" //
    );
  }

  public void test375() {
    check( //
        "Integrate[Tan[x]^3, x]", //
        "Log[Cos[x]]+Tan[x]^2/2" //
    );
  }

  public void test376() {
    check( //
        "Integrate[x^5*Sqrt[1+x^2], x]", //
        "(1+x^2)^(3/2)/3-1/5*2*(1+x^2)^(5/2)+(1+x^2)^(7/2)/7" //
    );
  }
}
