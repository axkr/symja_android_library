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

import java.lang.reflect.Field;
import java.util.HashMap;

//import org.apache.log4j.Logger;

import sun.misc.Unsafe;

/**
 * Utility to provide a sun.misc.Unsafe instance and manages native memory.
 * 
 * @author Tilman Neumann
 */
public class UnsafeUtil {
//	private static final Logger LOG = Logger.getLogger(UnsafeUtil.class);
//	private static final boolean DEBUG = false;
	
	private static final Unsafe UNSAFE = fetchUnsafe();
	
    // From DirectByteBuffer class: If IS_PAGE_ALIGNED is true, then we have to add PAGE_SIZE to the size of each allocation
	private static final boolean IS_PAGE_ALIGNED = false; // VM.isDirectMemoryPageAligned(); // IntelliJ does not like dependencies on VM
	private static final int PAGE_SIZE = UNSAFE.pageSize();
	private static final int ADDITIONAL_SIZE = IS_PAGE_ALIGNED ? PAGE_SIZE : 0;
	
	// a map from addresses to allocation sizes
	private static final HashMap<Long, Long> ADDRESS_2_SIZE_MAP = new HashMap<Long, Long>();
	// native memory already allocated in byte
	private static long TOTAL_ALLOCATED = 0;
	
	static {
		// register shutdown hook that checks release of all memory on normal shutdown
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
//				if(TOTAL_ALLOCATED == 0) {
//					LOG.info("All native memory has been released.");
//				} else {
//					LOG.error(TOTAL_ALLOCATED + " bytes of native memory have not been released !");
//				}
		    }
		});
	}
	
	private UnsafeUtil() {
		// static class
	}

	private static Unsafe fetchUnsafe() {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//			LOG.error("Could not get unsafe: " + e, e);
			return null;
		}
	}

	/**
	 * @return the Unsafe
	 */
	public static Unsafe getUnsafe() {
		return UNSAFE;
	}
	
	/**
	 * Allocate a native memory block.
	 * @param size desired size
	 * @return address
	 */
	public synchronized static long allocateMemory(long size) {
		long allocationSize = size + ADDITIONAL_SIZE;
//		if (DEBUG) LOG.debug("Allocate " + allocationSize + " bytes >>>");
		long address = UNSAFE.allocateMemory(allocationSize);
//		if (DEBUG) LOG.debug("<<< Allocation of " + allocationSize + " bytes complete.");
		ADDRESS_2_SIZE_MAP.put(address, allocationSize);
		TOTAL_ALLOCATED += allocationSize;
		return address;
	}
	
	/**
	 * Release a native memory block.
	 * @param address
	 */
	public synchronized static void freeMemory(long address) {
		Long allocationSize = ADDRESS_2_SIZE_MAP.get(address);
		if (allocationSize == null) {
			throw new IllegalStateException("Attempt to release native memory block that is not allocated! address = " + address);
		}
//		if (DEBUG) LOG.debug("Release " + allocationSize + " bytes >>>");
		UNSAFE.freeMemory(address);
//		if (DEBUG) LOG.debug("<<< Release of " + allocationSize + " bytes complete.");
		ADDRESS_2_SIZE_MAP.remove(address);
		TOTAL_ALLOCATED -= allocationSize;
	}
}
