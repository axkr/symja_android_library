/*
 * $Id$
 */

package edu.jas.gbufd;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;


/**
 * Characteristic Set class acccording to the simple algorithm, where the
 * leading coefficients are <strong>not</strong> rereduced. Implements methods
 * for Characteristic Sets and tests.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class CharacteristicSetSimple<C extends GcdRingElem<C>> implements CharacteristicSet<C> {


    private static final Logger logger = Logger.getLogger(CharacteristicSetSimple.class);


    private static boolean debug = logger.isDebugEnabled();


    /**
     * Characteristic set. According to the simple algorithm. The leading
     * coefficients are <strong>not</strong> rereduced.
     * @param A list of generic polynomials.
     * @return charSetWu(A).
     */
    public List<GenPolynomial<C>> characteristicSet(List<GenPolynomial<C>> A) {
        List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>();
        if (A == null || A.isEmpty()) {
            return S;
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        if (pfac.nvar <= 1) { // take gcd
            GreatestCommonDivisorAbstract<C> ufd = GCDFactory.<C> getImplementation(pfac.coFac);
            GenPolynomial<C> g = ufd.gcd(A).monic();
            logger.info("charSet base gcd = " + g);
            S.add(g);
            return S;
        }
        // sort polynomials according to the main variable
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        List<GenPolynomial<GenPolynomial<C>>> positiveDeg = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        List<GenPolynomial<C>> zeroDeg = new ArrayList<GenPolynomial<C>>();
        for (GenPolynomial<C> f : A) {
            if (f.isZERO()) {
                continue;
            }
            f = f.monic();
            if (f.isONE()) {
                S.add(f);
                return S;
            }
            GenPolynomial<GenPolynomial<C>> fr = PolyUtil.<C> recursive(rfac, f);
            if (fr.degree(0) == 0) {
                zeroDeg.add(fr.leadingBaseCoefficient());
            } else {
                positiveDeg.add(fr);
            }
        }
        if (positiveDeg.isEmpty() && zeroDeg.isEmpty()) {
            return S;
        }
        // do pseudo division wrt. the main variable
        OrderedPolynomialList<GenPolynomial<C>> opl = new OrderedPolynomialList<GenPolynomial<C>>(rfac,
                        positiveDeg);
        List<GenPolynomial<GenPolynomial<C>>> pd = new ArrayList<GenPolynomial<GenPolynomial<C>>>(opl.list);
        Collections.reverse(pd); // change OrderedPolynomialList to avoid
        if (debug) {
            logger.info("positive degrees: " + pd);
        }
        //System.out.println("positive degrees: " + pd);
        //System.out.println("zero     degrees: " + zeroDeg);
        while (pd.size() > 1) {
            GenPolynomial<GenPolynomial<C>> fr = pd.remove(0);
            GenPolynomial<GenPolynomial<C>> qr = pd.get(0); // = get(1)
            logger.info("pseudo remainder by deg = " + qr.degree() + " in variable " + rfac.getVars()[0]);
            GenPolynomial<GenPolynomial<C>> rr = PolyUtil.<C> recursiveSparsePseudoRemainder(fr, qr);
            if (rr.isZERO()) {
                logger.warn("variety is reducible");
                // replace qr by gcd(qr,fr) ?
                continue;
            }
            if (rr.degree(0) == 0) {
                zeroDeg.add(rr.leadingBaseCoefficient().monic());
            } else {
                pd.add(rr);
                pd = OrderedPolynomialList.sort(rfac, pd);
                Collections.reverse(pd); // avoid
            }
        }
        // recursion for degree zero polynomials
        List<GenPolynomial<C>> Sp = characteristicSet(zeroDeg); // recursion
        for (GenPolynomial<C> f : Sp) {
            GenPolynomial<C> fp = f.extend(pfac, 0, 0L);
            S.add(fp);
        }
        //logger.info("charSet recursion, Sp = " + Sp);
        if (pd.isEmpty()) {
            return S;
        }
        GenPolynomial<GenPolynomial<C>> rr = pd.get(0);
        GenPolynomial<C> sr = PolyUtil.<C> distribute(pfac, rr);
        sr = sr.monic();
        // no rereduction of leading coefficient wrt. characteristic set.
        S.add(0, sr);
        return S;
    }


    /**
     * Characteristic set test.
     * @param A list of generic polynomials.
     * @return true, if A is (at least a simple) characteristic set, else false.
     */
    public boolean isCharacteristicSet(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return true; // ?
        }
        GenPolynomialRing<C> pfac = A.get(0).ring;
        if (pfac.nvar <= 1) {
            return A.size() <= 1;
        }
        if (pfac.nvar < A.size()) {
            return false;
        }
        // select polynomials according to the main variable
        GenPolynomialRing<GenPolynomial<C>> rfac = pfac.recursive(1);
        List<GenPolynomial<C>> zeroDeg = new ArrayList<GenPolynomial<C>>();
        int positiveDeg = 0;
        for (GenPolynomial<C> f : A) {
            if (f.isZERO()) {
                return false;
            }
            //f = f.monic();
            GenPolynomial<GenPolynomial<C>> fr = PolyUtil.<C> recursive(rfac, f);
            if (fr.degree(0) == 0) {
                zeroDeg.add(fr.leadingBaseCoefficient());
            } else {
                positiveDeg++;
                if (positiveDeg > 1) {
                    return false;
                }
            }
        }
        return isCharacteristicSet(zeroDeg);
    }


    /**
     * Characteristic set reduction. Pseudo remainder wrt. the main variable.
     * @param P generic polynomial.
     * @param A list of generic polynomials as characteristic set.
     * @return characteristicSetRemainder(A,P)
     */
    public GenPolynomial<C> characteristicSetReduction(List<GenPolynomial<C>> A, GenPolynomial<C> P) {
        if (A == null || A.isEmpty()) {
            return P.monic();
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomial<C> R = PolyGBUtil.<C> topPseudoRemainder(A, P);
        R = R.monic();
        //System.out.println("remainder, R = " + R);
        return R;
    }

}
