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
    // parametric quadratic equation with a positivity constraint: the reduction of the equation
    // needs a condition on the parameters which the interval engine can't express, so the equation
    // is kept beside the constraint instead of being dropped
    check("Reduce(a*x^2 + b*x + c == 0&&x>0, x)", //
        "x>0&&c+b*x+a*x^2==0");
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

  /**
   * A linear system over the integers is a lattice coset, reported with one fresh parameter per
   * degree of freedom. The basis is in Hermite normal form and the offset is reduced against it,
   * so the same solution set is always reported the same way.
   */
  @Test
  public void testReduceLinearDiophantine() {
    check("Reduce(2*x + 3*y == 1, {x, y}, Integers)", //
        "C(1)∈Integers&&x==2+3*C(1)&&y==-1-2*C(1)");
    // 2 x == 4 y is x == 2 t, y == t; it is not y == x/2, which is not an integer for odd x
    check("Reduce(2*x == 4*y, {x, y}, Integers)", //
        "C(1)∈Integers&&x==2*C(1)&&y==C(1)");
    check("Reduce(3*x + 5*y == 7, {x, y}, Integers)", //
        "C(1)∈Integers&&x==4+5*C(1)&&y==-1-3*C(1)");
    // the greatest common divisor of the coefficients does not divide the right hand side
    check("Reduce(2*x + 4*y == 5, {x, y}, Integers)", //
        "False");
    // a variable the equation never mentions stays itself under its own membership
    check("Reduce(2*x == 4, {x, y}, Integers)", //
        "y∈Integers&&x==2");
  }

  /**
   * More unknowns than equations means more degrees of freedom, and the parameters share one
   * membership condition. Enumerating a prefix of such a set, as the constraint solver did, is an
   * arbitrary answer to a question with infinitely many solutions.
   */
  @Test
  public void testReduceLinearSystem() {
    check("Reduce(2*x + 3*y == 5*z, {x, y, z}, Integers)", //
        "(C(1)|C(2))∈Integers&&x==C(1)&&y==C(1)+5*C(2)&&z==C(1)+3*C(2)");
    check("Reduce(x + y + z == 1, {x, y, z}, Integers)", //
        "(C(1)|C(2))∈Integers&&x==C(1)&&y==C(2)&&z==1-C(1)-C(2)");
    check("Reduce(2*x + 3*y == 5*z, {x, y, z, w}, Integers)", //
        "w∈Integers&&(C(1)|C(2))∈Integers&&x==C(1)&&y==C(1)+5*C(2)&&z==C(1)+3*C(2)");
    check("Reduce(x == 2*y && y == 3*z, {x, y, z}, Integers)", //
        "C(1)∈Integers&&x==6*C(1)&&y==3*C(1)&&z==C(1)");
    // a redundant equation does not change the solution set
    check("Reduce(x + 2*y == 1 && 2*x + 4*y == 2, {x, y}, Integers)", //
        "C(1)∈Integers&&x==1+2*C(1)&&y==-C(1)");
    // an inconsistent system is decided from the coefficients, not by searching
    check("Reduce(x + 2*y == 1 && 2*x + 4*y == 3, {x, y}, Integers)", //
        "False");
    check("Reduce(2*x + 3*y - 5*z == 1 && 3*x - 4*y + 7*z == 3, {x, y, z}, Integers)", //
        "C(1)∈Integers&&x==C(1)&&y==22-29*C(1)&&z==13-17*C(1)");
  }

  /**
   * An inequality beside the equations becomes a condition on the generated parameter. Without
   * that substitution the answer could only restate the input, or express one unknown as a
   * fraction of the others, which is not an integer for most values.
   */
  @Test
  public void testReduceLinearSystemWithInequality() {
    check("Reduce(2*x == 4*y && x >= 0, {x, y}, Integers)", //
        "C(1)∈Integers&&C(1)>=0&&x==2*C(1)&&y==C(1)");
    check("Reduce(3*x + 5*y == 1 && x > 0, {x, y}, Integers)", //
        "C(1)∈Integers&&C(1)>=0&&x==2+5*C(1)&&y==-1-3*C(1)");
  }

  /**
   * A quantifier over the integers is eliminated by Cooper's method. Deciding it over a continuum
   * instead answers `Exists(y, x == 2*y + 1)` with True, losing the parity condition on `x`.
   */
  @Test
  public void testReduceIntegerQuantifier() {
    check("Reduce(Exists(y, x == 2*y + 1), x, Integers)", //
        "x∈Integers&&Mod(x,2)==1");
    check("Reduce(Exists(y, x == 2*y), x, Integers)", //
        "x∈Integers&&Mod(x,2)==0");
    check("Reduce(Exists(y, x == 3*y + 2), x, Integers)", //
        "x∈Integers&&Mod(x,3)==2");
    check("Reduce(ForAll(y, y <= x || y > x), x, Integers)", //
        "True");
    // an equation with no integer solution is decided, not searched for
    check("Reduce(Exists(y, 2*y == 1), x, Integers)", //
        "False");
  }

  /**
   * A system whose constraints prove every variable bounded is enumerated completely. The bounds
   * come from the constraints, including those which only follow from a combination of them,
   * rather than from a fixed search interval.
   */
  @Test
  public void testReduceIntegerBoundedSystem() {
    check("Reduce(x + 2*y + 3*z == 0 && x > 0 && y > 0 && z > 0, {x, y, z}, Integers)", //
        "False");
    check("Reduce(2*x + 3*y + 5*z == 1 && x >= 0 && y >= 0 && z >= 0, {x, y, z}, Integers)", //
        "False");
    check("Reduce(0 <= x <= 10 && Mod(x, 3) == 1, x, Integers)", //
        "x==1||x==4||x==7||x==10");
    check("Reduce(1 <= x <= 4 && x != 2, x, Integers)", //
        "x==1||x==3||x==4");
    check("Reduce(Mod(x, 5) == 0 && 1 <= x <= 4, x, Integers)", //
        "False");
  }

  /**
   * Over the primes every variable is at least two, which is often what makes a system finite. It
   * also excludes the negative values a bare primality test accepts.
   */
  /**
   * A quadratic equation in two unknowns is reported completely only when its shape proves the
   * solution set finite. An ellipse encloses finitely many lattice points and a product of two
   * linear forms equal to a non zero constant has finitely many divisor pairs; a Pell equation, a
   * parabola and a pair of lines all carry infinite families, and used to be reported as the first
   * twenty of their solutions.
   */
  @Test
  public void testReduceQuadraticTwoVariables() {
    check("Reduce(x^2 + y^2 == 25, {x, y}, Integers)", //
        "(x==-5&&y==0)||(x==-4&&y==-3)||(x==-4&&y==3)||(x==-3&&y==-4)||(x==-3&&y==4)||(x==\n"
            + "0&&y==-5)||(x==0&&y==5)||(x==3&&y==-4)||(x==3&&y==4)||(x==4&&y==-3)||(x==4&&y==3)||(x==\n"
            + "5&&y==0)");
    check("Reduce(x^2 + y^2 == 3, {x, y}, Integers)", //
        "False");
    check("Reduce(x^2 - y^2 == 5, {x, y}, Integers)", //
        "(x==-3&&y==-2)||(x==-3&&y==2)||(x==3&&y==-2)||(x==3&&y==2)");
    check("Reduce(x*y == 6, {x, y}, Integers)", //
        "(x==-6&&y==-1)||(x==-3&&y==-2)||(x==-2&&y==-3)||(x==-1&&y==-6)||(x==1&&y==6)||(x==\n"
            + "2&&y==3)||(x==3&&y==2)||(x==6&&y==1)");
    // a Pell equation has infinitely many solutions
    check("Reduce(x^2 - 2*y^2 == 1, {x, y}, Integers)", //
        "Reduce(x^2-2*y^2==1,{x,y},Integers)");
    // so does a parabola
    check("Reduce(x^2 == y, {x, y}, Integers)", //
        "Reduce(x^2==y,{x,y},Integers)");
    // and so does each line of a degenerate pair
    check("Reduce(x*y == 0, {x, y}, Integers)", //
        "Reduce(x*y==0,{x,y},Integers)");
  }

  @Test
  public void testReducePrimesIsPositive() {
    check("Reduce(x + y == 10, {x, y}, Primes)", //
        "(x==3&&y==7)||(x==5&&y==5)||(x==7&&y==3)");
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

  /**
   * A condition of a conjunction which the interval reduction cannot absorb has to be kept in the
   * result - dropping it would answer a statement which wasn't reduced.
   */
  @Test
  public void testReduceConjunctionKeepsConditions() {
    // the second condition is unsatisfiable, so the conjunction is
    check("Reduce(x > 0 && x^2 + 1 < 0, x)", //
        "False");
    check("Reduce(x^2 + 1 < 0 && x > 0, x)", //
        "False");
    check("Reduce({x > 0, x^2 + 1 < 0}, x)", //
        "False");
    check("Reduce(x^2 == 4 && x^2 + 1 < 0, x)", //
        "False");
    // a condition which isn't reduced is neither dropped nor guessed
    check("Reduce(Sin(x) < 1/2 && x > 0, x)", //
        "x>0&&Sin(x)<1/2");
  }

  /**
   * Every root of an equation is a solution; the reduction must not keep only the principal branch
   * of an inverse function.
   */
  @Test
  public void testReduceAllRoots() {
    check("Reduce(Abs(x) == 1, x, Reals)", //
        "x==-1||x==1");
    // over the complexes `Abs(x)==1` is the unit circle, which the real reduction can't describe
    check("Reduce(Abs(x) == 1, x)", //
        "Abs(x)==1");
    check("Reduce(Log(x)^2 == 1, x, Reals)", //
        "x==1/E||x==E");
    check("Reduce(Sin(x)^2 == 1/4 && 0 < x < Pi, x)", //
        "x==Pi/6||x==5/6*Pi");
  }

  /**
   * `Roots` returns `False` for every equation it cannot solve, so only a polynomial equation may
   * be handed to it - a non polynomial one is solved with `Solve` instead.
   */
  @Test
  public void testReduceNonPolynomialEquation() {
    check("Reduce(Sqrt(x) == 2, x)", //
        "x==4");
    check("Reduce(Sqrt(x) == x - 2, x, Reals)", //
        "x==4");
    check("Reduce(Sqrt(x - 1) == 2, x, Reals)", //
        "x==5");
    check("Reduce(x^x == 4, x, Reals)", //
        "x==Log(4)/ProductLog(Log(4))");
    check("Reduce(E^(2*x) - 3*E^x + 2 == 0, x, Reals)", //
        "x==0||x==Log(2)");
    check("Reduce(Sin(x) == Cos(x), x)", //
        "C(1)∈Integers&&(x==-3/4*Pi+2*Pi*C(1)||x==Pi/4+2*Pi*C(1))");
  }

  /**
   * Over the reals only the real members of a periodic solution family are solutions: a family with
   * a real period needs a real offset, a family with an imaginary period contributes at most one
   * member.
   */
  @Test
  public void testReducePeriodicRealDomain() {
    check("Reduce(Sin(x) == 2, x, Reals)", //
        "False");
    check("Reduce(Cos(x) == 3, x, Reals)", //
        "False");
    check("Reduce(Cosh(x) == 1/2, x, Reals)", //
        "False");
    // the complex solutions are kept over the complexes
    check("Reduce(Sin(x) == 2, x, Complexes)", //
        "C(1)∈Integers&&(x==Pi-ArcSin(2)+2*Pi*C(1)||x==ArcSin(2)+2*Pi*C(1))");
    // an imaginary period leaves the members with `C(1)==0`
    check("Reduce(Sinh(x) == 1, x, Reals)", //
        "x==ArcSinh(1)");
    check("Reduce(Cosh(x) == 2, x, Reals)", //
        "x==-ArcCosh(2)||x==ArcCosh(2)");
    check("Reduce(Sinh(y) == 0, y, Reals)", //
        "y==0");
  }

  /**
   * An exponential equation has the same kind of periodic solution family as a trigonometric
   * one.
   */
  @Test
  public void testReduceExponentialEquation() {
    check("Reduce(E^x == 2, x)", //
        "C(1)∈Integers&&x==I*2*Pi*C(1)+Log(2)");
    check("Reduce(E^x == 2, x, Reals)", //
        "x==Log(2)");
    check("Reduce(E^x == 2 && x > 0, x)", //
        "x==Log(2)");
    check("Reduce(Exp(x) == -1, x, Reals)", //
        "False");
    // a power of a non zero base is never zero
    check("Reduce(E^x == 0, x)", //
        "False");
    check("Reduce(2^x == 8, x, Reals)", //
        "x==3");
  }

  /**
   * A periodic equation which is restricted to a bounded window has finitely many solutions, which
   * are enumerated and verified.
   */
  @Test
  public void testReducePeriodicRegion() {
    check("Reduce(Sin(x) == 1/2 && 0 < x < 2*Pi, x)", //
        "x==Pi/6||x==5/6*Pi");
    check("Reduce(Cos(x) == 1/2 && 0 < x < 2*Pi, x)", //
        "x==Pi/3||x==5/3*Pi");
    check("Reduce(Tan(x) == 1 && 0 < x < 2*Pi, x)", //
        "x==Pi/4||x==5/4*Pi");
    check("Reduce(Sin(x) == 0 && 0 <= x <= 2*Pi, x)", //
        "x==0||x==Pi||x==2*Pi");
    check("Reduce(Sin(x) == 0 && 0 < x < 10, x)", //
        "x==Pi||x==2*Pi||x==3*Pi");
    check("Reduce({Sin(x) == 0, 0 < x, x < 10}, x)", //
        "x==Pi||x==2*Pi||x==3*Pi");
    check("Reduce(Cos(2*x) == 0 && 0 < x < Pi, x)", //
        "x==Pi/4||x==3/4*Pi");
    check("Reduce(Sin(x) == 1/2 && -2*Pi < x < 2*Pi, x)", //
        "x==-11/6*Pi||x==-7/6*Pi||x==Pi/6||x==5/6*Pi");
    check("Reduce(Sin(x) == Cos(x) && 0 < x < 2*Pi, x)", //
        "x==Pi/4||x==5/4*Pi");
    check("Reduce(Sinh(x) == 2 && 0 < x < 10, x)", //
        "x==ArcSinh(2)");
    check("Reduce(Sinh(x) == 2 && x < 0, x)", //
        "False");
    // no member of the window solves the equation
    check("Reduce(Sin(x) == 2 && 0 < x < 2*Pi, x)", //
        "False");
    // an unbounded window can't be enumerated, so the family is kept beside the bound
    check("Reduce(Sin(x) == 1/2 && x > 0, x)", //
        "C(1)∈Integers&&(x==Pi/6+2*Pi*C(1)||x==5/6*Pi+2*Pi*C(1))&&x>0");
  }

  /**
   * A trigonometric inequality over a bounded window is decided cell by cell between the zeros of
   * its two sides.
   */
  @Test
  public void testReduceTrigInequalityRegion() {
    check("Reduce(Sin(x) > 1/2 && 0 < x < 2*Pi, x)", //
        "x>Pi/6&&x<5/6*Pi");
    check("Reduce(Sin(x) >= 1/2 && 0 < x < 2*Pi, x)", //
        "x>=Pi/6&&x<=5/6*Pi");
    check("Reduce(Sin(x) < 1/2 && 0 < x < 2*Pi, x)", //
        "(x>0&&x<Pi/6)||(x>5/6*Pi&&x<2*Pi)");
    check("Reduce(Cos(x) > 0 && 0 < x < 2*Pi, x)", //
        "(x>0&&x<Pi/2)||(x>3/2*Pi&&x<2*Pi)");
    check("Reduce(Sin(x) != 1/2 && 0 < x < 2*Pi, x)", //
        "(x>0&&x<Pi/6)||(x>Pi/6&&x<5/6*Pi)||(x>5/6*Pi&&x<2*Pi)");
    check("Reduce(Sin(x) > 2 && 0 < x < 2*Pi, x)", //
        "False");
    check("Reduce(Sin(x) > -2 && 0 < x < 2*Pi, x)", //
        "x>0&&x<2*Pi");
    check("Reduce(x^2 < 1 && Sin(x) > 0, x)", //
        "x>0&&x<1");
    // a pole is a sign change which isn't a zero, so a pole bearing head isn't decided this way
    check("Reduce(Tan(x) > 1 && 0 < x < 2*Pi, x)", //
        "x>0&&x<2*Pi&&Tan(x)>1");
  }

  /** An inequality whose two sides never meet is decided by the range of the function. */
  @Test
  public void testReduceInequalityByRange() {
    check("Reduce(Sin(x) <= 1, x, Reals)", //
        "x∈Reals");
    check("Reduce(Sin(x) > 1, x, Reals)", //
        "False");
    check("Reduce(Tanh(x) < 1, x, Reals)", //
        "x∈Reals");
    check("Reduce(E^x > 0, x, Reals)", //
        "x∈Reals");
    check("Reduce(E^x < 0, x, Reals)", //
        "False");
    // the function isn't defined at `x==0`, so the range alone doesn't prove the universal claim
    check("Reduce(1/x^2 > 0, x, Reals)", //
        "x<0||x>0");
  }

  /**
   * `Inequality` is a comparator function too, so a chained relation has to be recognized before
   * the chained comparators - its arguments alternate between values and relation heads.
   */
  @Test
  public void testReduceChainedInequality() {
    check("Reduce(-5 < 3*x + 7 <= 22, x)", //
        "x>-4&&x<=5");
    check("Reduce(-5 < 3*x + 7/x <= 22, x)", //
        "x>=1/3&&x<=7");
    check("Reduce(0 < x + 1 < 2, x)", //
        "x>-1&&x<1");
  }

  /** The poles of a rational function are breakpoints of its sign and never solutions. */
  @Test
  public void testReduceRationalInequality() {
    check("Reduce(1/x < 1, x, Reals)", //
        "x<0||x>1");
    check("Reduce(7/x < 22, x, Reals)", //
        "x<0||x>7/22");
    check("Reduce(1/x >= 0, x, Reals)", //
        "x>0");
    check("Reduce(1/x == 0, x, Reals)", //
        "False");
    check("Reduce(1/(x^2 - 1) < 0, x, Reals)", //
        "x>-1&&x<1");
    check("Reduce(1/(x^2 + 1) < 0, x, Reals)", //
        "False");
    check("Reduce((x - 1)/(x - 2) > 0, x, Reals)", //
        "x<1||x>2");
    check("Reduce(x + 1/x > 2, x, Reals)", //
        "(x>0&&x<1)||x>1");
  }

  /** A polynomial relation of any degree is reduced inside a boolean combination too. */
  @Test
  public void testReducePolynomialAtomsInBooleanCombination() {
    check("Reduce(x^2 > 1 || x < -5, x, Reals)", //
        "x<-1||x>1");
    check("Reduce(x^2 <= 1 || x >= 3, x, Reals)", //
        "(x>=-1&&x<=1)||x>=3");
    check("Reduce(x^3 - x > 0 && x < 2, x)", //
        "(x>-1&&x<0)||(x>1&&x<2)");
    check("Reduce(x^2 != 1, x, Reals)", //
        "x<-1||(x>-1&&x<1)||x>1");
    check("Reduce(x^2 < 5 && x > -3, x, Reals)", //
        "x>-Sqrt(5)&&x<Sqrt(5)");
    check("Reduce(x^2 - 2 > 0 && x^2 - 3 < 0, x)", //
        "(x>-Sqrt(3)&&x<-Sqrt(2))||(x>Sqrt(2)&&x<Sqrt(3))");
  }

  /**
   * A piecewise defined function of a real variable is reduced by the case analysis of its
   * branches.
   */
  @Test
  public void testReducePiecewiseFunctions() {
    check("Reduce(Abs(x) < 1, x, Reals)", //
        "x>-1&&x<1");
    check("Reduce(Abs(x - 1) > 2, x, Reals)", //
        "x<-1||x>3");
    check("Reduce(Abs(x - 2) + Abs(x - 3) == 1, x, Reals)", //
        "x>=2&&x<=3");
    check("Reduce(Abs(Abs(x) - 2) + Abs(Abs(x) - 5) == 3, x, Reals)", //
        "(x>=-5&&x<=-2)||(x>=2&&x<=5)");
    check("Reduce(Abs(x - 3) - Abs(x + 1) == -4, x, Reals)", //
        "x>=3");
    check("Reduce(Max(x, 1) > 2, x, Reals)", //
        "x>2");
    check("Reduce(Max(x, -x) < 3, x, Reals)", //
        "x>-3&&x<3");
    check("Reduce(Min(x, 1 - x) > 1/4, x, Reals)", //
        "x>1/4&&x<3/4");
    check("Reduce(Max(x^2 - 1, 1 - x^2) > 1/2, x, Reals)", //
        "x<-Sqrt(3/2)||(x>-1/Sqrt(2)&&x<1/Sqrt(2))||x>Sqrt(3/2)");
    check("Reduce(Sign(x - 1) < 0, x, Reals)", //
        "x<1");
    check("Reduce(UnitStep(x - 3) == 1, x, Reals)", //
        "x>=3");
    check("Reduce(Ramp(x) > 2, x, Reals)", //
        "x>2");
    check("Reduce(Clip(x, {-2, 2}) < 1, x, Reals)", //
        "x<1");
    check("Reduce(Boole(x > 0) + Boole(x > 1) == 2, x, Reals)", //
        "x>1");
    check("Reduce(UnitBox(x) == 1, x, Reals)", //
        "x>=-1/2&&x<=1/2");
    check("Reduce(Piecewise({{x^2, x > 0}}, -x) > 2, x, Reals)", //
        "x<-2||x>Sqrt(2)");
    // an ordering in the input makes the variable real
    check("Reduce(x^2 > 3 || Abs(x) < 1, x, Reals)", //
        "x<-Sqrt(3)||(x>-1&&x<1)||x>Sqrt(3)");
  }

  /**
   * A system is reduced by eliminating the variables which an equation determines uniquely first,
   * so that the back substitution doesn't collapse different solution branches into one.
   */
  @Test
  public void testReduceSystemEliminationOrder() {
    check("Reduce(x^2 + y^2 == 1 && x == 0, {x, y})", //
        "(x==0&&y==-1)||(x==0&&y==1)");
    // more than one equation in the reduced variable determines the parameters too
    check("Reduce(a*x == 1 && x == 2, x)", //
        "x==2&&a==1/2");
    check("Reduce(x^2 == 4 && x^3 == 8, x)", //
        "x==2");
  }

  /** A system whose relations each constrain a single variable is reduced variable by variable. */
  @Test
  public void testReduceMultivariateInequalities() {
    check("Reduce(x > 1 && x < 0 && y > 0, {x, y}, Reals)", //
        "False");
    check("Reduce(x^2 > 1 && y < 3, {x, y}, Reals)", //
        "(x<-1||x>1)&&y<3");
    check("Reduce(x > 0 && y > 0, {x, y})", //
        "x>0&&y>0");
    // a relation which couples the variables needs a multivariate engine, so it stays unevaluated
    check("Reduce(x + y < 1 && x > 0 && y > 0, {x, y}, Reals)", //
        "Reduce(x+y<1&&x>0&&y>0,{x,y},Reals)");
  }

  /**
   * An unbounded integer solution set is described by a ray instead of an enumeration, and carries
   * the domain membership: the ray alone would describe the real numbers beyond the bound too. A
   * finite set names its members and needs no membership.
   */
  @Test
  public void testReduceIntegerRays() {
    check("Reduce(x > 0, x, Integers)", //
        "x∈Integers&&x>=1");
    check("Reduce(x < 5, x, Integers)", //
        "x∈Integers&&x<=4");
    check("Reduce(x^2 > 1, x, Integers)", //
        "x∈Integers&&(x<=-2||x>=2)");
    check("Reduce(x^2 >= 9, x, Integers)", //
        "x∈Integers&&(x<=-3||x>=3)");
    check("Reduce(x^2 >= 0, x, Integers)", //
        "x∈Integers");
    // a residue class is unbounded too
    check("Reduce(Mod(x, 3) == 1, x, Integers)", //
        "x∈Integers&&Mod(x,3)==1");
    check("Reduce(Mod(2*x, 6) == 4, x, Integers)", //
        "x∈Integers&&Mod(x,3)==2");
    check("Reduce(Divisible(x, 3), x, Integers)", //
        "x∈Integers&&Mod(x,3)==0");
    // an irrational bound is rounded to the enclosed integers
    check("Reduce(x^2 < 5 && x > -3, x, Integers)", //
        "x==-2||x==-1||x==0||x==1||x==2");
    // an unbounded set of primes can't be described by a ray
    check("Reduce(x > 0, x, Primes)", //
        "Reduce(x>0,x,Primes)");
  }

  /** A system over a discrete domain is enumerated with `Solve`. */
  @Test
  public void testReduceIntegerSystem() {
    check("Reduce(x + y == 5 && x > 0 && y > 0, {x, y}, Integers)", //
        "(x==1&&y==4)||(x==2&&y==3)||(x==3&&y==2)||(x==4&&y==1)");
  }

  /** Equations are reduced over the rational numbers too. */
  @Test
  public void testReduceRationals() {
    check("Reduce(x^2 == 4, x, Rationals)", //
        "x==-2||x==2");
    check("Reduce(x^2 == 2, x, Rationals)", //
        "False");
    check("Reduce(2*x == 1, x, Rationals)", //
        "x==1/2");
    // the rational solutions of an inequality are dense, so they aren't described
    check("Reduce(x > 0, x, Rationals)", //
        "Reduce(x>0,x,Rationals)");
  }

  /** The condition which the quantifier elimination leaves is reduced in turn. */
  @Test
  public void testReduceQuantifierResult() {
    check("Reduce(ForAll(y, x^2 + y^2 >= 1), {x}, Reals)", //
        "x<=-1||x>=1");
    check("Reduce(Exists(y, x^2 + y^2 < 1), {x}, Reals)", //
        "x>-1&&x<1");
    check("Reduce(ForAll(y, y^2 + x^2 > 0), {x}, Reals)", //
        "x<0||x>0");
  }

  /**
   * The roots of a parametric equation aren't real for every value of the parameters, so over the
   * reals every case of the leading-coefficient analysis carries the condition under which its
   * roots are real.
   */
  @Test
  public void testReduceParametricEquationReals() {
    check("Reduce(x^2 == a, x, Reals)", //
        "a>=0&&(x==-Sqrt(a)||x==Sqrt(a))");
    check("Reduce(a*x^2 == 1, x, Reals)", //
        "a>0&&(x==-1/Sqrt(a)||x==1/Sqrt(a))");
    check("Reduce(x^2 + b*x + c == 0, x, Reals)", //
        "b^2-4*c>=0&&(x==-b/2-Sqrt(b^2-4*c)/2||x==-b/2+Sqrt(b^2-4*c)/2)");
    check("Reduce(a*x^2 + b*x + c == 0, x, Reals)", //
        "(a!=0&&b^2-4*a*c>=0&&(x==-b/(2*a)-Sqrt(b^2-4*a*c)/(2*a)||x==-b/(2*a)+Sqrt(b^2-4*a*c)/(\n"
            + "2*a)))||(a==0&&b!=0&&x==-c/b)||(a==0&&b==0&&c==0)");
    // a linear equation always has a real root, so only the leading coefficient is analyzed
    check("Reduce(a*x == b, x, Reals)", //
        "(a!=0&&x==b/a)||(a==0&&b==0)");
    // the parameters are real over the reals, which makes this discriminant trivially non negative
    check("Reduce(x^2 == a^2, x, Reals)", //
        "x==-a||x==a");
    // over the complexes every root is a solution, so no condition is generated
    check("Reduce(x^2 == a, x)", //
        "x==-Sqrt(a)||x==Sqrt(a)");
    // the condition isn't known for a cubic, so the equation stays unevaluated instead of
    // asserting roots which are non-real for some parameter values
    check("Reduce(x^3 == a, x, Reals)", //
        "x^3==a");
    // an equation without parameters is reduced exactly
    check("Reduce(x^2 == 4, x, Reals)", //
        "x==-2||x==2");
    check("Reduce(x^2 == EulerGamma, x, Reals)", //
        "x==-Sqrt(EulerGamma)||x==Sqrt(EulerGamma)");
  }

  /** The variables of the reduction have to be symbols. */
  @Test
  public void testReduceInvalidVariable() {
    check("Reduce(x == 1, 5)", //
        "Reduce(x==1,5)");
    check("Reduce(x == 1, {})", //
        "Reduce(x==1,{})");
  }
}
