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
 * ModIntRing factory with RingFactory interface. Effectively immutable.
 * @author Heinz Kredel
 */

public final class ModIntRing implements ModularRingFactory<ModInt>, Iterable<ModInt> {


    /**
     * Module part of the factory data structure.
     */
    public final int modul;


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
    public final static java.math.BigInteger MAX_INT = new java.math.BigInteger(
                    String.valueOf(Short.MAX_VALUE)); // not larger!


    /**
     * The constructor creates a ModIntRing object from a int integer as module
     * part.
     * @param m int integer.
     */
    public ModIntRing(int m) {
        modul = m;
    }


    /**
     * The constructor creates a ModIntRing object from a int integer as module
     * part.
     * @param m int integer.
     * @param isField indicator if m is prime.
     */
    public ModIntRing(int m, boolean isField) {
        modul = m;
        this.isField = (isField ? 1 : 0);
    }


    /**
     * The constructor creates a ModIntRing object from a Int integer as module
     * part.
     * @param m Int integer.
     */
    public ModIntRing(Integer m) {
        this(m.intValue());
    }


    /**
     * The constructor creates a ModIntRing object from a Int integer as module
     * part.
     * @param m Int integer.
     * @param isField indicator if m is prime.
     */
    public ModIntRing(Integer m, boolean isField) {
        this(m.intValue(), isField);
    }


    /**
     * The constructor creates a ModIntRing object from a BigInteger converted
     * to int as module part.
     * @param m java.math.BigInteger.
     */
    public ModIntRing(java.math.BigInteger m) {
        this(m.intValueExact());
        if (MAX_INT.compareTo(m) < 0) { // m > max
            //System.out.println("modul to large for int " + m + ",max=" + MAX_INT);
            throw new IllegalArgumentException("modul to large for int " + m + ", max=" + MAX_INT);
        }
    }


    /**
     * The constructor creates a ModIntRing object from a BigInteger converted
     * to int as module part.
     * @param m java.math.BigInteger.
     * @param isField indicator if m is prime.
     */
    public ModIntRing(java.math.BigInteger m, boolean isField) {
        this(m.intValueExact(), isField);
        if (MAX_INT.compareTo(m) < 0) { // m > max
            //System.out.println("modul to large for int " + m + ",max=" + MAX_INT);
            throw new IllegalArgumentException("modul to large for int " + m + ", max=" + MAX_INT);
        }
    }


    /**
     * The constructor creates a ModIntRing object from a String object as
     * module part.
     * @param m String.
     */
    public ModIntRing(String m) {
        this(Integer.valueOf(m.trim()));
    }


    /**
     * The constructor creates a ModIntRing object from a String object as
     * module part.
     * @param m String.
     * @param isField indicator if m is prime.
     */
    public ModIntRing(String m, boolean isField) {
        this(Integer.valueOf(m.trim()), isField);
    }


    /**
     * Get the module part as BigInteger.
     * @return modul.
     */
    public java.math.BigInteger getModul() {
        return new java.math.BigInteger(Integer.toString(modul));
    }


    /**
     * Get the module part as int.
     * @return modul.
     */
    public int getIntModul() {
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
     * Create ModInt element c.
     * @param c
     * @return a ModInt of c.
     */
    public ModInt create(java.math.BigInteger c) {
        return new ModInt(this, c);
    }


    /**
     * Create ModInt element c.
     * @param c
     * @return a ModInt of c.
     */
    public ModInt create(int c) {
        return new ModInt(this, c);
    }


    /**
     * Create ModInt element c.
     * @param c
     * @return a ModInt of c.
     */
    public ModInt create(String c) {
        return parse(c);
    }


    /**
     * Copy ModInt element c.
     * @param c
     * @return a copy of c.
     */
    public ModInt copy(ModInt c) {
        return new ModInt(this, c.val);
    }


    /**
     * Get the zero element.
     * @return 0 as ModInt.
     */
    public ModInt getZERO() {
        return new ModInt(this, 0);
    }


    /**
     * Get the one element.
     * @return 1 as ModInt.
     */
    public ModInt getONE() {
        return new ModInt(this, 1);
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<ModInt> generators() {
        List<ModInt> g = new ArrayList<ModInt>(1);
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
        java.math.BigInteger m = new java.math.BigInteger(Integer.toString(modul));
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
        return new java.math.BigInteger(Integer.toString(modul));
    }


    /**
     * Get a ModInt element from a BigInteger value.
     * @param a BigInteger.
     * @return a ModInt.
     */
    public ModInt fromInteger(java.math.BigInteger a) {
        return new ModInt(this, a);
    }


    /**
     * Get a ModInt element from a int value.
     * @param a int.
     * @return a ModInt.
     */
    public ModInt fromInteger(int a) {
        return new ModInt(this, a);
    }


    /**
     * Get a ModInt element from a long value.
     * @param a lon.
     * @return a ModInt.
     */
    public ModInt fromInteger(long a) {
        return new ModInt(this, a);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " mod(" + modul + ")"; //",max="  + MAX_INT + ")";
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
            return "GFI(" + modul + ")";
        }
        return "ZMI(" + modul + ")";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof ModIntRing)) {
            return false;
        }
        ModIntRing m = (ModIntRing) b;
        return (modul == m.modul);
    }


    /**
     * Hash code for this ModIntRing.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return modul;
    }


    /**
     * ModInt random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public ModInt random(int n) {
        return random(n, random);
    }


    /**
     * ModInt random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public ModInt random(int n, Random rnd) {
        java.math.BigInteger v = new java.math.BigInteger(n, rnd);
        return new ModInt(this, v); // rnd.nextInt() not ok
    }


    /**
     * Parse ModInt from String.
     * @param s String.
     * @return ModInt from s.
     */
    public ModInt parse(String s) {
        return new ModInt(this, s);
    }


    /**
     * Parse ModInt from Reader.
     * @param r Reader.
     * @return next ModInt from r.
     */
    public ModInt parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * ModInt chinese remainder algorithm. This is a factory method. Assert
     * c.modul &ge; a.modul and c.modul * a.modul = this.modul.
     * @param c ModInt.
     * @param ci inverse of c.modul in ring of a.
     * @param a other ModInt.
     * @return S, with S mod c.modul == c and S mod a.modul == a.
     */
    public ModInt chineseRemainder(ModInt c, ModInt ci, ModInt a) {
        //if (true) { 
        //    if (c.ring.modul < a.ring.modul) {
        //        System.out.println("ModInt error " + c.ring + ", " + a.ring);
        //    }
        //}
        ModInt b = a.ring.fromInteger(c.val); // c mod a.modul
        ModInt d = a.subtract(b); // a-c mod a.modul
        if (d.isZERO()) {
            return new ModInt(this, c.val);
        }
        b = d.multiply(ci); // b = (a-c)*ci mod a.modul
        // (c.modul*b)+c mod this.modul = c mod c.modul = 
        // (c.modul*ci*(a-c)+c) mod a.modul = a mod a.modul
        int s = c.ring.modul * b.val;
        s = s + c.val;
        return new ModInt(this, s);
    }


    /**
     * Modular digit list chinese remainder algorithm. m1 and m2 are positive
     * beta-integers, with GCD(m1,m2)=1 and m=m1*m2 less than beta. L1 and L2
     * are lists of elements of Z(m1) and Z(m2) respectively. L is a list of all
     * a in Z(m) such that a is congruent to a1 modulo m1 and a is congruent to
     * a2 modulo m2 with a1 in L1 and a2 in L2. This is a factory method. Assert
     * c.modul &ge; a.modul and c.modul * a.modul = this.modul.
     * @param m1 ModInt.
     * @param m2 other ModInt.
     * @return L list of congruences.
     */
    public static List<ModInt> chineseRemainder(ModInt m1, ModInt m2, List<ModInt> L1, List<ModInt> L2) {
        int mm = m1.ring.modul * m2.ring.modul;
        ModIntRing m = new ModIntRing(mm);
        ModInt m21 = m2.ring.fromInteger(m1.ring.modul);
        ModInt mi1 = m21.inverse();

        List<ModInt> L = new ArrayList<ModInt>();
        for (ModInt a : L1) {
            for (ModInt b : L2) {
                ModInt c = m.chineseRemainder(a, mi1, b);
                L.add(c);
            }
        }
        return L;
    }


    /**
     * Get a ModInt iterator.
     * @return a iterator over all modular integers in this ring.
     */
    public Iterator<ModInt> iterator() {
        return new ModIntIterator(this);
    }

}


/**
 * Modular integer iterator.
 * @author Heinz Kredel
 */
class ModIntIterator implements Iterator<ModInt> {


    /**
     * data structure.
     */
    int curr;


    final ModIntRing ring;


    /**
     * ModInt iterator constructor.
     * @param fac modular integer factory;
     */
    public ModIntIterator(ModIntRing fac) {
        curr = 0;
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
    public synchronized ModInt next() {
        ModInt i = new ModInt(ring, curr);
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
