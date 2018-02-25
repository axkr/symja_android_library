/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
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

package de.lab4inf.math.functions;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.gof.Visitor;

/**
 * Base for all Lab4Math functions within this package.
 *
 * @author nwulff
 * @version $Id: L4MFunction.java,v 1.3 2014/06/26 11:25:35 nwulff Exp $
 * @since 07.12.2009
 */
public abstract class L4MFunction extends L4MObject implements Function {
    /* (non-Javadoc)
     * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
     */
    @Override
    public void accept(final Visitor<Function> visitor) {
        visitor.visit(this);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.Function#f(double[])
     */
    public abstract double f(final double... x);
}
 