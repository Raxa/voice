/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.leia.stream;

import jarvis.leia.message.Message;

/**
 * 
 * @author apurv
 *
 * @param <T>
 */
public abstract class MessageInputStream <T extends Message> {

	public abstract T readTextMessage();
}
