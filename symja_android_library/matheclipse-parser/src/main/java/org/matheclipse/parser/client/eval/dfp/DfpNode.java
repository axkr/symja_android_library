/*
 * Copyright 2005-2014 Axel Kramer (axelclk@gmail.com)
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
package org.matheclipse.parser.client.eval.dfp;

import org.apache.commons.math4.dfp.Dfp;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * 
 */
public class DfpNode extends ASTNode {

	private final Dfp value;

	public DfpNode(Dfp value) {
		super("DoubleNode");
		this.value = value;
	}

	public Dfp getDfpValue() {
		return value;
	}

	public String toString() {
		return value.toString();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof DfpNode) {
			return value.equals(((DfpNode) obj).value);
		}
		return false;
	}

	public int hashCode() {
		return value.hashCode();
	}

}
