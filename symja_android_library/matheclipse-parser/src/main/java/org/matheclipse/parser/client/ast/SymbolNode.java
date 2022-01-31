/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
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
package org.matheclipse.parser.client.ast;

/** A node for a parsed symbol string (i.e. Sin, Cos, Pi, $x,...) */
public final class SymbolNode extends ASTNode {
  final String context;

  /**
   * Create symbol in context <code>System`</code>. If <code>context == &quot;&quot;</code> the
   * current context from context path is used.
   *
   * @param value
   */
  public SymbolNode(final String value) {
    this(value, "");
  }

  /**
   * Create symbol in context <code>System`</code>. If <code>context == &quot;&quot;</code> the
   * current context from context path is used.
   *
   * @param value
   * @param context
   */
  public SymbolNode(final String value, final String context) {
    super(value);
    this.context = context;
  }

  public String context() {
    return context;
  }

  @Override
  public boolean dependsOn(String variableName) {
    return fStringValue.equals(variableName);
  }
}
