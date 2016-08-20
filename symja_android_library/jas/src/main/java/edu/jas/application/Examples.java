/*
 * $Id$
 */

package edu.jas.application;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.Product;
import edu.jas.arith.ProductRing;
import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gbufd.GBFactory;
import edu.jas.gbufd.RGroebnerBasePseudoSeq;
import edu.jas.gbufd.RReductionSeq;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.Scripting;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.ufd.Quotient;
import edu.jas.ufd.QuotientRing;


/**
 * Examples for application usage.
 * @author Christoph Zengler.
 * @author Heinz Kredel.
 */

public class Examples {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        if (args.length > 0) {
            example1();
            example2();
            example3();
            example4();
        }
        example5();
        example6();
        example10();
        example11();
        example12();
        ComputerThreads.terminate();
    }


    /**
     * example1. cyclic n-th roots polynomial systems.
     */
    public static void example1() {
        int n = 4;

        BigInteger fac = new BigInteger();
        GenPolynomialRing<BigInteger> ring = new GenPolynomialRing<BigInteger>(fac, n); //,var);
        System.out.println("ring = " + ring + "\n");

        List<GenPolynomial<BigInteger>> cp = new ArrayList<GenPolynomial<BigInteger>>(n);
        for (int i = 1; i <= n; i++) {
            GenPolynomial<BigInteger> p = cyclicPoly(ring, n, i);
            cp.add(p);
            System.out.println("p[" + i + "] = " + p);
            System.out.println();
        }
        System.out.println("cp = " + cp + "\n");

        List<GenPolynomial<BigInteger>> gb;
        //GroebnerBase<BigInteger> sgb = new GroebnerBaseSeq<BigInteger>();
        GroebnerBase<BigInteger> sgb = GBFactory.getImplementation(fac);
        gb = sgb.GB(cp);
        System.out.println("gb = " + gb);

    }


    static GenPolynomial<BigInteger> cyclicPoly(GenPolynomialRing<BigInteger> ring, int n, int i) {

        List<? extends GenPolynomial<BigInteger>> X = /*(List<GenPolynomial<BigInteger>>)*/ring
                        .univariateList();

        GenPolynomial<BigInteger> p = ring.getZERO();
        for (int j = 1; j <= n; j++) {
            GenPolynomial<BigInteger> pi = ring.getONE();
            for (int k = j; k < j + i; k++) {
                pi = pi.multiply(X.get(k % n));
            }
            p = p.sum(pi);
            if (i == n) {
                p = p.subtract(ring.getONE());
                break;
            }
        }
        return p;
    }


    /**
     * example2. abtract types:
     * List<GenPolynomial<Product<Residue<BigRational>>>>.
     */
    public static void example2() {
        List<GenPolynomial<Product<Residue<BigRational>>>> L = null;
        L = new ArrayList<GenPolynomial<Product<Residue<BigRational>>>>();

        BigRational bfac = new BigRational(1);
        GenPolynomialRing<BigRational> pfac = null;
        pfac = new GenPolynomialRing<BigRational>(bfac, 3);

        List<GenPolynomial<BigRational>> F = null;
        F = new ArrayList<GenPolynomial<BigRational>>();

        GenPolynomial<BigRational> p = null;
        for (int i = 0; i < 2; i++) {
            p = pfac.random(5, 4, 3, 0.4f);
            if (!p.isConstant()) {
                F.add(p);
            }
        }
        //System.out.println("F = " + F);

        Ideal<BigRational> id = new Ideal<BigRational>(pfac, F);
        id.doGB();
        if (id.isONE() || id.isZERO()) {
            System.out.println("id zero or one = " + id);
            return;
        }
        ResidueRing<BigRational> rr = new ResidueRing<BigRational>(id);
        System.out.println("rr = " + rr);

        ProductRing<Residue<BigRational>> pr = null;
        pr = new ProductRing<Residue<BigRational>>(rr, 3);

        String[] vars = new String[] { "a", "b" };
        GenPolynomialRing<Product<Residue<BigRational>>> fac;
        fac = new GenPolynomialRing<Product<Residue<BigRational>>>(pr, 2, vars);

        GenPolynomial<Product<Residue<BigRational>>> pp;
        for (int i = 0; i < 1; i++) {
            pp = fac.random(2, 4, 4, 0.4f);
            if (!pp.isConstant()) {
                L.add(pp);
            }
        }
        System.out.println("L = " + L);

        //PolynomialList<Product<Residue<BigRational>>> Lp = null;
        //Lp = new PolynomialList<Product<Residue<BigRational>>>(fac,L);
        //System.out.println("Lp = " + Lp);

        GroebnerBase<Product<Residue<BigRational>>> bb = new RGroebnerBasePseudoSeq<Product<Residue<BigRational>>>(
                        pr);

        System.out.println("isGB(L) = " + bb.isGB(L));

        List<GenPolynomial<Product<Residue<BigRational>>>> G = null;

        G = bb.GB(L);
        System.out.println("G = " + G);
        System.out.println("isGB(G) = " + bb.isGB(G));
    }


    /**
     * example3. abtract types: GB of List<GenPolynomial<Residue<BigRational>>>.
     */
    public static void example3() {
        List<GenPolynomial<Residue<BigRational>>> L = null;
        L = new ArrayList<GenPolynomial<Residue<BigRational>>>();

        BigRational bfac = new BigRational(1);
        GenPolynomialRing<BigRational> pfac = null;
        pfac = new GenPolynomialRing<BigRational>(bfac, 2);

        List<GenPolynomial<BigRational>> F = null;
        F = new ArrayList<GenPolynomial<BigRational>>();

        GenPolynomial<BigRational> p = null;
        for (int i = 0; i < 2; i++) {
            p = pfac.random(5, 5, 5, 0.4f);
            //p = pfac.parse("x0^2 -2" );
            if (!p.isConstant()) {
                F.add(p);
            }
        }
        //System.out.println("F = " + F);

        Ideal<BigRational> id = new Ideal<BigRational>(pfac, F);
        id.doGB();
        if (id.isONE() || id.isZERO()) {
            System.out.println("id zero or one = " + id);
            return;
        }
        ResidueRing<BigRational> rr = new ResidueRing<BigRational>(id);
        System.out.println("rr = " + rr);

        String[] vars = new String[] { "a", "b" };
        GenPolynomialRing<Residue<BigRational>> fac;
        fac = new GenPolynomialRing<Residue<BigRational>>(rr, 2, vars);

        GenPolynomial<Residue<BigRational>> pp;
        for (int i = 0; i < 2; i++) {
            pp = fac.random(2, 4, 6, 0.2f);
            if (!pp.isConstant()) {
                L.add(pp);
            }
        }
        System.out.println("L = " + L);

        GroebnerBase<Residue<BigRational>> bb;
        //bb = new GroebnerBasePseudoSeq<Residue<BigRational>>(rr);
        bb = GBFactory.getImplementation(rr);

        System.out.println("isGB(L) = " + bb.isGB(L));

        List<GenPolynomial<Residue<BigRational>>> G = null;

        G = bb.GB(L);
        System.out.println("G = " + G);
        System.out.println("isGB(G) = " + bb.isGB(G));
    }


    /**
     * example4. abtract types: comprehensive GB of
     * List<GenPolynomial<GenPolynomial<BigRational>>>.
     */
    public static void example4() {
        int kl = 2;
        int ll = 3;
        int el = 3;
        float q = 0.2f; //0.4f
        GenPolynomialRing<BigRational> cfac;
        GenPolynomialRing<GenPolynomial<BigRational>> fac;

        List<GenPolynomial<GenPolynomial<BigRational>>> L;

        ComprehensiveGroebnerBaseSeq<BigRational> bb;

        GenPolynomial<GenPolynomial<BigRational>> a;
        GenPolynomial<GenPolynomial<BigRational>> b;
        GenPolynomial<GenPolynomial<BigRational>> c;

        BigRational coeff = new BigRational(kl);
        String[] cv = { "a", "b" };
        cfac = new GenPolynomialRing<BigRational>(coeff, 2, cv);
        String[] v = { "x", "y" };
        fac = new GenPolynomialRing<GenPolynomial<BigRational>>(cfac, 2, v);
        bb = new ComprehensiveGroebnerBaseSeq<BigRational>(coeff);

        L = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = a; //c = fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        System.out.println("CGB exam L = " + L);
        L = bb.GB(L);
        System.out.println("CGB( L )   = " + L);
        System.out.println("isCGB( L ) = " + bb.isGB(L));

        L.add(b);
        System.out.println("CGB exam L = " + L);
        L = bb.GB(L);
        System.out.println("CGB( L )   = " + L);
        System.out.println("isCGB( L ) = " + bb.isGB(L));

        L.add(c);
        System.out.println("CGB exam L = " + L);
        L = bb.GB(L);
        System.out.println("CGB( L )   = " + L);
        System.out.println("isCGB( L ) = " + bb.isGB(L));
    }


    /**
     * example5. comprehensive GB of
     * List<GenPolynomial<GenPolynomial<BigRational>>> and GB for regular ring.
     */
    public static void example5() {
        int kl = 2;
        int ll = 4;
        int el = 3;
        float q = 0.3f; //0.4f
        GenPolynomialRing<BigRational> cfac;
        GenPolynomialRing<GenPolynomial<BigRational>> fac;

        List<GenPolynomial<GenPolynomial<BigRational>>> L;

        ComprehensiveGroebnerBaseSeq<BigRational> bb;

        GenPolynomial<GenPolynomial<BigRational>> a;
        GenPolynomial<GenPolynomial<BigRational>> b;
        GenPolynomial<GenPolynomial<BigRational>> c;

        BigRational coeff = new BigRational(kl);
        String[] cv = { "a", "b" };
        cfac = new GenPolynomialRing<BigRational>(coeff, 2, cv);
        String[] v = { "x", "y" };
        fac = new GenPolynomialRing<GenPolynomial<BigRational>>(cfac, 2, v);
        bb = new ComprehensiveGroebnerBaseSeq<BigRational>(coeff);

        L = new ArrayList<GenPolynomial<GenPolynomial<BigRational>>>();

        a = fac.random(kl, ll, el, q);
        b = fac.random(kl, ll, el, q);
        c = a; //c = fac.random(kl, ll, el, q );

        if (a.isZERO() || b.isZERO() || c.isZERO()) {
            return;
        }

        L.add(a);
        L.add(b);
        L.add(c);
        System.out.println("CGB exam L = " + L);
        GroebnerSystem<BigRational> sys = bb.GBsys(L);
        boolean ig = bb.isGB(sys.getCGB());
        System.out.println("CGB( L )   = " + sys.getCGB());
        System.out.println("isCGB( L ) = " + ig);

        List<GenPolynomial<Product<Residue<BigRational>>>> Lr, bLr;
        RReductionSeq<Product<Residue<BigRational>>> res = new RReductionSeq<Product<Residue<BigRational>>>();

        Lr = PolyUtilApp.<BigRational> toProductRes(sys.list);
        bLr = res.booleanClosure(Lr);

        System.out.println("booleanClosed(Lr)   = " + bLr);

        if (bLr.size() > 0) {
            GroebnerBase<Product<Residue<BigRational>>> rbb = new RGroebnerBasePseudoSeq<Product<Residue<BigRational>>>(
                            bLr.get(0).ring.coFac);
            System.out.println("isRegularGB(Lr) = " + rbb.isGB(bLr));
        }
    }


    /**
     * Example GBase and real root.
     */
    @SuppressWarnings("cast")
    public static void example6() {
        BigRational coeff = new BigRational();
        GroebnerBase<BigRational> gb = GBFactory.getImplementation(coeff);

        String exam = "(x,y,z) L " + "( " + "( x^2 - 2 ), ( y^2 - 3 ), ( z^2 + x * y )" + ") ";
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

        PolynomialList<BigRational> Gp = new PolynomialList<BigRational>(F.ring, G);
        System.out.println("G = " + Gp);

        // compute real roots of the ideal
        Ideal<BigRational> I = new Ideal<BigRational>(Gp);
        List<IdealWithRealAlgebraicRoots<BigRational>> Ir = PolyUtilApp.<BigRational> realAlgebraicRoots(I);
        for (IdealWithRealAlgebraicRoots<BigRational> R : Ir) {
            R.doDecimalApproximation();
            for (List<BigDecimal> Dr : R.decimalApproximation()) {
                System.out.println(Dr.toString());
            }
            System.out.println();
        }
    }


    /**
     * example7. Coefficients in Boolean residue class ring.
     */
    public static void example7() {
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


        Ideal<ModInteger> fieldPolys = new Ideal<ModInteger>(z2p, fieldPolynomials);
        ResidueRing<ModInteger> ring = new ResidueRing<ModInteger>(fieldPolys);
        String[] mvars = { "mv3", "mv2", "mv1" };
        GenPolynomialRing<Residue<ModInteger>> ringp = new GenPolynomialRing<Residue<ModInteger>>(ring,
                        mvars.length, mvars);

        List<GenPolynomial<Residue<ModInteger>>> polynomials = new ArrayList<GenPolynomial<Residue<ModInteger>>>();

        GenPolynomial<Residue<ModInteger>> v1 = ringp.univariate(0);
        GenPolynomial<Residue<ModInteger>> v2 = ringp.univariate(1);
        GenPolynomial<Residue<ModInteger>> v3 = ringp.univariate(2);
        GenPolynomial<Residue<ModInteger>> notV1 = v1.sum(ringp.ONE);
        GenPolynomial<Residue<ModInteger>> notV2 = v2.sum(ringp.ONE);
        GenPolynomial<Residue<ModInteger>> notV3 = v3.sum(ringp.ONE);

        //v1*v2
        GenPolynomial<Residue<ModInteger>> p1 = v1.multiply(v2);

        //v1*v2 + v1 + v2 + 1
        GenPolynomial<Residue<ModInteger>> p2 = notV1.multiply(notV2);

        //v1*v3 + v1 + v3 + 1
        GenPolynomial<Residue<ModInteger>> p3 = notV1.multiply(notV3);

        polynomials.add(p1);
        polynomials.add(p2);
        polynomials.add(p3);

        //GroebnerBase<Residue<ModInteger>> gb = new GroebnerBasePseudoSeq<Residue<ModInteger>>(ring);
        GroebnerBase<Residue<ModInteger>> gb = GBFactory.getImplementation(ring);
        List<GenPolynomial<Residue<ModInteger>>> G = gb.GB(polynomials);

        System.out.println(G);
    }


    /**
     * example8. Coefficients in Boolean residue class ring with cuppling of
     * variables.
     */
    public static void example8() {
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


        Ideal<ModInteger> fieldPolys = new Ideal<ModInteger>(z2p, fieldPolynomials);
        ResidueRing<ModInteger> ring = new ResidueRing<ModInteger>(fieldPolys);
        String[] mvars = { "mv3", "mv2", "mv1" };
        GenPolynomialRing<Residue<ModInteger>> ringp = new GenPolynomialRing<Residue<ModInteger>>(ring,
                        mvars.length, mvars);

        List<GenPolynomial<Residue<ModInteger>>> polynomials = new ArrayList<GenPolynomial<Residue<ModInteger>>>();

        GenPolynomial<Residue<ModInteger>> v1 = ringp.univariate(0);
        GenPolynomial<Residue<ModInteger>> v2 = ringp.univariate(1);
        GenPolynomial<Residue<ModInteger>> v3 = ringp.univariate(2);
        GenPolynomial<Residue<ModInteger>> notV1 = v1.sum(ringp.ONE);
        GenPolynomial<Residue<ModInteger>> notV2 = v2.sum(ringp.ONE);
        GenPolynomial<Residue<ModInteger>> notV3 = v3.sum(ringp.ONE);

        //v1*v2
        GenPolynomial<Residue<ModInteger>> p1 = v1.multiply(v2);

        //v1*v2 + v1 + v2 + 1
        GenPolynomial<Residue<ModInteger>> p2 = notV1.multiply(notV2);

        //v1*v3 + v1 + v3 + 1
        GenPolynomial<Residue<ModInteger>> p3 = notV1.multiply(notV3);

        polynomials.add(p1);
        polynomials.add(p2);
        polynomials.add(p3);

        List<Residue<ModInteger>> gens = ring.generators();
        System.out.println("gens = " + gens);
        GenPolynomial<Residue<ModInteger>> mv3v3 = v3.subtract(gens.get(1));
        GenPolynomial<Residue<ModInteger>> mv2v2 = v2.subtract(gens.get(2));
        GenPolynomial<Residue<ModInteger>> mv1v1 = v1.subtract(gens.get(3));

        System.out.println("mv3v3 = " + mv3v3);
        System.out.println("mv2v2 = " + mv2v2);
        System.out.println("mv1v1 = " + mv1v1);

        polynomials.add(mv3v3);
        polynomials.add(mv2v2);
        polynomials.add(mv1v1);

        //GroebnerBase<Residue<ModInteger>> gb = new GroebnerBasePseudoSeq<Residue<ModInteger>>(ring);
        GroebnerBase<Residue<ModInteger>> gb = GBFactory.getImplementation(ring);

        List<GenPolynomial<Residue<ModInteger>>> G = gb.GB(polynomials);

        System.out.println(G);
    }


    /**
     * example9. Groebner base and dimension.
     */
    public static void example9() {
        String[] vars = { "d1", "d2", "d3", "p1a", "p1b", "p1c", "p2a", "p2b", "p2c", "p3a", "p3b", "p3c",
                "p4a", "p4b", "p4c", "A", "B", "C", "D" };

        BigRational br = new BigRational();
        GenPolynomialRing<BigRational> pring = new GenPolynomialRing<BigRational>(br, vars);
        //GenPolynomialRing<BigRational> pring = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(TermOrder.INVLEX), vars);

        GenPolynomial<BigRational> e1 = pring.parse("A*p1a+B*p1b+C*p1c+D"); // (1)
        GenPolynomial<BigRational> e2 = pring.parse("A*p2a+B*p2b+C*p2c+D"); // (2)
        GenPolynomial<BigRational> e3 = pring.parse("A*p3a+B*p3b+C*p3c+D"); // (3)
        GenPolynomial<BigRational> e4 = pring.parse("A*p4a+B*p4b+C*p4c+D"); // (4)
        GenPolynomial<BigRational> e5 = pring.parse("p2a-p3a"); // (5)
        GenPolynomial<BigRational> e6 = pring.parse("p2b-p3b"); // (6)
        GenPolynomial<BigRational> e7 = pring.parse("p2c-p3c"); // (7)
        GenPolynomial<BigRational> e8 = pring.parse("(p2a-p1a)^2+(p2b-p1b)^2+(p2c-p1c)^2-d1^2"); // (8)
        GenPolynomial<BigRational> e9 = pring.parse("(p4a-p3a)^2+(p4b-p3b)^2+(p4c-p3c)^2-d2^2"); // (9)

        List<GenPolynomial<BigRational>> cp = new ArrayList<GenPolynomial<BigRational>>(9);
        cp.add(e1);
        cp.add(e2);
        cp.add(e3);
        cp.add(e4);
        cp.add(e5);
        cp.add(e6);
        cp.add(e7);
        cp.add(e8);
        cp.add(e9);

        GenPolynomial<BigRational> e10 = pring.parse("(p4a-p1a)^2+(p4b-p1b)^2+(p4c-p1c)^2-d3^2"); // (10)
        cp.add(e10);

        List<GenPolynomial<BigRational>> gb;
        GroebnerBase<BigRational> sgb = GBFactory.getImplementation(br);
        gb = sgb.GB(cp);
        //System.out.println("gb = " + gb);

        PolynomialList<BigRational> pl = new PolynomialList<BigRational>(pring, gb);
        Ideal<BigRational> id = new Ideal<BigRational>(pl, true);
        System.out.println("cp = " + cp);
        System.out.println("id = " + id);

        Dimension dim = id.dimension();
        System.out.println("dim = " + dim);
    }


    /**
     * example10. abtract types: GB of
     * List<GenPolynomial<AlgebraicNumber<Quotient
     * <AlgebraicNumber<BigRational>>>>>.
     */
    public static void example10() {
        Scripting.setLang(Scripting.Lang.Ruby);
        BigRational bfac = new BigRational(1);
        GenPolynomialRing<BigRational> pfac;
        pfac = new GenPolynomialRing<BigRational>(bfac, new String[] { "w2" });
        System.out.println("pfac = " + pfac.toScript());

        // p = w2^2 - 2
        GenPolynomial<BigRational> p = pfac.univariate(0, 2).subtract(pfac.fromInteger(2L));
        System.out.println("p = " + p.toScript());

        AlgebraicNumberRing<BigRational> afac;
        afac = new AlgebraicNumberRing<BigRational>(p, true);
        System.out.println("afac = " + afac.toScript());

        GenPolynomialRing<AlgebraicNumber<BigRational>> pafac;
        pafac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(afac, new String[] { "x" });
        System.out.println("pafac = " + pafac.toScript());

        QuotientRing<AlgebraicNumber<BigRational>> qafac;
        qafac = new QuotientRing<AlgebraicNumber<BigRational>>(pafac);
        System.out.println("qafac = " + qafac.toScript());

        GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>> pqafac;
        pqafac = new GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>>(qafac, new String[] { "wx" });
        System.out.println("pqafac = " + pqafac.toScript());
        List<GenPolynomial<Quotient<AlgebraicNumber<BigRational>>>> qgen = pqafac.generators();
        System.out.println("qgen = " + qgen);

        // q = wx^2 - x
        GenPolynomial<Quotient<AlgebraicNumber<BigRational>>> q;
        q = pqafac.univariate(0, 2).subtract(qgen.get(2));
        System.out.println("q = " + q.toScript());

        AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>> aqafac;
        aqafac = new AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>>(q, true);
        System.out.println("aqafac = " + aqafac.toScript());

        GenPolynomialRing<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> paqafac;
        paqafac = new GenPolynomialRing<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>>(aqafac,
                        new String[] { "y", "z" });
        System.out.println("paqafac = " + paqafac.toScript());

        List<GenPolynomial<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>>> L;
        L = new ArrayList<GenPolynomial<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>>>();

        GenPolynomial<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> pp;
        /*
        for (int i = 0; i < 2; i++) {
            pp = paqafac.random(2, 3, 3, 0.2f);
            System.out.println("pp = " + pp.toScript());
            if (pp.isConstant()) {
                pp = paqafac.univariate(0,3);
            }
            L.add(pp);
        }
        */
        pp = paqafac.parse("(( y^2 - x )*( z^2 - 2 ) )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);
        pp = paqafac.parse("( y^2 z - x^3 z - w2*wx )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);
        //System.out.println("L = " + L);

        GroebnerBaseAbstract<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> bb;
        //bb = new GroebnerBaseSeq<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>>(); //aqafac);
        bb = GBFactory.getImplementation(aqafac);
        //bb = GBFactory.getProxy(aqafac);

        System.out.println("isGB(L) = " + bb.isGB(L));

        long t = System.currentTimeMillis();
        List<GenPolynomial<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>>> G = bb.GB(L);
        t = System.currentTimeMillis() - t;
        System.out.println("time = " + t + " milliseconds");
        //System.out.println("G = " + G);
        for (GenPolynomial<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> g : G) {
            System.out.println("g = " + g.toScript());
        }
        System.out.println("isGB(G) = " + bb.isGB(G));
        bb.terminate();
    }


    /**
     * example11. abtract types: GB of List<GenPolynomial<BigRational>>>.
     */
    public static void example11() {
        Scripting.setLang(Scripting.Lang.Ruby);
        BigRational bfac = new BigRational(1);
        GenPolynomialRing<BigRational> pfac;
        String[] vars = new String[] { "w2", "xi", "x", "wx", "y", "z" };
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        pfac = new GenPolynomialRing<BigRational>(bfac, vars, to);
        System.out.println("pfac = " + pfac.toScript());

        List<GenPolynomial<BigRational>> L = new ArrayList<GenPolynomial<BigRational>>();
        GenPolynomial<BigRational> pp;
        pp = pfac.parse("( w2^2 - 2 )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);
        pp = pfac.parse("( wx^2 - x )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);
        pp = pfac.parse("( xi * x - 1 )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);
        pp = pfac.parse("(( y^2 - x )*( z^2 - 2 ) )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);
        pp = pfac.parse("( y^2 z - x^3 z - w2*wx )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);

        GroebnerBaseAbstract<BigRational> bb;
        //bb = new GroebnerBaseSeq<BigRational>(); //bfac);
        bb = GBFactory.getImplementation(bfac);
        //bb = GBFactory.getProxy(bfac);

        System.out.println("isGB(L) = " + bb.isGB(L));
        long t = System.currentTimeMillis();
        List<GenPolynomial<BigRational>> G = bb.GB(L);
        t = System.currentTimeMillis() - t;
        System.out.println("time = " + t + " milliseconds");
        for (GenPolynomial<BigRational> g : G) {
            System.out.println("g = " + g.toScript());
        }
        System.out.println("isGB(G) = " + bb.isGB(G));
        bb.terminate();
    }


    /**
     * example12. abtract types: GB of
     * List<GenPolynomial<Quotient<BigRational>>>>.
     */
    public static void example12() {
        Scripting.setLang(Scripting.Lang.Ruby);
        BigRational bfac = new BigRational(1);
        GenPolynomialRing<BigRational> cfac;
        String[] cvars = new String[] { "x" };
        TermOrder to = new TermOrder(TermOrder.INVLEX);
        cfac = new GenPolynomialRing<BigRational>(bfac, cvars, to);
        System.out.println("cfac = " + cfac.toScript());

        QuotientRing<BigRational> qfac;
        qfac = new QuotientRing<BigRational>(cfac);
        System.out.println("qfac = " + qfac.toScript());

        String[] vars = new String[] { "w2", "wx", "y", "z" };
        GenPolynomialRing<Quotient<BigRational>> pfac;
        pfac = new GenPolynomialRing<Quotient<BigRational>>(qfac, vars, to);
        System.out.println("pfac = " + pfac.toScript());

        List<GenPolynomial<Quotient<BigRational>>> L = new ArrayList<GenPolynomial<Quotient<BigRational>>>();
        GenPolynomial<Quotient<BigRational>> pp;
        pp = pfac.parse("( w2^2 - 2 )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);
        pp = pfac.parse("( wx^2 - x )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);
        pp = pfac.parse("(( y^2 - x )*( z^2 - 2 ) )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);
        pp = pfac.parse("( y^2 z - x^3 z - w2*wx )");
        System.out.println("pp = " + pp.toScript());
        L.add(pp);

        GroebnerBaseAbstract<Quotient<BigRational>> bb;
        //bb = new GroebnerBaseSeq<Quotient<BigRational>>(); //bfac);

        // sequential
        bb = GBFactory.getImplementation(qfac);
        System.out.println("isGB(L) = " + bb.isGB(L));
        long t = System.currentTimeMillis();
        List<GenPolynomial<Quotient<BigRational>>> G = bb.GB(L);
        t = System.currentTimeMillis() - t;
        System.out.println("time = " + t + " milliseconds");
        for (GenPolynomial<Quotient<BigRational>> g : G) {
            System.out.println("g = " + g.toScript());
        }
        System.out.println("isGB(G) = " + bb.isGB(G));
        bb.terminate();

        // parallel
        bb = GBFactory.getProxy(qfac);
        System.out.println("isGB(L) = " + bb.isGB(L));
        t = System.currentTimeMillis();
        G = bb.GB(L);
        t = System.currentTimeMillis() - t;
        System.out.println("time = " + t + " milliseconds");
        for (GenPolynomial<Quotient<BigRational>> g : G) {
            System.out.println("g = " + g.toScript());
        }
        System.out.println("isGB(G) = " + bb.isGB(G));
        bb.terminate();

        // builder
        bb = GBAlgorithmBuilder.polynomialRing(pfac).fractionFree().syzygyPairlist().parallel(3).build();
        System.out.println("isGB(L) = " + bb.isGB(L));
        t = System.currentTimeMillis();
        G = bb.GB(L);
        t = System.currentTimeMillis() - t;
        System.out.println("time = " + t + " milliseconds");
        for (GenPolynomial<Quotient<BigRational>> g : G) {
            System.out.println("g = " + g.toScript());
        }
        System.out.println("isGB(G) = " + bb.isGB(G));
        bb.terminate();
    }
}
