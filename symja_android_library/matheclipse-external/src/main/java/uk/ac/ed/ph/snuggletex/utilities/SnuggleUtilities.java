/* $Id: SnuggleUtilities.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import uk.ac.ed.ph.snuggletex.DOMOutputOptions;
import uk.ac.ed.ph.snuggletex.SnuggleConstants;

import java.util.regex.Pattern;

import org.w3c.dom.Element;

/**
 * A collection of occasionally useful SnuggleTeX-related utility methods, as well
 * as some more general LaTeX stuff.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class SnuggleUtilities {
    
    private static final Pattern charsToVerbPattern = Pattern.compile("([\\\\^~<>|]+)");
    private static final Pattern charsToBackslashPattern = Pattern.compile("([%#_$&\\{\\}])");

    /**
     * Quotes the given (ASCII) String so that it could be safely input in TEXT Mode in
     * LaTeX input.
     * 
     * @param text text to quote, assumed to be ASCII.
     * @return quoted text suitable for being input into LaTeX.
     */
    public static String quoteTextForInput(String text) {
        /* (Use '-' as \\verb delimiter here is '|' is special and we want a character
         * that won't be replaced in the next line as well) */
        String result = charsToVerbPattern.matcher(text).replaceAll("\\\\verb-$1-");
        result = charsToBackslashPattern.matcher(result).replaceAll("\\\\$1");
        return result;
    }
    
    /**
     * Extracts a SnuggleTeX annotation from a MathML <tt>math</tt> element, if found. This allows
     * you to extract the input LaTeX from a MathML element created by SnuggleTeX, provided that
     * {@link DOMOutputOptions#isAddingMathSourceAnnotations()} returned true when the element was
     * generated.
     * 
     * @param mathmlElement
     * @return SnuggleTeX encoding, or null if not present.
     */
    public static String extractSnuggleTeXAnnotation(Element mathmlElement) {
        return MathMLUtilities.extractAnnotationString(mathmlElement, SnuggleConstants.SNUGGLETEX_MATHML_SOURCE_ANNOTATION_ENCODING);
    }
}
