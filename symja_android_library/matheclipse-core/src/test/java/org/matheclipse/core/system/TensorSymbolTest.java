package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

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
    check("VectorSymbol(v,3)", //
        "VectorSymbol(v,3)");
    check("VectorSymbol(x,n)", //
        "VectorSymbol(x,n)");

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
        "MatrixSymbol(m,{2,2})");
    check("MatrixSymbol(\"n\", {2, 3}, Reals)", //
        "MatrixSymbol(n,{2,3},Reals)");


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
        "MatrixSymbol(m,{2,2})");
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
        "ArraySymbol(a,{2,3,4})");
    check("TensorRank(ArraySymbol(a, {n1, n2, n3}))", //
        "3");
    check("TensorDimensions(ArraySymbol(a, {2, 2, 2}))", //
        "{2,2,2}");
  }

  @Test
  public void testTensorContract() {
    // Trace of a MatrixSymbol
    check("TensorContract(MatrixSymbol(m, {3,3}), {1, 2})", //
        "Tr(MatrixSymbol(m,{3,3}))");

    // Invalid Contraction (Vector) - Should return unevaluated
    check("TensorContract(VectorSymbol(v, 3), {1, 2})", "TensorContract(VectorSymbol(v,3),{1,2})");
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
        "VectorSymbol(v,3)");

    // Flattening
    check(
        "TensorProduct(VectorSymbol(a, n), TensorProduct(VectorSymbol(b, n), VectorSymbol(c, n)))// FullForm", //
        "TensorProduct(VectorSymbol(a, n), VectorSymbol(b, n), VectorSymbol(c, n))");
  }

  @Test
  public void testTensorTranspose() {
    check("TensorTranspose(MatrixSymbol(m, {3,3}, Reals, Symmetric({1,2})), {1,2})",
        "MatrixSymbol(m,{3,3},Reals,Symmetric({1,2}))");
    check("TensorTranspose(MatrixSymbol(m, {3,3}, Reals, Symmetric({1,2})), {2, 1})",
        "TensorTranspose(MatrixSymbol(m,{3,3},Reals,Symmetric({1,2})),{2,1})");
    check("TensorTranspose(MatrixSymbol(m, {2,2}), {1, 2})", //
        "MatrixSymbol(m,{2,2})");

    // Transpose of a general matrix remains a TensorTranspose expression
    check("TensorTranspose(MatrixSymbol(m, {3,3}), {2, 1})",
        "TensorTranspose(MatrixSymbol(m,{3,3}),{2,1})");
  }

  @Test
  public void testArraySymbolObjects() {
    // Complexes is the default element domain and is omitted from the printed form
    check("MatrixSymbol(a, {2,2})", //
        "MatrixSymbol(a,{2,2})");
    check("MatrixSymbol(a, {2,2}, Complexes)", //
        "MatrixSymbol(a,{2,2})");
    check("MatrixSymbol(a, {2,2}, Reals)", //
        "MatrixSymbol(a,{2,2},Reals)");
    check("VectorSymbol(v, n, Reals)", //
        "VectorSymbol(v,n,Reals)");
    check("ArraySymbol(a, {2,3}, Reals, Symmetric({1,2}))", //
        "ArraySymbol(a,{2,3},Reals,Symmetric({1,2}))",
        "ArraySymbol: Symmetric({1,2}) is not a valid symmetry specification for ArraySymbol.");

    check("MatrixSymbol(a,{2,2}) === MatrixSymbol(a,{2,2},Complexes)", //
        "True");
    check("MatrixSymbol(a,{2,2}) === MatrixSymbol(a,{2,2},Reals)", //
        "False");
    check("FullForm(MatrixSymbol(a,{2,2}))", //
        "MatrixSymbol(a, List(2, 2))");
    check("InputForm(MatrixSymbol(a,{2,2},Reals))", //
        "MatrixSymbol(a,{2,2},Reals)");
    check("TeXForm(MatrixSymbol(a,{2,2}))", //
        "\\mathbf{a}");
    check("TeXForm(VectorSymbol(\"v\", n))", //
        "\\mathbf{v}");

    // DataExpr#compareTo() cannot order two objects which carry no data object, so the canonical
    // order of an S.Orderless expression needs AbstractArraySymbolExpr#compareTo()
    check("Sort({MatrixSymbol(b,{2,2}), MatrixSymbol(a,{2,2})})", //
        "{MatrixSymbol(a,{2,2}),MatrixSymbol(b,{2,2})}");
  }

  @Test
  public void testArraySymbolMessages() {
    check("MatrixSymbol(a, {2,2,3})", //
        "MatrixSymbol(a,{2,2,3})", //
        "MatrixSymbol: The list {2,2,3} of dimensions for a matrix must have length 2.");
    check("MatrixSymbol(a, {0,2})", //
        "MatrixSymbol(a,{0,2})", //
        "MatrixSymbol: Invalid dimension specification 0.");
    check("VectorSymbol(v, 0)", //
        "VectorSymbol(v,0)", //
        "VectorSymbol: Invalid dimension specification 0.");
    check("VectorSymbol(v, -3)", //
        "VectorSymbol(v,-3)", //
        "VectorSymbol: Invalid dimension specification -3.");
    // a symmetry can only permute slots of equal dimension, so the matrix has to be square
    check("MatrixSymbol(a, {2,3}, Reals, Symmetric({1,2}))", //
        "MatrixSymbol(a,{2,3},Reals,Symmetric({1,2}))", //
        "MatrixSymbol: Symmetric({1,2}) is not a valid symmetry specification for MatrixSymbol.");
    check("MatrixSymbol(a, {2,2}, Booleans)", //
        "MatrixSymbol(a,{2,2},Booleans)", //
        "MatrixSymbol: Booleans is not a valid domain specification for MatrixSymbol.");
    check("ArraySymbol(a, {})", //
        "ArraySymbol(a,{})", //
        "ArraySymbol: The list {} of dimensions for an array must have length 1.");
  }

  @Test
  public void testNonThreadableAttribute() {
    check("Attributes(MatrixSymbol)", //
        "{NonThreadable,Protected}");
    check("Attributes(VectorSymbol)", //
        "{NonThreadable,Protected}");
    check("Attributes(ArraySymbol)", //
        "{NonThreadable,Protected}");
  }

  @Test
  public void testSymbolicDot() {
    check("a=MatrixSymbol(a,{m,n}); b=MatrixSymbol(b,{n,p}); v=VectorSymbol(v,n);", //
        "");
    // SymbolicIdentityArray({n}) is the n x n identity matrix
    check("SymbolicIdentityArray({m}).a", //
        "MatrixSymbol(a,{m,n})");
    check("a.SymbolicIdentityArray({n})", //
        "MatrixSymbol(a,{m,n})");
    check("SymbolicZerosArray({m,n,p}).MatrixSymbol(c,{p,r})", //
        "SymbolicZerosArray({m,n,r})");
    check("a.b", //
        "MatrixSymbol(a,{m,n}).MatrixSymbol(b,{n,p})");
    check("TensorDimensions(a.b)", //
        "{m,p}");
    check("TensorDimensions(a.v)", //
        "{m}");
    check("TensorRank(a.v)", //
        "1");
    check("MatrixSymbol(c,{2,3}).MatrixSymbol(d,{2,3})", //
        "MatrixSymbol(c,{2,3}).MatrixSymbol(d,{2,3})", //
        "Dot: Dot contraction of MatrixSymbol(c,{2,3}) and MatrixSymbol(d,{2,3}) is invalid "
            + "because dimensions 3 and 2 are incompatible.");
  }

  @Test
  public void testSymbolicDimensions() {
    check("a=MatrixSymbol(a,{m,n}); s=MatrixSymbol(s,{n,n}); v=VectorSymbol(v,n);", //
        "");
    check("TensorDimensions(a+a)", //
        "{m,n}");
    check("TensorDimensions(2*a)", //
        "{m,n}");
    check("TensorDimensions(Sin(a))", //
        "{m,n}");
    check("TensorDimensions(Transpose(a))", //
        "{n,m}");
    check("TensorDimensions(Inverse(s))", //
        "{n,n}");
    check("TensorDimensions(TensorProduct(a,v))", //
        "{m,n,n}");
    check("TensorDimensions(Tr(s))", //
        "{}");
    // the identity array of {n1,...,nk} is the n1 x ... x nk x n1 x ... x nk array, which is the
    // shape Normal() builds as well
    check("TensorDimensions(SymbolicIdentityArray({2,2}))", //
        "{2,2,2,2}");
    check("TensorRank(SymbolicIdentityArray({2,2}))", //
        "4");
    check("Dimensions(Normal(SymbolicIdentityArray({2,3})))", //
        "{2,3,2,3}");
    check("TensorDimensions(x)", //
        "TensorDimensions(x)");
  }

  @Test
  public void testSymbolicTranspose() {
    check("a=MatrixSymbol(a,{m,n});", //
        "");
    check("Transpose(Transpose(a))", //
        "MatrixSymbol(a,{m,n})");
    check("Transpose(SymbolicZerosArray({m,n}))", //
        "SymbolicZerosArray({n,m})");
    check("Transpose(SymbolicOnesArray({m,n}))", //
        "SymbolicOnesArray({n,m})");
    check("Transpose(SymbolicIdentityArray({n}))", //
        "SymbolicIdentityArray({n})");
    check("Transpose(MatrixSymbol(q,{n,n},Reals,Symmetric({1,2})))", //
        "MatrixSymbol(q,{n,n},Reals,Symmetric({1,2}))");
    check("Transpose(MatrixSymbol(t,{n,n},Reals,Antisymmetric({1,2})))", //
        "-MatrixSymbol(t,{n,n},Reals,Antisymmetric({1,2}))");
    // conjugating a real array does nothing, so its conjugate transpose is its transpose
    check("ConjugateTranspose(MatrixSymbol(r,{m,n},Reals))", //
        "MatrixSymbol(r,{m,n},Reals)");
    check("Conjugate(MatrixSymbol(r,{m,n},Reals))", //
        "MatrixSymbol(r,{m,n},Reals)");
    check("Conjugate(a)", //
        "Conjugate(MatrixSymbol(a,{m,n}))");
    check("Conjugate(SymbolicOnesArray({m,n}))", //
        "SymbolicOnesArray({m,n})");
  }

  @Test
  public void testSymbolicMatrixFunctions() {
    check("s=MatrixSymbol(s,{n,n});", //
        "");
    check("Inverse(Inverse(s))", //
        "MatrixSymbol(s,{n,n})");
    check("Inverse(SymbolicIdentityArray({n}))", //
        "SymbolicIdentityArray({n})");
    check("Det(SymbolicIdentityArray({n}))", //
        "1");
    check("Tr(SymbolicIdentityArray({n}))", //
        "n");
    check("Tr(SymbolicOnesArray({n,n}))", //
        "n");
    check("Tr(SymbolicZerosArray({n,n}))", //
        "0");
    check("MatrixPower(s, 0)", //
        "SymbolicIdentityArray({n})");
    check("MatrixPower(s, 1)", //
        "MatrixSymbol(s,{n,n})");
    check("MatrixPower(s, 3)", //
        "MatrixPower(MatrixSymbol(s,{n,n}),3)");
    // only a square matrix can be inverted, and a symbolic matrix knows its own shape
    check("Inverse(MatrixSymbol(c,{2,3}))", //
        "Inverse(MatrixSymbol(c,{2,3}))", //
        "Inverse: Argument MatrixSymbol(c,{2,3}) at position 1 is not a non-empty square matrix.");
    check("Det(MatrixSymbol(c,{2,3}))", //
        "Det(MatrixSymbol(c,{2,3}))", //
        "Det: Argument MatrixSymbol(c,{2,3}) at position 1 is not a non-empty square matrix.");
  }

  @Test
  public void testSymbolicTensorProductArrayDot() {
    check("a=MatrixSymbol(a,{m,n});", //
        "");
    check("TensorProduct(SymbolicZerosArray({m,n}), a)", //
        "SymbolicZerosArray({m,n,m,n})");
    check("TensorProduct(MatrixSymbol(k,{k1,l}), SymbolicZerosArray({m,n,p}))", //
        "SymbolicZerosArray({k1,l,m,n,p})");
    check("TensorProduct(SymbolicOnesArray({m,n}), SymbolicOnesArray({p,q,r}))", //
        "SymbolicOnesArray({m,n,p,q,r})");
    check("ArrayDot(ArraySymbol(t,{m,p,q,r}), SymbolicIdentityArray({p,q,r}), 3)", //
        "ArraySymbol(t,{m,p,q,r})");
    check("ArrayDot(SymbolicIdentityArray({m,n}), ArraySymbol(t,{m,n,p}), 2)", //
        "ArraySymbol(t,{m,n,p})");
    check("ArrayDot(ArraySymbol(t,{k1,l,m,n}), SymbolicZerosArray({m,n,p}), 2)", //
        "SymbolicZerosArray({k1,l,p})");
    check("ArrayDot(a, MatrixSymbol(b,{n,p}), 1)", //
        "MatrixSymbol(a,{m,n}).MatrixSymbol(b,{n,p})");
  }

  @Test
  public void testSymbolicArrayNormal() {
    check("Normal(SymbolicZerosArray({2,2,2}))", //
        "{{{0,0},{0,0}},{{0,0},{0,0}}}");
    check("Normal(SymbolicOnesArray({2,3}))", //
        "{{1,1,1},{1,1,1}}");
    check("Normal(SymbolicDeltaProductArray({2,2,2,2},{{1,4},{2,3}}))", //
        "{{{{1,0},{0,0}},{{0,0},{1,0}}},{{{0,1},{0,0}},{{0,0},{0,1}}}}");
    check("IdentityMatrix(3) == Normal(SymbolicIdentityArray({3}))", //
        "True");
    // symbolic dimensions cannot be written out
    check("Normal(SymbolicZerosArray({m,n}))", //
        "SymbolicZerosArray({m,n})");
    check("Normal(SparseArray(SymbolicIdentityArray({3})))", //
        "{{1,0,0},{0,1,0},{0,0,1}}");
    check("Normal(SparseArray(SymbolicOnesArray({2,2})))", //
        "{{1,1},{1,1}}");
  }

  @Test
  public void testSymbolicArrayArithmetic() {
    check("a=MatrixSymbol(a,{m,n}); b=MatrixSymbol(b,{m,n});", //
        "");
    // a - a is the zero array of the same shape, not the scalar 0
    check("a - a", //
        "SymbolicZerosArray({m,n})");
    check("0*a", //
        "SymbolicZerosArray({m,n})");
    check("a - a + 1", //
        "SymbolicOnesArray({m,n})");
    check("SymbolicZerosArray({m,n}) + 1", //
        "SymbolicOnesArray({m,n})");
    check("SymbolicOnesArray({m,n}) - 1", //
        "SymbolicZerosArray({m,n})");
    check("SymbolicZerosArray({m,n}) + a", //
        "MatrixSymbol(a,{m,n})");
    check("SymbolicZerosArray({m,n}) * a", //
        "SymbolicZerosArray({m,n})");
    check("SymbolicOnesArray({m,n}) * a", //
        "MatrixSymbol(a,{m,n})");
    check("SymbolicOnesArray({m,n})^7", //
        "SymbolicOnesArray({m,n})");
    check("SymbolicIdentityArray({m,n})^7", //
        "SymbolicIdentityArray({m,n})");
    check("2*a + 3*a", //
        "5*MatrixSymbol(a,{m,n})");
    check("a + b", //
        "MatrixSymbol(a,{m,n})+MatrixSymbol(b,{m,n})");
    // a summand of unknown rank may be an array of its own, so it is not folded into the array
    check("SymbolicOnesArray({m,n}) + x", //
        "x+SymbolicOnesArray({m,n})");
  }

  @Test
  public void testNonThreadable() {
    check("a=MatrixSymbol(a,{m,n}); b=MatrixSymbol(b,{m,n});", //
        "");
    // a symbolic array is not combined with the elements of an explicit list
    check("a + b + {1,2}", //
        "{1,2}+MatrixSymbol(a,{m,n})+MatrixSymbol(b,{m,n})");
    check("a + {1,2}", //
        "{1,2}+MatrixSymbol(a,{m,n})");
    check("Sin(a) + {1,2}", //
        "{1,2}+Sin(MatrixSymbol(a,{m,n}))");
    check("Attributes(Dot)", //
        "{Flat,NonThreadable,OneIdentity,Protected}");
    check("Attributes(Transpose)", //
        "{NonThreadable,Protected}");
    // the attribute can be set on any symbol
    check("SetAttributes(nonScalar, NonThreadable); nonScalar(1) + {1,2}", //
        "{1,2}+nonscalar(1)");
    check("ClearAttributes(nonScalar, NonThreadable); nonScalar(1) + {1,2}", //
        "{1+nonscalar(1),2+nonscalar(1)}");
    // ordinary list arithmetic is unaffected
    check("{1,2}+{3,4}", //
        "{4,6}");
    check("Sin({1,2})", //
        "{Sin(1),Sin(2)}");
  }

  @Test
  public void testArrayDerivative() {
    check("a=MatrixSymbol(a,{m,n}); s=MatrixSymbol(s,{n,n}); u=VectorSymbol(u,n); "
        + "v=VectorSymbol(v,n);", //
        "");
    check("D(v, v)", //
        "SymbolicIdentityArray({n})");
    check("D(v.v, v)", //
        "2*VectorSymbol(v,n)");
    check("D(u.v, v)", //
        "VectorSymbol(u,n)");
    check("D(a.v, v)", //
        "MatrixSymbol(a,{m,n})");
    //  is the postfix Transpose operator
    check("D(v.s, v)", //
        "MatrixSymbol(s,{n,n})");
    check("D(v.s.v, v)", //
        "MatrixSymbol(s,{n,n}).VectorSymbol(v,n)+MatrixSymbol(s,{n,n}).VectorSymbol(v,n)");
    check("D(3*v.v + 1, v)", //
        "6*VectorSymbol(v,n)");
    check("D(Total(v), v)", //
        "SymbolicOnesArray({n})");
    check("D(Mean(v), v)", //
        "SymbolicOnesArray({n})/n");
    check("D(Tr(s), s)", //
        "SymbolicIdentityArray({n})");
    check("D(Det(s), s)", //
        "Det(MatrixSymbol(s,{n,n}))*Inverse(MatrixSymbol(s,{n,n}))");
    check("D(Transpose(a), a)", //
        "Transpose(SymbolicIdentityArray({m,n}),{2,1,3,4})");
    check("D(Norm(VectorSymbol(w,n,Reals)), VectorSymbol(w,n,Reals))", //
        "VectorSymbol(w,n,Reals)/Norm(VectorSymbol(w,n,Reals))");
    // the derivative of a constant has the shape of the function followed by that of the variable
    check("D(a.MatrixSymbol(b,{n,p}), x)", //
        "SymbolicZerosArray({m,p})");
    check("D(SymbolicOnesArray({b,c}), MatrixSymbol(mm,{2,2}))", //
        "SymbolicZerosArray({b,c,2,2})");
    // an unsupported derivative stays unevaluated instead of answering a wrong scalar
    check("D(Sin(v), v)", //
        "D(Sin(VectorSymbol(v,n)),VectorSymbol(v,n))");
    // an expression which mentions no symbolic array is read as a scalar
    check("D(f(x), MatrixSymbol(mm,{2,2}))", //
        "SymbolicZerosArray({2,2})");
  }

  @Test
  public void testArrayValuedDerivativeInScalar() {
    // the product rule of a Dot has to keep the order of its factors
    check("D(f(x).g(x), x)", //
        "f(x).g'(x)+f'(x).g(x)");
    check("D(Inverse(f(x)), x)", //
        "-Inverse(f(x)).f'(x).Inverse(f(x))");
    check("D(Tr(f(x)), x)", //
        "Tr(f'(x))");
    check("D(Det(f(x)), x)", //
        "Det(f(x))*Tr(Inverse(f(x)).f'(x))");
  }

  @Test
  public void testElementArrayDomains() {
    // the component domain defaults to Complexes
    check("Matrices({2,3})", //
        "Matrices({2,3},Complexes)");
    check("Vectors(3)", //
        "Vectors(3,Complexes)");
    check("Matrices({2,3,4})", //
        "Matrices({2,3,4})", //
        "Matrices: The list {2,3,4} of dimensions for a matrix must have length 2.");

    check("Element(MatrixSymbol(a,{2,3}), Matrices({2,3}))", //
        "True");
    check("Element(MatrixSymbol(a,{2,3}), Matrices({3,3}))", //
        "False");
    check("Element(MatrixSymbol(a,{2,3},Reals), Matrices({2,3},Reals))", //
        "True");
    // a complex valued matrix is not known to be real
    check("Element(MatrixSymbol(a,{2,3}), Matrices({2,3},Reals))", //
        "MatrixSymbol(a,{2,3})∈Matrices({2,3},Reals)");
    check("Element(VectorSymbol(v,3), Vectors(3))", //
        "True");
    check("Element(SymbolicIdentityArray({3}), Matrices({3,3},Integers))", //
        "True");
    check("Element({1,Pi,I}, Vectors(3,Complexes))", //
        "True");
    check("Element({1,Pi,I}, Vectors(3,Reals))", //
        "False");
    check("Element({1,Pi}, Vectors(3))", //
        "False");
    check("Element(Indexed(MatrixSymbol(a,{2,2},Reals),{1,2}), Reals)", //
        "True");
    check("Element(Indexed(MatrixSymbol(a,{2,2}),{1,2}), Reals)", //
        "Indexed(MatrixSymbol(a,{2,2}),{1,2})∈Reals");
  }

  @Test
  public void testIndexedSymbolicArray() {
    check("Indexed(SymbolicIdentityArray({n}),{i,j})", //
        "KroneckerDelta(i,j)");
    check("Indexed(SymbolicZerosArray({n,n}),{i,j})", //
        "0");
    check("Indexed(SymbolicOnesArray({n,n}),{i,j})", //
        "1");
    check("Indexed(SymbolicDeltaProductArray({2,2,2,2},{{1,4},{2,3}}),{i,j,k,l})", //
        "KroneckerDelta(i,l)*KroneckerDelta(j,k)");
    // a component of a symbolic array object is the component representation itself
    check("Indexed(MatrixSymbol(a,{2,2}),{1,2})", //
        "Indexed(MatrixSymbol(a,{2,2}),{1,2})");
  }

  @Test
  public void testTensorSymmetryAssumption() {
    // the symmetry is part of the domain and must survive being stored as an assumption
    check("$Assumptions = Element(m, Matrices({4,4}, Reals, Symmetric({1,2})));", //
        "");
    check("TensorSymmetry(m)", //
        "Symmetric({1,2})");
    check("TensorRank(m)", //
        "2");
    check("$Assumptions = Element(q, Matrices({4,4}, Reals));", //
        "");
    check("TensorSymmetry(q)", //
        "{}");
    check("$Assumptions=True;", //
        "");
  }

  @Test
  public void testArraySimplify() {
    check("s=MatrixSymbol(s,{n,n}); t=MatrixSymbol(t,{n,n}); w=VectorSymbol(w,n);", //
        "");
    check("ArraySimplify(Transpose(Transpose(s)))", //
        "MatrixSymbol(s,{n,n})");
    check("ArraySimplify(Inverse(Inverse(s)))", //
        "MatrixSymbol(s,{n,n})");
    check("ArraySimplify(Det(2*s)/Det(s))", //
        "2^n");
    check("ArraySimplify(Det(Transpose(s)))", //
        "Det(MatrixSymbol(s,{n,n}))");
    check("ArraySimplify(Det(Inverse(s)))", //
        "1/Det(MatrixSymbol(s,{n,n}))");
    check("ArraySimplify(Det(s.t))", //
        "Det(MatrixSymbol(s,{n,n}))*Det(MatrixSymbol(t,{n,n}))");
    check("ArraySimplify(Tr(Transpose(s)))", //
        "Tr(MatrixSymbol(s,{n,n}))");
    check("ArraySimplify(Tr(s+t))", //
        "Tr(MatrixSymbol(s,{n,n}))+Tr(MatrixSymbol(t,{n,n}))");
    check("ArraySimplify(Tr(3*s))", //
        "3*Tr(MatrixSymbol(s,{n,n}))");
    check("ArraySimplify(s.Inverse(s))", //
        "SymbolicIdentityArray({n})");
    check("ArraySimplify(s.SymbolicIdentityArray({n}).t)", //
        "MatrixSymbol(s,{n,n}).MatrixSymbol(t,{n,n})");
    // a vector valued product is written in the column form
    check("ArraySimplify(w.Transpose(s))", //
        "MatrixSymbol(s,{n,n}).VectorSymbol(w,n)");
    check("ArraySimplify(s.(2*t))", //
        "2*MatrixSymbol(s,{n,n}).MatrixSymbol(t,{n,n})");
  }

  @Test
  public void testArrayExpand() {
    check("s=MatrixSymbol(s,{n,n}); t=MatrixSymbol(t,{n,n}); u=MatrixSymbol(u,{n,n}); "
        + "w=VectorSymbol(w,n);", //
        "");
    check("ArrayExpand(s.(t+u))", //
        "MatrixSymbol(s,{n,n}).MatrixSymbol(t,{n,n})+MatrixSymbol(s,{n,n}).MatrixSymbol(u,{n,n})");
    check("ArrayExpand((s+t).w)", //
        "MatrixSymbol(s,{n,n}).VectorSymbol(w,n)+MatrixSymbol(t,{n,n}).VectorSymbol(w,n)");
    check("ArrayExpand(Inverse(s.t))", //
        "Inverse(MatrixSymbol(t,{n,n})).Inverse(MatrixSymbol(s,{n,n}))");
  }

  @Test
  public void testComponentExpand() {
    check("ComponentExpand(VectorSymbol(vname,3))", //
        "{Indexed(vname,{1}),Indexed(vname,{2}),Indexed(vname,{3})}");
    check("ComponentExpand(MatrixSymbol(g,{2,2}))", //
        "{{Indexed(g,{1,1}),Indexed(g,{1,2})},{Indexed(g,{2,1}),Indexed(g,{2,2})}}");
    check("ComponentExpand(Det(MatrixSymbol(g,{2,2})))", //
        "-Indexed(g,{1,2})*Indexed(g,{2,1})+Indexed(g,{1,1})*Indexed(g,{2,2})");
    check("ComponentExpand(SymbolicIdentityArray({2,2}))", //
        "{{{{1,0},{0,0}},{{0,1},{0,0}}},{{{0,0},{1,0}},{{0,0},{0,1}}}}");
    // a declared symmetry names only the sorted components
    check("ComponentExpand(MatrixSymbol(q,{2,2},Reals,Symmetric({1,2})))", //
        "{{Indexed(q,{1,1}),Indexed(q,{1,2})},{Indexed(q,{1,2}),Indexed(q,{2,2})}}");
    check("ComponentExpand(MatrixSymbol(q,{2,2},Reals,Antisymmetric({1,2})))", //
        "{{0,Indexed(q,{1,2})},{-Indexed(q,{1,2}),0}}");
    // symbolic dimensions cannot be written out in components
    check("ComponentExpand(MatrixSymbol(g,{m,n}))", //
        "MatrixSymbol(g,{m,n})");
  }
}
