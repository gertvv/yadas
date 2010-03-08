package gov.lanl.yadas;

import java.util.*;

/**
 * This class defines Metropolis moves in which 
 * intercept and slope parameters in 
 * regressions can be updated simultaneously.  
 * In particular, keeps the value of 
 * intercept + slope * xbar constant.  
 * It may sometimes make more sense to center the x's.  
 * Assume the first parameter is the intercept parameter and the 
 * second is the slope parameter.  
 * @see gov.lanl.yadas.MultipleParameterUpdate
 */
public class InterceptSlopePerturber implements TunablePerturber {
    
    /**
     * @param xbar is the average value of the covariate that the 
     * slope parameter is multiplied by.   
     */
    public InterceptSlopePerturber (int[] which, double[] mss, 
				    double[] xbar) {
	this.which = which;
	this.mss = mss;
	this.xbar = xbar;
    }
    
    public void perturb (double[][] candarray, int whoseTurn) {
	if (xbar[whoseTurn] == 0.0) return;
	double temp = mss[whoseTurn] * rand.nextGaussian ();
	if (Math.abs(xbar[whoseTurn]) >= 1.0) {
	    candarray[which[0]][whoseTurn] += temp;
	    candarray[which[1]][whoseTurn] -= temp / xbar[whoseTurn];
	} else {
	    candarray[which[0]][whoseTurn] += temp * xbar[whoseTurn];
	    candarray[which[1]][whoseTurn] -= temp;
	}	
    }
    
    public int numTurns () {
	return mss.length;
    }
    
    public double jacobian () {
	return 1.0;
    }
    
    public double[] getStepSizes () {
	double[] tem = new double[mss.length];
	System.arraycopy (mss, 0, tem, 0, mss.length);
	return tem;
    }
    public void setStepSize (double s, int i) {
	mss[i] = s;
    }
    public void setStepSizes (double[] s) {
	System.arraycopy (s, 0, mss, 0, s.length);
    }
    
    int[] which;
    double[] mss;
    double[] xbar;
    static Random rand = new Random();
}
