package org.matheclipse.core.rubi.independent;

import org.matheclipse.core.rubi.AbstractRubiTestCase;

public class BondarenkoProblemsTests extends AbstractRubiTestCase {
  static boolean init = true;

  public BondarenkoProblemsTests(String name) {
    super(name, false);
  }

  @Override
  protected void setUp() {
    try {
      super.setUp();
      fSeconds = 60;
      if (init) {
        System.out.println("Bondarenko");
        init = false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void test1() {
    check( //
        "Integrate[1/(Sqrt[2]+Sin[z]+Cos[z]), z]", //
        "-(1-Sqrt[2]*Sin[z])/(Cos[z]-Sin[z])" //
    );
  }

  public void test2() {
    check( //
        "Integrate[1/(Sqrt[1+x]+Sqrt[1-x])^2, x]", //
        "-1/(2*x)+Sqrt[1-x^2]/(2*x)+ArcSin[x]/2" //
    );
  }

  public void test3() {
    check( //
        "Integrate[1/(1+Cos[x])^2, x]", //
        "Sin[x]/(3*(1+Cos[x])^2)+Sin[x]/(3*(1+Cos[x]))" //
    );
  }

  public void test4() {
    check( //
        "Integrate[Sin[x]/Sqrt[1+x], x]", //
        "Sqrt[2*Pi]*Cos[1]*FresnelS[Sqrt[2/Pi]*Sqrt[1+x]]-Sqrt[2*Pi]*FresnelC[Sqrt[2/Pi]*Sqrt[1+x]]*Sin[1]" //
    );
  }

  public void test5() {
    check( //
        "Integrate[1/(Cos[x]+Sin[x])^6, x]", //
        "-(Cos[x]-Sin[x])/(10*(Cos[x]+Sin[x])^5)-(Cos[x]-Sin[x])/(15*(Cos[x]+Sin[x])^3)+(2*Sin[x])/(15*(Cos[x]+Sin[x]))" //
    );
  }

  public void test6() {
    check( //
        "Integrate[Log[x^4+1/x^4], x]", //
        "-4*x-Sqrt[2+Sqrt[2]]*ArcTan[(Sqrt[2-Sqrt[2]]-2*x)/Sqrt[2+Sqrt[2]]]-Sqrt[2-Sqrt[2]]*ArcTan[(Sqrt[2+Sqrt[2]]-2*x)/Sqrt[2-Sqrt[2]]]+Sqrt[2+Sqrt[2]]*ArcTan[(Sqrt[2-Sqrt[2]]+2*x)/Sqrt[2+Sqrt[2]]]+Sqrt[2-Sqrt[2]]*ArcTan[(Sqrt[2+Sqrt[2]]+2*x)/Sqrt[2-Sqrt[2]]]-1/2*Sqrt[2-Sqrt[2]]*Log[1-Sqrt[2-Sqrt[2]]*x+x^2]+1/2*Sqrt[2-Sqrt[2]]*Log[1+Sqrt[2-Sqrt[2]]*x+x^2]-1/2*Sqrt[2+Sqrt[2]]*Log[1-Sqrt[2+Sqrt[2]]*x+x^2]+1/2*Sqrt[2+Sqrt[2]]*Log[1+Sqrt[2+Sqrt[2]]*x+x^2]+x*Log[1/x^4+x^4]" //
    );
  }

  public void test7() {
    check( //
        "Integrate[Log[1+x]/(x*Sqrt[1+Sqrt[1+x]]), x]", //
        "-8*ArcTanh[Sqrt[1+Sqrt[1+x]]]-(2*Log[1+x])/Sqrt[1+Sqrt[1+x]]-Sqrt[2]*ArcTanh[Sqrt[1+Sqrt[1+x]]/Sqrt[2]]*Log[1+x]+2*Sqrt[2]*ArcTanh[1/Sqrt[2]]*Log[1-Sqrt[1+Sqrt[1+x]]]-2*Sqrt[2]*ArcTanh[1/Sqrt[2]]*Log[1+Sqrt[1+Sqrt[1+x]]]+Sqrt[2]*PolyLog[2,-(Sqrt[2]*(1-Sqrt[1+Sqrt[1+x]]))/(2-Sqrt[2])]-Sqrt[2]*PolyLog[2,(Sqrt[2]*(1-Sqrt[1+Sqrt[1+x]]))/(2+Sqrt[2])]-Sqrt[2]*PolyLog[2,-(Sqrt[2]*(1+Sqrt[1+Sqrt[1+x]]))/(2-Sqrt[2])]+Sqrt[2]*PolyLog[2,(Sqrt[2]*(1+Sqrt[1+Sqrt[1+x]]))/(2+Sqrt[2])]" //
    );
  }

  public void test8() {
    check( //
        "Integrate[Log[1+x]/x*Sqrt[1+Sqrt[1+x]], x]", //
        "-16*Sqrt[1+Sqrt[1+x]]+16*ArcTanh[Sqrt[1+Sqrt[1+x]]]+4*Sqrt[1+Sqrt[1+x]]*Log[1+x]-2*Sqrt[2]*ArcTanh[Sqrt[1+Sqrt[1+x]]/Sqrt[2]]*Log[1+x]+4*Sqrt[2]*ArcTanh[1/Sqrt[2]]*Log[1-Sqrt[1+Sqrt[1+x]]]-4*Sqrt[2]*ArcTanh[1/Sqrt[2]]*Log[1+Sqrt[1+Sqrt[1+x]]]+2*Sqrt[2]*PolyLog[2,-(Sqrt[2]*(1-Sqrt[1+Sqrt[1+x]]))/(2-Sqrt[2])]-2*Sqrt[2]*PolyLog[2,(Sqrt[2]*(1-Sqrt[1+Sqrt[1+x]]))/(2+Sqrt[2])]-2*Sqrt[2]*PolyLog[2,-(Sqrt[2]*(1+Sqrt[1+Sqrt[1+x]]))/(2-Sqrt[2])]+2*Sqrt[2]*PolyLog[2,(Sqrt[2]*(1+Sqrt[1+Sqrt[1+x]]))/(2+Sqrt[2])]" //
    );
  }

  public void test9() {
    check( //
        "Integrate[1/(1+Sqrt[x+Sqrt[1+x^2]]), x]", //
        "-1/(2*(x+Sqrt[1+x^2]))+1/Sqrt[x+Sqrt[1+x^2]]+Sqrt[x+Sqrt[1+x^2]]+Log[x+Sqrt[1+x^2]]/2-2*Log[1+Sqrt[x+Sqrt[1+x^2]]]" //
    );
  }

  public void test10() {
    check( //
        "Integrate[Sqrt[1+x]/(x+Sqrt[1+Sqrt[1+x]]), x]", //
        "2*Sqrt[1+x]+(8*ArcTanh[(1+2*Sqrt[1+Sqrt[1+x]])/Sqrt[5]])/Sqrt[5]" //
    );
  }

  public void test11() {
    check( //
        "Integrate[1/(x-Sqrt[1+Sqrt[1+x]]), x]", //
        "2/5*(5+Sqrt[5])*Log[1-Sqrt[5]-2*Sqrt[1+Sqrt[1+x]]]+2/5*(5-Sqrt[5])*Log[1+Sqrt[5]-2*Sqrt[1+Sqrt[1+x]]]" //
    );
  }

  public void test12() {
    check( //
        "Integrate[x/(x+Sqrt[1-Sqrt[1+x]]), x]", //
        "2*Sqrt[1+x]-4*Sqrt[1-Sqrt[1+x]]+(1-Sqrt[1+x])^2+(8*ArcTanh[(1+2*Sqrt[1-Sqrt[1+x]])/Sqrt[5]])/Sqrt[5]" //
    );
  }

  public void test13() {
    check( //
        "Integrate[Sqrt[Sqrt[1+x]+x]/((1+x^2)*Sqrt[1+x]), x]", //
        "-(I*ArcTan[(2+Sqrt[1+(-1)*I]-(1-2*Sqrt[1+(-1)*I])*Sqrt[1+x])/(2*Sqrt[I+Sqrt[1+(-1)*I]]*Sqrt[x+Sqrt[1+x]])])/(2*Sqrt[(1+(-1)*I)/(I+Sqrt[1+(-1)*I])])+(I*ArcTan[(2+Sqrt[1+I]-(1-2*Sqrt[1+I])*Sqrt[1+x])/(2*Sqrt[(-1)*I+Sqrt[1+I]]*Sqrt[x+Sqrt[1+x]])])/(2*Sqrt[-(1+I)/(I-Sqrt[1+I])])+(I*ArcTanh[(2-Sqrt[1+(-1)*I]-(1+2*Sqrt[1+(-1)*I])*Sqrt[1+x])/(2*Sqrt[(-1)*I+Sqrt[1+(-1)*I]]*Sqrt[x+Sqrt[1+x]])])/(2*Sqrt[-(1+(-1)*I)/(I-Sqrt[1+(-1)*I])])-(I*ArcTanh[(2-Sqrt[1+I]-(1+2*Sqrt[1+I])*Sqrt[1+x])/(2*Sqrt[I+Sqrt[1+I]]*Sqrt[x+Sqrt[1+x]])])/(2*Sqrt[(1+I)/(I+Sqrt[1+I])])" //
    );
  }

  public void test14() {
    check( //
        "Integrate[Sqrt[x+Sqrt[1+x]]/(1+x^2), x]", //
        "1/2*I*Sqrt[I+Sqrt[1+(-1)*I]]*ArcTan[(2+Sqrt[1+(-1)*I]-(1-2*Sqrt[1+(-1)*I])*Sqrt[1+x])/(2*Sqrt[I+Sqrt[1+(-1)*I]]*Sqrt[x+Sqrt[1+x]])]-1/2*I*Sqrt[(-1)*I+Sqrt[1+I]]*ArcTan[(2+Sqrt[1+I]-(1-2*Sqrt[1+I])*Sqrt[1+x])/(2*Sqrt[(-1)*I+Sqrt[1+I]]*Sqrt[x+Sqrt[1+x]])]+1/2*I*Sqrt[(-1)*I+Sqrt[1+(-1)*I]]*ArcTanh[(2-Sqrt[1+(-1)*I]-(1+2*Sqrt[1+(-1)*I])*Sqrt[1+x])/(2*Sqrt[(-1)*I+Sqrt[1+(-1)*I]]*Sqrt[x+Sqrt[1+x]])]-1/2*I*Sqrt[I+Sqrt[1+I]]*ArcTanh[(2-Sqrt[1+I]-(1+2*Sqrt[1+I])*Sqrt[1+x])/(2*Sqrt[I+Sqrt[1+I]]*Sqrt[x+Sqrt[1+x]])]" //
    );
  }

  public void test15() {
    check( //
        "Integrate[Sqrt[1+Sqrt[x]+Sqrt[1+2*Sqrt[x]+2*x]], x]", //
        "(2*Sqrt[1+Sqrt[x]+Sqrt[1+2*Sqrt[x]+2*x]]*(2+Sqrt[x]+6*x^(3/2)-(2-Sqrt[x])*Sqrt[1+2*Sqrt[x]+2*x]))/(15*Sqrt[x])" //
    );
  }

  public void test16() {
    check( //
        "Integrate[Sqrt[Sqrt[2]+Sqrt[x]+Sqrt[2+Sqrt[8]*Sqrt[x]+2*x]], x]", //
        "(2*Sqrt[2]*Sqrt[Sqrt[2]+Sqrt[x]+Sqrt[2]*Sqrt[1+Sqrt[2]*Sqrt[x]+x]]*(4+Sqrt[2]*Sqrt[x]+3*Sqrt[2]*x^(3/2)-Sqrt[2]*(2*Sqrt[2]-Sqrt[x])*Sqrt[1+Sqrt[2]*Sqrt[x]+x]))/(15*Sqrt[x])" //
    );
  }

  public void test17() {
    check( //
        "Integrate[Sqrt[x+Sqrt[1+x]]/x^2, x]", //
        "-Sqrt[x+Sqrt[1+x]]/x-ArcTan[(3+Sqrt[1+x])/(2*Sqrt[x+Sqrt[1+x]])]/4+3/4*ArcTanh[(1-3*Sqrt[1+x])/(2*Sqrt[x+Sqrt[1+x]])]" //
    );
  }

  public void test18() {
    check( //
        "Integrate[Sqrt[1/x+Sqrt[1+1/x]], x]", //
        "Sqrt[Sqrt[1+1/x]+1/x]*x+ArcTan[(3+Sqrt[1+1/x])/(2*Sqrt[Sqrt[1+1/x]+1/x])]/4-3/4*ArcTanh[(1-3*Sqrt[1+1/x])/(2*Sqrt[Sqrt[1+1/x]+1/x])]" //
    );
  }

  public void test19() {
    check( //
        "Integrate[Sqrt[1+E^(-x)]/(E^x-1/E^x), x]", //
        "-Sqrt[2]*ArcTanh[Sqrt[1+E^(-x)]/Sqrt[2]]" //
    );
  }

  public void test20() {
    check( //
        "Integrate[Sqrt[1+E^(-x)]/Sinh[x], x]", //
        "-2*Sqrt[2]*ArcTanh[Sqrt[1+E^(-x)]/Sqrt[2]]" //
    );
  }

  public void test21() {
    check( //
        "Integrate[1/(Cos[x]+Cos[3*x])^5, x]", //
        "-523/256*ArcTanh[Sin[x]]+(1483*ArcTanh[Sqrt[2]*Sin[x]])/(512*Sqrt[2])+Sin[x]/(32*(1-2*Sin[x]^2)^4)-(17*Sin[x])/(192*(1-2*Sin[x]^2)^3)+(203*Sin[x])/(768*(1-2*Sin[x]^2)^2)-(437*Sin[x])/(512*(1-2*Sin[x]^2))-43/256*Sec[x]*Tan[x]-1/128*Sec[x]^3*Tan[x]" //
    );
  }

  public void test22() {
    check( //
        "Integrate[1/(Cos[x]+Sin[x]+1)^2, x]", //
        "-Log[1+Tan[x/2]]-(Cos[x]-Sin[x])/(1+Cos[x]+Sin[x])" //
    );
  }

  public void test23() {
    check( //
        "Integrate[Sqrt[1+Tanh[4*x]], x]", //
        "ArcTanh[Sqrt[1+Tanh[4*x]]/Sqrt[2]]/(2*Sqrt[2])" //
    );
  }

  public void test24() {
    check( //
        "Integrate[Tanh[x]/Sqrt[E^(2*x)+E^x], x]", //
        "(2*Sqrt[E^x+E^(2*x)])/E^x-ArcTan[(I-(1-2*I)*E^x)/(2*Sqrt[1+I]*Sqrt[E^x+E^(2*x)])]/Sqrt[1+I]+ArcTan[(I+(1+2*I)*E^x)/(2*Sqrt[1+(-1)*I]*Sqrt[E^x+E^(2*x)])]/Sqrt[1+(-1)*I]" //
    );
  }

  public void test25() {
    check( //
        "Integrate[Sqrt[Sinh[2*x]/Cosh[x]], x]", //
        "(2*I*Sqrt[2]*EllipticE[Pi/4-1/2*I*x,2]*Sqrt[Sinh[x]])/Sqrt[I*Sinh[x]]" //
    );
  }

  public void test26() {
    check( //
        "Integrate[Log[x^2+Sqrt[1-x^2]], x]", //
        "-2*x-ArcSin[x]+Sqrt[1/2*(1+Sqrt[5])]*ArcTan[Sqrt[2/(1+Sqrt[5])]*x]+Sqrt[1/2*(1+Sqrt[5])]*ArcTan[(Sqrt[1/2*(1+Sqrt[5])]*x)/Sqrt[1-x^2]]+Sqrt[1/2*(-1+Sqrt[5])]*ArcTanh[Sqrt[2/(-1+Sqrt[5])]*x]-Sqrt[1/2*(-1+Sqrt[5])]*ArcTanh[(Sqrt[1/2*(-1+Sqrt[5])]*x)/Sqrt[1-x^2]]+x*Log[x^2+Sqrt[1-x^2]]" //
    );
  }

  public void test27() {
    check( //
        "Integrate[Log[1+E^x]/(1+E^(2*x)), x]", //
        "-1/2*Log[(1/2-I/2)*(I-E^x)]*Log[1+E^x]-1/2*Log[(-1/2-I/2)*(I+E^x)]*Log[1+E^x]-PolyLog[2,-E^x]-PolyLog[2,(1/2-I/2)*(1+E^x)]/2-PolyLog[2,(1/2+I/2)*(1+E^x)]/2" //
    );
  }

  public void test28() {
    check( //
        "Integrate[Log[1+Cosh[x]^2]^2*Cosh[x], x]", //
        "-8*Sqrt[2]*ArcTan[Sinh[x]/Sqrt[2]]+4*I*Sqrt[2]*ArcTan[Sinh[x]/Sqrt[2]]^2+8*Sqrt[2]*ArcTan[Sinh[x]/Sqrt[2]]*Log[(2*Sqrt[2])/(Sqrt[2]+I*Sinh[x])]+4*Sqrt[2]*ArcTan[Sinh[x]/Sqrt[2]]*Log[2+Sinh[x]^2]+4*I*Sqrt[2]*PolyLog[2,1-(2*Sqrt[2])/(Sqrt[2]+I*Sinh[x])]+8*Sinh[x]-4*Log[2+Sinh[x]^2]*Sinh[x]+Log[2+Sinh[x]^2]^2*Sinh[x]" //
    );
  }

  public void test29() {
    check( //
        "Integrate[Log[Sinh[x]+Cosh[x]^2]^2*Cosh[x], x]", //
        "-4*Sqrt[3]*ArcTan[(1+2*Sinh[x])/Sqrt[3]]-1/2*(1-I*Sqrt[3])*Log[1-I*Sqrt[3]+2*Sinh[x]]^2-(1+I*Sqrt[3])*Log[(I*(1-I*Sqrt[3]+2*Sinh[x]))/(2*Sqrt[3])]*Log[1+I*Sqrt[3]+2*Sinh[x]]-1/2*(1+I*Sqrt[3])*Log[1+I*Sqrt[3]+2*Sinh[x]]^2-(1-I*Sqrt[3])*Log[1-I*Sqrt[3]+2*Sinh[x]]*Log[-(I*(1+I*Sqrt[3]+2*Sinh[x]))/(2*Sqrt[3])]-2*Log[1+Sinh[x]+Sinh[x]^2]+(1-I*Sqrt[3])*Log[1-I*Sqrt[3]+2*Sinh[x]]*Log[1+Sinh[x]+Sinh[x]^2]+(1+I*Sqrt[3])*Log[1+I*Sqrt[3]+2*Sinh[x]]*Log[1+Sinh[x]+Sinh[x]^2]-(1+I*Sqrt[3])*PolyLog[2,-(I-Sqrt[3]+2*I*Sinh[x])/(2*Sqrt[3])]-(1-I*Sqrt[3])*PolyLog[2,(I+Sqrt[3]+2*I*Sinh[x])/(2*Sqrt[3])]+8*Sinh[x]-4*Log[1+Sinh[x]+Sinh[x]^2]*Sinh[x]+Log[1+Sinh[x]+Sinh[x]^2]^2*Sinh[x]" //
    );
  }

  public void test30() {
    check( //
        "Integrate[Log[x+Sqrt[1+x]]/(1+x^2), x]", //
        "1/2*I*Log[Sqrt[1+(-1)*I]-Sqrt[1+x]]*Log[x+Sqrt[1+x]]-1/2*I*Log[Sqrt[1+I]-Sqrt[1+x]]*Log[x+Sqrt[1+x]]+1/2*I*Log[Sqrt[1+(-1)*I]+Sqrt[1+x]]*Log[x+Sqrt[1+x]]-1/2*I*Log[Sqrt[1+I]+Sqrt[1+x]]*Log[x+Sqrt[1+x]]-1/2*I*Log[Sqrt[1+(-1)*I]+Sqrt[1+x]]*Log[(1-Sqrt[5]+2*Sqrt[1+x])/(1-2*Sqrt[1+(-1)*I]-Sqrt[5])]-1/2*I*Log[Sqrt[1+(-1)*I]-Sqrt[1+x]]*Log[(1-Sqrt[5]+2*Sqrt[1+x])/(1+2*Sqrt[1+(-1)*I]-Sqrt[5])]+1/2*I*Log[Sqrt[1+I]+Sqrt[1+x]]*Log[(1-Sqrt[5]+2*Sqrt[1+x])/(1-2*Sqrt[1+I]-Sqrt[5])]+1/2*I*Log[Sqrt[1+I]-Sqrt[1+x]]*Log[(1-Sqrt[5]+2*Sqrt[1+x])/(1+2*Sqrt[1+I]-Sqrt[5])]-1/2*I*Log[Sqrt[1+(-1)*I]+Sqrt[1+x]]*Log[(1+Sqrt[5]+2*Sqrt[1+x])/(1-2*Sqrt[1+(-1)*I]+Sqrt[5])]-1/2*I*Log[Sqrt[1+(-1)*I]-Sqrt[1+x]]*Log[(1+Sqrt[5]+2*Sqrt[1+x])/(1+2*Sqrt[1+(-1)*I]+Sqrt[5])]+1/2*I*Log[Sqrt[1+I]+Sqrt[1+x]]*Log[(1+Sqrt[5]+2*Sqrt[1+x])/(1-2*Sqrt[1+I]+Sqrt[5])]+1/2*I*Log[Sqrt[1+I]-Sqrt[1+x]]*Log[(1+Sqrt[5]+2*Sqrt[1+x])/(1+2*Sqrt[1+I]+Sqrt[5])]-1/2*I*PolyLog[2,(2*(Sqrt[1+(-1)*I]-Sqrt[1+x]))/(1+2*Sqrt[1+(-1)*I]-Sqrt[5])]-1/2*I*PolyLog[2,(2*(Sqrt[1+(-1)*I]-Sqrt[1+x]))/(1+2*Sqrt[1+(-1)*I]+Sqrt[5])]+1/2*I*PolyLog[2,(2*(Sqrt[1+I]-Sqrt[1+x]))/(1+2*Sqrt[1+I]-Sqrt[5])]+1/2*I*PolyLog[2,(2*(Sqrt[1+I]-Sqrt[1+x]))/(1+2*Sqrt[1+I]+Sqrt[5])]-1/2*I*PolyLog[2,-(2*(Sqrt[1+(-1)*I]+Sqrt[1+x]))/(1-2*Sqrt[1+(-1)*I]-Sqrt[5])]-1/2*I*PolyLog[2,-(2*(Sqrt[1+(-1)*I]+Sqrt[1+x]))/(1-2*Sqrt[1+(-1)*I]+Sqrt[5])]+1/2*I*PolyLog[2,-(2*(Sqrt[1+I]+Sqrt[1+x]))/(1-2*Sqrt[1+I]-Sqrt[5])]+1/2*I*PolyLog[2,-(2*(Sqrt[1+I]+Sqrt[1+x]))/(1-2*Sqrt[1+I]+Sqrt[5])]" //
    );
  }

  public void test31() {
    check( //
        "Integrate[Log[x+Sqrt[1+x]]^2/(1+x)^2, x]", //
        "Log[1+x]+(2*Log[x+Sqrt[1+x]])/Sqrt[1+x]-6*Log[Sqrt[1+x]]*Log[x+Sqrt[1+x]]-Log[x+Sqrt[1+x]]^2/(1+x)-(1+Sqrt[5])*Log[1-Sqrt[5]+2*Sqrt[1+x]]+6*Log[1/2*(-1+Sqrt[5])]*Log[1-Sqrt[5]+2*Sqrt[1+x]]+(3+Sqrt[5])*Log[x+Sqrt[1+x]]*Log[1-Sqrt[5]+2*Sqrt[1+x]]-1/2*(3+Sqrt[5])*Log[1-Sqrt[5]+2*Sqrt[1+x]]^2-(1-Sqrt[5])*Log[1+Sqrt[5]+2*Sqrt[1+x]]+(3-Sqrt[5])*Log[x+Sqrt[1+x]]*Log[1+Sqrt[5]+2*Sqrt[1+x]]-(3-Sqrt[5])*Log[-(1-Sqrt[5]+2*Sqrt[1+x])/(2*Sqrt[5])]*Log[1+Sqrt[5]+2*Sqrt[1+x]]-1/2*(3-Sqrt[5])*Log[1+Sqrt[5]+2*Sqrt[1+x]]^2-(3+Sqrt[5])*Log[1-Sqrt[5]+2*Sqrt[1+x]]*Log[(1+Sqrt[5]+2*Sqrt[1+x])/(2*Sqrt[5])]+6*Log[Sqrt[1+x]]*Log[1+(2*Sqrt[1+x])/(1+Sqrt[5])]+6*PolyLog[2,-(2*Sqrt[1+x])/(1+Sqrt[5])]-(3+Sqrt[5])*PolyLog[2,-(1-Sqrt[5]+2*Sqrt[1+x])/(2*Sqrt[5])]-(3-Sqrt[5])*PolyLog[2,(1+Sqrt[5]+2*Sqrt[1+x])/(2*Sqrt[5])]-6*PolyLog[2,1+(2*Sqrt[1+x])/(1-Sqrt[5])]" //
    );
  }

  public void test32() {
    check( //
        "Integrate[Log[x+Sqrt[1+x]]/x, x]", //
        "Log[-1+Sqrt[1+x]]*Log[x+Sqrt[1+x]]+Log[1+Sqrt[1+x]]*Log[x+Sqrt[1+x]]-Log[-1+Sqrt[1+x]]*Log[(1-Sqrt[5]+2*Sqrt[1+x])/(3-Sqrt[5])]-Log[1+Sqrt[1+x]]*Log[-(1-Sqrt[5]+2*Sqrt[1+x])/(1+Sqrt[5])]-Log[1+Sqrt[1+x]]*Log[-(1+Sqrt[5]+2*Sqrt[1+x])/(1-Sqrt[5])]-Log[-1+Sqrt[1+x]]*Log[(1+Sqrt[5]+2*Sqrt[1+x])/(3+Sqrt[5])]-PolyLog[2,(2*(1-Sqrt[1+x]))/(3-Sqrt[5])]-PolyLog[2,(2*(1-Sqrt[1+x]))/(3+Sqrt[5])]-PolyLog[2,(2*(1+Sqrt[1+x]))/(1-Sqrt[5])]-PolyLog[2,(2*(1+Sqrt[1+x]))/(1+Sqrt[5])]" //
    );
  }

  public void test33() {
    check( //
        "Integrate[ArcTan[2*Tan[x]], x]", //
        "x*ArcTan[2*Tan[x]]+1/2*I*x*Log[1-3*E^(2*I*x)]-1/2*I*x*Log[1-E^(2*I*x)/3]-PolyLog[2,E^(2*I*x)/3]/4+PolyLog[2,3*E^(2*I*x)]/4" //
    );
  }

  public void test34() {
    check( //
        "Integrate[ArcTan[x]*Log[x]/x, x]", //
        "1/2*I*Log[x]*PolyLog[2,(-1)*I*x]-1/2*I*Log[x]*PolyLog[2,I*x]-1/2*I*PolyLog[3,(-1)*I*x]+1/2*I*PolyLog[3,I*x]" //
    );
  }

  public void test35() {
    check( //
        "Integrate[ArcTan[x]^2*Sqrt[1+x^2], x]", //
        "ArcSinh[x]-Sqrt[1+x^2]*ArcTan[x]+1/2*x*Sqrt[1+x^2]*ArcTan[x]^2-I*ArcTan[E^(I*ArcTan[x])]*ArcTan[x]^2+I*ArcTan[x]*PolyLog[2,(-1)*I*E^(I*ArcTan[x])]-I*ArcTan[x]*PolyLog[2,I*E^(I*ArcTan[x])]-PolyLog[3,(-1)*I*E^(I*ArcTan[x])]+PolyLog[3,I*E^(I*ArcTan[x])]" //
    );
  }
}
