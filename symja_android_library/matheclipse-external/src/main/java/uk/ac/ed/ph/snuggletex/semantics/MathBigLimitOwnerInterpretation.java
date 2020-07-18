/* $Id: MathBigLimitOwnerInterpretation.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.semantics;

import uk.ac.ed.ph.snuggletex.internal.util.ObjectUtilities;

/**
 * Supplementary marker interpretation to be attached to operators which can "own big limits",
 * in the sense that they should be rendered in MathML using munder/mover/munderover
 * rather than msub/msup/msubsup.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathBigLimitOwnerInterpretation implements MathInterpretation {

    public InterpretationType getType() {
        return InterpretationType.MATH_BIG_LIMIT_OWNER;
    }
    
    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
