/* $Id: DefaultTransformerFactoryChooser.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import uk.ac.ed.ph.snuggletex.SnuggleRuntimeException;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;

import javax.xml.transform.TransformerFactory;

/**
 * Default implementation of {@link TransformerFactoryChooser} that uses the usual JAXP factory
 * lookup mechanisms for XSLT 1.0 stylesheets, and hard-codes to SAXON for XSLT 2.0 stylesheets.
 * 
 * @since 1.2.0
 *
 * @author David McKain
 * @version $Revision: 525 $
 */
public final class DefaultTransformerFactoryChooser implements TransformerFactoryChooser {

    /** Singleton instance */
    private static final DefaultTransformerFactoryChooser singletonInstance;
    
    static {
        singletonInstance = new DefaultTransformerFactoryChooser();
    }
    
    public static DefaultTransformerFactoryChooser getInstance() {
        return singletonInstance;
    }
    
    //-----------------------------------------------------------
    
    public boolean isXSLT20SupportAvailable() {
        return XMLUtilities.isSaxonAvailable();
    }
    
    public TransformerFactory getSuitableXSLT10TransformerFactory() {
        try {
            return XMLUtilities.createJAXPTransformerFactory();
        }
        catch (SnuggleRuntimeException e) {
            throw new SnuggleRuntimeException(getClass().getSimpleName()
                    + " could not select, create and configure a suitable XSLT 1.0 processor", e);
        }
    }
    
    public TransformerFactory getSuitableXSLT20TransformerFactory() {
        try {
            return XMLUtilities.createSaxonTransformerFactory();
        }
        catch (SnuggleRuntimeException e) {
            throw new SnuggleRuntimeException(getClass().getSimpleName()
                    + " could not select, create and configure a suitable XSLT 2.0 processor", e);
        }
    }
}