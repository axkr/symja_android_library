/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.jas.arith.ArithUtil;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;


/**
 * Real arithmetic utilities.
 * @author Heinz Kredel
 */

public class RealArithUtil {


    private static final Logger logger = LogManager.getLogger(RealArithUtil.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Continued fraction.
     * @param A real algebraic number.
     * @param M approximation, length of continued fraction.
     * @return continued fraction for A.
     */
    public static List<BigInteger> continuedFraction(RealAlgebraicNumber<BigRational> A, final int M) {
        List<BigInteger> cf = new ArrayList<BigInteger>();
        if (A == null) {
            return cf;
        }
        RealAlgebraicRing<BigRational> fac = A.ring;
        if (A.isZERO()) {
            cf.add(BigInteger.ZERO);
            return cf;
        }
        if (A.isONE()) {
            cf.add(BigInteger.ONE);
            return cf;
        }
        RealAlgebraicNumber<BigRational> x = A;
        BigInteger q = new BigInteger(x.floor());
        cf.add(q);
        RealAlgebraicNumber<BigRational> xd = x.subtract(fac.fromInteger(q.val));
        int m = 0;
        while (!xd.isZERO() && m++ < M) {
            //System.out.println("xd = " + xd + " :: " + xd.ring); // + ", q = " + q + ", x = " + x);
            //System.out.println("xd = " + xd.decimalMagnitude());
            x = xd.inverse();
            q = new BigInteger(x.floor());
            cf.add(q);
            xd = x.subtract(fac.fromInteger(q.val));
        }
        if (debug) {
            logger.info("cf = " + cf);
        }
        return cf;
    }


    /**
     * Continued fraction approximation.
     * @param A continued fraction.
     * @return ratonal number approximation for A.
     */
    public static BigRational continuedFractionApprox(List<BigInteger> A) {
        return ArithUtil.continuedFractionApprox(A);
    }

}
