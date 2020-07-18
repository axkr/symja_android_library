/* $Id: ClassPathURIResolver.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * Trivial implementation of {@link URIResolver} that looks for resources via the
 * {@link ClassLoader}.
 * <p>
 * Such resources should be specified via a special URI scheme called {@link #URI_SCHEME},
 * i.e. of the form <tt>classpath:/uk/ac/ed/ph/snuggletex/thingy.xsl</tt>.
 * <p>
 * (The '/' is required here, unlike the similar mechanism in Spring Framework.)
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class ClassPathURIResolver implements URIResolver {
    
    public static final String URI_SCHEME = "classpath";
    
    private static final ClassPathURIResolver singletonInstance = new ClassPathURIResolver();
    
    private ClassPathURIResolver() {
        /* (We'll make this a singleton as it is stateless) */
    }
    
    public static ClassPathURIResolver getInstance() {
        return singletonInstance;
    }
    
    public Source resolve(String href, String base) throws TransformerException {
        /* Handle the special document('') case as normal */
        if (href.equals("")) {
            return null;
        }
        
        /* Resolve href against base URI */
        URI baseUri;
        try {
            baseUri = new URI(base);
        }
        catch (URISyntaxException e) {
            throw new TransformerException("Could not convert base=" + base + " into a URI", e);
        }
        URI resolvedUri = baseUri.resolve(href);

        /* See if it's one of our URI schemes. If so, resolve */
        Source result = null;
        if (URI_SCHEME.equals(resolvedUri.getScheme())) {
            /* Strip off the leading '/' from the path */
            String resourceLocation = resolvedUri.getPath().substring(1);
            InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourceLocation);
            if (resourceStream!=null) {
                result = new StreamSource(resourceStream, resolvedUri.toString());
            }
            else {
                throw new TransformerException("Could not load resource at " + resourceLocation + " via ClassLoader");
            }
        }
        return result;
    }
}
