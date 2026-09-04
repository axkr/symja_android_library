package org.matheclipse.core.eval;

import java.util.Map;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IArraySymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumericArray;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.ISymbolicArray;

/**
 * Static helpers for the symbolic array objects - {@link S#VectorSymbol}, {@link S#MatrixSymbol},
 * {@link S#ArraySymbol} - and the symbolic array constants {@link S#SymbolicZerosArray},
 * {@link S#SymbolicOnesArray}, {@link S#SymbolicIdentityArray} and
 * {@link S#SymbolicDeltaProductArray}.
 *
 * <p>
 * {@link LinearAlgebraUtil#dimensions(IExpr, IExpr, int, boolean)} answers the dimensions of an
 * <i>explicit</i> array as machine integers and cannot describe a symbolic dimension like
 * <code>{m,n}</code>, which is why the symbolic paths use the methods here instead.
 * </p>
 */
public class SymbolicArrayUtil {

  private SymbolicArrayUtil() {
    // private constructor to avoid instantiation
  }

  /**
   * The maximum depth to which {@link #isArrayValued(IExpr)} and
   * {@link #tensorDimensions(IExpr, EvalEngine)} walk into an expression. Both are called from the
   * arithmetic evaluators, so they must stay cheap and must not recurse without a bound.
   */
  private static final int MAX_DEPTH = 32;

  /**
   * Test if <code>expr</code> stands for a non-scalar quantity, so that it must not be threaded
   * over a {@link S#List} by a {@link ISymbol#LISTABLE} function.
   *
   * <p>
   * That is the case for the symbolic array objects and the symbolic array constants themselves,
   * for every expression whose head carries the {@link ISymbol#NONTHREADABLE} attribute, and for
   * the result of applying a {@link ISymbol#LISTABLE} function to such an expression.
   * </p>
   *
   * @param expr the expression to test
   * @return <code>true</code> if <code>expr</code> is known to be non-scalar
   */
  public static boolean isArrayValued(IExpr expr) {
    return isArrayValued(expr, 0);
  }

  private static boolean isArrayValued(IExpr expr, int depth) {
    if (expr instanceof IArraySymbol) {
      return true;
    }
    if (depth >= MAX_DEPTH || !expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    if (expr.headInstanceOf(ISymbolicArray.class) != null) {
      return true;
    }
    IExpr head = ast.head();
    if (head instanceof ISymbol) {
      ISymbol headSymbol = (ISymbol) head;
      if (Attribute.NONTHREADABLE.isAnySetIn(headSymbol.getAttributes())) {
        return true;
      }
      if (headSymbol.hasListableAttribute()) {
        // a S.Listable function of a non-scalar argument is itself non-scalar; this is what makes
        // Sin(MatrixSymbol(a, {2,2})) and 2*MatrixSymbol(a, {2,2}) non-threadable as well
        return ast.exists(x -> isArrayValued(x, depth + 1));
      }
    } else if (head instanceof IArraySymbol) {
      // an expression like MatrixSymbol("a", {m,n})[x] is a matrix valued function
      return true;
    }
    return false;
  }

  /**
   * The rank of <code>expr</code>, i.e. the length of its {@link #tensorDimensions} list.
   *
   * @return <code>-1</code> if the rank could not be determined
   */
  public static int rank(IExpr expr, EvalEngine engine) {
    IAST dimensions = tensorDimensions(expr, engine);
    return dimensions.isPresent() ? dimensions.argSize() : -1;
  }

  /**
   * Determine the dimensions of a possibly symbolic array expression.
   *
   * <p>
   * Unlike {@link LinearAlgebraUtil#dimensions(IExpr, IExpr, int, boolean)} this method answers
   * <i>symbolic</i> dimensions like <code>{m,n}</code>, and it looks through the arithmetic and
   * array operations, so that the shape of <code>a.b</code> or <code>Transpose(a)</code> can be
   * derived from the shapes of the symbolic arrays inside.
   * </p>
   *
   * @param expr the expression whose dimensions should be determined
   * @param engine the evaluation engine, used for the {@link S#Element} assumptions
   * @return the dimensions list; {@link F#CEmptyList} for a scalar and {@link F#NIL} if the
   *         dimensions could not be determined
   */
  public static IAST tensorDimensions(IExpr expr, EvalEngine engine) {
    return tensorDimensions(expr, engine, 0);
  }

  private static IAST tensorDimensions(IExpr expr, EvalEngine engine, int depth) {
    if (depth >= MAX_DEPTH) {
      return F.NIL;
    }
    if (expr instanceof IArraySymbol) {
      return ((IArraySymbol) expr).getDimensions();
    }
    if (expr.isNumber()) {
      return F.CEmptyList;
    }
    if (expr.isSparseArray()) {
      return dimensionsOf(((ISparseArray) expr).getDimension());
    }
    if (expr.isNumericArray()) {
      return dimensionsOf(((INumericArray) expr).getDimension());
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      ISymbolicArray symbolicArray = expr.headInstanceOf(ISymbolicArray.class);
      if (symbolicArray != null) {
        return symbolicArray.getDimensions(ast);
      }
      if (expr.isList()) {
        IExpr dimensions = engine.evaluate(F.Dimensions(expr));
        return dimensions.isList() ? (IAST) dimensions : F.NIL;
      }
      return astDimensions(ast, engine, depth);
    }
    if (expr.isSymbol()) {
      return assumedDimensions(expr, engine);
    }
    return F.NIL;
  }

  /** The dimensions of an expression whose head is a built-in array operation. */
  private static IAST astDimensions(IAST ast, EvalEngine engine, int depth) {
    IExpr head = ast.head();
    if (!head.isBuiltInSymbol()) {
      return F.NIL;
    }
    final int id = ((ISymbol) head).ordinal();
    switch (id) {
      case ID.Dot:
        return dotDimensions(ast, engine, depth);
      case ID.Transpose:
      case ID.ConjugateTranspose:
        return transposeDimensions(ast, engine, depth);
      case ID.Inverse:
      case ID.PseudoInverse:
      case ID.Adjugate:
      case ID.MatrixExp:
      case ID.MatrixPower: {
        IAST dimensions = tensorDimensions(ast.arg1(), engine, depth + 1);
        // these all map a square matrix onto a square matrix of the same shape
        return (dimensions.isPresent() && dimensions.argSize() == 2
            && dimensions.arg1().equals(dimensions.arg2())) ? dimensions : F.NIL;
      }
      case ID.TensorProduct: {
        IAST dimensions = F.CEmptyList;
        for (int i = 1; i < ast.size(); i++) {
          dimensions = joinDimensions(dimensions, tensorDimensions(ast.get(i), engine, depth + 1));
          if (dimensions.isNIL()) {
            return F.NIL;
          }
        }
        return dimensions;
      }
      case ID.ArrayDot:
        return arrayDotDimensions(ast, engine, depth);
      case ID.Tr:
      case ID.Det:
      case ID.Norm: {
        // a scalar valued function of an array
        IAST dimensions = tensorDimensions(ast.arg1(), engine, depth + 1);
        return dimensions.isPresent() ? F.CEmptyList : F.NIL;
      }
      case ID.Total:
      case ID.Mean: {
        if (!ast.isAST1()) {
          return F.NIL;
        }
        IAST dimensions = tensorDimensions(ast.arg1(), engine, depth + 1);
        if (dimensions.isNIL() || dimensions.argSize() == 0) {
          return F.NIL;
        }
        // summing or averaging over the outermost level drops the first dimension
        return dimensions.removeAtCopy(1);
      }
      case ID.Indexed: {
        if (!ast.isAST2() || !ast.arg2().isList()) {
          return F.NIL;
        }
        IAST dimensions = tensorDimensions(ast.arg1(), engine, depth + 1);
        if (dimensions.isNIL() || dimensions.argSize() != ast.arg2().argSize()) {
          return F.NIL;
        }
        return F.CEmptyList;
      }
      default:
        break;
    }
    if (((ISymbol) head).hasListableAttribute()) {
      // Plus, Times, Power, Conjugate, Sin, ... - all arguments are combined elementwise, so all
      // non-scalar arguments must have the same shape and that shape is the result shape
      IAST result = F.CEmptyList;
      for (int i = 1; i < ast.size(); i++) {
        IAST dimensions = tensorDimensions(ast.get(i), engine, depth + 1);
        if (dimensions.isNIL()) {
          return F.NIL;
        }
        if (dimensions.argSize() > 0) {
          if (result.argSize() == 0) {
            result = dimensions;
          } else if (!result.equals(dimensions)) {
            return F.NIL;
          }
        }
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * The dimensions of a {@link S#Dot} chain. Contracting an array of dimensions
   * <code>{...,d}</code> with an array of dimensions <code>{d,...}</code> drops the two contracted
   * slots.
   */
  private static IAST dotDimensions(IAST dot, EvalEngine engine, int depth) {
    IAST dimensions = tensorDimensions(dot.arg1(), engine, depth + 1);
    if (dimensions.isNIL() || dimensions.argSize() == 0) {
      return F.NIL;
    }
    for (int i = 2; i < dot.size(); i++) {
      IAST next = tensorDimensions(dot.get(i), engine, depth + 1);
      if (next.isNIL() || next.argSize() == 0) {
        return F.NIL;
      }
      IExpr last = dimensions.last();
      IExpr first = next.arg1();
      if (!last.equals(first)) {
        if (last.isInteger() && first.isInteger()) {
          // Dot contraction of `1` and `2` is invalid because dimensions `3` and `4` are
          // incompatible.
          Errors.printMessage(S.Dot, "dotdim",
              F.List(dot.get(i - 1), dot.get(i), last, first), engine);
        }
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(dimensions.argSize() + next.argSize() - 2);
      result.appendArgs(dimensions, dimensions.argSize());
      result.appendAll(next, 2, next.size());
      dimensions = result;
    }
    return dimensions;
  }

  /** The dimensions of {@link S#Transpose} or {@link S#ConjugateTranspose}. */
  private static IAST transposeDimensions(IAST transpose, EvalEngine engine, int depth) {
    IAST dimensions = tensorDimensions(transpose.arg1(), engine, depth + 1);
    if (dimensions.isNIL() || dimensions.argSize() < 2) {
      return F.NIL;
    }
    final int rank = dimensions.argSize();
    if (transpose.isAST1()) {
      // the default permutation exchanges the first two levels
      IASTAppendable result = dimensions.copyAppendable();
      result.set(1, dimensions.arg2());
      result.set(2, dimensions.arg1());
      return result;
    }
    if (transpose.isAST2() && transpose.arg2().isList()) {
      IAST permutation = (IAST) transpose.arg2();
      if (permutation.argSize() != rank) {
        return F.NIL;
      }
      IASTAppendable result = dimensions.copyAppendable();
      for (int i = 1; i <= rank; i++) {
        int position = permutation.get(i).toIntDefault();
        if (position < 1 || position > rank) {
          return F.NIL;
        }
        // slot i of the argument becomes slot permutation[i] of the result
        result.set(position, dimensions.get(i));
      }
      return result;
    }
    return F.NIL;
  }

  /** The dimensions of <code>ArrayDot(a, b, k)</code>, which contracts the last k slots of a. */
  private static IAST arrayDotDimensions(IAST arrayDot, EvalEngine engine, int depth) {
    if (!arrayDot.isAST3() || !arrayDot.arg3().isInteger()) {
      return F.NIL;
    }
    int k = arrayDot.arg3().toIntDefault();
    if (k < 0) {
      return F.NIL;
    }
    IAST dimensions1 = tensorDimensions(arrayDot.arg1(), engine, depth + 1);
    IAST dimensions2 = tensorDimensions(arrayDot.arg2(), engine, depth + 1);
    if (dimensions1.isNIL() || dimensions2.isNIL()) {
      return F.NIL;
    }
    if (dimensions1.argSize() < k || dimensions2.argSize() < k) {
      return F.NIL;
    }
    for (int i = 0; i < k; i++) {
      if (!dimensions1.get(dimensions1.argSize() - k + i + 1).equals(dimensions2.get(i + 1))) {
        return F.NIL;
      }
    }
    IASTAppendable result = F.ListAlloc(dimensions1.argSize() + dimensions2.argSize() - 2 * k);
    result.appendArgs(dimensions1, dimensions1.argSize() - k + 1);
    result.appendAll(dimensions2, k + 1, dimensions2.size());
    return result;
  }

  /**
   * The dimensions a symbol was given through an <code>Element(x, Vectors(...) | Matrices(...) |
   * Arrays(...))</code> assumption.
   */
  private static IAST assumedDimensions(IExpr symbol, EvalEngine engine) {
    IAssumptions assumptions = engine.getAssumptions();
    if (assumptions == null) {
      return F.NIL;
    }
    Map<IExpr, IAST> tensorsMap = assumptions.getTensorsMap();
    if (tensorsMap == null) {
      return F.NIL;
    }
    IAST domain = tensorsMap.get(symbol);
    if (domain == null) {
      return F.NIL;
    }
    if (domain.isAST(S.Vectors)) {
      return F.list(domain.arg1());
    }
    if ((domain.isAST(S.Matrices) || domain.isAST(S.Arrays)) && domain.arg1().isList()) {
      return (IAST) domain.arg1();
    }
    return F.NIL;
  }

  private static IAST dimensionsOf(int[] dimensions) {
    return F.mapRange(0, dimensions.length, i -> F.ZZ(dimensions[i]));
  }

  /**
   * The element domains which a symbolic array object accepts, in the order in which
   * <a href="https://reference.wolfram.com/language/ref/ArraySymbol.html">ArraySymbol</a> documents
   * them.
   */
  public static boolean isValidDomain(IExpr domain) {
    return domain == S.Complexes || domain == S.Integers || domain == S.Reals
        || domain == S.NonNegativeReals || domain == S.PositiveReals;
  }

  /**
   * Test if <code>dimension</code> can be used as one dimension of a symbolic array. Valid are
   * positive integers and expressions which aren't numbers at all; <code>0</code>, a negative
   * integer and an inexact or fractional number are rejected.
   *
   * @param dimension the dimension specification to test
   * @return <code>true</code> if the dimension is valid
   */
  public static boolean isValidDimension(IExpr dimension) {
    if (dimension.isInteger()) {
      return dimension.isPositive();
    }
    return !dimension.isNumber();
  }

  /**
   * Test if <code>symmetry</code> is a valid symmetry specification for an array of the given
   * dimensions. Valid are {@link S#None} and <code>Symmetric</code>, <code>Antisymmetric</code>,
   * <code>Hermitian</code>, <code>Antihermitian</code> and <code>ZeroSymmetric</code> applied to
   * {@link S#All} or to a list of distinct slot positions. The dimensions at the named slots must
   * be structurally equal, because only then can the slots be permuted.
   *
   * @param symmetry the symmetry specification to test
   * @param dimensions the dimensions of the array
   * @return <code>true</code> if the symmetry is valid for these dimensions
   */
  public static boolean isValidSymmetry(IExpr symmetry, IAST dimensions) {
    if (symmetry.isNone()) {
      return true;
    }
    if (!symmetry.isAST1()) {
      return false;
    }
    IExpr head = symmetry.head();
    if (head != S.Symmetric && head != S.Antisymmetric && head != S.Hermitian
        && head != S.Antihermitian && head != S.ZeroSymmetric) {
      return false;
    }
    final int rank = dimensions.argSize();
    IAST slots;
    if (symmetry.first() == S.All) {
      slots = F.mapRange(1, rank + 1, i -> F.ZZ(i));
    } else if (symmetry.first().isList()) {
      slots = (IAST) symmetry.first();
    } else {
      return false;
    }
    if (slots.argSize() < 2) {
      return false;
    }
    IExpr firstDimension = F.NIL;
    for (int i = 1; i < slots.size(); i++) {
      int slot = slots.get(i).toIntDefault();
      if (slot < 1 || slot > rank) {
        return false;
      }
      for (int j = 1; j < i; j++) {
        if (slots.get(j).toIntDefault() == slot) {
          // a slot position may be named only once
          return false;
        }
      }
      IExpr dimension = dimensions.get(slot);
      if (firstDimension.isNIL()) {
        firstDimension = dimension;
      } else if (!firstDimension.equals(dimension)) {
        // only slots of equal dimension can be permuted into each other
        return false;
      }
    }
    return true;
  }

  /**
   * Test if <code>expr</code> is one of the symbolic array objects {@link S#VectorSymbol},
   * {@link S#MatrixSymbol} or {@link S#ArraySymbol}.
   */
  public static boolean isArraySymbol(IExpr expr) {
    return expr instanceof IArraySymbol;
  }

  /**
   * Test if <code>expr</code> is an expression headed by one of the symbolic array constants
   * {@link S#SymbolicZerosArray}, {@link S#SymbolicOnesArray}, {@link S#SymbolicIdentityArray} or
   * {@link S#SymbolicDeltaProductArray}.
   */
  public static boolean isSymbolicArrayHead(IExpr expr) {
    return expr.isAST() && expr.headInstanceOf(ISymbolicArray.class) != null;
  }

  /**
   * Create a {@link S#SymbolicZerosArray} of the given dimensions.
   *
   * @param dimensions the dimensions, or {@link F#NIL} for an array of unspecified dimensions
   * @return <code>SymbolicZerosArray(dimensions)</code>
   */
  public static IAST zeros(IAST dimensions) {
    return dimensions.isPresent() ? F.SymbolicZerosArray(dimensions) : F.ast(S.SymbolicZerosArray);
  }

  /**
   * Create a {@link S#SymbolicOnesArray} of the given dimensions.
   *
   * @param dimensions the dimensions, or {@link F#NIL} for an array of unspecified dimensions
   * @return <code>SymbolicOnesArray(dimensions)</code>
   */
  public static IAST ones(IAST dimensions) {
    return dimensions.isPresent() ? F.SymbolicOnesArray(dimensions) : F.ast(S.SymbolicOnesArray);
  }

  /**
   * Concatenate two dimension lists, as the dimensions of a {@link S#TensorProduct} are the
   * concatenation of the dimensions of its factors.
   *
   * @return {@link F#NIL} if one of the arguments is {@link F#NIL}
   */
  public static IAST joinDimensions(IAST dimensions1, IAST dimensions2) {
    if (dimensions1.isNIL() || dimensions2.isNIL()) {
      return F.NIL;
    }
    IASTAppendable result = F.ListAlloc(dimensions1.argSize() + dimensions2.argSize());
    result.appendArgs(dimensions1);
    result.appendArgs(dimensions2);
    return result;
  }

  /**
   * Test if two dimension lists describe the same shape.
   *
   * @return <code>false</code> if one of the arguments is {@link F#NIL}
   */
  public static boolean sameDimensions(IAST dimensions1, IAST dimensions2) {
    return dimensions1.isPresent() && dimensions2.isPresent() && dimensions1.equals(dimensions2);
  }
}
