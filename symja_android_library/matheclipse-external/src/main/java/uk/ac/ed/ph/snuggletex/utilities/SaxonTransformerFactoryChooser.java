/* $Id: SaxonTransformerFactoryChooser.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import uk.ac.ed.ph.snuggletex.SnuggleRuntimeException;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;

import javax.xml.transform.TransformerFactory;

/**
 * Implementation of {@link TransformerFactoryChooser} that is hard-wired to use whichever version
 * of Saxon can be found in the ClassPath under the <tt>net.sf.saxon</tt> package hierarchy, thus
 * providing support for XSLT 2.0.
 * 
 * @since 1.2.0
 *
 * @author David McKain
 * @version $Revision: 525 $
 */
public final class SaxonTransformerFactoryChooser implements TransformerFactoryChooser {

    /** Singleton instance */
    private static final SaxonTransformerFactoryChooser singletonInstance;
    
    static {
        singletonInstance = new SaxonTransformerFactoryChooser();
    }
    
    public static SaxonTransformerFactoryChooser getInstance() {
        return singletonInstance;
    }
    
    //-----------------------------------------------------------
    
    public boolean isXSLT20SupportAvailable() {
        return true;
    }
    
    public TransformerFactory getSuitableXSLT10TransformerFactory() {
        return getSaxonTransformerFactory();
    }
    
    public TransformerFactory getSuitableXSLT20TransformerFactory() {
        return getSaxonTransformerFactory();
    }
    
    public TransformerFactory getSaxonTransformerFactory() {
        try {
            return XMLUtilities.createSaxonTransformerFactory();
        }
        catch (SnuggleRuntimeException e) {
            throw new SnuggleRuntimeException(getClass().getSimpleName()
                    + " could not instantiate the TransformerFactoryImpl provided by Saxon", e);
        }
    }
}