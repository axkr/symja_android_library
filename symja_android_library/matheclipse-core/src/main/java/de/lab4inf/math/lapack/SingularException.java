/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.lapack;

/**
 * Internal exception to throw for a singular matrix.
 *
 * @author nwulff
 * @version $Id: SingularException.java,v 1.1 2014/02/11 20:25:04 nwulff Exp $
 * @since 31.03.2008
 */

/**
 * Internal exception to throw for a singular matrix.
 */
class SingularException extends IllegalArgumentException {
    /** reference to the serialVersionUID attribute.  */
    private static final long serialVersionUID = -7781732438847981681L;

    public SingularException(final String msg) {
        super(msg);
    }
}
 