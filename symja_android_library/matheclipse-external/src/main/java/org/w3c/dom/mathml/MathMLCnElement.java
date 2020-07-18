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
 * The cn element is used to specify actual numeric constants.
 * 
 * 
 */
public interface MathMLCnElement extends MathMLContentToken {
    /**
     * Values include, but are not restricted to, e-notation, integer,
     * rational, real, float, complex, complex-polar, complex-cartesian, and
     * constant.
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
     * A string representing an integer between 2 and 36; the base of the
     * numerical representation.
     * 
     * @return value of the base attribute.
     */
    String getBase();

    /**
     * setter for the base attribute.
     * 
     * @param base
     *            new value for base.
     * @see #getBase()
     */
    void setBase(String base);

    /**
     * The number of sep-separated arguments.
     * 
     * @return value of the nargs attribute.
     */
    int getNargs();
};
