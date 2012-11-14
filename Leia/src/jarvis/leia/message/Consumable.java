/*
 *
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

package jarvis.leia.message;

import jarvis.leia.header.Header;

/**
 * <p> Represents objects that can be consumed by a consumer.
 * A consumbale typically contains a header which represents the 
 * information about who can consume and how to consume this 
 * consumable.
 * @author apurv
 *
 */
public interface Consumable {
	
	public Header getHeader();	// first thing that is called by a consumer plans on consuming 
								// this consumable
	
	public String getData();	// called when a consumer needs to process the data in this 
								// consumable
	
	

}
