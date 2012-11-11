package edu.cmu.sphinx.linguist.language.grammar;

import java.io.IOException;

import edu.cmu.sphinx.util.LogMath;
import edu.cmu.sphinx.util.props.S4Boolean;
import edu.cmu.sphinx.util.props.S4Component;
import edu.cmu.sphinx.util.props.S4String;

public class CommandListGrammar extends Grammar{

	/** The property that defines the location of the word list grammar */
    @S4String(defaultValue = "command.gram")
    public final static String PROP_PATH = "path";

    /** The property that if true, indicates that this is a looping grammar */
    @S4Boolean(defaultValue = true)
    public final static String PROP_LOOP = "isLooping";

    /** The property that defines the logMath component. */
    @S4Component(type = LogMath.class)
    public final static String PROP_LOG_MATH = "logMath";
	
    
    // ---------------------
    // Configurable data
    // ---------------------
    private String path;
    private boolean isLooping;
    private LogMath logMath;
	
	@Override
	protected GrammarNode createGrammar() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
