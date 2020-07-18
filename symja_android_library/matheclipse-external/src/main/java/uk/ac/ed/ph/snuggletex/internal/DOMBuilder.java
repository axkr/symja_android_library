/* $Id: DOMBuilder.java 542 2010-03-24 18:43:49Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal;

import uk.ac.ed.ph.snuggletex.DOMOutputOptions;
import uk.ac.ed.ph.snuggletex.ErrorCode;
import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.LinkResolver;
import uk.ac.ed.ph.snuggletex.SnuggleConstants;
import uk.ac.ed.ph.snuggletex.SnuggleLogicException;
import uk.ac.ed.ph.snuggletex.SnuggleSession;
import uk.ac.ed.ph.snuggletex.DOMOutputOptions.ErrorOutputOptions;
import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;
import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.definitions.MathVariantMap;
import uk.ac.ed.ph.snuggletex.definitions.W3CConstants;
import uk.ac.ed.ph.snuggletex.dombuilding.CommandHandler;
import uk.ac.ed.ph.snuggletex.dombuilding.EnvironmentHandler;
import uk.ac.ed.ph.snuggletex.dombuilding.EqnArrayHandler;
import uk.ac.ed.ph.snuggletex.internal.util.ArrayListStack;
import uk.ac.ed.ph.snuggletex.internal.util.StringUtilities;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;
import uk.ac.ed.ph.snuggletex.semantics.Interpretation;
import uk.ac.ed.ph.snuggletex.semantics.InterpretationType;
import uk.ac.ed.ph.snuggletex.semantics.MathFunctionInterpretation;
import uk.ac.ed.ph.snuggletex.semantics.MathIdentifierInterpretation;
import uk.ac.ed.ph.snuggletex.semantics.MathNumberInterpretation;
import uk.ac.ed.ph.snuggletex.semantics.MathOperatorInterpretation;
import uk.ac.ed.ph.snuggletex.tokens.ArgumentContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.BraceContainerToken;
import uk.ac.ed.ph.snuggletex.tokens.CommandToken;
import uk.ac.ed.ph.snuggletex.tokens.EnvironmentToken;
import uk.ac.ed.ph.snuggletex.tokens.ErrorToken;
import uk.ac.ed.ph.snuggletex.tokens.FlowToken;
import uk.ac.ed.ph.snuggletex.tokens.SimpleToken;
import uk.ac.ed.ph.snuggletex.tokens.Token;
import uk.ac.ed.ph.snuggletex.utilities.CSSUtilities;
import uk.ac.ed.ph.snuggletex.utilities.MessageFormatter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This takes a {@link List} of (fixed) {@link Token}s and builds a (raw) XHTML + MathML
 * DOM tree branch from them.
 * 
 * <h2>Usage</h2>
 * 
 * Clients should not normally have to use this class directly -
 * call {@link SnuggleSession#buildDOMSubtree(Element, DOMOutputOptions)} and friends.
 * <p>
 * An instance of this class is intended to be used once and then discarded so can be stateful.
 * <p>
 * This contains a number of callbacks that {@link CommandHandler}s and {@link EnvironmentHandler}s
 * can use to do their magic.
 * 
 * @see TokenFixer
 * @see DOMBuildingController
 * 
 * @author  David McKain
 * @version $Revision: 542 $
 */
public final class DOMBuilder {
    
    /** Underlying {@link SessionContext} */
    private final SessionContext sessionContext;
    
    /** Build options specified by user */
    private final DOMOutputOptions options;
    
    /** {@link Element} that we're building into */
    private final Element buildRootElement;
    
    /** {@link Document} owning {@link #buildRootElement} */
    private final Document document;
    
    /** Helper to manage variables */
    private final VariableManager variableManager;
    
    /** CSS Properties, used for inlining and created lazily as required */
    private Properties currentInlineCSSProperties;
    
    //-------------------------------------------
    // Building State
    
    /** 
     * Trivial enumeration to keep track of where we are in the outgoing DOM. This makes life
     * a bit easier when handling certain types of tokens. Use the
     * {@link DOMBuilder#getOutputContext()}, {@link DOMBuilder#pushOutputContext(OutputContext)}
     * and {@link DOMBuilder#popOutputContext()} to manage the current status.
     */
    public static enum OutputContext {
        XHTML,
        MATHML_BLOCK,
        MATHML_INLINE,
        ;
    }
    
    /** Stack of {@link OutputContext} scopes. This never gets big but is safer done like this */
    private final ArrayListStack<OutputContext> outputContextStack;
    
    /** Stack of active {@link MathVariantMap}s. */
    private final ArrayListStack<MathVariantMap> mathVariantMapStack;
    
    //-------------------------------------------
    
    public DOMBuilder(final SessionContext sessionContext, final Element buildRootElement,
            final DOMOutputOptions options) {
        this.buildRootElement = buildRootElement;
        this.document = buildRootElement.getOwnerDocument();
        this.sessionContext = sessionContext;
        this.options = options;
        this.variableManager = new VariableManager();
        this.currentInlineCSSProperties = null;
        this.outputContextStack = new ArrayListStack<OutputContext>();
        this.mathVariantMapStack = new ArrayListStack<MathVariantMap>();
    }
    
    //-------------------------------------------
    // External entry point
    
    /**
     * This is the external entry point used by {@link SnuggleSession} only. If you are using
     * {@link DOMBuilder} as a callback to implementing your own built-in commands and
     * environments, then you shouldn't call this method.
     * 
     * @throws SnuggleParseException
     * @throws DOMException
     */
    public void buildDOMSubtree(final List<FlowToken> fixedTokens) throws SnuggleParseException {
        /* Reset state */
        outputContextStack.clear();
        mathVariantMapStack.clear();
        
        /* Do work */
        outputContextStack.push(OutputContext.XHTML);
        handleTokens(buildRootElement, fixedTokens, true);
        outputContextStack.pop();
        
        /* Perform state sanity check at end */
        if (!mathVariantMapStack.isEmpty()) {
            throw new SnuggleLogicException("mathVariantMapStack was non-empty at end of DOM building process");
        }
        if (!outputContextStack.isEmpty()) {
            throw new SnuggleLogicException("outputContextStack was non-empty at end of DOM building process");
        }
    }
    
    //-------------------------------------------
    // Usual Accessors
    
    public SessionContext getSessionContext() {
        return sessionContext;
    }
    
    public DOMOutputOptions getOptions() {
        return options;
    }
    
    /**
     * Gets at the underlying DOM Document. Use this to do exotic things not covered elsewhere.
     * To append XHTML and/or MathML elements, you <strong>should</strong> use the alternatives
     * methods listed here to ensure correct namespace handling.
     */
    public Document getDocument() {
        return document;
    }
    
    public VariableManager getVariableManager() {
        return variableManager;
    }
    
    //-------------------------------------------
    // State Accessors
    
    public ArrayListStack<MathVariantMap> getMathVariantMapStack() {
        return mathVariantMapStack;
    }
    
    //-------------------------------------------
    // Output context mutators - records whether we're doing XHTML or MathML content,
    // which is often useful.
    
    public OutputContext getOutputContext() {
        return outputContextStack.peek();
    }
    
    public void pushOutputContext(OutputContext outputContext) {
        outputContextStack.push(outputContext);
    }  
    
    public OutputContext popOutputContext() {
        return outputContextStack.pop();
    }  
    
    /**
     * Returns whether or not we're building a MathML island by checking the current
     * {@link OutputContext}.
     */
    public boolean isBuildingMathMLIsland() {
        OutputContext currentOutputContext = getOutputContext();
        return currentOutputContext==OutputContext.MATHML_BLOCK || currentOutputContext==OutputContext.MATHML_INLINE;
    }
    
    //-------------------------------------------
    // Callbacks Start Below
    //
    // Note that all methods may throw the unchecked DOMException !

    /**
     * @param trimWhitespace removes leading whitespace from the first resulting Node if it is a Text Node and
     *   removes trailing whitespace from the last Node if it is a Text Node.
     */
    public void handleTokens(Element parentElement, ArgumentContainerToken containerToken, boolean trimWhitespace)
            throws SnuggleParseException {
        handleTokens(parentElement, containerToken.getContents(), trimWhitespace);
    }

    /**
     * @param trimWhitespace removes leading whitespace from the first resulting Node if it is a Text Node and
     *   removes trailing whitespace from the last Node if it is a Text Node.
     */
    public void handleTokens(Element parentElement, List<FlowToken> siblingTokens, boolean trimWhitespace)
            throws SnuggleParseException {
        int childCountBefore = parentElement.getChildNodes().getLength();
        for (FlowToken content : siblingTokens) {
            handleToken(parentElement, content);
        }
        if (trimWhitespace) {
            NodeList childList = parentElement.getChildNodes();
            int childCountAfter = childList.getLength();
            int addedChildCount = childCountAfter - childCountBefore;
            if (addedChildCount>0) {
                int firstAddedChildIndex = childCountAfter - addedChildCount;
                int lastAddedChildIndex = childCountAfter - 1;
                
                Node firstAddedChildNode = childList.item(firstAddedChildIndex);
                if (firstAddedChildNode.getNodeType()==Node.TEXT_NODE) {
                    firstAddedChildNode.setNodeValue(firstAddedChildNode.getNodeValue().replaceFirst("^\\s+", ""));
                }
                Node lastAddedChildNode = childList.item(lastAddedChildIndex);
                if (lastAddedChildNode.getNodeType()==Node.TEXT_NODE) {
                    lastAddedChildNode.setNodeValue(lastAddedChildNode.getNodeValue().replaceFirst("\\s+$", ""));
                }
            }
        }
    }
    
    /**
     * Creates a single Node from the given token. If the token is a container then it will
     * be wrapped into a container element if required, depending on context.
     *
     * Example:
     * 
     * \msub{1+2}{3-\mrow{4*5}} -> <msup><mrow>...</mrow><mrow>...</mrow></msup>
     * @param parentElement
     * @param token
     * 
     * @throws DOMException
     * @throws SnuggleParseException
     */
    public void handleToken(Element parentElement, FlowToken token) throws SnuggleParseException {
        switch (token.getType()) {
            
            /* Complex Tokens */
            
            case BRACE_CONTAINER:
                /* Create an appropriate content container */
                BraceContainerToken braceToken = (BraceContainerToken) token;
                ArgumentContainerToken content = braceToken.getBraceContent();
                Element container = null;
                if (braceToken.getLatexMode()==LaTeXMode.MATH) {
                    container = appendMathMLElement(parentElement, "mrow");
                }
                else {
                    container = appendXHTMLElement(parentElement, "span");
                }
                
                /* Handle the children of this token */
                handleTokens(container, content, true);
                
                /* If we only added one child, we'll remove from the container and add directly
                 * to the tree instead.
                 */
                NodeList childNodes = container.getChildNodes();
                if (childNodes.getLength()==1) {
                    Node singleNode = childNodes.item(0);
                    parentElement.removeChild(container);
                    parentElement.appendChild(singleNode);
                }
                break;
                
            case COMMAND:
                /* Each command has its own builder instance to do the work here */
                CommandToken commandToken = (CommandToken) token;
                CommandHandler commandHandler = commandToken.getCommand().getDOMBuildingHandler();
                if (commandHandler==null) {
                    throw new SnuggleLogicException("No builder registered for Command " + commandToken.getCommand());
                }
                commandHandler.handleCommand(this, parentElement, commandToken);
                break;
                
            case ENVIRONMENT:
                /* This is essentially the same as COMMAND but I need to verify the details */
                EnvironmentToken envToken = (EnvironmentToken) token;
                EnvironmentHandler envHandler = envToken.getEnvironment().getDOMBuildingHandler();
                if (envHandler==null) {
                    throw new SnuggleLogicException("No builder registered for Environment " + envToken.getEnvironment());
                }
                envHandler.handleEnvironment(this, parentElement, envToken);
                break;
                
            case ERROR:
                appendErrorElement(parentElement, (ErrorToken) token);
                break;
                
            /* Simple Tokens */
                
            case TEXT_MODE_TEXT:
                handleTextToken(parentElement, (SimpleToken) token);
                break;
                
            case VERBATIM_MODE_TEXT:
                appendTextNode(parentElement, token.getSlice().extract().toString(), false);
                break;
                
            case LR_MODE_NEW_PARAGRAPH:
                /* This is a special token to indicate that a "new paragraph" is required in LR mode,
                 * which is not really feasible so we just generate a space instead.
                 */
                if (isBuildingMathMLIsland()) {
                    appendMathMLSpace(parentElement, "1ex");
                }
                else {
                    appendTextNode(parentElement, " ", false);
                }
                break;
                
            /* Math tokens (it's assumed that these all have Interpretations attached) */
                
            case MATH_NUMBER:
            case SINGLE_CHARACTER_MATH_IDENTIFIER:
            case SINGLE_CHARACTER_MATH_SPECIAL:
                /* First check we are in a suitable mode */
                if (isBuildingMathMLIsland()) {
                    appendSimpleMathElement(parentElement, token);
                }
                else {
                    throw new SnuggleLogicException("Math Mode token found but outputContext is currently " + outputContextStack);
                }
                break;
                
            case TAB_CHARACTER:
                /* If used appropriately, this will have been dealt with during fixing, so we have
                 * Error: alignment used in the wrong place.
                 */
                appendOrThrowError(parentElement, token, CoreErrorCode.TDEG00);
                break;
                
            default:
                throw new SnuggleLogicException("Unhandled switch case " + token.getType());
        }
    }
    
    public void handleTextToken(Element parentElement, SimpleToken textToken) {
        CharSequence rawText = textToken.getSlice().extract();
        StringBuilder resultBuilder = new StringBuilder();
        char c;
        for (int i=0, length=rawText.length(); i<length; i++) {
            c = rawText.charAt(i);
            switch (c) {
                case '~':
                    resultBuilder.append('\u00a0');
                    break;
                    
                case '<':
                    resultBuilder.append('\u00a1');
                    break;
                    
                case '>':
                    resultBuilder.append('\u00bf');
                    break;
                    
                case '|':
                    resultBuilder.append('\u2014');
                    break;
                    
                case '`':
                    if (i+1<length && rawText.charAt(i+1)=='`') {
                        /* Double open quote */
                        resultBuilder.append('\u201c');
                        i++;
                    }
                    else {
                        /* Single open quote */
                        resultBuilder.append('\u2018');
                    }
                    break;
                    
                case '\'':
                    if (i+1<length && rawText.charAt(i+1)=='\'') {
                        /* Double close quote */
                        resultBuilder.append('\u201d');
                        i++;
                    }
                    else {
                        /* Single close quote */
                        resultBuilder.append('\u2019');
                    }
                    break;
                    
                case '-':
                    if (i+1<length && rawText.charAt(i+1)=='-') {
                        if (i+2<length && rawText.charAt(i+2)=='-') {
                            /* --- is a long dash */
                            resultBuilder.append('\u2014');
                            i += 2;
                        }
                        else {
                            /* -- is a medium dash */
                            resultBuilder.append('\u2013');
                            i++;
                        }
                    }
                    else {
                        /* Just do a normal hyphen */
                        resultBuilder.append('-');
                    }
                    break;
                    
                default:
                    resultBuilder.append(c);
                    break;
            }
        }
        String resultString = resultBuilder.toString();
        if (isBuildingMathMLIsland()) {
            /* Need to wrap in an <mtext>...</mtext>.
             * Note that leading and trailing whitespace is ignored in <mtext/> elements so, if
             * whitespace is asked for, it must be added via a <mspace/>. Also need to handle
             * the case of whitespace only.
             */
            String trimmed = resultString.trim();
            if (trimmed.length()==0) {
                /* Whitespace only, which gets normalised to a single space */
                appendMathMLSpace(parentElement, "1ex");
            }
            else {
                /* Contains a non-whitespace character somewhere */
                if (Character.isWhitespace(resultString.charAt(0))) {
                    appendMathMLSpace(parentElement, "1ex");
                }
                appendMathMLTextElement(parentElement, "mtext", trimmed, false);
                if (Character.isWhitespace(resultString.charAt(resultString.length()-1))) {
                   appendMathMLSpace(parentElement, "1ex");
                }
            }
        }
        else {
            appendTextNode(parentElement, resultString, false);
        }
    }

    public void appendSimpleMathElement(Element parentElement, Token token) {
        EnumMap<InterpretationType, Interpretation> interpretationMap = token.getInterpretationMap();
        if (interpretationMap.containsKey(InterpretationType.MATH_IDENTIFIER)) {
            MathIdentifierInterpretation identifierInterp = (MathIdentifierInterpretation) interpretationMap.get(InterpretationType.MATH_IDENTIFIER);
            appendMathMLIdentifierElement(parentElement, identifierInterp.getName());
        }
        else if (interpretationMap.containsKey(InterpretationType.MATH_NUMBER)) {
            MathNumberInterpretation numberInterp = (MathNumberInterpretation) interpretationMap.get(InterpretationType.MATH_NUMBER);
            appendMathMLNumberElement(parentElement, numberInterp.getNumber().toString());
        }
        else if (interpretationMap.containsKey(InterpretationType.MATH_OPERATOR)) {
            MathOperatorInterpretation operatorInterp = (MathOperatorInterpretation) interpretationMap.get(InterpretationType.MATH_OPERATOR);
            appendMathMLOperatorElement(parentElement, operatorInterp.getMathMLOperatorContent());
        }
        else if (interpretationMap.containsKey(InterpretationType.MATH_FUNCTION)) {
            MathFunctionInterpretation functionInterp = (MathFunctionInterpretation) interpretationMap.get(InterpretationType.MATH_FUNCTION);
            appendMathMLIdentifierElement(parentElement, functionInterp.getName());
        }
        else {
            throw new SnuggleLogicException("Unexpected logic branch based on InterpretationMap, which was: " + interpretationMap);
        }
    }

    //-------------------------------------------
    // Helpers
    
    private final String lineSeparator = System.getProperty("line.separator");


    public Node appendTextNode(Element parentElement, String content, boolean trim) {
        String toAppend = trim ? content.trim() : content;
        toAppend = toAppend.replace(lineSeparator, "\n"); /* (Normalise line endings) */
        
        /* We'll coalesce adjacent text Nodes */
        Node lastChild = parentElement.getLastChild();
        if (lastChild!=null && lastChild.getNodeType()==Node.TEXT_NODE) {
            lastChild.setNodeValue(lastChild.getNodeValue() + toAppend);
        }
        else {
            /* Previous sibling is not text Node so create new one */
            lastChild = document.createTextNode(toAppend);
            parentElement.appendChild(lastChild);
        }
        return lastChild;
    }
    
    public Element appendSnuggleElement(Element parentElement, String elementLocalName) {
        String qName;
        if (options.isPrefixingSnuggleXML()) {
            qName = options.getSnuggleXMLPrefix() + ":" + elementLocalName;
        }
        else {
            qName = elementLocalName;
        }
        Element element = document.createElementNS(SnuggleConstants.SNUGGLETEX_NAMESPACE, qName);
        parentElement.appendChild(element);
        return element;
    }
    
    public Element appendXHTMLElement(Element parentElement, String elementLocalName) {
        String qName;
        if (options.isPrefixingXHTML()) {
            qName = options.getXHTMLPrefix() + ":" + elementLocalName;
        }
        else {
            qName = elementLocalName;
        }
        Element xhtmlElement = document.createElementNS(W3CConstants.XHTML_NAMESPACE, qName);
        parentElement.appendChild(xhtmlElement);
        return xhtmlElement;
    }
    
    public Element appendXHTMLTextElement(Element parentElement, String elementLocalName, String content, boolean trim) {
        Element xhtmlElement = appendXHTMLElement(parentElement, elementLocalName);
        appendTextNode(xhtmlElement, content, trim);
        return xhtmlElement;
    }
    
    public Element appendMathMLElement(Element parentElement, String elementLocalName) {
        String qName;
        if (options.isPrefixingMathML()) {
            qName = options.getMathMLPrefix() + ":" + elementLocalName;
        }
        else {
            qName = elementLocalName;
        }
        Element mathMLElement = document.createElementNS(W3CConstants.MATHML_NAMESPACE, qName);
        parentElement.appendChild(mathMLElement);
        return mathMLElement;
    }
    
    public Element appendMathMLTextElement(Element parentElement, String elementLocalName, String content, boolean trim) {
        Element mathMLElement = appendMathMLElement(parentElement, elementLocalName);
        appendTextNode(mathMLElement, content, trim);
        return mathMLElement;
    }
    
    public Element appendMathMLSpace(Element parentElement, String width) {
        Element mspaceElement = appendMathMLElement(parentElement, "mspace");
        mspaceElement.setAttribute("width", width);
        return mspaceElement;
    }
    
    public Element appendMathMLOperatorElement(Element parentElement, String content) {
        return appendMathMLTextElement(parentElement, "mo", content, true);
    }
    
    public Element appendMathMLNumberElement(Element parentElement, String number) {
        return appendMathMLTextElement(parentElement, "mn", number, true);
    }
    
    /**
     * Creates a MathML identifier element, which may either be a single character or multiple
     * characters (e.g. "sin"). The single-character case is interesting if we are currently
     * applying a "mathvariant" in that it may or may not then be mapped into a corresponding
     * target character.
     * 
     * @see DOMOutputOptions#isMathVariantMapping()
     * 
     * @param parentElement
     * @param name
     */
    public Element appendMathMLIdentifierElement(Element parentElement, String name) {
        String mappedIdentifier = name;
        String mathVariant = null;
        if (name.length()==1 && !mathVariantMapStack.isEmpty()) {
            /* If we have a single character identifier and something like \\mathcal is in force,
             * then we will apply a "mathvariant" attribute.
             */
            MathVariantMap currentMathCharacterMap = mathVariantMapStack.peek();
            mathVariant = currentMathCharacterMap.getMathVariantName();
            if (options.isMathVariantMapping()) {
                /* Client has asked to try to map this character to a suitable target Unicode
                 * character, so let's try this.
                 */
                char mappedChar = currentMathCharacterMap.getAccentedChar(name.charAt(0));
                if (mappedChar!=0) {
                    mappedIdentifier = Character.toString(mappedChar);
                }
            }
        }
        Element result = appendMathMLTextElement(parentElement, "mi", mappedIdentifier, true);
        if (mathVariant!=null) {
            result.setAttribute("mathvariant", mathVariant);
        }
        return result;
    }
    
    public void handleMathTokensAsSingleElement(Element parentElement, ArgumentContainerToken containerToken)
            throws SnuggleParseException {
        handleMathTokensAsSingleElement(parentElement, containerToken.getContents());
    }
    
    public void handleMathTokensAsSingleElement(Element parentElement, List<FlowToken> tokens)
            throws SnuggleParseException {
        /* We wrap in an <mrow/> */
        Element mrow = appendMathMLElement(parentElement, "mrow");
        handleTokens(mrow, tokens, true);
        NodeList addedNodes = mrow.getChildNodes();
        if (addedNodes.getLength()==1) {
            /* Only resulted in 1 Element being added so no need for <mrow/> */
            Node addedNode = addedNodes.item(0);
            parentElement.removeChild(mrow);
            parentElement.appendChild(addedNode);
        }
    }
    
    //-------------------------------------------
    
    public NodeList extractNodeListValue(ArgumentContainerToken token) throws SnuggleParseException {
        Element dummyContainer = document.createElement("dummy");
        handleTokens(dummyContainer, token, true);
        return dummyContainer.getChildNodes();
    }
    
    /**
     * This "converts" the given token to a String by performing
     * {{@link #handleTokens(Element, ArgumentContainerToken, boolean)}}
     * with a fake root element and then extracting the String value of the resulting Text Node(s).
     * 
     * @throws DOMException
     * @throws SnuggleParseException
     */
    public String extractStringValue(ArgumentContainerToken token) throws SnuggleParseException {
        StringBuilder resultBuilder = new StringBuilder();
        buildStringValue(resultBuilder, extractNodeListValue(token));
        return resultBuilder.toString();
    }
    
    private void buildStringValue(StringBuilder resultBuilder, NodeList nodes) {
        for (int i=0, size=nodes.getLength(); i<size; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType()==Node.TEXT_NODE) {
                resultBuilder.append(node.getNodeValue());
            }
            else if (node.getNodeType()==Node.ELEMENT_NODE) {
                buildStringValue(resultBuilder, node.getChildNodes());
            }
        }
    }
    
    //-------------------------------------------
    
    public void applyCSSStyle(Element xhtmlElement, String... cssClassNames) {
        if (options.isInliningCSS()) {
            /* We'll go through each requested class in order and pull in the appropriate CSS styles.
             * NOTE: We're not going to try to eliminate any duplicates here!
             */
            Properties inlineCSSProperties = getCurrentInlineCSSProperties();
            StringBuilder styleBuilder = new StringBuilder();
            String property;
            boolean needsSemiColon = false;
            for (String cssClassName : cssClassNames) {
                /* Look for a 'localName.className' declaration in the properties file. If not
                 * found, look up a generic '.className' declaration.
                 */
                property = inlineCSSProperties.getProperty(xhtmlElement.getLocalName() + "." + cssClassName);
                if (property==null) {
                    property = inlineCSSProperties.getProperty("." + cssClassName);
                }
                if (needsSemiColon) {
                    styleBuilder.append("; ");
                }
                property = property.trim();
                styleBuilder.append(property);
                needsSemiColon = property.endsWith(";");
            }

            /* If we found declarations, set style attribute accordingly. Otherwise, we'll ignore. */
            if (styleBuilder.length()>0) {
                xhtmlElement.setAttribute("style", styleBuilder.toString());
            }
        }
        else {
            /* Just set 'class' attribute */
            xhtmlElement.setAttribute("class", StringUtilities.join(cssClassNames, " "));
        }
    }
    
    private Properties getCurrentInlineCSSProperties() {
        if (currentInlineCSSProperties==null) {
            currentInlineCSSProperties = CSSUtilities.readInlineCSSProperties(options);
        }
        return currentInlineCSSProperties;
    }
    
    //-------------------------------------------
    // Various Helpers
    
    /**
     * Builds a MathML <tt>math</tt> element for the given Token token, using contentToken
     * to provide the content, descending into child Nodes as normal.
     */
    public void buildMathElement(final Element parentElement,
            final Token token, final ArgumentContainerToken mathContentToken,
            final boolean isDisplayMath)
            throws SnuggleParseException {
        buildMathElement(parentElement, token, mathContentToken, isDisplayMath, defaultMathContentBuilderCallback);
    }
    
    /**
     * Internal callback interface that specifies how to build the real content of a MathML
     * element.
     */
    public static interface MathContentBuilderCallback {
        
        public void buildMathElementContent(final Element contentContainerElement,
                final ArgumentContainerToken mathContentToken, final boolean isAnnotated)
                throws SnuggleParseException;
    }
    
    /**
     * Trivial instance of {@link MathContentBuilderCallback} that simply descends into child
     * tokens, handling the annotated/non-annotated case as required.
     */
    private final MathContentBuilderCallback defaultMathContentBuilderCallback = new MathContentBuilderCallback() {
        
        public void buildMathElementContent(final Element contentContainerElement,
                final ArgumentContainerToken mathContentToken, final boolean isAnnotated)
                throws SnuggleParseException {
            if (isAnnotated) {
                /* If annotated, then need to ensure we only generate one child Node */
                handleMathTokensAsSingleElement(contentContainerElement, mathContentToken);
            }
            else {
                handleTokens(contentContainerElement, mathContentToken, false);
            }
        }
    };
    
    /**
     * Builds a MathML <tt>math</tt> element for the given Token token, using contentToken
     * to provide the content, using the given {@link MathContentBuilderCallback} to
     * supply the content.
     * <p>
     * This is useful for things like {@link EqnArrayHandler} which generate a rather specific
     * structure.
     */
    public void buildMathElement(final Element parentElement,
            final Token token, final ArgumentContainerToken mathContentToken,
            final boolean isDisplayMath, final MathContentBuilderCallback mathContentBuilderCallback)
            throws SnuggleParseException {
        /* Change outputContext */
        pushOutputContext(isDisplayMath ? OutputContext.MATHML_BLOCK : OutputContext.MATHML_INLINE);
        
        /* Create <math>...</math> container with appropriate attributes */
        Element math = appendMathMLElement(parentElement, "math");
        if (isDisplayMath) {
            math.setAttribute("display", "block");
        }
        
        /* Build up content, with annotation if necessary */
        if (options.isAddingMathSourceAnnotations()) {
            /* The structure here is <semantics>...<annotation/></semantics>
             * where the first child of <semantics> is the resulting MathML
             * build by the callback, which must be a single element in this case.
             */
            Element semantics = appendMathMLElement(math, "semantics");
            Element container;
            if (options.isApplyingFirefox3SemanticsWorkaround() && isDisplayMath) {
                /* Work around Firefox 3 bug ignoring display="block" inside semantics by wrapping in an additional <mstyle/> */
                container = appendMathMLElement(semantics, "mstyle");
                container.setAttribute("displaystyle", "true");
            }
            else {
                container = semantics;
            }
            
            /* Descend into Math content */
            mathContentBuilderCallback.buildMathElementContent(container, mathContentToken, true);
            
            /* Construct annotation */
            String annotationContent = token.getSlice().extract().toString();
            Element sourceAnnotation = appendMathMLTextElement(semantics, "annotation", annotationContent, true);
            sourceAnnotation.setAttribute("encoding", SnuggleConstants.SNUGGLETEX_MATHML_SOURCE_ANNOTATION_ENCODING);
        }
        else {
            mathContentBuilderCallback.buildMathElementContent(math, mathContentToken, false);
        }
        /* Reset OutputContext */
        popOutputContext();
    }

    public Element findNearestXHTMLAncestorOrSelf(final Element element) {
        Element currentElement = element;
        Node parentNode;
        while (true) {
            if (currentElement==buildRootElement) {
                /* We're at the root of our tree, so stop */
                return currentElement;
            }
            else if (W3CConstants.XHTML_NAMESPACE.equals(currentElement.getNamespaceURI())) {
                /* We're at an XHTML element, so stop */
                return currentElement;
            }
            else  {
                /* Go up */
                parentNode = currentElement.getParentNode();
                if (parentNode==null || parentNode.getNodeType()!=Node.ELEMENT_NODE) {
                    throw new SnuggleLogicException("Traversed up DOM tree and never found our root Element!");
                }
                currentElement = (Element) parentNode;
            }
        }
    }
    
    public boolean isParentElement(final Element parentElement, final String requiredNamespaceUri,
            final String... requiredLocalNames) {
        if (!requiredNamespaceUri.equals(parentElement.getNamespaceURI())) {
            return false;
        }
        if (requiredLocalNames.length==0) {
            return true;
        }
        /* Ensure we match one of the given local names */
        String localName = parentElement.getLocalName();
        for (String allowedLocalName : requiredLocalNames) {
            if (localName.equals(allowedLocalName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks the given raw XML Name to ensure that it confirms to the correct syntax, returning
     * it unchanged if successful. If not valid, the {@link CoreErrorCode#TDEX03} is recorded.
     * 
     * @throws SnuggleParseException
     */
    public String validateXMLName(final Element parentElement, final Token nameToken, final String rawName)
            throws SnuggleParseException {
        /* Check name. This is easy since our input is good old ASCII so the XML Name production
         * simplifies to the regexp below...
         */
        if (!XMLUtilities.isXMLName(rawName)) {
            /* Error: Bad XML Name */
            appendOrThrowError(parentElement, nameToken, CoreErrorCode.TDEX03, rawName);
            return null;
        }
        return rawName;
    }
    
    /**
     * Checks the given XML Name as in {@link #validateXMLName(Element, Token, String)}, additionally
     * checking that an ID of the same name is not already in use in the output DOM. If it is,
     * {@link CoreErrorCode#TDEX05} is recorded. Returns non-null on success, null on error.
     * 
     * @throws SnuggleParseException
     */
    public String validateXMLId(final Element parentElement, final Token nameToken, final String rawName)
            throws SnuggleParseException {
        String validatedName = validateXMLName(parentElement, nameToken, rawName);
        if (validatedName!=null) {
            if (document.getElementById(validatedName)!=null) {
                /* Error: ID already in use */
                appendOrThrowError(parentElement, nameToken, CoreErrorCode.TDEX05, rawName);
                return null;
            }
        }
        return validatedName;
    }
    
    /**
     * Checks the given raw URI to ensure that it is valid, returning a {@link URI} Object if
     * valid or null if not. If not valid, the {@link CoreErrorCode#TDEX04} is recorded.
     * 
     * @throws SnuggleParseException
     */
    public URI validateURI(final Element parentElement, final Token token, final String rawURI)
            throws SnuggleParseException {
        URI result;
        try {
            result = new URI(rawURI);
        }
        catch (URISyntaxException e) {
            /* Error: not a URI */
            appendOrThrowError(parentElement, token, CoreErrorCode.TDEX04, rawURI);
            return null;
        }
        return result;
    }
    
    public URI resolveLink(final Element parentElement, final Token token, final String rawHref)
            throws SnuggleParseException {
        URI result = validateURI(parentElement, token, rawHref);
        if (result==null) {
            return null;
        }
        LinkResolver linkResolver = options.getLinkResolver();
        if (linkResolver!=null) {
            result = linkResolver.mapLink(result, token.getSlice().getDocument().getInput().getURI());
        }
        return result;
    }
    
    //-------------------------------------------

    public Element appendOrThrowError(final Element parentElement, final Token token,
            final ErrorCode errorCode, final Object... arguments)
            throws SnuggleParseException {
        InputError error = new InputError(errorCode, token.getSlice(), arguments);
        sessionContext.registerError(error);
        return appendErrorElement(parentElement, new ErrorToken(error, token.getLatexMode()));
    }
    
    public Element appendErrorElement(final Element parentElement, final ErrorToken errorToken) {
        ErrorOutputOptions errorOptions = options.getErrorOutputOptions();
        Element errorElement;
        switch (errorOptions) {
            case NO_OUTPUT:
                /* Add nothing */
                break;
                
            case XML_FULL:
            case XML_SHORT:
                /* Output XML at the current point in the DOM */
                errorElement = MessageFormatter.formatErrorAsXML(document,
                        errorToken.getError(),
                        errorOptions==ErrorOutputOptions.XML_FULL);
                parentElement.appendChild(errorElement);
                break;
                
            case XHTML:
                /* If we're in the middle of a MathML island,
                 * add a MathML <merror/> element at the current point */
                if (isBuildingMathMLIsland()) {
                    Element merror = appendMathMLElement(parentElement, "merror");
                    appendMathMLTextElement(merror, "mtext",
                            errorToken.getError().getErrorCode().toString(), false);
                }
                
                /* Output full XHTML fragment as a child of the nearest non-MathML ancestor-or-self */
                Element ancestorElement = findNearestXHTMLAncestorOrSelf(parentElement);
                errorElement = MessageFormatter.formatErrorAsXHTML(document, errorToken.getError());
                ancestorElement.appendChild(errorElement);
                break;
                
            default:
                throw new SnuggleLogicException("Unexpected switch case " + errorOptions);
        }
        return parentElement;
    }
}
