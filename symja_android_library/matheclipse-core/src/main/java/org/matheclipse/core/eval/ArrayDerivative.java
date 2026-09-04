package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IArraySymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Derivatives of and with respect to symbolic arrays.
 *
 * <p>
 * Two different derivatives live here. {@link #arrayD(IExpr, IArraySymbol, EvalEngine)} answers
 * <code>D(f, v)</code> where the differentiation variable <code>v</code> is a whole vector, matrix
 * or array; the result has the dimensions of <code>f</code> followed by those of <code>v</code>.
 * {@link #dArrayValuedInScalar(IExpr, IExpr, EvalEngine)} answers <code>D(f, x)</code> where
 * <code>x</code> is an ordinary scalar but <code>f</code> is array valued, so that the product rule
 * has to respect the order of the factors of a {@link S#Dot}.
 * </p>
 *
 * <p>
 * Every rule which does not apply answers {@link F#NIL}, so an unsupported derivative stays
 * unevaluated rather than producing a wrong scalar answer.
 * </p>
 */
public class ArrayDerivative {

  private ArrayDerivative() {
    // private constructor to avoid instantiation
  }

  /**
   * Test if <code>head</code> is one of the array operations whose derivative with respect to a
   * scalar {@link #dArrayValuedInScalar(IExpr, IExpr, EvalEngine)} knows.
   */
  public static boolean isArrayHead(IExpr head) {
    if (!head.isBuiltInSymbol()) {
      return false;
    }
    switch (((ISymbol) head).ordinal()) {
      case ID.ConjugateTranspose:
      case ID.Det:
      case ID.Dot:
      case ID.Inverse:
      case ID.MatrixPower:
      case ID.TensorProduct:
      case ID.Tr:
      case ID.Transpose:
        return true;
      default:
        return false;
    }
  }

  /**
   * The derivative of <code>fx</code> with respect to the symbolic array variable <code>x</code>.
   *
   * @param fx the expression to differentiate
   * @param x the symbolic vector, matrix or array to differentiate by
   * @param engine the evaluation engine
   * @return {@link F#NIL} if no rule applies, so that the derivative stays unevaluated
   */
  public static IExpr arrayD(IExpr fx, IArraySymbol x, EvalEngine engine) {
    final IAST variableDimensions = x.getDimensions();

    if (fx.isFree(x, true)) {
      // the derivative of a constant has the dimensions of the function followed by those of the
      // differentiation variable, and is zero throughout
      IAST functionDimensions = SymbolicArrayUtil.tensorDimensions(fx, engine);
      if (functionDimensions.isNIL()) {
        if (!containsSymbolicArray(fx)) {
          // an expression which mentions no symbolic array at all, like f(x), is read as a scalar
          functionDimensions = F.CEmptyList;
        } else {
          return F.NIL;
        }
      }
      return SymbolicArrayUtil
          .zeros(SymbolicArrayUtil.joinDimensions(functionDimensions, variableDimensions));
    }
    if (fx.equals(x)) {
      return F.SymbolicIdentityArray(variableDimensions);
    }
    if (!fx.isAST()) {
      return F.NIL;
    }
    IAST ast = (IAST) fx;

    if (fx.isPlus()) {
      // differentiation is linear
      return ast.mapThread(F.D(F.Slot1, x), 1);
    }
    if (fx.isTimes()) {
      return timesD(ast, x, engine);
    }
    if (fx.isPower()) {
      IExpr base = ast.arg1();
      IExpr exponent = ast.arg2();
      if (exponent.isFree(x, true) && isScalar(base, engine)) {
        return F.Times(exponent, F.Power(base, F.Subtract(exponent, F.C1)), F.D(base, x));
      }
      return F.NIL;
    }

    if (x.rank() == 1) {
      IExpr result = vectorD(ast, x, engine);
      if (result.isPresent()) {
        return result;
      }
    }
    if (x.rank() == 2) {
      IExpr result = matrixD(ast, x, engine);
      if (result.isPresent()) {
        return result;
      }
    }
    return F.NIL;
  }

  /** The derivative of a {@link S#Times} with respect to a symbolic array variable. */
  private static IExpr timesD(IAST times, IArraySymbol x, EvalEngine engine) {
    IASTAppendable constantFactors = F.TimesAlloc(times.argSize());
    IASTAppendable dependentFactors = F.TimesAlloc(times.argSize());
    for (int i = 1; i < times.size(); i++) {
      IExpr factor = times.get(i);
      if (factor.isFree(x, true)) {
        constantFactors.append(factor);
      } else {
        dependentFactors.append(factor);
      }
    }
    if (dependentFactors.argSize() == 1) {
      // a constant scalar factor is pulled out of the derivative
      return F.Times(constantFactors.oneIdentity1(), F.D(dependentFactors.arg1(), x));
    }
    for (int i = 1; i < dependentFactors.size(); i++) {
      if (!isScalar(dependentFactors.get(i), engine)) {
        // the product rule below multiplies the remaining factors onto the derivative, which is
        // only correct while every one of them is a scalar
        return F.NIL;
      }
    }
    IASTAppendable sum = F.PlusAlloc(dependentFactors.argSize());
    for (int i = 1; i < dependentFactors.size(); i++) {
      IASTAppendable term = F.TimesAlloc(dependentFactors.argSize() + 1);
      term.append(constantFactors.oneIdentity1());
      for (int j = 1; j < dependentFactors.size(); j++) {
        if (i != j) {
          term.append(dependentFactors.get(j));
        }
      }
      term.append(F.D(dependentFactors.get(i), x));
      sum.append(term);
    }
    return sum;
  }

  /** The derivative of an array expression with respect to a symbolic vector variable. */
  private static IExpr vectorD(IAST fx, IArraySymbol x, EvalEngine engine) {
    final IExpr length = x.getDimensions().arg1();
    if (fx.isAST(S.Dot)) {
      return dotD(fx, x, engine);
    }
    if (fx.isAST1()) {
      switch (fx.headID()) {
        case ID.Total:
          if (fx.arg1().equals(x)) {
            // every component contributes with weight one
            return F.SymbolicOnesArray(F.list(length));
          }
          break;
        case ID.Mean:
          if (fx.arg1().equals(x)) {
            return F.Divide(F.SymbolicOnesArray(F.list(length)), length);
          }
          break;
        case ID.Norm:
          if (fx.arg1().equals(x) && x.hasRealDomain()) {
            // the gradient of the euclidean norm is the unit vector in the direction of x
            return F.Divide(x, F.Norm(x));
          }
          break;
        default:
          break;
      }
    }
    return F.NIL;
  }

  /**
   * The derivative of a {@link S#Dot} chain with respect to a symbolic vector variable.
   *
   * <p>
   * A {@link S#Dot} is multilinear, so the derivative is the sum over the occurrences of the
   * variable, each with the remaining factors of the chain held constant.
   * </p>
   */
  private static IExpr dotD(IAST dot, IArraySymbol x, EvalEngine engine) {
    for (int i = 1; i < dot.size(); i++) {
      IExpr factor = dot.get(i);
      if (!factor.equals(x) && !factor.isFree(x, true)) {
        // a factor which depends on the variable without being the variable itself would need the
        // chain rule
        return F.NIL;
      }
    }
    IASTAppendable sum = F.PlusAlloc(dot.argSize());
    for (int i = 1; i < dot.size(); i++) {
      if (!dot.get(i).equals(x)) {
        continue;
      }
      IExpr left = subDot(dot, 1, i);
      IExpr right = subDot(dot, i + 1, dot.size());
      IExpr term = dotDTerm(left, right, engine);
      if (term.isNIL()) {
        return F.NIL;
      }
      sum.append(term);
    }
    return sum.argSize() == 0 ? F.NIL : sum;
  }

  /**
   * The contribution of one occurrence of the vector variable in a {@link S#Dot} chain, where
   * <code>left</code> and <code>right</code> are the products of the factors before and after it.
   */
  private static IExpr dotDTerm(IExpr left, IExpr right, EvalEngine engine) {
    final int leftRank = left.isNIL() ? 0 : SymbolicArrayUtil.rank(left, engine);
    final int rightRank = right.isNIL() ? 0 : SymbolicArrayUtil.rank(right, engine);
    if (leftRank < 0 || rightRank < 0) {
      return F.NIL;
    }
    if (left.isNIL()) {
      if (right.isNIL()) {
        return F.NIL;
      }
      // v.right: the derivative is the right factor, transposed if it is a matrix
      return rightRank == 1 ? right : (rightRank == 2 ? F.Transpose(right) : F.NIL);
    }
    if (right.isNIL()) {
      // left.v: a vector left factor is written as a column, a matrix left factor stays
      return leftRank == 1 ? columnForm(left, engine) : (leftRank == 2 ? left : F.NIL);
    }
    if (leftRank == 2 && rightRank == 1) {
      return F.Dot(F.Transpose(left), right);
    }
    if (leftRank == 1 && rightRank == 2) {
      return F.Dot(F.Transpose(right), left);
    }
    return F.NIL;
  }

  /**
   * Write a row vector product <code>w.M1.M2...</code> as the column vector product
   * <code>Transpose(Mk)...Transpose(M1).w</code>, which is the form the Wolfram Language answers a
   * vector derivative in.
   */
  private static IExpr columnForm(IExpr expr, EvalEngine engine) {
    if (expr.isAST(S.Dot) && expr.size() > 2) {
      IAST dot = (IAST) expr;
      IASTAppendable result = F.ast(S.Dot, dot.argSize());
      for (int i = dot.argSize(); i >= 2; i--) {
        result.append(F.Transpose(dot.get(i)));
      }
      result.append(dot.arg1());
      return engine.evaluate(result);
    }
    return expr;
  }

  /** The {@link S#Dot} of the factors <code>from</code> inclusive to <code>to</code> exclusive. */
  private static IExpr subDot(IAST dot, int from, int to) {
    if (from >= to) {
      return F.NIL;
    }
    if (to - from == 1) {
      return dot.get(from);
    }
    IASTAppendable result = F.ast(S.Dot, to - from);
    result.appendAll(dot, from, to);
    return result;
  }

  /** The derivative of an array expression with respect to a symbolic matrix variable. */
  private static IExpr matrixD(IAST fx, IArraySymbol x, EvalEngine engine) {
    if (!fx.isAST1() || !fx.arg1().equals(x)) {
      return F.NIL;
    }
    final IAST dimensions = x.getDimensions();
    switch (fx.headID()) {
      case ID.Tr:
        if (x.isSquareMatrix()) {
          return F.SymbolicIdentityArray(F.list(dimensions.arg1()));
        }
        return F.NIL;
      case ID.Det:
        if (x.isSquareMatrix()) {
          // Jacobi's formula for the derivative of a determinant
          return F.Times(F.Det(x), F.Transpose(F.Inverse(x)));
        }
        return F.NIL;
      case ID.Transpose: {
        // the components of D(x, x) are the products of two Kronecker deltas; transposing the
        // function exchanges the first two slots of that rank four array
        IAST identity = F.SymbolicIdentityArray(dimensions);
        return F.Transpose(identity, F.List(F.C2, F.C1, F.C3, F.C4));
      }
      default:
        return F.NIL;
    }
  }

  /**
   * The derivative of an array valued expression with respect to an ordinary scalar variable.
   *
   * <p>
   * The rules are the usual ones, but the product rule of a {@link S#Dot} must keep the factors in
   * their order, because the matrix product does not commute.
   * </p>
   *
   * @return {@link F#NIL} if no rule applies
   */
  public static IExpr dArrayValuedInScalar(IAST fx, IExpr x, EvalEngine engine) {
    switch (fx.headID()) {
      case ID.Dot: {
        IASTAppendable sum = F.PlusAlloc(fx.argSize());
        for (int i = 1; i < fx.size(); i++) {
          if (fx.get(i).isFree(x, true)) {
            continue;
          }
          IASTAppendable term = F.ast(S.Dot, fx.argSize());
          for (int j = 1; j < fx.size(); j++) {
            term.append(i == j ? F.D(fx.get(j), x) : fx.get(j));
          }
          sum.append(term);
        }
        return sum.argSize() == 0 ? F.NIL : sum;
      }
      case ID.Inverse:
        if (fx.isAST1()) {
          // differentiating Inverse(a).a == identity gives this rule
          return F.Times(F.CN1, F.Dot(F.Inverse(fx.arg1()), F.D(fx.arg1(), x), F.Inverse(fx.arg1())));
        }
        return F.NIL;
      case ID.Transpose:
      case ID.ConjugateTranspose:
        if (fx.isAST1()) {
          return F.unaryAST1(fx.head(), F.D(fx.arg1(), x));
        }
        if (fx.isAST2()) {
          return F.binaryAST2(fx.head(), F.D(fx.arg1(), x), fx.arg2());
        }
        return F.NIL;
      case ID.Tr:
        if (fx.isAST1()) {
          return F.Tr(F.D(fx.arg1(), x));
        }
        return F.NIL;
      case ID.Det:
        if (fx.isAST1()) {
          // Jacobi's formula
          return F.Times(F.Det(fx.arg1()),
              F.Tr(F.Dot(F.Inverse(fx.arg1()), F.D(fx.arg1(), x))));
        }
        return F.NIL;
      case ID.TensorProduct: {
        IASTAppendable sum = F.PlusAlloc(fx.argSize());
        for (int i = 1; i < fx.size(); i++) {
          if (fx.get(i).isFree(x, true)) {
            continue;
          }
          IASTAppendable term = F.ast(S.TensorProduct, fx.argSize());
          for (int j = 1; j < fx.size(); j++) {
            term.append(i == j ? F.D(fx.get(j), x) : fx.get(j));
          }
          sum.append(term);
        }
        return sum.argSize() == 0 ? F.NIL : sum;
      }
      case ID.MatrixPower: {
        if (!fx.isAST2()) {
          return F.NIL;
        }
        int power = fx.arg2().toIntDefault();
        if (power < 1 || power > 16 || !fx.arg2().isFree(x, true)) {
          return F.NIL;
        }
        IExpr base = fx.arg1();
        IASTAppendable sum = F.PlusAlloc(power);
        for (int j = 0; j < power; j++) {
          sum.append(F.Dot(F.MatrixPower(base, F.ZZ(j)), F.D(base, x),
              F.MatrixPower(base, F.ZZ(power - 1 - j))));
        }
        return sum;
      }
      default:
        return F.NIL;
    }
  }

  /** Test if <code>expr</code> mentions a symbolic array object or a symbolic array constant. */
  private static boolean containsSymbolicArray(IExpr expr) {
    return !expr.isFree(t -> t instanceof IArraySymbol
        || t.headInstanceOf(org.matheclipse.core.interfaces.ISymbolicArray.class) != null, false);
  }

  /** Test if <code>expr</code> is known to be a scalar, i.e. an array of rank zero. */
  private static boolean isScalar(IExpr expr, EvalEngine engine) {
    return SymbolicArrayUtil.tensorDimensions(expr, engine).isEmptyList();
  }
}
