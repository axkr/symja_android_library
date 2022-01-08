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

/**
 * A sequence-pattern node for zero or n expressions (i.e. <code>___</code> or <code>x___</code>)
 */
public final class Pattern3Node extends PatternNode {

  public Pattern3Node(final SymbolNode symbol, final ASTNode constraint) {
    this(symbol, constraint, false);
  }

  public Pattern3Node(final SymbolNode symbol, final ASTNode constraint, boolean optional) {
    super(symbol, constraint, optional);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass().equals(obj.getClass())) {
      Pattern3Node pn = (Pattern3Node) obj;
      if (fSymbol == pn.fSymbol) {
        if (fConstraint == null || pn.fConstraint == null) {
          return fConstraint == pn.fConstraint;
        }
        return fConstraint.equals(pn.fConstraint);
      } else {
        if (fSymbol == null || pn.fSymbol == null) {
          return false;
        }
        if (fSymbol.equals(pn.fSymbol)) {
          if (fConstraint == null || pn.fConstraint == null) {
            return fConstraint == pn.fConstraint;
          }
          return fConstraint.equals(pn.fConstraint);
        }
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (fSymbol != null) {
      return fSymbol.hashCode() * 37;
    }
    return 17;
  }

  @Override
  public String toString() {
    final StringBuilder buff = new StringBuilder();
    if (fSymbol != null) {
      buff.append(fSymbol.toString());
    }
    buff.append("___");
    if (fDefault) {
      buff.append('.');
    }
    if (fConstraint != null) {
      buff.append(fConstraint.toString());
    }
    return buff.toString();
  }
}
