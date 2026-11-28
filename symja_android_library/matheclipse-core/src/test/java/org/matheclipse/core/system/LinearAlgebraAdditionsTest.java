package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Tests for LDLDecomposition, JordanReduce, FrobeniusReduce, DrazinInverse, PfaffianDet, DiagonalizableMatrixQ, the Lyapunov solvers and the matrix and transformation constructors. */
public class LinearAlgebraAdditionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testLDLDecomposition() {
    check("LDLDecomposition({{9, 3, -6}, {3, 5, 2}, {-6, 2, 14}})", //
        "{{{1,0,0},{1/3,1,0},{-2/3,1,1}},\n{{9,0,0},\n {0,4,0},\n {0,0,6}}}");
    check("LDLDecomposition({{2, 4}, {4, 3}})", //
        "{{{1,0},{2,1}},\n{{2,0},\n {0,-5}}}");
    check("LDLDecomposition({{a, b}, {b, c}})", //
        "LDLDecomposition(\n{{a,b},\n {b,c}})");
    check("LDLDecomposition({{1, 2}, {3, 4}})", //
        "LDLDecomposition(\n{{1,2},\n {3,4}})");
  }

  @Test
  public void testModule() {
    check("Module({ld = LDLDecomposition({{9, 3, -6}, {3, 5, 2}, {-6, 2, 14}})}, ld[[1]] . ld[[2]] . Transpose(ld[[1]]))", //
        "{{9,3,-6},\n {3,5,2},\n {-6,2,14}}");
    check("Module({m = {{1, 1, 0}, {0, 0, 1}, {0, 0, 0}}, d}, d = DrazinInverse(m); {d . m . d == d, m . d == d . m, MatrixPower(m, 3) . d == MatrixPower(m, 2)})", //
        "{True,True,True}");
    check("Module({m = {{0, 2, -1, 4}, {-2, 0, 3, 1}, {1, -3, 0, 5}, {-4, -1, -5, 0}}}, PfaffianDet(m)^2 == Det(m))", //
        "True");
  }

  @Test
  public void testJordanReduce() {
    check("JordanReduce({{4, 1}, {0, 4}})", //
        "{{4,1},{0,4}}");
    check("JordanReduce({{3, 0, 0}, {0, 5, 0}, {0, 0, 1}})", //
        "{{5,0,0},{0,3,0},{0,0,1}}");
    check("JordanReduce({{2, 1, 0}, {0, 2, 0}, {0, 0, 2}})", //
        "{{2,0,0},{0,2,1},{0,0,2}}");
    check("JordanReduce({{1, 1, 0, 0}, {0, 1, 1, 0}, {0, 0, 1, 0}, {0, 0, 0, 7}})", //
        "{{1,1,0,0},{0,1,1,0},{0,0,1,0},{0,0,0,7}}");
    check("JordanReduce({{5, 4}, {1, 2}})", //
        "{{6,0},{0,1}}");
  }

  @Test
  public void testFrobeniusReduce() {
    check("FrobeniusReduce({{3, 1}, {0, 3}})", //
        "{{0,-9},{1,6}}");
    check("FrobeniusReduce({{4, 0}, {0, 7}})", //
        "{{0,-28},{1,11}}");
    check("FrobeniusReduce({{2, 0, 0}, {0, 2, 0}, {0, 0, 5}})", //
        "{{2,0,0},{0,0,-10},{0,1,7}}");
    check("FrobeniusReduce({{1, 2, 0}, {0, 1, 0}, {1, 0, 3}})", //
        "{{0,0,3},{1,0,-7},{0,1,5}}");
    check("FrobeniusReduce({{a, 1}, {0, a}})", //
        "{{0,-a^2},{1,2*a}}");
    // symbolic entries give the generic form
    check("FrobeniusReduce({{a,1,0},{0,a,0},{0,0,b}})", //
        "{{0,0,a^2*b},{1,0,-a^2-2*a*b},{0,1,2*a+b}}");
    check("FrobeniusReduce({{p,q},{r,s}})", //
        "{{0,q*r-p*s},{1,p+s}}");
    check("DiagonalizableMatrixQ({{a,1,0},{0,a,0},{0,0,b}})", //
        "False");
    check("DiagonalizableMatrixQ({{a,b},{c,d}})", //
        "True");
    // a Jordan block with a symbolic denominator
    check("DiagonalizableMatrixQ({{1/a,1},{0,1/a}})", //
        "False");
  }

  @Test
  public void testDrazinInverse() {
    check("DrazinInverse({{3, 1}, {2, 4}})", //
        "{{2/5,-1/10},\n {-1/5,3/10}}");
    check("DrazinInverse({{0, 2}, {0, 0}})", //
        "{{0,0},{0,0}}");
    check("DrazinInverse({{1, 1, 0}, {0, 0, 1}, {0, 0, 0}})", //
        "{{1,1,1},{0,0,0},{0,0,0}}");
    check("DrazinInverse({{2, 0}, {0, 0}})", //
        "{{1/2,0},{0,0}}");
  }

  @Test
  public void testPfaffianDet() {
    check("PfaffianDet({{0, 3}, {-3, 0}})", //
        "3");
    check("PfaffianDet({{0, 2, -1, 4}, {-2, 0, 3, 1}, {1, -3, 0, 5}, {-4, -1, -5, 0}})", //
        "23");
    check("PfaffianDet({{0, a, b, c}, {-a, 0, d, e}, {-b, -d, 0, f}, {-c, -e, -f, 0}})", //
        "c*d-b*e+a*f");
    check("PfaffianDet({{0, 1, 2}, {-1, 0, 3}, {-2, -3, 0}})", //
        "0");
    check("PfaffianDet({{1, 2}, {3, 4}})", //
        "PfaffianDet(\n{{1,2},\n {3,4}})");
  }

  @Test
  public void testDiagonalizableMatrixQ() {
    check("DiagonalizableMatrixQ({{3, 0}, {0, 5}})", //
        "True");
    check("DiagonalizableMatrixQ({{3, 1}, {0, 3}})", //
        "False");
    check("DiagonalizableMatrixQ({{0, -1}, {1, 0}})", //
        "True");
    check("DiagonalizableMatrixQ({{2, 1, 0}, {0, 2, 0}, {0, 0, 2}})", //
        "False");
    check("DiagonalizableMatrixQ({{1., 1.}, {0., 1.}})", //
        "False");
    check("DiagonalizableMatrixQ({{1, 2, 3}, {4, 5, 6}})", //
        "False");
  }

  @Test
  public void testLyapunovSolve() {
    // a singular equation: a particular solution if it is consistent
    check("LyapunovSolve({{1,0},{0,-1}},{{1,0},{0,1}})", //
        "{{1/2,0},{0,-1/2}}");
    check("LyapunovSolve({{0,0},{0,0}},{{1,0},{0,1}})", //
        "LyapunovSolve({{0,0},{0,0}},{{1,0},{0,1}})");
    check("LyapunovSolve({{2, 1}, {0, 4}}, {{3, 0}, {0, 2}})", //
        "{{37/48,-1/24},{-1/24,1/4}}");
    check("LyapunovSolve({{-3, 0}, {0, -1}}, {{6, 2}, {2, 2}})", //
        "{{-1,-1/2},{-1/2,-1}}");
    check("LyapunovSolve({{1, 0}, {0, 2}}, {{3, 1}, {0, 5}}, {{1, 1}, {1, 1}})", //
        "{{1/4,1/8},{1/5,4/35}}");
  }

  @Test
  public void testDiscreteLyapunovSolve() {
    check("DiscreteLyapunovSolve({{1/3, 0}, {0, 1/4}}, {{2, 0}, {0, 3}})", //
        "{{-9/4,0},{0,-16/5}}");
    check("DiscreteLyapunovSolve({{0, 1}, {-1/3, 0}}, {{1, 1}, {1, 2}})", //
        "{{-27/8,-3/4},{-3/4,-19/8}}");
    check("DiscreteLyapunovSolve({{1/2, 0}, {0, 1/5}}, {{1/3, 0}, {0, 1/2}}, {{1, 2}, {3, 4}})", //
        "{{-6/5,-8/3},{-45/14,-40/9}}");
  }

  @Test
  public void testBlockDiagonalMatrix() {
    check("BlockDiagonalMatrix({{{7}}, {{1, 2}, {3, 4}}})", //
        "{{7,0,0},{0,1,2},{0,3,4}}");
    check("BlockDiagonalMatrix({{{1, 2}}, {{3}, {4}}})", //
        "BlockDiagonalMatrix({{{1,2}},{{3},{4}}})");
  }

  @Test
  public void testLowerTriangularMatrix() {
    check("LowerTriangularMatrix({{1, 0}, {5, 2}})", //
        "{{1,0},{5,2}}");
    check("LowerTriangularMatrix({{1, 3}, {5, 2}})", //
        "LowerTriangularMatrix({{1,3},{5,2}})");
  }

  @Test
  public void testUpperTriangularMatrix() {
    check("UpperTriangularMatrix({{4, 1, 2}, {0, 5, 3}, {0, 0, 6}})", //
        "{{4,1,2},{0,5,3},{0,0,6}}");
  }

  @Test
  public void testCauchyMatrix() {
    check("CauchyMatrix({1, 3}, {2, 4, 6})", //
        "{{1/3,1/5,1/7},{1/5,1/7,1/9}}");
    check("CauchyMatrix({a, b})", //
        "{{1/(2*a),1/(a+b)},{1/(a+b),1/(2*b)}}");
  }

  @Test
  public void testSymmetrizedArray() {
    check("SymmetrizedArray({{1, 1} -> 5, {1, 2} -> 7, {2, 2} -> 9}, {2, 2}, Symmetric({1, 2}))", //
        "{{5,7},{7,9}}");
    check("SymmetrizedArray({{1, 2} -> a, {2, 3} -> b}, {3, 3}, Antisymmetric({1, 2}))", //
        "{{0,a,0},{-a,0,b},{0,-b,0}}");
    check("SymmetrizedArray({{1, 2, 3} -> 1}, {3, 3, 3}, Antisymmetric(All))", //
        "{{{0,0,0},{0,0,1},{0,-1,0}},{{0,0,-1},{0,0,0},{1,0,0}},{{0,1,0},{-1,0,0},{0,0,0}}}");
  }

  @Test
  public void testReflectionMatrix() {
    check("ReflectionMatrix({0, 1})", //
        "{{1,0},{0,-1}}");
    check("ReflectionMatrix({1, 2})", //
        "{{3/5,-4/5},{-4/5,-3/5}}");
    check("ReflectionMatrix({1, 0, 1})", //
        "{{0,0,-1},{0,1,0},{-1,0,0}}");
  }

  @Test
  public void testScalingMatrix() {
    check("ScalingMatrix({4, 5})", //
        "{{4,0},\n {0,5}}");
    check("ScalingMatrix(3, {1, 0})", //
        "{{3,0},{0,1}}");
    check("ScalingMatrix(2, {1, 1})", //
        "{{3/2,1/2},{1/2,3/2}}");
  }

  @Test
  public void testShearingMatrix() {
    check("ShearingMatrix(s, {1, 0}, {0, 1})", //
        "{{1,Tan(s)},{0,1}}");
    check("ShearingMatrix(Pi/4, {0, 1}, {1, 0})", //
        "{{1,0},{1,1}}");
  }

  @Test
  public void testRollPitchYawMatrix() {
    check("RollPitchYawMatrix({0, Pi/2, 0})", //
        "{{0,0,1},\n {0,1,0},\n {-1,0,0}}");
    check("RollPitchYawMatrix({Pi/3, 0, 0})", //
        "{{1/2,-Sqrt(3)/2,0},\n {Sqrt(3)/2,1/2,0},\n {0,0,1}}");
    check("RollPitchYawMatrix({0, 0, Pi/2}, {1, 2, 3})", //
        "{{0,-1,0},\n {1,0,0},\n {0,0,1}}");
    check("RollPitchYawMatrix({a, b, c}) // Dimensions", //
        "{3,3}");
  }

  @Test
  public void testTransformationMatrix() {
    check("TransformationMatrix(TranslationTransform({3, -1}))", //
        "{{1,0,3},{0,1,-1},{0,0,1}}");
    check("TransformationMatrix(ScalingTransform({2, 5}))", //
        "{{2,0,0},{0,5,0},{0,0,1}}");
    check("TransformationMatrix(x)", //
        "TransformationMatrix(x)");
    check("TransformationMatrix(RescalingTransform({{-1, 1}}))", //
        "{{1/2,1/2},{0,1}}");
  }

  @Test
  public void testRescalingTransform() {
    check("RescalingTransform({{0, 4}, {2, 6}})[{2, 3}]", //
        "{1/2,1/4}");
    check("RescalingTransform({{0, 1}, {0, 1}}, {{10, 20}, {-1, 1}})[{1/2, 1/4}]", //
        "{15,-1/2}");
  }

  @Test
  public void testLinearFractionalTransform() {
    check("LinearFractionalTransform({{{2, 1}, {0, 1}}, {1, 0}, {1, 0}, 1})[{1, 2}]", //
        "{5/2,1}");
    check("LinearFractionalTransform({{1, 0, 0}, {0, 1, 0}, {1, 1, 1}})[{2, 3}]", //
        "{1/3,1/2}");
  }

  @Test
  public void testFindGeometricTransform() {
    check("FindGeometricTransform({{2, 3}, {4, 5}, {6, 7}}, {{0, 0}, {2, 2}, {4, 4}}, TransformationClass -> \"Translation\")", //
        "{0,TransformationFunction({{1.0,0.0,2.0},{0.0,1.0,3.0},{0.0,0.0,1.0}})}");
    check("FindGeometricTransform({{0, 1}, {-1, 0}, {0, -1}}, {{1, 0}, {0, 1}, {-1, 0}}, TransformationClass -> \"Rigid\")", //
        "{0,TransformationFunction({{0.0,-1.0,0.0},{1.0,0.0,0.0},{0.0,0.0,1.0}})}");
    check("FindGeometricTransform({{1, 1}, {3, 1}, {1, 3}}, {{0, 0}, {1, 0}, {0, 1}})", //
        "{0,TransformationFunction({{2.0,0.0,1.0},{0.0,2.0,1.0},{0.0,0.0,1.0}})}");
    check("FindGeometricTransform({{0, 0}, {2, 0}, {2, 2}, {0, 2}}, {{0, 0}, {1, 0}, {1, 1}, {0, 1}})", //
        "{0,TransformationFunction({{2.0,0.0,0.0},{0.0,2.0,0.0},{0.0,0.0,1.0}})}");
  }

  @Test
  public void testNormalizedSquaredEuclideanDistance() {
    check("NormalizedSquaredEuclideanDistance({1, 2, 3}, {3, 2, 1})", //
        "1");
    check("NormalizedSquaredEuclideanDistance({1, 4, 2, 7}, {2, 1, 5, 3})", //
        "131/238");
    check("NormalizedSquaredEuclideanDistance({2, 5}, {4, 10})", //
        "1/10");
  }
}
