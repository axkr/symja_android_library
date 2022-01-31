package org.matheclipse.core.polynomials.longexponent;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.trie.Trie;
import edu.jas.kern.PrettyPrint;
import edu.jas.structure.NotInvertibleException;
import edu.jas.structure.RingElem;

/**
 * GenPolynomial generic polynomials implementing RingElem. n-variate ordered polynomials over C.
 * Objects of this class are intended to be immutable. The implementation is based on TreeMap
 * respectively SortedMap from exponents to coefficients. Only the coefficients are modeled with
 * generic types, the exponents are fixed to ExpVectorLong with long entries (this will eventually
 * be changed in the future). C can also be a non integral domain, e.g. a ModInteger, i.e. it may
 * contain zero divisors, since multiply() does now check for zeros. <b>Note:</b> multiply() now
 * checks for wrong method dispatch for GenSolvablePolynomial.
 */
public class ExprPolynomial implements RingElem<ExprPolynomial>, Iterable<ExprMonomial> {

  /** Iterator over monomials of a polynomial. Adaptor for val.entrySet().iterator(). */
  private static class ExprPolyIterator implements Iterator<ExprMonomial> {

    /** Internal iterator over polynomial map. */
    protected final Iterator<Map.Entry<ExpVectorLong, IExpr>> ms;

    /**
     * Constructor of polynomial iterator.
     *
     * @param m SortetMap of a polynomial.
     */
    public ExprPolyIterator(SortedMap<ExpVectorLong, IExpr> m) {
      ms = m.entrySet().iterator();
    }

    /**
     * Test for availability of a next monomial.
     *
     * @return true if the iteration has more monomials, else false.
     */
    @Override
    public boolean hasNext() {
      return ms.hasNext();
    }

    /**
     * Get next monomial element.
     *
     * @return next monomial.
     */
    @Override
    public ExprMonomial next() {
      return new ExprMonomial(ms.next());
    }

    /** Remove the last monomial returned from underlying set if allowed. */
    @Override
    public void remove() {
      ms.remove();
    }
  }

  /** */
  private static final long serialVersionUID = -3069278103478903325L;

  /** The factory for the polynomial ring. */
  public final ExprPolynomialRing ring;

  /** The data structure for polynomials. */
  protected final SortedMap<ExpVectorLong, IExpr> val; // do not change to
  // TreeMap

  private final boolean debug = Config.DEBUG;

  // protected GenPolynomial() { ring = null; val = null; } // don't use

  /**
   * Private constructor for GenPolynomial.
   *
   * @param r polynomial ring factory.
   * @param t TreeMap with correct ordering.
   */
  private ExprPolynomial(ExprPolynomialRing r, TreeMap<ExpVectorLong, IExpr> t) {
    ring = r;
    val = t;
  }

  /**
   * Constructor for zero GenPolynomial.
   *
   * @param r polynomial ring factory.
   */
  public ExprPolynomial(ExprPolynomialRing r) {
    this(r, new TreeMap<ExpVectorLong, IExpr>(r.tord.getDescendComparator()));
  }

  /**
   * Constructor for GenPolynomial c * x<sup>e</sup>.
   *
   * @param r polynomial ring factory.
   * @param c coefficient.
   * @param e exponent.
   */
  public ExprPolynomial(ExprPolynomialRing r, IExpr c, ExpVectorLong e) {
    this(r);
    if (!c.isZERO()) {
      val.put(e, c);
    }
  }

  /**
   * Constructor for GenPolynomial c * x<sup>0</sup>.
   *
   * @param r polynomial ring factory.
   * @param c coefficient.
   */
  public ExprPolynomial(ExprPolynomialRing r, IExpr c) {
    this(r, c, r.evzero);
  }

  /**
   * Constructor for GenPolynomial x<sup>e</sup>.
   *
   * @param r polynomial ring factory.
   * @param e exponent.
   */
  public ExprPolynomial(ExprPolynomialRing r, ExpVectorLong e) {
    this(r, r.coFac.getONE(), e);
  }

  /**
   * Constructor for GenPolynomial.
   *
   * @param r polynomial ring factory.
   * @param v the SortedMap of some other polynomial.
   */
  protected ExprPolynomial(ExprPolynomialRing r, SortedMap<ExpVectorLong, IExpr> v) {
    this(r);
    if (v.size() > 0) {
      // ExprPolynomialRing.creations++;
      val.putAll(v); // assume no zero coefficients and val is empty
    }
  }

  /**
   * Get the corresponding element factory.
   *
   * @return factory for this Element.
   * @see edu.jas.structure.Element#factory()
   */
  @Override
  public ExprPolynomialRing factory() {
    return ring;
  }

  /**
   * Copy this GenPolynomial.
   *
   * @return copy of this.
   */
  @Override
  public ExprPolynomial copy() {
    return new ExprPolynomial(ring, this.val);
  }

  /**
   * Length of GenPolynomial.
   *
   * @return number of coefficients of this GenPolynomial.
   */
  public int length() {
    return val.size();
  }

  /**
   * ExpVectorLong to coefficient map of GenPolynomial.
   *
   * @return val as unmodifiable SortedMap.
   */
  public SortedMap<ExpVectorLong, IExpr> getMap() {
    // return val;
    return Collections.<ExpVectorLong, IExpr>unmodifiableSortedMap(val);
  }

  /**
   * Put an ExpVectorLong to coefficient entry into the internal map of this GenPolynomial.
   * <b>Note:</b> Do not use this method unless you are constructing a new polynomial. this is
   * modified and breaks the immutability promise of this class.
   *
   * @param c coefficient.
   * @param e exponent.
   */
  public void doPutToMap(ExpVectorLong e, IExpr c) {
    if (debug) {
      IExpr a = val.get(e);
      if (a != null) {
        // logger.error("map entry exists " + e + " to " + a + " new " + c);
      }
    }
    if (!c.isZERO()) {
      val.put(e, c);
    }
  }

  /**
   * Remove an ExpVectorLong to coefficient entry from the internal map of this GenPolynomial.
   * <b>Note:</b> Do not use this method unless you are constructing a new polynomial. this is
   * modified and breaks the immutability promise of this class.
   *
   * @param e exponent.
   * @param c expected coefficient, null for ignore.
   */
  public void doRemoveFromMap(ExpVectorLong e, IExpr c) {
    IExpr b = val.remove(e);
    if (debug) {
      if (c == null) { // ignore b
        return;
      }
      if (!c.equals(b)) {
        // logger.error("map entry wrong " + e + " to " + c + " old " + b);
      }
    }
  }

  /**
   * Put an a sorted map of exponents to coefficients into the internal map of this GenPolynomial.
   * <b>Note:</b> Do not use this method unless you are constructing a new polynomial. this is
   * modified and breaks the immutability promise of this class.
   *
   * @param vals sorted map of exponents and coefficients.
   */
  public void doPutToMap(SortedMap<ExpVectorLong, IExpr> vals) {
    for (Map.Entry<ExpVectorLong, IExpr> me : vals.entrySet()) {
      ExpVectorLong e = me.getKey();
      if (debug) {
        IExpr a = val.get(e);
        if (a != null) {
          // logger.error("map entry exists " + e + " to " + a + " new " + me.getValue());
        }
      }
      IExpr c = me.getValue();
      if (!c.isZERO()) {
        val.put(e, c);
      }
    }
  }

  /**
   * String representation of GenPolynomial.
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (ring.vars != null) {
      return toString(ring.vars);
    }
    StringBuilder s = new StringBuilder();
    s.append(this.getClass().getSimpleName() + ":");
    s.append(ring.coFac.getClass().getSimpleName());
    if (ring.coFac.characteristic().signum() != 0) {
      s.append("(" + ring.coFac.characteristic() + ")");
    }
    s.append("[ ");
    boolean first = true;
    for (Map.Entry<ExpVectorLong, IExpr> m : val.entrySet()) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      }
      s.append(m.getValue().toString());
      s.append(" ");
      s.append(m.getKey().toString());
    }
    s.append(" ] "); // no not use: ring.toString() );
    return s.toString();
  }

  /**
   * String representation of GenPolynomial.
   *
   * @param v names for variables.
   * @see java.lang.Object#toString()
   */
  public String toString(IAST v) {
    StringBuilder s = new StringBuilder();
    if (PrettyPrint.isTrue()) {
      if (val.size() == 0) {
        s.append("0");
      } else {
        // s.append( "( " );
        boolean first = true;
        for (Map.Entry<ExpVectorLong, IExpr> m : val.entrySet()) {
          IExpr c = m.getValue();
          if (first) {
            first = false;
          } else {
            if (c.signum() < 0) {
              s.append(" - ");
              c = c.negate();
            } else {
              s.append(" + ");
            }
          }
          ExpVectorLong e = m.getKey();
          if (!c.isOne() || e.isZERO()) {
            String cs = c.toString();
            // if (c instanceof GenPolynomial || c instanceof
            // AlgebraicNumber) {
            if (cs.indexOf("-") >= 0 || cs.indexOf("+") >= 0) {
              s.append("( ");
              s.append(cs);
              s.append(" )");
            } else {
              s.append(cs);
            }
            s.append(" ");
          }
          if (v != null) {
            s.append(e.toString(v));
          } else {
            s.append(e);
          }
        }
        // s.append(" )");
      }
    } else {
      s.append(this.getClass().getSimpleName() + "[ ");
      if (val.size() == 0) {
        s.append("0");
      } else {
        boolean first = true;
        for (Map.Entry<ExpVectorLong, IExpr> m : val.entrySet()) {
          IExpr c = m.getValue();
          if (first) {
            first = false;
          } else {
            if (c.signum() < 0) {
              s.append(" - ");
              c = c.negate();
            } else {
              s.append(" + ");
            }
          }
          ExpVectorLong e = m.getKey();
          if (!c.isOne() || e.isZERO()) {
            s.append(c.toString());
            s.append(" ");
          }
          s.append(e.toString(v));
        }
      }
      s.append(" ] "); // no not use: ring.toString() );
    }
    return s.toString();
  }

  /**
   * Get a scripting compatible string representation.
   *
   * @return script compatible representation for this Element.
   * @see edu.jas.structure.Element#toScript()
   */
  @Override
  public String toScript() {
    if (isZERO()) {
      return "0";
    }
    StringBuilder s = new StringBuilder();
    if (val.size() > 1) {
      s.append("( ");
    }
    IAST v = ring.vars;
    // if (v == null) {
    // v = ExprPolynomialRing.newVars("x", ring.nvar);
    // }
    boolean first = true;
    for (Map.Entry<ExpVectorLong, IExpr> m : val.entrySet()) {
      IExpr c = m.getValue();
      if (first) {
        first = false;
      } else {
        if (c.signum() < 0) {
          s.append(" - ");
          c = c.negate();
        } else {
          s.append(" + ");
        }
      }
      ExpVectorLong e = m.getKey();
      String cs = c.toScript();
      boolean parenthesis = (cs.indexOf("-") >= 0 || cs.indexOf("+") >= 0);
      if (!c.isOne() || e.isZERO()) {
        if (parenthesis) {
          s.append("( ");
        }
        s.append(cs);
        if (parenthesis) {
          s.append(" )");
        }
        if (!e.isZERO()) {
          s.append(" * ");
        }
      }
      s.append(e.toScript(v));
    }
    if (val.size() > 1) {
      s.append(" )");
    }
    return s.toString();
  }

  /**
   * Get a scripting compatible string representation of the factory.
   *
   * @return script compatible representation for this ElemFactory.
   * @see edu.jas.structure.Element#toScriptFactory()
   */
  @Override
  public String toScriptFactory() {
    return factory().toScript();
  }

  /**
   * Is GenPolynomial&lt;C&gt; zero.
   *
   * @return If this is 0 then true is returned, else false.
   * @see edu.jas.structure.RingElem#isZERO()
   */
  public boolean isZero() {
    return (val.size() == 0);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZERO() {
    return isZero();
  }

  /**
   * Is GenPolynomial&lt;C&gt; one.
   *
   * @return If this is 1 then true is returned, else false.
   * @see edu.jas.structure.RingElem#isOne()
   */
  public boolean isOne() {
    if (val.size() != 1) {
      return false;
    }
    IExpr c = val.get(ring.evzero);
    if (c == null) {
      return false;
    }
    return c.isOne();
  }

  @Override
  public boolean isONE() {
    return isOne();
  }

  /**
   * Is GenPolynomial&lt;C&gt; a unit.
   *
   * @return If this is a unit then true is returned, else false.
   * @see edu.jas.structure.RingElem#isUnit()
   */
  @Override
  public boolean isUnit() {
    if (val.size() != 1) {
      return false;
    }
    IExpr c = val.get(ring.evzero);
    if (c == null) {
      return false;
    }
    return c.isUnit();
  }

  /**
   * Is GenPolynomial&lt;C&gt; a constant.
   *
   * @return If this is a constant polynomial then true is returned, else false.
   */
  public boolean isConstant() {
    if (val.size() != 1) {
      return false;
    }
    IExpr c = val.get(ring.evzero);
    if (c == null) {
      return false;
    }
    return true;
  }

  /**
   * Is GenPolynomial&lt;C&gt; homogeneous.
   *
   * @return true, if this is homogeneous, else false.
   */
  public boolean isHomogeneous() {
    if (val.size() <= 1) {
      return true;
    }
    long deg = -1;
    for (ExpVectorLong e : val.keySet()) {
      if (deg < 0) {
        deg = e.totalDeg();
      } else if (deg != e.totalDeg()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Comparison with any other object.
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object B) {
    if (B == null) {
      return false;
    }
    if (!(B instanceof ExprPolynomial)) {
      return false;
    }
    ExprPolynomial a = (ExprPolynomial) B;
    return this.compareTo(a) == 0;
  }

  /**
   * Hash code for this polynomial.
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int h;
    h = (ring.hashCode() << 27);
    h += val.hashCode();
    return h;
  }

  /**
   * GenPolynomial comparison.
   *
   * @param b GenPolynomial.
   * @return sign(this-b).
   */
  @Override
  public int compareTo(ExprPolynomial b) {
    if (b == null) {
      return 1;
    }
    SortedMap<ExpVectorLong, IExpr> av = this.val;
    SortedMap<ExpVectorLong, IExpr> bv = b.val;
    Iterator<Map.Entry<ExpVectorLong, IExpr>> ai = av.entrySet().iterator();
    Iterator<Map.Entry<ExpVectorLong, IExpr>> bi = bv.entrySet().iterator();
    int s = 0;
    int c = 0;
    while (ai.hasNext() && bi.hasNext()) {
      Map.Entry<ExpVectorLong, IExpr> aie = ai.next();
      Map.Entry<ExpVectorLong, IExpr> bie = bi.next();
      ExpVectorLong ae = aie.getKey();
      ExpVectorLong be = bie.getKey();
      s = ae.compareTo(be);
      if (s != 0) {
        return s;
      }
      if (c == 0) {
        IExpr ac = aie.getValue(); // av.get(ae);
        IExpr bc = bie.getValue(); // bv.get(be);
        c = ac.compareTo(bc);
      }
    }
    if (ai.hasNext()) {
      return 1;
    }
    if (bi.hasNext()) {
      return -1;
    }
    // now all keys are equal
    return c;
  }

  /**
   * GenPolynomial signum.
   *
   * @return sign(ldcf(this)).
   * @deprecated for comparability with JAS only
   */
  @Override
  @Deprecated
  public int signum() {
    if (this.isZERO()) {
      return 0;
    }
    ExpVectorLong t = val.firstKey();
    IExpr c = val.get(t);
    return c.signum();
  }

  /**
   * Number of variables.
   *
   * @return ring.nvar.
   */
  public int numberOfVariables() {
    return ring.nvar;
  }

  /**
   * Leading monomial.
   *
   * @return first map entry.
   */
  public Map.Entry<ExpVectorLong, IExpr> leadingMonomial() {
    if (val.size() == 0)
      return null;
    Iterator<Map.Entry<ExpVectorLong, IExpr>> ai = val.entrySet().iterator();
    return ai.next();
  }

  /**
   * Leading exponent vector.
   *
   * @return first exponent.
   */
  public ExpVectorLong leadingExpVectorLong() {
    if (val.size() == 0) {
      return null; // ring.evzero? needs many changes
    }
    return val.firstKey();
  }

  /**
   * Trailing exponent vector.
   *
   * @return last exponent.
   */
  public ExpVectorLong trailingExpVectorLong() {
    if (val.size() == 0) {
      return ring.evzero; // or null ?;
    }
    return val.lastKey();
  }

  /**
   * Leading base coefficient.
   *
   * @return first coefficient.
   */
  public IExpr leadingBaseCoefficient() {
    if (val.size() == 0) {
      return ring.coFac.getZERO();
    }
    return val.get(val.firstKey());
  }

  /**
   * Trailing base coefficient.
   *
   * @return coefficient of constant term.
   * @deprecated for comparability with JAS only
   */
  @Deprecated
  private IExpr trailingBaseCoefficient() {
    IExpr c = val.get(ring.evzero);
    if (c == null) {
      return ring.coFac.getZERO();
    }
    return c;
  }

  /**
   * Coefficient.
   *
   * @param e exponent.
   * @return coefficient for given exponent.
   */
  public IExpr coefficient(ExpVectorLong e) {
    IExpr c = val.get(e);
    if (c == null) {
      c = ring.coFac.getZERO();
    }
    return c;
  }

  /**
   * Reductum.
   *
   * @return this - leading monomial.
   */
  public ExprPolynomial reductum() {
    if (val.size() <= 1) {
      return ring.getZero();
    }
    Iterator<ExpVectorLong> ai = val.keySet().iterator();
    ExpVectorLong lt = ai.next();
    lt = ai.next(); // size > 1
    SortedMap<ExpVectorLong, IExpr> red = val.tailMap(lt);
    ExprPolynomial r = ring.getZero().copy();
    r.doPutToMap(red); // new GenPolynomial(ring, red);
    return r;
  }

  /**
   * Degree in variable i.
   *
   * @return maximal degree in the variable i.
   */
  public long degree(int i) {
    if (val.size() == 0) {
      return 0; // 0 or -1 ?;
    }
    int j;
    if (i >= 0) {
      j = ring.nvar - 1 - i;
    } else { // python like -1 means main variable
      j = ring.nvar + i;
    }
    long deg = 0;
    for (ExpVectorLong e : val.keySet()) {
      long d = e.getVal(j);
      if (d > deg) {
        deg = d;
      }
    }
    return deg;
  }

  /**
   * Maximal degree.
   *
   * @return maximal degree in any variables.
   */
  public long degree() {
    if (val.size() == 0) {
      return 0; // 0 or -1 ?;
    }
    long deg = 0;
    for (ExpVectorLong e : val.keySet()) {
      long d = e.maxDeg();
      if (d > deg) {
        deg = d;
      }
    }
    return deg;
  }

  /**
   * Total degree.
   *
   * @return total degree in any variables.
   */
  public long totalDegree() {
    if (val.size() == 0) {
      return 0; // 0 or -1 ?;
    }
    long deg = 0;
    for (ExpVectorLong e : val.keySet()) {
      long d = e.totalDeg();
      if (d > deg) {
        deg = d;
      }
    }
    return deg;
  }

  /**
   * Weight degree.
   *
   * @return weight degree in all variables.
   */
  public long weightDegree() {
    long[][] w = ring.tord.getWeight();
    if (w == null || w.length == 0) {
      return totalDegree(); // assume weight 1
    }
    if (val.isEmpty()) {
      return -1L; // 0 or -1 ?;
    }
    long deg = 0;
    for (ExpVectorLong e : val.keySet()) {
      long d = e.weightDeg(w);
      if (d > deg) {
        deg = d;
      }
    }
    return deg;
  }

  /**
   * Leading weight polynomial.
   *
   * @return polynomial with terms of maximal weight degree.
   */
  public ExprPolynomial leadingWeightPolynomial() {
    if (val.isEmpty()) {
      return ring.getZero();
    }
    long[][] w = ring.tord.getWeight();
    long maxw;
    if (w == null || w.length == 0) {
      maxw = totalDegree(); // assume weights = 1
    } else {
      maxw = weightDegree();
    }
    ExprPolynomial wp = new ExprPolynomial(ring);
    for (Map.Entry<ExpVectorLong, IExpr> m : val.entrySet()) {
      ExpVectorLong e = m.getKey();
      long d = e.weightDeg(w);
      if (d >= maxw) {
        wp.val.put(e, m.getValue());
      }
    }
    return wp;
  }

  /**
   * Is GenPolynomial&lt;C&gt; homogeneous with respect to a weight.
   *
   * @return true, if this is weight homogeneous, else false.
   */
  public boolean isWeightHomogeneous() {
    if (val.size() <= 1) {
      return true;
    }
    long[][] w = ring.tord.getWeight();
    if (w == null || w.length == 0) {
      return isHomogeneous(); // assume weights = 1
    }
    long deg = -1;
    for (ExpVectorLong e : val.keySet()) {
      if (deg < 0) {
        deg = e.weightDeg(w);
      } else if (deg != e.weightDeg(w)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Maximal degree vector.
   *
   * @return maximal degree vector of all variables.
   */
  public ExpVectorLong degreeVector() {
    ExpVectorLong deg = ring.evzero;
    if (val.size() == 0) {
      return deg;
    }
    for (ExpVectorLong e : val.keySet()) {
      deg = deg.lcm(e);
    }
    return deg;
  }

  /**
   * GenPolynomial maximum norm.
   *
   * @return ||this||.
   */
  public IExpr maxNorm() {
    IExpr n = ring.getZEROCoefficient();
    for (IExpr c : val.values()) {
      IExpr x = c.abs();
      if (n.compareTo(x) < 0) {
        n = x;
      }
    }
    return n;
  }

  /**
   * GenPolynomial sum norm.
   *
   * @return sum of all absolute values of coefficients.
   */
  public IExpr sumNorm() {
    IExpr n = ring.getZEROCoefficient();
    for (IExpr c : val.values()) {
      IExpr x = c.abs();
      n = n.add(x);
    }
    return n;
  }

  /**
   * GenPolynomial summation.
   *
   * @param S GenPolynomial.
   * @return this+S.
   */
  // public <T extends GenPolynomial> T sum(T /*GenPolynomial*/ S) {
  @Override
  public ExprPolynomial sum(ExprPolynomial S) {
    if (S == null) {
      return this;
    }
    if (S.isZERO()) {
      return this;
    }
    if (this.isZERO()) {
      return S;
    }
    assert (ring.nvar == S.ring.nvar);
    ExprPolynomial n = this.copy(); // new GenPolynomial(ring, val);
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    SortedMap<ExpVectorLong, IExpr> sv = S.val;
    for (Map.Entry<ExpVectorLong, IExpr> me : sv.entrySet()) {
      ExpVectorLong e = me.getKey();
      IExpr y = me.getValue(); // sv.get(e); // assert y != null
      IExpr x = nv.get(e);
      if (x != null) {
        x = x.add(y);
        if (!x.isZERO()) {
          nv.put(e, x);
        } else {
          nv.remove(e);
        }
      } else {
        nv.put(e, y);
      }
    }
    return n;
  }

  /**
   * GenPolynomial addition. This method is not very efficient, since this is copied.
   *
   * @param a coefficient.
   * @param e exponent.
   * @return this + a x<sup>e</sup>.
   */
  public ExprPolynomial sum(IExpr a, ExpVectorLong e) {
    if (a == null) {
      return this;
    }
    if (a.isZERO()) {
      return this;
    }
    ExprPolynomial n = this.copy(); // new GenPolynomial(ring, val);
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    // if ( nv.size() == 0 ) { nv.put(e,a); return n; }
    IExpr x = nv.get(e);
    if (x != null) {
      x = x.add(a);
      if (!x.isZERO()) {
        nv.put(e, x);
      } else {
        nv.remove(e);
      }
    } else {
      nv.put(e, a);
    }
    return n;
  }

  /**
   * GenPolynomial addition. This method is not very efficient, since this is copied.
   *
   * @param a coefficient.
   * @return this + a x<sup>0</sup>.
   */
  public ExprPolynomial sum(IExpr a) {
    return sum(a, ring.evzero);
  }

  /**
   * GenPolynomial destructive summation.
   *
   * @param S GenPolynomial.
   */
  public void doAddTo(ExprPolynomial S) {
    if (S == null || S.isZERO()) {
      return;
    }
    if (this.isZERO()) {
      this.val.putAll(S.val);
      return;
    }
    assert (ring.nvar == S.ring.nvar);
    SortedMap<ExpVectorLong, IExpr> nv = this.val;
    SortedMap<ExpVectorLong, IExpr> sv = S.val;
    for (Map.Entry<ExpVectorLong, IExpr> me : sv.entrySet()) {
      ExpVectorLong e = me.getKey();
      IExpr y = me.getValue(); // sv.get(e); // assert y != null
      IExpr x = nv.get(e);
      if (x != null) {
        x = x.add(y);
        if (!x.isZERO()) {
          nv.put(e, x);
        } else {
          nv.remove(e);
        }
      } else {
        nv.put(e, y);
      }
    }
  }

  /**
   * GenPolynomial destructive summation.
   *
   * @param a coefficient.
   * @param e exponent.
   */
  public void doAddTo(IExpr a, ExpVectorLong e) {
    if (a == null || a.isZERO()) {
      return;
    }
    SortedMap<ExpVectorLong, IExpr> nv = this.val;
    IExpr x = nv.get(e);
    if (x != null) {
      x = x.add(a);
      if (!x.isZERO()) {
        nv.put(e, x);
      } else {
        nv.remove(e);
      }
    } else {
      nv.put(e, a);
    }
  }

  /**
   * GenPolynomial destructive summation.
   *
   * @param a coefficient.
   */
  public void doAddTo(IExpr a) {
    doAddTo(a, ring.evzero);
  }

  /**
   * GenPolynomial subtraction.
   *
   * @param S GenPolynomial.
   * @return this-S.
   */
  @Override
  public ExprPolynomial subtract(ExprPolynomial S) {
    if (S == null) {
      return this;
    }
    if (S.isZERO()) {
      return this;
    }
    if (this.isZERO()) {
      return S.negate();
    }
    assert (ring.nvar == S.ring.nvar);
    ExprPolynomial n = this.copy(); // new GenPolynomial(ring, val);
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    SortedMap<ExpVectorLong, IExpr> sv = S.val;
    for (Map.Entry<ExpVectorLong, IExpr> me : sv.entrySet()) {
      ExpVectorLong e = me.getKey();
      IExpr y = me.getValue(); // sv.get(e); // assert y != null
      IExpr x = nv.get(e);
      if (x != null) {
        x = x.subtract(y);
        if (!x.isZERO()) {
          nv.put(e, x);
        } else {
          nv.remove(e);
        }
      } else {
        nv.put(e, y.negate());
      }
    }
    return n;
  }

  /**
   * GenPolynomial subtraction. This method is not very efficient, since this is copied.
   *
   * @param a coefficient.
   * @param e exponent.
   * @return this - a x<sup>e</sup>.
   */
  public ExprPolynomial subtract(IExpr a, ExpVectorLong e) {
    if (a == null || a.isZERO()) {
      return this;
    }
    ExprPolynomial n = this.copy();
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    IExpr x = nv.get(e);
    if (x != null) {
      x = x.subtract(a);
      if (!x.isZERO()) {
        nv.put(e, x);
      } else {
        nv.remove(e);
      }
    } else {
      nv.put(e, a.negate());
    }
    return n;
  }

  /**
   * GenPolynomial subtract. This method is not very efficient, since this is copied.
   *
   * @param a coefficient.
   * @return this + a x<sup>0</sup>.
   */
  public ExprPolynomial subtract(IExpr a) {
    return subtract(a, ring.evzero);
  }

  /**
   * GenPolynomial subtract a multiple.
   *
   * @param a coefficient.
   * @param S GenPolynomial.
   * @return this - a S.
   */
  public ExprPolynomial subtractMultiple(IExpr a, ExprPolynomial S) {
    if (a == null || a.isZERO()) {
      return this;
    }
    if (S == null || S.isZERO()) {
      return this;
    }
    if (this.isZERO()) {
      return S.multiply(a.negate());
    }
    assert (ring.nvar == S.ring.nvar);
    ExprPolynomial n = this.copy();
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    SortedMap<ExpVectorLong, IExpr> sv = S.val;
    for (Map.Entry<ExpVectorLong, IExpr> me : sv.entrySet()) {
      ExpVectorLong f = me.getKey();
      IExpr y = me.getValue(); // assert y != null
      y = a.multiply(y);
      IExpr x = nv.get(f);
      if (x != null) {
        x = x.subtract(y);
        if (!x.isZERO()) {
          nv.put(f, x);
        } else {
          nv.remove(f);
        }
      } else if (!y.isZERO()) {
        nv.put(f, y.negate());
      }
    }
    return n;
  }

  /**
   * GenPolynomial subtract a multiple.
   *
   * @param a coefficient.
   * @param e exponent.
   * @param S GenPolynomial.
   * @return this - a x<sup>e</sup> S.
   */
  public ExprPolynomial subtractMultiple(IExpr a, ExpVectorLong e, ExprPolynomial S) {
    if (a == null || a.isZERO()) {
      return this;
    }
    if (S == null || S.isZERO()) {
      return this;
    }
    if (this.isZERO()) {
      return S.multiply(a.negate(), e);
    }
    assert (ring.nvar == S.ring.nvar);
    ExprPolynomial n = this.copy();
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    SortedMap<ExpVectorLong, IExpr> sv = S.val;
    for (Map.Entry<ExpVectorLong, IExpr> me : sv.entrySet()) {
      ExpVectorLong f = me.getKey();
      f = e.sum(f);
      IExpr y = me.getValue(); // assert y != null
      y = a.multiply(y);
      IExpr x = nv.get(f);
      if (x != null) {
        x = x.subtract(y);
        if (!x.isZERO()) {
          nv.put(f, x);
        } else {
          nv.remove(f);
        }
      } else if (!y.isZERO()) {
        nv.put(f, y.negate());
      }
    }
    return n;
  }

  /**
   * GenPolynomial scale and subtract a multiple.
   *
   * @param b scale factor.
   * @param a coefficient.
   * @param S GenPolynomial.
   * @return this * b - a S.
   */
  public ExprPolynomial scaleSubtractMultiple(IExpr b, IExpr a, ExprPolynomial S) {
    if (a == null || S == null) {
      return this.multiply(b);
    }
    if (a.isZERO() || S.isZERO()) {
      return this.multiply(b);
    }
    if (this.isZERO() || b == null || b.isZERO()) {
      return S.multiply(a.negate()); // left?
    }
    if (b.isOne()) {
      return subtractMultiple(a, S);
    }
    assert (ring.nvar == S.ring.nvar);
    ExprPolynomial n = this.multiply(b);
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    SortedMap<ExpVectorLong, IExpr> sv = S.val;
    for (Map.Entry<ExpVectorLong, IExpr> me : sv.entrySet()) {
      ExpVectorLong f = me.getKey();
      // f = e.sum(f);
      IExpr y = me.getValue(); // assert y != null
      y = a.multiply(y); // now y can be zero
      IExpr x = nv.get(f);
      if (x != null) {
        x = x.subtract(y);
        if (!x.isZERO()) {
          nv.put(f, x);
        } else {
          nv.remove(f);
        }
      } else if (!y.isZERO()) {
        nv.put(f, y.negate());
      }
    }
    return n;
  }

  /**
   * GenPolynomial scale and subtract a multiple.
   *
   * @param b scale factor.
   * @param a coefficient.
   * @param e exponent.
   * @param S GenPolynomial.
   * @return this * b - a x<sup>e</sup> S.
   */
  public ExprPolynomial scaleSubtractMultiple(IExpr b, IExpr a, ExpVectorLong e, ExprPolynomial S) {
    if (a == null || S == null) {
      return this.multiply(b);
    }
    if (a.isZERO() || S.isZERO()) {
      return this.multiply(b);
    }
    if (this.isZERO() || b == null || b.isZERO()) {
      return S.multiply(a.negate(), e);
    }
    if (b.isOne()) {
      return subtractMultiple(a, e, S);
    }
    assert (ring.nvar == S.ring.nvar);
    ExprPolynomial n = this.multiply(b);
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    SortedMap<ExpVectorLong, IExpr> sv = S.val;
    for (Map.Entry<ExpVectorLong, IExpr> me : sv.entrySet()) {
      ExpVectorLong f = me.getKey();
      f = e.sum(f);
      IExpr y = me.getValue(); // assert y != null
      y = a.multiply(y); // now y can be zero
      IExpr x = nv.get(f);
      if (x != null) {
        x = x.subtract(y);
        if (!x.isZERO()) {
          nv.put(f, x);
        } else {
          nv.remove(f);
        }
      } else if (!y.isZERO()) {
        nv.put(f, y.negate());
      }
    }
    return n;
  }

  /**
   * GenPolynomial scale and subtract a multiple.
   *
   * @param b scale factor.
   * @param g scale exponent.
   * @param a coefficient.
   * @param e exponent.
   * @param S GenPolynomial.
   * @return this * a x<sup>g</sup> - a x<sup>e</sup> S.
   */
  public ExprPolynomial scaleSubtractMultiple(IExpr b, ExpVectorLong g, IExpr a, ExpVectorLong e,
      ExprPolynomial S) {
    if (a == null || S == null) {
      return this.multiply(b, g);
    }
    if (a.isZERO() || S.isZERO()) {
      return this.multiply(b, g);
    }
    if (this.isZERO() || b == null || b.isZERO()) {
      return S.multiply(a.negate(), e);
    }
    if (b.isOne() && g.isZERO()) {
      return subtractMultiple(a, e, S);
    }
    assert (ring.nvar == S.ring.nvar);
    ExprPolynomial n = this.multiply(b, g);
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    SortedMap<ExpVectorLong, IExpr> sv = S.val;
    for (Map.Entry<ExpVectorLong, IExpr> me : sv.entrySet()) {
      ExpVectorLong f = me.getKey();
      f = e.sum(f);
      IExpr y = me.getValue(); // assert y != null
      y = a.multiply(y); // y can be zero now
      IExpr x = nv.get(f);
      if (x != null) {
        x = x.subtract(y);
        if (!x.isZERO()) {
          nv.put(f, x);
        } else {
          nv.remove(f);
        }
      } else if (!y.isZERO()) {
        nv.put(f, y.negate());
      }
    }
    return n;
  }

  /**
   * GenPolynomial negation.
   *
   * @return -this.
   */
  @Override
  public ExprPolynomial negate() {
    ExprPolynomial n = ring.getZero().copy();
    // new GenPolynomial(ring, ring.getZERO().val);
    SortedMap<ExpVectorLong, IExpr> v = n.val;
    for (Map.Entry<ExpVectorLong, IExpr> m : val.entrySet()) {
      IExpr x = m.getValue(); // != null, 0
      v.put(m.getKey(), x.negate());
      // or m.setValue( x.negate() ) if this cloned
    }
    return n;
  }

  /**
   * If this polynomial contains negative exponents, multiply this polynomial by the negated minimal
   * exponents for that variable.
   *
   * @return
   */
  public ExprPolynomial multiplyByMinimumNegativeExponents() {
    long[] result = new long[numberOfVariables()];
    boolean negativeExponents = false;
    for (Map.Entry<ExpVectorLong, IExpr> m : val.entrySet()) {
      long[] k = m.getKey().getVal();
      for (int i = 0; i < k.length; i++) {
        if (k[i] < 0 && k[i] < result[i]) {
          result[i] = k[i];
          negativeExponents = true;
        }
      }
    }
    if (negativeExponents) {
      for (int i = 0; i < result.length; i++) {
        if (result[i] < 0) {
          result[i] *= (-1);
        }
      }
      return multiply(new ExpVectorLong(result));
    }
    return this;
  }

  /**
   * GenPolynomial absolute value, i.e. leadingCoefficient &gt; 0.
   *
   * @return abs(this).
   */
  @Override
  public ExprPolynomial abs() {
    if (leadingBaseCoefficient().signum() < 0) {
      return this.negate();
    }
    return this;
  }

  /**
   * GenPolynomial multiplication.
   *
   * @param S GenPolynomial.
   * @return this*S.
   */
  @Override
  public ExprPolynomial multiply(ExprPolynomial S) {
    if (S == null) {
      return ring.getZero();
    }
    if (S.isZERO()) {
      return ring.getZero();
    }
    if (this.isZERO()) {
      return this;
    }
    assert (ring.nvar == S.ring.nvar);
    // if (this instanceof GenSolvablePolynomial && S instanceof
    // GenSolvablePolynomial) {
    // // throw new RuntimeException("wrong method dispatch in JRE ");
    // logger.debug("warn: wrong method dispatch in JRE multiply(S) - trying
    // to fix");
    // GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
    // GenSolvablePolynomial<C> Sp = (GenSolvablePolynomial<C>) S;
    // return T.multiply(Sp);
    // }
    ExprPolynomial p = ring.getZero().copy();
    SortedMap<ExpVectorLong, IExpr> pv = p.val;
    for (Map.Entry<ExpVectorLong, IExpr> m1 : val.entrySet()) {
      IExpr c1 = m1.getValue();
      ExpVectorLong e1 = m1.getKey();
      for (Map.Entry<ExpVectorLong, IExpr> m2 : S.val.entrySet()) {
        IExpr c2 = m2.getValue();
        ExpVectorLong e2 = m2.getKey();
        IExpr c = c1.multiply(c2); // check non zero if not domain
        if (!c.isZERO()) {
          ExpVectorLong e = e1.sum(e2);
          IExpr c0 = pv.get(e);
          if (c0 == null) {
            pv.put(e, c);
          } else {
            c0 = c0.add(c);
            if (!c0.isZERO()) {
              pv.put(e, c0);
            } else {
              pv.remove(e);
            }
          }
        }
      }
    }
    return p;
  }

  /**
   * GenPolynomial multiplication. Product with coefficient ring element.
   *
   * @param s coefficient.
   * @return this*s.
   */
  public ExprPolynomial multiply(IExpr s) {
    if (s == null) {
      return ring.getZero();
    }
    if (s.isZERO()) {
      return ring.getZero();
    }
    if (this.isZERO()) {
      return this;
    }
    // if (this instanceof GenSolvablePolynomial) {
    // // throw new RuntimeException("wrong method dispatch in JRE ");
    // logger.debug("warn: wrong method dispatch in JRE multiply(s) - trying
    // to fix");
    // GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
    // return T.multiply(s);
    // }
    ExprPolynomial p = ring.getZero().copy();
    SortedMap<ExpVectorLong, IExpr> pv = p.val;
    for (Map.Entry<ExpVectorLong, IExpr> m1 : val.entrySet()) {
      IExpr c1 = m1.getValue();
      ExpVectorLong e1 = m1.getKey();
      IExpr c = c1.multiply(s); // check non zero if not domain
      if (!c.isZERO()) {
        pv.put(e1, c); // or m1.setValue( c )
      }
    }
    return p;
  }

  /**
   * GenPolynomial monic, i.e. leadingCoefficient == 1. If leadingCoefficient is not invertible
   * returns this unmodified.
   *
   * @return monic(this).
   */
  public ExprPolynomial monic() {
    if (this.isZERO()) {
      return this;
    }
    IExpr lc = leadingBaseCoefficient();
    if (!lc.isUnit()) {
      return this;
    }
    IExpr lm = lc.inverse();
    return multiply(lm);
  }

  /**
   * GenPolynomial multiplication. Product with ring element and exponent vector.
   *
   * @param s coefficient.
   * @param e exponent.
   * @return this * s x<sup>e</sup>.
   */
  public ExprPolynomial multiply(IExpr s, ExpVectorLong e) {
    if (s == null) {
      return ring.getZero();
    }
    if (s.isZERO()) {
      return ring.getZero();
    }
    if (this.isZERO()) {
      return this;
    }
    // if (this instanceof GenSolvablePolynomial) {
    // // throw new RuntimeException("wrong method dispatch in JRE ");
    // logger.debug("warn: wrong method dispatch in JRE multiply(s,e) -
    // trying to fix");
    // GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
    // return T.multiply(s, e);
    // }
    ExprPolynomial p = ring.getZero().copy();
    SortedMap<ExpVectorLong, IExpr> pv = p.val;
    for (Map.Entry<ExpVectorLong, IExpr> m1 : val.entrySet()) {
      IExpr c1 = m1.getValue();
      ExpVectorLong e1 = m1.getKey();
      IExpr c = c1.multiply(s); // check non zero if not domain
      if (!c.isZERO()) {
        ExpVectorLong e2 = e1.sum(e);
        pv.put(e2, c);
      }
    }
    return p;
  }

  /**
   * GenPolynomial multiplication. Product with exponent vector.
   *
   * @param e exponent (!= null).
   * @return this * x<sup>e</sup>.
   */
  public ExprPolynomial multiply(ExpVectorLong e) {
    // assert e != null. This is never allowed.
    if (this.isZERO()) {
      return this;
    }
    // if (this instanceof GenSolvablePolynomial) {
    // // throw new RuntimeException("wrong method dispatch in JRE ");
    // logger.debug("warn: wrong method dispatch in JRE multiply(e) - trying
    // to fix");
    // GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
    // return T.multiply(e);
    // }
    ExprPolynomial p = ring.getZero().copy();
    SortedMap<ExpVectorLong, IExpr> pv = p.val;
    for (Map.Entry<ExpVectorLong, IExpr> m1 : val.entrySet()) {
      IExpr c1 = m1.getValue();
      ExpVectorLong e1 = m1.getKey();
      ExpVectorLong e2 = e1.sum(e);
      pv.put(e2, c1);
    }
    return p;
  }

  /**
   * GenPolynomial multiplication. Product with 'monomial'.
   *
   * @param m 'monomial'.
   * @return this * m.
   */
  public ExprPolynomial multiply(Map.Entry<ExpVectorLong, IExpr> m) {
    if (m == null) {
      return ring.getZero();
    }
    return multiply(m.getValue(), m.getKey());
  }

  /**
   * GenPolynomial division. Division by coefficient ring element. Fails, if exact division is not
   * possible.
   *
   * @param s coefficient.
   * @return this/s.
   */
  public ExprPolynomial divide(IExpr s) {
    if (s == null || s.isZERO()) {
      throw new ArithmeticException("division by zero");
    }
    if (this.isZERO()) {
      return this;
    }
    // C t = s.inverse();
    // return multiply(t);
    ExprPolynomial p = ring.getZero().copy();
    SortedMap<ExpVectorLong, IExpr> pv = p.val;
    for (Map.Entry<ExpVectorLong, IExpr> m : val.entrySet()) {
      ExpVectorLong e = m.getKey();
      IExpr c1 = m.getValue();
      IExpr c = c1.divide(s);
      if (debug) {
        IExpr x = c1.remainder(s);
        if (!x.isZERO()) {
          // logger.info("divide x = " + x);
          throw new ArithmeticException("no exact division: " + c1 + "/" + s);
        }
      }
      if (c.isZERO()) {
        throw new ArithmeticException("no exact division: " + c1 + "/" + s + ", in " + this);
      }
      pv.put(e, c); // or m1.setValue( c )
    }
    return p;
  }

  /**
   * GenPolynomial division with remainder. Fails, if exact division by leading base coefficient is
   * not possible. Meaningful only for univariate polynomials over fields, but works in any case.
   *
   * @param S nonzero GenPolynomial with invertible leading coefficient.
   * @return [ quotient , remainder ] with this = quotient * S + remainder and deg(remainder) &lt;
   *         deg(S) or remiander = 0. Or <code>null</code> is the evaluation was not possible.
   * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
   */
  @Override
  public ExprPolynomial[] quotientRemainder(ExprPolynomial S) {
    if (S == null || S.isZERO()) {
      throw new ArithmeticException("division by zero");
    }
    IExpr c = S.leadingBaseCoefficient();
    if (!c.isUnit()) {
      throw new ArithmeticException("lbcf not invertible " + c);
    }
    IExpr ci = c.inverse();
    assert (ring.nvar == S.ring.nvar);
    ExpVectorLong e = S.leadingExpVectorLong();
    ExprPolynomial h;
    ExprPolynomial q = ring.getZero().copy();
    ExprPolynomial r = this.copy();
    while (!r.isZERO()) {
      ExpVectorLong f = r.leadingExpVectorLong();
      if (f.multipleOf(e)) {
        IExpr a = r.leadingBaseCoefficient();
        ExpVectorLong g = f.subtract(e);
        a = a.multiplyDistributed(ci);
        if (a.isZERO()) {
          return null;
        }
        q = q.sum(a, g);
        h = S.multiply(a, g);
        r = r.subtract(h);
        // IExpr a = r.leadingBaseCoefficient();
        // f = f.subtract(e);
        // a = a.multiply(ci);
        // q = q.sum(a, f);
        // h = S.multiply(a, f);
        // r = r.subtract(h);
      } else {
        break;
      }
    }
    return new ExprPolynomial[] {q, r};
  }

  /**
   * GenPolynomial division with remainder. Fails, if exact division by leading base coefficient is
   * not possible. Meaningful only for univariate polynomials over fields, but works in any case.
   *
   * @param S nonzero GenPolynomial with invertible leading coefficient.
   * @return [ quotient , remainder ] with this = quotient * S + remainder and deg(remainder) &lt;
   *         deg(S) or remiander = 0.
   * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
   * @deprecated use quotientRemainder()
   */
  @Deprecated
  private ExprPolynomial[] divideAndRemainder(ExprPolynomial S) {
    return quotientRemainder(S);
  }

  /**
   * GenPolynomial division. Fails, if exact division by leading base coefficient is not possible.
   * Meaningful only for univariate polynomials over fields, but works in any case.
   *
   * @param S nonzero GenPolynomial with invertible leading coefficient.
   * @return quotient with this = quotient * S + remainder.
   * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
   */
  @Override
  public ExprPolynomial divide(ExprPolynomial S) {
    // if (this instanceof GenSolvablePolynomial || S instanceof
    // GenSolvablePolynomial) {
    // // throw new RuntimeException("wrong method dispatch in JRE ");
    // // logger.debug("warn: wrong method dispatch in JRE multiply(S) -
    // trying to fix");
    // GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
    // GenSolvablePolynomial<C> Sp = (GenSolvablePolynomial<C>) S;
    // return T.quotientRemainder(Sp)[0];
    // }
    return quotientRemainder(S)[0];
  }

  /**
   * GenPolynomial remainder. Fails, if exact division by leading base coefficient is not possible.
   * Meaningful only for univariate polynomials over fields, but works in any case.
   *
   * @param S nonzero GenPolynomial with invertible leading coefficient.
   * @return remainder with this = quotient * S + remainder.
   * @see edu.jas.poly.PolyUtil#baseSparsePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial)
   */
  @Override
  public ExprPolynomial remainder(ExprPolynomial S) {
    // if (this instanceof GenSolvablePolynomial || S instanceof
    // GenSolvablePolynomial) {
    // // throw new RuntimeException("wrong method dispatch in JRE ");
    // // logger.debug("warn: wrong method dispatch in JRE multiply(S) -
    // trying to fix");
    // GenSolvablePolynomial<C> T = (GenSolvablePolynomial<C>) this;
    // GenSolvablePolynomial<C> Sp = (GenSolvablePolynomial<C>) S;
    // return T.quotientRemainder(Sp)[1];
    // }
    if (S == null || S.isZERO()) {
      throw new ArithmeticException("division by zero");
    }
    IExpr c = S.leadingBaseCoefficient();
    if (!c.isUnit()) {
      throw new ArithmeticException("lbc not invertible " + c);
    }
    IExpr ci = c.inverse();
    assert (ring.nvar == S.ring.nvar);
    ExpVectorLong e = S.leadingExpVectorLong();
    ExprPolynomial h;
    ExprPolynomial r = this.copy();
    while (!r.isZERO()) {
      ExpVectorLong f = r.leadingExpVectorLong();
      if (f.multipleOf(e)) {
        IExpr a = r.leadingBaseCoefficient();
        f = f.subtract(e);
        // logger.info("red div = " + e);
        a = a.multiplyDistributed(ci);
        h = S.multiply(a, f);
        r = r.subtract(h);
      } else {
        break;
      }
    }
    return r;
  }

  /**
   * GenPolynomial greatest common divisor. Only for univariate polynomials over fields.
   *
   * @param S GenPolynomial.
   * @return gcd(this,S).
   */
  @Override
  public ExprPolynomial gcd(ExprPolynomial S) {
    if (S == null || S.isZERO()) {
      return this;
    }
    if (this.isZERO()) {
      return S;
    }
    if (ring.nvar != 1) {
      throw new IllegalArgumentException("not univariate polynomials" + ring);
    }
    ExprPolynomial x;
    ExprPolynomial q = this;
    ExprPolynomial r = S;
    while (!r.isZERO()) {
      x = q.remainder(r);
      q = r;
      r = x;
    }
    return q.monic(); // normalize
  }

  /**
   * GenPolynomial extended greatest comon divisor. Only for univariate polynomials over fields.
   *
   * @param S GenPolynomial.
   * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S) or <code>null</code> is the
   *         evaluation was not possible.
   */
  @Override
  public ExprPolynomial[] egcd(ExprPolynomial S) {
    ExprPolynomial[] ret = new ExprPolynomial[3];
    ret[0] = null;
    ret[1] = null;
    ret[2] = null;
    if (S == null || S.isZERO()) {
      ret[0] = this;
      ret[1] = this.ring.getOne();
      ret[2] = this.ring.getZero();
      return ret;
    }
    if (this.isZERO()) {
      ret[0] = S;
      ret[1] = this.ring.getZero();
      ret[2] = this.ring.getOne();
      return ret;
    }
    if (ring.nvar != 1) {
      throw new IllegalArgumentException(
          this.getClass().getName() + " not univariate polynomials" + ring);
    }
    if (this.isConstant() && S.isConstant()) {
      IExpr t = this.leadingBaseCoefficient();
      IExpr s = S.leadingBaseCoefficient();
      if (t.isInteger() && s.isInteger()) {
        IExpr[] gg = t.egcd(s);
        ExprPolynomial z = this.ring.getZero();
        ret[0] = z.sum(gg[0]);
        ret[1] = z.sum(gg[1]);
        ret[2] = z.sum(gg[2]);
        return ret;
      }
    }
    ExprPolynomial[] qr;
    ExprPolynomial q = this;
    ExprPolynomial r = S;
    ExprPolynomial c1 = ring.getOne().copy();
    ExprPolynomial d1 = ring.getZero().copy();
    ExprPolynomial c2 = ring.getZero().copy();
    ExprPolynomial d2 = ring.getOne().copy();
    ExprPolynomial x1;
    ExprPolynomial x2;
    while (!r.isZERO()) {
      qr = q.quotientRemainder(r);
      if (qr == null) {
        return null;
      }
      q = qr[0];
      x1 = c1.subtract(q.multiply(d1));
      x2 = c2.subtract(q.multiply(d2));
      c1 = d1;
      c2 = d2;
      d1 = x1;
      d2 = x2;
      q = r;
      r = qr[1];
    }
    // normalize ldcf(q) to 1, i.e. make monic
    IExpr g = q.leadingBaseCoefficient();
    if (g.isUnit()) {
      IExpr h = g.inverse();
      q = q.multiply(h);
      c1 = c1.multiply(h);
      c2 = c2.multiply(h);
    }
    // assert ( ((c1.multiply(this)).sum( c2.multiply(S)).equals(q) ));
    ret[0] = q;
    ret[1] = c1;
    ret[2] = c2;
    return ret;
  }

  /**
   * GenPolynomial half extended greatest comon divisor. Only for univariate polynomials over
   * fields.
   *
   * @param S GenPolynomial.
   * @return [ gcd(this,S), a ] with a*this + b*S = gcd(this,S).
   */
  public ExprPolynomial[] hegcd(ExprPolynomial S) {
    ExprPolynomial[] ret = new ExprPolynomial[2];
    ret[0] = null;
    ret[1] = null;
    if (S == null || S.isZERO()) {
      ret[0] = this;
      ret[1] = this.ring.getOne();
      return ret;
    }
    if (this.isZERO()) {
      ret[0] = S;
      return ret;
    }
    if (ring.nvar != 1) {
      throw new IllegalArgumentException(
          this.getClass().getName() + " not univariate polynomials" + ring);
    }
    ExprPolynomial[] qr;
    ExprPolynomial q = this;
    ExprPolynomial r = S;
    ExprPolynomial c1 = ring.getOne().copy();
    ExprPolynomial d1 = ring.getZero().copy();
    ExprPolynomial x1;
    while (!r.isZERO()) {
      qr = q.quotientRemainder(r);
      q = qr[0];
      x1 = c1.subtract(q.multiply(d1));
      c1 = d1;
      d1 = x1;
      q = r;
      r = qr[1];
    }
    // normalize ldcf(q) to 1, i.e. make monic
    IExpr g = q.leadingBaseCoefficient();
    if (g.isUnit()) {
      IExpr h = g.inverse();
      q = q.multiply(h);
      c1 = c1.multiply(h);
    }
    // assert ( ((c1.multiply(this)).remainder(S).equals(q) ));
    ret[0] = q;
    ret[1] = c1;
    return ret;
  }

  /** GenPolynomial inverse. Required by RingElem. Throws not invertible exception. */
  @Override
  public ExprPolynomial inverse() {
    if (isUnit()) { // only possible if ldbcf is unit
      IExpr c = leadingBaseCoefficient().inverse();
      return ring.getOne().multiply(c);
    }
    throw new NotInvertibleException("element not invertible " + this + " :: " + ring);
  }

  /**
   * GenPolynomial modular inverse. Only for univariate polynomials over fields.
   *
   * @param m GenPolynomial.
   * @return a with with a*this = 1 mod m.
   */
  public ExprPolynomial modInverse(ExprPolynomial m) {
    if (this.isZERO()) {
      throw new NotInvertibleException("zero is not invertible");
    }
    ExprPolynomial[] hegcd = this.hegcd(m);
    ExprPolynomial a = hegcd[0];
    if (!a.isUnit()) { // gcd != 1
      throw new AlgebraicNotInvertibleException("element not invertible, gcd != 1", m, a,
          m.divide(a));
    }
    ExprPolynomial b = hegcd[1];
    if (b.isZERO()) { // when m divides this, e.g. m.isUnit()
      throw new NotInvertibleException("element not invertible, divisible by modul");
    }
    return b;
  }

  /**
   * Extend variables. Used e.g. in module embedding. Extend all ExpVectorLongs by i elements and
   * multiply by x_j^k.
   *
   * @param pfac extended polynomial ring factory (by i variables).
   * @param j index of variable to be used for multiplication.
   * @param k exponent for x_j.
   * @return extended polynomial.
   */
  public ExprPolynomial extend(ExprPolynomialRing pfac, int j, long k) {
    if (ring.equals(pfac)) { // nothing to do
      return this;
    }
    ExprPolynomial Cp = pfac.getZero().copy();
    if (this.isZERO()) {
      return Cp;
    }
    int i = pfac.nvar - ring.nvar;
    Map<ExpVectorLong, IExpr> C = Cp.val; // getMap();
    Map<ExpVectorLong, IExpr> A = val;
    for (Map.Entry<ExpVectorLong, IExpr> y : A.entrySet()) {
      ExpVectorLong e = y.getKey();
      IExpr a = y.getValue();
      ExpVectorLong f = e.extend(i, j, k);
      C.put(f, a);
    }
    return Cp;
  }

  /**
   * Extend lower variables. Used e.g. in module embedding. Extend all ExpVectorLongs by i lower
   * elements and multiply by x_j^k.
   *
   * @param pfac extended polynomial ring factory (by i variables).
   * @param j index of variable to be used for multiplication.
   * @param k exponent for x_j.
   * @return extended polynomial.
   */
  public ExprPolynomial extendLower(ExprPolynomialRing pfac, int j, long k) {
    if (ring.equals(pfac)) { // nothing to do
      return this;
    }
    ExprPolynomial Cp = pfac.getZero().copy();
    if (this.isZERO()) {
      return Cp;
    }
    int i = pfac.nvar - ring.nvar;
    Map<ExpVectorLong, IExpr> C = Cp.val; // getMap();
    Map<ExpVectorLong, IExpr> A = val;
    for (Map.Entry<ExpVectorLong, IExpr> y : A.entrySet()) {
      ExpVectorLong e = y.getKey();
      IExpr a = y.getValue();
      ExpVectorLong f = e.extendLower(i, j, k);
      C.put(f, a);
    }
    return Cp;
  }

  /**
   * Contract variables. Used e.g. in module embedding. Remove i elements of each ExpVectorLong.
   *
   * @param pfac contracted polynomial ring factory (by i variables).
   * @return Map of exponents and contracted polynomials. <b>Note:</b> could return SortedMap
   */
  public Map<ExpVectorLong, ExprPolynomial> contract(ExprPolynomialRing pfac) {
    ExprPolynomial zero = pfac.getZero(); // not pfac.coFac;
    ExprTermOrder t = ExprTermOrderByName.INVLEX; // new
    // ExprTermOrder(ExprTermOrder.INVLEX);
    Map<ExpVectorLong, ExprPolynomial> B =
        new TreeMap<ExpVectorLong, ExprPolynomial>(t.getAscendComparator());
    if (this.isZERO()) {
      return B;
    }
    int i = ring.nvar - pfac.nvar;
    Map<ExpVectorLong, IExpr> A = val;
    for (Map.Entry<ExpVectorLong, IExpr> y : A.entrySet()) {
      ExpVectorLong e = y.getKey();
      IExpr a = y.getValue();
      ExpVectorLong f = e.contract(0, i);
      ExpVectorLong g = e.contract(i, e.length() - i);
      ExprPolynomial p = B.get(f);
      if (p == null) {
        p = zero;
      }
      p = p.sum(a, g);
      B.put(f, p);
    }
    return B;
  }

  /**
   * Contract variables to coefficient polynomial. Remove i elements of each ExpVectorLong, removed
   * elements must be zero.
   *
   * @param pfac contracted polynomial ring factory (by i variables).
   * @return contracted coefficient polynomial.
   */
  public ExprPolynomial contractCoeff(ExprPolynomialRing pfac) {
    Map<ExpVectorLong, ExprPolynomial> ms = contract(pfac);
    ExprPolynomial c = pfac.getZero();
    for (Map.Entry<ExpVectorLong, ExprPolynomial> m : ms.entrySet()) {
      if (m.getKey().isZERO()) {
        c = m.getValue();
      } else {
        throw new RuntimeException("wrong coefficient contraction " + m + ", pol =  " + c);
      }
    }
    return c;
  }

  /**
   * Extend univariate to multivariate polynomial. This is an univariate polynomial in variable i of
   * the polynomial ring, it is extended to the given polynomial ring.
   *
   * @param pfac extended polynomial ring factory.
   * @param i index of the variable of this polynomial in pfac.
   * @return extended multivariate polynomial.
   */
  public ExprPolynomial extendUnivariate(ExprPolynomialRing pfac, int i) {
    if (i < 0 || pfac.nvar < i) {
      throw new IllegalArgumentException("index " + i + "out of range " + pfac.nvar);
    }
    if (ring.nvar != 1) {
      throw new IllegalArgumentException("polynomial not univariate " + ring.nvar);
    }
    if (this.isOne()) {
      return pfac.getOne();
    }
    int j = pfac.nvar - 1 - i;
    ExprPolynomial Cp = pfac.getZero().copy();
    if (this.isZERO()) {
      return Cp;
    }
    Map<ExpVectorLong, IExpr> C = Cp.val; // getMap();
    Map<ExpVectorLong, IExpr> A = val;
    for (Map.Entry<ExpVectorLong, IExpr> y : A.entrySet()) {
      ExpVectorLong e = y.getKey();
      long n = e.getVal(0);
      IExpr a = y.getValue();
      ExpVectorLong f = new ExpVectorLong(pfac.nvar, j, n);
      C.put(f, a); // assert not contained
    }
    return Cp;
  }

  /**
   * Make homogeneous.
   *
   * @param pfac extended polynomial ring factory (by 1 variable).
   * @return homogeneous polynomial.
   */
  public ExprPolynomial homogenize(ExprPolynomialRing pfac) {
    if (ring.equals(pfac)) { // not implemented
      throw new UnsupportedOperationException("case with same ring not implemented");
    }
    ExprPolynomial Cp = pfac.getZero().copy();
    if (this.isZERO()) {
      return Cp;
    }
    long deg = totalDegree();
    // int i = pfac.nvar - ring.nvar;
    Map<ExpVectorLong, IExpr> C = Cp.val; // getMap();
    Map<ExpVectorLong, IExpr> A = val;
    for (Map.Entry<ExpVectorLong, IExpr> y : A.entrySet()) {
      ExpVectorLong e = y.getKey();
      IExpr a = y.getValue();
      long d = deg - e.totalDeg();
      ExpVectorLong f = e.extend(1, 0, d);
      C.put(f, a);
    }
    return Cp;
  }

  /**
   * Dehomogenize.
   *
   * @param pfac contracted polynomial ring factory (by 1 variable).
   * @return in homogeneous polynomial.
   */
  public ExprPolynomial deHomogenize(ExprPolynomialRing pfac) {
    if (ring.equals(pfac)) { // not implemented
      throw new UnsupportedOperationException("case with same ring not implemented");
    }
    ExprPolynomial Cp = pfac.getZero().copy();
    if (this.isZERO()) {
      return Cp;
    }
    Map<ExpVectorLong, IExpr> C = Cp.val; // getMap();
    Map<ExpVectorLong, IExpr> A = val;
    for (Map.Entry<ExpVectorLong, IExpr> y : A.entrySet()) {
      ExpVectorLong e = y.getKey();
      IExpr a = y.getValue();
      ExpVectorLong f = e.contract(1, pfac.nvar);
      C.put(f, a);
    }
    return Cp;
  }

  /**
   * Reverse variables. Used e.g. in opposite rings.
   *
   * @return polynomial with reversed variables.
   */
  public ExprPolynomial reverse(ExprPolynomialRing oring) {
    ExprPolynomial Cp = oring.getZero().copy();
    if (this.isZERO()) {
      return Cp;
    }
    int k = -1;
    if (oring.tord.getEvord2() != 0 && oring.partial) {
      k = oring.tord.getSplit();
    }

    Map<ExpVectorLong, IExpr> C = Cp.val; // getMap();
    Map<ExpVectorLong, IExpr> A = val;
    ExpVectorLong f;
    for (Map.Entry<ExpVectorLong, IExpr> y : A.entrySet()) {
      ExpVectorLong e = y.getKey();
      if (k >= 0) {
        f = e.reverse(k);
      } else {
        f = e.reverse();
      }
      IExpr a = y.getValue();
      C.put(f, a);
    }
    return Cp;
  }

  /**
   * Iterator over coefficients.
   *
   * @return val.values().iterator().
   */
  public Iterator<IExpr> coefficientIterator() {
    return val.values().iterator();
  }

  /**
   * Iterator over exponents.
   *
   * @return val.keySet().iterator().
   */
  public Iterator<ExpVectorLong> exponentIterator() {
    return val.keySet().iterator();
  }

  /**
   * Iterator over monomials.
   *
   * @return a PolyIterator.
   */
  @Override
  public Iterator<ExprMonomial> iterator() {
    return new ExprPolyIterator(val);
  }

  /**
   * Map a unary function to the coefficients.
   *
   * @param f evaluation functor.
   * @return new polynomial with coefficients f(this(e)).
   */
  public ExprPolynomial map(final Function<IExpr, IExpr> f) {
    ExprPolynomial n = ring.getZero().copy();
    SortedMap<ExpVectorLong, IExpr> nv = n.val;
    for (ExprMonomial m : this) {
      // logger.info("m = " + m);
      IExpr c = f.apply(m.c);
      if (c != null && !c.isZERO()) {
        nv.put(m.e, c);
      }
    }
    return n;
  }

  /**
   * Get the coefficient rules of a polynomial <code>List()</code> form
   *
   * @return the monomials of a polynomial
   */
  public IAST coefficientRules() {
    IASTAppendable result = F.ListAlloc(val.size());
    for (Map.Entry<ExpVectorLong, IExpr> monomial : val.entrySet()) {
      IExpr coeff = monomial.getValue();
      ExpVectorLong exp = monomial.getKey();
      int len = exp.length();
      IASTAppendable ruleList = F.ListAlloc(len);
      for (int i = 0; i < len; i++) {
        ruleList.append(exp.getVal(len - i - 1));
      }
      result.append(F.Rule(ruleList, coeff));
    }
    return result;
  }

  /**
   * TODO currently not implemented
   *
   * @deprecated
   */
  @Deprecated
  public IAST coefficientArrays(int degree) {
    int numberOfVariables = ring.nvar;
    IASTAppendable result = F.ListAlloc(degree + 1);
    result.append(F.C0);
    for (int i = 0; i < degree; i++) {
      int[] dimension = new int[i + 1];
      for (int j = 0; j < dimension.length; j++) {
        dimension[j] = numberOfVariables;
      }
      SparseArrayExpr sparse = SparseArrayExpr.newArrayRules(F.List(), dimension, i + 1, F.C0);
      result.append(sparse);
    }

    for (Map.Entry<ExpVectorLong, IExpr> monomial : val.entrySet()) {
      IExpr coeff = monomial.getValue();

      ExpVectorLong exp = monomial.getKey();
      int maxDegree = (int) exp.maxDeg();
      if (maxDegree == 0) {
        result.set(1, coeff);
      } else {
        for (int i = exp.length() - 1; i >= 0; i--) {
          if (exp.getVal(i) != 0L) {
            SparseArrayExpr sparse = (SparseArrayExpr) result.get(maxDegree + 1);
            int[] positions = new int[maxDegree];
            positions[0] = exp.length() - i;

            Trie<int[], IExpr> data = sparse.toData();
            data.put(positions, coeff);
            break;
          }
        }
      }
      // int len = exp.length();
      // IASTAppendable ruleList = F.ListAlloc(len);
      // for (int i = 0; i < len; i++) {
      // ruleList.append(exp.getVal(len - i - 1));
      // }
      // result.append(F.Rule(ruleList, coeff));
    }
    return result;
  }

  /**
   * Get the monomials of a polynomial in <code>List()</code> form
   *
   * @return the monomials of a polynomial
   */
  public IAST monomialList() {
    IASTAppendable result = F.ListAlloc(val.size());
    for (Map.Entry<ExpVectorLong, IExpr> monomial : val.entrySet()) {
      // IExpr coeff = monomial.getValue();
      ExpVectorLong exp = monomial.getKey();
      IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
      monomTimes.append(val.get(exp));
      appendToExpr(monomTimes, exp, ring.vars);
      result.append(monomTimes);
    }
    return result;
  }

  private void appendToExpr(IASTAppendable times, ExpVectorLong expArray, IAST variables) {
    long[] arr = expArray.getVal();
    ExpVectorLong leer = ring.evzero;
    for (int i = 0; i < arr.length; i++) {
      if (arr[i] != 0L) {
        int ix = leer.varIndex(i);
        if (ix >= 0) {
          if (arr[i] == 1L) {
            times.append(variables.get(ix + 1));
          } else {
            times.append(F.Power(variables.get(ix + 1), arr[i]));
          }
        }
      }
    }
  }

  /**
   * Get the coefficients of a univariate polynomial up to n degree. Throws WrongNumberOfArguments
   * if the polynomial is not univariate.
   *
   * @return the coefficients of a univariate polynomial up to n degree
   */
  public IAST coefficientList() {
    final int argsSize = ring.getVars().size() - 1;
    if (argsSize == 1) {
      long exp;
      if (ring.tord.getEvord() == ExprTermOrder.IGRLEX
          || ring.tord.getEvord() == ExprTermOrder.REVILEX) {
        long lastDegree = degree();
        IExpr[] exprs = new IExpr[(int) lastDegree + 1];
        for (int i = 0; i < exprs.length; i++) {
          exprs[i] = F.C0;
        }
        for (Map.Entry<ExpVectorLong, IExpr> entry : val.entrySet()) {
          exp = entry.getKey().getVal(0);
          exprs[(int) exp] = entry.getValue();
        }
        return F.function(S.List, exprs);
      } else {
        long lastDegree = 0L;
        IASTAppendable result = F.ListAlloc(val.size());
        for (Map.Entry<ExpVectorLong, IExpr> entry : val.entrySet()) {
          exp = entry.getKey().getVal(0);
          while (lastDegree < exp) {
            result.append(F.C0);
            lastDegree++;
          }
          if (lastDegree == exp) {
            result.append(entry.getValue());
            lastDegree++;
          }
        }
        return result;
      }
    } else if (argsSize > 1) {
      long exp;
      int[] arr = new int[argsSize];
      for (int j = 0; j < argsSize; j++) {
        arr[j] = (int) degree(j) + 1;
      }
      IASTMutable constantArray = F.C0.constantArray(S.List, 0, arr);
      for (ExpVectorLong expArray : val.keySet()) {
        int[] positions = new int[argsSize];
        for (int i = 0; i < expArray.length(); i++) {
          exp = expArray.getVal(i);
          positions[expArray.varIndex(i)] = (int) exp + 1;
        }
        constantArray.setPart(val.get(expArray), positions);
      }
      return constantArray;
    }
    return F.NIL;
  }

  /**
   * Derivative of a polynomial. This method assumes that the polynomial is univariate. Throws
   * WrongNumberOfArguments if the polynomial is not univariate.
   *
   * @return the derivative polynomial if the polynomial is univariate
   */
  public ExprPolynomial derivativeUnivariate() {
    ExprPolynomial result = new ExprPolynomial(ring);
    long exp;
    IExpr coefficient;
    ExpVectorLong copy;
    for (ExpVectorLong expArray : val.keySet()) {
      exp = expArray.getVal(0);
      if (exp != 0) {
        copy = expArray.copy();
        copy.val[0] = exp - 1;
        coefficient = val.get(expArray).times(F.ZZ(exp));
        result.doAddTo(coefficient, copy);
      }
    }
    return result;
  }

  /**
   * Converts a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial to a MathEclipse
   * AST with head <code>Plus</code>
   *
   * @return
   */
  public IExpr getExpr() {
    if (length() == 0) {
      return F.C0;
    }

    IASTAppendable result = F.PlusAlloc(length());
    IAST vars = ring.vars;
    for (ExprMonomial monomial : this) {
      IExpr coeff = monomial.coefficient();
      ExpVectorLong exp = monomial.exponent();
      IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
      if (!coeff.isOne()) {
        monomTimes.append(coeff);
      }
      long lExp;
      int ix;
      IExpr variable;
      for (int i = 0; i < exp.length(); i++) {
        lExp = exp.getVal(i);
        if (lExp != 0L) {
          ix = exp.varIndex(i);
          variable = vars.get(ix + 1);
          if (lExp == 1L) {
            monomTimes.append(variable);
          } else {
            monomTimes.append(F.Power(variable, F.ZZ(lExp)));
          }
        }
      }
      result.append(monomTimes.oneIdentity1());
    }
    return result.oneIdentity0();
  }
}
