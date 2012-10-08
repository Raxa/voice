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
 * Implements commensurable string objects. 
 * 
 * @author apurv
 *
 */

public class CommensurableString implements Commensurable<CommensurableString> {

	private String str;
	
	public CommensurableString (String str) {
		this.str = str;
	}
	
	public String getString() {
		return str;
	}

	/**
	 * Compare both string without worrying about the case
	 * return true if both are equal
	 */
	@Override
	public boolean isEqual(CommensurableString obj) {
		String objString = obj.getString();
		if(str.compareToIgnoreCase(objString) == 0) {
			return true;
		}
		
		return false;
	}

}
