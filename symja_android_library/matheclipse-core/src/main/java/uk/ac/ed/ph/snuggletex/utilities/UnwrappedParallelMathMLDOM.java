/* $Id: UnwrappedParallelMathMLDOM.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Trivial wrapper Object that encapsulates the results of
 * {@link MathMLUtilities#unwrapParallelMathMLDOM(Element)}.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class UnwrappedParallelMathMLDOM {
    
    /** Containing <math/> element */
    private Element mathElement;
    
    /** First branch of the <semantics/> element */
    private Element firstBranch;
    
    /** Map of all <annotation/> contents, keyed on encoding attribute */
    private final Map<String, String> textAnnotations;
    
    /** Map of all <annotation-xml/> contents, keyed on encoding attribute */
    private final Map<String, NodeList> xmlAnnotations;
    
    public UnwrappedParallelMathMLDOM() {
        this.textAnnotations = new HashMap<String, String>();
        this.xmlAnnotations = new HashMap<String, NodeList>();
    }
    
    /** Returns the containing <math/> element */
    public Element getMathElement() {
        return mathElement;
    }
    
    /** Sets the containing <math/> element */
    public void setMathElement(Element mathElement) {
        this.mathElement = mathElement;
    }

    /** Returns the first branch of the top <semantics/> element. */
    public Element getFirstBranch() {
        return firstBranch;
    }
    
    /** Sets the first branch of the top <semantics/> element. */
    public void setFirstBranch(Element firstBranch) {
        this.firstBranch = firstBranch;
    }

    /**
     * Returns a {@link Map} of <annotation/> elements, keyed on the "encoding" attribute with
     * the text content as values.
     */
    public Map<String, String> getTextAnnotations() {
        return textAnnotations;
    }

    /**
     * Returns a {@link Map} of <annotation-xml/> elements, keyed on the "encoding" attribute with
     * the {@link NodeList} content as values.
     */
    public Map<String, NodeList> getXmlAnnotations() {
        return xmlAnnotations;
    }
}
