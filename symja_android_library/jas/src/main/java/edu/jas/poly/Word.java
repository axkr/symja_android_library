/*
 * $Id: Word.java 4144 2012-08-26 14:29:36Z kredel $
 */

package edu.jas.poly;


import java.util.SortedMap;
import java.util.TreeMap;

import edu.jas.structure.MonoidElem;
import edu.jas.structure.MonoidFactory;
import edu.jas.structure.NotInvertibleException;


/**
 * Word implements strings of letters for polynomials.
 * @author Heinz Kredel
 */

public class Word implements MonoidElem<Word> {


    /**
     * Defining alphabet in WordFactory.
     */
    public final WordFactory mono;


    /**
     * The data structure is a String of characters.
     */
    /*package*/final String val;


    /**
     * Stored hash code.
     */
    protected int hash = 0;


    /**
     * Constructor for Word.
     * @param m factory for words.
     */
    public Word(WordFactory m) {
        this(m, "");
    }


    /**
     * Constructor for Word.
     * @param m factory for words.
     * @param s String
     */
    public Word(WordFactory m, String s) {
        mono = m;
        hash = 0;
        if (s == null) {
            throw new IllegalArgumentException("null string not allowed");
        }
        val = s; // mono.clean(s) ??
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public MonoidFactory<Word> factory() {
        return mono;
    }


    /**
     * Copy this.
     * @return copy of this.
     */
    @Override
    public Word copy() {
        return new Word(mono, val);
    }


    /**
     * Get the word String.
     * @return val.
     */
    /*package*/String getVal() {
        return val;
    }


    /**
     * Get the letter at position i.
     * @param i position.
     * @return val[i].
     */
    public char getVal(int i) {
        return val.charAt(i);
    }


    /**
     * Get the length of this word.
     * @return val.length.
     */
    public int length() {
        return val.length();
    }


    /**
     * Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (val.length() == 0) {
            return "";
        }
        StringBuffer s = new StringBuffer("\"");
        for (int i = 0; i < length(); i++) {
            if (i != 0) {
                s.append(" ");
            }
            s.append(getVal(i));
        }
        s.append("\"");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        if (val.length() == 0) {
            return "";
        }
        StringBuffer s = new StringBuffer("(");
        for (int i = 0; i < length(); i++) {
            if (i != 0) {
                s.append("*"); // TODO check for python vs ruby
            }
            s.append(getVal(i));
        }
        s.append(")");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return mono.toString();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof Word)) {
            return false;
        }
        Word b = (Word) B;
        // mono == b.mono ??
        int t = this.compareTo(b);
        //System.out.println("equals: this = " + this + " B = " + B + " t = " + t);
        return (0 == t);
    }


    /**
     * hashCode.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = val.hashCode();
        }
        return hash;
    }


    /**
     * Is Word one.
     * @return If this is the empty word then true is returned, else false.
     */
    public boolean isONE() {
        return (0 == this.length());
    }


    /**
     * Is Word unit.
     * @return If this is a unit then true is returned, else false.
     */
    public boolean isUnit() {
        return isONE();
    }


    /**
     * Word multiplication.
     * @param V
     * @return this * V.
     */
    public Word multiply(Word V) {
        return new Word(mono, this.val + V.val);
    }


    /**
     * Word divide.
     * @param V
     * @return this / V.
     */
    public Word divide(Word V) {
        Word[] ret = divideWord(V);
        // TODO: fail if both non zero?
        if (!ret[0].isONE() && !ret[1].isONE()) {
            throw new IllegalArgumentException("not simple dividable: left = " + ret[0] + ", right = "
                            + ret[1] + ", use divideWord");
        }
        return ret[0].multiply(ret[1]);
    }


    /**
     * Word divide with prefix and suffix.
     * @param V
     * @return [prefix(this/V), suffix(this/V)] = [left,right] with left * V * right = this.
     */
    public Word[] divideWord(Word V) {
        int i = this.val.indexOf(V.val);
        if (i < 0) {
            throw new NotInvertibleException("not dividable: " + this + ", other " + V);
        }
        int len = V.val.length();
        String pre = this.val.substring(0, i);
        String suf = this.val.substring(i + len);
        Word[] ret = new Word[2];
        ret[0] = new Word(mono, pre);
        ret[1] = new Word(mono, suf);
        return ret;
    }


    /**
     * Word remainder.
     * @param V
     * @return this (this/V). <b>Note:</b> not useful.
     */
    public Word remainder(Word V) {
        int i = this.val.indexOf(V.val);
        if (i < 0) {
            throw new NotInvertibleException("not dividable: " + this + ", other " + V);
        }
        return V;
    }


    /**
     * Word inverse.
     * @return 1 / this. <b>Note:</b> throws UnsupportedOperationException.
     */
    public Word inverse() {
        throw new UnsupportedOperationException("no inverse implemented for Word");
    }


    /**
     * Word signum.
     * @return 0 if this is one, 1 if it is non empty.
     */
    public int signum() {
        int i = val.length();
        if (i > 0) {
            i = 1;
        }
        assert i >= 0;
        return i;
    }


    /**
     * Word degree.
     * @return total degree of all letters.
     */
    public long degree() {
        return val.length();
    }


    /**
     * Word least common multiple.
     * @param V
     * @return ?? component wise maximum of this and V.
     */
    public Word lcm(Word V) {
        throw new UnsupportedOperationException("no divide implemented for Word");
    }


    /**
     * Word greatest common divisor.
     * @param V
     * @return ?? component wise minimum of this and V.
     */
    public Word gcd(Word V) {
        throw new UnsupportedOperationException("no divide implemented for Word");
    }


    /**
     * Word dependency on letters.
     * @return sorted map of letters and the number of its occurences.
     */
    public SortedMap<String,Integer> dependencyOnVariables() {
        SortedMap<String,Integer> map = new TreeMap<String,Integer>();
        for ( int i = 0; i < val.length(); i++ ) {
	    String s = String.valueOf( val.charAt(i) );
            Integer n = map.get(s);
            if ( n == null ) {
                n = 0;
            }
            n = n + 1;
            map.put(s,n); 
        }
        return map;
    }


    /**
     * Word multiple test.
     * @param V
     * @return true if this is a multiple of V, else false.
     */
    public boolean multipleOf(Word V) {
        return this.val.contains(V.val);
    }


    /**
     * Word divides test.
     * @param V
     * @return true if this divides V, else false.
     */
    public boolean divides(Word V) {
        return V.val.contains(this.val);
    }


    /**
     * Word compareTo. Uses <pre>String.compareTo</pre>.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    //JAVA6only: @Override
    public int compareTo(Word V) {
        return this.val.compareTo(V.val);
    }


    /**
     * Word graded comparison. Compares first be degree, then lexicographical.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int gradCompareTo(Word V) {
        long e = this.degree();
        long f = V.degree();
        if (e < f) {
            return 1;
        } else if (e > f) {
            return -1;
        }
        return this.compareTo(V);
    }

}
