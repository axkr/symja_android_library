/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.Rational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.FactorAbstract;


/**
 * Real algebraic number coefficients factorization algorithms. This class
 * implements factorization methods for polynomials over bi-variate real
 * algebraic numbers from package
 * 
 * <pre>
 * edu.jas.application
 * </pre>
 * 
 * .
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class FactorRealReal<C extends GcdRingElem<C> & Rational> extends
                FactorAbstract<RealAlgebraicNumber<C>> {


    // TODO: is absolute possible? and what does it mean?
    //FactorAbsolute<AlgebraicNumber<C>>
    //FactorAbstract<AlgebraicNumber<C>>


    private static final Logger logger = Logger.getLogger(FactorRealReal.class);


    private final boolean debug = logger.isInfoEnabled();


    /**
     * Factorization engine for base coefficients.
     */
    public final FactorAbstract<edu.jas.root.RealAlgebraicNumber<C>> factorAlgebraic;


    /**
     * No argument constructor. <b>Note:</b> can't use this constructor.
     */
    protected FactorRealReal() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param fac algebraic number factory.
     */
    public FactorRealReal(RealAlgebraicRing<C> fac) {
        // ignore recursion, as it is handled in FactorRealAlgebraic:
        this(
                        fac,
                        FactorFactory.<edu.jas.root.RealAlgebraicNumber<C>> getImplementation((edu.jas.root.RealAlgebraicRing<C>) (Object) fac.realRing));
    }


    /**
     * Constructor.
     * @param fac algebraic number factory.
     * @param factorAlgebraic factorization engine for polynomials over base
     *            coefficients.
     */
    public FactorRealReal(RealAlgebraicRing<C> fac,
                    FactorAbstract<edu.jas.root.RealAlgebraicNumber<C>> factorAlgebraic) {
        super(fac);
        this.factorAlgebraic = factorAlgebraic;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree GenPolynomial&lt;RealAlgebraicNumber&lt;C&gt;&gt;.
     * @return [p_1,...,p_k] with P = prod_{i=1, ..., k} p_i.
     */
    @Override
    public List<GenPolynomial<RealAlgebraicNumber<C>>> baseFactorsSquarefree(
                    GenPolynomial<RealAlgebraicNumber<C>> P) {
        if (P == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<RealAlgebraicNumber<C>>> factors = new ArrayList<GenPolynomial<RealAlgebraicNumber<C>>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<RealAlgebraicNumber<C>> pfac = P.ring; // Q(alpha)[x]
        if (pfac.nvar > 1) {
            throw new IllegalArgumentException("only for univariate polynomials");
        }
        RealAlgebraicRing<C> rere = (RealAlgebraicRing<C>) pfac.coFac;
        edu.jas.root.RealAlgebraicRing<C> rfac = (edu.jas.root.RealAlgebraicRing<C>) (Object) rere.realRing;

        RealAlgebraicNumber<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            P = P.monic();
            factors.add(pfac.getONE().multiply(ldcf));
        }
        //System.out.println("\nP = " + P);
        GenPolynomialRing<edu.jas.root.RealAlgebraicNumber<C>> arfac = new GenPolynomialRing<edu.jas.root.RealAlgebraicNumber<C>>(
                        rfac, pfac);
        GenPolynomial<edu.jas.root.RealAlgebraicNumber<C>> A = PolyUtilApp.<C> realAlgFromRealCoefficients(
                        arfac, P);
        // factor A:
        List<GenPolynomial<edu.jas.root.RealAlgebraicNumber<C>>> afactors = factorAlgebraic
                        .baseFactorsSquarefree(A);
        for (GenPolynomial<edu.jas.root.RealAlgebraicNumber<C>> a : afactors) {
            GenPolynomial<RealAlgebraicNumber<C>> p = PolyUtilApp.<C> realFromRealAlgCoefficients(pfac, a);
            factors.add(p);
        }
        if (debug) {
            logger.info("rafactors = " + factors);
        }
        return factors;
    }

}
