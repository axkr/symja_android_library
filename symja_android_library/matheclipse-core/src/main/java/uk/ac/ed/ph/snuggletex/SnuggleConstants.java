/* $Id:SnuggleConstants.java 179 2008-08-01 13:41:24Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

/**
 * Public SnuggleTeX-related constants.
 *
 * @author  David McKain
 * @version $Revision:179 $
 */
public interface SnuggleConstants {
    
    /** Namespace for any SnuggleTeX-specific XML elements produced */
    public static final String SNUGGLETEX_NAMESPACE = "http://www.ph.ed.ac.uk/snuggletex";
    
    /** 
     * Value of the "encoding" attribute added to MathML element annotation elements, used
     * when {@link DOMOutputOptions#isAddingMathSourceAnnotations()} returns true.
     */
    public static final String SNUGGLETEX_MATHML_SOURCE_ANNOTATION_ENCODING = "SnuggleTeX";
    
    /**
     * @deprecated Please use {@link #SNUGGLETEX_MATHML_SOURCE_ANNOTATION_ENCODING} from now on.
     */
    @Deprecated
    public static final String SNUGGLETEX_MATHML_ANNOTATION_ENCODING = SNUGGLETEX_MATHML_SOURCE_ANNOTATION_ENCODING;
}
