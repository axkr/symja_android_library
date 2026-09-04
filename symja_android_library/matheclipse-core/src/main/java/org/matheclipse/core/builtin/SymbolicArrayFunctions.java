package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.SymbolicArrayUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IArraySymbol;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Evaluators and rewrite rules for the
 * <a href="https://reference.wolfram.com/language/guide/SymbolicArrays.html">symbolic vectors,
 * matrices and arrays</a>.
 *
 * <p>
 * The symbolic array objects stand for whole arrays without naming their components, so ordinary
 * arithmetic on them has to answer with an array again: <code>a - a</code> is the zero
 * <i>array</i>, not the scalar <code>0</code>, and multiplying an array by <code>0</code> gives the
 * zero array of the same shape. The rewriting for that lives here and is called from
 * {@link Arithmetic}, because it has to happen before the ordinary term collection of
 * {@link org.matheclipse.core.eval.PlusOp} and {@link org.matheclipse.core.eval.TimesOp} folds the
 * arrays into a scalar zero.
 * </p>
 */
public class SymbolicArrayFunctions {

  /**
   * The built-in functions which always produce an array result and therefore carry the
   * {@link ISymbol#NONTHREADABLE} attribute, so that neither they nor an expression headed by them
   * is combined with the elements of a {@link S#List} in arithmetic.
   */
  private static final IBuiltInSymbol[] NON_THREADABLE_HEADS = new IBuiltInSymbol[] { //
      S.Adjugate, S.ArrayDot, S.ConjugateTranspose, S.Dot, S.Inverse, S.KroneckerProduct, //
      S.MatrixExp, S.MatrixPower, S.PseudoInverse, S.TensorContract, S.TensorProduct, //
      S.TensorTranspose, S.Transpose};

  private static class Initializer {

    private static void init() {
      S.ArrayExpand.setEvaluator(new ArrayExpand());
      S.ArraySimplify.setEvaluator(new ArraySimplify());
      S.ComponentExpand.setEvaluator(new ComponentExpand());

      // this runs after the evaluators of these symbols have been registered, so that a setUp()
      // which assigns the attributes with ISymbol#setAttributes() cannot drop the bit again
      for (int i = 0; i < NON_THREADABLE_HEADS.length; i++) {
        NON_THREADABLE_HEADS[i].addAttributes(ISymbol.NONTHREADABLE);
      }
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private SymbolicArrayFunctions() {
    // private constructor to avoid instantiation
  }

  /**
   * Rewrite a {@link S#Dot} of two factors of which at least one is a symbolic array.
   *
   * <p>
   * An identity array is the identity of the matrix product and disappears, a zeros array makes the
   * whole product a zeros array of the contracted shape, and a contraction whose inner dimensions
   * disagree is reported through the <code>dotdim</code> message. Every other symbolic product
   * stays as it is.
   * </p>
   *
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr dotSymbolic(IExpr arg1, IExpr arg2, EvalEngine engine) {
    IAST dimensions1 = SymbolicArrayUtil.tensorDimensions(arg1, engine);
    IAST dimensions2 = SymbolicArrayUtil.tensorDimensions(arg2, engine);
    if (dimensions1.isNIL() || dimensions2.isNIL() || dimensions1.argSize() == 0
        || dimensions2.argSize() == 0) {
      return F.NIL;
    }
    if (!dimensions1.last().equals(dimensions2.arg1())) {
      if (dimensions1.last().isInteger() && dimensions2.arg1().isInteger()) {
        // Dot contraction of `1` and `2` is invalid because dimensions `3` and `4` are
        // incompatible.
        return Errors.printMessage(S.Dot, "dotdim",
            F.List(arg1, arg2, dimensions1.last(), dimensions2.arg1()), engine);
      }
      return F.NIL;
    }

    // SymbolicIdentityArray({n}) is the n x n identity matrix and is the identity of the product
    if (arg1.isAST(S.SymbolicIdentityArray, 2) && arg1.first().isList1()) {
      return arg2;
    }
    if (arg2.isAST(S.SymbolicIdentityArray, 2) && arg2.first().isList1()) {
      return arg1;
    }
    if (arg1.isAST(S.SymbolicZerosArray) || arg2.isAST(S.SymbolicZerosArray)) {
      IASTAppendable dimensions =
          F.ListAlloc(dimensions1.argSize() + dimensions2.argSize() - 2);
      dimensions.appendArgs(dimensions1, dimensions1.argSize());
      dimensions.appendAll(dimensions2, 2, dimensions2.size());
      return SymbolicArrayUtil.zeros(dimensions);
    }
    return F.NIL;
  }

  /**
   * Rewrite a {@link S#Transpose} or {@link S#ConjugateTranspose} of a symbolic array.
   *
   * @param arg1 the array to transpose
   * @param arg2 the permutation, or {@link F#NIL} for the default permutation which exchanges the
   *        first two levels
   * @param conjugate <code>true</code> for {@link S#ConjugateTranspose}
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr transposeSymbolic(IExpr arg1, IExpr arg2, boolean conjugate,
      EvalEngine engine) {
    IAST dimensions = SymbolicArrayUtil.tensorDimensions(arg1, engine);
    if (dimensions.isNIL() || dimensions.argSize() < 2) {
      // a scalar or a vector has no two levels to exchange; stay unevaluated without a message,
      // because the shape of a symbolic array is not the "not a matrix" case the message describes
      return F.NIL;
    }
    final int rank = dimensions.argSize();
    boolean defaultPermutation = arg2.isNIL() || isTransposition(arg2, rank);
    if (!defaultPermutation) {
      if (arg2.isList() && arg2.argSize() == rank && isIdentityPermutation(arg2)) {
        return arg1;
      }
      return F.NIL;
    }

    if (conjugate && arg1 instanceof IArraySymbol && ((IArraySymbol) arg1).hasRealDomain()) {
      // conjugating a real array does nothing, so the conjugate transpose is the transpose
      return F.Transpose(arg1);
    }
    if (arg1 instanceof IArraySymbol) {
      IExpr symmetry = ((IArraySymbol) arg1).getSymmetry();
      if (symmetry.isAST(S.Symmetric, 2) && isTransposition(symmetry.first(), rank)) {
        return conjugate ? F.NIL : arg1;
      }
      if (symmetry.isAST(S.Antisymmetric, 2) && isTransposition(symmetry.first(), rank)) {
        return conjugate ? F.NIL : F.Negate(arg1);
      }
      return F.NIL;
    }
    if (arg1.isAST(conjugate ? S.ConjugateTranspose : S.Transpose, 2)
        && SymbolicArrayUtil.isArrayValued(arg1.first())) {
      // exchanging the first two levels twice restores the original array
      return arg1.first();
    }
    if (arg1.isAST(S.SymbolicZerosArray, 2) || arg1.isAST(S.SymbolicOnesArray, 2)) {
      IAST arrayDimensions = (IAST) arg1.first();
      if (arrayDimensions.argSize() >= 2) {
        IASTAppendable transposed = arrayDimensions.copyAppendable();
        transposed.set(1, arrayDimensions.arg2());
        transposed.set(2, arrayDimensions.arg1());
        return F.unaryAST1(arg1.head(), transposed);
      }
      return F.NIL;
    }
    if (arg1.isAST(S.SymbolicIdentityArray, 2) && arg1.first().isList1()) {
      // the identity matrix is symmetric
      return arg1;
    }
    return F.NIL;
  }

  /** Test if <code>permutation</code> exchanges the first two of <code>rank</code> slots. */
  private static boolean isTransposition(IExpr permutation, int rank) {
    if (!permutation.isList() || permutation.argSize() < 2) {
      return false;
    }
    IAST list = (IAST) permutation;
    if (list.argSize() == 2) {
      // Symmetric({1,2}) names the two slots which may be exchanged
      return (list.arg1().equals(F.C1) && list.arg2().equals(F.C2))
          || (list.arg1().equals(F.C2) && list.arg2().equals(F.C1));
    }
    if (list.argSize() != rank) {
      return false;
    }
    if (!list.arg1().equals(F.C2) || !list.arg2().equals(F.C1)) {
      return false;
    }
    for (int i = 3; i <= rank; i++) {
      if (list.get(i).toIntDefault() != i) {
        return false;
      }
    }
    return true;
  }

  /** Test if <code>permutation</code> is the identity permutation <code>{1,2,...,n}</code>. */
  private static boolean isIdentityPermutation(IExpr permutation) {
    IAST list = (IAST) permutation;
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i).toIntDefault() != i) {
        return false;
      }
    }
    return true;
  }

  /**
   * Rewrite the {@link S#Conjugate} of a symbolic array. The entries of a real array and of the
   * symbolic array constants are their own conjugates.
   *
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr conjugateSymbolic(IExpr arg1) {
    if (arg1 instanceof IArraySymbol) {
      return ((IArraySymbol) arg1).hasRealDomain() ? arg1 : F.NIL;
    }
    if (SymbolicArrayUtil.isSymbolicArrayHead(arg1)) {
      // every entry of a symbolic zeros, ones, identity or delta product array is 0 or 1
      return arg1;
    }
    return F.NIL;
  }

  /**
   * Rewrite {@link S#Inverse} of a symbolic array. Only a square matrix can be inverted.
   *
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr inverseSymbolic(IExpr arg1, EvalEngine engine) {
    if (!checkSquareMatrix(S.Inverse, arg1, engine)) {
      return F.NIL;
    }
    if (arg1.isAST(S.Inverse, 2) && SymbolicArrayUtil.isArrayValued(arg1.first())) {
      return arg1.first();
    }
    if (arg1.isAST(S.SymbolicIdentityArray, 2)) {
      // the identity matrix is its own inverse
      return arg1;
    }
    return F.NIL;
  }

  /**
   * Rewrite {@link S#Det} of a symbolic array.
   *
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr detSymbolic(IExpr arg1, EvalEngine engine) {
    if (!checkSquareMatrix(S.Det, arg1, engine)) {
      return F.NIL;
    }
    if (arg1.isAST(S.SymbolicIdentityArray, 2)) {
      return F.C1;
    }
    if (arg1.isAST(S.SymbolicZerosArray, 2)) {
      return F.C0;
    }
    return F.NIL;
  }

  /**
   * Rewrite {@link S#Tr} of a symbolic array. The trace of an <code>n x n</code> identity matrix is
   * <code>n</code>, and so is the trace of an <code>n x n</code> array of ones.
   *
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr trSymbolic(IExpr arg1, EvalEngine engine) {
    IAST dimensions = SymbolicArrayUtil.tensorDimensions(arg1, engine);
    if (dimensions.isNIL() || dimensions.argSize() != 2
        || !dimensions.arg1().equals(dimensions.arg2())) {
      return F.NIL;
    }
    if (arg1.isAST(S.SymbolicIdentityArray, 2) || arg1.isAST(S.SymbolicOnesArray, 2)) {
      return dimensions.arg1();
    }
    if (arg1.isAST(S.SymbolicZerosArray, 2)) {
      return F.C0;
    }
    return F.NIL;
  }

  /**
   * Rewrite {@link S#MatrixPower} of a symbolic array.
   *
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr matrixPowerSymbolic(IExpr arg1, IExpr exponent, EvalEngine engine) {
    IAST dimensions = SymbolicArrayUtil.tensorDimensions(arg1, engine);
    if (!checkSquareMatrix(S.MatrixPower, arg1, engine) || dimensions.isNIL()) {
      return F.NIL;
    }
    int power = exponent.toIntDefault();
    if (power == 0) {
      return F.SymbolicIdentityArray(F.list(dimensions.arg1()));
    }
    if (power == 1) {
      return arg1;
    }
    if (power > 1) {
      if (arg1.isAST(S.SymbolicIdentityArray, 2) || arg1.isAST(S.SymbolicZerosArray, 2)) {
        return arg1;
      }
    }
    return F.NIL;
  }

  /**
   * Test if <code>arg1</code> is a square matrix, and report the <code>matsq</code> message if it
   * is definitely not one.
   *
   * @return <code>true</code> if <code>arg1</code> may be a square matrix
   */
  private static boolean checkSquareMatrix(IBuiltInSymbol head, IExpr arg1, EvalEngine engine) {
    IAST dimensions = SymbolicArrayUtil.tensorDimensions(arg1, engine);
    if (dimensions.isNIL()) {
      return false;
    }
    if (dimensions.argSize() == 2) {
      if (dimensions.arg1().equals(dimensions.arg2())) {
        return true;
      }
      if (!dimensions.arg1().isInteger() || !dimensions.arg2().isInteger()) {
        // two different symbolic dimensions may still turn out to be equal
        return false;
      }
    }
    // Argument `1` at position `2` is not a non-empty square matrix.
    Errors.printMessage(head, "matsq", F.List(arg1, F.C1), engine);
    return false;
  }

  /**
   * Convert a symbolic array constant into an explicit array. This is what {@link S#Normal} does
   * with a {@link S#SymbolicZerosArray}, a {@link S#SymbolicOnesArray}, a
   * {@link S#SymbolicIdentityArray} or a {@link S#SymbolicDeltaProductArray} whose dimensions are
   * positive integers.
   *
   * @param ast the symbolic array constant
   * @return {@link F#NIL} if the dimensions aren't positive integers
   */
  public static IExpr normalSymbolicArray(IAST ast) {
    if (!ast.arg1().isList()) {
      return F.NIL;
    }
    IAST dimensions = (IAST) ast.arg1();
    if (ast.isAST(S.SymbolicIdentityArray, 2)) {
      return LinearAlgebraUtil.normalSymbolicIdentityArray(dimensions);
    }
    if (ast.isAST(S.SymbolicZerosArray, 2)) {
      return constantArray(F.C0, dimensions);
    }
    if (ast.isAST(S.SymbolicOnesArray, 2)) {
      return constantArray(F.C1, dimensions);
    }
    if (ast.isAST(S.SymbolicDeltaProductArray, 3) && ast.arg2().isList()) {
      return deltaProductArray(dimensions, (IAST) ast.arg2());
    }
    return F.NIL;
  }

  /** An explicit array of the given dimensions, all of whose entries are <code>value</code>. */
  private static IExpr constantArray(IExpr value, IAST dimensions) {
    for (int i = 1; i < dimensions.size(); i++) {
      if (dimensions.get(i).toIntDefault() < 0) {
        return F.NIL;
      }
    }
    return EvalEngine.get().evaluate(F.ConstantArray(value, dimensions));
  }

  /**
   * An explicit array of the given dimensions whose entry is <code>1</code> where the indices in
   * each group of <code>indexGroups</code> all agree, and <code>0</code> otherwise.
   */
  private static IExpr deltaProductArray(IAST dimensions, IAST indexGroups) {
    final int rank = dimensions.argSize();
    int[] dimensionValues = new int[rank];
    for (int i = 0; i < rank; i++) {
      dimensionValues[i] = dimensions.get(i + 1).toIntDefault();
      if (dimensionValues[i] < 0) {
        return F.NIL;
      }
    }
    for (int i = 1; i < indexGroups.size(); i++) {
      if (!indexGroups.get(i).isList()) {
        return F.NIL;
      }
      IAST group = (IAST) indexGroups.get(i);
      for (int j = 1; j < group.size(); j++) {
        int slot = group.get(j).toIntDefault();
        if (slot < 1 || slot > rank) {
          return F.NIL;
        }
      }
    }
    return deltaProductRecursive(dimensionValues, indexGroups, 0, new int[rank]);
  }

  private static IExpr deltaProductRecursive(int[] dimensions, IAST indexGroups, int level,
      int[] indices) {
    if (level >= dimensions.length) {
      for (int i = 1; i < indexGroups.size(); i++) {
        IAST group = (IAST) indexGroups.get(i);
        int first = indices[group.arg1().toIntDefault() - 1];
        for (int j = 2; j < group.size(); j++) {
          if (indices[group.get(j).toIntDefault() - 1] != first) {
            return F.C0;
          }
        }
      }
      return F.C1;
    }
    IASTAppendable list = F.ListAlloc(dimensions[level]);
    for (int i = 0; i < dimensions[level]; i++) {
      indices[level] = i;
      list.append(deltaProductRecursive(dimensions, indexGroups, level + 1, indices));
    }
    return list;
  }

  /**
   * Rewrite a {@link S#TensorProduct} whose factors are symbolic array constants. The tensor
   * product of arrays of dimensions <code>d1</code> and <code>d2</code> has the concatenated
   * dimensions, and it is a zeros array as soon as one factor is one.
   *
   * @param tensorProduct the {@link S#TensorProduct} of the non-scalar factors
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr tensorProductSymbolic(IAST tensorProduct, EvalEngine engine) {
    if (tensorProduct.argSize() < 2) {
      return F.NIL;
    }
    boolean zero = false;
    boolean allOnes = true;
    IAST dimensions = F.CEmptyList;
    for (int i = 1; i < tensorProduct.size(); i++) {
      IExpr factor = tensorProduct.get(i);
      if (factor.isAST(S.SymbolicZerosArray, 2)) {
        zero = true;
      } else if (!factor.isAST(S.SymbolicOnesArray, 2)) {
        allOnes = false;
      }
      dimensions =
          SymbolicArrayUtil.joinDimensions(dimensions,
              SymbolicArrayUtil.tensorDimensions(factor, engine));
      if (dimensions.isNIL()) {
        return F.NIL;
      }
    }
    if (zero) {
      return SymbolicArrayUtil.zeros(dimensions);
    }
    if (allOnes) {
      return SymbolicArrayUtil.ones(dimensions);
    }
    return F.NIL;
  }

  /**
   * Rewrite an {@link S#ArrayDot} of which at least one argument is a symbolic array.
   *
   * <p>
   * Contracting the last <code>k</code> slots of an array against a
   * {@link S#SymbolicIdentityArray} of the same <code>k</code> dimensions reproduces the array, and
   * contracting against a {@link S#SymbolicZerosArray} gives a zeros array of the contracted shape.
   * </p>
   *
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr arrayDotSymbolic(IAST ast, IExpr arg1, IExpr arg2, EvalEngine engine) {
    if (ast.isAST2()) {
      // ArrayDot(a, b) contracts one slot, like Dot(a, b)
      return engine.evaluate(F.Dot(arg1, arg2));
    }
    if (!ast.isAST3() || !ast.arg3().isInteger()) {
      return F.NIL;
    }
    int k = ast.arg3().toIntDefault();
    if (k < 0) {
      return F.NIL;
    }
    if (k == 0) {
      return engine.evaluate(F.TensorProduct(arg1, arg2));
    }
    if (k == 1) {
      return engine.evaluate(F.Dot(arg1, arg2));
    }
    IAST dimensions1 = SymbolicArrayUtil.tensorDimensions(arg1, engine);
    IAST dimensions2 = SymbolicArrayUtil.tensorDimensions(arg2, engine);
    if (dimensions1.isNIL() || dimensions2.isNIL() || dimensions1.argSize() < k
        || dimensions2.argSize() < k) {
      return F.NIL;
    }
    for (int i = 0; i < k; i++) {
      if (!dimensions1.get(dimensions1.argSize() - k + i + 1).equals(dimensions2.get(i + 1))) {
        return F.NIL;
      }
    }
    // SymbolicIdentityArray({n1,...,nk}) has the dimensions {n1,...,nk,n1,...,nk} and is the
    // identity of the k-fold contraction
    if (arg1.isAST(S.SymbolicIdentityArray, 2) && dimensions1.argSize() == 2 * k) {
      return arg2;
    }
    if (arg2.isAST(S.SymbolicIdentityArray, 2) && dimensions2.argSize() == 2 * k) {
      return arg1;
    }
    if (arg1.isAST(S.SymbolicZerosArray) || arg2.isAST(S.SymbolicZerosArray)) {
      IASTAppendable dimensions =
          F.ListAlloc(dimensions1.argSize() + dimensions2.argSize() - 2 * k);
      dimensions.appendArgs(dimensions1, dimensions1.argSize() - k + 1);
      dimensions.appendAll(dimensions2, k + 1, dimensions2.size());
      return SymbolicArrayUtil.zeros(dimensions);
    }
    return F.NIL;
  }

  /**
   * Test if the element domain <code>declared</code> is contained in the element domain
   * <code>requested</code>, so that everything known to be in the first is also in the second.
   */
  public static boolean domainSubset(IExpr declared, IExpr requested) {
    if (declared == requested) {
      return true;
    }
    if (requested == S.Complexes) {
      return true;
    }
    if (requested == S.Reals) {
      return declared == S.Integers || declared == S.NonNegativeReals
          || declared == S.PositiveReals;
    }
    if (requested == S.NonNegativeReals) {
      return declared == S.PositiveReals;
    }
    return false;
  }

  /**
   * Answer <code>Element(expr, domain)</code> where <code>domain</code> is one of the array domains
   * {@link S#Vectors}, {@link S#Matrices} or {@link S#Arrays}.
   *
   * @param expr the expression whose membership is tested
   * @param domain the array domain
   * @return {@link S#True}, {@link S#False}, or {@link F#NIL} if the membership is undecided
   */
  public static IExpr elementOfArrayDomain(IExpr expr, IAST domain, EvalEngine engine) {
    IAST requestedDimensions = domainDimensions(domain);
    if (requestedDimensions.isNIL()) {
      return F.NIL;
    }
    IExpr requestedElementDomain = domain.argSize() >= 2 ? domain.arg2() : S.Complexes;

    IAST dimensions = SymbolicArrayUtil.tensorDimensions(expr, engine);
    if (dimensions.isNIL()) {
      return F.NIL;
    }
    if (dimensions.argSize() != requestedDimensions.argSize()) {
      return S.False;
    }
    for (int i = 1; i < dimensions.size(); i++) {
      if (!dimensions.get(i).equals(requestedDimensions.get(i))) {
        if (dimensions.get(i).isInteger() && requestedDimensions.get(i).isInteger()) {
          return S.False;
        }
        // two symbolic dimensions may still turn out to be equal
        return F.NIL;
      }
    }

    IExpr requestedSymmetry = domain.argSize() >= 3 ? domain.arg3() : S.None;
    if (expr instanceof IArraySymbol) {
      IArraySymbol arraySymbol = (IArraySymbol) expr;
      if (!sameSymmetry(requestedSymmetry, arraySymbol.getSymmetry())) {
        return F.NIL;
      }
      return domainSubset(arraySymbol.getDomain(), requestedElementDomain) ? S.True : F.NIL;
    }
    if (SymbolicArrayUtil.isSymbolicArrayHead(expr)) {
      // every entry of a symbolic zeros, ones, identity or delta product array is 0 or 1
      return sameSymmetry(requestedSymmetry, S.None) ? S.True : F.NIL;
    }
    if (expr.isList()) {
      // an explicit array belongs to the domain when every one of its entries does
      IExpr elements = engine
          .evaluate(F.Element(F.Apply(S.Alternatives, F.Flatten(expr)), requestedElementDomain));
      if (elements.isTrue() || elements.isFalse()) {
        return elements;
      }
      return F.NIL;
    }
    return F.NIL;
  }

  /**
   * Test if two symmetry specifications describe the same symmetry. The absence of a symmetry is
   * written as {@link S#None} on a symbolic array object and as an empty list of generators in an
   * array domain.
   */
  private static boolean sameSymmetry(IExpr symmetry1, IExpr symmetry2) {
    boolean none1 = symmetry1.isNone() || symmetry1.isEmptyList();
    boolean none2 = symmetry2.isNone() || symmetry2.isEmptyList();
    if (none1 || none2) {
      return none1 && none2;
    }
    return symmetry1.equals(symmetry2);
  }

  /** The dimensions list of a {@link S#Vectors}, {@link S#Matrices} or {@link S#Arrays} domain. */
  private static IAST domainDimensions(IAST domain) {
    if (domain.isAST(S.Vectors)) {
      return domain.arg1().isList() ? (IAST) domain.arg1() : F.list(domain.arg1());
    }
    if ((domain.isAST(S.Matrices) || domain.isAST(S.Arrays)) && domain.arg1().isList()) {
      return (IAST) domain.arg1();
    }
    return F.NIL;
  }

  /**
   * The component <code>Indexed(array, {i1,...,ir})</code> of a symbolic array constant, whose
   * entries are known Kronecker deltas.
   *
   * @return {@link F#NIL} if the component is not known
   */
  public static IExpr indexedSymbolicArray(IAST array, IAST indices) {
    if (array.isAST(S.SymbolicZerosArray, 2)) {
      return F.C0;
    }
    if (array.isAST(S.SymbolicOnesArray, 2)) {
      return F.C1;
    }
    if (array.isAST(S.SymbolicIdentityArray, 2) && array.arg1().isList()) {
      final int rank = array.arg1().argSize();
      if (indices.argSize() != 2 * rank) {
        return F.NIL;
      }
      // the entry is 1 exactly where each index of the first half equals its partner in the second
      IASTAppendable product = F.TimesAlloc(rank);
      for (int i = 1; i <= rank; i++) {
        product.append(F.KroneckerDelta(indices.get(i), indices.get(i + rank)));
      }
      return product.oneIdentity1();
    }
    if (array.isAST(S.SymbolicDeltaProductArray, 3) && array.arg1().isList()
        && array.arg2().isList()) {
      IAST dimensions = (IAST) array.arg1();
      IAST indexGroups = (IAST) array.arg2();
      if (indices.argSize() != dimensions.argSize()) {
        return F.NIL;
      }
      IASTAppendable product = F.TimesAlloc(indexGroups.argSize());
      for (int i = 1; i < indexGroups.size(); i++) {
        if (!indexGroups.get(i).isList()) {
          return F.NIL;
        }
        IAST group = (IAST) indexGroups.get(i);
        IASTAppendable delta = F.ast(S.KroneckerDelta, group.argSize());
        for (int j = 1; j < group.size(); j++) {
          int slot = group.get(j).toIntDefault();
          if (slot < 1 || slot > indices.argSize()) {
            return F.NIL;
          }
          delta.append(indices.get(slot));
        }
        product.append(delta);
      }
      return product.oneIdentity1();
    }
    return F.NIL;
  }

  /** The maximum number of rewrite rounds of {@link #rewrite(IExpr, boolean, EvalEngine)}. */
  private static final int MAX_REWRITE_ROUNDS = 16;

  /**
   * <code>ArrayExpand(expr)</code> - expand out the symbolic array operations in
   * <code>expr</code>.
   */
  private static final class ArrayExpand extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return withAssumptions(ast, engine, () -> rewrite(ast.arg1(), true, engine));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ArraySimplify(expr)</code> - simplify the symbolic array expression <code>expr</code>.
   */
  private static final class ArraySimplify extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return withAssumptions(ast, engine, () -> {
        IExpr input = ast.arg1();
        // the contraction rules are canonicalising - pushing a Transpose through a product or
        // splitting the determinant of a product is the simplified form even where it is longer -
        // so the expanded form is only preferred when it really is shorter
        IExpr contracted = rewrite(input, false, engine);
        IExpr expanded = rewrite(input, true, engine);
        return expanded.leafCount() < contracted.leafCount() ? expanded : contracted;
      });
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>ComponentExpand(expr)</code> - write the symbolic arrays in <code>expr</code> as explicit
   * arrays of their indexed components.
   */
  private static final class ComponentExpand extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return withAssumptions(ast, engine, () -> componentExpand(ast.arg1(), engine));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * Evaluate <code>body</code> with the assumptions given as the second argument of
   * <code>ast</code>, restoring the previous assumptions afterwards.
   */
  private static IExpr withAssumptions(IAST ast, EvalEngine engine,
      java.util.function.Supplier<IExpr> body) {
    IAssumptions oldAssumptions = engine.getAssumptions();
    try {
      OptionArgs options = null;
      if (ast.size() > 2) {
        options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
      }
      IExpr assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);
      if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
        IAssumptions assumptions =
            org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
        if (assumptions != null) {
          engine.setAssumptions(assumptions);
        }
      }
      return body.get();
    } finally {
      engine.setAssumptions(oldAssumptions);
    }
  }

  /**
   * Apply the array rewrite rules to a fixed point.
   *
   * @param expr the expression to rewrite
   * @param expand <code>true</code> to distribute the multilinear operations over sums,
   *        <code>false</code> to contract instead
   * @param engine the evaluation engine
   */
  private static IExpr rewrite(IExpr expr, boolean expand, EvalEngine engine) {
    IExpr current = engine.evaluate(expr);
    for (int round = 0; round < MAX_REWRITE_ROUNDS; round++) {
      IExpr rewritten = current.replaceAll(x -> arrayRule(x, expand, engine));
      if (rewritten.isNIL()) {
        break;
      }
      rewritten = engine.evaluate(rewritten);
      if (rewritten.equals(current)) {
        break;
      }
      current = rewritten;
    }
    return current;
  }

  /**
   * One rewrite step for a subexpression. The identities are the ones which hold for every array of
   * matching shape, so they are applied only where the operands really are arrays.
   *
   * @return {@link F#NIL} if no rule applies
   */
  private static IExpr arrayRule(IExpr expr, boolean expand, EvalEngine engine) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    IAST ast = (IAST) expr;
    switch (ast.headID()) {
      case ID.Dot:
        return dotRule(ast, expand, engine);
      case ID.Transpose:
      case ID.ConjugateTranspose:
        return transposeRule(ast, engine);
      case ID.Inverse:
        return inverseRule(ast, engine);
      case ID.Tr:
        return trRule(ast, engine);
      case ID.Det:
        return detRule(ast, engine);
      default:
        return F.NIL;
    }
  }

  /** Rewrite rules for a {@link S#Dot} chain. */
  private static IExpr dotRule(IAST dot, boolean expand, EvalEngine engine) {
    if (dot.argSize() < 2) {
      return F.NIL;
    }
    // pull the scalar factors out of the product
    IASTAppendable scalars = F.TimesAlloc(dot.argSize());
    IASTAppendable factors = F.ast(S.Dot, dot.argSize());
    boolean scalarFound = false;
    for (int i = 1; i < dot.size(); i++) {
      IExpr factor = dot.get(i);
      if (factor.isTimes()) {
        IAST times = (IAST) factor;
        IASTAppendable remaining = F.TimesAlloc(times.argSize());
        for (int j = 1; j < times.size(); j++) {
          if (SymbolicArrayUtil.tensorDimensions(times.get(j), engine).isEmptyList()) {
            scalars.append(times.get(j));
            scalarFound = true;
          } else {
            remaining.append(times.get(j));
          }
        }
        factors.append(remaining.oneIdentity1());
      } else {
        factors.append(factor);
      }
    }
    if (scalarFound) {
      return F.Times(scalars.oneIdentity1(), factors);
    }

    // cancel a matrix against its inverse where they are adjacent in the chain
    for (int i = 1; i < dot.argSize(); i++) {
      IExpr left = dot.get(i);
      IExpr right = dot.get(i + 1);
      if (left.isAST(S.Inverse, 2) && left.first().equals(right)
          || right.isAST(S.Inverse, 2) && right.first().equals(left)) {
        IAST dimensions = SymbolicArrayUtil.tensorDimensions(right, engine);
        if (dimensions.isPresent() && dimensions.argSize() == 2) {
          IASTAppendable result = F.ast(S.Dot, dot.argSize());
          result.appendAll(dot, 1, i);
          result.append(F.SymbolicIdentityArray(F.list(dimensions.arg1())));
          result.appendAll(dot, i + 2, dot.size());
          return result;
        }
      }
    }

    if (expand) {
      // Dot is multilinear, so it distributes over a sum
      for (int i = 1; i < dot.size(); i++) {
        if (dot.get(i).isPlus()) {
          IAST plus = (IAST) dot.get(i);
          final int position = i;
          return plus.mapThread(dot.setAtCopy(position, F.Slot1), position);
        }
      }
      return F.NIL;
    }

    // write a row vector product as a column vector product, the canonical form of a vector
    // valued Dot chain
    IAST firstDimensions = SymbolicArrayUtil.tensorDimensions(dot.arg1(), engine);
    if (firstDimensions.isPresent() && firstDimensions.argSize() == 1) {
      for (int i = 2; i < dot.size(); i++) {
        IAST dimensions = SymbolicArrayUtil.tensorDimensions(dot.get(i), engine);
        if (dimensions.isNIL() || dimensions.argSize() != 2) {
          return F.NIL;
        }
      }
      IASTAppendable result = F.ast(S.Dot, dot.argSize());
      for (int i = dot.argSize(); i >= 2; i--) {
        result.append(F.Transpose(dot.get(i)));
      }
      result.append(dot.arg1());
      return result;
    }
    return F.NIL;
  }

  /** Rewrite rules for {@link S#Transpose} and {@link S#ConjugateTranspose}. */
  private static IExpr transposeRule(IAST transpose, EvalEngine engine) {
    // reversing a transposed product is a canonicalisation, so it runs in both passes
    if (!transpose.isAST1()) {
      return F.NIL;
    }
    IExpr head = transpose.head();
    IExpr arg1 = transpose.arg1();
    if (arg1.isAST(S.Dot) && arg1.size() > 2) {
      // transposing a product reverses it
      IAST dot = (IAST) arg1;
      IASTAppendable result = F.ast(S.Dot, dot.argSize());
      for (int i = dot.argSize(); i >= 1; i--) {
        result.append(F.unaryAST1(head, dot.get(i)));
      }
      return result;
    }
    if (arg1.isPlus()) {
      return ((IAST) arg1).mapThread(F.unaryAST1(head, F.Slot1), 1);
    }
    if (arg1.isTimes()) {
      IAST times = (IAST) arg1;
      IASTAppendable scalars = F.TimesAlloc(times.argSize());
      IASTAppendable arrays = F.TimesAlloc(times.argSize());
      for (int i = 1; i < times.size(); i++) {
        if (SymbolicArrayUtil.tensorDimensions(times.get(i), engine).isEmptyList()) {
          scalars.append(head == S.ConjugateTranspose ? F.Conjugate(times.get(i)) : times.get(i));
        } else {
          arrays.append(times.get(i));
        }
      }
      if (scalars.argSize() > 0 && arrays.argSize() > 0) {
        return F.Times(scalars.oneIdentity1(), F.unaryAST1(head, arrays.oneIdentity1()));
      }
    }
    return F.NIL;
  }

  /** Rewrite rules for {@link S#Inverse}. */
  private static IExpr inverseRule(IAST inverse, EvalEngine engine) {
    if (!inverse.isAST1()) {
      return F.NIL;
    }
    IExpr arg1 = inverse.arg1();
    if (arg1.isAST(S.Dot) && arg1.size() > 2) {
      // inverting a product reverses it
      IAST dot = (IAST) arg1;
      IASTAppendable result = F.ast(S.Dot, dot.argSize());
      for (int i = dot.argSize(); i >= 1; i--) {
        result.append(F.Inverse(dot.get(i)));
      }
      return result;
    }
    if (arg1.isTimes()) {
      IAST times = (IAST) arg1;
      IASTAppendable scalars = F.TimesAlloc(times.argSize());
      IASTAppendable arrays = F.TimesAlloc(times.argSize());
      for (int i = 1; i < times.size(); i++) {
        if (SymbolicArrayUtil.tensorDimensions(times.get(i), engine).isEmptyList()) {
          scalars.append(times.get(i));
        } else {
          arrays.append(times.get(i));
        }
      }
      if (scalars.argSize() > 0 && arrays.argSize() > 0) {
        return F.Divide(F.Inverse(arrays.oneIdentity1()), scalars.oneIdentity1());
      }
    }
    return F.NIL;
  }

  /** Rewrite rules for {@link S#Tr}. */
  private static IExpr trRule(IAST tr, EvalEngine engine) {
    if (!tr.isAST1()) {
      return F.NIL;
    }
    IExpr arg1 = tr.arg1();
    if (arg1.isAST(S.Transpose, 2)) {
      // transposing a matrix leaves its diagonal alone
      return F.Tr(arg1.first());
    }
    if (arg1.isPlus()) {
      return ((IAST) arg1).mapThread(F.Tr(F.Slot1), 1);
    }
    if (arg1.isTimes()) {
      IAST times = (IAST) arg1;
      IASTAppendable scalars = F.TimesAlloc(times.argSize());
      IASTAppendable arrays = F.TimesAlloc(times.argSize());
      for (int i = 1; i < times.size(); i++) {
        if (SymbolicArrayUtil.tensorDimensions(times.get(i), engine).isEmptyList()) {
          scalars.append(times.get(i));
        } else {
          arrays.append(times.get(i));
        }
      }
      if (scalars.argSize() > 0 && arrays.argSize() > 0) {
        return F.Times(scalars.oneIdentity1(), F.Tr(arrays.oneIdentity1()));
      }
    }
    return F.NIL;
  }

  /** Rewrite rules for {@link S#Det}. */
  private static IExpr detRule(IAST det, EvalEngine engine) {
    if (!det.isAST1()) {
      return F.NIL;
    }
    IExpr arg1 = det.arg1();
    if (arg1.isAST(S.Transpose, 2)) {
      return F.Det(arg1.first());
    }
    if (arg1.isAST(S.Inverse, 2)) {
      return F.Power(F.Det(arg1.first()), F.CN1);
    }
    if (arg1.isAST(S.Dot) && arg1.size() > 2) {
      // the determinant is multiplicative
      IAST dot = (IAST) arg1;
      IASTAppendable result = F.TimesAlloc(dot.argSize());
      for (int i = 1; i < dot.size(); i++) {
        IAST dimensions = SymbolicArrayUtil.tensorDimensions(dot.get(i), engine);
        if (dimensions.isNIL() || dimensions.argSize() != 2
            || !dimensions.arg1().equals(dimensions.arg2())) {
          return F.NIL;
        }
        result.append(F.Det(dot.get(i)));
      }
      return result;
    }
    if (arg1.isTimes()) {
      IAST times = (IAST) arg1;
      IASTAppendable scalars = F.TimesAlloc(times.argSize());
      IASTAppendable arrays = F.TimesAlloc(times.argSize());
      for (int i = 1; i < times.size(); i++) {
        if (SymbolicArrayUtil.tensorDimensions(times.get(i), engine).isEmptyList()) {
          scalars.append(times.get(i));
        } else {
          arrays.append(times.get(i));
        }
      }
      if (scalars.argSize() > 0 && arrays.argSize() > 0) {
        IAST dimensions = SymbolicArrayUtil.tensorDimensions(arrays.oneIdentity1(), engine);
        if (dimensions.isPresent() && dimensions.argSize() == 2
            && dimensions.arg1().equals(dimensions.arg2())) {
          // scaling an n x n matrix scales its determinant by the n-th power
          return F.Times(F.Power(scalars.oneIdentity1(), dimensions.arg1()),
              F.Det(arrays.oneIdentity1()));
        }
      }
    }
    return F.NIL;
  }

  /**
   * Replace every symbolic array of positive integer dimensions in <code>expr</code> by the
   * explicit array of its {@link S#Indexed} components, and evaluate the result.
   */
  public static IExpr componentExpand(IExpr expr, EvalEngine engine) {
    IExpr replaced = componentReplacement(expr, engine);
    if (replaced.isPresent()) {
      return engine.evaluate(replaced);
    }
    IExpr result = expr.replaceAll(x -> componentReplacement(x, engine));
    return result.isPresent() ? engine.evaluate(result) : expr;
  }

  /** The explicit component array of one symbolic array, or {@link F#NIL}. */
  private static IExpr componentReplacement(IExpr expr, EvalEngine engine) {
    if (expr instanceof IArraySymbol) {
      return componentArray((IArraySymbol) expr);
    }
    if (SymbolicArrayUtil.isSymbolicArrayHead(expr)) {
      return normalSymbolicArray((IAST) expr);
    }
    return F.NIL;
  }

  /**
   * The explicit array of {@link S#Indexed} components of a symbolic array of positive integer
   * dimensions, honouring a declared symmetry.
   *
   * @return {@link F#NIL} if a dimension is not a positive integer
   */
  private static IExpr componentArray(IArraySymbol arraySymbol) {
    IAST dimensions = arraySymbol.getDimensions();
    int[] dimensionValues = new int[dimensions.argSize()];
    for (int i = 0; i < dimensionValues.length; i++) {
      dimensionValues[i] = dimensions.get(i + 1).toIntDefault();
      if (dimensionValues[i] < 1) {
        return F.NIL;
      }
    }
    IExpr symmetry = arraySymbol.getSymmetry();
    int[] symmetrySlots = null;
    boolean antisymmetric = false;
    if (symmetry.isAST(S.Symmetric, 2) || symmetry.isAST(S.Antisymmetric, 2)) {
      antisymmetric = symmetry.isAST(S.Antisymmetric, 2);
      IExpr slots = symmetry.first();
      if (slots.isList()) {
        IAST slotList = (IAST) slots;
        symmetrySlots = new int[slotList.argSize()];
        for (int i = 0; i < symmetrySlots.length; i++) {
          symmetrySlots[i] = slotList.get(i + 1).toIntDefault();
        }
      }
    }
    return componentRecursive(arraySymbol.getName(), dimensionValues, symmetrySlots, antisymmetric,
        0, new int[dimensionValues.length]);
  }

  private static IExpr componentRecursive(IExpr name, int[] dimensions, int[] symmetrySlots,
      boolean antisymmetric, int level, int[] indices) {
    if (level >= dimensions.length) {
      return component(name, symmetrySlots, antisymmetric, indices);
    }
    IASTAppendable list = F.ListAlloc(dimensions[level]);
    for (int i = 1; i <= dimensions[level]; i++) {
      indices[level] = i;
      list.append(
          componentRecursive(name, dimensions, symmetrySlots, antisymmetric, level + 1, indices));
    }
    return list;
  }

  /** One <code>Indexed(name, {i1,...,ir})</code> component, reduced by a declared symmetry. */
  private static IExpr component(IExpr name, int[] symmetrySlots, boolean antisymmetric,
      int[] indices) {
    int[] componentIndices = indices.clone();
    int sign = 1;
    if (symmetrySlots != null) {
      // the entries at the symmetric slots may be reordered, so only the sorted component is named
      int[] values = new int[symmetrySlots.length];
      for (int i = 0; i < symmetrySlots.length; i++) {
        values[i] = indices[symmetrySlots[i] - 1];
      }
      for (int i = 0; i < values.length; i++) {
        for (int j = i + 1; j < values.length; j++) {
          if (values[i] > values[j]) {
            int swap = values[i];
            values[i] = values[j];
            values[j] = swap;
            sign = -sign;
          } else if (antisymmetric && values[i] == values[j]) {
            // an antisymmetric array vanishes wherever two of its symmetric indices agree
            return F.C0;
          }
        }
      }
      for (int i = 0; i < symmetrySlots.length; i++) {
        componentIndices[symmetrySlots[i] - 1] = values[i];
      }
    }
    IASTAppendable indexList = F.ListAlloc(componentIndices.length);
    for (int i = 0; i < componentIndices.length; i++) {
      indexList.append(F.ZZ(componentIndices[i]));
    }
    IExpr indexed = F.binaryAST2(S.Indexed, name, indexList);
    return (antisymmetric && sign < 0) ? F.Negate(indexed) : indexed;
  }

  /**
   * One summand of a {@link S#Plus} which stands for an array, split into the scalar coefficient
   * and the array valued base.
   */
  private static final class ArrayTerm {
    final IExpr coefficient;
    final IExpr base;

    ArrayTerm(IExpr coefficient, IExpr base) {
      this.coefficient = coefficient;
      this.base = base;
    }
  }

  /**
   * Split an array valued summand into its scalar coefficient and its array valued base, so that
   * <code>3*a</code> contributes the coefficient <code>3</code> to the base <code>a</code> and
   * <code>a</code> and <code>-a</code> can cancel into a zero array.
   */
  private static ArrayTerm splitArrayTerm(IExpr term) {
    if (term.isTimes()) {
      IAST times = (IAST) term;
      IASTAppendable coefficient = F.TimesAlloc(times.argSize());
      IASTAppendable arrayFactors = F.TimesAlloc(times.argSize());
      for (int i = 1; i < times.size(); i++) {
        IExpr factor = times.get(i);
        if (SymbolicArrayUtil.isArrayValued(factor)) {
          arrayFactors.append(factor);
        } else {
          coefficient.append(factor);
        }
      }
      if (arrayFactors.argSize() >= 1) {
        return new ArrayTerm(coefficient.oneIdentity1(), arrayFactors.oneIdentity1());
      }
    }
    return new ArrayTerm(F.C1, term);
  }

  /**
   * Rewrite a {@link S#Plus} which contains a symbolic array summand.
   *
   * <p>
   * Summands which stand for arrays are collected by their base, so that the coefficients of equal
   * bases add up; a base whose coefficients cancel disappears, and if every array summand cancels
   * the result is the zero array of the common shape rather than the scalar <code>0</code>. A
   * {@link S#SymbolicOnesArray} summand is an array all of whose entries are equal, so its
   * coefficient may join the scalar summands.
   * </p>
   *
   * @param plus the {@link S#Plus} expression to rewrite
   * @param engine the evaluation engine
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr plusSymbolicArrays(IAST plus, EvalEngine engine) {
    IAST dimensions = F.NIL;
    IASTAppendable scalars = F.PlusAlloc(plus.argSize());
    List<ArrayTerm> arrayTerms = new ArrayList<ArrayTerm>();
    for (int i = 1; i < plus.size(); i++) {
      IExpr summand = plus.get(i);
      if (!SymbolicArrayUtil.isArrayValued(summand)) {
        // Adding a scalar to an array adds it to every entry, so a scalar summand may be folded
        // into the coefficient of a SymbolicOnesArray. A summand of unknown rank may be an array
        // of its own though, and then it may not be folded, so leave such a sum alone entirely.
        if (!SymbolicArrayUtil.tensorDimensions(summand, engine).isEmptyList()) {
          return F.NIL;
        }
        scalars.append(summand);
        continue;
      }
      ArrayTerm term = splitArrayTerm(summand);
      IAST termDimensions = SymbolicArrayUtil.tensorDimensions(term.base, engine);
      if (termDimensions.isPresent()) {
        if (dimensions.isNIL()) {
          dimensions = termDimensions;
        } else if (!dimensions.equals(termDimensions)) {
          // summands of different shape cannot be added; leave the sum alone
          return F.NIL;
        }
      }
      arrayTerms.add(term);
    }
    if (arrayTerms.isEmpty() || dimensions.isNIL()) {
      return F.NIL;
    }

    // collect equal bases, and fold a SymbolicOnesArray base into the scalar summands
    List<IExpr> bases = new ArrayList<IExpr>();
    List<IASTAppendable> coefficients = new ArrayList<IASTAppendable>();
    IASTAppendable onesCoefficient = F.PlusAlloc(arrayTerms.size());
    // S.Plus is S.Orderless, so a rebuilt sum which only differs in the argument order would be
    // sorted back and rewritten again on every pass; answer only when something really changed
    boolean evaled = false;
    for (ArrayTerm term : arrayTerms) {
      if (term.base.isAST(S.SymbolicZerosArray)) {
        // the zero array is the identity of the sum
        evaled = true;
        continue;
      }
      if (term.base.isAST(S.SymbolicOnesArray)) {
        onesCoefficient.append(term.coefficient);
        evaled = true;
        continue;
      }
      int position = bases.indexOf(term.base);
      if (position < 0) {
        bases.add(term.base);
        IASTAppendable sum = F.PlusAlloc(4);
        sum.append(term.coefficient);
        coefficients.add(sum);
      } else {
        coefficients.get(position).append(term.coefficient);
        evaled = true;
      }
    }

    IASTAppendable result = F.PlusAlloc(bases.size() + 2);
    for (int i = 0; i < bases.size(); i++) {
      IExpr coefficient = engine.evaluate(coefficients.get(i));
      if (coefficient.isZero()) {
        // this base cancels out completely
        evaled = true;
        continue;
      }
      result.append(coefficient.isOne() ? bases.get(i) : F.Times(coefficient, bases.get(i)));
    }

    // every entry of a SymbolicOnesArray is 1, so a scalar summand and a multiple of the ones
    // array describe the same array and can be combined
    IExpr constant = engine.evaluate(F.Plus(scalars.oneIdentity0(), onesCoefficient.oneIdentity0()));
    if (!evaled) {
      return F.NIL;
    }
    if (result.argSize() == 0) {
      if (constant.isZero()) {
        return SymbolicArrayUtil.zeros(dimensions);
      }
      IAST ones = SymbolicArrayUtil.ones(dimensions);
      return constant.isOne() ? ones : F.Times(constant, ones);
    }
    if (!constant.isZero()) {
      result.append(constant);
    }
    return result.oneIdentity0();
  }

  /**
   * Rewrite a {@link S#Times} which contains a symbolic array factor.
   *
   * <p>
   * A {@link S#Times} of array valued factors is the elementwise product, so a scalar factor of
   * <code>0</code> and a {@link S#SymbolicZerosArray} factor both make the whole product the zero
   * array of the common shape, and a {@link S#SymbolicOnesArray} factor is the identity of the
   * elementwise product and can be dropped.
   * </p>
   *
   * @param times the {@link S#Times} expression to rewrite
   * @param engine the evaluation engine
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr timesSymbolicArrays(IAST times, EvalEngine engine) {
    IAST dimensions = F.NIL;
    boolean zero = false;
    int arrayFactors = 0;
    for (int i = 1; i < times.size(); i++) {
      IExpr factor = times.get(i);
      if (factor.isZero()) {
        zero = true;
        continue;
      }
      if (!SymbolicArrayUtil.isArrayValued(factor)) {
        continue;
      }
      arrayFactors++;
      IAST factorDimensions = SymbolicArrayUtil.tensorDimensions(factor, engine);
      if (factorDimensions.isPresent()) {
        if (dimensions.isNIL()) {
          dimensions = factorDimensions;
        } else if (!dimensions.equals(factorDimensions)) {
          // factors of different shape cannot be multiplied elementwise; leave the product alone
          return F.NIL;
        }
      }
      if (factor.isAST(S.SymbolicZerosArray)) {
        zero = true;
      }
    }
    if (arrayFactors == 0 || dimensions.isNIL()) {
      return F.NIL;
    }
    if (zero) {
      return SymbolicArrayUtil.zeros(dimensions);
    }

    // drop the SymbolicOnesArray factors, which are the identity of the elementwise product
    IASTAppendable result = F.TimesAlloc(times.argSize());
    boolean evaled = false;
    for (int i = 1; i < times.size(); i++) {
      IExpr factor = times.get(i);
      if (factor.isAST(S.SymbolicOnesArray) && arrayFactors > 1) {
        arrayFactors--;
        evaled = true;
        continue;
      }
      result.append(factor);
    }
    return evaled ? result.oneIdentity1() : F.NIL;
  }

  /**
   * Rewrite an integer power of a symbolic array constant. The elements of a
   * {@link S#SymbolicZerosArray}, a {@link S#SymbolicOnesArray} and a
   * {@link S#SymbolicIdentityArray} are all <code>0</code> or <code>1</code>, so raising them to a
   * positive integer power reproduces the array.
   *
   * @param base the base of the power
   * @param exponent the exponent of the power
   * @return {@link F#NIL} if nothing could be rewritten
   */
  public static IExpr powerSymbolicArray(IExpr base, IExpr exponent) {
    if (!exponent.isInteger() || !exponent.isPositive()) {
      return F.NIL;
    }
    if (base.isAST(S.SymbolicZerosArray) || base.isAST(S.SymbolicOnesArray)
        || base.isAST(S.SymbolicIdentityArray)) {
      return base;
    }
    return F.NIL;
  }
}
