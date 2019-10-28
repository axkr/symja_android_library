/*
 * Copyright 2015 Fabian Prasser
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.core.newtonraphson;

import java.util.function.Function;

import org.hipparchus.linear.RealVector;

/**
 * This interface defines a function RxR -> R
 * @author Fabian Prasser
 * 
 */
public interface Function2D extends Function<RealVector, Double> {
    @Override
	public Double apply(RealVector input);
}
