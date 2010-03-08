package gov.lanl.yadas;

import java.util.*;

/** 
 * Updates parameters using variable-at-a-time Metropolis, with Cauchy proposals
 * centered at the current values.  There are examples where Gaussian proposals 
 * cause algorithms to fail to converge to the correct equilibrium distribution, 
 * and where the Cauchy modification works. 
 */
public class CauchyStandardPerturber extends StandardPerturber {

    /*
      replicates the update functionality in MCMCParameter
     */
    public CauchyStandardPerturber (double[] mss) {
	super (mss);
    }
    public CauchyStandardPerturber (double[] mss, int which) {
	super(mss, which);
    }
    public CauchyStandardPerturber (int which, double[] mss) {
	super(which,mss);
    }

    public void perturb (double[][] candarray, int whoseTurn) {
	double temp = mss[whoseTurn] * 
	    Math.tan(Math.PI*(rand.nextDouble()-0.5));
	candarray[which][whoseTurn] += temp;
   }

}
