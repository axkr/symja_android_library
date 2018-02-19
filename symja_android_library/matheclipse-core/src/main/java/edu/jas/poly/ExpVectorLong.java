/*
 * $Id$
 */

package edu.jas.poly;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * ExpVectorLong implements exponent vectors for polynomials using arrays of
 * long as storage unit. This class is used by ExpVector internally, there is no
 * need to use this class directly.
 *
 * @author Heinz Kredel
 * @see ExpVector
 */

public final class ExpVectorLong extends ExpVector
/*implements AbelianGroupElem<ExpVectorLong>*/ {


    /**
     * The data structure is an array of longs.
     */
    /*package*/final long[] val;


    /**
     * Constructor for ExpVector.
     *
     * @param n length of exponent vector.
     */
    public ExpVectorLong(int n) {
        this(new long[n], true);
    }


    /**
     * Constructor for ExpVector. Sets exponent i to e.
     *
     * @param n length of exponent vector.
     * @param i index of exponent to be set.
     * @param e exponent to be set.
     */
    public ExpVectorLong(int n, int i, long e) {
        this(new long[n], true);
        val[i] = e;
    }


    /**
     * Constructor for ExpVector. Sets val.
     *
     * @param v representation array.
     */
    public ExpVectorLong(long[] v) {
        this(v, false);
    }


    /**
     * Internal constructor for ExpVector. Sets val.
     *
     * @param v     internal representation array.
     * @param alloc true if internal representation array is newly
     *              allocated, else false.
     */
    protected ExpVectorLong(long[] v, boolean alloc) {
        super();
        if (v == null) {
            throw new IllegalArgumentException("null val not allowed");
        }
        if (alloc) {
            val = v;
        } else {
            val = Arrays.copyOf(v, v.length); // > Java-5
        }
    }


    /**
     * Constructor for ExpVector. Converts a String representation to an
     * ExpVector. Accepted format = (1,2,3,4,5,6,7).
     *
     * @param s String representation.
     */
    public ExpVectorLong(String s) throws NumberFormatException {
        this(parse(s).val, true);
    }


    /**
     * parser for ExpVector. Converts a String representation to an
     * ExpVector. Accepted format = (1,2,3,4,5,6,7).
     *
     * @param s String representation.
     * @return paresed ExpVector
     */
    public static ExpVectorLong parse(String s) throws NumberFormatException {
        long[] v = null;
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
                exps.add(Long.valueOf(a));
                b = k + 1;
            }
            if (b <= e) {
                teil = s.substring(b, e);
                a = Long.parseLong(teil);
                exps.add(Long.valueOf(a));
            }
            int length = exps.size();
            v = new long[length];
            for (int j = 0; j < length; j++) {
                v[j] = exps.get(j).longValue();
            }
        }
        return new ExpVectorLong(v, true);
    }


    /**
     * Clone this.
     *
     * @see Object#clone()
     */
    @Override
    public ExpVectorLong copy() {
        long[] w = new long[val.length];
        System.arraycopy(val, 0, w, 0, val.length);
        return new ExpVectorLong(w, true);
    }


    /**
     * Get the exponent vector.
     *
     * @return val.
     */
    @Override
    public long[] getVal() {
        long[] w = new long[val.length];
        System.arraycopy(val, 0, w, 0, val.length);
        return w;
        //return val;
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
        long x = val[i];
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
    public ExpVectorLong extend(int i, int j, long e) {
        long[] w = new long[val.length + i];
        System.arraycopy(val, 0, w, i, val.length);
        if (j >= i) {
            throw new IllegalArgumentException("i " + i + " <= j " + j + " invalid");
        }
        w[j] = e;
        return new ExpVectorLong(w, true);
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
    public ExpVectorLong extendLower(int i, int j, long e) {
        long[] w = new long[val.length + i];
        System.arraycopy(val, 0, w, 0, val.length);
        if (j >= i) {
            throw new IllegalArgumentException("i " + i + " <= j " + j + " invalid");
        }
        w[val.length + j] = e;
        return new ExpVectorLong(w, true);
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
    public ExpVectorLong contract(int i, int len) {
        if (i + len > val.length) {
            throw new IllegalArgumentException("len " + len + " > val.len " + val.length);
        }
        long[] w = new long[len];
        System.arraycopy(val, i, w, 0, len);
        return new ExpVectorLong(w, true);
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     *
     * @return reversed exponent vector.
     */
    @Override
    public ExpVectorLong reverse() {
        long[] w = new long[val.length];
        for (int i = 0; i < val.length; i++) {
            w[i] = val[val.length - 1 - i];
        }
        return new ExpVectorLong(w, true);
    }


    /**
     * Reverse lower j variables. Used e.g. in opposite rings. Reverses the
     * first j-1 variables, the rest is unchanged.
     *
     * @param j index of first variable reversed.
     * @return reversed exponent vector.
     */
    @Override
    public ExpVectorLong reverse(int j) {
        if (j <= 0 || j > val.length) {
            return this;
        }
        long[] w = new long[val.length];
        // copy first
        for (int i = 0; i < j; i++) {
            w[i] = val[i];
        }
        // reverse rest
        for (int i = j; i < val.length; i++) {
            w[i] = val[val.length + j - 1 - i];
        }
        //System.out.println("val = " + Arrays.toString(val));
        //System.out.println("w   = " + Arrays.toString(w));
        return new ExpVectorLong(w, true);
    }


    /**
     * Reverse upper j variables. Reverses the last j-1 variables, the rest is
     * unchanged.
     *
     * @param j index of first variable not reversed.
     * @return reversed exponent vector.
     */
    public ExpVectorLong reverseUpper(int j) {
        if (j <= 0 || j > val.length) {
            return this;
        }
        long[] w = new long[val.length];
        for (int i = 0; i < j; i++) {
            w[i] = val[j - 1 - i];
        }
        // copy rest
        for (int i = j; i < val.length; i++) {
            w[i] = val[i];
        }
        return new ExpVectorLong(w, true);
    }


    /**
     * Combine with ExpVector. Combine this with the other ExpVector V.
     *
     * @param V the other exponent vector.
     * @return combined exponent vector.
     */
    @Override
    public ExpVectorLong combine(ExpVector V) {
        if (V == null || V.length() == 0) {
            return this;
        }
        ExpVectorLong Vl = (ExpVectorLong) V;
        if (val.length == 0) {
            return Vl;
        }
        long[] w = new long[val.length + Vl.val.length];
        System.arraycopy(val, 0, w, 0, val.length);
        System.arraycopy(Vl.val, 0, w, val.length, Vl.val.length);
        return new ExpVectorLong(w, true);
    }


    /**
     * Permutation of exponent vector.
     *
     * @param P permutation.
     * @return P(e).
     */
    @Override
    public ExpVectorLong permutation(List<Integer> P) {
        long[] w = new long[val.length];
        int j = 0;
        for (Integer i : P) {
            w[j++] = val[i];
        }
        return new ExpVectorLong(w, true);
    }


    /**
     * Get the string representation.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + ":long";
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof ExpVectorLong)) {
            return false;
        }
        ExpVectorLong b = (ExpVectorLong) B;
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
    public ExpVectorLong abs() {
        long[] u = val;
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++) {
            if (u[i] >= 0L) {
                w[i] = u[i];
            } else {
                w[i] = -u[i];
            }
        }
        return new ExpVectorLong(w, true);
    }


    /**
     * ExpVector negate.
     *
     * @return -this.
     */
    @Override
    public ExpVectorLong negate() {
        long[] u = val;
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = -u[i];
        }
        return new ExpVectorLong(w, true);
    }


    /**
     * ExpVector summation.
     *
     * @param V
     * @return this+V.
     */
    @Override
    public ExpVectorLong sum(ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = u[i] + v[i];
        }
        return new ExpVectorLong(w, true);
    }


    /**
     * ExpVector subtract. Result may have negative entries.
     *
     * @param V
     * @return this-V.
     */
    @Override
    public ExpVectorLong subtract(ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = u[i] - v[i];
        }
        return new ExpVectorLong(w, true);
    }


    /**
     * ExpVector multiply by scalar.
     *
     * @param s scalar
     * @return s*this.
     */
    @Override
    public ExpVectorLong scalarMultiply(long s) {
        long[] u = val;
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = s * u[i];
        }
        return new ExpVectorLong(w, true);
    }


    /**
     * ExpVector substitution. Clone and set exponent to d at position i.
     *
     * @param i position.
     * @param d new exponent.
     * @return substituted ExpVector.
     */
    @Override
    public ExpVectorLong subst(int i, long d) {
        ExpVectorLong V = this.copy();
        //long e = 
        V.setVal(i, d);
        return V;
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
    }


    /**
     * ExpVector total degree.
     *
     * @return sum of all exponents.
     */
    @Override
    public long totalDeg() {
        long t = 0;
        long[] u = val; // U.val;
        for (int i = 0; i < u.length; i++) {
            t += u[i];
        }
        return t;
    }


    /**
     * ExpVector maximal degree.
     *
     * @return maximal exponent.
     */
    @Override
    public long maxDeg() {
        long t = 0;
        long[] u = val;
        for (int i = 0; i < u.length; i++) {
            if (u[i] > t) {
                t = u[i];
            }
        }
        return t;
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
        long[] u = val;
        for (int j = 0; j < w.length; j++) {
            long[] wj = w[j];
            for (int i = 0; i < u.length; i++) {
                t += wj[i] * u[i];
            }
        }
        return t;
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
        long[] u = val;
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
    public ExpVectorLong lcm(ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = (u[i] >= v[i] ? u[i] : v[i]);
        }
        return new ExpVectorLong(w, true);
    }


    /**
     * ExpVector greatest common divisor.
     *
     * @param V
     * @return component wise minimum of this and V.
     */
    @Override
    public ExpVectorLong gcd(ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
        long[] w = new long[u.length];
        for (int i = 0; i < u.length; i++) {
            w[i] = (u[i] <= v[i] ? u[i] : v[i]);
        }
        return new ExpVectorLong(w, true);
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
        long[] u = val;
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
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
        for (int i = 0; i < u.length; i++) {
            if (u[i] < v[i]) {
                return false;
            }
        }
        return true;
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
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
        int t = 0;
        for (int i = 0; i < u.length; i++) {
            if (u[i] > v[i])
                return 1;
            if (u[i] < v[i])
                return -1;
        }
        return t;
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
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
    }


    /**
     * ExpVector inverse graded lexicographical compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invGradCompareTo(ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
    }


    /**
     * ExpVector reverse inverse lexicographical compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int revInvLexCompareTo(ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
        int t = 0;
        for (int i = u.length - 1; i >= 0; i--) {
            if (u[i] > v[i])
                return 1;
            if (u[i] < v[i])
                return -1;
        }
        return t;
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
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
    }


    /**
     * ExpVector reverse inverse graded compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int revInvGradCompareTo(ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
    }


    /**
     * ExpVector inverse total degree lexicographical compareTo.
     *
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invTdegCompareTo(ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
    @Override
    public int revLexInvTdegCompareTo(ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
     * ExpVector inverse weighted lexicographical compareTo.
     *
     * @param w weight array.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int invWeightCompareTo(long[][] w, ExpVector V) {
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
        long[] u = val;
        long[] v = ((ExpVectorLong) V).val;
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
    }

}
