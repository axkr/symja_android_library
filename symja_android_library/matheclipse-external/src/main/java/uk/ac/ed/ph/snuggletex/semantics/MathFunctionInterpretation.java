/* $Id: MathFunctionInterpretation.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.semantics;

import uk.ac.ed.ph.snuggletex.internal.util.ObjectUtilities;

/**
 * Represents a mathematical function like sin, cos, etc. These are considered as
 * identifiers in MathML but we will consider them slightly more special here.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathFunctionInterpretation implements MathInterpretation {
    
    private final String name;
    
    public MathFunctionInterpretation(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public InterpretationType getType() {
        return InterpretationType.MATH_FUNCTION;
    }
    
    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
