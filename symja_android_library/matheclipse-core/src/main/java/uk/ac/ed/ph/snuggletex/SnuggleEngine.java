/* $Id: SnuggleEngine.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import uk.ac.ed.ph.snuggletex.definitions.BuiltinCommand;
import uk.ac.ed.ph.snuggletex.definitions.BuiltinEnvironment;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.snuggletex.utilities.DefaultTransformerFactoryChooser;
import uk.ac.ed.ph.snuggletex.utilities.SimpleStylesheetCache;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetManager;
import uk.ac.ed.ph.snuggletex.utilities.TransformerFactoryChooser;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the main entry point into SnuggleTeX.
 * 
 * <h2>Usage</h2>
 * 
 * <ul>
 *   <li>
 *     Create an instance of this engine and register any extra {@link SnugglePackage}s you have
 *     created or want to use by calling {@link #addPackage(SnugglePackage)}.
 *   </li>
 *   <li>
 *     Use {@link #createSession()} to create a "session" that will take one (or more) input
 *     documents and produce a DOM.
 *   </li>
 *   <li>
 *     Once configured, an instance of this Class can be shared by multiple Threads
 *     provided the only methods called are all forms of {@link #createSession()}
 *     and any "get" methods.
 *   </li>
 *   <li>
 *     Don't let the usual connotations associated with the name of this Class worry you that
 *     instantiating it is going to be expensive!
 *   </li>
 * </ul>
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class SnuggleEngine {
    
    /** List of all currently registered {@link SnugglePackage}s used by this Engine. */
    private final List<SnugglePackage> packages;

    /** Helper class to manage XSLT Stylesheets */
    private final StylesheetManager stylesheetManager;

    /** Default {@link SessionConfiguration} */
    private SessionConfiguration defaultSessionConfiguration;
    
    /** Default {@link DOMOutputOptions} */
    private DOMOutputOptions defaultDOMOutputOptions;
    
    /** Default {@link XMLStringOutputOptions} */
    private XMLStringOutputOptions defaultXMLStringOutputOptions;

    /**
     * Creates a new {@link SnuggleEngine} using a very simple internal cache for any
     * XSLT stylesheets required that simply stores them internally for the lifetime
     * of the engine. The {@link DefaultTransformerFactoryChooser} is used to choose
     * XSLT implementations.
     * <p>
     * This will be fine in most cases. If you want more control over
     * this, consider the alternative constructor.
     */
    public SnuggleEngine() {
        this(DefaultTransformerFactoryChooser.getInstance(), new SimpleStylesheetCache());
    }

    /**
     * Creates a new {@link SnuggleEngine} using the given {@link StylesheetCache}
     * for managing stylesheets. Use this if you want to integrate XSLT caching
     * with your own code or want more control over how things get cached.
     * The {@link DefaultTransformerFactoryChooser} is used to choose
     * XSLT implementations.
     */
    public SnuggleEngine(StylesheetCache stylesheetCache) {
        this(DefaultTransformerFactoryChooser.getInstance(), stylesheetCache);
    }

    /**
     * Creates a new {@link SnuggleEngine} using the given {@link StylesheetCache}
     * for managing stylesheets and given {@link TransformerFactoryChooser} for choosing which
     * XSLT implementation to use. Use this if you want to integrate XSLT caching
     * with your own code, want more control over how things get cached or want to control
     * the selection of XSLT implementations.
     */
    public SnuggleEngine(TransformerFactoryChooser transformerFactoryChooser, StylesheetCache stylesheetCache) {
        this(new StylesheetManager(transformerFactoryChooser, stylesheetCache));
    }
    
    /**
     * Creates a new {@link SnuggleEngine} using the given {@link StylesheetManager}
     * for managing stylesheets. Use this if you want to integrate XSLT caching
     * with your own code, want more control over how things get cached or want to control
     * the selection of XSLT implementations.
     */
    public SnuggleEngine(StylesheetManager stylesheetManager) {
        this.packages = new ArrayList<SnugglePackage>();
        this.defaultSessionConfiguration = new SessionConfiguration();
        this.defaultDOMOutputOptions = new DOMOutputOptions();
        this.defaultXMLStringOutputOptions = new XMLStringOutputOptions();
        
        /* Create manager for XSLT stlyesheets using the given cache */
        this.stylesheetManager = stylesheetManager;
        
        /* Add in core package */
        packages.add(CorePackageDefinitions.getPackage());
    }
    
    //-------------------------------------------------
    
    /**
     * Convenience method to extract the underlying {@link StylesheetManager} used by
     * this engine. Most people won't care about this, but it might be useful if you're
     * trying to integrate stylesheet caching.
     */
    public StylesheetManager getStylesheetManager() {
        return stylesheetManager;
    }
    
    //-------------------------------------------------
    
    /**
     * Registers the given {@link SnugglePackage} with this {@link SnuggleEngine}.
     * All commands, environments and error codes defined by the {@link SnugglePackage} will
     * then become available for use.
     */
    public void addPackage(SnugglePackage snugglePackage) {
        ConstraintUtilities.ensureNotNull(snugglePackage, "snugglePackage");
        packages.add(snugglePackage);
    }
    
    //-------------------------------------------------
    
    /**
     * Creates a new {@link SnuggleSession} using the default {@link SessionConfiguration}
     * if se
     */
    public SnuggleSession createSession() {
        SessionConfiguration sessionConfiguration = defaultSessionConfiguration;
        if (sessionConfiguration!=null) {
            sessionConfiguration = new SessionConfiguration();
        }
        return createSession(sessionConfiguration);
    }
    
    /**
     * Creates a new {@link SnuggleSession} using the given {@link SessionConfiguration}
     * 
     * @param sessionConfiguration {@link SessionConfiguration} to use, which must not be null.
     */
    public SnuggleSession createSession(SessionConfiguration sessionConfiguration) {
        ConstraintUtilities.ensureNotNull(sessionConfiguration, "sessionConfiguration");
        return new SnuggleSession(this, sessionConfiguration);
    }

    //-------------------------------------------------
    
    /**
     * Returns the {@link BuiltinCommand} corresponding to LaTeX command called
     * <tt>\texName</tt>, or null if this command is not defined.
     */
    public BuiltinCommand getBuiltinCommandByTeXName(String texName) {
        ConstraintUtilities.ensureNotNull(texName, "texName");
        BuiltinCommand result = null;
        for (SnugglePackage map : packages) {
            result = map.getBuiltinCommandByTeXName(texName);
            if (result!=null) {
                break;
            }
        }
        return result;
    }
    
    /**
     * Returns the {@link BuiltinEnvironment} corresponding to LaTeX environment
     * called <tt>texName</tt>, or null if this environment is not defined.
     */
    public BuiltinEnvironment getBuiltinEnvironmentByTeXName(String texName) {
        ConstraintUtilities.ensureNotNull(texName, "texName");
        BuiltinEnvironment result = null;
        for (SnugglePackage map : packages) {
            result = map.getBuiltinEnvironmentByTeXName(texName);
            if (result!=null) {
                break;
            }
        }
        return result;
    }
    
    //-------------------------------------------------

    /**
     * Gets the selected default {@link SessionConfiguration} for this {@link SnuggleEngine},
     * which will not be null.
     */
    public SessionConfiguration getDefaultSessionConfiguration() {
        return defaultSessionConfiguration;
    }
    
    /**
     * Sets the default {@link SessionConfiguration} for this {@link SnuggleEngine}.
     * This will be used when creating a new {@link SnuggleSession} via
     * {@link #createSession()}.
     * 
     * @param defaultSessionConfiguration new default {@link SessionConfiguration}, which
     *   must not be null.
     */
    public void setDefaultSessionConfiguration(SessionConfiguration defaultSessionConfiguration) {
        ConstraintUtilities.ensureNotNull(defaultSessionConfiguration, "defaultSessionConfiguration");
        this.defaultSessionConfiguration = defaultSessionConfiguration;
    }
    
    
    /**
     * Gets the default {@link DOMOutputOptions} for this {@link SnuggleEngine}, which will not
     * be null.
     */
    public DOMOutputOptions getDefaultDOMOutputOptions() {
        return defaultDOMOutputOptions;
    }

    /**
     * Sets the default {@link DOMOutputOptions} for this {@link SnuggleEngine}. These
     * will be used when calling {@link SnuggleSession#buildDOMSubtree()}.
     * 
     * @param defaultDOMOutputOptions new {@link DOMOutputOptions}, which must not be null.
     */
    public void setDefaultDOMOutputOptions(DOMOutputOptions defaultDOMOutputOptions) {
        ConstraintUtilities.ensureNotNull(defaultDOMOutputOptions, "defaultDOMOutputOptions");
        this.defaultDOMOutputOptions = defaultDOMOutputOptions;
    }

    
    /**
     * Gets the default {@link XMLStringOutputOptions} for this {@link SnuggleEngine}, which
     * will not be null.
     */
    public XMLStringOutputOptions getDefaultXMLStringOutputOptions() {
        return defaultXMLStringOutputOptions;
    }

    /**
     * Sets the default {@link XMLStringOutputOptions} for this {@link SnuggleEngine}.
     * These will be used when calling {@link SnuggleSession#buildXMLString()}.
     * 
     * @param defaultXMLOutputOptions new {@link XMLStringOutputOptions}, which must not be null.
     */
    public void setDefaultXMLStringOutputOptions(XMLStringOutputOptions defaultXMLOutputOptions) {
        ConstraintUtilities.ensureNotNull(defaultXMLOutputOptions, "defaultXMLOutputOptions");
        this.defaultXMLStringOutputOptions = defaultXMLOutputOptions;
    }


    /**
     * @deprecated Use {@link #getDefaultDOMOutputOptions()}
     */
    @Deprecated
    public DOMOutputOptions getDefaultDOMOptions() {
        return defaultDOMOutputOptions;
    }

    /**
     * @deprecated Use {@link #setDefaultDOMOutputOptions(DOMOutputOptions)}
     */
    @Deprecated
    public void setDefaultDOMOptions(DOMOutputOptions defaultDOMOptions) {
        ConstraintUtilities.ensureNotNull(defaultDOMOptions, "defaultDOMOptions");
        this.defaultDOMOutputOptions = defaultDOMOptions;
    }
}
