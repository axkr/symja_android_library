/*
 * $Id$
 */

package edu.jas.root;


import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
            //System.out.println("xd = " + xd + ", q = " + q + ", x = " + x);
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
        BigRational ab = BigRational.ZERO;
        if (A == null || A.isEmpty()) {
            return ab;
        }
        BigInteger a2, a1, b2, b1, a, b;
        a2 = BigInteger.ZERO;
        a1 = BigInteger.ONE;
        b2 = BigInteger.ONE;
        b1 = BigInteger.ZERO;
        for (BigInteger q : A) {
            a = q.multiply(a1).sum(a2);
            b = q.multiply(b1).sum(b2);
            //System.out.println("A/B = " + new BigRational(a,b));
            a2 = a1;
            a1 = a;
            b2 = b1;
            b1 = b;
        }
        ab = new BigRational(a1, b1);
        return ab;
    }

}
