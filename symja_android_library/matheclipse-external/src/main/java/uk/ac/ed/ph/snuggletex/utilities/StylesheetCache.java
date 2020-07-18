/* $Id: StylesheetCache.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import uk.ac.ed.ph.snuggletex.SnuggleEngine;

import javax.xml.transform.Templates;

/**
 * Encapsulates a simple cache for the internal XSLT stylesheets used by SnuggleTeX.
 * This can be used if you want SnuggleTeX to integrate with some kind of XSLT caching mechanism
 * (e.g. your own).
 * <p>
 * A {@link SnuggleEngine} creates a default implementation of this that caches stylesheets
 * over the lifetime of the {@link SnuggleEngine} Object, which is reasonable. If you want
 * to change this, create your own implementation and attach it to your {@link SnuggleEngine}.
 * <p>
 * You can use the {@link SimpleStylesheetCache} in your own applications if you want to.
 * 
 * <h2>Internal Note</h2>
 * 
 * (I'm not currently enforcing that implementations of this should be thread-safe. Therefore, make
 * sure that you synchronise correctly when accessing an instance of this cache. You would normally
 * just use a {@link StylesheetManager} instance to do this safely.)
 * 
 * @see SimpleStylesheetCache
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public interface StylesheetCache {
   
    /**
     * Tries to retrieve an XSLT stylesheet from the cache having the given key.
     * <p>
     * Return a previously cached {@link Templates} or null if your cache doesn't want to cache
     * this or if it does not contain the required result.
     */
    Templates getStylesheet(String key);
    
    /**
     * Instructs the cache that it might want to store the given XSLT stylesheet corresponding
     * to the given key.
     * <p>
     * Implementations can safely choose to do absolutely nothing here if they want.
     */
    void putStylesheet(String key, Templates stylesheet);

}
