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
package de.lab4inf.math.gof;

/**
 * Generic subject interface for the visitor design pattern.
 *
 * @param <T> type of the visitable subject.
 * @author nwulff
 * @version $Id: Visitable.java,v 1.1 2014/06/26 11:25:35 nwulff Exp $
 * @since 26.06.2014
 */
public interface Visitable<T extends Visitable<T>> {
    void accept(Visitor<T> visitor);
}
 