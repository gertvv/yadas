package gov.lanl.yadas;

import java.util.*;

/**
 * Replicates the update algorithm used by MCMCParameter, so that similar steps 
 * can be used inside a MultipleParameterUpdate.  (The update algorith is 
 * variable-at-a-time Metropolis, with Gaussian proposals centered at the current 
 * value.)  One application is 
 * if we want to attempt these moves with two different proposal standard deviations.  
 * Another application is using this class as a subclass, for example for 
 * CauchyStandardPerturber, which proposes adding a Cauchy amount to the current 
 * value of a parameter.   
 * @see gov.lanl.yadas.MultipleParameterUpdate
 */
public class StandardPerturber implements TunablePerturber {

    /*
      replicates the update functionality in MCMCParameter
     */
    public StandardPerturber (double[] mss) {
	this (mss, 0);
    }
    public StandardPerturber (double[] mss, int which) {
	this.mss = mss;
	this.which = which;
    }
    public StandardPerturber (int which, double[] mss) {
	this (mss, which);
    }

    public void perturb (double[][] candarray, int whoseTurn) {
	double temp = mss[whoseTurn] * rand.nextGaussian();
	candarray[which][whoseTurn] += temp;
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

    double[] mss;
    int which;

    static Random rand = new Random(System.currentTimeMillis());
}
