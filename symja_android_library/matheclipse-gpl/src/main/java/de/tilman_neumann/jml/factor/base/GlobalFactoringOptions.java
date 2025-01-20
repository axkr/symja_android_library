/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.base;

/**
 * Global factoring settings.
 * 
 * Code "guarded" with a static final boolean = false will be removed by the compiler.
 * Thus if turned off, such code does not decrement performance at all.
 * 
 * @author Tilman Neumann
 */
public interface GlobalFactoringOptions {
	/**
	 * Basic analysis of timings and operations. This option is used by most or all factoring algorithms that collect relations.
	 * In SIQS, the analysis includes number of polynomials, number of smooth and partial relations (also by large factor counts),
	 * trials division results, solver runs and tested null-vectors, and sub-phase timings.
	 */
	static final boolean ANALYZE = false;

	/**
	 * Monitor the congruence collecting progress on-the-fly.
	 * This option needs ANALYZE as well.
	 */
	static final boolean ANALYZE_PROGRESS = false;

	/**
	 * A switch to additionally turn on analysis of the size of large factors that yield smooth relations.
	 * This option needs ANALYZE as well.
	 */
	static final boolean ANALYZE_LARGE_FACTOR_SIZES = false;
	
	/**
	 * A switch to additionally turn on analysis of the number of Q-values with positive and negative sign.
	 * This option needs ANALYZE as well.
	 */
	static final boolean ANALYZE_Q_SIGNS = false;
}
