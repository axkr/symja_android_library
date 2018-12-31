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
package de.tilman_neumann.util;

/**
 * A simple time recorder.
 * @author Tilman Neumann
 */
public class Timer {
	private long start, t;
	
	/**
	 * Full constructor, starts timer.
	 */
	public Timer() {
		start();
	}

	/**
	 * Restart timer.
	 */
	public void start() {
		start = t = System.currentTimeMillis();
	}
	
	/**
	 * @return time difference from last check to this check in milliseconds
	 */
	public long capture() {
		long last = t;
		t = System.currentTimeMillis();
		return t-last;
	}
	
	/**
	 * @return the total run time in milliseconds since this timer was created or restarted
	 */
	public long totalRuntime() {
		return System.currentTimeMillis() - start;
	}
}
