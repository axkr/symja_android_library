/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigDecimal;
import edu.jas.arith.Rational;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
// import edu.jas.root.RealAlgebraicNumber;
import edu.jas.structure.GcdRingElem;


/**
 * Container for Ideals together with univariate polynomials and complex
 * algebraic roots.
 * @author Heinz Kredel
 */
public class IdealWithComplexAlgebraicRoots<D extends GcdRingElem<D> & Rational> extends IdealWithUniv<D> {


    /**
     * The list of complex algebraic roots.
     */
    public final List<List<Complex<RealAlgebraicNumber<D>>>> can;


    /**
     * The list of decimal approximations of the complex algebraic roots.
     */
    protected List<List<Complex<BigDecimal>>> droots = null;


    /**
     * Constructor not for use.
     */
    protected IdealWithComplexAlgebraicRoots() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param id the ideal
     * @param up the list of univariate polynomials
     * @param cr the list of complex algebraic roots
     */
    public IdealWithComplexAlgebraicRoots(Ideal<D> id, List<GenPolynomial<D>> up,
                    List<List<Complex<RealAlgebraicNumber<D>>>> cr) {
        super(id, up);
        can = cr;
    }


    /**
     * Constructor.
     * @param iu the ideal with univariate polynomials
     * @param cr the list of real algebraic roots
     */
    public IdealWithComplexAlgebraicRoots(IdealWithUniv<D> iu,
                    List<List<Complex<RealAlgebraicNumber<D>>>> cr) {
        super(iu.ideal, iu.upolys);
        can = cr;
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString() + "\ncomplex roots:\n");
        sb.append("[");
        boolean f1 = true;
        for (List<Complex<RealAlgebraicNumber<D>>> lr : can) {
            if (!f1) {
                sb.append(", ");
            } else {
                f1 = false;
            }
            sb.append("[");
            boolean f2 = true;
            for (Complex<RealAlgebraicNumber<D>> rr : lr) {
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
            sb.append("\ndecimal complex root approximation:\n");
            for (List<Complex<BigDecimal>> d : droots) {
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
        return super.toScript() + ",  " + can.toString();
    }


    /**
     * Get decimal approximation of the complex root tuples.
     */
    public synchronized List<List<Complex<BigDecimal>>> decimalApproximation() {
        if (this.droots != null) {
            return droots;
        }
        List<List<Complex<BigDecimal>>> rroots = new ArrayList<List<Complex<BigDecimal>>>();
        ComplexRing<BigDecimal> cfac = new ComplexRing<BigDecimal>(new BigDecimal());
        for (List<Complex<RealAlgebraicNumber<D>>> rri : this.can) {
            List<Complex<BigDecimal>> r = new ArrayList<Complex<BigDecimal>>();
            for (Complex<RealAlgebraicNumber<D>> rr : rri) {
                BigDecimal dr = new BigDecimal(rr.getRe().magnitude());
                BigDecimal di = new BigDecimal(rr.getIm().magnitude());
                Complex<BigDecimal> d = new Complex<BigDecimal>(cfac, dr, di);
                r.add(d);
            }
            rroots.add(r);
        }
        droots = rroots;
        return rroots;
    }


    /**
     * compute decimal approximation of the complex root tuples.
     */
    public void doDecimalApproximation() {
        List<List<Complex<BigDecimal>>> unused = decimalApproximation();
        if (unused.isEmpty()) { // use for findbugs
            System.out.println("unused is empty");
        }
        return;
    }


    /**
     * Is decimal approximation of the complex roots.
     * @return true, if the decimal complex roots approximate the complex roots.
     */
    public synchronized boolean isDecimalApproximation() {
        doDecimalApproximation();
        if (droots == null || droots.size() == 0) {
            return true;
        }
        if (upolys == null || upolys.size() == 0) {
            return true;
        }
        Complex<BigDecimal> dd = droots.get(0).get(0);
        ComplexRing<BigDecimal> dr = dd.ring;
        Complex<BigDecimal> c = new Complex<BigDecimal>(dr,
                        new BigDecimal("0.15").power(BigDecimal.DEFAULT_PRECISION / 2));
        c = c.norm();
        //System.out.println("eps: c = " + c);
        Complex<BigDecimal> cc = new Complex<BigDecimal>(dr,
                        new BigDecimal("0.1").power(BigDecimal.DEFAULT_PRECISION / 3));
        cc = cc.norm();

        ComplexRing<D> cr = new ComplexRing<D>(ideal.list.ring.coFac);
        List<GenPolynomial<Complex<BigDecimal>>> upds = new ArrayList<GenPolynomial<Complex<BigDecimal>>>(
                        upolys.size());
        for (GenPolynomial<D> up : upolys) {
            GenPolynomialRing<D> pfac = up.ring;
            GenPolynomialRing<Complex<D>> cpfac = new GenPolynomialRing<Complex<D>>(cr, pfac);
            GenPolynomialRing<Complex<BigDecimal>> dpfac = new GenPolynomialRing<Complex<BigDecimal>>(dr,
                            cpfac);
            GenPolynomial<Complex<D>> upc = PolyUtil.<D> complexFromAny(cpfac, up);
            GenPolynomial<Complex<BigDecimal>> upd = PolyUtil.<D> complexDecimalFromRational(dpfac, upc);
            //System.out.println("upd = " + upd);
            upds.add(upd);
        }
        for (List<Complex<BigDecimal>> rr : droots) {
            int i = 0;
            for (GenPolynomial<Complex<BigDecimal>> upd : upds) {
                Complex<BigDecimal> d = rr.get(i++);
                Complex<BigDecimal> z = PolyUtil.<Complex<BigDecimal>> evaluateMain(dr, upd, d);
                z = z.norm();
                //System.out.println("z = " + z + ", d = " + d);
                if (z.getRe().compareTo(c.getRe()) >= 0) {
                    //System.out.println("no root: z = " + z + ", c = " + c);
                    if (z.getRe().compareTo(cc.getRe()) >= 0) {
                        System.out.println("no root: z = " + z + ", cc = " + cc);
                        return false;
                    }
                }
            }
            //System.out.println();
        }

        GenPolynomialRing<D> pfac = ideal.list.ring;
        cr = new ComplexRing<D>(pfac.coFac);
        GenPolynomialRing<Complex<D>> cpfac = new GenPolynomialRing<Complex<D>>(cr, pfac);
        GenPolynomialRing<Complex<BigDecimal>> dpfac = new GenPolynomialRing<Complex<BigDecimal>>(dr, cpfac);
        List<GenPolynomial<D>> ips = ideal.list.list;
        c = new Complex<BigDecimal>(dr, new BigDecimal("0.15").power(BigDecimal.DEFAULT_PRECISION / 2 - 1));
        for (GenPolynomial<D> ip : ips) {
            GenPolynomial<Complex<D>> ipc = PolyUtil.<D> complexFromAny(cpfac, ip);
            GenPolynomial<Complex<BigDecimal>> ipd = PolyUtil.<D> complexDecimalFromRational(dpfac, ipc);
            //System.out.println("ipd = " + ipd);
            for (List<Complex<BigDecimal>> rr : droots) {
                Complex<BigDecimal> z = PolyUtil.<Complex<BigDecimal>> evaluateAll(dr, ipd, rr);
                z = z.norm();
                //System.out.println("z = " + z + ", rr = " + rr);
                if (z.getRe().compareTo(c.getRe()) >= 0) {
                    //System.out.println("no root: z = " + z + ", c = " + c);
                    if (z.getRe().compareTo(cc.getRe()) >= 0) {
                        System.out.println("no root: z = " + z + ", cc = " + cc);
                        System.out.println("ipd = " + ipd + ", rr = " + rr);
                        return false;
                    }
                }
            }
            //System.out.println();
        }
        return true;
    }

}
