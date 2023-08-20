package org.matheclipse.core.polynomials.symbolicexponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>ExpVectorSymbolic</code> implements exponent vectors for polynomials using arrays of <code>
 * IExpr</code> as storage unit.
 */
public final class ExpVectorSymbolic {

  /** The data structure is an array of longs. */
  /* package */ final IExpr[] val;

  int hash;

  /**
   * Constructor for ExpVectorLong.
   *
   * @param n length of exponent vector.
   */
  public ExpVectorSymbolic(int n) {
    super();
    val = new IExpr[n];
    for (int i = 0; i < n; i++) {
      val[i] = F.C0;
    }
  }

  /**
   * Constructor for ExpVectorLong. Sets exponent i to e.
   *
   * @param n length of exponent vector.
   * @param i index of exponent to be set.
   * @param e exponent to be set.
   */
  public ExpVectorSymbolic(int n, int i, IExpr e) {
    this(n);
    val[i] = e;
  }

  /**
   * Constructor for ExpVectorLong. Sets val.
   *
   * @param v internal representation array.
   */
  public ExpVectorSymbolic(IExpr[] v) {
    super();
    if (v == null) {
      throw new IllegalArgumentException("null val not allowed");
    }
    val = Arrays.copyOf(v, v.length); // > Java-5
  }

  /**
   * Clone this.
   *
   * @see java.lang.Object#clone()
   */
  public ExpVectorSymbolic copy() {
    IExpr[] w = new IExpr[val.length];
    System.arraycopy(val, 0, w, 0, val.length);
    return new ExpVectorSymbolic(w);
  }

  /**
   * Factory constructor for ExpVector. Sets val.
   *
   * @param v collection of exponents.
   */
  public static ExpVectorSymbolic create(Collection<IExpr> v) {
    IExpr[] w = new IExpr[v.size()];
    int i = 0;
    for (IExpr k : v) {
      w[i++] = k;
    }
    return new ExpVectorSymbolic(w);
  }

  /**
   * Get the exponent vector.
   *
   * @return val.
   */
  /* package */ public IExpr[] getVal() {
    return val;
  }

  /**
   * Get the exponent at position i.
   *
   * @param i position.
   * @return val[i].
   */
  public IExpr getVal(int i) {
    return val[i];
  }

  /**
   * Set the exponent at position i to e.
   *
   * @param i
   * @param e
   * @return old val[i].
   */
  protected IExpr setVal(int i, IExpr e) {
    IExpr x = val[i];
    val[i] = e;
    hash = 0; // beware of race condition
    return x;
  }

  /**
   * Is ExpVector zero.
   *
   * @return If this has all elements 0 then true is returned, else false.
   */
  public boolean isZERO() {
    return (0 == this.signum());
  }

  /**
   * Is this structure finite or infinite.
   *
   * @return true if this structure is finite, else false.
   * @see edu.jas.structure.ElemFactory#isFinite() <b>Note: </b> returns true because of finite set
   *      of values in each index.
   */
  public boolean isFinite() {
    return true;
  }

  /**
   * Standard variable names. Generate standard names for variables, i.e. x0 to x(n-1).
   *
   * @return standard names.
   */
  public IAST stdVars() {
    return STDVARS("x", length());
  }

  /**
   * Generate variable names. Generate names for variables, i.e. prefix0 to prefix(n-1).
   *
   * @param prefix name prefix.
   * @return standard names.
   */
  public IAST stdVars(String prefix) {
    return STDVARS(prefix, length());
  }

  /**
   * Standard variable names. Generate standard names for variables, i.e. x0 to x(n-1).
   *
   * @param n size of names array
   * @return standard names.
   */
  public static IAST STDVARS(int n) {
    return STDVARS("x", n);
  }

  /**
   * Generate variable names. Generate names for variables from given prefix. i.e. prefix0 to
   * prefix(n-1).
   *
   * @param n size of names array.
   * @param prefix name prefix.
   * @return vatiable names.
   */
  public static IAST STDVARS(String prefix, int n) {
    final String pref = (prefix == null || prefix.length() == 0) ? "x" : prefix;
    return F.mapRange(0, n, i -> F.Dummy(pref + i));
  }

  /**
   * Get the length of this exponent vector.
   *
   * @return val.length.
   */
  public int length() {
    return val.length;
  }

  /**
   * Extend variables. Used e.g. in module embedding. Extend this by i elements and set val[j] to e.
   *
   * @param i number of elements to extend.
   * @param j index of element to be set.
   * @param e new exponent for val[j].
   * @return extended exponent vector.
   */
  public ExpVectorSymbolic extend(int i, int j, IExpr e) {
    ExpVectorSymbolic result = valueOf(val.length + i);
    IExpr[] w = result.val;
    System.arraycopy(val, 0, w, i, val.length);
    if (j >= i) {
      throw new IllegalArgumentException("i " + i + " <= j " + j + " invalid");
    }
    w[j] = e;
    return result;
  }

  /**
   * Extend lower variables. Extend this by i lower elements and set val[j] to e.
   *
   * @param i number of elements to extend.
   * @param j index of element to be set.
   * @param e new exponent for val[j].
   * @return extended exponent vector.
   */
  public ExpVectorSymbolic extendLower(int i, int j, IExpr e) {
    ExpVectorSymbolic result = valueOf(val.length + i);
    IExpr[] w = result.val;
    System.arraycopy(val, 0, w, 0, val.length);
    if (j >= i) {
      throw new IllegalArgumentException("i " + i + " <= j " + j + " invalid");
    }
    w[val.length + j] = e;
    return result;
  }

  /**
   * Contract variables. Used e.g. in module embedding. Contract this to len elements.
   *
   * @param i position of first element to be copied.
   * @param len new length.
   * @return contracted exponent vector.
   */
  public ExpVectorSymbolic contract(int i, int len) {
    if (i + len > val.length) {
      throw new IllegalArgumentException("len " + len + " > val.len " + val.length);
    }
    ExpVectorSymbolic result = valueOf(len);
    IExpr[] w = result.val;
    System.arraycopy(val, i, w, 0, len);
    return result;
  }

  /**
   * Reverse variables. Used e.g. in opposite rings.
   *
   * @return reversed exponent vector.
   */
  public ExpVectorSymbolic reverse() {
    ExpVectorSymbolic result = valueOf(val.length);
    IExpr[] w = result.val;
    for (int i = 0; i < val.length; i++) {
      w[i] = val[val.length - 1 - i];
    }
    return result;
  }

  /**
   * Reverse lower j variables. Used e.g. in opposite rings. Reverses the first j-1 variables, the
   * rest is unchanged.
   *
   * @param j index of first variable reversed.
   * @return reversed exponent vector.
   */
  public ExpVectorSymbolic reverse(int j) {
    if (j <= 0 || j > val.length) {
      return this;
    }
    ExpVectorSymbolic result = valueOf(val.length);
    IExpr[] w = result.val;
    // copy first
    for (int i = 0; i < j; i++) {
      w[i] = val[i];
    }
    // reverse rest
    for (int i = j; i < val.length; i++) {
      w[i] = val[val.length + j - 1 - i];
    }
    return result;
  }

  /**
   * Reverse upper j variables. Reverses the last j-1 variables, the rest is unchanged.
   *
   * @param j index of first variable not reversed.
   * @return reversed exponent vector.
   */
  public ExpVectorSymbolic reverseUpper(int j) {
    if (j <= 0 || j > val.length) {
      return this;
    }
    ExpVectorSymbolic result = valueOf(val.length);
    IExpr[] w = result.val;
    for (int i = 0; i < j; i++) {
      w[i] = val[j - 1 - i];
    }
    // copy rest
    for (int i = j; i < val.length; i++) {
      w[i] = val[i];
    }
    return result;
  }

  /**
   * Combine with ExpVectorLong. Combine this with the other ExpVectorLong V.
   *
   * @param V the other exponent vector.
   * @return combined exponent vector.
   */
  public ExpVectorSymbolic combine(ExpVectorSymbolic V) {
    if (V == null || V.length() == 0) {
      return this;
    }
    ExpVectorSymbolic Vl = V;
    if (val.length == 0) {
      return Vl;
    }
    ExpVectorSymbolic result = valueOf(val.length + Vl.val.length);
    IExpr[] w = result.val;
    System.arraycopy(val, 0, w, 0, val.length);
    System.arraycopy(Vl.val, 0, w, val.length, Vl.val.length);
    return result;
  }

  /**
   * Permutation of exponent vector.
   *
   * @param P permutation.
   * @return P(e).
   */
  public ExpVectorSymbolic permutation(List<Integer> P) {
    ExpVectorSymbolic result = valueOf(val.length);
    IExpr[] w = result.val;
    int j = 0;
    for (Integer i : P) {
      w[j++] = val[i];
    }
    return result;
  }

  /**
   * Get the string representation.
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder("(");
    for (int i = 0; i < length(); i++) {
      s.append(getVal(i));
      if (i < length() - 1) {
        s.append(",");
      }
    }
    s.append(")");
    return s.toString();
  }

  /**
   * Get the string representation with variable names.
   *
   * @param vars names of variables.
   * @see java.lang.Object#toString()
   */
  public String toString(IAST vars) {
    StringBuilder s = new StringBuilder();
    boolean pit;
    int r = length();
    if (r != vars.argSize()) {
      // logger.warn("length mismatch " + r + " <> " + vars.length);
      return toString();
    }
    if (r == 0) {
      return s.toString();
    }
    IExpr vi;
    for (int i = r - 1; i > 0; i--) {
      vi = getVal(i);
      if (!vi.isZero()) {
        s.append(vars.get(r - i));
        if (!vi.isOne()) {
          s.append("^" + vi);
        }
        pit = false;
        for (int j = i - 1; j >= 0; j--) {
          if (!getVal(j).isZero()) {
            pit = true;
          }
        }
        if (pit) {
          s.append(" * ");
        }
      }
    }
    vi = getVal(0);
    if (!vi.isZero()) {
      s.append(vars.get(r));
      if (!vi.isOne()) {
        s.append("^" + vi);
      }
    }
    return s.toString();
  }

  /**
   * Get the string representation of the variables.
   *
   * @param vars names of variables.
   * @return string representation of the variables.
   * @see java.util.Arrays#toString()
   */
  public static String varsToString(IAST vars) {
    if (vars == null) {
      return "null";
    }
    StringBuilder s = new StringBuilder();
    vars.forEach(vars.size(), (x, i) -> {
      s.append(x);
      if (i < vars.argSize()) {
        s.append(",");
      }
    });
    return s.toString();
  }

  /**
   * Get a scripting compatible string representation.
   *
   * @return script compatible representation for this Element.
   * @see edu.jas.structure.Element#toScript()
   */
  public String toScript() {
    return toScript(stdVars());
  }

  /**
   * Get a scripting compatible string representation.
   *
   * @return script compatible representation for this Element.
   * @see edu.jas.structure.Element#toScript()
   */
  public String toScript(IAST vars) {
    // Python case
    int r = length();
    if (r != vars.argSize()) {
      return toString();
    }
    StringBuilder s = new StringBuilder();
    boolean pit;
    IExpr vi;
    for (int i = r - 1; i > 0; i--) {
      vi = getVal(i);
      if (!vi.isZero()) {
        s.append(vars.get(r - i));
        if (!vi.isOne()) {
          s.append("**" + vi);
        }
        pit = false;
        for (int j = i - 1; j >= 0; j--) {
          if (!getVal(j).isZero()) {
            pit = true;
          }
        }
        if (pit) {
          s.append(" * ");
        }
      }
    }
    vi = getVal(0);
    if (!vi.isZero()) {
      s.append(vars.get(r));
      if (!vi.isOne()) {
        s.append("**" + vi);
      }
    }
    return s.toString();
  }

  /**
   * Comparison with any other object.
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object B) {
    if (!(B instanceof ExpVectorSymbolic)) {
      return false;
    }
    IExpr[] u = val;
    IExpr[] v = ((ExpVectorSymbolic) B).val;
    if (u.length != v.length) {
      return false;
    }
    for (int i = 0; i < u.length; i++) {
      if (!u[i].equals(v[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * hashCode. Optimized for small exponents, i.e. &le; 2<sup>4</sup> and small number of variables,
   * i.e. &le; 8.
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    if (hash < 0) {
      int h = 0;
      final int len = length();
      for (int i = 0; i < len; i++) {
        hash = (hash << 3) + getVal(i).hashCode();
      }
      hash = h;
    }
    return hash;
  }

  /**
   * ExpVectorLong absolute value.
   *
   * @return abs(this).
   */
  public ExpVectorSymbolic abs() {
    IExpr[] u = val;
    ExpVectorSymbolic result = valueOf(u.length);
    IExpr[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      if (u[i].isNegative()) {
        w[i] = u[i].negative();
      } else {
        w[i] = u[i];
      }
    }
    return result;
  }

  /**
   * ExpVectorLong negate.
   *
   * @return -this.
   */
  public ExpVectorSymbolic negate() {
    IExpr[] u = val;
    ExpVectorSymbolic result = valueOf(u.length);
    IExpr[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      w[i] = u[i].negate();
    }
    return result;
  }

  /**
   * ExpVectorLong summation.
   *
   * @param V
   * @return this+V.
   */
  public ExpVectorSymbolic sum(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    ExpVectorSymbolic result = valueOf(u.length);
    IExpr[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      w[i] = S.Plus.of(u[i], v[i]);
    }
    return result;
  }

  /**
   * ExpVectorLong subtract. Result may have negative entries.
   *
   * @param V
   * @return this-V.
   */
  public ExpVectorSymbolic subtract(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    ExpVectorSymbolic result = valueOf(u.length);
    IExpr[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      w[i] = S.Plus.of(u[i], v[i].negate());
    }
    return result;
  }

  /**
   * ExpVectorLong substitution. Clone and set exponent to d at position i.
   *
   * @param i position.
   * @param d new exponent.
   * @return substituted ExpVectorLong.
   */
  public ExpVectorSymbolic subst(int i, IExpr d) {
    ExpVectorSymbolic V = this.copy();
    // long e =
    V.setVal(i, d);
    return V;
    // return EVSU(this, i, d);
  }

  /**
   * ExpVectorLong signum.
   *
   * @return 0 if this is zero, -1 if some entry is negative, 1 if no entry is negative and at least
   *         one entry is positive.
   */
  public int signum() {
    int t = 0;
    IExpr[] u = val;
    for (int i = 0; i < u.length; i++) {
      if (u[i].isNegativeResult()) {
        return -1;
      }
      if (u[i].isPositiveResult()) {
        t = 1;
      }
    }
    return t;
    // return EVSIGN(this);
  }

  /**
   * ExpVectorLong total degree.
   *
   * @return sum of all exponents.
   */
  public IExpr totalDeg() {
    IExpr[] u = val; // U.val;
    IASTAppendable t = F.PlusAlloc(u.length);
    for (int i = 0; i < u.length; i++) {
      t.append(u[i]);
    }
    return EvalEngine.get().evaluate(t);
    // return EVTDEG(this);
  }

  /**
   * ExpVectorLong maximal degree.
   *
   * @return maximal exponent.
   */
  public IExpr maxDeg() {
    IExpr[] u = val;
    IExpr t = F.C0;
    for (int i = 0; i < u.length; i++) {
      if (S.Greater.ofQ(u[i], t)) {
        t = u[i];
      }
    }
    return t;
    // return EVMDEG(this);
  }

  /**
   * ExpVectorLong weighted degree.
   *
   * @param w weights.
   * @return weighted sum of all exponents.
   */
  public IExpr weightDeg(IExpr[][] w) {
    if (w == null || w.length == 0) {
      return totalDeg(); // assume weight 1
    }

    IExpr[] u = val;
    IASTAppendable t = F.PlusAlloc(w.length);
    for (int j = 0; j < w.length; j++) {
      IExpr[] wj = w[j];
      for (int i = 0; i < u.length; i++) {
        t.append(F.Times(wj[i], u[i]));
      }
    }
    return EvalEngine.get().evaluate(t);
  }

  /**
   * ExpVectorLong least common multiple.
   *
   * @param V
   * @return component wise maximum of this and V.
   */
  public ExpVectorSymbolic lcm(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    ExpVectorSymbolic result = valueOf(u.length);
    IExpr[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      if (u[i].greaterEqual(v[i]).isTrue()) {
        // if (S.GreaterEqual.ofQ(u[i], v[i])) {
        w[i] = u[i];
      } else {
        w[i] = v[i];
      }
    }
    return result;
  }

  /**
   * ExpVectorLong greatest common divisor.
   *
   * @param V
   * @return component wise minimum of this and V.
   */
  public ExpVectorSymbolic gcd(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    ExpVectorSymbolic result = valueOf(u.length);
    IExpr[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      if (u[i].lessEqual(v[i]).isTrue()) {
        // if (S.LessEqual.ofQ(u[i], v[i])) {
        w[i] = u[i];
      } else {
        w[i] = v[i];
      }
    }
    return result;
  }

  /**
   * ExpVectorLong dependency on variables.
   *
   * @return array of indices where val has positive exponents.
   */
  public int[] dependencyOnVariables() {
    IExpr[] u = val;
    int l = 0;
    for (int i = 0; i < u.length; i++) {
      if (u[i].isPositiveResult()) {
        l++;
      }
    }
    int[] dep = new int[l];
    if (l == 0) {
      return dep;
    }
    int j = 0;
    for (int i = 0; i < u.length; i++) {
      if (u[i].isPositiveResult()) {
        dep[j] = i;
        j++;
      }
    }
    return dep;
  }

  /**
   * ExpVectorLong multiple test. Test if this is component wise greater or equal to V.
   *
   * @param V
   * @return true if this is a multiple of V, else false.
   */
  public boolean multipleOf(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    boolean t = true;
    for (int i = 0; i < u.length; i++) {
      if (S.Less.ofQ(u[i], v[i])) {
        return false;
      }
    }
    return t;
  }

  /**
   * ExpVectorLong compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int compareTo(ExpVectorSymbolic V) {
    return this.invLexCompareTo(V);
  }

  /**
   * Inverse lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVILCP(ExpVectorSymbolic U, ExpVectorSymbolic V) {
    return U.invLexCompareTo(V);
  }

  /**
   * Inverse lexicographical compare part. Compare entries between begin and end (-1).
   *
   * @param U
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVILCP(ExpVectorSymbolic U, ExpVectorSymbolic V, int begin, int end) {
    return U.invLexCompareTo(V, begin, end);
  }

  /**
   * Inverse graded lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVIGLC(ExpVectorSymbolic U, ExpVectorSymbolic V) {
    return U.invGradCompareTo(V);
  }

  /**
   * Inverse graded lexicographical compare part. Compare entries between begin and end (-1).
   *
   * @param U
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVIGLC(ExpVectorSymbolic U, ExpVectorSymbolic V, int begin, int end) {
    return U.invGradCompareTo(V, begin, end);
  }

  /**
   * Reverse inverse lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVRILCP(ExpVectorSymbolic U, ExpVectorSymbolic V) {
    return U.revInvLexCompareTo(V);
  }

  /**
   * Reverse inverse lexicographical compare part. Compare entries between begin and end (-1).
   *
   * @param U
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVRILCP(ExpVectorSymbolic U, ExpVectorSymbolic V, int begin, int end) {
    return U.revInvLexCompareTo(V, begin, end);
  }

  /**
   * Reverse inverse graded lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVRIGLC(ExpVectorSymbolic U, ExpVectorSymbolic V) {
    return U.revInvGradCompareTo(V);
  }

  /**
   * Reverse inverse graded lexicographical compare part. Compare entries between begin and end
   * (-1).
   *
   * @param U
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVRIGLC(ExpVectorSymbolic U, ExpVectorSymbolic V, int begin, int end) {
    return U.revInvGradCompareTo(V, begin, end);
  }

  /**
   * Inverse total degree lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVITDEGLC(ExpVectorSymbolic U, ExpVectorSymbolic V) {
    return U.invTdegCompareTo(V);
  }

  /**
   * Reverse lexicographical inverse total degree compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVRLITDEGC(ExpVectorSymbolic U, ExpVectorSymbolic V) {
    return U.revLexInvTdegCompareTo(V);
  }

  /**
   * Inverse weighted lexicographical compare.
   *
   * @param w weight array.
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVIWLC(IExpr[][] w, ExpVectorSymbolic U, ExpVectorSymbolic V) {
    return U.invWeightCompareTo(w, V);
  }

  /**
   * Inverse weighted lexicographical compare part. Compare entries between begin and end (-1).
   *
   * @param w weight array.
   * @param U
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVIWLC(IExpr[][] w, ExpVectorSymbolic U, ExpVectorSymbolic V, int begin,
      int end) {
    return U.invWeightCompareTo(w, V, begin, end);
  }

  /**
   * ExpVectorLong inverse lexicographical compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int invLexCompareTo(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    for (int i = 0; i < u.length; i++) {
      if (S.Greater.ofQ(u[i], v[i])) {
        return 1;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        return -1;
      }
    }
    return t;
    // return EVILCP(this, V);
  }

  /**
   * ExpVectorLong inverse lexicographical compareTo.
   *
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int invLexCompareTo(ExpVectorSymbolic V, int begin, int end) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    for (int i = begin; i < end; i++) {
      if (S.Greater.ofQ(u[i], v[i])) {
        return 1;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        return -1;
      }
    }
    return t;
    // return EVILCP(this, V, begin, end);
  }

  /**
   * ExpVectorLong inverse graded lexicographical compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int invGradCompareTo(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    int i;
    for (i = 0; i < u.length; i++) {
      if (S.Greater.ofQ(u[i], v[i])) {
        t = 1;
        break;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    IASTAppendable up = F.PlusAlloc(u.length - i);
    IASTAppendable vp = F.PlusAlloc(u.length - i);
    for (int j = i; j < u.length; j++) {
      up.append(u[j]);
      vp.append(v[j]);
    }
    IExpr upEvaled = EvalEngine.get().evaluate(up);
    IExpr vpEvaled = EvalEngine.get().evaluate(vp);
    if (S.Greater.ofQ(upEvaled, vpEvaled)) {
      t = 1;
    } else {
      if (S.Less.ofQ(upEvaled, vpEvaled)) {
        t = -1;
      }
    }
    return t;
    // return EVIGLC(this, V);
  }

  /**
   * ExpVectorLong inverse graded lexicographical compareTo.
   *
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int invGradCompareTo(ExpVectorSymbolic V, int begin, int end) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    int i;
    for (i = begin; i < end; i++) {
      if (S.Greater.ofQ(u[i], v[i])) {
        t = 1;
        break;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    IASTAppendable up = F.PlusAlloc(end - i);
    IASTAppendable vp = F.PlusAlloc(end - i);
    for (int j = i; j < end; j++) {
      up.append(u[j]);
      vp.append(v[j]);
    }
    IExpr upEvaled = EvalEngine.get().evaluate(up);
    IExpr vpEvaled = EvalEngine.get().evaluate(vp);

    if (S.Greater.ofQ(upEvaled, vpEvaled)) {
      t = 1;
    } else {
      if (S.Less.ofQ(upEvaled, vpEvaled)) {
        t = -1;
      }
    }
    return t;
  }

  /**
   * ExpVectorLong reverse inverse lexicographical compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int revInvLexCompareTo(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    for (int i = u.length - 1; i >= 0; i--) {
      if (S.Greater.ofQ(u[i], v[i])) {
        return 1;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        return -1;
      }
    }
    return t;
    // return EVRILCP(this, V);
  }

  /**
   * ExpVectorLong reverse inverse lexicographical compareTo.
   *
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int revInvLexCompareTo(ExpVectorSymbolic V, int begin, int end) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    for (int i = end - 1; i >= begin; i--) {
      if (S.Greater.ofQ(u[i], v[i])) {
        return 1;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        return -1;
      }
    }
    return t;
    // return EVRILCP(this, V, begin, end);
  }

  /**
   * ExpVectorLong reverse inverse graded compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int revInvGradCompareTo(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    int i;
    for (i = u.length - 1; i >= 0; i--) {
      if (S.Greater.ofQ(u[i], v[i])) {
        t = 1;
        break;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }

    IASTAppendable up = F.PlusAlloc(i + 1);
    IASTAppendable vp = F.PlusAlloc(i + 1);
    for (int j = i; j >= 0; j--) {
      up.append(u[j]);
      vp.append(v[j]);
    }
    IExpr upEvaled = EvalEngine.get().evaluate(up);
    IExpr vpEvaled = EvalEngine.get().evaluate(vp);

    if (S.Greater.ofQ(upEvaled, vpEvaled)) {
      t = 1;
    } else {
      if (S.Less.ofQ(upEvaled, vpEvaled)) {
        t = -1;
      }
    }
    return t;
    // return EVRIGLC(this, V);
  }

  /**
   * ExpVectorLong reverse inverse graded compareTo.
   *
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int revInvGradCompareTo(ExpVectorSymbolic V, int begin, int end) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    int i;
    for (i = end - 1; i >= begin; i--) {
      if (S.Greater.ofQ(u[i], v[i])) {
        t = 1;
        break;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    IASTAppendable up = F.PlusAlloc(i - begin + 1);
    IASTAppendable vp = F.PlusAlloc(i - begin + 1);
    for (int j = i; j >= begin; j--) {
      up.append(u[j]);
      vp.append(v[j]);
    }
    IExpr upEvaled = EvalEngine.get().evaluate(up);
    IExpr vpEvaled = EvalEngine.get().evaluate(vp);

    if (S.Greater.ofQ(upEvaled, vpEvaled)) {
      t = 1;
    } else {
      if (S.Less.ofQ(upEvaled, vpEvaled)) {
        t = -1;
      }
    }
    return t;
    // return EVRIGLC(this, V, begin, end);
  }

  /**
   * ExpVector inverse total degree lexicographical compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int invTdegCompareTo(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    int i;
    for (i = 0; i < u.length; i++) {
      if (S.Less.ofQ(u[i], v[i])) {
        t = 1;
        break;
      }
      if (S.Greater.ofQ(u[i], v[i])) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }

    IASTAppendable up = F.PlusAlloc(u.length - i + 1);
    IASTAppendable vp = F.PlusAlloc(u.length - i + 1);
    for (int j = i; j < u.length; j++) {
      up.append(u[j]);
      vp.append(v[j]);
    }
    IExpr upEvaled = EvalEngine.get().evaluate(up);
    IExpr vpEvaled = EvalEngine.get().evaluate(vp);

    if (S.Greater.ofQ(upEvaled, vpEvaled)) {
      t = 1;
    } else {
      if (S.Less.ofQ(upEvaled, vpEvaled)) {
        t = -1;
      }
    }
    return t;
  }

  /**
   * ExpVector reverse lexicographical inverse total degree compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int revLexInvTdegCompareTo(ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    int i;
    for (i = u.length - 1; i >= 0; i--) {
      if (S.Less.ofQ(u[i], v[i])) {
        t = 1;
        break;
      }
      if (S.Greater.ofQ(u[i], v[i])) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }

    IASTAppendable up = F.PlusAlloc(i + 1);
    IASTAppendable vp = F.PlusAlloc(i + 1);
    for (int j = i; j >= 0; j--) {
      up.append(u[j]);
      vp.append(v[j]);
    }
    IExpr upEvaled = EvalEngine.get().evaluate(up);
    IExpr vpEvaled = EvalEngine.get().evaluate(vp);
    if (S.Greater.ofQ(upEvaled, vpEvaled)) {
      t = 1;
    } else {
      if (S.Less.ofQ(upEvaled, vpEvaled)) {
        t = -1;
      }
    }
    return t;
  }

  /**
   * ExpVectorLong inverse weighted lexicographical compareTo.
   *
   * @param w weight array.
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int invWeightCompareTo(IExpr[][] w, ExpVectorSymbolic V) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    int i;
    for (i = 0; i < u.length; i++) {
      if (S.Greater.ofQ(u[i], v[i])) {
        t = 1;
        break;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    for (int k = 0; k < w.length; k++) {
      IExpr[] wk = w[k];

      IASTAppendable up = F.PlusAlloc(i + 1);
      IASTAppendable vp = F.PlusAlloc(i + 1);
      for (int j = i; j >= 0; j--) {
        up.append(F.Times(wk[j], u[j]));
        vp.append(F.Times(wk[j], v[j]));
      }
      IExpr upEvaled = EvalEngine.get().evaluate(up);
      IExpr vpEvaled = EvalEngine.get().evaluate(vp);
      if (S.Greater.ofQ(upEvaled, vpEvaled)) {
        return 1;
      }
      if (S.Less.ofQ(upEvaled, vpEvaled)) {
        return -1;
      }
    }
    return t;
    // return EVIWLC(w, this, V);
  }

  /**
   * ExpVectorLong inverse weighted lexicographical compareTo.
   *
   * @param w weight array.
   * @param V
   * @param begin
   * @param end
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int invWeightCompareTo(IExpr[][] w, ExpVectorSymbolic V, int begin, int end) {
    IExpr[] u = val;
    IExpr[] v = V.val;
    int t = 0;
    int i;
    for (i = begin; i < end; i++) {
      if (S.Greater.ofQ(u[i], v[i])) {
        t = 1;
        break;
      }
      if (S.Less.ofQ(u[i], v[i])) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    for (int k = 0; k < w.length; k++) {
      IExpr[] wk = w[k];

      IASTAppendable up = F.PlusAlloc(i - end + 1);
      IASTAppendable vp = F.PlusAlloc(i - end + 1);
      for (int j = i; j < end; j++) {
        up.append(F.Times(wk[j], u[j]));
        vp.append(F.Times(wk[j], v[j]));
      }
      IExpr upEvaled = EvalEngine.get().evaluate(up);
      IExpr vpEvaled = EvalEngine.get().evaluate(vp);
      if (S.Greater.ofQ(upEvaled, vpEvaled)) {
        return 1;
      }
      if (S.Less.ofQ(upEvaled, vpEvaled)) {
        return -1;
      }
    }
    return t;
    // return EVIWLC(w, this, V, begin, end);
  }

  /**
   * Creates an empty ExpVectorLong.
   *
   * @param n length of exponent vector.
   */
  public static ExpVectorSymbolic valueOf(int n) {
    return new ExpVectorSymbolic(n);
  }

  /**
   * Get the array index of a variable at index.
   *
   * @param idx index of the variable
   * @return array index of the variable.
   */
  public int varIndex(int idx) {
    return length() - idx - 1;
  }

  /**
   * Get the index of a variable.
   *
   * @param x variable name to be searched.
   * @param vars array of names of variables
   * @return index of x in vars.
   */
  public static int indexVar(IExpr x, final IAST vars) {
    int len = vars.size();
    for (int i = 1; i < len; i++) {
      if (x.equals(vars.get(i))) {
        return len - i - 1;
      }
    }
    return -1; // not found
  }
}
