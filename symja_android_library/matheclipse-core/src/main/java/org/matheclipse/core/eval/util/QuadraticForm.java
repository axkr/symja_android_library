package org.matheclipse.core.eval.util;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The coefficients of a polynomial of total degree <code>2</code> in a list of variables
 * <code>v</code>:
 *
 * <pre>
 * q(v) == v^T*Q*v + c^T*v + d
 * </pre>
 *
 * <p>
 * The symmetric matrix <code>Q</code> is read off the second derivatives, the vector <code>c</code>
 * off the first derivatives at the origin and the constant <code>d</code> is the value at the
 * origin. Reading the coefficients through derivatives means the expression doesn't have to be
 * expanded first, so <code>(x-1)^2+(y-2)^2</code> is accepted as it is written.
 *
 * <p>
 * The class answers the two questions which can be decided in closed form for a quadratic:
 *
 * <ul>
 * <li>{@link #sign(EvalEngine)} tells whether <code>q</code> keeps a sign on the whole space,
 * <li>{@link #identityFactor()} recognizes the scaled squared distances
 * <code>q(v) == alpha*|v-p|^2 + k</code>, whose extrema over a ball are elementary.
 * </ul>
 *
 * @see AssumedRegion
 */
public final class QuadraticForm {

  /** <code>q</code> is greater <code>0</code> for every real point. */
  public static final int POSITIVE_DEFINITE = 1;

  /** <code>q</code> is greater equal <code>0</code> for every real point. */
  public static final int POSITIVE_SEMIDEFINITE = 0;

  /** Nothing could be determined. */
  public static final int UNDETERMINED = -1;

  /**
   * Read the coefficients of <code>expr</code> as a polynomial of total degree <code>2</code> in
   * its own variables.
   *
   * @param expr
   * @param engine
   * @return <code>null</code> if <code>expr</code> isn't a polynomial of total degree
   *         <code>2</code>
   */
  public static QuadraticForm of(IExpr expr, EvalEngine engine) {
    return of(expr, new VariablesSet(expr).getVarList(), engine);
  }

  /**
   * Read the coefficients of <code>expr</code> as a polynomial of total degree <code>2</code> in
   * the given <code>variables</code>.
   *
   * @param expr
   * @param variables the variables of the quadratic form
   * @param engine
   * @return <code>null</code> if <code>expr</code> isn't a polynomial of total degree
   *         <code>2</code> in the <code>variables</code>
   */
  public static QuadraticForm of(IExpr expr, IAST variables, EvalEngine engine) {
    final int n = variables.argSize();
    if (n < 1) {
      return null;
    }
    IASTAppendable zeroRules = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      zeroRules.append(F.Rule(variables.get(i), F.C0));
    }
    final IExpr[][] matrix = new IExpr[n][n];
    final IExpr[] linear = new IExpr[n];
    for (int i = 1; i <= n; i++) {
      final IExpr derivative = engine.evaluate(F.D(expr, variables.get(i)));
      for (int j = i; j <= n; j++) {
        // 1/2 * D(q, v_i, v_j)
        IExpr entry = engine.evaluate(F.Times(F.C1D2, F.D(derivative, variables.get(j))));
        if (!entry.isNumber()) {
          // a second derivative which still depends on a variable means a degree above 2
          return null;
        }
        matrix[i - 1][j - 1] = entry;
        matrix[j - 1][i - 1] = entry;
      }
      IExpr coefficient = engine.evaluate(F.subst(derivative, zeroRules));
      if (!coefficient.isNumber()) {
        return null;
      }
      linear[i - 1] = coefficient;
    }
    IExpr constant = engine.evaluate(F.subst(expr, zeroRules));
    if (!constant.isNumber()) {
      return null;
    }
    return new QuadraticForm(variables, matrix, linear, constant);
  }

  private final IAST variables;

  private final IExpr[][] matrix;

  private final IExpr[] linear;

  private final IExpr constant;

  private QuadraticForm(IAST variables, IExpr[][] matrix, IExpr[] linear, IExpr constant) {
    this.variables = variables;
    this.matrix = matrix;
    this.linear = linear;
    this.constant = constant;
  }

  /**
   * The bordered matrix <code>M == {{Q, c/2}, {c^T/2, d}}</code> of the quadratic form, for which
   * <code>q(v) == {v,1}^T*M*{v,1}</code> holds.
   *
   * @return an <code>(n+1)x(n+1)</code> matrix
   */
  public IAST borderedMatrix() {
    final int n = size();
    IASTAppendable result = F.ListAlloc(n + 1);
    for (int i = 0; i <= n; i++) {
      IASTAppendable row = F.ListAlloc(n + 1);
      for (int j = 0; j <= n; j++) {
        if (i < n && j < n) {
          row.append(matrix[i][j]);
        } else if (i < n) {
          row.append(F.Times(F.C1D2, linear[i]));
        } else if (j < n) {
          row.append(F.Times(F.C1D2, linear[j]));
        } else {
          row.append(constant);
        }
      }
      result.append(row);
    }
    return result;
  }

  /**
   * The center <code>p == -c/(2*alpha)</code> of the scaled squared distance
   * <code>q(v) == alpha*|v-p|^2 + k</code>.
   *
   * @param alpha the value of {@link #identityFactor()}
   * @param engine
   * @return the coordinates of the center
   */
  public IExpr[] center(IExpr alpha, EvalEngine engine) {
    final int n = size();
    final IExpr[] center = new IExpr[n];
    for (int i = 0; i < n; i++) {
      center[i] = engine.evaluate(F.Divide(F.Times(F.CN1D2, linear[i]), alpha));
    }
    return center;
  }

  /** The constant <code>d</code>, i.e. the value of <code>q</code> at the origin. */
  public IExpr constant() {
    return constant;
  }

  /**
   * The factor <code>alpha</code> if the quadratic part is a multiple of the identity, i.e. if
   * <code>q</code> is the scaled squared distance
   * <code>q(v) == alpha*|v-center()|^2 + offset()</code>.
   *
   * @return {@link F#NIL} if the quadratic part isn't a non zero multiple of the identity
   */
  public IExpr identityFactor() {
    final int n = size();
    final IExpr alpha = matrix[0][0];
    if (alpha.isZero()) {
      return F.NIL;
    }
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (i == j) {
          if (!matrix[i][j].equals(alpha)) {
            return F.NIL;
          }
        } else if (!matrix[i][j].isZero()) {
          return F.NIL;
        }
      }
    }
    return alpha;
  }

  /**
   * The offset <code>k == d - |c|^2/(4*alpha)</code>, i.e. the extremal value of the scaled squared
   * distance <code>q(v) == alpha*|v-p|^2 + k</code>.
   *
   * @param alpha the value of {@link #identityFactor()}
   * @param engine
   * @return
   */
  public IExpr offset(IExpr alpha, EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(size() + 1);
    sum.append(constant);
    for (int i = 0; i < size(); i++) {
      sum.append(F.Divide(F.Times(F.CN1D4, F.Sqr(linear[i])), alpha));
    }
    return engine.evaluate(sum);
  }

  /**
   * Determine the sign which <code>q</code> keeps on the whole real space.
   *
   * <p>
   * Because <code>q(v) == {v,1}^T*M*{v,1}</code> holds for the bordered matrix <code>M</code>,
   * <code>q</code> is greater equal <code>0</code> everywhere exactly if <code>M</code> is positive
   * semidefinite and greater <code>0</code> everywhere exactly if <code>M</code> is positive
   * definite.
   *
   * @param engine
   * @return {@link #POSITIVE_DEFINITE}, {@link #POSITIVE_SEMIDEFINITE} or {@link #UNDETERMINED}
   */
  public int sign(EvalEngine engine) {
    IAST borderedMatrix = borderedMatrix();
    if (S.PositiveDefiniteMatrixQ.ofQ(engine, borderedMatrix)) {
      return POSITIVE_DEFINITE;
    }
    if (S.PositiveSemidefiniteMatrixQ.ofQ(engine, borderedMatrix)) {
      return POSITIVE_SEMIDEFINITE;
    }
    return UNDETERMINED;
  }

  /** The number of variables of the quadratic form. */
  public int size() {
    return variables.argSize();
  }

  /** The variables of the quadratic form. */
  public IAST variables() {
    return variables;
  }
}
