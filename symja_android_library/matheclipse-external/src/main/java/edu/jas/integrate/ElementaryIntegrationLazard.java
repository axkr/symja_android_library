/*
 * $Id$
 */

package edu.jas.integrate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorSubres;
import edu.jas.ufd.PolyUfdUtil;


/**
 * Method related to elementary integration. Lazard-Rioboo-Trager integration of
 * the logarithmic part.
 * 
 * @author Youssef Elbarbary
 * @param <C> coefficient type
 */

public class ElementaryIntegrationLazard<C extends GcdRingElem<C>> extends ElementaryIntegration<C> {


    private static final Logger logger = LogManager.getLogger(ElementaryIntegrationLazard.class);


    /**
     * Constructor.
     */
    public ElementaryIntegrationLazard(RingFactory<C> br) {
        super(br);
    }


    /**
     * Univariate GenPolynomial integration of the logarithmic part, Lazard -
     * Rioboo - Trager
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

        // derivative
        GenPolynomial<C> Pp = PolyUtil.<C> baseDerivative(P);
        // no: Pp = Pp.monic();
        // System.out.println("\nP = " + P);

        // Q[t]
        String[] vars = new String[] { "t" };
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(pfac.coFac, 1, pfac.tord, vars);
        GenPolynomial<C> t = cfac.univariate(0);
        // System.out.println("t = " + t);

        // Q[x][t]
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(pfac, cfac); // sic
        // System.out.println("rfac = " + rfac.toScript());

        // transform polynomials to bi-variate polynomial
        GenPolynomial<GenPolynomial<C>> Ac = PolyUfdUtil.<C> introduceLowerVariable(rfac, A);
        GenPolynomial<GenPolynomial<C>> Pc = PolyUfdUtil.<C> introduceLowerVariable(rfac, P);
        GenPolynomial<GenPolynomial<C>> Pcp = PolyUfdUtil.<C> introduceLowerVariable(rfac, Pp);

        // Q[t][x]
        GenPolynomialRing<GenPolynomial<C>> rfac1 = Pc.ring;
        // System.out.println("rfac1 = " + rfac1.toScript());

        // A - t P'
        GenPolynomial<GenPolynomial<C>> tc = rfac1.getONE().multiply(t);
        // System.out.println("tc = " + tc);
        GenPolynomial<GenPolynomial<C>> At = Ac.subtract(tc.multiply(Pcp));
        // System.out.println("A - tP' = " + At);

        GreatestCommonDivisorSubres<C> engine = new GreatestCommonDivisorSubres<C>();
        // = GCDFactory.<C>getImplementation( cfac.coFac );
        GreatestCommonDivisorAbstract<AlgebraicNumber<C>> aengine = null;

        // Subresultant von A - t P'
        List<GenPolynomial<GenPolynomial<C>>> RcList = engine.recursiveUnivariateSubResultantList(Pc, At); // returning
        GenPolynomial<GenPolynomial<C>> Rc = RcList.get(RcList.size() - 1); // just getting R
        //System.out.println("R = " + Rc);

        // SquareFree(R)
        SortedMap<GenPolynomial<C>, Long> resSquareFree = sqf.squarefreeFactors(Rc.leadingBaseCoefficient());
        logger.info("SquareFree(R) = {}", resSquareFree);

        // First Loop
        SortedMap<GenPolynomial<C>, Long> sfactors = null;
        GenPolynomial<AlgebraicNumber<C>> Sa = null;
        GenPolynomial<GenPolynomial<C>> S = null;
        GenPolynomial<C> Q = null;
        for (Entry<GenPolynomial<C>, Long> qi : resSquareFree.entrySet()) {
            Q = qi.getKey();
            // System.out.println("\nr(t) = " + r);
            if (Q.isConstant()) {
                continue;
            }
            if (Q.degreeMin() == 1) {
                Q = Q.divide(t);
            }
            vars = pfac.newVars("z_");
            pfac = pfac.copy();
            @SuppressWarnings("unused")
            String[] unused = pfac.setVars(vars);
            GenPolynomial<C> qi2 = pfac.copy(Q); // hack to exchange the variables
            // System.out.println("r(z_) = " + r);
            AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(qi2);
            logger.debug("afac = {}", afac); //.toScript()
            AlgebraicNumber<C> a = afac.getGenerator();
            // no: a = a.negate();
            // System.out.println("a = " + a);

            // K(alpha)[x]
            GenPolynomialRing<AlgebraicNumber<C>> pafac = new GenPolynomialRing<AlgebraicNumber<C>>(afac,
                            Pc.ring);

            // 1Condition 2Condition i = deg(D) + Check condition again!
            if (qi2.degree() > 0) {
                if (qi.getValue() == Pc.degree()) {
                    GenPolynomial<C> sExp = P;
                    // convert to K(alpha)[x]
                    Sa = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, sExp);
                    afactors.add(a);
                    adenom.add(Sa);
                    return new LogIntegral<C>(A, P, cfactors, cdenom, afactors, adenom);
                }
                int countj = 1;
                // RcList.remove(RcList.size()-1);
                for (GenPolynomial<GenPolynomial<C>> Ri : RcList) {
                    if (qi.getValue() == Ri.degree()) {
                        S = Ri;
                        // convert to K(alpha)[x]
                        Sa = PolyUtil.convertRecursiveToAlgebraicCoefficients(pafac, S);
                        sfactors = sqf.squarefreeFactors(S.leadingBaseCoefficient());
                        logger.info("SquareFree(S){}", sfactors);
                    }
                }
                for (GenPolynomial<C> ai : sfactors.keySet()) {
                    GenPolynomial<AlgebraicNumber<C>> aiC = PolyUtil.<C> convertToAlgebraicCoefficients(pafac,
                                    ai);
                    GenPolynomial<AlgebraicNumber<C>> qiC = PolyUtil.<C> convertToAlgebraicCoefficients(pafac,
                                    qi2);
                    if (aengine == null) {
                        aengine = GCDFactory.<AlgebraicNumber<C>> getImplementation(afac);
                    }
                    GenPolynomial<AlgebraicNumber<C>> gcd = aengine.baseGcd(aiC, qiC);
                    gcd = gcd.power(countj);
                    Sa = (Sa.divide(gcd));
                    countj++;
                    if (Sa.isZERO() || a.isZERO()) {
                        System.out.println("warning constant Sa ignored");
                        continue;
                    }
                }
                //System.out.println("S = " + Sa);
                afactors.add(a);
                adenom.add(Sa.monic());
                // adenom.add(Sa.monic());
            }
        }
        return new LogIntegral<C>(A, P, cfactors, cdenom, afactors, adenom);
    }
}
