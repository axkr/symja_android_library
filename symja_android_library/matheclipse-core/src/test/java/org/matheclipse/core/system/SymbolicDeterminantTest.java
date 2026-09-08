package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Tests for the division-free (memoized Laplace expansion) symbolic {@code Det}, {@code Inverse}
 * and {@code Adjugate}.
 *
 * <p>
 * Two things are checked for every kind of entry: the defining algebraic identities
 * (<code>Inverse(m).m == I</code> and <code>Adjugate(m).m == Det(m)*I</code>), and the shape of the
 * answer. The shape matters because an elimination based method has to divide by a pivot, and for
 * a symbolic entry that division is not exact, so the result picks up denominators and functions
 * that the determinant itself does not have.
 */
public class SymbolicDeterminantTest extends ExprEvaluatorTestCase {

  // ---------------------------------------------------------------- identities

  @Test
  public void testInverseIdentityPolynomial() {
    check("m = {{1+x,1,0,0},{1,1+x,1,0},{0,1,1+x,1},{0,0,1,1+x}}; "
        + "Union(Flatten(Together(Inverse(m).m - IdentityMatrix(4))))", //
        "{0}");
  }

  @Test
  public void testInverseIdentityRationalFunction() {
    check("m = {{1/x,1,0,1},{1,1/y,1,0},{0,1,1/z,1},{1,0,1,1/w}}; "
        + "Union(Flatten(Together(Inverse(m).m - IdentityMatrix(4))))", //
        "{0}");
  }

  @Test
  public void testInverseIdentityRadical() {
    check("m = {{Sqrt(2),1,0,1},{1,Sqrt(3),1,0},{0,1,Sqrt(5),1},{1,0,1,Sqrt(7)}}; "
        + "Union(Flatten(Simplify(Inverse(m).m - IdentityMatrix(4))))", //
        "{0}");
  }

  @Test
  public void testInverseIdentityTrigonometric() {
    check("m = {{Sin(x),Cos(x),1,0},{-Cos(x),Sin(x),0,1},{1,0,Sin(y),Cos(y)},{0,1,-Cos(y),Sin(y)}}; "
        + "Union(Flatten(Simplify(Inverse(m).m - IdentityMatrix(4))))", //
        "{0}");
  }

  @Test
  public void testInverseIdentityGaussianRational() {
    check("m = {{I,x,1},{1,2,I},{0,I,x}}; "
        + "Union(Flatten(Together(Inverse(m).m - IdentityMatrix(3))))", //
        "{0}");
  }

  @Test
  public void testAdjugateIdentity5x5() {
    check("m = {{a,b,c,d,e},{f,g,h,i,j},{k,l,m1,n,o},{p,q,r,s,t},{u,v,w,x,y}}; "
        + "Union(Flatten(Expand(Adjugate(m).m - Det(m)*IdentityMatrix(5))))", //
        "{0}");
  }

  @Test
  public void testAdjugateIdentityRationalFunction() {
    check("m = {{1/x,1,0,1},{1,1/y,1,0},{0,1,1/z,1},{1,0,1,1/w}}; "
        + "Union(Flatten(Together(Adjugate(m).m - Det(m)*IdentityMatrix(4))))", //
        "{0}");
  }

  // -------------------------------------------------------------------- shapes

  @Test
  public void testDeterminantOfRadicalsHasNoDenominator() {
    // dividing by the pivot -1+Sqrt(6) used to leave eight fractions
    check("Det({{Sqrt(2),1,0,1},{1,Sqrt(3),1,0},{0,1,Sqrt(5),1},{1,0,1,Sqrt(7)}})", //
        "-Sqrt(6)-Sqrt(14)-Sqrt(15)-Sqrt(35)+Sqrt(210)");
  }

  @Test
  public void testDeterminantOfTrigonometricHasNoCotangent() {
    // dividing by the pivot Sin(x) used to introduce Cot(x) and Csc(x)
    check(
        "Det({{Sin(x),Cos(x),1,0},{-Cos(x),Sin(x),0,1},{1,0,Sin(y),Cos(y)},{0,1,-Cos(y),Sin(y)}})", //
        "2+2*Cos(x)*Cos(y)-2*Sin(x)*Sin(y)");
  }

  @Test
  public void testDeterminantKeepsPythagoreanCollapse() {
    // intermediate sums are not expanded, so the orderless pair matcher can still rewrite
    // Sin(u)^2+Cos(u)^2 to 1
    check("Det({{Sin(x),Cos(x),0,0},{-Cos(x),Sin(x),0,0},{0,0,Sin(y),Cos(y)},{0,0,-Cos(y),Sin(y)}})", //
        "1");
  }

  @Test
  public void testDeterminantOfRationalFunctionsIsOneFraction() {
    check("Det({{1/x,1,0,1},{1,1/y,1,0},{0,1,1/z,1},{1,0,1,1/w}})", //
        "(1-w*x-x*y-w*z-y*z)/(w*x*y*z)");
  }

  @Test
  public void testDeterminantOfPolynomialsHasNoDenominator() {
    check("Denominator(Det({{a,b,c,d,e},{f,g,h,i,j},{k,l,m1,n,o},{p,q,r,s,t},{u,v,w,x,y}}))", //
        "1");
  }

  @Test
  public void testDeterminant5x5MatchesCofactorExpansion() {
    // the expansion carried the previous pivot as a denominator into every entry once the
    // polynomial gcd in `Cancel` gave up, which inflated the answer from 781 to 26281 leaves
    check("LeafCount(Det({{a,b,c,d,e},{f,g,h,i,j},{k,l,m1,n,o},{p,q,r,s,t},{u,v,w,x,y}}))", //
        "781");
  }

  @Test
  public void testInverseEntriesAreSingleFractions() {
    // `Convert.matrix2List` used to run `Simplify` on every entry of a matrix smaller than 5x5,
    // which turned this entry into the partial fraction x/(2*(-1+x+x^2))+(2+x)/(2+6*x+2*x^2)
    check("Inverse({{1+x,1,0,0},{1,1+x,1,0},{0,1,1+x,1},{0,0,1,1+x}})[[1,1]]", //
        "((1+x)*(1-2*x-x^2))/((1-x-x^2)*(1+3*x+x^2))");
  }

  @Test
  public void testInverseAgreesWithAdjugateOverDeterminant() {
    check("m = {{1+x,1,0,0},{1,1+x,1,0},{0,1,1+x,1},{0,0,1,1+x}}; "
        + "Inverse(m)[[1,1]] === Together(Adjugate(m)[[1,1]]/Det(m))", //
        "True");
  }

  @Test
  public void testInverseOfRotationMatrixStaysTrigonometric() {
    check("Inverse({{Cos(t),-Sin(t),0},{Sin(t),Cos(t),0},{0,0,1}})", //
        "{{Cos(t),Sin(t),0},\n" //
            + " {-Sin(t),Cos(t),0},\n" //
            + " {0,0,1}}");
  }

  // ------------------------------------------------------------ singular input

  @Test
  public void testSingularExponentialMatrix() {
    check("Inverse({{E^x,1},{1,E^(-x)}})", //
        "Inverse(\n" //
            + "{{E^x,1},\n" //
            + " {1,E^(-x)}})");
  }

  @Test
  public void testSingularRepeatedRow() {
    check("Inverse({{1,x,x^2},{1,x,x^2},{a,b,c}})", //
        "Inverse(\n" //
            + "{{1,x,x^2},\n" //
            + " {1,x,x^2},\n" //
            + " {a,b,c}})");
  }

  @Test
  public void testSingularDeterminantIsZero() {
    check("Det({{E^x,E^(2*x),1,0},{1,E^x,E^(-x),0},{0,1,E^x,1},{1,0,1,E^(-x)}})", //
        "0");
  }

  // ------------------------------------------------------------------ progress

  @Test
  public void testLargeSymbolicDeterminantTerminates() {
    // a fully symbolic 6x6 did not finish at all with the previous elimination; 7x7 is the guard
    check("LeafCount(Det(Table(Subscript(a,i,j),{i,7},{j,7})))", //
        "148681");
  }
}
