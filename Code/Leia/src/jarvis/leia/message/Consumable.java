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
