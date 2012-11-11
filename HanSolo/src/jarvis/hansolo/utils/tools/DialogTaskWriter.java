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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class DialogTaskWriter {

	private BufferedWriter writer;
	private int NUM_NODES; 
	private int [][] mat;
	private LinkedList<Variable> variables;
	private String preamble = "# This is J.A.R.V.I.S task description file \n" +
							"# Please don't alter it's content unless you know what you are doing";
	
	public DialogTaskWriter(String taskFile) throws IOException  {
		this.writer = new BufferedWriter(new FileWriter(taskFile));
		this.variables = new LinkedList<Variable>();
	}
	
	public void initialise(int[][] mat) throws Exception {
		this.mat = mat;
		int rows = mat.length;
		int cols = mat[0].length;
		if(rows != cols) {
			throw new Exception("[DialogTaskWriter] Task matrix must be square. " +
					"Dimensions don't match");
		}
		this.NUM_NODES = rows;
	}
	
	/**
	 * Sets global variables for use by all subsequent task nodes 
	 * @param variables
	 */
	public void addVariable( Variable variable) {
		this.variables.add( variable );
		Iterator<Variable> iter = variables.iterator();
		while(iter.hasNext()) {
			iter.next().setScope(Scope.GLOBAL);
		}
	}
	
	/**
	 * encodes int [32 - 64] -> String using ASCII translation 
	 * @param mat
	 * @return
	 * @throws Exception
	 */
	public String matToG6(){
		int rows = mat.length;
		int cols = mat[0].length;
		
		String g6 = "";			// contains the final g6 encryption of graph
		int[] unit = new int[6];	// contains 6 bits of adj
		int index = 0;
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				unit[index] = mat[i][j];
				index ++;
				if(index == 5) {
					g6 = g6 + toASCII(unit);
					index = 0;
				}
			}
		}
		
		// if the elements in mat[][] aren't a multiple of 6
		// autofill the last unit by tailing 0's
		if(index > 0) {
			while(index < 5) {
				unit[index] = 0;
				index++;
			}
			g6 = g6 + toASCII(unit);
		}
		
		return g6;
	}

	private String toASCII(int[] unit) {
		int value = 0;
		for(int i = 0; i < 6 ; i++) {
			value += (int)Math.pow(2, i) * unit[i];
		}
		value += 32;
		char toChar = (char) value;
		return toChar + "";
	}
	
	/**
	 * write jtd file
	 * @throws IOException 
	 */
	public void printCTD() throws IOException {
		writer.write(preamble);
		writer.newLine();
		
		writer.write("# Describe Dialog Graph");
		writer.newLine();
		writer.write("TaskTree:" + matToG6());
		writer.newLine();
		
		writer.write("# Define variables");
		writer.newLine();
		Iterator<Variable> iter = variables.iterator();
		while(iter.hasNext()) {
			writer.write(iter.next() + "");
			writer.newLine();
		}
		writer.close();
	}
	
}
