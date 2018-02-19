/*
 * Licensed to the Hipparchus project under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Hipparchus project licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hipparchus.ode;

import org.hipparchus.exception.MathIllegalArgumentException;

/**
 * Interface to compute by finite difference Jacobian matrix for some parameter
 * when computing {@link VariationalEquation partial derivatives equations}.
 */

public interface ParametersController extends Parameterizable {

    /**
     * Get parameter value from its name.
     *
     * @param name parameter name
     * @return parameter value
     * @throws MathIllegalArgumentException if parameter is not supported
     */
    double getParameter(String name) throws MathIllegalArgumentException;

    /**
     * Set the value for a given parameter.
     *
     * @param name  parameter name
     * @param value parameter value
     * @throws MathIllegalArgumentException if parameter is not supported
     */
    void setParameter(String name, double value) throws MathIllegalArgumentException;

}
