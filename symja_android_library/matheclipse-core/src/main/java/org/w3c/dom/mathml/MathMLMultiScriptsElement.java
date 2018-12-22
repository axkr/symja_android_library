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
 * This interface extends the MathMLPresentationElement interface for the
 * MathML multiscripts (including prescripts or tensors) element
 * mmultiscripts.
 * 
 * 
 */
public interface MathMLMultiScriptsElement extends MathMLPresentationElement {
    /**
     * A string representing the minimum amount to shift the baseline of the
     * subscripts down, if specified; this is the element's subscriptshift
     * attribute.
     * 
     * @return value of the subscriptshift attribute.
     */
    String getSubscriptshift();

    /**
     * setter for the subscriptshift attribute.
     * 
     * @param subscriptshift
     *            new value for subscriptshift.
     * @see #getSubscriptshift()
     */
    void setSubscriptshift(String subscriptshift);

    /**
     * A string representing the minimum amount to shift the baseline of the
     * superscripts up, if specified; this is the element's superscriptshift
     * attribute.
     * 
     * @return value of the superscriptshift attribute.
     */
    String getSuperscriptshift();

    /**
     * setter for the superscriptshift attribute.
     * 
     * @param superscriptshift
     *            new value for superscriptshift.
     * @see #getSuperscriptshift()
     */
    void setSuperscriptshift(String superscriptshift);

    /**
     * A MathMLElement representing the base of the script. This is the first
     * child of the element.
     * 
     * @return value of the base attribute.
     */
    MathMLElement getBase();

    /**
     * setter for the base attribute.
     * 
     * @param base
     *            new value for base.
     * @see #getBase()
     */
    void setBase(MathMLElement base);

    /**
     * A NodeList representing the prescripts of the script, which appear in
     * the order described by the expression (prescript presuperscript)*. This
     * is the same as traversing the contents of the NodeList returned by
     * Node::childNodes() from the Node following the mprescripts (if present)
     * to the end of the list.
     * 
     * @return value of the prescripts attribute.
     */
    MathMLNodeList getPrescripts();

    /**
     * A MathMLNodeList representing the scripts of the script, which appear
     * in the order described by the expression (script superscript)*. This is
     * the same as traversing the contents of the NodeList returned by
     * Node::childNodes() from the first Node up to and including the Node
     * preceding the mprescripts (if present).
     * 
     * @return value of the scripts attribute.
     */
    MathMLNodeList getScripts();

    /**
     * The number of script/subscript columns preceding (to the left of) the
     * base. Should always be half of getprescripts().length()
     * 
     * @return value of the numprescriptcolumns attribute.
     */
    int getNumprescriptcolumns();

    /**
     * The number of script/subscript columns following (to the right of) the
     * base. Should always be half of getscripts().length()
     * 
     * @return value of the numscriptcolumns attribute.
     */
    int getNumscriptcolumns();

    /**
     * A convenience method to retrieve pre-subscript children of the element,
     * referenced by column index .
     * 
     * @param colIndex
     *            Column index of prescript (where 1 represents the leftmost
     *            prescript column).
     * @return Returns the MathMLElement representing the colIndex-th
     *         presubscript (to the left of the base, counting from 1 at the
     *         far left). Note that this may be the MathMLElement
     *         corresponding to the special element none in the case of a
     *         missing presubscript (see the discussion of mmultiscripts), or
     *         it may be null if colIndex is out of range for the element.
     */
    MathMLElement getPreSubScript(int colIndex);

    /**
     * A convenience method to retrieve subscript children of the element,
     * referenced by column index.
     * 
     * @param colIndex
     *            Column index of script (where 1 represents the leftmost
     *            script column, the first to the right of the base).
     * @return Returns the MathMLElement representing the colIndex-th
     *         subscript to the right of the base. Note that this may be the
     *         MathMLElement corresponding to the special element none in the
     *         case of a missing subscript (see the discussion of
     *         mmultiscripts), or it may be null if colIndex is out of range
     *         for the element.
     */
    MathMLElement getSubScript(int colIndex);

    /**
     * A convenience method to retrieve pre-superscript children of the
     * element, referenced by column index .
     * 
     * @param colIndex
     *            Column index of pre-superscript (where 1 represents the
     *            leftmost prescript column).
     * @return Returns the MathMLElement representing the colIndex-th
     *         presuperscript (to the left of the base, counting from 1 at the
     *         far left). Note that this may be the MathMLElement
     *         corresponding to the special element none in the case of a
     *         missing presuperscript (see the discussion of mmultiscripts),
     *         or it may be null if colIndex is out of range for the element.
     */
    MathMLElement getPreSuperScript(int colIndex);

    /**
     * A convenience method to retrieve superscript children of the element,
     * referenced by column index .
     * 
     * @param colIndex
     *            Column index of script (where 1 represents the leftmost
     *            script column, the first to the right of the base)
     * @return Returns the MathMLElement representing the colIndex-th
     *         superscript to the right of the base. Note that this may be the
     *         MathMLElement corresponding to the special element none in the
     *         case of a missing superscript (see the discussion of
     *         mmultiscripts), or it may be null if colIndex is out of range
     *         for the element.
     */
    MathMLElement getSuperScript(int colIndex);

    /**
     * A convenience method to insert a pre-subscript before the position
     * referenced by column index. If colIndex is 0, the new pre-subscript is
     * appended as the last pre-subscript of the mmultiscripts element; if
     * colIndex is 1, a new pre-subscript is prepended at the far left. Note
     * that inserting a new pre-subscript will cause the insertion of an empty
     * pre-superscript in the same column.
     * 
     * @param colIndex
     *            Column index of pre-subscript (where 1 represents the
     *            leftmost prescript column).
     * @param newScript
     *            A MathMLElement representing the element to be inserted as a
     *            pre-subscript.
     * @return The MathMLElement child of this MathMLMultiScriptsElement
     *         representing the new script in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newScript represents an
     *             element that cannot be a pre-subscript. INDEX_SIZE_ERR:
     *             Raised if colIndex is greater than the number of
     *             pre-scripts of the element.
     */
    MathMLElement insertPreSubScriptBefore(int colIndex,
            MathMLElement newScript) throws DOMException;

    /**
     * A convenience method to set the pre-subscript child at the position
     * referenced by colIndex. If there is currently a pre-subscript at this
     * position, it is replaced by newScript.
     * 
     * @param colIndex
     *            Column index of pre-subscript (where 1 represents the
     *            leftmost prescript column).
     * @param newScript
     *            MathMLElement representing the element that is to be set as
     *            the colIndex-th pre-subscript child of this element.
     * @return The MathMLElement child of this MathMLMultiScriptsElement
     *         representing the new pre-subscript in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newScript represents an
     *             element that cannot be a pre-subscript. INDEX_SIZE_ERR:
     *             Raised if colIndex is greater than one more than the number
     *             of pre-scripts of the element.
     */
    MathMLElement setPreSubScriptAt(int colIndex, MathMLElement newScript)
            throws DOMException;

    /**
     * A convenience method to insert a subscript before the position
     * referenced by column index. If colIndex is 0, the new subscript is
     * appended as the last subscript of the mmultiscripts element; if
     * colIndex is 1, a new subscript is prepended at the far left. Note that
     * inserting a new subscript will cause the insertion of an empty
     * superscript in the same column.
     * 
     * @param colIndex
     *            Column index of subscript, where 1 represents the leftmost
     *            script column (the first to the right of the base).
     * @param newScript
     *            A MathMLElement representing the element to be inserted as a
     *            subscript.
     * @return The MathMLElement child of this MathMLMultiScriptsElement that
     *         represents the new subscript in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newScript represents an
     *             element that cannot be a subscript. INDEX_SIZE_ERR: Raised
     *             if colIndex is greater than the number of scripts of the
     *             element.
     */
    MathMLElement insertSubScriptBefore(int colIndex, MathMLElement newScript)
            throws DOMException;

    /**
     * A convenience method to set the subscript child at the position
     * referenced by colIndex. If there is currently a subscript at this
     * position, it is replaced by newScript.
     * 
     * @param colIndex
     *            Column index of subscript, where 1 represents the leftmost
     *            script column (the first to the right of the base).
     * @param newScript
     *            MathMLElement representing the element that is to be set as
     *            the colIndex-th subscript child of this element.
     * @return The MathMLElement child of this element representing the new
     *         subscript in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newScript represents an
     *             element that cannot be a subscript. INDEX_SIZE_ERR: Raised
     *             if colIndex is greater than one more than the number of
     *             scripts of the element.
     */
    MathMLElement setSubScriptAt(int colIndex, MathMLElement newScript)
            throws DOMException;

    /**
     * A convenience method to insert a pre-superscript before the position
     * referenced by column index. If colIndex is 0, the new pre-superscript
     * is appended as the last pre-superscript of the mmultiscripts element;
     * if colIndex is 1, a new pre-superscript is prepended at the far left.
     * Note that inserting a new pre-superscript will cause the insertion of
     * an empty pre-subscript in the same column.
     * 
     * @param colIndex
     *            Column index of pre-superscript (where 1 represents the
     *            leftmost prescript column).
     * @param newScript
     *            A MathMLElement representing the element to be inserted as a
     *            pre-superscript.
     * @return The MathMLElement child of this element that represents the new
     *         pre-superscript in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newScript represents an
     *             element that cannot be a pre-superscript. INDEX_SIZE_ERR:
     *             Raised if colIndex is greater than the number of
     *             pre-scripts of the element.
     */
    MathMLElement insertPreSuperScriptBefore(int colIndex,
            MathMLElement newScript) throws DOMException;

    /**
     * A convenience method to set the pre-superscript child at the position
     * referenced by colIndex. If there is currently a pre-superscript at this
     * position, it is replaced by newScript.
     * 
     * @param colIndex
     *            Column index of pre-superscript (where 1 represents the
     *            leftmost prescript column).
     * @param newScript
     *            MathMLElement representing the element that is to be set as
     *            the colIndex-th pre-superscript child of this element.
     * @return The MathMLElement child of this element that represents the new
     *         pre-superscript in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newScript represents an
     *             element that cannot be a pre-superscript. INDEX_SIZE_ERR:
     *             Raised if colIndex is greater than one more than the number
     *             of pre-scripts of the element.
     */
    MathMLElement setPreSuperScriptAt(int colIndex, MathMLElement newScript)
            throws DOMException;

    /**
     * A convenience method to insert a superscript before the position
     * referenced by column index. If colIndex is 0, the new superscript is
     * appended as the last superscript of the mmultiscripts element; if
     * colIndex is 1, a new superscript is prepended at the far left. Note
     * that inserting a new superscript will cause the insertion of an empty
     * subscript in the same column.
     * 
     * @param colIndex
     *            Column index of superscript, where 1 represents the leftmost
     *            script column (the first to the right of the base).
     * @param newScript
     *            A MathMLElement representing the element to be inserted as a
     *            superscript.
     * @return The MathMLElement child of this element that represents the new
     *         superscript in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newScript represents an
     *             element that cannot be a superscript. INDEX_SIZE_ERR:
     *             Raised if colIndex is greater than the number of scripts of
     *             the element.
     */
    MathMLElement insertSuperScriptBefore(int colIndex,
            MathMLElement newScript) throws DOMException;

    /**
     * A convenience method to set the superscript child at the position
     * referenced by colIndex. If there is currently a superscript at this
     * position, it is replaced by newScript.
     * 
     * @param colIndex
     *            Column index of superscript, where 1 represents the leftmost
     *            script column (the first to the right of the base).
     * @param newScript
     *            MathMLElement representing the element that is to be set as
     *            the colIndex-th superscript child of this element.
     * @return The MathMLElement child of this element that represents the new
     *         superscript in the DOM.
     * @throws DOMException
     *             HIERARCHY_REQUEST_ERR: Raised if newScript represents an
     *             element that cannot be a superscript. INDEX_SIZE_ERR:
     *             Raised if colIndex is greater than one more than the number
     *             of scripts of the element.
     */
    MathMLElement setSuperScriptAt(int colIndex, MathMLElement newScript)
            throws DOMException;
};
