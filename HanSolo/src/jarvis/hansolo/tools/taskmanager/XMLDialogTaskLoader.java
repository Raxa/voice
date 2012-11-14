/*
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

package jarvis.hansolo.tools.taskmanager;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLDialogTaskLoader {
	
	private Document doc;
	public XMLDialogTaskLoader(String dialogFile) 
			throws ParserConfigurationException, SAXException, IOException {
		File taskFile = new File(dialogFile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		this.doc = dBuilder.parse(taskFile);
		doc.getDocumentElement().normalize();
	}
	
	public Document getDialogTask() {
		return this.doc;
	}
	
	public NodeList getTaskList() {
		return this.doc.getChildNodes();
	}
	
	public String getRootName() {
		return this.doc.getDocumentElement().getNodeName();
	}
}
