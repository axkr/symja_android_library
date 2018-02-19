/*
 * $Id$
 */

package edu.jas.gb;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Serializable subclass to hold critical pairs of polynomials.
 * Used also to manage reduction status of the pair.
 *
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class CriticalPair<C extends RingElem<C>> extends AbstractPair<C> {

    protected volatile boolean inReduction;
    protected volatile GenPolynomial<C> reductum;
    //public final ExpVector sugar;


    /**
     * CriticalPair constructor.
     *
     * @param e  lcm(lt(pi),lt(pj).
     * @param pi polynomial i.
     * @param pj polynomial j.
     * @param i  index of pi.
     * @param j  index pf pj.
     */
    public CriticalPair(ExpVector e,
                        GenPolynomial<C> pi, GenPolynomial<C> pj,
                        int i, int j) {
        super(e, pi, pj, i, j);
        inReduction = false;
        reductum = null;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(super.toString() + "[ ");
        if (inReduction) {
            s.append("," + inReduction);
        }
        if (reductum != null) {
            s.append("," + reductum.leadingExpVector());
        }
        s.append(" ]");
        return s.toString();
    }


    /**
     * Set in reduction status.
     * inReduction is set to true.
     */
    public void setInReduction() {
        if (inReduction) {
            throw new IllegalStateException("already in reduction " + this);
        }
        inReduction = true;
    }


    /**
     * Get in reduction status.
     *
     * @return true if the polynomial is currently in reduction, else false.
     */
    public boolean getInReduction() {
        return inReduction;
    }


    /**
     * Get reduced polynomial.
     *
     * @return the reduced polynomial or null if not done.
     */
    public GenPolynomial<C> getReductum() {
        return reductum;
    }


    /**
     * Set reduced polynomial.
     *
     * @param r the reduced polynomial.
     */
    public void setReductum(GenPolynomial<C> r) {
        if (r == null) {
            throw new IllegalArgumentException("reduction null not allowed " + this);
        }
        inReduction = false;
        reductum = r;
    }


    /**
     * Is reduced to zero.
     *
     * @return true if the S-polynomial of this CriticalPair
     * was reduced to ZERO, else false.
     */
    public boolean isZERO() {
        if (reductum == null) { // not jet done
            return false;
        }
        return reductum.isZERO();
    }


    /**
     * Is reduced to one.
     *
     * @return true if the S-polynomial of this CriticalPair was
     * reduced to ONE, else false.
     */
    public boolean isONE() {
        if (reductum == null) { // not jet done
            return false;
        }
        return reductum.isONE();
    }

}

