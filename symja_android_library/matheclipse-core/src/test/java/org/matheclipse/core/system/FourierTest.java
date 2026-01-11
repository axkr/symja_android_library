package org.matheclipse.core.system;

import org.junit.Test;

public class FourierTest extends ExprEvaluatorTestCase {

  @Test
  public void testFourier() {
    // 4d tensor
    check("x = ConstantArray(0, {2, 3, 4}); x[[1, 1, 1]] = 1; x[[2, 2, 2]] = 1;", //
        "");
    check("Fourier(x)", //
        "{{{0.408248,0.204124+I*0.204124,0.0,0.204124+I*(-0.204124)}," //
            + "{0.102062+I*0.176777,0.0273474+I*(-0.102062),0.306186+I*(-0.176777),0.380901+I*0.102062}," //
            + "{0.102062+I*(-0.176777),0.380901+I*(-0.102062),0.306186+I*0.176777,0.0273474+I*0.102062}}," //
            + "{{0.0,0.204124+I*(-0.204124),0.408248,0.204124+I*0.204124}," //
            + "{0.306186+I*(-0.176777),0.380901+I*0.102062,0.102062+I*0.176777,0.0273474+I*(-0.102062)}," //
            + "{0.306186+I*0.176777,0.0273474+I*0.102062,0.102062+I*(-0.176777),0.380901+I*(-0.102062)}}}");


    check("Fourier({1, 0, 1, 0, 0, 1, 0, 0, 0, 1}, FourierParameters -> {1, 1})", //
        "{4.0,1.11803+I*0.363271,1.5+I*(-0.363271),-1.11803+I*(-1.53884),1.5+I*(-1.53884),I*1.46958*10^-15,1.5+I*1.53884,-1.11803+I*1.53884,1.5+I*0.363271,1.11803+I*(-0.363271)}");
    check("Fourier({1, 0, 1, 0, 0, 1, 0, 0, 0, 1}, FourierParameters -> {-1, 1})", //
        "{0.4,0.111803+I*0.0363271,0.15+I*(-0.0363271),-0.111803+I*(-0.153884),0.15+I*(-0.153884),0.0,0.15+I*0.153884,-0.111803+I*0.153884,0.15+I*0.0363271,0.111803+I*(-0.0363271)}");

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

    check("Fourier({1,2,0,0,7})", //
        "{4.47214,1.69098+I*(-2.12663),-2.80902+I*(-1.31433),-2.80902+I*1.31433,1.69098+I*2.12663}");
    check(" Abs(Fourier({1, 1, 2, 2, 1, 1, 0, 0}))^2", //
        "{8.0,1.70711,0.0,0.292893,0.0,0.292893,0.0,1.70711}");
    check("Fourier({1, 0, 0, 1, 0, 0, 1})", //
        "{1.13389,0.273087+I*(-0.131512),0.529516+I*(-0.663993),-0.0466748+I*0.204495,-0.0466748+I*(-0.204495),0.529516+I*0.663993,0.273087+I*0.131512}");
  }

  @Test
  public void testFourierSparse() {
    // Message: Position specification {5} in Fourier({1,2,0,0},{5}) is not applicable.
    check("Fourier({1, 2, 0, 0}, {5})", //
        "Fourier({1,2,0,0},{5})");

    // 1. Basic extraction from a simple list
    // Full: Fourier({1, 2, 0, 0}) -> {1.5, 0.5+I*1.0, -0.5, 0.5+I*(-1.0)}
    check("Fourier({1, 2, 0, 0}, {1, 3})", //
        "{1.5,-0.5}");
    check("Fourier({1, 2, 0, 0}, {2})", //
        "{0.5+I*1.0}");

    // Unsorted and repeated indices
    check("Fourier({1, 2, 0, 0}, {3, 1, 3})", //
        "{-0.5,1.5,-0.5}");

    // 3. Extraction with Complex input
    // Full: Fourier({1, 2-I, -I, -1+2*I}) -> {1.0, 2.0+I*2.0, I*(-1.0), -1.0+I*(-1.0)}
    check("Fourier({1, 2-I, -I, -1+2*I}, {1, 4})", //
        "{1.0,-1.0+I*(-1.0)}");

    // Extraction with FourierParameters -> {1, 1}
    // Full: Fourier({1, 0, 1, 0, 0, 1, 0, 0, 0, 1}, FourierParameters -> {1, 1})
    // -> {4.0, 1.11803+I*0.363271, ...}
    check("Fourier({1, 0, 1, 0, 0, 1, 0, 0, 0, 1}, {1, 2}, FourierParameters -> {1, 1})", //
        "{4.0,1.11803+I*0.363271}");

    // Extraction with FourierParameters -> {-1, 1} (Data Analysis convention)
    // Full: Fourier({1, 0, 1, 0, 0, 1, 0, 0, 0, 1}, FourierParameters -> {-1, 1})
    // -> {0.4, 0.111803+I*0.0363271, ...}
    check("Fourier({1, 0, 1, 0, 0, 1, 0, 0, 0, 1}, {1, 2}, FourierParameters -> {-1, 1})", //
        "{0.4,0.111803+I*0.0363271}");

    // Full: Fourier({1, 1, 2, 2, 1, 1, 0, 0})
    // -> {2.82843, -0.5+I*1.20711, 0.0, 0.5+I*(-0.207107), 0.0, 0.5+I*0.207107, 0.0,
    // -0.5+I*(-1.20711)}
    // Indices {3, 5, 7} are all 0.0
    check("Fourier({1, 1, 2, 2, 1, 1, 0, 0}, {3, 5, 7})", //
        "{0.0,0.0,-6.38752*10^-16}");
    // Indices {2, 8} check conjugate symmetry parts
    check("Fourier({1, 1, 2, 2, 1, 1, 0, 0}, {2, 8})", //
        "{-0.5+I*1.20711,-0.5+I*(-1.20711)}");
    check("Fourier({1, 2, 3, 4}, {1, 2, 3, 4})", //
        "{5.0,-1.0+I*(-1.0),-1.0+I*4.89859*10^-16,-1.0+I*1.0}");
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
    // 4d tensor
    check("x = ConstantArray(0, {2, 3, 3, 2}); x[[1, 1, 1, 1]] = 1;", //
        "");
    check("InverseFourier(x)", //
        "{{{{0.166667,0.166667},{0.166667,0.166667},{0.166667,0.166667}},{{0.166667,0.166667},{0.166667,0.166667},{0.166667,0.166667}},{{0.166667,0.166667},{0.166667,0.166667},{0.166667,0.166667}}},{{{0.166667,0.166667},{0.166667,0.166667},{0.166667,0.166667}},{{0.166667,0.166667},{0.166667,0.166667},{0.166667,0.166667}},{{0.166667,0.166667},{0.166667,0.166667},{0.166667,0.166667}}}}");

    check("InverseFourier({1, 0, 1, 0, 0, 1, 0, 0, 0, 1}, FourierParameters -> {1, 1})", //
        "{0.4,0.111803+I*(-0.0363271),0.15+I*0.0363271,-0.111803+I*0.153884," //
            + "0.15+I*0.153884,0.0,0.15+I*(-0.153884),-0.111803+I*(-0.153884)," //
            + "0.15+I*(-0.0363271),0.111803+I*0.0363271}");
    check("InverseFourier({1, 0, 1, 0, 0, 1, 0, 0, 0, 1}, FourierParameters -> {-1, 1})", //
        "{4.0,1.11803+I*(-0.363271),1.5+I*0.363271,-1.11803+I*1.53884,1.5+I*1.53884," //
            + "I*(-1.46958*10^-15),1.5+I*(-1.53884),-1.11803+I*(-1.53884),1.5+I*(-0.363271)," //
            + "1.11803+I*0.363271}");

    check("InverseFourier({1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1})", //
        "{2.7735,0.0603679+I*0.204301,0.0166159+I*0.319665," //
            + "-0.436565+I*(-0.0818178),0.33421+I*0.601029," //
            + "0.231309+I*0.246434,0.210088+I*(-0.421207)," //
            + "0.210088+I*0.421207,0.231309+I*(-0.246434),0.33421+I*(-0.601029)," //
            + "-0.436565+I*0.0818178,0.0166159+I*(-0.319665),0.0603679+I*(-0.204301)}");
    check("InverseFourier({1 + 2*I, 3 + 4*I})", //
        "{2.82843+I*4.24264,-1.41421+I*(-1.41421)}");
    check("InverseFourier({2.82843+I*9.19239,-1.41421+I*(-6.36396)})", //
        "{1.0+I*2.0,3.0+I*11.0}");
    check("InverseFourier({1.5,0.5+I*1.0,-0.5,0.5+I*(-1.0)})", //
        "{1.0,2.0,0.0,0.0}");

    check("InverseFourier({1,2,0,0,7})", //
        "{4.47214,1.69098+I*2.12663,-2.80902+I*1.31433,-2.80902+I*(-1.31433),1.69098+I*(-2.12663)}");
  }

  @Test
  public void testInverseFourierSparse() {
    // Message: Position specification {5} in InverseFourier({1,2,0,0},{5}) is not applicable.
    check("InverseFourier({1, 2, 0, 0}, {5})", //
        "InverseFourier({1,2,0,0},{5})");

    // 1. Basic extraction from a simple list
    // Full: InverseFourier({1, 0, 0, 0}) (n=4) -> {0.5, 0.5, 0.5, 0.5} (Default Scale 1/sqrt(n))
    check("InverseFourier({1, 0, 0, 0}, {1, 3})", //
        "{0.5,0.5}");

    // Full: InverseFourier({0, 1, 0, 0}) -> {0.5, -0.5I, -0.5, 0.5I}
    // Explanation: 0.5 * e^(-2pi*i*1*s/4) for s=0..3 (indices 1..4)
    // s=1 (idx 2): 0.5 * -i = -0.5I
    check("InverseFourier({0, 1, 0, 0}, {2})", //
        "{I*0.5}");

    // 2. Unsorted and repeated indices
    check("InverseFourier({0, 1, 0, 0}, {4, 2, 4})", //
        "{I*(-0.5),I*0.5,I*(-0.5)}");

    // 3. Extraction with Complex input
    // Reversing the Fourier test logic:
    // Fourier({1, 2, 0, 0}) -> {1.5, 0.5+I, -0.5, 0.5-I}
    // Therefore InverseFourier({1.5, 0.5+I, -0.5, 0.5-I}) -> {1.0, 2.0, 0.0, 0.0}
    check("InverseFourier({1.5, 0.5+I, -0.5, 0.5-I}, {1, 2})", //
        "{1.0,0.0}");

    // 4. Extraction with FourierParameters -> {1, 1}
    // Formula: 1/n^((1+a)/2) * Sum[...] = 1/n * Sum[...]
    // Input {1, 0, 0, 0}, n=4. Scale 1/4 = 0.25.
    check("InverseFourier({1, 0, 0, 0}, {1, 2}, FourierParameters -> {1, 1})", //
        "{0.25,0.25}");

    // 5. Extraction with FourierParameters -> {-1, 1} (Data Analysis convention)
    // Formula: 1/n^((1+a)/2) * Sum[...] = 1/n^0 * Sum[...] = 1 * Sum[...]
    // Input {1, 0, 0, 0}, n=4. Scale 1.
    check("InverseFourier({1, 0, 0, 0}, {1, 2}, FourierParameters -> {-1, 1})", //
        "{1.0,1.0}");

    // 6. Larger list specific positions
    // InverseFourier of {1, 2, 3, 4} (n=4)
    // Scale 0.5. Sum = 10 -> DC = 5.0.
    // Index 2 (k=1): 0.5 * (1 - 2i - 3 + 4i) = 0.5 * (-2 + 2i) = -1 + I
    // Index 3 (k=2): 0.5 * (1 - 2 + 3 - 4) = -1
    // Index 4 (k=3): 0.5 * (1 + 2i - 3 - 4i) = -1 - I
    check("InverseFourier({1, 2, 3, 4}, {1, 2, 3, 4})", //
        "{5.0,-1.0+I*(-1.0),-1.0+I*4.89859*10^-16,-1.0+I*1.0}");
  }

  @Test
  public void testSpectrogramArray() {
    check("d1 = Table(Sin(Pi/4*3*n), {n, 0, 7})", //
        "{0,1/Sqrt(2),-1,1/Sqrt(2),0,-1/Sqrt(2),1,-1/Sqrt(2)}");
    check("SpectrogramArray(d1, 4) // MatrixForm ", //
        "{{0.414214,1.0,-2.41421,1.0},\n"//
            + " {0.414214,I*1.0,2.41421,I*(-1.0)},\n"//
            + " {-1.0,-1.0+I*(-1.41421),-1.0,-1.0+I*1.41421},\n"//
            + " {1.0,1.41421+I*1.0,-1.0,1.41421+I*(-1.0)},\n"//
            + " {-0.414214,-1.0,2.41421,-1.0},\n"//
            + " {-1.12132,I*(-1.70711),-1.70711,I*1.70711},\n"//
            + " {-1.12132,1.70711,1.70711,1.70711},\n"//
            + " {-2.82843,0.0,0.0,0.0}}");
    check("SpectrogramArray(d1, 4,2) // MatrixForm ", //
        "{{0.414214,1.0,-2.41421,1.0},\n"//
            + " {-1.0,-1.0+I*(-1.41421),-1.0,-1.0+I*1.41421},\n"//
            + " {-0.414214,-1.0,2.41421,-1.0},\n"//
            + " {-1.12132,1.70711,1.70711,1.70711}}");
  }

  @Test
  public void testPeriodogramArray() {
    check("PeriodogramArray({0, 1, 0, -1, 0, 1, 1, -1}, 4)", //
        "{0.125,1.125,0.125,1.125}");
    check("PeriodogramArray({0, 1, 0, -1, 0, 1, 1, -1}, 4, 2)", //
        "{0.0833333,1.08333,0.0833333,1.08333}");
    check("PeriodogramArray({0, 1, 0, -1, 0, 1, 1, -1}, 4, 2, {.5, 1, 1, .2})", //
        "{0.657933,0.774381,0.19214,0.774381}");
    check("PeriodogramArray({0, 1, 0, -1, 0, 1, 1, -1}, 4, 2, HammingWindow)", //
        "{0.84538,0.775841,0.261247,0.775841}");
  }
}
