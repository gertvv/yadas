package gov.lanl.yadas;

/** 
 * BetaPointMass: A likelihood function that we do not anticipate 
 * many other people will be interested in.  'data' has a mixture 
 * distribution: point masses at 0 and 1 and a beta distribution.  
 * The mean of the beta piece is the same as the mean of the discrete 
 * piece.  
 * @author TLG
 */ 
public class BetaPointMass implements Likelihood {
    public double compute (double[][] args) {
	double[] data = args[0];
	double[] means = args[1];
	double[] pi = args[2];
	double[] kappa = args[3];
	double ss = 0;
	for (int i = 0; i < data.length; i++) {
	    if (data[i] == 0) {
		ss += Math.log(1-means[i]) + Math.log(pi[i]);
	    }
	    if (data[i] == 1) {
		ss += Math.log(means[i]) + Math.log(pi[i]);
	    }
	    if ((data[i] > 0) && (data[i] < 1)) {
		ss += Math.log(1-pi[i]) + Tools.loggamma(kappa[i]) -
		    Tools.loggamma (kappa[i]*means[i]) - 
		    Tools.loggamma (kappa[i] * (1-means[i])) + 
		    (kappa[i]*means[i] - 1) * Math.log(data[i]) + 
		    (kappa[i]*(1-means[i]) - 1) * Math.log(1-data[i]);
	    }
	}
	return ss;
    }
}
