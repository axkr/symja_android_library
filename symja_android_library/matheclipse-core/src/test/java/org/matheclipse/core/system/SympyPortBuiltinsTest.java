package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for the builtin functions, which are based on the org.matheclipse.core.sympy package */
public class SympyPortBuiltinsTest extends ExprEvaluatorTestCase {

  @Test
  public void testPermutationFunctions() {
    check("InversePermutation(Cycles({{1,2,3}}))", //
        "Cycles({{1,3,2}})");
    check("InversePermutation({2,3,1})", //
        "{3,1,2}");
    check("PermutationPower(Cycles({{1,2,3,4}}), 2)", //
        "Cycles({{1,3},{2,4}})");
    check("PermutationPower(Cycles({{1,2,3,4}}), -1)", //
        "Cycles({{1,4,3,2}})");
    check("PermutationPower(Cycles({{1,2,3,4}}), 4)", //
        "Cycles({})");
    check("PermutationPower({2,3,1}, 2)", //
        "{3,1,2}");
    check("PermutationOrder(Cycles({{1,2,3},{4,5}}))", //
        "6");
    check("PermutationOrder(Cycles({}))", //
        "1");
    check("PermutationSupport(Cycles({{3,5},{1,7}}))", //
        "{1,3,5,7}");
    check("PermutationLength(Cycles({{3,5},{1,7}}))", //
        "4");
    check("PermutationMax(Cycles({{3,5},{1,7}}))", //
        "7");
    check("PermutationMin(Cycles({{3,5},{2,7}}))", //
        "2");
    check("PermutationMax(Cycles({}))", //
        "0");
    check("PermutationMin(Cycles({}))", //
        "Infinity");
    check("PermutationOrder(x)", //
        "PermutationOrder(x)");
  }

  @Test
  public void testGroupFunctions() {
    check("GroupOrder(SymmetricGroup(4))", //
        "24");
    check("GroupOrder(AlternatingGroup(5))", //
        "60");
    check("GroupOrder(DihedralGroup(6))", //
        "12");
    check("GroupOrder(CyclicGroup(7))", //
        "7");
    check("GroupOrder(AbelianGroup({2,3}))", //
        "6");
    check("GroupOrder(PermutationGroup({Cycles({{1,2}}), Cycles({{1,2,3}})}))", //
        "6");
    check("GroupOrder(SymmetricGroup(20))", //
        "2432902008176640000");
    check("GroupElements(SymmetricGroup(3))", //
        "{Cycles({}),Cycles({{2,3}}),Cycles({{1,2}}),Cycles({{1,2,3}}),Cycles({{1,3,2}}),Cycles({{\n"
            + "1,3}})}");
    check("GroupOrbits(PermutationGroup({Cycles({{1,2}}), Cycles({{3,4,5}})}))", //
        "{{1,2},{3,4,5}}");
    // confirmed with WMA
    check("GroupOrbits(PermutationGroup({Cycles({{1, 2}}), Cycles({{4, 5}})}))", //
        "{{1,2},{3},{4,5}}");
    check("GroupGenerators(SymmetricGroup(4))", //
        "{Cycles({{1,2}}),Cycles({{1,2,3,4}})}");
    check("GroupOrbits(PermutationGroup({Cycles({{1,2}}), Cycles({{3,4,5}})}), {4, 7})", //
        "{{3,4,5},{7}}");
    check("GroupOrder(GroupStabilizer(SymmetricGroup(4), 1))", //
        "6");
    check("GroupOrder(GroupStabilizer(SymmetricGroup(5), {1, 2}))", //
        "6");
    check("GroupElementQ(AlternatingGroup(4), Cycles({{1,2}}))", //
        "False");
    check("GroupElementQ(AlternatingGroup(4), Cycles({{1,2,3}}))", //
        "True");
    check("GroupMultiplicationTable(CyclicGroup(3))", //
        "{{1,2,3},{2,3,1},{3,1,2}}");
    // confirmed with WMA
    check("GroupGenerators(AlternatingGroup(4))", //
        "{Cycles({{1,2,3}}),Cycles({{2,3,4}})}");
    check("GroupGenerators(DihedralGroup(1))", //
        "{Cycles({{1,2}})}");
    check("GroupGenerators(DihedralGroup(2))", //
        "{Cycles({{1,2}}),Cycles({{3,4}})}");
    check("GroupOrder(DihedralGroup(2))", //
        "4");
    check("GroupGenerators(DihedralGroup(5))", //
        "{Cycles({{2,5},{3,4}}),Cycles({{1,2,3,4,5}})}");
    check("GroupGenerators(AbelianGroup({2, 3}))", //
        "{Cycles({{1,2}}),Cycles({{3,4,5}})}");
    check("GroupGenerators(CyclicGroup(4))", //
        "{Cycles({{1,2,3,4}})}");
    check("GroupOrder(g)", //
        "GroupOrder(g)");
  }

  @Test
  public void testIntegerReverse() {
    check("IntegerReverse(1234)", //
        "4321");
    // confirmed with WMA
    check("IntegerReverse(-123)", //
        "321");
    check("IntegerReverse(1200)", //
        "21");
    check("IntegerReverse(6, 2)", //
        "3");
    check("IntegerReverse(6, 2, 5)", //
        "12");
    check("IntegerReverse({12, 345})", //
        "{21,543}");
    check("IntegerReverse(x)", //
        "IntegerReverse(x)");
  }

  @Test
  public void testMinMaxValue() {
    check("MinValue(x^2-2*x+3, x)", //
        "2");
    check("MaxValue(-x^2+4, x)", //
        "4");
    check("MinValue({x+y, x^2+y^2<=1}, {x,y})", //
        "-Sqrt(2)");
  }

  @Test
  public void testEulerEquations() {
    check("EulerEquations(x'(t)^2/2 - x(t)^2/2, x(t), t)", //
        "-x(t)-x''(t)==0");
    check("VariationalD(x'(t)^2/2 - x(t)^2/2, x(t), t)", //
        "-x(t)-x''(t)");
    check("Simplify(VariationalD(y(x)*Sqrt(y'(x)), y(x), x) - (Sqrt(y'(x))/2+(y(x)*y''(x))/(4*y'(x)^(3/2))))", //
        "0");
    check("EulerEquations(D(u(t,x),t)^2/2 - D(u(t,x),x)^2/2, u(t,x), {t,x})", //
        "Derivative(0,2)[u][t,x]-Derivative(2,0)[u][t,x]==0");
  }

  @Test
  public void testRecurrenceTable() {
    check("RecurrenceTable({a(n+1)==3*a(n), a(1)==7}, a(n), {n, 1, 5})", //
        "{7,21,63,189,567}");
    check("RecurrenceTable({a(n)==a(n-1)+a(n-2), a(1)==1, a(2)==1}, a(n), {n, 1, 8})", //
        "{1,1,2,3,5,8,13,21}");
    check("RecurrenceTable({a(n)==a(n-1)+a(n-2), a(1)==1, a(2)==1}, a(n), {n, 5, 8})", //
        "{5,8,13,21}");
    check("RecurrenceTable({a(n+1)==n*a(n), a(1)==1}, a, {n, 1, 5})", //
        "RecurrenceTable({a(1+n)==n*a(n),a(1)==1},a,{n,1,5})");
    check("RecurrenceTable({a(n+1)==n*a(n), a(1)==1}, a(n), {n, 6})", //
        "{1,1,2,6,24,120}");
  }

  @Test
  public void testCountRoots() {
    check("CountRoots((x-1)^2*(x+2), x)", //
        "3");
    check("CountRoots(x^2+1, x)", //
        "0");
    check("CountRoots(x^3-x, {x, 0, 1})", //
        "2");
    check("CountRoots(x^3-x, {x, -1/2, 1/2})", //
        "1");
    check("CountRoots(x^2-2, {x, 0, 2})", //
        "1");
    check("CountRoots((x^2-2)*(x^2-3), {x, 1, 2})", //
        "2");
    check("CountRoots((x^2-2)^3*(x-5), {x, -Infinity, 2})", //
        "6");
    check("CountRoots(Sin(x), x)", //
        "CountRoots(Sin(x),x)");
  }

  @Test
  public void testIsolatingInterval() {
    check("IsolatingInterval(3/4)", //
        "{3/4,3/4}");
    check("{a,b}=IsolatingInterval(Sqrt(2)); a<Sqrt(2)<b", //
        "True");
    check("{a,b}=IsolatingInterval(Sqrt(2), 1/1000); {a<Sqrt(2)<b, b-a<=1/1000}", //
        "{True,True}");
    check("{a,b}=IsolatingInterval(-Sqrt(2), 1/1000); {a< -Sqrt(2)<b, b-a<=1/1000}", //
        "{True,True}");
  }

  @Test
  public void testFunctionMonotonicity() {
    check("FunctionMonotonicity(x^3, x)", //
        "1");
    check("FunctionMonotonicity(-x, x)", //
        "-1");
    check("FunctionMonotonicity(x^2, x)", //
        "Indeterminate");
    check("FunctionMonotonicity({x^2, x>0}, x)", //
        "1");
    check("FunctionMonotonicity(5, x)", //
        "0");
    check("FunctionMonotonicity({Log(x), x>0}, x)", //
        "1");
    check("FunctionMonotonicity(E^(-x), x)", //
        "-1");
    // x^3 has an isolated stationary point
    // confirmed with WMA
    check("FunctionMonotonicity(x^3, x, StrictInequalities->True)", //
        "1");
    check("FunctionMonotonicity(-x^3-x, x, StrictInequalities->True)", //
        "-1");
    // confirmed with WMA
    check("FunctionMonotonicity(5, x, StrictInequalities->True)", //
        "Indeterminate");
    check("FunctionMonotonicity(x^2, x, StrictInequalities->True)", //
        "Indeterminate");
    check("FunctionMonotonicity({x^2, x>0}, x, Reals, StrictInequalities->True)", //
        "1");
    check("FunctionMonotonicity(x^3, x, StrictInequalities->False)", //
        "1");
  }

  @Test
  public void testFunctionConvexity() {
    check("FunctionConvexity(x^2, x)", //
        "1");
    check("FunctionConvexity(-x^2, x)", //
        "-1");
    check("FunctionConvexity(x^3, x)", //
        "Indeterminate");
    check("FunctionConvexity(2*x+1, x)", //
        "0");
    check("FunctionConvexity(E^x, x)", //
        "1");
    check("FunctionConvexity({x^3, x>0}, x)", //
        "1");
    check("FunctionConvexity(x^4, x, StrictInequalities->True)", //
        "1");
    check("FunctionConvexity(-E^x, x, StrictInequalities->True)", //
        "-1");
    // confirmed with WMA
    check("FunctionConvexity(2*x+1, x, StrictInequalities->True)", //
        "Indeterminate");
  }

  @Test
  public void testFourier() {
    check("FourierCoefficient(t, t, 3)", //
        "-I*1/3");
    // confirmed with WMA: Piecewise[{{0, n == 0}}, ((I/2)*(-1)^n*Sqrt[Pi])/n]
    check("FourierCoefficient(t, t, 3, FourierParameters -> {0, 2})", //
        "-I*1/6*Sqrt(Pi)");
    check("f=FourierCoefficient(t, t, n, FourierParameters -> {0, 2});"
        + "FullSimplify(Table(f, {n,0,4}) - Table(Piecewise({{0, n == 0}}, ((I/2)*(-1)^n*Sqrt(Pi))/n), {n,0,4}))", //
        "{0,0,0,0,0}");
    check("f=FourierCoefficient(t, t, n);"
        + "FullSimplify(Table(f, {n,0,4}) - Table(Piecewise({{0, n == 0}}, I*(-1)^n/n), {n,0,4}))", //
        "{0,0,0,0,0}");
    check("FullSimplify(ExpToTrig(FourierSeries(t, t, 2)) - (2*Sin(t)-Sin(2*t)))", //
        "0");
    check("FullSimplify(FourierTrigSeries(t, t, 3) - (2*Sin(t)-Sin(2*t)+2/3*Sin(3*t)))", //
        "0");
    check("FourierSinCoefficient(t, t, 2)", //
        "-1");
    // confirmed with WMA
    check("FourierCosCoefficient(t^2, t, 0)", //
        "2/3*Pi^2");
    // confirmed with WMA
    check("FullSimplify(FourierSeries(t, t, 1, FourierParameters -> {0, 2}) - (1/2*I*E^(-2*I*t) - 1/2*I*E^(2*I*t)))", //
        "0");
    // confirmed with WMA
    check("FourierCosCoefficient(t^2, t, 2, FourierParameters -> {0, 2})", //
        "Sqrt(Pi)/8");
    // confirmed with WMA
    check("FourierCosCoefficient(t^2, t, 2, FourierParameters -> {1, 2})", //
        "1/4");
    // confirmed with WMA: Sqrt(Pi/6)/9
    check("FullSimplify(FourierCosCoefficient(t^2, t, 2, FourierParameters -> {0, 3}) - Sqrt(Pi/6)/9)", //
        "0");
    check("FourierCosCoefficient(t^2, t, 2)", //
        "1");
    check("FullSimplify(FourierSinSeries(1, t, 3) - (4*Sin(t)/Pi + 4*Sin(3*t)/(3*Pi)))", //
        "0");
    check("FullSimplify(FourierCosSeries(t, t, 3) - (Pi/2 - 4*Cos(t)/Pi - 4*Cos(3*t)/(9*Pi)))", //
        "0");
  }

  @Test
  public void testKelvinKerKei() {
    check("KelvinKer(1.0)", //
        "0.286706");
    check("KelvinKei(1.0)", //
        "-0.494995");
    check("KelvinKer(1, 2.5)", //
        "-0.117256");
    check("KelvinKei(0.5, 1.5)", //
        "-0.278159");
    // confirmed with WMA: -3.48787*10^-16 - 2.93529*I
    check("Chop(KelvinKer(0.5, -1.5) - (-2.93529*I), 10^-5)", //
        "0");
    check("Chop(KelvinKei(0.5, -1.5) - 0.346306*I, 10^-5)", //
        "0");
    check("Chop(KelvinKer(0.5, -2.0+I) - (-1.383077-4.040214*I), 10^-5)", //
        "0");
    // integer order
    // confirmed with WMA: 0.0529349 - 2.89363*I
    check("Chop(KelvinKer(0, -1.5) - (0.0529349-2.89363*I), 10^-5)", //
        "0");
    check("Chop(KelvinKei(0, -1.5) - (-0.331396-1.751622*I), 10^-4)", //
        "0");
    check("Chop(KelvinKer(1, -1.5) - (0.417044-2.088735*I), 10^-4)", //
        "0");
    check("Chop(KelvinKer(0, 1.0+2.0*I) - (-0.816276-0.146672*I), 10^-4)", //
        "0");
    check("Chop(KelvinKer(0, -2.0+I) - (1.034178-3.424123*I), 10^-4)", //
        "0");
    check("Chop(KelvinKei(2, -1.0-2.0*I) - (0.867678+0.703619*I), 10^-4)", //
        "0");
    check("KelvinKer(n, z)", //
        "KelvinKer(n,z)");
  }

  @Test
  public void testAiryZeros() {
    check("AiryAiZero(1)", //
        "AiryAiZero(1)");
    check("N(AiryAiZero(1))", //
        "-2.33811");
    check("N(AiryBiZero(2))", //
        "-3.27109");
    check("AiryAiZero(2.0)", //
        "-4.08795");
    check("AiryAi(N(AiryAiZero(3), 30)) // Chop", //
        "0");
  }
}
