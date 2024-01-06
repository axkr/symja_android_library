/*
 * $Id$
 */

package edu.jas.poly;


import java.util.Arrays;

import edu.jas.arith.BigInteger;
import edu.jas.structure.MonoidElem;
import edu.jas.structure.MonoidFactory;


/**
 * IndexList implements index lists for exterior polynomials. Index lists are
 * implemented as arrays of Java int type. Objects of this class are intended to
 * be immutable, except for the sign. If in doubt use <code>valueOf</code> to
 * get a conformant index list.
 * @see "masnc.DIPE.mi#ILEXPR from SAC2/MAS"
 * @author Heinz Kredel
 */

public class IndexList implements MonoidElem<IndexList> {


    /**
     * Representation of index list as int arrays.
     */
    public final int[] val; // != null, when s != 0


    /**
     * Sign of index list.
     */
    public int sign; // = -1, 0, 1


    /**
     * Reference to IndexFactory.
     */
    public final IndexFactory mono;


    /**
     * Constructor for IndexList. No argument constructor defining 0 index list.
     */
    public IndexList(IndexFactory m) {
        this(m, 0, null);
    }


    /**
     * Constructor for IndexList.
     * @param v array with indices.
     */
    public IndexList(IndexFactory m, int[] v) {
        this(m, 1, v);
    }


    /**
     * Constructor for IndexList. <b>Note:</b> A copy of v is internally
     * created.
     * @param s sign of index list.
     * @param v array with indices.
     */
    public IndexList(IndexFactory m, int s, int[] v) {
        sign = s;
        mono = m;
        if (v == null) {
            if (s != 0) {
                throw new IllegalArgumentException("inconsistent: s = " + s + ", v = " + v);
            }
            val = v;
            sign = 0; // only when exception deactivated
        } else {
            val = Arrays.copyOf(v, v.length);
        }
        //if (v != null && v.length > 0) System.out.println("v[0]: " + v[0]);
    }


    /**
     * Constructor for IndexList. Converts a String representation to an
     * IndexList. Accepted format = E(1,2,3,4,5,6,7).
     * @param s String representation.
     */
    public IndexList(IndexFactory m, String s) throws NumberFormatException {
        this(m, m.parse(s).val);
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public MonoidFactory<IndexList> factory() {
        return mono; //(MonoidFactory<IndexList>) this;
        //throw new UnsupportedOperationException("no factory implemented for IndexList");
    }


    /**
     * Check for IndexList conformant specification.
     * @return true if this a a conformant IndexList, else false.
     */
    public boolean isConformant() {
        if (sign == 0 && val == null) {
            return true;
        }
        IndexList ck = mono.valueOf(val);
        return this.abs().equals(ck.abs());
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public IndexList copy() {
        return new IndexList(mono, sign, val);
    }


    /**
     * Get the index vector.
     * @return val.
     */
    public int[] getVal() {
        return val;
    }


    /**
     * Get the index at position i.
     * @param i position.
     * @return val[i].
     */
    public int getVal(int i) {
        return val[i];
    }


    /**
     * Set the index at position i to e.
     * @param i position
     * @param e new index
     * @return old val[i].
     */
    protected int setVal(int i, int e) {
        int v = val[i];
        val[i] = e;
        return v;
    }


    /**
     * Get the length of this index list.
     * @return val.length or -1 for 0 index list.
     */
    public int length() {
        if (sign == 0) {
            return -1;
        }
        return val.length;
    }


    /**
     * Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        //System.out.println("(" + sign + ", " + Arrays.toString(val) + ")");
        if (sign == 0) {
            return "0";
        }
        StringBuffer s = new StringBuffer();
        if (sign > 0) {
            s.append(mono.vname + "(");
        } else {
            s.append(mono.vname + "[-1](");
        }
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
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        return toString();
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    @Override
    public String toScriptFactory() {
        // Python case
        return "IndexFactory()";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof IndexList)) {
            return false;
        }
        IndexList b = (IndexList) B;
        int t = this.compareTo(b);
        //System.out.println("equals: this = " + this + " B = " + B + " t = " + t);
        return (0 == t);
    }


    /**
     * hashCode. Optimized for small indexes, i.e. &le; 2<sup>4</sup> and small
     * number of variables, i.e. &le; 8.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = Arrays.hashCode(val) + sign;
        return hash;
    }


    /**
     * Returns the number of bits in the representation of this index vector.
     * @return number of bits in the representation of this IndexList, including
     *         sign bits.
     */
    public long bitLength() {
        long blen = 2; // sign
        for (int i = 0; i < val.length; i++) {
            blen += BigInteger.bitLength(val[i]);
        }
        return blen;
    }


    /**
     * Is IndexList zero.
     * @return If this sign is 0 then true is returned, else false.
     */
    public boolean isZERO() {
        return (sign == 0);
    }


    /**
     * Is IndexList one.
     * @return If this sign != 0 and length val is zero then true returned, else
     *         false.
     */
    public boolean isONE() {
        return (sign != 0 && val.length == 0);
    }


    /**
     * Is IndexList a unit.
     * @return If this is a unit then true is returned, else false.
     */
    public boolean isUnit() {
        return isONE() || negate().isONE();
    }


    /**
     * IndexList absolute value.
     * @return abs(this).
     */
    public IndexList abs() {
        if (sign >= 0) {
            return this;
        }
        return new IndexList(mono, 1, val);
    }


    /**
     * IndexList negate.
     * @return -this.
     */
    public IndexList negate() {
        if (sign == 0) {
            return this;
        }
        return new IndexList(mono, -sign, val);
    }


    /**
     * IndexList exterior product. Also called wegde product.
     * @param V other index list
     * @return this /\ V.
     */
    public IndexList exteriorProduct(IndexList V) {
        if (isZERO() || V.isZERO()) {
            return mono.getZERO(); // = 0
        }
        int s = 1;
        int m = 0, n = 0; // todo: remove or rename
        int[] u = val;
        int[] v = V.val;
        int ii = 0;
        int[] w = new int[u.length + v.length];
        int i = 0, j = 0;
        while (i < u.length && j < v.length) {
            int ul = u[i];
            int vl = v[j];
            if (ul == vl) {
                return mono.getZERO(); // = 0
            }
            if (ul < vl) {
                w[ii++] = ul;
                i++;
                m++;
            } else {
                w[ii++] = vl;
                j++;
                n++;
                if (m % 2 != 0) {
                    s = -s;
                }
            }
        }
        if (i == u.length) {
            while (j < v.length) {
                w[ii++] = v[j++];
            }
        } else {
            m += u.length - i; // - 1;
            while (i < u.length) {
                w[ii++] = u[i++];
            }
        }
        if (m % 2 != 0 && n % 2 != 0) {
            s = -s;
        }
        //System.out.println("i = " + i + ", j = " + j + ", m = " + m + ", n = " + n);
        //System.out.println("s = " + s + ", w = " + Arrays.toString(w));
        //int[] x = Arrays.copyOf(w, ii);
        return new IndexList(mono, s, w);
    }


    /**
     * IndexList multiply. <b>Note:</b> implemented by exteriorProduct.
     * @param V other index list
     * @return this * V.
     */
    public IndexList multiply(IndexList V) {
        return exteriorProduct(V);
    }


    /**
     * IndexList interior left product.
     * @param V other index list
     * @return this _| V.
     */
    public IndexList interiorLeftProduct(IndexList V) {
        return V.interiorRightProduct(this);
    }


    /**
     * IndexList interior right product.
     * @param V other index list
     * @return this |_ V.
     */
    public IndexList interiorRightProduct(IndexList V) {
        if (!this.divides(V)) {
            return mono.getZERO(); // = 0
        }
        int[] u = val;
        int[] v = V.val;
        int[] w = new int[v.length - u.length];
        int ii = 0;
        int s = 1;
        int m = 0; // todo: remove or rename
        for (int i = 0; i < v.length; i++) {
            int vl = v[i];
            boolean found = false;
            for (int j = 0; j < u.length; j++) {
                if (vl == u[j]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                w[ii++] = vl;
                m++;
            } else {
                if (m % 2 != 0) {
                    s = -s;
                }
            }
        }
        //int[] x = Arrays.copyOf(w, ii);
        return new IndexList(mono, s, w);
    }


    /**
     * IndexList divides test. Test if this is contained in V.
     * @param V other index list
     * @return true if this divides V, else false.
     */
    public boolean divides(IndexList V) {
        if (isZERO() || V.isZERO()) {
            return false;
        }
        if (val.length > V.val.length) {
            return false;
        }
        int[] vval = V.val;
        for (int i = 0; i < val.length; i++) {
            int v = val[i];
            boolean found = false;
            for (int j = i; j < vval.length; j++) {
                if (v == vval[j]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }


    /**
     * IndexList inverse. <b>Note:</b> not implemented.
     * @return 1 / this.
     */
    public IndexList inverse() {
        throw new UnsupportedOperationException("inverse not implemented");
    }


    /**
     * IndexList divide. <b>Note:</b> experimental.
     * @param V other IndexList.
     * @return this/V. <b>Note:</b> computed as interiorRightProduct, eventually
     *         useful.
     */
    public IndexList divide(IndexList V) {
        return interiorRightProduct(V);
        //throw new UnsupportedOperationException("divide not implemented");
    }


    /**
     * IndexList remainder. <b>Note:</b> not implemented.
     * @param V other IndexList.
     * @return this - (this/V). <b>Note:</b> not useful.
     */
    public IndexList remainder(IndexList V) {
        throw new UnsupportedOperationException("remainder not implemented");
    }


    /**
     * IndexList signum.
     * @return sign;
     */
    public int signum() {
        return sign;
    }


    /**
     * IndexList degree.
     * @return number of of all indexes.
     */
    public int degree() {
        if (sign == 0) {
            return -1;
        }
        return val.length;
    }


    /**
     * IndexList maximal degree.
     * @return maximal index.
     */
    public int maxDeg() {
        if (degree() < 1) {
            return -1;
        }
        return val[val.length - 1];
    }


    /**
     * IndexList minimal degree.
     * @return minimal index.
     */
    public int minDeg() {
        if (degree() < 1) {
            return -1;
        }
        return val[0];
    }


    /**
     * IndexList compareTo.
     * @param V other index list
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    @Override
    public int compareTo(IndexList V) {
        return strongCompareTo(V);
    }


    /**
     * IndexList weakCompareTo. Ignoring the degree in first pass.
     * @param V other index list
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int weakCompareTo(IndexList V) {
        if (sign == 0 && V.sign == 0) {
            return 0;
        }
        if (sign == 0) {
            return -1;
        }
        if (V.sign == 0) {
            return +1;
        }
        // both not zero
        if (sign < V.sign) {
            return -1;
        }
        if (sign > V.sign) {
            return 1;
        }
        // both have same sign
        int[] vval = V.val;
        int m = Math.min(val.length, vval.length);
        for (int i = 0; i < m; i++) {
            if (val[i] < vval[i]) {
                return -1;
            }
            if (val[i] > vval[i]) {
                return 1;
            }
        }
        if (val.length < vval.length) {
            return -1;
        }
        if (val.length > vval.length) {
            return 1;
        }
        return 0;
    }


    /**
     * IndexList strongCompareTo. Sort by degree in first pass, then by indices.
     * @param V other index list
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int strongCompareTo(IndexList V) {
        if (sign == 0 && V.sign == 0) {
            return 0;
        }
        if (sign == 0) {
            return -1;
        }
        if (V.sign == 0) {
            return +1;
        }
        // both not zero :: ignore sign
        int[] vval = V.val;
        if (val.length < vval.length) {
            return -1;
        }
        if (val.length > vval.length) {
            return 1;
        }
        int m = Math.min(val.length, vval.length);
        int tl = 0;
        for (int i = 0; i < m; i++) {
            if (val[i] < vval[i]) {
                tl = -1;
                break;
            }
            if (val[i] > vval[i]) {
                tl = 1;
                break;
            }
        }
        // now val.length == vval.length
        return tl;
    }

}
