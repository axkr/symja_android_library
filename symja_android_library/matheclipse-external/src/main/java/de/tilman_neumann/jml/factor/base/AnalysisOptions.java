/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
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
 * Factoring analysis settings.
 * 
 * Code "guarded" with a static final boolean = false will be removed by the compiler.
 * Thus if turned off, analysis code does not decrement performance.
 * 
 * Note as well that we would never want to do analysis/profiling in nested algorithms like the internalQS called
 * by trial division to factor large Q rests, but it does not hurt either.
 * 
 * @author Tilman Neumann
 */
public interface AnalysisOptions {
	/**
	 * Basic analysis includes number of polynomials, number of smooth and partial relations
	 * (also by large factor counts), trials division results, solver runs and tested null-vectors,
	 * and sub-phase timings.
	 */
	static final boolean ANALYZE = false;

	/**
	 * A switch to additionally turn on analysis of the size of large factors that yield smooth relations.
	 */
	static final boolean ANALYZE_LARGE_FACTOR_SIZES = false;
	
	/**
	 * A switch to additionally turn on analysis of the number of Q-values with positive and negative sign.
	 */
	static final boolean ANALYZE_Q_SIGNS = false;
}
