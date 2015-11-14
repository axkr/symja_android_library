/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
// import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Complex roots implemented by Sturm sequences. Algorithms use exact method
 * derived from Wilf's numeric Routh-Hurwitz method.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class ComplexRootsSturm<C extends RingElem<C> & Rational> extends ComplexRootsAbstract<C> {


    private static final Logger logger = Logger.getLogger(ComplexRootsSturm.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     * @param cf coefficient factory.
     */
    public ComplexRootsSturm(RingFactory<Complex<C>> cf) {
        super(cf);
        //ufd = GCDFactory.<Complex<C>> getImplementation(cf);
    }


    /**
     * Cauchy index of rational function f/g on interval.
     * @param a interval bound for I = [a,b].
     * @param b interval bound for I = [a,b].
     * @param f univariate polynomial.
     * @param g univariate polynomial.
     * @return winding number of f/g in I.
     */
    public long indexOfCauchy(C a, C b, GenPolynomial<C> f, GenPolynomial<C> g) {
        List<GenPolynomial<C>> S = sturmSequence(g, f);
        //System.out.println("S = " + S);
        if (debug) {
            logger.info("sturmSeq = " + S);
        }
        RingFactory<C> cfac = f.ring.coFac;
        List<C> l = PolyUtil.<C> evaluateMain(cfac, S, a);
        List<C> r = PolyUtil.<C> evaluateMain(cfac, S, b);
        long v = RootUtil.<C> signVar(l) - RootUtil.<C> signVar(r);
        //System.out.println("v = " + v);
        //         if (v < 0L) {
        //             v = -v;
        //         }
        return v;
    }


    /**
     * Routh index of complex function f + i g on interval.
     * @param a interval bound for I = [a,b].
     * @param b interval bound for I = [a,b].
     * @param f univariate polynomial.
     * @param g univariate polynomial != 0.
     * @return index number of f + i g.
     */
    public long[] indexOfRouth(C a, C b, GenPolynomial<C> f, GenPolynomial<C> g) {
        List<GenPolynomial<C>> S = sturmSequence(f, g);
        //System.out.println("S = " + S);
        RingFactory<C> cfac = f.ring.coFac;
        List<C> l = PolyUtil.<C> evaluateMain(cfac, S, a);
        List<C> r = PolyUtil.<C> evaluateMain(cfac, S, b);
        long v = RootUtil.<C> signVar(l) - RootUtil.<C> signVar(r);
        //System.out.println("v = " + v);

        long d = f.degree(0);
        if (d < g.degree(0)) {
            d = g.degree(0);
        }
        //System.out.println("d = " + d);
        long ui = (d - v) / 2;
        long li = (d + v) / 2;
        //System.out.println("upper = " + ui);
        //System.out.println("lower = " + li);
        return new long[] { ui, li };
    }


    /**
     * Sturm sequence.
     * @param f univariate polynomial.
     * @param g univariate polynomial.
     * @return a Sturm sequence for f and g.
     */
    public List<GenPolynomial<C>> sturmSequence(GenPolynomial<C> f, GenPolynomial<C> g) {
        List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>();
        if (f == null || f.isZERO()) {
            return S;
        }
        if (f.isConstant()) {
            S.add(f.monic());
            return S;
        }
        GenPolynomial<C> F = f;
        S.add(F);
        GenPolynomial<C> G = g; //PolyUtil.<C> baseDeriviative(f);
        while (!G.isZERO()) {
            GenPolynomial<C> r = F.remainder(G);
            F = G;
            G = r.negate();
            S.add(F/*.monic()*/);
        }
        //System.out.println("F = " + F);
        if (F.isConstant()) {
            return S;
        }
        // make squarefree
        List<GenPolynomial<C>> Sp = new ArrayList<GenPolynomial<C>>(S.size());
        for (GenPolynomial<C> p : S) {
            p = p.divide(F);
            Sp.add(p);
        }
        return Sp;
    }


    /**
     * Complex root count of complex polynomial on rectangle.
     * @param rect rectangle.
     * @param a univariate complex polynomial.
     * @return root count of a in rectangle.
     */
    @Override
    public long complexRootCount(Rectangle<C> rect, GenPolynomial<Complex<C>> a)
                    throws InvalidBoundaryException {
        C rl = rect.lengthReal();
        C il = rect.lengthImag();
        // only linear polynomials have zero length intervals
        if (rl.isZERO() && il.isZERO()) {
            Complex<C> e = PolyUtil.<Complex<C>> evaluateMain(a.ring.coFac, a, rect.getSW());
            if (e.isZERO()) {
                return 1;
            }
            return 0;
        }
        if (rl.isZERO() || il.isZERO()) {
            //RingFactory<C> cf = (RingFactory<C>) rl.factory();
            //GenPolynomialRing<C> rfac = new GenPolynomialRing<C>(cf,a.ring);
            //cf = (RingFactory<C>) il.factory();
            //GenPolynomialRing<C> ifac = new GenPolynomialRing<C>(cf,a.ring);
            //GenPolynomial<C> rp = PolyUtil.<C> realPartFromComplex(rfac, a);
            //GenPolynomial<C> ip = PolyUtil.<C> imaginaryPartFromComplex(ifac, a);
            //RealRoots<C> rr = new RealRootsSturm<C>();
            if (rl.isZERO()) {
                //logger.info("lengthReal == 0: " + rect);
                //Complex<C> r = rect.getSW();
                //r = new Complex<C>(r.ring,r.getRe()/*,0*/);
                //Complex<C> e = PolyUtil.<Complex<C>> evaluateMain(a.ring.coFac, a, r);
                //logger.info("a(re(rect)): " + e);
                //if ( !e.getRe().isZERO() ) {
                //    return 0;
                //}
                //C ev = PolyUtil.<C> evaluateMain(rp.ring.coFac, rp, rl);
                //logger.info("re(a)(re(rect)): " + ev);
                //Interval<C> iv = new Interval<C>(rect.getSW().getIm(),rect.getNE().getIm());
                //logger.info("iv: " + iv);
                //long ic = rr.realRootCount(iv,ip);
                //logger.info("ic: " + ic);

                Complex<C> sw = rect.getSW();
                Complex<C> ne = rect.getNE();
                C delta = sw.ring.ring.parse("1"); // works since linear polynomial
                Complex<C> cd = new Complex<C>(sw.ring, delta/*, 0*/);
                sw = sw.subtract(cd);
                ne = ne.sum(cd);
                rect = rect.exchangeSW(sw);
                rect = rect.exchangeNE(ne);
                logger.info("new rectangle: " + rect.toScript());
            }
            if (il.isZERO()) {
                //logger.info("lengthImag == 0: " + rect);
                //Interval<C> rv = new Interval<C>(rect.getSW().getRe(),rect.getNE().getRe());
                //logger.info("rv: " + rv);
                //long rc = rr.realRootCount(rv,rp);
                //logger.info("rc: " + rc);

                Complex<C> sw = rect.getSW();
                Complex<C> ne = rect.getNE();
                C delta = sw.ring.ring.parse("1"); // works since linear polynomial
                Complex<C> cd = new Complex<C>(sw.ring, sw.ring.ring.getZERO(), delta);
                sw = sw.subtract(cd);
                ne = ne.sum(cd);
                rect = rect.exchangeSW(sw);
                rect = rect.exchangeNE(ne);
                logger.info("new rectangle: " + rect.toScript());
            }
        }
        return windingNumber(rect, a);
    }


    /**
     * Winding number of complex function A on rectangle.
     * @param rect rectangle.
     * @param A univariate complex polynomial.
     * @return winding number of A arround rect.
     */
    public long windingNumber(Rectangle<C> rect, GenPolynomial<Complex<C>> A) throws InvalidBoundaryException {
        Boundary<C> bound = new Boundary<C>(rect, A); // throws InvalidBoundaryException
        ComplexRing<C> cr = (ComplexRing<C>) A.ring.coFac;
        RingFactory<C> cf = cr.ring;
        C zero = cf.getZERO();
        C one = cf.getONE();
        long ix = 0L;
        for (int i = 0; i < 4; i++) {
            long ci = indexOfCauchy(zero, one, bound.getRealPart(i), bound.getImagPart(i));
            //System.out.println("ci["+i+","+(i+1)+"] = " + ci);
            ix += ci;
        }
        if (ix % 2L != 0) {
            throw new InvalidBoundaryException("odd winding number " + ix);
        }
        return ix / 2L;
    }


    /**
     * List of complex roots of complex polynomial a on rectangle.
     * @param rect rectangle.
     * @param a univariate squarefree complex polynomial.
     * @return list of complex roots.
     */
    @SuppressWarnings("cast")
    @Override
    public List<Rectangle<C>> complexRoots(Rectangle<C> rect, GenPolynomial<Complex<C>> a)
                    throws InvalidBoundaryException {
        ComplexRing<C> cr = (ComplexRing<C>) a.ring.coFac;
        List<Rectangle<C>> roots = new ArrayList<Rectangle<C>>();
        if (a.isConstant() || a.isZERO()) {
            return roots;
        }
        //System.out.println("rect = " + rect); 
        long n = windingNumber(rect, a);
        if (n < 0) { // can this happen?
            throw new RuntimeException("negative winding number " + n);
            //System.out.println("negative winding number " + n);
            //return roots;
        }
        if (n == 0) {
            return roots;
        }
        if (n == 1) {
            roots.add(rect);
            return roots;
        }
        Complex<C> eps = cr.fromInteger(1);
        eps = eps.divide(cr.fromInteger(1000)); // 1/1000
        //System.out.println("eps = " + eps);
        //System.out.println("rect = " + rect); 
        // construct new center
        Complex<C> delta = rect.corners[3].subtract(rect.corners[1]);
        delta = delta.divide(cr.fromInteger(2));
        //System.out.println("delta = " + delta); 
        boolean work = true;
        while (work) {
            Complex<C> center = rect.corners[1].sum(delta);
            //System.out.println("center = " + toDecimal(center)); 
            if (debug) {
                logger.info("new center = " + center);
            }
            try {
                Complex<C>[] cp = (Complex<C>[]) copyOfComplex(rect.corners, 4);
                // (Complex<C>[]) new Complex[4];  cp[0] = rect.corners[0];
                // cp[0] fix
                cp[1] = new Complex<C>(cr, cp[1].getRe(), center.getIm());
                cp[2] = center;
                cp[3] = new Complex<C>(cr, center.getRe(), cp[3].getIm());
                Rectangle<C> nw = new Rectangle<C>(cp);
                //System.out.println("nw = " + nw); 
                List<Rectangle<C>> nwr = complexRoots(nw, a);
                //System.out.println("#nwr = " + nwr.size()); 
                roots.addAll(nwr);
                if (roots.size() == a.degree(0)) {
                    work = false;
                    break;
                }

                cp = (Complex<C>[]) copyOfComplex(rect.corners, 4);
                cp[0] = new Complex<C>(cr, cp[0].getRe(), center.getIm());
                // cp[1] fix
                cp[2] = new Complex<C>(cr, center.getRe(), cp[2].getIm());
                cp[3] = center;
                Rectangle<C> sw = new Rectangle<C>(cp);
                //System.out.println("sw = " + sw); 
                List<Rectangle<C>> swr = complexRoots(sw, a);
                //System.out.println("#swr = " + swr.size()); 
                roots.addAll(swr);
                if (roots.size() == a.degree(0)) {
                    work = false;
                    break;
                }

                cp = (Complex<C>[]) copyOfComplex(rect.corners, 4);
                cp[0] = center;
                cp[1] = new Complex<C>(cr, center.getRe(), cp[1].getIm());
                // cp[2] fix
                cp[3] = new Complex<C>(cr, cp[3].getRe(), center.getIm());
                Rectangle<C> se = new Rectangle<C>(cp);
                //System.out.println("se = " + se); 
                List<Rectangle<C>> ser = complexRoots(se, a);
                //System.out.println("#ser = " + ser.size()); 
                roots.addAll(ser);
                if (roots.size() == a.degree(0)) {
                    work = false;
                    break;
                }

                cp = (Complex<C>[]) copyOfComplex(rect.corners, 4);
                cp[0] = new Complex<C>(cr, center.getRe(), cp[0].getIm());
                cp[1] = center;
                cp[2] = new Complex<C>(cr, cp[2].getRe(), center.getIm());
                // cp[3] fix
                Rectangle<C> ne = new Rectangle<C>(cp);
                //System.out.println("ne = " + ne); 
                List<Rectangle<C>> ner = complexRoots(ne, a);
                //System.out.println("#ner = " + ner.size()); 
                roots.addAll(ner);
                work = false;
            } catch (InvalidBoundaryException e) {
                // repeat with new center
                delta = delta.sum(delta.multiply(eps)); // distort
                //System.out.println("new delta = " + toDecimal(delta)); 
                eps = eps.sum(eps.multiply(cr.getIMAG()));
            }
        }
        return roots;
    }


    /**
     * Invariant rectangle for algebraic number.
     * @param rect root isolating rectangle for f which contains exactly one
     *            root.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return v a new rectangle contained in rect such that g(w) != 0 for w in
     *         v.
     */
    @Override
    public Rectangle<C> invariantRectangle(Rectangle<C> rect, GenPolynomial<Complex<C>> f,
                    GenPolynomial<Complex<C>> g) throws InvalidBoundaryException {
        Rectangle<C> v = rect;
        if (g == null || g.isZERO()) {
            return v;
        }
        if (g.isConstant()) {
            return v;
        }
        if (f == null || f.isZERO() || f.isConstant()) { // ?
            return v;
        }
        BigRational len = v.rationalLength();
        BigRational half = new BigRational(1, 2);
        while (true) {
            long n = windingNumber(v, g);
            //System.out.println("n = " + n);
            if (n < 0) { // can this happen?
                throw new RuntimeException("negative winding number " + n);
            }
            if (n == 0) {
                return v;
            }
            len = len.multiply(half);
            Rectangle<C> v1 = v;
            v = complexRootRefinement(v, f, len);
            if (v.equals(v1)) {
                //System.out.println("len = " + len);
                if (!f.gcd(g).isONE()) {
                    System.out.println("f.gcd(g) = " + f.gcd(g));
                    throw new RuntimeException("no convergence " + v);
                }
                //break; // no convergence
            }
        }
        //return v;
    }

}
