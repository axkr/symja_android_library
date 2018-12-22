/* $Id: MathBracketInterpretation.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.semantics;

import uk.ac.ed.ph.snuggletex.internal.util.ObjectUtilities;

/**
 * Supplementary interpretation indicating that a certain operator is a MathML bracket.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathBracketInterpretation implements MathInterpretation {
    
    public static enum BracketType {
        OPENER,
        CLOSER,
        OPENER_OR_CLOSER
    }

    /** 
     * Content for resulting mfenced open/close attribute. This allows brackets
     * to map to different open/close characters if they also have meaning as standalone operators,
     * such as the '<' operator.
     */
    private final String mfencedAttributeContent;
    
    private final BracketType bracketType;
    
    /**
     * Flag denoting whether to allow pairs of brackets of this type to be inferred during
     * Token Fixing. This is not always safe for things like angle brackets, which also
     * mean less than or greater than.
     */
    private final boolean pairingInferencePossible;
    
    public MathBracketInterpretation(final String mfencedAttributeContent, final BracketType bracketType,
            final boolean pairingInferencePossible) {
        this.mfencedAttributeContent = mfencedAttributeContent;
        this.bracketType = bracketType;
        this.pairingInferencePossible = pairingInferencePossible;
    }
    
    public String getMfencedAttributeContent() {
        return mfencedAttributeContent;
    }

    public BracketType getBracketType() {
        return bracketType;
    }
    
    public boolean isPairingInferencePossible() {
        return pairingInferencePossible;
    }

    public InterpretationType getType() {
        return InterpretationType.MATH_BRACKET;
    }
    
    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
