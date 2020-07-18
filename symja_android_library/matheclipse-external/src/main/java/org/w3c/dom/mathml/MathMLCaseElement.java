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
 * The piece element represents one of a sequence of cases used in the
 * piecewise definition of a function. It contains two child elements, each
 * represented by a MathMLContentElement. The first child determines the
 * subset of the domain affected, normally by giving a condition to be
 * satisfied. The second gives the value of the function over the indicated
 * subset of its domain.
 * 
 */
public interface MathMLCaseElement extends MathMLContentElement {
    /**
     * Accesses the MathMLContentElement representing the condition to be
     * satisfied in order for this branch of the piecewise definition to be
     * used.
     * 
     * @return value of the caseCondition attribute.
     */
    MathMLContentElement getCaseCondition();

    /**
     * setter for the caseCondition attribute.
     * 
     * @param caseCondition
     *            new value for caseCondition.
     * @see #getCaseCondition()
     */
    void setCaseCondition(MathMLContentElement caseCondition);

    /**
     * Accesses the MathMLContentElement representing the value to be taken by
     * the piecewise function when the condition described by caseCondition is
     * true.
     * 
     * @return value of the caseValue attribute.
     */
    MathMLContentElement getCaseValue();

    /**
     * setter for the caseValue attribute.
     * 
     * @param caseValue
     *            new value for caseValue.
     * @see #getCaseValue()
     */
    void setCaseValue(MathMLContentElement caseValue);
};
