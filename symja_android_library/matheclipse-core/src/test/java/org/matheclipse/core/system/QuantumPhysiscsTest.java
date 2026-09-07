package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class QuantumPhysiscsTest extends ExprEvaluatorTestCase {

  @Test
  public void testClebschGordan() {
    check("ClebschGordan({j, m}, {j1, m1}, {j2, m2})", //
        "(-1)^(j-j1+m2)*Sqrt(1+2*j2)*ThreeJSymbol({j,m},{j1,m1},{j2,-m2})");
    // https://en.wikipedia.org/wiki/Table_of_Clebsch%E2%80%93Gordan_coefficients
    check("ClebschGordan({3/2, -3/2}, {3/2, 3/2}, {1, 0})", //
        "3/(2*Sqrt(5))");


    // print message "is not physical
    check("ClebschGordan({2, 1}, {2, 4}, {4, 2})", //
        "0");

    check("ClebschGordan({5, 0}, {4, 0}, {1, 0})", //
        "Sqrt(5/33)");
    check("ClebschGordan({1/2, -1/2}, {1/2, -1/2}, {1, -1})", //
        "1");
    check("ClebschGordan({1/2, -1/2}, {1, 0}, {1/2, -1/2})", //
        "-1/Sqrt(3)");

    check("With({j1 = 3, j2 = 2},\n" //
        + "  Table(Sum(\n" //
        + "    If(Abs(m1 + m2) > j || Abs(m1 + m2) > ji, 0, \n" //
        + "     ClebschGordan({j1, m1}, {j2, m2}, {j, \n" //
        + "        m1 + m2}) ClebschGordan({j1, m1}, {j2, m2}, {ji, \n" //
        + "        m1 + m2})), {m1, -j1, j1}, {m2, -j2, j2}), {j, Abs(j1 - j2), \n" //
        + "    j1 + j2}, {ji, Abs(j1 - j2), j1 + j2})) // MatrixForm", //
        "{{3,0,0,0,0},\n" //
            + " {0,5,0,0,0},\n" //
            + " {0,0,7,0,0},\n" //
            + " {0,0,0,9,0},\n" //
            + " {0,0,0,0,11}}");
    // the three angular momenta must couple: j1+j2+j3 has to be an integer
    // ClebschGordan: ClebschGordan({1/2,1/2},{1/2,-1/2},{1/2,0}) is not triangular.
    check("ClebschGordan({1/2, 1/2}, {1/2, -1/2}, {1/2, 0})", //
        "0");
    // ClebschGordan: ClebschGordan({1/2,1/2},{1,0},{1,1/2}) is not triangular.
    check("ClebschGordan({1/2, 1/2}, {1, 0}, {1, 1/2})", //
        "0");

    // a single symbolic projection is forced by the selection rule m1+m2==m3
    check("ClebschGordan({1, m}, {1, 0}, {2, 1})", //
        "Piecewise({{1/Sqrt(2),m==1}},0)");
    check("ClebschGordan({1, 1}, {1, m}, {2, 1})", //
        "Piecewise({{1/Sqrt(2),m==0}},0)");
    check("ClebschGordan({1, 1}, {1, 0}, {2, m})", //
        "Piecewise({{1/Sqrt(2),m==1}},0)");

    // an inexact argument evaluates numerically at its own precision
    check("ClebschGordan({3/2`40, -3/2}, {3/2, 3/2}, {1, 0})", //
        "0.6708203932499369089227521006193828706321");
  }

  @Test
  public void testSixJSymbol() {
    check("SixJSymbol({1, 2, 3}, {1, 2, 3})", //
        "1/105");
    check("SixJSymbol({1/2, 1/2, 1}, {5/2, 7/2, 3})", //
        "-1/Sqrt(21)");
    check("SixJSymbol({1, 2, 3}, {2, 1, 2})", //
        "1/(5*Sqrt(21))");
    checkNumeric("SixJSymbol({1.0, 2.0, 1.0}, {2,3,2})", //
        "0.043643578047198484");
    checkNumeric("SixJSymbol({1.0, 1.0, 2.0}, {5,7,6})", //
        "0.12403473458920847");

    // SixJSymbol: SixJSymbol({1,2,1},{4,5,Pi}) is not triangular.
    check("SixJSymbol({1, 2, 1}, {4,5,Pi})", //
        "0");
    // SixJSymbol: SixJSymbol({1,2,1},{4,5,12}) is not triangular.
    check("SixJSymbol({1, 2, 1}, {4,5,12})", //
        "0");

    check("SixJSymbol({1, 1, 2}, {5,7,6})", //
        "1/Sqrt(65)");
    check("SixJSymbol({1, 2, 1}, {2,3,2})", //
        "1/(5*Sqrt(21))");
    check("SixJSymbol({1, 2, 3}, {2, 1, 2})", //
        "1/(5*Sqrt(21))");
    check("SixJSymbol({1, 2, 3}, {1,2,2})", //
        "1/15");
    // the four triads must couple: {1/2,1,1} and {1/2,1/2,1/2} do not add up to an integer
    // SixJSymbol: SixJSymbol({1/2,1/2,1},{1,1,1}) is not triangular.
    check("SixJSymbol({1/2, 1/2, 1}, {1, 1, 1})", //
        "0");
    // SixJSymbol: SixJSymbol({1/2,1,1},{1,1,1}) is not triangular.
    check("SixJSymbol({1/2, 1, 1}, {1, 1, 1})", //
        "0");
    // SixJSymbol: SixJSymbol({1/2,1/2,1/2},{1,1,3/2}) is not triangular.
    check("SixJSymbol({1/2, 1/2, 1/2}, {1, 1, 3/2})", //
        "0");
    // SixJSymbol: SixJSymbol({0,1/2,1/2},{1/2,1/2,1/2}) is not triangular.
    check("SixJSymbol({0, 1/2, 1/2}, {1/2, 1/2, 1/2})", //
        "0");

    check("SixJSymbol({8, 8, 8}, {8, 8, 8})", //
        "-12219/965770");
    check("SixJSymbol({20, 20, 20}, {20, 20, 20})", //
        "-33188637458619/6598917336119836");

    // an inexact argument evaluates numerically at its own precision
    check("SixJSymbol({1`30, 2, 3}, {1, 2, 3})", //
        "0.0095238095238095238095238095238");
  }

  @Test
  public void testThreeJSymbol() {
    check("ThreeJSymbol({1, m}, {1, 0}, {1, 5})", //
        "0");
    check("ThreeJSymbol({1, m}, {1, 0}, {2, 1})", //
        "Piecewise({{-1/Sqrt(10),m==-1}},0)");
    check("ThreeJSymbol({1, m}, {1, 1}, {2, 1})", //
        "0");
    check("ThreeJSymbol({1, m}, {1, 0}, {1, 1})", //
        "Piecewise({{1/Sqrt(6),m==-1}},0)");
    check("ThreeJSymbol({5, 0}, {4, m}, {1, 0})", //
        "Piecewise({{-Sqrt(5/11)/3,m==0}},0)");
    check("ThreeJSymbol({2, 1}, {2, 4}, {4, 2})", //
        "0");

    checkNumeric("ThreeJSymbol({1.5, -1.5}, {3/2, 3/2}, {1, 0})", //
        "0.3872983346207417");
    checkNumeric("ThreeJSymbol({1/2, -1/2}, {1/2, -1/2}, {1.0, 1.0})", //
        "-0.5773502691896257");


    check("ThreeJSymbol({3/2, -3/2}, {3/2, 3/2}, {1, 0})", //
        "Sqrt(3/5)/2");
    check("ThreeJSymbol({1/2, -1/2}, {1/2, -1/2}, {1, 1})", //
        "-1/Sqrt(3)");

    check("ThreeJSymbol({2, 0}, {6, 0}, {4, 0})", //
        "Sqrt(5/143)");

    check("ThreeJSymbol({5, 0}, {4, 0}, {1, 0})", //
        "-Sqrt(5/11)/3");
    check("ThreeJSymbol({6, 0}, {4, 0}, {2, 0})", //
        "Sqrt(5/143)");
    check("ThreeJSymbol({2, 1}, {2, 2}, {4, -3})", //
        "-1/(3*Sqrt(2))");


    check("{j1, j2} = {3, 2};\n" //
        + "Table(Sum(\n" //
        + "  If(Abs(m1 + m2) > j || Abs(m1 + m2) > ji, 0, \n" //
        + "   Sqrt((2 j + 1) (2 ji + 1))*ThreeJSymbol({j1, m1}, {j2, \n" //
        + "      m2}, {j, -(m1 + m2)})*ThreeJSymbol({j1, m1}, {j2, \n" //
        + "      m2}, {ji, -(m1 + m2)})), {m1, -j1, j1}, {m2, -j2, j2}), {j, \n" //
        + "  Abs(j1 - j2), j1 + j2}, {ji, Abs(j1 - j2), j1 + j2})", //
        "{{3,0,0,0,0},{0,5,0,0,0},{0,0,7,0,0},{0,0,0,9,0},{0,0,0,0,11}}");
    // the three angular momenta must couple: j1+j2+j3 has to be an integer
    // ThreeJSymbol: ThreeJSymbol({1/2,1/2},{1,0},{1,-1/2}) is not triangular.
    check("ThreeJSymbol({1/2, 1/2}, {1, 0}, {1, -1/2})", //
        "0");
    // ThreeJSymbol: ThreeJSymbol({1/2,1/2},{5/2,-1/2},{5/2,0}) is not triangular.
    check("ThreeJSymbol({1/2, 1/2}, {5/2, -1/2}, {5/2, 0})", //
        "0");
    // every j-m has to be an integer
    // ThreeJSymbol: ThreeJSymbol({1/2,0},{1/2,0},{1,0}) is not physical.
    check("ThreeJSymbol({1/2, 0}, {1/2, 0}, {1, 0})", //
        "0");
    // ThreeJSymbol: ThreeJSymbol({1,1/2},{1,-1/2},{1,0}) is not physical.
    check("ThreeJSymbol({1, 1/2}, {1, -1/2}, {1, 0})", //
        "0");

    // a symbolic projection in the last column is forced too
    check("ThreeJSymbol({1, 0}, {1, 1}, {2, m})", //
        "Piecewise({{-1/Sqrt(10),m==-1}},0)");

    // an inexact argument evaluates numerically at its own precision
    check("ThreeJSymbol({2`30, 0}, {6, 0}, {4, 0})", //
        "0.186989398001691435561930024532");
  }


  @Test
  public void testWignerD() {
    // https://mathematica.stackexchange.com/questions/189787/definition-of-wignerd-function
    check("WignerD({1/2, 1/2, -1/2}, 0,b,0)", //
        "Sin(b/2)");
    check("WignerD({1/2, -1/2, 1/2}, 0,b,0)", //
        "-Sin(b/2)");
    check("WignerD({1/2, 1/2, -1/2}, b)", //
        "Sin(b/2)");
    check("WignerD({1/2, -1/2, 1/2}, b)", //
        "-Sin(b/2)");

    check("WignerD({3/2,-3/2,-3/2}, ps,th,ph)", //
        "Cos(th/2)^3/E^(I*3/2*ph+I*3/2*ps)");

    // not physical.
    check("WignerD({1, 1/2, 1/2}, ps,th,ph)", //
        "WignerD({1,1/2,1/2},ps,th,ph)");

    check("WignerD({1,0,1}, ps,th,ph)", //
        "-Sqrt(2)*E^(I*ph)*Cos(th/2)*Sin(th/2)");
    check("Table(WignerD({1/2, m1, m2}, ps, th, ph), {m1,-1/2,1/2}, {m2,-1/2,1/2})", //
        "{{Cos(th/2)/E^(I*1/2*ph+I*1/2*ps),-E^(I*1/2*ph-I*1/2*ps)*Sin(th/2)},"//
            + "{Sin(th/2)/E^(\n" //
            + "I*1/2*ph-I*1/2*ps),E^(I*1/2*ph+I*1/2*ps)*Cos(th/2)}}");
    check("WignerD({1, 0, 1}, 20.2*Degree, 30*Degree, 11.0*Degree)", //
        "-0.347058+I*(-0.0674612)");

    check("WignerD({1/2,-1/2,-1/2}, ps,th,ph)", //
        "Cos(th/2)/E^(I*1/2*ph+I*1/2*ps)");

    // TODO calculate numerically for unphysical parameters
    // check("WignerD({1, 1/2, 1/2}, 1.34, Pi/3, Pi/4)", //
    // "");

    // two angles are the Euler angles beta and gamma
    check("WignerD({1/2, 1/2, -1/2}, b, c)", //
        "Sin(b/2)/E^(I*1/2*c)");
    check("WignerD({2, 2, 2}, a, b, c)", //
        "E^(I*2*a+I*2*c)*Cos(b/2)^4");
    check("WignerD({0, 0, 0}, a, b, c)", //
        "1");
    // |m| <= j is required
    check("WignerD({1/2, 3/2, 1/2}, b)", //
        "WignerD({1/2,3/2,1/2},b)");
    // an inexact argument evaluates numerically at its own precision
    check("WignerD({1`30, 0, 1}, 0.5)", //
        "-0.339005049421044863954942262049");
  }


}
