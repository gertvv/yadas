package gov.lanl.yadas;

import java.util.*;

/**
 * Interface that defines input sources for use in YADAS analyses.  
 */
public interface MCMCInput {

	public double[] r (String varname);
	public int[] i (String varname);
	// consider adding length (): not worthwhile, can get from r("x").length 
	// r (double val), 
	
}
