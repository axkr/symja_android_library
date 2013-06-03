/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Complex roots abstract class.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public abstract class ComplexRootsAbstract<C extends RingElem<C> & Rational> implements ComplexRoots<C> {


    private static final Logger logger = Logger.getLogger(ComplexRootsAbstract.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Engine for square free decomposition.
     */
    public final Squarefree<Complex<C>> engine;


    /**
     * Constructor.
     * @param cf coefficient factory.
     */
    public ComplexRootsAbstract(RingFactory<Complex<C>> cf) {
        if (!(cf instanceof ComplexRing)) {
            throw new IllegalArgumentException("cf not supported coefficients " + cf);
        }
        engine = SquarefreeFactory.<Complex<C>> getImplementation(cf);
    }


    /**
     * Root bound. With f(-M + i M) * f(-M - i M) * f(M - i M) * f(M + i M) !=
     * 0.
     * @param f univariate polynomial.
     * @return M such that root(f) is contained in the rectangle spanned by M.
     */
    public Complex<C> rootBound(GenPolynomial<Complex<C>> f) {
        if (f == null) {
            return null;
        }
        RingFactory<Complex<C>> cfac = f.ring.coFac;
        Complex<C> M = cfac.getONE();
        if (f.isZERO() || f.isConstant()) {
            return M;
        }
        Complex<C> a = f.leadingBaseCoefficient().norm();
        for (Complex<C> c : f.getMap().values()) {
            Complex<C> d = c.norm().divide(a);
            if (M.compareTo(d) < 0) {
                M = d;
            }
        }
        M = M.sum(cfac.getONE());
        //System.out.println("M = " + M);
        return M;
    }


    /**
     * Magnitude bound.
     * @param rect rectangle.
     * @param f univariate polynomial.
     * @return B such that |f(c)| &lt; B for c in rect.
     */
    public C magnitudeBound(Rectangle<C> rect, GenPolynomial<Complex<C>> f) {
        if (f == null) {
            return null;
        }
        if (f.isZERO()) {
            return f.ring.coFac.getONE().getRe();
        }
        //System.out.println("f = " + f);
        if (f.isConstant()) {
            Complex<C> c = f.leadingBaseCoefficient();
            return c.norm().getRe();
        }
        GenPolynomial<Complex<C>> fa = f.map(new UnaryFunctor<Complex<C>, Complex<C>>() {


            public Complex<C> eval(Complex<C> a) {
                return a.norm();
            }
        });
        //System.out.println("fa = " + fa);
        Complex<C> Mc = rect.getNW().norm();
        C M = Mc.getRe();
        //System.out.println("M = " + M);
        Complex<C> M1c = rect.getSW().norm();
        C M1 = M1c.getRe();
        if (M.compareTo(M1) < 0) {
            M = M1;
            Mc = M1c;
        }
        M1c = rect.getSE().norm();
        M1 = M1c.getRe();
        if (M.compareTo(M1) < 0) {
            M = M1;
            Mc = M1c;
        }
        M1c = rect.getNE().norm();
        M1 = M1c.getRe();
        if (M.compareTo(M1) < 0) {
            //M = M1;
            Mc = M1c;
        }
        //System.out.println("M = " + M);
        Complex<C> B = PolyUtil.<Complex<C>> evaluateMain(f.ring.coFac, fa, Mc);
        //System.out.println("B = " + B);
        return B.getRe();
    }


    /**
     * Complex root count of complex polynomial on rectangle.
     * @param rect rectangle.
     * @param a univariate complex polynomial.
     * @return root count of a in rectangle.
     */
    public abstract long complexRootCount(Rectangle<C> rect, GenPolynomial<Complex<C>> a)
                    throws InvalidBoundaryException;


    /**
     * List of complex roots of complex polynomial a on rectangle.
     * @param rect rectangle.
     * @param a univariate squarefree complex polynomial.
     * @return list of complex roots.
     */
    public abstract List<Rectangle<C>> complexRoots(Rectangle<C> rect, GenPolynomial<Complex<C>> a)
                    throws InvalidBoundaryException;


    /**
     * List of complex roots of complex polynomial.
     * @param a univariate complex polynomial.
     * @return list of complex roots.
     */
    @SuppressWarnings("unchecked")
    public List<Rectangle<C>> complexRoots(GenPolynomial<Complex<C>> a) {
        List<Rectangle<C>> roots = new ArrayList<Rectangle<C>>();
        if (a.isConstant() || a.isZERO()) {
            return roots;
        }
        ComplexRing<C> cr = (ComplexRing<C>) a.ring.coFac;
        SortedMap<GenPolynomial<Complex<C>>, Long> sa = engine.squarefreeFactors(a);
        for (Map.Entry<GenPolynomial<Complex<C>>, Long> me : sa.entrySet()) {
            GenPolynomial<Complex<C>> p = me.getKey();
            Complex<C> Mb = rootBound(p);
            C M = Mb.getRe();
            C M1 = M.sum(M.factory().fromInteger(1)); // asymmetric to origin
            //System.out.println("M = " + M);
            if (debug) {
                logger.info("rootBound = " + M);
            }
            Complex<C>[] corner = (Complex<C>[]) new Complex[4];
            corner[0] = new Complex<C>(cr, M1.negate(), M); // nw
            corner[1] = new Complex<C>(cr, M1.negate(), M1.negate()); // sw
            corner[2] = new Complex<C>(cr, M, M1.negate()); // se
            corner[3] = new Complex<C>(cr, M, M); // ne
            Rectangle<C> rect = new Rectangle<C>(corner);
            try {
                List<Rectangle<C>> rs = complexRoots(rect, p);
                long e = me.getValue(); // sa.get(p);
                for (int i = 0; i < e; i++) { // add with multiplicity
                    roots.addAll(rs);
                }
            } catch (InvalidBoundaryException e) {
                //logger.error("invalid boundary for p = " + p);
                throw new RuntimeException("this should never happen " + e);
            }
        }
        return roots;
    }


    /**
     * Complex root refinement of complex polynomial a on rectangle.
     * @param rect rectangle containing exactly one complex root.
     * @param a univariate squarefree complex polynomial.
     * @param len rational length for refinement.
     * @return refined complex root.
     */
    public Rectangle<C> complexRootRefinement(Rectangle<C> rect, GenPolynomial<Complex<C>> a, BigRational len)
                    throws InvalidBoundaryException {
        ComplexRing<C> cr = (ComplexRing<C>) a.ring.coFac;
        Rectangle<C> root = rect;
        long w;
        if (debug) {
            w = complexRootCount(root, a);
            if (w != 1) {
                System.out.println("#root = " + w);
                System.out.println("root = " + root);
                throw new ArithmeticException("no initial isolating rectangle " + rect);
            }
        }
        Complex<C> eps = cr.fromInteger(1);
        eps = eps.divide(cr.fromInteger(1000)); // 1/1000
        BigRational length = len.multiply(len);
        Complex<C> delta = null;
        boolean work = true;
        while (work) {
            try {
                while (root.rationalLength().compareTo(length) > 0) {
                    //System.out.println("root = " + root + ", len = " + new BigDecimal(root.rationalLength())); 
                    if (delta == null) {
                        delta = root.corners[3].subtract(root.corners[1]);
                        delta = delta.divide(cr.fromInteger(2));
                        //System.out.println("delta = " + toDecimal(delta)); 
                    }
                    Complex<C> center = root.corners[1].sum(delta);
                    //System.out.println("refine center = " + toDecimal(center)); 
                    if (debug) {
                        logger.info("new center = " + center);
                    }

                    Complex<C>[] cp = (Complex<C>[]) copyOfComplex(root.corners, 4);
                    // cp[0] fix
                    cp[1] = new Complex<C>(cr, cp[1].getRe(), center.getIm());
                    cp[2] = center;
                    cp[3] = new Complex<C>(cr, center.getRe(), cp[3].getIm());
                    Rectangle<C> nw = new Rectangle<C>(cp);
                    w = complexRootCount(nw, a);
                    if (w == 1) {
                        root = nw;
                        delta = null;
                        continue;
                    }

                    cp = (Complex<C>[]) copyOfComplex(root.corners, 4);
                    cp[0] = new Complex<C>(cr, cp[0].getRe(), center.getIm());
                    // cp[1] fix
                    cp[2] = new Complex<C>(cr, center.getRe(), cp[2].getIm());
                    cp[3] = center;
                    Rectangle<C> sw = new Rectangle<C>(cp);
                    w = complexRootCount(sw, a);
                    //System.out.println("#swr = " + w); 
                    if (w == 1) {
                        root = sw;
                        delta = null;
                        continue;
                    }

                    cp = (Complex<C>[]) copyOfComplex(root.corners, 4);
                    cp[0] = center;
                    cp[1] = new Complex<C>(cr, center.getRe(), cp[1].getIm());
                    // cp[2] fix
                    cp[3] = new Complex<C>(cr, cp[3].getRe(), center.getIm());
                    Rectangle<C> se = new Rectangle<C>(cp);
                    w = complexRootCount(se, a);
                    //System.out.println("#ser = " + w); 
                    if (w == 1) {
                        root = se;
                        delta = null;
                        continue;
                    }

                    cp = (Complex<C>[]) copyOfComplex(root.corners, 4);
                    cp[0] = new Complex<C>(cr, center.getRe(), cp[0].getIm());
                    cp[1] = center;
                    cp[2] = new Complex<C>(cr, cp[2].getRe(), center.getIm());
                    // cp[3] fix
                    Rectangle<C> ne = new Rectangle<C>(cp);
                    w = complexRootCount(ne, a);
                    //System.out.println("#ner = " + w); 
                    if (w == 1) {
                        root = ne;
                        delta = null;
                        continue;
                    }
                    if (true) {
                        w = complexRootCount(root, a);
                        System.out.println("#root = " + w);
                        System.out.println("root = " + root);
                    }
                    throw new ArithmeticException("no isolating rectangle " + rect);
                }
                work = false;
            } catch (InvalidBoundaryException e) {
                // repeat with new center
                delta = delta.sum(delta.multiply(eps)); // distort
                //System.out.println("new refine delta = " + toDecimal(delta));
                eps = eps.sum(eps.multiply(cr.getIMAG()));
            }
        }
        return root;
    }


    /**
     * List of complex roots of complex polynomial.
     * @param a univariate complex polynomial.
     * @param len rational length for refinement.
     * @return list of complex roots to desired precision.
     */
    @SuppressWarnings("unchecked")
    public List<Rectangle<C>> complexRoots(GenPolynomial<Complex<C>> a, BigRational len) {
        ComplexRing<C> cr = (ComplexRing<C>) a.ring.coFac;
        SortedMap<GenPolynomial<Complex<C>>, Long> sa = engine.squarefreeFactors(a);
        List<Rectangle<C>> roots = new ArrayList<Rectangle<C>>();
        for (Map.Entry<GenPolynomial<Complex<C>>, Long> me : sa.entrySet()) {
            GenPolynomial<Complex<C>> p = me.getKey();
            Complex<C> Mb = rootBound(p);
            C M = Mb.getRe();
            C M1 = M.sum(M.factory().fromInteger(1)); // asymmetric to origin
            if (debug) {
                logger.info("rootBound = " + M);
            }
            Complex<C>[] corner = (Complex<C>[]) new Complex[4];
            corner[0] = new Complex<C>(cr, M1.negate(), M); // nw
            corner[1] = new Complex<C>(cr, M1.negate(), M1.negate()); // sw
            corner[2] = new Complex<C>(cr, M, M1.negate()); // se
            corner[3] = new Complex<C>(cr, M, M); // ne
            Rectangle<C> rect = new Rectangle<C>(corner);
            try {
                List<Rectangle<C>> rs = complexRoots(rect, p);
                List<Rectangle<C>> rf = new ArrayList<Rectangle<C>>(rs.size());
                for (Rectangle<C> r : rs) {
                    Rectangle<C> rr = complexRootRefinement(r, p, len);
                    rf.add(rr);
                }
                long e = me.getValue(); // sa.get(p);
                for (int i = 0; i < e; i++) { // add with multiplicity
                    roots.addAll(rf);
                }
            } catch (InvalidBoundaryException e) {
                throw new RuntimeException("this should never happen " + e);
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
     * @return v with v a new rectangle contained in iv such that g(w) != 0 for
     *         w in v.
     */
    public abstract Rectangle<C> invariantRectangle(Rectangle<C> rect, GenPolynomial<Complex<C>> f,
                    GenPolynomial<Complex<C>> g) throws InvalidBoundaryException;


    /**
     * Get decimal approximation.
     * @param a complex number.
     * @return decimal(a).
     */
    public String toDecimal(Complex<C> a) {
        C r = a.getRe();
        String s = r.toString();
        BigRational rs = new BigRational(s);
        BigDecimal rd = new BigDecimal(rs);
        C i = a.getIm();
        s = i.toString();
        BigRational is = new BigRational(s);
        BigDecimal id = new BigDecimal(is);
        //System.out.println("rd = " + rd);
        //System.out.println("id = " + id);
        return rd.toString() + " i " + id.toString();
    }


    /**
     * Approximate complex root.
     * @param rt root isolating rectangle.
     * @param f univariate polynomial, non-zero.
     * @param eps requested interval length.
     * @return a decimal approximation d such that |d-v| &lt; eps, for f(v) = 0,
     *         v in rt.
     */
    public Complex<BigDecimal> approximateRoot(Rectangle<C> rt, GenPolynomial<Complex<C>> f, C eps)
                    throws NoConvergenceException {
        if (rt == null) {
            throw new IllegalArgumentException("null interval not allowed");
        }
        Complex<BigDecimal> d = rt.getDecimalCenter();
        //System.out.println("d  = " + d);
        if (f == null || f.isZERO() || f.isConstant() || eps == null) {
            return d;
        }
        if (rt.length().compareTo(eps) < 0) {
            return d;
        }
        ComplexRing<BigDecimal> cr = d.ring;
        Complex<C> sw = rt.getSW();
        BigDecimal swr = new BigDecimal(sw.getRe().getRational());
        BigDecimal swi = new BigDecimal(sw.getIm().getRational());
        Complex<BigDecimal> ll = new Complex<BigDecimal>(cr, swr, swi);
        Complex<C> ne = rt.getNE();
        BigDecimal ner = new BigDecimal(ne.getRe().getRational());
        BigDecimal nei = new BigDecimal(ne.getIm().getRational());
        Complex<BigDecimal> ur = new Complex<BigDecimal>(cr, ner, nei);

        BigDecimal e = new BigDecimal(eps.getRational());
        Complex<BigDecimal> q = new Complex<BigDecimal>(cr, new BigDecimal("0.25"));
        e = e.multiply(d.norm().getRe()); // relative error
        //System.out.println("e  = " + e);

        // polynomials with decimal coefficients
        GenPolynomialRing<Complex<BigDecimal>> dfac = new GenPolynomialRing<Complex<BigDecimal>>(cr, f.ring);
        GenPolynomial<Complex<BigDecimal>> df = PolyUtil.<C> complexDecimalFromRational(dfac, f);
        GenPolynomial<Complex<C>> fp = PolyUtil.<Complex<C>> baseDeriviative(f);
        GenPolynomial<Complex<BigDecimal>> dfp = PolyUtil.<C> complexDecimalFromRational(dfac, fp);

        // Newton Raphson iteration: x_{n+1} = x_n - f(x_n)/f'(x_n)
        int i = 0;
        final int MITER = 50;
        int dir = -1;
        while (i++ < MITER) {
            Complex<BigDecimal> fx = PolyUtil.<Complex<BigDecimal>> evaluateMain(cr, df, d); // f(d)
            //BigDecimal fs = fx.norm().getRe();
            //System.out.println("fs = " + fs);
            if (fx.isZERO()) {
                return d;
            }
            Complex<BigDecimal> fpx = PolyUtil.<Complex<BigDecimal>> evaluateMain(cr, dfp, d); // f'(d)
            if (fpx.isZERO()) {
                throw new NoConvergenceException("zero deriviative should not happen");
            }
            Complex<BigDecimal> x = fx.divide(fpx);
            Complex<BigDecimal> dx = d.subtract(x);
            //System.out.println("dx = " + dx);
            if (d.subtract(dx).norm().getRe().compareTo(e) <= 0) {
                return dx;
            }
            //             if ( false ) { // not useful:
            //                 Complex<BigDecimal> fxx  = PolyUtil.<Complex<BigDecimal>> evaluateMain(cr, df, dx); // f(dx)
            //                 //System.out.println("fxx = " + fxx);
            //                 BigDecimal fsx = fxx.norm().getRe();
            //                 System.out.println("fsx = " + fsx);
            //                 while ( fsx.compareTo( fs ) >= 0 ) {
            //                     System.out.println("trying to increase f(d) ");
            //                     if ( i++ > MITER ) { // dx > right: dx - right > 0
            //                         throw new NoConvergenceException("no convergence after " + i + " steps");
            //                     }
            //                     x = x.multiply(q); // x * 1/4
            //                     dx = d.subtract(x);
            //                     //System.out.println(" x = " + x);
            //                     System.out.println("dx = " + dx);
            //                     fxx  = PolyUtil.<Complex<BigDecimal>> evaluateMain(cr, df, dx); // f(dx)
            //                     //System.out.println("fxx = " + fxx);
            //                     fsx = fxx.norm().getRe();
            //                     System.out.println("fsx = " + fsx);
            //                 }
            //             }
            // check interval bounds
            while (dx.getRe().compareTo(ll.getRe()) < 0 || dx.getIm().compareTo(ll.getIm()) < 0
                            || dx.getRe().compareTo(ur.getRe()) > 0 || dx.getIm().compareTo(ur.getIm()) > 0) { // dx < ll: dx - ll < 0
                                                                                                               // dx > ur: dx - ur > 0
                if (i++ > MITER) { // dx > right: dx - right > 0
                    throw new NoConvergenceException("no convergence after " + i + " steps");
                }
                if (i > MITER / 2 && dir == 0) {
                    Complex<C> cc = rt.getCenter();
                    Rectangle<C> nrt = rt.exchangeSE(cc);
                    Complex<BigDecimal> sd = nrt.getDecimalCenter();
                    d = sd;
                    x = cr.getZERO();
                    logger.info("trying new SE starting point " + d);
                    i = 0;
                    dir = 1;
                }
                if (i > MITER / 2 && dir == 1) {
                    Complex<C> cc = rt.getCenter();
                    Rectangle<C> nrt = rt.exchangeNW(cc);
                    Complex<BigDecimal> sd = nrt.getDecimalCenter();
                    d = sd;
                    x = cr.getZERO();
                    logger.info("trying new NW starting point " + d);
                    i = 0;
                    dir = 2;
                }
                if (i > MITER / 2 && dir == 2) {
                    Complex<C> cc = rt.getCenter();
                    Rectangle<C> nrt = rt.exchangeSW(cc);
                    Complex<BigDecimal> sd = nrt.getDecimalCenter();
                    d = sd;
                    x = cr.getZERO();
                    logger.info("trying new SW starting point " + d);
                    i = 0;
                    dir = 3;
                }
                if (i > MITER / 2 && dir == 3) {
                    Complex<C> cc = rt.getCenter();
                    Rectangle<C> nrt = rt.exchangeNE(cc);
                    Complex<BigDecimal> sd = nrt.getDecimalCenter();
                    d = sd;
                    x = cr.getZERO();
                    logger.info("trying new NE starting point " + d);
                    i = 0;
                    dir = 4;
                }
                if (i > MITER / 2 && (dir == -1 || dir == 4 || dir == 5)) {
                    Complex<C> sr = rt.randomPoint();
                    BigDecimal srr = new BigDecimal(sr.getRe().getRational());
                    BigDecimal sri = new BigDecimal(sr.getIm().getRational());
                    Complex<BigDecimal> sd = new Complex<BigDecimal>(cr, srr, sri);
                    d = sd;
                    x = cr.getZERO();
                    logger.info("trying new random starting point " + d);
                    if (dir == -1) {
                        i = 0;
                        dir = 0;
                    } else if (dir == 4) {
                        i = 0;
                        dir = 5;
                    } else {
                        //i = 0; 
                        dir = 6; // end
                    }
                }
                x = x.multiply(q); // x * 1/4
                dx = d.subtract(x);
                //System.out.println(" x = " + x);
                //System.out.println("dx = " + dx);
            }
            d = dx;
        }
        throw new NoConvergenceException("no convergence after " + i + " steps");
    }


    /**
     * List of decimal approximations of complex roots of complex polynomial.
     * @param a univariate complex polynomial.
     * @param eps length for refinement.
     * @return list of complex decimal root approximations to desired precision.
     */
    @SuppressWarnings("unchecked")
    public List<Complex<BigDecimal>> approximateRoots(GenPolynomial<Complex<C>> a, C eps) {
        ComplexRing<C> cr = (ComplexRing<C>) a.ring.coFac;
        SortedMap<GenPolynomial<Complex<C>>, Long> sa = engine.squarefreeFactors(a);
        List<Complex<BigDecimal>> roots = new ArrayList<Complex<BigDecimal>>();
        for (Map.Entry<GenPolynomial<Complex<C>>, Long> me : sa.entrySet()) {
            GenPolynomial<Complex<C>> p = me.getKey();
            List<Complex<BigDecimal>> rf = null;
            if (p.degree(0) <= 1) {
                Complex<C> tc = p.trailingBaseCoefficient();
                tc = tc.negate();
                BigDecimal rr = new BigDecimal(tc.getRe().getRational());
                BigDecimal ri = new BigDecimal(tc.getIm().getRational());
                ComplexRing<BigDecimal> crf = new ComplexRing<BigDecimal>(rr);
                Complex<BigDecimal> r = new Complex<BigDecimal>(crf, rr, ri);
                rf = new ArrayList<Complex<BigDecimal>>(1);
                rf.add(r);
            } else {
                Complex<C> Mb = rootBound(p);
                C M = Mb.getRe();
                C M1 = M.sum(M.factory().fromInteger(1)); // asymmetric to origin
                if (debug) {
                    logger.info("rootBound = " + M);
                }
                Complex<C>[] corner = (Complex<C>[]) new Complex[4];
                corner[0] = new Complex<C>(cr, M1.negate(), M); // nw
                corner[1] = new Complex<C>(cr, M1.negate(), M1.negate()); // sw
                corner[2] = new Complex<C>(cr, M, M1.negate()); // se
                corner[3] = new Complex<C>(cr, M, M); // ne
                Rectangle<C> rect = new Rectangle<C>(corner);
                List<Rectangle<C>> rs = null;
                try {
                    rs = complexRoots(rect, p);
                } catch (InvalidBoundaryException e) {
                    throw new RuntimeException("this should never happen " + e);
                }
                rf = new ArrayList<Complex<BigDecimal>>(rs.size());
                for (Rectangle<C> r : rs) {
                    Complex<BigDecimal> rr = null;
                    while (rr == null) {
                        try {
                            rr = approximateRoot(r, p, eps);
                            rf.add(rr);
                        } catch (NoConvergenceException e) {
                            // fall back to exact algorithm
                            BigRational len = r.rationalLength();
                            len = len.multiply(new BigRational(1, 1000));
                            try {
                                r = complexRootRefinement(r, p, len);
                                logger.info("fall back rootRefinement = " + r);
                                //System.out.println("len = " + len);
                            } catch (InvalidBoundaryException ee) {
                                throw new RuntimeException("this should never happen " + ee);
                            }
                        }
                    }
                }
            }
            long e = me.getValue(); // sa.get(p);
            for (int i = 0; i < e; i++) { // add with multiplicity
                roots.addAll(rf);
            }
        }
        return roots;
    }


    /**
     * Copy the specified array.
     * @param original array.
     * @param newLength new array length.
     * @return copy of this.
     */
    public Complex[] copyOfComplex(Complex[] original, int newLength) {
        Complex[] copy = new Complex[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }


    /**
     * Invariant rectangle for algebraic number magnitude.
     * @param rect root isolating rectangle for f which contains exactly one
     *            root.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @param eps length limit for rectangle length.
     * @return v with v a new rectangle contained in rect such that |g(a) -
     *         g(b)| &lt; eps for a, b in v in rect.
     */
    public Rectangle<C> invariantMagnitudeRectangle(Rectangle<C> rect, GenPolynomial<Complex<C>> f,
                    GenPolynomial<Complex<C>> g, C eps) throws InvalidBoundaryException {
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
        GenPolynomial<Complex<C>> gp = PolyUtil.<Complex<C>> baseDeriviative(g);
        //System.out.println("g  = " + g);
        //System.out.println("gp = " + gp);
        C B = magnitudeBound(rect, gp);
        //System.out.println("B = " + B + " : " + B.getClass());

        BigRational len = v.rationalLength();
        BigRational half = new BigRational(1, 2);

        C vlen = v.length();
        vlen = vlen.multiply(vlen);
        //eps = eps.multiply(eps);
        //System.out.println("v = " + v);
        //System.out.println("vlen = " + vlen);
        while (B.multiply(vlen).compareTo(eps) >= 0) { // TODO: test squared
            len = len.multiply(half);
            v = complexRootRefinement(v, f, len);
            //System.out.println("v = " + v);
            vlen = v.length();
            vlen = vlen.multiply(vlen);
            //System.out.println("vlen = " + vlen);
        }
        //System.out.println("vlen = " + vlen);
        return v;
    }


    /**
     * Complex algebraic number magnitude.
     * @param rect root isolating rectangle for f which contains exactly one
     *            root, with rect such that |g(a) - g(b)| &lt; eps for a, b in
     *            rect.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @return g(rect) .
     */
    public Complex<C> complexRectangleMagnitude(Rectangle<C> rect, GenPolynomial<Complex<C>> f,
                    GenPolynomial<Complex<C>> g) {
        if (g.isZERO() || g.isConstant()) {
            return g.leadingBaseCoefficient();
        }
        RingFactory<Complex<C>> cfac = f.ring.coFac;
        //System.out.println("cfac = " + cfac + " : " + cfac.getClass());
        Complex<C> c = rect.getCenter();
        Complex<C> ev = PolyUtil.<Complex<C>> evaluateMain(cfac, g, c);
        return ev;
    }


    /**
     * Complex algebraic number magnitude.
     * @param rect root isolating rectangle for f which contains exactly one
     *            root, with rect such that |g(a) - g(b)| &lt; eps for a, b in
     *            rect.
     * @param f univariate polynomial, non-zero.
     * @param g univariate polynomial, gcd(f,g) == 1.
     * @param eps length limit for rectangle length.
     * @return g(rect) .
     */
    public Complex<C> complexMagnitude(Rectangle<C> rect, GenPolynomial<Complex<C>> f,
                    GenPolynomial<Complex<C>> g, C eps) throws InvalidBoundaryException {
        if (g.isZERO() || g.isConstant()) {
            return g.leadingBaseCoefficient();
        }
        Rectangle<C> v = invariantMagnitudeRectangle(rect, f, g, eps);
        //System.out.println("ref = " + ref);
        return complexRectangleMagnitude(v, f, g);
    }

}
