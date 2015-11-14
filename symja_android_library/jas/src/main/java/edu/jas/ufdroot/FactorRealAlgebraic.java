/*
 * $Id$
 */

package edu.jas.ufdroot;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.root.PolyUtilRoot;
import edu.jas.root.RealAlgebraicNumber;
import edu.jas.root.RealAlgebraicRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;


/**
 * Real algebraic number coefficients factorization algorithms. This class
 * implements factorization methods for polynomials over real algebraic numbers
 * from package
 * 
 * <pre>
 * edu.jas.root
 * </pre>
 * 
 * .
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class FactorRealAlgebraic<C extends GcdRingElem<C> & Rational> extends
                FactorAbstract<RealAlgebraicNumber<C>> {


    // TODO: is absolute possible? and what does it mean?
    //FactorAbsolute<AlgebraicNumber<C>>
    //FactorAbstract<AlgebraicNumber<C>>


    private static final Logger logger = Logger.getLogger(FactorRealAlgebraic.class);


    //private final boolean debug = logger.isInfoEnabled();


    /**
     * Factorization engine for base coefficients.
     */
    public final FactorAbstract<AlgebraicNumber<C>> factorAlgebraic;


    /**
     * No argument constructor. <b>Note:</b> can't use this constructor.
     */
    protected FactorRealAlgebraic() {
        throw new IllegalArgumentException("don't use this constructor");
    }


    /**
     * Constructor.
     * @param fac algebraic number factory.
     */
    public FactorRealAlgebraic(RealAlgebraicRing<C> fac) {
        this(fac, FactorFactory.<AlgebraicNumber<C>> getImplementation(fac.algebraic));
    }


    /**
     * Constructor.
     * @param fac algebraic number factory.
     * @param factorAlgebraic factorization engine for polynomials over base
     *            coefficients.
     */
    public FactorRealAlgebraic(RealAlgebraicRing<C> fac, FactorAbstract<AlgebraicNumber<C>> factorAlgebraic) {
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
        RealAlgebraicRing<C> rfac = (RealAlgebraicRing<C>) pfac.coFac;

        RealAlgebraicNumber<C> ldcf = P.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            P = P.monic();
            factors.add(pfac.getONE().multiply(ldcf));
        }
        //System.out.println("\nP = " + P);
        GenPolynomialRing<AlgebraicNumber<C>> afac = new GenPolynomialRing<AlgebraicNumber<C>>(
                        rfac.algebraic, pfac);
        GenPolynomial<AlgebraicNumber<C>> A = PolyUtilRoot.<C> algebraicFromRealCoefficients(afac, P);
        // factor A:
        List<GenPolynomial<AlgebraicNumber<C>>> afactors = factorAlgebraic.baseFactorsSquarefree(A);
        for (GenPolynomial<AlgebraicNumber<C>> a : afactors) {
            GenPolynomial<RealAlgebraicNumber<C>> p = PolyUtilRoot.<C> realFromAlgebraicCoefficients(pfac, a);
            factors.add(p);
        }
        logger.info("real algebraic factors = " + factors);
        return factors;
    }

}
