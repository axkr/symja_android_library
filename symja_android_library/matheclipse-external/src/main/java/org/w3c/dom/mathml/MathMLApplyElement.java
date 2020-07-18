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
 * The apply element allows a function or operator to be applied to its
 * arguments.
 * 
 * 
 */
public interface MathMLApplyElement extends MathMLContentContainer {
    /**
     * The MathML element representing the function or operator that is
     * applied to the list of arguments.
     * 
     * @return value of the operator attribute.
     */
    MathMLElement getOperator();

    /**
     * setter for the operator attribute.
     * 
     * @param operator
     *            new value for operator.
     * @see #getOperator()
     */
    void setOperator(MathMLElement operator);

    /**
     * This attribute represents the lowlimit child element of this node (if
     * any). This expresses, for instance, the lower limit of integration if
     * this is an apply element whose first child is a int. See .
     * 
     * @return value of the lowLimit attribute.
     */
    MathMLElement getLowLimit();

    /**
     * setter for the lowLimit attribute.
     * 
     * @param lowLimit
     *            new value for lowLimit.
     * @see #getLowLimit()
     */
    void setLowLimit(MathMLElement lowLimit);

    /**
     * This attribute represents the uplimit child element of this node (if
     * any). This expresses, for instance, the upper limit of integration if
     * this is an apply element whose first child is a int. See .
     * 
     * @return value of the upLimit attribute.
     */
    MathMLElement getUpLimit();

    /**
     * setter for the upLimit attribute.
     * 
     * @param upLimit
     *            new value for upLimit.
     * @see #getUpLimit()
     */
    void setUpLimit(MathMLElement upLimit);
};
