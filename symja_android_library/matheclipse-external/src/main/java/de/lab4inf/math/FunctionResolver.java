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

import java.util.Set;

/**
 * Service interface to resolve function implementations by name.
 * Services like this can be injected via the ServiceLoader or more
 * easier by the L4MLoader utility class like:
 * <pre>
 * FunctionResolver service = L4MLoader.load(FunctionResolver.class);
 * Function fct = service.byName("sin");
 * double x=0.2, y = fct.f(x);  // y = sin(x)
 * </pre>
 *
 * @author nwulff
 * @version $Id: FunctionResolver.java,v 1.6 2013/07/11 10:28:12 nwulff Exp $
 * @see de.lab4inf.math.L4MLoader
 * @since 06.01.2012
 */
@Service
public interface FunctionResolver {
    /**
     * Return the names of all service known functions as set.
     *
     * @return Set with function names
     */
    Set<String> functionNames();

    /**
     * Find a function implementation by name,
     *
     * @param fctName the spoken name for the function
     * @return Function implementation
     * @throws NoSuchMethodException if fctName is unknown
     */
    Function byName(String fctName) throws NoSuchMethodException;
}
 