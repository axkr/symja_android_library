/* $Id: W3CConstants.java 573 2010-05-21 10:35:31Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.definitions;

import javax.xml.XMLConstants;

/**
 * A collection of useful constants for various W3C-related topics (e.g. system identifiers,
 * namespaces, etc...).
 * <p>
 * These supplement the core XML-related constants in {@link XMLConstants}.
 * <p>
 * (This is copied from <tt>ph-commons-util</tt>.)
 * 
 * @since 1.2.0
 *
 * @author  David McKain
 * @version $Revision: 573 $
 */
public interface W3CConstants {
    
    /** Base for W3C-related stuff */
    String W3C_BASE_URI = "http://www.w3.org/";
    
    //--------------------------------------------------
    // Public and System Identifiers
    
    String XHTML_10_TRANSITIONAL_PUBLIC_IDENTIFIER = "-//W3C//DTD XHTML 1.0 Transitional//EN";
    String XHTML_10_TRANSITIONAL_SYSTEM_IDENTIFIER = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";
    
    String XHTML_10_STRICT_PUBLIC_IDENTIFIER = "-//W3C//DTD XHTML 1.0 Strict//EN";
    String XHTML_10_STRICT_SYSTEM_IDENTIFIER = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";

    String XHTML_11_MATHML_20_PUBLIC_IDENTIFIER = "-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN";
    String XHTML_11_MATHML_20_SYSTEM_IDENTIFIER = "http://www.w3.org/Math/DTD/mathml2/xhtml-math11-f.dtd";
    
    //--------------------------------------------------
    // Namespaces

    String XHTML_NAMESPACE = W3C_BASE_URI + "1999/xhtml";
    String XLINK_NAMESPACE = W3C_BASE_URI + "1999/xlink";
    String MATHML_NAMESPACE = W3C_BASE_URI + "1998/Math/MathML";
    String MATHML_PREF_NAMESPACE = W3C_BASE_URI + "2002/Math/preference";
}
