/*
 * $Id$
 */

package edu.jas.poly;


import java.io.Reader;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
//import java.util.Set;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.kern.StringUtil;
import edu.jas.structure.MonoidFactory;


/**
 * WordFactory implements alphabet related methods.
 * @author Heinz Kredel
 */

public final class WordFactory implements MonoidFactory<Word> {


    /**
     * The data structure is a String of characters which defines the alphabet.
     */
    /*package*/final String alphabet;


    /**
     * The empty word for this monoid.
     */
    public final Word ONE;


    /**
     * The translation reference string.
     */
    public static final String transRef = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    /**
     * The translation array of Strings.
     */
    public final String[] translation;


    /**
     * Random number generator.
     */
    private final static Random random = new Random();


    /**
     * Log4j logger object.
     */
    private static final Logger logger = LogManager.getLogger(WordFactory.class);


    /**
     * Comparator for Words.
     */
    public static abstract class WordComparator implements Comparator<Word>, Serializable {


        public abstract int compare(Word e1, Word e2);
    }


    /**
     * Defined descending order comparator. Sorts the highest terms first.
     */
    private static final WordComparator horder = new WordComparator() {


        @Override
        public int compare(Word e1, Word e2) {
            //return e1.gradCompareTo(e2);
            return e1.gradInvlexCompareTo(e2);
        }
    };


    /**
     * Defined ascending order comparator. Sorts the lowest terms first.
     */
    private static final WordComparator lorder = new WordComparator() {


        @Override
        public int compare(Word e1, Word e2) {
            //return -e1.gradCompareTo(e2);
            return -e1.gradInvlexCompareTo(e2);
        }
    };


    /**
     * Constructor for WordFactory.
     */
    public WordFactory() {
        this("");
    }


    /**
     * Constructor for WordFactory.
     * @param s String of single letters for alphabet
     */
    public WordFactory(String s) {
        if (s == null) {
            throw new IllegalArgumentException("null string not allowed");
        }
        String alp = cleanSpace(s);
        translation = null;
        Map<String, Integer> hist = Word.histogram(alp);
        if (hist.size() != alp.length()) {
            logger.warn("multiple characters in String: " + hist);
            //Set<String> k = hist.keySet();
            String[] ka = hist.keySet().toArray(new String[hist.size()]);
            alphabet = concat(ka);
        } else {
            alphabet = alp;
        }
        //System.out.println("hist = " + hist);
        ONE = new Word(this, "", false);
    }


    /**
     * Constructor for WordFactory.
     * @param S String array for alphabet
     */
    public WordFactory(String[] S) {
        String[] V = cleanAll(S);
        if (isSingleLetters(V)) {
            alphabet = concat(V);
            translation = null;
        } else {
            alphabet = transRef.substring(0, V.length);
            translation = V;
            logger.info("alphabet = " + alphabet + ", translation = " + Arrays.toString(translation));
        }
        ONE = new Word(this, "", false);
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        if (alphabet.length() == 0) {
            return true;
        }
        return false;
    }


    /**
     * Query if this monoid is commutative.
     * @return true if this monoid is commutative, else false.
     */
    public boolean isCommutative() {
        if (alphabet.length() == 0) {
            return true;
        }
        return false;
    }


    /**
     * Query if this monoid is associative.
     * @return true if this monoid is associative, else false.
     */
    public boolean isAssociative() {
        return true;
    }


    /**
     * Get the one element, the empty word.
     * @return 1 as Word.
     */
    public Word getONE() {
        return ONE;
    }


    /**
     * Copy word.
     * @param w word to copy.
     * @return copy of w.
     */
    @Override
    public Word copy(Word w) {
        return new Word(this, w.getVal(), false);
    }


    /**
     * Get the alphabet length.
     * @return alphabet.length.
     */
    public int length() {
        return alphabet.length();
    }


    /**
     * Get the alphabet String.
     * @return alphabet.
     */
    /*package*/String getVal() {
        return alphabet;
    }


    /**
     * Get the translation array of Strings.
     * @return alphabet.
     */
    /*package*/String[] getTrans() {
        return translation;
    }


    /**
     * Get the alphabet letter at position i.
     * @param i position.
     * @return val[i].
     */
    public char getVal(int i) {
        return alphabet.charAt(i);
    }


    /**
     * Get the variable names.
     * @return array of variable names
     */
    public String[] getVars() {
        String[] vars = new String[alphabet.length()];
        if (translation == null) {
            for (int i = 0; i < alphabet.length(); i++) {
                vars[i] = String.valueOf(getVal(i));
            }
        } else {
            for (int i = 0; i < alphabet.length(); i++) {
                vars[i] = translation[i];
            }
        }
        return vars;
    }


    /**
     * Extend variables. Extend number of variables by length(vn). 
     * @param vn names for extended variables.
     * @return extended word ring factory.
     */
    public WordFactory extend(String[] vn) {
        String[] vars = new String[length() + vn.length];
        int i = 0;
        for (String v : getVars()) {
            vars[i++] = v;
        }
        for (String v : vn) {
            vars[i++] = v;
        }
        return new WordFactory(vars);
    }

    
    /**
     * Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("\"");
        if (translation == null) {
            for (int i = 0; i < alphabet.length(); i++) {
                if (i != 0) {
                    s.append(",");
                }
                s.append(getVal(i));
            }
        } else {
            for (int i = 0; i < alphabet.length(); i++) {
                if (i != 0) {
                    s.append(",");
                }
                s.append(translation[i]);
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
    @Override
    public String toScript() {
        return toString();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object B) {
        if (!(B instanceof WordFactory)) {
            return false;
        }
        WordFactory b = (WordFactory) B;
        return alphabet.equals(b.alphabet);
    }


    /**
     * hashCode.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return alphabet.hashCode();
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     */
    public List<Word> generators() {
        int len = alphabet.length();
        List<Word> gens = new ArrayList<Word>(len);
        // ONE is not a word generator
        for (int i = 0; i < len; i++) {
            Word w = new Word(this, String.valueOf(alphabet.charAt(i)), false);
            gens.add(w);
        }
        return gens;
    }


    /**
     * Get the Element for a.
     * @param a long
     * @return element corresponding to a.
     */
    public Word fromInteger(long a) {
        throw new UnsupportedOperationException("not implemented for WordFactory");
    }


    /**
     * Get the Element for a.
     * @param a java.math.BigInteger.
     * @return element corresponding to a.
     */
    public Word fromInteger(BigInteger a) {
        throw new UnsupportedOperationException("not implemented for WordFactory");
    }


    /**
     * Get the Element for an ExpVector.
     * @param e ExpVector.
     * @return element corresponding to e.
     */
    public Word valueOf(ExpVector e) {
        Word w = ONE;
        List<Word> gens = generators();
        int n = alphabet.length();
        int m = e.length();
        if (m > n) {
            throw new IllegalArgumentException("alphabet to short for exponent " + e + ", alpahbet = "
                            + alphabet);
        }
        for (int i = 0; i < m; i++) {
            int x = (int) e.getVal(m - i - 1);
            Word y = gens.get(i);
            Word u = ONE;
            for (int j = 0; j < x; j++) {
                u = u.multiply(y);
            }
            w = w.multiply(u);
        }
        return w;
    }


    /**
     * Get the element from an other word.
     * @param w other word.
     * @return w in this word factory.
     */
    public Word valueOf(Word w) {
        String s = w.toString();
        return parse(s);
    }

    
    /**
     * IndexOf for letter in alphabet.
     * @param s letter character.
     * @return index of s in the alphabet, or -1 if s is not contained in the alphabet.
     */
    public int indexOf(char s) {
        return alphabet.indexOf(s);
    }


    /**
     * Generate a random Element with size less equal to n.
     * @param n
     * @return a random element.
     */
    public Word random(int n) {
        return random(n, random);
    }


    /**
     * Generate a random Element with size less equal to n.
     * @param n
     * @param random is a source for random bits.
     * @return a random element.
     */
    public Word random(int n, Random random) {
        StringBuffer sb = new StringBuffer();
        int len = alphabet.length();
        for (int i = 0; i < n; i++) {
            int r = Math.abs(random.nextInt() % len);
            sb.append(alphabet.charAt(r));
        }
        return new Word(this, sb.toString(), false);
    }


    /**
     * Parse from String.
     * @param s String.
     * @return a Element corresponding to s.
     */
    public Word parse(String s) {
        String st = clean(s);
        String regex;
        if (translation == null) {
            regex = "[" + alphabet + " ]*";
        } else {
            regex = "[" + concat(translation) + " ]*";
        }
        if (!st.matches(regex)) {
            throw new IllegalArgumentException("word '" + st + "' contains letters not from: " + alphabet
                            + " or from " + concat(translation));
        }
        // now only alphabet or translation chars are contained in st
        return new Word(this, st, true);
    }


    /**
     * Parse from Reader. White space is delimiter for word.
     * @param r Reader.
     * @return the next Element found on r.
     */
    public Word parse(Reader r) {
        return parse(StringUtil.nextString(r));
    }


    /**
     * Test if the alphabet of w is a subalphabet of this.
     * @param w other word factory to test.
     * @return true, if w is a subalphabet of this, else false.
     */
    public boolean isSubFactory(WordFactory w) {
        if (w == null) {
            throw new IllegalArgumentException("w may not be null");
        }
        String s = w.toString().replace(",", " ");
        Word c = null;
        try {
            c = parse(s);
        } catch (IllegalArgumentException ignored) {
            // ignore
        }
        //System.out.println("c: " + c + ", s = " + s);
        if (c != null) {
            return true;
        }
        return false;
    }


    /**
     * Contract word to this word factory.
     * <code>this.isSubFactory(w.mono)</code> must be true, otherwise null is returned.
     * @param w other word to contract.
     * @return w with this factory, or null if not contractable.
     */
    public Word contract(Word w) {
        if (w == null) {
            throw new IllegalArgumentException("w may not be null");
        }
        Word c = null;
        try {
            c = parse(w.toString());
        } catch (IllegalArgumentException ignored) {
            // ignore
        }
        return c;
    }

    
    /**
     * Get the descending order comparator. Sorts the highest terms first.
     * @return horder.
     */
    public WordComparator getDescendComparator() {
        return horder;
    }


    /**
     * Get the ascending order comparator. Sorts the lowest terms first.
     * @return lorder.
     */
    public WordComparator getAscendComparator() {
        return lorder;
    }


    /**
     * Prepare parse from String.
     * @param s String.
     * @return a Element corresponding to s.
     */
    public static String cleanSpace(String s) {
        String st = s.trim();
        st = st.replaceAll("\\*", "");
        st = st.replaceAll("\\s", "");
        st = st.replaceAll("\\(", "");
        st = st.replaceAll("\\)", "");
        st = st.replaceAll("\\\"", "");
        return st;
    }


    /**
     * Prepare parse from String.
     * @param s String.
     * @return a Element corresponding to s.
     */
    public static String clean(String s) {
        String st = s.trim();
        st = st.replaceAll("\\*", " ");
        //st = st.replaceAll("\\s", "");
        st = st.replaceAll("\\(", "");
        st = st.replaceAll("\\)", "");
        st = st.replaceAll("\\\"", "");
        return st;
    }


    /**
     * Prepare parse from String array.
     * @param v String array.
     * @return an array of cleaned strings.
     */
    public static String[] cleanAll(String[] v) {
        String[] t = new String[v.length];
        for (int i = 0; i < v.length; i++) {
            t[i] = cleanSpace(v[i]);
            if (t[i].length() == 0) {
                logger.error("empty v[i]: '" + v[i] + "'");
            }
            //System.out.println("clean all: " + v[i] + " --> " + t[i]);
        }
        return t;
    }


    /**
     * Concat variable names.
     * @param v an array of strings.
     * @return the concatination of the strings in v.
     */
    public static String concat(String[] v) {
        StringBuffer s = new StringBuffer();
        if (v == null) {
            return s.toString();
        }
        for (int i = 0; i < v.length; i++) {
            s.append(v[i]);
        }
        return s.toString();
    }


    /**
     * Trim all variable names.
     * @param v an array of strings.
     * @return an array of strings with all elements trimmed.
     */
    public static String[] trimAll(String[] v) {
        String[] t = new String[v.length];
        for (int i = 0; i < v.length; i++) {
            t[i] = v[i].trim();
            if (t[i].length() == 0) {
                logger.error("empty v[i]: '" + v[i] + "'");
            }
        }
        return t;
    }


    /**
     * IndexOf for String array.
     * @param v an array of strings.
     * @param s string.
     * @return index of s in v, or -1 if s is not contained in v.
     */
    public static int indexOf(String[] v, String s) {
        for (int i = 0; i < v.length; i++) {
            if (s.equals(v[i])) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Test if all variables are single letters.
     * @param v an array of strings.
     * @return true, if all variables have length 1, else false.
     */
    public static boolean isSingleLetters(String[] v) {
        for (int i = 0; i < v.length; i++) {
            if (v[i].length() != 1) {
                return false;
            }
        }
        return true;
    }


    /**
     * Translate variable names.
     * @param v an array of strings.
     * @return the translated string of v with respect to t.
     */
    public String translate(String[] v) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < v.length; i++) {
            String a = v[i];
            int k = indexOf(translation, a);
            if (k < 0) {
                System.out.println("t = " + Arrays.toString(translation));
                System.out.println("v = " + Arrays.toString(v));
                logger.error("v[i] not found in t: " + a);
                //continue;
                throw new IllegalArgumentException("v[i] not found in t: " + a);
            }
            s.append(transRef.charAt(k));
        }
        return s.toString();
    }


    /**
     * Translate variable name.
     * @param c internal char.
     * @return the extenal translated string.
     */
    public String transVar(char c) {
        int k = alphabet.indexOf(c);
        return translation[k];
    }

}
