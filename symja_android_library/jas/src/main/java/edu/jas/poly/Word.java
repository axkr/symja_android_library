/*
 * $Id$
 */

package edu.jas.poly;


import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import edu.jas.structure.MonoidElem;
import edu.jas.structure.MonoidFactory;
import edu.jas.structure.NotInvertibleException;


/**
 * Word implements strings of letters for polynomials.
 * @author Heinz Kredel
 */

public final class Word implements MonoidElem<Word> {


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
        this(m,s,true);
    }


    /**
     * Constructor for Word.
     * @param m factory for words.
     * @param s String
     * @param translate indicator if s needs translation
     */
    public Word(WordFactory m, String s, boolean translate) {
        mono = m;
        hash = 0;
        if (s == null) {
            throw new IllegalArgumentException("null string not allowed");
        }
        if ( translate ) {
            if ( mono.translation != null ) {
                //System.out.println("s = " + s);
                String[] S = GenPolynomialTokenizer.variableList(s);
                //System.out.println("S = " + Arrays.toString(S));
                val = mono.translate(S);
                //System.out.println("val = " + val);
            } else {
                val = WordFactory.cleanSpace(s); //??
            }
        } else {
            val = s;
        }
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
        return new Word(mono, val, false);
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
        String vv;
        if ( mono.translation == null ) {
            for (int i = 0; i < length(); i++) {
                if (i != 0) {
                    s.append(" ");
                }
                s.append(getVal(i));
            }
        } else { 
            for (int i = 0; i < length(); i++) {
                if (i != 0) {
                    s.append(" ");
                }
                s.append(mono.transVar(getVal(i)));
            }
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
        StringBuffer s = new StringBuffer("");
        if ( mono.translation == null ) {
            for (int i = 0; i < length(); i++) {
                if (i != 0) {
                    s.append("*"); // checked for python vs ruby
                }
                s.append(getVal(i));
            }
        } else { 
            for (int i = 0; i < length(); i++) {
                if (i != 0) {
                    s.append("*"); // checked for python vs ruby
                }
                s.append(mono.transVar(getVal(i)));
            }
        }
        s.append("");
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
     * @param V other word.
     * @return this * V.
     */
    public Word multiply(Word V) {
        return new Word(mono, this.val + V.val, false);
    }


    /**
     * Word divide.
     * @param V other word.
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
     * @param V other word.
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
        ret[0] = new Word(mono, pre, false);
        ret[1] = new Word(mono, suf, false);
        return ret;
    }


    /**
     * Word remainder.
     * @param V other word.
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
     * @return 1 / this. 
     */
    public Word inverse() {
        if ( val.length() == 0 ) {
            return this;
        }
        throw new NotInvertibleException("not inversible " + this);
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
     * @param V other word.
     * @return true if this is a multiple of V, else false.
     */
    public boolean multipleOf(Word V) {
        return this.val.contains(V.val);
    }


    /**
     * Word divides test.
     * @param V other word.
     * @return true if this divides V, else false.
     */
    public boolean divides(Word V) {
        return V.val.contains(this.val);
    }


    /**
     * Word compareTo. Uses <pre>String.compareTo</pre>.
     * @param V other word.
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    //JAVA6only: @Override
    public int compareTo(Word V) {
        return this.val.compareTo(V.val);
    }


    /**
     * Word graded comparison. Compares first be degree, then lexicographical.
     * @param V other word.
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


    /**
     * Word graded comparison. Compares first be degree, then inverse lexicographical.
     * @param V other word.
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int gradInvlexCompareTo(Word V) {
        long e = this.degree();
        long f = V.degree();
        if (e < f) {
            return 1;
        } else if (e > f) {
            return -1;
        }
        return -this.compareTo(V);
    }


    /**
     * Is word overlap.
     * @param ol = [l1,r1,l2,r2] an Overlap container of four words
     * @param V word
     * @return true if l1 * this * r1 = l2 * V * r2, else false.
     */
    public boolean isOverlap(Overlap ol, Word V) {
        return ol.isOverlap(this,V);
    }


    /**
     * Word overlap list.
     * @param V other word.
     * @return list of overlaps [l1,r1,l2,r2] with l1 * this * r1 = l2 * V * r2.
     *         If no such overlaps exist the empty overlap list is returned.
     */
    public OverlapList overlap(Word V) {
        OverlapList ret = new OverlapList();
        Word wone = mono.getONE();
        String a = this.val;
        String b = V.val;
        int ai = a.length();
        int bi = b.length();
        int j = b.indexOf(a);
        if (j >= 0) {
            while ( j >= 0 ) {
                String pre = b.substring(0, j);
                String suf = b.substring(j + ai);
                Word wpre = new Word(mono, pre, false);
                Word wsuf = new Word(mono, suf, false);
                ret.add(new Overlap(wpre,wsuf,wone,wone));
                j = b.indexOf(a,j+ai); // +1 also inner overlaps ?
            }
            return ret;
        }
        j = a.indexOf(b);
        if (j >= 0) {
            while ( j >= 0 ) {
                String pre = a.substring(0, j);
                String suf = a.substring(j + bi);
                Word wpre = new Word(mono, pre, false);
                Word wsuf = new Word(mono, suf, false);
                ret.add(new Overlap(wone,wone,wpre,wsuf));
                j = a.indexOf(b,j+bi); // +1 also inner overlaps ?
            }
            return ret;
        }
        if ( ai >= bi ) {
            for ( int i = 0; i < bi; i++ ) {
                String as = a.substring(0,i+1);
                String bs = b.substring(bi-i-1,bi);
                //System.out.println("i = " + i + ", bs = " + bs + ", as = " + as);
                if ( as.equals(bs) ) {
                    Word w1 = new Word(mono, b.substring(0,bi-i-1), false);
                    Word w2 = new Word(mono, a.substring(i+1), false);
                    ret.add(new Overlap(w1, wone, wone, w2));
                    break;
                }
            }
            for ( int i = 0; i < bi; i++ ) {
                String as = a.substring(ai-i-1,ai);
                String bs = b.substring(0,i+1);
                //System.out.println("i = " + i + ", bs = " + bs + ", as = " + as);
                if ( as.equals(bs) ) {
                    Word w1 = new Word(mono, b.substring(i+1), false);
                    Word w2 = new Word(mono, a.substring(0,ai-i-1), false);
                    ret.add(new Overlap(wone, w1, w2, wone));
                    break;
                }
            }
        } else { // ai < bi
            for ( int i = 0; i < ai; i++ ) {
                String as = a.substring(ai-i-1,ai);
                String bs = b.substring(0,i+1);
                //System.out.println("i = " + i + ", bs = " + bs + ", as = " + as);
                if ( as.equals(bs) ) {
                    Word w1 = new Word(mono, b.substring(i+1), false);
                    Word w2 = new Word(mono, a.substring(0,ai-i-1), false);
                    ret.add(new Overlap(wone, w1, w2, wone));
                    break;
                }
            }
            for ( int i = 0; i < ai; i++ ) {
                String as = a.substring(0,i+1);
                String bs = b.substring(bi-i-1,bi);
                //System.out.println("i = " + i + ", bs = " + bs + ", as = " + as);
                if ( as.equals(bs) ) {
                    Word w1 = new Word(mono, b.substring(0,bi-i-1), false);
                    Word w2 = new Word(mono, a.substring(i+1), false);
                    ret.add(new Overlap(w1, wone, wone, w2));
                    break;
                }
            }
        }
        return ret;
    }


    /**
     * Word pseudo least common multiple.
     * @param V other word.
     * @return w = l1*this*r1, with l1*this*r1 == l2*V*r2, if l1, r1, l2, r2 exist,
     *         else null is returned.
     */
    public Word lcm(Word V) {
        OverlapList oll = overlap(V);
        if ( oll.ols.isEmpty() ) {
            return null; 
        }
        Overlap ol = oll.ols.get(0);
        Word g =  ol.l1.multiply(this).multiply(ol.r1); 
        return g;
    }

}
