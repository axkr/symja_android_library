// ============================================================================
//   Copyright 2006-2012 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.maths.number;

/**
 * Convenience implementation of {@link NumberGenerator} that always
 * returns the same value.
 * @param <T> The numeric type (Integer, Long, Double, etc.) of the constant.
 * @author Daniel Dyer
 */
public class ConstantGenerator<T extends Number> implements NumberGenerator<T>
{
    private final T constant;

    /**
     * Creates a number generator that always returns the same
     * values.
     * @param constant The value to be returned by all invocations
     * of the {@link #nextValue()} method.
     */
    public ConstantGenerator(T constant)
    {
        this.constant = constant;
    }

    /**
     * @return The constant value specified when the generator was constructed.
     */
    public T nextValue()
    {
        return constant;
    }
}
