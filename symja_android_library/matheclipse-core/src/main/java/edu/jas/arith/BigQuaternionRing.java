/*
 * $Id$
 */

package edu.jas.arith;


import org.apache.log4j.Logger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.jas.kern.StringUtil;
import edu.jas.structure.RingFactory;

// import java.math.BigInteger;


/**
 * BigQuaternion ring class based on BigRational implementing the RingElem
 * interface.
 *
 * @author Heinz Kredel
 */

public final class BigQuaternionRing implements RingFactory<BigQuaternion> {


    protected final static Random random = new Random();
    private static final Logger logger = Logger.getLogger(BigQuaternionRing.class);
    /**
     * List of all 24 integral units.
     */
    static List<BigQuaternion> entierUnits = null; //later: unitsOfHurwitzian();
    /**
     * The constant 0.
     */
    public final BigQuaternion ZERO = new BigQuaternion(this);


    //private static final boolean debug = logger.isDebugEnabled();
    /**
     * The constant 1.
     */
    public final BigQuaternion ONE = new BigQuaternion(this, BigRational.ONE);
    /**
     * The constant i.
     */
    public final BigQuaternion I = new BigQuaternion(this, BigRational.ZERO, BigRational.ONE);
    /**
     * The constant j.
     */
    public final BigQuaternion J = new BigQuaternion(this, BigRational.ZERO, BigRational.ZERO,
            BigRational.ONE);
    /**
     * The constant k.
     */
    public final BigQuaternion K = new BigQuaternion(this, BigRational.ZERO, BigRational.ZERO,
            BigRational.ZERO, BigRational.ONE);
    /**
     * Flag to signal if this ring is integral.
     */
    protected boolean integral = false;


    /**
     * Constructor for a BigQuaternion ring.
     */
    public BigQuaternionRing() {
        this(false);
    }


    /**
     * Constructor for a BigQuaternion ring.
     */
    public BigQuaternionRing(boolean i) {
        integral = i;
        logger.info("integral = " + integral);
    }

    /**
     * Get a list of the generating elements.
     *
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<BigQuaternion> generators() {
        List<BigQuaternion> g = new ArrayList<BigQuaternion>(4);
        g.add(getONE());
        g.add(I);
        g.add(J);
        g.add(K);
        return g;
    }

    /**
     * Is this structure finite or infinite.
     *
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return false;
    }

    /**
     * Copy BigQuaternion element c.
     *
     * @param c BigQuaternion.
     * @return a copy of c.
     */
    public BigQuaternion copy(BigQuaternion c) {
        return new BigQuaternion(this, c.re, c.im, c.jm, c.km);
    }

    /**
     * Get the zero element.
     *
     * @return 0 as BigQuaternion.
     */
    public BigQuaternion getZERO() {
        return ZERO;
    }

    /**
     * Get the one element.
     *
     * @return q as BigQuaternion.
     */
    public BigQuaternion getONE() {
        return ONE;
    }

    /**
     * Query if this ring is commutative.
     *
     * @return false.
     */
    public boolean isCommutative() {
        return false;
    }

    /**
     * Query if this ring is associative.
     *
     * @return true.
     */
    public boolean isAssociative() {
        return true;
    }

    /**
     * Query if this ring is a field.
     *
     * @return true.
     */
    public boolean isField() {
        return !integral;
    }

    /**
     * Characteristic of this ring.
     *
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return java.math.BigInteger.ZERO;
    }

    /**
     * Get a BigQuaternion element from a BigInteger.
     *
     * @param a BigInteger.
     * @return a BigQuaternion.
     */
    public BigQuaternion fromInteger(java.math.BigInteger a) {
        return new BigQuaternion(this, new BigRational(a));
    }

    /**
     * Get a BigQuaternion element from a long.
     *
     * @param a long.
     * @return a BigQuaternion.
     */
    public BigQuaternion fromInteger(long a) {
        return new BigQuaternion(this, new BigRational(a));
    }

    /**
     * Get a BigQuaternion element from a long vector.
     *
     * @param a long vector.
     * @return a BigQuaternion.
     */
    public BigQuaternion fromInteger(long[] a) {
        return new BigQuaternion(this, new BigRational(a[0]), new BigRational(a[1]), new BigRational(a[2]),
                new BigRational(a[3]));
    }

    /**
     * Get the string representation. Is compatible with the string constructor.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        String s = "BigQuaternionRing(" + integral + ")";
        return s;
    }


    /**
     * Get a scripting compatible string representation.
     *
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer("BigQuaternionRing(");
        s.append(integral);
        s.append(")");
        return s.toString();
    }


    /**
     * Comparison with any other object.
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof BigQuaternionRing)) {
            return false;
        }
        BigQuaternionRing B = (BigQuaternionRing) b;
        return this.integral == B.integral;
    }


    /**
     * Hash code for this BigQuaternionRing.
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = 4711;
        return h;
    }


    /**
     * BigQuaternion units of the Hurwitzian integers. BigQuaternion units with
     * all integer or all 1/2 times integer components.
     *
     * @return list of all 24 units.
     */
    public List<BigQuaternion> unitsOfHurwitzian() {
        if (entierUnits != null) {
            return entierUnits;
        }
        BigRational half = BigRational.HALF;
        // Lipschitz integer units
        List<BigQuaternion> units = generators();
        List<BigQuaternion> u = new ArrayList<BigQuaternion>(units);
        for (BigQuaternion ue : u) {
            units.add(ue.negate());
        }
        // Hurwitz integer units
        long[][] comb = new long[][]{{1, 1, 1, 1}, {-1, 1, 1, 1}, {1, -1, 1, 1}, {-1, -1, 1, 1},
                {1, 1, -1, 1}, {-1, 1, -1, 1}, {1, -1, -1, 1}, {-1, -1, -1, 1}, {1, 1, 1, -1},
                {-1, 1, 1, -1}, {1, -1, 1, -1}, {-1, -1, 1, -1}, {1, 1, -1, -1}, {-1, 1, -1, -1},
                {1, -1, -1, -1}, {-1, -1, -1, -1}};
        for (long[] row : comb) {
            BigQuaternion ue = fromInteger(row);
            ue = ue.multiply(half);
            units.add(ue);
        }
        //System.out.println("units = " + units);
        //for (BigQuaternion ue : units) {
        //System.out.println("unit = " + ue + ", norm = " + ue.norm());
        //}
        entierUnits = units;
        return units;
    }


    /**
     * BigQuaternion random. Random rational numbers A, B, C and D are generated
     * using random(n). Then R is the quaternion number with real part A and
     * imaginary parts B, C and D.
     *
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigQuaternion.
     */
    public BigQuaternion random(int n) {
        return random(n, random);
    }


    /**
     * BigQuaternion random. Random rational numbers A, B, C and D are generated
     * using RNRAND(n). Then R is the quaternion number with real part A and
     * imaginary parts B, C and D.
     *
     * @param n   such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return R, a random BigQuaternion.
     */
    public BigQuaternion random(int n, Random rnd) {
        BigRational r = BigRational.ONE.random(n, rnd);
        BigRational i = BigRational.ONE.random(n, rnd);
        BigRational j = BigRational.ONE.random(n, rnd);
        BigRational k = BigRational.ONE.random(n, rnd);
        BigQuaternion q = new BigQuaternion(this, r, i, j, k);
        if (integral) {
            q = q.roundToHurwitzian();
        }
        return q;
    }


    /*
     * Quaternion number, random. Random rational numbers A, B, C and D are
     * generated using RNRAND(n). Then R is the quaternion number with real part
     * A and imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigQuaternion.
    public static BigQuaternion QRAND(int n) {
        return ONE.random(n, random);
    }
     */


    /**
     * Parse quaternion number from String.
     *
     * @param s String.
     * @return BigQuaternion from s.
     */
    public BigQuaternion parse(String s) {
        return new BigQuaternion(this, s);
    }


    /**
     * Parse quaternion number from Reader.
     *
     * @param r Reader.
     * @return next BigQuaternion from r.
     */
    public BigQuaternion parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }

}
