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
 * Auxiliary class for formatting time strings.
 * 
 * @author Tilman Neumann
 */
public class TimeUtil {
	/**
	 * @param t0 start time in milliseconds
	 * @param t1 end time in milliseconds
	 * @return duration string
	 */
	public static String timeDiffStr(long t0, long t1) {
		return timeStr(t1-t0);
	}
	
	/**
	 * @param milliSeconds duration in milliseconds
	 * @return duration string
	 */
	public static String timeStr(long milliSeconds) {
		String ret = "";
		long seconds = milliSeconds / 1000;
		if (seconds > 0) {
			long minutes = seconds / 60;
			if (minutes > 0) {
				long hours = minutes / 60;
				if (hours > 0) {
					long days = hours / 24;
					if (days > 0) {
						ret = days + "d";
					}
					long remainingHours = hours % 24;
					if (remainingHours > 0) {
						if (ret.length()>0) {
							ret += ", ";
						}
						ret += remainingHours + "h";
					}
				}
				long remainingMinutes = minutes % 60;
				if (remainingMinutes > 0) {
					if (ret.length()>0) {
						ret += ", ";
					}
					ret += remainingMinutes + "m";
				}
			}
			long remainingSeconds = seconds % 60;
			if (remainingSeconds > 0) {
				if (ret.length()>0) {
					ret += ", ";
				}
				ret += remainingSeconds + "s";
			}
		}
		long remainingMilliSeconds = milliSeconds % 1000;
		if (ret.length()>0) {
			if (remainingMilliSeconds>0) {
				ret += ", " + remainingMilliSeconds + "ms";
			}
		} else {
			ret += remainingMilliSeconds + "ms";
		}
		return ret;
	}
}
