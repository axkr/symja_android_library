/*
 * Copyright 2022 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.mangara.diophantine;

import java.math.BigInteger;
import java.util.Objects;

/**
 * A pair of integers (x, y). This class is immutable.
 */
public class XYPair {

  /**
   * The first (x) and second (y) values of this pair.
   */
  public final BigInteger x, y;

  /**
   * Creates a new (x, y) pair with the given values.
   * 
   * @param x
   * @param y
   */
  public XYPair(final long x, final long y) {
    this.x = BigInteger.valueOf(x);
    this.y = BigInteger.valueOf(y);
  }

  /**
   * Creates a new (x, y) pair with the given values.
   * 
   * @param x
   * @param y
   * @throws IllegalArgumentException if x or y are null
   */
  public XYPair(final BigInteger x, final BigInteger y) {
    if (x == null || y == null) {
      throw new IllegalArgumentException("x and y may not be null.");
    }

    this.x = x;
    this.y = y;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + Objects.hashCode(this.x);
    hash = 23 * hash + Objects.hashCode(this.y);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final XYPair other = (XYPair) obj;
    if (!Objects.equals(this.x, other.x)) {
      return false;
    }
    if (!Objects.equals(this.y, other.y)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ')';
  }
}
