/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2021 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.base.congruence;

import java.util.HashSet;

/**
 * Interface for cycle counting algorithms.
 * @author Till
 *
 * @see [LLDMW02] Leyland, Lenstra, Dodson, Muffett, Wagstaff 2002: "MPQS with three large primes", Lecture Notes in Computer Science, 2369.
 * @see [LM94] Lenstra, Manasse 1994: "Factoring With Two Large Primes", Mathematics of Computation, volume 63, number 208, page 789.
 */
public interface CycleCounter {

	/** Initialize this cycle counter for a new factor argument. */
	void initializeForN();
	
	/**
	 * Counts the number of independent cycles in the partial relations.
	 * 
	 * @param partial the newest partial relation to add
	 * @param correctSmoothCount the correct number of smooths from partials (only for debugging)
	 * @return the updated number of smooths from partials
	 */
	int addPartial(Partial partial, int correctSmoothCount);

	/**
	 * @return the partial relations found so far
	 */
	HashSet<Partial> getPartialRelations();

	/**
	 * @return number of partial relations found so far.
	 */
	int getPartialRelationsCount();
	
	/**
	 * @return the number of cycles counted by this algorithm
	 */
	int getCycleCount();
}
