/*
 * $Id$
 */

package edu.jas.ufd;


import java.io.Serializable;
import java.util.List;

import edu.jas.kern.Scripting;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Container for the evaluation points of a polynomial.
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class EvalPoints<C extends RingElem<C>> implements Serializable {


    /**
     * Original multivariate polynomial to be evaluated.
     */
    public final GenPolynomial<C> poly;


    /**
     * Evaluated univariate polynomial as evaluated.
     */
    public final GenPolynomial<C> upoly;


    /**
     * Evaluation points.
     */
    public final List<C> evalPoints;


    /**
     * Constructor.
     * @param p given GenPolynomial in r variables, x_{r+1}, x_{r}, ..., x_{1}.
     * @param u evaluated GenPolynomial = p(x_{r+1}, lr, ..., l1).
     * @param ep list of evaluation points, (l1, ..., lr).
     */
    public EvalPoints(GenPolynomial<C> p, GenPolynomial<C> u, List<C> ep) {
        poly = p;
        upoly = u;
        evalPoints = ep;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("EvalPoints(");
        sb.append(poly.toString());
        sb.append(", ");
        sb.append(upoly.toString());
        sb.append(", ");
        sb.append(evalPoints.toString());
        sb.append(")");
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this container.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    public String toScript() {
        StringBuffer sb = new StringBuffer();
        switch (Scripting.getLang()) {
        case Python:
            sb.append("EvalPoints(");
            break;
        case Ruby:
            sb.append("EvalPoints.new(");
        default:
        }
        sb.append(poly.toScript());
        sb.append(", ");
        sb.append(upoly.toScript());
        sb.append(", ");
        sb.append(evalPoints.toString());
        sb.append(")");
        return sb.toString();
    }

}
