/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.leia.message;

import java.io.Serializable;

import jarvis.leia.header.Header;

/**
 * Implements an abstract structure to represent a typical 
 * message being passed between two or more components of Jarvis
 * <p/>
 * A typical message will have a header representing it's type,
 * priority, source and destination. There will be additional 
 * payload of type String to carry information that needs to be 
 * carried in this message
 * 
 * @author apurv
 *
 */
public abstract class Message implements Consumable, Serializable, Comparable<Message> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Header header;
	
	public String data;
	
	public long TIME_STAMP;
	
	
	@Override
	public Header getHeader() {
		return header;
	}	
	
}
