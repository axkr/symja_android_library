/* $Id: MathOperatorInterpretation.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.semantics;

import uk.ac.ed.ph.snuggletex.internal.util.ObjectUtilities;

/**
 * Represents a generic Mathematical operator, associating it with the content of 
 * the resulting MathML <tt>mo</tt> element.
 * 
 * @see MathBracketInterpretation
 * @see MathNegatableInterpretation
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathOperatorInterpretation implements MathInterpretation {
    
    private final String mathmlOperatorContent;
    
    public MathOperatorInterpretation(final String mathmlOperatorContent) {
        this.mathmlOperatorContent = mathmlOperatorContent;
    }
    
    public String getMathMLOperatorContent() {
        return mathmlOperatorContent;
    }
    
    public InterpretationType getType() {
        return InterpretationType.MATH_OPERATOR;
    }
    
    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
