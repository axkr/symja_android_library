/*
 * $Id$
 */

package edu.jas.integrate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gbufd.GBFactory;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.PolyUfdUtil;


/**
 * Method related to elementary integration. Czichowski integration based on
 * Groebner bases for the logarithmic part.
 * 
 * @author Youssef Elbarbary
 * @param <C> coefficient type
 */

public class ElementaryIntegrationCzichowski<C extends GcdRingElem<C>> extends ElementaryIntegration<C> {


    private static final Logger logger = LogManager.getLogger(ElementaryIntegrationCzichowski.class);


    /**
     * Engine for Groebner basis.
     */
    public final GroebnerBaseAbstract<C> red;


    /**
     * Constructor.
     */
    public ElementaryIntegrationCzichowski(RingFactory<C> br) {
        super(br);
        red = GBFactory.<C> getImplementation(br);
    }


    /**
     * Univariate GenPolynomial integration of the logaritmic part, Czichowski
     * 
     * @param A univariate GenPolynomial, deg(A) &lt; deg(P).
     * @param P univariate irreducible GenPolynomial. // gcd(A,P) == 1 automatic
     * @return logarithmic part container.
     */
    @Override
    public LogIntegral<C> integrateLogPart(GenPolynomial<C> A, GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            throw new IllegalArgumentException("P == null or P == 0");
        }
        // System.out.println("\nP_base_algeb_part = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException("only for univariate polynomials " + pfac);
        }
        if (!pfac.coFac.isField()) {
            throw new IllegalArgumentException("only for field coefficients " + pfac);
        }
        List<C> cfactors = new ArrayList<C>();
        List<GenPolynomial<C>> cdenom = new ArrayList<GenPolynomial<C>>();
        List<AlgebraicNumber<C>> afactors = new ArrayList<AlgebraicNumber<C>>();
        List<GenPolynomial<AlgebraicNumber<C>>> adenom = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();

        // P linear
        if (P.degree(0) <= 1) {
            cfactors.add(A.leadingBaseCoefficient());
            cdenom.add(P);
            return new LogIntegral<C>(A, P, cfactors, cdenom, afactors, adenom);
        }

        // deriviative
        GenPolynomial<C> Pp = PolyUtil.<C> baseDeriviative(P);
        // no: Pp = Pp.monic();
        // System.out.println("Pp = " + Pp);

        // Q[t]
        String[] vars = new String[] { "t" };
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(pfac.coFac, 1, pfac.tord, vars);
        GenPolynomial<C> t = cfac.univariate(0);

        // Q[x][t]
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(pfac, cfac); // sic
        // System.out.println("rfac = " + rfac.toScript());

        // transform polynomials to bi-variate polynomial
        GenPolynomial<GenPolynomial<C>> Ac = PolyUfdUtil.<C> introduceLowerVariable(rfac, A);
        // System.out.println("Ac = " + Ac);
        GenPolynomial<GenPolynomial<C>> Pc = PolyUfdUtil.<C> introduceLowerVariable(rfac, P);
        // System.out.println("Pc = " + Pc);
        GenPolynomial<GenPolynomial<C>> Pcp = PolyUfdUtil.<C> introduceLowerVariable(rfac, Pp);
        // System.out.println("Pcp = " + Pcp);

        // Q[t][x]
        GenPolynomialRing<GenPolynomial<C>> rfac1 = Pc.ring;
        // System.out.println("rfac1 = " + rfac1.toScript());

        // A - t P'
        GenPolynomial<GenPolynomial<C>> tc = rfac1.getONE().multiply(t);
        // System.out.println("tc = " + tc);
        GenPolynomial<GenPolynomial<C>> At = Ac.subtract(tc.multiply(Pcp));
        // System.out.println("At = " + At);

        // Q[t][x] to Q[t,x]
        GenPolynomialRing<C> dfac = pfac.distribute();
        GenPolynomial<C> Atd = PolyUtil.distribute(dfac, At);
        GenPolynomial<C> Pcd = PolyUtil.distribute(dfac, Pc);

        // Groebner Basis
        List<GenPolynomial<C>> myList = new ArrayList<GenPolynomial<C>>();
        myList.add(Atd);
        myList.add(Pcd);
        List<GenPolynomial<C>> mGB = red.GB(myList);
        Collections.sort(mGB); // OrderedPolynomialList

        // Q[t,x] to Q[t][x]
        List<GenPolynomial<GenPolynomial<C>>> gbList = PolyUtil.recursive(rfac1, mGB);

        // Content & Primitive Part
        int counter = 1;
        for (GenPolynomial<GenPolynomial<C>> tmGB : gbList) {
            if (counter == gbList.size()) {
                continue;
            }
            GenPolynomial<C> content = ufd.recursiveContent(tmGB);

            // Content of GB i+1
            GenPolynomial<C> c = ufd.recursiveContent(gbList.get(counter));
            GenPolynomial<C> Q = content.divide(c);

            // Primitive Part of GB i+1
            GenPolynomial<GenPolynomial<C>> ppS = ufd.baseRecursivePrimitivePart(gbList.get(counter));
            // System.out.println("pp(S) = " + ppS);
            counter++;

            // vars = new String[] { "z_" + Math.abs(r.hashCode() % 1000) };
            vars = pfac.newVars("z_");
            pfac = pfac.copy();
            @SuppressWarnings("unused")
            String[] unused = pfac.setVars(vars);
            if (Q.degreeMin() == 1) {
                Q = Q.divide(t);
            }
            Q = pfac.copy(Q); // hack to exchange the variables
            AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(Q);
            logger.debug("afac = " + afac.toScript());
            AlgebraicNumber<C> a = afac.getGenerator();
            // no: a = a.negate();
            // System.out.println("a = " + a);


            // K(alpha)[x]
            GenPolynomialRing<AlgebraicNumber<C>> pafac = new GenPolynomialRing<AlgebraicNumber<C>>(afac,
                            ppS.ring);
            // System.out.println("pafac = " + pafac.toScript());

            // convert to K(alpha)[x]
            GenPolynomial<AlgebraicNumber<C>> Sa = PolyUtil.convertRecursiveToAlgebraicCoefficients(pafac,
                            ppS);
            // System.out.println("Sa = " + Sa);

            afactors.add(a);
            adenom.add(Sa);
            // adenom.add(Sa.monic());
        }
        return new LogIntegral<C>(A, P, cfactors, cdenom, afactors, adenom);
    }

}
