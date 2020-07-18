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
 * This interface represents the semantics element in MathML.
 * 
 */
public interface MathMLSemanticsElement extends MathMLElement {
    /**
     * This attribute represents the first child of the semantics element,
     * i.e. the child giving the primary content represented by the element.
     * 
     * @return value of the body attribute.
     */
    MathMLElement getBody();

    /**
     * setter for the body attribute.
     * 
     * @param body
     *            new value for body.
     * @see #getBody()
     */
    void setBody(MathMLElement body);

    /**
     * Represents the number of annotation or annotation-xml children of the
     * semantics element, i.e. the number of alternate content forms for this
     * element.
     * 
     * @return value of the nAnnotations attribute.
     */
    int getNAnnotations();

    /**
     * This method gives access to the index-th alternate content associated
     * with a semantics element.
     * 
     * @param index
     *            The one-based index of the annotation being retrieved.
     * @return The MathMLAnnotationElement or MathMLXMLAnnotationElement
     *         representing the index-th annotation or annotation-xml child of
     *         the semantics element. Note that all child elements of a
     *         semantics element other than the first are required to be of
     *         one of these types.
     */
    MathMLElement getAnnotation(int index);

    /**
     * This method inserts newAnnotation before the current index-th alternate
     * content associated with a semantics element. If index is 0,
     * newAnnotation is appended as the last annotation or annotation-xml
     * child of this element.
     * 
     * @param newAnnotation
     *            A MathMLAnnotationElement or MathMLXMLAnnotationElement
     *            representing the new annotation or annotation-xml to be
     *            inserted.
     * @param index
     *            The position in the list of annotation or annotation-xml
     *            children before which newAnnotation is to be inserted. The
     *            first annotation is numbered 1.
     * @return The MathMLAnnotationElement or MathMLXMLAnnotationElement child
     *         of this element that represents the new annotation in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newAnnotation is not a
     *             MathMLAnnotationElement or MathMLXMLAnnotationElement.
     *             INDEX_SIZE_ERR: Raised if index is greater than the current
     *             number of annotation or annotation-xml children of this
     *             semantics element.
     */
    MathMLElement insertAnnotation(MathMLElement newAnnotation, int index)
            throws DOMException;

    /**
     * This method allows setting or replacement of the index-th alternate
     * content associated with a semantics element. If there is already an
     * annotation or annotation-xml element with this index, it is replaced by
     * newAnnotation.
     * 
     * @param newAnnotation
     *            A MathMLAnnotationElement or MathMLXMLAnnotationElement
     *            representing the new value of the index-th annotation or
     *            annotation-xml child of this semantics element.
     * @param index
     *            The position in the list of annotation or annotation-xml
     *            children of this semantics element that is to be occupied by
     *            newAnnotation. The first annotation element is numbered 1.
     * @return The MathMLAnnotationElement or MathMLXMLAnnotationElement child
     *         of this element that represents the new annotation in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newAnnotation is not a
     *             MathMLAnnotationElement or MathMLXMLAnnotationElement.
     *             INDEX_SIZE_ERR: Raised if index is greater than one more
     *             than the current number of annotation or annotation-xml
     *             children of this semantics element.
     */
    MathMLElement setAnnotation(MathMLElement newAnnotation, int index)
            throws DOMException;

    /**
     * A convenience method to delete the index-th alternate content
     * associated with this semantics element.
     * 
     * @param index
     *            The one-based index of the annotation being deleted.
     */
    void deleteAnnotation(int index);

    /**
     * A convenience method to delete the index-th alternate content
     * associated with this semantics element, and to return it to the caller.
     * 
     * @param index
     *            The one-based index of the annotation being deleted.
     * @return The MathMLAnnotationElement or MathMLXMLAnnotationElement being
     *         deleted.
     */
    MathMLElement removeAnnotation(int index);
};
