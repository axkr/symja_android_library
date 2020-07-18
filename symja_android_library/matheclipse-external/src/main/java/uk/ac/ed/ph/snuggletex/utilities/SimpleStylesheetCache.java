/* $Id: SimpleStylesheetCache.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import uk.ac.ed.ph.snuggletex.SnuggleEngine;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Templates;

/**
 * Trivial implementation of {@link StylesheetCache} that simply caches all stylesheets
 * in a {@link HashMap}.
 * <p>
 * This is used internally by {@link SnuggleEngine} and certain extensions. It might also
 * be useful in other very simple situations.
 */
public class SimpleStylesheetCache implements StylesheetCache {
    
    private final Map<String, Templates> cacheMap;
    
    public SimpleStylesheetCache() {
        this.cacheMap = new HashMap<String, Templates>();
    }
    
    public Templates getStylesheet(String key) {
        return cacheMap.get(key);
    }
    
    public void putStylesheet(String key, Templates stylesheet) {
        cacheMap.put(key, stylesheet);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "(map=" + cacheMap
            + ")";
    }
}