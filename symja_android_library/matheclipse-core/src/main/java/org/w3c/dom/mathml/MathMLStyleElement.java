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
 * This interface extends the MathMLElement interface for the MathML style
 * element mstyle. While the mstyle element may contain any attributes
 * allowable on any MathML presentation element, only attributes specific to
 * the mstyle element are included in the interface below. Other attributes
 * should be accessed using the methods on the base Element class,
 * particularly the Element::getAttribute and Element::setAttribute methods,
 * or even the Node::attributes attribute to access all of them at once. Not
 * only does this obviate a lengthy list below, but it seems likely that most
 * implementations will find this a considerably more useful interface to a
 * MathMLStyleElement.
 * 
 * 
 */
public interface MathMLStyleElement extends MathMLPresentationContainer {
    /**
     * A string of the form +/- unsigned integer; represents the scriptlevel
     * attribute for the mstyle element, if specified. See also the discussion
     * of this attribute.
     * 
     * @return value of the scriptlevel attribute.
     */
    String getScriptlevel();

    /**
     * setter for the scriptlevel attribute.
     * 
     * @param scriptlevel
     *            new value for scriptlevel.
     * @see #getScriptlevel()
     */
    void setScriptlevel(String scriptlevel);

    /**
     * Either true or false; a string representing the displaystyle attribute
     * for the mstyle element, if specified. See also the discussion of this
     * attribute.
     * 
     * @return value of the displaystyle attribute.
     */
    String getDisplaystyle();

    /**
     * setter for the displaystyle attribute.
     * 
     * @param displaystyle
     *            new value for displaystyle.
     * @see #getDisplaystyle()
     */
    void setDisplaystyle(String displaystyle);

    /**
     * A string of the form number; represents the scriptsizemultiplier
     * attribute for the mstyle element, if specified. See also the discussion
     * of this attribute.
     * 
     * @return value of the scriptsizemultiplier attribute.
     */
    String getScriptsizemultiplier();

    /**
     * setter for the scriptsizemultiplier attribute.
     * 
     * @param scriptsizemultiplier
     *            new value for scriptsizemultiplier.
     * @see #getScriptsizemultiplier()
     */
    void setScriptsizemultiplier(String scriptsizemultiplier);

    /**
     * A string of the form number v-unit; represents the scriptminsize
     * attribute for the mstyle element, if specified. See also the discussion
     * of this attribute.
     * 
     * @return value of the scriptminsize attribute.
     */
    String getScriptminsize();

    /**
     * setter for the scriptminsize attribute.
     * 
     * @param scriptminsize
     *            new value for scriptminsize.
     * @see #getScriptminsize()
     */
    void setScriptminsize(String scriptminsize);

    /**
     * A string representation of a color or the string transparent;
     * represents the background attribute for the mstyle element, if
     * specified. See also the discussion of this attribute.
     * 
     * @return value of the background attribute.
     */
    String getBackground();

    /**
     * setter for the background attribute.
     * 
     * @param background
     *            new value for background.
     * @see #getBackground()
     */
    void setBackground(String background);

    /**
     * A string of the form number h-unit; represents the
     * veryverythinmathspace attribute for the mstyle element, if specified.
     * See also the discussion of this attribute.
     * 
     * @return value of the veryverythinmathspace attribute.
     */
    String getVeryverythinmathspace();

    /**
     * setter for the veryverythinmathspace attribute.
     * 
     * @param veryverythinmathspace
     *            new value for veryverythinmathspace.
     * @see #getVeryverythinmathspace()
     */
    void setVeryverythinmathspace(String veryverythinmathspace);

    /**
     * A string of the form number h-unit; represents the verythinmathspace
     * attribute for the mstyle element, if specified. See also the discussion
     * of this attribute.
     * 
     * @return value of the verythinmathspace attribute.
     */
    String getVerythinmathspace();

    /**
     * setter for the verythinmathspace attribute.
     * 
     * @param verythinmathspace
     *            new value for verythinmathspace.
     * @see #getVerythinmathspace()
     */
    void setVerythinmathspace(String verythinmathspace);

    /**
     * A string of the form number h-unit; represents the thinmathspace
     * attribute for the mstyle element, if specified. See also the discussion
     * of this attribute.
     * 
     * @return value of the thinmathspace attribute.
     */
    String getThinmathspace();

    /**
     * setter for the thinmathspace attribute.
     * 
     * @param thinmathspace
     *            new value for thinmathspace.
     * @see #getThinmathspace()
     */
    void setThinmathspace(String thinmathspace);

    /**
     * A string of the form number h-unit; represents the mediummathspace
     * attribute for the mstyle element, if specified. See also the discussion
     * of this attribute.
     * 
     * @return value of the mediummathspace attribute.
     */
    String getMediummathspace();

    /**
     * setter for the mediummathspace attribute.
     * 
     * @param mediummathspace
     *            new value for mediummathspace.
     * @see #getMediummathspace()
     */
    void setMediummathspace(String mediummathspace);

    /**
     * A string of the form number h-unit; represents the thickmathspace
     * attribute for the mstyle element, if specified. See also the discussion
     * of this attribute.
     * 
     * @return value of the thickmathspace attribute.
     */
    String getThickmathspace();

    /**
     * setter for the thickmathspace attribute.
     * 
     * @param thickmathspace
     *            new value for thickmathspace.
     * @see #getThickmathspace()
     */
    void setThickmathspace(String thickmathspace);

    /**
     * A string of the form number h-unit; represents the verythickmathspace
     * attribute for the mstyle element, if specified. See also the discussion
     * of this attribute.
     * 
     * @return value of the verythickmathspace attribute.
     */
    String getVerythickmathspace();

    /**
     * setter for the verythickmathspace attribute.
     * 
     * @param verythickmathspace
     *            new value for verythickmathspace.
     * @see #getVerythickmathspace()
     */
    void setVerythickmathspace(String verythickmathspace);

    /**
     * A string of the form number h-unit; represents the
     * veryverythickmathspace attribute for the mstyle element, if specified.
     * See also the discussion of this attribute.
     * 
     * @return value of the veryverythickmathspace attribute.
     */
    String getVeryverythickmathspace();

    /**
     * setter for the veryverythickmathspace attribute.
     * 
     * @param veryverythickmathspace
     *            new value for veryverythickmathspace.
     * @see #getVeryverythickmathspace()
     */
    void setVeryverythickmathspace(String veryverythickmathspace);

    /**
     * A string of the form number h-unit; represents the
     * negativeveryverythinmathspace attribute for the mstyle element, if
     * specified. See also the discussion of this attribute.
     * 
     * @return value of the negativeveryverythinmathspace attribute.
     */
    String getNegativeveryverythinmathspace();

    /**
     * setter for the negativeveryverythinmathspace attribute.
     * 
     * @param negativeveryverythinmathspace
     *            new value for negativeveryverythinmathspace.
     * @see #getNegativeveryverythinmathspace()
     */
    void setNegativeveryverythinmathspace(String negativeveryverythinmathspace);

    /**
     * A string of the form number h-unit; represents the
     * negativeverythinmathspace attribute for the mstyle element, if
     * specified. See also the discussion of this attribute.
     * 
     * @return value of the negativeverythinmathspace attribute.
     */
    String getNegativeverythinmathspace();

    /**
     * setter for the negativeverythinmathspace attribute.
     * 
     * @param negativeverythinmathspace
     *            new value for negativeverythinmathspace.
     * @see #getNegativeverythinmathspace()
     */
    void setNegativeverythinmathspace(String negativeverythinmathspace);

    /**
     * A string of the form number h-unit; represents the
     * negativethinmathspace attribute for the mstyle element, if specified.
     * See also the discussion of this attribute.
     * 
     * @return value of the negativethinmathspace attribute.
     */
    String getNegativethinmathspace();

    /**
     * setter for the negativethinmathspace attribute.
     * 
     * @param negativethinmathspace
     *            new value for negativethinmathspace.
     * @see #getNegativethinmathspace()
     */
    void setNegativethinmathspace(String negativethinmathspace);

    /**
     * A string of the form number h-unit; represents the
     * negativemediummathspace attribute for the mstyle element, if specified.
     * See also the discussion of this attribute.
     * 
     * @return value of the negativemediummathspace attribute.
     */
    String getNegativemediummathspace();

    /**
     * setter for the negativemediummathspace attribute.
     * 
     * @param negativemediummathspace
     *            new value for negativemediummathspace.
     * @see #getNegativemediummathspace()
     */
    void setNegativemediummathspace(String negativemediummathspace);

    /**
     * A string of the form number h-unit; represents the
     * negativethickmathspace attribute for the mstyle element, if specified.
     * See also the discussion of this attribute.
     * 
     * @return value of the negativethickmathspace attribute.
     */
    String getNegativethickmathspace();

    /**
     * setter for the negativethickmathspace attribute.
     * 
     * @param negativethickmathspace
     *            new value for negativethickmathspace.
     * @see #getNegativethickmathspace()
     */
    void setNegativethickmathspace(String negativethickmathspace);

    /**
     * A string of the form number h-unit; represents the
     * negativeverythickmathspace attribute for the mstyle element, if
     * specified. See also the discussion of this attribute.
     * 
     * @return value of the negativeverythickmathspace attribute.
     */
    String getNegativeverythickmathspace();

    /**
     * setter for the negativeverythickmathspace attribute.
     * 
     * @param negativeverythickmathspace
     *            new value for negativeverythickmathspace.
     * @see #getNegativeverythickmathspace()
     */
    void setNegativeverythickmathspace(String negativeverythickmathspace);

    /**
     * A string of the form number h-unit; represents the
     * negativeveryverythickmathspace attribute for the mstyle element, if
     * specified. See also the discussion of this attribute.
     * 
     * @return value of the negativeveryverythickmathspace attribute.
     */
    String getNegativeveryverythickmathspace();

    /**
     * setter for the negativeveryverythickmathspace attribute.
     * 
     * @param negativeveryverythickmathspace
     *            new value for negativeveryverythickmathspace.
     * @see #getNegativeveryverythickmathspace()
     */
    void setNegativeveryverythickmathspace(
            String negativeveryverythickmathspace);
};
