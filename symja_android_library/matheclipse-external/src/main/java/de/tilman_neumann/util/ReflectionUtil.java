/*
 * integer-sequence-search (ISS) is an offline OEIS sequence search engine.
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
 * Static auxiliary methods for java objects meta data.
 * 
 * @author Tilman Neumann
 */
public class ReflectionUtil {
	private ReflectionUtil() {
		// static class
	}
	
	/**
	 * Returns the package name for the given class.
	 * 
	 * @param clazz Class
	 * @return Package name
	 */
	public static String getPackageName(Class<?> clazz) {
		String className = clazz.getName();
		int lastPointIndex = className.lastIndexOf('.');
		if (lastPointIndex < 0) {
			return "";
		}
		return className.substring(0, lastPointIndex);
	}
}
