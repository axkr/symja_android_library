/*
 * $Id$
 */

package edu.jas.poly;


import edu.jas.structure.RingElem;


/**
 * Generate Relation Tables for solvable polynomial rings. Adds the respective
 * relations to the relation table of the given solvable ring factory. Relations
 * are of the form x<sub>j</sub> * x<sub>i</sub> = x<sub>i</sub> x<sub>j</sub> +
 * p<sub>ij</sub> for 1 &le; i &lt; j &le; n = number of variables.
 * @author Heinz Kredel.
 */
public interface RelationGenerator<C extends RingElem<C>> {


    /**
     * Generates the relation table of a solvable polynomial ring.
     * @param ring solvable polynomial ring factory.
     */
    public abstract void generate(GenSolvablePolynomialRing<C> ring);

}
