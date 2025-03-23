package org.matheclipse.core.system;

import org.junit.Test;

public class FourierTest extends ExprEvaluatorTestCase {

  @Test
  public void testFourier() {
    check("Fourier({1, 1, 2, 2, 1, 1, 0, 0})", //
        "{2.82843,-0.5+I*1.20711,0.0,0.5+I*(-0.207107),0.0,0.5+I*0.207107,0.0,-0.5+I*(-1.20711)}");
    check("Fourier({1,0,0,1,0,0,1,0})", //
        "{1.06066,0.103553+I*(-0.103553),I*(-0.353553),0.603553+I*0.603553,0.353553,0.603553+I*(-0.603553),I*0.353553,0.103553+I*0.103553}");

    check("Fourier({1,2-I, -I, -1+2*I})", //
        "{1.0,2.0+I*2.0,I*(-1.0),-1.0+I*(-1.0)}");

    check("Fourier({1 + 2*I, 3 + 11*I})", //
        "{2.82843+I*9.19239,-1.41421+I*(-6.36396)}");
    check("Fourier({1,2,0,0})", //
        "{1.5,0.5+I*1.0,-0.5,0.5+I*(-1.0)}");

    // Fourier: Argument {1,2,0,0,7} is restricted to vectors with a length of power of 2.
    check("Fourier({1,2,0,0,7})", //
        "Fourier({1,2,0,0,7})");
  }

  @Test
  public void testFourierCosTransform() {
    check("FourierCosTransform(Exp(-t^2),t,w)", //
        "1/(Sqrt(2)*E^(w^2/4))");

    // TODO
    check("FourierCosTransform(1/Sqrt(t),t,w)", //
        "FourierCosTransform(1/Sqrt(t),t,w)");
    check("FourierCosTransform(Sin(t)/t,t,w)", //
        "FourierCosTransform(Sin(t)/t,t,w)");
  }

  @Test
  public void testFourierSinTransform() {
    // check("FourierSinTransform(Cos(t)/t, t, 1.2)", //
    // "");
    check("FourierSinTransform(Exp(-t),t,w)", //
        "(Sqrt(2/Pi)*w)/(1+w^2)");

    // TODO
    check("FourierSinTransform(Cos(t)/t,t,w)", //
        "FourierSinTransform(Cos(t)/t,t,w)");
    check("FourierSinTransform(t/(t^2+1),t,w)", //
        "Sqrt(2/Pi)*(1/2*Pi*Cosh(w)-1/2*Pi*Sinh(w))");
  }

  @Test
  public void testFourierMatrix() {
    // Maximum AST dimension 1018081 exceeded
    check("FourierMatrix(1009)", //
        "FourierMatrix(1009)");
    check("FourierMatrix(4)", //
        "{{1/2,1/2,1/2,1/2},\n" + " {1/2,I*1/2,-1/2,-I*1/2},\n" + " {1/2,-1/2,1/2,-1/2},\n"
            + " {1/2,-I*1/2,-1/2,I*1/2}}");
    check("FourierMatrix(8)", //
        "{{1/(2*Sqrt(2)),1/(2*Sqrt(2)),1/(2*Sqrt(2)),1/(2*Sqrt(2)),1/(2*Sqrt(2)),1/(2*Sqrt(\n"
            + "2)),1/(2*Sqrt(2)),1/(2*Sqrt(2))},\n"
            + " {1/(2*Sqrt(2)),1/4+I*1/4,(I*1/2)/Sqrt(2),-1/4+I*1/4,-1/(2*Sqrt(2)),-1/4-I*1/4,(-\n"
            + "I*1/2)/Sqrt(2),1/4-I*1/4},\n"
            + " {1/(2*Sqrt(2)),(I*1/2)/Sqrt(2),-1/(2*Sqrt(2)),(-I*1/2)/Sqrt(2),1/(2*Sqrt(2)),(\n"
            + "I*1/2)/Sqrt(2),-1/(2*Sqrt(2)),(-I*1/2)/Sqrt(2)},\n"
            + " {1/(2*Sqrt(2)),-1/4+I*1/4,(-I*1/2)/Sqrt(2),1/4+I*1/4,-1/(2*Sqrt(2)),1/4-I*1/4,(\n"
            + "I*1/2)/Sqrt(2),-1/4-I*1/4},\n"
            + " {1/(2*Sqrt(2)),-1/(2*Sqrt(2)),1/(2*Sqrt(2)),-1/(2*Sqrt(2)),1/(2*Sqrt(2)),-1/(2*Sqrt(\n"
            + "2)),1/(2*Sqrt(2)),-1/(2*Sqrt(2))},\n"
            + " {1/(2*Sqrt(2)),-1/4-I*1/4,(I*1/2)/Sqrt(2),1/4-I*1/4,-1/(2*Sqrt(2)),1/4+I*1/4,(-\n"
            + "I*1/2)/Sqrt(2),-1/4+I*1/4},\n"
            + " {1/(2*Sqrt(2)),(-I*1/2)/Sqrt(2),-1/(2*Sqrt(2)),(I*1/2)/Sqrt(2),1/(2*Sqrt(2)),(-\n"
            + "I*1/2)/Sqrt(2),-1/(2*Sqrt(2)),(I*1/2)/Sqrt(2)},\n"
            + " {1/(2*Sqrt(2)),1/4-I*1/4,(-I*1/2)/Sqrt(2),-1/4-I*1/4,-1/(2*Sqrt(2)),-1/4+I*1/4,(\n"
            + "I*1/2)/Sqrt(2),1/4+I*1/4}}");
  }

  @Test
  public void testFourierDCTMatrix() {
    check("FourierDCTMatrix(1, 1)", //
        "{{1}}");
    check("FourierDCTMatrix(2, 1)", //
        "{{1/Sqrt(2),1/Sqrt(2)},\n" //
            + " {1/Sqrt(2),-1/Sqrt(2)}}");
    check("FourierDCTMatrix(3, 1)", //
        "{{1/2,1/2,1/2},\n"//
            + " {1,0,-1},\n"//
            + " {1/2,-1/2,1/2}}");
    check("FourierDCTMatrix(7, 1)", //
        "{{1/(2*Sqrt(3)),1/(2*Sqrt(3)),1/(2*Sqrt(3)),1/(2*Sqrt(3)),1/(2*Sqrt(3)),1/(2*Sqrt(\n" //
            + "3)),1/(2*Sqrt(3))},\n" //
            + " {1/Sqrt(3),1/2,1/(2*Sqrt(3)),0,-1/(2*Sqrt(3)),-1/2,-1/Sqrt(3)},\n" //
            + " {1/Sqrt(3),1/(2*Sqrt(3)),-1/(2*Sqrt(3)),-1/Sqrt(3),-1/(2*Sqrt(3)),1/(2*Sqrt(3)),1/Sqrt(\n" //
            + "3)},\n" //
            + " {1/Sqrt(3),0,-1/Sqrt(3),0,1/Sqrt(3),0,-1/Sqrt(3)},\n" //
            + " {1/Sqrt(3),-1/(2*Sqrt(3)),-1/(2*Sqrt(3)),1/Sqrt(3),-1/(2*Sqrt(3)),-1/(2*Sqrt(3)),1/Sqrt(\n" //
            + "3)},\n" //
            + " {1/Sqrt(3),-1/2,1/(2*Sqrt(3)),0,-1/(2*Sqrt(3)),1/2,-1/Sqrt(3)},\n" //
            + " {1/(2*Sqrt(3)),-1/(2*Sqrt(3)),1/(2*Sqrt(3)),-1/(2*Sqrt(3)),1/(2*Sqrt(3)),-1/(2*Sqrt(\n" //
            + "3)),1/(2*Sqrt(3))}}");
    check("FourierDCTMatrix(7, 3)", //
        "{{1/Sqrt(7),1/Sqrt(7),1/Sqrt(7),1/Sqrt(7),1/Sqrt(7),1/Sqrt(7),1/Sqrt(7)},\n" //
            + " {(2*Cos(Pi/14))/Sqrt(7),(2*Cos(3/14*Pi))/Sqrt(7),(2*Cos(5/14*Pi))/Sqrt(7),0,(-2*Cos(\n" //
            + "5/14*Pi))/Sqrt(7),(-2*Cos(3/14*Pi))/Sqrt(7),(-2*Cos(Pi/14))/Sqrt(7)},\n" //
            + " {(2*Cos(Pi/7))/Sqrt(7),(2*Cos(3/7*Pi))/Sqrt(7),(-2*Cos(2/7*Pi))/Sqrt(7),-2/Sqrt(\n" //
            + "7),(-2*Cos(2/7*Pi))/Sqrt(7),(2*Cos(3/7*Pi))/Sqrt(7),(2*Cos(Pi/7))/Sqrt(7)},\n" //
            + " {(2*Cos(3/14*Pi))/Sqrt(7),(-2*Cos(5/14*Pi))/Sqrt(7),(-2*Cos(Pi/14))/Sqrt(7),0,(\n" //
            + "2*Cos(Pi/14))/Sqrt(7),(2*Cos(5/14*Pi))/Sqrt(7),(-2*Cos(3/14*Pi))/Sqrt(7)},\n" //
            + " {(2*Cos(2/7*Pi))/Sqrt(7),(-2*Cos(Pi/7))/Sqrt(7),(-2*Cos(3/7*Pi))/Sqrt(7),2/Sqrt(\n" //
            + "7),(-2*Cos(3/7*Pi))/Sqrt(7),(-2*Cos(Pi/7))/Sqrt(7),(2*Cos(2/7*Pi))/Sqrt(7)},\n" //
            + " {(2*Cos(5/14*Pi))/Sqrt(7),(-2*Cos(Pi/14))/Sqrt(7),(2*Cos(3/14*Pi))/Sqrt(7),0,(-\n" //
            + "2*Cos(3/14*Pi))/Sqrt(7),(2*Cos(Pi/14))/Sqrt(7),(-2*Cos(5/14*Pi))/Sqrt(7)},\n" //
            + " {(2*Cos(3/7*Pi))/Sqrt(7),(-2*Cos(2/7*Pi))/Sqrt(7),(2*Cos(Pi/7))/Sqrt(7),-2/Sqrt(\n" //
            + "7),(2*Cos(Pi/7))/Sqrt(7),(-2*Cos(2/7*Pi))/Sqrt(7),(2*Cos(3/7*Pi))/Sqrt(7)}}");

    check("FourierDCTMatrix(7, 4)", //
        "{{Sqrt(2/7)*Cos(Pi/28),Sqrt(2/7)*Cos(3/28*Pi),Sqrt(2/7)*Cos(5/28*Pi),1/Sqrt(7),Sqrt(\n" //
            + "2/7)*Cos(9/28*Pi),Sqrt(2/7)*Cos(11/28*Pi),Sqrt(2/7)*Cos(13/28*Pi)},\n" //
            + " {Sqrt(2/7)*Cos(3/28*Pi),Sqrt(2/7)*Cos(9/28*Pi),-Sqrt(2/7)*Cos(13/28*Pi),-1/Sqrt(\n" //
            + "7),-Sqrt(2/7)*Cos(Pi/28),-Sqrt(2/7)*Cos(5/28*Pi),-Sqrt(2/7)*Cos(11/28*Pi)},\n" //
            + " {Sqrt(2/7)*Cos(5/28*Pi),-Sqrt(2/7)*Cos(13/28*Pi),-Sqrt(2/7)*Cos(3/28*Pi),-1/Sqrt(\n" //
            + "7),Sqrt(2/7)*Cos(11/28*Pi),Sqrt(2/7)*Cos(Pi/28),Sqrt(2/7)*Cos(9/28*Pi)},\n" //
            + " {1/Sqrt(7),-1/Sqrt(7),-1/Sqrt(7),1/Sqrt(7),1/Sqrt(7),-1/Sqrt(7),-1/Sqrt(7)},\n" //
            + " {Sqrt(2/7)*Cos(9/28*Pi),-Sqrt(2/7)*Cos(Pi/28),Sqrt(2/7)*Cos(11/28*Pi),1/Sqrt(7),-Sqrt(\n" //
            + "2/7)*Cos(3/28*Pi),Sqrt(2/7)*Cos(13/28*Pi),Sqrt(2/7)*Cos(5/28*Pi)},\n" //
            + " {Sqrt(2/7)*Cos(11/28*Pi),-Sqrt(2/7)*Cos(5/28*Pi),Sqrt(2/7)*Cos(Pi/28),-1/Sqrt(7),Sqrt(\n" //
            + "2/7)*Cos(13/28*Pi),Sqrt(2/7)*Cos(9/28*Pi),-Sqrt(2/7)*Cos(3/28*Pi)},\n" //
            + " {Sqrt(2/7)*Cos(13/28*Pi),-Sqrt(2/7)*Cos(11/28*Pi),Sqrt(2/7)*Cos(9/28*Pi),-1/Sqrt(\n" //
            + "7),Sqrt(2/7)*Cos(5/28*Pi),-Sqrt(2/7)*Cos(3/28*Pi),Sqrt(2/7)*Cos(Pi/28)}}");
    check("FourierDCTMatrix(7, 2)", //
        "{{1/Sqrt(7),Cos(Pi/14)/Sqrt(7),Cos(Pi/7)/Sqrt(7),Cos(3/14*Pi)/Sqrt(7),Cos(2/7*Pi)/Sqrt(\n" //
            + "7),Cos(5/14*Pi)/Sqrt(7),Cos(3/7*Pi)/Sqrt(7)},\n" //
            + " {1/Sqrt(7),Cos(3/14*Pi)/Sqrt(7),Cos(3/7*Pi)/Sqrt(7),-Cos(5/14*Pi)/Sqrt(7),-Cos(Pi/\n" //
            + "7)/Sqrt(7),-Cos(Pi/14)/Sqrt(7),-Cos(2/7*Pi)/Sqrt(7)},\n" //
            + " {1/Sqrt(7),Cos(5/14*Pi)/Sqrt(7),-Cos(2/7*Pi)/Sqrt(7),-Cos(Pi/14)/Sqrt(7),-Cos(3/\n" //
            + "7*Pi)/Sqrt(7),Cos(3/14*Pi)/Sqrt(7),Cos(Pi/7)/Sqrt(7)},\n" //
            + " {1/Sqrt(7),0,-1/Sqrt(7),0,1/Sqrt(7),0,-1/Sqrt(7)},\n" //
            + " {1/Sqrt(7),-Cos(5/14*Pi)/Sqrt(7),-Cos(2/7*Pi)/Sqrt(7),Cos(Pi/14)/Sqrt(7),-Cos(3/\n" //
            + "7*Pi)/Sqrt(7),-Cos(3/14*Pi)/Sqrt(7),Cos(Pi/7)/Sqrt(7)},\n" //
            + " {1/Sqrt(7),-Cos(3/14*Pi)/Sqrt(7),Cos(3/7*Pi)/Sqrt(7),Cos(5/14*Pi)/Sqrt(7),-Cos(Pi/\n" //
            + "7)/Sqrt(7),Cos(Pi/14)/Sqrt(7),-Cos(2/7*Pi)/Sqrt(7)},\n" //
            + " {1/Sqrt(7),-Cos(Pi/14)/Sqrt(7),Cos(Pi/7)/Sqrt(7),-Cos(3/14*Pi)/Sqrt(7),Cos(2/7*Pi)/Sqrt(\n" //
            + "7),-Cos(5/14*Pi)/Sqrt(7),Cos(3/7*Pi)/Sqrt(7)}}");
  }

  @Test
  public void testFourierDSTMatrix() {
    check("FourierDSTMatrix(1, 1)", //
        "{{1}}");
    check("FourierDSTMatrix(2, 1)", //
        "{{1/Sqrt(2),1/Sqrt(2)},\n" //
            + " {1/Sqrt(2),-1/Sqrt(2)}}");
    check("FourierDSTMatrix(3, 1)", //
        "{{1/2,1/Sqrt(2),1/2},\n"//
            + " {1/Sqrt(2),0,-1/Sqrt(2)},\n"//
            + " {1/2,-1/Sqrt(2),1/2}}");
    check("FourierDSTMatrix(7, 1)", //
        "{{Sin(Pi/8)/2,1/(2*Sqrt(2)),Cos(Pi/8)/2,1/2,Cos(Pi/8)/2,1/(2*Sqrt(2)),Sin(Pi/8)/\n" //
            + "2},\n" //
            + " {1/(2*Sqrt(2)),1/2,1/(2*Sqrt(2)),0,-1/(2*Sqrt(2)),-1/2,-1/(2*Sqrt(2))},\n" //
            + " {Cos(Pi/8)/2,1/(2*Sqrt(2)),-Sin(Pi/8)/2,-1/2,-Sin(Pi/8)/2,1/(2*Sqrt(2)),Cos(Pi/\n" //
            + "8)/2},\n" //
            + " {1/2,0,-1/2,0,1/2,0,-1/2},\n" //
            + " {Cos(Pi/8)/2,-1/(2*Sqrt(2)),-Sin(Pi/8)/2,1/2,-Sin(Pi/8)/2,-1/(2*Sqrt(2)),Cos(Pi/\n" //
            + "8)/2},\n" //
            + " {1/(2*Sqrt(2)),-1/2,1/(2*Sqrt(2)),0,-1/(2*Sqrt(2)),1/2,-1/(2*Sqrt(2))},\n" //
            + " {Sin(Pi/8)/2,-1/(2*Sqrt(2)),Cos(Pi/8)/2,-1/2,Cos(Pi/8)/2,-1/(2*Sqrt(2)),Sin(Pi/\n" //
            + "8)/2}}");
    check("FourierDSTMatrix(7, 3)", //
        "{{(2*Sin(Pi/14))/Sqrt(7),(2*Sin(3/14*Pi))/Sqrt(7),(2*Sin(5/14*Pi))/Sqrt(7),2/Sqrt(\n" //
            + "7),(2*Sin(5/14*Pi))/Sqrt(7),(2*Sin(3/14*Pi))/Sqrt(7),(2*Sin(Pi/14))/Sqrt(7)},\n" //
            + " {(2*Sin(Pi/7))/Sqrt(7),(2*Sin(3/7*Pi))/Sqrt(7),(2*Sin(2/7*Pi))/Sqrt(7),0,(-2*Sin(\n" //
            + "2/7*Pi))/Sqrt(7),(-2*Sin(3/7*Pi))/Sqrt(7),(-2*Sin(Pi/7))/Sqrt(7)},\n" //
            + " {(2*Sin(3/14*Pi))/Sqrt(7),(2*Sin(5/14*Pi))/Sqrt(7),(-2*Sin(Pi/14))/Sqrt(7),-2/Sqrt(\n" //
            + "7),(-2*Sin(Pi/14))/Sqrt(7),(2*Sin(5/14*Pi))/Sqrt(7),(2*Sin(3/14*Pi))/Sqrt(7)},\n" //
            + " {(2*Sin(2/7*Pi))/Sqrt(7),(2*Sin(Pi/7))/Sqrt(7),(-2*Sin(3/7*Pi))/Sqrt(7),0,(2*Sin(\n" //
            + "3/7*Pi))/Sqrt(7),(-2*Sin(Pi/7))/Sqrt(7),(-2*Sin(2/7*Pi))/Sqrt(7)},\n" //
            + " {(2*Sin(5/14*Pi))/Sqrt(7),(-2*Sin(Pi/14))/Sqrt(7),(-2*Sin(3/14*Pi))/Sqrt(7),2/Sqrt(\n" //
            + "7),(-2*Sin(3/14*Pi))/Sqrt(7),(-2*Sin(Pi/14))/Sqrt(7),(2*Sin(5/14*Pi))/Sqrt(7)},\n" //
            + " {(2*Sin(3/7*Pi))/Sqrt(7),(-2*Sin(2/7*Pi))/Sqrt(7),(2*Sin(Pi/7))/Sqrt(7),0,(-2*Sin(Pi/\n" //
            + "7))/Sqrt(7),(2*Sin(2/7*Pi))/Sqrt(7),(-2*Sin(3/7*Pi))/Sqrt(7)},\n" //
            + " {1/Sqrt(7),-1/Sqrt(7),1/Sqrt(7),-1/Sqrt(7),1/Sqrt(7),-1/Sqrt(7),1/Sqrt(7)}}");

    check("FourierDSTMatrix(7, 4)", //
        "{{Sqrt(2/7)*Sin(Pi/28),Sqrt(2/7)*Sin(3/28*Pi),Sqrt(2/7)*Sin(5/28*Pi),1/Sqrt(7),Sqrt(\n" //
            + "2/7)*Sin(9/28*Pi),Sqrt(2/7)*Sin(11/28*Pi),Sqrt(2/7)*Sin(13/28*Pi)},\n" //
            + " {Sqrt(2/7)*Sin(3/28*Pi),Sqrt(2/7)*Sin(9/28*Pi),Sqrt(2/7)*Sin(13/28*Pi),1/Sqrt(7),Sqrt(\n" //
            + "2/7)*Sin(Pi/28),-Sqrt(2/7)*Sin(5/28*Pi),-Sqrt(2/7)*Sin(11/28*Pi)},\n" //
            + " {Sqrt(2/7)*Sin(5/28*Pi),Sqrt(2/7)*Sin(13/28*Pi),Sqrt(2/7)*Sin(3/28*Pi),-1/Sqrt(\n" //
            + "7),-Sqrt(2/7)*Sin(11/28*Pi),-Sqrt(2/7)*Sin(Pi/28),Sqrt(2/7)*Sin(9/28*Pi)},\n" //
            + " {1/Sqrt(7),1/Sqrt(7),-1/Sqrt(7),-1/Sqrt(7),1/Sqrt(7),1/Sqrt(7),-1/Sqrt(7)},\n" //
            + " {Sqrt(2/7)*Sin(9/28*Pi),Sqrt(2/7)*Sin(Pi/28),-Sqrt(2/7)*Sin(11/28*Pi),1/Sqrt(7),Sqrt(\n" //
            + "2/7)*Sin(3/28*Pi),-Sqrt(2/7)*Sin(13/28*Pi),Sqrt(2/7)*Sin(5/28*Pi)},\n" //
            + " {Sqrt(2/7)*Sin(11/28*Pi),-Sqrt(2/7)*Sin(5/28*Pi),-Sqrt(2/7)*Sin(Pi/28),1/Sqrt(7),-Sqrt(\n" //
            + "2/7)*Sin(13/28*Pi),Sqrt(2/7)*Sin(9/28*Pi),-Sqrt(2/7)*Sin(3/28*Pi)},\n" //
            + " {Sqrt(2/7)*Sin(13/28*Pi),-Sqrt(2/7)*Sin(11/28*Pi),Sqrt(2/7)*Sin(9/28*Pi),-1/Sqrt(\n" //
            + "7),Sqrt(2/7)*Sin(5/28*Pi),-Sqrt(2/7)*Sin(3/28*Pi),Sqrt(2/7)*Sin(Pi/28)}}");
    check("FourierDSTMatrix(7, 2)", //
        "{{Sin(Pi/14)/Sqrt(7),Sin(Pi/7)/Sqrt(7),Sin(3/14*Pi)/Sqrt(7),Sin(2/7*Pi)/Sqrt(7),Sin(\n" //
            + "5/14*Pi)/Sqrt(7),Sin(3/7*Pi)/Sqrt(7),1/Sqrt(7)},\n" //
            + " {Sin(3/14*Pi)/Sqrt(7),Sin(3/7*Pi)/Sqrt(7),Sin(5/14*Pi)/Sqrt(7),Sin(Pi/7)/Sqrt(7),-Sin(Pi/\n" //
            + "14)/Sqrt(7),-Sin(2/7*Pi)/Sqrt(7),-1/Sqrt(7)},\n" //
            + " {Sin(5/14*Pi)/Sqrt(7),Sin(2/7*Pi)/Sqrt(7),-Sin(Pi/14)/Sqrt(7),-Sin(3/7*Pi)/Sqrt(\n" //
            + "7),-Sin(3/14*Pi)/Sqrt(7),Sin(Pi/7)/Sqrt(7),1/Sqrt(7)},\n" //
            + " {1/Sqrt(7),0,-1/Sqrt(7),0,1/Sqrt(7),0,-1/Sqrt(7)},\n" //
            + " {Sin(5/14*Pi)/Sqrt(7),-Sin(2/7*Pi)/Sqrt(7),-Sin(Pi/14)/Sqrt(7),Sin(3/7*Pi)/Sqrt(\n" //
            + "7),-Sin(3/14*Pi)/Sqrt(7),-Sin(Pi/7)/Sqrt(7),1/Sqrt(7)},\n" //
            + " {Sin(3/14*Pi)/Sqrt(7),-Sin(3/7*Pi)/Sqrt(7),Sin(5/14*Pi)/Sqrt(7),-Sin(Pi/7)/Sqrt(\n" //
            + "7),-Sin(Pi/14)/Sqrt(7),Sin(2/7*Pi)/Sqrt(7),-1/Sqrt(7)},\n" //
            + " {Sin(Pi/14)/Sqrt(7),-Sin(Pi/7)/Sqrt(7),Sin(3/14*Pi)/Sqrt(7),-Sin(2/7*Pi)/Sqrt(7),Sin(\n" //
            + "5/14*Pi)/Sqrt(7),-Sin(3/7*Pi)/Sqrt(7),1/Sqrt(7)}}");
  }

  @Test
  public void testInverseFourier() {
    check("InverseFourier({1 + 2*I, 3 + 4*I})", //
        "{2.82843+I*4.24264,-1.41421+I*(-1.41421)}");
    check("InverseFourier({2.82843+I*9.19239,-1.41421+I*(-6.36396)})", //
        "{1.0+I*2.0,3.0+I*11.0}");
    check("InverseFourier({1.5,0.5+I*1.0,-0.5,0.5+I*(-1.0)})", //
        "{1.0,2.0+I*2.22045*10^-16,0.0,I*(-2.22045*10^-16)}");

    // InverseFourier: Argument {1,2,0,0,7} is restricted to vectors with a length of power of 2.
    check("InverseFourier({1,2,0,0,7})", //
        "InverseFourier({1,2,0,0,7})");
  }
}
