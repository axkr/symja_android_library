package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import org.apfloat.Apint;
import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** Tests built-in functions */
public class LinearAlgebraTestCase extends ExprEvaluatorTestCase {


  @Test
  public void testAdjugate() {
    check("Adjugate({{E^5, 1, 3 - 2*I}, {1 + I, Pi/2, 5}, {0, 1, -4}})", //
        "{{-5-2*Pi,7-I*2,5+(-3/2+I)*Pi},\n"//
            + " {4+I*4,-4*E^5,5+I-5*E^5},\n"//
            + " {1+I,-E^5,-1-I+1/2*E^5*Pi}}");
    // https://en.wikipedia.org/wiki/Adjugate_matrix
    check("Adjugate({{-3,2,-5},\n" //
        + " {-1,0,-2}," //
        + " {3,-4,1}})", //
        "{{-8,18,-4},\n" //
            + " {-5,12,-1},\n" //
            + " {4,-6,2}}");
    check("Adjugate({{-3.0,2,-5.1},\n" //
        + " {-1,0,-2}," //
        + " {3,-4,1}})", //
        "{{-8.0,18.4,-4.0},\n"//
            + " {-5.0,12.3,-0.9},\n"//
            + " {4.0,-6.0,2.0}}");
    check("Adjugate({{5, 4}, {4, 11}})", //
        "{{11,-4},\n" //
            + " {-4,5}}");
    check("Adjugate({{5, 4}, {4, 11.0}})", //
        "{{11.0,-4.0},\n"//
            + " {-4.0,5.0}}");
  }

  @Test
  public void testAnglePath() {
    // avoid index out of bounds exception
    check("AnglePath(RandomReal({-1, 1}, {100}));", //
        "");
    check("AnglePath({90*Degree, 90*Degree, 90*Degree})", //
        "{{0,0},{0,1},{-1,1},{-1,0}}");
    check("AnglePath({90*Degree, Pi/3, -Pi/4})", //
        "{{0,0},{0,1},{-Sqrt(3)/2,3/2},{(1-Sqrt(3))/(2*Sqrt(2))-Sqrt(3)/2,3/2+(1+Sqrt(3))/(\n"
            + "2*Sqrt(2))}}");
    check("AnglePath({{0.7, 90*Degree}, {2.3, Pi/3}, {3.5, -Pi/4}})", //
        "{{0,0},{0.0,0.7},{-1.99186,1.85},{-2.89773,5.23074}}");

    check("AnglePath({t1,t2,t3})", //
        "{{0,0},{Cos(t1),Sin(t1)},{Cos(t1)+Cos(t1+t2),Sin(t1)+Sin(t1+t2)},{Cos(t1)+Cos(t1+t2)+Cos(t1+t2+t3),Sin(t1)+Sin(t1+t2)+Sin(t1+t2+t3)}}");

    check("AnglePath({{r1,t1},{r2,t2},{r3,t3}})", //
        "{{0,0},{r1*Cos(t1),r1*Sin(t1)},{r1*Cos(t1)+r2*Cos(t1+t2),r1*Sin(t1)+r2*Sin(t1+t2)},{r1*Cos(t1)+r2*Cos(t1+t2)+r3*Cos(t1+t2+t3),r1*Sin(t1)+r2*Sin(t1+t2)+r3*Sin(t1+t2+t3)}}");

    check("AnglePath({{x, y},t0}, {{r1,t1},{r2,t2},{r3,t3}})", //
        "{{x,y},{x+r1*Cos(t0+t1),y+r1*Sin(t0+t1)},{x+r1*Cos(t0+t1)+r2*Cos(t0+t1+t2),y+r1*Sin(t0+t1)+r2*Sin(t0+t1+t2)},{x+r1*Cos(t0+t1)+r2*Cos(t0+t1+t2)+r3*Cos(t0+t1+t2+t3),y+r1*Sin(t0+t1)+r2*Sin(t0+t1+t2)+r3*Sin(t0+t1+t2+t3)}}");
    check("AnglePath({{x, y},t0}, { })", //
        "{{x,y}}");

  }

  @Test
  public void testAngleVector() {
    check("AngleVector(x)", //
        "{Cos(x),Sin(x)}");
    check("AngleVector(Pi/6)", //
        "{Sqrt(3)/2,1/2}");
    check("AngleVector(90*Degree)", //
        "{0,1}");
    check("AngleVector({1, 10}, a)", //
        "{1+Cos(a),10+Sin(a)}");
    check("AngleVector({x,y}, {r,t})", //
        "{x+r*Cos(t),y+r*Sin(t)}");
  }

  @Test
  public void testAntihermitianMatrixQ() {
    check("AntihermitianMatrixQ({{42, 7 + 11*I}, {-7 + 11*I, 21}})", //
        "True");
    check("AntihermitianMatrixQ({{I, 3 + 4*I}, {-3 + 4*I, 0}})", //
        "True");
    check("AntihermitianMatrixQ({{I, 3 + 4*I}, {3 + 4*I, 0}})", //
        "False");
    check(
        "AntihermitianMatrixQ(({{I, a, b},  {-Conjugate[a], 0, c}, {-Conjugate[b],-Conjugate[c],-I} }))", //
        "True");
  }

  @Test
  public void testAntisymmetricMatrixQ() {
    check("AntisymmetricMatrixQ({{42, 7 + 11*I}, {-7 + 11*I, 21}})", //
        "False");
    check("AntisymmetricMatrixQ({{0, -2, 3}, {2, 0, -4}, {-3, 4, 0}})", //
        "True");
  }


  @Test
  public void testCharacteristicPolynomial() {
    // https://codegolf.stackexchange.com/questions/150613/characteristic-polynomial
    // [0] -> [1 0]
    check("CharacteristicPolynomial({{0}}, x)", //
        "-x");

    // [1] -> [1 -1]
    check("CharacteristicPolynomial({{1}}, x)", //
        "1-x");

    // [1 1; 0 1] -> [1 -2 1]
    check("CharacteristicPolynomial({{1,1},{0,1}}, x)", //
        "1-2*x+x^2");

    // [80 80; 57 71] -> [1 -151 1120]
    check("CharacteristicPolynomial({{80,80},{57,71}}, x)", //
        "1120-151*x+x^2");

    // [1 2 0; 2 -3 5; 0 1 1] -> [1 1 -14 12]
    check("CharacteristicPolynomial({{1, 2, 0},{2, -3, 5},{0, 1, 1}}, x)", //
        "12-14*x+x^2+x^3");

    // [4 2 1 3; 4 -3 9 0; -1 1 0 3; 20 -4 5 20] -> [1 -21 -83 559 -1987]
    check("CharacteristicPolynomial({{4, 2, 1, 3},{4, -3, 9, 0},{-1, 1, 0, 3},{20, -4, 5, 20}}, x)", //
        "-1987+559*x-83*x^2-21*x^3+x^4");

    // [0 5 0 12 -3 -6; 6 3 7 16 4 2; 4 0 5 1 13 -2; 12 10 12 -2 1 -6; 16 13 12 -4 7 10; 6 17 0 3 3
    // -1] -> [1 -12 -484 3249 -7065 -836601 -44200]
    check(
        "CharacteristicPolynomial({{0, 5, 0, 12, -3, -6},{6, 3, 7, 16, 4, 2},{4, 0, 5, 1, 13, -2},"//
            + "{12, 10, 12, -2, 1, -6},{16, 13, 12, -4, 7, 10},{6, 17, 0, 3, 3, -1}}, x)", //
        "-44200-836601*x-7065*x^2+3249*x^3-484*x^4-12*x^5+x^6");

    // [1 0 0 1 0 0 0; 1 1 0 0 1 0 1; 1 1 0 1 1 0 0; 1 1 0 1 1 0 0; 1 1 0 1 1 1 1; 1 1 1 0 1 1 1; 0
    // 1 0 0 0 0 1] -> [1 -6 10 -6 3 -2 0 0]
    check(
        "CharacteristicPolynomial({{1, 0, 0, 1, 0, 0, 0},{1, 1, 0, 0, 1, 0, 1},{1, 1, 0, 1, 1, 0, 0},"//
            + "{1, 1, 0, 1, 1, 0, 0},{1, 1, 0, 1, 1, 1, 1},{1, 1, 1, 0, 1, 1, 1},{0, 1, 0, 0, 0, 0, 1}" //
            + "}, x)", //
        "-2*x^2+3*x^3-6*x^4+10*x^5-6*x^6+x^7");

    check("CharacteristicPolynomial({{a, b}, {c, d}}, x)", //
        "-b*c+a*d-a*x-d*x+x^2");
    check("CharacteristicPolynomial({{1, 1, 1}, {1, 1/2, 1/3}, {1, 2, 3}},x)", //
        "1/3+7/3*x-9/2*x^2+x^3");
    check("CharacteristicPolynomial(N({{1, 1, 1}, {1, 1/2, 1/3}, {1, 2, 3}}),x)", //
        "-0.333333-2.33333*x+4.5*x^2-x^3");
    check("CharacteristicPolynomial({{1, 2*I}, {3 + 4*I, 5}}, z)", //
        "13-I*6-6*z+z^2");
  }

  @Test
  public void testCholeskyDecomposition() {
    check("matG=CholeskyDecomposition({{11.0,3.0},{3.0, 5.0}})", //
        "{{3.31662,0.904534},\n" + //
            " {0,2.04495}}");
    check("Transpose(matG).matG", //
        "{{11.0,3.0},\n" + //
            " {3.0,5.0}}");
    check("CholeskyDecomposition({{0.5, 0.3, 0.4}, {0.3, 1.1, -0.2}, {0.4, -0.2, 0.7}})", //
        "{{0.707107,0.424264,0.565685},\n" //
            + " {0,0.959166,-0.458732},\n" //
            + " {0,0,0.411783}}");
    check("CholeskyDecomposition({{2, I}, {-I, 3}})", //
        "{{Sqrt(2),I/Sqrt(2)},\n" //
            + " {0,Sqrt(5/2)}}");

    // message: CholeskyDecomposition: The matrix {{2,1}, {3,a}} is not hermitian or real and
    // symmetric.
    check("CholeskyDecomposition({{2,1},{3,a}})", //
        "CholeskyDecomposition({{2,1},{3,a}})");
    check("CholeskyDecomposition({{2,1},{1,3}})", //
        "{{Sqrt(2),1/Sqrt(2)},\n" //
            + " {0,Sqrt(5/2)}}");
  }

  @Test
  public void testCirclePoints() {
    // check("CirclePoints(3)", "{{Sqrt(3)/2,-1/2},{0,1},{-Sqrt(3)/2,-1/2}}");
    check("CirclePoints(2)", //
        "{{1,0},{-1,0}}");

    check("CirclePoints(4)", //
        "{{1/Sqrt(2),-1/Sqrt(2)},{1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),-\n"
            + "1/Sqrt(2)}}");
    // check("CirclePoints(10)", "");
  }

  @Test
  public void testCofactor() {
    check(
        "Cofactor({{6, 0, 4, 9, 5}, {1, 9, 3, 1, 2}, {5, 4, 5, 3, 8}, {3, 9, 8, 2, 5}, {4, 1, 6, 6, 4}},{1,3})", //
        "-30");
    check("Cofactor({{1, 2, 3, 4}, {5, 6, 7, 8}, {8, 7, 6, 4}, {5, 3, 2, 1}}, {3, 2})", //
        "4");
  }

  @Test
  public void testCollinearPoints() {
    // https://youtu.be/UDt9M8_zxlw
    check("CollinearPoints({{1,2,3}, {3,8,1}, {7,20,-3} })", //
        "True");

    check("CollinearPoints({{0, 0, 1}, {1, 0, 1}, {0, 1, 1}, {a, b, c}})", //
        "False");
    check("CollinearPoints({{1, 2, 1}, {3, 4, 1}, {5, 6, 1}, {7, 8, 1}})", //
        "True");

    check("CollinearPoints({{x1,y1}, {x2,y2}, {px,py}})", //
        "x2*y1+px*(-y1+y2)==py*(-x1+x2)+x1*y2");
    check("CollinearPoints({{0,0}, {1,2}, {2,4}})", //
        "True");
    check("CollinearPoints({{0,1}, {1,2}, {x,y}})", //
        "1+x==y");
  }

  @Test
  public void testConjugateTranspose() {
    check("ConjugateTranspose({a*I,b*I,2*I,3*I} )", //
        "{-I*Conjugate(a),-I*Conjugate(b),-I*2,-I*3}");

    check("ConjugateTranspose({{{0,0},{0,0}},{0,0}})", //
        "{{{0,0},0},{{0,0},0}}");

    check("ConjugateTranspose(SparseArray({{1,2+I,3},{4,5-I,6},{7,8,9}}))", //
        "SparseArray(Number of elements: 9 Dimensions: {3,3} Default value: 0)");
    check("ConjugateTranspose(SparseArray({{1,2+I,3},{4,5-I,6},{7,8,9}})) // Normal", //
        "{{1,4,7},\n" + " {2-I,5+I,8},\n" + " {3,6,9}}");
    check("ConjugateTranspose({{1,2+I,3},{4,5-I,6},{7,8,9}})", //
        "{{1,4,7},{2-I,5+I,8},{3,6,9}}");
    check("ConjugateTranspose(N({{1,2+I,3},{4,5-I,6},{7,8,9}}))", //
        "{{1.0,4.0,7.0},{2.0+I*(-1.0),5.0+I*1.0,8.0},{3.0,6.0,9.0}}");

    check("ConjugateTranspose({{1, 2*I, 3}, {3 + 4*I, 5, I}})", //
        "{{1,3-I*4},{-I*2,5},{3,-I}}");
  }

  @Test
  public void testCoordinateBoundingBox() {
    check("CoordinateBoundingBox({{0, 1}, {2, 3}, {3,4}, {2, 3}, {1,1}})", //
        "{{0,1},{3,4}}");
    check("CoordinateBoundingBox({{0, 1}, {1, 2}, {2, 1}, {3, 2}, {4, 0}}, Scaled(1/4))", //
        "{{-1,-1/2},{5,5/2}}");
    check("CoordinateBoundingBox({{a,b,u}, {c,d,v}, {e,f,w}},Scaled(1/4))", //
        "{{Min(a,c,e)+1/4*(-Max(a,c,e)+Min(a,c,e)),Min(b,d,f)+1/4*(-Max(b,d,f)+Min(b,d,f)),Min(u,v,w)+\n"//
            + "1/4*(-Max(u,v,w)+Min(u,v,w))},{Max(a,c,e)+1/4*(Max(a,c,e)-Min(a,c,e)),Max(b,d,f)+\n"//
            + "1/4*(Max(b,d,f)-Min(b,d,f)),Max(u,v,w)+1/4*(Max(u,v,w)-Min(u,v,w))}}");
    check("CoordinateBoundingBox({{a,b,u}, {c,d,v}, {e,f,w}})", //
        "{{Min(a,c,e),Min(b,d,f),Min(u,v,w)},{Max(a,c,e),Max(b,d,f),Max(u,v,w)}}");
    check("CoordinateBoundingBox({{a,b}, {c,d}, {e,f}})", //
        "{{Min(a,c,e),Min(b,d,f)},{Max(a,c,e),Max(b,d,f)}}");
    check("CoordinateBoundingBox({{0, 1}, {1, 2}, {2, 1}, {3, 2}, {4, 0}})", //
        "{{0,0},{4,2}}");
  }

  @Test
  public void testCoordinateBounds() {
    check("CoordinateBounds({{1.0,-1.0},{0.0,2.6457513110645907},{-1.0,-1.0}})", //
        "{{-1.0,1.0},{-1.0,2.64575}}");

    check("CoordinateBounds({{0, 1}, {1, 2}, {2, 1}, {3, 2}, {4, 0}})", //
        "{{0,4},{0,2}}");
    check("CoordinateBounds({{0, 1}, {1, 2}, {2, 1}, {3, 2}, {4, 0}},2)", //
        "{{-2,6},{-2,4}}");
  }

  @Test
  public void testCoplanarPoints() {
    check("CoplanarPoints( {{3,2,-5}, {-1,4,-3}, {-3,8,-5}, {-3,2,1}} )", //
        "True");
    check("CoplanarPoints( {{0,-1,-1}, {4,5,1}, {3,9,4}, {-4,4,3}} )", //
        "False");
    check("CoplanarPoints({{1, a, 1}, {a, 2, 1}, {1, 2, 1}, {2, 3, 4}, {a, b, c}})", //
        "(1-a)*(-2+a)==0&&(-1+a)*(-5+3*b-c)==0");
    check("CoplanarPoints({ {a, 2, 1}, {1, 2, 1}, {2, 3, 4}, {a, b, c} })", //
        "(-1+a)*(-5+3*b-c)==0");
    check("CoplanarPoints({ {a, 2, 1}, {1, 2, 1}, {a, b, c}, {2, 3, 4}})", //
        "(-1+a)*(5-3*b+c)==0");
    check("CoplanarPoints( {{0, 0, 0}, {1, 1, -2}, {-1, 2, -1}, {3, -4, 1}} )", //
        "True");
    check("CoplanarPoints( {{0, 0, 0}, {1, 1, -2}, {-1, 2, -1}, {x,y,z}} )", //
        "x+y+z==0");
    check("CoplanarPoints( {{1,2}, {3,4}, {a,b}, {c,d}})", //
        "True");
    check("CoplanarPoints( {{1,2}, {3,4}, {a,b,r}, {c,d}})", //
        "CoplanarPoints({{1,2},{3,4},{a,b,r},{c,d}})");
  }

  @Test
  public void testCross() {
    check("Cross({x, y, z})", //
        "Cross({x,y,z})");

    // check("Cross({a1, b1, c1, d1}, {a2, b2, c2, d2}, {a3, b3, c3, d3})",
    // "{b3 c2 d1-b2 c3 d1-b3 c1 d2+b1 c3 d2+b2 c1 d3-b1 c2 d3,"
    // + "-a3 c2 d1+a2 c3 d1+a3 c1 d2-a1 c3 d2-a2 c1 d3+a1 c2 d3,"
    // + "a3 b2 d1-a2 b3 d1-a3 b1 d2+a1 b3 d2+a2 b1 d3-a1 b2 d3,"
    // + "-a3 b2 c1+a2 b3 c1+a3 b1 c2-a1 b3 c2-a2 b1 c3+a1 b2 c3}");
    check("Cross({a,b}, {c,d})", //
        "-b*c+a*d");
    check("Cross({a, b, c}, {x, y, z})", //
        "{-c*y+b*z,c*x-a*z,-b*x+a*y}");
    check("Cross({x, y})", //
        "{-y,x}");
    check("Cross({x1, y1, z1}, {x2, y2, z2})", //
        "{-y2*z1+y1*z2,x2*z1-x1*z2,-x2*y1+x1*y2}");
    check("Cross({1, 2}, {3, 4, 5})", //
        "Cross({1,2},{3,4,5})");

    check("Cross({1,2,3},{1,1/2,1/3})", //
        "{-5/6,8/3,-3/2}");
    check("Cross(N({1,2,3}),N({1,1/2,1/3}))", //
        "{-0.833333,2.66667,-1.5}");
  }

  @Test
  public void testDesignMatrix() {
    check("DesignMatrix({{1,1},{2,2*Sqrt(2)},{3,3*Sqrt(3)},{4,8},{5,5*Sqrt(5)}}, {x, x^2}, x)", //
        "{{1,1,1},{1,2,4},{1,3,9},{1,4,16},{1,5,25}}");

    check("data = Table({i, i^(3/2) }, {i, 5})", //
        "{{1,1},{2,2*Sqrt(2)},{3,3*Sqrt(3)},{4,8},{5,5*Sqrt(5)}}");
    check("DesignMatrix(data, x, x)", //
        "{{1,1},{1,2},{1,3},{1,4},{1,5}}");

    check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, x, x)", //
        "{{1,2},{1,3},{1,5},{1,7}}"); //
    check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, f(x), x)", //
        "{{1,f(2)},{1,f(3)},{1,f(5)},{1,f(7)}}");
    check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}},{1, f(x)}, x)", //
        "{{1,f(2)},{1,f(3)},{1,f(5)},{1,f(7)}}");
  }

  @Test
  public void testDet() {
    check("N(Det({{Pi,2.0},{3,4}}))", //
        "6.56637");
    check("N(Det({{Pi,2.0},{3,4}}),50)", //
        "6.566370614359172");

    check("Det({{1,2},{3,4}})", //
        "-2");
    check("Det({{1,2.0},{3,4}})", //
        "-2.0");
    check("Det({{a,b},{c,d}})", //
        "-b*c+a*d");

    check("Det({{{1,0,0},{0,1,0}, {0,0,1}},{a,b,c},{d,e,f}})", //
        "Det({{{1,0,0},{0,1,0},{0,0,1}},{a,b,c},{d,e,f}})");
    check("Det(-2)", //
        "Det(-2)");
    check("Det({{}})", //
        "Det({{}})");

    // github #121 - print error
    check("Det({{1, 1, 1}, {2, 2, 2}})", //
        "Det({{1,1,1},{2,2,2}})");

    check("Det({{42}})", //
        "42");
    check("Det({{x}})", //
        "x");
    check("Det({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}})", //
        "-2");
    check("Det(SparseArray({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}}))", //
        "-2");
    check("Det({{a11, a12},{a21,a22}})", //
        "-a12*a21+a11*a22");
    check("Det({{a,b,c},{d,e,f},{g,h,i}})", //
        "-c*e*g+b*f*g+c*d*h-a*f*h-b*d*i+a*e*i");
  }

  @Test
  public void testDiagonal() {

    check("Diagonal(SparseArray({{1,2,3},{4,5,6},{7,8,9}}))", //
        "{1,5,9}");
    check("Diagonal(SparseArray({{1,2,3},{4,5,6},{7,8,9}}), 1)", //
        "{2,6}");
    check("Diagonal(SparseArray({{1,2,3},{4,5,6},{7,8,9}}), -1)", //
        "{4,8}");

    check("Diagonal({{{0}, 1}, {2, 3}, {4, 5}})", //
        "{{0},3}");
    check("Diagonal({1, 2, 3, 4})", //
        "{}");
    check("Diagonal({{1,0}, {0,1},0})", //
        "{1,1}");
    check("Diagonal({{1,2,3},{4,5,6},{7,8,9}})", //
        "{1,5,9}");
    check("Diagonal({{1,2,3},{4,5,6},{7,8,9}}, 1)", //
        "{2,6}");
    check("Diagonal({{1,2,3},{4,5,6},{7,8,9}}, -1)", //
        "{4,8}");
  }

  @Test
  public void testDiagonalMatrix() {
    check("DiagonalMatrix(SparseArray({1, 2, 3}))", //
        "SparseArray(Number of elements: 3 Dimensions: {3,3} Default value: 0)");
    check("DiagonalMatrix({1, 2, 3})", //
        "{{1,0,0},\n" + //
            " {0,2,0},\n" + //
            " {0,0,3}}");
  }


  @Test
  public void testDiagonalMatrixQ() {
    check("DiagonalMatrixQ({{a, 0, 0}, {b, 0, 0}, {0, 0, c}})", //
        "False");
    check("DiagonalMatrixQ({{0, a, 0, 0}, {0, 0, b, 0}, {0, 0, 0, c}}, 1)", //
        "True");
    check("DiagonalMatrixQ({{0, a, 0, 0}, {0, 0, b, 0}, {0, 0, c, 0}}, -1)", //
        "False");
    check("DiagonalMatrixQ({{0, 0, 0, 0}, {a, 0, 0, 0}, {0, b, 0, 0}}, -1)", //
        "True");
    check("DiagonalMatrixQ(DiagonalMatrix(SparseArray({1, 2, 3})))", //
        "True");
    check("DiagonalMatrixQ(DiagonalMatrix({1, 2, 3}))", //
        "True");
  }

  @Test
  public void testDimensions() {
    check("Dimensions({{{1,0},{0,1}},{0,0}})", //
        "{2,2}");
    check("Options(Dimensions)", //
        "{AllowedHeads->Automatic}");
    check("s = SparseArray({{1, 1} -> 1, {2, 2} -> 2, {3, 3} -> 3, {1, 3} -> 4});", //
        "");
    check("Dimensions(s)", //
        "{3,3}");
    check("AtomQ(s)", //
        "True");
    check("Dimensions(s,1)", //
        "{3}");
    check("Dimensions(s,2)", //
        "{3,3}");
    check("Dimensions(s,3)", //
        "{3,3}");
    check("Dimensions({a, b})", //
        "{2}");
    check("Dimensions({{a, b, c}, {d, e, f}})", //
        "{2,3}");
    check("Dimensions({{a, b, c}, {d, e}, {f}})", //
        "{3}");
    check("Dimensions({{{{a, b}}}})", //
        "{1,1,1,2}");
    check("Dimensions({{{{a, b}}}}, 2)", //
        "{1,1}");
    check("Dimensions(f(f(x, y), f(a, b), f(s, t)))", //
        "{3,2}");
    check("Dimensions(f(g(x, y), g(a, b), g(s, t)))", //
        "{3}");
    check("Dimensions(Array(a, {2, 1, 4, 3}))", //
        "{2,1,4,3}");
  }

  @Test
  public void testDot() {
    check("{{a,2}}.{{a},{3}}", //
        "{{6+a^2}}");
    check("{{3,4}}.{{a},{3}}", //
        "{{3*(4+a)}}");
    check("{{a,2},{3,4}}.{{a,2},{3,4}}", //
        "{{6+a^2,2*(4+a)},\n" //
            + " {3*(4+a),22}}");

    check("{{1, 2, 3}, {3, 4, 11}, {13, 7, 8}}.{-11/4,33/4,-5/4}", //
        "{10,11,12}");
    check("{-11/4,33/4,-5/4}.{{1, 2, 3}, {3, 4, 11}, {13, 7, 8}}", //
        "{23/4,75/4,145/2}");

    check("{{1,2},{3,4}}.{{1,2},{3,4}}", //
        "{{7,10},\n" //
            + " {15,22}}");
    check("$x.$y", "$x.$y");
    check("{{1,2},{3,4}}.{{1,2},{3,-4}}.{{1,2},{3,4}}", //
        "{{-11,-10},\n" //
            + " {-15,-10}}");

    // github #121 - print error

    // message Dot: Nonrectangular tensor encountered
    check("Dot({{0,2},{-8,2}},{{{0},0},{0,3}})", //
        "{{0,2},\n" //
            + " {-8,2}}.{{{0},0},{0,3}}");

    // message Dot: Tensors {{}} and {{}} have incompatible shapes.
    check("{{}}.{{}}", //
        "{{}}.\n" + "{{}}");


    check("#1.#123 // FullForm", //
        "Dot(Slot(1), Slot(123))");

    // only 1 arg
    check("Dot({a,b,c})", //
        "{a,b,c}");
    check("Dot({{1, 2}, {3, 4}, {5, 6}})", //
        "{{1,2},{3,4},{5,6}}");

    check("a=RandomInteger(9, {2, 3, 4});\n" //
        + "b=RandomInteger(9, {4, 5, 2});\n" + "c=RandomInteger(9, {2});", //
        "");
    check("Dimensions(a.b)", //
        "{2,3,5,2}");
    check("Dimensions(c.a.b.c)", //
        "{3,5}");
    check("{}.{{}}", //
        "{}.\n" + "{{}}");
    check("{}.{ }", //
        "0");
    check("{}.{4,5.0,6}", //
        "{}.{4,5.0,6}");
    check("0.17583681.41125407852.0 // HoldForm // FullForm", //
        "HoldForm(Times(Times(0.17583681`, 0.41125407852`), 0.0`))");

    check("{{1, 2}, {3.0, 4}, {5, 6}}.{1,1}", //
        "{3.0,7.0,11.0}");
    check("{{1, 2}, {3.0, 4}, {5, 6}}.{{1},{1}}", //
        "{{3.0},\n" + " {7.0},\n" + " {11.0}}");
    check("{1,1,1}.{{1, 2}, {3.0, 4}, {5, 6}}", //
        "{9.0,12.0}");
    check("{{1,1,1}}.{{1, 2}, {3.0, 4}, {5, 6}}", //
        "{{9.0,12.0}}");
    check("{1,2,3.0}.{4,5.0,6}", //
        "32.0");

    check("{{1, 2}, {3, 4}, {5, 6}}.{1,1}", //
        "{3,7,11}");
    check("{{1, 2}, {3, 4}, {5, 6}}.{{1},{1}}", //
        "{{3},\n" + " {7},\n" + " {11}}");
    check("{1,1,1}.{{1, 2}, {3, 4}, {5, 6}}", //
        "{9,12}");
    check("{{1,1,1}}.{{1, 2}, {3, 4}, {5, 6}}", //
        "{{9,12}}");
    check("{1,2,3}.{4,5,6}", //
        "32");

    check("a = {{{8, 0}, {4, 2}}, {{0, 7}, {5, 6}}}", //
        "{{{8,0},{4,2}},{{0,7},{5,6}}}");
    check("x = {1, 2};\n" //
        + "y = {1/2, 1/3};\n" + "z = {-1, 1};", //
        "");
    check("a.x.y.z", //
        "6");
    check("a.z.y.x", //
        "3");

    check("a = {{1, 2},{3, 4},{5, 6}}", //
        "{{1,2},{3,4},{5,6}}");
    check("a.{1, 1}", //
        "{3,7,11}");
    check("a.{{1}, {1}}", //
        "{{3},\n" + " {7},\n" + " {11}}");
    check("{1,1,1}.a", //
        "{9,12}");
    check("{{1,1,1}}.a", //
        "{{9,12}}");

    check("a = {1+I, 2-I, -1-2*I}", //
        "{1+I,2-I,-1-I*2}");
    check("a.a", //
        "I*2");
    check("Conjugate(a).a", //
        "12");
    check("Norm(a)^2", //
        "12");
  }

  @Test
  public void testDotIssue932() {
    // issue #932 StackOverflowError
    check("{{0,1,-1+2^m},{1-2^m,2,-1+2^m},{2^m,-2,-2^m}}.{{1,0,1},{0,1,1},{1,-1,-1}}", //
        "{{-1+2^m,2-2^m,2-2^m},\n" //
            + " {0,3-2^m,4-2^(1+m)},\n" //
            + " {0,-2+2^m,-2+2^(1+m)}}");
  }

  @Test
  public void testEigensystem() {
    check("Eigensystem({{0}},{-1,-2,3})", //
        "Eigensystem(\n" //
            + "{{0}},{-1,-2,3})");


    // example from https://github.com/Hipparchus-Math/hipparchus/issues/365
    check("Eigensystem({{1,1,0.0}, {1,1,1}, {0.0,1,1}})", //
        "{{2.41421,1.0,-0.414214},{{0.5,0.707107,0.5},{0.707107,-4.54581*10^-29,-0.707107},{-0.5,0.707107,-0.5}}}");
    check("Eigensystem({{1,1,1.0*^-16 }, {1,1,1}, {1.0*^-17,1,1}})", //
        "{{2.41421,1.0,-0.414214},{{0.5,0.707107,0.5},{0.707107,3.57168*10^-17,-0.707107},{-0.5,0.707107,-0.5}}}");

    //
    check("Eigensystem(Table(If(Abs(i - j) < 3, 1.0, 0), {i, 5}, {j, 5}), 3)", //
        "{{3.93543,1.61803,-0.618034},"//
            + "{{0.350542,0.469959,0.559033,0.469959,0.350542}," //
            + "{-0.601501,-0.371748,8.98084*10^-17,0.371748,0.601501},"//
            + "{0.371748,-0.601501,-4.53187*10^-16,0.601501,-0.371748}}}");

    check("Eigensystem({{1,0,0,0,0},{3,1,0,0,0},{6,3,2,0,0},{10,6,3,2,0},{15,10,6,3,2}})", //
        "{{2,2,2,1,1},{{0,0,0,0,1},{0,0,0,0,0},{0,0,0,0,0},{0,-1,3,-3,1},{0,0,0,0,0}}}");

    // example from https://en.wikipedia.org/wiki/Eigenvalues_and_eigenvectors
    check(//
        "Chop(Eigensystem({{2.0,0.0,0.0,0.0},{1.0,2.0,0.0,0.0},{0.0,1.0,3.0,0.0},{0.0,0.0,1.0,3.0}}), 10^-7)", //
        "{{3.0,3.0,2.0,2.0},{{0,0,0,1.0},{0,0,0,0},{0,0.57735,-0.57735,0.57735},{0,0,0,0}}}");

    check("Eigensystem({{1,0,0},{0,1,0},{0,0,1}})", //
        "{{1,1,1},{{1,0,0},{0,1,0},{0,0,1}}}");
    check("Eigensystem({{1,0,0},{-2,1,0},{0,0,1}})", //
        "{{1,1,1},{{0,1,0},{0,0,1},{0,0,0}}}");

    check("Eigensystem({{1.1, 2.2, 3.25}, {0.76, 4.6, 5}, {0.1, 0.1, 6.1}})", //
        "{{6.60674,4.52536,0.667901},{{0.48687,0.833694,0.260598},{0.479424,0.873368,-0.085911},{0.985096,-0.171352,-0.0149803}}}");
    check("Eigensystem(N(Table(1/(i + j + 1), {i, 3}, {j, 3})))", //
        "{{0.657051,0.0189263,0.000212737},{{0.703153,0.549268,0.451532},{-0.668535,0.29444,0.68291},{-0.242151,0.782055,-0.574241}}}");
  }

  @Test
  public void testEigenvalues() {
    // check("Eigenvalues("//
    // + "{{5.42,3.26+I*0.643,-0.467+I*(-0.193)}," //
    // + " {3.26+I*(-0.643),3.82,1.04+I*(-2.35)}," //
    // + " {-0.467+I*0.193,1.04+I*2.35,4.88}})", //
    // "{8.76846+I*4.60561*10^-16,5.16361,0.187924}");


    check("Eigenvalues({{1,0,0,0,0},{3,1,0,0,0},{6,3,2,0,0},{10,6,3,2,0},{15,10,6,3,2}})", //
        "{2,2,2,1,1}");
    check("mat = Array(a, {2,2}); Eigenvalues(mat.ConjugateTranspose(mat))[[1]] // FullSimplify", //
        "1/2*(Abs(a(1,1))^2+Abs(a(1,2))^2+Abs(a(2,1))^2+Abs(a(2,2))^2-Sqrt((Abs(a(1,1))^2+Abs(a(\n" //
            + "1,2))^2)^2-2*(Abs(a(1,1))^2+Abs(a(1,2))^2)*(Abs(a(2,1))^2+Abs(a(2,2))^2)+(Abs(a(\n" //
            + "2,1))^2+Abs(a(2,2))^2)^2+4*(a(2,1)*Conjugate(a(1,1))+a(2,2)*Conjugate(a(1,2)))*(a(\n" //
            + "1,1)*Conjugate(a(2,1))+a(1,2)*Conjugate(a(2,2)))))");

    check("Eigenvalues({{1, 0, 0}, {-2, 1, 0}, {0, 1, 1}})", //
        "{1,1,1}");
    check("Eigenvalues({{7}},-1)", //
        "{7.0}");
    check("Eigenvalues({{-1}},1)", //
        "{-1.0}");
    // print message: Eigenvalues: Cannot take eigenvalues 1 through 2 out of the total of 1
    // eigenvalues.
    check("Eigenvalues({{7}},-19)", //
        "Eigenvalues(\n" + //
            "{{7}},-19)");
    check("Eigenvalues({{-1}},2)", //
        "Eigenvalues(\n"//
            + "{{-1}},2)");
    check(
        "Eigenvalues({{0,1,1,0,1,0,0,0},{1,0,0,1,0,1,0,0},{1,0,0,1,0,0,1,0},{0,1,1,0,0,0,0,1},{1,0,0,0,0,1,1,0},{0,1,0,0,1,0,0,1},{0,0,1,0,1,0,0,1},{0,0,0,1,0,1,1,0}})", //
        "{-3,3,-1,-1,-1,1,1,1}");

    // print message: Eigenvalues: Sequence specification (+n,-n,{+n},{-n},{m,n}) or {m,n,s}
    // expected at position 2 in Eigenvalues({{1,0},{0,1}},{1,1,1,1}).
    check("Eigenvalues( {{1,0},{0,1}},{1,1,1,1})", //
        "Eigenvalues({{1,0},{0,1}},{1,1,1,1})");
    check("Eigenvalues(SparseArray({{1.0, 2, 3}, {4, 5, 6}, {7, 8, 9}}))", //
        "{16.11684,-1.11684,-9.2965*10^-16}");

    check("Eigenvalues({{-3.0,-1.5,-3.0},{0,-1,0},{1,0,0}})", //
        "{-1.5+I*(-0.866025),-1.5+I*0.866025,-1.0}");
    check("Eigenvalues({{1.0, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "{16.11684,-1.11684,-9.2965*10^-16}");

    // example from https://en.wikipedia.org/wiki/Eigenvalues_and_eigenvectors
    check("Eigenvalues({{2,0,0,0},{1,2,0,0},{0,1,3,0},{0,0,1,3}})", //
        "{3,3,2,2}");


    check("Eigenvalues({{1,1,1},{2,2,2},{3,3,3}})", //
        "{6,0,0}");
    check("Eigenvalues({{-2,-2,4}, {-1,-3,7}, {2,4,6}})", //
        "{1+Sqrt(61),1-Sqrt(61),-1}");
    check("Eigenvalues({{0,1,-1},{1,1,0},{-1,0,1}})", //
        "{2,-1,1}");
    check("Eigenvalues({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})", //
        "{1,1,1}");
    check("Eigenvalues({{1/3, 1/2, 3/5}, {1/2, 4/5, 1}, {3/5, 1, 9/7}})", //
        "{254/315+(14954373125000+I*7875000*Sqrt(9477810222))^(1/3)/31500+1215035/63*2^(1/\n"
            + "3)/(29908746250000+I*15750000*Sqrt(9477810222))^(1/3),254/315+1215035/63*(-1+I*Sqrt(\n"
            + "3))/(2^(2/3)*(29908746250000+I*15750000*Sqrt(9477810222))^(1/3))+((-1-I*Sqrt(3))*(\n"
            + "29908746250000+I*15750000*Sqrt(9477810222))^(1/3))/(63000*2^(1/3)),254/315+\n"
            + "1215035/63*(-1-I*Sqrt(3))/(2^(2/3)*(29908746250000+I*15750000*Sqrt(9477810222))^(\n"
            + "1/3))+((-1+I*Sqrt(3))*(29908746250000+I*15750000*Sqrt(9477810222))^(1/3))/(63000*\n"
            + "2^(1/3))}");

    check("m = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}", //
        "{{1,2,3},{4,5,6},{7,8,9}}");
    check("Roots(CharacteristicPolynomial(m,x)==0, x)", //
        "x==0||x==3/2*(5-Sqrt(33))||x==3/2*(5+Sqrt(33))");
    check("EigenValues(m)", //
        "{3/2*(5+Sqrt(33)),3/2*(5-Sqrt(33)),0}");

    // 4x4
    check("Eigenvalues(SparseArray({{1, 3} -> 2, {2, 2} -> 3, {3, 1} -> 1, {4, 2} -> 5}, {4, 4}))", //
        "{3,-Sqrt(2),Sqrt(2),0}");

    // 3x3
    check("Eigenvalues({{1,2,3},{3,2,1},{2,1,3}})", //
        "{6,-Sqrt(2),Sqrt(2)}");
    check("Eigenvalues({{1.1, 2.2, 3.25}, {0.76, 4.6, 5}, {0.1, 0.1, 6.1}}) // N", //
        "{6.60674,4.52536,0.667901}");

    // Eigenvalues
    check("Eigenvalues({{-8, 12, 4}, {12, -20, 0}, {4, 0, -2}}) // N // Chop", //
        "{-27.59215,-4.6517,2.24386}");

    check("Eigenvalues(A)", //
        "Eigenvalues(A)");

    check("Eigenvalues({{a}})", //
        "{a}");
    check("Eigenvalues({{a, b}, {0, a}})", //
        "{a,a}");
    check("Eigenvalues({{a, b}, {0, d}})", //
        "{1/2*(a+d-Sqrt(a^2-2*a*d+d^2)),1/2*(a+d+Sqrt(a^2-2*a*d+d^2))}");
    check("Eigenvalues({{a,b}, {c,d}})", //
        "{1/2*(a+d-Sqrt(a^2+4*b*c-2*a*d+d^2)),1/2*(a+d+Sqrt(a^2+4*b*c-2*a*d+d^2))}");
    check("Eigenvalues({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "{3/2*(5+Sqrt(33)),3/2*(5-Sqrt(33)),0}");
    check("Eigenvalues({{0.0,1.0,-1.0},{1.0,1.0,0.0},{-1.0,0.0,1.0}})", //
        "{2.0,-1.0,1.0}");
  }

  @Test
  public void testEigenvectors() {
    check("Eigenvectors({{-8, -6, 9, 10}, {-6, -4, 0, 6}, {0, 0, -4, 0}, {-10, -6, 9, 12}})", //
        "{{1,1,0,1},{0,3,2,0},{1,0,0,1},{0,0,0,0}}");

    if (Config.EXPENSIVE_JUNIT_TESTS) {
      // slow because of first trying symbolic computation
      check("Eigenvectors({{1/3, 1/2, 3/5}, {1/2, 4/5, 1}, {3/5, 1, 9/7}}) // N", //
          "{{0.488797,0.792073,1.0},{-1.3983+I*(-1.49544*10^-11),-0.399603+I*(-3.33265*10^-13),1.0},{1.30633+I*(-4.69012*10^-12),-2.06866+I*(-1.40389*10^-12),1.0}}");
    }

    check("Eigenvectors({{Pi, 1/3}, {I, 5}})", //
        "{{-I*1/2*(-5+Pi-Sqrt(25+I*4/3-10*Pi+Pi^2)),1},{-I*1/2*(-5+Pi+Sqrt(25+I*4/3-10*Pi+Pi^\n"
            + "2)),1}}");

    check("Eigenvectors(SparseArray({{1.0, 2.0, 3}, {4, 5, 6}, {7, 8, 9}}))", //
        "{{0.231971,0.525322,0.818673},{0.78583,0.0867513,-0.612328},{-0.408248,0.816497,-0.408248}}");


    check("Eigenvectors({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})", //
        "{{1,0,0},{0,1,0},{0,0,1}}");

    // only 2 eigenvectors => fill up with 0.0 vector
    // see https://github.com/Hipparchus-Math/hipparchus/issues/249
    check("Eigenvectors({{1,0,0},{-2,1,0},{0,0,1}})", //
        "{{0,1,0},{0,0,1},{0,0,0}}");
    check("Eigenvalues({{1,0,0},{-2,1,0},{0,0,1}})", //
        "{1,1,1}");

    check("Eigenvectors({{-8, -6, 9, 10}, {-6, -4, 0, 6}, {0, 0, -4, 0}, {-10, -6, 9, 12}})", //
        "{{1,1,0,1},{0,3,2,0},{1,0,0,1},{0,0,0,0}}");

    // TODO https://github.com/Hipparchus-Math/hipparchus/issues/337
    check("Eigenvectors({{1, 0, 0}, {-2, 1, 0}, {0, 1, 1}})", //
        "{{0,0,1},{0,0,0},{0,0,0}}");
    check("Eigenvectors({{1,0,0,0,0},{3,1,0,0,0},{6,3,2,0,0},{10,6,3,2,0},{15,10,6,3,2}})", //
        "{{0,0,0,0,1},{0,0,0,0,0},{0,0,0,0,0},{0,-1,3,-3,1},{0,0,0,0,0}}");
    check("Eigenvectors({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}) // MatrixForm", //
        "{{-1/2+3/2*Sqrt(3/11),1/4+3/4*Sqrt(3/11),1},\n" //
            + " {-1/2-3/2*Sqrt(3/11),1/4-3/4*Sqrt(3/11),1},\n" //
            + " {1,-2,1}}");

    check("Eigenvalues({{1.1, 2.2, 3.25}, {0.76, 4.6, 5}, {0.1, 0.1, 6.1}}) // MatrixForm", //
        "{6.60674,4.52536,0.667901}");
    check("Eigenvectors({{1.1, 2.2, 3.25}, {0.76, 4.6, 5}, {0.1, 0.1, 6.1}}) // MatrixForm", //
        "{{0.48687,0.833694,0.260598},\n" //
            + " {0.479424,0.873368,-0.085911},\n" //
            + " {0.985096,-0.171352,-0.0149803}}");
    check("Eigenvectors({{4.2, 7.2, 9.3}, {-2.1, 5.2, 1.3}, {1.5, 4.4, 3.2}}) // MatrixForm", //
        "{{0.873208,-0.110109+I*0.338016,0.32246+I*0.0845519},\n"
            + " {0.873208,-0.110109+I*(-0.338016),0.32246+I*(-0.0845519)},\n"
            + " {0.665539,0.370108,-0.648134}}");
    check("Eigenvectors(A)", //
        "Eigenvectors(A)");
    check("Eigenvectors({{a}})", //
        "1");
    check("Eigenvectors({{a, b}, {0, a}})", //
        "{{1,0},{0,0}}");
    check("Eigenvectors({{a, b}, {0, d}})", //
        "{{1,0},{-b/(a-d),1}}");
    check("Eigenvectors({{a, b}, {c, d}})", //
        "{{(a-d-Sqrt(a^2+4*b*c-2*a*d+d^2))/(2*c),1},{(a-d+Sqrt(a^2+4*b*c-2*a*d+d^2))/(2*c),\n" + //
            "1}}");

  }

  @Test
  public void testEigenvectorsIssue718() {
    check("Eigenvectors({{-1,-5},{0,4}})", //
        "{{-0.707107,0.707107},{1.0,0.0}}");
    check("{Normalize[{-1,1}],Normalize[{1,0}] }", //
        "{{-1/Sqrt(2),1/Sqrt(2)},{1,0}}");
    check("Eigenvectors({{-2,-2,4}, {-1,-3,7}, {2, 4, 6.00001}})", //
        "{{0.223932,0.482825,0.846602},{0.580105,0.74736,-0.323932},{0.894427,-0.447214,-7.22845*10^-18}}");

  }

  @Test
  public void testEigenvectorsIssue979() {
    // https://github.com/Hipparchus-Math/hipparchus/issues/337
    // TODO Hipparchus checks submatrices for singularity
    check("Eigenvectors({{1,0,0},\n" //
        + "{-2,1,0},\n"//
        + "{0,1,1}})", //
        "{{0,0,1},{0,0,0},{0,0,0}}");
  }

  @Test
  public void testFromPolarCoordinates() {
    // Evaluation point {r,5} is not a valid set of polar or hyperspherical coordinates.
    check("FromPolarCoordinates({r, 5})", //
        "FromPolarCoordinates({r,5})");
    // Evaluation point {r,Pi,p} is not a valid set of polar or hyperspherical coordinates.
    check("FromPolarCoordinates({r, Pi, p})", //
        "FromPolarCoordinates({r,Pi,p})");

    check("FromPolarCoordinates(SparseArray({r, t}))", //
        "{r*Cos(t),r*Sin(t)}");
    check("FromPolarCoordinates(SparseArray({r, t, p}))", //
        "{r*Cos(t),r*Cos(p)*Sin(t),r*Sin(p)*Sin(t)}");

    check("FromPolarCoordinates({r, t})", //
        "{r*Cos(t),r*Sin(t)}");
    check("FromPolarCoordinates({r, t, p})", //
        "{r*Cos(t),r*Cos(p)*Sin(t),r*Sin(p)*Sin(t)}");
    check("FromPolarCoordinates({{{r, t}, {1,0}}, {{2, Pi}, {1, Pi/2}}})", //
        "{{{r*Cos(t),r*Sin(t)},{1,0}},{{-2,0},{0,1}}}");
  }

  @Test
  public void testFromSphericalCoordinates() {
    check("FromSphericalCoordinates( {r, t, p} )", //
        "{r*Cos(p)*Sin(t),r*Sin(p)*Sin(t),r*Cos(t)}");
    check("FromSphericalCoordinates({{1, Pi/2, 0}, {2, 3/4*Pi, Pi}, {1, Pi/4, Pi/4}})", //
        "{{1,0,0},{-Sqrt(2),0,-Sqrt(2)},{1/2,1/2,1/Sqrt(2)}}");
  }

  @Test
  public void testGroupings() {
    // TODO
    // check("Groupings({{1, 2, 3, 4, 5}, {a, b, c, d, e}}, 3)", //
    // " ");
    check("Groupings({a, b, c, d, e, f}, 3)", //
        "{}");
    check("Groupings({a, b, c, d, e, f}, 2)", //
        "{{{{{{a,b},c},d},e},f},{a,{{{{b,c},d},e},f}},{{a,{{{b,c},d},e}},f},{a,{b,{{{c,d},e},f}}},{{{a,{{b,c},d}},e},f},{a,{{b,{{c,d},e}},f}},{{a,{b,{{c,d},e}}},f},{a,{b,{c,{{d,e},f}}}},{{{{a,{b,c}},d},e},f},{a,{{{b,{c,d}},e},f}},{{a,{{b,{c,d}},e}},f},{a,{b,{{c,{d,e}},f}}},{{{a,{b,{c,d}}},e},f},{a,{{b,{c,{d,e}}},f}},{{a,{b,{c,{d,e}}}},f},{a,{b,{c,{d,{e,f}}}}},{{{{a,b},{c,d}},e},f},{a,{{{b,c},{d,e}},f}},{{a,{{b,c},{d,e}}},f},{a,{b,{{c,d},{e,f}}}},{{{{a,b},c},{d,e}},f},{a,{{{b,c},d},{e,f}}},{{{a,b},{{c,d},e}},f},{a,{{b,c},{{d,e},f}}},{{{a,{b,c}},{d,e}},f},{a,{{b,{c,d}},{e,f}}},{{{a,b},{c,{d,e}}},f},{a,{{b,c},{d,{e,f}}}},{{{{a,b},c},d},{e,f}},{{a,b},{{{c,d},e},f}},{{a,{{b,c},d}},{e,f}},{{a,b},{c,{{d,e},f}}},{{{a,{b,c}},d},{e,f}},{{a,b},{{c,{d,e}},f}},{{a,{b,{c,d}}},{e,f}},{{a,b},{c,{d,{e,f}}}},{{{a,b},{c,d}},{e,f}},{{a,b},{{c,d},{e,f}}},{{{a,b},c},{{d,e},f}},{{{a,b},c},{d,{e,f}}},{{a,{b,c}},{{d,e},f}},{{a,{b,c}},{d,{e,f}}}}");
    check("Groupings({a, b, c, d, e}, 2)", //
        "{{{{{a,b},c},d},e},{a,{{{b,c},d},e}},{{a,{{b,c},d}},e},{a,{b,{{c,d},e}}},{{{a,{b,c}},d},e},{a,{{b,{c,d}},e}},{{a,{b,{c,d}}},e},{a,{b,{c,{d,e}}}},{{{a,b},{c,d}},e},{a,{{b,c},{d,e}}},{{{a,b},c},{d,e}},{{a,b},{{c,d},e}},{{a,{b,c}},{d,e}},{{a,b},{c,{d,e}}}}");
    check("Groupings({a, b, c, d, e}, 3)", //
        "{{{a,b,c},d,e},{a,{b,c,d},e},{a,b,{c,d,e}}}");
    check("Groupings(5, 3)", //
        "{{{1,2,3},4,5},{1,{2,3,4},5},{1,2,{3,4,5}}}");
    check("Groupings(4, 2)", //
        "{{{{1,2},3},4},{1,{{2,3},4}},{{1,{2,3}},4},{1,{2,{3,4}}},{{1,2},{3,4}}}");
  }

  @Test
  public void testHankelMatrix() {

    // no message
    check("HankelMatrix({x, y, z}, {z, a, b, c, d})", //
        "{{x,y,z,a,b},\n"//
            + " {y,z,a,b,c},\n"//
            + " {z,a,b,c,d}}");

    check("HankelMatrix({f, a, b, c, d},{d, y, z})", //
        "{{f,a,b},\n"//
            + " {a,b,c},\n"//
            + " {b,c,d},\n"//
            + " {c,d,y},\n"//
            + " {d,y,z}}");

    // Warning: the column element z and row element f at positions 3 and 1 are not
    // the same. Using column element.
    check("HankelMatrix({x, y, z}, {f, a, b, c, d})", //
        "{{x,y,z,a,b},\n"//
            + " {y,z,a,b,c},\n"//
            + " {z,a,b,c,d}}");
    // Warning: the column element d and row element x at positions 5 and 1 are not
    // the same. Using column element.
    check("HankelMatrix({f, a, b, c, d},{x, y, z})", //
        "{{f,a,b},\n"//
            + " {a,b,c},\n"//
            + " {b,c,d},\n"//
            + " {c,d,y},\n"//
            + " {d,y,z}}");
    check("HankelMatrix({a,b,c,d})", //
        "{{a,b,c,d},\n"//
            + " {b,c,d,0},\n"//
            + " {c,d,0,0},\n"//
            + " {d,0,0,0}}");
    check("HankelMatrix(4)", //
        "{{1,2,3,4},\n" //
            + " {2,3,4,0},\n" //
            + " {3,4,0,0},\n" //
            + " {4,0,0,0}}");
  }

  @Test
  public void testHermiteDecomposition() {
    check("m={{1,2,3},{5,4,3},{8,7,9}}", //
        "{{1,2,3},{5,4,3},{8,7,9}}");
    check("{u,r}=HermiteDecomposition(m)", //
        "{{{1,0,0},{3,1,-1},{-1,-3,2}},{{1,2,3},{0,3,3},{0,0,6}}}");
    check("Abs(Det(u))", //
        "1");
    check("u.m==r", //
        "True");

    check("m2={{1,2,3},{5,4,3}}", //
        "{{1,2,3},{5,4,3}}");
    check("{u,r}=HermiteDecomposition(m2)", //
        "{{{1,0},{5,-1}},{{1,2,3},{0,6,12}}}");

    // test singular matrix
    check("m={{1,2,3},{4,5,6},{7,8,9}}", //
        "{{1,2,3},{4,5,6},{7,8,9}}");
    check("{u,r}=HermiteDecomposition(m)", //
        "{{{1,0,0},{4,-1,0},{1,-2,1}},{{1,2,3},{0,3,6},{0,0,0}}}");
    check("{u,r}=HermiteDecomposition({{2,0,0,0},{0,2,0,0},{0,0,2,0},{0,0,0,2}})", //
        "{{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}},{{2,0,0,0},{0,2,0,0},{0,0,2,0},{0,0,0,\n"
            + "2}}}");
    check("{u,r}=HermiteDecomposition({{4,12,-3,5},{2,6,1,0},{-1,0,5,2},{8,4,10,1}})", //
        "{{{127,-264,99,15},{69,-143,54,8},{51,-106,40,6},{128,-266,100,15}},{{1,0,0,848},{\n"
            + "0,2,0,461},{0,0,1,341},{0,0,0,855}}}");
    check("{u,r}=HermiteDecomposition({{3,0,1,1},{0,1,0,0},{0,0,19,1},{0,0,0,3}})", //
        "{{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}},{{3,0,1,1},{0,1,0,0},{0,0,19,1},{0,0,\n"
            + "0,3}}}");
    check(
        "{u,r}=HermiteDecomposition({{ 5, 2, -1, 4, 0}, {3,8,2,0,6}, {1,-4,5,2,1}, {0,3,7,9,2}, {6,1,0,5,8}})", //
        "{{{-298,6,-69,5,257},{-51,1,-12,1,44},{-457,9,-106,8,394},{-270,5,-63,5,233},{-\n"
            + "703,14,-163,12,606}},{{1,0,0,0,2033},{0,1,0,1,348},{0,0,1,2,3116},{0,0,0,4,1841},{\n"
            + "0,0,0,0,4793}}}");
  }

  @Test
  public void testHermitianMatrixQ() {
    // example from https://en.wikipedia.org/wiki/Hermitian_matrix
    check("HermitianMatrixQ({{2, 2 + I, 4}, {2-I, 3, I}, {4, -I, 1}})", //
        "True");

    check("HermitianMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})", //
        "True");
    check("HermitianMatrixQ({{1, 3 + 3*I}, {3 - 4*I, 2}})", //
        "False");
    check("HermitianMatrixQ(Table(Re(i)*Re(j), {i, 10}, {j, 10}))", //
        "True");
  }

  @Test
  public void testHessenbergDecomposition() {
    check("m = ({\n" //
        + "    {1.0, 2.9, 3.8, 4.7},\n" //
        + "    {5.6, 6.5, 7.4, 8.3},\n" //
        + "    {9.2, 10.1, 11.0, 12.9},\n" //
        + "    {13.8, 14.7, 15.6, 16.5}\n" //
        + "   });", //
        "");

    check("{p, h} = HessenbergDecomposition(m)", //
        "{\n" //
            + "{{1.0,0.0,0.0,0.0},\n" //
            + " {0.0,-0.319901,-0.662596,-0.677222},\n" //
            + " {0.0,-0.525551,-0.470638,0.708728},\n" //
            + " {0.0,-0.788327,0.582638,-0.197671}},\n" //
            + "{{1.0,-6.62994,-0.971553,-0.199829},\n" //
            + " {-17.50543,34.5077,9.12041,2.79619},\n" //
            + " {0.0,2.22233,-0.34559,0.0200774},\n" //
            + " {0.0,0.0,0.391391,-0.162112}}}");

    check("UpperTriangularMatrixQ(h, -1)", //
        "True");

    check("{p, h} = HessenbergDecomposition({{2.0,5,8,7},{5,2,2,8},{7,5,6,6},{5,4,4,8}})", //
        "{\n" //
            + "{{1.0,0.0,0.0,0.0},\n" //
            + " {0.0,-0.502519,-0.475751,-0.721897},\n" //
            + " {0.0,-0.703526,-0.260306,0.66128},\n" //
            + " {0.0,-0.502519,0.84018,-0.203895}},\n" //
            + "{{2.0,-11.65844,1.42005,0.253491},\n" //
            + " {-9.94987,14.53535,-5.31022,2.43082},\n" //
            + " {0.0,-1.83299,0.3897,-0.51527},\n" //
            + " {0.0,0.0,-3.8319,1.07495}}}");
  }

  @Test
  public void testHilbertMatrix() {
    check("HilbertMatrix(4)", //
        "{{1,1/2,1/3,1/4},\n" //
            + " {1/2,1/3,1/4,1/5},\n" //
            + " {1/3,1/4,1/5,1/6},\n" //
            + " {1/4,1/5,1/6,1/7}}");
    check("HilbertMatrix({2,3})", //
        "{{1,1/2,1/3},\n" //
            + " {1/2,1/3,1/4}}");
    check("HilbertMatrix({3,2})", //
        "{{1,1/2},\n" //
            + " {1/2,1/3},\n"//
            + " {1/3,1/4}}");

    check("Inverse(HilbertMatrix(3))", //
        "{{9,-36,30},\n" //
            + " {-36,192,-180},\n" //
            + " {30,-180,180}}");
  }

  @Test
  public void testHilbertMatrixApfloat() {
    IAST expr = F.HilbertMatrix(ApfloatNum.valueOf(new Apint("2")));
    ExprEvaluator exprEvaluator = new ExprEvaluator();
    IExpr result = exprEvaluator.eval(expr);
    assertEquals(result.toString(), //
        "{{1,1/2},\n" //
            + " {1/2,1/3}}");
  }

  @Test
  public void testIdentityMatrix() {
    check("IdentityMatrix(0)", //
        "{}");
    check("IdentityMatrix(1)", //
        "{{1}}");
    check("IdentityMatrix(3)", //
        "{{1,0,0},\n" //
            + " {0,1,0},\n" //
            + " {0,0,1}}");

    check("IdentityMatrix(3,SparseArray) // MatrixForm", //
        "{{1,0,0},\n" //
            + " {0,1,0},\n" //
            + " {0,0,1}}");

    check("IdentityMatrix(3,List)", //
        "{{1,0,0},\n" //
            + " {0,1,0},\n" //
            + " {0,0,1}}");

    check("IdentityMatrix(3)", //
        "{{1,0,0},\n" //
            + " {0,1,0},\n" //
            + " {0,0,1}}");
  }

  @Test
  public void testInverse() {
    // message Inverse: Matrix {{0,1},{0,0}} is singular.
    check("Inverse({{0, 1.0}, {0, 0}})", //
        "Inverse(\n" //
            + "{{0,1.0},\n" //
            + " {0,0}})");

    check("Inverse(s*{{1,0,0},{0,1,0},{0,0,1}}-{{-1,1,1},{-4,-4,1},{1,1,1}})", //
        "{{(-5+3*s+s^2)/(-10+s+4*s^2+s^3),s/(-10+s+4*s^2+s^3),(5+s)/(-10+s+4*s^2+s^3)},\n"
            + " {(5-4*s)/(-10+s+4*s^2+s^3),(-2+s^2)/(-10+s+4*s^2+s^3),(-3+s)/(-10+s+4*s^2+s^3)},\n"
            + " {s/(-10+s+4*s^2+s^3),(2+s)/(-10+s+4*s^2+s^3),(8+5*s+s^2)/(-10+s+4*s^2+s^3)}}");
    check("N(Inverse({{1,2.0},{3,4}}),50)", //
        "{{-2,1},{1.5,-0.5}}");

    check("Inverse({{1,2},{3,4}})", "{{-2,1},\n" + " {3/2,-1/2}}");
    check("Inverse({{1,2.0},{3,4}})", "{{-2.0,1.0},\n" + " {1.5,-0.5}}");

    check("Inverse(SparseArray({{1, 2, 0}, {2, 3, 0}, {3, 4, 1}}))", //
        "{{-3,2,0},\n" //
            + " {2,-1,0},\n" + " {1,-2,1}}");

    check("Inverse(-2)", //
        "Inverse(-2)");
    check("Inverse({{}})", //
        "Inverse({{}})");
    check("Inverse({{a,b,c}, {d,e,f}, {x,y,z}})", //
        "{{(f*y-e*z)/(c*e*x-b*f*x-c*d*y+a*f*y+b*d*z-a*e*z),(-c*y+b*z)/(c*e*x-b*f*x-c*d*y+a*f*y+b*d*z-a*e*z),(c*e-b*f)/(c*e*x-b*f*x-c*d*y+a*f*y+b*d*z-a*e*z)},\n"
            + " {(f*x-d*z)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z),(-c*x+a*z)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z),(c*d-a*f)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z)},\n"
            + " {(-e*x+d*y)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z),(b*x-a*y)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z),(-b*d+a*e)/(-c*e*x+b*f*x+c*d*y-a*f*y-b*d*z+a*e*z)}}");
    check("Inverse({{1, 2, 0}, {2, 3, 0}, {3, 4, 1}})", //
        "{{-3,2,0},\n" //
            + " {2,-1,0},\n" + " {1,-2,1}}");
    check("Inverse({{1, 0}, {0, 0}})", //
        "Inverse(\n" //
            + "{{1,0},\n" + " {0,0}})");
    check("Inverse({{1, 0, 0}, {0, Sqrt(3)/2, 1/2}, {0,-1 / 2, Sqrt(3)/2}})", //
        "{{1,0,0},\n" //
            + " {0,Sqrt(3)/2,-1/2},\n" + " {0,1/2,Sqrt(3)/2}}");
    check("Inverse({{u, v}, {v, u}})", //
        "{{u/(u^2-v^2),-v/(u^2-v^2)},\n" //
            + " {-v/(u^2-v^2),u/(u^2-v^2)}}");
    check("Inverse({{1.4, 2}, {3, -6.7}})", //
        "{{0.435631,0.130039},\n" //
            + " {0.195059,-0.0910273}}");
    check("Inverse(HilbertMatrix(5))", //
        "{{25,-300,1050,-1400,630},\n" //
            + " {-300,4800,-18900,26880,-12600},\n" + " {1050,-18900,79380,-117600,56700},\n"
            + " {-1400,26880,-117600,179200,-88200},\n" + " {630,-12600,56700,-88200,44100}}");
    check("Inverse({{u, v}, {v, u}}).{{u, v}, {v, u}}  // Simplify", //
        "{{1,0}," //
            + "{0,1}}");
    check("Inverse({{1,2}, {1,2}})", //
        "Inverse(\n" //
            + "{{1,2},\n" + " {1,2}})");
    check("m = {{a, b}, {c, d}};Inverse(m).m ", //
        "{{1,0},\n" //
            + " {0,1}}");
  }

  @Test
  public void testLeastSquares() {
    // LeastSquares[{{3.2, 2.2, 1.2}, {2.1, 7.1, 8.5}, {9.5, 6.7, 3.7}}, {7, 8, 9}]
    check("LeastSquares({{3.2, 2.2, 1.2}, {2.1, 7.1, 8.5}, {9.5, 6.7, 3.7}}, {7, 8, 9})", //
        "{73.94988,-174.3795,128.3294}");
    // {-1577780898195/827587904419-11087326045520/827587904419*I,
    // 35583840059240/5793115330933+275839049310660/5793115330933*I,
    // -3352155369084/827587904419-28321055437140/827587904419*I}
    check("LeastSquares({{1,1},{1,2},{1,3.0}},{})", //
        "LeastSquares(\n" + //
            "{{1,1},\n" + //
            " {1,2},\n" + //
            " {1,3.0}},{})");
    check("Table(Complex(i,Rational(2 * i + 2 + j, 1 + 9 * i + j)),{i,0,3},{j,0,2})", //
        "{{I*2,I*3/2,I*4/3},{1+I*2/5,1+I*5/11,1+I*1/2},{2+I*6/19,2+I*7/20,2+I*8/21},{3+\n"
            + "I*2/7,3+I*9/29,3+I*1/3}}");
    check(
        "LeastSquares(Table(Complex(i,Rational(2 * i + 2 + j, 1 + 9 * i + j)),{i,0,3},{j,0,2}), {1,1,1,1})", //
        "{-1577780898195/827587904419-I*11087326045520/827587904419,35583840059240/\n"
            + "5793115330933+I*275839049310660/5793115330933,-3352155369084/827587904419-\n"
            + "I*28321055437140/827587904419}");

    check("LeastSquares({{1, 1}, {1, 2}, {1, 3.0}}, {7, 7, 8})", //
        "{6.33333,0.5}");
    check("LeastSquares(SparseArray({{1, 1}, {1, 2}, {1, 3.0}}), SparseArray({7, 7, 8}))", //
        "{6.33333,0.5}");

    check("LeastSquares({{1, 1}, {1, 2}, {1, 3}}, {7, 7, 8})", //
        "{19/3,1/2}");
    check("LeastSquares({{1, 1}, {1, 2}, {1, 3}}, {7, 7, x})", //
        "{35/3-2/3*x,-7/2+x/2}");
  }

  @Test
  public void testLinearSolve() {
    check(
        "LinearSolve({ { 1/10, 6/5, 1/9 },{ 1, 59/45, 1/10 },{6/5, 1/10, 1/9 } },{ 1/10, 6/5, 1/9 })", //
        "{99109/101673,10898/11297,-9034/869}");
    check(
        "{ { 1/10, 6/5, 1/9 },{ 1, 59/45, 1/10 },{6/5, 1/10, 1/9 } }.{99109/101673,10898/11297,-9034/869}", //
        "{1/10,6/5,1/9}");
    check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1,1,1})", //
        "{-1,1,0}");
    // github issue #44
    check("LinearSolve({{1,0,-1,0},{0,1,0,-1},{1,-2,-1,0},{-1,0,3,1}}," //
        + "{0.06,0.06,-0.4,-0.06})", //
        "{-0.025,0.23,-0.085,0.17}");

    check("LinearSolve({{a, b, c, d}}, {x})", //
        "{x/a,0,0,0}");
    check("LinearSolve({{a, b,c,d,e}, {f,g,h,i,j}}, {x, y})", //
        "{(g*x-b*y)/(-b*f+a*g),(-f*x+a*y)/(-b*f+a*g),0,0,0}");
    check("LinearSolve({{a,b,c,d,e}, {f,g,h,i,j}, {k,l,m,n,o}}, {x,y,z})", //
        "{(h*l*x-g*m*x-c*l*y+b*m*y+c*g*z-b*h*z)/(c*g*k-b*h*k-c*f*l+a*h*l+b*f*m-a*g*m),(-h*k*x+f*m*x+c*k*y-a*m*y-c*f*z+a*h*z)/(c*g*k-b*h*k-c*f*l+a*h*l+b*f*m-a*g*m),(g*k*x-f*l*x-b*k*y+a*l*y+b*f*z-a*g*z)/(c*g*k-b*h*k-c*f*l+a*h*l+b*f*m-a*g*m),\n" //
            + "0,0}");
    // underdetermined system:
    check("LinearSolve({{1, 2, 3}, {4, 5, 6}}, {6, 15})", //
        "{0,3,0}");
    // linear equations have no solution
    check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, -2, 1})", //
        "LinearSolve(\n" + "{{1,2,3},\n" + " {4,5,6},\n" + " {7,8,9}},{1,-2,1})");

    check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, 1, 1})", //
        "{-1,1,0}");
    check("LinearSolve({{1, 2}, {3, 4}}, {1, {2}})", //
        "LinearSolve(\n" + "{{1,2},\n" + " {3,4}},{1,{2}})");

    check("LinearSolve({{1, 1, 1}, {1, 2, 3}, {1, 4, 9}}, {1, 2, 3})", //
        "{-1/2,2,-1/2}");
    check("LinearSolve(N({{1, 1, 1}, {1, 2, 3}, {1, 4, 9}}), N({1, 2, 3}))", //
        "{-0.5,2.0,-0.5}");
    check("LinearSolve({{a, b}, {c, d}}, {x, y})", //
        "{(d*x-b*y)/(-b*c+a*d),(-c*x+a*y)/(-b*c+a*d)}");
    check("LinearSolve({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}}, {1, 2, 3})", //
        "{0,1,2}");
    check("{{1, 1, 0}, {1, 0, 1}, {0, 1, 1}} . {0, 1, 2}", //
        "{1,2,3}");
    check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, 1, 1})", //
        "{-1,1,0}");
    check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, -2, 3})", //
        "LinearSolve(\n" + "{{1,2,3},\n" + " {4,5,6},\n" + " {7,8,9}},{1,-2,3})");
    check("LinearSolve({1, {2}}, {1, 2})", //
        "LinearSolve({1,{2}},{1,2})");
  }

  @Test
  public void testLinearSolveFunction001() {
    check("lsf=LinearSolve({{1, 2}, {3, 4}})", //
        "LinearSolveFunction(Matrix dimensions: {2,2})");
    check("lsf[{5, 6}]", //
        "{-4,9/2}");
    check("lsf[{{5, 6}, {7, 8}}]", //
        "{{-3,-4},\n" //
            + " {4,5}}");
    check("lsf[{{5,6,7}, {8,9,10}}]", //
        "{{-2,-3,-4},\n" //
            + " {7/2,9/2,11/2}}");
    // error: LinearSolveFunction: Coefficient matrix and target vector or matrix do not have the
    // same dimensions.
    check("lsf[{{5, 6}, {7, 8},{9,10}}]", //
        "LinearSolveFunction(Matrix dimensions: {2,2})[\n" //
            + "{{5,6},\n" + " {7,8},\n" + " {9,10}}]");
  }

  @Test
  public void testLinearSolveFunction002() {
    check("lsf=LinearSolve(HilbertMatrix(6));", //
        "");
    check("lsf[{1,1,1,1,1,1}]", //
        "{-6,210,-1680,5040,-6300,2772}");
  }

  @Test
  public void testLinearSolveFunction003() {
    check("lsf=LinearSolve(N(HilbertMatrix(6)));", //
        "");
    check("lsf[{1,1,1,1,1,1}]", //
        "{-6.0,210.0,-1680.0,5040.0,-6300.0,2772.0}");
  }

  @Test
  public void testLinearSolveFunction004() {
    check("lsf=LinearSolve(N(HilbertMatrix(6),30));", //
        "");
    check("lsf[{1,1,1,1,1,1}]", //
        "{-5.99999999999999999999999999332,209.999999999999999999999999809,-1679.9999999999999999999999987," //
            + "5039.99999999999999999999999662,-6299.99999999999999999999999627,2771.99999999999999999999999853}");
  }

  @Test
  public void testLowerTriangularize() {

    check("LowerTriangularize({{1,0},{{1,0},{0,1}}})", //
        "LowerTriangularize({{1,0},{{1,0},{0,1}}})");

    check("LowerTriangularize(SparseArray({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}}))", //
        "{{a,0,0,0},\n" + " {d,e,0,0},\n" + " {h,i,j,0},\n" + " {l,m,n,o}}");
    check("LowerTriangularize({{1,0}, {0,1}},{})", //
        "LowerTriangularize({{1,0},{0,1}},{})");
    check("LowerTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}}, -1)", //
        "{{0,0,0,0},\n" + " {d,0,0,0},\n" + " {h,i,0,0},\n" + " {l,m,n,0}}");
    check("LowerTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}})", //
        "{{a,0,0,0},\n" + " {d,e,0,0},\n" + " {h,i,j,0}}");

    check("LowerTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}})", //
        "{{a,0,0,0},\n" + " {d,e,0,0},\n" + " {h,i,j,0},\n" + " {l,m,n,o}}");
  }

  @Test
  public void testLowerTriangularMatrixQ() {
    check("m={{0, 0, 0}, {1, 2, 0}, {2, 3, 0}}", //
        "{{0,0,0},{1,2,0},{2,3,0}}");
    check("LowerTriangularMatrixQ(m,-1)", //
        "False");
    check("LowerTriangularMatrixQ(m)", //
        "True");

    check("s = SparseArray({{1, 1} -> 2, {2, 1} -> 1, {3, 2} -> 5}, {3, 3});", //
        "");
    check("LowerTriangularMatrixQ(s)", //
        "True");
  }

  @Test
  public void testLUDecomposition() {
    // check("LUDecomposition({{1, 2, 3}, {3, 4, 11}, {13, 7, 8}})",
    // "{{{1,2,3},{3,-2,2},{13,19/2,-50}},{1,2,3},0}");
    check("LUDecomposition({{1, 2, 3}, {3, 4, 11}, {13, 7, 8}})", //
        "{\n" + "{{1,0,0},\n" //
            + " {3,1,0},\n" //
            + " {13,19/2,1}},\n" //
            + "{{1,2,3},\n" //
            + " {0,-2,2},\n" //
            + " {0,0,-50}},{1,2,3}}");

    // check(
    // "LUBackSubstitution({{{1,2,3},{3,-2,2},{13,19/2,-50}},{1,2,3},0},{10,11,12})"
    // ,
    // "{-11/4,33/4,-5/4}");



    // check("LUDecomposition({{1,2},{3,4}})", "{{{1,2},{3,-2}},{1,2},0}");
    check("LUDecomposition({{0}},Sqrt(2)/2)", //
        "LUDecomposition(\n" + "{{0}},1/Sqrt(2))");
    check("LUDecomposition({{1,2},{3,4}})", "{\n" //
        + "{{1,0},\n" + " {3,1}},\n" + "{{1,2},\n" + " {0,-2}},{1,2}}");
    check("LUDecomposition({{1,1},{5,-8}})", "{\n" //
        + "{{1,0},\n" + " {5,1}},\n" + "{{1,1},\n" + " {0,-13}},{1,2}}");
  }
  // @Test
  // public void testSystem102() {
  // check("LUBackSubstitution({{{1,2},{3,-2}},{1,2},0},{1,2})", "{0,1/2}");
  // }

  @Test
  public void testMatrices() {
    check("Table(a(i0, j), {i0, 2}, {j, 2})", //
        "{{a(1,1),a(1,2)},{a(2,1),a(2,2)}}");
    check("Array(a, {2, 2})", //
        "{{a(1,1),a(1,2)},{a(2,1),a(2,2)}}");
    check("ConstantArray(0, {3, 2})", //
        "{{0,0},{0,0},{0,0}}");
    check("DiagonalMatrix({a, b, c})", //
        "{{a,0,0},\n" + " {0,b,0},\n" + " {0,0,c}}");
    check("IdentityMatrix(3)", //
        "{{1,0,0},\n" //
            + " {0,1,0},\n"//
            + " {0,0,1}}");
    check("IdentityMatrix({2,3})", //
        "{{1,0,0},\n" //
            + " {0,1,0}}");
  }

  @Test
  public void testMatrixExp() {
    check("MatrixExp({{0, 1}, {-1, 0}}*t)", //
        "{{Cos(t),Sin(t)},{-Sin(t),Cos(t)}}");
    check("MatrixExp({{0, a}, {b, 0}})", //
        "{{1/(2*E^Sqrt(a*b))+E^Sqrt(a*b)/2,-a/(2*Sqrt(a*b)*E^Sqrt(a*b))+(a*E^Sqrt(a*b))/(\n" //
            + "2*Sqrt(a*b))},{-b/(2*Sqrt(a*b)*E^Sqrt(a*b))+(b*E^Sqrt(a*b))/(2*Sqrt(a*b)),1/(2*E^Sqrt(a*b))+E^Sqrt(a*b)/\n"
            + "2}}");
    check("MatrixExp({{a,b}, {c,d}})", //
        "{{((-a+d+Sqrt(a^2+4*b*c-2*a*d+d^2))*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/(2*Sqrt(a^\n" //
            + "2+4*b*c-2*a*d+d^2))+((a-d+Sqrt(a^2+4*b*c-2*a*d+d^2))*E^(a/2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^\n" //
            + "2)/2))/(2*Sqrt(a^2+4*b*c-2*a*d+d^2)),(-b*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^\n" //
            + "2+4*b*c-2*a*d+d^2)+(b*E^(a/2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^2+4*b*c-2*a*d+d^\n" //
            + "2)},{(-c*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^2+4*b*c-2*a*d+d^2)+(c*E^(a/\n" //
            + "2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/Sqrt(a^2+4*b*c-2*a*d+d^2),((a-d+Sqrt(a^2+4*b*c-\n" //
            + "2*a*d+d^2))*E^(a/2+d/2-Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/(2*Sqrt(a^2+4*b*c-2*a*d+d^2))+((-a+d+Sqrt(a^\n" //
            + "2+4*b*c-2*a*d+d^2))*E^(a/2+d/2+Sqrt(a^2+4*b*c-2*a*d+d^2)/2))/(2*Sqrt(a^2+4*b*c-2*a*d+d^\n" //
            + "2))}}");

    check("MatrixExp({{3.4, 1.2}, {0.001, -0.9}})", //
        "{{29.97054,8.24991},\n" + //
            " {0.00687492,0.408375}}");
    check("MatrixExp({{1.2, 5.6}, {3, 4}})", //
        "{{346.5575,661.7346},\n" + //
            " {354.5007,677.4248}}");
    check("MatrixExp({{2, 0, 0}, {0, 1, -1}, {0, 1, 1}})", //
        "{{7.38906,0.0,0.0},\n" + //
            " {0.0,1.46869,-2.28736},\n" + //
            " {0.0,2.28736,1.46869}}");

  }

  @Test
  public void testMatrixMinimalPolynomial() {
    check("MatrixMinimalPolynomial({{0,0},{0,0}}, {{-1}})", //
        "MatrixMinimalPolynomial({{0,0},{0,0}},{{-1}})");

    // wikipedia
    check("MatrixMinimalPolynomial({{1, -1, -1}, {1, -2, 1}, {0, 1, -3}}, x)", //
        "-1+x+4*x^2+x^3");

    check("MatrixMinimalPolynomial({{2, 0}, {0, 2}}, x)", //
        "-2+x");
    check("MatrixMinimalPolynomial({{3, -1, 0}, {0, 2, 0}, {1, -1, 2}}, x)", //
        "6-5*x+x^2");
    check("CharacteristicPolynomial({{3, -1, 0}, {0, 2, 0}, {1, -1, 2}}, x)", //
        "-12+16*x-7*x^2+x^3");
    check("Factor(6-5*x+x^2)", //
        "(-3+x)*(-2+x)");
    check("Factor(12-16*x+7*x^2-x^3)", //
        "(2-x)^2*(3-x)");
  }

  @Test
  public void testMatrixPower() {
    check("MatrixPower({{a,2},{3,4}},3)", //
        "{{24+12*a+a^3,2*(22+4*a+a^2)},\n" //
            + " {3*(22+4*a+a^2),112+6*a}}");


    // github #121 - print error
    check("MatrixPower({{2},{1}},2)", //
        "MatrixPower({{2},{1}},2)");
    check("MatrixPower({{1, 0}, {0}}, 2)", //
        "MatrixPower({{1,0},{0}},2)");
    check("MatrixPower({{1,0}, {0,1}},2147483647)", //
        "MatrixPower({{1,0},{0,1}},2147483647)");

    check("MatrixPower({{1, 2}, {2, 5}}, -3)", //
        "{{169,-70},\n" + //
            " {-70,29}}");
    check("MatrixPower({{1, 2}, {1, 1}}, 10)", //
        "{{3363,4756},\n" + //
            " {2378,3363}}");

    check("MatrixPower({{1,2},{3,4}},3)", //
        "{{37,54},\n" //
            + " {81,118}}");
    check("MatrixPower({{1,2},{3,4}},1)", //
        "{{1,2},\n {3,4}}");
    check("MatrixPower({{1,2},{3,4}},0)", //
        "{{1,0},\n"//
            + " {0,1}}");
  }

  @Test
  public void testMatrixQ() {
    check("MatrixQ({{1, 2, 3}, {3, 4, 11}, {13, 7, 8}})", //
        "True");
    check("MatrixQ({{1, 2, 3}, {3, 4, 11}, {13, 7, 8, 6}})", //
        "False");

    check("MatrixQ( )", //
        "MatrixQ()");
    check("MatrixQ({})", //
        "False");
    check("MatrixQ({{}})", //
        "True");
    check("MatrixQ({{}}, NumberQ)", //
        "True");
    check("MatrixQ({{a, b, f}, {c, d, e}})", //
        "True");
    check("MatrixQ(SparseArray({{1, 3}, {4.0, 3/2}}), NumberQ)", //
        "True");
    check("MatrixQ({{1, 3}, {4.0, 3/2}}, NumberQ)", //
        "True");
  }

  @Test
  public void testMatrixRank() {
    // github #232
    check("RowReduce({{1,2,3,4,5},{2,4,6,8,11},{3,6,9,12,14},{4,8,12,16,20}})", //
        "{{1,2,3,4,0},\n" //
            + " {0,0,0,0,1},\n" + " {0,0,0,0,0},\n" + " {0,0,0,0,0}}");
    check("MatrixRank({{1,2,3,4,5},{2,4,6,8,11},{3,6,9,12,14},{4,8,12,16,20}})", //
        "2");

    check("MatrixRank({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}})", //
        "3");

    check("MatrixRank({{a, b}, {3*a, 3*b}})", //
        "1");

    check("MatrixRank({{1, 0}, {0}})", //
        "MatrixRank({{1,0},{0}})");

    check("MatrixRank({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "2");

    check("MatrixRank({{1, 0}, {3, 2}, {7, 2}, {8, 1}})", //
        "2");

    check("MatrixRank({{a, b}, {c, d}})", //
        "2");

    check("MatrixRank({{a, b}, {2*a, 2*b}})", //
        "1");

    check("MatrixRank({{1., 2., 3.}, {4., 5., 6.}, {7., 8., 9.}})", //
        "2");

    check("MatrixRank({{1, I}, {I, -1}})", //
        "1");

    check("MatrixRank({{1, 2, 3, 4.0 },\n" + //
        "{ 1, 1, 1, 1 },\n" + //
        "{ 2, 3, 4, 5 },\n" + //
        "{ 2, 2, 2, 2 }})", //
        "2");

    check("MatrixRank({{ 1.0, 2.0, 3.0, 4.0 },\n" + //
        "{ 1.0, 1.0, 1.0, 1.0 },\n" + //
        "{ 2.0, 3.0, 4.0, 5.0 },\n" + //
        "{ 2.0, 2.0, 2.0, 2.0 }})", //
        "2");
  }

  @Test
  public void testMinors() {
    check("Minors({{a, b, c}}, 1)", //
        "{{a,b,c}}");
    check("Minors({{a, b, c}}, 2)", //
        "{}");

    check("Minors({{a, b, c},{d, e, f}},0)", //
        "{{1}}");
    check("Minors({{a, b, c},{d, e, f}},1)", //
        "{{a,b,c},\n" //
            + " {d,e,f}}");
    check("Minors({{a, b, c},{d, e, f}},2)", //
        "{{-b*d+a*e,-c*d+a*f,-c*e+b*f}}");
    check("Minors({{a, b, c},{d, e, f}},3)", //
        "{}");
    check("Minors({{a, b},{c,d},{e, f}},1)", //
        "{{a,b},\n" //
            + " {c,d},\n" //
            + " {e,f}}");
    check("Minors({{a, b},{c,d},{e, f}},2)", //
        "{{-b*c+a*d},\n" //
            + " {-b*e+a*f},\n" //
            + " {-d*e+c*f}}");

    check("(mat = Table(i^2 + i j + j^3, {i, 4}, {j, 4})) // MatrixForm", //
        "{{3,11,31,69},\n" //
            + " {7,16,37,76},\n" //
            + " {13,23,45,85},\n" //
            + " {21,32,55,96}}");
    check("Minors(mat,3) // MatrixForm", //
        "{{-24,-84,-96,-36},\n" //
            + " {-72,-252,-288,-108},\n" //
            + " {-72,-252,-288,-108},\n" //
            + " {-24,-84,-96,-36}}");

    check("Subsets[{a, b, c}, {2}]", //
        "{{a,b},{a,c},{b,c}}");

    check("Minors({{a}})", //
        "{{1}}");
    check("Minors({ {a},{b},{c} })", //
        "{{1}}");
    check("Minors({ { } })", //
        "{}");
    check("Minors({ { },{} })", //
        "{}");


    // https://en.wikipedia.org/wiki/Minor_(linear_algebra)
    check("Minors({{1,4,7},{3,0,5},{-1,9,11}})", //
        "{{-12,-16,20},\n" //
            + " {13,18,-19},\n" //
            + " {27,38,-45}}");

    check("m0 = Array(Subscript(a, ##) &, {3, 3})", //
        "{{Subscript(a,1,1),Subscript(a,1,2),Subscript(a,1,3)},{Subscript(a,2,1),Subscript(a,\n"
            + "2,2),Subscript(a,2,3)},{Subscript(a,3,1),Subscript(a,3,2),Subscript(a,3,3)}}");
    check("Minors(m0)", //
        "{{-Subscript(a,1,2)*Subscript(a,2,1)+Subscript(a,1,1)*Subscript(a,2,2),-Subscript(a,\n" //
            + "1,3)*Subscript(a,2,1)+Subscript(a,1,1)*Subscript(a,2,3),-Subscript(a,1,3)*Subscript(a,\n" //
            + "2,2)+Subscript(a,1,2)*Subscript(a,2,3)},\n" //
            + " {-Subscript(a,1,2)*Subscript(a,3,1)+Subscript(a,1,1)*Subscript(a,3,2),-Subscript(a,\n" //
            + "1,3)*Subscript(a,3,1)+Subscript(a,1,1)*Subscript(a,3,3),-Subscript(a,1,3)*Subscript(a,\n" //
            + "3,2)+Subscript(a,1,2)*Subscript(a,3,3)},\n" //
            + " {-Subscript(a,2,2)*Subscript(a,3,1)+Subscript(a,2,1)*Subscript(a,3,2),-Subscript(a,\n" //
            + "2,3)*Subscript(a,3,1)+Subscript(a,2,1)*Subscript(a,3,3),-Subscript(a,2,3)*Subscript(a,\n" //
            + "3,2)+Subscript(a,2,2)*Subscript(a,3,3)}}");

    check("m1 = Table(i^2 + i j + j^3, {i, 4}, {j, 4})", //
        "{{3,11,31,69},{7,16,37,76},{13,23,45,85},{21,32,55,96}}");
    check("Minors(m1)", //
        "{{-24,-84,-96,-36},\n" //
            + " {-72,-252,-288,-108},\n" //
            + " {-72,-252,-288,-108},\n" //
            + " {-24,-84,-96,-36}}");

    check("Minors(Partition(Range(9), 3))", //
        "{{-3,-6,-3},\n" //
            + " {-6,-12,-6},\n" //
            + " {-3,-6,-3}}"); //

    check("m2 = Partition(Range(16), 4)", //
        "{{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}}");
    check("Minors(m2)", //
        "{{0,0,0,0},\n" //
            + " {0,0,0,0},\n" //
            + " {0,0,0,0},\n" //
            + " {0,0,0,0}}");


    check("(mat = Partition(Range(12), 4)) // MatrixForm", //
        "{{1,2,3,4},\n" //
            + " {5,6,7,8},\n" //
            + " {9,10,11,12}}");

    check("Minors(mat, 2) // MatrixForm", //
        "{{-4,-8,-12,-4,-8,-4},\n" //
            + " {-8,-16,-24,-8,-16,-8},\n" //
            + " {-4,-8,-12,-4,-8,-4}}");
    check("Minors(mat, 2, Identity) // MatrixForm", //
        "{{{{1,2},{5,6}},{{1,3},{5,7}},{{1,4},{5,8}},{{2,3},{6,7}},{{2,4},{6,8}},{{3,4},{\n" //
            + "7,8}}},\n" //
            + " {{{1,2},{9,10}},{{1,3},{9,11}},{{1,4},{9,12}},{{2,3},{10,11}},{{2,4},{10,12}},{{\n" //
            + "3,4},{11,12}}},\n" //
            + " {{{5,6},{9,10}},{{5,7},{9,11}},{{5,8},{9,12}},{{6,7},{10,11}},{{6,8},{10,12}},{{\n" //
            + "7,8},{11,12}}}}");


    check("(mat = Partition(Range(20), 5)) // MatrixForm", //
        "{{1,2,3,4,5},\n" //
            + " {6,7,8,9,10},\n" //
            + " {11,12,13,14,15},\n" //
            + " {16,17,18,19,20}}");

    check("Minors(mat, 4) // MatrixForm", //
        "{{0,0,0,0,0}}");
    check("Minors(mat, 3) // MatrixForm", //
        "{{0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0},\n" //
            + " {0,0,0,0,0,0,0,0,0,0}}");
    check("Minors(mat, 2) // MatrixForm", //
        "{{-5,-10,-15,-20,-5,-10,-15,-5,-10,-5},\n" //
            + " {-10,-20,-30,-40,-10,-20,-30,-10,-20,-10},\n" //
            + " {-15,-30,-45,-60,-15,-30,-45,-15,-30,-15},\n" //
            + " {-5,-10,-15,-20,-5,-10,-15,-5,-10,-5},\n" //
            + " {-10,-20,-30,-40,-10,-20,-30,-10,-20,-10},\n" //
            + " {-5,-10,-15,-20,-5,-10,-15,-5,-10,-5}}");

    check("Table(Minors(IdentityMatrix({4, 4}), k) // MatrixForm, {k, 0, 4})", //
        "{\n" //
            + "{{1}},\n" //
            + "{{1,0,0,0},\n" //
            + " {0,1,0,0},\n" //
            + " {0,0,1,0},\n" //
            + " {0,0,0,1}},\n" //
            + "{{1,0,0,0,0,0},\n" //
            + " {0,1,0,0,0,0},\n" //
            + " {0,0,1,0,0,0},\n" //
            + " {0,0,0,1,0,0},\n" //
            + " {0,0,0,0,1,0},\n" //
            + " {0,0,0,0,0,1}},\n" //
            + "{{1,0,0,0},\n" //
            + " {0,1,0,0},\n" //
            + " {0,0,1,0},\n" //
            + " {0,0,0,1}},\n" //
            + "{{1}}}");

  }

  @Test
  public void testNegativeDefiniteMatrixQ() {
    check("NegativeDefiniteMatrixQ({{5,-1},{-1, 4} })", //
        "False");
    check("NegativeDefiniteMatrixQ({{-2,0},{0,0}})", //
        "False");
    check("NegativeDefiniteMatrixQ({{-5,1},{1,-4}})", //
        "True");
  }

  @Test
  public void testNegativeSemidefiniteMatrixQ() {
    check("NegativeSemidefiniteMatrixQ({{5,-1},{-1, 4} })", //
        "False");
    check("NegativeSemidefiniteMatrixQ({{-2,0},{0,0}})", //
        "True");
    check("NegativeSemidefiniteMatrixQ({{-5,1},{1,-4}})", //
        "True");
  }

  @Test
  public void testNormalMatrixQ() {
    check("NormalMatrixQ({{5 + I, -2*I}, {2, 4 + 2*I}})", //
        "True");
    check("NormalMatrixQ({{1, 2, -1}, {-1, 1, 2}, {2, -1, 1}})", //
        "True");
    check("NormalMatrixQ({{a,b,c}, {d,e,f}, {g,h,i}})", //
        "False");
  }

  @Test
  public void testNullSpace() {
    // TODO improve Zero tests
    // see https://docs.sympy.org/latest/tutorials/intro-tutorial/matrices.html#zero-testing
    check("NullSpace({{-2*Cosh(q/3),Exp(-q),1},{Exp(q),-2*Cosh(q/3),1},{1,1,-2*Cosh(q/3)}})", //
        "{{-(-40*E^q-16*E^(2*q)-8*E^(3*q)+16*E^q*Cosh(q/3)-64*E^(2*q)*Cosh(q/3)+32*E^q*Cosh(q/\n"
            + "3)^2+32*E^(2*q)*Cosh(q/3)^2+64*E^(2*q)*Cosh(q/3)^3-4*Sech(q/3)-8*E^q*Sech(q/3)+8*E^(\n"
            + "2*q)*Sech(q/3)+8*E^q*Sech(q/3)^2+2*E^(2*q)*Sech(q/3)^2+2*E^(3*q)*Sech(q/3)^2+Sech(q/\n"
            + "3)^3+E^q*Sech(q/3)^3+E^(2*q)*Sech(q/3)^3)/(E^(2*q)*(-16*Cosh(q)+4/(3*Cosh(q/3)+Cosh(q))-\n"
            + "12*Sech(q/3))),-(-8-16*E^q-40*E^(2*q)-64*E^q*Cosh(q/3)+16*E^(2*q)*Cosh(q/3)+32*E^q*Cosh(q/\n"
            + "3)^2+32*E^(2*q)*Cosh(q/3)^2+64*E^q*Cosh(q/3)^3+8*E^q*Sech(q/3)-8*E^(2*q)*Sech(q/\n"
            + "3)-4*E^(3*q)*Sech(q/3)+2*Sech(q/3)^2+2*E^q*Sech(q/3)^2+8*E^(2*q)*Sech(q/3)^2+E^q*Sech(q/\n"
            + "3)^3+E^(2*q)*Sech(q/3)^3+E^(3*q)*Sech(q/3)^3)/(E^q*(-4*Cosh(q/3)+Sech(q/3))*(8*Cosh(\n"
            + "2/3*q)+Sech(q/3)^2)),1}}");

    check("NullSpace({{10, 4, -6, -4}, {4, 10, -15, -4}, {0, 0, 0, 0}, {4, 4, -6, 2}} )", //
        "{{0,3,2,0}}");

    // TODO check results:
    check("NullSpace({{1, 2}, {2, 4}})", //
        "{{-2,1}}");
    check("NullSpace({{1.0, 2.0}, {1.0, 2.0}})", //
        "{{-2.0,1}}");

    check("NullSpace( {{1., 2.}, {3., 4.}})", //
        "{}");
    check(
        "NullSpace({{1, 2, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 2, 1}})", //
        "{{-2,1,0}}");

    check("LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {0,0,0})", //
        "{0,0,0}");
    check("NullSpace({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "{{1,-2,1}}");

    check("NullSpace({{-1/3, 0, I}})", //
        "{{I*3,0,1},\n" + " {0,1,0}}");
    check("NullSpace({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "{{1,-2,1}}");
    check("A = {{1, 1, 0}, {1, 0, 1}, {0, 1, 1}}", //
        "{{1,1,0},{1,0,1},{0,1,1}}");
    check("NullSpace(A)", //
        "{}");
    check("MatrixRank(A)", //
        "3");
    check("NullSpace({1, {2}})", //
        "NullSpace({1,{2}})");

    check(
        "NullSpace({{1, 2, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n" + "{0, 0, 0},\n"
            + "{0, 0, 1},\n" //
            + "{0, 0, -1},\n" + "{1, 2, 1}})", //
        "{{-2,1,0}}");
    check("NullSpace({{1,2,3},{4,5,6},{7,8,9}})", //
        "{{1,-2,1}}");
    check("NullSpace({{1,1,0,1,5},{1,0,0,2,2},{0,0,1,4,-1},{0,0,0,0,0}})",
        "{{-2,1,-4,1,0},\n" + " {-2,-3,1,0,1}}");
    check("NullSpace({{a,b,c}," + "{c,b,a}})", //
        "{{1,-(a+c)/b,1}}");
    check("NullSpace({{1,2,3}," + "{5,6,7}," + "{9,10,11}})", "{{1,-2,1}}");
    check("NullSpace({{1,2,3,4}," //
        + "{5,6,7,8}," //
        + "{9,10,11,12}})", //
        "{{2,-3,0,1},\n" //
            + " {1,-2,1,0}}");
    check("(-1/2+I*1/2)*(-I)", //
        "1/2+I*1/2");
    check("NullSpace({{1+I,1-I}, {-1+I,1+I}})", //
        "{{I,1}}");
    check("NullSpace({{1,1,1,1,1},{1,0,0,0,0},{0,0,0,0,1},{0,1,1,1,0},{1,0,0,0,1}})", //
        "{{0,-1,1,0,0},\n" + " {0,-1,0,1,0}}");
  }

  @Test
  public void testOrthogonalize() {
    check(
        "Orthogonalize({{1. + I, 2., 3. - 2. I}, {0, 4., 5. I}, {1. + I, 6., 3. + 3. I}}) // MatrixForm", //
        "{{0.138133+I*0.359969,0.498103+I*0.221836,0.96899+I*(-0.165348)},\n" //
            + " {0.395836+I*(-0.108194),0.853462+I*(-0.151916),-0.512711+I*(-0.336411)},\n" //
            + " {0.0,0.0,0.0}}");
    check("Orthogonalize({{2.0-3.3*I,3}, {2,7}, {4.0+I,5}}) // MatrixForm", //
        "{{1.0019+I*(-0.331784),0.624317+I*0.532447},\n" //
            + " {-0.624317+I*(-0.532447),1.0019+I*(-0.331784)},\n" //
            + " {0.0,0.0}}");

    // TODO
    // check("Orthogonalize({{1, 2}, {3, 1}, {6, 9}, {7, 8}})", //
    // "{{1/Sqrt(5),2/Sqrt(5)},{2/Sqrt(5),-1/Sqrt(5)},{0,0},{0,0}}");
    check("Together((2*Sqrt(3)+3*Sqrt(5))/(5*Sqrt(7)))", //
        "(2*Sqrt(3)+3*Sqrt(5))/(5*Sqrt(7))");
    // check("Orthogonalize({{1,2,3},{5,2,7},{3,5,1}})", //
    // "{{1/Sqrt(14),Sqrt(2/7),3/Sqrt(14)},{5/Sqrt(42),-2*Sqrt(2/21),1/Sqrt(42)},{1/Sqrt(\n"
    // + "3),1/Sqrt(3),-1/Sqrt(3)}}");

    check("2/Sqrt(14)", //
        "Sqrt(2/7)");
    check("(1/4)*Sqrt(1/2)", //
        "1/(4*Sqrt(2))");
    check("4*Sqrt(2)", //
        "4*Sqrt(2)");
    check("1/Sqrt(14)", //
        "1/Sqrt(14)");
    check("2/Sqrt(14)", //
        "Sqrt(2/7)");
    check("5/Sqrt(42)", //
        "5/Sqrt(42)");
    check("-2/Sqrt(2/21)", //
        "-Sqrt(42)");
    check("1/Sqrt(3)", //
        "1/Sqrt(3)");
    check("-1/Sqrt(3)", //
        "-1/Sqrt(3)");

    check("Orthogonalize({{3,1.0},{2.0,2}})", //
        "{{0.948683,0.316228},{-0.316228,0.948683}}");
    check("Orthogonalize({{3,1},{2,2}})", //
        "{{3/Sqrt(10),1/Sqrt(10)},{-1/Sqrt(10),3/Sqrt(10)}}");
    check("Orthogonalize({{1,0,1},{1,1,1}})", //
        "{{1/Sqrt(2),0,1/Sqrt(2)},{0,1,0}}");
    check("Orthogonalize({{2,3}, {2,7}, {4.0,5}})", //
        "{{0.5547,0.83205},{-0.83205,0.5547},{0.0,0.0}}");
    check("Orthogonalize({{2,3}, {2,7}, {4,5}})", //
        "{{2/Sqrt(13),3/Sqrt(13)},{-3/Sqrt(13),2/Sqrt(13)},{0,0}}");
    check("Orthogonalize({{1,2,3},{5,2,7},{3,5,1}})", //
        "{{1/Sqrt(14),Sqrt(2/7),3/Sqrt(14)},{5/Sqrt(42),-2*Sqrt(2/21),1/Sqrt(42)},{1/Sqrt(\n"
            + "3),1/Sqrt(3),-1/Sqrt(3)}}");
    check("Orthogonalize({{1,0,0},{0,0,1}})", //
        "{{1,0,0},{0,0,1}}");
  }

  @Test
  public void testOrthogonalMatrixQ() {
    // https://en.wikipedia.org/wiki/Orthogonal_matrix
    check(
        "OrthogonalMatrixQ(SparseArray({{0, 0, 0, 1}, {0, 0, 1, 0}, {1, 0, 0, 0}, {0, 1, 0, 0}}))", //
        "True");
    check("OrthogonalMatrixQ({{0, 0, 0, 1}, {0, 0, 1, 0}, {1, 0, 0, 0}, {0, 1, 0, 0}})", //
        "True");

    // rectangular
    check("OrthogonalMatrixQ(1/2*{{1, 1, 1, -1}, {-1, 1, 1, 1}})", //
        "True");
    check("OrthogonalMatrixQ(N(1/Sqrt(5)*{{2, -1}, {1, 2}}, 25))", //
        "True");
    check("OrthogonalMatrixQ({{0.8660254037844386, -0.5}, {0.5, 0.8660254037844386}})", //
        "True");
    check("OrthogonalMatrixQ(1/Sqrt(3)*{{2, -I}, {I, 2}})", //
        "True");
    check("OrthogonalMatrixQ({{Cos[a], -Sin[a]}, {Sin[a], Cos[a]}})", //
        "True");
    check("OrthogonalMatrixQ({{1,0,0},{0,Cos[a], -Sin[a]}, {0,Sin[a], Cos[a]}})", //
        "True");
    check("OrthogonalMatrixQ({{a, b}, {c, d}}/Sqrt(a^2 + b^2))", //
        "False");
    check("Block({c = b, d = -a}, OrthogonalMatrixQ({{a, b}, {c, d}}/Sqrt(a^2 + b^2)))", //
        "True");
  }

  @Test
  public void testOuter() {
    check("Outer(g, f(a, b), f(x, y, z))", //
        "f(f(g(a,x),g(a,y),g(a,z)),f(g(b,x),g(b,y),g(b,z)))");

    check("Outer(f, {a, b}, {x, y, z})", //
        "{{f(a,x),f(a,y),f(a,z)},{f(b,x),f(b,y),f(b,z)}}");
    check("Outer(Times, {1, 2, 3, 4}, {a, b, c})", //
        "{{a,b,c},{2*a,2*b,2*c},{3*a,3*b,3*c},{4*a,4*b,4*c}}");
    check("Outer(Times, {{1, 2}, {3, 4}}, {{a, b}, {c, d}})", //
        "{{{{a,b},{c,d}},{{2*a,2*b},{2*c,2*d}}},{{{3*a,3*b},{3*c,3*d}},{{4*a,4*b},{4*c,4*d}}}}");
    check("Outer(f, {a, b}, {x, y, z}, {u, v})", //
        "{{{f(a,x,u),f(a,x,v)},{f(a,y,u),f(a,y,v)},{f(a,z,u),f(a,z,v)}},{{f(b,x,u),f(b,x,v)},{f(b,y,u),f(b,y,v)},{f(b,z,u),f(b,z,v)}}}");
    check("Outer(Times, {{1, 2}, {3, 4}}, {{a, b, c}, {d, e}})", //
        "{{{{a,b,c},{d,e}},{{2*a,2*b,2*c},{2*d,2*e}}},{{{3*a,3*b,3*c},{3*d,3*e}},{{4*a,4*b,\n"
            + "4*c},{4*d,4*e}}}}");
    check("Outer(g, f(a, b), f(x, y, z))", //
        "f(f(g(a,x),g(a,y),g(a,z)),f(g(b,x),g(b,y),g(b,z)))");
    check("Dimensions(Outer(f, {x, x, x}, {x, x, x, x}, {x, x}, {x, x, x, x, x}))", //
        "{3,4,2,5}");
    check("Dimensions(Outer(f, {{x, x}, {x, x}}, {x, x, x}, {{x}}))", //
        "{2,2,3,1,1}");
    check("Outer(f, {a, b}, {1,2,3})", //
        "{{f(a,1),f(a,2),f(a,3)},{f(b,1),f(b,2),f(b,3)}}");
    check("Outer(Times, {{1, 2}}, {{a, b}, {x, y, z}})", //
        "{{{{a,b},{x,y,z}},{{2*a,2*b},{2*x,2*y,2*z}}}}");

    check("trigs = Outer(Composition, {Sin, Cos, Tan}, {ArcSin, ArcCos, ArcTan})", //
        "{{Sin@*ArcSin,Sin@*ArcCos,Sin@*ArcTan},{Cos@*ArcSin,Cos@*ArcCos,Cos@*ArcTan},{Tan@*ArcSin,Tan@*ArcCos,Tan@*ArcTan}}");
    check("Map(#(0) &, trigs, {2})", //
        "{{0,1,0},{1,0,1},{0,ComplexInfinity,0}}");
    check(
        "Outer(StringJoin, {\"\", \"re\", \"un\"}, {\"cover\", \"draw\", \"wind\"}, {\"\", \"ing\", \"s\"})", //
        "{{{cover,covering,covers},{draw,drawing,draws},{wind,winding,winds}},{{recover,recovering,recovers},{redraw,redrawing,redraws},{rewind,rewinding,rewinds}},{{uncover,uncovering,uncovers},{undraw,undrawing,undraws},{unwind,unwinding,unwinds}}}");
  }


  @Test
  public void testOuterSparseArray() {
    check("(s1=SparseArray(Table(2^i -> i, {i, 3}))) // MatrixForm", //
        "{0,1,0,2,0,0,0,3}");
    check("(s2=SparseArray({1} -> 1, {4})) // MatrixForm", //
        "{1,0,0,0}");
    // check("Outer(Times,s1,s2) // MatrixForm", //
    // "");
  }

  @Test
  public void testPauliMatrix() {
    check("PauliMatrix({0,1,2,3,4})", //
        "{{{1,0},{0,1}},{{0,1},{1,0}},{{0,-I},{I,0}},{{1,0},{0,-1}},{{1,0},{0,1}}}");
  }

  // @Test
  // public void testPositiveDefiniteMatrixQ() {
  // // TODO https://github.com/Hipparchus-Math/hipparchus/issues/442
  // check(
  // "PositiveDefiniteMatrixQ({{5.42,3.26 +
  // 0.643*I,-0.467-0.193*I},{3.26-0.643*I,3.82,1.04-2.35*I},{-0.467+0.193*I,1.04+2.35*I,4.88}})",
  // "True");
  // check("PositiveDefiniteMatrixQ({{-5,1},{1,-4}})", //
  // "False");
  // check("PositiveDefiniteMatrixQ({{2,0},{0,0}})", //
  // "False");
  // check("PositiveDefiniteMatrixQ({{5,-1},{-1, 4} })", //
  // "True");
  // }

  @Test
  public void testPositiveSemidefiniteMatrixQ() {
    check("PositiveSemidefiniteMatrixQ({{-5,1},{1,-4}})", //
        "False");
    check("PositiveSemidefiniteMatrixQ({{2,0},{0,0}})", //
        "True");
    check("PositiveSemidefiniteMatrixQ({{5,-1},{-1, 4} })", //
        "True");

  }

  @Test
  public void testPseudoInverse() {
    check("PseudoInverse(-2)", //
        "PseudoInverse(-2)");
    check("PseudoInverse({{}})", //
        "PseudoInverse({{}})");
    check("PseudoInverse({{1,2}, {1,2}})", //
        "{{0.1,0.1},\n" + " {0.2,0.2}}");
    check("PseudoInverse({1, {2}})", //
        "PseudoInverse({1,{2}})");
    check("PseudoInverse(PseudoInverse({{1, 2}, {2, 3}, {3, 4}}))", //
        "{{1.0,2.0},\n" + //
            " {2.0,3.0},\n" + //
            " {3.0,4.0}}");
    check("PseudoInverse({{1, 2, 0}, {2, 3, 0}, {3, 4, 1}})", //
        "{{-3.0,2.0,4.44089*10^-16},\n" + " {2.0,-1.0,-2.77556*10^-16},\n" + " {1.0,-2.0,1.0}}");
    check("PseudoInverse({{1.0, 2.5}, {2.5, 1.0}})", //
        "{{-0.190476,0.47619},\n" + //
            " {0.47619,-0.190476}}");
    check("PseudoInverse({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}})", //
        "{{-0.483333,-0.244444,-0.00555556,0.233333},\n" + //
            " {-0.0333333,-0.0111111,0.0111111,0.0333333},\n" + //
            " {0.416667,0.222222,0.0277778,-0.166667}}");
    check("PseudoInverse(N({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}}))", //
        "{{-0.483333,-0.244444,-0.00555556,0.233333},\n" + //
            " {-0.0333333,-0.0111111,0.0111111,0.0333333},\n" + //
            " {0.416667,0.222222,0.0277778,-0.166667}}");
    check("PseudoInverse(N({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}))", //
        "{{-0.638889,-0.166667,0.305556},\n"//
            + " {-0.0555556,-3.50414*10^-16,0.0555556},\n"//
            + " {0.527778,0.166667,-0.194444}}"); //
  }

  @Test
  public void testQRDecomposition() {
    // check(
    // "N(QRDecomposition({{1, 2}, {3, 4}, {5, 6}}), 50)", //
    // "{\n" //
    // +
    // "{{-0.16903085094570331550192366547318905852615717802269,0.89708522714506048313496492401527158286105584462791,0.40824829046386301636621401245098189866099124677609},\n"
    // + "
    // {-0.50709255283710994650577099641956717557847153406808,0.27602622373694168711845074585085279472647872142397,-0.81649658092772603273242802490196379732198249355222},\n"
    // + "
    // {-0.84515425472851657750961832736594529263078589011347,-0.34503277967117710889806343231356599340809840177998,0.4082482904638630163662140124509818986609912467761}},\n"
    // +
    // "{{-5.9160797830996160425673282915616170484155012307943,-7.4373574416109458820846412808203185751509158329985},\n"
    // + " {0,0.82807867121082506135535223755255838417943616427194},\n"
    // + " {0,0}}}");

    // TODO https: // github.com/Hipparchus-Math/hipparchus/issues/137
    // check(
    // "qrd=QRDecomposition({{0.20196127709244793 + 0.925718764963565*I, 0.05384699229616263
    // + 0.37879682375770196*I, 0.675740491722238 + 0.7581876553357298*I}, \n"
    // + " {0.15034789645557778 + 0.523962371716961*I, 0.12845115500176374 +
    // 0.8281433754893717*I, 0.8470430157403761 + 0.06803052493562567*I}, \n"
    // + " {0.9842768557114026 + 0.29845479189900503*I, 0.3148161888755976 +
    // 0.07972894452542922*I, 0.47007587051022437 + 0.3777710615375067*I}})", //
    // "{\n" //
    // +
    // "{{-0.78596788157487251+I*(-0.41641150790401054),0.10092416871068+I*0.4748177204221769,0.96092701912128708+I*(-0.39046321623522134)},\n"
    // + "
    // {-0.46670803707619551+I*(-0.20986103141169112),-0.90398884023878067+I*0.10786367156055666,-0.14378183427974028+I*0.0030342822735377611},\n"
    // + "
    // {-0.81059871236446845+I*0.52458743675928043,0.64468678990975258+I*0.07691637615555559,-0.702878251984557+I*(-0.53443527106410859)}},\n"
    // +
    // "{{-0.68788360430750516+I*(-0.81336162840457315),-0.067755213346249981+I*(-0.63308226763918029),-1.1756539332085431+I*(-1.1464338161551266)},\n"
    // + "
    // {0.0,-0.18304460039071709+I*(-0.5953651779713297),-0.79086291949341713+I*0.70693989848929339},\n"
    // + " {0.0,0.0,0.69487393799819596+I*(-0.05925237687724915)}}}");
    // check(
    // "ConjugateTranspose(qrd[[1]]).qrd[[2]]", //
    // "");
    check("Together(1-(1+Sqrt(35))/Sqrt(35))", //
        "-1/Sqrt(35)");

    check("QRDecomposition(Indeterminate)", //
        "QRDecomposition(Indeterminate)");
    check("-3/35*Sqrt(35)", //
        "-3/Sqrt(35)");
    // TODO MMA gives 2x3 and 2x2 matrices
    check("QRDecomposition({{1, 2}, {3, 4}, {5, 6}}) // N", //
        "{{{-0.169031,-0.507093,-0.845154},{0.897085,0.276026,-0.345033},{0.408248,-0.816497,0.408248}},{{-5.91608,-7.43736},{0.0,0.828079},{0.0,0.0}}}");//

    check("QRDecomposition({{12, -51, 4}, {6, 167, -68}, {-4, 24, -41}})", //
        "{\n" + "{{-6/7,-3/7,2/7},\n" //
            + " {69/175,-158/175,-6/35},\n" //
            + " {-58/175,6/175,-33/35}},\n" //
            + "{{-14,-21,14},\n" //
            + " {0,-175,70},\n" //
            + " {0,0,35}}}");

    check("QRDecomposition({{1, 2, 3}, {4, 5, 6}})", //
        "{\n" //
            + "{{-1/Sqrt(17),-4/Sqrt(17)},\n" //
            + " {4/Sqrt(17),-1/Sqrt(17)}},\n" //
            + "{{-Sqrt(17),-22/Sqrt(17),-27/Sqrt(17)},\n" //
            + " {0,3/Sqrt(17),6/Sqrt(17)}}}");
  }

  @Test
  public void testRescale() {
    check("Rescale({-.7, .5, 1.2, 5.6, 1.8})", //
        "{0.0,0.190476,0.301587,1.0,0.396825}");
    check("Rescale({2.5, 3.5, 4.5, 6.5}, {0, 10})", //
        "{0.25,0.35,0.45,0.65}");
    check("Rescale({1, 2, 3, 4, 5, 6}, {0, a})", //
        "{1/a,2/a,3/a,4/a,5/a,6/a}");
    check("Rescale(1 + 0.5 I, {0, 1 + I})", //
        "0.75+I*(-0.25)");
    check("Rescale({a,b})", //
        "{a/(Max(a,b)-Min(a,b))-Min(a,b)/(Max(a,b)-Min(a,b)),b/(Max(a,b)-Min(a,b))-Min(a,b)/(Max(a,b)-Min(a,b))}");
    check("Rescale(x,{xmin, xmax})", //
        "x/(xmax-xmin)-xmin/(xmax-xmin)");
    check("Rescale(x,{xmin, xmax},{ymin, ymax})", //
        "(x*(ymax-ymin))/(xmax-xmin)+(-xmin*ymax+xmax*ymin)/(xmax-xmin)");
    check("Rescale(2.5,{-10,10})", //
        "0.625");
    check("Rescale(2.5,{10,10})", //
        "Indeterminate");
    // celsius to fahrenheit in steps of 10 degrees from -40 to 100 degree
    check("Table({x, Rescale(x, {-40, 100}, {-40, 212})}, " //
        + "{x, -40, 100, 10})", //
        "{{-40,-40},{-30,-22},{-20,-4},{-10,14},{0,32},{10,50},{20,68},{30,86},{40,104},{\n"
            + "50,122},{60,140},{70,158},{80,176},{90,194},{100,212}}");
    check("Rescale({1, 2, 3, 4, 5}, {-100, 100})", //
        "{101/200,51/100,103/200,13/25,21/40}");
  }

  @Test
  public void testRiccatiSolve() {
    check("RiccatiSolve({ {{-3, 2}, {1, 1}}, " //
        + "{{0}, {1}} }, " //
        + "{ {{1.0,0.0},{0.0,1.0}}, " //
        + "{{1.0}} })", //
        "{{0.322124,0.74066},\n" + //
            " {0.74066,3.2277}}");

    check("RiccatiSolve({ {{3, -2}, {4, -1}}, " //
        + "{{0}, {1}} }, " //
        + "{ {{1.0,0.0},{0.0,1.0}}, " //
        + "{{1.0}} })", //
        "{{19.75982,-7.64298},\n" + //
            " {-7.64298,4.70718}}");

    check("RiccatiSolve({ {{-3, 2}, {1, 1}}, " //
        + "{{0}, {1}} }, " //
        + "{ {{1., -1.}, {-1., 1.}}, " //
        + "{{3}}})", //
        "{{0.589517,1.82157},\n" + //
            " {1.82157,8.81884}}");
  }

  @Test
  public void testRotationMatrix() {
    check("RotationMatrix(90*Degree)", //
        "{{0,-1},{1,0}}");
    check("RotationMatrix(t,{0,0,1})", //
        "{{Cos(t),-Sin(t),0},{Sin(t),Cos(t),0},{0,0,1}}");
    check("RotationMatrix(t,{0,1,0})", //
        "{{Cos(t),0,Sin(t)},{0,1,0},{-Sin(t),0,Cos(t)}}");
    check("RotationMatrix(t,{1,0,0})", //
        "{{1,0,0},{0,Cos(t),-Sin(t)},{0,Sin(t),Cos(t)}}");
  }

  @Test
  public void testRowReduce001() {
    check("RowReduce({{10, 4, -6, -4}, {4, 10, -15, -4}, {0, 0, 0, 0}, {4, 4, -6, 2}} )", //
        "{{1,0,0,0},\n" //
            + " {0,1,-3/2,0},\n" //
            + " {0,0,0,1},\n" //
            + " {0,0,0,0}}");
    check("RowReduce(\n" //
        + "{{1,-1,1}, {1,5,7}, {1,-7,-5}})", //
        "{{1,0,2},\n"//
            + " {0,1,1},\n"//
            + " {0,0,0}}");//
    check("RowReduce(\n" //
        + "{{1,2,3,4},{4,5,6,7},{6,7,8,9}})", //
        "{{1,0,-1,-2},\n" //
            + " {0,1,2,3},\n" //
            + " {0,0,0,0}}");

    check("RowReduce(\n" //
        + "{{1, a, 2}, {0, 1, 1}, {-1, 1, 1}},\n"
        + "ZeroTest -> (Quiet(Length@Solve(# == 0, Reals) > 0) &))",
        "{{1,0,2-a},\n" //
            + " {0,1,1},\n" + " {0,0,2-a}}");
    // without ZeroTest option
    check("RowReduce(\n" //
        + "{{1, a, 2}, {0, 1, 1}, {-1, 1, 1}})",
        "{{1,0,0},\n" //
            + " {0,1,0},\n" + " {0,0,1}}");
  }

  @Test
  public void testRowReduce002() {
    check("RowReduce({\n" //
        + "    {a,b,0,0,0,0,0,0,1},\n" + "    {0,0,0,0,a1,b1,0,0,1},\n"
        + "    {0,0,c,d,0,0,0,0,1},\n" + "    {0,0,0,0,0,0,c1,d1,1},\n"
        + "    {0,b1,0,-d1,a,0,-c,0,0},\n" + "    {-b1,0,d1,0,b,0,-d,0,0},\n"
        + "    {0,-a1,0,c1,0,a,0,-c,0},\n" + "    {a1,0,-c1,0,0,b,0,-d,0}\n" + "    },\n"
        + "    ZeroTest->PossibleZeroQ)",
        "{{1,0,0,0,-b/b1,0,0,0,0},\n" //
            + " {0,1,0,0,a/b1,0,0,0,0},\n" + " {0,0,1,d/c,0,0,0,0,0},\n"
            + " {0,0,0,0,a1/b1,1,0,0,0},\n" + " {0,0,0,d1/c,0,0,1,0,0},\n"
            + " {0,0,0,-c1/c,0,0,0,1,0},\n" + " {0,0,0,0,0,0,0,0,1},\n" + " {0,0,0,0,0,0,0,0,0}}");
  }

  @Test
  public void testRowReduce003() {
    check("RowReduce({{1, 2, 3, a}, {4, 5, 6, a^2}, {7, 8, 9, a^3}} )", //
        "{{1,0,-1,0},\n" //
            + " {0,1,2,0},\n" + " {0,0,0,1}}");

    check("RowReduce(SparseArray({{1,2,2,8},{-1,1,4,4},{-1,-1,1,9}}))", //
        "{{1,0,0,26},\n" //
            + " {0,1,0,-22},\n" + " {0,0,1,13}}");

    //
    check("RowReduce({{1, 2, 3, 1}, {5, 6, 7, 1}, {7, 8, 9, 1}})", //
        "{{1,0,-1,-1},\n" //
            + " {0,1,2,1},\n" + " {0,0,0,0}}");
    check("RowReduce({{1, 2, 3, 1}, {5, 6, 7, -2}, {7, 8, 9, 1}})", //
        "{{1,0,-1,0},\n" //
            + " {0,1,2,0},\n" + " {0,0,0,1}}");
    check(
        "RowReduce({{1, 2, 3, 4, 1, 0, 0, 0}, {5, 6, 7, 8, 0, 1, 0, 0}, {9, 10, 11, 12, 0, 0, 1, 0}, {13, 14, 15, 16, 0, 0, 0, 1}})", //
        "{{1,0,-1,-2,0,0,-7/2,5/2},\n" //
            + " {0,1,2,3,0,0,13/4,-9/4},\n" + " {0,0,0,0,1,0,-3,2},\n" + " {0,0,0,0,0,1,-2,1}}");
    check("RowReduce(N({{1, 2, 3, 1}, {5, 6, 7, -2}, {7, 8, 9, 1}}))", //
        "{{1.0,0.0,-1.0,0.0},\n" //
            + " {0.0,1.0,2.0,0.0},\n" + " {0.0,0.0,0.0,1.0}}");
    check("RowReduce({{1,5,7},{-2,-7,-5}})", //
        "{{1,0,-8},\n" //
            + " {0,1,3}}");
    check("RowReduce({{1,2,2,8},{-1,1,4,4},{-1,-1,1,9}})", //
        "{{1,0,0,26},\n" //
            + " {0,1,0,-22},\n" + " {0,0,1,13}}");
    check("RowReduce({{1,2,3,4},{4,3,2,1}})", //
        "{{1,0,-1,-2},\n" //
            + " {0,1,2,3}}");
    check("RowReduce({{1,2,2,4,1},{0,1,1,2,1},{0,0,0,2,1},{0,0,0,1,1}})", //
        "{{1,0,0,0,0},\n" //
            + " {0,1,1,0,0},\n" + " {0,0,0,1,0},\n" + " {0,0,0,0,1}}");

    check("RowReduce({{2, 4, 8}, {3, 6, 9}})", //
        "{{1,2,0},\n" //
            + " {0,0,1}}");
    check("RowReduce({{1, -2, 1, 0}, {0, 2, -8, 8}, {5, 0, -5, 10}})", //
        "{{1,0,0,1},\n" //
            + " {0,1,0,0},\n" + " {0,0,1,-1}}");
    check("RowReduce({{1, 2, 7}, {-2, 5, 4}, {-5, 6, 3}})", //
        "{{1,0,0},\n" //
            + " {0,1,0},\n" + " {0,0,1}}");

    check("RowReduce({{1, 0, a}, {1, 1, b}})", //
        "{{1,0,a},\n" //
            + " {0,1,-a+b}}");

    check(
        "RowReduce({{1, 2, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 2, 1}})", //
        "{{1,2,0},\n" //
            + " {0,0,1},\n" + " {0,0,0},\n" + " {0,0,0},\n" + " {0,0,0},\n" + " {0,0,0},\n"
            + " {0,0,0}}");

    check("RowReduce({{1, 2, 3, 1}, {4, 5, 6, -1}, {7, 8, 9, 2}})", //
        "{{1,0,-1,0},\n" //
            + " {0,1,2,0},\n" + " {0,0,0,1}}");

    check("RowReduce({{1,0,-1,0},{0,1,0,-1},{1,-2,-1,0},{-1,0,3,1}})", //
        "{{1,0,0,0},\n" //
            + " {0,1,0,0},\n" + " {0,0,1,0},\n" + " {0,0,0,1}}");

    check("RowReduce({{1, 0, a}, {1, 1, b}})", //
        "{{1,0,a},\n" //
            + " {0,1,-a+b}}");

    check("RowReduce({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "{{1,0,-1},\n" //
            + " {0,1,2},\n" + " {0,0,0}}");

    check("RowReduce({{1, 0}, {0}})", //
        "RowReduce({{1,0},{0}})");

    check("RowReduce({{1, 2, 3, 1}, {4, 5, 6, 1}, {7, 8, 9, 1}})", //
        "{{1,0,-1,-1},\n" //
            + " {0,1,2,1},\n" + " {0,0,0,0}}");

    check("RowReduce({{1, 2, 3, 1}, {4, 5, 6, -1}, {7, 8, 9, 2}})", //
        "{{1,0,-1,0},\n" //
            + " {0,1,2,0},\n" + " {0,0,0,1}}");

    check("RowReduce({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "{{1,0,-1},\n" //
            + " {0,1,2},\n" + " {0,0,0}}");

    check("RowReduce({{3, 1, a}, {2, 1, b}})", //
        "{{1,0,a-b},\n" //
            + " {0,1,-2*a+3*b}}");

    check("RowReduce({{1., 2., 3.}, {4., 5., 6.}, {7., 8., 9.}})", //
        "{{1.0,0.0,-1.0},\n" //
            + " {0.0,1.0,2.0},\n" + " {0.0,0.0,0.0}}");

    check("RowReduce({{1, I}, {I, -1}})", //
        "{{1,I},\n" //
            + " {0,0}}");

    check("RowReduce({{1,2,3,1,0,0}, {4,5,6,0,1,0}, {7,8,9,0,0,1}})", //
        "{{1,0,-1,0,-8/3,5/3},\n" //
            + " {0,1,2,0,7/3,-4/3},\n" + " {0,0,0,1,-2,1}}");
  }

  @Test
  public void testSchurDecomposition() {
    check("m={{2.7, 4.8, 8.1}, {-.6, 0, 0}, {.1, 0, .3}};", //
        "");
    check("{q,t}=SchurDecomposition(m)", //
        "{\n" //
            + "{{-0.892714,0.449224,0.0354887},\n" //
            + " {0.439993,0.85194,0.283909},\n" //
            + " {-0.0973047,-0.269065,0.958194}},\n" //
            + "{{1.21716,-2.90287,-8.26847},\n" //
            + " {0.00010145,1.18284,4.04539},\n" //
            + " {0.0,-8.89583*10^-24,0.6}}}");
    check("m - q.t.ConjugateTranspose(q) // Chop", //
        "{{0,0,0},{0,0,0},{0,0,0}}");

    check(
        "m = {{1.81066, 0.31066, 1.5}, {-0.53033, 2.03033, 0.43934}, {-0.96967, -0.53033, 2.56066}};", //
        "");
    check("{q,t}=SchurDecomposition(m)", //
        "{\n" //
            + "{{0.408028,-0.897605,0.166789},\n" //
            + " {-0.323352,-0.312928,-0.89304},\n" //
            + " {-0.85379,-0.310453,0.417926}},\n" //
            + "{{2.19946,-1.00329,-0.35864},\n" //
            + " {1.71543,1.9817,-0.476196},\n" //
            + " {0.0,-3.59747*10^-17,2.22049}}}");
    check("m - q.t.ConjugateTranspose(q) // Chop", //
        "{{0,0,0},{0,0,0},{0,0,0}}");
  }

  @Test
  public void testSingularValueDecomposition() {
    // check("Transpose(Orthogonalize({{1/13*(-6+Sqrt(205)),1},{1/13*(-6-Sqrt(205)),1}}))//N", //
    // "{{0.538954,-0.842335},{0.842335,0.538954}}");

    check("{u,s,v}=SingularValueDecomposition({{1.5, 2.0}, {2.5, 3.0}})", //
        "{{{0.538954,0.842335},\n" //
            + " {0.842335,-0.538954}},{{4.63555,0.0},\n" //
            + " {0.0,0.107862}},{{0.628678,-0.777666},\n" //
            + " {0.777666,0.628678}}}");
    check("u.s.ConjugateTranspose(v)", //
        "{{1.5,2.0},\n" //
            + " {2.5,3.0}}");
    check("{u,s,v}=SingularValueDecomposition({{3/2, 2}, {5/2, 3}})", //
        "{{{0.538954,0.842335},\n" //
            + " {0.842335,-0.538954}},{{4.63555,0.0},\n"//
            + " {0.0,0.107862}},{{0.628678,-0.777666},\n" //
            + " {0.777666,0.628678}}}");
    check("u.s.ConjugateTranspose(v)//N", //
        "{{1.5,2.0},{2.5,3.0}}");

    // See http://issues.apache.org/jira/browse/MATH-320:
    check("{u,s,v}=SingularValueDecomposition({{1,2},{1,2}})", //
        "{{{-0.707107,-0.707107},\n" //
            + " {-0.707107,0.707107}},{{3.16228,0.0},\n" //
            + " {0.0,0.0}},{{-0.447214,-0.894427},\n" //
            + " {-0.894427,0.447214}}}");
    check("u.s.ConjugateTranspose(v)", //
        "{{1.0,2.0},\n"//
            + " {1.0,2.0}}");

    check("SingularValueDecomposition({{ 24.0/25.0, 43.0/25.0 },{57.0/25.0, 24.0/25.0 }})", //
        "{{{0.6,0.8},\n"//
            + " {0.8,-0.6}},{{3.0,0.0},\n" //
            + " {0.0,1.0}},{{0.8,-0.6},\n" //
            + " {0.6,0.8}}}");


    check("SingularValueDecomposition({1, {2}})", //
        "SingularValueDecomposition({1,{2}})");
  }

  @Test
  public void testSingularValueList() {
    // check("m1 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}};", //
    // "");
    // check("Transpose(m1)", //
    // "{{1.0,4.0,7.0,10.0},\n" //
    // + " {2.0,5.0,8.0,11.0},\n" + " {3.0,6.0,9.0,12.0}}");
    // check("ConjugateTranspose(m1)", //
    // "{{1.0,4.0,7.0,10.0},\n" //
    // + " {2.0,5.0,8.0,11.0},\n" + " {3.0,6.0,9.0,12.0}}");
    // check("SingularValueList(m1)", //
    // "{25.46241,1.29066}");
    // check("Norm(m1)", //
    // "25.46241");
    check("m2 = {{0.490394,0.0888901,0.536156,0.955578,0.695596},\n" //
        + " {0.0738935,0.098087,0.870086,0.71151,0.415052},\n"
        + " {0.205551,0.671352,0.4668,0.27744,0.369186},\n"
        + " {0.119547,0.575965,0.301828,0.320825,0.914003},\n"
        + " {0.949468,0.349907,0.666519,0.907584,0.273987}};", //
        "");
    check("SingularValueList(m2)", //
        "{2.56924,0.898176,0.629938,0.491696,0.0610678}");
    // check("SingularValueList(m2,2)", //
    // "{2.56924,0.898176}");
  }

  @Test
  public void testSquareMatrixQ() {
    check("SquareMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})", //
        "True");
    check("SquareMatrixQ({{}})", //
        "False");
    check("SquareMatrixQ({{a,b,c}, {d,e,f}})", //
        "False");
  }

  @Test
  public void testSymmetricMatrixQ() {
    // example from https://en.wikipedia.org/wiki/Symmetric_matrix
    check("SymmetricMatrixQ(SparseArray({{1,7,3}, {7,4,-5}, {3,-5,6}}))", //
        "True");
    check("SymmetricMatrixQ({{1,7,3}, {7,4,-5}, {3,-5,6}})", //
        "True");

    check("SymmetricMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})", //
        "False");
    check("SymmetricMatrixQ({{1, 3 + 3*I}, {3 + 3*I, 2}})", //
        "True");
    check("SymmetricMatrixQ(Table(Re(i)*Re(j), {i, 10}, {j, 10}))", //
        "True");
    check("Block({b = c}, SymmetricMatrixQ({{a, b}, {c, d}}))", //
        "True");
  }

  @Test
  public void testTensorDimensions() {
    check(
        "$Assumptions = { Element(A, Matrices({l,m})), Element(B, Matrices({m,n})), Element(C, Matrices({h,h}))};", //
        "");

    // TensorDimensions: Dot contraction of B and C is invalid because dimensions n and h are
    // incompatibe.
    check("TensorDimensions(A.B.C)", //
        "TensorDimensions(A.B.C)");
    check("TensorDimensions(A.B)", //
        "{l,n}");
  }

  @Test
  public void testTensorSymmetry() {
    check("m = {{1, Log(x^2)}, {2*Log(x), 2}};", //
        "");
    check("TensorSymmetry(m)", //
        "{}");
    check("TensorSymmetry(m, SameTest->(Simplify(#1-#2, x>0)==0 &))", //
        "Symmetric({1,2})");
  }

  @Test
  public void testToeplitzMatrix() {
    check("ToeplitzMatrix({1, 2, 3, 4, 5, 6}, {1, a, b, c})", //
        "{{1,a,b,c},\n" + //
            " {2,1,a,b},\n" + //
            " {3,2,1,a},\n" + //
            " {4,3,2,1},\n" + //
            " {5,4,3,2},\n" + //
            " {6,5,4,3}}");

    check("ToeplitzMatrix({1, a, b, c}, {1, 2, 3, 4, 5, 6})", //
        "{{1,2,3,4,5,6},\n" + //
            " {a,1,2,3,4,5},\n" + //
            " {b,a,1,2,3,4},\n" + //
            " {c,b,a,1,2,3}}");

    check("ToeplitzMatrix(-3)", //
        "ToeplitzMatrix(-3)");
    check("ToeplitzMatrix(3)", //
        "{{1,2,3},\n" //
            + " {2,1,2},\n" + " {3,2,1}}");
    check("ToeplitzMatrix({a,b,c,d})", //
        "{{a,b,c,d},\n" //
            + " {b,a,b,c},\n" + " {c,b,a,b},\n" + " {d,c,b,a}}");
    check("ToeplitzMatrix(4)", //
        "{{1,2,3,4},\n" //
            + " {2,1,2,3},\n" + " {3,2,1,2},\n" + " {4,3,2,1}}");
  }

  @Test
  public void testToPolarCoordinates() {
    check("-Pi/2 < 0", //
        "True");
    check("Arg(1)", //
        "0");
    check("-Pi/2 < Arg(1) ", //
        "True");
    check(" Arg(1) <= Pi/2", //
        "True");

    check("ToPolarCoordinates(SparseArray({1, 1}))", //
        "{Sqrt(2),Pi/4}");
    check("ToPolarCoordinates(SparseArray({x, y, z}))", //
        "{Sqrt(x^2+y^2+z^2),ArcCos(x/Sqrt(x^2+y^2+z^2)),ArcTan(y,z)}");

    check("ToPolarCoordinates({x, y})", //
        "{Sqrt(x^2+y^2),ArcTan(x,y)}");
    check("ToPolarCoordinates({1, 1})", //
        "{Sqrt(2),Pi/4}");
    check("ToPolarCoordinates({x, y, z})", //
        "{Sqrt(x^2+y^2+z^2),ArcCos(x/Sqrt(x^2+y^2+z^2)),ArcTan(y,z)}");
    check("ToPolarCoordinates({{{x, y}, {1, 0}}, {{-2, 0}, {0, 1}}})", //
        "{{{Sqrt(x^2+y^2),ArcTan(x,y)},{1,0}},{{2,Pi},{1,Pi/2}}}");
    check("ToPolarCoordinates({{{1, -1}}})", //
        "{{{Sqrt(2),-Pi/4}}}");
    check("ToPolarCoordinates({{} , {}})", //
        "{{},{}}");
  }

  @Test
  public void testToSphericalCoordinates() {
    check("ToSphericalCoordinates({x, y, z})", //
        "{Sqrt(x^2+y^2+z^2),ArcTan(z,Sqrt(x^2+y^2)),ArcTan(x,y)}");
    check("ToSphericalCoordinates({{{1, 1, 1}}, {{-2, 0, 0}, {0, 1, -1}}})", //
        "{{{Sqrt(3),ArcTan(Sqrt(2)),Pi/4}},{{2,Pi/2,Pi},{Sqrt(2),3/4*Pi,Pi/2}}}");
  }

  @Test
  public void testTr() {
    // TODO calculate for levels
    // check(
    // "Tr(Array(a, {4, 3, 2}), f, 2)", //
    // "");

    check("Tr({{1,2},{3,4}})", //
        "5");
    check("Tr({{1,2},{3,4},{5,6}})", //
        "5");
    check("Tr({{1,2,5},{3,4,6}})", //
        "5");
    check("Tr({{a,b,c},{d,e,f}})", //
        "a+e");

    check("Tr({{{1,0},{0,1}},{0,0}})", //
        "{1,0}");
    check("Tr(Array(a,{4,3,2}))", //
        "a(1,1,1)+a(2,2,2)");
    check("Tr(Array(a,{4,7,3}),f)", //
        "f(a(1,1,1),a(2,2,2),a(3,3,3))");
    check("Tr(Array(a,{4,7,0}),f)", //
        "f()");
    check("Tr(SparseArray({1,2,3}))", //
        "6");
    check("Tr({1,2,3})", //
        "6");
    check("Tr({{}})", //
        "0");
    check("Tr({{1, 2}, {4, 5}, {7, 8}})", //
        "6");
    check("Tr({{1, 2, 3}, {4, 5, 6} })", //
        "6");
    check("Tr({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "15");
    check("Tr({{a, b, c}, {d, e, f}, {g, h, i}})", //
        "a+e+i");
    check("Tr({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, f)", //
        "f(1,5,9)");
    check("Tr(SparseArray({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}), f)", //
        "f(1,5,9)");
  }

  @Test
  public void testTranspose() {
    check("Transpose({0,1,2,3})", //
        "{0,1,2,3}");
    check("Transpose({{0,0}, {0,0}},-0.8+I*1.2)", //
        "Transpose({{0,0},{0,0}},-0.8+I*1.2)");

    check(
        "tens={{{4,5},{1,2},{1,7},{9,6},{1,9}},{{1,0},{5,6},{2,0},{8,5},{0,8}},{{0,6},{9,2},{1,7},{8,5},{9,6}}}", //
        "{{{4,5},{1,2},{1,7},{9,6},{1,9}},{{1,0},{5,6},{2,0},{8,5},{0,8}},{{0,6},{9,2},{1,\n" //
            + "7},{8,5},{9,6}}}");
    // TODO
    check("Transpose(SparseArray(tens), {3,1,2}) // Normal", //
        "{{{4,1,0},{5,0,6}},{{1,5,9},{2,6,2}},{{1,2,1},{7,0,7}},{{9,8,8},{6,5,5}},{{1,0,9},{\n" //
            + "9,8,6}}}");

    check("Transpose(tens, {3,1,2})", //
        "{{{4,1,0},{5,0,6}},{{1,5,9},{2,6,2}},{{1,2,1},{7,0,7}},{{9,8,8},{6,5,5}},{{1,0,9},{\n" //
            + "9,8,6}}}");

    check("Transpose({{1,2},{3,4}})", //
        "{{1,3},{2,4}}");
    check("Transpose({{1,2},{3,4},{5,6}})", //
        "{{1,3,5},{2,4,6}}");

    check("Transpose({{-1}},{2})", //
        "Transpose({{-1}},{2})");
    check("Transpose({{0,1,2},{0.7121489163040792,-0.4571283540266432 }})", //
        "{{0,1,2},{0.712149,-0.457128}}ᵀ");


    check("Transpose({{},{},{}})", //
        "{}");
    check("Transpose(SparseArray({{1, 2, 3}, {4, 5, 6}})) // Normal", //
        "{{1,4},\n" //
            + " {2,5},\n" + " {3,6}}");
    check("Transpose(SparseArray({{1, 2, 3}, {4, 5, 6}}))", //
        "SparseArray(Number of elements: 6 Dimensions: {3,2} Default value: 0)");
    check("Transpose({{1, 2, 3}, {4, 5, 6}}, {2,1})", //
        "{{1,4},{2,5},{3,6}}");
    check("Transpose({{1, 2, 3}, {4, 5, 6}}, {1,2})", //
        "{{1,2,3},{4,5,6}}");

    check("m = Array(a, {2, 3, 2})", //
        "{{{a(1,1,1),a(1,1,2)},{a(1,2,1),a(1,2,2)},{a(1,3,1),a(1,3,2)}},{{a(2,1,1),a(2,1,\n"
            + "2)},{a(2,2,1),a(2,2,2)},{a(2,3,1),a(2,3,2)}}}");
    check("Transpose(m, {1,3,2})",
        "{{{a(1,1,1),a(1,2,1),a(1,3,1)},{a(1,1,2),a(1,2,2),a(1,3,2)}},{{a(2,1,1),a(2,2,1),a(\n"
            + "2,3,1)},{a(2,1,2),a(2,2,2),a(2,3,2)}}}");
    check("Transpose(m, {3,2,1})", //
        "{{{a(1,1,1),a(2,1,1)},{a(1,2,1),a(2,2,1)},{a(1,3,1),a(2,3,1)}},{{a(1,1,2),a(2,1,\n"
            + "2)},{a(1,2,2),a(2,2,2)},{a(1,3,2),a(2,3,2)}}}");
    check("Transpose(m, {2,1,3})", //
        "{{{a(1,1,1),a(1,1,2)},{a(2,1,1),a(2,1,2)}},{{a(1,2,1),a(1,2,2)},{a(2,2,1),a(2,2,\n"
            + "2)}},{{a(1,3,1),a(1,3,2)},{a(2,3,1),a(2,3,2)}}}");

    check("s = SparseArray(m)", //
        "SparseArray(Number of elements: 12 Dimensions: {2,3,2} Default value: 0)");

    check("t=Transpose(s, {1,3,2})", //
        "SparseArray(Number of elements: 12 Dimensions: {2,2,3} Default value: 0)");
    check("t // MatrixForm", //
        "{{{a(1,1,1),a(1,2,1),a(1,3,1)},{a(1,1,2),a(1,2,2),a(1,3,2)}},\n" //
            + " {{a(2,1,1),a(2,2,1),a(2,3,1)},{a(2,1,2),a(2,2,2),a(2,3,2)}}}");

    check("t=Transpose(s, {3,2,1})", //
        "SparseArray(Number of elements: 12 Dimensions: {2,3,2} Default value: 0)");
    check("t // MatrixForm", //
        "{{{a(1,1,1),a(2,1,1)},{a(1,2,1),a(2,2,1)},{a(1,3,1),a(2,3,1)}},\n" //
            + " {{a(1,1,2),a(2,1,2)},{a(1,2,2),a(2,2,2)},{a(1,3,2),a(2,3,2)}}}");

    check("t=Transpose(s, {2,1,3})", //
        "SparseArray(Number of elements: 12 Dimensions: {3,2,2} Default value: 0)");
    check("t // MatrixForm", //
        "{{{a(1,1,1),a(1,1,2)},{a(2,1,1),a(2,1,2)}},\n" //
            + " {{a(1,2,1),a(1,2,2)},{a(2,2,1),a(2,2,2)}},\n" //
            + " {{a(1,3,1),a(1,3,2)},{a(2,3,1),a(2,3,2)}}}");



    check("Transpose({{1, 2, 3}, {4, 5, 6}})", //
        "{{1,4},{2,5},{3,6}}");
  }

  @Test
  public void testUnitaryMatrixQ() {

    check("m0 =  {{0, I}, {I, 0}};", //
        "");
    check("UnitaryMatrixQ(m0)", //
        "True");

    check("m1 = 1/Sqrt(5)*{{1, 2}, {-2, 1}};", //
        "");
    check("UnitaryMatrixQ(m1)", //
        "True");

    check("m2 = ({\n" //
        + " {1, 0, 0},\n" //
        + " {0, Cosh(Im(a))/Sqrt(Cosh(2*Im(a))), \n" //
        + " I*Sinh(Im(a))/Sqrt(Cosh(2*Im(a)))},\n" //
        + " {0 , I*Sinh(Im(a))/Sqrt(Cosh(2*Im(a))), \n" //
        + " Cosh(Im(a))/Sqrt(Cosh(2*Im(a)))}\n" //
        + " });", //
        "");

    check("UnitaryMatrixQ(m2)", //
        "True");
  }

  @Test
  public void testUnitVector() {
    // message: UnitVector: Positive machine-sized integer expected at position 2 in
    // UnitVector(4,0).
    check("UnitVector(4,0)", //
        "UnitVector(4,0)");
    check("UnitVector(-3)", //
        "UnitVector(-3)");
    check("UnitVector(4,-2)", //
        "UnitVector(4,-2)");
    check("UnitVector(2)", //
        "{0,1}");
    check("UnitVector(4,3)", //
        "{0,0,1,0}");
    check("UnitVector(4,4)", //
        "{0,0,0,1}");
    check("UnitVector(4,5)", //
        "UnitVector(4,5)");
  }

  @Test
  public void testUpperTriangularize() {
    check("UpperTriangularize(SparseArray({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}}))", //
        "{{a,b,c,d},\n" + //
            " {0,e,f,g},\n" + //
            " {0,0,j,k},\n" + //
            " {0,0,0,o}}");
    check("UpperTriangularize({{1,0}, {0,1}},{})", //
        "UpperTriangularize({{1,0},{0,1}},{})");
    check("UpperTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}}, 1)", //
        "{{0,b,c,d},\n" + " {0,0,f,g},\n" + " {0,0,0,k},\n" + " {0,0,0,0}}");
    check("UpperTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}})", //
        "{{a,b,c,d},\n" + " {0,e,f,g},\n" + " {0,0,j,k}}");
    check("UpperTriangularize({{a,b,c,d}, {d,e,f,g}, {h,i,j,k}, {l,m,n,o}})", //
        "{{a,b,c,d},\n" + " {0,e,f,g},\n" + " {0,0,j,k},\n" + " {0,0,0,o}}");
  }

  @Test
  public void testUpperTriangularMatrixQ() {
    check("m={{a,b,c},{0,e,f},{0,0,g}}", //
        "{{a,b,c},{0,e,f},{0,0,g}}");

    check("UpperTriangularMatrixQ(m,-5)", //
        "True");
    check("UpperTriangularMatrixQ(m,-2)", //
        "True");
    check("UpperTriangularMatrixQ(m,-1)", //
        "True");
    check("UpperTriangularMatrixQ(m)", //
        "True");
    check("UpperTriangularMatrixQ(m,1)", //
        "False");
    check("UpperTriangularMatrixQ(m,5)", //
        "False");

    check("m={{0, 0, 0}, {1, 2, 0}, {2, 3, 0}}", //
        "{{0,0,0},{1,2,0},{2,3,0}}");
    check("UpperTriangularMatrixQ(m,-1)", //
        "False");
    check("UpperTriangularMatrixQ(m)", //
        "False");

    check("s = SparseArray({{1, 1} -> 2, {2, 1} -> 1, {3, 2} -> 5}, {3, 3});", //
        "");
    check("UpperTriangularMatrixQ(s)", //
        "False");
  }

  @Test
  public void testVandermondeMatrix() {
    check("VandermondeMatrix({a,b,c})", //
        "{{1,a,a^2},\n" //
            + " {1,b,b^2},\n" //
            + " {1,c,c^2}}");
    check("VandermondeMatrix({a,b,c,d})", //
        "{{1,a,a^2,a^3},\n" //
            + " {1,b,b^2,b^3},\n" //
            + " {1,c,c^2,c^3},\n" //
            + " {1,d,d^2,d^3}}");
  }

  @Test
  public void testVectorAngle() {
    check("VectorAngle({x,-3,-1/2},{x,-3,-1/2})", //
        "ArcCos((37/4+x*Conjugate(x))/(37/4+Abs(x)^2))");
    check("(1/2)*Sqrt(2)", //
        "1/Sqrt(2)");
    check("ArcCos(1/Sqrt(2))", //
        "Pi/4");
    check("VectorAngle({1,0},{0,1})", //
        "Pi/2");
    check("VectorAngle({1, 2}, {3, 1})", //
        "Pi/4");
    check("VectorAngle({1, 1, 0}, {1, 0, 1})", //
        "Pi/3");
    check("VectorAngle({0, 1}, {0, 1})", //
        "0");

    check("VectorAngle({1,0,0},{1,1,1})", //
        "ArcCos(1/Sqrt(3))");
    check("VectorAngle({1,0},{1,1})", //
        "Pi/4");

    check("Norm({1,0})", //
        "1");
    check("Norm({1,1})", //
        "Sqrt(2)");
    check("{1,0}.{1,1}", //
        "1");
  }

  @Test
  public void testVectorGreater() {
    check("VectorGreaterEqual({SparseArray({1,2,3}),{0,1}})", //
        "False");

    check("VectorGreater({{ },{ }})", //
        "True");
    check("VectorGreater({{ },{ }})", //
        "True");
    check("VectorGreater({{11,12,13},{1/2,-4,-2/3}})", //
        "True");
    check("VectorGreater({{11,12,13},{1/2,12,-2/3}})", //
        "False");
    check("VectorGreater({{11,12,13},{1/2,b,-2/3}})", //
        "VectorGreater({{11,12,13},{1/2,b,-2/3}})");
  }

  @Test
  public void testVectorQ() {
    check("VectorQ({-11/4,33/4,-5/4})", //
        "True");
    check("VectorQ({-11/4,33/4,{-5/4,b}})", //
        "False");

    check("isVecQ(v_) := Length(v) == 3 && VectorQ(v);", //
        "");
    check("isVecQ({a,b,c})", //
        "True");
    check("isVecQ(Det({{{1,0,0},{0,1,0}, {0,0,1}},{a,b,c},{d,e,f}}))", //
        "False");
    check("fromCartesian[pt_?isVecQ, coordsys:_f] := {pt,coordsys};", //
        "");
    check("fromCartesian( {d,e,f} ,g(x))", //
        "fromcartesian({d,e,f},g(x))");
    check("fromCartesian( {d,e,f} ,f(x))", //
        "{{d,e,f},f(x)}");
    check("VectorQ(SparseArray({1, 1/2, 3, I}), NumberQ)", //
        "True");
    check("VectorQ({1, 1/2, 3, I}, NumberQ)", //
        "True");
    check("VectorQ({a, b, c})", //
        "True");
  }
}
