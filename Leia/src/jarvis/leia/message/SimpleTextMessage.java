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

import jarvis.leia.header.SimpleTextMessageHeader;

/**
 * 
 * @author apurv
 *
 */
public class SimpleTextMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimpleTextMessage(SimpleTextMessageHeader header, String data) {
		this.header = header;
		this.data = data;
	}
	
	
	@Override
	public String getData() {		
		return this.data;
	}
	
	@Override
	public String toString() {		
		String obj = this.header.toString() + "\n DATA \t\t\t:\t\t" + data;
		return obj;
	}

	@Override
	public int compareTo(Message o) {
		if(this.getHeader().getPriority() > o.getHeader().getPriority()) {
			return 1;
		} else if (this.getHeader().getPriority() < o.getHeader().getPriority()) {
			return -1;
		} else if ( this.getHeader().getTimeStamp() > o.getHeader().getTimeStamp()) {
			return 1;
		} else {
			return -1;
		}
		
	}

	

}
