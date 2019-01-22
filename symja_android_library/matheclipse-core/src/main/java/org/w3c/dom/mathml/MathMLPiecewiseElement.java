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
 * The piecewise element represents the piecewise definition of a function. It
 * contains child piece elements, each represented by a MathMLCaseElement,
 * giving the various conditions and associated function value specifications
 * in the function definition, and an optional otherwise child element,
 * represented by a MathMLContentElement, giving the default value of the
 * function - that is, the value to be assigned when none of the conditions
 * specified in the piece child elements hold.
 * 
 */
public interface MathMLPiecewiseElement extends MathMLContentElement {
    /**
     * A MathMLNodeList containing one MathMLCaseElement representing each of
     * the piece element children of this MathMLPiecewiseElement. The
     * otherwise child (if present) is not contained in this MathMLNodeList.
     * 
     * @return value of the pieces attribute.
     */
    MathMLNodeList getPieces();

    /**
     * Returns a MathMLContentElement representing the value to be taken by
     * the piecewise function when none of the conditions described in the
     * piece children is true.
     * 
     * @return value of the otherwise attribute.
     */
    MathMLContentElement getOtherwise();

    /**
     * setter for the otherwise attribute.
     * 
     * @param otherwise
     *            new value for otherwise.
     * @see #getOtherwise()
     */
    void setOtherwise(MathMLContentElement otherwise);

    /**
     * A convenience method to retrieve the child piece at the position
     * referenced by index.
     * 
     * @param index
     *            Position of desired case in the list of cases. The first
     *            piece is numbered 1; the otherwise child (if present) is not
     *            counted, regardless of its position. If index is greater
     *            than the number of pieces, a null MathMLCaseElement is
     *            returned; no error is generated.
     * @return The MathMLCaseElement retrieved.
     */
    MathMLCaseElement getCase(int index);

    /**
     * A convenience method to set the value of the child piece at the
     * position referenced by index to the value of case.
     * 
     * @param index
     *            Position of the piece to be set to case. The first piece is
     *            numbered 1; the otherwise child (if present) is not counted,
     *            regardless of its position. If there is currently a piece at
     *            this position, it will be replaced by case. If index is one
     *            more than the number of piece child elements, a new one will
     *            be appended.
     * @param caseElement
     *            A MathMLCaseElement representing the new value of the
     *            indexth piece child.
     * @return The new MathMLCaseElement created.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than one more
     *             than the number of pieces in this element.
     */
    MathMLCaseElement setCase(int index, MathMLCaseElement caseElement)
            throws DOMException;

    /**
     * A convenience method to delete the child piece at the position
     * referenced by index. The deletion changes the indices of the following
     * pieces.
     * 
     * @param index
     *            Position of the piece to be deleted. The first piece is
     *            numbered 1; the otherwise child (if present) is not counted,
     *            regardless of its position.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of pieces in this element.
     */
    void deleteCase(int index) throws DOMException;

    /**
     * A convenience method to remove the child piece at the position
     * referenced by index and return it to the caller. The removal changes
     * the indices of the following pieces.
     * 
     * @param index
     *            Position of the piece to be removed. The first piece is
     *            numbered 1; the otherwise child (if present) is not counted,
     *            regardless of its position.
     * @return The MathMLCaseElement removed.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of pieces in this element.
     */
    MathMLCaseElement removeCase(int index) throws DOMException;

    /**
     * A convenience method to insert a new piece child into this element.
     * 
     * @param index
     *            Position before which case is to be inserted. If index is 0,
     *            newCase is appended as the last piece child of this element.
     *            The otherwise child (if present) is not counted, regardless
     *            of its position.
     * @param newCase
     *            A MathMLCaseElement representing the piece to be inserted.
     * @return The new MathMLCaseElement inserted. This is the actual Element
     *         in the DOM.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater one more than
     *             the number of pieces in this element.
     */
    MathMLCaseElement insertCase(int index, MathMLCaseElement newCase)
            throws DOMException;

    /**
     * A convenience method to retrieve the child of the indexth piece in this
     * element which specifies the function value for that case.
     * 
     * @param index
     *            Position of the piece whose value is being requested in the
     *            list of pieces. The first piece is numbered 1; the otherwise
     *            child (if present) is not counted, regardless of its
     *            position.
     * @return The MathMLContentElement representing the value to be taken by
     *         the function in the indexth case.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of pieces in this element.
     */
    MathMLContentElement getCaseValue(int index) throws DOMException;

    /**
     * A convenience method to set the function value for the indexth piece in
     * this element.
     * 
     * @param index
     *            Position of the piece whose value is being set in the list
     *            of pieces. The first piece is numbered 1; the otherwise
     *            child (if present) is not counted, regardless of its
     *            position.
     * @param value
     *            A MathMLContentElement representing the function value to be
     *            assigned in the indexth case.
     * @return The MathMLContentElement representing the value to be taken by
     *         the function in the indexth case.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of pieces in this element.
     */
    MathMLContentElement setCaseValue(int index, MathMLContentElement value)
            throws DOMException;

    /**
     * A convenience method to retrieve the child of the piece at the position
     * referenced by index which gives the condition for this case.
     * 
     * @param index
     *            Position of the piece whose condition is being requested in
     *            the list of pieces. The first piece is numbered 1; the
     *            otherwise child (if present) is not counted, regardless of
     *            its position.
     * @return The MathMLContentElement representing the condition to be
     *         satisfied for the indexth case.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of pieces in this element.
     */
    MathMLContentElement getCaseCondition(int index) throws DOMException;

    /**
     * A convenience method to set the condition for the indexth piece in this
     * element.
     * 
     * @param index
     *            Position of the piece whose condition is being set in the
     *            list of pieces. The first piece is numbered 1; the otherwise
     *            child (if present) is not counted, regardless of its
     *            position.
     * @param condition
     *            A MathMLContentElement representing the condition to be
     *            associated to the indexth case.
     * @return The MathMLContentElement which is inserted as the condition
     *         child of the indexth piece.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of pieces in this element.
     */
    MathMLContentElement setCaseCondition(int index,
            MathMLContentElement condition) throws DOMException;
};
