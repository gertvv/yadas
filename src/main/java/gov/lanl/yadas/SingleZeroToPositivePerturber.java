package gov.lanl.yadas;
import java.util.*;
import java.text.*;

/**
 * Update step for use in reversible jump algorithms: the proposed move takes a 
 * parameter which is currently equal to zero and proposes an exponentially 
 * distributed new value, with mean set in the constructor.  
 */
public class SingleZeroToPositivePerturber implements JumpPerturber {

    public SingleZeroToPositivePerturber (int which, int whichpoint, double mien) {
	this.which = which;
	this.whichpoint = whichpoint;
	this.mien = mien;
	nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(3);
	nf.setGroupingUsed(false);
    }

    public void perturb (double[][] candarray, int whoseTurn) { 
	double temp = rand.nextDouble();
	whoseTurn = whichpoint;
	scale = temp = - mien * Math.log(temp);
	candarray[which][whoseTurn] = temp;
	stringvalue = nf.format(candarray[which][whoseTurn]) + "";
    }
    public int numTurns () { 
	return 1;
    }
    public double jacobian () { 
	// nonsense
	return adj;
    }
    public double density (double[][] oldarr, double[][] newarr, 
			   int whoseTurn) {
	whoseTurn = whichpoint;
	return Math.exp(-newarr[which][whichpoint]/mien)/mien;
    }

    public String toString () {
	return stringvalue;
    }

    private String stringvalue = "";
    private NumberFormat nf;

    private final int which;
    private final int whichpoint;
    private double mien = 1.0;
    private double scale = 1.0;
    private double adj = 1.0;
    static Random rand = new Random(System.currentTimeMillis());
}

