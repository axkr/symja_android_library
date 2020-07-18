/* $Id: DOMOutputOptions.java 542 2010-03-24 18:43:49Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import uk.ac.ed.ph.snuggletex.internal.util.ObjectUtilities;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;

import java.util.Properties;

/**
 * This class is used to specify how you want DOM trees to be built when calling relevant methods
 * in {@link SnuggleSession} (e.g. {@link SnuggleSession#buildDOMSubtree(org.w3c.dom.Element)}
 *
 * @author  David McKain
 * @version $Revision: 542 $
 */
public class DOMOutputOptions implements Cloneable {
    
    /** Default prefix to use if/when prefixing XHTML elements */
    public static final String DEFAULT_XHTML_PREFIX = "h";
    
    /** Default prefix to use if/when prefixing MathML elements */
    public static final String DEFAULT_MATHML_PREFIX = "m";
    
    /** Default prefix to use if/when prefixing SnuggleTeX XML elements */
    public static final String DEFAULT_SNUGGLETEX_XML_PREFIX = "s";
    
    /**
     * Enumerates the various options for representing {@link InputError}s in the resulting
     * DOM.
     */
    public static enum ErrorOutputOptions {
        
        /**
         * No error information is appended to the DOM. (Clients can still get at error information
         * via {@link SnuggleSession#getErrors()}.)
         */
        NO_OUTPUT,
        
        /**
         * Basic error information is appended to the DOM as an empty XML element containing just the
         * error code.
         */
        XML_SHORT,
        
        /**
         * Full error information is appended to the DOM as a text XML element containing the error
         * code (as an attribute) plus full textual information of the error's location.
         */
        XML_FULL,
        
        /**
         * Each error is marked up as an XHTML <tt>div</tt> element associated to the <tt>error</tt>
         * CSS class.
         * <p>
         * Errors occurring inside MathML expressions will be represented by a <tt>merror</tt>
         * placeholder, with a full XHTML element appended immediately after the MathML.
         */
        XHTML,
        ;
    }
    
    /**
     * Specifies how errors should be represented in the resulting DOM.
     */
    private ErrorOutputOptions errorOutputOptions;
    
    /** 
     * Set to true to inline CSS styling (i.e. uses <tt>style</tt> attributes). This is useful
     * if your output is going to end up embedded in someone else's page or in a system where you
     * have no control over stylesheets.
     */
    private boolean inliningCSS;
    
    /**
     * If non-null, then the given {@link Properties} will be used to work out CSS styles.
     * If null, the default CSS will be used.
     */
    private Properties inlineCSSProperties;
    
    /**
     * Set to true if you want XHTML element names to be prefixed.
     * If false, then the default namespace is changed when entering an XHTML element scope.
     * <p>
     * Default is false.
     */
    private boolean prefixingXHTML;
    
    /**
     * Prefix to use when prefixing XHTML element names.
     * Only used if {@link #prefixingXHTML} is true.
     * <p>
     * Default is {@link #DEFAULT_XHTML_PREFIX}
     * Must be non-null and a valid NCName.
     */
    private String xhtmlPrefix;

    /**
     * Set to true if you want MathML element names to be prefixed.
     * If false, then the default namespace is changed when entering a MathML element scope.
     * <p>
     * Default is false.
     */
    private boolean prefixingMathML;
    
    /**
     * Prefix to use when prefixing MathML element names.
     * Only used if {@link #prefixingMathML} is true.
     * <p>
     * Default is {@link #DEFAULT_MATHML_PREFIX}
     * Must be non-null and a valid NCName.
     */
    private String mathMLPrefix;
    
    /**
     * Set to true if you want any SnuggleTeX-specific XML elements, such
     * as error messages and certain MathML annotations to be prefixed.
     * <p>
     * If false, then the default namespace is changed when entering the scope of
     * these elements.
     * <p>
     * Default is false.
     */
    private boolean prefixingSnuggleXML;
    
    /**
     * Prefix to use when prefixing SnuggleTeX-specific XML element names.
     * Only used if {@link #prefixingSnuggleXML} is true and ignored if prefix is null.
     * <p>
     * (Note that this currently has no effect on any SnuggleTeX elements generated within
     * MathML elements.)
     * <p>
     * Default is {@link #DEFAULT_SNUGGLETEX_XML_PREFIX}.
     * Must be non-null and a valid NCName.
     */
    private String snuggleXMLPrefix;
    
    /** Set to true to annotate MathML elements with the original SnuggleTeX source. */
    private boolean addingMathSourceAnnotations;
    
    /** 
     * Set to true to apply a workaround to the MathML to make it render better in Firefox 3
     * whenever {@link #addingMathSourceAnnotations} is true.
     */
    private boolean applyingFirefox3SemanticsWorkaround;
    
    /**
     * Set to true to perform automatic mappings of (safe) Unicode characters when applying
     * "stylings" like <tt>\\mathcal</tt> and <tt>\\mathbb</tt>. Doing this can help if you
     * don't have control over CSS and if clients may not have appropriate fonts installed
     * as it forces the mapping of certain characters to glyphs that may be available.
     * <p>
     * (Firefox by default does not change fonts for these cases as it is not clear what
     * font to map to so setting this to true can help with some characters.)
     * 
     * TODO: Revisit how best to do mathvariant with Firefox.
     */
    private boolean mathVariantMapping;
    
    /**
     * List of optional {@link DOMPostProcessor} that will be called in turn to "fix up" or
     * modify the raw DOM produced by SnuggleTeX immediately after it has been built.
     * <p>
     * One use of this is by registering a {@link DownConvertingPostProcessor}, which will
     * attempt to "down-convert" simple MathML expressions into (X)HTML equivalents.
     * For example, simple linear expressions and simple sub/superscripts can often be converted
     * to acceptable XHTML alternatives.
     * Any expressions deemed too complex to be down-converted are kept as MathML.
     * <p>
     * The SnuggleTeX Up-Conversion module also includes an "UpConvertingPostProcessor" that
     * may be used here as well.
     * 
     * @see DownConvertingPostProcessor
     */
    private DOMPostProcessor[] domPostProcessors;
    
    /**
     * Optional helper to "resolve" (i.e. munge) any XHTML links found during page creation.
     * This may be useful if generating a set of hyperlinked pages.
     */
    private LinkResolver linkResolver;
    
    public DOMOutputOptions() {
        this.errorOutputOptions = ErrorOutputOptions.NO_OUTPUT;
        this.domPostProcessors = null;
        this.inliningCSS = false;
        this.inlineCSSProperties = null;
        this.prefixingXHTML = false;
        this.prefixingMathML = false;
        this.prefixingSnuggleXML = false;
        this.xhtmlPrefix = DEFAULT_XHTML_PREFIX;
        this.mathMLPrefix = DEFAULT_MATHML_PREFIX;
        this.snuggleXMLPrefix = DEFAULT_SNUGGLETEX_XML_PREFIX;
        this.addingMathSourceAnnotations = false;
        this.applyingFirefox3SemanticsWorkaround = false;
        this.mathVariantMapping = false;
        this.linkResolver = null;
    }
    
    /** 
     * Gets the current {@link ErrorOutputOptions}, specifying how (and whether) error details
     * should be embedded into the resulting XML. The default is {@link ErrorOutputOptions#NO_OUTPUT}.
     */
    public ErrorOutputOptions getErrorOutputOptions() {
        return errorOutputOptions;
    }
    
    /** 
     * Sets the current {@link ErrorOutputOptions}, specifying how (and whether) error details
     * should be embedded into the resulting XML.
     * 
     * @param errorOptions new {@link ErrorOutputOptions}, which must not be null.
     */
    public void setErrorOutputOptions(ErrorOutputOptions errorOptions) {
        if (errorOptions==null) {
            throw new IllegalArgumentException("ErrorOutputOptions must not be null");
        }
        this.errorOutputOptions = errorOptions;
    }
    
    
    /**
     * Get whether CSS styling is being inlined via <tt>style</tt> attributes or not.
     * This is useful if your output is going to end up embedded in someone else's page
     * or in a system where you have no control over stylesheets. The default is false.
     */
    public boolean isInliningCSS() {
        return inliningCSS;
    }
    
    /**
     * Set whether CSS styling is to be inlined via <tt>style</tt> attributes or not.
     * This is useful if your output is going to end up embedded in someone else's page
     * or in a system where you have no control over stylesheets.
     * 
     * @param inliningCSS true to inline CSS, false otherwise
     */
    public void setInliningCSS(boolean inliningCSS) {
        this.inliningCSS = inliningCSS;
    }
    
    
    /**
     * Gets the {@link Properties} Object that will be used to determine inline CSS properties,
     * if used. If null, the default CSS supplied within SnuggleTeX will be used. Default is null.
     */
    public Properties getInlineCSSProperties() {
        return inlineCSSProperties;
    }
    
    /**
     * Sets the {@link Properties} Object that will be used to determine inline CSS properties,
     * if used. If null, the default CSS supplied within SnuggleTeX will be used.
     * 
     * @param inlineCSSProperties Properties file used to determine inline CSS properties, which
     *   should be null if you want to use the SnuggleTeX defaults.
     */
    public void setInlineCSSProperties(Properties inlineCSSProperties) {
        this.inlineCSSProperties = inlineCSSProperties;
    }
    
    
    /**
     * Returns whether XHTML elements should be prefixed or not. Default is false.
     */
    public boolean isPrefixingXHTML() {
        return prefixingXHTML;
    }
    
    /**
     * Sets whether XHTML elements should be prefixed or not.
     * 
     * @param prefixingXHTML true to prefix XHTML elements, false otherwise.
     */
    public void setPrefixingXHTML(boolean prefixingXHTML) {
        this.prefixingXHTML = prefixingXHTML;
    }

    
    /**
     * Gets the prefix to use for XHTML elements when {@link #isPrefixingXHTML()} returns true.
     * Default is {@link #DEFAULT_XHTML_PREFIX}.
     */
    public String getXHTMLPrefix() {
        return xhtmlPrefix;
    }

    
    /**
     * Sets the prefix to use for XHTML elements when {@link #isPrefixingXHTML()} returns true.
     * 
     * @param xhtmlPrefix desired prefix, which must be non-null and a valid XML NCName.
     */
    public void setXHTMLPrefix(String xhtmlPrefix) {
        if (!XMLUtilities.isXMLNCName(xhtmlPrefix)) {
            throw new IllegalArgumentException("XHTML prefix must be a valid NCName");
        }
        this.xhtmlPrefix = xhtmlPrefix;
    }

    
    /**
     * Returns whether MathML elements should be prefixed or not. Default is false.
     */
    public boolean isPrefixingMathML() {
        return prefixingMathML;
    }
    
    /**
     * Sets whether MathML elements should be prefixed or not.
     * 
     * @param prefixingMathML true to prefix MathML elements, false otherwise.
     */
    public void setPrefixingMathML(boolean prefixingMathML) {
        this.prefixingMathML = prefixingMathML;
    }


    /**
     * Gets the prefix to use for MathML elements when {@link #isPrefixingMathML()} returns true.
     * Default is {@link #DEFAULT_MATHML_PREFIX}.
     */
    public String getMathMLPrefix() {
        return mathMLPrefix;
    }

    /**
     * Sets the prefix to use for MathML elements when {@link #isPrefixingMathML()} returns true.
     * 
     * @param mathMLPrefix desired prefix, which must be non-null and a valid XML NCName.
     */
    public void setMathMLPrefix(String mathMLPrefix) {
        if (!XMLUtilities.isXMLNCName(mathMLPrefix)) {
            throw new IllegalArgumentException("MathML prefix must be a valid NCName");
        }
        this.mathMLPrefix = mathMLPrefix;
    }
    
    
    /**
     * Returns whether custom SnuggleTeX XML elements should be prefixed or not. Default is false.
     */
    public boolean isPrefixingSnuggleXML() {
        return prefixingSnuggleXML;
    }
    
    /**
     * Sets whether custom SnuggleTeX XML elements should be prefixed or not.
     * 
     * @param prefixingSnuggleXML true to prefix custom SnuggleTeX XML elements, false otherwise.
     */
    public void setPrefixingSnuggleXML(boolean prefixingSnuggleXML) {
        this.prefixingSnuggleXML = prefixingSnuggleXML;
    }

    
    /**
     * Gets the prefix to use for custom SnuggleTeX XML elements when {@link #isPrefixingSnuggleXML()} 
     * returns true. Default is {@link #DEFAULT_SNUGGLETEX_XML_PREFIX}.
     */
    public String getSnuggleXMLPrefix() {
        return snuggleXMLPrefix;
    }

    /**
     * Sets the prefix to use for custom SnuggleTeX XML elements when {@link #isPrefixingSnuggleXML()}
     * returns true.
     * 
     * @param snuggleXMLPrefix desired prefix, which must be non-null and a valid XML NCName.
     */
    public void setSnuggleXMLPrefix(String snuggleXMLPrefix) {
        if (!XMLUtilities.isXMLNCName(snuggleXMLPrefix)) {
            throw new IllegalArgumentException("SnuggleTeX XML prefix must be a valid NCName");
        }
        this.snuggleXMLPrefix = snuggleXMLPrefix;
    }


    /** 
     * Returns whether the MathML <tt>math</tt> elements generated by SnuggleTeX should
     * be annotated with the LaTeX input used to generate them. Default is false.
     */
    public boolean isAddingMathSourceAnnotations() {
        return addingMathSourceAnnotations;
    }
    
    /** 
     * Sets whether the MathML <tt>math</tt> elements generated by SnuggleTeX should
     * be annotated with the LaTeX input used to generate them.
     * 
     * @param addingMathSourceAnnotations set true to include source annotations, false
     *   otherwise.
     */
    public void setAddingMathSourceAnnotations(boolean addingMathSourceAnnotations) {
        this.addingMathSourceAnnotations = addingMathSourceAnnotations;
    }

    
    /** @deprecated Use {@link #isAddingMathSourceAnnotations()} instead */
    @Deprecated
    public boolean isAddingMathAnnotations() {
        return isAddingMathSourceAnnotations();
    }

    /** @deprecated Use {@link #setAddingMathSourceAnnotations(boolean)} instead */
    @Deprecated
    public void setAddingMathAnnotations(boolean addingMathSourceAnnotations) {
        setAddingMathSourceAnnotations(addingMathSourceAnnotations);
    }
    
    
    /**
     * Returns whether we should wrap "display math" MathML content within an additional
     * <![CDATA[<mstyle displaystyle="true">....</mstyle>]]> wrapper when generating
     * annotated MathML when {@link #isAddingMathSourceAnnotations()} returns true.
     * <p>
     * This is a workaround for <a href="https://bugzilla.mozilla.org/show_bug.cgi?id=468059">Bug 468059</a>
     * which first seemed to appear in Firefox 3 and MathML within semantics elements to
     * effectively ignore any <tt>display="block"</tt> attribute on the enclosing MathML
     * element.
     * <p>
     * This property is ignored if {@link #isAddingMathSourceAnnotations()} returns false.
     * <p>
     * Use this if having annotations and good rendering in Firefox 3 is important to you.
     * 
     * @since 1.2.1
     * 
     * @return true if applying the workaround described above when, false otherwise.
     */
    public boolean isApplyingFirefox3SemanticsWorkaround() {
        return applyingFirefox3SemanticsWorkaround;
    }

    /**
     * Specifies whether we should wrap "display math" MathML content within an additional
     * <![CDATA[<mstyle displaystyle="true">....</mstyle>]]> wrapper when generating
     * annotated MathML when {@link #isAddingMathSourceAnnotations()} returns true.
     * <p>
     * This is a workaround for <a href="https://bugzilla.mozilla.org/show_bug.cgi?id=468059">Bug 468059</a>
     * which first seemed to appear in Firefox 3 and MathML within semantics elements to
     * effectively ignore any <tt>display="block"</tt> attribute on the enclosing MathML
     * element.
     * <p>
     * This property is ignored if {@link #isAddingMathSourceAnnotations()} returns false.
     * <p>
     * Use this if having annotations and good rendering in Firefox 3 is important to you.
     * 
     * @since 1.2.1
     * 
     * @param applyingFirefox3SemanticsWorkaround true to applying the workaround described above when, false otherwise.
     */
    public void setApplyingFirefox3SemanticsWorkaround(boolean applyingFirefox3SemanticsWorkaround) {
        this.applyingFirefox3SemanticsWorkaround = applyingFirefox3SemanticsWorkaround;
    }

    /**
     * Returns whether we should perform automatic mappings of (safe) Unicode characters when applying
     * "stylings" like <tt>\\mathcal</tt> and <tt>\\mathbb</tt>. Doing this can help if you
     * don't have control over CSS and if clients may not have appropriate fonts installed
     * as it forces the mapping of certain characters to glyphs that may be available.
     * p>
     * (Firefox by default does not change fonts for these cases as it is not clear what
     * font to map to so setting this to true can help with some characters.)
     * <p>
     * Default is false.
     */
    public boolean isMathVariantMapping() {
        return mathVariantMapping;
    }

    /**
     * Sets whether we should perform automatic mappings of (safe) Unicode characters when applying
     * "stylings" like <tt>\\mathcal</tt> and <tt>\\mathbb</tt>. Doing this can help if you
     * don't have control over CSS and if clients may not have appropriate fonts installed
     * as it forces the mapping of certain characters to glyphs that may be available.
     * p>
     * (Firefox by default does not change fonts for these cases as it is not clear what
     * font to map to so setting this to true can help with some characters.)
     * 
     * @param mathVariantMapping set true to perform mappings, false otherwise.
     */
    public void setMathVariantMapping(boolean mathVariantMapping) {
        this.mathVariantMapping = mathVariantMapping;
    }
    
    
    /**
     * Returns the array of {@link DOMPostProcessor} that will be called in turn to "fix up" or
     * modify the raw DOM produced by SnuggleTeX immediately after it has been built.
     * <p>
     * The default is null, which is treated the same as an empty array here.
     * <p>
     * One use of this is by registering a {@link DownConvertingPostProcessor}, which will
     * attempt to "down-convert" simple MathML expressions into (X)HTML equivalents.
     * For example, simple linear expressions and simple sub/superscripts can often be converted
     * to acceptable XHTML alternatives.
     * Any expressions deemed too complex to be down-converted are kept as MathML.
     * <p>
     * The SnuggleTeX Up-Conversion module also includes an "UpConvertingPostProcessor" that
     * may be used here as well.
     * 
     * @see DownConvertingPostProcessor
     */
    public DOMPostProcessor[] getDOMPostProcessors() {
        return domPostProcessors;
    }
    
    /**
     * Sets the array of {@link DOMPostProcessor} that will be called in turn to "fix up" or
     * modify the raw DOM produced by SnuggleTeX immediately after it has been built.
     * <p>
     * One use of this is by registering a {@link DownConvertingPostProcessor}, which will
     * attempt to "down-convert" simple MathML expressions into (X)HTML equivalents.
     * For example, simple linear expressions and simple sub/superscripts can often be converted
     * to acceptable XHTML alternatives.
     * Any expressions deemed too complex to be down-converted are kept as MathML.
     * <p>
     * The SnuggleTeX Up-Conversion module also includes an "UpConvertingPostProcessor" that
     * may be used here as well.
     * 
     * @see DownConvertingPostProcessor
     * 
     * @param domPostProcessors array of {@link DOMPostProcessor} to use, which may be empty.
     */
    public void setDOMPostProcessors(DOMPostProcessor... domPostProcessors) {
        this.domPostProcessors = domPostProcessors;
    }
    
    /**
     * Appends to the currently specified {@link DOMPostProcessor} that will be called in
     * turn to "fix up" or modify the raw DOM produced by SnuggleTeX immediately after it has been built.
     * 
     * @see DownConvertingPostProcessor
     * 
     * @param domPostProcessors array of {@link DOMPostProcessor} to append to currently specified
     *   {@link DOMPostProcessor}s, which may be empty.
     */
    public void addDOMPostProcessors(DOMPostProcessor... domPostProcessors) {
        this.domPostProcessors = ObjectUtilities.concat(this.domPostProcessors, domPostProcessors, DOMPostProcessor.class);
    }


    /**
     * Gets the currently specified "link resolver" which can fix up any XHTML links created by
     * certain LaTeX inputs. Default is null, which does nothing.
     */
    public LinkResolver getLinkResolver() {
        return linkResolver;
    }

    /**
     * Sets the "link resolver" that will be given a chance to fix up any XHTML links created by
     * certain LaTeX inputs.
     * 
     * @param linkResolver new {@link LinkResolver} to use, which may be null to indicate you
     *   want links left as-is.
     */
    public void setLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }


    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new SnuggleLogicException(e);
        }
    }
}
