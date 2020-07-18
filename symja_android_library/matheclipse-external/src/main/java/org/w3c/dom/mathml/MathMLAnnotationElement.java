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
 * This interface represents the annotation element of MathML.
 * 
 * 
 */
public interface MathMLAnnotationElement extends MathMLElement {
    /**
     * Provides access to the content of an annotation element.
     * 
     * @return value of the body attribute.
     */
    String getBody();

    /**
     * setter for the body attribute.
     * 
     * @param body
     *            new value for body.
     * @see #getBody()
     */
    void setBody(String body);

    /**
     * Provides access to the encoding attribute of an annotation element.
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
};
