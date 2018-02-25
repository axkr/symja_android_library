/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2012,  Prof. Dr. Nikolaus Wulff
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for a Lab4Math service to be located
 * by a ServiceLoader like the L4MLoader utility.
 * <p>
 * An interface T annotated by @Service can be located via:
 * <pre>
 *
 *   T anInstance = L4MLoader.load(T.class);
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: Service.java,v 1.3 2013/02/26 16:57:44 nwulff Exp $
 * @since 08.01.2012
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String value() default "";
}
 