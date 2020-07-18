/* $Id: XMLUtilities.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal.util;

import uk.ac.ed.ph.snuggletex.SerializationSpecifier;
import uk.ac.ed.ph.snuggletex.SnuggleRuntimeException;
import uk.ac.ed.ph.snuggletex.definitions.Globals;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetManager;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Some trivial little helpers for creating suitably-configured {@link TransformerFactory}
 * instances.
 * <p>
 * (This is based on similar utility methods in <tt>ph-commons</tt>.)
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class XMLUtilities {
    
    /** Explicit name of the SAXON 9.X TransformerFactoryImpl Class, as used by the up-conversion extensions */
    public static final String SAXON_TRANSFORMER_FACTORY_CLASS_NAME = "net.sf.saxon.TransformerFactoryImpl";

    /**
     * Creates an instance of the currently specified JAXP {@link TransformerFactory}, ensuring
     * that the result supports the {@link DOMSource#FEATURE} and {@link DOMResult#FEATURE}
     * features.
     */
    public static TransformerFactory createJAXPTransformerFactory() {
        TransformerFactory transformerFactory = null;
        try {
            transformerFactory = TransformerFactory.newInstance();
        }
        catch (TransformerFactoryConfigurationError e) {
            throw new SnuggleRuntimeException(e);
        }
        /* Make sure we have DOM-based features */
        requireFeature(transformerFactory, DOMSource.FEATURE);
        requireFeature(transformerFactory, DOMResult.FEATURE);
        
        /* Must have been OK! */
        return transformerFactory;
    }
    
    public static void requireFeature(final TransformerFactory transformerFactory, final String feature) {
        if (!transformerFactory.getFeature(feature)) {
            throw new SnuggleRuntimeException("TransformerFactory "
                    + transformerFactory.getClass().getName()
                    + " needs to support feature "
                    + feature
                    + " in order to be used with SnuggleTeX");
        }   
    }
    
    public static boolean isSaxonAvailable() {
        try {
            Class.forName(SAXON_TRANSFORMER_FACTORY_CLASS_NAME);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Explicitly creates a Saxon 9 {@link TransformerFactory}, as used by the up-conversion
     * extensions.
     */
    public static TransformerFactory createSaxonTransformerFactory() {
        TransformerFactory transformerFactory;
        try {
            /* We call up SAXON explicitly without going through the usual factory path */
            transformerFactory = (TransformerFactory) Class.forName(SAXON_TRANSFORMER_FACTORY_CLASS_NAME).newInstance();
        }
        catch (Exception e) {
            throw new SnuggleRuntimeException("Failed to explicitly instantiate Saxon "
                    + SAXON_TRANSFORMER_FACTORY_CLASS_NAME
                    + " class - check your ClassPath!", e);
        }
        /* Make sure we have DOM-based features */
        requireFeature(transformerFactory, DOMSource.FEATURE);
        requireFeature(transformerFactory, DOMResult.FEATURE);
        
        /* Must have been OK! */
        return transformerFactory;
    }
    
    /**
     * Tests whether the given {@link TransformerFactory} is known to support XSLT 2.0.
     * <p>
     * (Currently, this involves checking for a suitable version of Saxon; this will
     * change once more processors become available.)
     */
    public static boolean supportsXSLT20(final TransformerFactory tranformerFactory) {
        return tranformerFactory.getClass().getName().startsWith("net.sf.saxon.");
    }
    
    public static boolean supportsXSLT20(final Transformer tranformer) {
        return tranformer.getClass().getName().startsWith("net.sf.saxon.");
    }
    
    //------------------------------------------------------------------
    
    /**
     * Creates a (namespace-aware) DOM {@link DocumentBuilder}, throwing a {@link SnuggleRuntimeException}
     * if such a thing cannot be created/configured.
     */
    public static DocumentBuilder createNSAwareDocumentBuilder() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            return documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new SnuggleRuntimeException("Could not create Namespace-aware DocumentBuilder", e);
        }
    }

    /**
     * Trivial convenience method to extract the value of a (text) Element, coping with the
     * possibility that text child Nodes may not have been coalesced.
     * 
     * @param textElement
     */
    public static String extractTextElementValue(final Element textElement) {
        NodeList childNodes = textElement.getChildNodes();
        String result;
        if (childNodes.getLength()==1) {
            /* Text Nodes have been coalesced */
            result = ensureExtractTextNodeValue(childNodes.item(0));
        }
        else {
            /* Need to coalesce manually */
            StringBuilder resultBuilder = new StringBuilder();
            for (int i=0; i<childNodes.getLength(); i++) {
                resultBuilder.append(ensureExtractTextNodeValue(childNodes.item(i)));
            }
            result = resultBuilder.toString();
        }
        return result;
    }
    
    private static String ensureExtractTextNodeValue(final Node node) {
        if (node.getNodeType()==Node.TEXT_NODE) {
            return node.getNodeValue();
        }
        throw new IllegalArgumentException("Node is not a text Node");
    }
    
    //------------------------------------------------------------------
    
    /**
     * Serializes the given {@link Node} to a well-formed external parsed entity.
     * (If the given Node is an {@link Element} or a {@link Document} then the result
     * will be a well-formed XML String.)
     *
     * @param node DOM Node to serialize.
     * @param serializationOptions XML serialization options
     */
    public static String serializeNode(final Node node, final SerializationSpecifier serializationOptions) {
        return serializeNode(new StylesheetManager(), node, serializationOptions);
    }
    
    /**
     * Serializes the given {@link Node} to a well-formed external parsed entity.
     * (If the given Node is an {@link Element} or a {@link Document} then the result
     * will be a well-formed XML String.)
     * <p>
     * (This may use an XSLT 2.0 stylesheet to help with named entities, hence the requirement for a
     * {@link StylesheetManager}).
     * 
     * @param stylesheetManager used to help compile and cache stylesheets used in this process.
     *
     * @param node DOM Node to serialize.
     * @param serializationOptions XML serialization options
     */
    public static String serializeNode(StylesheetManager stylesheetManager, final Node node,
            final SerializationSpecifier serializationOptions) {
        StringWriter resultWriter = new StringWriter();
        
        /* This process consists of an XSLT 1.0 transform to extract the child Nodes, plus
         * a further optional XSLT 2.0 transform to map character references to named entities.
         */
        try {
            Transformer serializer = stylesheetManager.getSerializer(null, serializationOptions);
            serializer.transform(new DOMSource(node), new StreamResult(resultWriter));
        }
        catch (Exception e) {
            throw new SnuggleRuntimeException("Could not serialize DOM", e);
        }
        return resultWriter.toString();
    }
    
    /**
     * Serializes the <tt>children</tt> of given {@link Node} to a well-formed external parsed entity.
     * <p>
     * (This uses a little XSLT stylesheet to help, hence the requirement for a {@link StylesheetManager}).
     * 
     * @param stylesheetManager used to help compile and cache stylesheets used in this process.
     * @param node DOM Node to serialize.
     * @param serializationOptions XML serialization options
     */
    public static String serializeNodeChildren(StylesheetManager stylesheetManager, final Node node,
            final SerializationSpecifier serializationOptions) {
        StringWriter resultWriter = new StringWriter();
        
        /* This process consists of an XSLT 1.0 transform to extract the child Nodes, plus
         * a further optional XSLT 2.0 transform to map character references to named entities.
         */
        try {
            Transformer serializer = stylesheetManager.getSerializer(Globals.EXTRACT_CHILD_NODES_XSL_RESOURCE_NAME,
                    serializationOptions);
            serializer.transform(new DOMSource(node), new StreamResult(resultWriter));
        }
        catch (Exception e) {
            throw new SnuggleRuntimeException("Could not serialize DOM", e);
        }
        return resultWriter.toString();
    }
    
    //------------------------------------------------------------------
    
    public static boolean isXMLName(String string) {
        return string!=null && string.matches("[a-zA-Z_:][a-zA-Z0-9_:.-]*");
    }
    
    public static boolean isXMLNCName(String string) {
        return string!=null && string.matches("[a-zA-Z_][a-zA-Z0-9_.-]*");
    }
}