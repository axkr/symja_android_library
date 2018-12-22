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
 * The tendsto element expresses that a quantity is tending to a specified
 * value. An interface derived from the MathMLPredefinedSymbol is used to
 * allow for the inclusion of the type attribute.
 * 
 * 
 */
public interface MathMLTendsToElement extends MathMLPredefinedSymbol {
    /**
     * A string describing how the quantity approaches the specified value.
     * Predefined values of the string include above, below, and two-sided.
     * The default value is two-sided.
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
};
