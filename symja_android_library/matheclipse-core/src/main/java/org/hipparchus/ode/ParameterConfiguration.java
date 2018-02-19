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

/**
 * Simple container pairing a parameter name with a step in order to compute
 * the associated Jacobian matrix by finite difference.
 * <p>
 * Instances of this class are guaranteed to be immutable.
 * </p>
 */
public class ParameterConfiguration {

    /**
     * Parameter name.
     */
    private final String parameterName;

    /**
     * Parameter step for finite difference computation.
     */
    private final double hP;

    /**
     * Parameter name and step pair constructor.
     *
     * @param parameterName parameter name
     * @param hP            parameter step
     */
    ParameterConfiguration(final String parameterName, final double hP) {
        this.parameterName = parameterName;
        this.hP = hP;
    }

    /**
     * Get parameter name.
     *
     * @return parameterName parameter name
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Get parameter step.
     *
     * @return hP parameter step
     */
    public double getHP() {
        return hP;
    }

}
