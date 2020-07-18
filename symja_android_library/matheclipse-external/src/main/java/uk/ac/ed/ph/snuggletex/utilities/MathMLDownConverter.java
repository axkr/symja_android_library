/* $Id: MathMLDownConverter.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import uk.ac.ed.ph.snuggletex.DOMOutputOptions;
import uk.ac.ed.ph.snuggletex.SnuggleConstants;
import uk.ac.ed.ph.snuggletex.SnuggleRuntimeException;
import uk.ac.ed.ph.snuggletex.definitions.Globals;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;

import java.util.Properties;
import java.util.Map.Entry;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This utility class "down-converts" a DOM {@link Document} so that simple MathML expressions
 * are replaced by XHTML + CSS alternatives.
 * <p>
 * More complex MathML expressions are left as-is.
 * <p>
 * This can be used independently of SnuggleTeX if required; you may want to instantiate and
 * provide your own {@link StylesheetCache} in this case if you already do things with XSLT.
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MathMLDownConverter {
    
    private final Properties cssProperties;
    private final StylesheetManager stylesheetManager;
    
    public MathMLDownConverter() {
        this(null, (Properties) null);
    }
    
    public MathMLDownConverter(final Properties cssProperties) {
        this(null, cssProperties);
    }
    
    public MathMLDownConverter(final StylesheetCache stylesheetCache) {
        this(new StylesheetManager(stylesheetCache), (Properties) null);
    }
    
    /**
     * Creates a new {@link MathMLDownConverter} which will inline CSS properties if specified by
     * the given {@link DOMOutputOptions}.
     * 
     * @param stylesheetManager
     * @param options
     */
    public MathMLDownConverter(final StylesheetManager stylesheetManager, final DOMOutputOptions options) {
        this(stylesheetManager, options!=null && options.isInliningCSS() ? CSSUtilities.readInlineCSSProperties(options) : null);
    }
    
    public MathMLDownConverter(final StylesheetManager stylesheetManager, final Properties cssProperties) {
        this.stylesheetManager = stylesheetManager!=null ? stylesheetManager : new StylesheetManager();
        this.cssProperties = cssProperties;
    }
    
    public Document downConvertDOM(Document document) {
        /* If inlining CSS, create a document to hold the name/value pairs as described in
         * buildCSSPropertiesDocument(). Otherwise, we'll create an empty one to indicate
         * that nothing should be inlined */
        Document cssPropertiesDocument = XMLUtilities.createNSAwareDocumentBuilder().newDocument();
        if (cssProperties!=null) {
            buildCSSPropertiesDocument(cssPropertiesDocument, cssProperties);
        }
        
        /* Create URI Resolver to let the XSLT get at this document */
        CSSPropertiesURIResolver uriResolver = new CSSPropertiesURIResolver(cssPropertiesDocument);

        /* Run the conversion XSLT */
        Templates templates = stylesheetManager.getStylesheet(Globals.MATHML_TO_XHTML_XSL_RESOURCE_NAME);
        Document result = XMLUtilities.createNSAwareDocumentBuilder().newDocument();
        try {
            Transformer transformer = templates.newTransformer();
            transformer.setURIResolver(uriResolver);
            transformer.transform(new DOMSource(document), new DOMResult(result));
        }
        catch (Exception e) {
            throw new SnuggleRuntimeException("Unexpected Exception down-converting DOM", e);
        }
        return result;
    }
    
    /**
     * Converts the CSS Properties specified within the {@link DOMOutputOptions} into an XML
     * document of the form:
     * 
     * <pre><![CDATA[
     * <properties xmlns="http;//www.ph.ed.ac.uk/snuggletex">
     *   <property name="..." value="..."/>
     *   ...
     * </properties>
     * ]]></pre>
     */
    public void buildCSSPropertiesDocument(Document result, Properties cssProperties) {
        Element root = result.createElementNS(SnuggleConstants.SNUGGLETEX_NAMESPACE, "properties");
        result.appendChild(root);
        
        Element element;
        for (Entry<Object,Object> entry : cssProperties.entrySet()) {
            element = result.createElementNS(SnuggleConstants.SNUGGLETEX_NAMESPACE, "property");
            element.setAttribute("name", (String) entry.getKey());
            element.setAttribute("value", (String) entry.getValue());
            root.appendChild(element);
        }
    }
    
    /**
     * Trivial {@link URIResolver} that returns an XML Document corresponding to
     * the current session's CSS Properties when the URI
     * {@link Globals#CSS_PROPERTIES_DOCUMENT_URN} is used.
     */
    protected static final class CSSPropertiesURIResolver implements URIResolver {
        
        private final Source cssPropertiesSource;
        
        public CSSPropertiesURIResolver(final Document cssPropertiesDocument) {
            this.cssPropertiesSource = new DOMSource(cssPropertiesDocument, Globals.CSS_PROPERTIES_DOCUMENT_URN);
        }
        
        public Source resolve(String href, String base) {
            return href.equals(Globals.CSS_PROPERTIES_DOCUMENT_URN) ? cssPropertiesSource : null;
        }
    }
}
