/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.parser.client.eval;

public class DoubleVariable implements IDoubleValue {
  double value;

  public DoubleVariable(double v) {
    value = v;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.matheclipse.parser.eval.IDoubleValue#getValue()
   */
  @Override
  public double getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.matheclipse.parser.eval.IDoubleValue#setValue(double)
   */
  @Override
  public void setValue(double value) {
    this.value = value;
  }
}
