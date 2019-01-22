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
 * This interface supports the MathML Content elements that may contain child
 * Content elements. The elements directly supported by MathMLContentContainer
 * include: reln (deprecated), lambda, lowlimit, uplimit, degree,
 * domainofapplication, and momentabout. Interfaces derived from
 * MathMLContentContainer support the elements apply, fn, interval, condition,
 * declare, bvar, set, list, vector, matrix, and matrixrow.
 * 
 * 
 */
public interface MathMLContentContainer extends MathMLContentElement,
        MathMLContainer {
    /**
     * The number of bvar child elements of this element.
     * 
     * @return value of the nBoundVariables attribute.
     */
    int getNBoundVariables();

    /**
     * This attribute represents the condition child element of this node. See .
     * 
     * @return value of the condition attribute.
     */
    MathMLConditionElement getCondition();

    /**
     * setter for the condition attribute.
     * 
     * @param condition
     *            new value for condition.
     * @see #getCondition()
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit a child condition element. In particular, raised if
     *             this element is not a apply, set, or list.
     */
    void setCondition(MathMLConditionElement condition);

    /**
     * This attribute represents the degree child element of this node. This
     * expresses, for instance, the degree of differentiation if this element
     * is a bvar child of an apply element whose first child is a diff or
     * partialdiff. If this is an apply element whose first child is a
     * partialdiff, the opDegree attribute, if present, represents the total
     * degree of differentiation. See .
     * 
     * @return value of the opDegree attribute.
     */
    MathMLElement getOpDegree();

    /**
     * setter for the opDegree attribute.
     * 
     * @param opDegree
     *            new value for opDegree.
     * @see #getOpDegree()
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit a child degree element. In particular, raised if
     *             this element is not a bvar or apply.
     */
    void setOpDegree(MathMLElement opDegree);

    /**
     * This attribute represents the domainofapplication child element of this
     * node, if present. This may express, for instance, the domain of
     * integration if this element is an apply element whose first child is an
     * integral operator (int). See .
     * 
     * @return value of the domainOfApplication attribute.
     */
    MathMLElement getDomainOfApplication();

    /**
     * setter for the domainOfApplication attribute.
     * 
     * @param domainOfApplication
     *            new value for domainOfApplication.
     * @see #getDomainOfApplication()
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit a child domainofapplication element.
     */
    void setDomainOfApplication(MathMLElement domainOfApplication);

    /**
     * This attribute represents the momentabout child element of this node,
     * if present. This typically expresses the point about which a
     * statistical moment is to be calculated, if this element is an apply
     * element whose first child is a moment. See .
     * 
     * @return value of the momentAbout attribute.
     */
    MathMLElement getMomentAbout();

    /**
     * setter for the momentAbout attribute.
     * 
     * @param momentAbout
     *            new value for momentAbout.
     * @see #getMomentAbout()
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit a child momentabout element. In particular, raised
     *             if this element is not an apply whose first child is a
     *             moment.
     */
    void setMomentAbout(MathMLElement momentAbout);

    /**
     * This method retrieves the index-th MathMLBvarElement child of the
     * MathMLElement. Note that only bvar child elements are counted in
     * determining the index-th bound variable.
     * 
     * @param index
     *            The one-based index into the bound variable children of this
     *            element of the MathMLBvarElement to be retrieved.
     * @return The MathMLBvarElement representing the index-th bvar child of
     *         this element.
     */
    MathMLBvarElement getBoundVariable(int index);

    /**
     * This method inserts a MathMLBvarElement as a child node before the
     * current index-th bound variable child of this MathMLElement. If index
     * is 0, newBVar is appended as the last bound variable child. This has
     * the effect of adding a bound variable to the expression this element
     * represents. Note that the new bound variable is inserted as the
     * index-th bvar child node, not necessarily as the index-th child node.
     * The point of the method is to allow insertion of bound variables
     * without requiring the caller to calculate the exact order of child
     * qualifier elements.
     * 
     * @param newBVar
     *            A MathMLBvarElement representing the bvar element being
     *            added.
     * @param index
     *            The one-based index into the bound variable children of this
     *            element before which newBVar is to be inserted.
     * @return The MathMLBvarElement being added.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit child bvar elements.
     */
    MathMLBvarElement insertBoundVariable(MathMLBvarElement newBVar, int index)
            throws DOMException;

    /**
     * This method sets the index-th bound variable child of this
     * MathMLElement to newBVar. This has the effect of setting a bound
     * variable in the expression this element represents. Note that the new
     * bound variable is inserted as the index-th bvar child node, not
     * necessarily as the index-th child node. The point of the method is to
     * allow insertion of bound variables without requiring the caller to
     * calculate the exact order of child qualifier elements. If there is
     * already a bvar at the index-th position, it is replaced by newBVar.
     * 
     * @param newBVar
     *            The new MathMLBvarElement child of this element being set.
     * @param index
     *            The one-based index into the bound variable children of this
     *            element at which newBVar is to be inserted.
     * @return The MathMLBvarElement being set.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if this element does not
     *             permit child bvar elements.
     */
    MathMLBvarElement setBoundVariable(MathMLBvarElement newBVar, int index)
            throws DOMException;

    /**
     * This method deletes the index-th MathMLBvarElement child of the
     * MathMLElement. This has the effect of removing this bound variable from
     * the list of qualifiers affecting the element this represents.
     * 
     * @param index
     *            The one-based index into the bound variable children of this
     *            element of the MathMLBvarElement to be removed.
     */
    void deleteBoundVariable(int index);

    /**
     * This method removes the index-th MathMLBvarElement child of the
     * MathMLElement and returns it to the caller. This has the effect of
     * removing this bound variable from the list of qualifiers affecting the
     * element this represents.
     * 
     * @param index
     *            The one-based index into the bound variable children of this
     *            element of the MathMLBvarElement to be removed.
     * @return The MathMLBvarElement being removed.
     */
    MathMLBvarElement removeBoundVariable(int index);
};
