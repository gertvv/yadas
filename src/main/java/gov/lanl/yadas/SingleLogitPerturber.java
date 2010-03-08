package gov.lanl.yadas;
import java.text.*;
import java.util.*;

/**
 * Perturber usable in reversible jump algorithms, in which the proposed move is a 
 * Gaussian perturbation to one of the parameters on the logit scale.  Different 
 * from LogitPerturber in that this class can be used to set up a sequence of 
 * reversible jump moves.  
 */
public class SingleLogitPerturber implements JumpPerturber {

    public SingleLogitPerturber (int which, int whichpoint, double mss) {
	this.which = which;
	this.whichpoint = whichpoint;
	this.mss = mss;
	nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(3);
	nf.setGroupingUsed(false);
    }

    public void perturb (double[][] candarray, int whoseTurn) { 
	double temp;
	whoseTurn = whichpoint;
	scale = Math.exp(mss * rand.nextGaussian());
	temp = 1 / (1 + scale * (1 - candarray[which][whoseTurn]) / 
		    candarray[which][whoseTurn]);
	adj = temp * (1 - temp) / candarray[which][whoseTurn] / 
	    (1 - candarray[which][whoseTurn]);
	candarray[which][whoseTurn] = temp;
	stringvalue = nf.format(candarray[which][whoseTurn]) + "";
    }
    public int numTurns () { 
	return 1;
    }
    public double jacobian () { 
	return adj;
    }
    public double density (double[][] oldarr, double[][] newarr, 
			   int whoseTurn) {
	whoseTurn = whichpoint;
	double temp = oldarr[which][whoseTurn];
	temp = Math.log(temp/(1-temp));
	double temp1 = newarr[which][whoseTurn];
	temp1 = Math.log(temp1/(1-temp1));
	return 1/Math.sqrt(2*Math.PI) / mss * 
	    Math.exp(-(temp-temp1)*(temp-temp1)/2/mss/mss) / 
	    newarr[which][whoseTurn] / (1 - newarr[which][whoseTurn]);
    }

    public String toString () {
	return stringvalue;
    }

    private String stringvalue = "";
    private NumberFormat nf;
    private final int which;
    private final int whichpoint;
    private double mss;
    private double scale = 1.0;
    private double adj = 1.0;
    static Random rand = new Random(System.currentTimeMillis());
}

