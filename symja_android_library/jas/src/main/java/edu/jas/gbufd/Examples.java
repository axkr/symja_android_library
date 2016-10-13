/*
 * $Id$
 */

package edu.jas.gbufd;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.gb.GroebnerBase;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;


/**
 * Examples for Groebner base usage.
 * @author Christoph Zengler.
 * @author Heinz Kredel.
 */
public class Examples {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        //example1();
        //example2();
        //example3();
        exampleGB();
        //exampleGB1();
        //exampleGBTrinks();
    }


    /**
     * example1. Coefficients in Boolean residue class ring.
     * 
     */
    public static void example1() {
        // moved to edu.jas.application.Examples
    }


    /*
     * example2. Coefficients in Boolean residue class ring with cuppling of
     * variables.
     * 
     */
    public static void example2() {
        // moved to edu.jas.application.Examples
    }


    /**
     * example3. Coefficients in Boolean ring and additional idempotent
     * generators.
     * 
     */
    public static void example3() {
        String[] vars = { "v3", "v2", "v1" };

        ModIntegerRing z2 = new ModIntegerRing(2);
        GenPolynomialRing<ModInteger> z2p = new GenPolynomialRing<ModInteger>(z2, vars.length, new TermOrder(
                        TermOrder.INVLEX), vars);
        List<GenPolynomial<ModInteger>> fieldPolynomials = new ArrayList<GenPolynomial<ModInteger>>();

        //add v1^2 + v1, v2^2 + v2, v3^2 + v3 to fieldPolynomials
        for (int i = 0; i < vars.length; i++) {
            GenPolynomial<ModInteger> var = z2p.univariate(i);
            fieldPolynomials.add(var.multiply(var).sum(var));
        }


        List<GenPolynomial<ModInteger>> polynomials = new ArrayList<GenPolynomial<ModInteger>>();

        GenPolynomial<ModInteger> v1 = z2p.univariate(0);
        GenPolynomial<ModInteger> v2 = z2p.univariate(1);
        GenPolynomial<ModInteger> v3 = z2p.univariate(2);
        GenPolynomial<ModInteger> notV1 = v1.sum(z2p.ONE);
        GenPolynomial<ModInteger> notV2 = v2.sum(z2p.ONE);
        GenPolynomial<ModInteger> notV3 = v3.sum(z2p.ONE);

        //v1*v2
        GenPolynomial<ModInteger> p1 = v1.multiply(v2);

        //v1*v2 + v1 + v2 + 1
        GenPolynomial<ModInteger> p2 = notV1.multiply(notV2);

        //v1*v3 + v1 + v3 + 1
        GenPolynomial<ModInteger> p3 = notV1.multiply(notV3);

        polynomials.add(p1);
        polynomials.add(p2);
        polynomials.add(p3);

        polynomials.addAll(fieldPolynomials);

        //GroebnerBase<ModInteger> gb = new GroebnerBaseSeq<ModInteger>();
        GroebnerBase<ModInteger> gb = GBFactory.getImplementation(z2);

        List<GenPolynomial<ModInteger>> G = gb.GB(polynomials);

        System.out.println(G);
    }


    /**
     * Example GBase.
     * 
     */
    @SuppressWarnings("cast")
    static public void exampleGB1() {
        BigRational coeff = new BigRational();
        GroebnerBase<BigRational> gb = GBFactory.getImplementation(coeff);

        String exam = "(x1,x2,y) G " + "( " + "( x1 + x2 - 10 ), ( 2 x1 - x2 + 4 ) " + ") ";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        PolynomialList<BigRational> F = null;

        try {
            F = (PolynomialList<BigRational>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("F = " + F);

        List<GenPolynomial<BigRational>> G = gb.GB(F.list);

        PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring, G);
        System.out.println("G = " + trinks);
    }


    /**
     * Example GBase.
     * 
     */
    @SuppressWarnings("cast")
    static public void exampleGB() {
        BigRational coeff = new BigRational();
        GroebnerBase<BigRational> gb = GBFactory.getImplementation(coeff);

        String exam = "(x,y) G " + "( " + "( y - ( x^2 - 1 ) ) " + ") ";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        PolynomialList<BigRational> F = null;

        try {
            F = (PolynomialList<BigRational>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("F = " + F);

        List<GenPolynomial<BigRational>> G = gb.GB(F.list);

        PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring, G);
        System.out.println("G = " + trinks);
    }


    /**
     * Example Trinks GBase.
     * 
     */
    @SuppressWarnings("cast")
    static public void exampleGBTrinks() {
        BigRational coeff = new BigRational();
        GroebnerBase<BigRational> bb = GBFactory.getImplementation(coeff);

        String exam = "(B,S,T,Z,P,W) L " + "( " + "( 45 P + 35 S - 165 B - 36 ), "
                        + "( 35 P + 40 Z + 25 T - 27 S ), " + "( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), "
                        + "( - 9 W + 15 T P + 20 S Z ), " + "( P W + 2 T Z - 11 B**3 ), "
                        + "( 99 W - 11 B S + 3 B**2 ), " + "( B**2 + 33/50 B + 2673/10000 ) " + ") ";
        Reader source = new StringReader(exam);
        GenPolynomialTokenizer parser = new GenPolynomialTokenizer(source);
        PolynomialList<BigRational> F = null;

        try {
            F = (PolynomialList<BigRational>) parser.nextPolynomialSet();
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("F = " + F);

        List<GenPolynomial<BigRational>> G = bb.GB(F.list);

        PolynomialList<BigRational> trinks = new PolynomialList<BigRational>(F.ring, G);
        System.out.println("G = " + trinks);
    }

}
