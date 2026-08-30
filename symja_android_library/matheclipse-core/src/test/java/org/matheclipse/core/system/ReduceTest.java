package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class ReduceTest extends ExprEvaluatorTestCase {

  @Test
  public void testReduce001() {
    check("Reduce({x > 1 && x < 5, x >= 5 && x < 8})", //
        "False");
  }

  @Test
  public void testReduceInequalityByExtrema() {
    // globally decided via Minimize/Maximize: min(x^2+1) == 1 > 0
    check("Reduce(x^2 + 1 > 0, x)", //
        "x∈Reals");
    check("Reduce(x^2 + 1 >= 0, x)", //
        "x∈Reals");
    check("Reduce(x^2 + 1 < 0, x)", //
        "False");
    check("Reduce(x^2 + 1 <= 0, x)", //
        "False");
    // max(-x^2 - 1) == -1 < 0
    check("Reduce(-x^2 - 1 < 0, x)", //
        "x∈Reals");
  }

  @Test
  public void testReduce002() {
    check("Reduce(x > 1 && x < 5 || x >= 5 && x < 8)", //
        "x>1&&x<8");
    check("Reduce(x > 1 && x < 5 || x >= 9/2 && x < 8)", //
        "x>1&&x<8");
    check("Reduce(x > 1 && x < 4 || x >= 9/2 && x < 8)", //
        "(x>1&&x<4)||(x>=9/2&&x<8)");
  }

  @Test
  public void testReduceXReals() {
    check("Reduce(x > 1 && x < 5 || x >= 5 && x < 8,x,Reals)", //
        "x>1&&x<8");
    check("Reduce(x > 1 && x < 5 || x >= 9/2 && x < 8,x,Reals)", //
        "x>1&&x<8");
    check("Reduce(x > 1 && x < 4 || x >= 9/2 && x < 8,x,Reals)", //
        "(x>1&&x<4)||(x>=9/2&&x<8)");
  }

  @Test
  public void testReduce003() {
    check("Reduce({x > 1, x > 2, x >= 5})", //
        "x>=5");
    check("Reduce({x > 1, x > 2, x > 5})", //
        "x>5");
  }

  @Test
  public void testReduce004() {
    check("Reduce({x < 1, x < 2, x <= 5})", //
        "x<1");
    check("Reduce({x <= 1, x < 2, x <= 5})", //
        "x<=1");
  }

  @Test
  public void testReduceEquals() {
    check("Reduce(x == 1 || x == 42)", //
        "x==1||x==42");
    check("Reduce({x == 7, x <= 7})", //
        "x==7");
    check("Reduce({x == 7, x < 7})", //
        "False");
    check("Reduce({x == 13/2, x <= 7})", //
        "x==13/2");
    check("Reduce({x == 13/2, x <= 5})", //
        "False");
    check("Reduce({x == 13/2, x >= 7})", //
        "False");
    check("Reduce({x == 13/2, x >= 5})", //
        "x==13/2");
    check("Reduce({x > 1, x > 2, x == 5})", //
        "x==5");
  }

  @Test
  public void testReduce005() {
    check("Reduce(x<1 &&x < 2 || x < 7 && x>1/2)", //
        "x<7");

    check("Reduce(x>1 &&x < 2 || x < 7 && x>1/2)", //
        "x>1/2&&x<7");
    check("Reduce(x<1 &&x < 2 )", //
        "x<1");

    check("Reduce(x > 1 && x < 4 || x >= 4)", //
        "x>1");
    check("Reduce(x < 2 || x < 7 && x>3)", //
        "x<2||(x>3&&x<7)");
    check("Reduce(x < 2 || x < 7 && x>1/2)", //
        "x<7");
  }

  @Test
  public void testReduce006() {
    check(
        "Reduce((x==1||x==-1||x==(-1)^(1/3)||x==-(-1)^(1/3)||x==(-1)^(2/3)||x==-(-1)^(2/3))&&x<0,x)", //
        "x==-1");
    check(
        "Reduce((x==1||x==-1||x==(-1)^(1/3)||x==-(-1)^(1/3)||x==(-1)^(2/3)||x==-(-1)^(2/3))&&x>0,x)", //
        "x==1");
    check("Reduce(x^6-1==0&&x>0,x)", //
        "x==1");
    check("Reduce(x^6-1==0,x,Reals)", //
        "x==-1||x==1");
    check("Reduce(x^6-1==0,x,Complexes)", //
        "x==-1||x==1||x==-(-1)^(1/3)||x==(-1)^(1/3)||x==-(-1)^(2/3)||x==(-1)^(2/3)");
    check("Reduce(x==1&&x>0,x)", //
        "x==1");

    check("Reduce(x>0&&x==(-1),x)", //
        "False");
    check("Reduce(x==(-1)&&x>0,x)", //
        "False");

    // complex values should return False
    check("Reduce(x==(-1)^(2/3)&&x>0,x)", //
        "False");
    // ommit variable
    check("Reduce(x^6-1==0&&x>0 )", //
        "x==1");
  }

  @Test
  public void testReduceQuadratic() {
    check("Reduce(a*x^2 + b*x + c == 0, x)", //
        "(a!=0&&(x==-b/(2*a)-Sqrt(b^2-4*a*c)/(2*a)||x==-b/(2*a)+Sqrt(b^2-4*a*c)/(2*a)))||(a==\n"
            + "0&&b!=0&&x==-c/b)||(a==0&&b==0&&c==0)");
    // parametric quadratic equation with a positivity constraint: left unevaluated
    // (previously returned the incorrect "x>0", which silently dropped the equation)
    check("Reduce(a*x^2 + b*x + c == 0&&x>0, x)", //
        "Reduce(c+b*x+a*x^2==0&&x>0,x)");
  }

  /**
   * A linear equation whose leading coefficient can vanish needs the same case analysis as a
   * quadratic one: the generic case, and the degenerate case in which the coefficient is zero.
   */
  @Test
  public void testReduceParametricLinear() {
    check("Reduce(a*x == 1, x)", //
        "a!=0&&x==1/a");
    // if the coefficient vanishes the equation degenerates to `0 == b`
    check("Reduce(a*x == b, x)", //
        "(a!=0&&x==b/a)||(a==0&&b==0)");
    check("Reduce(a*x + b == 0, x)", //
        "(a!=0&&x==-b/a)||(a==0&&b==0)");
    check("Reduce(a*x == 0, x)", //
        "(a!=0&&x==0)||a==0");
    check("Reduce((a + 1)*x == 2, x)", //
        "a!=-1&&x==2/(1+a)");
    check("Reduce(a*x + b*x == 1, x)", //
        "a+b!=0&&x==1/(a+b)");
    // a numeric leading coefficient cannot vanish, so no case analysis is generated
    check("Reduce(2*x == 1, x)", //
        "x==1/2");
    check("Reduce(x + 1 == 0, x)", //
        "x==-1");
    check("Reduce(0*x == 1, x)", //
        "False");
  }

  @Test
  public void testReduceMultivariate() {
    check("Reduce(x^2 - y^3 == 1, {x, y})", //
        "x==-Sqrt(1+y^3)||x==Sqrt(1+y^3)");
    check("Reduce(x + y == 1, {x, y})", //
        "x==1-y");
  }

  @Test
  public void testReduceComplexes() {
    check("Reduce(x^3==EulerGamma,x)", //
        "x==-(-EulerGamma)^(1/3)||x==EulerGamma^(1/3)||x==(-1)^(2/3)*EulerGamma^(1/3)");
    check("Reduce(x^6-1==0,x)", //
        "x==-1||x==1||x==-(-1)^(1/3)||x==(-1)^(1/3)||x==-(-1)^(2/3)||x==(-1)^(2/3)");
  }

  @Test
  public void testReduceConstant() {
    check("Reduce(x^3==EulerGamma,x,Reals)", //
        "x==EulerGamma^(1/3)");
  }

  @Test
  public void testReduceIntegersEquation() {
    check("Reduce(x^2 == 4, x, Integers)", //
        "x==-2||x==2");
    // no integer root
    check("Reduce(x^2 == 3, x, Integers)", //
        "False");
    // equation combined with an inequality constraint
    check("Reduce(x^2 == 4 && x > 0, x, Integers)", //
        "x==2");
  }

  @Test
  public void testReduceIntegersInterval() {
    check("Reduce(x > 0 && x < 4, x, Integers)", //
        "x==1||x==2||x==3");
  }

  @Test
  public void testReducePrimes() {
    check("Reduce(x > 1 && x < 10, x, Primes)", //
        "x==2||x==3||x==5||x==7");
  }

  @Test
  public void testReduceLinearDiophantine() {
    check("Reduce(2*x + 3*y == 1, {x, y}, Integers)", //
        "C(1)∈Integers&&x==-1+3*C(1)&&y==1-2*C(1)");
  }

  @Test
  public void testReduceElementInput() {
    check("Reduce(x > 0 && x < 4 && Element(x, Integers))", //
        "x==1||x==2||x==3");
  }

  @Test
  public void testReduceBooleans() {
    check("Reduce(p || ! p, {p}, Booleans)", //
        "True");
    check("Reduce(p && ! p, {p}, Booleans)", //
        "False");
    check("Reduce(p && q, {p, q}, Booleans)", //
        "p&&q");
  }

  @Test
  public void testReduceForAll() {
    check("Reduce(ForAll(x, x^2 + 1 > 0))", //
        "True");
  }

  @Test
  public void testReduceExists() {
    check("Reduce(Exists(x, x^2 == 4))", //
        "True");
  }

  @Test
  public void testReduceMultivariateSystem() {
    check("Reduce({x + y == 1, x - y == 3}, {x, y})", //
        "x==2&&y==-1");
    // all roots of the system are returned
    check("Reduce({x^2 == 4, y == 2}, {x, y})", //
        "(x==-2&&y==2)||(x==2&&y==2)");
    check("Reduce({x^3 == 1, y == 2}, {x, y}, Reals)", //
        "x==1&&y==2");
    check("Reduce({x^2 + y^2 == 1, x == y}, {x, y})", //
        "(x==-1/Sqrt(2)&&y==-1/Sqrt(2))||(x==1/Sqrt(2)&&y==1/Sqrt(2))");
    // inconsistent system
    check("Reduce({x == 1, x == 2}, {x, y})", //
        "False");
    check("Reduce({x + y == 1, x + y == 2}, {x, y})", //
        "False");
    // a variable which isn't determined by an equation stays free
    check("Reduce({x == y, y == z, z == x}, {x, y, z})", //
        "x==z&&y==z");
    // an inequation which isn't decided by the solution is kept
    check("Reduce(x == 1 && y > 0, {x, y})", //
        "x==1&&y>0");
    // a condition on a symbol which isn't reduced is kept
    check("Reduce({x == 1, y == 2, z == 3}, {x, y})", //
        "x==1&&y==2&&z==3");
    // `Roots` doesn't solve periodic equations, the univariate reduction does
    check("Reduce({Sin(x) == 0, y == 2}, {x, y})", //
        "(C(1)∈Integers&&x==2*Pi*C(1)&&y==2)||(C(1)∈Integers&&x==Pi+2*Pi*C(1)&&y==2)");
  }

  @Test
  public void testReduceListRelation() {
    check("Reduce({x, y} == {1, 2}, {x, y})", //
        "x==1&&y==2");
    check("Reduce({x, y} == {1, 2})", //
        "x==1&&y==2");
    check("Reduce({x, y} == {1, 2}, {x, y}, Reals)", //
        "x==1&&y==2");
    // nested lists are expanded recursively
    check("Reduce({{x, y}, {z, w}} == {{1, 2}, {3, 4}}, {x, y, z, w})", //
        "x==1&&y==2&&z==3&&w==4");
    check("Reduce(a + {x, y} == {1, 2}, {x, y})", //
        "x==1-a&&y==2-a");
    // lists of different length are not equal
    check("Reduce({x, y} == {1, 2, 3}, {x, y})", //
        "False");
    // Unequal expands into a disjunction
    check("Reduce({x, y} != {1, 2}, {x, y})", //
        "x!=1||y!=2");
    // an additional constraint filters the solutions
    check("Reduce({x, y} == {1, 2} && x > 0, {x, y})", //
        "x==1&&y==2");
    check("Reduce({x, y} == {1, 2} && x > 5, {x, y})", //
        "False");
    check("Reduce({x^2, y} == {4, 2}, {x, y})", //
        "(x==-2&&y==2)||(x==2&&y==2)");
  }

  @Test
  public void testReduceSideConditions() {
    // relations of a symbol which isn't reduced are kept, they are neither dropped nor folded
    // into the interval of the reduced variable
    check("Reduce({x, y} == {1, 2}, x)", //
        "x==1&&y==2");
    check("Reduce({x == 1, y == 2}, x)", //
        "x==1&&y==2");
    check("Reduce(x == 1 && y == 2, x)", //
        "x==1&&y==2");
    check("Reduce(x == 1 && y > 0, x)", //
        "x==1&&y>0");
    check("Reduce(x > 0 && y == 2, x)", //
        "x>0&&y==2");
    check("Reduce(x^2 == 4 && y == 2, x)", //
        "(x==-2||x==2)&&y==2");
    check("Reduce(x == 1 && y == 2 && x > 0, x)", //
        "x==1&&y==2");
    // nothing constrains the reduced variable
    check("Reduce(y == 2, x)", //
        "y==2");
    // the alternatives of a disjunction are reduced separately
    check("Reduce(x == 1 || y == 2, x)", //
        "x==1||y==2");
    check("Reduce((x == 1 && y == 2) || x == 5, x)", //
        "(x==1&&y==2)||x==5");
    // a disjunction whose alternatives all constrain the variable is still merged
    check("Reduce((x > 1 && x < 5) || (x >= 5 && x < 8), x)", //
        "x>1&&x<8");
  }

  @Test
  public void testReduceUnequal() {
    // `x != 1` splits into the real intervals `x < 1 || x > 1` only over the reals; over the
    // complexes the complement of a point cannot be ordered
    check("Reduce(x != 1, x)", //
        "x!=1");
    check("Reduce(x != 1, x, Complexes)", //
        "x!=1");
    check("Reduce(x != 1, x, Reals)", //
        "x<1||x>1");
    check("Reduce(x != I, x)", //
        "x!=I");
    check("Reduce(x != a, x)", //
        "x!=a");
    check("Reduce(x != 1 && x != 2, x)", //
        "x!=1&&x!=2");
    check("Reduce(x != 1 && x != 2, x, Reals)", //
        "x<1||(x>1&&x<2)||x>2");
    // an ordering in the input makes the variable real, so the intervals are valid
    check("Reduce(x != 1 && x > 0, x)", //
        "(x>0&&x<1)||x>1");
    // as does an explicit `Element` declaration
    check("Reduce(Element(x, Reals) && x != 1)", //
        "x<1||x>1");
    // the interval reasoning itself stays sound, only the ordered rendering is dropped
    check("Reduce(x != 1 || x == 1, x)", //
        "True");
    check("Reduce(x != 1 && x == 1, x)", //
        "False");
    check("Reduce(x != 2 && x^2 == 4, x)", //
        "x==-2");
    check("Reduce({x, y} != {1, 2}, x)", //
        "x!=1||y!=2");
  }

  @Test
  public void testReduceDisjunction() {
    // the roots of an equation in a disjunction were dropped instead of being solved
    check("Reduce(x^2 == 4 || x == 5, x)", //
        "x==-2||x==2||x==5");
    check("Reduce(x == 5 || x^2 == 4, x)", //
        "x==-2||x==2||x==5");
    check("Reduce(x^2 == 4 || x^2 == 9, x)", //
        "x==-3||x==-2||x==2||x==3");
    check("Reduce(x^2 == 4 || x == 2, x)", //
        "x==-2||x==2");
    check("Reduce(x^2 == 4 || x > 10, x)", //
        "x==-2||x==2||x>10");
    // a point which an inequation already contains adds nothing to the disjunction
    check("Reduce(x^2 == 4 || x != 1, x)", //
        "x!=1");
    check("Reduce(x == 2 || x != 1, x)", //
        "x!=1");
    check("Reduce(x == 1 || x != 2, x)", //
        "x!=2");
    // two different excluded values cover every value
    check("Reduce(x != 1 || x != 2, x)", //
        "True");
    check("Reduce(x == 1 || x == 2 || x != 1, x)", //
        "True");
  }

  /**
   * A relation between two lists holds componentwise. Every solver takes it apart through the same
   * splitter, so they agree on which systems they accept.
   */
  @Test
  public void testListRelationAcrossSolvers() {
    check("Reduce({x, y} == {1, 2}, {x, y})", //
        "x==1&&y==2");
    check("Solve({x, y} == {1, 2}, {x, y})", //
        "{{x->1,y->2}}");
    check("SolveValues({x, y} == {1, 2}, {x, y})", //
        "{{1,2}}");
    check("FindInstance({x, y} == {1, 2}, {x, y})", //
        "{{x->1,y->2}}");
    check("Eliminate({x, y} == {1, 2}, {y})", //
        "x==1");
    check("NSolve({x, y} == {1.5, 2.5}, {x, y})", //
        "{{x->1.5,y->2.5}}");
    // nested lists are split recursively
    check("Solve({{x, y}, {z, w}} == {{1, 2}, {3, 4}}, {x, y, z, w})", //
        "{{w->4,x->1,y->2,z->3}}");
    check("FindInstance({{x, y}, {z, w}} == {{1, 2}, {3, 4}}, {x, y, z, w})", //
        "{{w->4,x->1,y->2,z->3}}");
    // lists of different lengths aren't componentwise
    check("Solve({x, y} == {1, 2, 3}, {x, y})", //
        "{}");
  }

  @Test
  public void testReduceSolvedForm() {
    check("Reduce(x == 1 || y == 2, {x, y})", //
        "x==1||y==2");
    check("Reduce(x > 0 && y > 0, {x, y})", //
        "x>0&&y>0");
  }

  @Test
  public void testReduceAndOr() {
    check("{a = x > 1 && x < 5, b = x > 5 && x < 8}", //
        "{x>1&&x<5,x>5&&x<8}");
    check("Reduce(a&&b)", //
        "False");

    check("{a = x > 1 && x < 5, b = x >= 5 && x < 8}", //
        "{x>1&&x<5,x>=5&&x<8}");
    check("Reduce(a||b)", //
        "x>1&&x<8");
  }

  @Test
  public void testPeriodicFunctions() {
    check("Reduce(Sin(a*x)+b==0, x)", //
        "(a==0&&b==0)||(C(1)∈Integers&&a!=0&&(x==(-ArcSin(b)+2*Pi*C(1))/a||x==(Pi+ArcSin(b)+\n"
            + "2*Pi*C(1))/a))");
    check("Reduce(Tan(a*x)+b==0, x)", //
        "(a==0&&b==0)||(C(1)∈Integers&&a!=0&&x==(-ArcTan(b)+Pi*C(1))/a)");
  }

  @Test
  public void testReducePolynomialInequalityRoots() {
    // the sign analysis isolates the real roots with `Roots`; the non-real roots of the polynomial
    // don't split the real line
    check("Reduce(x^4 - 1 > 0, x)", //
        "x<-1||x>1");
    check("Reduce((x-1)*(x-2)*(x-3) <= 0, x)", //
        "x<=1||(x>=2&&x<=3)");
    check("Reduce(x^3 - 2 > 0, x)", //
        "x>2^(1/3)");
    // a polynomial whose real root has no radical form is isolated as a `Root` object
    check("Reduce(x^5 - x - 1 > 0, x)", //
        "x>Root(-1-#1+#1^5&,1,0)");
  }

  @Test
  public void testReduceIssue1413() {
    // Implement Reduce for inequality
    check("Reduce(3*x^2 - 3 < 0, x)", //
        "x>-1&&x<1");
    check("Reduce(3*x^4 - 3 < 0, x)", //
        "x>-1&&x<1");
    check("f := 2*(x - 1) < x + 3;", //
        "");
    check("Reduce(f,x)", //
        "x<5");
    check("Reduce(0 < x < 2&& 1 < x < 4, x)", //
        "x>1&&x<2");
    check("Reduce({0 < x < 2, 1 < x < 4}, x)", //
        "x>1&&x<2");
    // cubic inequality reduces over the reals even in the default domain
    check("Reduce(x^3-2*x+1<0,x)", //
        "x<-1/2-Sqrt(5)/2||(x>-1/2+Sqrt(5)/2&&x<1)");
  }

  @Test
  public void testReduceIssue1427() {
    // Implement Reduce for cubic polynomial inequality.
    // `(x>-1&&x<0)||x>1` is Symja's compact form of `-1 < x < 0 || x > 1`
    check("Reduce(4*x^3-4*x>0,x,Reals)", //
        "(x>-1&&x<0)||x>1");
    // inequalities are real-valued, so the same reduction happens in the default domain
    check("Reduce(4*x^3-4*x>0,x)", //
        "(x>-1&&x<0)||x>1");
  }

  @Test
  public void testReducePolynomialInequalityReals() {
    // non-strict relation includes the roots
    check("Reduce(4*x^3-4*x>=0,x,Reals)", //
        "(x>=-1&&x<=0)||x>=1");
    // opposite direction
    check("Reduce(4*x^3-4*x<0,x,Reals)", //
        "x<-1||(x>0&&x<1)");
    // even multiplicity root: sign doesn't change, strict `>` excludes the touching point x==1
    check("Reduce((x-1)^2*(x+2)>0,x,Reals)", //
        "(x>-2&&x<1)||x>1");
    // ... and the non-strict `>=` collapses to a single half-line
    check("Reduce((x-1)^2*(x+2)>=0,x,Reals)", //
        "x>=-2");
    // irrational (exact) roots
    check("Reduce(x^2-2>0,x,Reals)", //
        "x<-Sqrt(2)||x>Sqrt(2)");
    // higher degree (quintic) with five real roots
    check("Reduce(x^5-5*x^3+4*x>0,x,Reals)", //
        "(x>-2&&x<-1)||(x>0&&x<1)||x>2");
  }

  @Test
  public void testReduceContradiction() {
    // strict relations exclude the meeting point -> unsatisfiable
    check("Reduce(x<a&&x>a,x)", //
        "False");
    // non-strict relations include the root -> single point solution
    check("Reduce(x<=a&&x>=a,x)", //
        "x==a");
  }
}
