/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package jarvis.utils.textprocessing.distance;

/**
 * A simplified interface to represent data-types that can be 
 * compared. Comparison in this context is different from the one
 * issued by java.lang.Comparable . 
 * <p/>
 * Two objects of the type commensurable are either equal or unequal
 * depending on the call obj1.isEqual(obj2). Ideally, this call is 
 * expected to be reflexive, but it's not mandatory.
 * <p/>
 * 
 * @author apurv
 *
 */
public interface Commensurable<T> {

	/**
	 * Is called to compare this object with obj. 
	 * The relation R between "this" object and "obj"
	 * will be called such that ("this" R "obj"). So if 
	 * R is not reflexive this.isEqual(obj) is read as
	 * (this R obj).
	 * <p/>
	 * @param obj
	 * @return boolean result of comparison. true if equal, false otherwise
	 */
	public boolean isEqual(T obj);
}
