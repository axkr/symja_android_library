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
package io.github.mangara.diophantine.iterators;

import java.math.BigInteger;
import java.util.Iterator;
import io.github.mangara.diophantine.XYPair;

/**
 * A forward iterator over all pairs of integers.
 * <p>
 * The sequence starts with (0, 0), (1, 0), (1, -1), (0, -1), (-1, -1), (-1, 0), (-1, 1), (0, 1),
 * (1, 1), (2, 0), ... It spirals out from (0, 0), hitting all pairs at L_{\infty} distance k before
 * those at distance k + 1.
 */
public class XYIterator implements Iterator<XYPair> {

  private XYPair next = new XYPair(0, 0);

  @Override
  public boolean hasNext() {
    return true;
  }

  @Override
  public XYPair next() {
    XYPair result = next;
    next = step(next);
    return result;
  }

  private XYPair step(XYPair xy) {
    int sigX = xy.x.signum();
    int sigY = xy.y.signum();
    int XcompY = xy.x.abs().compareTo(xy.y.abs());

    // Step down and right at the end
    if (sigX > 0 && xy.y.equals(BigInteger.ONE)) {
      return new XYPair(xy.x.add(BigInteger.ONE), BigInteger.ZERO);
    }

    // Right side: walk down
    if (XcompY > 0 && sigX > 0) {
      return down(xy);
    }
    // Left side: walk up
    if (XcompY > 0 && sigX < 0) {
      return up(xy);
    }
    // Top side: walk right
    if (XcompY < 0 && sigY > 0) {
      return right(xy);
    }
    // Bottom side: walk left
    if (XcompY < 0 && sigY < 0) {
      return left(xy);
    }

    // Top right: walk down
    if (XcompY == 0 && sigX == sigY && sigX > 0) {
      return down(xy);
    }
    // Bottom left: walk up
    if (XcompY == 0 && sigX == sigY && sigX < 0) {
      return up(xy);
    }
    // Bottom right: walk left
    if (XcompY == 0 && sigX != sigY && sigX > 0) {
      return left(xy);
    }
    // Top left: walk right
    if (XcompY == 0 && sigX != sigY && sigX < 0) {
      return right(xy);
    }

    // (0, 0)
    return right(xy);
  }

  private XYPair up(XYPair xy) {
    return new XYPair(xy.x, xy.y.add(BigInteger.ONE));
  }

  private XYPair down(XYPair xy) {
    return new XYPair(xy.x, xy.y.subtract(BigInteger.ONE));
  }

  private XYPair right(XYPair xy) {
    return new XYPair(xy.x.add(BigInteger.ONE), xy.y);
  }

  private XYPair left(XYPair xy) {
    return new XYPair(xy.x.subtract(BigInteger.ONE), xy.y);
  }
}
