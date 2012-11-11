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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class Variable {

	// define a Not-Defined-Variable symbol for variables that 
	// are yet to be defined
	public final String NDV = "$NDV$";
	
	// by default all variables are NDV
	private String VALUE = "$NDV$";
	
	private LinkedList<String> valueSet;
	
	public Variable (int id, String name, VariableType type) {
		this.ID = id;
		this.NAME = name;
		this.TYPE = type;
		this.questions = new LinkedList<String>();
		this.confirmations = new LinkedList<String>();
		this.valueSet = new LinkedList<String>();
	}
	
	// unique ID
	public int ID;
	
	// every variable has to have a name
	private String NAME;
	
	// every variable has a type
	// by default: STR
	private VariableType TYPE = VariableType.STR;
	
	// by default: GLOBAL
	private Scope SCOPE = Scope.GLOBAL;
	
	// specifies what to ask to update this variable
	private LinkedList<String> questions;
	
	// specifies how to confirm this variables value
	private LinkedList<String> confirmations;
	
	// get the name of this variable
	public String getVariableName() {
		return this.NAME;
	}
	
	// get the data type the variable corresponds to
	public VariableType getType() {
		return this.TYPE;
	}
	
	// get variable's value
	public String getValue() {
		return this.VALUE;
	}
	
	// get Scope of this variable
	public Scope getScope() {
		return this.SCOPE;
	}
	
	// get a question to ask to set this variable
	public String getQuestion() {
		Iterator<String> iter = questions.iterator();
		return iter.next();
	}
	
	// get confirmation
	public String getConfirmation(){
		Iterator<String> iter = confirmations.iterator();
		String confirmation = iter.next();
		StringTokenizer st = new StringTokenizer(confirmation);
		String result = "";
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			if(token.compareToIgnoreCase("$val") == 0) {
				token = this.getValue();
			}
			result += token + " ";
		}
		return result;
	}
	
	// get value set
	public LinkedList<String> getValueSet() {
		return this.valueSet;
	}
	
	// set name
	public void setName(String name) {
		this.NAME = name;
	}
	
	// set value
	public void setValue(String value) {
		this.VALUE = value;
	}
	
	// set possibleValue
	public void setPossibleValues(String value) {
		this.valueSet.add(value);
	}
	
	// set Scope
	public  void setScope(Scope scope) {
		this.SCOPE = scope;
	}
	
	// set questions
	public void setQuestion(String question) {
		this.questions.add(question);
	}
	
	// set confirmations
	public void setConfirmation(String confirmation) {
		this.confirmations.add(confirmation);
	}
	
	// toString
	@Override
	public String toString() {
		String definition = "def var { \n" +
							"ID:" + ID + "\n" +
							"NAME:" + NAME + "\n" +
							"VALUE:" + VALUE + "\n" +
							"QUESTION:" +
							getPrintable(questions) + "\n" +
							"CONFIRMATION:" +
							getPrintable(confirmations) + "\n"+
							"}";
		return definition;
	}
	
	private String getPrintable(LinkedList<String> list) {
		Iterator<String> iter = list.iterator();
		String result ="\n";
		while(iter.hasNext()) {
			result += "STYLE:" + iter.next() + "\n";
		}
		return result;
	}
	
	
}
