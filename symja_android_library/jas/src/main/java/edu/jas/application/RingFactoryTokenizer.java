/*
 * $Id$
 */

package edu.jas.application;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigQuaternion;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLongRing;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.RelationTable;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * RingFactory Tokenizer. Used to read ring factories from input streams. It can
 * read also QuotientRing factory.
 * @author Heinz Kredel
 */

public class RingFactoryTokenizer {


    private static final Logger logger = Logger.getLogger(RingFactoryTokenizer.class);


    private final boolean debug = logger.isDebugEnabled();


    private String[] vars;


    private int nvars = 1;


    private TermOrder tord;


    private RelationTable table;


    //private Reader in;
    private final StreamTokenizer tok;


    private final Reader reader;


    private RingFactory fac;


    private static enum coeffType {
        BigRat, BigInt, ModInt, BigC, BigQ, BigD, ANrat, ANmod, RatFunc, ModFunc, IntFunc
    };


    private coeffType parsedCoeff = coeffType.BigRat;


    private GenPolynomialRing pfac;


    private static enum polyType {
        PolBigRat, PolBigInt, PolModInt, PolBigC, PolBigD, PolBigQ, PolANrat, PolANmod, PolRatFunc, PolModFunc, PolIntFunc
    };


    private polyType parsedPoly = polyType.PolBigRat;


    private GenSolvablePolynomialRing spfac;


    /**
     * No-args constructor reads from System.in.
     */
    public RingFactoryTokenizer() {
        this(new BufferedReader(new InputStreamReader(System.in,Charset.forName("UTF8"))));
    }


    /**
     * Constructor with Ring and Reader.
     * @param rf ring factory.
     * @param r reader stream.
     */
    public RingFactoryTokenizer(GenPolynomialRing rf, Reader r) {
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
        vars = rf.getVars();
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
    }


    /**
     * Constructor with Reader.
     * @param r reader stream.
     */
    @SuppressWarnings("unchecked")
    public RingFactoryTokenizer(Reader r) {
        //BasicConfigurator.configure();
        vars = null;
        tord = new TermOrder();
        nvars = 1;
        fac = new BigRational(1);

        //pfac = null;
        pfac = new GenPolynomialRing<BigRational>(fac, nvars, tord, vars);

        //spfac = null;
        spfac = new GenSolvablePolynomialRing<BigRational>(fac, nvars, tord, vars);

        reader = r;
        tok = new StreamTokenizer(r);
        tok.resetSyntax();
        // tok.eolIsSignificant(true); no more
        tok.eolIsSignificant(false);
        tok.wordChars('0', '9');
        tok.wordChars('a', 'z');
        tok.wordChars('A', 'Z');
        tok.wordChars('_', '_'); // for subscripts x_i
        tok.wordChars('/', '/'); // wg. rational numbers
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
        case BigD:
            pfac = new GenPolynomialRing<BigDecimal>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigD;
            break;
        case RatFunc:
            pfac = new GenPolynomialRing<Quotient<BigInteger>>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolRatFunc;
            break;
        case ModFunc:
            pfac = new GenPolynomialRing<Quotient<ModInteger>>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolModFunc;
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
        case BigD:
            spfac = new GenSolvablePolynomialRing<BigDecimal>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolBigD;
            break;
        case RatFunc:
            spfac = new GenSolvablePolynomialRing<Quotient<BigInteger>>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolRatFunc;
            break;
        case ModFunc:
            spfac = new GenSolvablePolynomialRing<Quotient<ModInteger>>(fac, nvars, tord, vars);
            parsedPoly = polyType.PolModFunc;
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
     * Parsing method for comments. syntax: (* comment *) | /_* comment *_/
     * without _ Does not work with this pushBack(), unused.
     */
    public String nextComment() throws IOException {
        // syntax: (* comment *) | /* comment */ 
        StringBuffer c = new StringBuffer();
        int tt;
        if (debug)
            logger.debug("comment: " + tok);
        tt = tok.nextToken();
        if (debug)
            logger.debug("comment: " + tok);
        if (tt == '(') {
            tt = tok.nextToken();
            if (debug)
                logger.debug("comment: " + tok);
            if (tt == '*') {
                if (debug)
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
            if (debug)
                logger.debug("comment: " + tok);
        }
        tok.pushBack();
        if (debug)
            logger.debug("comment: " + tok);
        return c.toString();
    }


    /**
     * Parsing method for variable list. syntax: (a, b c, de) gives [ "a", "b",
     * "c", "de" ]
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
        }
        Object[] ol = l.toArray();
        String[] v = new String[ol.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = (String) ol[i];
        }
        return v;
    }


    /**
     * Parsing method for coefficient ring. syntax: Rat | Q | Int | Z | Mod
     * modul | Complex | C | D | Quat | AN[ (var) ( poly ) | AN[ modul (var) (
     * poly ) ] | RatFunc (var_list) | ModFunc modul (var_list) | IntFunc
     * (var_list)
     * @return the next coefficient factory.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
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
                coeff = new BigQuaternion(0);
                ct = coeffType.BigQ;
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
                        BigInteger lm = new BigInteger(ModLongRing.MAX_LONG); //Long.MAX_VALUE);
                        if (mo.compareTo(lm) < 0) {
                            coeff = new ModLongRing(mo.getVal());
                        } else {
                            coeff = new ModIntegerRing(mo.getVal());
                        }
                        System.out.println("coeff = " + coeff + " :: " + coeff.getClass());
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
            } else if (tok.sval.equalsIgnoreCase("RatFunc")) {
                String[] rfv = nextVariableList();
                //System.out.println("rfv = " + rfv.length + " " + rfv[0]);
                int vr = rfv.length;
                BigInteger bi = new BigInteger();
                TermOrder to = new TermOrder(TermOrder.INVLEX);
                GenPolynomialRing<BigInteger> pcf = new GenPolynomialRing<BigInteger>(bi, vr, to, rfv);
                coeff = new QuotientRing(pcf);
                ct = coeffType.RatFunc;
            } else if (tok.sval.equalsIgnoreCase("ModFunc")) {
                tt = tok.nextToken();
                RingFactory mi = new ModIntegerRing("19");
                if (tok.sval != null && tok.sval.length() > 0) {
                    if (digit(tok.sval.charAt(0))) {
                        mi = new ModIntegerRing(tok.sval);
                    } else {
                        tok.pushBack();
                    }
                } else {
                    tok.pushBack();
                }
                String[] rfv = nextVariableList();
                //System.out.println("rfv = " + rfv.length + " " + rfv[0]);
                int vr = rfv.length;
                TermOrder to = new TermOrder(TermOrder.INVLEX);
                GenPolynomialRing<ModInteger> pcf = new GenPolynomialRing<ModInteger>(mi, vr, to, rfv);
                coeff = new QuotientRing(pcf);
                ct = coeffType.ModFunc;
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
                        logger.error("AlgebraicNumber only for univariate polynomials");
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
                    if (debug) {
                        logger.debug("pfac = " + pfac);
                    }
                    GenPolynomialTokenizer ptok = new GenPolynomialTokenizer(pfac, reader);
                    GenPolynomial mod = ptok.nextPolynomial();
                    ptok = null;
                    if (debug) {
                        logger.debug("mod = " + mod);
                    }
                    pfac = tpfac;
                    fac = tfac;
                    vars = ovars;
                    if (tcfac instanceof ModIntegerRing) {
                        GenPolynomial<ModInteger> gfmod;
                        gfmod = (GenPolynomial<ModInteger>) mod;
                        coeff = new AlgebraicNumberRing<ModInteger>(gfmod);
                        ct = coeffType.ANmod;
                    } else {
                        GenPolynomial<BigRational> anmod;
                        anmod = (GenPolynomial<BigRational>) mod;
                        coeff = new AlgebraicNumberRing<BigRational>(anmod);
                        ct = coeffType.ANrat;
                    }
                    if (debug) {
                        logger.debug("coeff = " + coeff);
                    }
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
     * Parsing method for weight list. syntax: (w1, w2, w3, ..., wn)
     * @return the next weight list.
     * @throws IOException
     */
    public long[] nextWeightList() throws IOException {
        List<Long> l = new ArrayList<Long>();
        long[] w = null;
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
        }
        Object[] ol = l.toArray();
        w = new long[ol.length];
        for (int i = 0; i < w.length; i++) {
            w[i] = ((Long) ol[ol.length - i - 1]).longValue();
        }
        return w;
    }


    /**
     * Parsing method for weight array. syntax: ( (w11, ...,w1n), ..., (wm1,
     * ..., wmn) )
     * @return the next weight array.
     * @throws IOException
     */
    public long[][] nextWeightArray() throws IOException {
        List<long[]> l = new ArrayList<long[]>();
        long[][] w;
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
        }
        Object[] ol = l.toArray();
        w = new long[ol.length][];
        for (int i = 0; i < w.length; i++) {
            w[i] = (long[]) ol[i];
        }
        return w;
    }


    /**
     * Parsing method for split index. syntax: |i|
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
            if (debug) {
                logger.debug("split index");
            }
            tt = tok.nextToken();
            if (tt == StreamTokenizer.TT_EOF) {
                return e;
            } else if (tok.sval != null) {
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
            if (debug) {
                logger.debug("split index");
            }
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
                        return e0; // ??
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
     * Parsing method for term order name. syntax: termOrderName = L, IL, LEX,
     * G, IG, GRLEX, W(weights) |split index|
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
                } else if (tok.sval.equalsIgnoreCase("W")) {
                    long[][] w = nextWeightArray();
                    //int s = nextSplitIndex(); // no more
                    return new TermOrder(w);
                }
            }
        }
        int s = nextSplitIndex();
        if (s <= 0) {
            return new TermOrder(evord);
        }
        return new TermOrder(evord, evord, vars.length, s);
    }


    /**
     * Parsing method for solvable polynomial relation table. syntax: ( p_1,
     * p_2, p_3, ..., p_{n+3} ) semantics: p_{n+1} * p_{n+2} = p_{n+3} The next
     * relation table is stored into the solvable polynomial factory.
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
        if (debug) {
            logger.debug("start relation table: " + tt);
        }
        if (tok.sval != null) {
            if (tok.sval.equalsIgnoreCase("RelationTable")) {
                GenPolynomialTokenizer ptok = new GenPolynomialTokenizer(pfac, reader);
                rels = ptok.nextPolynomialList();
                ptok = null;
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
                    sp = new GenSolvablePolynomial(spfac);
                    sp.doPutToMap(p.getMap());
                    table.update(e, f, sp);
                }
            }
        }
        if (debug) {
            logger.info("table = " + table);
        }
        return;
    }


    /**
     * Parsing method for polynomial set. syntax: coeffRing varList
     * termOrderName polyList.
     * @return the next polynomial set.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenPolynomialRing nextPolynomialRing() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = " + comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = " + coeff);

        vars = nextVariableList();
        logger.info("vars = " + Arrays.toString(vars));
        if (vars != null) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = " + tord);
        // check more TOs
        initFactory(coeff, parsedCoeff); // global: nvars, tord, vars
        // now pfac is initialized
        return pfac;
    }


    /**
     * Parsing method for solvable polynomial set. syntax: varList termOrderName
     * relationTable polyList.
     * @return the next solvable polynomial set.
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public GenSolvablePolynomialRing nextSolvablePolynomialRing() throws IOException {
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = " + comments);

        RingFactory coeff = nextCoefficientRing();
        logger.info("coeff = " + coeff);

        vars = nextVariableList();
        logger.info("vars = " + Arrays.toString(vars));
        if (vars != null) {
            nvars = vars.length;
        }

        tord = nextTermOrder();
        logger.info("tord = " + tord);
        // check more TOs

        initFactory(coeff, parsedCoeff); // must be because of symmetric read
        initSolvableFactory(coeff, parsedCoeff); // global: nvars, tord, vars
        //System.out.println("pfac = " + pfac);
        //System.out.println("spfac = " + spfac);

        nextRelationTable();
        if (logger.isInfoEnabled()) {
            logger.info("table = " + table + ", tok = " + tok);
        }
        // now spfac is initialized
        return spfac;
    }


    private boolean digit(char x) {
        return '0' <= x && x <= '9';
    }


    //private boolean letter(char x) {
    //    return ('a' <= x && x <= 'z') || ('A' <= x && x <= 'Z');
    //}


    // unused
    public void nextComma() throws IOException {
        int tt;
        if (tok.ttype == ',') {
            tt = tok.nextToken();
            if (debug) {
                logger.debug("after comma: " + tt);
            }
        }
    }


    /**
     * Parse variable list from String.
     * @param s String. Syntax: (n1,...,nk) or (n1 ... nk), brackest are also
     *            optional.
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
        vl = new String[sl.size()];
        int i = 0;
        for (String si : sl) {
            vl[i] = si;
            i++;
        }
        return vl;
    }

}
