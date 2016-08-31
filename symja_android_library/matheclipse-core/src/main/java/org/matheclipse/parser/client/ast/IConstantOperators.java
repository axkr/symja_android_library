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
 * Default header operator strings
 * 
 */
public interface IConstantOperators {

	/**
	 * Head for lists (i.e. {a,b,c,...} )
	 */
	public final static String List = "List";

	/**
	 * Head for the Out history (needed for the % operator)
	 */
	public final static String Out = "Out";

	/**
	 * Head for the Out history (needed for the <array>[ ] operator)
	 */
	public final static String Part = "Part";

	/**
	 * Head for the Out history (needed for the #1,#2,... operator)
	 */
	public final static String Slot = "Slot";

	/**
	 * Head for the Out history (needed for the ##1,##2,... operator)
	 */
	public final static String SlotSequence = "SlotSequence";

}
