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
 * The condition element is used to place a condition on one or more free
 * variables or identifiers.
 * 
 * 
 */
public interface MathMLConditionElement extends MathMLContentElement {
    /**
     * A MathMLApplyElement that represents the condition.
     * 
     * @return value of the condition attribute.
     */
    MathMLApplyElement getCondition();

    /**
     * setter for the condition attribute.
     * 
     * @param condition
     *            new value for condition.
     * @see #getCondition()
     */
    void setCondition(MathMLApplyElement condition);
};
