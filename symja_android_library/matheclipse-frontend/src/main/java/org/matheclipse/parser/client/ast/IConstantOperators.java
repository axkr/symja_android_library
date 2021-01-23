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

/** Default header operator strings */
public interface IConstantOperators {

  /** Symbol for representing option All */
  public static final String All = "All";

  public static final String Inequality = "Inequality";

  /** Head for lists (i.e. &lt;|a->,b,...|&gt; ) */
  public static final String Association = "Association";

  /** Head for partial derivative */
  public static final String Derivative = "Derivative";

  /** Head for lists (i.e. {a,b,c,...} ) */
  public static final String List = "List";

  public static final String Optional = "Optional";

  /** Head for the Out history (needed for the % operator) */
  public static final String Out = "Out";

  public static final String Part = "Part";

  public static final String Pattern = "Pattern";

  public static final String Slot = "Slot";

  public static final String Span = "Span";

  public static final String SlotSequence = "SlotSequence";
}
