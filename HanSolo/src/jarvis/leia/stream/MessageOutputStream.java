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

import jarvis.leia.message.SimpleTextMessage;

/**
 * 
 * @author apurv
 *
 */
public abstract class MessageOutputStream {

	public abstract void writeTextMessage(SimpleTextMessage message) throws InterruptedException;
}
