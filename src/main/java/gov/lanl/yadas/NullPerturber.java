package gov.lanl.yadas;
import java.util.*;
import java.text.*;

/** 
 * Perturber used in Reversible Jump applications in which one of the parameter 
 * subspaces has only a single element, no that no move is possible or necessary.  
 */
public class NullPerturber implements JumpPerturber {

    public NullPerturber () {
    }

    public void perturb (double[][] candarray, int whoseTurn) { 
    }
    public int numTurns () { 
	return 1;
    }
    public double jacobian () { 
	// nonsense
	return 1.0;
    }
    public double density (double[][] oldarr, double[][] newarr, 
			   int whoseTurn) {
	return 1.0;
    }

    public String toString () {
	return stringvalue;
    }

    private String stringvalue = "";
    private NumberFormat nf;

}

