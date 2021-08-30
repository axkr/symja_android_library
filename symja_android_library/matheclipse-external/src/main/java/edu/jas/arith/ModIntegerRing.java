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
 * ModIntegerRing factory with RingFactory interface. Effectively immutable.
 * @author Heinz Kredel
 */

public final class ModIntegerRing implements ModularRingFactory<ModInteger>, Iterable<ModInteger> {


    /**
     * Module part of the factory data structure.
     */
    public final java.math.BigInteger modul;


    private final static Random random = new Random();


    /**
     * Indicator if this ring is a field.
     */
    private int isField = -1; // initially unknown


    /*
     * Certainty if module is probable prime.
     */
    //private int certainty = 10;


    /**
     * The constructor creates a ModIntegerRing object from a BigInteger object
     * as module part.
     * @param m math.BigInteger.
     */
    public ModIntegerRing(java.math.BigInteger m) {
        modul = m;
    }


    /**
     * The constructor creates a ModIntegerRing object from a BigInteger object
     * as module part.
     * @param m math.BigInteger.
     * @param isField indicator if m is prime.
     */
    public ModIntegerRing(java.math.BigInteger m, boolean isField) {
        modul = m;
        this.isField = (isField ? 1 : 0);
    }


    /**
     * The constructor creates a ModIntegerRing object from a long as module
     * part.
     * @param m long.
     */
    public ModIntegerRing(long m) {
        this(new java.math.BigInteger(String.valueOf(m)));
    }


    /**
     * The constructor creates a ModIntegerRing object from a long as module
     * part.
     * @param m long.
     * @param isField indicator if m is prime.
     */
    public ModIntegerRing(long m, boolean isField) {
        this(new java.math.BigInteger(String.valueOf(m)), isField);
    }


    /**
     * The constructor creates a ModIntegerRing object from a String object as
     * module part.
     * @param m String.
     */
    public ModIntegerRing(String m) {
        this(new java.math.BigInteger(m.trim()));
    }


    /**
     * The constructor creates a ModIntegerRing object from a String object as
     * module part.
     * @param m String.
     * @param isField indicator if m is prime.
     */
    public ModIntegerRing(String m, boolean isField) {
        this(new java.math.BigInteger(m.trim()), isField);
    }


    /**
     * Get the module part.
     * @return modul.
     */
    public java.math.BigInteger getModul() {
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
     * Create ModInteger element c.
     * @param c
     * @return a ModInteger of c.
     */
    public ModInteger create(java.math.BigInteger c) {
        return new ModInteger(this, c);
    }


    /**
     * Create ModInteger element c.
     * @param c
     * @return a ModInteger of c.
     */
    public ModInteger create(long c) {
        return new ModInteger(this, c);
    }


    /**
     * Create ModInteger element c.
     * @param c
     * @return a ModInteger of c.
     */
    public ModInteger create(String c) {
        return parse(c);
    }


    /**
     * Copy ModInteger element c.
     * @param c
     * @return a copy of c.
     */
    public ModInteger copy(ModInteger c) {
        return new ModInteger(this, c.val);
    }


    /**
     * Get the zero element.
     * @return 0 as ModInteger.
     */
    public ModInteger getZERO() {
        return new ModInteger(this, java.math.BigInteger.ZERO);
    }


    /**
     * Get the one element.
     * @return 1 as ModInteger.
     */
    public ModInteger getONE() {
        return new ModInteger(this, java.math.BigInteger.ONE);
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<ModInteger> generators() {
        List<ModInteger> g = new ArrayList<ModInteger>(1);
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
        // if ( modul.isProbablePrime(certainty) ) {
        if (modul.isProbablePrime(modul.bitLength())) {
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
        return modul;
    }


    /**
     * Get a ModInteger element from a BigInteger value.
     * @param a BigInteger.
     * @return a ModInteger.
     */
    public ModInteger fromInteger(java.math.BigInteger a) {
        return new ModInteger(this, a);
    }


    /**
     * Get a ModInteger element from a long value.
     * @param a long.
     * @return a ModInteger.
     */
    public ModInteger fromInteger(long a) {
        return new ModInteger(this, a);
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return " bigMod(" + modul.toString() + ")";
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
            return "GF(" + modul.toString() + ")";
        }
        return "ZM(" + modul.toString() + ")";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if (!(b instanceof ModIntegerRing)) {
            return false;
        }
        ModIntegerRing m = (ModIntegerRing) b;
        return (0 == modul.compareTo(m.modul));
    }


    /**
     * Hash code for this ModIntegerRing.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return modul.hashCode();
    }


    /**
     * ModInteger random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public ModInteger random(int n) {
        return random(n, random);
    }


    /**
     * ModInteger random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public ModInteger random(int n, Random rnd) {
        java.math.BigInteger v = new java.math.BigInteger(n, rnd);
        return new ModInteger(this, v);
    }


    /**
     * Parse ModInteger from String.
     * @param s String.
     * @return ModInteger from s.
     */
    public ModInteger parse(String s) {
        return new ModInteger(this, s);
    }


    /**
     * Parse ModInteger from Reader.
     * @param r Reader.
     * @return next ModInteger from r.
     */
    public ModInteger parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * ModInteger chinese remainder algorithm. This is a factory method. Assert
     * c.modul &ge; a.modul and c.modul * a.modul = this.modul.
     * @param c ModInteger.
     * @param ci inverse of c.modul in ring of a.
     * @param a other ModInteger.
     * @return S, with S mod c.modul == c and S mod a.modul == a.
     */
    public ModInteger chineseRemainder(ModInteger c, ModInteger ci, ModInteger a) {
        //if (false) { // debug
        //    if (c.ring.modul.compareTo(a.ring.modul) < 1) {
        //        System.out.println("ModInteger error " + c + ", " + a);
        //    }
        //}
        ModInteger b = a.ring.fromInteger(c.val); // c mod a.modul
        ModInteger d = a.subtract(b); // a-c mod a.modul
        if (d.isZERO()) {
            return fromInteger(c.val);
        }
        b = d.multiply(ci); // b = (a-c)*ci mod a.modul
        // (c.modul*b)+c mod this.modul = c mod c.modul = 
        // (c.modul*ci*(a-c)+c) mod a.modul = a mod a.modul
        java.math.BigInteger s = c.ring.modul.multiply(b.val);
        s = s.add(c.val);
        return fromInteger(s);
    }


    /**
     * Modular integer list chinese remainder algorithm. m1 and m2 are positive
     * integers, with GCD(m1,m2)=1 and m=m1*m2 less than beta. L1 and L2 are
     * lists of elements of Z(m1) and Z(m2) respectively. L is a list of all a
     * in Z(m) such that a is congruent to a1 modulo m1 and a is congruent to a2
     * modulo m2 with a1 in L1 and a2 in L2. This is a factory method. Assert
     * c.modul &ge; a.modul and c.modul * a.modul = this.modul.
     * @param m1 modular integer.
     * @param m2 other modular integer.
     * @return L list of congruences.
     */
    public static List<ModInteger> chineseRemainder(ModInteger m1, ModInteger m2, List<ModInteger> L1,
                    List<ModInteger> L2) {
        java.math.BigInteger mm = m1.ring.modul.multiply(m2.ring.modul);
        ModIntegerRing m = new ModIntegerRing(mm);
        ModInteger m21 = m2.ring.fromInteger(m1.ring.modul);
        ModInteger mi1 = m21.inverse();

        List<ModInteger> L = new ArrayList<ModInteger>();
        for (ModInteger a : L1) {
            for (ModInteger b : L2) {
                ModInteger c = m.chineseRemainder(a, mi1, b);
                L.add(c);
            }
        }
        return L;
    }


    /**
     * Get a ModInteger iterator.
     * @return a iterator over all modular integers in this ring.
     */
    public Iterator<ModInteger> iterator() {
        return new ModIntegerIterator(this);
    }

}


/**
 * Modular integer iterator.
 * @author Heinz Kredel
 */
class ModIntegerIterator implements Iterator<ModInteger> {


    /**
     * data structure.
     */
    java.math.BigInteger curr;


    final ModIntegerRing ring;


    /**
     * ModInteger iterator constructor.
     * @param fac modular integer factory;
     */
    public ModIntegerIterator(ModIntegerRing fac) {
        curr = java.math.BigInteger.ZERO;
        ring = fac;
    }


    /**
     * Test for availability of a next element.
     * @return true if the iteration has more elements, else false.
     */
    public synchronized boolean hasNext() {
        return curr.compareTo(ring.modul) < 0;
    }


    /**
     * Get next integer.
     * @return next integer.
     */
    public synchronized ModInteger next() {
        ModInteger i = new ModInteger(ring, curr);
        curr = curr.add(java.math.BigInteger.ONE);
        return i;
    }


    /**
     * Remove an element if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove elements");
    }
}
