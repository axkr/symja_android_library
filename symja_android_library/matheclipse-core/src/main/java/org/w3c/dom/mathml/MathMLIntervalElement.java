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
 * The interval element is used to represent simple mathematical intervals on
 * the real number line. It contains either two child elements that evaluate
 * to real numbers or one child element that is a condition for defining
 * membership in the interval.
 * 
 * 
 */
public interface MathMLIntervalElement extends MathMLContentElement {
    /**
     * A string with value open, closed, open-closed or closed-open. The
     * default value is closed.
     * 
     * @return value of the closure attribute.
     */
    String getClosure();

    /**
     * setter for the closure attribute.
     * 
     * @param closure
     *            new value for closure.
     * @see #getClosure()
     */
    void setClosure(String closure);

    /**
     * A MathMLContentElement representing the real number defining the start
     * of the interval. If end has not already been set, it becomes the same
     * as start until set otherwise.
     * 
     * @return value of the start attribute.
     */
    MathMLContentElement getStart();

    /**
     * setter for the start attribute.
     * 
     * @param start
     *            new value for start.
     * @see #getStart()
     */
    void setStart(MathMLContentElement start);

    /**
     * A MathMLContentElement representing the real number defining the end of
     * the interval. If start has not already been set, it becomes the same as
     * end until set otherwise.
     * 
     * @return value of the end attribute.
     */
    MathMLContentElement getEnd();

    /**
     * setter for the end attribute.
     * 
     * @param end
     *            new value for end.
     * @see #getEnd()
     */
    void setEnd(MathMLContentElement end);
};
