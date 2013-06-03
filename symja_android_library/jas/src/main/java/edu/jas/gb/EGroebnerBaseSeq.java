/*
 * $Id$
 */

package edu.jas.gb;


import edu.jas.structure.RingElem;


/**
 * E-Groebner Base sequential algorithm. Nearly empty class, only the
 * e-reduction is used instead of d-reduction. <b>Note:</b> Minimal reduced GBs
 * are again unique. see BWK, section 10.1.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class EGroebnerBaseSeq<C extends RingElem<C>> extends DGroebnerBaseSeq<C> {


    //private static final Logger logger = Logger.getLogger(EGroebnerBaseSeq.class);
    //private final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    protected EReduction<C> ered; // shadow super.red ??


    /**
     * Constructor.
     */
    public EGroebnerBaseSeq() {
        this(new EReductionSeq<C>());
    }


    /**
     * Constructor.
     * @param ered E-Reduction engine
     */
    public EGroebnerBaseSeq(EReductionSeq<C> ered) {
        super(ered);
        this.ered = ered;
        assert this.ered == super.red;
    }


}
