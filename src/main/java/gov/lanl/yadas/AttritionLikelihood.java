package gov.lanl.yadas;

/**
 * AttritionLikelihood: used in analysis of auto racing results.  
 * See Graves, Reese, and Fitzgerald, JASA 2003.  
 * @author TLG
 */
public class AttritionLikelihood implements Likelihood {

    /** 
     * The constructor is a bit redundant: racevec contains as many 
     * 0's as there are drivers in the 0'th race, etc., while 
     * finishvec's entries corresponding to racevec = 0 should always 
     * range from 1 to the number of such drivers.  
     */
    public AttritionLikelihood (int[] racevec, int[] finishvec) {
	// define two-dimensional array to sum appropriate things in
	vec0 = racevec;
	vec1 = finishvec;
	n = vec0.length;
	int currentrace = -1;
	int i;
	probs = new double[vec0[n-1] + 1][];

	// compute sum of intensities for remaining drivers
	for (int i0 = n-1; i0 >= 0; i0--) {
	    i = vec0[i0];
	    if (i != currentrace) {
		probs[i] = new double[vec1[i0]];
		for (int j = 0; j < probs[i].length; j++) {
		    probs[i][j] = 0.0;
		}
		currentrace = i;
	    }
	}
    }

    public double compute (double[][] args) {

	int i,j;
	// compute sum of intensities for remaining drivers
	for (int i0 = 0; i0 < n; i0++) {
	    i = vec0[i0];
	    j = vec1[i0];
	    if (j == 1) {
		probs[i][j-1] = Math.exp(-args[0][i0] * args[1][i0]);
	    } else {
		probs[i][j-1] = probs[i][j-2] + Math.exp(-args[0][i0] * 
							 args[1][i0]);
	    }
	}
	
	// convert the sum into a probability
	for (int i0 = 0; i0 < vec0.length; i0++) {
	    i = vec0[i0];
	    probs[i][vec1[i0]-1] = Math.exp(- args[0][i0] * args[1][i0]) /
		probs[i][vec1[i0]-1];
	}
	
	// now compute the log likelihood by combining all terms
	double out = 0.0;
	for (int i0 = 0; i0 < probs.length; i0++) {
	    for (int j0 = 0; j0 < probs[i0].length; j0++) {
		/*
		  if (probs[i0][j0] > 1) {
		  System.out.println("i0 = " + i0 + " j0 = " + j0 + 
		  " probs[i0][j0] = " + probs[i0][j0]);
		  System.exit(0);
		  }
		  if (Math.abs(probs[i0][j0]) == 0.0) {
		  System.out.println("i0 = " + i0 + ", j0 = " + j0);
		  System.exit(0);
		  }
		*/
		out += Math.log(probs[i0][j0]);
	    }
	}
	return out;
    }

    public static void main (String[] args) {
	// put a test of the class in here
    }

    private int[] vec0;
    private int[] vec1;
    private double[][] probs;
    private int n;
}

/* 
   Computes likelihood for the "attrition model" of auto racing results.
*/
