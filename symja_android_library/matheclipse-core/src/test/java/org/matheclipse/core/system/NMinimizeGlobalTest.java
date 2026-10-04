package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * <code>NMinimize</code> and <code>NMaximize</code> on functions with several local optima: the
 * answer is the global one, also where the local search from the origin ends in another.
 */
public class NMinimizeGlobalTest extends ExprEvaluatorTestCase {

  @Test
  public void testOneVariable() {
    // two minima: the one next to the origin, x == 1.13, is the higher one
    check("NMinimize(x^4 - 3*x^2 + x, x)", //
        "{-3.51391,{x->-1.30084}}");
    check("NMaximize(-(x^4 - 3*x^2 + x), x)", //
        "{3.51391,{x->-1.30084}}");
    // periodic: of the equal minima the one next to the origin
    check("NMinimize(Sin(x) + Sin(10*x/3), x)", //
        "{-1.9887,{x->-7.99813}}");
    // a minimum outside of the region in which the population search starts
    check("NMinimize((x - 40)^2 + Sin(x), x)", //
        "{0.583153,{x->40.46515}}");
    check("NMinimize((x - 1000000)^2, x)", //
        "{0.0,{x->1.*10^6}}");
  }

  @Test
  public void testSeveralVariables() {
    // Styblinski-Tang: 4 minima for 2 variables, the lowest at -2.9035 in every variable
    check("NMinimize(1/2*((a^4-16*a^2+5*a)+(b^4-16*b^2+5*b)), {a,b})", //
        "{-78.33233,{a->-2.90353,b->-2.90353}}");
    check("NMinimize(1/2*((a^4-16*a^2+5*a)+(b^4-16*b^2+5*b)+(c^4-16*c^2+5*c)+(d^4-16*d^2+5*d)+(e^4-16*e^2+5*e)), {a,b,c,d,e})", //
        "{-195.8308,{a->-2.90353,b->-2.90353,c->-2.90353,d->-2.90353,e->-2.90353}}");
    // Rastrigin with its minimum moved away from the origin, where the local search starts
    check("NMinimize(20 + ((a-1.3)^2-10*Cos(2*Pi*(a-1.3))) + ((b+2.2)^2-10*Cos(2*Pi*(b+2.2))), {a,b})", //
        "{0.0,{a->1.3,b->-2.2}}");
    // Griewank, moved
    check("NMinimize(1 + ((a-7)^2+(b+5)^2)/4000 - Cos(a-7)*Cos((b+5)/Sqrt(2)), {a,b})", //
        "{0.0,{a->7.0,b->-5.0}}");
    // drop-wave, moved
    check("NMinimize(-(1 + Cos(12*Sqrt((x-0.7)^2 + (y+0.4)^2)))/(0.5*((x-0.7)^2 + (y+0.4)^2) + 2), {x, y})", //
        "{-1.0,{x->0.7,y->-0.4}}");
    // one minimum: the result of the local search, unchanged
    check("NMinimize((x-1)^2 + (y+2)^2, {x, y})", //
        "{0.0,{x->1.0,y->-2.0}}");
  }

  @Test
  public void testUnbounded() {
    // no lower bound
    check("NMinimize(x^3 - x, x)", //
        "{-Infinity,{x->Indeterminate}}");
    // the infimum 0 is not taken
    check("NMinimize(Exp(x), x)", //
        "{-Infinity,{x->Indeterminate}}");
    check("NMaximize(-Exp(x), x)", //
        "{Infinity,{x->Indeterminate}}");
    check("NMinimize(x, x)", //
        "{-Infinity,{x->Indeterminate}}");
  }

  @Test
  public void testBox() {
    // Schwefel: the minimum 0 is at 420.9687 in every variable. The scan of the box left one
    // variable in the basin at -302.52, with the value 118.44
    check("NMinimize({2094.9144517 - (a*Sin(Sqrt(Abs(a))) + b*Sin(Sqrt(Abs(b))) + c*Sin(Sqrt(Abs(c))) + d*Sin(Sqrt(Abs(d))) + e*Sin(Sqrt(Abs(e)))), -500<=a<=500, -500<=b<=500, -500<=c<=500, -500<=d<=500, -500<=e<=500}, {a,b,c,d,e})", //
        "{0.0000153378,{a->420.9687,b->420.9687,c->420.9687,d->420.9687,e->420.9687}}");
    // Eggholder
    check("NMinimize({-(y + 47)*Sin(Sqrt(Abs(y + x/2 + 47))) - x*Sin(Sqrt(Abs(x - (y + 47)))), -512<=x<=512, -512<=y<=512}, {x, y})", //
        "{-959.6407,{x->512.0,y->404.2318}}");
  }

  @Test
  public void testIndexedVariables() {
    // the terms of a sum over indexed variables are written out as they are
    check("Sum((v(i)-i)^2, {i,1,3})", //
        "(1-v(1))^2+(2-v(2))^2+(3-v(3))^2");
    check("Sum(c(i, j), {i, 1, 2}, {j, 1, 2})", //
        "c(1,1)+c(1,2)+c(2,1)+c(2,2)");
    check("Sum(a(i)*x^i, {i, 0, 2})", //
        "a(0)+x*a(1)+x^2*a(2)");
    // so the minimum of a sum of squares is not a negative rounding error of the expanded polynomial
    check("FindMinimum(Sum((v(i)-i)^2, {i,1,3}), Table(v(i), {i,1,3}))", //
        "{0.0,{v(1)->1.0,v(2)->2.0,v(3)->3.0}}");
    check("NMinimize(Sum(v(i)^2 - 10*Cos(2*Pi*v(i)), {i,1,3}) + 30, Table(v(i), {i,1,3}))", //
        "{0.0,{v(1)->0.0,v(2)->0.0,v(3)->0.0}}");
  }

  /** The population search has random numbers of its own: a call gives the same answer again. */
  @Test
  public void testDeterministic() {
    String input = "NMinimize(1/2*((a^4-16*a^2+5*a)+(b^4-16*b^2+5*b)) + Sin(3*a*b), {a,b})";
    String first = evaluator.eval(input).toString();
    for (int i = 0; i < 5; i++) {
      assertEquals(first, evaluator.eval(input).toString());
    }
  }
}
