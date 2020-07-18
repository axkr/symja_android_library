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
 * This interface extends the MathMLPresentationToken interface for the MathML
 * operator element mo.
 * 
 * 
 */
public interface MathMLOperatorElement extends MathMLPresentationToken {
    /**
     * The form attribute (prefix, infix or postfix) for the mo element, if
     * specified.
     * 
     * @return value of the form attribute.
     */
    String getForm();

    /**
     * setter for the form attribute.
     * 
     * @param form
     *            new value for form.
     * @see #getForm()
     */
    void setForm(String form);

    /**
     * The fence attribute (true or false) for the mo element, if specified.
     * 
     * @return value of the fence attribute.
     */
    String getFence();

    /**
     * setter for the fence attribute.
     * 
     * @param fence
     *            new value for fence.
     * @see #getFence()
     */
    void setFence(String fence);

    /**
     * The separator attribute (true or false) for the mo element, if
     * specified.
     * 
     * @return value of the separator attribute.
     */
    String getSeparator();

    /**
     * setter for the separator attribute.
     * 
     * @param separator
     *            new value for separator.
     * @see #getSeparator()
     */
    void setSeparator(String separator);

    /**
     * The lspace attribute (spacing to left) of the mo element, if specified.
     * 
     * @return value of the lspace attribute.
     */
    String getLspace();

    /**
     * setter for the lspace attribute.
     * 
     * @param lspace
     *            new value for lspace.
     * @see #getLspace()
     */
    void setLspace(String lspace);

    /**
     * The rspace attribute (spacing to right) of the mo element, if
     * specified.
     * 
     * @return value of the rspace attribute.
     */
    String getRspace();

    /**
     * setter for the rspace attribute.
     * 
     * @param rspace
     *            new value for rspace.
     * @see #getRspace()
     */
    void setRspace(String rspace);

    /**
     * The stretchy attribute (true or false) for the mo element, if
     * specified.
     * 
     * @return value of the stretchy attribute.
     */
    String getStretchy();

    /**
     * setter for the stretchy attribute.
     * 
     * @param stretchy
     *            new value for stretchy.
     * @see #getStretchy()
     */
    void setStretchy(String stretchy);

    /**
     * The symmetric attribute (true or false) for the mo element, if
     * specified.
     * 
     * @return value of the symmetric attribute.
     */
    String getSymmetric();

    /**
     * setter for the symmetric attribute.
     * 
     * @param symmetric
     *            new value for symmetric.
     * @see #getSymmetric()
     */
    void setSymmetric(String symmetric);

    /**
     * The maxsize attribute for the mo element, if specified.
     * 
     * @return value of the maxsize attribute.
     */
    String getMaxsize();

    /**
     * setter for the maxsize attribute.
     * 
     * @param maxsize
     *            new value for maxsize.
     * @see #getMaxsize()
     */
    void setMaxsize(String maxsize);

    /**
     * The minsize attribute for the mo element, if specified.
     * 
     * @return value of the minsize attribute.
     */
    String getMinsize();

    /**
     * setter for the minsize attribute.
     * 
     * @param minsize
     *            new value for minsize.
     * @see #getMinsize()
     */
    void setMinsize(String minsize);

    /**
     * The largeop attribute for the mo element, if specified.
     * 
     * @return value of the largeop attribute.
     */
    String getLargeop();

    /**
     * setter for the largeop attribute.
     * 
     * @param largeop
     *            new value for largeop.
     * @see #getLargeop()
     */
    void setLargeop(String largeop);

    /**
     * The movablelimits (true or false) attribute for the mo element, if
     * specified.
     * 
     * @return value of the movablelimits attribute.
     */
    String getMovablelimits();

    /**
     * setter for the movablelimits attribute.
     * 
     * @param movablelimits
     *            new value for movablelimits.
     * @see #getMovablelimits()
     */
    void setMovablelimits(String movablelimits);

    /**
     * The accent attribute (true or false) for the mo element, if specified.
     * 
     * @return value of the accent attribute.
     */
    String getAccent();

    /**
     * setter for the accent attribute.
     * 
     * @param accent
     *            new value for accent.
     * @see #getAccent()
     */
    void setAccent(String accent);
};
