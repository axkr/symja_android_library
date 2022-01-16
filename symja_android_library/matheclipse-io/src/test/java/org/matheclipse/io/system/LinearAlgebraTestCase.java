package org.matheclipse.io.system;

/** Tests built-in functions */
public class LinearAlgebraTestCase extends AbstractTestCase {

  public LinearAlgebraTestCase(String name) {
    super(name);
  }

  public void testAngleVector() {
    check("AngleVector(x)", //
        "{Cos(x),Sin(x)}");
    check("AngleVector(Pi/6)", //
        "{Sqrt(3)/2,1/2}");
    check("AngleVector(90*Degree)", //
        "{0,1}");
    check("AngleVector({1, 10}, a)", //
        "{1+Cos(a),10+Sin(a)}");
  }

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

  public void testAntisymmetricMatrixQ() {
    check("AntisymmetricMatrixQ({{42, 7 + 11*I}, {-7 + 11*I, 21}})", //
        "False");
    check("AntisymmetricMatrixQ({{0, -2, 3}, {2, 0, -4}, {-3, 4, 0}})", //
        "True");
  }

  public void testCholeskyDecomposition() {
    check("matG=CholeskyDecomposition({{11.0,3.0},{3.0, 5.0}})", //
        "{{3.31662,0.904534},\n" + //
            " {0.0,2.04495}}");
    check("Transpose(matG).matG", //
        "{{11.0,3.0},\n" + //
            " {3.0,5.0}}");
  }

  public void testCirclePoints() {
    // check("CirclePoints(3)", "{{Sqrt(3)/2,-1/2},{0,1},{-Sqrt(3)/2,-1/2}}");
    check("CirclePoints(2)", //
        "{{1,0},{-1,0}}");

    check("CirclePoints(4)", //
        "{{1/Sqrt(2),-1/Sqrt(2)},{1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),1/Sqrt(2)},{-1/Sqrt(2),-\n"
            + "1/Sqrt(2)}}");
    // check("CirclePoints(10)", "");
  }

  public void testCofactor() {
    check("Cofactor({{1, 2, 3, 4}, {5, 6, 7, 8}, {8, 7, 6, 4}, {5, 3, 2, 1}}, {3, 2})", //
        "4");
  }

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

  public void testConjugateTranspose() {
    check("ConjugateTranspose({{{0,0},{0,0}},{0,0}})", //
        "ConjugateTranspose({{{0,0},{0,0}},{0,0}})");

    check("ConjugateTranspose(SparseArray({{1,2+I,3},{4,5-I,6},{7,8,9}}))", //
        "SparseArray(Number of elements: 9 Dimensions: {3,3} Default value: 0)");
    check("ConjugateTranspose(SparseArray({{1,2+I,3},{4,5-I,6},{7,8,9}})) // Normal", //
        "{{1,4,7},\n" + " {2-I,5+I,8},\n" + " {3,6,9}}");
    check("ConjugateTranspose({{1,2+I,3},{4,5-I,6},{7,8,9}})", //
        "{{1,4,7},\n" + //
            " {2-I,5+I,8},\n" + //
            " {3,6,9}}");
    check("ConjugateTranspose(N({{1,2+I,3},{4,5-I,6},{7,8,9}}))", //
        "{{1.0,4.0,7.0},\n" + //
            " {2.0+I*(-1.0),5.0+I*1.0,8.0},\n" + //
            " {3.0,6.0,9.0}}");

    check("ConjugateTranspose({{1, 2*I, 3}, {3 + 4*I, 5, I}})", //
        "{{1,3-I*4},\n" + //
            " {-I*2,5},\n" + //
            " {3,-I}}");
  }

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

  public void testDesignMatrix() {
    // check("data = Table({i, i^(3/2) }, {i, 2})", //
    // "{{1,1},{2,2*Sqrt(2)}}");
    // check("DesignMatrix(data, x, x)", //
    // "{{1,1},{1,2}}");
    // check("DesignMatrix(data, {x, x^2}, x)", //
    // "{{1,{{x,x^2}},{x,x},{1}},{1,{{x,x^2}},{x,x},{2}}}");
    check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, x, x)", //
        "{{1,2},{1,3},{1,5},{1,7}}");
    check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, f(x), x)", //
        "{{1,f(2)},{1,f(3)},{1,f(5)},{1,f(7)}}");

    check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, x, x)", //
        "{{1,2},{1,3},{1,5},{1,7}}");
    check("DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, f(x), x)", //
        "{{1,f(2)},{1,f(3)},{1,f(5)},{1,f(7)}}");
  }

  public void testDet() {

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

  public void testDiagonalMatrix() {
    check("DiagonalMatrix(SparseArray({1, 2, 3}))", //
        "{{1,0,0},\n" + //
            " {0,2,0},\n" + //
            " {0,0,3}}");
    check("DiagonalMatrix({1, 2, 3})", //
        "{{1,0,0},\n" + //
            " {0,2,0},\n" + //
            " {0,0,3}}");
  }


  public void testDiagonalnMatrixQ() {
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

  public void testDimensions() {
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

  public void testDot() {
    check("#1.#123 // FullForm", //
        "Dot(Slot(1), Slot(123))");
    // github #121 - print error

    // message Dot: Nonrectangular tensor encountered
    check("Dot({{0,2},{-8,2}},{{{0},0},{0,3}})", //
        "{{0,2},\n" + " {-8,2}}.{{{0},0},{0,3}}");

    // message Dot: Tensors {{}} and {{}} have incompatible shapes.
    check("{{}}.{{}}", //
        "{{}}.\n" + "{{}}");

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


  public void testEigenvalues() {
    // check("m = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}", //
    // "{{1,2,3},{4,5,6},{7,8,9}}");
    // check("Roots(CharacteristicPolynomial(m,x)==0, x)",//
    // "x==0||x==15/2-3/2*Sqrt(33)||x==15/2+3/2*Sqrt(33)");
    // check("EigenValues(m)",//
    // "{15/2+3/2*Sqrt(33),15/2-3/2*Sqrt(33),0}");

    // Eigenvalues
    check("Eigenvalues({{-8, 12, 4}, {12, -20, 0}, {4, 0, -2}})", //
        "{2.24386,-4.6517,-27.59215}");
    check("Eigenvalues(SparseArray({{1.0, 2, 3}, {4, 5, 6}, {7, 8, 9}}))", //
        "{16.11684,-1.11684,-9.29650*10^-16}");

    check("Eigenvalues(A)", //
        "Eigenvalues(A)");
    check("Eigenvalues({{1.0, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "{16.11684,-1.11684,-9.29650*10^-16}");
    check("Eigenvalues({{a}})", //
        "{a}");
    check("Eigenvalues({{a, b}, {0, a}})", //
        "{a,a}");
    check("Eigenvalues({{a, b}, {0, d}})", //
        "{1/2*(a+d-Sqrt(a^2-2*a*d+d^2)),1/2*(a+d+Sqrt(a^2-2*a*d+d^2))}");
    check("Eigenvalues({{a,b}, {c,d}})", //
        "{1/2*(a+d-Sqrt(a^2+4*b*c-2*a*d+d^2)),1/2*(a+d+Sqrt(a^2+4*b*c-2*a*d+d^2))}");
    check("Eigenvalues({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})", //
        "{1.0,1.0,1.0}");
    check("Eigenvalues({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})", //
        "{16.11684,-1.11684,-9.29650*10^-16}");
  }

  public void testEigenvectors() {
    check("Eigenvectors(SparseArray({{1.0, 2.0, 3}, {4, 5, 6}, {7, 8, 9}}))", //
        "{{0.231971,0.525322,0.818673},{0.78583,0.0867513,-0.612328},{-0.408248,0.816497,-0.408248}}");
    check("Eigenvectors({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}) // MatrixForm", //
        "{{0.231971,0.525322,0.818673},\n" //
            + " {0.78583,0.0867513,-0.612328},\n" + " {-0.408248,0.816497,-0.408248}}");
    check("Eigenvectors({{1.1, 2.2, 3.25}, {0.76, 4.6, 5}, {0.1, 0.1, 6.1}}) // MatrixForm", //
        "{{0.985096,-0.171352,-0.0149803},\n" + " {0.48687,0.833694,0.260598},\n"
            + " {0.479424,0.873368,-0.085911}}");
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
    check("Eigenvectors({{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})", //
        "{{1.0,0.0,0.0},{0.0,1.0,0.0},{0.0,0.0,1.0}}");

  }

  public void testFourierMatrix() {
    check("FourierMatrix(1009)", //
        "Maximum AST dimension 1018081 exceeded");
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

  public void testFromPolarCoordinates() {
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

  public void testHilbertMatrix() {
    check("Inverse(HilbertMatrix(3))", //
        "{{9,-36,30},\n" + " {-36,192,-180},\n" + " {30,-180,180}}");
  }

  public void testInverse() {
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

  public void testLeastSquares() {
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

  public void testLinearSolve() {

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
        "{(-h*l*x+g*m*x+c*l*y-b*m*y-c*g*z+b*h*z)/(-c*g*k+b*h*k+c*f*l-a*h*l-b*f*m+a*g*m),(h*k*x-f*m*x-c*k*y+a*m*y+c*f*z-a*h*z)/(-c*g*k+b*h*k+c*f*l-a*h*l-b*f*m+a*g*m),(-g*k*x+f*l*x+b*k*y-a*l*y-b*f*z+a*g*z)/(-c*g*k+b*h*k+c*f*l-a*h*l-b*f*m+a*g*m),\n"
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

  public void testLinearSolveFunction002() {
    check("lsf=LinearSolve(HilbertMatrix(6));", //
        "");
    check("lsf[{1,1,1,1,1,1}]", //
        "{-6,210,-1680,5040,-6300,2772}");
  }

  public void testLinearSolveFunction003() {
    check("lsf=LinearSolve(N(HilbertMatrix(6)));", //
        "");
    check("lsf[{1,1,1,1,1,1}]", //
        "{-6.0,210.0,-1680.0,5040.0,-6300.0,2772.0}");
  }

  public void testLinearSolveFunction004() {
    check("lsf=LinearSolve(N(HilbertMatrix(6),30));", //
        "");
    check("lsf[{1,1,1,1,1,1}]", //
        "{-5.99999999999999999999999999332,209.999999999999999999999999809,-1679.9999999999999999999999987," //
            + "5039.99999999999999999999999662,-6299.99999999999999999999999627,2771.99999999999999999999999853}");
  }

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
        "{{1,0,0},\n" + " {0,1,0},\n" + " {0,0,1}}");
  }

  public void testMatrixExp() {
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
    // check("MatrixExp({{0, 1}, {-1, 0}}*t)", //
    // "");
  }

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
        "12-16*x+7*x^2-x^3");
    check("Factor(6-5*x+x^2)", //
        "(-3+x)*(-2+x)");
    check("Factor(12-16*x+7*x^2-x^3)", //
        "(2-x)^2*(3-x)");
  }

  public void testMatrixPower() {
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
  }

  public void testMatrixQ() {
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

  public void testMinors() {
    check("m0 = Array(Subscript(a, ##) &, {3, 3})", //
        "{{Subscript(a,1,1),Subscript(a,1,2),Subscript(a,1,3)},{Subscript(a,2,1),Subscript(a,\n"
            + "2,2),Subscript(a,2,3)},{Subscript(a,3,1),Subscript(a,3,2),Subscript(a,3,3)}}");
    check("Minors(m0)", //
        "{{-Subscript(a,1,2)*Subscript(a,2,1)+Subscript(a,1,1)*Subscript(a,2,2),-Subscript(a,\n"
            + "1,3)*Subscript(a,2,1)+Subscript(a,1,1)*Subscript(a,2,3),-Subscript(a,1,3)*Subscript(a,\n"
            + "2,2)+Subscript(a,1,2)*Subscript(a,2,3)},{-Subscript(a,1,2)*Subscript(a,3,1)+Subscript(a,\n"
            + "1,1)*Subscript(a,3,2),-Subscript(a,1,3)*Subscript(a,3,1)+Subscript(a,1,1)*Subscript(a,\n"
            + "3,3),-Subscript(a,1,3)*Subscript(a,3,2)+Subscript(a,1,2)*Subscript(a,3,3)},{-Subscript(a,\n"
            + "2,2)*Subscript(a,3,1)+Subscript(a,2,1)*Subscript(a,3,2),-Subscript(a,2,3)*Subscript(a,\n"
            + "3,1)+Subscript(a,2,1)*Subscript(a,3,3),-Subscript(a,2,3)*Subscript(a,3,2)+Subscript(a,\n"
            + "2,2)*Subscript(a,3,3)}}");

    check("m1 = Table(i^2 + i j + j^3, {i, 4}, {j, 4})", //
        "{{3,11,31,69},{7,16,37,76},{13,23,45,85},{21,32,55,96}}");
    check("Minors(m1)", //
        "{{-24,-84,-96,-36},{-72,-252,-288,-108},{-72,-252,-288,-108},{-24,-84,-96,-36}}");

    check("m2 = Partition(Range(16), 4)", //
        "{{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,16}}");
    check("Minors(m2)", //
        "{{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}}");
  }

  public void testNullSpace() {
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

  public void testOrthogonalize() {
    // TODO
    // check("Orthogonalize({{1, 2}, {3, 1}, {6, 9}, {7, 8}})", //
    // "{{1/Sqrt(5),2/Sqrt(5)},{2/Sqrt(5),-1/Sqrt(5)},{0,0},{0,0}}");

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

    check("Orthogonalize({{3,1},{2,2}})", //
        "{{3/Sqrt(10),1/Sqrt(10)},{-1/Sqrt(10),3/Sqrt(10)}}");
    check("Orthogonalize({{1,0,1},{1,1,1}})", //
        "{{1/Sqrt(2),0,1/Sqrt(2)},{0,1,0}}");
    check("Orthogonalize({{2,3}, {2,7}, {4,5}})", //
        "{{2/Sqrt(13),3/Sqrt(13)},{-3/Sqrt(13),2/Sqrt(13)},{0,0}}");
    check("Orthogonalize({{1,2,3},{5,2,7},{3,5,1}})", //
        "{{1/Sqrt(14),Sqrt(2/7),3/Sqrt(14)},{5/Sqrt(42),-2*Sqrt(2/21),1/Sqrt(42)},{1/Sqrt(\n"
            + "3),1/Sqrt(3),-1/Sqrt(3)}}");
    check("Orthogonalize({{1,0,0},{0,0,1}})", //
        "{{1,0,0},{0,0,1}}");
  }

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

  public void testOuter() {
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

  public void testPauliMatrix() {
    check("PauliMatrix(0)", //
        "{{1,0},{0,1}}");
    check("PauliMatrix(2)", //
        "{{0,-I},{I,0}}");
    check("Table(PauliMatrix(k), {k, 3})", //
        "{{{0,1},{1,0}},{{0,-I},{I,0}},{{1,0},{0,-1}}}");
  }

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
    check("PseudoInverse({{1, 2, 0}, {2, 3, 0}, {3, 4, 1}})", "{{-3.0,2.0,4.44089*10^-16},\n" + //
        " {2.0,-1.0,-2.77556*10^-16},\n" + //
        " {1.0,-2.0,1.0}}");
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
        "{{-0.638889,-0.166667,0.305556},\n" + //
            " {-0.0555556,-3.50414*10^-16,0.0555556},\n" + //
            " {0.527778,0.166667,-0.194444}}"); //
  }

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
    check("QRDecomposition({{1, 2}, {3, 4}, {5, 6}})", //
        "{\n" //
            + "{{-1/Sqrt(35),-3/Sqrt(35),-Sqrt(5/7)},\n" //
            + " {(827424+485264*Sqrt(6)-106080*Sqrt(35)-20280*Sqrt(210))/(-327600-285600*Sqrt(6)+\n" //
            + "223968*Sqrt(35)+63648*Sqrt(210)),(-270727082735471107200-133586801522901619200*Sqrt(\n" //
            + "6)+36505647383365776000*Sqrt(35)+11066026464301536000*Sqrt(210))/(1448857579225*(-\n" //
            + "408-78*Sqrt(6)+10*Sqrt(210))*(-327600-285600*Sqrt(6)+223968*Sqrt(35)+63648*Sqrt(\n" //
            + "210))),(67681770683867776800+33396700380725404800*Sqrt(6)-9126411845841444000*Sqrt(\n" //
            + "35)-2766506616075384000*Sqrt(210))/(289771515845*(-408-78*Sqrt(6)+10*Sqrt(210))*(-\n" //
            + "327600-285600*Sqrt(6)+223968*Sqrt(35)+63648*Sqrt(210)))},\n" //
            + " {(-285600-54600*Sqrt(6)+63648*Sqrt(35)+37328*Sqrt(210))/(-327600-285600*Sqrt(6)+\n" //
            + "223968*Sqrt(35)+63648*Sqrt(210)),(-13037731208344920000-3952152308679120000*Sqrt(\n" //
            + "6)+2762521252402766400*Sqrt(35)+1363130627784710400*Sqrt(210))/(29568522025*(-\n" //
            + "408-78*Sqrt(6)+10*Sqrt(210))*(-327600-285600*Sqrt(6)+223968*Sqrt(35)+63648*Sqrt(\n" //
            + "210))),(1303773120834492000+395215230867912000*Sqrt(6)-276252125240276640*Sqrt(\n" //
            + "35)-136313062778471040*Sqrt(210))/(5913704405*(-408-78*Sqrt(6)+10*Sqrt(210))*(-\n" //
            + "327600-285600*Sqrt(6)+223968*Sqrt(35)+63648*Sqrt(210)))}},\n" //
            + "{{-Sqrt(35),-44/Sqrt(35)},\n" //
            + " {0,2*Sqrt(6/35)},\n" //
        + " {0,0}}}");

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

  public void testRowReduce() {
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

  public void testSingularValueDecomposition() {
    check("SingularValueDecomposition({{1.5, 2.0}, {2.5, 3.0}})", //
        "{{{0.538954,0.842335},\n" + " {0.842335,-0.538954}},{{4.63555,0.0},\n"
            + " {0.0,0.107862}},{{0.628678,-0.777666},\n" + " {0.777666,0.628678}}}");
    check("SingularValueDecomposition({{3/2, 2}, {5/2, 3}})", //
        "{{{0.538954,0.842335},\n" + " {0.842335,-0.538954}},{{4.63555,0.0},\n"
            + " {0.0,0.107862}},{{0.628678,-0.777666},\n" + " {0.777666,0.628678}}}");
    check("SingularValueDecomposition({1, {2}})", //
        "SingularValueDecomposition({1,{2}})");
  }

  public void testSingularValueList() {
    check("m1 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}};", //
        "");
    check("Transpose(m1)", //
        "{{1.0,4.0,7.0,10.0},\n" //
            + " {2.0,5.0,8.0,11.0},\n" + " {3.0,6.0,9.0,12.0}}");
    check("ConjugateTranspose(m1)", //
        "{{1.0,4.0,7.0,10.0},\n" //
            + " {2.0,5.0,8.0,11.0},\n" + " {3.0,6.0,9.0,12.0}}");
    check("SingularValueList(m1)", //
        "{25.46241,1.29066}");
    check("Norm(m1)", //
        "25.46241");
    check("m2 = {{0.490394,0.0888901,0.536156,0.955578,0.695596},\n" //
        + " {0.0738935,0.098087,0.870086,0.71151,0.415052},\n"
        + " {0.205551,0.671352,0.4668,0.27744,0.369186},\n"
        + " {0.119547,0.575965,0.301828,0.320825,0.914003},\n"
        + " {0.949468,0.349907,0.666519,0.907584,0.273987}};", //
        "");
    check("SingularValueList(m2)", //
        "{2.56924,0.898176,0.629938,0.491696,0.0610678}");
    check("SingularValueList(m2,2)", //
        "{2.56924,0.898176}");
  }

  public void testSquareMatrixQ() {
    check("SquareMatrixQ({{1, 3 + 4*I}, {3 - 4*I, 2}})", //
        "True");
    check("SquareMatrixQ({{}})", //
        "False");
    check("SquareMatrixQ({{a,b,c}, {d,e,f}})", //
        "False");
  }

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

  public void testTensorSymmetry() {
    check("m = {{1, Log(x^2)}, {2*Log(x), 2}};", //
        "");
    check("TensorSymmetry(m)", //
        "{}");
    check("TensorSymmetry(m, SameTest->(Simplify(#1-#2, x>0)==0 &))", //
        "Symmetric({1,2})");
  }

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

  public void testTr() {
    // TODO calculate for levels
    // check(
    // "Tr(Array(a, {4, 3, 2}), f, 2)", //
    // "");
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

  public void testTranspose() {
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
    check("Transpose({{1, 2, 3}, {4, 5, 6}})", //
        "{{1,4},\n" + //
            " {2,5},\n" + //
            " {3,6}}");
  }

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

  public void testVectorQ() {

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
