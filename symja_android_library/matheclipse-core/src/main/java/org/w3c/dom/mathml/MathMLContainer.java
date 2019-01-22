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
 * This is an abstract interface containing functionality required by MathML
 * elements that may contain arbitrarily many child elements. No elements are
 * directly supported by this interface; all instances are instances of either
 * MathMLPresentationContainer, MathMLContentContainer, or MathMLMathElement.
 * 
 * 
 */
public interface MathMLContainer {
    /**
     * The number of child elements of this element which represent arguments
     * of the element, as opposed to qualifiers or declare elements. Thus for
     * a MathMLContentContainer it does not contain elements representing
     * bound variables, conditions, separators, degrees, or upper or lower
     * limits (bvar, condition, sep, degree, lowlimit, or uplimit).
     * 
     * @return value of the nArguments attribute.
     */
    int getNArguments();

    /**
     * This attribute accesses the child MathMLElements of this element which
     * are arguments of it, as a MathMLNodeList. Note that this list does not
     * contain any MathMLElements representing qualifier elements or declare
     * elements.
     * 
     * @return value of the arguments attribute.
     */
    MathMLNodeList getArguments();

    /**
     * Provides access to the declare elements which are children of this
     * element, in a MathMLNodeList. All Nodes in this list must be
     * MathMLDeclareElements.
     * 
     * @return value of the declarations attribute.
     */
    MathMLNodeList getDeclarations();

    /**
     * This method returns the indexth child argument element of this element.
     * This frequently differs from the value of
     * Node::childNodes().item(index), as qualifier elements and declare
     * elements are not counted.
     * 
     * @param index
     *            The one-based index of the argument to be retrieved.
     * @return A MathMLElement representing the index-th argument of this
     *         element.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of child elements.
     */
    MathMLElement getArgument(int index) throws DOMException;

    /**
     * This method sets newArgument as the index-th argument of this element.
     * If there is currently an index-th argument, it is replaced by
     * newArgument. This frequently differs from setting the node at
     * Node::childNodes().item(index), as qualifier elements and declare
     * elements are not counted.
     * 
     * @param newArgument
     *            A MathMLElement representing the element that is to be set
     *            as the index-th argument of this element.
     * @param index
     *            The index of the argument that is to be set to newArgument.
     *            The first argument is numbered 1. If index is one more than
     *            the current number of arguments, a new argument is appended.
     * @return The MathMLElement child of this element that represents the new
     *         argument in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit a child element of the type of newArgument, if this
     *             is a MathMLContentContainer and newArgument is a qualifier
     *             element, or if newElement is a MathMLDeclareElement.
     *             INDEX_SIZE_ERR: Raised if index is greater than one more
     *             than the number of child elements.
     */
    MathMLElement setArgument(MathMLElement newArgument, int index)
            throws DOMException;

    /**
     * This method inserts newArgument before the current index-th argument of
     * this element. If index is 0, or if index is one more than the current
     * number of arguments, newArgument is appended as the last argument. This
     * frequently differs from setting the node at
     * Node::childNodes().item(index), as qualifier elements and declare
     * elements are not counted.
     * 
     * @param newArgument
     *            A MathMLElement representing the element that is to be
     *            inserted as a child argument of this element.
     * @param index
     *            The one-based index of the position before which newArgument
     *            is to be inserted. The first argument is numbered 1.
     * @return The MathMLElement child of this element that represents the new
     *         argument in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit a child argument of the type of newArgument, or, for
     *             MathMLContentContainers, if newArgument represents a
     *             qualifier element. INDEX_SIZE_ERR: Raised if index is
     *             greater than one more than the number of child arguments.
     */
    MathMLElement insertArgument(MathMLElement newArgument, int index)
            throws DOMException;

    /**
     * This method deletes the index-th child element that is an argument of
     * this element. Note that child elements which are qualifier elements or
     * declare elements are not counted in determining the index-th argument.
     * 
     * @param index
     *            The one-based index of the argument to be deleted.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of child elements.
     */
    void deleteArgument(int index) throws DOMException;

    /**
     * This method deletes the index-th child element that is an argument of
     * this element, and returns it to the caller. Note that child elements
     * that are qualifier elements or declare elements are not counted in
     * determining the index-th argument.
     * 
     * @param index
     *            The one-based index of the argument to be removed.
     * @return A MathMLElement representing the argument being removed.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of child elements.
     */
    MathMLElement removeArgument(int index) throws DOMException;

    /**
     * This method retrieves the index-th child declare element of this
     * element.
     * 
     * @param index
     *            A one-based index into the list of child declare elements of
     *            this element giving the position of the declare element to
     *            be retrieved.
     * @return The MathMLDeclareElement representing the index-th child
     *         declare.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of child declare elements.
     */
    MathMLDeclareElement getDeclaration(int index) throws DOMException;

    /**
     * This method inserts newDeclaration as the index-th child declaration of
     * this element. If there is already an index-th declare child element, it
     * is replaced by newDeclaration.
     * 
     * @param newDeclaration
     *            A MathMLDeclareElement to be inserted as the index-th child
     *            declare element.
     * @param index
     *            A one-based index into the list of child declare elements of
     *            this element giving the position into which newDeclaration
     *            is to be inserted. If index is one more than the number of
     *            declare children of this element, newDeclaration is appended
     *            as the last declare child.
     * @return The MathMLDeclareElement being inserted.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than one more
     *             than the number of child declare elements.
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit child declare elements.
     */
    MathMLDeclareElement setDeclaration(MathMLDeclareElement newDeclaration,
            int index) throws DOMException;

    /**
     * This method inserts newDeclaration before the current index-th child
     * declare element of this element. If index is 0, newDeclaration is
     * appended as the last child declare element.
     * 
     * @param newDeclaration
     *            A MathMLDeclareElement to be inserted as the index-th child
     *            declare element.
     * @param index
     *            A one-based index into the list of child declare elements of
     *            this element giving the position before which newDeclaration
     *            is to be inserted. If index is 0 or if it is one more than
     *            the number of child declare children, newDeclaration is
     *            appended as the last child declare element.
     * @return The MathMLDeclareElement child of this element representing
     *         newDeclaration in the DOM.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than one more
     *             than the number of child declare elements.
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit child declare elements.
     */
    MathMLDeclareElement insertDeclaration(
            MathMLDeclareElement newDeclaration, int index)
            throws DOMException;

    /**
     * This method removes the MathMLDeclareElement representing the index-th
     * declare child element of this element, and returns it to the caller.
     * Note that index is the position in the list of declare element
     * children, as opposed to the position in the list of all child Nodes.
     * 
     * @param index
     *            The one-based index of the declare element to be removed.
     * @return The MathMLDeclareElement being removed as a child Node of this
     *         element.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of child declare elements.
     */
    MathMLDeclareElement removeDeclaration(int index) throws DOMException;

    /**
     * This method deletes the MathMLDeclareElement representing the index-th
     * declare child element of this element. Note that index is the position
     * in the list of declare element children, as opposed to the position in
     * the list of all child Nodes.
     * 
     * @param index
     *            The one-based index of the declare element to be removed.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if index is greater than the number
     *             of child declare elements.
     */
    void deleteDeclaration(int index) throws DOMException;
};
