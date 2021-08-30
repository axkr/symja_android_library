/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.Rational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.root.RealAlgebraicNumber;
import edu.jas.structure.GcdRingElem;


/**
 * Container for Ideals together with univariate polynomials and real algebraic
 * roots.
 * @author Heinz Kredel
 */
public class IdealWithRealAlgebraicRoots<D extends GcdRingElem<D> & Rational> extends IdealWithUniv<D> {


    /**
     * The list of real algebraic roots.
     */
    public final List<List<RealAlgebraicNumber<D>>> ran;


    /**
     * The list of decimal approximations of the real algebraic roots.
     */
    protected List<List<BigDecimal>> droots = null;


    /**
     * Constructor not for use.
     */
    protected IdealWithRealAlgebraicRoots() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univaraite polynomials
     * @param rr the list of real algebraic roots
     */
    public IdealWithRealAlgebraicRoots(Ideal<D> id, List<GenPolynomial<D>> up,
                    List<List<RealAlgebraicNumber<D>>> rr) {
        super(id, up);
        ran = rr;
    }


    /**
     * Constructor.
     * @param iu the ideal with univariate polynomials
     * @param rr the list of real algebraic roots
     */
    public IdealWithRealAlgebraicRoots(IdealWithUniv<D> iu, List<List<RealAlgebraicNumber<D>>> rr) {
        super(iu.ideal, iu.upolys);
        ran = rr;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString() + "\nreal roots:\n");
        sb.append("[");
        boolean f1 = true;
        for (List<RealAlgebraicNumber<D>> lr : ran) {
            if (!f1) {
                sb.append(", ");
            } else {
                f1 = false;
            }
            sb.append("[");
            boolean f2 = true;
            for (RealAlgebraicNumber<D> rr : lr) {
                if (!f2) {
                    sb.append(", ");
                } else {
                    f2 = false;
                }
                sb.append(rr.ring.toScript());
            }
            sb.append("]");
        }
        sb.append("]");
        if (droots != null) {
            sb.append("\ndecimal real root approximation:\n");
            for (List<BigDecimal> d : droots) {
                sb.append(d.toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    @Override
    public String toScript() {
        // Python case
        return super.toScript() + ",  " + ran.toString();
    }


    /**
     * Get decimal approximation of the real root tuples.
     */
    public synchronized List<List<BigDecimal>> decimalApproximation() {
        if (this.droots != null) {
            return droots;
        }
        List<List<BigDecimal>> rroots = new ArrayList<List<BigDecimal>>();
        for (List<RealAlgebraicNumber<D>> rri : this.ran) {
            List<BigDecimal> r = new ArrayList<BigDecimal>();
            for (RealAlgebraicNumber<D> rr : rri) {
                BigDecimal d = new BigDecimal(rr.magnitude());
                r.add(d);
            }
            rroots.add(r);
        }
        droots = rroots;
        return rroots;
    }


    /**
     * compute decimal approximation of the real root tuples.
     */
    public void doDecimalApproximation() {
        List<List<BigDecimal>> unused = decimalApproximation();
        if (unused.isEmpty()) { // use for findbugs
            System.out.println("unused is empty");
        }
        return;
    }


    /**
     * Is decimal approximation of the real roots.
     * @return true, if the decimal real roots approximate the real roots.
     */
    public synchronized boolean isDecimalApproximation() {
        doDecimalApproximation();
        if (droots == null || droots.size() == 0) {
            return true;
        }
        if (upolys == null || upolys.size() == 0) {
            return true;
        }
        BigDecimal dr = droots.get(0).get(0);
        BigDecimal c = new BigDecimal("0.15").power(BigDecimal.DEFAULT_PRECISION / 2);
        //System.out.println("eps: c = " + c);
        List<GenPolynomial<BigDecimal>> upds = new ArrayList<GenPolynomial<BigDecimal>>(upolys.size());
        for (GenPolynomial<D> up : upolys) {
            GenPolynomialRing<D> pfac = up.ring;
            GenPolynomialRing<BigDecimal> dpfac = new GenPolynomialRing<BigDecimal>(dr, pfac);
            GenPolynomial<BigDecimal> upd = PolyUtil.<D> decimalFromRational(dpfac, up);
            //System.out.println("upd = " + upd);
            upds.add(upd);
        }
        for (List<BigDecimal> rr : droots) {
            int i = 0;
            for (GenPolynomial<BigDecimal> upd : upds) {
                BigDecimal d = rr.get(i++);
                BigDecimal z = PolyUtil.<BigDecimal> evaluateMain(dr, upd, d);
                z = z.abs();
                //System.out.println("z = " + z + ", d = " + d);
                if (z.compareTo(c) >= 0) {
                    //System.out.println("no root: z = " + z);
                    BigDecimal cc = new BigDecimal("0.1").power(BigDecimal.DEFAULT_PRECISION / 3);
                    if (z.compareTo(cc) >= 0) {
                        System.out.println("no root: z = " + z + ", cc = " + cc);
                        return false;
                    }
                }
            }
        }
        GenPolynomialRing<D> pfac = ideal.list.ring;
        GenPolynomialRing<BigDecimal> dpfac = new GenPolynomialRing<BigDecimal>(dr, pfac);
        List<GenPolynomial<D>> ips = ideal.list.list;
        //List<GenPolynomial<BigDecimal>> ipds = new ArrayList<GenPolynomial<BigDecimal>>(ips.size());
        c = new BigDecimal("0.15").power(BigDecimal.DEFAULT_PRECISION / 2 - 1);
        for (GenPolynomial<D> ip : ips) {
            GenPolynomial<BigDecimal> ipd = PolyUtil.<D> decimalFromRational(dpfac, ip);
            //System.out.println("ipd = " + ipd);
            //ipds.add(ipd);
            for (List<BigDecimal> rr : droots) {
                BigDecimal z = PolyUtil.<BigDecimal> evaluateAll(dr, ipd, rr);
                z = z.abs();
                //System.out.println("z = " + z + ", rr = " + rr);
                if (z.compareTo(c) >= 0) {
                    //System.out.println("no root: z = " + z + ", c = " + c);
                    BigDecimal cc = new BigDecimal("0.1").power(BigDecimal.DEFAULT_PRECISION / 3);
                    if (z.compareTo(cc) >= 0) {
                        System.out.println("no root: z = " + z + ", cc = " + cc);
                        System.out.println("ipd = " + ipd + ", rr = " + rr);
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
