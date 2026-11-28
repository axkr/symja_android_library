package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Polynomial sequences and Boolean quantifiers: <code>SubresultantPolynomials</code>,
 * <code>SubresultantPolynomialRemainders</code>, <code>PowerSymmetricPolynomial</code>,
 * <code>AugmentedSymmetricPolynomial</code>, <code>Conjunction</code>, <code>Disjunction</code>,
 * <code>UnateQ</code> and <code>TensorExpand</code>.
 */
public final class AlgebraLogicFunctions {

  /** The most terms a symmetric polynomial is built from. */
  private static final int MAX_TERMS = 100_000;

  /** The most Boolean variables a truth table is built for. */
  private static final int MAX_BOOLEAN_VARIABLES = 16;

  private static class Initializer {

    private static void init() {
      S.SubresultantPolynomials.setEvaluator(new SubresultantPolynomials());
      S.SubresultantPolynomialRemainders.setEvaluator(new SubresultantPolynomialRemainders());
      S.PowerSymmetricPolynomial.setEvaluator(new PowerSymmetricPolynomial());
      S.AugmentedSymmetricPolynomial.setEvaluator(new AugmentedSymmetricPolynomial());
      S.Conjunction.setEvaluator(new BooleanQuantifier(true));
      S.Disjunction.setEvaluator(new BooleanQuantifier(false));
      S.UnateQ.setEvaluator(new UnateQ());
      S.TensorExpand.setEvaluator(new TensorExpand());
    }
  }

  // ---------------------------------------------------------------------------------------------
  // subresultants
  // ---------------------------------------------------------------------------------------------

  /**
   * The coefficient arithmetic of the subresultant functions: expanded coefficients, or
   * coefficients reduced modulo an integer.
   */
  private static final class CoefficientRing {
    final IExpr modulus;
    final EvalEngine engine;

    CoefficientRing(IExpr modulus, EvalEngine engine) {
      this.modulus = modulus.isInteger() && !modulus.isZero() ? modulus : F.NIL;
      this.engine = engine;
    }

    boolean isModular() {
      return modulus.isPresent();
    }

    IExpr normalize(IExpr e) {
      IExpr expanded = engine.evaluate(F.Expand(e));
      return isModular() ? engine.evaluate(F.PolynomialMod(expanded, modulus)) : expanded;
    }

    /** The exact quotient of two coefficients. */
    IExpr divide(IExpr numerator, IExpr denominator) {
      if (denominator.isOne()) {
        return numerator;
      }
      if (isModular()) {
        IExpr inverse = engine.evaluate(F.PowerMod(denominator, F.CN1, modulus));
        return normalize(F.Times(numerator, inverse));
      }
      return engine.evaluate(F.Expand(F.Cancel(F.Divide(numerator, denominator))));
    }

    IExpr power(IExpr base, int exponent) {
      return exponent == 0 ? F.C1 : normalize(F.Power(base, F.ZZ(exponent)));
    }
  }

  /**
   * The coefficients of <code>poly</code> in <code>x</code>, constant term first, without trailing
   * zeros (the zero polynomial has none), or <code>null</code> if <code>poly</code> isn't a
   * polynomial in <code>x</code> with exact coefficients.
   */
  private static IExpr[] coefficients(IExpr poly, IExpr x, EvalEngine engine) {
    if (!poly.isFree(e -> e.isInexactNumber(), false)) {
      return null;
    }
    IExpr expanded = F.evalExpandAll(poly, engine);
    if (!expanded.isPolynomial(x)) {
      return null;
    }
    IExpr list = engine.evaluate(F.CoefficientList(expanded, x));
    if (!list.isList()) {
      return null;
    }
    IExpr[] result = new IExpr[list.argSize()];
    for (int i = 0; i < result.length; i++) {
      result[i] = ((IAST) list).get(i + 1);
    }
    return trim(result);
  }

  private static IExpr[] trim(IExpr[] coefficients) {
    int length = coefficients.length;
    while (length > 0 && coefficients[length - 1].isZero()) {
      length--;
    }
    return length == coefficients.length ? coefficients : Arrays.copyOf(coefficients, length);
  }

  /** The polynomial with the ascending <code>coefficients</code> in <code>x</code>. */
  private static IExpr polynomial(IExpr[] coefficients, IExpr x, EvalEngine engine) {
    IASTAppendable sum = F.PlusAlloc(coefficients.length);
    for (int i = 0; i < coefficients.length; i++) {
      sum.append(F.Times(coefficients[i], F.Power(x, F.ZZ(i))));
    }
    return engine.evaluate(F.Expand(sum.oneIdentity0()));
  }

  /**
   * The ascending coefficients of the polynomials <code>arg1</code> and <code>arg2</code> in the
   * variable <code>arg3</code>.
   *
   * @return <code>{a, b}</code>, or <code>null</code> after a message if <code>arg3</code> isn't a
   *         variable or an argument isn't a polynomial with exact coefficients
   */
  private static IExpr[][] coefficientPair(IAST ast, IBuiltInSymbol head, EvalEngine engine) {
    IExpr x = Validate.checkIsVariable(ast, 3, engine);
    if (x.isNIL()) {
      return null;
    }
    IExpr[] a = coefficients(ast.arg1(), x, engine);
    IExpr[] b = coefficients(ast.arg2(), x, engine);
    if (a == null || b == null) {
      npolys(head, ast.arg1(), ast.arg2(), x, engine);
      return null;
    }
    return new IExpr[][] {a, b};
  }

  /** <code>Errors.printMessage(head, "npolys", ...)</code> */
  private static IExpr npolys(IBuiltInSymbol head, IExpr p, IExpr q, IExpr x, EvalEngine engine) {
    // `1` and `2` should be polynomials with exact coefficients and the degree of `1` in `3`
    // should not be less than the degree of `2` in `3`.
    return Errors.printMessage(head, "npolys", F.list(p, q, x), engine);
  }

  /**
   * <code>SubresultantPolynomials(p, q, x)</code> - the subresultant polynomials
   * <code>S_0, ..., S_n</code> of <code>p</code> and <code>q</code>, where <code>n</code> is the
   * degree of <code>q</code>. The coefficient of <code>x^j</code> in <code>S_j</code> is the
   * <code>j</code>-th principal subresultant coefficient of <code>Subresultants</code>.
   */
  private static final class SubresultantPolynomials extends AbstractFunctionOptionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr[][] ab = coefficientPair(ast, S.SubresultantPolynomials, engine);
      if (ab == null) {
        return F.NIL;
      }
      IExpr p = ast.arg1();
      IExpr q = ast.arg2();
      IExpr x = ast.arg3();
      IExpr[] a = ab[0];
      IExpr[] b = ab[1];
      if (b.length == 0) {
        // the zero polynomial has no subresultants
        return F.CEmptyList;
      }
      int m = a.length - 1;
      int n = b.length - 1;
      if (m < n) {
        npolys(S.SubresultantPolynomials, p, q, x, engine);
        return F.NIL;
      }
      CoefficientRing ring = new CoefficientRing(options[0], engine);
      IASTAppendable result = F.ListAlloc(n + 1);
      for (int j = 0; j < n; j++) {
        IASTAppendable sum = F.PlusAlloc(j + 1);
        for (int k = 0; k <= j; k++) {
          engine.checkTimeLimit();
          IExpr det = engine.evaluate(F.Expand(F.Det(subresultantMatrix(a, b, j, k))));
          sum.append(F.Times(det, F.Power(x, F.ZZ(k))));
        }
        IExpr sj = engine.evaluate(F.Expand(sum.oneIdentity0()));
        result.append(ring.isModular() ? ring.normalize(sj) : sj);
      }
      IExpr[] bReduced = b;
      if (ring.isModular()) {
        bReduced = new IExpr[b.length];
        for (int i = 0; i < b.length; i++) {
          bReduced[i] = ring.normalize(b[i]);
        }
      }
      IExpr lc = b[n];
      IExpr qPolynomial = polynomial(bReduced, x, engine);
      if (m == n) {
        result.append(engine.evaluate(F.Times(F.Power(lc, F.CN1), qPolynomial)));
      } else {
        IExpr last = engine.evaluate(F.Expand(F.Times(F.Power(lc, F.ZZ(m - n - 1)), qPolynomial)));
        result.append(ring.isModular() ? ring.normalize(last) : last);
      }
      return result;
    }

    /**
     * The matrix whose rows are the coefficients of <code>x^(n-j-1)*a, ..., a</code> and
     * <code>x^(m-j-1)*b, ..., b</code>, with the columns of the degrees
     * <code>m+n-j-1, ..., j+1</code> and last the column of the degree <code>k</code>.
     */
    private static IAST subresultantMatrix(IExpr[] a, IExpr[] b, int j, int k) {
      int m = a.length - 1;
      int n = b.length - 1;
      int size = m + n - 2 * j;
      int top = m + n - j - 1;
      IASTAppendable matrix = F.ListAlloc(size);
      appendRows(matrix, a, n - j, size, top, k);
      appendRows(matrix, b, m - j, size, top, k);
      return matrix;
    }

    private static void appendRows(IASTAppendable matrix, IExpr[] c, int shifts, int size, int top,
        int k) {
      for (int i = 0; i < shifts; i++) {
        int s = shifts - 1 - i;
        IASTAppendable row = F.ListAlloc(size);
        for (int column = 0; column < size - 1; column++) {
          row.append(coefficient(c, top - column - s));
        }
        row.append(coefficient(c, k - s));
        matrix.append(row);
      }
    }

    private static IExpr coefficient(IExpr[] c, int index) {
      return index >= 0 && index < c.length ? c[index] : F.C0;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.Modulus}, new IExpr[] {F.C0});
    }
  }

  /**
   * <code>SubresultantPolynomialRemainders(p, q, x)</code> - the subresultant polynomial remainder
   * sequence of <code>p</code> and <code>q</code>: the two polynomials followed by the
   * pseudo-remainders divided by the factors <code>beta</code> of the subresultant algorithm, up to
   * the last non-zero remainder.
   */
  private static final class SubresultantPolynomialRemainders
      extends AbstractFunctionOptionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr[][] ab = coefficientPair(ast, S.SubresultantPolynomialRemainders, engine);
      if (ab == null) {
        return F.NIL;
      }
      IExpr p = ast.arg1();
      IExpr q = ast.arg2();
      IExpr x = ast.arg3();
      IExpr[] a = ab[0];
      IExpr[] b = ab[1];
      CoefficientRing ring = new CoefficientRing(options[0], engine);
      a = reduce(a, ring);
      b = reduce(b, ring);
      if (b.length == 0) {
        return F.list(polynomial(a, x, engine), F.C0);
      }
      if (a.length < b.length) {
        npolys(S.SubresultantPolynomialRemainders, p, q, x, engine);
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc();
      result.append(polynomial(a, x, engine));
      result.append(polynomial(b, x, engine));

      IExpr[] r0 = a;
      IExpr[] r1 = b;
      int delta = degree(r0) - degree(r1);
      IExpr beta = (delta % 2 == 0) ? F.CN1 : F.C1;
      IExpr psi = F.CN1;
      while (degree(r1) > 0) {
        engine.checkTimeLimit();
        IExpr[] remainder = pseudoRemainder(r0, r1, ring);
        if (remainder.length == 0) {
          break;
        }
        IExpr[] r2 = new IExpr[remainder.length];
        for (int i = 0; i < remainder.length; i++) {
          r2[i] = ring.divide(remainder[i], beta);
        }
        r2 = trim(r2);
        result.append(polynomial(r2, x, engine));
        IExpr lc = r1[r1.length - 1];
        if (delta > 0) {
          psi = ring.divide(ring.power(F.Negate(lc), delta), ring.power(psi, delta - 1));
        }
        delta = degree(r1) - degree(r2);
        beta = ring.normalize(F.Times(F.CN1, lc, ring.power(psi, delta)));
        r0 = r1;
        r1 = r2;
      }
      return result;
    }

    private static IExpr[] reduce(IExpr[] c, CoefficientRing ring) {
      if (!ring.isModular()) {
        return c;
      }
      IExpr[] result = new IExpr[c.length];
      for (int i = 0; i < c.length; i++) {
        result[i] = ring.normalize(c[i]);
      }
      return trim(result);
    }

    private static int degree(IExpr[] c) {
      return c.length - 1;
    }

    /**
     * The pseudo-remainder <code>lc(b)^(deg(a)-deg(b)+1)*a</code> modulo <code>b</code>.
     */
    private static IExpr[] pseudoRemainder(IExpr[] a, IExpr[] b, CoefficientRing ring) {
      int db = degree(b);
      IExpr lcb = b[db];
      IExpr[] r = a;
      int e = degree(a) - db + 1;
      while (r.length > 0 && degree(r) >= db) {
        int shift = degree(r) - db;
        IExpr lr = r[r.length - 1];
        IExpr[] next = new IExpr[r.length];
        for (int i = 0; i < r.length; i++) {
          IExpr term = F.Times(lcb, r[i]);
          if (i >= shift && i - shift <= db) {
            term = F.Subtract(term, F.Times(lr, b[i - shift]));
          }
          next[i] = ring.normalize(term);
        }
        next[next.length - 1] = F.C0;
        r = trim(next);
        e--;
      }
      if (e > 0) {
        IExpr factor = ring.power(lcb, e);
        for (int i = 0; i < r.length; i++) {
          r[i] = ring.normalize(F.Times(factor, r[i]));
        }
        r = trim(r);
      }
      return r;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.Modulus}, new IExpr[] {F.C0});
    }
  }

  // ---------------------------------------------------------------------------------------------
  // symmetric polynomials
  // ---------------------------------------------------------------------------------------------

  /**
   * <code>PowerSymmetricPolynomial(r, {x1, ..., xn})</code> - the power sum
   * <code>x1^r + ... + xn^r</code>; <code>PowerSymmetricPolynomial({r1, ..., rk}, data)</code> -
   * the sum over the rows <code>{x1, ..., xk}</code> of <code>data</code> of
   * <code>x1^r1*...*xk^rk</code>. The one-argument form is a formal symbol.
   */
  private static final class PowerSymmetricPolynomial extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST2() || !ast.arg2().isList()) {
        return F.NIL;
      }
      IExpr r = ast.arg1();
      IAST data = (IAST) ast.arg2();
      IASTAppendable sum = F.PlusAlloc(data.argSize());
      if (r.isList()) {
        IAST exponents = (IAST) r;
        if (exponents.exists(e -> e.isNumber() && e.isNegative())) {
          return F.NIL;
        }
        for (IExpr row : data) {
          if (!row.isList() || row.argSize() != exponents.argSize()) {
            return F.NIL;
          }
          IASTAppendable product = F.TimesAlloc(exponents.argSize());
          for (int i = 1; i <= exponents.argSize(); i++) {
            product.append(F.Power(((IAST) row).get(i), exponents.get(i)));
          }
          sum.append(product.oneIdentity1());
        }
      } else {
        if (r.isNumber() && r.isNegative()) {
          return F.NIL;
        }
        for (IExpr x : data) {
          sum.append(F.Power(x, r));
        }
      }
      return engine.evaluate(sum.oneIdentity0());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>AugmentedSymmetricPolynomial({r1, ..., rm}, {x1, ..., xn})</code> - the sum over all
   * ordered <code>m</code>-tuples of distinct variables of <code>x_i1^r1*...*x_im^rm</code>. With
   * exponent lists <code>{{r11, ..., r1k}, ...}</code> the variables are the rows of a matrix. The
   * one-argument form is a formal symbol.
   */
  private static final class AugmentedSymmetricPolynomial extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST2() || !ast.arg1().isList() || !ast.arg2().isList()) {
        return F.NIL;
      }
      IAST partition = (IAST) ast.arg1();
      IAST data = (IAST) ast.arg2();
      int m = partition.argSize();
      int n = data.argSize();
      boolean tuples = m > 0 && partition.arg1().isList();
      int width = tuples ? partition.arg1().argSize() : 1;
      for (IExpr part : partition) {
        if (tuples) {
          if (!part.isList() || part.argSize() != width
              || !((IAST) part).forAll(e -> e.isInteger() && !e.isNegative())) {
            return F.NIL;
          }
        } else if (!part.isInteger() || !part.isPositive()) {
          return F.NIL;
        }
      }
      if (tuples && !data.forAll(row -> row.isList() && row.argSize() == width)) {
        return F.NIL;
      }
      if (m == 0) {
        return F.C1;
      }
      if (m > n) {
        return F.C0;
      }
      double count = 1.0;
      for (int i = 0; i < m; i++) {
        count *= (n - i);
      }
      if (count > MAX_TERMS) {
        return F.NIL;
      }
      IASTAppendable sum = F.PlusAlloc((int) count);
      tuples(partition, data, tuples, new int[m], new boolean[n], 0, sum, engine);
      return engine.evaluate(sum);
    }

    private static void tuples(IAST partition, IAST data, boolean tuples, int[] indices,
        boolean[] used, int depth, IASTAppendable sum, EvalEngine engine) {
      int m = indices.length;
      if (depth == m) {
        IASTAppendable product = F.TimesAlloc(m);
        for (int j = 0; j < m; j++) {
          IExpr variable = data.get(indices[j] + 1);
          IExpr exponent = partition.get(j + 1);
          if (tuples) {
            for (int l = 1; l <= exponent.argSize(); l++) {
              product.append(F.Power(variable.getAt(l), exponent.getAt(l)));
            }
          } else {
            product.append(F.Power(variable, exponent));
          }
        }
        sum.append(product.oneIdentity1());
        return;
      }
      engine.checkTimeLimit();
      for (int i = 0; i < used.length; i++) {
        if (!used[i]) {
          used[i] = true;
          indices[depth] = i;
          tuples(partition, data, tuples, indices, used, depth + 1, sum, engine);
          used[i] = false;
        }
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // Boolean functions
  // ---------------------------------------------------------------------------------------------

  /** The variables of a variable specification: a list or a single expression. */
  private static IAST variableList(IExpr spec) {
    return spec.isList() ? (IAST) spec : F.list(spec);
  }

  /**
   * The values of <code>expr</code> for all True/False assignments of the <code>variables</code>.
   * In assignment <code>i</code> the variable <code>j</code> is True when bit <code>j</code> of
   * <code>i</code> is set.
   */
  private static IExpr[] truthTable(IExpr expr, IAST variables, EvalEngine engine) {
    int n = variables.argSize();
    IExpr[] table = new IExpr[1 << n];
    for (int i = 0; i < table.length; i++) {
      engine.checkTimeLimit();
      IASTAppendable rules = F.ListAlloc(n);
      for (int j = 0; j < n; j++) {
        rules.append(F.Rule(variables.get(j + 1), F.booleSymbol((i & (1 << j)) != 0)));
      }
      table[i] = engine.evaluate(F.ReplaceAll(expr, rules));
    }
    return table;
  }

  /**
   * <code>Conjunction(expr, {x1, ..., xn})</code> - the And of <code>expr</code> over all
   * True/False assignments of the variables, <code>Disjunction</code> the Or; both minimized.
   */
  private static final class BooleanQuantifier extends AbstractFunctionEvaluator {
    final boolean conjunction;

    BooleanQuantifier(boolean conjunction) {
      this.conjunction = conjunction;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IAST variables = variableList(ast.arg2());
      if (variables.argSize() > MAX_BOOLEAN_VARIABLES) {
        return F.NIL;
      }
      IExpr[] table = truthTable(ast.arg1(), variables, engine);
      IASTAppendable combined = F.ast(conjunction ? S.And : S.Or, table.length);
      for (IExpr value : table) {
        combined.append(value);
      }
      IExpr result = engine.evaluate(combined);
      if (result.isTrue() || result.isFalse()) {
        return result;
      }
      return engine.evaluate(F.unaryAST1(S.BooleanMinimize, result));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>UnateQ(bf)</code> - True if the Boolean function <code>bf</code> is positive unate
   * (monotone increasing) in all its variables, <code>UnateQ(bf, {x1, ...})</code> in the given
   * variables.
   */
  private static final class UnateQ extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr bf = ast.arg1();
      IExpr variablesExpr = engine.evaluate(F.unaryAST1(S.BooleanVariables, bf));
      if (!variablesExpr.isList()) {
        return F.NIL;
      }
      IAST variables = (IAST) variablesExpr;
      int n = variables.argSize();
      if (n == 0) {
        return S.True;
      }
      if (n > MAX_BOOLEAN_VARIABLES) {
        return F.NIL;
      }
      List<Integer> checked = new ArrayList<Integer>();
      if (ast.isAST2()) {
        IAST selected = variableList(ast.arg2());
        for (IExpr x : selected) {
          int index = variables.indexOf(x);
          if (index > 0) {
            checked.add(index - 1);
          }
        }
      } else {
        for (int j = 0; j < n; j++) {
          checked.add(j);
        }
      }
      IExpr[] table = truthTable(bf, variables, engine);
      for (IExpr value : table) {
        if (!value.isTrue() && !value.isFalse()) {
          return F.NIL;
        }
      }
      for (int j : checked) {
        int bit = 1 << j;
        for (int i = 0; i < table.length; i++) {
          if ((i & bit) == 0 && table[i].isTrue() && table[i | bit].isFalse()) {
            return S.False;
          }
        }
      }
      return S.True;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  // ---------------------------------------------------------------------------------------------
  // tensors
  // ---------------------------------------------------------------------------------------------

  /**
   * <code>TensorExpand(texpr)</code> - distribute <code>TensorProduct</code>, <code>Dot</code> and
   * <code>TensorWedge</code> over sums in each argument, pull numbers out of their arguments,
   * distribute the linear <code>TensorContract</code> and <code>TensorTranspose</code> over sums,
   * and distribute products over sums.
   */
  private static final class TensorExpand extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return expand(ast.arg1(), engine);
    }

    private static boolean isMultilinear(IExpr expr) {
      return expr.isAST(S.TensorProduct) || expr.isAST(S.Dot) || expr.isAST(S.TensorWedge);
    }

    private static boolean isLinear(IExpr expr) {
      return (expr.isAST(S.TensorContract, 3) || expr.isAST(S.TensorTranspose, 3));
    }

    private static boolean hasTensorOperation(IExpr expr) {
      return !expr.isFree(e -> isMultilinear(e) || isLinear(e), true);
    }

    private static IExpr expand(IExpr expr, EvalEngine engine) {
      engine.checkTimeLimit();
      if (!expr.isAST()) {
        return expr;
      }
      IAST ast = (IAST) expr;
      if (ast.isPlus()) {
        return engine.evaluate(ast.map(x -> expand(x, engine), 1));
      }
      if (ast.isTimes()) {
        // products are distributed over sums, also without a tensor operation
        List<List<IExpr>> factors = new ArrayList<List<IExpr>>();
        for (IExpr factor : ast) {
          factors.add(summands(expand(factor, engine)));
        }
        return distribute(S.Times, factors, false, engine);
      }
      if (!hasTensorOperation(ast)) {
        return expr;
      }
      if (isMultilinear(ast)) {
        List<List<IExpr>> slots = new ArrayList<List<IExpr>>();
        for (IExpr argument : ast) {
          slots.add(summands(expand(argument, engine)));
        }
        return distribute(ast.head(), slots, true, engine);
      }
      if (isLinear(ast)) {
        IExpr tensor = expand(ast.arg1(), engine);
        if (tensor.isPlus()) {
          return engine.evaluate(((IAST) tensor).map(t -> ast.setAtCopy(1, t), 1));
        }
        return engine.evaluate(ast.setAtCopy(1, tensor));
      }
      return engine.evaluate(ast.map(x -> expand(x, engine), 1));
    }

    private static List<IExpr> summands(IExpr expr) {
      List<IExpr> result = new ArrayList<IExpr>();
      if (expr.isPlus()) {
        for (IExpr term : (IAST) expr) {
          result.add(term);
        }
      } else {
        result.add(expr);
      }
      return result;
    }

    /**
     * The sum of <code>head(t1, ..., tk)</code> over all choices of a summand <code>ti</code> from
     * every slot. If <code>pullNumbers</code> is set, the number factor of every argument is moved
     * in front of the term.
     */
    private static IExpr distribute(IExpr head, List<List<IExpr>> slots, boolean pullNumbers,
        EvalEngine engine) {
      IASTAppendable sum = F.PlusAlloc();
      int[] choice = new int[slots.size()];
      while (true) {
        engine.checkTimeLimit();
        IASTAppendable term = F.ast(head, slots.size());
        IASTAppendable factor = F.TimesAlloc(2);
        for (int i = 0; i < choice.length; i++) {
          IExpr t = slots.get(i).get(choice[i]);
          if (pullNumbers && t.isTimes() && t.first().isNumber()) {
            factor.append(t.first());
            t = ((IAST) t).rest().oneIdentity1();
          }
          term.append(t);
        }
        factor.append(term);
        sum.append(factor.oneIdentity1());
        int i = choice.length - 1;
        while (i >= 0 && ++choice[i] == slots.get(i).size()) {
          choice[i] = 0;
          i--;
        }
        if (i < 0) {
          break;
        }
      }
      return engine.evaluate(sum.oneIdentity0());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private AlgebraLogicFunctions() {}
}
