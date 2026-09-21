package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.junit.jupiter.api.Tag;

/** Tests for statistical moment functions */
public class EliminateTest extends ExprEvaluatorTestCase {

  @Test
  public void testEliminate() {
    check("Eliminate({f == x^5 + y^5, a == x + y, b == x*y}, {x, y})", //
        "-a^5+5*a^3*b-5*a*b^2+f==0");
    // check("Eliminate({a0*x^p+a1*x^q==0},x)", //
    // "(-a1)*x^q == a0*x^p");

    // the same equation with both sides negated - `Resultant`, which eliminates the variables,
    // changes sign with the order of its arguments
    check("Eliminate({x^2 + y^2 + z^2 == 1, x - y + z == 2, x^3 - y^2 == z + 1}, {y, z})",
        "-18*x+4*x^2-28*x^3+8*x^4+4*x^5+4*x^6==-27");

    // an inequation is no equation to eliminate from, but it constrains the result
    check("Eliminate({x==y,y>2},{x})", //
        "y>2");

    check("Eliminate({(a*x + b)/(c*x + d)==y},x)", //
        "True");
    check("Eliminate({x == 2 + y, y == z}, y)", //
        "x-z==2");
    check("Eliminate({x == 2 + y, y == z}, {y,v})", //
        "x-z==2");
    check("Eliminate({2*x + 3*y + 4*z == 1, 9*x + 8*y + 7*z == 2}, z)", //
        "11/2*x+11/4*y==1/4");

    check("Eliminate({x == 2 + y^3, y^2 == z}, y)", //
        "x-z^(3/2)==2");

    // use evaluation step: Cos(ArcSin(y)) => Sqrt(1-y^2)
    check("Eliminate({Sin(x)==y, Cos(x) == z}, x)", //
        "Sqrt(1-y^2)-z==0");
    check("Eliminate({a^x==y, b^(2*x) == z}, x)", //
        "b^((2*Log(y))/Log(a))-z==0");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }

  /**
   * The equations may be given as a conjunction, and a member which is <code>True</code> or
   * <code>False</code> is a valid (if trivial) equation.
   */
  @Test
  public void testEliminateBooleanMembers() {
    check("Eliminate(x==2+y && y==z, y)", //
        "x-z==2");
    check("Eliminate({x==2+y && y==z}, y)", //
        "x-z==2");
    check("Eliminate(2*x+3*y+4*z==1 && 9*x+8*y+7*z==2, z)", //
        "11/2*x+11/4*y==1/4");
    // a tautology constrains nothing
    check("Eliminate({True, x==2+y, y==z}, y)", //
        "x-z==2");
    check("Eliminate({x==x, x==2+y, y==z}, y)", //
        "x-z==2");
    // a contradiction makes the whole system unsatisfiable
    check("Eliminate({False, x==2+y, y==z}, y)", //
        "False");
    check("Eliminate({1==2, x==2+y, y==z}, y)", //
        "False");
  }

  /**
   * The elimination is algebraic: raising an equation to a power gains roots, and Mathematica keeps
   * them. <code>Sqrt(x)==-1</code> has no solution, but squaring it gives <code>x==1</code>, so the
   * system below eliminates to <code>y==2</code> rather than to <code>False</code>.
   * <code>Solve</code> sorts such roots out by cross checking its solutions.
   */
  @Test
  public void testEliminateIsAlgebraic() {
    check("Eliminate({Sqrt(x)+1==0, y==x^3+x}, x)", //
        "y==2");
    check("Eliminate(Abs(x-1)==-1, x)", //
        "True");
    // the root of a solvable equation is the same
    check("Eliminate({Sqrt(x)-2==0, y==x^3+x}, x)", //
        "y==68");
    check("Eliminate({Sqrt(x)==-y, z==x^3+x}, x)", //
        "-y^2-y^6+z==0");
    // Solve keeps its empty solution set
    check("Solve(Sqrt(x)==-1,x)", //
        "{}");
    check("Solve({Sqrt(x)-2==0, y==2*x},{x,y})", //
        "{{x->4,y->8}}");
  }

  /**
   * <code>InverseFunctions</code> decides whether an inverse function may be applied,
   * <code>WorkingPrecision</code> evaluates the result numerically, and an option which isn't
   * supported leaves the expression unevaluated.
   */
  @Test
  public void testEliminateOptions() {
    // the inverse functions of Sin and Cos are used
    check("Eliminate(Sin(x^2-y)==1 && Cos(x-y^2)==2, y, InverseFunctions->True)", //
        "Cos(Pi^2/4-x-Pi*x^2+x^4)==2");
    // TODO `InverseFunctions->False` isn't honoured by the isolation of a unary function yet
    check("Eliminate(Sin(x^2-y)==1 && Cos(x-y^2)==2, y, InverseFunctions->False)", //
        "Cos(Pi^2/4-x-Pi*x^2+x^4)==2");
    // an algebraic elimination doesn't need them
    check("Eliminate(x^2+y^2+z^2==1 && x-y+z==2 && x^3-y^2==z+1, z, InverseFunctions->False)", //
        "x+x^3-y-y^2==3&&-4*x+2*x^2+4*y-2*x*y+2*y^2==-3");
    check("Eliminate(x^2+y^2+z^2==E && x-y+z==2 && x^3-y^2==z+Pi, z, WorkingPrecision->20)", //
        "x+x^3-y-y^2==5.1415926535897932384&&-4*x+2*x^2+4*y-2*x*y+2*y^2==-1.2817181715409547646");
    // TODO `Mode->Modular` eliminates over a residue class ring
    check("Eliminate(y==5*x+1 && 2*x+3*y==5 && x*y==7, y, Mode->Modular)", //
        "Eliminate(y==1+5*x&&2*x+3*y==5&&x*y==7,y,mode->modular)");
  }

  /** An inequation is kept as a constraint on the result. */
  @Test
  public void testEliminateInequations() {
    check("Eliminate(x*z==y && x^2+x^2*z^2==1, z)", //
        "x^2+y^2==1");
    check("Eliminate(x*z==y && x^2+x^2*z^2==1 && t!=0, z)", //
        "x^2+y^2==1&&t!=0");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testEliminateReferenceExamples() {
    check("Eliminate({x==2+y, y==z}, y)", //
        "x-z==2");
    check("Eliminate({f==x^5+y^5, a==x+y, b==x*y}, {x,y})", //
        "-a^5+5*a^3*b-5*a*b^2+f==0");
    check("Eliminate(2*x+3*y+4*z==1 && 9*x+8*y+7*z==2, z)", //
        "11/2*x+11/4*y==1/4");
    // Mathematica gives the equivalent pair -3-2*x+2*x^2+2*x^3+2*y-2*x*y==0 &&
    // 3-4*x+2*x^2+4*y-2*x*y+2*y^2==0
    check("Eliminate(x^2+y^2+z^2==1 && x-y+z==2 && x^3-y^2==z+1, z)", //
        "x+x^3-y-y^2==3&&-4*x+2*x^2+4*y-2*x*y+2*y^2==-3");
    check("Eliminate(x^2+y^2+z^2==1 && x-y+z==2 && x^3-y^2==z+1, {y,z})", //
        "-18*x+4*x^2-28*x^3+8*x^4+4*x^5+4*x^6==-27");
    // TODO Mathematica eliminates the radicals completely and gives a polynomial in x
    check("Eliminate(Sqrt(x)+Sqrt(y-1)==1 && 2*x^(1/3)+3*y^2==2, y)", //
        "Sqrt(-1+Sqrt(2-2*x^(1/3))/Sqrt(3))+Sqrt(x)==1");
    // Mathematica gives the equivalent 2*x-ArcCos(2)==Pi/2 || 2*x+ArcCos(2)==-Pi/2
    check("Eliminate(Sin(x+y)==1 && Cos(x-y)==2, y)", //
        "Sin(2*x)==2");
    // the common root condition of two polynomials, which `Resultant` gives as well
    check("Eliminate(a*x^3+(a^2-1)*x^2+(2*a-3)*x+4==0 && (1-a^2)*x^4-2*a*x+7*a^2-1==0, x)", //
        "a*(102+2270*a-4602*a^2-2549*a^3+9336*a^4-1749*a^5-7464*a^6+4122*a^7+3102*a^8-\n" //
            + "2372*a^9-546*a^10+623*a^11-49*a^13)==0");
    // the variable is eliminated with the equation of the lowest degree, so that no root is
    // needed: Mathematica gives the same 24-28*x+29*x^2+42*y-12*x*y+34*y^2==0
    check("Eliminate(x^2+y^2+z^2==1 && 2*x-3*y+5*z==7, z)", //
        "-28/25*x+29/25*x^2+42/25*y-12/25*x*y+34/25*y^2==-24/25");
  }
}
