package gov.lanl.yadas;
import java.util.*;
import java.text.*;

/**
 * Update step usable in reversible jump algorithms: the proposed move takes a 
 * parameter that is currently positive and sets it to zero.  
 */
public class SinglePositiveToZeroPerturber implements JumpPerturber {

    public SinglePositiveToZeroPerturber (int which, int whichpoint) {
	this.which = which;
	this.whichpoint = whichpoint;
	nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(3);
	nf.setGroupingUsed(false);
    }

    public void perturb (double[][] candarray, int whoseTurn) { 
	whoseTurn = whichpoint;
	candarray[which][whoseTurn] = 0.0;
	stringvalue = nf.format(candarray[which][whoseTurn]) + "";
    }
    public int numTurns () { 
	return 1;
    }
    public double jacobian () { 
	// nonsense
	return 1;
    }
    public double density (double[][] oldarr, double[][] newarr, 
			   int whoseTurn) {
	return 1;
    }

    public String toString () {
	return stringvalue;
    }

    private String stringvalue = "";
    private NumberFormat nf;

    private final int which;
    private final int whichpoint;
}

