/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
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
package de.lab4inf.math.extrema;

/**
 * Enumeration of evolutionary strategy modes.
 *
 * @author nwulff
 * @version $Id: EvolutionaryStrategy.java,v 1.3 2010/02/25 15:31:54 nwulff Exp $
 * @since 20.06.2007
 */
public enum EvolutionaryStrategy {
    /**
     * the best parents plus child strategy.
     */
    PARENTS_PLUS_CHILDS("m+l"),
    /**
     * the best childs only strategy.
     */
    ONLY_CHILDS("m;l");

    private final String type;

    private EvolutionaryStrategy(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
 