package org.matheclipse.core.polynomials.longexponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * ExpVectorLong implements exponent vectors for polynomials using arrays of long as storage unit.
 * This class is used by ExpVectorLong internally, there is no need to use this class directly.
 */
public final class ExpVectorLong {

  /** The data structure is an array of longs. */
  /* package */ final long[] val;

  int hash;

  /**
   * Constructor for ExpVectorLong.
   *
   * @param n length of exponent vector.
   */
  public ExpVectorLong(int n) {
    super();
    val = new long[n];
  }

  /**
   * Constructor for ExpVectorLong. Sets exponent i to e.
   *
   * @param n length of exponent vector.
   * @param i index of exponent to be set.
   * @param e exponent to be set.
   */
  public ExpVectorLong(int n, int i, long e) {
    this(new long[n]);
    val[i] = e;
  }

  /**
   * Constructor for ExpVectorLong. Sets val.
   *
   * @param v internal representation array.
   */
  public ExpVectorLong(long[] v) {
    super();
    if (v == null) {
      throw new IllegalArgumentException("null val not allowed");
    }
    val = Arrays.copyOf(v, v.length); // > Java-5
  }

  /**
   * Constructor for ExpVectorLong. Converts a String representation to an ExpVectorLong. Accepted
   * format = (1,2,3,4,5,6,7).
   *
   * @param s String representation.
   */
  public ExpVectorLong(String s) throws NumberFormatException {
    super();
    // first format = (1,2,3,4,5,6,7)
    List<Long> exps = new ArrayList<Long>();
    s = s.trim();
    int b = s.indexOf('(');
    int e = s.indexOf(')', b + 1);
    String teil;
    int k;
    long a;
    if (b >= 0 && e >= 0) {
      b++;
      while ((k = s.indexOf(',', b)) >= 0) {
        teil = s.substring(b, k);
        a = Long.parseLong(teil);
        exps.add(a);
        b = k + 1;
      }
      if (b <= e) {
        teil = s.substring(b, e);
        a = Long.parseLong(teil);
        exps.add(a);
      }
      int length = exps.size();
      val = new long[length];
      for (int j = 0; j < length; j++) {
        val[j] = exps.get(j);
      }
    } else {
      // not implemented
      val = null;
      // length = -1;
      // Vector names = new Vector();
      // vars = s;
    }
  }

  /**
   * Clone this.
   *
   * @see java.lang.Object#clone()
   */
  public ExpVectorLong copy() {
    long[] w = new long[val.length];
    System.arraycopy(val, 0, w, 0, val.length);
    return new ExpVectorLong(w);
  }

  /**
   * Factory constructor for ExpVector. Sets val.
   *
   * @param v collection of exponents.
   */
  public static ExpVectorLong create(Collection<Long> v) {
    long[] w = new long[v.size()];
    int i = 0;
    for (Long k : v) {
      w[i++] = k;
    }
    return new ExpVectorLong(w);
  }

  /**
   * Get the exponent vector.
   *
   * @return val.
   */
  /* package */ public long[] getVal() {
    return val;
  }

  /**
   * Get the exponent at position i.
   *
   * @param i position.
   * @return val[i].
   */
  public long getVal(int i) {
    return val[i];
  }

  /**
   * Set the exponent at position i to e.
   *
   * @param i
   * @param e
   * @return old val[i].
   */
  protected long setVal(int i, long e) {
    long x = val[i];
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
    IASTAppendable vars = F.ListAlloc(n);
    if (prefix == null || prefix.length() == 0) {
      prefix = "x";
    }
    for (int i = 0; i < n; i++) {
      vars.append(F.Dummy(prefix + i)); // (n-1-i);
    }
    return vars;
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
  public ExpVectorLong extend(int i, int j, long e) {
    ExpVectorLong result = valueOf(val.length + i);
    long[] w = result.val;
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
  public ExpVectorLong extendLower(int i, int j, long e) {
    ExpVectorLong result = valueOf(val.length + i);
    long[] w = result.val;
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
  public ExpVectorLong contract(int i, int len) {
    if (i + len > val.length) {
      throw new IllegalArgumentException("len " + len + " > val.len " + val.length);
    }
    ExpVectorLong result = valueOf(len);
    long[] w = result.val;
    System.arraycopy(val, i, w, 0, len);
    return result;
  }

  /**
   * Reverse variables. Used e.g. in opposite rings.
   *
   * @return reversed exponent vector.
   */
  public ExpVectorLong reverse() {
    ExpVectorLong result = valueOf(val.length);
    long[] w = result.val;
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
  public ExpVectorLong reverse(int j) {
    if (j <= 0 || j > val.length) {
      return this;
    }
    ExpVectorLong result = valueOf(val.length);
    long[] w = result.val;
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
  public ExpVectorLong reverseUpper(int j) {
    if (j <= 0 || j > val.length) {
      return this;
    }
    ExpVectorLong result = valueOf(val.length);
    long[] w = result.val;
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
  public ExpVectorLong combine(ExpVectorLong V) {
    if (V == null || V.length() == 0) {
      return this;
    }
    ExpVectorLong Vl = V;
    if (val.length == 0) {
      return Vl;
    }
    ExpVectorLong result = valueOf(val.length + Vl.val.length);
    long[] w = result.val;
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
  public ExpVectorLong permutation(List<Integer> P) {
    ExpVectorLong result = valueOf(val.length);
    long[] w = result.val;
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
    long vi;
    for (int i = r - 1; i > 0; i--) {
      vi = getVal(i);
      if (vi != 0) {
        s.append(vars.get(r - i));
        if (vi != 1) {
          s.append("^" + vi);
        }
        pit = false;
        for (int j = i - 1; j >= 0; j--) {
          if (getVal(j) != 0) {
            pit = true;
          }
        }
        if (pit) {
          s.append(" * ");
        }
      }
    }
    vi = getVal(0);
    if (vi != 0) {
      s.append(vars.get(r));
      if (vi != 1) {
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
    long vi;
    for (int i = r - 1; i > 0; i--) {
      vi = getVal(i);
      if (vi != 0) {
        s.append(vars.get(r - i));
        if (vi != 1) {
          s.append("**" + vi);
        }
        pit = false;
        for (int j = i - 1; j >= 0; j--) {
          if (getVal(j) != 0) {
            pit = true;
          }
        }
        if (pit) {
          s.append(" * ");
        }
      }
    }
    vi = getVal(0);
    if (vi != 0) {
      s.append(vars.get(r));
      if (vi != 1) {
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
    if (!(B instanceof ExpVectorLong)) {
      return false;
    }
    ExpVectorLong b = (ExpVectorLong) B;
    long[] u = val;
    long[] v = b.val;
    if (u.length != v.length) {
      return false;
    }
    for (int i = 0; i < u.length; i++) {
      if (u[i] != v[i]) {
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
    if (hash == 0) {
      for (int i = 0; i < length(); i++) {
        hash = hash << 4 + getVal(i);
      }
      if (hash == 0) {
        hash = 1;
      }
    }
    return hash;
  }

  /**
   * ExpVectorLong absolute value.
   *
   * @return abs(this).
   */
  public ExpVectorLong abs() {
    long[] u = val;
    ExpVectorLong result = valueOf(u.length);
    long[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      if (u[i] >= 0L) {
        w[i] = u[i];
      } else {
        w[i] = -u[i];
      }
    }
    return result;
  }

  /**
   * ExpVectorLong negate.
   *
   * @return -this.
   */
  public ExpVectorLong negate() {
    long[] u = val;
    ExpVectorLong result = valueOf(u.length);
    long[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      w[i] = -u[i];
    }
    return result;
  }

  /**
   * ExpVectorLong summation.
   *
   * @param V
   * @return this+V.
   */
  public ExpVectorLong sum(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    ExpVectorLong result = valueOf(u.length);
    long[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      w[i] = u[i] + v[i];
    }
    return result;
  }

  /**
   * ExpVectorLong subtract. Result may have negative entries.
   *
   * @param V
   * @return this-V.
   */
  public ExpVectorLong subtract(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    ExpVectorLong result = valueOf(u.length);
    long[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      w[i] = u[i] - v[i];
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
  public ExpVectorLong subst(int i, long d) {
    ExpVectorLong V = this.copy();
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
    long[] u = val;
    for (int i = 0; i < u.length; i++) {
      if (u[i] < 0) {
        return -1;
      }
      if (u[i] > 0) {
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
  public long totalDeg() {
    long t = 0;
    long[] u = val; // U.val;
    for (int i = 0; i < u.length; i++) {
      t += u[i];
    }
    return t;
    // return EVTDEG(this);
  }

  /**
   * ExpVectorLong maximal degree.
   *
   * @return maximal exponent.
   */
  public long maxDeg() {
    long t = 0;
    long[] u = val;
    for (int i = 0; i < u.length; i++) {
      if (u[i] > t) {
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
  public long weightDeg(long[][] w) {
    if (w == null || w.length == 0) {
      return totalDeg(); // assume weight 1
    }
    long t = 0;
    long[] u = val;
    for (int j = 0; j < w.length; j++) {
      long[] wj = w[j];
      for (int i = 0; i < u.length; i++) {
        t += wj[i] * u[i];
      }
    }
    return t;
    // return EVWDEG( w, this );
  }

  /**
   * ExpVectorLong least common multiple.
   *
   * @param V
   * @return component wise maximum of this and V.
   */
  public ExpVectorLong lcm(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    ExpVectorLong result = valueOf(u.length);
    long[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      w[i] = (u[i] >= v[i] ? u[i] : v[i]);
    }
    return result;
  }

  /**
   * ExpVectorLong greatest common divisor.
   *
   * @param V
   * @return component wise minimum of this and V.
   */
  public ExpVectorLong gcd(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    ExpVectorLong result = valueOf(u.length);
    long[] w = result.val;
    for (int i = 0; i < u.length; i++) {
      w[i] = (u[i] <= v[i] ? u[i] : v[i]);
    }
    return result;
  }

  /**
   * ExpVectorLong dependency on variables.
   *
   * @return array of indices where val has positive exponents.
   */
  public int[] dependencyOnVariables() {
    long[] u = val;
    int l = 0;
    for (int i = 0; i < u.length; i++) {
      if (u[i] > 0) {
        l++;
      }
    }
    int[] dep = new int[l];
    if (l == 0) {
      return dep;
    }
    int j = 0;
    for (int i = 0; i < u.length; i++) {
      if (u[i] > 0) {
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
  public boolean multipleOf(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    boolean t = true;
    for (int i = 0; i < u.length; i++) {
      if (u[i] < v[i]) {
        return false;
      }
    }
    return t;
    // return EVMT(this, V);
  }

  /**
   * ExpVectorLong compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int compareTo(ExpVectorLong V) {
    return this.invLexCompareTo(V);
  }

  /**
   * Inverse lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVILCP(ExpVectorLong U, ExpVectorLong V) {
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
  public static int EVILCP(ExpVectorLong U, ExpVectorLong V, int begin, int end) {
    return U.invLexCompareTo(V, begin, end);
  }

  /**
   * Inverse graded lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVIGLC(ExpVectorLong U, ExpVectorLong V) {
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
  public static int EVIGLC(ExpVectorLong U, ExpVectorLong V, int begin, int end) {
    return U.invGradCompareTo(V, begin, end);
  }

  /**
   * Reverse inverse lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVRILCP(ExpVectorLong U, ExpVectorLong V) {
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
  public static int EVRILCP(ExpVectorLong U, ExpVectorLong V, int begin, int end) {
    return U.revInvLexCompareTo(V, begin, end);
  }

  /**
   * Reverse inverse graded lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVRIGLC(ExpVectorLong U, ExpVectorLong V) {
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
  public static int EVRIGLC(ExpVectorLong U, ExpVectorLong V, int begin, int end) {
    return U.revInvGradCompareTo(V, begin, end);
  }

  /**
   * Inverse total degree lexicographical compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVITDEGLC(ExpVectorLong U, ExpVectorLong V) {
    return U.invTdegCompareTo(V);
  }

  /**
   * Reverse lexicographical inverse total degree compare.
   *
   * @param U
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public static int EVRLITDEGC(ExpVectorLong U, ExpVectorLong V) {
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
  public static int EVIWLC(long[][] w, ExpVectorLong U, ExpVectorLong V) {
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
  public static int EVIWLC(long[][] w, ExpVectorLong U, ExpVectorLong V, int begin, int end) {
    return U.invWeightCompareTo(w, V, begin, end);
  }

  /**
   * ExpVectorLong inverse lexicographical compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int invLexCompareTo(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    for (int i = 0; i < u.length; i++) {
      if (u[i] > v[i])
        return 1;
      if (u[i] < v[i])
        return -1;
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
  public int invLexCompareTo(ExpVectorLong V, int begin, int end) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    for (int i = begin; i < end; i++) {
      if (u[i] > v[i])
        return 1;
      if (u[i] < v[i])
        return -1;
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
  public int invGradCompareTo(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    int i;
    for (i = 0; i < u.length; i++) {
      if (u[i] > v[i]) {
        t = 1;
        break;
      }
      if (u[i] < v[i]) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    long up = 0;
    long vp = 0;
    for (int j = i; j < u.length; j++) {
      up += u[j];
      vp += v[j];
    }
    if (up > vp) {
      t = 1;
    } else {
      if (up < vp) {
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
  public int invGradCompareTo(ExpVectorLong V, int begin, int end) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    int i;
    for (i = begin; i < end; i++) {
      if (u[i] > v[i]) {
        t = 1;
        break;
      }
      if (u[i] < v[i]) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    long up = 0;
    long vp = 0;
    for (int j = i; j < end; j++) {
      up += u[j];
      vp += v[j];
    }
    if (up > vp) {
      t = 1;
    } else {
      if (up < vp) {
        t = -1;
      }
    }
    return t;
    // return EVIGLC(this, V, begin, end);
  }

  /**
   * ExpVectorLong reverse inverse lexicographical compareTo.
   *
   * @param V
   * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
   */
  public int revInvLexCompareTo(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    for (int i = u.length - 1; i >= 0; i--) {
      if (u[i] > v[i])
        return 1;
      if (u[i] < v[i])
        return -1;
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
  public int revInvLexCompareTo(ExpVectorLong V, int begin, int end) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    for (int i = end - 1; i >= begin; i--) {
      if (u[i] > v[i])
        return 1;
      if (u[i] < v[i])
        return -1;
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
  public int revInvGradCompareTo(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    int i;
    for (i = u.length - 1; i >= 0; i--) {
      if (u[i] > v[i]) {
        t = 1;
        break;
      }
      if (u[i] < v[i]) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    long up = 0;
    long vp = 0;
    for (int j = i; j >= 0; j--) {
      up += u[j];
      vp += v[j];
    }
    if (up > vp) {
      t = 1;
    } else {
      if (up < vp) {
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
  public int revInvGradCompareTo(ExpVectorLong V, int begin, int end) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    int i;
    for (i = end - 1; i >= begin; i--) {
      if (u[i] > v[i]) {
        t = 1;
        break;
      }
      if (u[i] < v[i]) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    long up = 0;
    long vp = 0;
    for (int j = i; j >= begin; j--) {
      up += u[j];
      vp += v[j];
    }
    if (up > vp) {
      t = 1;
    } else {
      if (up < vp) {
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
  public int invTdegCompareTo(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    int i;
    for (i = 0; i < u.length; i++) {
      if (u[i] < v[i]) {
        t = 1;
        break;
      }
      if (u[i] > v[i]) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    long up = 0;
    long vp = 0;
    for (int j = i; j < u.length; j++) {
      up += u[j];
      vp += v[j];
    }
    if (up > vp) {
      t = 1;
    } else {
      if (up < vp) {
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
  public int revLexInvTdegCompareTo(ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    int i;
    for (i = u.length - 1; i >= 0; i--) {
      if (u[i] < v[i]) {
        t = 1;
        break;
      }
      if (u[i] > v[i]) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    long up = 0;
    long vp = 0;
    for (int j = i; j >= 0; j--) {
      up += u[j];
      vp += v[j];
    }
    if (up > vp) {
      t = 1;
    } else {
      if (up < vp) {
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
  public int invWeightCompareTo(long[][] w, ExpVectorLong V) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    int i;
    for (i = 0; i < u.length; i++) {
      if (u[i] > v[i]) {
        t = 1;
        break;
      }
      if (u[i] < v[i]) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    for (int k = 0; k < w.length; k++) {
      long[] wk = w[k];
      long up = 0;
      long vp = 0;
      for (int j = i; j < u.length; j++) {
        up += wk[j] * u[j];
        vp += wk[j] * v[j];
      }
      if (up > vp) {
        return 1;
      } else if (up < vp) {
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
  public int invWeightCompareTo(long[][] w, ExpVectorLong V, int begin, int end) {
    long[] u = val;
    long[] v = V.val;
    int t = 0;
    int i;
    for (i = begin; i < end; i++) {
      if (u[i] > v[i]) {
        t = 1;
        break;
      }
      if (u[i] < v[i]) {
        t = -1;
        break;
      }
    }
    if (t == 0) {
      return t;
    }
    for (int k = 0; k < w.length; k++) {
      long[] wk = w[k];
      long up = 0;
      long vp = 0;
      for (int j = i; j < end; j++) {
        up += wk[j] * u[j];
        vp += wk[j] * v[j];
      }
      if (up > vp) {
        return 1;
      } else if (up < vp) {
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
  public static ExpVectorLong valueOf(int n) {
    return new ExpVectorLong(n);
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
