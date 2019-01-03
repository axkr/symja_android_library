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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

//import org.apache.log4j.Logger;

/**
 * Sorted list.
 * 
 * @param <T> class of objects to be sorted
 * @author Tilman Neumann
 */
public class SortedList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 7448332292916331255L;

//	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(SortedList.class);

    /** Compares list elements. if null then the elements <T> must be Comparable */
    private Comparator<T> explicitComparator = null;
    /** In descending order the biggest elements according to Comparator or Comparable are sorted in at the beginning. */
    private SortOrder sortOrder = null;
    
    /**
     * Complete constructor for a list sorted in ascending or descending order, where
     * the comparison is done by an explicit constructor or the comparable capability
     * of list elements.
     * 
     * @param cmp External Comparator object or null
     * @param sortOrder ASCENDING or DESCENDING
     */ 
    public SortedList(Comparator<T> cmp, SortOrder sortOrder) {
        this.explicitComparator = cmp;
        if (sortOrder==null) throw new NullPointerException("sort order");
        this.sortOrder = sortOrder;
    }

    /**
     * Copy constructor.
     */
    public SortedList(SortedList<T> original) {
    	super(original);
        this.explicitComparator = original.explicitComparator;
        this.sortOrder = original.sortOrder;

    }

    // Methods ===============================================================
	
	/**
	 * Insert the new object at the position given by the Comparator.
	 */
	@Override
	public boolean add(T t) {
		return this.quickInsort(t);
	}

	/**
	 * Insert the new objects at the position given by the Comparator.
	 */
	@Override
	public boolean addAll(Collection<? extends T> externalObjs) {
		return this.quickInsort(externalObjs);
	}
	
    /**
     * Sort the given collection into this.
     * 
     * The argument list should be sortable after the same criteria like this,
     * using the same Comparator or Comparable interface.
     * 
     * @param externalObjs Collection of new objects
     */
    public boolean quickInsort(Collection<? extends T> externalObjs) {
       	boolean modified = false;
        if (externalObjs != null) {
            int nofExternalObjs = externalObjs.size();
            if (nofExternalObjs > 0) {
                //LOG.info("Sort in " + nofExternalObjs + " new objects...");
                for (final T externalObj : externalObjs) {
                    modified = this.quickInsort(externalObj) || modified;
                }
            }
        }
        return modified;
    }
    
    /**
     * Sorts a single new object into this.
     * 
     * @param externalObj new object
     */
    public boolean quickInsort(T externalObj) {
        if (externalObj != null) {
            return this.quickInsort(externalObj, 0, this.size());
        }
        return false; // list unmodified
    }
    
    /**
     * Sorts the given object into a subrange of this list indicated by minIdx..maxIdx.
     * 
     * @param externalObj new object
     * @param minIdx smallest index of subrange
     * @param maxIdx biggest index of subrange
     */
    private boolean quickInsort(T externalObj, int minIdx, int maxIdx) {
        //LOG.debug("minIdx = " + minIdx + ", maxIdx = " + maxIdx);
        if (minIdx == maxIdx) {
            // recurrence end: don't compare, sort in:
            super.add(minIdx, externalObj);
            //LOG.debug("Object " + externalObj + " sorted in at position " + minIdx + ".");
            return true;
        }
        
        final int cmpIdx = (minIdx + maxIdx) >> 1; // ">> 1" means "divide 2"
        final T internalObj = this.get(cmpIdx);
        final int idxCmp = this.compare(internalObj, externalObj);
        //LOG.debug("internalObj = " + internalObj + ", idxCmp = " + idxCmp);
        if (idxCmp <= 0) {
        	// The internal object shall have an index less than or equal to that of the new object.
        	// -> the new object must be sorted in behind the internal object.
            return this.quickInsort(externalObj, cmpIdx+1, maxIdx);
        }
        // the new object must be sorted in before the internal object.
        return this.quickInsort(externalObj, minIdx, cmpIdx);
    }
    
    /**
     * Compares internal and external object and returns how the insort relation looks like
     * (before, equals, after).
     * 
     * @param internalObj Internal object, already contained by this.
     * @param externalObj External object, to be sorted in
     * @return <0 / 0 / >0 if internal object shall be before / no matter where / behind the
     * external object.
     */
    @SuppressWarnings("unchecked")
    private int compare(T internalObj, T externalObj) {
        if ((internalObj!=null) && (externalObj!=null)) {
            // two non-null objects must be compared:
            int idxCmp = 0;
            if (this.explicitComparator != null) {
                idxCmp = this.explicitComparator.compare(internalObj, externalObj);
            } else if (internalObj instanceof Comparable) {
                //  objects must be Comparable
                final Comparable<T> implicitComparator = (Comparable<T>) internalObj;
                idxCmp = implicitComparator.compareTo(externalObj);
            } else {
                // no Comparator and not Comparable :(
                throw new IllegalStateException("SortedList.compare(): Sorting not possible, " + internalObj.getClass() + " is not Comparable and an external Comparator has not been given.");
            }
            // revert comparison result if sort order is descending
            return idxCmp*sortOrder.getMultiplier();
        }
        if (internalObj != null) {
            // external object is null -> to the end of the list...
            return -1;
        }
        // internal object is null -> external must be inserted before
        return 1;
    }
}
