/* $Id:StyleDeclarationInterpretation.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.semantics;

/**
 * Represents styled text in either MATH and/or TEXT Modes.
 * 
 * @author  David McKain
 * @version $Revision:179 $
 */
public enum StyleDeclarationInterpretation implements TextInterpretation {

    BF("div", "bf", "b",    null, "bold"),
    RM("div", "rm", "span", "rm", "normal"),
    EM("div", "em", "em",   null, "italic"),
    IT("div", "it", "i",    null, "italic"),
    TT("div", "tt", "tt",   null, "monospace"),
    SC("div", "sc", "span", "sc", null),
    SL("div", "sl", "span", "sl", null),
    SF("div", "sf", "span", "sf", "sans-serif"),

    TINY("div", "tiny", "span", "tiny", null),
    SCRIPTSIZE("div", "scriptsize", "span", "scriptsize", null),
    FOOTNOTESIZE("div", "footnotesize", "span", "footnotesize", null),
    SMALL("div", "small", "span", "small", null),
    NORMALSIZE("div", "normalsize", "span", "normalsize", null),
    LARGE("div", "large", "span", "large", null),
    LARGE_2("div", "large2", "span", "large2", null),
    LARGE_3("div", "large3", "span", "large3", null),
    HUGE("div", "huge", "span", "huge", null),
    HUGE_2("div", "huge2", "span", "huge2", null),

    UNDERLINE("div", "underline", "span", "underline", null),
    
    ;
    
    /** Name of resulting XHTML block element name */
    private final String targetBlockXHTMLElementName;
    
    /** Name of resulting CSS class for XHTML block elements */
    private final String targetBlockCSSClassName;
    
    /** Name of resulting XHTML inline element name */
    private final String targetInlineXHTMLElementName;
    
    /** Name of resulting CSS class for XHTML inline elements */
    private final String targetInlineCSSClassName;
    
    /** 
     * Name of 'variant' attribute in resulting MathML <mstyle/> element, if supported, or null
     * if this style cannot be used in Math mode.
     */
    private final String targetMathMLMathVariantName;
    
    private StyleDeclarationInterpretation(final String targetBlockXHTMLElementName,
            final String targetBlockCSSClassName, final String targetInlineXHTMLElementName,
            final String targetInlineCSSClassName, final String targetMathMLMathVariantName) {
        this.targetBlockXHTMLElementName = targetBlockXHTMLElementName;
        this.targetBlockCSSClassName = targetBlockCSSClassName;
        this.targetInlineXHTMLElementName = targetInlineXHTMLElementName;
        this.targetInlineCSSClassName = targetInlineCSSClassName;
        this.targetMathMLMathVariantName = targetMathMLMathVariantName;
    }
    
    public String getTargetBlockXHTMLElementName() {
        return targetBlockXHTMLElementName;
    }
    
    public String getTargetBlockCSSClassName() {
        return targetBlockCSSClassName;
    }


    public String getTargetInlineXHTMLElementName() {
        return targetInlineXHTMLElementName;
    }
    
    public String getTargetInlineCSSClassName() {
        return targetInlineCSSClassName;
    }

    public String getTargetMathMLMathVariantName() {
        return targetMathMLMathVariantName;
    }

    public InterpretationType getType() {
        return InterpretationType.STYLE_DECLARATION;
    }
}
