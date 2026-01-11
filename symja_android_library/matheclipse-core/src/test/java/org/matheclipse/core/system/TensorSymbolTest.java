package org.matheclipse.core.system;

import org.junit.Test;

/**
 * JUnit tests for Symbolic Tensors (MatrixSymbol, VectorSymbol, ArraySymbol) and related Tensor*
 * functions.
 */
public class TensorSymbolTest extends ExprEvaluatorTestCase {


  @Test
  public void testSymbolicIdentityArray() {
    check("Normal(SymbolicIdentityArray({3,2,1}))", //
        "{{{{{{1},{0}},{{0},{0}},{{0},{0}}}},{{{{0},{1}},{{0},{0}},{{0},{0}}}}},{{{{{0},{\n" //
            + "0}},{{1},{0}},{{0},{0}}}},{{{{0},{0}},{{0},{1}},{{0},{0}}}}},{{{{{0},{0}},{{0},{\n" //
            + "0}},{{1},{0}}}},{{{{0},{0}},{{0},{0}},{{0},{1}}}}}}");
    check("Normal(SymbolicIdentityArray({1,2,3}))", //
        "{{{{{{1,0,0},{0,0,0}}},{{{0,1,0},{0,0,0}}},{{{0,0,1},{0,0,0}}}},{{{{0,0,0},{1,0,\n" //
            + "0}}},{{{0,0,0},{0,1,0}}},{{{0,0,0},{0,0,1}}}}}}");
    check(" Normal(SymbolicIdentityArray({2,2,2}))", //
        "{{{{{{1,0},{0,0}},{{0,0},{0,0}}},{{{0,1},{0,0}},{{0,0},{0,0}}}},{{{{0,0},{1,0}},{{\n" //
            + "0,0},{0,0}}},{{{0,0},{0,1}},{{0,0},{0,0}}}}},{{{{{0,0},{0,0}},{{1,0},{0,0}}},{{{\n" //
            + "0,0},{0,0}},{{0,1},{0,0}}}},{{{{0,0},{0,0}},{{0,0},{1,0}}},{{{0,0},{0,0}},{{0,0},{\n" //
            + "0,1}}}}}}");
    check("Normal(SymbolicIdentityArray({2,2}))", //
        "{{{{1,0},{0,0}},{{0,1},{0,0}}},{{{0,0},{1,0}},{{0,0},{0,1}}}}");
    check(" Normal(SymbolicIdentityArray({3,2}))", //
        "{{{{1,0},{0,0},{0,0}},{{0,1},{0,0},{0,0}}},{{{0,0},{1,0},{0,0}},{{0,0},{0,1},{0,\n" //
            + "0}}},{{{0,0},{0,0},{1,0}},{{0,0},{0,0},{0,1}}}}");
  }


  @Test
  public void testVectorSymbol() {
    check("VectorSymbol(v, 3)", //
        "VectorSymbol(v, 3)");
    check("VectorSymbol(x, n, Complexes)", //
        "VectorSymbol(x, n, Complexes)");

    check("TensorRank(VectorSymbol(v, 3))", //
        "1");
    check("TensorDimensions(VectorSymbol(v, 3))", //
        "{3}");
    check("TensorDimensions(VectorSymbol(v, n))", //
        "{n}");
    check("TensorSymmetry(VectorSymbol(v, n))", //
        "{}");
  }

  @Test
  public void testMatrixSymbol() {
    check("m=MatrixSymbol(\"m\", {2, 2})", //
        "MatrixSymbol(m, {2,2})");
    check("MatrixSymbol(\"n\", {2, 3}, Reals)", //
        "MatrixSymbol(n, {2,3})");


    check("TensorRank(MatrixSymbol(m, {2, 2}))", //
        "2");
    check("TensorDimensions(MatrixSymbol(a, {b,c}))", //
        "{b,c}");


    check("TensorSymmetry(MatrixSymbol(a, {3,3}))", //
        "{}");

  }

  @Test
  public void testMatrixSymbolD() {
    check("m=MatrixSymbol(\"m\", {2, 2})", //
        "MatrixSymbol(m, {2,2})");
    check("D(SymbolicZerosArray({b,c}), x)", //
        "SymbolicZerosArray({b,c})");
    check("D(SymbolicOnesArray({b,c}), x)", //
        "SymbolicZerosArray({b,c})");
    check("D(m, m)", //
        "SymbolicIdentityArray({2,2})");
    check("3*D(m, m)", //
        "3*SymbolicIdentityArray({2,2})");
    check("D(f(x), m)", //
        "SymbolicZerosArray({2,2})");
  }

  @Test
  public void testArraySymbol() {
    check("ArraySymbol(a, {2, 3, 4})", //
        "ArraySymbol(a, {2,3,4})");
    check("TensorRank(ArraySymbol(a, {n1, n2, n3}))", //
        "3");
    check("TensorDimensions(ArraySymbol(a, {2, 2, 2}))", //
        "{2,2,2}");
  }

  @Test
  public void testTensorContract() {
    // Trace of a MatrixSymbol
    check("TensorContract(MatrixSymbol(m, {3,3}), {1, 2})", //
        "Tr(MatrixSymbol(m, {3,3}))");

    // Invalid Contraction (Vector) - Should return unevaluated
    check("TensorContract(VectorSymbol(v, 3), {1, 2})", "TensorContract(VectorSymbol(v, 3),{1,2})");
  }

  @Test
  public void testTensorProduct() {
    check("TensorProduct(I)", //
        "I");
    // Scalar extraction
    // TensorProduct[v, 5*m] -> 5 * TensorProduct[v, m]
    // Note: Exact string match depends on Times ordering, usually scalars first.
    check("TensorProduct(VectorSymbol(v, 3), 5 * MatrixSymbol(m, {3,3})) // FullForm",
        "Times(5, TensorProduct(VectorSymbol(v, 3), MatrixSymbol(m, List(3, 3))))");

    // Identity (Single argument)
    check("TensorProduct(VectorSymbol(v, 3))", //
        "VectorSymbol(v, 3)");

    // Flattening
    check(
        "TensorProduct(VectorSymbol(a, n), TensorProduct(VectorSymbol(b, n), VectorSymbol(c, n)))// FullForm", //
        "TensorProduct(VectorSymbol(a, n), VectorSymbol(b, n), VectorSymbol(c, n))");
  }

  @Test
  public void testTensorTranspose() {
    check("TensorTranspose(MatrixSymbol(m, {3,3}, Reals, Symmetric({1,2})), {1,2})",
        "MatrixSymbol(m, {3,3}, Reals, Symmetric({1,2}))");
    check("TensorTranspose(MatrixSymbol(m, {3,3}, Reals, Symmetric({1,2})), {2, 1})",
        "TensorTranspose(MatrixSymbol(m, {3,3}, Reals, Symmetric({1,2})),{2,1})");
    check("TensorTranspose(MatrixSymbol(m, {2,2}), {1, 2})", //
        "MatrixSymbol(m, {2,2})");

    // Transpose of a general matrix remains a TensorTranspose expression
    check("TensorTranspose(MatrixSymbol(m, {3,3}), {2, 1})",
        "TensorTranspose(MatrixSymbol(m, {3,3}),{2,1})");
  }
}
