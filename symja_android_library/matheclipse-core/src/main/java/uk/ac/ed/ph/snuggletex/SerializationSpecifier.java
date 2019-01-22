/* $Id: SerializationSpecifier.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex;

import uk.ac.ed.ph.snuggletex.utilities.MathMLUtilities;
import uk.ac.ed.ph.snuggletex.utilities.SerializationOptions;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;

/**
 * Interface specifying various options for serializing XML produced by SnuggleTeX
 * and some of its utility classes.
 * <p>
 * The main implementation of this is {@link XMLStringOutputOptions}, which you can use
 * for normal SnuggleTeX functionality. There is also a {@link SerializationOptions}
 * implementation that can be used for various utility classes, such as {@link MathMLUtilities}.
 * 
 * @see XMLStringOutputOptions
 * @see SerializationOptions
 * 
 * @since 1.2.0
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public interface SerializationSpecifier {
    
    /** Default encoding to use */
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    /**
     * Gets the {@link SerializationMethod} to use when generating the final output.
     * <p>
     * Default is {@link SerializationMethod#XML}.
     * This must not be null.
     * <p>
     * Note that {@link SerializationMethod#XHTML} is only supported properly if you are using
     * an XSLT 2.0 processor; otherwise it reverts to {@link SerializationMethod#XML}
     */
    SerializationMethod getSerializationMethod();

    /**
     * Sets the {@link SerializationMethod} to use when generating the final output.
     * This must not be null.
     * <p>
     * Note that {@link SerializationMethod#XHTML} is only supported properly if you are using
     * an XSLT 2.0 processor; otherwise it reverts to {@link SerializationMethod#XML}
     * 
     * @param serializationMethod {@link SerializationMethod} to use, which must not be null.
     */
    void setSerializationMethod(SerializationMethod serializationMethod);
    
    
    /** 
     * Gets the encoding for the resulting serialized XML.
     * <p>
     * Default is {@link SerializationSpecifier#DEFAULT_ENCODING}.
     */
    String getEncoding();
    
    /** 
     * Sets the encoding for the resulting serialized XML.
     * <p>
     * Must not be null.
     * 
     * @param encoding encoding to use, which must be non-null and recognised by the XSLT
     *   {@link TransformerFactory} that will end up doing the serialization.
     */
    void setEncoding(String encoding);
    
    
    /**
     * Returns whether the resulting XML will be indented or not.
     * (This depends on how clever the underlying XSLT engine will be!)
     * <p>
     * Default is false.
     */
    boolean isIndenting();
    
    /**
     * Sets whether the resulting XML will be indented or not.
     * (This depends on how clever the underlying XSLT engine will be!)
     * 
     * @param indenting true to indent, false otherwise.
     */
    void setIndenting(boolean indenting);
    
    
    /**
     * Gets whether to include an XML declaration on the resulting output.
     * Default is false.
     */
    boolean isIncludingXMLDeclaration();
    
    /**
     * Sets whether to include an XML declaration on the resulting output.
     * 
     * @param includingXMLDeclaration true to include an XML declaration, false otherwise.
     */
    void setIncludingXMLDeclaration(boolean includingXMLDeclaration);
    
    
    /**
     * Returns whether to use named entities for certain MathML symbols rather than
     * numeric character references.
     * <p>
     * Default is false.
     */
    boolean isUsingNamedEntities();

    /**
     * Sets whether to use named entities for certain MathML symbols rather than
     * numeric character references.
     * <p>
     * Note that this requires an XSLT 2.0-compliant engine (e.g. Saxon, which is in the
     * "full" SnuggleTeX distribution.)
     * <p>
     * (Also note that the resulting XML won't be parseable unless accompanied with a DTD
     * defining the MathML entities!)
     * 
     * @param usingNamedEntities true to use named entities, false to default to using
     *   numeric character references.
     */
    void setUsingNamedEntities(boolean usingNamedEntities);
    

    /**
     * Gets the public identifier to use in the resulting DOCTYPE declaration,
     * as described in {@link OutputKeys#DOCTYPE_PUBLIC}.
     * <p>
     * Default is null
     */
    String getDoctypePublic();

    /**
     * Sets the public identifier to use in the resulting DOCTYPE declaration,
     * as described in {@link OutputKeys#DOCTYPE_PUBLIC}.
     * 
     * @param doctypePublic public identifier to use, null for no identifier.
     */
    void setDoctypePublic(String doctypePublic);


    /**
     * Gets the system identifier to use in the resulting DOCTYPE declaration,
     * as described in {@link OutputKeys#DOCTYPE_SYSTEM}.
     * <p>
     * Default is null
     */
    String getDoctypeSystem();

    /**
     * Sets the system identifier to use in the resulting DOCTYPE declaration,
     * as described in {@link OutputKeys#DOCTYPE_SYSTEM}.
     * 
     * @param doctypeSystem system identifier to use, null for no identifier.
     */
    void setDoctypeSystem(String doctypeSystem);
}
