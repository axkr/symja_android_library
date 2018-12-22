/* $Id: WebPageOutputOptions.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import static uk.ac.ed.ph.snuggletex.internal.util.ObjectUtilities.concat;

import uk.ac.ed.ph.snuggletex.definitions.W3CConstants;
import uk.ac.ed.ph.snuggletex.internal.util.ConstraintUtilities;

import javax.xml.transform.Transformer;

/**
 * Builds on {@link XMLStringOutputOptions} to add in options for configuring how to build a
 * web page using the relevant methods in {@link SnuggleSession}
 * (e.g. {@link SnuggleSession#createWebPage(WebPageOutputOptions)}).
 * <p>
 * You will generally want to use
 * {@link WebPageOutputOptionsTemplates#createWebPageOptions(WebPageOutputOptions.WebPageType)}
 * to create pre-configured instances of these Objects, which can then be tweaked as desired.
 * But you can also create and configure {@link WebPageOutputOptions} from scratch if you
 * know exactly what you want to do.
 * 
 * @see DOMOutputOptions
 * @see XMLStringOutputOptions
 * @see WebPageOutputOptionsTemplates
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public class WebPageOutputOptions extends XMLStringOutputOptions {
    
    /**
     * Enumerates the different web page "types" supported. This is used both by
     * {@link WebPageOutputOptionsTemplates} to help generate suitable instances
     * of {@link WebPageOutputOptions}, and also to tweak certain parts of the page generation
     * process.
     */
    public static enum WebPageType {
        
        /** 
         * Mozilla-compatible output. XHTML + MathML; no XML declaration; no DOCTYPE.
         * <p>
         * This is intended to be served as <tt>application/xhtml+xml</tt> with
         * encoding declared via HTTP header and <tt>meta</tt> element.
         * <p>
         * This is the best option for serving content exclusively on Mozilla-based browsers.
         * <p>
         * This will display as an XML tree on IE, which is not useful.
         */
        MOZILLA,
        
        /**
         * "Cross-browser" XHTML + MathML; has XML declaration and DOCTYPE declaration
         * consisting of the Public identifier defined in {@link W3CConstants#XHTML_11_MATHML_20_PUBLIC_IDENTIFIER}
         * and System identifier defined in {@link W3CConstants#XHTML_11_MATHML_20_SYSTEM_IDENTIFIER}.
         * The <tt>charset</tt> is declared only in the <tt>meta</tt> element in order
         * to appease MathPlayer.
         * <p>
         * Intended to be served as <tt>application/xhtml+xml</tt>
         * <p>
         * Works on both Mozilla and IE6/7 (<strong>provided</strong> MathPlayer has been installed).
         * This will display wrongly on IE6/7 if MathPlayer is not installed.
         * <p>
         * The main issue with this is that IE will want to download the relevant DTD, which
         * hinders performance slightly.
         */
        CROSS_BROWSER_XHTML,

        /**
         * HTML + MathML intended for Internet Explorer 6/7 with the MathPlayer plug-in.
         * <p>
         * Intended to be served as <tt>text/html</tt>.
         * <p>
         * This only works on IE clients with the MathPlayer plug-in preinstalled,
         * but is a good option if that's your target audience.
         * <p>
         * This will display wrongly on IE6/7 if MathPlayer is not installed.
         */
        MATHPLAYER_HTML,
        
        //----------------------------------------------------------
        // The following require further configuration
        
        /**
         * "Cross-browser" XHTML + MathML suitable for Mozilla and Internet Explorer 6/7.
         * Intended to be used in conjunction with the client-side Universal StyleSheet XSLT
         * to accommodate the two cases, prompting
         * for the download of MathPlayer on IE6/7 if it is not already installed.
         * <p>
         * Page is created with an XML declaration but no DOCTYPE declaration.
         * <p>
         * The <strong>pref:renderer</strong> attribute on the <tt>html</tt> element will be set
         * to <tt>mathplayer-dl</tt>.
         * <p>
         * You <strong>MUST</strong> also call
         * {@link #setClientSideXSLTStylesheetURLs(String...)}
         * to indicate where the USS is going to be loaded from. This <strong>MUST</strong>
         * be on a server local to the document you are serving from, because IE enforces
         * a "same origin" policy for loading XSLT stylesheets. If you don't do
         * this, your page will not work on IE.
         * 
         * <h2>Notes</h2>
         * 
         * The SnuggleTeX source distribution contains a slightly fixed version of the
         * USS that works in IE7 that you can use if you like.
         */
        UNIVERSAL_STYLESHEET,
        
        /**
         * XHTML + MathML containing one or more processing instructions designed to invoke
         * client-side XSLT. No XML declaration and no DOCTYPE.
         * <p>
         * Intended to be served as <tt>application/xhtml+xml</tt>.
         * <p>
         * Combining this with the Universal Math Stylesheet or something similar can give
         * good cross-browser results.
         */
        CLIENT_SIDE_XSLT_STYLESHEET,
        
        /**
         * HTML deemed suitable for use by any User Agent. 
         * <p>
         * Intended to be served as <tt>text/html</tt>.
         * <p>
         * You will have to use a suitable {@link DOMPostProcessor} to convert any MathML islands
         * into other forms. (E.g. replace by an applet, replace by images, ...)
         * <p>
         * This is what the SnuggleTeX JEuclid extension hooks into to do its magic. 
         */
        PROCESSED_HTML,
        
        ;
    }
    
    /** Desired "type" of web page to be constructed. Must not be null. */
    private WebPageType webPageType;
    
    /** 
     * MIME type for the resulting page.
     * Defaults to {@link WebPageOutputOptionsTemplates#DEFAULT_CONTENT_TYPE}.
     * This must not be null.
     */
    private String contentType;
    
    /** 
     * Language code for the resulting page.
     * Default is <tt>en</tt>.
     * May be set to null
     */
    private String lang;
    
    /** 
     * Title for the resulting page.
     * Default is null.
     * If null, then no title is added.
     */
    private String title;
    
    /**
     * Indicates whether page title should be inserted at the start of the web page
     * body as an XHTML <tt>h1</tt> element. This has no effect if title is null.
     */
    private boolean addingTitleHeading;
    
    /**
     * Set to include SnuggleTeX-related CSS as a <tt>style</tt> element within the resulting
     * page. If you choose not to do this, you probably want to put <tt>snuggletex.css</tt>
     * somewhere accessible and pass its location in {@link #clientSideXSLTStylesheetURLs}.
     * <p>
     * Default is true, as that's the simplest way of getting up to speed quickly.
     */
    private boolean includingStyleElement;
    
    /** 
     * Array of relative URLs specifying client-side CSS stylesheets to be specified in the
     * resulting page.
     * <p>
     * The URLs are used as-is; the caller should have ensured they make sense in advance!
     * <p>
     * The caller can use this to specify the location of <tt>snuggletex.css</tt>, as well
     * as any other required stylesheets.
     */
    private String[] cssStylesheetURLs;
    
    /** 
     * Array of relative URLs specifying client-side XSLT stylesheets to be specified in the
     * resulting page.
     * <p>
     * The URLs are used as-is; the caller should have ensured they make sense in advance!
     * <p>
     * This is ignored for {@link WebPageType#MATHPLAYER_HTML}. Also, if nothing is set
     * here for a {@link WebPageType#CLIENT_SIDE_XSLT_STYLESHEET} then {@link WebPageType#MOZILLA}
     * will be used as a template instead.
     */
    private String[] clientSideXSLTStylesheetURLs;
    
    /**
     * Optional JAXP {@link Transformer}s representing XSLT stylesheet(s) that
     * will be applied to the resulting web page once it has been built but
     * before it is serialised. This can be useful if you want to add in headers
     * and footers to the resulting XHTML web page.
     * <p>
     * Remember that the XHTML is all in its correct namespace so you will need
     * to write your stylesheet appropriately. Ensure that any further XHTML you
     * generate is also in the correct namespace; it will later be converted to
     * no-namespace HTML if required by the serialisation process.
     * <p>
     * <strong>NOTE:</strong> Source documents may contain Processing
     * Instructions (e.g. to invoke MathPlayer) so these must be handled as
     * appropriate.
     * <p>
     * If null or empty, then no stylesheet is applied.
     */
    private Transformer[] stylesheets;
    
    public WebPageOutputOptions() {
        super();
        this.contentType = WebPageOutputOptionsTemplates.DEFAULT_CONTENT_TYPE;
        this.webPageType = WebPageType.MOZILLA;
        this.lang = WebPageOutputOptionsTemplates.DEFAULT_LANG;
        this.includingStyleElement = true;
    }
    
    
    /**
     * Returns the desired "type" of web page to be constructed.
     * <p>
     * The default is {@link WebPageType#MOZILLA}
     */
    public WebPageType getWebPageType() {
        return webPageType;
    }
    
    /**
     * Sets the desired "type" of web page to be constructed.
     * 
     * @param webPageType desired type, which must not be null
     */
    public void setWebPageType(WebPageType webPageType) {
        ConstraintUtilities.ensureNotNull(webPageType, "webPageType");
        this.webPageType = webPageType;
    }
    
    
    /** 
     * Returns the MIME type for the resulting page.
     * <p>
     * Defaults to {@link WebPageOutputOptionsTemplates#DEFAULT_CONTENT_TYPE}.
     */
    public String getContentType() {
        return contentType;
    }
    
    /** 
     * Sets the MIME type for the resulting page.
     * 
     * @param contentType desired contentType, which must not be null.
     */
    public void setContentType(String contentType) {
        ConstraintUtilities.ensureNotNull(contentType, "contentType");
        this.contentType = contentType;
    }


    /**
     * Returns the language of the resulting page, null if not set.
     * <p>
     * Defaults to {@link WebPageOutputOptionsTemplates#DEFAULT_LANG}.
     */
    public String getLang() {
        return lang;
    }
    
    /**
     * Sets the language of the resulting page.
     * 
     * @param lang desired language, which may be null.
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    
    /**
     * Returns the title for the resulting page, null if not set.
     * <p>
     * Default is null.
     * <p>
     * This is used to generate a <tt>title</tt> and possible a <tt>h1</tt>
     * header if {@link #isAddingTitleHeading()} returns true.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title for the resulting page.
     * <p>
     * This is used to generate a <tt>title</tt> and possible a <tt>h1</tt>
     * header if {@link #isAddingTitleHeading()} returns true.
     * 
     * @param title title for the required page, which may be null to indicate
     *   that no title should be included. 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    
    /**
     * Returns whether page title should be inserted at the start of the web page
     * body as an XHTML <tt>h1</tt> element.
     * <p>
     * Default is false.
     * <p>
     * This has no effect if {@link #getTitle()} returns null.
     */
    public boolean isAddingTitleHeading() {
        return addingTitleHeading;
    }
    
    /**
     * Sets whether page title should be inserted at the start of the web page
     * body as an XHTML <tt>h1</tt> element.
     * <p>
     * This has no effect if {@link #getTitle()} returns null.
     * 
     * @param addingTitleHeading true to add a title header if a title has been set, false otherwise.
     */
    public void setAddingTitleHeading(boolean addingTitleHeading) {
        this.addingTitleHeading = addingTitleHeading;
    }

    
    /**
     * Returns whether to include SnuggleTeX-related CSS as a <tt>style</tt> element within the
     * resulting page. If you choose not to do this, you probably want to put <tt>snuggletex.css</tt>
     * somewhere accessible and pass its location in via {@link #setClientSideXSLTStylesheetURLs(String...)}.
     * <p>
     * Default is true, as that's the simplest way of getting up to speed quickly.
     */
    public boolean isIncludingStyleElement() {
        return includingStyleElement;
    }
    
    /**
     * Sets whether to include SnuggleTeX-related CSS as a <tt>style</tt> element within the
     * resulting page. If you choose not to do this, you probably want to put <tt>snuggletex.css</tt>
     * somewhere accessible and pass its location in via {@link #setClientSideXSLTStylesheetURLs(String...)}.
     * 
     * @param includingStyleElement set to true to include a <tt>style</tt> element, false otherwise.
     */
    public void setIncludingStyleElement(boolean includingStyleElement) {
        this.includingStyleElement = includingStyleElement;
    }


    /** 
     * Returns specified array of relative URLs specifying client-side CSS stylesheets to be
     * referenced in the resulting page.
     * <p>
     * Default is null.
     * <p>
     * The URLs are used as-is; the caller should have ensured they make sense in advance!
     * <p>
     * The caller can use this to specify the location of <tt>snuggletex.css</tt>, as well
     * as any other required stylesheets.
     */
    public String[] getCSSStylesheetURLs() {
        return cssStylesheetURLs;
    }
    
    /** 
     * Specifies an array of relative URLs specifying client-side CSS stylesheets to be
     * referenced in the resulting page.
     * <p>
     * The URLs are used as-is; the caller should have ensured they make sense in advance!
     * <p>
     * The caller can use this to specify the location of <tt>snuggletex.css</tt>, as well
     * as any other required stylesheets.
     * 
     * @param cssStylesheetURLs array of CSS stylesheet URLs, which may be empty
     */
    public void setCSSStylesheetURLs(String... cssStylesheetURLs) {
        this.cssStylesheetURLs = cssStylesheetURLs;
    }
    
    /** 
     * Appends to existing array of relative URLs specifying client-side CSS stylesheets to be
     * referenced in the resulting page.
     * <p>
     * The URLs are used as-is; the caller should have ensured they make sense in advance!
     * <p>
     * The caller can use this to specify the location of <tt>snuggletex.css</tt>, as well
     * as any other required stylesheets.
     * 
     * @param cssStylesheetURLs array of CSS stylesheet URLs to add, which may be empty
     */
    public void addCSSStylesheetURLs(String... cssStylesheetURLs) {
        this.cssStylesheetURLs = concat(this.cssStylesheetURLs, cssStylesheetURLs, String.class);
    }
    
    
    /** 
     * Returns specified array of relative URLs specifying client-side XSLT stylesheets to be
     * referenced in the resulting page.
     * <p>
     * Default is null
     * <p>
     * The URLs are used as-is; the caller should have ensured they make sense in advance!
     * <p>
     * This is ignored for {@link WebPageType#MATHPLAYER_HTML}. Also, if nothing is set
     * here for a {@link WebPageType#CLIENT_SIDE_XSLT_STYLESHEET} then {@link WebPageType#MOZILLA}
     * will be used as a template instead.
     */
    public String[] getClientSideXSLTStylesheetURLs() {
        return clientSideXSLTStylesheetURLs;
    }
    
    /** 
     * Sets an array of relative URLs specifying client-side XSLT stylesheets to be
     * referenced in the resulting page.
     * <p>
     * The URLs are used as-is; the caller should have ensured they make sense in advance!
     * <p>
     * This is ignored for {@link WebPageType#MATHPLAYER_HTML}. Also, if nothing is set
     * here for a {@link WebPageType#CLIENT_SIDE_XSLT_STYLESHEET} then {@link WebPageType#MOZILLA}
     * will be used as a template instead.
     * 
     * @param clientSideXSLTStylesheetURLs array of URLs to use, which may be empty.
     */
    public void setClientSideXSLTStylesheetURLs(String... clientSideXSLTStylesheetURLs) {
        this.clientSideXSLTStylesheetURLs = clientSideXSLTStylesheetURLs;
    }
    
    /** 
     * Appends to existing array of relative URLs specifying client-side XSLT stylesheets to be
     * referenced in the resulting page.
     * <p>
     * The URLs are used as-is; the caller should have ensured they make sense in advance!
     * <p>
     * This is ignored for {@link WebPageType#MATHPLAYER_HTML}. Also, if nothing is set
     * here for a {@link WebPageType#CLIENT_SIDE_XSLT_STYLESHEET} then {@link WebPageType#MOZILLA}
     * will be used as a template instead.
     * 
     * @param clientSideXSLTStylesheetURLs array of URLs to append, which may be empty.
     */
    public void addClientSideXSLTStylesheetURLs(String... clientSideXSLTStylesheetURLs) {
        this.clientSideXSLTStylesheetURLs = concat(this.clientSideXSLTStylesheetURLs, clientSideXSLTStylesheetURLs, String.class);
    }


    /**
     * Returns an array of specified JAXP {@link Transformer}s representing XSLT stylesheet(s)
     * that will be applied to the resulting web page once it has been built but
     * before it is serialised. This can be useful if you want to add in headers
     * and footers to the resulting XHTML web page.
     * <p>
     * Default is null.
     * <p>
     * Remember that the XHTML is all in its correct namespace so you will need
     * to write your stylesheet appropriately. Ensure that any further XHTML you
     * generate is also in the correct namespace; it will later be converted to
     * no-namespace HTML if required by the serialisation process.
     * <p>
     * <strong>NOTE:</strong> Source documents may contain Processing
     * Instructions (e.g. to invoke MathPlayer) so these must be handled as
     * appropriate.
     */
    public Transformer[] getStylesheets() {
        return stylesheets;
    }
    
    /**
     * Sets an array of JAXP {@link Transformer}s representing XSLT stylesheet(s)
     * that will be applied to the resulting web page once it has been built but
     * before it is serialised. This can be useful if you want to add in headers
     * and footers to the resulting XHTML web page.
     * <p>
     * Remember that the XHTML is all in its correct namespace so you will need
     * to write your stylesheet appropriately. Ensure that any further XHTML you
     * generate is also in the correct namespace; it will later be converted to
     * no-namespace HTML if required by the serialisation process.
     * <p>
     * <strong>NOTE:</strong> Source documents may contain Processing
     * Instructions (e.g. to invoke MathPlayer) so these must be handled as
     * appropriate.
     * 
     * @param stylesheets array of XSLT stylesheets to apply, which may be null. They
     *   are applied in the order specified.
     */
    public void setStylesheets(Transformer... stylesheets) {
        this.stylesheets = stylesheets;
    }
    
    /**
     * Appends to existing array of JAXP {@link Transformer}s representing XSLT stylesheet(s)
     * that will be applied to the resulting web page once it has been built but
     * before it is serialised. This can be useful if you want to add in headers
     * and footers to the resulting XHTML web page.
     * <p>
     * Remember that the XHTML is all in its correct namespace so you will need
     * to write your stylesheet appropriately. Ensure that any further XHTML you
     * generate is also in the correct namespace; it will later be converted to
     * no-namespace HTML if required by the serialisation process.
     * <p>
     * <strong>NOTE:</strong> Source documents may contain Processing
     * Instructions (e.g. to invoke MathPlayer) so these must be handled as
     * appropriate.
     * 
     * @param stylesheets array of additional XSLT stylesheets to apply, which may be null. They
     *   are applied in the order specified.
     */
    public void addStylesheets(Transformer... stylesheets) {
        this.stylesheets = concat(this.stylesheets, stylesheets, Transformer.class);
    }
}
