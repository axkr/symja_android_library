/* $Id: SnuggleSession.java 577 2010-05-21 14:36:50Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import uk.ac.ed.ph.snuggletex.definitions.BuiltinCommand;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.UserDefinedCommand;
import uk.ac.ed.ph.snuggletex.definitions.UserDefinedEnvironment;
import uk.ac.ed.ph.snuggletex.internal.DOMBuildingController;
import uk.ac.ed.ph.snuggletex.internal.LaTeXTokeniser;
import uk.ac.ed.ph.snuggletex.internal.SessionContext;
import uk.ac.ed.ph.snuggletex.internal.SnuggleInputReader;
import uk.ac.ed.ph.snuggletex.internal.SnuggleParseException;
import uk.ac.ed.ph.snuggletex.internal.TokenFixer;
import uk.ac.ed.ph.snuggletex.internal.WebPageBuilder;
import uk.ac.ed.ph.snuggletex.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;
import uk.ac.ed.ph.snuggletex.utilities.SerializationOptions;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This represents a single "job" for SnuggleTeX.
 * 
 * <h2>Usage</h2>
 * 
 * <ul>
 *   <li>
 *     Create a session with {@link SnuggleEngine#createSession()}, optionally passing
 *     configuration details for this session.
 *   </li>
 *   <li>
 *     Call {@link #parseInput(SnuggleInput)} on one or more input documents to tokenise and
 *     fix-up the LaTeX contained therein.
 *   </li>
 *   <li>
 *     Call one or more of the DOM and/or web page building methods to generate the appropriate
 *     type of output.
 *   </li>
 *   <li>
 *     Call {@link #getErrors()} to get at the errors that have arisen during this session.
 *   </li>
 *   <li>
 *     Call {@link #createSnapshot()} at any time to make a "snapshot" of the current state for
 *     later reuse.
 *   </li>
 *   <li>
 *     An instance of this Class should only be used by one Thread at a time. It is stateful and
 *     intended to be discarded after use.
 *   </li>
 * </ul>
 *
 * @author  David McKain
 * @version $Revision: 577 $
 */
public final class SnuggleSession implements SessionContext {
    
    /**
     * Trivial enumeration which specifies the possible actions to perform once we
     * finish writing with the various {@link SnuggleSession#writeWebPage(WebPageOutputOptions, OutputStream)}
     * methods.
     * 
     * @since 1.2.2
     */
    public static enum EndOutputAction {
        /** Closes the {@link OutputStream} after writing */
        CLOSE,
        
        /** Flushes the {@link OutputStream} after writing */
        FLUSH,
        
        /** Does nothing after writing */
        DO_NOTHING
    };
    
    /** Engine that created this Session */
    private final SnuggleEngine engine;

    /** {@link LaTeXTokeniser} used to parse inputs */
    private final LaTeXTokeniser tokeniser;
    
    /** {@link TokenFixer} used to massage inputs after parsing */
    private final TokenFixer tokenFixer;
    
    //-------------------------------------------------
    // Stateful stuff
    
    /** Configuration for this session */
    private final SessionConfiguration configuration;
    
    /** Errors accumulated during this session */
    private final List<InputError> errors;
    
    /** Map of user-defined commands, keyed on name */
    private final Map<String, UserDefinedCommand> userCommandMap;
    
    /** Map of user-defined environments, keyed on name */
    private final Map<String, UserDefinedEnvironment> userEnvironmentMap;
    
    /** All tokens currently completely parsed (and fixed) by this session */
    private final List<FlowToken> parsedTokens; 
    
    /**
     * (This package-private constructor is used when creating a new session via
     * {@link SnuggleEngine#createSession()} et al.)
     */
    SnuggleSession(final SnuggleEngine engine, final SessionConfiguration configuration) {
        this.engine = engine;
        
        /* We'll clone the supplied configuration, if supplied, so that
         * any run-time changes made to it do not affect the caller's version
         * of the configuration.
         */
        this.configuration = configuration;

        /* Set up main worker Objects */
        this.tokeniser = new LaTeXTokeniser(this);
        this.tokenFixer = new TokenFixer(this);
        
        /* Initialise session state */
        this.errors = new ArrayList<InputError>();
        this.userCommandMap = new HashMap<String, UserDefinedCommand>();
        this.userEnvironmentMap = new HashMap<String, UserDefinedEnvironment>();
        this.parsedTokens = new ArrayList<FlowToken>();
    }
    
    /**
     * (This package-private constructor is used when creating a session from an existing
     * {@link SnuggleSnapshot} via {@link SnuggleSnapshot#createSession()}.)
     */
    SnuggleSession(final SnuggleSnapshot snapshot) {
        /* Set up main worker Objects */
        this.tokeniser = new LaTeXTokeniser(this);
        this.tokenFixer = new TokenFixer(this);
        
        /* Copy stuff from the template */
        this.engine = snapshot.engine;
        this.configuration = (SessionConfiguration) snapshot.configuration.clone();
        this.errors = new ArrayList<InputError>(snapshot.errors);
        this.userCommandMap = new HashMap<String, UserDefinedCommand>(snapshot.userCommandMap);
        this.userEnvironmentMap = new HashMap<String, UserDefinedEnvironment>(snapshot.userEnvironmentMap);
        this.parsedTokens = new ArrayList<FlowToken>(snapshot.parsedTokens);
    }
    
    //-------------------------------------------------
    
    public SessionConfiguration getConfiguration() {
        return configuration;
    }

    public List<InputError> getErrors() {
        return errors;
    }
    
    public List<FlowToken> getParsedTokens() {
        return parsedTokens;
    }
    
    //---------------------------------------------
    // Public "business" methods

    /**
     * Parses the data contained within the given {@link SnuggleInput}, and performs fixing
     * on the resulting tokens.
     * 
     * @param snuggleInput input to parse, which must not be null
     * 
     * @return true if parsing finished, false if it was terminated by an error in the
     *   input LaTeX and if the session was configured to fail on the first error.
     */
    public boolean parseInput(SnuggleInput snuggleInput) throws IOException {
        ConstraintUtilities.ensureNotNull(snuggleInput, "snuggleInput");
        /* Perform tokenisation, then fix up and store the results */
        try {
            SnuggleInputReader reader = new SnuggleInputReader(this, snuggleInput);
            ArgumentContainerToken result = tokeniser.tokenise(reader);
            tokenFixer.fixTokenTree(result);
            parsedTokens.addAll(result.getContents());
        }
        catch (SnuggleParseException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Creates a {@link SnuggleSnapshot} Object holding the current state of this session that can
     * be later used to recreate a session having exactly the same state.
     */
    public SnuggleSnapshot createSnapshot() {
        return new SnuggleSnapshot(engine,
                (SessionConfiguration) configuration.clone(),
                new ArrayList<InputError>(errors), 
                new HashMap<String, UserDefinedCommand>(userCommandMap),
                new HashMap<String, UserDefinedEnvironment>(userEnvironmentMap),
                new ArrayList<FlowToken>(parsedTokens));
    }
    
    /**
     * Resets this {@link SnuggleSession} back to an initial blank state
     */
    public void reset() {
        errors.clear();
        userCommandMap.clear();
        userEnvironmentMap.clear();
        parsedTokens.clear();
        tokeniser.reset();
    }
    
    //---------------------------------------------
    
    /**
     * Builds a DOM sub-tree based on the currently parsed tokens, appending the results as
     * children of the given target root Element. The given {@link DOMOutputOptions} Object
     * is used to configure the process.
     * <p>
     * If the {@link DOMOutputOptions} specifies that MathML should be down-converted to
     * XHTML where possible, then this will also happen.
     * 
     * @return true if completed successfully, false if the process was terminated by an error in the
     *   input LaTeX and if the session was configured to fail on the first error. 
     */
    public boolean buildDOMSubtree(final Element targetRoot, final DOMOutputOptions options) {
        ConstraintUtilities.ensureNotNull(targetRoot, "targetRoot");
        ConstraintUtilities.ensureNotNull(options, "options");
        try {
            new DOMBuildingController(this, options).buildDOMSubtree(targetRoot, parsedTokens);
            return true;
        }
        catch (SnuggleParseException e) {
            return false;
        }
    }
    
    /**
     * Builds a DOM sub-tree based on the currently parsed tokens, appending the results as
     * children of the given target root Element. This uses the default {@link DOMOutputOptions}
     * associated with this engine.
     * 
     * @return true if completed successfully, false if the process was terminated by an error in the
     *   input LaTeX and if the session was configured to fail on the first error. 
     */
    public boolean buildDOMSubtree(final Element targetRoot) {
        return buildDOMSubtree(targetRoot, engine.getDefaultDOMOutputOptions());
    }
    
    /**
     * Convenience method to build a DOM {@link NodeList} representing the converted Tokens.
     * These Nodes will belong to a "fake root" element in the
     * {@link SnuggleConstants#SNUGGLETEX_NAMESPACE} namespace called "root".
     * <p>
     * The default {@link DOMOutputOptions} specified in the {@link SnuggleEngine} will be
     * used.
     * 
     * @return resulting {@link NodeList} if the process completed successfully, null if the process was
     *   terminated by an error in the input LaTeX and if the session was configured to fail on
     *   the first error. 
     */
    public NodeList buildDOMSubtree() {
        return buildDOMSubtree(engine.getDefaultDOMOutputOptions());
    }
    
    /**
     * Convenience method to build a DOM {@link NodeList} representing the converted Tokens.
     * These Nodes will belong to a "fake root" element in the
     * {@link SnuggleConstants#SNUGGLETEX_NAMESPACE} namespace called "root".
     * <p>
     * The given {@link DOMOutputOptions} Object is used to configure the process, which must
     * not be null.
     * 
     * @param options {@link DOMOutputOptions} to use, which must not be null
     * 
     * @return resulting {@link NodeList} if the process completed successfully, null if the process was
     *   terminated by an error in the input LaTeX and if the session was configured to fail on
     *   the first error. 
     */
    public NodeList buildDOMSubtree(final DOMOutputOptions options) {
        ConstraintUtilities.ensureNotNull(options, "DOMOutputOptions");
        Document document = XMLUtilities.createNSAwareDocumentBuilder().newDocument();
        Element temporaryRoot = document.createElementNS(SnuggleConstants.SNUGGLETEX_NAMESPACE, "root");
        document.appendChild(temporaryRoot);
        if (!buildDOMSubtree(temporaryRoot, options)) {
            return null;
        }
        return temporaryRoot.getChildNodes();
    }
    
    //---------------------------------------------
    
    /**
     * Creates a well-formed external general parsed entity out of the currently parsed tokens.
     * <p>
     * The default {@link XMLStringOutputOptions} specified in the {@link SnuggleEngine} will be
     * used.
     * 
     * @return resulting XML if the process completed successfully, null if the process was
     *   terminated by an error in the input LaTeX and if the session was configured to fail on
     *   the first error.  
     */
    public String buildXMLString() {
        return buildXMLString(engine.getDefaultXMLStringOutputOptions());
    }
    
    /**
     * Creates a well-formed external general parsed entity out of the currently parsed tokens.
     * <p>
     * The given {@link XMLStringOutputOptions} Object is used to configure the process.
     * 
     * @param options {@link XMLStringOutputOptions} to use, which must not be null.
     * 
     * @return resulting XML if the process completed successfully, null if the process was
     *   terminated by an error in the input LaTeX and if the session was configured to fail on
     *   the first error. 
     */
    public String buildXMLString(final XMLStringOutputOptions options) {
        ConstraintUtilities.ensureNotNull(options, "XMLStringOutputOptions");
        DocumentBuilder documentBuilder = XMLUtilities.createNSAwareDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element temporaryRoot = document.createElement("root");
        document.appendChild(temporaryRoot);
        if (!buildDOMSubtree(temporaryRoot, options)) {
            return null;
        }
        return XMLUtilities.serializeNodeChildren(getStylesheetManager(), temporaryRoot, options);
    }
    
    /**
     * Convenience method to create a well-formed external general parsed entity out of the
     * currently parsed tokens.
     * <p>
     * The default {@link DOMOutputOptions} specified in the {@link SnuggleEngine} will be
     * used.
     * 
     * @param indent whether to indent the resulting XML or not
     *
     * @return resulting XML if the process completed successfully, null if the process was
     *   terminated by an error in the input LaTeX and if the session was configured to fail on
     *   the first error.
     *   
     * @deprecated Use {@link #buildXMLString(XMLStringOutputOptions)} instead
     */
    @Deprecated
    public String buildXMLString(boolean indent) {
        return buildXMLString(engine.getDefaultDOMOutputOptions(), indent);
    }
    
    /**
     * Convenience method to create a well-formed external general parsed entity out of the
     * currently parsed tokens.
     * <p>
     * The given {@link DOMOutputOptions} Object is used to configure the process.
     * 
     * @return resulting XML if the process completed successfully, null if the process was
     *   terminated by an error in the input LaTeX and if the session was configured to fail on
     *   the first error.
     *   
     * @deprecated Use {@link #buildXMLString(XMLStringOutputOptions)} instead.
     */
    @Deprecated
    public String buildXMLString(final DOMOutputOptions options) {
        return buildXMLString(options, false);
    }
    
    /**
     * Convenience method to create a well-formed external general parsed entity out of the
     * currently parsed tokens.
     * <p>
     * The given {@link DOMOutputOptions} Object is used to configure the process.
     * 
     * @param indent whether to indent the resulting XML or not
     * 
     * @return resulting XML if the process completed successfully, null if the process was
     *   terminated by an error in the input LaTeX and if the session was configured to fail on
     *   the first error.
     *   
     * @deprecated Use {@link #buildXMLString(XMLStringOutputOptions)} instead.
     */
    @Deprecated
    public String buildXMLString(final DOMOutputOptions options, final boolean indent) {
        DocumentBuilder documentBuilder = XMLUtilities.createNSAwareDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element temporaryRoot = document.createElement("root");
        document.appendChild(temporaryRoot);
        if (!buildDOMSubtree(temporaryRoot, options)) {
            return null;
        }
        SerializationSpecifier serializationOptions = new SerializationOptions();
        serializationOptions.setEncoding(XMLStringOutputOptions.DEFAULT_ENCODING);
        serializationOptions.setIndenting(indent);
        return XMLUtilities.serializeNodeChildren(getStylesheetManager(), temporaryRoot, serializationOptions);
    }
    
    //---------------------------------------------

    /**
     * Builds a complete web page based on the currently parsed tokens, returning a DOM
     * {@link Document} Object.
     * <p>
     * The provided {@link WebPageOutputOptions} Object is
     * used to determine which type of web page to generate and how it should be configured.
     * <p>
     * Any XSLT stylesheet specified by {@link WebPageOutputOptions#getStylesheets()}
     * will have been applied to the result before it is returned. On the other hand, serialisation
     * options in the {@link WebPageOutputOptions} (such as Content Type and encoding) 
     * will not have been applied when this method returns.
     * 
     * @return resulting Document if the process completed successfully, null if the process was
     *   terminated by an error in the input LaTeX and if the session was configured to fail on
     *   the first error. 
     */
    public Document createWebPage(final WebPageOutputOptions options) {
        ConstraintUtilities.ensureNotNull(options, "options");
        try {
            return new WebPageBuilder(this, options).createWebPage(parsedTokens);
        }
        catch (SnuggleParseException e) {
            return null;
        }
    }
    
    /**
     * Builds a complete web page based on the currently parsed tokens, sending the results
     * to the given {@link OutputStream}, which is closed afterwards.
     * <p>
     * The provided {@link WebPageOutputOptions} Object is
     * used to determine which type of web page to generate and how it should be configured.
     * 
     * @return true if completed successfully, false if the process was terminated by an error in the
     *   input LaTeX and if the session was configured to fail on the first error. 
     */
    public boolean writeWebPage(final WebPageOutputOptions options, final OutputStream outputStream)
            throws IOException {
        return writeWebPage(options, null, outputStream, EndOutputAction.CLOSE);
    }
    
    /**
     * Builds a complete web page based on the currently parsed tokens, sending the results
     * to the given {@link OutputStream} and performing the given {@link EndOutputAction} to
     * it afterwards (i.e. close, flush or do nothing).
     * <p>
     * The provided {@link WebPageOutputOptions} Object is
     * used to determine which type of web page to generate and how it should be configured.
     * 
     * @since 1.2.2
     * 
     * @return true if completed successfully, false if the process was terminated by an error in the
     *   input LaTeX and if the session was configured to fail on the first error. 
     */
    public boolean writeWebPage(final WebPageOutputOptions options, final OutputStream outputStream,
            final EndOutputAction endOutputAction)
            throws IOException {
        return writeWebPage(options, null, outputStream, endOutputAction);
    }
    
    /**
     * Builds a complete web page based on the currently parsed tokens, sending the results
     * to the given {@link OutputStream}, which is closed afterwards.
     * <p>
     * The provided {@link WebPageOutputOptions} Object is
     * used to determine which type of web page to generate and how it should be configured.
     * <p>
     * If the <tt>contentTypeSettable</tt> Object has a
     * property called <tt>contentType</tt>, then it is set in advance to the appropriate HTTP
     * <tt>Content-Type</tt> header for the resulting page before the web page data is written.
     * 
     * @return true if completed successfully, false if the process was terminated by an error in the
     *   input LaTeX and if the session was configured to fail on the first error. 
     * 
     * @throws IOException if an I/O problem arose whilst writing out the web page data.
     * @throws SnuggleRuntimeException if calling <tt>setContentType()</tt> on the contentTypeSettable
     *   Object failed, with the underlying Exception wrapped up.
     */
    public boolean writeWebPage(final WebPageOutputOptions options, final Object contentTypeSettable,
            final OutputStream outputStream) throws IOException {
        return writeWebPage(options, contentTypeSettable, outputStream, EndOutputAction.CLOSE);
    }
    
    /**
     * Builds a complete web page based on the currently parsed tokens, sending the results
     * to the given {@link OutputStream} and performing the given {@link EndOutputAction} to
     * it afterwards (i.e. close, flush or do nothing).
     * <p>
     * The provided {@link WebPageOutputOptions} Object is
     * used to determine which type of web page to generate and how it should be configured.
     * <p>
     * If the <tt>contentTypeSettable</tt> Object has a
     * property called <tt>contentType</tt>, then it is set in advance to the appropriate HTTP
     * <tt>Content-Type</tt> header for the resulting page before the web page data is written.
     * 
     * @since 1.2.2
     * 
     * @return true if completed successfully, false if the process was terminated by an error in the
     *   input LaTeX and if the session was configured to fail on the first error. 
     * 
     * @throws IOException if an I/O problem arose whilst writing out the web page data.
     * @throws SnuggleRuntimeException if calling <tt>setContentType()</tt> on the contentTypeSettable
     *   Object failed, with the underlying Exception wrapped up.
     */
    public boolean writeWebPage(final WebPageOutputOptions options, final Object contentTypeSettable,
            final OutputStream outputStream, final EndOutputAction endOutputAction)
            throws IOException {
        ConstraintUtilities.ensureNotNull(options, "options");
        ConstraintUtilities.ensureNotNull(outputStream, "outputStream");
        ConstraintUtilities.ensureNotNull(endOutputAction, "endOutputAction");
        try {
            new WebPageBuilder(this, options).writeWebPage(parsedTokens, contentTypeSettable, outputStream, endOutputAction);
            return true;
        }
        catch (SnuggleParseException e) {
            return false;
        }
    }
    
    /**
     * Calls the <tt>setContentType</tt> of the given Object to something appropriate for the
     * given {@link WebPageOutputOptions}. This may be useful in some cases.
     * <p>
     * The main example for this would be passing a <tt>javax.servlet.http.HttpResponse</tt>
     * Object, which I want to avoid a compile-time dependency on.
     * 
     * @see #writeWebPage(WebPageOutputOptions, Object, OutputStream)
     */
    public void setWebPageContentType(final WebPageOutputOptions options, final Object contentTypeSettable) {
        ConstraintUtilities.ensureNotNull(options, "options");
        ConstraintUtilities.ensureNotNull(contentTypeSettable, "contentTypeSettable");
        new WebPageBuilder(this, options).setWebPageContentType(contentTypeSettable);
    }
    
    //---------------------------------------------
    // Business helpers
    
    /**
     * Delegates to {@link SnuggleEngine#getBuiltinCommandByTeXName(String)}
     * 
     * @see SnuggleEngine#getBuiltinCommandByTeXName(String)
     */
    public BuiltinCommand getBuiltinCommandByTeXName(String texName) {
        return engine.getBuiltinCommandByTeXName(texName);
    }
    
    /**
     * Delegates to {@link SnuggleEngine#getBuiltinEnvironmentByTeXName(String)}
     * 
     * @see SnuggleEngine#getBuiltinEnvironmentByTeXName(String)
     */
    public BuiltinEnvironment getBuiltinEnvironmentByTeXName(String texName) {
        return engine.getBuiltinEnvironmentByTeXName(texName);
    }
    
    /**
     * Gets the {@link Map} of all user-defined commands created in this session.
     */
    public Map<String, UserDefinedCommand> getUserCommandMap() {
        return userCommandMap;
    }
    
    /**
     * Gets the {@link Map} of all user-defined environments created in this session.
     */
    public Map<String, UserDefinedEnvironment> getUserEnvironmentMap() {
        return userEnvironmentMap;
    }
    
    /**
     * Gets the {@link StylesheetManager} being used by the underlying {@link SnuggleEngine}
     */
    public StylesheetManager getStylesheetManager() {
        return engine.getStylesheetManager();
    }

    /**
     * Records a new error, throwing a {@link SnuggleParseException} if
     * the current {@link SessionConfiguration} deems that we should
     * fail on the first error.
     * 
     * @param inputError {@link InputError} to register, which must not be null.
     * 
     * @throws SnuggleParseException if this session is configured to fail fast 
     */
    public void registerError(InputError inputError) throws SnuggleParseException {
        ConstraintUtilities.ensureNotNull(inputError, "InputError");
        errors.add(inputError);
        if (configuration.isFailingFast()) {
            throw new SnuggleParseException(inputError);
        }
    }
}
