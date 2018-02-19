/*
 * $Id$
 */

package edu.jas.poly;


import java.util.ArrayList;
import java.util.List;


/**
 * ExpVectorShort implements exponent vectors for polynomials using arrays of
 * short as storage unit. This class is used by ExpVector internally, there is
 * no need to use this class directly.
 *
 * @author Heinz Kredel
 * @see ExpVector
 */

public final class ExpVectorShort extends ExpVector
/*implements AbelianGroupElem<ExpVectorShort>*/ {


    /**
     * Largest short.
     */
    public static final long maxShort = (long) Short.MAX_VALUE / 2;
    /**
     * Smallest short.
     */
    public static final long minShort = (long) Short.MIN_VALUE / 2;
    /**
     * The data structure is an array of longs.
     */
    /*package*/final short[] val;


    /**
     * Constructor for ExpVector.
     *
     * @param n length of exponent vector.
     */
    public ExpVectorShort(int n) {
        this(new short[n]);
    }


    /**
     * Constructor for ExpVector. Sets exponent i to e.
     *
     * @param n length of exponent vector.
     * @param i index of exponent to be set.
     * @param e exponent to be set.
     */
    public ExpVectorShort(int n, int i, short e) {
        this(n);
        val[i] = e;
    }


    /**
     * Constructor for ExpVector. Sets exponent i to e.
     *
     * @param n length of exponent vector.
     * @param i index of exponent to be set.
     * @param e exponent to be set.
     */
    public ExpVectorShort(int n, int i, long e) {
        this(n);
        if (e >= maxShort || e <= minShort) {
            throw new IllegalArgumentException("exponent to large: " + e);
        }
        val[i] = (short) e;
    }


    /**
     * Internal constructor for ExpVector. Sets val.
     *
     * @param v internal representation array.
     */
    protected ExpVectorShort(short[] v) {
        super();
        val = v;
    }


    /**
     * Constructor for ExpVector. Sets val, converts from long array.
     *
     * @param v long representation array.
     */
    public ExpVectorShort(long[] v) {
        this(v.length);
        for (int i = 0; i < v.length; i++) {
            if (v[i] >= maxShort || v[i] <= minShort) {
                throw new IllegalArgumentException("exponent to large: " + v[i]);
            }
            val[i] = (short) v[i];
        }
    }


    /**
     * Constructor for ExpVector. Converts a String representation to an
     * ExpVector. Accepted format = (1,2,3,4,5,6,7).
     *
     * @param s String representation.
     */
    public ExpVectorShort(String s) throws NumberFormatException {
        super();
        // first format = (1,2,3,4,5,6,7)
        List<Short> exps = new ArrayList<Short>();
        s = s.trim();
        int b = s.indexOf('(');
        int e = s.indexOf(')', b + 1);
        String teil;
        int k;
        short a;
        if (b >= 0 && e >= 0) {
            b++;
            while ((k = s.indexOf(',', b)) >= 0) {
                teil = s.substring(b, k);
                a = Short.parseShort(teil);
                exps.add(Short.valueOf(a));
                b = k + 1;
            }
            if (b <= e) {
                teil = s.substring(b, e);
                a = Short.parseShort(teil);
                exps.add(Short.valueOf(a));
            }
            int length = exps.size();
            val = new short[length];
            for (int j = 0; j < length; j++) {
                val[j] = exps.get(j).shortValue();
            }
        } else {
            // not implemented
            val = null;
            // length = -1;
            //Vector names = new Vector();
            //vars = s;
        }
    }


    /**
     * Clone this.
     *
     * @see Object#clone()
     */
    @Override
    public ExpVectorShort copy() {
        short[] w = new short[val.length];
        System.arraycopy(val, 0, w, 0, val.length);
        return new ExpVectorShort(w);
    }


    /**
     * Get the exponent vector.
     *
     * @return val as long.
     */
    @Override
    public long[] getVal() {
        long v[] = new long[val.length];
        for (int i = 0; i < val.length; i++) {
            v[i] = val[i];
        }
        return v;
    }


    /**
     * Get the exponent at position i.
     *
     * @param i position.
     * @return val[i].
     */
    @Override
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
    @Override
    protected long setVal(int i, long e) {
        short x = val[i];
        if (e >= maxShort || e <= minShort) {
            throw new IllegalArgumentException("exponent to large: " + e);
        }
        val[i] = (short) e;
        hash = 0; // beware of race condition
        return x;
    }


    /**
     * Set the exponent at position i to e.
     *
     * @param i
     * @param e
     * @return old val[i].
     */
    protected short setVal(int i, short e) {
        short x = val[i];
        val[i] = e;
        hash = 0; // beware of race condition
        return x;
    }


    /**
     * Get the length of this exponent vector.
     *
     * @return val.length.
     */
    @Override
    public int length() {
        return val.length;
    }


    /**
     * Extend variables. Used e.g. in module embedding. Extend this by i
     * elements and set val[j] to e.
     *
     * @param i number of elements to extend.
     * @param j index of element to be set.
     * @param e new exponent for val[j].
     * @return extended exponent vector.
     */
    @Override
    public ExpVectorShort extend(int i, int j, long e) {
        short[] w = new short[val.length + i];
        System.arraycopy(val, 0, w, i, val.length);
        if (j >= i) {
            throw new IllegalArgumentException("i " + i + " <= j " + j + " invalid");
        }
        if (e >= maxShort || e <= minShort) {
            throw new IllegalArgumentException("exponent to large: " + e);
        }
        w[j] = (short) e;
        return new ExpVectorShort(w);
    }


    /**
     * Extend lower variables. Extend this by i lower elements and set val[j] to
     * e.
     *
     * @param i number of elements to extend.
     * @param j index of element to be set.
     * @param e new exponent for val[j].
     * @return extended exponent vector.
     */
    @Override
    public ExpVectorShort extendLower(int i, int j, long e) {
        short[] w = new short[val.length + i];
        System.arraycopy(val, 0, w, 0, val.length);
        if (j >= i) {
            throw new IllegalArgumentException("i " + i + " <= j " + j + " invalid");
        }
        w[val.length + j] = (short) e;
        return new ExpVectorShort(w);
    }


    /**
     * Contract variables. Used e.g. in module embedding. Contract this to len
     * elements.
     *
     * @param i   position of first element to be copied.
     * @param len new length.
     * @return contracted exponent vector.
     */
    @Override
    public ExpVectorShort contract(int i, int len) {
        if (i + len > val.length) {
            throw new IllegalArgumentException("len " + len + " > val.len " + val.length);
        }
        short[] w = new short[len];
        System.arraycopy(val, i, w, 0, len);
        return new ExpVectorShort(w);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     *
     * @return reversed exponent vector.
     */
    @Override
    public ExpVectorShort reverse() {
        short[] w = new short[val.length];
        for (int i = 0; i < val.length; i++) {
            w[i] = val[val.length - 1 - i];
        }
        return new ExpVectorShort(w);
    }


    /**
     * Reverse lower j variables. Used e.g. in opposite rings. Reverses the
     * first j-1 variables, the rest is unchanged.
     *
     * @param j index of first variable reversed.
     * @return reversed exponent vector.
     */
    @Override
    public ExpVectorShort reverse(int j) {
        if (j <= 0 || j > val.length) {
            return this;
        }
        short[] w = new short[val.length];
        for (int i = 0; i < j; i++) {
            w[i] = val[i];
        }
        // copy rest
        for (int i = j; i < val.length; i++) {
            w[i] = val[val.length + j - 1 - i];
        }
        return new ExpVectorShort(w);
    }


    /**
     * Combine with ExpVector. Combine this with the other ExpVector V.
     *
     * @param V the other exponent vector.
     * @return combined exponent vector.
     */
    @Override
    public ExpVectorShort combine(ExpVector V) {
        if (V == null || V.length() == 0) {
            return this;
        }
        ExpVectorShort Vi = (ExpVectorShort) V;
        if (val.length == 0) {
            return Vi;
        }
        short[] w = new short[val.length + Vi.val.length];
        System.arraycopy(val, 0, w, 0, val.length);
        System.arraycopy(Vi.val, 0, w, val.length, Vi.val.length);
        return new ExpVectorShort(w);
    }


    /**
     * Permutation of exponent vector.
     *
     * @param P permutation.
     * @return P(e).
     */
    @Override
    public ExpVectorShort permutation(List<Integer> P) {
        short[] w = new short[val.length];
        int j = 0;
        for (Integer i : P) {
            w[j++] = val[i];
        }
        return new ExpVectorShort(w);
    }


    /**
     * Get the string representation.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + ":short";
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof ExpVectorShort)) {
            return false;
        }
        ExpVectorShort b = (ExpVectorShort) B;
        int t = this.invLexCompareTo(b);
        //System.out.println("equals: this = " + this + " B = " + B + " t = " + t);
        return (0 == t);
    }


    /**
     * hashCode for this exponent vector.
     *
     * @see Object#hashCode() Only for findbugs.
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * ExpVector absolute value.
     *
     * @return abs(this).
     */
    @Override
    public ExpVectorShort abs() {
        short[] u = val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++) {
            if (u[i] >= 0L) {
                w[i] = u[i];
            } else {
                w[i] = (short) (-u[i]);
            }
        }
        return new ExpVectorShort(w);
        //return EVABS(this);
    }


    /**
     * ExpVector negate.
     *
     * @return -this.
     */
    @Override
    public ExpVectorShort negate() {
        short[] u = val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = (short) (-u[i]);
        }
        return new ExpVectorShort(w);
        // return EVNEG(this);
    }


    /**
     * ExpVector summation.
     *
     * @param V
     * @return this+V.
     */
    @Override
    public ExpVectorShort sum(ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = (short) (u[i] + v[i]);
        }
        return new ExpVectorShort(w);
        // return EVSUM(this, V);
    }


    /**
     * ExpVector subtract. Result may have negative entries.
     *
     * @param V
     * @return this-V.
     */
    @Override
    public ExpVectorShort subtract(ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = (short) (u[i] - v[i]);
        }
        return new ExpVectorShort(w);
        //return EVDIF(this, V);
    }


    /**
     * ExpVector multiply by scalar.
     *
     * @param s scalar
     * @return s*this.
     */
    @Override
    public ExpVectorShort scalarMultiply(long s) {
        if (s >= maxShort || s <= minShort) {
            throw new IllegalArgumentException("scalar to large: " + s);
        }
        short[] u = val;
        short[] w = new short[u.length];
        short ss = (short) s;
        for (int i = 0; i < u.length; i++) {
            w[i] = (short) (ss * u[i]);
        }
        return new ExpVectorShort(w);
    }


    /**
     * ExpVector substitution. Clone and set exponent to d at position i.
     *
     * @param i position.
     * @param d new exponent.
     * @return substituted ExpVector.
     */
    public ExpVectorShort subst(int i, short d) {
        ExpVectorShort V = this.copy();
        //long e = 
        V.setVal(i, d);
        return V;
        //return EVSU(this, i, d);
    }


    /**
     * ExpVector substitution. Clone and set exponent to d at position i.
     *
     * @param i position.
     * @param d new exponent.
     * @return substituted ExpVector.
     */
    @Override
    public ExpVectorShort subst(int i, long d) {
        ExpVectorShort V = this.copy();
        //long e = 
        V.setVal(i, d);
        return V;
        //return EVSU(this, i, d);
    }


    /**
     * ExpVector signum.
     *
     * @return 0 if this is zero, -1 if some entry is negative, 1 if no entry is
     * negative and at least one entry is positive.
     */
    @Override
    public int signum() {
        int t = 0;
        short[] u = val;
        for (int i = 0; i < u.length; i++) {
            if (u[i] < 0) {
                return -1;
            }
            if (u[i] > 0) {
                t = 1;
            }
        }
        return t;
        //return EVSIGN(this);
    }


    /**
     * ExpVector total degree.
     *
     * @return sum of all exponents.
     */
    @Override
    public long totalDeg() {
        long t = 0;
        short[] u = val; // U.val;
        for (int i = 0; i < u.length; i++) {
            t += u[i];
        }
        return t;
        //return EVTDEG(this);
    }


    /**
     * ExpVector maximal degree.
     *
     * @return maximal exponent.
     */
    @Override
    public long maxDeg() {
        long t = 0;
        short[] u = val;
        for (int i = 0; i < u.length; i++) {
            if (u[i] > t) {
                t = u[i];
            }
        }
        return t;
        //return EVMDEG(this);
    }


    /**
     * ExpVector weighted degree.
     *
     * @param w weights.
     * @return weighted sum of all exponents.
     */
    @Override
    public long weightDeg(long[][] w) {
        if (w == null || w.length == 0) {
            return totalDeg(); // assume weight 1 
        }
        long t = 0;
        short[] u = val;
        for (int j = 0; j < w.length; j++) {
            long[] wj = w[j];
            for (int i = 0; i < u.length; i++) {
                t += wj[i] * u[i];
            }
        }
        return t;
        //return EVWDEG( w, this );
    }


    /**
     * ExpVector weighted degree.
     *
     * @param w weights.
     * @return weighted sum of all exponents.
     */
    @Override
    public long weightDeg(long[] w) {
        if (w == null || w.length == 0) {
            return totalDeg(); // assume weight 1 
        }
        long t = 0;
        short[] u = val;
        for (int i = 0; i < w.length; i++) {
            t += w[i] * u[i];
        }
        return t;
    }


    /**
     * ExpVector least common multiple.
     *
     * @param V
     * @return component wise maximum of this and V.
     */
    @Override
    public ExpVectorShort lcm(ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = (u[i] >= v[i] ? u[i] : v[i]);
        }
        return new ExpVectorShort(w);
        //return EVLCM(this, V);
    }


    /**
     * ExpVector greatest common divisor.
     *
     * @param V
     * @return component wise minimum of this and V.
     */
    @Override
    public ExpVectorShort gcd(ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = (u[i] <= v[i] ? u[i] : v[i]);
        }
        return new ExpVectorShort(w);
        //return EVGCD(this, V);
    }


    /**
     * ExpVector dependent variables.
     *
     * @return number of indices where val has positive exponents.
     */
    public int dependentVariables() {
        int l = 0;
        for (int i = 0; i < val.length; i++) {
            if (val[i] > 0) {
                l++;
            }
        }
        return l;
    }


    /**
     * ExpVector dependency on variables.
     *
     * @return array of indices where val has positive exponents.
     */
    @Override
    public int[] dependencyOnVariables() {
        short[] u = val;
        int l = dependentVariables();
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
     * ExpVector multiple test. Test if this is component wise greater or equal
     * to V.
     *
     * @param V
     * @return true if this is a multiple of V, else false.
     */
    @Override
    public boolean multipleOf(ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        boolean t = true;
        for (int i = 0; i < u.length; i++) {
            if (u[i] < v[i]) {
                return false;
            }
        }
        return t;
        //return EVMT(this, V);
    }


    /**
     * ExpVector compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int compareTo(ExpVector V) {
        return this.invLexCompareTo(V);
    }


    /**
     * ExpVector inverse lexicographical compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invLexCompareTo(ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        int t = 0;
        for (int i = 0; i < u.length; i++) {
            if (u[i] > v[i])
                return 1;
            if (u[i] < v[i])
                return -1;
        }
        return t;
        //return EVILCP(this, V);
    }


    /**
     * ExpVector inverse lexicographical compareTo.
     *
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invLexCompareTo(ExpVector V, int begin, int end) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        if (begin < 0) {
            begin = 0;
            ;
        }
        if (end >= val.length) {
            end = val.length;
        }
        int t = 0;
        for (int i = begin; i < end; i++) {
            if (u[i] > v[i])
                return 1;
            if (u[i] < v[i])
                return -1;
        }
        return t;
        //return EVILCP(this, V, begin, end);
    }


    /**
     * ExpVector inverse graded lexicographical compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invGradCompareTo(ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
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
        //return EVIGLC(this, V);
    }


    /**
     * ExpVector inverse graded lexicographical compareTo.
     *
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invGradCompareTo(ExpVector V, int begin, int end) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        if (begin < 0) {
            begin = 0;
            ;
        }
        if (end >= val.length) {
            end = val.length;
        }
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
        //return EVIGLC(this, V, begin, end);
    }


    /**
     * ExpVector reverse inverse lexicographical compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int revInvLexCompareTo(ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        int t = 0;
        for (int i = u.length - 1; i >= 0; i--) {
            if (u[i] > v[i])
                return 1;
            if (u[i] < v[i])
                return -1;
        }
        return t;
        //return EVRILCP(this, V);
    }


    /**
     * ExpVector reverse inverse lexicographical compareTo.
     *
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int revInvLexCompareTo(ExpVector V, int begin, int end) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        if (begin < 0) {
            begin = 0;
            ;
        }
        if (end >= val.length) {
            end = val.length;
        }
        int t = 0;
        for (int i = end - 1; i >= begin; i--) {
            if (u[i] > v[i])
                return 1;
            if (u[i] < v[i])
                return -1;
        }
        return t;
        //return EVRILCP(this, V, begin, end);
    }


    /**
     * ExpVector reverse inverse graded compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int revInvGradCompareTo(ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
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
        //return EVRIGLC(this, V);
    }


    /**
     * ExpVector reverse inverse graded compareTo.
     *
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int revInvGradCompareTo(ExpVector V, int begin, int end) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        if (begin < 0) {
            begin = 0;
            ;
        }
        if (end >= val.length) {
            end = val.length;
        }
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
        //return EVRIGLC(this, V, begin, end);
    }


    /**
     * ExpVector inverse total degree lexicographical compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invTdegCompareTo(ExpVector V) {
        throw new UnsupportedOperationException("not implemented for short ExpVector");
    }


    /**
     * ExpVector reverse lexicographical inverse total degree compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int revLexInvTdegCompareTo(ExpVector V) {
        throw new UnsupportedOperationException("not implemented for short ExpVector");
    }


    /**
     * ExpVector inverse weighted lexicographical compareTo.
     *
     * @param w weight array.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invWeightCompareTo(long[][] w, ExpVector V) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
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
        //return EVIWLC(w, this, V);
    }


    /**
     * ExpVector inverse weighted lexicographical compareTo.
     *
     * @param w     weight array.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invWeightCompareTo(long[][] w, ExpVector V, int begin, int end) {
        short[] u = val;
        short[] v = ((ExpVectorShort) V).val;
        if (begin < 0) {
            begin = 0;
            ;
        }
        if (end >= val.length) {
            end = val.length;
        }
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
        //return EVIWLC(w, this, V, begin, end);
    }

}
