/*
 *
 * See the file "LICENSE" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * Raxa.org
 *
 */

package jarvis.hansolo.utils.tools;

public class Scope {
	
	public String VALUE;
	public static final Scope GLOBAL = new Scope("GLOBAL");
	public static final Scope LOCAL = new Scope("LOCAL");
	
	private Scope(String value) {
		this.VALUE = value;
	}

}
