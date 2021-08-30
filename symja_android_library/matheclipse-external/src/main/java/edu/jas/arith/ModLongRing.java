/*
 * $Id$
 */

package edu.jas.arith;


import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.jas.kern.StringUtil;


/**
 * ModLongRing factory with RingFactory interface. Effectively immutable.
 * @author Heinz Kredel
 */

public final class ModLongRing implements ModularRingFactory<ModLong>, Iterable<ModLong> {


    /**
     * Module part of the factory data structure.
     */
    public final long modul;


    /**
     * Random number generator.
     */
    private final static Random random = new Random();


    /**
     * Indicator if this ring is a field.
     */
    private int isField = -1; // initially unknown


    /*
     * Certainty if module is probable prime.
     */
    //private final int certainty = 10;


    /**
     * maximal representable integer.
     */
    public final static java.math.BigInteger MAX_LONG = new java.math.BigInteger(
                    String.valueOf(Integer.MAX_VALUE)); // not larger!


    /**
     * The constructor creates a ModLongRing object from a long integer as
     * module part.
     * @param m long integer.
     */
    public ModLongRing(long m) {
        modul = m;
    }


    /**
     * The constructor creates a ModLongRing object from a long integer as
     * module part.
     * @param m long integer.
     * @param isField indicator if m is prime.
     */
    public ModLongRing(long m, boolean isField) {
        modul = m;
        this.isField = (isField ? 1 : 0);
    }


    /**
     * The constructor creates a ModLongRing object from a Long integer as
     * module part.
     * @param m Long integer.
     */
    public ModLongRing(Long m) {
        this(m.longValue());
    }


    /**
     * The constructor creates a ModLongRing object from a Long integer as
     * module part.
     * @param m Long integer.
     * @param isField indicator if m is prime.
     */
    public ModLongRing(Long m, boolean isField) {
        this(m.longValue(), isField);
    }


    /**
     * The constructor creates a ModLongRing object from a BigInteger converted
     * to long as module part.
     * @param m java.math.BigInteger.
     */
    public ModLongRing(java.math.BigInteger m) {
        this(m.longValueExact());
        if (MAX_LONG.compareTo(m) < 0) { // m > max
            //System.out.println("modul to large for long " + m + ",max=" + MAX_LONG);
            throw new IllegalArgumentException("modul to large for long " + m + ", max=" + MAX_LONG);
        }
    }


    /**
     * The constructor creates a ModLongRing object from a BigInteger converted
     * to long as module part.
     * @param m java.math.BigInteger.
     * @param isField indicator if m is prime.
     */
    public ModLongRing(java.math.BigInteger m, boolean isField) {
        this(m.longValueExact(), isField);
        if (MAX_LONG.compareTo(m) < 0) { // m > max
            //System.out.println("modul to large for long " + m + ",max=" + MAX_LONG);
            throw new IllegalArgumentException("modul to large for long " + m + ", max=" + MAX_LONG);
        }
    }


    /**
     * The constructor creates a ModLongRing object from a String object as
     * module part.
     * @param m String.
     */
    public ModLongRing(String m) {
        this(Long.valueOf(m.trim()));
    }


    /**
     * The constructor creates a ModLongRing object from a String object as
     * module part.
     * @param m String.
     * @param isField indicator if m is prime.
     */
    public ModLongRing(String m, boolean isField) {
        this(Long.valueOf(m.trim()), isField);
    }


    /**
     * Get the module part as BigInteger.
     * @return modul.
     */
    public java.math.BigInteger getModul() {
        return new java.math.BigInteger(Long.toString(modul));
    }


    /**
     * Get the module part as long.
     * @return modul.
     */
    public long getLongModul() {
        return modul;
    }


    /**
     * Get the module part as BigInteger.
     * @return modul.
     */
    public BigInteger getIntegerModul() {
        return new BigInteger(modul);
    }


    /**
     * Create ModLong element c.
     * @param c
     * @return a ModLong of c.
     */
    public ModLong create(java.math.BigInteger c) {
        return new ModLong(this, c);
    }


    /**
     * Create ModLong element c.
     * @param c
     * @return a ModLong of c.
     */
    public ModLong create(long c) {
        return new ModLong(this, c);
    }


    /**
     * Create ModLong element c.
     * @param c
     * @return a ModLong of c.
     */
    public ModLong create(String c) {
        return parse(c);
    }


    /**
     * Copy ModLong element c.
     * @param c
     * @return a copy of c.
     */
    public ModLong copy(ModLong c) {
        return new ModLong(this, c.val);
    }


    /**
     * Get the zero element.
     * @return 0 as ModLong.
     */
    public ModLong getZERO() {
        return new ModLong(this, 0L);
    }


    /**
     * Get the one element.
     * @return 1 as ModLong.
     */
    public ModLong getONE() {
        return new ModLong(this, 1L);
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<ModLong> generators() {
        List<ModLong> g = new ArrayList<ModLong>(1);
        g.add(getONE());
        return g;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return true;
    }


    /**
     * Query if this ring is commutative.
     * @return true.
     */
    public boolean isCommutative() {
        return true;
    }


    /**
     * Query if this ring is associative.
     * @return true.
     */
    public boolean isAssociative() {
        return true;
    }


    /**
     * Query if this ring is a field.
     * @return true if module is prime, else false.
     */
    public boolean isField() {
        if (isField > 0) {
            return true;
        }
        if (isField == 0) {
            return false;
        }
        //System.out.println("isProbablePrime " + modul + " = " + modul.isProbablePrime(certainty));
        java.math.BigInteger m = new java.math.BigInteger(Long.toString(modul));
        if (m.isProbablePrime(m.bitLength())) {
            isField = 1;
            return true;
        }
        isField = 0;
        return false;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return new java.math.BigInteger(Long.toString(modul));
    }


    /**
     * Get a ModLong element from a BigInteger value.
     * @param a BigInteger.
     * @return a ModLong.
     */
    public ModLong fromInteger(java.math.BigInteger a) {
        return new ModLong(this, a);
    }


    /**
     * Get a ModLong element from a long value.
     * @param a long.
     * @return a ModLong.
     */
    public ModLong fromInteger(long a) {
        return new ModLong(this, a);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " mod(" + modul + ")"; //",max="  + MAX_LONG + ")";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    @Override
    public String toScript() {
        // Python and Ruby case
        if (isField()) {
            return "GFL(" + modul + ")";
        }
        return "ZML(" + modul + ")";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof ModLongRing)) {
            return false;
        }
        ModLongRing m = (ModLongRing) b;
        return (modul == m.modul);
    }


    /**
     * Hash code for this ModLongRing.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int) modul;
    }


    /**
     * ModLong random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public ModLong random(int n) {
        return random(n, random);
    }


    /**
     * ModLong random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public ModLong random(int n, Random rnd) {
        java.math.BigInteger v = new java.math.BigInteger(n, rnd);
        return new ModLong(this, v); // rnd.nextLong() not ok
    }


    /**
     * Parse ModLong from String.
     * @param s String.
     * @return ModLong from s.
     */
    public ModLong parse(String s) {
        return new ModLong(this, s);
    }


    /**
     * Parse ModLong from Reader.
     * @param r Reader.
     * @return next ModLong from r.
     */
    public ModLong parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * ModLong chinese remainder algorithm. This is a factory method. Assert
     * c.modul &ge; a.modul and c.modul * a.modul = this.modul.
     * @param c ModLong.
     * @param ci inverse of c.modul in ring of a.
     * @param a other ModLong.
     * @return S, with S mod c.modul == c and S mod a.modul == a.
     */
    public ModLong chineseRemainder(ModLong c, ModLong ci, ModLong a) {
        //if (true) { 
        //    if (c.ring.modul < a.ring.modul) {
        //        System.out.println("ModLong error " + c.ring + ", " + a.ring);
        //    }
        //}
        ModLong b = a.ring.fromInteger(c.val); // c mod a.modul
        ModLong d = a.subtract(b); // a-c mod a.modul
        if (d.isZERO()) {
            return new ModLong(this, c.val);
        }
        b = d.multiply(ci); // b = (a-c)*ci mod a.modul
        // (c.modul*b)+c mod this.modul = c mod c.modul = 
        // (c.modul*ci*(a-c)+c) mod a.modul = a mod a.modul
        long s = c.ring.modul * b.val;
        s = s + c.val;
        return new ModLong(this, s);
    }


    /**
     * Modular digit list chinese remainder algorithm. m1 and m2 are positive
     * beta-integers, with GCD(m1,m2)=1 and m=m1*m2 less than beta. L1 and L2
     * are lists of elements of Z(m1) and Z(m2) respectively. L is a list of all
     * a in Z(m) such that a is congruent to a1 modulo m1 and a is congruent to
     * a2 modulo m2 with a1 in L1 and a2 in L2. This is a factory method. Assert
     * c.modul &ge; a.modul and c.modul * a.modul = this.modul.
     * @param m1 ModLong.
     * @param m2 other ModLong.
     * @return L list of congruences.
     */
    public static List<ModLong> chineseRemainder(ModLong m1, ModLong m2, List<ModLong> L1, List<ModLong> L2) {
        long mm = m1.ring.modul * m2.ring.modul;
        ModLongRing m = new ModLongRing(mm);
        ModLong m21 = m2.ring.fromInteger(m1.ring.modul);
        ModLong mi1 = m21.inverse();

        List<ModLong> L = new ArrayList<ModLong>();
        for (ModLong a : L1) {
            for (ModLong b : L2) {
                ModLong c = m.chineseRemainder(a, mi1, b);
                L.add(c);
            }
        }
        return L;
    }


    /**
     * Get a ModLong iterator.
     * @return a iterator over all modular integers in this ring.
     */
    public Iterator<ModLong> iterator() {
        return new ModLongIterator(this);
    }

}


/**
 * Modular integer iterator.
 * @author Heinz Kredel
 */
class ModLongIterator implements Iterator<ModLong> {


    /**
     * data structure.
     */
    long curr;


    final ModLongRing ring;


    /**
     * ModLong iterator constructor.
     * @param fac modular integer factory;
     */
    public ModLongIterator(ModLongRing fac) {
        curr = 0L;
        ring = fac;
    }


    /**
     * Test for availability of a next element.
     * @return true if the iteration has more elements, else false.
     */
    public synchronized boolean hasNext() {
        return curr < ring.modul;
    }


    /**
     * Get next integer.
     * @return next integer.
     */
    public synchronized ModLong next() {
        ModLong i = new ModLong(ring, curr);
        curr++;
        return i;
    }


    /**
     * Remove an element if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove elements");
    }
}
