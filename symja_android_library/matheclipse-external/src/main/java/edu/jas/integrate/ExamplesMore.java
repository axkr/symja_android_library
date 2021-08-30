/*
 * $Id$
 */

package edu.jas.integrate;


import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * More examples for integrating rational functions.
 * 
 * @author Youssef Elbarbary
 */

public class ExamplesMore {


    /**
     * Main program.
     * 
     * @param args
     */
    public static void main(String[] args) {
        exampleRothstein();
        exampleLazard();
        exampleCzichwoski();
        exampleBernoulli();
        ComputerThreads.terminate();
    }


    /**
     * Example for integrating a rational function using Rothstein-Trager
     * algorithm.
     */
    public static void exampleRothstein() {
        BigRational br = new BigRational(0);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(TermOrder.INVLEX), vars);

        QuotientRing<BigRational> qfac = new QuotientRing<BigRational>(fac);
        ElementaryIntegration<BigRational> eIntegrator = new ElementaryIntegration<BigRational>(br);

        GenPolynomial<BigRational> a = fac.parse("x^4 - 3 x^2 + 6");
        GenPolynomial<BigRational> d = fac.parse("x^6 - 5 x^4 + 5 x^2 + 4");
        // GenPolynomial<BigRational> a = fac.parse("36");
        // GenPolynomial<BigRational> d = fac.parse("x^5 - 2 x^4 - 2 x^3 + 4 x^2 + x - 2");
        // GenPolynomial<BigRational> a = fac.parse("8 x^9 + x^8 - 12 x^7 - 4 x^6 - 26 x^5 - 6 x^4 + 30 x^3 + 23 x^2 - 2 x - 7");
        // GenPolynomial<BigRational> d = fac.parse("x^10 - 2 x^8 - 2 x^7 - 4 x^6 + 7 x^4 + 10 x^3 + 3 x^2 - 4 x - 2");
        // GenPolynomial<BigRational> a = fac.parse("x^5 - x^4 + 4 x^3 + x^2 - x + 5");
        // GenPolynomial<BigRational> d = fac.parse("x^4 - 2 x^3 + 5 x^2 - 4 x + 4");

        Quotient<BigRational> q = new Quotient<BigRational>(qfac, a, d);
        eIntegrator.irredLogPart = true;

        double startTime = System.currentTimeMillis();
        edu.jas.integrate.QuotIntegral<BigRational> ret = eIntegrator.integrate(q);
        double endTime = System.currentTimeMillis();
        System.out.println("Rothstein took " + ((endTime - startTime) / 1000) + " seconds");
        System.out.println("Result: " + ret);

        boolean testAnswer = eIntegrator.isIntegral(ret);
        System.out.println(testAnswer);
    }


    /**
     * Example for integrating a rational function using Lazard algorithm.
     */
    public static void exampleLazard() {
        BigRational br = new BigRational(0);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(TermOrder.INVLEX), vars);

        QuotientRing<BigRational> qfac = new QuotientRing<BigRational>(fac);
        ElementaryIntegration<BigRational> eIntegratorLaz = new ElementaryIntegrationLazard<BigRational>(br);

        GenPolynomial<BigRational> a = fac.parse("x^4 - 3 x^2 + 6");
        GenPolynomial<BigRational> d = fac.parse("x^6 - 5 x^4 + 5 x^2 + 4");
        // GenPolynomial<BigRational> a = fac.parse("36");
        // GenPolynomial<BigRational> d = fac.parse("x^5 - 2 x^4 - 2 x^3 + 4 x^2 + x - 2");
        // GenPolynomial<BigRational> a = fac.parse("8 x^9 + x^8 - 12 x^7 - 4 x^6 - 26 x^5 - 6 x^4 + 30 x^3 + 23 x^2 - 2 x - 7");
        // GenPolynomial<BigRational> d = fac.parse("x^10 - 2 x^8 - 2 x^7 - 4 x^6 + 7 x^4 + 10 x^3 + 3 x^2 - 4 x - 2");
        // GenPolynomial<BigRational> a = fac.parse("x^5 - x^4 + 4 x^3 + x^2 - x + 5");
        // GenPolynomial<BigRational> d = fac.parse("x^4 - 2 x^3 + 5 x^2 - 4 x + 4");

        Quotient<BigRational> q = new Quotient<BigRational>(qfac, a, d);
        eIntegratorLaz.irredLogPart = true;

        double startTime = System.currentTimeMillis();
        QuotIntegral<BigRational> ret = eIntegratorLaz.integrate(q);
        double endTime = System.currentTimeMillis();
        System.out.println("Lazard took " + ((endTime - startTime) / 1000) + " seconds");
        System.out.println("Result: " + ret);

        boolean testAnswer = eIntegratorLaz.isIntegral(ret);
        System.out.println(testAnswer);
        // System.out.println("-----");
    }


    /**
     * Example for integrating a rational function using Czichowski algorithm.
     */
    public static void exampleCzichwoski() {
        BigRational br = new BigRational(0);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(TermOrder.INVLEX), vars);

        QuotientRing<BigRational> qfac = new QuotientRing<BigRational>(fac);
        ElementaryIntegration<BigRational> eIntegratorCzi = new ElementaryIntegrationCzichowski<BigRational>(
                        br);

        // GenPolynomial<BigRational> a = fac.parse("x^4 - 3 x^2 + 6");
        // GenPolynomial<BigRational> d = fac.parse("x^6 - 5 x^4 + 5 x^2 + 4");
        // GenPolynomial<BigRational> a = fac.parse("36");
        // GenPolynomial<BigRational> d = fac.parse("x^5 - 2 x^4 - 2 x^3 + 4 x^2 + x - 2");
        // GenPolynomial<BigRational> a = fac.parse("8 x^9 + x^8 - 12 x^7 - 4 x^6 - 26 x^5 - 6 x^4 + 30 x^3 + 23 x^2 - 2 x - 7");
        // GenPolynomial<BigRational> d = fac.parse("x^10 - 2 x^8 - 2 x^7 - 4 x^6 + 7 x^4 + 10 x^3 + 3 x^2 - 4 x - 2");
        GenPolynomial<BigRational> a = fac.parse("x^5 - x^4 + 4 x^3 + x^2 - x + 5");
        GenPolynomial<BigRational> d = fac.parse("x^4 - 2 x^3 + 5 x^2 - 4 x + 4");

        Quotient<BigRational> q = new Quotient<BigRational>(qfac, a, d);
        eIntegratorCzi.irredLogPart = true;

        double startTime = System.currentTimeMillis();
        QuotIntegral<BigRational> ret = eIntegratorCzi.integrate(q); //
        double endTime = System.currentTimeMillis();
        System.out.println("Czichowski took " + ((endTime - startTime) / 1000) + " seconds");
        System.out.println("Result: " + ret);

        boolean testAnswer = eIntegratorCzi.isIntegral(ret);
        System.out.println(testAnswer);
    }


    /**
     * Example for integrating a rational function using Bernoulli algorithm.
     */
    public static void exampleBernoulli() {
        BigRational br = new BigRational(0);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> fac;
        fac = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(TermOrder.INVLEX), vars);

        QuotientRing<BigRational> qfac = new QuotientRing<BigRational>(fac);
        ElementaryIntegration<BigRational> eIntegratorBer = new ElementaryIntegrationBernoulli<BigRational>(
                        br);

        GenPolynomial<BigRational> a = fac.parse("x^4 - 3 x^2 + 6");
        GenPolynomial<BigRational> d = fac.parse("x^6 - 5 x^4 + 5 x^2 + 4");
        // GenPolynomial<BigRational> a = fac.parse("36");
        // GenPolynomial<BigRational> d = fac.parse("x^5 - 2 x^4 - 2 x^3 + 4 x^2 + x - 2");
        // GenPolynomial<BigRational> a = fac.parse("x^5 - x^4 + 4 x^3 + x^2 - x + 5");
        // GenPolynomial<BigRational> d = fac.parse("x^4 - 2 x^3 + 5 x^2 - 4 x + 4");

        Quotient<BigRational> q = new Quotient<BigRational>(qfac, a, d);

        double startTime = System.currentTimeMillis();
        edu.jas.integrate.QuotIntegral<BigRational> ret = eIntegratorBer.integrate(q);
        double endTime = System.currentTimeMillis();
        System.out.println("Bernoulli took " + ((endTime - startTime) / 1000) + " seconds");
        System.out.println("Result: " + ret);

        // boolean testAnswer = eIntegratorBer.isIntegral(ret);
        // System.out.println(testAnswer);
    }

}
