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

public class VariableType{
	 
	public String TYPE;
	public static final VariableType STR = new VariableType("STR");
	public static final VariableType INT = new VariableType("INT");
	public static final VariableType FLOAT = new VariableType("FLOAT");
	public static final VariableType BOOL = new VariableType("BOOL");
	
	private VariableType(String type){
		this.TYPE = type;
	}
}