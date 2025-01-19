package org.matheclipse.core.system;

import org.junit.Test;

public class QuantityTest extends ExprEvaluatorTestCase {

  @Test
  public void testQuantityList() {
    check("Quantity(Sqrt(Range(3)), \"Meters\")", //
        "{1[Meters],Sqrt(2)[Meters],Sqrt(3)[Meters]}");
  }

  @Test
  public void testQuantity() {
    check("Quantity(\"StandardAccelerationOfGravity\")", //
        "1[StandardAccelerationOfGravity]");
    check("1+(3+Quantity(1.2,\"m\"))", //
        "4+1.2[m]");
    // check(
    // "Quantity(8, \"Meters\")*(1+y_)", //
    // "8[Meters]*(1+y_)");

    // TODO return unevaluated and print "compat" message that types are incompatible
    check("Quantity(0, \"kg\") + Quantity(0, \"A\") + Quantity(0, \"m\")", //
        "0[A]+0[kg]+0[m]");

    check("N(Quantity(2/3,\"m\"))", //
        "0.666667[m]");

    check("1/Quantity(0,\"s\")^I", //
        "1/0[s]^I");
    check("Quantity(50, \"s\") + Quantity(1, \"min\")", //
        "110[s]");
    check("Quantity(1, \"min\") + Quantity(50, \"s\")", //
        "110[s]");
    check("Quantity(1, \"min\") + Quantity(120, \"min\")", //
        "121[min]");
    check("Quantity(1, \"min\") + Quantity(50, \"s\")", //
        "110[s]");
    check("Quantity(50, \"s\") + Quantity(1, \"min\") ", //
        "110[s]");

    check("Quantity(50, \"min\") + Quantity(1, \"s\")", //
        "3001[s]");

    check("Table(i, {i, Quantity(5, \"s\"), Quantity(1, \"m\"), Quantity(4, \"s\")})", //
        "Table(i,{i,Quantity(5,s),Quantity(1,m),Quantity(4,s)})");
    check("Table(i, {i, Quantity(5, \"s\"), Quantity(1, \"min\"), Quantity(4, \"s\")})", //
        "{5[s],9[s],13[s],17[s],21[s],25[s],29[s],33[s],37[s],41[s],45[s],49[s],53[s],57[s]}");
    check("Table(i, {i, Quantity(50, \"s\"), Quantity(1, \"min\") })", //
        "{50[s],51[s],52[s],53[s],54[s],55[s],56[s],57[s],58[s],59[s],60[s]}");
    check("Table(i, {i, Quantity(10, \"s\") })", //
        "{1[s],2[s],3[s],4[s],5[s],6[s],7[s],8[s],9[s],10[s]}");
    check("Quantity(1, \"min\")<=Quantity(60, \"s\")", //
        "True");

    check("Quantity(1, \"min\")>Quantity(50, \"s\")", //
        "True");
    check("Quantity(1, \"min\")>Quantity(60, \"s\")", //
        "False");
    check("Quantity(1, \"min\")>=Quantity(60, \"s\")", //
        "True");
    check("Quantity(60, \"s\")<=Quantity(1, \"min\")", //
        "True");
    check("Quantity(60, \"s\")<=Quantity(2, \"min\")", //
        "True");

    check("Quantity(1, \"min\")<Quantity(50, \"s\")", //
        "False");
    check("Quantity(1, \"min\")<Quantity(60, \"s\")", //
        "False");
    check("Quantity(1, \"min\")<=Quantity(60, \"s\")", //
        "True");

    // leave unevaluated because of different unit types
    check("Quantity(1,\"s\")==Quantity(1,\"m\")", //
        "1[s]==1[m]");

    check("Quantity(60, \"s\")==Quantity(1, \"min\")", //
        "True");
    check("Quantity(1, \"min\")==Quantity(60, \"s\")", //
        "True");

    check("Quantity(60, \"s\")!=Quantity(1, \"min\")", //
        "False");
    check("Quantity(1, \"min\")!=Quantity(60, \"s\")", //
        "False");

    check("Quantity(42, \"s\")!=Quantity(1, \"min\")", //
        "True");
    check("Quantity(42, \"min\")!=Quantity(60, \"s\")", //
        "True");

    // github #139
    check("-2+Quantity(1, \"ft\")", //
        "-2+1[ft]");
    check("Quantity(9.8, \"m\")/Quantity(1, \"s\")", //
        "9.8[m*s^-1]");
    check("Quantity(9.8, \"m\")/Quantity(0, \"s\")", //
        "ComplexInfinity[m*s^-1]");
    check("Quantity(0, \"s\")^(-1)", //
        "ComplexInfinity[s^-1]");
    check("2*Quantity(1, \"ft\")", //
        "2[ft]");
    check("0+Quantity(1, \"ft\")", //
        "1[ft]");

    check("0*Quantity(1, \"ft\")", //
        "0[ft]");

    check("Quantity(\"m\")", //
        "1[m]");
    check("Quantity(3.25, \"m *rad\")", //
        "3.25[m*rad]");
    check("Quantity(3, \"Hz^(-2)*N*m^(-1)\")", //
        "3[Hz^-2*N*m^-1]");
    check("0+Quantity(3, \"m\")", //
        "3[m]");
    check("0*Quantity(3, \"m\")", //
        "0[m]");
    check("1*Quantity(3, \"m\")", //
        "3[m]");
    check("Quantity(3, \"m\")", //
        "3[m]");
    check("Quantity(3, \"Meters\")", //
        "3[Meters]");
  }

  @Test
  public void testQuantityQ() {
    check("QuantityQ(Quantity(2, x))", //
        "False");
    check("QuantityQ(Quantity(3, \"m\"))", //
        "True");
    check("QuantityQ(Quantity(3, \"Meters\"))", //
        "True");
  }

  @Test
  public void testQuantityMagnitude() {
    check("QuantityMagnitude(Quantity(2000000000000/8896443230521, \"lbf\"), \"N\")", //
        "1");
    check("QuantityMagnitude(Quantity(1290320000/8896443230521, \"psi\"), \"Pa\")", //
        "1");
    check("QuantityMagnitude(Quantity(6.241509125883258*10^9, \"GeV\"), \"J\")", //
        "1.0");
    check("QuantityMagnitude(Quantity(360, \"deg\"), \"rad\")", //
        "2*Pi");
    check("QuantityMagnitude(Quantity(3.4, \"m\"))", //
        "3.4");
    check("QuantityMagnitude(Quantity(3.4, \"km\"), \"m\")", //
        "3400.0");
  }

  @Test
  public void testQuantityUnit() {
    check("QuantityUnit(Quantity(42, \"Kilograms\"))", //
        "Kilograms");
    check("QuantityUnit(Quantity(3.4, \"Meters\"))", //
        "Meters");
    check("QuantityUnit(Quantity(19.25, \"Acres\"))", //
        "Acres");
  }

}
