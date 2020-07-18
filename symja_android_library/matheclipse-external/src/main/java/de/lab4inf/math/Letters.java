/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2010,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package de.lab4inf.math;

/**
 * Definition of some greek letters and mathematical symbols based on unicode.
 *
 * @author nwulff
 * @version $Id: Letters.java,v 1.3 2012/01/19 10:30:57 nwulff Exp $
 * @since 13.09.2010
 */
public interface Letters {
    /**
     * the greek alpha character as unicode.
     */
    char ALPHA = '\u03B1';
    /**
     * the greek beta character as unicode.
     */
    char BETA = '\u03B2';
    /**
     * the greek gamma character as unicode.
     */
    char GAMMA = '\u03B3';
    /**
     * the greek upper gamma character as unicode.
     */
    char UPPER_GAMMA = '\u0393';
    /**
     * the greek delta character as unicode.
     */
    char DELTA = '\u03B4';
    /**
     * the greek upper delta character as unicode.
     */
    char UPPER_DELTA = '\u0394';
    /**
     * the greek epsilon character as unicode.
     */
    char EPSILON = '\u03B5';
    /**
     * the greek zeta character as unicode.
     */
    char ZETA = '\u03B6';
    /**
     * the greek eta character as unicode.
     */
    char ETA = '\u03B7';
    /**
     * the greek theta character as unicode.
     */
    char THETA = '\u03B8';
    /**
     * the greek upper theta character as unicode.
     */
    char UPPER_THETA = '\u0398';
    /**
     * the greek var theta character as unicode.
     */
    char VAR_THETA = '\u03D1';
    /**
     * the greek iota character as unicode.
     */
    char IOTA = '\u03B9';
    /**
     * the greek kappa character as unicode.
     */
    char KAPPA = '\u03F0';
    /**
     * the greek var kappa character as unicode.
     */
    char VAR_KAPPA = '\u03BA';
    /**
     * the greek lambda character as unicode.
     */
    char LAMBDA = '\u03BB';
    /**
     * the greek upper lambda character as unicode.
     */
    char UPPER_LAMBDA = '\u039B';
    /**
     * the greek mu character as unicode.
     */
    char MU = '\u03BC';
    /**
     * the greek nu character as unicode.
     */
    char NU = '\u03BD';
    /**
     * the greek xi character as unicode.
     */
    char XI = '\u03BE';
    /**
     * the greek upper xi character as unicode.
     */
    char UPPER_XI = '\u039E';
    /**
     * the greek pi character as unicode.
     */
    char PI = '\u03C0';
    /**
     * the greek upper pi character as unicode.
     */
    char UPPER_PI = '\u03A0';
    /**
     * the greek rho character as unicode.
     */
    char RHO = '\u03C1';
    /**
     * the greek var sigma (stigma) character as unicode.
     */
    char VAR_SIGMA = '\u03C2';
    /**
     * the greek sigma character as unicode.
     */
    char SIGMA = '\u03C3';
    /**
     * the greek upper sigma character as unicode.
     */
    char UPPER_SIGMA = '\u03A3';
    /**
     * the greek tau character as unicode.
     */
    char TAU = '\u03C4';
    /**
     * the greek upsilon character as unicode.
     */
    char UPSILON = '\u03C5';
    /**
     * the greek phi character as unicode.
     */
    char PHI = '\u03C6';
    /**
     * the greek upper phi character as unicode.
     */
    char UPPER_PHI = '\u03A6';
    /**
     * the greek chi character as unicode.
     */
    char CHI = '\u03C7';
    /**
     * the greek phi character as unicode.
     */
    char PSI = '\u03C8';
    /**
     * the greek upper phi character as unicode.
     */
    char UPPER_PSI = '\u03A8';
    /**
     * the greek phi character as unicode.
     */
    char OMEGA = '\u03C9';
    /**
     * the greek upper phi character as unicode.
     */
    char UPPER_OMEGA = '\u03A9';

    // ======    Math symbols =====================================================
    /**
     * the plus-minus symbol as unicode.
     */
    char PLUS_MINUS = '\u00B1';
    /**
     * the for all abbreviation as unicode.
     */
    char FOR_ALL = '\u2200';
    /**
     * the partial derivative operator as unicode.
     */
    char PARTIAL = '\u2202';
    /**
     * the exits abbreviation as unicode.
     */
    char EXISTS = '\u2203';
    /**
     * the not exits abbreviation as unicode.
     */
    char NOT_EXISTS = '\u2204';
    /**
     * the empty set symbol as unicode.
     */
    char EMPTY = '\u2205';
    /**
     * the nabla operator as unicode.
     */
    char NABLA = '\u2206';
    /**
     * the laplace operator as unicode.
     */
    char LAPLACE = '\u2207';
    /**
     * the element of character as unicode.
     */
    char ELEMENT_OF = '\u2208';
    /**
     * the not element of character as unicode.
     */
    char NOT_ELEMENT_OF = '\u2209';
    /**
     * the summation character (upper sigma) as unicode.
     */
    char SUMMATION = '\u2211';
    /**
     * the product character (upper sigma) as unicode.
     */
    char PRODUCT = '\u220F';
    /**
     * the integral symbol as unicode.
     */
    char INTEGRAL = '\u222B';
    /**
     * the proportional symbol as unicode.
     */
    char PROPORTIONAL = '\u221D';
    /**
     * the infinity symbol as unicode.
     */
    char INFINITY = '\u221E';
    /**
     * chi**2 with upper square sign
     */
    String CHI2 = CHI + "\u00B2";

    /**
     * introduced dummy method because of checkstyle.
     *
     * @return string as specified for java objects
     **/
    String toString();
}
 