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
 * This interface extends the MathMLPresentationElement interface for the
 * MathML alignment mark element malignmark.
 * 
 * 
 */
public interface MathMLAlignMarkElement extends MathMLPresentationElement {
    /**
     * A string specifying alignment on the right edge of the preceding
     * element or the left edge of the following element. Allowed values are
     * left and right.
     * 
     * @return value of the edge attribute.
     */
    String getEdge();

    /**
     * setter for the edge attribute.
     * 
     * @param edge
     *            new value for edge.
     * @see #getEdge()
     */
    void setEdge(String edge);
};
