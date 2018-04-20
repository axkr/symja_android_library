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
 * Interface for providing different types of sequences of numbers.  This is
 * a simple but powerful abstraction that provides considerable flexibility
 * in implementing classes that require numeric configuration.  Refer to the
 * implementations in this package for examples of how it can be used.
 * @param <T> The type (Integer, Long, Double, etc.) of number to generate.
 * @author Daniel Dyer
 * @see ConstantGenerator
 * @see AdjustableNumberGenerator
 */
public interface NumberGenerator<T extends Number>
{
    /**
     * @return The next value from the generator.
     */
    T nextValue();
}
