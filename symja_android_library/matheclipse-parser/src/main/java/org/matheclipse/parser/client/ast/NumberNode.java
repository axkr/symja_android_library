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
package org.matheclipse.parser.client.ast;

/** The basic node for storing a parsed number expression string */
public abstract class NumberNode extends ASTNode {

  protected boolean sign;

  protected NumberNode(final String value) {
    super(value);
    sign = false;
  }

  public double doubleValue() {
    return Double.parseDouble(toString());
  }

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public String getString() {
    if (sign) {
      return "-" + fStringValue;
    }
    return fStringValue;
  }

  @Override
  public int hashCode() {
    if (sign) {
      return fStringValue.hashCode() * 17;
    }
    return fStringValue.hashCode();
  }

  public boolean isSign() {
    return sign;
  }

  /** Toggle the sign of the number. */
  public void toggleSign() {
    sign = !sign;
  }

  @Override
  public String toString() {
    if (sign) {
      return "-" + fStringValue;
    }
    return fStringValue;
  }
}
