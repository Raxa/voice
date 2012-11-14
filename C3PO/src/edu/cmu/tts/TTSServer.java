/*
 *
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

package edu.cmu.tts;


/**
 * A bare-bones Server containing a ServerSocket waiting for connection
 * requests. Subclasses should implement the <code>spawnProtocolHandler</code>
 * method.
 */
public abstract class TTSServer {

    /**
     * This method takes care of any protocols that need to be followed before 
     * passing playing the prompt		
     *
     * @param prompt the message to be finally played
     * @return true if prompt was successfully played, else false
     */
    protected abstract boolean protocolHandler(String prompt);
}