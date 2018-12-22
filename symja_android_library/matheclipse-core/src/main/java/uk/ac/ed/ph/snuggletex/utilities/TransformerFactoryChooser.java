/* $Id:XMLFactory.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import uk.ac.ed.ph.snuggletex.SnuggleRuntimeException;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

/**
 * This interface allows you to specify how XSLT implementations should be instantiated.
 * This can sometimes be useful if you want to explicitly avoid the standard JAXP
 * {@link TransformerFactory#newInstance()} loading mechanism.
 * <p>
 * This is managed via {@link StylesheetManager}.
 * <p>
 * The default implementation is {@link DefaultTransformerFactoryChooser} which is used
 * unless you explicitly request something different.
 * 
 * @since 1.2.0
 * 
 * @see DefaultTransformerFactoryChooser
 * @see SaxonTransformerFactoryChooser
 *
 * @author  David McKain
 * @version $Revision:2824 $
 */
public interface TransformerFactoryChooser {
    
    /**
     * Implementors of this interface should indicate whether support for XSLT 2.0 is available.
     */
    boolean isXSLT20SupportAvailable();
    
    /**
     * Implementors of this interface should return an XSLT 1.0-compatible {@link TransformerFactory}
     * that SnuggleTeX is free to mess around with and is capable of handling {@link DOMSource}
     * and {@link DOMResult}s.
     * 
     * @throws SnuggleRuntimeException if no such factory could not obtained.
     */
    TransformerFactory getSuitableXSLT10TransformerFactory();
    
    /**
     * Implementors of this interface should return an XSLT 2.0-compatible {@link TransformerFactory}
     * that SnuggleTeX is free to mess around with and is capable of handling {@link DOMSource}
     * and {@link DOMResult}s.
     * 
     * @throws SnuggleRuntimeException if no such factory could not obtained.
     */
    TransformerFactory getSuitableXSLT20TransformerFactory();

}