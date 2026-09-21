package org.matheclipse.core.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** AlgebraicNumber objects, ToNumberField and the functions of number fields built on them. */
public class AlgebraicNumberTest extends ExprEvaluatorTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testAlgebraicNumber() {
    check("AlgebraicNumber(3,{1,2})", //
        "7");
    check("AlgebraicNumber(5/2,{3,2})", //
        "8");
    check("AlgebraicNumber((1+I)/2,{1,3})", //
        "AlgebraicNumber(1+I,{1,3/2})");
    check("AlgebraicNumber(Sqrt(2),{})", //
        "0");
    check("AlgebraicNumber(Sqrt(2),{5,0})", //
        "5");
    check("AlgebraicNumber(3^(1/5),{1,2,1,3,3,1})", //
        "AlgebraicNumber(Root(-3+#1^5&,1,0),{4,2,1,3,3})");
    check("AlgebraicNumber(Root(5*#^5+11*#+1&,1),{1,1,2})", //
        "AlgebraicNumber(Root(625+1375*#1+#1^5&,1,0),{1,1/5,2/25,0,0})");
    check("AlgebraicNumber(AlgebraicNumber(Root(-3+#1^3&,1),{1,2,1}),{1,1,2})", //
        "AlgebraicNumber(Root(-16-15*#1-3*#1^2+#1^3&,1,0),{1,1,2})");
    check("AlgebraicNumber(Sqrt(2)+Sqrt(5),{1,1/2})", //
        "AlgebraicNumber(Root(9-14*#1^2+#1^4&,4,0),{1,1/2,0,0})");
    // no algebraic generator
    check("AlgebraicNumber(x,{1,2})", //
        "AlgebraicNumber(x,{1,2})");
    check("AlgebraicNumber(Pi,{1,2})", //
        "AlgebraicNumber(Pi,{1,2})");
    check("AlgebraicNumber(Sqrt(2),{1,1/2})+AlgebraicNumber(Sqrt(2),{1,2})", //
        "AlgebraicNumber(Sqrt(2),{2,5/2})");
    check("AlgebraicNumber(Sqrt(2),{1,1/2})*AlgebraicNumber(Sqrt(2),{1,2})", //
        "AlgebraicNumber(Sqrt(2),{3,5/2})");
    check("1/AlgebraicNumber(Sqrt(2),{1,1/2})", //
        "AlgebraicNumber(Sqrt(2),{2,-1})");
    check("AlgebraicNumber(Sqrt(2),{1,1/2})^3", //
        "AlgebraicNumber(Sqrt(2),{5/2,7/4})");
    check("3*AlgebraicNumber(Sqrt(2),{1,2})", //
        "AlgebraicNumber(Sqrt(2),{3,6})");
    check("AlgebraicNumber(Sqrt(2),{1,2})^0", //
        "1");
    check("AlgebraicNumber(Sqrt(2),{1,1/2})-AlgebraicNumber(Sqrt(2),{1,1/2})", //
        "0");
    check("AlgebraicNumber(Sqrt(2),{1,1})+AlgebraicNumber(Sqrt(3),{1,1})", //
        "AlgebraicNumber(Sqrt(2),{1,1})+AlgebraicNumber(Sqrt(3),{1,1})");
    check("1+AlgebraicNumber(Root(#^3+#+1&,3),{1,2,1})^2", //
        "AlgebraicNumber(Root(1+#1+#1^3&,3,0),{-2,-1,5})");
    check("N(AlgebraicNumber(Sqrt(2)*I,{1,-1}))", //
        "1.0+I*(-1.41421)");
    check("N(AlgebraicNumber(Sqrt(2)*I,{1,-1}),30)", //
        "1+I*(-1.4142135623730950488016887242)");
    check("Abs(AlgebraicNumber(Sqrt(2),{0,-1}))", //
        "AlgebraicNumber(Sqrt(2),{0,1})");
    check("NumericQ(AlgebraicNumber(Sqrt(2),{1,2}))", //
        "True");
    check("AlgebraicNumber(I,{0,1})==AlgebraicNumber(I,{2,1})", //
        "False");
    check("AlgebraicNumber(I,{0,1})!=AlgebraicNumber(I,{2,1})", //
        "True");
    check("RootReduce(AlgebraicNumber(Root(#^3+#+1&,3),{1,2,1}))", //
        "Root(-1+10*#1-#1^2+#1^3&,3,0)");
    check("MinimalPolynomial(AlgebraicNumber(Root(#^3+#+1&,3),{1,2,1}),x)", //
        "-1+10*x-x^2+x^3");
    check("AlgebraicNumberQ(AlgebraicNumber(Sqrt(2),{1,2}))", //
        "True");
    // field arithmetic agrees with the numerical values
    check("a=AlgebraicNumber(2^(1/3),{1,-1,2});b=AlgebraicNumber(2^(1/3),{-3,1/2,5});Chop(N(a*b)-N(a)*N(b))", //
        "0");
    check("a=AlgebraicNumber(2^(1/3),{1,-1,2});a*(1/a)", //
        "1");
    check("a=AlgebraicNumber(2^(1/3),{1,-1,2});Chop(N(a^-2)-N(a)^-2)", //
        "0");
  }

  @Test
  public void testAlgebraicNumberPolynomial() {
    check("AlgebraicNumberPolynomial(2,x)", //
        "2");
    check("AlgebraicNumberPolynomial(1/2,x)", //
        "1/2");
    check("AlgebraicNumberPolynomial(AlgebraicNumber(Sqrt(2),{1,2}),x)", //
        "1+2*x");
    check("AlgebraicNumberPolynomial(AlgebraicNumber(Sqrt(2)+Sqrt(3),{1,2,3,4}),x)", //
        "1+2*x+3*x^2+4*x^3");
    check("AlgebraicNumberPolynomial(AlgebraicNumber(Sqrt(2),{1,0,3}),x)", //
        "7");
    check("AlgebraicNumberPolynomial({2,AlgebraicNumber(Sqrt(2),{1,2})},x)", //
        "{2,1+2*x}");
    check("AlgebraicNumberPolynomial(AlgebraicNumber(Sqrt(2),{1,2}),{x,y})", //
        "{1+2*x,1+2*y}");
    check("AlgebraicNumberPolynomial(Sqrt(2),x)", //
        "AlgebraicNumberPolynomial(Sqrt(2),x)");
    check("Attributes(AlgebraicNumberPolynomial)", //
        "{Listable,Protected}");
  }

  @Test
  public void testAlgebraicNumberDenominator() {
    check("AlgebraicNumberDenominator({1/3,3,-1/6,0,6/4})", //
        "{3,1,6,1,2}");
    check("AlgebraicNumberDenominator(1/Sqrt(3))", //
        "3");
    check("AlgebraicNumberDenominator((1+Sqrt(5))/2)", //
        "1");
    check("AlgebraicNumberDenominator(1/Sqrt(Sqrt(2)+3))", //
        "7");
    check("AlgebraicNumberDenominator(1/(1+Sqrt(3)))", //
        "2");
    check("AlgebraicNumberDenominator(2^(1/3)/2)", //
        "2");
    check("AlgebraicNumberDenominator((1+3*I)^(-1/3))", //
        "10");
    check("AlgebraicNumberDenominator(1/Sqrt(1+I))", //
        "2");
    check("AlgebraicNumberDenominator(Root(5-6*#1+3*#1^3&,1))", //
        "3");
    check("AlgebraicNumberDenominator(AlgebraicNumber(Sqrt(2),{1/5,1}))", //
        "5");
    // the leading coefficient of 25*x^2-10*x-49 is 25, the denominator only 5
    check("AlgebraicNumberDenominator(1/5+Sqrt(2))", //
        "5");
    check("AlgebraicNumberDenominator({Sqrt(2),1/Sqrt(2),1/3})", //
        "{1,2,3}");
    check("AlgebraicNumberDenominator(Pi)", //
        "AlgebraicNumberDenominator(Pi)");
    // n*a is an algebraic integer, and n is the least such
    check("AlgebraicIntegerQ(AlgebraicNumberDenominator((1+3*I)^(-1/3))*(1+3*I)^(-1/3))", //
        "True");
    check("AlgebraicIntegerQ((AlgebraicNumberDenominator((1+3*I)^(-1/3))-1)*(1+3*I)^(-1/3))", //
        "False");
    check("AlgebraicIntegerQ(AlgebraicNumberDenominator(1/Sqrt(Sqrt(2)+3))/Sqrt(Sqrt(2)+3))", //
        "True");
    check("Attributes(AlgebraicNumberDenominator)", //
        "{Listable,Protected}");
  }

  @Test
  public void testToNumberField() {
    check("ToNumberField(Sqrt(2),2^(1/4))", //
        "AlgebraicNumber(Root(-2+#1^4&,2,0),{0,0,1,0})");
    check("ToNumberField(2,1/2)", //
        "2");
    check("ToNumberField(Sqrt(3),Sqrt(2))", //
        "ToNumberField(Sqrt(3),Sqrt(2))");
    check("ToNumberField({Sqrt(2),I},All)[[2]]", //
        "AlgebraicNumber(Root(9-2*#1^2+#1^4&,4,0),{0,1/6,0,1/6})");
    check("ToNumberField({Sqrt(2),I},All)[[1,1]]", //
        "Root(9-2*#1^2+#1^4&,4,0)");
    check("ToNumberField(2^(1/3))", //
        "AlgebraicNumber(Root(-2+#1^3&,1,0),{0,1,0})");
    check("ToNumberField({1/2,Sqrt(5),(1+Sqrt(5))/2},Sqrt(5))", //
        "{1/2,AlgebraicNumber(Sqrt(5),{0,1}),AlgebraicNumber(Sqrt(5),{1/2,1/2})}");
    check("ToNumberField(1/(1+Sqrt(3)),Sqrt(3))", //
        "AlgebraicNumber(Sqrt(3),{-1/2,1/2})");
    // the object has the value of the number it expresses
    check("t=ToNumberField(E^(Pi*I/4),I*AlgebraicNumber(Sqrt(2),{1,2}));Chop(N(t)-N(E^(Pi*I/4)))", //
        "0");
    check("t=ToNumberField({2^(1/3),Sqrt(2)},All);Chop(N(t)-N({2^(1/3),Sqrt(2)}))", //
        "{0,0}");
  }

  @Test
  public void testNormTraceExtension() {
    check("AlgebraicNumberNorm(GoldenRatio)", //
        "-1");
    check("AlgebraicNumberTrace(GoldenRatio)", //
        "1");
    check("AlgebraicNumberNorm(E^(Pi*I/8))", //
        "1");
    check("AlgebraicNumberTrace(E^(Pi*I/8))", //
        "0");
    check("AlgebraicNumberNorm(AlgebraicNumber(Sqrt(2)*I,{1,2}))", //
        "9");
    check("AlgebraicNumberTrace(AlgebraicNumber(Sqrt(2)*I,{1,2}))", //
        "2");
    check("Options(AlgebraicNumberNorm)", //
        "{Extension->None}");
    check("AlgebraicNumberNorm(Sqrt(2),Extension->E^(Pi*I/4))", //
        "4");
    check("AlgebraicNumberNorm({2,Sqrt(5)},Extension->Sqrt(5))", //
        "{4,-5}");
    check("AlgebraicNumberNorm(2*Sqrt(5),Extension->Sqrt(5))", //
        "-20");
    check("AlgebraicNumberTrace(3,Extension->Sqrt(2)+Sqrt(3))", //
        "12");
    check("AlgebraicNumberTrace((1+Sqrt(2))/2,Extension->Sqrt(2))", //
        "1");
    check("AlgebraicNumberTrace(Sqrt(2),Extension->E^(Pi*I/4))", //
        "0");
    // Sqrt(3) is not in Q(Sqrt(2))
    check("AlgebraicNumberNorm(Sqrt(3),Extension->Sqrt(2))", //
        "AlgebraicNumberNorm(Sqrt(3),Extension->Sqrt(2))");
    check("AlgebraicNumberNorm(Sqrt(2),Extension->None)", //
        "-2");
  }

  @Test
  public void testAlgebraicIntegerQAndFriends() {
    check("AlgebraicIntegerQ(Root(#^3+#+1&,1))", //
        "True");
    check("AlgebraicIntegerQ(Root(3*#^3+2&,1))", //
        "False");
    check("AlgebraicIntegerQ({1,2})", //
        "False");
    check("AlgebraicIntegerQ(AlgebraicNumber(Sqrt(2),{1/2,1/2}))", //
        "False");
    check("FirstPosition(a+b,a+b)", //
        "{}");
    check("FirstPosition({a,b},c)", //
        "Missing(NotFound)");
    check("NumberFieldIntegralBasis({Sqrt(2),Sqrt(5)})", //
        "{{1,Sqrt(2)},{1,1/2*(1+Sqrt(5))}}");
  }

  @Test
  public void testMaximalOrder() {
    // field discriminants: Q(2^(1/3)), Q(Sqrt(2),Sqrt(3)), Q(zeta_8), Q(10^(1/3)), Q(zeta_9)
    check("NumberFieldDiscriminant /@ {2^(1/3),Sqrt(2)+Sqrt(3),E^(I*Pi/4),10^(1/3),E^(2*Pi*I/9)}", //
        "{-108,2304,256,-300,-19683}");
    // Dedekind's cubic, whose ring of integers is not Z[theta] for any theta
    check("NumberFieldDiscriminant(Root(#^3-#^2-2*#-8&,1))", //
        "-503");
    check("NumberFieldIntegralBasis(Root(#^3-#^2-2*#-8&,1))[[3]]", //
        "Root(-8-2*#1-#1^2+#1^3&,1,0)/2+Root(-8-2*#1-#1^2+#1^3&,1,0)^2/2");
    check("AlgebraicIntegerQ /@ NumberFieldIntegralBasis(Root(#^3-#^2-2*#-8&,1))", //
        "{True,True,True}");
    check("MinimalPolynomial((Root(#^3-#^2-2*#-8&,1)+Root(#^3-#^2-2*#-8&,1)^2)/2,x)", //
        "-8-10*x-3*x^2+x^3");
    check("NumberFieldIntegralBasis(2^(1/3))", //
        "{1,2^(1/3),2^(2/3)}");
    check("NumberFieldIntegralBasis(10^(1/3))", //
        "{1,10^(1/3),1/3+10^(1/3)/3+10^(2/3)/3}");
    check("NumberFieldIntegralBasis(E^(I*Pi/4))", //
        "{1,(1+I)/Sqrt(2),I,(-1+I)/Sqrt(2)}");
    check("NumberFieldIntegralBasis(Root(2+3*#^3&,2))", //
        "{1,3*Root(2+3*#1^3&,2,0),3*Root(2+3*#1^3&,2,0)^2}");
    check("AlgebraicIntegerQ /@ NumberFieldIntegralBasis(Sqrt(2)+Sqrt(3))", //
        "{True,True,True,True}");
    check("AlgebraicIntegerQ(NumberFieldIntegralBasis(Sqrt(2+Sqrt(3))) . {-1,2,2,3})", //
        "True");
    // the quadratic case is unchanged
    check("NumberFieldIntegralBasis(Sqrt(5))", //
        "{1,1/2*(1+Sqrt(5))}");
    // a Root object is a number: MinimalPolynomial declines rather than treating it as a variable
    check("MinimalPolynomial(Root(#^3+#+1&,1)+Sqrt(2),x)", //
        "MinimalPolynomial(Sqrt(2)+Root(1+#1+#1^3&,1,0),x)");
  }

}
