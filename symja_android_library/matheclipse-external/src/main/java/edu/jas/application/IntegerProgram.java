/*
 * $Id$
 */

package edu.jas.application;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.arith.BigInteger;
import edu.jas.poly.ExpVector;
import edu.jas.poly.ExpVectorLong;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;


/**
 * Solution of Integer Programming problems using Groebner bases. Integer
 * Program is in standard form -&gt; minimization of given Equation plus
 * restrictions. See chapter 8 in Cox, Little, O'Shea "Using Algebraic 
 * Geometry", 1998.
 * @author Maximilian Nohr
 */
public class IntegerProgram implements java.io.Serializable {


    private static final Logger logger = LogManager.getLogger(IntegerProgram.class);


    private static boolean DEBUG = logger.isDebugEnabled(); //false;


    private boolean negVars;


    private boolean success;


    /* 
    Integer Program is in standard form -&gt; minimization of given 
    Equation + restrictions
    */
    int n; // # of variables including slack variables


    int m; // # of restrictions


    long[] C; // List of Coefficients c_1...c_n of objective function


    long[] B; // List of  b_1...b_m, restriction right hand side  


    long[][] A; // m x n Matrix of a_{11}....a_{mn}, restriction matrix


    long[] D; // Polynomial degrees 1...n


    long[][] Aa; // restriction matrix a_{11}..a_{mn} after Laurent transformation


    Ideal<BigInteger> I; //the Ideal


    Ideal<BigInteger> GB; // the Groebner base for the ideal


    TermOrder to; // the Term order for the GB


    PolynomialList<BigInteger> F; // The Polynomials that generate the Ideal


    GenPolynomial<BigInteger> S; // The Polynomial that is reduced for the Solution


    /**
     * Constructor. Use one instance for every new problem since solve() is not
     * reentrant.
     */
    public IntegerProgram() {
    }


    /**
     * Set DEBUG flag to parameter value.
     * @param b
     */
    public void setDebug(boolean b) {
        DEBUG = b;
    }


    /*
     * Setup the Ideal corresponding to the Integer Program. 
     */
    @SuppressWarnings("unchecked")
    private void createIdeal() {
        Aa = A.clone();
        negVars = negVarTest();
        String[] w = new String[n];
        String[] f = new String[n];
        String[] z = new String[m];
        String[] t = new String[n];
        StringBuilder sb = new StringBuilder();
        sb.append("Int(");

        if (negVars) { //A or B has negative values
            for (int i = 1; i <= n; i++) {
                w[i - 1] = "w" + i;
            }
            for (int i = 1; i <= m; i++) {
                z[i - 1] = "z" + i;
            }
            for (int i = 0; i < n; i++) {
                StringBuffer h = new StringBuffer("");
                long min = 0;
                for (int j = 0; j < m; j++) {
                    if (A[j][i] < min) {
                        min = A[j][i];
                    }
                }
                if (min < 0) {
                    long e = -min;
                    h.append("t^" + e + " * ");
                    for (int j = 0; j < m; j++) {
                        Aa[j][i] = A[j][i] + e;
                        h.append(z[j] + "^" + Aa[j][i] + " * ");
                    }
                } else {
                    for (int j = 0; j < m; j++) {
                        if (A[j][i] != 0) {
                            h.append(z[j] + "^" + A[j][i] + " * ");
                        }
                    }
                }
                f[i] = h.substring(0, h.length() - 3).toString();
            }
            setDeg();
            setTO();
            for (int i = 0; i < n; i++) {
                t[i] = f[i] + " - " + w[i];
            }
            sb.append("t");
            for (int i = 0; i < m; i++) {
                sb.append(",").append(z[i]);
            }
            for (int i = 0; i < n; i++) {
                sb.append(",").append(w[i]);
            }
            sb.append(") W ");
            //sb.append(to.weightToString().substring(6, to.weightToString().length()));
            sb.append(to.weightToString());
            sb.append(" ( ( t");
            for (int i = 0; i < m; i++) {
                sb.append(" * ").append(z[i]);
            }
            sb.append(" - 1 )");
            for (int i = 0; i < n; i++) {
                sb.append(", (").append(t[i]).append(" )");
            }
            sb.append(") ");

        } else { //if neither A nor B contain negative values
            for (int i = 1; i <= n; i++) {
                w[i - 1] = "w" + i;
            }
            for (int i = 1; i <= m; i++) {
                z[i - 1] = "z" + i;
            }
            for (int i = 0; i < n; i++) {
                StringBuffer h = new StringBuffer("");
                for (int j = 0; j < m; j++) {
                    if (A[j][i] != 0) {
                        h.append(z[j] + "^" + A[j][i] + " * ");
                    }
                }
                f[i] = h.substring(0, h.length() - 3).toString();
            }
            setDeg();
            setTO();
            for (int i = 0; i < n; i++) {
                t[i] = f[i] + " - " + w[i];
            }
            sb.append(z[0]);
            for (int i = 1; i < m; i++) {
                sb.append(",").append(z[i]);
            }
            for (int i = 0; i < n; i++) {
                sb.append(",").append(w[i]);
            }
            sb.append(") W ");
            //sb.append(to.weightToString().substring(6, to.weightToString().length()));
            sb.append(to.weightToString());
            sb.append(" ( (").append(t[0]).append(")");
            for (int i = 1; i < n; i++) {
                sb.append(", (").append(t[i]).append(" )");
            }
            sb.append(") ");
        }
        if (DEBUG) {
            logger.debug("list=" + sb.toString());
        }

        Reader source = new StringReader(sb.toString());
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        PolynomialList<BigInteger> F = null;

        try {
            F = (PolynomialList<BigInteger>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (DEBUG) {
            logger.debug("F=" + F);
        }
        I = new Ideal<BigInteger>(F);
        return;
    }


    /**
     * @return true if the last calculation had a solution, else false
     */
    public boolean getSuccess() {
        return success;
    }


    /**
     * Solve Integer Program.
     * @param A matrix of restrictions
     * @param B restrictions right hand side
     * @param C objective function
     * @return solution s, such that s*C -&gt; minimal and A*s = B, or s = null
     *         if no solution exists
     */
    public long[] solve(long[][] A, long[] B, long[] C) {
        this.A = Arrays.copyOf(A, A.length);
        this.B = Arrays.copyOf(B, B.length);
        this.C = Arrays.copyOf(C, C.length);
        this.n = A[0].length;
        this.m = A.length;
        D = new long[n];

        createIdeal();
        GB = I.GB();
        return solve(B);
    }


    /**
     * Solve Integer Program for new right hand side. Uses the GB (matrix A and
     * C) from the last calculation.
     * @param B restrictions right hand side
     * @return solution s, such that s*C -&gt; minimal and A*s = B, or s = null
     *         if no solution exists
     */
    public long[] solve(long[] B) {
        long[] returnMe = new long[n];
        if (B.length != m) {
            System.out.println("ERROR: Dimensions don't match: " + B.length + " != " + m);
            return returnMe;
        }
        long[] l;
        this.B = Arrays.copyOf(B, B.length);
        if (DEBUG) {
            logger.debug("GB=" + GB);
        }
        if (negVars) {
            l = new long[m + n + 1];
            long min = findMin(B);
            if (min < 0) {
                long r = -min;
                l[m + n] = r;
                for (int i = 0; i < m; i++) {
                    l[m + n - 1 - i] = B[i] + r;
                }
            } else {
                for (int i = 0; i < m; i++) {
                    l[m + n - 1 - i] = B[i];
                }
            }
        } else {
            l = new long[m + n];
            for (int i = 0; i < m; i++) {
                l[m + n - 1 - i] = B[i];
            }
        }
        ExpVector e = new ExpVectorLong(l);
        S = new GenPolynomial<BigInteger>(I.getRing(), e);
        S = GB.normalform(S);

        ExpVector E = S.exponentIterator().next();
        for (int i = 0; i < n; i++) {
            returnMe[n - 1 - i] = E.getVal(i);
        }
        success = true;
        for (int i = n; i < n + m; i++) {
            if (E.getVal(i) != 0) {
                success = false;
                break;
            }
        }
        if (success) {
            if (DEBUG) {
                logger.debug("The solution is: " + Arrays.toString(returnMe));
            }
        } else {
            logger.warn("The Problem does not have a feasible solution.");
            returnMe = null;
        }
        return returnMe;
    }


    /*
     * Set the degree.
     */
    private void setDeg() {
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                D[j] += Aa[i][j];
            }
        }
    }


    /*
     * Set the term order.
     */
    private void setTO() {
        int h;
        if (negVars) {//if A and/or B contains negative values
            h = m + n + 1;
        } else {
            h = m + n;
        }
        long[] u1 = new long[h];
        long[] u2 = new long[h];
        for (int i = 0; i < h - n; i++) { //m+1 because t needs another 1 
            u1[h - 1 - i] = 1;
        }
        long[] h1 = new long[h]; // help vectors to construct u2 out of
        long[] h2 = new long[h];
        for (int i = h - n; i < h; i++) {
            h1[i] = C[i - (h - n)];
            h2[i] = D[i - (h - n)];
        }
        long min = h1[0];
        for (int i = 0; i < h; i++) {
            u2[h - 1 - i] = h1[i] + h2[i];
            if (u2[h - 1 - i] < min) {
                min = u2[h - 1 - i];
            }
        }
        while (min < 0) {
            min = u2[0];
            for (int i = 0; i < h; i++) {
                u2[h - 1 - i] += h2[i];
                if (u2[h - 1 - i] < min) {
                    min = u2[h - 1 - i];
                }
            }
        }
        long[][] wv = { u1, u2 };
        to = new TermOrder(wv);
    }


    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Function to minimize:\n");

        char c = 'A'; // variables are named A, B, C, ...

        boolean plus = false;
        for (int i = 0; i < n; i++) {
            if (C[i] != 0) {
                if (C[i] < 0) {
                    sb.append("(").append(C[i]).append(")");
                    sb.append("*");
                } else if (C[i] != 1) {
                    sb.append(C[i]);
                    sb.append("*");
                }
                sb.append(c);
                sb.append(" + ");
                plus = true;
            }
            c++;
        }
        if (plus) {
            sb.delete(sb.lastIndexOf("+"), sb.length());
        }
        sb.append("\nunder the Restrictions:\n");
        for (int i = 0; i < m; i++) {
            c = 'A';
            //System.out.println("A["+i+"] = " + Arrays.toString(A[i]));
            plus = false;
            for (int j = 0; j < n; j++) {
                if (A[i][j] != 0) {
                    if (A[i][j] < 0) {
                        sb.append("(").append(A[i][j]).append(")");
                        sb.append("*");
                    } else if (A[i][j] != 1) {
                        sb.append(A[i][j]);
                        sb.append("*");
                    } 
                    sb.append(c);
                    sb.append(" + ");
                    plus = true;
                }
                c++;
            }
            if (plus) {
                sb.delete(sb.lastIndexOf("+"), sb.length()); 
            } else {
                sb.append("0 ");
            }
            sb.append("= ").append(B[i]).append("\n");
        }
        return sb.toString();
    }


    /*
     * Test for negative variables.
     * @return true if negative variables appear
     */
    private boolean negVarTest() {
        for (int i = 0; i < m; i++) {
            if (B[i] < 0) {
                return true;
            }
            for (int j = 0; j < n; j++) {
                if (A[i][j] < 0) {
                    return true;
                }

            }
        }
        return false;
    }


    /*
     * Find minimal element.
     * @param B vector of at least one element, B.length &gt;= 1 
     * @return minimal element of B
     */
    private long findMin(long[] B) {
        long min = B[0];
        for (int i = 1; i < B.length; i++) {
            if (B[i] < min) {
                min = B[i];
            }
        }
        return min;
    }

}
