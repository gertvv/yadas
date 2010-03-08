package gov.lanl.yadas;
import java.text.*;
import java.util.*;

/**
 * Perturber usable in reversible jump algorithms, in which the proposed move is a 
 * Gaussian perturbation to one of the parameters on the log scale.  
 */
public class SingleLogPerturber implements JumpPerturber {

    public SingleLogPerturber (int which, int whichpoint, double mss) {
	this.which = which;
	this.whichpoint = whichpoint;
	this.mss = mss;
	nf = NumberFormat.getNumberInstance();
	nf.setMaximumFractionDigits(3);
	nf.setGroupingUsed(false);
    }

    public void perturb (double[][] candarray, int whoseTurn) { 
	whoseTurn = whichpoint;
	scale = Math.exp(mss * rand.nextGaussian());
	candarray[which][whoseTurn] *= scale;
	stringvalue = nf.format(candarray[which][whoseTurn]) + "";
    }
    public int numTurns () { 
	return 1;
    }
    public double jacobian () { 
	return scale;
    }
    public double density (double[][] oldarr, double[][] newarr, 
			   int whoseTurn) {
	whoseTurn = whichpoint;
	double ratio = newarr[which][whichpoint] / oldarr[which][whichpoint];
	return 1/Math.sqrt(2*Math.PI) / mss * 
	    Math.exp(-Math.log(ratio) * Math.log(ratio)/2/mss/mss) / 
	    newarr[which][whoseTurn];
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
    static Random rand = new Random(System.currentTimeMillis());
}

