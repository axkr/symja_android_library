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
 * The lambda element is used to construct a user-defined function from an
 * expression and one or more free variables.
 * 
 * 
 */
public interface MathMLLambdaElement extends MathMLContentContainer {
    /**
     * The MathMLElement representing the expression. This is included only as
     * a convenience; getting it should give the same result as
     * MathMLContentContainer::getArgument(1).
     * 
     * @return value of the expression attribute.
     */
    MathMLElement getExpression();

    /**
     * setter for the expression attribute.
     * 
     * @param expression
     *            new value for expression.
     * @see #getExpression()
     */
    void setExpression(MathMLElement expression);
};
