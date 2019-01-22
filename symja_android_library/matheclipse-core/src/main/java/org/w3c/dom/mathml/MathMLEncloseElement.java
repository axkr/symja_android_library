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
 * This interface supports the menclose element .
 * 
 * 
 */
public interface MathMLEncloseElement extends MathMLPresentationContainer {
    /**
     * A string giving a name for the notation enclosing the element's
     * contents. Represents the notation attribute of the menclose. Any string
     * is allowed as a value; predefined values include longdiv, actuarial,
     * radical, box, roundedbox, circle, left, right, top, bottom,
     * updiagonalstrike, downdiagonalstrike, verticalstrike, horizontalstrike,
     * or combinations of these strings separated by whitespace (see ).
     * 
     * @return value of the notation attribute.
     */
    String getNotation();

    /**
     * setter for the notation attribute.
     * 
     * @param notation
     *            new value for notation.
     * @see #getNotation()
     */
    void setNotation(String notation);
};
