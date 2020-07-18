/*
 * Copyright 2007 - 2007 JEuclid, http://jeuclid.sf.net
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.w3c.dom.mathml;

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * The declare construct has two primary roles. The first is to change or set
 * the default attribute values for a specific mathematical object. The second
 * is to establish an association between a name and an object.
 * 
 * 
 */
public interface MathMLDeclareElement extends MathMLContentElement {
    /**
     * A string indicating the type of the identifier. It must be compatible
     * with the type of the constructor, if a constructor is present. The type
     * is inferred from the constructor if present, otherwise it must be
     * specified.
     * 
     * @return value of the type attribute.
     */
    String getType();

    /**
     * setter for the type attribute.
     * 
     * @param type
     *            new value for type.
     * @see #getType()
     */
    void setType(String type);

    /**
     * If the identifier is a function, this attribute specifies the number of
     * arguments the function takes. This represents the declare element's
     * nargs attribute; see .
     * 
     * @return value of the nargs attribute.
     */
    int getNargs();

    /**
     * setter for the nargs attribute.
     * 
     * @param nargs
     *            new value for nargs.
     * @see #getNargs()
     */
    void setNargs(int nargs);

    /**
     * A string with the values prefix, infix, postfix, or function-model.
     * 
     * @return value of the occurrence attribute.
     */
    String getOccurrence();

    /**
     * setter for the occurrence attribute.
     * 
     * @param occurrence
     *            new value for occurrence.
     * @see #getOccurrence()
     */
    void setOccurrence(String occurrence);

    /**
     * A URI specifying the detailed semantics of the element.
     * 
     * @return value of the definitionURL attribute.
     */
    String getDefinitionURL();

    /**
     * setter for the definitionURL attribute.
     * 
     * @param definitionURL
     *            new value for definitionURL.
     * @see #getDefinitionURL()
     */
    void setDefinitionURL(String definitionURL);

    /**
     * A description of the syntax used in definitionURL.
     * 
     * @return value of the encoding attribute.
     */
    String getEncoding();

    /**
     * setter for the encoding attribute.
     * 
     * @param encoding
     *            new value for encoding.
     * @see #getEncoding()
     */
    void setEncoding(String encoding);

    /**
     * A MathMLCiElement representing the name being declared.
     * 
     * @return value of the identifier attribute.
     */
    MathMLCiElement getIdentifier();

    /**
     * setter for the identifier attribute.
     * 
     * @param identifier
     *            new value for identifier.
     * @see #getIdentifier()
     */
    void setIdentifier(MathMLCiElement identifier);

    /**
     * An optional MathMLElement providing an initial value for the object
     * being declared.
     * 
     * @return value of the constructor attribute.
     */
    MathMLElement getConstructor();

    /**
     * setter for the constructor attribute.
     * 
     * @param constructor
     *            new value for constructor.
     * @see #getConstructor()
     */
    void setConstructor(MathMLElement constructor);
};
