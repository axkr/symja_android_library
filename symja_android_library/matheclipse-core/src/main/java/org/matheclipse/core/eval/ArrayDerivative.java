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
      case ID.Mean:
      case ID.TensorProduct:
      case ID.Total:
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
      return plusD(ast, x, engine);
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

    IExpr anyRankResult = anyRankD(ast, x, engine);
    if (anyRankResult.isPresent()) {
      return anyRankResult;
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

  /**
   * The derivative of a {@link S#Plus} with respect to a symbolic array variable.
   *
   * <p>
   * Differentiation is linear, with two refinements. A summand free of the variable contributes a
   * zeros array, which the sum absorbs anyway, so it is dropped - this also avoids having to know
   * its shape. And a scalar summand of a non-scalar sum, like <code>-Mean(v)</code> in
   * <code>v - Mean(v)</code>, stands for that scalar added to every component, so its derivative
   * is broadcast over the shape of the sum.
   * </p>
   */
  private static IExpr plusD(IAST plus, IArraySymbol x, EvalEngine engine) {
    final IAST plusDimensions = SymbolicArrayUtil.tensorDimensions(plus, engine);
    IASTAppendable sum = F.PlusAlloc(plus.argSize());
    for (int i = 1; i < plus.size(); i++) {
      IExpr term = plus.get(i);
      if (term.isFree(x, true)) {
        continue;
      }
      IExpr termDerivative = F.D(term, x);
      if (plusDimensions.isPresent() && plusDimensions.argSize() > 0 && isScalar(term, engine)) {
        termDerivative = F.TensorProduct(F.SymbolicOnesArray(plusDimensions), termDerivative);
      }
      sum.append(termDerivative);
    }
    return sum.oneIdentity0();
  }

  /**
   * The derivatives of the array operations whose rule does not depend on the rank of the
   * variable <code>x</code>.
   */
  private static IExpr anyRankD(IAST fx, IArraySymbol x, EvalEngine engine) {
    if (fx.argSize() < 1 || fx.argSize() > 2 || !fx.arg1().equals(x)) {
      return F.NIL;
    }
    final IAST dimensions = x.getDimensions();
    final int rank = dimensions.argSize();
    switch (fx.headID()) {
      case ID.Total:
        if (fx.isAST1()) {
          return totalD(dimensions);
        }
        return F.NIL;
      case ID.Mean:
        if (fx.isAST1()) {
          return F.Divide(totalD(dimensions), dimensions.arg1());
        }
        return F.NIL;
      case ID.Tr:
        if (fx.isAST1() && rank >= 2) {
          if (rank == 2 && dimensions.arg1().equals(dimensions.arg2())) {
            return F.SymbolicIdentityArray(F.list(dimensions.arg1()));
          }
          // Tr sums the components whose indices all agree, so the derivative is one exactly where
          // the rank indices of the component are all equal
          return F.SymbolicDeltaProductArray(dimensions, F.list(F.Range(rank)));
        }
        return F.NIL;
      case ID.Transpose: {
        if (rank < 2) {
          return F.NIL;
        }
        // the components of D(x, x) are products of Kronecker deltas pairing slot k with slot
        // rank+k; transposing the function permutes the first rank slots of that identity array.
        // Confirmed against real Mathematica (2026-09-12) for a matrix, which prints this as plain
        // Transpose[SymbolicIdentityArray[{m,n}]], not with an explicit permutation list
        final IExpr identity = F.SymbolicIdentityArray(dimensions);
        if (fx.isAST1()) {
          return F.Transpose(identity);
        }
        IAST permutation = transposePermutation(fx.arg2(), rank);
        if (permutation.isNIL()) {
          return F.NIL;
        }
        IASTAppendable extended = F.ListAlloc(2 * rank);
        extended.appendArgs(permutation);
        for (int k = rank + 1; k <= 2 * rank; k++) {
          extended.append(F.ZZ(k));
        }
        return F.Transpose(identity, extended);
      }
      default:
        return F.NIL;
    }
  }

  /**
   * The derivative of <code>Total(x)</code> by <code>x</code>. <code>Total</code> sums over the
   * first level only, so for a variable of rank <code>r</code> component <code>k</code> of the
   * result (<code>k &lt; r</code>) is paired with slot <code>k+1</code> of the variable, which is
   * slot <code>r+k</code> of the derivative.
   */
  private static IExpr totalD(IAST dimensions) {
    final int rank = dimensions.argSize();
    if (rank == 1) {
      // every component contributes with weight one
      return F.SymbolicOnesArray(dimensions);
    }
    IAST totalDimensions = dimensions.removeAtCopy(1);
    IASTAppendable pairs = F.ListAlloc(rank - 1);
    for (int k = 1; k < rank; k++) {
      pairs.append(F.list(F.ZZ(k), F.ZZ(rank + k)));
    }
    return F.SymbolicDeltaProductArray(
        SymbolicArrayUtil.joinDimensions(totalDimensions, dimensions), pairs);
  }

  /**
   * The permutation list of the second argument of <code>Transpose(a, spec)</code> for an array of
   * the given rank: a permutation list is taken as it is, and an integer <code>k</code> cycles the
   * levels, i.e. it is <code>RotateLeft(Range(rank), k)</code>.
   *
   * @return {@link F#NIL} if <code>spec</code> is not a valid permutation of that length
   */
  private static IAST transposePermutation(IExpr spec, int rank) {
    int[] permutation = new int[rank];
    if (spec.isInteger()) {
      int k = spec.toIntDefault();
      if (k == Integer.MIN_VALUE) {
        return F.NIL;
      }
      k = ((k % rank) + rank) % rank;
      for (int i = 0; i < rank; i++) {
        permutation[i] = (i + k) % rank + 1;
      }
    } else if (spec.isList() && spec.argSize() == rank) {
      for (int i = 0; i < rank; i++) {
        permutation[i] = spec.getAt(i + 1).toIntDefault();
      }
    } else {
      return F.NIL;
    }
    boolean[] seen = new boolean[rank + 1];
    IASTAppendable result = F.ListAlloc(rank);
    for (int position : permutation) {
      if (position < 1 || position > rank || seen[position]) {
        return F.NIL;
      }
      seen[position] = true;
      result.append(F.ZZ(position));
    }
    return result;
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
    if (fx.isAST2() && fx.arg1().equals(x) && fx.arg2().isFree(x, true)) {
      final IExpr r = fx.arg2();
      switch (fx.headID()) {
        case ID.Moment:
          // Moment(x, r) is Total(x^r)/n
          return F.Times(r, F.Power(length, F.CN1), F.Power(x, F.Subtract(r, F.C1)));
        case ID.CentralMoment: {
          // CentralMoment(x, r) is Total((x-mean)^r)/n, and the derivative of the mean is 1/n in
          // every component
          IExpr deviations = F.Power(F.Subtract(x, F.Mean(x)), F.Subtract(r, F.C1));
          return F.Times(r, F.Power(length, F.CN1),
              F.Subtract(deviations, F.Mean(deviations)));
        }
        default:
          break;
      }
    }
    if (fx.isAST1()) {
      switch (fx.headID()) {
        case ID.Variance:
          if (fx.arg1().equals(x) && x.hasRealDomain()) {
            // the sample variance sum((x-mean)^2)/(n-1); the derivative of the mean drops out
            // because the deviations sum to zero
            return F.Times(F.C2, F.Power(F.Subtract(length, F.C1), F.CN1),
                F.Subtract(x, F.Mean(x)));
          }
          break;
        case ID.StandardDeviation:
          if (fx.arg1().equals(x) && x.hasRealDomain()) {
            return F.Times(F.Power(F.Subtract(length, F.C1), F.CN1),
                F.Power(F.StandardDeviation(x), F.CN1), F.Subtract(x, F.Mean(x)));
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
   * A {@link S#Dot} is multilinear, so the derivative is the ordinary product rule: for each
   * occurrence of the variable, differentiate that one factor and dot the result into the
   * unchanged remaining factors, then sum over the occurrences. Confirmed against real Mathematica
   * (2026-09-12), <code>D[v.s.v, v]</code> is <code>s.v + v.s</code>, with the second term left
   * exactly as <code>v.s</code> rather than being rewritten to <code>Transpose[s].v</code>. The
   * differentiated occurrence is replaced by its Jacobian, for the variable itself
   * <code>SymbolicIdentityArray[dims(x)]</code>, which {@link S#Dot}'s own identity-array
   * absorption collapses back out of the chain. Only a vector valued chain whose differentiated
   * factor comes first needs a {@link S#Transpose}, see
   * {@link #dotDTerm(IAST, int, IArraySymbol, IExpr, EvalEngine)}.
   * </p>
   */
  private static IExpr dotD(IAST dot, IArraySymbol x, EvalEngine engine) {
    IExpr identity = F.SymbolicIdentityArray(x.getDimensions());
    IASTAppendable sum = F.PlusAlloc(dot.argSize());
    for (int i = 1; i < dot.size(); i++) {
      IExpr factor = dot.get(i);
      if (factor.isFree(x, true)) {
        // a factor free of x contributes nothing to the sum
        continue;
      }
      IASTAppendable term = dotDTerm(dot, i, x, identity, engine);
      if (term.isNIL()) {
        return F.NIL;
      }
      sum.append(engine.evaluate(term));
    }
    return sum.argSize() == 0 ? F.NIL : sum;
  }

  /**
   * The product rule term of a {@link S#Dot} for a vector valued factor <code>f</code> which depends
   * on the vector variable <code>x</code> - either <code>x</code> itself, whose Jacobian is the
   * identity array, or an expression like <code>y - X.b</code> in <code>(y - X.b).(y - X.b)</code>
   * differentiated by <code>b</code>. The Jacobian
   * <code>J = D(f, x)</code> has the dimensions of <code>f</code> followed by those of
   * <code>x</code>, so a factor at the end of the chain is simply replaced by <code>J</code>. A
   * factor at the start is first moved to the end: <code>f.w</code> equals <code>w.f</code> for a
   * vector <code>w</code>, and <code>f.m</code> equals <code>Transpose(m).f</code> for a matrix
   * <code>m</code>.
   *
   * @return {@link F#NIL} if the factor is not a vector, sits inside the chain, or has no known
   *         Jacobian
   */
  private static IASTAppendable dotDTerm(IAST dot, int position, IArraySymbol x, IExpr identity,
      EvalEngine engine) {
    final IExpr factor = dot.get(position);
    if (x.rank() != 1 || SymbolicArrayUtil.rank(factor, engine) != 1) {
      return F.NIL;
    }
    final int last = dot.argSize();
    if (position != 1 && position != last) {
      // a vector inside the chain, like b in x.b.s, contracts with both neighbours; replacing it by
      // a matrix would silently contract the neighbours with each other instead
      return F.NIL;
    }
    IExpr jacobian;
    if (factor.equals(x)) {
      jacobian = identity;
    } else {
      jacobian = engine.evaluate(F.D(factor, x));
      if (jacobian.isAST(S.D) || SymbolicArrayUtil.rank(jacobian, engine) != 2) {
        return F.NIL;
      }
    }
    IASTAppendable term = F.ast(S.Dot, dot.argSize());
    if (position == last) {
      term.appendArgs(dot, last);
      term.append(jacobian);
      return term;
    }
    IExpr rest = dot.removeAtCopy(1).oneIdentity1();
    int restRank = SymbolicArrayUtil.rank(rest, engine);
    if (restRank == 1) {
      term.append(rest);
    } else if (restRank == 2) {
      term.append(F.Transpose(rest));
    } else {
      return F.NIL;
    }
    term.append(jacobian);
    return term;
  }

  /** The derivative of an array expression with respect to a symbolic matrix variable. */
  private static IExpr matrixD(IAST fx, IArraySymbol x, EvalEngine engine) {
    if (fx.argSize() < 1 || !fx.arg1().equals(x)) {
      return F.NIL;
    }
    switch (fx.headID()) {
      case ID.Det:
        if (fx.isAST1() && x.isSquareMatrix()) {
          // Jacobi's formula for the derivative of a determinant
          return F.Times(F.Det(x), F.Transpose(F.Inverse(x)));
        }
        return F.NIL;
      case ID.Inverse:
        if (fx.isAST1() && x.isSquareMatrix()) {
          // component (i,j,k,l) is -Inverse(x)[i,k]*Inverse(x)[l,j]; in the tensor product the
          // slots are ordered (i,k,l,j), and Transpose moves slot s to position permutation[s]
          IExpr inverse = F.Inverse(x);
          return F.Times(F.CN1, F.Transpose(F.TensorProduct(inverse, inverse),
              F.List(F.C1, F.C3, F.C4, F.C2)));
        }
        return F.NIL;
      case ID.Norm:
        if (fx.isAST2() && fx.arg2().isString("Frobenius") && x.hasRealDomain()) {
          // the gradient of the Frobenius norm points in the direction of x
          return F.Divide(x, fx);
        }
        return F.NIL;
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
      case ID.Total:
      case ID.Mean:
        if (fx.isAST1() && SymbolicArrayUtil.isArrayValued(fx.arg1())) {
          // both are linear, so they commute with differentiation
          return F.unaryAST1(fx.head(), F.D(fx.arg1(), x));
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
