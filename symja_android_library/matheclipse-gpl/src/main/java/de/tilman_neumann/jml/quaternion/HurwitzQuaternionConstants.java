/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2022 Tilman Neumann - tilman.neumann@web.de
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.jml.quaternion;

import static de.tilman_neumann.jml.base.BigIntConstants.I_0;
import static de.tilman_neumann.jml.base.BigIntConstants.I_1;
import static de.tilman_neumann.jml.base.BigIntConstants.I_MINUS_1;

public class HurwitzQuaternionConstants {
	
	public static final HurwitzQuaternion HQ_0 = new HurwitzQuaternion(I_0, true);
	public static final HurwitzQuaternion HQ_1 = new HurwitzQuaternion(I_1, true);
	
	public static final HurwitzQuaternion[] HALF_INTEGER_UNITS = new HurwitzQuaternion[] {
		new HurwitzQuaternion(I_1,       I_1,       I_1,       I_1,       false),
		
		new HurwitzQuaternion(I_1,       I_1,       I_1,       I_MINUS_1, false),
		new HurwitzQuaternion(I_1,       I_1,       I_MINUS_1, I_1,       false),
		new HurwitzQuaternion(I_1,       I_MINUS_1, I_1,       I_1,       false),
		new HurwitzQuaternion(I_MINUS_1, I_1,       I_1,       I_1,       false),

		new HurwitzQuaternion(I_1,       I_1,       I_MINUS_1, I_MINUS_1, false),
		new HurwitzQuaternion(I_1,       I_MINUS_1, I_1,       I_MINUS_1, false),
		new HurwitzQuaternion(I_MINUS_1, I_1,       I_1,       I_MINUS_1, false),
		new HurwitzQuaternion(I_1,       I_MINUS_1, I_MINUS_1, I_1,       false),
		new HurwitzQuaternion(I_MINUS_1, I_1,       I_MINUS_1, I_1,       false),
		new HurwitzQuaternion(I_MINUS_1, I_MINUS_1, I_1,       I_1,       false),

		new HurwitzQuaternion(I_1,       I_MINUS_1, I_MINUS_1, I_MINUS_1, false),
		new HurwitzQuaternion(I_MINUS_1, I_1,       I_MINUS_1, I_MINUS_1, false),
		new HurwitzQuaternion(I_MINUS_1, I_MINUS_1, I_1,       I_MINUS_1, false),
		new HurwitzQuaternion(I_MINUS_1, I_MINUS_1, I_MINUS_1, I_1,       false),

		new HurwitzQuaternion(I_MINUS_1, I_MINUS_1, I_MINUS_1, I_MINUS_1, false)
	};
}
