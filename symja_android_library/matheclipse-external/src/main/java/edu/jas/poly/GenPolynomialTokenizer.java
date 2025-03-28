/*
 * $Id$
 */

package edu.jas.poly;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.jas.arith.BigComplex;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigOctonion;
import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigQuaternionRing;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModIntRing;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLongRing;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * GenPolynomial Tokenizer. Used to read rational polynomials and lists of
 * polynomials from input streams. Arbitrary polynomial rings and coefficient
 * rings can be read with RingFactoryTokenizer. <b>Note:</b> Can no more read
 * QuotientRing since end of 2010, revision 3441. Quotient coefficients and
 * others can still be read if the respective factory is provided via the
 * constructor.
 * @see edu.jas.application.RingFactoryTokenizer
 * @author Heinz Kredel
 */
public class GenPolynomialTokenizer {


    private static final Logger logger = LogManager.getLogger(GenPolynomialTokenizer.class);


    private static final boolean debug = logger.isDebugEnabled();


    private String[] vars;


    private int nvars = 1;


    private TermOrder tord;


    private RelationTable table;


    private final StreamTokenizer tok;


    private final Reader reader;


    private RingFactory fac;


    private static enum coeffType {
        BigRat, BigInt, ModInt, BigC, BigQ, BigO, BigD, ANrat, ANmod, IntFunc
    };


    private coeffType parsedCoeff = coeffType.BigRat;


    private GenPolynomialRing pfac;


    private static enum polyType {
        PolBigRat, PolBigInt, PolModInt, PolBigC, PolBigD, PolBigQ, PolBigO, PolANrat, PolANmod, PolIntFunc
    };


    @SuppressWarnings("unused")
    private polyType parsedPoly = polyType.PolBigRat;


    private GenSolvablePolynomialRing spfac;


    /**
     * No-args constructor reads from System.in.
     */
    public GenPolynomialTokenizer() {
      this(new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)));
    }


    /**
     * Constructor with Ring and Reader.
     * @param rf ring factory.
     * @param r reader stream.
     */
    public GenPolynomialTokenizer(GenPolynomialRing rf, Reader r) {
        this(r);
        if (rf == null) {
            return;
        }
        if (rf instanceof GenSolvablePolynomialRing) {
            pfac = rf;
            spfac = (GenSolvablePolynomialRing) rf;
        } else {
            pfac = rf;
            spfac = null;
        }
        fac = rf.coFac;
        vars = rf.vars;
        if (vars != null) {
            nvars = vars.length;
        }
        tord = rf.tord;
        // relation table
        if (spfac != null) {
            table = spfac.table;
        } else {
            table = null;
        }
        if (!pfac.getZERO().isZERO()) { // happened since May 5 2022
            // Name        : java-11-openjdk-headless
            // Version     : 11.0.15.0
            // Release     : 150000.3.80.1
            //System.out.println("thread@tokenizer: " + Thread.currentThread());
            throw new RuntimeException("zero not 0: " + pfac.getZERO() + " " + pfac.getZERO().val);
        }
    }


    /**
     * Constructor with Reader.
     * @param r reader stream.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomialTokenizer(Reader r) {
        vars = null;
        tord = new TermOrder();
        nvars = 1;
        fac = new BigRational(1);

        pfac = new GenPolynomialRing<BigRational>(fac, nvars, tord, vars);
        spfac = new GenSolvablePolynomialRing<BigRational>(fac, nvars, tord, vars);

        reader = r;
        tok = new StreamTokenizer(reader);
        tok.resetSyntax();
        // tok.eolIsSignificant(true); no more
        tok.eolIsSignificant(false);
        tok.wordChars('0', '9');
        tok.wordChars('a', 'z');
        tok.wordChars('A', 'Z');
        tok.wordChars('_', '_'); // for subscripts x_i
        tok.wordChars('/', '/'); // wg. rational numbers
        tok.wordChars('.', '.'); // wg. floats
        tok.wordChars('~', '~'); // wg. quaternions
        tok.wordChars(128 + 32, 255);
        tok.whitespaceChars(0, ' ');
        tok.commentChar('#');
        tok.quoteChar('"');
        tok.quoteChar('\'');
        //tok.slashStarComments(true); does not work

    }


    /**
     * Initialize coefficient and polynomial factories.
     * @param rf ring factory.
     * @param ct coefficient type.
     */
    @SuppressWarnings("unchecked")
    public void initFactory(RingFactory rf, coeffType ct) {
        fac = rf;
        parsedCoeff = ct;

        switch (ct) {
        case BigRat:
            pfac = new GenPolynomialRing<BigRational>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigRat;
            break;
        case BigInt:
            pfac = new GenPolynomialRing<BigInteger>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigInt;
            break;
        case ModInt:
            pfac = new GenPolynomialRing<ModInteger>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolModInt;
            break;
        case BigC:
            pfac = new GenPolynomialRing<BigComplex>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigC;
            break;
        case BigQ:
            pfac = new GenPolynomialRing<BigQuaternion>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigQ;
            break;
        case BigO:
            pfac = new GenPolynomialRing<BigOctonion>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigO;
            break;
        case BigD:
            pfac = new GenPolynomialRing<BigDecimal>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigD;
            break;
        case IntFunc:
            pfac = new GenPolynomialRing<GenPolynomial<BigRational>>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolIntFunc;
            break;
        default:
            pfac = new GenPolynomialRing<BigRational>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigRat;
        }
    }


    /**
     * Initialize coefficient and solvable polynomial factories.
     * @param rf ring factory.
     * @param ct coefficient type.
     */
    @SuppressWarnings("unchecked")
    public void initSolvableFactory(RingFactory rf, coeffType ct) {
        fac = rf;
        parsedCoeff = ct;

        switch (ct) {
        case BigRat:
            spfac = new GenSolvablePolynomialRing<BigRational>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigRat;
            break;
        case BigInt:
            spfac = new GenSolvablePolynomialRing<BigInteger>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigInt;
            break;
        case ModInt:
            spfac = new GenSolvablePolynomialRing<ModInteger>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolModInt;
            break;
        case BigC:
            spfac = new GenSolvablePolynomialRing<BigComplex>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigC;
            break;
        case BigQ:
            spfac = new GenSolvablePolynomialRing<BigQuaternion>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigQ;
            break;
        case BigO:
            spfac = new GenSolvablePolynomialRing<BigOctonion>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigO;
            break;
        case BigD:
            spfac = new GenSolvablePolynomialRing<BigDecimal>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigD;
            break;
        case IntFunc:
            spfac = new GenSolvablePolynomialRing<GenPolynomial<BigRational>>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolIntFunc;
            break;
        default:
            spfac = new GenSolvablePolynomialRing<BigRational>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigRat;
        }
    }


    /**
     * Parsing method for GenPolynomial. Syntax depends also on the syntax of
     * the coefficients, as the respective parser is used. Basic term/monomial
     * syntax:
     * 
     * <pre>
     ... coefficient variable**exponent ... variable^exponent + ... - ....
     * </pre>
     * 
     * Juxtaposition means multiplication <code>*</code>. Then terms/monomials
     * can be added or subtracted <code>+, -</code> and grouped by parenthesis
     * <code>()</code>. There are some heuristics to detect when a coefficient
     * should be parsed. To force parsing of a coefficient enclose it in braces
     * <code>{}</code>.
     * @return the next polynomial.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial nextPolynomial() throws IOException {
        logger.debug("torder = {}", tord);
        GenPolynomial a = pfac.getZERO();
        GenPolynomial a1 = pfac.getONE();
        ExpVector leer = pfac.evzero;

        if (debug) {
            logger.debug("pfac = {}", pfac.toScript());
            logger.debug("a = {}", a);
            logger.debug("a1 = {}", a1);
        }
        GenPolynomial b = a1;
        GenPolynomial c;
        int tt; //, oldtt;
        //String rat = "";
        char first;
        RingElem r;
        ExpVector e;
        int ix;
        long ie;
        while (true) {
            // next input. determine next action
            tt = tok.nextToken();
            //System.out.println("while tt = " + tok);
            logger.debug("while tt = {}", tok);
            if (tt == StreamTokenizer.TT_EOF)
                break;
            switch (tt) {
            case ')':
            case ',':
                return a; // do not change or remove
            case '-':
                b = b.negate();
            case '+':
            case '*':
                tt = tok.nextToken();
                break;
            default: // skip
            }
            // read coefficient, monic monomial and polynomial
            if (tt == StreamTokenizer.TT_EOF)
                break;
            switch (tt) {
            // case '_': removed 
            case '}':
                throw new InvalidExpressionException("mismatch of braces after " + a + ", error at " + b);
            case '{': // recursion
                StringBuffer rf = new StringBuffer();
                int level = 0;
                do {
                    tt = tok.nextToken();
                    //System.out.println("token { = " + ((char)tt) + ", " + tt + ", level = " + level);
                    if (tt == StreamTokenizer.TT_EOF) {
                        throw new InvalidExpressionException(
                                        "mismatch of braces after " + a + ", error at " + b);
                    }
                    if (tt == '{') {
                        level++;
                    }
                    if (tt == '}') {
                        level--;
                        if (level < 0) {
                            continue; // skip last closing brace 
                        }
                    }
                    if (tok.sval != null) {
                        if (rf.length() > 0 && rf.charAt(rf.length() - 1) != '.') {
                            rf.append(" ");
                        }
                        rf.append(tok.sval); // " " + 
                    } else {
                        rf.append((char) tt);
                    }
                } while (level >= 0);
                //System.out.println("coeff{} = " + rf.toString() );
                try {
                    r = (RingElem) fac.parse(rf.toString());
                } catch (NumberFormatException re) {
                    throw new InvalidExpressionException("not a number :" + rf + ": " + fac, re);
                }
                logger.debug("coeff {}", r);
                ie = nextExponent();
                logger.debug("ie {}", ie);
                r = (RingElem) r.power(ie);
                logger.debug("coeff^ie {}", r);
                b = b.multiply(r, leer);

                tt = tok.nextToken();
                logger.debug("tt,digit = {}", tok);
                //no break;
                break;

            //case '.': // eventually a float
            //System.out.println("start . = " + reader);
            //throw new InvalidExpressionException("float must start with a digit ");

            case StreamTokenizer.TT_WORD:
                //System.out.println("TT_WORD: " + tok.sval);
                if (tok.sval == null || tok.sval.length() == 0)
                    break;
                // read coefficient
                first = tok.sval.charAt(0);
                if (digit(first) || first == '/' || first == '.' || first == '~') {
                    //System.out.println("coeff 0 = " + tok.sval );
                    StringBuffer df = new StringBuffer();
                    df.append(tok.sval);
                    if (tok.sval.length() > 1 && digit(tok.sval.charAt(1))) {
                        //System.out.println("start / or . = " + tok.sval);
                        if (first == '/') { // let x/2 be x 1/2
                            df.insert(0, "1");
                        }
                        if (first == '.') { // let x.2 be x 0.2
                            df.insert(0, "0");
                        }
                    }
                    if (tok.sval.charAt(tok.sval.length() - 1) == 'i') { // complex number
                        tt = tok.nextToken();
                        logger.debug("tt,im = {}", tok);
                        if (tok.sval != null || tt == '-') {
                            if (tok.sval != null) {
                                df.append(tok.sval);
                            } else {
                                df.append("-");
                            }
                            if (tt == '-') {
                                tt = tok.nextToken(); // todo: decimal number
                                if (tok.sval != null && digit(tok.sval.charAt(0))) {
                                    df.append(tok.sval);
                                } else {
                                    tok.pushBack();
                                }
                            }
                        } else {
                            tok.pushBack();
                        }
                    }
                    if (tok.sval.charAt(tok.sval.length() - 1) == 'j') { // quaternion number
                        tt = tok.nextToken();
                        logger.debug("tt,jm = {}", tok);
                        if (tok.sval != null || tt == '-') {
                            if (tok.sval != null) {
                                df.append(tok.sval);
                            } else {
                                df.append("-");
                            }
                            if (tt == '-') {
                                tt = tok.nextToken(); // todo: decimal number
                                if (tok.sval != null && digit(tok.sval.charAt(0))) {
                                    df.append(tok.sval);
                                } else {
                                    tok.pushBack();
                                }
                            }
                        } else {
                            tok.pushBack();
                        }
                    }
                    if (tok.sval.charAt(tok.sval.length() - 1) == 'k') { // quaternion number
                        tt = tok.nextToken();
                        logger.debug("tt,km = {}", tok);
                        if (tok.sval != null || tt == '-') {
                            if (tok.sval != null) {
                                df.append(tok.sval);
                            } else {
                                df.append("-");
                            }
                            if (tt == '-') {
                                tt = tok.nextToken(); // todo: decimal number
                                if (tok.sval != null && digit(tok.sval.charAt(0))) {
                                    df.append(tok.sval);
                                } else {
                                    tok.pushBack();
                                }
                            }
                        } else {
                            tok.pushBack();
                        }
                    }
                    tt = tok.nextToken();
                    if (tt == '.') { // decimal number, obsolete by word char?
                        tt = tok.nextToken();
                        logger.debug("tt,dot = {}", tok);
                        if (tok.sval != null) {
                            df.append(".");
                            df.append(tok.sval);
                        } else {
                            tok.pushBack();
                            tok.pushBack();
                        }
                    } else {
                        tok.pushBack();
                    }
                    try {
                        //System.out.println("df = " + df + ", fac = " + fac.getClass());
                        r = (RingElem) fac.parse(df.toString());
                        //System.out.println("r = " + r);
                    } catch (NumberFormatException re) {
                        //System.out.println("re = " + re);
                        throw new InvalidExpressionException("not a number :" + df + ": " + fac, re);
                    }
                    logger.debug("coeff {}", r);
                    //System.out.println("r = " + r.toScriptFactory());
                    ie = nextExponent();
                    logger.debug("ie {}", ie);
                    // r = r^ie;
                    r = (RingElem) r.power(ie);
                    logger.debug("coeff^ie {}", r);
                    b = b.multiply(r, leer);

                    tt = tok.nextToken();
                    logger.debug("tt,digit = {}", tok);
                }
                if (tt == StreamTokenizer.TT_EOF)
                    break;
                if (tok.sval == null)
                    break;
                // read monomial or recursion 
                first = tok.sval.charAt(0);
                if (letter(first)) {
                    ix = leer.indexVar(tok.sval, vars); //indexVar( tok.sval );
                    if (ix < 0) { // not found
                        try {
                            r = (RingElem) fac.parse(tok.sval);
                        } catch (NumberFormatException re) {
                            throw new InvalidExpressionException("recursively unknown variable " + tok.sval);
                        }
                        logger.info("coeff {}", r);
                        //if (r.isONE() || r.isZERO()) {
                        //logger.error("Unknown variable {}", tok.sval);
                        //break;
                        //throw new InvalidExpressionException("recursively unknown variable " + tok.sval);
                        //}
                        ie = nextExponent();
                        //  System.out.println("ie: " + ie);
                        r = (RingElem) r.power(ie);
                        b = b.multiply(r);
                    } else { // found
                        //  System.out.println("ix: " + ix);
                        ie = nextExponent();
                        //  System.out.println("ie: " + ie);
                        e = ExpVector.create(vars.length, ix, ie);
                        b = b.multiply(e);
                    }
                    tt = tok.nextToken();
                    logger.debug("tt,letter = {}", tok);
                }
                break;

            case '(':
                c = nextPolynomial();
                logger.debug("factor {}", c);
                ie = nextExponent();
                logger.debug("ie {}", ie);
                c = (GenPolynomial) c.power(ie);
                logger.debug("factor^ie {}", c);
                b = b.multiply(c);

                tt = tok.nextToken();
                logger.debug("tt,digit = {}", tok);
                //no break;
                break;

            default: //skip 
            }
            if (tt == StreamTokenizer.TT_EOF)
                break;
            // complete polynomial
            tok.pushBack();
            switch (tt) {
            case '-':
            case '+':
            case ')':
            case ',':
                logger.debug("b, = {}", b);
                a = a.sum(b);
                b = a1;
                break;
            case '*':
                logger.debug("b, = {}", b);
                //a = a.sum(b); 
                //b = a1;
                break;
            case '\n':
                tt = tok.nextToken();
                logger.debug("tt,nl = {}", tt);
                break;
            default: // skip or finish ?
                logger.debug("default: {}", tok);
            }
        }
        logger.debug("b = {}", b);
        a = a.sum(b);
        logger.debug("a = {}", a);
        // b = a1;
        return a;
    }


    /**
     * Parsing method for exponent (of variable). Syntax:
     * <pre>
     * ^long | **long
     * </pre>
     * 
     * @return the next exponent or 1.
     * @throws IOException
     */
    public long nextExponent() throws IOException {
        long e = 1;
        char first;
        int tt;
        tt = tok.nextToken();
        if (tt == '^') {
            logger.debug("exponent ^");
            tt = tok.nextToken();
            if (tok.sval != null) {
                first = tok.sval.charAt(0);
                if (digit(first)) {
                    e = Long.parseLong(tok.sval);
                    logger.debug("exponent ^{}", e);
                    return e;
                }
            }
        }
        if (tt == '*') {
            tt = tok.nextToken();
            if (tt == '*') {
                logger.debug("exponent **");
                tt = tok.nextToken();
                if (tok.sval != null) {
                    first = tok.sval.charAt(0);
                    if (digit(first)) {
                        e = Long.parseLong(tok.sval);
                        logger.debug("exponent **{}", e);
                        return e;
                    }
                }
            }
            tok.pushBack();
        }
        tok.pushBack();
        return e;
    }


    /**
     * Parsing method for comments. Syntax:
     * 
     * <pre>
     * (* comment *) | /_* comment *_/
     * </pre>
     * 
     * without <code>_</code>. Unused, as it does not work with this pushBack().
     */
    public String nextComment() throws IOException {
        // syntax: (* comment *) | /* comment */ 
        StringBuffer c = new StringBuffer();
        int tt;
        logger.debug("comment: {}", tok);
        tt = tok.nextToken();
        logger.debug("comment: {}", tok);
        if (tt == '(') {
            tt = tok.nextToken();
            logger.debug("comment: {}", tok);
            if (tt == '*') {
                logger.debug("comment: ");
                while (true) {
                    tt = tok.nextToken();
                    if (tt == '*') {
                        tt = tok.nextToken();
                        if (tt == ')') {
                            return c.toString();
                        }
                        tok.pushBack();
                    }
                    c.append(tok.sval);
                }
            }
            tok.pushBack();
            logger.debug("comment: {}", tok);
        }
        tok.pushBack();
        logger.debug("comment: {}", tok);
        return c.toString();
    }


    /**
     * Parsing method for variable list. Syntax:
     * 
     * <pre>
     * (a, b c, de)
     * </pre>
     * 
     * gives <code>[ "a", "b", "c", "de" ]</code>
     * @return the next variable list.
     * @throws IOException
     */
    public String[] nextVariableList() throws IOException {
        List<String> l = new ArrayList<String>();
        int tt;
        tt = tok.nextToken();
        //System.out.println("vList tok = " + tok);
        if (tt == '(' || tt == '{') {
            logger.debug("variable list");
            tt = tok.nextToken();
            while (true) {
                if (tt == StreamTokenizer.TT_EOF)
                    break;
                if (tt == ')' || tt == '}')
                    break;
                if (tt == StreamTokenizer.TT_WORD) {
                    //System.out.println("TT_WORD: " + tok.sval);
                    l.add(tok.sval);
                }
                tt = tok.nextToken();
            }
        } else {
            tok.pushBack();
        }
        Object[] ol = l.toArray();
        String[] v = new String[ol.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = (String) ol[i];
        }
        return v;
    }


    /**
     * Parsing method for coefficient ring. Syntax:
     * 
     * <pre>
     * Rat | Q | Int | Z | Mod modul | Complex | C | D | Quat | AN[ (var) ( poly ) ] | AN[ modul (var) ( poly ) ] | IntFunc (var_list)
     * </pre>
     * 
     * @return the next coefficient factory.
     * @throws IOException
     */
    @SuppressWarnings({ "unchecked", "cast" })
    public RingFactory nextCoefficientRing() throws IOException {
        RingFactory coeff = null;
        coeffType ct = null;
        int tt;
        tt = tok.nextToken();
        if (tok.sval != null) {
            if (tok.sval.equalsIgnoreCase("Q")) {
                coeff = new BigRational(0);
                ct = coeffType.BigRat;
            } else if (tok.sval.equalsIgnoreCase("Rat")) {
                coeff = new BigRational(0);
                ct = coeffType.BigRat;
            } else if (tok.sval.equalsIgnoreCase("D")) {
                coeff = new BigDecimal(0);
                ct = coeffType.BigD;
            } else if (tok.sval.equalsIgnoreCase("Z")) {
                coeff = new BigInteger(0);
                ct = coeffType.BigInt;
            } else if (tok.sval.equalsIgnoreCase("Int")) {
                coeff = new BigInteger(0);
                ct = coeffType.BigInt;
            } else if (tok.sval.equalsIgnoreCase("C")) {
                coeff = new BigComplex(0);
                ct = coeffType.BigC;
            } else if (tok.sval.equalsIgnoreCase("Complex")) {
                coeff = new BigComplex(0);
                ct = coeffType.BigC;
            } else if (tok.sval.equalsIgnoreCase("Quat")) {
                logger.warn("parse of quaternion coefficients may fail for negative components (use ~ for -)");
                coeff = new BigQuaternionRing();
                ct = coeffType.BigQ;
            } else if (tok.sval.equalsIgnoreCase("Oct")) {
                logger.warn("parse of octonion coefficients may fail for negative components (use ~ for -)");
                coeff = new BigOctonion(new BigQuaternionRing());
                ct = coeffType.BigO;
            } else if (tok.sval.equalsIgnoreCase("Mod")) {
                tt = tok.nextToken();
                boolean openb = false;
                if (tt == '[') { // optional
                    openb = true;
                    tt = tok.nextToken();
                }
                if (tok.sval != null && tok.sval.length() > 0) {
                    if (digit(tok.sval.charAt(0))) {
                        BigInteger mo = new BigInteger(tok.sval);
                        BigInteger lm = new BigInteger(ModLongRing.MAX_LONG); //wrong: Long.MAX_VALUE);
                        if (mo.compareTo(lm) < 0) {
                            if (mo.compareTo(new BigInteger(ModIntRing.MAX_INT)) < 0) {
                                coeff = new ModIntRing(mo.getVal());
                            } else {
                                coeff = new ModLongRing(mo.getVal());
                            }
                        } else {
                            coeff = new ModIntegerRing(mo.getVal());
                        }
                        //System.out.println("coeff = " + coeff + " :: " + coeff.getClass());
                        ct = coeffType.ModInt;
                    } else {
                        tok.pushBack();
                    }
                } else {
                    tok.pushBack();
                }
                if (tt == ']' && openb) { // optional
                    tt = tok.nextToken();
                }
            } else if (tok.sval.equalsIgnoreCase("RatFunc") || tok.sval.equalsIgnoreCase("ModFunc")) {
                //logger.error("RatFunc and ModFunc can no more be read, see edu.jas.application.RingFactoryTokenizer.");
                throw new InvalidExpressionException(
                                "RatFunc and ModFunc can no more be read, see edu.jas.application.RingFactoryTokenizer.");
            } else if (tok.sval.equalsIgnoreCase("IntFunc")) {
                String[] rfv = nextVariableList();
                //System.out.println("rfv = " + rfv.length + " " + rfv[0]);
                int vr = rfv.length;
                BigRational bi = new BigRational();
                TermOrder to = new TermOrder(TermOrder.INVLEX);
                GenPolynomialRing<BigRational> pcf = new GenPolynomialRing<BigRational>(bi, vr, to, rfv);
                coeff = pcf;
                ct = coeffType.IntFunc;
            } else if (tok.sval.equalsIgnoreCase("AN")) {
                tt = tok.nextToken();
                if (tt == '[') {
                    tt = tok.nextToken();
                    RingFactory tcfac = new ModIntegerRing("19");
                    if (tok.sval != null && tok.sval.length() > 0) {
                        if (digit(tok.sval.charAt(0))) {
                            tcfac = new ModIntegerRing(tok.sval);
                        } else {
                            tcfac = new BigRational();
                            tok.pushBack();
                        }
                    } else {
                        tcfac = new BigRational();
                        tok.pushBack();
                    }
                    String[] anv = nextVariableList();
                    //System.out.println("anv = " + anv.length + " " + anv[0]);
                    int vs = anv.length;
                    if (vs != 1) {
                        throw new InvalidExpressionException(
                                        "AlgebraicNumber only for univariate polynomials "
                                                        + Arrays.toString(anv));
                    }
                    String[] ovars = vars;
                    vars = anv;
                    GenPolynomialRing tpfac = pfac;
                    RingFactory tfac = fac;
                    fac = tcfac;
                    // pfac and fac used in nextPolynomial()
                    if (tcfac instanceof ModIntegerRing) {
                        pfac = new GenPolynomialRing<ModInteger>(tcfac, vs, new TermOrder(), anv);
                    } else {
                        pfac = new GenPolynomialRing<BigRational>(tcfac, vs, new TermOrder(), anv);
                    }
                    logger.debug("pfac = {}", pfac);
                    tt = tok.nextToken();
                    GenPolynomial mod;
                    if (tt == '(') {
                        mod = nextPolynomial();
                        tt = tok.nextToken();
                        if (tok.ttype != ')')
                            tok.pushBack();
                    } else {
                        tok.pushBack();
                        mod = nextPolynomial();
                    }
                    logger.debug("mod = {}", mod);
                    pfac = tpfac;
                    fac = tfac;
                    vars = ovars;
                    if (tcfac instanceof ModIntegerRing) {
                        GenPolynomial<ModInteger> gfmod;
                        gfmod = mod;
                        coeff = new AlgebraicNumberRing<ModInteger>(gfmod);
                        ct = coeffType.ANmod;
                    } else {
                        GenPolynomial<BigRational> anmod;
                        anmod = mod;
                        coeff = new AlgebraicNumberRing<BigRational>(anmod);
                        ct = coeffType.ANrat;
                    }
                    logger.debug("coeff = {}", coeff);
                    tt = tok.nextToken();
                    if (tt == ']') {
                        //ok, no nextToken();
                    } else {
                        tok.pushBack();
                    }
                } else {
                    tok.pushBack();
                }
            }
        }
        if (coeff == null) {
            tok.pushBack();
            coeff = new BigRational();
            ct = coeffType.BigRat;
        }
        parsedCoeff = ct;
        return coeff;
    }


    /**
     * Parsing method for weight list. Syntax:
     * 
     * <pre>
     * (w1, w2, w3, ..., wn)
     * </pre>
     * 
     * @return the next weight list.
     * @throws IOException
     */
    public long[] nextWeightList() throws IOException {
        List<Long> l = new ArrayList<Long>();
        long e;
        char first;
        int tt;
        tt = tok.nextToken();
        if (tt == '(') {
            logger.debug("weight list");
            tt = tok.nextToken();
            while (true) {
                if (tt == StreamTokenizer.TT_EOF)
                    break;
                if (tt == ')')
                    break;
                if (tok.sval != null) {
                    first = tok.sval.charAt(0);
                    if (digit(first)) {
                        e = Long.parseLong(tok.sval);
                        l.add(Long.valueOf(e));
                        //System.out.println("w: " + e);
                    }
                }
                tt = tok.nextToken(); // also comma
            }
        } else {
            tok.pushBack();
        }
        Long[] ol = new Long[1];
        ol = l.toArray(ol);
        long[] w = new long[ol.length];
        for (int i = 0; i < w.length; i++) {
            w[i] = ol[ol.length - i - 1].longValue();
        }
        return w;
    }


    /**
     * Parsing method for weight array. Syntax:
     * 
     * <pre>
     * ( (w11, ...,w1n), ..., (wm1, ..., wmn) )
     * </pre>
     * 
     * @return the next weight array.
     * @throws IOException
     */
    public long[][] nextWeightArray() throws IOException {
        List<long[]> l = new ArrayList<long[]>();
        long[] e;
        char first;
        int tt;
        tt = tok.nextToken();
        if (tt == '(') {
            logger.debug("weight array");
            tt = tok.nextToken();
            while (true) {
                if (tt == StreamTokenizer.TT_EOF)
                    break;
                if (tt == ')')
                    break;
                if (tt == '(') {
                    tok.pushBack();
                    e = nextWeightList();
                    l.add(e);
                    //System.out.println("wa: " + e);
                } else if (tok.sval != null) {
                    first = tok.sval.charAt(0);
                    if (digit(first)) {
                        tok.pushBack();
                        tok.pushBack();
                        e = nextWeightList();
                        l.add(e);
                        break;
                        //System.out.println("w: " + e);
                    }
                }
                tt = tok.nextToken(); // also comma
            }
        } else {
            tok.pushBack();
        }
        Object[] ol = l.toArray();
        long[][] w = new long[ol.length][];
        for (int i = 0; i < w.length; i++) {
            w[i] = (long[]) ol[i];
        }
        return w;
    }


    /**
     * Parsing method for split index. Syntax:
     * 
     * <pre>
     * |i|
     * </pre>
     * 
     * @return the next split index.
     * @throws IOException
     */
    public int nextSplitIndex() throws IOException {
        int e = -1; // =unknown
        int e0 = -1; // =unknown
        char first;
        int tt;
        tt = tok.nextToken();
        if (tt == '|') {
            logger.debug("split index");
            tt = tok.nextToken();
            if (tt == StreamTokenizer.TT_EOF) {
                return e;
            }
            if (tok.sval != null) {
                first = tok.sval.charAt(0);
                if (digit(first)) {
                    e = Integer.parseInt(tok.sval);
                    //System.out.println("w: " + i);
                }
                tt = tok.nextToken();
                if (tt != '|') {
                    tok.pushBack();
                }
            }
        } else if (tt == '[') {
            logger.debug("split index");
            tt = tok.nextToken();
            if (tt == StreamTokenizer.TT_EOF) {
                return e;
            }
            if (tok.sval != null) {
                first = tok.sval.charAt(0);
                if (digit(first)) {
                    e0 = Integer.parseInt(tok.sval);
                    //System.out.println("w: " + i);
                }
                tt = tok.nextToken();
                if (tt == ',') {
                    tt = tok.nextToken();
                    if (tt == StreamTokenizer.TT_EOF) {
                        return e0;
                    }
                    if (tok.sval != null) {
                        first = tok.sval.charAt(0);
                        if (digit(first)) {
                            e = Integer.parseInt(tok.sval);
                            //System.out.println("w: " + i);
                        }
                    }
                    if (tt != ']') {
                        tok.pushBack();
                    }
                }
            }
        } else {
            tok.pushBack();
        }
        return e;
    }


    /**
     * Parsing method for term order name. Syntax:
     * 
     * <pre>
     * L | IL | LEX | G | IG | GRLEX | W(weights) | '|'split index'|'
     * </pre>
     * 
     * @return the next term order.
     * @throws IOException
     */
    public TermOrder nextTermOrder() throws IOException {
        int evord = TermOrder.DEFAULT_EVORD;
        int tt;
        tt = tok.nextToken();
        if (tt == StreamTokenizer.TT_EOF) { /* nop */
        } else if (tt == StreamTokenizer.TT_WORD) {
            // System.out.println("TT_WORD: " + tok.sval);
            if (tok.sval != null) {
                if (tok.sval.equalsIgnoreCase("L")) {
                    evord = TermOrder.INVLEX;
                } else if (tok.sval.equalsIgnoreCase("IL")) {
                    evord = TermOrder.INVLEX;
                } else if (tok.sval.equalsIgnoreCase("INVLEX")) {
                    evord = TermOrder.INVLEX;
                } else if (tok.sval.equalsIgnoreCase("LEX")) {
                    evord = TermOrder.LEX;
                } else if (tok.sval.equalsIgnoreCase("G")) {
                    evord = TermOrder.IGRLEX;
                } else if (tok.sval.equalsIgnoreCase("IG")) {
                    evord = TermOrder.IGRLEX;
                } else if (tok.sval.equalsIgnoreCase("IGRLEX")) {
                    evord = TermOrder.IGRLEX;
                } else if (tok.sval.equalsIgnoreCase("GRLEX")) {
                    evord = TermOrder.GRLEX;
                } else if (tok.sval.equalsIgnoreCase("REVITDG")) {
                    evord = TermOrder.REVITDG;
                } else if (tok.sval.equalsIgnoreCase("REVILEX")) {
                    evord = TermOrder.REVILEX;
                } else if (tok.sval.equalsIgnoreCase("W")) {
                    long[][] w = nextWeightArray();
                    return new TermOrder(w);
                }
            }
        } else {
            tok.pushBack();
        }
        int s = nextSplitIndex();
        if (s <= 0) {
            return new TermOrder(evord);
        }
        return new TermOrder(evord, evord, nvars, s);
    }


    /**
     * Parsing method for polynomial list. Syntax:
     * 
     * <pre>
     * ( p1, p2, p3, ..., pn )
     * </pre>
     * 
     * @return the next polynomial list.
     * @throws IOException
     */
    public List<GenPolynomial> nextPolynomialList() throws IOException {
        GenPolynomial a;
        List<GenPolynomial> L = new ArrayList<GenPolynomial>();
        int tt;
        tt = tok.nextToken();
        if (tt == StreamTokenizer.TT_EOF)
            return L;
        if (tt != '(')
            return L;
        logger.debug("polynomial list");
        while (true) {
            tt = tok.nextToken();
            if (tok.ttype == ',')
                continue;
            if (tt == '(') {
                a = nextPolynomial();
                tt = tok.nextToken();
                if (tok.ttype != ')')
                    tok.pushBack();
            } else {
                tok.pushBack();
                a = nextPolynomial();
            }
            logger.info("next pol = {}", a);
            L.add(a);
            if (tok.ttype == StreamTokenizer.TT_EOF)
                break;
            if (tok.ttype == ')')
                break;
        }
        return L;
    }


    /**
     * Parsing method for submodule list. Syntax:
     * 
     * <pre>
     * ( ( p11, p12, p13, ..., p1n ), ..., ( pm1, pm2, pm3, ..., pmn ) )
     * </pre>
     * 
     * @return the next list of polynomial lists.
     * @throws IOException
     */
    public List<List<GenPolynomial>> nextSubModuleList() throws IOException {
        List<List<GenPolynomial>> L = new ArrayList<List<GenPolynomial>>();
        int tt;
        tt = tok.nextToken();
        if (tt == StreamTokenizer.TT_EOF)
            return L;
        if (tt != '(')
            return L;
        logger.debug("module list");
        List<GenPolynomial> v = null;
        while (true) {
            tt = tok.nextToken();
            if (tok.ttype == ',')
                continue;
            if (tok.ttype == ')')
                break;
            if (tok.ttype == StreamTokenizer.TT_EOF)
                break;
            if (tt == '(') {
                tok.pushBack();
                v = nextPolynomialList();
                logger.info("next vect = {}", v);
                L.add(v);
            }
        }
        return L;
    }


    /**
     * Parsing method for solvable polynomial relation table. Syntax:
     * 
     * <pre>
     * ( p_1, p_2, p_3, ..., p_{n+1}, p_{n+2}, p_{n+3} )
     * </pre>
     * 
     * semantics: <code>p_{n+1} * p_{n+2} = p_{n+3}</code>. The next relation
     * table is stored into the solvable polynomial factory.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void nextRelationTable() throws IOException {
        if (spfac == null) {
            return;
        }
        RelationTable table = spfac.table;
        List<GenPolynomial> rels = null;
        GenPolynomial p;
        GenSolvablePolynomial sp;
        int tt;
        tt = tok.nextToken();
        logger.debug("start relation table: {}", tt);
        if (tok.sval != null) {
            if (tok.sval.equalsIgnoreCase("RelationTable")) {
                rels = nextPolynomialList();
            }
        }
        if (rels == null) {
            tok.pushBack();
            return;
        }
        for (Iterator<GenPolynomial> it = rels.iterator(); it.hasNext();) {
            p = it.next();
            ExpVector e = p.leadingExpVector();
            if (it.hasNext()) {
                p = it.next();
                ExpVector f = p.leadingExpVector();
                if (it.hasNext()) {
                    p = it.next();
                    sp = new GenSolvablePolynomial(spfac, p.val);
                    table.update(e, f, sp);
                }
            }
        }
        logger.info("table = {}", table);
        return;
    }


    /**
     * Parsing method for polynomial set. Syntax:
     * 
     * <pre>
     * coeffRing varList termOrderName polyList
     * </pre>
     * 
     * @return the next polynomial set.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public PolynomialList nextPolynomialSet() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = {}", comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = {}", coeff.getClass().getSimpleName());

        vars = nextVariableList();
        logger.info("vars = {}", Arrays.toString(vars));
        if (vars != null) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = {}", tord);
        // check more TOs

        initFactory(coeff, parsedCoeff); // global: nvars, tord, vars
        List<GenPolynomial> s = null;
        s = nextPolynomialList();
        logger.info("s = {}", s);
        // comments += nextComment();
        return new PolynomialList(pfac, s);
    }


    /**
     * Parsing method for module set. Syntax:
     * 
     * <pre>
     * coeffRing varList termOrderName moduleList
     * </pre>
     * 
     * @return the next module set.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public ModuleList nextSubModuleSet() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = {}", comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = {}", coeff.getClass().getSimpleName());

        vars = nextVariableList();
        logger.info("vars = {}", Arrays.toString(vars));
        if (vars != null) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = {}", tord);
        // check more TOs

        initFactory(coeff, parsedCoeff); // global: nvars, tord, vars
        List<List<GenPolynomial>> m = null;
        m = nextSubModuleList();
        logger.info("m = {}", m);
        // comments += nextComment();

        return new ModuleList(pfac, m);
    }


    /**
     * Parsing method for solvable polynomial list. Syntax:
     * 
     * <pre>
     * ( p1, p2, p3, ..., pn )
     * </pre>
     * 
     * @return the next solvable polynomial list.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public List<GenSolvablePolynomial> nextSolvablePolynomialList() throws IOException {
        List<GenPolynomial> s = nextPolynomialList();
        logger.info("s = {}", s);
        // comments += nextComment();

        GenPolynomial p;
        GenSolvablePolynomial ps;
        List<GenSolvablePolynomial> sp = new ArrayList<GenSolvablePolynomial>(s.size());
        for (Iterator<GenPolynomial> it = s.iterator(); it.hasNext();) {
            p = it.next();
            ps = new GenSolvablePolynomial(spfac, p.val);
            //System.out.println("ps = " + ps);
            sp.add(ps);
        }
        return sp;
    }


    /**
     * Parsing method for solvable polynomial. Syntax: same as for polynomial.
     * If the relation table is set-up, then multiplication will mean
     * solvable-multiplication.
     * @return the next polynomial.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenSolvablePolynomial nextSolvablePolynomial() throws IOException {
        GenPolynomial p = nextPolynomial();
        //logger.info("nextPolynomial = {}", p);
        // comments += nextComment();

        GenSolvablePolynomial ps = new GenSolvablePolynomial(spfac, p.val);
        logger.info("nextSolvablePolynomial = {}", ps);
        return ps;
    }


    /**
     * Parsing method for solvable polynomial set. Syntax:
     * 
     * <pre>
     * varList termOrderName relationTable polyList
     * </pre>
     * 
     * @return the next solvable polynomial set.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public PolynomialList nextSolvablePolynomialSet() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = {}", comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = {}", coeff.getClass().getSimpleName());

        vars = nextVariableList();
        logger.info("vars = {}", Arrays.toString(vars));
        if (vars != null) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = {}", tord);
        // check more TOs

        initFactory(coeff, parsedCoeff); // must be because of symmetric read
        initSolvableFactory(coeff, parsedCoeff); // global: nvars, tord, vars
        //System.out.println("pfac = " + pfac);
        //System.out.println("spfac = " + spfac);

        nextRelationTable();
        logger.info("table = {}", table);

        List<GenSolvablePolynomial> s = null;
        s = nextSolvablePolynomialList();
        logger.info("s = {}", s);
        // comments += nextComment();
        return new PolynomialList(spfac, s); // Ordered ?
    }


    /**
     * Parsing method for solvable submodule list. Syntax:
     * 
     * <pre>
     * ( ( p11, p12, p13, ..., p1n ), ..., ( pm1, pm2, pm3, ..., pmn ) )
     * </pre>
     * 
     * @return the next list of solvable polynomial lists.
     * @throws IOException
     */
    public List<List<GenSolvablePolynomial>> nextSolvableSubModuleList() throws IOException {
        List<List<GenSolvablePolynomial>> L = new ArrayList<List<GenSolvablePolynomial>>();
        int tt;
        tt = tok.nextToken();
        if (tt == StreamTokenizer.TT_EOF)
            return L;
        if (tt != '(')
            return L;
        logger.debug("module list");
        List<GenSolvablePolynomial> v = null;
        while (true) {
            tt = tok.nextToken();
            if (tok.ttype == ',')
                continue;
            if (tok.ttype == ')')
                break;
            if (tok.ttype == StreamTokenizer.TT_EOF)
                break;
            if (tt == '(') {
                tok.pushBack();
                v = nextSolvablePolynomialList();
                logger.info("next vect = {}", v);
                L.add(v);
            }
        }
        return L;
    }


    /**
     * Parsing method for solvable module set. Syntax:
     * 
     * <pre>
     * varList termOrderName relationTable moduleList
     * </pre>
     * 
     * @return the next solvable module set.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public ModuleList nextSolvableSubModuleSet() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = {}", comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = {}", coeff.getClass().getSimpleName());

        vars = nextVariableList();
        logger.info("vars = {}", Arrays.toString(vars));
        if (vars != null) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = {}", tord);
        // check more TOs

        initFactory(coeff, parsedCoeff); // must be because of symmetric read
        initSolvableFactory(coeff, parsedCoeff); // global: nvars, tord, vars

        //System.out.println("spfac = " + spfac);

        nextRelationTable();
        logger.info("table = {}", table);

        List<List<GenSolvablePolynomial>> s = null;
        s = nextSolvableSubModuleList();
        logger.info("s = {}", s);
        // comments += nextComment();

        return new OrderedModuleList(spfac, s); // Ordered
    }


    /**
     * Parsing method for word polynomial. Syntax: same as for polynomial.
     * Multiplication will be non commutative.
     * @return the next polynomial.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenWordPolynomial nextWordPolynomial() throws IOException {
        GenWordPolynomialRing wfac = new GenWordPolynomialRing(pfac);
        return nextWordPolynomial(wfac);
    }


    /**
     * Parsing method for word polynomial. Syntax: same as for polynomial.
     * Multiplication will be non commutative.
     * @param wfac word polynomial ring.
     * @return the next polynomial.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenWordPolynomial nextWordPolynomial(GenWordPolynomialRing wfac) throws IOException {
        //logger.info("wfac = {}", wfac);
        WordFactory wf = wfac.alphabet;

        GenWordPolynomial a = wfac.getZERO();
        GenWordPolynomial a1 = wfac.getONE();
        Word leer = wfac.wone;
        RingFactory fac = wfac.coFac;
        if (debug) {
            logger.debug("a = {}", a);
            logger.debug("a1 = {}", a1);
        }
        GenWordPolynomial b = a1;
        GenWordPolynomial c;
        int tt;
        char first;
        RingElem r;
        Word e;
        //int ix;
        long ie;
        while (true) {
            // next input. determine next action
            tt = tok.nextToken();
            //System.out.println("while tt = " + tok);
            logger.debug("while tt = {}", tok);
            if (tt == StreamTokenizer.TT_EOF)
                break;
            switch (tt) {
            case ')':
            case ',':
                logger.info("nextWordPolynomial = {}", a);
                return a; // do not change or remove
            case '-':
                b = b.negate();
            case '+':
            case '*':
                tt = tok.nextToken();
                break;
            default: // skip
            }
            // read coefficient, monic monomial and polynomial
            if (tt == StreamTokenizer.TT_EOF)
                break;
            switch (tt) {
            // case '_': removed 
            case '}':
                throw new InvalidExpressionException("mismatch of braces after " + a + ", error at " + b);
            case '{': // recursion
                StringBuffer rf = new StringBuffer();
                int level = 0;
                do {
                    tt = tok.nextToken();
                    //System.out.println("token { = " + ((char)tt) + ", " + tt + ", level = " + level);
                    if (tt == StreamTokenizer.TT_EOF) {
                        throw new InvalidExpressionException(
                                        "mismatch of braces after " + a + ", error at " + b);
                    }
                    if (tt == '{') {
                        level++;
                    }
                    if (tt == '}') {
                        level--;
                        if (level < 0) {
                            continue; // skip last closing brace 
                        }
                    }
                    if (tok.sval != null) {
                        if (rf.length() > 0 && rf.charAt(rf.length() - 1) != '.') {
                            rf.append(" ");
                        }
                        rf.append(tok.sval); // " " + 
                    } else {
                        rf.append((char) tt);
                    }
                } while (level >= 0);
                //System.out.println("coeff{} = " + rf.toString() );
                try {
                    r = (RingElem) fac.parse(rf.toString());
                } catch (NumberFormatException re) {
                    throw new InvalidExpressionException("not a number :" + rf + ": " + fac, re);
                }
                logger.debug("coeff {}", r);
                ie = nextExponent();
                logger.debug("ie {}", ie);
                r = (RingElem) r.power(ie);
                logger.debug("coeff^ie {}", r);
                b = b.multiply(r, leer);

                tt = tok.nextToken();
                logger.debug("tt,digit = {}", tok);
                //no break;
                break;

            //case '.': // eventually a float
            //System.out.println("start . = " + reader);
            //throw new InvalidExpressionException("float must start with a digit ");

            case StreamTokenizer.TT_WORD:
                //System.out.println("TT_WORD: " + tok.sval);
                if (tok.sval == null || tok.sval.length() == 0)
                    break;
                // read coefficient
                first = tok.sval.charAt(0);
                if (digit(first) || first == '/' || first == '.' || first == '~') {
                    //System.out.println("coeff 0 = " + tok.sval );
                    StringBuffer df = new StringBuffer();
                    df.append(tok.sval);
                    if (tok.sval.length() > 1 && digit(tok.sval.charAt(1))) {
                        //System.out.println("start / or . = " + tok.sval);
                        if (first == '/') { // let x/2 be x 1/2
                            df.insert(0, "1");
                        }
                        if (first == '.') { // let x.2 be x 0.2
                            df.insert(0, "0");
                        }
                    }
                    if (tok.sval.charAt(tok.sval.length() - 1) == 'i') { // complex number
                        tt = tok.nextToken();
                        logger.debug("tt,im = {}", tok);
                        if (tok.sval != null || tt == '-') {
                            if (tok.sval != null) {
                                df.append(tok.sval);
                            } else {
                                df.append("-");
                            }
                            if (tt == '-') {
                                tt = tok.nextToken(); // todo: decimal number
                                if (tok.sval != null && digit(tok.sval.charAt(0))) {
                                    df.append(tok.sval);

                                } else {
                                    tok.pushBack();
                                }
                            }
                        } else {
                            tok.pushBack();
                        }
                    }
                    tt = tok.nextToken();
                    if (tt == '.') { // decimal number, obsolete by word char?
                        tt = tok.nextToken();
                        logger.debug("tt,dot = {}", tok);
                        if (tok.sval != null) {
                            df.append(".");
                            df.append(tok.sval);
                        } else {
                            tok.pushBack();
                            tok.pushBack();
                        }
                    } else {
                        tok.pushBack();
                    }
                    try {
                        //System.out.println("df = " + df + ", fac = " + fac.getClass());
                        r = (RingElem) fac.parse(df.toString());
                        //System.out.println("r = " + r);
                    } catch (NumberFormatException re) {
                        //System.out.println("re = " + re);
                        throw new InvalidExpressionException("not a number :" + df + ": " + fac, re);
                    }
                    logger.debug("coeff {}", r);
                    //System.out.println("r = " + r.toScriptFactory());
                    ie = nextExponent();
                    logger.debug("ie {}", ie);
                    // r = r^ie;
                    r = (RingElem) r.power(ie);
                    logger.debug("coeff^ie {}", r);
                    b = b.multiply(r, leer);

                    tt = tok.nextToken();
                    logger.debug("tt,digit = {}", tok);
                }
                if (tt == StreamTokenizer.TT_EOF)
                    break;
                if (tok.sval == null)
                    break;
                // read monomial or recursion 
                first = tok.sval.charAt(0);
                if (letter(first)) {
                    //ix = leer.indexVar(tok.sval, vars); 
                    try {
                        e = wf.parse(tok.sval);
                    } catch (IllegalArgumentException ee) {
                        e = null;
                    }
                    logger.info("monom {}", e);
                    if (e == null) { // not found, look for coeff var
                        try {
                            r = (RingElem) fac.parse(tok.sval);
                        } catch (NumberFormatException re) {
                            throw new InvalidExpressionException("recursively unknown variable " + tok.sval);
                        }
                        ie = nextExponent();
                        //  System.out.println("ie: " + ie);
                        r = (RingElem) r.power(ie);
                        //  System.out.println("re:  " + r);
                        b = b.multiply(r, leer);
                    } else { // found
                        ie = nextExponent();
                        //  System.out.println("ie: " + ie);
                        e = e.power(ie);
                        //  System.out.println("e:  " + e);
                        b = b.multiply(e);
                    }
                    tt = tok.nextToken();
                    logger.debug("tt,letter = {}", tok);
                }
                break;

            case '(':
                c = nextWordPolynomial(wfac);
                logger.debug("factor {}", c);
                ie = nextExponent();
                logger.debug("ie {}", ie);
                c = (GenWordPolynomial) c.power(ie);
                logger.debug("factor^ie {}", c);
                b = b.multiply(c);

                tt = tok.nextToken();
                logger.debug("tt,digit = {}", tok);
                //no break;
                break;

            default: //skip 
            }
            if (tt == StreamTokenizer.TT_EOF)
                break;
            // complete polynomial
            tok.pushBack();
            switch (tt) {
            case '-':
            case '+':
            case ')':
            case ',':
                logger.debug("b, = {}", b);
                a = a.sum(b);
                b = a1;
                break;
            case '*':
                logger.debug("b, = {}", b);
                //a = a.sum(b); 
                //b = a1;
                break;
            case '\n':
                tt = tok.nextToken();
                logger.debug("tt,nl = {}", tt);
                break;
            default: // skip or finish ?
                logger.debug("default: {}", tok);
            }
        }
        logger.debug("b = {}", b);
        a = a.sum(b);
        logger.info("nextWordPolynomial = {}", a);
        // b = a1;
        return a;
    }


    /**
     * Parsing method for word polynomial list. Syntax:
     *
     * <pre>
     * ( p1, p2, p3, ..., pn )
     * </pre>
     *
     * @return the next word polynomial list.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public List<GenWordPolynomial> nextWordPolynomialList() throws IOException {
        GenWordPolynomialRing wfac = new GenWordPolynomialRing(pfac);
        return nextWordPolynomialList(wfac);
    }


    /**
     * Parsing method for word polynomial list. Syntax:
     *
     * <pre>
     * ( p1, p2, p3, ..., pn )
     * </pre>
     *
     * @param wfac word polynomial ring.
     * @return the next word polynomial list.
     * @throws IOException
     */
    public List<GenWordPolynomial> nextWordPolynomialList(GenWordPolynomialRing wfac) throws IOException {
        GenWordPolynomial a;
        List<GenWordPolynomial> L = new ArrayList<GenWordPolynomial>();
        int tt;
        tt = tok.nextToken();
        if (tt == StreamTokenizer.TT_EOF)
            return L;
        if (tt != '(')
            return L;
        logger.debug("word polynomial list");
        while (true) {
            tt = tok.nextToken();
            if (tok.ttype == ',')
                continue;
            if (tt == '(') {
                a = nextWordPolynomial(wfac);
                tt = tok.nextToken();
                if (tok.ttype != ')')
                    tok.pushBack();
            } else {
                tok.pushBack();
                a = nextWordPolynomial(wfac);
            }
            logger.info("next pol = {}", a);
            L.add(a);
            if (tok.ttype == StreamTokenizer.TT_EOF)
                break;
            if (tok.ttype == ')')
                break;
        }
        return L;
    }


    /**
     * Parsing method for exterior polynomial. Syntax: same as for polynomial.
     * Multiplication will be non commutative.
     * @return the next polynomial.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenExteriorPolynomial nextExteriorPolynomial() throws IOException {
        GenExteriorPolynomialRing wfac = new GenExteriorPolynomialRing(pfac);
        return nextExteriorPolynomial(wfac);
    }


    /**
     * Parsing method for exterior polynomial. Syntax: except for
     * index list same as for polynomial. Multiplication will be
     * anti-commutative.
     * @param wfac exterior polynomial ring.
     * @return the next polynomial.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenExteriorPolynomial nextExteriorPolynomial(GenExteriorPolynomialRing wfac) throws IOException {
        logger.info("wfac = {}", wfac);
        IndexFactory wf = wfac.ixfac;

        GenExteriorPolynomial a = wfac.getZERO();
        GenExteriorPolynomial a1 = wfac.getONE();
        IndexList leer = wfac.wone;
        RingFactory fac = wfac.coFac;
        if (debug) {
            logger.debug("a = {}", a);
            logger.debug("a1 = {}", a1);
        }
        GenExteriorPolynomial b = a1;
        GenExteriorPolynomial c;
        int tt;
        char first;
        RingElem r;
        IndexList e;
        //int ix;
        long ie;
        while (true) {
            // next input. determine next action
            tt = tok.nextToken();
            //System.out.println("while tt = " + tok);
            logger.debug("while tt = {}", tok);
            if (tt == StreamTokenizer.TT_EOF)
                break;
            switch (tt) {
            case ')':
            case ',':
                logger.info("nextExteriorPolynomial = {}", a);
                return a; // do not change or remove
            case '-':
                b = b.negate();
            case '+':
            case '*':
                tt = tok.nextToken();
                break;
            default: // skip
            }
            // read coefficient, monic monomial and polynomial
            if (tt == StreamTokenizer.TT_EOF)
                break;
            switch (tt) {
            // case '_': removed
            case '}':
                throw new InvalidExpressionException("mismatch of braces after " + a + ", error at " + b);
            case '{': // recursion
                StringBuffer rf = new StringBuffer();
                int level = 0;
                do {
                    tt = tok.nextToken();
                    //System.out.println("token { = " + ((char)tt) + ", " + tt + ", level = " + level);
                    if (tt == StreamTokenizer.TT_EOF) {
                        throw new InvalidExpressionException(
                                        "mismatch of braces after " + a + ", error at " + b);
                    }
                    if (tt == '{') {
                        level++;
                    }
                    if (tt == '}') {
                        level--;
                        if (level < 0) {
                            continue; // skip last closing brace
                        }
                    }
                    if (tok.sval != null) {
                        if (rf.length() > 0 && rf.charAt(rf.length() - 1) != '.') {
                            rf.append(" ");
                        }
                        rf.append(tok.sval); // " " +
                    } else {
                        rf.append((char) tt);
                    }
                } while (level >= 0);
                //System.out.println("coeff{} = " + rf.toString() );
                try {
                    r = (RingElem) fac.parse(rf.toString());
                } catch (NumberFormatException re) {
                    throw new InvalidExpressionException("not a number :" + rf + ": " + fac, re);
                }
                logger.debug("coeff {}", r);
                ie = nextExponent();
                logger.debug("ie {}", ie);
                r = (RingElem) r.power(ie);
                logger.debug("coeff^ie {}", r);
                b = b.multiply(r, leer);

                tt = tok.nextToken();
                logger.debug("tt,digit = {}", tok);
                //no break;
                break;

            //case '.': // eventually a float
            //System.out.println("start . = " + reader);
            //throw new InvalidExpressionException("float must start with a digit ");

            case StreamTokenizer.TT_WORD:
                //System.out.println("TT_WORD: " + tok.sval);
                if (tok.sval == null || tok.sval.length() == 0)
                    break;
                // read coefficient
                first = tok.sval.charAt(0);
                if (digit(first) || first == '/' || first == '.' || first == '~') {
                    //System.out.println("coeff 0 = " + tok.sval );
                    StringBuffer df = new StringBuffer();
                    df.append(tok.sval);
                    if (tok.sval.length() > 1 && digit(tok.sval.charAt(1))) {
                        //System.out.println("start / or . = " + tok.sval);
                        if (first == '/') { // let x/2 be x 1/2
                            df.insert(0, "1");
                        }
                        if (first == '.') { // let x.2 be x 0.2
                            df.insert(0, "0");
                        }
                    }
                    if (tok.sval.charAt(tok.sval.length() - 1) == 'i') { // complex number
                        tt = tok.nextToken();
                        logger.debug("tt,im = {}", tok);
                        if (tok.sval != null || tt == '-') {
                            if (tok.sval != null) {
                                df.append(tok.sval);
                            } else {
                                df.append("-");
                            }
                            if (tt == '-') {
                                tt = tok.nextToken(); // todo: decimal number
                                if (tok.sval != null && digit(tok.sval.charAt(0))) {
                                    df.append(tok.sval);

                                } else {
                                    tok.pushBack();
                                }
                            }
                        } else {
                            tok.pushBack();
                        }
                    }
                    tt = tok.nextToken();
                    if (tt == '.') { // decimal number, obsolete by word char?
                        tt = tok.nextToken();
                        logger.debug("tt,dot = {}", tok);
                        if (tok.sval != null) {
                            df.append(".");
                            df.append(tok.sval);
                        } else {
                            tok.pushBack();
                            tok.pushBack();
                        }
                    } else {
                        tok.pushBack();
                    }
                    try {
                        //System.out.println("df = " + df + ", fac = " + fac.getClass());
                        r = (RingElem) fac.parse(df.toString());
                        //System.out.println("r = " + r);
                    } catch (NumberFormatException re) {
                        //System.out.println("re = " + re);
                        throw new InvalidExpressionException("not a number :" + df + ": " + fac, re);
                    }
                    logger.debug("coeff {}", r);
                    //System.out.println("r = " + r.toScriptFactory());
                    ie = nextExponent();
                    logger.debug("ie {}", ie);
                    // r = r^ie;
                    r = (RingElem) r.power(ie);
                    logger.debug("coeff^ie {}", r);
                    b = b.multiply(r, leer);

                    tt = tok.nextToken();
                    logger.debug("tt,digit = {}", tok);
                }
                if (tt == StreamTokenizer.TT_EOF)
                    break;
                if (tok.sval == null)
                    break;
                // read monomial, index list or recursion
                first = tok.sval.charAt(0);
                //System.out.println("first = '" + first + "'");
                if (letter(first)) {
                    //ix = leer.indexVar(tok.sval, vars);
                    if ("E".charAt(0) == first) {
                       StringBuffer ri = new StringBuffer();
                       ri.append(tok.sval);
                       //System.out.println("index tok.sval " + tok.sval);
                       while (true) {
                          tt = tok.nextToken();
                          //System.out.println("index tok.token " + tt + ", tok.sval " + tok.sval);
                          if (tt == '(') {
                              ri.append("(");
			      continue;
                          }
                          if (tt == ',') {
                              ri.append(",");
			      continue;
                          }
                          if (tok.sval != null) {
                              ri.append(tok.sval);
                              //System.out.println("index tok.sval " + tok.sval);
                          }
                          if (tt == ')') {
                              ri.append(")");
                              break;
                          }
                       }
                       //System.out.println("index list, ri " + ri.toString());
                       try {
                           e = wf.parse(ri.toString());
                       } catch (IllegalArgumentException ee) {
                           e = null;
                       }
                    } else {
			e = null; //wf.valueOf(tok.sval);
                    }
                    logger.info("monom {}", e);
                    //System.out.println("index list, e " + e);
                    if (e == null) { // not found, look for coeff var
                        try {
                            r = (RingElem) fac.parse(tok.sval);
                        } catch (NumberFormatException re) {
                            throw new InvalidExpressionException("recursively unknown variable " + tok.sval);
                        }
                        ie = nextExponent();
                        //  System.out.println("ie: " + ie);
                        r = (RingElem) r.power(ie);
                        //  System.out.println("re:  " + r);
                        b = b.multiply(r, leer);
                    } else { // found
                        ie = nextExponent();
                        //  System.out.println("ie: " + ie);
                        //no RingElem: e = e.power(ie);
                        //no RingElem: e = Power.<IndexList> positivePower(e, ie);
                        if (ie > 1) { // exterior product is 0
                            e = wf.getZERO();
                        } if (ie <= 0) {
                            e = wf.getONE();
                        }
                        //System.out.println("e:  " + e);
                        b = b.multiply(e);
                    }
                    tt = tok.nextToken();
                    logger.debug("tt,letter = {}", tok);
                }
                break;

            case '(':
                c = nextExteriorPolynomial(wfac);
                logger.debug("factor {}", c);
                ie = nextExponent();
                logger.debug("ie {}", ie);
                c = (GenExteriorPolynomial) c.power(ie);
                logger.debug("factor^ie {}", c);
                b = b.multiply(c);

                tt = tok.nextToken();
                logger.debug("tt,digit = {}", tok);
                //no break;
                break;

            default: //skip
            }
            if (tt == StreamTokenizer.TT_EOF)
                break;
            // complete polynomial
            tok.pushBack();
            switch (tt) {
            case '-':
            case '+':
            case ')':
            case ',':
                logger.debug("b, = {}", b);
                a = a.sum(b);
                b = a1;
                break;
            case '*':
                logger.debug("b, = {}", b);
                //a = a.sum(b);
                //b = a1;
                break;
            case '\n':
                tt = tok.nextToken();
                logger.debug("tt,nl = {}", tt);
                break;
            default: // skip or finish ?
                logger.debug("default: {}", tok);
            }
        }
        logger.debug("b = {}", b);
        a = a.sum(b);
        logger.info("nextExteriorPolynomial = {}", a);
        // b = a1;
        return a;
    }


    /**
     * Parsing method for exterior polynomial list. Syntax:
     * <pre>
     * ( p1, p2, p3, ..., pn )
     * </pre>
     *
     * @return the next exterior polynomial list.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public List<GenExteriorPolynomial> nextExteriorPolynomialList() throws IOException {
        GenExteriorPolynomialRing wfac = new GenExteriorPolynomialRing(pfac);
        return nextExteriorPolynomialList(wfac);
    }


    /**
     * Parsing method for exterior polynomial list. Syntax:
     * <pre>
     * ( p1, p2, p3, ..., pn )
     * </pre>
     *
     * @param wfac exterior polynomial ring.
     * @return the next exterior polynomial list.
     * @throws IOException
     */
    public List<GenExteriorPolynomial> nextExteriorPolynomialList(GenExteriorPolynomialRing wfac) throws IOException {
        GenExteriorPolynomial a;
        List<GenExteriorPolynomial> L = new ArrayList<GenExteriorPolynomial>();
        int tt;
        tt = tok.nextToken();
        if (tt == StreamTokenizer.TT_EOF)
            return L;
        if (tt != '(')
            return L;
        logger.debug("exterior polynomial list");
        while (true) {
            tt = tok.nextToken();
            if (tok.ttype == ',')
                continue;
            if (tt == '(') {
                a = nextExteriorPolynomial(wfac);
                tt = tok.nextToken();
                if (tok.ttype != ')')
                    tok.pushBack();
            } else {
                tok.pushBack();
                a = nextExteriorPolynomial(wfac);
            }
            logger.info("next pol = {}", a);
            L.add(a);
            if (tok.ttype == StreamTokenizer.TT_EOF)
                break;
            if (tok.ttype == ')')
                break;
        }
        return L;
    }


    // must also allow +/- // does not work with tokenizer
    //private static boolean number(char x) {
    //    return digit(x) || x == '-' || x == '+';
    //}


    static boolean digit(char x) {
        return '0' <= x && x <= '9';
    }


    static boolean letter(char x) {
        return ('a' <= x && x <= 'z') || ('A' <= x && x <= 'Z');
    }


    // unused
    public void nextComma() throws IOException {
        int tt;
        if (tok.ttype == ',') {
            tt = tok.nextToken();
            logger.debug("after comma: {}", tt);
        }
    }


    /**
     * Parse variable list from String.
     * @param s String. Syntax:
     * 
     *            <pre>
     * (n1,...,nk)
     *            </pre>
     * 
     *            or
     * 
     *            <pre>
     * (n1 ... nk)
     *            </pre>
     * 
     *            parenthesis are optional.
     * @return array of variable names found in s.
     */
    public static String[] variableList(String s) {
        String[] vl = null;
        if (s == null) {
            return vl;
        }
        String st = s.trim();
        if (st.length() == 0) {
            return new String[0];
        }
        if (st.charAt(0) == '(') {
            st = st.substring(1);
        }
        if (st.charAt(st.length() - 1) == ')') {
            st = st.substring(0, st.length() - 1);
        }
        st = st.replaceAll(",", " ");
        List<String> sl = new ArrayList<String>();
        Scanner sc = new Scanner(st);
        while (sc.hasNext()) {
            String sn = sc.next();
            sl.add(sn);
        }
        sc.close();
        vl = new String[sl.size()];
        int i = 0;
        for (String si : sl) {
            vl[i] = si;
            i++;
        }
        return vl;
    }


    /**
     * Extract variable list from expression.
     * @param s String. Syntax: any polynomial expression.
     * @return array of variable names found in s.
     */
    public static String[] expressionVariables(String s) {
        String[] vl = null;
        if (s == null) {
            return vl;
        }
        String st = s.trim();
        if (st.length() == 0) {
            return new String[0];
        }
        st = st.replaceAll(",", " ");
        st = st.replaceAll("\\+", " ");
        st = st.replaceAll("-", " ");
        st = st.replaceAll("\\*", " ");
        st = st.replaceAll("/", " ");
        st = st.replaceAll("\\(", " ");
        st = st.replaceAll("\\)", " ");
        st = st.replaceAll("\\{", " ");
        st = st.replaceAll("\\}", " ");
        st = st.replaceAll("\\[", " ");
        st = st.replaceAll("\\]", " ");
        st = st.replaceAll("\\^", " ");
        //System.out.println("st = " + st);

        Set<String> sl = new TreeSet<String>();
        Scanner sc = new Scanner(st);
        while (sc.hasNext()) {
            String sn = sc.next();
            if (sn == null || sn.length() == 0) {
                continue;
            }
            //System.out.println("sn = " + sn);
            int i = 0;
            while (digit(sn.charAt(i)) && i < sn.length() - 1) {
                i++;
            }
            //System.out.println("sn = " + sn + ", i = " + i);
            if (i > 0) {
                sn = sn.substring(i, sn.length());
            }
            //System.out.println("sn = " + sn);
            if (sn.length() == 0) {
                continue;
            }
            if (!letter(sn.charAt(0))) {
                continue;
            }
            //System.out.println("sn = " + sn);
            sl.add(sn);
        }
        sc.close();
        vl = new String[sl.size()];
        int i = 0;
        for (String si : sl) {
            vl[i] = si;
            i++;
        }
        return vl;
    }

}
