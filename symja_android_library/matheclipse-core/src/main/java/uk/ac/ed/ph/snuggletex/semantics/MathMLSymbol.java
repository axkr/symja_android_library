/* $Id:MathMLOperator.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.semantics;

/**
 * Defines the resulting content of various MathML "symbols" (i.e. identifiers and operators). 
 * 
 * @author David McKain
 * @version $Revision:179 $
 */
public interface MathMLSymbol {
    
    public static final String ADD = "+";
    public static final String SUBTRACT = "-";
    public static final String ASTERISK = "*";
    public static final String SLASH = "/";
    public static final String COMMA = ",";
    public static final String EQUALS = "=";
    public static final String DOT = ".";
    
    public static final String LESS_THAN = "<";
    public static final String GREATER_THAN = ">";
    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";
    public static final String OPEN_CURLY_BRACKET = "{";
    public static final String CLOSE_CURLY_BRACKET = "}";
    public static final String OPEN_SQUARE_BRACKET = "[";
    public static final String CLOSE_SQUARE_BRACKET = "]";
    public static final String DIVIDES = "|";
    public static final String VERT_BRACKET = "|";

    public static final String FACTORIAL = "!";
    public static final String BACKSLASH = "\\";
    
    public static final String JMATH = "\u006a";
    public static final String NEG = "\u00ac";
    public static final String PM = "\u00b1";

    public static final String UC_AA = "\u00c5";
    public static final String TIMES = "\u00d7";
    public static final String AA = "\u00e5";
    public static final String DIV = "\u00f7";
    
    public static final String IMATH = "\u0131";
    
    public static final String ALPHA = "\u03b1";
    public static final String BETA = "\u03b2";
    public static final String GAMMA = "\u03b3";
    public static final String DELTA = "\u03b4";
    public static final String EPSILON = "\u03f5";
    public static final String VAREPSILON = "\u03b5";
    public static final String ZETA = "\u03b6";
    public static final String ETA = "\u03b7";
    public static final String THETA = "\u03b8";
    public static final String VARTHETA = "\u03d1";
    public static final String IOTA = "\u03b9";
    public static final String KAPPA = "\u03ba";
    public static final String LAMBDA = "\u03bb";
    public static final String MU = "\u03bc";
    public static final String NU = "\u03bd";
    public static final String XI = "\u03be";
    public static final String PI = "\u03c0";
    public static final String VARPI = "\u03d6";
    public static final String RHO = "\u03c1";
    public static final String VARRHO = "\u03f1";
    public static final String SIGMA = "\u03c3";
    public static final String VARSIGMA = "\u03c2";
    public static final String TAU = "\u03c4";
    public static final String UPSILON = "\u03c5";
    public static final String PHI = "\u03c6";
    public static final String VARPHI = "\u03d5";
    public static final String CHI = "\u03c7";
    public static final String PSI = "\u03c8";
    public static final String OMEGA = "\u03c9";
    public static final String UC_GAMMA = "\u0393";
    public static final String UC_DELTA = "\u0394";
    public static final String UC_THETA = "\u0398";
    public static final String UC_LAMBDA = "\u039b";
    public static final String UC_XI = "\u039e";
    public static final String UC_PI = "\u03a0";
    public static final String UC_SIGMA = "\u03a3";
    public static final String UC_UPSILON = "\u03a5";
    public static final String UC_PHI = "\u03a6";
    public static final String UC_PSI = "\u03a8";
    public static final String UC_OMEGA = "\u03a9";

    public static final String PRIME = "\u2032";
    public static final String DAGGER = "\u2020";
    public static final String DDAGGER = "\u2021";
    public static final String BULLET = "\u2022";
    public static final String APPLY_FUNCTION = "\u2061";
    public static final String INVISIBLE_TIMES = "\u2062";
    
    public static final String HBAR = "\u210f";
    public static final String ELL = "\u2113";
    public static final String WP = "\u2118";
    public static final String RE = "\u211c";
    public static final String IM = "\u2111";
    public static final String MHO = "\u2127";
    public static final String ALEPH = "\u2135";
    public static final String LEFTARROW = "\u2190";
    public static final String UPARROW = "\u2191";
    public static final String RIGHTARROW = "\u2192";
    public static final String DOWNARROW = "\u2193";
    public static final String LEFTRIGHTARROW = "\u2194";
    public static final String UPDOWNARROW = "\u2195";
    public static final String NWARROW = "\u2196";
    public static final String NEARROW = "\u2197";
    public static final String SEARROW = "\u2198";
    public static final String SWARROW = "\u2199";
    public static final String MAPSTO = "\u21a6";
    public static final String HOOKLEFTARROW = "\u21a9";
    public static final String HOOKRIGHTARROW = "\u21aa";
    public static final String LEFTHARPOONUP = "\u21bc";
    public static final String LEFTHARPOONDOWN = "\u21bd";
    public static final String RIGHTHARPOONOUP = "\u21c0";
    public static final String RIGHTHARPOONDOWN = "\u21c1";
    public static final String RIGHTLEFTHARPOONS = "\u21cc";
    public static final String UC_LEFTARROW = "\u21d0";
    public static final String UC_UPARROW = "\u21d1";
    public static final String UC_RIGHTARROW = "\u21d2";
    public static final String UC_DOWNARROW = "\u21d3";
    public static final String UC_LEFTRIGHTARROW = "\u21d4";
    public static final String UC_UPDOWNARROW = "\u21d5";

    public static final String FORALL = "\u2200";
    public static final String PARTIAL = "\u2202";
    public static final String EXISTS = "\u2203";
    public static final String EMPTYSET = "\u2205";
    public static final String NABLA = "\u2207";
    public static final String IN = "\u2208";
    public static final String NOT_IN = "\u2209";
    public static final String NI = "\u220b";
    public static final String NOT_NI = "\u220c";
    public static final String PROD = "\u220f";
    public static final String COPROD = "\u2210";
    public static final String SUM = "\u2211";
    public static final String MP = "\u2213";
    public static final String SETMINUS = "\u2216";
    public static final String AST = "\u2217";
    public static final String CIRC = "\u2218";
    public static final String PROPTO = "\u221d";
    public static final String INFTY = "\u221e";
    public static final String MID = "\u2223";
    public static final String NOT_MID = "\u2224";
    public static final String DOUBLE_VERT_BRACKET = "\u2225";
    public static final String PARALLEL = "\u2225";
    public static final String NOT_PARALLEL = "\u2226";
    public static final String WEDGE = "\u2227";
    public static final String VEE = "\u2228";
    public static final String CAP = "\u2229";
    public static final String CUP = "\u222a";
    public static final String SURD = "\u221a";
    public static final String ANGLE = "\u2220";
    public static final String INTEGRAL = "\u222b";
    public static final String OINT = "\u222e";
    public static final String TOP = "\u22a4";
    public static final String BOT = "\u22a5";
    public static final String SIM = "\u223c";
    public static final String WR = "\u2240";
    public static final String NOT_SIM = "\u2241";
    public static final String SIMEQ = "\u2243";
    public static final String NOT_SIMEQ = "\u2244";
    public static final String CONG = "\u2245";
    public static final String NOT_CONG = "\u2246";
    public static final String APPROX = "\u2248";
    public static final String NOT_APPROX = "\u2249";
    public static final String ASYMP = "\u224d";
    public static final String DOTEQ = "\u2250";
    public static final String NOT_EQUALS = "\u2260";
    public static final String EQUIV = "\u2261";
    public static final String NOT_EQUIV = "\u2262";
    public static final String LEQ = "\u2264";
    public static final String GEQ = "\u2265";
    public static final String LL = "\u226a";
    public static final String GG = "\u226b";
    public static final String NOT_LESS_THAN = "\u226e";
    public static final String NOT_GREATER_THAN = "\u226f";
    public static final String NOT_LEQ = "\u2270";
    public static final String NOT_GEQ = "\u2271";
    public static final String PREC = "\u227a";
    public static final String SUCC = "\u227b";
    public static final String PRECEQ = "\u227c";
    public static final String SUCCEQ = "\u227d";
    public static final String NOT_PREC = "\u2280";
    public static final String NOT_SUCC = "\u2281";
    public static final String SUBSET = "\u2282";
    public static final String SUPSET = "\u2283";
    public static final String NOT_SUBSET = "\u2284";
    public static final String NOT_SUPSET = "\u2285";
    public static final String SUBSETEQ = "\u2286";
    public static final String SUPSETEQ = "\u2287";
    public static final String NOT_SUBSETEQ = "\u2288";
    public static final String NOT_SUPSETEQ = "\u2289";
    public static final String UPLUS = "\u228e";
    public static final String SQSUBSET = "\u228f";
    public static final String SQSUPSET = "\u2290";
    public static final String SQSUBSETEQ = "\u2291";
    public static final String SQSUPSETEQ = "\u2292";
    public static final String SQCAP = "\u2293";
    public static final String SQCUP = "\u2294";
    public static final String OPLUS = "\u2295";
    public static final String OMINUS = "\u2296";
    public static final String OTIMES = "\u2297";
    public static final String OSLASH = "\u2298";
    public static final String ODOT = "\u2299";
    public static final String VDASH = "\u22a2";
    public static final String DASHV = "\u22a3";
    public static final String PERP = "\u22a5";
    public static final String MODELS = "\u22a8";
    public static final String NOT_VDASH = "\u22ac";
    public static final String NOT_MODELS = "\u22ad";
    public static final String TRIANGLELEFT = "\u22b2";
    public static final String TRIANGLERIGHT = "\u22b3";
    public static final String BIGWEDGE = "\u22c0";
    public static final String BIGVEE = "\u22c1";
    public static final String DIAMOND = "\u22c4";
    public static final String CDOT = "\u22c5";
    public static final String STAR = "\u22c6";
    public static final String BOWTIE = "\u22c8";
    public static final String NOT_SQSUBSETEQ = "\u22e2";
    public static final String NOT_SQSUPSETEQ = "\u22e3";
    public static final String BIGCAP = "\u22c2";
    public static final String BIGCUP = "\u22c3";
    public static final String VDOTS = "\u22ee";
    public static final String CDOTS = "\u22ef";
    public static final String DDOTS = "\u22f1";
    
    public static final String FROWN = "\u2322";
    public static final String SMILE = "\u2323";
    public static final String OPEN_ANGLE_BRACKET = "\u2329";
    public static final String CLOSE_ANGLE_BRACKET = "\u232a";

    public static final String BIGTRIANGLEUP = "\u25b3";
    public static final String TRIANGLE = "\u25b5";
    public static final String BIGTRIANGLEDOWN = "\u25bd";
    public static final String BIGCIRC = "\u25cb";
    
    public static final String SPADESUIT = "\u2660";
    public static final String HEARTSUIT = "\u2661"; /* (NB: This is not filled in, as per LaTeX. The MathML entity for heartsuit is mapped to U+2665) */
    public static final String DIAMONDSUIT = "\u2662"; /* (NB: This is not filled in, as per LaTeX. The MathML entity for diamondsuit is mapped to U+2666) */
    public static final String CLUBSUIT = "\u2663";
    public static final String FLAT = "\u266d";
    public static final String NATURAL = "\u266e";
    public static final String SHARP = "\u266f";

    public static final String BIGODOT = "\u2a00";
    public static final String BIGOPLUS = "\u2a01";
    public static final String BIGOTIMES = "\u2a02";
    public static final String BIGUPLUS = "\u2a04";
    public static final String BIGSQCUP = "\u2a06";
    public static final String AMALG = "\u2a3f";
}
