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

import org.w3c.dom.DOMException;

/*
 * Please note: This file was automatically generated from the source of the
 * MathML specification. Do not edit it. If there are errors or missing
 * elements, please correct the stylesheet instead.
 */

/**
 * vector is the container element for a vector.
 * 
 * 
 */
public interface MathMLVectorElement extends MathMLContentElement {
    /**
     * The number of components in the vector.
     * 
     * @return value of the ncomponents attribute.
     */
    int getNcomponents();

    /**
     * A convenience method to retrieve a component.
     * 
     * @param index
     *            Position of the component in the list of components. The
     *            first element is numbered 1.
     * @return The MathMLContentElement component at the position specified by
     *         index. If index is not a valid index (i.e. is greater than the
     *         number of components of the vector or less than 1), a null
     *         MathMLContentElement is returned.
     */
    MathMLContentElement getComponent(int index);

    /**
     * A convenience method to insert a new component in the vector before the
     * current index-th component. If index is 0 or is one more than the
     * number of components currently in the vector, newComponent is appended
     * as the last component of the vector.
     * 
     * @param newComponent
     *            A MathMLContentElement representing the component that is to
     *            be added.
     * @param index
     *            Position of the component in the list of components. The
     *            first component is numbered 1.
     * @return The MathMLContentElement child of this MathMLVectorElement
     *         representing the new component in the DOM.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than one more
     *             than the current number of components of this vector
     *             element.
     */
    MathMLContentElement insertComponent(MathMLContentElement newComponent,
            int index) throws DOMException;

    /**
     * A convenience method to set the index-th component of the vector to
     * newComponent. If index is one more than the current number of
     * components, newComponent is appended as the last component.
     * 
     * @param newComponent
     *            A MathMLContentElement representing the element that is to
     *            be the index-th component of the vector.
     * @param index
     *            Position of the component in the list of components. The
     *            first element is numbered 1.
     * @return The MathMLContentElement child of this MathMLVectorElement that
     *         represents the new component in the DOM.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than one more
     *             than the current number of components of this vector
     *             element.
     */
    MathMLContentElement setComponent(MathMLContentElement newComponent,
            int index) throws DOMException;

    /**
     * A convenience method to delete an element. The deletion changes the
     * indices of the following components.
     * 
     * @param index
     *            Position of the component in the vector. The position of the
     *            first component is 1
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the current
     *             number of components of this vector element.
     */
    void deleteComponent(int index) throws DOMException;

    /**
     * A convenience method to remove a component from a vector and return it
     * to the caller. If index is greater than the number of components or is
     * 0, a null MathMLContentElement is returned.
     * 
     * @param index
     *            Position of the component in the list of components. The
     *            first element is numbered 1.
     * @return The MathMLContentElement component being removed.
     */
    MathMLContentElement removeComponent(int index);
};
