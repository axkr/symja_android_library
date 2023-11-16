package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

public class MatrixDTest extends ExprEvaluatorTestCase {

  @Test
  public void testMatrixD001() {

    // bad input 1
    check(
        "MatrixD(-2147483648,Slot(\"\"),{{0,0},2147483647},{x,-1/2,1/2,Graph({1->2,2->3,3->1})}, 7*19,DirectedInfinity({0,0,0}),(-2147483648)^2,2*{1,2,3})", //
        "MatrixD(-2147483648,Slot(),{{0,0},2147483647},{x,-1/2,1/2,Graph({1,2,3},{1->2,2->3,3->1})},\n"
            + "133,{ComplexInfinity,ComplexInfinity,ComplexInfinity},4611686018427387904,{2,4,6})");
    // bad input 2
    check(
        "MatrixD({1},SparseArray({{1,0},{0,1}})*Sqrt(5),2*(-3/2),OptionValue(OptionValue(x)),{{0,0},10007},OptionValue(-3/2),(3/2)[[1]],-I)", //
        "MatrixD({1},SparseArray(Number of elements: 2 Dimensions: {2,2} Default value: 0),-\n"
            + "3,OptionValue(OptionValue(x)),{{0,0},10007},OptionValue(-3/2),(3/2)[[1]],-I)");
    // bad input 3
    check(
        "MatrixD((Heads->False)*Pi,x[[{3/4}]],\"\",2+I,Null,-1,{{1,0},10007},ComplexInfinity,2-I)", //
        "MatrixD(Pi*(Heads->False),x[[{3/4}]],,2+I,Null,-1,{{1,0},10007},ComplexInfinity,\n"
            + "2-I)");
    check(
        "$Assumptions={Element(X, Matrices({n,n},Complexes)),Element(A, Matrices({n,n},Complexes)),Element(B, Matrices({4,4},Complexes))}", //
        "{X∈Matrices({n,n},Complexes),A∈Matrices({n,n},Complexes),B∈Matrices({4,4},Complexes)}");

    check("MatrixD(X,X)", //
        "$SingleEntryMatrix");
    check("MatrixD(A.X, X)", //
        "0.X+A.$SingleEntryMatrix");
    check("MatrixD(Det(MatrixPower(X, k)), X)", //
        "k*Det(MatrixPower(X,k))*Inverse(Transpose(X))");
    check("MatrixD(Inverse(X), X)", //
        "(-Inverse(X)).$SingleEntryMatrix.Inverse(X)");
  }

  @Test
  public void testMatrixDet001() {
    check(
        "$Assumptions={Element(X, Matrices({n,n},Complexes)),Element(A, Matrices({n,n},Complexes)),Element(B, Matrices({4,4},Complexes))}", //
        "{X∈Matrices({n,n},Complexes),A∈Matrices({n,n},Complexes),B∈Matrices({4,4},Complexes)}");

    check("TensorRank(X)", //
        "2");
    check("MatrixD(Transpose(X), X)", //
        "Transpose($SingleEntryMatrix)");
    check("MatrixD(Det(X), X)", //
        "Det(X)*Transpose(Inverse(X))");
    check("MatrixD(Det(A.X.B), X)", //
        "Det(A.X.B)*Transpose(Inverse(X))");
    check("MatrixD(Det(Inverse(X)),X)", //
        "-Det(Inverse(X))*Transpose(Inverse(X))");
    check("MatrixD(Log(Det(Transpose(X).X)), X)", // (55)
        "2*Inverse(Transpose(X))");
    check("MatrixD(Log @ Det(X), X)", // (57)
        "Inverse(Transpose(X))");
  }

  // @Test
  // public void testMatrixD002() {
  // check(
  // "$Assumptions={Element(X, Matrices({n,n},Complexes)),Element(A,
  // Matrices({n,n},Complexes)),Element(B, Matrices({4,4},Complexes))}", //
  // "{X∈Matrices({n,n},Complexes),A∈Matrices({n,n},Complexes),B∈Matrices({4,4},Complexes)}");
  // check(
  // "MatrixD(A, X)",
  // "0");
  // check(
  // "MatrixD(a*X, X)",
  // "$SingleEntryMatrix*a");
  // check(
  // "MatrixD(Transpose(X)*A*x + c*Transpose(sin(y))*X, X)",
  // "");
  // }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
