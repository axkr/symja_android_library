package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for <code>SolveValues</code>, which returns the values instead of a list of rules. */
public class SolveValuesTest extends ExprEvaluatorTestCase {

  @Test
  public void testSolveValuesNegativeLeadingCoefficient() {
    check("SolveValues((x^2+2)*(x^2-2)==0,x,Reals)", //
        "{-Sqrt(2),Sqrt(2)}");
  }

  @Test
  public void testSolveValuesBareSymbol() {
    // a bare symbol gives a flat list
    check("SolveValues(x^2==1,x)", //
        "{-1,1}");
    check("SolveValues(a*x==b,x)", //
        "{b/a}");
    check("SolveValues(x^2==-1,x)", //
        "{-I,I}");
    check("SolveValues(x^2+a*x+1==0,x)", //
        "{-a/2-Sqrt(-4+a^2)/2,-a/2+Sqrt(-4+a^2)/2}");
  }

  @Test
  public void testSolveValuesVariableList() {
    // a variable list gives one tuple per solution
    check("SolveValues(x^2==1,{x})", //
        "{{-1},{1}}");
    check("SolveValues({x==1},{x})", //
        "{{1}}");
  }

  @Test
  public void testSolveValuesSystemsOfEquations() {
    check("SolveValues({x+y==2,x-y==0},{x,y})", //
        "{{1,1}}");
    // the values are ordered like the variables, not like the rules of the solution
    check("SolveValues({x+y==2,x-y==0},{y,x})", //
        "{{1,1}}");
    check("SolveValues({x^2+y^2==1,y==x},{x,y})", //
        "{{-1/Sqrt(2),-1/Sqrt(2)},{1/Sqrt(2),1/Sqrt(2)}}");
  }

  @Test
  public void testSolveValuesDomainFilters() {
    check("SolveValues(x^2==1,x,Reals)", //
        "{-1,1}");
  }

  @Test
  public void testSolveValuesAbsNoSolutionsLost() {
    check("SolveValues(Abs(x)==2,x,Reals)", //
        "{-2,2}");
  }

  @Test
  public void testSolveValuesNoSolution() {
    check("SolveValues(x^2==-1,x,Reals)", //
        "{}");
    check("SolveValues(1==2,x)", //
        "{}");
  }

  @Test
  public void testSolveValuesOptions() {
    check("SolveValues(x^2==2,x,Modulus->7)", //
        "{3,4}");
    check("SolveValues(x^3-x==0,x,MaxRoots->2)", //
        "{-1,0}");
  }

  @Test
  public void testSolveValuesNeedsEquations() {
    // like Solve, SolveValues needs the equations to be written down:
    // SolveValues: -4+x^2 is not a quantified system of equations and inequalities.
    check("SolveValues(x^2-4,x)", //
        "SolveValues(-4+x^2,x)");
    check("SolveValues({x+y-3,x-y-1},{x,y})", //
        "SolveValues({-3+x+y,-1+x-y},{x,y})");
  }
}
